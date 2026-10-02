package android.car;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public final class EvConnectorType {
    public static final int CHADEMO = 3;
    public static final int COMBO_1 = 4;
    public static final int COMBO_2 = 5;
    public static final int GBT = 9;
    public static final int GBT_DC = 10;
    public static final int J1772 = 1;
    public static final int MENNEKES = 2;
    public static final int OTHER = 101;
    public static final int SCAME = 11;
    public static final int TESLA_HPWC = 7;
    public static final int TESLA_ROADSTER = 6;
    public static final int TESLA_SUPERCHARGER = 8;
    public static final int UNKNOWN = 0;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Enum {
    }

    private EvConnectorType() {
    }
}
