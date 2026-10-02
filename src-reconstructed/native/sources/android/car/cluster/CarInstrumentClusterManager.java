package android.car.cluster;

import android.annotation.SystemApi;
import android.car.Car;
import android.car.CarManagerBase;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
@Deprecated
public class CarInstrumentClusterManager extends CarManagerBase {

    @SystemApi
    @Deprecated
    public static final String CATEGORY_NAVIGATION = "android.car.cluster.NAVIGATION";

    @SystemApi
    @Deprecated
    public static final String KEY_EXTRA_ACTIVITY_STATE = "android.car.cluster.ClusterActivityState";

    @SystemApi
    @Deprecated
    public interface Callback {
        void onClusterActivityStateChanged(String str, Bundle bundle);
    }

    public CarInstrumentClusterManager(Car car, IBinder iBinder) {
        super(car);
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
    }

    @SystemApi
    @Deprecated
    public void registerCallback(String str, Callback callback) {
    }

    @SystemApi
    @Deprecated
    public void startActivity(Intent intent) {
    }

    @SystemApi
    @Deprecated
    public void unregisterCallback(Callback callback) {
    }
}
