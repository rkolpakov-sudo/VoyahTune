# Диаграммы архитектуры VoyahTune

**Коммит:** `45beee4` (master). **Дата аудита:** 2026-09-23.

---

## 1. Диаграмма компонентов

```
┌─────────────────────────────────────────────────────────────────────┐
│                        ХОСТ ГУ (Voyah Free)                        │
│                                                                     │
│  ┌──────────────────────┐    ┌──────────────────────────────────┐   │
│  │  RestoreMode (UI)    │    │  Native (priv-app, ru.big.town.  │   │
│  │  ru.big.town.        │    │  anative)                        │   │
│  │  restoremode         │    │                                  │   │
│  │                      │    │  ┌────────────┐ ┌─────────────┐  │   │
│  │  MainActivity        │    │  │ SetModes   │ │ ApolloTlc   │  │   │
│  │  AdvanceActivity     │◄───┼─►│ Service    │ │ Service     │  │   │
│  │  TripHistory / Log   │MSG │  │ ApplyEngine│ │ (Binder)    │  │   │
│  │                      │    │  └─────┬──────┘ └──────┬──────┘  │   │
│  │  DrivePreferences    │◄───┼────────┤ provider      │         │   │
│  │  (SharedPreferences) │prov│        │               │         │   │
│  │                      │    │  ┌─────▼──────┐        │         │   │
│  │  SplitStore / Dpi /  │    │  │ CanSender  │        │         │   │
│  │  ShortcutStore       │    │  │ (JNI lock) │        │         │   │
│  └──────────┬───────────┘    │  └─────┬──────┘        │         │   │
│             │ broadcasts     │        │ JNI           │ Binder  │   │
│             │ (explicit pkg) │  ┌─────▼──────┐  ┌─────▼──────┐  │   │
│             │                │  │native-lib  │  │ ICanBus    │  │   │
│             │                │  │.cpp mutex  │  │ Service    │  │   │
│             │                │  └─────┬──────┘  │ ICarSignal │  │   │
│             │                │        │         │ Service    │  │   │
│             │                └────────┼─────────┴─────┬──────┘  │   │
│             │                         │               │         │   │
│  ┌──────────▼─────────────────────────▼───────────────▼──────┐  │   │
│  │              libqg_hal.so / CAN-шина / VCU               │  │   │
│  └───────────────────────────────────────────────────────────┘  │   │
│                                                                  │   │
│  ┌───────────── Settings.Global ─────────────────────────────┐  │   │
│  │ leavecar / freeform / apollo master / dock / steer / win  │  │   │
│  └───────────────▲───────────────────────▲───────────────────┘  │   │
│                  │ читает/пишет          │ читает/пишет          │   │
│  ┌───────────────┴──────────┐  ┌─────────┴──────────────────┐  │   │
│  │ Native / RestoreMode     │  │ Frida-скрипты (in-process) │  │   │
│  └──────────────────────────┘  │  vd_bypass → system_server │  │   │
│                                │  launcherdock → launcher   │  │   │
│                                │  steeringwheelkeys → keymgr│  │   │
│                                │  multidisplay → syservice  │  │   │
│                                │  apollo_tech → vehicleset  │  │   │
│                                └──────────▲─────────────────┘  │   │
│                                           │ inject              │   │
│                                ┌──────────┴─────────────────┐  │   │
│                                │ load.bin (watchdog, root)  │  │   │
│                                │ voyahtune_load.rc (init)   │  │   │
│                                └────────────────────────────┘  │   │
└─────────────────────────────────────────────────────────────────────┘
```

**Ключевые связи:**
- RestoreMode ⇄ Native: Messenger (`bindService`), ContentProvider (чтение настроек), explicit broadcasts.
- Native → CAN: два канала — JNI `libqg_hal` (основной) и OEM Binder `ICanBusService`/`ICarSignalService` (Apollo TLC + query).
- Native/RestoreMode → Settings.Global → Frida-скрипты (зеркало конфигурации дока/руля/окон).
- Frida-скрипты → Native: explicit broadcasts (`STEER_ACTION`, `OPEN_FREEFORM`, `OPEN_DOCK_SPLIT`) + provider `nowplaying`.

---

## 2. Диаграмма последовательности установки (full)

