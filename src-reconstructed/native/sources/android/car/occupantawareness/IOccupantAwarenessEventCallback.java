package android.car.occupantawareness;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IOccupantAwarenessEventCallback extends IInterface {

    public static class Default implements IOccupantAwarenessEventCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.occupantawareness.IOccupantAwarenessEventCallback
        public void onDetectionEvent(OccupantAwarenessDetection occupantAwarenessDetection) throws RemoteException {
        }

        @Override // android.car.occupantawareness.IOccupantAwarenessEventCallback
        public void onStatusChanged(SystemStatusEvent systemStatusEvent) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements IOccupantAwarenessEventCallback {
        private static final String DESCRIPTOR = "android.car.occupantawareness.IOccupantAwarenessEventCallback";
        static final int TRANSACTION_onDetectionEvent = 2;
        static final int TRANSACTION_onStatusChanged = 1;

        private static class Proxy implements IOccupantAwarenessEventCallback {
            public static IOccupantAwarenessEventCallback sDefaultImpl;
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

            @Override // android.car.occupantawareness.IOccupantAwarenessEventCallback
            public void onDetectionEvent(OccupantAwarenessDetection occupantAwarenessDetection) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (occupantAwarenessDetection != null) {
                        parcelObtain.writeInt(1);
                        occupantAwarenessDetection.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(2, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onDetectionEvent(occupantAwarenessDetection);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.occupantawareness.IOccupantAwarenessEventCallback
            public void onStatusChanged(SystemStatusEvent systemStatusEvent) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (systemStatusEvent != null) {
                        parcelObtain.writeInt(1);
                        systemStatusEvent.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(1, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onStatusChanged(systemStatusEvent);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IOccupantAwarenessEventCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IOccupantAwarenessEventCallback)) ? new Proxy(iBinder) : (IOccupantAwarenessEventCallback) iInterfaceQueryLocalInterface;
        }

        public static IOccupantAwarenessEventCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IOccupantAwarenessEventCallback iOccupantAwarenessEventCallback) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iOccupantAwarenessEventCallback == null) {
                return false;
            }
            Proxy.sDefaultImpl = iOccupantAwarenessEventCallback;
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
                onStatusChanged(parcel.readInt() != 0 ? SystemStatusEvent.CREATOR.createFromParcel(parcel) : null);
                return true;
            }
            if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                onDetectionEvent(parcel.readInt() != 0 ? OccupantAwarenessDetection.CREATOR.createFromParcel(parcel) : null);
                return true;
            }
            if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            parcel2.writeString(DESCRIPTOR);
            return true;
        }
    }

    void onDetectionEvent(OccupantAwarenessDetection occupantAwarenessDetection) throws RemoteException;

    void onStatusChanged(SystemStatusEvent systemStatusEvent) throws RemoteException;
}
