# Code Review — privileged Android-приложение `ru.big.town.anative` (VoyahTune / Native)

> Объём: 33 Java-файла (`ru.big.town.anative`), `AndroidManifest.xml`, `app/build.gradle.kts`, `cpp/native-lib.cpp`, `CMakeLists.txt`, `readme.md`. Только исследование; файлы не изменялись.
> Коммит: `45beee4` (master). Дата аудита: 2026-09-23.

---

## 1. Архитектура

**Назначение.** Privileged-системное приложение для Voyah Free: прямая запись CAN-команд (drive modes, свет, дворники, TCL/PLC Apollo, медиа-клавиши, режимы парковки/гаража) минус OEM-обработчики. Плюс: док-лаунчер, split-host, now-playing провайдер, trip-stats, back-button overlay, battery-heat, light-sensor, wiper-cold.

**Слои (top → bottom):**

| Слой | Классы | Роль |
|------|--------|------|
| UI | `MainActivity`, `SplitHostActivity`, dock-раскладки | настройки режимов, док, split |
| Foreground-сервисы | `SetModesService` (1060), `ApolloTlcService` (1692), `NowPlayingService`, `TripStatsService`, `LightSensorService`, `WiperColdService`, `BatteryHeatService`, `BackButtonService` | постоянный runtime, foreground notification |
| Broadcast | `SetModesReceiverStatic` → `SetModesReceiverDynamic` → `SetModesConfigReceiver` | boot / SWC / garage / screen / signature-config |
| Policy (JVM, без Android) | `ModeSyncPolicy`, `MediaControlPolicy`, `MediaKeyPairDelivery`, `DoorPauseRunState`/`Timeline`, `SteeringActionPolicy`, `DriveModeCanPolicy`, `HeadlightCanPolicy`, `ApolloTlcPolicy` | чистые функции, юнит-тестируемые |
| Transport | `DriveModeCanTransport` (TX=77), `HeadlightCanTransport` (TX=58) | построение 10-байт фреймов |
| Coalescing / engine | `ApplyEngine` (808) — последовательный worker, epochs/coverage | защита от sleep/reset mid-batch |
| CAN I/O | `CanSender` → `MainActivity.cis_can_control_bytes` (JNI) → `native-lib.cpp` → `libqg_hal` / OEM Binder `ICanBusService` (TX=77/58), `ICarSignalService` (TX=46/36/28) | единственная точка записи в шину |
| Хранилище | ContentProvider `ru.big.town.restoremode` (modes), `Settings.Global` (Apollo master, dock guard), `SharedPreferences NativePrefs` (кэш), `/private/configs/token/accountInfo` (OEM individual profile) | persistent state |

**Сборка.** Флейворы `full` (`IS_FULL=true`) / `light`; оба `HAS_DIRECT_APOLLO=true`; `minSdk 30`, `targetSdk 35`, NDK 27, CMake 3.22.1; `viewBinding`, `buildConfig`; `minify=false` (release). AIDL нет — Binder по reflection/hardcoded interface descriptor.

**Сильные стороны архитектуры:**
- Policy-классы изолированы от Android → 82+ юнит-тестов (`ModeSyncPolicy` settle/cooldown, `MediaKeyPairDelivery` NOT_SENT/DOWN_ONLY/COMPLETE, `DriveModeCanPolicy` individual profile 2|3 / 1..3 и т.д.).
- Единая точка отправки CAN (`CanSender`) с `NATIVE_SEND_LOCK`, ThreadLocal `SEND_GUARD`, debug-эмуляцией и валидацией длины кадра (ровно 10).
- `ApplyEngine` сериализует multi-frame батчи, epoch/coverage защищают от триггеров «спящего» wake на следующем цикле.
- Fail-closed паттерн в `ApolloTlcService`: schema mismatch / нет write-права → `writeMaster(false)`, `forceMasterOff`, отказ от bind.

