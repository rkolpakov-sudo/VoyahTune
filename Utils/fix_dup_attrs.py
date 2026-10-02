"""Удаление из apktool-декодированных attrs.xml всех attr, которые уже
объявлены в Maven-библиотеках (appcompat/material/coordinatorlayout/...).

Корень: ресурсы исходного APK = app ∪ libs; после подключения libs из Maven
их <declare-styleable>` заново определяют те же attr → AAPT2 duplicate value.
Скрипт собирает имена attr из всех .aar в Gradle-кэше и вырезает
top-level-дубликаты из наших attrs.xml.
"""
import re
import sys
import zipfile
from pathlib import Path

GRADLE_CACHE = Path.home() / ".gradle" / "caches" / "modules-2" / "files-2.1"
ATTR_DECL = re.compile(r"<attr\b[^>]*\bname=\"([^\"]+)\"")

TARGETS = [
    Path(r"Native\app\src\main\res\values\attrs.xml"),
    Path(r"RestoreMode\app\src\main\res\values\attrs.xml"),
]

# только используемые нами androidx/material AAR (чтобы не зацепить мусор)
LIB_PREFIXES = (
    "androidx.", "com.google.android.material", "com.google.errorprone",
)


def collect_lib_attrs() -> set[str]:
    attrs: set[str] = set()
    if not GRADLE_CACHE.is_dir():
        print("gradle cache not found:", GRADLE_CACHE)
        return attrs
    aars = list(GRADLE_CACHE.rglob("*.aar"))
    used = 0
    for aar in aars:
        # путь: .../<group>/<name>/<version>/<sha>/<name>-<version>.aar
        parts = aar.parts
        try:
            i = parts.index("files-2.1")
            group = parts[i + 1]
        except (ValueError, IndexError):
            continue
        if not group.startswith(LIB_PREFIXES):
            continue
        try:
            with zipfile.ZipFile(aar) as z:
                for n in z.namelist():
                    if n.startswith("res/values") and n.endswith(".xml"):
                        xml = z.read(n).decode("utf-8", errors="replace")
                        attrs.update(ATTR_DECL.findall(xml))
            used += 1
        except Exception as e:
            print("skip", aar.name, e)
    print(f"scanned {used} aars, lib attr names: {len(attrs)}")
    return attrs


def strip_attrs(path: Path, lib_attrs: set[str]) -> None:
    text = path.read_text(encoding="utf-8")
    removed = []
    # имена, реально объявленные top-level в файле
    declared = set(ATTR_DECL.findall(text))
    for name in sorted(declared & lib_attrs):
        pat = re.compile(
            r"[ \t]*<attr\b[^>]*\bname=\"%s\"[^>]*(?:/>|>.*?</attr>)[ \t]*\r?\n"
            % re.escape(name),
            re.S,
        )
        text, n = pat.subn("", text)
        if n:
            removed.append(name)
    path.write_text(text, encoding="utf-8")
    print(f"{path}: removed {len(removed)} lib-owned top-level attrs")
    left = declared & lib_attrs
    still = [n for n in left if re.search(r"<attr\b[^>]*\bname=\"%s\"" % n, text)]
    if still:
        print("  WARN still present:", still)


def main() -> int:
    lib_attrs = collect_lib_attrs()
    if not lib_attrs:
        return 1
    for t in TARGETS:
        if t.exists():
            strip_attrs(t, lib_attrs)
        else:
            print("missing", t)
    return 0


if __name__ == "__main__":
    sys.exit(main())
