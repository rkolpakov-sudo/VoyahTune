import pathlib

ROOT = pathlib.Path("RestoreMode/app/src/main/java/com/sun/jna")
REGIONS = {
    "Native.java": [(1, 12), (155, 175), (350, 380), (995, 1050), (1640, 1690)],
    "Structure.java": [(1060, 1080), (1425, 1455), (1585, 1605)],
    "CallbackReference.java": [(175, 195), (248, 262), (508, 522)],
    "Function.java": [(138, 160), (185, 200), (245, 260), (400, 415)],
    "NativeLibrary.java": [(385, 400)],
    "Pointer.java": [(130, 145), (260, 275)],
}
for name, regions in REGIONS.items():
    p = ROOT / name
    ls = p.read_text(encoding="utf-8", errors="replace").splitlines()
    print("=" * 25, name, len(ls))
    for a, b in regions:
        for i in range(a - 1, min(b, len(ls))):
            print(f"{i+1:5d}| {ls[i][:190]}")
        print("   ...")
