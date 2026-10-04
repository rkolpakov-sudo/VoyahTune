import pathlib
import sys

log = sys.argv[1] if len(sys.argv) > 1 else "logs/build-rm-07.log"
raw = pathlib.Path(log).read_bytes()
try:
    t = raw.decode("utf-16")
except UnicodeDecodeError:
    t = raw.decode("utf-8", errors="replace")

lines = t.splitlines()
blocks = []
i = 0
while i < len(lines):
    l = lines[i]
    if ".java:" in l and "error:" in l:
        block = [l.strip()]
        j = i + 1
        while j < len(lines) and not (".java:" in lines[j] and "error:" in lines[j]) and j < i + 5:
            block.append(lines[j])
            j += 1
        blocks.append("\n".join(block))
        i = j
    else:
        i += 1

seen = set()
uniq = []
for b in blocks:
    k = b.splitlines()[0]
    if k in seen:
        continue
    seen.add(k)
    uniq.append(b)

out = pathlib.Path("logs/rm-errs-uniq.txt")
out.write_text(("\n=====\n".join(uniq)), encoding="utf-8")
print(f"{len(blocks)} errors, {len(uniq)} unique -> {out}")
for b in uniq:
    print(b.splitlines()[0])
