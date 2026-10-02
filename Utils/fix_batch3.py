"""Батч-фикс javac-ошибок: f$0, e/th-декларации, ??-остатки, ручные патчи."""
import pathlib
import re
import sys

BASES = [pathlib.Path("Native/app/src/main/java"), pathlib.Path("RestoreMode/app/src/main/java")]
log = []


def L(msg):
    print(msg)
    log.append(msg)


def read(p):
    return p.read_text(encoding="utf-8", errors="replace")


def write(p, t):
    p.write_text(t, encoding="utf-8")


# ---------------------------------------------------------------- 1. f$0 -> Outer.this
def fix_f0():
    total = 0
    for base in BASES:
        for f in sorted(base.rglob("*.java")):
            t = read(f)
            if "this.f$0" not in t:
                continue
            lines = t.splitlines()
            stem = f.stem
            cur = None
            changed = 0
            for idx, line in enumerate(lines):
                m = re.search(r"// from class: ([\w.$]+)", line)
                if m:
                    name = m.group(1)
                    # убрать пакет
                    parts = name.split(".")
                    simple = parts[-1]
                    if re.fullmatch(r"\d+", simple) and len(parts) > 1:
                        simple = parts[-2]
                    elif simple == "" and len(parts) > 1:
                        simple = parts[-2]
                    outer = simple.split("$$")[0].replace("$", ".")
                    if outer:
                        cur = outer
                outer_name = cur or stem.replace("$", ".")
                if "this.f$0" in line:
                    newline = line.replace("this.f$0", f"{outer_name}.this")
                    if newline != line:
                        changed += 1
                        lines[idx] = newline
            if changed:
                write(f, "\n".join(lines) + ("\n" if t.endswith("\n") else ""))
                total += changed
                L(f"  f$0: {f}: {changed}")
    L(f"f$0 total: {total}")


# ---------------------------------------------------------------- 2. e/th блочные декларации
METHOD_RE = re.compile(
    r"^\s{4,}(?!try\b|catch\b|if\b|else\b|for\b|while\b|switch\b|synchronized\b|do\b|finally\b|new\b|return\b)"
    r"[\w<>\[\]?,. ]+\s+\w+\s*\([^;{}]*\)\s*(?:throws [\w., ]+)?\s*\{\s*$"
)
SITE_RE = re.compile(r"^(\s*)(e|th) = (e\d+|th\d+);\s*$")


def catch_types(cl_line):
    m = re.search(r"catch\s*\(([^)]*)\)", cl_line)
    if not m:
        return None
    inner = m.group(1)
    # отрезать имя параметра
    tokens = [x.strip() for x in inner.split("|")]
    types = []
    for tk in tokens:
        parts = tk.split()
        if parts:
            types.append(parts[0])
    return types


EXCEPTION_FAMILY = {
    "Exception", "RuntimeException", "IOException", "RemoteException", "InterruptedException",
    "InvocationTargetException", "ReflectiveOperationException", "ParseException",
    "PackageManager", "SQLException", "IllegalStateException", "IllegalArgumentException",
}


def lub(types):
    if len(types) == 1:
        return types[0]
    if all(t in EXCEPTION_FAMILY or t.endswith("Exception") for t in types):
        return "Exception"
    return "Throwable"


def find_method_start(lines, cl_idx, D, depths):
    # ближайшая выше строка-заголовок метода, чья скобка реально оборачивает catch
    for k in range(cl_idx - 1, -1, -1):
        if not METHOD_RE.match(lines[k]):
            continue
        if k + 1 <= cl_idx and min(depths[k + 1:cl_idx + 1]) > depths[k]:
            return k
    return None


