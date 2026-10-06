package ru.big.town.anative;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/* JADX INFO: loaded from: classes2.dex */
final class PowerHoldStatusTracker implements AutoCloseable {
    static final long ACTIVATION_TIMEOUT_MS = 10_000L;
    private static final String TAG = "PowerHoldStatus";
    private Runnable activationTimeout;
    private long activeEpoch;
    private final CloseAction closeAction;
    private volatile boolean closed;
    private PowerHoldStatusPolicy.Snapshot lastPublished;
    private final StatusListener listener;
    private long liveRevision;
    private final PowerHoldStatusPolicy.Machine machine = new PowerHoldStatusPolicy.Machine();
    private final Scheduler scheduler;
    private final SeedLoader seedLoader;
    private CanBusEventHub.Subscription subscription;
    private static final OemVehicleStateTransport.StateKey SWITCH_KEY = new OemVehicleStateTransport.StateKey("POWER_HOLD_MODE_SWITCH", 1161);
    private static final OemVehicleStateTransport.StateKey WARNING_KEY = new OemVehicleStateTransport.StateKey("POWER_HOLD_MODE_WARNING", 1163);

    interface ActivationReady {
        void onReady(long j);
    }

    interface CloseAction {
        void close();
    }

    interface Scheduler {
        boolean post(Runnable runnable);

        boolean postDelayed(Runnable runnable, long j);

        void removeCallbacks(Runnable runnable);
    }

    interface SeedCallback {
        void onResult(long j, Integer num, Integer num2);
    }

    interface SeedLoader extends AutoCloseable {
        @Override // java.lang.AutoCloseable
        void close();

        void load(long j, SeedCallback seedCallback);
    }

    interface StatusListener {
        void onStatus(PowerHoldStatusPolicy.Snapshot snapshot, PowerHoldPolicy.Outcome outcome, boolean z);
    }

    static PowerHoldStatusTracker create(Context context, StatusListener statusListener) {
        Context app = context.getApplicationContext();
        final HandlerThread handlerThread = new HandlerThread(TAG);
        final HandlerThread handlerThread2 = new HandlerThread("PowerHoldSeed");
        handlerThread.start();
        handlerThread2.start();
        final Handler handler = new Handler(handlerThread.getLooper());
        final PowerHoldStatusTracker powerHoldStatusTracker = new PowerHoldStatusTracker(new Scheduler() { // from class: ru.big.town.anative.PowerHoldStatusTracker.1
            @Override // ru.big.town.anative.PowerHoldStatusTracker.Scheduler
            public boolean post(Runnable runnable) {
                return handler.post(runnable);
            }

            @Override // ru.big.town.anative.PowerHoldStatusTracker.Scheduler
            public boolean postDelayed(Runnable runnable, long j) {
                return handler.postDelayed(runnable, j);
            }

            @Override // ru.big.town.anative.PowerHoldStatusTracker.Scheduler
            public void removeCallbacks(Runnable runnable) {
                handler.removeCallbacks(runnable);
            }
        }, new AnonymousClass2(new Handler(handlerThread2.getLooper()), app), statusListener, new CloseAction() { // from class: ru.big.town.anative.PowerHoldStatusTracker$$ExternalSyntheticLambda5
            @Override // ru.big.town.anative.PowerHoldStatusTracker.CloseAction
            public final void close() {
                PowerHoldStatusTracker.lambda$create$0(handler, handlerThread, handlerThread2);
            }
        });
        Objects.requireNonNull(powerHoldStatusTracker);
        powerHoldStatusTracker.subscription = CanBusEventHub.get(app).subscribe(CanBusEventRouter.INTEREST_CONNECTION | CanBusEventRouter.INTEREST_VEHICLE_STATE, new int[]{PowerHoldPolicy.POWER_HOLD_MODE_SWITCH_ID, PowerHoldPolicy.POWER_HOLD_MODE_WARNING_ID}, handler, new CanBusEventHub.Listener() { // from class: ru.big.town.anative.PowerHoldStatusTracker$$ExternalSyntheticLambda6
            @Override // ru.big.town.anative.CanBusEventHub.Listener
            public final void onCanBusEvent(CanBusEvent canBusEvent) {
                powerHoldStatusTracker.m2019lambda$acceptEvent$1$rubigtownanativePowerHoldStatusTracker(canBusEvent);
            }
        });
        return powerHoldStatusTracker;
    }

    /* JADX INFO: renamed from: ru.big.town.anative.PowerHoldStatusTracker$2, reason: invalid class name */
    static class AnonymousClass2 implements SeedLoader {
        private volatile boolean stopped;
        final /* synthetic */ Context val$app;
        final /* synthetic */ Handler val$seedHandler;

        AnonymousClass2(Handler handler, Context context) {
            this.val$seedHandler = handler;
            this.val$app = context;
        }

