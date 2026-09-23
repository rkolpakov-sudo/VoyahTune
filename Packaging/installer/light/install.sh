#!/bin/sh
# Установка Open Voyah v@VERSION@-LIGHT. Запускать из папки релиза (бэкапы падают в ./backup).
# LIGHT = без Frida/root-инъекций: режимы вождения, прямой Binder Apollo, автосвет, прогрев/статусы, дворники,
# поездки, Power Hold, режим мойки, звук пешеходов, плавающая кнопка «Назад», установка приложений,
# ярлыки приложений (обычный запуск). БЕЗ сплита/дока/VirtualDisplay, БЕЗ Frida и любой root-инъекции,
# БЕЗ раздела «Кнопки на руле». init.logcat.sh системы НЕ трогаем.
cd "$(dirname "$0")" || exit 1
if [ ! -f ./dns-overlay.sh ]; then
    echo "!!! Не найден ./dns-overlay.sh — установка прервана до изменения устройства."
    exit 1
fi
. ./dns-overlay.sh || {
    echo "!!! Не удалось загрузить ./dns-overlay.sh — установка прервана."
    exit 1
}
for ydns_required in ydns_prepare_helper ydns_query_state choose_yandex_dns install_yandex_dns disable_yandex_dns; do
    if ! command -v "$ydns_required" >/dev/null 2>&1; then
        echo "!!! dns-overlay.sh не содержит $ydns_required — установка прервана."
        exit 1
    fi
done
for LIGHT_REQUIRED_ASSET in native.apk restore_mode.apk \
        privapp-permissions-ru.big.town.anative.xml; do
    if [ ! -s "$LIGHT_REQUIRED_ASSET" ]; then
        echo "!!! Отсутствует или пуст обязательный файл $LIGHT_REQUIRED_ASSET — устройство не изменялось."
        exit 1
    fi
done
if ! ydns_prepare_helper; then
    echo "!!! Не удалось подготовить DNS-overlay helper — установка прервана."
    exit 1
fi

adb root
wait_adb_device || exit 1
adb root

# Direct Apollo не использует legacy VehicleSetting hook. Закрываем старые opt-in/liveness ключи
# до disable-verity и любых изменений /system, в том числе при переходе с full на light.
echo "=== Preflight direct-only Apollo (VehicleSetting hook OFF) ==="
for APOLLO_SAFE_KEY in \
        open_voyah_apollo_legacy_hook_enabled \
        open_voyah_apollo_master \
        open_voyah_apollo_profile_supported \
        open_voyah_apollo_profile_heartbeat; do
    if ! adb shell settings put global "$APOLLO_SAFE_KEY" 0; then
        echo "!!! Не удалось записать $APOLLO_SAFE_KEY=0 — установка прервана до записи в /system."
        exit 1
    fi
    APOLLO_SAFE_STATE=$(adb shell settings get global "$APOLLO_SAFE_KEY" 2>/dev/null \
        | tr -d '\r')
    if [ "$APOLLO_SAFE_STATE" != "0" ]; then
        echo "!!! $APOLLO_SAFE_KEY не подтвердил 0 — установка прервана до записи в /system."
        exit 1
    fi
done
echo "  Legacy opt-in, master, profile и heartbeat закрыты."

# Оба флейвора Native владеют signature-разрешением прямой записи в CanBus. Чужой первый владелец
# сделал бы установленный APK несовместимым, поэтому конфликт проверяется до изменения /system.
echo "=== Preflight владельца com.qinggan.permission.WRITE_CANBUS ==="
CANBUS_PERMISSION_DUMP=$(adb shell dumpsys package permissions 2>/dev/null)
if [ $? -ne 0 ]; then
    echo "!!! PackageManager permissions недоступны — установка прервана до записи в /system."
    exit 1
