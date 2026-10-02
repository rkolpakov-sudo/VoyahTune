import pathlib
import re

SH = pathlib.Path("RestoreMode/app/src/main/java/com/k2fsa/sherpa/onnx")
log = []

# OfflineModelConfig: inline local vars into this() — restricted to the synthetic ctor block
p = SH / "OfflineModelConfig.java"
lines = p.read_text(encoding="utf-8", errors="replace").splitlines(keepends=True)
warn_idx = None
for i, l in enumerate(lines):
    if "Illegal instructions before constructor call" in l:
        warn_idx = i
        break
assert warn_idx is not None, "no warn marker"
this_idx = None
for i in range(warn_idx, len(lines)):
    if lines[i].strip().startswith("this(offlineTransducerModelConfig2"):
        this_idx = i
        break
assert this_idx is not None, "no this() line"

var_pat = re.compile(r"^        \S[\w.<>\[\]]*\s+(\w+) = (\(i2 & \d+\) != 0 \?.*);\s*$")
vars_map = {}
var_idx = []
for i in range(warn_idx + 1, this_idx):
    m = var_pat.match(lines[i])
    if m:
        vars_map[m.group(1)] = m.group(2)
        var_idx.append(i)
assert len(vars_map) == 14, f"vars={len(vars_map)}"

new_this = lines[this_idx]
for name, rhs in vars_map.items():
    new_this, cnt = re.subn(r"\b" + re.escape(name) + r"\b", "(" + rhs + ")", new_this)
    assert cnt >= 1, (name, cnt)
lines[this_idx] = new_this
for i in reversed(var_idx):
    del lines[i]
p.write_text("".join(lines), encoding="utf-8")
log.append(f"OfflineModelConfig inlined {len(vars_map)} args")

# SpeakerKt.modelName -> package-private
p = SH / "SpeakerKt.java"
t = p.read_text(encoding="utf-8", errors="replace")
old = '    private static final String modelName = "3dspeaker'
if old in t:
    p.write_text(t.replace(old, '    static final String modelName = "3dspeaker', 1), encoding="utf-8")
    log.append("SpeakerKt.modelName made package-private")
else:
    log.append("SpeakerKt already done")

# KeywordSpotterResult: expand joinToString$default (ACC_SYNTHETIC, javac-invisible)
p = SH / "KeywordSpotterResult.java"
t = p.read_text(encoding="utf-8", errors="replace")
done = 0
old_a = 'ArraysKt.joinToString$default(this.tokens, ", ", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 62, (Object) null)'
new_a = 'ArraysKt.joinToString(this.tokens, ", ", "", "", -1, "...", (Function1) null)'
if old_a in t:
    t = t.replace(old_a, new_a, 1)
    done += 1
old_b = 'ArraysKt.joinToString$default(this.timestamps, (CharSequence) ", ", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) new Function1<Float, CharSequence>()'
new_b = 'ArraysKt.joinToString(this.timestamps, ", ", "", "", -1, null, (Function1) new Function1<Float, CharSequence>()'
if old_b in t:
    t = t.replace(old_b, new_b, 1)
    done += 1
old_c = "}, 30, (Object) null) + ']';"
new_c = "}) + ']';"
if old_c in t:
    t = t.replace(old_c, new_c, 1)
    done += 1
p.write_text(t, encoding="utf-8")
log.append(f"KeywordSpotterResult replacements: {done}")

# TtsKt: expand contains$default
p = SH / "TtsKt.java"
t = p.read_text(encoding="utf-8", errors="replace")
old = 'StringsKt.contains$default((CharSequence) lexicon, (CharSequence) ",", false, 2, (Object) null)'
new = 'StringsKt.contains((CharSequence) lexicon, (CharSequence) ",", false)'
if old in t:
    p.write_text(t.replace(old, new, 1), encoding="utf-8")
    log.append("TtsKt contains$default expanded")
else:
    log.append("TtsKt already done")

# NativeLibrary: lost loop vars r1/th
p = pathlib.Path("RestoreMode/app/src/main/java/com/sun/jna/NativeLibrary.java")
t = p.read_text(encoding="utf-8", errors="replace")
old = """                    while (r1.hasNext()) {
                        sb.append("\\n");
                        sb.append(th.getMessage());
                    }"""
new = """                    for (Throwable th : arrayList) {
                        sb.append("\\n");
                        sb.append(th.getMessage());
                    }"""
if old in t:
    p.write_text(t.replace(old, new, 1), encoding="utf-8")
    log.append("NativeLibrary error loop fixed")
else:
    log.append("NativeLibrary already done")

print("\n".join(log))
