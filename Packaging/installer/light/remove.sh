#!/bin/sh
# Удаление Open Voyah v@VERSION@-LIGHT — полный откат к состоянию ДО установки.
# LIGHT ничего не инжектит и не трогает init.logcat.sh/Frida — чистим только priv-app + whitelist + оба APK.
# FIX-1: работаем из папки скрипта — относительные пути (./dns-overlay.sh, ./backup) обязаны
# резолвиться одинаково при запуске из любого CWD (регрессия b6c90e5, merge 90ed97c).
cd "$(dirname "$0")" || exit 1
if [ ! -f ./dns-overlay.sh ]; then
    echo "!!! Не найден ./dns-overlay.sh — удаление прервано до изменения устройства."
    exit 1
fi
. ./dns-overlay.sh || {
    echo "!!! Не удалось загрузить ./dns-overlay.sh — удаление прервано."
    exit 1
}
# Проверяем функции common-либы, от которых зависит движок (FIX-1/4/10 хелперы).
for ydns_required in ydns_prepare_helper restore_yandex_dns wait_adb_device device_health_warn; do
    if ! command -v "$ydns_required" >/dev/null 2>&1; then
        echo "!!! dns-overlay.sh не содержит $ydns_required — удаление прервано."
        exit 1
    fi
done
if ! ydns_prepare_helper restore; then
    echo "!!! Не удалось подготовить DNS-overlay helper — удаление прервано."
    exit 1
fi

adb root >/dev/null 2>&1
# FIX-4: wait с таймаутом (B3) вместо вечного блокирующего adb wait-for-device.
wait_adb_device || exit 1
adb root >/dev/null 2>&1
# FIX-10: только предупреждения о батарее/месте/модели (движок не блокирует — гейт только в TUI).
device_health_warn
# /system записываемым: снимаем verity (идемпотентно) + overlay-remount + сырой remount. Полный
# ребут-цикл здесь не нужен (после install verity уже снята; если её вернул OTA — сначала прогнать
# install.sh, он снимет verity и перезагрузит).
adb disable-verity >/dev/null 2>&1
adb remount >/dev/null 2>&1
adb shell 'mount -o rw,remount /system 2>/dev/null; mount -o rw,remount / 2>/dev/null'
# FIX-2 (b6c90e5, регрессия merge 90ed97c): RW-gate — не удалять файлы из read-only /system
# (иначе partial state без кода ошибки). Полный remove всегда имел этот гейт, light потерял.
if [ "$(adb shell 'touch /system/.ovw_rwtest 2>/dev/null && rm -f /system/.ovw_rwtest && echo RW || echo RO' | tr -d '\r')" != "RW" ]; then
    echo "!!! /system read-only — удаление прервано (файлы не тронуты)."
    echo "    Проверьте вручную: adb disable-verity ; adb reboot ; adb root ; adb remount."
    echo "    Если /system не делается записываемым — сначала install.sh из этого же релиза."
    exit 1
fi

echo "=== Откат DNS-overlay ==="
if ! restore_yandex_dns; then
    echo "!!! Не удалось восстановить/отключить DNS-overlay — остальные компоненты не удалялись."
    exit 1
fi

# --- Whitelist + Native из /system/priv-app (+ снять /data-оверлей обновления) ---
echo "=== Удаление APK Open Voyah ==="
adb shell am force-stop ru.big.town.anative >/dev/null 2>&1
adb shell am force-stop ru.big.town.restoremode >/dev/null 2>&1
# Android 11 app-data удаляет только PackageManager/installd. Raw rm из /data/user_de ломает
# /data_mirror encryption state и может сделать следующий Native незапускаемым.
if ! adb shell '
    pm uninstall ru.big.town.anative >/dev/null 2>&1 || true
    pm uninstall --user 0 ru.big.town.anative >/dev/null 2>&1 || true
    pm uninstall ru.big.town.restoremode >/dev/null 2>&1 || true
    if pm path ru.big.town.anative 2>/dev/null | grep -q "^package:/data/app/"; then exit 1; fi
    if pm path ru.big.town.restoremode 2>/dev/null | grep -q "^package:"; then exit 1; fi
'; then
    echo "!!! PackageManager не завершил удаление APK Open Voyah — перезагрузка отменена."
    exit 1
fi
echo "  PackageManager удалил user-data, RestoreMode и Native update; raw app-data не трогаем."
# FIX-7/R-A3: откат по контракту «состояние ДО установки». Если backup/ содержит системные
# Native.apk/whitelist — вернуть их атомарно, иначе удалить наши. cmp-фильтр отсекает загрязнение
# backup'а повторной установкой (backup == лежащему здесь же релизному APK → это не оригинал,
# а наш файл → откат = удаление).
NATIVE_RESTORE=0
if [ -s backup/Native.apk ] && { [ ! -s ./native.apk ] || ! cmp -s backup/Native.apk ./native.apk; }; then
    NATIVE_RESTORE=1
