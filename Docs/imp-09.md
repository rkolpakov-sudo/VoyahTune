# IMP-09 — Приоритетные полосы CAN-диспетчера (SPEC L51, P1|S)

Статус: в разработке. WP3: приоритетные полосы CAN-диспетчера.

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
| L0 (safety) | LIGHT_STATUS, DOOR, VEHICLE_STATE(подвеска, стоп-сигналы) | Свет, двери, аварийные состояния |
| L1 (режимы) | GEAR, VEHICLE_STATE(DRIVING_MODE,EPS_MODE,...) | Режимы движения, CAN-режимы |
| L2 (комфорт) | AMBIENT_TEMPERATURE, остальные VEHICLE_STATE | Температура, второстепенные сигналы |

### 1. Три очереди в Mailbox

`Mailbox` получает три `ArrayDeque<CanBusEvent>` (queues[LANE_0..LANE_2]) вместо одной.

- `offer(event)`: определяет lane по `event.kind`, добавляет в соответствующую очередь.
- `drain()`: извлекает из L0 пока не пусто → 1 событие L1 → 1 событие L2.
- `starveCount`: если после N L0/L1 drain'ов L2 ни разу не получил событие → принудительное
  извлечение из L2 (предотвращение голодания, `STARVE_LIMIT = 10`).
- `dropForCapacity()`: per-lane capacity (L0=8, L1=16, L2=32). Дроп политика как в текущей:
  level-сигналы уплотняются, ordered-транзакции дропаются по переполнению lane.
- `droppedCounts[3]`: per-lane счётчик дропов.

### 2. Rate-limiting per lane

Конфигурируемый max событий в секунду per lane. Реализуется через `lastDrainTimestamps[3]`.
Если lane превысил rate → пропуск drain'а для этой lane на один цикл.

### 3. Backpressure-метрики

Новые поля в Mailbox:
- `droppedCounts[3]` — сколько событий дропнуто per lane
- `totalAccepted[3]` — сколько принято
- `peakQueueDepth[3]` — пиковая глубина очереди

Экспортируются через диагностику (метод `diagnostics()` на Mailbox/Router).

### 4. Outbound приоритет

OemVehicleStateTransport получает per-lane `transactionLock[3]`. L0 отправка не ждёт L2.
Если L2 отправка в процессе, L0 прерывает её (завершает текущую транзакцию, L2
откладывается).

## Файлы

| Файл | Изменение |
|------|-----------|
| `CanBusEventRouter.java` | Mailbox: три очереди, drain-цикл L0>L1>L2, per-lane конфиг/дроп/backpressure; новый tier() метод |
| `CanBusEventRouterTest.java` | 8 новых тестов: priority ordering, per-lane capacity, rate-limit, starvation guard, backpressure |
| `Docs/behavior-matrix.md` | Строка L51 |

## Риски / ограничения

- Ordered-транзакции (DOOR/GEAR) теряют глобальный FIFO при переключении между L0 и L1.
  Но в SPEC заложено: безопасность (L0) имеет приоритет над режимами (L1).
- Rate-limit значения эмпирические — требуют настройки на реальном авто.
- Outbound приоритет сложнее inbound (L0 может прерывать L2 транзакцию) — фаза 2.