"""fix_batch13: LightSensorService — переписать register/unregisterCallbackTransaction по reference."""
import pathlib

p = pathlib.Path("Native/app/src/main/java/ru/big/town/anative/LightSensorService.java")
ls = p.read_text(encoding="utf-8", errors="replace").splitlines()


def find(needle, start=0):
    for i in range(start, len(ls)):
        if needle in ls[i]:
            return i
    raise AssertionError("not found: " + needle)


# --- registerCallbackTransaction: decl .. closing brace
a = find("private RegistrationResult registerCallbackTransaction(")
# closing: first line that is exactly '    }' after a
b = next(i for i in range(a + 1, len(ls)) if ls[i] == "    }")
new_reg = """    private RegistrationResult registerCallbackTransaction(IBinder iBinder, IBinder iBinder2) {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        boolean z = false;
        try {
            parcelObtain.writeInterfaceToken(CAR_SIGNAL_DESCRIPTOR);
            parcelObtain.writeStrongBinder(iBinder2);
            z = true;
            if (!iBinder.transact(46, parcelObtain, parcelObtain2, 0)) {
                return RegistrationResult.NOT_SENT;
            }
            parcelObtain2.readException();
            Log.i(TAG, "registerCallback: OK (TX=46)");
            return RegistrationResult.SUCCESS;
        } catch (RemoteException | RuntimeException e) {
            Log.w(TAG, "registerCallback: error: " + e.getMessage());
            return (z && iBinder.isBinderAlive()) ? RegistrationResult.AMBIGUOUS : RegistrationResult.NOT_SENT;
        } finally {
            parcelObtain.recycle();
            parcelObtain2.recycle();
        }
    }""".split("\n")
print(f"registerCallbackTransaction: lines {a+1}..{b+1}")
ls[a:b + 1] = new_reg

# --- unregisterCallbackTransaction
a = find("private boolean unregisterCallbackTransaction(")
b = next(i for i in range(a + 1, len(ls)) if ls[i] == "    }")
new_unreg = """    private boolean unregisterCallbackTransaction(IBinder iBinder, IBinder iBinder2) {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInterfaceToken(CAR_SIGNAL_DESCRIPTOR);
            parcelObtain.writeStrongBinder(iBinder2);
            if (!iBinder.transact(47, parcelObtain, parcelObtain2, 0)) {
                return !iBinder.isBinderAlive();
            }
            parcelObtain2.readException();
            Log.i(TAG, "unregisterCallback: OK");
            return true;
        } catch (RemoteException | RuntimeException e) {
            Log.w(TAG, "unregisterCallback: error: " + e.getMessage());
            return !iBinder.isBinderAlive();
        } finally {
            parcelObtain.recycle();
            parcelObtain2.recycle();
        }
    }""".split("\n")
print(f"unregisterCallbackTransaction: lines {a+1}..{b+1}")
ls[a:b + 1] = new_unreg

p.write_text("\n".join(ls) + "\n", encoding="utf-8")
print("done, lines:", len(ls))
