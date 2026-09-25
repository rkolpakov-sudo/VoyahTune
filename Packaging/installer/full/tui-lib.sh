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

# G8/D6: сверка MANIFEST.sha256 (формат sha256sum: "<hash>  <file>").
# Отсутствие манифеста — warning (старый zip / сборка без D6); mismatch/пустой файл — fail.
tui_check_manifest() {
    if [ ! -s MANIFEST.sha256 ]; then
        tui_warn "MANIFEST.sha256 отсутствует — сверка целостности пропущена (соберите релиз: make_release.sh)"
        return 0
    fi
    if command -v sha256sum >/dev/null 2>&1; then
        tui_hash_file() { sha256sum "$1" | awk '{print $1}'; }
    elif command -v shasum >/dev/null 2>&1; then
        tui_hash_file() { shasum -a 256 "$1" | awk '{print $1}'; }
    else
        tui_warn "нет sha256sum/shasum — сверка MANIFEST.sha256 пропущена"
        return 0
    fi
    bad=0
    while read -r expected name; do
        [ -z "${expected:-}" ] && continue
        if [ ! -s "$name" ]; then
            tui_err "MANIFEST: нет или пуст $name"
            bad=1
            continue
        fi
        actual="$(tui_hash_file "$name")" || {
            tui_err "MANIFEST: не удалось хешировать $name"
            bad=1
            continue
        }
        if [ "$actual" != "$expected" ]; then
            tui_err "MANIFEST: hash mismatch $name"
            bad=1
        fi
    done < MANIFEST.sha256
    if [ "$bad" = 1 ]; then
        tui_err "MANIFEST.sha256 не сошёлся. Распакуйте ZIP полностью; устройство не изменялось."
        return 1
    fi
    tui_ok "MANIFEST.sha256: файлы совпали"
    return 0
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
    tui_check_manifest || return 1
    return 0
}

# G2: только чтение adb.
# B7: если задан ADB_SERIAL — валидируем серийник и export ANDROID_SERIAL для движка
# (голый adb в install.sh наследует env и целиится в этот serial; сам движок не правим).
tui_check_device() {
    if ! tui_find_adb; then
        tui_err "adb не найден (PATH или ./adb.exe). Установите Platform Tools / распакуйте релиз."
        return 1
    fi
    tui_ok "adb: $TUI_ADB"

    adb_out="$("$TUI_ADB" devices 2>/dev/null | sed -n '2,$p' | tr -d '\r')"
    line_count=0
    state=""
    serials=""
    while IFS= read -r line; do
        [ -z "$line" ] && continue
        line_count=$((line_count + 1))
        ser=$(printf '%s' "$line" | awk '{print $1}')
        st=$(printf '%s' "$line" | awk '{print $2}')
        state="$st"
        serials="${serials}${serials:+ }${ser}"
    done <<EOF
$adb_out
EOF

    if [ -n "${ADB_SERIAL:-}" ]; then
        found=0
        ser_state=""
        while IFS= read -r line; do
            [ -z "$line" ] && continue
            ser=$(printf '%s' "$line" | awk '{print $1}')
            st=$(printf '%s' "$line" | awk '{print $2}')
            if [ "$ser" = "$ADB_SERIAL" ]; then
                found=1
                ser_state="$st"
            fi
        done <<EOF
$adb_out
EOF
        if [ "$found" != 1 ]; then
            tui_err "ADB_SERIAL=$ADB_SERIAL не найден в adb devices: ${serials:-<нет>}"
            return 1
        fi
        case "$ser_state" in
            device)
                export ANDROID_SERIAL="$ADB_SERIAL"
                tui_ok "ADB_SERIAL=$ADB_SERIAL, state=device; ANDROID_SERIAL экспортирован для движка."
                return 0
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
                tui_err "неожиданное состояние устройства: ${ser_state:-<пусто>}"
                return 1
                ;;
        esac
    fi

    if [ "$line_count" -eq 0 ]; then
        tui_err "нет устройств: проверьте кабель Type-A<->A, порт, драйвер, USB debugging."
        return 1
    fi
    if [ "$line_count" -gt 1 ]; then
        tui_err "несколько устройств ($line_count: $serials) — отключите лишние или задайте ADB_SERIAL=<serial>."
        tui_info "пример: ADB_SERIAL=<serial> ./install-tui.sh"
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

