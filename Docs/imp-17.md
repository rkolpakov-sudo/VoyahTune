# IMP-17 — Бейджи реального состояния на карточках (SPEC L59/R1/R3/R8, P1|S)

Статус: реализован. WP3: UX бейджи — CONFIRMED/PENDING/FAILED/TIMEOUT.

## Требования SPEC

> **L59/R1/R3/R8**: Бейджи реального состояния на карточках
> (CONFIRMED/PENDING/FAILED/TIMEOUT); пометки «не запоминается автоматически» для
> аромат/сервис-подвески с причиной в tooltip.

## Реализация

**AdvanceActivity.java** (RestoreMode) — уже отображает статусы CONFIRMED/FAILED/TIMEOUT
в тексте состояния (lines 3501-3510). Расширено на MainActivity карточки: `stateBadge`
extras в Intent при отправке результата apply — карточка получает бейдж через Bundle.

**MainActivity.java**: отображение бейджа состояния под заголовком карточки, цвет в
зависимости от статуса (зелёный=CONFIRMED, жёлтый=PENDING, красный=FAILED,
серый=TIMEOUT). Tooltip с причиной для «не запоминается автоматически» (аромат,
сервис-подвеска) из extra "reason".