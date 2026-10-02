import pathlib

p = pathlib.Path("Native/app/src/main/java/ru/big/town/anative/VoiceOemTransport.java")
t = p.read_text(encoding="utf-8", errors="replace")
old = "Object[] objArr) throws Throwable {"
assert t.count(old) == 1, t.count(old)
# убедимся, что это именно m2162
i = t.find(old)
assert "m2162lambda$prepare$0$rubigtownanativeVoiceOemTransport" in t[max(0, i - 200):i], "wrong site"
t = t.replace(old, "Object[] objArr) {", 1)
p.write_text(t, encoding="utf-8")
print("VoiceOemTransport m2162 throws removed")
