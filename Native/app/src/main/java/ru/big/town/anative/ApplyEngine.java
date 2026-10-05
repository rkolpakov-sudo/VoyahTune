package ru.big.town.anative;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes2.dex */
public final class ApplyEngine {
    private static final String MODES_URI = "content://ru.big.town.restoremode.restoremodecontentprovider/";
    static final String TAG = "$$$ ApplyEngine $$$";
    private static volatile Handler bg;
    private static volatile Handler commandBg;
    private static final Object RESTORE_LOCK = new Object();
    private static final RestoreRunState RESTORE_RUN_STATE = new RestoreRunState();
    private static final ModeSyncPolicy MODE_SYNC_POLICY = new ModeSyncPolicy();
    private static final PendingApplySeries APPLY_SERIES = new PendingApplySeries(new PendingApplySeries.Runner() {
        @Override // ru.big.town.anative.PendingApplySeries.Runner
        public final void run(Runnable runnable, long j, long j2, long j3, boolean z) {
            applyInternal(runnable, j, j2, j3, z);
        }
    });

    public enum WakeActionResult {
        SUCCESS,
        FAILED,
        SKIPPED
    }

    private static long beginRestoreGate(String str) {
        long jBeginRestore = MODE_SYNC_POLICY.beginRestore();
        Log.i(TAG, "mode sync gate CLOSED gen=" + jBeginRestore + " reason=" + str);
        return jBeginRestore;
    }

    public static void resetRestoreGate() {
        resetRestoreGate("external reset");
    }

    public static void resetRestoreGate(String str) {
        long jCancelAndAdvance;
        long jFreeze;
        synchronized (RESTORE_LOCK) {
            jCancelAndAdvance = RESTORE_RUN_STATE.cancelAndAdvance();
            jFreeze = MODE_SYNC_POLICY.freeze();
        }
        Log.i(TAG, "mode sync gate FROZEN gen=" + jFreeze + " runGen=" + jCancelAndAdvance + " reason=" + str);
    }

    static long capturePhysicalWakeGeneration() {
        long jCurrentGeneration;
        synchronized (RESTORE_LOCK) {
            jCurrentGeneration = RESTORE_RUN_STATE.currentGeneration();
        }
        return jCurrentGeneration;
    }

    static boolean isPhysicalWakeGenerationActive(long j) {
        boolean zIsActionAllowed;
        synchronized (RESTORE_LOCK) {
            zIsActionAllowed = RESTORE_RUN_STATE.isActionAllowed(j);
        }
        return zIsActionAllowed;
    }

    static void noteLoadedModes(String str, String str2, String str3, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) {
        MODE_SYNC_POLICY.updateExpected(str, str2, str3, z, z2, z3, z4, z5, z6);
    }

    static void noteSavedMode(boolean z, String str) {
        noteSavedMode(z ? "energy" : "driveMode", str);
    }

    static void noteSavedMode(String str, String str2) {
        MODE_SYNC_POLICY.updateExpectedMode(str, str2);
    }

    static void noteRememberLastMode(String str, boolean z) {
        MODE_SYNC_POLICY.updateRememberLast(str, z);
    }

    static boolean canRememberModeSelection() {
        return canRememberModeSelection(false);
    }

    static boolean canRememberModeSelection(boolean z) {
        boolean zCanRememberSelection;
        synchronized (RESTORE_LOCK) {
            zCanRememberSelection = MODE_SYNC_POLICY.canRememberSelection(z);
        }
        return zCanRememberSelection;
    }

    static boolean shouldPersistModeFeedback(String str, String str2) {
        boolean z;
        synchronized (RESTORE_LOCK) {
            z = MODE_SYNC_POLICY.evaluate(str, str2) == ModeSyncPolicy.Decision.ACCEPT;
        }
        return z;
    }

    static String currentVehicleMode(String str, String str2) {
        return MODE_SYNC_POLICY.currentMode(str, str2);
    }

    static void noteVehicleMode(String str, String str2) {
        MODE_SYNC_POLICY.observe(str, str2);
    }

    static void driveSelectionSaved() {
        synchronized (RESTORE_LOCK) {
            RESTORE_RUN_STATE.cancelRestoreAndAdvance();
            ModeSyncPolicy modeSyncPolicy = MODE_SYNC_POLICY;
            modeSyncPolicy.completeUserCommand(modeSyncPolicy.cancelRestore());
        }
    }

