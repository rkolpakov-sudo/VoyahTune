package ru.big.town.anative;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
final class PowerHoldPolicy {
    static final String BMS_SOC_DISPLAY = "BMS_SOC_DISPLAY";
    static final int BMS_SOC_DISPLAY_ID = 615;
    static final int ENGINE_EXTENDER_ON = 1;
    static final int MINIMUM_SOC_PERCENT = 15;
    static final int PARKING_ORDINAL = 0;
    static final int PARKING_VALUE = 0;
    static final int PERMANENT_DURATION = 15;
    static final String POWER_HOLD_MODE_SWITCH = "POWER_HOLD_MODE_SWITCH";
    static final int POWER_HOLD_MODE_SWITCH_ID = 1161;
    static final String POWER_HOLD_MODE_TIME = "POWER_HOLD_MODE_TIME";
    static final int POWER_HOLD_MODE_TIME_ID = 1162;
    static final String POWER_HOLD_MODE_WARNING = "POWER_HOLD_MODE_WARNING";
    static final int POWER_HOLD_MODE_WARNING_ID = 1163;
    static final int POWER_HOLD_ON = 1;
    static final String SCENE_MODE_EXTENDER_SET = "SCENE_MODE_EXTENDER_SET";
    static final int SCENE_MODE_EXTENDER_SET_ID = 1127;

    static boolean isParking(int i, int i2) {
        return i == 0 && i2 == 0;
    }

    enum Outcome {
        ACCEPTED(1),
        NOT_IN_PARK(2),
        LOW_BATTERY(3),
        STATE_UNAVAILABLE(4),
        TRANSPORT_FAILURE(5);

        final int ipcCode;

        Outcome(int i) {
            this.ipcCode = i;
        }

        static Outcome fromIpcCode(int i) {
            for (Outcome outcome : values()) {
                if (outcome.ipcCode == i) {
                    return outcome;
                }
            }
            return TRANSPORT_FAILURE;
        }
    }

    private PowerHoldPolicy() {
    }

    static Outcome validate(int i, int i2, int i3) {
        if (!isParking(i, i2)) {
            return Outcome.NOT_IN_PARK;
        }
        if (i3 < 0) {
            return Outcome.STATE_UNAVAILABLE;
        }
        if (i3 < 15) {
            return Outcome.LOW_BATTERY;
        }
        return Outcome.ACCEPTED;
    }

    static Map<String, Integer> activationValues() {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put(POWER_HOLD_MODE_TIME, 15);
        linkedHashMap.put(SCENE_MODE_EXTENDER_SET, 1);
        linkedHashMap.put(POWER_HOLD_MODE_SWITCH, 1);
        return Collections.unmodifiableMap(linkedHashMap);
    }

    static Map<String, Integer> stableIds() {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put(BMS_SOC_DISPLAY, Integer.valueOf(BMS_SOC_DISPLAY_ID));
        linkedHashMap.put(SCENE_MODE_EXTENDER_SET, Integer.valueOf(SCENE_MODE_EXTENDER_SET_ID));
        linkedHashMap.put(POWER_HOLD_MODE_SWITCH, Integer.valueOf(POWER_HOLD_MODE_SWITCH_ID));
        linkedHashMap.put(POWER_HOLD_MODE_TIME, Integer.valueOf(POWER_HOLD_MODE_TIME_ID));
        linkedHashMap.put(POWER_HOLD_MODE_WARNING, Integer.valueOf(POWER_HOLD_MODE_WARNING_ID));
        return Collections.unmodifiableMap(linkedHashMap);
    }
}
