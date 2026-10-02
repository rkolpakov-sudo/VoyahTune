package android.car.occupantawareness;

import android.car.Car;
import android.car.CarManagerBase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class OccupantAwarenessManager extends CarManagerBase {
    private static final boolean DBG = false;
    private static final int MSG_HANDLE_DETECTION_EVENT = 1;
    private static final int MSG_HANDLE_SYSTEM_STATUS_CHANGE = 0;
    private static final String TAG = "OAS.Manager";
    private ChangeCallback mChangeCallback;
    private final EventCallbackHandler mEventCallbackHandler;
    private ChangeListenerToService mListenerToService;
    private final Object mLock;
    private final IOccupantAwarenessManager mOccupantAwarenessService;

    public static abstract class ChangeCallback {
        public abstract void onDetectionEvent(OccupantAwarenessDetection occupantAwarenessDetection);

        public abstract void onSystemStateChanged(SystemStatusEvent systemStatusEvent);
    }

    private static class ChangeListenerToService extends IOccupantAwarenessEventCallback.Stub {
        private final WeakReference<OccupantAwarenessManager> mOccupantAwarenessManager;

        ChangeListenerToService(OccupantAwarenessManager occupantAwarenessManager) {
            this.mOccupantAwarenessManager = new WeakReference<>(occupantAwarenessManager);
        }

        @Override // android.car.occupantawareness.IOccupantAwarenessEventCallback
        public void onDetectionEvent(OccupantAwarenessDetection occupantAwarenessDetection) {
            OccupantAwarenessManager occupantAwarenessManager = this.mOccupantAwarenessManager.get();
            if (occupantAwarenessManager != null) {
                occupantAwarenessManager.handleDetectionEvent(occupantAwarenessDetection);
            }
        }

        @Override // android.car.occupantawareness.IOccupantAwarenessEventCallback
        public void onStatusChanged(SystemStatusEvent systemStatusEvent) {
            OccupantAwarenessManager occupantAwarenessManager = this.mOccupantAwarenessManager.get();
            if (occupantAwarenessManager != null) {
                occupantAwarenessManager.handleSystemStatusChanged(systemStatusEvent);
            }
        }
    }

    private static final class EventCallbackHandler extends Handler {
        private final WeakReference<OccupantAwarenessManager> mOccupantAwarenessManager;

        EventCallbackHandler(OccupantAwarenessManager occupantAwarenessManager, Looper looper) {
            super(looper);
            this.mOccupantAwarenessManager = new WeakReference<>(occupantAwarenessManager);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            OccupantAwarenessManager occupantAwarenessManager = this.mOccupantAwarenessManager.get();
            if (occupantAwarenessManager != null) {
                int i = message.what;
                if (i == 0) {
                    occupantAwarenessManager.dispatchSystemStatusToClient((SystemStatusEvent) message.obj);
                } else if (i == 1) {
                    occupantAwarenessManager.dispatchDetectionEventToClient((OccupantAwarenessDetection) message.obj);
                } else {
                    throw new RuntimeException("Unknown message " + message.what);
                }
            }
        }
    }

    public OccupantAwarenessManager(Car car, IBinder iBinder) {
        super(car);
        this.mLock = new Object();
        this.mOccupantAwarenessService = IOccupantAwarenessManager.Stub.asInterface(iBinder);
        this.mEventCallbackHandler = new EventCallbackHandler(this, getEventHandler().getLooper());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchDetectionEventToClient(OccupantAwarenessDetection occupantAwarenessDetection) {
        ChangeCallback changeCallback;
        if (occupantAwarenessDetection == null) {
            return;
        }
        synchronized (this.mLock) {
            changeCallback = this.mChangeCallback;
        }
        if (changeCallback != null) {
            changeCallback.onDetectionEvent(occupantAwarenessDetection);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchSystemStatusToClient(SystemStatusEvent systemStatusEvent) {
        if (systemStatusEvent == null) {
            return;
        }
        synchronized (this.mLock) {
            ChangeCallback changeCallback = this.mChangeCallback;
            if (changeCallback != null) {
                changeCallback.onSystemStateChanged(systemStatusEvent);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDetectionEvent(OccupantAwarenessDetection occupantAwarenessDetection) {
        EventCallbackHandler eventCallbackHandler = this.mEventCallbackHandler;
        eventCallbackHandler.sendMessage(eventCallbackHandler.obtainMessage(1, occupantAwarenessDetection));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSystemStatusChanged(SystemStatusEvent systemStatusEvent) {
        EventCallbackHandler eventCallbackHandler = this.mEventCallbackHandler;
        eventCallbackHandler.sendMessage(eventCallbackHandler.obtainMessage(0, systemStatusEvent));
    }

    public int getCapabilityForRole(int i) {
        try {
            return this.mOccupantAwarenessService.getCapabilityForRole(i);
        } catch (RemoteException e) {
            return ((Integer) handleRemoteExceptionFromCarService(e, 0)).intValue();
        }
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
        synchronized (this.mLock) {
            this.mChangeCallback = null;
            this.mListenerToService = null;
        }
    }

    public void registerChangeCallback(ChangeCallback changeCallback) {
        synchronized (this.mLock) {
            if (this.mChangeCallback != null) {
                throw new IllegalStateException("Attempting to register a new listener when an existing listener has already been registered.");
            }
            this.mChangeCallback = changeCallback;
            try {
                if (this.mListenerToService == null) {
                    this.mListenerToService = new ChangeListenerToService(this);
                }
                this.mOccupantAwarenessService.registerEventListener(this.mListenerToService);
            } catch (RemoteException e) {
                handleRemoteExceptionFromCarService(e);
            }
        }
    }

    public void unregisterChangeCallback() {
        synchronized (this.mLock) {
            if (this.mChangeCallback == null) {
                Log.e(TAG, "No listener exists to unregister.");
                return;
            }
            this.mChangeCallback = null;
            synchronized (this.mLock) {
                try {
                    this.mOccupantAwarenessService.unregisterEventListener(this.mListenerToService);
                } catch (RemoteException e) {
                    handleRemoteExceptionFromCarService(e);
                }
                this.mListenerToService = null;
            }
        }
    }
}
