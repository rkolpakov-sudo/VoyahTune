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
