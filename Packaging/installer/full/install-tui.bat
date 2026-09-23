@echo off
setlocal EnableExtensions DisableDelayedExpansion
rem install-tui.bat - interactive menu around full install.bat (ASCII/CRLF only).
rem Does not change engine phases. Logging uses PowerShell Tee-Object when available.
rem Preflight (G1/G2): bundle files before menu; device state before mutations. Exit 3 = stop before /system.
cd /d "%~dp0" || exit /b 1

rem --- G1: local bundle preflight (read-only, no ADB) ---
set "TUI_BUNDLE_MISSING=0"
for %%F in (adb.exe AdbWinApi.dll AdbWinUsbApi.dll install.bat remove.bat verify_post_install.bat ^
        load.bin steeringwheelkeys.js launcherdock.js multidisplay.js vd_bypass.js apollo_tech.js ^
        frida-inject-16.2.1-android-arm64 voyahtune.load.rc voyahtune.load.sh init.logcat.original.sh ^
        native.apk restore_mode.apk privapp-permissions-ru.big.town.anative.xml) do (
    if not exist "%%F" (
        echo [FAIL] missing: %%F
        set "TUI_BUNDLE_MISSING=1"
    ) else (
        for %%A in ("%%F") do if %%~zA LEQ 0 (
            echo [FAIL] empty: %%F
            set "TUI_BUNDLE_MISSING=1"
        )
    )
)
if "%TUI_BUNDLE_MISSING%"=="1" (
    echo.
    echo [STOP] Bundle incomplete. Re-extract the ZIP fully. Device was not changed.
    echo        See README.txt PREFLIGHT section.
    exit /b 3
)
echo [OK] bundle files present

rem --- G1b: MANIFEST.sha256 integrity (D6/G8); warn if absent, stop on mismatch ---
call :check_manifest
if errorlevel 1 (
    echo.
    echo [STOP] MANIFEST.sha256 verification failed. Bundle tampered or incomplete.
    echo        Re-extract the ZIP fully. Device was not changed.
    echo        See README.txt PREFLIGHT section.
    exit /b 3
)

:tui_main
cls
echo ============================================================
echo  Open Voyah installer TUI @VERSION@
echo ============================================================
echo   1  Install full (install.bat + install.log)
echo   2  Verify post-install (read-only; Full or Light profile)
echo   3  Remove / restore (remove.bat)
echo   4  Yandex DNS only (install-yandex-dns.bat)
echo   5  Exit
echo ------------------------------------------------------------
choice /c 12345 /n /m "Select option [1-5]: "
if errorlevel 5 goto :eof
if errorlevel 4 goto :dns
if errorlevel 3 goto :remove
if errorlevel 2 goto :verify
if errorlevel 1 goto :install
goto :tui_main

:install
echo.
echo [SAFETY] Vehicle in P, parking brake ON. Do not unplug USB.
echo          Cable must be USB Type-A to Type-A. Two reboots possible.
echo          Log file: install.log
choice /c YN /n /m "Start full install? [Y/N]: "
if errorlevel 2 goto :tui_main

call :preflight_device
if errorlevel 1 (
    echo.
    echo [STOP] Device preflight failed. install.bat was NOT started. Device not changed.
    echo        Fix ADB (cable Type-A^<-^>A, USB debugging), then retry.
    pause
    goto :tui_main
)

where powershell >nul 2>nul
if errorlevel 1 (
    echo powershell not found - running install.bat without tee log...
    call install.bat
    set "TUI_RC=%ERRORLEVEL%"
) else (
    echo Running install.bat with tee to install.log ...
    powershell -NoProfile -ExecutionPolicy Bypass -Command ^
      "cmd /c install.bat 2>&1 | Tee-Object -FilePath install.log; exit $LASTEXITCODE"
    set "TUI_RC=%ERRORLEVEL%"
)

echo.
if "%TUI_RC%"=="0" (
    echo [OK] install.bat exit 0. Device should reboot.
    echo      Do not open Open Voyah apps until reboot finishes.
    echo      Optional: select 2 for verify.
) else (
    echo [FAIL] install.bat exit %TUI_RC%. Log: install.log if tee was used.
    echo        Do not reboot if the log says "Do not reboot".
    echo        Fix the cause in README.txt, then run the SAME install.bat again.
)
echo.
pause
goto :tui_main

