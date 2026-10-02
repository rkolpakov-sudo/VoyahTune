package android.car;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface IExperimentalCar extends IInterface {

    public static class Default implements IExperimentalCar {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.IExperimentalCar
        public void init(IExperimentalCarHelper iExperimentalCarHelper, List<String> list) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements IExperimentalCar {
        private static final String DESCRIPTOR = "android.car.IExperimentalCar";
        static final int TRANSACTION_init = 1;

        private static class Proxy implements IExperimentalCar {
            public static IExperimentalCar sDefaultImpl;
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

            @Override // android.car.IExperimentalCar
            public void init(IExperimentalCarHelper iExperimentalCarHelper, List<String> list) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iExperimentalCarHelper != null ? iExperimentalCarHelper.asBinder() : null);
                    parcelObtain.writeStringList(list);
                    if (this.mRemote.transact(1, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().init(iExperimentalCarHelper, list);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IExperimentalCar asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IExperimentalCar)) ? new Proxy(iBinder) : (IExperimentalCar) iInterfaceQueryLocalInterface;
        }

        public static IExperimentalCar getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IExperimentalCar iExperimentalCar) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iExperimentalCar == null) {
                return false;
            }
            Proxy.sDefaultImpl = iExperimentalCar;
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
                init(IExperimentalCarHelper.Stub.asInterface(parcel.readStrongBinder()), parcel.createStringArrayList());
                return true;
            }
            if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            parcel2.writeString(DESCRIPTOR);
            return true;
        }
    }

    void init(IExperimentalCarHelper iExperimentalCarHelper, List<String> list) throws RemoteException;
}
