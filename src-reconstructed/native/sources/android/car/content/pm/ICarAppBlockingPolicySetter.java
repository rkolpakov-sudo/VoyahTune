package android.car.content.pm;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface ICarAppBlockingPolicySetter extends IInterface {

    public static class Default implements ICarAppBlockingPolicySetter {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.content.pm.ICarAppBlockingPolicySetter
        public void setAppBlockingPolicy(CarAppBlockingPolicy carAppBlockingPolicy) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarAppBlockingPolicySetter {
        private static final String DESCRIPTOR = "android.car.content.pm.ICarAppBlockingPolicySetter";
        static final int TRANSACTION_setAppBlockingPolicy = 1;

        private static class Proxy implements ICarAppBlockingPolicySetter {
            public static ICarAppBlockingPolicySetter sDefaultImpl;
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

            @Override // android.car.content.pm.ICarAppBlockingPolicySetter
            public void setAppBlockingPolicy(CarAppBlockingPolicy carAppBlockingPolicy) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (carAppBlockingPolicy != null) {
                        parcelObtain.writeInt(1);
                        carAppBlockingPolicy.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setAppBlockingPolicy(carAppBlockingPolicy);
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

        public static ICarAppBlockingPolicySetter asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarAppBlockingPolicySetter)) ? new Proxy(iBinder) : (ICarAppBlockingPolicySetter) iInterfaceQueryLocalInterface;
        }

        public static ICarAppBlockingPolicySetter getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarAppBlockingPolicySetter iCarAppBlockingPolicySetter) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarAppBlockingPolicySetter == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarAppBlockingPolicySetter;
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
            setAppBlockingPolicy(parcel.readInt() != 0 ? CarAppBlockingPolicy.CREATOR.createFromParcel(parcel) : null);
            parcel2.writeNoException();
            return true;
        }
    }

    void setAppBlockingPolicy(CarAppBlockingPolicy carAppBlockingPolicy) throws RemoteException;
}
