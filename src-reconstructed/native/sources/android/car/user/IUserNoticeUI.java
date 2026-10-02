package android.car.user;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IUserNoticeUI extends IInterface {

    public static class Default implements IUserNoticeUI {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.user.IUserNoticeUI
        public void setCallbackBinder(IUserNotice iUserNotice) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements IUserNoticeUI {
        private static final String DESCRIPTOR = "android.car.user.IUserNoticeUI";
        static final int TRANSACTION_setCallbackBinder = 1;

        private static class Proxy implements IUserNoticeUI {
            public static IUserNoticeUI sDefaultImpl;
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

            @Override // android.car.user.IUserNoticeUI
            public void setCallbackBinder(IUserNotice iUserNotice) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iUserNotice != null ? iUserNotice.asBinder() : null);
                    if (this.mRemote.transact(1, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().setCallbackBinder(iUserNotice);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IUserNoticeUI asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IUserNoticeUI)) ? new Proxy(iBinder) : (IUserNoticeUI) iInterfaceQueryLocalInterface;
        }

        public static IUserNoticeUI getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IUserNoticeUI iUserNoticeUI) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iUserNoticeUI == null) {
                return false;
            }
            Proxy.sDefaultImpl = iUserNoticeUI;
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
                setCallbackBinder(IUserNotice.Stub.asInterface(parcel.readStrongBinder()));
                return true;
            }
            if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            parcel2.writeString(DESCRIPTOR);
            return true;
        }
    }

    void setCallbackBinder(IUserNotice iUserNotice) throws RemoteException;
}
