#!/usr/bin/env python3
"""Тесты utils/parity-diff.py (SPEC L111) — без авто, на синтетических трассах."""
import contextlib
import importlib.util
import io
import tempfile
import unittest
from pathlib import Path

REPO = Path(__file__).resolve().parents[2]
SPEC = importlib.util.spec_from_file_location(
    "parity_diff", REPO / "Utils" / "parity-diff.py"
)
parity_diff = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(parity_diff)

LOGCAT_A = """\
10-04 09:15:01.310  2841  2863 I $$$ OemVehicleState $$$: TX57 getVehicleState LOW_BEAM=0
10-04 09:15:01.640  2841  2863 I $$$ OemVehicleState $$$: TX58 accepted-unconfirmed [door-wake] LOW_BEAM=1
10-04 09:15:02.250  2841  2863 I $$$ OemVehicleState $$$: TX77 accepted-unconfirmed [wake] states=Bundle[{LOW_BEAM=1}]
"""


class NormalizeTest(unittest.TestCase):
    def test_strips_logcat_time_and_pid(self):
        a = parity_diff.normalize("logcat", LOGCAT_A)
        b = parity_diff.normalize(
            "logcat",
            "11-05 22:33:44.999  9999  10000 I $$$ OemVehicleState $$$: "
            "TX57 getVehicleState LOW_BEAM=0\n"
            "11-05 22:33:45.111  9999  10000 I $$$ OemVehicleState $$$: "
            "TX58 accepted-unconfirmed [door-wake] LOW_BEAM=1\n"
            "11-05 22:33:45.777  9999  10000 I $$$ OemVehicleState $$$: "
            "TX77 accepted-unconfirmed [wake] states=Bundle[{LOW_BEAM=1}]\n",
        )
        self.assertEqual(a, b)
        self.assertTrue(a[0].startswith("I $$$ OemVehicleState $$$: "))

    def test_strips_hex_uuid_and_epoch_tokens(self):
        lines = parity_diff.normalize(
            "logcat",
            "handle=0x7f3a2b1c session=550e8400-e29b-41d4-a716-446655440000 "
            "stamp=1759486501310 hash=0123456789abcdef0123456789abcdef01234567 t=12.5ms",
        )
        self.assertEqual(
            [
                "handle=0x<X> session=<UUID> stamp=<EPOCH> "
                "hash=<TOKEN> t=<T>"
            ],
            lines,
        )

    def test_candump_and_dumpsys_clock_normalized(self):
        can = parity_diff.normalize("cantrace", "[123.456789] can0 123#DEADBEEF")
        self.assertEqual(["[<T>] can0 123#DEADBEEF"], can)
        dump = parity_diff.normalize(
            "dumpsys", "  mLastFocus=Window{ab12} time=2026-10-04 09:15:01.310"
        )
        self.assertNotIn("09:15:01", dump[0])
        self.assertNotIn("2026-10-04", dump[0])

    def test_candump_paren_timestamp_normalized(self):
        # candump -t a/d: (epoch.µs) / (delta) — скобки в начале строки
        a = parity_diff.normalize("cantrace", "(1759486501.310123) can0 123#DEADBEEF")
        b = parity_diff.normalize("cantrace", "(1760000000.987654) can0 123#DEADBEEF")
        self.assertEqual(a, b)
        self.assertEqual(["(<T>) can0 123#DEADBEEF"], a)

    def test_dumpsys_window_identity_normalized(self):
        a = parity_diff.normalize(
            "dumpsys", "mCurrentFocus=Window{4a3f2b1c u0 com.app/.Main}"
        )
        b = parity_diff.normalize(
            "dumpsys", "mCurrentFocus=Window{9b8c7d6e u0 com.app/.Main}"
        )
        self.assertEqual(a, b)
        self.assertIn("Window{<ID> u0 com.app/.Main}", a[0])
        # маска только для известных типов: посторонние {hex} не трогаем
        self.assertEqual(
            ["Bundle[{deadbeef}]"],
            parity_diff.normalize("dumpsys", "Bundle[{deadbeef}]"),
        )

    def test_long_decimal_kept_but_hex_token_masked(self):
        lines = parity_diff.normalize(
            "logcat",
            "seq=12345678901234567890123456789012 "
            "hash=0123456789abcdef0123456789abcdef01234567",
        )
        self.assertEqual(
            "seq=12345678901234567890123456789012 hash=<TOKEN>", lines[0]
        )


class ClassifyTest(unittest.TestCase):
    def test_identical_when_only_noise_differs(self):
        status, drift = parity_diff.classify(
            LOGCAT_A, LOGCAT_A.replace("09:15:01", "23:59:59").replace(
                "2841  2863", "9001  9002"
            ),
            "logcat", [],
        )
        self.assertEqual("identical", status)
        self.assertEqual([], drift)

    def test_value_change_is_regression(self):
        status, drift = parity_diff.classify(
            LOGCAT_A, LOGCAT_A.replace("LOW_BEAM=1", "LOW_BEAM=7", 1),
            "logcat", [],
        )
        self.assertEqual("regression", status)
        self.assertTrue(any("LOW_BEAM=1" in line for line in drift))
        self.assertTrue(any("LOW_BEAM=7" in line for line in drift))

    def test_allowlisted_diff_is_expected(self):
        fork = LOGCAT_A.replace(
            "TX77 accepted-unconfirmed [wake]",
            "TX77 returned 2 [wake]",
        )
        import re
        status, _drift = parity_diff.classify(
            LOGCAT_A, fork, "logcat", [re.compile(r"TX77 (accepted|returned)")]
        )
        self.assertEqual("expected-diff", status)

    def test_missing_trace_is_pending(self):
        status, drift = parity_diff.classify(None, LOGCAT_A, "logcat", [])
        self.assertEqual("pending", status)
        self.assertEqual([], drift)


