# DESIGN: UX установки + автоматизация + TUI с защитой от ошибок

**Статус:** D2 реализован (новые файлы, движок не изменён) · **Согласовано Q1–Q6** · 2026-09-23  
**Эталон:** Voyah Free Sport+ 2026 · **Аксиомы:** `AGENTS.md`  
**Связано:** `docs/audit/code-review-installer.md`, `deep-review-2.md` (BUG-I*), план Фаза 3

### Решения пользователя (Q1–Q6)

| Q | Ответ |
|---|--------|
| Q1 Форма TUI | **POSIX TUI + bat menu** |
| Q2 Маркеры в движке | **Нет** — только обёртка |
| Q3 Лог | **TUI всегда tee** → `install.log` |
| Q4 verify | **Да, в D2** |
| Q5 DNS | **Как сейчас** (внутри install.sh / отдельный bat) |
| Q6 Scope | **Сначала full** (light позже) |

### Реализация D2 (этот этап)

| Файл | Роль |
|------|------|
| `Packaging/installer/full/tui-lib.sh` | ANSI, confirm, preflight bundle/adb, profile, tee, exit-map |
| `Packaging/installer/full/install-tui.sh` | Оркестратор: safety → preflight → plan → run install.sh → map → verify |
| `Packaging/installer/full/verify_post_install.sh` | read-only чек после успеха |
| `Packaging/installer/full/install-tui.bat` | ASCII menu 1–5 (install/verify/remove/dns/exit) |
| `Packaging/installer/full/verify_post_install.bat` | Windows read-only verify |
| `make_release.sh` | TUI-файлы в `required` только для **full** |
| `Packaging/README.txt` | Упоминание TUI; install.bat остаётся non-interactive |

Флаги `install-tui.sh`: `--yes`, `--non-interactive`, `--dry-run`, `--help`.

---

## 0. Принципы дизайна (вытекают из аксиом)

1. **`full/install.sh` / `full/install.bat` — источник истины мутаций.** TUI не переписывает фазы, не меняет порядок disable-verity / backup / boot-hook / reboot.
2. **Существующая идемпотентность сохраняется:** «повтори тот же скрипт из той же папки» остаётся основным recovery.
3. **Автоматика не заменяет живую проверку** на Sport+ 2026: TUI предлагает чек-лист, не «гарантирует» исход.
4. **Non-interactive режим обязателен** (README: bat без pause; сервер/CI) — TUI = опция, не замена.
5. **Windows и POSIX** — два шелла; общий слой = концепции + тексты, не один бинарь.

---

## 1. Диагноз текущего UX (факты из кода)

| Проблема | Проявление | Источник |
|----------|-------------|----------|
| Пользователь не знает, сколько фаз | Просто `=== Заголовок ===` подряд | install.sh |
| Нет финального успеха в sh | bat: `Installation complete`; sh — обрыв после reboot | UX-S1 |
| «Do not reboot» / «Не перезагружайте» — разные формулировки | README ссылается на англ., sh печатает рус. | UX-S2, README.txt:115 |
| Ошибка не говорит, **что делать дальше** | Есть исключения (boot-hook), много `exit 1` без гейта | install.sh |
| BAT может `exit 0` после `!!!` | Ложный успех в отдельной ветке boot-hook | BUG-I19 |
| Лог не сохраняется | README просит «полный вывод», tee нет | UX-S4 |
| light: push без rc | Ложный success копирования | BUG-I05 = R9 |
| Нет диагностики **до** ручного запуска | Кабель/unauthorized/offline разбираются в README, не в скрипте | README.txt:45–56 |
| Нет «прочитал инструкцию» гейта | Безопасность P/тормоз/кабель только в тексте | README.txt:4–16 |
| sh интерактивен только для DNS | Батарея вопросов минимальна — хорошо для автоматизации | install.sh Фаза 11 |
| Смешанные языки sh(рус)/bat(англ) | UX-S6 | — |

