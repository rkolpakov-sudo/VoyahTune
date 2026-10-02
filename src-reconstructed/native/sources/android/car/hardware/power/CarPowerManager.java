package android.car.hardware.power;

import android.annotation.SystemApi;
import android.car.Car;
import android.car.CarManagerBase;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public class CarPowerManager extends CarManagerBase {
    private static final boolean DBG = false;
    private static final String TAG = "CarPowerManager";
    private CompletableFuture<Void> mFuture;
    private CarPowerStateListener mListener;
    private ICarPowerStateListener mListenerToService;
    private CarPowerStateListenerWithCompletion mListenerWithCompletion;
    private final Object mLock;
    private final ICarPower mService;

    public interface CarPowerStateListener {
        public static final int INVALID = 0;
        public static final int ON = 6;
        public static final int SHUTDOWN_CANCELLED = 8;
        public static final int SHUTDOWN_ENTER = 5;
        public static final int SHUTDOWN_PREPARE = 7;
        public static final int SUSPEND_ENTER = 2;
        public static final int SUSPEND_EXIT = 3;
        public static final int WAIT_FOR_VHAL = 1;

        void onStateChanged(int i);
    }

    public interface CarPowerStateListenerWithCompletion {
        void onStateChanged(int i, CompletableFuture<Void> completableFuture);
    }

    public CarPowerManager(Car car, IBinder iBinder) {
        super(car);
        this.mLock = new Object();
        this.mService = ICarPower.Stub.asInterface(iBinder);
    }

    private void cleanupFutureLocked() {
        CompletableFuture<Void> completableFuture = this.mFuture;
        if (completableFuture != null) {
            if (!completableFuture.isDone()) {
                this.mFuture.cancel(false);
            }
            this.mFuture = null;
        }
    }

    private void setServiceForListenerLocked(boolean z) {
        if (this.mListenerToService == null) {
            ICarPowerStateListener.Stub stub = new ICarPowerStateListener.Stub(this, z) { // from class: android.car.hardware.power.CarPowerManager.1
                final CarPowerManager this$0;
                final boolean val$useCompletion;

                {
                    this.this$0 = this;
                    this.val$useCompletion = z;
                }

                @Override // android.car.hardware.power.ICarPowerStateListener
                public void onStateChanged(int i) throws RemoteException {
                    CarPowerStateListener carPowerStateListener;
                    CarPowerStateListenerWithCompletion carPowerStateListenerWithCompletion;
                    CompletableFuture<Void> completableFuture;
                    if (!this.val$useCompletion) {
                        synchronized (this.this$0.mLock) {
                            carPowerStateListener = this.this$0.mListener;
                        }
                        if (carPowerStateListener != null) {
                            carPowerStateListener.onStateChanged(i);
                            return;
                        }
                        return;
                    }
                    synchronized (this.this$0.mLock) {
                        this.this$0.updateFutureLocked(i);
                        carPowerStateListenerWithCompletion = this.this$0.mListenerWithCompletion;
                        completableFuture = this.this$0.mFuture;
                    }
                    if (carPowerStateListenerWithCompletion != null) {
                        carPowerStateListenerWithCompletion.onStateChanged(i, completableFuture);
                    }
                }
            };
            try {
                if (z) {
                    this.mService.registerListenerWithCompletion(stub);
                } else {
                    this.mService.registerListener(stub);
                }
                this.mListenerToService = stub;
            } catch (RemoteException e) {
                handleRemoteExceptionFromCarService(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFutureLocked(int i) {
        cleanupFutureLocked();
        if (i == 7) {
            CompletableFuture<Void> completableFuture = new CompletableFuture<>();
            this.mFuture = completableFuture;
            completableFuture.whenComplete(new BiConsumer(this) { // from class: android.car.hardware.power._$$Lambda$CarPowerManager$OcodOGJnKRrwqzJK2haZpw0lWow
                public final CarPowerManager f$0;

                {
                    this.f$0 = this;
                }

                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    this.f$0.lambda$updateFutureLocked$0$CarPowerManager((Void) obj, (Throwable) obj2);
                }
            });
        }
    }

    public void clearListener() {
        ICarPowerStateListener iCarPowerStateListener;
        synchronized (this.mLock) {
            iCarPowerStateListener = this.mListenerToService;
            this.mListenerToService = null;
            this.mListener = null;
            this.mListenerWithCompletion = null;
            cleanupFutureLocked();
        }
        if (iCarPowerStateListener == null) {
            Log.w(TAG, "unregisterListener: listener was not registered");
            return;
        }
        try {
            this.mService.unregisterListener(iCarPowerStateListener);
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    public int getPowerState() {
        try {
            return this.mService.getPowerState();
        } catch (RemoteException e) {
            return ((Integer) handleRemoteExceptionFromCarService(e, 0)).intValue();
        }
    }

    public /* synthetic */ void lambda$updateFutureLocked$0$CarPowerManager(Void r2, Throwable th) {
        ICarPowerStateListener iCarPowerStateListener;
        if (th != null && !(th instanceof CancellationException)) {
            Log.e(TAG, "Exception occurred while waiting for future", th);
        }
        synchronized (this.mLock) {
            iCarPowerStateListener = this.mListenerToService;
        }
        try {
            this.mService.finished(iCarPowerStateListener);
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
        synchronized (this.mLock) {
            this.mListener = null;
            this.mListenerWithCompletion = null;
        }
    }

    public void requestShutdownOnNextSuspend() {
        try {
            this.mService.requestShutdownOnNextSuspend();
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    public void scheduleNextWakeupTime(int i) {
        try {
            this.mService.scheduleNextWakeupTime(i);
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    public void setListener(CarPowerStateListener carPowerStateListener) {
        synchronized (this.mLock) {
            if (this.mListener != null || this.mListenerWithCompletion != null) {
                throw new IllegalStateException("Listener must be cleared first");
            }
            this.mListener = carPowerStateListener;
            setServiceForListenerLocked(false);
        }
    }

    public void setListenerWithCompletion(CarPowerStateListenerWithCompletion carPowerStateListenerWithCompletion) {
        synchronized (this.mLock) {
            if (this.mListener != null || this.mListenerWithCompletion != null) {
                throw new IllegalStateException("Listener must be cleared first");
            }
            this.mListenerWithCompletion = carPowerStateListenerWithCompletion;
            setServiceForListenerLocked(true);
        }
    }
}