fi
PRIVAPP_RESTORE=0
if [ -s backup/privapp-permissions-ru.big.town.anative.xml ] && { [ ! -s ./privapp-permissions-ru.big.town.anative.xml ] || ! cmp -s backup/privapp-permissions-ru.big.town.anative.xml ./privapp-permissions-ru.big.town.anative.xml; }; then
    PRIVAPP_RESTORE=1
fi
if [ "$NATIVE_RESTORE" = 1 ]; then
    if ! adb shell "mkdir -p /system/priv-app/Native && rm -f /system/priv-app/.Native.apk.voyahtune.new /system/priv-app/.Native.apk.voyahtune-restore.new"; then
        echo "!!! Не удалось подготовить /system/priv-app/Native к восстановлению — перезагрузка отменена."
        exit 1
    fi
    if ! adb push backup/Native.apk /system/priv-app/.Native.apk.voyahtune-restore.new; then
        echo "!!! Не удалось передать backup/Native.apk — перезагрузка отменена."
        exit 1
    fi
    if ! adb shell "chown 0:0 /system/priv-app/.Native.apk.voyahtune-restore.new && chmod 0644 /system/priv-app/.Native.apk.voyahtune-restore.new && restorecon /system/priv-app/.Native.apk.voyahtune-restore.new && mv -f /system/priv-app/.Native.apk.voyahtune-restore.new /system/priv-app/Native/Native.apk && restorecon /system/priv-app/Native/Native.apk && sync && test -f /system/priv-app/Native/Native.apk"; then
        adb shell "rm -f /system/priv-app/.Native.apk.voyahtune-restore.new" >/dev/null 2>&1
        echo "!!! Не удалось восстановить Native.apk из backup — перезагрузка отменена."
        exit 1
    fi
    echo "  Native.apk восстановлен из backup (был до установки Open Voyah)."
else
    if ! adb shell "rm -f /system/priv-app/.Native.apk.voyahtune.new && rm -rf /system/priv-app/Native && test ! -e /system/priv-app/.Native.apk.voyahtune.new && test ! -e /system/priv-app/Native"; then
        echo "!!! Не удалось полностью удалить системные файлы Open Voyah — перезагрузка отменена."
        exit 1
    fi
fi
if [ "$PRIVAPP_RESTORE" = 1 ]; then
    if ! adb shell "rm -f /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune.new /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune-restore.new"; then
        echo "!!! Не удалось очистить staging whitelist-xml — перезагрузка отменена."
        exit 1
    fi
    if ! adb push backup/privapp-permissions-ru.big.town.anative.xml /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune-restore.new; then
        echo "!!! Не удалось передать backup whitelist-xml — перезагрузка отменена."
        exit 1
    fi
    if ! adb shell "chown 0:0 /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune-restore.new && chmod 0644 /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune-restore.new && restorecon /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune-restore.new && mv -f /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune-restore.new /system/etc/permissions/privapp-permissions-ru.big.town.anative.xml && restorecon /system/etc/permissions/privapp-permissions-ru.big.town.anative.xml && sync && test -f /system/etc/permissions/privapp-permissions-ru.big.town.anative.xml"; then
        adb shell "rm -f /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune-restore.new" >/dev/null 2>&1
        echo "!!! Не удалось восстановить whitelist-xml из backup — перезагрузка отменена."
        exit 1
    fi
    echo "  Whitelist-xml восстановлен из backup (был до установки Open Voyah)."
else
    if ! adb shell "rm -f /system/etc/permissions/privapp-permissions-ru.big.town.anative.xml /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune.new && test ! -e /system/etc/permissions/privapp-permissions-ru.big.town.anative.xml && test ! -e /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune.new"; then
        echo "!!! Не удалось полностью удалить whitelist Open Voyah — перезагрузка отменена."
        exit 1
    fi
