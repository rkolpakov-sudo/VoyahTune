@echo off
setlocal EnableExtensions DisableDelayedExpansion
cd /d "%~dp0" || exit /b 1
for %%F in (adb.exe AdbWinApi.dll AdbWinUsbApi.dll native.apk restore_mode.apk privapp-permissions-ru.big.town.anative.xml) do if not exist "%%F" (
    echo !!! Required file %%F is missing. The device was not changed.
    exit /b 1
)

adb.exe root
call :wait_adb_device 60
if errorlevel 1 exit /b 1
adb.exe root

echo === Preflight direct-only Apollo ^(VehicleSetting hook OFF^) ===
call :put_apollo_safe_key open_voyah_apollo_legacy_hook_enabled
if errorlevel 1 exit /b 1
call :put_apollo_safe_key open_voyah_apollo_master
if errorlevel 1 exit /b 1
call :put_apollo_safe_key open_voyah_apollo_profile_supported
if errorlevel 1 exit /b 1
call :put_apollo_safe_key open_voyah_apollo_profile_heartbeat
if errorlevel 1 exit /b 1
echo   Legacy opt-in, master, profile, and heartbeat are disabled.

echo === Preflight owner check for com.qinggan.permission.WRITE_CANBUS ===
adb.exe shell dumpsys package permissions >nul 2>nul
if errorlevel 1 (
    echo !!! PackageManager permissions are unavailable. Installation stopped before writing to /system.
    exit /b 1
)
set CANBUS_PERMISSION_PRESENT=0
adb.exe shell "dumpsys package permissions | grep -qF 'Permission [com.qinggan.permission.WRITE_CANBUS]'" >nul 2>nul
if not errorlevel 1 set CANBUS_PERMISSION_PRESENT=1
set CANBUS_PERMISSION_OWNER=
if "%CANBUS_PERMISSION_PRESENT%"=="1" for /f "tokens=2 delims==" %%i in ('adb.exe shell "dumpsys package permissions ^| grep -A 32 -F 'Permission [com.qinggan.permission.WRITE_CANBUS]' ^| grep -F 'sourcePackage='" 2^>nul') do if not defined CANBUS_PERMISSION_OWNER set CANBUS_PERMISSION_OWNER=%%i
if "%CANBUS_PERMISSION_PRESENT%"=="0" goto :canbus_permission_ok
if "%CANBUS_PERMISSION_OWNER%"=="ru.big.town.anative" goto :canbus_permission_ok
if "%CANBUS_PERMISSION_OWNER%"=="" (
    echo !!! Owner of com.qinggan.permission.WRITE_CANBUS is unknown. Installation stopped before writing to /system.
    echo     Same as install.sh: fail-closed when the owner cannot be confirmed.
    exit /b 1
)
echo !!! com.qinggan.permission.WRITE_CANBUS already belongs to %CANBUS_PERMISSION_OWNER%.
echo     Remove the incompatible package and repeat light install. /system is still unchanged.
exit /b 1
:canbus_permission_ok
if "%CANBUS_PERMISSION_PRESENT%"=="0" echo   The permission is not declared yet. Native will create it.
if "%CANBUS_PERMISSION_PRESENT%"=="1" echo   The permission belongs to ru.big.town.anative. This update is compatible.

echo === Preparing writable /system ^(verity, overlay^) ===
adb.exe disable-verity
call :ensure_rw
if "%RWSTATE%"=="RW" goto :sys_rw_ok
echo   /system is read-only. Rebooting once to apply disable-verity...
adb.exe reboot
call :wait_adb_device 120
if errorlevel 1 exit /b 1
set /a _bi=0
:wait_boot_ovw
adb.exe shell getprop sys.boot_completed 2>nul | findstr /b "1" >nul
if not errorlevel 1 goto :booted_ovw
set /a _bi+=1
if %_bi% GEQ 60 goto :booted_ovw
timeout /t 5 /nobreak >nul
goto :wait_boot_ovw
:booted_ovw
timeout /t 3 /nobreak >nul
adb.exe root
call :wait_adb_device 60
if errorlevel 1 exit /b 1
adb.exe root
call :ensure_rw
if "%RWSTATE%"=="RW" goto :sys_rw_ok
echo !!! /system remains read-only. Installation stopped without changing /system.
echo     Possible causes: locked bootloader, EROFS firmware, or unsupported verity configuration.
echo     Try manually: adb disable-verity, adb reboot, adb root, adb remount.
exit /b 1
:sys_rw_ok
echo   /system is writable. Continuing.

set BACKUP_DIR=backup
if not exist "%BACKUP_DIR%" (
    mkdir "%BACKUP_DIR%"
    if errorlevel 1 (
        echo !!! Could not prepare %BACKUP_DIR%. Installation stopped before replacing files.
        exit /b 1
    )
)

echo === Backing up files to %BACKUP_DIR%\ ===
call :backup_pull /system/priv-app/Native/Native.apk     Native.apk
if errorlevel 1 exit /b 1
call :backup_pull /system/etc/permissions/privapp-permissions-ru.big.town.anative.xml privapp-permissions-ru.big.town.anative.xml
if errorlevel 1 exit /b 1

