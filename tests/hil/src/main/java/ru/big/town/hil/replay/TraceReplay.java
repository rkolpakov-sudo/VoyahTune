package ru.big.town.hil.replay;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.qinggan.canbus.VehicleState;

import ru.big.town.hil.CanEmulatorCore;
import ru.big.town.hil.TxCode;

/**
 * Прогон извлечённых из logcat транзакций через can-emulator и сверка
 * результата с эталоном (SPEC L112).
 *
 * <p>Транскрипт канонический: без таймметок и pid (паритет их игнорирует,
 * SPEC L111). Эталон — закоммиченный файл; изменение формата/последовательности
 * логов Native меняет транскрипт и валит регрессию в CI.</p>
 */
public final class TraceReplay {

    private TraceReplay() {
    }

    /** Полный прогон: parse + replay. */
    public static List<String> run(String logcatText, CanEmulatorCore core) {
        List<TxRecord> records = LogcatTxParser.parse(splitLines(logcatText));
        return transcript(records, core);
    }

    /** Канонический транскрипт: каждая запись → одна строка результата. */
    public static List<String> transcript(List<TxRecord> records, CanEmulatorCore core) {
        List<String> out = new ArrayList<>();
        for (TxRecord r : records) {
            out.add(one(r, core));
        }
        return out;
    }

    private static String one(TxRecord r, CanEmulatorCore core) {
        switch (r.op) {
            case TX58:
                return tx58(r, core);
            case TX57:
                return tx57(r, core);
            case TX77:
                return tx77(r, core);
            case TX6:
                return tx6(r, core);
            case TX9:
                return r.outcome == TxRecord.Outcome.UNAVAILABLE
                        ? "TX9 unavailable"
                        : "TX9 " + lower(r.outcome);
            case TX20:
                return "TX20 completed";
            case TX36:
                return r.outcome == TxRecord.Outcome.DROPPED
                        ? "TX36 completion-dropped"
                        : "TX36 " + lower(r.outcome);
            default:
                return "UNSUPPORTED " + r.raw;
        }
    }

    private static String tx58(TxRecord r, CanEmulatorCore core) {
        switch (r.outcome) {
            case OK:
                Integer id = idOf(r.key);
                if (id == null) {
                    return "TX58 " + r.key + "=" + r.value + " unsupported-state";
                }
                core.transact(TxCode.SET_STATE, List.of(1, 0, id, r.value));
                return "TX58 " + r.key + "=" + r.value + " accepted emu=" + cache(core, id);
            case REJECTED:
                return "TX58 " + r.key + " rejected";
            case FAILED:
                return "TX58 " + r.key + " failed";
            case UNAVAILABLE:
                return "TX58 " + r.key + " unavailable";
            default:
                return "TX58 " + nullSafe(r.key) + " " + lower(r.outcome);
        }
    }

    private static String tx57(TxRecord r, CanEmulatorCore core) {
        switch (r.outcome) {
            case OK:
                Integer id = idOf(r.key);
                if (id == null) {
                    return "TX57 " + r.key + " trace=" + r.value + " emu=unsupported-state";
                }
                Object reply = core.transact(TxCode.GET_STATE, List.of(1, 0, id)).replyArgs.get(0);
                return "TX57 " + r.key + " trace=" + r.value + " emu=" + reply;
            case REJECTED:
                return "TX57 " + r.key + " rejected";
            case FAILED:
                return "TX57 " + r.key + " failed";
            default:
                return "TX57 " + nullSafe(r.key) + " " + lower(r.outcome);
        }
    }

    private static String tx77(TxRecord r, CanEmulatorCore core) {
        switch (r.outcome) {
            case OK:
                Map<String, Integer> states = r.states;
                if (hasUnknownState(states)) {
                    return "TX77 accepted " + render(states) + " unsupported-state";
                }
                Map<String, Integer> byName = new LinkedHashMap<>(states);
                core.transact(TxCode.SET_BUNDLE, List.of(1, 0, byName));
                StringBuilder emu = new StringBuilder("{");
                boolean first = true;
                for (Map.Entry<String, Integer> e : states.entrySet()) {
                    if (!first) {
                        emu.append(", ");
                    }
                    first = false;
                    Integer v = core.cacheValue(VehicleState.idOf(e.getKey()));
                    emu.append(e.getKey()).append('=').append(v == null ? "absent" : v);
                }
                emu.append('}');
                return "TX77 accepted " + render(states) + " emu=" + emu;
            case REJECTED:
                return "TX77 rejected";
            case RETURNED:
                return "TX77 returned " + r.returnCode;
            case FAILED:
                return "TX77 failed";
            default:
                return "TX77 " + lower(r.outcome);
        }
    }

    private static String tx6(TxRecord r, CanEmulatorCore core) {
        switch (r.outcome) {
            case OK:
                List<Object> reply = core.transact(TxCode.GEAR_STATUS, new ArrayList<>()).replyArgs;
                return "TX6 trace=" + r.gearOrdinal + "/" + r.value
                        + " emu=" + reply.get(1) + "/" + reply.get(2);
            case NULL_REPLY:
                return "TX6 null-reply";
            case REJECTED:
                return "TX6 rejected";
            case FAILED:
                return "TX6 failed";
            default:
                return "TX6 " + lower(r.outcome);
        }
    }

    /** Сверка транскрипта с эталоном; пустой список = паритет. */
    public static List<String> compare(List<String> expected, List<String> actual) {
        List<String> out = new ArrayList<>();
        int n = Math.max(expected.size(), actual.size());
        for (int i = 0; i < n; i++) {
            String e = i < expected.size() ? expected.get(i) : "<missing>";
            String a = i < actual.size() ? actual.get(i) : "<missing>";
            if (!e.equals(a)) {
                out.add("line " + (i + 1) + ": expected <" + e + "> actual <" + a + ">");
            }
        }
        return out;
    }

    private static boolean hasUnknownState(Map<String, Integer> states) {
        for (String name : states.keySet()) {
            if (idOf(name) == null) {
                return true;
            }
        }
        return false;
    }

    private static Integer idOf(String name) {
        if (name == null) {
            return null;
        }
        try {
            return VehicleState.idOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String cache(CanEmulatorCore core, int id) {
        Integer v = core.cacheValue(id);
        return v == null ? "absent" : v.toString();
    }

    private static String render(Map<String, Integer> states) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Integer> e : states.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(e.getKey()).append('=').append(e.getValue());
        }
        return sb.append('}').toString();
    }

    private static String lower(TxRecord.Outcome o) {
        return o.name().replace('_', '-').toLowerCase(java.util.Locale.ROOT);
    }

    private static String nullSafe(String s) {
        return s == null ? "?" : s;
    }

    private static List<String> splitLines(String text) {
        List<String> lines = new ArrayList<>();
        for (String line : text.split("\n", -1)) {
            lines.add(line);
        }
        if (!lines.isEmpty() && lines.get(lines.size() - 1).isEmpty()) {
            lines.remove(lines.size() - 1);
        }
        return lines;
    }
}
