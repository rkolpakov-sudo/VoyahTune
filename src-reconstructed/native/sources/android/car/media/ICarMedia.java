package android.car.media;

import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface ICarMedia extends IInterface {

    public static class Default implements ICarMedia {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.media.ICarMedia
        public List<ComponentName> getLastMediaSources(int i) throws RemoteException {
            return null;
        }

        @Override // android.car.media.ICarMedia
        public ComponentName getMediaSource(int i) throws RemoteException {
            return null;
        }

        @Override // android.car.media.ICarMedia
        public boolean isIndependentPlaybackConfig() throws RemoteException {
            return false;
        }

        @Override // android.car.media.ICarMedia
        public void registerMediaSourceListener(ICarMediaSourceListener iCarMediaSourceListener, int i) throws RemoteException {
        }

        @Override // android.car.media.ICarMedia
        public void setIndependentPlaybackConfig(boolean z) throws RemoteException {
        }

        @Override // android.car.media.ICarMedia
        public void setMediaSource(ComponentName componentName, int i) throws RemoteException {
        }

        @Override // android.car.media.ICarMedia
        public void unregisterMediaSourceListener(ICarMediaSourceListener iCarMediaSourceListener, int i) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarMedia {
        private static final String DESCRIPTOR = "android.car.media.ICarMedia";
        static final int TRANSACTION_getLastMediaSources = 5;
        static final int TRANSACTION_getMediaSource = 1;
        static final int TRANSACTION_isIndependentPlaybackConfig = 6;
        static final int TRANSACTION_registerMediaSourceListener = 3;
        static final int TRANSACTION_setIndependentPlaybackConfig = 7;
        static final int TRANSACTION_setMediaSource = 2;
        static final int TRANSACTION_unregisterMediaSourceListener = 4;

        private static class Proxy implements ICarMedia {
            public static ICarMedia sDefaultImpl;
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

            @Override // android.car.media.ICarMedia
            public List<ComponentName> getLastMediaSources(int i) throws RemoteException {
                List<ComponentName> listCreateTypedArrayList;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(5, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        listCreateTypedArrayList = parcelObtain2.createTypedArrayList(ComponentName.CREATOR);
                    } else {
                        listCreateTypedArrayList = Stub.getDefaultImpl().getLastMediaSources(i);
                    }
                    return listCreateTypedArrayList;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarMedia
            public ComponentName getMediaSource(int i) throws RemoteException {
                ComponentName mediaSource;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        mediaSource = parcelObtain2.readInt() != 0 ? (ComponentName) ComponentName.CREATOR.createFromParcel(parcelObtain2) : null;
                    } else {
                        mediaSource = Stub.getDefaultImpl().getMediaSource(i);
                    }
                    return mediaSource;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarMedia
            public boolean isIndependentPlaybackConfig() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(6, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isIndependentPlaybackConfig();
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarMedia
            public void registerMediaSourceListener(ICarMediaSourceListener iCarMediaSourceListener, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarMediaSourceListener != null ? iCarMediaSourceListener.asBinder() : null);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(3, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().registerMediaSourceListener(iCarMediaSourceListener, i);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarMedia
            public void setIndependentPlaybackConfig(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(7, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setIndependentPlaybackConfig(z);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarMedia
            public void setMediaSource(ComponentName componentName, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (componentName != null) {
                        parcelObtain.writeInt(1);
                        componentName.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(2, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setMediaSource(componentName, i);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.media.ICarMedia
            public void unregisterMediaSourceListener(ICarMediaSourceListener iCarMediaSourceListener, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iCarMediaSourceListener != null ? iCarMediaSourceListener.asBinder() : null);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(4, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregisterMediaSourceListener(iCarMediaSourceListener, i);
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

        public static ICarMedia asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarMedia)) ? new Proxy(iBinder) : (ICarMedia) iInterfaceQueryLocalInterface;
        }

        public static ICarMedia getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarMedia iCarMedia) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarMedia == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarMedia;
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
                    ComponentName mediaSource = getMediaSource(parcel.readInt());
                    parcel2.writeNoException();
                    if (mediaSource != null) {
                        parcel2.writeInt(1);
                        mediaSource.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    setMediaSource(parcel.readInt() != 0 ? (ComponentName) ComponentName.CREATOR.createFromParcel(parcel) : null, parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    registerMediaSourceListener(ICarMediaSourceListener.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterMediaSourceListener(ICarMediaSourceListener.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    List<ComponentName> lastMediaSources = getLastMediaSources(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeTypedList(lastMediaSources);
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean zIsIndependentPlaybackConfig = isIndependentPlaybackConfig();
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsIndependentPlaybackConfig ? 1 : 0);
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    setIndependentPlaybackConfig(parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    List<ComponentName> getLastMediaSources(int i) throws RemoteException;

    ComponentName getMediaSource(int i) throws RemoteException;

    boolean isIndependentPlaybackConfig() throws RemoteException;

    void registerMediaSourceListener(ICarMediaSourceListener iCarMediaSourceListener, int i) throws RemoteException;

    void setIndependentPlaybackConfig(boolean z) throws RemoteException;

    void setMediaSource(ComponentName componentName, int i) throws RemoteException;

    void unregisterMediaSourceListener(ICarMediaSourceListener iCarMediaSourceListener, int i) throws RemoteException;
}
