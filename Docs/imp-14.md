# IMP-14 — Миграционный движок установщика (SPEC L56, WP6, P0|M)

Статус: в разработке. Priority P0|M. WP6: УДАЛЕНИЕ ОРИГИНАЛА + МИГРАЦИЯ + RESIDUE (L140-L147).

## Требования SPEC

> **L56 IMP-14**: Миграционный движок установщика (реализация в WP6). P0|M
> **L140**: Цель: безопасная замена оригинала нашей сборкой с импортом настроек (D5,D6).
> **L141**: Детект оригинала: package names совпадают (D4) + отпечаток подписи = original-fingerprint.
>   Оба признака = оригинал (миграция). Имена совпадают, отпечаток наш = форк (обновление).
> **L142**: Последовательность миграции (a-h)
> **L143**: Residue-чеклист с пост-проверками
> **L144**: Обработка ошибок: ошибка миграции != падение установки; fallback = чистая установка
>   + отчёт «миграция прервана на этапе X»; backup ВСЕГДА до любых удалений.
> **L145**: Remove форка: возврат стока из backup/; очистка /data/local/<mig>/; проверка WRITE_CANBUS
> **L146**: Приёмка (fake ADB, 5 сценариев)

## Архитектура

Миграция реализуется как новая операция `Action::Migrate` в installer-core (Rust), третья ветвь рядом с `Install`/`Remove`. Классические bash-команды для этапов миграции добавляются в `install.sh` и `remove.sh` (для sync-classic-commands.py) либо напрямую в `classic_commands.rs`.

### Инвентаризация: что уже есть

| Компонент | Статус | Примечание |
|-----------|--------|------------|
| `Action::Install` / `Action::Remove` | ✅ | `plans.rs` enum |
| `Engine::execute()` install/remove branch | ✅ | ~1554 строка |
| `classic_steps()` install/remove | ✅ | `plans.rs` |
| `root_sequence()` | ✅ | remount/rw, disable-verity |
| `push_file()` | ✅ | атомарный push + chown |
| `shell()`, `ignore()`, `postflight_shell()` | ✅ | ADB helpers |
| `native_broadcast()`, `verify_active_apk()` | ✅ | пост-проверки |
| `resolve_canbus_conflict()` | ✅ | VoyahHlCTRL |
| `inventory::diagnose()` | ✅ | состояние: clean/original/fork |

### Что нужно добавить

| Компонент | Файл | Оценка |
|-----------|------|--------|
| `Action::Migrate` | `plans.rs` | S |
| `classic_steps()` migrate branch | `plans.rs` | S |
| migrate branch in `plan()` | `plans.rs` | S |
| `inventory.migration_state` (original/fork/other) | `inventory.rs` | M |
| `IMP14_MIGRATE_STAGING` | `classic_commands.rs` | M |
| `IMP14_BACKUP` | `classic_commands.rs` | M |
| `IMP14_REMOVE_ORIGINAL` | `classic_commands.rs` | M |
| `IMP14_RESTORE` | `classic_commands.rs` | M |
| `IMP14_RESIDUE_CHECK` | `classic_commands.rs` | M |
| `migrate_staging()` | `engine.rs` | M |
| `migrate_backup()` | `engine.rs` | M |
| `migrate_removal()` | `engine.rs` | M |
| `migrate_restore()` | `engine.rs` | M |
| `migrate_residue()` | `engine.rs` | M |
| `execute()` migrate branch | `engine.rs` | L |
| 5 fake ADB acceptance scenarios | `tests/` | L |

### Шаги миграции (L142 a-h)

```
migrate-detect       → inventory: определить оригинал/форк (L141)
migrate-staging      → (a) cp -a data/ui_prefs + native_data → /data/local/<mig>/
migrate-backup       → (b) adb pull /system/priv-app/ → ./backup/ (на компьютер)
migrate-consent      → (c) guided-удаление с подтверждением (y/N)
migrate-removal      → (d) pm uninstall + remount + rm /system/priv-app/ + sync + remount ro
migrate-reboot       → (e) adb reboot
install-fork         → (f) штатный процесс установки (уже есть)
migrate-restore      → (g) chown + merge prefs → отчёт
migrate-verify       → (h) adb reboot; verify grantedPermissions + WRITE_CANBUS
residue-check        → (L143) чеклист остаточных артефактов
```

### Обработка ошибок (L144)

- `migrate-staging`/`migrate-backup` ошибка → abort миграции, чистая установка без миграции
- `migrate-consent` отказ → отмена всей операции
- `migrate-removal` сбой → abort, восстановление из backup
- `migrate-restore` частичный сбой → отчёт «пропущено N ключей», установка не отменяется
- Backup ВСЕГДА до любых удалений (проверяется на уровне engine)

### Детект оригинала (L141)

Логика в `inventory::diagnose()`:
1. `pm path --user 0 <pkg_ui>` = найден → package names совпадают (D4)
2. `keytool -printcert -jarfile /system/priv-app/<pkg>/<pkg>.apk | grep SHA256` → fingerprint
3. Сравнение с original-fingerprint (`Docs/original-fingerprint.md`):
   - Совпадает → `MigrationState::Original`
   - Совпадает с нашим отпечатком → `MigrationState::Fork`
   - Не совпадает → `MigrationState::OtherFork`

### Размер

WP6 целиком — ~1500 строк нового Rust-кода + ~300 строк bash-команд + ~500 строк тестов.
Фазирование:

| Фаза | Содержание | Строк |
|------|-----------|-------|
| 1 | Action::Migrate + шаги + инвентаризация | 300 |
| 2 | staging + backup + removal ADB команды | 400 |
| 3 | restore + verify + residue | 400 |
| 4 | Обработка ошибок + remove-fork | 200 |
| 5 | 5 сценариев fake ADB | 500 |

## Риски / ограничения

- Миграция с прерванным наполовину удалением: backup на компьютере — основной механизм восстановления
- Отпечаток оригинала (original-fingerprint) живёт в отдельном файле — его обновление требует перевыпуска
- `+` в путях /data/local/<mig>/: имя миграционной папки — фиксированный `voyahtune-migrate` (без плюса), чтобы избежать проблем с shell
- Residue-чеклист не блокирует установку — только events + warnings
- Поле `legacy_migrated` уже есть в `Engine` — используется для миграции app_client.js; IMP-14 добавляет ещё один флаг `migration_pending`

## Тесты

TODO после фазы 5: `Installer/tests/test_migration.py` — 5 fake ADB сценариев.