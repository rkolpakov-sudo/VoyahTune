package android.car;

import android.bluetooth.BluetoothDevice;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface ICarBluetoothUserService extends IInterface {

    public static class Default implements ICarBluetoothUserService {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.ICarBluetoothUserService
        public boolean bluetoothConnectToProfile(int i, BluetoothDevice bluetoothDevice) throws RemoteException {
            return false;
        }

        @Override // android.car.ICarBluetoothUserService
        public boolean bluetoothDisconnectFromProfile(int i, BluetoothDevice bluetoothDevice) throws RemoteException {
            return false;
        }

        @Override // android.car.ICarBluetoothUserService
        public void closeBluetoothConnectionProxies() throws RemoteException {
        }

        @Override // android.car.ICarBluetoothUserService
        public int getProfilePriority(int i, BluetoothDevice bluetoothDevice) throws RemoteException {
            return 0;
        }

        @Override // android.car.ICarBluetoothUserService
        public boolean isBluetoothConnectionProxyAvailable(int i) throws RemoteException {
            return false;
        }

        @Override // android.car.ICarBluetoothUserService
        public void setProfilePriority(int i, BluetoothDevice bluetoothDevice, int i2) throws RemoteException {
        }

        @Override // android.car.ICarBluetoothUserService
        public void setupBluetoothConnectionProxies() throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarBluetoothUserService {
        private static final String DESCRIPTOR = "android.car.ICarBluetoothUserService";
        static final int TRANSACTION_bluetoothConnectToProfile = 4;
        static final int TRANSACTION_bluetoothDisconnectFromProfile = 5;
        static final int TRANSACTION_closeBluetoothConnectionProxies = 2;
        static final int TRANSACTION_getProfilePriority = 6;
        static final int TRANSACTION_isBluetoothConnectionProxyAvailable = 3;
        static final int TRANSACTION_setProfilePriority = 7;
        static final int TRANSACTION_setupBluetoothConnectionProxies = 1;

        private static class Proxy implements ICarBluetoothUserService {
            public static ICarBluetoothUserService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.car.ICarBluetoothUserService
            public boolean bluetoothConnectToProfile(int i, BluetoothDevice bluetoothDevice) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (bluetoothDevice != null) {
                        parcelObtain.writeInt(1);
                        bluetoothDevice.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (!this.mRemote.transact(4, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().bluetoothConnectToProfile(i, bluetoothDevice);
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarBluetoothUserService
            public boolean bluetoothDisconnectFromProfile(int i, BluetoothDevice bluetoothDevice) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (bluetoothDevice != null) {
                        parcelObtain.writeInt(1);
                        bluetoothDevice.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (!this.mRemote.transact(5, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().bluetoothDisconnectFromProfile(i, bluetoothDevice);
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarBluetoothUserService
            public void closeBluetoothConnectionProxies() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(2, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().closeBluetoothConnectionProxies();
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.car.ICarBluetoothUserService
            public int getProfilePriority(int i, BluetoothDevice bluetoothDevice) throws RemoteException {
                int profilePriority;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (bluetoothDevice != null) {
                        parcelObtain.writeInt(1);
                        bluetoothDevice.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(6, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        profilePriority = parcelObtain2.readInt();
                    } else {
                        profilePriority = Stub.getDefaultImpl().getProfilePriority(i, bluetoothDevice);
                    }
                    return profilePriority;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarBluetoothUserService
            public boolean isBluetoothConnectionProxyAvailable(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (!this.mRemote.transact(3, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isBluetoothConnectionProxyAvailable(i);
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarBluetoothUserService
            public void setProfilePriority(int i, BluetoothDevice bluetoothDevice, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (bluetoothDevice != null) {
                        parcelObtain.writeInt(1);
                        bluetoothDevice.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i2);
                    if (this.mRemote.transact(7, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setProfilePriority(i, bluetoothDevice, i2);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarBluetoothUserService
            public void setupBluetoothConnectionProxies() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setupBluetoothConnectionProxies();
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICarBluetoothUserService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarBluetoothUserService)) ? new Proxy(iBinder) : (ICarBluetoothUserService) iInterfaceQueryLocalInterface;
        }

        public static ICarBluetoothUserService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarBluetoothUserService iCarBluetoothUserService) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarBluetoothUserService == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarBluetoothUserService;
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    setupBluetoothConnectionProxies();
                    parcel2.writeNoException();
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    closeBluetoothConnectionProxies();
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsBluetoothConnectionProxyAvailable = isBluetoothConnectionProxyAvailable(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsBluetoothConnectionProxyAvailable ? 1 : 0);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zBluetoothConnectToProfile = bluetoothConnectToProfile(parcel.readInt(), parcel.readInt() != 0 ? (BluetoothDevice) BluetoothDevice.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeInt(zBluetoothConnectToProfile ? 1 : 0);
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zBluetoothDisconnectFromProfile = bluetoothDisconnectFromProfile(parcel.readInt(), parcel.readInt() != 0 ? (BluetoothDevice) BluetoothDevice.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeInt(zBluetoothDisconnectFromProfile ? 1 : 0);
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    int profilePriority = getProfilePriority(parcel.readInt(), parcel.readInt() != 0 ? (BluetoothDevice) BluetoothDevice.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeInt(profilePriority);
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    setProfilePriority(parcel.readInt(), parcel.readInt() != 0 ? (BluetoothDevice) BluetoothDevice.CREATOR.createFromParcel(parcel) : null, parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    boolean bluetoothConnectToProfile(int i, BluetoothDevice bluetoothDevice) throws RemoteException;

    boolean bluetoothDisconnectFromProfile(int i, BluetoothDevice bluetoothDevice) throws RemoteException;

    void closeBluetoothConnectionProxies() throws RemoteException;

    int getProfilePriority(int i, BluetoothDevice bluetoothDevice) throws RemoteException;

    boolean isBluetoothConnectionProxyAvailable(int i) throws RemoteException;

    void setProfilePriority(int i, BluetoothDevice bluetoothDevice, int i2) throws RemoteException;

    void setupBluetoothConnectionProxies() throws RemoteException;
}
