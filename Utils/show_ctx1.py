import pathlib

BASE = pathlib.Path('Native/app/src/main/java')


def show(rel, ranges, full=None):
    p = BASE / rel
    if not p.exists():
        print('MISSING', rel)
        return
    lines = p.read_text(encoding='utf-8', errors='replace').splitlines()
    print(f'===== {rel} ({len(lines)} lines) =====')
    for a, b in ranges:
        for j in range(max(0, a-1), min(len(lines), b)):
            print(f'{j+1:5d}| {lines[j][:170]}')
        print('  ---')


show('ru/big/town/anative/AppDisplayLauncher.java', [(95, 140)])
show('ru/big/town/anative/ApplyEngine.java', [(300, 320)])
show('ru/big/town/anative/BatteryHeatRefreshGate.java', [(45, 70)])
show('ru/big/town/anative/OemVehicleStateTransport.java', [(1, 60)])
