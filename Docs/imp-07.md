# IMP-07 — Geometry-watchdog (SPEC L49, L111–L114, R12)

Статус: **реализован** (WP3, см. DECISIONS IMP-07). Kill-критерий: **K=2 итерации на авто**
(L121), on-car сверка заморожена вместе с кампанией.

## Требование SPEC L49

> Geometry-watchdog: снимок ожидаемых freeform-bounds; триггеры сверки (config change,
> перенос панели, смена дисплея, выход из сна, запуск приложения); самолечение с дебаунсом
> 700мс; last-known-good в персист; журнал мутаций геометрии. Поведение 3.22 (проверки до
> мутаций, проверенный контекст WM) = база.

KPI L170: **geometry-self-heal ≤ 2 с** (лог watchdog).

## Где живёт

Целиком внутри `Packaging/payload-common/vd_bypass.js` (system_server, Frida).
Исследование (см. DECISIONS IMP-07): лоадер (`load.bin`) может только публиковать
наблюдения — он не способен откатить мутацию окна в живом `system_server`; coarse-restart
лейна `vd` после смерти `system_server` уже работает. Поэтому самолечение — в JS-хуке,
лоадер — наблюдатель. Native-изменения не требуются: все пять триггеров уже существуют
как события внутри `vd_bypass.js`.

## Элементы

### 1. Снимок ожидаемых bounds (last-known-good)

Файл `/data/local/open_voyah/vd_hooks/geometry.lkg` — JSON-строка (одна запись):

```json
{"v":1,"on":true,"left":145,"top":45,"right":1920,"bottom":720,"compactBottom":560,"liftType":2,"ts":<epoch ms>}
```

- **Запись**: только в watchdog-чеке/старте, когда текущий снимок конфига (`FF` после
  успешного `refreshFreeformCfg()`) отличается отpersisted LKG (идемпотентность: не
  перезаписывать без изменений).
- **Чтение/восстановление**: на старте, если `refreshFreeformCfg()` не удался (невалидные
  Settings), — `restoreGeometryFromLkg()` валидирует снимок теми же правилами viewport и
  применяет к `FF` вместо падения (`throw "initial policy unavailable"` остаётся только
  для случая «нет валидного LKG»). В чеке restore НЕ вызывается: при неудачном refresh
  in-memory удерживает последнее применённое (более новое) состояние — откат к LKG там
  был бы регрессией; чек лишь журналирует `config-invalid`.
- dpi/fullscreen-карты в LKG не входят (per-package политика): восстановление оставляет их
  пустыми — деградация без ломки геометрии, всё фиксирует журнал.

Атомарная запись `.new` + `renameTo` — по образцу `publishAgentStatus`.

### 2. Триггеры сверки (все пять из L49)

Каждый триггер вызывает `scheduleGeometryCheck(trigger)`:

| Триггер SPEC | Точка в vd_bypass.js |
|---|---|
| config change | `WinReceiver.onReceive` (WIN_RELOAD) |
| перенос панели | `LiftReceiver.onReceive` (action.qg.layout.changed) |
| смена дисплея | `ffDisplayChangedMethod.implementation` (onDisplayChanged) |
| выход из сна | `ScreenReceiver.onReceive` (SCREEN_ON) |
| запуск приложения | `ffConfigImplementation` (ensureActivityConfiguration — config-pass запускаемого приложения) |

### 3. Дебаунс 700 мс

`GEOMETRY_WATCHDOG_DEBOUNCE_MS = 700`: окно-коалесценция — чек планируется один раз на
окно 700мс, события внутри окна НЕ сдвигают его (первое событие планирует, остальные
сливаются в тот же чек). Это гарантирует ограниченную задержку от первого события
(≤700мс + работа чека → KPI ≤2с) и исключает голодание при непрерывном config-трафике.
Таймер входит в `cancelFreeformPending()` + epoch-guard по `FF.hookEpoch`
(SCREEN_OFF/WIN_RELOAD отменяют; bump epoch в `scheduleFreeformHotAttach` также
обезвреживает чек — вместо него traversal делает сам attach).

### 4. Чек `runGeometryCheck(trigger)`

