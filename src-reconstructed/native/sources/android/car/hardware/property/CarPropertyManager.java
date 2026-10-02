package android.car.hardware.property;

import android.car.Car;
import android.car.CarManagerBase;
import android.car.VehiclePropertyIds;
import android.car.hardware.CarPropertyConfig;
import android.car.hardware.CarPropertyValue;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceSpecificException;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseArray;
import com.android.car.internal.CarRatedFloatListeners;
import com.android.car.internal.SingleMessageHandler;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class CarPropertyManager extends CarManagerBase {
    public static final int CAR_SET_PROPERTY_ERROR_CODE_ACCESS_DENIED = 4;
    public static final int CAR_SET_PROPERTY_ERROR_CODE_INVALID_ARG = 2;
    public static final int CAR_SET_PROPERTY_ERROR_CODE_PROPERTY_NOT_AVAILABLE = 3;
    public static final int CAR_SET_PROPERTY_ERROR_CODE_TRY_AGAIN = 1;
    public static final int CAR_SET_PROPERTY_ERROR_CODE_UNKNOWN = 5;
    private static final boolean DBG = false;
    private static final int MSG_GENERIC_EVENT = 0;
    public static final float SENSOR_RATE_FAST = 10.0f;
    public static final float SENSOR_RATE_FASTEST = 100.0f;
    public static final float SENSOR_RATE_NORMAL = 1.0f;
    public static final float SENSOR_RATE_ONCHANGE = 0.0f;
    public static final float SENSOR_RATE_UI = 5.0f;
    private static final String TAG = "CarPropertyManager";
    private final SparseArray<CarPropertyListeners> mActivePropertyListener;
    private final int mAppTargetSdk;
    private CarPropertyEventListenerToService mCarPropertyEventToService;
    private final SparseArray<CarPropertyConfig> mConfigMap;
    private final SingleMessageHandler<CarPropertyEvent> mHandler;
    private final ICarProperty mService;

    public interface CarPropertyEventCallback {
        void onChangeEvent(CarPropertyValue carPropertyValue);

        void onErrorEvent(int i, int i2);

        default void onErrorEvent(int i, int i2, int i3) {
            onErrorEvent(i, i2);
        }
    }

    private static class CarPropertyEventListenerToService extends ICarPropertyEventListener.Stub {
        private final WeakReference<CarPropertyManager> mMgr;

        CarPropertyEventListenerToService(CarPropertyManager carPropertyManager) {
            this.mMgr = new WeakReference<>(carPropertyManager);
        }

        @Override // android.car.hardware.property.ICarPropertyEventListener
        public void onEvent(List<CarPropertyEvent> list) throws RemoteException {
            CarPropertyManager carPropertyManager = this.mMgr.get();
            if (carPropertyManager != null) {
                carPropertyManager.handleEvent(list);
            }
        }
    }

    private final class CarPropertyListeners extends CarRatedFloatListeners<CarPropertyEventCallback> {
        final CarPropertyManager this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        CarPropertyListeners(CarPropertyManager carPropertyManager, float f) {
            super(f);
            this.this$0 = carPropertyManager;
        }

        void onErrorEvent(CarPropertyEvent carPropertyEvent) {
            ArrayList arrayList;
            CarPropertyValue<?> carPropertyValue = carPropertyEvent.getCarPropertyValue();
            synchronized (this.this$0.mActivePropertyListener) {
                arrayList = new ArrayList(getListeners());
            }
            arrayList.forEach(new Consumer<CarPropertyEventCallback>(this, carPropertyValue, carPropertyEvent) { // from class: android.car.hardware.property.CarPropertyManager.CarPropertyListeners.2
                final CarPropertyListeners this$1;
                final CarPropertyEvent val$event;
                final CarPropertyValue val$value;

                {
                    this.this$1 = this;
                    this.val$value = carPropertyValue;
                    this.val$event = carPropertyEvent;
                }

                @Override // java.util.function.Consumer
                public void accept(CarPropertyEventCallback carPropertyEventCallback) {
                    carPropertyEventCallback.onErrorEvent(this.val$value.getPropertyId(), this.val$value.getAreaId(), this.val$event.getErrorCode());
                }
            });
        }

        void onPropertyChanged(CarPropertyEvent carPropertyEvent) {
            ArrayList arrayList;
            long timestamp = carPropertyEvent.getCarPropertyValue().getTimestamp();
            if (needUpdateForAreaId(carPropertyEvent.getCarPropertyValue().getAreaId(), timestamp)) {
                synchronized (this.this$0.mActivePropertyListener) {
                    arrayList = new ArrayList(getListeners());
                }
                arrayList.forEach(new Consumer<CarPropertyEventCallback>(this, timestamp, carPropertyEvent) { // from class: android.car.hardware.property.CarPropertyManager.CarPropertyListeners.1
                    final CarPropertyListeners this$1;
                    final CarPropertyEvent val$event;
                    final long val$updateTime;

                    {
                        this.this$1 = this;
                        this.val$updateTime = timestamp;
                        this.val$event = carPropertyEvent;
                    }

                    @Override // java.util.function.Consumer
                    public void accept(CarPropertyEventCallback carPropertyEventCallback) {
                        if (this.this$1.needUpdateForSelectedListener(carPropertyEventCallback, this.val$updateTime)) {
                            carPropertyEventCallback.onChangeEvent(this.val$event.getCarPropertyValue());
                        }
                    }
                });
            }
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface CarSetPropertyErrorCode {
    }

    public CarPropertyManager(Car car, ICarProperty iCarProperty) {
        super(car);
        this.mActivePropertyListener = new SparseArray<>();
        this.mConfigMap = new SparseArray<>();
        this.mService = iCarProperty;
        this.mAppTargetSdk = getContext().getApplicationInfo().targetSdkVersion;
        try {
            for (CarPropertyConfig carPropertyConfig : iCarProperty.getPropertyList()) {
                this.mConfigMap.put(carPropertyConfig.getPropertyId(), carPropertyConfig);
            }
            Handler eventHandler = getEventHandler();
            if (eventHandler == null) {
                this.mHandler = null;
            } else {
                this.mHandler = new SingleMessageHandler<CarPropertyEvent>(this, eventHandler.getLooper(), 0) { // from class: android.car.hardware.property.CarPropertyManager.1
                    final CarPropertyManager this$0;

                    {
                        this.this$0 = this;
                    }

                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // com.android.car.internal.SingleMessageHandler
                    public void handleEvent(CarPropertyEvent carPropertyEvent) {
                        CarPropertyListeners carPropertyListeners;
                        synchronized (this.this$0.mActivePropertyListener) {
                            carPropertyListeners = (CarPropertyListeners) this.this$0.mActivePropertyListener.get(carPropertyEvent.getCarPropertyValue().getPropertyId());
                        }
                        if (carPropertyListeners != null) {
                            int eventType = carPropertyEvent.getEventType();
                            if (eventType == 0) {
                                carPropertyListeners.onPropertyChanged(carPropertyEvent);
                            } else {
                                if (eventType != 1) {
                                    throw new IllegalArgumentException();
                                }
                                carPropertyListeners.onErrorEvent(carPropertyEvent);
                            }
                        }
                    }
                };
            }
        } catch (RemoteException e) {
            Log.e(TAG, "getPropertyList exception ", e);
            throw new RuntimeException(e);
        }
    }

    private void checkSupportedProperty(int i) {
        switch (i) {
            case VehiclePropertyIds.INITIAL_USER_INFO /* 299896583 */:
            case VehiclePropertyIds.SWITCH_USER /* 299896584 */:
            case VehiclePropertyIds.CREATE_USER /* 299896585 */:
            case VehiclePropertyIds.REMOVE_USER /* 299896586 */:
            case VehiclePropertyIds.USER_IDENTIFICATION_ASSOCIATION /* 299896587 */:
                throw new IllegalArgumentException("Unsupported property: " + VehiclePropertyIds.toString(i) + " (" + i + ")");
            default:
                return;
        }
    }

    private void doUnregisterListenerLocked(CarPropertyEventCallback carPropertyEventCallback, int i) {
        CarPropertyListeners carPropertyListeners = this.mActivePropertyListener.get(i);
        if (carPropertyListeners != null) {
            boolean zRemove = carPropertyListeners.contains(carPropertyEventCallback) ? carPropertyListeners.remove(carPropertyEventCallback) : false;
            if (!carPropertyListeners.isEmpty()) {
                if (zRemove) {
                    registerOrUpdatePropertyListener(i, carPropertyListeners.getRate());
                }
            } else {
                try {
                    this.mService.unregisterListener(i, this.mCarPropertyEventToService);
                } catch (RemoteException e) {
                    handleRemoteExceptionFromCarService(e);
                }
                this.mActivePropertyListener.remove(i);
            }
        }
    }

    private <T> T handleCarServiceSpecificException(int i, int i2, int i3, T t) {
        if (i == 1) {
            throw new PropertyNotAvailableAndRetryException(i2, i3);
        }
        if (i == 3) {
            throw new PropertyNotAvailableException(i2, i3);
        }
        if (i == 4) {
            throw new PropertyAccessDeniedSecurityException(i2, i3);
        }
        if (i == 5) {
            throw new CarInternalErrorException(i2, i3);
        }
        Log.e(TAG, "Invalid errorCode: " + i + " in CarService");
        return t;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleEvent(List<CarPropertyEvent> list) {
        SingleMessageHandler<CarPropertyEvent> singleMessageHandler = this.mHandler;
        if (singleMessageHandler != null) {
            singleMessageHandler.sendEvents(list);
        }
    }

    private boolean registerOrUpdatePropertyListener(int i, float f) {
        try {
            this.mService.registerListener(i, f, this.mCarPropertyEventToService);
            return true;
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
        }
    }

    private static int[] toIntArray(Integer[] numArr) {
        int length = numArr.length;
        int[] iArr = new int[length];
        for (int i = 0; i < length; i++) {
            iArr[i] = numArr[i].intValue();
        }
        return iArr;
    }

    public int getAreaId(int i, int i2) {
        checkSupportedProperty(i);
        CarPropertyConfig<?> carPropertyConfig = getCarPropertyConfig(i);
        if (carPropertyConfig == null) {
            throw new IllegalArgumentException("The property propId: 0x" + Integer.toHexString(i) + " is not available");
        }
        if (carPropertyConfig.isGlobalProperty()) {
            return 0;
        }
        for (int i3 : carPropertyConfig.getAreaIds()) {
            if ((i2 & i3) == i2) {
                return i3;
            }
        }
        throw new IllegalArgumentException("The property propId: 0x" + Integer.toHexString(i) + " is not available at the area: 0x" + Integer.toHexString(i2));
    }

    public boolean getBooleanProperty(int i, int i2) {
        checkSupportedProperty(i);
        CarPropertyValue property = getProperty(Boolean.class, i, i2);
        if (property != null) {
            return ((Boolean) property.getValue()).booleanValue();
        }
        return false;
    }

    public CarPropertyConfig<?> getCarPropertyConfig(int i) {
        checkSupportedProperty(i);
        return this.mConfigMap.get(i);
    }

    public float getFloatProperty(int i, int i2) {
        checkSupportedProperty(i);
        CarPropertyValue property = getProperty(Float.class, i, i2);
        if (property != null) {
            return ((Float) property.getValue()).floatValue();
        }
        return 0.0f;
    }

    public int[] getIntArrayProperty(int i, int i2) {
        checkSupportedProperty(i);
        CarPropertyValue property = getProperty(Integer[].class, i, i2);
        return property != null ? toIntArray((Integer[]) property.getValue()) : new int[0];
    }

    public int getIntProperty(int i, int i2) {
        checkSupportedProperty(i);
        CarPropertyValue property = getProperty(Integer.class, i, i2);
        if (property != null) {
            return ((Integer) property.getValue()).intValue();
        }
        return 0;
    }

    public <E> CarPropertyValue<E> getProperty(int i, int i2) {
        Object objHandleCarServiceSpecificException;
        checkSupportedProperty(i);
        try {
            return this.mService.getProperty(i, i2);
        } catch (RemoteException e) {
            objHandleCarServiceSpecificException = handleRemoteExceptionFromCarService(e, null);
            return (CarPropertyValue) objHandleCarServiceSpecificException;
        } catch (ServiceSpecificException e2) {
            if (this.mAppTargetSdk >= 30) {
                objHandleCarServiceSpecificException = handleCarServiceSpecificException(e2.errorCode, i, i2, null);
                return (CarPropertyValue) objHandleCarServiceSpecificException;
            }
            if (e2.errorCode == 1) {
                return null;
            }
            throw new IllegalStateException(String.format("Failed to get property: 0x%x, areaId: 0x%x", Integer.valueOf(i), Integer.valueOf(i2)));
        }
    }

    public <E> CarPropertyValue<E> getProperty(Class<E> cls, int i, int i2) {
        Object objHandleCarServiceSpecificException;
        Class<?> cls2;
        checkSupportedProperty(i);
        try {
            CarPropertyValue<E> property = this.mService.getProperty(i, i2);
            if (property != null && property.getValue() != null && (cls2 = property.getValue().getClass()) != cls) {
                throw new IllegalArgumentException("Invalid property type. Expected: " + cls + ", but was: " + cls2);
            }
            return property;
        } catch (RemoteException e) {
            objHandleCarServiceSpecificException = handleRemoteExceptionFromCarService(e, null);
            return (CarPropertyValue) objHandleCarServiceSpecificException;
        } catch (ServiceSpecificException e2) {
            if (this.mAppTargetSdk >= 30) {
                objHandleCarServiceSpecificException = handleCarServiceSpecificException(e2.errorCode, i, i2, null);
                return (CarPropertyValue) objHandleCarServiceSpecificException;
            }
            if (e2.errorCode == 1) {
                return null;
            }
            throw new IllegalStateException(String.format("Failed to get property: 0x%x, areaId: 0x%x", Integer.valueOf(i), Integer.valueOf(i2)));
        }
    }

    public List<CarPropertyConfig> getPropertyList() {
        ArrayList arrayList = new ArrayList(this.mConfigMap.size());
        for (int i = 0; i < this.mConfigMap.size(); i++) {
            arrayList.add(this.mConfigMap.valueAt(i));
        }
        return arrayList;
    }

    public List<CarPropertyConfig> getPropertyList(ArraySet<Integer> arraySet) {
        ArrayList arrayList = new ArrayList();
        Iterator<Integer> it = arraySet.iterator();
        while (it.hasNext()) {
            int iIntValue = it.next().intValue();
            checkSupportedProperty(iIntValue);
            CarPropertyConfig carPropertyConfig = this.mConfigMap.get(iIntValue);
            if (carPropertyConfig != null) {
                arrayList.add(carPropertyConfig);
            }
        }
        return arrayList;
    }

    public String getReadPermission(int i) {
        checkSupportedProperty(i);
        try {
            return this.mService.getReadPermission(i);
        } catch (RemoteException e) {
            return (String) handleRemoteExceptionFromCarService(e, "");
        }
    }

    public String getWritePermission(int i) {
        checkSupportedProperty(i);
        try {
            return this.mService.getWritePermission(i);
        } catch (RemoteException e) {
            return (String) handleRemoteExceptionFromCarService(e, "");
        }
    }

    public boolean isPropertyAvailable(int i, int i2) {
        checkSupportedProperty(i);
        try {
            CarPropertyValue property = this.mService.getProperty(i, i2);
            return property != null && property.getStatus() == 0;
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
        }
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
        synchronized (this.mActivePropertyListener) {
            this.mActivePropertyListener.clear();
            this.mCarPropertyEventToService = null;
        }
    }

    public boolean registerCallback(CarPropertyEventCallback carPropertyEventCallback, int i, float f) {
        boolean z;
        synchronized (this.mActivePropertyListener) {
            if (this.mCarPropertyEventToService == null) {
                this.mCarPropertyEventToService = new CarPropertyEventListenerToService(this);
            }
            CarPropertyConfig carPropertyConfig = this.mConfigMap.get(i);
            if (carPropertyConfig == null) {
                Log.e(TAG, "registerListener:  propId is not in config list:  " + i);
                return false;
            }
            if (carPropertyConfig.getChangeMode() == 1) {
                f = 0.0f;
            }
            CarPropertyListeners carPropertyListeners = this.mActivePropertyListener.get(i);
            if (carPropertyListeners == null) {
                carPropertyListeners = new CarPropertyListeners(this, f);
                this.mActivePropertyListener.put(i, carPropertyListeners);
                z = true;
            } else {
                z = false;
            }
            if (carPropertyListeners.addAndUpdateRate(carPropertyEventCallback, f)) {
                z = true;
            }
            return !z || registerOrUpdatePropertyListener(i, f);
        }
    }

    public void setBooleanProperty(int i, int i2, boolean z) {
        setProperty(Boolean.class, i, i2, Boolean.valueOf(z));
    }

    public void setFloatProperty(int i, int i2, float f) {
        setProperty(Float.class, i, i2, Float.valueOf(f));
    }

    public void setIntProperty(int i, int i2, int i3) {
        setProperty(Integer.class, i, i2, Integer.valueOf(i3));
    }

    public <E> void setProperty(Class<E> cls, int i, int i2, E e) {
        checkSupportedProperty(i);
        try {
            if (this.mCarPropertyEventToService == null) {
                this.mCarPropertyEventToService = new CarPropertyEventListenerToService(this);
            }
            this.mService.setProperty(new CarPropertyValue(i, i2, e), this.mCarPropertyEventToService);
        } catch (RemoteException e2) {
            handleRemoteExceptionFromCarService(e2);
        } catch (ServiceSpecificException e3) {
            if (this.mAppTargetSdk >= 30) {
                handleCarServiceSpecificException(e3.errorCode, i, i2, null);
            } else {
                if (e3.errorCode != 1) {
                    throw new IllegalStateException(String.format("Failed to set property: 0x%x, areaId: 0x%x", Integer.valueOf(i), Integer.valueOf(i2)));
                }
                throw new RuntimeException(String.format("Failed to set property: 0x%x, areaId: 0x%x", Integer.valueOf(i), Integer.valueOf(i2)));
            }
        }
    }

    public void unregisterCallback(CarPropertyEventCallback carPropertyEventCallback) {
        synchronized (this.mActivePropertyListener) {
            int size = this.mActivePropertyListener.size();
            int[] iArr = new int[size];
            for (int i = 0; i < this.mActivePropertyListener.size(); i++) {
                iArr[i] = this.mActivePropertyListener.keyAt(i);
            }
            for (int i2 = 0; i2 < size; i2++) {
                doUnregisterListenerLocked(carPropertyEventCallback, iArr[i2]);
            }
        }
    }

    public void unregisterCallback(CarPropertyEventCallback carPropertyEventCallback, int i) {
        synchronized (this.mActivePropertyListener) {
            doUnregisterListenerLocked(carPropertyEventCallback, i);
        }
    }
}
