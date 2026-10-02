package com.android.car.internal;

import android.car.Car;
import android.car.VehiclePropertyIds;
import android.util.SparseArray;

/* JADX INFO: loaded from: classes.dex */
public final class PropertyPermissionMapping {
    private static final int VENDOR_MASK = 536870912;
    private static final Permission VENDOR_PERMISION = Permission.of(Car.PERMISSION_VENDOR_EXTENSION);
    private final SparseArray<Permission> mPermissions = new SparseArray<>();

    private static class Permission {
        private final String mReadPermission;
        private final String mWritePermission;

        private Permission(String str, String str2) {
            this.mReadPermission = str;
            this.mWritePermission = str2;
        }

        static Permission of(String str) {
            return new Permission(str, str);
        }

        static Permission of(String str, String str2) {
            return new Permission(str, str2);
        }
    }

    public PropertyPermissionMapping() {
        map(Car.PERMISSION_CONTROL_CAR_DOORS, 373295872, 373295873, 371198722);
        map(Car.PERMISSION_CONTROL_CAR_MIRRORS, 339741504, 339741505, 339741506, 339741507, 287312708, 287312709);
        map(Car.PERMISSION_CONTROL_CAR_SEATS, 356518784, 356518785, 354421634, 356518787, 356518788, 356518789, 356518790, 356518791, 356518792, 356518793, 356518794, 356518795, 356518796, 356518797, 356518798, 356518799, 356518800, 356518801, 356518802, 356518803, 356518804, Integer.valueOf(VehiclePropertyIds.SEAT_HEADREST_HEIGHT_POS), 356518806, 356518807, 356518808, 356518809, 356518810);
        map(Car.PERMISSION_CONTROL_CAR_WINDOWS, 322964416, 322964417, Integer.valueOf(VehiclePropertyIds.WINDOW_LOCK));
        map(Car.PERMISSION_CONTROL_CAR_CLIMATE, 356517120, 356517121, 358614274, 358614275, 320865540, 354419973, 354419974, 354419975, 354419976, 354419977, 354419978, 356517131, 339739916, 289408269, 289408270, 356517135, 354419984, 356582673, 354419986, Integer.valueOf(VehiclePropertyIds.HVAC_SEAT_VENTILATION));
        map(Car.PERMISSION_IDENTIFICATION, Integer.valueOf(VehiclePropertyIds.INFO_VIN));
        map(Car.PERMISSION_CAR_INFO, 286261505, 286261506, 289407235, 291504388, 289472773, 291504390, 289472775, 289407240, 289407241, 356516106);
        map(Car.PERMISSION_MILEAGE, 291504644);
        map(Car.PERMISSION_SPEED, 291504647, Integer.valueOf(VehiclePropertyIds.PERF_VEHICLE_SPEED_DISPLAY), 290521862);
        map(Car.PERMISSION_CAR_ENGINE_DETAILED, Integer.valueOf(VehiclePropertyIds.ENGINE_COOLANT_TEMP), 289407747, Integer.valueOf(VehiclePropertyIds.ENGINE_OIL_TEMP), 291504901);
        map(Car.PERMISSION_ENERGY, 291504903, 291504905, 291504908, Integer.valueOf(VehiclePropertyIds.RANGE_REMAINING), Integer.valueOf(VehiclePropertyIds.FUEL_LEVEL_LOW));
        map(Car.PERMISSION_ENERGY_PORTS, 287310600, 287310602, 287310603);
        map(Car.PERMISSION_TIRES, Integer.valueOf(VehiclePropertyIds.TIRE_PRESSURE));
        map(Car.PERMISSION_POWERTRAIN, 289408000, Integer.valueOf(VehiclePropertyIds.CURRENT_GEAR), 287310850, Integer.valueOf(VehiclePropertyIds.PARKING_BRAKE_AUTO_APPLY), 289408009);
        map(Car.PERMISSION_EXTERIOR_ENVIRONMENT, 287310855, 291505923);
        map(Car.PERMISSION_CAR_DYNAMICS_STATE, 287310858, 287310859);
        map(Car.PERMISSION_EXTERIOR_LIGHTS, Integer.valueOf(VehiclePropertyIds.TURN_SIGNAL_STATE), Integer.valueOf(VehiclePropertyIds.HEADLIGHTS_STATE), Integer.valueOf(VehiclePropertyIds.HIGH_BEAM_LIGHTS_STATE), Integer.valueOf(VehiclePropertyIds.FOG_LIGHTS_STATE), Integer.valueOf(VehiclePropertyIds.HAZARD_LIGHTS_STATE));
        map(Car.PERMISSION_EXTERIOR_LIGHTS, Car.PERMISSION_CONTROL_EXTERIOR_LIGHTS, Integer.valueOf(VehiclePropertyIds.HEADLIGHTS_SWITCH), Integer.valueOf(VehiclePropertyIds.HIGH_BEAM_LIGHTS_SWITCH), Integer.valueOf(VehiclePropertyIds.FOG_LIGHTS_SWITCH), Integer.valueOf(VehiclePropertyIds.HAZARD_LIGHTS_SWITCH));
    }

    private Permission getPermission(int i) {
        return isVendorExtension(i) ? VENDOR_PERMISION : this.mPermissions.get(i);
    }

    private static boolean isVendorExtension(int i) {
        return (i & 536870912) == 536870912;
    }

    private void map(Permission permission, Integer[] numArr) {
        for (Integer num : numArr) {
            this.mPermissions.put(num.intValue(), permission);
        }
    }

    private void map(String str, String str2, Integer... numArr) {
        map(Permission.of(str, str2), numArr);
    }

    private void map(String str, Integer... numArr) {
        map(Permission.of(str), numArr);
    }

    public String getReadPermission(int i) {
        Permission permission = getPermission(i);
        if (permission == null) {
            return null;
        }
        return permission.mReadPermission;
    }

    public String getWritePermission(int i) {
        Permission permission = getPermission(i);
        if (permission == null) {
            return null;
        }
        return permission.mWritePermission;
    }
}
