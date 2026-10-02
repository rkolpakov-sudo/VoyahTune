import pathlib
import re

root = pathlib.Path("RestoreMode/app/src/main/java/com/k2fsa/sherpa/onnx")
pat_comment = re.compile(r"/\* JADX INFO: renamed from:? (component\d+), reason: from getter \*/")
pat_method = re.compile(r"(public final [\w.<>\[\]]+ )(\w+)(\(\) \{)")

total = 0
files = 0
for p in sorted(root.glob("*.java")):
    t = p.read_text(encoding="utf-8", errors="replace")
    lines = t.splitlines(keepends=True)
    changed = 0
    for i in range(len(lines) - 1):
        m = pat_comment.search(lines[i])
        if not m:
            continue
        orig = m.group(1)
        mm = pat_method.search(lines[i + 1])
        if not mm:
            continue
        lines[i + 1] = lines[i + 1][: mm.start(2)] + orig + lines[i + 1][mm.end(2):]
        changed += 1
    if changed:
        p.write_text("".join(lines), encoding="utf-8")
        total += changed
        files += 1
        print(f"{changed:3d} | {p.name}")

print(f"TOTAL renamed: {total} in {files} files")
