import pathlib, re, glob

BASE = pathlib.Path('Native/app/src/main/java')
RMBASE = pathlib.Path('RestoreMode/app/src/main/java')


def grep_all(base, pat, ctx=4, limit=60):
    rx = re.compile(pat)
    n = 0
    for f in sorted(base.rglob('*.java')):
        lines = f.read_text(encoding='utf-8', errors='replace').splitlines()
        for i, l in enumerate(lines):
            if rx.search(l):
                print(f'--- {f}:{i+1}')
                for j in range(max(0, i-ctx), min(len(lines), i+ctx+1)):
                    print(f'  {j+1:5d}| {lines[j][:150]}')
                n += 1
                if n >= limit:
                    return


print('##### VoiceEngine full file #####')
p = RMBASE / 'ru/big/town/restoremode/VoiceEngine.java'
print(p.read_text(encoding='utf-8', errors='replace'))

print('##### VoiceRecognizer: method sigs + throw e #####')
p = RMBASE / 'ru/big/town/restoremode/VoiceRecognizer.java'
lines = p.read_text(encoding='utf-8', errors='replace').splitlines()
for i, l in enumerate(lines):
    if re.search(r'(public|private|protected|static).*\b(recognize|start|stop|cancel)\w*\(', l) and '{' in l or \
       (re.search(r'(public|private|protected|static|void|boolean|int).*\(', l) and i+1 < len(lines) and '{' in lines[i+1] and 'throws' in l):
        print(f'  sig {i+1}: {l.strip()[:170]}')
    if 'throw e' in l:
        print(f'  throwE {i+1}: {l.strip()[:170]}')

print('##### AppWidgetStore: r13/r5 uses #####')
p = RMBASE / 'ru/big/town/restoremode/AppWidgetStore.java'
lines = p.read_text(encoding='utf-8', errors='replace').splitlines()
for i, l in enumerate(lines):
    if re.search(r'\br13\b|\br5\b|CanSelect', l):
        print(f'  {i+1:5d}| {l.strip()[:150]}')

print('##### SuspensionWidgetView: CanSelect uses #####')
p = RMBASE / 'ru/big/town/restoremode/SuspensionWidgetView.java'
lines = p.read_text(encoding='utf-8', errors='replace').splitlines()
for i, l in enumerate(lines):
    if 'CanSelect' in l:
        print(f'  {i+1:5d}| {l.strip()[:150]}')

print('##### Segment class def #####')
for f in RMBASE.rglob('*.java'):
    lines = f.read_text(encoding='utf-8', errors='replace').splitlines()
    for i, l in enumerate(lines):
        if re.search(r'class Segment\b|Segment\(', l):
            print(f'  {f}:{i+1}: {l.strip()[:160]}')

print('##### Structure: objArr uses #####')
p = RMBASE / 'com/sun/jna/Structure.java'
lines = p.read_text(encoding='utf-8', errors='replace').splitlines()
for i, l in enumerate(lines):
    if 'objArr' in l:
        print(f'  {i+1:5d}| {l.strip()[:150]}')

print('##### sherpa ?? sites context #####')
for f in sorted(RMBASE.rglob('*.java')):
    lines = f.read_text(encoding='utf-8', errors='replace').splitlines()
    for i, l in enumerate(lines):
        if l.strip().startswith('??'):
            for j in range(max(0, i-3), min(len(lines), i+5)):
                print(f'  {f.name}:{j+1:4d}| {lines[j][:140]}')
            print('  ---')
