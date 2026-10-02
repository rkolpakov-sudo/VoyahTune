package android.car;

import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public abstract class CarManagerBase {
    protected final Car mCar;

    public CarManagerBase(Car car) {
        this.mCar = car;
    }

    protected Context getContext() {
        return this.mCar.getContext();
    }

    protected Handler getEventHandler() {
        return this.mCar.getEventHandler();
    }

    protected <T> T handleRemoteExceptionFromCarService(RemoteException remoteException, T t) {
        return (T) this.mCar.handleRemoteExceptionFromCarService(remoteException, t);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleRemoteExceptionFromCarService(RemoteException remoteException) {
        this.mCar.handleRemoteExceptionFromCarService(remoteException);
    }

    protected abstract void onCarDisconnected();
}
