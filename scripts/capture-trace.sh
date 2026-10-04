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
# Перезапись уже снятых файлов трассы — только с --force.
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
    sed -n '2,16p' "$0"
}

# Проверка, что у опции есть значение (иначе shift 2 под set -e убивает
# скрипт без сообщения, а пустое значение тихо меняет поведение).
need_value() {
    [ "$2" -ge 2 ] || die "$1 требует значение"
}

# Запуск команды с ограничением по $DURATION секунд: timeout, если есть,
# иначе фоновый запуск + kill (иначе candump/adb зависли бы навсегда).
run_timed() {
    if command -v timeout >/dev/null 2>&1; then
        timeout "$DURATION" "$@"
        return $?
    fi
    warn "timeout не найден — ограничиваю длительность вручную"
    "$@" &
    local pid=$!
    local waited=0
    while [ "$waited" -lt "$DURATION" ]; do
        kill -0 "$pid" 2>/dev/null || break
        sleep 1
        waited=$((waited + 1))
    done
    kill "$pid" 2>/dev/null || true
    wait "$pid" 2>/dev/null || true
}

SCENARIO=""
ORIGIN="original"
OUT_ROOT="traces"
SERIAL=""
DURATION=30
CAN_CMD=""
FORCE=0

while [ $# -gt 0 ]; do
    case "$1" in
        --scenario) need_value "$1" $#; SCENARIO="$2"; shift 2 ;;
        --origin)   need_value "$1" $#; ORIGIN="$2"; shift 2 ;;
        --out)      need_value "$1" $#; OUT_ROOT="$2"; shift 2 ;;
        --serial)   need_value "$1" $#; SERIAL="$2"; shift 2 ;;
        --duration) need_value "$1" $#; DURATION="$2"; shift 2 ;;
        --can-cmd)  need_value "$1" $#; CAN_CMD="$2"; shift 2 ;;
        --force)    FORCE=1; shift ;;
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
case "$DURATION" in
    ''|*[!0-9]*) die "--duration должен быть целым числом секунд: $DURATION" ;;
esac
[ "$DURATION" -gt 0 ] || die "--duration должен быть больше 0: $DURATION"

BASE="$OUT_ROOT/$ORIGIN/$SCENARIO"

# Снятая трасса восстановить неоткуда — отказываемся перезаписывать без --force.
if [ "$FORCE" -ne 1 ]; then
    for ext in logcat nativelog dumpsys cantrace; do
        if [ -e "$BASE.$ext" ]; then
            die "файл уже существует: $BASE.$ext (перезапись только с --force)"
        fi
    done
fi

command -v adb >/dev/null 2>&1 || die "adb не найден в PATH"

ADB=(adb)
if [ -n "$SERIAL" ]; then
    ADB+=(-s "$SERIAL")
fi
"${ADB[@]}" get-state >/dev/null 2>&1 || die "нет устройства (проверьте adb devices / --serial)"

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

# 4. CAN-кадры — best-effort (см. шапку). run_timed ограничивает длительность
# даже без timeout (иначе candump завис бы навсегда).
if [ -n "$CAN_CMD" ]; then
    run_timed bash -c "$CAN_CMD" > "$BASE.cantrace" \
        || warn "can-cmd прерван/завершился с кодом $?"
elif "${ADB[@]}" shell which candump >/dev/null 2>&1; then
    run_timed "${ADB[@]}" shell candump -t a any > "$BASE.cantrace" \
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