:verify
if not exist "verify_post_install.bat" (
    echo verify_post_install.bat not found in this folder.
    pause
    goto :tui_main
)
echo.
choice /c FLN /n /m "Verify profile: [F]ull [L]ight [N] cancel: "
if errorlevel 3 goto :tui_main
if errorlevel 2 (set "TUI_VERIFY_LIGHT=1") else (set "TUI_VERIFY_LIGHT=0")
call :preflight_device
if errorlevel 1 (
    echo [STOP] Device preflight failed. verify was NOT started.
    pause
    goto :tui_main
)
if "%TUI_VERIFY_LIGHT%"=="1" (
    call verify_post_install.bat --light
) else (
    call verify_post_install.bat
)
echo.
pause
goto :tui_main

:remove
echo.
echo [WARN] remove.bat restores from .\backup in THIS folder.
choice /c YN /n /m "Run remove.bat now? [Y/N]: "
if errorlevel 2 goto :tui_main
call :preflight_device
if errorlevel 1 (
    echo [STOP] Device preflight failed. remove.bat was NOT started. Device not changed.
    pause
    goto :tui_main
)
call remove.bat
echo.
pause
goto :tui_main

:dns
echo.
echo [INFO] Run Yandex DNS only after a successful full install.
choice /c YN /n /m "Run install-yandex-dns.bat now? [Y/N]: "
if errorlevel 2 goto :tui_main
if not exist "install-yandex-dns.bat" (
    echo install-yandex-dns.bat not found in this folder.
    pause
    goto :tui_main
)
call :preflight_device
if errorlevel 1 (
    echo [STOP] Device preflight failed. DNS install was NOT started. Device not changed.
    pause
    goto :tui_main
)
call install-yandex-dns.bat
echo.
pause
goto :tui_main

rem --- G2: read-only device preflight (exit 3 on fail) ---
:preflight_device
if exist "adb.exe" (
    set "TUI_ADB=adb.exe"
) else (
    where adb >nul 2>nul
    if errorlevel 1 (
        echo [FAIL] adb not found. Install Platform Tools or extract the release fully.
        exit /b 3
    )
    set "TUI_ADB=adb"
)
set "TUI_DEV_COUNT=0"
set "TUI_DEV_STATE="
for /f "skip=1 tokens=1,2" %%a in ('"%TUI_ADB%" devices 2^>nul') do (
    if not "%%a"=="" (
        set /a TUI_DEV_COUNT+=1
        set "TUI_DEV_STATE=%%b"
    )
)
if "%TUI_DEV_COUNT%"=="0" (
    echo [FAIL] no device: check Type-A^<-^>A cable, port, driver, USB debugging.
    exit /b 3
)
if not "%TUI_DEV_COUNT%"=="1" (
    echo [FAIL] %TUI_DEV_COUNT% devices connected - disconnect extras/emulators, or select one with adb -s serial per INSTALL_GUIDE.
    exit /b 3
)
if /i not "%TUI_DEV_STATE%"=="device" (
    echo [FAIL] device state=%TUI_DEV_STATE% - unlock HU and confirm RSA key (unauthorized), or reconnect (offline).
    exit /b 3
)
echo [OK] one device, state=device
exit /b 0

rem --- G1b: verify MANIFEST.sha256 if present (PowerShell Get-FileHash) ---
:check_manifest
if not exist "MANIFEST.sha256" (
    echo [WARN] MANIFEST.sha256 not found - integrity check skipped ^(rebuild release^)
    exit /b 0
)
where powershell >nul 2>nul
if errorlevel 1 (
    echo [WARN] powershell not found - MANIFEST.sha256 check skipped
    exit /b 0
)
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$bad=0; Get-Content -LiteralPath 'MANIFEST.sha256' | ForEach-Object { if ([string]::IsNullOrWhiteSpace($_)) { return }; $p = $_ -split '\s+',2; if ($p.Count -lt 2) { Write-Host ('[FAIL] bad line: ' + $_); $bad=1; return }; $h = $p[0].ToLower(); $f = $p[1]; if (-not (Test-Path -LiteralPath $f)) { Write-Host ('[FAIL] missing: ' + $f); $bad=1; return }; $a = (Get-FileHash -LiteralPath $f -Algorithm SHA256).Hash.ToLower(); if ($a -ne $h) { Write-Host ('[FAIL] hash mismatch: ' + $f); $bad=1 } }; if ($bad) { exit 1 } else { Write-Host '[OK] MANIFEST.sha256 verified'; exit 0 }"
exit /b %ERRORLEVEL%
