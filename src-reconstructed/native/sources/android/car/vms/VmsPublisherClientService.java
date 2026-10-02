package android.car.vms;

import android.annotation.SystemApi;
import android.app.Service;
import android.car.Car;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
@Deprecated
public abstract class VmsPublisherClientService extends Service {
    private static final boolean DBG = false;
    private static final String TAG = "VmsPublisherClientService";
    private Car mCar;
    private VmsClient mClient;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final VmsClientManager.VmsClientCallback mClientCallback = new PublisherClientCallback();
    private final Object mLock = new Object();

    private class PublisherClientCallback implements VmsClientManager.VmsClientCallback {
        final VmsPublisherClientService this$0;

        private PublisherClientCallback(VmsPublisherClientService vmsPublisherClientService) {
            this.this$0 = vmsPublisherClientService;
        }

        @Override // android.car.vms.VmsClientManager.VmsClientCallback
        public void onClientConnected(VmsClient vmsClient) {
            synchronized (this.this$0.mLock) {
                this.this$0.mClient = vmsClient;
            }
            this.this$0.onVmsPublisherServiceReady();
        }

        @Override // android.car.vms.VmsClientManager.VmsClientCallback
        public void onLayerAvailabilityChanged(VmsAvailableLayers vmsAvailableLayers) {
        }

        @Override // android.car.vms.VmsClientManager.VmsClientCallback
        public void onPacketReceived(int i, VmsLayer vmsLayer, byte[] bArr) {
        }

        @Override // android.car.vms.VmsClientManager.VmsClientCallback
        public void onSubscriptionStateChanged(VmsSubscriptionState vmsSubscriptionState) {
            this.this$0.onVmsSubscriptionChange(vmsSubscriptionState);
        }
    }

    private VmsClient getVmsClient() {
        VmsClient vmsClient;
        synchronized (this.mLock) {
            vmsClient = this.mClient;
            if (vmsClient == null) {
                throw new IllegalStateException("VMS client connection is not ready");
            }
        }
        return vmsClient;
    }

    public final int getPublisherId(byte[] bArr) {
        return getVmsClient().registerProvider(bArr);
    }

    public final VmsSubscriptionState getSubscriptions() {
        return getVmsClient().getSubscriptionState();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    protected void onCarLifecycleChanged(Car car, boolean z) {
        if (z) {
            VmsClientManager vmsClientManager = (VmsClientManager) car.getCarManager(Car.VEHICLE_MAP_SERVICE);
            if (vmsClientManager == null) {
                Log.e(TAG, "VmsClientManager is not available");
            } else {
                vmsClientManager.registerVmsClientCallback(new HandlerExecutor(this.mHandler), this.mClientCallback, true);
            }
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        synchronized (this.mLock) {
            this.mCar = Car.createCar(this, this.mHandler, 0L, new Car.CarServiceLifecycleListener(this) { // from class: android.car.vms._$$Lambda$eVj_YyTGA2eIOCe2FB9y4gQzPvk
                public final VmsPublisherClientService f$0;

                {
                    this.f$0 = this;
                }

                @Override // android.car.Car.CarServiceLifecycleListener
                public final void onLifecycleChanged(Car car, boolean z) {
                    this.f$0.onCarLifecycleChanged(car, z);
                }
            });
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        synchronized (this.mLock) {
            Car car = this.mCar;
            if (car != null) {
                car.disconnect();
                this.mCar = null;
            }
        }
    }

    protected abstract void onVmsPublisherServiceReady();

    public abstract void onVmsSubscriptionChange(VmsSubscriptionState vmsSubscriptionState);

    public final void publish(VmsLayer vmsLayer, int i, byte[] bArr) {
        getVmsClient().publishPacket(i, vmsLayer, bArr);
    }

    public final void setLayersOffering(VmsLayersOffering vmsLayersOffering) {
        getVmsClient().setProviderOfferings(vmsLayersOffering.getPublisherId(), vmsLayersOffering.getDependencies());
    }
}
