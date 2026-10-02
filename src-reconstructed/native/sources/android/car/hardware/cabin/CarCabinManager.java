package android.car.hardware.cabin;

import android.annotation.SystemApi;
import android.car.Car;
import android.car.CarManagerBase;
import android.car.hardware.CarPropertyConfig;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.property.CarPropertyManager;
import android.car.hardware.property.ICarProperty;
import android.os.IBinder;
import android.util.ArraySet;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
@Deprecated
public final class CarCabinManager extends CarManagerBase {
    public static final int ID_DOOR_LOCK = 371198722;
    public static final int ID_DOOR_MOVE = 373295873;
    public static final int ID_DOOR_POS = 373295872;
    public static final int ID_MIRROR_FOLD = 287312709;
    public static final int ID_MIRROR_LOCK = 287312708;
    public static final int ID_MIRROR_Y_MOVE = 339741507;
    public static final int ID_MIRROR_Y_POS = 339741506;
    public static final int ID_MIRROR_Z_MOVE = 339741505;
    public static final int ID_MIRROR_Z_POS = 339741504;
    public static final int ID_SEAT_BACKREST_ANGLE_1_MOVE = 356518792;
    public static final int ID_SEAT_BACKREST_ANGLE_1_POS = 356518791;
    public static final int ID_SEAT_BACKREST_ANGLE_2_MOVE = 356518794;
    public static final int ID_SEAT_BACKREST_ANGLE_2_POS = 356518793;
    public static final int ID_SEAT_BELT_BUCKLED = 354421634;
    public static final int ID_SEAT_BELT_HEIGHT_MOVE = 356518788;
    public static final int ID_SEAT_BELT_HEIGHT_POS = 356518787;
    public static final int ID_SEAT_DEPTH_MOVE = 356518798;
    public static final int ID_SEAT_DEPTH_POS = 356518797;
    public static final int ID_SEAT_FORE_AFT_MOVE = 356518790;
    public static final int ID_SEAT_FORE_AFT_POS = 356518789;
    public static final int ID_SEAT_HEADREST_ANGLE_MOVE = 356518808;
    public static final int ID_SEAT_HEADREST_ANGLE_POS = 356518807;
    public static final int ID_SEAT_HEADREST_FORE_AFT_MOVE = 356518810;
    public static final int ID_SEAT_HEADREST_FORE_AFT_POS = 356518809;
    public static final int ID_SEAT_HEADREST_HEIGHT_MOVE = 356518806;
    public static final int ID_SEAT_HEADREST_HEIGHT_POS = 356518805;
    public static final int ID_SEAT_HEIGHT_MOVE = 356518796;
    public static final int ID_SEAT_HEIGHT_POS = 356518795;
    public static final int ID_SEAT_LUMBAR_FORE_AFT_MOVE = 356518802;
    public static final int ID_SEAT_LUMBAR_FORE_AFT_POS = 356518801;
    public static final int ID_SEAT_LUMBAR_SIDE_SUPPORT_MOVE = 356518804;
    public static final int ID_SEAT_LUMBAR_SIDE_SUPPORT_POS = 356518803;
    public static final int ID_SEAT_MEMORY_SELECT = 356518784;
    public static final int ID_SEAT_MEMORY_SET = 356518785;
    public static final int ID_SEAT_TILT_MOVE = 356518800;
    public static final int ID_SEAT_TILT_POS = 356518799;
    public static final int ID_WINDOW_LOCK = 322964420;
    public static final int ID_WINDOW_MOVE = 322964417;
    public static final int ID_WINDOW_POS = 322964416;
    private final ArraySet<Integer> mCabinPropertyIds;
    private final ArraySet<CarCabinEventCallback> mCallbacks;
    private final CarPropertyManager mCarPropertyMgr;
    private CarPropertyEventListenerToBase mListenerToBase;
    private final Object mLock;

    public interface CarCabinEventCallback {
        void onChangeEvent(CarPropertyValue carPropertyValue);

        void onErrorEvent(int i, int i2);
    }

    private static class CarPropertyEventListenerToBase implements CarPropertyManager.CarPropertyEventCallback {
        private final WeakReference<CarCabinManager> mManager;

        CarPropertyEventListenerToBase(CarCabinManager carCabinManager) {
            this.mManager = new WeakReference<>(carCabinManager);
        }

        @Override // android.car.hardware.property.CarPropertyManager.CarPropertyEventCallback
        public void onChangeEvent(CarPropertyValue carPropertyValue) {
            CarCabinManager carCabinManager = this.mManager.get();
            if (carCabinManager != null) {
                carCabinManager.handleOnChangeEvent(carPropertyValue);
            }
        }

