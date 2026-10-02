package android.car.drivingstate;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface ICarDrivingStateChangeListener extends IInterface {

    public static class Default implements ICarDrivingStateChangeListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.drivingstate.ICarDrivingStateChangeListener
        public void onDrivingStateChanged(CarDrivingStateEvent carDrivingStateEvent) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarDrivingStateChangeListener {
        private static final String DESCRIPTOR = "android.car.drivingstate.ICarDrivingStateChangeListener";
        static final int TRANSACTION_onDrivingStateChanged = 1;

        private static class Proxy implements ICarDrivingStateChangeListener {
            public static ICarDrivingStateChangeListener sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.car.drivingstate.ICarDrivingStateChangeListener
            public void onDrivingStateChanged(CarDrivingStateEvent carDrivingStateEvent) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (carDrivingStateEvent != null) {
                        parcelObtain.writeInt(1);
                        carDrivingStateEvent.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(1, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onDrivingStateChanged(carDrivingStateEvent);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICarDrivingStateChangeListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarDrivingStateChangeListener)) ? new Proxy(iBinder) : (ICarDrivingStateChangeListener) iInterfaceQueryLocalInterface;
        }

        public static ICarDrivingStateChangeListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarDrivingStateChangeListener iCarDrivingStateChangeListener) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarDrivingStateChangeListener == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarDrivingStateChangeListener;
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                onDrivingStateChanged(parcel.readInt() != 0 ? CarDrivingStateEvent.CREATOR.createFromParcel(parcel) : null);
                return true;
            }
            if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            parcel2.writeString(DESCRIPTOR);
            return true;
        }
    }

    void onDrivingStateChanged(CarDrivingStateEvent carDrivingStateEvent) throws RemoteException;
}
