#!/bin/sh
# ./make_release.sh VERSION --payload [--no-build] [--no-zip]
#
# Сборка payload для форка VoyahTune (SPEC L101 «Шаг 1.3»; адаптация
# reference-3.14/make_release.sh под нашу структуру репозитория).
#
#   ./make_release.sh 4.0.0+build.1 --payload
#       → Releases/build/payload-<VERSION>/          (manifest.json + common/*)
#       → Releases/dist/payload_<VERSION>.zip        (manifest.json + common/ в корне архива)
#       → Releases/dist/payload_<VERSION>.json       (version/url/size/sha256)
#       → Releases/dist/BUILD-INFO.json              (revision, тулчейн вкл. Rust-пин, builder id,
#                                                      дата, хеши zip/manifest/APK — IMP-11/SPEC L53)
#       → Releases/dist/SHA256SUMS                   (все вышеперечисленные артефакты)
#       старые build уходят в Releases/archive/<version>/ (IMP-11/SPEC L139)
#
#   --no-build   APK не пересобирать, взять из уже существующей сборки Gradle
#   --no-zip     архив и сопроводительные файлы не генерировать (только staging + manifest)
#
# Источник payload:
#   * 24 файла  → Packaging/payload-common/ (В GIT)
#   * 2 APK     → сборка Gradle (:Native:app / :RestoreMode:app, assembleRelease)
#   * 6 бинарей → blobs/ по sha256 из blobs/BLOBS-SHA256.txt (ре-хост blobs-v1; fetch-blobs.sh)
# Состав жёстко сверяется с payload/manifest.json — лишние/недостающие файлы это ошибка.
#
# Releases/ — ТОЛЬКО вывод и целиком в .gitignore (сборки в build/, раздача в dist/).
# Классический релиз (install.sh, .bat, инжект-скрипты) приедёт с Installer (Шаг 1.4).
set -e

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
COMMON="$ROOT/Packaging"
PAYLOAD_COMMON="$COMMON/payload-common"
BUILD="$ROOT/Releases/build"
DIST="$ROOT/Releases/dist"
ARCHIVE="$ROOT/Releases/archive"
MANIFEST_SRC="$ROOT/payload/manifest.json"
BLOBS_LIST="$ROOT/blobs/BLOBS-SHA256.txt"

# Бинарии, которых нет в Packaging/payload-common: источник — blobs/ (ре-хост по sha256).
BLOB_ASSETS="blobs/frida/frida-inject
blobs/native/voyahtune-ui-maintenance
blobs/native/voyahtune-updater
blobs/apk/dns.apk
blobs/apk/voyahtune-ui-next.apk
blobs/apk/voyahtune-updater.apk"

# Python нужен для dispatcher'а и manifest.json (regen/verify). На CI это python3;
# локально под Windows часто остаётся одна заглушка «python3 из Microsoft Store» — тогда берём python.
PYTHON="${PYTHON:-}"
if [ -z "$PYTHON" ]; then
    if python3 -c 'import sys' >/dev/null 2>&1; then
        PYTHON="python3"
    elif python -c 'import sys' >/dev/null 2>&1; then
        PYTHON="python"
    else
        echo "Не найден python3/python — сборка manifest.json невозможна." >&2
        exit 1
    fi
fi

# Диспетчер эталонного release-процесса (reference make_release.sh:16-22): флаги
# настольных установщиков уходят в Installer/scripts/release.py (сборка GUI-установщиков,
# macOS-only). Наш --payload остаётся здесь: CI job payload ждёт BUILD-INFO/SHA256SUMS.
for release_arg in "$@"; do
    case "$release_arg" in
        --installers|--mac|--windows|--linux)
            exec "$PYTHON" "$ROOT/Installer/scripts/release.py" "$@" ;;
    esac
done

VERSION=""
MODE=""
DO_BUILD=1
DO_ZIP=1

for arg in "$@"; do
    case "$arg" in
        --payload)  MODE="payload" ;;
        --no-build) DO_BUILD=0 ;;
        --no-zip)   DO_ZIP=0 ;;
        -h|--help)  sed -n '2,25p' "$0"; exit 0 ;;
        -*)         echo "Неизвестный флаг: $arg" >&2; exit 1 ;;
        *)          VERSION="$arg" ;;
    esac
