package android.car;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface ICarOccupantZoneCallback extends IInterface {

    public static class Default implements ICarOccupantZoneCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.ICarOccupantZoneCallback
        public void onOccupantZoneConfigChanged(int i) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarOccupantZoneCallback {
        private static final String DESCRIPTOR = "android.car.ICarOccupantZoneCallback";
        static final int TRANSACTION_onOccupantZoneConfigChanged = 1;

        private static class Proxy implements ICarOccupantZoneCallback {
            public static ICarOccupantZoneCallback sDefaultImpl;
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

            @Override // android.car.ICarOccupantZoneCallback
            public void onOccupantZoneConfigChanged(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(1, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onOccupantZoneConfigChanged(i);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICarOccupantZoneCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarOccupantZoneCallback)) ? new Proxy(iBinder) : (ICarOccupantZoneCallback) iInterfaceQueryLocalInterface;
        }

        public static ICarOccupantZoneCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarOccupantZoneCallback iCarOccupantZoneCallback) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarOccupantZoneCallback == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarOccupantZoneCallback;
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
                onOccupantZoneConfigChanged(parcel.readInt());
                return true;
            }
            if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            parcel2.writeString(DESCRIPTOR);
            return true;
        }
    }

    void onOccupantZoneConfigChanged(int i) throws RemoteException;
}
