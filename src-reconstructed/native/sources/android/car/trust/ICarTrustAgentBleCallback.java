package android.car.trust;

import android.bluetooth.BluetoothDevice;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface ICarTrustAgentBleCallback extends IInterface {

    public static class Default implements ICarTrustAgentBleCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.trust.ICarTrustAgentBleCallback
        public void onBleEnrollmentDeviceConnected(BluetoothDevice bluetoothDevice) throws RemoteException {
        }

        @Override // android.car.trust.ICarTrustAgentBleCallback
        public void onBleEnrollmentDeviceDisconnected(BluetoothDevice bluetoothDevice) throws RemoteException {
        }

        @Override // android.car.trust.ICarTrustAgentBleCallback
        public void onEnrollmentAdvertisingFailed() throws RemoteException {
        }

        @Override // android.car.trust.ICarTrustAgentBleCallback
        public void onEnrollmentAdvertisingStarted() throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarTrustAgentBleCallback {
        private static final String DESCRIPTOR = "android.car.trust.ICarTrustAgentBleCallback";
        static final int TRANSACTION_onBleEnrollmentDeviceConnected = 3;
        static final int TRANSACTION_onBleEnrollmentDeviceDisconnected = 4;
        static final int TRANSACTION_onEnrollmentAdvertisingFailed = 2;
        static final int TRANSACTION_onEnrollmentAdvertisingStarted = 1;

        private static class Proxy implements ICarTrustAgentBleCallback {
            public static ICarTrustAgentBleCallback sDefaultImpl;
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

            @Override // android.car.trust.ICarTrustAgentBleCallback
            public void onBleEnrollmentDeviceConnected(BluetoothDevice bluetoothDevice) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bluetoothDevice != null) {
                        parcelObtain.writeInt(1);
                        bluetoothDevice.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(3, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onBleEnrollmentDeviceConnected(bluetoothDevice);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.trust.ICarTrustAgentBleCallback
            public void onBleEnrollmentDeviceDisconnected(BluetoothDevice bluetoothDevice) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bluetoothDevice != null) {
                        parcelObtain.writeInt(1);
                        bluetoothDevice.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(4, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onBleEnrollmentDeviceDisconnected(bluetoothDevice);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.trust.ICarTrustAgentBleCallback
            public void onEnrollmentAdvertisingFailed() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(2, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onEnrollmentAdvertisingFailed();
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.trust.ICarTrustAgentBleCallback
            public void onEnrollmentAdvertisingStarted() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(1, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onEnrollmentAdvertisingStarted();
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICarTrustAgentBleCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarTrustAgentBleCallback)) ? new Proxy(iBinder) : (ICarTrustAgentBleCallback) iInterfaceQueryLocalInterface;
        }

        public static ICarTrustAgentBleCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarTrustAgentBleCallback iCarTrustAgentBleCallback) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarTrustAgentBleCallback == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarTrustAgentBleCallback;
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
                onEnrollmentAdvertisingStarted();
                return true;
            }
            if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                onEnrollmentAdvertisingFailed();
                return true;
            }
            if (i == 3) {
                parcel.enforceInterface(DESCRIPTOR);
                onBleEnrollmentDeviceConnected(parcel.readInt() != 0 ? (BluetoothDevice) BluetoothDevice.CREATOR.createFromParcel(parcel) : null);
                return true;
            }
            if (i == 4) {
                parcel.enforceInterface(DESCRIPTOR);
                onBleEnrollmentDeviceDisconnected(parcel.readInt() != 0 ? (BluetoothDevice) BluetoothDevice.CREATOR.createFromParcel(parcel) : null);
                return true;
            }
            if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            parcel2.writeString(DESCRIPTOR);
            return true;
        }
    }

    void onBleEnrollmentDeviceConnected(BluetoothDevice bluetoothDevice) throws RemoteException;

    void onBleEnrollmentDeviceDisconnected(BluetoothDevice bluetoothDevice) throws RemoteException;

    void onEnrollmentAdvertisingFailed() throws RemoteException;

    void onEnrollmentAdvertisingStarted() throws RemoteException;
}
