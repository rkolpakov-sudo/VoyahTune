# tui-lib.sh — общий POSIX-слой UX для install-tui.sh / verify_post_install.sh.
# Только чтение и вывод; мутации устройства — exclusively в install.sh / install.bat.
# Источник: сibling в плоской папке релиза (сборка: make_release.sh → full/*).

TUI_RC_FILE=""
TUI_LOG_FILE=""
TUI_ADB=""

tui_init() {
    if [ -t 1 ] && [ -z "${NO_COLOR:-}" ]; then
        TUI_C_RESET="$(printf '\033[0m')"
        TUI_C_BOLD="$(printf '\033[1m')"
        TUI_C_RED="$(printf '\033[31m')"
        TUI_C_GREEN="$(printf '\033[32m')"
        TUI_C_YELLOW="$(printf '\033[33m')"
        TUI_C_BLUE="$(printf '\033[34m')"
        TUI_C_CYAN="$(printf '\033[36m')"
    else
        TUI_C_RESET=""
        TUI_C_BOLD=""
        TUI_C_RED=""
        TUI_C_GREEN=""
        TUI_C_YELLOW=""
        TUI_C_BLUE=""
        TUI_C_CYAN=""
    fi
    TUI_YES="${TUI_YES:-0}"
    TUI_NONINTERACTIVE="${TUI_NONINTERACTIVE:-0}"
}

tui_hr() {
    printf '%s\n' "------------------------------------------------------------"
}

tui_title() {
    printf '\n%s%s%s\n' "${TUI_C_BOLD}" "$1" "${TUI_C_RESET}"
    tui_hr
}

tui_info() { printf '%s  %s%s\n' "${TUI_C_CYAN}" "$*" "${TUI_C_RESET}"; }
tui_ok()   { printf '%s  [OK]%s %s\n' "${TUI_C_GREEN}" "${TUI_C_RESET}" "$*"; }
tui_warn() { printf '%s [! ]%s %s\n' "${TUI_C_YELLOW}" "${TUI_C_RESET}" "$*"; }
tui_err()  { printf '%s [!!!]%s %s\n' "${TUI_C_RED}" "${TUI_C_RESET}" "$*"; }

tui_pause() {
    [ "${TUI_NONINTERACTIVE}" = 1 ] && return 0
    printf '%s  Enter — продолжить...%s' "${TUI_C_DIM:-}" "${TUI_C_RESET:-}"
    # shellcheck disable=SC2162
    read _tui_dummy || true
    printf '\n'
}

# G0/G3: confirm; --yes / --non-interactive => yes без опроса.
tui_confirm() {
    prompt="$1"
    if [ "${TUI_YES}" = 1 ] || [ "${TUI_NONINTERACTIVE}" = 1 ]; then
        tui_info "auto-yes: $prompt"
        return 0
    fi
    printf '%s  %s [y/N]: %s' "${TUI_C_YELLOW}" "$prompt" "${TUI_C_RESET}"
    # shellcheck disable=SC2162
    read tui_ans || tui_ans=""
    case "$tui_ans" in
        y|Y|yes|YES|Yes) return 0 ;;
        *) return 1 ;;
    esac
}

tui_find_adb() {
    if command -v adb >/dev/null 2>&1; then
        TUI_ADB=adb
        return 0
    fi
    if command -v adb.exe >/dev/null 2>&1; then
        TUI_ADB=adb.exe
        return 0
    fi
    if [ -x ./adb.exe ]; then
        TUI_ADB=./adb.exe
        return 0
    fi
    if [ -x ./adb ]; then
        TUI_ADB=./adb
        return 0
    fi
    return 1
}

# G1: обязательный набор full-install (минимальный preflight до device).
tui_check_bundle() {
    missing=0
    for f in dns-overlay.sh install.sh install.bat remove.sh \
            native.apk restore_mode.apk \
            privapp-permissions-ru.big.town.anative.xml \
            load.bin steeringwheelkeys.js launcherdock.js multidisplay.js \
            vd_bypass.js apollo_tech.js frida-inject-16.2.1-android-arm64 \
            voyahtune.load.rc voyahtune.load.sh init.logcat.original.sh; do
        if [ ! -s "$f" ]; then
            tui_err "нет или пуст: $f"
            missing=1
        fi
    done
    if [ "$missing" = 1 ]; then
        tui_err "Комплект неполон. Распакуйте ZIP полностью; устройство не изменялось."
        return 1
    fi
    tui_ok "файлы полного комплекта на месте"
    return 0
}

# G2: только чтение adb.
tui_check_device() {
    if ! tui_find_adb; then
        tui_err "adb не найден (PATH или ./adb.exe). Установите Platform Tools / распакуйте релиз."
        return 1
    fi
    tui_ok "adb: $TUI_ADB"

    adb_out="$("$TUI_ADB" devices 2>/dev/null | sed -n '2,$p' | tr -d '\r')"
    line_count=0
    state=""
    while IFS= read -r line; do
        [ -z "$line" ] && continue
        line_count=$((line_count + 1))
        state=$(printf '%s' "$line" | awk '{print $2}')
    done <<EOF
$adb_out
EOF

    if [ "$line_count" -eq 0 ]; then
        tui_err "нет устройств: проверьте кабель Type-A<->A, порт, драйвер, USB debugging."
        return 1
    fi
    if [ "$line_count" -gt 1 ]; then
        tui_err "несколько устройств ($line_count) — отключите лишние Android/эмуляторы."
        return 1
    fi
    case "$state" in
        device)
            tui_ok "одно устройство, state=device"
            ;;
        unauthorized)
            tui_err "unauthorized — разблокируйте ГУ и подтвердите RSA-ключ USB debugging."
            return 1
            ;;
        offline)
            tui_err "offline — переподключите кабель; adb kill-server && adb start-server."
            return 1
            ;;
        *)
            tui_err "неожиданное состояние устройства: ${state:-<пусто>}"
            return 1
            ;;
    esac
    return 0
}

