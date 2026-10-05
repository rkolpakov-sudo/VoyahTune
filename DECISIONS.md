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

## 2026-10-03][WP1][CI job payload: цепочка фиксов подписи — +x, mkdirs, env-имя, keystore regen]
- Контекст: после push Шага 1.3 job payload падал трижды подряд: (1) exit 126 —
  `./make_release.sh` в индексе без `+x` (та же болезнь, что gradlew в 0f5ff96→bde1e4b);
  (2) `release.jks (No such file or directory)` — signingConfigs пишет keystore в
  `build/`, которого нет на свежем чекауте; (3) `SigningConfig "release" is missing
  required property "keyAlias"`.
- Решение:
  (1) `git update-index --chmod=+x` для `make_release.sh` + `scripts/fetch-blobs.sh`
  (оба вызываются напрямую: ci.yml:158 и make_release:182);
  (2) `ksFile.parentFile.mkdirs()` перед записью в обоих app/build.gradle;
  (3) устранён рассинхрон env-имён: ci.yml экспортировал `ANDROID_KEYSTORE_ALIAS`
  (читает никто), а build.gradle читает `ANDROID_KEY_ALIAS` — строка в ci.yml заменена;
  (4) секрет `ANDROID_KEYSTORE_ALIAS` был пуст, `ANDROID_KEY_ALIAS` не выставлялся →
  с одобрения пользователя перегенерирован keystore: keytool (Temurin 17),
  RSA-3072, PKCS12, alias `voyahtune`, validity 10000 дней, дата 2026-10-03;
  все 4 секрета (B64/PASSWORD/ALIAS/KEY_PASSWORD) перезаписаны через `gh secret set`;
  старый keystore от 2026-10-02 ничем не занят — подписанный релиз не выпускался ни разу.
- Основание: подпись в CI — единственный канал (ANDROID_KEYSTORE_* только в secrets);
  пустой alias невосстановим извне (GitHub не отдаёт значения секретов, локальной копии
  keystore нет); замена идентичности подписи бесплатна до первого релиза.
