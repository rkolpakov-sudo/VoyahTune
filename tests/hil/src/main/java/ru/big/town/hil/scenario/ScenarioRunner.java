package ru.big.town.hil.scenario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.qinggan.canbus.VehicleState;

import ru.big.town.hil.CanEmulatorCore;
import ru.big.town.hil.EmuConfig;
import ru.big.town.hil.TxCode;
import ru.big.town.hil.TxLog;
import ru.big.town.hil.TxLogEntry;
import ru.big.town.hil.TxResult;
import ru.big.town.hil.WriteMode;

public final class ScenarioRunner {

    public ScenarioResult run(Scenario scenario, HilHarness harness) {
        return run(scenario, harness, new CanEmulatorCore(buildConfig(scenario.config)));
    }

    public ScenarioResult run(Scenario scenario, HilHarness harness, CanEmulatorCore core) {
        core.setSink(harness::onPush);
        long wallStart = System.nanoTime();
        List<ScenarioResult.StepResult> out = new ArrayList<>();
        Map<String, Long> triggerById = new HashMap<>();
        long triggerStart = wallStart;
        boolean allOk = true;
        for (int i = 0; i < scenario.steps.size(); i++) {
            Step step = scenario.steps.get(i);
            long t0 = System.nanoTime();
            if (step.isMutating()) {
                triggerStart = t0;
            }
            String failure = null;
            try {
                if (step instanceof Step.SleepStep) {
                    harness.sleep(((Step.SleepStep) step).millis);
                    triggerById.put("sleep", t0);
                } else if (step instanceof Step.SetModeStep) {
                    Step.SetModeStep setMode = (Step.SetModeStep) step;
                    core.setWriteMode(setMode.mode);
                    triggerById.put("set_mode:" + setMode.mode.name(), t0);
                } else if (step instanceof Step.ActionStep) {
                    Step.ActionStep action = (Step.ActionStep) step;
                    failure = runAction(action, core, harness);
                    triggerById.put(action.name, t0);
                } else if (step instanceof Step.InjectStep) {
                    Step.InjectStep inject = (Step.InjectStep) step;
                    failure = runInject(inject, core);
                    if (inject.kind == Step.InjectStep.Kind.STATE) {
                        triggerById.put("inject:state:" + (inject.stateName != null ? inject.stateName : inject.stateId),
                                t0);
                    } else {
                        triggerById.put("inject:" + inject.kind.name().toLowerCase(Locale.ROOT), t0);
                    }
                } else if (step instanceof Step.ExpectTxnStep) {
                    Step.ExpectTxnStep expect = (Step.ExpectTxnStep) step;
                    failure = checkTxn(core.log(), expect,
                            resolveWindow(expect.after, triggerById, triggerStart));
                } else if (step instanceof Step.ExpectPushStep) {
                    Step.ExpectPushStep expect = (Step.ExpectPushStep) step;
                    failure = checkPush(core.log(), expect.cbCode, expect.stateId, expect.value, expect.withinMs,
                            resolveWindow(expect.after, triggerById, triggerStart), false);
                } else if (step instanceof Step.AssertNoPushStep) {
                    Step.AssertNoPushStep assertStep = (Step.AssertNoPushStep) step;
                    failure = checkPush(core.log(), assertStep.cbCode, assertStep.stateId, assertStep.value,
                            assertStep.withinMs, resolveWindow(assertStep.after, triggerById, triggerStart), true);
                } else if (step instanceof Step.AssertCacheStep) {
                    failure = checkCache((Step.AssertCacheStep) step, core);
                } else if (step instanceof Step.AssertCacheAbsentStep) {
                    failure = checkCacheAbsent((Step.AssertCacheAbsentStep) step, core);
                } else if (step instanceof Step.AssertAppStep) {
                    failure = checkApp((Step.AssertAppStep) step, harness);
                } else {
                    failure = "unknown step type " + step.getClass().getName();
                }
            } catch (RuntimeException | AssertionError e) {
                failure = e.toString();
            }
            out.add(new ScenarioResult.StepResult(i, step.describe(), failure == null, failure,
                    System.nanoTime() - t0));
            if (failure != null) {
                allOk = false;
                break;
            }
        }
        return new ScenarioResult(scenario.name, scenario.description, allOk, out, core.log().lines(),
                System.nanoTime() - wallStart);
    }

