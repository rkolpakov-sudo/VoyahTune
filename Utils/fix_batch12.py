import pathlib

p = pathlib.Path("Native/app/src/main/java/ru/big/town/anative/CanBusEventHub.java")
t = p.read_text(encoding="utf-8", errors="replace")
old = """            if (!iBinder.transact(20, parcelObtain, parcelObtain2, 0)) {
                if (zPost) {
                    return;
                } else {
                    return;
                }
            }"""
new = """            if (!iBinder.transact(20, parcelObtain, parcelObtain2, 0)) {
                return;
            }"""
assert old in t, "MISS"
t = t.replace(old, new, 1)
# неиспользуемая декларация zPost
old2 = """    public void m1859x339b20c4(IBinder iBinder, long j) {
        boolean zPost;
        long jElapsedRealtime"""
new2 = """    public void m1859x339b20c4(IBinder iBinder, long j) {
        long jElapsedRealtime"""
if old2 in t:
    t = t.replace(old2, new2, 1)
p.write_text(t, encoding="utf-8")
print("CanBusEventHub fixed")