# Read-only профиль устройства (не блокирует — только предупреждения).
tui_device_profile() {
    [ -n "$TUI_ADB" ] || return 0
    sdk="$("$TUI_ADB" shell getprop ro.build.version.sdk 2>/dev/null | tr -d '\r')"
    model="$("$TUI_ADB" shell getprop ro.product.model 2>/dev/null | tr -d '\r')"
    fp="$("$TUI_ADB" shell getprop ro.build.fingerprint 2>/dev/null | tr -d '\r')"
    [ -n "$model" ] && tui_info "модель: $model"
    [ -n "$sdk" ] && tui_info "Android SDK: $sdk"
    if [ -n "$sdk" ] && [ "$sdk" -lt 33 ] 2>/dev/null; then
        tui_warn "SDK $sdk < 33: риск R20 (RestoreMode registerReceiver) — подтвердите на своей машине."
    fi
    if printf '%s' "$fp" | grep -qi 'sport'; then
        tui_ok "отпечаток похож на линейку Sport (сверьте с матрицей совместимости)"
    elif [ -n "$fp" ]; then
        tui_warn "прошивка не опознана как Sport+ 2026 — статус «не проверено» (docs/audit)."
    fi
    return 0
}

tui_show_safety() {
    tui_title "ВАЖНО — безопасность"
    cat <<'EOF'
  1. Автомобиль полностью остановлен: селектор P, стояночный тормоз.
  2. Не отключайте питание и USB во время установки и перезагрузок.
  3. Только кабель USB Type-A <-> Type-A (Type-C не подходит).
  4. Распакуйте ZIP полностью; не запускайте из архиватора.
  5. Будет 1–2 перезагрузки головного устройства.
  6. Лог: install.log в этой папке (создаётся при запуске через TUI).
EOF
}

tui_show_plan() {
    tui_title "План установки full (движок install.sh не изменяется TUI)"
    cat <<'EOF'
  [1] Локальный preflight файлов (без ADB)
  [2] adb root + wait-for-device
  [3] Preflight Apollo safe-keys (=0)
  [4] Preflight владелец WRITE_CANBUS
  [5] disable-verity → при необходимости ОДИН reboot → remount /system
  [6] Бэкап системных файлов в ./backup
  [7] Frida: steering wheel + VirtualDisplay + Apollo diagnostic
  [8] Boot-hook: voyahtune.*.rc (+ миграция legacy init.logcat.sh)
  [9] Native.apk + privapp + leavecar + freeform + RestoreMode
  [10] Меню Yandex DNS (штатный выбор install.sh)
  [11] Финальная перезагрузка ГУ
  [12] verify_post_install (read-only, по желанию)
EOF
    if [ -d backup ]; then
        tui_warn "./backup уже есть — старые бэкапы сохраняются как есть (повторный запуск идемпотентен)."
    else
        tui_info "backup будет создан в ./backup при первом мутационном шаге"
    fi
}

# Запуск движка с tee в install.log и корректным exit code (POSIX, без pipefail).
tui_run_engine() {
    engine="$1"
    if [ ! -f "$engine" ]; then
        tui_err "нет движка $engine"
        return 1
    fi
    TUI_LOG_FILE="install.log"
    TUI_RC_FILE="install.log.tui-rc"
    rm -f "$TUI_RC_FILE"
    {
        if [ "$engine" = "./install.sh" ] || [ "$engine" = "install.sh" ]; then
            sh "./install.sh"
        else
            sh "./$engine"
        fi
        printf '%s\n' "$?" > "$TUI_RC_FILE"
    } 2>&1 | tee "$TUI_LOG_FILE"
    if [ -f "$TUI_RC_FILE" ]; then
        tui_engine_rc="$(cat "$TUI_RC_FILE")"
        rm -f "$TUI_RC_FILE"
    else
        tui_engine_rc=1
    fi
    # shellcheck disable=SC2086
    return 0
}

# G5: карта результата движка → следующий шаг пользователя.
tui_map_exit() {
    rc="$1"
    log="${2:-install.log}"
    tui_title "Итог"
    if [ "$rc" = 0 ]; then
        tui_ok "install.sh завершился успешно (exit 0)."
        printf '%s\n' "  Убедитесь, что ГУ перезагрузилось (или перезагрузите вручную)."
        printf '%s\n' "  Не открывайте приложения Open Voyah до перезагрузки."
        return 0
    fi

    tui_err "install.sh завершился с кодом $rc."
    ban=0
    if [ -f "$log" ]; then
        if grep -E 'Не перезагружайте|Do not reboot|do not reboot' "$log" >/dev/null 2>&1; then
            ban=1
        fi
        tui_info "лог: $log (приложите к отчёту, строки с !!!)"
    fi
    if [ "$ban" = 1 ]; then
        tui_err "НЕ ПЕРЕЗАГРЫВАЙТЕ ГУ, пока ADB не восстановлен и повторный запуск не завершился успешно."
        tui_info "восстановите соединение → запустите install-tui.sh (или install.sh) заново из ЭТОЙ же папки."
    else
        tui_warn "Обычный recovery: исправьте причину (README.txt) → повторите ТОТ ЖЕ install.sh из этой папки."
        tui_info "операции рассчитаны на повторный запуск; не удаляйте ./backup."
    fi
    return 1
}