fi
# Light не создаёт full-hook файлы. Здесь остаются только наши собственные helper/state/log paths;
# CE/DE, profiles и Android/data уже принадлежат PackageManager и вручную не удаляются.
if ! adb shell '
    rm -f \
        /data/local/bin/apollo_tech.js \
        /data/local/bin/apollo_tech.js.new \
        /data/local/bin/app_client.js \
        /data/local/bin/app_client.js.voyahtune.new \
        /data/local/bin/fullscreen_client.js \
        /data/local/bin/fullscreen_client.js.voyahtune.new \
        /data/local/bin/keyboard_lock_en.js \
        /data/local/bin/keyboard_ru.js \
        /data/local/bin/voyahtune_keyboard_en_config.json \
        /data/local/bin/voyahtune_keyboard_ru_config.json \
        /data/local/bin/voyahtune_skb_qwerty_ru.json \
        /data/local/bin/voyahtune-hook-manifest.json \
        /data/local/tmp/voyahtune-hook-status.v1 \
        /data/local/tmp/voyahtune_app_client.* \
        /data/local/tmp/voyahtune_fullscreen_client.* \
        /data/local/tmp/voyahtune_keyboard.pid \
        /data/local/tmp/voyahtune_keyboard.attempt \
        /data/local/tmp/voyahtune_keyboard.txt \
        /data/local/tmp/voyahtune_keyboard.txt.try \
        /data/local/tmp/voyahtune_apollo.pid \
        /data/local/tmp/voyahtune_apollo.attempt \
        /data/local/tmp/voyahtune_apollo.txt \
        /data/local/tmp/voyahtune_apollo.txt.try \
        /data/local/tmp/voyah_apollo.pid \
        /data/local/tmp/voyah_apollo.down \
        /data/local/tmp/voyah_apollo.disabled \
        /data/local/tmp/voyah_apollo.txt \
        /data/local/tmp/voyah_apollo.txt.1 \
        /data/local/tmp/voyah_apollo.txt.try \
        /data/local/tmp/open_voyah_dns_overlay.sh \
        /data/local/tmp/open_voyah_yandex_dns.apk \
        /sdcard/tmp/voyahtune_native_log.txt \
        /sdcard/tmp/voyah_native_log.txt && \
    rm -rf /data/local/open_voyah && \
    for path in \
        /data/local/bin/apollo_tech.js \
        /data/local/bin/apollo_tech.js.new \
        /data/local/bin/app_client.js \
        /data/local/bin/app_client.js.voyahtune.new \
        /data/local/bin/fullscreen_client.js \
        /data/local/bin/fullscreen_client.js.voyahtune.new \
        /data/local/bin/keyboard_lock_en.js \
        /data/local/bin/keyboard_ru.js \
        /data/local/bin/voyahtune_keyboard_en_config.json \
        /data/local/bin/voyahtune_keyboard_ru_config.json \
        /data/local/bin/voyahtune_skb_qwerty_ru.json \
        /data/local/bin/voyahtune-hook-manifest.json \
        /data/local/tmp/voyahtune-hook-status.v1 \
        /data/local/tmp/voyahtune_keyboard.pid \
        /data/local/tmp/voyahtune_keyboard.attempt \
        /data/local/tmp/voyahtune_keyboard.txt \
        /data/local/tmp/voyahtune_keyboard.txt.try \
        /data/local/tmp/voyahtune_apollo.pid \
        /data/local/tmp/voyahtune_apollo.attempt \
        /data/local/tmp/voyahtune_apollo.txt \
        /data/local/tmp/voyahtune_apollo.txt.try \
        /data/local/tmp/voyah_apollo.pid \
        /data/local/tmp/voyah_apollo.down \
        /data/local/tmp/voyah_apollo.disabled \
        /data/local/tmp/voyah_apollo.txt \
        /data/local/tmp/voyah_apollo.txt.1 \
        /data/local/tmp/voyah_apollo.txt.try \
        /data/local/tmp/open_voyah_dns_overlay.sh \
        /data/local/tmp/open_voyah_yandex_dns.apk \
        /data/local/open_voyah \
        /sdcard/tmp/voyahtune_native_log.txt \
        /sdcard/tmp/voyah_native_log.txt; do
        if [ -e "$path" ] || [ -L "$path" ]; then exit 1; fi
    done
    ! ls /data/local/tmp/voyahtune_app_client.* >/dev/null 2>&1 || exit 1
    ! ls /data/local/tmp/voyahtune_fullscreen_client.* >/dev/null 2>&1 || exit 1
'; then
    echo "!!! Не удалось полностью удалить собственные helper/state-файлы Open Voyah — перезагрузка отменена."
    exit 1
fi

# Light создаёт эти fail-closed ключи при установке прямого Apollo; полный откат удаляет их.
echo "=== Очистка Settings.Global ==="
if ! adb shell '
    for setting_name in \
        open_voyah_apollo_master open_voyah_apollo_legacy_hook_enabled \
        open_voyah_apollo_asc open_voyah_apollo_sdb \
        open_voyah_apollo_profile_supported open_voyah_apollo_profile_heartbeat \
        hidden_api_policy; do
        settings delete global "$setting_name" >/dev/null 2>&1 || exit 1
    done
    settings delete global voyahtune_keyboard_mode >/dev/null 2>&1 || exit 1
'; then
    echo "!!! Не удалось полностью очистить Settings.Global — перезагрузка отменена."
    exit 1
fi
echo "  Настройки Open Voyah очищены."

# Примечание: persist.app.feature.leavecar (power hold) НЕ откатываем — это штатная функция авто.

adb reboot
