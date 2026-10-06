# IMP-03 — Individual: выбор A/B по трассам WP2 (SPEC L45, R4, P1|S-M)

Статус: **реализован** (2026-10-06). WP3: корректность — Individual выбор по трассам WP2.

## Требования SPEC

> **L45/R4**: Individual: один TX77 вместо двух OEM-кадров (аудит).
> **Трасса A**: две транзакции TX77 в порядке OEM (кадр режим → кадр руль/педаль).
> **Трасса B**: одна транзакция + сверка ASC 785/959 + лог CAN_MSG_IVI_pwrSet_0A5 fail /
>   chassisSet_1BE fail.
> **HIL-сценарий**: второй кадр потерян.

## Аудит

**Файлы**:
- `OemIndividualDriveProfileReader.java` — читает Individual профили из OEM Settings
  (руль/педаль per account), возвращает `IndividualProfile` с `steering` и `accelerator`.
- `DriveModeCanTransport.java` — отправляет CAN-пакеты режимов через
  `OemVehicleStateTransport.sendBundle()`.
- `DriveModeCanPolicy.java` — строит `Plan` из набора VehicleState ключей.

**Текущее поведение**: `sendBundle()` отправляет один TX77 с картой состояний. Individual
режим использует тот же механизм — одна bundle-транзакция. SPEC требует **две** TX77
в порядке: кадр режима → кадр руль/педаль.

## Реализация (Трасса A + B)

`DriveModeCanTransport.dispatchSplitIndividual()` — вызывается из `dispatch()` для
`"INDIVIDUAL"`:

1. План делится на два подмножества по именам ключей:
   - кадр 1: `DRIVING_MODE_SET` (режим);
   - кадр 2: `EPS_MODE_SET` + `PROP_MODE_SET` (руль/педаль).
2. **ОДНА** заявка `CommandStatusHub.submit(FEATURE_DRIVE_MODE, action)` — внутри
   `action.send()` последовательно `sendBundle(frame1)` → `sendBundle(frame2)`
   (две submit по одному feature недопустимы: вторая немедленно фейлит первую в
   `CommandDispatcher.submit`, UI получил бы FAILED).
3. Отказ кадра 1 → **Трасса B**: `sendBundle(states)` целиком (одна TX77) + лог.
4. Отказ кадра 2 → лог ASC 785/959 («Individual steer/pedal frame lost»), режим уже
   применён; итог решает read-back окно (retry CommandDispatcher повторяет обе TX77).
5. Неполный план (нет одного из подмножеств) → сразу одна TX77.

Логи: `Individual split OK: mode + steer/pedal frames accepted` /
`Individual mode frame rejected; fallback single TX77 (trace B)`.

## Файлы

| Файл | Изменение |
|------|-----------|
| `DriveModeCanTransport.java` | dispatchSplitIndividual() — две TX77 в одной submit |
| `DriveModeCanPolicy.java` | (аудит) IndividualPlan — без изменений, разбор по именам ключей |
| `Docs/behavior-matrix.md` | Строка R4 |

## Риски / ограничения

- Разделение на две TX77 может увеличить CAN-нагрузку. L51 (IMP-09) уже адресует
  приоритеты: Individual — L1 (режимы), безопасность L0 не страдает.
- HIL-сценарий «второй кадр потерян» (rollback кадра 1) — отложен до on-car кампании;
  сейчас кадр 2 теряется без отката кадра 1 (read-back может завести command в
  TIMEOUT/FAILED — штатная обработка IMP-01).