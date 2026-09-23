# Code Review: Установщики VoyahTune

**Область:** `Packaging/installer/**`, `make_release.sh`, `push.sh`, `Packaging/system/*`, `Packaging/tests/test_apollo_direct_only.sh`, `Packaging/README.md`. Только исследование; файлы не изменялись.
**Коммит:** `45beee4` (master). **Дата аудита:** 2026-09-23.

---

## Файловая таблица

| Файл | Назначение | Отличия/заметки |
|---|---|---|
| `full/install.sh` (646) | Полная установка: Frida-инфраструктура + boot-hook + Native + whitelist + freeform + DNS + reboot | См. диаграмму; атомарные per-file операции; snapshot/rollback boot-hook; интегрированное интерактивное DNS-меню |
| `full/install.bat` (572) | Windows-близнец full install | DNS **не** встроен (отдельный `install-yandex-dns.bat`); CANBUS owner при неопределенности — **WARN+continue** (в .sh — abort); rw-тест через push файла; сообщения только ASCII |
| `full/remove.sh` (331) | Полный откат | Apollo master/opt-in → 0 + sleep 3; DNS restore до компонентов; остановка `voyahtune_load` (`ctl.stop`+poll); legacy-init миграция; kill load.bin/frida-inject; symmetric backup для `apollo_tech.js` (`.absent`); удаление data-каталогов Native (лечение zygote `data_de/null`); `leavecar` не откатывается |
| `full/remove.bat` (337) | Windows-близнец | Аналогичен, ожидание остановки сервиса до 20 с |
| `light/install.sh` (215) | Без Frida/root-инъекций: только Native+whitelist+RestoreMode+DNS | Слабее full: `adb push` **без проверки exit-code и без атомарности/restorecon** (стр. 154, 159); `backup_pull` трактует любую ошибку pull как «absent»; отсутствуют хуки руля/дока/VD |
| `light/install.bat` (154) | Windows-близнец light | Те же пробелы push-контроля (стр. 89, 93); DNS отдельно |
| `light/remove.sh` / `.bat` (60/51) | Откат light | Не трогает init.logcat/Frida/RC/freeform/док/руль (их light не ставил); чистит whitelist/Native/data + apollo-ключи |
| `common/dns-overlay.sh` (176) | Host-helper (sourc'ится) | SHA-256 pin RRO, 5 обязательных функций, интерактивное stty-меню (стрелки), `external`/`broken` → не трогать, неинтерактивный запуск → keep |
| `common/dns-overlay-device.sh` (524) | Device-helper транзакции | Lock-dir, schema-состояние `/data/local/open_voyah/qgdns`, бэкап оригинала, атомарный `mv` + hash-verify, только API 30, проверка адресов eth0 T-Box, отказ при наличии `/vendor/overlay/config/config.xml`, **никогда не ребутит** |
| `common/dns-overlay.bat` (90) / `install-yandex-dns.bat` (74) | Windows DNS | install-yandex-dns — отдельный явный шаг после основной установки, проверяет on/off/external/broken, свой reboot |
| `make_release.sh` (534) | Сборка релиза | `set -e`, version-sanitize, lock-dir, SHA-256 DNS APK, ровно один вхождение hash в каждом helper, staging→atomic publish→commit-point, ZIP с rollback, CRLF+ASCII-верификация `.bat`, `@VERSION@`-штамп, payload-чеклист full/light |
| `push.sh` (10) | **Legacy dev-скрипт** | Жёсткие пути `/home/big/AndroidStudioProjects/...`, без обработки ошибок; в релиз не входит — риск случайного запуска |
| `system/voyahtune.load.rc` (21) | Boot-hook | `post-fs-data` → `setenforce 0` через `u:r:su:s0`; сервис `voyahtune_load` (late_start, root, `disabled`); `sys.boot_completed=1` → `enable` |
| `system/voyahtune.load.sh` (12) | Тело сервиса | `mkdir /data/local/bin`, `exec load.bin >> /data/local/tmp/voyah_load.txt` (**без ротации**) |
| `system/privapp-permissions-ru.big.town.anative.xml` (45) | Whitelist 34 привилегий | `WRITE_SECURE_SETTINGS`, `INJECT_EVENTS`, `ADD_TRUSTED_DISPLAY`, `FORCE_STOP_PACKAGES`, `DEVICE_POWER`, `REBOOT`, car-пермишены и др. RestoreMode-XML отсутствует (только anative) |

---

## Диаграмма потока `full/install.sh`

```
[ФАЗА 0 — локальный preflight, устройство НЕ изменяется]
  0.1 source ./dns-overlay.sh; проверка 5 функций; ydns_prepare_helper (adb в PATH,
      device-helper, SHA-256 DNS RRO APK)
  0.2 наличие 14 обязательных непустых ассетов (load.bin, 5 js, frida, rc/sh,
      init.logcat.original, оба APK, privapp xml)
  0.3 adb root ×2 + wait-for-device
        │ ошибка → exit 1 (до изменений)
        ▼
[ФАЗА 1 — Apollo direct-only safety (только Settings)]
  1.1 put+get-verify 0: legacy_hook_enabled, master, profile_supported, heartbeat
        │ любая ошибка → exit 1 (до /system)
        ▼
[ФАЗА 2 — владелец com.qinggan.permission.WRITE_CANBUS (dumpsys)]
  2.1 не объявлен → ok; owner=anative → ok; иначе/не определён → exit 1
        ▼
[ФАЗА 3 — записываемый /system]
  3.1 adb disable-verity
  3.2 system_is_writable() = remount + touch-тест
  3.3 RO → adb reboot  ★РЕБУТ №1★ → ждать boot_completed (≤60×5с) → sleep 3 → root ×2
  3.4 всё ещё RO → exit 1 (загрузчик/EROFS/verity) — /system не тронут
        ▼
[ФАЗА 4 — бэкап → ./backup/ (локально)]
  4.1 backup_pull ×8 (load.bin, 4 js, frida-inject, Native.apk, privapp xml):
      существующий non-empty backup → skip (оригинал священный);
      remote PRESENT → pull через .new+mv; ABSENT → skip; ERROR → exit 1
  4.2 backup_pull_with_absent для apollo_tech.js (симметрия: файл ИЛИ .absent-маркер)
        │ ошибка → exit 1 (до перезаписи)
        ▼
[ФАЗА 5 — Frida-инфраструктура → /data/local/bin (атомарно per-file)]
  5.1 install_required_data_file: push .new → chown/chmod → mv → test -f
      load.bin(755), 5×js(644), frida-inject(755)
      ★ state переживает ребут: файлы в /data/local (не затираются OTA /system) ★
        │ ошибка → exit 1 (оставлены новые data-файлы — идемпотентный re-run)
        ▼
[ФАЗА 6 — миграция legacy init.logcat.sh]
  6.1 state: MISSING | CLEAN → skip; LEGACY (marker «# init.logcat.sh Open Voyah:») →
      сохранить rollback-копию (валидация: shebang, logcat, нет marker, sh -n)
  6.2 источник чистого файла: backup/init.logcat.sh (если прошёл валидацию) иначе
      init.logcat.original.sh
  6.3 атомарный publish (.new → sh -n → restorecon → mv → sync) → verify CLEAN
      иначе rollback_legacy (LEGACY_INIT_MIGRATED=1→0)
        │ ошибка → exit 1
        ▼
[ФАЗА 7 — boot-hook (RC-сервисы)]
  7.1 stage .new ВНЕ /system/etc/init/ (init сканирует все regular files!)
  7.2 контент-валидация grep-ом (путь load.bin, post-fs-data, setenforce 0,
      имя сервиса, boot_completed, enable)
  7.3 snapshot текущего hook: .previous (cp -p) ИЛИ .absent-маркер
  7.4 publish: mv .new → /system/etc/init/voyahtune.load.{sh,rc} → restorecon → sync
  7.5 verify READY (те же grep)
      ├─ fail → boot_hook_rollback (порядок: при старом load.rc.absent сначала убрать
      │         новый composite, затем sh/setenforce; иначе sh/setenforce, старый rc —
      │         последним) → статус 1 (rollback ок) или 2 (rollback НЕ подтверждён)
      └─ status 1: boot_hook_final_state = ABSENT → вернуть legacy init.logcat;
                   READY → оставить RC, legacy НЕ возвращать;
                   PARTIAL/неизвестно → legacy НЕ возвращать (опасность двух boot-path)
         status 2 → exit 1 с «Не перезагружайте ГУ»
  7.6 успех → LEGACY_INIT_MIGRATED=0, удалить obsolete setenforce.rc, cleanup snapshot
        ▼
[ФАЗА 8 — Native + whitelist (атомарно, restorecon+sync)]
  8.1 /system/priv-app/Native/Native.apk (644)
  8.2 /system/etc/permissions/privapp-permissions-…xml (644)
        │ ошибка → exit 1
        ▼
[ФАЗА 9 — Settings/props]
  9.1 persist.app.feature.leavecar=true (если не true)
  9.2 enable_freeform_support=1, force_resizable_activities=1 (применятся после ребута)
        ▼
[ФАЗА 10 — adb install -r -g restore_mode.apk]  │ ошибка → exit 1
        ▼
[ФАЗА 11 — DNS-overlay]
  11.1 ydns_query_state ∈ {on,off,external,broken}
  11.2 choose (TTY → меню; неинтерактив → keep; external/broken → keep)
  11.3 install | disable | keep
        │ любая ошибка → exit 1 (финальный ребут отменён)
        ▼
[ФАЗА 12] adb reboot  ★РЕБУТ №2 (финальный)★
           активирует RC-hook, PM перечитает priv-app/whitelist, применится freeform
```

---

## Состояние между ребутами (install.sh)

| Событие | Когда | Что уже изменено | Что «ждёт» ребута |
|---|---|---|---|
| **Ребут №1** (только если RO) | Фаза 3, **до** бэкапов и записи файлов | Apollo-ключи=0 (Settings), disable-verity зафиксирован | dm-verity off → OverlayFS remount |
| Смерть процесса между фазами | Любой момент | Частично: например, Фаза 5 записала `/data/local/bin`, Фаза 6/7 не завершены | Маркера «install in progress» **нет**; единственный индикатор — `backup/` и pid-маркеры; восстановление = **повторный запуск того же install.sh** (идемпотентность: skip-существующих бэкапов, повтор atomic-шагов, повтор snapshot boot-hook) |
| **Ребут №2** (финальный) | Фаза 12, только при полном успехе | Всё записано | setenforce 0, запуск load.bin → инжект 4 целей, перечитка privapp-whitelist, freeform-настройки, DNS RRO |

**Отсутствие глобального trap/EXIT-rollback — сознательное:** ошибки между фазами дают `exit 1` с частичным, но переустановочным состоянием. Единственные настоящие rollback-цепочки: legacy init.logcat (со своим verify) и boot-hook (snapshot `.previous`/`.absent` + state-machine).

---

## Атомарность — что ломает/не гарантируется

**Гарантируется:**
- Per-file атомарность: stage-файл вне целевого сканируемого каталога → chown/chmod/`restorecon` → `mv` → `sync` → `test` (Фазы 5, 6, 7, 8).
- Бэкап неперезаписываем: существующий non-empty backup священный; для нового файла — симметрия наличия через `.absent`.
- Boot-hook: snapshot до publish, rollback с корректным порядком, `sync` после каждого rename, финальный grep-verify READY.
- DNS device-helper: lock-dir, hash-pin, atomic `mv`, оригинал в state-dir, внешний overlay не трогается.

**Ослабляет атомарность:**
1. **Нет транзакции между фазами:** сбой в Фазе 8+ оставляет новые Frida-файлы и boot-hook на месте; если RC-hook уже стоял от прошлой установки — при любом ребуте подхватятся новые js/load.bin. Инструкция «повторите installer до перезагрузки» — единственная защита.
2. **`light/install.sh` не проверяет `adb push`** (стр. 154, 159) и не делает `restorecon` — сбой push молча уйдёт в финальный reboot; метка SELinux на новый APK может не восстановиться (full это делает).
3. **light `backup_pull`** маскирует реальную ошибку pull как «файл отсутствует» — потеря бэкапа возможна без ошибки.
4. **Расхождение sh/bat:** неопределённый owner CANBUS — .sh abort, .bat continue; DNS — .sh интегрирован, .bat отдельно (для DNS задокументировано, для CANBUS — нет).
5. **remove восстанавливает из backup только `load.bin` и `frida-inject`**, а 5 js всегда `rm -f` без восстановления — если до установки лежали js прошлой версии, они не вернутся (противоречие «откат к состоянию ДО»).
6. `push.sh` в корне — без guard'ов, легко запустить по ошибке.

---

## Обработка ошибок/remove — сводка

- remove.sh: каждый блок при неудаче либо `exit 1` (до дальнейших удалений), либо с сообщением «Не перезагружайте ГУ; повторите remove»; остановка RC-сервиса при неудаче → rollback legacy-init; удаление RC-файлов до очистки транзакционных `.new/.previous/.absent`.
- «Do not reboot» появляется только когда rollback boot-hook/legacy не подтверждён (status 2 / PARTIAL) — корректная привязка риска двух boot-path.
- Финальный `adb reboot` отсутствует при любом `exit 1` — хорошо.

---

## Релизный процесс (`make_release.sh` + README)

- **`test_apollo_direct_only.sh`** — статические grep/порядковые проверки (`assert_before`): fail-closed opt-in **до** `disable-verity` во всех 4 установщиках; `load.bin` — direct-only ветка до `inject_verified_marker`; `apollo_tech.js` — opt-in до hash/OEM-резолюции, H97X не ставит generic callback, wake-фильтр по числовым ID 924/958; запрет TX28/TX29-callback в `ApolloTlcService`; `HAS_DIRECT_APOLLO=true` ровно в 2 флейворах; синтаксис `sh -n` + `node --check`. Это регрессионный каркас, который надо прогонять при любом правке установщиков/apollo.
- **`Packaging/README.md`** — источник истины: править только здесь, `Releases/` в .gitignore; плоская папка релиза; `@VERSION@` штамповать только через `make_release.sh`; DNS RRO с pinned SHA `c4694866…`; Apollo direct доступен обоим флейворам, legacy-хук — только full и только по ручному opt-in; старые релизы — по git-тегам. `README.txt` (пользовательский) фиксирует: Type-A↔Type-A кабель, «Do not reboot» при оборванном ADB, повторный запуск того же скрипта из той же папки (backup не смешивать).

---

## Список рисков (упорядочено по серьёзности)

1. **system_server (vd_bypass)** — крэш = soft-reboot всей системы (см. code-review-frida.md).
2. **`setenforce 0` на каждом буте** — SELinux выключен глобально; любая later-уязвимость в userland становится эксплуатируемее. Ничем не компенсируется.
3. **Версионная хрупкость имён** OEM-классов (Frida-хуки) — fail-soft, но функции молча отключаются.
4. **Связность `STOCK_PREFIX` ↔ `ffBlacklisted`** — ручной синхрон.
5. **Неограниченный рост логов инжекта** без ротации (кроме Apollo).
6. **Неатомарность между фазами install.sh** — смешанное состояние при обрыве; спасает только идемпотентный re-run и дисциплина «не перезагружать».
7. **light/install.sh: push без проверки rc + без restorecon + неатомарно** — худшее место всего комплекта.
8. **sh/bat-расхождение по CANBUS owner** (abort vs continue).
9. **frida-inject -e hang** — закрыт timeout/single-flight; остаточный риск минимален.
10. **Pinned SHA-256 Apollo** — обновление OEM APK ломает legacy-хук (fail-closed).
11. **Синтетический replay DOWN+UP** в steeringwheelkeys — тонкая логика.
12. **`Java.retain`/`$dispose` Drawable** в launcherdock (cap 64).
13. **`push.sh` в корне репозитория** — hardcoded-пути чужой машины.
14. **Откат remove неполон для js** (всегда `rm`, не restore).
15. **Широкие привилегии в whitelist** (`INJECT_EVENTS`, `ADD_TRUSTED_DISPLAY`, `DEVICE_POWER`, `REBOOT`, `CAR_MOCK_VEHICLE_HAL`).

---

## Возможности улучшений (минимальные/безопасные)

1. **`light/install.sh`: добавить `|| exit 1` на `adb push` native.apk/privapp + `restorecon`** — выровнять с full, 4 строки, без изменения логики.
2. **Ротация логов `inject_ret`** — аналогично Apollo (1 МБ), для `voyah_*.txt` и `voyah_load.txt`.
3. **Привести .bat к .sh по CANBUS owner** — при неопределённости owner'а `exit 1` в обоих (или зафиксировать в README, почему .bat мягче).
4. **remove.sh: восстанавливать js из backup по той же схеме, что `load.bin`/`frida-inject`** — устранить противоречие «полного отката», 5 строк.
5. **Автотест синхронности `STOCK_PREFIX` ↔ `ffBlacklisted`** — расширить `test_apollo_direct_only.sh`.
6. **light `backup_pull` различать ошибку и отсутствие** — переиспользовать remote-state пробу из full (`PRESENT/ABSENT/ERROR`).
7. **`push.sh`**: guard путей или перенос в `Utils/` (не трогает релиз).
8. **Сообщение об успехе в `full/install.sh`** перед финальным `adb reboot` — паритет с .bat.
9. **Вынести дублирующиеся функции** (legacy-init, backup, atomic-install, boot-hook) из `install.sh`/`remove.sh` в sourc'имый `common/install-lib.sh` — снижает дрейф .sh↔.bat и install↔remove (правка средняя, но только перенос без смены логики).
10. **Не предлагать:** глобальный `set -e`/транзакционный EXIT-rollback (сломает best-effort команды и сознательную идемпотентность), изменение hot-path vd_bypass, отказ от `setenforce 0` (архитектурное решение проекта).

---

**Итог:** комплект в целом написан опытно — per-file атомарность, snapshot/rollback boot-hook, fail-closed Apollo, verify-before-mark инжектов и static-тесты это подтверждают. Сосредоточенные зоны риска: system_server-хуки и версионная хрупкость (неустранима без живых голов), light-установщик с непроверяемым push, отсутствие межфазовой транзакции в full install и нероторируемый рост логов при сбое инжекта.