echo === Native.apk in /system/priv-app ^(privileged permissions required for CAN features^) ===
adb.exe shell mkdir -p /system/priv-app/Native
if errorlevel 1 (
    echo !!! Could not create /system/priv-app/Native. Installation stopped.
    exit /b 1
)
adb.exe shell chmod 755 /system/priv-app/Native
adb.exe push native.apk /system/priv-app/.Native.apk.voyahtune.new
if errorlevel 1 (
    echo !!! Failed to stage Native.apk. Installation stopped.
    adb.exe shell "rm -f /system/priv-app/.Native.apk.voyahtune.new" >nul 2>nul
    exit /b 1
)
adb.exe shell "chown 0:0 /system/priv-app/.Native.apk.voyahtune.new && chmod 644 /system/priv-app/.Native.apk.voyahtune.new && restorecon /system/priv-app/.Native.apk.voyahtune.new && mv -f /system/priv-app/.Native.apk.voyahtune.new /system/priv-app/Native/Native.apk && restorecon /system/priv-app/Native/Native.apk && sync && test -f /system/priv-app/Native/Native.apk"
if errorlevel 1 (
    echo !!! Native.apk atomic install failed. Installation stopped.
    adb.exe shell "rm -f /system/priv-app/.Native.apk.voyahtune.new" >nul 2>nul
    exit /b 1
)
adb.exe shell "ls -all /system/priv-app/Native"

adb.exe shell "mkdir -p /system/etc/permissions"
if errorlevel 1 (
    echo !!! Could not create /system/etc/permissions. Installation stopped.
    exit /b 1
)
adb.exe push privapp-permissions-ru.big.town.anative.xml /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune.new
if errorlevel 1 (
    echo !!! Failed to stage privapp whitelist. Installation stopped.
    adb.exe shell "rm -f /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune.new" >nul 2>nul
    exit /b 1
)
adb.exe shell "chown 0:0 /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune.new && chmod 644 /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune.new && restorecon /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune.new && mv -f /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune.new /system/etc/permissions/privapp-permissions-ru.big.town.anative.xml && restorecon /system/etc/permissions/privapp-permissions-ru.big.town.anative.xml && sync && test -f /system/etc/permissions/privapp-permissions-ru.big.town.anative.xml"
if errorlevel 1 (
    echo !!! Privapp whitelist atomic install failed. Installation stopped.
    adb.exe shell "rm -f /system/etc/.privapp-permissions-ru.big.town.anative.xml.voyahtune.new" >nul 2>nul
    exit /b 1
)

set LEAVECAR=
for /f "delims=" %%i in ('adb.exe shell getprop persist.app.feature.leavecar') do set LEAVECAR=%%i
if not "%LEAVECAR%"=="true" (
    echo Enabling leave car ^(power hold^)...
    adb.exe shell setprop persist.app.feature.leavecar true
)

adb.exe install -r -g restore_mode.apk
if errorlevel 1 (
    echo !!! RestoreMode was not installed. Fix the error and run the installer again before rebooting.
    exit /b 1
)

adb.exe reboot
if errorlevel 1 (
    echo !!! The installation is prepared, but ADB could not reboot the device. Reboot it manually.
    exit /b 1
)
echo Installation complete.
echo Run install-yandex-dns.bat separately if Yandex DNS is required.
exit /b 0
goto :eof

:put_apollo_safe_key
adb.exe shell settings put global %~1 0
if errorlevel 1 (
    echo !!! Could not write %~1=0. Installation stopped before writing to /system.
    exit /b 1
)
set "APOLLO_SAFE_STATE="
for /f "delims=" %%i in ('adb.exe shell settings get global %~1 2^>nul') do set "APOLLO_SAFE_STATE=%%i"
if not "%APOLLO_SAFE_STATE%"=="0" (
    echo !!! %~1 did not confirm value 0. Installation stopped before writing to /system.
    exit /b 1
)
exit /b 0

:ensure_rw
adb.exe remount >nul 2>nul
adb.exe shell "mount -o rw,remount /system 2>/dev/null; mount -o rw,remount / 2>/dev/null" >nul 2>nul
echo rwtest> "%TEMP%\_ovw_rwtest.tmp"
adb.exe push "%TEMP%\_ovw_rwtest.tmp" /system/_ovw_rwtest >nul 2>nul
if errorlevel 1 (set RWSTATE=RO) else (set RWSTATE=RW)
adb.exe shell "rm -f /system/_ovw_rwtest" >nul 2>nul
del "%TEMP%\_ovw_rwtest.tmp" >nul 2>nul
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

:backup_pull
if exist "%BACKUP_DIR%\%~2" (
    for %%A in ("%BACKUP_DIR%\%~2") do if %%~zA LEQ 0 (
        echo !!! Existing backup %BACKUP_DIR%\%~2 is empty or is not a file.
        exit /b 1
    )
    echo Backup: %BACKUP_DIR%\%~2 already exists - keeping the original
    exit /b 0
)
set "BACKUP_REMOTE_STATE="
for /f "delims=" %%i in ('adb.exe shell "if [ -f %1 ]; then echo PRESENT; elif [ -e %1 ]; then echo ERROR; else echo ABSENT; fi" 2^>nul') do set "BACKUP_REMOTE_STATE=%%i"
if "%BACKUP_REMOTE_STATE%"=="ABSENT" (
    echo Backup: %1 does not exist - skipped
    exit /b 0
)
if not "%BACKUP_REMOTE_STATE%"=="PRESENT" (
    echo !!! Could not safely read %1 before backup.
    exit /b 1
)
del "%BACKUP_DIR%\%~2.new" 1>nul 2>nul
adb.exe pull %1 "%BACKUP_DIR%\%~2.new" 1>nul 2>nul
if errorlevel 1 goto :backup_pull_failed
for %%A in ("%BACKUP_DIR%\%~2.new") do if %%~zA LEQ 0 goto :backup_pull_failed
move /y "%BACKUP_DIR%\%~2.new" "%BACKUP_DIR%\%~2" 1>nul 2>nul
if errorlevel 1 goto :backup_pull_failed
echo Backup: %1 -^> %BACKUP_DIR%\%~2
exit /b 0

:backup_pull_failed
del "%BACKUP_DIR%\%~2.new" 1>nul 2>nul
echo !!! Could not save existing %1. Installation stopped.
exit /b 1
