import io, sys

p = r"RestoreMode\app\src\main\java\ru\big\town\restoremode\VoiceSeatCommands.java"
t = io.open(p, encoding="utf-8", newline="").read()
ok = True

def rep(old, new, count=1):
    global t, ok
    n = t.count(old)
    if n != count:
        print(f"  !! x{n} (expected {count}): {old[:90]!r}")
        ok = False
        return
    t = t.replace(old, new)

# 1) string switch: fall-through lost breaks; dispatch must be a second switch
rep("""            str7.hashCode();
            byte b = -1;
            switch (str7) {
                case "driver":
                    b = 0;
                case "filler":
                    b = 1;
                case "passenger":
                    b = 2;
                case "1":
                    b = 3;
                case "2":
                    b = 4;
                case "3":
                    b = 5;
                case "on":
                    b = 6;
                case "off":
                    b = 7;
                case "heat":
                    b = 8;
                case "seat":
                    b = 9;
                case "vent":
                    b = 10;
                case "front":
                    b = 11;
                case "level":
                    b = 12;
                case "waves":
                    b = 13;
                case "massage":
                    b = 14;
                case "rollers":
                    b = 15;
                default:
                    switch (b) {""",
    """            byte b = -1;
            switch (str7) {
                case "driver":
                    b = 0;
                    break;
                case "filler":
                    b = 1;
                    break;
                case "passenger":
                    b = 2;
                    break;
                case "1":
                    b = 3;
                    break;
                case "2":
                    b = 4;
                    break;
                case "3":
                    b = 5;
                    break;
                case "on":
                    b = 6;
                    break;
                case "off":
                    b = 7;
                    break;
                case "heat":
                    b = 8;
                    break;
                case "seat":
                    b = 9;
                    break;
                case "vent":
                    b = 10;
                    break;
                case "front":
                    b = 11;
                    break;
                case "level":
                    b = 12;
                    break;
                case "waves":
                    b = 13;
                    break;
                case "massage":
                    b = 14;
                    break;
                case "rollers":
                    b = 15;
                    break;
            }
            {
                switch (b) {""")

# 2) verb: repeated same token allowed, different token fails (matches reference)
rep("""                        case 6:
                        case 7:
                            if (str6 == null && !str6.equals(str7)) {
                                return null;
                            }
                            str6 = str7;
                            break;
                            break;""",
    """                        case 6:
                        case 7:
                            if (str6 != null && !str6.equals(str7)) {
                                return null;
                            }
                            str6 = str7;
                            break;""")

# 3) lost loop increment at end of while body
rep("""                        default:
                            return null;
                    }
            }
        }
        if (z && str2 == null) {""",
    """                        default:
                            return null;
                    }
            }
            i++;
        }
        if (z && str2 == null) {""")

io.open(p, "w", encoding="utf-8", newline="").write(t)
print("VoiceSeatCommands fixed" if ok else "PATTERNS FAILED")
sys.exit(0 if ok else 1)
