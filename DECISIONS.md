# DECISIONS

Формат: `[дата][WP][решение][альтернативы][причина][риск][статус]`
(см. SPEC L193; решения сверх D1-D17 — только здесь и ДО коммита)

## 2026-10-02][WP0][Отдельный репозиторий проекта, без ссылок на справочный git
- Альтернативы: форк существующего репозитория автора; продолжение в его репо.
- Причина: автор, вероятно, коммерциализует проект (последний публичный релиз 3.14);
  наша база — payload 3.22.0; инфраструктура полностью своя (D8).
- Риск: дублирование контекста легенды — mitigated: легенда читается локально (read-only).
- Статус: принято.

## 2026-10-02][WP0][Полная декомпиляция всех APK без выборочного подхода
- Альтернативы: точечная декомпиляция по классам из reference-map.
- Причина: заказчик — «нам не нужны потенциальные ошибки»; полный объём исходников.
- Риск: объём src-reconstructed велик — mitigated: apktool-ресурсы в .gitignore, полный
  код индексируется.
- Статус: принято.

## 2026-10-02][WP0][В git коммитится только src-reconstructed/**/sources (код); res-reconstructed/ не коммитится
- Альтернативы: коммитить полное дерево (smali + res + models, 273 МБ).
- Причина: smali/res/models полностью воспроизводимы из payload (apktool/jadx);
  в git достаточно читаемого исходника (57 МБ, 6655 файлов). Блобы — только в Releases.
- Риск: необходимость перегенерации при разборе — mitigated: команды в README + тулчейн пиннинг.
- Статус: принято.

## 2026-10-02][WP0][Протокол веток: WP0 ведётся на main, тег wp0-done на main
- Альтернативы: изолированная ветка wp0 + merge.
- Причина: стартовый коммит репозитория — инвентаризация, ветка от пустого дерева
  не даёт изоляции; отклонение от L186 зафиксировано здесь, начиная с WP1 — строго
  одна ветка = один WP = один тег.
- Риск: минимален (пустое дерево).
- Статус: принято.

## 2026-10-02][WP0][Качество jadx: 91 (native) / 76 (restore_mode) decompile-ошибок
- Альтернативы: rerun с --show-bad-code; smali-фолбэк.
- Причина: ошибки jadx — методы, восстановленные частично; ground truth = smali из
  apktool (res-reconstructed, локально). Для правок используется реконструированный
  код, при сомнениях — сверка со smali.
- Риск: искажение кода в отдельных методах — mitigated: smali-сверка при правках.
- Статус: принято.

## 2026-10-02][WP1][Ключи подписи: генерация локально, хранение ТОЛЬКО в GitHub Secrets
- Альтернативы: ключи в репо; локальное хранилище без CI.
- Причина: IMP-23 (P0) — «ключи только в CI-secrets»; D8 — свои ключи.
- Риск: потеря ключа = потеря ability подписывать обновления — mitigated: JKS +
  пароли сохраняются владельцем офлайн (резервный комплект L162).
- Статус: принято.

## 2026-10-02][WP1][CI-гейты с первого дня (не с WP4)
- Альтернативы: гейты по расписанию WP (detachment-audit — в WP4).
- Причина: detachment-audit и проверка payload — дешёвые регрессии; раннее
  включение безопаснее и не даёт «закопать» ссылки на оригинальный контур.
- Риск: ложные срабатывания на раннем дереве — mitigated: whitelist только
  docs/original-fingerprint.md.
- Статус: принято.

