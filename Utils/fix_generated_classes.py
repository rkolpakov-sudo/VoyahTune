"""Замена декомпилированных android.car-исходников jar из легенды +
удаление классов, генерируемых AGP/D8 (R, BuildConfig, databinding,
ExternalSyntheticLambda-синтетики)."""
import pathlib
import re
import shutil

ROOT = pathlib.Path(".")
NATIVE_SRC = ROOT / "Native/app/src/main/java"
RM_SRC = ROOT / "RestoreMode/app/src/main/java"

# 1. android.car jar (как в легенде: Native/app/lib/android.car.jar)
lib_dir = ROOT / "Native/app/lib"
lib_dir.mkdir(parents=True, exist_ok=True)
jar_src = ROOT / "reference-3.14/Native/app/lib/android.car.jar"
shutil.copy2(jar_src, lib_dir / "android.car.jar")
print("copied android.car.jar:", (lib_dir / "android.car.jar").stat().st_size)

# 2. удаление деревьев android/car и com/android/car
for tree in [NATIVE_SRC / "android/car", NATIVE_SRC / "com/android/car"]:
    if tree.exists():
        n = sum(1 for _ in tree.rglob("*.java"))
        shutil.rmtree(tree)
        print(f"removed {tree} ({n} java)")
# пустые родители
for p in [NATIVE_SRC / "android", NATIVE_SRC / "com/android"]:
    if p.exists() and not any(p.iterdir()):
        p.rmdir()
        print("removed empty", p)

# 3. удаление сгенерированных классов
gen_files = [
    NATIVE_SRC / "ru/big/town/anative/BuildConfig.java",
    NATIVE_SRC / "ru/big/town/anative/R.java",
    RM_SRC / "ru/big/town/restoremode/BuildConfig.java",
    RM_SRC / "ru/big/town/restoremode/R.java",
    RM_SRC / "com/k2fsa/sherpa/onnx/R.java",
    RM_SRC / "com/sun/jna/R.java",
]
for f in gen_files:
    if f.exists():
        f.unlink()
        print("removed", f)

for src in [NATIVE_SRC, RM_SRC]:
    for d in [src / "ru/big/town/anative/databinding", src / "ru/big/town/restoremode/databinding"]:
        if d.exists():
            shutil.rmtree(d)
            print("removed", d)

# 4. ExternalSyntheticLambda-синтетики (D8 сгенерирует заново)
n = 0
for src in [NATIVE_SRC, RM_SRC]:
    for f in src.rglob("*.java"):
        if "$$ExternalSynthetic" in f.name:
            f.unlink()
            n += 1
print("removed ExternalSynthetic files:", n)

# 5. BuildConfig для sherpa (используется BUILD_TYPE)
sc = RM_SRC / "com/k2fsa/sherpa/onnx/BuildConfig.java"
sc.write_text(
    "package com.k2fsa.sherpa.onnx;\n\n"
    "public final class BuildConfig {\n"
    "    public static final boolean DEBUG = false;\n"
    '    public static final String BUILD_TYPE = "release";\n'
    "    public static final String LIBRARY_PACKAGE_NAME = \"com.k2fsa.sherpa.onnx\";\n"
    "    private BuildConfig() {\n"
    "    }\n"
    "}\n",
    encoding="utf-8",
)
print("wrote", sc)
