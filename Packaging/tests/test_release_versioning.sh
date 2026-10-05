#!/bin/sh
set -eu

ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/../.." && pwd)
RELEASE="$ROOT/make_release.sh"
CI="$ROOT/.github/workflows/ci.yml"
TOOLCHAIN="$ROOT/Installer/rust-toolchain.toml"

fail() {
    echo "release versioning contract test failed: $*" >&2
    exit 1
}

require_file() {
    file=$1
    needle=$2
    message=$3
    grep -Fq "$needle" "$file" || fail "$message"
}

forbid_file() {
    file=$1
    needle=$2
    message=$3
    if grep -Fq "$needle" "$file"; then
        fail "$message"
    fi
}

require_count() {
    file=$1
    needle=$2
    expected=$3
    message=$4
    actual=$(grep -Fc "$needle" "$file" || true)
    [ "$actual" -eq "$expected" ] || fail "$message (expected $expected, got $actual)"
}

# --- 1. make_release.sh: ERE-схема, гейт перепубликации, archive, BUILD-INFO -----------------
require_count "$RELEASE" \
    "^4\.[0-9]{1,4}\.[0-9]{1,4}\+build\.[0-9]{1,6}$" 1 \
    "strict version scheme regex must appear exactly once"
require_file "$RELEASE" 'Недопустимая версия' \
    "scheme rejection message is missing"
require_file "$RELEASE" 'Перепубликация запрещена (R7/IMP-11): dist/payload_' \
    "dist-zip republish gate message is missing"
require_file "$RELEASE" 'Перепубликация запрещена (R7/IMP-11): номер' \
    "archive republish gate message is missing"
require_file "$RELEASE" 'ARCHIVE="$ROOT/Releases/archive"' \
    "Releases/archive variable is missing"
require_file "$RELEASE" 'Releases/archive/$OLD_BUILD_VERSION/' \
    "stale dist compacts into archive/<version>/ before overwrite"
require_file "$RELEASE" 'Releases/archive/$old_v/build/' \
    "foreign staging dirs sweep into archive/<version>/build/ after success"
require_file "$RELEASE" '"rust": "%s"' \
    "BUILD-INFO toolchain.rust field is missing"
require_file "$RELEASE" 'Installer/rust-toolchain.toml' \
    "BUILD-INFO must parse the Rust channel pin"
require_file "$RELEASE" '"hashes": {' \
    "BUILD-INFO hashes block is missing"
require_file "$RELEASE" '"payload_%s.zip": "%s"' \
    "BUILD-INFO must hash the payload zip"
require_file "$RELEASE" '"restore_mode.apk": "%s"' \
    "BUILD-INFO must hash the restore-mode apk"
require_file "$RELEASE" './make_release.sh 4.0.0+build.1 --payload' \
    "help/example must use the 4.x.y+build.N scheme"
forbid_file "$RELEASE" '4.0.0-build.1 --payload' \
    "legacy -build.N example must be gone from make_release.sh"

# --- 2. ci.yml: гейт существующего номера + схема версии ------------------------------------
require_file "$CI" 'git ls-remote --tags origin "refs/tags/v${VERSION}"' \
    "ci.yml must fail on an existing v<VERSION> tag"
require_file "$CI" 'Version gate (no republish, R7/IMP-11)' \
    "ci.yml version gate step is missing"
require_count "$CI" '4.0.0+build.${{ github.run_number }}' 2 \
    "ci.yml must build with 4.0.0+build.<run_number> in gate and make_release"
forbid_file "$CI" '4.0.0-build.' \
    "legacy -build.N scheme must be gone from ci.yml"

# --- 3. пин тулчейна Rust (SPEC L53) ---------------------------------------------------------
require_file "$TOOLCHAIN" 'channel = "1.98.1"' \
    "Installer/rust-toolchain.toml must pin Rust 1.98.1"

# --- 4. функциональные отказы: схема и гейт срабатывают ДО тяжёлых шагов ----------------------
expect_reject() {
    ver=$1
    needle=$2
    if out=$(cd "$ROOT" && sh "$RELEASE" "$ver" --payload 2>&1); then
        fail "version $ver must be rejected"
    fi
    case "$out" in
        *"$needle"*) ;;
        *) fail "rejection of $ver must contain '$needle' (got: $out)" ;;
    esac
    case "$out" in
        *"payload → Releases/build"*) fail "version $ver must fail before any build step" ;;
    esac
}

expect_reject "3.22.0" "4.x.y+build.N"           # не-схема форка
expect_reject "4.0.0-build.9" "4.x.y+build.N"     # легаси -build.N → отказ

# Гейт перепубликации: валидная версия доходит до гейта, если dist-zip уже существует.
GATE_V="4.0.0+build.77"
GATE_ZIP="$ROOT/Releases/dist/payload_$GATE_V.zip"
GATE_ARC="$ROOT/Releases/archive/$GATE_V"
GATE_ZIP_BAK=""
GATE_ARC_BAK=""

cleanup() {
    rm -f "$GATE_ZIP"
    if [ -n "$GATE_ZIP_BAK" ]; then mv -f "$GATE_ZIP_BAK" "$GATE_ZIP"; fi
    if [ -n "$GATE_ARC_BAK" ]; then mv -f "$GATE_ARC_BAK" "$GATE_ARC"; fi
    return 0
}
trap cleanup EXIT INT TERM

# Чужие файлы этого номера (если вдруг есть) сохраняем и возвращаем через trap.
mkdir -p "$ROOT/Releases/dist" "$ROOT/Releases/archive"
if [ -e "$GATE_ZIP" ]; then
    GATE_ZIP_BAK="$GATE_ZIP.release_versioning_bak"
    mv "$GATE_ZIP" "$GATE_ZIP_BAK"
fi
if [ -e "$GATE_ARC" ]; then
    GATE_ARC_BAK="$GATE_ARC.release_versioning_bak"
    mv "$GATE_ARC" "$GATE_ARC_BAK"
fi
: > "$GATE_ZIP"
expect_reject "$GATE_V" "Перепубликация запрещена"

echo "release versioning contract: OK"