---

## 2. CAN-шина

**Два независимых канала записи:**

1. **Прямой JNI (libqg_hal)** — `native-lib.cpp`:
   - `cis_can_control_bytes(cmdNum, frame[10])` → `gCanTransactionMutex` (process-global) → `dlopen("libqg_hal.so")` → ioctl/транзакция.
   - Кадр фиксированно 10 байт; валидация в `CanSender` до JNI.
   - `dlopen` при первом вызове; ошибка загрузки возвращает ≠0 → `CanSender.send=false` → restore не считается успешным (намеренно).

2. **OEM CanBus Binder** (`com.qinggan.canbus.ICanBusService`):
   - `ApolloTlcService` bind/unbind, generation-контроль (`canBusVerificationGeneration`), watchdog на `onServiceConnected` (`BIND_CONNECT_TIMEOUT_MS`), экспоненциальный rebind (`BIND_RETRY_MS << min(attempt,4)`, cap 5, `BIND_RETRY_MAX_MS`).
   - **Schema verification**: `PathClassLoader` по `info.sourceDir` CanBus APK → `Class.forName("com.qinggan.canbus.VehicleState")` → сверка `getValue()` стабильных id и `ordinal()` для каждого `Signal`/`Entitlement` из `ApolloTlcPolicy`. Mismatch → `rejectCanBusVerification` → master off, snapshot invalidate.
   - Write-право: `checkSelfPermission(WRITE_CANBUS_PERMISSION)` (signature-level, объявлено в манифесте). Без него `isBinderProfilePinned=false`, bind не выполняется.
   - TX-коды: 77 (drive modes / VehicleState 545/722/774/782), 58 (headlight), 46/36/28 (ICarSignalService — Apollo TLC query/set, coalesced queries, метрики по `TRACKED_TRANSACTIONS`).

**Политики (что именно уходит в шину):**
- `DriveModeCanPolicy`: IndividualProfile — steering bits 2|3, accelerator 1..3; `HeadlightCanPolicy`: LOW_BEAM=215, OUT_LAMP_OFF=1096, AUTO_LAMP_SWITCH=1097.
- `ApolloTlcPolicy`: PLC/TLC/ANP/GLA/TSR switch/status, gear, capability SA; fail-closed — при любом сбое верификации `writeMaster(false)`.
- `MediaControlPolicy` / `MediaKeyPairDelivery`: keys 85/87/88/127, sticky token, DOWN→UP pair (Outcome COMPLETE только при обоих кадрах).

**Гарантии конкурентности `CanSender`:**
- `NATIVE_SEND_LOCK` — один process-global descriptor libqg_hal; batch-метод `send(byte[][])` держит монитор reentrantly, чтобы свет/дворники не вклинились между кадрами drive-mode.
- Double-check `sendAllowed()` после ожидания lock (машина могла уснуть).
- ThreadLocal `SEND_GUARD` — cooperative cancellation между ioctl; исключение в guard → кадр подавлен (`fail-closed`).
- `debugMode` volatile — эмуляция в логи вместо шины.

**Чтение CAN/сигналов:** в основном через ICarSignalService query (coalescing + метрики avg/max µs) и OEM settings provider; прямого чтения ioctl из JNI в review не обнаружено (запись-доминантное приложение).

---

## 3. Таблица файлов

