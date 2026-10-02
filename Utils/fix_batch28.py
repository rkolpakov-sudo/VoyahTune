import io, pathlib, sys

ROOT = pathlib.Path("RestoreMode/app/src/main/java/com/sun/jna")

def load(p):
    return io.open(p, encoding="utf-8").read()

def save(p, t):
    io.open(p, "w", encoding="utf-8", newline="").write(t)

def rep(t, old, new, path, count=1):
    n = t.count(old)
    if n != count:
        print(f"  !! {path}: pattern x{n} (expected {count}): {old[:80]!r}")
        return t, False
    return t.replace(old, new), True

ok = True

# ---------- Native.java ----------
p = ROOT / "Native.java"
t = load(p)
t, r = rep(t, """import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Window;
""", "", p); ok &= r
t, r = rep(t, """    static native long getWindowHandle0(Component component);

    static native long indexOf(""", """    static native long indexOf(""", p); ok &= r
t, r = rep(t, """    public static long getWindowID(Window window) throws HeadlessException {
        return AWT.getWindowID(window);
    }

    public static long getComponentID(Component component) throws HeadlessException {
        return AWT.getComponentID(component);
    }

    public static Pointer getWindowPointer(Window window) throws HeadlessException {
        return new Pointer(AWT.getWindowID(window));
    }

    public static Pointer getComponentPointer(Component component) throws HeadlessException {
        return new Pointer(AWT.getComponentID(component));
    }

    public static Pointer getDirectBufferPointer(Buffer buffer) {""",
    """    public static Pointer getDirectBufferPointer(Buffer buffer) {""", p); ok &= r
t, r = rep(t, "return Structure.size(cls, (Structure) obj);",
           "return Structure.size((Class<Structure>) cls, (Structure) obj);", p); ok &= r
t, r = rep(t, """                return Structure.size(cls);""",
           """                return Structure.size((Class<Structure>) cls);""", p); ok &= r
t, r = rep(t, """
    private static class AWT {
        private AWT() {
        }

        static long getWindowID(Window window) throws HeadlessException {
            return getComponentID(window);
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: java.awt.HeadlessException */
        static long getComponentID(Object obj) throws HeadlessException {
            if (GraphicsEnvironment.isHeadless()) {
                throw new HeadlessException("No native windows when headless");
            }
            Component component = (Component) obj;
            if (component.isLightweight()) {
                throw new IllegalArgumentException("Component must be heavyweight");
            }
            if (!component.isDisplayable()) {
                throw new IllegalStateException("Component must be displayable");
            }
            if (Platform.isX11() && System.getProperty("java.version").startsWith("1.4") && !component.isVisible()) {
                throw new IllegalStateException("Component must be visible");
            }
            return Native.getWindowHandle0(component);
        }
    }
}""", """}""", p); ok &= r
save(p, t)
print("Native.java done, ok =", ok)

# ---------- Pointer.java ----------
p = ROOT / "Pointer.java"
t = load(p)
t, r = rep(t, "return Structure.updateStructureByReference(cls, structure, getPointer(j));",
           "return Structure.updateStructureByReference((Class<Structure>) cls, structure, getPointer(j));", p); ok &= r
t, r = rep(t, "structureArr[i] = Structure.updateStructureByReference(cls, structureArr[i], pointerArray[i]);",
           "structureArr[i] = Structure.updateStructureByReference((Class<Structure>) cls, structureArr[i], pointerArray[i]);", p); ok &= r
save(p, t)
print("Pointer.java done, ok =", ok)

# ---------- Structure.java ----------
p = ROOT / "Structure.java"
t = load(p)
t, r = rep(t, "obj = newInstance(cls, PLACEHOLDER_MEMORY);",
           "obj = newInstance((Class<Structure>) cls, PLACEHOLDER_MEMORY);", p); ok &= r
t, r = rep(t, "obj = newInstance(cls, Structure.PLACEHOLDER_MEMORY);",
           "obj = newInstance((Class<Structure>) cls, Structure.PLACEHOLDER_MEMORY);", p); ok &= r
t, r = rep(t, """                    storeTypeInfo(Boolean.class, map2.get(FFITypes.ffi_type_uint32));
                    return;
                }
                throw new Error("FFI types not initialized");
            }
            throw new Error("Native library not initialized");
        }""",
    """                    storeTypeInfo(Boolean.class, map2.get(FFITypes.ffi_type_uint32));
                } else {
                    throw new Error("FFI types not initialized");
                }
            } else {
                throw new Error("Native library not initialized");
            }
        }""", p); ok &= r
save(p, t)
print("Structure.java done, ok =", ok)

# ---------- CallbackReference.java ----------
p = ROOT / "CallbackReference.java"
t = load(p)
t, r = rep(t, """    private CallbackReference(Callback callback, int i, boolean z) {
        long jCreateNativeCallback;
        super(callback);""",
    """    private CallbackReference(Callback callback, int i, boolean z) {
        super(callback);
        long jCreateNativeCallback;""", p); ok &= r
t, r = rep(t, "Structure.validate(cls);",
           "Structure.validate((Class<Structure>) cls);", p); ok &= r
t, r = rep(t, "Structure structureNewInstance = Structure.newInstance(cls);",
           "Structure structureNewInstance = Structure.newInstance((Class<Structure>) cls);", p); ok &= r
save(p, t)
print("CallbackReference.java done, ok =", ok)

# ---------- Function.java ----------
p = ROOT / "Function.java"
t = load(p)
t, r = rep(t, """            i++;
            this = function;
        }""",
    """            i++;
        }""", p); ok &= r
t, r = rep(t, "structureArr[i3] = Structure.updateStructureByReference(componentType2, structureArr[i3], pointerArray.getPointer(Native.POINTER_SIZE * i3));",
           "structureArr[i3] = Structure.updateStructureByReference((Class<Structure>) componentType2, structureArr[i3], pointerArray.getPointer(Native.POINTER_SIZE * i3));", p); ok &= r
t, r = rep(t, "Native.invokeStructure(this, this.peer, i2, objArr, Structure.newInstance(cls))",
           "Native.invokeStructure(this, this.peer, i2, objArr, Structure.newInstance((Class<Structure>) cls))", p); ok &= r
t, r = rep(t, "Structure.newInstance(componentType2).toArray(structureArr);",
           "Structure.newInstance((Class<Structure>) componentType2).toArray(structureArr);", p); ok &= r
save(p, t)
print("Function.java done, ok =", ok)

# ---------- NativeLibrary.java ----------
p = ROOT / "NativeLibrary.java"
t = load(p)
t, r = rep(t, "return getInstance(str, (Map<String, ?>) Collections.emptyMap());",
           "return getInstance(str, Collections.<String, Object>emptyMap());", p); ok &= r
save(p, t)
print("NativeLibrary.java done, ok =", ok)

print("ALL OK" if ok else "SOME PATTERNS FAILED")
sys.exit(0 if ok else 1)
