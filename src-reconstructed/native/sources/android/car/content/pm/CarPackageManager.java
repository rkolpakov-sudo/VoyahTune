package android.car.content.pm;

import android.annotation.SystemApi;
import android.app.PendingIntent;
import android.car.Car;
import android.car.CarManagerBase;
import android.content.ComponentName;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public final class CarPackageManager extends CarManagerBase {

    @SystemApi
    public static final int FLAG_SET_POLICY_ADD = 2;

    @SystemApi
    public static final int FLAG_SET_POLICY_REMOVE = 4;

    @SystemApi
    public static final int FLAG_SET_POLICY_WAIT_FOR_CHANGE = 1;
    private static final String TAG = "CarPackageManager";
    private final ICarPackageManager mService;

    @Retention(RetentionPolicy.SOURCE)
    public @interface SetPolicyFlags {
    }

    public CarPackageManager(Car car, IBinder iBinder) {
        super(car);
        this.mService = ICarPackageManager.Stub.asInterface(iBinder);
    }

    @SystemApi
    public boolean isActivityBackedBySafeActivity(ComponentName componentName) {
        try {
            return this.mService.isActivityBackedBySafeActivity(componentName);
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
        }
    }

    public boolean isActivityDistractionOptimized(String str, String str2) {
        try {
            return this.mService.isActivityDistractionOptimized(str, str2);
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
        }
    }

    public boolean isPendingIntentDistractionOptimized(PendingIntent pendingIntent) {
        try {
            return this.mService.isPendingIntentDistractionOptimized(pendingIntent);
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
        }
    }

    public boolean isServiceDistractionOptimized(String str, String str2) {
        try {
            return this.mService.isServiceDistractionOptimized(str, str2);
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
        }
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
    }

    public void restartTask(int i) {
        try {
            this.mService.restartTask(i);
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    @SystemApi
    public void setAppBlockingPolicy(String str, CarAppBlockingPolicy carAppBlockingPolicy, int i) {
        if ((i & 1) != 0 && Looper.getMainLooper().isCurrentThread()) {
            throw new IllegalStateException("FLAG_SET_POLICY_WAIT_FOR_CHANGE cannot be used in main thread");
        }
        try {
            this.mService.setAppBlockingPolicy(str, carAppBlockingPolicy, i);
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    public void setEnableActivityBlocking(boolean z) {
        try {
            this.mService.setEnableActivityBlocking(z);
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }
}
