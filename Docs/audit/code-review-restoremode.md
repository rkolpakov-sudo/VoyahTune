# Code Review: RestoreMode (VoyahTune UI)

**Область**: `C:\Projects\VoyahTune\RestoreMode` — 13 Java-файлов, AndroidManifest, Gradle, layouts/res. Только анализ, файлы не изменялись.
**Коммит**: `45beee4` (master). **Дата аудита**: 2026-09-23.

---

## 1. Обзор архитектуры

**Назначение**: одномерный (`:app`) Android-приложение-«панель управления» для головного устройства Voyah (Android Automotive-like). Пакет `ru.big.town.restoremode`, UI-бренд — **VoyahTune**. Весь тяжёлый I/O (CAN, root, VirtualDisplay, Frida) выполняет **Native-приложение `ru.big.town.anative`** (priv-app); RestoreMode — фронтенд + локальное хранилище настроек.

**Слои**:

```
┌─────────────────────── RestoreMode (UI) ────────────────────────┐
│ MainActivity (лаунчер, «домашний экран»)                        │
│ AdvanceActivity (7-раздельный хаб настроек, 2114 строк)         │
│ TripHistoryActivity / LoggingActivity / AdvanceActivityStarButton│
│ RestoreModeContentProvider ← Native читает DrivePreferences      │
│ SplitRatioSaveReceiver (manifest, signature-permission)          │
│ GlobalVars (static: prefs + Messenger)                           │
│ Stores: SplitStore / AppDpiStore / AppShortcutStore / SplitConfigSync│
└───────────────┬─────────────────────────────┬───────────────────┘
     bindService + Messenger          broadcasts (оба направления)
     content://…restoremode…provider  content://…anative.nowplaying
┌───────────────▼─────────────────────────────▼───────────────────┐
│ Native (ru.big.town.anative): SetModesService,                  │
│ SetModesConfigReceiver, BatteryHeatService, NowPlaying*,         │
│ ApplyEngine, VirtualDisplay-хост, Frida-хуки (док/руль)          │
└─────────────────────────────────────────────────────────────────┘
```

- **Activities** (5, все `exported=false`, кроме MainActivity-лаунчера): `MainActivity`, `AdvanceActivity`, `AdvanceActivityStarButton`, `TripHistoryActivity`, `LoggingActivity`.
- **ContentProvider** (`RestoreModeContentProvider`, authority `ru.big.town.restoremode.restoremodecontentprovider`, **exported без permission**) — read-mostly снимок 20 колонок `DrivePreferences` + ограниченный `update()` (5 ключей), чтобы Native читал настройки единым источником и писал обратно «последний активный режим» при смене кнопкой руля.
- **Канал команд**: `bindService` к `ru.big.town.anative.SetModesService` → `Messenger` с целочисленными `MSG_*` (1,2,4,10,11,20–28,34–41); реплай `MSG_RESULT=4`.
- **Флейворы** (`build.gradle.kts:64-75`): `full` (сплит/VD/док/руль) и `light`; оба с `HAS_DIRECT_APOLLO=true`. Ветвление — через `BuildConfig.IS_FULL`.
- **Зеркалирование**: пресеты сплита и действия руля дублируются broadcast'ом в Native → `Settings.Global` → читаются Frida-скриптами (`launcherdock.js`, `keymng2.js`).
- **Хранилище**: один файл SharedPreferences **`DrivePreferences`** — фактически база данных всего продукта.

---

## 2. Таблица файлов (purpose / key methods / dependencies)

