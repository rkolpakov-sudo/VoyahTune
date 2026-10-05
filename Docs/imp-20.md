# IMP-20 — Model-based тесты state-machines (SPEC L61/L55, P1|M)

Статус: реализован. WP8: генератор перестановок событий state-machines.

## Требования SPEC

> **L61**: Model-based тесты state-machines: генератор перестановок событий; инварианты
> холостого хода; HIL-интеграция (IMP-10a); допускается упрощение.

## Реализация

**tests/hil/scenario/ScenarioRunner.java** — уже поддерживает сценарии с шагами
ActionStep и AssertAppStep, которые включают CAN-отправку и проверку состояния.

**tests/hil/CanEmulatorCore.java** — CAN-эмулятор с поддержкой VehicleState read/write,
подписка на callback'и.

Новый **tests/hil/model/StateSpace.java**: генерирует декартово произведение событий
для заданной state-machine (AVAS, LightMode, SleepController). Для каждой комбинации:
ActionStep (отправить CAN) + AssertAppStep (проверить состояние). Результат —
Scenario .json, загружаемый ScenarioLoader-ом и выполняемый ScenarioRunner-ом.

Инварианты холостого хода: assert что после N шагов без входных событий система
остаётся в последнем стабильном состоянии (no oscillation).