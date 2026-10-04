import pathlib
import re

SH = pathlib.Path("RestoreMode/app/src/main/java/com/k2fsa/sherpa/onnx")
log = []

# 1. default-arg bridges: 0 == true ? 1 : 0 -> null
n = 0
for p in SH.glob("*.java"):
    t = p.read_text(encoding="utf-8", errors="replace")
    if "0 == true ? 1 : 0" in t:
        c = t.count("0 == true ? 1 : 0")
        t = t.replace("0 == true ? 1 : 0", "null")
        p.write_text(t, encoding="utf-8")
        n += c
log.append(f"sherpa default-marker nulls: {n}")

# 2. OfflineWhisper hashCode typo
p = SH / "OfflineWhisperModelConfig.java"
t = p.read_text(encoding="utf-8", errors="replace")
old = "return i + (z2 ? 1 : z2);"
assert old in t, "MISS whisper"
p.write_text(t.replace(old, "return i + (z2 ? 1 : 0);", 1), encoding="utf-8")
log.append("whisper hashCode fixed")

# 3. OfflineModelConfig: inline local vars into this() (Illegal instructions before constructor call)
p = SH / "OfflineModelConfig.java"
lines = p.read_text(encoding="utf-8", errors="replace").splitlines(keepends=True)
var_pat = re.compile(r"^        \S[\w.<>\[\]]*\s+(\w+) = (\(i2 & \d+\) != 0 \?.*);\s*$")
vars_map = {}
var_idx = []
for i, l in enumerate(lines):
    m = var_pat.match(l)
    if m and "&" in m.group(2):
        vars_map[m.group(1)] = m.group(2)
        var_idx.append(i)
assert len(vars_map) == 14, len(vars_map)
# find this(...) line
this_idx = None
for i, l in enumerate(lines):
    if l.strip().startswith("this(offlineTransducerModelConfig2"):
        this_idx = i
        break
assert this_idx is not None, "no this() line"
new_this = lines[this_idx]
for name, rhs in vars_map.items():
    new_this, cnt = re.subn(r"\b" + re.escape(name) + r"\b", "(" + rhs + ")", new_this)
    assert cnt >= 1, (name, cnt)
lines[this_idx] = new_this
# remove var lines (descending)
for i in reversed(var_idx):
    del lines[i]
p.write_text("".join(lines), encoding="utf-8")
log.append(f"OfflineModelConfig inlined {len(vars_map)} args")

# 4. SpeakerKt.modelName -> package-private
p = SH / "SpeakerKt.java"
t = p.read_text(encoding="utf-8", errors="replace")
old = '    private static final String modelName = "3dspeaker'
assert old in t, "MISS speaker"
p.write_text(t.replace(old, '    static final String modelName = "3dspeaker', 1), encoding="utf-8")
log.append("SpeakerKt.modelName made package-private")

# 5. KeywordSpotterResult: expand joinToString$default (ACC_SYNTHETIC, javac-invisible)
p = SH / "KeywordSpotterResult.java"
t = p.read_text(encoding="utf-8", errors="replace")
old_a = 'ArraysKt.joinToString$default(this.tokens, ", ", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 62, (Object) null)'
new_a = 'ArraysKt.joinToString(this.tokens, ", ", "", "", -1, "...", (Function1) null)'
assert old_a in t, "MISS joinA"
t = t.replace(old_a, new_a, 1)
old_b = 'ArraysKt.joinToString$default(this.timestamps, (CharSequence) ", ", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) new Function1<Float, CharSequence>()'
new_b = 'ArraysKt.joinToString(this.timestamps, ", ", "", "", -1, null, (Function1) new Function1<Float, CharSequence>()'
assert old_b in t, "MISS joinB"
t = t.replace(old_b, new_b, 1)
old_c = "}, 30, (Object) null) + ']';"
new_c = "}) + ']';"
assert old_c in t, "MISS joinC"
t = t.replace(old_c, new_c, 1)
p.write_text(t, encoding="utf-8")
log.append("KeywordSpotterResult joinToString$default expanded x2")

# 6. TtsKt: expand contains$default
p = SH / "TtsKt.java"
t = p.read_text(encoding="utf-8", errors="replace")
old = 'StringsKt.contains$default((CharSequence) lexicon, (CharSequence) ",", false, 2, (Object) null)'
new = 'StringsKt.contains((CharSequence) lexicon, (CharSequence) ",", false)'
assert old in t, "MISS contains"
p.write_text(t.replace(old, new, 1), encoding="utf-8")
log.append("TtsKt contains$default expanded")

# 7. NativeLibrary: lost loop vars r1/th
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
assert old in t, "MISS nativelib"
p.write_text(t.replace(old, new, 1), encoding="utf-8")
log.append("NativeLibrary error loop fixed")

print("\n".join(log))