| Файл | Назначение (1 строка) | Ключевые методы | Зависимости |
|---|---|---|---|
| `MainActivity.java` (880 стр.) | Домашний экран: виджеты поездки/прогрева/тогглы + плитки сплитов/ярлыков, биндинг Messenger | `onCreate`, `bindToMessengerService`/`scheduleMessengerRebind`, `sendMessageToService`, `renderSplitTiles`, `sendSplitVd`/`sendAppVd`, `renderBatteryHeat`/`applyBatteryHeatIndicator`, `updateTripTimer`, `getModes`, `onActivityResult`, `onCard*`, `onButton*` | `GlobalVars`, `SplitStore`, `AppDpiStore`, `AppShortcutStore`, `BuildConfig`, Material dialogs/Snackbar, provider (свой), Native: SetModesService + broadcasts |
| `AdvanceActivity.java` (2114 стр.) | Хаб настроек: 7 секций (экран, авто, сплиты, Apollo, CAN-команды, руль, другое) | `setSection`, `onButtonClickApply`/`setApplying` (12с timeout), CAN-`TextWatcher` (валидация `%31`), `initSplitScreen`/`renderSplitPresets`/`saveSplitPresets`, `initDockOverride`/`pushDockConfig`, `initSteeringButtons`/`pushSteerConfig`, Apollo-машина состояний (`canChangeApollo*`, `updateApolloUi`, `formatApolloError`), `engineeringPassword`, `showAppPicker`, `initAppDpiList` | `GlobalVars`, `SplitStore`, `SplitConfigSync`, `AppDpiStore`, `AppShortcutStore`, `BuildConfig`, provider-независим; broadcasts LUX/MODE/SETTING/APOLLO |
| `AdvanceActivityStarButton.java` (233 стр.) | Экран двух CAN-команд для «кнопки звёздочки» (дубль форматтера ×2) | `onButtonClickSave/Apply1/Apply2/Clean*`, два идентичных `TextWatcher` | `GlobalVars.editor/sharedPreferences/clientMessenger` (**null- risk**), layout `activity_advance_start_button` |
| `TripHistoryActivity.java` (158 стр.) | Список последних 10 поездок + удаление | `renderTripLog` (JSON→rows), `confirmDelete` (broadcast `TRIP_DELETE`), `applyWindowInsets`, `fmtDurationShort` | `MainActivity.ACTION_*`, Material, JSON |
| `LoggingActivity.java` (131 стр.) | Тумблер записи логов Native + живая лента (poll 1 с) + share | `onButtonShareLog`, `requestSnapshot`, `logReceiver` (якорь скролла от низа) | broadcasts `LOGGING_SET/SHARE`, `REQUEST_LOG`, `LOG_UPDATE`; `DrivePreferences.loggingEnabled` |
| `RestoreModeContentProvider.java` (158 стр.) | Единый read-снимок настроек для Native + whitelist-`update` 5 ключей | `query` (20 колонок MatrixCursor), `update` (driveMode/energy/recycle/forcedEv/disablePedestrianSound), заглушки `insert/delete`, `getType` | `DrivePreferences` |
| `GlobalVars.java` (27 стр.) | Процессный static-контейнер: prefs/editor + Messenger + refcount | `clientConnected`/`clientDisconnected` (synchronized) | Android Messenger/SharedPreferences |
| `SplitStore.java` (90 стр.) | Пресеты сплита: JSON-массив под `splitPresets`, стабильные UUID id (с миграцией) | `load`/`save`, `leftFraction`, `Preset.ready()` | SharedPreferences, org.json |
| `SplitConfigSync.java` (91 стр.) | Зеркало пресетов/dock/steer в Native (`Settings.Global` через его receiver) | `pushAll`/`pushDock`/`pushSteering`, `resolveSteerAction` (CSV back-compat), `addDockSplitExtras` | `SplitStore`, `AppDpiStore`, explicit-component broadcast |
| `SplitRatioSaveReceiver.java` (41 стр.) | Постоянный приёмник результата ресайза сплита (работает без MainActivity) | `onReceive`: presetId→idx fallback → `save`+`pushAll` | `SplitStore`, `SplitConfigSync`, signature-permission в манифесте |
| `AppDpiStore.java` (36 стр.) | Per-app DPI: JSON-объект `appDpi` (pkg→dpi, 0=авто) | `get`/`set` | SharedPreferences, org.json |
| `AppShortcutStore.java` (35 стр.) | Список ярлыков приложений на главной, JSON `appShortcuts` | `load` (dedup)/`save` | SharedPreferences, org.json |
| `NowPlayingClient.java` (134 стр.) | Pull/push-клиент «сейчас играет» к провайдеру Native (query/art/broadcast) | `query`, `fromBroadcast`, `loadArt`, `requestRefresh` | ContentResolver, `content://ru.big.town.anative.nowplaying` — **в проекте не используется ни разу** |

