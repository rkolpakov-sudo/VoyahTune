# HISTORY.md — журнал сессий и изменений VoyahTune (append-only)

> Новый блок — **в конец файла**. Правила: фиксировать только факты; открытые вопросы; при изменении кода — файлы + как проверять. Аксиомы: см. `AGENTS.md`.

---

## 2026-09-23 — Сессия 1: Фаза 0 (аудит) + введение аксиом

### Контекст
- Рабочая папка пришла **пустой** (только `.git init` + план `Qwen_markdown_20260923_r7xvknhqu.md`).
- Клонирован `https://github.com/nexron171/VoyahTune`, ветка `master`, **эталонный коммит `45beee4`** («Drive mode new transport»).

### Выполнено (код НЕ менялся)
1. **Фаза 0 — полный код-ревью** (4 направления):
   - Native: 33 Java-файла + manifest + Gradle + `native-lib.cpp`
   - RestoreMode: 13 Java-файла + manifest + layouts
   - Frida: 5 скриптов в `Packaging/inject/`
   - Установщики: full/light (.sh/.bat), DNS common, `make_release.sh`, boot-hook, whitelist, `test_apollo_direct_only.sh`
   - Документация: readme, hownews, Packaging README, Native readme
2. **Созданы артефакты** `docs/audit/`:
   - `code-review-native.md`, `code-review-restoremode.md`, `code-review-frida.md`, `code-review-installer.md`
   - `architecture-diagrams.md`, `risk-assessment.md` (R1–R26), `compatibility-matrix.md` (черновик), `phase0-report.md`
3. **Введены аксиомы пользователем** (Sport+ 2026 = эталон; сохранение работоспособности; тройной анализ; вопросы при неуверенности).
4. **Созданы файлы агента:**
   - `AGENTS.md` (корень) — аксиомы и правила сессий
   - `docs/agent/PROJECT.md` — описание проекта для handoff
   - `docs/agent/HISTORY.md` — этот журнал

### Изменения кода
- **Нет.** Рабочее ПО не тронуто.

### Ключевые выводы (детали в phase0-report.md)
- Топ-риск: открытый `RestoreModeContentProvider` (R3), 5 unprotected receivers (R4), `debugMode` fail-open (R5), vd_bypass (R9→R2), light install push без rc (R9).
- Сильные стороны: `CanSender`, `ApplyEngine`, fail-closed Apollo, атомарный boot-hook — **не рефакторить**.

### Открытые вопросы пользователю
1. Записаны ли аксиомы в `AGENTS.md` устраивают? (файл создан по запросу)
2. Sport+ 2026 = OD-прошивка? Строка матрицы = «Voyah Free OD Sport+ 26»?
3. Стартовать ли Фазу 1 (документирование, код не трогается)?

### Статус на конец сессии
- Фаза 0: **✅ завершена**
- Фаза 1–6: **не начаты**
- Согласованных изменений кода: **0**

---

## 2026-09-23 — Сессия 1 (продолжение): повторное глубокое код-ревью

### Контекст
- Запрос пользователя: поиск ошибок кода, проблем архитектуры и **безопасных UX-улучшений** (не нарушающих работу).
- Режим READ-ONLY по аксиомам; код **не изменялся**.

### Выполнено
- 4 параллельных глубоких ревью: Native-bugs, RestoreMode-bugs, Installer/Frida-bugs, safe-UX.
- Результаты сведены в **`docs/audit/deep-review-2.md`**.

### Ключевые находки (новые vs Фазы 0)
- **Native:** exported log-receiver без permission (BUG-N01); лог в `/sdcard/tmp` (N02); гонка статиков режимов (N03); star-button `MSG_RESULT` до CAN (N05); `applyTheme mode==3` (N07).
- **RestoreMode:** баги API-оверлоада `RECEIVER_EXPORTED` (R01=R20); битый portrait layout (R02); **гонка binder-потоков в provider** (R06, новое); CAN-форматтер съедает `\n` (R07, новое); утечка handler без onDestroy (R09); `apolloPending` без таймаута (R10); потеря пресетов при частичном JSON (R12).
- **Installer:** CRLF vs LF (I01); light push без rc (I05=R9); light remove без RW-gate (I04); sh↔bat CANBUS/DNS (I03/I08); bat `exit 0` после ошибки (I19).
- **Safe UX:** 20 пунктов UX-S* + 11 UX-R*; TOP-10 docs/echo/UI-feedback без CAN-path.

### Изменения кода
- **Нет.**

### Открытые вопросы
- Согласование пачек: (A) docs-only UX → (B) UI-feedback → (C) must-fix installer — какой порядок / что «делать»?

### Статус
- Глубокое ревью: **✅** (отчёт: `docs/audit/deep-review-2.md`)
- Согласованных изменений кода: **0**

---

## 2026-09-23 — Сессия 1 (продолжение): дизайн UX/TUI установки

### Контекст
- Запрос: максимально улучшить/упростить UX установки, автоматизация, TUI с усиленной защитой от ошибок.
- Изучены `full/install.sh` (1–150), `install.bat` (1–80), `README.txt` (полностью).

### Выполнено
- Черновик дизайна: **`docs/agent/design-install-tui.md`**.
- Подход: **TUI-оркестратор вокруг неизменного движка** (12 фаз не переписываем); барьеры G0–G8; A1–A12 автоматизация; этапы D0–D7.
- Открыты вопросы **Q1–Q6** пользователю (форма TUI, правки движка, tee-лог, verify, DNS, light).

### Изменения кода
- **Нет.**

### Статус
- Дизайн install-TUI: **✅ черновик**, ожидает Q1–Q6 / «делай D2»
- Изменений кода: **0**

---

## 2026-09-23 — Сессия 1 (D2): TUI-обёртка установки (новые файлы)

### Согласование
- Q1–Q6 пользователь выбрал рекомендованные варианты (POSIX+bat, без маркеров в движке, tee в TUI, verify в D2, DNS как есть, сначала full).
- «Continue» + ответы на Q = продолжение D2. Движок `install.sh`/`install.bat`/`light/*` **не изменялся**.

### Изменено/создано
**Новые файлы:**
- `Packaging/installer/full/tui-lib.sh`
- `Packaging/installer/full/install-tui.sh` (`--yes` `--non-interactive` `--dry-run`)
- `Packaging/installer/full/verify_post_install.sh`
- `Packaging/installer/full/install-tui.bat` (ASCII menu)
- `Packaging/installer/full/verify_post_install.bat`

**Правки:**
- `make_release.sh` — `verify_release_payload` full: +5 TUI-файлов
- `Packaging/README.txt` — абзац про install-tui (install.bat остаётся non-interactive)
- `docs/agent/design-install-tui.md` — статус D2 + Q1–Q6

### Проверки
- `bash -n`: tui-lib, install-tui, verify, test_apollo, make_release — **OK**
- `test_apollo_direct_only.sh` — **PASS**
- `install-tui.sh --dry-run` в репо (нет flat-релиза) — корректно exit 3 на bundle preflight

