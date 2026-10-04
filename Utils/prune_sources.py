#!/usr/bin/env python3
"""WP1: удаление third-party декомпилированных деревьев, заменяемых Maven-зависимостями.

Причина: у части классов androidx/kotlin/kotlinx/material jadx теряет типы локальных
переменных (`?? rX;` — «Type inference failed»). Эти библиотеки подключаются из Maven
с ТОЧНЫМИ версиями из META-INF исходных APK (см. DECISIONS, стратегия B).
Деревья, которые остаются в исходниках (нет Maven-эквивалента или нет ошибок):
  android/car, android/support, com/android, com/k2fsa, com/sun, com/google/common,
  com/google/errorprone, _COROUTINE
Удаляемые деревья: androidx/, kotlin/, kotlinx/, com/google/android/material/,
org/jetbrains, org/intellij, org/jspecify (аннотации идут транзитивно с kotlin-stdlib).
"""
from __future__ import annotations

import shutil
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
MODULES = [ROOT / "Native" / "app" / "src" / "main" / "java",
           ROOT / "RestoreMode" / "app" / "src" / "main" / "java"]
PRUNE = [
    "androidx",
    "kotlin",
    "kotlinx",
    "com/google/android/material",
    "org/jetbrains",
    "org/intellij",
    "org/jspecify",
]


def count_java(p: Path) -> int:
    return sum(1 for _ in p.rglob("*.java")) if p.is_dir() else 0


def main() -> int:
    for module in MODULES:
        if not module.is_dir():
            print(f"skip missing {module}")
            continue
        for rel in PRUNE:
            target = module / rel
            n = count_java(target)
            if target.exists():
                shutil.rmtree(target)
            print(f"{module.parts[-5]}/{rel}: removed {n} java files")
            # clean empty parents
            parent = target.parent
            while parent != module and parent.exists() and not any(parent.iterdir()):
                parent.rmdir()
                parent = parent.parent
    total = sum(count_java(m) for m in MODULES)
    print(f"remaining first-party+kept sources: {total} java files")
    return 0


if __name__ == "__main__":
    sys.exit(main())
