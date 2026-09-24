# LIVE-чек-лист: установка VoyahTune 3.7.1 на Sport+ 2026 (Фаза V)

**Статус подготовки:** ✅ READY — ждём подключения ПК ↔ ГУ (кабель Type-A↔A) и команду «делай».  
**Релиз:** full `3.7.1` · flat: `C:\VoyahTune` (32 файла) · light ZIP также в `Releases\dist\`  
**Эталон:** Voyah Free Sport+ 2026 (OD Sport+ 26)  
**Движок:** `install.sh` (порядок фаз не менять) · обёртка: `install-tui.sh` / `install-tui.bat`

---

## 0. Условия безопасности (обязательно перед «делай»)

| # | Условие | Кто подтверждает |
|---|---------|------------------|
| S1 | Авто **полностью остановлено**, селектор **P**, ручник **вкл** | пользователь |
| S2 | Кабель **USB Type-A ↔ Type-A** (USB 2.0); Type-C не подходит | пользователь |
| S3 | Питание ГУ/USB **не отключать** до конца (1–2 ребута) | пользователь |
| S4 | Не запускать приложения Open Voyah до **финальной** перезагрузки | агент/пользователь |
| S5 | Использовать **только** `C:\VoyahTune` (не подмешивать файлы других версий) | агент |

---

## 1. Подготовка выполнена (до подключения)

- [x] Среда: JDK 17, Android SDK, adb (platform-tools + bundled `C:\VoyahTune\adb.exe`)
- [x] `VoyahTune-3.7.1.zip` / `-light.zip` в `Releases\dist\`
- [x] MANIFEST full: **31/31 OK**; light: OK; `bash -n` all scripts OK; Apollo test PASS
- [x] Flat full распакован в **`C:\VoyahTune`** (короткий путь, README)
- [x] TUI dry-run: **exit 0** (bundle+MANIFEST+adb OK; «нет устройств» — ожидаемо)
- [x] Эмулятор **остановлен** — `adb devices` пуст, конфликт serial исключён
- [x] Удаление: `remove.bat` / `./remove.sh` **из той же папки** `C:\VoyahTune` (backup там)

---

## 2. Когда ГУ подключено — команда агенту

Пользователь даёт явную команду (например: **«делай установку»**).  
До команды агент **не** выполняет `adb root`, install, reboot, записи в `/system`.

### 2.1. Preflight (read-only, агент)

```bat
cd /d C:\VoyahTune
adb.exe devices
```

Ожидание:
- **ровно одно** устройство в статусе `device`
- `adb.exe shell getprop ro.build.version.release` / `ro.product.model` — сверить с Sport+
- при `unauthorized` — на ГУ подтвердить USB debugging (на экране)
- при 0 / offline — кабель/порт/драйвер; **не** запускать install

Далее (read-only dry-run):

```bat
install-tui.bat
```

или POSIX: `./install-tui.sh --dry-run` → должен быть **preflight OK + план 12 шагов**.

### 2.2. Живая установка (после явного «делай»)

**Рекомендуется full + TUI:**

```bat
cd /d C:\VoyahTune
install-tui.bat
```

(Меню: Install → confirm safety → движок `install.sh` с tee → `install.log` → verify.)

**Non-interactive (без меню):**

```bat
install.bat
```

или `./install-tui.sh --yes` / `./install.sh`.

### 2.3. Порядок фаз (движок — не ломать)

1. Локальный preflight файлов (без ADB)  
2. `adb root` + wait-for-device  
3. Preflight Apollo safe-keys **=0**  
4. Preflight владелец `WRITE_CANBUS` (`ru.big.town.anative` или пусто)  
5. `disable-verity` → при необходимости **1 reboot** → remount `/system`  
6. Backup → `./backup`  
7. Frida (full): rуль / VirtualDisplay / dormant Apollo  
8. Boot-hook `voyahtune.*.rc` (+ миграция legacy)  
9. Native.apk + privapp + leavecar + freeform + RestoreMode  
10. Меню **Yandex DNS** (опционально; иначе keep)  
11. **Финальная перезагрузка**  
12. `verify_post_install` (read-only)

### 2.4. После успеха

- [ ] Дождаться полной загрузки ГУ (не открывать Open Voyah раньше)
- [ ] `adb devices` снова `device`
- [ ] `./verify_post_install.sh` / `verify_post_install.bat` — read-only
- [ ] На ГУ: RestoreMode UI, Native (SET ALL MODES), режимы после сна
- [ ] DNS (опционально): `install-yandex-dns.bat` **только после** успешной установки
- [ ] Скопировать `C:\VoyahTune\install.log` → `Releases\logs\live-install-<date>.log`
- [ ] Обновить HISTORY.md (Фаза V: что сработало / что упало)

### 2.5. Откат / ошибка

| Симптом | Действие |
|---------|----------|
| `!!!` + `Do not reboot` / «Не перезагружайте» | **Не ребутить.** Восстановить ADB, повторить **тот же** install.sh из `C:\VoyahTune` |
| MANIFEST mismatch | Распаковать ZIP заново целиком в `C:\VoyahTune` |
| WRITE_CANBUS чужой владелец | Убрать конфликтующий пакет; install **не** продолжать |
| `/system` RO / EROFS | Следовать тексту консоли; повтор install идемпотентен |
| Полный откат | `remove.bat` / `./remove.sh` **из `C:\VoyahTune`** (не удалять `backup/` до конца) |

---

## 3. Что НЕ делаем без отдельной команды

- Порядок/логика фаз `install.sh` · `CanSender`/JNI/`vd_bypass`/`load.bin`/`.rc`
- `setenforce 1` вручную · Apollo master вручную · minify/R8
- light вместо full «на всякий» — **решение пользователя** перед «делай»
- Коммит/push — только по явной просьбе

---

## 4. Флейвор

| | full (подготовлен) | light |
|--|-------------------|--------|
| Путь | `C:\VoyahTune` (32 файла) | ZIP: `Releases\dist\VoyahTune-3.7.1-light.zip` |
| TUI | `install-tui.bat` / `.sh` | нет TUI → `install.bat` / `install.sh` |
| Frida/boot-hook | ✅ | ❌ |

**По умолчанию при команде:** full (эталонный комплект). Если нужен light — сказать в команде явно.

---

## 5. MAX CONTROL — протокол обратной связи (обязательно при работе с ГУ)

Директива пользователя 2026-09-24: **«Максимальная степень контроля и обратной связь»**.  
Во время live-сессии **каждое действие** сопровождается фиксацией; молчаливых пауз >10–15 с без статуса **нет**.

### 5.1. Каналы контроля (все пишутся параллельно)

| Канал | Файл | Что |
|-------|------|-----|
| **Статус-файл** | `Releases/logs/BUILD_STATUS.txt` | `phase=`, `step=N/12`, `updated=`, `adb=`, `msg=` |
| **TUI/engine log** | `C:\VoyahTune\install.log` | tee из install.sh (при запуске через TUI) |
| **Watchdog** | `Releases/logs/live-watchdog.log` | polling ADB/.boot каждые **3–5 с** |
| **Скрин/фокус** | по запросу | `dumpsys window` / screencap при UI-проверках |
| **Сообщение пользователю** | чат | короткий статус после **каждого** checkpoint |

### 5.2. Формат `BUILD_STATUS.txt` (live)

```
VoyahTune LIVE
updated=<ISO8601>
phase=<WAIT-CABLE|PREFLIGHT|INSTALL-RUN|REBOOT-1|WAIT-BOOT|INSTALL-RESUME|REBOOT-2|POST-VERIFY|DONE|FAIL>
step=<n>/12
adb=<empty|device|offline|unauthorized|multiple>
boot=<0|1|?>
engine_rc=<-|0|N>
msg=<одна строка, что происходит / что ждём>
next=<одна команда, которую делает агент>
```

Обновлять **не реже**:
- при каждом смене phase/step;
- каждые **5–10 с** во время active install (polling);
- каждые **15–20 с** в wait-boot / wait-device;
- **немедленно** при ошибке `!!!`, rc≠0, `Do not reboot`.

### 5.3. Checkpoint-карта (после каждого — сообщение в чат)

| CP | Когда | Подтверждение |
|----|-------|---------------|
| C0 | Команда «делай» получена | phase=PREFLIGHT |
| C1 | `adb devices` → 1×`device` | serial + model |
| C2 | Dry-run / TUI preflight OK | exit 0 |
| C3 | Safety S1–S5 подтверждены | до запуска движка |
| C4 | engine start | phase=INSTALL-RUN, step=1/12 |
| C5 | Apollo keys=0 OK | step=3 |
| C6 | WRITE_CANBUS preflight OK | step=4 |
| C7 | verity / RW / reboot#1 | phase=REBOOT-1 или skip |
| C8 | boot_completed=1 после #1 | phase=INSTALL-RESUME |
| C9 | backup/ создан | step=6 |
| C10 | Frida + boot-hook OK | step=7–8 |
| C11 | APK+priv-app OK | step=9 |
| C12 | DNS choice | step=10 |
| C13 | reboot#2 issued | phase=REBOOT-2 |
| C14 | boot_completed=1 после #2 | phase=POST-VERIFY |
| C15 | verify_post_install | phase=DONE / FAIL |
| C16 | install.log скопирован + HISTORY | конец сессии |

### 5.4. Watchdog (read-only, только мониторит)

Скрипт **не** пишет в ГУ, **не** запускает install/reboot. Только читает ADB и лог.

```powershell
# Releases/logs — запуск отдельным потоком/окном ДО install
powershell -File Docs\agent\live_watchdog.ps1
```

Что делает каждые 3–5 с:
1. `adb devices` → строка в watchdog.log + при смене состояния → **срочно** обновить BUILD_STATUS.
2. `getprop sys.boot_completed` (если device).
3. Размер/последние строки `C:\VoyahTune\install.log` (если растёт — движок жив).
4. Смена `phase` подсказывает следующий `next=`.
5. Таймаут: device пропал >30 с во время INSTALL-RUN → `phase=FAIL msg=ADB-LOST`.

### 5.5. Правила фидбека агенту → пользователю

1. **Короткий статус каждые 10–15 с** активной работы (что step, что ждём).
2. После reboot: **отдельное сообщение** «reboot issued, жду boot — polling 5 с», далее ticker до `boot_completed=1`.
3. Ошибка: **немедленно** — полный текст `!!!`, rc, «не перезагружать», план recovery.
4. Успех: phase=DONE + 3 bullet: verify, UI на ГУ, лог сохранён.
5. Если пользователь молчит >2 мин на危险 шаге (verity/reboot) — **повторить checkpoint**, не продолжать мутации молча.

### 5.6. STOP-гейты (максимальный контроль)

| Условие | Действие |
|---------|----------|
| ≠1 device / unauthorized / offline | **STOP**, phase=FAIL, чиним ADB |
| Не P / ручник / движение | **STOP** до подтверждения S1 |
| MANIFEST / Apollo / CANBUS preflight fail | **STOP** до /system |
| `Do not reboot` | **STOP**, не ребутить, повтор install |
| ADB lost >30 с mid-install | **STOP**, phase=FAIL, recovery из чек-листа |
| rc≠0 | **STOP**, сохранить install.log, разбор |

### 5.7. Запуск контроля (порядок)

```text
1) Watchdog уже может быть запущен (Docs\agent\live_watchdog.ps1) — read-only.
2) Пользователь подключает Type-A↔A + даёт команду.
3) Агент: preflight → сообщение C1–C3 в чат.
4) install-tui / install.bat.
5) Чат-heartbeat каждые 10–15 с + BUILD_STATUS phase.
6) Стоп watchdog: создать Releases\logs\watchdog.stop
```

---

*Создано: 2026-09-24. Установка на авто — только после явной команды пользователя (Фаза V).  
MAX CONTROL: 2026-09-24 — по директиве пользователя.*
