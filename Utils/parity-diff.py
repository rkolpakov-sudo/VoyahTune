#!/usr/bin/env python3
"""Попарный diff трасс паритет-оракула (SPEC L111).

Сравнивает traces/original/<scenario>.<kind> с traces/fork/<scenario>.<kind>,
нормализуя таймметки, pid и токены (SPEC: «игнор: таймметки, pid, токены»).

Классификация пары:
  identical      — после нормализации расхождений нет
  expected-diff  — все строки diff покрыты allowlist-паттернами
                   (Docs/parity-expected-diffs.txt, каждое расхождение
                   документируется там же)
  regression     — непокрытое расхождение (к фиксу, L116)
  pending        — трасса(-ы) ещё не сняты (L109/L110)

Отчёт: Docs/parity-report.md (в SPEC L111 путь «docs/...»; в репо каталог Docs/).
Коды выхода: 0 — регрессий нет; 1 — есть регрессии (или pending при --strict);
2 — ошибка аргументов/файлов.
"""
from __future__ import annotations

import argparse
import difflib
import re
import sys
from pathlib import Path

# Приложение A (SPEC L194): 12 сценариев P-списка.
SCENARIOS = (
    ("01", "awakening"),
    ("02", "guest-lock"),
    ("03", "sleep-wake-x3"),
    ("04", "individual-tx77"),
    ("05", "avas-toggle"),
    ("06", "light-swreason"),
    ("07", "srev-setpoint"),
    ("08", "forced-ev"),
    ("09", "window-fullscreen-dpi"),
    ("10", "split-transfer"),
    ("11", "voice-first-call"),
    ("12", "battery-heat-suspension"),
)

KINDS = ("logcat", "nativelog", "dumpsys", "cantrace")

RE_LOGCAT = re.compile(
    r"^(?:\d{2}-\d{2}\s+)?"
    r"(\d{2}:\d{2}:\d{2}\.\d{3})\s+"
    r"(\d+)\s+(\d+)\s+"
    r"([VDIWEF])\s+(.*)$"
)
RE_UUID = re.compile(
    r"\b[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-"
    r"[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\b"
)
RE_TOKEN = re.compile(r"\b[0-9a-fA-F]{32,}")
RE_HEX = re.compile(r"\b0x[0-9A-Fa-f]+\b")
RE_MS = re.compile(r"\bt=\d+(?:\.\d+)?ms\b")
RE_FULL_TS = re.compile(r"\d{4}-\d{2}-\d{2}[ T]\d{2}:\d{2}:\d{2}(?:\.\d+)?")
RE_CLOCK = re.compile(r"(?<![\w.:-])\d{2}:\d{2}:\d{2}(?:\.\d+)?(?![\d:])")
RE_CANDUMP_T = re.compile(r"^\s*\[\d+\.\d+\]")
# candump -t a/d: (1759486501.310123) / (12.345678) — скобки в начале строки
RE_CANDUMP_PAREN = re.compile(r"^\s*\(\d+(?:\.\d+)?\)")
# hex-идентификаторы объектов window-дампа (Window{4a3f2b1c u0 …}) меняются
# между сессиями. Только после известных типов — широкая маска {hex}
# скрыла бы настоящие расхождения (опасно для оракула).
RE_DUMPSYS_ID = re.compile(
    r"\b((?:Window|WindowState|AppWindowToken|AppWindow|ActivityRecord"
    r"|ActivityToken|Token|SurfaceView|Layer)\{)[0-9a-fA-F]+(?=[\s}])"
)
RE_EPOCH_MS = re.compile(r"(?<!\d)\d{13}(?!\d)")


def _mask_token(match: re.Match) -> str:
    """32+ hex-токен → <TOKEN>; чисто-числовые id/счётчики не трогаем."""
    token = match.group(0)
    return "<TOKEN>" if any(c in "abcdefABCDEF" for c in token) else token


