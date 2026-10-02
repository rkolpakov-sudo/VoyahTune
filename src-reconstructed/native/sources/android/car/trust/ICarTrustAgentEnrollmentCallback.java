package android.car.trust;

import android.bluetooth.BluetoothDevice;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface ICarTrustAgentEnrollmentCallback extends IInterface {

    public static class Default implements ICarTrustAgentEnrollmentCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.trust.ICarTrustAgentEnrollmentCallback
        public void onAuthStringAvailable(BluetoothDevice bluetoothDevice, String str) throws RemoteException {
        }

        @Override // android.car.trust.ICarTrustAgentEnrollmentCallback
        public void onEnrollmentHandshakeFailure(BluetoothDevice bluetoothDevice, int i) throws RemoteException {
        }

        @Override // android.car.trust.ICarTrustAgentEnrollmentCallback
        public void onEscrowTokenActiveStateChanged(long j, boolean z) throws RemoteException {
        }

        @Override // android.car.trust.ICarTrustAgentEnrollmentCallback
        public void onEscrowTokenAdded(long j) throws RemoteException {
        }

        @Override // android.car.trust.ICarTrustAgentEnrollmentCallback
        public void onEscrowTokenRemoved(long j) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarTrustAgentEnrollmentCallback {
        private static final String DESCRIPTOR = "android.car.trust.ICarTrustAgentEnrollmentCallback";
        static final int TRANSACTION_onAuthStringAvailable = 2;
        static final int TRANSACTION_onEnrollmentHandshakeFailure = 1;
        static final int TRANSACTION_onEscrowTokenActiveStateChanged = 5;
        static final int TRANSACTION_onEscrowTokenAdded = 3;
        static final int TRANSACTION_onEscrowTokenRemoved = 4;

        private static class Proxy implements ICarTrustAgentEnrollmentCallback {
            public static ICarTrustAgentEnrollmentCallback sDefaultImpl;
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

            @Override // android.car.trust.ICarTrustAgentEnrollmentCallback
            public void onAuthStringAvailable(BluetoothDevice bluetoothDevice, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bluetoothDevice != null) {
                        parcelObtain.writeInt(1);
                        bluetoothDevice.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeString(str);
                    if (this.mRemote.transact(2, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onAuthStringAvailable(bluetoothDevice, str);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.trust.ICarTrustAgentEnrollmentCallback
            public void onEnrollmentHandshakeFailure(BluetoothDevice bluetoothDevice, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bluetoothDevice != null) {
                        parcelObtain.writeInt(1);
                        bluetoothDevice.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(1, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onEnrollmentHandshakeFailure(bluetoothDevice, i);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.trust.ICarTrustAgentEnrollmentCallback
            public void onEscrowTokenActiveStateChanged(long j, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeLong(j);
                    parcelObtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(5, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onEscrowTokenActiveStateChanged(j, z);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.trust.ICarTrustAgentEnrollmentCallback
            public void onEscrowTokenAdded(long j) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeLong(j);
                    if (this.mRemote.transact(3, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onEscrowTokenAdded(j);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.trust.ICarTrustAgentEnrollmentCallback
            public void onEscrowTokenRemoved(long j) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeLong(j);
                    if (this.mRemote.transact(4, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onEscrowTokenRemoved(j);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICarTrustAgentEnrollmentCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarTrustAgentEnrollmentCallback)) ? new Proxy(iBinder) : (ICarTrustAgentEnrollmentCallback) iInterfaceQueryLocalInterface;
        }

        public static ICarTrustAgentEnrollmentCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarTrustAgentEnrollmentCallback iCarTrustAgentEnrollmentCallback) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarTrustAgentEnrollmentCallback == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarTrustAgentEnrollmentCallback;
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
                onEnrollmentHandshakeFailure(parcel.readInt() != 0 ? (BluetoothDevice) BluetoothDevice.CREATOR.createFromParcel(parcel) : null, parcel.readInt());
                return true;
            }
            if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                onAuthStringAvailable(parcel.readInt() != 0 ? (BluetoothDevice) BluetoothDevice.CREATOR.createFromParcel(parcel) : null, parcel.readString());
                return true;
            }
            if (i == 3) {
                parcel.enforceInterface(DESCRIPTOR);
                onEscrowTokenAdded(parcel.readLong());
                return true;
            }
            if (i == 4) {
                parcel.enforceInterface(DESCRIPTOR);
                onEscrowTokenRemoved(parcel.readLong());
                return true;
            }
            if (i == 5) {
                parcel.enforceInterface(DESCRIPTOR);
                onEscrowTokenActiveStateChanged(parcel.readLong(), parcel.readInt() != 0);
                return true;
            }
            if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            parcel2.writeString(DESCRIPTOR);
            return true;
        }
    }

    void onAuthStringAvailable(BluetoothDevice bluetoothDevice, String str) throws RemoteException;

    void onEnrollmentHandshakeFailure(BluetoothDevice bluetoothDevice, int i) throws RemoteException;

    void onEscrowTokenActiveStateChanged(long j, boolean z) throws RemoteException;

    void onEscrowTokenAdded(long j) throws RemoteException;

    void onEscrowTokenRemoved(long j) throws RemoteException;
}
