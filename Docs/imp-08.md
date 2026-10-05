# Hook generations & crash artifacts (IMP-08, SPEC L50)

Категория: стабильность. Ссылка: R2 (последствия SIGSEGV в 3.22 — причина не
установлена, артефакты не собираются). P0|M. Порядок: после IMP-01/IMP-02 (L119).

Четыре пункта SPEC L50 → реализация:

| SPEC L50 | Реализация |
|---|---|
| Реестр поколений хуков: манифест агентов (версия, pid-поколение, таймстарт) | `HookGenerationRegistry` (RestoreMode) |
| Boot-reconciliation «ожидание vs факт» | `reconcile()` + врезки в provider и диагностику |
| Идемпотентное самолечение без перезагрузки | supervise-loop лоадера (существующий) + идемпотентность реестра; см. §3 |
| Crash-артефакты (tombstone ±30с, dumpsys) в `/data/local/<product>/crashes/` с выгрузкой в «Сохранить отчёт» (локально, без сети) | lane `crashes` в `load.bin` + `HookReportExporter` |

## 1. Реестр поколений (`HookGenerationRegistry`)

Хранение: SharedPreferences `HookGeneration` (отдельно от контрактных
`HookStatus`, которые пишет root-провайдер — реестр не трогает контракт).

- **Манифест агентов**: `HookStatusContract.HOOK_IDS` — 7 агентов
  (`vd-bypass, steering-wheel, launcher-dock, multi-display, apollo-tech,
  keyboard-en, keyboard-ru`), версия манифеста = release payload `3.22.0`
  (`MANIFEST_VERSION`), отображение — `HOOK_LABELS`.
- **pid-поколение**: `generation = <Settings.Global.BOOT_COUNT>|<loaderPid>`
  из payload (`v=1;loader=...;pid=...`). Смена любого из компонентов = новое
  поколение.
- **Таймстарт**: `firstSeenAt` — момент первого наблюдения поколения
  (персистится, обнуляется только при смене поколения).
- Источник истины факта — payload v1 (`HookStatusContract.parse`); контракт
  валидации и формат НЕ меняются (тесты `test_hook_status.sh`,
  `HookStatusContractTest`).

## 2. Boot-reconciliation «ожидание vs факт»

Триггеры (оба идемпотентны, один и тот же `reconcile()`):

1. **При публикации payload** — `RestoreModeContentProvider.call()` после
   `commit()`: root публикует payload после boot_completed → реконсиляция
   происходит на старте даже без открытого UI («boot-reconciliation»).
2. **Тик диагностики** — `AdvanceActivity.readSystemMetrics()` (5с,
   единственный таймер секции): ловит переход grace-окна без смены payload.

Вердикт на хука: `active` → OK; `disabled` → OFF (не деградация — штатное
выключение агента); `failed` → DEGRADED; `waiting/injecting` → PENDING в
течение grace `GRACE_MS=60с` от таймстарта поколения, затем DEGRADED
(«ожидание vs факт»: факт не сошёлся с манифестом после отведённого окна).
Overall: `LOADER_DOWN` (loader=stopped) > `DEGRADED` > `PENDING` > `NO_DATA`
(payload ещё не опубликован) > `OK`.

Побочные эффекты только при изменении: смена поколения или смена overall →
append в журнал (кольцевой, ≤30 записей `ts|overall|детали`); повторный
вызов с теми же входами ничего не пишет (идемпотентность).

UI: строка-приписка к `renderForUi` в тексте `textHookStatus`
(`Сверка: ... / Поколение: boot=N pid=P с HH:MM`), полный журнал — в отчёте.

## 3. Идемпотентное самолечение без перезагрузки

Что уже есть и покрыто тестами (документируется, не дублируется):

- **Supervise-loop лоадера**: каждый worker-lane перезапускается при смерти
  целевого процесса; попытки защищены attempt-latch + backoff
  (`test_parallel_hook_loader.py`, `test_loader_fault_backoff.sh`,
  `test_loader_pipe_records.sh`) — упавший хук возвращается без ребута
  устройства.
- **Доставка payload**: state-change-only публикация + bounded provider
  retries (`HOOK_STATUS_PROVIDER_MAX_ATTEMPTS=3`), сброс лимита на любой
  смене payload — транзиентная недоступность провайдера не «замораживает»
  статус навсегда.

Что добавляет IMP-08: сам реестр **восстановим без перезагрузки** — все его
структуры (поколение/таймстарт/журнал) пересчитываются из текущих входов;
после kill/restart приложения реконсиляция сходится к тому же overall
(проверяется JVM-тестом повторного reconcile).

