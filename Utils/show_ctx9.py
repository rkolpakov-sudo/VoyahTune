import pathlib
import re

N = pathlib.Path('Native/app/src/main/java/ru/big/town/anative')


def show(f, a, b, tag=''):
    ls = (N / f).read_text(encoding='utf-8', errors='replace').splitlines()
    print('=====', f, a, b, tag)
    for i in range(a - 1, min(len(ls), b)):
        print(f'{i+1:5d}| {ls[i][:165]}')


def grep(f, p, tag=''):
    ls = (N / f).read_text(encoding='utf-8', errors='replace').splitlines()
    print('---- grep', f, p, tag)
    for i, l in enumerate(ls):
        if re.search(p, l):
            print(f'{i+1:5d}| {l.strip()[:150]}')


show('WashModeController.java', 95, 115)
grep('BatteryHeatService.java', r'activate\$11')
grep('ApplyEngine.java', r'\bobj\b')
show('SetModesService.java', 1490, 1535, 'reg method')
grep('PowerHoldStatusTracker.java', r'lambda\$acceptEvent\$1')
grep('BatteryHeatService.java', r'\bboolean m\d')
