package android.car;

import android.car.user.UserCreationResult;
import android.car.user.UserIdentificationAssociationResponse;
import android.car.user.UserRemovalResult;
import android.car.user.UserSwitchResult;
import android.content.pm.UserInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.internal.infra.AndroidFuture;
import com.android.internal.os.IResultReceiver;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface ICarUserService extends IInterface {

    public static class Default implements ICarUserService {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.ICarUserService
        public AndroidFuture<UserCreationResult> createDriver(String str, boolean z) throws RemoteException {
            return null;
        }

        @Override // android.car.ICarUserService
        public UserInfo createPassenger(String str, int i) throws RemoteException {
            return null;
        }

        @Override // android.car.ICarUserService
        public void createUser(String str, String str2, int i, int i2, AndroidFuture<UserCreationResult> androidFuture) throws RemoteException {
        }

        @Override // android.car.ICarUserService
        public List<UserInfo> getAllDrivers() throws RemoteException {
            return null;
        }

        @Override // android.car.ICarUserService
        public void getInitialUserInfo(int i, int i2, IResultReceiver iResultReceiver) throws RemoteException {
        }

        @Override // android.car.ICarUserService
        public List<UserInfo> getPassengers(int i) throws RemoteException {
            return null;
        }

        @Override // android.car.ICarUserService
        public UserIdentificationAssociationResponse getUserIdentificationAssociation(int[] iArr) throws RemoteException {
            return null;
        }

        @Override // android.car.ICarUserService
        public boolean isUserHalUserAssociationSupported() throws RemoteException {
            return false;
        }

        @Override // android.car.ICarUserService
        public UserRemovalResult removeUser(int i) throws RemoteException {
            return null;
        }

        @Override // android.car.ICarUserService
        public void resetLifecycleListenerForUid() throws RemoteException {
        }

        @Override // android.car.ICarUserService
        public void setLifecycleListenerForUid(IResultReceiver iResultReceiver) throws RemoteException {
        }

        @Override // android.car.ICarUserService
        public void setUserIdentificationAssociation(int i, int[] iArr, int[] iArr2, AndroidFuture<UserIdentificationAssociationResponse> androidFuture) throws RemoteException {
        }

        @Override // android.car.ICarUserService
        public void setUserSwitchUiCallback(IResultReceiver iResultReceiver) throws RemoteException {
        }

        @Override // android.car.ICarUserService
        public boolean startPassenger(int i, int i2) throws RemoteException {
            return false;
        }

        @Override // android.car.ICarUserService
        public boolean stopPassenger(int i) throws RemoteException {
            return false;
        }

        @Override // android.car.ICarUserService
        public void switchDriver(int i, AndroidFuture<UserSwitchResult> androidFuture) throws RemoteException {
        }

        @Override // android.car.ICarUserService
        public void switchUser(int i, int i2, AndroidFuture<UserSwitchResult> androidFuture) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarUserService {
        private static final String DESCRIPTOR = "android.car.ICarUserService";
        static final int TRANSACTION_createDriver = 1;
        static final int TRANSACTION_createPassenger = 2;
        static final int TRANSACTION_createUser = 6;
        static final int TRANSACTION_getAllDrivers = 8;
        static final int TRANSACTION_getInitialUserInfo = 14;
        static final int TRANSACTION_getPassengers = 9;
        static final int TRANSACTION_getUserIdentificationAssociation = 15;
        static final int TRANSACTION_isUserHalUserAssociationSupported = 17;
        static final int TRANSACTION_removeUser = 7;
        static final int TRANSACTION_resetLifecycleListenerForUid = 13;
        static final int TRANSACTION_setLifecycleListenerForUid = 12;
        static final int TRANSACTION_setUserIdentificationAssociation = 16;
        static final int TRANSACTION_setUserSwitchUiCallback = 5;
        static final int TRANSACTION_startPassenger = 10;
        static final int TRANSACTION_stopPassenger = 11;
        static final int TRANSACTION_switchDriver = 3;
        static final int TRANSACTION_switchUser = 4;

        private static class Proxy implements ICarUserService {
            public static ICarUserService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.car.ICarUserService
            public AndroidFuture<UserCreationResult> createDriver(String str, boolean z) throws RemoteException {
                AndroidFuture<UserCreationResult> androidFutureCreateDriver;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        androidFutureCreateDriver = parcelObtain2.readInt() != 0 ? (AndroidFuture) AndroidFuture.CREATOR.createFromParcel(parcelObtain2) : null;
                    } else {
                        androidFutureCreateDriver = Stub.getDefaultImpl().createDriver(str, z);
                    }
                    return androidFutureCreateDriver;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarUserService
            public UserInfo createPassenger(String str, int i) throws RemoteException {
                UserInfo userInfoCreatePassenger;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(2, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        userInfoCreatePassenger = parcelObtain2.readInt() != 0 ? (UserInfo) UserInfo.CREATOR.createFromParcel(parcelObtain2) : null;
                    } else {
                        userInfoCreatePassenger = Stub.getDefaultImpl().createPassenger(str, i);
                    }
                    return userInfoCreatePassenger;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarUserService
            public void createUser(String str, String str2, int i, int i2, AndroidFuture<UserCreationResult> androidFuture) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (androidFuture != null) {
                        parcelObtain.writeInt(1);
                        androidFuture.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(6, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().createUser(str, str2, i, i2, androidFuture);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarUserService
            public List<UserInfo> getAllDrivers() throws RemoteException {
                List<UserInfo> listCreateTypedArrayList;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(8, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        listCreateTypedArrayList = parcelObtain2.createTypedArrayList(UserInfo.CREATOR);
                    } else {
                        listCreateTypedArrayList = Stub.getDefaultImpl().getAllDrivers();
                    }
                    return listCreateTypedArrayList;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarUserService
            public void getInitialUserInfo(int i, int i2, IResultReceiver iResultReceiver) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeStrongBinder(iResultReceiver != null ? iResultReceiver.asBinder() : null);
                    if (this.mRemote.transact(14, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().getInitialUserInfo(i, i2, iResultReceiver);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.car.ICarUserService
            public List<UserInfo> getPassengers(int i) throws RemoteException {
                List<UserInfo> listCreateTypedArrayList;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(9, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        listCreateTypedArrayList = parcelObtain2.createTypedArrayList(UserInfo.CREATOR);
                    } else {
                        listCreateTypedArrayList = Stub.getDefaultImpl().getPassengers(i);
                    }
                    return listCreateTypedArrayList;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarUserService
            public UserIdentificationAssociationResponse getUserIdentificationAssociation(int[] iArr) throws RemoteException {
                UserIdentificationAssociationResponse userIdentificationAssociationResponseCreateFromParcel;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeIntArray(iArr);
                    if (this.mRemote.transact(15, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        userIdentificationAssociationResponseCreateFromParcel = parcelObtain2.readInt() != 0 ? UserIdentificationAssociationResponse.CREATOR.createFromParcel(parcelObtain2) : null;
                    } else {
                        userIdentificationAssociationResponseCreateFromParcel = Stub.getDefaultImpl().getUserIdentificationAssociation(iArr);
                    }
                    return userIdentificationAssociationResponseCreateFromParcel;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarUserService
            public boolean isUserHalUserAssociationSupported() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(17, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isUserHalUserAssociationSupported();
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarUserService
            public UserRemovalResult removeUser(int i) throws RemoteException {
                UserRemovalResult userRemovalResultCreateFromParcel;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(7, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        userRemovalResultCreateFromParcel = parcelObtain2.readInt() != 0 ? UserRemovalResult.CREATOR.createFromParcel(parcelObtain2) : null;
                    } else {
                        userRemovalResultCreateFromParcel = Stub.getDefaultImpl().removeUser(i);
                    }
                    return userRemovalResultCreateFromParcel;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarUserService
            public void resetLifecycleListenerForUid() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(13, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().resetLifecycleListenerForUid();
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarUserService
            public void setLifecycleListenerForUid(IResultReceiver iResultReceiver) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iResultReceiver != null ? iResultReceiver.asBinder() : null);
                    if (this.mRemote.transact(12, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setLifecycleListenerForUid(iResultReceiver);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarUserService
            public void setUserIdentificationAssociation(int i, int[] iArr, int[] iArr2, AndroidFuture<UserIdentificationAssociationResponse> androidFuture) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeIntArray(iArr);
                    parcelObtain.writeIntArray(iArr2);
                    if (androidFuture != null) {
                        parcelObtain.writeInt(1);
                        androidFuture.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(16, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setUserIdentificationAssociation(i, iArr, iArr2, androidFuture);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarUserService
            public void setUserSwitchUiCallback(IResultReceiver iResultReceiver) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iResultReceiver != null ? iResultReceiver.asBinder() : null);
                    if (this.mRemote.transact(5, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setUserSwitchUiCallback(iResultReceiver);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarUserService
            public boolean startPassenger(int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (!this.mRemote.transact(10, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().startPassenger(i, i2);
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarUserService
            public boolean stopPassenger(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (!this.mRemote.transact(11, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().stopPassenger(i);
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarUserService
            public void switchDriver(int i, AndroidFuture<UserSwitchResult> androidFuture) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (androidFuture != null) {
                        parcelObtain.writeInt(1);
                        androidFuture.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(3, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().switchDriver(i, androidFuture);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.ICarUserService
            public void switchUser(int i, int i2, AndroidFuture<UserSwitchResult> androidFuture) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (androidFuture != null) {
                        parcelObtain.writeInt(1);
                        androidFuture.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(4, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().switchUser(i, i2, androidFuture);
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

        public static ICarUserService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarUserService)) ? new Proxy(iBinder) : (ICarUserService) iInterfaceQueryLocalInterface;
        }

        public static ICarUserService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarUserService iCarUserService) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarUserService == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarUserService;
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
                    AndroidFuture<UserCreationResult> androidFutureCreateDriver = createDriver(parcel.readString(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    if (androidFutureCreateDriver != null) {
                        parcel2.writeInt(1);
                        androidFutureCreateDriver.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    UserInfo userInfoCreatePassenger = createPassenger(parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    if (userInfoCreatePassenger != null) {
                        parcel2.writeInt(1);
                        userInfoCreatePassenger.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    switchDriver(parcel.readInt(), parcel.readInt() != 0 ? (AndroidFuture) AndroidFuture.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    switchUser(parcel.readInt(), parcel.readInt(), parcel.readInt() != 0 ? (AndroidFuture) AndroidFuture.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    setUserSwitchUiCallback(IResultReceiver.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    createUser(parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt() != 0 ? (AndroidFuture) AndroidFuture.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    UserRemovalResult userRemovalResultRemoveUser = removeUser(parcel.readInt());
                    parcel2.writeNoException();
                    if (userRemovalResultRemoveUser != null) {
                        parcel2.writeInt(1);
                        userRemovalResultRemoveUser.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    List<UserInfo> allDrivers = getAllDrivers();
                    parcel2.writeNoException();
                    parcel2.writeTypedList(allDrivers);
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    List<UserInfo> passengers = getPassengers(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeTypedList(passengers);
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zStartPassenger = startPassenger(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zStartPassenger ? 1 : 0);
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zStopPassenger = stopPassenger(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zStopPassenger ? 1 : 0);
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    setLifecycleListenerForUid(IResultReceiver.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 13:
                    parcel.enforceInterface(DESCRIPTOR);
                    resetLifecycleListenerForUid();
                    parcel2.writeNoException();
                    return true;
                case 14:
                    parcel.enforceInterface(DESCRIPTOR);
                    getInitialUserInfo(parcel.readInt(), parcel.readInt(), IResultReceiver.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 15:
                    parcel.enforceInterface(DESCRIPTOR);
                    UserIdentificationAssociationResponse userIdentificationAssociation = getUserIdentificationAssociation(parcel.createIntArray());
                    parcel2.writeNoException();
                    if (userIdentificationAssociation != null) {
                        parcel2.writeInt(1);
                        userIdentificationAssociation.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 16:
                    parcel.enforceInterface(DESCRIPTOR);
                    setUserIdentificationAssociation(parcel.readInt(), parcel.createIntArray(), parcel.createIntArray(), parcel.readInt() != 0 ? (AndroidFuture) AndroidFuture.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 17:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsUserHalUserAssociationSupported = isUserHalUserAssociationSupported();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsUserHalUserAssociationSupported ? 1 : 0);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    AndroidFuture<UserCreationResult> createDriver(String str, boolean z) throws RemoteException;

    UserInfo createPassenger(String str, int i) throws RemoteException;

    void createUser(String str, String str2, int i, int i2, AndroidFuture<UserCreationResult> androidFuture) throws RemoteException;

    List<UserInfo> getAllDrivers() throws RemoteException;

    void getInitialUserInfo(int i, int i2, IResultReceiver iResultReceiver) throws RemoteException;

    List<UserInfo> getPassengers(int i) throws RemoteException;

    UserIdentificationAssociationResponse getUserIdentificationAssociation(int[] iArr) throws RemoteException;

    boolean isUserHalUserAssociationSupported() throws RemoteException;

    UserRemovalResult removeUser(int i) throws RemoteException;

    void resetLifecycleListenerForUid() throws RemoteException;

    void setLifecycleListenerForUid(IResultReceiver iResultReceiver) throws RemoteException;

    void setUserIdentificationAssociation(int i, int[] iArr, int[] iArr2, AndroidFuture<UserIdentificationAssociationResponse> androidFuture) throws RemoteException;

    void setUserSwitchUiCallback(IResultReceiver iResultReceiver) throws RemoteException;

    boolean startPassenger(int i, int i2) throws RemoteException;

    boolean stopPassenger(int i) throws RemoteException;

    void switchDriver(int i, AndroidFuture<UserSwitchResult> androidFuture) throws RemoteException;

    void switchUser(int i, int i2, AndroidFuture<UserSwitchResult> androidFuture) throws RemoteException;
}