Ограничение (документируется): **принудительный re-inject по кнопке из
приложения не реализуется** — попытки инъекций принадлежат lane-механизмам
(`reserve_injection_attempt`, MD/Drive backoff), и их ручной обход создаёт
crash-loop риск, который запрещён kill-критериями P0 (L192). Актором
самолечения остаётся лоадер; приложение обеспечивает детекцию, журнал и
эскалацию в UI.

## 4. Crash-артефакты

### Сбор (loader, новый lane `crashes`)

- Триггер: детекция изменения mtime среди `/data/tombstones/tombstone_*`,
  **двухтактная стабилизация** (первый такт — зафиксировать, второй —
  собрать; защита от копирования дозаписываемого tombstone).
- Окно: tombstone'ы с `mtime ∈ [T-30с, T+30с]` (SPEC: «±30с»), где T —
  самый новый tombstone.
- Артефакты в `/data/local/voyahtune/crashes/<epoch>/`:
  `tombstone_XX.txt` (копии), `dumpsys_window.txt`, `dumpsys_activity.txt`,
  `dumpsys_meminfo.txt` (`dumpsys -t 30` под `timeout`), `context.txt`
  (boot_id, uptime, колlected-время, loader pid).
- Ретеншн: последние 10 директорий (имена — epoch, сортируются).
- Каждая новая директория **зеркалируется** в
  `/data/data/ru.big.town.restoremode/files/crashes/` (best-effort: chown
  uid приложения, chmod, `restorecon -RF`) — приложение всегда читает свои
  файлы; первичный канонический путь — SPEC-шный `/data/local/voyahtune/`
  (slug продукта `VoyahTune` в нижнем регистре — конвенция путей лоадера
  `voyahtune_*`).

### Выгрузка («Сохранить отчёт», локально, без сети)

Кнопка «Сохранить отчёт» на экране «Логирование» → `HookReportExporter`
(фоновый поток) собирает zip в `cacheDir/reports/`:

- `hook_status.txt` — payload, рендер UI, строка реконсиляции, журнал
  поколений, boot count, версия приложения;
- `crashes/**` — зеркало crash-артефактов (если есть);

zip отдаётся через `ACTION_SEND` + FileProvider
`ru.big.town.restoremode.fileprovider` (файл `res/xml/file_paths.xml`,
cache-path `reports/`). Сетевых вызовов нет. Существующая кнопка
«Выгрузить логи» (канал Native `shareLogFile`) не меняется.

## Тесты

| Уровень | Что |
|---|---|
| shell (static) | `Packaging/tests/test_crash_artifacts.sh` — require/forbid строк лоадера (lane, пути, окно ±30, ретеншн, зеркало); обновлён pin `WORKER_LANES` в `test_loader_fault_backoff.sh` |
| loader behavior | существующие `test_parallel_hook_loader.py` (lane `crashes` не нарушает — ранний выход при отсутствии tombstones на host), `test_hook_status.sh` (контракт payload не тронут) |
| JVM | `HookGenerationRegistryTest` (вердикты, grace, смена поколения, идемпотентность/journal-ring, описания), `HookReportExporterTest` (состав zip, пустые crashes) |
| HIL | не применимо: IMP-08 не имеет CAN-измерения; покрытие — shell + JVM + on-car |
| on-car (P0) | убийство целевого процесса → lane self-heal; вызованный краш → директория в обоих путях; «Сохранить отчёт» выгружает zip (kill-критерий K=2, L121) |

## Поведение как расхождение форка

Запись в `Docs/behavior-matrix.md` (L116): строка «Стабильность: SIGSEGV» —
причина дополнена сбором crash-артефактов форком (IMP-08): OEM 3.22 не
собирает tombstone/dumpsys-контекст, причина E4 «не установлена».

## Ограничения / риски

- Зеркало в данные приложения пишет root; при отказе mkdir/cp/chown сбор
  продолжает работать в каноническом пути (лог `loge`), экспорт покажет
  только текст статуса — деградация, не потеря данных.
- `dumpsys -t 30` выполняется в отдельном lane-процессе и не блокирует ни
  status-публикацию, ни supervise-loop (задержка lane ≤ ~95с только в момент
  краха).
- Сбор привязан к tombstone'ам (`/data/tombstones`); Java-крэши без
  native-фрагмента покрываются dumpsys-контекстом только при наличии
  tombstone в том же окне — известное ограничение, снимается на авто
  (on-car checklist).
