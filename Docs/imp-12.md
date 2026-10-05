# IMP-12 — Пиннинг тулчейна + офлайн-верификатор payload (SPEC L54, P1|S-M)

Статус: реализован. WP5: пиннинг тулчейна, офлайн-верификатор payload.

## Требования SPEC

> **L54**: Пиннинг тулчейна; офлайн-верификатор payload в Utils/ (schema/хешы/
> подпись нашим ключом); подписанные metadata: SHA-256 артефактов + revision + runtime-хешы.

## Реализация

- **Пиннинг тулчейна**:
  - Rust: `Installer/rust-toolchain.toml` `channel = "1.98.1"`
  - Gradle: `gradle/wrapper/gradle-wrapper.properties`, `gradle/wrapper/gradle-8.12.0-bin.zip.sha256`
  - AGP: `build.gradle` — `com.android.application` version pinned
  - JDK: CI job `setup-java@v4` versions 17
- **Офлайн-верификатор**: `Utils/offline_payload_verify.py` — проверяет schema 4, buildRevision, артефакты
- **BUILD-INFO**: `Releases/dist/BUILD-INFO.json` содержит toolchain versions (Gradle, AGP, JDK, Rust) + хеши артефактов