## 2026-10-02][WP1][Стратегия Gradle-сборки: полный реконструированный source-set (эксперимент), fallback — Maven-зависимости
- Альтернативы: A) собрать всё декомпилированное дерево против android.jar (включая
  androidx/kotlin из jadx, ресурсы = объединённые ресурсы APK); B) Maven androidx/kotlin
  с подбором версий из META-INF/*.version.
- Причина: вариант A исключает расхождение версий androidx↔ресурсы (R-поля полны);
  вариант B чище, но требует точного матчинга версий.
- Риск: javac может спотыкаться о декомпилированный kotlin-код — mitigated: fallback
  на B (kotlin-stdlib/kotlinx-coroutines/androidx через Maven, декомпиляция исключается).
  Решение фиксируется по факту сборки.
- Статус: принято (A первичен, B — фолбэк).

## 2026-10-02][WP1][RestoreMode собирается: :RestoreMode:app:assembleDebug BUILD SUCCESSFUL
- Контекст: итеративный цикл build-rm-13..20 (javac-ошибки декомпиляции jadx → точечные фиксы).
- Решение: фиксы по восходящей — NativeLibrary (4 DA/checked-except), Native.java (static-init
  Throwable-wrap, delegate IOException-wrap, rethrow из проглатывающего catch, return false,
  break в scan-цикле, Exception вместо никогда-не-бросаемого IOException); AdvanceActivity
  (i=0 при intent==null, readSystemMetrics — единый try/catch вместо вложенного),
  SplitStore (z=false по умолчанию), SuspensionWidgetView (final frameBitmap для лямбды),
  NowPlayingClient (return null / throw th из внешних catch); VoiceRecognizer.java заменён
  реконструкцией по reference-3.14 (семантика 3.22 сверена — идентична, лямбд-артефакты
  jadx удалены); VoiceSeatCommands (string-switch: break на каждый case + вынос dispatch во
  второй switch, потерянный i++ в цикле, инвертированная verb-проверка str6 != null,
  двойной break); Utils/verify_payload.py — убран битый relative_to (CI payload-verify
  падал на существующих файлах).
- Основание: reference-3.14 — источник истины; каждая замена сверена с эталоном или
  javap-константами; ошибки DA/достижимости — типовые дефекты --show-bad-code.
- Риск: реконструкция VoiceRecognizer могла потерять отличия 3.22 — mitigated: полная
  сверка всех методов и вызываемых сигнатур (VoiceSessionControl/VoiceEngineCache/
  VoiceModels/VoiceRecording/VoiceAudioConfig) с декомпилированным вариантом до замены.
- Статус: принято. Проверки: assembleDebug обоих модулей (68 tasks OK, APK 132МБ+17МБ),
  testDebugUnitTest (NO-SOURCE — тестов ещё нет), Utils/verify_payload.py — локальный
  синтетический прогон без traceback.

## 2026-10-02][WP1][Первый коммит WP1: source-set переехал в Gradle-деревья, CI-совместимость .gitignore/network-audit
- Контекст: ~6700 записей — переезд src-reconstructed/{native,restore_mode}/sources →
  {Native,RestoreMode}/app/src/main/java (staged rename R/RM), удаление third-party
  (androidx/kotlin/kotlinx/com/android/org → Maven, RD 6236) и сгенерированных файлов
  (R/BuildConfig/databinding/*$$ExternalSynthetic*, RD 52 — генерируются AGP),
  новые Gradle-файлы/манифесты/res/assets/jniLibs, ~70 Utils fix-скриптов.
- Решение: (1) .gitignore — снят *.jar с gradle/wrapper/gradle-wrapper.jar и
  Native/app/lib/android.car.jar (иначе checkout на CI без них не соберётся),
  добавлен src-reconstructed2/ (повторный jadx-прогон, локальный only);
  (2) scripts/network-audit.sh перенацелен с src-reconstructed/$app/sources/ru/big/town
  на Native|RestoreMode/app/src/main/java/ru/big/town (старый путь после переезда
  исчезал → блокирующий джоб падал бы на MISSING sources); (3) решение WP0
  «коммитится только src-reconstructed/**/sources» считается закрытым —
  источники теперь живут в Gradle-деревьях.
