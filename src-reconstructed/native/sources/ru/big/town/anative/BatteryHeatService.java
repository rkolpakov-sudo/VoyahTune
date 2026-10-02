package ru.big.town.anative;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.os.EnvironmentCompat;
import java.lang.ref.WeakReference;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes2.dex */
public class BatteryHeatService extends Service {
    public static final String ACTION_BATTERY_HEAT_ACTIVATE = "ru.big.town.anative.BATTERY_HEAT_ACTIVATE";
    public static final String ACTION_BATTERY_HEAT_AUTO_CHANGED = "ru.big.town.anative.BATTERY_HEAT_AUTO_CHANGED";
    public static final String ACTION_BATTERY_HEAT_UPDATE = "ru.big.town.anative.BATTERY_HEAT_UPDATE";
    private static final String ACTION_PHYSICAL_WAKE_SETTINGS_REFRESH = "ru.big.town.anative.BATTERY_HEAT_PHYSICAL_WAKE_SETTINGS_REFRESH";
    public static final String ACTION_REQUEST_BATTERY_HEAT = "ru.big.town.anative.REQUEST_BATTERY_HEAT";
    private static final String ACTION_STARTUP_SETTINGS_REFRESH = "ru.big.town.anative.BATTERY_HEAT_STARTUP_SETTINGS_REFRESH";
    private static final long ACTIVATE_FAILURE_RETRY_MS = 30000;
    private static final long ACTIVATE_REARM_MS = 300000;
    static final int ACTIVATION_ACTIVE = 3;
    static final int ACTIVATION_AWAITING_CONFIRMATION = 2;
    static final int ACTIVATION_BLOCKED = 4;
    private static final long ACTIVATION_CONFIRM_QUERY_MS = 3000;
    private static final long ACTIVATION_CONFIRM_TIMEOUT_MS = 3000;
    static final int ACTIVATION_ENABLED = 5;
    static final int ACTIVATION_IDLE = 0;
    static final int ACTIVATION_SENDING = 1;
    private static final int AUTO_TEMP_THRESHOLD_C = 10;
    private static final long BATTERY_SAFETY_WATCHDOG_MS = 30000;
    private static final String BIND_PERMISSION = "ru.big.town.anative.permission.BIND_SET_MODES_SERVICE";
    private static final LatestValueDelivery<BroadcastWrite> BROADCASTS;
    private static final long BROADCAST_COALESCE_MS = 250;
    private static final ThreadPoolExecutor BROADCAST_EXECUTOR;
    private static final String CHANNEL_ID = "battery_heat_channel";
    private static final int COL_BATTERY_HEAT_AUTO = 17;
    private static final Uri CONTENT_PROVIDER_URI;
    public static final String EXTRA_BATTERY_HEAT_AUTO_ENABLED = "autoEnabled";
    private static final int FIELD_AUTO_CTRL = 6;
    private static final int FIELD_AUTO_INFO = 7;
    private static final int FIELD_BMS_STATE = 5;
    private static final int FIELD_H97C_FAIL = 2;
    private static final int FIELD_H97C_STATUS = 1;
    private static final int FIELD_H97C_SWITCH = 0;
    private static final int FIELD_H97X_FAIL = 4;
    private static final int FIELD_H97X_PREHEAT = 3;
    private static final long FORCE_QUERY_MS = 6000;
    private static final OemVehicleStateTransport.StateKey H97C_CONTROL_KEY;
    private static final int H97C_REQUIRED_MASK = 39;
    private static final OemVehicleStateTransport.StateKey H97X_PREHEAT_KEY;
    private static final int H97X_REQUIRED_MASK = 56;
    private static final int ID_AUTO_CTRL = 1298;
    private static final int ID_AUTO_CTRL_INFO = 1299;
    private static final int ID_BMS_STATE = 958;
    private static final int ID_DRIVER_PREHEAT_SET = 1080;
    private static final int ID_PREHEAT_FAIL_STATE = 1265;
    private static final int ID_TEP_CONTROL_FAIL = 1296;
    private static final int ID_TEP_CONTROL_STATUS = 1295;
    private static final int ID_TEP_CONTROL_SWITCH = 1294;
    private static final long INCOMPLETE_SNAPSHOT_RETRY_MS = 300000;
    private static final String TAG = "$$$ BatteryHeatService $$$";
    private static final int TEMP_INVALID = -9999;
    private static final int UNKNOWN = Integer.MIN_VALUE;
    private volatile long activeCanBusEpoch;
    private volatile long ambientTempEpoch;
    private volatile long autoDecisionGeneration;
    private volatile boolean autoSettingKnown;
    private volatile long autoSettingRevision;
    private boolean broadcastScheduled;
    private volatile boolean cachedAutoEnabled;
    private CanBusEventHub canBusEventHub;
    private CanBusEventHub.Subscription canBusSubscription;
    private volatile Handler handler;
    private long instanceGeneration;
    private boolean receiverRegistered;
    private boolean startupRefreshRequested;
    private HandlerThread workerThread;
    private static final AtomicLong INSTANCE_SEQUENCE = new AtomicLong();
    private static final AtomicLong ACTIVE_INSTANCE = new AtomicLong();
    private static final AtomicLong BROADCAST_REVISION = new AtomicLong();
    private static final ThreadPoolExecutor SETTINGS_EXECUTOR = newBoundedExecutor("BatteryHeatSettings");
    private final BatteryHeatRefreshGate refreshGate = new BatteryHeatRefreshGate();
    private volatile int ambientTemp = TEMP_INVALID;
    private volatile int controlStatus = Integer.MIN_VALUE;
    private volatile int switchState = Integer.MIN_VALUE;
    private volatile int failReason = Integer.MIN_VALUE;
    private volatile int h97xFailReason = Integer.MIN_VALUE;
    private volatile int h97cFailReason = Integer.MIN_VALUE;
    private volatile int autoCtrl = Integer.MIN_VALUE;
    private volatile int autoCtrlInfo = Integer.MIN_VALUE;
    private volatile int preheatSet = Integer.MIN_VALUE;
    private volatile int bmsState = Integer.MIN_VALUE;
    private long lastActivateElapsed = -4611686018427387904L;
    private long lastActivateAttemptElapsed = -4611686018427387904L;
    private volatile boolean activationPending = false;
    private volatile boolean confirmationPending = false;
    private int confirmationPlatform = 0;
    private long lastVehicleSnapshotRequestElapsed = -4611686018427387904L;
    private volatile boolean destroyed = false;
    private volatile int vehicleFieldsSeenMask = 0;
    private final Runnable forceQueryRunnable = new Runnable() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda1
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.requestVehicleStateSnapshot();
        }
    };
    private final Runnable activationConfirmationTimeoutRunnable = new Runnable() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda2
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.m1835lambda$new$1$rubigtownanativeBatteryHeatService();
        }
    };
    private final Runnable activationConfirmationQueryRunnable = new Runnable() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda3
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.m1837lambda$new$2$rubigtownanativeBatteryHeatService();
        }
    };
    private final Runnable broadcastRunnable = new Runnable() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda4
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.m1836lambda$new$18$rubigtownanativeBatteryHeatService();
        }
    };
    private final BroadcastReceiver uiReceiver = new BroadcastReceiver() { // from class: ru.big.town.anative.BatteryHeatService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BatteryHeatService.ACTION_BATTERY_HEAT_ACTIVATE.equals(action)) {
                BatteryHeatService batteryHeatService = BatteryHeatService.this;
                batteryHeatService.activate("manual (виджет)", true, 0L, Long.MIN_VALUE, batteryHeatService.currentPlatform());
            } else if (BatteryHeatService.ACTION_BATTERY_HEAT_AUTO_CHANGED.equals(action)) {
                if (intent.hasExtra(BatteryHeatService.EXTRA_BATTERY_HEAT_AUTO_ENABLED)) {
                    BatteryHeatService.this.applyAutoSettingChange(intent.getBooleanExtra(BatteryHeatService.EXTRA_BATTERY_HEAT_AUTO_ENABLED, false));
                }
            } else if (BatteryHeatService.ACTION_REQUEST_BATTERY_HEAT.equals(action)) {
                BatteryHeatService.this.requestBroadcastUpdate();
            }
        }
    };
    private final Runnable batterySafetyWatchdog = new Runnable() { // from class: ru.big.town.anative.BatteryHeatService.2
        @Override // java.lang.Runnable
        public void run() {
            if (BatteryHeatService.this.destroyed) {
                return;
            }
            try {
                long jElapsedRealtime = SystemClock.elapsedRealtime();
                if (BatteryHeatService.this.isVehicleSnapshotIncomplete() && jElapsedRealtime - BatteryHeatService.this.lastVehicleSnapshotRequestElapsed >= 300000) {
                    BatteryHeatService.this.requestVehicleStateSnapshot();
                }
                BatteryHeatService.this.maybeAutoActivate("safety-watchdog");
            } finally {
                if (!BatteryHeatService.this.destroyed) {
                    BatteryHeatService.this.handler.postDelayed(this, 30000L);
                }
            }
        }
    };

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    static {
        ThreadPoolExecutor threadPoolExecutorNewBoundedExecutor = newBoundedExecutor("BatteryHeatBroadcast");
        BROADCAST_EXECUTOR = threadPoolExecutorNewBoundedExecutor;
        BROADCASTS = new LatestValueDelivery<>(threadPoolExecutorNewBoundedExecutor, new Consumer() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda23
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                BatteryHeatService.sendSnapshotBroadcast((BatteryHeatService.BroadcastWrite) obj);
            }
        });
        H97X_PREHEAT_KEY = new OemVehicleStateTransport.StateKey("DRIVER_PREHEAT_SET", ID_DRIVER_PREHEAT_SET);
        H97C_CONTROL_KEY = new OemVehicleStateTransport.StateKey("BATTERY_TEP_CONTROL_SWITCH", ID_TEP_CONTROL_SWITCH);
        CONTENT_PROVIDER_URI = Uri.parse("content://ru.big.town.restoremode.restoremodecontentprovider/");
    }

    private static ThreadPoolExecutor newBoundedExecutor(final String str) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue(1), new ThreadFactory() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda12
            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable runnable) {
                return BatteryHeatService.lambda$newBoundedExecutor$0(str, runnable);
            }
        }, new ThreadPoolExecutor.AbortPolicy());
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }

    static /* synthetic */ Thread lambda$newBoundedExecutor$0(String str, Runnable runnable) {
        Thread thread = new Thread(runnable, str);
        thread.setDaemon(true);
        return thread;
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class BroadcastWrite {
        final Context app;
        final long generation;
        final Intent intent;

        BroadcastWrite(Context context, long j, Intent intent) {
            this.app = context;
            this.generation = j;
            this.intent = intent;
        }
    }

    /* JADX INFO: renamed from: lambda$new$1$ru-big-town-anative-BatteryHeatService, reason: not valid java name */
    /* synthetic */ void m1835lambda$new$1$rubigtownanativeBatteryHeatService() {
        if (this.destroyed || !this.confirmationPending) {
            return;
        }
        Log.w(TAG, "battery heat command was accepted but not confirmed by vehicle");
        clearActivationConfirmation();
        advanceAutoDecision();
        requestBroadcastUpdate();
    }

    /* JADX INFO: renamed from: lambda$new$2$ru-big-town-anative-BatteryHeatService, reason: not valid java name */
    /* synthetic */ void m1837lambda$new$2$rubigtownanativeBatteryHeatService() {
        if (this.destroyed || !this.confirmationPending) {
            return;
        }
        requestVehicleStateSnapshot(true, "activation-confirmation");
        Handler handler = this.handler;
        if (handler != null) {
            handler.postDelayed(this.activationConfirmationTimeoutRunnable, 3000L);
        }
    }

    /* JADX INFO: renamed from: ru.big.town.anative.BatteryHeatService$3, reason: invalid class name */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$ru$big$town$anative$CanBusEvent$Kind;

        static {
            int[] iArr = new int[CanBusEvent.Kind.values().length];
            $SwitchMap$ru$big$town$anative$CanBusEvent$Kind = iArr;
            try {
                iArr[CanBusEvent.Kind.CONNECTION.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$ru$big$town$anative$CanBusEvent$Kind[CanBusEvent.Kind.VEHICLE_STATE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$ru$big$town$anative$CanBusEvent$Kind[CanBusEvent.Kind.AMBIENT_TEMPERATURE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCanBusEvent(CanBusEvent canBusEvent) {
        if (this.destroyed) {
            return;
        }
        int i = AnonymousClass3.$SwitchMap$ru$big$town$anative$CanBusEvent$Kind[canBusEvent.kind.ordinal()];
        if (i == 1) {
            this.activeCanBusEpoch = canBusEvent.connectionEpoch;
            advanceAutoDecision();
            resetVehicleSnapshotTracking();
            this.handler.removeCallbacks(this.forceQueryRunnable);
            this.handler.postDelayed(this.forceQueryRunnable, FORCE_QUERY_MS);
            return;
        }
        if (i == 2) {
            onVehicleState(canBusEvent.first, canBusEvent.second);
        } else {
            if (i != 3) {
                return;
            }
            onAmbientTemp(canBusEvent.first, canBusEvent.connectionEpoch);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestVehicleStateSnapshot() {
        requestVehicleStateSnapshot(false, "incomplete-snapshot");
    }

    private void requestVehicleStateSnapshot(boolean z, String str) {
        if (this.destroyed || this.canBusEventHub == null) {
            return;
        }
        if (z || isVehicleSnapshotIncomplete()) {
            this.lastVehicleSnapshotRequestElapsed = SystemClock.elapsedRealtime();
            this.canBusEventHub.requestVehicleStateSnapshot();
            Log.i(TAG, "queryVehicleState requested (" + str + "), profile=" + platformName(currentPlatform()) + " fields=" + Integer.bitCount(this.vehicleFieldsSeenMask));
        }
    }

    private void onVehicleState(int i, int i2) {
        int i3;
        boolean zControlBusy = controlBusy();
        int iCurrentPlatform = currentPlatform();
        boolean zActivationConfirmed = activationConfirmed(iCurrentPlatform);
        int i4 = this.failReason;
        if (i == ID_BMS_STATE) {
            this.bmsState = i2;
            i3 = 5;
        } else if (i == ID_DRIVER_PREHEAT_SET) {
            this.preheatSet = i2;
            i3 = 3;
        } else if (i == ID_PREHEAT_FAIL_STATE) {
            this.h97xFailReason = i2;
            i3 = 4;
        } else if (i == ID_AUTO_CTRL) {
            this.autoCtrl = i2;
            i3 = 6;
        } else if (i != ID_AUTO_CTRL_INFO) {
            switch (i) {
                case ID_TEP_CONTROL_SWITCH /* 1294 */:
                    this.switchState = i2;
                    i3 = 0;
                    break;
                case ID_TEP_CONTROL_STATUS /* 1295 */:
                    this.controlStatus = i2;
                    i3 = 1;
                    break;
                case ID_TEP_CONTROL_FAIL /* 1296 */:
                    this.h97cFailReason = i2;
                    i3 = 2;
                    break;
                default:
                    return;
            }
        } else {
            this.autoCtrlInfo = i2;
            i3 = 7;
        }
        this.vehicleFieldsSeenMask = (1 << i3) | this.vehicleFieldsSeenMask;
        int iCurrentPlatform2 = currentPlatform();
        this.failReason = BatteryHeatAutoPolicy.effectiveFailure(iCurrentPlatform2, this.h97xFailReason, this.h97cFailReason, Integer.MIN_VALUE);
        boolean zActivationConfirmed2 = activationConfirmed(iCurrentPlatform2);
        boolean z = (iCurrentPlatform == iCurrentPlatform2 && i4 == this.failReason && zControlBusy == controlBusy()) ? false : true;
        if (z) {
            advanceAutoDecision();
        }
        if (zActivationConfirmed2) {
            if (!zActivationConfirmed || this.confirmationPending) {
                this.lastActivateElapsed = SystemClock.elapsedRealtime();
                clearActivationConfirmation();
            }
        } else if (BatteryHeatAutoPolicy.blockingFailure(this.failReason) && this.confirmationPending) {
            clearActivationConfirmation();
        }
        Log.i(TAG, "vehicleState id=" + i + " state=" + i2);
        requestBroadcastUpdate();
        if (z) {
            maybeAutoActivate("vehicle-state");
        }
    }

    private void onAmbientTemp(int i, long j) {
        if (i == this.ambientTemp && j == this.ambientTempEpoch) {
            return;
        }
        this.ambientTemp = i;
        this.ambientTempEpoch = j;
        advanceAutoDecision();
        Log.i(TAG, "ambientTemp=" + i + "°C");
        requestBroadcastUpdate();
        if (BatteryHeatAutoPolicy.settingRefreshNeededForTemperature(this.autoSettingKnown)) {
            requestSettingsRefresh("temp-change", true);
        } else {
            maybeAutoActivate("temp-change");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeAutoActivate(String str) {
        long j = this.activeCanBusEpoch;
        long j2 = this.autoDecisionGeneration;
        int iCurrentPlatform = currentPlatform();
        if (m1834lambda$activate$9$rubigtownanativeBatteryHeatService(this.instanceGeneration, j, j2, iCurrentPlatform)) {
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            if (jElapsedRealtime - this.lastActivateElapsed >= 300000 && jElapsedRealtime - this.lastActivateAttemptElapsed >= 30000 && !this.activationPending) {
                Log.i(TAG, "AUTO прогрев: " + str + " ambient=" + this.ambientTemp + "°C < 10");
                activate("auto <10°C", false, j, j2, iCurrentPlatform);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void activate(String str, boolean z, final long j, final long j2, int i) {
        if (this.destroyed) {
            return;
        }
        final int iCurrentPlatform = i == 0 ? currentPlatform() : i;
        if (iCurrentPlatform == 0) {
            Log.w(TAG, "activate battery heat deferred: vehicle profile is not known");
            requestVehicleStateSnapshot(true, "activation-profile");
            requestBroadcastUpdate();
            return;
        }
        if (controlBusy()) {
            Log.i(TAG, "activate battery heat ignored: control is already active/pending");
            requestBroadcastUpdate();
            return;
        }
        if (BatteryHeatAutoPolicy.blockingFailure(this.failReason)) {
            Log.i(TAG, "activate battery heat blocked by vehicle reason=" + this.failReason);
            requestBroadcastUpdate();
            return;
        }
        if (this.activationPending) {
            Log.i(TAG, "activate battery heat coalesced — " + str);
            return;
        }
        this.activationPending = true;
        requestBroadcastUpdate();
        Log.i(TAG, "★ activate battery heat — " + str);
        if (z) {
            final AtomicLong atomicLong = new AtomicLong();
            final AtomicBoolean atomicBoolean = new AtomicBoolean();
            ApplyEngine.postIndependentUserCommand("battery heat " + str, new Runnable() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1831lambda$activate$6$rubigtownanativeBatteryHeatService(atomicBoolean, iCurrentPlatform, atomicLong);
                }
            }, new Runnable() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1833lambda$activate$8$rubigtownanativeBatteryHeatService(atomicBoolean, atomicLong, iCurrentPlatform);
                }
            });
        } else {
            final long j3 = this.instanceGeneration;
            final AtomicLong atomicLong2 = new AtomicLong();
            final int i2 = iCurrentPlatform;
            ApplyEngine.postWakeAction("battery heat " + str, new BooleanSupplier() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda7
                @Override // java.util.function.BooleanSupplier
                public final boolean getAsBoolean() {
                    return this.f$0.m1826lambda$activate$12$rubigtownanativeBatteryHeatService(j3, j, j2, iCurrentPlatform, atomicLong2);
                }
            }, (Consumer<ApplyEngine.WakeActionResult>) new Consumer() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda8
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    this.f$0.m1828lambda$activate$14$rubigtownanativeBatteryHeatService(atomicLong2, j2, i2, (ApplyEngine.WakeActionResult) obj);
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$activate$6$ru-big-town-anative-BatteryHeatService, reason: not valid java name */
    /* synthetic */ void m1831lambda$activate$6$rubigtownanativeBatteryHeatService(AtomicBoolean atomicBoolean, final int i, final AtomicLong atomicLong) {
        atomicBoolean.set(CanSender.runGuardedSend(new BooleanSupplier() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda17
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                return this.f$0.m1829lambda$activate$3$rubigtownanativeBatteryHeatService(i);
            }
        }, new Runnable() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                atomicLong.compareAndSet(0L, SystemClock.elapsedRealtime());
            }
        }, new BooleanSupplier() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda19
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                return this.f$0.m1830lambda$activate$5$rubigtownanativeBatteryHeatService(i);
            }
        }));
    }

    /* JADX INFO: renamed from: lambda$activate$3$ru-big-town-anative-BatteryHeatService, reason: not valid java name */
    /* synthetic */ boolean m1829lambda$activate$3$rubigtownanativeBatteryHeatService(int i) {
        return (this.destroyed || currentPlatform() != i || controlBusy() || BatteryHeatAutoPolicy.blockingFailure(this.failReason)) ? false : true;
    }

    /* JADX INFO: renamed from: lambda$activate$8$ru-big-town-anative-BatteryHeatService, reason: not valid java name */
    /* synthetic */ void m1833lambda$activate$8$rubigtownanativeBatteryHeatService(AtomicBoolean atomicBoolean, final AtomicLong atomicLong, final int i) {
        final ApplyEngine.WakeActionResult wakeActionResult;
        if (atomicBoolean.get()) {
            wakeActionResult = ApplyEngine.WakeActionResult.SUCCESS;
        } else {
            wakeActionResult = ApplyEngine.WakeActionResult.FAILED;
        }
        Handler handler = this.handler;
        if (this.destroyed || handler == null) {
            return;
        }
        handler.post(new Runnable() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1832lambda$activate$7$rubigtownanativeBatteryHeatService(wakeActionResult, atomicLong, i);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$activate$7$ru-big-town-anative-BatteryHeatService, reason: not valid java name */
    /* synthetic */ void m1832lambda$activate$7$rubigtownanativeBatteryHeatService(ApplyEngine.WakeActionResult wakeActionResult, AtomicLong atomicLong, int i) {
        m1827lambda$activate$13$rubigtownanativeBatteryHeatService(wakeActionResult, atomicLong.get(), Long.MIN_VALUE, i);
    }

    /* JADX INFO: renamed from: lambda$activate$12$ru-big-town-anative-BatteryHeatService, reason: not valid java name */
    /* synthetic */ boolean m1826lambda$activate$12$rubigtownanativeBatteryHeatService(final long j, final long j2, final long j3, final int i, final AtomicLong atomicLong) {
        return CanSender.runGuardedSend(new BooleanSupplier() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda20
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                return this.f$0.m1834lambda$activate$9$rubigtownanativeBatteryHeatService(j, j2, j3, i);
            }
        }, new Runnable() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda21
            @Override // java.lang.Runnable
            public final void run() {
                atomicLong.compareAndSet(0L, SystemClock.elapsedRealtime());
            }
        }, new BooleanSupplier() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda22
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                return this.f$0.m1825lambda$activate$11$rubigtownanativeBatteryHeatService(i);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$activate$14$ru-big-town-anative-BatteryHeatService, reason: not valid java name */
    /* synthetic */ void m1828lambda$activate$14$rubigtownanativeBatteryHeatService(AtomicLong atomicLong, final long j, final int i, final ApplyEngine.WakeActionResult wakeActionResult) {
        final long j2 = atomicLong.get();
        Handler handler = this.handler;
        if (this.destroyed || handler == null) {
            return;
        }
        handler.post(new Runnable() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1827lambda$activate$13$rubigtownanativeBatteryHeatService(wakeActionResult, j2, j, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: finishActivation, reason: merged with bridge method [inline-methods] */
    public void m1827lambda$activate$13$rubigtownanativeBatteryHeatService(ApplyEngine.WakeActionResult wakeActionResult, long j, long j2, int i) {
        if (this.destroyed) {
            return;
        }
        boolean z = false;
        this.activationPending = false;
        requestBroadcastUpdate();
        if (j2 != Long.MIN_VALUE && j2 != this.autoDecisionGeneration) {
            z = true;
        }
        if (j > 0) {
            this.lastActivateAttemptElapsed = j;
            if (!z && wakeActionResult == ApplyEngine.WakeActionResult.SUCCESS) {
                if (activationConfirmed(i)) {
                    this.lastActivateElapsed = SystemClock.elapsedRealtime();
                    clearActivationConfirmation();
                } else {
                    beginActivationConfirmation(i);
                }
            }
        }
        if (z) {
            maybeAutoActivate("stale-completion-handoff");
        } else {
            if (wakeActionResult == ApplyEngine.WakeActionResult.SKIPPED || wakeActionResult == ApplyEngine.WakeActionResult.SUCCESS) {
                return;
            }
            Log.w(TAG, "battery heat CAN failed; next qualifying event after 30000ms may retry");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: sendOemBatteryHeatCommand, reason: merged with bridge method [inline-methods] and merged with bridge method [inline-methods] */
    public boolean m1830lambda$activate$5$rubigtownanativeBatteryHeatService(int i) {
        OemVehicleStateTransport.StateKey stateKey;
        if (i == 2) {
            stateKey = H97C_CONTROL_KEY;
        } else {
            if (i != 1) {
                return false;
            }
            stateKey = H97X_PREHEAT_KEY;
        }
        return OemVehicleStateTransport.sendVehicleState(getApplicationContext(), stateKey, 1, "battery temperature control " + platformName(i)).accepted();
    }

    private void beginActivationConfirmation(int i) {
        this.confirmationPending = true;
        this.confirmationPlatform = i;
        Handler handler = this.handler;
        if (handler != null) {
            handler.removeCallbacks(this.activationConfirmationQueryRunnable);
            handler.removeCallbacks(this.activationConfirmationTimeoutRunnable);
            handler.postDelayed(this.activationConfirmationQueryRunnable, 3000L);
        }
        requestBroadcastUpdate();
    }

    private void clearActivationConfirmation() {
        this.confirmationPending = false;
        this.confirmationPlatform = 0;
        Handler handler = this.handler;
        if (handler != null) {
            handler.removeCallbacks(this.activationConfirmationQueryRunnable);
            handler.removeCallbacks(this.activationConfirmationTimeoutRunnable);
        }
    }

    private boolean heatingActive() {
        return BatteryHeatAutoPolicy.heatingActive(this.controlStatus, this.preheatSet, this.bmsState);
    }

    private boolean activationConfirmed(int i) {
        return BatteryHeatAutoPolicy.activationConfirmed(i, this.controlStatus, this.switchState, this.preheatSet, this.bmsState);
    }

    private boolean controlBusy() {
        return BatteryHeatAutoPolicy.controlBusy(this.controlStatus, this.switchState, this.preheatSet, this.bmsState, this.confirmationPending);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int currentPlatform() {
        return BatteryHeatAutoPolicy.platform((this.vehicleFieldsSeenMask & 24) != 0, (this.vehicleFieldsSeenMask & 6) != 0);
    }

    private static String platformName(int i) {
        if (i == 1) {
            return "H97X";
        }
        if (i == 2) {
            return "H97C";
        }
        return EnvironmentCompat.MEDIA_UNKNOWN;
    }

    private int activationPhase() {
        if (heatingActive()) {
            return 3;
        }
        if (BatteryHeatAutoPolicy.blockingFailure(this.failReason)) {
            return 4;
        }
        if (this.activationPending) {
            return 1;
        }
        if (this.confirmationPending) {
            return 2;
        }
        if (currentPlatform() == 2 && this.switchState == 1) {
            return 5;
        }
        return this.controlStatus == 2 ? 2 : 0;
    }

    private void advanceAutoDecision() {
        this.autoDecisionGeneration++;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: automaticActivationCurrent, reason: merged with bridge method [inline-methods] */
    public boolean m1834lambda$activate$9$rubigtownanativeBatteryHeatService(long j, long j2, long j3, int i) {
        return BatteryHeatAutoPolicy.canSend(!this.destroyed && ACTIVE_INSTANCE.get() == j && i != 0 && i == currentPlatform(), j2, this.activeCanBusEpoch, this.ambientTempEpoch, j3, this.autoDecisionGeneration, this.cachedAutoEnabled, this.ambientTemp != TEMP_INVALID, this.ambientTemp < 10, controlBusy(), BatteryHeatAutoPolicy.blockingFailure(this.failReason));
    }

    private void requestSettingsRefresh(String str, boolean z) {
        BatteryHeatRefreshGate.Request requestOffer;
        if (this.destroyed || (requestOffer = this.refreshGate.offer(this.activeCanBusEpoch, z, str)) == null) {
            return;
        }
        submitSettingsRefresh(requestOffer);
    }

    private void submitSettingsRefresh(BatteryHeatRefreshGate.Request request) {
        final BatteryHeatRefreshGate.Request request2;
        if (this.destroyed || request == null) {
            return;
        }
        final ContentResolver contentResolver = getApplicationContext().getContentResolver();
        final WeakReference weakReference = new WeakReference(this);
        final long j = this.autoSettingRevision;
        try {
            request2 = request;
            try {
                SETTINGS_EXECUTOR.execute(new Runnable() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda16
                    @Override // java.lang.Runnable
                    public final void run() {
                        BatteryHeatService.lambda$submitSettingsRefresh$17(weakReference, j, request2, contentResolver);
                    }
                });
            } catch (RejectedExecutionException unused) {
                this.refreshGate.reject(request2);
                Log.w(TAG, "settings refresh queue full; waiting for next real event");
            }
        } catch (RejectedExecutionException unused2) {
            request2 = request;
        }
    }

    static /* synthetic */ void lambda$submitSettingsRefresh$17(WeakReference weakReference, final long j, final BatteryHeatRefreshGate.Request request, ContentResolver contentResolver) {
        Handler handler;
        final BatteryHeatService batteryHeatService = (BatteryHeatService) weakReference.get();
        if (batteryHeatService == null || batteryHeatService.destroyed) {
            return;
        }
        if (!BatteryHeatAutoPolicy.revisionCurrent(j, batteryHeatService.autoSettingRevision)) {
            Handler handler2 = batteryHeatService.handler;
            if (handler2 != null) {
                handler2.post(new Runnable() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.finishSettingsRefresh(request, null, j);
                    }
                });
                return;
            }
            return;
        }
        final Boolean boolQueryAutoEnabled = queryAutoEnabled(contentResolver);
        final BatteryHeatService batteryHeatService2 = (BatteryHeatService) weakReference.get();
        if (batteryHeatService2 == null || batteryHeatService2.destroyed || (handler = batteryHeatService2.handler) == null) {
            return;
        }
        handler.post(new Runnable() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.finishSettingsRefresh(request, boolQueryAutoEnabled, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishSettingsRefresh(BatteryHeatRefreshGate.Request request, Boolean bool, long j) {
        if (this.destroyed) {
            return;
        }
        BatteryHeatRefreshGate.Completion completionFinish = this.refreshGate.finish(request);
        if (completionFinish.publish && BatteryHeatAutoPolicy.revisionCurrent(j, this.autoSettingRevision) && bool != null) {
            if (!this.autoSettingKnown || this.cachedAutoEnabled != bool.booleanValue()) {
                this.autoSettingKnown = true;
                this.cachedAutoEnabled = bool.booleanValue();
                advanceAutoDecision();
            }
            requestBroadcastUpdate();
            if (request.evaluateAuto && request.epoch == this.activeCanBusEpoch && isActiveInstance()) {
                maybeAutoActivate(request.reason);
            }
        }
        if (completionFinish.next != null) {
            submitSettingsRefresh(completionFinish.next);
        }
    }

    private static Boolean queryAutoEnabled(ContentResolver contentResolver) {
        try {
            Cursor cursorQuery = contentResolver.query(CONTENT_PROVIDER_URI, null, null, null, null);
            if (cursorQuery == null) {
                return null;
            }
            try {
                if (!cursorQuery.moveToFirst() || cursorQuery.getColumnCount() <= 17) {
                    return null;
                }
                boolean z = true;
                if (cursorQuery.getInt(17) != 1) {
                    z = false;
                }
                return Boolean.valueOf(z);
            } finally {
                cursorQuery.close();
            }
        } catch (Exception e) {
            Log.w(TAG, "queryAutoEnabled: " + e.getMessage());
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestBroadcastUpdate() {
        Handler handler;
        if (this.destroyed || this.broadcastScheduled || (handler = this.handler) == null) {
            return;
        }
        this.broadcastScheduled = true;
        if (handler.postDelayed(this.broadcastRunnable, BROADCAST_COALESCE_MS)) {
            return;
        }
        this.broadcastScheduled = false;
    }

    /* JADX INFO: renamed from: lambda$new$18$ru-big-town-anative-BatteryHeatService, reason: not valid java name */
    /* synthetic */ void m1836lambda$new$18$rubigtownanativeBatteryHeatService() {
        this.broadcastScheduled = false;
        if (this.destroyed) {
            return;
        }
        enqueueBroadcastUpdate();
    }

    private void enqueueBroadcastUpdate() {
        Intent intent = new Intent(ACTION_BATTERY_HEAT_UPDATE);
        intent.putExtra("ambientTemp", this.ambientTemp);
        intent.putExtra("controlStatus", this.controlStatus);
        intent.putExtra("switchState", this.switchState);
        intent.putExtra("failReason", this.failReason);
        intent.putExtra("autoCtrl", this.autoCtrl);
        intent.putExtra("autoCtrlInfo", this.autoCtrlInfo);
        intent.putExtra("preheatSet", this.preheatSet);
        intent.putExtra("bmsState", this.bmsState);
        intent.putExtra("vehiclePlatform", currentPlatform());
        intent.putExtra("activationPhase", activationPhase());
        intent.putExtra("confirmationPlatform", this.confirmationPlatform);
        intent.putExtra(EXTRA_BATTERY_HEAT_AUTO_ENABLED, this.cachedAutoEnabled ? 1 : 0);
        intent.putExtra("tempThreshold", 10);
        BROADCASTS.offer(this.instanceGeneration, BROADCAST_REVISION.incrementAndGet(), new BroadcastWrite(getApplicationContext(), this.instanceGeneration, intent));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void sendSnapshotBroadcast(BroadcastWrite broadcastWrite) {
        if (broadcastWrite == null || ACTIVE_INSTANCE.get() != broadcastWrite.generation) {
            return;
        }
        try {
            broadcastWrite.app.sendBroadcast(broadcastWrite.intent);
        } catch (Exception e) {
            Log.w(TAG, "broadcastUpdate: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyAutoSettingChange(boolean z) {
        this.autoSettingRevision++;
        this.autoSettingKnown = true;
        this.cachedAutoEnabled = z;
        advanceAutoDecision();
        requestBroadcastUpdate();
        maybeAutoActivate("setting-change");
    }

    private boolean isActiveInstance() {
        return !this.destroyed && ACTIVE_INSTANCE.get() == this.instanceGeneration;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate() — BatteryHeatService");
        long jIncrementAndGet = INSTANCE_SEQUENCE.incrementAndGet();
        this.instanceGeneration = jIncrementAndGet;
        ACTIVE_INSTANCE.set(jIncrementAndGet);
        createNotificationChannel();
        startForeground(5, new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("Прогрев батареи").setContentText("Мониторинг температуры и статуса ВВБ").setSmallIcon(R.drawable.ic_launcher_foreground).build());
        HandlerThread handlerThread = new HandlerThread("BatteryHeat", 10);
        this.workerThread = handlerThread;
        handlerThread.start();
        this.handler = new Handler(this.workerThread.getLooper());
        this.handler.post(new Runnable() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.initializeMonitoring();
            }
        });
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        String action = intent == null ? ACTION_STARTUP_SETTINGS_REFRESH : intent.getAction();
        if (ACTION_PHYSICAL_WAKE_SETTINGS_REFRESH.equals(action)) {
            this.startupRefreshRequested = true;
            postSettingsRefresh("physical-wake");
        } else if (ACTION_STARTUP_SETTINGS_REFRESH.equals(action) || action == null) {
            if (!this.startupRefreshRequested) {
                this.startupRefreshRequested = true;
                postSettingsRefresh("startup");
            }
        } else {
            Log.w(TAG, "Ignoring unknown start action: " + action);
        }
        return 1;
    }

    private void postSettingsRefresh(final String str) {
        Handler handler = this.handler;
        if (this.destroyed || handler == null) {
            return;
        }
        handler.post(new Runnable() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1839x2392f824(str);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$postSettingsRefresh$19$ru-big-town-anative-BatteryHeatService, reason: not valid java name */
    /* synthetic */ void m1839x2392f824(String str) {
        requestSettingsRefresh(str, true);
    }

    static void requestStartup(Context context) {
        requestSettingsRefreshStart(context, ACTION_STARTUP_SETTINGS_REFRESH);
    }

    static void requestPhysicalWake(Context context) {
        requestSettingsRefreshStart(context, ACTION_PHYSICAL_WAKE_SETTINGS_REFRESH);
    }

    private static void requestSettingsRefreshStart(Context context, String str) {
        context.startForegroundService(new Intent(context, (Class<?>) BatteryHeatService.class).setAction(str));
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        this.destroyed = true;
        ACTIVE_INSTANCE.compareAndSet(this.instanceGeneration, 0L);
        this.refreshGate.close();
        final Handler handler = this.handler;
        final HandlerThread handlerThread = this.workerThread;
        if (handler != null && handlerThread != null) {
            handler.removeCallbacks(this.batterySafetyWatchdog);
            if (!handler.postAtFrontOfQueue(new Runnable() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1838lambda$onDestroy$20$rubigtownanativeBatteryHeatService(handler, handlerThread);
                }
            })) {
                handlerThread.quitSafely();
            }
        }
        super.onDestroy();
    }

    /* JADX INFO: renamed from: lambda$onDestroy$20$ru-big-town-anative-BatteryHeatService, reason: not valid java name */
    /* synthetic */ void m1838lambda$onDestroy$20$rubigtownanativeBatteryHeatService(Handler handler, HandlerThread handlerThread) {
        try {
            CanBusEventHub.Subscription subscription = this.canBusSubscription;
            this.canBusSubscription = null;
            if (subscription != null) {
                subscription.close();
            }
            this.canBusEventHub = null;
            if (this.receiverRegistered) {
                try {
                    unregisterReceiver(this.uiReceiver);
                } catch (Exception unused) {
                }
                this.receiverRegistered = false;
            }
        } finally {
            handler.removeCallbacksAndMessages(null);
            this.handler = null;
            handlerThread.quitSafely();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initializeMonitoring() {
        final BatteryHeatService batteryHeatService;
        if (this.destroyed) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter(ACTION_REQUEST_BATTERY_HEAT);
        intentFilter.addAction(ACTION_BATTERY_HEAT_ACTIVATE);
        intentFilter.addAction(ACTION_BATTERY_HEAT_AUTO_CHANGED);
        try {
            batteryHeatService = this;
            try {
                ContextCompat.registerReceiver(batteryHeatService, this.uiReceiver, intentFilter, BIND_PERMISSION, this.handler, 2);
                batteryHeatService.receiverRegistered = true;
            } catch (Exception e) {
                e = e;
                Log.w(TAG, "registerReceiver: " + e.getMessage());
            }
        } catch (Exception e2) {
            e = e2;
            batteryHeatService = this;
        }
        if (batteryHeatService.destroyed) {
            return;
        }
        CanBusEventHub canBusEventHub = CanBusEventHub.get(batteryHeatService);
        batteryHeatService.canBusEventHub = canBusEventHub;
        batteryHeatService.canBusSubscription = canBusEventHub.subscribe(49, new int[]{ID_TEP_CONTROL_SWITCH, ID_TEP_CONTROL_STATUS, ID_TEP_CONTROL_FAIL, ID_AUTO_CTRL, ID_AUTO_CTRL_INFO, ID_DRIVER_PREHEAT_SET, ID_PREHEAT_FAIL_STATE, ID_BMS_STATE}, batteryHeatService.handler, new CanBusEventHub.Listener() { // from class: ru.big.town.anative.BatteryHeatService$$ExternalSyntheticLambda13
            @Override // ru.big.town.anative.CanBusEventHub.Listener
            public final void onCanBusEvent(CanBusEvent canBusEvent) {
                this.f$0.onCanBusEvent(canBusEvent);
            }
        });
        batteryHeatService.requestBroadcastUpdate();
        batteryHeatService.handler.postDelayed(batteryHeatService.batterySafetyWatchdog, 30000L);
    }

    private void resetVehicleSnapshotTracking() {
        this.vehicleFieldsSeenMask = 0;
        this.controlStatus = Integer.MIN_VALUE;
        this.switchState = Integer.MIN_VALUE;
        this.h97cFailReason = Integer.MIN_VALUE;
        this.h97xFailReason = Integer.MIN_VALUE;
        this.failReason = Integer.MIN_VALUE;
        this.preheatSet = Integer.MIN_VALUE;
        this.bmsState = Integer.MIN_VALUE;
        this.autoCtrl = Integer.MIN_VALUE;
        this.autoCtrlInfo = Integer.MIN_VALUE;
        clearActivationConfirmation();
        requestBroadcastUpdate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isVehicleSnapshotIncomplete() {
        return !BatteryHeatAutoPolicy.snapshotComplete(this.vehicleFieldsSeenMask, 56, 39);
    }

    private void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Прогрев батареи", 2);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
