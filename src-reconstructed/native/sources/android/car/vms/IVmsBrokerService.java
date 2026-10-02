package android.car.vms;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SharedMemory;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface IVmsBrokerService extends IInterface {

    public static class Default implements IVmsBrokerService {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.vms.IVmsBrokerService
        public VmsProviderInfo getProviderInfo(IBinder iBinder, int i) throws RemoteException {
            return null;
        }

        @Override // android.car.vms.IVmsBrokerService
        public void publishLargePacket(IBinder iBinder, int i, VmsLayer vmsLayer, SharedMemory sharedMemory) throws RemoteException {
        }

        @Override // android.car.vms.IVmsBrokerService
        public void publishPacket(IBinder iBinder, int i, VmsLayer vmsLayer, byte[] bArr) throws RemoteException {
        }

        @Override // android.car.vms.IVmsBrokerService
        public VmsRegistrationInfo registerClient(IBinder iBinder, IVmsClientCallback iVmsClientCallback, boolean z) throws RemoteException {
            return null;
        }

        @Override // android.car.vms.IVmsBrokerService
        public int registerProvider(IBinder iBinder, VmsProviderInfo vmsProviderInfo) throws RemoteException {
            return 0;
        }

        @Override // android.car.vms.IVmsBrokerService
        public void setMonitoringEnabled(IBinder iBinder, boolean z) throws RemoteException {
        }

        @Override // android.car.vms.IVmsBrokerService
        public void setProviderOfferings(IBinder iBinder, int i, List<VmsLayerDependency> list) throws RemoteException {
        }

        @Override // android.car.vms.IVmsBrokerService
        public void setSubscriptions(IBinder iBinder, List<VmsAssociatedLayer> list) throws RemoteException {
        }

        @Override // android.car.vms.IVmsBrokerService
        public void unregisterClient(IBinder iBinder) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements IVmsBrokerService {
        private static final String DESCRIPTOR = "android.car.vms.IVmsBrokerService";
        static final int TRANSACTION_getProviderInfo = 3;
        static final int TRANSACTION_publishLargePacket = 9;
        static final int TRANSACTION_publishPacket = 8;
        static final int TRANSACTION_registerClient = 1;
        static final int TRANSACTION_registerProvider = 6;
        static final int TRANSACTION_setMonitoringEnabled = 5;
        static final int TRANSACTION_setProviderOfferings = 7;
        static final int TRANSACTION_setSubscriptions = 4;
        static final int TRANSACTION_unregisterClient = 2;

        private static class Proxy implements IVmsBrokerService {
            public static IVmsBrokerService sDefaultImpl;
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

            @Override // android.car.vms.IVmsBrokerService
            public VmsProviderInfo getProviderInfo(IBinder iBinder, int i) throws RemoteException {
                VmsProviderInfo vmsProviderInfoCreateFromParcel;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    parcelObtain.writeInt(i);
                    if (this.mRemote.transact(3, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        vmsProviderInfoCreateFromParcel = parcelObtain2.readInt() != 0 ? VmsProviderInfo.CREATOR.createFromParcel(parcelObtain2) : null;
                    } else {
                        vmsProviderInfoCreateFromParcel = Stub.getDefaultImpl().getProviderInfo(iBinder, i);
                    }
                    return vmsProviderInfoCreateFromParcel;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.vms.IVmsBrokerService
            public void publishLargePacket(IBinder iBinder, int i, VmsLayer vmsLayer, SharedMemory sharedMemory) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    parcelObtain.writeInt(i);
                    if (vmsLayer != null) {
                        parcelObtain.writeInt(1);
                        vmsLayer.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (sharedMemory != null) {
                        parcelObtain.writeInt(1);
                        sharedMemory.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(9, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().publishLargePacket(iBinder, i, vmsLayer, sharedMemory);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.vms.IVmsBrokerService
            public void publishPacket(IBinder iBinder, int i, VmsLayer vmsLayer, byte[] bArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    parcelObtain.writeInt(i);
                    if (vmsLayer != null) {
                        parcelObtain.writeInt(1);
                        vmsLayer.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    parcelObtain.writeByteArray(bArr);
                    if (this.mRemote.transact(8, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().publishPacket(iBinder, i, vmsLayer, bArr);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.vms.IVmsBrokerService
            public VmsRegistrationInfo registerClient(IBinder iBinder, IVmsClientCallback iVmsClientCallback, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    parcelObtain.writeStrongBinder(iVmsClientCallback != null ? iVmsClientCallback.asBinder() : null);
                    parcelObtain.writeInt(z ? 1 : 0);
                    if (!this.mRemote.transact(1, parcelObtain, parcelObtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().registerClient(iBinder, iVmsClientCallback, z);
                    }
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0 ? VmsRegistrationInfo.CREATOR.createFromParcel(parcelObtain2) : null;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.vms.IVmsBrokerService
            public int registerProvider(IBinder iBinder, VmsProviderInfo vmsProviderInfo) throws RemoteException {
                int iRegisterProvider;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    if (vmsProviderInfo != null) {
                        parcelObtain.writeInt(1);
                        vmsProviderInfo.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(6, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        iRegisterProvider = parcelObtain2.readInt();
                    } else {
                        iRegisterProvider = Stub.getDefaultImpl().registerProvider(iBinder, vmsProviderInfo);
                    }
                    return iRegisterProvider;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.vms.IVmsBrokerService
            public void setMonitoringEnabled(IBinder iBinder, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    parcelObtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(5, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setMonitoringEnabled(iBinder, z);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.vms.IVmsBrokerService
            public void setProviderOfferings(IBinder iBinder, int i, List<VmsLayerDependency> list) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeTypedList(list);
                    if (this.mRemote.transact(7, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setProviderOfferings(iBinder, i, list);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.vms.IVmsBrokerService
            public void setSubscriptions(IBinder iBinder, List<VmsAssociatedLayer> list) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    parcelObtain.writeTypedList(list);
                    if (this.mRemote.transact(4, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().setSubscriptions(iBinder, list);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.vms.IVmsBrokerService
            public void unregisterClient(IBinder iBinder) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iBinder);
                    if (this.mRemote.transact(2, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregisterClient(iBinder);
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

        public static IVmsBrokerService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof IVmsBrokerService)) ? new Proxy(iBinder) : (IVmsBrokerService) iInterfaceQueryLocalInterface;
        }

        public static IVmsBrokerService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(IVmsBrokerService iVmsBrokerService) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iVmsBrokerService == null) {
                return false;
            }
            Proxy.sDefaultImpl = iVmsBrokerService;
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
                    VmsRegistrationInfo vmsRegistrationInfoRegisterClient = registerClient(parcel.readStrongBinder(), IVmsClientCallback.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    if (vmsRegistrationInfoRegisterClient != null) {
                        parcel2.writeInt(1);
                        vmsRegistrationInfoRegisterClient.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterClient(parcel.readStrongBinder());
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    VmsProviderInfo providerInfo = getProviderInfo(parcel.readStrongBinder(), parcel.readInt());
                    parcel2.writeNoException();
                    if (providerInfo != null) {
                        parcel2.writeInt(1);
                        providerInfo.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    setSubscriptions(parcel.readStrongBinder(), parcel.createTypedArrayList(VmsAssociatedLayer.CREATOR));
                    parcel2.writeNoException();
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    setMonitoringEnabled(parcel.readStrongBinder(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    int iRegisterProvider = registerProvider(parcel.readStrongBinder(), parcel.readInt() != 0 ? VmsProviderInfo.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeInt(iRegisterProvider);
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    setProviderOfferings(parcel.readStrongBinder(), parcel.readInt(), parcel.createTypedArrayList(VmsLayerDependency.CREATOR));
                    parcel2.writeNoException();
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    publishPacket(parcel.readStrongBinder(), parcel.readInt(), parcel.readInt() != 0 ? VmsLayer.CREATOR.createFromParcel(parcel) : null, parcel.createByteArray());
                    parcel2.writeNoException();
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    publishLargePacket(parcel.readStrongBinder(), parcel.readInt(), parcel.readInt() != 0 ? VmsLayer.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0 ? (SharedMemory) SharedMemory.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    VmsProviderInfo getProviderInfo(IBinder iBinder, int i) throws RemoteException;

    void publishLargePacket(IBinder iBinder, int i, VmsLayer vmsLayer, SharedMemory sharedMemory) throws RemoteException;

    void publishPacket(IBinder iBinder, int i, VmsLayer vmsLayer, byte[] bArr) throws RemoteException;

    VmsRegistrationInfo registerClient(IBinder iBinder, IVmsClientCallback iVmsClientCallback, boolean z) throws RemoteException;

    int registerProvider(IBinder iBinder, VmsProviderInfo vmsProviderInfo) throws RemoteException;

    void setMonitoringEnabled(IBinder iBinder, boolean z) throws RemoteException;

    void setProviderOfferings(IBinder iBinder, int i, List<VmsLayerDependency> list) throws RemoteException;

    void setSubscriptions(IBinder iBinder, List<VmsAssociatedLayer> list) throws RemoteException;

    void unregisterClient(IBinder iBinder) throws RemoteException;
}
