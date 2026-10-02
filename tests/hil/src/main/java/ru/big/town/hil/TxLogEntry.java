package ru.big.town.hil;

import java.util.Collections;
import java.util.List;

public final class TxLogEntry {
    public enum Dir {
        CLIENT_TO_SVC,
        SVC_TO_CLIENT,
        SVC_TO_APP,
        INTERNAL
    }

    public final long tNanos;
    public final Dir dir;
    public final int code;
    public final String label;
    public final List<Object> args;
    public final String note;

    TxLogEntry(long tNanos, Dir dir, int code, String label, List<Object> args, String note) {
        this.tNanos = tNanos;
        this.dir = dir;
        this.code = code;
        this.label = label;
        this.args = Collections.unmodifiableList(args);
        this.note = note;
    }

    public String describe(double startNanos) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(java.util.Locale.ROOT, "t=%10.3fms  %-16s code=%-3d %-40s %s",
                (this.tNanos - startNanos) / 1.0e6, this.dir.name(), this.code, this.label,
                this.args.isEmpty() ? "" : this.args.toString()));
        if (this.note != null && !this.note.isEmpty()) {
            sb.append("  # ").append(this.note);
        }
        return sb.toString();
    }
}
