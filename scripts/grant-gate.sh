#!/usr/bin/env bash
# Grant-gate (SPEC L113): WRITE_CANBUS выдан под нашими ключами, Native
# стартует и подписывается на CAN. Пропуск только при полном успехе.
#
# Проверки (по порядку):
#   1. dumpsys package <pkg> | WRITE_CANBUS ... granted=true
#   2. Native запущен (pidof <pkg>)
#   3. подписка на CAN: в logcat есть «CanBus callback registered»
#      (ждёт до --timeout секунд после запуска Native)
#   4. в свежем logcat нет «WRITE_CANBUS permission missing»
#
# Коды выхода: 0 — гейт пройден; 1 — провал (СТОП по L113: пересмотр
# архитектуры установки); 2 — ошибка аргументов/окружения (adb/устройство).
#
# Примеры:
#   scripts/grant-gate.sh
#   scripts/grant-gate.sh --serial emulator-5554 --timeout 60
set -euo pipefail

PKG_DEFAULT="ru.big.town.anative"
MARKER_CAN='CanBus callback registered'
MARKER_NO_PERM='WRITE_CANBUS permission missing'

die() {
    printf 'grant-gate: %s\n' "$*" >&2
    exit 2
}

warn() {
    printf 'grant-gate: ПРЕДУПРЕЖДЕНИЕ: %s\n' "$*" >&2
}

usage() {
    sed -n '2,17p' "$0"
}

# Проверка, что у опции есть значение (иначе shift 2 под set -e убивает
# скрипт без сообщения, а пустое значение тихо меняет поведение).
need_value() {
    [ "$2" -ge 2 ] || die "$1 требует значение"
}

gate_fail() {
    printf 'grant-gate: ПРОВАЛ: %s\n' "$1" >&2
    printf 'grant-gate: СТОП (SPEC L113): стоп проекта, пересмотр архитектуры\n' >&2
    printf 'grant-gate: установки (возможно переименование пакетов + пересборка\n' >&2
    printf 'grant-gate: whitelist). См. Docs/parity-runbook.md.\n' >&2
    exit 1
}

PKG="$PKG_DEFAULT"
SERIAL=""
TIMEOUT=30

while [ $# -gt 0 ]; do
    case "$1" in
        --pkg)     need_value "$1" $#; PKG="$2"; shift 2 ;;
        --serial)  need_value "$1" $#; SERIAL="$2"; shift 2 ;;
        --timeout) need_value "$1" $#; TIMEOUT="$2"; shift 2 ;;
        -h|--help) usage; exit 0 ;;
        *)         die "неизвестный аргумент: $1" ;;
    esac
done
case "$TIMEOUT" in
    ''|*[!0-9]*) die "--timeout должен быть целым числом секунд: $TIMEOUT" ;;
esac
[ "$TIMEOUT" -gt 0 ] || die "--timeout должен быть больше 0: $TIMEOUT"

command -v adb >/dev/null 2>&1 || die "adb не найден в PATH"

ADB=(adb)
if [ -n "$SERIAL" ]; then
    ADB+=(-s "$SERIAL")
fi
"${ADB[@]}" get-state >/dev/null 2>&1 || die "нет устройства (проверьте adb devices / --serial)"

# 1. WRITE_CANBUS granted (dumpsys package; строка вида
#    «com.qinggan.permission.WRITE_CANBUS: granted=true»).
dumpsys_out=$("${ADB[@]}" shell dumpsys package "$PKG" | tr -d '\r' || true)
canbus_lines=$(printf '%s\n' "$dumpsys_out" | grep 'WRITE_CANBUS' || true)
if [ -z "$canbus_lines" ]; then
    gate_fail "пакет $PKG не установлен или манифест без WRITE_CANBUS (пустой dumpsys)"
fi
if printf '%s\n' "$canbus_lines" | grep -q 'granted=true'; then
    printf 'grant-gate: [1/4] WRITE_CANBUS granted=true ... OK\n'
else
    printf 'grant-gate: строки dumpsys с WRITE_CANBUS:\n%s\n' "$canbus_lines" >&2
    gate_fail "WRITE_CANBUS не в granted (подписи ключей не совпали?)"
fi

# 2. Native запущен.
pid=$("${ADB[@]}" shell pidof "$PKG" | tr -d '\r' || true)
if [ -z "$pid" ]; then
    gate_fail "Native ($PKG) не запущен (pidof пуст)"
fi
printf 'grant-gate: [2/4] Native запущен (pid=%s) ... OK\n' "$pid"

# 3. Подписка на CAN: ждём маркер в логе (дать Native время на bind).
deadline=$((SECONDS + TIMEOUT))
can_ok=0
while [ "$SECONDS" -lt "$deadline" ]; do
    if "${ADB[@]}" logcat -d 2>/dev/null | tr -d '\r' | grep -q "$MARKER_CAN"; then
        can_ok=1
        break
    fi
    sleep 2
done
if [ "$can_ok" -ne 1 ]; then
    gate_fail "нет маркера «$MARKER_CAN» за ${TIMEOUT}s (CAN не подписан?)"
fi
printf 'grant-gate: [3/4] подписка на CAN (%s) ... OK\n' "$MARKER_CAN"

# 4. Свежий лог без ошибок прав (окно -t 500, чтобы не падать на исторических).
recent=$("${ADB[@]}" logcat -d -t 500 2>/dev/null | tr -d '\r' || true)
if printf '%s\n' "$recent" | grep -q "$MARKER_NO_PERM"; then
    printf '%s\n' "$recent" | grep "$MARKER_NO_PERM" | head -5 >&2
    gate_fail "Native логирует «$MARKER_NO_PERM»"
fi
printf 'grant-gate: [4/4] нет «%s» в свежем логе ... OK\n' "$MARKER_NO_PERM"

printf 'grant-gate: ГЕЙТ ПРОЙДЕН (SPEC L113): %s pid=%s, CAN подписан.\n' \
    "$PKG" "$pid"
printf 'Дальше: снятие трасс L109/L110 и Utils/parity-diff.py (L111).\n'
