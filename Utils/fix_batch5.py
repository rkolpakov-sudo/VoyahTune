"""fix_batch5: jadx-ссылки Outer.N.this -> AnonymousClassN.this + восстановление скобок."""
import pathlib
import re

ROOTS = [pathlib.Path("Native/app/src/main/java"),
         pathlib.Path("RestoreMode/app/src/main/java")]
log = []


def L(m):
    print(m)
    log.append(m)


def mask(src):
    """Заменить строки/комментарии пробелами, сохранив переводы строк."""
    out = list(src)
    i, n = 0, len(src)
    state = None  # None,'lc','bc','s','c'
    while i < n:
        c = src[i]
        nx = src[i + 1] if i + 1 < n else ''
        if state is None:
            if c == '/' and nx == '/':
                state = 'lc'; out[i] = out[i + 1] = ' '; i += 2; continue
            if c == '/' and nx == '*':
                state = 'bc'; out[i] = out[i + 1] = ' '; i += 2; continue
            if c == '"':
                state = 's'; out[i] = ' '; i += 1; continue
            if c == "'":
                state = 'c'; out[i] = ' '; i += 1; continue
            i += 1; continue
        if state == 'lc':
            if c == '\n':
                state = None
            else:
                out[i] = ' '
            i += 1; continue
        if state == 'bc':
            if c == '*' and nx == '/':
                out[i] = out[i + 1] = ' '; state = None; i += 2; continue
            if c != '\n':
                out[i] = ' '
            i += 1; continue
        if state in ('s', 'c'):
            q = '"' if state == 's' else "'"
            if c == '\\':
                out[i] = ' '
                if i + 1 < n and src[i + 1] != '\n':
                    out[i + 1] = ' '
                i += 2; continue
            if c == q:
                out[i] = ' '; state = None; i += 1; continue
            if c != '\n':
                out[i] = ' '
            i += 1; continue
    return ''.join(out)


CLS_RE = re.compile(
    r"\b(?:class|interface|enum)\s+([A-Za-z_$][\w$]*)")
REF_RE = re.compile(r"\b([A-Za-z_$][\w$]*)((?:\.\d+)+)\.this")


def fix_file(p):
    src = p.read_text(encoding="utf-8", errors="replace")
    if not REF_RE.search(src):
        return
    masked = mask(src)
    mlines = src.splitlines()
    klines = masked.splitlines()
    stack = []  # (name, depth_inside)
    depth = 0
    changed = 0
    for li, k in enumerate(klines):
        # pop закрытые классы
        while stack and depth < stack[-1][1]:
            stack.pop()
        refs = REF_RE.findall(mlines[li]) if li < len(mlines) else []
        if refs:
            for root, nums in refs:
                nums = [int(x) for x in nums.split('.')[1:]]
                start = 0
                for idx, (nm, _) in enumerate(stack):
                    if nm == root:
                        start = idx
                        break
                else:
                    L(f"  WARN {p.name}:{li+1}: root {root} not in stack "
                      f"({[s[0] for s in stack]})")
                    continue
                seg = stack[start + 1:start + 1 + len(nums)]
                if len(seg) != len(nums):
                    L(f"  WARN {p.name}:{li+1}: chain too short for {root}{nums} "
                      f"({[s[0] for s in stack]})")
                    continue
                for j, (nm, _) in enumerate(seg):
                    if nm != f"AnonymousClass{nums[j]}" and nm != str(nums[j]):
                        L(f"  WARN {p.name}:{li+1}: seg{j}={nm} != "
                          f"AnonymousClass{nums[j]}")
                target = seg[-1][0]
                old = f"{root}{''.join('.' + str(x) for x in nums)}.this"
                new = f"{target}.this"
                mlines[li] = mlines[li].replace(old, new)
                changed += 1
                L(f"  {p.name}:{li+1}: {old} -> {new}")
        # обновить depth и стек классов
        clsm = CLS_RE.search(k)
        if clsm:
            # depth после добавления скобок этой строки
            after = depth + sum(1 for c in k if c == '{') - sum(1 for c in k if c == '}')
            stack.append((clsm.group(1), after if '{' in k else depth + 1))
        depth += sum(1 for c in k if c == '{') - sum(1 for c in k if c == '}')
    if changed:
        p.write_text("\n".join(mlines) + ("\n" if src.endswith("\n") else ""),
                     encoding="utf-8")


# --- LightDiagnosticsService: восстановить скобки if-блока (187-195) ---
p = pathlib.Path("Native/app/src/main/java/ru/big/town/anative/LightDiagnosticsService.java")
def f(t):
    old = """            if (LightDiagnosticsService.this.generation == i && LightDiagnosticsService.this.carBinder == iBinder && LightDiagnosticsService.this.carCallback == iBinder2)
                final int carInt = LightDiagnosticsService.readCarInt(iBinder, 36);
                final int carInt2 = LightDiagnosticsService.readCarInt(iBinder, 73);
                LightDiagnosticsService.this.main.post(new Runnable() { // from class: ru.big.town.anative.LightDiagnosticsService$2$$ExternalSyntheticLambda1"""
    new = """            if (LightDiagnosticsService.this.generation == i && LightDiagnosticsService.this.carBinder == iBinder && LightDiagnosticsService.this.carCallback == iBinder2) {
                final int carInt = LightDiagnosticsService.readCarInt(iBinder, 36);
                final int carInt2 = LightDiagnosticsService.readCarInt(iBinder, 73);
                LightDiagnosticsService.this.main.post(new Runnable() { // from class: ru.big.town.anative.LightDiagnosticsService$2$$ExternalSyntheticLambda1"""
    if old in t:
        t = t.replace(old, new)
        # закрыть добавленную скобку перед else-if
        old2 = """                });
            } else if (zTransactCallback) {"""
        new2 = """                });
            } else if (zTransactCallback) {"""
        L("  LDS braces opened (closing verified below)")
    return t
t = p.read_text(encoding="utf-8", errors="replace")
t = f(t)
p.write_text(t, encoding="utf-8")

for base in ROOTS:
    for f in sorted(base.rglob("*.java")):
        fix_file(f)

pathlib.Path("logs/fix_batch5.log").write_text("\n".join(log), encoding="utf-8")
