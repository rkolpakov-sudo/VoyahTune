package android.car;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface ICarOccupantZone extends IInterface {

    public static class Default implements ICarOccupantZone {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.ICarOccupantZone
        public boolean assignProfileUserToOccupantZone(int i, int i2) throws RemoteException {
            return false;
        }

        @Override // android.car.ICarOccupantZone
        public int[] getAllDisplaysForOccupantZone(int i) throws RemoteException {
            return null;
        }

        @Override // android.car.ICarOccupantZone
        public List<CarOccupantZoneManager.OccupantZoneInfo> getAllOccupantZones() throws RemoteException {
            return null;
        }

        @Override // android.car.ICarOccupantZone
        public int getAudioZoneIdForOccupant(int i) throws RemoteException {
            return 0;
        }

        @Override // android.car.ICarOccupantZone
        public int getDisplayForOccupant(int i, int i2) throws RemoteException {
            return 0;
        }

        @Override // android.car.ICarOccupantZone
        public int getDisplayType(int i) throws RemoteException {
            return 0;
        }

        @Override // android.car.ICarOccupantZone
        public CarOccupantZoneManager.OccupantZoneInfo getOccupantForAudioZoneId(int i) throws RemoteException {
            return null;
        }

        @Override // android.car.ICarOccupantZone
        public int getOccupantZoneIdForUserId(int i) throws RemoteException {
            return 0;
        }

        @Override // android.car.ICarOccupantZone
        public int getUserForOccupant(int i) throws RemoteException {
            return 0;
        }

        @Override // android.car.ICarOccupantZone
        public void registerCallback(ICarOccupantZoneCallback iCarOccupantZoneCallback) throws RemoteException {
        }

        @Override // android.car.ICarOccupantZone
        public void unregisterCallback(ICarOccupantZoneCallback iCarOccupantZoneCallback) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarOccupantZone {
        private static final String DESCRIPTOR = "android.car.ICarOccupantZone";
        static final int TRANSACTION_assignProfileUserToOccupantZone = 11;
        static final int TRANSACTION_getAllDisplaysForOccupantZone = 3;
        static final int TRANSACTION_getAllOccupantZones = 1;
        static final int TRANSACTION_getAudioZoneIdForOccupant = 2;
        static final int TRANSACTION_getDisplayForOccupant = 4;
        static final int TRANSACTION_getDisplayType = 6;
        static final int TRANSACTION_getOccupantForAudioZoneId = 5;
        static final int TRANSACTION_getOccupantZoneIdForUserId = 8;
        static final int TRANSACTION_getUserForOccupant = 7;
        static final int TRANSACTION_registerCallback = 9;
        static final int TRANSACTION_unregisterCallback = 10;

        private static class Proxy implements ICarOccupantZone {
            public static ICarOccupantZone sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.car.ICarOccupantZone
            public boolean assignProfileUserToOccupantZone(int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (!this.mRemote.transact(11, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().assignProfileUserToOccupantZone(i, i2);
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarOccupantZone
            public int[] getAllDisplaysForOccupantZone(int i) throws RemoteException {
                int[] iArrCreateIntArray;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(3, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        iArrCreateIntArray = parcelObtain2.createIntArray();
                    } else {
                        iArrCreateIntArray = Stub.getDefaultImpl().getAllDisplaysForOccupantZone(i);
                    }
                    return iArrCreateIntArray;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarOccupantZone
            public List<CarOccupantZoneManager.OccupantZoneInfo> getAllOccupantZones() throws RemoteException {
                List<CarOccupantZoneManager.OccupantZoneInfo> listCreateTypedArrayList;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        listCreateTypedArrayList = parcelObtain2.createTypedArrayList(CarOccupantZoneManager.OccupantZoneInfo.CREATOR);
                    } else {
                        listCreateTypedArrayList = Stub.getDefaultImpl().getAllOccupantZones();
                    }
                    return listCreateTypedArrayList;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarOccupantZone
            public int getAudioZoneIdForOccupant(int i) throws RemoteException {
                int audioZoneIdForOccupant;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(2, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        audioZoneIdForOccupant = parcelObtain2.readInt();
                    } else {
                        audioZoneIdForOccupant = Stub.getDefaultImpl().getAudioZoneIdForOccupant(i);
                    }
                    return audioZoneIdForOccupant;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarOccupantZone
            public int getDisplayForOccupant(int i, int i2) throws RemoteException {
                int displayForOccupant;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (this.mRemote.transact(4, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        displayForOccupant = parcelObtain2.readInt();
                    } else {
                        displayForOccupant = Stub.getDefaultImpl().getDisplayForOccupant(i, i2);
                    }
                    return displayForOccupant;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarOccupantZone
            public int getDisplayType(int i) throws RemoteException {
                int displayType;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(6, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        displayType = parcelObtain2.readInt();
                    } else {
                        displayType = Stub.getDefaultImpl().getDisplayType(i);
                    }
                    return displayType;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.car.ICarOccupantZone
            public CarOccupantZoneManager.OccupantZoneInfo getOccupantForAudioZoneId(int i) throws RemoteException {
                CarOccupantZoneManager.OccupantZoneInfo occupantZoneInfoCreateFromParcel;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(5, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        occupantZoneInfoCreateFromParcel = parcelObtain2.readInt() != 0 ? CarOccupantZoneManager.OccupantZoneInfo.CREATOR.createFromParcel(parcelObtain2) : null;
                    } else {
                        occupantZoneInfoCreateFromParcel = Stub.getDefaultImpl().getOccupantForAudioZoneId(i);
                    }
                    return occupantZoneInfoCreateFromParcel;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarOccupantZone
            public int getOccupantZoneIdForUserId(int i) throws RemoteException {
                int occupantZoneIdForUserId;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(8, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        occupantZoneIdForUserId = parcelObtain2.readInt();
                    } else {
                        occupantZoneIdForUserId = Stub.getDefaultImpl().getOccupantZoneIdForUserId(i);
                    }
                    return occupantZoneIdForUserId;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarOccupantZone
            public int getUserForOccupant(int i) throws RemoteException {
                int userForOccupant;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(7, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        userForOccupant = parcelObtain2.readInt();
                    } else {
                        userForOccupant = Stub.getDefaultImpl().getUserForOccupant(i);
                    }
                    return userForOccupant;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarOccupantZone
            public void registerCallback(ICarOccupantZoneCallback iCarOccupantZoneCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarOccupantZoneCallback != null ? iCarOccupantZoneCallback.asBinder() : null);
                    if (this.mRemote.transact(9, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().registerCallback(iCarOccupantZoneCallback);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarOccupantZone
            public void unregisterCallback(ICarOccupantZoneCallback iCarOccupantZoneCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarOccupantZoneCallback != null ? iCarOccupantZoneCallback.asBinder() : null);
                    if (this.mRemote.transact(10, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregisterCallback(iCarOccupantZoneCallback);
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

        public static ICarOccupantZone asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarOccupantZone)) ? new Proxy(iBinder) : (ICarOccupantZone) iInterfaceQueryLocalInterface;
        }

        public static ICarOccupantZone getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarOccupantZone iCarOccupantZone) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarOccupantZone == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarOccupantZone;
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
                    List<CarOccupantZoneManager.OccupantZoneInfo> allOccupantZones = getAllOccupantZones();
                    parcel2.writeNoException();
                    parcel2.writeTypedList(allOccupantZones);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    int audioZoneIdForOccupant = getAudioZoneIdForOccupant(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(audioZoneIdForOccupant);
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    int[] allDisplaysForOccupantZone = getAllDisplaysForOccupantZone(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeIntArray(allDisplaysForOccupantZone);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    int displayForOccupant = getDisplayForOccupant(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(displayForOccupant);
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    CarOccupantZoneManager.OccupantZoneInfo occupantForAudioZoneId = getOccupantForAudioZoneId(parcel.readInt());
                    parcel2.writeNoException();
                    if (occupantForAudioZoneId != null) {
                        parcel2.writeInt(1);
                        occupantForAudioZoneId.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    int displayType = getDisplayType(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(displayType);
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    int userForOccupant = getUserForOccupant(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(userForOccupant);
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    int occupantZoneIdForUserId = getOccupantZoneIdForUserId(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(occupantZoneIdForUserId);
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    registerCallback(ICarOccupantZoneCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterCallback(ICarOccupantZoneCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zAssignProfileUserToOccupantZone = assignProfileUserToOccupantZone(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zAssignProfileUserToOccupantZone ? 1 : 0);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    boolean assignProfileUserToOccupantZone(int i, int i2) throws RemoteException;

    int[] getAllDisplaysForOccupantZone(int i) throws RemoteException;

    List<CarOccupantZoneManager.OccupantZoneInfo> getAllOccupantZones() throws RemoteException;

    int getAudioZoneIdForOccupant(int i) throws RemoteException;

    int getDisplayForOccupant(int i, int i2) throws RemoteException;

    int getDisplayType(int i) throws RemoteException;

    CarOccupantZoneManager.OccupantZoneInfo getOccupantForAudioZoneId(int i) throws RemoteException;

    int getOccupantZoneIdForUserId(int i) throws RemoteException;

    int getUserForOccupant(int i) throws RemoteException;

    void registerCallback(ICarOccupantZoneCallback iCarOccupantZoneCallback) throws RemoteException;

    void unregisterCallback(ICarOccupantZoneCallback iCarOccupantZoneCallback) throws RemoteException;
}
