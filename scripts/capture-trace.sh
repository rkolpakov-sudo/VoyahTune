#!/usr/bin/env bash
# Захват трасс паритет-оракула (SPEC L109/L110): logcat + NativeLog +
# dumpsys window + CAN-кадры для одного сценария P-списка (Приложение A).
#
# Выход: traces/<origin>/<scenario>.{logcat,nativelog,dumpsys,cantrace}
#   origin = original | fork (L109 — оригинал 3.22.0, L110 — наша сборка).
#
# cantrace — best-effort: штатный Android candump обычно отсутствует,
# поэтому передавайте --can-cmd '<команда захвата CAN на стенде>'.
# Скрипт не падает без CAN: пишет заглушку и предупреждает.
#
# Примеры:
#   scripts/capture-trace.sh --scenario 01 --origin original
#   scripts/capture-trace.sh --scenario 05 --origin fork --serial emulator-5554 \
#       --can-cmd 'candump -t a can0' --duration 60
set -euo pipefail

die() {
    printf 'capture-trace: %s\n' "$*" >&2
    exit 2
}

warn() {
    printf 'capture-trace: ПРЕДУПРЕЖДЕНИЕ: %s\n' "$*" >&2
}

usage() {
    sed -n '2,15p' "$0"
}

SCENARIO=""
ORIGIN="original"
OUT_ROOT="traces"
SERIAL=""
DURATION=30
CAN_CMD=""

while [ $# -gt 0 ]; do
    case "$1" in
        --scenario) SCENARIO="${2-}"; shift 2 ;;
        --origin)   ORIGIN="${2-}"; shift 2 ;;
        --out)      OUT_ROOT="${2-}"; shift 2 ;;
        --serial)   SERIAL="${2-}"; shift 2 ;;
        --duration) DURATION="${2-}"; shift 2 ;;
        --can-cmd)  CAN_CMD="${2-}"; shift 2 ;;
        -h|--help)  usage; exit 0 ;;
        *)          die "неизвестный аргумент: $1" ;;
    esac
done

[ -n "$SCENARIO" ] || die "--scenario обязателен (01..12)"
case "$SCENARIO" in
    01|02|03|04|05|06|07|08|09|10|11|12) ;;
    *) die "сценарий должен быть из P-списка (01..12): $SCENARIO" ;;
esac
case "$ORIGIN" in
    original|fork) ;;
    *) die "--origin только original|fork: $ORIGIN" ;;
esac

command -v adb >/dev/null 2>&1 || die "adb не найден в PATH"

ADB=(adb)
if [ -n "$SERIAL" ]; then
    ADB+=(-s "$SERIAL")
fi
"${ADB[@]}" get-state >/dev/null 2>&1 || die "нет устройства (проверьте adb devices / --serial)"

BASE="$OUT_ROOT/$ORIGIN/$SCENARIO"
mkdir -p "$OUT_ROOT/$ORIGIN"

# 1. Полный logcat (threadtime: дата, pid, tid — parity-diff их нормализует).
"${ADB[@]}" logcat -d -v threadtime > "$BASE.logcat"
[ -s "$BASE.logcat" ] || warn "logcat пуст — устройство спит/логи не сброшены?"

# 2. NativeLog: только теги форка (TX57/58/77/6/9 — вход HIL-replay, L112).
"${ADB[@]}" logcat -d -v threadtime \
    -s '$$$ OemVehicleState $$$:V' \
       '$$$ HeadlightCanTransport $$$:V' \
       '$$$ LightSensorService $$$:V' \
       'CanBusEventHub:V' \
    > "$BASE.nativelog"

# 3. Состояние окон (геометрия окон/док — сценарии 09/10).
"${ADB[@]}" shell dumpsys window | tr -d '\r' > "$BASE.dumpsys"

# 4. CAN-кадры — best-effort (см. шапку).
if [ -n "$CAN_CMD" ]; then
    if command -v timeout >/dev/null 2>&1; then
        timeout "$DURATION" bash -c "$CAN_CMD" > "$BASE.cantrace" \
            || warn "can-cmd прерван/завершился с кодом $?"
    else
        warn "timeout не найден — --can-cmd выполняется без тайм-аута"
        bash -c "$CAN_CMD" > "$BASE.cantrace" \
            || warn "can-cmd завершился с кодом $?"
    fi
elif "${ADB[@]}" shell which candump >/dev/null 2>&1; then
    timeout "$DURATION" "${ADB[@]}" shell candump -t a any > "$BASE.cantrace" \
        || warn "candump прерван (это нормально для короткого окна)"
else
    printf '# candump не найден на устройстве; повторите с --can-cmd\n' \
        > "$BASE.cantrace"
    warn "CAN-кадры не сняты: нет candump; используйте --can-cmd '<команда стенда>'"
fi

printf 'capture-trace: сценарий %s (%s) записан:\n' "$SCENARIO" "$ORIGIN"
for ext in logcat nativelog dumpsys cantrace; do
    if [ -f "$BASE.$ext" ]; then
        printf '  %-10s %s байт\n' "$ext" "$(wc -c < "$BASE.$ext" | tr -d ' ')"
    fi
done
printf 'Дальше: Utils/parity-diff.py (L111) после снятия пары original/fork.\n'
