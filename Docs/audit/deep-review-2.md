# Повторное глубокое код-ревью — баги, архитектура, безопасный UX

**Дата:** 2026-09-23 · **Коммит:** `45beee4` · **Режим:** READ-ONLY, код не изменялся.
**Эталон:** Voyah Free Sport+ 2026 (текущая реализация рабочая).
**Согласование:** любое исправление — только после явного «да» по пункту (`AGENTS.md` §2.2).

Связь с Фазой 0: пересечения отмечены `(=R##)`. Новые находки — основной интерес.

---

## 1. BUG — реальные ошибки кода

### 1.1 Native (приоритет)

| ID | Где | Суть | Severity | Минимальный фикс |
|----|-----|------|----------|------------------|
| **BUG-N01** | `SetModesService.java:756-760` | `logRequestReceiver` — `RECEIVER_EXPORTED` без permission: любой app может слать `REQUEST_LOG`/`LOGGING_SET` и **получать кольцевой лог Native** (CAN-метки, состояния). Частично пересекается с R4. | High | signature-permission или `RECEIVER_NOT_EXPORTED` + explicit |
| **BUG-N02** | `NativeLog.java:146-154` | Лог в world-readable `/sdcard/tmp/voyah_native_log.txt` | High | `getExternalFilesDir`/`getFilesDir` (share уже через FileProvider) |
| **BUG-N03** | `MainActivity.java:19-32` | Статики режимов (`driveMode`/`energy`/…) без `volatile`: пишет ApplyEngine, читает UI — возможен torn-снимок | Med | immutable-снимок + `volatile`/`AtomicReference` |
| **BUG-N04** | `GlobalVars.java:7` | `SAVE_CONTEXT` non-volatile → bg-поток теоретически видит null | Med | `volatile Context` |
| **BUG-N05** | `SetModesService.java:82-86,1047` | Star-button: `MSG_RESULT` шлётся **до** фактической постановки в CAN-очередь — UI разблокируется раньше отправки | Med | слать `MSG_RESULT` из `onComplete` user-command (как `applyNow`) |
| **BUG-N06** | `SetModesService.java:1051` | Пустой `SAVE_CONTEXT` → star-button **молча** no-op без лога | Low | fallback `getApplicationContext()` + `Log.w` |
| **BUG-N07** | `SetModesService.java:547-548` | `applyTheme`: guard `mode > 3`, UI/коммент — только 0..2 → `mode==3` уходит в `UiModeManager` | Low | `mode > 2` |
| **BUG-N08** | `MainActivity.java:117-130` | Пустая star-команда → `{{}}` (1 пустой frame) вместо `byte[0][]` → всегда `send=false` WARN | Low | `new byte[0][]` для пустой строки |
| **BUG-N09** | `DriveModeCanTransport.java:119-135` | `initialized=true` **до** проверки permission → если grant позже, re-init не случится | Low | `initialized` только после успеха schema+permission |
| **BUG-N10** | `MediaKeyPairDelivery.java:18,25` | `catch (Throwable ignored)` без лога — диагностика DOWN_ONLY «вслепую» | Low | один `Log.w` с outcome (не меняет CAN) |

### 1.2 RestoreMode (приоритет)

