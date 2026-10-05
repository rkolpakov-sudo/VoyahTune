#!/bin/sh
set -eu

ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/../.." && pwd)
VD="$ROOT/Packaging/payload-common/vd_bypass.js"

fail() {
    echo "geometry watchdog contract test failed: $*" >&2
    exit 1
}

node --check "$VD"

require_one() {
    needle=$1
    message=$2
    [ "$(grep -Fc "$needle" "$VD")" -eq 1 ] || fail "$message"
}

require() {
    needle=$1
    message=$2
    grep -Fq "$needle" "$VD" || fail "$message"
}

line_of() {
    grep -nF "$1" "$VD" | head -n 1 | cut -d: -f1
}

require_order() {
    first=$(line_of "$1")
    second=$(line_of "$2")
    [ -n "$first" ] && [ -n "$second" ] && [ "$first" -lt "$second" ] \
        || fail "$3"
}

# --- секция watchdog: константы, файлы, состояния --------------------------------------------
require_one "var GEOMETRY_WATCHDOG_DEBOUNCE_MS = 700;" \
    "watchdog debounce constant must be exactly 700ms"
require_one "var GEOMETRY_JOURNAL_LIMIT = 30;" \
    "geometry journal ring limit must be 30 entries"
require_one 'var GEOMETRY_LKG_PATH = "/data/local/open_voyah/vd_hooks/geometry.lkg";' \
    "last-known-good geometry path is missing or duplicated"
require_one 'var GEOMETRY_JOURNAL_PATH = "/data/local/open_voyah/vd_hooks/geometry.journal";' \
    "geometry journal path is missing or duplicated"
require_one 'installed.push("geometry watchdog (lkg+journal)");' \
    "watchdog must report itself in the installed hooks list"

