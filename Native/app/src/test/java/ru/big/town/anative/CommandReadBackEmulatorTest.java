package ru.big.town.anative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import ru.big.town.hil.CanEmulatorCore;
import ru.big.town.hil.CallbackSink;
import ru.big.town.hil.EmuConfig;
import ru.big.town.hil.TxCode;
import ru.big.town.hil.TxLogEntry;
import ru.big.town.hil.TxResult;
import ru.big.town.hil.WriteMode;

import static org.junit.Assert.*;

/**
 * IMP-01 (SPEC L43) read-back integration: CommandDispatcher against the
 * can-emulator — echo, silence, late echo, contradictory echo.
 */
public class CommandReadBackEmulatorTest {

    private static final int STATE_ID = 665;
    private static final String FEATURE = "driveMode";
    private static final String EXPECTED_VALUE = "CanEmulator echo";

    private FakeScheduler scheduler;
    private CommandDispatcher dispatcher;
    private CanEmulatorCore core;
    private final List<CommandResult.State> states = new ArrayList();

    private void setUp(WriteMode writeMode, Long lateMillis, Integer conflictValue) {
        this.scheduler = new FakeScheduler();
        this.dispatcher = new CommandDispatcher(this.scheduler, new CommandDispatcher.Clock() {
            @Override // ru.big.town.anative.CommandDispatcher.Clock
            public final long now() {
                return CommandReadBackEmulatorTest.this.scheduler.now();
            }
        }, new Random(7), new CommandDispatcher.Listener() {
            @Override // ru.big.town.anative.CommandDispatcher.Listener
            public void onResult(CommandResult commandResult) {
                CommandReadBackEmulatorTest.this.states.add(commandResult.state());
            }
        });
        EmuConfig emuConfig = new EmuConfig();
        emuConfig.writeMode = writeMode;
        if (lateMillis != null) {
            emuConfig.lateMillis = lateMillis.longValue();
        }
        if (conflictValue != null) {
            emuConfig.conflictValue = conflictValue.intValue();
        }
        this.core = new CanEmulatorCore(emuConfig);
        this.core.setSink(new CallbackSink() {
            @Override // ru.big.town.hil.CallbackSink
            public void deliver(int i, List<Object> list) {
                if (i != TxCode.CB_VEHICLE_STATE || list.size() < 4) {
                    return;
                }
                int intValue = ((Integer) list.get(3)).intValue();
                if (intValue == 0) {
                    CommandReadBackEmulatorTest.this.dispatcher.onAck(FEATURE, EXPECTED_VALUE);
                } else {
                    CommandReadBackEmulatorTest.this.dispatcher.onMismatch(FEATURE, EXPECTED_VALUE + " value=" + intValue);
                }
            }
        });
    }

    private boolean sendTarget() {
        TxResult txResultTransact = this.core.transact(TxCode.SET_STATE, List.of(1, 0, STATE_ID, 0));
        return txResultTransact.send;
    }

    @Test public void immediateEchoConfirmsCommand() {
        setUp(WriteMode.ACK, null, null);
        assertTrue(this.dispatcher.submit(FEATURE, new CommandDispatcher.SendAction() {
            @Override // ru.big.town.anative.CommandDispatcher.SendAction
            public boolean send() {
                return CommandReadBackEmulatorTest.this.sendTarget();
            }
        }));
        CommandResult lastResult = this.dispatcher.lastResult(FEATURE);
        assertEquals(CommandResult.State.CONFIRMED, lastResult.state());
        assertEquals(EXPECTED_VALUE, lastResult.ackSource());
        assertEquals(1, lastResult.attempts());
        assertEquals(1, this.core.log().count(TxLogEntry.Dir.CLIENT_TO_SVC, TxCode.SET_STATE));
        assertTrue(this.scheduler.idle());
    }

    @Test public void silenceTimesOutAfterThreeSends() {
        setUp(WriteMode.SILENT, null, null);
        assertTrue(this.dispatcher.submit(FEATURE, new CommandDispatcher.SendAction() {
            @Override // ru.big.town.anative.CommandDispatcher.SendAction
            public boolean send() {
                return CommandReadBackEmulatorTest.this.sendTarget();
            }
        }));
        assertEquals(1, this.core.log().count(TxLogEntry.Dir.CLIENT_TO_SVC, TxCode.SET_STATE));
        this.scheduler.advance(1500L);
        this.scheduler.advance(1000L);
        this.scheduler.advance(1500L);
        this.scheduler.advance(1000L);
        this.scheduler.advance(1500L);
        CommandResult lastResult = this.dispatcher.lastResult(FEATURE);
        assertEquals(CommandResult.State.TIMEOUT, lastResult.state());
        assertEquals(3, lastResult.attempts());
        assertEquals(3, this.core.log().count(TxLogEntry.Dir.CLIENT_TO_SVC, TxCode.SET_STATE));
        assertTrue(this.scheduler.idle());
    }

    @Test public void lateEchoWithinWindowStillConfirms() {
        setUp(WriteMode.LATE, 250L, null);
        assertTrue(this.dispatcher.submit(FEATURE, new CommandDispatcher.SendAction() {
            @Override // ru.big.town.anative.CommandDispatcher.SendAction
            public boolean send() {
                return CommandReadBackEmulatorTest.this.sendTarget();
            }
        }));
        CommandResult lastResult = this.dispatcher.lastResult(FEATURE);
        assertEquals(CommandResult.State.CONFIRMED, lastResult.state());
        assertEquals(1, lastResult.attempts());
        assertTrue(this.scheduler.idle());
        assertEquals(Arrays.asList(CommandResult.State.CONFIRMED), this.states);
    }

    @Test public void contradictoryEchoFailsCommand() {
        setUp(WriteMode.CONFLICTING, null, 1);
        assertTrue(this.dispatcher.submit(FEATURE, new CommandDispatcher.SendAction() {
            @Override // ru.big.town.anative.CommandDispatcher.SendAction
            public boolean send() {
                return CommandReadBackEmulatorTest.this.sendTarget();
            }
        }));
        CommandResult lastResult = this.dispatcher.lastResult(FEATURE);
        assertEquals(CommandResult.State.FAILED, lastResult.state());
        assertTrue(String.valueOf(lastResult.ackSource()), String.valueOf(lastResult.ackSource()).contains("value=1"));
        assertTrue(this.scheduler.idle());
    }
}
