package android.car.cluster.renderer;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IInstrumentClusterHelper extends IInterface {

    public static class Default implements IInstrumentClusterHelper {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.cluster.renderer.IInstrumentClusterHelper
        public boolean startFixedActivityModeForDisplayAndUser(Intent intent, Bundle bundle, int i) throws RemoteException {
            return false;
        }

        @Override // android.car.cluster.renderer.IInstrumentClusterHelper
        public void stopFixedActivityMode(int i) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements IInstrumentClusterHelper {
        private static final String DESCRIPTOR = "android.car.cluster.renderer.IInstrumentClusterHelper";
        static final int TRANSACTION_startFixedActivityModeForDisplayAndUser = 1;
        static final int TRANSACTION_stopFixedActivityMode = 2;

        private static class Proxy implements IInstrumentClusterHelper {
            public static IInstrumentClusterHelper sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return "android.car.cluster.renderer.IInstrumentClusterHelper";
            }

            @Override // android.car.cluster.renderer.IInstrumentClusterHelper
            public boolean startFixedActivityModeForDisplayAndUser(Intent intent, Bundle bundle, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("android.car.cluster.renderer.IInstrumentClusterHelper");
                    if (intent != null) {
                        parcelObtain.writeInt(1);
                        intent.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (bundle != null) {
                        parcelObtain.writeInt(1);
                        bundle.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i);
                    if (!this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().startFixedActivityModeForDisplayAndUser(intent, bundle, i);
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.cluster.renderer.IInstrumentClusterHelper
            public void stopFixedActivityMode(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("android.car.cluster.renderer.IInstrumentClusterHelper");
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(2, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().stopFixedActivityMode(i);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "android.car.cluster.renderer.IInstrumentClusterHelper");
        }

        public static IInstrumentClusterHelper asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface("android.car.cluster.renderer.IInstrumentClusterHelper");
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IInstrumentClusterHelper)) ? new Proxy(iBinder) : (IInstrumentClusterHelper) iInterfaceQueryLocalInterface;
        }

        public static IInstrumentClusterHelper getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IInstrumentClusterHelper iInstrumentClusterHelper) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iInstrumentClusterHelper == null) {
                return false;
            }
            Proxy.sDefaultImpl = iInstrumentClusterHelper;
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1) {
                parcel.enforceInterface("android.car.cluster.renderer.IInstrumentClusterHelper");
                boolean zStartFixedActivityModeForDisplayAndUser = startFixedActivityModeForDisplayAndUser(parcel.readInt() != 0 ? (Intent) Intent.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(parcel) : null, parcel.readInt());
                parcel2.writeNoException();
                parcel2.writeInt(zStartFixedActivityModeForDisplayAndUser ? 1 : 0);
                return true;
            }
            if (i != 2) {
                if (i != 1598968902) {
                    return super.onTransact(i, parcel, parcel2, i2);
                }
                parcel2.writeString("android.car.cluster.renderer.IInstrumentClusterHelper");
                return true;
            }
            parcel.enforceInterface("android.car.cluster.renderer.IInstrumentClusterHelper");
            stopFixedActivityMode(parcel.readInt());
            parcel2.writeNoException();
            return true;
        }
    }

    boolean startFixedActivityModeForDisplayAndUser(Intent intent, Bundle bundle, int i) throws RemoteException;

    void stopFixedActivityMode(int i) throws RemoteException;
}