**Тесты**: только `ApolloStateQueryPolicyTest` (3 кейса fallback-политики) + заглушка `ExampleUnitTest` + `ExampleInstrumentedTest`.

---

## 3. IPC-механизмы

### 3.1 Messenger (bindService → `SetModesService`)

Биндинг живёт в `MainActivity` (`MainActivity.java:360-410`), состояние — `GlobalVars`. Retry каждые 5 с; обработка `onBindingDied`/`onNullBinding`.

| Код | Назначение | Кто шлёт |
|---|---|---|
| `MSG_APPLY_DRIVE_MODES=1` | Применить режимы + CAN-цикл (реплай `MSG_RESULT=4`) | Advance |
| `MSG_APPLY_DRIVE_MODES_STAR_BUTTON=2` | Выполнить команду звёздочки (arg1=1/2) | StarButton |
| `MSG_RESULT=4` | Окончание цикла применения | Native → replyTo |
| `MSG_AUTO_LIGHT_ENABLE/DISABLE=10/11` | Старт/стоп LightSensorService | Main, Advance |
| `MSG_LEAVE_CAR=20`, `MSG_APPLY_PEDESTRIAN=21`, `MSG_WASH_MODE=23` | Power Hold / звук пешеходов / режим мойки | Main |
| `MSG_REBOOT=22`, `MSG_FLOATING_BACK=24/25`, `MSG_GRANT_INSTALL=26`, `MSG_CLOSE_ALL=27`, `MSG_SET_THEME=28` | Системные операции | Advance |
| `MSG_SPLIT_LAUNCH_VD=34` | Запуск сплита/одиночного приложения на VD (Bundle: left/right, DPI, split, presetId) | Main |
| `MSG_APPLY_FORCED_EV=35` | Форс-электрорежим | Main, Advance |
| `MSG_APOLLO_QUERY/SET_*=36..41` | Apollo Tech read/write | Advance |

### 3.2 Broadcasts **RestoreMode → Native** (все с `setPackage("ru.big.town.anative")` либо explicit `setClassName`)

`REQUEST_TRIP_UPDATE`, `TRIP_RESET`, `BATTERY_HEAT_ACTIVATE`, `REQUEST_BATTERY_HEAT`, `TRIP_HISTORY(enabled)`, `TRIP_DELETE(start)`, `REQUEST_LUX_UPDATE`, `REQUEST_APOLLO_TLC_UPDATE`, `REQUEST_NOW_PLAYING`, `REQUEST_LOG`, `LOGGING_SET(on)`, `LOGGING_SHARE`, `DOCK_CONFIG`/`STEER_CONFIG` (→ `SetModesConfigReceiver`, компонентная адресация — хорошо).

### 3.3 Broadcasts **Native → RestoreMode** (регистрация в `onResume`)

| Action | Receiver | Защита при приёме |
|---|---|---|
| `TRIP_UPDATE` | Main + TripHistory | **`RECEIVER_EXPORTED` без permission** |
| `BATTERY_HEAT_UPDATE` | Main | **exported без permission** |
| `SETTING_SYNCED` | Main + Advance | signature-permission `BIND_SET_MODES_SERVICE` ✔ |
| `MODE_SYNCED` | Advance | **exported без permission** |
| `LUX_UPDATE` | Advance | **exported без permission** |
| `APOLLO_TLC_UPDATE` | Advance | signature-permission ✔ |
| `LOG_UPDATE` | Logging | **exported без permission** |
| `SPLIT_RATIO_SAVE` (→ нашему `SplitRatioSaveReceiver`) | manifest-receiver | signature-permission в манифесте ✔ |
| `NOW_PLAYING` | (клиент есть, подписчика нет) | — |

### 3.4 ContentProvider

