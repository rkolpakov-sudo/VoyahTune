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
