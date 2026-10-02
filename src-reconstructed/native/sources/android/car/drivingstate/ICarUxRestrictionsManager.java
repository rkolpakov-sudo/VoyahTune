package android.car.drivingstate;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.IRemoteCallback;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface ICarUxRestrictionsManager extends IInterface {

    public static class Default implements ICarUxRestrictionsManager {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.drivingstate.ICarUxRestrictionsManager
        public List<CarUxRestrictionsConfiguration> getConfigs() throws RemoteException {
            return null;
        }

        @Override // android.car.drivingstate.ICarUxRestrictionsManager
        public CarUxRestrictions getCurrentUxRestrictions(int i) throws RemoteException {
            return null;
        }

        @Override // android.car.drivingstate.ICarUxRestrictionsManager
        public int getMappedPhysicalDisplayOfVirtualDisplay(int i) throws RemoteException {
            return 0;
        }

        @Override // android.car.drivingstate.ICarUxRestrictionsManager
        public String getRestrictionMode() throws RemoteException {
            return null;
        }

        @Override // android.car.drivingstate.ICarUxRestrictionsManager
        public List<CarUxRestrictionsConfiguration> getStagedConfigs() throws RemoteException {
            return null;
        }

        @Override // android.car.drivingstate.ICarUxRestrictionsManager
        public void registerUxRestrictionsChangeListener(ICarUxRestrictionsChangeListener iCarUxRestrictionsChangeListener, int i) throws RemoteException {
        }

        @Override // android.car.drivingstate.ICarUxRestrictionsManager
        public void reportVirtualDisplayToPhysicalDisplay(IRemoteCallback iRemoteCallback, int i, int i2) throws RemoteException {
        }

        @Override // android.car.drivingstate.ICarUxRestrictionsManager
        public boolean saveUxRestrictionsConfigurationForNextBoot(List<CarUxRestrictionsConfiguration> list) throws RemoteException {
            return false;
        }

        @Override // android.car.drivingstate.ICarUxRestrictionsManager
        public boolean setRestrictionMode(String str) throws RemoteException {
            return false;
        }

        @Override // android.car.drivingstate.ICarUxRestrictionsManager
        public void unregisterUxRestrictionsChangeListener(ICarUxRestrictionsChangeListener iCarUxRestrictionsChangeListener) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarUxRestrictionsManager {
        private static final String DESCRIPTOR = "android.car.drivingstate.ICarUxRestrictionsManager";
        static final int TRANSACTION_getConfigs = 6;
        static final int TRANSACTION_getCurrentUxRestrictions = 3;
        static final int TRANSACTION_getMappedPhysicalDisplayOfVirtualDisplay = 10;
        static final int TRANSACTION_getRestrictionMode = 12;
        static final int TRANSACTION_getStagedConfigs = 5;
        static final int TRANSACTION_registerUxRestrictionsChangeListener = 1;
        static final int TRANSACTION_reportVirtualDisplayToPhysicalDisplay = 9;
        static final int TRANSACTION_saveUxRestrictionsConfigurationForNextBoot = 4;
        static final int TRANSACTION_setRestrictionMode = 11;
        static final int TRANSACTION_unregisterUxRestrictionsChangeListener = 2;

        private static class Proxy implements ICarUxRestrictionsManager {
            public static ICarUxRestrictionsManager sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.car.drivingstate.ICarUxRestrictionsManager
            public List<CarUxRestrictionsConfiguration> getConfigs() throws RemoteException {
                List<CarUxRestrictionsConfiguration> listCreateTypedArrayList;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(6, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        listCreateTypedArrayList = parcelObtain2.createTypedArrayList(CarUxRestrictionsConfiguration.CREATOR);
                    } else {
                        listCreateTypedArrayList = Stub.getDefaultImpl().getConfigs();
                    }
                    return listCreateTypedArrayList;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.drivingstate.ICarUxRestrictionsManager
            public CarUxRestrictions getCurrentUxRestrictions(int i) throws RemoteException {
                CarUxRestrictions carUxRestrictionsCreateFromParcel;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(3, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        carUxRestrictionsCreateFromParcel = parcelObtain2.readInt() != 0 ? CarUxRestrictions.CREATOR.createFromParcel(parcelObtain2) : null;
                    } else {
                        carUxRestrictionsCreateFromParcel = Stub.getDefaultImpl().getCurrentUxRestrictions(i);
                    }
                    return carUxRestrictionsCreateFromParcel;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.car.drivingstate.ICarUxRestrictionsManager
            public int getMappedPhysicalDisplayOfVirtualDisplay(int i) throws RemoteException {
                int mappedPhysicalDisplayOfVirtualDisplay;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(10, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        mappedPhysicalDisplayOfVirtualDisplay = parcelObtain2.readInt();
                    } else {
                        mappedPhysicalDisplayOfVirtualDisplay = Stub.getDefaultImpl().getMappedPhysicalDisplayOfVirtualDisplay(i);
                    }
                    return mappedPhysicalDisplayOfVirtualDisplay;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.drivingstate.ICarUxRestrictionsManager
            public String getRestrictionMode() throws RemoteException {
                String string;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(12, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        string = parcelObtain2.readString();
                    } else {
                        string = Stub.getDefaultImpl().getRestrictionMode();
                    }
                    return string;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.drivingstate.ICarUxRestrictionsManager
            public List<CarUxRestrictionsConfiguration> getStagedConfigs() throws RemoteException {
                List<CarUxRestrictionsConfiguration> listCreateTypedArrayList;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(5, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        listCreateTypedArrayList = parcelObtain2.createTypedArrayList(CarUxRestrictionsConfiguration.CREATOR);
                    } else {
                        listCreateTypedArrayList = Stub.getDefaultImpl().getStagedConfigs();
                    }
                    return listCreateTypedArrayList;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.drivingstate.ICarUxRestrictionsManager
            public void registerUxRestrictionsChangeListener(ICarUxRestrictionsChangeListener iCarUxRestrictionsChangeListener, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarUxRestrictionsChangeListener != null ? iCarUxRestrictionsChangeListener.asBinder() : null);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().registerUxRestrictionsChangeListener(iCarUxRestrictionsChangeListener, i);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.drivingstate.ICarUxRestrictionsManager
            public void reportVirtualDisplayToPhysicalDisplay(IRemoteCallback iRemoteCallback, int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iRemoteCallback != null ? iRemoteCallback.asBinder() : null);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (this.mRemote.transact(9, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().reportVirtualDisplayToPhysicalDisplay(iRemoteCallback, i, i2);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.drivingstate.ICarUxRestrictionsManager
            public boolean saveUxRestrictionsConfigurationForNextBoot(List<CarUxRestrictionsConfiguration> list) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeTypedList(list);
                    if (!this.mRemote.transact(4, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().saveUxRestrictionsConfigurationForNextBoot(list);
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.drivingstate.ICarUxRestrictionsManager
            public boolean setRestrictionMode(String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    if (!this.mRemote.transact(11, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().setRestrictionMode(str);
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.drivingstate.ICarUxRestrictionsManager
            public void unregisterUxRestrictionsChangeListener(ICarUxRestrictionsChangeListener iCarUxRestrictionsChangeListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarUxRestrictionsChangeListener != null ? iCarUxRestrictionsChangeListener.asBinder() : null);
                    if (this.mRemote.transact(2, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregisterUxRestrictionsChangeListener(iCarUxRestrictionsChangeListener);
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

        public static ICarUxRestrictionsManager asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarUxRestrictionsManager)) ? new Proxy(iBinder) : (ICarUxRestrictionsManager) iInterfaceQueryLocalInterface;
        }

        public static ICarUxRestrictionsManager getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarUxRestrictionsManager iCarUxRestrictionsManager) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarUxRestrictionsManager == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarUxRestrictionsManager;
            return true;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    registerUxRestrictionsChangeListener(ICarUxRestrictionsChangeListener.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterUxRestrictionsChangeListener(ICarUxRestrictionsChangeListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    CarUxRestrictions currentUxRestrictions = getCurrentUxRestrictions(parcel.readInt());
                    parcel2.writeNoException();
                    if (currentUxRestrictions != null) {
                        parcel2.writeInt(1);
                        currentUxRestrictions.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zSaveUxRestrictionsConfigurationForNextBoot = saveUxRestrictionsConfigurationForNextBoot(parcel.createTypedArrayList(CarUxRestrictionsConfiguration.CREATOR));
                    parcel2.writeNoException();
                    parcel2.writeInt(zSaveUxRestrictionsConfigurationForNextBoot ? 1 : 0);
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    List<CarUxRestrictionsConfiguration> stagedConfigs = getStagedConfigs();
                    parcel2.writeNoException();
                    parcel2.writeTypedList(stagedConfigs);
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    List<CarUxRestrictionsConfiguration> configs = getConfigs();
                    parcel2.writeNoException();
                    parcel2.writeTypedList(configs);
                    return true;
                default:
                    switch (i) {
                        case 9:
                            parcel.enforceInterface(DESCRIPTOR);
                            reportVirtualDisplayToPhysicalDisplay(IRemoteCallback.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt(), parcel.readInt());
                            parcel2.writeNoException();
                            return true;
                        case 10:
                            parcel.enforceInterface(DESCRIPTOR);
                            int mappedPhysicalDisplayOfVirtualDisplay = getMappedPhysicalDisplayOfVirtualDisplay(parcel.readInt());
                            parcel2.writeNoException();
                            parcel2.writeInt(mappedPhysicalDisplayOfVirtualDisplay);
                            return true;
                        case 11:
                            parcel.enforceInterface(DESCRIPTOR);
                            boolean restrictionMode = setRestrictionMode(parcel.readString());
                            parcel2.writeNoException();
                            parcel2.writeInt(restrictionMode ? 1 : 0);
                            return true;
                        case 12:
                            parcel.enforceInterface(DESCRIPTOR);
                            String restrictionMode2 = getRestrictionMode();
                            parcel2.writeNoException();
                            parcel2.writeString(restrictionMode2);
                            return true;
                        default:
                            return super.onTransact(i, parcel, parcel2, i2);
                    }
            }
        }
    }

    List<CarUxRestrictionsConfiguration> getConfigs() throws RemoteException;

    CarUxRestrictions getCurrentUxRestrictions(int i) throws RemoteException;

    int getMappedPhysicalDisplayOfVirtualDisplay(int i) throws RemoteException;

    String getRestrictionMode() throws RemoteException;

    List<CarUxRestrictionsConfiguration> getStagedConfigs() throws RemoteException;

    void registerUxRestrictionsChangeListener(ICarUxRestrictionsChangeListener iCarUxRestrictionsChangeListener, int i) throws RemoteException;

    void reportVirtualDisplayToPhysicalDisplay(IRemoteCallback iRemoteCallback, int i, int i2) throws RemoteException;

    boolean saveUxRestrictionsConfigurationForNextBoot(List<CarUxRestrictionsConfiguration> list) throws RemoteException;

    boolean setRestrictionMode(String str) throws RemoteException;

    void unregisterUxRestrictionsChangeListener(ICarUxRestrictionsChangeListener iCarUxRestrictionsChangeListener) throws RemoteException;
}
