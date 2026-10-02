package ru.big.town.anative;

import android.util.Log;
import java.util.function.BooleanSupplier;

/* JADX INFO: loaded from: classes2.dex */
public final class CanSender {
    private static final int CAN_FRAME_SIZE = 10;
    public static final String TAG = "$$$ CanSender $$$";
    private static final Object NATIVE_SEND_LOCK = new Object();
    private static final ThreadLocal<BooleanSupplier> SEND_GUARD = new ThreadLocal<>();
    private static final ThreadLocal<Runnable> FRAME_ATTEMPT = new ThreadLocal<>();

    private CanSender() {
    }

    public static boolean send(int i, byte[] bArr, String str) {
        if (bArr == null || bArr.length != 10) {
            Log.w(TAG, "Invalid CAN frame suppressed [" + str + "]: length=" + (bArr == null ? "null" : Integer.valueOf(bArr.length)));
            return false;
        }
        if (!sendAllowed()) {
            return false;
        }
        synchronized (NATIVE_SEND_LOCK) {
            if (!beginFrameAttemptForCurrentGuard()) {
                return false;
            }
            int iCis_can_control_bytes = MainActivity.cis_can_control_bytes(i, bArr);
            if (iCis_can_control_bytes == 0) {
                return true;
            }
            Log.w(TAG, "CAN send failed (res=" + iCis_can_control_bytes + ") [" + str + "]");
            return false;
        }
    }

    public static boolean send(int i, byte[][] bArr, String str) {
        if (bArr == null) {
            return true;
        }
        synchronized (NATIVE_SEND_LOCK) {
            for (byte[] bArr2 : bArr) {
                if (!sendAllowed()) {
                    return false;
                }
                if (!send(i, bArr2, str)) {
                    return false;
                }
            }
            return true;
        }
    }

    static boolean runGuardedSend(BooleanSupplier booleanSupplier, BooleanSupplier booleanSupplier2) {
        return runGuardedSend(booleanSupplier, null, booleanSupplier2);
    }

    static boolean runGuardedSend(BooleanSupplier booleanSupplier, Runnable runnable, BooleanSupplier booleanSupplier2) {
        ThreadLocal<BooleanSupplier> threadLocal = SEND_GUARD;
        BooleanSupplier booleanSupplier3 = threadLocal.get();
        Runnable runnable2 = FRAME_ATTEMPT.get();
        threadLocal.set(combine(booleanSupplier3, booleanSupplier));
        setFrameAttempt(combine(runnable2, runnable));
        try {
            return sendAllowed() && booleanSupplier2.getAsBoolean();
        } finally {
            restoreGuard(booleanSupplier3);
            restoreFrameAttempt(runnable2);
        }
    }

    static void runGuardedAction(BooleanSupplier booleanSupplier, Runnable runnable) {
        ThreadLocal<BooleanSupplier> threadLocal = SEND_GUARD;
        BooleanSupplier booleanSupplier2 = threadLocal.get();
        threadLocal.set(combine(booleanSupplier2, booleanSupplier));
        try {
            if (sendAllowed()) {
                runnable.run();
            }
        } finally {
            restoreGuard(booleanSupplier2);
        }
    }

    private static BooleanSupplier combine(final BooleanSupplier booleanSupplier, final BooleanSupplier booleanSupplier2) {
        if (booleanSupplier == null) {
            return booleanSupplier2;
        }
        return booleanSupplier2 == null ? booleanSupplier : new BooleanSupplier() { // from class: ru.big.town.anative.CanSender$$ExternalSyntheticLambda1
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                return CanSender.lambda$combine$0(booleanSupplier, booleanSupplier2);
            }
        };
    }

    static /* synthetic */ boolean lambda$combine$0(BooleanSupplier booleanSupplier, BooleanSupplier booleanSupplier2) {
        return booleanSupplier.getAsBoolean() && booleanSupplier2.getAsBoolean();
    }

    private static Runnable combine(final Runnable runnable, final Runnable runnable2) {
        if (runnable == null) {
            return runnable2;
        }
        return runnable2 == null ? runnable : new Runnable() { // from class: ru.big.town.anative.CanSender$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                CanSender.lambda$combine$1(runnable, runnable2);
            }
        };
    }

    static /* synthetic */ void lambda$combine$1(Runnable runnable, Runnable runnable2) {
        runnable.run();
        runnable2.run();
    }

    private static void restoreGuard(BooleanSupplier booleanSupplier) {
        if (booleanSupplier == null) {
            SEND_GUARD.remove();
        } else {
            SEND_GUARD.set(booleanSupplier);
        }
    }

    private static void setFrameAttempt(Runnable runnable) {
        if (runnable == null) {
            FRAME_ATTEMPT.remove();
        } else {
            FRAME_ATTEMPT.set(runnable);
        }
    }

    private static void restoreFrameAttempt(Runnable runnable) {
        setFrameAttempt(runnable);
    }

    private static void notifyFrameAttempt() {
        Runnable runnable = FRAME_ATTEMPT.get();
        if (runnable != null) {
            runnable.run();
        }
    }

    static boolean beginFrameAttemptForCurrentGuard() {
        if (!sendAllowed()) {
            return false;
        }
        notifyFrameAttempt();
        return true;
    }

    private static boolean sendAllowed() {
        BooleanSupplier booleanSupplier = SEND_GUARD.get();
        if (booleanSupplier == null) {
            return true;
        }
        try {
            return booleanSupplier.getAsBoolean();
        } catch (Throwable th) {
            Log.e(TAG, "CAN guard failed; frame suppressed", th);
            return false;
        }
    }

    public static boolean send(int i, byte[] bArr) {
        return send(i, bArr, (String) null);
    }

    public static boolean send(int i, byte[][] bArr) {
        return send(i, bArr, (String) null);
    }
}
