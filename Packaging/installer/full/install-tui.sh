#!/bin/sh
# install-tui.sh — интерактивная обёртка над full/install.sh (D2).
# НЕ меняет фазы/порядок движка: только preflight UX, confirm, tee-лог, карта exit-кода, verify.
# Запуск из плоской папки релиза: ./install-tui.sh [--yes] [--non-interactive] [--dry-run]
# Non-interactive без --dry-run ≈ прямой запуск install.sh после быстрого preflight.

set -u

TUI_SELF="$0"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR" || exit 1

# shellcheck source=tui-lib.sh
. "$SCRIPT_DIR/tui-lib.sh" || {
    echo "!!! Не найден tui-lib.sh рядом с install-tui.sh — обёртка не запущена."
    exit 1
}

TUI_DRY_RUN=0
for arg in "$@"; do
    case "$arg" in
        --yes|-y)              TUI_YES=1 ;;
        --non-interactive)     TUI_NONINTERACTIVE=1 ;;
        --dry-run)             TUI_DRY_RUN=1 ;;
        -h|--help)
            sed -n '2,6p' "$SCRIPT_DIR/$(basename "$TUI_SELF")"
            exit 0
            ;;
        *)
            echo "Неизвестный флаг: $arg (см. --help)" >&2
            exit 1
            ;;
    esac
done

tui_init

tui_title "Open Voyah — установка (TUI) @VERSION@"
tui_info "движок: install.sh · мутации выполняет только install.sh"

tui_show_safety
if [ "$TUI_DRY_RUN" != 1 ] && [ "${TUI_NONINTERACTIVE}" != 1 ]; then
    if ! tui_confirm "Подтверждаю пункты безопасности выше — продолжить?"; then
        tui_warn "Отменено пользователем. Устройство не изменялось."
        exit 2
    fi
fi

tui_title "Preflight (только чтение)"
bundle_rc=0
tui_check_bundle || bundle_rc=1
dev_rc=0
if [ "$bundle_rc" = 0 ]; then
    if [ "$TUI_DRY_RUN" = 1 ]; then
        if tui_find_adb; then
            tui_check_device || dev_rc=1
            if [ "$dev_rc" = 0 ]; then
                tui_device_profile || true
            fi
        else
            tui_warn "adb не найден — device check в dry-run пропущен (bundle OK)"
        fi
    else
        tui_check_device || dev_rc=1
        if [ "$dev_rc" = 0 ]; then
            tui_device_profile || true
        fi
    fi
fi

if [ "$bundle_rc" != 0 ]; then
    tui_title "Стоп до мутаций"
    tui_err "Preflight не пройден — install.sh НЕ запускался, /system не изменялась."
    printf '%s\n' "  → Распакуйте ZIP заново полностью (README.txt)."
    exit 3
fi

if [ "$TUI_DRY_RUN" != 1 ] && [ "$dev_rc" != 0 ]; then
    tui_title "Стоп до мутаций"
    tui_err "Preflight не пройден — install.sh НЕ запускался, /system не изменялась."
    printf '%s\n' "  → См. README.txt «ПРОВЕРКА ADB» / «ТИПОВЫЕ ОШИБКИ УСТАНОВКИ»."
    exit 3
fi

if [ "$TUI_DRY_RUN" = 1 ]; then
    tui_show_plan
    tui_ok "dry-run: preflight пройден; движок не запускался."
    exit 0
fi

tui_show_plan
if [ "${TUI_NONINTERACTIVE}" != 1 ]; then
    if ! tui_confirm "Начать установку (мутации устройства + backup в ./backup)?"; then
        tui_warn "Отменено. Устройство не изменялось."
        exit 2
    fi
fi

tui_title "Запуск install.sh (лог: install.log)"
tui_run_engine "install.sh"
map_rc=0
tui_map_exit "${tui_engine_rc:-1}" "install.log" || map_rc=1

if [ "${tui_engine_rc:-1}" = 0 ] && [ "${TUI_NONINTERACTIVE}" != 1 ]; then
    if [ -f "$SCRIPT_DIR/verify_post_install.sh" ]; then
        if tui_confirm "Запустить verify_post_install (read-only, без изменений)?"; then
            sh "$SCRIPT_DIR/verify_post_install.sh"
            vrc=$?
            if [ "$vrc" = 0 ]; then
                tui_ok "verify: все обязательные проверки пройдены"
            else
                tui_warn "verify: есть предупреждения/ошибки — см. вывод выше"
            fi
        fi
    fi
    tui_info "Yandex DNS: меню уже отрабатывал внутри install.sh; отдельно — install-yandex-dns."
fi

exit "$map_rc"
