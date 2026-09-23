#!/bin/sh
# verify_post_install.sh — read-only чек-лист после успешной install (full или light).
# Ничего не пишет в ГУ. Коды: 0 = все обязательные OK; 1 = есть fail; 2 = нет ADB/устройства.
# Запуск: ./verify_post_install.sh [--light]
#   --light / VERIFY_LIGHT=1 — light-профиль: boot-hook отсутствует = OK (не warn).

set -u

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR" || exit 1

VERIFY_LIGHT="${VERIFY_LIGHT:-0}"
for arg in "$@"; do
    case "$arg" in
        --light) VERIFY_LIGHT=1 ;;
        -h|--help)
            sed -n '2,6p' "$0"
            exit 0
            ;;
    esac
done

if [ -f "$SCRIPT_DIR/tui-lib.sh" ]; then
    # shellcheck source=tui-lib.sh
    . "$SCRIPT_DIR/tui-lib.sh"
    tui_init
else
    tui_ok()   { printf '  [OK] %s\n' "$*"; }
    tui_err()  { printf ' [!!!] %s\n' "$*"; }
    tui_warn() { printf ' [! ] %s\n' "$*"; }
    tui_info() { printf '  %s\n' "$*"; }
    tui_title() { printf '\n%s\n' "$1"; }
    tui_hr() { printf '%s\n' "------------------------------------------------------------"; }
    tui_find_adb() {
        if command -v adb >/dev/null 2>&1; then TUI_ADB=adb; return 0; fi
        if command -v adb.exe >/dev/null 2>&1; then TUI_ADB=adb.exe; return 0; fi
        if [ -x ./adb.exe ]; then TUI_ADB=./adb.exe; return 0; fi
        return 1
    }
fi

if [ "$VERIFY_LIGHT" = 1 ]; then
    tui_title "Open Voyah — verify light (read-only) @VERSION@"
else
    tui_title "Open Voyah — verify (read-only) @VERSION@"
fi

if ! tui_find_adb; then
    tui_err "adb не найден — проверить установку нельзя."
    exit 2
fi

if ! "$TUI_ADB" get-state >/dev/null 2>&1; then
    state="$("$TUI_ADB" get-state 2>/dev/null | tr -d '\r')"
    tui_err "устройство недоступно (state=${state:-unknown}) — подключите кабель и USB debugging."
    exit 2
fi

fails=0
warns=0

check_path() {
    label="$1"; pkg="$2"
    if "$TUI_ADB" shell pm path "$pkg" 2>/dev/null | tr -d '\r' | grep -q '^package:'; then
        tui_ok "$label ($pkg)"
    else
        tui_err "$label: $pkg не установлен"
        fails=$((fails + 1))
    fi
}

check_prop() {
    label="$1"; prop="$2"; want="$3"
    got="$("$TUI_ADB" shell getprop "$prop" 2>/dev/null | tr -d '\r')"
    if [ "$got" = "$want" ]; then
        tui_ok "$label: $prop=$got"
    else
        tui_warn "$label: $prop='$got' (ожидали '$want')"
        warns=$((warns + 1))
    fi
}

check_global() {
    label="$1"; key="$2"; want="$3"
    got="$("$TUI_ADB" shell settings get global "$key" 2>/dev/null | tr -d '\r')"
    if [ "$got" = "$want" ]; then
        tui_ok "$label: $key=$got"
    else
        tui_warn "$label: $key='$got' (ожидали '$want')"
        warns=$((warns + 1))
    fi
}

check_sys_file() {
    label="$1"; path="$2"
    if "$TUI_ADB" shell "test -e $path" 2>/dev/null; then
        tui_ok "$label: $path"
    else
        tui_err "$label: нет $path"
        fails=$((fails + 1))
    fi
}

tui_title "Пакеты"
check_path "Native" "ru.big.town.anative"
check_path "RestoreMode" "ru.big.town.restoremode"

tui_title "Свойства / настройки"
check_prop "leave car (power hold)" persist.app.feature.leavecar true
check_global "freeform" enable_freeform_support 1
check_global "resizable activities" force_resizable_activities 1

tui_title "Whitelist привилегий"
check_sys_file "privapp permissions" \
    /system/etc/permissions/privapp-permissions-ru.big.town.anative.xml

tui_title "Boot-hook full (voyahtune.*.rc)"
if "$TUI_ADB" shell "test -e /system/etc/init/voyahtune.load.sh" 2>/dev/null; then
    ready="$("$TUI_ADB" shell "if [ -x /system/etc/init/voyahtune.load.sh ] && grep -qF '/data/local/bin/load.bin' /system/etc/init/voyahtune.load.sh 2>/dev/null && [ -r /system/etc/init/voyahtune.load.rc ] && grep -qF 'service voyahtune_load' /system/etc/init/voyahtune.load.rc 2>/dev/null; then echo READY; else echo BROKEN; fi" 2>/dev/null | tr -d '\r')"
    if [ "$ready" = "READY" ]; then
        tui_ok "boot-hook READY"
    else
        tui_err "boot-hook BROKEN — не перезагружайте «вслепую»; повторите install.sh из папки релиза"
        fails=$((fails + 1))
    fi
elif [ "$VERIFY_LIGHT" = 1 ]; then
    tui_ok "boot-hook отсутствует — ожидание для light-комплекта"
else
    tui_warn "voyahtune.load.sh отсутствует (возможен light — запустите с --light, или неполная установка full)"
    warns=$((warns + 1))
fi

tui_title "Итог verify"
if [ "$fails" -eq 0 ]; then
    if [ "$warns" = 0 ]; then
        tui_ok "все обязательные проверки пройдены"
    else
        tui_warn "обязательные OK, предупреждений: $warns"
    fi
    printf '%s\n' "  Ручные проверки (README / матрица): режимы до/после, рулевые клавиши, сплит."
    exit 0
fi
tui_err "провалено проверок: $fails (предупреждений: $warns)"
printf '%s\n' "  Recovery: тот же install.sh из папки релиза; лог install.log."
exit 1