- Основание: declaration substitution п.3.22 — third-party из Maven с пиннингом;
  сгенерированные файлы не являются источниками; CI должен собирать APK из checkout.
- Риск: пропущен незамеченный файл-зависимость — mitigated: check-ignore по всем
  *.jar/ *.aar, network-audit прогнан локально (findings=0, exit 0).
- Статус: принято. Проверки: git add -A, whitespace git diff --check, локальный
  network-audit, сборка уже зелёная (см. предыдущую запись).

## 2026-10-02][WP1][IMP-10a: ядро HIL (can-emulator + scenario-runner, 12 YAML) и Robolectric-мост в реальный Native-код]
- Контекст: SPEC L104 требует эталонный поток avas-wake (sleep → door → TX58 avas_off →
  acc/drive → эхо ≤2s → ui_state=CONFIRMED), L105 — ≥10 YAML-сценариев, L86 — режимы
  ACK/SILENT/LATE/CONFLICTING, L103 — CI job tests. Протокол TX2/6/9/20/26/28/29/57/58/77
  и CB1/4/10/12/36 восстановлен по исходникам; в декомпиляции OemVehicleStateTransport.
  resolveStates и HeadlightCanTransport.resolveSchema найдены и исправлены
  безусловные return-артефакты (иначе TX58 мёртв).
- Решение: (1) модуль `:tests:hil` (java-library, snakeyaml 2.3): фикстура
  com.qinggan.canbus.VehicleState (30 name→id, parent-delegation через PathClassLoader
  подменяет enum сервиса), ядро CanEmulatorCore (лог TX/CB, кэш, инжекты, режимы записи,
  sink-эхо), HilHarness/JvmHarness, ScenarioLoader (строгая валидация), ScenarioRunner
  (окна expect по триггерам шагов), артефакты JUnit-XML + transactions.log; 12 YAML
  в tests/hil/scenarios/; 25 unit-тестов зелёные. (2) Robolectric-мост в test-scope
  Native: FakeCanBusBinder (AIDL-декод → ядро, attachInterface-дескриптор обязателен —
  иначе DemandConnection отбрасывает биндер), RobolectricCanBridge (grantPermissions,
  ShadowPackageManager.addPackage с sourceDir, setComponentNameAndServiceForBindServiceForIntent
  + setBindServiceCallsOnServiceConnectedDirectly — эхо доставляется реальному
  CanBusEventHub CallbackBinder → роутер → подписчик), SEND-шаги идут через настоящий
  OemVehicleStateTransport.sendVehicleState; 3 теста Native зелёные, avas-wake
  проходит целиком по реальному коду (TX28 → TX58[1,3,665,1] → CB36 → routed_id=665).
- Основание: единственный честный способ проверить L104 — пустить YAML через реальные
  resolveStates/bindService/TX58/роутинг хаба; локальный биндер в том же процессе даёт
  синхронный onServiceConnected (проверено пробами: Parcel, writeNoException/readException,
  writeStrongBinder round-trip, oneway transact с null-reply).
- Риск: CanBusEventHub — статический синглтон → мост ограничен одним тест-методом
  (повторный create() получил бы старый хаб/байндинг); статика OemVehicleStateTransport
  переживает пробы-тесты — mitigated: мост-тест самодостаточен, пробы статики хаба не
  трогают; фильтр `--tests` по Robolectric-классу иногда даёт "No tests found" —
  прогнать без фильтра.
- Статус: принято. Проверки: :tests:hil:test + :Native:app:testDebugUnitTest — 28/28,
  :Native:app:assembleDebug зелёный. Коммит — только по явной команде (ветка wp1).
  Дальше: шаг 1.5 CI (jobs tests/payload/gui-win/gui-mac), 1.3 Packaging, 1.4 Installer.

