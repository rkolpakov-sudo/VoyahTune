package android.car.vms;

import android.annotation.SystemApi;
import android.car.Car;
import android.car.CarManagerBase;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Log;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class VmsClientManager extends CarManagerBase {
    private static final boolean DBG = false;
    private static final String TAG = "VmsClientManager";
    private final IVmsBrokerService mBrokerService;
    private final Map<VmsClientCallback, VmsClient> mClients;
    private final Object mLock;

    public interface VmsClientCallback {
        void onClientConnected(VmsClient vmsClient);

        void onLayerAvailabilityChanged(VmsAvailableLayers vmsAvailableLayers);

        void onPacketReceived(int i, VmsLayer vmsLayer, byte[] bArr);

        void onSubscriptionStateChanged(VmsSubscriptionState vmsSubscriptionState);
    }

    public VmsClientManager(Car car, IBinder iBinder) {
        super(car);
        this.mLock = new Object();
        this.mClients = new ArrayMap();
        this.mBrokerService = IVmsBrokerService.Stub.asInterface(iBinder);
    }

    static /* synthetic */ void lambda$registerVmsClientCallback$0(VmsClientCallback vmsClientCallback, VmsClient vmsClient, boolean z) {
        vmsClientCallback.onClientConnected(vmsClient);
        if (z) {
            return;
        }
        vmsClientCallback.onLayerAvailabilityChanged(vmsClient.getAvailableLayers());
        vmsClientCallback.onSubscriptionStateChanged(vmsClient.getSubscriptionState());
    }

    @Override // android.car.CarManagerBase
    protected void onCarDisconnected() {
        synchronized (this.mLock) {
            Log.w(TAG, "Car disconnected with " + this.mClients.size() + " active clients");
            this.mClients.clear();
        }
    }

    public void registerVmsClientCallback(Executor executor, VmsClientCallback vmsClientCallback) {
        registerVmsClientCallback(executor, vmsClientCallback, false);
    }

    void registerVmsClientCallback(Executor executor, final VmsClientCallback vmsClientCallback, final boolean z) {
        Objects.requireNonNull(executor, "executor cannot be null");
        Objects.requireNonNull(vmsClientCallback, "callback cannot be null");
        synchronized (this.mLock) {
            if (this.mClients.containsKey(vmsClientCallback)) {
                Log.w(TAG, "VmsClient already registered");
                return;
            }
            final VmsClient vmsClient = new VmsClient(this.mBrokerService, executor, vmsClientCallback, z, true, new Consumer(this) { // from class: android.car.vms._$$Lambda$VmsClientManager$te4JE2QTsm0GVKKWLVIwNfYvvck
                public final VmsClientManager f$0;

                {
                    this.f$0 = this;
                }

                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    this.f$0.handleRemoteExceptionFromCarService((RemoteException) obj);
                }
            });
            this.mClients.put(vmsClientCallback, vmsClient);
            try {
                vmsClient.register();
                executor.execute(new Runnable(vmsClientCallback, vmsClient, z) { // from class: android.car.vms._$$Lambda$VmsClientManager$gQvIX4HpwUP0enZIjCLAFH_WyuM
                    public final VmsClientManager.VmsClientCallback f$0;
                    public final VmsClient f$1;
                    public final boolean f$2;

                    {
                        this.f$0 = vmsClientCallback;
                        this.f$1 = vmsClient;
                        this.f$2 = z;
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        VmsClientManager.lambda$registerVmsClientCallback$0(this.f$0, this.f$1, this.f$2);
                    }
                });
            } catch (RemoteException e) {
                Log.e(TAG, "Error while registering", e);
                synchronized (this.mLock) {
                    this.mClients.remove(vmsClientCallback);
                    handleRemoteExceptionFromCarService(e);
                }
            }
        }
    }

    public void unregisterVmsClientCallback(VmsClientCallback vmsClientCallback) {
        VmsClient vmsClientRemove;
        synchronized (this.mLock) {
            vmsClientRemove = this.mClients.remove(vmsClientCallback);
        }
        if (vmsClientRemove == null) {
            Log.w(TAG, "Unregister called for unknown callback");
            return;
        }
        try {
            vmsClientRemove.unregister();
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }
}
