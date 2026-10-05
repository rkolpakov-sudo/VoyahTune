package ru.big.town.anative;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.UiModeManager;
import android.car.Car;
import android.car.hardware.power.CarPowerManager;
import android.car.hardware.property.CarPropertyManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.net.Uri;
import android.os.BadParcelableException;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.InputEvent;
import android.view.MotionEvent;
import android.view.Surface;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import kotlinx.coroutines.DebugKt;
import ru.big.town.common.SuspensionWidgetProtocol;

/* JADX INFO: loaded from: classes2.dex */
public class SetModesService extends Service {
    static final String ACTION_EMBEDDED_TASK_LEFT = "ru.big.town.anative.EMBEDDED_TASK_LEFT";
    static final String ACTION_LOGGING_SET = "ru.big.town.anative.LOGGING_SET";
    static final String ACTION_LOGGING_SHARE = "ru.big.town.anative.LOGGING_SHARE";
    static final String ACTION_LOG_UPDATE = "ru.big.town.anative.LOG_UPDATE";
    static final String ACTION_POWER_HOLD_STATUS_UPDATE = "ru.big.town.anative.POWER_HOLD_STATUS_UPDATE";
    static final String ACTION_REQUEST_LOG = "ru.big.town.anative.REQUEST_LOG";
    static final String ACTION_REQUEST_POWER_HOLD_STATUS = "ru.big.town.anative.REQUEST_POWER_HOLD_STATUS";
    private static final long AUTO_LAUNCH_DEBOUNCE_MS = 60000;
    private static final String BIND_PERMISSION = "ru.big.town.anative.permission.BIND_SET_MODES_SERVICE";
    private static final long CAR_POWER_CONNECT_WATCHDOG_MS = 15000;
    private static final long CAR_POWER_RECONNECT_DELAY_MS = 5000;
    private static final long EMBEDDED_LAUNCH_GRACE_MS = 3000;
    private static final long VOICE_PREWARM_DELAY_MS = 15000;
    static final String EXTRA_EMBEDDED_TASK_PKG = "pkg";
    static final String EXTRA_POWER_HOLD_EXIT_REASON = "exitReason";
    static final String EXTRA_POWER_HOLD_REQUEST_OUTCOME = "requestOutcome";
    static final String EXTRA_POWER_HOLD_STATUS = "status";
    static final int MSG_APPLY_DRIVE_MODES = 1;
    static final int MSG_APPLY_DRIVE_MODES_STAR_BUTTON = 2;
    static final int MSG_APPLY_FORCED_EV = 35;
    static final int MSG_APPLY_PEDESTRIAN = 21;
    static final int MSG_APPLY_SUSPENSION_MAINTENANCE = 37;
    static final int MSG_AUTO_LIGHT_DISABLE = 11;
    static final int MSG_AUTO_LIGHT_ENABLE = 10;
    static final int MSG_CLOSE_ALL = 27;
    static final int MSG_EMBEDDED_TRANSFER = 38;
    static final int MSG_FLOATING_BACK = 24;
    static final int MSG_FLOATING_BACK_SIDE = 25;
    static final int MSG_GRANT_INSTALL = 26;
    static final int MSG_LEAVE_CAR = 20;
    static final int MSG_LOGGING_ENABLE = 32;
    static final int MSG_LOGGING_SHARE = 33;
    static final int MSG_REBOOT = 22;
    static final int MSG_RESULT = 4;
    static final int MSG_SET_THEME = 28;
    static final int MSG_SPLIT_LAUNCH_VD = 34;
    static final int MSG_WASH_MODE = 23;
    private static final String RESTOREMODE_CONFIG_SYNC_ACTION = "ru.big.town.restoremode.SYNC_SAVED_CONFIG";
    private static final String RESTOREMODE_CONFIG_SYNC_RECEIVER = "ru.big.town.restoremode.SavedConfigSyncReceiver";
    static final String RESTOREMODE_MAIN = "ru.big.town.restoremode.MainActivity";
    static final String RESTOREMODE_PKG = "ru.big.town.restoremode";
    static final int STATE_ON = 6;
    static final int STATE_SHUTDOWN_PREPARE = 7;
    static final String TAG = "$$$ SetModesService $$$";
    private volatile Handler carPowerHandler;
    private volatile CarPowerManager carPowerManager;
    private HandlerThread carPowerThread;
    private Messenger clientMessenger;
    private volatile Car mCar;
    private CarPropertyManager mCarPropertyManager;
    private PowerHoldController powerHoldController;
    private boolean powerHoldStatusReceiverRegistered;
    private PowerHoldStatusTracker powerHoldStatusTracker;
    private ScreenLiftTaskRestorer screenLiftTaskRestorer;
    private SetModesReceiverDynamic setModesReceiverDynamic;
    private SuspensionWidgetController suspensionWidget;
    private VehicleStateControllers vehicleStateControllers;
    private WashModeController washModeController;
    private final Map<String, VirtualDisplay> embeddedDisplays = new HashMap();
    private final Map<String, String> embeddedPackages = new HashMap();
    private final Map<String, Boolean> embeddedLaunched = new HashMap();
    private final Map<String, Long> embeddedLaunchAt = new HashMap();
    private final VoiceCommandController voiceCommands = new VoiceCommandController(this);
    private long lastAutoLaunch = -4611686018427387904L;
    private final BroadcastReceiver logRequestReceiver = new BroadcastReceiver() { // from class: ru.big.town.anative.SetModesService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (SetModesService.ACTION_LOGGING_SET.equals(action)) {
                SetModesService.this.setLoggingEnabled(intent.getBooleanExtra(DebugKt.DEBUG_PROPERTY_VALUE_ON, false));
                return;
            }
            if (SetModesService.ACTION_LOGGING_SHARE.equals(action)) {
                SetModesService.this.shareLogFile();
                return;
            }
            Intent intent2 = new Intent(SetModesService.ACTION_LOG_UPDATE);
            intent2.putExtra("log", NativeLog.get().snapshot());
            intent2.putExtra("running", NativeLog.get().isRunning());
            intent2.putExtra("path", NativeLog.get().logFile(SetModesService.this.getApplicationContext()).getAbsolutePath());
            SetModesService.this.sendBroadcast(intent2);
        }
    };
    private boolean receiverRegistered = false;
    private final String CHANNEL_ID = "screen_monitor_channel";
    private final CarPowerCallbackGate carPowerCallbackGate = new CarPowerCallbackGate();
    private final SleepController sleepController = new SleepController();
    private long ancillaryWakeSession = -1;
    private long pendingWakeSession = -1;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean startupInitialized = false;
    private boolean wakeSessionActive = false;
    private volatile boolean serviceDestroyed = false;
    private boolean screenOffObserved = false;
    private boolean pendingPhysicalWake = false;
    private final Runnable startNowPlayingRunnable = new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda19
        @Override // java.lang.Runnable
        public final void run() {
            if (SetModesService.this.ancillaryWakeSessionCurrent()) {
                SetModesService.this.m2065lambda$new$10$rubigtownanativeSetModesService();
            }
        }
    };
    private final Runnable reassertFloatingBackRunnable = new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda20
        @Override // java.lang.Runnable
        public final void run() {
            if (SetModesService.this.ancillaryWakeSessionCurrent()) {
                SetModesService.this.reassertFloatingBack();
            }
        }
    };
    private final Runnable autoLaunchRunnable = new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda21
        @Override // java.lang.Runnable
        public final void run() {
            if (SetModesService.this.ancillaryWakeSessionCurrent()) {
                SetModesService.this.maybeAutoLaunchRestoreMode();
            }
        }
    };
    private final Runnable floatingBackEnableRunnable = new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda22
        @Override // java.lang.Runnable
        public final void run() {
            SetModesService.this.m2066lambda$new$11$rubigtownanativeSetModesService();
        }
    };
