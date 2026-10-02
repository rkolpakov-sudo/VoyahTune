package android.car.diagnostic;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface ICarDiagnostic extends IInterface {

    public static class Default implements ICarDiagnostic {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.diagnostic.ICarDiagnostic
        public boolean clearFreezeFrames(long[] jArr) throws RemoteException {
            return false;
        }

        @Override // android.car.diagnostic.ICarDiagnostic
        public CarDiagnosticEvent getFreezeFrame(long j) throws RemoteException {
            return null;
        }

        @Override // android.car.diagnostic.ICarDiagnostic
        public long[] getFreezeFrameTimestamps() throws RemoteException {
            return null;
        }

        @Override // android.car.diagnostic.ICarDiagnostic
        public CarDiagnosticEvent getLatestLiveFrame() throws RemoteException {
            return null;
        }

        @Override // android.car.diagnostic.ICarDiagnostic
        public boolean isClearFreezeFramesSupported() throws RemoteException {
            return false;
        }

        @Override // android.car.diagnostic.ICarDiagnostic
        public boolean isFreezeFrameNotificationSupported() throws RemoteException {
            return false;
        }

        @Override // android.car.diagnostic.ICarDiagnostic
        public boolean isGetFreezeFrameSupported() throws RemoteException {
            return false;
        }

        @Override // android.car.diagnostic.ICarDiagnostic
        public boolean isLiveFrameSupported() throws RemoteException {
            return false;
        }

        @Override // android.car.diagnostic.ICarDiagnostic
        public boolean isSelectiveClearFreezeFramesSupported() throws RemoteException {
            return false;
        }

        @Override // android.car.diagnostic.ICarDiagnostic
        public boolean registerOrUpdateDiagnosticListener(int i, int i2, ICarDiagnosticEventListener iCarDiagnosticEventListener) throws RemoteException {
            return false;
        }

        @Override // android.car.diagnostic.ICarDiagnostic
        public void unregisterDiagnosticListener(int i, ICarDiagnosticEventListener iCarDiagnosticEventListener) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarDiagnostic {
        private static final String DESCRIPTOR = "android.car.diagnostic.ICarDiagnostic";
        static final int TRANSACTION_clearFreezeFrames = 6;
        static final int TRANSACTION_getFreezeFrame = 5;
        static final int TRANSACTION_getFreezeFrameTimestamps = 4;
        static final int TRANSACTION_getLatestLiveFrame = 3;
        static final int TRANSACTION_isClearFreezeFramesSupported = 11;
        static final int TRANSACTION_isFreezeFrameNotificationSupported = 9;
        static final int TRANSACTION_isGetFreezeFrameSupported = 10;
        static final int TRANSACTION_isLiveFrameSupported = 8;
        static final int TRANSACTION_isSelectiveClearFreezeFramesSupported = 12;
        static final int TRANSACTION_registerOrUpdateDiagnosticListener = 2;
        static final int TRANSACTION_unregisterDiagnosticListener = 7;

        private static class Proxy implements ICarDiagnostic {
            public static ICarDiagnostic sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.car.diagnostic.ICarDiagnostic
            public boolean clearFreezeFrames(long[] jArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeLongArray(jArr);
                    if (!this.mRemote.transact(6, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().clearFreezeFrames(jArr);
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.diagnostic.ICarDiagnostic
            public CarDiagnosticEvent getFreezeFrame(long j) throws RemoteException {
                CarDiagnosticEvent carDiagnosticEventCreateFromParcel;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeLong(j);
                    if (this.mRemote.transact(5, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        carDiagnosticEventCreateFromParcel = parcelObtain2.readInt() != 0 ? CarDiagnosticEvent.CREATOR.createFromParcel(parcelObtain2) : null;
                    } else {
                        carDiagnosticEventCreateFromParcel = Stub.getDefaultImpl().getFreezeFrame(j);
                    }
                    return carDiagnosticEventCreateFromParcel;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.diagnostic.ICarDiagnostic
            public long[] getFreezeFrameTimestamps() throws RemoteException {
                long[] jArrCreateLongArray;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(4, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        jArrCreateLongArray = parcelObtain2.createLongArray();
                    } else {
                        jArrCreateLongArray = Stub.getDefaultImpl().getFreezeFrameTimestamps();
                    }
                    return jArrCreateLongArray;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.car.diagnostic.ICarDiagnostic
            public CarDiagnosticEvent getLatestLiveFrame() throws RemoteException {
                CarDiagnosticEvent carDiagnosticEventCreateFromParcel;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(3, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        carDiagnosticEventCreateFromParcel = parcelObtain2.readInt() != 0 ? CarDiagnosticEvent.CREATOR.createFromParcel(parcelObtain2) : null;
                    } else {
                        carDiagnosticEventCreateFromParcel = Stub.getDefaultImpl().getLatestLiveFrame();
                    }
                    return carDiagnosticEventCreateFromParcel;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.diagnostic.ICarDiagnostic
            public boolean isClearFreezeFramesSupported() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(11, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isClearFreezeFramesSupported();
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.diagnostic.ICarDiagnostic
            public boolean isFreezeFrameNotificationSupported() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(9, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isFreezeFrameNotificationSupported();
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.diagnostic.ICarDiagnostic
            public boolean isGetFreezeFrameSupported() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(10, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isGetFreezeFrameSupported();
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.diagnostic.ICarDiagnostic
            public boolean isLiveFrameSupported() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(8, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isLiveFrameSupported();
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.diagnostic.ICarDiagnostic
            public boolean isSelectiveClearFreezeFramesSupported() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(12, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isSelectiveClearFreezeFramesSupported();
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.diagnostic.ICarDiagnostic
            public boolean registerOrUpdateDiagnosticListener(int i, int i2, ICarDiagnosticEventListener iCarDiagnosticEventListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeStrongBinder(iCarDiagnosticEventListener != null ? iCarDiagnosticEventListener.asBinder() : null);
                    if (!this.mRemote.transact(2, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().registerOrUpdateDiagnosticListener(i, i2, iCarDiagnosticEventListener);
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.diagnostic.ICarDiagnostic
            public void unregisterDiagnosticListener(int i, ICarDiagnosticEventListener iCarDiagnosticEventListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeStrongBinder(iCarDiagnosticEventListener != null ? iCarDiagnosticEventListener.asBinder() : null);
                    if (this.mRemote.transact(7, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregisterDiagnosticListener(i, iCarDiagnosticEventListener);
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

        public static ICarDiagnostic asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarDiagnostic)) ? new Proxy(iBinder) : (ICarDiagnostic) iInterfaceQueryLocalInterface;
        }

        public static ICarDiagnostic getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarDiagnostic iCarDiagnostic) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarDiagnostic == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarDiagnostic;
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
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zRegisterOrUpdateDiagnosticListener = registerOrUpdateDiagnosticListener(parcel.readInt(), parcel.readInt(), ICarDiagnosticEventListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeInt(zRegisterOrUpdateDiagnosticListener ? 1 : 0);
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    CarDiagnosticEvent latestLiveFrame = getLatestLiveFrame();
                    parcel2.writeNoException();
                    if (latestLiveFrame != null) {
                        parcel2.writeInt(1);
                        latestLiveFrame.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    long[] freezeFrameTimestamps = getFreezeFrameTimestamps();
                    parcel2.writeNoException();
                    parcel2.writeLongArray(freezeFrameTimestamps);
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    CarDiagnosticEvent freezeFrame = getFreezeFrame(parcel.readLong());
                    parcel2.writeNoException();
                    if (freezeFrame != null) {
                        parcel2.writeInt(1);
                        freezeFrame.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zClearFreezeFrames = clearFreezeFrames(parcel.createLongArray());
                    parcel2.writeNoException();
                    parcel2.writeInt(zClearFreezeFrames ? 1 : 0);
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterDiagnosticListener(parcel.readInt(), ICarDiagnosticEventListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsLiveFrameSupported = isLiveFrameSupported();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsLiveFrameSupported ? 1 : 0);
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsFreezeFrameNotificationSupported = isFreezeFrameNotificationSupported();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsFreezeFrameNotificationSupported ? 1 : 0);
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsGetFreezeFrameSupported = isGetFreezeFrameSupported();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsGetFreezeFrameSupported ? 1 : 0);
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsClearFreezeFramesSupported = isClearFreezeFramesSupported();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsClearFreezeFramesSupported ? 1 : 0);
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsSelectiveClearFreezeFramesSupported = isSelectiveClearFreezeFramesSupported();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsSelectiveClearFreezeFramesSupported ? 1 : 0);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    boolean clearFreezeFrames(long[] jArr) throws RemoteException;

    CarDiagnosticEvent getFreezeFrame(long j) throws RemoteException;

    long[] getFreezeFrameTimestamps() throws RemoteException;

    CarDiagnosticEvent getLatestLiveFrame() throws RemoteException;

    boolean isClearFreezeFramesSupported() throws RemoteException;

    boolean isFreezeFrameNotificationSupported() throws RemoteException;

    boolean isGetFreezeFrameSupported() throws RemoteException;

    boolean isLiveFrameSupported() throws RemoteException;

    boolean isSelectiveClearFreezeFramesSupported() throws RemoteException;

    boolean registerOrUpdateDiagnosticListener(int i, int i2, ICarDiagnosticEventListener iCarDiagnosticEventListener) throws RemoteException;

    void unregisterDiagnosticListener(int i, ICarDiagnosticEventListener iCarDiagnosticEventListener) throws RemoteException;
}