    private static String runAction(Step.ActionStep step, CanEmulatorCore core, HilHarness harness) {
        switch (step.kind) {
            case SEND: {
                int ordinal = 0;
                if (step.sendStateName != null) {
                    ordinal = VehicleState.valueOf(step.sendStateName).ordinal();
                }
                if (step.sendStateId == null || step.sendValue == null) {
                    return "send step requires state and value";
                }
                boolean accepted = harness.sendState(core, step.sendStateName, step.sendStateId, ordinal,
                        step.sendValue);
                return accepted ? null : "TX58 rejected by emulator";
            }
            case READ: {
                int ordinal = 0;
                if (step.readStateName != null) {
                    ordinal = VehicleState.valueOf(step.readStateName).ordinal();
                }
                TxResult result = core.transact(TxCode.GET_STATE, List.of(1, ordinal, step.readStateId));
                if (!result.send) {
                    return "TX57 rejected by emulator";
                }
                return null;
            }
            case TXN: {
                TxResult result = core.transact(step.txnCode, new ArrayList<>());
                if (!result.send) {
                    return "TX" + step.txnCode + " rejected by emulator";
                }
                return null;
            }
            case BUNDLE: {
                TxResult result = core.transact(TxCode.SET_BUNDLE, List.of(0, 1, step.bundle));
                if (!result.send) {
                    return "TX77 rejected by emulator";
                }
                return null;
            }
            case HARNESS:
            default:
                harness.action(step.name, core);
                return null;
        }
    }

    private static String runInject(Step.InjectStep step, CanEmulatorCore core) {
        switch (step.kind) {
            case DOOR:
                core.setDoor(step.value);
                return null;
            case GEAR:
                core.setGear(1, step.value);
                return null;
            case SPEED:
                core.setSpeed(step.value);
                return null;
            case STATE:
                core.injectState(step.stateId, step.value);
                return null;
            default:
                return "unknown inject kind";
        }
    }

    private static String checkTxn(TxLog log, Step.ExpectTxnStep step, long windowStart) {
        long windowEnd = windowStart + step.withinMs * 1_000_000L;
        TxLogEntry match = findClient(log, step.code, step.stateId, step.value, windowStart);
        if (match == null) {
            TxLogEntry stale = findClient(log, step.code, step.stateId, step.value, Long.MIN_VALUE);
            if (stale != null) {
                return "matching transaction exists only before the window (at t=" + log.ageMillis(stale)
                        + "ms from scenario start)";
            }
            return "no matching transaction: code=" + step.code
                    + (step.stateId != null ? " state_id=" + step.stateId : "")
                    + (step.value != null ? " value=" + step.value : "");
        }
        long offsetMs = (match.tNanos - windowStart) / 1_000_000L;
        if (match.tNanos > windowEnd) {
            return "matched too late: +" + offsetMs + "ms > within " + step.withinMs + "ms";
        }
        return null;
    }

    private static String checkPush(TxLog log, int cbCode, Integer stateId, Integer value, long withinMs,
            long windowStart, boolean negated) {
        long windowEnd = windowStart + withinMs * 1_000_000L;
        if (negated) {
            TxLogEntry inWindow = findPush(log, cbCode, stateId, value, windowStart, windowEnd);
            if (inWindow != null) {
                long offsetMs = (inWindow.tNanos - windowStart) / 1_000_000L;
                return "unexpected push cb=" + cbCode + " arrived at +" + offsetMs + "ms";
            }
            return null;
        }
        TxLogEntry match = findPush(log, cbCode, stateId, value, windowStart, Long.MAX_VALUE);
        if (match == null) {
            TxLogEntry stale = findPush(log, cbCode, stateId, value, Long.MIN_VALUE, windowStart - 1);
            if (stale != null) {
                return "matching push exists only before the window (at t=" + log.ageMillis(stale)
                        + "ms from scenario start)";
            }
            return "no matching push: cb=" + cbCode
                    + (stateId != null ? " state_id=" + stateId : "")
                    + (value != null ? " value=" + value : "")
                    + " within " + withinMs + "ms";
        }
        long offsetMs = (match.tNanos - windowStart) / 1_000_000L;
        if (match.tNanos > windowEnd) {
            return "matched too late: +" + offsetMs + "ms > within " + withinMs + "ms";
        }
        return null;
    }

    private static TxLogEntry findClient(TxLog log, int code, Integer stateId, Integer value, long since) {
        for (TxLogEntry e : log.entries()) {
            if (e.tNanos < since || e.dir != TxLogEntry.Dir.CLIENT_TO_SVC || e.code != code) {
                continue;
            }
            if (stateId == null) {
                return e;
            }
            if (code == TxCode.SET_STATE) {
                if (e.args.size() >= 4 && asInt(e.args, 2) == stateId
                        && (value == null || asInt(e.args, 3) == value)) {
                    return e;
                }
            } else if (code == TxCode.GET_STATE) {
                if (e.args.size() >= 3 && asInt(e.args, 2) == stateId) {
                    return e;
                }
            } else {
                return e;
            }
        }
        return null;
    }

