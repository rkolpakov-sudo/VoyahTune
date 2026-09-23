@echo off
setlocal EnableExtensions
rem verify_post_install.bat - read-only Windows checks after full install (ASCII/CRLF).
rem No mutations. Exit 0 = required OK; 1 = fail; 2 = no adb/device.
cd /d "%~dp0" || exit /b 1

if not exist "adb.exe" (
    echo [!!!] adb.exe missing. Cannot verify.
    exit /b 2
)

adb.exe get-state >nul 2>nul
if errorlevel 1 (
    echo [!!!] Device not available. Connect cable and USB debugging.
    exit /b 2
)

set FAILS=0

echo ============================================================
echo  Open Voyah verify (read-only) @VERSION@
echo ============================================================

adb.exe shell pm path ru.big.town.anative 2>nul | findstr /b /c:"package:" >nul
if errorlevel 1 (
    echo [!!!] Native not installed
    set /a FAILS+=1
) else (
    echo   [OK] Native
)

adb.exe shell pm path ru.big.town.restoremode 2>nul | findstr /b /c:"package:" >nul
if errorlevel 1 (
    echo [!!!] RestoreMode not installed
    set /a FAILS+=1
) else (
    echo   [OK] RestoreMode
)

set LEAVECAR=
for /f "delims=" %%i in ('adb.exe shell getprop persist.app.feature.leavecar 2^>nul') do set "LEAVECAR=%%i"
if not "%LEAVECAR%"=="true" (
    echo [! ] leavecar is "%LEAVECAR%" expected "true"
) else (
    echo   [OK] leavecar=true
)

adb.exe shell test -e /system/etc/permissions/privapp-permissions-ru.big.town.anative.xml 2>nul
if errorlevel 1 (
    echo [!!!] privapp permissions file missing
    set /a FAILS+=1
) else (
    echo   [OK] privapp permissions
)

adb.exe shell "test -e /system/etc/init/voyahtune.load.sh" 2>nul
if errorlevel 1 (
    echo [! ] voyahtune.load.sh absent - light kit or incomplete full install
) else (
    adb.exe shell "if [ -x /system/etc/init/voyahtune.load.sh ] && grep -qF '/data/local/bin/load.bin' /system/etc/init/voyahtune.load.sh 2>/dev/null && [ -r /system/etc/init/voyahtune.load.rc ] && grep -qF 'service voyahtune_load' /system/etc/init/voyahtune.load.rc 2>/dev/null; then echo READY; else echo BROKEN; fi" 2>nul | findstr /c:"READY" >nul
    if errorlevel 1 (
        echo [!!!] boot-hook BROKEN - re-run install.bat from this folder
        set /a FAILS+=1
    ) else (
        echo   [OK] boot-hook READY
    )
)

echo ============================================================
if "%FAILS%"=="0" (
    echo [OK] Required verify checks passed.
    echo      Manual: modes before/after, steering keys, split - see README.
    exit /b 0
)
echo [!!!] Verify failures: %FAILS%
echo       Recovery: run the SAME install.bat from this folder; see install.log.
exit /b 1