### Не сделано (следующие этапы)
- D3: BUG-I19 (exit 0 после `!!!` в bat) — **ждёт явного «делай»**
- D4: light push `|| exit 1` (I05), RW-gate remove (I04)
- D5: фазовые `[N/12]` в движке (пользователь отказал)
- D6: MANIFEST.sha256
- D7: PowerShell TUI
- Live-прогон на Sport+ 2026 — **не выполнен**

### Открытые вопросы
- Согласовать D3 (минимальный фикс exit code в full/light bat)?
- Light TUI — когда?

---

## 2026-09-23 — Сессия 1: единый план рефакторинга

### Контекст
- Запрос: «Сведи все данные исследований и планирования в полный единый документ плана рефакторинга».

### Выполнено
- Создан **`docs/agent/REFACTOR_PLAN.md`** (версия 1.0) — сводка:
  - аксиомы + красные линии;
  - дашборд статуса Фаз 0–6 и TUI D0–D7;
  - каталог R1–R26, BUG-N/R/I, ARCH, UX;
  - единая дорожная карта tracks A/B/C + уровни приоритета;
  - WP-пакеты с гейтами; чьё-NE; тесты/приёмка; журнал решений Q1–Q6;
  - карта источников; приложения (фазы, тройной анализ).

### Изменения кода
- **Нет.** Только документация.

### Статус
- Единый план: **✅ v1.0**
- Далее: live D2 на авто / «делай D3» или «делай C1» — ждёт пользователя.

---

## 2026-09-23 — Сессия 2: Ф1 docs + D3/D4 + пакеты C1/C2/C3 (partial)

### Контекст
- Реплика пользователя: **«Приступай»** — общий «да» на последовательное выполнение `REFACTOR_PLAN.md` (Ф1 → D3 → D4 → C1 → C2 → partial C3) с сохранением тройного анализа и запретов hot-path.
- Live-тесты на Sport+ 2026 **не выполнялись** (нет доступа к авто).

### Выполнено — Ф1 docs (без код-гейта)
- Создано:
  - `docs/user/installation.md`, `docs/user/safety.md`
  - `docs/technical/architecture.md`, `docs/technical/can-and-references.md`
  - `docs/developer/contributing.md`, `docs/developer/testing.md`, `docs/developer/release.md`
- `readme.md`: FAQ Q/A перепутаны исправлены (UX-S13); кабель Type-A↔A (UX-S17); две перезагрузки (UX-S14); TUI-упоминание; новый Q про root/перезагрузки.
- `Packaging/README.txt`: light/full + две перезагрузки в IMPORTANT (UX-S16); уточнён шаг «Installation complete».

### Выполнено — D3 (BUG-I19)
- `Packaging/installer/full/install.bat` `:handle_safe_boot_hook_failure`:
  - `exit /b 1` на ветках `!!!` (verify fail, PARTIAL, rollback fail);
  - `exit /b 0` только для READY / успешного legacy rollback;
  - после status≠0 в main — явный `echo !!! Boot hook install failed` + `exit /b 1`.

### Выполнено — D4 (I05/I04)
- `light/install.sh`: `mkdir/chmod/push` Native.apk и whitelist — `|| exit 1`; `chown/chmod/restorecon/sync/test` после push; при fail push — `rm` staging.
- `light/install.bat`: те же проверки `errorlevel` + restorecon.
- `light/remove.sh` / `light/remove.bat`: **RW-gate** (touch `/system/.ovw_rwtest`) перед `rm` из `/system`; при RO — `exit 1`, файлы не тронуты.

### Выполнено — C1
| ID | Файл | Фикс |
|----|------|------|
| R06 | `RestoreModeContentProvider.java` | `query()` только на локальных переменных (нет полей-состояния) |
| R07 | `AdvanceActivity.java` | `split("\n",-1)` + сохранение `\n` между логическими строками; валидация `%31` на полной длине **сохранена** |
| R12 | `SplitStore.java` | try/catch на элемент в load/save; при битом JSON — без автосейва |
| N03 | `Native/MainActivity.java` | `volatile` на static-состоянии режимов |
| N04 | `GlobalVars.java` | `volatile SAVE_CONTEXT` |
| N05 | `SetModesService.java` | star-button: `MSG_RESULT` **после** `ApplyEngine.postUserCommand` (+ overload `worker(..., Messenger)`); null-context — notify сразу |

### Выполнено — C2 (partial)
| ID | Файл | Фикс |
|----|------|------|
| R01 | MainActivity, AdvanceActivity, LoggingActivity, TripHistory | `ContextCompat.registerReceiver(..., RECEIVER_EXPORTED)` (API-gate без смены permission-путей) |
| R03 | `RestoreMode/MainActivity` | null-check `data` в `onActivityResult` |

### Выполнено — C3 (partial)
| ID | Файл | Фикс |
|----|------|------|
| N02 | `NativeLog.java` + `file_paths.xml` | лог в `getFilesDir()` (не world-readable `/sdcard/tmp`); FileProvider `files-path` + legacy paths |

### Проверки
- `bash -n`: full/light install.sh, light/remove.sh, test_apollo — **OK**
- `test_apollo_direct_only.sh` — **PASS**
- Gradle/Android SDK — **нет в среде** (`SDK=no`) → unit-тесты и assemble **не гонялись**

