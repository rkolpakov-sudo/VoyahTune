"""fix_batch7: остатки 3 ошибок build-native-12."""
import pathlib

N = pathlib.Path("Native/app/src/main/java/ru/big/town/anative")
log = []


def L(m):
    print(m)
    log.append(m)


def rw(name, pairs):
    p = N / name
    t = p.read_text(encoding="utf-8", errors="replace")
    for old, new in pairs:
        cnt = t.count(old)
        if cnt >= 1:
            t = t.replace(old, new)
            L(f"  OK({cnt}) {name}: {old.strip().splitlines()[0][:80]}")
        else:
            L(f"  !! MISS {name}: {old.strip().splitlines()[0][:80]}")
    p.write_text(t, encoding="utf-8")


# OemVehicleStateTransport: удалить дубликат Result result;
rw("OemVehicleStateTransport.java", [
    ("""        Result result;
        Result result;
""", """        Result result;
"""),
])

# ApplyEngine: final-захват для лямбды
rw("ApplyEngine.java", [
    ("""            bArrValidCanFrames = new byte[0][];
        }
        for (int i = 0; bArrValidCanFrames.length > 0 && i < MainActivity.customCommandCount; i++) {""",
     """            bArrValidCanFrames = new byte[0][];
        }
        final byte[][] bArrCapture = bArrValidCanFrames;
        for (int i = 0; bArrValidCanFrames.length > 0 && i < MainActivity.customCommandCount; i++) {"""),
    ("""                    return MainActivity.setCanValues(1, bArrValidCanFrames, "custom command (unlock/wake)");""",
     """                    return MainActivity.setCanValues(1, bArrCapture, "custom command (unlock/wake)");"""),
])

pathlib.Path("logs/fix_batch7.log").write_text("\n".join(log), encoding="utf-8")
print("done")
