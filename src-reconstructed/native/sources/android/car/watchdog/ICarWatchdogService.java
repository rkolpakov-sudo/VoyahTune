package android.car.watchdog;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface ICarWatchdogService extends IInterface {

    public static class Default implements ICarWatchdogService {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.watchdog.ICarWatchdogService
        public void registerClient(ICarWatchdogServiceCallback iCarWatchdogServiceCallback, int i) throws RemoteException {
        }

        @Override // android.car.watchdog.ICarWatchdogService
        public void tellClientAlive(ICarWatchdogServiceCallback iCarWatchdogServiceCallback, int i) throws RemoteException {
        }

        @Override // android.car.watchdog.ICarWatchdogService
        public void unregisterClient(ICarWatchdogServiceCallback iCarWatchdogServiceCallback) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarWatchdogService {
        private static final String DESCRIPTOR = "android.car.watchdog.ICarWatchdogService";
        static final int TRANSACTION_registerClient = 1;
        static final int TRANSACTION_tellClientAlive = 3;
        static final int TRANSACTION_unregisterClient = 2;

        private static class Proxy implements ICarWatchdogService {
            public static ICarWatchdogService sDefaultImpl;
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

            @Override // android.car.watchdog.ICarWatchdogService
            public void registerClient(ICarWatchdogServiceCallback iCarWatchdogServiceCallback, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarWatchdogServiceCallback != null ? iCarWatchdogServiceCallback.asBinder() : null);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().registerClient(iCarWatchdogServiceCallback, i);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.watchdog.ICarWatchdogService
            public void tellClientAlive(ICarWatchdogServiceCallback iCarWatchdogServiceCallback, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarWatchdogServiceCallback != null ? iCarWatchdogServiceCallback.asBinder() : null);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(3, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().tellClientAlive(iCarWatchdogServiceCallback, i);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.watchdog.ICarWatchdogService
            public void unregisterClient(ICarWatchdogServiceCallback iCarWatchdogServiceCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarWatchdogServiceCallback != null ? iCarWatchdogServiceCallback.asBinder() : null);
                    if (this.mRemote.transact(2, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregisterClient(iCarWatchdogServiceCallback);
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

        public static ICarWatchdogService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarWatchdogService)) ? new Proxy(iBinder) : (ICarWatchdogService) iInterfaceQueryLocalInterface;
        }

        public static ICarWatchdogService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarWatchdogService iCarWatchdogService) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarWatchdogService == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarWatchdogService;
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
                registerClient(ICarWatchdogServiceCallback.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
            } else if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                unregisterClient(ICarWatchdogServiceCallback.Stub.asInterface(parcel.readStrongBinder()));
            } else {
                if (i != 3) {
                    if (i != 1598968902) {
                        return super.onTransact(i, parcel, parcel2, i2);
                    }
                    parcel2.writeString(DESCRIPTOR);
                    return true;
                }
                parcel.enforceInterface(DESCRIPTOR);
                tellClientAlive(ICarWatchdogServiceCallback.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
            }
            parcel2.writeNoException();
            return true;
        }
    }

    void registerClient(ICarWatchdogServiceCallback iCarWatchdogServiceCallback, int i) throws RemoteException;

    void tellClientAlive(ICarWatchdogServiceCallback iCarWatchdogServiceCallback, int i) throws RemoteException;

    void unregisterClient(ICarWatchdogServiceCallback iCarWatchdogServiceCallback) throws RemoteException;
}
