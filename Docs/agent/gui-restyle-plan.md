# VoyahTune — план restyle GUI (RestoreMode)

**Версия:** 0.1 · **Дата:** 2026-09-24  
**Статус:** черновик на согласование · **Код не менялся**  
**Эталон:** Voyah Free Sport+ 2026 · **Гейт:** любой `.xml`/`.java` — только после явного «да» по пункту (`AGENTS.md` §2.2)

---

## 1. Цель

Сделать GUI **привлекательнее** без:
- смены логики и IPC;
- миграции на Compose (отдельный трек, только по «да»);
- риска для hot-path (CAN/бут/install — не пересекаются с UI-ресурсами).

---

## 2. Текущее состояние GUI (факт, 2026-09-24)

| Параметр | Значение |
|----------|----------|
| Framework | Java + XML Views + Material3 (NoActionBar) |
| Layouts | **38** файлов (~4.3K строк XML) |
| Гигант | `activity_advance.xml` ≈ **2834 строки (~65%)** |
| Палитра | inline hex (в т.ч. `#000`), **без** M3 color system |
| ConstraintLayout | 1 layout из 38 |
| Jetpack Compose | нет |
| viewBinding | включён; код — `findViewById` |
| Тема | `values/styles.xml`, `themes.xml`, `colors.xml` |
| Native layouts | 8 файлов (вторичны; основной объём — RestoreMode) |

Ключевые файлы:
- `RestoreMode/app/src/main/res/layout/*.xml` (37) + `layout-land/activity_main.xml`
- `RestoreMode/app/src/main/res/values/{colors,themes,styles,strings}.xml`
- `Native/app/src/main/res/layout/*.xml` (8)

---

## 3. Инструмент (выбор)

| Вариант | Роль |
|---------|------|
| **Android Studio Layout Editor (Split)** | **основной**: code+design, drag&drop, preview устройств/тем |
| Figma / Sketch | только макет-референс; ручной перенос в XML |
| Jetpack Compose + @Preview | **вне объёма** этого плана (нужен отдельный «да» + миграция) |
| On-device / web WYSIWYG | отклонены (alpha, не реальный рендер) |

Условие: Android Studio с Android SDK; локальная сборка `./gradlew assembleDebug` для проверки.

---

## 4. Пакеты работ (WP) — по одному «да»

### WP-G1 — M3 tokens (низкий риск, высокий эффект)

**Что:** перевести палитру на Material You / M3 attributes; убрать inline hex из layout.

| Шаг | Файлы | Суть |
|-----|-------|------|
| G1.1 | `values/colors.xml` | токены: primary/secondary/surface/container, day+night |
| G1.2 | `values/themes.xml` | `Theme.VoyahTune.*` → parent Material3; подключить токены |
| G1.3 | layout (post) | `#RRGGBB` → `?attr/color*` / `@color/*` |
| G1.4 | `styles.xml` | свести к минимуму; дубли с themes разобрать |

**Не трогать:** `strings.xml` (кроме явных UX-подписей), id/viewBinding-имена, `onClickListener` в Java.

**Проверка:** сборка full+light; smoke: MainActivity, Advance, Settings — без crash; day/night preview.

**Риск:** низкий (ресурсы). **Регресс:** возможен сдвиг оттенков — сверка со скринами Sport+ 2026 «до».

---

### WP-G2 — Структура layout (средний риск)

**Что:** сделать редактируемым и гибким.

| Шаг | Действие |
|-----|----------|
| G2.1 | `activity_advance.xml`: вынести секции во **вложенные layout / `<include>`** (или merge-фрагменты-заглушки) — цель: файл < ~600 строк |
| G2.2 | Типовые экраны LinearLayout → **ConstraintLayout** (AS «Convert to ConstraintLayout») |
| G2.3 | `layout-land/activity_main.xml` — синхронизировать с portrait после G1/G2 |

**Ограничения:**
- **все `android:id` сохранить** — иначе `findViewById` в Java упадёт;
- не менять `android:visibility` дефолты без сверки с логикой;
- не трогать `AdvanceActivity.java` в этом WP (только если include потребует code-behind — отдельный «да»).

**Проверка:** unit-тесты Native+RestoreMode PASS; ручной обход всех Activity/фрагментов.

---

### WP-G3 — Визуальная полировка (после G1–G2)

- типографика: `typeScale` M3 (display/title/body/label);
- карточки/tiles главного экрана: elevation + shape corners;
- пустые состояния и прогресс (из UX-каталога R6/R7 — **после** C1/C2 по плану, не вклиниваться раньше гейта поведенческих багов);
- иконки: сверка с `hownews.md`/readme (не «лечить» без запроса).

---

### WP-G4 — (опционально) Compose pilot

- **Один** экран (кандидат: Settings или About) через `ComposeView` в существующем XML;
- только после закрытия G1–G2 и явного «да на Compose-pilot»;
- не мигрировать CAN/IPC-слой.

---

## 5. Порядок и гейты

```
G1 tokens  →  «да» →  G2 structure  →  «да» →  G3 polish  →  (опц.) G4 compose
     │                    │
     └── docs+preview     └── id-stable check + tests
```

| WP | Гейт |
|----|------|
| G1 | «делай G1» (ресурсы темы) |
| G2 | «делай G2» + подтверждение, что id не ломаем |
| G3 | «делай G3» после живого smoke G1/G2 на стенде/эмуляторе |
| G4 | отдельный явный «да» |

**Не входит:** правка `CanSender`/`ApplyEngine`/boot/install; изменение Messenger `MSG_*`; minify/R8.

---

## 6. Проверка (чеклист на «да»)

1. `gradlew.bat assembleFullRelease assembleLightRelease assembleRelease` (RestoreMode) — SUCCESS  
2. Unit tests Native + RestoreMode — PASS  
3. Ручной обход: Main, Advance, Settings, Split-related UI (без крашей)  
4. `git diff` — только ожидаемые `.xml` (+ возможно `themes`/`colors`); **id-список не уменьшился**  
5. На Sport+ 2026 (когда будет стенд): визуальный A/B «до/после»

---

## 7. Что НЕ делаем в этом плане

1. Полная миграция на Compose.  
2. Рефакторинг Java-логики «заодно».  
3. Переименование view id.  
4. Правка IPC/CAN/бут/install.  
5. Изменение `strings.xml`-контрактов, на которые завязан код (без grep-проверки).

---

## 8. Открытые вопросы (нужен твой выбор)

1. **Берём G1 (tokens) первым?** — да / нет / сначала другой WP  
2. Compose pilot (G4) — в плане или вынести?  
3. Эталон «красоты»: есть референс (скрин/сайт/машинный HMI) или «просто modern M3 dark»?

---

*Документация-only. Код — после явного «да» по пункту. HISTORY.md — в конце сессии.*