- `query` → 20 колонок: режимы, пороги, **`customCommand` (сырые CAN-фреймы)**, звёздочки, `batteryHeatAuto`, `pauseMediaOnDoor`, `forcedEv` и др.
- `update` → Native пишет 5 белых ключей (смена режима кнопкой руля → переживает пробуждение).
- **Без `android:permission`/`readPermission`/`writePermission` при `exported="true"`**.

### 3.5 NowPlaying (Native-провайдер)

`NowPlayingClient` читает `content://ru.big.town.anative.nowplaying[/art]` и умеет подписываться на `NOW_PLAYING` — инфраструктура готова, **в RestoreMode не подключена**.

---

## 4. Персистентность состояния

**Единый файл**: `DrivePreferences` (MODE_PRIVATE). Основные группы ключей:

| Группа | Ключи | Формат / кто пишет | Кто читает |
|---|---|---|---|
| Режимы | `driveMode`, `energy`, `recycle`, `*Enabled` | строки/bool; Advance (radio), **Native через provider.update** | provider.query → ApplyEngine Native |
| CAN-команды | `customCommand`, `customCommandCount`, `customCommandStarButton1/2` | строка/ int | Advance (editor), Native |
| Видимость главной | `showTripTimer/PowerHold/WashMode/AutoLight/Pedestrian/BatteryHeat` (default true), `showForcedEv` (**default false**), `saveTripHistory` | bool, Advance пишет | Main `applyMainScreenVisibility` в `onResume` |
| Мгновенные тогглы | `autoLight`, `disablePedestrianSound`, `forcedEv` | bool; Main/Advance; **Native → provider.update** при синхронизации с рулём | Main `refreshToggles`, provider |
| Прочее | `batteryHeatAuto` (кол. 17), `pauseMediaOnDoor` (18), `wiperColdMode`, `autoLaunchOnWake`, `floatingBackButton/Side`, `themeOverride`, `debugMode`, `loggingEnabled`, `showCustomCommands`, `lightSensorThreshold*`, `checkBox34`, `driveEnabled…` | bool/int | Advance; **Native через provider** (без broadcast — по комментариям) |
| **SplitStore** | `splitPresets` — JSON-массив `{id(UUID), l,ll,r,rl,ratio 0..4,resizable,split}` | `load` с разовой миграцией id (`SplitStore.java:43-69`); `save` **всегда через `SplitConfigSync.pushAll`** (`AdvanceActivity.java:1016-1019`) | Main (плитки), SplitRatioSaveReceiver, SplitConfigSync |
| **AppDpiStore** | `appDpi` — JSON-объект `pkg→dpi` (0=авто) | Advance (спиннер), `get` при старте VD | sendSplitVd/sendAppVd, pushDock |
| **AppShortcutStore** | `appShortcuts` — JSON-массив пакетов (dedup при load) | Advance add/remove | Main `renderSplitTiles` |
| **Dock** | `dockOverride1/2`, `*Label`, `dockOverrideNSplit` (**INDEX пресета!**), `*SplitLabel` | Advance (пикер/долгий тап/clear) | `SplitConfigSync.pushDock` → Native Settings.Global → Frida |
| **Steering** | 8× `steerStar/Dvr/Voice/Phone Short/Long` = id действия (`none`, `drive:*`, `split:N`, `app:pkg`…) | Advance | `pushSteering` → Native; `resolveSteerAction` разворачивает `split:N` в CSV из 8 полей |

**Цикл пропорции сплита (split config sync)**:
1. Пользователь тянет делитель в сплите (только `resizable`) → Native шлёт `SPLIT_RATIO_SAVE{split, presetId, presetIdx}`.
2. `SplitRatioSaveReceiver` находит пресет **по id, fallback — по индексу** (совместимость со старым Native), пишет `preset.split`, `SplitStore.save` + `SplitConfigSync.pushAll` — обе копии (prefs и Settings.Global) обновляются атомарно относительно UI.
3. При следующем открытии `leftFraction(preset)` возвращает сохранённую руками долю, иначе фиксированную из `ratio` (3:4→3/7 и т.д.).

