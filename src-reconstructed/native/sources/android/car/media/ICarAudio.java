package android.car.media;

import android.media.AudioDeviceAttributes;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface ICarAudio extends IInterface {

    public static class Default implements ICarAudio {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.media.ICarAudio
        public boolean clearZoneIdForUid(int i) throws RemoteException {
            return false;
        }

        @Override // android.car.media.ICarAudio
        public CarAudioPatchHandle createAudioPatch(String str, int i, int i2) throws RemoteException {
            return null;
        }

        @Override // android.car.media.ICarAudio
        public int[] getAudioZoneIds() throws RemoteException {
            return null;
        }

        @Override // android.car.media.ICarAudio
        public String[] getExternalSources() throws RemoteException {
            return null;
        }

        @Override // android.car.media.ICarAudio
        public int getGroupMaxVolume(int i, int i2) throws RemoteException {
            return 0;
        }

        @Override // android.car.media.ICarAudio
        public int getGroupMinVolume(int i, int i2) throws RemoteException {
            return 0;
        }

        @Override // android.car.media.ICarAudio
        public int getGroupVolume(int i, int i2) throws RemoteException {
            return 0;
        }

        @Override // android.car.media.ICarAudio
        public List<AudioDeviceAttributes> getInputDevicesForZoneId(int i) throws RemoteException {
            return null;
        }

        @Override // android.car.media.ICarAudio
        public String getOutputDeviceAddressForUsage(int i, int i2) throws RemoteException {
            return null;
        }

        @Override // android.car.media.ICarAudio
        public int[] getUsagesForVolumeGroupId(int i, int i2) throws RemoteException {
            return null;
        }

        @Override // android.car.media.ICarAudio
        public int getVolumeGroupCount(int i) throws RemoteException {
            return 0;
        }

        @Override // android.car.media.ICarAudio
        public int getVolumeGroupIdForUsage(int i, int i2) throws RemoteException {
            return 0;
        }

        @Override // android.car.media.ICarAudio
        public int getZoneIdForUid(int i) throws RemoteException {
            return 0;
        }

        @Override // android.car.media.ICarAudio
        public boolean isDynamicRoutingEnabled() throws RemoteException {
            return false;
        }

        @Override // android.car.media.ICarAudio
        public void registerVolumeCallback(IBinder iBinder) throws RemoteException {
        }

        @Override // android.car.media.ICarAudio
        public void releaseAudioPatch(CarAudioPatchHandle carAudioPatchHandle) throws RemoteException {
        }

        @Override // android.car.media.ICarAudio
        public void setBalanceTowardRight(float f) throws RemoteException {
        }

        @Override // android.car.media.ICarAudio
        public void setFadeTowardFront(float f) throws RemoteException {
        }

        @Override // android.car.media.ICarAudio
        public void setGroupVolume(int i, int i2, int i3, int i4) throws RemoteException {
        }

        @Override // android.car.media.ICarAudio
        public boolean setZoneIdForUid(int i, int i2) throws RemoteException {
            return false;
        }

        @Override // android.car.media.ICarAudio
        public void unregisterVolumeCallback(IBinder iBinder) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarAudio {
        private static final String DESCRIPTOR = "android.car.media.ICarAudio";
        static final int TRANSACTION_clearZoneIdForUid = 17;
        static final int TRANSACTION_createAudioPatch = 9;
        static final int TRANSACTION_getAudioZoneIds = 14;
        static final int TRANSACTION_getExternalSources = 8;
        static final int TRANSACTION_getGroupMaxVolume = 3;
        static final int TRANSACTION_getGroupMinVolume = 4;
        static final int TRANSACTION_getGroupVolume = 5;
        static final int TRANSACTION_getInputDevicesForZoneId = 19;
        static final int TRANSACTION_getOutputDeviceAddressForUsage = 18;
        static final int TRANSACTION_getUsagesForVolumeGroupId = 13;
        static final int TRANSACTION_getVolumeGroupCount = 11;
        static final int TRANSACTION_getVolumeGroupIdForUsage = 12;
        static final int TRANSACTION_getZoneIdForUid = 15;
        static final int TRANSACTION_isDynamicRoutingEnabled = 1;
        static final int TRANSACTION_registerVolumeCallback = 20;
        static final int TRANSACTION_releaseAudioPatch = 10;
        static final int TRANSACTION_setBalanceTowardRight = 7;
        static final int TRANSACTION_setFadeTowardFront = 6;
        static final int TRANSACTION_setGroupVolume = 2;
        static final int TRANSACTION_setZoneIdForUid = 16;
        static final int TRANSACTION_unregisterVolumeCallback = 21;

        private static class Proxy implements ICarAudio {
            public static ICarAudio sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.car.media.ICarAudio
            public boolean clearZoneIdForUid(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (!this.mRemote.transact(17, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().clearZoneIdForUid(i);
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarAudio
            public CarAudioPatchHandle createAudioPatch(String str, int i, int i2) throws RemoteException {
                CarAudioPatchHandle carAudioPatchHandleCreateFromParcel;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (this.mRemote.transact(9, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        carAudioPatchHandleCreateFromParcel = parcelObtain2.readInt() != 0 ? CarAudioPatchHandle.CREATOR.createFromParcel(parcelObtain2) : null;
                    } else {
                        carAudioPatchHandleCreateFromParcel = Stub.getDefaultImpl().createAudioPatch(str, i, i2);
                    }
                    return carAudioPatchHandleCreateFromParcel;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarAudio
            public int[] getAudioZoneIds() throws RemoteException {
                int[] iArrCreateIntArray;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(14, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        iArrCreateIntArray = parcelObtain2.createIntArray();
                    } else {
                        iArrCreateIntArray = Stub.getDefaultImpl().getAudioZoneIds();
                    }
                    return iArrCreateIntArray;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarAudio
            public String[] getExternalSources() throws RemoteException {
                String[] strArrCreateStringArray;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(8, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        strArrCreateStringArray = parcelObtain2.createStringArray();
                    } else {
                        strArrCreateStringArray = Stub.getDefaultImpl().getExternalSources();
                    }
                    return strArrCreateStringArray;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarAudio
            public int getGroupMaxVolume(int i, int i2) throws RemoteException {
                int groupMaxVolume;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (this.mRemote.transact(3, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        groupMaxVolume = parcelObtain2.readInt();
                    } else {
                        groupMaxVolume = Stub.getDefaultImpl().getGroupMaxVolume(i, i2);
                    }
                    return groupMaxVolume;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarAudio
            public int getGroupMinVolume(int i, int i2) throws RemoteException {
                int groupMinVolume;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (this.mRemote.transact(4, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        groupMinVolume = parcelObtain2.readInt();
                    } else {
                        groupMinVolume = Stub.getDefaultImpl().getGroupMinVolume(i, i2);
                    }
                    return groupMinVolume;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarAudio
            public int getGroupVolume(int i, int i2) throws RemoteException {
                int groupVolume;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (this.mRemote.transact(5, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        groupVolume = parcelObtain2.readInt();
                    } else {
                        groupVolume = Stub.getDefaultImpl().getGroupVolume(i, i2);
                    }
                    return groupVolume;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarAudio
            public List<AudioDeviceAttributes> getInputDevicesForZoneId(int i) throws RemoteException {
                List<AudioDeviceAttributes> listCreateTypedArrayList;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(19, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        listCreateTypedArrayList = parcelObtain2.createTypedArrayList(AudioDeviceAttributes.CREATOR);
                    } else {
                        listCreateTypedArrayList = Stub.getDefaultImpl().getInputDevicesForZoneId(i);
                    }
                    return listCreateTypedArrayList;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.car.media.ICarAudio
            public String getOutputDeviceAddressForUsage(int i, int i2) throws RemoteException {
                String string;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (this.mRemote.transact(18, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        string = parcelObtain2.readString();
                    } else {
                        string = Stub.getDefaultImpl().getOutputDeviceAddressForUsage(i, i2);
                    }
                    return string;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarAudio
            public int[] getUsagesForVolumeGroupId(int i, int i2) throws RemoteException {
                int[] iArrCreateIntArray;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (this.mRemote.transact(13, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        iArrCreateIntArray = parcelObtain2.createIntArray();
                    } else {
                        iArrCreateIntArray = Stub.getDefaultImpl().getUsagesForVolumeGroupId(i, i2);
                    }
                    return iArrCreateIntArray;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarAudio
            public int getVolumeGroupCount(int i) throws RemoteException {
                int volumeGroupCount;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(11, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        volumeGroupCount = parcelObtain2.readInt();
                    } else {
                        volumeGroupCount = Stub.getDefaultImpl().getVolumeGroupCount(i);
                    }
                    return volumeGroupCount;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarAudio
            public int getVolumeGroupIdForUsage(int i, int i2) throws RemoteException {
                int volumeGroupIdForUsage;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (this.mRemote.transact(12, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        volumeGroupIdForUsage = parcelObtain2.readInt();
                    } else {
                        volumeGroupIdForUsage = Stub.getDefaultImpl().getVolumeGroupIdForUsage(i, i2);
                    }
                    return volumeGroupIdForUsage;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarAudio
            public int getZoneIdForUid(int i) throws RemoteException {
                int zoneIdForUid;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(15, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        zoneIdForUid = parcelObtain2.readInt();
                    } else {
                        zoneIdForUid = Stub.getDefaultImpl().getZoneIdForUid(i);
                    }
                    return zoneIdForUid;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarAudio
            public boolean isDynamicRoutingEnabled() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isDynamicRoutingEnabled();
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarAudio
            public void registerVolumeCallback(IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    if (this.mRemote.transact(20, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().registerVolumeCallback(iBinder);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarAudio
            public void releaseAudioPatch(CarAudioPatchHandle carAudioPatchHandle) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (carAudioPatchHandle != null) {
                        parcelObtain.writeInt(1);
                        carAudioPatchHandle.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(10, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().releaseAudioPatch(carAudioPatchHandle);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarAudio
            public void setBalanceTowardRight(float f) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeFloat(f);
                    if (this.mRemote.transact(7, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setBalanceTowardRight(f);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarAudio
            public void setFadeTowardFront(float f) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeFloat(f);
                    if (this.mRemote.transact(6, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setFadeTowardFront(f);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarAudio
            public void setGroupVolume(int i, int i2, int i3, int i4) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    parcelObtain.writeInt(i4);
                    if (this.mRemote.transact(2, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setGroupVolume(i, i2, i3, i4);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarAudio
            public boolean setZoneIdForUid(int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    if (!this.mRemote.transact(16, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().setZoneIdForUid(i, i2);
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarAudio
            public void unregisterVolumeCallback(IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    if (this.mRemote.transact(21, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregisterVolumeCallback(iBinder);
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

        public static ICarAudio asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarAudio)) ? new Proxy(iBinder) : (ICarAudio) iInterfaceQueryLocalInterface;
        }

        public static ICarAudio getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarAudio iCarAudio) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarAudio == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarAudio;
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
                    boolean zIsDynamicRoutingEnabled = isDynamicRoutingEnabled();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsDynamicRoutingEnabled ? 1 : 0);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    setGroupVolume(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    int groupMaxVolume = getGroupMaxVolume(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(groupMaxVolume);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    int groupMinVolume = getGroupMinVolume(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(groupMinVolume);
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    int groupVolume = getGroupVolume(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(groupVolume);
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    setFadeTowardFront(parcel.readFloat());
                    parcel2.writeNoException();
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    setBalanceTowardRight(parcel.readFloat());
                    parcel2.writeNoException();
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    String[] externalSources = getExternalSources();
                    parcel2.writeNoException();
                    parcel2.writeStringArray(externalSources);
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    CarAudioPatchHandle carAudioPatchHandleCreateAudioPatch = createAudioPatch(parcel.readString(), parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    if (carAudioPatchHandleCreateAudioPatch != null) {
                        parcel2.writeInt(1);
                        carAudioPatchHandleCreateAudioPatch.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    releaseAudioPatch(parcel.readInt() != 0 ? CarAudioPatchHandle.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    int volumeGroupCount = getVolumeGroupCount(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(volumeGroupCount);
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    int volumeGroupIdForUsage = getVolumeGroupIdForUsage(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(volumeGroupIdForUsage);
                    return true;
                case 13:
                    parcel.enforceInterface(DESCRIPTOR);
                    int[] usagesForVolumeGroupId = getUsagesForVolumeGroupId(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeIntArray(usagesForVolumeGroupId);
                    return true;
                case 14:
                    parcel.enforceInterface(DESCRIPTOR);
                    int[] audioZoneIds = getAudioZoneIds();
                    parcel2.writeNoException();
                    parcel2.writeIntArray(audioZoneIds);
                    return true;
                case 15:
                    parcel.enforceInterface(DESCRIPTOR);
                    int zoneIdForUid = getZoneIdForUid(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zoneIdForUid);
                    return true;
                case 16:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zoneIdForUid2 = setZoneIdForUid(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zoneIdForUid2 ? 1 : 0);
                    return true;
                case 17:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zClearZoneIdForUid = clearZoneIdForUid(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zClearZoneIdForUid ? 1 : 0);
                    return true;
                case 18:
                    parcel.enforceInterface(DESCRIPTOR);
                    String outputDeviceAddressForUsage = getOutputDeviceAddressForUsage(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeString(outputDeviceAddressForUsage);
                    return true;
                case 19:
                    parcel.enforceInterface(DESCRIPTOR);
                    List<AudioDeviceAttributes> inputDevicesForZoneId = getInputDevicesForZoneId(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeTypedList(inputDevicesForZoneId);
                    return true;
                case 20:
                    parcel.enforceInterface(DESCRIPTOR);
                    registerVolumeCallback(parcel.readStrongBinder());
                    parcel2.writeNoException();
                    return true;
                case 21:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterVolumeCallback(parcel.readStrongBinder());
                    parcel2.writeNoException();
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    boolean clearZoneIdForUid(int i) throws RemoteException;

    CarAudioPatchHandle createAudioPatch(String str, int i, int i2) throws RemoteException;

    int[] getAudioZoneIds() throws RemoteException;

    String[] getExternalSources() throws RemoteException;

    int getGroupMaxVolume(int i, int i2) throws RemoteException;

    int getGroupMinVolume(int i, int i2) throws RemoteException;

    int getGroupVolume(int i, int i2) throws RemoteException;

    List<AudioDeviceAttributes> getInputDevicesForZoneId(int i) throws RemoteException;

    String getOutputDeviceAddressForUsage(int i, int i2) throws RemoteException;

    int[] getUsagesForVolumeGroupId(int i, int i2) throws RemoteException;

    int getVolumeGroupCount(int i) throws RemoteException;

    int getVolumeGroupIdForUsage(int i, int i2) throws RemoteException;

    int getZoneIdForUid(int i) throws RemoteException;

    boolean isDynamicRoutingEnabled() throws RemoteException;

    void registerVolumeCallback(IBinder iBinder) throws RemoteException;

    void releaseAudioPatch(CarAudioPatchHandle carAudioPatchHandle) throws RemoteException;

    void setBalanceTowardRight(float f) throws RemoteException;

    void setFadeTowardFront(float f) throws RemoteException;

    void setGroupVolume(int i, int i2, int i3, int i4) throws RemoteException;

    boolean setZoneIdForUid(int i, int i2) throws RemoteException;

    void unregisterVolumeCallback(IBinder iBinder) throws RemoteException;
}
