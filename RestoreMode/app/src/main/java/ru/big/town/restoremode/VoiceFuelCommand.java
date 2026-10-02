package ru.big.town.restoremode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import kotlin.time.DurationKt;
import kotlin.time.InstantKt;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceFuelCommand {
    static final String PREFIX = "fuel_charge:";
    private static final String[] SMALL = {"ноль", "один", "два", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять", "десять", "одиннадцать", "двенадцать", "тринадцать", "четырнадцать", "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать"};
    private static final String[] TENS = {"", "", "двадцать", "тридцать", "сорок", "пятьдесят", "шестьдесят", "семьдесят", "восемьдесят", "девяносто"};
    private static final String[] HUNDREDS = {"", "сто", "двести", "триста", "четыреста", "пятьсот", "шестьсот", "семьсот", "восемьсот", "девятьсот"};
    private static final List<String> FILLER = Arrays.asList("включи", "включить", "включите", "установи", "поставь", "режим", "режима", "на", "пожалуйста", "переключи", "переключите", "переключить");

    VoiceFuelCommand() {
    }

    static VoiceCommandCatalog.Command match(String str) {
        String strTrim;
        if (str == null) {
            strTrim = "";
        } else {
            strTrim = str.toLowerCase(Locale.ROOT).replace((char) 1105, (char) 1077).replace((char) 8722, '-').replaceAll("[^\\p{L}\\p{N}+.,%\\- ]", " ").replaceAll("(?<![0-9])[.,]|[.,](?![0-9])", " ").trim();
        }
        ArrayList arrayList = new ArrayList();
        int i = 0;
        for (String str2 : strTrim.split("\\s+")) {
            if (str2.equals("топливо")) {
                i++;
            } else if (!str2.isEmpty() && !FILLER.contains(str2)) {
                arrayList.add(str2);
            }
        }
        if (i != 1 || arrayList.isEmpty()) {
            return null;
        }
        String str3 = (String) arrayList.get(arrayList.size() - 1);
        if (Arrays.asList("процент", "процента", "процентов", "%").contains(str3)) {
            arrayList.remove(arrayList.size() - 1);
        } else if (str3.endsWith("%")) {
            arrayList.set(arrayList.size() - 1, str3.substring(0, str3.length() - 1));
        }
        BigDecimal number = parseNumber(arrayList);
        if (number == null) {
            return null;
        }
        return command(number.max(BigDecimal.valueOf(25L)).min(BigDecimal.valueOf(80L)).divide(BigDecimal.valueOf(5L), 0, RoundingMode.HALF_UP).intValue() * 5);
    }

    static VoiceCommandCatalog.Command command(int i) {
        int i2 = i % 10;
        String str = TENS[i / 10] + (i2 == 0 ? "" : " " + SMALL[i2]);
        return new VoiceCommandCatalog.Command(PREFIX + i, "Топливо: поддерживать " + i + "% (SREV)", false, "топливо " + str, "топливо " + i, "включи топливо " + str + " процентов");
    }

    private static BigDecimal parseNumber(List<String> list) {
        int i;
        Integer group;
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() == 1 && list.get(0).matches("[+-]?[0-9]+([.,][0-9]+)?")) {
            return new BigDecimal(list.get(0).replace(',', '.'));
        }
        boolean zEquals = list.get(0).equals("минус");
        if (zEquals || list.get(0).equals("плюс")) {
            list = list.subList(1, list.size());
        }
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() == 1 && list.get(0).matches("[0-9]+([.,][0-9]+)?")) {
            BigDecimal bigDecimal = new BigDecimal(list.get(0).replace(',', '.'));
            return zEquals ? bigDecimal.negate() : bigDecimal;
        }
        long jIntValue = 0;
        int i2 = Integer.MAX_VALUE;
        int i3 = 0;
        for (int i4 = 0; i4 < list.size(); i4++) {
            String str = list.get(i4);
            if (Arrays.asList("тысяча", "тысячи", "тысяч").contains(str)) {
                i = 1000;
            } else if (Arrays.asList("миллион", "миллиона", "миллионов").contains(str)) {
                i = DurationKt.NANOS_IN_MILLIS;
            } else {
                i = Arrays.asList("миллиард", "миллиарда", "миллиардов").contains(str) ? InstantKt.NANOS_PER_SECOND : 0;
            }
            if (i != 0) {
                if (i >= i2) {
                    return null;
                }
                if (i4 == i3) {
                    group = i3 == 0 ? 1 : null;
                } else {
                    group = parseGroup(list.subList(i3, i4));
                }
                if (group == null || group.intValue() == 0) {
                    return null;
                }
                jIntValue += ((long) group.intValue()) * ((long) i);
                i3 = i4 + 1;
                i2 = i;
            }
        }
        if (i3 < list.size()) {
            Integer group2 = parseGroup(list.subList(i3, list.size()));
            if (group2 == null || (i3 > 0 && group2.intValue() == 0)) {
                return null;
            }
            jIntValue += (long) group2.intValue();
        }
        if (zEquals) {
            jIntValue = -jIntValue;
        }
        return BigDecimal.valueOf(jIntValue);
    }

    private static Integer parseGroup(List<String> list) {
        int i;
        int i2 = 0;
        int iIndexOf = Arrays.asList(HUNDREDS).indexOf(list.get(0));
        if (iIndexOf > 0) {
            i = iIndexOf * 100;
            i2 = 1;
        } else {
            i = 0;
        }
        if (i2 < list.size()) {
            int iIndexOf2 = Arrays.asList(TENS).indexOf(list.get(i2));
            if (iIndexOf2 >= 2) {
                i += iIndexOf2 * 10;
                int i3 = i2 + 1;
                if (i3 < list.size()) {
                    int iSmall = small(list.get(i3));
                    if (iSmall < 1 || iSmall > 9) {
                        return null;
                    }
                    i += iSmall;
                    i2 += 2;
                } else {
                    i2 = i3;
                }
            } else {
                int iSmall2 = small(list.get(i2));
                if (iSmall2 < 0 || (i > 0 && iSmall2 == 0)) {
                    return null;
                }
                i += iSmall2;
                i2++;
            }
        }
        if (i2 == list.size()) {
            return Integer.valueOf(i);
        }
        return null;
    }

    private static int small(String str) {
        if (str.equals("нуль")) {
            return 0;
        }
        if (str.equals("одна")) {
            return 1;
        }
        if (str.equals("две")) {
            return 2;
        }
        return Arrays.asList(SMALL).indexOf(str);
    }
}