**Слабое место**: dock-сплит и steer-сплит адресуются **индексом** (`dockOverrideNSplit`, `split:N`), а не UUID — удаление/перестановка пресета молча ломает назначение (грациозно: `HasSplit=false` / «Сплит (не найден)», но неочевидно для пользователя).

---

## 5. Ключевые функции

### 5.1 MainActivity (UI главного)

- **Лайфсайкл Messenger**: `bindingRequested`/`connectionReported`/`destroyed` + rebind-рантайбл (`MainActivity.java:325-410`) — аккуратная защита от `onNullBinding`/`onBindingDied` и утечки `unbindService`.
- **Таймер поездки**: 1-сек `tripTick` + инкремент `elapsedRealtime() - driveStartElapsed` (**контракт: Native шлёт именно `elapsedRealtime`**); статус-строка «в пути/на паузе».
- **Прогрев батареи**: state-machine индикатора по магическим кодам `bms==8` (fault), `bms==9`/`status==1` (греется), `fail 1..4` (невозможен), `temp < threshold` (холодно) → 5 цветов пилюли (`MainActivity.java:206-230`). Запуск — broadcast `BATTERY_HEAT_ACTIVATE`.
- **Тоггл-карточки** автосвет/пешеход/ForcedEV: prefs + немедленный `MSG_*`; входящая `SETTING_SYNCED` (signature) синхронизирует с рулём.
- **Плитки**: сплиты (только `IS_FULL` и `ready()`) + ярлыки приложений (full→VD, light→обычный лаунч); grid 5 колонок якорями `Space`.
- **Отступы**: родной док-head unit **145dp** + fallback `status_bar_height` через `getIdentifier` (`MainActivity.java:501-512`).

### 5.2 AdvanceActivity

- **Навигация**: 7 секций visibility-свичем, `SECTION_TITLES`; light-флейвор прячет сплиты и руль (`:495-498`); «Собственные команды» скрыты до `showCustomCommands`.
- **CAN-редактор**: фильтр `[^0-9a-f,\n]`, группировка по 2 hex-символа + пробел, перенос после 20-го символа; валидация `formatted.length() % 31 == 0` (10 байт = 20 hex + 10 пробелов + `\n`) → включает кнопку «назад» (`:406-461`). Логирует **каждое нажатие клавиши с содержимым**.
- **Применить**: сохранение команд → `MSG_APPLY_DRIVE_MODES` → `applyClient` ждёт `MSG_RESULT`, страховка 12 с; прогресс скрыт в секции Apollo (`:674-682`).
- **Apollo Tech**: чисто read-only модель, guard-функции (`canChangeApolloTlc` требует P-передачу, `gear==0`, PLC 1/2 и т.д.), словарь ~60 fail-closed кодов → человекочитаемые сообщения (`formatApolloError`). **`canChangeApolloMaster()` всегда `false` (`:1409-1411`)** — мастер-тумблер и диалог включения недостижимы; `buttonApolloForceOff` **всегда `GONE`** в `updateApolloUi` (`:1512-1515`), хотя `canForceApolloMasterOff()` реализован. `apolloPending` **не имеет локального таймаута** — если потерян broadcast, тумблеры заблокированы до следующего `APOLLO_TLC_UPDATE`.
- **Пароль инженерного меню**: посимвольное сложение ГГГГ+ММДД без переноса, дата — **Пекинская** (`:726-762`).
- **Док/руль**: пикеры → prefs → `SplitConfigSync` при каждом изменении и при входе в секцию.

### 5.3 TripHistoryActivity

Снимок `tripsJson` из интента MainActivity + живые обновления по `TRIP_UPDATE`; рендер JSON→`item_trip` с удалением по `start`-ключу через `TRIP_DELETE`; парсинг в try/catch, пустое состояние — программный TextView.

### 5.4 NowPlayingClient

Полноценный pull/push клиент (provider + art InputStream + `requestRefresh`) с аккуратными helpers по именам колонок — **мёртвый код в текущем проекте** (ни одного вызова вне самого файла).

---

## 6. Риски

### Критические / высокие