| ID | Где | Суть | Severity | Минимальный фикс |
|----|-----|------|----------|------------------|
| **BUG-R01** | `MainActivity/Advance/Trip/Logging` `registerReceiver(..., RECEIVER_EXPORTED)` | Оверлоуд с `flags` (int) — **API 33**, а minSdk 30 → `NoSuchMethodError` на API 30–32. **(=R20)** Для Sport+ 2026 (вероятно ≥33) маскирован; дорестайл — краш | High | `ContextCompat.registerReceiver(..., RECEIVER_EXPORTED)` + lint NewApi |
| **BUG-R02** | `layout/activity_main.xml:216` + `MainActivity:496-510` | Портретный layout: `onClick="onButtonClickApply"` **нет в MainActivity** → `IllegalStateException`; нет `mainContent` → NPE в insets; `setContentView` **до** `setRequestedOrientation` | High | выровнять portrait с land **или** placeholder + сначала orientation |
| **BUG-R03** | `MainActivity.java:296-300` | `data.toString()` без null-check в `onActivityResult` | Med | `if (data == null) return;` |
| **BUG-R04** | `MainActivity.java:560-577` | `cursor` без null-guard и без try/finally | Low | null-check + finally close |
| **BUG-R05** | `AdvanceActivityStarButton.java:41,91` | NPE после process death: `GlobalVars.sharedPreferences` инициализируется только в MainActivity | Med | локальный `getSharedPreferences` в активности |
| **BUG-R06** | `RestoreModeContentProvider.java:14-83` | **Новое:** state в полях класса, `query` на binder-пуле → конкурентные query **перемешивают** колонки (Native может получить смешанный снимок настроек) | Med | все переменные **локальными** внутри `query()` |
| **BUG-R07** | `AdvanceActivity.java:420-436` | **Новое:** CAN-форматтер: `\n` только при `j≥19` → строки <20 hex **слипаются**, >20 получают мусорные переносы; ручные переводы строк уничтожаются | Med | `append("\n")` после каждой строки; длину >20 отклонять/обрезать аккуратно (валидацию `%31` **не ломать** — только отображение формата) |
| **BUG-R08** | `AdvanceActivityStarButton.java:92-97,136-218` | Стартовый текст не валидируется; два редактора делят одни Save/Back | Low | `isValid(e1)&&isValid(e2)`; после `setText` — прогон watcher |
| **BUG-R09** | `AdvanceActivity.java:171-172` | Нет `onDestroy` → `applyTimeout` (12s) живёт после destroy → утечка Activity | Low | `onDestroy`: `uiHandler.removeCallbacksAndMessages(null)` |
| **BUG-R10** | `AdvanceActivity.java:1393,1419-1444` | `apolloPending` без таймаута → тумблеры Apollo вешаются без объяснения | Med | локальный timeout 5–10s + строка «Нет ответа Native» |
| **BUG-R11** | `TripHistoryActivity.java:120-134` | try/catch на **весь** цикл JSON → одна битая запись = пустой список | Low | try/catch на итерацию, skip битой |
| **BUG-R12** | `SplitStore.java:47-64` | **Новое:** исключение на элементе k → `out` усечён → следующий `save` **потеряет** остальные пресеты молча | Low-Med | try/catch на элемент; при фейле всего файла — не save обратно |
| **BUG-R13** | `MainActivity.java:827-834` | `presetIndex()` по значению → при дублях чужой пресет (fallback `presetIdx`) | Low | адресация только по `preset.id` |

### 1.3 Установщики / Frida (сводка полного отчёта агента: 27×BUG-I)

| ID | Где | Суть | Severity |
|----|-----|------|----------|
| **BUG-I01** | рабочее дерево vs git | **CRLF в checkout vs LF в git** — риск поведения .sh на разных ОС | High |
| **BUG-I04** | `light/remove.sh` | Нет RW-gate перед удалением | High |
| **BUG-I05** | `light/install.sh:154,159` | `adb push` без `|| exit 1` → **ложный success** (пересекается с R9) | High |
| **BUG-I03/I08** | `.sh` ↔ `.bat` | Расхождения: CANBUS owner (abort vs continue), DNS, таймауты | High |
| **BUG-I07** | `steeringwheelkeys.js` | Нет общего try/catch на hot-path — исключение = потеря перехвата | Med |
| **BUG-I19** | install.bat | `exit 0` после `!!!` в boot-hook failure-ветке → **ложный код успеха** | Med |

*Полный список 27 BUG-I / 14 UX-I / 9 ARCH-I — в отчёте подзадачи; при необходимости выгружу отдельным файлом.*

---

