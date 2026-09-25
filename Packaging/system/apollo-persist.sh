#!/system/bin/sh
# apollo-persist.sh — per-boot Apollo runtime flag watchdog
#
# Re-creates the apollo_settings_runtime.v1 flag file every 10 seconds so that
# load.bin can inject apollo_tech.js even when the Native app unconditionally
# deletes the flag (NV column 28 = 0 — no Chinese Baidu Apollo subscription).
#
# Installed by the FULL installer; sourced from voyahtune.load.sh as a
# background process. Dies when the RC service is stopped (reboot/shutdown).

PERSIST_TARGET="/data/user_de/0/ru.big.town.anative/files/apollo_settings_runtime.v1"
PERSIST_BOOT_ID_FILE="/proc/sys/kernel/random/boot_id"
PERSIST_TARGET_DIR="/data/user_de/0/ru.big.town.anative/files"
PERSIST_LOCK="/data/local/tmp/apollo_persist.pid"
PERSIST_INTERVAL=10

# Single instance
[ -f "$PERSIST_LOCK" ] && {
    LOCKPID=$(cat "$PERSIST_LOCK" 2>/dev/null) || LOCKPID=""
    [ -n "$LOCKPID" ] && [ "$LOCKPID" != "$$" ] && pgrep -F "$PERSIST_LOCK" >/dev/null 2>&1 && exit 0
}
echo "$$" > "$PERSIST_LOCK" || exit 1

ensure_flag() {
    BOOT_ID=$(cat "$PERSIST_BOOT_ID_FILE" 2>/dev/null) || return 1
    [ -n "$BOOT_ID" ] || return 1

    if [ -f "$PERSIST_TARGET" ]; then
        FLAG_BOOT=""
        FLAG_ENABLED=""
        while IFS='=' read -r key value; do
            [ -n "$key" ] || continue
            case "$key" in
                boot) FLAG_BOOT="$value" ;;
                enabled) FLAG_ENABLED="$value" ;;
            esac
        done < "$PERSIST_TARGET"
        [ "$FLAG_BOOT" = "$BOOT_ID" ] && [ "$FLAG_ENABLED" = "1" ] && return 0
    fi

    mkdir -p "$PERSIST_TARGET_DIR" 2>/dev/null || return 1
    printf 'v=1\nboot=%s\nenabled=1\n' "$BOOT_ID" > "$PERSIST_TARGET" 2>/dev/null || return 1
    chcon u:object_r:app_data_file:s0 "$PERSIST_TARGET" 2>/dev/null || true
    chown system:system "$PERSIST_TARGET" 2>/dev/null || true
}

logi() { /system/bin/log -t vt_apollo_persist -p i "$@"; }
logi "apollo-persist watchdog started (PID $$)"

while true; do
    ensure_flag || logi "ensure_flag failed"
    sleep "$PERSIST_INTERVAL"
done