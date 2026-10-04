"""Ликвидация '$' в именах ресурсов декодированного APK.

aapt2 оригинальной сборки генерировал ресурсы вида `$name__N.xml` (сплит
градиентов inline-attr). AGP-мергер запрещает '$' в именах файлов.

- lib-авд ($avd/$m3/$mtrl): удаляем — AAR при сборке сгенерирует свои
  (appcompat/material шлют <animated-vector> с inline-градиентами).
- public.xml: убираем <public> для удалённых, переименовываем ic_launcher.
- $ic_launcher_foreground__0: app-ресурс → переименовываем (без '$'),
  обновляем ссылку в ic_launcher_foreground.xml и public.xml.
"""
import re
import sys
from pathlib import Path

MODULES = [Path(r"Native\app\src\main\res"), Path(r"RestoreMode\app\src\main\res")]
KEEP_PREFIX = "$ic_launcher_foreground"


def main() -> int:
    for res in MODULES:
        deleted: set[str] = set()
        renamed: dict[str, str] = {}
        for f in sorted(res.rglob("*.xml")):
            if "$" not in f.name:
                continue
            stem = f.stem  # напр. $avd_hide_password__0
            if stem.startswith(KEEP_PREFIX):
                new_name = f.name.replace("$", "", 1)
                new_f = f.with_name(new_name)
                f.rename(new_f)
                renamed[stem] = new_name[:-4]  # без .xml
                print(f"{res.name}: rename {f.name} -> {new_name}")
            else:
                f.unlink()
                deleted.add(stem)
                print(f"{res.name}: delete {f.name}")
        # правка ссылок во всех xml ресурсов
        for f in res.rglob("*.xml"):
            t = f.read_text(encoding="utf-8", errors="replace")
            orig = t
            for stem in deleted:
                t = t.replace(f"@drawable/{stem}", "@drawable/MISSING_DELETED")
                t = re.sub(
                    r'<public type="drawable" name="%s"[^/]*/>\r?\n?' % re.escape(stem),
                    "",
                    t,
                )
            for old, new in renamed.items():
                t = t.replace(f"@drawable/{old}", f"@drawable/{new}")
                t = t.replace(f'name="{old}"', f'name="{new}"')
            if t != orig:
                f.write_text(t, encoding="utf-8")
                print(f"  patched {f.relative_to(res)}")
        # проверка остаточных ссылок
        for f in res.rglob("*.xml"):
            t = f.read_text(encoding="utf-8", errors="replace")
            for m in re.finditer(r"@drawable/(\$[\w]+)", t):
                print(f"  LEFTOVER ref {m.group(1)} in {f.relative_to(res)}")
            if "MISSING_DELETED" in t:
                print(f"  MISSING_DELETED still referenced in {f.relative_to(res)}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
