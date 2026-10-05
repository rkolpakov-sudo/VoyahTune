# IMP-15 — Голос: prewarm по ACC ON (R10/D10, P1|M)

Статус: в разработке. Priority P1|M. WP3/WP7.

## Проблема

R10: Голосовой помощник тратит 10-20 секунд на прогрев после первого вызова (загрузка
модели zipformer-ru-0.54-int8, компиляция JIT, инициализация DeepFilter). Это время
отсчитывается с момента открытия VoiceActivity — пользователь ждёт.

## Решение

Заранее начинать прогрев по сигналу ACC ON (CarPowerManager state 6), а не при первом
открытии VoiceActivity. К моменту, когда пользователь открывает голосовой помощник,
модели уже прогреты.

**Текущая архитектура**:
- `VoiceWarmupService` (RestoreMode) начинает прогрев при `onActivityStarted` — при
  старте любой Activity, что поздно: пользователь уже взаимодействует.
- Модели уже загружаются, но с 10-20с латентностью для первого вызова.

**Новая архитектура**:
- `SetModesService` (Native): при `CarPowerManager state 6` (ACC ON) в
  `handlePowerStateChanged()` запускает таймер 15с (10с запас модели + 5с запас).
- По таймеру → Intent `ru.big.town.restoremode.VoiceWarmupService` с действием
  `ACTION_VOICE_PREWARM`.
- `VoiceWarmupService` (RestoreMode): `onStartCommand()` проверяет intent action.
  Если `ACTION_VOICE_PREWARM` → вызывает `update()` (существующий метод, проверяет
  `voiceAssistantEnabled` и запускает `VoiceRecognizer.keepWarm()`).
- Если пользователь уже открыл VoiceActivity (активность стартовала), warmup уже идёт →
  второй запуск `keepWarm()` не делает ничего (guard `VoiceEngineCache`).

**Защита**:
- Таймер отменяется при `isSleepOrShutdownState()` — выключение.
- Если `voiceAssistantEnabled == false` → `update()` завершается без загрузки.
- Идемпотентность `VoiceEngine.keepWarm()`: повторный вызов не загружает модели.
- Только одна копия таймера на wake-цикл (guard через `flushTimers()` на sleep).

## Изменения

| Файл | Изменение |
|------|-----------|
| `Native/app/src/main/java/ru/big/town/anative/SetModesService.java` | В `handlePowerStateChanged()`: при wake state 6 → schedule `VOICE_PREWARM_DELAY_MS=15000` callback; отмена на sleep. |
| `RestoreMode/app/src/main/java/ru/big/town/restoremode/VoiceWarmupService.java` | Новое действие `ACTION_VOICE_PREWARM`; `onStartCommand()` обрабатывает его. |
| `Docs/behavior-matrix.md` | Строка R10/D10. |

## Тесты

- Unit: `SetModesService` power state 6 → таймер запущен; power sleep → таймер отменён.
- Unit: `VoiceWarmupService` ACTION_VOICE_PREWARM → `update()` вызван.
- Интеграционные: голос после ACC ON без открытия VoiceActivity (модели прогреты).

## Риски / ограничения

- Таймер 15с — эмпирическое значение. Если модель грузится дольше, prewarm не успевает.
- `CarPowerManager state 6` может прийти до готовности restoremode (гонка при загрузке):
  нужен guard на регистрацию `VoiceWarmupService` (проверка `getContext().getService()`).
- Intent Native→RestoreMode может не дойти при загрузке — fallback: `VoiceActivity` как
  сейчас, warmup по `onActivityStarted`.
- R10/D10: поведение меняется только для голоса (prewarm) — не затрагивает другие каналы.