def fix_eth():
    for base in BASES:
        for f in sorted(base.rglob("*.java")):
            t = read(f)
            if not re.search(r"^\s*(e|th) = (e\d+|th\d+);\s*$", t, re.M):
                continue
            lines = t.splitlines()
            # пересчёт глубин
            def chg(line):
                line = re.sub(r'"(\\.|[^"\\])*"', '""', line)
                line = re.sub(r"'(\\.|[^'\\])*'", "''", line)
                line = re.sub(r"//.*", "", line)
                return line.count("{") - line.count("}")

            depths = []
            d = 0
            for l in lines:
                depths.append(d)
                d += chg(l)
            changed = False
            i = 0
            while i < len(lines):
                m = SITE_RE.match(lines[i])
                if not m:
                    i += 1
                    continue
                indent, var, rhs = m.group(1), m.group(2), m.group(3)
                # catch-строка
                cl = None
                for j in range(i, max(-1, i - 7), -1):
                    if re.search(r"catch\s*\(", lines[j]):
                        cl = j
                        break
                if cl is None:
                    L(f"  eth: {f}:{i+1} catch not found -> skip")
                    i += 1
                    continue
                D = depths[i]
                ms = find_method_start(lines, cl, D, depths)
                if ms is None:
                    L(f"  eth: {f}:{i+1} method start not found -> skip")
                    i += 1
                    continue
                # есть ли декларация var в методе (до сайта)
                decl_rx = re.compile(
                    r"\b(?:Exception|Throwable|IOException|RuntimeException|LinkageError)\s+" + var + r"\b\s*(?:=\s*null)?\s*;"
                )
                if any(decl_rx.search(lines[k]) for k in range(ms, i)):
                    i += 1
                    continue
                types = catch_types(lines[cl])
                if not types:
                    L(f"  eth: {f}:{i+1} catch types parse fail -> skip")
                    i += 1
                    continue
                typ = lub(types)
                lines[i] = f"{indent}{typ} {var} = {rhs};"
                changed = True
                L(f"  eth: {f}:{i+1}: {var} = {rhs} -> {typ} {var} = {rhs}")
                i += 1
            if changed:
                write(f, "\n".join(lines) + ("\n" if t.endswith("\n") else ""))


# ---------------------------------------------------------------- 3. ?? - паттерны
def fix_qq():
    for base in BASES:
        for f in sorted(base.rglob("*.java")):
            t = read(f)
            orig = t
            # sherpa hashCode: ?? rN = z; -> int rN = z ? 1 : 0;
            t = re.sub(r"^\s*\?\? (r\d+) = z;\s*$", lambda m: m.group(0).replace(f"?? {m.group(1)} = z;", f"int {m.group(1)} = z ? 1 : 0;"), t, flags=re.M)
            if t != orig:
                write(f, t)
                L(f"  qq(sherpa): {f}")


# ---------------------------------------------------------------- 4. objArr = 0
def fix_objarr():
    for base in BASES:
        for f in sorted(base.rglob("*.java")):
            t = read(f)
            if "Object[]" not in t or "= 0;" not in t:
                continue
            lines = t.splitlines()
            # объявления Object[] x = 0;
            decls = {}
            for idx, l in enumerate(lines):
                m = re.match(r"^(\s*)Object\[\] (\w+) = 0;\s*$", l)
                if m:
                    decls.setdefault(m.group(2), []).append(idx)
            if not decls:
                continue
            changed = False
            for var, idxs in decls.items():
                others = [
                    k for k, l in enumerate(lines)
                    if re.search(r"(?<![\w.$])" + re.escape(var) + r"(?![\w$])", l) and k not in idxs
                ]
                if not others:
                    for k in sorted(idxs, reverse=True):
                        del lines[k]
                        changed = True
                    L(f"  objArr: {f}: deleted unused {var} x{len(idxs)}")
                else:
                    for k in idxs:
                        lines[k] = lines[k].replace("= 0;", "= null;")
                        changed = True
                    L(f"  objArr: {f}: {var} -> null (used at {[o+1 for o in others]})")
            if changed:
                write(f, "\n".join(lines) + ("\n" if t.endswith("\n") else ""))


