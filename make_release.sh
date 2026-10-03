#!/bin/sh
# ./make_release.sh VERSION --payload [--no-build] [--no-zip]
#
# Сборка payload для форка VoyahTune (SPEC L101 «Шаг 1.3»; адаптация
# reference-3.14/make_release.sh под нашу структуру репозитория).
#
#   ./make_release.sh 4.0.0-build.1 --payload
#       → Releases/build/payload-<VERSION>/          (manifest.json + common/*)
#       → Releases/dist/payload_<VERSION>.zip        (manifest.json + common/ в корне архива)
#       → Releases/dist/payload_<VERSION>.json       (version/url/size/sha256)
#       → Releases/dist/BUILD-INFO.json              (revision, тулчейн, builder id, дата)
#       → Releases/dist/SHA256SUMS                   (все вышеперечисленные артефакты)
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
COMMON="$ROOT/Packaging/payload-common"
BUILD="$ROOT/Releases/build"
DIST="$ROOT/Releases/dist"
MANIFEST_SRC="$ROOT/payload/manifest.json"
BLOBS_LIST="$ROOT/blobs/BLOBS-SHA256.txt"

# Бинарии, которых нет в Packaging/payload-common: источник — blobs/ (ре-хост по sha256).
BLOB_ASSETS="blobs/frida/frida-inject
blobs/native/voyahtune-ui-maintenance
blobs/native/voyahtune-updater
blobs/apk/dns.apk
blobs/apk/voyahtune-ui-next.apk
blobs/apk/voyahtune-updater.apk"

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
    echo "Не указана версия. Пример: ./make_release.sh 4.0.0-build.1 --payload" >&2
    exit 1
fi
# Версию принимаем и как «3.2.2», и как «v3.2.2» — нормализуем к виду без префикса.
VERSION="${VERSION#v}"
# Ниже готовая папка заменяется целиком, поэтому имя обязано быть одним безопасным path-компонентом.
case "$VERSION" in
    [A-Za-z0-9]*) ;;
    *) echo "Недопустимая версия '$VERSION': первый символ должен быть латинской буквой или цифрой." >&2; exit 1 ;;
esac
case "$VERSION" in
    *[!A-Za-z0-9._+-]*) echo "Недопустимая версия '$VERSION': разрешены A-Z, a-z, 0-9, '.', '_', '+', '-'." >&2; exit 1 ;;
esac

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

# Python нужен только для manifest.json (regen/verify). На CI это python3; локально под
# Windows часто остаётся одна заглушка «python3 из Microsoft Store» — тогда берём python.
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

file_size() {
    # POSIX-совместимо: wc -c работает и на GNU, и на BSD (macOS).
    wc -c < "$1" | tr -d '[:space:]'
}

STAGING_DIR=""
ZIP_TMP=""
RELEASE_LOCK=""
RELEASE_LOCK_HELD=0

cleanup_release_stage() {
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

[ -d "$COMMON" ] || { echo "Нет $COMMON — источник payload отсутствует." >&2; exit 1; }
[ -f "$MANIFEST_SRC" ] || { echo "Нет $MANIFEST_SRC — базовый контракт payload отсутствует." >&2; exit 1; }

# ---------------------------------------------------------------------------------------------
# 1. APK (Gradle, единый wrapper в корне репозитория).
# ---------------------------------------------------------------------------------------------
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
    echo ">>> gradlew :Native:app:assembleRelease :RestoreMode:app:assembleRelease"
    (cd "$ROOT" && ./gradlew :Native:app:assembleRelease :RestoreMode:app:assembleRelease -q)
fi

NATIVE_APK="$(apk_output Native)" || { echo "Нет Native release APK (см. --no-build)." >&2; exit 1; }
RESTORE_APK="$(apk_output RestoreMode)" || { echo "Нет RestoreMode release APK (см. --no-build)." >&2; exit 1; }
case "$NATIVE_APK" in *unsigned*) echo "ВНИМАНИЕ: native.apk БЕЗ подписи (ANDROID_KEYSTORE_* не задан)." ;; esac
case "$RESTORE_APK" in *unsigned*) echo "ВНИМАНИЕ: restore_mode.apk БЕЗ подписи (ANDROID_KEYSTORE_* не задан)." ;; esac

