import io, sys

p = r"RestoreMode\app\src\main\java\com\sun\jna\Native.java"
t = io.open(p, encoding="utf-8").read()
ok = True

def rep(old, new, count=1):
    global t, ok
    n = t.count(old)
    if n != count:
        print(f"  !! x{n} (expected {count}): {old[:90]!r}")
        ok = False
        return
    t = t.replace(old, new)

# 1) static init: loadNativeDispatchLibrary throws Throwable
rep("""        callbackExceptionHandler = uncaughtExceptionHandler;
        loadNativeDispatchLibrary();""",
    """        callbackExceptionHandler = uncaughtExceptionHandler;
        try {
            loadNativeDispatchLibrary();
        } catch (Throwable thNdd) {
            throw new UnsatisfiedLinkError("Unable to load JNA native support: " + thNdd.getMessage());
        }""")

# 2) extractFromResourcePath(String) delegate wraps Throwable
rep("""    public static File extractFromResourcePath(String str) throws IOException {
        return extractFromResourcePath(str, null);
    }""",
    """    public static File extractFromResourcePath(String str) throws IOException {
        try {
            return extractFromResourcePath(str, null);
        } catch (Throwable thx) {
            throw new IOException("Failed to extract native library " + str, thx);
        }
    }""")

# 3) outer catch swallowed exception -> rethrow (missing return at end of method)
rep("""        } catch (IOException e2) {
            IOException e = e2;
        }
    }

    public static Library synchronizedLibrary(final Library library) {""",
    """        } catch (IOException e2) {
            throw e2;
        }
    }

    public static Library synchronizedLibrary(final Library library) {""")

# 4) isSupportedNativeType: empty catch -> return false
rep("""        try {
            return getNativeSize(cls) != 0;
        } catch (IllegalArgumentException unused) {
        }
    }""",
    """        try {
            return getNativeSize(cls) != 0;
        } catch (IllegalArgumentException unused) {
            return false;
        }
    }""")

# 5) exception-type scan loop lost its breaks (unreachable statement after while(true))
rep("""                    if (i5 < exceptionTypes.length) {
                        str = str5;
                        if (LastErrorException.class.isAssignableFrom(exceptionTypes[i5])) {
                            z = true;
                        } else {
                            i5++;
                            str5 = str;
                        }
                    } else {
                        str = str5;
                        z = false;
                    }
                }""",
    """                    if (i5 < exceptionTypes.length) {
                        str = str5;
                        if (LastErrorException.class.isAssignableFrom(exceptionTypes[i5])) {
                            z = true;
                            break;
                        } else {
                            i5++;
                            str5 = str;
                        }
                    } else {
                        str = str5;
                        z = false;
                        break;
                    }
                }""")

# 6) try { break; } catch (IOException ...) never thrown
rep("""                        try {
                            break;
                        } catch (IOException unused2) {
                        }""",
    """                        try {
                            break;
                        } catch (Exception unused2) {
                        }""")

io.open(p, "w", encoding="utf-8", newline="").write(t)
print("Native.java fixed" if ok else "PATTERNS FAILED")
sys.exit(0 if ok else 1)
