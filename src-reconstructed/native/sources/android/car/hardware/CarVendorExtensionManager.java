package android.car.hardware;

import android.annotation.SystemApi;
import android.car.Car;
import android.car.CarManagerBase;
import android.car.hardware.property.CarPropertyManager;
import android.car.hardware.property.ICarProperty;
import android.os.IBinder;
import android.util.ArraySet;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
@Deprecated
public final class CarVendorExtensionManager extends CarManagerBase {
    private static final boolean DBG = false;
    private static final String TAG = "CarVendorExtensionManager";
    private final ArraySet<CarVendorExtensionCallback> mCallbacks;
    private CarPropertyEventListenerToBase mListenerToBase;
    private final Object mLock;
    private final CarPropertyManager mPropertyManager;

    private static class CarPropertyEventListenerToBase implements CarPropertyManager.CarPropertyEventCallback {
        private final WeakReference<CarVendorExtensionManager> mManager;

        CarPropertyEventListenerToBase(CarVendorExtensionManager carVendorExtensionManager) {
            this.mManager = new WeakReference<>(carVendorExtensionManager);
        }

        @Override // android.car.hardware.property.CarPropertyManager.CarPropertyEventCallback
        public void onChangeEvent(CarPropertyValue carPropertyValue) {
            CarVendorExtensionManager carVendorExtensionManager = this.mManager.get();
            if (carVendorExtensionManager != null) {
                carVendorExtensionManager.handleOnChangeEvent(carPropertyValue);
            }
        }

        @Override // android.car.hardware.property.CarPropertyManager.CarPropertyEventCallback
        public void onErrorEvent(int i, int i2) {
            CarVendorExtensionManager carVendorExtensionManager = this.mManager.get();
            if (carVendorExtensionManager != null) {
                carVendorExtensionManager.handleOnErrorEvent(i, i2);
            }
        }
    }

    public interface CarVendorExtensionCallback {
        void onChangeEvent(CarPropertyValue carPropertyValue);

        void onErrorEvent(int i, int i2);
    }

    public CarVendorExtensionManager(Car car, IBinder iBinder) {
        super(car);
        this.mCallbacks = new ArraySet<>();
        this.mLock = new Object();
        this.mListenerToBase = null;
        this.mPropertyManager = new CarPropertyManager(car, ICarProperty.Stub.asInterface(iBinder));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnChangeEvent(CarPropertyValue carPropertyValue) {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mCallbacks);
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ((CarVendorExtensionCallback) it.next()).onChangeEvent(carPropertyValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnErrorEvent(int i, int i2) {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mCallbacks);
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ((CarVendorExtensionCallback) it.next()).onErrorEvent(i, i2);
        }
    }

    public <E> E getGlobalProperty(Class<E> cls, int i) {
        return (E) getProperty(cls, i, 0);
    }

    public List<CarPropertyConfig> getProperties() {
        return this.mPropertyManager.getPropertyList();
    }

    public <E> E getProperty(Class<E> cls, int i, int i2) {
        return this.mPropertyManager.getProperty(cls, i, i2).getValue();
    }

    public boolean isPropertyAvailable(int i, int i2) {
        return this.mPropertyManager.isPropertyAvailable(i, i2);
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
        synchronized (this.mLock) {
            this.mCallbacks.clear();
        }
        this.mPropertyManager.onCarDisconnected();
    }

    public void registerCallback(CarVendorExtensionCallback carVendorExtensionCallback) {
        synchronized (this.mLock) {
            if (this.mCallbacks.isEmpty()) {
                this.mListenerToBase = new CarPropertyEventListenerToBase(this);
            }
            Iterator<CarPropertyConfig> it = this.mPropertyManager.getPropertyList().iterator();
            while (it.hasNext()) {
                this.mPropertyManager.registerCallback(this.mListenerToBase, it.next().getPropertyId(), 0.0f);
            }
            this.mCallbacks.add(carVendorExtensionCallback);
        }
    }

    public <E> void setGlobalProperty(Class<E> cls, int i, E e) {
        this.mPropertyManager.setProperty(cls, i, 0, e);
    }

    public <E> void setProperty(Class<E> cls, int i, int i2, E e) {
        this.mPropertyManager.setProperty(cls, i, i2, e);
    }

    public void unregisterCallback(CarVendorExtensionCallback carVendorExtensionCallback) {
        synchronized (this.mLock) {
            this.mCallbacks.remove(carVendorExtensionCallback);
            Iterator<CarPropertyConfig> it = this.mPropertyManager.getPropertyList().iterator();
            while (it.hasNext()) {
                this.mPropertyManager.unregisterCallback(this.mListenerToBase, it.next().getPropertyId());
            }
            if (this.mCallbacks.isEmpty()) {
                this.mListenerToBase = null;
            }
        }
    }
}