class ReportTest(unittest.TestCase):
    def _run(self, tmp: Path, strict: bool = False, expected_text: str = "# пусто\n"):
        orig = tmp / "original"
        fork = tmp / "fork"
        orig.mkdir()
        fork.mkdir()
        (orig / "01.logcat").write_text(LOGCAT_A, encoding="utf-8")
        (fork / "01.logcat").write_text(LOGCAT_A, encoding="utf-8")
        expected = tmp / "expected.txt"
        expected.write_text(expected_text, encoding="utf-8")
        report = tmp / "report.md"
        argv = [
            "--original", str(orig), "--fork", str(fork),
            "--report", str(report), "--expected", str(expected),
        ]
        if strict:
            argv.append("--strict")
        code = parity_diff.main(argv)
        return code, report

    def test_all_scenarios_present_and_clean_exit(self):
        code, report = self._run(Path(tempfile.mkdtemp()))
        self.assertEqual(0, code)
        text = report.read_text(encoding="utf-8")
        for sid, slug in parity_diff.SCENARIOS:
            self.assertIn(f"| {sid} {slug} |", text)
        self.assertIn("| 01 awakening | identical | pending | pending | pending | pending |",
                      text)

    def test_regression_fails_run(self):
        tmp = Path(tempfile.mkdtemp())
        (tmp / "original").mkdir()
        (tmp / "fork").mkdir()
        (tmp / "original" / "05.logcat").write_text(LOGCAT_A, encoding="utf-8")
        (tmp / "fork" / "05.logcat").write_text(
            LOGCAT_A.replace("LOW_BEAM=1", "LOW_BEAM=9", 1), encoding="utf-8"
        )
        code = parity_diff.main([
            "--original", str(tmp / "original"),
            "--fork", str(tmp / "fork"),
            "--report", str(tmp / "report.md"),
            "--expected", str(tmp / "missing-expected.txt"),
        ])
        self.assertEqual(1, code)
        text = (tmp / "report.md").read_text(encoding="utf-8")
        self.assertIn("## Регрессии", text)
        self.assertIn("### 05.logcat", text)

    def test_strict_pending_fails(self):
        code, _ = self._run(Path(tempfile.mkdtemp()), strict=True)
        self.assertEqual(1, code)

    def test_expected_diff_patterns_load_from_file(self):
        code, report = self._run(
            Path(tempfile.mkdtemp()),
            expected_text=r"# comment" + "\n" + r"OemVehicleState" + "\n",
        )
        self.assertEqual(0, code)
        text = report.read_text(encoding="utf-8")
        # пары identical (01.logcat совпадают) — паттерны не нужны; просто битых нет
        self.assertNotIn("regression", text.split("Итого пар:")[1])

    def test_unknown_scenario_rejected(self):
        tmp = Path(tempfile.mkdtemp())
        with self.assertRaises(SystemExit) as cm, \
                contextlib.redirect_stderr(io.StringIO()):
            parity_diff.main([
                "--original", str(tmp), "--fork", str(tmp),
                "--report", str(tmp / "r.md"),
                "--scenario", "99",
            ])
        self.assertEqual(2, cm.exception.code)

    def test_pending_lists_scenario_when_any_kind_missing(self):
        # снят только logcat: строка Итог=pending есть, но старая логика
        # смотрела только на logcat и не показывала сценарий в списке pending
        tmp = Path(tempfile.mkdtemp())
        (tmp / "original").mkdir()
        (tmp / "fork").mkdir()
        (tmp / "original" / "01.logcat").write_text(LOGCAT_A, encoding="utf-8")
        (tmp / "fork" / "01.logcat").write_text(LOGCAT_A, encoding="utf-8")
        report = tmp / "report.md"
        code = parity_diff.main([
            "--original", str(tmp / "original"), "--fork", str(tmp / "fork"),
            "--report", str(report), "--expected", str(tmp / "missing.txt"),
        ])
        self.assertEqual(0, code)
        text = report.read_text(encoding="utf-8")
        self.assertIn("Pending (не снято): 01, 02", text)

    def test_invalid_expected_pattern_is_usage_error(self):
        tmp = Path(tempfile.mkdtemp())
        (tmp / "original").mkdir()
        (tmp / "fork").mkdir()
        expected = tmp / "expected.txt"
        expected.write_text("[unclosed\n", encoding="utf-8")
        with self.assertRaises(SystemExit) as cm, \
                contextlib.redirect_stderr(io.StringIO()):
            parity_diff.main([
                "--original", str(tmp / "original"), "--fork", str(tmp / "fork"),
                "--report", str(tmp / "report.md"), "--expected", str(expected),
            ])
        self.assertEqual(2, cm.exception.code)

    def test_report_write_failure_is_usage_error(self):
        # --report указывает на каталог: IsADirectoryError должен дать 2,
        # а не 1 (код 1 зарезервирован под регрессии)
        tmp = Path(tempfile.mkdtemp())
        (tmp / "original").mkdir()
        (tmp / "fork").mkdir()
        report_dir = tmp / "report_dir"
        report_dir.mkdir()
        code = parity_diff.main([
            "--original", str(tmp / "original"), "--fork", str(tmp / "fork"),
            "--report", str(report_dir), "--expected", str(tmp / "missing.txt"),
        ])
        self.assertEqual(2, code)


if __name__ == "__main__":
    unittest.main()
