# IMP-05 — ACC авторетрай (SPEC L47/L34/R8, P2|S)

Статус: реализован. WP3: ACC авторетрай ≤3, backoff 2/5/10с.

## Требования SPEC

> **L47/R8**: ACC: авторетрай <=3 (backoff 2/5/10с) после неоднозначного отказа +
> UI-статус «доступен повтор» вместо блокировки до нового цикла.

## Реализация

`ApplyEngine.java`: в `beginRestoreGate()` добавить retry-loop для
`ACCEPTED_UNCONFIRMED`. При unconfirmed:
1. Счётчик retry ≤ 3
2. Backoff: 2000ms, 5000ms, 10000ms (по массиву `RETRY_BACKOFF_MS`)
3. Повторный вызов `CanRestorePlan.sendPending()`
4. Если после 3 попыток всё ещё unconfirmed — возвращать `SUCCESS` (не блокировать),
   в лог — предупреждение

`MainActivity.java`: если последний ACC-цикл завершился с retry=ACCEPTED_UNCONFIRMED,
показывать UI-статус «доступен повтор» на карточке ACC.

## Файлы

| Файл | Изменение |
|------|-----------|
| `ApplyEngine.java` | `beginRestoreGate()` — retry-loop ×3 с backoff |
| `MainActivity.java` | UI-статус «доступен повтор» при unconfirmed |