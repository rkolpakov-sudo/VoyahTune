package ru.big.town.anative;

/**
 * IMP-01 (SPEC L43): read-back source table.
 *
 * Maps every tracked feature to the CAN read-back source that confirms it:
 * modes/SREV -> VCU_Indication 0x2FA (TX57 cache); suspension -> ASC 785/959;
 * light -> LightStatus/SWReason; AVAS -> state bit in TX57 cache (IMP-02).
 */
final class ReadBackTable {
    static final String FEATURE_DRIVE_MODE = "driveMode";
    static final String FEATURE_ENERGY = "energy";
    static final String FEATURE_RECYCLE = "recycle";
    static final String FEATURE_SUSPENSION = "suspension";
    static final String FEATURE_LIGHT = "light";
    static final String FEATURE_AVAS = "avas";

    static final String SOURCE_VCU_INDICATION = "VCU_Indication 0x2FA";
    static final String SOURCE_ASC = "ASC 785/959";
    static final String SOURCE_LIGHT_STATUS = "LightStatus";
    static final String SOURCE_LIGHT_SW_REASON = "SWReason";
    static final String SOURCE_AVAS_TX57 = "AVAS state bit (TX57 cache)";

    private ReadBackTable() {
    }

    static boolean isTracked(String str) {
        return FEATURE_DRIVE_MODE.equals(str) || FEATURE_ENERGY.equals(str) || FEATURE_RECYCLE.equals(str) || FEATURE_SUSPENSION.equals(str) || FEATURE_LIGHT.equals(str) || FEATURE_AVAS.equals(str);
    }

    static String sourceFor(String str) {
        if (FEATURE_DRIVE_MODE.equals(str) || FEATURE_ENERGY.equals(str) || FEATURE_RECYCLE.equals(str)) {
            return SOURCE_VCU_INDICATION;
        }
        if (FEATURE_SUSPENSION.equals(str)) {
            return SOURCE_ASC;
        }
        if (FEATURE_LIGHT.equals(str)) {
            return SOURCE_LIGHT_SW_REASON;
        }
        if (FEATURE_AVAS.equals(str)) {
            return SOURCE_AVAS_TX57;
        }
        return null;
    }
}
