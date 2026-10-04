import pathlib, re

BASE = pathlib.Path('Native/app/src/main/java')
RMBASE = pathlib.Path('RestoreMode/app/src/main/java')


def show(base, rel, ranges):
    p = base / rel
    if not p.exists():
        print('MISSING', rel)
        return
    lines = p.read_text(encoding='utf-8', errors='replace').splitlines()
    print(f'===== {rel} =====')
    for a, b in ranges:
        for j in range(max(0, a-1), min(len(lines), b)):
            print(f'{j+1:5d}| {lines[j][:160]}')
        print('  ---')


def show_around(base, rel, pat, ctx=9):
    p = base / rel
    lines = p.read_text(encoding='utf-8', errors='replace').splitlines()
    print(f'===== {rel} :: {pat} =====')
    for i, l in enumerate(lines):
        if re.search(pat, l):
            for j in range(max(0, i-ctx), min(len(lines), i+ctx+1)):
                mark = '>>' if j == i else '  '
                print(f'{mark}{j+1:5d}| {lines[j][:155]}')
            print('  ---')


# все сайты e = eN / th = thN
sites = [
    (BASE, 'ru/big/town/anative/AppDisplayLauncher.java', r'e = e\d'),
    (BASE, 'ru/big/town/anative/BatteryHeatService.java', r'e = e\d|th = th\d'),
    (BASE, 'ru/big/town/anative/LightSensorService.java', r'e = e\d'),
    (BASE, 'ru/big/town/anative/NativeLog.java', r'e = e\d|th = th\d'),
    (BASE, 'ru/big/town/anative/NowPlayingService.java', r'e = e\d'),
    (BASE, 'ru/big/town/anative/OemIndividualDriveProfileReader.java', r'e = e\d|th = th\d'),
    (BASE, 'ru/big/town/anative/OemVehicleStateTransport.java', r'e = e\d|th = th\d'),
    (BASE, 'ru/big/town/anative/SetModesService.java', r'e = e\d|th = th\d'),
    (BASE, 'ru/big/town/anative/SplitHostActivity.java', r'e = e\d'),
    (BASE, 'ru/big/town/anative/WiperColdService.java', r'e = e\d'),
    (RMBASE, 'com/sun/jna/Native.java', r'e = e\d'),
    (RMBASE, 'com/sun/jna/NativeLibrary.java', r'th = th\d'),
    (RMBASE, 'ru/big/town/restoremode/AdvanceActivity.java', r'e = e\d'),
    (RMBASE, 'ru/big/town/restoremode/NowPlayingClient.java', r'th = th\d'),
    (RMBASE, 'ru/big/town/restoremode/VoiceEngine.java', r'e = e\d'),
    (RMBASE, 'ru/big/town/restoremode/VoiceRecognizer.java', r'e = e\d|th = th\d'),
]
for b, r, p in sites:
    show_around(b, r, p, ctx=8)

# прочие контексты
show(BASE, 'ru/big/town/anative/OemVehicleStateTransport.java', [(215, 244), (455, 475), (808, 830)])
show(BASE, 'ru/big/town/anative/BatteryHeatRefreshGate.java', [(54, 106)])
show(RMBASE, 'com/sun/jna/Structure.java', [(905, 990)])
show(RMBASE, 'ru/big/town/restoremode/VoiceCommandSequence.java', [(76, 150), (172, 210)])
show(RMBASE, 'ru/big/town/restoremode/AppWidgetStore.java', [(88, 125)])
show(RMBASE, 'ru/big/town/restoremode/SuspensionWidgetView.java', [(270, 300)])
show(RMBASE, 'com/k2fsa/sherpa/onnx/AudioTaggingModelConfig.java', [(80, 105)])
show(RMBASE, 'ru/big/town/restoremode/MainActivity.java', [(1275, 1295)])
