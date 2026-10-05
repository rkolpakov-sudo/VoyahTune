# IMP-23 — Запрет сети в контуре авто: CI-grep, ключи в secrets, permissions-минимизация (SPEC L62)

Статус: реализован (WP4, см. DECISIONS IMP-23). Priority P0|S.

## Проверка

### 1. Network API в Native/RestoreMode — ЧИСТО

`scripts/network-audit.sh` сканирует 9 паттернов в `ru/big/town` обоих APK:

```
java.net.  javax.net.  HttpURLConnection  OkHttp  Retrofit
WebSocket  DatagramSocket  InetAddress  android.webkit.WebView.loadUrl
```

Результат: **0 hits** — первый-партийный код не использует сетевые API.
- `android.net.Uri` (10 файлов в Native, 3 в RestoreMode) — только content:// IPC (Android ContentProvider), не сеть.
- `java.io.BufferedReader` (NativeLog.java) — только чтение локального logcat субпроцесса.
- `com/sun/jna/Native.java` (bundled JNA 7.0.4) — `java.net.URL` для загрузки classpath-ресурсов (извлечение .so из JAR), не HTTP.

Добавлены паттерны для IMP-23: `grpc`, `WebView.(loadData|loadHtml)`.

### 2. Ключи — только в CI-secrets

- PEM-ключей, `.p12`/`.jks`/`.pfx`/`.key` файлов в репозитории нет.
- `ANDROID_KEYSTORE_B64`/`ANDROID_KEYSTORE_PASSWORD`/`ANDROID_KEY_ALIAS`/`ANDROID_KEY_PASSWORD` — только `${{ secrets.* }}` в `.github/workflows/ci.yml`.
- `fork.config.toml` L34: «JKS + пароли — только в GitHub Secrets, IMP-23».
- `build.gradle` читает `System.getenv()` — без переменных сборка unsigned (с предупреждением).

### 3. Permissions-минимизация при инвентаризации

`Installer/crates/installer-core/src/inventory.rs` проверяет:
- SDK version = 30 (Android 11)
- Architecture = arm64-v8a
- Обязательные компоненты: `canbus.service`, `keymanager.service`
- Идентификация оригинала/форка через signers

Избыточная информация не экспортируется: `remnants`/`foreign_files`/`problems` содержат только диагностически необходимые данные. `token` — sha256 от stable-полей (без секретов).

### 4. CI gate

`ci.yml` job `network-audit` запускает `scripts/network-audit.sh` — **любой hit → exit 1**.

## Риски / ограничения

- Паттерны покрывают явные network API; обфусцированные вызовы (Runtime.exec + curl/wget) не детектятся — контракт: форк не содержит таких скриптов.
- JNA bundling — third-party код, не наш; `URL.openStream()` читает classpath, не сеть.