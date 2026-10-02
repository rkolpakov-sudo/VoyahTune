package android.car.drivingstate;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface ICarUxRestrictionsChangeListener extends IInterface {

    public static class Default implements ICarUxRestrictionsChangeListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.drivingstate.ICarUxRestrictionsChangeListener
        public void onUxRestrictionsChanged(CarUxRestrictions carUxRestrictions) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarUxRestrictionsChangeListener {
        private static final String DESCRIPTOR = "android.car.drivingstate.ICarUxRestrictionsChangeListener";
        static final int TRANSACTION_onUxRestrictionsChanged = 1;

        private static class Proxy implements ICarUxRestrictionsChangeListener {
            public static ICarUxRestrictionsChangeListener sDefaultImpl;
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

            @Override // android.car.drivingstate.ICarUxRestrictionsChangeListener
            public void onUxRestrictionsChanged(CarUxRestrictions carUxRestrictions) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (carUxRestrictions != null) {
                        parcelObtain.writeInt(1);
                        carUxRestrictions.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(1, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onUxRestrictionsChanged(carUxRestrictions);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICarUxRestrictionsChangeListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarUxRestrictionsChangeListener)) ? new Proxy(iBinder) : (ICarUxRestrictionsChangeListener) iInterfaceQueryLocalInterface;
        }

        public static ICarUxRestrictionsChangeListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarUxRestrictionsChangeListener iCarUxRestrictionsChangeListener) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarUxRestrictionsChangeListener == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarUxRestrictionsChangeListener;
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
                onUxRestrictionsChanged(parcel.readInt() != 0 ? CarUxRestrictions.CREATOR.createFromParcel(parcel) : null);
                return true;
            }
            if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            parcel2.writeString(DESCRIPTOR);
            return true;
        }
    }

    void onUxRestrictionsChanged(CarUxRestrictions carUxRestrictions) throws RemoteException;
}
