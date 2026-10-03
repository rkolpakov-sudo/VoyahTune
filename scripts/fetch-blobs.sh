#!/bin/sh
# scripts/fetch-blobs.sh — скачать бинарные блобы из нашего ре-хоста и проверить по sha256.
# SPEC L101 (Шаг 1.3): «блок подтягивается из нашего ре-хоста по sha256 (пиннинг CI)».
#
# Использование:
#   scripts/fetch-blobs.sh                      # все блобы из blobs/BLOBS-SHA256.txt
#   scripts/fetch-blobs.sh PATH [PATH ...]      # только перечисленные пути (пространство имён:
#                                               #   blobs/frida/frida-inject и т.п.)
#   scripts/fetch-blobs.sh --verify             # ничего не качать, только проверить локальные файлы
#
# Переменные окружения:
#   BLOBS_BASE_URL  базовый URL ре-хоста (GitHub Release «плоский»: ассет = последний компонент
#                   пути, поэтому имена файлов обязаны быть уникальны — это проверяется при
#                   публикации release). По умолчанию:
#                   https://github.com/rkolpakov-sudo/VoyahTune/releases/download/blobs-v1
#
# Выход: 0 — все запрошенные блобы на месте и совпали; 1 — есть отсутствующие/несовпадающие/нескачанные.
set -eu

ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
LIST="$ROOT/blobs/BLOBS-SHA256.txt"
BLOBS_BASE_URL="${BLOBS_BASE_URL:-https://github.com/rkolpakov-sudo/VoyahTune/releases/download/blobs-v1}"

VERIFY_ONLY=0
FILTER=""

while [ $# -gt 0 ]; do
    case "$1" in
        --verify) VERIFY_ONLY=1 ;;
        -h|--help) sed -n '2,17p' "$0"; exit 0 ;;
        -*) echo "Неизвестный флаг: $1" >&2; exit 1 ;;
        *) FILTER="${FILTER}$1
" ;;
    esac
    shift
done

[ -f "$LIST" ] || { echo "Нет списка блобов: $LIST" >&2; exit 1; }

sha256_of() {
    if command -v sha256sum >/dev/null 2>&1; then
        sha256sum "$1" | awk '{print $1}'
    elif command -v shasum >/dev/null 2>&1; then
        shasum -a 256 "$1" | awk '{print $1}'
    else
        echo "Не найден ни sha256sum, ни shasum." >&2
        return 1
    fi
}

download() {
    # $1 url, $2 dst
    if command -v curl >/dev/null 2>&1; then
        curl -fsSL --retry 3 -o "$2" "$1"
    elif command -v wget >/dev/null 2>&1; then
        wget -q -O "$2" "$1"
    else
        echo "Нет ни curl, ни wget — скачать $1 невозможно." >&2
        return 1
    fi
}

wanted() {
    [ -n "$FILTER" ] || return 0
    printf '%s' "$FILTER" | grep -Fx -q -- "$1"
}

cd "$ROOT"

checked=0
downloaded=0
bad=0

while read -r hash path _rest; do
    [ -n "${path:-}" ] || continue
    case "$path" in \#*) continue ;; esac
    wanted "$path" || continue
    checked=$((checked + 1))

    if [ -f "$path" ] && [ "$(sha256_of "$path")" = "$hash" ]; then
        continue
    fi

    if [ "$VERIFY_ONLY" = 1 ]; then
        if [ -f "$path" ]; then
            echo "MISMATCH: $path" >&2
        else
            echo "MISSING:  $path" >&2
        fi
        bad=$((bad + 1))
        continue
    fi

    url="$BLOBS_BASE_URL/$(basename "$path")"
    tmp="$path.part.$$"
    mkdir -p "$(dirname "$path")"
    echo "fetch $path ← $url"
    if ! download "$url" "$tmp"; then
        rm -f "$tmp"
        echo "Не удалось скачать $url" >&2
        bad=$((bad + 1))
        continue
    fi
    if [ "$(sha256_of "$tmp")" != "$hash" ]; then
        rm -f "$tmp"
        echo "SHA-256 скачанного не совпал: $path" >&2
        bad=$((bad + 1))
        continue
    fi
    # Публикуем атомарно: читатели не увидят наполовину скачанный файл.
    mv -f "$tmp" "$path"
    downloaded=$((downloaded + 1))
done < "$LIST"

echo "blobs: checked=$checked downloaded=$downloaded bad=$bad base=$BLOBS_BASE_URL"
[ "$bad" -eq 0 ] || exit 1
[ "$checked" -gt 0 ] || { echo "Ни один блоб не совпал с фильтром." >&2; exit 1; }
