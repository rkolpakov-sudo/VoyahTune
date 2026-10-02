"""Удаление из декодированного res файлов-дубликатов ресурсов Maven-AAR.

v2: сравнивает по БАЗОВОМУ типу ресурса (layout-land -> layout,
color-night -> color, drawable-anydpi-v26 -> drawable), т.к. apktool
раскладывает файлы по квалифицированным папкам, а AAR — тоже.
"""
import shutil
import sys
import xml.etree.ElementTree as ET
import zipfile
from pathlib import Path

GRADLE_CACHE = Path.home() / ".gradle" / "caches" / "modules-2" / "files-2.1"
LIB_PREFIXES = ("androidx.", "com.google.android.material")
BASE_TYPES = {"layout", "drawable", "anim", "animator", "menu", "color", "xml",
              "interpolator", "raw", "mipmap", "transition", "font"}
MODULES = [Path(r"Native\app\src\main\res"), Path(r"RestoreMode\app\src\main\res")]


def root_tag(xml_text: str) -> str | None:
    try:
        return ET.fromstring(xml_text).tag
    except ET.ParseError:
        return None


def base_type(dirname: str) -> str:
    # layout-land -> layout; values-night -> values (но values не трогаем)
    return dirname.split("-", 1)[0]


def aar_files() -> dict[tuple[str, str], str]:
    out: dict[tuple[str, str], str] = {}
    for aar in GRADLE_CACHE.rglob("*.aar"):
        parts = aar.parts
        try:
            group = parts[parts.index("files-2.1") + 1]
        except (ValueError, IndexError):
            continue
        if not group.startswith(LIB_PREFIXES):
            continue
        try:
            with zipfile.ZipFile(aar) as z:
                for n in z.namelist():
                    if not n.startswith("res/"):
                        continue
                    rest = n[4:]
                    if "/" not in rest:
                        continue
                    dirname, fname = rest.split("/", 1)
                    bt = base_type(dirname)
                    if bt not in BASE_TYPES or "/" in fname or "." not in fname:
                        continue
                    name = fname.rsplit(".", 1)[0]
                    out.setdefault((bt, name), z.read(n).decode("utf-8", errors="replace"))
        except Exception:
            pass
    return out


def main() -> int:
    libs = aar_files()
    print("lib files indexed (base types):", len(libs))
    total = 0
    kept = []
    for res in MODULES:
        removed = 0
        for f in sorted(res.rglob("*.xml")):
            rel = f.relative_to(res)
            if len(rel.parts) != 2:
                continue
            dirname, fname = rel.parts
            bt = base_type(dirname)
            if bt not in BASE_TYPES:
                continue
            name = fname.rsplit(".", 1)[0]
            key = (bt, name)
            if key not in libs:
                continue
            try:
                ours = f.read_text(encoding="utf-8", errors="replace")
            except OSError:
                continue
            if root_tag(ours) == root_tag(libs[key]):
                f.unlink()
                removed += 1
            else:
                kept.append(str(rel))
        print(f"{res}: removed {removed} (root-tag-mismatch kept below)")
        total += removed
    if kept:
        print("KEPT (root tag differs):", len(kept))
        for k in kept:
            print("  ", k)
    print("total removed:", total)
    # подчистить пустые папки
    for res in MODULES:
        for d in sorted(res.rglob("*"), reverse=True):
            if d.is_dir() and not any(d.iterdir()):
                d.rmdir()
    return 0


if __name__ == "__main__":
    sys.exit(main())
