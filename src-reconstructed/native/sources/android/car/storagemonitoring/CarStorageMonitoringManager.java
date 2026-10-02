package android.car.storagemonitoring;

import android.annotation.SystemApi;
import android.car.Car;
import android.car.CarManagerBase;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.car.internal.SingleMessageHandler;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class CarStorageMonitoringManager extends CarManagerBase {
    public static final String INTENT_EXCESSIVE_IO = "android.car.storagemonitoring.EXCESSIVE_IO";
    private static final int MSG_IO_STATS_EVENT = 0;
    public static final int PRE_EOL_INFO_NORMAL = 1;
    public static final int PRE_EOL_INFO_UNKNOWN = 0;
    public static final int PRE_EOL_INFO_URGENT = 3;
    public static final int PRE_EOL_INFO_WARNING = 2;
    public static final long SHUTDOWN_COST_INFO_MISSING = -1;
    private static final String TAG = "CarStorageMonitoringManager";
    private ListenerToService mListenerToService;
    private final Set<IoStatsListener> mListeners;
    private final SingleMessageHandler<IoStats> mMessageHandler;
    private final ICarStorageMonitoring mService;

    public interface IoStatsListener {
        void onSnapshot(IoStats ioStats);
    }

    private static final class ListenerToService extends IIoStatsListener.Stub {
        private final WeakReference<CarStorageMonitoringManager> mManager;

        ListenerToService(CarStorageMonitoringManager carStorageMonitoringManager) {
            this.mManager = new WeakReference<>(carStorageMonitoringManager);
        }

        @Override // android.car.storagemonitoring.IIoStatsListener
        public void onSnapshot(IoStats ioStats) {
            CarStorageMonitoringManager carStorageMonitoringManager = this.mManager.get();
            if (carStorageMonitoringManager != null) {
                carStorageMonitoringManager.mMessageHandler.sendEvents(Collections.singletonList(ioStats));
            }
        }
    }

    public CarStorageMonitoringManager(Car car, IBinder iBinder) {
        super(car);
        this.mListeners = new HashSet();
        this.mService = ICarStorageMonitoring.Stub.asInterface(iBinder);
        this.mMessageHandler = new SingleMessageHandler<IoStats>(this, getEventHandler(), 0) { // from class: android.car.storagemonitoring.CarStorageMonitoringManager.1
            final CarStorageMonitoringManager this$0;

            {
                this.this$0 = this;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.android.car.internal.SingleMessageHandler
            public void handleEvent(IoStats ioStats) {
                Iterator it = this.this$0.mListeners.iterator();
                while (it.hasNext()) {
                    ((IoStatsListener) it.next()).onSnapshot(ioStats);
                }
            }
        };
    }

    public List<IoStatsEntry> getAggregateIoStats() {
        try {
            return this.mService.getAggregateIoStats();
        } catch (RemoteException e) {
            return (List) handleRemoteExceptionFromCarService(e, Collections.emptyList());
        }
    }

    public List<IoStatsEntry> getBootIoStats() {
        try {
            return this.mService.getBootIoStats();
        } catch (RemoteException e) {
            return (List) handleRemoteExceptionFromCarService(e, Collections.emptyList());
        }
    }

    public List<IoStats> getIoStatsDeltas() {
        try {
            return this.mService.getIoStatsDeltas();
        } catch (RemoteException e) {
            return (List) handleRemoteExceptionFromCarService(e, Collections.emptyList());
        }
    }

    public int getPreEolIndicatorStatus() {
        try {
            return this.mService.getPreEolIndicatorStatus();
        } catch (RemoteException e) {
            return ((Integer) handleRemoteExceptionFromCarService(e, 0)).intValue();
        }
    }

    public long getShutdownDiskWriteAmount() {
        try {
            return this.mService.getShutdownDiskWriteAmount();
        } catch (RemoteException e) {
            return ((Integer) handleRemoteExceptionFromCarService(e, 0)).intValue();
        }
    }

    public WearEstimate getWearEstimate() {
        try {
            return this.mService.getWearEstimate();
        } catch (RemoteException e) {
            return (WearEstimate) handleRemoteExceptionFromCarService(e, null);
        }
    }

    public List<WearEstimateChange> getWearEstimateHistory() {
        try {
            return this.mService.getWearEstimateHistory();
        } catch (RemoteException e) {
            return (List) handleRemoteExceptionFromCarService(e, Collections.emptyList());
        }
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
        this.mListeners.clear();
        this.mListenerToService = null;
    }

    public void registerListener(IoStatsListener ioStatsListener) {
        try {
            if (this.mListeners.isEmpty()) {
                if (this.mListenerToService == null) {
                    this.mListenerToService = new ListenerToService(this);
                }
                this.mService.registerListener(this.mListenerToService);
            }
            this.mListeners.add(ioStatsListener);
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    public void unregisterListener(IoStatsListener ioStatsListener) {
        try {
            if (this.mListeners.remove(ioStatsListener) && this.mListeners.isEmpty()) {
                this.mService.unregisterListener(this.mListenerToService);
                this.mListenerToService = null;
            }
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }
}
