package android.car;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public final class VehicleOilLevel {
    public static final int CRITICALLY_LOW = 0;
    public static final int ERROR = 4;
    public static final int HIGH = 3;
    public static final int LOW = 1;
    public static final int NORMAL = 2;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Enum {
    }

    private VehicleOilLevel() {
    }
}
