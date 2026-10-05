#!/usr/bin/env bash
# Пульт кампании «подключение → прошивка → тесты» (WP2 L109-L116 + WP7 фаза 1).
# ТОЛЬКО read-only проверки ПК/ГУ: изменения авто выполняет установщик по
# карточкам on-car-campaign.md (приватный мастер-док владельца).
#
# Подкоманды:
#   env          ПК-гейт: adb/node/python, SHA-256 релиза, инсталлер, эталоны E9/E10
#   device       ГУ-гейт: единственное устройство, батарея, место, пакеты,
#                WRITE_CANBUS, маркер канала CAN -> art/campaign/<ts>/preflight-device.txt
#   audit TAG    Снимок состояния ГУ (до/после) -> art/campaign/<ts>/audit-TAG/
#   shot         Скриншот экрана ГУ -> art/campaign/screens/screen-NNN.png
#   postinstall  После установки форка: grant-gate + интеграционные чеки
#
# Коды выхода: 0 — PASS (можно продолжать по карточке); 1 — FAIL (СТОП кампании,
# нужен агент); 2 — окружение/аргументы.
#
# Примеры:
#   scripts/campaign.sh env
#   scripts/campaign.sh device
#   scripts/campaign.sh audit baseline
#   scripts/campaign.sh shot
#   scripts/campaign.sh postinstall
set -euo pipefail

PKG="ru.big.town.anative"

die() {
    printf 'campaign: %s\n' "$*" >&2
    exit 2
}

usage() {
    sed -n '2,22p' "$0"
}

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
STAMP="$(date +%Y%m%d-%H%M%S)"
OUT="$ROOT/art/campaign/$STAMP"
FAILS=0

