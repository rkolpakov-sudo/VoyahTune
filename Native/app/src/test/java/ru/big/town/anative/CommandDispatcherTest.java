package ru.big.town.anative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommandDispatcherTest {

    private FakeScheduler scheduler;
    private CommandDispatcher dispatcher;
    private final List<CommandResult.State> states = new ArrayList();
    private int sends;

    private void setUp() {
        this.scheduler = new FakeScheduler();
        this.dispatcher = new CommandDispatcher(this.scheduler, new CommandDispatcher.Clock() { // from class: ru.big.town.anative.CommandDispatcherTest$$ExternalSyntheticLambda0
            @Override // ru.big.town.anative.CommandDispatcher.Clock
            public final long now() {
                return CommandDispatcherTest.this.scheduler.now();
            }
        }, new Random(42), new CommandDispatcher.Listener() { // from class: ru.big.town.anative.CommandDispatcherTest$$ExternalSyntheticLambda1
            @Override // ru.big.town.anative.CommandDispatcher.Listener
            public final void onResult(CommandResult commandResult) {
                CommandDispatcherTest.this.states.add(commandResult.state());
            }
        });
    }

    private CommandDispatcher.SendAction sender(final boolean z) {
        return new CommandDispatcher.SendAction() { // from class: ru.big.town.anative.CommandDispatcherTest$$ExternalSyntheticLambda2
            @Override // ru.big.town.anative.CommandDispatcher.SendAction
            public final boolean send() {
                CommandDispatcherTest.this.sends++;
                return z;
            }
        };
    }

    @Test public void fastAckConfirmsWithoutRetry() {
        setUp();
        this.dispatcher.submit("drive", sender(true));
        assertEquals(CommandResult.State.PENDING_ACK, this.dispatcher.lastResult("drive").state());
        assertEquals(1, this.dispatcher.lastResult("drive").attempts());
        this.dispatcher.onAck("drive", "VCU_Indication 0x2FA");
        CommandResult result = this.dispatcher.lastResult("drive");
        assertEquals(CommandResult.State.CONFIRMED, result.state());
        assertEquals("VCU_Indication 0x2FA", result.ackSource());
        assertEquals(1, result.attempts());
        assertEquals(1, this.sends);
        assertTrue(this.scheduler.idle());
    }

    @Test public void silenceTimesOutAfterTwoRetries() {
        setUp();
        this.dispatcher.submit("drive", sender(true));
        assertEquals(1, this.sends);
        this.scheduler.advance(READ_BACK);
        this.scheduler.advance(READ_BACK_RETRY);
        assertEquals(2, this.sends);
        this.scheduler.advance(READ_BACK);
        this.scheduler.advance(READ_BACK_RETRY);
        assertEquals(3, this.sends);
        this.scheduler.advance(READ_BACK);
        CommandResult result = this.dispatcher.lastResult("drive");
        assertEquals(CommandResult.State.TIMEOUT, result.state());
        assertEquals(3, result.attempts());
        assertEquals(3, this.sends);
        assertTrue(this.scheduler.idle());
        assertEquals(5, this.scheduler.scheduledDelays.size());
        long longValue = this.scheduler.scheduledDelays.get(1).longValue();
        assertTrue("first backoff " + longValue, longValue >= 270 && longValue <= 330);
        long longValue2 = this.scheduler.scheduledDelays.get(3).longValue();
        assertTrue("second backoff " + longValue2, longValue2 >= 810 && longValue2 <= 990);
    }

    @Test public void sendRejectionFailsImmediately() {
        setUp();
        this.dispatcher.submit("drive", sender(false));
        CommandResult result = this.dispatcher.lastResult("drive");
        assertEquals(CommandResult.State.FAILED, result.state());
        assertEquals(1, result.attempts());
        assertEquals(Arrays.asList(CommandResult.State.FAILED), this.states);
        assertTrue(this.scheduler.idle());
    }

    @Test public void contradictoryReadBackFailsWithSource() {
        setUp();
        this.dispatcher.submit("suspension", sender(true));
        this.dispatcher.onMismatch("suspension", "ASC 785");
        CommandResult result = this.dispatcher.lastResult("suspension");
        assertEquals(CommandResult.State.FAILED, result.state());
        assertEquals("ASC 785", result.ackSource());
        assertEquals(1, this.sends);
        assertTrue(this.scheduler.idle());
    }

    @Test public void lateAckDuringBackoffStillConfirms() {
        setUp();
        this.dispatcher.submit("drive", sender(true));
        this.scheduler.advance(READ_BACK);
        this.dispatcher.onAck("drive", "TX57 cache");
        CommandResult result = this.dispatcher.lastResult("drive");
        assertEquals(CommandResult.State.CONFIRMED, result.state());
        assertEquals(1, result.attempts());
        assertEquals(1, this.sends);
        assertTrue(this.scheduler.idle());
    }

    @Test public void ackWithoutActiveCommandIgnored() {
        setUp();
        this.dispatcher.onAck("drive", "VCU_Indication 0x2FA");
        assertNull(this.dispatcher.lastResult("drive"));
        assertFalse(this.dispatcher.hasActive("drive"));
        this.dispatcher.submit("drive", sender(true));
        this.dispatcher.onAck("drive", "VCU_Indication 0x2FA");
        this.dispatcher.onAck("drive", "VCU_Indication 0x2FA");
        assertEquals(CommandResult.State.CONFIRMED, this.dispatcher.lastResult("drive").state());
        assertEquals(Arrays.asList(CommandResult.State.PENDING_ACK, CommandResult.State.CONFIRMED), this.states);
    }

    @Test public void ackForDifferentFeatureIgnored() {
        setUp();
        this.dispatcher.submit("drive", sender(true));
        this.dispatcher.onAck("light", "LightStatus");
        assertEquals(CommandResult.State.PENDING_ACK, this.dispatcher.lastResult("drive").state());
    }

    @Test public void newSubmitSupersedesPreviousCommand() {
        setUp();
        this.dispatcher.submit("drive", sender(true));
        this.dispatcher.submit("drive", sender(true));
        assertEquals(2, this.sends);
        assertEquals(Arrays.asList(CommandResult.State.PENDING_ACK, CommandResult.State.FAILED, CommandResult.State.PENDING_ACK), this.states);
        assertEquals(CommandResult.State.PENDING_ACK, this.dispatcher.lastResult("drive").state());
        this.dispatcher.onAck("drive", "VCU_Indication 0x2FA");
        assertEquals(CommandResult.State.CONFIRMED, this.dispatcher.lastResult("drive").state());
        assertTrue(this.scheduler.idle());
    }

    @Test public void retryKeepsPendingWithIncrementedAttempts() {
        setUp();
        this.dispatcher.submit("drive", sender(true));
        this.scheduler.advance(READ_BACK);
        this.scheduler.advance(READ_BACK_RETRY);
        CommandResult result = this.dispatcher.lastResult("drive");
        assertEquals(CommandResult.State.PENDING_ACK, result.state());
        assertEquals(2, result.attempts());
        assertEquals(2, this.sends);
        this.dispatcher.onAck("drive", "VCU_Indication 0x2FA");
        assertEquals(2, this.dispatcher.lastResult("drive").attempts());
        assertEquals(CommandResult.State.CONFIRMED, this.dispatcher.lastResult("drive").state());
    }

    @Test public void submitReturnsFirstSendOutcome() {
        setUp();
        assertTrue(this.dispatcher.submit("drive", sender(true)));
        assertEquals(CommandResult.State.PENDING_ACK, this.dispatcher.lastResult("drive").state());
        assertFalse(this.dispatcher.submit("light", sender(false)));
        assertEquals(CommandResult.State.FAILED, this.dispatcher.lastResult("light").state());
    }

    @Test public void sendExceptionFailsInsteadOfCrashing() {
        setUp();
        CommandDispatcher.SendAction sendAction = new CommandDispatcher.SendAction() { // from class: ru.big.town.anative.CommandDispatcherTest$$ExternalSyntheticLambda3
            @Override // ru.big.town.anative.CommandDispatcher.SendAction
            public boolean send() {
                throw new IllegalStateException("transport down");
            }
        };
        assertFalse(this.dispatcher.submit("drive", sendAction));
        CommandResult result = this.dispatcher.lastResult("drive");
        assertEquals(CommandResult.State.FAILED, result.state());
        assertEquals(1, result.attempts());
        assertTrue(this.scheduler.idle());
    }

    private static final long READ_BACK = 1500;
    private static final long READ_BACK_RETRY = 1000;
}
