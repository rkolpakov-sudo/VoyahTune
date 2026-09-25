# VoyahTune — ЕДИНЫЙ ПЛАН РЕФАКТОРИНГА

**Версия документа:** 1.0 · **Дата:** 2026-09-23  
**Статус:** актуален; **эталон исправности:** Voyah Free Sport+ 2026 (OD Sport+ 26)  
**База кода:** `master` @ `45beee4` (+ незакоммиченные артефакты аудита/D2)  
**Приоритет инструкций:** `AGENTS.md` (аксиомы) → **этот документ** → общие правила opencode.

> Документ сводит: исходный план 7 фаз, Фазу 0 (аудит), глубокое ревью (BUG/ARCH/UX/R1–R26), дизайн TUI установки (D0–D7) и журнал `HISTORY.md`.  
> **Код менять только после явного «да» по пункту** (`AGENTS.md` §2.2–2.3).

---

## СОДЕРЖАНИЕ

1. [Аксиомы и красные линии](#1-аксиомы-и-красные-линии)
2. [Сводка проекта](#2-сводка-проекта)
3. [Статус работ (дашборд)](#3-статус-работ-дашборд)
4. [Каталог находок](#4-каталог-находок)
5. [Единый дорожная карта (tracks)](#5-единая-дорожная-карта-tracks)
6. [Пакеты работ (WP) с гейтами согласования](#6-пакеты-работ-wp-с-гейтами-согласования)
7. [Чего НЕ делать](#7-чего-не-делать)
8. [Тестирование и приёмка](#8-тестирование-и-приёмка)
9. [Журнал решений](#9-журнал-решений)
10. [Следующие шаги](#10-следующие-шаги)
11. [Карта источников](#11-карта-источников)

---

## 1. Аксиомы и красные линии

### 1.1. Аксиомы (пользователь, 2026-09-23 — только пользователь может менять)

1. **Эталон:** текущая реализация **функциональна и работает** на **Voyah Free Sport+ 2026**. Единственная целевая машина.
2. **Сохранение работоспособности > всё.** Запрещён рефакторинг «ради чистоты». Изменения кода только ради (а) функционала, (б) задокументированного бага, (в) безопасности — и только после **явного** согласования.
3. **Сверка** с `Docs/CAN-команды.odt`, `Docs/can.pdf`, `readme.md`, `hownews.md`, `Packaging/README.md`, поведением кода. Никаких CAN/policy/транспорта/бута «по интуиции».
4. **Нет полной уверенности → не менять, задать вопрос.**
5. **Тройной анализ** перед каждым изменением файла: (1) назначение/точки вызова, (2) impact на hot-path, (3) сверка с эталоном и справочниками. Формат ответа — в `AGENTS.md` §4.

### 1.2. Что можно без отдельного код-согласования

- Чтение/анализ; документация `docs/**`, README; read-only тесты; `HISTORY.md`.

### 1.3. Что только после явного «делай» по пункту

- Любой `.java`, `.js` (Frida), `.sh`/`.bat`, `.xml`, `.rc`/`.bin`, Gradle.  
- В т.ч. security-fix из аудита (R3, R4, R9, R12…).  
- Фазы 3–6 исходного плана — **по одному пункту**.

### 1.4. Категорически не трогать без отдельного целевого проекта и живых тестов

| Объект | Причина |
|--------|---------|
| `CanSender`, `ApplyEngine`, `*CanTransport`, `*CanPolicy` | hot-path CAN → риск для автомобиля |
| `native-lib.cpp`, JNI (`cis_can_control_bytes`) | то же |
| `vd_bypass.js`, `load.bin`, `voyahtune.load.rc/.sh` | system_server / бут → крэш = потеря ГУ |
| Порядок и логика фаз `full/install.sh` | атомарность, две перезагрузки |
| Вынос в AIDL, «модульность», minify/R8 release без keep | не даёт функции, ломает проверенное |
| API `RECEIVER_EXPORTED` «вслепую» | сначала фактический API ГУ (R20) |
| Включение Apollo master / `canChangeApolloMaster()→true` | ADAS-entitlement |

### 1.5. Ограничения железа/прошивки (из плана и PROJECT.md)

| Ограничение | Значение |
|-------------|----------|
| Кабель | только USB **Type-A ↔ Type-A**, USB 2.0; Type-C не подходит |
| ADB | `root`, `disable-verity`, запись `/system`; Wi-Fi ADB только дорестайл |
| Перезагрузки | **две** (verity при необходимости + финальная) |
| CAN | кадры ровно 10 байт; TX 77/58/46/36/28 |
| Прошивки | 4.1 OD: энергия+рекуперация; 7.1 OD: только рекуперация (OEM-баг) |

---

## 2. Сводка проекта

### 2.1. Что это

**VoyahTune / Open Voyah** — Android-система для Voyah Free: восстановление режимов после пробуждения, автоматизации, UI, full-only Frida (сплиты, док, кнопки руля), Apollo/ADAS TLC.

| Флейвор | light | full |
|---------|:-----:|:----:|
| Режимы, автосвет, дворники, прогрев, Direct Apollo | ✅ | ✅ |
| Frida, сплиты, док, руль, boot-hook `load.bin` | ❌ | ✅ |
| Запись `/system`, `adb root` | ✅ | ✅ |

### 2.2. Архитектура (одна строка)

`RestoreMode` (UI/prefs) ⇄ Messenger/Provider/Broadcast ⇄ `Native` (SetModesService, ApplyEngine, ApolloTlc, CanSender → JNI → CAN) + Settings.Global; full: Frida in-process + `voyahtune.*.rc` + `load.bin`.

### 2.3. Установка (12 фаз full, не ломать)

Локальный preflight → Apollo safe-keys → CANBUS owner → disable-verity [/reboot #1] → remount → backup → Frida/JS → boot-hook (+legacy migrate) → Native/priv-app/leavecar/freeform/RestoreMode → DNS menu → reboot #2 → *(TUI: verify read-only)*.

### 2.4. Сильные стороны (не «лечить»)

- `CanSender` fail-closed + lock; policy-классы + 82+ unit-тестов  
- Fail-closed Apollo schema; epoch/coverage ApplyEngine  
- Пер-файловая атомарность и snapshot/rollback boot-hook; идемпотентный re-run  
- `test_apollo_direct_only.sh`; signature-permission уже на части IPC  

### 2.5. Стек

Java ~70% · shell/bat ~17% · Frida JS ~12% · Gradle full/light · minSdk 30 · targetSdk 35 · `minify=false`.

---

## 3. Статус работ (дашборд)

| Этап | Источник плана | Стодокументы | Код | Заметки |
|------|----------------|--------------|-----|---------|
| **Фаза 0 — аудит** | исходный план | ✅ `docs/audit/*` (8 файлов) | — | чтение + риски R1–R26 + matrix draft |
| **Повторное глубокое ревью** | доп. запрос | ✅ `deep-review-2.md` | — | BUG N/R/I, ARCH, UX-S/R |
| **Аксиомы + agent-файлы** | запрос | ✅ `AGENTS.md`, `PROJECT.md`, `HISTORY.md` | — | — |
| **Дизайн TUI (D0/Q1–Q6)** | запрос UX install | ✅ `design-install-tui.md` | — | решения Q зафиксированы |
| **TUI D2 (обёртка)** | «Continue» + Q | ✅ | ✅ **новые файлы** | движок install **не тронут** |
| **Фаза 1 — документирование** | план | ✅ docs/user+tech+dev, FAQ/README | — | каркас готов; matrix живыми тестами позже |
| **Фаза 2 — тесты + matrix** | план | черновик matrix | `verify_post_install` partial | **нет живых тестов на авто** |
| **Фаза 3 — безопасность install** | план | design D3–D6 | ✅ **D3 I19 + D4 I04/I05 + D6 MANIFEST** | live D2/D3/D4/D6 не гонялись на авто |
| **Фаза 4 — баги поведения** | план + deep-review | каталог есть | ✅ **C1 + C2 partial + N02** | R3/R4/N01 = отдельный гейт |
| **Фаза 5 — UX/UI feedback** | deep-review | каталог есть | ❌ | после live-тестов Ф4 |
| **Фаза 6 — новые функции** | план | — | ❌ | опционально, в конце |
| **Security IPC (R3/R4…)** | audit | risk-assessment | **нет** | отдельный гейт + живой тест |
| **Light TUI / D4 / MANIFEST** | design | design | **D6 ✅** (light TUI / подпись — нет) | light TUI после D2 на авто |

**Согласованных изменений «горячего» кода (CAN/бут/install-фаз):** **0**  
(2026-09-23 «Приступай» = да на Ф1+D3+D4+C1/C2/N02; **N05** — только порядок `MSG_RESULT`, байты CAN не менялись; R3/R4/N01 — отдельный гейт.)  
**Новые файлы D2:** 5 шт. + 3 правки docs/packaging. **Ф1 docs:** 7 новых + FAQ/README/README.txt.  
**Live-прогон на Sport+ 2026 для TUI/installer/Java-фиксов:** **не выполнен**.  
**Gradle unit tests:** не гонялись (нет Android SDK в среде).

---

## 4. Каталог находок

### 4.1. Риски R1–R26 (сводка — полная матрица: `docs/audit/risk-assessment.md`)

| Приоритет | R# | Суть | Когда трогать |
|-----------|-----|------|----------------|
| **Критические** | R1 | ошибка CAN-policy/transport | только отдельный проект + авто |
| | R2 | крэш `vd_bypass` в system_server | не трогать hot-path |
| | R3 | Provider exported без permission | гейт + тест Native⇄RestoreMode |
| | R5 | `debugMode` fail-open | release-guard, по «да» |
| | R7 | `setenforce 0` на буте | **документировать** (арх. решение), не «чинить» молча |
| | R22 | нет подписи релизов | Фаза 3 |
| | R24 | нет CAN-тестов | Фаза 2 |
| | R25 | risk disable-verity/гарантия | README warnings |
| **Высокие** | R4 | 5 receivers без permission | гейт + тест |
| | R6 | гонка JNI vs Binder | отдельный проект |
| | R8, R9 | install partial state; light push без rc | R9=I05 — must-fix installer |
| | R20 | `RECEIVER_EXPORTED` API 33 @ minSdk 30 | **сначала** `getprop ro.build.version.sdk` |
| | R23 | bus factor / docs | Фаза 1 |
| **Средние** | R10–R19, R21 | OTA/Frida/log/backup/NPE… | инкрементально |
| **Низкие** | R26 | `push.sh` guard | можно позже |

### 4.2. BUG (deep-review-2) — по доменам

#### Native (10)

| ID | Severity | Суть | Кандидат в |
|----|----------|------|------------|
| N01 | High | log-receiver exported без permission | security/UX трек |
| N02 | High | лог в world-readable `/sdcard/tmp` | security |
| N03 | Med | statics без volatile (UI/ApplyEngine) | Ф4 точечно |
| N04 | Med | `SAVE_CONTEXT` non-volatile | Ф4 |
| N05 | Med | star `MSG_RESULT` до CAN | Ф4 (CAN-touch: осторожно) |
| N06–N10 | Low | no-op, theme guard, empty star, init, catch-ignore | по «да» |

#### RestoreMode (13)

| ID | Severity | Суть | Кандидат в |
|----|----------|------|------------|
| **R01** | High | `RECEIVER_EXPORTED` API33 crash (=R20) | Ф4 после API ГУ |
| **R02** | High | portrait layout crash | Ф4 / UX-R11 |
| R03 | Med | `data.toString()` NPE | безопасный UI |
| R05 | Med | process death NPE StarButton | Ф4 |
| **R06** | Med | Provider state в полях → перемешивание колонок | security/корректность |
| **R07** | Med | CAN-форматтер `\n` (только отображение!) | Ф4, **валидацию %31 не ломать** |
| R09–R10 | Med/Low | leak handler; apolloPending без timeout | UX feedback |
| R12 | Low-Med | SplitStore частичный save | Ф4 |
| R04, R08, R11, R13 | Low | null/format/JSON/index | по «да» |

#### Installer / Frida (сводка 27 BUG-I)

| ID | Severity | Суть | Кандидат в |
|----|----------|------|------------|
| **I05=R9** | High | light `push` без `\|\| exit 1` | **D4 must-fix** |
| **I04** | High | light remove без RW-gate | **D4** |
| I03/I08 | High | sh↔bat drift (CANBUS/DNS) | синхронизация, по «да» |
| I01 | High | CRLF/LF в checkout | process/`.gitattributes` |
| I07 | Med | steeringwheelkeys без общего try/catch | только с авто |
| **I19** | Med | bat `exit 0` после `!!!` | **D3** |

### 4.3. ARCH (описывать, не «лечить» немедленно)

- ARCH-N01: три сериализации CAN без общего fence → **не трогать**  
- ARCH-N03: Messenger `MSG_*` голые int → единый контракт **без смены чисел**, позже  
- ARCH-R01/R02: provider/receivers без permission → =R3/R4  
- ARCH-I: sh↔bat drift, нет install-lib, логи без ротации → только по «да»

### 4.4. UX-каталог (безопасные)

**Docs/echo (низкий приоритет после TUI):** UX-S13 FAQ, S14 две перезагрузки, S16 README, S17 кабель в readme, S1 complete в sh *(частично закрыт TUI-сообщением)*, S2 единые «Do not reboot» *(частично в tui_map_exit)*.

**UI-feedback (car-risk None/Low):** UX-R1/R10 snack «Применить»/«сервис не готов», R2 таймаут, R3 ok-toggle, R6 пустые состояния, R7 таймер, R8 прогрев fail, R9 default switch, S9/S10 Apollo status, S11 notifications…

**Установка UX:** дизайн G0–G8, A1–A12 — **D2 реализован** (см. §6 WP-TUI).

### 4.5. Приоритетная шкала багов (согласована в сессии)

1. **Баги поведения на эталоне:** BUG-R06, R07, N03/N04, N05, R12  
2. **Краши UI:** R01, R02, R03, R09, R10  
3. **Безопасность IPC/логов:** N01, N02, R3, R4 — гейт + живой тест  
4. **Фаза 1–2 плана** (docs + чек-лист)  
5. **Must-fix installer:** I05, I19, I04, I01, I03/I08  
6. **Echo/docs UX** — самое низкое  

---

## 5. Единая дорожная карта (tracks)

Три независимых трека; **не смешивать** в одном PR: install-UX, поведенческие баги, IPC-security.

```
TR-A  INSTALL UX / TUI     →  D2 ✅  →  D3  →  D4  →  D6  →  light TUI (D7+)
TR-B  ФАЗЫ ИСХОДНОГО ПЛАНА →  0 ✅  →  1  →  2  →  3'  →  4  →  5  →  6
TR-C  SECURITY / BUGS      →  (согласование R3/R4/R9/…) →  мини-PR по одному
```

**Рекомендуемый порядок после TUI на авто (уровни):**

| # | Уровень | Что | Гейт |
|---|---------|-----|------|
| 1 | Баги поведения Sport+ | R06, R07, N03/N04, N05, R12 | «да» на пункт + тройной анализ |
| 2 | Краши UI | R01–R03, R09–R10 | API SDK R01; «да» |
| 3 | Security IPC | N01, N02, R3, R4 | отдельный «да» + живой тест |
| 4 | Фаза 1 docs | user/tech/developer | docs можно без код-гейта |
| 5 | Фаза 2 tests | checklist, basic_check, matrix live | нужен доступ к авто |
| 6 | Installer must-fix | D3=I19, D4=I05/I04, I01, I03 | «делай D3/D4» |
| 7 | UX echo/docs | S13–S17 | низкий |
| 8 | Фаза 3' hardening | backup manifest, codes, sign (D6) | после 1–6 |
| 9 | Фаза 6 features | termux, profiles… | только в конце |

**Исходный план (длительности — ориентир):** 0: 2–3 нед · 1: 1–2 · 2: 1 · 3: 1–2 · 4: 2–3 · 5: 1 · 6: 2–4 → **итого 10–16 нед** при живом доступе к авто.

---

## 6. Пакеты работ (WP) с гейтами согласования

### WP-0 Аудит — ✅ ГОТОВ

- Артефакты: `docs/audit/{code-review-*,architecture-diagrams,risk-assessment,compatibility-matrix,phase0-report}.md`
- Гейт закрыт: да.

### WP-A Docs Phase 1 — ⏳ ЧАСТИЧНО

| Задача | Статус |
|--------|--------|
| `PROJECT.md`, `HISTORY.md`, `AGENTS.md` | ✅ |
| README.txt упоминание TUI | ✅ |
| `docs/user/*` installation/features/faq | ❌ |
| `docs/technical/*` arch, CAN, boot, IPC contracts | ❌ (частично PROJECT) |
| `docs/developer/*` build/test/release | ❌ |
| Единые тексты «Do not reboot» / 2 reboot | ❌ (в TUI-слое частично) |
| Матрица совместимости → живые значения | ❌ |

**Гейт:** docs без кода — можно по «делай Фазу 1».

### WP-B Tests Phase 2 — ⏳

| Задача | Статус |
|--------|--------|
| Чек-лист ручного тестирования (из плана) | ❌ (черновик в плане) |
| `basic_check.sh` / e2e | ❌ |
| `verify_post_install.sh/.bat` (post-install read-only) | ✅ D2 |
| Заполнить matrix на ≥3 прошивках / Sport+ 2026 | ❌ |
| Расширение policy unit tests | не начато |

**Гейт:** нужен физический доступ к ГУ.

### WP-TUI Install UX — 🟡 D2

| Этап | Содержание | Статус |
|------|------------|--------|
| D0 | дизайн + Q1–Q6 | ✅ |
| D1 | docs-only UX тексты | 🟡 частично (README TUI) |
| **D2** | `tui-lib`, `install-tui.sh/.bat`, verify .sh/.bat, make_release full required, README | ✅ код новых файлов |
| D3 | fix **BUG-I19** exit-code в bat | ⏳ ждёт «делай D3» |
| D4 | **I05** light push rc, **I04** RW-gate remove | ⏳ |
| D5 | маркеры `[N/12]` в движке | ❌ **отклонено** (Q2) |
| D6 | `MANIFEST.sha256` + G1 в TUI | ✅ код (live не гонялся) |
| D7 | PowerShell TUI | ⏳ опц. |
| Live | прогон install-tui на Sport+ 2026 | ❌ |

**Согласованные решения D2 (Q1–Q6):** POSIX+bat menu · без правок движка · tee только в TUI · verify в D2 · DNS как есть · **только full**.

**Как проверять D2 (стенд):**  
1) из flat-папки релиза: `./install-tui.sh --dry-run` → plan, exit 0;  
2) `./install-tui.sh` → safety → preflight → confirm → install.log;  
3) `install-tui.bat` → menu 1;  
4) после успеха verify;  
5) `bash -n` + `test_apollo_direct_only.sh` PASS;  
6) **напоминание:** bat tee через PowerShell не проверен на живой Windows.

### WP-C Security / Bug-fixes — ⏳ ЖДЁТ «да» ПО ПУНКТУ»

| Пакет | Пункты | Предусловие |
|-------|--------|-------------|
| C1 Поведение | R06, R07, N03, N04, R05, R12 | тройной анализ, без CAN-байтов (R05 осторожно) |
| C2 Краши | R01 (после SDK!), R02, R03, R09, R10 | R01: `adb shell getprop ro.build.version.sdk` |
| C3 IPC security | R3, R4, N01, N02 | отдельное «да» + тест связи на авто |
| C4 Installer | I19 (D3), I05/I04 (D4), I01, I03/I08 | «делай D3/D4» |
| C5 UX UI | R1, R2, R3, R10, S11… | после C1–C2 |

**Ни один пункт C* не начат без явной реплики пользователя.**

### WP-D Phase 3' Hardening — ⏳

- Backup manifest (SHA-256, из исходного плана)  
- Стандартные exit codes 10–80 (движок — отдельный гейт; TUI-маппинг уже частично)  
- Подпись/`MANIFEST` релиза (=D6)  
- `--dry-run` **движка** (сейчас dry-run только в TUI-обёртке)

### WP-E Phase 4–6 — ⏳

- Ф4: инкрементальные баги из readme/FAQ (7.1 OD, долгий старт…) после WP-C  
- Ф5: logging helpers, progress — **пересекается с отклонённым D5**; только без правок фаз или с отдельным «да»  
- Ф6: Termux, профили устройств — в самом конце  

---

## 7. Чего НЕ делать

1. Не разбивать `install.sh` на модули «для чистоты» (атомарность + две перезагрузки).  
2. Не вводить общий CAN-fence JNI⇄Binder без живых тестов.  
3. Не включать Apollo master.  
4. Не ставить permission на provider/receivers без гейта и теста.  
5. Не трогать `vd_bypass`/boot-hook semantics без отдельного проекта.  
6. Не менять числа Messenger `MSG_*`.  
7. Не чинить валидацию CAN `%31` через изменение payload (только отображение R07).  
8. Не считать готовым TUI без прогона на Sport+ 2026.  
9. Не коммитить/пушить без явной просьбы.  
10. Не начинать Фазы 3–6 исходного плана «пачками» — только по пункту.

---

## 8. Тестирование и приёмка

### 8.1. Хост (без авто)

```text
bash -n Packaging/installer/full/{tui-lib,install-tui,verify_post_install}.sh
bash -n make_release.sh
bash Packaging/tests/test_apollo_direct_only.sh   # PASS
```

### 8.2. Установка (стенд Sport+ 2026)

| # | Шаг | Критерий |
|---|-----|----------|
| 1 | Полный ZIP, кабель A–A | файлы на месте |
| 2 | `install.bat` / `./install.sh` (эталон, без TUI) | 12 фаз, 2 reboot, сервисы после 2-й |
| 3 | `install-tui.sh --dry-run` | exit 0, план |
| 4 | `install-tui.sh` полный | log, backup, success |
| 5 | `verify_post_install` | exit 0 |
| 6 | Обрыв USB на фазе N | «не перезагружать» / re-run |
| 7 | `remove` → `install` | восстановление |
| 8 | Modes/energy/recuperation, руль, сплиты, док | как до установки TUI (регресс = эталон) |

### 8.3. Чек-лист ручного теста (минимум — из исходного плана)

- [ ] Подготовка: авто/прошивка/кабель  
- [ ] Установка: начало, reboot1, reboot2, приложение в меню  
- [ ] Восстановление: driveMode / energy / recuperation  
- [ ] Откат: remove → исходное состояние  
- [ ] **Доп. D2:** install.log создан; verify OK; bat menu работает  

### 8.4. Приёмка «фазы»

Отчёт в `HISTORY.md` по шаблону: задачи, результаты, риски, следующие шаги, статус фаз 0–6.

---

## 9. Журнал решений

| Дата | Решение | Кто |
|------|---------|-----|
| 2026-09-23 | Аксиомы Sport+ 2026, тройной анализ, гейт на код | пользователь |
| 2026-09-23 | Фаза 0 завершена read-only | агент |
| 2026-09-23 | Глубокое ревью — каталог BUG/ARCH/UX | агент |
| 2026-09-23 | Приоритет: баги поведения → краши → security → docs → installer → echo | обсуждение |
| 2026-09-23 | **Q1** TUI = POSIX + bat menu | пользователь |
| 2026-09-23 | **Q2** без маркеров фаз в движке | пользователь |
| 2026-09-23 | **Q3** tee только в TUI | пользователь |
| 2026-09-23 | **Q4** verify в D2 | пользователь |
| 2026-09-23 | **Q5** DNS как сейчас | пользователь |
| 2026-09-23 | **Q6** сначала full | пользователь |
| 2026-09-23 | D2 выполнен (новые файлы) | агент + Continue |

---

## 10. Следующие шаги

- **Готово (документация):** Ф1 docs + INSTALL_GUIDE (`Docs/user/INSTALL_GUIDE.md`) + FAQ/README + **install-track I** (preflight §11.1b, light/TUI, case, matrix R12, README.txt PREFLIGHT).
- **Готово (код):** D2 TUI, D3 I19, D4 light atomic/RW-gate, C1/C2/C3 partial, fix-all 2026-09-23 сессия 4, **install-track II Gate A** (verify `--light`, sh-TUI меню 5+dry, log-rotate, wait-hint), `.gitattributes` LF.
- **Публикация:** форк — https://github.com/rkolpakov-sudo/VoyahTune.
- **Дальше:** Фаза III остаток (B2–B5 по одному «да») → Gate B (bat-TUI preflight, TUI-default, MANIFEST) → **Фаза V live Sport+ 2026** → коммит/push по явной просьбе.

### Выполнено в сессии 2026-09-23 («Приступай»)

- ✅ Ф1 docs (user/tech/dev + FAQ/README)
- ✅ D3 BUG-I19 exit code
- ✅ D4 I05 push checks + I04 RW-gate (light sh+bat)
- ✅ C1: R06, R07, R12, N03, N04, N05
- ✅ C2 partial: R01 ContextCompat, R03 null data
- ✅ C3 partial: N02 log → getFilesDir

### Выполнено в сессии 4 (fix-all, 2026-09-23)

- ✅ C1/R3-class: provider + все IPC register/send под `BIND_SET_MODES_SERVICE` (+ setPackage)
- ✅ H3: allowBackup=false, мёртвые SMS/STORAGE/PHONE/GET_TASKS, queries self-package
- ✅ H4 residual: SetModesReceiverDynamic — задокументирован
- ✅ H5/H6/H7/H8 + back-ловушка + portrait Apply + presetId + Logging running + light remove settings
- ✅ medium: versionCode, readme case, Packaging TUI note, TripHistory per-item, Apollo force-off

### Выполнено в сессии 5 (install-track I+II Gate A + III-B1…B5 + II-B1, 2026-09-23)

- ✅ I-A1…A6: LF gitattributes, INSTALL_GUIDE preflight/light/TUI/case, matrix R12, README.txt PREFLIGHT+wait
- ✅ II-A1: verify `--light` / `VERIFY_LIGHT` + bat-TUI Full/Light
- ✅ II-A2: sh-TUI меню Install/Verify/Remove/DNS/Dry-run (паритет bat)
- ✅ II-A3: ротация `install.log` → `.1`
- ✅ II-A4: wait-hint 30–60с в safety + preflight-fail
- ✅ **III-B1**: success-эхо в `full/install.sh` + `light/install.sh` перед финальным `adb reboot`
- ✅ **III-B2**: `cd "$(dirname "$0")" || exit 1` в 4 install/remove `.sh`
- ✅ **III-B3**: `wait_adb_device` timeout helper в `dns-overlay.sh` + 5 `.bat` subroutines (60с/120с post-reboot, fail-closed)
- ✅ **III-B4**: light backup_pull PRESENT/ABSENT/ERROR (паритет full); remove не трогали
- ✅ **III-B5**: `mkdir BACKUP_DIR` fail-closed в light install.sh/.bat + full install/remove.bat
- ✅ **II-B1**: `install-tui.bat` G1 bundle preflight (exit 3) + G2 `:preflight_device` перед Install/Verify/Remove/DNS
- ✅ **II-B2**: TUI-default в `README.txt` / `INSTALL_GUIDE.md` / `installation.md`
- ✅ **IV-D6**: `MANIFEST.sha256` — `write_manifest` в `make_release.sh` (full+light) + `tui_check_manifest` (`.sh`) + `:check_manifest` (`.bat`)
- ✅ **III-B6**: exit-map (G5) в `tui_map_exit` — CANBUS/EROFS/загрузчик-сигнатуры → next-step; баннер ban приоритет; **движок не трогали**
- ✅ **III-B7**: `ADB_SERIAL` в `tui_check_device` → export `ANDROID_SERIAL` для движка; без serial при >1 dev — fail-closed + подсказка; bat — только текст ошибки

### Немедленно (в очереди «Выполняй по очереди»)

1. ~~III-B6 exit-map, III-B7 adb-s~~ — ✅ выполнено 2026-09-23 (TUI-only, согласовано «да»).
2. **Фаза V** live-матрица Sport+ 2026 → коммит/push **по явной просьбе**.

### Параллельно без кода

5. Заполнить `compatibility-matrix.md` живыми тестами.
6. Whitelist `CAR_MOCK_VEHICLE_HAL` — «да» + проверка CanBus.

### После тестов на авто

7. Подпись релизов (отдельно от D6).
8. Light TUI / D7 — по необходимости.

### Открытые вопросы

- Подтверждён ли аксиома-файл `AGENTS.md`?  
- Sport+ 2026 = OD Sport+ 26 в матрице?  
- Нужна ли выгрузка полного списка 27 BUG-I в отдельный файл?

---

## 11. Карта источников

| Документ | Роль в этом плане |
|----------|-------------------|
| **`AGENTS.md`** | аксиомы, гейты, формат тройного анализа — **выше** |
| **`docs/agent/REFACTOR_PLAN.md`** | **этот единый план** |
| `docs/agent/PROJECT.md` | архитектура, IPC-шпаргалка, стенд |
| `docs/agent/HISTORY.md` | журнал сессий (append-only) |
| `docs/agent/design-install-tui.md` | полный дизайн TUI, G0–G8, A1–A12, D0–D7 |
| `docs/audit/phase0-report.md` | итог Фазы 0 |
| `docs/audit/risk-assessment.md` | R1–R26 |
| `docs/audit/deep-review-2.md` | BUG/ARCH/UX каталог |
| `docs/audit/code-review-*.md` | детали по модулям |
| `docs/audit/compatibility-matrix.md` | прошивки × функции (draft) |
| `docs/audit/architecture-diagrams.md` | диаграммы |
| `Packaging/README.txt`, `readme.md`, `hownews.md` | офиц. доки, справочники |
| `Docs/CAN-команды.odt`, `Docs/can.pdf` | CAN — **только чтение** |

---

## Приложение A — Сводная таблица фаз (исходный план → единый)

| Фаза | Название | Статус | WP |
|------|----------|--------|-----|
| 0 | Аудит кода | ✅ | WP-0 |
| 1 | Документирование | ✅ docs-каркас + FAQ | WP-A |
| 2 | Тестовая инфраструктура | 🟡 verify + matrix draft | WP-B |
| 3 | Безопасность install | 🟡 D2+D3+D4+D6; live нет; подпись нет | WP-D / подпись |
| 4 | Исправление багов | 🟡 C1+C2 partial + N02; R3/R4 = гейт | WP-C |
| 5 | Инкрементальные улучшения | 🟡 TUI = UX-вклад | WP-TUI / C5 |
| 6 | Новые функции | ❌ | WP-E |
| — | Installer D6/D7 | ⏳ после live D3/D4 | WP-TUI |
| — | IPC security R3/R4/N01 | ⏳ отдельный гейт | WP-C3 |

## Приложение B — Шаблон тройного анализа (копия для WP)

```
ИЗМЕНЕНИЕ: <файл:строка>
1) Назначение и точки вызова: ...
2) Impact (CAN/бут/установка/IPC/UI): ...
3) Сверка со справочниками и Sport+ 2026: ...
РИСКИ: ...
НЕ ПОКРЫТО УВЕРЕННОСТЬЮ: ... (или «нет»)
ПРОВЕРКА: <как тестировать вручную>
СОГЛАСОВАНИЕ: ожидает «да» / уже согласовано (дата, реплика)
```

---

*Сводка создана 2026-09-23. Дополнять HISTORY.md; аксиомы — только пользователем.*
