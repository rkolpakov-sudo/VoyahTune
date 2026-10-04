package ru.big.town.hil.scenario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ru.big.town.hil.WriteMode;

public abstract class Step {
    public abstract String describe();

    public boolean isMutating() {
        return false;
    }

    public static final class SleepStep extends Step {
        public final long millis;

        public SleepStep(long millis) {
            this.millis = millis;
        }

        @Override
        public String describe() {
            return "sleep " + this.millis + "ms";
        }

        @Override
        public boolean isMutating() {
            return true;
        }
    }

    public static final class SetModeStep extends Step {
        public final WriteMode mode;

        public SetModeStep(WriteMode mode) {
            this.mode = mode;
        }

        @Override
        public String describe() {
            return "set_mode " + this.mode;
        }

        @Override
        public boolean isMutating() {
            return true;
        }
    }

    public static final class ActionStep extends Step {
        public enum Kind {
            HARNESS, SEND, READ, TXN, BUNDLE
        }

        public final String name;
        public final Kind kind;
        public final Integer sendStateId;
        public final Integer sendValue;
        public final String sendStateName;
        public final Integer readStateId;
        public final String readStateName;
        public final Integer txnCode;
        public final Map<String, Integer> bundle;

        public ActionStep(String name) {
            this(name, Kind.HARNESS, null, null, null, null, null, null, null);
        }

        private ActionStep(String name, Kind kind, Integer sendStateId, Integer sendValue, String sendStateName,
                Integer readStateId, String readStateName, Integer txnCode, Map<String, Integer> bundle) {
            this.name = name;
            this.kind = kind;
            this.sendStateId = sendStateId;
            this.sendValue = sendValue;
            this.sendStateName = sendStateName;
            this.readStateId = readStateId;
            this.readStateName = readStateName;
            this.txnCode = txnCode;
            this.bundle = bundle;
        }

        public static ActionStep harness(String name) {
            return new ActionStep(name);
        }

        public static ActionStep send(String name, int stateId, int value, String stateName) {
            return new ActionStep(name, Kind.SEND, stateId, value, stateName, null, null, null, null);
        }

        public static ActionStep read(String name, int stateId, String stateName) {
            return new ActionStep(name, Kind.READ, null, null, null, stateId, stateName, null, null);
        }

        public static ActionStep txn(int code, String name) {
            return new ActionStep(name, Kind.TXN, null, null, null, null, null, code, null);
        }

        public static ActionStep bundle(String name, Map<String, Integer> bundle) {
            return new ActionStep(name, Kind.BUNDLE, null, null, null, null, null, null, bundle);
        }

        @Override
        public String describe() {
            switch (this.kind) {
                case SEND:
                    return "action " + this.name + " (TX58 " + this.sendStateName + "=" + this.sendValue + ")";
                case READ:
                    return "action " + this.name + " (TX57 " + this.readStateName + ")";
                case TXN:
                    return "action " + this.name + " (TX" + this.txnCode + ")";
                case BUNDLE:
                    return "action " + this.name + " (TX77 bundle " + this.bundle + ")";
                case HARNESS:
                default:
                    return "action " + this.name;
            }
        }

        @Override
        public boolean isMutating() {
            return true;
        }
    }

    public static final class InjectStep extends Step {
        public enum Kind {
            DOOR, GEAR, SPEED, STATE
        }

        public final Kind kind;
        public final int value;
        public final Integer stateId;
        public final String stateName;

        private InjectStep(Kind kind, int value, Integer stateId, String stateName) {
            this.kind = kind;
            this.value = value;
            this.stateId = stateId;
            this.stateName = stateName;
        }

        public static InjectStep door(int value) {
            return new InjectStep(Kind.DOOR, value, null, null);
        }

        public static InjectStep gear(int value) {
            return new InjectStep(Kind.GEAR, value, null, null);
        }

        public static InjectStep speed(int value) {
            return new InjectStep(Kind.SPEED, value, null, null);
        }

        public static InjectStep state(int id, int value, String name) {
            return new InjectStep(Kind.STATE, value, id, name);
        }

        @Override
        public String describe() {
            switch (this.kind) {
                case DOOR:
                    return "inject door=" + this.value;
                case GEAR:
                    return "inject gear=" + this.value;
                case SPEED:
                    return "inject speed=" + this.value;
                case STATE:
                default:
                    return "inject state " + (this.stateName != null ? this.stateName : this.stateId) + "=" + this.value;
            }
        }

