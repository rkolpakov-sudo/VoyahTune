# Runbook: паритет-оракул L109–L117 (WP2)

Операционная процедура доказательства паритета с 3.22.0 на авто.
Канон требований — `SPEC.md` L108–L117 (WP2) и Приложение A (L194, 12 P-сценариев).
Статус артефактов: `Docs/parity-report.md`, `Docs/behavior-matrix.md`.

## 0. Предусловия

- Чистое авто с ADB-доступом (`adb devices` показывает устройство).
- **Fingerprint ориентира** (Приложение B, E9/E10), сверить с
  `Docs/original-fingerprint.md`:
  - payload 3.22.0 sha256 = `f309156c836e8533f9770cf1555272e7383fe3b20fb289a364f0ccfd9056dcf1`
  - revision 3.22.0 = `62b74765a826375fe24b9bde207886011b7d1645`
- Стенд CAN: штатного `candump` на Android обычно нет — готова команда
  захвата CAN для стенда (передаётся в `capture-trace.sh --can-cmd '...'`).
  Без неё cantrace снимается заглушкой (best-effort), паритет CAN не доказуем.
- Понятный пакет нашей сборки: `ru.big.town.anative`,
  permission `com.qinggan.permission.WRITE_CANBUS` (подписи ключей — ключи
  билда, см. `Native/app/src/main/AndroidManifest.xml`).
- Инструменты уже в репо: `scripts/capture-trace.sh`, `scripts/grant-gate.sh`,
  `Utils/parity-diff.py`, `gradlew :tests:hil:replay` (L112).

**Kill-критерий L113**: провал grant-gate = стоп проекта, пересмотр
архитектуры установки (переименование пакетов + пересборка whitelist).

## 1. Фаза 1 — L109: трассы ОРИГИНАЛА

1. Установить оригинал 3.22.0 на чистое авто (проверить E9/E10 до установки).
2. Для каждого сценария 01–12 (Приложение A) выполнить описанные в нём
   действия оператора и снять трассу:

   ```bash
   scripts/capture-trace.sh --scenario 01 --origin original \
       --can-cmd '<команда захвата CAN стенда>' --duration 60
   ```

   Повторять для `--scenario 02 … 12`. Перезапись — только с `--force`.
3. Проверить комплект: `traces/original/<NN>.{logcat,nativelog,dumpsys,cantrace}`
   для всех 12 сценариев.

## 2. Фаза 2 — L110: переход на нашу сборку + fork-трассы

1. Удалить оригинал **guided, с бэкапом системной папки**
   (контракт: `Docs/installer-protocol.md`, разделы «Каталог», «Фазы и
   восстановление»).
2. Установить нашу сборку (см. `Docs/installer-architecture.md`,
   `Docs/installer-payload.md`).
3. Сразу после установки прогнать grant-gate (и ещё раз — перед приёмкой L116):

   ```bash
   scripts/grant-gate.sh            # добавить --serial/ --timeout при необходимости
   ```

   Коды: `0` гейт пройден; `1` провал → стоп по L113; `2` нет adb/устройства.
4. Те же 12 сценариев тем же порядком →:

   ```bash
   scripts/capture-trace.sh --scenario 01 --origin fork --can-cmd '...' --duration 60
   ```

## 3. Фаза 3 — L111: diff трасс

```bash
python Utils/parity-diff.py --strict
```

- Классы пары: `identical` / `expected-diff` / `regression` / `pending`.
- `regression` (непокрытое расхождение) — к фиксу.
- `expected-diff` — только через allowlist `Docs/parity-expected-diffs.txt`,
  **каждый паттерн с комментарием «зачем»** (требование L116: допустимые
  расхождения задокументированы).
- `pending` — трасса не снята; при `--strict` это ошибка (гейт L116).
- Отчёт пишется в `Docs/parity-report.md` (сценарий × статус).

## 4. Фаза 4 — L112: HIL-replay как постоянная регрессия

Транскрипт-эталон (REF) генерируется **из трасс оригинала**:

```bash
gradlew :tests:hil:replay -Ptrace=traces/original/01.nativelog -Pout=build/ref-01.ref
```

Сверка форка против REF (код `1` = дрейф, `2` = ошибка аргументов/файлов):

```bash
gradlew :tests:hil:replay -Ptrace=traces/fork/01.nativelog -Pref=build/ref-01.ref
```

Канонические REF для CI хранятся в
`tests/hil/src/test/resources/replay/refs/<NN>.ref` (генерация из
`traces/original/*` после L109, коммит вместе с трассами L115) —
`:tests:hil:test` гоняет стеновые тесты на каждый PR; реальные REF добавляются
после снятия трасс.

Известное ограничение: enum `VehicleState` (27 состояний) — прочие
OEM-состояния из трассы форка дадут `unsupported-state` в транскрипте; это
не дрейф поведения, а расширение домена (задокументировать в expected-diffs).

## 5. Фаза 5 — L113: grant-gate (детально)

```bash
scripts/grant-gate.sh [--pkg ru.big.town.anative] [--serial …] [--timeout 30]
```

Четыре проверки (по порядку):

| # | Проверка | Источник |
|---|----------|----------|
| 1 | `dumpsys package <pkg>` содержит `WRITE_CANBUS` … `granted=true` | signature-permission под нашими ключами |
| 2 | `pidof ru.big.town.anative` — Native запущен | L113 «Native стартует» |
| 3 | в `logcat -d` есть `CanBus callback registered` (ждёт до `--timeout`) | подписка на CAN состоялась |
| 4 | в свежем `logcat -d -t 500` нет `WRITE_CANBUS permission missing` | негативный маркер |

При провале любого пункта — код `1` и инструкция «СТОП (SPEC L113):
стоп проекта, пересмотр архитектуры установки (возможно переименование
пакетов + пересборка whitelist)».

## 6. Фаза 6 — L114: behavior-matrix

Каждое расхождение с 3.22.0 (включая `expected-diff`) — запись в
`Docs/behavior-matrix.md` по шаблону (Приложение C):
`| Фича | Событие применения | Захват из OEM | Read-back | Запоминается | Причина |`.
Явные эталоны расхождений — сценарии 05 (AVAS не запоминается) и 07
(SREV возврат ≠ принятие уставки); см. также E2, E5.

## 7. L115–L117: артефакты, приёмка, откат

**Артефакты (L115)**: `traces/original/*`, `traces/fork/*` (12 сценариев ×
4 вида); `Docs/parity-report.md`; HIL-replay в CI; `Docs/behavior-matrix.md` v1.

**Приёмка (L116)** — чек-лист:

- [ ] `python Utils/parity-diff.py --strict` → exit 0 (100% P-списка,
      расхождения только через задокументированный allowlist);
- [ ] `scripts/grant-gate.sh` → exit 0;
- [ ] REF-сверки форка против трасс оригинала без дрейфа (L112);
- [ ] `Docs/behavior-matrix.md` заполнен, все расхождения записаны (L114).

**Откат (L117)**: трассы сохраняются (не удаляются); наша сборка удаляется,
оригинал восстанавливается из бэкапа системной папки
(`Docs/installer-protocol.md`, «Фазы и восстановление»).

## Скорость

~5–7 дней + окна доступа к авто (L117). Основное узкое место — физический
доступ: 12 сценариев × 2 прогона (оригинал/форк).
