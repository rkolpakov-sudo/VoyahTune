import pathlib

p = pathlib.Path("Native/app/src/main/java/ru/big/town/anative/OemVehicleStateTransport.java")
ls = p.read_text(encoding="utf-8", errors="replace").splitlines()
assert "private void bindOnce(Context context, DemandConnection demandConnection) {" in ls[589], ls[589]
assert ls[658].strip() == "}", repr(ls[658])
new_body = """    private void bindOnce(Context context, DemandConnection demandConnection) {
        boolean zBindService = false;
        try {
            Intent intent = new Intent(CANBUS_ACTION);
            intent.setPackage(CANBUS_PACKAGE);
            zBindService = context.bindService(intent, 1, context.getMainExecutor(), demandConnection);
            Log.i(TAG, "demand bindService gen=" + demandConnection.generation + " returned " + zBindService);
        } catch (RuntimeException e) {
            Log.e(TAG, "Demand CanBus bind failed", e);
        } finally {
            synchronized (this.connectionLock) {
                if (this.activeConnection == demandConnection) {
                    this.connectionRegistered = zBindService;
                    if (!zBindService) {
                        this.canBusBinder = null;
                        this.activeConnection = null;
                        this.bindingInProgress = false;
                    }
                }
                this.connectionLock.notifyAll();
            }
        }
    }""".split("\n")
ls[589:659] = new_body
p.write_text("\n".join(ls) + "\n", encoding="utf-8")
print("bindOnce reconstructed, file lines:", len(ls))
