import pathlib

t = pathlib.Path("logs/build-rm-05.log").read_bytes().decode("utf-16", errors="replace")
lines = t.splitlines()
out = []
for i, l in enumerate(lines):
    if ".java:" in l and "error:" in l:
        block = [l]
        for j in range(i + 1, min(i + 5, len(lines))):
            if ".java:" in lines[j] and "error:" in lines[j]:
                break
            block.append(lines[j])
        out.append("\n".join(block))
seen = set()
for b in out:
    # dedupe by first line
    if b.splitlines()[0] in seen:
        continue
    seen.add(b.splitlines()[0])
    print(b)
    print("---")
