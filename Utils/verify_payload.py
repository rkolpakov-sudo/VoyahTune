#!/usr/bin/env python3
"""WP1: верификация payload против payload/manifest.json (sha256 каждого артефакта).

Использование:  python3 Utils/verify_payload.py <unpacked-dir>
(unpacked-dir — каталог с manifest.json и файлами из payload zip)
Выход: 0 — все 29 артефактов совпали; 1 — расхождение/отсутствие.
"""
from __future__ import annotations

import hashlib
import json
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parents[1]


def sha256(path: Path) -> str:
    h = hashlib.sha256()
    with path.open("rb") as f:
        for chunk in iter(lambda: f.read(1 << 20), b""):
            h.update(chunk)
    return h.hexdigest()


def main() -> int:
    if len(sys.argv) != 2:
        print(__doc__)
        return 2
    unpacked = Path(sys.argv[1])
    manifest_path = REPO / "payload" / "manifest.json"
    manifest = json.loads(manifest_path.read_text(encoding="utf-8"))

    bad = 0
    for art in manifest["artifacts"]:
        path = unpacked / art["path"]
        if not path.exists():
            print(f"MISSING: {art['path']}")
            bad += 1
            continue
        if sha256(path) != art["sha256"]:
            print(f"MISMATCH: {art['path']}")
            bad += 1
    total = len(manifest["artifacts"])
    print(f"verified {total - bad}/{total} artifacts")
    if manifest.get("schema") != 4:
        print(f"schema != 4: {manifest.get('schema')}")
        bad += 1
    if manifest.get("buildRevision") != "62b74765a826375fe24b9bde207886011b7d1645":
        print(f"unexpected buildRevision: {manifest.get('buildRevision')}")
        bad += 1
    return 1 if bad else 0


if __name__ == "__main__":
    raise SystemExit(main())
