package android.car;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IPerUserCarService extends IInterface {

    public static class Default implements IPerUserCarService {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.IPerUserCarService
        public ICarBluetoothUserService getBluetoothUserService() throws RemoteException {
            return null;
        }

        @Override // android.car.IPerUserCarService
        public ILocationManagerProxy getLocationManagerProxy() throws RemoteException {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IPerUserCarService {
        private static final String DESCRIPTOR = "android.car.IPerUserCarService";
        static final int TRANSACTION_getBluetoothUserService = 1;
        static final int TRANSACTION_getLocationManagerProxy = 2;

        private static class Proxy implements IPerUserCarService {
            public static IPerUserCarService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.car.IPerUserCarService
            public ICarBluetoothUserService getBluetoothUserService() throws RemoteException {
                ICarBluetoothUserService iCarBluetoothUserServiceAsInterface;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        iCarBluetoothUserServiceAsInterface = ICarBluetoothUserService.Stub.asInterface(parcelObtain2.readStrongBinder());
                    } else {
                        iCarBluetoothUserServiceAsInterface = Stub.getDefaultImpl().getBluetoothUserService();
                    }
                    return iCarBluetoothUserServiceAsInterface;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.car.IPerUserCarService
            public ILocationManagerProxy getLocationManagerProxy() throws RemoteException {
                ILocationManagerProxy iLocationManagerProxyAsInterface;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(2, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        iLocationManagerProxyAsInterface = ILocationManagerProxy.Stub.asInterface(parcelObtain2.readStrongBinder());
                    } else {
                        iLocationManagerProxyAsInterface = Stub.getDefaultImpl().getLocationManagerProxy();
                    }
                    return iLocationManagerProxyAsInterface;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPerUserCarService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IPerUserCarService)) ? new Proxy(iBinder) : (IPerUserCarService) iInterfaceQueryLocalInterface;
        }

        public static IPerUserCarService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IPerUserCarService iPerUserCarService) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iPerUserCarService == null) {
                return false;
            }
            Proxy.sDefaultImpl = iPerUserCarService;
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
                ICarBluetoothUserService bluetoothUserService = getBluetoothUserService();
                parcel2.writeNoException();
                parcel2.writeStrongBinder(bluetoothUserService != null ? bluetoothUserService.asBinder() : null);
                return true;
            }
            if (i != 2) {
                if (i != 1598968902) {
                    return super.onTransact(i, parcel, parcel2, i2);
                }
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            parcel.enforceInterface(DESCRIPTOR);
            ILocationManagerProxy locationManagerProxy = getLocationManagerProxy();
            parcel2.writeNoException();
            parcel2.writeStrongBinder(locationManagerProxy != null ? locationManagerProxy.asBinder() : null);
            return true;
        }
    }

    ICarBluetoothUserService getBluetoothUserService() throws RemoteException;

    ILocationManagerProxy getLocationManagerProxy() throws RemoteException;
}
