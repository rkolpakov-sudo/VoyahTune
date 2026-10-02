package android.car.input;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.KeyEvent;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface ICarInputCallback extends IInterface {

    public static class Default implements ICarInputCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.input.ICarInputCallback
        public void onCaptureStateChanged(int i, int[] iArr) throws RemoteException {
        }

        @Override // android.car.input.ICarInputCallback
        public void onKeyEvents(int i, List<KeyEvent> list) throws RemoteException {
        }

        @Override // android.car.input.ICarInputCallback
        public void onRotaryEvents(int i, List<RotaryEvent> list) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarInputCallback {
        private static final String DESCRIPTOR = "android.car.input.ICarInputCallback";
        static final int TRANSACTION_onCaptureStateChanged = 4;
        static final int TRANSACTION_onKeyEvents = 2;
        static final int TRANSACTION_onRotaryEvents = 3;

        private static class Proxy implements ICarInputCallback {
            public static ICarInputCallback sDefaultImpl;
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

            @Override // android.car.input.ICarInputCallback
            public void onCaptureStateChanged(int i, int[] iArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeIntArray(iArr);
                    if (this.mRemote.transact(4, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onCaptureStateChanged(i, iArr);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.input.ICarInputCallback
            public void onKeyEvents(int i, List<KeyEvent> list) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeTypedList(list);
                    if (this.mRemote.transact(2, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onKeyEvents(i, list);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.input.ICarInputCallback
            public void onRotaryEvents(int i, List<RotaryEvent> list) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeTypedList(list);
                    if (this.mRemote.transact(3, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onRotaryEvents(i, list);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICarInputCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarInputCallback)) ? new Proxy(iBinder) : (ICarInputCallback) iInterfaceQueryLocalInterface;
        }

        public static ICarInputCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarInputCallback iCarInputCallback) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarInputCallback == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarInputCallback;
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                onKeyEvents(parcel.readInt(), parcel.createTypedArrayList(KeyEvent.CREATOR));
                return true;
            }
            if (i == 3) {
                parcel.enforceInterface(DESCRIPTOR);
                onRotaryEvents(parcel.readInt(), parcel.createTypedArrayList(RotaryEvent.CREATOR));
                return true;
            }
            if (i == 4) {
                parcel.enforceInterface(DESCRIPTOR);
                onCaptureStateChanged(parcel.readInt(), parcel.createIntArray());
                return true;
            }
            if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            parcel2.writeString(DESCRIPTOR);
            return true;
        }
    }

    void onCaptureStateChanged(int i, int[] iArr) throws RemoteException;

    void onKeyEvents(int i, List<KeyEvent> list) throws RemoteException;

    void onRotaryEvents(int i, List<RotaryEvent> list) throws RemoteException;
}
