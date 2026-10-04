#!/usr/bin/env python3
"""WP1: подготовка AndroidManifest.xml из apktool-decoded формы под AGP 8.

Убирает package= (namespace в build.gradle), platformBuildVersion*,
android:compileSdkVersion* — их задаёт AGP.
"""
import pathlib
import re
import sys

TARGETS = [
    pathlib.Path("Native/app/src/main/AndroidManifest.xml"),
    pathlib.Path("RestoreMode/app/src/main/AndroidManifest.xml"),
]


def main() -> int:
    for p in TARGETS:
        if not p.exists():
            print(f"skip (missing): {p}")
            continue
        s = p.read_text(encoding="utf-8")
        s2 = re.sub(r'\s+package="[^"]*"', "", s, count=1)
        s2 = re.sub(r'\s+platformBuildVersion\w+="[^"]*"', "", s2)
        s2 = re.sub(r'\s+android:compileSdkVersion\w*="[^"]*"', "", s2)
        p.write_text(s2, encoding="utf-8")
        print(f"{p}: stripped {len(s) - len(s2)} chars")
    return 0


if __name__ == "__main__":
    sys.exit(main())
