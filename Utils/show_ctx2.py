import pathlib

ROOT = pathlib.Path("RestoreMode/app/src/main/java/ru/big/town/restoremode")
REGIONS = {
    "VoiceCommands.java": [(60, 115)],
    "VoiceModels.java": [(175, 230)],
    "NowPlayingClient.java": [(92, 165)],
    "AdvanceActivity.java": [(555, 575), (1805, 1820), (1970, 1990), (2555, 2575), (2985, 3010)],
    "VoiceFuzzyMatcher.java": [(60, 118)],
    "VoiceActivity.java": [(1, 40)],
    "VoiceRecognizer.java": [(156, 200), (340, 440)],
}
for name, regions in REGIONS.items():
    p = ROOT / name
    ls = p.read_text(encoding="utf-8", errors="replace").splitlines()
    print("=" * 25, name, len(ls))
    for a, b in regions:
        for i in range(a - 1, min(b, len(ls))):
            print(f"{i+1:5d}| {ls[i][:180]}")
        print("   ...")
