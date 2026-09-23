# CAN и справочники

## Первоисточники (всегда сверяться)

| Файл | Что |
|------|-----|
| `Docs/CAN-команды.odt` | таблицы команд |
| `Docs/can.pdf` | PDF-справочник |
| `Docs/TRACE/` | трассы |
| `readme.md`, `hownews.md` | пользовательский FAQ и changelog |
| `Packaging/README.txt` | инструкция конечному пользователю |
| `docs/agent/PROJECT.md` | MSG_*, provider, флейворы |

**Никаких изменений CAN / policy / транспорта / бута «по интуиции»** — только после сверки со справочниками и аксиом (`AGENTS.md`).

## Кадры (из PROJECT/аудита)

- Длина кадра: **ровно 10 байт**.
- TX: `77` — режимы; `58` — свет; `46` / `36` / `28` — ICarSignalService / Apollo query.
- Точные payload-ы — только в `Docs/CAN-команды.odt` / `can.pdf`; в коде — policy-классы + `CanSender`.

## Стек отправки (full и light)

```
UI / ApplyEngine
    → policy (набор допустимых байтов)
    → CanSender (lock, fail-closed)
    → JNI cis_can_control_bytes (native-lib.cpp)
    → CAN
```

`native-lib.cpp` и `CanSender` — **hot-path**. Изменения — только отдельный проект + живые тесты на Sport+ 2026.

## Известные OEM-ограничения

- **4.1 OD:** энергия + рекуперация в шторке быстрого доступа — корректно.
- **7.1 OD:** только рекуперация в шторке (баг OEM). Панель приборов — почти всегда корректна.
- Это **не** баг VoyahTune; FAQ в `readme.md`.

## Тесты без авто

- `Packaging/tests/test_apollo_direct_only.sh` — static-проверки (запускать на каждом изменении installer/Apollo-путей).
- Юнит-тесты policy: `Native/app/src/test` (JVM).
- **Живые CAN-тесты** — только на Sport+ 2026 (см. `docs/audit/compatibility-matrix.md`).
