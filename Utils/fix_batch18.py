import pathlib

p = pathlib.Path("Native/app/src/main/java/ru/big/town/anative/OemIndividualDriveProfileReader.java")
ls = p.read_text(encoding="utf-8", errors="replace").splitlines()
assert ls[121] == "    private static SettingRead readOemGlobal(ContentResolver contentResolver, String str) {", repr(ls[121])
assert ls[165] == "    }", repr(ls[165])

new_m = """    private static SettingRead readOemGlobal(ContentResolver contentResolver, String str) {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(Uri.parse(OEM_GLOBAL_SETTINGS_URI), VALUE_PROJECTION, "name=?", new String[]{str}, null);
            if (cursor == null) {
                return SettingRead.failed();
            }
            return SettingRead.success(cursor.moveToNext() ? cursor.getString(0) : null);
        } catch (RuntimeException e) {
            Log.e(TAG, "Cannot read OEM setting " + str, e);
            return SettingRead.failed();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }""".split("\n")

ls[121:166] = new_m
p.write_text("\n".join(ls) + "\n", encoding="utf-8")
print("readOemGlobal reconstructed; lines:", len(ls))