done

if [ -z "$MODE" ]; then
    echo "Не выбран режим. Сейчас поддерживается только --payload (Шаг 1.3);" >&2
    echo "классический релиз появится с Installer (Шаг 1.4). См. --help." >&2
    exit 1
fi

if [ -z "$VERSION" ]; then
    echo "Не указана версия. Пример: ./make_release.sh 4.0.0+build.1 --payload" >&2
    exit 1
fi
# Версию принимаем и как «4.0.0+build.1», и как «v4.0.0+build.1» — нормализуем к виду без префикса.
VERSION="${VERSION#v}"
# IMP-11 (R7, SPEC L53/L136): единственная допустимая схема форка — 4.x.y+build.N.
# Строгость обязательна: прежняя запись 4.0.0-build.N создавала два написания одного
# номера — это и есть уязвимость R7 (перепубликация под одним номером).
if ! printf '%s\n' "$VERSION" | grep -Eq '^4\.[0-9]{1,4}\.[0-9]{1,4}\+build\.[0-9]{1,6}$'; then
    echo "Недопустимая версия '$VERSION': схема форка — 4.x.y+build.N (SPEC L53/L136, R7)." >&2
    exit 1
fi
# Гейт перепубликации (R7/IMP-11): уже использованный номер не переиспользуем — ни в dist,
# ни в archive (номер, однажды ушедший в архив, всё равно «существующий»). CI дополнительно
# падает по существующему тегу v<VERSION> (см. .github/workflows/ci.yml, SPEC L136).
if [ -f "$DIST/payload_$VERSION.zip" ]; then
    echo "Перепубликация запрещена (R7/IMP-11): dist/payload_$VERSION.zip уже существует — увеличьте build.N." >&2
    exit 1
fi
if [ -d "$ARCHIVE/$VERSION" ]; then
    echo "Перепубликация запрещена (R7/IMP-11): номер $VERSION уже уходил в Releases/archive — увеличьте build.N." >&2
    exit 1
fi

sha256_file() {
    if command -v sha256sum >/dev/null 2>&1; then
        sha256sum "$1" | awk '{print $1}'
    elif command -v shasum >/dev/null 2>&1; then
        shasum -a 256 "$1" | awk '{print $1}'
    else
        echo "Не найден ни sha256sum, ни shasum — невозможно проверить $1." >&2
        return 1
    fi
}

file_size() {
    # POSIX-совместимо: wc -c работает и на GNU, и на BSD (macOS).
    wc -c < "$1" | tr -d '[:space:]'
}

STAGING_DIR=""
ZIP_TMP=""
RELEASE_LOCK=""
RELEASE_LOCK_HELD=0
IDENTITY_DIR=""

cleanup_release_stage() {
    if [ -n "$IDENTITY_DIR" ] && [ -d "$IDENTITY_DIR" ]; then
        rm -rf "$IDENTITY_DIR" || true
    fi
    if [ -n "$STAGING_DIR" ] && [ -d "$STAGING_DIR" ]; then
        rm -rf "$STAGING_DIR" || true
    fi
    if [ -n "$ZIP_TMP" ] && [ -f "$ZIP_TMP" ]; then
        rm -f "$ZIP_TMP" || true
    fi
    if [ "$RELEASE_LOCK_HELD" = 1 ] && [ -n "$RELEASE_LOCK" ]; then
        rmdir "$RELEASE_LOCK" 2>/dev/null || true
    fi
}

handle_release_signal() {
    trap - HUP INT TERM
    exit 1
}

trap cleanup_release_stage EXIT
trap handle_release_signal HUP INT TERM

mkdir -p "$BUILD"
RELEASE_LOCK="$BUILD/.release-$VERSION.lock"
# Блокируем обработчики только на tiny critical section mkdir+ownership flag: иначе signal между
# успешным mkdir и assignment оставит stale lock.
trap '' HUP INT TERM
if ! mkdir "$RELEASE_LOCK" 2>/dev/null; then
    trap handle_release_signal HUP INT TERM
    echo "Уже идёт сборка версии $VERSION (lock: $RELEASE_LOCK)." >&2
    exit 1
