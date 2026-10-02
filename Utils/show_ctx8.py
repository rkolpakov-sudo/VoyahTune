import pathlib
import re

N = pathlib.Path('Native/app/src/main/java/ru/big/town/anative')
RM = pathlib.Path('RestoreMode/app/src/main/java')


def show(p, a, b, tag=''):
    lines = p.read_text(encoding='utf-8', errors='replace').splitlines()
    print(f'===== {p.name} {a}-{b} {tag} =====')
    for j in range(a - 1, min(len(lines), b)):
        print(f'{j+1:5d}| {lines[j][:165]}')


def grep(p, pat, tag=''):
    lines = p.read_text(encoding='utf-8', errors='replace').splitlines()
    print(f'----- grep {p.name} {pat} {tag} -----')
    for j, l in enumerate(lines):
        if re.search(pat, l):
            print(f'{j+1:5d}| {l.strip()[:160]}')


ae = N / 'ApplyEngine.java'
show(ae, 440, 475, 'obj decl')
grep(ae, r'bArrValidCanFrames', 'decl')

grep(N / 'BatteryHeatService.java', r'm1825|m1834lambda\$activate\$9', 'lambdas')

lss = N / 'LightSensorService.java'
show(lss, 802, 845, 'reg full')
show(lss, 1136, 1165, 'ssq tail')
grep(lss, r'savedByDisplay', 'types')  # noop wrong file

nps = N / 'NowPlayingService.java'
show(nps, 383, 400, 'init block')

pht = N / 'PowerHoldStatusTracker.java'
grep(pht, r'PowerHoldStatusTracker\.this', 'outer refs')
show(pht, 40, 62, 'create')
show(pht, 95, 108, 'A2 decl')
grep(pht, r'static .* create|private PowerHoldStatusTracker\(', 'ctor/create')
grep(N / 'WashModeController.java', r'WashModeController\.this', 'outer refs')

mcr = N / 'MediaControlRouter.java'
show(mcr, 263, 300, 'after token')

grep(N / 'ManualAutoGate.java', r'class Ticket|Ticket\(', 'ticket')
for f in N.rglob('*.java'):
    t = f.read_text(encoding='utf-8', errors='replace')
    if re.search(r'\bclass Ticket\b', t):
        print(f'*** Ticket class in {f.name}')
        for j, l in enumerate(t.splitlines()):
            if 'Ticket' in l:
                print(f'  {j+1:4d}| {l.strip()[:150]}')

slr = N / 'ScreenLiftTaskRestorer.java'
grep(slr, r'savedByDisplay', 'field')

sms = N / 'SetModesService.java'
show(sms, 688, 705, 'cause696')
grep(sms, r'Throwable th;', 'th decl in method')
show(sms, 1530, 1550, 'th tail')
grep(N / 'NativeLog.java', r'Process proc|Process\b', 'proc field')
