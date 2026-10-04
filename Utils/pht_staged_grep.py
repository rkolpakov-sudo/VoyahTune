import pathlib, os
t = pathlib.Path(os.environ['TEMP'] + '/pht_staged.java').read_bytes().decode('utf-16')
ls = t.splitlines()
for i, l in enumerate(ls):
    if 'f$0' in l:
        print(f'{i+1:4d}| {l.strip()[:150]}')
