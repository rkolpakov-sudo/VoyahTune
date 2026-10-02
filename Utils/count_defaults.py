import pathlib
import re
from collections import Counter

for mod in ["RestoreMode/app/src/main/java", "Native/app/src/main/java"]:
    root = pathlib.Path(mod)
    c = Counter()
    files = 0
    sites = 0
    for p in root.rglob("*.java"):
        t = p.read_text(encoding="utf-8", errors="replace")
        hits = re.findall(r"([A-Za-z_][\w.]*)\.([\w$]*\$default)\(", t)
        if hits:
            files += 1
            sites += len(hits)
        for h in hits:
            c[h[0] + "." + h[1]] += 1
    print(mod, "files:", files, "sites:", sites)
    print("  top:", c.most_common(20))
