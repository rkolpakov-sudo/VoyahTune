# IMP-04 — SettingsRepository: per-feature политика источника истины (SPEC L46/L31/R5, P1|M)

Статус: реализован. WP3: SettingsRepository — политики источников истины.

## Требования SPEC

> **L46/R5**: SettingsRepository: per-feature политика источника истины:
> VOT (только снапшот форка); OEM_MIRROR (чтение кэша TX57); GUARDED_SYNC (запись в
> OEM-профиль ТОЛЬКО после разрешения аккаунта и подтверждения чтения).
> SREV: GUARDED_SYNC пишет persist.qinggan.account.uid.power<uid> только при
> однозначно определённом активном аккаунте, иначе VOT + пометка в UI.

## Реализация

**SettingsRepository.java** — новый класс с:
- `Policy` enum: VOT, OEM_MIRROR, GUARDED_SYNC
- `policyFor(feature)` — per-feature policy map
- `guardedWrite(context, feature, accountUid, writeOem, verifyRead)` — write with
  account gate + read-back confirmation. Если account не resolv'ен → VOT (snapshot only).

**SaveChargeController.java**:
- `readCurrentAccountId()` — читает `/private/configs/token/accountInfo`
- `selectSrev()` / `setLevel()` — используют `SettingsRepository.guardedWrite()`
  с проверкой account UID и read-back верификацией после записи.

## Риски / ограничения

- Аккаунт читается из файла один раз при `apply()` — для долгих сессий может устареть.
- GUARDED_SYNC read-back использует `readVehicleStates()` — задержка CAN может дать
  false-negative. Fallback: VOT (snapshot), ошибка в логе. 
- Другие feature (drive, light) пока используют VOT/OEM_MIRROR — без изменений.