**Что уже хорошо (не ломать):** per-file атомарность, snapshot/rollback boot-hook, preflight до `/system`, идемпотентный re-run, DNS lock-dir, `test_apollo_direct_only.sh`.

---

## 2. Архитектура: «TUI-оркестратор + неизменный движок»

```
┌─────────────────────────────────────────────────────────────┐
│  Слой UX (новый, опциональный)                              │
│  ┌──────────────────┐    ┌──────────────────────────────┐   │
│  │ install-tui.sh   │    │ install-tui.bat / .ps1       │   │
│  │ (POSIX + ANSI)   │    │ (cmd menu или PowerShell)    │   │
│  └────────┬─────────┘    └──────────────┬───────────────┘   │
│           │ 1) preflight UX            │                    │
│           │ 2) чек-лист + confirm       │                    │
│           │ 3) run engine с tee/логом   │                    │
│           │ 4) разбор exit code         │                    │
│           │ 5) guide recovery           │                    │
└───────────┼─────────────────────────────┼──────────────────┘
            ▼                             ▼
┌─────────────────────────────────────────────────────────────┐
│  Слой движка (СУЩЕСТВУЮЩИЙ, фазы не меняем на первом этапе)│
│  full/install.sh  |  full/install.bat  |  light/*           │
│  make_release.sh (склейка @VERSION@)                        │
└─────────────────────────────────────────────────────────────┘
            │
            ▼
┌─────────────────────────────────────────────────────────────┐
│  Слой после установки (новый, read-only к ГУ)               │
│  verify_post_install.sh|bat  →  авточек-лист Sport+ 2026    │
└─────────────────────────────────────────────────────────────┘
```

**Почему обёртка, а не «переписать install.sh в TUI»:**
- 646 строк с rollback state-machine — любой «интерактивный ввод внутрь фаз» ломает атомарность и тест `test_apollo_direct_only.sh`.
- Идемпотентный re-run уже работает — TUI только **объясняет** повторный запуск.
- Откат поведения движка = 0, если TUI падает (движок запускается и напрямую).

**Когда можно второй этап (после живых тестов):** точечные `phase_begin/phase_end` echo-хуки **только текстовые**, без смены порядка — чтобы TUI рисовал `[4/12]`. Это уже правка движка → согласование отдельно.

---

## 3. TUI — функциональные требования

### 3.1 Экраны (state machine UX)

| Экран | Содержание | Действия |
|-------|------------|----------|
| **Welcome** | Версия релиза, full/light, предупреждение: **P + стояночный тормоз**, не трогать USB, Type-A↔A | `Продолжить` / `Выход` / `Открыть README` |
| **Checklist (read-only)** | 8–10 пунктов с авто- и ручной отметкой | Space = toggle, `Enter` = далее |
| **Preflight device** | adb, 1 устройство, state=device, root, `/system` RW?, CANBUS owner, free space | Авто; при fail — кнопка `Что делать` (текст из README) |
| **Plan** | Список 12 фаз full / N фаз light + «будут 1–2 перезагрузки» + что попадёт в `backup/` | `Начать установку` / `Назад` |
| **Run** | Лог в реальном времени + прогресс-бар фаз (пока эмуляция по маркерам `===`) | `Остановить` = Ctrl+C → экран recovery |
| **Fail** | Код ошибки, **где остановились**, «Do not reboot» **или** «можно перезапустить», следующий шаг | `Повторить с этого же скрипта` / `Отчёть об ошибке` (копирует install.log) |
| **Success** | Что установлено, напомнить **2 перезагрузки**, ссылка на `verify_post_install`, опция DNS | `Запустить verify` / `Выход` |
| **Remove** | Отдельный вход: только из папки с backup; показывает содержимое backup | Гейт подтверждения |

### 3.2 Автоматизация (без изменения мутаций движка)

