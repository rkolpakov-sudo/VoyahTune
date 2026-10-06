package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class ModeFeedbackDecoder {
    static final int DRIVE_MODE_VSTATE_ID = 545;
    static final int ENERGY_MODE_VSTATE_ID = 957;
    static final int RECYCLE_MODE_VSTATE_ID = VehicleRestorePolicy.REGEN_LEVEL_ID;

    static final class Feedback {
        final String mode;
        final String modeKey;

        Feedback(String str, String str2) {
            this.modeKey = str;
            this.mode = str2;
        }
    }

    private ModeFeedbackDecoder() {
    }

    static Feedback decode(int i, int i2) {
        if (i == DRIVE_MODE_VSTATE_ID) {
            switch (i2) {
                case 1:
                    return new Feedback("driveMode", "ECO");
                case 2:
                    return new Feedback("driveMode", "COMFORT");
                case 3:
                    return new Feedback("driveMode", "SPORT");
                case 4:
                    return new Feedback("driveMode", "OUTING");
                case 5:
                    return new Feedback("driveMode", "INDIVIDUAL");
                case 6:
                    return new Feedback("driveMode", "SNOW");
                default:
                    return null;
            }
        }
        if (i == ENERGY_MODE_VSTATE_ID) {
            if (i2 == 2) {
                return new Feedback("energy", "EV");
            }
            if (i2 == 3) {
                return new Feedback("energy", "REV");
            }
            if (i2 != 4) {
                return null;
            }
            return new Feedback("energy", "SREV");
        }
        if (i != RECYCLE_MODE_VSTATE_ID) {
            return null;
        }
        if (i2 == 2) {
            return new Feedback("recycle", "LOW");
        }
        if (i2 == 3) {
            return new Feedback("recycle", "MEDIUM");
        }
        if (i2 != 4) {
            return null;
        }
        return new Feedback("recycle", "HIGH");
    }
}
