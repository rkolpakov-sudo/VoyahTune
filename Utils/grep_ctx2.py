import pathlib, re

PATS = [
    r'this = Result',
    r'return this;$',
    r'voyah-cluster-media',
    r'Enum\.valueOf',
    r'sensorApplyRequest = sensorApplyRequest2',
    r'ExternalSyntheticLambda\d+\(this\)',
    r'consumer\.accept\(obj\)',
    r'for \(StateKey stateKey',
    r'Object\[\] \w+ = 0;',
    r'e = e2;',
    r'th = th2;',
    r'clusterMediaHostActivity = "',
]

for app in ['Native', 'RestoreMode']:
    base = pathlib.Path(app) / 'app/src/main/java'
    for f in sorted(base.rglob('*.java')):
        lines = f.read_text(encoding='utf-8', errors='replace').splitlines()
        for i, l in enumerate(lines):
            for p in PATS:
                if re.search(p, l):
                    print(f'{f}:{i+1}: {l.strip()[:150]}')
                    break
