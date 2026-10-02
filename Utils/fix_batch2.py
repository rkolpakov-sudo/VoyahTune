"""Батч-фикс javac-ошибок Native/RestoreMode (см. логи build-native-7)."""
import pathlib
import re
import shutil

NATIVE = pathlib.Path("Native/app/src/main/java")
RM = pathlib.Path("RestoreMode/app/src/main/java")

# --- 1. MediaMetadataCompat: убрать зависимость от MediaSessionCompat ---
mmc = NATIVE / "android/support/v4/media/MediaMetadataCompat.java"
if mmc.exists():
    t = mmc.read_text(encoding="utf-8", errors="replace")
    t = t.replace(
        "import android.support.v4.media.session.MediaSessionCompat;\n", ""
    )
    # ensureClassLoader(bundle) -> инлайн (тело из MediaSessionCompat:352-356)
    t = re.sub(
        r"MediaSessionCompat\.ensureClassLoader\((\w+)\);",
        r"if (\1 != null) {\n                \1.setClassLoader(MediaMetadataCompat.class.getClassLoader());\n            }",
        t,
    )
    t = t.replace(
        "parcel.readBundle(MediaSessionCompat.class.getClassLoader())",
        "parcel.readBundle(MediaMetadataCompat.class.getClassLoader())",
    )
    mmc.write_text(t, encoding="utf-8")
    print("patched MediaMetadataCompat")

# --- 2. удаление android/support и errorprone ---
for src in [NATIVE, RM]:
    for tree in [src / "android/support", src / "com/google/errorprone"]:
        if tree.exists():
            n = sum(1 for _ in tree.rglob("*.java"))
            shutil.rmtree(tree)
            print(f"removed {tree} ({n} java)")
    for pkg in [src / "android", src / "com/google"]:
        if pkg.exists() and not any(pkg.iterdir()):
            pkg.rmdir()
            print("removed empty", pkg)

# --- 3. Apollo: Throwable cause ---
ap = NATIVE / "ru/big/town/anative/ApolloSettingsRuntimeState.java"
if ap.exists():
    t = ap.read_text(encoding="utf-8", errors="replace")
    t = t.replace("            Exception cause = e;", "            Throwable cause = e;")
    ap.write_text(t, encoding="utf-8")
    print("patched ApolloSettingsRuntimeState")

# --- 4. build.gradle: collection не нужен (транзитивен), нужен constraintlayout-core ---
bg = pathlib.Path("Native/app/build.gradle")
t = bg.read_text(encoding="utf-8")
if "constraintlayout-core" not in t:
    t = t.replace(
        "implementation 'androidx.constraintlayout:constraintlayout:2.2.1'",
        "implementation 'androidx.constraintlayout:constraintlayout:2.2.1'\n"
        "    implementation 'androidx.constraintlayout:constraintlayout-core:1.1.1'",
    )
    bg.write_text(t, encoding="utf-8")
    print("added constraintlayout-core dep")

