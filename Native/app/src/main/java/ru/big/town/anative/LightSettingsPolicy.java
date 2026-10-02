package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class LightSettingsPolicy {

    enum Decision {
        NEED_THRESHOLDS,
        APPLY_WITHOUT_THRESHOLDS,
        COMPLETE_CANCELLED
    }

    private LightSettingsPolicy() {
    }

    static Decision decide(boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        if (z && z2) {
            return Decision.COMPLETE_CANCELLED;
        }
        if (z3 && z4) {
            return Decision.COMPLETE_CANCELLED;
        }
        if (z5) {
            return Decision.APPLY_WITHOUT_THRESHOLDS;
        }
        return Decision.NEED_THRESHOLDS;
    }
}