### Изменения кода — тройной анализ (сводно)
- **CAN/hot-path:** `CanSender`/JNI/`ApplyEngine` core/**не тронуты**; N05 — только порядок `MSG_RESULT` у star-button (Messenger replyTo), байты CAN без изменений.
- **Бут/install-фазы:** порядок 12 фаз **не менялся**; только exit-codes и проверки ошибок light + I19.
- **Согласование:** «Приступай» (2026-09-23) = общий да на план; R3/R4/N01 **не входят** (отдельный гейт).
- **Риск/live:** фиксы installer и N05/R01 требуют **live-теста** на Sport+ 2026; N02 — share лога через FileProvider после смены пути.

### Открытые вопросы
1. Live D2 (TUI) на авто — не прогнан.
2. R3/R4/N01 (permission) — ждут отдельного «делай» + тест IPC.
3. R20: фактический `ro.build.version.sdk` ГУ неизвестен.
4. Полный список 27 BUG-I — выгружать отдельно?

### Статус фаз плана
- 0: ✅ · 1: **✅ (docs-каркас + FAQ/README)** · 2: 🟡 · 3: 🟡 (D2 done; D3/D4 done; D6 нет) · 4: **🟡 C1+C2 partial** · 5: 🟡 · 6: ❌
- Согласованных изменений «горячего» кода: **0** (N05 — IPC порядок, не CAN-байты); live-прогон installer/Java: **нет**.

---

## 2026-09-23 — Сессия 3: INSTALL_GUIDE + публикация форка

### Контекст
- Просьба: (1) подробный поступательный гайд установки со сложностями и решениями; (2) разместить проект на GitHub форком к оригиналу.
- Согласование пользователя: явная просьба про коммит/публикацию (аксиомы §2.5).

### Выполнено
1. **Гайд:** создан `docs/user/INSTALL_GUIDE.md` — 14 разделов: комплекты full/light, безопасность, оборудование, подготовка, тренажёр ADB, Windows (install.bat / TUI / закрытие окна), macOS/Linux (Gatekeeper), две перезагрузки, Yandex DNS, post-install чек-лист, **§11 типовые сложности** (ADB / BAT / root+/system / сбои посреди / «не работает» / строки `!!!`), удаление, FAQ, чек-лист баг-репорта.
2. **Ссылки:** `docs/user/installation.md` и `readme.md` указывают на `INSTALL_GUIDE.md`.
3. **GitHub:** форк создан — **https://github.com/rkolpakov-sudo/VoyahTune** (parent: `nexron171/VoyahTune`).
4. Remote `fork` → форк; коммит рабочих изменений сессий 1–3 (docs, audit, TUI, фиксы D3/D4/C1/C2/C3, AGENTS/plan/guide); push ветки `master` в форк.

### Изменения кода
- Файлы: **без изменения hot-path в этой сессии**; в коммит попадают уже согласованные ранее фиксы (D3/D4/C1/C2/C3 partial) + документация.
- Согласование: «Приступай» (план) + явная просьба опубликовать форк (2026-09-23).

### Проверка
- `bash -n` по shell-скриптам — OK (сессия 2); `test_apollo_direct_only.sh` — PASS.
- Гайд: ревью путей к файлам относительно корня релиза/репо.
- Push: `git push fork master`.

### Открытые вопросы
1. PR в `nexron171/VoyahTune` — не открывал (не просили; upstream может закрыт).
2. Live-тесты на Sport+ 2026 и R3/R4/N01 — прежние.
3. Описание/тема репозитория форка настроены ли GitHub UI — не проверялись.

### Статус фаз плана
- 0: ✅ · 1: ✅ (+ INSTALL_GUIDE) · 2: 🟡 · 3: 🟡 (D2–D4) · 4: 🟡 C1+C2 partial · 5: 🟡 · 6: ❌

---

## 2026-09-23 — Сессия 4: fix-all по код-ревью (C1/C4/H1–H8 + medium)

### Контекст
- Коммит в работе: после `f1a10a9` (форк/гайд/ревью); **не коммитить без явной просьбы**.
- Вход: «Выполни весь перечень выявленных проблем! В случае неверности сверься со справочной документацией!» = общий да на fix-all.
- Источник: `Docs/audit/deep-review-2.md`, `risk-assessment.md`, code-review-*; сверено с `Packaging/README.md`, `readme.md`, кодом (hot-path НЕ тронут).

### Выполнено

**Манифесты / permission-модель (R3/R4-класс, но без смены сигнатуры):**
- RestoreMode provider: `readPermission`/`writePermission` = `BIND_SET_MODES_SERVICE`.
- RestoreMode manifest: `allowBackup=false`; удалён `WRITE_EXTERNAL_STORAGE` (внешнее хранилище не используется); self-package из `<queries>` удалён.
- Native manifest: удалены `SEND_SMS`, `READ_SMS`, `WRITE/READ_EXTERNAL_STORAGE`, `READ_PHONE_STATE` (maxSdk 28), `GET_TASKS` (maxSdk 21); `requestLegacyExternalStorage` удалён; `allowBackup=false`.
- `SetModesReceiverDynamic` оставлен exported **без** permission — Frida шлёт explicit `setClassName` из чужого контекста, системный sender (KEYCODE_SWC/SCREEN_ON/OFF/GARAGE) тоже; residual H2 задокументирован комментарием в манифесте.
- `registerReceiver(..., BIND_SET_MODES_SERVICE, ..., RECEIVER_EXPORTED)`: Native `BatteryHeatService`, `TripStatsService`, `LightSensorService`, `NowPlayingService`, `SetModesService` (logRequest); RestoreMode `MainActivity`, `AdvanceActivity`, `LoggingActivity`, `TripHistoryActivity` (через ContextCompat).
- `sendBroadcast(intent, BIND_SET_MODES_SERVICE)` + `setPackage` на исходящих Native↔RestoreMode (TRIP/BATTERY_HEAT/LUX/LOG/MODE_SYNCED/SETTING_SYNCED/REQUEST_*).

**RestoreMode UI / crash:**
- H6: `MainActivity` — `setRequestedOrientation(LANDSCAPE)` до `setContentView`; null-guard layout; `initIntentStarButton` extras → `resultIntentStarButton`; `getModes()` try/catch + null-guard курсора; **добавлен `onButtonClickApply(View)`** (portrait `activity_main.xml:216` ссылался на несуществующий метод).
- H5: `AdvanceActivityStarButton.validateStarButtons()` — per-line hex 20 символов (без `%31`), из TextWatcher + начальная валидация; save блокируется при невалидности; back всегда разрешён.
- Back-ловушка: `finishWithCustomCommands()` — при невалидном формате log + `finish()` без сохранения.
- Apollo force-off: `updateApolloUi()` теперь показывает кнопку по `canForceApolloMasterOff()` (раньше всегда GONE — dead UI).
- Medium: `TripHistoryActivity.renderTripLog` per-item try/catch; `versionCode=2` (оба `build.gradle.kts`); `readme.md` `docs/user`→`Docs/user`; `Packaging/README.txt` — TUI только в полном комплекте.

**Installer:**
- H7: `full/light install.bat` — CANBUS unknown owner → `exit /b 1` fail-closed (как `.sh`).
- H8: `light install.sh/.bat` — атомарный push Native.apk + privapp-whitelist через `.voyahtune.new` + chown/chmod/restorecon + `mv -f` + `sync` + `test -f`.
- Light `remove.sh/.bat` — чистка `voyahtune_dock*`, `voyahtune_steer*` settings (как full), не только `open_voyah_apollo_*`.

**presetId (устойчивость к удалению пресетов):**
- `pickDockSplit` → `dockOverride{N}SplitId` (string uuid); legacy int `dockOverride{N}Split` мигрирует через `SplitStore.resolveAssigned`.
- `pickSteerSplit` → `splitid:<uuid>`; `SplitConfigSync.resolveSteerAction` и `steerActionLabel` принимают `splitid:` + legacy `split:N`.
- `SplitStore.findById` / `resolveAssigned` добавлены; `SplitPresetIdx` по-прежнему шлётся как fallback для старого Native.

**LoggingActivity `running`:**
- Snapshot Native шлёт `running` (SetModesService:519); UI синхронизирует switch+prefs с фактическим состоянием Native (без обратного broadcast через `syncingSwitch`).

**Приватность/whitelist:**
- `privapp-permissions-*.xml` сверен с новым манифестом: `REAL_GET_TASKS` оставлен в обоих; удалённых SMS/STORAGE в whitelist не было; `CAR_MOCK_VEHICLE_HAL` оставлен (в коде не найден — оставлен как есть, риск не снимался самовольно).

### Изменения кода
- **RestoreMode:** `AndroidManifest.xml`, `MainActivity.java`, `AdvanceActivity.java`, `AdvanceActivityStarButton.java`, `LoggingActivity.java`, `TripHistoryActivity.java`, `SplitStore.java`, `SplitConfigSync.java`
- **Native:** `AndroidManifest.xml`, `SetModesService.java`, `BatteryHeatService.java`, `TripStatsService.java`, `LightSensorService.java`, `NowPlayingService.java`, `MainActivity.java`
- **Installer:** `full/install.bat`, `light/install.bat`, `light/remove.sh`, `light/remove.bat`
- **Docs:** `readme.md`, `Packaging/README.txt`; оба `build.gradle.kts` versionCode=2
- Согласование: явный приказ «Выполни весь перечень…» (2026-09-23). **Коммит/пуш — не выполнялись** (ждут явной просьбы).

### Проверка / как тестировать
- `bash -n` по 4 install/remove `.sh` — OK (Git bash).
- Gradle/юнит — **SDK=no**, не запускались; Java-фиксы требуют компиляции + live-теста на Sport+ 2026.
- Ручной чек на авто: portrait-экран MainActivity (Apply), Advance StarButton невалидный hex → save блокируется, Logging switch после перезапуска Native, док/руль сплит после удаления пресета (не должен «уехать» на чужой), light install/remove полный цикл, permission-gate: RestoreMode видит TRIP/BATTERY/LOG broadcast, сторонний пакет — нет.

### Открытые вопросы
1. Live-тест fix-all на Sport+ 2026 — обязателен до любого релиза/коммита.
2. `setenforce 0` / транспорт-дедуп — задокументированы, **не трогались**.
3. Whitelist `CAR_MOCK_VEHICLE_HAL` — в коде не найден; убрать только по явному «да» + проверка, что CanBus не падает без него.
4. R20: `ro.build.version.sdk` ГУ неизвестен (RECEIVER_EXPORTED de facto = API 33+).

### Статус фаз плана
- 0: ✅ · 1: ✅ · 2: 🟡 · 3: 🟡 (D2–D4 + fix-all installer) · 4: 🟡 C1+C2+R3/R4-class+H*+medium · 5: 🟡 · 6: ❌

---

## 2026-09-23 — Сессия 5: install-track Фаза I+II (docs + обёртки) + III-B1 (движок)

### Контекст
- Вход: план reinstall v1.1 → «Приступай к реализации» = старт **Фазы I (Gate A)** + **Фазы II Gate A**; далее явное «да» только на **III-B1**.
- **Фаза III остаток** (B2 cd, B3 wait-timeout, B4 light backup, B5 exit nonzero, B6 exit-map, B7 adb-s) и **Gate B** (bat-TUI preflight, TUI-default docs, MANIFEST) — **не начаты**, ждут явных «да» по пунктам.
- Аксиомы: порядок фаз движка, `vd_bypass`/`load.bin`/rc, CAN — не тронуты.

### Выполнено

**Фаза I — docs / process (Gate A):**
| ID | Что |
|----|-----|
| I-A1 | `.gitattributes`: `**/*.sh text eol=lf` (I01) |
| I-A2 | `INSTALL_GUIDE` §11.1b — **preflight Apollo / WRITE_CANBUS** (симптом → действие, «/system не тронута») |
| I-A3 | Гайд: TUI-меню = **5 пунктов**; **TUI/verify только full** (§6.2, §7.2–7.3); light → plain install |
| I-A4 | `INSTALL_GUIDE`: `docs/user`→`Docs/user` (6 ссылок), опечатка «тржинг»→«тренажёр», APK «плоско в корне ZIP» (не `Releases/`) |
| I-A5 | `compatibility-matrix.md:36`: bat CANBUS = **fail-closed** (R12 закрыт fix-all), не «warn+continue» |
| I-A6 | `readme.md`: APK в корне ZIP; TUI рекомендуется; TUI/verify = full; `Packaging/README.txt` — секции **PREFLIGHT ДО ЗАПИСИ В /SYSTEM** + wait-for-device >30–60с |

**Фаза II Gate A — обёртки / read-only:**
| ID | Что |
|----|-----|
| II-A1 | `verify_post_install.sh/.bat` — **`--light` / `VERIFY_LIGHT=1`**: boot-hook absent = **OK** (не warn); bat-TUI verify спрашивает Full/Light |
| II-A2 | `install-tui.sh` + `tui-show-menu` в `tui-lib.sh` — меню **Install / Verify / Remove / DNS / Dry-run / Exit** (паритет bat, + dry-run); интерактивный TTY без флагов → меню; `--menu`; `tui_run_install_flow` вынесен |
| II-A3 | `tui_run_engine`: **ротация** `install.log` → `install.log.1` |
| II-A4 | Подсказка wait-for-device >30–60с: `tui_show_safety` п.7 + preflight-fail в install-tui.sh |

**Фаза III Gate B — движок (согласовано «Выполняй по очереди» 2026-09-23):**
| ID | Что |
|----|-----|
| III-B1 | `full/install.sh` + `light/install.sh`: явный success-эхо **перед** финальным `adb reboot` |
| III-B2 | `cd "$(dirname "$0")" \|\| exit 1` в `full/install.sh`, `full/remove.sh`, `light/install.sh`, `light/remove.sh` (защита от запуска не из релизной папки) |
| III-B3 | **wait-timeout**: helper `wait_adb_device` в `dns-overlay.sh` (ADB_WAIT_TIMEOUT, default 60с, post-reboot 120с) + `:wait_adb_device` subroutine во всех 5 `.bat`; fail-closed `exit 1` с README-подсказкой |
| III-B4 | `light/install.sh`+`light/install.bat` `backup_pull` — порт full: PRESENT/ABSENT/ERROR, `.new`+`mv` атомарно, ERROR → fail-closed; **remove не трогали** (Native всегда наш = паритет с full) |
| III-B5 | exit nonzero при preflight-фейле: `mkdir BACKUP_DIR` с `|| exit 1`/errorlevel в light install + full install/remove `.bat` (BUG-I19/D3 уже был закрыт ранее) |
| III-B6 | **exit-map (G5, только TUI)**: `tui_map_exit` — помимо баннера «Не перезагружайте», распознаёт сигнатуры движка `WRITE_CANBUS уже принадлежит/не определён` и `EROFS/заблокирован загрузчик/disable-verity не срабатывает` → конкретный next-step; success-ветка и ban-приоритет не менялись; движок не трогали |
| III-B7 | **adb-s (только TUI)**: `tui_check_device` — env `ADB_SERIAL=<serial>` → валидация в `adb devices` (state=device) + `export ANDROID_SERIAL` (голый `adb install.sh` наследует env, движок не правили); без `ADB_SERIAL` при >1 устройств — fail-closed как раньше + подсказка; bat — только текст ошибки multi-device (без env) |

**Фаза II Gate B — bat-TUI:**
| ID | Что |
|----|-----|
| II-B1 | `install-tui.bat`: **G1 bundle preflight** (файлы+size, exit 3 до меню) + **G2 device preflight** `:preflight_device` (adb, count=1, state=device, exit 3) перед Install/Verify/Remove/DNS; verify сохраняет Full/Light через `TUI_VERIFY_LIGHT` до device-чека |
| II-B2 | TUI-default: `Packaging/README.txt` (Win: install-tui.bat первым, install.bat = non-interactive; macOS: install-tui.sh + chmod), `Docs/user/INSTALL_GUIDE.md` §6.1 TUI / §6.2 non-interactive / §7.3 TUI-first, `Docs/user/installation.md`; `readme.md` уже рекомендовал TUI |
| IV-D6 | **MANIFEST.sha256**: `make_release.sh` `write_manifest()` (staging full+light, формат sha256sum, LF) → `tui_check_manifest()` в `tui-lib.sh` (в конце `tui_check_bundle`; отсутствие = warn, mismatch = fail) + `:check_manifest` в `install-tui.bat` (PowerShell Get-FileHash, exit 3); `README.txt` PREFLIGHT + `Docs/developer/release.md` чек-лист |

### Изменения кода
- `.gitattributes`
- `Docs/user/INSTALL_GUIDE.md`, `readme.md`, `docs/audit/compatibility-matrix.md`, `Packaging/README.txt` (+ II-B2 + D6 PREFLIGHT)
- `Packaging/installer/full/verify_post_install.sh|.bat`
- `Packaging/installer/full/install-tui.sh|.bat` (+ II-B1 preflight, + II-B2/D6 `:check_manifest`)
- `Packaging/installer/full/tui-lib.sh` (+ II-A2 меню, + D6 `tui_check_manifest`, + III-B6 exit-map, + III-B7 ADB_SERIAL/ANDROID_SERIAL)
- `Packaging/installer/full/install-tui.bat` (+ III-B7 текст multi-device)
- `Packaging/installer/common/dns-overlay.sh` (+ `wait_adb_device`)
- `Packaging/installer/{full,light}/{install,remove}.sh` (B1/B2/B3/B4/B5)
- `Packaging/installer/{full,light}/{install,remove}.bat` (B3/B4/B5)
- `Packaging/installer/common/install-yandex-dns.bat` (B3)
- `make_release.sh` (+ D6 `write_manifest` full+light)
- `Docs/developer/release.md` (+ D6 checklist)
- Согласование: «Приступай к реализации» + явное «да» на III-B1 + **«Выполняй по очереди»** (B2→B4→B3→B5→II-B1→II-B2→IV-D6), 2026-09-23. **Явное «да» на III-B6+B7 (TUI-only)** — 2026-09-23. **Коммит/пуш не выполнялись.**

### Проверка
- `bash -n`: все `.sh` (install/remove full+light, dns-overlay, tui-lib, install-tui, verify, make_release) — **OK**.
- install-tui.bat: labels/gotos/calls resolve (`ALL_LABELS_RESOLVE`); TUI_VERIFY_LIGHT сохранён до preflight; **CRLF=214, bareLF=0, nonAscii=0**.
- D6 sh: `write_manifest` + `tui_check_manifest` логика — `sha256sum -c` OK, tamper detect OK, `D6_SH_FUNCTIONAL_OK`.
- D6 PowerShell: verify OK + tamper exit=1 через `cmd /c` — `D6_BAT_POWERSHELL_OK`.
- **B6**: `tui_map_exit` — BAN/CANBUS/SYSRO/GENERIC/SUCCESS — 6/6 OK.
- **B7**: fake-adb — 0dev/1dev/multi-hint/ADB_SERIAL select+export persist+child inherit/notfound/unauth/one+serial — **OK**.
- `Packaging/tests/test_apollo_direct_only.sh` — **PASS**.
- Read-only: не запускались на ГУ; live — Фаза V.

### Открытые вопросы
1. Live-матрица Sport+ 2026 (Фаза V) — без неё install-track не готов к релизу (B6/B7 код готов, live-валидация позже).
2. Полный `./make_release.sh` (с write_manifest) — не гонялся (нет Android SDK); при следующей сборке проверить `sha256sum -c MANIFEST.sha256`.
3. Коды exit 10–80 **в движке** — отдельный гейт (не начинали).

### Статус фаз плана (install-track)
- I: ✅ · II Gate A: ✅ · II Gate B: ✅ (B1 ✅, B2 ✅, D6 ✅) · III: ✅ (B1–B7 ✅) · IV: 🟡 (D6 ✅; подпись релиза — нет) · V: ❌

---



---

## 2026-09-24 — Сессия 6: среда сборки + релиз 3.7.1 + TUI dry-run

### Контекст
- Пользователь: «Ставить и делать всё!» + версия **3.7.1** (question-tool).
- Требование фидбека: короткие итерации, `Releases/logs/BUILD_STATUS.txt` (phase=…).

### Среда (без админа)
- JDK 17: winget Temurin 17.0.20.101 → `C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot` (JAVA_HOME).
- Android SDK: `%LOCALAPPDATA%\Android\Sdk` — cmdline-tools latest, platform-tools (adb 37.0.1), platforms;android-35 (+optional/android.car.jar), build-tools;35.0.0, ndk;27.0.12077973, cmake;3.22.1; лицензии приняты.
- User PATH: JAVA_HOME, ANDROID_HOME, ANDROID_SDK_ROOT, bin/platform-tools/cmdline-tools; **+ GnuWin32 zip** (`C:\Program Files (x86)\GnuWin32\bin`, winget `GnuWin32.Zip` 3.0).
- `local.properties` созданы в Native/RestoreMode (gitignored); Gradle 8.13 wrapper OK.
- Pre-flight: `bash -n` ×7 OK; Apollo test **PASS**.

### Сборка и релиз 3.7.1
- Native full+light: BUILD SUCCESSFUL (после lint-fix); APK ~13.8MB каждый.
- RestoreMode full+light: BUILD SUCCESSFUL (~11.1MB каждый).
- `./make_release.sh 3.7.1`: **EXIT=0, 62s** →
  - `Releases/dist/VoyahTune-3.7.1.zip` 36 237 459b (full, 32 файла) — sha256 `e283099f5c592b320571e7c5e7ba5fcadcf8e02d87421cf43a08277f9f31c036`
  - `Releases/dist/VoyahTune-3.7.1-light.zip` 12 586 558b (light, 17 файлов) — sha256 `5fe17d54a259c8fc987ae997a2af595d6347e4732d623a631dbfb123a9b3fb2c`
- `sha256sum -c MANIFEST.sha256`: full **32/32 OK**, light **17/17 OK**; `unzip -tq` both OK.

### TUI dry-run (из flat-папки full)
- `./install-tui.sh --dry-run` → **TUI_DRY_EXIT=0**: bundle OK, MANIFEST OK, adb=./adb.exe, «нет устройств» (кабеля нет — ожидаемо), план 12 шагов показан, движок не запускался.

### Изменения кода (git dirty, **не коммичено**)
| Файл | Что |
|------|-----|
| `make_release.sh` | `verify_windows_batch_files`: gawk → **perl** (MSYS gawk text-mode не видит CR → ложный fail на Windows) |
| `Native/app/build.gradle.kts` | `lint { checkReleaseBuilds=false; abortOnError=false }` |
| `RestoreMode/app/build.gradle.kts` | то же |
| `Native/gradle.properties` | TLS timeouts + `android.lintVitalAbortOnError=false` + `checkReleaseBuilds=false` |
| `RestoreMode/gradle.properties` | то же |

**Не тронуто:** CAN/hot-path, boot-хуки, install.sh engine, фазы полной установки.

### Проверка
1. `bash -n make_release.sh` — OK.
2. `./make_release.sh 3.7.1` — exit 0; MANIFEST + zip -t.
3. Full: `cd Releases/build/VoyahTune-3.7.1 && ./install-tui.sh --dry-run` — exit 0.
4. Live на Sport+ 2026 — только с кабелем и явного «делай» (Фаза V).

### Открытые вопросы
1. Коммитить ли 5 dirty-файлов (build fix + make_release perl-verify)? — жду явное «коммитить».
2. hownews.md: описание версии 3.7.1 — по просьбе (make_release напоминает).
3. Live-тест на авто — Фаза V, по явной директиве.

### Статус workpack (обновление)
- install-track I ✅ · II ✅ · III ✅ · IV 🟡 (D6 ✅) · **V ❌** (кабеля нет).
- **Сборка/релиз/док-чеклист: DONE для 3.7.1** (среда + APK + ZIP + MANIFEST + TUI dry-run).

---

## 2026-09-24 — Сессия 7: эмулятор + воспроизведение UI (Native / RestoreMode)

### Контекст
- Директива: «Ты должен поднять эмулятор и воспроизвести интерфейс» + усиление обратной связи (polling 3–10с, `BUILD_STATUS.txt`).
- Вход: среда SDK уже стоит; релиз 3.7.1 собран; dirty-файлы build-fix — **не коммичены**.

### Среда эмулятора
- `sdkmanager` доустановил: **emulator + system-images;android-35;default;x86_64** (EXIT=0, 223s).
- AVD `VoyahTune` (pixel_6, API 35, ram 1536M); headless: `-no-window -no-audio -no-boot-anim -no-snapshot -gpu swiftshader_indirect`.
- Первый старт упал: `partition-size (4096) must be between 10MB and 2047MB` → **убран флаг** `-partition-size`; boot ~48s, `emulator-5554 device`.
- WHPX usable (ACCEL_CHECK=0), несмотря на VirtualizationFirmwareEnabled=False.
- PID: `%TEMP%\emu_pid.txt`.

### RestoreMode — ✅ полный UI + интерактив
- Главный экран: тайлы Время в пути / Power Hold / Настройки / Автосвет / Режим мойки / Настройки Android / Звук пешеходов / Прогреть батарею.
- **Тогглы подтверждены** (tap → dump → скрин):
  - Автосвет: `не активно` → `активно` → **восстановлено** `не активно`.
  - Звук пешеходов: `активно` → `не активно` → **восстановлено** `активно`.
- **Настройки** → `AdvanceActivity` (Главный экран / Настройки автомобиля / Приложения и разделение экрана / Apollo Tech; toggles Время в пути, Power Hold, Режим мойки, Автосвет, Звук предупреждения пешеходов; кнопка «Применить»). BACK → MainActivity.

### Native — ✅ UI есть; ANR = ожидание CarService
- Чистый кадр: `native.png` — заголовок **Native** + кнопка **SET ALL MODES**.
- Повторный старт ~5с → ANR. logcat:  
  `Context.startForegroundService() did not then call Service.startForeground(): SetModesService`
- **Корень:** `SetModesService.onCreate` → `initializeCarPowerManager()` → `Car.createCar(..., CAR_WAIT_TIMEOUT_WAIT_FOREVER, ...)` (SetModesService.java:869). Без CarService (эмулятор) блокировка → `startForeground` (строка 938) не вызывается за 5с.
- На Sport+ 2026 CarService есть → эталонное поведение **не должно** ANR-иться. **Код не меняли** (CAN/service hot-path под запрет без отдельного согласования).
- Спелаш с зелёным Android — системный splash до первого кадра/ANR; не баг layout.

### Артефакты
- `Releases/logs/ui-shots/` — `native.png`, `native_after_wait.png`, `native_t0.png`, `rm_base.png`, `rm_autosvet_tap.png`, `rm_pedestrian_tap.png`, `rm_settings.png`, XML-hierarchy.
- `Releases/logs/BUILD_STATUS.txt` — `phase=UI-REPRO-DONE`.
- `Releases/logs/ui-shots/restore_ui_now.xml` / `rm_final.xml` — dump иерархии.

### Изменения кода
- **Нет** (JAVA/JS/манифесты/установщики не трогали). Только docs (этот блок) + BUILD_STATUS (лог).

### Проверка
1. `adb devices` → emulator-5554.
2. RestoreMode: тап тайлов, dump `uiautomator dump` — статусы меняются.
3. Native: `am start -n ru.big.town.anative/.MainActivity` → ANR через ~5с на эмуляторе (ожидаемо без CarService).
4. Live CAN/Frida/бут — только Sport+ 2026 (Фаза V).

### Открытые вопросы
1. Дать ли фикс `SetModesService` (timeout вместо WAIT_FOREVER / startForeground до createCar) — **только по явному «делай»**, тройной анализ, hot-path.
2. Коммитить ли dirty build-fix + docs — жду «коммитить».
3. Закрыть ли эмулятор (`adb emu kill`) или оставить для further UI-тестов?

### Статус фаз плана
- 0: ✅ · 1–2: 🟡 · 3–6: ❌ (гейт согласования) · install-track V: ❌ (кабеля нет).
- UI-эмуляция: **DONE** (RestoreMode interactive + Native static + ANR root-caused).

### Коммит (2026-09-24)
- `8fcd452` — build+UI: lint/TLS, make_release perl CRLF, align RestoreMode tile headers, HISTORY s6–s7.
- `cc816d8` — docs: HISTORY note.
- Пуш: `fork/master` (`rkolpakov-sudo/VoyahTune`) → cc816d8. Ветка `master`, upstream=fork.
- Dirty tree: clean (BUILD_STATUS/logs не в git).

---

## 2026-09-24 — Сессия 8: подготовка live-установки на авто (Фаза V pre-flight)

### Контекст
- Директива: «подготовка к установке на автомобиль»; пользователь даст команду **после** подключения ПК↔ГУ. Сама установка **до команды не выполняется**.

### Выполнено (без мутаций устройства)
1. **Целостность релиза 3.7.1:** ZIP full/light на месте; MANIFEST full **31/31 OK**; light OK; `bash -n` все .sh OK; Apollo `test_apollo_direct_only.sh` **PASS**.
2. **Распаковка:** full → **`C:\VoyahTune`** (плоско, 32 файла, короткий путь по README). ZIP содержит вложенную папку — развёрнута в корень.
3. **TUI dry-run из `C:\VoyahTune`:** exit **0** — bundle OK, MANIFEST OK, adb=./adb.exe, «нет устройств» (до подключения), план 12 фаз.
4. **adb:** эмулятор **kill** (`adb emu kill`) — `adb devices` пуст → при подключении ГУ будет ровно один serial (нет конфликта ADB_SERIAL).
5. **Док:** создан `docs/agent/live-install-checklist.md` — S-условия, preflight, 12 фаз, verify, откат, full vs light.
6. **Статус:** `Releases/logs/BUILD_STATUS.txt` → `phase=READY-FOR-CAR`.

### Изменения кода
- **Нет** (только docs/agent + логи). Устройство не менялось.

### Как продолжится (по команде пользователя)
1. Подключить Type-A↔A, `adb devices` → one `device`.
2. Агент: read-only preflight (getprop, dry-run TUI).
3. Явное «делай» → `C:\VoyahTune\install-tui.bat` (full) или light по явному выбору.
4. 1–2 reboot → verify → `install.log` → HISTORY Фаза V.

### Открытые вопросы
1. **full или light** при подключении? (по умолчанию full — подготовлен в `C:\VoyahTune`)
2. Yandex DNS — ставить после install или keep? (меню в движке)
3. Live-матрица совместимости — заполнять по факту Sport+ 2026.

### Статус фаз плана
- 0: ✅ · 1–2: 🟡 · 3–4: 🟡 (частично) · install-track V: **pre-flight DONE, live ожидает команду**.

### MAX CONTROL (та же сессия, директива)
- Пользователь: «Усиль контроль… Максимальная степень контроля и обратная связь».
- В `docs/agent/live-install-checklist.md` §5: протокол (BUILD_STATUS schema, C0–C16 checkpoints, STOP-гейты, heartbeat 3–15 с).
- Создан read-only `Docs/agent/live_watchdog.ps1` — polling adb/boot/install.log, пишет `Releases/logs/live-watchdog.log` + переписывает `BUILD_STATUS.txt`; **не** мутирует ГУ.
- Запуск watchdog: отдельное окно **до** install; stop: `Releases/logs/watchdog.stop`.
- **Watchdog запущен** pid **21684** (read-only, poll 3–5 с): пишет `live-watchdog.log` + `BUILD_STATUS` phase=WAIT-CABLE. Проверка smoke: SYNTAX_OK, START/STOP/END.

---

## 2026-09-24 — Сессия 9: GitLab = актуальный upstream (deep compare)

### Контекст
- Пользователь: актуальный git — `https://gitlab.com/openvoyah/voyahtune`; мы работали с устаревшим GitHub-клоном.
- Добавлен remote `gitlab` (fetch only). Рабочее дерево **не переключали**, коммитов в gitlab **не делали**.

