# blobs/ — бинарные блобы (ре-хост, НЕ реверс, D16)

Все файлы извлечены из payload 3.22.0 (`artifacts/3.22.0/unpacked/`) и зафиксированы
в `BLOBS-SHA256.txt`. В git НЕ коммитятся (см. `.gitignore`); распространяются через
наши Releases (WP7, blobs-v1) с пиннингом по sha256 в CI.

## Состав

| Путь | Источник | Стратегия |
|---|---|---|
| `frida/frida-inject` | payload `common/frida-inject` | ре-хост; интерфейс: инъекция JS-хуков в процессы |
| `native/libanative-*.so` | native.apk `lib/<abi>/` (4 ABI) | ре-хост внутри APK; контракт: JNI-слой Native |
| `native/libonnxruntime.so` | restore_mode.apk `lib/arm64-v8a/` | ре-хост; ONNX Runtime для голоса |
| `native/libsherpa-onnx-jni.so`, `libsherpa-onnx-c-api.so` | restore_mode.apk | ре-хост; sherpa-onnx JNI (Zipformer2) |
| `native/libvoyah_df.so` | restore_mode.apk | ре-хост; DeepFilterNet3 (шумоподавление) |
| `native/voyahtune-ui-maintenance`, `native/voyahtune-updater` | payload `common/` (ELF) | ре-хост; **voyahtune-updater = root-служба, УДАЛЯЕТСЯ в WP4** |
| `apk/dns.apk` | payload `common/dns.apk` | ре-хост; DNS-помощник (составной payload, не наша сборка) |
| `apk/voyahtune-ui-next.apk` | payload `common/voyahtune-ui-next.apk` | ре-хост; UI-пакет |
| `apk/voyahtune-updater.apk` | payload `common/voyahtune-updater.apk` | ре-хост; апдейтер-пакет |
| `voice/zipformer-ru-0.54-int8/*.onnx` | restore_mode.apk `assets/` | ре-хост; ASR-модель (encoder 67.6 МБ — уже int8) |
| `voice/silero_vad.onnx` | restore_mode.apk `assets/` | ре-хост; VAD |
| `voice/voice-licenses/` | restore_mode.apk `assets/` | лицензии моделей (Apache-2.0/MIT, model card) |
| `adb/` | *(пусто)* | ADB-бандл установщика 1.4.0 — добавляется в WP6 |

## Скачивание и проверка

- `sh scripts/fetch-blobs.sh --verify` — только сверка sha256 уже скачанного (30/30);
  `sh scripts/fetch-blobs.sh` — скачать всё; `sh scripts/fetch-blobs.sh <path>…` —
  подмножество (именно так делает `make_release.sh --payload`).
- URL: `BLOBS_BASE_URL` (дефолт
  `https://github.com/rkolpakov-sudo/VoyahTune/releases/download/blobs-v1`).
- Релиз `blobs-v1`: **плоские ассеты** — имя файла в релизе = базовое имя файла в
  `BLOBS-SHA256.txt` (вложенных путей GitHub Releases не поддерживают), отсюда
  уникальность базовых имён (30 записей = 30 уникальных имён).

## Что здесь НЕ блоб

- `load.bin` — shell-loader (начинается с `#!/s`), поддерживаемый исходник.
- JS-хуки, `dns-helper.sh`, init.rc, конфиги JSON/XML — поддерживаемый исходник (I.2 L09).

## Лицензии

См. `voice/voice-licenses/` (DeepFilterNet: Apache-2.0 + MIT; zipformer: model card).
Frida — upstream GPL-3.0 (проект frida). Остальное: интерфейсы документируются, не реверсятся (D16).