private final Runnable carPowerReconnectRunnable = new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda23
        @Override // java.lang.Runnable
        public final void run() {
            SetModesService.this.m7683lambda$new$4$rubigtownanativeSetModesService();
        }
    };
    private final Runnable voicePrewarmRunnable = new Runnable() {
        @Override
        public final void run() {
            SetModesService.this.triggerVoicePrewarm();
        }
    };
    private final BroadcastReceiver powerHoldStatusRequestReceiver = new BroadcastReceiver() { // from class: ru.big.town.anative.SetModesService.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            PowerHoldStatusTracker powerHoldStatusTracker;
            if (SetModesService.ACTION_REQUEST_POWER_HOLD_STATUS.equals(intent.getAction()) && (powerHoldStatusTracker = SetModesService.this.powerHoldStatusTracker) != null) {
                powerHoldStatusTracker.requestCurrentStatus();
            }
        }
    };
    final Messenger serviceMessenger = new Messenger(new IncomingHandler());

    private static boolean isSleepOrShutdownState(int i) {
        return i == 2 || i == 5 || i == 7;
    }

    private static boolean isWakeState(int i) {
        return i == 6 || i == 3 || i == 1 || i == 8;
    }

    class IncomingHandler extends Handler {
        IncomingHandler() {
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r12v12 */
        /* JADX WARN: Type inference failed for: r12v13, types: [boolean, int] */
        /* JADX WARN: Type inference failed for: r12v17 */
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            final boolean z;
            boolean r12 = false;
            int i = message.what;
            if (i == 1) {
                SetModesService.this.clientMessenger = message.replyTo;
                final Messenger messenger = message.replyTo;
                ApplyEngine.applyNow(new Runnable() { // from class: ru.big.town.anative.SetModesService$IncomingHandler$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SetModesService.notifyApplyDone(messenger);
                    }
                });
                Log.i(SetModesService.TAG, "handleMessage() MSG_APPLY_DRIVE_MODES");
                return;
            }
            if (i == 2) {
                SetModesService.this.clientMessenger = message.replyTo;
                SetModesService.worker(1, 100, 2, message.arg1);
                Log.i(SetModesService.TAG, "handleMessage() MSG_APPLY_DRIVE_MODES_STAR_BUTTON");
                SetModesService.notifyApplyDone(message.replyTo);
                return;
            }
            if (i == 10) {
                Log.i(SetModesService.TAG, "handleMessage() MSG_AUTO_LIGHT_ENABLE");
                SetModesService.this.setAutoLightEnabled(true);
                return;
            }
            if (i == 11) {
                Log.i(SetModesService.TAG, "handleMessage() MSG_AUTO_LIGHT_DISABLE");
                SetModesService.this.setAutoLightEnabled(false);
                return;
            }
            switch (i) {
                case 20:
                    Log.i(SetModesService.TAG, "handleMessage() MSG_LEAVE_CAR");
                    final PowerHoldStatusTracker powerHoldStatusTracker = SetModesService.this.powerHoldStatusTracker;
                    if (powerHoldStatusTracker == null) {
                        Log.w(SetModesService.TAG, "Power Hold tracker is unavailable");
                    } else {
                        powerHoldStatusTracker.beginActivation(new PowerHoldStatusTracker.ActivationReady() { // from class: ru.big.town.anative.SetModesService$IncomingHandler$$ExternalSyntheticLambda1
                            @Override // ru.big.town.anative.PowerHoldStatusTracker.ActivationReady
                            public final void onReady(long j) {
                                SetModesService.IncomingHandler.this.m2074xb2f90d7b(powerHoldStatusTracker, j);
                            }
                        });
                    }
                    break;
                case 21:
                    Log.i(SetModesService.TAG, "handleMessage() MSG_APPLY_PEDESTRIAN arg1=" + message.arg1);
                    z = message.arg1 == 1;
                    ApplyEngine.postUserCommand("pedestrian sound", new Runnable() { // from class: ru.big.town.anative.SetModesService$IncomingHandler$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            AvasController.get().requestUserToggle(z);
                        }
                    });
                    break;
                case 22:
                    Log.i(SetModesService.TAG, "handleMessage() MSG_REBOOT");
                    SetModesService.this.rebootSystem();
                    break;
                case 23:
                    Log.i(SetModesService.TAG, "handleMessage() MSG_WASH_MODE");
                    ApplyEngine.postUserCommand("wash mode", new Runnable() { // from class: ru.big.town.anative.SetModesService$IncomingHandler$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            SetModesService.IncomingHandler.this.m2075x4033befc();
                        }
                    });
                    break;
                case 24:
                    Log.i(SetModesService.TAG, "handleMessage() MSG_FLOATING_BACK arg1=" + message.arg1);
                    SetModesService.this.setFloatingBackEnabled(message.arg1 == 1);
                    break;
                case 25:
                    Log.i(SetModesService.TAG, "handleMessage() MSG_FLOATING_BACK_SIDE arg1=" + message.arg1);
                    SetModesService.this.setFloatingBackSide(message.arg1);
                    break;
                case 26:
                    String string = message.getData() != null ? message.getData().getString(SetModesService.EXTRA_EMBEDDED_TASK_PKG) : null;
                    Log.i(SetModesService.TAG, "handleMessage() MSG_GRANT_INSTALL pkg=" + string + " uid=" + message.arg1);
                    SetModesService.this.grantInstallPermission(string, message.arg1);
                    break;
                case 27:
                    Log.i(SetModesService.TAG, "handleMessage() MSG_CLOSE_ALL");
                    SetModesService.this.closeAllApps();
                    break;
                case 28:
                    Log.i(SetModesService.TAG, "handleMessage() MSG_SET_THEME arg1=" + message.arg1);
                    SetModesService.this.applyTheme(message.arg1);
                    break;
                default:
                    switch (i) {
                        case 32:
                            Log.i(SetModesService.TAG, "handleMessage() MSG_LOGGING_ENABLE arg1=" + message.arg1);
                            SetModesService.this.setLoggingEnabled(message.arg1 == 1);
                            break;
                        case 33:
                            Log.i(SetModesService.TAG, "handleMessage() MSG_LOGGING_SHARE");
                            SetModesService.this.shareLogFile();
                            break;
                        case 34:
                            Bundle data = message.getData();
                            final String string2 = data != null ? data.getString("left") : null;
                            String string3 = data != null ? data.getString("right") : null;
                            int i2 = data != null ? data.getInt(SplitHostActivity.EXTRA_LEFT_DPI, 0) : 0;
                            int i3 = data != null ? data.getInt(SplitHostActivity.EXTRA_RIGHT_DPI, 0) : 0;
                            boolean z2 = data != null && data.getBoolean("singleVd", false);
                            z = data != null && data.getBoolean(SplitHostActivity.EXTRA_RESIZABLE, false);
                            float f = data != null ? data.getFloat(SplitHostActivity.EXTRA_SPLIT, 0.0f) : 0.0f;
                            int i4 = data != null ? data.getInt(SplitHostActivity.EXTRA_PRESET_IDX, -1) : -1;
                            String string4 = data != null ? data.getString(SplitHostActivity.EXTRA_PRESET_ID, "") : "";
                            Log.i(SetModesService.TAG, "handleMessage() MSG_SPLIT_LAUNCH_VD left=" + string2 + " right=" + string3 + " ratio=" + message.arg1 + " lDpi=" + i2 + " rDpi=" + i3 + " singleVd=" + z2 + " resizable=" + z + " split=" + f + " preset=" + i4 + " presetId=" + string4);
                            if (data != null) {
                                r12 = false;
                                if (data.getBoolean("embeddedRelease", false)) {
                                    SetModesService.this.releaseEmbeddedDisplay(data.getString("widgetId", ""));
                                }
                            } else {
                                r12 = false;
                            }
                            if (data != null && data.getBoolean("embeddedSurface", r12)) {
                                SetModesService.this.startEmbeddedDisplay(data.getString("widgetId", ""), string2, (Surface) data.getParcelable("surface"), data.getInt("width", 0), data.getInt(SuspensionWidgetProtocol.HEIGHT, 0), i2);
                            } else if (data != null && data.getBoolean("embeddedTouch", r12)) {
                                SetModesService.this.injectEmbeddedTouch(data.getString("widgetId", ""), (MotionEvent) data.getParcelable(NotificationCompat.CATEGORY_EVENT));
                            } else if (z2) {
                                SplitHostActivity.launchSingle(SetModesService.this, string2, i2, 0);
                            } else if (string3 == null || string3.isEmpty()) {
                                boolean zEnsureAppDpi = SetModesReceiverDynamic.ensureAppDpi(SetModesService.this, string2, i2);
                                Runnable runnable = new Runnable() { // from class: ru.big.town.anative.SetModesService$IncomingHandler$$ExternalSyntheticLambda6
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        SetModesService.IncomingHandler.this.m2077x751e8500(string2);
                                    }
                                };
                                if (zEnsureAppDpi) {
                                    SetModesService.this.mainHandler.postDelayed(runnable, 300L);
                                } else {
                                    runnable.run();
                                }
                            } else {
                                SetModesService.this.launchVirtualSplit(string2, string3, message.arg1, i2, i3, z, f, i4, string4);
                            }
                            break;
                        case 35:
                            Log.i(SetModesService.TAG, "handleMessage() MSG_APPLY_FORCED_EV arg1=" + message.arg1);
                            z = message.arg1 == 1;
                            ApplyEngine.postUserCommand("forced EV", new Runnable() { // from class: ru.big.town.anative.SetModesService$IncomingHandler$$ExternalSyntheticLambda5
                                @Override // java.lang.Runnable
                                public final void run() {
                                    MainActivity.sendForcedEvCommand(z);
                                }
                            });
                            break;
                        case 36:
                            try {
                                SetModesService.this.voiceCommands.handle(message.getData());
                            } catch (BadParcelableException e) {
                                Log.e(SetModesService.TAG, "Invalid voice command parcel; update VoyahTune UI", e);
                                return;
                            }
                            break;
                        case 37:
                            z = message.arg1 == 1;
                            ApplyEngine.postUserCommand("suspension maintenance", new Runnable() { // from class: ru.big.town.anative.SetModesService$IncomingHandler$$ExternalSyntheticLambda4
                                @Override // java.lang.Runnable
                                public final void run() {
                                    SetModesService.IncomingHandler.this.m2076x5aa921fe(z);
                                }
                            });
                            break;
                        case 38:
                            Bundle data2 = message.getData();
                            if (data2 != null) {
                                if (data2.getBoolean("embeddedMove", false)) {
                                    SetModesService.this.moveEmbeddedDisplay(data2);
                                } else if (data2.getBoolean("embeddedSwap", false)) {
                                    SetModesService.this.swapEmbeddedDisplays(data2);
                                }
                                break;
                            }
                            break;
                        default:
                            switch (i) {
                                case SuspensionWidgetProtocol.WATCH /* 90 */:
                                case SuspensionWidgetProtocol.UNWATCH /* 91 */:
                                case SuspensionWidgetProtocol.SELECT /* 92 */:
                                    if (SetModesService.this.suspensionWidget == null) {
                                        SetModesService.this.suspensionWidget = new SuspensionWidgetController(SetModesService.this);
                                    }
                                    SetModesService.this.suspensionWidget.handle(message);
                                    break;
                                default:
                                    Log.i(SetModesService.TAG, "handleMessage() default");
                                    super.handleMessage(message);
                                    break;
                            }
                            break;
                    }
                    break;
            }
        }

        /* JADX INFO: renamed from: lambda$handleMessage$3$ru-big-town-anative-SetModesService$IncomingHandler, reason: not valid java name */
        /* synthetic */ void m2074xb2f90d7b(final PowerHoldStatusTracker powerHoldStatusTracker, final long j) {
            final AtomicReference atomicReference = new AtomicReference(PowerHoldPolicy.Outcome.TRANSPORT_FAILURE);
            ApplyEngine.postUserCommand("power hold", new Runnable() { // from class: ru.big.town.anative.SetModesService$IncomingHandler$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    SetModesService.IncomingHandler.this.m2073x9883aa79(atomicReference);
                }
            }, new Runnable() { // from class: ru.big.town.anative.SetModesService$IncomingHandler$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    powerHoldStatusTracker.finishActivation(j, (PowerHoldPolicy.Outcome) atomicReference.get());
                }
            });
        }

        /* JADX INFO: renamed from: lambda$handleMessage$1$ru-big-town-anative-SetModesService$IncomingHandler, reason: not valid java name */
        /* synthetic */ void m2073x9883aa79(AtomicReference atomicReference) {
            PowerHoldController powerHoldController = SetModesService.this.powerHoldController;
            if (powerHoldController != null) {
                atomicReference.set(powerHoldController.activate());
            }
            Log.i(SetModesService.TAG, "power hold activation outcome=" + atomicReference.get());
        }

        /* JADX INFO: renamed from: lambda$handleMessage$4$ru-big-town-anative-SetModesService$IncomingHandler, reason: not valid java name */
        /* synthetic */ void m2075x4033befc() {
            WashModePolicy.Outcome outcomeActivate;
            WashModeController washModeController = SetModesService.this.washModeController;
            if (washModeController == null) {
                outcomeActivate = WashModePolicy.Outcome.TRANSPORT_FAILURE;
            } else {
                outcomeActivate = washModeController.activate();
            }
            Log.i(SetModesService.TAG, "wash mode activation outcome=" + outcomeActivate);
        }

        /* JADX INFO: renamed from: lambda$handleMessage$6$ru-big-town-anative-SetModesService$IncomingHandler, reason: not valid java name */
        /* synthetic */ void m2076x5aa921fe(boolean z) {
            if (MainActivity.sendSuspensionMaintenanceCommand(SetModesService.this, z)) {
                MainActivity.persistSavedToggle(SetModesService.this, "suspensionMaintenance", z);
            }
        }

        /* JADX INFO: renamed from: lambda$handleMessage$8$ru-big-town-anative-SetModesService$IncomingHandler, reason: not valid java name */
        /* synthetic */ void m2077x751e8500(String str) {
            SetModesReceiverDynamic.openFreeformApp(SetModesService.this, str, 0);
        }
    }

    WashModePolicy.Outcome activateVoiceWash() {
        WashModeController washModeController = this.washModeController;
        return washModeController == null ? WashModePolicy.Outcome.TRANSPORT_FAILURE : washModeController.activate();
    }

    void activateVoicePowerHold(final BooleanSupplier booleanSupplier, final Consumer<PowerHoldPolicy.Outcome> consumer) {
        final PowerHoldStatusTracker powerHoldStatusTracker = this.powerHoldStatusTracker;
        if (powerHoldStatusTracker == null) {
            consumer.accept(PowerHoldPolicy.Outcome.TRANSPORT_FAILURE);
        } else {
            powerHoldStatusTracker.beginActivation(new PowerHoldStatusTracker.ActivationReady() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda1
                @Override // ru.big.town.anative.PowerHoldStatusTracker.ActivationReady
                public final void onReady(long j) {
                    SetModesService.this.m2059x1ad7957a(booleanSupplier, powerHoldStatusTracker, consumer, j);
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$activateVoicePowerHold$2$ru-big-town-anative-SetModesService, reason: not valid java name */
    /* synthetic */ void m2059x1ad7957a(final BooleanSupplier booleanSupplier, final PowerHoldStatusTracker powerHoldStatusTracker, final Consumer consumer, final long j) {
        final AtomicReference atomicReference = new AtomicReference(PowerHoldPolicy.Outcome.TRANSPORT_FAILURE);
        ApplyEngine.postUserCommand("voice power hold", new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                SetModesService.this.m2058xf387ee78(booleanSupplier, atomicReference);
            }
        }, new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda25
            @Override // java.lang.Runnable
            public final void run() {
                SetModesService.lambda$activateVoicePowerHold$1(powerHoldStatusTracker, j, atomicReference, consumer);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$activateVoicePowerHold$0$ru-big-town-anative-SetModesService, reason: not valid java name */
    /* synthetic */ void m2058xf387ee78(BooleanSupplier booleanSupplier, AtomicReference atomicReference) {
        PowerHoldController powerHoldController;
        if (!booleanSupplier.getAsBoolean() || (powerHoldController = this.powerHoldController) == null) {
            return;
        }
        atomicReference.set(powerHoldController.activate());
    }

    static /* synthetic */ void lambda$activateVoicePowerHold$1(PowerHoldStatusTracker powerHoldStatusTracker, long j, AtomicReference atomicReference, Consumer consumer) {
        powerHoldStatusTracker.finishActivation(j, (PowerHoldPolicy.Outcome) atomicReference.get());
        consumer.accept((PowerHoldPolicy.Outcome) atomicReference.get());
    }

    boolean isVoiceServiceAction(String str) {
        return str.equals("apply") || str.equals("battery_heat") || str.equals("close_all") || str.equals("reboot") || str.startsWith("auto_light:");
    }

    void executeVoiceServiceAction(String str, final ResultReceiver resultReceiver) {
        try {
            if (str.equals("apply")) {
                ApplyEngine.applyNow(new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda10
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoiceCommandController.respond(resultReceiver, true, null);
                    }
                });
                return;
            }
            if (str.equals("battery_heat")) {
                sendBroadcast(new Intent(BatteryHeatService.ACTION_BATTERY_HEAT_ACTIVATE).setPackage(getPackageName()));
            } else if (str.equals("close_all")) {
                closeAllApps();
            } else if (str.equals("reboot")) {
                rebootSystem();
            } else if (str.startsWith("auto_light:")) {
                final boolean zEndsWith = str.endsWith(":on");
                ApplyEngine.postIndependentUserCommand("voice auto light", new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda12
                    @Override // java.lang.Runnable
                    public final void run() {
                        SetModesService.this.m2063x141b548b(zEndsWith, resultReceiver);
                    }
                });
                return;
            }
            VoiceCommandController.respond(resultReceiver, true, null);
        } catch (RuntimeException e) {
            Log.e(TAG, "Voice command failed", e);
            VoiceCommandController.respond(resultReceiver, false, "Не удалось выполнить команду");
        }
    }

    /* JADX INFO: renamed from: lambda$executeVoiceServiceAction$4$ru-big-town-anative-SetModesService, reason: not valid java name */
    /* synthetic */ void m2063x141b548b(boolean z, ResultReceiver resultReceiver) {
        try {
            AutoLightSettings.set(this, z);
            VoiceCommandController.respond(resultReceiver, true, null);
        } catch (RuntimeException unused) {
            VoiceCommandController.respond(resultReceiver, false, "Не удалось выполнить команду");
        }
    }

    private SharedPreferences prefs() {
        return getSharedPreferences("NativePrefs", 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFloatingBackEnabled(boolean z) {
        BackButtonService.setFloatingButtonEnabled(this, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFloatingBackSide(int i) {
        boolean z = (prefs().getInt("floatingBackSide", 0) == 1) != (i == 1);
        SharedPreferences.Editor editorPutInt = prefs().edit().putInt("floatingBackSide", i);
        if (z) {
            editorPutInt.putInt("floatingBackOffset", -1);
        }
        editorPutInt.apply();
        BackButtonService.updatePosition();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reassertFloatingBack() {
        if (prefs().getBoolean("floatingBack", false)) {
            if (BackButtonService.reshow()) {
                Log.i(TAG, "reassertFloatingBack: сервис жив → оверлей пере-показан");
                return;
            }
            Log.i(TAG, "reassertFloatingBack: сервис не подключён → форс-переустановка a11y");
            BackButtonService.disableForReconnect(this);
            this.mainHandler.removeCallbacks(this.floatingBackEnableRunnable);
            this.mainHandler.postDelayed(this.floatingBackEnableRunnable, 800L);
        }
    }

    private boolean readAutoLaunchFromProvider() {
        try {
            Cursor cursorQuery = getContentResolver().query(Uri.parse("content://ru.big.town.restoremode.restoremodecontentprovider/"), null, null, null, null);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToFirst() && cursorQuery.getColumnCount() > 16) {
                        return cursorQuery.getInt(16) == 1;
                    }
                } finally {
                    cursorQuery.close();
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "readAutoLaunchFromProvider: " + e.getMessage());
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeAutoLaunchRestoreMode() {
        if (!readAutoLaunchFromProvider()) {
            Log.i(TAG, "maybeAutoLaunchRestoreMode: autoLaunch=false — пропуск");
            return;
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        if (jElapsedRealtime - this.lastAutoLaunch < AUTO_LAUNCH_DEBOUNCE_MS) {
            Log.i(TAG, "maybeAutoLaunchRestoreMode: пропуск (дебаунс)");
            return;
        }
        this.lastAutoLaunch = jElapsedRealtime;
        try {
            Intent intent = new Intent();
            intent.setClassName(RESTOREMODE_PKG, RESTOREMODE_MAIN);
            intent.addFlags(872415232);
            try {
                startActivity(intent);
            } catch (Exception unused) {
            }
            final NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(new NotificationChannel("autolaunch_channel", "Автозапуск VoyahTune", 4));
                notificationManager.notify(4242, new NotificationCompat.Builder(this, "autolaunch_channel").setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle("VoyahTune").setContentText("Открытие приложения").setPriority(1).setCategory(NotificationCompat.CATEGORY_CALL).setFullScreenIntent(PendingIntent.getActivity(this, 0, intent, 201326592), true).setAutoCancel(true).setOngoing(false).build());
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        notificationManager.cancel(4242);
                    }
                }, EMBEDDED_LAUNCH_GRACE_MS);
            }
            Log.i(TAG, "maybeAutoLaunchRestoreMode: RestoreMode запущен (+fullScreenIntent)");
        } catch (Exception e) {
            Log.e(TAG, "maybeAutoLaunchRestoreMode failed: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void grantInstallPermission(String str, int i) {
        if (str == null || str.isEmpty()) {
            return;
        }
        try {
            if (i <= 0) {
                i = getPackageManager().getPackageUid(str, 0);
            }
            AppOpsManager.class.getMethod("setMode", Integer.TYPE, Integer.TYPE, String.class, Integer.TYPE).invoke((AppOpsManager) getSystemService("appops"), 66, Integer.valueOf(i), str, 0);
            Log.i(TAG, "grantInstall: " + str + " uid=" + i + " -> ALLOWED");
        } catch (Exception e) {
            boolean z = e instanceof InvocationTargetException;
            Throwable cause = e;
            if (z && e.getCause() != null) {
                cause = e.getCause();
            }
            Log.e(TAG, "grantInstall failed for " + str + ": " + cause);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeAllApps() {
        try {
            ActivityManager activityManager = (ActivityManager) getSystemService("activity");
            PackageManager packageManager = getPackageManager();
            int i = 0;
            ResolveInfo resolveInfoResolveActivity = packageManager.resolveActivity(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME"), 0);
            String str = (resolveInfoResolveActivity == null || resolveInfoResolveActivity.activityInfo == null) ? null : resolveInfoResolveActivity.activityInfo.packageName;
            Method method = ActivityManager.class.getMethod("forceStopPackage", String.class);
            for (ApplicationInfo applicationInfo : packageManager.getInstalledApplications(0)) {
                String str2 = applicationInfo.packageName;
                if ((applicationInfo.flags & 1) == 0 && !str2.equals(RESTOREMODE_PKG) && !str2.equals(BuildConfig.APPLICATION_ID) && !str2.equals(str) && !str2.startsWith("com.qinggan") && !str2.startsWith("com.android.car")) {
                    try {
                        method.invoke(activityManager, str2);
                        i++;
                    } catch (Exception e) {
                        boolean z = e instanceof InvocationTargetException;
                        Throwable cause = e;
                        if (z && e.getCause() != null) {
                            cause = e.getCause();
                        }
                        Log.e(TAG, "forceStop failed " + str2 + ": " + cause);
                    }
                }
            }
            Log.i(TAG, "closeAllApps: остановлено " + i + " сторонних приложений");
        } catch (Exception e2) {
            Log.e(TAG, "closeAllApps failed: " + e2.getMessage());
        }
    }

    private void launchVirtualSplit(String str, String str2, int i, int i2, int i3) {
        launchVirtualSplit(str, str2, i, i2, i3, false, 0.0f, -1, "");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchVirtualSplit(String str, String str2, int i, int i2, int i3, boolean z, float f, int i4, String str3) {
        if (str == null || str.isEmpty() || str2 == null || str2.isEmpty()) {
            Log.w(TAG, "launchVirtualSplit: нужны два пакета");
            return;
        }
        try {
            Settings.Global.putInt(getContentResolver(), "enable_freeform_support", 1);
            Settings.Global.putInt(getContentResolver(), "force_resizable_activities", 1);
        } catch (Exception e) {
            Log.w(TAG, "freeform settings: " + e.getMessage());
        }
        try {
            Intent intent = new Intent(this, (Class<?>) SplitHostActivity.class);
            intent.addFlags(335544320);
            intent.putExtra(SplitHostActivity.EXTRA_LEFT, str);
            intent.putExtra(SplitHostActivity.EXTRA_RIGHT, str2);
            intent.putExtra(SplitHostActivity.EXTRA_RATIO, i);
            intent.putExtra(SplitHostActivity.EXTRA_LEFT_DPI, i2);
            intent.putExtra(SplitHostActivity.EXTRA_RIGHT_DPI, i3);
            intent.putExtra(SplitHostActivity.EXTRA_RESIZABLE, z);
            intent.putExtra(SplitHostActivity.EXTRA_SPLIT, f);
            intent.putExtra(SplitHostActivity.EXTRA_PRESET_IDX, i4);
            intent.putExtra(SplitHostActivity.EXTRA_PRESET_ID, str3);
            DockLaunchGuard.arm(this, 0, BuildConfig.APPLICATION_ID);
            startActivity(intent);
            Log.i(TAG, "launchVirtualSplit host started");
        } catch (Exception e2) {
            Log.e(TAG, "launchVirtualSplit failed: " + e2.getMessage());
        }
    }

    private boolean hasTaskOnDisplay(String str, int i) {
        List<ActivityManager.RunningTaskInfo> runningTasks;
        try {
            ActivityManager activityManager = (ActivityManager) getSystemService("activity");
            if (activityManager == null || (runningTasks = activityManager.getRunningTasks(100)) == null) {
                return true;
            }
            for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTasks) {
                ComponentName componentName = runningTaskInfo.baseActivity != null ? runningTaskInfo.baseActivity : runningTaskInfo.topActivity;
                if (componentName != null && str.equals(componentName.getPackageName()) && runningTaskInfo.getClass().getField("displayId").getInt(runningTaskInfo) == i) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            Log.w(TAG, "hasTaskOnDisplay: " + e.getMessage());
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startEmbeddedDisplay(final String str, String str2, Surface surface, int i, int i2, int i3) {
        Surface surface2;
        int i4;
        int i5;
        VirtualDisplay virtualDisplayCreateVirtualDisplay;
        if (str == null || str.isEmpty() || str2 == null || str2.isEmpty() || surface == null || !surface.isValid() || i <= 0 || i2 <= 0) {
            return;
        }
        try {
            VirtualDisplay virtualDisplay = this.embeddedDisplays.get(str);
            Long l = this.embeddedLaunchAt.get(str);
            boolean z = l == null || SystemClock.elapsedRealtime() - l.longValue() > EMBEDDED_LAUNCH_GRACE_MS;
            if (virtualDisplay != null && z && !hasTaskOnDisplay(str2, virtualDisplay.getDisplay().getDisplayId())) {
                Log.i(TAG, "embedded VD stale widget=" + str + " pkg=" + str2 + " — пересоздаём дисплей");
                releaseEmbeddedDisplay(str);
                virtualDisplay = null;
            }
            if (virtualDisplay != null) {
                virtualDisplay.setSurface(surface);
                virtualDisplay.resize(i, i2, i3 > 0 ? i3 : 213);
            } else {
                DisplayManager displayManager = (DisplayManager) getSystemService("display");
                try {
                    virtualDisplayCreateVirtualDisplay = displayManager.createVirtualDisplay("voyah-app-widget-" + str, i, i2, i3 > 0 ? i3 : 213, surface, 1289);
                    surface2 = surface;
                    i4 = i;
                    i5 = i2;
                } catch (Exception unused) {
                    surface2 = surface;
                    i4 = i;
                    i5 = i2;
                    virtualDisplayCreateVirtualDisplay = displayManager.createVirtualDisplay("voyah-app-widget-" + str, i4, i5, i3 > 0 ? i3 : 213, surface2, 265);
                }
                virtualDisplay = virtualDisplayCreateVirtualDisplay;
                if (virtualDisplay == null) {
                    return;
                }
                virtualDisplay.setSurface(surface2);
                this.embeddedDisplays.put(str, virtualDisplay);
                this.embeddedPackages.put(str, str2);
                this.embeddedLaunched.put(str, false);
                Log.i(TAG, "embedded VD created widget=" + str + " display=" + virtualDisplay.getDisplay().getDisplayId() + " " + i4 + "x" + i5);
            }
            if (Boolean.TRUE.equals(this.embeddedLaunched.get(str))) {
                return;
            }
            AppDisplayLauncher.launch(getApplicationContext(), str2, virtualDisplay.getDisplay().getDisplayId(), false, new BooleanSupplier() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda15
                @Override // java.util.function.BooleanSupplier
                public final boolean getAsBoolean() {
                    return SetModesService.this.m2071x1ced41ef(str);
                }
            }, new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    SetModesService.this.m2072x30951570(str);
                }
            });
            this.embeddedLaunched.put(str, true);
            this.embeddedLaunchAt.put(str, Long.valueOf(SystemClock.elapsedRealtime()));
        } catch (Exception e) {
            Log.e(TAG, "embedded VD failed widget=" + str + ": " + e.getMessage());
        }
    }

    /* JADX INFO: renamed from: lambda$startEmbeddedDisplay$6$ru-big-town-anative-SetModesService, reason: not valid java name */
    /* synthetic */ boolean m2071x1ced41ef(String str) {
        return this.embeddedDisplays.containsKey(str);
    }

    /* JADX INFO: renamed from: lambda$startEmbeddedDisplay$7$ru-big-town-anative-SetModesService, reason: not valid java name */
    /* synthetic */ void m2072x30951570(String str) {
        this.embeddedLaunched.put(str, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseEmbeddedDisplay(String str) {
        VirtualDisplay virtualDisplayRemove = this.embeddedDisplays.remove(str);
        this.embeddedPackages.remove(str);
        this.embeddedLaunched.remove(str);
        this.embeddedLaunchAt.remove(str);
        if (virtualDisplayRemove != null) {
            try {
                virtualDisplayRemove.release();
            } catch (Exception unused) {
            }
            Log.i(TAG, "embedded VD released widget=" + str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveEmbeddedDisplay(Bundle bundle) {
        VirtualDisplay virtualDisplayRemove = null;
        String string = bundle.getString("fromWidgetId", "");
        String string2 = bundle.getString("widgetId", "");
        String string3 = bundle.getString(Car.PACKAGE_SERVICE, "");
        Surface surface = (Surface) bundle.getParcelable("surface");
        int i = bundle.getInt("width", 0);
        int i2 = bundle.getInt(SuspensionWidgetProtocol.HEIGHT, 0);
        int i3 = bundle.getInt("dpi", 0);
        if (string2 == null || string2.isEmpty()) {
            return;
        }
        if (string != null) {
            try {
                virtualDisplayRemove = string.isEmpty() ? null : this.embeddedDisplays.remove(string);
            } catch (Exception e) {
                Log.e(TAG, "embedded move failed: " + e.getMessage());
                return;
            }
        }
        if (virtualDisplayRemove != null) {
            this.embeddedPackages.remove(string);
            this.embeddedLaunched.remove(string);
            this.embeddedLaunchAt.remove(string);
        }
        if (virtualDisplayRemove == null) {
            Log.i(TAG, "embedded move: нет источника, запуск заново в " + string2);
            startEmbeddedDisplay(string2, string3, surface, i, i2, i3);
            return;
        }
        VirtualDisplay virtualDisplay = this.embeddedDisplays.get(string2);
        if (virtualDisplay != null && virtualDisplay != virtualDisplayRemove) {
            releaseEmbeddedDisplay(string2);
        }
        this.embeddedDisplays.put(string2, virtualDisplayRemove);
        this.embeddedPackages.put(string2, string3);
        this.embeddedLaunched.put(string2, true);
        this.embeddedLaunchAt.put(string2, Long.valueOf(SystemClock.elapsedRealtime()));
        if (surface != null && surface.isValid()) {
            virtualDisplayRemove.setSurface(surface);
            if (i > 0 && i2 > 0) {
                if (i3 <= 0) {
                    i3 = 213;
                }
                virtualDisplayRemove.resize(i, i2, i3);
            }
        }
        Log.i(TAG, "embedded VD moved " + string + " -> " + string2 + " pkg=" + string3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void swapEmbeddedDisplays(Bundle bundle) {
        String string = bundle.getString("widgetId", "");
        String string2 = bundle.getString("widgetId2", "");
        if (string == null || string2 == null || string.isEmpty() || string2.isEmpty() || string.equals(string2)) {
            return;
        }
        Surface surface = (Surface) bundle.getParcelable("surface");
        Surface surface2 = (Surface) bundle.getParcelable("surface2");
        int i = bundle.getInt("width", 0);
        int i2 = bundle.getInt(SuspensionWidgetProtocol.HEIGHT, 0);
        int i3 = bundle.getInt("dpi", 0);
        int i4 = bundle.getInt("width2", 0);
        int i5 = bundle.getInt("height2", 0);
        int i6 = bundle.getInt("dpi2", 0);
        try {
            VirtualDisplay virtualDisplay = this.embeddedDisplays.get(string);
            VirtualDisplay virtualDisplay2 = this.embeddedDisplays.get(string2);
            if (virtualDisplay != null && virtualDisplay2 != null) {
                String str = this.embeddedPackages.get(string);
                String str2 = this.embeddedPackages.get(string2);
                this.embeddedDisplays.put(string, virtualDisplay2);
                this.embeddedDisplays.put(string2, virtualDisplay);
                this.embeddedPackages.put(string, str2);
                this.embeddedPackages.put(string2, str);
                long jElapsedRealtime = SystemClock.elapsedRealtime();
                this.embeddedLaunched.put(string, true);
                this.embeddedLaunched.put(string2, true);
                this.embeddedLaunchAt.put(string, Long.valueOf(jElapsedRealtime));
                this.embeddedLaunchAt.put(string2, Long.valueOf(jElapsedRealtime));
                try {
                    virtualDisplay.setSurface(null);
                } catch (Exception unused) {
                }
                try {
                    virtualDisplay2.setSurface(null);
                } catch (Exception unused2) {
                }
                if (surface != null && surface.isValid()) {
                    virtualDisplay2.setSurface(surface);
                    if (i > 0 && i2 > 0) {
                        virtualDisplay2.resize(i, i2, i3 > 0 ? i3 : 213);
                    }
                }
                if (surface2 != null && surface2.isValid()) {
                    virtualDisplay.setSurface(surface2);
                    if (i4 > 0 && i5 > 0) {
                        virtualDisplay.resize(i4, i5, i6 > 0 ? i6 : 213);
                    }
                }
                Log.i(TAG, "embedded VD swapped " + string + " <-> " + string2);
                return;
            }
            Log.w(TAG, "embedded swap: нет дисплея для " + string + " или " + string2);
        } catch (Exception e) {
            Log.e(TAG, "embedded swap failed: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void injectEmbeddedTouch(String str, MotionEvent motionEvent) {
        VirtualDisplay virtualDisplay = this.embeddedDisplays.get(str);
        if (virtualDisplay == null || motionEvent == null) {
            return;
        }
        MotionEvent motionEventObtain = null;
        try {
            motionEventObtain = MotionEvent.obtain(motionEvent);
            MotionEvent.class.getMethod("setDisplayId", Integer.TYPE).invoke(motionEventObtain, Integer.valueOf(virtualDisplay.getDisplay().getDisplayId()));
            Object systemService = getSystemService("input");
            systemService.getClass().getMethod("injectInputEvent", InputEvent.class, Integer.TYPE).invoke(systemService, motionEventObtain, 0);
        } catch (Exception e) {
            Log.w(TAG, "embedded touch failed: " + e.getMessage());
        } finally {
            if (motionEventObtain != null) {
                motionEventObtain.recycle();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLoggingEnabled(boolean z) {
        prefs().edit().putBoolean("logging", z).apply();
        if (z) {
            NativeLog.get().start(getApplicationContext());
        } else {
            NativeLog.get().stopAndDelete(getApplicationContext());
        }
    }

    private void restoreLoggingState() {
        if (prefs().getBoolean("logging", false)) {
            NativeLog.get().start(getApplicationContext());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void shareLogFile() {
        try {
            File fileLogFile = NativeLog.get().logFile(getApplicationContext());
            if (fileLogFile != null && fileLogFile.exists()) {
                Uri uriForFile = FileProvider.getUriForFile(this, "ru.big.town.anative.fileprovider", fileLogFile);
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.STREAM", uriForFile);
                intent.putExtra("android.intent.extra.SUBJECT", fileLogFile.getName());
                intent.setClipData(ClipData.newRawUri(fileLogFile.getName(), uriForFile));
                intent.addFlags(1);
                Intent intentCreateChooser = Intent.createChooser(intent, "Выгрузить логи");
                intentCreateChooser.addFlags(268435457);
                startActivity(intentCreateChooser);
                Log.i(TAG, "shareLogFile: share " + uriForFile);
                return;
            }
            Log.w(TAG, "shareLogFile: файла нет");
        } catch (Exception e) {
            Log.e(TAG, "shareLogFile failed: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void rebootSystem() {
        try {
            PowerManager powerManager = (PowerManager) getSystemService(Car.POWER_SERVICE);
            if (powerManager != null) {
                Log.i(TAG, "rebootSystem: PowerManager.reboot()");
                powerManager.reboot(null);
            } else {
                Log.e(TAG, "rebootSystem: PowerManager == null");
            }
        } catch (Exception e) {
            Log.e(TAG, "rebootSystem failed: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyTheme(int i) {
        if (i < 0 || i > 3) {
            i = 0;
        }
        try {
            Settings.Secure.putInt(getContentResolver(), "ui_night_mode", i);
        } catch (Exception e) {
            Log.w(TAG, "applyTheme secure ui_night_mode: " + e.getMessage());
        }
        try {
            UiModeManager uiModeManager = (UiModeManager) getSystemService("uimode");
            if (uiModeManager != null) {
                uiModeManager.setNightMode(i);
            }
        } catch (Exception e2) {
            Log.w(TAG, "applyTheme setNightMode (нет MODIFY_DAY_NIGHT_MODE?): " + e2.getMessage());
        }
        Log.i(TAG, "applyTheme mode=" + i);
    }

    private void restoreAutoLightState() {
        ApplyEngine.postWakeAction("restore auto light service switch", new BooleanSupplier() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda2
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                return SetModesService.this.m2069x2d5b5559();
            }
        }, (Consumer<ApplyEngine.WakeActionResult>) null);
    }

    /* JADX INFO: renamed from: lambda$restoreAutoLightState$8$ru-big-town-anative-SetModesService, reason: not valid java name */
    /* synthetic */ boolean m2069x2d5b5559() {
        AutoLightSettings.restore(this);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAutoLightEnabled(final boolean z) {
        ApplyEngine.postIndependentUserCommand("auto light switch", new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                SetModesService.this.m2070lambda$setAutoLightEnabled$9$rubigtownanativeSetModesService(z);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$setAutoLightEnabled$9$ru-big-town-anative-SetModesService, reason: not valid java name */
    /* synthetic */ void m2070lambda$setAutoLightEnabled$9$rubigtownanativeSetModesService(boolean z) {
        AutoLightSettings.set(this, z);
    }

    private void resetWiperColdOnPowerOn() {
        boolean z = prefs().getBoolean("wiperCold", false);
        boolean z2 = prefs().getBoolean("wiperServiceActive", false);
        if (z || z2) {
            Intent intent = new Intent(this, (Class<?>) WiperColdService.class);
            intent.setAction(WiperColdService.ACTION_POWER_ON_RESET);
            startForegroundService(intent);
            Log.i(TAG, "resetWiperColdOnPowerOn: sent POWER_ON_RESET (enabled=" + z + " active=" + z2 + ")");
        }
    }

    private void forwardPowerOnToTripStats() {
        Intent intent = new Intent(this, (Class<?>) TripStatsService.class);
        intent.setAction(TripStatsService.ACTION_POWER_ON);
        startForegroundService(intent);
    }

    private void startTripStatsService() {
        startForegroundService(new Intent(this, (Class<?>) TripStatsService.class));
    }

    private void startBatteryHeatService() {
        BatteryHeatService.requestStartup(this);
    }

    private void startNowPlayingService() {
        startForegroundService(new Intent(this, (Class<?>) NowPlayingService.class));
    }

    private void restoreWiperColdState() {
        boolean z = prefs().getBoolean("wiperCold", false);
        boolean z2 = prefs().getBoolean("pauseMediaOnDoor", false);
        Log.i(TAG, "restoreWiperColdState: wiperCold=" + z + " pauseMediaOnDoor=" + z2);
        if (z || z2) {
            startForegroundService(new Intent(this, (Class<?>) WiperColdService.class));
        }
    }

    /* JADX INFO: renamed from: lambda$new$10$ru-big-town-anative-SetModesService, reason: not valid java name */
    /* synthetic */ void m2065lambda$new$10$rubigtownanativeSetModesService() {
        try {
            startNowPlayingService();
        } catch (Exception e) {
            Log.w(TAG, "startNowPlayingService: " + e.getMessage());
        }
    }

    /* JADX INFO: renamed from: lambda$new$11$ru-big-town-anative-SetModesService, reason: not valid java name */
    /* synthetic */ void m2066lambda$new$11$rubigtownanativeSetModesService() {
        BackButtonService.setFloatingButtonEnabled(this, true);
    }

    static void notifyEmbeddedTaskLeft(Context context, String str) {
        Intent intent = new Intent(ACTION_EMBEDDED_TASK_LEFT);
        intent.setPackage(RESTOREMODE_PKG);
        intent.putExtra(EXTRA_EMBEDDED_TASK_PKG, str);
        try {
            context.sendBroadcast(intent, BIND_PERMISSION);
        } catch (RuntimeException e) {
            Log.w(TAG, "notifyEmbeddedTaskLeft failed: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void publishPowerHoldStatus(PowerHoldStatusPolicy.Snapshot snapshot, PowerHoldPolicy.Outcome outcome, boolean z) {
        Intent intent = new Intent(ACTION_POWER_HOLD_STATUS_UPDATE);
        intent.setPackage(RESTOREMODE_PKG);
        intent.putExtra("status", snapshot.status.ipcCode);
        intent.putExtra(EXTRA_POWER_HOLD_EXIT_REASON, snapshot.exitReason.ipcCode);
        intent.putExtra(EXTRA_POWER_HOLD_REQUEST_OUTCOME, outcome == null ? 0 : outcome.ipcCode);
        try {
            sendBroadcast(intent, BIND_PERMISSION);
        } catch (RuntimeException e) {
            Log.w(TAG, "publishPowerHoldStatus failed: " + e.getMessage());
        }
    }

    private boolean beginWakeSession() {
        if (this.wakeSessionActive) {
            return false;
        }
        this.wakeSessionActive = true;
        return true;
    }

    private void endWakeSession() {
        this.wakeSessionActive = false;
    }

    private boolean ancillaryWakeSessionCurrent() {
        if (this.sleepController.isCurrentSession(this.ancillaryWakeSession)) {
            return true;
        }
        Log.i(TAG, "ancillary wake task dropped: session " + this.ancillaryWakeSession + " is stale (current " + this.sleepController.sessionId() + ")");
        return false;
    }

    private void scheduleAncillaryWakeTasks() {
        this.ancillaryWakeSession = this.sleepController.sessionId();
        this.mainHandler.removeCallbacks(this.startNowPlayingRunnable);
        this.mainHandler.postDelayed(this.startNowPlayingRunnable, 6000L);
        this.mainHandler.removeCallbacks(this.reassertFloatingBackRunnable);
        this.mainHandler.postDelayed(this.reassertFloatingBackRunnable, EMBEDDED_LAUNCH_GRACE_MS);
        this.mainHandler.removeCallbacks(this.autoLaunchRunnable);
        this.mainHandler.postDelayed(this.autoLaunchRunnable, CAR_POWER_RECONNECT_DELAY_MS);
    }

    private void cancelAncillaryWakeTasks() {
        this.mainHandler.removeCallbacks(this.startNowPlayingRunnable);
        this.mainHandler.removeCallbacks(this.reassertFloatingBackRunnable);
        this.mainHandler.removeCallbacks(this.autoLaunchRunnable);
        this.mainHandler.removeCallbacks(this.floatingBackEnableRunnable);
    }

    private void scheduleVoicePrewarm() {
        this.mainHandler.removeCallbacks(this.voicePrewarmRunnable);
        this.mainHandler.postDelayed(this.voicePrewarmRunnable, VOICE_PREWARM_DELAY_MS);
        Log.i(TAG, "Voice prewarm scheduled in " + VOICE_PREWARM_DELAY_MS + "ms");
    }

    private void cancelVoicePrewarm() {
        this.mainHandler.removeCallbacks(this.voicePrewarmRunnable);
    }

    private void triggerVoicePrewarm() {
        Log.i(TAG, "Voice prewarm triggered after ACC ON");
        Intent intent = new Intent();
        intent.setClassName(RESTOREMODE_PKG, "ru.big.town.restoremode.VoiceWarmupService");
        try {
            startForegroundService(intent);
        } catch (RuntimeException e) {
            Log.w(TAG, "Voice prewarm startForegroundService failed", e);
        }
    }

    private void requestSavedConfigSync(String str) {
        Intent intent = new Intent(RESTOREMODE_CONFIG_SYNC_ACTION);
        intent.setClassName(RESTOREMODE_PKG, RESTOREMODE_CONFIG_SYNC_RECEIVER);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        try {
            sendBroadcast(intent);
            Log.i(TAG, "saved config sync requested by " + str);
        } catch (RuntimeException e) {
            Log.w(TAG, "saved config sync request failed: " + e.getMessage());
        }
    }

    private void runWakeSideEffects(String source) {
        if (this.serviceDestroyed) {
            return;
        }
        if (beginWakeSession()) {
            requestSavedConfigSync("physical wake");
            restoreAutoLightState();
            resetWiperColdOnPowerOn();
            forwardPowerOnToTripStats();
            BatteryHeatService.requestPhysicalWake(this);
            scheduleAncillaryWakeTasks();
            Log.i(TAG, "wake side-effects started by " + source);
            return;
        }
        Log.i(TAG, "wake side-effects coalesced for " + source);
    }

    private void requestWashModeCleanup(final String str) {
        final WashModeController washModeController = this.washModeController;
        if (washModeController == null || !washModeController.hasArmedRequest()) {
            return;
        }
        ApplyEngine.postIndependentUserCommand("wash mode cleanup: " + str, new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                WashModeController washModeController2 = washModeController;
                String str2 = str;
                Log.i(SetModesService.TAG, "wash mode request cleanup " + (washModeController2.cleanupRequestBit(str2) ? "accepted" : "deferred") + " by " + str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleScreenOffFallback() {
        this.screenOffObserved = true;
        this.pendingPhysicalWake = false;
        endWakeSession();
        cancelAncillaryWakeTasks();
        requestWashModeCleanup("SCREEN_OFF");
        this.sleepController.onSleepComplete();
        AvasController.get().onSleep("SCREEN_OFF");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleScreenOnFallback() {
        this.screenOffObserved = false;
        requestWashModeCleanup("SCREEN_ON");
        if (this.pendingPhysicalWake) {
            this.pendingPhysicalWake = false;
            if (this.sleepController.isCurrentSession(this.pendingWakeSession)) {
                runWakeSideEffects("deferred CarPower wake");
            } else {
                Log.i(TAG, "deferred CarPower wake dropped: session " + this.pendingWakeSession + " is stale (current " + this.sleepController.sessionId() + ")");
            }
        }
        this.sleepController.onWakeComplete();
        AvasController.get().onWakeEvent("SCREEN_ON");
    }

    private boolean isScreenInteractive() {
        try {
            PowerManager powerManager = (PowerManager) getSystemService(Car.POWER_SERVICE);
            return powerManager != null && powerManager.isInteractive();
        } catch (Throwable unused) {
            return !this.screenOffObserved;
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        final SetModesService setModesService = this;
        Log.i(TAG, "onCreate()");
        super.onCreate();
        ApplyEngine.activateWake("service create");
        ApolloSettingsRuntimeState.isEnabled(this);
        this.washModeController = WashModeController.create(this);
        this.powerHoldController = PowerHoldController.create(this);
        this.powerHoldStatusTracker = PowerHoldStatusTracker.create(this, new PowerHoldStatusTracker.StatusListener() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda7
            @Override // ru.big.town.anative.PowerHoldStatusTracker.StatusListener
            public final void onStatus(PowerHoldStatusPolicy.Snapshot snapshot, PowerHoldPolicy.Outcome outcome, boolean z) {
                SetModesService.this.publishPowerHoldStatus(snapshot, outcome, z);
            }
        });
        try {
            this.vehicleStateControllers = VehicleStateControllers.get(getApplicationContext());
        } catch (RuntimeException e) {
            Log.w(TAG, "start vehicle state controllers: " + e.getMessage());
        }
        AvasController.get().init(getApplicationContext());
            try {
                ContextCompat.registerReceiver(setModesService, this.powerHoldStatusRequestReceiver, new IntentFilter(ACTION_REQUEST_POWER_HOLD_STATUS), BIND_PERMISSION, this.mainHandler, 2);
                setModesService.powerHoldStatusReceiverRegistered = true;
            } catch (RuntimeException e2) {
                RuntimeException e = e2;
                Log.w(TAG, "register Power Hold status receiver: " + e.getMessage());
            }
        setModesService.screenOffObserved = !setModesService.isScreenInteractive();
        setModesService.initializeCarPowerManager();
        setModesService.setModesReceiverDynamic = new SetModesReceiverDynamic(new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                SetModesService.this.handleScreenOffFallback();
            }
        }, new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                SetModesService.this.handleScreenOnFallback();
            }
        }, setModesService.sleepController);
        ScreenLiftTaskRestorer screenLiftTaskRestorer = new ScreenLiftTaskRestorer(setModesService.getApplicationContext());
        setModesService.screenLiftTaskRestorer = screenLiftTaskRestorer;
        screenLiftTaskRestorer.register();
        try {
            IntentFilter intentFilter = new IntentFilter(ACTION_REQUEST_LOG);
            intentFilter.addAction(ACTION_LOGGING_SET);
            intentFilter.addAction(ACTION_LOGGING_SHARE);
            ContextCompat.registerReceiver(setModesService, setModesService.logRequestReceiver, intentFilter, 2);
        } catch (Exception e4) {
            Log.w(TAG, "register logRequestReceiver: " + e4.getMessage());
        }
        setModesService.restoreLoggingState();
        Log.i(TAG, "onCreated");
    }

    private void handlePowerStateChanged(int i) {
        if (this.serviceDestroyed) {
            return;
        }
        Log.i(TAG, "Power state changed: " + i + " (" + powerStateName(i) + ")");
        if (isWakeState(i)) {
            this.sleepController.onWakeTrigger(SleepController.Event.POWER_WAKE);
            requestWashModeCleanup("power state " + powerStateName(i));
            ApplyEngine.activateWake("power state " + powerStateName(i));
            if (isScreenInteractive() || i == 6 || i == 8) {
                this.screenOffObserved = false;
                this.pendingPhysicalWake = false;
                runWakeSideEffects(powerStateName(i));
                this.sleepController.onWakeComplete();
                AvasController.get().onWakeEvent("power " + powerStateName(i));
                scheduleVoicePrewarm();
                return;
            } else {
                this.pendingPhysicalWake = true;
                this.pendingWakeSession = this.sleepController.sessionId();
                Log.i(TAG, "physical wake side-effects deferred until SCREEN_ON");
                return;
            }
        }
        if (isSleepOrShutdownState(i)) {
            this.screenOffObserved = true;
            this.pendingPhysicalWake = false;
            this.sleepController.onSleepTrigger(SleepController.Event.POWER_SLEEP);
            endWakeSession();
            cancelAncillaryWakeTasks();
            requestWashModeCleanup("power state " + powerStateName(i));
            ApplyEngine.resetRestoreGate("power state " + powerStateName(i));
            this.sleepController.onSleepComplete();
            AvasController.get().onSleep("power " + powerStateName(i));
            cancelVoicePrewarm();
        }
        Log.i(TAG, "onStateChanged() ignored state: " + i);
    }

    private static String powerStateName(int i) {
        switch (i) {
            case 1:
                return "WAIT_FOR_VHAL";
            case 2:
                return "SUSPEND_ENTER";
            case 3:
                return "SUSPEND_EXIT";
            case 4:
            default:
                return "STATE_" + i;
            case 5:
                return "SHUTDOWN_ENTER";
            case 6:
                return "ON";
            case 7:
                return "SHUTDOWN_PREPARE";
            case 8:
                return "SHUTDOWN_CANCELLED";
        }
    }

    private void initializeCarPowerManager() {
        HandlerThread handlerThread = new HandlerThread("SetModesCarPower");
        this.carPowerThread = handlerThread;
        handlerThread.start();
        final Handler handler = new Handler(this.carPowerThread.getLooper());
        this.carPowerHandler = handler;
        if (handler.post(new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                SetModesService.this.m2064xf1a7ca1(handler);
            }
        })) {
            return;
        }
        this.carPowerHandler = null;
        this.carPowerThread.quitSafely();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: createCarPowerConnectionOnWorker, reason: merged with bridge method [inline-methods] */
    public void m2064xf1a7ca1(Handler handler) {
        if (this.serviceDestroyed || this.carPowerHandler != handler) {
            return;
        }
        try {
            Car carCreateCar = Car.createCar(getApplicationContext(), handler, 0L, new Car.CarServiceLifecycleListener() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda11
                @Override // android.car.Car.CarServiceLifecycleListener
                public final void onLifecycleChanged(Car car, boolean z) {
                    SetModesService.this.dispatchCarLifecycleToWorker(car, z);
                }
            });
            if (carCreateCar == null) {
                Log.e(TAG, "Car.createCar returned null");
                scheduleCarPowerReconnectOnWorker("create returned null", CAR_POWER_RECONNECT_DELAY_MS);
                return;
            }
            if (!this.serviceDestroyed && this.carPowerHandler == handler) {
                this.mCar = carCreateCar;
                scheduleCarPowerReconnectOnWorker("connect watchdog", CAR_POWER_CONNECT_WATCHDOG_MS);
                return;
            }
            carCreateCar.disconnect();
        } catch (Throwable th) {
            Log.e(TAG, "Error initializing CarPowerManager", th);
            scheduleCarPowerReconnectOnWorker("create failed", CAR_POWER_RECONNECT_DELAY_MS);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchCarLifecycleToWorker(final Car car, final boolean z) {
        Handler handler = this.carPowerHandler;
        if (this.serviceDestroyed || handler == null || handler.post(new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                SetModesService.this.m2060x67cbbf8b(car, z);
            }
        })) {
            return;
        }
        Log.w(TAG, "Car lifecycle dropped: worker stopped, ready=" + z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: handleCarLifecycleOnWorker, reason: merged with bridge method [inline-methods] */
    public void m2060x67cbbf8b(Car car, boolean z) {
        if (this.serviceDestroyed || car != this.mCar) {
            return;
        }
        Log.i(TAG, "Car lifecycle: ready=" + z);
        if (!z) {
            this.carPowerCallbackGate.invalidateCurrent();
            clearPublishedCarPowerManager(this.carPowerManager);
            scheduleCarPowerReconnectOnWorker("lifecycle disconnected", CAR_POWER_RECONNECT_DELAY_MS);
            return;
        }
        Handler handler = this.carPowerHandler;
        if (handler != null) {
            handler.removeCallbacks(this.carPowerReconnectRunnable);
        }
        try {
            CarPowerManager carPowerManager = (CarPowerManager) car.getCarManager(Car.POWER_SERVICE);
            if (!this.serviceDestroyed && car == this.mCar) {
                if (carPowerManager == null) {
                    this.carPowerCallbackGate.invalidateCurrent();
                    clearPublishedCarPowerManager(this.carPowerManager);
                    Log.e(TAG, "Failed to get CarPowerManager");
                    scheduleCarPowerReconnectOnWorker("power manager unavailable", CAR_POWER_RECONNECT_DELAY_MS);
                    return;
                }
                CarPowerManager carPowerManager2 = this.carPowerManager;
                if (carPowerManager2 != null && carPowerManager2 != carPowerManager) {
                    this.carPowerCallbackGate.invalidateCurrent();
                    clearPublishedCarPowerManager(carPowerManager2);
                    try {
                        carPowerManager2.clearListener();
                    } catch (Throwable th) {
                        Log.w(TAG, "clear stale CarPower listener failed: " + th.getMessage());
                    }
                }
                this.carPowerManager = carPowerManager;
                GlobalVars.mCarPowerManager = carPowerManager;
                registerPowerStateListenerOnWorker(carPowerManager);
            }
        } catch (Throwable th2) {
            this.carPowerCallbackGate.invalidateCurrent();
            clearPublishedCarPowerManager(this.carPowerManager);
            Log.e(TAG, "getCarManager(POWER_SERVICE) failed", th2);
            scheduleCarPowerReconnectOnWorker("power manager failed", CAR_POWER_RECONNECT_DELAY_MS);
        }
    }

    private void registerPowerStateListenerOnWorker(CarPowerManager carPowerManager) {
        Throwable th;
        final long jBeginRegistration;
        this.carPowerCallbackGate.invalidateCurrent();
        try {
            carPowerManager.clearListener();
        } catch (Throwable unused) {
        }
        long j = -1;
        try {
            if (!this.serviceDestroyed && carPowerManager == this.carPowerManager) {
                jBeginRegistration = this.carPowerCallbackGate.beginRegistration();
                if (jBeginRegistration == -1) {
                    return;
                }
                try {
                    carPowerManager.setListener(new CarPowerManager.CarPowerStateListener() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda4
                        @Override // android.car.hardware.power.CarPowerManager.CarPowerStateListener
                        public final void onStateChanged(int i) {
                            SetModesService.this.m2067x7a2667b5(jBeginRegistration, i);
                        }
                    });
                    Log.i(TAG, "CarPowerStateListener registered");
                } catch (NoSuchMethodError unused2) {
                    j = jBeginRegistration;
                    this.carPowerCallbackGate.invalidate(j);
                    clearPublishedCarPowerManager(carPowerManager);
                    Log.w(TAG, "setListener(Listener) not available on this platform, skipping");
                } catch (Throwable th2) {
                    this.carPowerCallbackGate.invalidate(jBeginRegistration);
                    clearPublishedCarPowerManager(carPowerManager);
                    Log.e(TAG, "setListener failed: " + th2.getMessage());
                    scheduleCarPowerReconnectOnWorker("listener registration failed", CAR_POWER_RECONNECT_DELAY_MS);
                }
            }
        } catch (NoSuchMethodError unused3) {
        } catch (Throwable th3) {
            th = th3;
        }
    }

    private void scheduleCarPowerReconnectOnWorker(String str, long j) {
        Handler handler = this.carPowerHandler;
        if (this.serviceDestroyed || handler == null) {
            return;
        }
        handler.removeCallbacks(this.carPowerReconnectRunnable);
        if (handler.postDelayed(this.carPowerReconnectRunnable, j)) {
            Log.i(TAG, "CarPower reconnect scheduled in " + j + "ms: " + str);
        } else {
            Log.w(TAG, "CarPower reconnect dropped: worker stopped");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reconnectCarPowerOnWorker() {
        Handler handler = this.carPowerHandler;
        if (this.serviceDestroyed || handler == null || Looper.myLooper() != handler.getLooper()) {
            return;
        }
        Log.w(TAG, "CarPower connection watchdog fired; recreating connection");
        this.carPowerCallbackGate.invalidateCurrent();
        releaseCarPowerManagerOnWorker(this.carPowerManager);
        m2064xf1a7ca1(handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: dispatchPowerStateFromBinder, reason: merged with bridge method [inline-methods] */
    public void m2067x7a2667b5(final long j, final int i) {
        Handler handler = this.carPowerHandler;
        if (this.serviceDestroyed || handler == null) {
            return;
        }
        Runnable runnable = new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SetModesService.this.m2062xf3bb593d(j, i);
            }
        };
        if (Looper.myLooper() == handler.getLooper()) {
            runnable.run();
        } else {
            if (handler.post(runnable)) {
                return;
            }
            Log.w(TAG, "CarPower state dropped: worker stopped");
        }
    }

    /* JADX INFO: renamed from: lambda$dispatchPowerStateFromBinder$17$ru-big-town-anative-SetModesService, reason: not valid java name */
    /* synthetic */ void m2062xf3bb593d(final long j, final int i) {
        if (this.serviceDestroyed || !this.carPowerCallbackGate.isCurrent(j)) {
            return;
        }
        this.mainHandler.post(new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda26
            @Override // java.lang.Runnable
            public final void run() {
                SetModesService.this.m2061xe01385bc(j, i);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$dispatchPowerStateFromBinder$16$ru-big-town-anative-SetModesService, reason: not valid java name */
    /* synthetic */ void m2061xe01385bc(long j, int i) {
        if (this.carPowerCallbackGate.isCurrent(j)) {
            handlePowerStateChanged(i);
        }
    }

    private void clearPublishedCarPowerManager(CarPowerManager carPowerManager) {
        if (carPowerManager == null) {
            return;
        }
        if (this.carPowerManager == carPowerManager) {
            this.carPowerManager = null;
        }
        if (GlobalVars.mCarPowerManager == carPowerManager) {
            GlobalVars.mCarPowerManager = null;
        }
    }

    private void releaseCarPowerManagerAsync() {
        this.carPowerCallbackGate.close();
        final Handler handler = this.carPowerHandler;
        final HandlerThread handlerThread = this.carPowerThread;
        this.carPowerHandler = null;
        final CarPowerManager carPowerManager = this.carPowerManager;
        clearPublishedCarPowerManager(carPowerManager);
        if (handler == null || handlerThread == null) {
            this.mCar = null;
        } else {
            if (handler.postAtFrontOfQueue(new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    SetModesService.this.m2068x46d6740b(carPowerManager, handler, handlerThread);
                }
            })) {
                return;
            }
            this.carPowerManager = null;
            this.mCar = null;
            handlerThread.quitSafely();
            Log.w(TAG, "CarPower cleanup dropped: worker already stopped");
        }
    }

    /* JADX INFO: renamed from: lambda$releaseCarPowerManagerAsync$18$ru-big-town-anative-SetModesService, reason: not valid java name */
    /* synthetic */ void m2068x46d6740b(CarPowerManager carPowerManager, Handler handler, HandlerThread handlerThread) {
        try {
            releaseCarPowerManagerOnWorker(carPowerManager);
        } finally {
            handler.removeCallbacksAndMessages(null);
            handlerThread.quitSafely();
        }
    }

    private void releaseCarPowerManagerOnWorker(CarPowerManager carPowerManager) {
        CarPowerManager carPowerManager2 = this.carPowerManager;
        if (carPowerManager2 != null) {
            carPowerManager = carPowerManager2;
        }
        this.carPowerManager = null;
        if (GlobalVars.mCarPowerManager == carPowerManager) {
            GlobalVars.mCarPowerManager = null;
        }
        if (carPowerManager != null) {
            try {
                carPowerManager.clearListener();
                Log.i(TAG, "CarPowerStateListener unregistered");
            } catch (NoSuchMethodError unused) {
                Log.w(TAG, "clearListener() not available on this platform");
            } catch (Throwable th) {
                Log.w(TAG, "clearListener() failed: " + th.getMessage());
            }
        }
        Car car = this.mCar;
        this.mCar = null;
        if (car != null) {
            try {
                car.disconnect();
            } catch (Throwable th2) {
                Log.w(TAG, "Car disconnect failed: " + th2.getMessage());
            }
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        Notification notificationBuild = new NotificationCompat.Builder(this, "screen_monitor_channel").setContentTitle("Screen Monitor").setContentText("Monitoring screen state").setSmallIcon(R.drawable.ic_launcher_foreground).build();
        createNotificationChannel();
        startForeground(1, notificationBuild);
        if (!this.receiverRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.KEYCODE_SWC_USER_DEFINE");
            intentFilter.addAction("com.android.server.jobscheduler.GARAGE_MODE_OFF");
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            ContextCompat.registerReceiver(getApplicationContext(), this.setModesReceiverDynamic, intentFilter, 2);
            this.receiverRegistered = true;
        }
        if (!this.startupInitialized) {
            this.startupInitialized = true;
            requestSavedConfigSync("service start");
            ApplyEngine.activateWake("service start");
            restoreAutoLightState();
            restoreWiperColdState();
            startTripStatsService();
            startBatteryHeatService();
            scheduleAncillaryWakeTasks();
            Log.i(TAG, "onStartCommand(): startup initialized");
        } else {
            Log.i(TAG, "onStartCommand(): already initialized");
        }
        ApplyEngine.scheduleAccApply(this);
        return 1;
    }

    private void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel("screen_monitor_channel", "Screen Monitor", 2);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.serviceMessenger.getBinder();
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        SuspensionWidgetController suspensionWidgetController = this.suspensionWidget;
        if (suspensionWidgetController != null) {
            suspensionWidgetController.close();
        }
        this.voiceCommands.close();
        this.serviceDestroyed = true;
        Iterator<VirtualDisplay> it = this.embeddedDisplays.values().iterator();
        while (it.hasNext()) {
            try {
                it.next().release();
            } catch (Exception unused) {
            }
        }
        this.embeddedDisplays.clear();
        this.embeddedPackages.clear();
        this.embeddedLaunched.clear();
        this.embeddedLaunchAt.clear();
        if (this.powerHoldStatusReceiverRegistered) {
            try {
                unregisterReceiver(this.powerHoldStatusRequestReceiver);
            } catch (IllegalArgumentException unused2) {
            }
            this.powerHoldStatusReceiverRegistered = false;
        }
        PowerHoldStatusTracker powerHoldStatusTracker = this.powerHoldStatusTracker;
        this.powerHoldStatusTracker = null;
        if (powerHoldStatusTracker != null) {
            powerHoldStatusTracker.close();
        }
        this.vehicleStateControllers = null;
        this.powerHoldController = null;
        releaseCarPowerManagerAsync();
        this.pendingPhysicalWake = false;
        endWakeSession();
        cancelAncillaryWakeTasks();
        this.mainHandler.removeCallbacksAndMessages(null);
        ScreenLiftTaskRestorer screenLiftTaskRestorer = this.screenLiftTaskRestorer;
        this.screenLiftTaskRestorer = null;
        if (screenLiftTaskRestorer != null) {
            screenLiftTaskRestorer.close();
        }
        if (this.receiverRegistered) {
            try {
                getApplicationContext().unregisterReceiver(this.setModesReceiverDynamic);
            } catch (IllegalArgumentException unused3) {
                Log.w(TAG, "unregisterReceiver: not registered");
            }
            this.receiverRegistered = false;
        }
        try {
            unregisterReceiver(this.logRequestReceiver);
        } catch (IllegalArgumentException unused4) {
        }
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void notifyApplyDone(Messenger messenger) {
        if (messenger == null) {
            return;
        }
        try {
            messenger.send(Message.obtain((Handler) null, 4));
        } catch (RemoteException e) {
            Log.w(TAG, "notifyApplyDone failed: " + e.getMessage());
        }
    }

    public static void worker(int i, int i2, int i3, final int i4) {
        Log.i(TAG, " Call worker" + String.format(" repeat: %d, pause: %d, mode %d, msg_arg1: %d", Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4)));
        if (GlobalVars.SAVE_CONTEXT == null || i3 != 2) {
            return;
        }
        ApplyEngine.postUserCommand("star button " + i4, new Runnable() { // from class: ru.big.town.anative.SetModesService$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                SetModesService.lambda$worker$19(i4);
            }
        });
    }

    static /* synthetic */ void lambda$worker$19(int i) {
        MainActivity.loadModes(GlobalVars.SAVE_CONTEXT);
        Log.i(TAG, " Run customCommandStarButton");
        if (i == 1) {
            MainActivity.setCanValues(1, MainActivity.getCustomCommandStarButton1(), "star button command 1");
        }
        if (i == 2) {
            MainActivity.setCanValues(1, MainActivity.getCustomCommandStarButton2(), "star button command 2");
        }
    }
}
