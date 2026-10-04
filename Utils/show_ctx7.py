import pathlib

N = pathlib.Path('Native/app/src/main/java/ru/big/town/anative')

sites = [
    ('ApplyEngine', 364), ('ApplyEngine', 491), ('ApplyEngine', 559),
    ('BatteryHeatService', 534), ('BatteryHeatService', 726), ('BatteryHeatService', 741),
    ('LightSensorService', 815), ('LightSensorService', 1015), ('LightSensorService', 1125),
    ('ManualAutoGate', 16), ('MediaControlRouter', 253),
    ('NativeLog', 79), ('NativeLog', 94), ('NativeLog', 142),
    ('NowPlayingService', 383), ('NowPlayingService', 441),
    ('PowerHoldStatusTracker', 82), ('PowerHoldStatusTracker', 127),
    ('ScreenLiftTaskRestorer', 149),
    ('SetModesService', 665), ('SetModesService', 1522),
    ('WashModeController', 84), ('SplitHostActivity', 1153),
]

cache = {}
for name, ln in sites:
    if name not in cache:
        cache[name] = (N / f'{name}.java').read_text(encoding='utf-8', errors='replace').splitlines()
    ls = cache[name]
    a, b = max(0, ln - 20), min(len(ls), ln + 10)
    print(f'===== {name}.java around {ln} =====')
    for i in range(a, b):
        mark = '>>' if i + 1 == ln else '  '
        print(f'{mark}{i+1:5d}| {ls[i][:170]}')
    print()
