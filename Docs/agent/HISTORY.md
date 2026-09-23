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
