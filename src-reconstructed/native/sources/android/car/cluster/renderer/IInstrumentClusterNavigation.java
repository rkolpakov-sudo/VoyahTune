package android.car.cluster.renderer;

import android.car.navigation.CarNavigationInstrumentCluster;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IInstrumentClusterNavigation extends IInterface {

    public static class Default implements IInstrumentClusterNavigation {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.cluster.renderer.IInstrumentClusterNavigation
        public CarNavigationInstrumentCluster getInstrumentClusterInfo() throws RemoteException {
            return null;
        }

        @Override // android.car.cluster.renderer.IInstrumentClusterNavigation
        public void onNavigationStateChanged(Bundle bundle) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements IInstrumentClusterNavigation {
        private static final String DESCRIPTOR = "android.car.cluster.renderer.IInstrumentClusterNavigation";
        static final int TRANSACTION_getInstrumentClusterInfo = 2;
        static final int TRANSACTION_onNavigationStateChanged = 1;

        private static class Proxy implements IInstrumentClusterNavigation {
            public static IInstrumentClusterNavigation sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.car.cluster.renderer.IInstrumentClusterNavigation
            public CarNavigationInstrumentCluster getInstrumentClusterInfo() throws RemoteException {
                CarNavigationInstrumentCluster carNavigationInstrumentClusterCreateFromParcel;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(2, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        carNavigationInstrumentClusterCreateFromParcel = parcelObtain2.readInt() != 0 ? CarNavigationInstrumentCluster.CREATOR.createFromParcel(parcelObtain2) : null;
                    } else {
                        carNavigationInstrumentClusterCreateFromParcel = Stub.getDefaultImpl().getInstrumentClusterInfo();
                    }
                    return carNavigationInstrumentClusterCreateFromParcel;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.car.cluster.renderer.IInstrumentClusterNavigation
            public void onNavigationStateChanged(Bundle bundle) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        parcelObtain.writeInt(1);
                        bundle.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().onNavigationStateChanged(bundle);
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

        public static IInstrumentClusterNavigation asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IInstrumentClusterNavigation)) ? new Proxy(iBinder) : (IInstrumentClusterNavigation) iInterfaceQueryLocalInterface;
        }

        public static IInstrumentClusterNavigation getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IInstrumentClusterNavigation iInstrumentClusterNavigation) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iInstrumentClusterNavigation == null) {
                return false;
            }
            Proxy.sDefaultImpl = iInstrumentClusterNavigation;
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
                onNavigationStateChanged(parcel.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(parcel) : null);
                parcel2.writeNoException();
                return true;
            }
            if (i != 2) {
                if (i != 1598968902) {
                    return super.onTransact(i, parcel, parcel2, i2);
                }
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            parcel.enforceInterface(DESCRIPTOR);
            CarNavigationInstrumentCluster instrumentClusterInfo = getInstrumentClusterInfo();
            parcel2.writeNoException();
            if (instrumentClusterInfo != null) {
                parcel2.writeInt(1);
                instrumentClusterInfo.writeToParcel(parcel2, 1);
            } else {
                parcel2.writeInt(0);
            }
            return true;
        }
    }

    CarNavigationInstrumentCluster getInstrumentClusterInfo() throws RemoteException;

    void onNavigationStateChanged(Bundle bundle) throws RemoteException;
}