# --- пять триггеров сверки (SPEC L49): ровно по одному вызову на триггер ---------------------
for trigger in config-change panel-lift display-change wake app-launch; do
    [ "$(grep -Fc "scheduleGeometryCheck(\"$trigger\");" "$VD")" -eq 1 ] \
        || fail "trigger \"$trigger\" must schedule exactly one geometry check"
done

# --- порядок включения триггеров в существующие точки ----------------------------------------
require_order 'scheduleGeometryCheck("config-change");' \
    "if (ffReloadTimer !== null) clearTimeout(ffReloadTimer);" \
    "WIN_RELOAD must schedule a config-change check before its own debounce"
require_order "if (agentFailed || (type !== 1 && type !== 2) || type === FF.liftType) return;" \
    'scheduleGeometryCheck("panel-lift");' \
    "panel-lift check must be scheduled only after the lift receiver guards"
require_order 'scheduleGeometryCheck("panel-lift");' \
    "FF.liftType = type;" \
    "panel-lift check must observe the new lift type"
require_order "if (!FF.on || !FF.screenOn || ffDisplayChangedApplying) return;" \
    'scheduleGeometryCheck("display-change");' \
    "display-change check must respect the reparent replay guards"
require_order "ffApplyTaskDpi(ffTaskField.get(this), pkg);" \
    'scheduleGeometryCheck("app-launch");' \
    "app-launch check must run only for on-screen third-party tasks"
require_order "FF.screenOn = true;" \
    'scheduleGeometryCheck("wake");' \
    "wake check must be scheduled only after the screen is marked interactive"
require_order 'scheduleGeometryCheck("wake");' \
    "if (FF.on) scheduleFreeformHotAttach(attachDelay," \
    "wake check must precede the delayed hot-attach scheduling"

# --- дебаунс: окно-коалесценция, epoch-guard, Java.perform -----------------------------------
sched_start=$(line_of "function scheduleGeometryCheck(trigger) {")
run_start=$(line_of "function runGeometryCheck(trigger) {")
run_end=$(line_of 'installed.push("geometry watchdog (lkg+journal)");')
[ -n "$sched_start" ] && [ -n "$run_start" ] && [ -n "$run_end" ] \
    || fail "watchdog function section is missing"

require_in() {
    needle=$1
    message=$2
    awk -v first="$sched_start" -v last="$run_end" -v text="$needle" '
        NR >= first && NR <= last && index($0, text) { found = 1 }
        END { exit found ? 0 : 1 }
    ' "$VD" || fail "$message"
}

forbid_in_run() {
    needle=$1
    message=$2
    awk -v first="$run_start" -v last="$run_end" -v text="$needle" '
        NR >= first && NR <= last && index($0, text) { found = 1 }
        END { exit found ? 1 : 0 }
    ' "$VD" || fail "$message"
}

require_in "if (agentFailed || !FF || geometryCheckTimer !== null) return;" \
    "scheduleGeometryCheck must coalesce events into one pending 700ms window"
require_in "var epoch = FF.hookEpoch;" \
    "scheduled check must capture the hook epoch"
require_in "if (agentFailed || epoch !== FF.hookEpoch || !FF.on || !FF.screenOn) return;" \
    "pending check must be dropped when the epoch changed or the agent is off/asleep"
require_in "}, GEOMETRY_WATCHDOG_DEBOUNCE_MS);" \
    "check must fire after the 700ms debounce window"
require_in "Java.perform(function () {" \
    "check body must re-enter the Java bridge"

# --- отмена таймера в cancelFreeformPending --------------------------------------------------
require_one "ffTraversalTimer, geometryCheckTimer].forEach(function (timer) {" \
    "geometry check timer must be cancelled with the other pending timers"
[ "$(grep -Fc "geometryCheckTimer = null;" "$VD")" -ge 2 ] \
    || fail "geometry check timer must be reset both on cancellation and on fire"

# --- тело чека: только чтение + лечебные пути, без failAgent/mutations ------------------------
require_in "if (agentFailed || !FF.on || !FF.screenOn) return;" \
    "runGeometryCheck must re-check agent/screen guards"
require_in "if (!refreshFreeformCfg()) {" \
    "runGeometryCheck must refresh the config snapshot"
require_in 'geometryJournalAppend(trigger, "config-invalid", "settings rejected, snapshot retained");' \
    "invalid Settings must be journaled while the in-memory snapshot is retained"
require_in "if (writeGeometryLkgIfChanged()) {" \
    "runGeometryCheck must persist the changed snapshot to LKG"
require_in 'requestFreeformTraversalOnce("geometry watchdog trigger=" + trigger);' \
    "snapshot change must be applied through the ordinary WM traversal"
require_in "&& (!ffLayoutAttached || !ffConfigAttached) && !ffHotAttachPending) {" \
    "detached hot hooks must be reattached only outside the stabilization window"
require_in 'attachFreeformHotHooks("geometry watchdog trigger=" + trigger);' \
    "lost hot hooks must be re-attached by the watchdog"
require_in 'Log.i(TAG, "geometry watchdog heal trigger=" + trigger + " in "' \
    "heal must be reported to KPI log (SPEC L170 <=2s)"

forbid_in_run "failAgent(" \
    "watchdog must never fail the agent"
forbid_in_run "putString" \
    "watchdog must never mutate Settings"
forbid_in_run "putInt" \
    "watchdog must never mutate Settings"
forbid_in_run "SettingsGlobal." \
    "watchdog must only read policy through refreshFreeformCfg"
forbid_in_run "layoutWindowLw" \
    "watchdog must stay out of the layoutWindowLw hot path"

# --- LKG: атомарная запись, поля снимка, restore на старте ------------------------------------
require_one 'File.$new(path + ".new").renameTo(File.$new(path))' \
    "agent text files must be written atomically via .new+rename"
require_one "liftType: FF.liftType, ts: Date.now()" \
    "LKG snapshot must carry viewport fields and a timestamp"
require_one "if (!restoreGeometryFromLkg()) throw new Error(\"initial policy unavailable\");" \
    "startup must fall back to the last-known-good geometry before failing"
require_one 'geometryJournalAppend("startup", "startup-lkg-restore", geometryViewportSummary());' \
    "startup LKG restore must be journaled"
require_one "function restoreGeometryFromLkg() {" \
    "LKG restore function is missing"

# --- журнал: ленивая загрузка, dedup, кольцевой лимит -----------------------------------------
require_one "if (geometryJournal !== null) return;" \
    "journal must be loaded lazily on first use"
require_one "if (key === geometryJournalLastKey) return;" \
    "consecutive duplicate journal entries must be suppressed"
require_one "while (geometryJournal.length > GEOMETRY_JOURNAL_LIMIT) geometryJournal.shift();" \
    "journal must stay within the ring limit"
require_one 'geometryJournal.push(Date.now() + "|" + key);' \
    "journal entries must be epoch|trigger|action|detail"

echo "geometry watchdog contract test: OK"
