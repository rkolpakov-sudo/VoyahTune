package ru.big.town.anative;

/**
 * IMP-06 (SPEC L48): sleep/wake state machine.
 *
 * States: AWAKE -> SLEEPING -> ASLEEP -> WAKING -> AWAKE.
 *
 * Sleep triggers (SCREEN_OFF / power sleep state): from AWAKE or WAKING the
 * controller enters SLEEPING and the monotonic sessionId is bumped, ending the
 * awake period; a confirming sleep trigger moves SLEEPING -> ASLEEP without
 * bumping. Wake triggers (SCREEN_ON / GARAGE_MODE_OFF / power wake state) move
 * SLEEPING or ASLEEP -> WAKING; onWakeComplete() returns to AWAKE.
 *
 * Repeated identical screen events (two SCREEN_OFF in a row, two SCREEN_ON in
 * a row) are rejected until the opposite screen event arrives, so callers can
 * skip duplicate gate resets. Power and garage events are never deduplicated
 * (their effect chains differ per source, behaviour of 3.22 is preserved).
 *
 * Work tagged with sessionId() at schedule time is validated later via
 * isCurrentSession(): it is rejected once any sleep period started a newer
 * session.
 */
final class SleepController {
    enum State {
        AWAKE,
        SLEEPING,
        ASLEEP,
        WAKING
    }

    enum Event {
        SCREEN_OFF,
        SCREEN_ON,
        GARAGE_WAKE,
        POWER_WAKE,
        POWER_SLEEP
    }

    private State state = State.AWAKE;
    private long sessionId = 1;
    private Event lastScreenEvent;

    synchronized State state() {
        return this.state;
    }

    synchronized long sessionId() {
        return this.sessionId;
    }

    synchronized boolean isCurrentSession(long j) {
        return j > 0 && j == this.sessionId;
    }

    synchronized boolean onSleepTrigger(Event event) {
        if (event != Event.SCREEN_OFF && event != Event.POWER_SLEEP) {
            return false;
        }
        if (isScreenEvent(event) && event == this.lastScreenEvent) {
            return false;
        }
        if (isScreenEvent(event)) {
            this.lastScreenEvent = event;
        }
        State state = this.state;
        if (state == State.AWAKE || state == State.WAKING) {
            this.sessionId++;
            this.state = State.SLEEPING;
        }
        return true;
    }

    synchronized boolean onWakeTrigger(Event event) {
        if (event != Event.SCREEN_ON && event != Event.GARAGE_WAKE && event != Event.POWER_WAKE) {
            return false;
        }
        if (event == Event.SCREEN_ON) {
            if (this.lastScreenEvent == Event.SCREEN_ON) {
                return false;
            }
            this.lastScreenEvent = Event.SCREEN_ON;
        }
        State state = this.state;
        if (state == State.SLEEPING || state == State.ASLEEP) {
            this.state = State.WAKING;
        }
        return true;
    }

    synchronized void onSleepComplete() {
        if (this.state == State.SLEEPING) {
            this.state = State.ASLEEP;
        }
    }

    synchronized void onWakeComplete() {
        if (this.state == State.WAKING) {
            this.state = State.AWAKE;
        }
    }

    private static boolean isScreenEvent(Event event) {
        return event == Event.SCREEN_OFF || event == Event.SCREEN_ON;
    }
}
