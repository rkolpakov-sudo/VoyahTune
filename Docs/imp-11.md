# IMP-11 — Версионирование 4.x.y+build.N, archive/, BUILD-INFO, CI-гейт (SPEC L53, L136, R7)

Статус: реализован (WP5, см. DECISIONS IMP-11). Priority P0|S. R7 — «перепубликации под
тем же номером (3.16x2; 3.21 backups)» — дефект базы, устраняется здесь.

## Требования SPEC

> **L53 IMP-11**: Версионирование 4.x.y+build.N; archive/ для старых build;
> BUILD-INFO (revision, тулчейн Rust 1.98.1, builder id, дата, хеши); запрет
> перепубликации enforced в CI. P0|S
>
> **L136**: Версионирование 4.x.y+build.N; archive/ для старых build; запрет
> перепубликации: **CI падает при существующем номере**.
> **L139**: Откат: git; старые build в archive/.
> **L161**: … тег `v4.x.y+build.N` → публикация Releases → анонс.

## 1. Схема версии — строго `4.x.y+build.N`

Канон — семвер-схема SPEC (`fork.config.toml [distribution].version_scheme`,
README, тег L161). Валидация в `make_release.sh` (POSIX ERE):

```
^4\.[0-9]{1,4}\.[0-9]{1,4}\+build\.[0-9]{1,6}$
```

- Префикс `v` допустим и нормализуется (`VERSION="${VERSION#v}"` — как раньше);
- **Легаси-форма `-build.N` (4.0.0-build.1) отклоняется** с подсказкой схемы:
  история локальных сборок в DECISIONS остаётся историей; опубликованных
  payload-релизов/тегов ещё нет (только `blobs-v1`), так что миграции не требуется.
  Строгость обязательна: две записи одного номера (`+`/`-`) — это и есть R7.
- CI: версия payload-джобы — `4.0.0+build.${{ github.run_number }}`
  (run_number уникален и никогда не переиспользуется).

## 2. Запрет перепубликации (двухуровневый)

1. **make_release.sh** (локально и на CI, сразу после валидации схемы):
   если `Releases/dist/payload_<VERSION>.zip` уже существует — падение
   «номер уже собран/опубликован (R7/IMP-11) — bump build.N». Дополнительно
   гейтится `Releases/archive/<VERSION>/`: номер, однажды ушедший в архив,
   всё равно считается использованным (иначе после архивации его можно было
   бы собрать повторно — то самое R7). Публикуемый артефакт = dist-zip;
   staging `Releases/build/payload-<v>` пересобирается атомарно и гейтом
   не блокируется (retry после сбоя — до первой публикации номера — остаётся
   возможен).
2. **ci.yml** (шаг до сборки, «CI падает при существующем номере»):
   `git ls-remote --tags origin "refs/tags/v<VERSION>"` — тег существует →
   падение. Защищает от переиспользования номера при ручной правке версии
   и от ребилда уже опубликованного тега.

Публикация Releases (тег → release) — ручная по L161; автоматика публикации нет,
поэтому гейт стоит именно на сборке/ребилде номера.

## 3. archive/ для старых build

`Releases/archive/<version>/` (всё внутри gitignore-каталога `Releases/`).
Внутри шага dist (`make_release.sh`), ДО перезаписи dist-файлов:

- читается `version` из существующего `Releases/dist/BUILD-INFO.json`;
- если это другая версия — её комплект (`payload_<v>.zip`, `payload_<v>.json`,
  `BUILD-INFO.json`, `SHA256SUMS`) + любые чужие `payload_*` переезжают в
  `Releases/archive/<v>/` (не теряются при перезаписи);
- после успешной сборки чужие staging-каталоги `Releases/build/payload-*`
  переезжают в `Releases/archive/<v>/build/payload-<v>/`.

Откат (L139): git — исходники, archive/ — собранные артефакты прошлых номеров.

## 4. BUILD-INFO (L135/L53)

Существующий `Releases/dist/BUILD-INFO.json` расширяется:

```json
{
  "product": "VoyahTune", "version": "<V>", "revision": "<git sha>",
  "toolchain": { "gradle": "...", "agp": "...", "jdk": "...", "rust": "1.98.1" },
  "builder": "github-actions/run-N | user@host",
  "date": "<UTC ISO8601>",
  "hashes": {
    "payload_<V>.zip": "<sha256>",
    "manifest.json": "<sha256>",
    "native.apk": "<sha256>",
    "restore_mode.apk": "<sha256>"
  }
}
```

- `toolchain.rust` — из пина `Installer/rust-toolchain.toml` (`channel = "1.98.1"`,
  единственный источник правды; fallback `rustc --version`, затем `unknown`).
- `hashes` — те же артефакты, что входят в комплект (zip + внутренний manifest +
  обе APK); `SHA256SUMS` остаётся контрольным списком dist-каталога и включает
  сам BUILD-INFO (как и раньше).

## 5. Тесты

`Packaging/tests/test_release_versioning.sh` (шаблон контрактных vd/release-тестов):

- grep `make_release.sh`: ERE-схема, гейт перепубликации (dist-zip + сообщение),
  `Releases/archive`, поля BUILD-INFO (`rust`, `hashes`), парсинг пина Rust;
- grep `ci.yml`: шаг гейта (`git ls-remote --tags`), версия
  `4.0.0+build.${{ github.run_number }}`;
- grep пина: `Installer/rust-toolchain.toml` содержит `channel = "1.98.1"`;
- **функционально** (выход до тяжёлых шагов):
  - `3.22.0` → отказ по схеме (сообщение с `4.x.y+build.N`);
  - `4.0.0-build.9` (легаси) → отказ по схеме;
  - `4.0.0+build.77` c заранее созданным dist-zip → отказ «уже собран»
    (заодно доказывает, что валидную версию скрипт принимает);
  - мусорные файлы тест подчищает через trap, чужие файлы не трогает.

## Риски / ограничения

- **`+` в имени/URL**: `+` в path — легальный sub-delim, GitHub отдаёт ассеты с `+`
  буквально (path-`+` не декодируется в пробел; form-encoding действует только для
  query). Публикации ещё нет — выбираем канон SPEC до первого релиза, чтобы не
  переименовывать потом. Локальный импорт ZIP от `+` не зависит.
- Гейт CI смотрит на теги (публикация по L161 — ручная); одноразовый
  `github.run_number` делает коллизию на CI невозможной — шаг защищает от ручных
  правок/ребилда.
- `versionCode/versionName` Android-приложений остаются `3022000`/`3.22.0`
  (идентичность D4: форк — та же база; release-идентичность APK несёт
  `voyahtune-build.json` c `releaseVersion`, который уже приходит из
  `-PvoyahReleaseVersion=$VERSION`). IMP-11 не трогает Android-идентичность.
- Shell-контракты (`test_android11_package_lifecycle.sh` и др.) проверяют
  `make_release.sh` по строкам — новые блоки не удаляют ни одной проверяемой строки.
