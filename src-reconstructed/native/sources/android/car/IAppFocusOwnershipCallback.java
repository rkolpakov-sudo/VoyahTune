package android.car;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IAppFocusOwnershipCallback extends IInterface {

    public static class Default implements IAppFocusOwnershipCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.IAppFocusOwnershipCallback
        public void onAppFocusOwnershipGranted(int i) throws RemoteException {
        }

        @Override // android.car.IAppFocusOwnershipCallback
        public void onAppFocusOwnershipLost(int i) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements IAppFocusOwnershipCallback {
        private static final String DESCRIPTOR = "android.car.IAppFocusOwnershipCallback";
        static final int TRANSACTION_onAppFocusOwnershipGranted = 2;
        static final int TRANSACTION_onAppFocusOwnershipLost = 1;

        private static class Proxy implements IAppFocusOwnershipCallback {
            public static IAppFocusOwnershipCallback sDefaultImpl;
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

            @Override // android.car.IAppFocusOwnershipCallback
            public void onAppFocusOwnershipGranted(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(2, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onAppFocusOwnershipGranted(i);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.IAppFocusOwnershipCallback
            public void onAppFocusOwnershipLost(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(1, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onAppFocusOwnershipLost(i);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAppFocusOwnershipCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IAppFocusOwnershipCallback)) ? new Proxy(iBinder) : (IAppFocusOwnershipCallback) iInterfaceQueryLocalInterface;
        }

        public static IAppFocusOwnershipCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IAppFocusOwnershipCallback iAppFocusOwnershipCallback) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iAppFocusOwnershipCallback == null) {
                return false;
            }
            Proxy.sDefaultImpl = iAppFocusOwnershipCallback;
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
                onAppFocusOwnershipLost(parcel.readInt());
                return true;
            }
            if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                onAppFocusOwnershipGranted(parcel.readInt());
                return true;
            }
            if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            parcel2.writeString(DESCRIPTOR);
            return true;
        }
    }

    void onAppFocusOwnershipGranted(int i) throws RemoteException;

    void onAppFocusOwnershipLost(int i) throws RemoteException;
}