## 2. ARCH — архитектурные проблемы (описание, не «переписать»)

| ID | Проблема | Риск при «лечении» | Рекомендация |
|----|----------|-------------------|--------------|
| ARCH-N01 | **Три** независимых сериализации CAN: `NATIVE_SEND_LOCK`, `gCanTransactionMutex`, per-transport Apollo — **нет общего порядка** drive-mode → light → Apollo | Любой «общий fence» = hot-path | **Не трогать** без целевого проекта + тестов на авто |
| ARCH-N02 | Глобальный mutable state (static поля MainActivity, GlobalVars, sticky-токены) | Рефакторинг = regression risk | Только точечные фиксы race (BUG-N03/N04) по согласованию |
| ARCH-N03 | Messenger `MSG_*` — голые int без версии, дубли в 3 файлах RestoreMode | Смена нумерации ломает IPC | Единый `NativeProtocol` **без смены чисел** (refactor-only, после «да») |
| ARCH-N04 | Dual registration receivers в SetModesService (manifest + runtime) | Тонкое поведение sleep/wake | Документировать, не рефакторить |
| ARCH-R01 | Provider exported без permission **(=R3)** | Смена conditions IPC | Только согласование + живой тест Sport+ 2026 |
| ARCH-R02 | 5 receivers без permission **(=R4)** | То же | То же |
| ARCH-R04 | `DrivePreferences` god-object + позиционный контракт колонок | Перестановка колонки = молчаливый слом | `getColumnIndex` + новые ключи только в конец |
| ARCH-R05 | Индексная адресация dock/steer пресетов при наличии UUID | Смена формата prefs = миграция | Migrate на `presetId` + fallback на idx (обратная совместимость уже частично есть) |
| ARCH-R06 | `GlobalVars` static + non-static handler → утечка Activity | Вынос биндинга — средний рефактор | WeakReference handler — низкий риск, по «да» |
| ARCH-R07 | Мёртвый/вводящий в заблуждение код: `NowPlayingClient` (0 вызовов), `initIntentStarButton` кладёт extras **не в тот Intent**, `canChangeApolloMaster(){return false}` при живом UI и тексте ошибки про **скрытую** кнопку force-off | Путает при доработках | Чистка UI (см. UX-R11) — безопасно; **включение** master — нет |
| ARCH-I | sh↔bat drift, нет `install-lib.sh`, рост логов без ротации | Общий lib — средняя правка | Только после «да», без смены фаз |

---

## 3. UX — безопасные улучшения (car-risk None/Low, CAN-bytes не трогают)

### 3.1 Только документация / echo (можно без «рискованного» кода)

| ID | Что | Где |
|----|-----|-----|
| UX-S13 | readme FAQ: Q/A перепутаны (строки 31–33), тон FAQ | `readme.md` |
| UX-S14 | Противоречие «одна/две перезагрузки» | readme / README.txt / install.sh |
| UX-S16 | README.txt: не про вторую перезагрузку и light/full | `Packaging/README.txt` |
| UX-S17 | Кабель Type-A↔A не упомянут в корневом readme | `readme.md` |
| UX-S15 | Циклические ссылки readme ↔ PROJECT ↔ matrix про 4.1/7.1 OD | docs |
| UX-S1 | Нет `Installation full complete` в install.sh (bat печатает) | `install.sh:646` (только echo) |
| UX-S2 | «Do not reboot» / «Не перезагружайте» — разные формулировки | sh vs README.txt vs bat |
| UX-S3 | Прогресс фаз `[N/12]` — префикс к echo | install.sh / bat |
| UX-S4 | Где сохранить лог установки (`tee` / README) | docs + echo |

### 3.2 UI-код, car-risk = None/Low (только feedback, отправка не меняется)

