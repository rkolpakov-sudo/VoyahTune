package ru.big.town.restoremode;

/* JADX INFO: loaded from: classes2.dex */
final class FragranceSettings {
    static final int DEFAULT_DURATION = 0;
    static final boolean DEFAULT_ENABLED = false;
    static final int DEFAULT_INTENSITY = 2;
    static final int DEFAULT_TASTE = 1;
    static final String DURATION = "fragranceDuration";
    static final String ENABLED = "fragranceEnabled";
    static final String INTENSITY = "fragranceIntensity";
    static final String TASTE = "fragranceTaste";

    static int normalizeDuration(int i) {
        if (i < 0 || i > 2) {
            return 0;
        }
        return i;
    }

    static int normalizeIntensity(int i) {
        if (i < 1 || i > 3) {
            return 2;
        }
        return i;
    }

    static int normalizeTaste(int i) {
        if (i < 1 || i > 3) {
            return 1;
        }
        return i;
    }

    private FragranceSettings() {
    }
}
