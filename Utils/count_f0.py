import pathlib, re, collections
cnt = 0
files = collections.Counter()
for app in ['Native', 'RestoreMode']:
    base = pathlib.Path(app) / 'app/src/main/java'
    for f in base.rglob('*.java'):
        t = f.read_text(encoding='utf-8', errors='replace')
        n = len(re.findall(r'this\.f\$0', t))
        if n:
            cnt += n
            files[str(f)] += n
print('total this.f$0:', cnt)
for f, c in files.most_common(60):
    print(f'  {c:3d} {f}')