1. **Экспорт провайдера без permission** (`AndroidManifest.xml:51-55`):
   - `query` отдаёт **любому** приложению `customCommand` (сырые CAN-фреймы) и все настройки — информационная утечка.
   - `update` позволяет **любому** приложению писать `forcedEv`, `driveMode`, `energy`, `recycle`, `disablePedestrianSound` (`RestoreModeContentProvider.java:137-157`) — если Native доверяет этим колонкам при пробуждении, стороннее приложение может **изменить поведение автомобиля**. Нужен `readPermission`/`writePermission` уровня signature (тот же `BIND_SET_MODES_SERVICE`), либо `exported=false` + наследование authority-доступа иным способом.
2. **Незащищённые входящие broadcast'ы**: `TRIP_UPDATE`, `BATTERY_HEAT_UPDATE`, `MODE_SYNCED`, `LUX_UPDATE`, `LOG_UPDATE` регистрируются `RECEIVER_EXPORTED` **без permission** (`MainActivity.java:647-655`, `AdvanceActivity.java:2091-2098`, `LoggingActivity.java:120`). Спук может: подделать показание температуры/статус прогрева (**driver-facing safety UI**), сбить radio-режимы, внедрить текст в экран логов, подделать историю. Паттерн signature-permission **уже применён** для `SETTING_SYNCED`/`APOLLO_TLC_UPDATE` — нужно распространить на остальные.
3. **Устаревший дефолтный `layout/activity_main.xml`** (портретная версия): нет `mainContent` → NPE в insets-listener; кнопка ссылается на **несуществующий** `onButtonClickApply` в MainActivity → краш по клику; старый UI режимов, уже переехавший в Advance. Маскируется только `SCREEN_ORIENTATION_LANDSCAPE` + `layout-land` — хрупкая защита (краш, если inflate приходит в портретной ориентации до forced-rotate, `setContentView` строка 496 вызывается **до** `setRequestedOrientation` на 497).
4. **`onActivityResult`: `data.toString()` без null-проверки** (`MainActivity.java:297`) — NPE при `RESULT_OK` с пустым `data`.
5. **`AdvanceActivityStarButton` NPE после process death**: `GlobalVars.sharedPreferences`/`editor` инициализируются только в `MainActivity.onCreate` (`:515,534`); активность обращается к ним напрямую (`AdvanceActivityStarButton.java:41-43,91-93`). Вдобавок вся активность **недостижима**: `exported=false`, и ни один layout не ссылается на `onButtonClickAdvanceStarButton`; баг `initIntentStarButton` кладёт extras **не в тот Intent** (`MainActivity.java:554-555`), а `getModes()` вообще не читает колонки 14–15 (`customCommandStarButton*`) — мёртвая/сломанная ветка целиком.
6. **`registerReceiver(..., RECEIVER_EXPORTED)` — API-оверлоуд/флаги добавлены в API 33**, а `minSdk = 30`. На устройстве с Android 11/12 (API 30–32) — `NoSuchMethodError` в `MainActivity.onResume`. **Проверить фактический API головы**; если < 33 — нужен compat-регистратор. Lint (NewApi), судя по всему, в CI не гоняется.

### Средние