# WSL не видит Windows-инструменты — резолвим по известным путям хоста.
ADB="$(command -v adb 2>/dev/null || true)"
if [ -z "$ADB" ]; then
    ADB=$(ls /mnt/c/Users/*/AppData/Local/Android/Sdk/platform-tools/adb.exe \
        2>/dev/null | head -1 || true)
fi
NODE_BIN="$(command -v node 2>/dev/null || true)"
if [ -z "$NODE_BIN" ]; then
    NODE_BIN=$(ls "/mnt/c/Program Files/nodejs/node.exe" 2>/dev/null || true)
fi

pass() { printf 'campaign: [PASS] %s\n' "$*"; }
fail() {
    printf 'campaign: [FAIL] %s\n' "$*" >&2
    FAILS=$((FAILS + 1))
}
note() { printf 'campaign: [ .. ] %s\n' "$*"; }

finish() {
    if [ "$FAILS" -gt 0 ]; then
        printf 'campaign: ИТОГ FAIL (%d) — СТОП кампании, не продолжать без агента\n' \
            "$FAILS" >&2
        exit 1
    fi
    printf 'campaign: ИТОГ PASS — можно переходить к следующей карточке\n'
    exit 0
}

# --- env: подготовка ПК (без устройства) -------------------------------------
cmd_env() {
    [ -n "$ADB" ] && pass "adb: $ADB" || fail "adb не найден (PATH и Android SDK)"
    [ -n "$NODE_BIN" ] && pass "node: $NODE_BIN (сборка инсталлера)" \
        || fail "node не найден (PATH и Program Files/nodejs)"
    if command -v python >/dev/null 2>&1 || command -v python3 >/dev/null 2>&1; then
        pass "python в PATH"
    else
        fail "python/python3 не в PATH (manifest payload)"
    fi

    local dist="$ROOT/Releases/dist"
    if [ -f "$dist/SHA256SUMS" ]; then
        if (cd "$dist" && sha256sum -c SHA256SUMS >/dev/null 2>&1); then
            pass "SHA256SUMS: все артефакты релиза сверены"
        else
            fail "SHA256SUMS: сверка провалена (релиз повреждён или не дособран)"
        fi
    else
        fail "нет Releases/dist/SHA256SUMS (сборка: ./make_release.sh <V> --payload)"
    fi
    local nzip
    nzip=$(ls "$dist"/payload_*.zip 2>/dev/null | wc -l | tr -d ' ')
    if [ "$nzip" -ge 1 ]; then
        pass "payload ZIP присутствует: $nzip шт."
    else
        fail "нет Releases/dist/payload_*.zip"
    fi

    local exe
    exe=$(ls "$ROOT"/Releases/dist/VoyahTune-Installer-*/*[Ii]nstaller*.exe \
        "$ROOT"/Releases/build/installers-*/*.exe \
        "$ROOT"/Installer/target/release/bundle/nsis/*.exe 2>/dev/null | head -1 || true)
    if [ -n "$exe" ]; then
        pass "GUI-инсталлер собран: ${exe#"$ROOT"/}"
    else
        fail "нет VoyahTune-Installer.exe (сборка: node Installer/scripts/build.mjs --payload Releases/build/payload-4.0.0-build.2)"
    fi

    [ -f "$ROOT/payload/manifest.json" ] && pass "payload/manifest.json" \
        || fail "нет payload/manifest.json"
    [ -d "$ROOT/artifacts/3.22.0" ] && pass "эталон оригинала artifacts/3.22.0 (E9)" \
        || fail "нет artifacts/3.22.0 (сверка оригинала E9)"
    [ -f "$ROOT/Docs/original-fingerprint.md" ] && pass "Docs/original-fingerprint.md (E9/E10)" \
        || fail "нет Docs/original-fingerprint.md"

    if [ -n "$(git -C "$ROOT" status --porcelain 2>/dev/null | head -1)" ]; then
        note "рабочее дерево грязное (не блокирует, но фиксируй версию кода)"
    else
        pass "рабочее дерево чистое"
    fi
    note "резервный комплект владельца (SPEC Приложение E) и OBD-прибор DTC — вручную"
    finish
}

# --- device: подключение ГУ (read-only) --------------------------------------
cmd_device() {
    [ -n "$ADB" ] || die "adb не найден (PATH и Android SDK)"
    mkdir -p "$OUT"
    local report="$OUT/preflight-device.txt"
    : > "$report"

    local state
    state=$("$ADB" get-state 2>&1) || die "нет устройства (adb get-state: $state)"
    [ "$state" = "device" ] || fail "состояние устройства: $state (нужен authorized/device)"

    local count
    count=$("$ADB" devices | awk 'NR>1 && $2=="device"' | wc -l | tr -d ' ')
    if [ "$count" -eq 1 ]; then
        pass "единственное подключённое устройство"
    else
        fail "подключено устройств: $count (GUI требует подтвердить единственное)"
    fi

    {
        echo "== campaign preflight device: $STAMP =="
        echo "-- getprop --"
        "$ADB" shell getprop ro.product.model | tr -d '\r'
        "$ADB" shell getprop ro.build.version.release | tr -d '\r'
        "$ADB" shell getprop ro.build.fingerprint | tr -d '\r'
        echo "-- battery --"
        "$ADB" shell dumpsys battery | tr -d '\r'
        echo "-- storage /data --"
        "$ADB" shell df -k /data | tr -d '\r'
        echo "-- packages --"
        "$ADB" shell pm list packages -f | tr -d '\r' | grep -Ei 'anative|voyah|restoremode' || true
        echo "-- package $PKG --"
        "$ADB" shell dumpsys package "$PKG" | tr -d '\r' | grep -E 'versionName|firstInstallTime|WRITE_CANBUS' || true
        echo "-- canbus marker (logcat -t 500) --"
        "$ADB" logcat -d -t 500 2>/dev/null | tr -d '\r' | grep -E 'CanBus callback registered|WRITE_CANBUS permission missing' | tail -5 || true
    } >> "$report" 2>&1

    local level
    level=$("$ADB" shell dumpsys battery | tr -d '\r' | awk '/level:/ {print $2; exit}')
    if [ -n "${level:-}" ] && [ "$level" -ge 30 ] 2>/dev/null; then
        pass "батарея ГУ: ${level}%"
    else
        fail "батарея ГУ: ${level:-?}% (<30% или не прочитана) — не начинать установку"
    fi

    if grep -q "WRITE_CANBUS permission missing" "$report"; then
        fail "в логе есть WRITE_CANBUS permission missing (см. grant-gate)"
    else
        pass "нет маркера WRITE_CANBUS permission missing"
    fi

    note "отчёт: ${report#"$ROOT"/}"
    finish
}

# --- audit: снимок состояния ГУ ----------------------------------------------
cmd_audit() {
    [ $# -ge 1 ] || die "audit требует TAG (например: baseline, after-install)"
    local tag="$1"
    case "$tag" in
        *[!A-Za-z0-9_-]*) die "TAG: только [A-Za-z0-9_-]: $tag" ;;
    esac
    [ -n "$ADB" ] || die "adb не найден (PATH и Android SDK)"
    "$ADB" get-state >/dev/null 2>&1 || die "нет устройства"

    local dir="$OUT/audit-$tag"
    mkdir -p "$dir"
    "$ADB" shell getprop > "$dir/props.txt" 2>&1 || true
    "$ADB" shell pm list packages -f > "$dir/packages.txt" 2>&1 || true
    "$ADB" shell service list > "$dir/services.txt" 2>&1 || true
    "$ADB" shell ps -A > "$dir/ps.txt" 2>&1 || true
    "$ADB" shell ip addr > "$dir/ip_addr.txt" 2>&1 || true
    "$ADB" shell ip route > "$dir/ip_route.txt" 2>&1 || true
    "$ADB" shell dumpsys battery > "$dir/battery.txt" 2>&1 || true
    "$ADB" shell df -k > "$dir/df.txt" 2>&1 || true
    "$ADB" logcat -d -t 500 > "$dir/logcat_tail.txt" 2>&1 || true
    pass "снимок audit-$tag: ${dir#"$ROOT"/} (9 файлов, read-only)"
    if [ "$tag" = "baseline" ]; then
        note "DTC-baseline — снять OBD-прибором владельца до изменений и вложить сюда"
    fi
    finish
}

# --- postinstall: интеграционные чеки после установки форка -------------------
cmd_postinstall() {
    [ -n "$ADB" ] || die "adb не найден (PATH и Android SDK)"
    mkdir -p "$OUT"
    local report="$OUT/postinstall.txt"
    : > "$report"

    local path
    path=$("$ADB" shell pm path --user 0 "$PKG" 2>/dev/null | tr -d '\r' | head -1 || true)
    if [ -n "$path" ]; then
        pass "пакет $PKG установлен: $path"
    else
        fail "пакет $PKG не найден (pm path пуст) — установка не применилась"
    fi

    local ver
    ver=$("$ADB" shell dumpsys package "$PKG" 2>/dev/null | tr -d '\r' | awk -F= '/versionName=/ {print $2; exit}')
    note "versionName=${ver:-?} — сверить с Releases/dist/payload_*.json (version) вручную"
    echo "versionName=${ver:-}" >> "$report"

    if bash "$ROOT/scripts/grant-gate.sh" >> "$report" 2>&1; then
        pass "grant-gate (SPEC L113) пройден"
    else
        fail "grant-gate провален (см. ${report#"$ROOT"/})"
    fi
    finish
}

# --- shot: скриншот экрана ГУ (хронологическая лента) ------------------------
cmd_shot() {
    [ -n "$ADB" ] || die "adb не найден (PATH и Android SDK)"
    "$ADB" get-state >/dev/null 2>&1 || die "нет устройства"
    local dir="$ROOT/art/campaign/screens"
    mkdir -p "$dir"
    local n file
    n=$(ls "$dir"/screen-*.png 2>/dev/null \
        | sed -E 's/.*screen-0*([0-9]+)\.png/\1/' | sort -n | tail -1)
    n=$((${n:-0} + 1))
    file="$dir/screen-$(printf '%03d' "$n").png"
    if "$ADB" exec-out screencap -p > "$file" 2>/dev/null && [ -s "$file" ]; then
        pass "скриншот ГУ: ${file#"$ROOT"/}"
    else
        rm -f "$file"
        fail "screencap не дал изображения (экран выключен? попробуйте ещё раз)"
    fi
    finish
}

# --- dispatcher ---------------------------------------------------------------
[ $# -ge 1 ] || { usage; exit 2; }
cmd="$1"
shift
case "$cmd" in
    env)         [ $# -eq 0 ] || die "env не принимает аргументов"; cmd_env ;;
    device)      [ $# -eq 0 ] || die "device не принимает аргументов"; cmd_device ;;
    audit)       cmd_audit "$@" ;;
    shot)        [ $# -eq 0 ] || die "shot не принимает аргументов"; cmd_shot ;;
    postinstall) [ $# -eq 0 ] || die "postinstall не принимает аргументов"; cmd_postinstall ;;
    -h|--help)   usage; exit 0 ;;
    *)           die "неизвестная подкоманда: $cmd (env|device|audit|postinstall)" ;;
esac
