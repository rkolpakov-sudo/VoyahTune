package android.car;

import android.car.hardware.CarPropertyValue;
import android.car.hardware.property.CarPropertyManager;
import android.car.hardware.property.ICarProperty;
import android.os.IBinder;

/* JADX INFO: loaded from: classes.dex */
public final class CarInfoManager extends CarManagerBase {
    public static final int BASIC_INFO_DRIVER_SEAT = 356516106;
    public static final int BASIC_INFO_EV_BATTERY_CAPACITY = 291504390;
    public static final int BASIC_INFO_EV_CONNECTOR_TYPES = 289472775;
    public static final int BASIC_INFO_EV_PORT_LOCATION = 289407241;
    public static final int BASIC_INFO_FUEL_CAPACITY = 291504388;
    public static final int BASIC_INFO_FUEL_DOOR_LOCATION = 289407240;
    public static final int BASIC_INFO_FUEL_TYPES = 289472773;
    public static final int BASIC_INFO_KEY_MANUFACTURER = 286261505;
    public static final int BASIC_INFO_KEY_MODEL = 286261506;
    public static final int BASIC_INFO_KEY_MODEL_YEAR = 289407235;
    public static final String BASIC_INFO_KEY_VEHICLE_ID = "android.car.vehicle-id";
    public static final String INFO_KEY_PRODUCT_CONFIGURATION = "android.car.product-config";
    private final CarPropertyManager mCarPropertyMgr;

    public CarInfoManager(Car car, IBinder iBinder) {
        super(car);
        this.mCarPropertyMgr = new CarPropertyManager(car, ICarProperty.Stub.asInterface(iBinder));
    }

    public int getDriverSeat() {
        return this.mCarPropertyMgr.getIntProperty(356516106, 0);
    }

    public float getEvBatteryCapacity() {
        CarPropertyValue property = this.mCarPropertyMgr.getProperty(Float.class, 291504390, 0);
        if (property != null) {
            return ((Float) property.getValue()).floatValue();
        }
        return 0.0f;
    }

    public int[] getEvConnectorTypes() {
        int[] intArrayProperty = this.mCarPropertyMgr.getIntArrayProperty(289472775, 0);
        int[] iArr = new int[intArrayProperty.length];
        for (int i = 0; i < intArrayProperty.length; i++) {
            int i2 = intArrayProperty[i];
            if (i2 != 101) {
                switch (i2) {
                    case 1:
                        iArr[i] = 1;
                        break;
                    case 2:
                        iArr[i] = 2;
                        break;
                    case 3:
                        iArr[i] = 11;
                        break;
                    case 4:
                        iArr[i] = 3;
                        break;
                    case 5:
                        iArr[i] = 4;
                        break;
                    case 6:
                        iArr[i] = 5;
                        break;
                    case 7:
                        iArr[i] = 6;
                        break;
                    case 8:
                        iArr[i] = 7;
                        break;
                    case 9:
                        iArr[i] = 8;
                        break;
                    case 10:
                        iArr[i] = 9;
                        break;
                    case 11:
                        iArr[i] = 10;
                        break;
                    default:
                        iArr[i] = 0;
                        break;
                }
            } else {
                iArr[i] = 101;
            }
        }
        return iArr;
    }

    public int getEvPortLocation() {
        return this.mCarPropertyMgr.getIntProperty(289407241, 0);
    }

    public float getFuelCapacity() {
        return this.mCarPropertyMgr.getFloatProperty(291504388, 0);
    }

    public int getFuelDoorLocation() {
        return this.mCarPropertyMgr.getIntProperty(289407240, 0);
    }

    public int[] getFuelTypes() {
        return this.mCarPropertyMgr.getIntArrayProperty(289472773, 0);
    }

    public String getManufacturer() {
        CarPropertyValue property = this.mCarPropertyMgr.getProperty(String.class, 286261505, 0);
        return property != null ? (String) property.getValue() : "";
    }

    public String getModel() {
        CarPropertyValue property = this.mCarPropertyMgr.getProperty(String.class, 286261506, 0);
        return property != null ? (String) property.getValue() : "";
    }

    @Deprecated
    public String getModelYear() {
        int intProperty = this.mCarPropertyMgr.getIntProperty(289407235, 0);
        return intProperty == 0 ? "" : Integer.toString(intProperty);
    }

    public int getModelYearInInteger() {
        return this.mCarPropertyMgr.getIntProperty(289407235, 0);
    }

    @Deprecated
    public String getVehicleId() {
        return "";
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
        this.mCarPropertyMgr.onCarDisconnected();
    }
}