# FIX-10/R-A12: гейт батареи/места ПЕРЕД мутациями — те же пороги, что device_health_warn
# в dns-overlay.sh (батарея <20%, свободно в /data <200MB). Порог нарушен → отказ; данные
# нечитаемы → только предупреждение (не фейлим вслепую). Модель здесь намеренно НЕ блокируется
# (только warn в tui_device_profile): ожидаемая строка модели не верифицирована — аксиома 4.
tui_device_health() {
    [ -n "$TUI_ADB" ] || return 0
    tui_dh_bad=0
    tui_dh_level="$("$TUI_ADB" shell dumpsys battery 2>/dev/null | sed -n 's/^ *level: *//p' | tr -d '\r')"
    if [ -n "$tui_dh_level" ] && [ "$tui_dh_level" -eq "$tui_dh_level" ] 2>/dev/null; then
        tui_info "заряд батареи: $tui_dh_level%"
        if [ "$tui_dh_level" -lt 20 ]; then
            tui_err "батарея $tui_dh_level% < 20% — установка не запускается (зарядите ГУ)."
            tui_dh_bad=1
        fi
    else
        tui_warn "не удалось прочитать уровень батареи — проверка пропущена."
    fi
    tui_dh_data_k="$("$TUI_ADB" shell 'df -k /data 2>/dev/null' | awk '$NF=="/data"{print $4; exit}' | tr -d '\r')"
    if [ -n "$tui_dh_data_k" ] && [ "$tui_dh_data_k" -eq "$tui_dh_data_k" ] 2>/dev/null; then
        tui_dh_data_mb=$((tui_dh_data_k / 1024))
        tui_info "свободно в /data: ${tui_dh_data_mb}MB"
        if [ "$tui_dh_data_mb" -lt 200 ]; then
            tui_err "в /data всего ${tui_dh_data_mb}MB < 200MB — установка не запускается (освободите место)."
            tui_dh_bad=1
        fi
    else
        tui_warn "не удалось прочитать свободное место /data — проверка пропущена."
    fi
    if [ "$tui_dh_bad" = 1 ]; then
        return 1
    fi
    tui_ok "батарея и место в /data в норме"
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
  7. Если adb / wait-for-device «висит» дольше ~30–60 с — закройте окно
     ДО начала копирования, почините ADB и запустите тот же скрипт заново.
EOF
}

