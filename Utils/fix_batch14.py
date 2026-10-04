"""fix_batch14: MainActivity — str/str2 ветка, currentSavedToggle по reference, z2 init."""
import pathlib

p = pathlib.Path("Native/app/src/main/java/ru/big/town/anative/MainActivity.java")
ls = p.read_text(encoding="utf-8", errors="replace").splitlines()
log = []


def find(needle, start=0):
    for i in range(start, len(ls)):
        if needle in ls[i]:
            return i
    raise AssertionError("not found: " + needle)


# 1. else-ветка: str2 = null -> str = null (reference: appliedEnergy = ... : null)
i = find("                    str2 = null;")
seg = "\n".join(ls[i - 2:i + 3])
if ls[i] == "                    str2 = null;" and ls[i + 1] == "                }" and ls[i + 2] == "                str2 = str;":
    ls[i] = "                    str = null;"
    log.append("OK str2->str null branch (461-region)")
else:
    log.append("MISS null branch, seg=" + repr(seg))

# 2. currentSavedToggle: заменить тело с Cursor-объявления до закрывающего метода
m = find("public static boolean currentSavedToggle")
a = find("        Cursor cursorQuery = null;", m)
b = next(i for i in range(a + 1, len(ls)) if ls[i] == "    }")
new_cst = """        Cursor cursorQuery = null;
        try {
            cursorQuery = context.getContentResolver().query(MODES_PROVIDER_URI, null, null, null, null);
            if (cursorQuery != null && cursorQuery.getCount() != 0 && cursorQuery.getColumnCount() > i) {
                cursorQuery.moveToFirst();
                return cursorQuery.getInt(i) == 1;
            }
        } catch (Exception e) {
            Log.w(MODES_LOG, "currentSavedToggle " + str + ": " + e.getMessage());
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
        if ("forcedEv".equals(str)) {
            return forcedEv;
        }
        return "suspensionMaintenance".equals(str) ? suspensionMaintenance : disablePedestrianSound;
    }""".split("\n")
log.append(f"currentSavedToggle: lines {a+1}..{b+1}")
ls[a:b + 1] = new_cst

# 3. persistSavedToggle: z2 init
t = "\n".join(ls)
old3 = """    public static void persistSavedToggle(Context context, String str, boolean z) {
        boolean z2;
        if (context != null) {"""
new3 = """    public static void persistSavedToggle(Context context, String str, boolean z) {
        boolean z2 = false;
        if (context != null) {"""
if old3 in t:
    t = t.replace(old3, new3, 1)
    log.append("OK z2 = false")
else:
    log.append("MISS z2 decl")

p.write_text(t, encoding="utf-8")
print("\n".join(log))
