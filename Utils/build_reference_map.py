#!/usr/bin/env python3
"""WP0 шаг 0.6: reference-map — сопоставление реконструированного кода с легендой.

Источники:
  src-reconstructed/<app>/sources/**.java   (jadx, полная декомпиляция)
  reference-3.14/<Module>/app/src/main/java/**.java  (легенда, read-only)

Статусы (SPEC L93):
  matched      — FQN есть в легенде (перенять имена/комменты/тесты)
  new-in-3.16+ — только в 3.22 (метка DELTA-3.16+, чистка при первом изменении)
  legend-only  — есть в легенде, нет в 3.22 (аудит удаления)

Выход: docs/reference-map.json + сводка в stdout.
"""
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

APPS = {
    "native": ROOT / "src-reconstructed" / "native" / "sources",
    "restore_mode": ROOT / "src-reconstructed" / "restore_mode" / "sources",
}
LEGEND_MODULES = ["Native", "RestoreMode", "SharedAndroid", "Updater", "Packaging"]
FIRST_PARTY_PREFIX = ("ru.big.town", "ru.big.town.anative", "ru.big.town.restoremode")


def fqns_from_sources(sources_root: Path, first_party_only: bool) -> set[str]:
    result: set[str] = set()
    if not sources_root.is_dir():
        return result
    for java in sources_root.rglob("*.java"):
        rel = java.relative_to(sources_root)
        parts = list(rel.with_suffix("").parts)
        if parts[-1] == "package-info":
            continue
        fqn = ".".join(parts)
        if first_party_only and not fqn.startswith(FIRST_PARTY_PREFIX):
            continue
        result.add(fqn)
    return result


def fqns_from_legend() -> set[str]:
    result: set[str] = set()
    for module in LEGEND_MODULES:
        base = ROOT / "reference-3.14" / module
        if not base.is_dir():
            continue
        for java in base.rglob("*.java"):
            parts = list(java.parts)
            if "java" not in parts:
                continue
            idx = parts.index("java")
            rel = Path(*parts[idx + 1:]).with_suffix("")
            fqn = ".".join(rel.parts)
            if "androidTest" in java.parts or "test" in rel.parts[:1]:
                continue
            result.add(fqn)
    return result


def main() -> None:
    legend = fqns_from_legend()
    report: dict = {"schema": "reference-map-v1", "legend_classes": len(legend), "apps": {}}
    total_recon = total_covered = 0

    for app, src in APPS.items():
        recon = fqns_from_sources(src, first_party_only=True)
        matched = sorted(recon & legend)
        new = sorted(recon - legend)
        report["apps"][app] = {
            "reconstructed_first_party": len(recon),
            "matched": len(matched),
            "new-in-3.16+": len(new),
            "matched_classes": matched,
            "new_classes": new,
        }
        total_recon += len(recon)
        total_covered += len(matched) + len(new)
        print(f"{app}: first-party={len(recon)} matched={len(matched)} new={len(new)}")

    all_recon = set()
    for app, src in APPS.items():
        all_recon |= fqns_from_sources(src, first_party_only=True)
    report["legend_only"] = sorted(legend - all_recon)
    coverage = 100.0 if total_recon == 0 else 100.0 * total_covered / total_recon
    report["coverage_percent"] = round(coverage, 2)
    print(f"coverage matched+new = {coverage:.1f}% (need >= 80%)")
    print(f"legend-only = {len(report['legend_only'])} classes (аудит удаления)")

    out = ROOT / "docs" / "reference-map.json"
    out.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n",
                   encoding="utf-8")
    print(f"-> {out}")


if __name__ == "__main__":
    main()
