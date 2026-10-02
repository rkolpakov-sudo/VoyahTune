package android.car.drivingstate;

import android.annotation.SystemApi;
import android.car.Car;
import android.car.CarManagerBase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class CarDrivingStateManager extends CarManagerBase {
    private static final boolean DBG = false;
    private static final int MSG_HANDLE_DRIVING_STATE_CHANGE = 0;
    private static final String TAG = "CarDrivingStateMgr";
    private static final boolean VDBG = false;
    private final ICarDrivingState mDrivingService;
    private CarDrivingStateEventListener mDrvStateEventListener;
    private final EventCallbackHandler mEventCallbackHandler;
    private CarDrivingStateChangeListenerToService mListenerToService;

    private static class CarDrivingStateChangeListenerToService extends ICarDrivingStateChangeListener.Stub {
        private final WeakReference<CarDrivingStateManager> mDrvStateMgr;

        CarDrivingStateChangeListenerToService(CarDrivingStateManager carDrivingStateManager) {
            this.mDrvStateMgr = new WeakReference<>(carDrivingStateManager);
        }

        @Override // android.car.drivingstate.ICarDrivingStateChangeListener
        public void onDrivingStateChanged(CarDrivingStateEvent carDrivingStateEvent) {
            CarDrivingStateManager carDrivingStateManager = this.mDrvStateMgr.get();
            if (carDrivingStateManager != null) {
                carDrivingStateManager.handleDrivingStateChanged(carDrivingStateEvent);
            }
        }
    }

    @SystemApi
    public interface CarDrivingStateEventListener {
        void onDrivingStateChanged(CarDrivingStateEvent carDrivingStateEvent);
    }

    private static final class EventCallbackHandler extends Handler {
        private final WeakReference<CarDrivingStateManager> mDrvStateMgr;

        EventCallbackHandler(CarDrivingStateManager carDrivingStateManager, Looper looper) {
            super(looper);
            this.mDrvStateMgr = new WeakReference<>(carDrivingStateManager);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            CarDrivingStateManager carDrivingStateManager = this.mDrvStateMgr.get();
            if (carDrivingStateManager != null) {
                carDrivingStateManager.dispatchDrivingStateChangeToClient((CarDrivingStateEvent) message.obj);
            }
        }
    }

    public CarDrivingStateManager(Car car, IBinder iBinder) {
        super(car);
        this.mDrivingService = ICarDrivingState.Stub.asInterface(iBinder);
        this.mEventCallbackHandler = new EventCallbackHandler(this, getEventHandler().getLooper());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchDrivingStateChangeToClient(CarDrivingStateEvent carDrivingStateEvent) {
        CarDrivingStateEventListener carDrivingStateEventListener;
        if (carDrivingStateEvent == null) {
            return;
        }
        synchronized (this) {
            carDrivingStateEventListener = this.mDrvStateEventListener;
        }
        if (carDrivingStateEventListener != null) {
            carDrivingStateEventListener.onDrivingStateChanged(carDrivingStateEvent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDrivingStateChanged(CarDrivingStateEvent carDrivingStateEvent) {
        EventCallbackHandler eventCallbackHandler = this.mEventCallbackHandler;
        eventCallbackHandler.sendMessage(eventCallbackHandler.obtainMessage(0, carDrivingStateEvent));
    }

    @SystemApi
    public CarDrivingStateEvent getCurrentCarDrivingState() {
        try {
            return this.mDrivingService.getCurrentDrivingState();
        } catch (RemoteException e) {
            return (CarDrivingStateEvent) handleRemoteExceptionFromCarService(e, null);
        }
    }

    public void injectDrivingState(int i) {
        try {
            this.mDrivingService.injectDrivingState(new CarDrivingStateEvent(i, SystemClock.elapsedRealtimeNanos()));
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
        synchronized (this) {
            this.mListenerToService = null;
            this.mDrvStateEventListener = null;
        }
    }

    @SystemApi
    public void registerListener(CarDrivingStateEventListener carDrivingStateEventListener) {
        synchronized (this) {
            if (carDrivingStateEventListener == null) {
                throw new IllegalArgumentException("Listener is null");
            }
            if (this.mDrvStateEventListener != null) {
                return;
            }
            this.mDrvStateEventListener = carDrivingStateEventListener;
            try {
                if (this.mListenerToService == null) {
                    this.mListenerToService = new CarDrivingStateChangeListenerToService(this);
                }
                this.mDrivingService.registerDrivingStateChangeListener(this.mListenerToService);
            } catch (RemoteException e) {
                handleRemoteExceptionFromCarService(e);
            }
        }
    }

    @SystemApi
    public void unregisterListener() {
        synchronized (this) {
            if (this.mDrvStateEventListener == null) {
                return;
            }
            try {
                this.mDrivingService.unregisterDrivingStateChangeListener(this.mListenerToService);
                this.mDrvStateEventListener = null;
                this.mListenerToService = null;
            } catch (RemoteException e) {
                handleRemoteExceptionFromCarService(e);
            }
        }
    }
}
