package android.car.vms;

import android.annotation.SystemApi;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SharedMemory;
import android.system.ErrnoException;
import android.system.OsConstants;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class VmsClient {
    private static final boolean DBG = false;
    private static final VmsAvailableLayers DEFAULT_AVAILABLE_LAYERS = new VmsAvailableLayers((Set<VmsAssociatedLayer>) Collections.emptySet(), 0);
    private static final VmsSubscriptionState DEFAULT_SUBSCRIPTIONS = new VmsSubscriptionState(0, Collections.emptySet(), Collections.emptySet());
    private static final int LARGE_PACKET_THRESHOLD = 16384;
    private static final String TAG = "VmsClient";
    private final VmsClientManager.VmsClientCallback mCallback;
    private final IVmsClientCallback mClientCallback;
    private final Consumer<RemoteException> mExceptionHandler;
    private final Executor mExecutor;
    private final boolean mLegacyClient;
    private boolean mMonitoringEnabled;
    private final IVmsBrokerService mService;
    private final Object mLock = new Object();
    private VmsAvailableLayers mAvailableLayers = DEFAULT_AVAILABLE_LAYERS;
    private VmsSubscriptionState mSubscriptionState = DEFAULT_SUBSCRIPTIONS;
    private final IBinder mClientToken = new Binder();

    /* JADX INFO: Access modifiers changed from: private */
    static class IVmsClientCallbackImpl extends IVmsClientCallback.Stub {
        private final boolean mAutoCloseMemory;
        private final WeakReference<VmsClient> mClient;

        private IVmsClientCallbackImpl(VmsClient vmsClient, boolean z) {
            this.mClient = new WeakReference<>(vmsClient);
            this.mAutoCloseMemory = z;
        }

        private void executeCallback(final BiConsumer<VmsClient, VmsClientManager.VmsClientCallback> biConsumer) {
            final VmsClient vmsClient = this.mClient.get();
            if (vmsClient == null) {
                Log.w(VmsClient.TAG, "VmsClient unavailable");
                return;
            }
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            try {
                vmsClient.mExecutor.execute(new Runnable(biConsumer, vmsClient) { // from class: android.car.vms._$$Lambda$VmsClient$IVmsClientCallbackImpl$8W48vN7eusPmxodMzrnBELAcFX4
                    public final BiConsumer f$0;
                    public final VmsClient f$1;

                    {
                        this.f$0 = biConsumer;
                        this.f$1 = vmsClient;
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        BiConsumer biConsumer2 = this.f$0;
                        VmsClient vmsClient2 = this.f$1;
                        biConsumer2.accept(vmsClient2, vmsClient2.mCallback);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(jClearCallingIdentity);
            }
        }

        static /* synthetic */ void lambda$onLayerAvailabilityChanged$0(VmsAvailableLayers vmsAvailableLayers, VmsClient vmsClient, VmsClientManager.VmsClientCallback vmsClientCallback) {
            synchronized (vmsClient.mLock) {
                vmsClient.mAvailableLayers = vmsAvailableLayers;
            }
            vmsClientCallback.onLayerAvailabilityChanged(vmsAvailableLayers);
        }

        static /* synthetic */ void lambda$onSubscriptionStateChanged$1(VmsSubscriptionState vmsSubscriptionState, VmsClient vmsClient, VmsClientManager.VmsClientCallback vmsClientCallback) {
            synchronized (vmsClient.mLock) {
                vmsClient.mSubscriptionState = vmsSubscriptionState;
            }
            vmsClientCallback.onSubscriptionStateChanged(vmsSubscriptionState);
        }

        @Override // android.car.vms.IVmsClientCallback
        public void onLargePacketReceived(final int i, final VmsLayer vmsLayer, SharedMemory sharedMemory) {
            final byte[] bArrSharedMemoryToPacket;
            if (this.mAutoCloseMemory) {
                try {
                    bArrSharedMemoryToPacket = VmsClient.sharedMemoryToPacket(sharedMemory);
                    if (sharedMemory != null) {
                        sharedMemory.close();
                    }
                } catch (Throwable th) {
                    if (sharedMemory != null) {
                        try {
                            sharedMemory.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } else {
                bArrSharedMemoryToPacket = VmsClient.sharedMemoryToPacket(sharedMemory);
            }
            executeCallback(new BiConsumer(i, vmsLayer, bArrSharedMemoryToPacket) { // from class: android.car.vms._$$Lambda$VmsClient$IVmsClientCallbackImpl$lIn_OFTglQJ26KvnmzXS6FCatH0
                public final int f$0;
                public final VmsLayer f$1;
                public final byte[] f$2;

                {
                    this.f$0 = i;
                    this.f$1 = vmsLayer;
                    this.f$2 = bArrSharedMemoryToPacket;
                }

                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((VmsClientManager.VmsClientCallback) obj2).onPacketReceived(this.f$0, this.f$1, this.f$2);
                }
            });
        }

        @Override // android.car.vms.IVmsClientCallback
        public void onLayerAvailabilityChanged(final VmsAvailableLayers vmsAvailableLayers) {
            executeCallback(new BiConsumer(vmsAvailableLayers) { // from class: android.car.vms._$$Lambda$VmsClient$IVmsClientCallbackImpl$_cQcGQ5TOfMnzvWtkoXQ9v9Hke0
                public final VmsAvailableLayers f$0;

                {
                    this.f$0 = vmsAvailableLayers;
                }

                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    VmsClient.IVmsClientCallbackImpl.lambda$onLayerAvailabilityChanged$0(this.f$0, (VmsClient) obj, (VmsClientManager.VmsClientCallback) obj2);
                }
            });
        }

        @Override // android.car.vms.IVmsClientCallback
        public void onPacketReceived(final int i, final VmsLayer vmsLayer, final byte[] bArr) {
            executeCallback(new BiConsumer(i, vmsLayer, bArr) { // from class: android.car.vms._$$Lambda$VmsClient$IVmsClientCallbackImpl$Zf5RNWml5vRdj2NUDuWxyF_GXWE
                public final int f$0;
                public final VmsLayer f$1;
                public final byte[] f$2;

                {
                    this.f$0 = i;
                    this.f$1 = vmsLayer;
                    this.f$2 = bArr;
                }

                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((VmsClientManager.VmsClientCallback) obj2).onPacketReceived(this.f$0, this.f$1, this.f$2);
                }
            });
        }

        @Override // android.car.vms.IVmsClientCallback
        public void onSubscriptionStateChanged(final VmsSubscriptionState vmsSubscriptionState) {
            executeCallback(new BiConsumer(vmsSubscriptionState) { // from class: android.car.vms._$$Lambda$VmsClient$IVmsClientCallbackImpl$GoBC0fIIc3_mDP9xGO3ZPbH3Dp0
                public final VmsSubscriptionState f$0;

                {
                    this.f$0 = vmsSubscriptionState;
                }

                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    VmsClient.IVmsClientCallbackImpl.lambda$onSubscriptionStateChanged$1(this.f$0, (VmsClient) obj, (VmsClientManager.VmsClientCallback) obj2);
                }
            });
        }
    }

    public VmsClient(IVmsBrokerService iVmsBrokerService, Executor executor, VmsClientManager.VmsClientCallback vmsClientCallback, boolean z, boolean z2, Consumer<RemoteException> consumer) {
        this.mService = iVmsBrokerService;
        this.mExecutor = executor;
        this.mCallback = vmsClientCallback;
        this.mLegacyClient = z;
        this.mClientCallback = new IVmsClientCallbackImpl(z2);
        this.mExceptionHandler = consumer;
    }

    private static SharedMemory packetToSharedMemory(byte[] bArr) {
        try {
            SharedMemory sharedMemoryCreate = SharedMemory.create("VmsClient", bArr.length);
            ByteBuffer byteBufferMapReadWrite = null;
            try {
                try {
                    byteBufferMapReadWrite = sharedMemoryCreate.mapReadWrite();
                    byteBufferMapReadWrite.put(bArr);
                    if (byteBufferMapReadWrite != null) {
                        SharedMemory.unmap(byteBufferMapReadWrite);
                    }
                    if (sharedMemoryCreate.setProtect(OsConstants.PROT_READ)) {
                        return sharedMemoryCreate;
                    }
                    sharedMemoryCreate.close();
                    throw new SecurityException("Failed to set read-only protection on shared memory");
                } catch (ErrnoException e) {
                    sharedMemoryCreate.close();
                    throw new IllegalStateException("Failed to create write buffer", e);
                }
            } catch (Throwable th) {
                if (byteBufferMapReadWrite != null) {
                    SharedMemory.unmap(byteBufferMapReadWrite);
                }
                throw th;
            }
        } catch (ErrnoException e2) {
            throw new IllegalStateException("Failed to allocate shared memory", e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] sharedMemoryToPacket(SharedMemory sharedMemory) {
        try {
            ByteBuffer byteBufferMapReadOnly = sharedMemory.mapReadOnly();
            try {
                byte[] bArr = new byte[byteBufferMapReadOnly.capacity()];
                byteBufferMapReadOnly.get(bArr);
                return bArr;
            } finally {
                SharedMemory.unmap(byteBufferMapReadOnly);
            }
        } catch (ErrnoException e) {
            throw new IllegalStateException("Failed to create read buffer", e);
        }
    }

    public VmsAvailableLayers getAvailableLayers() {
        VmsAvailableLayers vmsAvailableLayers;
        synchronized (this.mLock) {
            vmsAvailableLayers = this.mAvailableLayers;
        }
        return vmsAvailableLayers;
    }

    public byte[] getProviderDescription(int i) {
        try {
            return this.mService.getProviderInfo(this.mClientToken, i).getDescription();
        } catch (RemoteException e) {
            Log.e(TAG, "While getting publisher information for " + i, e);
            this.mExceptionHandler.accept(e);
            return null;
        }
    }

    public VmsSubscriptionState getSubscriptionState() {
        VmsSubscriptionState vmsSubscriptionState;
        synchronized (this.mLock) {
            vmsSubscriptionState = this.mSubscriptionState;
        }
        return vmsSubscriptionState;
    }

    public boolean isMonitoringEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mMonitoringEnabled;
        }
        return z;
    }

    public void publishPacket(int i, VmsLayer vmsLayer, byte[] bArr) {
        Objects.requireNonNull(vmsLayer, "layer cannot be null");
        Objects.requireNonNull(bArr, "packet cannot be null");
        try {
            if (bArr.length < 16384) {
                this.mService.publishPacket(this.mClientToken, i, vmsLayer, bArr);
                return;
            }
            SharedMemory sharedMemoryPacketToSharedMemory = packetToSharedMemory(bArr);
            try {
                this.mService.publishLargePacket(this.mClientToken, i, vmsLayer, sharedMemoryPacketToSharedMemory);
                if (sharedMemoryPacketToSharedMemory != null) {
                    sharedMemoryPacketToSharedMemory.close();
                }
            } catch (Throwable th) {
                if (sharedMemoryPacketToSharedMemory != null) {
                    try {
                        sharedMemoryPacketToSharedMemory.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "While publishing packet as " + i);
            this.mExceptionHandler.accept(e);
        }
    }

    public void register() throws RemoteException {
        VmsRegistrationInfo vmsRegistrationInfoRegisterClient = this.mService.registerClient(this.mClientToken, this.mClientCallback, this.mLegacyClient);
        synchronized (this.mLock) {
            this.mAvailableLayers = vmsRegistrationInfoRegisterClient.getAvailableLayers();
            this.mSubscriptionState = vmsRegistrationInfoRegisterClient.getSubscriptionState();
        }
    }

    public int registerProvider(byte[] bArr) {
        Objects.requireNonNull(bArr, "providerDescription cannot be null");
        try {
            return this.mService.registerProvider(this.mClientToken, new VmsProviderInfo(bArr));
        } catch (RemoteException e) {
            Log.e(TAG, "While registering provider", e);
            this.mExceptionHandler.accept(e);
            return -1;
        }
    }

    public void setMonitoringEnabled(boolean z) {
        try {
            this.mService.setMonitoringEnabled(this.mClientToken, z);
            synchronized (this.mLock) {
                this.mMonitoringEnabled = z;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "While setting monitoring state to " + z, e);
            this.mExceptionHandler.accept(e);
        }
    }

    public void setProviderOfferings(int i, Set<VmsLayerDependency> set) {
        Objects.requireNonNull(set, "offerings cannot be null");
        try {
            this.mService.setProviderOfferings(this.mClientToken, i, new ArrayList(set));
        } catch (RemoteException e) {
            Log.e(TAG, "While setting provider offerings for " + i, e);
            this.mExceptionHandler.accept(e);
        }
    }

    public void setSubscriptions(Set<VmsAssociatedLayer> set) {
        try {
            this.mService.setSubscriptions(this.mClientToken, new ArrayList(set));
        } catch (RemoteException e) {
            Log.e(TAG, "While setting subscriptions", e);
            this.mExceptionHandler.accept(e);
        }
    }

    public void unregister() throws RemoteException {
        this.mService.unregisterClient(this.mClientToken);
    }

    public void unregisterProvider(int i) {
        try {
            setProviderOfferings(i, Collections.emptySet());
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "While unregistering provider " + i, e);
        }
    }
}