# ---------------------------------------------------------------- 5. ручные патчи
def manual():
    N = pathlib.Path("Native/app/src/main/java")
    RM = pathlib.Path("RestoreMode/app/src/main/java")

    # --- ClusterMediaHostActivity ---
    p = N / "ru/big/town/anative/ClusterMediaHostActivity.java"
    t = read(p)
    t = t.replace(
        "        ClusterMediaHostActivity clusterMediaHostActivity;\n        if (isCurrent()) {",
        "        Exception e;\n        if (isCurrent()) {",
    )
    t = t.replace(
        '                clusterMediaHostActivity = "voyah-cluster-media";\n', ""
    )
    t = t.replace(
        """                } catch (Exception e) {
                    e = e;
                }
            } catch (Exception e2) {
                e = e2;
                clusterMediaHostActivity = this;
            }
            Log.e(TAG, "Virtual display failed", e);
            clusterMediaHostActivity.launchFailed();""",
        """                } catch (Exception e2) {
                    e = e2;
                }
            } catch (Exception e3) {
                e = e3;
            }
            Log.e(TAG, "Virtual display failed", e);
            this.launchFailed();""",
    )
    if "clusterMediaHostActivity" in t:
        L("  WARN ClusterMedia: остатки clusterMediaHostActivity")
    write(p, t)
    L("  manual: ClusterMediaHostActivity")

    # --- VoiceEngine ---
    p = RM / "ru/big/town/restoremode/VoiceEngine.java"
    t = read(p)
    t = t.replace(
        """            } catch (Exception | LinkageError e) {
                e = e;
                voiceNeuralFilter = voiceNeuralFilter2;""",
        """            } catch (Exception | LinkageError e) {
                voiceNeuralFilter = voiceNeuralFilter2;""",
    )
    t = t.replace(
        """        } catch (Exception | LinkageError e2) {
            e = e2;
        }""",
        """        } catch (Exception | LinkageError e2) {
            throw e2;
        }""",
    )
    write(p, t)
    L("  manual: VoiceEngine")

    # --- LightSensorService final reassign ---
    p = N / "ru/big/town/anative/LightSensorService.java"
    t = read(p)
    t = t.replace(
        "            final SensorApplyRequest sensorApplyRequest = this.pendingMainSensorApply;",
        "            SensorApplyRequest sensorApplyRequest = this.pendingMainSensorApply;",
    )
    t = t.replace(
        """            Handler handler = this.carSignalIoHandler;
            if (handler != null) {
                handler.post(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1974x42c96c97(sensorApplyRequest);
                    }""",
        """            final SensorApplyRequest sensorApplyRequestPost = sensorApplyRequest;
            Handler handler = this.carSignalIoHandler;
            if (handler != null) {
                handler.post(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        LightSensorService.this.m1974x42c96c97(sensorApplyRequestPost);
                    }""",
    )
    write(p, t)
    L("  manual: LightSensorService")

    # --- ApplyEngine j -> jCurrentGeneration ---
    p = N / "ru/big/town/anative/ApplyEngine.java"
    t = read(p)
    t = t.replace(
        "return ApplyEngine.RESTORE_RUN_STATE.isActionAllowed(j);",
        "return ApplyEngine.RESTORE_RUN_STATE.isActionAllowed(jCurrentGeneration);",
    )
    write(p, t)
    L("  manual: ApplyEngine")

    # --- HeadlightCanTransport ---
    p = N / "ru/big/town/anative/HeadlightCanTransport.java"
    t = read(p)
    t = t.replace(
        "private final Runnable rebindRunnable = new HeadlightCanTransport$$ExternalSyntheticLambda1(this);",
        "private final Runnable rebindRunnable = this::restartBinding;",
    )
    t = t.replace(
        "this.mainHandler.post(new HeadlightCanTransport$$ExternalSyntheticLambda1(this));",
        "this.mainHandler.post(this::restartBinding);",
    )
    t = t.replace("Enum.valueOf(cls, command.vehicleStateName)", "Enum.valueOf((Class) cls, command.vehicleStateName)")
    write(p, t)
    L("  manual: HeadlightCanTransport")

    # --- LatestValueDelivery Listener<T> ---
    p = N / "ru/big/town/anative/LatestValueDelivery.java"
    t = read(p)
    t = t.replace(
        """        this(executor, new Listener() { // from class: ru.big.town.anative.LatestValueDelivery$$ExternalSyntheticLambda0
            @Override // ru.big.town.anative.LatestValueDelivery.Listener
            public final void accept(long j, long j2, Object obj) {
                consumer.accept(obj);
            }
        });""",
        """        this(executor, new Listener<T>() { // from class: ru.big.town.anative.LatestValueDelivery$$ExternalSyntheticLambda0
            @Override // ru.big.town.anative.LatestValueDelivery.Listener
            public final void accept(long j, long j2, T t) {
                consumer.accept(t);
            }
        });""",
    )
    write(p, t)
    L("  manual: LatestValueDelivery")

    # --- OemVehicleStateTransport ---
    p = N / "ru/big/town/anative/OemVehicleStateTransport.java"
    lines = read(p).splitlines()
    # method1 transactSingle (676..712) / method2 transactBundle (818..860) — по маркерам
    def method_range(sig_pred):
        for idx, l in enumerate(lines):
            if sig_pred(l):
                # найти конец метода: depth возврат
                d = 0
                started = False
                for j in range(idx, len(lines)):
                    l2 = re.sub(r'"(\\.|[^"\\])*"', '""', lines[j])
                    l2 = re.sub(r"//.*", "", l2)
                    d += l2.count("{") - l2.count("}")
                    if l2.count("{"):
                        started = True
                    if started and d <= 0:
                        return idx, j
        return None

    r1 = method_range(lambda l: l.startswith("    public Result transactSingle("))
    r2 = method_range(lambda l: l.startswith("    public Result transactBundle("))

    def apply_result_fix(ab):
        if ab is None:
            L("  WARN OemVehicleState method not found")
            return
        a, b = ab
        lines.insert(a + 1, "        Result result;")
        b += 1
        for k in range(a + 1, b + 1):
            l = lines[k]
            l = l.replace("this = Result.", "result = Result.")
            if re.match(r"^\s*return this;\s*$", l):
                l = l.replace("return this;", "return result;")
            lines[k] = l

    # снизу вверх: сначала более поздний метод, чтобы индексы первого не съехали
    apply_result_fix(r2)
    apply_result_fix(r1)
    t = "\n".join(lines) + "\n"
    t = t.replace(
        "static /* synthetic */ Map lambda$readVehicleStates$0(LinkedHashMap linkedHashMap, Session session) {",
        "static /* synthetic */ Map lambda$readVehicleStates$0(LinkedHashMap<StateKey, Integer> linkedHashMap, Session session) {",
    )
    t = t.replace(
        "Enum enumValueOf = Enum.valueOf(this.vehicleStateClass, stateKey.name);",
        "Enum enumValueOf = Enum.valueOf((Class) this.vehicleStateClass, stateKey.name);",
    )
    write(p, t)
    L("  manual: OemVehicleStateTransport")

    # --- SuspensionWidgetView ---
    p = RM / "ru/big/town/restoremode/SuspensionWidgetView.java"
    t = read(p)
    t = t.replace("        ?? CanSelect;\n", "        int CanSelect = 0;\n")
    t = t.replace(
        "CanSelect = suspensionWidgetView.canSelect(i4);",
        "CanSelect = suspensionWidgetView.canSelect(i4) ? 1 : 0;",
    )
    write(p, t)
    L("  manual: SuspensionWidgetView")

    # --- AppWidgetStore ---
    p = RM / "ru/big/town/restoremode/AppWidgetStore.java"
    t = read(p)
    t = t.replace("        ?? r13;\n", "        int r13 = 0;\n")
    t = t.replace("            ?? r5 = 0;\n", "            int r5 = 0;\n")
    t = t.replace("                ?? OptJSONObject = jSONArray.optJSONObject(i);", "                JSONObject OptJSONObject = jSONArray.optJSONObject(i);")
    t = t.replace("if (OptJSONObject == 0) {", "if (OptJSONObject == null) {")
    write(p, t)
    L("  manual: AppWidgetStore")

    # --- VoiceCommandSequence ---
    p = RM / "ru/big/town/restoremode/VoiceCommandSequence.java"
    t = read(p)
    t = t.replace(
        "segment = new Segment(str3, commandMatch2, objArr4 == true ? 1 : 0);",
        "segment = new Segment(str3, commandMatch2, null);",
    )
    t = t.replace(
        "segment = new Segment(str3, objArr2 == true ? 1 : 0, reason(str3));",
        "segment = new Segment(str3, null, reason(str3));",
    )
    write(p, t)
    L("  manual: VoiceCommandSequence")

    # --- Structure.java:897 удаление ниже в fix_objarr (после ручного) ---


# ---------------------------------------------------------------- 6. отчёт об остатках
def report():
    L("--- остатки ---")
    for base in BASES:
        for f in sorted(base.rglob("*.java")):
            t = read(f)
            for i, l in enumerate(t.splitlines()):
                if "this.f$0" in l:
                    L(f"  f$0 LEFT: {f}:{i+1}")
                if re.match(r"^\s*\?\?", l) and "JADX" not in l:
                    L(f"  ?? LEFT: {f}:{i+1}: {l.strip()[:100]}")
                if re.match(r"^\s*(e|th) = (e\d+|th\d+);\s*$", l):
                    L(f"  eth LEFT: {f}:{i+1}")
                if re.search(r"new \w+\$\$ExternalSyntheticLambda\w+\(", l):
                    L(f"  SYNTHNEW: {f}:{i+1}: {l.strip()[:120]}")
                if re.match(r"^\s*Object\[\] \w+ = 0;\s*$", l):
                    L(f"  OBJARR LEFT: {f}:{i+1}")


if __name__ == "__main__":
    manual()
    fix_f0()
    fix_eth()
    fix_qq()
    fix_objarr()
    report()
    pathlib.Path("logs/fix_batch3.log").write_text("\n".join(log), encoding="utf-8")
