package android.car;

import android.os.IBinder;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public final class CarBluetoothManager extends CarManagerBase {
    private static final String TAG = "CarBluetoothManager";
    private final ICarBluetooth mService;

    public CarBluetoothManager(Car car, IBinder iBinder) {
        super(car);
        this.mService = ICarBluetooth.Stub.asInterface(iBinder);
    }

    public void connectDevices() {
        try {
            this.mService.connectDevices();
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
    }
}