# ---------------------------------------------------------------------------------------------
# 2. Блобы: fetch по sha256 из ре-хоста (уже совпавшие файлы скрипт не качает повторно).
# ---------------------------------------------------------------------------------------------
BLOB_SRC_LIST="$(printf '%s\n' "$BLOB_ASSETS" | tr '\n' ' ')"
# shellcheck disable=SC2086  # пути без пробелов, передаём их как отдельные аргументы
"$ROOT/scripts/fetch-blobs.sh" $BLOB_SRC_LIST

# ---------------------------------------------------------------------------------------------
# 3. Staging: 29 файлов + manifest.json, сверка состава с payload/manifest.json.
# ---------------------------------------------------------------------------------------------
STAGING_DIR="$(mktemp -d "$BUILD/.payload-stage.XXXXXX")" || exit 1
STAGE="$STAGING_DIR/payload-$VERSION"
mkdir -p "$STAGE/common"

for f in "$COMMON"/*; do
    name="$(basename "$f")"
    [ "$name" = "README.md" ] && continue
    cp -p "$f" "$STAGE/common/$name"
done
cp -p "$NATIVE_APK"   "$STAGE/common/native.apk"
cp -p "$RESTORE_APK"  "$STAGE/common/restore_mode.apk"
for blob in $BLOB_ASSETS; do
    cp -p "$ROOT/$blob" "$STAGE/common/$(basename "$blob")"
done

"$PYTHON" "$ROOT/Utils/build_payload_manifest.py" "$STAGE" --version "$VERSION" --revision "$(git -C "$ROOT" rev-parse HEAD 2>/dev/null || echo unknown)"
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

    # BUILD-INFO (SPEC L135): revision, тулчейн, builder id, дата.
    REVISION="$(git -C "$ROOT" rev-parse HEAD 2>/dev/null || echo unknown)"
    GRADLE_V="$(sed -n 's/.*gradle-\([0-9][0-9.]*\)-bin\.zip.*/\1/p' "$ROOT/gradle/wrapper/gradle-wrapper.properties")"
    AGP_V="$(sed -n "s/.*com.android.application' version '\([0-9][0-9.]*\)'.*/\1/p" "$ROOT/build.gradle")"
    JDK_V="$(java -version 2>&1 | sed -n 's/.*version "\([0-9][0-9]*\).*/\1/p' | head -n 1)"
    BUILDER_ID="${BUILDER_ID:-}"
    if [ -z "$BUILDER_ID" ] && [ -n "${GITHUB_RUN_ID:-}" ]; then
        BUILDER_ID="github-actions/run-$GITHUB_RUN_ID"
    fi
    if [ -z "$BUILDER_ID" ]; then
        BUILDER_ID="$(id -un 2>/dev/null || echo unknown)@$(hostname 2>/dev/null || echo unknown)"
    fi
    BUILD_INFO="$DIST/BUILD-INFO.json"
    printf '{\n  "product": "VoyahTune",\n  "version": "%s",\n  "revision": "%s",\n  "toolchain": {\n    "gradle": "%s",\n    "agp": "%s",\n    "jdk": "%s"\n  },\n  "builder": "%s",\n  "date": "%s"\n}\n' \
        "$VERSION" "$REVISION" "${GRADLE_V:-unknown}" "${AGP_V:-unknown}" "${JDK_V:-unknown}" \
        "$BUILDER_ID" "$(date -u +%Y-%m-%dT%H:%M:%SZ)" > "$BUILD_INFO.tmp"
    mv -f "$BUILD_INFO.tmp" "$BUILD_INFO"

    # SHA256SUMS — все раздаваемые артефакты, формат sha256sum -c (два пробела, относительные пути).
    (cd "$DIST" && {
        for f in "payload_$VERSION.zip" "payload_$VERSION.json" "BUILD-INFO.json"; do
            sha256_file "$f" | awk -v n="$f" '{print $1 "  " n}'
        done
    } > SHA256SUMS.tmp && mv -f SHA256SUMS.tmp SHA256SUMS)

    echo "dist → Releases/dist/payload_$VERSION.zip ($ZIP_SIZE bytes, sha256 $ZIP_SHA)"
    echo "cat  → Releases/dist/payload_$VERSION.json, BUILD-INFO.json, SHA256SUMS"
fi

echo "Готово: $FINAL_OUT"