fi
RELEASE_LOCK_HELD=1
trap handle_release_signal HUP INT TERM

[ -d "$PAYLOAD_COMMON" ] || { echo "Нет $PAYLOAD_COMMON — источник payload отсутствует." >&2; exit 1; }
[ -f "$MANIFEST_SRC" ] || { echo "Нет $MANIFEST_SRC — базовый контракт payload отсутствует." >&2; exit 1; }

# ---------------------------------------------------------------------------------------------
# 0. Контрактные проверки (reference make_release.sh:200-239; порядок — как в
#     Installer/scripts/release.py). Защищают состав payload и install/remove-скрипты.
# ---------------------------------------------------------------------------------------------
verify_payload_contracts() {
    if ! sh "$COMMON/tests/test_android11_package_lifecycle.sh"; then
        echo "Android 11 package lifecycle guard failed; release was not created." >&2
        exit 1
    fi
    if ! sh "$COMMON/tests/test_saved_config_startup_wake.sh"; then
        echo "Startup/wake saved-config guard failed; release was not created." >&2
        exit 1
    fi
    if ! sh "$COMMON/tests/test_keyboard_modes.sh"; then
        echo "Keyboard opt-in lifecycle guard failed; release was not created." >&2
        exit 1
    fi
    if ! sh "$COMMON/tests/test_hook_status.sh"; then
        echo "Hook status/install contract guard failed; release was not created." >&2
        exit 1
    fi
    if ! sh "$COMMON/tests/test_app_client.sh"; then
        echo "App client geometry/packaging guard failed; release was not created." >&2
        exit 1
    fi
    if ! sh "$COMMON/tests/test_mapkit_dpi_client.sh"; then
        echo "MapKit DPI client guard failed; release was not created." >&2
        exit 1
    fi
    if ! sh "$COMMON/tests/test_acc_restore_hook.sh"; then
        echo "ACC restore hook guard failed; release was not created." >&2
        exit 1
    fi
    if ! sh "$COMMON/tests/test_drive_reset_hook.sh"; then
        echo "Account reset hook guard failed; release was not created." >&2
        exit 1
    fi
    if ! sh "$COMMON/tests/test_apollo_safe_device.sh"; then
        echo "Apollo legacy hook guard failed; release was not created." >&2
        exit 1
    fi
    if ! bash "$ROOT/Utils/android11-oem-stubs/tests/static-checks.sh"; then
        echo "Android 11 OEM stub harness guard failed; release was not created." >&2
        exit 1
    fi
    if ! sh -n "$ROOT/Packaging/installer/device/install.sh"; then
        echo "install.sh has invalid shell syntax; release was not created." >&2
        exit 1
    fi
    if ! sh -n "$ROOT/Packaging/installer/device/remove.sh"; then
        echo "remove.sh has invalid shell syntax; release was not created." >&2
        exit 1
    fi
    # Ключевые JS-агенты и загрузчик обязаны входить в состав payload
    # (полный реестр — payload/manifest.json, recipe.files).
    for required in app_client.js load.bin steeringwheelkeys.js; do
        if [ ! -s "$PAYLOAD_COMMON/$required" ]; then
            echo "Нет $PAYLOAD_COMMON/$required — состав payload неполон." >&2
            exit 1
        fi
    done
}
verify_payload_contracts

# ---------------------------------------------------------------------------------------------
# 1. Блобы: fetch по sha256 из ре-хоста (уже совпавшие файлы скрипт не качает повторно).
#     Раньше gradle: release-identity APK хеширует blob-источники при сборке (SPEC L102).
# ---------------------------------------------------------------------------------------------
BLOB_SRC_LIST="$(printf '%s\n' "$BLOB_ASSETS" | tr '\n' ' ')"
# shellcheck disable=SC2086  # пути без пробелов, передаём их как отдельные аргументы
"$ROOT/scripts/fetch-blobs.sh" $BLOB_SRC_LIST

