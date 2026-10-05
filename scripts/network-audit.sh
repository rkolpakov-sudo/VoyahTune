#!/usr/bin/env bash
# network-audit — P0 (SPEC L188, IMP-23): телеметрия/сеть в контуре авто запрещены.
# Сканирует перво-партийный код (ru/big/town) обоих APK.
# Блокирующий гейт с WP1: любой hit = exit 1.
set -u
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
FOUND=0

PATTERNS=(
  "java.net."
  "javax.net."
  "HttpURLConnection"
  "OkHttp"
  "Retrofit"
  "WebSocket"
  "DatagramSocket"
  "InetAddress"
  "android.webkit.WebView.loadUrl"
  "android.webkit.WebView.loadData"
  "grpc"
)

# WP1: перво-партийный код живёт в Gradle-деревьях (src-reconstructed переехал)
for dir in \
  "$ROOT/Native/app/src/main/java/ru/big/town" \
  "$ROOT/RestoreMode/app/src/main/java/ru/big/town"; do
  [ -d "$dir" ] || { echo "MISSING sources: $dir"; exit 1; }
  for p in "${PATTERNS[@]}"; do
    while IFS= read -r hit; do
      [ -z "$hit" ] && continue
      echo "NETWORK:$p -> $hit"
      FOUND=$((FOUND+1))
    done < <(grep -r -F -n --include="*.java" -e "$p" "$dir" 2>/dev/null | head -30)
  done
done

echo "network-audit: findings=$FOUND"
[ "$FOUND" -eq 0 ] || exit 1
exit 0
