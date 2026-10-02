package android.car;

import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface ILocationManagerProxy extends IInterface {

    public static class Default implements ILocationManagerProxy {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.ILocationManagerProxy
        public Location getLastKnownLocation(String str) throws RemoteException {
            return null;
        }

        @Override // android.car.ILocationManagerProxy
        public boolean injectLocation(Location location) throws RemoteException {
            return false;
        }

        @Override // android.car.ILocationManagerProxy
        public boolean isLocationEnabled() throws RemoteException {
            return false;
        }
    }

    public static abstract class Stub extends Binder implements ILocationManagerProxy {
        private static final String DESCRIPTOR = "android.car.ILocationManagerProxy";
        static final int TRANSACTION_getLastKnownLocation = 3;
        static final int TRANSACTION_injectLocation = 2;
        static final int TRANSACTION_isLocationEnabled = 1;

        private static class Proxy implements ILocationManagerProxy {
            public static ILocationManagerProxy sDefaultImpl;
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

            @Override // android.car.ILocationManagerProxy
            public Location getLastKnownLocation(String str) throws RemoteException {
                Location lastKnownLocation;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    if (this.mRemote.transact(3, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        lastKnownLocation = parcelObtain2.readInt() != 0 ? (Location) Location.CREATOR.createFromParcel(parcelObtain2) : null;
                    } else {
                        lastKnownLocation = Stub.getDefaultImpl().getLastKnownLocation(str);
                    }
                    return lastKnownLocation;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ILocationManagerProxy
            public boolean injectLocation(Location location) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (location != null) {
                        parcelObtain.writeInt(1);
                        location.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (!this.mRemote.transact(2, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().injectLocation(location);
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ILocationManagerProxy
            public boolean isLocationEnabled() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isLocationEnabled();
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ILocationManagerProxy asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ILocationManagerProxy)) ? new Proxy(iBinder) : (ILocationManagerProxy) iInterfaceQueryLocalInterface;
        }

        public static ILocationManagerProxy getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ILocationManagerProxy iLocationManagerProxy) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iLocationManagerProxy == null) {
                return false;
            }
            Proxy.sDefaultImpl = iLocationManagerProxy;
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            int iIsLocationEnabled;
            if (i == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                iIsLocationEnabled = isLocationEnabled();
            } else {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 1598968902) {
                            return super.onTransact(i, parcel, parcel2, i2);
                        }
                        parcel2.writeString(DESCRIPTOR);
                        return true;
                    }
                    parcel.enforceInterface(DESCRIPTOR);
                    Location lastKnownLocation = getLastKnownLocation(parcel.readString());
                    parcel2.writeNoException();
                    if (lastKnownLocation != null) {
                        parcel2.writeInt(1);
                        lastKnownLocation.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                }
                parcel.enforceInterface(DESCRIPTOR);
                iIsLocationEnabled = injectLocation(parcel.readInt() != 0 ? (Location) Location.CREATOR.createFromParcel(parcel) : null);
            }
            parcel2.writeNoException();
            parcel2.writeInt(iIsLocationEnabled);
            return true;
        }
    }

    Location getLastKnownLocation(String str) throws RemoteException;

    boolean injectLocation(Location location) throws RemoteException;

    boolean isLocationEnabled() throws RemoteException;
}
