"""Сравнение ресурсов декодированного APK с ресурсами Maven-AAR.

Цель: понять, сколько файлов совпадает (дамп библиотеки → удалить,
пусть ресурс даёт AAR) и сколько отличается (настоящий override app → оставить).
"""
import re
import zipfile
from collections import defaultdict
from pathlib import Path

GRADLE_CACHE = Path.home() / ".gradle" / "caches" / "modules-2" / "files-2.1"
LIB_PREFIXES = ("androidx.", "com.google.android.material")
RES = Path(r"Native\app\src\main\res")
TYPES_NO_EXT = {"layout", "drawable", "anim", "animator", "menu", "color", "xml",
                "interpolator", "raw", "mipmap", "transition", "font"}


def norm(text: str) -> str:
    text = re.sub(r"<!--.*?-->", "", text, flags=re.S)
    text = re.sub(r"\s+", " ", text)
    return text.strip()


def aar_index() -> dict[tuple[str, str], tuple[str, str]]:
    """(type, name) -> (aar_name, content). Берём первый встретившийся AAR."""
    out = {}
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
                    typ, fname = rest.split("/", 1)
                    if "/" in fname or typ not in TYPES_NO_EXT:
                        continue
                    name = fname.rsplit(".", 1)[0]
                    if (typ, name) not in out:
                        out[(typ, name)] = (aar.name, z.read(n).decode("utf-8", errors="replace"))
        except Exception:
            pass
    return out


def main() -> None:
    libs = aar_index()
    print("lib resource files indexed:", len(libs))
    same = []
    diff = []
    for f in sorted(RES.rglob("*.xml")):
        rel = f.relative_to(RES)
        parts = rel.parts
        if len(parts) != 2:
            continue
        typ, fname = parts
        if typ not in TYPES_NO_EXT:
            continue
        name = fname.rsplit(".", 1)[0]
        key = (typ, name)
        if key not in libs:
            continue
        aar_name, lib_content = libs[key]
        our = f.read_text(encoding="utf-8", errors="replace")
        if norm(our) == norm(lib_content):
            same.append(str(rel))
        else:
            diff.append((str(rel), aar_name))
    print(f"IDENTICAL to lib (safe to delete): {len(same)}")
    for s in same[:60]:
        print("   =", s)
    print(f"DIFFERENT (possible override, keep): {len(diff)}")
    for d, a in diff[:60]:
        print("   ~", d, "vs", a)


if __name__ == "__main__":
    main()