| # | Возможность | Реализация | Риск для авто |
|---|-------------|------------|---------------|
| A1 | Авто-определение adb в PATH / `./tools` | Общая функция preflight | None |
| A2 | Разбор `adb devices`: 0 / unauthorized / offline / offline / multiple → **конкретный текст** из README:45–56 | Только чтение | None |
| A3 | `getprop` fingerprint: модель, VoyahOS, Android SDK | `ro.product.*`, `ro.build.version.sdk` → **профиль Sport+ 2026** (если совпал — галка «эталон»; иначе «не проверено») | None (read-only) |
| A4 | Предупреждение R20: SDK &lt; 33 → «RestoreMode может падать при registerReceiver — подтвердите на вашей машине» | read-only | None |
| A5 | Проверка **одного** устройства (без эмуляторов) | adb | None |
| A6 | Лог `install.log` через tee/redirect + ротация при старте | Обёртка | None |
| A7 | Единые exit codes 10–80 (из плана Фазы 3) — **обёртка мапит** код движка → экран Fail | Без правки движка на этапе 1 | None |
| A8 | Флаги: `--yes` (все confirm=yes), `--dry-run` (только preflight+plan, движок не трогает — **движок без dry-run пока: TUI не вызывает мутации**), `--non-interactive` (= текущий bat) | CLI обёртки | None |
| A9 | Resume-подсказка: при запускe в папке, где уже есть `backup/` и state «was interrupted» → «Продолжение = тот же скрипт» | Только UI-состояние по файлам | None |
| A10 | Post-install verify (read-only): пакети, leavecar, файлы boot-hook, props freeform, `pm path` Native | Новый `verify_post_install.sh` | None |
| A11 | Страховка bat: `setlocal` + trap-equivalent — всегда печатать итог `OK/FAIL` и `exit /b` ≠0 при `!!!` (фикс BUG-I19) | **Правка bat** → согласование, отдельный пункт | Low (код выхода) |
| A12 | light: `adb push \|\| exit 1` (BUG-I05) | **Правка light** → согласование | Low–Med |

**Не делать в автоматике:** авто-нажатие «да» на DNS без пользователя (сейчас штатное меню — ок); авто-reboot вне фаз движка; обход preflight CANBUS/verity.

### 3.3 Усиленная защита от ошибок установки

| Барьер | Когда | Механика |
|--------|-------|----------|
| **G0 Safety** | До всего | Явный confirm: P + ручник + USB не выдергивать + Type-A↔A (текст README) |
| **G1 Bundle** | До adb | 14 файлов full (уже в движке) + **SHA-256 manifest релиза** (новый, см. ниже) |
| **G2 Device** | До root | 1 устройство, state, root OK, SDK, fingerprint |
| **G3 Mutate gate** | Перед первой записью в device (Apollo settings / disable-верити) | Синопсис плана + «backup будет в ./backup» + require Enter (или `--yes`) |
| **G4 Reboot gate** | Перед reboot №1 и №2 | Печать: сколько будет перезагрузок, что уже сделано, **Do not reboot / reboot сейчас** — единая строка |
| **G5 Fail-safe banner** | Любой `exit` движка ≠0 | TUI ловит: **«Не перезагружайте ГУ»** ИЛИ **«Можно перезапустить тот же скрипт»** — по карте кодов фаз |
| **G6 Verify** | После успеха | verify_post_install + чек-лист ручных пунктов (режимы до/после) |
| **G7 Remove gate** | remove.* | Показать `ls backup/`, требование «та же папка релиза», двойной confirm |
| **G8 Manifest** | Сборка релиза | `make_release.sh` пишет `MANIFEST.sha256` всех payload; TUI/G1 сверяет — защита от «подмешали js/apk» |

**Карта fail → пользователь (пример):**

