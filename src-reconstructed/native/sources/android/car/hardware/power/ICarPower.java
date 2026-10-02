package android.car.hardware.power;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface ICarPower extends IInterface {

    public static class Default implements ICarPower {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.hardware.power.ICarPower
        public void finished(ICarPowerStateListener iCarPowerStateListener) throws RemoteException {
        }

        @Override // android.car.hardware.power.ICarPower
        public int getPowerState() throws RemoteException {
            return 0;
        }

        @Override // android.car.hardware.power.ICarPower
        public void registerListener(ICarPowerStateListener iCarPowerStateListener) throws RemoteException {
        }

        @Override // android.car.hardware.power.ICarPower
        public void registerListenerWithCompletion(ICarPowerStateListener iCarPowerStateListener) throws RemoteException {
        }

        @Override // android.car.hardware.power.ICarPower
        public void requestShutdownOnNextSuspend() throws RemoteException {
        }

        @Override // android.car.hardware.power.ICarPower
        public void scheduleNextWakeupTime(int i) throws RemoteException {
        }

        @Override // android.car.hardware.power.ICarPower
        public void unregisterListener(ICarPowerStateListener iCarPowerStateListener) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarPower {
        private static final String DESCRIPTOR = "android.car.hardware.power.ICarPower";
        static final int TRANSACTION_finished = 4;
        static final int TRANSACTION_getPowerState = 7;
        static final int TRANSACTION_registerListener = 1;
        static final int TRANSACTION_registerListenerWithCompletion = 6;
        static final int TRANSACTION_requestShutdownOnNextSuspend = 3;
        static final int TRANSACTION_scheduleNextWakeupTime = 5;
        static final int TRANSACTION_unregisterListener = 2;

        private static class Proxy implements ICarPower {
            public static ICarPower sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.car.hardware.power.ICarPower
            public void finished(ICarPowerStateListener iCarPowerStateListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarPowerStateListener != null ? iCarPowerStateListener.asBinder() : null);
                    if (this.mRemote.transact(4, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().finished(iCarPowerStateListener);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.car.hardware.power.ICarPower
            public int getPowerState() throws RemoteException {
                int powerState;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(7, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        powerState = parcelObtain2.readInt();
                    } else {
                        powerState = Stub.getDefaultImpl().getPowerState();
                    }
                    return powerState;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.hardware.power.ICarPower
            public void registerListener(ICarPowerStateListener iCarPowerStateListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarPowerStateListener != null ? iCarPowerStateListener.asBinder() : null);
                    if (this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().registerListener(iCarPowerStateListener);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.hardware.power.ICarPower
            public void registerListenerWithCompletion(ICarPowerStateListener iCarPowerStateListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarPowerStateListener != null ? iCarPowerStateListener.asBinder() : null);
                    if (this.mRemote.transact(6, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().registerListenerWithCompletion(iCarPowerStateListener);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.hardware.power.ICarPower
            public void requestShutdownOnNextSuspend() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(3, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().requestShutdownOnNextSuspend();
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.hardware.power.ICarPower
            public void scheduleNextWakeupTime(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(5, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().scheduleNextWakeupTime(i);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.hardware.power.ICarPower
            public void unregisterListener(ICarPowerStateListener iCarPowerStateListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarPowerStateListener != null ? iCarPowerStateListener.asBinder() : null);
                    if (this.mRemote.transact(2, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregisterListener(iCarPowerStateListener);
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

        public static ICarPower asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarPower)) ? new Proxy(iBinder) : (ICarPower) iInterfaceQueryLocalInterface;
        }

        public static ICarPower getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarPower iCarPower) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarPower == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarPower;
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
                    registerListener(ICarPowerStateListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterListener(ICarPowerStateListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    requestShutdownOnNextSuspend();
                    parcel2.writeNoException();
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    finished(ICarPowerStateListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    scheduleNextWakeupTime(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    registerListenerWithCompletion(ICarPowerStateListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    int powerState = getPowerState();
                    parcel2.writeNoException();
                    parcel2.writeInt(powerState);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void finished(ICarPowerStateListener iCarPowerStateListener) throws RemoteException;

    int getPowerState() throws RemoteException;

    void registerListener(ICarPowerStateListener iCarPowerStateListener) throws RemoteException;

    void registerListenerWithCompletion(ICarPowerStateListener iCarPowerStateListener) throws RemoteException;

    void requestShutdownOnNextSuspend() throws RemoteException;

    void scheduleNextWakeupTime(int i) throws RemoteException;

    void unregisterListener(ICarPowerStateListener iCarPowerStateListener) throws RemoteException;
}