        @Override
        public boolean isMutating() {
            return true;
        }
    }

    public static final class ExpectTxnStep extends Step {
        public final int code;
        public final Integer stateId;
        public final Integer value;
        public final long withinMs;
        public final String after;

        public ExpectTxnStep(int code, Integer stateId, Integer value, long withinMs, String after) {
            this.code = code;
            this.stateId = stateId;
            this.value = value;
            this.withinMs = withinMs;
            this.after = after;
        }

        @Override
        public String describe() {
            StringBuilder sb = new StringBuilder("expect_txn code=").append(this.code);
            if (this.stateId != null) {
                sb.append(" state_id=").append(this.stateId);
            }
            if (this.value != null) {
                sb.append(" value=").append(this.value);
            }
            sb.append(" within=").append(this.withinMs).append("ms");
            if (this.after != null) {
                sb.append(" after=").append(this.after);
            }
            return sb.toString();
        }
    }

    public static final class ExpectPushStep extends Step {
        public final int cbCode;
        public final Integer stateId;
        public final Integer value;
        public final long withinMs;
        public final String after;

        public ExpectPushStep(int cbCode, Integer stateId, Integer value, long withinMs, String after) {
            this.cbCode = cbCode;
            this.stateId = stateId;
            this.value = value;
            this.withinMs = withinMs;
            this.after = after;
        }

        @Override
        public String describe() {
            StringBuilder sb = new StringBuilder("expect_push cb=").append(this.cbCode);
            if (this.stateId != null) {
                sb.append(" state_id=").append(this.stateId);
            }
            if (this.value != null) {
                sb.append(" value=").append(this.value);
            }
            sb.append(" within=").append(this.withinMs).append("ms");
            if (this.after != null) {
                sb.append(" after=").append(this.after);
            }
            return sb.toString();
        }
    }

    public static final class AssertNoPushStep extends Step {
        public final int cbCode;
        public final Integer stateId;
        public final Integer value;
        public final long withinMs;
        public final String after;

        public AssertNoPushStep(int cbCode, Integer stateId, Integer value, long withinMs, String after) {
            this.cbCode = cbCode;
            this.stateId = stateId;
            this.value = value;
            this.withinMs = withinMs;
            this.after = after;
        }

        @Override
        public String describe() {
            StringBuilder sb = new StringBuilder("assert_no_push cb=").append(this.cbCode);
            if (this.stateId != null) {
                sb.append(" state_id=").append(this.stateId);
            }
            if (this.value != null) {
                sb.append(" value=").append(this.value);
            }
            sb.append(" within=").append(this.withinMs).append("ms");
            if (this.after != null) {
                sb.append(" after=").append(this.after);
            }
            return sb.toString();
        }
    }

    public static final class AssertCacheStep extends Step {
        public final Integer stateId;
        public final String stateName;
        public final long expected;

        public AssertCacheStep(Integer stateId, String stateName, long expected) {
            this.stateId = stateId;
            this.stateName = stateName;
            this.expected = expected;
        }

        @Override
        public String describe() {
            return "assert_cache " + (this.stateName != null ? this.stateName : this.stateId)
                    + "==" + this.expected;
        }
    }

    public static final class AssertCacheAbsentStep extends Step {
        public final Integer stateId;
        public final String stateName;

        public AssertCacheAbsentStep(Integer stateId, String stateName) {
            this.stateId = stateId;
            this.stateName = stateName;
        }

        @Override
        public String describe() {
            return "assert_cache_absent " + (this.stateName != null ? this.stateName : this.stateId);
        }
    }

    public static final class AssertAppStep extends Step {
        public final String name;
        public final Object expected;

        public AssertAppStep(String name, Object expected) {
            this.name = name;
            this.expected = expected;
        }

        @Override
        public String describe() {
            return "assert_app " + this.name + "==" + this.expected;
        }
    }

    public static List<Step> unmodifiable(List<Step> steps) {
        return Collections.unmodifiableList(new ArrayList<>(steps));
    }

    public static Map<String, Integer> unmodifiableBundle(Map<String, Integer> bundle) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(bundle));
    }
}