| Сигнал | Экран |
|--------|--------|
| preflight файлы | «Распакуйте ZIP заново полностью» |
| unauthorized | «Разблокируйте ГУ, подтвердите RSA» |
| root denied | «Эта прошивка не даёт root ADB — стоп» (README:77–79) |
| /system RO после verity+reboot | «EROFS / загрузчик / несовместимо» (README:81–84) |
| CANBUS owner чужой | «Удалите несовместимый пакет, /system не тронут» |
| boot-hook status 2 | **«Do not reboot»** — только повтор installer |
| успех | «Две перезагрузки; сервис после второй» |

---

## 4. Технология TUI

### 4.1 POSIX (`install-tui.sh`)

| Вариант | Плюсы | Минусы | Вердикт |
|---------|-------|--------|---------|
| **Чистый POSIX + ANSI** (цвета, `read -r`, простые экраны) | 0 зависимостей, macOS/Linux/Git Bash, lint `sh -n` | Нет «классического» dialog | **Рекомендация default** |
| `dialog` / `whiptail` | Красивые gauge/yesno | Нет в чистом macOS; на Windows — только в MSYS | Optional fallback |
| Python textual/rich | Мощный TUI | **Нет гарантии Python у пользователя** | Не предлагать |
| fzf | Быстрый выбор | Доп. пакет | Не обязательно |

**Структура файлов (предложение):**
```
Packaging/installer/
├── full/install.sh          # движок — без изменений этап 1
├── full/install.bat
├── common/tui-lib.sh        # ANSI, menu, confirm, log tee, exit-map  (новый)
├── install-tui.sh           # оркестратор full|light                  (новый)
└── verify_post_install.sh   # read-only чек                           (новый)
```
`make_release.sh`: класть `install-tui.sh` + `tui-lib.sh` в **оба** full/light payload (payload checklist расширить).

### 4.2 Windows

| Вариант | Плюсы | Минусы | Вердикт |
|---------|-------|--------|---------|
| **A. `install.bat` остаётся** + `install-tui.bat` с `choice`/`menu` (ANSI в Windows 10+ Terminal) | ASCII/CRLF сохранить, без PowerShell | Простой UI | **Минимальный этап** |
| **B. `install-tui.ps1`** (WPF/Out-GridView или консольное меню) | Настоящий TUI, прогресс | Два языка, execution policy, README.txt должен объяснять | **Этап 2**, если A мало |
| **C. Полностью PowerShell вместо bat** | Единый движок | **Замена проверенного bat = риск** | Не предлагать |

**Рекомендация:** этап 1 — A + fix BUG-I19 в существующем bat; этап 2 — по желанию B.

### 4.3 Авто-определение full/light

TUI смотрит наличие `vd_bypass.js`/`load.bin` → предлагает full или light; не даёт запустить full-движок на light-наборе (G1).

---

## 5. Метрики успеха UX

1. Пользователь **до первой мутации** видит: план, # перезагрузок, backup path, safety P/ручник.
2. Любая ошибка ≤ 1 экрана: что, где, **можно ли перезагружать**, что нажать.
3. `install.log` создаётся всегда; README.txt знает путь.
4. После успеха одной кнопкой — verify (read-only) вместо чтения 127 строк README.
5. Non-interactive: `./install-tui.sh --non-interactive` ≡ поведение сегодняшнего `install.sh` / `install.bat`.
6. `sh -n` + `test_apollo_direct_only.sh` + payload checklist — зелёные.
7. На Sport+ 2026: полный прогон install → verify → remove → install без ручного «гадания» по logcat.

---

## 6. Этапность внедрения (только после «да» по этапу)

