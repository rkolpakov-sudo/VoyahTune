# Отчет по Фазе 0: Подготовка и полный аудит кода

**Дата:** 2026-09-23
**Коммит:** `45beee4` (master, nexron171/VoyahTune)
**Статус:** ✅ Аудит кода завершен (чтение и анализ — без изменений кода)

---

## Выполненные задачи

- [x] Клонирование репозитория (локальный checkout был пуст — только план)
- [x] Код-ревью Native: 33 Java-файла + manifest + Gradle + native-lib.cpp
- [x] Код-ревью RestoreMode: 13 Java-файла + manifest + layouts
- [x] Код-ревью Frida: 5 скриптов (steeringwheelkeys, launcherdock, multidisplay, vd_bypass, apollo_tech)
- [x] Код-ревью установщиков: full/light install+remove (.sh/.bat), common DNS, make_release.sh, boot-hook RC, whitelist XML, test_apollo_direct_only.sh
- [x] Аудит документации: readme, hownews, Packaging README, Native readme
- [x] Диаграммы архитектуры (компоненты, установка, данные)
- [x] Оценка рисков (матрица R1–R26)
- [x] Матрица совместимости (черновик на основе docs/code, требует живых тестов)

## Результаты (выходные артефакты)

```
docs/audit/
├── code-review-native.md          # 33 файла, CAN, риски C/H/M/L, потоки данных
├── code-review-restoremode.md     # 13 файлов, IPC, DrivePreferences, риски
├── code-review-frida.md           # 5 скриптов, хуки, риски system_server
├── code-review-installer.md       # 12-фазная диаграмма install.sh, атомарность
├── architecture-diagrams.md       # компоненты, последовательность, данные
├── risk-assessment.md             # матрица рисков + приоритеты
├── compatibility-matrix.md        # прошивки × функции (черновик)
└── phase0-report.md               # этот файл
```

### Ключевые находки (топ-10)

1. **RestoreModeContentProvider exported без permission** — любой app может писать driveMode/forcedEv (R3).
2. **5 незащищённых exported-receivers** в RestoreMode, включая safety-виджет прогрева (R4).
3. **debugMode fail-open** — эмуляция CAN может засчитаться как успех restore (R5).
4. **vd_bypass в system_server** — крэш = soft-reboot; версионная хрупкость приватных полей (R2).
5. **light/install.sh не проверяет adb push** и не делает restorecon (R9).
6. **Межканальная гонка JNI vs Binder** — нет общего CAN lock (R6).
7. **setenforce 0 на каждом буте** — SELinux выключен (R7, архитектурное решение).
8. **API risk:** `RECEIVER_EXPORTED` (API 33) при minSdk=30 — проверить фактический API ГУ (R20).
9. **Нет матрицы совместимости и дат в hownews**; тестировано только рестайл 2024 (R23).
10. **Сильные стороны:** fail-closed Apollo schema, epoch/coverage ApplyEngine, per-file атомарность установщика, 82+ юнит-тестов policy, static-тесты test_apollo_direct_only.sh.

### Сильные стороны архитектуры (не ломать)

- Единая точка CAN (`CanSender`) с lock/guard/fail-closed.
- Policy-классы чистые и тестируемые (JVM).
- Fail-closed verification в ApolloTlcService.
- Snapshot/rollback boot-hook; идемпотентный re-run установщика.
- Signature-permission уже применён к части IPC (SETTING_SYNCED, APOLLO_*, SPLIT_RATIO_SAVE) — распространить на остальные.

## Проблемы и риски

- Матрица совместимости — **черновик**: значения «функции по прошивкам» требуют живых тестов (нет доступа к авто из этой сессии).
- R20 (API level для RECEIVER_EXPORTED) не может быть закрыт без устройства.
- Frida-хуки и vd_bypass **не должны** рефакторироваться без тестов на целевых прошивках.
- Локальный git: ветка master отслеживает origin/master, незакоммиченный артефакт `Qwen_markdown_...md` в корне (посторонний файл плана).

## Следующие шаги (по плану)

1. **Фаза 1:** документирование (user/technical/developer) на основе docs/audit/* — IPC-контракты, boot-sequence, permissions, known-issues.
2. **Фаза 2:** тестовая инфраструктура — чек-лист, basic_check.sh, заполнение compatibility-matrix живыми тестами (мин. 3 авто).
3. **Фаза 3:** безопасность — backup manifest, --dry-run, коды ошибок, подпись релизов; **параллельно минимальные security-fix PR**: R3/R4 permissions, R9 light push, R12 sh/bat, ротация логов R15.
4. **Не делать** до Фазы 2: изменения CanSender/ApplyEngine/Transport/Policy, install.sh phase order, vd_bypass hot-path.