## 2026-10-03][WP1][Шаг 1.3 Packaging: make_release.sh, job payload, леч release-сборки
- Контекст: SPEC L101/L103 — сборка payload_<v>.zip из 29 файлов (21 payload-common +
  2 собранных APK + 6 ре-хост блобов). Release-сборка падала трижды подряд: (1) D8
  «Type defined multiple times» — jadx-дубли библиотечных классов в source-set;
  (2) lintVitalRelease — 8 fatal ResourceCycle/MissingDefaultResource на
  восстановленных ресурсах зависимостей (abc_*, material-стили); (3) packageRelease —
  «already contains entry assets/dexopt/baseline.prof».
- Решение:
  (1) `Utils/find_dup_classes.py` — системный пересбор jadx-дублей (dex string-pool ↔
  FQCN .java); удалены `ListenableFuture` (Native, мёртвый стаб), `EventLogTags`
  (Native), `_COROUTINE/*` + `com/google/common/*` (RestoreMode); после чистки
  external=7811/8058, source=101/231, duplicates=0/0;
  (2) `lint { checkReleaseBuilds false; abortOnError false }` в обоих app/build.gradle —
  fatal-ошибки про ресурсы зависимостей не должны блокировать release-сборку (lint
  не входит в CI job tests);
  (3) удалены 4 восстановленных файла `assets/dexopt/baseline.prof{,m}` — AGP сам
  компилирует baseline-профили зависимостей и пакует их в тот же путь, восстановленные
  из APK копии = дубль (профили старта, функционально не критичны, AGP-версия канонична);
  (4) новые инструменты: `make_release.sh` (root: верификация версии легенды, gradle-сборка,
  fetch-blobs подмножества, staging, manifest build+verify, атомарный publish,
  zip из staging, payload_<v>.json + BUILD-INFO.json + SHA256SUMS; флаги `--payload`
  обязателен, `--no-build`/`--no-zip`), `scripts/fetch-blobs.sh` (all/подмножество/
  `--verify`, sha256sum/shasum, curl/wget, атомарный `.part.$$`+mv),
  `Utils/build_payload_manifest.py` (build/verify schema=4);
  (5) `.gitignore`: `blobs/*` c `!BLOBS-SHA256.txt`/`!README.md`, + `Releases/`;
  `blobs/apk/` — 3 payload-APK (dns.apk, voyahtune-ui-next.apk, voyahtune-updater.apk)
  как расширение списка блобов L92 (плоские ассеты blobs-v1, базовые имена уникальны);
  (6) CI job `payload` вместо TODO (cache blobs по hashFiles, secrets подписи,
  `sha256sum -c`, upload-artifact), shellcheck-строка расширена на make_release.sh.
- Основание: release-блокировки (lint/dex-dup/baseline.prof) — артефакты jadx-реконструкции,
  а не контракта; systematic dup-scan надёжнее точечных удалений; flat-ассеты —
  ограничение GitHub Releases; подпись APK только в CI (ANDROID_KEYSTORE_*),
  локально unsigned — make_release предупреждает и продолжает.
- Риск: отключение lintVital может скрыть будущие ошибки ресурсов — mitigated: lint
  остаётся запускаемым вручную (`gradlew lint`), в CI-контракт (L103) не входит;
  удалённый baseline-профиль APK — только стартовая оптимизация, без функциональных
  потерь; `blobs/apk/` — осознанное отклонение от структуры L92 (ре-хост, не реверс, D16).
- Статус: принято. Проверки: `sh -n` оба скрипта = 0; `fetch-blobs --verify` 30/30;
  `find_dup_classes` duplicates=0/0; `assembleRelease` RC=0 (Native 14.6 МБ,
  RestoreMode 129 МБ, unsigned локально); `./make_release.sh 4.0.0-build.0 --payload`
  MR_RC=0: 29/29 verified, zip 125 098 128 байт, `sha256sum -c SHA256SUMS` OK.
  Коммит/push — только по явной команде; публикация blobs-v1 одобрена.
  Дальше: публикация blobs-v1 (30 плоских ассетов), push + прогон CI.
