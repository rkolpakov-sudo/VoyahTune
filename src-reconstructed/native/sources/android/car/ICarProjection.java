package android.car;

import android.bluetooth.BluetoothDevice;
import android.car.projection.ProjectionStatus;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Messenger;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface ICarProjection extends IInterface {

    public static class Default implements ICarProjection {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.ICarProjection
        public int[] getAvailableWifiChannels(int i) throws RemoteException {
            return null;
        }

        @Override // android.car.ICarProjection
        public Bundle getProjectionOptions() throws RemoteException {
            return null;
        }

        @Override // android.car.ICarProjection
        public void registerKeyEventHandler(ICarProjectionKeyEventHandler iCarProjectionKeyEventHandler, byte[] bArr) throws RemoteException {
        }

        @Override // android.car.ICarProjection
        public void registerProjectionRunner(Intent intent) throws RemoteException {
        }

        @Override // android.car.ICarProjection
        public void registerProjectionStatusListener(ICarProjectionStatusListener iCarProjectionStatusListener) throws RemoteException {
        }

        @Override // android.car.ICarProjection
        public boolean releaseBluetoothProfileInhibit(BluetoothDevice bluetoothDevice, int i, IBinder iBinder) throws RemoteException {
            return false;
        }

        @Override // android.car.ICarProjection
        public boolean requestBluetoothProfileInhibit(BluetoothDevice bluetoothDevice, int i, IBinder iBinder) throws RemoteException {
            return false;
        }

        @Override // android.car.ICarProjection
        public void startProjectionAccessPoint(Messenger messenger, IBinder iBinder) throws RemoteException {
        }

        @Override // android.car.ICarProjection
        public void stopProjectionAccessPoint(IBinder iBinder) throws RemoteException {
        }

        @Override // android.car.ICarProjection
        public void unregisterKeyEventHandler(ICarProjectionKeyEventHandler iCarProjectionKeyEventHandler) throws RemoteException {
        }

        @Override // android.car.ICarProjection
        public void unregisterProjectionRunner(Intent intent) throws RemoteException {
        }

        @Override // android.car.ICarProjection
        public void unregisterProjectionStatusListener(ICarProjectionStatusListener iCarProjectionStatusListener) throws RemoteException {
        }

        @Override // android.car.ICarProjection
        public void updateProjectionStatus(ProjectionStatus projectionStatus, IBinder iBinder) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarProjection {
        private static final String DESCRIPTOR = "android.car.ICarProjection";
        static final int TRANSACTION_getAvailableWifiChannels = 13;
        static final int TRANSACTION_getProjectionOptions = 12;
        static final int TRANSACTION_registerKeyEventHandler = 3;
        static final int TRANSACTION_registerProjectionRunner = 1;
        static final int TRANSACTION_registerProjectionStatusListener = 10;
        static final int TRANSACTION_releaseBluetoothProfileInhibit = 8;
        static final int TRANSACTION_requestBluetoothProfileInhibit = 7;
        static final int TRANSACTION_startProjectionAccessPoint = 5;
        static final int TRANSACTION_stopProjectionAccessPoint = 6;
        static final int TRANSACTION_unregisterKeyEventHandler = 4;
        static final int TRANSACTION_unregisterProjectionRunner = 2;
        static final int TRANSACTION_unregisterProjectionStatusListener = 11;
        static final int TRANSACTION_updateProjectionStatus = 9;

        private static class Proxy implements ICarProjection {
            public static ICarProjection sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.car.ICarProjection
            public int[] getAvailableWifiChannels(int i) throws RemoteException {
                int[] iArrCreateIntArray;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(13, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        iArrCreateIntArray = parcelObtain2.createIntArray();
                    } else {
                        iArrCreateIntArray = Stub.getDefaultImpl().getAvailableWifiChannels(i);
                    }
                    return iArrCreateIntArray;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.car.ICarProjection
            public Bundle getProjectionOptions() throws RemoteException {
                Bundle projectionOptions;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(12, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        projectionOptions = parcelObtain2.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(parcelObtain2) : null;
                    } else {
                        projectionOptions = Stub.getDefaultImpl().getProjectionOptions();
                    }
                    return projectionOptions;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarProjection
            public void registerKeyEventHandler(ICarProjectionKeyEventHandler iCarProjectionKeyEventHandler, byte[] bArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarProjectionKeyEventHandler != null ? iCarProjectionKeyEventHandler.asBinder() : null);
                    parcelObtain.writeByteArray(bArr);
                    if (this.mRemote.transact(3, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().registerKeyEventHandler(iCarProjectionKeyEventHandler, bArr);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarProjection
            public void registerProjectionRunner(Intent intent) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        parcelObtain.writeInt(1);
                        intent.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().registerProjectionRunner(intent);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarProjection
            public void registerProjectionStatusListener(ICarProjectionStatusListener iCarProjectionStatusListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarProjectionStatusListener != null ? iCarProjectionStatusListener.asBinder() : null);
                    if (this.mRemote.transact(10, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().registerProjectionStatusListener(iCarProjectionStatusListener);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarProjection
            public boolean releaseBluetoothProfileInhibit(BluetoothDevice bluetoothDevice, int i, IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bluetoothDevice != null) {
                        parcelObtain.writeInt(1);
                        bluetoothDevice.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i);
                    parcelObtain.writeStrongBinder(iBinder);
                    if (!this.mRemote.transact(8, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().releaseBluetoothProfileInhibit(bluetoothDevice, i, iBinder);
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarProjection
            public boolean requestBluetoothProfileInhibit(BluetoothDevice bluetoothDevice, int i, IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bluetoothDevice != null) {
                        parcelObtain.writeInt(1);
                        bluetoothDevice.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i);
                    parcelObtain.writeStrongBinder(iBinder);
                    if (!this.mRemote.transact(7, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().requestBluetoothProfileInhibit(bluetoothDevice, i, iBinder);
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarProjection
            public void startProjectionAccessPoint(Messenger messenger, IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (messenger != null) {
                        parcelObtain.writeInt(1);
                        messenger.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(iBinder);
                    if (this.mRemote.transact(5, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().startProjectionAccessPoint(messenger, iBinder);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarProjection
            public void stopProjectionAccessPoint(IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    if (this.mRemote.transact(6, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().stopProjectionAccessPoint(iBinder);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarProjection
            public void unregisterKeyEventHandler(ICarProjectionKeyEventHandler iCarProjectionKeyEventHandler) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarProjectionKeyEventHandler != null ? iCarProjectionKeyEventHandler.asBinder() : null);
                    if (this.mRemote.transact(4, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregisterKeyEventHandler(iCarProjectionKeyEventHandler);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarProjection
            public void unregisterProjectionRunner(Intent intent) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        parcelObtain.writeInt(1);
                        intent.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(2, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregisterProjectionRunner(intent);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarProjection
            public void unregisterProjectionStatusListener(ICarProjectionStatusListener iCarProjectionStatusListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarProjectionStatusListener != null ? iCarProjectionStatusListener.asBinder() : null);
                    if (this.mRemote.transact(11, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregisterProjectionStatusListener(iCarProjectionStatusListener);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarProjection
            public void updateProjectionStatus(ProjectionStatus projectionStatus, IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (projectionStatus != null) {
                        parcelObtain.writeInt(1);
                        projectionStatus.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeStrongBinder(iBinder);
                    if (this.mRemote.transact(9, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().updateProjectionStatus(projectionStatus, iBinder);
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

        public static ICarProjection asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarProjection)) ? new Proxy(iBinder) : (ICarProjection) iInterfaceQueryLocalInterface;
        }

        public static ICarProjection getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarProjection iCarProjection) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarProjection == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarProjection;
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
                    registerProjectionRunner(parcel.readInt() != 0 ? (Intent) Intent.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterProjectionRunner(parcel.readInt() != 0 ? (Intent) Intent.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    registerKeyEventHandler(ICarProjectionKeyEventHandler.Stub.asInterface(parcel.readStrongBinder()), parcel.createByteArray());
                    parcel2.writeNoException();
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterKeyEventHandler(ICarProjectionKeyEventHandler.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    startProjectionAccessPoint(parcel.readInt() != 0 ? (Messenger) Messenger.CREATOR.createFromParcel(parcel) : null, parcel.readStrongBinder());
                    parcel2.writeNoException();
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    stopProjectionAccessPoint(parcel.readStrongBinder());
                    parcel2.writeNoException();
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zRequestBluetoothProfileInhibit = requestBluetoothProfileInhibit(parcel.readInt() != 0 ? (BluetoothDevice) BluetoothDevice.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), parcel.readStrongBinder());
                    parcel2.writeNoException();
                    parcel2.writeInt(zRequestBluetoothProfileInhibit ? 1 : 0);
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zReleaseBluetoothProfileInhibit = releaseBluetoothProfileInhibit(parcel.readInt() != 0 ? (BluetoothDevice) BluetoothDevice.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), parcel.readStrongBinder());
                    parcel2.writeNoException();
                    parcel2.writeInt(zReleaseBluetoothProfileInhibit ? 1 : 0);
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    updateProjectionStatus(parcel.readInt() != 0 ? ProjectionStatus.CREATOR.createFromParcel(parcel) : null, parcel.readStrongBinder());
                    parcel2.writeNoException();
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    registerProjectionStatusListener(ICarProjectionStatusListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterProjectionStatusListener(ICarProjectionStatusListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    Bundle projectionOptions = getProjectionOptions();
                    parcel2.writeNoException();
                    if (projectionOptions != null) {
                        parcel2.writeInt(1);
                        projectionOptions.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 13:
                    parcel.enforceInterface(DESCRIPTOR);
                    int[] availableWifiChannels = getAvailableWifiChannels(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeIntArray(availableWifiChannels);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    int[] getAvailableWifiChannels(int i) throws RemoteException;

    Bundle getProjectionOptions() throws RemoteException;

    void registerKeyEventHandler(ICarProjectionKeyEventHandler iCarProjectionKeyEventHandler, byte[] bArr) throws RemoteException;

    void registerProjectionRunner(Intent intent) throws RemoteException;

    void registerProjectionStatusListener(ICarProjectionStatusListener iCarProjectionStatusListener) throws RemoteException;

    boolean releaseBluetoothProfileInhibit(BluetoothDevice bluetoothDevice, int i, IBinder iBinder) throws RemoteException;

    boolean requestBluetoothProfileInhibit(BluetoothDevice bluetoothDevice, int i, IBinder iBinder) throws RemoteException;

    void startProjectionAccessPoint(Messenger messenger, IBinder iBinder) throws RemoteException;

    void stopProjectionAccessPoint(IBinder iBinder) throws RemoteException;

    void unregisterKeyEventHandler(ICarProjectionKeyEventHandler iCarProjectionKeyEventHandler) throws RemoteException;

    void unregisterProjectionRunner(Intent intent) throws RemoteException;

    void unregisterProjectionStatusListener(ICarProjectionStatusListener iCarProjectionStatusListener) throws RemoteException;

    void updateProjectionStatus(ProjectionStatus projectionStatus, IBinder iBinder) throws RemoteException;
}
