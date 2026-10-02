package ru.big.town.hil;

import java.util.ArrayList;
import java.util.List;

public final class TxLog {
    private final List<TxLogEntry> entries = new ArrayList<>();
    private final long startNanos = System.nanoTime();

    public long startNanos() {
        return this.startNanos;
    }

    public long nowNanos() {
        return System.nanoTime();
    }

    synchronized TxLogEntry add(TxLogEntry.Dir dir, int code, List<Object> args, String note) {
        TxLogEntry entry = new TxLogEntry(System.nanoTime(), dir, code, TxCode.label(code), new ArrayList<>(args), note);
        this.entries.add(entry);
        return entry;
    }

    public synchronized List<TxLogEntry> entries() {
        return new ArrayList<>(this.entries);
    }

    public synchronized List<String> lines() {
        List<String> out = new ArrayList<>();
        for (TxLogEntry e : this.entries) {
            out.add(e.describe(this.startNanos));
        }
        return out;
    }

    public synchronized long ageMillis(TxLogEntry entry) {
        return (entry.tNanos - this.startNanos) / 1_000_000L;
    }

    public synchronized int count(TxLogEntry.Dir dir, int code) {
        int n = 0;
        for (TxLogEntry e : this.entries) {
            if (e.dir == dir && e.code == code) {
                n++;
            }
        }
        return n;
    }

    public synchronized TxLogEntry firstClientWrite(int stateId, Integer value) {
        for (TxLogEntry e : this.entries) {
            if (e.dir == TxLogEntry.Dir.CLIENT_TO_SVC && e.code == TxCode.SET_STATE
                    && e.args.size() >= 4 && asInt(e.args, 2) == stateId
                    && (value == null || asInt(e.args, 3) == value)) {
                return e;
            }
        }
        return null;
    }

    public synchronized TxLogEntry firstClient(int code, long sinceNanos) {
        for (TxLogEntry e : this.entries) {
            if (e.tNanos >= sinceNanos && e.dir == TxLogEntry.Dir.CLIENT_TO_SVC && e.code == code) {
                return e;
            }
        }
        return null;
    }

    public synchronized TxLogEntry firstClientWrite(int stateId, Integer value, long sinceNanos) {
        for (TxLogEntry e : this.entries) {
            if (e.tNanos >= sinceNanos && e.dir == TxLogEntry.Dir.CLIENT_TO_SVC && e.code == TxCode.SET_STATE
                    && e.args.size() >= 4 && asInt(e.args, 2) == stateId
                    && (value == null || asInt(e.args, 3) == value)) {
                return e;
            }
        }
        return null;
    }

    public synchronized TxLogEntry firstCallback(int cbCode, long sinceNanos) {
        for (TxLogEntry e : this.entries) {
            if (e.tNanos >= sinceNanos && e.dir == TxLogEntry.Dir.SVC_TO_APP && e.code == cbCode) {
                return e;
            }
        }
        return null;
    }

    public synchronized TxLogEntry firstCallbackState(int stateId, Integer value, long sinceNanos) {
        for (TxLogEntry e : this.entries) {
            if (e.tNanos >= sinceNanos && e.dir == TxLogEntry.Dir.SVC_TO_APP && e.code == TxCode.CB_VEHICLE_STATE
                    && e.args.size() >= 4 && asInt(e.args, 2) == stateId
                    && (value == null || asInt(e.args, 3) == value)) {
                return e;
            }
        }
        return null;
    }

    private static int asInt(List<Object> args, int index) {
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