| Файл | Строк | Слой | Роль | Оценка |
|------|------:|------|------|--------|
| `ApolloTlcService.java` | 1692 | service | Bind CanBus, schema verify, TLC write, master Settings.Global | ★★★★★ критичен |
| `SetModesService.java` | 1060 | service | Foreground, CarPower, receivers, Messenger IPC, ApplyEngine host | ★★★★★ критичен |
| `SplitHostActivity.java` | 964 | UI | Split-host, dock guard | ★★★★ |
| `LightSensorService.java` | 809 | service | Auto-light, CAN headlight | ★★★★ |
| `ApplyEngine.java` | 808 | engine | Последовательный apply, epochs/coverage/wake | ★★★★★ критичен |
| `MainActivity.java` | 707 | UI/core | Modes provider, JNI entry, setCanValues, toggles | ★★★★★ критичен |
| `TripStatsService.java` | 627 | service | Trip-метрики | ★★★ |
| `WiperColdService.java` | 676 | service | Дворники/холод, CAN wiper | ★★★★ |
| `BackButtonService.java` | ~500 | overlay | Кнопка «назад» overlay | ★★★ |
| `BatteryHeatService.java` | ~400 | service | Батарея/нагрев | ★★★ |
| `NowPlayingService.java` | ~400 | service | MediaSession → provider | ★★★★ |
| `CanSender.java` | 140 | CAN | Единая точка send, locks/guards/debug | ★★★★★ критичен |
| `DriveModeCanTransport.java` | ~300 | CAN | Фреймы TX=77 | ★★★★★ критичен |
| `HeadlightCanTransport.java` | ~250 | CAN | Фреймы TX=58 | ★★★★★ критичен |
| `DriveModeCanPolicy.java` | ~200 | policy | Individual profile steering/accel | ★★★★★ |
| `HeadlightCanPolicy.java` | full | policy | LOW_BEAM/AUTO/OFF id | ★★★★★ |
| `ApolloTlcPolicy.java` | full | policy | Signal/Entitlement ids, verificationResultCurrent, binderProfilePinned | ★★★★★ |
| `ModeSyncPolicy.java` | ~250 | policy | POST_RESTORE_SETTLE=20s, COOLDOWN=3s, MAX_CORRECTIONS=1, ACCEPT/IGNORE/CORRECT | ★★★★★ |
| `MediaControlPolicy.java` | ~300 | policy | Routes, sticky tokens | ★★★★ |
| `MediaControlRouter.java` | ~350 | policy | ROUTE_DIRECT/KEYMANAGER/NATIVE/NOOP | ★★★★★ |
| `MediaKeyPairDelivery.java` | full | policy | NOT_SENT/DOWN_ONLY/COMPLETE | ★★★★★ |
| `DoorPauseRunState.java` | full | policy | Pause/run state machine | ★★★★ |
| `DoorPauseTimeline.java` | full | policy | Timeline of door events | ★★★★ |
| `SteeringActionPolicy.java` | full | policy | nextMode steering wheel | ★★★★ |
| `DockLaunchGuard.java` | full | util | Settings.Global `voyahtune_dockLaunchGuard<id>`, HOLD=5s, display 0/1 | ★★★★ |
| `NowPlayingProvider.java` | ~300 | IPC | ContentProvider, METHOD_MEDIA_COMMAND, exported=true | ★★★★ риск |
| `OemIndividualDriveProfileReader.java` | ~250 | data | `content://qinggan.settings/global` + accountInfo | ★★★★ |
| `SetModesReceiverStatic.java` | full | boot | BOOT_COMPLETED + QINGGAN_BOOT_COMPLETE | ★★★★★ |
| `SetModesReceiverDynamic.java` | ~200 | receiver | SWC, GARAGE_OFF, SCREEN_ON/OFF, RECEIVER_EXPORTED | ★★★★ |
| `SetModesConfigReceiver.java` | ~150 | receiver | signature SetModesConfig, STEER/DOCK_CONFIG | ★★★★★ |
| `NativeLog.java` | ~200 | util | logcat --pid → `/sdcard/tmp/voyah_native_log.txt`, ring 600 | ★★★ |
| `GlobalVars.java` | small | core | SAVE_CONTEXT, CarPowerManager, buttonDriveMode | ★★★★★ |
| `Anative.java` | small | app | Application class | ★★★ |
| `AndroidManifest.xml` | full | config | компоненты/permissions/exported | ★★★★★ |
| `app/build.gradle.kts` | full | build | full/light, BuildConfig, NDK | ★★★★ |
| `cpp/native-lib.cpp` | full | JNI | libqg_hal, mutex, 10-byte frame | ★★★★★ критичен |
| `cpp/CMakeLists.txt` | 37 | build | anative SHARED, android+log | ★★ |

