import pathlib
import re

t = pathlib.Path("logs/build-rm-05.log").read_bytes().decode("utf-16", errors="replace")
errs = re.findall(r"([A-Za-z]:\\[^\r\n]*?\.java):(\d+): error: ([^\r\n]+)", t)
seen = set()
for f, line, m in errs:
    k = (f, line, m)
    if k in seen:
        continue
    seen.add(k)
    idx = f.find("main" + "\\")
    short = f[idx + 5:] if idx >= 0 else f
    print(f"{short}:{line}: {m.strip()}")