7. **Гонки SplitStore**: каждый UI-колбэк делает `load → mutate → save → render`, параллельно `SplitRatioSaveReceiver` может сохранить `split` — **lost update**; `presetIndex()` ищет пресет по значению (l/r/ratio) — при дублях вернёт первый попавшийся (`MainActivity.java:827-834`). Нужны id-адресация и единый synchronized-сейв.
8. **Индексные ссылки** (`dockOverrideNSplit`, `split:N`) на пресеты — см. §4.
9. **Магическая валидация `% 31`** принимает кратные 31 (теоретически 62 = две команды без `\n`); в StarButton два редактора **делят одни и те же Save/Back-кнопки** — состояние определяется последним отредактированным полем, можно сохранить с невалидной второй командой.
10. **Дублирование `MSG_*`-кодов** между MainActivity/Advance/StarButton — рассинхрон с Native при эволюции протокола (коды сейчас совпадают).
11. **`apolloPending` без таймаута** (в отличие от `applyTimeout` у Apply) — возможная перманентная блокировка тумблеров Apollo.
12. **`getModes()`**: позиционный доступ `cursor.getString(0..4)` (хрупко при реордеринге колонок), нет guard на `cursor==null`; прочитанные `driveMode/energy/recycle` в MainActivity **далее не используются** (мёртвое чтение).
13. **Hardcoded**: `145f` dp док ×3 (`MainActivity:502`, `AdvanceActivity:636`, `TripHistoryActivity:64`); `"ru.big.town.anative"` литералами во множестве мест; CAN-кадр по умолчанию прямо в layout (`activity_advance.xml:633`); магические `bms==8/9`, `fail 1..4`; `1920dp×720dp` в `activity_advance_start_button.xml`; `textSize` **px** во всех layout (не dp/sp — «зашито» под head unit).
14. **Backup**: `allowBackup="true"`, а `backup_rules.xml`/`data_extraction_rules.xml` — **шаблонные TODO** — судьба `DrivePreferences` (включая CAN-команды) при backup/transfer не определена осознанно.
15. **Permissions**: `QUERY_ALL_PACKAGES` (оправдан комментарием, но широкий), `WRITE_EXTERNAL_STORAGE` при `minSdk=30` — скорее всего, мёртв/невалиден для scoped storage; `<queries>` содержит **self-package** `ru.big.town.restoremode` — бесполезен.
16. **Логирование**: verbose-логи TextWatcher'ов пишут содержимое CAN-редактора в logcat (`AdvanceActivity.java:411-458`); `e.printStackTrace()` вместо единого `Log`-тега.
17. **LoggingActivity** не опрашивает у Native фактическое состояние `loggingEnabled` — только локальный pref (расхождение возможно после сброса/восстановления).
18. **Сборка**: `release` подписан **debug-ключом**, minify **включён в debug и выключен в release** (инверсия), `versionCode=1`, `multiDexEnabled` не нужен на API 30+, `ndkVersion` без нативного кода, `viewBinding=true`, но везде `findViewById`, `dependenciesInfo.includeInApk=true` — шум.

### Низкие

19. `RestoreModeContentProvider`: поля-состояние вместо локальных переменных; `getType()` возвращает `vnd.android.cursor.dir/users` (чужой MIME); `insert/delete` — TODO-заглушки.
20. `GlobalVars.connectedClients` — refcount при единственном `ServiceConnection` never > 1; чтение `isBound/serviceMessenger` не синхронизировано с записи (пишется из main thread — приемлемо, но хрупко).
21. `IncomingHandler` — только логирование (фактически мёртвый).
22. Пароль инженерного меню показывается в UI (по дизайну, но чувствительно).
23. `TripHistoryActivity SimpleDateFormat` — не потокобезопасен (UI-only, ок).
24. `roundIcon` указывает на `@mipmap/ic_restoremode` вместо `ic_restoremode_round`.

---

## 7. Возможности улучшения

### Мёртвый код / ресурсы
| Объект | Признак |
|---|---|
| `NowPlayingClient.java` | 0 вызовов вне файла |
| `AdvanceActivityStarButton` + `activity_advance_start_button.xml` + `initIntentStarButton`/`onButtonClickAdvanceStarButton` | недостижимы из UI, exported=false; поля `serviceMessenger`/`isBound`, пустой `onDestroy` — мусор |
| `layout/activity_main.xml` (портретный) | устаревший экран, битые `onClick`/ID — удалить или синхронизировать с `layout-land` |
| `strings.activity_advance_off_text` | не используется |
| Layouts `item_dock_app`, `item_dock_split`, `spinner_item`, `spinner_dropdown_item`; `xml/restore_mode.xml`; `drawable-nodpi/leave_car.png` | нет ссылок (кандидаты на удаление; проверить также `ic_dock_*`, `dock_item_bg`, `fragment_setting_module_bg`, `simple_button`, `card_blue_ripple`, PNG `radio_button_height_70_*`) |
| `MainActivity.MSG_APPLY_DRIVE_MODES`, поля `driveMode/energy/recycle` после `getModes`, `StarButton*`, `IncomingHandler` | не используются/мёртвые |
| `showApolloMasterEnableDialog` + ветка включения master + `buttonApolloForceOff` | недостижимы при `canChangeApolloMaster()==false` и всегдашнем `GONE` — либо включить, либо вычистить |
| `proguard-rules.pro` | почти пустой при minify в debug |
| `<queries>` self-package | бесполезен |