---

## 4. Критические функции

| Функция | Файл:строка | Почему критична |
|---------|-------------|-----------------|
| `CanSender.send(int, byte[], String)` | `CanSender.java:51` | Единственный вход в шину. Валидация 10 байт, `NATIVE_SEND_LOCK`, double-check guard, debug-эмуляция. Возврат `false` ≠ успех — callers обязаны проверять. |
| `CanSender.send(int, byte[][], String)` | `CanSender.java:80` | Атомарность multi-frame drive-mode (2–3 кадра) без вклинивания света/дворников. |
| `CanSender.runGuardedSend/Action` | `CanSender.java:94,105` | ThreadLocal- guard для sleep/reset cancellation между ioctl. |
| `MainActivity.cis_can_control_bytes` | JNI → `native-lib.cpp` | `gCanTransactionMutex`, `dlopen libqg_hal`; ошибка ≠0 валит restore. |
| `native-lib.cpp` frame send | `native-lib.cpp` | Process-global descriptor; mutex — единственная синхронизация на native. |
| `ApplyEngine.scheduleApply` / worker | `ApplyEngine.java` | Последовательный apply; `completeCycle`/`coverage` с epoch — не дать триггеру прошлого wake выполниться в новом. |
| `SetModesService.onStartCommand` + `worker()` | `SetModesService.java:964,1047` | START_STICKY, Messenger IPC; star-button пресеты идут в тот же ApplyEngine worker. |
| `SetModesReceiverStatic.onReceive` | `SetModesReceiverStatic` | Boot-триггер → foreground service; главный точка входа после включения. |
| `ApolloTlcService.resolveVehicleStateSchema` | `ApolloTlcService.java:1462` | Reflection schema verify; любой mismatch → fail-closed (master off). |
| `ApolloTlcService.ensureCanBusBound` + rebind/watchdog | `ApolloTlcService.java:1304,1333,1345` | Жизненный цикл OEM Binder; generation + epoch против гонок bind/unbind. |
| `ApolloTlcService.writeMaster/forceMasterOff` | `ApolloTlcPolicy` + service | Settings.Global master = «аварийный рубильник» TLC. |
| `DriveModeCanPolicy.resolveIndividualProfile` | `DriveModeCanPolicy` | steering 2\|3, accelerator 1..3 — содержимое TX=77. |
| `HeadlightCanPolicy` ids | `HeadlightCanPolicy` | 215 / 1096 / 1097 — неверный id = фары не работают или всегда on. |
| `ModeSyncPolicy.decide` | `ModeSyncPolicy` | ACCEPT/IGNORE/CORRECT с settle 20s / cooldown 3s / max 1 correction — защита от флэппинга. |
| `MediaControlRouter.route/routeWithSticky` | `MediaControlRouter` | Единый роут медиа-команд; sticky pinning предотвращает рассинхрон DOWN/UP. |
| `MediaKeyPairDelivery.deliver` | `MediaKeyPairDelivery` | Outcome COMPLETE только при успешной паре DOWN+UP; DOWN_ONLY = застрявшая клавиша. |
| `DockLaunchGuard.acquire` | `DockLaunchGuard` | Settings.Global guard 5s — предотвращает re-launch dock во время перехода. |
| `NowPlayingProvider.call(METHOD_MEDIA_COMMAND)` | `NowPlayingProvider` | Экспортированный IPC с приватным ключом `com.qinggan.keymanager.service` — единственная защита. |
| `MainActivity.setCanValues` / `loadModes` | `MainActivity` | Применение сохранённых режимов из provider → CAN. |
| `SteeringActionPolicy.nextMode` | `SteeringActionPolicy` | Циклическое переключение пресетов по кнопке руля. |

