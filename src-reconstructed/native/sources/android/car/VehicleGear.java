package android.car;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public final class VehicleGear {
    public static final int GEAR_DRIVE = 8;
    public static final int GEAR_EIGHTH = 2048;
    public static final int GEAR_FIFTH = 256;
    public static final int GEAR_FIRST = 16;
    public static final int GEAR_FOURTH = 128;
    public static final int GEAR_NEUTRAL = 1;
    public static final int GEAR_NINTH = 4096;
    public static final int GEAR_PARK = 4;
    public static final int GEAR_REVERSE = 2;
    public static final int GEAR_SECOND = 32;
    public static final int GEAR_SEVENTH = 1024;
    public static final int GEAR_SIXTH = 512;
    public static final int GEAR_THIRD = 64;
    public static final int GEAR_UNKNOWN = 0;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Enum {
    }

    private VehicleGear() {
    }

    public static String toString(int i) {
        if (i == 0) {
            return "GEAR_UNKNOWN";
        }
        if (i == 1) {
            return "GEAR_NEUTRAL";
        }
        if (i == 2) {
            return "GEAR_REVERSE";
        }
        if (i == 4) {
            return "GEAR_PARK";
        }
        if (i == 8) {
            return "GEAR_DRIVE";
        }
        if (i == 16) {
            return "GEAR_FIRST";
        }
        if (i == 32) {
            return "GEAR_SECOND";
        }
        if (i == 64) {
            return "GEAR_THIRD";
        }
        if (i == 128) {
            return "GEAR_FOURTH";
        }
        if (i == 256) {
            return "GEAR_FIFTH";
        }
        if (i == 512) {
            return "GEAR_SIXTH";
        }
        if (i == 1024) {
            return "GEAR_SEVENTH";
        }
        if (i == 2048) {
            return "GEAR_EIGHTH";
        }
        if (i == 4096) {
            return "GEAR_NINTH";
        }
        return "0x" + Integer.toHexString(i);
    }
}