fi
case "$CANBUS_PERMISSION_DUMP" in
    *"Permission [com.qinggan.permission.WRITE_CANBUS]"*)
        CANBUS_PERMISSION_OWNER=$(printf '%s\n' "$CANBUS_PERMISSION_DUMP" | awk '
            /Permission \[com\.qinggan\.permission\.WRITE_CANBUS\]/ { in_block=1; next }
            in_block && /Permission \[/ { exit }
            in_block && /sourcePackage=/ {
                sub(/^.*sourcePackage=/, ""); gsub(/[[:space:]]/, ""); print; exit
            }')
        if [ "$CANBUS_PERMISSION_OWNER" != "ru.big.town.anative" ]; then
            if [ -n "$CANBUS_PERMISSION_OWNER" ]; then
                echo "!!! com.qinggan.permission.WRITE_CANBUS уже принадлежит $CANBUS_PERMISSION_OWNER."
            else
                echo "!!! Владелец com.qinggan.permission.WRITE_CANBUS не определён однозначно."
            fi
            echo "    Удалите несовместимый пакет и повторите light install; /system ещё не изменялся."
            exit 1
        fi
        echo "  Permission уже принадлежит ru.big.town.anative — совместимое обновление."
        ;;
    *)
        echo "  Permission ещё не объявлен — его создаст Native."
        ;;
esac

# --- Гарантируем ЗАПИСЫВАЕМЫЙ /system --------------------------------------------------------
# Light тоже пишет в /system (priv-app + whitelist привилегий), поэтому подготовка нужна ровно та же,
# что и в full. Без неё на стоковой/после-OTA голове dm-verity держит /system read-only → push даёт
# "I/O error" и установка падает. disable-verity вступает в силу ТОЛЬКО после РЕБУТА. Делаем
# ИДЕМПОТЕНТНО: если /system уже записываем (готовая голова) — ребута НЕ будет; иначе снимаем verity,
# ОДИН раз перезагружаемся и продолжаем. adb remount = OverlayFS поверх read-only/динамических
# разделов (устойчивее сырого mount -o rw,remount). При невозможности — прерываемся, НЕ трогая /system.
system_is_writable() {
    adb remount >/dev/null 2>&1
    adb shell 'mount -o rw,remount /system 2>/dev/null; mount -o rw,remount / 2>/dev/null' >/dev/null 2>&1
    [ "$(adb shell 'touch /system/.ovw_rwtest 2>/dev/null && rm -f /system/.ovw_rwtest && echo RW || echo RO' | tr -d '\r')" = "RW" ]
}

echo "=== Готовим /system к записи (verity → overlay) ==="
adb disable-verity 2>&1 | sed 's/^/  /'
if ! system_is_writable; then
    echo "  /system ещё read-only → перезагрузка ОДИН раз (применяем disable-verity)..."
    adb reboot
    ADB_WAIT_TIMEOUT=120 wait_adb_device || exit 1
    i=0
    while [ $i -lt 60 ]; do
        [ "$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" = "1" ] && break
        sleep 5; i=$((i + 1))
    done
    sleep 3
    adb root >/dev/null 2>&1; wait_adb_device || exit 1; adb root >/dev/null 2>&1
fi
if ! system_is_writable; then
    echo "!!! /system ОСТАЁТСЯ read-only — установка прервана (в /system ничего не тронуто)."
    echo "    Причины: заблокирован загрузчик (disable-verity не срабатывает) / прошивка с EROFS"
    echo "    (несжимаемая read-only ФС) / verity не снимается на этой сборке."
    echo "    Проверьте вручную: adb disable-verity ; adb reboot ; adb root ; adb remount ; adb shell mount | grep system"
    exit 1
fi
echo "  /system записываем — продолжаем."

BACKUP_DIR="backup"
mkdir -p "$BACKUP_DIR" || {
    echo "!!! Не удалось подготовить $BACKUP_DIR — установка прервана до перезаписи файлов."
    exit 1
}

# Бэкап файла с головы перед перезаписью. Если бэкап уже есть — не трогаем (сохраняем оригинал).
# Remote-state: PRESENT → pull; ABSENT → пропуск; ERROR → fail-closed (не маскировать под absence).
backup_pull() {
    if [ -e "$BACKUP_DIR/$2" ]; then
        if [ -f "$BACKUP_DIR/$2" ] && [ -s "$BACKUP_DIR/$2" ]; then
            echo "Backup: $BACKUP_DIR/$2 уже есть — пропуск (сохраняем оригинал)"
            return 0
        fi
        echo "!!! Существующий backup $BACKUP_DIR/$2 пуст или не является файлом."
        return 1
    fi
    BACKUP_REMOTE_STATE=$(adb shell "if [ -f '$1' ]; then echo PRESENT; elif [ -e '$1' ]; then echo ERROR; else echo ABSENT; fi" 2>/dev/null) || {
        echo "!!! Не удалось проверить $1 перед backup."
        return 1
    }
    BACKUP_REMOTE_STATE=$(printf '%s' "$BACKUP_REMOTE_STATE" | tr -d '\r')
    case "$BACKUP_REMOTE_STATE" in
        ABSENT)
            echo "Backup: $1 отсутствует, пропуск"
            return 0
            ;;
        PRESENT)
            rm -f "$BACKUP_DIR/$2.new"
            if adb pull "$1" "$BACKUP_DIR/$2.new" >/dev/null 2>&1 \
                    && [ -s "$BACKUP_DIR/$2.new" ] \
                    && mv -f "$BACKUP_DIR/$2.new" "$BACKUP_DIR/$2"; then
                echo "Backup: $1 -> $BACKUP_DIR/$2"
                return 0
            fi
            rm -f "$BACKUP_DIR/$2.new"
            echo "!!! Не удалось сохранить существующий $1 — установка прервана."
            return 1
            ;;
        *)
            echo "!!! $1 существует, но не является доступным regular file — установка прервана."
            return 1
            ;;
    esac
}

echo "=== Бэкап перезаписываемых файлов в $BACKUP_DIR/ ==="
backup_pull /system/priv-app/Native/Native.apk     Native.apk || exit 1
backup_pull /system/etc/permissions/privapp-permissions-ru.big.town.anative.xml privapp-permissions-ru.big.town.anative.xml || exit 1

