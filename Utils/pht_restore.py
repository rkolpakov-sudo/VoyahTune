"""V2: восстановление PHT из staged (UTF-8) + f0-конверсия + PHT-правки + откат ClusterMedia-eth."""
import pathlib
import subprocess

p = pathlib.Path("Native/app/src/main/java/ru/big/town/anative/PowerHoldStatusTracker.java")
out = subprocess.run(["git", "show", f":{p.as_posix()}"], capture_output=True)
t = out.stdout.decode("utf-8-sig")
lines = t.splitlines()
print("restored:", len(lines), "lines")

# 1. f0-конверсия: this.f$0. -> PowerHoldStatusTracker.this. (все 8 сайтов)
n = t.count("this.f$0.")
t = t.replace("this.f$0.", "PowerHoldStatusTracker.this.")
print("f0 converted:", n)

# 2. первое вхождение m2019 (static create) -> локальная переменная
old1 = "                PowerHoldStatusTracker.this.m2019lambda$acceptEvent$1$rubigtownanativePowerHoldStatusTracker(canBusEvent);"
new1 = "                powerHoldStatusTracker.m2019lambda$acceptEvent$1$rubigtownanativePowerHoldStatusTracker(canBusEvent);"
i = t.find(old1)
print("first m2019 at line", t[:i].count("\n") + 1 if i >= 0 else "MISS")
if i >= 0:
    t = t[:i] + new1 + t[i + len(old1):]

# 3. AnonymousClass2 -> static
old2 = "    class AnonymousClass2 implements SeedLoader {"
if old2 in t:
    t = t.replace(old2, "    static class AnonymousClass2 implements SeedLoader {")
    print("AnonymousClass2 -> static")
else:
    print("AnonymousClass2 MISS/already static")

p.write_text(t, encoding="utf-8")

# 4. ClusterMedia: откат eth-повтора (191/194 -> присваивания методному e)
cm = pathlib.Path("Native/app/src/main/java/ru/big/town/anative/ClusterMediaHostActivity.java")
ct = cm.read_text(encoding="utf-8", errors="replace")
old = """                } catch (Exception e2) {
                    Exception e = e2;
                }
            } catch (Exception e3) {
                Exception e = e3;
            }"""
new = """                } catch (Exception e2) {
                    e = e2;
                }
            } catch (Exception e3) {
                e = e3;
            }"""
if old in ct:
    ct = ct.replace(old, new)
    cm.write_text(ct, encoding="utf-8")
    print("ClusterMedia 191/194 reverted to method-level e")
else:
    print("ClusterMedia MISS (state?)")

# 5. финальная проверка
t2 = p.read_text(encoding="utf-8", errors="replace")
print("PHT f$0 left:", t2.count("this.f$0"))
ct2 = cm.read_text(encoding="utf-8", errors="replace")
print("CM Exception e = e2 left:", ct2.count("Exception e = e2;"))
print("CM method e = e2 present:", ct2.count("                    e = e2;"))
