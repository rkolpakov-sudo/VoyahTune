"""fix_batch6: чистка 33 javac-ошибок Native:app (build-native-10)."""
import pathlib

N = pathlib.Path("Native/app/src/main/java/ru/big/town/anative")
log = []


def L(m):
    print(m)
    log.append(m)


def rw(name, pairs):
    p = N / name
    t = p.read_text(encoding="utf-8", errors="replace")
    for old, new in pairs:
        cnt = t.count(old)
        if cnt == 1:
            t = t.replace(old, new)
            L(f"  OK {name}: {old.strip().splitlines()[0][:90]}")
        elif cnt == 0:
            L(f"  !! MISS {name}: {old.strip().splitlines()[0][:90]}")
        else:
            L(f"  !! MULTI({cnt}) {name}: {old.strip().splitlines()[0][:90]}")
    p.write_text(t, encoding="utf-8")


# --- ApplyEngine ---
rw("ApplyEngine.java", [
    ("""        try {
            return booleanSupplier.getAsBoolean();
        } catch (Throwable unused) {
        }
    }""",
     """        try {
            return booleanSupplier.getAsBoolean();
        } catch (Throwable unused) {
            return true;
        }
    }"""),
    ("""    public static CycleResult applyInternal(Runnable runnable, long j, long j2, long j3, boolean z) {
        Object obj;""",
     """    public static CycleResult applyInternal(Runnable runnable, long j, long j2, long j3, boolean z) {
        Object obj = RESTORE_LOCK;"""),
    ("""        final byte[][] bArrValidCanFrames;""",
     """        byte[][] bArrValidCanFrames;"""),
])

# --- BatteryHeatService ---
rw("BatteryHeatService.java", [
    ("""                return BatteryHeatService.this.m1825lambda$activate$11$rubigtownanativeBatteryHeatService(i);""",
     """                return BatteryHeatService.this.m1830lambda$activate$5$rubigtownanativeBatteryHeatService(i);"""),
    ("""                        BatteryHeatService.this.finishSettingsRefresh(request, null, j);""",
     """                        batteryHeatService.finishSettingsRefresh(request, null, j);"""),
    ("""                BatteryHeatService.this.finishSettingsRefresh(request, boolQueryAutoEnabled, j);""",
     """                batteryHeatService2.finishSettingsRefresh(request, boolQueryAutoEnabled, j);"""),
])

# --- LightSensorService ---
rw("LightSensorService.java", [
    ("""        boolean z = false;
        try {
            try {
                parcelObtain.writeInterfaceToken(CAR_SIGNAL_DESCRIPTOR);""",
     """        boolean z = false;
        RegistrationResult regResult = RegistrationResult.NOT_SENT;
        try {
            try {
                parcelObtain.writeInterfaceToken(CAR_SIGNAL_DESCRIPTOR);"""),
    ("""                        str = RegistrationResult.SUCCESS;""",
     """                        regResult = RegistrationResult.SUCCESS;"""),
    ("""                        str = RegistrationResult.NOT_SENT;
                    }""",
     """                        regResult = RegistrationResult.NOT_SENT;
                    }"""),
    ("""                    str = (z && iBinder.isBinderAlive()) ? RegistrationResult.AMBIGUOUS : RegistrationResult.NOT_SENT;""",
     """                    regResult = (z && iBinder.isBinderAlive()) ? RegistrationResult.AMBIGUOUS : RegistrationResult.NOT_SENT;"""),
    ("""        return str;
    }""",
     """        return regResult;
    }"""),
    ("""        final boolean zApplySensorRequest = false;""",
     """        final boolean[] zApplySensorRequest = {false};"""),
    (""" && (zApplySensorRequest = applySensorRequest(sensorApplyRequest, i, sensorQueryRun.ingressRevision, sensorQueryRun.settingsGeneration))) {""",
     """ && (zApplySensorRequest[0] = applySensorRequest(sensorApplyRequest, i, sensorQueryRun.ingressRevision, sensorQueryRun.settingsGeneration))) {"""),
    ("""                LightSensorService.this.m1960xedd937dc(sensorQueryRun, zApplySensorRequest);""",
     """                LightSensorService.this.m1960xedd937dc(sensorQueryRun, zApplySensorRequest[0]);"""),
    ("""                    LightSensorService.this.dropSettingsQueryOnMain(sensorApplyRequest);""",
     """                    lightSensorService.dropSettingsQueryOnMain(sensorApplyRequest);"""),
    ("""                        LightSensorService.this.finishSettingsWithoutQueryOnMain(sensorApplyRequest);""",
     """                        lightSensorService.finishSettingsWithoutQueryOnMain(sensorApplyRequest);"""),
    ("""                LightSensorService.this.finishSettingsQueryOnMain(sensorApplyRequest, lightThresholdsQueryThresholds);""",
     """                lightSensorService2.finishSettingsQueryOnMain(sensorApplyRequest, lightThresholdsQueryThresholds);"""),
])

# --- ManualAutoGate ---
rw("ManualAutoGate.java", [
    ("""        return new Ticket();""",
     """        return new Ticket(this);"""),
])