# ---------------------------------------------------------------------------------------------
# 2. APK (Gradle, единый wrapper в корне репозитория) + release identity (SPEC L102).
#     Перед assembleRelease готовим канонический recipe и sources-карту: make_release
#     передаёт их как -PvoyahInstallRecipe/-PvoyahReleaseSources, задача VoyahBuildIdentity
#     пишет в APK assets/voyahtune-build.json (recipeSha256 + runtimeHashes), который
#     verify-payload сверяет с манифестом payload.
# ---------------------------------------------------------------------------------------------
REVISION="$(git -C "$ROOT" rev-parse HEAD 2>/dev/null || echo unknown)"

# win_path: MSYS-путь /c/... → C:/... для Windows-JVM gradle; на Linux (cygpath нет) — как есть.
win_path() {
    if command -v cygpath >/dev/null 2>&1; then cygpath -m "$1"; else echo "$1"; fi
}

apk_output() {
    # $1 — модуль; ищем подписанный release APK, при отсутствии ключей — unsigned.
    if [ -f "$ROOT/$1/app/build/outputs/apk/release/app-release.apk" ]; then
        echo "$ROOT/$1/app/build/outputs/apk/release/app-release.apk"
    elif [ -f "$ROOT/$1/app/build/outputs/apk/release/app-release-unsigned.apk" ]; then
        echo "$ROOT/$1/app/build/outputs/apk/release/app-release-unsigned.apk"
    else
        return 1
    fi
}

NATIVE_APK=""
RESTORE_APK=""

if [ "$DO_BUILD" = 1 ]; then
    IDENTITY_DIR="$(mktemp -d "$BUILD/.identity.XXXXXX")" || exit 1
    RECIPE_FILE="$IDENTITY_DIR/recipe.json"
    SOURCES_FILE="$IDENTITY_DIR/sources.json"
    "$PYTHON" - "$ROOT" "$RECIPE_FILE" "$SOURCES_FILE" <<'PYEOF'
import json
import os
import sys

root, recipe_out, sources_out = sys.argv[1], sys.argv[2], sys.argv[3]
base = json.load(open(os.path.join(root, "payload", "manifest.json"), encoding="utf-8"))
recipe = base["recipe"]
# Каноническая сериализация: compact, ключи по алфавиту (serde_json Map = BTreeMap),
# без завершающего \n — байт-в-байт совпадает с recipe_sha256 в release-core verify.
with open(recipe_out, "wb") as f:
    f.write(json.dumps(recipe, sort_keys=True, separators=(",", ":"),
                       ensure_ascii=False).encode("utf-8"))
common = os.path.join(root, "Packaging", "payload-common")
blob_map = {
    "frida-inject": "blobs/frida/frida-inject",
    "voyahtune-ui-maintenance": "blobs/native/voyahtune-ui-maintenance",
    "voyahtune-updater": "blobs/native/voyahtune-updater",
    "voyahtune-ui-next.apk": "blobs/apk/voyahtune-ui-next.apk",
    "voyahtune-updater.apk": "blobs/apk/voyahtune-updater.apk",
}
artifacts = []
for op in recipe["files"]:
    name = op["artifact"]
    if name == "native.apk":
        # identity native.apk считается после его подписи — как в эталоне, не включаем.
        continue
    if name in blob_map:
        src = blob_map[name]
    elif os.path.isfile(os.path.join(common, name)):
        src = os.path.join("Packaging", "payload-common", name)
    else:
        sys.exit(f"нет источника для {name}")
    if not os.path.isfile(os.path.join(root, src)):
        sys.exit(f"нет файла {src}")
    artifacts.append({"name": name, "source": src})
with open(sources_out, "w", encoding="utf-8") as f:
    json.dump({"schema": 1, "artifacts": artifacts}, f, ensure_ascii=False, indent=1)
PYEOF
    echo ">>> gradlew :Native:app:assembleRelease :RestoreMode:app:assembleRelease (identity -Pvoyah*)"
    (cd "$ROOT" && ./gradlew :Native:app:assembleRelease :RestoreMode:app:assembleRelease -q \
        "-PvoyahReleaseVersion=$VERSION" \
        "-PvoyahBuildRevision=$REVISION" \
        "-PvoyahInstallRecipe=$(win_path "$RECIPE_FILE")" \
        "-PvoyahReleaseSources=$(win_path "$SOURCES_FILE")")
    rm -rf "$IDENTITY_DIR"
    IDENTITY_DIR=""
