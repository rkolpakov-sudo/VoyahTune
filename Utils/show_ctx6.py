import pathlib

N = pathlib.Path('Native/app/src/main/java')
RM = pathlib.Path('RestoreMode/app/src/main/java')


def show(p, a, b, tag=''):
    lines = p.read_text(encoding='utf-8', errors='replace').splitlines()
    print(f'===== {p.name} {a}-{b} {tag} =====')
    for j in range(a - 1, min(len(lines), b)):
        print(f'{j+1:5d}| {lines[j][:160]}')


show(N / 'ru/big/town/anative/ClusterMediaHostActivity.java', 150, 205)
show(N / 'ru/big/town/anative/NativeLog.java', 218, 232)
show(N / 'ru/big/town/anative/NowPlayingService.java', 252, 275)
show(RM / 'ru/big/town/restoremode/VoiceCommandSequence.java', 45, 78)
show(RM / 'ru/big/town/restoremode/VoiceCommandSequence.java', 155, 175)
show(N / 'ru/big/town/anative/BatteryHeatRefreshGate.java', 54, 70)
show(RM / 'ru/big/town/restoremode/VoiceRecognizer.java', 255, 270, 'decl')
show(RM / 'ru/big/town/restoremode/VoiceRecognizer.java', 155, 168, 'decl2')
show(RM / 'ru/big/town/restoremode/VoiceRecognizer.java', 205, 235, 'pe')
show(RM / 'ru/big/town/restoremode/VoiceRecognizer.java', 405, 440, 'r415')
show(RM / 'ru/big/town/restoremode/VoiceRecognizer.java', 530, 545, 'r537')
show(RM / 'ru/big/town/restoremode/VoiceRecognizer.java', 625, 650, 'tail')
