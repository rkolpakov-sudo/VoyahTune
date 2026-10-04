#!/usr/bin/env python3
"""WP1: найти jadx-дубли — классы, которые есть и в наших исходниках, и во внешнем classpath.

D8/mergeDex падает на первом же дубле ("Type X is defined multiple times"), поэтому ловить
их по одному через пересборку крайне медленно. Утилита берёт словарь классов из уже
сгенерированного внешнего dex (intermediates/external_libs_dex/.../classes.dex), извлекает
FQCN всех .java в source-set и печатает пересечение.

Использование:
  python3 Utils/find_dup_classes.py --dex <classes.dex> --src <src/java-root> [--delete]

--delete удаляет дублирующие .java (внешняя копия остаётся в classpath, поэтому ссылки
на такие классы продолжают компилироваться). Выход: 0 — дублей нет; 1 — найдены.
"""
from __future__ import annotations

import argparse
import re
import struct
import sys
from pathlib import Path

PKG_RE = re.compile(r"^\s*package\s+([\w.]+)\s*;", re.M)


def dex_descriptors(path: Path) -> set[str]:
    data = path.read_bytes()
    if data[:4] != b"dex\n":
        raise SystemExit(f"не DEX-файл: {path}")
    string_ids_size, string_ids_off = struct.unpack_from("<II", data, 0x38)
    out: set[str] = set()
    for i in range(string_ids_size):
        off = struct.unpack_from("<I", data, string_ids_off + 4 * i)[0]
        # string_data_item: uleb128 utf16_size, затем MUTF-8, terminated by 0x00
        while data[off] & 0x80:
            off += 1
        off += 1
        end = data.index(b"\x00", off)
        if data[off : off + 1] == b"L" and data[end - 1 : end] == b";":
            out.add(data[off:end].decode("utf-8", "replace"))
    return out


def src_classes(root: Path) -> dict[str, Path]:
    classes: dict[str, Path] = {}
    for p in root.rglob("*.java"):
        try:
            text = p.read_text(encoding="utf-8", errors="replace")
        except OSError:
            continue
        m = PKG_RE.search(text)
        if not m:
            continue
        classes[f"{m.group(1)}.{p.stem}"] = p
    return classes


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--dex", required=True, type=Path)
    ap.add_argument("--src", required=True, type=Path)
    ap.add_argument("--delete", action="store_true")
    args = ap.parse_args()

    if not args.dex.is_file():
        raise SystemExit(f"нет dex: {args.dex} (соберите variant, чтобы он появился)")
    if not args.src.is_dir():
        raise SystemExit(f"нет каталога исходников: {args.src}")

    ext = dex_descriptors(args.dex)
    src = src_classes(args.src)
    dups = sorted(
        (fqcn for fqcn in src if f"L{fqcn.replace('.', '/')};" in ext),
        key=str.lower,
    )
    for fqcn in dups:
        print(f"DUP: {fqcn}  ({src[fqcn]})")
    print(f"external={len(ext)} source={len(src)} duplicates={len(dups)}")
    if not dups:
        return 0
    if args.delete:
        for fqcn in dups:
            src[fqcn].unlink(missing_ok=True)
        print(f"deleted {len(dups)} duplicate sources")
    return 1


if __name__ == "__main__":
    raise SystemExit(main())
