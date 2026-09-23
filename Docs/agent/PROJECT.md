# PROJECT.md — описание проекта VoyahTune для бесшовного перехода между сессиями

> Версия: 1.0 · Дата: 2026-09-23 · Основа: код-ревью коммита `45beee4` + план рефакторинга.
> Обязательно к чтению вместе с `AGENTS.md` (аксиомы) и `HISTORY.md` (где мы находимся).

---

## 1. Что это

**VoyahTune** (эволюция RestoreMode / «Open Voyah») — система Android-приложений и установочных скриптов для автомобиля **Voyah Free**.

**Целевая машина (аксиома):** **Voyah Free Sport+ 2026** (в changelog: «OD Sport+ 26»). Все решения принимаются под этот стенд.

**Функции:**
- Восстановление режимов вождения/энергии/рекуперации после пробуждения (прямая запись CAN).
- Автоматизации: автосвет, сервисный режим дворников, прогрев батареи, статистика поездок, Power Hold, режим мойки, звук пешеходов, Forced EV.
- UI: главный экран с виджетами, хаб настроек «Дополнительно», свои CAN-команды, кнопки руля.
- Full-only (Frida): сплиты/freeform, замена системного дока, перехват кнопок руля, мультидисплей.
- Apollo/ADAS TLC: прямой Binder (full+light), legacy-хук VehicleSetting — только full и только opt-in.

**Версии флейворов:**

| | light | full |
|--|-------|------|
| Режимы/автосвет/дворники/прогрев/Direct Apollo | ✅ | ✅ |
| Frida, сплиты, док, кнопки руля, boot-hook load.bin | ❌ | ✅ |
| Запись `/system`, `adb root` | ✅ | ✅ |

---

## 2. Железо и ограничения автомобиля

| Ограничение | Значение |
|-------------|----------|
| Кабель | Только USB **Type-A ↔ Type-A**, USB 2.0; Type-C не работает |
| ADB over Wi-Fi | Только дорестайл (Android 9) |
| Требования установки | `adb root`, `disable-verity`, запись в `/system` |
| Перезагрузки | **Две:** №1 — если `/system` RO после disable-verity; №2 — финальная (поднимает boot-hook, priv-app, freeform) |
| CAN | Кадры **ровно 10 байт**; TX=77 (режимы), TX=58 (свет), TX=46/36/28 (ICarSignalService/Apollo query) |
| Прошивки | 4.1 OD: энергия+рекуперация в шторке; **7.1 OD: только рекуперация** (баг OEM) — сверять с readme FAQ |

---

## 3. Архитектура (упрощённо)

```
RestoreMode (ru.big.town.restoremode)          Native (ru.big.town.anative, priv-app)
┌─────────────────────────┐   Messenger MSG_*  ┌──────────────────────────────────────┐
│ MainActivity, Advance…  │◄──────────────────►│ SetModesService + ApplyEngine        │
│ DrivePreferences (prefs)│   Provider (чтение)│ ApolloTlcService (Binder, fail-closed)│
│ SplitStore/Dpi/Shortcut │◄──────────────────►│ CanSender (JNI lock) → native-lib.cpp│
└───────────┬─────────────┘   Broadcasts       │  → libqg_hal → CAN                  │
            │ explicit setPackage              │ Settings.Global (dock/steer/apollo)  │
            ▼                                 └──────────┬───────────────────────────┘
      SharedPrefs / UI                                   │ Settings.Global
                                                         ▼
                                        Frida in-process: vd_bypass (system_server),
                                        launcherdock, steeringwheelkeys, multidisplay,
                                        apollo_tech (opt-in)
                                        ▲ load.bin (watchdog 10s) ← voyahtune.load.rc
```

**Сильные стороны (не ломать):** единый CAN-gate `CanSender` (fail-closed, batch lock); policy-классы чистые и юнит-тестируемые; epoch/coverage в `ApplyEngine`; snapshot/rollback boot-hook; signature-permission уже на части IPC.

**Сборка:** Gradle (Kotlin DSL), флейворы `full`/`light`, `HAS_DIRECT_APOLLO=true` у обоих, minSdk 30, targetSdk 35, `minify=false` в release.

---

## 4. IPC-контракты (краткая шпаргалка)

### Messenger (RestoreMode → `SetModesService`)
`MSG_APPLY_DRIVE_MODES=1`, `STAR_BUTTON=2`, `RESULT=4`, `AUTO_LIGHT 10/11`, `LEAVE_CAR=20`, `PEDESTRIAN=21`, `REBOOT=22`, `WASH=23`, `FLOATING_BACK 24/25`, `GRANT_INSTALL=26`, `CLOSE_ALL=27`, `THEME=28`, `SPLIT_LAUNCH_VD=34`, `FORCED_EV=35`, `APOLLO_* 36–41`.

