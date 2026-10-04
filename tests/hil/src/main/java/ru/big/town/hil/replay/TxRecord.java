package ru.big.town.hil.replay;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Распознанная Binder-транзакция, извлечённая из logcat-трассы (SPEC L112).
 * Определяется строго из текста строки лога; таймметки и pid не сохраняются.
 */
public final class TxRecord {

    public enum Op {
        TX6, TX9, TX20, TX36, TX57, TX58, TX77, UNSUPPORTED
    }

    public enum Outcome {
        OK,            // транзакция принята (accepted / state=N / ordinal/value / took)
        REJECTED,      // rejected
        FAILED,        // failed
        RETURNED,      // вернула ненулевой код (TX77 returned N)
        UNAVAILABLE,   // unavailable (TX9/TX58)
        NULL_REPLY,    // вернула null (TX6)
        DROPPED        // completion dropped (TX36)
    }

    public final int lineNo;
    public final Op op;
    public final Outcome outcome;
    public final String label;
    public final String key;
    public final Integer value;
    public final Integer returnCode;
    public final Integer gearOrdinal;
    public final Map<String, Integer> states;
    public final String raw;

    private TxRecord(int lineNo, Op op, Outcome outcome, String label, String key,
                     Integer value, Integer returnCode, Integer gearOrdinal,
                     Map<String, Integer> states, String raw) {
        this.lineNo = lineNo;
        this.op = op;
        this.outcome = outcome;
        this.label = label;
        this.key = key;
        this.value = value;
        this.returnCode = returnCode;
        this.gearOrdinal = gearOrdinal;
        this.states = states == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<>(states));
        this.raw = raw;
    }

    static TxRecord of(int lineNo, Op op, Outcome outcome, String label, String key,
                       Integer value, Integer returnCode, Integer gearOrdinal,
                       Map<String, Integer> states, String raw) {
        return new TxRecord(lineNo, op, outcome, label, key, value, returnCode,
                gearOrdinal, states, raw);
    }

    static TxRecord unsupported(int lineNo, String raw) {
        return new TxRecord(lineNo, Op.UNSUPPORTED, Outcome.FAILED, null, null,
                null, null, null, null, raw);
    }
}