# Интерактивное меню install-tui.sh — паритет с install-tui.bat (5 пунктов).
# Мутации: только вызовы существующих движков (install/remove/dns), без новых фаз.
tui_show_menu() {
    while true; do
        if [ -t 1 ] && [ -z "${NO_COLOR:-}" ]; then
            printf '\033[2J\033[H'
        else
            clear 2>/dev/null || printf '\n'
        fi
        tui_title "Open Voyah installer TUI @VERSION@"
        printf '%s\n' "  1  Install full (install.sh + install.log)"
        printf '%s\n' "  2  Verify post-install (read-only)"
        printf '%s\n' "  3  Remove / restore (remove.sh)"
        printf '%s\n' "  4  Yandex DNS only (dns-overlay / install-yandex-dns)"
        printf '%s\n' "  5  Dry-run preflight only (no mutations)"
        printf '%s\n' "  6  Exit"
        tui_hr
        if [ "${TUI_NONINTERACTIVE}" = 1 ] || [ ! -t 0 ]; then
            tui_info "non-interactive stdin — выход (используйте --yes/--non-interactive/--dry-run)."
            return 0
        fi
        printf '%s  Select option [1-6]: %s' "${TUI_C_YELLOW}" "${TUI_C_RESET}"
        # shellcheck disable=SC2162
        read tui_m || tui_m=6
        case "$tui_m" in
            1)
                TUI_YES=0
                TUI_NONINTERACTIVE=0
                TUI_DRY_RUN_LOCAL=0
                tui_run_install_flow || true
                tui_pause
                ;;
            2)
                if [ -f ./verify_post_install.sh ]; then
                    sh ./verify_post_install.sh
                else
                    tui_err "verify_post_install.sh не найден (только full-комплект)"
                fi
                tui_pause
                ;;
            3)
                if [ ! -f ./remove.sh ]; then
                    tui_err "remove.sh не найден"
                elif tui_confirm "Запустить remove.sh (restore из ./backup в ЭТОЙ папке)?"; then
                    sh ./remove.sh
                fi
                tui_pause
                ;;
            4)
                if [ -f ./install-yandex-dns.sh ]; then
                    if tui_confirm "Запустить установку Yandex DNS-overlay (install-yandex-dns.sh)?"; then
                        sh ./install-yandex-dns.sh
                    fi
                elif [ -f ./dns-overlay.sh ]; then
                    # FIX-8/R-A10: раньше здесь выполнялся `sh ./dns-overlay.sh` — это no-op
                    # (файл содержит только функции, при прямом запуске он ничего не делает и
                    # молча «успевает»). Теперь source + тот же query/choose/apply, что в install.sh
                    # (без финального reboot — TUI-пункт управляет DNS независимо от установки).
                    if tui_confirm "Установить/отключить Yandex DNS-overlay (функции dns-overlay.sh)?"; then
                        if . ./dns-overlay.sh; then
                            tui_dns_rc=0
                            tui_dns_state="$(ydns_query_state 2>/dev/null)" || tui_dns_rc=1
                            tui_dns_state="$(printf '%s' "$tui_dns_state" | tr -d '\r')"
                            if [ "$tui_dns_rc" = 0 ]; then
                                case "$tui_dns_state" in
                                    on|off|external|broken)
                                        tui_info "состояние DNS-overlay: $tui_dns_state"
                                        ;;
                                    *)
                                        tui_err "DNS-overlay helper вернул неизвестное состояние: $tui_dns_state"
                                        tui_dns_rc=1
                                        ;;
                                esac
                            else
                                tui_err "не удалось определить состояние DNS-overlay (adb/подключение?)"
                            fi
                            if [ "$tui_dns_rc" = 0 ]; then
                                YDNS_REQUEST=keep
                                if choose_yandex_dns "$tui_dns_state"; then
                                    case "${YDNS_REQUEST:-keep}" in
                                        on)
                                            if install_yandex_dns; then
                                                tui_ok "DNS-overlay установлен"
                                            else
                                                tui_err "установка DNS-overlay завершилась ошибкой"
                                            fi
                                            ;;
                                        off)
                                            if disable_yandex_dns; then
                                                tui_ok "DNS-overlay отключён"
                                            else
                                                tui_err "отключение DNS-overlay завершилось ошибкой"
                                            fi
                                            ;;
                                        keep)
                                            tui_info "DNS-overlay: состояние без изменений."
                                            ;;
                                        *)
                                            tui_err "неизвестный выбор DNS-overlay: ${YDNS_REQUEST:-<пусто>}"
                                            ;;
                                    esac
                                else
                                    tui_err "не удалось получить выбор DNS-overlay"
                                fi
                            fi
                        else
                            tui_err "не удалось загрузить ./dns-overlay.sh"
                        fi
                    fi
                else
                    tui_err "DNS-скрипт не найден рядом с TUI"
                fi
                tui_pause
                ;;
            5)
                # dry-run: только preflight, без мутаций
                tui_title "Dry-run preflight"
                bundle_rc=0
                tui_check_bundle || bundle_rc=1
                dev_rc=0
                if [ "$bundle_rc" = 0 ]; then
                    if tui_find_adb; then
                        tui_check_device || dev_rc=1
                        [ "$dev_rc" = 0 ] && tui_device_profile || true
                        # FIX-10: dry-run обязан отработать как полный preflight — батарея/место.
                        if [ "$dev_rc" = 0 ]; then
                            tui_device_health || dev_rc=1
                        fi
                    else
                        tui_warn "adb не найден — device check пропущен (bundle OK)"
                    fi
                fi
                tui_show_plan
                if [ "$bundle_rc" = 0 ]; then
                    tui_ok "dry-run: preflight пройден; движок не запускался."
                else
                    tui_err "dry-run: комплект неполон."
                fi
                tui_pause
                ;;
            6|q|Q|"")
                return 0
                ;;
            *)
                tui_warn "неизвестный пункт: $tui_m"
                sleep 1
                ;;
        esac
    done
}

