#!/usr/bin/env python3
"""WP1 / SPEC L101 (Шаг 1.3): regen и verify manifest.json собранного payload.

Использование:
  python3 Utils/build_payload_manifest.py <staging-dir> --version X.Y.Z --revision <git-sha>
  python3 Utils/build_payload_manifest.py <staging-dir> --verify

staging-dir — каталог с файлами payload в каноническом виде (<staging>/common/* и т.п.,
пути берутся из artifacts[].path базового манифеста). Скрипт:
  build:  пересчитывает sha256/size каждого артефакта по файлам staging,
          проставляет releaseVersion/buildRevision, пишет <staging>/manifest.json;
  verify: сверяет <staging>/manifest.json с фактическими файлами.

Выход: 0 — ок; 1 — расхождение/отсутствие; 2 — ошибка аргументов.
"""
from __future__ import annotations

import argparse
import hashlib
import json
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parents[1]
BASE_MANIFEST = REPO / "payload" / "manifest.json"


def sha256(path: Path) -> str:
    h = hashlib.sha256()
    with path.open("rb") as f:
        for chunk in iter(lambda: f.read(1 << 20), b""):
            h.update(chunk)
    return h.hexdigest()


def iter_files(staging: Path, artifacts: list[dict]):
    for art in artifacts:
        yield art, staging / art["path"]


def cmd_build(staging: Path, version: str, revision: str) -> int:
    base = json.loads(BASE_MANIFEST.read_text(encoding="utf-8"))
    expected = {art["path"] for art in base["artifacts"]}
    bad = 0
    for art, path in iter_files(staging, base["artifacts"]):
        if not path.is_file():
            print(f"MISSING: {art['path']}", file=sys.stderr)
            bad += 1
            continue
        art["sha256"] = sha256(path)
        art["size"] = path.stat().st_size
    if bad:
        print(f"build manifest: {bad} артефактов отсутствуют в staging", file=sys.stderr)
        return 1
    # Контракт жёсткий: в staging не должно быть файлов сверх манифеста (иначе лишнее
    # попадёт в раздаваемый архив payload).
    actual = {
        p.relative_to(staging).as_posix()
        for p in staging.rglob("*")
        if p.is_file() and p.name != "manifest.json"
    }
    for extra in sorted(actual - expected):
        print(f"EXTRA: {extra}", file=sys.stderr)
        bad += 1
    if bad:
        print(f"build manifest: {bad} лишних файлов в staging", file=sys.stderr)
        return 1
    base["releaseVersion"] = version
    base["buildRevision"] = revision
    out = staging / "manifest.json"
    out.write_text(json.dumps(base, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"manifest.json: {len(base['artifacts'])} артефактов, version={version}, revision={revision}")
    return 0


def cmd_verify(staging: Path) -> int:
    manifest_path = staging / "manifest.json"
    if not manifest_path.is_file():
        print(f"MISSING: {manifest_path}", file=sys.stderr)
        return 1
    manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
    if manifest.get("schema") != 4:
        print(f"MISMATCH: schema={manifest.get('schema')!r} (ожидается 4)", file=sys.stderr)
        return 1
    bad = 0
    for art, path in iter_files(staging, manifest["artifacts"]):
        if not path.is_file():
            print(f"MISSING: {art['path']}", file=sys.stderr)
            bad += 1
            continue
        if sha256(path) != art["sha256"]:
            print(f"MISMATCH: {art['path']}", file=sys.stderr)
            bad += 1
        elif path.stat().st_size != art["size"]:
            print(f"SIZE MISMATCH: {art['path']}", file=sys.stderr)
            bad += 1
    total = len(manifest["artifacts"])
    print(f"verified {total - bad}/{total} artifacts")
    return 1 if bad else 0


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("staging", type=Path, help="каталог staging payload")
    ap.add_argument("--version", help="releaseVersion (только build)")
    ap.add_argument("--revision", help="buildRevision (только build)")
    ap.add_argument("--verify", action="store_true", help="проверить существующий manifest.json")
    args = ap.parse_args()

    if not args.staging.is_dir():
        print(f"Нет каталога: {args.staging}", file=sys.stderr)
        return 2
    if args.verify:
        return cmd_verify(args.staging)
    if not args.version or not args.revision:
        ap.error("для build нужны --version и --revision (либо используйте --verify)")
    return cmd_build(args.staging, args.version, args.revision)


if __name__ == "__main__":
    raise SystemExit(main())
