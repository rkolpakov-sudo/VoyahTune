#!/usr/bin/env bash
# detachment-audit — CI-гейт по SPEC L127 (WP4), запускается с WP1 в report-режиме.
# Блокирующие паттерны: runtime-код форка не должен ссылаться на контур оригинала.
# Whitelist: docs/original-fingerprint.md (детектор оригинала, L127).
#
# Режимы:
#   report (default) — печатает находки, выход 0  (WP1..WP3; инфо)
#   strict           — находки = exit 1            (WP4+, см. DECISIONS)
set -u
MODE="${1:-report}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

# Сканируемые директории runtime-кода (не docs/SPEC/fork.config [base])
# res-reconstructed добавлен: ресурсы со ссылками сообщества мигрируют в Gradle-дерево (WP1)
TARGETS=("src-reconstructed" "Packaging" "Utils" "Installer" "Native" "RestoreMode" "tests" "res-reconstructed")

# Один проход grep: все паттерны сразу (-F фиксированные строки)
GREP_ARGS=(
  -r -F -n --binary-files=without-match
  --exclude-dir=.git
  --exclude-dir=resources
  --exclude-dir=unknown
  --exclude="SPEC.md"
  --exclude="detachment-audit.sh"
  -e "storage.yandexcloud.net"
  -e "t.me/VoyahTune"
  -e "@VoyahTune"
  -e "drive2.ru"
  -e "Releases/ota/index.json"
)

FOUND=0
for t in "${TARGETS[@]}"; do
  dir="$ROOT/$t"
  [ -d "$dir" ] || continue
  while IFS= read -r hit; do
    [ -z "$hit" ] && continue
    case "$hit" in
      *docs/original-fingerprint.md*) continue ;;
    esac
    echo "DETACH -> $hit"
    FOUND=$((FOUND+1))
  done < <(grep "${GREP_ARGS[@]}" "$dir" 2>/dev/null | head -80)
done

# Корневая root-служба Updater: init-контракт и бинарь (L128 — удаление в WP4)
for f in "Packaging/payload-common/voyahtune.updater.rc"; do
  if [ -e "$ROOT/$f" ]; then
    echo "DETACH -> init-contract present: $f (remove in WP4, L128)"
    FOUND=$((FOUND+1))
  fi
done

echo "detachment-audit: findings=$FOUND mode=$MODE"
if [ "$MODE" = "strict" ] && [ "$FOUND" -gt 0 ]; then
  exit 1
fi
exit 0
