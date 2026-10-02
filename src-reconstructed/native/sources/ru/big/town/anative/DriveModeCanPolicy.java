package ru.big.town.anative;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
final class DriveModeCanPolicy {

    enum VehicleStateKey {
        DRIVING_MODE_SET(545),
        EPS_MODE_SET(722),
        PROP_MODE_SET(782);

        final int stableId;

        VehicleStateKey(int i) {
            this.stableId = i;
        }
    }

    static final class IndividualProfile {
        final int accelerator;
        final int steering;

        private IndividualProfile(int i, int i2) {
            this.steering = i;
            this.accelerator = i2;
        }

        static IndividualProfile validated(int i, int i2) {
            if ((i == 2 || i == 3) && i2 >= 1 && i2 <= 3) {
                return new IndividualProfile(i, i2);
            }
            return null;
        }
    }

    static final class Plan {
        private final EnumMap<VehicleStateKey, Integer> values;

        private Plan(EnumMap<VehicleStateKey, Integer> enumMap) {
            this.values = enumMap;
        }

        Map<VehicleStateKey, Integer> values() {
            return Collections.unmodifiableMap(this.values);
        }
    }

    private DriveModeCanPolicy() {
    }

    static boolean isSupported(String str) {
        return "ECO".equals(str) || "COMFORT".equals(str) || "SPORT".equals(str) || "OUTING".equals(str) || "INDIVIDUAL".equals(str) || "SNOW".equals(str);
    }

    static Plan planFor(String str, IndividualProfile individualProfile) {
        if (str == null) {
            return null;
        }
        str.hashCode();
        int i = 5;
        int i2 = 1;
        int i3 = 3;
        switch (str) {
            case "OUTING":
                i = 4;
                i2 = 3;
                i3 = 2;
                EnumMap enumMap = new EnumMap(VehicleStateKey.class);
                enumMap.put(VehicleStateKey.DRIVING_MODE_SET, Integer.valueOf(i));
                enumMap.put(VehicleStateKey.EPS_MODE_SET, Integer.valueOf(i3));
                enumMap.put(VehicleStateKey.PROP_MODE_SET, Integer.valueOf(i2));
                return new Plan(enumMap);
            case "ECO":
                i = 1;
                i3 = 2;
                EnumMap enumMap2 = new EnumMap(VehicleStateKey.class);
                enumMap2.put(VehicleStateKey.DRIVING_MODE_SET, Integer.valueOf(i));
                enumMap2.put(VehicleStateKey.EPS_MODE_SET, Integer.valueOf(i3));
                enumMap2.put(VehicleStateKey.PROP_MODE_SET, Integer.valueOf(i2));
                return new Plan(enumMap2);
            case "SNOW":
                i = 6;
                i2 = 2;
                i3 = i2;
                EnumMap enumMap3 = new EnumMap(VehicleStateKey.class);
                enumMap3.put(VehicleStateKey.DRIVING_MODE_SET, Integer.valueOf(i));
                enumMap3.put(VehicleStateKey.EPS_MODE_SET, Integer.valueOf(i3));
                enumMap3.put(VehicleStateKey.PROP_MODE_SET, Integer.valueOf(i2));
                return new Plan(enumMap3);
            case "SPORT":
                i = 3;
                i2 = 3;
                EnumMap enumMap4 = new EnumMap(VehicleStateKey.class);
                enumMap4.put(VehicleStateKey.DRIVING_MODE_SET, Integer.valueOf(i));
                enumMap4.put(VehicleStateKey.EPS_MODE_SET, Integer.valueOf(i3));
                enumMap4.put(VehicleStateKey.PROP_MODE_SET, Integer.valueOf(i2));
                return new Plan(enumMap4);
            case "INDIVIDUAL":
                if (individualProfile == null) {
                    return null;
                }
                i3 = individualProfile.steering;
                i2 = individualProfile.accelerator;
                EnumMap enumMap5 = new EnumMap(VehicleStateKey.class);
                enumMap5.put(VehicleStateKey.DRIVING_MODE_SET, Integer.valueOf(i));
                enumMap5.put(VehicleStateKey.EPS_MODE_SET, Integer.valueOf(i3));
                enumMap5.put(VehicleStateKey.PROP_MODE_SET, Integer.valueOf(i2));
                return new Plan(enumMap5);
            case "COMFORT":
                i = 2;
                i2 = 2;
                i3 = i2;
                EnumMap enumMap6 = new EnumMap(VehicleStateKey.class);
                enumMap6.put(VehicleStateKey.DRIVING_MODE_SET, Integer.valueOf(i));
                enumMap6.put(VehicleStateKey.EPS_MODE_SET, Integer.valueOf(i3));
                enumMap6.put(VehicleStateKey.PROP_MODE_SET, Integer.valueOf(i2));
                return new Plan(enumMap6);
            default:
                return null;
        }
    }
}
