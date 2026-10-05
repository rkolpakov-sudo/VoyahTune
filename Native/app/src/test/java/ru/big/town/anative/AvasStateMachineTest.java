package ru.big.town.anative;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * IMP-02 (SPEC L44): pure state machine tests. The acceptance cycle
 * "выкл -> запереть(гостевой) -> открыть -> Drive" runs x10.
 */
public class AvasStateMachineTest {

    private static final int OFF = AvasStateMachine.TX57_DISABLED;
    private static final int ON = AvasStateMachine.TX57_ENABLED;

    @Test
    public void initialSeedComesFromUserFlag() {
        AvasStateMachine machine = new AvasStateMachine();
        assertTrue(machine.seedSnapshotFromUserFlag(true));
        assertEquals(Integer.valueOf(OFF), machine.snapshot());
        assertFalse(machine.seedSnapshotFromUserFlag(false));
        assertEquals(Integer.valueOf(OFF), machine.snapshot());
        assertEquals(AvasStateMachine.State.IDLE, machine.state());
    }

    @Test
    public void wakeEventAppliesSnapshotOncePerEvent() {
        AvasStateMachine machine = new AvasStateMachine();
        machine.seedSnapshotFromUserFlag(true);
        AvasStateMachine.ApplyRequest first = machine.onWakeEvent("ignition");
        assertNotNull(first);
        assertEquals(OFF, first.tx57Value);
        assertEquals(AvasStateMachine.State.WAKE_APPLY, machine.state());
        machine.onWriteSettled(true);
        assertEquals(AvasStateMachine.State.IDLE, machine.state());
        machine.onWakeEvent("ignition");
        assertEquals(AvasStateMachine.State.WAKE_APPLY, machine.state());
    }

    @Test
    public void windowOpensOnlyAfterFirstDriveFollowingDoor() {
        AvasStateMachine machine = new AvasStateMachine();
        machine.seedSnapshotFromUserFlag(true);
        assertNull(machine.onFirstDrive());
        assertEquals(AvasStateMachine.State.IDLE, machine.state());
        machine.onDoorOpen();
        machine.onWriteSettled(true);
        AvasStateMachine.ApplyRequest drive = machine.onFirstDrive();
        assertNotNull(drive);
        assertEquals(AvasStateMachine.State.CAPTURE_WINDOW, machine.state());
        assertFalse(machine.isDoorArmed());
    }

    @Test
    public void windowClosesOnDoorAndSleep() {
        AvasStateMachine machine = new AvasStateMachine();
        machine.seedSnapshotFromUserFlag(true);
        machine.onDoorOpen();
        machine.onFirstDrive();
        assertEquals(AvasStateMachine.State.CAPTURE_WINDOW, machine.state());
        machine.onDoorOpen();
        assertEquals(AvasStateMachine.State.WAKE_APPLY, machine.state());
        machine.onWriteSettled(true);
        assertEquals(AvasStateMachine.State.IDLE, machine.state());

        machine.onDoorOpen();
        machine.onFirstDrive();
        assertEquals(AvasStateMachine.State.CAPTURE_WINDOW, machine.state());
        machine.onSleep();
        assertEquals(AvasStateMachine.State.IDLE, machine.state());
    }

    @Test
    public void inWindowChangeCapturesIntoSnapshot() {
        AvasStateMachine machine = new AvasStateMachine();
        machine.seedSnapshotFromUserFlag(true);
        machine.onDoorOpen();
        machine.onFirstDrive();
        assertNull(machine.onObservedValue(ON));
        assertEquals(Integer.valueOf(ON), machine.snapshot());
        assertEquals(AvasStateMachine.State.CAPTURE_WINDOW, machine.state());
    }

    @Test
    public void outOfWindowChangeTriggersCorrectiveReapply() {
        AvasStateMachine machine = new AvasStateMachine();
        machine.seedSnapshotFromUserFlag(true);
        AvasStateMachine.ApplyRequest corrective = machine.onObservedValue(ON);
        assertNotNull(corrective);
        assertEquals(OFF, corrective.tx57Value);
        assertEquals(AvasStateMachine.State.CORRECTIVE_REAPPLY, machine.state());
        machine.onWriteSettled(true);
        assertEquals(AvasStateMachine.State.IDLE, machine.state());
    }

