package android.car.occupantawareness;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IOccupantAwarenessManager extends IInterface {

    public static class Default implements IOccupantAwarenessManager {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.occupantawareness.IOccupantAwarenessManager
        public int getCapabilityForRole(int i) throws RemoteException {
            return 0;
        }

        @Override // android.car.occupantawareness.IOccupantAwarenessManager
        public void registerEventListener(IOccupantAwarenessEventCallback iOccupantAwarenessEventCallback) throws RemoteException {
        }

        @Override // android.car.occupantawareness.IOccupantAwarenessManager
        public void unregisterEventListener(IOccupantAwarenessEventCallback iOccupantAwarenessEventCallback) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements IOccupantAwarenessManager {
        private static final String DESCRIPTOR = "android.car.occupantawareness.IOccupantAwarenessManager";
        static final int TRANSACTION_getCapabilityForRole = 1;
        static final int TRANSACTION_registerEventListener = 2;
        static final int TRANSACTION_unregisterEventListener = 3;

        private static class Proxy implements IOccupantAwarenessManager {
            public static IOccupantAwarenessManager sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.car.occupantawareness.IOccupantAwarenessManager
            public int getCapabilityForRole(int i) throws RemoteException {
                int capabilityForRole;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        capabilityForRole = parcelObtain2.readInt();
                    } else {
                        capabilityForRole = Stub.getDefaultImpl().getCapabilityForRole(i);
                    }
                    return capabilityForRole;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.car.occupantawareness.IOccupantAwarenessManager
            public void registerEventListener(IOccupantAwarenessEventCallback iOccupantAwarenessEventCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iOccupantAwarenessEventCallback != null ? iOccupantAwarenessEventCallback.asBinder() : null);
                    if (this.mRemote.transact(2, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().registerEventListener(iOccupantAwarenessEventCallback);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.occupantawareness.IOccupantAwarenessManager
            public void unregisterEventListener(IOccupantAwarenessEventCallback iOccupantAwarenessEventCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iOccupantAwarenessEventCallback != null ? iOccupantAwarenessEventCallback.asBinder() : null);
                    if (this.mRemote.transact(3, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregisterEventListener(iOccupantAwarenessEventCallback);
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

        public static IOccupantAwarenessManager asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IOccupantAwarenessManager)) ? new Proxy(iBinder) : (IOccupantAwarenessManager) iInterfaceQueryLocalInterface;
        }

        public static IOccupantAwarenessManager getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IOccupantAwarenessManager iOccupantAwarenessManager) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iOccupantAwarenessManager == null) {
                return false;
            }
            Proxy.sDefaultImpl = iOccupantAwarenessManager;
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
                int capabilityForRole = getCapabilityForRole(parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(capabilityForRole);
                return true;
            }
            if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                registerEventListener(IOccupantAwarenessEventCallback.Stub.asInterface(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            }
            if (i != 3) {
                if (i != 1598968902) {
                    return super.onTransact(i, parcel, parcel2, i2);
                }
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            parcel.enforceInterface(DESCRIPTOR);
            unregisterEventListener(IOccupantAwarenessEventCallback.Stub.asInterface(parcel.readStrongBinder()));
            parcel2.writeNoException();
            return true;
        }
    }

    int getCapabilityForRole(int i) throws RemoteException;

    void registerEventListener(IOccupantAwarenessEventCallback iOccupantAwarenessEventCallback) throws RemoteException;

    void unregisterEventListener(IOccupantAwarenessEventCallback iOccupantAwarenessEventCallback) throws RemoteException;
}