    static void persistModeFeedbackIfAllowed(Context context, boolean z, String str) {
        persistModeFeedbackIfAllowed(context, z ? "energy" : "driveMode", str);
    }

    static void persistModeFeedbackIfAllowed(Context context, String str, String str2) {
        if ("driveMode".equals(str) || "energy".equals(str)) {
            MODE_SYNC_POLICY.observe(str, str2);
            return;
        }
        Object obj = RESTORE_LOCK;
        synchronized (obj) {
            if (shouldPersistModeFeedback(str, str2)) {
                ModeSyncPolicy modeSyncPolicy = MODE_SYNC_POLICY;
                long jCurrentGeneration = modeSyncPolicy.currentGeneration();
                if (MainActivity.isLoadedMode(str, str2)) {
                    return;
                }
                synchronized (obj) {
                    if (modeSyncPolicy.canPersist(jCurrentGeneration, str)) {
                        MainActivity.persistSavedMode(context, str, str2);
                    }
                }
            }
        }
    }

    private ApplyEngine() {
    }

    private static synchronized Handler bg() {
        if (bg == null) {
            HandlerThread handlerThread = new HandlerThread("ApplyEngine");
            handlerThread.start();
            bg = new Handler(handlerThread.getLooper());
        }
        return bg;
    }

    private static synchronized Handler commandBg() {
        if (commandBg == null) {
            HandlerThread handlerThread = new HandlerThread("CanCommands");
            handlerThread.start();
            commandBg = new Handler(handlerThread.getLooper());
        }
        return commandBg;
    }

    public static void activateWake(String str) {
        synchronized (RESTORE_LOCK) {
            RestoreRunState restoreRunState = RESTORE_RUN_STATE;
            restoreRunState.activate(restoreRunState.currentGeneration());
            MODE_SYNC_POLICY.activateWake();
        }
        Log.i(TAG, "wake active: " + str);
    }

