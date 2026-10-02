package android.car.watchdog;

import android.annotation.SystemApi;
import android.car.Car;
import android.car.CarManagerBase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.util.Preconditions;
import com.android.internal.util.function.pooled.PooledLambda;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class CarWatchdogManager extends CarManagerBase {
    private static final boolean DEBUG = false;
    private static final int INVALID_SESSION_ID = -1;
    private static final int NUMBER_OF_CONDITIONS_TO_BE_MET = 2;
    private static final String TAG = "CarWatchdogManager";
    public static final int TIMEOUT_CRITICAL = 0;
    public static final int TIMEOUT_MODERATE = 1;
    public static final int TIMEOUT_NORMAL = 2;
    private static final int WHAT_CHECK_MAIN_THREAD = 1;
    private Executor mCallbackExecutor;
    private final ICarWatchdogClientImpl mClientImpl;
    private final Object mLock;
    private final Handler mMainHandler;
    private CarWatchdogClientCallback mRegisteredClient;
    private int mRemainingConditions;
    private final ICarWatchdogService mService;
    private SessionInfo mSession;

    public static abstract class CarWatchdogClientCallback {
        public boolean onCheckHealthStatus(int i, int i2) {
            return false;
        }

        public void onPrepareProcessTermination() {
        }
    }

    private static final class ICarWatchdogClientImpl extends ICarWatchdogServiceCallback.Stub {
        private final WeakReference<CarWatchdogManager> mManager;

        private ICarWatchdogClientImpl(CarWatchdogManager carWatchdogManager) {
            this.mManager = new WeakReference<>(carWatchdogManager);
        }

        @Override // android.car.watchdog.ICarWatchdogServiceCallback
        public void onCheckHealthStatus(int i, int i2) {
            CarWatchdogManager carWatchdogManager = this.mManager.get();
            if (carWatchdogManager != null) {
                carWatchdogManager.checkClientStatus(i, i2);
            }
        }

        @Override // android.car.watchdog.ICarWatchdogServiceCallback
        public void onPrepareProcessTermination() {
            CarWatchdogManager carWatchdogManager = this.mManager.get();
            if (carWatchdogManager != null) {
                carWatchdogManager.notifyProcessTermination();
            }
        }
    }

    private final class SessionInfo {
        public int currentId;
        public int lastReportedId;
        final CarWatchdogManager this$0;

        SessionInfo(CarWatchdogManager carWatchdogManager, int i, int i2) {
            this.this$0 = carWatchdogManager;
            this.currentId = i;
            this.lastReportedId = i2;
        }
    }

    @Target({ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TimeoutLengthEnum {
    }

    public CarWatchdogManager(Car car, IBinder iBinder) {
        super(car);
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mLock = new Object();
        this.mSession = new SessionInfo(this, -1, -1);
        this.mService = ICarWatchdogService.Stub.asInterface(iBinder);
        this.mClientImpl = new ICarWatchdogClientImpl();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkClientStatus(final int i, final int i2) {
        this.mMainHandler.removeMessages(1);
        synchronized (this.mLock) {
            if (this.mRegisteredClient == null) {
                Log.w(TAG, "Cannot check client status. The client has not been registered.");
                return;
            }
            this.mSession.currentId = i;
            final CarWatchdogClientCallback carWatchdogClientCallback = this.mRegisteredClient;
            Executor executor = this.mCallbackExecutor;
            this.mRemainingConditions = 2;
            this.mMainHandler.sendMessage(PooledLambda.obtainMessage(_$$Lambda$CarWatchdogManager$kFmjLtJdjtDl6LIbITNQIMVk37Y.INSTANCE, this).setWhat(1));
            executor.execute(new Runnable(this, carWatchdogClientCallback, i, i2) { // from class: android.car.watchdog._$$Lambda$CarWatchdogManager$dZ8USxGrd1QOKUXHqW13UuI_OcA
                public final CarWatchdogManager f$0;
                public final CarWatchdogManager.CarWatchdogClientCallback f$1;
                public final int f$2;
                public final int f$3;

                {
                    this.f$0 = this;
                    this.f$1 = carWatchdogClientCallback;
                    this.f$2 = i;
                    this.f$3 = i2;
                }

                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$checkClientStatus$0$CarWatchdogManager(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    private boolean checkConditionLocked() {
        if (this.mRemainingConditions < 0) {
            Log.wtf(TAG, "Remaining condition is less than zero: should not happen");
        }
        return this.mRemainingConditions == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkMainThread() {
        int i;
        boolean zCheckConditionLocked;
        synchronized (this.mLock) {
            this.mRemainingConditions--;
            i = this.mSession.currentId;
            zCheckConditionLocked = checkConditionLocked();
        }
        if (zCheckConditionLocked) {
            reportToService(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyProcessTermination() {
        synchronized (this.mLock) {
            final CarWatchdogClientCallback carWatchdogClientCallback = this.mRegisteredClient;
            if (carWatchdogClientCallback == null) {
                Log.w(TAG, "Cannot notify the client. The client has not been registered.");
            } else {
                this.mCallbackExecutor.execute(new Runnable(carWatchdogClientCallback) { // from class: android.car.watchdog._$$Lambda$CarWatchdogManager$169iZNfvofq4iuoUIGB9oxuYchQ
                    public final CarWatchdogManager.CarWatchdogClientCallback f$0;

                    {
                        this.f$0 = carWatchdogClientCallback;
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.onPrepareProcessTermination();
                    }
                });
            }
        }
    }

    private void reportToService(int i) {
        try {
            this.mService.tellClientAlive(this.mClientImpl, i);
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    public /* synthetic */ void lambda$checkClientStatus$0$CarWatchdogManager(CarWatchdogClientCallback carWatchdogClientCallback, int i, int i2) {
        if (carWatchdogClientCallback.onCheckHealthStatus(i, i2)) {
            synchronized (this.mLock) {
                if (this.mSession.lastReportedId == i) {
                    return;
                }
                this.mSession.lastReportedId = i;
                this.mRemainingConditions--;
                boolean zCheckConditionLocked = checkConditionLocked();
                if (zCheckConditionLocked) {
                    reportToService(i);
                }
            }
        }
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
    }

    public void registerClient(Executor executor, CarWatchdogClientCallback carWatchdogClientCallback, int i) {
        synchronized (this.mLock) {
            CarWatchdogClientCallback carWatchdogClientCallback2 = this.mRegisteredClient;
            if (carWatchdogClientCallback2 == carWatchdogClientCallback) {
                return;
            }
            if (carWatchdogClientCallback2 != null) {
                throw new IllegalStateException("Cannot register the client. Only one client can be registered.");
            }
            this.mRegisteredClient = carWatchdogClientCallback;
            this.mCallbackExecutor = executor;
            try {
                this.mService.registerClient(this.mClientImpl, i);
            } catch (RemoteException e) {
                synchronized (this.mLock) {
                    this.mRegisteredClient = null;
                    handleRemoteExceptionFromCarService(e);
                }
            }
        }
    }

    public void tellClientAlive(CarWatchdogClientCallback carWatchdogClientCallback, int i) {
        synchronized (this.mLock) {
            if (this.mRegisteredClient != carWatchdogClientCallback) {
                throw new IllegalStateException("Cannot report client status. The client has not been registered.");
            }
            Preconditions.checkArgument(i != -1 && this.mSession.currentId == i, "Cannot report client status. The given session id doesn't match the current one.");
            if (this.mSession.lastReportedId == i) {
                Log.w(TAG, "The given session id is already reported.");
                return;
            }
            this.mSession.lastReportedId = i;
            this.mRemainingConditions--;
            boolean zCheckConditionLocked = checkConditionLocked();
            if (zCheckConditionLocked) {
                reportToService(i);
            }
        }
    }

    public void unregisterClient(CarWatchdogClientCallback carWatchdogClientCallback) {
        synchronized (this.mLock) {
            if (this.mRegisteredClient != carWatchdogClientCallback) {
                Log.w(TAG, "Cannot unregister the client. It has not been registered.");
                return;
            }
            this.mRegisteredClient = null;
            this.mCallbackExecutor = null;
            try {
                this.mService.unregisterClient(this.mClientImpl);
            } catch (RemoteException e) {
                handleRemoteExceptionFromCarService(e);
            }
        }
    }
}
