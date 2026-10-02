package android.car.diagnostic;

import android.annotation.SystemApi;
import android.car.Car;
import android.car.CarLibLog;
import android.car.CarManagerBase;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import com.android.car.internal.CarPermission;
import com.android.car.internal.CarRatedListeners;
import com.android.car.internal.SingleMessageHandler;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class CarDiagnosticManager extends CarManagerBase {
    public static final int[] FRAME_TYPES = {0, 1};
    public static final int FRAME_TYPE_FREEZE = 1;
    public static final int FRAME_TYPE_LIVE = 0;
    private static final int MSG_DIAGNOSTIC_EVENTS = 0;
    private final SparseArray<CarDiagnosticListeners> mActiveListeners;
    private final SingleMessageHandler<CarDiagnosticEvent> mHandlerCallback;
    private final CarDiagnosticEventListenerToService mListenerToService;
    private final ICarDiagnostic mService;
    private final CarPermission mVendorExtensionPermission;

    private static class CarDiagnosticEventListenerToService extends ICarDiagnosticEventListener.Stub {
        private final WeakReference<CarDiagnosticManager> mManager;

        CarDiagnosticEventListenerToService(CarDiagnosticManager carDiagnosticManager) {
            this.mManager = new WeakReference<>(carDiagnosticManager);
        }

        private void handleOnDiagnosticEvents(CarDiagnosticManager carDiagnosticManager, List<CarDiagnosticEvent> list) {
            carDiagnosticManager.mHandlerCallback.sendEvents(list);
        }

        @Override // android.car.diagnostic.ICarDiagnosticEventListener
        public void onDiagnosticEvents(List<CarDiagnosticEvent> list) {
            CarDiagnosticManager carDiagnosticManager = this.mManager.get();
            if (carDiagnosticManager != null) {
                handleOnDiagnosticEvents(carDiagnosticManager, list);
            }
        }
    }

    private class CarDiagnosticListeners extends CarRatedListeners<OnDiagnosticEventListener> {
        final CarDiagnosticManager this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        CarDiagnosticListeners(CarDiagnosticManager carDiagnosticManager, int i) {
            super(i);
            this.this$0 = carDiagnosticManager;
        }

        void onDiagnosticEvent(CarDiagnosticEvent carDiagnosticEvent) {
            ArrayList arrayList;
            long j = carDiagnosticEvent.timestamp;
            if (j < this.mLastUpdateTime) {
                Log.w(CarLibLog.TAG_DIAGNOSTIC, "dropping old data");
                return;
            }
            this.mLastUpdateTime = j;
            if (!this.this$0.mVendorExtensionPermission.checkGranted()) {
                carDiagnosticEvent = carDiagnosticEvent.withVendorSensorsRemoved();
            }
            synchronized (this.this$0.mActiveListeners) {
                arrayList = new ArrayList(getListeners());
            }
            arrayList.forEach(new Consumer<OnDiagnosticEventListener>(this, carDiagnosticEvent) { // from class: android.car.diagnostic.CarDiagnosticManager.CarDiagnosticListeners.1
                final CarDiagnosticListeners this$1;
                final CarDiagnosticEvent val$eventToDispatch;

                {
                    this.this$1 = this;
                    this.val$eventToDispatch = carDiagnosticEvent;
                }

                @Override // java.util.function.Consumer
                public void accept(OnDiagnosticEventListener onDiagnosticEventListener) {
                    onDiagnosticEventListener.onDiagnosticEvent(this.val$eventToDispatch);
                }
            });
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface FrameType {
    }

    public interface OnDiagnosticEventListener {
        void onDiagnosticEvent(CarDiagnosticEvent carDiagnosticEvent);
    }

    public CarDiagnosticManager(Car car, IBinder iBinder) {
        super(car);
        this.mActiveListeners = new SparseArray<>();
        this.mService = ICarDiagnostic.Stub.asInterface(iBinder);
        this.mHandlerCallback = new SingleMessageHandler<CarDiagnosticEvent>(this, getEventHandler().getLooper(), 0) { // from class: android.car.diagnostic.CarDiagnosticManager.1
            final CarDiagnosticManager this$0;

            {
                this.this$0 = this;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.android.car.internal.SingleMessageHandler
            public void handleEvent(CarDiagnosticEvent carDiagnosticEvent) {
                CarDiagnosticListeners carDiagnosticListeners;
                synchronized (this.this$0.mActiveListeners) {
                    carDiagnosticListeners = (CarDiagnosticListeners) this.this$0.mActiveListeners.get(carDiagnosticEvent.frameType);
                }
                if (carDiagnosticListeners != null) {
                    carDiagnosticListeners.onDiagnosticEvent(carDiagnosticEvent);
                }
            }
        };
        this.mVendorExtensionPermission = new CarPermission(getContext(), Car.PERMISSION_VENDOR_EXTENSION);
        this.mListenerToService = new CarDiagnosticEventListenerToService(this);
    }

    private void assertFrameType(int i) {
        if (i != 0 && i != 1) {
            throw new IllegalArgumentException(String.format("%d is not a valid diagnostic frame type", Integer.valueOf(i)));
        }
    }

    private void doUnregisterListenerLocked(OnDiagnosticEventListener onDiagnosticEventListener, int i) {
        CarDiagnosticListeners carDiagnosticListeners = this.mActiveListeners.get(i);
        if (carDiagnosticListeners != null) {
            boolean zRemove = carDiagnosticListeners.contains(onDiagnosticEventListener) ? carDiagnosticListeners.remove(onDiagnosticEventListener) : false;
            if (!carDiagnosticListeners.isEmpty()) {
                if (zRemove) {
                    registerOrUpdateDiagnosticListener(i, carDiagnosticListeners.getRate());
                }
            } else {
                try {
                    this.mService.unregisterDiagnosticListener(i, this.mListenerToService);
                } catch (RemoteException e) {
                    handleRemoteExceptionFromCarService(e);
                }
                this.mActiveListeners.remove(i);
            }
        }
    }

    private boolean registerOrUpdateDiagnosticListener(int i, int i2) {
        try {
            return this.mService.registerOrUpdateDiagnosticListener(i, i2, this.mListenerToService);
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
        }
    }

    public boolean clearFreezeFrames(long... jArr) {
        try {
            return this.mService.clearFreezeFrames(jArr);
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
        }
    }

    public CarDiagnosticEvent getFreezeFrame(long j) {
        try {
            return this.mService.getFreezeFrame(j);
        } catch (RemoteException e) {
            return (CarDiagnosticEvent) handleRemoteExceptionFromCarService(e, null);
        }
    }

    public long[] getFreezeFrameTimestamps() {
        try {
            return this.mService.getFreezeFrameTimestamps();
        } catch (RemoteException e) {
            return (long[]) handleRemoteExceptionFromCarService(e, new long[0]);
        }
    }

    public CarDiagnosticEvent getLatestLiveFrame() {
        try {
            return this.mService.getLatestLiveFrame();
        } catch (RemoteException e) {
            return (CarDiagnosticEvent) handleRemoteExceptionFromCarService(e, null);
        }
    }

    public boolean isClearFreezeFramesSupported() {
        try {
            return this.mService.isClearFreezeFramesSupported();
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
        }
    }

    public boolean isFreezeFrameNotificationSupported() {
        try {
            return this.mService.isFreezeFrameNotificationSupported();
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
        }
    }

    public boolean isGetFreezeFrameSupported() {
        try {
            return this.mService.isGetFreezeFrameSupported();
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
        }
    }

    public boolean isLiveFrameSupported() {
        try {
            return this.mService.isLiveFrameSupported();
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
        }
    }

    public boolean isSelectiveClearFreezeFramesSupported() {
        try {
            return this.mService.isSelectiveClearFreezeFramesSupported();
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
        }
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
        synchronized (this.mActiveListeners) {
            this.mActiveListeners.clear();
        }
    }

    public boolean registerListener(OnDiagnosticEventListener onDiagnosticEventListener, int i, int i2) {
        boolean z;
        assertFrameType(i);
        synchronized (this.mActiveListeners) {
            CarDiagnosticListeners carDiagnosticListeners = this.mActiveListeners.get(i);
            if (carDiagnosticListeners == null) {
                carDiagnosticListeners = new CarDiagnosticListeners(this, i2);
                this.mActiveListeners.put(i, carDiagnosticListeners);
                z = true;
            } else {
                z = false;
            }
            if (carDiagnosticListeners.addAndUpdateRate(onDiagnosticEventListener, i2)) {
                z = true;
            }
            return !z || registerOrUpdateDiagnosticListener(i, i2);
        }
    }

    public void unregisterListener(OnDiagnosticEventListener onDiagnosticEventListener) {
        synchronized (this.mActiveListeners) {
            for (int i : FRAME_TYPES) {
                doUnregisterListenerLocked(onDiagnosticEventListener, i);
            }
        }
    }
}