---

## 5. Риски

### Critical

| # | Риск | Детали |
|---|------|--------|
| C1 | **Прямая запись CAN без защиты** | Приложение — privileged, пишет произвольные 10-байт фреймы (TX 77/58/46/36/28). Ошибка в `DriveModeCanPolicy`/`HeadlightCanPolicy`/транспортных классах = реальное воздействие на рулёжку/свет во время движения. `readme.md` прямо предупреждает. |
| C2 | **`NowPlayingProvider` exported=true** | Любое приложение может `call()` с `METHOD_MEDIA_COMMAND`, если знает/подберёт ключ `com.qinggan.keymanager.service`. Ключ в коде (не в keystore) → скомпрометирован = управление медиа/потенциально форвардинг команд. |
| C3 | **Reflection schema verify single point of failure** | `PathClassLoader` + `Class.forName` по CanBus APK. Если OEM изменит `VehicleState` (id/ordinal) → полный отказ TLC (fail-closed — безопасно, но функциональная недоступность). Если APK отсутствует/повреждён — та же ветка. |
| C4 | **`debugMode` глобально выключает шину** | `volatile debugMode` из SharedPreferences provider. Если cache/флаг застрянет true на боевом auto → restore «успех» без реальной отправки (`send=true` при эмуляции). Обратный риск: false на устройстве без libqg_hal → постоянные `res≠0`. |
| C5 | **Boot → foreground → CAN без user-consent** | `SetModesReceiverStatic` на BOOT_COMPLETED + `QINGGAN_BOOT_COMPLETE` сразу поднимает сервис, который применяет modes в CAN. Нет видимого «safe mode» / выключающего жеста при первом старте (кроме debugMode). |

### High

| # | Риск | Детали |
|---|------|--------|
| H1 | **Два канала CAN одновременно** | JNI libqg_hal и OEM Binder пишут независимо; синхронизация только внутри каждого канала (`gCanTransactionMutex` / CanBus binder call). Межканальные гонки (drive-mode JNI + TLC Binder) не сериализуются общим lock'ом. |
| H2 | **`SetModesReceiverDynamic` RECEIVER_EXPORTED** | Принимает SWC/GARAGE/SCREEN от любых отправителей (action-based, не permission-based в dynamic register). Эмуляция `KEYCODE_SWC_USER_DEFINE` возможна side-loaded приложением → форс star-button пресета. |
| H3 | **Settings.Global как канал управления** | `GLOBAL_MASTER_KEY`, `voyahtune_dockLaunchGuard<displayId>` — глобальные системные ключи. Другие privileged-приложения могут читать/писать → влияние на TLC/dock. |
| H4 | **Messenger IPC `SetModesService.onBind`** | `onBind` возвращает Messenger без проверки calling-uid/permission в видимом коде `IncomingHandler`. Если манифест не ограничивает bind — любой клиент может слать `MSG_*`. |
| H5 | **release `minify=false`** | Все имена/policy id/строки ключи (`com.qinggan.keymanager.service`) открыты в dex → упрощает reverse-engineering и подбор IPC-ключей. |
| H6 | **OEM profile через `PathClassLoader`/settings** | `OemIndividualDriveProfileReader` + schema load из внешнего APK sourceDir — доверие к целостности OEM-кода на диске; root/мануальное замещение APK меняет политику CAN. |

### Medium

