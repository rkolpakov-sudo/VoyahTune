# IMP-03 — Individual: выбор A/B по трассам WP2 (SPEC L45, R4, P1|S-M)

Статус: в разработке. WP3: корректность — Individual выбор по трассам WP2.

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

**Исправление** (Трасса A):
`DriveModeCanTransport.sendIndividual()` разбивает `IndividualProfile` на две отправки:
1. TX77: режимные состояния (без steering/accelerator)
2. TX77: руль/педаль (steering + accelerator)
Между отправками — проверка `CanSender.beginFrameAttempt()`.

**Трасса B**: если sendIndividual() не support'ится авто (ASC 785/959), fallback на
один TX77 + лог ошибки CAN_MSG_IVI_pwrSet_0A5/chassisSet_1BE в диагностику.

## Файлы

| Файл | Изменение |
|------|-----------|
| `DriveModeCanTransport.java` | sendIndividual() — две TX77 отправки |
| `DriveModeCanPolicy.java` | IndividualPlan — разделение на два набора ключей |
| `Docs/behavior-matrix.md` | Строка R4 |

## Риски / ограничения

- Разделение на две TX77 может увеличить CAN-нагрузку. L51 (IMP-09) уже адресует
  приоритеты: Individual — L1 (режимы), безопасность L0 не страдает.
- HIL-сценарий «второй кадр потерян» проверяет rollback: если вторая TX77 не прошла,
  первая должна быть отменена/скомпенсирована.