import pathlib

p = pathlib.Path("Native/app/src/main/java/ru/big/town/anative/ModeSyncPolicy.java")
t = p.read_text(encoding="utf-8", errors="replace")
old = """    synchronized boolean canRememberSelection(boolean z) {
        boolean z2;
        if (!z) {
            z2 = canRememberSelection();
        }
        return z2;
    }"""
new = """    synchronized boolean canRememberSelection(boolean z) {
        return z || canRememberSelection();
    }"""
assert old in t, "MISS"
p.write_text(t.replace(old, new), encoding="utf-8")
print("ModeSyncPolicy fixed")
