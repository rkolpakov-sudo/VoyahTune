package ru.big.town.hil.replay;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Извлекает последовательность Binder-транзакций из logcat-трассы (SPEC L112).
 *
 * <p>Парсится формат {@code -v threadtime}: необязательная дата, время, pid, tid,
 * уровень, тег, сообщение. Структурируются строки, чьё сообщение начинается с
 * {@code TX<число>} (OemVehicleStateTransport, HeadlightCanTransport,
 * LightSensorService, CanBusEventHub — все форматы, найденные в легенде Native).
 * Таймметки и pid в запись не переносятся: паритет их игнорирует (SPEC L111).</p>
 */
public final class LogcatTxParser {

    private static final Pattern LINE = Pattern.compile(
            "^(?:\\d{2}-\\d{2}\\s+)?"
                    + "\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\s+"
                    + "\\d+\\s+\\d+\\s+"
                    + "[VDIWEF]\\s+"
                    + "(.+?):\\s"
                    + "(.*)$");

    private static final Pattern TX_PREFIX = Pattern.compile("^TX(\\d+)\\b(.*)$");

    private LogcatTxParser() {
    }

    /**
     * Разбирает текст logcat-трассы; возвращает записи только для строк с TX*.
     * Не-логcat-строки и сообщения без TX-префикса игнорируются.
     */
    public static List<TxRecord> parse(Iterable<String> lines) {
        List<TxRecord> out = new ArrayList<>();
        int lineNo = 0;
        for (String line : lines) {
            lineNo++;
            String msg = messageOf(line);
            if (msg == null) {
                continue;
            }
            TxRecord rec = parseMessage(lineNo, msg);
            if (rec != null) {
                out.add(rec);
            }
        }
        return out;
    }

    /** Извлекает сообщение лога; null, если строка не в формате threadtime. */
    static String messageOf(String line) {
        String clean = line.endsWith("\r") ? line.substring(0, line.length() - 1) : line;
        Matcher m = LINE.matcher(clean);
        if (!m.matches()) {
            return null;
        }
        return m.group(2);
    }

