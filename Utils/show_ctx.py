import pathlib

ROOT = pathlib.Path("RestoreMode/app/src/main/java/ru/big/town/restoremode")

REGIONS = {
    "VoiceSeatCommands.java": [(245, 355)],
    "VoiceSettingsPage.java": [(205, 285)],
    "AppWidgetStore.java": [(10, 30), (100, 125)],
    "VoiceFuzzyMatcher.java": [(118, 140)],
    "MainActivity.java": [(350, 362), (1455, 1470), (1730, 1755)],
    "SuspensionWidgetView.java": [(190, 210)],
    "VoiceActivity.java": [(320, 340)],
    "VoiceRecognizer.java": [(40, 60), (140, 165), (400, 445)],
    "VoiceEngineCache.java": [(35, 55)],
    "VoiceCommands.java": [(100, 120)],
    "VoiceModels.java": [(195, 215)],
    "VoiceWarmupService.java": [(60, 78)],
    "TileDragController.java": [(1, 20), (135, 150)],
    "NowPlayingClient.java": [(1, 20), (50, 115)],
    "VoiceCommandCatalog.java": [(1, 80)],
}

for name, regions in REGIONS.items():
    p = ROOT / name
    if not p.exists():
        print(f"!! missing {name}")
        continue
    ls = p.read_text(encoding="utf-8", errors="replace").splitlines()
    print("=" * 25, name, len(ls))
    for a, b in regions:
        for i in range(a - 1, min(b, len(ls))):
            print(f"{i+1:5d}| {ls[i][:190]}")
        print("   ...")