### Provider
- RestoreMode: authority `ru.big.town.restoremode.restoremodecontentprovider` — query 20 колонок (в т.ч. `customCommand` — сырые CAN), update 5 ключей. **⚠ Экспорт без permission (риск R3).**
- Native: `content://ru.big.town.anative.nowplaying` — now playing (клиент в RestoreMode — мёртвый код).

### Broadcasts (в основном explicit на пакет)
- RestoreMode → Native: trip, battery heat, lux, log, dock/steer config, apollo request…
- Native → RestoreMode: `TRIP_UPDATE`, `BATTERY_HEAT_UPDATE`, `SETTING_SYNCED` ✔ signature, `MODE_SYNCED`, `LUX_UPDATE`, `APOLLO_TLC_UPDATE` ✔, `LOG_UPDATE`, `SPLIT_RATIO_SAVE` ✔.
- **⚠ 5 receivers exported без permission (риск R4).**

### Settings.Global (пишут Native/установщик → читают/Frida)
`persist.app.feature.leavecar`, `enable_freeform_support`, `force_resizable_activities`, `voyahtune_dock1/2`, `dockLaunchGuardN`, `voyahtune_ste*`, `voyahtune_win_*`, `voyahtune_dpi_*`, `open_voyah_apollo_*`, Apollo master.

### Frida-цели (full)
| Скрипт | Процесс |
|--------|---------|
| `vd_bypass.js` | system_server |
| `launcherdock.js` | launcher (OD/PI классы различаются) |
| `steeringwheelkeys.js` | `com.qinggan.keymanager.service` |
| `multidisplay.js` | `com.qinggan.systemservice` |
| `apollo_tech.js` | `com.qinggan.app.vehiclesetting` (opt-in) |

---

## 5. Установка (сводно)

- **Источник комплекта:** `Packaging/`; сборка релиза: `make_release.sh <ver>`; `Releases/` вне git.
- **full/install.sh (12 фаз):** preflight → Apollo safety=0 → CANBUS owner → disable-verity → [reboot №1] → backup → `/data/local/bin` → legacy init → boot-hook (snapshot/rollback) → Native.apk+whitelist → props → RestoreMode.apk → DNS → [reboot №2].
- **Межфазовой транзакции нет** — при обрыве: **не перезагружать**, повторить тот же installer (идемпотентен).
- **light:** без Frida/boot-hook; **слабое место:** push без `|| exit 1` и без restorecon (риск R9).
- **Откат:** `remove.*` из той же папки с `backup/`.
- **Регресс:** `Packaging/tests/test_apollo_direct_only.sh` — гонять при правках установщиков/apollo.

---

## 6. Хранилище настроек

Один файл **`DrivePreferences`** (RestoreMode): режимы, `customCommand*`, видимость виджетов, тогглы, `splitPresets` (JSON, UUID), `appDpi`, `appShortcuts`, dock/steer (⚠ dock/steer сплит адресуется **индексом** пресета), `debugMode`, `loggingEnabled` и др. Native читает через provider, пишет 5 белых ключей.

---

## 7. Риски (полная матрица: `docs/audit/risk-assessment.md`)

Топ, о которых помнить всегда:
- **R1** ошибка в CAN policy/transport → команда в шину.
- **R2** крэш `vd_bypass` в system_server → soft-reboot.
- **R3/R4** открытый provider/receivers RestoreMode.
- **R5** `debugMode` fail-open в `CanSender`.
- **R9** light install без проверки push.
- **R20** `RECEIVER_EXPORTED` (API 33) при minSdk 30 — **не трогать без подтверждения API ГУ**.
- **R7** `setenforce 0` на буте — осознанное решение проекта.

---

## 8. Тестирование (Фаза 2 — ещё не создана)

- Ручной чек-лист на **Sport+ 2026** (режимы до/после пробуждения, автосвет, дворники, откат).
- Read-only ADB smoke: устройство, root, пакет `ru.big.town.anative`, `leavecar=true`, сервисы живы.
- Матрица совместимости: **эталон — Sport+ 2026**; всё остальное явно «не проверено».

---

## 9. Ссылки

- Репозиторий: `https://github.com/nexron171/VoyahTune` (master, аудит: `45beee4`)
- План: `Qwen_markdown_20260923_r7xvknhqu.md`
- Аудит: `docs/audit/` · Аксиомы: `AGENTS.md` · Журнал: `HISTORY.md`
- Справочники: `Docs/CAN-команды.odt`, `Docs/can.pdf`
- Сообщество: drive2 (см. readme), voyahchat.ru (кабели, USB-debugging)
