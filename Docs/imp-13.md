# IMP-13 — Каналы canary→stable, on-car gate (SPEC L55/L32/R6, P1|M)

Статус: реализован. WP5/WP8: release channels.

## Требования SPEC

> **L55/R6**: Каналы canary->stable; gate релиза = on-car приёмка (WP8);
> стоп-триггер = crash-артефакты IMP-08 или провал чек-листа; проверки активного APK
> (SHA-256 PackageManager, rollback) ВНУТРИ установщика; OTA не переносим.

## Реализация

- **fork.config.toml** — stable_ids (уже есть).
- **`.github/workflows/promote-stable.yml`** — manual workflow: принимает version +
  acceptance-token, создаёт git tag v<VERSION> как маркер stable релиза.
- **Canary**: сборка по git push (существующий ci.yml — build.N из run_number).
- **Gate**: stable promotion только через ручной запуск с acceptance-token.