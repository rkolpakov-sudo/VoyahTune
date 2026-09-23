# Матрица совместимости VoyahTune

**Коммит:** `45beee4` (master). **Дата аудита:** 2026-09-23.
**Статус:** ⚠️ **черновик на основе документации и кода** — столбцы «Функции» требуют подтверждения живыми тестами на автомобилях (Фаза 2 плана: минимум 3 тестирования).

---

## 1. Прошивки / кузова (из документации)

| Версия прошивки / тип | Источник данных | Восстановление режимов | Режим энергии (шторка) | Рекуперация (шторка) | Автосвет | Дворники (сервисный) | Сплиты / freeform | Direct Apollo (Binder) | Frida-инъекции | Замечания |
|----------------------|-----------------|------------------------|------------------------|----------------------|----------|----------------------|-------------------|------------------------|----------------|-----------|
| **4.1 OD** | readme FAQ | ✅ (панель корректна) | ✅ восстанавливается | ✅ восстанавливается | ✅ | ✅ (v2.6+) | ❌/⚠️ нужен test | ✅ (фейвор full+light) | ✅ только full | Шторка/настройки рассинхрон — баг OEM-софта (не приложения) |
| **7.1 OD** | readme FAQ | ⚠️ частично | ❌ OEM не восстанавливает | ✅ восстанавливается | ✅ | ✅ | ❌/⚠️ нужен test | ✅ | ✅ только full | Только рекуперация в шторке; энергия — ограничение OEM listener'а |
| **Рестайл 2024 (тестовый стенд автора)** | readme, hownews | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ (v3.1+) | ✅ | ✅ | **Единственный официально тестированный** (Voyah Free OD Sport+ 26 в hownews post-v3.4) |
| **Дорестайл (Android 9)** | plan doc / community | ✅ | ✅ (по аналогии) | ✅ | ✅ | ✅ | ⚠️ | ✅ | ✅ | ADB over Wi-Fi доступен; сверить API level для RECEIVER_EXPORTED (риск R20) |
| **Прочие OD/PI/рест** | readme FAQ | ✅ если VCU принимает CAN | ⚠️ неизвестно | ⚠️ неизвестно | ⚠️ | ⚠️ | ⚠️ | ⚠️ | ⚠️ | «Версия прошивки неважна — важна готовность VCU принимать команды»; сообщество в drive2 |

**Легенда:** ✅ подтверждено документацией/кодом · ⚠️ не проверено / зависит от версии · ❌ не работает / не поддерживается · ✅/⚠️ — частично.

---

## 2. Требования к установке (из кода установщиков)

| Требование | Full | Light | Источник |
|------------|------|-------|----------|
| Кабель USB Type-A ↔ Type-A (USB 2.0) | ✅ | ✅ | README.txt, voyahchat |
| Type-C | ❌ не работает | ❌ | plan doc / community |
| `adb root` | ✅ | ✅ | install.sh Фаза 0 |
| `adb disable-verity` + writable `/system` | ✅ | ✅ | install.sh Фаза 3; reject: EROFS/locked bootloader |
| Две перезагрузки | ✅ | ✅ (если RO) | install.sh Фаза 3 + 12 |
| Frida (`frida-inject-16.2.1`) | ✅ | ❌ | make_release payload |
| boot-hook (`voyahtune.load.rc` + load.bin) | ✅ | ❌ | install.sh Фаза 5–7 |
| Кнопки руля / док / сплиты (Frida) | ✅ | ❌ | Inject scripts |
| Direct Apollo (Binder, без Frida) | ✅ | ✅ | `HAS_DIRECT_APOLLO=true` оба флейвора |
| Запись в `/system` (Native.apk + whitelist) | ✅ | ✅ | install.sh Фаза 8 |
| Владелец `WRITE_CANBUS` = anative | ✅ (sh: abort) | ✅ (sh: abort) | Фаза 2; **.bat: fail-closed abort (сверено с fix-all; R12 закрыт)** |
| Yandex DNS RRO overlay | опц. (меню/отдельный .bat) | опц. | dns-overlay helpers |
| Наличие `/vendor/overlay/config/config.xml` | → DNS отклоняется | то же | dns-overlay-device.sh |
| Наличие чужого overlay config | → DNS external/broken → keep | то же | dns-overlay |

---

## 3. Функции × флейворы (из кода BuildConfig)

