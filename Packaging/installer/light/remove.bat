@echo off
setlocal EnableExtensions DisableDelayedExpansion
cd /d "%~dp0" || exit /b 1
set "YDNS_HELPER=%~dp0dns-overlay.bat"
if not exist "%YDNS_HELPER%" (
    echo !!! Missing dns-overlay.bat. Removal stopped before changing the device.
    exit /b 1
)
call "%YDNS_HELPER%" prepare
if errorlevel 1 (
    echo !!! The DNS overlay helper is not ready. Removal stopped before changing the device.
    exit /b 1
)

adb.exe root
call :wait_adb_device 60
if errorlevel 1 exit /b 1
adb.exe root
adb.exe disable-verity >nul 2>nul
adb.exe remount >nul 2>nul
adb.exe shell "mount -o rw,remount /system 2>/dev/null; mount -o rw,remount / 2>/dev/null"
echo rwtest> "%TEMP%\_ovw_rwtest.tmp"
set "RWSTATE=RO"
adb.exe push "%TEMP%\_ovw_rwtest.tmp" /system/_ovw_rwtest >nul 2>nul
if not errorlevel 1 set "RWSTATE=RW"
adb.exe shell "rm -f /system/_ovw_rwtest" >nul 2>nul
del "%TEMP%\_ovw_rwtest.tmp" >nul 2>nul
if not "%RWSTATE%"=="RW" (
    echo !!! /system is read-only. Removal stopped without changing system files.
    echo     Try manually: adb disable-verity, adb reboot, adb root, adb remount.
    echo     If /system cannot be made writable, run install.sh from this same release first.
    exit /b 1
)

echo === Restoring the DNS overlay ===
call "%YDNS_HELPER%" restore
if errorlevel 1 (
    echo !!! Could not restore or disable the DNS overlay. Other components were not removed.
    exit /b 1
)

adb.exe shell "rm -f /system/etc/permissions/privapp-permissions-ru.big.town.anative.xml"
adb.exe shell "rm -rf /system/priv-app/Native"
adb.exe shell "ls -all /system/priv-app/Native"
adb.exe shell pm uninstall ru.big.town.anative
adb.exe shell pm uninstall ru.big.town.restoremode
adb.exe shell am force-stop ru.big.town.anative
adb.exe shell "rm -rf /data/user/0/ru.big.town.anative /data/user_de/0/ru.big.town.anative /data/data/ru.big.town.anative"

adb.exe shell settings delete global open_voyah_apollo_master 2>nul
adb.exe shell settings delete global open_voyah_apollo_legacy_hook_enabled 2>nul
adb.exe shell settings delete global open_voyah_apollo_asc 2>nul
adb.exe shell settings delete global open_voyah_apollo_sdb 2>nul
adb.exe shell settings delete global open_voyah_apollo_profile_supported 2>nul
adb.exe shell settings delete global open_voyah_apollo_profile_heartbeat 2>nul
adb.exe shell settings delete global voyahtune_dock1 2>nul
adb.exe shell settings delete global voyahtune_dock2 2>nul
adb.exe shell settings delete global voyahtune_dock1Dpi 2>nul
adb.exe shell settings delete global voyahtune_dock2Dpi 2>nul
adb.exe shell settings delete global voyahtune_steerStarShort 2>nul
adb.exe shell settings delete global voyahtune_steerStarLong 2>nul
adb.exe shell settings delete global voyahtune_steerDvrShort 2>nul
adb.exe shell settings delete global voyahtune_steerDvrLong 2>nul
adb.exe shell settings delete global voyahtune_steerVoiceShort 2>nul
adb.exe shell settings delete global voyahtune_steerVoiceLong 2>nul
adb.exe shell settings delete global voyahtune_steerPhoneShort 2>nul
adb.exe shell settings delete global voyahtune_steerPhoneLong 2>nul


adb.exe reboot
if errorlevel 1 (
    echo !!! Removal is prepared, but ADB could not reboot the device. Reboot it manually.
    exit /b 1
)
echo Removal complete. The device is rebooting.
exit /b 0

:wait_adb_device
set /a _awt=0
:wait_adb_device_loop
for /f "delims=" %%i in ('adb.exe get-state 2^>nul') do set "_awt_state=%%i"
if "%_awt_state%"=="device" (
    set "_awt_state="
    exit /b 0
)
set "_awt_state="
set /a _awt+=1
if %_awt% GEQ %~1 (
    echo !!! Device did not reach state=device within %~1 seconds.
    echo     Check USB Type-A cable, USB debugging; try adb kill-server / start-server.
    exit /b 1
)
timeout /t 1 /nobreak >nul
goto wait_adb_device_loop