def normalize(kind: str, text: str) -> list[str]:
    """Нормализованные строки трассы (таймметки/pid/токены изъяты)."""
    out = []
    for raw in text.splitlines():
        line = raw.rstrip("\r")
        if kind in ("logcat", "nativelog"):
            m = RE_LOGCAT.match(line)
            if m:
                # время/pid/tid изъяты; уровень+тег+сообщение сохраняются
                line = f"{m.group(4)} {m.group(5)}"
            else:
                line = re.sub(
                    r"^\d{2}-\d{2}\s+\d{2}:\d{2}:\d{2}\.\d{3}\s+", "", line
                )
        else:
            line = RE_CANDUMP_T.sub("[<T>]", line)
            if kind == "cantrace":
                line = RE_CANDUMP_PAREN.sub("(<T>)", line)
            else:
                line = RE_DUMPSYS_ID.sub(r"\1<ID>", line)
        # Сначала полная дата-время, затем «часы» (внутри даты) — иначе
        # подстрока времени съедается первой и дата остаётся.
        line = RE_FULL_TS.sub("<TS>", line)
        line = RE_CLOCK.sub("<T>", line)
        line = RE_UUID.sub("<UUID>", line)
        line = RE_TOKEN.sub(_mask_token, line)
        line = RE_HEX.sub("0x<X>", line)
        line = RE_MS.sub("t=<T>", line)
        line = RE_EPOCH_MS.sub("<EPOCH>", line)
        out.append(line)
    return out


def load_patterns(path: Path | None) -> list[re.Pattern]:
    if path is None or not path.is_file():
        return []
    patterns = []
    for raw in path.read_text(encoding="utf-8").splitlines():
        line = raw.strip()
        if not line or line.startswith("#"):
            continue
        try:
            patterns.append(re.compile(line))
        except re.error as exc:
            print(
                f"parity-diff: невалидный паттерн в {path}: {line!r}: {exc}",
                file=sys.stderr,
            )
            raise SystemExit(2) from exc
    return patterns


def _covered(lines: list[str], patterns: list[re.Pattern]) -> bool:
    if not patterns:
        return False
    return all(any(p.search(line) for p in patterns) for line in lines)


def classify(
    original_text: str | None,
    fork_text: str | None,
    kind: str,
    patterns: list[re.Pattern],
) -> tuple[str, list[str]]:
    """Возвращает (статус, строки расхождений)."""
    if original_text is None or fork_text is None:
        return "pending", []
    a = normalize(kind, original_text)
    b = normalize(kind, fork_text)
    sm = difflib.SequenceMatcher(a=a, b=b, autojunk=False)
    removed: list[str] = []
    added: list[str] = []
    for tag, i1, i2, j1, j2 in sm.get_opcodes():
        if tag == "equal":
            continue
        removed.extend(a[i1:i2])
        added.extend(b[j1:j2])
    drift = [f"- {line}" for line in removed] + [f"+ {line}" for line in added]
    if not drift:
        return "identical", []
    if _covered(removed + added, patterns):
        return "expected-diff", drift
    return "regression", drift


def _overall(statuses: list[str]) -> str:
    if "regression" in statuses:
        return "regression"
    if "pending" in statuses:
        return "pending"
    if "expected-diff" in statuses:
        return "expected-diff"
    return "identical"


