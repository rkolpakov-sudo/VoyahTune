# Code Review: Frida-скрипты VoyahTune

**Область:** `Packaging/inject/*` (5 файлов). Только исследование; файлы не изменялись.
**Коммит:** `45beee4` (master). **Дата аудита:** 2026-09-23.

**Контекст инжекции (из `load.bin`):** watchdog-цикл раз в 10 с под root (RC-сервис `voyahtune_load`, после `sys.boot_completed=1`, `setenforce 0` на `post-fs-data`), владение — symlink-lock `/data/local/tmp/voyah_load.v2.lock`. Цели: `system_server` → vd_bypass; `com.qinggan.app.launcher` → launcherdock; `com.qinggan.keymanager.service` → steeringwheelkeys (фоновый single-flight, т.к. `frida-inject -e` может зависнуть); `com.qinggan.systemservice` → multidisplay; `com.qinggan.app.vehiclesetting` → apollo_tech (только при opt-in). Для 1/2/4 — eternalize (`-e`) + verify-before-mark (grep ошибок до записи pid-маркера); для Apollo — additionally ready-marker `[apollo] hook ready` и identity `v2|boot_uuid|pid|starttime`.

---

## Файловая таблица

| Файл | Цель (процесс/класс) | Хуки | Модифицируемое поведение | Взаимодействие с Native |
|---|---|---|---|---|
| `steeringwheelkeys.js` (346 стр.) | `com.qinggan.keymanager.service`, `…engine.KeyManagerReader` | `onKeyEvent(KeyEvent)` — единая точка | SWC-коды 3090/173/130/128 → STAR/DVR/VOICE/PHONE с таймером short/long (600 мс), слоты из `Settings.Global voyahtune_ste*`; медиа-коды 3/4/6 → маршрутизация через provider `content://…nowplaying` (`media_command`: direct/keymanager/native/noop), иначе `AudioManager.dispatchMediaKeyEvent`; replay штатной пары DOWN+UP при отложенном stock-действии | **Broadcast out:** `ru.big.town.anative.STEER_ACTION` → `SetModesReceiverDynamic` (explicit, `FLAG_INCLUDE_STOPPED_PACKAGES`). **Receiver in:** `MEDIA_KEY_PROXY` (гейт — sender-пермишен `WRITE_SECURE_SETTINGS`), ACK `setResultCode(-1)`. **Provider call** к Native. Читает `Settings.Global` (пишет Native из UI «Кнопки на руле») |
| `launcherdock.js` (623 стр.) | Лаунчер; OD: `com.qinggan.launcher.navigation.NavigationBarMain`, PI: `com.qinggan.mainlauncher.navigation.NavigationBar` (+ `…NavigationBarController`, `LauncherModel`, `ThirdAppUtil`) | `updateTheme`, `initScreenUpViews`, `updateSelectedApp`, `onClick`, `dismiss` (бар+контроллер), `isThirdShowFloatApp` ×2, `onMoveStart` (трассировка) | Иконки слотов 1/2 из `voyahtune_dock1/2` (setBackground + ретейн Drawable, cap 64), клик слота → freeform-запуск, long-tap слота → сплит, long-tap «меню» → VoyahTune; пиннинг дока поверх сторонних (`dockKept`/`pendingDockLaunch`), подавление floating-home, reverse-mapping подсветки, гейт по экрану (`isDriverBar`/`mScreenId`) — пассажирский бар не трогается | **Broadcast out:** `OPEN_FREEFORM`, `OPEN_DOCK_SPLIT` → `SetModesReceiverDynamic`. **Receiver in:** `DOCK_RELOAD` (явная сигнатура `onReceive` — иначе `AbstractMethodError` → крэш лаунчера). Читает `voyahtune_dock*`, `dockpin`, `freeform`, `floathome`, `dockLaunchGuardN` (пишет Native). Запускает `ru.big.town.restoremode.MainActivity` |
| `multidisplay.js` (131 стр.) | `com.qinggan.systemservice`, `…multidisplay.MultiDisplayImpl` | `isWhiteListApp` (все перегрузки) | Whitelist-чек переноса между экранами → `true` для всех, кроме NEVER (лаунчер, `ru.big.town*`, systemui); флаг `voyahtune_multidisplay` (деф. 1), кэш без чтения Settings в горячем хуке; осечка → оригинал | **Receiver in:** `MD_RELOAD` (перечитка флага). Читает `Settings.Global` |
| `vd_bypass.js` (481 стр.) | **system_server** | 1) `InputManagerService.checkInjectEventsPermission` 2) `DisplayManagerService$BinderService.checkCallingPermission` 3) `ActivityStackSupervisor.isCallerAllowedToLaunchOnDisplay` 4) `ActivityRecord.canBeLaunchedOnDisplay` 5) `PackageManagerService.hasSystemFeature` 6) `DisplayPolicy.layoutWindowLw` (detachable) 7) `ActivityRecord.ensureActivityConfiguration` (detachable) | Bypass 1–5 только для UID `ru.big.town.anative` (fail-closed при -1) / фичи secondary displays; 6–7 — «фейк-freeform»: clamp Rect окна (`voyahtune_win_*`, деф 145/45/1920/720) с сохранением/восстановением `DisplayFrames.mStable` + per-app DPI (`voyahtune_dpi_<pkg>`); detache горячих хуков на SCREEN_OFF + отложенный reattach (1 с/5 с), epoch-отмена | **Receiver in:** `WIN_RELOAD` (пермишен-гейт `WRITE_SECURE_SETTINGS`) — перечитка кэша. Конфиг в `Settings.Global` (пишет Native) |
| `apollo_tech.js` (973 стр.) | `com.qinggan.app.vehiclesetting` | `BaiduProviderUtil.doQuerySubscribeInfo`, `CanBusTool$3.onVehicleStateChanged` (только legacy 97C), `ContentObserver` по master-ключу | Fail-closed по умолчанию: нет `open_voyah_apollo_legacy_hook_enabled=1` → очистка gate и выход. Далее: pinned SHA-256 VehicleSetting+CanBusService, профиль 97C (ASC/SDB=1) или diagnostic H97X; fake-подписка JSON при открытом gate, иначе pass-through с bounded stock-resync (≤3 попытки); wake-фильтр 924=1/958=3 с coalescing; heartbeat 30 с | Пишет `Settings.Global`: `open_voyah_apollo_{asc,sdb,profile_supported,profile_heartbeat,master}` (на H97X мастер всегда принудительно 0). Sentinel-свойство JVM против повторного attach; полный self-cleanup хуков при install-fail |

