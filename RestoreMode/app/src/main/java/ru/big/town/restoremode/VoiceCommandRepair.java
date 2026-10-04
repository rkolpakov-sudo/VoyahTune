package ru.big.town.restoremode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceCommandRepair {
    private static final Map<String, String> FORMS = new LinkedHashMap();

    VoiceCommandRepair() {
    }

    static {
        forms("комфорт", "комфорта комфорту комфортом комфорте комфортный комфортного комфортном комфортную");
        forms("спорт", "спорта спорту спортом спорте спортивный спортивного спортивном спортивную");
        forms("топливо", "топлива топливу топливом топливе топливный топливного топливном топливную");
        forms("гибрид", "гибрида гибриду гибридом гибриде гибридный гибридного гибридном гибридную");
        forms("электро", "электрический электрического электрическом электрическую");
        forms("экономичный", "экономичного экономичном экономичную");
        forms("снег", "снега снегу снегом снеге снежный снежного снежном снежную");
        forms("внедорожный", "внедорожного внедорожном внедорожную внедорожье");
        forms("загородный", "загородного загородном загородную загород");
        forms("индивидуальный", "индивидуального индивидуальном индивидуальную");
        forms("рекуперация", "рекуперации рекуперацию рекуперацией");
        forms("низкая", "низкий низкую низкой низкое низкого низком");
        forms("слабая", "слабый слабую слабой слабое слабого слабом");
        forms("средняя", "средний среднюю средней среднее среднего среднем");
        forms("стандартная", "стандартный стандартную стандартной стандартное стандартного стандартном");
        forms("высокая", "высокий высокую высокой высокое высокого высоком");
        forms("сильная", "сильный сильную сильной сильное сильного сильном");
        forms("обогрев", "обогрева обогреву обогревом обогреве подогрев подогрева подогреву подогревом подогреве подогрей подогреть подогрела подогрел подогрели");
        forms("руль", "руля рулю рулем руле");
        forms("стекло", "стекла стеклу стеклом стекле");
        forms("задний", "заднее заднего заднем заднюю задней");
        forms("батарея", "батареи батарею батарее батареей");
        forms("ближний", "ближнего ближнем ближнюю");
        forms("свет", "света свету светом свете");
        forms("фары", "фара фару фарам фарами фарах");
        forms("бензобак", "бензобака бензобаку бензобаком бензобаке");
        forms("бак", "бака баку баком баке");
        forms("лючок", "лючка лючку лючком лючке люк люка люку люком люке");
        forms("зарядка", "зарядки зарядку зарядке зарядкой");
        forms("зарядный", "зарядного зарядном зарядную");
        forms("порт", "порта порту портом порте");
        forms("сервисный", "сервисного сервисном сервисную");
        forms("обслуживание", "обслуживания обслуживанию обслуживании");
        forms("подвеска", "подвески подвеску подвеске подвеской");
        forms("мойка", "мойки мойку мойке мойкой");
        forms("звук", "звука звуку звуком звуке");
        forms("пешеходы", "пешеходов пешеходам пешеходами пешеходах");
        forms("заряд", "заряда заряду зарядом заряде");
        forms("сохранение", "сохранения сохранению сохранением сохранении");
    }

    private static void forms(String str, String str2) {
        FORMS.put(str, str);
        for (String str3 : str2.split(" ")) {
            FORMS.put(str3, str);
        }
    }

    static boolean known(String str) {
        return FORMS.containsKey(str);
    }

    static List<String> variants(String str) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(str);
        for (Map.Entry<String, String> entry : FORMS.entrySet()) {
            if (entry.getValue().equals(str) && !entry.getKey().equals(str)) {
                arrayList.add(entry.getKey());
            }
        }
        return arrayList;
    }

    static String normalize(String str, boolean z) {
        int i;
        String strTrim = str.toLowerCase(Locale.ROOT).replace((char) 1105, (char) 1077).replace((char) 8722, '-').replaceAll("[^\\p{L}\\p{N}+.,%\\- ]", " ").replaceAll("(?<![0-9])[.,]|[.,](?![0-9])", " ").trim();
        String[] strArrSplit = strTrim.split("\\s+");
        if (strArrSplit.length > 32 || strTrim.length() > 256) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        int i2 = 0;
        int i3 = 0;
        while (i2 < strArrSplit.length) {
            String str2 = strArrSplit[i2];
            String str3 = FORMS.get(str2);
            int i4 = 1;
            if (str3 == null && repairable(str2)) {
                String str4 = "";
                int i5 = i2;
                int i6 = 1;
                String str5 = "";
                while (true) {
                    i = i2 + 3;
                    if (i5 >= Math.min(i, strArrSplit.length) || !repairable(strArrSplit[i5])) {
                        break;
                    }
                    str5 = str5 + strArrSplit[i5];
                    String str6 = FORMS.get(str5);
                    if (str6 != null) {
                        i6 = (i5 - i2) + 1;
                        str3 = str6;
                    }
                    i5++;
                }
                if (str3 == null && z) {
                    for (int i7 = i2; i7 < Math.min(i, strArrSplit.length) && repairable(strArrSplit[i7]); i7++) {
                        str4 = str4 + strArrSplit[i7];
                        String strOneTypo = oneTypo(str4);
                        if (strOneTypo != null) {
                            if (str3 != null) {
                                return null;
                            }
                            i6 = (i7 - i2) + 1;
                            str3 = strOneTypo;
                        }
                    }
                    if (str3 != null && (i3 = i3 + 1) > 1) {
                        return null;
                    }
                }
                i4 = i6;
            }
            if (str3 != null) {
                str2 = str3;
            }
            arrayList.add(str2);
            i2 += i4;
        }
        return String.join(" ", arrayList);
    }

    private static boolean repairable(String str) {
        return (!str.matches("[а-я]+") || str.contains("ключ") || str.contains("блок") || Arrays.asList("не", "ни", "нет", "нельзя", "отмена", "отмени", "или", "и", "потом", "затем", "если", "включи", "включить", "включите", "выключи", "выключить", "отключи", "переключи", "переключить", "переключите", "установи", "поставь", "открой", "открыть", "откройте", "закрой", "закрыть", "режим", "режима", "на", "пожалуйста", "минус", "плюс").contains(str)) ? false : true;
    }

    private static String oneTypo(String str) {
        if (str.length() < 6) {
            return null;
        }
        String value = null;
        for (Map.Entry<String, String> entry : FORMS.entrySet()) {
            if (entry.getKey().length() >= 6 && distanceOne(str, entry.getKey())) {
                if (value != null && !value.equals(entry.getValue())) {
                    return null;
                }
                value = entry.getValue();
            }
        }
        return value;
    }

    private static boolean distanceOne(String str, String str2) {
        if (Math.abs(str.length() - str2.length()) > 1) {
            return false;
        }
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        while (i < str.length() && i2 < str2.length()) {
            if (str.charAt(i) == str2.charAt(i2)) {
                i++;
                i2++;
                continue;
            }
            i3++;
            if (i3 > 1) {
                return false;
            }
            if (str.length() >= str2.length()) {
                i++;
            }
            if (str2.length() >= str.length()) {
                i2++;
            }
        }
        return (i3 + (str.length() - i)) + (str2.length() - i2) == 1;
    }
}