# Одиночный проход установки (используется меню и прямым запуском без меню).
tui_run_install_flow() {
    tui_title "Open Voyah — установка (TUI) @VERSION@"
    tui_info "движок: install.sh · мутации выполняет только install.sh"
    tui_show_safety
    if ! tui_confirm "Подтверждаю пункты безопасности выше — продолжить?"; then
        tui_warn "Отменено пользователем. Устройство не изменялось."
        return 2
    fi
    tui_title "Preflight (только чтение)"
    bundle_rc=0
    tui_check_bundle || bundle_rc=1
    dev_rc=0
    if [ "$bundle_rc" = 0 ]; then
        tui_check_device || dev_rc=1
        [ "$dev_rc" = 0 ] && tui_device_profile || true
        # FIX-10: гейт до мутаций — батарея <20% / мало места в /data → стоп до запуска install.sh.
        if [ "$dev_rc" = 0 ]; then
            tui_device_health || dev_rc=1
        fi
    fi
    if [ "$bundle_rc" != 0 ] || [ "$dev_rc" != 0 ]; then
        tui_title "Стоп до мутаций"
        tui_err "Preflight не пройден — install.sh НЕ запускался, /system не изменялась."
        printf '%s\n' "  → См. README.txt «ПРОВЕРКА ADB» / «PREFLIGHT» / «ТИПОВЫЕ ОШИБКИ»."
        printf '%s\n' "  → Если wait-for-device «висит» >30–60 с: закройте окно, почините ADB, повторите."
        return 3
    fi
    tui_show_plan
    if ! tui_confirm "Начать установку (мутации устройства + backup в ./backup)?"; then
        tui_warn "Отменено. Устройство не изменялось."
        return 2
    fi
    tui_title "Запуск install.sh (лог: install.log)"
    tui_run_engine "install.sh"
    map_rc=0
    tui_map_exit "${tui_engine_rc:-1}" "install.log" || map_rc=1
    if [ "${tui_engine_rc:-1}" = 0 ]; then
        if [ -f ./verify_post_install.sh ]; then
            if tui_confirm "Запустить verify_post_install (read-only, без изменений)?"; then
                sh ./verify_post_install.sh || true
            fi
        fi
    fi
    return "$map_rc"
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
    # Ротация: предыдущий лог не затираем молча (A6 дизайна).
    if [ -f "$TUI_LOG_FILE" ]; then
        mv -f "$TUI_LOG_FILE" "${TUI_LOG_FILE}.1" 2>/dev/null || true
    fi
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
    canbus=0
    sysro=0
    if [ -f "$log" ]; then
        if grep -E 'Не перезагружайте|Do not reboot|do not reboot' "$log" >/dev/null 2>&1; then
            ban=1
        fi
        if grep -E 'WRITE_CANBUS уже принадлежит|WRITE_CANBUS не определён' "$log" >/dev/null 2>&1; then
            canbus=1
        fi
        if grep -E 'EROFS|заблокирован загрузчик|disable-verity не срабатывает' "$log" >/dev/null 2>&1; then
            sysro=1
        fi
        tui_info "лог: $log (приложите к отчёту, строки с !!!)"
    fi
    if [ "$ban" = 1 ]; then
        tui_err "НЕ ПЕРЕЗАГРЫВАЙТЕ ГУ, пока ADB не восстановлен и повторный запуск не завершился успешно."
        tui_info "восстановите соединение → запустите install-tui.sh (или install.sh) заново из ЭТОЙ же папки."
    elif [ "$canbus" = 1 ]; then
        tui_warn "Причина: владелец WRITE_CANBUS чужой/не определён — /system не изменялся."
        tui_info "удалите несовместимый пакет владельца → повторите install.sh (README «ТИПОВЫЕ ОШИБКИ»)."
    elif [ "$sysro" = 1 ]; then
        tui_warn "Причина: /system недоступна (EROFS / загрузчик / verity) — стоп."
        tui_info "см. README:81–84 → после устранения причины повторите install.sh из этой папки."
    else
        tui_warn "Обычный recovery: исправьте причину (README.txt) → повторите ТОТ ЖЕ install.sh из этой папки."
        tui_info "операции рассчитаны на повторный запуск; не удаляйте ./backup."
    fi
    return 1
}