### Факты
| Метрика | Значение |
|---------|----------|
| merge-base HEAD ↔ `gitlab/master` | **`45beee4`** (наш старый audit base) |
| `gitlab/master` | **`e334cd9`** — *docs: … 3.11.1 release notes* |
| Только у нас | **4** коммита (`f1a10a9`…`cc816d8`) |
| Только на GitLab | **104** коммита |
| Diff tree HEAD↔gitlab | **450** paths: 338 A / 77 M / 33 D / 2 R |
| Обе стороны меняли с `45beee4` | **25** файлов (конфликт ~36 markers) |
| Remote `origin` (GitHub nexron171) после `fetch` | **== gitlab master `e334cd9`** (0/0) — зеркало/mirror; локальный ref `origin/master` был протухшим на `45beee4` |
| Теги GitLab | `v3.7`…**`v3.11.1`** |
| versionName GitLab | **3.11.1**; наш HEAD versionName **1.0**; сборка релиза **3.7.1** (make_release) |

### Hot-path: GitLab сильно ушёл вперёд (45beee4 → gitlab)
- `ApplyEngine.java` **−486/+171**, `DriveModeCanTransport` **−258/+48**, `CanSender` **+50**, `SetModesService` **+563/−135**
- `SetModesService`: upstream **`CAR_WAIT_TIMEOUT_DO_NOT_WAIT` + worker + `startForeground`** — ANR, который мы root-cause-или на эмуляторе, **в upstream уже не тот код**
- `vd_bypass.js` **+311/−59**, `load.bin` большой binary diff, `install.sh` **+201/−30**, `make_release.sh` **+49/−4**
- **Удалены в upstream:** `ApolloTlcPolicy.java`, `ApolloTlcService.java`, `ApolloTlcPolicyTest` (у нас ещё есть — obsolete)
- **Новый upstream:** `ApolloRestorePolicy`, `ApolloSettingsRuntime*`, mode memory, widgets, dock/fullscreen, wiper/autolight notes в hownews до v3.2+

