# Тестирование VoyahTune

## Уровни

| Уровень | Где | Когда |
|---------|-----|-------|
| Static / shell syntax | `bash -n` по `.sh` | каждый PR с shell |
| Installer regression | `Packaging/tests/test_apollo_direct_only.sh` | installer / Apollo / direct path |
| JVM unit | `Native/app/src/test` (policy и т.п.) | Java-правки Native |
| Локальная сборка | Gradle `full` / `light` | Java/ресурсы (нужен Android SDK) |
| **Live на авто** | Voyah Free Sport+ 2026 | **перед любым hot-path / install-фазой** |

## Запуск (Windows)

```powershell
& "C:\Program Files\Git\bin\bash.exe" -n Packaging/installer/full/install.sh
& "C:\Program Files\Git\bin\bash.exe" Packaging/tests/test_apollo_direct_only.sh
```

`sh` в системе может не быть — использовать Git Bash по полному пути.

## Live-чеклист Sport+ 2026 (минимум)

1. `adb devices` → one `device`.
2. Установка full (или light) → без `!!!` → **две** перезагрузки по плану.
3. `SetModesService` up после 2-й перезагрузки; «Применить» → режим на приборке.
4. Пробуждение → восстановление режима.
5. Откат `remove.*` → система штатная.
6. TUI (если менялся): `install-tui.*` → verify exit code ≠ 0 при ошибке.

## Что нельзя тестировать без авто

- Реальные CAN-отправки, boot-hook, disable-verity, Frida в system_server, частота пробуждений.
- Значения `docs/audit/compatibility-matrix.md` (остаётся draft).

## Приёмка фиксов из плана

Фикс считается принятым только если:

1. Тройной анализ записан (или понятен из PR/сессии);
2. Статические проверки зелёные;
3. Для hot-path / install — **живой** прогон на Sport+ 2026 (иначе помечено «требует live-теста» в `HISTORY.md`).
