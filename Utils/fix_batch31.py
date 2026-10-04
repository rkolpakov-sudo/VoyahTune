import io, sys

p = r"RestoreMode\app\src\main\java\com\sun\jna\NativeLibrary.java"
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

rep("""            if (Platform.isLinux()) {
                ArrayList<String> linuxLdPaths = getLinuxLdPaths();""",
    """            if (Platform.isLinux()) {
                ArrayList<String> linuxLdPaths;
                try {
                    linuxLdPaths = getLinuxLdPaths();
                } catch (Throwable thLd) {
                    linuxLdPaths = new ArrayList<>();
                }""")

rep("""                } else {
                    nativeLibraryLoadLibrary = loadLibrary(str, map2);
                }""",
    """                } else {
                    try {
                        nativeLibraryLoadLibrary = loadLibrary(str, map2);
                    } catch (Throwable eLoad) {
                        throw new UnsatisfiedLinkError("Unable to load library '" + str + "': " + eLoad.getMessage());
                    }
                }""")

rep("""                                } catch (IOException e7) {
                                    IOException e = e7;""",
    """                                } catch (Exception e7) {
                                    Exception e = e7;""")

rep("""                            try {
                                break;
                            } catch (IOException unused) {""",
    """                            try {
                                break;
                            } catch (Exception unused) {""")

io.open(p, "w", encoding="utf-8", newline="").write(t)
print("NativeLibrary fixed" if ok else "PATTERNS FAILED")
sys.exit(0 if ok else 1)