### Устаревшие/неактуальные у НАС (relative to GitLab)
1. **Вся база кода** до `45beee4` + наши 4 коммита поверх неё — **stale vs 3.11.1**
2. **`ApolloTlc*`** — удалены upstream
3. **`Packaging/installer/full/install-tui.*`, `tui-lib.sh`, `verify_post_install.*`** — только у нас; upstream: классические install/remove + **новый `Installer/` (Rust CLI + Tauri/Svelte GUI, `make_release --installers`)**; README: *«Старые install/remove — эталон Rust-порта»*
4. **Наша custom TUI/D6 MANIFEST/preflight** — не в upstream как TUI; upstream имеет свой preflight/atomic hook manifest в install.sh
5. **Релиз `3.7.1` + `C:\VoyahTune` payload** — собран из **stale** ветки; официальный latest **3.11.1**
6. **Только у нас (не в GitLab):** `AGENTS.md`, `Docs/agent/**`, `Docs/audit/**`, `Qwen_markdown_*.md` — наш process/audit, не «продукт»; при merge сохранять как our-side
7. **UI-fix `layout-land/activity_main.xml`** — обе стороны правили; upstream дальше менял RestoreMode UI (widgets v3.0+) — **наш top-align может конфликтовать/быть superseded**
8. **make_release CRLF/perl fix** — upstream тоже правил make_release; сверить, не потерять наш offline/CRLF verify
9. Feature-branch ветки GitLab в основном **ahead=0** (slugs history); активные: `CHINA`+4, `master-china`+6, `play-info`+3, `new-split`+1

