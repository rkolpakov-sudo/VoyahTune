package android.car.vms;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SharedMemory;

/* JADX INFO: loaded from: classes.dex */
public interface IVmsClientCallback extends IInterface {

    public static class Default implements IVmsClientCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.vms.IVmsClientCallback
        public void onLargePacketReceived(int i, VmsLayer vmsLayer, SharedMemory sharedMemory) throws RemoteException {
        }

        @Override // android.car.vms.IVmsClientCallback
        public void onLayerAvailabilityChanged(VmsAvailableLayers vmsAvailableLayers) throws RemoteException {
        }

        @Override // android.car.vms.IVmsClientCallback
        public void onPacketReceived(int i, VmsLayer vmsLayer, byte[] bArr) throws RemoteException {
        }

        @Override // android.car.vms.IVmsClientCallback
        public void onSubscriptionStateChanged(VmsSubscriptionState vmsSubscriptionState) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements IVmsClientCallback {
        private static final String DESCRIPTOR = "android.car.vms.IVmsClientCallback";
        static final int TRANSACTION_onLargePacketReceived = 4;
        static final int TRANSACTION_onLayerAvailabilityChanged = 1;
        static final int TRANSACTION_onPacketReceived = 3;
        static final int TRANSACTION_onSubscriptionStateChanged = 2;

        private static class Proxy implements IVmsClientCallback {
            public static IVmsClientCallback sDefaultImpl;
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

            @Override // android.car.vms.IVmsClientCallback
            public void onLargePacketReceived(int i, VmsLayer vmsLayer, SharedMemory sharedMemory) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (vmsLayer != null) {
                        parcelObtain.writeInt(1);
                        vmsLayer.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (sharedMemory != null) {
                        parcelObtain.writeInt(1);
                        sharedMemory.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(4, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onLargePacketReceived(i, vmsLayer, sharedMemory);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.vms.IVmsClientCallback
            public void onLayerAvailabilityChanged(VmsAvailableLayers vmsAvailableLayers) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (vmsAvailableLayers != null) {
                        parcelObtain.writeInt(1);
                        vmsAvailableLayers.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(1, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onLayerAvailabilityChanged(vmsAvailableLayers);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.vms.IVmsClientCallback
            public void onPacketReceived(int i, VmsLayer vmsLayer, byte[] bArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (vmsLayer != null) {
                        parcelObtain.writeInt(1);
                        vmsLayer.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeByteArray(bArr);
                    if (this.mRemote.transact(3, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onPacketReceived(i, vmsLayer, bArr);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.vms.IVmsClientCallback
            public void onSubscriptionStateChanged(VmsSubscriptionState vmsSubscriptionState) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (vmsSubscriptionState != null) {
                        parcelObtain.writeInt(1);
                        vmsSubscriptionState.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(2, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onSubscriptionStateChanged(vmsSubscriptionState);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IVmsClientCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IVmsClientCallback)) ? new Proxy(iBinder) : (IVmsClientCallback) iInterfaceQueryLocalInterface;
        }

        public static IVmsClientCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IVmsClientCallback iVmsClientCallback) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iVmsClientCallback == null) {
                return false;
            }
            Proxy.sDefaultImpl = iVmsClientCallback;
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
                onLayerAvailabilityChanged(parcel.readInt() != 0 ? VmsAvailableLayers.CREATOR.createFromParcel(parcel) : null);
                return true;
            }
            if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                onSubscriptionStateChanged(parcel.readInt() != 0 ? VmsSubscriptionState.CREATOR.createFromParcel(parcel) : null);
                return true;
            }
            if (i == 3) {
                parcel.enforceInterface(DESCRIPTOR);
                onPacketReceived(parcel.readInt(), parcel.readInt() != 0 ? VmsLayer.CREATOR.createFromParcel(parcel) : null, parcel.createByteArray());
                return true;
            }
            if (i == 4) {
                parcel.enforceInterface(DESCRIPTOR);
                onLargePacketReceived(parcel.readInt(), parcel.readInt() != 0 ? VmsLayer.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0 ? (SharedMemory) SharedMemory.CREATOR.createFromParcel(parcel) : null);
                return true;
            }
            if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            parcel2.writeString(DESCRIPTOR);
            return true;
        }
    }

    void onLargePacketReceived(int i, VmsLayer vmsLayer, SharedMemory sharedMemory) throws RemoteException;

    void onLayerAvailabilityChanged(VmsAvailableLayers vmsAvailableLayers) throws RemoteException;

    void onPacketReceived(int i, VmsLayer vmsLayer, byte[] bArr) throws RemoteException;

    void onSubscriptionStateChanged(VmsSubscriptionState vmsSubscriptionState) throws RemoteException;
}