| Функция | full | light | Зависимость |
|---------|------|-------|-------------|
| Восстановление режимов (CAN TX=77) | ✅ | ✅ | CanSender + JNI |
| Автосвет (датчик, TX=58) | ✅ | ✅ | LightSensorService |
| Сервисный режим дворников | ✅ | ✅ | WiperColdService |
| Прогрев батареи | ✅ | ✅ | BatteryHeatService |
| Статистика поездок | ✅ | ✅ | TripStatsService |
| Power Hold / leavecar | ✅ | ✅ | Settings.Global |
| Плавающая кнопка «Назад» | ✅ | ✅ | BackButtonService |
| Звук пешеходов OFF | ✅ | ✅ | CAN |
| Forced EV | ✅ | ✅ | provider + CAN |
| Direct Apollo TLC | ✅ | ✅ | ApolloTlcService (Binder) |
| Legacy Apollo hook (VehicleSetting) | ⚠️ opt-in | ❌ (скрыт) | `open_voyah_apollo_legacy_hook_enabled=1`; только full ставит apollo_tech.js |
| Сплиты / freeform (VD) | ✅ | ❌ | Frida vd_bypass + launcherdock |
| Замена системного дока | ✅ | ❌ | launcherdock.js |
| Кнопки руля (перехват SWC) | ✅ | ❌ | steeringwheelkeys.js |
| Мультидисплей whitelist | ✅ | ❌ | multidisplay.js |
| Кнопка звёздочки (CAN preset) | ✅ | ✅ | MSG_APPLY_DRIVE_MODES_STAR_BUTTON |
| Свои CAN-команды (UI) | ✅ | ✅ | `showCustomCommands` pref |
| Yandex DNS | ✅ | ✅ | common/ (отдельный шаг на Windows) |

---

## 4. Известные ограничения / баги (из FAQ и ревью)

| Проблема | Версии | Критичность | Природа | Источник |
|----------|--------|-------------|---------|----------|
| Режимы в шторке/настройках не синхронизированы | 4.1 OD (частично OK), 7.1 OD (только рекуперация) | Высокая (восприятие) | **OEM-софт** — их CAN-listener неполный | readme FAQ |
| Долгий запуск после снятия с охраны | все | Средняя (субъективно) | Не баг приложения (по readme) | readme FAQ |
| Панель быстрых настроек показывает неверный режим «электро+эко» | не уточнено | Низкая | OEM индикация; приборка корректна | readme FAQ |
| Восстановление режимов неполное на 7.1 OD | 7.1 OD | Высокая | См. выше | plan doc Фаза 4, readme |
| CAN-команды VIN-специфичны (`LDP95H…23`) | все | Высокая при переносе | Автор предупреждает: проверять через frida | Native/readme.md |
| Тестировано только рестайл 2024 | — | Процессный риск | Нет матрицы → **этот документ** | readme |

---

## 5. Пробелы для заполнения живыми тестами (Фаза 2)

- [ ] Дорестайл (API 29/30?): фактический API level → риск `RECEIVER_EXPORTED` (R20).
- [ ] 4.1 OD: полный набор функций (сплиты, док, руль) — сверить с таблицей §1.
- [ ] 7.1 OD: direct Apollo, сплиты, дворники.
- [ ] PI (pre-production/другой борт): launcherdock классы OD vs PI.
- [ ] H97C vs H97X Apollo profile (код различает 97C legacy / H97X diagnostic).
- [ ] Минимум 3 установки full + 1 light на разных прошивках по чек-листу Фазы 2.
- [ ] `basic_check.sh`: Native установлен, leavecar=true, сервис жив, CAN send smoke.

---

## 6. Источники

| Источник | Что взято |
|----------|-----------|
| `readme.md` (FAQ) | 4.1/7.1 OD, кабели, «тестировано на ресте 2024» |
| `hownews.md` | Версии функций v1.1→post-3.4, OD Sport+ 26 |
| `Packaging/README.md`, `README.txt` | Full/light matrix, EROFS/verity, DNS pin |
| `Native/app/build.gradle.kts`, `RestoreMode/...` | `IS_FULL`, `HAS_DIRECT_APOLLO` |
| `Packaging/inject/*.js`, `load.bin` | Зависимости Frida-функций |
| `Packaging/installer/**` | Фазы, CANBUS owner, light gaps |
| `Native/readme.md` | VIN-специфичность CAN |
| Код-ревью 2026-09-23 (docs/audit/*) | Риски R1–R26 |