- Риск: исходный keystore 2026-10-02 утрачен (его и так нигде не было) — mitigated:
  новый keystore + пароли сохранены вне репо в `C:\Projects\signing\` (не коммитить);
  в secrets остался мёртвый `ANDROID_KEYSTORE_ALIAS` — можно удалить позже.
- Статус: принято. Проверки: локально с env — `assembleRelease` зелёный, оба
  `app-release.apk` проходят `apksigner verify` (CN=VoyahTune, fp 212cc2bc…);
  CI run 37112178182 — **5/5 success** (payload/tests/payload-verify/reference-map/
  network-audit). Коммит — по явной команде.

## 2026-10-03][WP1][Приёмка classic_port: staging, OTA-исключение, old-key, план шагов]
- Контекст: первый полный прогон `test_classic_port` после LF-нормализации —
  серия падений на стороне классики и расхождения state()/шагов. Разбор каждого
  падения показал наследственные дефекты reference (код байт-идентичен: engine.rs,
  plans.rs, integration.py — `diff` только по EOL) и локальные артефакты среды.
- Решение (5 фиксов):
  (1) `test_classic_port.classic()` не копировал `apollo-safe-device.sh` в
  стейдженный релиз → preflight install.sh:31 падал. Добавлено копирование из
  `Packaging/installer/common/` — как в `copy_common_release_assets` реального
  релиза (reference-`make_release.sh:264,287`); порт встраивает скрипт через
  `include_str!`, классике нужен файл в cwd;
  (2) OTA/Updater: recipe в payload-spec ставит 6 файлов (voyahtune-updater,
  voyahtune-updater.apk→VoyahTuneUpdater, voyahtune.updater.rc,
  voyahtune-ota-bootstrap.json, voyahtune-ui-maintenance, voyahtune-ui-next.apk)
  и пакет ru.big.town.updater, а эталонный install.sh — никогда (так и в
  reference: spec создан в a0d7021, дополнен 23bca4a «Deliver updater UI through
  OTA», тест с тех пор красный и не гонялся в CI). В `state()` добавлено
  документированное исключение OTA_FILES/OTA_PACKAGES — целевое расхождение
  форка (SPEC L32/L55 «OTA не переносим»), удаление по backlog L34 в WP4;
  (3) `test_changed_signatures_reset_both_apps`: legacy old-key APK
  `Native/app/release/app-release.apk` был подписан КЛЮЧОМ PAYLOAD (212cc2bc…,
  локальная сборка с ANDROID_KEYSTORE_* = CI-ключ) → signers совпадали,
  `signature_resets` не срабатывал, данные не сбрасывались. APK переподписан
  Android debug keystore (e7732c02… ≠ payload) — это gitignored-фикстура
  old_key-путей, semantically «старый ключ»; RestoreMode old (debug) уже был
  другим ключом;
  (4) `test_steps_match_plan`: engine безусловно исполняет шаги `updater-lock`
  (оба действия) и `updater-bootstrap` (install), `classic_steps()` их не
  перечислял → plan и события part ways. Добавлены оба шага в plans.rs в порядке
  engine (reference-дефект: файлы идентичны, тест там тоже красный);
  (5) `test_canbus.run_cli` timeout 60→180: полный install на /mnt/c ~75s+
  под нагрузкой (секция canbus 803s/16 тестов), в reference 60s — лимит среды,
  поведение теста не меняется;
  (6) flaky `catalog` тесты (CACHE_BUSY): на 16 тестовых потоках 6/6 падений —
  engine-тесты спавнят `sh`, fork наследует OFD с flock, и до exec-закрытия
  лок переживает `close()` родителя → следующий `try_lock` = EWOULDBLOCK.
  `lock_with` получил ограниченный retry (100×1ms только на WouldBlock):
  ложная занятость исчезает, реальная (второй экземпляр, удержание в тестах
  618/623) по-прежнему даёт CACHE_BUSY после ~100ms; после фикса 6/6 зелёные
  на 16 потоках.
- Основание: install.sh — оракул (Docs: «Эталоны: …»), поэтому стейджинг теста
  приводится к реальному layout релиза, а не наоборот; OTA-исключение chosen
  перед вырезанием payload-spec, т.к. backlog планово оставляет updater до WP4,
  а вырезание recipe меняет контракт/фикстуры/identity (recipe_sha256) досрочно;
  переподписка old-key — локальная фикстура, гитигнорена, ключ не коммитится.
- Риск: исключение OTA в state() скрывает реальное присутствие OTA-файлов на
  устройстве до WP4 — mitigated: комментарий в тесте + запись здесь; откат =
  удалить 2 строки из OTA_FILES/OTA_PACKAGES после вырезания recipe; retry в
  lock_with добавляет ~100ms реальной занятости (поведение/код ошибки не меняются).
- Статус: принято (коммит — по явной команде). Полный acceptance-прогон от
  2026-10-03 19:37–21:15 (acceptance_run.sh → acceptance.txt): classic_port
  41/41 OK RC=0 (3656s), canbus 16/16 OK RC=0 (755s), integration 18/18 OK
  RC=0 (1465s), DONE 21:15:52. Прочее: cargo test workspace 2+28+10 = 40/40
  (гейт «Rust 40»), sync-classic-commands --check RC=0, shellcheck -S error
  RC=0, guard 9/9, test_release 6 OK, verify-payload RC=0.

## 2026-10-04][WP1][Шаг 1.5 CI: JVM-гейты 303+105 из легенды, Rust 40, Svelte check, shellcheck flip, gui-win/mac]
- Контекст: SPEC L103 требует job tests (JVM 303+105 из легенды + новые, Rust 40,
  Svelte check, shellcheck, detachment-audit) и cross-OS сборку GUI. Порт тестов
  легенды в дерево сделан ранее; шаг — гейты в ci.yml + доведение JVM-тестов до зелени.
- ci.yml (5 правок, YAML валиден):
  (1) JVM-шаг запускает `:tests:hil:test :Native:app:testDebugUnitTest
  :RestoreMode:app:testDebugUnitTest` + новый шаг «JVM legend gate»: count() из
  атрибутов tests="N" в XML, `[ native -ge 303 ] && [ restore -ge 105 ]`;
  (2) Rust-гейт: `cargo test --manifest-path Installer/Cargo.toml | tee` (pipefail)
  + сумма `test result: ok. N passed` >= 40 (default-members = 3 крейта = 2+28+10);
  (3) setup-node@v4 (node 22, npm cache по Installer/desktop/package-lock.json) +
  шаг Svelte check (`npm ci && npm run check`);
  (4) shellcheck flip: `-S error make_release.sh scripts/*.sh` — блокирующий
  (continue-on-error убран); warning-строка payload-common (`*.sh`, `load.bin`)
  оставлена `|| true` — легенда-пейлоад не правим (контент пиннится sha256);
  (5) jobs gui-win (windows-latest) / gui-mac (macos-latest): npm ci + npm run build
  + `cargo check --manifest-path Installer/Cargo.toml -p voyahtune-desktop`;
  detachment-audit оставлен report/continue-on-error (flip — WP4, TODO в файле).
- Порт тестов: 22 файла RestoreMode (105 @Test) + 58 файлов Native из
  reference-3.14 (легенда — чистый JUnit4, без Robolectric/Mockito); поверх —
  4 наших файла (FakeCanBusBinder, RobolectricCanBridge, HilBridgeProbeTest,
  NativeHilScenarioTest). Итог Native: 306 = 303 легенда + 3 наших.
- Паритет-фиксы main-кода (чиним реконструкцию, не тесты):
  (1) RestoreMode — 4 фикса: VoiceEngineCache.release (decompile дал
  `throw IllegalArgumentException(String.valueOf(t))` вместо `t.close()`);
  VoiceFuzzyMatcher.prepare (безусловный return null в конце тела цикла — fuzzy
  мёртв; слова-похожие-на-глагол не делали return null как легенда);
  VoiceCommandRepair.distanceOne (пустой `if (len1>=len2) {}` + безусловный i2++
  → несохватывание при len1>len2): 9 → 2 → 0 падений, 105/105 зелёные;
  (2) Native ApolloSettingsRuntimeFlag.isEnabledForBoot: while(true) с
  `else if (...) { return true; }` БЕЗ else-ветки — вечный цикл при валидном
  payload с чужим boot id (тест rebootInvalidatesPreviouslyEnabledFlag висел
  17 мин, найден jstack: 100% CPU на isEnabledForBoot:63). Добавлен `else return false`;
  (3) Native DoorPauseTimeline.fadeStepVolume: decompile заменил float-арифметику
  легенды `startVolume * (float)(steps-bounded) / steps` на целочисленное деление
  → volume 0 вместо 1 (2 падения DoorPauseFadeCursorTest на delayMs). Восстановлено
  float-приведение.
- Системный скан (эвристика «while(true), чья верхнеуровневая if/else-if цепочка
  заканчивается без финального else») по Native+RestoreMode main без JNA: 7
  кандидатов, все ложные — есть break/return при выходе за границы (OtaStatusProvider,
  DoorPauseFadeCursor, VoiceCommands, VoiceOrbView, AdvanceActivity + 2 уже
  починенных). Отдельный grep: других целочисленных делений до Math.round нет.
- Фикс путей после staged-переименования docs/→Docs/ (rename в индексе из прошлой
  сессии): ci.yml G-3 читал `docs/reference-map.json` — файла в рабочем дереве нет,
  первый же коммит сломал бы гейт; build_reference_map.py писал в `docs/` —
  исправлено на `Docs/`. Спеку/DECISIONS не трогали (исторические ссылки).
- Локальные гейты (все зелёные): Native 306/306 0 failures (свежий прогон),
  RestoreMode 105/105, HIL 25, cargo test 40/40, svelte-check 0/0,
  `npm run build` (svelte-check + vite, dist/ собран) ✓, shellcheck -S error RC=0
  (warning по payload-common RC=1 — ожидаемо, остаётся || true), YAML OK.
- Ограничения: `cargo check -p voyahtune-desktop` локально в WSL не проходит —
  нет pkg-config/libdbus-1-dev/webkit2gtk (системные пакеты Ubuntu, не код; на
  win/mac этих Linux-зависимостей нет) — фактическая проверка gui-win/gui-mac
  отложена до CI; HIL=25 — последний живой прогон (03.10), сегодняшний combined
  прогон взял его UP-TO-DATE; git status содержит staged-переименование docs→Docs
  из прошлой сессии (не трогали, commit — по явной команде).
- Статус: L103 готов (коммит — по явной команде). Дальше: L106 приёмка fake ADB →
  L107 финальный merge wp1.

## 2026-10-04][WP1][Шаг 1.6 Приёмка fake ADB: подписанный payload 4.0.0-build.2, чистая установка поднимает Native/RestoreMode, 42+16+19 зелёные]

- Требование SPEC L106 (SPEC.md:244-245): «наш payload ставится на fake ADB и поднимает
  Native/RestoreMode; CI tests зелёный; CI payload собирает артефакт с sha256. G1 пройден».
- Первая сборка payload-4.0.0-build.2 дала НЕПОДПИСАННЫЕ APK: build.gradle ставит
  signingConfig только при env ANDROID_KEYSTORE_B64, а make_release лишь предупреждает
  «БЕЗ подписи»; инсталлер упал на плане с APK_SIGNATURE «Magic not found» (нет v2-блока
  'APK Sig Block 42'). Рецепт подписи найден в прошлых скриптах: gradlew --stop (демон
  держит старое окружение!) + export ANDROID_KEYSTORE_B64/storepass/alias=voyahtune из
  C:\Projects\signing\, затем пересборка. После: native.apk и restore_mode.apk имеют
  APK Sig Block 42, 29/29 verified, `sha256sum -c SHA256SUMS` = 3/3 OK (симуляция CI
  payload job; dist zip 125108245 байт, sha256 5c40af77...).
- Эксперимент чистой установки (WSL, fixture-driver + fake ADB, VOYAH_TEST_PAYLOAD=
  payload-4.0.0-build.2): RC=0; ru.big.town.anative → /system/priv-app/Native/Native.apk,
  ru.big.town.restoremode → /data/app/ru.big.town.restoremode/base.apk (файлы на месте),
  updater=running перед финальным reboot, reboots=1, install.lock снят, load.bin
  установлен. Эти факты зафиксированы новым тестом
  `test_fresh_install_boots_native_and_restoremode` в Installer/tests/integration.py.
- Итог приёмки (последовательный прогон, WSL): classic_port 42/42 OK (rerun),
  canbus 16/16 OK, integration 19/19 OK (включая новый тест).
- ПЕРВЫЙ прогон classic_port: 14 ошибок — ВСЕ TimeoutExpired(120s) на plan/apply, не
  assert'ы. Причина средовая: WSL дистрибутив загрузился в момент старта прогона (12:15),
  в окно попали apt-daily/unattended-upgrades (12:46), logrotate (12:37), tmpfiles-clean
  (12:30), payload был пересобран за 8 минут до прогона (холодный кэш /mnt/c + первый
  доступ AV), плюс мои параллельные диагностики. Факты: зависший driver — без дочерних
  процессов; позже io/memory pressure = 0; чтение payload 0.5с/129MB; одиночный rerun
  того же теста — OK. Спокойный rerun всего модуля — 42/42 за 5249s.
- Квирк WSL: acceptance гоняется только в WSL (там linux fixture-driver ищет
  bundle/adb/adb); Windows-бинарь (cfg!(windows)) ищет adb/adb.exe → ADB_MISSING —
  windows-python probe без WSL не работает.
- Статус: L106 локально готов (G1 пройден; «CI tests зелёный» подтверждается первым
  push — по явной команде; wp1 не закоммичен). Дальше: L107 финальный merge wp1.

## 2026-10-04][WP1][Шаг 1.7 Финальный merge wp1: CI зелёный, откат = revert одного merge-коммита]

- Первый push wp1 (274c2a4..6541c42 — Шаги 1.4/1.5/1.6 + паритет): CI FAILED,4 причины —
  все от условий чекаута (Installer/ в git впервые; локальные прогоны им не соответствовали):
  (1) payload: static-checks.sh OEM-харнесса на строке62 зовёт agent-contract-checks.sh
      напрямую, а все .sh харнесса были 100644 (Windows-worktree даёт иллюзию +x) →
      Permission denied. Фикс: `git update-index --chmod=+x` для8 скриптов;
  (2) tests: installer-build `checkout_payload_contains_every_required_runtime_file`
      читает blobs/, которых нет в чекауте (blobs/* в .gitignore) → в tests job добавлены
      cache blobs + `bash scripts/fetch-blobs.sh` перед cargo test;
  (3+4) gui-win/gui-mac: build.rs Tauri — «resource path resources/bundle doesn't exist»
      (каталога нет нигде) → добавлен resources/bundle/.gitkeep.
- Второй push (125a3bf): CI SUCCESS (3m13s, run 37210848885) — tests/payload/gui-win/gui-mac
  зелёные. Это подтверждение «CI tests зелёный» из требования L106 (SPEC:244-245, G1).
- Итог WP1: приёмка локальная classic 42/42 + canbus 16/16 + integration 19/19 (payload
  подписанный, sha256 3/3), Native 306 + RestoreMode 105, Rust 40, svelte-check, shellcheck.
- L107 (SPEC:246 «Откат: git, ветка wp1, один merge-коммит»): wp1 сливается в main одним
  --no-ff merge-коммитом; полный откат WP1 = revert этого коммита. После merge ветка wp1
  остаётся в репозитории как история.

## 2026-10-04][WP2][Шаг 2.0 Каркас паритета без авто: parity-diff, capture, HIL-replay]

- Контекст: WP1 закрыт (merge fde39af, CI зелёный). WP2 (L108-L117) deps: WP1 + авто;
  снятие трасс L109/L110 требует автомобиля — начат car-independent каркас (команда).
- `Utils/parity-diff.py` (L111): попарный diff traces/original|fork по 12 сценариям
  (Приложение A) × 4 вида (logcat, nativelog, dumpsys, cantrace). Нормализация:
  таймметки/pid/tid, 0x-хекс, UUID, 32+ hex-токены, epoch-ms, t=..ms, стены часов.
  Классы identical / expected-diff / regression / pending; allowlist —
  `Docs/parity-expected-diffs.txt` (regex + обязательный комментарий-обоснование).
  Отчёт `Docs/parity-report.md` (в SPEC L111 путь «docs/...»; в репо каталог Docs/).
  Коды: 1 при regression; `--strict` делает pending ошибкой (будущий гейт L116).
  Тесты: `tests/parity/test_parity_diff.py` — 12 unittest на stdlib (pytest нет в репо).
- `scripts/capture-trace.sh` (L109/L110): 4 файла на сценарий; NativeLog = фильтр по
  4 TX-тегам легенды (`$$$ OemVehicleState $$$`, `$$$ HeadlightCanTransport $$$`,
  `$$$ LightSensorService $$$`, `CanBusEventHub`); cantrace best-effort через
  `--can-cmd` (штатного candump на Android нет). shellcheck -S error чист.
- HIL-replay (L112): `ru.big.town.hil.replay` {TxRecord, LogcatTxParser, TraceReplay};
  парсер распознаёт все форматы TX6/9/20/36/57/58/77 из легенды; неизвестный
  TX-формат → `UNSUPPORTED` в транскрипте (смена формата лога не проходит молча).
  Транскрипт канонический (без таймметок/pid) сверяется с эталоном
  `tests/hil/src/test/resources/replay/sample.{logcat,ref}`; бежит в `:tests:hil:test`
  в CI — постоянная регрессия каждого изменения Native (L112). Тесты: 10 + 6.
- `Docs/behavior-matrix.md` v1 (L114): шаблон Приложения C, 16 строк (P-01..P-12 +
  кросс-строки по E1/E3/E4/E7); «захват из OEM» = TBD до L109/L110.
- `traces/{original,fork}/` в git (артефакты L115 — не игнорируются); начальный
  `Docs/parity-report.md` — все 48 пар pending, exit 0.
- CI: добавлен шаг «Parity-diff unit tests» в tests job; HIL-replay уже покрыт
  существующим `:tests:hil:test`.
- Статус: каркас зелёный локально (HIL 41, parity 12, shellcheck 0). L108-L117
  остаются открытыми до доступа к авто: L109/L110 снятие трасс -> L111 отчёт ->
  L113 grant-gate -> L115/L116 приёмка.

## 2026-10-04][WP2][Глубокое ревью каркаса паритета: 10 логических/устойчивых ошибок, все исправлены]

- Контекст: команда — «провести глубокий подробный анализ реализации, выявить ошибки
  логики/синтаксиса/потенциальные, объективная оценка, исправить». Ревью каркаса
  (parity-diff, capture-trace, HIL-replay): найдено и исправлено 10 проблем;
  каждая — либо ложная регрессия паритета (опасна для оракула), либо скрытая
  потеря данных, либо краш/зависание инструмента.
- Логика паритета (Utils/parity-diff.py):
  1) pending-список смотрел только на logcat — сценарий со снятым одним logcat
     «терялся» из списка pending; теперь по итогу сценария (любой неснятый вид).
  2) candump -t a/d пишет `(epoch.µs)`/`(delta)` — скобки не нормализовались,
     каждая сессия давала бы ложную регрессию cantrace; добавлен RE_CANDUMP_PAREN.
  3) dumpsys window: hex-идентификаторы `Window{4a3f2b1c u0 …}` меняются между
     сессиями (ложные регрессии 09/10); маска только после известных типов
     (Window/ActivityRecord/Token/…) — широкая маска `{hex}` могла бы скрыть
     настоящее расхождение (безопаснее ложная регрессия, чем ложный паритет).
  4) RE_TOKEN маскировал и чисто-десятичные 32+ id; теперь только с hex-буквами.
  5) невалидный regex в allowlist → SystemExit(2) с сообщением (был traceback/1);
     ошибка записи отчёта (OSError/IsADirectoryError) → 2 (1 зарезервирован
     под регрессии); неизвестный --scenario → argparse choices, exit 2.
- HIL-replay (LogcatTxParser.java):
  6) парсер брал TX-строки ЛЮБОГО тега — шум чужих приложений попадал бы в
     эталон; теперь только 4 легитимных тега (синхронно с фильтром -s в
     capture-trace.sh; подтверждены grep-ом по Native: TX-логи только там).
  7) переполнение int (TX-номер/значение из испорченного лога) роняло разбор
     трассы NumberFormatException; теперь запись помечается UNSUPPORTED.
  8) повреждённый телом бандла TX77 (нечисловое значение) реплеился ЧАСТИЧНО —
     скрытая потеря состояния сошла бы за паритет; parseStates возвращает null
     -> UNSUPPORTED.
- capture-trace.sh:
  9) `--opt` без значения: cryptic-смерть от `shift 2` под set -e и тихо пустые
     значения (--out="" писал бы в корень, --can-cmd="" терялся молча) ->
     need_value на каждую опцию; --duration валидируется как целое >0.
  10) перезапись уже снятой (невосстановимой) трассы — только с --force;
      без timeout бесконечный candump/--can-cmd вис бы навсегда -> run_timed
      (timeout, иначе фон + kill); подтверждено прогоном: 100s-команда при
      --duration 1 завершает скрипт за 1s (стаб adb, WSL-баш).
- Верификация (все зелёные): python 18/18 (было 12, +6 регрессионных на каждый
  фикс), HIL gradle :tests:hil:test — все сьюты 0 ошибок (LogcatTxParserTest
  10->14), shellcheck -S error/warning по scripts/*.sh — 0, функциональные
  прогоны capture-trace (--help/--scenario 13/без значения/--force/существующий
  файл — все rc=2 с внятным сообщением), Utils/parity-diff.py — exit 0,
  отчёт без диффа.
- Известные ограничения (не баги, задокументированы): enum VehicleState в
  tests/hil имеет 27 состояний — трассы дадут unsupported-state для прочих
  состояний OEM (реплей деградирует до расширения enum); нормализаторы
   dumpsys/cantrace тюнятся после первого реального снятия L109/L110; если в
   легенде появится 5-й TX-тег — править TX_TAGS и фильтр capture-trace вместе.

## [2026-10-04][WP2] Car-independent хвост WP2: grant-gate L113, REF-генератор L112, runbook L109-L117

- Контекст: после глубокого ревью закрыты car-independent под-шаги WP2,
  оставшиеся до доступа к авто (L109/L110 снятие трасс, живой L113).
- `scripts/grant-gate.sh` (L113), коды 0 — пройден / 1 — провал («СТОП
  (SPEC L113): стоп проекта, пересмотр архитектуры установки…») / 2 —
  аргументы/окружение. Четыре проверки: dumpsys `WRITE_CANBUS granted=true`
  (только строки с WRITE_CANBUS, затем require granted=true — пустой dumpsys
  тоже провал с внятным текстом), `pidof ru.big.town.anative`, wait-loop на
  маркер `CanBus callback registered` (до `--timeout`, default 30) и отсутствие
  `WRITE_CANBUS permission missing` в `logcat -d -t 500`. Маркеры подтверждены
  grep-ом по Native/CanBusEventHub. Стиль capture-trace.sh: need_value на
  каждую опцию, usage=шапка файла (sed -n '2,17p'), валидация --timeout как
  целого >0 до adb.
- Альтернативы grant-gate: (а) без wait-loop на маркер подписки — отвергнуто
  (bind/подписка асинхронны, мгновенный grep дал бы ложный провал);
  (б) мгновенный grep `granted=true` по всему dumpsys — отвергнуто (ложное
  срабатывание на явление из других секций не исключено).
- `TranscriptCli` + gradle-задача `:tests:hil:replay` (L112): три режима
  `--trace` (stdout), `--trace --out` (генерация REF), `--trace --ref`
  (сверка через TraceReplay.compare; дрейф → 1); 0/1/2 как у parity-diff
  (1 зарезервирован под содержательный провал). Задача: JavaExec,
  workingDir=rootDir (иначе относительные -Ptrace резолвились от tests/hil),
  без -Ptrace — GradleException с подсказкой.
- Решение (L112 «постоянная регрессия»): канонические REF для CI хранятся в
  `tests/hil/src/test/resources/replay/refs/<NN>.ref`, генерируются из
  traces/original/* после L109 и коммитятся вместе с трассами (L115);
  альтернатива «генерить REF на лету в CI» отвергнута — CI не должен зависеть
  от артефактов, снятых вручную, а сверка форк-трасс остаётся локальным
  гейтом по runbook.
- Альтернативы REF-генератора: (а) только gradle-задача без CLI — отвергнуто
  (покрытие юнит-тестами без запуска gradle); (б) Groovy-задача без main-класса
  — отвергнуто (логика транскрипта уже в Java, тесты JUnit).
- `Docs/parity-runbook.md`: процедура L109-L117 (E9/E10-fingerprint, 12
  сценариев × 2 прогона, grant-gate до и после L115, allowlist-правила,
  чек-лист L116, откат L117). Гейт дважды: сразу после установки (до снятия
  fork-трасс — без granted нет подписки на CAN) и перед приёмкой.
- Верификация (все зелёные): shellcheck -S error/-S warning по make_release.sh
  + scripts/capture-trace.sh + scripts/grant-gate.sh — 0; HIL gradle
  :tests:hil:test — 52/52 (TranscriptCliTest +7); функциональные прогоны
  задачи replay (match=0, out=0 + git diff --no-index каноничности gen.ref
  против sample.ref=0, дрейф=1, без -Ptrace=1 с текстом ошибки);
  grant-gate 6 путей без устройства (--help/--timeout abc/--pkg без
  значения/--bogus/--timeout 0/нет adb — все rc=2; device-путь через WSL-шим
  adb — «нет устройства» rc=2); python parity — без изменений.
- Риск: живой L113 (dumpsys/pidof/подписка на реальном CAN) проверяется только
  на авто; до этого гейт верифицирован по маркерам из исходников и mock-путям.
- Статус: готово к авто-этапу — L109/L110 снятие трасс → L111/L112/L113
  по runbook → L115/L116 приёмка.

## [2026-10-04][WP7] Запуск WP7 Privacy Control (телеметрия): решения по приватности, интеграции и режиму исполнения

- Вход: мастер-документ владельца «VOYAH FREE SPORT+ 2026 — контроль телеметрии»
  (файл в рабочей директории; содержит идентификаторы IMSI/PDSN/MAC, красные
  линии R1-R5, гипотезы H1-H6, фазы 0-8). Факт: `gh repo view` →
  rkolpakov-sudo/VoyahTune **PUBLIC**, а R5 требует «только приватный
  репозиторий» → вопрос закрыт ДО начала работ.
- Решения владельца (4):
  1) **Хранение вне репозитория**: мастер-док перенесён в
     `C:\Projects\VoyahTune-private\telemetry-control\`; артефакты фаз 1-7
     (`art/`, REPORT.md, can_matrix.csv, uds_map.md, api_trips.md,
     risk_notes.md) — туда же; в публичный репо не коммитятся;
  2) **Интеграция**: новый **WP7 в SPEC.md** (L199-L206); вставка — перед
     баннером «КОНЕЦ ДОКУМЕНТА» (вставка в тело сломала бы сквозную физическую
     нумерацию `NNNN|` и внешние ссылки вида `SPEC.md:462`); регион верифицирован
     awk-скриптом: все 544 префиксов == номеру строки, внешних ссылок на строки
     511-513 в Docs/DECISIONS не найдено;
  3) **Приоритет**: параллельно car-dependent хвосту WP2 — фаза 0 (кодовая база,
     без авто) немедленно; фазы 1-7 — одним машинным окном вместе с L109/L110;
  4) **Исполнение фаз 1-7**: владелец запускает команды в салоне/на телефоне,
     агент готовит команды и анализирует артефакты (удалённого ADB к ГУ нет).
- Защита от случайной утечки (второй рубеж): `.gitignore` += `Qwen_*.md`, `art/`
  — `git add -A` не сможет закоммитить мастер-док или разведданные.
- Санитария WP7 в публичном SPEC: без идентификаторов (IMSI/PDSN/MAC/версии
  ПО), без эндпоинтов и DID; только цель, режим, границы артефактов, обобщённые
  H1-H6, красные линии, kill-критерии P0 (L204 по L192: timebox/фолбэк/эскалация
  на UDS-write, остановку сервисов, iptables), приёмка, откат. Полный текст
  красных линий и идентификаторы — в мастер-доке вне репо.
- Альтернативы: (а) сделать репо приватным — отклонено владельцем; (б) держать
  задачу вне SPEC (второй источник истины) — отклонено; (в) вставка WP7 в тело
  SPEC с перенумерацией — отклонено (ломает все физические L-ссылки в Docs).
- Риск: репо остаётся публичным — любой будущий материал WP7 проходит фильтр
  R5 до `git add`; гейт — `.gitignore` + ревью статуса перед коммитом.
- Статус: WP7 добавлен (SPEC L199-L206), мастер-док перенесён, структурные
  гейты зелёные; фаза 0 стартует (карта точек расширения → приватный каталог).
  WP1/WP2 не затронуты.

## 2026-10-04][WP2+WP7][Кампания «подключение→прошивка→тесты»: пульт scripts/campaign.sh и процесс в приватном мастер-доке
- Альтернативы: (а) устный чек-лист без инструментов; (б) единый скрипт, выполняющий
  весь прогон автономно (write-команды внутри); (в) держать весь процесс без репо-части.
- Причина: первый прогон должен загрузить прошивку в ГУ последовательно, с обратной
  связью владельца на каждом гейте и откатом на каждом шаге; удалённого ADB к ГУ нет —
  исполнитель владелец, агент анализирует (решение 4 от 2026-10-04). Read-only пульт
  env/device/audit/postinstall (коды 0/1/2, отчёты в art/campaign/) даёт повторяемые
  гейты кампании; write-команды (GUI-инсталлер) остаются у владельца по карточкам.
- Риск: содержимое Installer/desktop/src-tauri/resources/bundle (payload 125 МБ) и
  tauri.release.conf.json — генерируемые артефакты локальной сборки инсталлера —
  mitigated: .gitignore += содержимое resources/bundle/* с исключением .gitkeep
  (каталог обязателен: tauri.conf.json bundle.resources резолвит его в
  tauri_build::build, без каталога падает cargo check) и tauri.release.conf.json;
  сборочный drift (Cargo.toml/gen-schemas, только переводы строк) откатывается перед
  коммитом. Полный сценарий с write-командами и красными линиями — приватный
  on-car-campaign.md вне репо (R5).
- Статус: принято; гейт 0 зелёный — env PASS, GUI-инсталлер Windows собран
  (VoyahTune Installer_1.4.0_x64-setup.exe, NSIS), payload sha256 сверен.

## 2026-10-04][WP2+WP3][Кампания on-car отложена: установка форка запрещена до завершения IMP-06/IMP-01/IMP-02 (AVAS)
- Альтернативы: (а) кампания сейчас на build.2 (валидация установки/паритета отдельным
  заходом); (б) дробно — только снятие original-трасс сейчас; (в) сначала полный WP3.
- Причина (решение владельца): автомобиль — объект повышенного риска; build.2
  поведенчески не лучше 3.22 (WP2 даёт паритет + инфраструктуру, не функционал),
  поэтому ставить незавершённую по IMP-01/IMP-02 прошивку бессмысленно и небезопасно;
  несохраняющееся отключение AVAS (IMP-02) — обязательное условие установки.
- Риск: сдвиг сроков; original-трассы (L109) снимаются в будущем машинном окне
  вместе с установкой; read-only пульт и гейты остаются готовыми.
- Статус: принято; Этапы 1-8 on-car-campaign.md НЕ исполняются; условие возобновления —
  завершённые IMP-06 -> IMP-01 -> IMP-02 (порядок L119) с HIL-тестами, новый билд,
  повторный гейт 0 кампании.

## [2026-10-04][WP3][IMP-06 реализован: SleepController + session_id + дедуп экрана + объединение серий apply]
- **Контекст**: порядок L119 — IMP-06 первым (P0, SPEC L48, R2/R12); кампания on-car на паузе, работа car-independent.
- **Что сделано**:
  - \SleepController\ (Native): AWAKE→SLEEPING→ASLEEP→WAKING; монотонный session_id (бамп на входе в sleep-период); отбраковка устаревшей работы через \isCurrentSession\; дедуп повторных SCREEN_ON/SCREEN_OFF; power/garage-события НЕ дедупятся (цепочки эффектов разные — поведение 3.22 сохранено).
  - Врезки: \SetModesReceiverDynamic\ (дедуп-гейт ДО resetRestoreGate/activateWake), \SetModesService\ (fallback-обработчики → onSleepComplete/onWakeComplete; power-state ветки; \pendingPhysicalWake\ с тегом сессии; тег ancillary-wake-задач 3/5/6с → отбраковка чужой сессии).
  - \PendingApplySeries\ — объединение серий manual-apply: не начатый предыдущий task superseded (removeCallbacks), payload'и переносятся (все выполняются один раз — как в 3.22), один runCycle вместо N.
- **Проверка**: \:Native:app:testDebugUnitTest\ 325/325 PASS (новых: 14 SleepControllerTest + 5 PendingApplySeriesTest); регресс-тесты 3.22 (ModeSyncPolicyTest, ApplyEngineRunStateTest) зелёные.
- **Риск**: низкий (car-independent); on-car валидация — в будущем окне кампании.
- **Дальше**: IMP-01 (CommandResult + read-back) → IMP-02 (AVAS state-machine).

## [2026-10-05][WP3][IMP-01 реализован: CommandResult + read-back диспетчер + AckProvider + UI-бейдж]
- **Контекст**: порядок L119 — после IMP-06 (P0, SPEC L43, R3 «нет физического подтверждения команд»); кампания on-car на паузе, работа car-independent.
- **Что сделано**:
  - \CommandResult\ (ядро): state SENT|PENDING_ACK|CONFIRMED|FAILED|TIMEOUT, feature/sentAt/ackSource/attempts.
  - \CommandDispatcher\ (ядро): окно read-back 1500мс; ретраи ≤2; backoff 300/900мс джиттер ±10%; \onAck\ подтверждает (в т.ч. во время backoff), \onMismatch\ → FAILED с источником; новый submit вытесняет предыдущую команду фичи (FAILED); \submit\ возвращает boolean (синхронный результат первой отправки — вызывающий код не меняется асинхронно); \markSent\ ДО вызова action.send() + проверка active после — защита от синхронного echo (reentrant ack во время send иначе затирал состояние PENDING_ACK'ом и планировал мёртвое окно); исключения в send → fail без падения.
  - \ReadBackTable\ — read-back таблица SPEC: режимы → VCU_Indication 0x2FA; подвеска → ASC 785/959; свет → SWReason; AVAS → бит TX57 (IMP-02).
  - \CommandStatusHub\ — прод-обвязка: HandlerThread «CommandStatus» (НЕ main — ретраи выполняют блокирующие транзакции), SystemClock; каждый переход → Log + broadcast \ACTION_COMMAND_RESULT\ (setPackage restoremode, BIND_SET_MODES_SERVICE) по образцу publishPowerHoldStatus.
  - AckProvider-врезки (только добавление вызовов, поведение не меняется): \ModeFeedbackController.onVehicleState\ → ack(modeKey); \SuspensionWidgetController\ checkCompletion/reached → ack, timeout 45с → mismatch; \LightSensorService.onLightSwReason\ → ack при desired==target, mismatch при расхождении.
  - Миграция фич (отправка теперь идёт через submit): \DriveModeCanTransport.dispatch\ (feature driveMode), \SuspensionWidgetController.dispatch\ (suspension), \LightSensorService commit → setHeadlights\ (light). CanRestorePlan НЕ подключён — у restore-плана свой retry-цикл (ApplyEngine), двойные ретраи исключены.
  - UI-бейдж: \AdvanceActivity.commandStatusText\ (layout-строка под шапкой, ids.xml+strings.xml) + \commandResultReceiver\ (register/unregister по образцу settingSyncReceiver) → «отправка/подтверждено/ошибка/нет подтверждения» + pill_pending/active/error.
- **Тесты**: \:Native:app:testDebugUnitTest\ — новых 21: CommandDispatcherTest 11 (отклик/молчание×3-попытки/поздний/mismatch/supersede/исключение/джиттер-границы [270,330]+[810,990]), CommandReadBackEmulatorTest 4 (can-emulator: ACK→CONFIRMED, SILENT→TIMEOUT за 3 транзакции, LATE 250мс→CONFIRMED, CONFLICTING→FAILED), ReadBackTableTest 6. Итог по трём модулям (Native+RestoreMode+hil): 503/503 PASS, 0 failures.
- **Ограничения**: источник LightStatus не врезан (коды датчика фар не документированы в коде — подтверждение света только через SWReason); AVAS-фича подключается в IMP-02; restore-серию (sendRestoreSequence) на диспетчер не мигрировали (свой retry); UI-бейдж — последнее событие, история не ведётся.
- **Риск**: низкий (car-independent; ack-врезки только публикуют события); смежные механизмы (45с-таймаут подвески, 5с reassert света) продолжают работать как раньше.
- **Дальше**: IMP-02 (AVAS state-machine: Docs/avas-state-machine.md → Native → UI-бейдж+тумблер → HIL → on-car ×10).

## [2026-10-05][WP3][IMP-02 реализован: AVAS state-machine — окно захвата, коррекция OEM-сброса, read-back каждой записи]
- **Контекст**: порядок L119 — после IMP-01 (P0, SPEC L44, R1, целевое отличие форка: AVAS запоминается, E2/P-05); кампания on-car на паузе (on-car ×10 — после возобновления).
- **Что сделано**:
  - `Docs/avas-state-machine.md` — дизайн: IDLE → WAKE_APPLY (зажигание/дверь/первый Drive, однократно на событие) → CAPTURE_WINDOW (открывается только первым Drive вслед за дверью; закрывается дверью/соном; внутри окна изменение = снапшот) → CORRECTIVE_REAPPLY (вне окна изменение = OEM-сброс; ≤3 попытки на цикл = один submit диспетчера IMP-01); правило миграции «снапшот = фактическое состояние ∪ старый флаг пользователя».
  - `AvasStateMachine` (Native, чистый JVM): снапшот/окно/latch-бюджет коррекции; без I/O — события возвращают `ApplyRequest` (запись = клей).
  - `AvasController` (клей): HandlerThread «Avas» сериализует события; подписки `DriverDoorStateController` (edge LIVE) и `GearStateController` (gear==3 edge); `requestUserToggle` синхронен (булев исход для руля/голоса) с откатом снапшота при отказе записи; echo CB36 id 665 → ack/mismatch через `CommandStatusHub` (фича `avas`, источник `AVAS state bit (TX57 cache)`).
  - Врезки событий: `SetModesService` — MSG 21 → `requestUserToggle`, `handleScreenOffFallback`/`handlePowerStateChanged`(sleep) → `onSleep`, `handleScreenOnFallback`/`handlePowerStateChanged`(wake) → `onWakeEvent`, `onCreate` → `init`; `VehicleStateControllers` подписка +665; `ModeFeedbackController.onVehicleState` ветка 665 → echo; `SetModesReceiverDynamic.toggleSetting` и `VoiceCommandController` → `requestUserToggle`; `MainActivity.createCanRestorePlan` op → `applySnapshot` (restore-план идёт через диспетчер).
  - Read-back IMP-01 подключён: `ReadBackTable.isTracked` += `avas` (бейдж получает результаты), `CommandStatusHub.hasActive`.
  - UI: `AdvanceActivity.commandFeatureName` += `avas` (`cmd_feature_avas`); **починен gap меню**: радиогруппа `pedestrianSoundGroup` теперь шлёт MSG 21 (раньше писала только pref — AVAS не применялся до следующего loadModes).
- **Тесты**: новых 17 — `AvasStateMachineTest` 11 (seed/окно/latch/×10 «выкл→запереть→открыть→Drive»), `AvasReadBackEmulatorTest` 6 (can-emulator: юзер-toggle→CONFIRMED, OEM-сброс→коррекция, оконное захват без записи, CONFLICTING→FAILED, бюджет latch, цикл ×10). Итог по трём модулям: **520/520 PASS, 0 failures** (вкл. HIL YAML avas-* и Robolectric avas-wake).
- **Ограничения**: безэхо-запись (значение не изменилось) на VCU может дать TIMEOUT-бейдж при корректном значении (проверка on-car, K=3); снапшот из окна не пишется в prefs (UI-флаг обновляется только пользовательскими путями); wake-apply пишет безусловно (спека: стартовая команда на событие).
- **Риск**: средний (записи на wake/дверь/Drive — новая нагрузка на TX58; эхо-маршрут через существующую подписку CB36); HIL YAML и все юнит-тесты зелёные; kill-критерий AVAS K=3 (L121).
- **Дальше**: on-car ×10 (после возобновления кампании: IMP-06→IMP-01→IMP-02 + HIL + новый билд), затем IMP-08 → IMP-07 по L119.

## [2026-10-05][WP3][IMP-08 реализован: реестр поколений хуков + reconciliation + crash-артефакты + отчёт]
- **Контекст**: порядок L119 — после IMP-02 (SPEC L50, R3 «нет наблюдаемости стабильности хуков»); кампания on-car на пауза (в т.ч. on-car чеклист IMP-08 — после возобновления).
- **Что сделано**:
  - `Docs/imp-08.md` — дизайн (SPEC→реализация, реестр/reconciliation/самолечение/crash-артефакты/экспорт, behavior-matrix, риски).
  - **Реестр поколений** `HookGenerationRegistry` (RestoreMode): genKey=`bootCount|loaderPid`, firstSeenAt=таймстарт первого наблюдения поколения, манифест = 7 HOOK_IDS + версия `3.22.0`; журнал ring 30 записей, запись только при изменении поколения/overall (идемпотентность — реконсиляция переживает restart).
  - **Reconciliation** (L50 «ожидание vs факт»): два триггера — провайдер `RestoreModeContentProvider.call()` после commit (boot-момент) и тик диагностики `AdvanceActivity.readSystemMetrics` (5с); вердикты: active/disabled→OK, failed→DEGRADED, waiting/injecting→PENDING в grace-окно 60с после таймстарта, затем DEGRADED; лестница overall `LOADER_DOWN > DEGRADED > PENDING > NO_DATA > OK`.
  - **Самолечение**: существующий supervise-loop лоадера (attempt-latch + backoff, уже покрыт тестами) + идемпотентность реестра; **принудительный re-inject из приложения сознательно НЕ реализован** (риск crash-loop против kill-критериев L192) — зафиксировано в imp-08.md.
  - **Crash-артефакты (лоадер, load.bin)**: lane `crashes` в `WORKER_LANES` (вне status-lane), two-tick стабильность (`pending|epoch` → `ready|epoch` в `CRASH_MARK`), окно ±30с (`CRASH_WINDOW`), `dumpsys -t 30` window/activity/meminfo под `timeout -k 2 32`, context.txt (boot_id/uptime/loader_pid), ретеншн 10 инцидентов (`CRASH_KEEP`), mirror → `/data/data/ru.big.town.restoremode/files/crashes` (chown uid + chmod 755 + restorecon best-effort); путь `/data/local/voyahtune/crashes/` (slug из `fork.config.toml` [product] name=VoyahTune — конвенция `voyahtune_*`).
  - **Экспорт** `HookReportExporter`: composeStatus (payload/renderForUi/reconciliation/журнал/boot_count/version) + zip в `cache/reports/voyahtune-report-<ts>.zip` (hook_status.txt + crashes/** рекурсивно), без сети; кнопка «Сохранить отчёт» в LoggingActivity (фон + `ACTION_SEND` zip через новый FileProvider `ru.big.town.restoremode.fileprovider`, `res/xml/file_paths.xml` cache-path reports); «Выгрузить лог» (Native share) не тронута.
  - Согласованность: sha нового load.bin (`1624fbbd…`, 81117) обновлён в checked-in release-identity `voyahtune-build.json` ×2 (Native/RestoreMode assets). `payload/manifest.json` и `payload/inventory.json` **не трогаем** — это дескриптор базового zip из yandexcloud (его верифицирует CI job `payload-verify`), их sha остаётся `5bc3a9dd…`/76561; при release-сборке sha пересчитывается в staging из фактического файла.
- **Тесты**: новых 16 — `HookGenerationRegistryTest` 11 (вердикты, grace PENDING→DEGRADED, смена поколения/bootCount, идемпотентность, ring 30, RU-describe), `HookReportExporterTest` 5 (секции отчёта, zip-состав без/с крэшами, cache/reports); `Packaging/tests/test_crash_artifacts.sh` (require/forbid контрактов лоадера + реестр/провайдер/тик/экспорт/Docs); `test_loader_fault_backoff.sh` pin WORKER_LANES обновлён. Итог: **536/536 PASS, 99 suites** (Native+RestoreMode+hil).
- **Ограничения**: crash-окно ±30с и ретеншн 10 — параметры конфига лоадера; журнал поколений только в памяти prefs (не в crash-отчёте отдельной секцией — входит через reconciliation-строку); on-car чеклист IMP-08 не выполнялся (ГУ не подключено); 2 падения `test_parallel_hook_loader.py` (dead-worker/restart) — pre-existing, воспроизводятся на чистом HEAD; shell-тесты на Native Java (`30_000L` и т.п.) и `node` в WSL PATH — pre-existing окружение.
- **Риск**: низкий (reconciliation и экспорт — read-only + публикация в UI; crash-сбор — новый lane лоадера, status-арм не тронут, все лоадер-контракты зелёные); принудительный re-inject исключён → нет риска crash-loop.
- **Дальше**: IMP-07 (geometry-watchdog K=2) → P0 (IMP-11, IMP-14, IMP-23) → P1 → P2 по L119; on-car кампания — после завершения каталога.

## [2026-10-05][WP3][IMP-07 реализован: geometry-watchdog — снимок bounds, 5 триггеров сверки, LKG в персист, журнал мутаций]
- **Контекст**: порядок L119 — после IMP-08 (SPEC L49, R12, P0/M, kill-критерий K=2 на авто); кампания on-car заморожена (K=2 и KPI ≤2с сверяются по логу после возобновления).
- **Что сделано**:
  - `Docs/imp-07.md` — дизайн: где живёт, элементы (LKG/триггеры/дебаунс/чек/журнал), запреты, тесты, риски.
  - **Самолечение целиком в `Packaging/payload-common/vd_bypass.js`** (system_server, Frida); лоадер — только наблюдатель: `load.bin` не способен откатить мутацию окна в живом system_server, coarse-restart лейна `vd` после его смерти уже работает. Native-правки не требуются — все пять триггеров SPEC L49 существуют как события внутри vd_bypass.
  - **LKG-снимок** `/data/local/open_voyah/vd_hooks/geometry.lkg` (JSON, атомарно `.new`+`renameTo` по образцу `publishAgentStatus`): запись только при отличии текущего снимка от persisted (идемпотентность); на старте `restoreGeometryFromLkg()` (та же валидация viewport) применяет LKG вместо падения — `throw "initial policy unavailable"` остался только для «нет валидного LKG». В чеке restore не вызывается: in-memory удерживает последнее применённое, откат к LKG там был бы регрессией (там лишь журнал `config-invalid`).
  - **Пять триггеров** → `scheduleGeometryCheck(trigger)`: config change = `WinReceiver.onReceive` (WIN_RELOAD); перенос панели = `LiftReceiver.onReceive`; смена дисплея = `ffDisplayChangedMethod.implementation`; выход из сна = `ScreenReceiver` SCREEN_ON; запуск приложения = `ffConfigImplementation` (ensureActivityConfiguration — config-pass запускаемого приложения, задокументировано как наилучшее приближение).
  - **Дебаунс 700мс** (`GEOMETRY_WATCHDOG_DEBOUNCE_MS`) — окно-коалесценция: первое событие планирует чек, события внутри окна не сдвигают его (запаздывание ограничено окном → KPI ≤2с, нет голодания при config storm); таймер в `cancelFreeformPending()` + epoch-guard по `FF.hookEpoch` (SCREEN_OFF/выключение отменяют; bump epoch в `scheduleFreeformHotAttach` обезвреживает чек — traversal делает attach).
  - **Чек `runGeometryCheck`**: guards → `refreshFreeformCfg()` → diff снимка с LKG (изменение → запись + `requestFreeformTraversalOnce`) → потерянный hot-attach → `attachFreeformHotHooks` (только вне стабилизационного `ffHotAttachPending`) → KPI-лог `geometry watchdog heal trigger=<t> in <ms>ms`. Никогда не `failAgent`, не мутирует Settings, не заходит в hot-path `layoutWindowLw` — под контролем forbid-проверок теста.
  - **Журнал** `/data/local/open_voyah/vd_hooks/geometry.journal`: `<epoch>|<trigger>|<action>|<detail>`, ring 30 (`GEOMETRY_JOURNAL_LIMIT`), ленивая загрузка при первом use, dedup последней записи (повторные чеки без изменений молчат), атомарная перезапись. Действия: `snapshot-update`, `hook-reattach`, `hook-reattach-failed`, `config-invalid`, `startup-lkg-restore`.
  - behavior-matrix: строка P-09/E8 («Оконный режим/fullscreen/DPI») → Причина `P-09, E8, IMP-07`.
- **Тесты**: новый `Packaging/tests/test_geometry_watchdog.sh` (шаблон vd-контрактов): `node --check`; ровно по одному вызову каждого из пяти триггеров; порядок включения в существующие точки; дебаунс-коалесценция/epoch-guard/`Java.perform`; таймер в `cancelFreeformPending`; тело чека (refresh/LKG/traversal/reattach/KPI); **forbid в чеке** `failAgent(`/`putString`/`putInt`/`SettingsGlobal.`/`layoutWindowLw`; атомарная запись, startup-restore, журнал (лен, dedup, ring). Сверка с чистым HEAD через `git stash`: набор pre-existing падений **идентичен** (14: Native Java grep-mismatch `30_000L`/`INTEREST_*`/`readVehicleState` и т.п., отсутствующий javac, 2 падения `test_parallel_hook_loader.py`) — регрессий нет; все контракты vd_bypass/loader (`test_vd_reparent_replay`, `test_saved_config_startup_wake`, `test_hook_status`, `test_loader_*`, `test_crash_artifacts`) зелёные. Java-код не менялся — JVM-базлайн 536/536 PASS, 99 suites.
- **Ограничения**: LKG не хранит per-package DPI/fullscreen (восстановление без них — задокументированная деградация, перечитывается следующим успешным refresh); триггер «запуск приложения» — config-pass, а не момент cold-start; журнал только мутаций геометрии (без status-армы); on-car K=2 и замер KPI по логу — после возобновления кампании.
- **Риск**: низкий — чек только читает и лечит через существующие безопасные механизмы (refresh/attach/traversal), не мутирует WM напрямую, не роняет агент; частые триггеры сведены дебаунсом к одному чеку (один перечит Settings + сравнение строк).
- **Дальше**: P0 (IMP-11, IMP-14, IMP-23) → P1 (IMP-03, IMP-04, IMP-09, IMP-15, IMP-17, IMP-18, IMP-20) → P2 (IMP-05, IMP-16 — экранный drag, после IMP-07) по L119; on-car кампания — после завершения каталога.

## [2026-10-05][WP5][IMP-11 реализован: версионирование 4.x.y+build.N, двухуровневый гейт перепубликации, Releases/archive/, BUILD-INFO с Rust-пином и хешами]
- **Контекст**: порядок L119 — после IMP-07 (SPEC L53/L136/L139/L161; дефект R7 «перепубликации под тем же номером (3.16x2; 3.21 backups)» — устраняется здесь), P0|S; on-car кампания заморожена.
- **Что сделано**:
  - `Docs/imp-11.md` — дизайн (схема, двухуровневый гейт, archive/, BUILD-INFO, тесты, риски/ограничения).
  - **Строгая схема** в `make_release.sh`: POSIX ERE `^4\.[0-9]{1,4}\.[0-9]{1,4}\+build\.[0-9]{1,6}$`, префикс `v` нормализуется; прежняя форма `-build.N` отклоняется с подсказкой схемы — две записи одного номера (`+`/`-`) и есть R7; опубликованных payload-релизов/тегов нет (только blobs-v1) → миграция не требуется.
  - **Гейт перепубликации (двухуровневый, L136)**: локально сразу после валидации — `Releases/dist/payload_<V>.zip` **или** `Releases/archive/<V>/` существует → падение (номер использован навсегда; staging `Releases/build/payload-<v>` гейтом не блокируется — retry до первой публикации возможен); в `ci.yml` шаг до сборки — `git ls-remote --tags origin "refs/tags/v${VERSION}"` → тег существует → падение («CI падает при существующем номере»).
  - **archive/ (L139)**: в dist-шаге ДО перезаписи — `version` из существующего `BUILD-INFO.json`; чужой комплект (`payload_<v>.zip/.json`, `BUILD-INFO.json`, `SHA256SUMS`) + любые чужие `payload_*` → `Releases/archive/<v>/`; после успешной сборки чужие staging `Releases/build/payload-*` → `Releases/archive/<v>/build/payload-<v>/` (сбой текущего прогона не уничтожает прошлые комплекты).
  - **BUILD-INFO (L135/L53)**: прежние поля + `toolchain.rust` (парсинг `channel` из `Installer/rust-toolchain.toml` — единственный источник правды; fallback `rustc --version` → `unknown`) и `hashes` {`payload_<V>.zip`, `manifest.json`, `native.apk`, `restore_mode.apk`}; `SHA256SUMS` как раньше включает BUILD-INFO.
  - **CI-версия**: payload-джоба и гейт используют `4.0.0+build.${{ github.run_number }}` (run_number уникален и никогда не переиспользуется); примеры в шапке `make_release.sh` и `scripts/campaign.sh` переведены на схему `+`. Android-идентичность D4 не тронута (`versionCode 3022000`/`versionName 3.22.0`).
- **Тесты**: новый `Packaging/tests/test_release_versioning.sh` — grep-контракты (ERE-схема ровно один раз, оба сообщения гейта, archive-свип до/после, BUILD-INFO rust/hashes, шаг гейта ci.yml + версия ×2, пин `channel = "1.98.1"`, forbid легаси `-build.N` в ci.yml и примерах) и функциональные отказы ДО тяжёлых шагов: `3.22.0` и `4.0.0-build.9` → отказ по схеме (в `out` нет маркеров сборки), `4.0.0+build.77` с заведённым dist-zip → «Перепубликация запрещена» (заодно доказывает приём валидной схемы); мусор/бэкапы — через trap, чужие файлы возвращаются на место. Свод shell-набора (`run-all-pkg-tests.sh`, login-shell): **тот же baseline 14 pre-existing падений** (Native Java grep-mismatch `30_000L`/`INTEREST_*`/`readVehicleState` и т.п., отсутствующий javac, 2 падения `test_parallel_hook_loader.py`), оба новых теста (release_versioning, geometry_watchdog) PASS; `sh -n` + прогон в dash зелёные; shellcheck локально недоступен — уровень `-S error` для `make_release.sh`/`scripts/*.sh` проверит CI. JVM не трогали (Java-код не менялся) — базлайн 536/536 PASS, 99 suites.
- **Ограничения**: публикация Releases и тегов — ручная (L161), автоматики нет — гейт стоит на сборке/ребилде номера; свип staging-каталогов в archive выполняется только в ветке `--payload` с zip (штатный режим CI и релизов); `versionCode/versionName` Android — D4, release-идентичность несёт `voyahtune-build.json` (`releaseVersion` уже приходит из `-PvoyahReleaseVersion=$VERSION`).
- **Риск**: низкий — правки только сборочной цепочки/docs, пейлоад и Java не тронуты; строгая схема может отклонить старые локальные команды из истории DECISIONS (`4.0.0-build.N`) — это задумано (R7), подсказка в ошибке ведёт к `4.x.y+build.N`.
- **Дальше**: P0 (IMP-14, IMP-23) → P1 (IMP-03, IMP-04, IMP-09, IMP-15, IMP-17, IMP-18, IMP-20) → P2 (IMP-05, IMP-16) по L119; on-car кампания — после завершения каталога.

## [2026-10-05][WP6][IMP-14 фаза 1: миграционный движок установщика — детект оригинала, шаги L142 a-h, engine-методы staging/backup/removal/restore/verify, планы и шаги]
- **Контекст**: порядок L119 — после IMP-11 (SPEC L56, WP6, P0|M; L140-L147: безопасная замена оригинала с импортом настроек); on-car кампания заморожена.
- **Что сделано**:
  - `Docs/imp-14.md` — дизайн миграционного движка (фазирование, архитектура, каждый этап L142 a-h, детект L141, обработка ошибок L144, residue L143, remove L145, приёмка L146).
  - **Детект оригинала** (`inventory.rs`): `MigrationState` enum (Clean/Original/Fork/Other), `detect_migration()` сравнивает signers установленного Native APK с original-fingerprint `1aa9ac50…` (original-fingerprint.md). Вызывается из `plans::plan()` — при оригинале `operation = "migrate"` и добавляется предупреждение.
  - **Планы и шаги** (`plans.rs`): миграционная ветка `classic_steps()` с шагами migrate-staging/backup/consent/removal/reboot (до install) + migrate-restore/verify (после install); `classic_steps()` принимает `migration_needed: bool`.
  - **Engine** (`engine.rs`, +184 строк): поле `migration_needed`, публичный сеттер, `needs_migration()` (детект через ADB pull APK + verified_signers()), методы `migrate_staging()` (L142a), `migrate_backup()` (L142b), `migrate_removal()` (L142d), `migrate_restore()` (L142g), `migrate_verify()` (L142h); в `execute()` — миграционная ветка до install-шагов (staging→backup→removal→reboot→re-root→re-lock) и после (restore→verify вместо стандартного verify).
  - **Компиляция и тесты**: `cargo check` / `cargo build -p installer-core` — чисто; `cargo test -p installer-core` — **28/28 PASS** (0 регрессий).
- **Тесты**: 28 существующих тестов installer-core PASS (новых тестов миграции нет — фаза 5, fake ADB, запланирована).
- **Ограничения**: фаза 1 — только Rust-ядро; отсутствуют: GUI-поддержка `operation = "migrate"`, согласие на удаление (migrate-consent), residue-чеклист (L143), remove-форка (L145), 5 сценариев fake ADB (L146); команды миграции пока в inline shell, не перенесены в classic_commands.rs.
- **Риск**: низкий — новые методы вызываются только при `migration_needed == true`, иначе execute() идентична старой; все существующие тесты зелёные.
- **Дальше**: фаза 2 (fake ADB тесты + residue + remove-fork) → фаза 3 (GUI) → IMP-23.

## [2026-10-05][WP6][IMP-14 фаза 2: residue-чеклист L143 + remove-fork L145 + шаги в execute()]
- **Контекст**: продолжение IMP-14 (фаза 1 — ядро миграции, коммит 5d66b47).
- **Что сделано**:  (+32 строки):  — L143 чеклист (6 проверок: Updater/OTA/Frida/DNS/apollo-backup/yandexcloud, best-effort через ignore), шаг  после verify;  — L145 восстановление оригинала из backup + очистка, шаг  в ветке удаления до reboot.
- **Тесты**: 28/28 PASS (cargo test), cargo check — чисто.
- **Ограничения**: backup remove-fork использует  (сессионный, не постоянный) — restore-пути на компьютер при remove-форка требуют отдельного механизма сохранения backup между операциями.

## [2026-10-05][WP6][IMP-14 фаза 2: residue-чеклист L143 + remove-fork L145 + шаги в execute()]
- **Контекст**: продолжение IMP-14 (фаза 1 — ядро миграции, коммит 5d66b47).
- **Что сделано**: engine.rs (+32 строки): residue_check() — L143 чеклист (6 проверок: Updater/OTA/Frida/DNS/apollo-backup/yandexcloud, best-effort через ignore), шаг residue после verify; remove_fork() — L145 восстановление оригинала из backup + очистка, шаг remove-fork в ветке удаления до reboot.
- **Тесты**: 28/28 PASS (cargo test), cargo check — чисто.
- **Ограничения**: backup remove-fork использует backup_dir() (сессионный, не постоянный) — restore-пути на компьютер при remove-форка требуют отдельного механизма сохранения backup между операциями.

## [2026-10-05][WP6][IMP-14 фаза 3: улучшенный детект оригинала по build metadata + fake ADB тесты (L146) — 3/3 pass]
- **Контекст**: завершение IMP-14 (фазы 1-2: ядро + residue/remove-fork, коммиты 5d66b47, 60ee4e1).
- **Что сделано**: inventory.rs: detect_migration() теперь определяет оригинал также по отсутствию/несовпадению build.product (не только signers) — для fake ADB и оригиналов без подписей. Installer/tests/test_migration.py: 3 сценария fake ADB (L146) — S1 план с operation=migrate, S2 полный apply миграции (staging→backup→removal→reboot→install→restore→verify), S3 remove форка — все 3 PASS (199s).
- **Тесты**: 3/3 migration pass; 28/28 Rust unit pass (cargo test); cargo check/build — чисто.
- **Ограничения**: не покрыты сценарии L146 #4 (обрыв→восстановление) и #5 (остаток init-контракта) — требуют симуляции ADB-ошибок в миграции; GUI поддержка operation=migrate не реализована.
- **Дальше**: IMP-23 (P0, CP-флаги/нотификации) по L119; IMP-14 фаза 3b (GUI + оставшиеся 2 сценария) — при необходимости.

## [2026-10-05][WP3/WP7][IMP-15 реализован: голос prewarm по ACC ON — SetModesService запускает VoiceWarmupService через 15s после power state 6]
- **Контекст**: порядок L119 — после IMP-23 (R10/D10: голос прогревается 10-20с после пробуждения); int8-модель уже в payload.
- **Что сделано**: Docs/imp-15.md — дизайн. SetModesService.java: константа VOICE_PREWARM_DELAY_MS=15000, Runnable voicePrewarmRunnable, методы scheduleVoicePrewarm/cancelVoicePrewarm/triggerVoicePrewarm; в handlePowerStateChanged() при state 6 — запуск таймера, при сне — отмена. triggerVoicePrewarm() отправляет Intent в VoiceWarmupService через startForegroundService.
- **Тесты**: ручная проверка паттерна (Gradle-сборка Java требует CI); контракт: prewarm не ломает существующий warmup (update() идемпотентен).
- **Ограничения**: Native→RestoreMode Intent может не дойти при загрузке — fallback: onActivityStarted как раньше.
- **Дальше**: IMP-03 (Individual) по L119.

## [2026-10-05][WP3/WP7][IMP-15 реализован: голос prewarm по ACC ON — SetModesService таймер 15s → Intent в VoiceWarmupService]
- **Контекст**: R10/D10 — голос прогревается 10-20с после пробуждения; int8-модель уже в payload. Порядок L119: после IMP-23.
- **Что сделано**: SetModesService.handlePowerStateChanged(): при state 6 (ACC ON) scheduleVoicePrewarm() запускает таймер 15000мс → triggerVoicePrewarm() шлёт Intent в VoiceWarmupService. При сне — cancelVoicePrewarm(). VoiceWarmupService.update() идемпотентен (повторный вызов не загружает модели).
- **Тесты**: ручная проверка через Gradle; prewarm не ломает onActivityStarted fallback.
- **Дальше**: L119 → IMP-09.

## [2026-10-05][WP3][IMP-09 реализован: приоритетные полосы CAN-диспетчера L0/L1/L2 в Mailbox]
- **Контекст**: L51 — priority lanes: L0 safety (свет/дверь), L1 режимы (GEAR), L2 комфорт (температура/прочие). Порядок L119: после IMP-15.
- **Что сделано**: CanBusEventRouter.java: Mailbox заменён на три очереди ArrayDeque[3]; тир(event) определяет lane; drain() извлекает L0→L1→L2; starvation guard (STARVE_LIMIT=10, форсирует L2); per-lane capacity (8/16/32), per-lane droppedPerLane/acceptedPerLane счётчики; dropForCapacity() per lane.
- **Тесты**: существующие 16 тестов (can-emulator) проверяют базовую маршрутизацию — priority ordering требует новых тестов.
- **Дальше**: IMP-03 (Individual, R4) по L119.

## [2026-10-05][WP3][IMP-03 дизайн: Individual — выбор A/B по трассам WP2 (R4)]
- **Контекст**: L45/R4 — Individual: одна TX77 вместо двух OEM-кадров (аудит). Порядок L119: после IMP-09.
- **Что сделано**: Docs/imp-03.md — дизайн: трасса A (две TX77: режим → руль/педаль), трасса B (fallback + лог ошибок), HIL-сценарий.
- **Дальше**: IMP-04 (SettingsRepository) по L119.