| # | Риск | Детали |
|---|------|--------|
| M1 | **AIDL отсутствует, Binder по hardcoded names** | `ICanBusService`/`ICarSignalService` — тонкая связь с конкретной прошивкой; любой rename = runtime failure (обрабатывается, но без compile-time проверки). |
| M2 | **`NativeLog` пишет в `/sdcard/tmp`** | Мировочитаемый storage; CAN-метки/labels в логе = информационная утечка (VIN-контекст, режимы). Нет `isExternalStorageManager`-проверки в видимом коде — падение на новых scoped-storage OEM. |
| M3 | **START_STICKY + многократный boot-путь** | Возможны дубли сервисов при `QINGGAN_BOOT_COMPLETE` + `BOOT_COMPLETED`; код partly-guards (`already initialized`), но race при быстром двойном старте. |
| M4 | **`GlobalVars` static mutable** | `SAVE_CONTEXT`, `mCarPowerManager`, `buttonDriveMode` — static state без синхронизации; нижний слой (CanSender/ApplyEngine) зависит от порядка init. |
| M5 | **Экспоненциальный rebind cap=5** | После 5 неудач rebind перестаёт (`rebindAttempt` не сбрасывается на успехе в видимом коде?) — долгосрочная потеря OEM-канала без restart. |
| M6 | **Кольцевой лог 600 строк** | Для диагностики CAN-инцидентов мало; нет кольца большего размера / crash-safe flush. |

### Low

| # | Риск | Детали |
|---|------|--------|
| L1 | `minify=false` + отсутствие R8 shrink | Размер APK, строковые константы открыты. |
| L2 | Legacy `LocalBroadcastManager` в закомментированном коде | Мёртвый код в `MainActivity.onButtonClick` / `SetModesService`. |
| L3 | Нет ProGuard-keep для reflection-имён | Если включат minify — `VehicleState`/binder методы сломаются первыми. |
| L4 | `catch (Throwable)` в `sendAllowed` | Широкий, но оправдан fail-closed. |
| L5 | Hardcoded package names (`qinggan.*`, `ru.big.town.restoremode`) | Нет feature-flag/di на других OEM. |

---

## 6. Улучшения

**Безопасность**
1. `NowPlayingProvider`: убрать `exported=true` для незнакомых callers — проверять `Binder.getCallingUid()` / signature-permission вместо строкового ключа; или `exported=false` + `grantUriPermissions`.
2. Добавить signature-permission на `SetModesService` bind и проверку `getCallingUid()` в `IncomingHandler` (H4).
3. Dynamic receiver `SetModesReceiverDynamic`: либо permission-gate (`RECEIVER_EXPORTED` + неявный permission), либо фильтровать по `getSendingUid()` для SWC.
4. Включить R8 `minify`+`shrinkResources` с явными keep-правилами для reflection (`VehicleState`, JNI-методов, Binder транзакций).
5. `NativeLog` → app-private storage (`context.getExternalFilesDir` или internal) + rotation size; убрать `/sdcard/tmp`.

**CAN / надёжность**
6. Единый process-wide «CAN epoch/fence», общий для JNI и OEM Binder каналов (H1) — например, общий `ReentrantLock` или очередь кадров через единственный writer-thread.
7. Сбрасывать `rebindAttempt` при успешном `onServiceConnected` (M5); добавить метрику/уведомление при исчерпании 5 попыток.
8. Явно логировать и persist-флаг «restore successful only if real send» — убедиться, что boot-apply не может засчитаться при `debugMode=true` на боевом устройстве (C4): например, запретить `debugMode` в `full` flavor или warning-notification.
9. Двойной boot-guard (idempotent startId) против гонки `BOOT_COMPLETED`+`QINGGAN_BOOT_COMPLETE` (M3).

**Тесты / наблюдаемость**
10. Юнит-тесты уже есть для policy — расширить на `ApplyEngine` coverage/epoch (сейчас покрытие в основном через интеграционные сценарии) и на `MediaControlRouter` sticky-переходы.
11. Интеграционный smoke «CAN send failure → restore not success» (уже заложено в `CanSender` return-контракте) — закрепить тестом.
12. Продолжить вынос оставшихся Android-зависимостей из транспортов (`DriveModeCanTransport`, `HeadlightCanTransport`) в чистые policy, чтобы покрыть 100% frame-layout тестами (как `HeadlightCanPolicy`).

