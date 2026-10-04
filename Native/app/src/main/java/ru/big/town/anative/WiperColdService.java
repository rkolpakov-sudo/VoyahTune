package ru.big.town.anative;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.car.Car;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import androidx.core.app.NotificationCompat;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes2.dex */
public class WiperColdService extends Service {
    public static final String ACTION_POWER_ON_RESET = "ru.big.town.anative.WIPER_COLD_POWER_ON_RESET";
    private static final int CAN_CMD_NUM = 1;
    private static final String CHANNEL_ID = "wiper_cold_channel";
    private static final int DOOR_OPEN = 1;
    private static final long FADE_BACKPRESSURE_MS = 42;
    private static final int FADE_STEPS = 12;
    private static final long FADE_TOTAL_MS = 500;
    private static final int GEAR_MIN_MOVING = 1;
    private static final String KEYMANAGER_PACKAGE = "com.qinggan.keymanager.service";
    private static final int MEDIA_PROXY_ACK = -1;
    private static final String MEDIA_PROXY_ACTION = "ru.big.town.anative.MEDIA_KEY_PROXY";
    private static final int MEDIA_PROXY_UNHANDLED = 0;
    private static final long POWER_ON_RESET_SUPPRESS_MS = 10000;
    private static final String PREFS_NAME = "NativePrefs";
    private static final String PREF_MEDIA_PAUSE = "pauseMediaOnDoor";
    private static final String PREF_SERVICE_ACTIVE = "wiperServiceActive";
    private static final String PREF_WIPER_ENABLED = "wiperCold";
    private static final long REMOTE_AUDIO_DRAIN_MS = 2200;
    private static final String TAG = "$$$ WiperColdService $$$";
    private static final String WIPER_TOGGLE_FRAME = "65 08 00 00 c1 c0 00 00 40 00";
    private DriverDoorStateController.Subscription driverDoorSubscription;
    private GearStateController.Subscription gearStateSubscription;
    private volatile Handler mediaHandler;
    private HandlerThread mediaThread;
    private volatile Handler timerHandler;
    private final DoorPauseRunState mediaPauseState = new DoorPauseRunState();
    private final DoorPauseWorkGate mediaPauseWorkGate = new DoorPauseWorkGate();
    private AudioManager mediaFadeAudioManager = null;
    private volatile boolean destroyed = false;
    private boolean wiperTogglePending = false;
    private Boolean queuedWiperTarget = null;
    private int lastFLDoor = -1;
    private long lastPowerOnResetElapsed = 0;

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDriverDoorState(DriverDoorStateController.State state) {
        if (this.destroyed) {
            return;
        }
        if (state.isLive()) {
            onDoorState(state.frontLeft);
        } else {
            applyDoorSeed(state.frontLeft);
        }
    }

    private void applyDoorSeed(int i) {
        if (i < 0) {
            return;
        }
        this.lastFLDoor = i;
        Log.i(TAG, "seed: fLDoor=" + this.lastFLDoor + " active=" + isServiceActive());
        if (isWiperEnabled()) {
            evaluate("seed");
        }
    }

    private void onDoorState(int i) {
        if (i < 0 || i == this.lastFLDoor) {
            return;
        }
        boolean z = i == 1;
        this.lastFLDoor = i;
        boolean zIsMediaPauseEnabled = isMediaPauseEnabled();
        Log.i(TAG, "door: fLDoor=" + i + " openedNow=" + z + " mediaPause=" + zIsMediaPauseEnabled + " wiper=" + isWiperEnabled() + " active=" + isServiceActive());
        if (z && zIsMediaPauseEnabled) {
            requestDoorMediaPause();
        }
        if (isWiperEnabled()) {
            evaluate("door");
        }
    }