```
Пользователь          install.sh            ГУ (ADB)              ГУ (после ребута)
    │                     │                    │                        │
    │  ./install.sh       │                    │                        │
    │────────────────────►│                    │                        │
    │                     │ Preflight (файлы,  │                        │
    │                     │ dns-helper, root)  │                        │
    │                     │───────────────────►│                        │
    │                     │ Apollo safety=0    │                        │
    │                     │───────────────────►│                        │
    │                     │ WRITE_CANBUS owner │                        │
    │                     │───────────────────►│                        │
    │                     │ disable-verity     │                        │
    │                     │───────────────────►│                        │
    │                     │ /system RO? ───────┼── REBOOT №1 ──────────►│
    │                     │◄── wait boot ──────┼────────────────────────│ dm-verity off
    │                     │ remount RW         │                        │
    │                     │ Backup ×8 → ./backup/                       │
    │                     │ /data/local/bin: load.bin, 5 js, frida      │
    │                     │ legacy init.logcat migration                │
    │                     │ boot-hook RC: stage→snapshot→publish→verify │
    │                     │ Native.apk + privapp.xml (atomic)           │
    │                     │ leavecar / freeform props                   │
    │                     │ adb install RestoreMode.apk                 │
    │                     │ DNS menu (install|disable|keep)             │
    │                     │ adb reboot ─────────┼── REBOOT №2 ─────────►│
    │                     │                    │                        │ setenforce 0
    │                     │                    │                        │ load.bin →
    │                     │                    │                        │ frida inject ×4
    │                     │                    │                        │ BOOT_COMPLETED →
    │                     │                    │                        │ SetModesService
    │◄── "Готово" ────────│                    │                        │ ApplyEngine
```

---

## 3. Диаграмма данных

### 3.1 Хранилища

```
┌─────────────────────────────────────────────────────────────────┐
│ RestoreMode: DrivePreferences (SharedPreferences, MODE_PRIVATE) │
├─────────────────────────────────────────────────────────────────┤
│ modes: driveMode, energy, recycle, *Enabled                     │
│ CAN: customCommand, customCommandStarButton1/2                  │
│ UI: showTripTimer/PowerHold/WashMode/..., saveTripHistory       │
│ toggles: autoLight, disablePedestrianSound, forcedEv            │
│ stores: splitPresets(JSON+UUID), appDpi(JSON), appShortcuts(JSON)│
│ dock: dockOverride1/2, dockOverrideNSplit(INDEX!), *Label       │
│ steer: steerStar/Dvr/Voice/Phone Short/Long → action ids        │
└──────────────┬──────────────────────────────────────────────────┘
               │ provider.query (20 cols) / update (5 keys)
               ▼
┌─────────────────────────────────────────────────────────────────┐
│ Native: SharedPreferences NativePrefs (cache)                   │
│  - debugMode, cacheForcedEv, cacheDisablePedestrianSound        │
│ Settings.Global:                                                │
│  - persist.app.feature.leavecar, enable_freeform_support        │
│  - voyahtune_dock1/2, dockLaunchGuardN, dockpin, freeform       │
│  - voyahtune_ste* (steering slots), voyahtune_win_*, dpi_*      │
│  - open_voyah_apollo_{master,legacy_hook_enabled,...}           │
│  - GLOBAL_MASTER_KEY (Apollo TLC master)                        │
│ OEM: /private/configs/token/accountInfo (individual profile)    │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 Поток применения режимов (hot path)

```
Trigger (boot / SWC star / UI Apply / screen)
  → SetModesReceiver* / Messenger MSG_APPLY_DRIVE_MODES
  → ApplyEngine.scheduleApply (epoch, coverage)
  → worker: provider.query(DrivePreferences) → frames via DriveModeCanPolicy/Transport
  → CanSender.send(NATIVE_SEND_LOCK)
      ├─ debugMode → emulate log (return true)
      └─ JNI libqg_hal → ioctl TX=77/58 (10-byte frame)
  → completeCycle → ModeSyncPolicy.decide (settle 20s / cooldown 3s)
  → broadcast SETTING_SYNCED / MODE_SYNCED → RestoreMode UI
```

### 3.3 Поток сплитов (full only)

```
AdvanceActivity saveSplitPresets
  → SplitStore.save (UUID id)
  → SplitConfigSync.pushAll → broadcast DOCK_CONFIG/STEER_CONFIG
  → Native SetModesConfigReceiver → Settings.Global
  → Frida launcherdock/vd_bypass читают Settings.Global

User drags split divider
  → Native SPLIT_RATIO_SAVE{presetId, idx, ratio}
  → RestoreMode SplitRatioSaveReceiver → SplitStore.update → pushAll
```

---

## 4. Процесс установки (сводная текстовая диаграмма)

См. также `code-review-installer.md` — полная 12-фазная диаграмма `full/install.sh`.

```
Preflight → Apollo safety → CANBUS owner → disable-verity
    → [REBOOT №1 if RO] → Backup → /data/local/bin (Frida)
    → legacy init migration → boot-hook RC → Native.apk + whitelist
    → props → RestoreMode.apk → DNS → [REBOOT №2]
```

Light-вариант: без фаз 5–7 (Frida/boot-hook), только Native + whitelist + RestoreMode + DNS.
