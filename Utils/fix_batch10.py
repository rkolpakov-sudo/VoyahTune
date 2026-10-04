"""fix_batch10: OemVehicleStateTransport — missing return + throws Throwable."""
import pathlib

p = pathlib.Path("Native/app/src/main/java/ru/big/town/anative/OemVehicleStateTransport.java")
t = p.read_text(encoding="utf-8", errors="replace")
log = []


def rep(old, new, label, count=1):
    global t
    c = t.count(old)
    if c >= count:
        t = t.replace(old, new) if count == 0 else t.replace(old, new, count)
        log.append(f"OK({c}) {label}")
    else:
        log.append(f"MISS({c}) {label}")


# 1. missing return в transactVehicleState
rep("""            } catch (RemoteException | RuntimeException e) {
                Log.e(TAG, "TX57 getVehicleState failed " + stateKey, e);
                dropBinding(null, iBinder);
            }
        } finally {""",
    """            } catch (RemoteException | RuntimeException e) {
                Log.e(TAG, "TX57 getVehicleState failed " + stateKey, e);
                dropBinding(null, iBinder);
            }
            return null;
        } finally {""",
    "transactVehicleState return null")

# 2. удалить throws Throwable (reference не содержит)
for sig in [
    "private Result sendSingleInternal(Context context, StateValue stateValue, String str) throws Throwable {",
    "private Result sendBundleInternal(Context context, LinkedHashMap<StateKey, Integer> linkedHashMap, String str) throws Throwable {",
    "private IBinder acquireBinder(Context context) throws Throwable {",
    "private void bindOnce(Context context, DemandConnection demandConnection) throws Throwable {",
]:
    rep(sig, sig.replace(" throws Throwable", ""), "throws removed: " + sig.split("(")[0])

# sendRestoreSequenceInternal — длинная строка, режем по ' throws Throwable {'
rep("sendRestoreSequenceInternal(Context context, StateValue stateValue, LinkedHashMap<StateKey, Integer> linkedHashMap, LinkedHashMap<StateKey, Integer> linkedHashMap2, String str) throws Throwable {",
    "sendRestoreSequenceInternal(Context context, StateValue stateValue, LinkedHashMap<StateKey, Integer> linkedHashMap, LinkedHashMap<StateKey, Integer> linkedHashMap2, String str) {",
    "throws removed: sendRestoreSequenceInternal")

p.write_text(t, encoding="utf-8")
pathlib.Path("logs/fix_batch10.log").write_text("\n".join(log), encoding="utf-8")
print("\n".join(log))
print("throws Throwable left:", t.count("throws Throwable"))