---

## Ключевые особенности надёжности (уже в коде)

- Явные сигнатуры `onReceive` (шортхенд `registerClass` ронял процессы через `AbstractMethodError` — задокументировано в launcherdock/vd_bypass/multidisplay).
- Eternalize вместо приаттаченного `frida-inject` на system_server (ptrace дестабилизирует).
- Точечные rare-методы вместо общего `checkComponentPermission` (watchdog).
- Try/catch на уровне окна в горячем `layoutWindowLw` (ошибка одного окна не гасит freeform глобально).
- Fail-closed UID в vd_bypass.

---

## Риски

1. **system_server (vd_bypass)** — крэш = soft-reboot всей системы. Смягчено (try/catch, detache на SCREEN_OFF, eternalize, явный `onReceive`, fail-closed UID), но хрупкость приватных полей WM (`mStableFrame`…, `ensureActivityConfiguration(int,boolean,boolean)`, `ActivityStackSupervisor` — переименован уже в A12+) оставляет риск тихой деградации или исключения на новой прошивке.
2. **Версионная хрупкость имён:** OD/PI-классы лаунчера, `KeyManagerReader`, `MultiDisplayImpl.isWhiteListApp`, `STOCK_SLOT_PKG` (комментарий сам просит подтверждения на H97C), приватные поля `mScreenUpItemView1/2`/`mScreenId`. Все хуки fail-soft, но функции молча отключаются.
3. **Ручная купонная связность `STOCK_PREFIX` (launcherdock) ↔ `ffBlacklisted` (vd_bypass)** — рассинхрон → док зависает поверх полноэкранного окна (задокументировано только комментарием).
4. **Неограниченный рост логов инжекта:** `inject_ret` делает `cat >> voyah_{vd_bypass,lnch,md,swk}.txt` на **каждой** неудачной попытке раз в 10 с без ротации (ротация 1 МБ есть только у Apollo-лога) → риск заполнения `/data/local/tmp` при персистентном сбое инжекта (например, после смены имени класса).
5. **frida-inject -e hang** на keymanager/VehicleSetting — закрыт `timeout -k 5 30` + background single-flight + busy-TTL 45 с; остаточный риск — два конкурентных инжекта, если `pidof frida-inject` промахнётся (маловероятно).
6. **Pinned SHA-256 Apollo** — обновление OEM APK ломает legacy-хук (fail-closed, приемлемо, но «неожиданно» для пользователя).
7. **Синтетический replay DOWN+UP в steeringwheelkeys** — самая тонкая логика (удержание/таймер/native-fallback); потеря UP или исключение внутри `pressHandler` может оставить OEM-кеймениджер в pressed-state до следующего события (частично закрыто replay-парой).
8. **`Java.retain`/`$dispose` Drawable в launcherdock** (cap 64) — паттерн снятия старых ref'ов при бурсте перекрасок темы потенциально рискован, если `updateTheme` дергает dispose на ещё используемом фоне (сейчас dispose только вытесненных).

---

## Возможности улучшений (минимальные/безопасные)

1. **Ротация логов `inject_ret`** — аналогично Apollo: `if size ≥ 1M → mv .1` перед `cat >>` для `voyah_vd_bypass.txt`, `voyah_lnch.txt`, `voyah_md.txt`, `voyah_swk.txt` (и `voyah_load.txt` в `voyahtune.load.sh`). Защита `/data` при персистентном fail.
2. **Автотест синхронности `STOCK_PREFIX` ↔ `ffBlacklisted`** — расширить `test_apollo_direct_only.sh` grep-проверкой совпадения списков (оба — статические литералы). Дёшево, закрывает риск №3.

**Не предлагать:** изменение hot-path vd_bypass, отказ от eternalize, обобщение хуков шире текущих точек входа — без живых голов на целевых прошивках это недоказуемо.