| Этап | Что | Код движка | Согласование |
|------|-----|------------|--------------|
| **D0** | Этот дизайн + согласование вариантов (§8) | Нет | Вопросы ниже |
| **D1** | Docs-only: README.txt — единые фразы «Do not reboot», две перезагрузки, путь install.log, FAQ tee | Нет (только txt/md) | Можно сразу «да docs» |
| **D2** | `tui-lib.sh` + `install-tui.sh` **только preflight+plan+run+map exit** (движок без правок); tee лог; verify_post_install | **Нет** | Новый файлы |
| **D3** | Fix BUG-I19 + язык/итог в **bat** (минимум) | bat: exit code + echo | По строкам |
| **D4** | light push `\|\| exit 1` (BUG-I05) + RW-gate remove (I04) | light sh | По строкам |
| **D5** | Опциональные phase-маркеры `[N/12]` в движке (только echo) | full sh/bat | По строкам, прогон test_* |
| **D6** | MANIFEST.sha256 в make_release + G1 в TUI | make_release + TUI | Сборка релиза |
| **D7** | Windows PowerShell TUI (если нужно) | Новый ps1 | Опционально |

**Вне этапов TUI (отдельный трек):** R3/R4 permissions, IPC, CAN-баги — не смешивать с UX установки.

---

## 7. Что TUI **не** будет делать (красная линия)

- Менять порядок/семантику 12 фаз full, disable-verity, boot-hook, snapshot/rollback.
- Вместо пользователя соглашаться на DNS/«да» в `--non-interactive` без `--yes`.
- Добавлять новые мутации (props, push) сверх текущего движка.
- «Умный» auto-recovery с другими командами adb (только **повтор того же скрипта**).
- Запускать remove как «ремонт» после fail без явного выбора пользователя.

---

## 8. Вопросы на согласование (нужны ответы до D2)

**Q1. Форма TUI этапа 1:**  
(A) Только POSIX ANSI `install-tui.sh` + bat остаётся без TUI  
(B) POSIX TUI **и** простой `install-tui.bat` menu  
(C) Сразу PowerShell TUI на Windows  

**Q2. Движок `install.sh`:**  
(A) Этап D2 **без** правок движка (маркеры фаз эмулируются по `===` / нет)  
(B) Сразу добавить дешёвые `echo "[N/12] ..."` в движок (D5 раньше)  

**Q3. Лог:**  
(A) TUI всегда tee → `install.log`  
(B) Движок сам пишет tee (правка шапки install.sh)  

**Q4. Verify:** нужен ли `verify_post_install` в комплект D2, или docs-чек-лист достаточен?  

**Q5. DNS:** оставить как сейчас (меню в конце sh / отдельный bat) или TUI тоже управляет DNS-шагом?  

**Q6. light:** включать light в TUI сразу или сначала полный TUI только для full?  

---

## 9. Черновик скелета (не для коммита, иллюстрация)

```sh
# install-tui.sh — иллюстрация слоя UX (псевдокод)
tui_safety_gate          # P, ручник, кабель, USB
tui_check_files bundle   # G1 + опц. MANIFEST
tui_check_device         # A1–A5: adb state, root, fingerprint
tui_show_plan FULL|LIGHT # 12 фаз, 2 reboot, backup/
tui_confirm "MUTATE"     # G3
tui_run_tee ./full/install.sh   # движок без изменений
rc=$?
tui_map_exit $rc         # G5: reboot ban / re-run / success
[ $rc -eq 0 ] && tui_verify_post_install
```

---

## 10. Итог

- **Цель:** максимум UX и защиты ошибок **вокруг** проверенного движка, не внутри hot-path фаз.
- **Автоматизация:** preflight, разбор ошибок ADB, профиль авто, tee-лог, exit-map, post-verify, manifest релиза.
- **TUI:** POSIX ANSI (default) + опционально бат/PowerShell; экраны Welcome→Checklist→Plan→Run→Fail/Success.
- **Защита:** G0–G8 (safety, bundle, device, mutate/reboot gate, fail-safe banner, verify, remove, sha256 manifest).
- **Код движка на D2 — не трогаем**; фиксы I19/I05 — отдельными мини-пунктами после «да».

**Жду ответов на Q1–Q6** — затем можно зафиксировать этап D1/D2 в HISTORY и переходить к реализации только после явного «делай D2».
