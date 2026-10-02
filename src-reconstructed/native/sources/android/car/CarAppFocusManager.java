package android.car;

import android.os.IBinder;
import android.os.RemoteException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public final class CarAppFocusManager extends CarManagerBase {
    public static final int APP_FOCUS_MAX = 2;
    public static final int APP_FOCUS_REQUEST_FAILED = 0;
    public static final int APP_FOCUS_REQUEST_SUCCEEDED = 1;
    public static final int APP_FOCUS_TYPE_NAVIGATION = 1;

    @Deprecated
    public static final int APP_FOCUS_TYPE_VOICE_COMMAND = 2;
    private final Map<OnAppFocusChangedListener, IAppFocusListenerImpl> mChangeBinders;
    private final Map<OnAppFocusOwnershipCallback, IAppFocusOwnershipCallbackImpl> mOwnershipBinders;
    private final IAppFocus mService;

    @Retention(RetentionPolicy.SOURCE)
    public @interface AppFocusRequestResult {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface AppFocusType {
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class IAppFocusListenerImpl extends IAppFocusListener.Stub {
        private final Set<Integer> mAppTypes;
        private final WeakReference<OnAppFocusChangedListener> mListener;
        private final WeakReference<CarAppFocusManager> mManager;

        private IAppFocusListenerImpl(CarAppFocusManager carAppFocusManager, OnAppFocusChangedListener onAppFocusChangedListener) {
            this.mAppTypes = new HashSet();
            this.mManager = new WeakReference<>(carAppFocusManager);
            this.mListener = new WeakReference<>(onAppFocusChangedListener);
        }

        public void addAppType(int i) {
            this.mAppTypes.add(Integer.valueOf(i));
        }

        public Set<Integer> getAppTypes() {
            return this.mAppTypes;
        }

        public boolean hasAppTypes() {
            return !this.mAppTypes.isEmpty();
        }

        @Override // android.car.IAppFocusListener
        public void onAppFocusChanged(final int i, final boolean z) {
            CarAppFocusManager carAppFocusManager = this.mManager.get();
            final OnAppFocusChangedListener onAppFocusChangedListener = this.mListener.get();
            if (carAppFocusManager == null || onAppFocusChangedListener == null) {
                return;
            }
            carAppFocusManager.getEventHandler().post(new Runnable(onAppFocusChangedListener, i, z) { // from class: android.car._$$Lambda$CarAppFocusManager$IAppFocusListenerImpl$GEkdUyrWmIsOwrPHNdFoc6BPTy8
                public final CarAppFocusManager.OnAppFocusChangedListener f$0;
                public final int f$1;
                public final boolean f$2;

                {
                    this.f$0 = onAppFocusChangedListener;
                    this.f$1 = i;
                    this.f$2 = z;
                }

                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.onAppFocusChanged(this.f$1, this.f$2);
                }
            });
        }

        public void removeAppType(int i) {
            this.mAppTypes.remove(Integer.valueOf(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class IAppFocusOwnershipCallbackImpl extends IAppFocusOwnershipCallback.Stub {
        private final Set<Integer> mAppTypes;
        private final WeakReference<OnAppFocusOwnershipCallback> mCallback;
        private final WeakReference<CarAppFocusManager> mManager;

        private IAppFocusOwnershipCallbackImpl(CarAppFocusManager carAppFocusManager, OnAppFocusOwnershipCallback onAppFocusOwnershipCallback) {
            this.mAppTypes = new HashSet();
            this.mManager = new WeakReference<>(carAppFocusManager);
            this.mCallback = new WeakReference<>(onAppFocusOwnershipCallback);
        }

        public void addAppType(int i) {
            this.mAppTypes.add(Integer.valueOf(i));
        }

        public Set<Integer> getAppTypes() {
            return this.mAppTypes;
        }

        public boolean hasAppTypes() {
            return !this.mAppTypes.isEmpty();
        }

        @Override // android.car.IAppFocusOwnershipCallback
        public void onAppFocusOwnershipGranted(final int i) {
            CarAppFocusManager carAppFocusManager = this.mManager.get();
            final OnAppFocusOwnershipCallback onAppFocusOwnershipCallback = this.mCallback.get();
            if (carAppFocusManager == null || onAppFocusOwnershipCallback == null) {
                return;
            }
            carAppFocusManager.getEventHandler().post(new Runnable(onAppFocusOwnershipCallback, i) { // from class: android.car._$$Lambda$CarAppFocusManager$IAppFocusOwnershipCallbackImpl$qTHeNucsOtp4FiO9TiUGyKE18eo
                public final CarAppFocusManager.OnAppFocusOwnershipCallback f$0;
                public final int f$1;

                {
                    this.f$0 = onAppFocusOwnershipCallback;
                    this.f$1 = i;
                }

                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.onAppFocusOwnershipGranted(this.f$1);
                }
            });
        }

        @Override // android.car.IAppFocusOwnershipCallback
        public void onAppFocusOwnershipLost(final int i) {
            CarAppFocusManager carAppFocusManager = this.mManager.get();
            final OnAppFocusOwnershipCallback onAppFocusOwnershipCallback = this.mCallback.get();
            if (carAppFocusManager == null || onAppFocusOwnershipCallback == null) {
                return;
            }
            carAppFocusManager.getEventHandler().post(new Runnable(onAppFocusOwnershipCallback, i) { // from class: android.car._$$Lambda$CarAppFocusManager$IAppFocusOwnershipCallbackImpl$UuCAjgDpuxMhRQhsLVGujMv9AyI
                public final CarAppFocusManager.OnAppFocusOwnershipCallback f$0;
                public final int f$1;

                {
                    this.f$0 = onAppFocusOwnershipCallback;
                    this.f$1 = i;
                }

                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.onAppFocusOwnershipLost(this.f$1);
                }
            });
        }

        public void removeAppType(int i) {
            this.mAppTypes.remove(Integer.valueOf(i));
        }
    }

    public interface OnAppFocusChangedListener {
        void onAppFocusChanged(int i, boolean z);
    }

    public interface OnAppFocusOwnershipCallback {
        void onAppFocusOwnershipGranted(int i);

        void onAppFocusOwnershipLost(int i);
    }

    public CarAppFocusManager(Car car, IBinder iBinder) {
        super(car);
        this.mChangeBinders = new HashMap();
        this.mOwnershipBinders = new HashMap();
        this.mService = IAppFocus.Stub.asInterface(iBinder);
    }

    public void abandonAppFocus(OnAppFocusOwnershipCallback onAppFocusOwnershipCallback) {
        synchronized (this) {
            IAppFocusOwnershipCallbackImpl iAppFocusOwnershipCallbackImplRemove = this.mOwnershipBinders.remove(onAppFocusOwnershipCallback);
            if (iAppFocusOwnershipCallbackImplRemove == null) {
                return;
            }
            try {
                Iterator<Integer> it = iAppFocusOwnershipCallbackImplRemove.getAppTypes().iterator();
                while (it.hasNext()) {
                    this.mService.abandonAppFocus(iAppFocusOwnershipCallbackImplRemove, it.next().intValue());
                }
            } catch (RemoteException e) {
                handleRemoteExceptionFromCarService(e);
            }
        }
    }

    public void abandonAppFocus(OnAppFocusOwnershipCallback onAppFocusOwnershipCallback, int i) {
        if (onAppFocusOwnershipCallback == null) {
            throw new IllegalArgumentException("null callback");
        }
        synchronized (this) {
            IAppFocusOwnershipCallbackImpl iAppFocusOwnershipCallbackImpl = this.mOwnershipBinders.get(onAppFocusOwnershipCallback);
            if (iAppFocusOwnershipCallbackImpl == null) {
                return;
            }
            try {
                this.mService.abandonAppFocus(iAppFocusOwnershipCallbackImpl, i);
            } catch (RemoteException e) {
                handleRemoteExceptionFromCarService(e);
            }
            synchronized (this) {
                iAppFocusOwnershipCallbackImpl.removeAppType(i);
                if (!iAppFocusOwnershipCallbackImpl.hasAppTypes()) {
                    this.mOwnershipBinders.remove(onAppFocusOwnershipCallback);
                }
            }
        }
    }

    public void addFocusListener(OnAppFocusChangedListener onAppFocusChangedListener, int i) {
        IAppFocusListenerImpl iAppFocusListenerImpl;
        if (onAppFocusChangedListener == null) {
            throw new IllegalArgumentException("null listener");
        }
        synchronized (this) {
            iAppFocusListenerImpl = this.mChangeBinders.get(onAppFocusChangedListener);
            if (iAppFocusListenerImpl == null) {
                iAppFocusListenerImpl = new IAppFocusListenerImpl(onAppFocusChangedListener);
                this.mChangeBinders.put(onAppFocusChangedListener, iAppFocusListenerImpl);
            }
            iAppFocusListenerImpl.addAppType(i);
        }
        try {
            this.mService.registerFocusListener(iAppFocusListenerImpl, i);
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    public int[] getActiveAppTypes() {
        try {
            return this.mService.getActiveAppTypes();
        } catch (RemoteException e) {
            return (int[]) handleRemoteExceptionFromCarService(e, new int[0]);
        }
    }

    public boolean isOwningFocus(OnAppFocusOwnershipCallback onAppFocusOwnershipCallback, int i) {
        synchronized (this) {
            IAppFocusOwnershipCallbackImpl iAppFocusOwnershipCallbackImpl = this.mOwnershipBinders.get(onAppFocusOwnershipCallback);
            if (iAppFocusOwnershipCallbackImpl == null) {
                return false;
            }
            try {
                return this.mService.isOwningFocus(iAppFocusOwnershipCallbackImpl, i);
            } catch (RemoteException e) {
                return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
            }
        }
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
    }

    public void removeFocusListener(OnAppFocusChangedListener onAppFocusChangedListener) {
        synchronized (this) {
            IAppFocusListenerImpl iAppFocusListenerImplRemove = this.mChangeBinders.remove(onAppFocusChangedListener);
            if (iAppFocusListenerImplRemove == null) {
                return;
            }
            try {
                Iterator<Integer> it = iAppFocusListenerImplRemove.getAppTypes().iterator();
                while (it.hasNext()) {
                    this.mService.unregisterFocusListener(iAppFocusListenerImplRemove, it.next().intValue());
                }
            } catch (RemoteException e) {
                handleRemoteExceptionFromCarService(e);
            }
        }
    }

    public void removeFocusListener(OnAppFocusChangedListener onAppFocusChangedListener, int i) {
        synchronized (this) {
            IAppFocusListenerImpl iAppFocusListenerImpl = this.mChangeBinders.get(onAppFocusChangedListener);
            if (iAppFocusListenerImpl == null) {
                return;
            }
            try {
                this.mService.unregisterFocusListener(iAppFocusListenerImpl, i);
            } catch (RemoteException e) {
                handleRemoteExceptionFromCarService(e);
            }
            synchronized (this) {
                iAppFocusListenerImpl.removeAppType(i);
                if (!iAppFocusListenerImpl.hasAppTypes()) {
                    this.mChangeBinders.remove(onAppFocusChangedListener);
                }
            }
        }
    }

    public int requestAppFocus(int i, OnAppFocusOwnershipCallback onAppFocusOwnershipCallback) {
        IAppFocusOwnershipCallbackImpl iAppFocusOwnershipCallbackImpl;
        if (onAppFocusOwnershipCallback == null) {
            throw new IllegalArgumentException("null listener");
        }
        synchronized (this) {
            iAppFocusOwnershipCallbackImpl = this.mOwnershipBinders.get(onAppFocusOwnershipCallback);
            if (iAppFocusOwnershipCallbackImpl == null) {
                iAppFocusOwnershipCallbackImpl = new IAppFocusOwnershipCallbackImpl(onAppFocusOwnershipCallback);
                this.mOwnershipBinders.put(onAppFocusOwnershipCallback, iAppFocusOwnershipCallbackImpl);
            }
            iAppFocusOwnershipCallbackImpl.addAppType(i);
        }
        try {
            return this.mService.requestAppFocus(iAppFocusOwnershipCallbackImpl, i);
        } catch (RemoteException e) {
            return ((Integer) handleRemoteExceptionFromCarService(e, 0)).intValue();
        }
    }
}
