# Backlog улучшений (I.6, полный каталог)

Формат: ID | категория | R-ссылка | приоритет | размер | WP-реализация
Ссылки: SPEC.md L43-L62. P0 — обязательно для G3, kill-критерии обязательны (L192).

| ID | Категория | R | P | Размер | Реализация |
|---|---|---|---|---|---|
| IMP-01 | корректность | R3 | P0 | M | WP3: CommandResult + read-back таблица, UI-бейджи |
| IMP-02 | восстановление | R1 | P0 | M | WP3: AVAS state-machine (ЦЕЛЕВОЕ отличие форка), K=3 |
| IMP-03 | корректность | R4 | P1 | S-M | WP3: Individual — выбор A/B по трассам WP2 |
| IMP-04 | состояния | R5 | P1 | M | WP3: SettingsRepository, политики источников истины |
| IMP-05 | корректность | R8 | P2 | S | WP3: ACC-авторетрай <=3, backoff 2/5/10с |
| IMP-06 | состояния | R2/R12 | P0 | S-M | WP3 (первым): SleepController + session_id |
| IMP-07 | графика | R12 | P0 | M | WP3: geometry-watchdog, K=2 |
| IMP-08 | стабильность | R2 | P0 | M | WP3: реестр поколений хуков + crash-артефакты |
| IMP-09 | транспорт | стаб. | P1 | S | WP3: приоритетные полосы CAN-диспетчера |
| IMP-10a | тесты | HIL | P0 | M | WP1: can-emulator + scenario-runner |
| IMP-10b | тесты | HIL | P0 | M | WP8: CAN-рекордер + генераторы OEM-сбросов |
| IMP-11 | supply | R7 | P0 | S | WP5: версионирование 4.x.y+build.N, CI-гейт |
| IMP-12 | supply | — | P1 | S-M | WP5: пиннинг тулчейна, офлайн-верификатор payload |
| IMP-13 | supply | R6 | P1 | M | WP5: каналы canary→stable, on-car gate |
| IMP-14 | supply | — | P0 | M | WP6: миграционный движок установщика |
| IMP-15 | голос | R10/D10 | P1 | M | WP3/WP7: prewarm по ACC ON; int8 уже в payload (!) |
| IMP-16 | графика/UX | R11 | P2 | S | WP3: сплит-drag после IMP-07 |
| IMP-17 | UX | R1/R3/R8 | P1 | S | WP3: бейджи состояния на карточках |
| IMP-18 | docs | — | P1 | S-M | WP9: behavior-matrix, version-mapping, avas-state-machine, can-interface |
| IMP-20 | тесты | — | P1 | M | WP8: model-based тесты state-machines |
| IMP-23 | security | — | P0 | S | WP4: CI-grep network API, ключи в CI-secrets |

## Заметки по инвентаризации (WP0)

- **IMP-15**: payload уже содержит `zipformer-ru-0.54-int8` (encoder 67.6 МБ int8) —
  исследование квантования вести от факта «уже int8», сравнивать с float-вариантом.
- **voyahtune-updater** (root-служба, ELF + init.rc) — удаление в WP4 (L128).
- OTA-bootstrap (`voyahtune-ota-bootstrap.json`) — не переносим (L63).
