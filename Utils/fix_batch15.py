"""fix_batch15: MainActivity.loadModes — реконструкция по reference (try-catch-finally + фоллбэк)."""
import pathlib

p = pathlib.Path("Native/app/src/main/java/ru/big/town/anative/MainActivity.java")
ls = p.read_text(encoding="utf-8", errors="replace").splitlines()

# sanity checks (1-based -> 0-based)
assert "public static int loadModes(Context context, boolean z) {" in ls[246], ls[246]
assert ls[247].strip() == "Cursor cursorQuery = null;", ls[247]
assert ls[248].strip() == "try {", ls[248]
assert "query(Uri.parse" in ls[250], ls[250]
assert ls[251].lstrip().startswith("if (cursorQuery == null"), ls[251]
assert "Content provider not ready" in ls[252], ls[252]
assert ls[253].strip() == "if (cursorQuery != null) {", ls[253]
assert ls[261].strip() == "cursorQuery.moveToFirst();", ls[261]
assert "FRESH: driveEnabled" in ls[299], ls[299]
assert ls[303].strip() == "return 2;", ls[303]
assert "Exception reading ContentProvider" in ls[305], ls[305]
assert ls[316].strip() == "}", ls[316]

log_line = ls[252]
query_line = ls[250]
exc_log = ls[305]
# пере-индентация: 16sp -> 12sp
assert log_line.startswith(" " * 20), repr(log_line[:24])
assert query_line.startswith(" " * 16), repr(query_line[:20])
assert exc_log.startswith(" " * 16), repr(exc_log[:20])

new = (
    ls[:248]
    + [
        "        try {",
        "            " + query_line[16:],
        "            if (cursorQuery == null || cursorQuery.getCount() == 0 || cursorQuery.getColumnCount() < 5) {",
        "                " + log_line[20:],
        "            } else {",
    ]
    + ls[261:300]  # тело успеха (262..300), отступ 16 = else-тело
    + ["                return 2;"]
    + [
        "            }",
        "        } catch (Exception e) {",
        "            " + exc_log[16:],
        "        } finally {",
        "            if (cursorQuery != null) {",
        "                cursorQuery.close();",
        "            }",
        "        }",
        "        if (!z) {",
        "            return 0;",
        "        }",
        "        return loadModesFromCache(context) ? 1 : 0;",
        "    }",
    ]
    + ls[317:]
)

p.write_text("\n".join(new) + "\n", encoding="utf-8")
print("loadModes reconstructed; lines:", len(new))