| ID | Что | Где | car-risk |
|----|-----|-----|----------|
| **UX-R1** | «Применить» молчит при `!isBound` / невалидных командах → Snackbar | `AdvanceActivity` | None |
| **UX-R2** | Таймаут 12s без сообщения → «Не удалось подтвердить…» | `applyTimeout` | None |
| **UX-R3** | Тоггл-карточки игнорируют `ok=sendMessage…` → snack при fail | `MainActivity` | None |
| **UX-R4** | Подсказка формата CAN-редактора + Toast при блокировке «назад» | `AdvanceActivity` | None |
| **UX-R6** | Пустые состояния сплитов/логов | Main / Logging | None |
| **UX-R7** | Таймер поездки при `driveStartElapsed==0` показывает «дни с загрузки» | `MainActivity` | None |
| **UX-R8** | «Прогреть» при уже видимом fail — сразу причина, не «Запуск…» | `MainActivity` | None |
| **UX-R9** | Дефолт `showForcedEv`: Advance=true vs Main=false — рассинхрон UI | `bindShowSwitch` | None |
| **UX-R10** | Snackbar «Сервис не готов» в Advance (сейчас только Log.w) | `AdvanceActivity` | None |
| **UX-S7/S8** | Apply progress / disconnected Messenger — полный цикл feedback | Advance / Main | Low |
| **UX-S9/S10** | Статусы Apollo: единые строки + код ошибки в default | `buildApolloStatus` | None |
| **UX-S11** | Уведомления: единый префикс «VoyahTune · …», язык | 6 сервисов | None |
| **UX-R11/S12/S18** | Чистка: portrait layout (после grep), текст ошибки про скрытую force-off, hardcoded 1920×720 | resources | None |

### 3.3 TOP-10 безопасных UX (приоритет)

1. UX-S13 — readme FAQ (docs-only)
2. UX-S1 — «Installation complete» в install.sh (echo)
3. UX-R1/R10 — feedback «Применить»/«Сервис не готов» (UI)
4. UX-R2 — таймаут apply с сообщением (UI)
5. UX-S11 — notifications (строки)
6. UX-S14 — две перезагрузки в docs (docs-only)
7. UX-S3 — `[N/12]` в установщике (echo)
8. UX-S16 — README.txt про 2-ю перезагрузку (docs)
9. UX-R11 — мёртвый portrait / текст Apollo force-off (ресурсы/строки)
10. UX-R03/R06 — null-safety `data`/provider locals (код, низкий риск)

---

## 4. Чего НЕ делать (подтверждено повторным ревью)

- `CanSender`, кадры, `ModeSyncPolicy` constants, `native-lib.cpp` — **без отдельного проекта**.
- Общий CAN-fence между JNI и Binder (ARCH-N01) — только с живыми тестами.
- Включение Apollo master UI / `canChangeApolloMaster()→true` — меняет ADAS-entitlement.
- Permission на provider/receivers — только согласование + тест связи Native ⇄ RestoreMode на Sport+ 2026.
- Порядок фаз `full/install.sh`, vd_bypass hot-path, семантика boot-hook.
- Валидация `%31` → изменения того, **что** уходит в `customCommand` (отображение формата — можно аккуратно, BUG-R07).

---

## 5. Итог в цифрах

| Категория | Native | RestoreMode | Installer/Frida | UX (safe) |
|-----------|-------:|------------:|----------------:|----------:|
| BUG | 10 | 13 | 27 (сводка) | — |
| ARCH | 6 | 7 | 9 | — |
| Safe UX | 5 | 11 | 14 | 20 (UX-S*) |

**Новые vs Фазы 0:** BUG-N01/N03–N10, BUG-R06/R07/R09–R13, UX-R1–R11, BUG-I01/I03–I07/I19, UX-S1–S20.

**Следующий шаг (ждёт «да»):** выбрать пачку — например, **(A)** docs-only UX (S13–S17, S1) → **(B)** безопасный UI-feedback (R1–R3, R10) → **(C)** must-fix installer (I05, I04, I03/I08, I01) — каждый пункт отдельно с тройным анализом.
