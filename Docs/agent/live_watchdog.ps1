# live_watchdog.ps1 - READ-ONLY monitor for VoyahTune live install (Phase V).
# Does NOT write to the head unit. Does NOT start install/reboot.
# Start: powershell -NoProfile -ExecutionPolicy Bypass -File Docs\agent\live_watchdog.ps1
# Stop:  create file Releases\logs\watchdog.stop  (or Ctrl+C)

$ErrorActionPreference = 'SilentlyContinue'
$root = 'C:\Projects\VoyahTune'
$statusPath = Join-Path $root 'Releases\logs\BUILD_STATUS.txt'
$wdLog = Join-Path $root 'Releases\logs\live-watchdog.log'
$engineLog = 'C:\VoyahTune\install.log'
$stopFlag = Join-Path $root 'Releases\logs\watchdog.stop'
$adbSdk = Join-Path $env:LOCALAPPDATA 'Android\Sdk\platform-tools\adb.exe'
$adbRel = 'C:\VoyahTune\adb.exe'
$intervalIdle = 5
$intervalActive = 3
$adbLostAfterSec = 30

function Write-Wd {
    param([string]$Line)
    $ts = (Get-Date).ToString('yyyy-MM-dd HH:mm:ss.fff')
    $row = "$ts | $Line"
    try { Add-Content -LiteralPath $wdLog -Value $row -Encoding UTF8 } catch { }
    Write-Output $row
}

function Set-Status {
    param(
        [string]$Phase,
        [string]$Step,
        [string]$Adb,
        [string]$Boot,
        [string]$EngineRc,
        [string]$Msg,
        [string]$Next
    )
    $now = (Get-Date).ToString('o')
    $lines = @(
        'VoyahTune LIVE'
        "updated=$now"
        "phase=$Phase"
        "step=$Step"
        "adb=$Adb"
        "boot=$Boot"
        "engine_rc=$EngineRc"
        "msg=$Msg"
        "next=$Next"
        "watchdog=running pid=$PID"
    )
    try { Set-Content -LiteralPath $statusPath -Value ($lines -join "`n") -Encoding UTF8 } catch { }
}

function Get-Adb {
    $exe = $null
    if (Test-Path -LiteralPath $adbRel) { $exe = $adbRel }
    elseif (Test-Path -LiteralPath $adbSdk) { $exe = $adbSdk }
    if (-not $exe) {
        return @{ ok = $false; text = 'adb-missing'; state = 'no-adb'; exe = $null }
    }
    $out = (& $exe devices 2>&1 | Out-String)
    $devs = @()
    foreach ($l in ($out -split "`r?`n")) {
        if ($l -match '^(\S+)\s+(device|unauthorized|offline|recovery|sideload)\b') {
            $devs += [pscustomobject]@{ Serial = $Matches[1]; State = $Matches[2] }
        }
    }
    $state = 'empty'
    if ($devs.Count -eq 1) { $state = $devs[0].State }
    elseif ($devs.Count -gt 1) { $state = 'multiple' }
    $text = ''
    if ($devs.Count -gt 0) { $text = (($devs | ForEach-Object { "$($_.Serial):$($_.State)" }) -join ';') }
    return @{ ok = $true; text = $text; state = $state; exe = $exe; count = $devs.Count }
}

function Get-Boot {
    param([string]$AdbExe)
    if (-not $AdbExe) { return '?' }
    $v = (& $AdbExe shell getprop sys.boot_completed 2>$null | Out-String)
    $v = ($v -replace '\r', '').Trim()
    if ($v -eq '1') { return '1' }
    if ($v -eq '') { return '0' }
    return $v
}

function Get-EngineInfo {
    if (-not (Test-Path -LiteralPath $engineLog)) {
        return @{ exists = $false; size = [int64]0; phaseHint = ''; last = '' }
    }
    $fi = Get-Item -LiteralPath $engineLog
    $tail = ''
    try {
        $all = @(Get-Content -LiteralPath $engineLog -Tail 20 -Encoding UTF8)
        $tail = ($all -join ' | ')
        if ($tail.Length -gt 400) { $tail = $tail.Substring($tail.Length - 400) }
    } catch { }
    $hint = ''
    if ($tail -match 'Do not reboot') { $hint = 'HOLD-NO-REBOOT' }
    elseif ($tail -match '!!!') { $hint = 'ENGINE-ERROR' }
    elseif ($tail -match 'Installation complete') { $hint = 'ENGINE-DONE' }
    elseif ($tail -match 'disable-verity') { $hint = 'VERITY-REBOOT' }
    elseif ($tail -match '===') { $hint = 'ENGINE-RUN' }
    return @{ exists = $true; size = $fi.Length; phaseHint = $hint; last = $tail }
}

