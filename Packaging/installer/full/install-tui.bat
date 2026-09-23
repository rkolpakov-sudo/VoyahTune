@echo off
setlocal EnableExtensions DisableDelayedExpansion
rem install-tui.bat - interactive menu around full install.bat (ASCII/CRLF only).
rem Does not change engine phases. Logging uses PowerShell Tee-Object when available.
cd /d "%~dp0" || exit /b 1

:tui_main
cls
echo ============================================================
echo  Open Voyah installer TUI @VERSION@
echo ============================================================
echo   1  Install full (install.bat + install.log)
echo   2  Verify post-install (read-only)
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
call verify_post_install.bat
echo.
pause
goto :tui_main

:remove
echo.
echo [WARN] remove.bat restores from .\backup in THIS folder.
choice /c YN /n /m "Run remove.bat now? [Y/N]: "
if errorlevel 2 goto :tui_main
call remove.bat
echo.
pause
goto :tui_main

:dns
echo.
echo [INFO] Run Yandex DNS only after a successful full install.
choice /c YN /n /m "Run install-yandex-dns.bat now? [Y/N]: "
if errorlevel 2 goto :tui_main
call install-yandex-dns.bat
echo.
pause
goto :tui_main
