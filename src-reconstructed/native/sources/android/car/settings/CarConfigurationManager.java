package android.car.settings;

import android.car.Car;
import android.car.CarManagerBase;
import android.os.IBinder;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public class CarConfigurationManager extends CarManagerBase {
    private static final String TAG = "CarConfigurationManager";
    private final ICarConfigurationManager mConfigurationService;

    public CarConfigurationManager(Car car, IBinder iBinder) {
        super(car);
        this.mConfigurationService = ICarConfigurationManager.Stub.asInterface(iBinder);
    }

    public SpeedBumpConfiguration getSpeedBumpConfiguration() {
        try {
            return this.mConfigurationService.getSpeedBumpConfiguration();
        } catch (RemoteException e) {
            return (SpeedBumpConfiguration) handleRemoteExceptionFromCarService(e, null);
        }
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
    }
}