def build_report(
    scenarios: list[tuple[str, str]],
    results: dict[tuple[str, str], tuple[str, list[str]]],
) -> str:
    lines = [
        "# Parity report (SPEC L111)",
        "",
        "<!-- generated by Utils/parity-diff.py; do not edit by hand -->",
        "",
        "Классы: identical / expected-diff (паттерн в Docs/parity-expected-diffs.txt)"
        " / regression (фикс) / pending (трасса не снята, L109/L110).",
        "",
        "| Сценарий | logcat | nativelog | dumpsys | cantrace | Итог |",
        "|---|---|---|---|---|---|",
    ]
    for sid, slug in scenarios:
        statuses = []
        cells = []
        for kind in KINDS:
            status = results[(sid, kind)][0]
            statuses.append(status)
            cells.append(status)
        lines.append(
            f"| {sid} {slug} | " + " | ".join(cells) + f" | {_overall(statuses)} |"
        )
    counts: dict[str, int] = {}
    for status, _ in results.values():
        counts[status] = counts.get(status, 0) + 1
    lines += [
        "",
        "Итого пар: "
        + ", ".join(f"{k}={v}" for k, v in sorted(counts.items())),
    ]
    regressions = [
        (sid, kind, drift)
        for (sid, kind), (status, drift) in sorted(results.items())
        if status == "regression"
    ]
    if regressions:
        lines += ["", "## Регрессии", ""]
        for sid, kind, drift in regressions:
            lines.append(f"### {sid}.{kind}")
            lines.append("")
            lines.append(f"```{kind}")
            lines.extend(drift[:200])
            if len(drift) > 200:
                lines.append(f"... ({len(drift) - 200} строк опущено)")
            lines += ["```", ""]
    expected = [
        (sid, kind, drift)
        for (sid, kind), (status, drift) in sorted(results.items())
        if status == "expected-diff"
    ]
    if expected:
        lines += ["## Ожидаемые расхождения (задокументированы)", ""]
        for sid, kind, drift in expected:
            lines.append(f"- {sid}.{kind}: {len(drift)} строк(и) —"
                         " паттерны в Docs/parity-expected-diffs.txt")
        lines.append("")
    # Pending — по итогу сценария: хватает любой неснятой трассы, а не
    # только logcat (иначе сценарий, где снят один logcat, «терялся» бы
    # из списка pending).
    by_sid: dict[str, list[str]] = {}
    for (sid, _kind), (status, _) in results.items():
        by_sid.setdefault(sid, []).append(status)
    pending = sorted(sid for sid, sts in by_sid.items() if "pending" in sts)
    if pending:
        lines += [
            "Pending (не снято): " + ", ".join(pending) + ".",
            "",
        ]
    return "\n".join(lines)


def run(
    original_dir: Path,
    fork_dir: Path,
    report_path: Path,
    expected_path: Path,
    only_scenario: str | None = None,
) -> dict[tuple[str, str], tuple[str, list[str]]]:
    patterns = load_patterns(expected_path)
    scenarios = [
        (sid, slug) for sid, slug in SCENARIOS
        if only_scenario is None or sid == only_scenario
    ]
    if not scenarios:
        print(f"parity-diff: неизвестный сценарий: {only_scenario}", file=sys.stderr)
        raise SystemExit(2)
    results: dict[tuple[str, str], tuple[str, list[str]]] = {}
    for sid, _slug in scenarios:
        for kind in KINDS:
            o = original_dir / f"{sid}.{kind}"
            f = fork_dir / f"{sid}.{kind}"
            o_text = o.read_text(encoding="utf-8", errors="replace") if o.is_file() else None
            f_text = f.read_text(encoding="utf-8", errors="replace") if f.is_file() else None
            results[(sid, kind)] = classify(o_text, f_text, kind, patterns)
    report_path.parent.mkdir(parents=True, exist_ok=True)
    report_path.write_text(build_report(scenarios, results), encoding="utf-8")
    return results


def main(argv: list[str] | None = None) -> int:
    repo = Path(__file__).resolve().parents[1]
    ap = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    ap.add_argument("--original", type=Path, default=repo / "traces" / "original")
    ap.add_argument("--fork", type=Path, default=repo / "traces" / "fork")
    ap.add_argument("--report", type=Path, default=repo / "Docs" / "parity-report.md")
    ap.add_argument(
        "--expected", type=Path,
        default=repo / "Docs" / "parity-expected-diffs.txt",
    )
    ap.add_argument(
        "--scenario", choices=[sid for sid, _ in SCENARIOS],
        help="только один сценарий (01..12)",
    )
    ap.add_argument(
        "--strict", action="store_true",
        help="pending считать ошибкой (гейт L116 после снятия трасс)",
    )
    args = ap.parse_args(argv)

    try:
        results = run(
            args.original, args.fork, args.report, args.expected, args.scenario
        )
    except (OSError, UnicodeDecodeError) as exc:
        print(f"parity-diff: ошибка файлов: {exc}", file=sys.stderr)
        return 2
    statuses = [status for status, _ in results.values()]
    regressions = statuses.count("regression")
    pendings = statuses.count("pending")
    print(
        f"parity-diff: {len(statuses)} пар, regression={regressions}, "
        f"pending={pendings}, report={args.report}"
    )
    if regressions:
        return 1
    if args.strict and pendings:
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