# NB: install.sh — это ОБНОВЛЕНИЕ и СОХРАНЯЕТ настройки. RestoreMode ставится через -r (его data
# остаётся); Native обновляется пушем APK в /system БЕЗ сноса data, поэтому локальные тумблеры Native
# (autoLight / wiperCold / floatingBack) тоже сохраняются.
# Полная чистка протухшего состояния Native (лечение краха zygote "data_de/null") вынесена в remove.sh.
echo "=== Native.apk в /system/priv-app (нужны привилегированные пермишены для CAN-функций) ==="
# /system уже сделан записываемым выше (verity, overlay) — отдельный remount не нужен.
adb shell mkdir -p /system/priv-app/Native || exit 1
adb shell chmod 755 /system/priv-app/Native || exit 1
if ! adb push native.apk /system/priv-app/.Native.apk.voyahtune.new; then
    echo "!!! Не удалось подготовить Native.apk — установка прервана."
    adb shell "rm -f /system/priv-app/.Native.apk.voyahtune.new" >/dev/null 2>&1
    exit 1
fi
adb shell "chown 0:0 /system/priv-app/.Native.apk.voyahtune.new && chmod 644 /system/priv-app/.Native.apk.voyahtune.new && restorecon /system/priv-app/.Native.apk.voyahtune.new && mv -f /system/priv-app/.Native.apk.voyahtune.new /system/priv-app/Native/Native.apk && restorecon /system/priv-app/Native/Native.apk && sync && test -f /system/priv-app/Native/Native.apk" || {
    echo "!!! Атомарная установка Native.apk не удалась — установка прервана."
    adb shell "rm -f /system/priv-app/.Native.apk.voyahtune.new" >/dev/null 2>&1
    exit 1
}
adb shell "ls -all /system/priv-app/Native"

# Whitelist привилегированных пермишенов (нужен на enforce-ROM: FORCE_STOP/WRITE_SECURE_SETTINGS/…)
adb shell "mkdir -p /system/etc/permissions" || exit 1
if ! adb push privapp-permissions-ru.big.town.anative.xml /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune.new; then
    echo "!!! Не удалось подготовить whitelist привилегированных пермишенов — установка прервана."
    adb shell "rm -f /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune.new" >/dev/null 2>&1
    exit 1
fi
adb shell "chown 0:0 /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune.new && chmod 644 /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune.new && restorecon /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune.new && mv -f /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune.new /system/etc/permissions/privapp-permissions-ru.big.town.anative.xml && restorecon /system/etc/permissions/privapp-permissions-ru.big.town.anative.xml && sync && test -f /system/etc/permissions/privapp-permissions-ru.big.town.anative.xml" || {
    echo "!!! Атомарная установка whitelist не удалась — установка прервана."
    adb shell "rm -f /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune.new" >/dev/null 2>&1
    exit 1
}

# Включить power hold (leave car), если опция выключена или отсутствует
LEAVECAR=$(adb shell getprop persist.app.feature.leavecar | tr -d '\r')
if [ "$LEAVECAR" != "true" ]; then
    echo "Enabling leave car (power hold)..."
    adb shell setprop persist.app.feature.leavecar true
fi

if ! adb install -r -g restore_mode.apk; then
    echo "!!! RestoreMode не установлен — исправьте ошибку и повторите installer до перезагрузки."
    exit 1
fi

echo "=== DNS для доступа через T-Box ==="
if ! YDNS_CURRENT="$(ydns_query_state)"; then
    echo "!!! Не удалось определить текущее состояние DNS-overlay — финальная перезагрузка отменена."
    exit 1
fi
case "$YDNS_CURRENT" in
    on|off|external|broken) ;;
    *)
        echo "!!! DNS-overlay helper вернул неизвестное состояние: $YDNS_CURRENT"
        exit 1
        ;;
esac
echo "Текущее состояние DNS-overlay: $YDNS_CURRENT"
YDNS_REQUEST=
if ! choose_yandex_dns "$YDNS_CURRENT"; then
    echo "!!! Не удалось получить выбор DNS-overlay — финальная перезагрузка отменена."
    exit 1
fi
case "${YDNS_REQUEST:-keep}" in
    on)
        install_yandex_dns || {
            echo "!!! Установка DNS-overlay завершилась ошибкой — финальная перезагрузка отменена."
            exit 1
        }
        ;;
    off)
        disable_yandex_dns || {
            echo "!!! Отключение DNS-overlay завершилось ошибкой — финальная перезагрузка отменена."
            exit 1
        }
        ;;
    keep)
        echo "DNS-overlay: оставляем текущее состояние без изменений."
        ;;
    *)
        echo "!!! Неизвестный выбор DNS-overlay: ${YDNS_REQUEST:-<пусто>}"
        exit 1
        ;;
esac

# Ребут нужен, чтобы менеджер пакетов перечитал privapp-whitelist для /system/priv-app.
echo "Установка успешно завершена — устройство перезагружается."
adb reboot