fi

NATIVE_APK="$(apk_output Native)" || { echo "Нет Native release APK (см. --no-build)." >&2; exit 1; }
RESTORE_APK="$(apk_output RestoreMode)" || { echo "Нет RestoreMode release APK (см. --no-build)." >&2; exit 1; }
case "$NATIVE_APK" in *unsigned*) echo "ВНИМАНИЕ: native.apk БЕЗ подписи (ANDROID_KEYSTORE_* не задан)." ;; esac
case "$RESTORE_APK" in *unsigned*) echo "ВНИМАНИЕ: restore_mode.apk БЕЗ подписи (ANDROID_KEYSTORE_* не задан)." ;; esac

# ---------------------------------------------------------------------------------------------
# 3. Staging: 29 файлов + manifest.json, сверка состава с payload/manifest.json.
# ---------------------------------------------------------------------------------------------
STAGING_DIR="$(mktemp -d "$BUILD/.payload-stage.XXXXXX")" || exit 1
STAGE="$STAGING_DIR/payload-$VERSION"
mkdir -p "$STAGE/common"

for f in "$PAYLOAD_COMMON"/*; do
    name="$(basename "$f")"
    [ "$name" = "README.md" ] && continue
    cp -p "$f" "$STAGE/common/$name"
done
cp -p "$NATIVE_APK"   "$STAGE/common/native.apk"
cp -p "$RESTORE_APK"  "$STAGE/common/restore_mode.apk"
for blob in $BLOB_ASSETS; do
    cp -p "$ROOT/$blob" "$STAGE/common/$(basename "$blob")"
done

"$PYTHON" "$ROOT/Utils/build_payload_manifest.py" "$STAGE" --version "$VERSION" --revision "$REVISION"
"$PYTHON" "$ROOT/Utils/build_payload_manifest.py" "$STAGE" --verify

# ---------------------------------------------------------------------------------------------
# 4. Публикация staging → Releases/build/payload-<VERSION> (atomic replace).
# ---------------------------------------------------------------------------------------------
FINAL_OUT="$BUILD/payload-$VERSION"
PREV_OUT=""
if [ -e "$FINAL_OUT" ]; then
    PREV_OUT="$BUILD/.payload-prev.$$"
    mv "$FINAL_OUT" "$PREV_OUT"
fi
if ! mv "$STAGE" "$FINAL_OUT"; then
    [ -n "$PREV_OUT" ] && mv "$PREV_OUT" "$FINAL_OUT"
    echo "Не удалось опубликовать $FINAL_OUT." >&2
    exit 1
fi
[ -n "$PREV_OUT" ] && rm -rf "$PREV_OUT"
rm -rf "$STAGING_DIR"
STAGING_DIR=""
echo "payload → Releases/build/payload-$VERSION"

# ---------------------------------------------------------------------------------------------
# 5. Dist: архив, запись каталога, BUILD-INFO, SHA256SUMS.
# ---------------------------------------------------------------------------------------------
if [ "$DO_ZIP" = 1 ]; then
    command -v zip >/dev/null 2>&1 || { echo "zip не найден — установите zip или используйте --no-zip." >&2; exit 1; }
    mkdir -p "$DIST"
    # IMP-11/SPEC L139: старые build уезжают в Releases/archive/<version>/ ДО перезаписи dist,
    # чтобы комплект прошлого номера (zip/json + его BUILD-INFO/SHA256SUMS) не потерялся.
    OLD_BUILD_VERSION="$(sed -n 's/.*"version": *"\([^"]*\)".*/\1/p' "$DIST/BUILD-INFO.json" 2>/dev/null | head -n 1)"
    if [ -n "$OLD_BUILD_VERSION" ] && [ "$OLD_BUILD_VERSION" != "$VERSION" ]; then
        mkdir -p "$ARCHIVE/$OLD_BUILD_VERSION"
        for stale in "BUILD-INFO.json" "SHA256SUMS" \
                "payload_$OLD_BUILD_VERSION.zip" "payload_$OLD_BUILD_VERSION.json"; do
            if [ -f "$DIST/$stale" ]; then
                mv "$DIST/$stale" "$ARCHIVE/$OLD_BUILD_VERSION/$stale"
            fi
        done
        echo "archive ← dist: $OLD_BUILD_VERSION → Releases/archive/$OLD_BUILD_VERSION/"
    fi
    # Просроченные чужие payload_* (прерванные прошлые прогоны) — тоже в archive.
    for stale_zip in "$DIST"/payload_*.zip; do
        [ -f "$stale_zip" ] || continue
        stale_name="$(basename "$stale_zip")"
        stale_v="${stale_name#payload_}"
        stale_v="${stale_v%.zip}"
        if [ "$stale_v" = "$VERSION" ]; then continue; fi
        mkdir -p "$ARCHIVE/$stale_v"
        mv "$stale_zip" "$ARCHIVE/$stale_v/$stale_name"
        if [ -f "$DIST/payload_$stale_v.json" ]; then
            mv "$DIST/payload_$stale_v.json" "$ARCHIVE/$stale_v/payload_$stale_v.json"
        fi
    done
    ZIP_TMP="$DIST/.payload_$VERSION.zip.part.$$"
    # Архив пакуется ИЗНУТРИ каталога: manifest.json и common/ должны лежать в корне zip
    # (контракт Installer, тот же layout, что у легендного payload_3.22.0.zip).
    (cd "$FINAL_OUT" && zip -qr "$ZIP_TMP" manifest.json common)
    if command -v unzip >/dev/null 2>&1; then
        unzip -tq "$ZIP_TMP" >/dev/null
    fi
    ZIP_FILE="$DIST/payload_$VERSION.zip"
    mv -f "$ZIP_TMP" "$ZIP_FILE"
    ZIP_TMP=""

    ZIP_SHA="$(sha256_file "$ZIP_FILE")"
    ZIP_SIZE="$(file_size "$ZIP_FILE")"

    # url берётся из fork.config.toml [distribution].releases_url_template (WP5-эндпоинт),
    # переопределяется переменной окружения PAYLOAD_RELEASE_URL.
    URL_TEMPLATE="$(sed -n 's/^releases_url_template[[:space:]]*=[[:space:]]*"\([^"]*\)".*/\1/p' "$ROOT/fork.config.toml")"
    PAYLOAD_URL="${PAYLOAD_RELEASE_URL:-$(printf '%s' "$URL_TEMPLATE" | sed "s/{version}/$VERSION/g")}"
    if [ -z "$URL_TEMPLATE" ] && [ -z "${PAYLOAD_RELEASE_URL:-}" ]; then
        echo "ВНИМАНИЕ: releases_url_template в fork.config.toml пуст — url в записи будет пустым." >&2
    fi

    ENTRY_FILE="$DIST/payload_$VERSION.json"
    printf '{\n  "version": "%s",\n  "url": "%s",\n  "size": %s,\n  "sha256": "%s"\n}\n' \
        "$VERSION" "$PAYLOAD_URL" "$ZIP_SIZE" "$ZIP_SHA" > "$ENTRY_FILE.tmp"
    mv -f "$ENTRY_FILE.tmp" "$ENTRY_FILE"

    # BUILD-INFO (SPEC L135/L53): revision, тулчейн вкл. пин Rust, builder id, дата, хеши.
    GRADLE_V="$(sed -n 's/.*gradle-\([0-9][0-9.]*\)-bin\.zip.*/\1/p' "$ROOT/gradle/wrapper/gradle-wrapper.properties")"
    AGP_V="$(sed -n "s/.*com.android.application' version '\([0-9][0-9.]*\)'.*/\1/p" "$ROOT/build.gradle")"
    JDK_V="$(java -version 2>&1 | sed -n 's/.*version "\([0-9][0-9]*\).*/\1/p' | head -n 1)"
    # Источник правды по Rust — пин Installer/rust-toolchain.toml (SPEC L53: тулчейн Rust 1.98.1).
    RUST_V="$(sed -n 's/^channel[[:space:]]*=[[:space:]]*"\([^"]*\)".*/\1/p' "$ROOT/Installer/rust-toolchain.toml" 2>/dev/null | head -n 1)"
    if [ -z "$RUST_V" ] && command -v rustc >/dev/null 2>&1; then
        RUST_V="$(rustc --version 2>/dev/null | awk '{print $2}' || true)"
    fi
    BUILDER_ID="${BUILDER_ID:-}"
    if [ -z "$BUILDER_ID" ] && [ -n "${GITHUB_RUN_ID:-}" ]; then
        BUILDER_ID="github-actions/run-$GITHUB_RUN_ID"
    fi
    if [ -z "$BUILDER_ID" ]; then
        BUILDER_ID="$(id -un 2>/dev/null || echo unknown)@$(hostname 2>/dev/null || echo unknown)"
    fi
    # Хеши раздаваемого комплекта (SPEC L53 «хеши»): сам zip + внутренний manifest + обе APK.
    MANIFEST_SHA="$(sha256_file "$FINAL_OUT/manifest.json")"
    NATIVE_SHA="$(sha256_file "$NATIVE_APK")"
    RESTORE_SHA="$(sha256_file "$RESTORE_APK")"
    BUILD_INFO="$DIST/BUILD-INFO.json"
    printf '{\n  "product": "VoyahTune",\n  "version": "%s",\n  "revision": "%s",\n  "toolchain": {\n    "gradle": "%s",\n    "agp": "%s",\n    "jdk": "%s",\n    "rust": "%s"\n  },\n  "builder": "%s",\n  "date": "%s",\n  "hashes": {\n    "payload_%s.zip": "%s",\n    "manifest.json": "%s",\n    "native.apk": "%s",\n    "restore_mode.apk": "%s"\n  }\n}\n' \
        "$VERSION" "$REVISION" "${GRADLE_V:-unknown}" "${AGP_V:-unknown}" "${JDK_V:-unknown}" "${RUST_V:-unknown}" \
        "$BUILDER_ID" "$(date -u +%Y-%m-%dT%H:%M:%SZ)" \
        "$VERSION" "$ZIP_SHA" "$MANIFEST_SHA" "$NATIVE_SHA" "$RESTORE_SHA" > "$BUILD_INFO.tmp"
    mv -f "$BUILD_INFO.tmp" "$BUILD_INFO"

    # SHA256SUMS — все раздаваемые артефакты, формат sha256sum -c (два пробела, относительные пути).
    (cd "$DIST" && {
        for f in "payload_$VERSION.zip" "payload_$VERSION.json" "BUILD-INFO.json"; do
            sha256_file "$f" | awk -v n="$f" '{print $1 "  " n}'
        done
    } > SHA256SUMS.tmp && mv -f SHA256SUMS.tmp SHA256SUMS)

    echo "dist → Releases/dist/payload_$VERSION.zip ($ZIP_SIZE bytes, sha256 $ZIP_SHA)"
    echo "cat  → Releases/dist/payload_$VERSION.json, BUILD-INFO.json, SHA256SUMS"

    # IMP-11/SPEC L139: staging-каталоги прошлых номеров → в архив (после успешной сборки,
    # чтобы сбой текущего прогона не уничтожал прошлые комплекты).
    for old_build in "$BUILD"/payload-*; do
        [ -d "$old_build" ] || continue
        old_name="$(basename "$old_build")"
        old_v="${old_name#payload-}"
        if [ "$old_v" = "$VERSION" ]; then continue; fi
        mkdir -p "$ARCHIVE/$old_v/build"
        if [ -e "$ARCHIVE/$old_v/build/$old_name" ]; then
            rm -rf "$ARCHIVE/$old_v/build/$old_name"
        fi
        mv "$old_build" "$ARCHIVE/$old_v/build/$old_name"
        echo "archive ← build: $old_v/$old_name → Releases/archive/$old_v/build/"
    done
fi

echo "Готово: $FINAL_OUT"