        @Override // android.car.hardware.property.CarPropertyManager.CarPropertyEventCallback
        public void onErrorEvent(int i, int i2) {
            CarCabinManager carCabinManager = this.mManager.get();
            if (carCabinManager != null) {
                carCabinManager.handleOnErrorEvent(i, i2);
            }
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PropertyId {
    }

    public CarCabinManager(Car car, IBinder iBinder) {
        super(car);
        this.mLock = new Object();
        this.mCallbacks = new ArraySet<>();
        this.mListenerToBase = null;
        this.mCabinPropertyIds = new ArraySet<>(Arrays.asList(373295872, 373295873, 371198722, 339741504, 339741505, 339741506, 339741507, 287312708, 287312709, 356518784, 356518785, 354421634, 356518787, 356518788, 356518789, 356518790, 356518791, 356518792, 356518793, 356518794, 356518795, 356518796, 356518797, 356518798, 356518799, 356518800, 356518801, 356518802, 356518803, 356518804, Integer.valueOf(ID_SEAT_HEADREST_HEIGHT_POS), 356518806, 356518807, 356518808, 356518809, 356518810, 322964416, 322964417, Integer.valueOf(ID_WINDOW_LOCK)));
        this.mCarPropertyMgr = new CarPropertyManager(car, ICarProperty.Stub.asInterface(iBinder));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnChangeEvent(CarPropertyValue carPropertyValue) {
        ArraySet arraySet;
        synchronized (this.mLock) {
            arraySet = new ArraySet((ArraySet) this.mCallbacks);
        }
        Iterator it = arraySet.iterator();
        while (it.hasNext()) {
            ((CarCabinEventCallback) it.next()).onChangeEvent(carPropertyValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnErrorEvent(int i, int i2) {
        ArraySet arraySet;
        synchronized (this.mLock) {
            arraySet = new ArraySet((ArraySet) this.mCallbacks);
        }
        if (arraySet.isEmpty()) {
            return;
        }
        Iterator it = arraySet.iterator();
        while (it.hasNext()) {
            ((CarCabinEventCallback) it.next()).onErrorEvent(i, i2);
        }
    }

    public static boolean isZonedProperty(int i) {
        return true;
    }

    public boolean getBooleanProperty(int i, int i2) {
        return this.mCarPropertyMgr.getBooleanProperty(i, i2);
    }

    public float getFloatProperty(int i, int i2) {
        return this.mCarPropertyMgr.getFloatProperty(i, i2);
    }

    public int getIntProperty(int i, int i2) {
        return this.mCarPropertyMgr.getIntProperty(i, i2);
    }

    public List<CarPropertyConfig> getPropertyList() {
        return this.mCarPropertyMgr.getPropertyList(this.mCabinPropertyIds);
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
        synchronized (this.mLock) {
            this.mCallbacks.clear();
        }
        this.mCarPropertyMgr.onCarDisconnected();
    }

    public void registerCallback(CarCabinEventCallback carCabinEventCallback) {
        List<CarPropertyConfig> propertyList = getPropertyList();
        synchronized (this.mLock) {
            if (this.mListenerToBase == null) {
                this.mListenerToBase = new CarPropertyEventListenerToBase(this);
            }
            Iterator<CarPropertyConfig> it = propertyList.iterator();
            while (it.hasNext()) {
                this.mCarPropertyMgr.registerCallback(this.mListenerToBase, it.next().getPropertyId(), 0.0f);
            }
            this.mCallbacks.add(carCabinEventCallback);
        }
    }

    public void setBooleanProperty(int i, int i2, boolean z) {
        if (this.mCabinPropertyIds.contains(Integer.valueOf(i))) {
            this.mCarPropertyMgr.setBooleanProperty(i, i2, z);
        }
    }

    public void setFloatProperty(int i, int i2, float f) {
        if (this.mCabinPropertyIds.contains(Integer.valueOf(i))) {
            this.mCarPropertyMgr.setFloatProperty(i, i2, f);
        }
    }

    public void setIntProperty(int i, int i2, int i3) {
        if (this.mCabinPropertyIds.contains(Integer.valueOf(i))) {
            this.mCarPropertyMgr.setIntProperty(i, i2, i3);
        }
    }

    public void unregisterCallback(CarCabinEventCallback carCabinEventCallback) {
        synchronized (this.mLock) {
            this.mCallbacks.remove(carCabinEventCallback);
            Iterator<CarPropertyConfig> it = getPropertyList().iterator();
            while (it.hasNext()) {
                this.mCarPropertyMgr.unregisterCallback(this.mListenerToBase, it.next().getPropertyId());
            }
            if (this.mCallbacks.isEmpty()) {
                this.mListenerToBase = null;
            }
        }
    }
}
