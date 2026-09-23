# Архитектура VoyahTune (кратко)

Полное описание и IPC-контракты: **`docs/agent/PROJECT.md`**. Диаграммы аудита: `docs/audit/architecture-diagrams.md`.

## Один абзац

`RestoreMode` (UI, prefs) ⇄ Messenger / Provider / Broadcast ⇄ `Native` (`SetModesService`, `ApplyEngine`, `ApolloTlc`, `CanSender` → JNI → CAN) + `Settings.Global`. Full дополнительно: Frida in-process + `voyahtune.*.rc` + `load.bin`.

## Слои

| Компонент | Пакет | Роль |
|-----------|-------|------|
| RestoreMode | `ru.big.town.restoremode` | Activity, настройки, сплиты, виджеты |
| Native | `ru.big.town.anative` | системный сервис, CAN, Apollo |
| Installer | `Packaging/installer/{full,light}` | 12 фаз full / укороченный light |
| Frida | `Packaging/inject/*.js` | только full: док, руль, freeform, vd_bypass |
| Boot | `voyahtune.load.rc` + `load.bin` | только full, после `post-fs-data` |

## Данные и контракты

- **Messenger** `MSG_*` — числа **не менять** без согласования (ARCH-N03).
- **Provider** RestoreMode — чтение 20 колонок, update 5 ключей; экспорт без permission — риск **R3** (гейт).
- **Broadcast** — преимущественно explicit `setPackage`; часть уже signature-permission (`SETTING_SYNCED`, `APOLLO_*`, `SPLIT_RATIO_SAVE`).
- **CAN** — кадры ровно 10 байт; единая точка `CanSender` (fail-closed, lock) — **hot-path, не рефакторить** без отдельного проекта.

## Флейворы

См. `docs/user/installation.md`. `HAS_DIRECT_APOLLO=true` у обоих; minSdk 30, targetSdk 35, `minify=false`.

## Сборка

Gradle (Kotlin DSL), флейворы `full` / `light`. Release notes: `hownews.md`.