# --- MediaControlRouter: break вне switch -> if/else ---
rw("MediaControlRouter.java", [
    ("""                if (!z) {
                    token = null;
                    break;
                }
                Iterator it = arrayList2.iterator();
                do {
                    if (!it.hasNext()) {
                        token = null;
                        break;
                    }
                    token = (MediaSession.Token) it.next();
                } while (containsToken(arrayList, token));""",
     """                if (!z) {
                    token = null;
                } else {
                    Iterator it = arrayList2.iterator();
                    do {
                        if (!it.hasNext()) {
                            token = null;
                            break;
                        }
                        token = (MediaSession.Token) it.next();
                    } while (containsToken(arrayList, token));
                }"""),
])

# --- NativeLog ---
rw("NativeLog.java", [
    ("""    private Process proc;""",
     """    private java.lang.Process proc;"""),
    ("""            public final void run() throws Throwable {
                NativeLog.this.m1987lambda$start$0$rubigtownanativeNativeLog(iMyPid);
            }""",
     """            public final void run() {
                try {
                    NativeLog.this.m1987lambda$start$0$rubigtownanativeNativeLog(iMyPid);
                } catch (Throwable th) {
                    throw new RuntimeException(th);
                }
            }"""),
    ("""                Process process = this.proc;""",
     """                java.lang.Process process = this.proc;"""),
    ("""                Process processStart = new ProcessBuilder("logcat", "--pid=" + i, "-v", "time").redirectErrorStream(true).start();""",
     """                java.lang.Process processStart = new ProcessBuilder("logcat", "--pid=" + i, "-v", "time").redirectErrorStream(true).start();"""),
])

# --- NowPlayingService ---
rw("NowPlayingService.java", [
    ("""MediaController.Callback callback = new MediaController.Callback(mediaController, j, j2, mediaController.getSessionToken()) { // from class:""",
     """MediaController.Callback callback = new MediaController.Callback() { // from class:"""),
    ("""                                    this.val$watchedToken = token;""",
     """                                    this.val$watchedToken = mediaController.getSessionToken();"""),
    ("""                            this = nowPlayingService;
""", ""),
])

# --- PowerHoldStatusTracker ---
rw("PowerHoldStatusTracker.java", [
    ("""                PowerHoldStatusTracker.this.m2019lambda$acceptEvent$1$rubigtownanativePowerHoldStatusTracker(canBusEvent);""",
     """                powerHoldStatusTracker.m2019lambda$acceptEvent$1$rubigtownanativePowerHoldStatusTracker(canBusEvent);"""),
    ("""    class AnonymousClass2 implements SeedLoader {""",
     """    static class AnonymousClass2 implements SeedLoader {"""),
])

# --- ScreenLiftTaskRestorer ---
rw("ScreenLiftTaskRestorer.java", [
    ("""        HashMap map = new HashMap(this.savedByDisplay);""",
     """        Map<Integer, SavedTask> map = new HashMap<>(this.savedByDisplay);"""),
])

# --- SetModesService ---
rw("SetModesService.java", [
    ("""                boolean z = e instanceof InvocationTargetException;
                Exception cause = e;
                if (z && e.getCause() != null) {
                    cause = e;
                    cause = e.getCause();
                }
                cause = e;
                Log.e(TAG, "grantInstall failed for " + str + ": " + cause);""",
     """                boolean z = e instanceof InvocationTargetException;
                Throwable cause = e;
                if (z && e.getCause() != null) {
                    cause = e.getCause();
                }
                Log.e(TAG, "grantInstall failed for " + str + ": " + cause);"""),
    ("""                        boolean z = e instanceof InvocationTargetException;
                        Exception cause = e;
                        if (z && e.getCause() != null) {
                            cause = e;
                            cause = e.getCause();
                        }
                        cause = e;
                        Log.e(TAG, "forceStop failed " + str2 + ": " + cause);""",
     """                        boolean z = e instanceof InvocationTargetException;
                        Throwable cause = e;
                        if (z && e.getCause() != null) {
                            cause = e.getCause();
                        }
                        Log.e(TAG, "forceStop failed " + str2 + ": " + cause);"""),
    ("""                } catch (Throwable th2) {
                    Throwable th = th2;
                    this.carPowerCallbackGate.invalidate(jBeginRegistration);
                    clearPublishedCarPowerManager(carPowerManager);
                    Log.e(TAG, "setListener failed: " + th.getMessage());""",
     """                } catch (Throwable th2) {
                    this.carPowerCallbackGate.invalidate(jBeginRegistration);
                    clearPublishedCarPowerManager(carPowerManager);
                    Log.e(TAG, "setListener failed: " + th2.getMessage());"""),
])

# --- WashModeController ---
rw("WashModeController.java", [
    ("""    class AnonymousClass1 implements VehicleGateway {""",
     """    static class AnonymousClass1 implements VehicleGateway {"""),
])

# --- SplitHostActivity ---
rw("SplitHostActivity.java", [
    ("""                SplitHostActivity.this.finishAndRemoveTask();""",
     """                splitHostActivity.finishAndRemoveTask();"""),
])

pathlib.Path("logs/fix_batch6.log").write_text("\n".join(log), encoding="utf-8")
print("done")
