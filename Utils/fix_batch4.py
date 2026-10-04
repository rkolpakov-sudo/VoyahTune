"""fix_batch4: добивание пропущенных сайтов e/th и objArr-артефактов."""
import pathlib
import re

N = pathlib.Path("Native/app/src/main/java")
RM = pathlib.Path("RestoreMode/app/src/main/java")
log = []


def L(m):
    print(m)
    log.append(m)


def rw(p, fn):
    t = p.read_text(encoding="utf-8", errors="replace")
    t2 = fn(t)
    p.write_text(t2, encoding="utf-8")


# --- ClusterMedia: вернуть присваивания методному e ---
p = N / "ru/big/town/anative/ClusterMediaHostActivity.java"
def f(t):
    t = t.replace(
        """                } catch (Exception e2) {
                    Exception e = e2;
                }
            } catch (Exception e3) {
                Exception e = e3;
            }""",
        """                } catch (Exception e2) {
                    e = e2;
                }
            } catch (Exception e3) {
                e = e3;
            }""",
    )
    return t
rw(p, f)
t = p.read_text(encoding="utf-8", errors="replace")
for i, l in enumerate(t.splitlines()):
    if "clusterMediaHostActivity" in l:
        L(f"  CM leftover {i+1}: {l.strip()[:140]}")
L("  clustermedia reverted")

# --- NativeLog 226/229 ---
p = N / "ru/big/town/anative/NativeLog.java"
def f(t):
    t = t.replace(
        """            } catch (Exception e3) {
                e = e3;
            }
        } catch (Throwable th3) {
            th = th3;
        }""",
        """            } catch (Exception e3) {
                Exception e = e3;
            }
        } catch (Throwable th3) {
            Throwable th = th3;
        }""",
    )
    return t
rw(p, f)
L("  nativelog 226/229")

# --- NowPlayingService 268 ---
p = N / "ru/big/town/anative/NowPlayingService.java"
def f(t):
    t = t.replace(
        """        } catch (Exception e2) {
            e = e2;
            nowPlayingService = this;
            handler2 = handler;
        }""",
        """        } catch (Exception e2) {
            Exception e = e2;
            nowPlayingService = this;
            handler2 = handler;
        }""",
    )
    return t
rw(p, f)
L("  nowplaying 268")

# --- VoiceRecognizer 220 ---
p = RM / "ru/big/town/restoremode/VoiceRecognizer.java"
def f(t):
    t = t.replace(
        """        } catch (Exception | LinkageError e2) {
            e = e2;
            j2 = j;
            listener2 = listener;
        } catch (Throwable th4) {""",
        """        } catch (Exception | LinkageError e2) {
            Throwable e = e2;
            j2 = j;
            listener2 = listener;
        } catch (Throwable th4) {""",
    )
    return t
rw(p, f)
L("  voicerecognizer 220")

# --- удаление остатков Object[] objArr* = 0/null в двух файлах ---
for p in [RM / "ru/big/town/restoremode/VoiceCommandSequence.java",
          N / "ru/big/town/anative/BatteryHeatRefreshGate.java"]:
    lines = p.read_text(encoding="utf-8", errors="replace").splitlines()
    out = []
    for l in lines:
        m = re.match(r"^(\s*)Object\[\] (objArr\d*) = (0|null);\s*$", l)
        if m:
            var = m.group(2)
            others = [x for x in lines if x is not l and re.search(r"(?<![\w.$])" + var + r"(?![\w$])", x)]
            if not others:
                L(f"  del {p.name}: {l.strip()}")
                continue
            L(f"  keep {p.name}: {l.strip()} (used)")
        out.append(l)
    p.write_text("\n".join(out) + "\n", encoding="utf-8")

# --- финальный отчёт ---
L("--- leftovers ---")
for base in [N, RM]:
    for f in base.rglob("*.java"):
        t = f.read_text(encoding="utf-8", errors="replace")
        for i, l in enumerate(t.splitlines()):
            if re.match(r"^\s*(e|th) = (e\d+|th\d+);\s*$", l):
                L(f"  eth: {f}:{i+1}: {l.strip()}")
            if re.match(r"^\s*Object\[\] \w+ = (0|null);\s*$", l):
                L(f"  objarr: {f}:{i+1}")
            if "clusterMediaHostActivity" in l:
                L(f"  cmname: {f}:{i+1}: {l.strip()[:120]}")

pathlib.Path("logs/fix_batch4.log").write_text("\n".join(log), encoding="utf-8")
