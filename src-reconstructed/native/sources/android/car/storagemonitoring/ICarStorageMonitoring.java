package android.car.storagemonitoring;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface ICarStorageMonitoring extends IInterface {

    public static class Default implements ICarStorageMonitoring {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.car.storagemonitoring.ICarStorageMonitoring
        public List<IoStatsEntry> getAggregateIoStats() throws RemoteException {
            return null;
        }

        @Override // android.car.storagemonitoring.ICarStorageMonitoring
        public List<IoStatsEntry> getBootIoStats() throws RemoteException {
            return null;
        }

        @Override // android.car.storagemonitoring.ICarStorageMonitoring
        public List<IoStats> getIoStatsDeltas() throws RemoteException {
            return null;
        }

        @Override // android.car.storagemonitoring.ICarStorageMonitoring
        public int getPreEolIndicatorStatus() throws RemoteException {
            return 0;
        }

        @Override // android.car.storagemonitoring.ICarStorageMonitoring
        public long getShutdownDiskWriteAmount() throws RemoteException {
            return 0L;
        }

        @Override // android.car.storagemonitoring.ICarStorageMonitoring
        public WearEstimate getWearEstimate() throws RemoteException {
            return null;
        }

        @Override // android.car.storagemonitoring.ICarStorageMonitoring
        public List<WearEstimateChange> getWearEstimateHistory() throws RemoteException {
            return null;
        }

        @Override // android.car.storagemonitoring.ICarStorageMonitoring
        public void registerListener(IIoStatsListener iIoStatsListener) throws RemoteException {
        }

        @Override // android.car.storagemonitoring.ICarStorageMonitoring
        public void unregisterListener(IIoStatsListener iIoStatsListener) throws RemoteException {
        }
    }

    public static abstract class Stub extends Binder implements ICarStorageMonitoring {
        private static final String DESCRIPTOR = "android.car.storagemonitoring.ICarStorageMonitoring";
        static final int TRANSACTION_getAggregateIoStats = 6;
        static final int TRANSACTION_getBootIoStats = 5;
        static final int TRANSACTION_getIoStatsDeltas = 7;
        static final int TRANSACTION_getPreEolIndicatorStatus = 2;
        static final int TRANSACTION_getShutdownDiskWriteAmount = 10;
        static final int TRANSACTION_getWearEstimate = 3;
        static final int TRANSACTION_getWearEstimateHistory = 4;
        static final int TRANSACTION_registerListener = 8;
        static final int TRANSACTION_unregisterListener = 9;

        private static class Proxy implements ICarStorageMonitoring {
            public static ICarStorageMonitoring sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.car.storagemonitoring.ICarStorageMonitoring
            public List<IoStatsEntry> getAggregateIoStats() throws RemoteException {
                List<IoStatsEntry> listCreateTypedArrayList;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(6, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        listCreateTypedArrayList = parcelObtain2.createTypedArrayList(IoStatsEntry.CREATOR);
                    } else {
                        listCreateTypedArrayList = Stub.getDefaultImpl().getAggregateIoStats();
                    }
                    return listCreateTypedArrayList;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.storagemonitoring.ICarStorageMonitoring
            public List<IoStatsEntry> getBootIoStats() throws RemoteException {
                List<IoStatsEntry> listCreateTypedArrayList;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(5, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        listCreateTypedArrayList = parcelObtain2.createTypedArrayList(IoStatsEntry.CREATOR);
                    } else {
                        listCreateTypedArrayList = Stub.getDefaultImpl().getBootIoStats();
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

            @Override // android.car.storagemonitoring.ICarStorageMonitoring
            public List<IoStats> getIoStatsDeltas() throws RemoteException {
                List<IoStats> listCreateTypedArrayList;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(7, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        listCreateTypedArrayList = parcelObtain2.createTypedArrayList(IoStats.CREATOR);
                    } else {
                        listCreateTypedArrayList = Stub.getDefaultImpl().getIoStatsDeltas();
                    }
                    return listCreateTypedArrayList;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.storagemonitoring.ICarStorageMonitoring
            public int getPreEolIndicatorStatus() throws RemoteException {
                int preEolIndicatorStatus;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(2, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        preEolIndicatorStatus = parcelObtain2.readInt();
                    } else {
                        preEolIndicatorStatus = Stub.getDefaultImpl().getPreEolIndicatorStatus();
                    }
                    return preEolIndicatorStatus;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.storagemonitoring.ICarStorageMonitoring
            public long getShutdownDiskWriteAmount() throws RemoteException {
                long shutdownDiskWriteAmount;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(10, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        shutdownDiskWriteAmount = parcelObtain2.readLong();
                    } else {
                        shutdownDiskWriteAmount = Stub.getDefaultImpl().getShutdownDiskWriteAmount();
                    }
                    return shutdownDiskWriteAmount;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.storagemonitoring.ICarStorageMonitoring
            public WearEstimate getWearEstimate() throws RemoteException {
                WearEstimate wearEstimateCreateFromParcel;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(3, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        wearEstimateCreateFromParcel = parcelObtain2.readInt() != 0 ? WearEstimate.CREATOR.createFromParcel(parcelObtain2) : null;
                    } else {
                        wearEstimateCreateFromParcel = Stub.getDefaultImpl().getWearEstimate();
                    }
                    return wearEstimateCreateFromParcel;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.storagemonitoring.ICarStorageMonitoring
            public List<WearEstimateChange> getWearEstimateHistory() throws RemoteException {
                List<WearEstimateChange> listCreateTypedArrayList;
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(4, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                        listCreateTypedArrayList = parcelObtain2.createTypedArrayList(WearEstimateChange.CREATOR);
                    } else {
                        listCreateTypedArrayList = Stub.getDefaultImpl().getWearEstimateHistory();
                    }
                    return listCreateTypedArrayList;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.storagemonitoring.ICarStorageMonitoring
            public void registerListener(IIoStatsListener iIoStatsListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iIoStatsListener != null ? iIoStatsListener.asBinder() : null);
                    if (this.mRemote.transact(8, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().registerListener(iIoStatsListener);
                    }
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // android.car.storagemonitoring.ICarStorageMonitoring
            public void unregisterListener(IIoStatsListener iIoStatsListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeStrongBinder(iIoStatsListener != null ? iIoStatsListener.asBinder() : null);
                    if (this.mRemote.transact(9, parcelObtain, parcelObtain2, 0) || Stub.getDefaultImpl() == null) {
                        parcelObtain2.readException();
                    } else {
                        Stub.getDefaultImpl().unregisterListener(iIoStatsListener);
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

        public static ICarStorageMonitoring asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (iInterfaceQueryLocalInterface == null || !(iInterfaceQueryLocalInterface instanceof ICarStorageMonitoring)) ? new Proxy(iBinder) : (ICarStorageMonitoring) iInterfaceQueryLocalInterface;
        }

        public static ICarStorageMonitoring getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        public static boolean setDefaultImpl(ICarStorageMonitoring iCarStorageMonitoring) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (iCarStorageMonitoring == null) {
                return false;
            }
            Proxy.sDefaultImpl = iCarStorageMonitoring;
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
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    int preEolIndicatorStatus = getPreEolIndicatorStatus();
                    parcel2.writeNoException();
                    parcel2.writeInt(preEolIndicatorStatus);
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    WearEstimate wearEstimate = getWearEstimate();
                    parcel2.writeNoException();
                    if (wearEstimate != null) {
                        parcel2.writeInt(1);
                        wearEstimate.writeToParcel(parcel2, 1);
                    } else {
                        parcel2.writeInt(0);
                    }
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    List<WearEstimateChange> wearEstimateHistory = getWearEstimateHistory();
                    parcel2.writeNoException();
                    parcel2.writeTypedList(wearEstimateHistory);
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    List<IoStatsEntry> bootIoStats = getBootIoStats();
                    parcel2.writeNoException();
                    parcel2.writeTypedList(bootIoStats);
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    List<IoStatsEntry> aggregateIoStats = getAggregateIoStats();
                    parcel2.writeNoException();
                    parcel2.writeTypedList(aggregateIoStats);
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    List<IoStats> ioStatsDeltas = getIoStatsDeltas();
                    parcel2.writeNoException();
                    parcel2.writeTypedList(ioStatsDeltas);
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    registerListener(IIoStatsListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    unregisterListener(IIoStatsListener.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    long shutdownDiskWriteAmount = getShutdownDiskWriteAmount();
                    parcel2.writeNoException();
                    parcel2.writeLong(shutdownDiskWriteAmount);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    List<IoStatsEntry> getAggregateIoStats() throws RemoteException;

    List<IoStatsEntry> getBootIoStats() throws RemoteException;

    List<IoStats> getIoStatsDeltas() throws RemoteException;

    int getPreEolIndicatorStatus() throws RemoteException;

    long getShutdownDiskWriteAmount() throws RemoteException;

    WearEstimate getWearEstimate() throws RemoteException;

    List<WearEstimateChange> getWearEstimateHistory() throws RemoteException;

    void registerListener(IIoStatsListener iIoStatsListener) throws RemoteException;

    void unregisterListener(IIoStatsListener iIoStatsListener) throws RemoteException;
}
