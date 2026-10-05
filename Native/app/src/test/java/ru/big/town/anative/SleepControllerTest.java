package ru.big.town.anative;

import org.junit.Test;

import static org.junit.Assert.*;

public class SleepControllerTest {

    @Test public void initialAwakeWithFirstSession() {
        SleepController c = new SleepController();
        assertEquals(SleepController.State.AWAKE, c.state());
        assertEquals(1L, c.sessionId());
        assertTrue(c.isCurrentSession(1L));
        assertFalse(c.isCurrentSession(0L));
        assertFalse(c.isCurrentSession(2L));
    }

    @Test public void screenOffEntersSleepingAndBumpsSession() {
        SleepController c = new SleepController();
        assertTrue(c.onSleepTrigger(SleepController.Event.SCREEN_OFF));
        assertEquals(SleepController.State.SLEEPING, c.state());
        assertEquals(2L, c.sessionId());
        c.onSleepComplete();
        assertEquals(SleepController.State.ASLEEP, c.state());
        assertEquals(2L, c.sessionId());
    }

    @Test public void duplicateScreenOffRejectedUntilOppositeEvent() {
        SleepController c = new SleepController();
        assertTrue(c.onSleepTrigger(SleepController.Event.SCREEN_OFF));
        assertFalse(c.onSleepTrigger(SleepController.Event.SCREEN_OFF));
        assertEquals(2L, c.sessionId());
        c.onSleepComplete();
        assertFalse(c.onSleepTrigger(SleepController.Event.SCREEN_OFF));
        assertEquals(2L, c.sessionId());
        assertTrue(c.onWakeTrigger(SleepController.Event.SCREEN_ON));
        assertTrue(c.onSleepTrigger(SleepController.Event.SCREEN_OFF));
        assertEquals(3L, c.sessionId());
    }

    @Test public void duplicateScreenOnRejectedUntilOppositeEvent() {
        SleepController c = new SleepController();
        assertTrue(c.onSleepTrigger(SleepController.Event.SCREEN_OFF));
        c.onSleepComplete();
        assertTrue(c.onWakeTrigger(SleepController.Event.SCREEN_ON));
        assertEquals(SleepController.State.WAKING, c.state());
        c.onWakeComplete();
        assertEquals(SleepController.State.AWAKE, c.state());
        assertFalse(c.onWakeTrigger(SleepController.Event.SCREEN_ON));
        assertTrue(c.onSleepTrigger(SleepController.Event.SCREEN_OFF));
    }

    @Test public void wakeDoesNotBumpSession() {
        SleepController c = new SleepController();
        c.onSleepTrigger(SleepController.Event.SCREEN_OFF);
        c.onSleepComplete();
        assertTrue(c.onWakeTrigger(SleepController.Event.SCREEN_ON));
        assertEquals(2L, c.sessionId());
        c.onWakeComplete();
        assertEquals(2L, c.sessionId());
        assertTrue(c.isCurrentSession(2L));
    }

    @Test public void monotonicSessionAcrossSleepWakeCycles() {
        SleepController c = new SleepController();
        for (long expected = 2; expected <= 4; expected++) {
            assertTrue(c.onSleepTrigger(SleepController.Event.SCREEN_OFF));
            assertEquals(expected, c.sessionId());
            c.onSleepComplete();
            assertTrue(c.onWakeTrigger(SleepController.Event.SCREEN_ON));
            c.onWakeComplete();
        }
        assertEquals(4L, c.sessionId());
    }

    @Test public void staleSessionRejectedAfterNextSleep() {
        SleepController c = new SleepController();
        long stale = c.sessionId();
        assertTrue(c.isCurrentSession(stale));
        c.onSleepTrigger(SleepController.Event.SCREEN_OFF);
        assertFalse(c.isCurrentSession(stale));
        assertTrue(c.isCurrentSession(c.sessionId()));
    }

    @Test public void powerSleepConfirmDoesNotDoubleBump() {
        SleepController c = new SleepController();
        assertTrue(c.onSleepTrigger(SleepController.Event.POWER_SLEEP));
        assertEquals(SleepController.State.SLEEPING, c.state());
        assertEquals(2L, c.sessionId());
        c.onSleepComplete();
        assertTrue(c.onSleepTrigger(SleepController.Event.POWER_SLEEP));
        assertEquals(SleepController.State.ASLEEP, c.state());
        assertEquals(2L, c.sessionId());
    }

    @Test public void screenOnAfterPowerSleepStillAccepted() {
        SleepController c = new SleepController();
        assertTrue(c.onSleepTrigger(SleepController.Event.POWER_SLEEP));
        c.onSleepComplete();
        assertTrue(c.onSleepTrigger(SleepController.Event.SCREEN_OFF));
        assertEquals(SleepController.State.ASLEEP, c.state());
        assertEquals(2L, c.sessionId());
        assertTrue(c.onWakeTrigger(SleepController.Event.POWER_WAKE));
        assertEquals(SleepController.State.WAKING, c.state());
        assertEquals(2L, c.sessionId());
    }

    @Test public void garageWakeAcceptedEvenWhenRepeated() {
        SleepController c = new SleepController();
        c.onSleepTrigger(SleepController.Event.SCREEN_OFF);
        c.onSleepComplete();
        assertTrue(c.onWakeTrigger(SleepController.Event.GARAGE_WAKE));
        assertEquals(SleepController.State.WAKING, c.state());
        assertTrue(c.onWakeTrigger(SleepController.Event.GARAGE_WAKE));
    }

    @Test public void screenOnAfterGarageAccepted() {
        SleepController c = new SleepController();
        c.onSleepTrigger(SleepController.Event.SCREEN_OFF);
        c.onSleepComplete();
        assertTrue(c.onWakeTrigger(SleepController.Event.GARAGE_WAKE));
        assertTrue(c.onWakeTrigger(SleepController.Event.SCREEN_ON));
    }

    @Test public void wrongEventKindRejected() {
        SleepController c = new SleepController();
        assertFalse(c.onSleepTrigger(SleepController.Event.SCREEN_ON));
        assertFalse(c.onSleepTrigger(SleepController.Event.GARAGE_WAKE));
        assertFalse(c.onWakeTrigger(SleepController.Event.SCREEN_OFF));
        assertFalse(c.onWakeTrigger(SleepController.Event.POWER_SLEEP));
        assertEquals(SleepController.State.AWAKE, c.state());
        assertEquals(1L, c.sessionId());
    }

    @Test public void completionEventsOnlyApplyFromMatchingState() {
        SleepController c = new SleepController();
        c.onWakeComplete();
        assertEquals(SleepController.State.AWAKE, c.state());
        c.onSleepComplete();
        assertEquals(SleepController.State.AWAKE, c.state());
        c.onSleepTrigger(SleepController.Event.SCREEN_OFF);
        c.onWakeComplete();
        assertEquals(SleepController.State.SLEEPING, c.state());
        c.onSleepComplete();
        assertEquals(SleepController.State.ASLEEP, c.state());
    }

    @Test public void wakeFromSleepingEntersWakingWithoutBump() {
        SleepController c = new SleepController();
        c.onSleepTrigger(SleepController.Event.SCREEN_OFF);
        assertTrue(c.onWakeTrigger(SleepController.Event.SCREEN_ON));
        assertEquals(SleepController.State.WAKING, c.state());
        assertEquals(2L, c.sessionId());
    }
}