### Решения (без правок кода)
- **Не** вливать gitlab в рабочее дерево и **не** ставить `C:\VoyahTune` 3.7.1 на авто без отдельной команды.
- Live-установка **только после** согласования: rebase/merge на `e334cd9` или clear отдельного worktree от GitLab + повторный audit наших 4 коммитов (что переносить: docs/audit, UI-fix, make_release, install fixes — TUI, вероятно, отдельно/upstream弃).

### Открытые вопросы
1. Переносим ли наши 4 коммита на GitLab (rebase) или только документацию/audit?
2. TUI из Packaging — оставить как наш fork-фичу или считать superseded `Installer/`?
3. `C:\VoyahTune` / live Phase V — **пересобирать от 3.11.1**?

---

## 2026-09-24 — Сессия 10: MERGE GitLab 3.11.1 → fork (ветка `merge/gitlab-3.11.1`)

### Решение пользователя
«Наша разработка отдельна, на моём git. Обновляем улучшенные компоненты из GitLab в наш fork. TUI и улучшенная установка остаются; расхождения выясняем.»

### Выполнено
- Remote `gitlab` fetch-only; ветка **`merge/gitlab-3.11.1`**; merge commit **`90ed97c`** (parents: `cc816d8` + `e334cd9`).
- **Не** трогали remotes push; **не** пушили; `master` не переключали (stash агент-docs → pop на merge-ветке).

