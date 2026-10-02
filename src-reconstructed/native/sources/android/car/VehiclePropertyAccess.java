package android.car;

import android.annotation.SystemApi;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class VehiclePropertyAccess {
    public static final int NONE = 0;
    public static final int READ = 1;
    public static final int READ_WRITE = 3;
    public static final int WRITE = 2;

    private VehiclePropertyAccess() {
    }
}
