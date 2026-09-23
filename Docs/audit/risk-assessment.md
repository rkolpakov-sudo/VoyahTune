# Оценка рисков VoyahTune

**Коммит:** `45beee4` (master). **Дата аудита:** 2026-09-23.
**Основа:** полный код-ревью Native (33 файла), RestoreMode (13 файлов), 5 Frida-скриптов, установщиков (12+ скриптов), документации.

---

## Матрица рисков (упорядочена по критичности)

| # | Риск | Компонент | Вероятность | Критичность | Меры / смягчение |
|---|------|-----------|-------------|-------------|------------------|
| R1 | Ошибка в CAN-политике/транспорте → неверная команда в шину во время движения | Native CanSender/Policy | Средняя | **Критическая** (безопасность) | Fail-closed guard в CanSender; юнит-тесты policy; тесты на реальном авто обязательны |
| R2 | Крэш system_server из-за vd_bypass (приватные поля WM, rename в новых Android) | Frida vd_bypass | Средняя на новых прошивках | **Критическая** (soft-reboot) | Try/catch, eternalize, fail-closed UID; **матрица прошивок**; не расширять hot-path |
| R3 | Стороннее приложение пишет через RestoreModeContentProvider (exported без permission) в driveMode/forcedEv | RestoreMode provider | Высокая (экспорт открыт) | **Критическая** (поведение авто) | Добавить signature read/writePermission; проверить контракт с Native |
| R4 | Подделка broadcast TRIP/BATTERY_HEAT/MODE_SYNCED (RECEIVER_EXPORTED без permission) → неверный safety-UI прогрева | RestoreMode receivers | Высокая | Высокая (driver-facing UI) | Распространить signature-permission (паттерн SETTING_SYNCED уже есть) |
| R5 | `debugMode` застрял true → restore «успех» без реальной отправки CAN | Native CanSender | Низкая | Высокая | Запретить debugMode в release/full; warning-notification |
| R6 | Межканальная гонка JNI vs OEM Binder (нет общего lock) | Native CAN | Низкая | Высокая | Единый CAN epoch/fence или writer-поток |
| R7 | `setenforce 0` на каждом буте — SELinux выключен глобально | boot-hook | **Гарантировано** | Высокая (security) | Архитектурное решение проекта — задокументировать в threat model; компенсировать минимальностью attack surface |
| R8 | Сбой в середине install.sh → смешанное состояние; при ребуте подхватится частичный boot-hook | Installer full | Средняя | Высокая | Идемпотентный re-run; инструкция «не перезагружать»; **marker «install in progress»** (TODO) |
| R9 | light/install.sh: push без exit-code/restorecon → молча битая установка | Installer light | Средняя | Высокая | `|| exit 1` + restorecon (4 строки) |
| R10 | OEM обновил CanBus APK → reflection schema verify fail → полный отказ Apollo TLC | Native ApolloTlc | Средняя при OTA | Средняя (fail-closed, функционально) | Матрица совместимости; человекочитаемое сообщение в UI |
| R11 | OEM обновил классы лаунчера/KeyManager/MultiDisplay → хук молча отключается | Frida (все) | Средняя при OTA | Средняя | Fail-soft заложен; мониторинг логов инжекта; **ротация логов** (TODO) |
| R12 | Несовместимость sh/bat: CANBUS owner abort vs continue | Installer | Средняя | Средняя | Привести .bat к .sh или задокументировать |
| R13 | remove не восстанавливает 5 js из backup (только rm) → неполный откат | Installer remove | Средняя | Средняя | Восстановление по схеме load.bin (5 строк) |
| R14 | Рассинхрон STOCK_PREFIX (launcherdock) ↔ ffBlacklisted (vd_bypass) → док поверх окна | Frida | Низкая (ручной синхрон) | Средняя | Автотест в test_apollo_direct_only.sh |
| R15 | Неограниченный рост voyah_*.txt при персистентном сбое инжекта | load.bin/RC | Низкая | Средняя (диск) | Ротация 1 МБ как у Apollo |
| R16 | NowPlayingProvider exported + строковый ключ в коде (minify=false) | Native IPC | Средняя | Средняя | Binder.getCallingUid / signature-perm; R8 minify |
| R17 | Messenger bind SetModesService без проверки calling-uid | Native IPC | Низкая (если manifest ограничивает) | Средняя | getCallingUid в IncomingHandler |
| R18 | Гонка SplitStore load/mutate/save (lost update); index-based адресация dock/steer пресетов | RestoreMode | Средняя | Низкая-Средняя | UUID-адресация, single writer |
| R19 | NPE: onActivityResult data.toString(); StarButton process death; portrait layout без mainContent | RestoreMode | Средняя | Средняя (краш UI) | Null-guards; удалить legacy layout/StarButton |
| R20 | `registerReceiver(RECEIVER_EXPORTED)` при minSdk=30 → NoSuchMethodError на API <33 | RestoreMode | **Зависит от API ГУ** | Высокая если API<33 | Проверить фактический API; compat-регистратор; lint NewApi |
| R21 | Бэкапы не защищены от изменения/потери (нет манифеста/хэшей) | Installer | Низкая | Средняя | Backup manifest + SHA-256 (Фаза 3 плана) |
| R22 | Подмена релиза (нет цифровой подписи) | Release | Низкая | Критическая | Подпись релизов openssl (Фаза 3 плана) |
| R23 | Зависимость от одного/few разработчиков; нет дат в hownews; нет матрицы совместимости | Docs/process | **Факт** | Высокая (bus factor) | Документирование (Фаза 0–1 плана) |
| R24 | Нет тестовой инфраструктуры для CAN; только 82+ policy unit tests | Tests | **Факт** | Высокая для рефакторинга | Чек-лист + basic_check.sh + расширение policy tests (Фаза 2 плана) |
| R25 | disable-verity может быть необратим / лишить гарантии | Installer | Средняя | Критическая (юридически/hardware) | Явные предупреждения пользователя (README) |
| R26 | Рекламация `push.sh` в корне (чужие пути) | Dev tooling | Низкая | Низкая | Guard / перенос в Utils/ |

