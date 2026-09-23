# Релизный процесс VoyahTune

## Источники

- **`Packaging/README.md`** — что где лежит, флаги `make_release.sh`.
- **`Packaging/README.txt`** — копируется в корень релиза (пользователь).
- **`hownews.md`** — release notes для пользователя.
- **`docs/user/installation.md`** — краткая установка.

## Сборка

```bash
./make_release.sh 3.2.2
# флаги: --full-only --light-only --no-build --no-zip
```

Выход:

```
Releases/build/VoyahTune-X.Y.Z/         ← flat full
Releases/build/VoyahTune-X.Y.Z-light/    ← flat light
Releases/dist/*.zip
```

`Releases/` в `.gitignore`, в git не хранится. **Правим только `Packaging/`.**

## Чек-лист перед ZIP

- [ ] `bash -n` на всех `.sh` релиза (включая TUI: `install-tui.sh`, `tui-lib.sh`, `verify_post_install.sh`).
- [ ] `Packaging/tests/test_apollo_direct_only.sh` — PASS.
- [ ] APK собраны: `full` и/или `light` (если не `--no-build`).
- [ ] В full: TUI-файлы в `required` (`make_release.sh` / `verify_release_payload`).
- [ ] `README.txt` упоминает `install-tui`, **две перезагрузки**, кабель Type-A↔A.
- [ ] Версия в `hownews.md` / архиве совпадает.
- [ ] Живой smoke на Sport+ 2026 для hot-path изменений (иначе явная пометка).

## Чего избегать

- Правок скриптов прямо в `Releases/**` (артефакт, не источник).
- Смешивания bat/sh из разных версий в одном релизе.
- Minify/R8 release без keep-правил (в проекте `minify=false` — не включать самим).
