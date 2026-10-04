package ru.big.town.restoremode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceCommandSequence {
    static final int MAX_SEGMENTS = 3;
    static final String REASON_CHOICE = "Неоднозначная команда";
    static final String REASON_CONFIRM = "Требует подтверждения";
    static final String REASON_NAVIGATION = "Недоступна в последовательности";
    static final String REASON_NEGATION = "Отрицание в команде";
    static final String REASON_SEAT = "Не указано место";
    static final String REASON_TOO_LONG = "Больше 3 команд в одной фразе";
    static final String REASON_UNKNOWN = "Не распознана";
    static final String REASON_WINDOW = "Не указано, какое окно";
    private static final List<String> HARD_SEPARATORS = Arrays.asList("далее", "затем", "потом");
    private static final List<String> NEGATION = Arrays.asList("не", "ни", "нет", "нельзя");
    private static final List<String> CHOICE = Arrays.asList("или", "если", "отмена", "отмени");
    private static final List<String> ON_VERBS = Arrays.asList("включи", "включить", "включите", "установи", "поставь");
    private static final List<String> OFF_VERBS = Arrays.asList("выключи", "выключить", "выключите", "отключи", "отключить", "отключите");

    static final class Segment {
        final VoiceCommandCatalog.Command command;
        final String reason;
        final String text;

        private Segment(String str, VoiceCommandCatalog.Command command, String str2) {
            this.text = str;
            this.command = command;
            this.reason = str2;
        }

        boolean accepted() {
            return this.command != null;
        }
    }

    private VoiceCommandSequence() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    static List<Segment> parse(List<VoiceCommandCatalog.Command> list, String str) {
        Segment segment;
        ArrayList arrayList = new ArrayList();
        String strNormalize = VoiceCommandCatalog.normalize(str);
        if (strNormalize.isEmpty()) {
            return arrayList;
        }
        VoiceCommandCatalog.Command commandMatch = VoiceCommandCatalog.match(list, strNormalize);
        String str2 = null;
        if (commandMatch != null) {
            arrayList.add(new Segment(strNormalize, commandMatch, str2));
            return classify(arrayList);
        }
        Iterator<String> it = splitOn(strNormalize, HARD_SEPARATORS).iterator();
        while (it.hasNext()) {
            for (String str3 : splitOnAnd(list, it.next())) {
                VoiceCommandCatalog.Command commandMatch2 = VoiceCommandCatalog.match(list, str3);
                if (commandMatch2 != null) {
                    segment = new Segment(str3, commandMatch2, null);
                } else {
                    segment = new Segment(str3, null, reason(str3));
                }
                arrayList.add(segment);
            }
        }
        return classify(arrayList);
    }

    static List<Segment> sample() {
        return parse(VoiceCommandCatalog.builtIns(), "примени настройки затем открой окно затем включи массаж");
    }

    static boolean isNavigation(String str) {
        return "system_back".equals(str) || "open_voyahtune".equals(str) || str.startsWith("app:") || str.startsWith("split:") || str.startsWith("call:");
    }

    private static List<String> splitOnAnd(List<VoiceCommandCatalog.Command> list, String str) {
        String[] strArrSplit = str.split(" ");
        ArrayList arrayList = new ArrayList();
        int i = 0;
        for (int i2 = 0; i2 < strArrSplit.length; i2++) {
            if ("и".equals(strArrSplit[i2])) {
                String strJoin = join(strArrSplit, 0, i2);
                String strJoin2 = join(strArrSplit, i2 + 1, strArrSplit.length);
                if (!strJoin.isEmpty() && !strJoin2.isEmpty() && (VoiceCommandCatalog.match(list, strJoin) != null || VoiceCommandCatalog.match(list, strJoin2) != null)) {
                    arrayList.add(Integer.valueOf(i2));
                }
            }
        }
        ArrayList arrayList2 = new ArrayList();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            int iIntValue = ((Integer) it.next()).intValue();
            arrayList2.add(join(strArrSplit, i, iIntValue));
            i = iIntValue + 1;
        }
        arrayList2.add(join(strArrSplit, i, strArrSplit.length));
        arrayList2.removeIf(new Predicate() { // from class: ru.big.town.restoremode.VoiceCommandSequence$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((String) obj).isEmpty();
            }
        });
        return arrayList2;
    }

    private static List<String> splitOn(String str, List<String> list) {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        for (String str2 : str.split(" ")) {
            if (list.contains(str2)) {
                if (sb.length() > 0) {
                    arrayList.add(sb.toString());
                }
                sb.setLength(0);
            } else {
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(str2);
            }
        }
        if (sb.length() > 0) {
            arrayList.add(sb.toString());
        }
        return arrayList;
    }

    private static String join(String[] strArr, int i, int i2) {
        StringBuilder sb = new StringBuilder();
        while (i < i2) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(strArr[i]);
            i++;
        }
        return sb.toString();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static List<Segment> classify(List<Segment> list) {
        String str;
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (i < list.size()) {
            Segment segment = list.get(i);
            if (segment.command == null) {
                arrayList.add(segment);
            } else {
                VoiceCommandCatalog.Command command = null;
                if (i >= 3) {
                    str = REASON_TOO_LONG;
                } else if (segment.command.confirm) {
                    str = REASON_CONFIRM;
                } else {
                    str = (!isNavigation(segment.command.action) || i == list.size() + (-1)) ? null : REASON_NAVIGATION;
                }
                if (str != null) {
                    segment = new Segment(segment.text, command, str);
                }
                arrayList.add(segment);
            }
            i++;
        }
        return arrayList;
    }

    private static String reason(String str) {
        boolean zContains = false;
        boolean zContains2 = false;
        for (String str2 : str.split(" ")) {
            if (NEGATION.contains(str2)) {
                return REASON_NEGATION;
            }
            if (CHOICE.contains(str2)) {
                return REASON_CHOICE;
            }
            zContains |= ON_VERBS.contains(str2);
            zContains2 |= OFF_VERBS.contains(str2);
        }
        if (zContains && zContains2) {
            return REASON_CHOICE;
        }
        if (VoiceWindowCommands.mentionsWindow(str)) {
            return REASON_WINDOW;
        }
        return mentionsSeat(str) ? REASON_SEAT : REASON_UNKNOWN;
    }

    private static boolean mentionsSeat(String str) {
        for (String str2 : str.split(" ")) {
            if (str2.startsWith("массаж") || str2.startsWith("сидень") || str2.startsWith("сиден") || str2.startsWith("кресл") || str2.startsWith("подогрев") || str2.startsWith("обогрев") || str2.startsWith("вентиляц") || str2.startsWith("обдув") || str2.startsWith("водител") || str2.startsWith("пассажир")) {
                return true;
            }
        }
        return false;
    }
}