        @Override // ru.big.town.anative.PowerHoldStatusTracker.SeedLoader
        public void load(final long j, final SeedCallback seedCallback) {
            if (this.stopped) {
                return;
            }
            Handler handler = this.val$seedHandler;
            final Context context = this.val$app;
            if (handler.post(new Runnable() { // from class: ru.big.town.anative.PowerHoldStatusTracker$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PowerHoldStatusTracker.AnonymousClass2.lambda$load$0(context, seedCallback, j);
                }
            })) {
                return;
            }
            Log.w(PowerHoldStatusTracker.TAG, "Power Hold seed was not queued");
        }

        static /* synthetic */ void lambda$load$0(Context context, SeedCallback seedCallback, long j) {
            Map<OemVehicleStateTransport.StateKey, Integer> vehicleStates = OemVehicleStateTransport.readVehicleStates(context, Arrays.asList(PowerHoldStatusTracker.SWITCH_KEY, PowerHoldStatusTracker.WARNING_KEY));
            seedCallback.onResult(j, vehicleStates == null ? null : vehicleStates.get(PowerHoldStatusTracker.SWITCH_KEY), vehicleStates != null ? vehicleStates.get(PowerHoldStatusTracker.WARNING_KEY) : null);
        }

        @Override // ru.big.town.anative.PowerHoldStatusTracker.SeedLoader, java.lang.AutoCloseable
        public void close() {
            this.stopped = true;
            this.val$seedHandler.removeCallbacksAndMessages(null);
        }
    }

    static /* synthetic */ void lambda$create$0(Handler handler, HandlerThread handlerThread, HandlerThread handlerThread2) {
        handler.removeCallbacksAndMessages(null);
        handlerThread.quitSafely();
        handlerThread2.quitSafely();
    }

    PowerHoldStatusTracker(Scheduler scheduler, SeedLoader seedLoader, StatusListener statusListener, CloseAction closeAction) {
        if (scheduler == null || seedLoader == null || statusListener == null || closeAction == null) {
            throw new IllegalArgumentException("Power Hold tracker dependency is null");
        }
        this.scheduler = scheduler;
        this.seedLoader = seedLoader;
        this.listener = statusListener;
        this.closeAction = closeAction;
    }

    void acceptEvent(final CanBusEvent canBusEvent) {
        if (canBusEvent == null || this.closed) {
            return;
        }
        this.scheduler.post(new Runnable() { // from class: ru.big.town.anative.PowerHoldStatusTracker$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                PowerHoldStatusTracker.this.m2019lambda$acceptEvent$1$rubigtownanativePowerHoldStatusTracker(canBusEvent);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: acceptEventOnSerial, reason: merged with bridge method [inline-methods] */
    public void m2019lambda$acceptEvent$1$rubigtownanativePowerHoldStatusTracker(CanBusEvent canBusEvent) {
        if (this.closed) {
            return;
        }
        switch (canBusEvent.kind) {
            case CONNECTION:
                onConnection(canBusEvent.connectionEpoch);
                return;
            case CONNECTION_LOST:
                onConnectionLost();
                return;
            case VEHICLE_STATE:
                if (canBusEvent.connectionEpoch == this.activeEpoch) {
                    this.liveRevision++;
                    if (canBusEvent.first == PowerHoldPolicy.POWER_HOLD_MODE_SWITCH_ID) {
                        publishIfChanged(this.machine.onSwitch(this.activeEpoch, canBusEvent.second), null, false);
                        cancelTimeoutUnlessActivating();
                    } else if (canBusEvent.first == PowerHoldPolicy.POWER_HOLD_MODE_WARNING_ID) {
                        publishIfChanged(this.machine.onWarning(this.activeEpoch, canBusEvent.second), null, false);
                    }
                }
                return;
            default:
                return;
        }
    }

    private void onConnection(long epoch) {
        if (epoch <= 0 || epoch == this.activeEpoch) {
            return;
        }
        this.activeEpoch = epoch;
        this.liveRevision = 0L;
        cancelActivationTimeout();
        publishIfChanged(this.machine.onConnection(epoch), null, false);
        final long seedRevision = this.liveRevision;
        this.seedLoader.load(epoch, new SeedCallback() { // from class: ru.big.town.anative.PowerHoldStatusTracker$$ExternalSyntheticLambda3
            @Override // ru.big.town.anative.PowerHoldStatusTracker.SeedCallback
            public final void onResult(long j3, Integer num, Integer num2) {
                PowerHoldStatusTracker.this.m2023lambda$onConnection$3$rubigtownanativePowerHoldStatusTracker(seedRevision, j3, num, num2);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$onConnection$3$ru-big-town-anative-PowerHoldStatusTracker, reason: not valid java name */
    /* synthetic */ void m2023lambda$onConnection$3$rubigtownanativePowerHoldStatusTracker(final long j, final long j2, final Integer num, final Integer num2) {
        this.scheduler.post(new Runnable() { // from class: ru.big.town.anative.PowerHoldStatusTracker$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                PowerHoldStatusTracker.this.m2022lambda$onConnection$2$rubigtownanativePowerHoldStatusTracker(j2, j, num, num2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: finishSeed, reason: merged with bridge method [inline-methods] */
    public void m2022lambda$onConnection$2$rubigtownanativePowerHoldStatusTracker(long epoch, long seedRevision, Integer num, Integer num2) {
        if (this.closed || epoch != this.activeEpoch || seedRevision != liveRevision) {
            return;
        }
        if (num2 != null) {
            this.machine.onWarning(epoch, num2.intValue());
        }
        if (num != null) {
            publishIfChanged(this.machine.onSwitch(epoch, num.intValue()), null, false);
        }
    }

    private void onConnectionLost() {
        long j = this.activeEpoch;
        this.activeEpoch = 0L;
        this.liveRevision++;
        cancelActivationTimeout();
        publishIfChanged(this.machine.onConnectionLost(j), null, false);
    }

    void beginActivation(final ActivationReady activationReady) {
        if (activationReady == null || this.closed) {
            return;
        }
        this.scheduler.post(new Runnable() { // from class: ru.big.town.anative.PowerHoldStatusTracker$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                PowerHoldStatusTracker.this.m2020x83285a8d(activationReady);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$beginActivation$4$ru-big-town-anative-PowerHoldStatusTracker, reason: not valid java name */
    /* synthetic */ void m2020x83285a8d(ActivationReady activationReady) {
        if (this.closed) {
            return;
        }
        cancelActivationTimeout();
        long jBeginActivation = this.machine.beginActivation();
        publishIfChanged(this.machine.snapshot(), null, false);
        activationReady.onReady(jBeginActivation);
    }

    void finishActivation(final long j, PowerHoldPolicy.Outcome outcome) {
        if (outcome == null) {
            outcome = PowerHoldPolicy.Outcome.TRANSPORT_FAILURE;
        }
        final PowerHoldPolicy.Outcome outcomeTerminal = outcome;
        this.scheduler.post(new Runnable() { // from class: ru.big.town.anative.PowerHoldStatusTracker$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                PowerHoldStatusTracker.this.m2021xa28cc0e8(j, outcomeTerminal);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$finishActivation$5$ru-big-town-anative-PowerHoldStatusTracker, reason: not valid java name */
    /* synthetic */ void m2021xa28cc0e8(long j, PowerHoldPolicy.Outcome outcome) {
        if (this.closed) {
            return;
        }
        PowerHoldStatusPolicy.Snapshot snapshotFinishActivation = this.machine.finishActivation(j, outcome);
        publishIfChanged(snapshotFinishActivation, outcome, true);
        if (outcome == PowerHoldPolicy.Outcome.ACCEPTED && snapshotFinishActivation.status == PowerHoldStatusPolicy.Status.ACTIVATING && snapshotFinishActivation.requestGeneration == j) {
            scheduleActivationTimeout(j);
        } else {
            cancelActivationTimeout();
        }
    }

    void requestCurrentStatus() {
        this.scheduler.post(new Runnable() { // from class: ru.big.town.anative.PowerHoldStatusTracker$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                PowerHoldStatusTracker.this.m2024x9f91534();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$requestCurrentStatus$6$ru-big-town-anative-PowerHoldStatusTracker, reason: not valid java name */
    /* synthetic */ void m2024x9f91534() {
        if (this.closed) {
            return;
        }
        publishIfChanged(this.machine.snapshot(), null, true);
    }

    private void scheduleActivationTimeout(final long j) {
        cancelActivationTimeout();
        Runnable timeout = new Runnable() { // from class: ru.big.town.anative.PowerHoldStatusTracker$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                PowerHoldStatusTracker.this.m2025x7d4c8195(j);
            }
        };
        this.activationTimeout = timeout;
        if (this.scheduler.postDelayed(timeout, ACTIVATION_TIMEOUT_MS)) {
            return;
        }
        this.activationTimeout = null;
        publishIfChanged(this.machine.onActivationTimeout(j), null, false);
    }

    /* JADX INFO: renamed from: lambda$scheduleActivationTimeout$7$ru-big-town-anative-PowerHoldStatusTracker, reason: not valid java name */
    /* synthetic */ void m2025x7d4c8195(long j) {
        if (this.closed || this.activationTimeout == null) {
            return;
        }
        this.activationTimeout = null;
        publishIfChanged(this.machine.onActivationTimeout(j), null, false);
    }

    private void cancelTimeoutUnlessActivating() {
        if (this.machine.snapshot().status != PowerHoldStatusPolicy.Status.ACTIVATING) {
            cancelActivationTimeout();
        }
    }

    private void cancelActivationTimeout() {
        Runnable runnable = this.activationTimeout;
        this.activationTimeout = null;
        if (runnable != null) {
            this.scheduler.removeCallbacks(runnable);
        }
    }

    private void publishIfChanged(PowerHoldStatusPolicy.Snapshot snapshot, PowerHoldPolicy.Outcome outcome, boolean z) {
        if (this.closed) {
            return;
        }
        if (!z && outcome == null && snapshot.equals(this.lastPublished)) {
            return;
        }
        this.lastPublished = snapshot;
        this.listener.onStatus(snapshot, outcome, z);
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        CanBusEventHub.Subscription subscription = this.subscription;
        this.subscription = null;
        if (subscription != null) {
            subscription.close();
        }
        cancelActivationTimeout();
        this.seedLoader.close();
        this.closeAction.close();
    }
}