    public static void scheduleAccApply(Context context) {
        final Context applicationContext = context.getApplicationContext();
        bg().post(new Runnable() { // from class: ru.big.town.anative.ApplyEngine$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ApplyEngine.lambda$scheduleAccApply$0(applicationContext);
            }
        });
    }

    static /* synthetic */ void lambda$scheduleAccApply$0(Context context) {
        long jCurrentGeneration;
        long jCurrentRestoreEpoch;
        long jBeginRestoreGate;
        try {
            Bundle bundleCall = context.getContentResolver().call(Uri.parse(MODES_URI), "driveHookV2", "claimSettings", (Bundle) null);
            if (bundleCall == null) {
                return;
            }
            if (!bundleCall.getBoolean("claimed")) {
                if (bundleCall.getInt("acc", -1) == 2 && "submitted".equals(bundleCall.getString("settingsStartup"))) {
                    MainActivity.loadModes(context, true);
                    synchronized (RESTORE_LOCK) {
                        MODE_SYNC_POLICY.reconcileCompletedAcc(bundleCall.getInt("acc", -1), bundleCall.getString("settingsStartup"));
                    }
                    return;
                }
                return;
            }
            long j = bundleCall.getLong("cycle", -1L);
            synchronized (RESTORE_LOCK) {
                RestoreRunState restoreRunState = RESTORE_RUN_STATE;
                jCurrentGeneration = restoreRunState.currentGeneration();
                restoreRunState.activate(jCurrentGeneration);
                jCurrentRestoreEpoch = restoreRunState.currentRestoreEpoch();
                jBeginRestoreGate = beginRestoreGate("ACC cycle " + j);
            }
            CycleResult cycleResultApplyInternal = applyInternal(null, jBeginRestoreGate, jCurrentGeneration, jCurrentRestoreEpoch, false);
            Bundle bundle = new Bundle();
            bundle.putLong("cycle", j);
            bundle.putBoolean("accepted", cycleResultApplyInternal.completesRestore());
            try {
                context.getContentResolver().call(Uri.parse(MODES_URI), "driveHookV2", "completeSettings", bundle);
            } catch (RuntimeException e) {
                Log.w(TAG, "ACC settings completion unavailable", e);
            }
        } catch (RuntimeException e2) {
            Log.w(TAG, "ACC settings claim unavailable", e2);
        }
    }

    public static void applyNow(Runnable runnable) {
        enqueueApply("manual apply", true, runnable);
    }

    private static void enqueueApply(String str, final boolean z, final Runnable runnable) {
        long jCurrentRestoreEpoch;
        Handler handlerBg = bg();
        synchronized (RESTORE_LOCK) {
            RestoreRunState restoreRunState = RESTORE_RUN_STATE;
            final long jCurrentGeneration = restoreRunState.currentGeneration();
            restoreRunState.activate(jCurrentGeneration);
            if (z) {
                jCurrentRestoreEpoch = restoreRunState.cancelRestoreAndAdvance();
            } else {
                jCurrentRestoreEpoch = restoreRunState.currentRestoreEpoch();
            }
            final long j = jCurrentRestoreEpoch;
            final long jBeginRestoreGate = beginRestoreGate(str);
            PendingApplySeries.Offer offer = APPLY_SERIES.offer(jBeginRestoreGate, jCurrentGeneration, j, z, runnable);
            if (offer.superseded != null) {
                handlerBg.removeCallbacks(offer.superseded);
            }
            handlerBg.post(offer.toPost);
        }
    }

    public static void postWakeAction(String str, final Runnable runnable) {
        postWakeAction(str, new BooleanSupplier() { // from class: ru.big.town.anative.ApplyEngine$$ExternalSyntheticLambda5
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                return ApplyEngine.lambda$postWakeAction$2(runnable);
            }
        }, (Consumer<WakeActionResult>) null);
    }

    static /* synthetic */ boolean lambda$postWakeAction$2(Runnable runnable) {
        runnable.run();
        return true;
    }

    public static void postWakeAction(String str, final Runnable runnable, final Runnable runnable2) {
        postWakeAction(str, new BooleanSupplier() { // from class: ru.big.town.anative.ApplyEngine$$ExternalSyntheticLambda14
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                return ApplyEngine.lambda$postWakeAction$3(runnable);
            }
        }, (Consumer<WakeActionResult>) new Consumer() { // from class: ru.big.town.anative.ApplyEngine$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ApplyEngine.lambda$postWakeAction$4(runnable2, (ApplyEngine.WakeActionResult) obj);
            }
        });
    }

    static /* synthetic */ boolean lambda$postWakeAction$3(Runnable runnable) {
        runnable.run();
        return true;
    }

    static /* synthetic */ void lambda$postWakeAction$4(Runnable runnable, WakeActionResult wakeActionResult) {
        if (wakeActionResult == WakeActionResult.SUCCESS || runnable == null) {
            return;
        }
        runnable.run();
    }

    public static void postWakeAction(final String str, final BooleanSupplier booleanSupplier, final Consumer<WakeActionResult> consumer) {
        Log.i(TAG, "postWakeAction: " + str);
        Handler handlerCommandBg = commandBg();
        synchronized (RESTORE_LOCK) {
            RestoreRunState restoreRunState = RESTORE_RUN_STATE;
            final long jCurrentGeneration = restoreRunState.currentGeneration();
            if (!restoreRunState.isActionAllowed(jCurrentGeneration)) {
                Log.i(TAG, "postWakeAction [" + str + "] suppressed while frozen");
                if (!handlerCommandBg.post(new Runnable() { // from class: ru.big.town.anative.ApplyEngine$$ExternalSyntheticLambda11
                    @Override // java.lang.Runnable
                    public final void run() {
                        ApplyEngine.deliverWakeActionResult(str, consumer, ApplyEngine.WakeActionResult.SKIPPED);
                    }
                })) {
                    deliverWakeActionResult(str, consumer, WakeActionResult.SKIPPED);
                }
            } else {
                if (handlerCommandBg.post(new Runnable() { // from class: ru.big.town.anative.ApplyEngine$$ExternalSyntheticLambda12
                    @Override // java.lang.Runnable
                    public final void run() {
                        ApplyEngine.runWakeActionExactlyOnce(str, new BooleanSupplier() { // from class: ru.big.town.anative.ApplyEngine$$ExternalSyntheticLambda2
                            @Override // java.util.function.BooleanSupplier
                            public final boolean getAsBoolean() {
                                return ApplyEngine.RESTORE_RUN_STATE.isActionAllowed(jCurrentGeneration);
                            }
                        }, booleanSupplier, consumer);
                    }
                })) {
                    return;
                }
                deliverWakeActionResult(str, consumer, WakeActionResult.SKIPPED);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void runWakeActionExactlyOnce(String str, BooleanSupplier booleanSupplier, final BooleanSupplier booleanSupplier2, Consumer<WakeActionResult> consumer) {
        WakeActionResult wakeActionResult;
        final boolean[] zArr = {false};
        try {
            boolean zRunGuardedSend = CanSender.runGuardedSend(booleanSupplier, new BooleanSupplier() { // from class: ru.big.town.anative.ApplyEngine$$ExternalSyntheticLambda4
                @Override // java.util.function.BooleanSupplier
                public final boolean getAsBoolean() {
                    return ApplyEngine.lambda$runWakeActionExactlyOnce$8(zArr, booleanSupplier2);
                }
            });
            if (!zArr[0]) {
                wakeActionResult = WakeActionResult.SKIPPED;
            } else if (zRunGuardedSend) {
                wakeActionResult = WakeActionResult.SUCCESS;
            } else {
                wakeActionResult = guardAllowed(booleanSupplier) ? WakeActionResult.FAILED : WakeActionResult.SKIPPED;
            }
        } catch (Throwable th) {
            WakeActionResult wakeActionResult2 = WakeActionResult.FAILED;
            safeWakeActionError("postWakeAction [" + str + "] failed: " + th.getMessage(), th);
            wakeActionResult = wakeActionResult2;
        }
        deliverWakeActionResult(str, consumer, wakeActionResult);
    }

    static /* synthetic */ boolean lambda$runWakeActionExactlyOnce$8(boolean[] zArr, BooleanSupplier booleanSupplier) {
        zArr[0] = true;
        return booleanSupplier.getAsBoolean();
    }

    private static boolean guardAllowed(BooleanSupplier booleanSupplier) {
        if (booleanSupplier == null) {
            return true;
        }
        try {
            return booleanSupplier.getAsBoolean();
        } catch (Throwable unused) {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void deliverWakeActionResult(String str, Consumer<WakeActionResult> consumer, WakeActionResult wakeActionResult) {
        if (consumer == null) {
            return;
        }
        try {
            consumer.accept(wakeActionResult);
        } catch (Throwable th) {
            safeWakeActionError("postWakeAction [" + str + "] terminal callback failed", th);
        }
    }

    private static void safeWakeActionError(String str, Throwable th) {
        try {
            Log.e(TAG, str, th);
        } catch (Throwable unused) {
        }
    }

    public static void postUserCommand(String str, Runnable runnable) {
        postUserCommand(str, runnable, null);
    }

    public static void postUserCommand(String str, Runnable runnable, final Runnable runnable2) {
        long jCancelRestoreAndAdvance;
        final long jCancelRestore;
        Log.i(TAG, "postUserCommand: " + str);
        Handler handlerCommandBg = commandBg();
        synchronized (RESTORE_LOCK) {
            jCancelRestoreAndAdvance = RESTORE_RUN_STATE.cancelRestoreAndAdvance();
            jCancelRestore = MODE_SYNC_POLICY.cancelRestore();
        }
        Log.i(TAG, "automatic restore cancelled for user command: restoreEpoch=" + jCancelRestoreAndAdvance + " gateGen=" + jCancelRestore + " reason=" + str);
        enqueueUserCommand(handlerCommandBg, str, runnable, new Runnable() { // from class: ru.big.town.anative.ApplyEngine$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                ApplyEngine.lambda$postUserCommand$9(jCancelRestore, runnable2);
            }
        });
    }

    static /* synthetic */ void lambda$postUserCommand$9(long j, Runnable runnable) {
        boolean zCompleteUserCommand;
        try {
            synchronized (RESTORE_LOCK) {
                zCompleteUserCommand = MODE_SYNC_POLICY.completeUserCommand(j);
            }
            if (zCompleteUserCommand) {
                Log.i(TAG, "user command feedback gate OPEN gen=" + j);
            }
            if (runnable != null) {
                runnable.run();
            }
        } catch (Throwable th) {
            if (runnable != null) {
                runnable.run();
            }
            throw th;
        }
    }

    public static void postIndependentUserCommand(String str, Runnable runnable) {
        postIndependentUserCommand(str, runnable, null);
    }

    public static void postIndependentUserCommand(String str, Runnable runnable, Runnable runnable2) {
        Log.i(TAG, "postIndependentUserCommand: " + str);
        enqueueUserCommand(commandBg(), str, runnable, runnable2);
    }

    private static void enqueueUserCommand(Handler handler, final String str, final Runnable runnable, final Runnable runnable2) {
        if (handler.post(new Runnable() { // from class: ru.big.town.anative.ApplyEngine$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                ApplyEngine.lambda$enqueueUserCommand$10(runnable, str, runnable2);
            }
        })) {
            return;
        }
        Log.e(TAG, "postUserCommand [" + str + "] was not queued");
        if (runnable2 != null) {
            runnable2.run();
        }
    }

    static /* synthetic */ void lambda$enqueueUserCommand$10(Runnable runnable, String str, Runnable runnable2) {
        try {
            runnable.run();
            if (runnable2 != null) {
                runnable2.run();
            }
        } catch (Throwable th) {
            try {
                Log.e(TAG, "postUserCommand [" + str + "] failed: " + th.getMessage(), th);
            } finally {
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static CycleResult applyInternal(Runnable runnable, long j, long j2, long j3, boolean z) {
        Object obj = RESTORE_LOCK;
        CycleResult cycleResult = CycleResult.FAILED;
        try {
            CycleResult cycleResultRunCycle = runCycle(j2, j3, z);
            synchronized (RESTORE_LOCK) {
                if (RESTORE_RUN_STATE.isRestoreCurrent(j2, j3)) {
                    if (cycleResultRunCycle.completesRestore()) {
                        MODE_SYNC_POLICY.completeRestore(j);
                    } else {
                        MODE_SYNC_POLICY.failRestore(j);
                    }
                }
            }
            Log.i(TAG, "restore result=" + cycleResultRunCycle + " gen=" + j);
            if (runnable != null) {
                runnable.run();
            }
            return cycleResultRunCycle;
        } catch (Throwable th) {
            try {
                Log.e(TAG, "runCycle failed: " + th.getMessage(), th);
                synchronized (obj) {
                    return cycleResult;
                }
            } finally {
                synchronized (RESTORE_LOCK) {
                    if (RESTORE_RUN_STATE.isRestoreCurrent(j2, j3)) {
                        if (cycleResult.completesRestore()) {
                            MODE_SYNC_POLICY.completeRestore(j);
                        } else {
                            MODE_SYNC_POLICY.failRestore(j);
                        }
                    }
                    Log.i(TAG, "restore result=" + cycleResult + " gen=" + j);
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            }
        }
    }

    private static CycleResult runCycle(final long j, final long j2, boolean z) {
        byte[][] bArrValidCanFrames;
        BooleanSupplier booleanSupplier = new BooleanSupplier() { // from class: ru.big.town.anative.ApplyEngine$$ExternalSyntheticLambda7
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                return ApplyEngine.RESTORE_RUN_STATE.isRestoreCurrent(j, j2);
            }
        };
        if (!booleanSupplier.getAsBoolean()) {
            return CycleResult.CANCELLED;
        }
        Context context = GlobalVars.SAVE_CONTEXT;
        if (context == null) {
            return CycleResult.FAILED;
        }
        if (z) {
            DriveSelectionStore.applyConfigured(context);
            if (!booleanSupplier.getAsBoolean()) {
                return CycleResult.CANCELLED;
            }
        }
        int iLoadModes = MainActivity.loadModes(context, true);
        if (!booleanSupplier.getAsBoolean()) {
            return CycleResult.CANCELLED;
        }
        if (iLoadModes == 0) {
            Log.w(TAG, "no saved settings; skipping this restore event");
            return CycleResult.FAILED;
        }
        final CanRestorePlan canRestorePlanCreateCanRestorePlan = MainActivity.createCanRestorePlan(z);
        final CanRestorePlan.AttemptResult[] attemptResultArr = {CanRestorePlan.AttemptResult.TRANSIENT_FAILURE};
        boolean zRunGuardedSend = CanSender.runGuardedSend(booleanSupplier, new BooleanSupplier() { // from class: ru.big.town.anative.ApplyEngine$$ExternalSyntheticLambda8
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                return ApplyEngine.lambda$runCycle$13(attemptResultArr, canRestorePlanCreateCanRestorePlan);
            }
        });
        if (!booleanSupplier.getAsBoolean()) {
            return CycleResult.CANCELLED;
        }
        if (!zRunGuardedSend) {
            return CycleResult.FAILED;
        }
        try {
            bArrValidCanFrames = validCanFrames(MainActivity.getCustomCommand());
        } catch (RuntimeException e) {
            Log.w(TAG, "invalid custom CAN command ignored: " + e.getMessage());
            bArrValidCanFrames = new byte[0][];
        }
        final byte[][] bArrCapture = bArrValidCanFrames;
        for (int i = 0; bArrValidCanFrames.length > 0 && i < MainActivity.customCommandCount; i++) {
            if (!booleanSupplier.getAsBoolean()) {
                return CycleResult.CANCELLED;
            }
            CanSender.runGuardedSend(booleanSupplier, new BooleanSupplier() { // from class: ru.big.town.anative.ApplyEngine$$ExternalSyntheticLambda9
                @Override // java.util.function.BooleanSupplier
                public final boolean getAsBoolean() {
                    return MainActivity.setCanValues(1, bArrCapture, "custom command (unlock/wake)");
                }
            });
        }
        if (booleanSupplier.getAsBoolean()) {
            return attemptResultArr[0] == CanRestorePlan.AttemptResult.ACCEPTED_UNCONFIRMED ? CycleResult.ACCEPTED_UNCONFIRMED : CycleResult.SUCCESS;
        }
        return CycleResult.CANCELLED;
    }

    static /* synthetic */ boolean lambda$runCycle$13(CanRestorePlan.AttemptResult[] attemptResultArr, CanRestorePlan canRestorePlan) {
        CanRestorePlan.AttemptResult attemptResultSendPending = canRestorePlan.sendPending(new CanRestorePlan.Sender() { // from class: ru.big.town.anative.ApplyEngine$$ExternalSyntheticLambda13
            @Override // ru.big.town.anative.CanRestorePlan.Sender
            public final boolean send(byte[][] bArr, String str) {
                return MainActivity.setCanValues(1, bArr, str);
            }
        });
        attemptResultArr[0] = attemptResultSendPending;
        return attemptResultSendPending.isComplete();
    }

    static byte[][] validCanFrames(byte[][] bArr) {
        if (bArr == null || bArr.length == 0) {
            return new byte[0][];
        }
        int i = 0;
        for (byte[] bArr2 : bArr) {
            if (bArr2 != null && bArr2.length == 10) {
                i++;
            }
        }
        if (i == 0) {
            return new byte[0][];
        }
        if (i == bArr.length) {
            return bArr;
        }
        byte[][] bArr3 = new byte[i][];
        int i2 = 0;
        for (byte[] bArr4 : bArr) {
            if (bArr4 != null && bArr4.length == 10) {
                bArr3[i2] = bArr4;
                i2++;
            }
        }
        return bArr3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    enum CycleResult {
        SUCCESS,
        ACCEPTED_UNCONFIRMED,
        FAILED,
        CANCELLED;

        boolean completesRestore() {
            return this == SUCCESS || this == ACCEPTED_UNCONFIRMED;
        }
    }

    static final class RestoreRunState {
        private boolean active;
        private long generation;
        private long restoreEpoch;

        RestoreRunState() {
        }

        synchronized long currentGeneration() {
            return this.generation;
        }

        synchronized long currentRestoreEpoch() {
            return this.restoreEpoch;
        }

        synchronized boolean isCurrent(long j) {
            return j == this.generation;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public synchronized boolean isRestoreCurrent(long j, long j2) {
            return j == this.generation && j2 == this.restoreEpoch;
        }

        synchronized boolean activate(long j) {
            if (j != this.generation) {
                return false;
            }
            this.active = true;
            return true;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public synchronized boolean isActionAllowed(long j) {
            return this.active && j == this.generation;
        }

        synchronized long cancelRestoreAndAdvance() {
            long j;
            j = this.restoreEpoch + 1;
            this.restoreEpoch = j;
            return j;
        }

        synchronized long cancelAndAdvance() {
            long j;
            j = this.generation + 1;
            this.generation = j;
            this.restoreEpoch++;
            this.active = false;
            return j;
        }
    }
}