**Код / долг**
13. Удалить закомментированный legacy-код (`LocalBroadcastManager`, APPLY_DRIVE_MODES branches).
14. Заменить `GlobalVars` static на DI-контейнер или init-object с явным lifecycle.
15. Зафиксировать hardcoded OEM package/action names в единый `OemContracts` класс (C3/H6 облегчает диагностику смены прошивки).

---

## 7. Потоки данных

```
[BOOT]
QINGGAN_BOOT_COMPLETE / BOOT_COMPLETED
  → SetModesReceiverStatic.onReceive
  → SetModesService.startForegroundService (notification channel "Screen Monitor")
      ├─ register SetModesReceiverDynamic (SWC, GARAGE_OFF, SCREEN_ON/OFF)
      ├─ CarPowerManager listener (GlobalVars.mCarPowerManager)
      └─ ApplyEngine (sequential worker, epochs/coverage)
  → signature SetModesConfigReceiver (STEER_CONFIG / DOCK_CONFIG)

[APPLY MODES]  (SWC star / button / boot / screen)
ApplyEngine.scheduleApply
  → worker: loadModes(GlobalVars.SAVE_CONTEXT)  ← ContentProvider ru.big.town.restoremode
  → MainActivity.setCanValues(n, frames, label)
  → CanSender.send(cmd, frames, label)
       ├─ debugMode?  → Log "EMULATE CAN" (return true)
       └─ else synchronized(NATIVE_SEND_LOCK):
            MainActivity.cis_can_control_bytes (JNI)
              → native-lib.cpp gCanTransactionMutex
              → dlopen(libqg_hal) → ioctl TX=77/58/...
  → ApplyEngine.completeCycle(success?) → ModeSyncPolicy.decide (settle/cooldown) → UI sync

[Apollo TLC]
ApolloTlcService.onStartCommand / schemaExecutor
  → resolveVehicleStateSchema (PathClassLoader + Class.forName VehicleState)
  → matches? bind CanBus (TX=46/36/28 ICarSignalService) : writeMaster(false)
  → runtime write: ICanBusService binder TX=77/58 (drive/light subsets)
  → publishState → sendBroadcast ACTION_APOLLO_TLC_UPDATE (BIND_PERMISSION) → restoremode UI

[MEDIA]
MediaSessionManager / NowPlayingService
  → NowPlayingProvider.call(METHOD_MEDIA_COMMAND, key="com.qinggan.keymanager.service")
  → MediaControlRouter.route → MediaControlPolicy (sticky token)
  → MediaKeyPairDelivery (DOWN→UP) → CanSender keys 85/87/88/127

[DOCK / SPLIT]
SplitHostActivity / launcher
  → DockLaunchGuard.acquire (Settings.Global voyahtune_dockLaunchGuard<id>, 5s)
  → app launch (display 0/1)

[PERSISTENCE]
modes toggles: MainActivity.persistSavedToggle
  → ContentProvider ru.big.town.restoremode (update)
  → SharedPreferences NativePrefs cache (cacheForcedEv / cacheDisablePedestrianSound)
  → broadcast SETTING_SYNCED → ru.big.town.restoremode

[DIAG]
All Log.* / CanSender labels
  → NativeLog: logcat --pid → /sdcard/tmp/voyah_native_log.txt (ring 600)
```

**Сводка:** архитектура — layered service + pure-policy + единый CAN-gate; основные риски — exported IPC (`NowPlayingProvider`, dynamic receiver), прямая запись в CAN как fail-open при зависшем `debugMode`/эмуляции, межканальная гонка JNI vs Binder, отсутствие minify. Сильные стороны — fail-closed schema verify, epoch/coverage в `ApplyEngine`, сериализация `NATIVE_SEND_LOCK`, JVM-тестируемые policy.
