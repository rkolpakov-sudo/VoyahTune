package android.car;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface IExperimentalCarHelper extends IInterface {

    public static class Default implements IExperimentalCarHelper {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.IExperimentalCarHelper
        public void onInitComplete(List<String> list, List<String> list2, List<String> list3, List<IBinder> list4) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements IExperimentalCarHelper {
        private static final String DESCRIPTOR = "android.car.IExperimentalCarHelper";
        static final int TRANSACTION_onInitComplete = 1;

        private static class Proxy implements IExperimentalCarHelper {
            public static IExperimentalCarHelper sDefaultImpl;
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

            @Override // android.car.IExperimentalCarHelper
            public void onInitComplete(List<String> list, List<String> list2, List<String> list3, List<IBinder> list4) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStringList(list);
                    parcelObtain.writeStringList(list2);
                    parcelObtain.writeStringList(list3);
                    parcelObtain.writeBinderList(list4);
                    if (this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().onInitComplete(list, list2, list3, list4);
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

        public static IExperimentalCarHelper asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IExperimentalCarHelper)) ? new Proxy(iBinder) : (IExperimentalCarHelper) iInterfaceQueryLocalInterface;
        }

        public static IExperimentalCarHelper getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IExperimentalCarHelper iExperimentalCarHelper) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iExperimentalCarHelper == null) {
                return false;
            }
            Proxy.sDefaultImpl = iExperimentalCarHelper;
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i != 1) {
                if (i != 1598968902) {
                    return super.onTransact(i, parcel, parcel2, i2);
                }
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            parcel.enforceInterface(DESCRIPTOR);
            onInitComplete(parcel.createStringArrayList(), parcel.createStringArrayList(), parcel.createStringArrayList(), parcel.createBinderArrayList());
            parcel2.writeNoException();
            return true;
        }
    }

    void onInitComplete(List<String> list, List<String> list2, List<String> list3, List<IBinder> list4) throws RemoteException;
}
