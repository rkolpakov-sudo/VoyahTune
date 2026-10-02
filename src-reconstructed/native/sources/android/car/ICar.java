package android.car;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface ICar extends IInterface {

    public static class Default implements ICar {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.ICar
        public int disableFeature(String str) throws RemoteException {
            return 0;
        }

        @Override // android.car.ICar
        public int enableFeature(String str) throws RemoteException {
            return 0;
        }

        @Override // android.car.ICar
        public List<String> getAllEnabledFeatures() throws RemoteException {
            return null;
        }

        @Override // android.car.ICar
        public List<String> getAllPendingDisabledFeatures() throws RemoteException {
            return null;
        }

        @Override // android.car.ICar
        public List<String> getAllPendingEnabledFeatures() throws RemoteException {
            return null;
        }

        @Override // android.car.ICar
        public int getCarConnectionType() throws RemoteException {
            return 0;
        }

        @Override // android.car.ICar
        public String getCarManagerClassForFeature(String str) throws RemoteException {
            return null;
        }

        @Override // android.car.ICar
        public IBinder getCarService(String str) throws RemoteException {
            return null;
        }

        @Override // android.car.ICar
        public void getInitialUserInfo(int i, int i2, IBinder iBinder) throws RemoteException {
        }

        @Override // android.car.ICar
        public boolean isFeatureEnabled(String str) throws RemoteException {
            return false;
        }

        @Override // android.car.ICar
        public void onFirstUserUnlocked(int i, long j, long j2, int i2) throws RemoteException {
        }

        @Override // android.car.ICar
        public void onUserLifecycleEvent(int i, long j, int i2, int i3) throws RemoteException {
        }

        @Override // android.car.ICar
        public void setCarServiceHelper(IBinder iBinder) throws RemoteException {
        }

        @Override // android.car.ICar
        public void setInitialUser(int i) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICar {
        private static final String DESCRIPTOR = "android.car.ICar";
        static final int TRANSACTION_disableFeature = 16;
        static final int TRANSACTION_enableFeature = 15;
        static final int TRANSACTION_getAllEnabledFeatures = 17;
        static final int TRANSACTION_getAllPendingDisabledFeatures = 18;
        static final int TRANSACTION_getAllPendingEnabledFeatures = 19;
        static final int TRANSACTION_getCarConnectionType = 13;
        static final int TRANSACTION_getCarManagerClassForFeature = 20;
        static final int TRANSACTION_getCarService = 12;
        static final int TRANSACTION_getInitialUserInfo = 4;
        static final int TRANSACTION_isFeatureEnabled = 14;
        static final int TRANSACTION_onFirstUserUnlocked = 3;
        static final int TRANSACTION_onUserLifecycleEvent = 2;
        static final int TRANSACTION_setCarServiceHelper = 1;
        static final int TRANSACTION_setInitialUser = 5;

        private static class Proxy implements ICar {
            public static ICar sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.car.ICar
            public int disableFeature(String str) throws RemoteException {
                int iDisableFeature;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("android.car.ICar");
                    parcelObtain.writeString(str);
                    if (this.mRemote.transact(16, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        iDisableFeature = parcelObtain2.readInt();
                    } else {
                        iDisableFeature = Stub.getDefaultImpl().disableFeature(str);
                    }
                    return iDisableFeature;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICar
            public int enableFeature(String str) throws RemoteException {
                int iEnableFeature;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("android.car.ICar");
                    parcelObtain.writeString(str);
                    if (this.mRemote.transact(15, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        iEnableFeature = parcelObtain2.readInt();
                    } else {
                        iEnableFeature = Stub.getDefaultImpl().enableFeature(str);
                    }
                    return iEnableFeature;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICar
            public List<String> getAllEnabledFeatures() throws RemoteException {
                List<String> listCreateStringArrayList;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("android.car.ICar");
                    if (this.mRemote.transact(17, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        listCreateStringArrayList = parcelObtain2.createStringArrayList();
                    } else {
                        listCreateStringArrayList = Stub.getDefaultImpl().getAllEnabledFeatures();
                    }
                    return listCreateStringArrayList;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICar
            public List<String> getAllPendingDisabledFeatures() throws RemoteException {
                List<String> listCreateStringArrayList;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("android.car.ICar");
                    if (this.mRemote.transact(18, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        listCreateStringArrayList = parcelObtain2.createStringArrayList();
                    } else {
                        listCreateStringArrayList = Stub.getDefaultImpl().getAllPendingDisabledFeatures();
                    }
                    return listCreateStringArrayList;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICar
            public List<String> getAllPendingEnabledFeatures() throws RemoteException {
                List<String> listCreateStringArrayList;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("android.car.ICar");
                    if (this.mRemote.transact(19, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        listCreateStringArrayList = parcelObtain2.createStringArrayList();
                    } else {
                        listCreateStringArrayList = Stub.getDefaultImpl().getAllPendingEnabledFeatures();
                    }
                    return listCreateStringArrayList;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICar
            public int getCarConnectionType() throws RemoteException {
                int carConnectionType;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("android.car.ICar");
                    if (this.mRemote.transact(13, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        carConnectionType = parcelObtain2.readInt();
                    } else {
                        carConnectionType = Stub.getDefaultImpl().getCarConnectionType();
                    }
                    return carConnectionType;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICar
            public String getCarManagerClassForFeature(String str) throws RemoteException {
                String string;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("android.car.ICar");
                    parcelObtain.writeString(str);
                    if (this.mRemote.transact(20, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        string = parcelObtain2.readString();
                    } else {
                        string = Stub.getDefaultImpl().getCarManagerClassForFeature(str);
                    }
                    return string;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICar
            public IBinder getCarService(String str) throws RemoteException {
                IBinder strongBinder;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("android.car.ICar");
                    parcelObtain.writeString(str);
                    if (this.mRemote.transact(12, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        strongBinder = parcelObtain2.readStrongBinder();
                    } else {
                        strongBinder = Stub.getDefaultImpl().getCarService(str);
                    }
                    return strongBinder;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICar
            public void getInitialUserInfo(int i, int i2, IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("android.car.ICar");
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeStrongBinder(iBinder);
                    if (this.mRemote.transact(4, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().getInitialUserInfo(i, i2, iBinder);
                } finally {
                    parcelObtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return "android.car.ICar";
            }

            @Override // android.car.ICar
            public boolean isFeatureEnabled(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("android.car.ICar");
                    parcelObtain.writeString(str);
                    if (!this.mRemote.transact(14, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isFeatureEnabled(str);
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICar
            public void onFirstUserUnlocked(int i, long j, long j2, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("android.car.ICar");
                    parcelObtain.writeInt(i);
                    parcelObtain.writeLong(j);
                    parcelObtain.writeLong(j2);
                    parcelObtain.writeInt(i2);
                    if (this.mRemote.transact(3, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onFirstUserUnlocked(i, j, j2, i2);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICar
            public void onUserLifecycleEvent(int i, long j, int i2, int i3) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("android.car.ICar");
                    parcelObtain.writeInt(i);
                    parcelObtain.writeLong(j);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    if (this.mRemote.transact(2, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onUserLifecycleEvent(i, j, i2, i3);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICar
            public void setCarServiceHelper(IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("android.car.ICar");
                    parcelObtain.writeStrongBinder(iBinder);
                    if (this.mRemote.transact(1, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().setCarServiceHelper(iBinder);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICar
            public void setInitialUser(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("android.car.ICar");
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(5, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().setInitialUser(i);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "android.car.ICar");
        }

        public static ICar asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface("android.car.ICar");
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICar)) ? new Proxy(iBinder) : (ICar) iInterfaceQueryLocalInterface;
        }

        public static ICar getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICar iCar) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCar == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCar;
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1) {
                parcel.enforceInterface("android.car.ICar");
                setCarServiceHelper(parcel.readStrongBinder());
                return true;
            }
            if (i == 2) {
                parcel.enforceInterface("android.car.ICar");
                onUserLifecycleEvent(parcel.readInt(), parcel.readLong(), parcel.readInt(), parcel.readInt());
                return true;
            }
            if (i == 3) {
                parcel.enforceInterface("android.car.ICar");
                onFirstUserUnlocked(parcel.readInt(), parcel.readLong(), parcel.readLong(), parcel.readInt());
                return true;
            }
            if (i == 4) {
                parcel.enforceInterface("android.car.ICar");
                getInitialUserInfo(parcel.readInt(), parcel.readInt(), parcel.readStrongBinder());
                return true;
            }
            if (i == 5) {
                parcel.enforceInterface("android.car.ICar");
                setInitialUser(parcel.readInt());
                return true;
            }
            if (i == 1598968902) {
                parcel2.writeString("android.car.ICar");
                return true;
            }
            switch (i) {
                case 12:
                    parcel.enforceInterface("android.car.ICar");
                    IBinder carService = getCarService(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(carService);
                    return true;
                case 13:
                    parcel.enforceInterface("android.car.ICar");
                    int carConnectionType = getCarConnectionType();
                    parcel2.writeNoException();
                    parcel2.writeInt(carConnectionType);
                    return true;
                case 14:
                    parcel.enforceInterface("android.car.ICar");
                    boolean zIsFeatureEnabled = isFeatureEnabled(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsFeatureEnabled ? 1 : 0);
                    return true;
                case 15:
                    parcel.enforceInterface("android.car.ICar");
                    int iEnableFeature = enableFeature(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(iEnableFeature);
                    return true;
                case 16:
                    parcel.enforceInterface("android.car.ICar");
                    int iDisableFeature = disableFeature(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(iDisableFeature);
                    return true;
                case 17:
                    parcel.enforceInterface("android.car.ICar");
                    List<String> allEnabledFeatures = getAllEnabledFeatures();
                    parcel2.writeNoException();
                    parcel2.writeStringList(allEnabledFeatures);
                    return true;
                case 18:
                    parcel.enforceInterface("android.car.ICar");
                    List<String> allPendingDisabledFeatures = getAllPendingDisabledFeatures();
                    parcel2.writeNoException();
                    parcel2.writeStringList(allPendingDisabledFeatures);
                    return true;
                case 19:
                    parcel.enforceInterface("android.car.ICar");
                    List<String> allPendingEnabledFeatures = getAllPendingEnabledFeatures();
                    parcel2.writeNoException();
                    parcel2.writeStringList(allPendingEnabledFeatures);
                    return true;
                case 20:
                    parcel.enforceInterface("android.car.ICar");
                    String carManagerClassForFeature = getCarManagerClassForFeature(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeString(carManagerClassForFeature);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    int disableFeature(String str) throws RemoteException;

    int enableFeature(String str) throws RemoteException;

    List<String> getAllEnabledFeatures() throws RemoteException;

    List<String> getAllPendingDisabledFeatures() throws RemoteException;

    List<String> getAllPendingEnabledFeatures() throws RemoteException;

    int getCarConnectionType() throws RemoteException;

    String getCarManagerClassForFeature(String str) throws RemoteException;

    IBinder getCarService(String str) throws RemoteException;

    void getInitialUserInfo(int i, int i2, IBinder iBinder) throws RemoteException;

    boolean isFeatureEnabled(String str) throws RemoteException;

    void onFirstUserUnlocked(int i, long j, long j2, int i2) throws RemoteException;

    void onUserLifecycleEvent(int i, long j, int i2, int i3) throws RemoteException;

    void setCarServiceHelper(IBinder iBinder) throws RemoteException;

    void setInitialUser(int i) throws RemoteException;
}