Write-Wd "WATCHDOG START pid=$PID"
$prevState = ''
$prevSize = [int64](-1)
$adbLostSince = $null
$bootWas = '?'
$phase = 'WAIT-CABLE'
$next = 'wait cable + user command'

Set-Status -Phase 'WAIT-CABLE' -Step '-/12' -Adb 'init' -Boot '?' -EngineRc '-' -Msg 'watchdog started' -Next 'wait for user command + cable'

while ($true) {
    if (Test-Path -LiteralPath $stopFlag) {
        Write-Wd 'STOP flag present - exit'
        break
    }

    $adb = Get-Adb
    $adbState = 'no-adb'
    if ($adb.ok) { $adbState = $adb.state }
    $boot = '?'
    if ($adbState -eq 'device') { $boot = Get-Boot $adb.exe }

    $eng = Get-EngineInfo
    $key = "$adbState|$boot|$($eng.size)|$($eng.phaseHint)"

    if ($eng.exists -and $eng.size -gt 0 -and $adbState -ne 'device') {
        if (-not $adbLostSince) { $adbLostSince = Get-Date }
        elseif (((Get-Date) - $adbLostSince).TotalSeconds -gt $adbLostAfterSec) {
            Write-Wd "FAIL ADB-LOST > ${adbLostAfterSec}s during engine log present"
            Set-Status -Phase 'FAIL' -Step '?' -Adb $adbState -Boot $boot -EngineRc '-' -Msg 'ADB-LOST during install' -Next 'restore cable/adb; re-run same install.sh'
            $adbLostSince = $null
        }
    } else {
        $adbLostSince = $null
    }

    if ($key -ne $prevState) {
        $msg = "state adb=$adbState boot=$boot eng=$($eng.phaseHint) size=$($eng.size) devices=$($adb.text)"
        Write-Wd $msg

        $phase = 'WAIT-CABLE'
        $step = '-/12'
        $next = 'poll every 3-5s'

        if ($eng.phaseHint -eq 'ENGINE-DONE') {
            $phase = 'POST-VERIFY'
            $next = 'verify_post_install + copy log'
        } elseif ($eng.phaseHint -eq 'HOLD-NO-REBOOT') {
            $phase = 'FAIL'
            $next = 'DO NOT REBOOT - restore ADB, re-run install'
        } elseif ($eng.phaseHint -eq 'ENGINE-ERROR') {
            $phase = 'FAIL'
            $next = 'read install.log !!! lines; recovery'
        } elseif ($eng.phaseHint -eq 'VERITY-REBOOT') {
            $phase = 'REBOOT-WAIT'
            $next = 'poll sys.boot_completed every 3s'
        } elseif ($eng.exists -and $eng.size -gt 0) {
            if ($adbState -eq 'device' -and $boot -eq '1') { $phase = 'INSTALL-RUN' }
            elseif ($adbState -eq 'device') { $phase = 'WAIT-BOOT' }
            elseif ($adbState -eq 'empty') { $phase = 'REBOOT-WAIT' }
            else { $phase = 'INSTALL-RUN' }
            $next = 'tail engine; heartbeat user every 10-15s'
        } elseif ($adbState -eq 'device') {
            $phase = 'PREFLIGHT'
            $next = 'device present - dry-run / await command'
        } elseif ($adbState -eq 'multiple') {
            $phase = 'FAIL'
            $next = 'multiple devices - disconnect extra'
        } elseif ($adbState -eq 'unauthorized' -or $adbState -eq 'offline') {
            $phase = 'FAIL'
            $next = "adb=$adbState - fix on HU screen/cable"
        }

        Set-Status -Phase $phase -Step $step -Adb $adbState -Boot $boot -EngineRc '-' -Msg $msg -Next $next

        if ($boot -ne $bootWas) {
            Write-Wd "boot_completed $bootWas -> $boot"
            $bootWas = $boot
        }
        $prevState = $key
    } else {
        if ($eng.exists -and $eng.size -ne $prevSize) {
            Write-Wd "engine log size $prevSize -> $($eng.size)"
            $prevSize = $eng.size
        }
    }

    $sleep = $intervalIdle
    if ($eng.exists -and $eng.size -gt 0) { $sleep = $intervalActive }
    if ($phase -eq 'REBOOT-WAIT' -or $phase -eq 'WAIT-BOOT' -or $phase -eq 'INSTALL-RUN') { $sleep = 3 }
    Start-Sleep -Seconds $sleep
}

Write-Wd 'WATCHDOG END'