---

## Сводная оценка по категориям

### Критические (требуют действия в Фазах 0–3)
- R1 (CAN correctness), R2 (system_server), R3 (provider export), R5 (debugMode fail-open), R7 (SELinux — задокументировать), R22 (подпись), R24 (тесты), R25 (verity warnings).

### Высокие
- R4 (receivers), R6 (межканальная гонка), R8–R9 (installer atomicity/light), R20 (API compat), R23 (bus factor/docs).

### Средние
- R10–R19, R21.

### Низкие
- R26 + мелкие UI/NPE.

---

## Риски, характерные для автомобильного ПО (специфика VoyahTune)

| Фактор | Проявление в проекте | Следствие для рефакторинга |
|--------|---------------------|---------------------------|
| Железо/шина CAN | 10-байт кадры TX 77/58/46/36/28, VIN-специфичные команды | Моки невозможны; каждое изменение Policy/Transport → тест на авто |
| OTA прошивки | OEM rename классов → Frida/schema verify fail | Матрица совместимости обязательна; fail-closed/fail-soft задокументировать |
| Две перезагрузки | install.sh state machine | Не ломать атомарность фаз; marker install-in-progress |
| priv-app + 34 привилегии | INJECT_EVENTS, REBOOT, DEVICE_POWER… | Минимизировать surface; signature-permissions на IPC |
| Frida в system_server | vd_bypass | Не рефакторировать hot-path без живых голов |
| Физическое тестирование | Нет CI для CAN | Только инкрементальные изменения с ручным чек-листом |

---

## Рекомендации по приоритетам (согласованы с планом Фаз 0–3)

1. **Фаза 0 (сейчас):** этот аудит + матрица совместимости (заполняется живыми тестами).
2. **Фаза 1:** документация IPC-контрактов (Messenger codes, provider columns, Settings.Global keys) — снимает R18/R23.
3. **Фаза 2:** чек-лист + `basic_check.sh` — снимает R24 для regression smoke.
4. **Фаза 3:** backup manifest, `--dry-run`, коды ошибок, подпись релизов — снимает R8/R21/R22.
5. **Безопасность (можно параллельно Фазе 1, минимальные PR):**
   - R3: signature-permission на RestoreModeContentProvider.
   - R4: signature-permission на незащищённые receivers.
   - R12: синхронизация .bat CANBUS behavior.
   - R9: `|| exit 1` + restorecon в light/install.sh.
   - Ротация логов (R15).
6. **Не делать без тестов на авто:** изменения CanSender/ApplyEngine/Transport/Policy, vd_bypass hot-path, install.sh phase order.