Порядок (всё вне hot-path, в `setTimeout` + `Java.perform`):

1. Guards: `agentFailed`, `!FF.on || !FF.screenOn` → выход без действий.
2. `refreshFreeformCfg()` — сверка ожидаемого (Settings) с текущим (in-memory `FF`):
   успех → снимок конфига актуален; неудача → in-memory удержан (штатное поведение),
   запись `config-invalid` в журнал.
3. Снимок vs persisted LKG: различие → `writeGeometryLkg` + журнал `snapshot-update` +
   `requestFreeformTraversalOnce` (окна перекладываются по свежему конфигу).
4. Hot-hooks: `FF.on && FF.screenOn && (!ffLayoutAttached || !ffConfigAttached) &&
   !ffHotAttachPending` → `attachFreeformHotHooks("geometry-watchdog")` + журнал
   `hook-reattach` (ловит потерянный attach после sleep/wake-гонок).
5. KPI-лог: при любом лечении `Log.i(TAG, "geometry watchdog heal trigger=… in <ms>ms")`,
   где ms = от `scheduleGeometryCheck` до завершения чека (вкл. 700мс дебаунс) — целевое
   значение ≤ 2000 (L170).

Чек НИКОГДА не вызывает `failAgent` (лечит, не роняет), НЕ мутирует Settings, НЕ пишет
в hot-path `layoutWindowLw`.

### 5. Журнал мутаций геометрии

Файл `/data/local/open_voyah/vd_hooks/geometry.journal` — построчный, кольцевой
(`GEOMETRY_JOURNAL_LIMIT = 30`), хранится в памяти + персист перезаписью, загрузка при
первом использовании. Формат записи:

```
<epoch ms>|<trigger>|<action>|<detail>
```

Действия: `snapshot-update`, `hook-reattach`, `config-invalid`, `startup-lkg-restore`.
Dedup: одинаковая последняя запись (action+detail) не дублируется — повторные чеки без
изменений не засоряют журнал (идемпотентность, тот же принцип, что в IMP-08 registry).

## Поведение 3.22 = база (не переписываем)

- Hot-path `layoutWindowLw`: проверки до мутаций, save→mutate→finally restore,
  `mGlobalLock`-контекст — **не трогаем**.
- Запрещённые API (под контролем тестов): `task.setBounds|setAppBounds|mSizeCompatBounds`,
  `setLaunchBounds` в Native, мутации Settings из watchdog.
- Существующие таймеры/epoch-механика, WIN_RELOAD-permission-gate, status-публикация —
  расширяются, не заменяются.

## Тесты

`Packaging/tests/test_geometry_watchdog.sh` (шаблон `test_vd_reparent_replay.sh`):

- `node --check vd_bypass.js`;
- require: константа 700, пути geometry.lkg/geometry.journal, все пять вызовов
  `scheduleGeometryCheck(...)` с ожидаемыми триггерами, `runGeometryCheck`,
  `restoreGeometryFromLkg`, epoch-guard, cap 30, KPI-log строка, атомарная запись;
- require: дебаунс-таймер отменяется в `cancelFreeformPending`;
- forbid: `putInt|putString` в watchdog-секции (мутации Settings), запрещённые bounds-API,
  `failAgent` внутри `runGeometryCheck`.

Существующие контракты (`test_vd_hot_hooks_disabled.sh`, `test_vd_reparent_replay.sh`,
`test_screen_lift_resize_restore.sh`, `test_loader_*`) — прогоняются без изменений.

## Риски / ограничения

- **Риск: низкий** — чек только читает и лечит через существующие безопасные механизмы
  (refresh/attach/traversal), не мутирует WM напрямую, не роняет агент.
- «Смена дисплея» триггер и «запуск приложения» (ensureActivityConfiguration) —
  относительно частые события; debounce сводит их к одному чеку, стоимость чека —
  один перечит конфига Settings + сравнение строк.
- LKG не хранит per-package DPI/fullscreen — восстановление без них (задокументированная
  деградация; пользовательские политики перечитываются при следующем успешном refresh).
- On-car: K=2, сверка поведения и KPI ≤2с по логу — после возобновления кампании.
