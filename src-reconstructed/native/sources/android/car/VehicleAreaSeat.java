package android.car;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public final class VehicleAreaSeat {
    public static final int SEAT_ROW_1_CENTER = 2;
    public static final int SEAT_ROW_1_LEFT = 1;
    public static final int SEAT_ROW_1_RIGHT = 4;
    public static final int SEAT_ROW_2_CENTER = 32;
    public static final int SEAT_ROW_2_LEFT = 16;
    public static final int SEAT_ROW_2_RIGHT = 64;
    public static final int SEAT_ROW_3_CENTER = 512;
    public static final int SEAT_ROW_3_LEFT = 256;
    public static final int SEAT_ROW_3_RIGHT = 1024;
    public static final int SEAT_UNKNOWN = 0;
    public static final int SIDE_CENTER = 0;
    public static final int SIDE_LEFT = -1;
    public static final int SIDE_RIGHT = 1;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Enum {
    }

    private VehicleAreaSeat() {
    }

    public static int fromRowAndSide(int i, int i2) {
        if (i < 1 || i > 3 || i2 < -1 || i2 > 1) {
            return 0;
        }
        return (1 << ((i - 1) * 4)) << (i2 + 1);
    }
}
