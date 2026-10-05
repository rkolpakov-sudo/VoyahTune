package ru.big.town.anative;

/**
 * IMP-02 (SPEC L44): AVAS state machine core.
 *
 * Pure JVM (no Android imports): IDLE -> WAKE_APPLY (apply the snapshot once
 * per wake event: ignition / door / first Drive) -> CAPTURE_WINDOW (opened
 * ONLY by the first Drive after a door open; closed by door/sleep; inside the
 * window observed changes update the snapshot, outside they are OEM resets)
 * -> CORRECTIVE_REAPPLY (one dispatcher submit = at most 3 sends per cycle).
 *
 * The machine never performs I/O: event methods return an ApplyRequest the
 * glue (AvasController) must send through the IMP-01 read-back dispatcher.
 * Snapshot migration rule (SPEC): seeded from the old user flag, refined by
 * the actual TX57 state (captured inside the window, corrected outside).
 */
final class AvasStateMachine {
    static final int TX57_DISABLED = VehicleRestorePolicy.PEDESTRIAN_SOUND_DISABLED;
    static final int TX57_ENABLED = VehicleRestorePolicy.PEDESTRIAN_SOUND_ENABLED;

    enum State {
        IDLE,
        WAKE_APPLY,
        CAPTURE_WINDOW,
        CORRECTIVE_REAPPLY
    }

    static final class ApplyRequest {
        final int tx57Value;
        final String reason;

        ApplyRequest(int i, String str) {
            this.tx57Value = i;
            this.reason = str;
        }
    }

    private State state = State.IDLE;
    private Integer snapshot;
    private boolean doorArmed;
    private boolean correctiveSpent;
    private String lastEvent = "init";

    synchronized State state() {
        return this.state;
    }

    synchronized Integer snapshot() {
        return this.snapshot;
    }

    synchronized boolean isDoorArmed() {
        return this.doorArmed;
    }

    synchronized String lastEvent() {
        return this.lastEvent;
    }

    /** Migration: seed the snapshot from the old user flag (only once). */
    synchronized boolean seedSnapshotFromUserFlag(boolean z) {
        if (this.snapshot != null) {
            return false;
        }
        this.snapshot = Integer.valueOf(VehicleRestorePolicy.pedestrianSoundState(z));
        return true;
    }

    /** WAKE_APPLY: apply the snapshot once per wake event (L44). */
    synchronized ApplyRequest onWakeEvent(String str) {
        this.lastEvent = str;
        this.state = State.WAKE_APPLY;
        this.correctiveSpent = false;
        if (this.snapshot == null) {
            this.state = State.IDLE;
            return null;
        }
        return new ApplyRequest(this.snapshot.intValue(), "wake apply: " + str);
    }

    /** Door opened: closes the capture window, arms the first Drive, wake apply. */
    synchronized ApplyRequest onDoorOpen() {
        this.lastEvent = "door open";
        this.doorArmed = true;
        if (this.state == State.CAPTURE_WINDOW) {
            this.state = State.IDLE;
        }
        return onWakeEvent("door");
    }

    /**
     * First Drive after a door open: opens the capture window and issues the
     * wake apply. Without a preceding door open the event is ignored (SPEC:
     * the window opens ONLY after the first Drive in wake of a door open).
     */
    synchronized ApplyRequest onFirstDrive() {
        this.lastEvent = "first drive";
        if (!this.doorArmed) {
            return null;
        }
        this.doorArmed = false;
        ApplyRequest applyRequestOnWakeEvent = onWakeEvent("first drive");
        this.state = State.CAPTURE_WINDOW;
        return applyRequestOnWakeEvent;
    }

    /** Sleep closes the capture window and resets the corrective budget. */
    synchronized void onSleep() {
        this.lastEvent = "sleep";
        this.state = State.IDLE;
        this.correctiveSpent = false;
    }

    /**
     * User toggle: commit the snapshot from the user flag before writing
     * (the snapshot is the restoring truth).
     */
    synchronized void onUserToggle(boolean z) {
        this.lastEvent = "user toggle";
        this.snapshot = Integer.valueOf(VehicleRestorePolicy.pedestrianSoundState(z));
    }

    /** Roll the snapshot back when the user write could not even be sent. */
    synchronized void restoreSnapshot(Integer num) {
        this.snapshot = num;
    }

    /**
     * Observed TX57 value (CB36 echo). Inside the capture window a differing
     * value is a normal change -> capture into the snapshot. Outside the
     * window a differing value is an OEM reset -> corrective reapply, at most
     * one dispatcher submit (its own retries keep the L181 <=3 attempts per
     * cycle budget) until the next wake/sleep resets the latch.
     */
    synchronized ApplyRequest onObservedValue(int i) {
        if (this.snapshot == null) {
            this.snapshot = Integer.valueOf(i);
            this.lastEvent = "seed from actual";
            return null;
        }
        if (i == this.snapshot.intValue()) {
            return null;
        }
        if (this.state == State.CAPTURE_WINDOW) {
            this.snapshot = Integer.valueOf(i);
            this.lastEvent = "captured in window";
            return null;
        }
        if (this.correctiveSpent) {
            this.lastEvent = "oem reset ignored (budget spent)";
            return null;
        }
        this.correctiveSpent = true;
        this.state = State.CORRECTIVE_REAPPLY;
        this.lastEvent = "oem reset outside window";
        return new ApplyRequest(this.snapshot.intValue(), "oem reset outside capture window");
    }

    /** Terminal read-back result for the in-flight AVAS write. */
    synchronized void onWriteSettled(boolean z) {
        if (z) {
            if (this.state == State.WAKE_APPLY || this.state == State.CORRECTIVE_REAPPLY) {
                this.state = State.IDLE;
            }
            this.lastEvent = "confirmed";
            return;
        }
        if (this.state == State.WAKE_APPLY || this.state == State.CORRECTIVE_REAPPLY) {
            this.state = State.IDLE;
        }
        this.lastEvent = "not confirmed";
    }
}
