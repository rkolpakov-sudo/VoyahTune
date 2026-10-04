package ru.big.town.anative;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
final class FragranceRestorePolicy {
    static final int DEFAULT_DURATION = 0;
    static final int DEFAULT_INTENSITY = 2;
    static final int DEFAULT_TASTE = 1;
    static final String DURATION_STATE = "FCM_DURATION_CONTROL";
    static final int DURATION_STATE_ID = 1067;
    static final String INTENSITY_STATE = "IVI_FRAG_CONCERNTION";
    static final int INTENSITY_STATE_ID = 777;
    static final int SWITCH_ON = 2;
    static final String SWITCH_STATE = "FCM_SW_REQ";
    static final int SWITCH_STATE_ID = 774;
    static final String TASTE_STATE = "IVI_FRAG_TASTE";
    static final int TASTE_STATE_ID = 775;

    private static boolean inRange(int i, int i2, int i3) {
        return i >= i2 && i <= i3;
    }

    private FragranceRestorePolicy() {
    }

    static Settings normalize(int i, int i2, int i3) {
        if (!inRange(i, 1, 3)) {
            i = 1;
        }
        if (!inRange(i2, 0, 2)) {
            i2 = 0;
        }
        if (!inRange(i3, 1, 3)) {
            i3 = 2;
        }
        return new Settings(i, i2, i3);
    }

    static Map<String, Integer> fragranceBundle(Settings settings) {
        if (settings == null) {
            throw new IllegalArgumentException("Fragrance settings are null");
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put(TASTE_STATE, Integer.valueOf(settings.taste));
        linkedHashMap.put(INTENSITY_STATE, Integer.valueOf(settings.intensity));
        linkedHashMap.put(SWITCH_STATE, 2);
        return Collections.unmodifiableMap(linkedHashMap);
    }

    static Map<String, Integer> stableIds() {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put(DURATION_STATE, Integer.valueOf(DURATION_STATE_ID));
        linkedHashMap.put(SWITCH_STATE, Integer.valueOf(SWITCH_STATE_ID));
        linkedHashMap.put(TASTE_STATE, Integer.valueOf(TASTE_STATE_ID));
        linkedHashMap.put(INTENSITY_STATE, Integer.valueOf(INTENSITY_STATE_ID));
        return Collections.unmodifiableMap(linkedHashMap);
    }

    static final class Settings {
        final int duration;
        final int intensity;
        final int taste;

        Settings(int i, int i2, int i3) {
            this.taste = i;
            this.duration = i2;
            this.intensity = i3;
        }
    }
}
