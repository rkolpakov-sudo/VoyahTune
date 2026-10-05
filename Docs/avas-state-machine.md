# AVAS state-machine (IMP-02, SPEC L44)

Целевое отличие форка: AVAS (звук предупреждения пешеходов, `HUM_VSP_FUNCTION_SW`,
TX57 id 665, 1=выкл/2=вкл) **запоминается и восстанавливается**, в отличие от
OEM 3.21/3.22 (эталон расхождения E2, сценарий P-05).

## Машина состояний

```
                 wake-событие (зажигание / дверь / первый Drive),
                 однократно на событие
        ┌──────────────────────────────────────────────┐
        ▼                                              │
      IDLE ──onWakeEvent──> WAKE_APPLY ──read-back────>│ (подтверждено)
        ▲                        │                     │
        │                        │ первый Drive        │ не подтверждено
        │                        │ вслед за дверью     │ (FAILED/TIMEOUT):
        │                        ▼                     │  latch: ≤3 попытки
        │                  CAPTURE_WINDOW ─────────────┘  на цикл,
        │                   │        │                    затем IDLE
        │   штатное изменение        │ dверь/сон
        │   (эхо TX57 != снапшот):   │
        │   снапшот := факт          │
        └────────────────────────────┘
```

| Состояние | Входит | Выходит |
|---|---|---|
| `IDLE` | старт; подтверждение записи; сон | wake-событие → `WAKE_APPLY`; первый Drive при взведённой двери → `CAPTURE_WINDOW`; эхо вне окна ≠ снапшот → `CORRECTIVE_REAPPLY` |
| `WAKE_APPLY` | wake-событие (зажигание/дверь/первый Drive), однократно на событие: пишется снапшот через read-back-диспетчер | подтверждение (эхо == ожидание) → `IDLE` |
| `CAPTURE_WINDOW` | первый Drive **вслед за открытием двери** | дверь или сон → `IDLE` (окно закрывается) |
| `CORRECTIVE_REAPPLY` | изменение TX57 **вне** окна = OEM-сброс: корректирующее применение снапшота | подтверждение → `IDLE`; бюджет попыток исчерпан → `IDLE` (лог) |

## Правила

1. **Снапшот** (`snapshot`, значение TX57) — истина для восстановления.
   Миграция (SPEC): снапшот инициализируется **старым флагом пользователя**
   `disablePedestrianSound` при старте; **фактическое состояние** TX57 его
   уточняет: внутри окна — принимается (штатное изменение), вне окна —
   расхождение чинится коррекцией (флаг/снапшот побеждают факт).
2. **Пользовательский тумблер** (карточка/меню/руль/голос) коммитит снапшот
   ДО записи (`onUserToggle`), затем пишет через диспетчер IMP-01
   (read-back «после каждой записи»). Отказ записи откатывает снапшот.
3. **WAKE_APPLY**: однократно на wake-событие; пишет снапшот безусловно
   (OEM мог сбросить состояние во сне — эхо между событиями отсутствует).
4. **Окно захвата** открывается ТОЛЬКО после первого перехода в Drive
   (gear==3) вслед за открытием двери; закрывается дверью и сном.
5. **OEM-сброс** (эхо TX57 ≠ снапшот без активной записи):
   * внутри окна → `снапшот := факт` (запоминается);
   * вне окна → `CORRECTIVE_REAPPLY`: одна отправка диспетчера,
     который сам делает ≤3 попытки (1 + 2 ретрая, SPEC L181) с
     read-back окном 1500мс; после исчерпания — лог и `IDLE`
     до следующего wake-события (latch сбрасывается на wake/соне).
6. **Read-back источник**: эхо CB36 TX57 id 665 →
   `ReadBackTable.SOURCE_AVAS_TX57`; подтверждение/расхождение идут
   через `CommandStatusHub` → UI-бейдж (`commandStatusText`, фича `avas`).

## События (точки врезки в Native)

| Событие | Источник | Врезка |
|---|---|---|
| дверь открыта/закрыта | `DriverDoorStateController` (edge, только LIVE) | `AvasController.init` → подписка |
| первый Drive (gear==3 edge) | `GearStateController` | та же подписка |
| сон | `SetModesService.handleScreenOffFallback` / `handlePowerStateChanged` (sleep) | `AvasController.onSleep` |
| зажигание (wake) | `SetModesService.handleScreenOnFallback` / `handlePowerStateChanged` (wake, после `onWakeComplete`) | `AvasController.onWakeEvent` |
| эхо TX57 665 | `VehicleStateControllers` ( подписка id 665 ) → `ModeFeedbackController.onVehicleState` | `AvasController.onVehicleStateEcho` |
| пользовательский тумблер | MSG 21 (карточка/меню), `toggleSetting` (руль), `VoiceCommandController` | `AvasController.requestUserToggle` |
| restore-план (ручной apply) | `MainActivity.createCanRestorePlan` op «pedestrian sound mode» | `AvasController.applySnapshot` |

## UI

* Тумблер: карточка `tile_pedestrian` (MSG 21), меню `AdvanceActivity`
  (`pedestrianSoundGroup` → MSG 21 — ранее писало только pref),
  руль/голос (CAN + persist).
* Бейдж: `AdvanceActivity.commandStatusText` — фича `avas`
  (`cmd_feature_avas`), состояния `cmd_status_*` из IMP-01.

## Тесты

* `AvasStateMachineTest` (JVM, чистая): цикл
  «выкл → запереть(гостевой) → открыть → Drive» **×10**;
  правила окна/latch/миграции.
* `AvasReadBackEmulatorTest` (JVM, can-emulator + диспетчер IMP-01):
  запись пользователя → CONFIRMED; OEM-сброс вне окна → коррекция ≤3
  попытки → CONFIRMED; изменение внутри окна → снапшот без записи;
  полный цикл ×10.
* HIL YAML (`tests/hil/scenarios/avas-*.yaml`) — регрессия IMP-01/IMP-10a
  (`ScenarioSuiteTest`, `NativeHilScenarioTest`).
* on-car: цикл ×10 в окне кампании (пауза до IMP-06→IMP-01→IMP-02, K=3).

## Ограничения / риски

* Безэхо-запись (значение не изменилось) на реальном VCU может не породить
  CB36 → диспетчер выработает TIMEOUT после 3 попыток (бейдж «нет
  подтверждения», значение при этом корректно). Проверка — on-car ×10 (K=3).
* Снапшот, захваченный в окне, не записывается в prefs — UI-флаг
  обновляется только пользовательскими путями (SETTING_SYNCED).
* `LightStatus`-источник, restore-серию диспетчер не мигрирует (IMP-01
  ограничения сохраняются).
