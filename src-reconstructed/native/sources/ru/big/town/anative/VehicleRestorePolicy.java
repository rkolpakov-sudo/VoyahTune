package ru.big.town.anative;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
final class VehicleRestorePolicy {
    static final String PEDESTRIAN_SOUND = "HUM_VSP_FUNCTION_SW";
    static final int PEDESTRIAN_SOUND_DISABLED = 1;
    static final int PEDESTRIAN_SOUND_ENABLED = 2;
    static final int PEDESTRIAN_SOUND_ID = 665;
    static final int REGEN_HIGH = 4;
    static final String REGEN_LEVEL = "HUM_ENERGY_PTREGEN_LEVL";
    static final int REGEN_LEVEL_ID = 619;
    static final int REGEN_LOW = 2;
    static final int REGEN_MEDIUM = 3;
    static final String SAVE_CHARGE_LEVEL = "SREV_SOC_SET";
    static final int SAVE_CHARGE_LEVEL_ID = 1196;
    static final int SOC_EV = 2;
    static final int SOC_FORCE_EV = 5;
    static final String SOC_MODE = "IVI_SOC_MODESET";
    static final int SOC_MODE_ID = 957;
    static final int SOC_REV = 3;
    static final int SOC_SMART = 1;
    static final int SOC_SREV = 4;
    static final String SUSPENSION_MAINTENANCE = "ASC_MAINTAIN_SWITCH";
    static final int SUSPENSION_MAINTENANCE_ID = 711;

    static int pedestrianSoundState(boolean z) {
        return z ? 1 : 2;
    }

    static int suspensionMaintenanceState(boolean z) {
        return z ? 2 : 1;
    }

    static int requireSaveChargeLevel(int i) {
        if (i < 25 || i > 80 || i % 5 != 0) {
            throw new IllegalArgumentException("Unsupported SREV charge target: " + i);
        }
        return (i - 25) / 5;
    }

    private VehicleRestorePolicy() {
    }

    static void appendPrimaryTo(Map<String, Integer> map, boolean z, String str, boolean z2) {
        if (map == null) {
            throw new IllegalArgumentException("Target bundle is null");
        }
        if (z) {
            map.put(SOC_MODE, Integer.valueOf(requireEnergy(str)));
        }
        if (z2) {
            map.put(SOC_MODE, 5);
        }
    }

    static void appendRecuperationTo(Map<String, Integer> map, boolean z, String str, String str2) {
        if (map == null) {
            throw new IllegalArgumentException("Target bundle is null");
        }
        if (z && allowsRecuperationRestore(str2)) {
            map.put(REGEN_LEVEL, Integer.valueOf(requireRecycle(str)));
        }
    }

    static boolean allowsRecuperationRestore(String str) {
        return !"SNOW".equals(str);
    }

    static int requireEnergy(String str) {
        if ("SMART".equals(str) || "Smart".equals(str)) {
            return 1;
        }
        if ("EV".equals(str)) {
            return 2;
        }
        if ("REV".equals(str)) {
            return 3;
        }
        if ("SREV".equals(str)) {
            return 4;
        }
        if ("FORCE_EV".equals(str)) {
            return 5;
        }
        throw new IllegalArgumentException("Unsupported energy mode: " + str);
    }

    static int requireRecycle(String str) {
        if ("LOW".equals(str)) {
            return 2;
        }
        if ("MEDIUM".equals(str)) {
            return 3;
        }
        if ("HIGH".equals(str)) {
            return 4;
        }
        throw new IllegalArgumentException("Unsupported recuperation mode: " + str);
    }

    static Map<String, Integer> stableIds() {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put(SOC_MODE, Integer.valueOf(SOC_MODE_ID));
        linkedHashMap.put(REGEN_LEVEL, Integer.valueOf(REGEN_LEVEL_ID));
        linkedHashMap.put(PEDESTRIAN_SOUND, Integer.valueOf(PEDESTRIAN_SOUND_ID));
        return Collections.unmodifiableMap(linkedHashMap);
    }
}
