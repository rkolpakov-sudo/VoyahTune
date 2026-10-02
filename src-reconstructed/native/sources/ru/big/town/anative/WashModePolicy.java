package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class WashModePolicy {
    static final String CLEANING_MODE = "CAR_CLEANING_MODE_SWITCH";
    static final int CLEANING_MODE_ID = 1133;
    static final int CLEANING_OFF = 0;
    static final int CLEANING_ON = 1;
    static final int PARKING_ORDINAL = 0;
    static final int PARKING_VALUE = 0;

    enum Outcome {
        ACCEPTED,
        NOT_IN_PARK,
        TRANSPORT_FAILURE
    }

    static boolean isParking(int i, int i2) {
        return i == 0 && i2 == 0;
    }

    private WashModePolicy() {
    }
}
