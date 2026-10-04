package ru.big.town.hil;

import com.qinggan.canbus.VehicleState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CanEmulatorCore {
    private final EmuConfig config;
    private final TxLog log = new TxLog();
    private final Map<Integer, Integer> cache = new HashMap<>();
    private final Set<Object> callbacks = Collections.newSetFromMap(new IdentityHashMap<>());
    private CallbackSink sink = CallbackSink.NOOP;
    private int doorValue;
    private int gearOrdinal;
    private int gearValue;
    private int speed;
    private int fuelPercent;
    private float fuelLiters;

    public CanEmulatorCore() {
        this(new EmuConfig());
    }

    public CanEmulatorCore(EmuConfig config) {
        this.config = config;
        this.doorValue = config.doorValue;
        this.gearOrdinal = config.gearOrdinal;
        this.gearValue = config.gearValue;
        this.speed = config.speed;
        this.fuelPercent = config.fuelPercent;
        this.fuelLiters = config.fuelLiters;
    }

    public synchronized TxResult transact(int code, List<Object> args) {
        this.log.add(TxLogEntry.Dir.CLIENT_TO_SVC, code, args, null);
        TxResult result;
        List<Runnable> after = new ArrayList<>();
        try {
            switch (code) {
                case TxCode.GET_STATE:
                    result = TxResult.reply(readCache(argInt(args, 2)));
                    break;
                case TxCode.SET_STATE: {
                    if (args.size() < 4) {
                        result = TxResult.reject();
                        break;
                    }
                    int id = argInt(args, 2);
                    int value = argInt(args, 3);
                    result = TxResult.reply();
                    Runnable push = appWrite(id, value, "TX58");
                    if (push != null) {
                        after.add(push);
                    }
                    break;
                }
                case TxCode.SET_BUNDLE: {
                    if (args.size() < 3 || !(args.get(2) instanceof Map)) {
                        result = TxResult.reject();
                        break;
                    }
                    result = onBundle((Map<?, ?>) args.get(2), after);
                    break;
                }
                case TxCode.ADD_CALLBACK:
                    if (args.isEmpty()) {
                        result = TxResult.reject();
                    } else {
                        this.callbacks.add(args.get(0));
                        result = TxResult.reply(1);
                    }
                    break;
                case TxCode.REMOVE_CALLBACK:
                    if (args.isEmpty()) {
                        result = TxResult.reject();
                    } else {
                        this.callbacks.remove(args.get(0));
                        result = TxResult.reply(1);
                    }
                    break;
                case TxCode.DOOR_STATUS:
                    result = TxResult.reply(1, 0, this.doorValue);
                    break;
                case TxCode.GEAR_STATUS:
                    result = TxResult.reply(1, this.gearOrdinal, this.gearValue);
                    break;
                case TxCode.FUEL_LEVEL:
                    result = TxResult.reply(1, this.fuelPercent, 0, this.fuelLiters, 0, 0, 0, 0);
                    break;
                case TxCode.VEHICLE_SPEED:
                    result = TxResult.reply(this.speed);
                    break;
                case TxCode.SNAPSHOT_QUERY:
                    result = TxResult.reply();
                    after.add(this::pushSnapshot);
                    break;
                default:
                    this.log.add(TxLogEntry.Dir.INTERNAL, code, args, "unsupported transaction");
                    result = TxResult.reply();
                    break;
            }
        } catch (RuntimeException e) {
            this.log.add(TxLogEntry.Dir.INTERNAL, code, args, "malformed: " + e);
            return TxResult.reject();
        }
        this.log.add(TxLogEntry.Dir.SVC_TO_CLIENT, code, result.replyArgs, null);
        for (Runnable r : after) {
            r.run();
        }
        return result;
    }

    private TxResult onBundle(Map<?, ?> bundle, List<Runnable> after) {
        Map<Integer, Integer> resolved = new HashMap<>();
        for (Map.Entry<?, ?> e : bundle.entrySet()) {
            if (!(e.getKey() instanceof String) || !(e.getValue() instanceof Integer)) {
                this.log.add(TxLogEntry.Dir.INTERNAL, TxCode.SET_BUNDLE, new ArrayList<>(bundle.entrySet()),
                        "bundle has non-string/int entries");
                return TxResult.reply(1);
            }
            String name = (String) e.getKey();
            int value = (Integer) e.getValue();
            int id;
            try {
                id = VehicleState.idOf(name);
            } catch (IllegalArgumentException ex) {
                this.log.add(TxLogEntry.Dir.INTERNAL, TxCode.SET_BUNDLE, new ArrayList<>(bundle.entrySet()),
                        "unknown state name " + name);
                return TxResult.reply(1);
            }
            resolved.put(id, value);
        }
        for (Map.Entry<Integer, Integer> e : resolved.entrySet()) {
            Runnable push = appWrite(e.getKey(), e.getValue(), "TX77");
            if (push != null) {
                after.add(push);
            }
        }
        return TxResult.reply(0);
    }

    private Runnable appWrite(int id, int value, String src) {
        switch (this.config.writeMode) {
            case SILENT:
                this.log.add(TxLogEntry.Dir.INTERNAL, TxCode.SET_STATE, List.of(id, value),
                        src + " suppressed (SILENT)");
                return null;
            case LATE:
                sleep(this.config.lateMillis);
                cachePut(id, value);
                return () -> push(TxCode.CB_VEHICLE_STATE, List.of(1, 0, id, value), src + " echo (LATE)");
            case CONFLICTING: {
                int reported = this.config.conflictValue;
                cachePut(id, reported);
                return () -> push(TxCode.CB_VEHICLE_STATE, List.of(1, 0, id, reported),
                        src + " echo (CONFLICTING, requested=" + value + ")");
            }
            case ACK:
            default:
                cachePut(id, value);
                return () -> push(TxCode.CB_VEHICLE_STATE, List.of(1, 0, id, value), src + " echo");
        }
    }

    private void pushSnapshot() {
        Map<Integer, Integer> copy;
        synchronized (this) {
            copy = new HashMap<>(this.cache);
        }
        for (Map.Entry<Integer, Integer> e : copy.entrySet()) {
            push(TxCode.CB_VEHICLE_STATE, List.of(1, 0, e.getKey(), e.getValue()), "TX20 snapshot");
        }
    }

    public synchronized void setDoor(int value) {
        this.doorValue = value;
        push(TxCode.CB_DOOR, List.of(1, 0, value), "door event");
    }

    public synchronized void setGear(int ordinal, int value) {
        this.gearOrdinal = ordinal;
        this.gearValue = value;
        push(TxCode.CB_GEAR, List.of(1, 0, value), "gear event");
    }

    public synchronized void injectState(int id, int value) {
        cachePut(id, value);
        push(TxCode.CB_VEHICLE_STATE, List.of(1, 0, id, value), "inject");
    }

    public synchronized void setSpeed(int value) {
        this.speed = value;
        this.log.add(TxLogEntry.Dir.INTERNAL, -1, List.of(value), "set-speed");
    }

    public synchronized void pushRaw(int cbCode, List<Object> args) {
        push(cbCode, args, "raw");
    }

    public synchronized void setWriteMode(WriteMode mode) {
        this.config.writeMode = mode;
        this.log.add(TxLogEntry.Dir.INTERNAL, -1, List.of(mode.name()), "set-write-mode");
    }

    private void push(int cbCode, List<Object> args, String note) {
        this.log.add(TxLogEntry.Dir.SVC_TO_APP, cbCode, args, note);
        this.sink.deliver(cbCode, args);
    }

    private int readCache(int id) {
        Integer v = this.cache.get(id);
        return v == null ? this.config.missingValue : v;
    }

    private void cachePut(int id, int value) {
        this.cache.put(id, value);
    }

    public synchronized void setSink(CallbackSink sink) {
        this.sink = sink == null ? CallbackSink.NOOP : sink;
    }

    public synchronized Integer cacheValue(int id) {
        return this.cache.get(id);
    }

    public synchronized Map<Integer, Integer> snapshotCache() {
        return Collections.unmodifiableMap(new HashMap<>(this.cache));
    }

    public synchronized int registeredCallbacks() {
        return this.callbacks.size();
    }

    public synchronized int doorValue() {
        return this.doorValue;
    }

    public synchronized int gearValue() {
        return this.gearValue;
    }

    public synchronized int speed() {
        return this.speed;
    }

    public EmuConfig config() {
        return this.config;
    }

    public TxLog log() {
        return this.log;
    }

    private static void sleep(long millis) {
        if (millis <= 0) {
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted during LATE delay", e);
        }
    }

    private static int argInt(List<Object> args, int index) {
        Object v = args.get(index);
        if (v instanceof Integer) {
            return (Integer) v;
        }
        if (v instanceof Long) {
            return ((Long) v).intValue();
        }
        throw new IllegalArgumentException("arg " + index + " is not an integer: " + v);
    }
}
