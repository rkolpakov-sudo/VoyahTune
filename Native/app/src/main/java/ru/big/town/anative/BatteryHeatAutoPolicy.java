package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class BatteryHeatAutoPolicy {
    static final int PLATFORM_H97C = 2;
    static final int PLATFORM_H97X = 1;
    static final int PLATFORM_UNKNOWN = 0;

    static boolean blockingFailure(int i) {
        return i >= 1 && i <= 4;
    }

    static boolean canSend(boolean activeInstance, long expectedCanBusEpoch, long actualCanBusEpoch, long ambientTemperatureEpoch, long expectedDecisionGeneration, long actualDecisionGeneration, boolean autoEnabled, boolean temperatureKnown, boolean temperatureCold, boolean controlBusy, boolean controlBlocked) {
        return activeInstance && expectedCanBusEpoch == actualCanBusEpoch && ambientTemperatureEpoch == actualCanBusEpoch && expectedDecisionGeneration == actualDecisionGeneration && autoEnabled && temperatureKnown && temperatureCold && !controlBusy && !controlBlocked;
    }

    static int effectiveFailure(int i, int i2, int i3, int i4) {
        return (i != 2 && (i == 1 || i3 == i4)) ? i2 : i3;
    }

    static boolean heatingActive(int i, int i2, int i3) {
        return i == 1 || i2 == 1 || i3 == 9;
    }

    static int platform(boolean z, boolean z2) {
        if (z2) {
            return 2;
        }
        return z ? 1 : 0;
    }

    static boolean revisionCurrent(long j, long j2) {
        return j == j2;
    }

    static boolean settingRefreshNeededForTemperature(boolean settingKnown) {
        return !settingKnown;
    }

    static boolean snapshotComplete(int i, int i2, int i3) {
        return (i & i2) == i2 || (i & i3) == i3;
    }

    private BatteryHeatAutoPolicy() {
    }

    static boolean activationConfirmed(int i, int i2, int i3, int i4, int i5) {
        if (heatingActive(i2, i4, i5)) {
            return true;
        }
        return i == 2 && i3 == 1;
    }

    static boolean controlBusy(int i, int i2, int i3, int i4, boolean z) {
        return z || i2 == 1 || i == 2 || heatingActive(i, i3, i4);
    }
}
