package ru.big.town.restoremode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlinx.coroutines.DebugKt;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceFuzzyMatcher {
    private static final int NONE = 1000;
    private static final Set<String> REJECT = new HashSet(Arrays.asList("не", "ни", "нет", "нельзя", "отмена", "отмени", "или", "и", "потом", "затем", "если"));
    private static final Set<String> FILLER = new HashSet(Arrays.asList("режим", "режима", "на", "пожалуйста"));
    private static final Set<String> FIXED = new HashSet(Arrays.asList("прогрей", "прогреть", "оставь", "поднять", "подними", "поднимите", "минус", "плюс"));

    VoiceFuzzyMatcher() {
    }

    static final class Phrase {
        final List<List<String>> forms = new ArrayList();
        final String intent;
        final int letters;
        final String[] words;

        Phrase(String str, List<String> list) {
            this.intent = str;
            int length = 0;
            this.words = (String[]) list.toArray(new String[0]);
            for (String str2 : list) {
                this.forms.add(VoiceCommandRepair.variants(str2));
                length += str2.length();
            }
            this.letters = length;
        }
    }

    static Phrase prepare(String str) {
        String strNormalize = VoiceCommandRepair.normalize(str, false);
        if (strNormalize == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        String str2 = null;
        for (String str3 : strNormalize.split("\\s+")) {
            if (REJECT.contains(str3) || !str3.matches("[а-я]+")) {
                return null;
            }
            if (FILLER.contains(str3)) {
                continue;
            }
            String strAction = action(str3);
            if (strAction != null) {
                if (str2 != null && !str2.equals(strAction)) {
                    return null;
                }
                str2 = strAction;
            } else {
                if (str3.contains("ключ") || str3.contains("блок") || str3.startsWith("вкл") || str3.startsWith("выкл") || str3.startsWith("откл") || str3.startsWith("перекл")) {
                    return null;
                }
                arrayList.add(str3);
            }
        }
        if (arrayList.isEmpty() || arrayList.size() > 8) {
            return null;
        }
        if (str2 == null) {
            str2 = DebugKt.DEBUG_PROPERTY_VALUE_ON;
        }
        return new Phrase(str2, arrayList);
    }

    private static String action(String str) {
        if (Arrays.asList("включи", "включить", "включите", "установи", "поставь").contains(str)) {
            return DebugKt.DEBUG_PROPERTY_VALUE_ON;
        }
        if (Arrays.asList("выключи", "выключить", "выключите", "отключи", "отключить", "отключите").contains(str)) {
            return DebugKt.DEBUG_PROPERTY_VALUE_OFF;
        }
        if (Arrays.asList("переключи", "переключить", "переключите").contains(str)) {
            return "switch";
        }
        if (Arrays.asList("заблокировать", "заблокируй", "заблокируйте").contains(str)) {
            return "lock";
        }
        if (Arrays.asList("разблокировать", "разблокируй", "разблокируйте").contains(str)) {
            return "unlock";
        }
        if (Arrays.asList("открой", "открыть", "откройте").contains(str)) {
            return "open";
        }
        if (Arrays.asList("закрой", "закрыть").contains(str)) {
            return "close";
        }
        return null;
    }

    static VoiceCommandCatalog.Command match(List<VoiceCommandCatalog.Command> list, String str) {
        int i;
        Phrase phrasePrepare = prepare(str);
        if (phrasePrepare == null) {
            return null;
        }
        HashMap<String, Integer> map = new HashMap<>();
        HashMap<String, VoiceCommandCatalog.Command> map2 = new HashMap<>();
        HashMap<String, Boolean> map3 = new HashMap<>();
        Iterator<VoiceCommandCatalog.Command> it = list.iterator();
        while (true) {
            i = 1000;
            if (!it.hasNext()) {
                break;
            }
            VoiceCommandCatalog.Command next = it.next();
            for (Phrase phrase : next.fuzzyPhrases) {
                int iScore = score(phrasePrepare, phrase);
                if (iScore < ((Integer) map.getOrDefault(next.action, 1000)).intValue()) {
                    map.put(next.action, Integer.valueOf(iScore));
                    map2.put(next.action, next);
                    map3.put(next.action, Boolean.valueOf(iScore <= budget(phrasePrepare, phrase)));
                } else if (iScore == ((Integer) map.getOrDefault(next.action, 1000)).intValue() && iScore <= budget(phrasePrepare, phrase)) {
                    map3.put(next.action, true);
                }
            }
        }
        String str2 = null;
        int i2 = 1000;
        for (Map.Entry entry : map.entrySet()) {
            int iIntValue = ((Integer) entry.getValue()).intValue();
            if (iIntValue < i) {
                str2 = (String) entry.getKey();
                i2 = i;
                i = iIntValue;
            } else if (iIntValue < i2) {
                i2 = iIntValue;
            }
        }
        if (!Boolean.TRUE.equals(map3.get(str2)) || i > 5 || i2 - i < 2) {
            return null;
        }
        return (VoiceCommandCatalog.Command) map2.get(str2);
    }

    private static int budget(Phrase phrase, Phrase phrase2) {
        if (phrase.words.length == 1) {
            return (phrase.letters < 6 || phrase2.letters < 6) ? 0 : 1;
        }
        return Math.min(5, Math.min(phrase.letters, phrase2.letters) / 3);
    }

    private static int score(Phrase phrase, Phrase phrase2) {
        int iDistance;
        int length = phrase.words.length;
        if (phrase.intent.equals(phrase2.intent) && length == phrase2.words.length) {
            int[][] iArr = (int[][]) Array.newInstance((Class<?>) Integer.TYPE, length, length);
            for (int i = 0; i < length; i++) {
                for (int i2 = 0; i2 < length; i2++) {
                    String str = phrase.words[i];
                    String str2 = phrase2.words[i2];
                    int iMin = str.equals(str2) ? 0 : 1000;
                    if (iMin != 0 && !VoiceCommandRepair.known(str)) {
                        Set<String> set = FIXED;
                        if (!set.contains(str) && !set.contains(str2)) {
                            for (String str3 : phrase2.forms.get(i2)) {
                                if (Math.min(str.length(), str3.length()) >= 4 && (iDistance = distance(str, str3)) <= Math.max(str.length(), str3.length()) / 2) {
                                    iMin = Math.min(iMin, iDistance);
                                }
                            }
                        }
                    }
                    iArr[i][i2] = iMin;
                }
            }
            int i3 = 1 << length;
            int[] iArr2 = new int[i3];
            Arrays.fill(iArr2, 1000);
            iArr2[0] = 0;
            for (int i4 = 0; i4 < i3; i4++) {
                int iBitCount = Integer.bitCount(i4);
                if (iBitCount != length && iArr2[i4] <= 7) {
                    for (int i5 = 0; i5 < length; i5++) {
                        int i6 = 1 << i5;
                        if ((i4 & i6) == 0) {
                            int i7 = i6 | i4;
                            iArr2[i7] = Math.min(iArr2[i7], iArr2[i4] + iArr[iBitCount][i5]);
                        }
                    }
                }
            }
            int i8 = iArr2[i3 - 1];
            if (i8 <= 7) {
                return i8;
            }
        }
        return 1000;
    }

    static int distance(String str, String str2) {
        int[] iArr = new int[str2.length() + 1];
        int[] iArr2 = new int[str2.length() + 1];
        for (int i = 0; i <= str2.length(); i++) {
            iArr[i] = i;
        }
        int i2 = 1;
        while (i2 <= str.length()) {
            iArr2[0] = i2;
            for (int i3 = 1; i3 <= str2.length(); i3++) {
                int i4 = i3 - 1;
                iArr2[i3] = Math.min(Math.min(iArr[i3] + 1, iArr2[i4] + 1), iArr[i4] + (str.charAt(i2 + (-1)) == str2.charAt(i4) ? 0 : 1));
            }
            i2++;
            int[] iArr3 = iArr2;
            iArr2 = iArr;
            iArr = iArr3;
        }
        return iArr[str2.length()];
    }
}
