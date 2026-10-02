package ru.big.town.anative;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import java.lang.ref.WeakReference;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import kotlinx.coroutines.DebugKt;

/* JADX INFO: loaded from: classes2.dex */
public class LightSensorService extends Service {
    public static final String ACTION_LUX_UPDATE = "ru.big.town.anative.LUX_UPDATE";
    private static final long BIND_RETRY_MS = 5000;
    private static final String CALLBACK_DESCRIPTOR = "com.qinggan.carsignal.ICarSignalServiceCallBack";
    private static final long CANBUS_REASSERT_DELAY_MS = 5000;
    private static final String CAR_SIGNAL_ACTION = "com.qinggan.carsignal.CarSignalService";
    private static final String CAR_SIGNAL_DESCRIPTOR = "com.qinggan.carsignal.ICarSignalService";
    private static final String CAR_SIGNAL_PACKAGE = "com.qinggan.carsignal.service";
    private static final int CB_onLightSensorChanged = 13;
    private static final String CHANNEL_ID = "light_sensor_channel";
    private static final int COL_THRESHOLD_OFF = 10;
    private static final int COL_THRESHOLD_ON = 9;
    private static final long DRIVE_FALLBACK_MS = 5000;
    public static final String EXTRA_SENSOR_LEVEL = "sensorLevel";
    private static final long FORCE_INIT_MS = 10000;
    private static final int GEAR_DRIVE = 3;
    private static final long HEADLIGHT_GUARD_MS = 2500;
    private static final int MAX_OUTSTANDING_CALLBACKS = 2;
    private static final int RSM_LIGHT_SW_REASON = 1072;
    private static final long SAFETY_POLL_MS = 30000;
    private static final long SENSOR_DEBOUNCE_MS = 3000;
    private static final String TAG = "$$$ LightSensorService $$$";
    private static final int TX_getLightSensorLevel = 36;
    private static final int TX_registerCallback = 46;
    private static final int TX_unregisterCallback = 47;
    private long activeCarSignalBindingGeneration;
    private volatile CarSignalCallbackBinder activeCarSignalCallback;
    private CanBusEventHub.Subscription canBusSubscription;
    private CarSignalCallbackBinder carSignalCallbackBinder;
    private ServiceConnection carSignalConnection;
    private Executor carSignalIoExecutor;
    private Handler carSignalIoHandler;
    private HandlerThread carSignalIoThread;
    private GearStateController.Subscription gearStateSubscription;
    private long nextCarSignalBindingGeneration;
    private long nextSensorApplyGeneration;
    private long nextSettingsSnapshotGeneration;
    private SensorApplyRequest pendingIoSensorApply;
    private long pendingIoSettingsGeneration;
    private SensorApplyRequest pendingIoSettingsRequest;
    private volatile SensorApplyRequest pendingMainSensorApply;
    private RegisterRequest pendingRegistration;
    private SettingsSnapshot pendingSettingsSnapshot;
    private RegisterRequest runningRegistration;
    private SensorQueryRun runningSensorQuery;
    private volatile LatestIntDelivery sensorCallbackDelivery;
    private boolean sensorQueryRequested;
    private boolean sensorQueryRunning;
    private Handler timerHandler;
    private static final AtomicInteger OUTSTANDING_CALLBACKS = new AtomicInteger();
    private static final ThreadPoolExecutor CAR_SIGNAL_QUERY_EXECUTOR = newBoundedBinderExecutor("CarSignalQuery");
    private static final ThreadPoolExecutor CAR_SIGNAL_REGISTRATION_EXECUTOR = newBoundedBinderExecutor("CarSignalRegistration");
    private static final ThreadPoolExecutor CAR_SIGNAL_CLEANUP_EXECUTOR = newBoundedBinderExecutor("CarSignalCleanup");
    private static final ThreadPoolExecutor LIGHT_SETTINGS_EXECUTOR = newBoundedBinderExecutor("LightSettings");
    private static final ManualAutoGate MANUAL_AUTO_GATE = new ManualAutoGate();
    private static final Uri CONTENT_PROVIDER_URI = Uri.parse("content://ru.big.town.restoremode.restoremodecontentprovider/");
    private final AtomicBoolean carSignalMaintenancePosted = new AtomicBoolean();
    private IBinder carSignalBinder = null;
    private boolean carSignalBindingRequested = false;
    private boolean carSignalConnected = false;
    private boolean callbackRegistered = false;
    private boolean callbackRegistrationInFlight = false;
    private long lastBindAttempt = -5000;
    private final Runnable carSignalRebindRunnable = new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda18
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.ensureBound();
        }
    };
    private long carSignalEpoch = 0;
    private volatile long activeCarSignalEpoch = 0;
    private boolean headlightsOn = false;
    private volatile boolean everSent = false;
    private boolean forceInitCompleted = false;
    private long readyCarSignalEpoch = 0;
    private long forceInitCarSignalEpoch = 0;
    private long commitSequence = 0;
    private long pendingSensorEpoch = 0;
    private long pendingSensorRevision = 0;
    private int pendingSensorLevel = -1;
    private long lastCommitElapsed = 0;
    private long lastSensorEpoch = 0;
    private long lastSensorRevision = 0;
    private int lastSensorLevel = -1;
    private final LatestRequestGate<SensorApplyRequest> settingsRequestGate = new LatestRequestGate<>();
    private int lastGear = -1;
    private volatile int lastReason = -1;
    private volatile boolean destroyed = false;
    private int lastAutoLamp = -1;
    private int lastDippedBeam = -1;
    private int lastHeadLight = -1;
    private final Runnable settingsRetryRunnable = new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda19
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.m1968lambda$new$18$rubigtownanativeLightSensorService();
        }
    };
    private final Runnable sensorDebounceRunnable = new Runnable() { // from class: ru.big.town.anative.LightSensorService.1
        @Override // java.lang.Runnable
        public void run() {
            CarSignalCallbackBinder carSignalCallbackBinder = LightSensorService.this.activeCarSignalCallback;
            if (LightSensorService.this.pendingSensorLevel >= 0 && LightSensorService.this.pendingSensorEpoch == LightSensorService.this.readyCarSignalEpoch && LightSensorService.this.pendingSensorEpoch == LightSensorService.this.activeCarSignalEpoch && carSignalCallbackBinder != null && carSignalCallbackBinder.epoch == LightSensorService.this.pendingSensorEpoch && carSignalCallbackBinder.ingressRevision.get() == LightSensorService.this.pendingSensorRevision) {
                long j = LightSensorService.this.pendingSensorEpoch;
                long j2 = LightSensorService.this.pendingSensorRevision;
                int i = LightSensorService.this.pendingSensorLevel;
                LightSensorService.this.onSensorLevel(j, j2, i, "callback");
                SensorApplyRequest sensorApplyRequest = LightSensorService.this.pendingMainSensorApply;
                if (sensorApplyRequest != null && sensorApplyRequest.epoch == j && LightSensorService.this.applySensorRequest(sensorApplyRequest, i, j2, 0L)) {
                    LightSensorService.this.pendingMainSensorApply = null;
                    LightSensorService.this.acknowledgeSensorApplyFromCallback(j, sensorApplyRequest.generation);
                }
            }
        }
    };
    private final Runnable forceInitRunnable = new Runnable() { // from class: ru.big.town.anative.LightSensorService.2
        @Override // java.lang.Runnable
        public void run() {
            long j = LightSensorService.this.forceInitCarSignalEpoch;
            if (j != 0 && j == LightSensorService.this.readyCarSignalEpoch && j == LightSensorService.this.activeCarSignalEpoch) {
                LightSensorService.this.forceInitCarSignalEpoch = 0L;
                if (LightSensorService.MANUAL_AUTO_GATE.blocksAntiAuto()) {
                    Log.i(LightSensorService.TAG, "force-init: OEM Auto выбран с руля — инициализация отменена");
                    LightSensorService.this.forceInitCompleted = true;
                    return;
                }
                LightSensorService lightSensorService = LightSensorService.this;
                if (lightSensorService.reasonToDesired(lightSensorService.lastReason) != null) {
                    LightSensorService lightSensorService2 = LightSensorService.this;
                    lightSensorService2.forceInitCompleted = lightSensorService2.applyTargetWithSensorLevel("force-init", -1, true);
                } else {
                    LightSensorService.this.requestSensorLevelForApply(j, SensorApplyMode.FORCE, "force-init", true);
                }
            }
        }
    };
    private final Runnable safetyRunnable = new Runnable() { // from class: ru.big.town.anative.LightSensorService.3
        @Override // java.lang.Runnable
        public void run() {
            LightSensorService.this.requestCarSignalMaintenance();
            long j = LightSensorService.this.readyCarSignalEpoch;
            LightSensorService lightSensorService = LightSensorService.this;
            Boolean boolReasonToDesired = lightSensorService.reasonToDesired(lightSensorService.lastReason);
            if (!LightSensorService.this.everSent && boolReasonToDesired != null) {
                LightSensorService.this.applyTargetWithSensorLevel("poll-retry", -1);
            }
            if (j != 0 && j == LightSensorService.this.activeCarSignalEpoch) {
                if (!LightSensorService.this.everSent && boolReasonToDesired == null) {
                    LightSensorService.this.requestSensorLevelForApply(j, SensorApplyMode.IF_UNSENT, "poll-retry");
                } else {
                    LightSensorService.this.requestSensorLevel(j);
                }
            }
            LightSensorService.this.timerHandler.postDelayed(this, LightSensorService.SAFETY_POLL_MS);
        }
    };
    private final Runnable driveFallbackRunnable = new Runnable() { // from class: ru.big.town.anative.LightSensorService.4
        @Override // java.lang.Runnable
        public void run() {
            if (LightSensorService.MANUAL_AUTO_GATE.blocksAntiAuto()) {
                Log.i(LightSensorService.TAG, "drive+5s: OEM Auto выбран с руля — anti-Auto пропущен");
                return;
            }
            LightSensorService lightSensorService = LightSensorService.this;
            if (lightSensorService.reasonToDesired(lightSensorService.lastReason) != null) {
                LightSensorService.this.applyTargetWithSensorLevel("drive+5s (анти-Auto)", -1);
                return;
            }
            long j = LightSensorService.this.readyCarSignalEpoch;
            if (j == 0 || j != LightSensorService.this.activeCarSignalEpoch) {
                return;
            }
            LightSensorService.this.requestSensorLevelForApply(j, SensorApplyMode.FORCE, "drive+5s (анти-Auto)", true);
        }
    };
    private final Runnable canbusReassertRunnable = new Runnable() { // from class: ru.big.town.anative.LightSensorService.5
        @Override // java.lang.Runnable
        public void run() {
            if (LightSensorService.MANUAL_AUTO_GATE.blocksAntiAuto()) {
                Log.i(LightSensorService.TAG, "canbus-reset: OEM Auto выбран с руля — отмена");
                return;
            }
            if (LightSensorService.this.everSent && LightSensorService.this.headlightsOn) {
                if (LightSensorService.this.lastAutoLamp != 1) {
                    Log.i(LightSensorService.TAG, "canbus-reset: за выдержку состояние ушло из авто — отмена");
                } else {
                    Log.i(LightSensorService.TAG, "canbus-reset: выдержка прошла, BCM всё ещё в авто → возвращаем ближний");
                    LightSensorService.this.commit(true, "canbus-reset");
                }
            }
        }
    };
    private final BroadcastReceiver requestReceiver = new BroadcastReceiver() { // from class: ru.big.town.anative.LightSensorService.6
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            LightSensorService lightSensorService = LightSensorService.this;
            lightSensorService.broadcastUpdate(lightSensorService.lastSensorLevel);
        }
    };

    private enum RegistrationResult {
        SUCCESS,
        NOT_SENT,
        AMBIGUOUS
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static ThreadPoolExecutor newBoundedBinderExecutor(final String str) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue(1), new ThreadFactory() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda23
            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable runnable) {
                return LightSensorService.lambda$newBoundedBinderExecutor$0(str, runnable);
            }
        }, new ThreadPoolExecutor.AbortPolicy());
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }

    static /* synthetic */ Thread lambda$newBoundedBinderExecutor$0(String str, Runnable runnable) {
        Thread thread = new Thread(runnable, str);
        thread.setDaemon(true);
        return thread;
    }

    /* JADX INFO: Access modifiers changed from: private */
    final class CarSignalCallbackBinder extends Binder {
        final long epoch;
        final IBinder remote;
        final AtomicLong ingressRevision = new AtomicLong();
        final AtomicBoolean registrationSlotHeld = new AtomicBoolean();
        final AtomicBoolean cleanupScheduled = new AtomicBoolean();

        CarSignalCallbackBinder(long j, IBinder iBinder) {
            this.epoch = j;
            this.remote = iBinder;
        }

        @Override // android.os.Binder
        protected boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (LightSensorService.this.destroyed && i >= 1 && i <= 16777215) {
                return true;
            }
            if (i != 13) {
                if (i < 1 || i > 16777215) {
                    return super.onTransact(i, parcel, parcel2, i2);
                }
                return true;
            }
            parcel.enforceInterface(LightSensorService.CALLBACK_DESCRIPTOR);
            int i3 = parcel.readInt();
            if (!LightSensorService.this.destroyed && LightSensorService.this.activeCarSignalEpoch == this.epoch && LightSensorService.this.activeCarSignalCallback == this) {
                long jIncrementAndGet = this.ingressRevision.incrementAndGet();
                LatestIntDelivery latestIntDelivery = LightSensorService.this.sensorCallbackDelivery;
                if (latestIntDelivery != null) {
                    latestIntDelivery.offer(this.epoch, jIncrementAndGet, i3);
                }
            }
            return true;
        }
    }

    /* JADX INFO: renamed from: ru.big.town.anative.LightSensorService$7, reason: invalid class name */
    static /* synthetic */ class AnonymousClass7 {
        static final /* synthetic */ int[] $SwitchMap$ru$big$town$anative$CanBusEvent$Kind;

        static {
            int[] iArr = new int[CanBusEvent.Kind.values().length];
            $SwitchMap$ru$big$town$anative$CanBusEvent$Kind = iArr;
            try {
                iArr[CanBusEvent.Kind.LIGHT_STATUS.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$ru$big$town$anative$CanBusEvent$Kind[CanBusEvent.Kind.VEHICLE_STATE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCanBusEvent(CanBusEvent canBusEvent) {
        if (this.destroyed) {
            return;
        }
        int i = AnonymousClass7.$SwitchMap$ru$big$town$anative$CanBusEvent$Kind[canBusEvent.kind.ordinal()];
        if (i == 1) {
            onLightStatusChanged(canBusEvent.first, canBusEvent.second, canBusEvent.third);
        } else if (i == 2 && canBusEvent.first == RSM_LIGHT_SW_REASON) {
            onLightSwReason(canBusEvent.second);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void acceptSensorCallbackLevel(long j, long j2, int i) {
        CarSignalCallbackBinder carSignalCallbackBinder = this.activeCarSignalCallback;
        if (!this.destroyed && this.readyCarSignalEpoch == j && this.activeCarSignalEpoch == j && carSignalCallbackBinder != null && carSignalCallbackBinder.epoch == j && carSignalCallbackBinder.ingressRevision.get() == j2) {
            this.pendingSensorEpoch = j;
            this.pendingSensorRevision = j2;
            this.pendingSensorLevel = i;
            this.timerHandler.removeCallbacks(this.sensorDebounceRunnable);
            this.timerHandler.postDelayed(this.sensorDebounceRunnable, SENSOR_DEBOUNCE_MS);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void markCarSignalReadyOnMain(long j) {
        if (this.destroyed || this.activeCarSignalEpoch != j) {
            return;
        }
        this.readyCarSignalEpoch = j;
        this.lastSensorEpoch = 0L;
        this.lastSensorRevision = 0L;
        this.lastSensorLevel = -1;
        this.pendingSensorEpoch = 0L;
        this.pendingSensorRevision = 0L;
        this.pendingSensorLevel = -1;
        this.timerHandler.removeCallbacks(this.sensorDebounceRunnable);
        this.timerHandler.removeCallbacks(this.forceInitRunnable);
        if (this.forceInitCompleted) {
            return;
        }
        this.forceInitCarSignalEpoch = j;
        this.timerHandler.postDelayed(this.forceInitRunnable, FORCE_INIT_MS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: invalidateCarSignalOnMain, reason: merged with bridge method [inline-methods] */
    public void m1966x10662c83(long j) {
        if (this.readyCarSignalEpoch != j) {
            return;
        }
        this.readyCarSignalEpoch = 0L;
        this.forceInitCarSignalEpoch = 0L;
        this.lastSensorEpoch = 0L;
        this.lastSensorRevision = 0L;
        this.lastSensorLevel = -1;
        this.pendingSensorEpoch = 0L;
        this.pendingSensorRevision = 0L;
        this.pendingSensorLevel = -1;
        if (this.pendingMainSensorApply != null && this.pendingMainSensorApply.epoch == j) {
            this.pendingMainSensorApply = null;
        }
        SettingsSnapshot settingsSnapshot = this.pendingSettingsSnapshot;
        if (settingsSnapshot != null && settingsSnapshot.request.epoch == j) {
            this.pendingSettingsSnapshot = null;
        }
        this.timerHandler.removeCallbacks(this.forceInitRunnable);
        this.timerHandler.removeCallbacks(this.sensorDebounceRunnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestSensorLevel(final long j) {
        Handler handler = this.carSignalIoHandler;
        if (this.destroyed || handler == null || this.readyCarSignalEpoch != j) {
            return;
        }
        handler.post(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1973x936ad25d(j);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$requestSensorLevel$1$ru-big-town-anative-LightSensorService, reason: not valid java name */
    /* synthetic */ void m1973x936ad25d(long j) {
        if (!this.destroyed && this.carSignalConnected && this.carSignalEpoch == j) {
            m1974x42c96c97(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestSensorLevelForApply(long j, SensorApplyMode sensorApplyMode, String str) {
        requestSensorLevelForApply(j, sensorApplyMode, str, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestSensorLevelForApply(long j, SensorApplyMode sensorApplyMode, String str, boolean z) {
        if (!this.destroyed && this.readyCarSignalEpoch == j && this.activeCarSignalEpoch == j) {
            final SensorApplyRequest sensorApplyRequest = this.pendingMainSensorApply;
            if (sensorApplyRequest == null || sensorApplyRequest.epoch != j || sensorApplyRequest.mode.priority < sensorApplyMode.priority) {
                long j2 = this.nextSensorApplyGeneration + 1;
                this.nextSensorApplyGeneration = j2;
                SensorApplyRequest sensorApplyRequest2 = new SensorApplyRequest(j, j2, sensorApplyMode, str, z);
                this.pendingMainSensorApply = sensorApplyRequest2;
                sensorApplyRequest = sensorApplyRequest2;
            }
            Handler handler = this.carSignalIoHandler;
            if (handler != null) {
                handler.post(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1974x42c96c97(sensorApplyRequest);
                    }
                });
            }
        }
    }

    private enum SensorApplyMode {
        IF_UNSENT(1),
        FORCE(2);

        final int priority;

        SensorApplyMode(int i) {
            this.priority = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class SensorApplyRequest {
        final boolean cancelOnManualAuto;
        final long epoch;
        final long generation;
        final SensorApplyMode mode;
        final String reason;

        SensorApplyRequest(long j, long j2, SensorApplyMode sensorApplyMode, String str, boolean z) {
            this.epoch = j;
            this.generation = j2;
            this.mode = sensorApplyMode;
            this.reason = str;
            this.cancelOnManualAuto = z;
        }
    }

    private static final class SettingsSnapshot {
        final SensorApplyRequest request;
        final SensorSampleFence sensorFence;
        final LightThresholds thresholds;

        SettingsSnapshot(SensorApplyRequest sensorApplyRequest, LightThresholds lightThresholds, long j, long j2) {
            this.request = sensorApplyRequest;
            this.thresholds = lightThresholds;
            this.sensorFence = new SensorSampleFence(j, j2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class SensorQueryRun {
        final SensorApplyRequest apply;
        final IBinder binder;
        final CarSignalCallbackBinder callback;
        final long epoch;
        final long ingressRevision;
        final long settingsGeneration;

        SensorQueryRun(IBinder iBinder, CarSignalCallbackBinder carSignalCallbackBinder, long j, long j2, SensorApplyRequest sensorApplyRequest, long j3) {
            this.binder = iBinder;
            this.callback = carSignalCallbackBinder;
            this.epoch = j;
            this.ingressRevision = j2;
            this.apply = sensorApplyRequest;
            this.settingsGeneration = j3;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    final class CarSignalConnection implements ServiceConnection {
        private final long generation;

        CarSignalConnection(long j) {
            this.generation = j;
        }

        private boolean isCurrent() {
            return LightSensorService.this.carSignalConnection == this && LightSensorService.this.activeCarSignalBindingGeneration == this.generation;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (!isCurrent() || LightSensorService.this.destroyed) {
                return;
            }
            LightSensorService.this.carSignalIoHandler.removeCallbacks(LightSensorService.this.carSignalRebindRunnable);
            LightSensorService.this.carSignalBindingRequested = true;
            LightSensorService.this.carSignalBinder = iBinder;
            LightSensorService.this.carSignalConnected = true;
            LightSensorService.this.callbackRegistered = false;
            LightSensorService.this.callbackRegistrationInFlight = false;
            LightSensorService lightSensorService = LightSensorService.this;
            final long j = lightSensorService.carSignalEpoch + 1;
            lightSensorService.carSignalEpoch = j;
            LightSensorService.this.carSignalCallbackBinder = LightSensorService.this.new CarSignalCallbackBinder(j, iBinder);
            LightSensorService lightSensorService2 = LightSensorService.this;
            lightSensorService2.activeCarSignalCallback = lightSensorService2.carSignalCallbackBinder;
            LightSensorService.this.activeCarSignalEpoch = j;
            Log.i(LightSensorService.TAG, "CarSignalService connected, alive=" + iBinder.isBinderAlive());
            LightSensorService.this.timerHandler.post(new Runnable() { // from class: ru.big.town.anative.LightSensorService$CarSignalConnection$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1980xca18d05c(j);
                }
            });
            LightSensorService.this.m1974x42c96c97(null);
            LightSensorService.this.startRegisterCallbackOnIo();
        }

        /* JADX INFO: renamed from: lambda$onServiceConnected$0$ru-big-town-anative-LightSensorService$CarSignalConnection, reason: not valid java name */
        /* synthetic */ void m1980xca18d05c(long j) {
            LightSensorService.this.markCarSignalReadyOnMain(j);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            if (isCurrent()) {
                OldCarSignalSession oldCarSignalSessionInvalidateCarSignalRemoteOnIo = LightSensorService.this.invalidateCarSignalRemoteOnIo();
                if (oldCarSignalSessionInvalidateCarSignalRemoteOnIo.registered) {
                    LightSensorService.this.scheduleUnregister(oldCarSignalSessionInvalidateCarSignalRemoteOnIo.remote, oldCarSignalSessionInvalidateCarSignalRemoteOnIo.callback);
                }
                Log.w(LightSensorService.TAG, "CarSignalService disconnected — waiting for automatic reconnect");
            }
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            if (isCurrent()) {
                LightSensorService.this.restartCarSignalBindingOnIo("binding died");
            }
        }

        @Override // android.content.ServiceConnection
        public void onNullBinding(ComponentName componentName) {
            if (isCurrent()) {
                LightSensorService.this.restartCarSignalBindingOnIo("null binding");
            }
        }
    }

    private static final class OldCarSignalSession {
        final CarSignalCallbackBinder callback;
        final long epoch;
        final boolean registered;
        final IBinder remote;

        OldCarSignalSession(IBinder iBinder, CarSignalCallbackBinder carSignalCallbackBinder, boolean z, long j) {
            this.remote = iBinder;
            this.callback = carSignalCallbackBinder;
            this.registered = z;
            this.epoch = j;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class RegisterRequest {
        final CarSignalCallbackBinder callback;
        final long epoch;
        final IBinder remote;

        RegisterRequest(IBinder iBinder, CarSignalCallbackBinder carSignalCallbackBinder, long j) {
            this.remote = iBinder;
            this.callback = carSignalCallbackBinder;
            this.epoch = j;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestCarSignalMaintenance() {
        if (this.destroyed || !this.carSignalMaintenancePosted.compareAndSet(false, true)) {
            return;
        }
        Handler handler = this.carSignalIoHandler;
        if (handler == null || !handler.post(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1972x503396a();
            }
        })) {
            this.carSignalMaintenancePosted.set(false);
        }
    }

    /* JADX INFO: renamed from: lambda$requestCarSignalMaintenance$3$ru-big-town-anative-LightSensorService, reason: not valid java name */
    /* synthetic */ void m1972x503396a() {
        try {
            if (!this.destroyed) {
                ensureBound();
                startRegisterCallbackOnIo();
            }
        } finally {
            this.carSignalMaintenancePosted.set(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ensureBound() {
        if (this.destroyed || this.carSignalBindingRequested) {
            return;
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        if (jElapsedRealtime - this.lastBindAttempt < 5000) {
            return;
        }
        this.lastBindAttempt = jElapsedRealtime;
        long j = this.nextCarSignalBindingGeneration + 1;
        this.nextCarSignalBindingGeneration = j;
        CarSignalConnection carSignalConnection = new CarSignalConnection(j);
        this.carSignalConnection = carSignalConnection;
        this.activeCarSignalBindingGeneration = j;
        try {
            Intent intent = new Intent(CAR_SIGNAL_ACTION);
            intent.setPackage(CAR_SIGNAL_PACKAGE);
            boolean zBindService = bindService(intent, 1, this.carSignalIoExecutor, carSignalConnection);
            this.carSignalBindingRequested = zBindService;
            Log.i(TAG, "ensureBound: bindService returned " + zBindService);
            if (zBindService) {
                return;
            }
            this.carSignalConnection = null;
            this.activeCarSignalBindingGeneration = 0L;
            scheduleCarSignalRebindOnIo();
        } catch (Exception e) {
            this.carSignalBindingRequested = false;
            this.carSignalConnection = null;
            this.activeCarSignalBindingGeneration = 0L;
            Log.e(TAG, "ensureBound: exception: " + e.getMessage(), e);
            scheduleCarSignalRebindOnIo();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public OldCarSignalSession invalidateCarSignalRemoteOnIo() {
        final long j = this.carSignalEpoch;
        OldCarSignalSession oldCarSignalSession = new OldCarSignalSession(this.carSignalBinder, this.carSignalCallbackBinder, this.callbackRegistered, j);
        this.activeCarSignalEpoch = 0L;
        this.activeCarSignalCallback = null;
        this.carSignalBinder = null;
        this.carSignalCallbackBinder = null;
        this.carSignalConnected = false;
        this.callbackRegistered = false;
        this.callbackRegistrationInFlight = false;
        RegisterRequest registerRequest = this.pendingRegistration;
        if (registerRequest != null && registerRequest.callback == oldCarSignalSession.callback) {
            releaseRegistrationSlot(this.pendingRegistration.callback);
            this.pendingRegistration = null;
        }
        SensorApplyRequest sensorApplyRequest = this.pendingIoSensorApply;
        if (sensorApplyRequest != null && sensorApplyRequest.epoch == j) {
            this.pendingIoSensorApply = null;
        }
        SensorApplyRequest sensorApplyRequest2 = this.pendingIoSettingsRequest;
        if (sensorApplyRequest2 != null && sensorApplyRequest2.epoch == j) {
            this.pendingIoSettingsRequest = null;
            this.pendingIoSettingsGeneration = 0L;
        }
        this.sensorQueryRequested = false;
        this.carSignalEpoch++;
        this.timerHandler.post(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda22
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1966x10662c83(j);
            }
        });
        return oldCarSignalSession;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restartCarSignalBindingOnIo(String str) {
        Log.w(TAG, "CarSignalService " + str + " — replacing binding");
        releaseCarSignalBindingOnIo(str);
        scheduleCarSignalRebindOnIo();
    }

    private void scheduleCarSignalRebindOnIo() {
        if (this.destroyed) {
            return;
        }
        this.lastBindAttempt = SystemClock.elapsedRealtime();
        this.carSignalIoHandler.removeCallbacks(this.carSignalRebindRunnable);
        this.carSignalIoHandler.postDelayed(this.carSignalRebindRunnable, 5000L);
    }

    private void releaseCarSignalBindingOnIo(String str) {
        this.carSignalIoHandler.removeCallbacks(this.carSignalRebindRunnable);
        ServiceConnection serviceConnection = this.carSignalConnection;
        boolean z = this.carSignalBindingRequested;
        OldCarSignalSession oldCarSignalSessionInvalidateCarSignalRemoteOnIo = invalidateCarSignalRemoteOnIo();
        this.carSignalBindingRequested = false;
        this.carSignalConnection = null;
        this.activeCarSignalBindingGeneration = 0L;
        if (z && serviceConnection != null) {
            try {
                unbindService(serviceConnection);
            } catch (Exception e) {
                Log.w(TAG, str + ": CarSignal unbindService failed: " + e.getMessage());
            }
        }
        if (oldCarSignalSessionInvalidateCarSignalRemoteOnIo.registered) {
            scheduleUnregister(oldCarSignalSessionInvalidateCarSignalRemoteOnIo.remote, oldCarSignalSessionInvalidateCarSignalRemoteOnIo.callback);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startRegisterCallbackOnIo() {
        CarSignalCallbackBinder carSignalCallbackBinder;
        if (this.destroyed || !this.carSignalConnected || this.carSignalBinder == null || (carSignalCallbackBinder = this.carSignalCallbackBinder) == null || this.callbackRegistered || this.callbackRegistrationInFlight || carSignalCallbackBinder.cleanupScheduled.get()) {
            return;
        }
        IBinder iBinder = this.carSignalBinder;
        CarSignalCallbackBinder carSignalCallbackBinder2 = this.carSignalCallbackBinder;
        long j = this.carSignalEpoch;
        if (!reserveRegistrationSlot(carSignalCallbackBinder2)) {
            Log.w(TAG, "registerCallback deferred: cleanup backpressure");
            return;
        }
        this.callbackRegistrationInFlight = true;
        this.pendingRegistration = new RegisterRequest(iBinder, carSignalCallbackBinder2, j);
        startNextRegistrationOnIo();
    }

    private void startNextRegistrationOnIo() {
        final RegisterRequest registerRequest;
        if (this.runningRegistration != null || (registerRequest = this.pendingRegistration) == null) {
            return;
        }
        this.pendingRegistration = null;
        this.runningRegistration = registerRequest;
        try {
            CAR_SIGNAL_REGISTRATION_EXECUTOR.execute(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1977xdb42e09a(registerRequest);
                }
            });
        } catch (RejectedExecutionException unused) {
            this.runningRegistration = null;
            releaseRegistrationSlot(registerRequest.callback);
            if (registerRequest.callback == this.carSignalCallbackBinder) {
                this.callbackRegistrationInFlight = false;
            }
        }
    }

    /* JADX INFO: renamed from: lambda$startNextRegistrationOnIo$6$ru-big-town-anative-LightSensorService, reason: not valid java name */
    /* synthetic */ void m1977xdb42e09a(final RegisterRequest registerRequest) {
        RegistrationResult registrationResultRegisterCallbackTransaction = registerCallbackTransaction(registerRequest.remote, registerRequest.callback);
        final boolean z = registrationResultRegisterCallbackTransaction == RegistrationResult.SUCCESS;
        if (registrationResultRegisterCallbackTransaction == RegistrationResult.NOT_SENT) {
            releaseRegistrationSlot(registerRequest.callback);
        } else if (registrationResultRegisterCallbackTransaction == RegistrationResult.AMBIGUOUS) {
            scheduleUnregister(registerRequest.remote, registerRequest.callback);
        }
        Handler handler = this.carSignalIoHandler;
        if ((handler == null || !handler.post(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda28
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1976x8424efbb(registerRequest, z);
            }
        })) && z) {
            scheduleUnregister(registerRequest.remote, registerRequest.callback);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r5v10 */
    /* JADX WARN: Type inference failed for: r5v12 */
    /* JADX WARN: Type inference failed for: r5v13 */
    private RegistrationResult registerCallbackTransaction(IBinder iBinder, IBinder iBinder2) {
        String str = TAG;
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        boolean z = false;
        try {
            try {
                parcelObtain.writeInterfaceToken(CAR_SIGNAL_DESCRIPTOR);
                parcelObtain.writeStrongBinder(iBinder2);
                try {
                    if (iBinder.transact(46, parcelObtain, parcelObtain2, 0)) {
                        parcelObtain2.readException();
                        Log.i(TAG, "registerCallback: OK (TX=46)");
                        str = RegistrationResult.SUCCESS;
                    } else {
                        str = RegistrationResult.NOT_SENT;
                    }
                } catch (RemoteException | RuntimeException e) {
                    e = e;
                    z = true;
                    Log.w(str, "registerCallback: error: " + e.getMessage());
                    str = (z && iBinder.isBinderAlive()) ? RegistrationResult.AMBIGUOUS : RegistrationResult.NOT_SENT;
                }
            } finally {
                parcelObtain.recycle();
                parcelObtain2.recycle();
            }
        } catch (RemoteException | RuntimeException e2) {
            e = e2;
        }
        return str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: finishRegisterCallbackOnIo, reason: merged with bridge method [inline-methods] */
    public void m1976x8424efbb(RegisterRequest registerRequest, boolean z) {
        if (this.runningRegistration != registerRequest) {
            return;
        }
        this.runningRegistration = null;
        if (!this.destroyed && registerRequest.remote == this.carSignalBinder && registerRequest.callback == this.carSignalCallbackBinder && registerRequest.epoch == this.carSignalEpoch) {
            this.callbackRegistrationInFlight = false;
            if (z) {
                this.callbackRegistered = true;
            }
        } else if (z) {
            scheduleUnregister(registerRequest.remote, registerRequest.callback);
        }
        startNextRegistrationOnIo();
    }

    private static boolean reserveRegistrationSlot(CarSignalCallbackBinder carSignalCallbackBinder) {
        if (!carSignalCallbackBinder.registrationSlotHeld.compareAndSet(false, true)) {
            return true;
        }
        AtomicInteger atomicInteger = OUTSTANDING_CALLBACKS;
        if (atomicInteger.incrementAndGet() <= 2) {
            return true;
        }
        atomicInteger.decrementAndGet();
        carSignalCallbackBinder.registrationSlotHeld.set(false);
        return false;
    }

    private static void releaseRegistrationSlot(CarSignalCallbackBinder carSignalCallbackBinder) {
        if (carSignalCallbackBinder == null || !carSignalCallbackBinder.registrationSlotHeld.compareAndSet(true, false)) {
            return;
        }
        OUTSTANDING_CALLBACKS.decrementAndGet();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleUnregister(final IBinder iBinder, final CarSignalCallbackBinder carSignalCallbackBinder) {
        if (iBinder == null || carSignalCallbackBinder == null || !carSignalCallbackBinder.cleanupScheduled.compareAndSet(false, true)) {
            return;
        }
        try {
            CAR_SIGNAL_CLEANUP_EXECUTOR.execute(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1975xa11f3a3f(iBinder, carSignalCallbackBinder);
                }
            });
        } catch (RejectedExecutionException unused) {
            Log.w(TAG, "unregisterCallback deferred: cleanup queue saturated");
        }
    }

    /* JADX INFO: renamed from: lambda$scheduleUnregister$7$ru-big-town-anative-LightSensorService, reason: not valid java name */
    /* synthetic */ void m1975xa11f3a3f(IBinder iBinder, CarSignalCallbackBinder carSignalCallbackBinder) {
        if (unregisterCallbackTransaction(iBinder, carSignalCallbackBinder)) {
            carSignalCallbackBinder.cleanupScheduled.set(false);
            releaseRegistrationSlot(carSignalCallbackBinder);
        } else {
            Log.w(TAG, "unregisterCallback not confirmed; registration gate remains closed");
        }
    }

    private boolean unregisterCallbackTransaction(IBinder iBinder, IBinder iBinder2) {
        boolean zIsBinderAlive;
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken(CAR_SIGNAL_DESCRIPTOR);
                parcelObtain.writeStrongBinder(iBinder2);
                if (!iBinder.transact(47, parcelObtain, parcelObtain2, 0)) {
                    zIsBinderAlive = iBinder.isBinderAlive();
                    return !zIsBinderAlive;
                }
                parcelObtain2.readException();
                Log.i(TAG, "unregisterCallback: OK");
                return true;
            } catch (RemoteException | RuntimeException e) {
                Log.w(TAG, "unregisterCallback: error: " + e.getMessage());
                zIsBinderAlive = iBinder.isBinderAlive();
            }
        } finally {
            parcelObtain.recycle();
            parcelObtain2.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: requestSensorLevelOnIo, reason: merged with bridge method [inline-methods] */
    public void m1974x42c96c97(SensorApplyRequest sensorApplyRequest) {
        if (this.destroyed || !this.carSignalConnected || this.carSignalBinder == null || this.carSignalCallbackBinder == null) {
            return;
        }
        if (sensorApplyRequest != null) {
            if (sensorApplyRequest.epoch != this.carSignalEpoch) {
                return;
            }
            SensorApplyRequest sensorApplyRequest2 = this.pendingIoSensorApply;
            if (sensorApplyRequest2 == null || sensorApplyRequest2.epoch != sensorApplyRequest.epoch || sensorApplyRequest2.mode.priority < sensorApplyRequest.mode.priority || (sensorApplyRequest2.mode == sensorApplyRequest.mode && sensorApplyRequest2.generation < sensorApplyRequest.generation)) {
                this.pendingIoSensorApply = sensorApplyRequest;
            }
        }
        this.sensorQueryRequested = true;
        startSensorLevelQueryOnIo();
    }

    private void startSensorLevelQueryOnIo() {
        if (this.destroyed || this.sensorQueryRunning || !this.sensorQueryRequested) {
            return;
        }
        final IBinder iBinder = this.carSignalBinder;
        CarSignalCallbackBinder carSignalCallbackBinder = this.carSignalCallbackBinder;
        long j = this.carSignalEpoch;
        if (!this.carSignalConnected || iBinder == null || carSignalCallbackBinder == null) {
            return;
        }
        this.sensorQueryRequested = false;
        SensorApplyRequest sensorApplyRequest = this.pendingIoSensorApply;
        SensorApplyRequest sensorApplyRequest2 = (sensorApplyRequest == null || sensorApplyRequest.epoch == j) ? sensorApplyRequest : null;
        final SensorQueryRun sensorQueryRun = new SensorQueryRun(iBinder, carSignalCallbackBinder, j, carSignalCallbackBinder.ingressRevision.get(), sensorApplyRequest2, this.pendingIoSettingsRequest == sensorApplyRequest2 ? this.pendingIoSettingsGeneration : 0L);
        this.sensorQueryRunning = true;
        this.runningSensorQuery = sensorQueryRun;
        try {
            CAR_SIGNAL_QUERY_EXECUTOR.execute(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1979x972890c5(iBinder, sensorQueryRun);
                }
            });
        } catch (RejectedExecutionException unused) {
            this.sensorQueryRunning = false;
            this.runningSensorQuery = null;
            this.sensorQueryRequested = true;
        }
    }

    /* JADX INFO: renamed from: lambda$startSensorLevelQueryOnIo$9$ru-big-town-anative-LightSensorService, reason: not valid java name */
    /* synthetic */ void m1979x972890c5(IBinder iBinder, final SensorQueryRun sensorQueryRun) {
        final int sensorLevelOnQueryThread = readSensorLevelOnQueryThread(iBinder);
        Handler handler = this.carSignalIoHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda20
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1978x400a9fe6(sensorQueryRun, sensorLevelOnQueryThread);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: finishSensorLevelQueryOnIo, reason: merged with bridge method [inline-methods] */
    public void m1978x400a9fe6(final SensorQueryRun sensorQueryRun, final int i) {
        if (this.sensorQueryRunning && this.runningSensorQuery == sensorQueryRun) {
            if (!this.destroyed && this.carSignalConnected && sensorQueryRun.binder == this.carSignalBinder && sensorQueryRun.callback == this.carSignalCallbackBinder && sensorQueryRun.epoch == this.carSignalEpoch && sensorQueryRun.callback.ingressRevision.get() == sensorQueryRun.ingressRevision && i >= 0 && this.timerHandler.post(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1964xbf55d3b2(sensorQueryRun, i);
                }
            })) {
                return;
            }
            this.sensorQueryRunning = false;
            this.runningSensorQuery = null;
            if (this.sensorQueryRequested) {
                startSensorLevelQueryOnIo();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: acceptSensorQueryResultOnMain, reason: merged with bridge method [inline-methods] */
    public void m1964xbf55d3b2(final SensorQueryRun sensorQueryRun, int i) {
        final boolean zApplySensorRequest = false;
        if (!this.destroyed && this.readyCarSignalEpoch == sensorQueryRun.epoch && this.activeCarSignalEpoch == sensorQueryRun.epoch && this.activeCarSignalCallback == sensorQueryRun.callback && sensorQueryRun.callback.ingressRevision.get() == sensorQueryRun.ingressRevision) {
            onSensorLevel(sensorQueryRun.epoch, sensorQueryRun.ingressRevision, i, "poll");
            SensorApplyRequest sensorApplyRequest = sensorQueryRun.apply;
            if (sensorApplyRequest != null && this.pendingMainSensorApply == sensorApplyRequest && (zApplySensorRequest = applySensorRequest(sensorApplyRequest, i, sensorQueryRun.ingressRevision, sensorQueryRun.settingsGeneration))) {
                this.pendingMainSensorApply = null;
            }
        }
        Handler handler = this.carSignalIoHandler;
        if (handler == null || handler.post(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1960xedd937dc(sensorQueryRun, zApplySensorRequest);
            }
        })) {
            return;
        }
        Log.w(TAG, "TX36 completion dropped: CarSignal IO stopped");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: finishSensorQueryAfterMainOnIo, reason: merged with bridge method [inline-methods] */
    public void m1960xedd937dc(SensorQueryRun sensorQueryRun, boolean z) {
        if (this.sensorQueryRunning && this.runningSensorQuery == sensorQueryRun) {
            if (z && this.pendingIoSensorApply == sensorQueryRun.apply) {
                this.pendingIoSensorApply = null;
                if (this.pendingIoSettingsRequest == sensorQueryRun.apply) {
                    this.pendingIoSettingsRequest = null;
                    this.pendingIoSettingsGeneration = 0L;
                }
            }
            this.sensorQueryRunning = false;
            this.runningSensorQuery = null;
            if (this.sensorQueryRequested) {
                startSensorLevelQueryOnIo();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void acknowledgeSensorApplyFromCallback(final long j, final long j2) {
        Handler handler = this.carSignalIoHandler;
        if (handler == null) {
            return;
        }
        handler.post(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1961x79988c41(j, j2);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$acknowledgeSensorApplyFromCallback$12$ru-big-town-anative-LightSensorService, reason: not valid java name */
    /* synthetic */ void m1961x79988c41(long j, long j2) {
        SensorApplyRequest sensorApplyRequest = this.pendingIoSensorApply;
        if (sensorApplyRequest != null && sensorApplyRequest.epoch == j && sensorApplyRequest.generation == j2) {
            this.pendingIoSensorApply = null;
            if (this.pendingIoSettingsRequest == sensorApplyRequest) {
                this.pendingIoSettingsRequest = null;
                this.pendingIoSettingsGeneration = 0L;
            }
        }
    }

    private void requestSettingsForApply(SensorApplyRequest sensorApplyRequest) {
        if (isSettingsRequestCurrentOnMain(sensorApplyRequest)) {
            LightSettingsPolicy.Decision decision = settingsDecision(sensorApplyRequest);
            if (decision != LightSettingsPolicy.Decision.NEED_THRESHOLDS) {
                resolveSettingsRequestOnMain(sensorApplyRequest, decision);
                return;
            }
            SensorApplyRequest sensorApplyRequestOffer = this.settingsRequestGate.offer(sensorApplyRequest);
            if (sensorApplyRequestOffer != null) {
                submitSettingsQuery(sensorApplyRequestOffer);
            }
        }
    }

    private void submitSettingsQuery(final SensorApplyRequest sensorApplyRequest) {
        if (!isSettingsRequestCurrentOnMain(sensorApplyRequest)) {
            dropSettingsQueryOnMain(sensorApplyRequest);
            return;
        }
        if (settingsDecision(sensorApplyRequest) != LightSettingsPolicy.Decision.NEED_THRESHOLDS) {
            finishSettingsWithoutQueryOnMain(sensorApplyRequest);
            return;
        }
        final ContentResolver contentResolver = getApplicationContext().getContentResolver();
        final WeakReference weakReference = new WeakReference(this);
        try {
            LIGHT_SETTINGS_EXECUTOR.execute(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda21
                @Override // java.lang.Runnable
                public final void run() {
                    LightSensorService.lambda$submitSettingsQuery$16(weakReference, sensorApplyRequest, contentResolver);
                }
            });
        } catch (RejectedExecutionException unused) {
            this.settingsRequestGate.reject(sensorApplyRequest);
            scheduleSettingsRetry();
        }
    }

    static /* synthetic */ void lambda$submitSettingsQuery$16(WeakReference weakReference, final SensorApplyRequest sensorApplyRequest, ContentResolver contentResolver) {
        Handler handler;
        Handler handler2;
        final LightSensorService lightSensorService = (LightSensorService) weakReference.get();
        if (lightSensorService == null || lightSensorService.destroyed || lightSensorService.pendingMainSensorApply != sensorApplyRequest || lightSensorService.activeCarSignalEpoch != sensorApplyRequest.epoch) {
            if (lightSensorService == null || (handler = lightSensorService.timerHandler) == null) {
                return;
            }
            handler.post(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda25
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.dropSettingsQueryOnMain(sensorApplyRequest);
                }
            });
            return;
        }
        if (lightSensorService.settingsDecision(sensorApplyRequest) != LightSettingsPolicy.Decision.NEED_THRESHOLDS) {
            Handler handler3 = lightSensorService.timerHandler;
            if (handler3 != null) {
                handler3.post(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda26
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.finishSettingsWithoutQueryOnMain(sensorApplyRequest);
                    }
                });
                return;
            }
            return;
        }
        final LightThresholds lightThresholdsQueryThresholds = queryThresholds(contentResolver);
        final LightSensorService lightSensorService2 = (LightSensorService) weakReference.get();
        if (lightSensorService2 == null || (handler2 = lightSensorService2.timerHandler) == null) {
            return;
        }
        handler2.post(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda27
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.finishSettingsQueryOnMain(sensorApplyRequest, lightThresholdsQueryThresholds);
            }
        });
    }

    private boolean isSettingsRequestCurrentOnMain(SensorApplyRequest sensorApplyRequest) {
        return !this.destroyed && this.pendingMainSensorApply == sensorApplyRequest && sensorApplyRequest.epoch == this.readyCarSignalEpoch && sensorApplyRequest.epoch == this.activeCarSignalEpoch;
    }

    private LightSettingsPolicy.Decision settingsDecision(SensorApplyRequest sensorApplyRequest) {
        return LightSettingsPolicy.decide(sensorApplyRequest.cancelOnManualAuto, MANUAL_AUTO_GATE.blocksAntiAuto(), sensorApplyRequest.mode == SensorApplyMode.IF_UNSENT, this.everSent, reasonToDesired(this.lastReason) != null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishSettingsWithoutQueryOnMain(SensorApplyRequest sensorApplyRequest) {
        LatestRequestGate.Completion<SensorApplyRequest> completionFinish = this.settingsRequestGate.finish(sensorApplyRequest);
        if (completionFinish.publish && isSettingsRequestCurrentOnMain(sensorApplyRequest)) {
            resolveSettingsRequestOnMain(sensorApplyRequest, settingsDecision(sensorApplyRequest));
        }
        if (completionFinish.next != null) {
            submitSettingsQuery(completionFinish.next);
        }
    }

    private void resolveSettingsRequestOnMain(SensorApplyRequest sensorApplyRequest, LightSettingsPolicy.Decision decision) {
        if (isSettingsRequestCurrentOnMain(sensorApplyRequest)) {
            if (decision == LightSettingsPolicy.Decision.NEED_THRESHOLDS) {
                requestSettingsForApply(sensorApplyRequest);
                return;
            }
            if (applySensorRequest(sensorApplyRequest, -1, 0L, 0L) && this.pendingMainSensorApply == sensorApplyRequest) {
                this.pendingMainSensorApply = null;
                SettingsSnapshot settingsSnapshot = this.pendingSettingsSnapshot;
                if (settingsSnapshot != null && settingsSnapshot.request == sensorApplyRequest) {
                    this.pendingSettingsSnapshot = null;
                }
                acknowledgeSensorApplyFromCallback(sensorApplyRequest.epoch, sensorApplyRequest.generation);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dropSettingsQueryOnMain(SensorApplyRequest sensorApplyRequest) {
        LatestRequestGate.Completion<SensorApplyRequest> completionFinish = this.settingsRequestGate.finish(sensorApplyRequest);
        if (completionFinish.next != null) {
            submitSettingsQuery(completionFinish.next);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishSettingsQueryOnMain(final SensorApplyRequest sensorApplyRequest, LightThresholds lightThresholds) {
        LatestRequestGate.Completion<SensorApplyRequest> completionFinish = this.settingsRequestGate.finish(sensorApplyRequest);
        if (completionFinish.publish && isSettingsRequestCurrentOnMain(sensorApplyRequest)) {
            LightSettingsPolicy.Decision decision = settingsDecision(sensorApplyRequest);
            if (decision != LightSettingsPolicy.Decision.NEED_THRESHOLDS) {
                resolveSettingsRequestOnMain(sensorApplyRequest, decision);
            } else {
                CarSignalCallbackBinder carSignalCallbackBinder = this.activeCarSignalCallback;
                long j = (carSignalCallbackBinder == null || carSignalCallbackBinder.epoch != sensorApplyRequest.epoch) ? Long.MAX_VALUE : carSignalCallbackBinder.ingressRevision.get();
                final long j2 = this.nextSettingsSnapshotGeneration + 1;
                this.nextSettingsSnapshotGeneration = j2;
                this.pendingSettingsSnapshot = new SettingsSnapshot(sensorApplyRequest, lightThresholds, j2, j);
                Handler handler = this.carSignalIoHandler;
                if (handler != null) {
                    handler.post(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda14
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.m1965xe9c87265(sensorApplyRequest, j2);
                        }
                    });
                }
            }
        }
        if (completionFinish.next != null) {
            submitSettingsQuery(completionFinish.next);
        }
    }

    /* JADX INFO: renamed from: lambda$finishSettingsQueryOnMain$17$ru-big-town-anative-LightSensorService, reason: not valid java name */
    /* synthetic */ void m1965xe9c87265(SensorApplyRequest sensorApplyRequest, long j) {
        if (!this.destroyed && sensorApplyRequest.epoch == this.carSignalEpoch && this.pendingIoSensorApply == sensorApplyRequest) {
            this.pendingIoSettingsRequest = sensorApplyRequest;
            this.pendingIoSettingsGeneration = j;
            m1974x42c96c97(sensorApplyRequest);
        }
    }

    private void scheduleSettingsRetry() {
        if (this.destroyed) {
            return;
        }
        this.timerHandler.removeCallbacks(this.settingsRetryRunnable);
        this.timerHandler.postDelayed(this.settingsRetryRunnable, 5000L);
    }

    /* JADX INFO: renamed from: lambda$new$18$ru-big-town-anative-LightSensorService, reason: not valid java name */
    /* synthetic */ void m1968lambda$new$18$rubigtownanativeLightSensorService() {
        SensorApplyRequest sensorApplyRequestRetry;
        if (this.destroyed || (sensorApplyRequestRetry = this.settingsRequestGate.retry()) == null) {
            return;
        }
        submitSettingsQuery(sensorApplyRequestRetry);
    }

    private int readSensorLevelOnQueryThread(IBinder iBinder) {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken(CAR_SIGNAL_DESCRIPTOR);
                if (iBinder.transact(36, parcelObtain, parcelObtain2, 0)) {
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                }
            } catch (RemoteException | RuntimeException e) {
                Log.w(TAG, "readSensorLevel: error: " + e.getMessage());
            }
            return -1;
        } finally {
            parcelObtain.recycle();
            parcelObtain2.recycle();
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        Log.i(TAG, "onCreate() — LightSensorService (event-driven + safety-poll)");
        Log.i(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        this.timerHandler = new Handler(Looper.getMainLooper());
        HandlerThread handlerThread = new HandlerThread("CarSignalIo");
        this.carSignalIoThread = handlerThread;
        handlerThread.start();
        this.carSignalIoHandler = new Handler(this.carSignalIoThread.getLooper());
        this.carSignalIoExecutor = new Executor() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda7
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                this.f$0.m1969lambda$onCreate$19$rubigtownanativeLightSensorService(runnable);
            }
        };
        this.sensorCallbackDelivery = new LatestIntDelivery(new Executor() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda8
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                this.f$0.m1970lambda$onCreate$20$rubigtownanativeLightSensorService(runnable);
            }
        }, new LatestIntDelivery.Listener() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda9
            @Override // ru.big.town.anative.LatestIntDelivery.Listener
            public final void accept(long j, long j2, int i) {
                this.f$0.acceptSensorCallbackLevel(j, j2, i);
            }
        });
        HeadlightCanTransport.initialize(this);
        createNotificationChannel();
        startForeground(2, new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("Автосвет").setContentText("Управление фарами активно").setSmallIcon(R.drawable.ic_launcher_foreground).build());
        registerReceiver(this.requestReceiver, new IntentFilter("ru.big.town.anative.REQUEST_LUX_UPDATE"), 2);
        requestCarSignalMaintenance();
        this.canBusSubscription = CanBusEventHub.get(this).subscribe(24, new int[]{RSM_LIGHT_SW_REASON}, this.timerHandler, new CanBusEventHub.Listener() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda10
            @Override // ru.big.town.anative.CanBusEventHub.Listener
            public final void onCanBusEvent(CanBusEvent canBusEvent) {
                this.f$0.onCanBusEvent(canBusEvent);
            }
        });
        this.gearStateSubscription = VehicleStateControllers.get(this).gear().subscribe(this.timerHandler, new GearStateController.Listener() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda12
            @Override // ru.big.town.anative.GearStateController.Listener
            public final void onGearChanged(int i) {
                this.f$0.onGear(i);
            }
        });
        this.timerHandler.postDelayed(this.safetyRunnable, 2000L);
    }

    /* JADX INFO: renamed from: lambda$onCreate$19$ru-big-town-anative-LightSensorService, reason: not valid java name */
    /* synthetic */ void m1969lambda$onCreate$19$rubigtownanativeLightSensorService(Runnable runnable) {
        Handler handler = this.carSignalIoHandler;
        if ((handler == null || !handler.post(runnable)) && !this.destroyed) {
            Log.w(TAG, "CarSignal ServiceConnection callback dropped");
        }
    }

    /* JADX INFO: renamed from: lambda$onCreate$20$ru-big-town-anative-LightSensorService, reason: not valid java name */
    /* synthetic */ void m1970lambda$onCreate$20$rubigtownanativeLightSensorService(Runnable runnable) {
        if (!this.timerHandler.post(runnable)) {
            throw new RejectedExecutionException("main Handler stopped");
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        Log.i(TAG, "onStartCommand()");
        return 1;
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log.i(TAG, "onDestroy() — headlightsOn=" + this.headlightsOn);
        this.destroyed = true;
        this.settingsRequestGate.close();
        LatestIntDelivery latestIntDelivery = this.sensorCallbackDelivery;
        this.sensorCallbackDelivery = null;
        if (latestIntDelivery != null) {
            latestIntDelivery.close();
        }
        CanBusEventHub.Subscription subscription = this.canBusSubscription;
        this.canBusSubscription = null;
        if (subscription != null) {
            subscription.close();
        }
        GearStateController.Subscription subscription2 = this.gearStateSubscription;
        this.gearStateSubscription = null;
        if (subscription2 != null) {
            subscription2.close();
        }
        try {
            unregisterReceiver(this.requestReceiver);
        } catch (Exception unused) {
        }
        this.timerHandler.removeCallbacks(this.safetyRunnable);
        this.timerHandler.removeCallbacks(this.forceInitRunnable);
        this.timerHandler.removeCallbacks(this.sensorDebounceRunnable);
        this.timerHandler.removeCallbacks(this.canbusReassertRunnable);
        this.timerHandler.removeCallbacks(this.driveFallbackRunnable);
        this.timerHandler.removeCallbacks(this.settingsRetryRunnable);
        Handler handler = this.carSignalIoHandler;
        final HandlerThread handlerThread = this.carSignalIoThread;
        if (handler != null && handlerThread != null && !handler.post(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1971lambda$onDestroy$21$rubigtownanativeLightSensorService(handlerThread);
            }
        })) {
            handlerThread.quitSafely();
        }
        this.timerHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /* JADX INFO: renamed from: lambda$onDestroy$21$ru-big-town-anative-LightSensorService, reason: not valid java name */
    /* synthetic */ void m1971lambda$onDestroy$21$rubigtownanativeLightSensorService(HandlerThread handlerThread) {
        releaseCarSignalBindingOnIo("onDestroy");
        handlerThread.quitSafely();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSensorLevel(long j, long j2, int i, String str) {
        if (j == this.readyCarSignalEpoch && j == this.activeCarSignalEpoch) {
            this.lastSensorEpoch = j;
            this.lastSensorRevision = j2;
            this.lastSensorLevel = i;
            broadcastUpdate(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean applySensorRequest(SensorApplyRequest sensorApplyRequest, int i, long j, long j2) {
        if (sensorApplyRequest.cancelOnManualAuto && MANUAL_AUTO_GATE.blocksAntiAuto()) {
            Log.i(TAG, sensorApplyRequest.reason + ": OEM Auto выбран с руля — pending action отменён");
            if (sensorApplyRequest.mode == SensorApplyMode.FORCE) {
                this.forceInitCompleted = true;
            }
            return true;
        }
        if (sensorApplyRequest.mode == SensorApplyMode.IF_UNSENT && this.everSent) {
            return true;
        }
        LightThresholds lightThresholds = null;
        if (reasonToDesired(this.lastReason) == null) {
            SettingsSnapshot settingsSnapshot = this.pendingSettingsSnapshot;
            if (settingsSnapshot == null || settingsSnapshot.request != sensorApplyRequest) {
                requestSettingsForApply(sensorApplyRequest);
                return false;
            }
            if (!settingsSnapshot.sensorFence.accepts(j, j2)) {
                Log.i(TAG, sensorApplyRequest.reason + ": sensor sample predates thresholds — waiting");
                return false;
            }
            LightThresholds lightThresholds2 = settingsSnapshot.thresholds;
            this.pendingSettingsSnapshot = null;
            lightThresholds = lightThresholds2;
        }
        boolean zApplyTargetWithSensorLevel = applyTargetWithSensorLevel(sensorApplyRequest.reason, i, sensorApplyRequest.mode == SensorApplyMode.FORCE, lightThresholds);
        if (zApplyTargetWithSensorLevel && sensorApplyRequest.mode == SensorApplyMode.FORCE) {
            this.forceInitCompleted = true;
        }
        return zApplyTargetWithSensorLevel;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean applyTargetWithSensorLevel(String str, int i) {
        return applyTargetWithSensorLevel(str, i, false, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean applyTargetWithSensorLevel(String str, int i, boolean z) {
        return applyTargetWithSensorLevel(str, i, z, null);
    }

    private boolean applyTargetWithSensorLevel(String str, int i, boolean z, LightThresholds lightThresholds) {
        Boolean boolReasonToDesired = reasonToDesired(this.lastReason);
        String str2 = str + " ext reason=" + this.lastReason;
        if (boolReasonToDesired == null) {
            if (lightThresholds == null) {
                Log.i(TAG, str + ": thresholds pending — decision deferred");
                return false;
            }
            boolReasonToDesired = lightThresholds.desiredFor(i);
            str2 = str + " cabin level=" + i;
        }
        if (boolReasonToDesired == null && z && this.everSent) {
            boolReasonToDesired = Boolean.valueOf(this.headlightsOn);
            str2 = str + " retain=" + (this.headlightsOn ? "low" : DebugKt.DEBUG_PROPERTY_VALUE_OFF);
        }
        if (boolReasonToDesired == null) {
            Log.i(TAG, str + ": нет данных (reason=" + this.lastReason + ") — не трогаем");
            return false;
        }
        Log.i(TAG, str2 + " → " + (boolReasonToDesired.booleanValue() ? "ближний" : "выкл"));
        return commit(boolReasonToDesired.booleanValue(), str2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean commit(final boolean z, String str) {
        Log.i(TAG, "★ commit(" + (z ? "ближний" : "выкл") + ") — " + str);
        final long jBeginAutomaticDecision = MANUAL_AUTO_GATE.beginAutomaticDecision();
        if (jBeginAutomaticDecision == -1) {
            Log.i(TAG, "auto-light decision suppressed by queued manual command");
            return false;
        }
        final long j = this.commitSequence + 1;
        this.commitSequence = j;
        this.headlightsOn = z;
        this.everSent = true;
        this.lastCommitElapsed = SystemClock.elapsedRealtime();
        ApplyEngine.postWakeAction("auto light ".concat(z ? "low" : DebugKt.DEBUG_PROPERTY_VALUE_OFF), new BooleanSupplier() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda5
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                return this.f$0.m1962lambda$commit$22$rubigtownanativeLightSensorService(jBeginAutomaticDecision, z);
            }
        }, (Consumer<ApplyEngine.WakeActionResult>) new Consumer() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda6
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.m1963lambda$commit$23$rubigtownanativeLightSensorService(j, (ApplyEngine.WakeActionResult) obj);
            }
        });
        return true;
    }

    /* JADX INFO: renamed from: lambda$commit$22$ru-big-town-anative-LightSensorService, reason: not valid java name */
    /* synthetic */ boolean m1962lambda$commit$22$rubigtownanativeLightSensorService(long j, boolean z) {
        if (!MANUAL_AUTO_GATE.isAutomaticActionCurrent(j)) {
            Log.i(TAG, "auto-light action superseded by newer manual intent");
            return false;
        }
        return MainActivity.setHeadlights(this, z);
    }

    /* JADX INFO: renamed from: lambda$commit$23$ru-big-town-anative-LightSensorService, reason: not valid java name */
    /* synthetic */ void m1963lambda$commit$23$rubigtownanativeLightSensorService(long j, ApplyEngine.WakeActionResult wakeActionResult) {
        if (wakeActionResult != ApplyEngine.WakeActionResult.SUCCESS) {
            invalidateCommit(j);
        }
    }

    private void invalidateCommit(final long j) {
        if (this.destroyed) {
            return;
        }
        this.timerHandler.post(new Runnable() { // from class: ru.big.town.anative.LightSensorService$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1967xa483fd0b(j);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$invalidateCommit$24$ru-big-town-anative-LightSensorService, reason: not valid java name */
    /* synthetic */ void m1967xa483fd0b(long j) {
        if (this.destroyed || this.commitSequence != j) {
            return;
        }
        this.everSent = false;
        Log.w(TAG, "auto-light commit was cancelled/failed; safety poll will retry");
    }

    private void onLightSwReason(int i) {
        String str;
        this.lastReason = i;
        Boolean boolReasonToDesired = reasonToDesired(i);
        StringBuilder sbAppend = new StringBuilder("RSM lightSWReason=").append(i).append(" → ");
        if (boolReasonToDesired == null) {
            str = "без изменений";
        } else {
            str = boolReasonToDesired.booleanValue() ? "ближний" : "выкл";
        }
        Log.i(TAG, sbAppend.append(str).toString());
        if (boolReasonToDesired == null) {
            return;
        }
        if (this.everSent && boolReasonToDesired.booleanValue() == this.headlightsOn) {
            return;
        }
        commit(boolReasonToDesired.booleanValue(), "ext-sensor reason=" + i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Boolean reasonToDesired(int i) {
        if (i == 0) {
            return Boolean.FALSE;
        }
        if (i == 2 || i == 3 || i == 4) {
            return Boolean.TRUE;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onGear(int i) {
        if (i < 0 || i == this.lastGear) {
            return;
        }
        boolean z = i == 3;
        this.lastGear = i;
        if (z) {
            Log.i(TAG, "gear=Drive → через 5000мс выставим таргет (анти-Auto)");
            this.timerHandler.removeCallbacks(this.driveFallbackRunnable);
            this.timerHandler.postDelayed(this.driveFallbackRunnable, 5000L);
        }
    }

    private void onLightStatusChanged(int i, int i2, int i3) {
        if (i == this.lastAutoLamp && i2 == this.lastDippedBeam && i3 == this.lastHeadLight) {
            return;
        }
        this.lastAutoLamp = i;
        this.lastDippedBeam = i2;
        this.lastHeadLight = i3;
        long jElapsedRealtime = SystemClock.elapsedRealtime() - this.lastCommitElapsed;
        Log.i(TAG, "lightstatus: autoLamp=" + i + " dippedBeam=" + i2 + " headLight=" + i3 + " ourTarget=" + (this.headlightsOn ? "ближний" : "выкл") + " sinceCommit=" + jElapsedRealtime + "ms");
        this.timerHandler.removeCallbacks(this.canbusReassertRunnable);
        if (MANUAL_AUTO_GATE.blocksAntiAuto()) {
            Log.i(TAG, "lightstatus: OEM Auto выбран с руля — anti-Auto подавлен");
            return;
        }
        if (this.everSent && this.headlightsOn) {
            if (jElapsedRealtime < HEADLIGHT_GUARD_MS) {
                Log.i(TAG, "lightstatus: игнор — эхо нашей команды (" + jElapsedRealtime + "ms назад)");
            } else if (i == 1) {
                Log.i(TAG, "lightstatus: поймал АВТО при таргете ближний → выдержка 5000ms");
                this.timerHandler.postDelayed(this.canbusReassertRunnable, 5000L);
            }
        }
    }

    static ManualAutoGate.Ticket reserveManualHeadlightCommand() {
        return MANUAL_AUTO_GATE.reserveManualCommand();
    }

    static boolean setManualAutoOverride(boolean z) {
        return MANUAL_AUTO_GATE.setSelected(z);
    }

    private static LightThresholds queryThresholds(ContentResolver contentResolver) {
        int i = 3;
        int i2 = 5;
        try {
            Cursor cursorQuery = contentResolver.query(CONTENT_PROVIDER_URI, null, null, null, null);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToFirst() && cursorQuery.getColumnCount() > 10) {
                        i = cursorQuery.getInt(9);
                        i2 = cursorQuery.getInt(10);
                    }
                    cursorQuery.close();
                } catch (Throwable th) {
                    cursorQuery.close();
                    throw th;
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "queryThresholds: " + e.getMessage() + " — defaults");
        }
        if (i > i2) {
            Log.w(TAG, "queryThresholds: thresholds inverted — swapped");
        }
        return new LightThresholds(i, i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broadcastUpdate(int i) {
        Intent intent = new Intent(ACTION_LUX_UPDATE);
        intent.putExtra(EXTRA_SENSOR_LEVEL, i);
        sendBroadcast(intent);
    }

    private void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Автосвет", 2);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