    private static TxLogEntry findPush(TxLog log, int cbCode, Integer stateId, Integer value, long since,
            long until) {
        for (TxLogEntry e : log.entries()) {
            if (e.tNanos < since || e.tNanos > until || e.dir != TxLogEntry.Dir.SVC_TO_APP || e.code != cbCode) {
                continue;
            }
            if (stateId == null && value == null) {
                return e;
            }
            if (cbCode == TxCode.CB_VEHICLE_STATE) {
                if (e.args.size() >= 4
                        && (stateId == null || asInt(e.args, 2) == stateId)
                        && (value == null || asInt(e.args, 3) == value)) {
                    return e;
                }
            } else if (cbCode == TxCode.CB_DOOR || cbCode == TxCode.CB_GEAR) {
                if (e.args.size() >= 3 && (value == null || asInt(e.args, 2) == value)) {
                    return e;
                }
            } else {
                return e;
            }
        }
        return null;
    }

    private static String checkCache(Step.AssertCacheStep step, CanEmulatorCore core) {
        Integer actual = core.cacheValue(step.stateId);
        if (actual == null) {
            return "emulator cache has no value for state_id=" + step.stateId
                    + (step.stateName != null ? " (" + step.stateName + ")" : "");
        }
        if (actual.longValue() != step.expected) {
            return "expected " + step.expected + " but cache has " + actual;
        }
        return null;
    }

    private static String checkCacheAbsent(Step.AssertCacheAbsentStep step, CanEmulatorCore core) {
        Integer actual = core.cacheValue(step.stateId);
        if (actual != null) {
            return "cache unexpectedly holds " + actual + " for state_id=" + step.stateId
                    + (step.stateName != null ? " (" + step.stateName + ")" : "");
        }
        return null;
    }

    private static String checkApp(Step.AssertAppStep step, HilHarness harness) {
        Object actual = harness.appState(step.name);
        if (actual == null) {
            return "app state '" + step.name + "' is unavailable";
        }
        String expected = String.valueOf(step.expected);
        if (!expected.equals(String.valueOf(actual))) {
            return "expected " + step.name + "=" + expected + " but app reports " + actual;
        }
        return null;
    }

    private static long resolveWindow(String after, Map<String, Long> triggerById, long defaultStart) {
        if (after == null) {
            return defaultStart;
        }
        Long start = triggerById.get(after);
        if (start == null) {
            throw new IllegalStateException("window step did not run: after=" + after);
        }
        return start;
    }

    public static EmuConfig buildConfig(Map<String, Object> config) {
        EmuConfig emu = new EmuConfig();
        for (Map.Entry<String, Object> e : config.entrySet()) {
            String key = e.getKey();
            Object raw = e.getValue();
            switch (key) {
                case "write_mode":
                    emu.writeMode = WriteMode.valueOf(String.valueOf(raw).toUpperCase(Locale.ROOT));
                    break;
                case "late_ms":
                    emu.lateMillis = ((Number) raw).longValue();
                    break;
                case "conflict_value":
                    emu.conflictValue = ((Number) raw).intValue();
                    break;
                case "missing_value":
                    emu.missingValue = ((Number) raw).intValue();
                    break;
                case "door":
                    emu.doorValue = ((Number) raw).intValue();
                    break;
                case "gear_ordinal":
                    emu.gearOrdinal = ((Number) raw).intValue();
                    break;
                case "gear_value":
                    emu.gearValue = ((Number) raw).intValue();
                    break;
                case "speed":
                    emu.speed = ((Number) raw).intValue();
                    break;
                case "fuel_percent":
                    emu.fuelPercent = ((Number) raw).intValue();
                    break;
                case "fuel_liters":
                    emu.fuelLiters = ((Number) raw).floatValue();
                    break;
                default:
                    throw new IllegalStateException("unhandled config key " + key);
            }
        }
        return emu;
    }

    private static int asInt(List<Object> args, int index) {
        Object v = args.get(index);
        if (v instanceof Integer) {
            return (Integer) v;
        }
        if (v instanceof Long) {
            return ((Long) v).intValue();
        }
        throw new IllegalStateException("arg " + index + " is not an integer: " + v);
    }
}
