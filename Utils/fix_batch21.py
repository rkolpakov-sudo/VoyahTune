"""fix_batch21: SetModesService — grantInstallPermission (reference), setModesService double-assign,
jBeginRegistration catch-assign."""
import pathlib

p = pathlib.Path("Native/app/src/main/java/ru/big/town/anative/SetModesService.java")
ls = p.read_text(encoding="utf-8", errors="replace").splitlines()
log = []

# --- 1. grantInstallPermission: один try по reference (653..672)
a = 652
assert ls[a].strip().startswith("public void grantInstallPermission("), ls[a]
b = next(i for i in range(a + 1, len(ls)) if ls[i] == "    }")
reflection = ls[b - 2]  # AppOpsManager... invoke line
grant_log = ls[b - 1]   # Log.i grantInstall...
assert "AppOpsManager.class.getMethod" in reflection, reflection
assert "grantInstall: " in grant_log, grant_log
new_g = (
    ls[:a]
    + [
        "    public void grantInstallPermission(String str, int i) {",
        "        if (str == null || str.isEmpty()) {",
        "            return;",
        "        }",
        "        try {",
        "            if (i <= 0) {",
        "                i = getPackageManager().getPackageUid(str, 0);",
        "            }",
        "    " + reflection,   # 8sp -> 12sp
        "    " + grant_log,
        "        } catch (Exception e) {",
        "            boolean z = e instanceof InvocationTargetException;",
        "            Throwable cause = e;",
        "            if (z && e.getCause() != null) {",
        "                cause = e.getCause();",
        "            }",
        "            Log.e(TAG, \"grantInstall failed for \" + str + \": \" + cause);",
        "        }",
        "    }",
    ]
    + ls[b + 1:]
)
ls = new_g
log.append(f"grantInstallPermission: {a+1}..{b+1} rewritten")

# --- 2. setModesService: decl + удалить outer try/catch
decl = "        final SetModesService setModesService;"
assert decl in ls, "decl miss"
i = ls.index(decl)
ls[i] = "        final SetModesService setModesService = this;"
log.append(f"decl at {i+1} initialized")

# найти outer-try блок: '        try {' перед '            setModesService = this;'
t_i = ls.index("            setModesService = this;")
assert ls[t_i - 1] == "        try {", repr(ls[t_i - 1])
assert ls[t_i + 1] == "            try {", repr(ls[t_i + 1])
c_i = ls.index("        } catch (RuntimeException e3) {", t_i)
assert ls[c_i + 1] == "            RuntimeException e = e3;", ls[c_i + 1]
assert ls[c_i + 2] == "            setModesService = this;", ls[c_i + 2]
assert ls[c_i + 3] == "        }", ls[c_i + 3]
# удалить: outer try(t_i-1), assign(t_i), outer catch(c_i..c_i+3)
for idx in sorted([c_i, c_i + 1, c_i + 2, c_i + 3, t_i - 1, t_i], reverse=True):
    del ls[idx]
log.append("setModesService outer try/catch removed")

# --- 3. jBeginRegistration: удалить catch-присваивание
j_i = ls.index("            jBeginRegistration = -1;")
# контекст
assert "catch (Throwable th3)" in ls[j_i - 2], ls[j_i - 2]
assert ls[j_i + 1] == "        }", ls[j_i + 1]
del ls[j_i]
log.append("jBeginRegistration catch-assign removed")

p.write_text("\n".join(ls) + "\n", encoding="utf-8")
print("\n".join(log))
