package android.car.cluster;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IInstrumentClusterManagerService extends IInterface {

    public static class Default implements IInstrumentClusterManagerService {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.cluster.IInstrumentClusterManagerService
        public void registerCallback(IInstrumentClusterManagerCallback iInstrumentClusterManagerCallback) throws RemoteException {
        }

        @Override // android.car.cluster.IInstrumentClusterManagerService
        public void startClusterActivity(Intent intent) throws RemoteException {
        }

        @Override // android.car.cluster.IInstrumentClusterManagerService
        public void unregisterCallback(IInstrumentClusterManagerCallback iInstrumentClusterManagerCallback) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements IInstrumentClusterManagerService {
        private static final String DESCRIPTOR = "android.car.cluster.IInstrumentClusterManagerService";
        static final int TRANSACTION_registerCallback = 2;
        static final int TRANSACTION_startClusterActivity = 1;
        static final int TRANSACTION_unregisterCallback = 3;

        private static class Proxy implements IInstrumentClusterManagerService {
            public static IInstrumentClusterManagerService sDefaultImpl;
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

            @Override // android.car.cluster.IInstrumentClusterManagerService
            public void registerCallback(IInstrumentClusterManagerCallback iInstrumentClusterManagerCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iInstrumentClusterManagerCallback != null ? iInstrumentClusterManagerCallback.asBinder() : null);
                    if (this.mRemote.transact(2, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().registerCallback(iInstrumentClusterManagerCallback);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.cluster.IInstrumentClusterManagerService
            public void startClusterActivity(Intent intent) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        parcelObtain.writeInt(1);
                        intent.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(1, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().startClusterActivity(intent);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.cluster.IInstrumentClusterManagerService
            public void unregisterCallback(IInstrumentClusterManagerCallback iInstrumentClusterManagerCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iInstrumentClusterManagerCallback != null ? iInstrumentClusterManagerCallback.asBinder() : null);
                    if (this.mRemote.transact(3, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().unregisterCallback(iInstrumentClusterManagerCallback);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IInstrumentClusterManagerService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IInstrumentClusterManagerService)) ? new Proxy(iBinder) : (IInstrumentClusterManagerService) iInterfaceQueryLocalInterface;
        }

        public static IInstrumentClusterManagerService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IInstrumentClusterManagerService iInstrumentClusterManagerService) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iInstrumentClusterManagerService == null) {
                return false;
            }
            Proxy.sDefaultImpl = iInstrumentClusterManagerService;
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
                startClusterActivity(parcel.readInt() != 0 ? (Intent) Intent.CREATOR.createFromParcel(parcel) : null);
                return true;
            }
            if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                registerCallback(IInstrumentClusterManagerCallback.Stub.asInterface(parcel.readStrongBinder()));
                return true;
            }
            if (i == 3) {
                parcel.enforceInterface(DESCRIPTOR);
                unregisterCallback(IInstrumentClusterManagerCallback.Stub.asInterface(parcel.readStrongBinder()));
                return true;
            }
            if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            parcel2.writeString(DESCRIPTOR);
            return true;
        }
    }

    void registerCallback(IInstrumentClusterManagerCallback iInstrumentClusterManagerCallback) throws RemoteException;

    void startClusterActivity(Intent intent) throws RemoteException;

    void unregisterCallback(IInstrumentClusterManagerCallback iInstrumentClusterManagerCallback) throws RemoteException;
}