    private static TxRecord parseMessage(int lineNo, String msg) {
        Matcher p = TX_PREFIX.matcher(msg);
        if (!p.matches()) {
            return null;
        }
        TxRecord.Op op = opOf(Integer.parseInt(p.group(1)));
        Matcher m;
        String s = msg;

        switch (op) {
            case TX58:
                if ((m = match(s, "^TX58 accepted-unconfirmed \\[([^\\]]*)\\] ([A-Za-z0-9_]+)=(-?\\d+)$")) != null) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.OK, m.group(1),
                            m.group(2), Integer.valueOf(m.group(3)), null, null, null, msg);
                }
                if ((m = match(s, "^TX58 rejected \\[([^\\]]*)\\] ([A-Za-z0-9_]+)$")) != null) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.REJECTED, m.group(1),
                            m.group(2), null, null, null, null, msg);
                }
                if ((m = match(s, "^TX58 failed \\[([^\\]]*)\\] ([A-Za-z0-9_]+)$")) != null) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.FAILED, m.group(1),
                            m.group(2), null, null, null, null, msg);
                }
                if ((m = match(s, "^TX58 ([A-Za-z0-9_]+) state=(\\d+)$")) != null) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.OK, null,
                            m.group(1), Integer.valueOf(m.group(2)), null, null, null, msg);
                }
                if ((m = match(s, "^TX58 rejected for ([A-Za-z0-9_]+)$")) != null) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.REJECTED, null,
                            m.group(1), null, null, null, null, msg);
                }
                if ((m = match(s, "^TX58 unavailable for ([A-Za-z0-9_]+)$")) != null) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.UNAVAILABLE, null,
                            m.group(1), null, null, null, null, msg);
                }
                if ((m = match(s, "^TX58 failed for ([A-Za-z0-9_]+)$")) != null) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.FAILED, null,
                            m.group(1), null, null, null, null, msg);
                }
                break;
            case TX57:
                if ((m = match(s, "^TX57 getVehicleState ([A-Za-z0-9_]+)=(-?\\d+)$")) != null) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.OK, null, m.group(1),
                            Integer.valueOf(m.group(2)), null, null, null, msg);
                }
                if ((m = match(s, "^TX57 getVehicleState rejected ([A-Za-z0-9_]+)$")) != null) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.REJECTED, null,
                            m.group(1), null, null, null, null, msg);
                }
                if ((m = match(s, "^TX57 getVehicleState failed ([A-Za-z0-9_]+)$")) != null) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.FAILED, null,
                            m.group(1), null, null, null, null, msg);
                }
                break;
            case TX77:
                if ((m = match(s, "^TX77 accepted-unconfirmed \\[([^\\]]*)\\] states=Bundle\\[\\{(.*)\\}\\]$")) != null) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.OK, m.group(1),
                            null, null, null, null, parseStates(m.group(2)), msg);
                }
                if ((m = match(s, "^TX77 rejected \\[([^\\]]*)\\]$")) != null) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.REJECTED, m.group(1),
                            null, null, null, null, null, msg);
                }
                if ((m = match(s, "^TX77 returned (\\d+) \\[([^\\]]*)\\]$")) != null) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.RETURNED, m.group(2),
                            null, null, Integer.valueOf(m.group(1)), null, null, msg);
                }
                if ((m = match(s, "^TX77 failed \\[([^\\]]*)\\]$")) != null) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.FAILED, m.group(1),
                            null, null, null, null, null, msg);
                }
                break;
            case TX6:
                if ((m = match(s, "^TX6 getGearStatus ordinal=(-?\\d+) value=(-?\\d+)$")) != null) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.OK, null, null,
                            Integer.valueOf(m.group(2)), null, Integer.valueOf(m.group(1)),
                            null, msg);
                }
                if (s.equals("TX6 getGearStatus returned null")) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.NULL_REPLY, null,
                            null, null, null, null, null, msg);
                }
                if (s.equals("TX6 getGearStatus rejected")) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.REJECTED, null,
                            null, null, null, null, null, msg);
                }
                if (s.equals("TX6 getGearStatus failed")) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.FAILED, null,
                            null, null, null, null, null, msg);
                }
                break;
            case TX9:
                if (s.equals("TX9 getFuelLevel unavailable")) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.UNAVAILABLE, null,
                            null, null, null, null, null, msg);
                }
                break;
            case TX20:
                if ((m = match(s, "^TX20 took \\d+ ms$")) != null) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.OK, null,
                            null, null, null, null, null, msg);
                }
                break;
            case TX36:
                if (s.startsWith("TX36 completion dropped")) {
                    return TxRecord.of(lineNo, op, TxRecord.Outcome.DROPPED, null,
                            null, null, null, null, null, msg);
                }
                break;
            default:
                break;
        }
        // TX-строка неизвестного формата: фиксируем, чтобы изменение формата
        // лога не прошло молча (регрессия паритета).
        return TxRecord.unsupported(lineNo, msg);
    }

    private static TxRecord.Op opOf(int num) {
        switch (num) {
            case 6:
                return TxRecord.Op.TX6;
            case 9:
                return TxRecord.Op.TX9;
            case 20:
                return TxRecord.Op.TX20;
            case 36:
                return TxRecord.Op.TX36;
            case 57:
                return TxRecord.Op.TX57;
            case 58:
                return TxRecord.Op.TX58;
            case 77:
                return TxRecord.Op.TX77;
            default:
                return TxRecord.Op.UNSUPPORTED;
        }
    }

    private static Map<String, Integer> parseStates(String body) {
        Map<String, Integer> states = new LinkedHashMap<>();
        if (body == null || body.isEmpty()) {
            return states;
        }
        for (String pair : body.split(",\\s*")) {
            int eq = pair.indexOf('=');
            if (eq <= 0) {
                continue;
            }
            String name = pair.substring(0, eq);
            try {
                states.put(name, Integer.valueOf(pair.substring(eq + 1).trim()));
            } catch (NumberFormatException ignored) {
                // нечисловое значение: пропускаем, строка сохранится в raw
            }
        }
        return states;
    }

    private static Matcher match(String s, String regex) {
        Matcher m = Pattern.compile(regex).matcher(s);
        return m.matches() ? m : null;
    }
}
