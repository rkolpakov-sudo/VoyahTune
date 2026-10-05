package ru.big.town.anative;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

/**
 * IMP-02 (SPEC L44): Android glue for the AVAS state machine.
 *
 * Owns a single HandlerThread ("Avas") that serializes vehicle events (door /
 * gear / sleep / wake / TX57 echoes); user toggles run synchronously on the
 * caller thread so the existing steer/voice/menu paths keep their boolean
 * result. Every write goes through the IMP-01 read-back dispatcher
 * (feature "avas"); the CB36 echo for TX57 id 665 confirms or fails it.
 */
final class AvasController {
    private static final String TAG = "$$$ AVAS $$$";
    private static volatile AvasController instance;

    private final AvasStateMachine machine = new AvasStateMachine();
    private HandlerThread thread;
    private Handler handler;
    private Context appContext;
    private Integer pendingWriteValue;
    private int lastDoor = -1;
    private int lastGear = -1;
    private boolean initialized;

    static AvasController get() {
        if (instance == null) {
            synchronized (AvasController.class) {
                if (instance == null) {
                    instance = new AvasController();
                }
            }
        }
        return instance;
    }

    private AvasController() {
    }

    AvasStateMachine machineForTest() {
        return this.machine;
    }

    synchronized void init(Context context) {
        if (this.initialized) {
            return;
        }
        this.initialized = true;
        this.appContext = context.getApplicationContext();
        this.thread = new HandlerThread("Avas");
        this.thread.start();
        this.handler = new Handler(this.thread.getLooper());
        seedIfUnknown();
        VehicleStateControllers vsc = VehicleStateControllers.get(this.appContext);
        this.lastDoor = vsc.driverDoor().currentFrontLeft();
        this.lastGear = vsc.gear().currentGear();
        vsc.driverDoor().subscribe(this.handler, state -> {
            if (state.isLive()) {
                handleDoorChanged(state.frontLeft);
            }
        });
        vsc.gear().subscribe(this.handler, this::handleGearChanged);
        Log.i(TAG, "init: snapshot=" + this.machine.snapshot() + " door=" + this.lastDoor + " gear=" + this.lastGear);
    }

    /** Sleep event (SetModesService screen/power fallbacks). */
    void onSleep(String source) {
        post(() -> handleSleep(source));
    }

    /** Wake event (ignition / screen on): WAKE_APPLY, once per event. */
    void onWakeEvent(String source) {
        post(() -> handleWake(source));
    }

    /** CB36 echo of TX57 id 665 (ModeFeedbackController). */
    void onVehicleStateEcho(int value) {
        synchronized (this) {
            handleEcho(value);
        }
    }

    /**
     * User toggle (card/menu MSG 21, steering wheel, voice): commit the
     * snapshot from the user flag, then write through the dispatcher.
     * Returns the first-send outcome; rolls the snapshot back when the send
     * could not be performed at all.
     */
    synchronized boolean requestUserToggle(boolean disable) {
        Integer prev = this.machine.snapshot();
        this.machine.onUserToggle(disable);
        boolean ok = requestApply(new AvasStateMachine.ApplyRequest(
                VehicleRestorePolicy.pedestrianSoundState(disable), "user toggle"));
        if (!ok) {
            this.machine.restoreSnapshot(prev);
        }
        return ok;
    }

    /** Restore-plan entry (manual apply): apply the snapshot once. */
    synchronized boolean applySnapshot(String reason) {
        seedIfUnknown();
        Integer snap = this.machine.snapshot();
        if (snap == null) {
            Log.w(TAG, reason + ": no snapshot, skip");
            return false;
        }
        return requestApply(new AvasStateMachine.ApplyRequest(snap.intValue(), reason));
    }

    synchronized AvasStateMachine.State stateForTest() {
        return this.machine.state();
    }

    synchronized Integer pendingWriteForTest() {
        return this.pendingWriteValue;
    }

    synchronized void handleDoorChanged(int frontLeft) {
        if (frontLeft < 0) {
            return;
        }
        if (frontLeft == 1 && this.lastDoor != 1) {
            this.lastDoor = 1;
            requestApply(this.machine.onDoorOpen());
            return;
        }
        if (frontLeft == 0) {
            this.lastDoor = 0;
        }
    }

    synchronized void handleGearChanged(int gear) {
        if (gear == this.lastGear) {
            return;
        }
        boolean firstDrive = gear == 3 && this.lastGear != 3;
        this.lastGear = gear;
        if (firstDrive) {
            requestApply(this.machine.onFirstDrive());
        }
    }

    private synchronized void handleWake(String source) {
        seedIfUnknown();
        requestApply(this.machine.onWakeEvent(source));
    }

    private synchronized void handleSleep(String source) {
        Log.i(TAG, "sleep: " + source);
        this.machine.onSleep();
    }

    private synchronized void handleEcho(int value) {
        Integer expected = this.pendingWriteValue;
        boolean active = CommandStatusHub.get().hasActive(ReadBackTable.FEATURE_AVAS);
        if (expected != null && active) {
            if (value == expected.intValue()) {
                this.pendingWriteValue = null;
                CommandStatusHub.get().ack(ReadBackTable.FEATURE_AVAS, ReadBackTable.SOURCE_AVAS_TX57);
                this.machine.onWriteSettled(true);
                Log.i(TAG, "confirmed value=" + value);
                return;
            }
            this.pendingWriteValue = null;
            CommandStatusHub.get().mismatch(ReadBackTable.FEATURE_AVAS, "expected=" + expected + " actual=" + value);
            this.machine.onWriteSettled(false);
            Log.w(TAG, "mismatch expected=" + expected + " actual=" + value);
            return;
        }
        this.pendingWriteValue = null;
        AvasStateMachine.ApplyRequest request = this.machine.onObservedValue(value);
        if (request != null) {
            Log.i(TAG, "oem reset detected: " + request.reason);
            requestApply(request);
        }
    }

    private boolean requestApply(AvasStateMachine.ApplyRequest request) {
        if (request == null) {
            return false;
        }
        final int value = request.tx57Value;
        this.pendingWriteValue = Integer.valueOf(value);
        Log.i(TAG, "apply: " + request.reason + " value=" + value);
        boolean ok = CommandStatusHub.get().submit(ReadBackTable.FEATURE_AVAS,
                () -> MainActivity.sendPedestrianSoundCommand(value == AvasStateMachine.TX57_DISABLED));
        if (!ok) {
            this.pendingWriteValue = null;
            Log.w(TAG, "apply send rejected: " + request.reason);
        }
        return ok;
    }

    private void seedIfUnknown() {
        if (this.machine.snapshot() != null || this.appContext == null) {
            return;
        }
        try {
            this.machine.seedSnapshotFromUserFlag(
                    MainActivity.currentSavedToggle(this.appContext, "disablePedestrianSound"));
            Log.i(TAG, "seeded snapshot from user flag: " + this.machine.snapshot());
        } catch (RuntimeException e) {
            Log.w(TAG, "seed failed: " + e.getMessage());
        }
    }

    private void post(Runnable action) {
        Handler h;
        synchronized (this) {
            h = this.handler;
        }
        if (h == null) {
            action.run();
            return;
        }
        h.post(action);
    }
}