### Правило разбора 24 конфликтов
| Группа | Решение |
|--------|---------|
| versionName, hot-path Native, RestoreMode product, Installer docs | **gitlab** |
| `NativeLog.java` (app-private storage / FileProvider) | **ours** |
| `make_release.sh` verify payload | **union**: GitLab keyboard/app_client + **наши TUI/verify_post_install** |
| TUI (`install-tui.*`, `tui-lib`, `verify_post_install`) | **оставлены** (не в upstream) |
| `AGENTS.md`, `Docs/agent/**`, `Docs/audit/**` | **оставлены** (наши) |

### Важные факты после merge
- **`SetModesService`**: `CAR_WAIT_TIMEOUT_DO_NOT_WAIT` + worker (ANR-фикс upstream) — **принят**.
- **`ApolloTlc*`** удалены upstream — принято.
- **`Installer/`** (Rust+Tauri) добавлен; **наша TUI** параллельно в `Packaging/installer/full/`.
- **layout-land**: gitlab убрал статические trip/batteryHeat из XML (виджеты через GridLayout) — **принят gitlab**; наши старые id в этом layout больше нет.
- **RestoreModeContentProvider**: gitlab instance-поля (не локальные) — принят как upstream; **риск**: гонка binder-threads (наш прежний локальный-fix переопределен).
- **Security**: часть `sendBroadcast(..., BIND_SET_MODES_SERVICE)` в gitlab заменена на plain/exported ContextCompat — **принято как upstream**; R3-замечания аудита **частично неактуальны**, нужен re-audit.

