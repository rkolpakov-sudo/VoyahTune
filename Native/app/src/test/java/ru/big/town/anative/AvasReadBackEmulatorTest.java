package ru.big.town.anative;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import ru.big.town.hil.CanEmulatorCore;
import ru.big.town.hil.CallbackSink;
import ru.big.town.hil.EmuConfig;
import ru.big.town.hil.TxCode;
import ru.big.town.hil.TxLogEntry;
import ru.big.town.hil.WriteMode;

import static org.junit.Assert.*;

/**
 * IMP-02 (SPEC L44): AVAS state machine + IMP-01 read-back dispatcher against
 * the can-emulator. Acceptance: the cycle
 * "выкл -> запереть(гостевой) -> открыть -> Drive" x10 with read-back after
 * every write and corrective reapply outside the capture window.
 */
public class AvasReadBackEmulatorTest {

    private static final int AVAS_ID = VehicleRestorePolicy.PEDESTRIAN_SOUND_ID;
    private static final int OFF = AvasStateMachine.TX57_DISABLED;
    private static final int ON = AvasStateMachine.TX57_ENABLED;

    /** Mirrors AvasController: machine + dispatcher + emulator echo routing. */
    private static final class Harness {
        final AvasStateMachine machine = new AvasStateMachine();
        final FakeScheduler scheduler = new FakeScheduler();
        final CanEmulatorCore core;
        final List<CommandResult.State> states = new ArrayList<>();
        final CommandDispatcher dispatcher;
        Integer pendingWrite;

        Harness(WriteMode mode) {
            EmuConfig config = new EmuConfig();
            config.writeMode = mode;
            if (mode == WriteMode.CONFLICTING) {
                config.conflictValue = ON;
            }
            this.core = new CanEmulatorCore(config);
            this.core.setSink(new CallbackSink() {
                @Override
                public void deliver(int code, List<Object> args) {
                    if (code == TxCode.CB_VEHICLE_STATE && args.size() >= 4) {
                        Harness.this.deliverEcho(((Integer) args.get(3)).intValue());
                    }
                }
            });
            this.dispatcher = new CommandDispatcher(this.scheduler, () -> this.scheduler.now(),
                    new Random(7), result -> this.states.add(result.state()));
        }

        boolean apply(AvasStateMachine.ApplyRequest request) {
            if (request == null) {
                return false;
            }
            final int value = request.tx57Value;
            this.pendingWrite = Integer.valueOf(value);
            boolean ok = this.dispatcher.submit(ReadBackTable.FEATURE_AVAS, () -> write(value));
            if (!ok) {
                this.pendingWrite = null;
            }
            return ok;
        }

        /** AvasController.handleEcho for TX57 id 665. */
        void deliverEcho(int value) {
            Integer expected = this.pendingWrite;
            boolean active = this.dispatcher.hasActive(ReadBackTable.FEATURE_AVAS);
            if (expected != null && active) {
                if (value == expected.intValue()) {
                    this.pendingWrite = null;
                    this.dispatcher.onAck(ReadBackTable.FEATURE_AVAS, ReadBackTable.SOURCE_AVAS_TX57);
                    this.machine.onWriteSettled(true);
                } else {
                    this.pendingWrite = null;
                    this.dispatcher.onMismatch(ReadBackTable.FEATURE_AVAS, "expected=" + expected + " actual=" + value);
                    this.machine.onWriteSettled(false);
                }
                return;
            }
            this.pendingWrite = null;
            AvasStateMachine.ApplyRequest request = this.machine.onObservedValue(value);
            if (request != null) {
                apply(request);
            }
        }

        boolean userToggle(boolean disable) {
            Integer prev = this.machine.snapshot();
            this.machine.onUserToggle(disable);
            boolean ok = apply(new AvasStateMachine.ApplyRequest(
                    VehicleRestorePolicy.pedestrianSoundState(disable), "user toggle"));
            if (!ok) {
                this.machine.restoreSnapshot(prev);
            }
            return ok;
        }

        boolean write(int value) {
            return this.core.transact(TxCode.SET_STATE, List.of(1, 0, AVAS_ID, value)).send;
        }

        void doorOpen() {
            apply(this.machine.onDoorOpen());
        }

        void firstDrive() {
            apply(this.machine.onFirstDrive());
        }

        int writes() {
            return this.core.log().count(TxLogEntry.Dir.CLIENT_TO_SVC, TxCode.SET_STATE);
        }
    }

    @Test
    public void userToggleConfirmsThroughReadBack() {
        Harness harness = new Harness(WriteMode.ACK);
        harness.machine.seedSnapshotFromUserFlag(false);
        assertEquals(Integer.valueOf(ON), harness.machine.snapshot());
        assertTrue(harness.userToggle(true));
        assertEquals(Integer.valueOf(OFF), harness.machine.snapshot());
        assertEquals(List.of(CommandResult.State.CONFIRMED), harness.states);
        assertEquals(1, harness.writes());
        assertEquals(Integer.valueOf(OFF), harness.core.cacheValue(AVAS_ID));
        assertTrue(harness.scheduler.idle());
    }