    private void evaluate(String str) {
        boolean zIsServiceActive = isServiceActive();
        Log.i(TAG, "evaluate(" + str + "): fLDoor=" + this.lastFLDoor + " active=" + zIsServiceActive);
        if (zIsServiceActive) {
            Log.i(TAG, "evaluate: пропуск — режим уже активен (wiperServiceActive=true)");
            return;
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime() - this.lastPowerOnResetElapsed;
        if (jElapsedRealtime < POWER_ON_RESET_SUPPRESS_MS) {
            Log.i(TAG, "evaluate: пропуск — окно после power-on reset (" + jElapsedRealtime + "ms)");
        } else if (this.lastFLDoor != 1) {
            Log.i(TAG, "evaluate: пропуск — водительская дверь не открыта (fLDoor=" + this.lastFLDoor + ")");
        } else {
            Log.i(TAG, "★ условие выполнено (" + str + "): дверь водителя открыта → включаем сервисный режим дворников");
            requestServiceActive(true, "wiper service ON (driver door open)");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onGearState(int i) {
        Log.i(TAG, "gear=" + i + " active=" + isServiceActive());
        if (i >= 1) {
            returnWipersIfActive("gear→" + i + " (готов ехать)");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPowerOnReset() {
        returnWipersIfActive("power on");
    }

    private void returnWipersIfActive(String str) {
        if (isServiceActive()) {
            this.lastPowerOnResetElapsed = SystemClock.elapsedRealtime();
            Log.i(TAG, str + " → возвращаем дворники в обычный режим (toggle)");
            requestServiceActive(false, "wiper service OFF (" + str + ")");
            return;
        }
        Log.i(TAG, str + " → сервисный режим не активен, команду не шлём");
    }

    private void requestServiceActive(final boolean z, final String str) {
        if (this.wiperTogglePending) {
            this.queuedWiperTarget = Boolean.valueOf(z);
            Log.i(TAG, "wiper toggle coalesced, target=" + z);
        } else {
            if (isServiceActive() == z) {
                return;
            }
            this.wiperTogglePending = true;
            ApplyEngine.postWakeAction(str, new BooleanSupplier() { // from class: ru.big.town.anative.WiperColdService$$ExternalSyntheticLambda2
                @Override // java.util.function.BooleanSupplier
                public final boolean getAsBoolean() {
                    return WiperColdService.lambda$requestServiceActive$0(str);
                }
            }, (Consumer<ApplyEngine.WakeActionResult>) new Consumer() { // from class: ru.big.town.anative.WiperColdService$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    WiperColdService.this.m2173x1ef12e2c(z, str, (ApplyEngine.WakeActionResult) obj);
                }
            });
        }
    }

    static /* synthetic */ boolean lambda$requestServiceActive$0(String str) {
        byte[] hexBinary = MainActivity.parseHexBinary(WIPER_TOGGLE_FRAME);
        Log.i(TAG, "sendToggle: [" + str + "] frame=65 08 00 00 c1 c0 00 00 40 00");
        return CanSender.send(1, hexBinary, str);
    }

    /* JADX INFO: renamed from: lambda$requestServiceActive$2$ru-big-town-anative-WiperColdService, reason: not valid java name */
    /* synthetic */ void m2173x1ef12e2c(final boolean z, final String str, final ApplyEngine.WakeActionResult wakeActionResult) {
        if (this.destroyed) {
            return;
        }
        this.timerHandler.post(new Runnable() { // from class: ru.big.town.anative.WiperColdService$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                WiperColdService.this.m2172xbd9e918d(z, str, wakeActionResult);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$requestServiceActive$1$ru-big-town-anative-WiperColdService, reason: not valid java name */
    /* synthetic */ void m2172xbd9e918d(boolean z, String str, ApplyEngine.WakeActionResult wakeActionResult) {
        finishWiperToggle(z, str, wakeActionResult == ApplyEngine.WakeActionResult.SUCCESS);
    }

    private void finishWiperToggle(boolean z, String str, boolean z2) {
        if (this.destroyed) {
            return;
        }
        this.wiperTogglePending = false;
        if (z2) {
            setServiceActive(z);
        } else {
            Log.w(TAG, "wiper toggle failed/cancelled: " + str);
        }
        Boolean bool = this.queuedWiperTarget;
        this.queuedWiperTarget = null;
        if (bool == null || bool.booleanValue() == isServiceActive()) {
            return;
        }
        requestServiceActive(bool.booleanValue(), "wiper coalesced target " + bool);
    }

    private boolean isServiceActive() {
        return getSharedPreferences(PREFS_NAME, 0).getBoolean(PREF_SERVICE_ACTIVE, false);
    }

    private boolean isWiperEnabled() {
        return getSharedPreferences(PREFS_NAME, 0).getBoolean(PREF_WIPER_ENABLED, false);
    }

    private boolean isMediaPauseEnabled() {
        return getSharedPreferences(PREFS_NAME, 0).getBoolean(PREF_MEDIA_PAUSE, false);
    }

    private void requestDoorMediaPause() {
        final int iTryAcquire = this.mediaPauseWorkGate.tryAcquire();
        if (iTryAcquire == -1) {
            Log.i(TAG, "pauseActiveMediaWithFade: duplicate suppressed");
            return;
        }
        Handler handler = this.mediaHandler;
        if (this.destroyed || handler == null || !handler.post(new Runnable() { // from class: ru.big.town.anative.WiperColdService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                WiperColdService.this.m2171x4e830d22(iTryAcquire);
            }
        })) {
            this.mediaPauseWorkGate.release(iTryAcquire);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: pauseActiveMediaWithFadeOnWorker, reason: merged with bridge method [inline-methods] */
    public void m2171x4e830d22(int i) {
        try {
            runMediaPauseAndFade(i);
        } catch (Throwable th) {
            Log.w(TAG, "pauseActiveMediaWithFade: " + th.getMessage());
            cancelMediaFadeAndRestoreVolume();
            this.mediaPauseWorkGate.release(i);
        }
    }

    private void runMediaPauseAndFade(final int i) {
        int i2;
        int streamVolume;
        if (this.destroyed || !this.mediaPauseWorkGate.isLatest(i)) {
            this.mediaPauseWorkGate.release(i);
            return;
        }
        AudioManager audioManager = (AudioManager) getSystemService(Car.AUDIO_SERVICE);
        if (audioManager == null) {
            streamVolume = -1;
        } else {
            try {
                streamVolume = audioManager.getStreamVolume(3);
            } catch (Exception e) {
                Log.w(TAG, "pauseActiveMedia: getStreamVolume: " + e.getMessage());
                streamVolume = -1;
            }
        }
        i2 = streamVolume;
        if (!this.destroyed && this.mediaPauseWorkGate.isLatest(i)) {
            Log.i(TAG, "pauseActiveMediaWithFade: startVol=" + i2 + " (дверь водителя открыта)");
            final int iBegin = this.mediaPauseState.begin(i2);
            if (iBegin == -1) {
                this.mediaPauseWorkGate.release(i);
                return;
            }
            this.mediaFadeAudioManager = audioManager;
            if (this.destroyed || !this.mediaPauseWorkGate.isLatest(i)) {
                finishMediaFade(iBegin, i, true);
                return;
            }
            dispatchDoorPause(audioManager, i);
            if (this.destroyed || !this.mediaPauseWorkGate.isLatest(i)) {
                finishMediaFade(iBegin, i, true);
                return;
            }
            if (audioManager == null || i2 <= 0) {
                Handler handler = this.mediaHandler;
                if (handler == null || !handler.postDelayed(new Runnable() { // from class: ru.big.town.anative.WiperColdService$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        WiperColdService.this.m2174x1abcd720(iBegin, i);
                    }
                }, REMOTE_AUDIO_DRAIN_MS)) {
                    finishMediaFade(iBegin, i, false);
                    return;
                }
                return;
            }
            m2175xf892c8c9(iBegin, i, audioManager, new DoorPauseFadeCursor(i2, 12, FADE_TOTAL_MS, REMOTE_AUDIO_DRAIN_MS), SystemClock.uptimeMillis());
            return;
        }
        this.mediaPauseWorkGate.release(i);
    }

    /* JADX INFO: renamed from: lambda$runMediaPauseAndFade$4$ru-big-town-anative-WiperColdService, reason: not valid java name */
    /* synthetic */ void m2174x1abcd720(int i, int i2) {
        finishMediaFade(i, i2, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: scheduleMediaFadeTick, reason: merged with bridge method [inline-methods] */
    public void m2175xf892c8c9(final int i, final int i2, final AudioManager audioManager, final DoorPauseFadeCursor doorPauseFadeCursor, final long j) {
        long j2;
        if (this.mediaPauseState.isCurrent(i)) {
            if (this.destroyed || !this.mediaPauseWorkGate.isLatest(i2)) {
                finishMediaFade(i, i2, true);
                return;
            }
            long jUptimeMillis = SystemClock.uptimeMillis() - j;
            DoorPauseFadeCursor.Action actionActionAt = doorPauseFadeCursor.actionAt(jUptimeMillis);
            if (actionActionAt.kind == DoorPauseFadeCursor.Kind.RESTORE) {
                finishMediaFade(i, i2, true);
                return;
            }
            boolean z = false;
            try {
                if (actionActionAt.kind == DoorPauseFadeCursor.Kind.WRITE) {
                    try {
                        audioManager.setStreamVolume(3, actionActionAt.volume, 0);
                    } catch (Exception e) {
                        Log.w(TAG, "media fade volume=" + actionActionAt.volume + ": " + e.getMessage());
                    }
                    doorPauseFadeCursor.markAttempted(actionActionAt.volume);
                    jUptimeMillis = SystemClock.uptimeMillis() - j;
                    actionActionAt = doorPauseFadeCursor.actionAt(jUptimeMillis);
                    if (actionActionAt.kind == DoorPauseFadeCursor.Kind.RESTORE) {
                        finishMediaFade(i, i2, true);
                        return;
                    }
                    z = true;
                }
                if (actionActionAt.kind == DoorPauseFadeCursor.Kind.WRITE) {
                    j2 = z ? FADE_BACKPRESSURE_MS : 0L;
                } else {
                    j2 = actionActionAt.delayMs;
                }
                long jCapDelayToRestore = doorPauseFadeCursor.capDelayToRestore(jUptimeMillis, j2);
                Handler handler = this.mediaHandler;
                long j3 = j + jUptimeMillis + jCapDelayToRestore;
                if (handler == null || !handler.postAtTime(new Runnable() { // from class: ru.big.town.anative.WiperColdService$$ExternalSyntheticLambda7
                    @Override // java.lang.Runnable
                    public final void run() {
                        WiperColdService.this.m2175xf892c8c9(i, i2, audioManager, doorPauseFadeCursor, j);
                    }
                }, j3)) {
                    finishMediaFade(i, i2, true);
                }
            } catch (Throwable th) {
                doorPauseFadeCursor.markAttempted(actionActionAt.volume);
                throw th;
            }
        }
    }

    private void finishMediaFade(int i, int i2, boolean z) {
        AudioManager audioManager;
        if (this.mediaPauseState.isCurrent(i)) {
            int iFinishAndTakeRestoreVolume = this.mediaPauseState.finishAndTakeRestoreVolume(i);
            if (z && (audioManager = this.mediaFadeAudioManager) != null && iFinishAndTakeRestoreVolume >= 0) {
                try {
                    audioManager.setStreamVolume(3, iFinishAndTakeRestoreVolume, 0);
                } catch (Exception e) {
                    Log.w(TAG, "finishMediaFade: restore volume: " + e.getMessage());
                }
                Log.i(TAG, "pauseActiveMediaWithFade: volume restored=" + iFinishAndTakeRestoreVolume + " after 2200ms drain window");
            }
            this.mediaFadeAudioManager = null;
            this.mediaPauseWorkGate.release(i2);
        }
    }

    /* JADX WARN: Code duplicated, block: B:14:0x0078  */
    private void dispatchDoorPause(AudioManager audioManager, int i) {
        boolean z = false;
        MediaControlRouter.Result resultDispatch = MediaControlRouter.dispatch(this, MediaControlPolicy.Command.PAUSE_ONLY);
        Log.i(TAG, "dispatchDoorPause: route=" + resultDispatch.route + " key=" + resultDispatch.keyCode + " pkg=" + resultDispatch.packageName + " stateClass=" + resultDispatch.playbackClass);
        if ("direct".equals(resultDispatch.route) || "noop".equals(resultDispatch.route)) {
            return;
        }
        if (audioManager != null) {
            try {
                if (audioManager.isMusicActive()) {
                    z = true;
                } else {
                    z = false;
                }
            } catch (Exception e) {
                Log.w(TAG, "dispatchDoorPause: isMusicActive: " + e.getMessage());
            }
        } else {
            z = false;
        }
        if ("keymanager".equals(resultDispatch.route)) {
            int iPauseKeyWithAudioEvidence = MediaControlPolicy.pauseKeyWithAudioEvidence(resultDispatch.keyCode, z);
            if (iPauseKeyWithAudioEvidence != resultDispatch.keyCode) {
                Log.i(TAG, "dispatchDoorPause: active music stream confirms safe PLAY_PAUSE fallback");
            }
            sendMediaProxy(iPauseKeyWithAudioEvidence, false, audioManager, i);
            return;
        }
        if ("native".equals(resultDispatch.route)) {
            sendMediaProxy(85, true, audioManager, i);
        }
    }

    private void sendMediaProxy(final int i, boolean z, final AudioManager audioManager, final int i2) {
        WiperColdService wiperColdService;
        if (i != 85 && i != 87 && i != 88 && i != 127) {
            Log.w(TAG, "sendMediaProxy: rejected key=" + i);
            return;
        }
        Intent intent = new Intent(MEDIA_PROXY_ACTION);
        intent.setPackage(KEYMANAGER_PACKAGE);
        intent.addFlags(32);
        intent.putExtra("keyCode", i);
        intent.putExtra("nativeQG", z);
        Handler handler = this.mediaHandler;
        if (this.destroyed || handler == null) {
            return;
        }
        try {
            wiperColdService = this;
            try {
                wiperColdService.sendOrderedBroadcast(intent, null, new BroadcastReceiver() { // from class: ru.big.town.anative.WiperColdService.1
                    @Override // android.content.BroadcastReceiver
                    public void onReceive(Context context, Intent intent2) {
                        if (WiperColdService.this.destroyed || !WiperColdService.this.mediaPauseWorkGate.isLatest(i2)) {
                            return;
                        }
                        if (getResultCode() == -1) {
                            WiperColdService.this.mediaPauseWorkGate.acknowledgeProxy(i2);
                        } else if (WiperColdService.this.mediaPauseWorkGate.tryClaimFallback(i2)) {
                            Log.w(WiperColdService.TAG, "sendMediaProxy: hook unavailable, standard fallback key=" + i);
                            try {
                                WiperColdService.this.dispatchGlobalMediaKey(audioManager, i);
                            } finally {
                                WiperColdService.this.mediaPauseWorkGate.finishFallback(i2);
                            }
                        }
                    }
                }, handler, 0, null, null);
            } catch (Exception e) {
                e = e;
                Log.w(TAG, "sendMediaProxy: " + e.getMessage());
                if (wiperColdService.destroyed || !wiperColdService.mediaPauseWorkGate.tryClaimFallback(i2)) {
                    return;
                }
                try {
                    wiperColdService.dispatchGlobalMediaKey(audioManager, i);
                } finally {
                    wiperColdService.mediaPauseWorkGate.finishFallback(i2);
                }
            }
        } catch (Exception e2) {
            Exception e = e2;
            wiperColdService = this;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchGlobalMediaKey(AudioManager audioManager, int i) {
        if (audioManager == null) {
            try {
                audioManager = (AudioManager) getSystemService(Car.AUDIO_SERVICE);
            } catch (Exception e) {
                Log.w(TAG, "dispatchGlobalMediaKey: " + e.getMessage());
                return;
            }
        }
        if (audioManager == null) {
            return;
        }
        long jUptimeMillis = SystemClock.uptimeMillis();
        audioManager.dispatchMediaKeyEvent(new KeyEvent(jUptimeMillis, jUptimeMillis, 0, i, 0));
        audioManager.dispatchMediaKeyEvent(new KeyEvent(jUptimeMillis, jUptimeMillis, 1, i, 0));
        Log.i(TAG, "dispatchGlobalMediaKey: key=" + i);
    }

    private void setServiceActive(boolean z) {
        getSharedPreferences(PREFS_NAME, 0).edit().putBoolean(PREF_SERVICE_ACTIVE, z).apply();
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate() — WiperColdService, wiperServiceActive=" + isServiceActive());
        this.timerHandler = new Handler(Looper.getMainLooper());
        createNotificationChannel();
        startForeground(3, new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("Контроль двери водителя").setContentText("Сервисный режим дворников / пауза музыки").setSmallIcon(R.drawable.ic_launcher_foreground).build());
        HandlerThread handlerThread = new HandlerThread("WiperDoorMedia");
        this.mediaThread = handlerThread;
        handlerThread.start();
        this.mediaHandler = new Handler(this.mediaThread.getLooper());
        VehicleStateControllers vehicleStateControllers = VehicleStateControllers.get(this);
        this.gearStateSubscription = vehicleStateControllers.gear().subscribe(this.timerHandler, new GearStateController.Listener() { // from class: ru.big.town.anative.WiperColdService$$ExternalSyntheticLambda4
            @Override // ru.big.town.anative.GearStateController.Listener
            public final void onGearChanged(int i) {
                WiperColdService.this.onGearState(i);
            }
        });
        this.driverDoorSubscription = vehicleStateControllers.driverDoor().subscribe(this.timerHandler, new DriverDoorStateController.Listener() { // from class: ru.big.town.anative.WiperColdService$$ExternalSyntheticLambda5
            @Override // ru.big.town.anative.DriverDoorStateController.Listener
            public final void onDriverDoorChanged(DriverDoorStateController.State state) {
                WiperColdService.this.onDriverDoorState(state);
            }
        });
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        String action = intent != null ? intent.getAction() : null;
        Log.i(TAG, "onStartCommand() action=" + action);
        if (!ACTION_POWER_ON_RESET.equals(action)) {
            return 1;
        }
        this.timerHandler.post(new Runnable() { // from class: ru.big.town.anative.WiperColdService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                WiperColdService.this.onPowerOnReset();
            }
        });
        return 1;
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        this.destroyed = true;
        this.mediaPauseWorkGate.close();
        GearStateController.Subscription subscription = this.gearStateSubscription;
        DriverDoorStateController.Subscription subscription2 = this.driverDoorSubscription;
        this.gearStateSubscription = null;
        this.driverDoorSubscription = null;
        if (subscription != null) {
            subscription.close();
        }
        if (subscription2 != null) {
            subscription2.close();
        }
        if (this.timerHandler != null) {
            this.timerHandler.removeCallbacksAndMessages(null);
        }
        final Handler handler = this.mediaHandler;
        final HandlerThread handlerThread = this.mediaThread;
        if (handler != null && handlerThread != null) {
            if (!handler.postAtFrontOfQueue(new Runnable() { // from class: ru.big.town.anative.WiperColdService$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    WiperColdService.this.m2170lambda$onDestroy$6$rubigtownanativeWiperColdService(handler, handlerThread);
                }
            })) {
                this.mediaPauseState.cancelAndTakeRestoreVolume();
                this.mediaHandler = null;
                handlerThread.quitSafely();
            }
        } else {
            this.mediaPauseState.cancelAndTakeRestoreVolume();
        }
        super.onDestroy();
    }

    /* JADX INFO: renamed from: lambda$onDestroy$6$ru-big-town-anative-WiperColdService, reason: not valid java name */
    /* synthetic */ void m2170lambda$onDestroy$6$rubigtownanativeWiperColdService(Handler handler, HandlerThread handlerThread) {
        try {
            cancelMediaFadeAndRestoreVolume();
        } finally {
            handler.removeCallbacksAndMessages(null);
            this.mediaHandler = null;
            handlerThread.quitSafely();
        }
    }

    private void cancelMediaFadeAndRestoreVolume() {
        int iCancelAndTakeRestoreVolume = this.mediaPauseState.cancelAndTakeRestoreVolume();
        AudioManager audioManager = this.mediaFadeAudioManager;
        if (audioManager != null && iCancelAndTakeRestoreVolume >= 0) {
            try {
                audioManager.setStreamVolume(3, iCancelAndTakeRestoreVolume, 0);
            } catch (Exception e) {
                Log.w(TAG, "onDestroy: restore media volume: " + e.getMessage());
            }
        }
        this.mediaFadeAudioManager = null;
    }

    private void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Сервисный режим дворников", 2);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
