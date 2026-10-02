package android.car.diagnostic;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface ICarDiagnosticEventListener extends IInterface {

    public static class Default implements ICarDiagnosticEventListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.diagnostic.ICarDiagnosticEventListener
        public void onDiagnosticEvents(List<CarDiagnosticEvent> list) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarDiagnosticEventListener {
        private static final String DESCRIPTOR = "android.car.diagnostic.ICarDiagnosticEventListener";
        static final int TRANSACTION_onDiagnosticEvents = 1;

        private static class Proxy implements ICarDiagnosticEventListener {
            public static ICarDiagnosticEventListener sDefaultImpl;
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

            @Override // android.car.diagnostic.ICarDiagnosticEventListener
            public void onDiagnosticEvents(List<CarDiagnosticEvent> list) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeTypedList(list);
                    if (this.mRemote.transact(1, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().onDiagnosticEvents(list);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICarDiagnosticEventListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarDiagnosticEventListener)) ? new Proxy(iBinder) : (ICarDiagnosticEventListener) iInterfaceQueryLocalInterface;
        }

        public static ICarDiagnosticEventListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarDiagnosticEventListener iCarDiagnosticEventListener) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarDiagnosticEventListener == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarDiagnosticEventListener;
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
                onDiagnosticEvents(parcel.createTypedArrayList(CarDiagnosticEvent.CREATOR));
                return true;
            }
            if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            parcel2.writeString(DESCRIPTOR);
            return true;
        }
    }

    void onDiagnosticEvents(List<CarDiagnosticEvent> list) throws RemoteException;
}
