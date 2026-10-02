package android.car.drivingstate;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface ICarDrivingState extends IInterface {

    public static class Default implements ICarDrivingState {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.drivingstate.ICarDrivingState
        public CarDrivingStateEvent getCurrentDrivingState() throws RemoteException {
            return null;
        }

        @Override // android.car.drivingstate.ICarDrivingState
        public void injectDrivingState(CarDrivingStateEvent carDrivingStateEvent) throws RemoteException {
        }

        @Override // android.car.drivingstate.ICarDrivingState
        public void registerDrivingStateChangeListener(ICarDrivingStateChangeListener iCarDrivingStateChangeListener) throws RemoteException {
        }

        @Override // android.car.drivingstate.ICarDrivingState
        public void unregisterDrivingStateChangeListener(ICarDrivingStateChangeListener iCarDrivingStateChangeListener) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarDrivingState {
        private static final String DESCRIPTOR = "android.car.drivingstate.ICarDrivingState";
        static final int TRANSACTION_getCurrentDrivingState = 3;
        static final int TRANSACTION_injectDrivingState = 4;
        static final int TRANSACTION_registerDrivingStateChangeListener = 1;
        static final int TRANSACTION_unregisterDrivingStateChangeListener = 2;

        private static class Proxy implements ICarDrivingState {
            public static ICarDrivingState sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.car.drivingstate.ICarDrivingState
            public CarDrivingStateEvent getCurrentDrivingState() throws RemoteException {
                CarDrivingStateEvent carDrivingStateEventCreateFromParcel;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(3, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        carDrivingStateEventCreateFromParcel = parcelObtain2.readInt() != 0 ? CarDrivingStateEvent.CREATOR.createFromParcel(parcelObtain2) : null;
                    } else {
                        carDrivingStateEventCreateFromParcel = Stub.getDefaultImpl().getCurrentDrivingState();
                    }
                    return carDrivingStateEventCreateFromParcel;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.car.drivingstate.ICarDrivingState
            public void injectDrivingState(CarDrivingStateEvent carDrivingStateEvent) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (carDrivingStateEvent != null) {
                        parcelObtain.writeInt(1);
                        carDrivingStateEvent.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(4, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().injectDrivingState(carDrivingStateEvent);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.drivingstate.ICarDrivingState
            public void registerDrivingStateChangeListener(ICarDrivingStateChangeListener iCarDrivingStateChangeListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarDrivingStateChangeListener != null ? iCarDrivingStateChangeListener.asBinder() : null);
                    if (this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().registerDrivingStateChangeListener(iCarDrivingStateChangeListener);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.drivingstate.ICarDrivingState
            public void unregisterDrivingStateChangeListener(ICarDrivingStateChangeListener iCarDrivingStateChangeListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarDrivingStateChangeListener != null ? iCarDrivingStateChangeListener.asBinder() : null);
                    if (this.mRemote.transact(2, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregisterDrivingStateChangeListener(iCarDrivingStateChangeListener);
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

        public static ICarDrivingState asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarDrivingState)) ? new Proxy(iBinder) : (ICarDrivingState) iInterfaceQueryLocalInterface;
        }

        public static ICarDrivingState getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarDrivingState iCarDrivingState) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarDrivingState == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarDrivingState;
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
                registerDrivingStateChangeListener(ICarDrivingStateChangeListener.Stub.asInterface(parcel.readStrongBinder()));
            } else if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                unregisterDrivingStateChangeListener(ICarDrivingStateChangeListener.Stub.asInterface(parcel.readStrongBinder()));
            } else {
                if (i == 3) {
                    parcel.enforceInterface(DESCRIPTOR);
                    CarDrivingStateEvent currentDrivingState = getCurrentDrivingState();
                    parcel2.writeNoException();
                    if (currentDrivingState != null) {
                        parcel2.writeInt(1);
                        currentDrivingState.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                }
                if (i != 4) {
                    if (i != 1598968902) {
                        return super.onTransact(i, parcel, parcel2, i2);
                    }
                    parcel2.writeString(DESCRIPTOR);
                    return true;
                }
                parcel.enforceInterface(DESCRIPTOR);
                injectDrivingState(parcel.readInt() != 0 ? CarDrivingStateEvent.CREATOR.createFromParcel(parcel) : null);
            }
            parcel2.writeNoException();
            return true;
        }
    }

    CarDrivingStateEvent getCurrentDrivingState() throws RemoteException;

    void injectDrivingState(CarDrivingStateEvent carDrivingStateEvent) throws RemoteException;

    void registerDrivingStateChangeListener(ICarDrivingStateChangeListener iCarDrivingStateChangeListener) throws RemoteException;

    void unregisterDrivingStateChangeListener(ICarDrivingStateChangeListener iCarDrivingStateChangeListener) throws RemoteException;
}
