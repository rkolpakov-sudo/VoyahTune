package android.car.trust;

import android.bluetooth.BluetoothDevice;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface ICarTrustAgentEnrollment extends IInterface {

    public static class Default implements ICarTrustAgentEnrollment {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.trust.ICarTrustAgentEnrollment
        public void enrollmentHandshakeAccepted(BluetoothDevice bluetoothDevice) throws RemoteException {
        }

        @Override // android.car.trust.ICarTrustAgentEnrollment
        public List<TrustedDeviceInfo> getEnrolledDeviceInfosForUser(int i) throws RemoteException {
            return null;
        }

        @Override // android.car.trust.ICarTrustAgentEnrollment
        public boolean isEscrowTokenActive(long j, int i) throws RemoteException {
            return false;
        }

        @Override // android.car.trust.ICarTrustAgentEnrollment
        public void registerBleCallback(ICarTrustAgentBleCallback iCarTrustAgentBleCallback) throws RemoteException {
        }

        @Override // android.car.trust.ICarTrustAgentEnrollment
        public void registerEnrollmentCallback(ICarTrustAgentEnrollmentCallback iCarTrustAgentEnrollmentCallback) throws RemoteException {
        }

        @Override // android.car.trust.ICarTrustAgentEnrollment
        public void removeAllTrustedDevices(int i) throws RemoteException {
        }

        @Override // android.car.trust.ICarTrustAgentEnrollment
        public void removeEscrowToken(long j, int i) throws RemoteException {
        }

        @Override // android.car.trust.ICarTrustAgentEnrollment
        public void setTrustedDeviceEnrollmentEnabled(boolean z) throws RemoteException {
        }

        @Override // android.car.trust.ICarTrustAgentEnrollment
        public void setTrustedDeviceUnlockEnabled(boolean z) throws RemoteException {
        }

        @Override // android.car.trust.ICarTrustAgentEnrollment
        public void startEnrollmentAdvertising() throws RemoteException {
        }

        @Override // android.car.trust.ICarTrustAgentEnrollment
        public void stopEnrollmentAdvertising() throws RemoteException {
        }

        @Override // android.car.trust.ICarTrustAgentEnrollment
        public void terminateEnrollmentHandshake() throws RemoteException {
        }

        @Override // android.car.trust.ICarTrustAgentEnrollment
        public void unregisterBleCallback(ICarTrustAgentBleCallback iCarTrustAgentBleCallback) throws RemoteException {
        }

        @Override // android.car.trust.ICarTrustAgentEnrollment
        public void unregisterEnrollmentCallback(ICarTrustAgentEnrollmentCallback iCarTrustAgentEnrollmentCallback) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarTrustAgentEnrollment {
        private static final String DESCRIPTOR = "android.car.trust.ICarTrustAgentEnrollment";
        static final int TRANSACTION_enrollmentHandshakeAccepted = 3;
        static final int TRANSACTION_getEnrolledDeviceInfosForUser = 10;
        static final int TRANSACTION_isEscrowTokenActive = 5;
        static final int TRANSACTION_registerBleCallback = 13;
        static final int TRANSACTION_registerEnrollmentCallback = 11;
        static final int TRANSACTION_removeAllTrustedDevices = 7;
        static final int TRANSACTION_removeEscrowToken = 6;
        static final int TRANSACTION_setTrustedDeviceEnrollmentEnabled = 8;
        static final int TRANSACTION_setTrustedDeviceUnlockEnabled = 9;
        static final int TRANSACTION_startEnrollmentAdvertising = 1;
        static final int TRANSACTION_stopEnrollmentAdvertising = 2;
        static final int TRANSACTION_terminateEnrollmentHandshake = 4;
        static final int TRANSACTION_unregisterBleCallback = 14;
        static final int TRANSACTION_unregisterEnrollmentCallback = 12;

        private static class Proxy implements ICarTrustAgentEnrollment {
            public static ICarTrustAgentEnrollment sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.car.trust.ICarTrustAgentEnrollment
            public void enrollmentHandshakeAccepted(BluetoothDevice bluetoothDevice) throws RemoteException {
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
                    if (this.mRemote.transact(3, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().enrollmentHandshakeAccepted(bluetoothDevice);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.trust.ICarTrustAgentEnrollment
            public List<TrustedDeviceInfo> getEnrolledDeviceInfosForUser(int i) throws RemoteException {
                List<TrustedDeviceInfo> listCreateTypedArrayList;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(10, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        listCreateTypedArrayList = parcelObtain2.createTypedArrayList(TrustedDeviceInfo.CREATOR);
                    } else {
                        listCreateTypedArrayList = Stub.getDefaultImpl().getEnrolledDeviceInfosForUser(i);
                    }
                    return listCreateTypedArrayList;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.car.trust.ICarTrustAgentEnrollment
            public boolean isEscrowTokenActive(long j, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeLong(j);
                    parcelObtain.writeInt(i);
                    if (!this.mRemote.transact(5, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isEscrowTokenActive(j, i);
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.trust.ICarTrustAgentEnrollment
            public void registerBleCallback(ICarTrustAgentBleCallback iCarTrustAgentBleCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarTrustAgentBleCallback != null ? iCarTrustAgentBleCallback.asBinder() : null);
                    if (this.mRemote.transact(13, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().registerBleCallback(iCarTrustAgentBleCallback);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.trust.ICarTrustAgentEnrollment
            public void registerEnrollmentCallback(ICarTrustAgentEnrollmentCallback iCarTrustAgentEnrollmentCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarTrustAgentEnrollmentCallback != null ? iCarTrustAgentEnrollmentCallback.asBinder() : null);
                    if (this.mRemote.transact(11, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().registerEnrollmentCallback(iCarTrustAgentEnrollmentCallback);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.trust.ICarTrustAgentEnrollment
            public void removeAllTrustedDevices(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(7, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().removeAllTrustedDevices(i);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.trust.ICarTrustAgentEnrollment
            public void removeEscrowToken(long j, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeLong(j);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(6, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().removeEscrowToken(j, i);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.trust.ICarTrustAgentEnrollment
            public void setTrustedDeviceEnrollmentEnabled(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(8, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setTrustedDeviceEnrollmentEnabled(z);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.trust.ICarTrustAgentEnrollment
            public void setTrustedDeviceUnlockEnabled(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(9, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setTrustedDeviceUnlockEnabled(z);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.trust.ICarTrustAgentEnrollment
            public void startEnrollmentAdvertising() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().startEnrollmentAdvertising();
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.trust.ICarTrustAgentEnrollment
            public void stopEnrollmentAdvertising() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(2, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().stopEnrollmentAdvertising();
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.trust.ICarTrustAgentEnrollment
            public void terminateEnrollmentHandshake() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(4, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().terminateEnrollmentHandshake();
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.trust.ICarTrustAgentEnrollment
            public void unregisterBleCallback(ICarTrustAgentBleCallback iCarTrustAgentBleCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarTrustAgentBleCallback != null ? iCarTrustAgentBleCallback.asBinder() : null);
                    if (this.mRemote.transact(14, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregisterBleCallback(iCarTrustAgentBleCallback);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.trust.ICarTrustAgentEnrollment
            public void unregisterEnrollmentCallback(ICarTrustAgentEnrollmentCallback iCarTrustAgentEnrollmentCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarTrustAgentEnrollmentCallback != null ? iCarTrustAgentEnrollmentCallback.asBinder() : null);
                    if (this.mRemote.transact(12, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregisterEnrollmentCallback(iCarTrustAgentEnrollmentCallback);
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

        public static ICarTrustAgentEnrollment asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarTrustAgentEnrollment)) ? new Proxy(iBinder) : (ICarTrustAgentEnrollment) iInterfaceQueryLocalInterface;
        }

        public static ICarTrustAgentEnrollment getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarTrustAgentEnrollment iCarTrustAgentEnrollment) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarTrustAgentEnrollment == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarTrustAgentEnrollment;
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
                    startEnrollmentAdvertising();
                    parcel2.writeNoException();
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    stopEnrollmentAdvertising();
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    enrollmentHandshakeAccepted(parcel.readInt() != 0 ? (BluetoothDevice) BluetoothDevice.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    terminateEnrollmentHandshake();
                    parcel2.writeNoException();
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsEscrowTokenActive = isEscrowTokenActive(parcel.readLong(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsEscrowTokenActive ? 1 : 0);
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    removeEscrowToken(parcel.readLong(), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    removeAllTrustedDevices(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    setTrustedDeviceEnrollmentEnabled(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    setTrustedDeviceUnlockEnabled(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    List<TrustedDeviceInfo> enrolledDeviceInfosForUser = getEnrolledDeviceInfosForUser(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeTypedList(enrolledDeviceInfosForUser);
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    registerEnrollmentCallback(ICarTrustAgentEnrollmentCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterEnrollmentCallback(ICarTrustAgentEnrollmentCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 13:
                    parcel.enforceInterface(DESCRIPTOR);
                    registerBleCallback(ICarTrustAgentBleCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 14:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterBleCallback(ICarTrustAgentBleCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void enrollmentHandshakeAccepted(BluetoothDevice bluetoothDevice) throws RemoteException;

    List<TrustedDeviceInfo> getEnrolledDeviceInfosForUser(int i) throws RemoteException;

    boolean isEscrowTokenActive(long j, int i) throws RemoteException;

    void registerBleCallback(ICarTrustAgentBleCallback iCarTrustAgentBleCallback) throws RemoteException;

    void registerEnrollmentCallback(ICarTrustAgentEnrollmentCallback iCarTrustAgentEnrollmentCallback) throws RemoteException;

    void removeAllTrustedDevices(int i) throws RemoteException;

    void removeEscrowToken(long j, int i) throws RemoteException;

    void setTrustedDeviceEnrollmentEnabled(boolean z) throws RemoteException;

    void setTrustedDeviceUnlockEnabled(boolean z) throws RemoteException;

    void startEnrollmentAdvertising() throws RemoteException;

    void stopEnrollmentAdvertising() throws RemoteException;

    void terminateEnrollmentHandshake() throws RemoteException;

    void unregisterBleCallback(ICarTrustAgentBleCallback iCarTrustAgentBleCallback) throws RemoteException;

    void unregisterEnrollmentCallback(ICarTrustAgentEnrollmentCallback iCarTrustAgentEnrollmentCallback) throws RemoteException;
}