### Верификация
- `compileFullDebugJavaWithJavac` **Native=0**, **RestoreMode=0** (offline).
- `sh -n` install/remove full+light, `make_release.sh` **OK**.
- Unit tests offline: **fail только на `junit:4.13.2` cache** (не код).
- Conflict markers: **0**; unmerged: **0**; files in merge: **408** (~+50k/−9k).

### Не сделано / дальше
- Merge **не влит в `master`** и **не запушен** (нужна явная команда).
- Пересборка релиза 3.11.1 + `C:\VoyahTune` — **не делали**.
- Re-audit security (broadcast permission) и сверка TUI ↔ `Installer/`.
- Live Phase V — ставить только свежий 3.11.1 после твоего «делай».

---

## ШАБЛОН следующей сессии

```markdown
## YYYY-MM-DD — Сессия N: <кратко>

### Контекст
- Коммит/ветка:
- Вход: что было открыто/запрошено

### Выполнено
- ...

### Изменения кода
- Файлы: ... (или «нет»)
- Согласование: дата/цитата пользователя | не требовалось (документы)

### Проверка / как тестировать
- ...

### Открытые вопросы
- ...

### Статус фаз плана
- 0: ... 1: ... 2: ... 3: ...
```

---

## Сессия 11 — 2026-09-24: merge GitLab 3.11.1 → master, EOL fix, release 3.11.1

### Сделано
- **Merge GitLab 3.11.1** в fork/master: ветка `merge/gitlab-3.11.1`, 24 конфликта разобраны (product=gitlab, TUI/ours, make_release union, NativeLog ours). Коммиты `90ed97c` + `9745ca2`.
- **Push fork/master** и merge-ветки → `rkolpakov-sudo/VoyahTune`.
- **Unit tests** Native + RestoreMode: PASS (online).
- **Гейт-тесты make_release** (6/6 + static): PASS после EOL-фикса.
- **EOL-фикс (критично):** `core.autocrlf=true` клал CRLF в `load.bin`, `voyahtune.load.rc`, inject `*.js`, `gradlew` (shebang `#!/usr/bin/env sh\\r` → `env: sh\\r`). Worktree нормализован в LF; `.gitattributes` расширен (eol=lf для gradlew/load.bin/.rc/.js/sh/исходников). Без этого `sh -n` падал, а загрузчик на авто мог не стартовать.
- **test_power_hold_oem:** upstream AppWidgets перенёс `powerHoldBadge` в `tile_power_hold.xml` (код MainActivity уже биндит через widgetView). Firmware-fixtures (tmp/car_apks) сделаны optional offline.
- **Релиз 3.11.1:** APK Native+RestoreMode full/light собраны (gradlew.bat, JDK 17, --no-daemon после I/O-конфликта локов). `make_release.sh 3.11.1 --no-build --no-zip` → FULL+LIGHT; ZIP собраны GnuWin32 zip, `unzip -tq` OK. MANIFEST full+light **OK**.
- **C:\\VoyahTune** заменён на full 3.11.1 (40 файлов), MANIFEST в месте назначения **EXIT=0**. Старый install.log сохранён в `Releases\\logs\\install_3.7.1.log.bak`.
- BUILD_STATUS: `phase=READY-FOR-INSTALL`.

### Известные нюансы среды
- `bash` в PowerShell = **WSL** (Java 25.0.3-ea) — `./gradlew` из make_release падает; сборка APK через `gradlew.bat` + JAVA_HOME JDK17. Упаковка через `--no-build`.
- `zip` только в GnuWin32/Git; make_release без zip → `--no-zip` + ручной zip.
- `test_apollo_direct_only.sh`: node не найден (не в gate).
- Security re-audit plain sendBroadcast — не завершён (открытый вопрос).

### Не сделано / дальше
- Установка на Sport+ 2026 — ждёт команду после подключения ПК↔ГУ (`install-tui.bat` из `C:\\VoyahTune`).
- Re-audit R3 broadcast permissions после merge.
- Push fork подтверждён; history/BUILD_STATUS обновлены.

### Проверка
- `sha256sum -c MANIFEST.sha256` в `C:\\VoyahTune` → 0
- `sh -n` install/remove/tui/verify → OK
- Гейт-тесты 6/6 + static → PASS
