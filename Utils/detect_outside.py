import pathlib, re

BASES = [pathlib.Path('Native/app/src/main/java'), pathlib.Path('RestoreMode/app/src/main/java')]


def depth_changes(line):
    # наивно: скобки вне строк/символов/комментариев
    line = re.sub(r'"(\\.|[^"\\])*"', '""', line)
    line = re.sub(r"'(\\.|[^'\\])*'", "''", line)
    line = re.sub(r'//.*', '', line)
    return line.count('{') - line.count('}')


def analyze():
    for base in BASES:
        for f in sorted(base.rglob('*.java')):
            text = f.read_text(encoding='utf-8', errors='replace')
            lines = text.splitlines()
            depths = []
            d = 0
            for l in lines:
                depths.append(d)
                d += depth_changes(l)
            sites = []
            for i, l in enumerate(lines):
                m = re.match(r'(\s*)(e|th) = (e\d+|th\d+);\s*$', l)
                if m:
                    sites.append((i, m.group(2), m.group(1)))
            if not sites:
                continue
            print(f'===== {f} =====')
            for i, var, indent in sites:
                # блок catch: ищем назад строку catch
                cl = None
                for j in range(i, max(-1, i - 5), -1):
                    if re.search(r'catch\s*\(', lines[j]):
                        cl = j
                        break
                if cl is None:
                    print(f'  site {i+1}: NO CATCH FOUND')
                    continue
                D = depths[i]
                # конец catch-блока: первая строка после сайта с depth < D
                blk_end = None
                for j in range(i + 1, len(lines)):
                    if depths[j] < D:
                        blk_end = j - 1
                        break
                if blk_end is None:
                    blk_end = len(lines) - 1
                # конец метода: первая строка после блока с depth < D-1
                meth_end = len(lines)
                for j in range(blk_end + 1, len(lines)):
                    if depths[j] < D - 1:
                        meth_end = j
                        break
                # ищем использования var в (blk_end+1 .. meth_end)
                uses = []
                for j in range(blk_end + 1, meth_end):
                    lj = lines[j]
                    if re.search(r'(?<![\w.$])' + var + r'(?![\w$])', lj):
                        uses.append((j + 1, lj.strip()[:120]))
                status = 'OUTSIDE-USE!' if uses else 'ok'
                # catch-тип
                cm = re.search(r'catch\s*\(([^)]*)\)', lines[cl])
                ctype = cm.group(1) if cm else '?'
                print(f'  site {i+1}: var={var} catch=({ctype}) block_end={blk_end+1} {status}')
                for un, ul in uses:
                    print(f'      use@{un}: {ul}')


analyze()
