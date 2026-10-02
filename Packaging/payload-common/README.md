# Packaging/payload-common — поддерживаемый исходник из payload 3.22.0

Файлы скопированы **verbatim** из `payload_3.22.0.zip → common/` (SPEC L09, L91).
Это поддерживаемый исходник: JS-хуки Frida, shell-скрипты, init-контракты,
конфигурации клавиатур, privapp-whitelist.

| Файл | Роль |
|---|---|
| `*.js` | JS-хуки Frida (loader, dock, клавиатура, руль, VD/freeform, ACC, drive-reset) |
| `load.bin` | shell-loader (начинается с `#!/s`; несмотря на расширение — текст) |
| `dns-helper.sh`, `voyahtune.load.sh`, `init.logcat.original.sh` | shell-скрипты |
| `voyahtune.load.rc`, `voyahtune.updater.rc` | init-контракты (updater.rc УДАЛЯЕТСЯ в WP4) |
| `whitelist.xml` | privapp-permissions (имена пакетов сохраняются, D4) |
| `voyahtune-ota-bootstrap.json` | OTA-bootstrap — **не переносим** (L63), только для аудита |
| `voyahtune_keyboard_*.json`, `*_qwerty_ru.json` | конфигурации клавиатур |

Сверка целостности: `payload/inventory.json` (sha256 каждого артефакта).
Перегенерация из payload: `Utils/build_inventory.py` + распаковка zip.
