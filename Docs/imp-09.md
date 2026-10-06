# IMP-09 — Приоритетные полосы CAN-диспетчера (SPEC L51, P1|S)

Статус: **реализован** (2026-10-06). WP3: приоритетные полосы CAN-диспетчера + завершение
по плану этого файла (rate-limit, backpressure-метрики, 8 новых тестов).

## Требования SPEC

> **L51**: Приоритетные полосы CAN-диспетчера: L0 safety (свет/подвеска-стоп), L1 режимы, L2 комфорт;
> коалесценция внутри полосы; rate-limit per lane (конфиг); backpressure-метрики в локальную
> диагностику. P1|S

## Текущая архитектура

`CanBusEventRouter.Mailbox` — одна очередь `ArrayDeque<CanBusEvent>`. Все CAN-события
(DOOR, GEAR, LIGHT_STATUS, VEHICLE_STATE, AMBIENT_TEMPERATURE) проходят через единый FIFO.
Единственная приоритизация — в `dropForCapacity()`: level-сигналы уплотняются, ordered-транзакции
(DOOR/GEAR) сохраняют FIFO, CONNECTION никогда не дропается.

## Изменения

### Lane mapping (Kind → Lane)

| Lane | Kinds | CAN сигналы |
|------|-------|-------------|
| L0 (safety) | CONNECTION, CONNECTION_LOST, LIGHT_STATUS, DOOR | Барьеры эпохи, свет, двери |
| L1 (режимы) | GEAR, VEHICLE_STATE(DRIVING_MODE,EPS_MODE,...) | Режимы движения, CAN-режимы |
| L2 (комфорт) | AMBIENT_TEMPERATURE, остальные VEHICLE_STATE | Температура, второстепенные сигналы |

Барьеры эпохи (CONNECTION/CONNECTION_LOST) отнесены к L0: событие новой эпохи обязано
предшествовать любым state-событиям (иначе подписчик получает stale-данные после clear).

### 1. Три очереди в Mailbox

`Mailbox` получает три `ArrayDeque<CanBusEvent>` (queues[LANE_0..LANE_2]) вместо одной.

- `offer(event)`: определяет lane по `event.kind`, добавляет в соответствующую очередь.
- `drain()`: извлекает из L0 пока не пусто → 1 событие L1 → 1 событие L2.
- `starveCount`: если после N L0/L1 drain'ов L2 ни разу не получил событие → принудительное
  извлечение из L2 (предотвращение голодания, `STARVE_LIMIT = 10`); L0/L1-событие
  возвращается в СВОЮ очередь.
- `dropForCapacity()`: per-lane capacity (L0=8, L1=16, L2=32). Дроп политика как в текущей:
  level-сигналы уплотняются, ordered-транзакции дропаются по переполнению lane.
- **Глобальная ёмкость (паритет вендора)**: дополнительно к per-lane действует общая
  ёмкость = capacity подписки (`globalCapacity`) с кросс-лane dropGlobalCapacity()
  (level L0→L2 → ordered L0→L2 → fallback; барьеры не дропаются). Без этого вендор-тесты
  `callbackBurstCannotEvictConnectionBarrier` / `overflowDropsCoalescibleLevelBeforeTransition`
  / `transitionDroppedByOverflowCanBeAcceptedAgain` (cap 2-3) нарушались.
- `droppedPerLane[3]` / `acceptedPerLane[3]`: per-lane счётчики.

### 2. Rate-limiting per lane

`RATE_PER_LANE[3]` (static volatile, events/sec; **0 = unlimited — default**). В `drain()`
lane с недавним дренажем пропускается в течение rate-окна; если все непустые lane в
окне — drain откладывается до следующего `offer` (у Executor нет delayed-постов;
включается только конфигом после on-car калибровки).

### 3. Backpressure-метрики

- `droppedPerLane[3]`, `acceptedPerLane[3]`, `peakQueueDepth[3]`
- `Mailbox.diagnostics()` и `Router.diagnostics()` — строки для локальной диагностики.

### 4. Outbound приоритет

OemVehicleStateTransport получает per-lane `transactionLock[3]`. L0 отправка не ждёт L2.
Если L2 отправка в процессе, L0 прерывает её (завершает текущую транзакцию, L2
откладывается). **Фаза 2 — не реализована** (см. риски).

## Файлы

| Файл | Изменение |
|------|-----------|
| `CanBusEventRouter.java` | Mailbox: три очереди, drain-цикл L0>L1>L2, per-lane конфиг/дроп/backpressure; новый tier() метод |
| `CanBusEventRouterTest.java` | 8 новых тестов: priority ordering, per-lane capacity, rate-limit, starvation guard, backpressure |
| `Docs/behavior-matrix.md` | Строка L51 |

## Риски / ограничения

- Ordered-транзакции (DOOR/GEAR) теряют глобальный FIFO при переключении между L0 и L1.
  Но в SPEC заложено: безопасность (L0) имеет приоритет над режимами (L1).
- Rate-limit значения эмпирические — требуют настройки на реальном авто; default 0
  (unlimited); при срабатывании drain откладывается до следующего offer.
- Outbound приоритет (L0 прерывает L2-транзакцию) — фаза 2, не реализован.
- Все VEHICLE_STATE рулятся в L2: выделение подвески/стоп-сигналов в L0 по signal-ID
  требует ID-маппинга, которого нет в payload (уточнить на traces L109/L110).
- Тесты: 15 вендор-паритет + 8 новых (priority ×3, per-lane capacity, starvation,
  rate-limit ×2, diagnostics) = 23/23 PASS.