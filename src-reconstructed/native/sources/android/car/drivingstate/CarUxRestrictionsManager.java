package android.car.drivingstate;

import android.car.Car;
import android.car.CarManagerBase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class CarUxRestrictionsManager extends CarManagerBase {
    private static final boolean DBG = false;
    private static final int MSG_HANDLE_UX_RESTRICTIONS_CHANGE = 0;
    private static final String TAG = "CarUxRManager";
    public static final String UX_RESTRICTION_MODE_BASELINE = "baseline";
    private static final boolean VDBG = false;
    private int mDisplayId;
    private final EventCallbackHandler mEventCallbackHandler;
    private CarUxRestrictionsChangeListenerToService mListenerToService;
    private final IRemoteCallback mRequester;
    private OnUxRestrictionsChangedListener mUxRListener;
    private final ICarUxRestrictionsManager mUxRService;

    private static class CarUxRestrictionsChangeListenerToService extends ICarUxRestrictionsChangeListener.Stub {
        private final WeakReference<CarUxRestrictionsManager> mUxRestrictionsManager;

        CarUxRestrictionsChangeListenerToService(CarUxRestrictionsManager carUxRestrictionsManager) {
            this.mUxRestrictionsManager = new WeakReference<>(carUxRestrictionsManager);
        }

        @Override // android.car.drivingstate.ICarUxRestrictionsChangeListener
        public void onUxRestrictionsChanged(CarUxRestrictions carUxRestrictions) {
            CarUxRestrictionsManager carUxRestrictionsManager = this.mUxRestrictionsManager.get();
            if (carUxRestrictionsManager != null) {
                carUxRestrictionsManager.handleUxRestrictionsChanged(carUxRestrictions);
            }
        }
    }

    private static final class EventCallbackHandler extends Handler {
        private final WeakReference<CarUxRestrictionsManager> mUxRestrictionsManager;

        EventCallbackHandler(CarUxRestrictionsManager carUxRestrictionsManager, Looper looper) {
            super(looper);
            this.mUxRestrictionsManager = new WeakReference<>(carUxRestrictionsManager);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            CarUxRestrictionsManager carUxRestrictionsManager = this.mUxRestrictionsManager.get();
            if (carUxRestrictionsManager != null) {
                carUxRestrictionsManager.dispatchUxRChangeToClient((CarUxRestrictions) message.obj);
            }
        }
    }

    public interface OnUxRestrictionsChangedListener {
        void onUxRestrictionsChanged(CarUxRestrictions carUxRestrictions);
    }

    public CarUxRestrictionsManager(Car car, IBinder iBinder) {
        super(car);
        this.mDisplayId = -1;
        this.mRequester = new IRemoteCallback.Stub(this) { // from class: android.car.drivingstate.CarUxRestrictionsManager.1
            final CarUxRestrictionsManager this$0;

            {
                this.this$0 = this;
            }

            public void sendResult(Bundle bundle) {
            }
        };
        this.mUxRService = ICarUxRestrictionsManager.Stub.asInterface(iBinder);
        this.mEventCallbackHandler = new EventCallbackHandler(this, getEventHandler().getLooper());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchUxRChangeToClient(CarUxRestrictions carUxRestrictions) {
        if (carUxRestrictions == null) {
            return;
        }
        synchronized (this) {
            OnUxRestrictionsChangedListener onUxRestrictionsChangedListener = this.mUxRListener;
            if (onUxRestrictionsChangedListener != null) {
                onUxRestrictionsChangedListener.onUxRestrictionsChanged(carUxRestrictions);
            }
        }
    }

    private int getDisplayId() {
        int i = this.mDisplayId;
        if (i != -1) {
            return i;
        }
        this.mDisplayId = getContext().getDisplayId();
        Log.i(TAG, "Context returns display ID " + this.mDisplayId);
        if (this.mDisplayId == -1) {
            this.mDisplayId = 0;
            Log.e(TAG, "Could not retrieve display id. Using default: " + this.mDisplayId);
        }
        return this.mDisplayId;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUxRestrictionsChanged(CarUxRestrictions carUxRestrictions) {
        EventCallbackHandler eventCallbackHandler = this.mEventCallbackHandler;
        eventCallbackHandler.sendMessage(eventCallbackHandler.obtainMessage(0, carUxRestrictions));
    }

    public List<CarUxRestrictionsConfiguration> getConfigs() {
        try {
            return this.mUxRService.getConfigs();
        } catch (RemoteException e) {
            return (List) handleRemoteExceptionFromCarService(e, null);
        }
    }

    public CarUxRestrictions getCurrentCarUxRestrictions() {
        return getCurrentCarUxRestrictions(getDisplayId());
    }

    public CarUxRestrictions getCurrentCarUxRestrictions(int i) {
        try {
            return this.mUxRService.getCurrentUxRestrictions(i);
        } catch (RemoteException e) {
            return (CarUxRestrictions) handleRemoteExceptionFromCarService(e, null);
        }
    }

    public int getMappedPhysicalDisplayOfVirtualDisplay(int i) {
        try {
            return this.mUxRService.getMappedPhysicalDisplayOfVirtualDisplay(i);
        } catch (RemoteException e) {
            return ((Integer) handleRemoteExceptionFromCarService(e, 0)).intValue();
        }
    }

    public String getRestrictionMode() {
        try {
            return this.mUxRService.getRestrictionMode();
        } catch (RemoteException e) {
            return (String) handleRemoteExceptionFromCarService(e, null);
        }
    }

    public List<CarUxRestrictionsConfiguration> getStagedConfigs() {
        try {
            return this.mUxRService.getStagedConfigs();
        } catch (RemoteException e) {
            return (List) handleRemoteExceptionFromCarService(e, null);
        }
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
        this.mListenerToService = null;
        synchronized (this) {
            this.mUxRListener = null;
        }
    }

    public void registerListener(OnUxRestrictionsChangedListener onUxRestrictionsChangedListener) {
        registerListener(onUxRestrictionsChangedListener, getDisplayId());
    }

    public void registerListener(OnUxRestrictionsChangedListener onUxRestrictionsChangedListener, int i) {
        synchronized (this) {
            if (this.mUxRListener != null) {
                return;
            }
            this.mUxRListener = onUxRestrictionsChangedListener;
            try {
                if (this.mListenerToService == null) {
                    this.mListenerToService = new CarUxRestrictionsChangeListenerToService(this);
                }
                this.mUxRService.registerUxRestrictionsChangeListener(this.mListenerToService, i);
            } catch (RemoteException e) {
                handleRemoteExceptionFromCarService(e);
            }
        }
    }

    public void reportVirtualDisplayToPhysicalDisplay(int i, int i2) {
        try {
            this.mUxRService.reportVirtualDisplayToPhysicalDisplay(this.mRequester, i, i2);
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    public boolean saveUxRestrictionsConfigurationForNextBoot(CarUxRestrictionsConfiguration carUxRestrictionsConfiguration) {
        return saveUxRestrictionsConfigurationForNextBoot(Arrays.asList(carUxRestrictionsConfiguration));
    }

    public boolean saveUxRestrictionsConfigurationForNextBoot(List<CarUxRestrictionsConfiguration> list) {
        try {
            return this.mUxRService.saveUxRestrictionsConfigurationForNextBoot(list);
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
        }
    }

    public boolean setRestrictionMode(String str) {
        Objects.requireNonNull(str, "mode must not be null");
        try {
            return this.mUxRService.setRestrictionMode(str);
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
        }
    }

    public void unregisterListener() {
        synchronized (this) {
            if (this.mUxRListener == null) {
                return;
            }
            this.mUxRListener = null;
            try {
                this.mUxRService.unregisterUxRestrictionsChangeListener(this.mListenerToService);
            } catch (RemoteException e) {
                handleRemoteExceptionFromCarService(e);
            }
        }
    }
}
