import pathlib
import os
import re

t = pathlib.Path(os.environ['TEMP'] + '/pht_staged.java').read_text(encoding='utf-8', errors='replace')
ls = t.splitlines()
for i, l in enumerate(ls):
    if re.search(r'm2019|class AnonymousClass2|lambda\$acceptEvent|canBusEvent', l):
        print(f'{i+1:4d}| {l.strip()[:150]}')
