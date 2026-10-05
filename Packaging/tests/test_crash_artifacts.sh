#!/bin/sh
# IMP-08 (SPEC L50): crash artifacts lane + generation registry wiring.
set -eu

ROOT=$(CDPATH= cd -- "$(dirname "$0")/../.." && pwd)
LOADER="$ROOT/Packaging/payload-common/load.bin"
FAULT="$ROOT/Packaging/tests/test_loader_fault_backoff.sh"
REGISTRY="$ROOT/RestoreMode/app/src/main/java/ru/big/town/restoremode/HookGenerationRegistry.java"
CONTRACT="$ROOT/RestoreMode/app/src/main/java/ru/big/town/restoremode/HookStatusContract.java"
PROVIDER="$ROOT/RestoreMode/app/src/main/java/ru/big/town/restoremode/RestoreModeContentProvider.java"
ADVANCE="$ROOT/RestoreMode/app/src/main/java/ru/big/town/restoremode/AdvanceActivity.java"
LOGGING="$ROOT/RestoreMode/app/src/main/java/ru/big/town/restoremode/LoggingActivity.java"
EXPORTER="$ROOT/RestoreMode/app/src/main/java/ru/big/town/restoremode/HookReportExporter.java"
APP_MANIFEST="$ROOT/RestoreMode/app/src/main/AndroidManifest.xml"
LOGGING_LAYOUT="$ROOT/RestoreMode/app/src/main/res/layout/activity_logging.xml"
IDS="$ROOT/RestoreMode/app/src/main/res/values/ids.xml"
SPEC_DOC="$ROOT/Docs/imp-08.md"
MATRIX="$ROOT/Docs/behavior-matrix.md"

fail() { echo "crash artifacts test failed: $*" >&2; exit 1; }
require() { grep -Fq -- "$2" "$1" || fail "$1: missing $2"; }
forbid() { if grep -Fq -- "$2" "$1"; then fail "$1: forbidden $2"; fi; }

# --- loader: dedicated crashes lane (never inside the status lane) ---
require "$LOADER" 'WORKER_LANES="acc steering multidisplay launcher vd apollo keyboard apps status crashes"'
require "$LOADER" 'crashes) collect_crash_artifacts ;;'
require "$LOADER" 'apollo|keyboard|apps|status|crashes) WORKER_INTERVAL=$OPTIONAL_CYCLE_SECONDS ;;'
require "$LOADER" 'status) collect_worker_states; publish_hook_status running ;;'
forbid "$LOADER" 'status) collect_worker_states; publish_hook_status running; collect_crash'

# --- loader: canonical store, marker protocol, SPEC window, retention ---
require "$LOADER" 'CRASH_ROOT=/data/local/voyahtune/crashes'
require "$LOADER" 'CRASH_MIRROR=/data/data/ru.big.town.restoremode/files/crashes'
require "$LOADER" 'CRASH_MARK=/data/local/tmp/voyahtune_crash.mark'
require "$LOADER" 'CRASH_WINDOW=30'
require "$LOADER" 'CRASH_KEEP=10'
require "$LOADER" 'collect_crash_artifacts() {'
require "$LOADER" 'crash_mirror_artifacts() {'
require "$LOADER" 'printf '"'"'pending|%s\n'"'"' "$CA_NEWEST"'
require "$LOADER" 'printf '"'"'ready|%s\n'"'"' "$CA_NEWEST"'
require "$LOADER" '[ -x /system/bin/dumpsys ] || return 0'
require "$LOADER" 'dumpsys -t 30 window > "$CA_DIR/dumpsys_window.txt"'
require "$LOADER" 'dumpsys -t 30 activity activities > "$CA_DIR/dumpsys_activity.txt"'
require "$LOADER" 'dumpsys -t 30 meminfo > "$CA_DIR/dumpsys_meminfo.txt"'
require "$LOADER" 'cat "$CA_TOMB" > "$CA_DIR/$CA_NAME.txt"'
require "$LOADER" 'chown -R "$CM_UID:$CM_UID" "$CRASH_MIRROR"'
require "$LOADER" 'restorecon -RF "$CRASH_MIRROR"'

# --- fault-backoff contract tracks the extended lane list exactly once ---
[ "$(grep -Fc 'WORKER_LANES="acc steering multidisplay launcher vd apollo keyboard apps status crashes"' "$FAULT")" -eq 1 ] \
    || fail "test_loader_fault_backoff.sh must pin the extended WORKER_LANES exactly once"

# --- app: generation registry (manifest / generation / grace / journal) ---
require "$REGISTRY" 'PREFERENCES_NAME = "HookGeneration"'
require "$REGISTRY" 'MANIFEST_VERSION = "3.22.0"'
require "$REGISTRY" 'GRACE_MS'
require "$REGISTRY" 'reconcile('
require "$REGISTRY" 'journal'
require "$REGISTRY" 'BOOT_COUNT'
require "$REGISTRY" 'HOOK_IDS'
require "$CONTRACT" 'static final String[] HOOK_IDS'
require "$REGISTRY" 'class HookGenerationRegistry'

# --- app: boot-reconciliation hooks (payload publish + metrics tick) ---
require "$PROVIDER" 'HookGenerationRegistry'
require "$ADVANCE" 'HookGenerationRegistry'

# --- app: local report export (no network) via FileProvider ---
require "$EXPORTER" 'ZipOutputStream'
require "$EXPORTER" 'hook_status.txt'
require "$EXPORTER" 'crashes'
require "$LOGGING" 'onButtonShareReport'
require "$LOGGING" 'ACTION_SEND'
forbid "$LOGGING" 'http://'
forbid "$LOGGING" 'https://'
require "$LOGGING_LAYOUT" 'android:id="@id/buttonShareReport"'
require "$LOGGING_LAYOUT" 'android:onClick="onButtonShareReport"'
require "$IDS" 'buttonShareReport'
require "$APP_MANIFEST" 'ru.big.town.restoremode.fileprovider'
require "$APP_MANIFEST" 'android:resource="@xml/file_paths"'

# --- documentation and fork-difference record (SPEC L120/L116) ---
require "$SPEC_DOC" 'IMP-08'
require "$SPEC_DOC" '/data/local/voyahtune/crashes/'
require "$SPEC_DOC" 'HookGenerationRegistry'
require "$MATRIX" 'IMP-08'

echo "PASS: IMP-08 crash artifacts lane, generation registry and local report export"