### Дублирование → рефакторинг
1. **CAN-форматтер/валидатор** продублирован **3 раза** (Advance ×1, StarButton ×2, ~50 строк каждый) → вынести `CanCommandFormatter.format/isValid` + unit-тесты.
2. **`applyWindowInsets` (145dp + status_bar_height)** ×3 → один helper/base-класс `DockInsetActivity`.
3. **`MSG_*` / action-строки** → единый `NativeProtocol` (константы + `NATIVE_PKG`).
4. **Перечисление лаунчер-приложений** дублируется в `showAppPicker` и `initAppDpiList` → общий `InstalledAppsProvider`.
5. **Snackbar/dialog-хелперы** (`MaterialAlertDialogBuilder(this, R.style.DarkDialog)`) ×10+ → `Ui.dialog(...)`/`Ui.snack(...)`.
6. **Строка «Сервис не готов» + guard `GlobalVars.isBound`** → `MessengerSender.trySend(what, arg, data)`.
7. **Индексные ссылки на пресеты** → хранить только `presetId` (SplitStore уже имеет UUID), резолвить через `findById`.
8. **Race SplitStore** → single writer: in-memory кэш списка + `synchronized` save, либо `SharedPreferences.OnSharedPreferenceChangeListener` для ререндера вместо pull-мутаций по индексам.
9. **Провайдер**: заменить позиционные индексы на `getColumnIndex` (пусть даже внутри MainActivity), permission на authority, `getType` → корректный MIME, локальные переменные вместо полей.
10. **Экспорт receivers** → signature-permission везде, где приходит данные от Native (консистентно с `SETTING_SYNCED`).
11. **viewBinding** уже включён — перевести `findViewById`-цепочки (особенно `AdvanceActivity`).
12. **Тесты**: покрыть `SplitStore` (миграция id, `leftFraction`), `AppDpiStore`, `SplitConfigSync.resolveSteerAction`, `engineeringPassword`, CAN-валидатор — сейчас 3 의미ных assert'а на весь проект.
13. **Сборка**: release → свой keystore + minify/R8 + нормальный `versionCode`-автобамп; debug — без minify; убрать `ndkVersion`/`multiDexEnabled`; включить `lint` (NewApi поймает риск §6.6).
14. **i18n**: русские строки захардкожены в Java/XML (включая диалоги безопасности) — вынести в `strings.xml`.
15. **Логи**: убрать логирование тела CAN-ввода, заменить `printStackTrace` на `Log.w(TAG, msg, e)`.
16. **Backup rules**: явно `exclude` (или `include`) для `DrivePreferences`.
17. **LoggingActivity**: добавить pull текущего состояния `loggingEnabled` у Native (аналог `REQUEST_*`-паттерна).
18. **`apolloPending`**: локальный таймаут (как `applyTimeout`), сбрасывающий guard-состояние.

---

### Итог

Архитектура «тонкий UI + привилегированный Native» реализована последовательно: signature-permission для критичных каналов (`SETTING_SYNCED`, `APOLLO_*`, `SPLIT_RATIO_SAVE`), стабильные UUID у сплитов с миграцией, явный компонентный адрес config-ов, аккуратный Messenger-ретрай и guard-машина Apollo. Основные долги — **безопасность экспорта** (провайдер без permission + 5 незащищённых exported-receivers, включая safety-виджет прогрева), **наследие** (портретный layout, недостижимый StarButton-экран, мёртвый `NowPlayingClient`), **дублирование** (CAN-форматтер ×3, insets ×3, MSG-коды ×3) и **index-based адресация пресетов** с соответствующими гонками load/save.