    @Test
    public void correctiveBudgetIsOneSubmitPerCycleAndResetsOnWake() {
        AvasStateMachine machine = new AvasStateMachine();
        machine.seedSnapshotFromUserFlag(true);
        assertNotNull(machine.onObservedValue(ON));
        assertNull(machine.onObservedValue(ON));
        assertNull(machine.onObservedValue(ON));
        machine.onWriteSettled(false);
        assertEquals(AvasStateMachine.State.IDLE, machine.state());
        machine.onWakeEvent("ignition");
        machine.onWriteSettled(true);
        AvasStateMachine.ApplyRequest next = machine.onObservedValue(ON);
        assertNotNull(next);
        assertEquals(OFF, next.tx57Value);
    }

    @Test
    public void sleepResetsCorrectiveBudget() {
        AvasStateMachine machine = new AvasStateMachine();
        machine.seedSnapshotFromUserFlag(true);
        assertNotNull(machine.onObservedValue(ON));
        machine.onSleep();
        assertEquals(AvasStateMachine.State.IDLE, machine.state());
        AvasStateMachine.ApplyRequest next = machine.onObservedValue(ON);
        assertNotNull(next);
        assertEquals(OFF, next.tx57Value);
    }

    @Test
    public void userToggleCommitsSnapshotBeforeWrite() {
        AvasStateMachine machine = new AvasStateMachine();
        machine.seedSnapshotFromUserFlag(false);
        assertEquals(Integer.valueOf(ON), machine.snapshot());
        machine.onUserToggle(true);
        assertEquals(Integer.valueOf(OFF), machine.snapshot());
        machine.restoreSnapshot(Integer.valueOf(ON));
        assertEquals(Integer.valueOf(ON), machine.snapshot());
    }

    @Test
    public void equalObservedValueIsNoop() {
        AvasStateMachine machine = new AvasStateMachine();
        machine.seedSnapshotFromUserFlag(true);
        assertNull(machine.onObservedValue(OFF));
        assertEquals(Integer.valueOf(OFF), machine.snapshot());
        assertEquals(AvasStateMachine.State.IDLE, machine.state());
    }

    @Test
    public void acceptanceCycleX10() {
        for (int cycle = 1; cycle <= 10; cycle++) {
            AvasStateMachine machine = new AvasStateMachine();
            assertTrue("cycle " + cycle + ": seed", machine.seedSnapshotFromUserFlag(false));
            assertEquals("cycle " + cycle + ": seeded ON", Integer.valueOf(ON), machine.snapshot());

            // выкл (user toggles AVAS off)
            machine.onUserToggle(true);
            assertEquals("cycle " + cycle, Integer.valueOf(OFF), machine.snapshot());

            // запереть (гостевой): sleep closes the window, OEM resets AVAS to ON
            machine.onSleep();
            assertEquals("cycle " + cycle, AvasStateMachine.State.IDLE, machine.state());
            AvasStateMachine.ApplyRequest corrective = machine.onObservedValue(ON);
            assertNotNull("cycle " + cycle + ": oem reset must be corrected", corrective);
            assertEquals("cycle " + cycle, OFF, corrective.tx57Value);
            assertEquals("cycle " + cycle, AvasStateMachine.State.CORRECTIVE_REAPPLY, machine.state());

            // открыть: door wake applies the snapshot again (<=3 sends this cycle)
            AvasStateMachine.ApplyRequest wake = machine.onDoorOpen();
            assertNotNull("cycle " + cycle + ": door wake apply", wake);
            assertEquals("cycle " + cycle, OFF, wake.tx57Value);
            assertEquals("cycle " + cycle, AvasStateMachine.State.WAKE_APPLY, machine.state());

            // Drive: capture window opens, wake apply confirms
            AvasStateMachine.ApplyRequest drive = machine.onFirstDrive();
            assertNotNull("cycle " + cycle + ": first drive apply", drive);
            assertEquals("cycle " + cycle, OFF, drive.tx57Value);
            assertEquals("cycle " + cycle, AvasStateMachine.State.CAPTURE_WINDOW, machine.state());
            machine.onWriteSettled(true);
            assertEquals("cycle " + cycle, AvasStateMachine.State.CAPTURE_WINDOW, machine.state());
            assertEquals("cycle " + cycle, Integer.valueOf(OFF), machine.snapshot());

            // в окне: observed echo of the snapshot value is a no-op
            assertNull("cycle " + cycle, machine.onObservedValue(OFF));

            // следующий цикл начинается заново
            machine.onSleep();
            assertEquals("cycle " + cycle, AvasStateMachine.State.IDLE, machine.state());
        }
    }
}
