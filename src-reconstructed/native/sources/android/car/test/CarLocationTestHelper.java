package android.car.test;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

/* JADX INFO: loaded from: classes.dex */
public class CarLocationTestHelper {
    public static boolean injectLocation(Location location, Context context) {
        return ((LocationManager) context.getSystemService("location")).injectLocation(location);
    }
}
