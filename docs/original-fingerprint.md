# Отпечатки подписи оригинальных APK (payload 3.22.0)

Инструмент: `apksigner verify --print-certs` (Android build-tools 35.0.0).
`keytool -printcert -jarfile` сообщил «Not a signed jar file» — подписи используют
APK Signature Scheme v2/v3 (JAR-подписи v1 отсутствуют).

Назначение: детектор оригинала в WP6 (L141): package name + отпечаток = оригинал.

## native.apk

- Package: `ru.big.town.anative` (см. recipe: `/system/priv-app/Native/Native.apk`)
- Signer #1 certificate DN: `C=US, O=Android, CN=Android Debug`
- Signer #1 certificate SHA-256 digest: `1aa9ac5067106888c4565a678339debd2ee2a40222063bb5a91a83fbaa03d05b`

## restore_mode.apk

- Package: `ru.big.town.restoremode` (manifest `recipe.packages`)
- Signer #1 certificate DN: `C=US, O=Android, CN=Android Debug`
- Signer #1 certificate SHA-256 digest: `1aa9ac5067106888c4565a678339debd2ee2a40222063bb5a91a83fbaa03d05b`

## voyahtune-updater.apk

- Destination: `/system/priv-app/VoyahTuneUpdater/VoyahTuneUpdater.apk`
- Signer #1 certificate DN: `C=US, O=Android, CN=Android Debug`
- Signer #1 certificate SHA-256 digest: `1aa9ac5067106888c4565a678339debd2ee2a40222063bb5a91a83fbaa03d05b`

## voyahtune-ui-next.apk

- **Идентичен voyahtune-updater.apk** (sha256 совпадает: `ce56351e…`)
- Signer #1 certificate DN: `C=US, O=Android, CN=Android Debug`
- Signer #1 certificate SHA-256 digest: `1aa9ac5067106888c4565a678339debd2ee2a40222063bb5a91a83fbaa03d05b`

## dns.apk

- Роль: DNS-overlay (конфигурация, опция установки D11)
- Signer #1 certificate DN: `C=US, O=Android, CN=Android Debug`
- Signer #1 certificate SHA-256 digest: `17c1ffff147efdb27d576cee66acb46384cb40f981ca274ff522515b4d75483b`

## Наблюдения

- Все системные APK подписаны **debug-ключом вендора** (не production): отпечаток
  `1aa9ac50…` общий для native/restore_mode/updater — признак «собрано вручную».
- `dns.apk` подписан другим debug-ключом (`17c1ffff…`).
- Отпечатки фиксируются как есть; смена подписи на наш ключ — только в WP1 (CI-secrets).
