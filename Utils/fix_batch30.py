import io, pathlib, sys

ROOT = pathlib.Path("RestoreMode/app/src/main/java/ru/big/town/restoremode")
ok = True

def fix(name, pairs):
    global ok
    p = ROOT / name
    t = io.open(p, encoding="utf-8").read()
    for old, new, count in pairs:
        n = t.count(old)
        if n != count:
            print(f"  !! {name}: x{n} (expected {count}): {old[:80]!r}")
            ok = False
            continue
        t = t.replace(old, new)
    io.open(p, "w", encoding="utf-8", newline="").write(t)
    print(name, "done")

fix("AdvanceActivityStarButton.java", [
    ("import com.google.android.material.timepicker.TimeModel;\n", "", 1),
    ('String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf(sb.length()))',
     'String.format("%d", Integer.valueOf(sb.length()))', 2),
])

fix("VoiceEngineCache.java", [
    ("throw new IllegalArgumentException(t);", "throw new IllegalArgumentException(String.valueOf(t));", 1),
])

print("ALL OK" if ok else "SOME PATTERNS FAILED")
sys.exit(0 if ok else 1)