    @Test
    public void oemResetOutsideWindowTriggersCorrectiveReapply() {
        Harness harness = new Harness(WriteMode.ACK);
        harness.machine.seedSnapshotFromUserFlag(true);
        harness.deliverEcho(ON);
        assertEquals("corrective write confirms synchronously", AvasStateMachine.State.IDLE, harness.machine.state());
        assertEquals(List.of(CommandResult.State.CONFIRMED), harness.states);
        assertEquals(Integer.valueOf(OFF), harness.core.cacheValue(AVAS_ID));
        assertEquals(1, harness.writes());
    }

    @Test
    public void inWindowChangeIsCapturedWithoutWrite() {
        Harness harness = new Harness(WriteMode.ACK);
        harness.machine.seedSnapshotFromUserFlag(true);
        harness.doorOpen();
        harness.firstDrive();
        assertEquals(AvasStateMachine.State.CAPTURE_WINDOW, harness.machine.state());
        int writesBefore = harness.writes();
        harness.deliverEcho(ON);
        assertEquals(Integer.valueOf(ON), harness.machine.snapshot());
        assertEquals(AvasStateMachine.State.CAPTURE_WINDOW, harness.machine.state());
        assertEquals(writesBefore, harness.writes());
    }

    @Test
    public void conflictingEchoFailsCommandAndKeepsSnapshot() {
        Harness harness = new Harness(WriteMode.CONFLICTING);
        harness.machine.seedSnapshotFromUserFlag(false);
        assertTrue("first send is accepted", harness.userToggle(true));
        assertEquals(List.of(CommandResult.State.FAILED), harness.states);
        assertEquals(Integer.valueOf(OFF), harness.machine.snapshot());
        assertEquals(1, harness.writes());
    }

    @Test
    public void correctiveBudgetHoldsUntilNextWakeEvent() {
        Harness harness = new Harness(WriteMode.ACK);
        harness.machine.seedSnapshotFromUserFlag(true);
        harness.deliverEcho(ON);
        assertEquals(1, harness.writes());
        harness.deliverEcho(ON);
        assertEquals("budget spent: no second corrective", 1, harness.writes());
        harness.machine.onSleep();
        harness.deliverEcho(ON);
        assertEquals("sleep resets the budget", 2, harness.writes());
        assertEquals(Integer.valueOf(OFF), harness.machine.snapshot());
    }

    @Test
    public void acceptanceCycleX10() {
        Harness harness = new Harness(WriteMode.ACK);
        for (int cycle = 1; cycle <= 10; cycle++) {
            harness.machine.seedSnapshotFromUserFlag(false);

            // выкл
            assertTrue("cycle " + cycle, harness.userToggle(true));
            assertEquals("cycle " + cycle, Integer.valueOf(OFF), harness.machine.snapshot());
            assertEquals("cycle " + cycle, Integer.valueOf(OFF), harness.core.cacheValue(AVAS_ID));

            // запереть (гостевой): OEM resets AVAS back to ON while locked
            harness.machine.onSleep();
            harness.deliverEcho(ON);
            assertEquals("cycle " + cycle, AvasStateMachine.State.IDLE, harness.machine.state());
            assertEquals("cycle " + cycle, Integer.valueOf(OFF), harness.core.cacheValue(AVAS_ID));

            // открыть: wake apply confirms synchronously -> back to IDLE
            harness.doorOpen();
            assertEquals("cycle " + cycle, AvasStateMachine.State.IDLE, harness.machine.state());
            assertEquals("cycle " + cycle, CommandResult.State.CONFIRMED,
                    harness.states.get(harness.states.size() - 1));

            // Drive: capture window opens, apply confirms
            harness.firstDrive();
            assertEquals("cycle " + cycle, AvasStateMachine.State.CAPTURE_WINDOW, harness.machine.state());
            assertEquals("cycle " + cycle, Integer.valueOf(OFF), harness.core.cacheValue(AVAS_ID));

            // inside the window the confirmed value is a no-op observation
            harness.deliverEcho(OFF);
            assertEquals("cycle " + cycle, Integer.valueOf(OFF), harness.machine.snapshot());
            assertEquals("cycle " + cycle, AvasStateMachine.State.CAPTURE_WINDOW, harness.machine.state());

            harness.machine.onSleep();
            assertEquals("cycle " + cycle, AvasStateMachine.State.IDLE, harness.machine.state());
        }
        assertEquals("every cycle must end confirmed",
                CommandResult.State.CONFIRMED, harness.states.get(harness.states.size() - 1));
        assertTrue(harness.scheduler.idle());
    }
}
