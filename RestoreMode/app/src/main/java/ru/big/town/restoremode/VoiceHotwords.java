package ru.big.town.restoremode;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceHotwords {
    VoiceHotwords() {
    }

    static String fromCommands(List<VoiceCommandCatalog.Command> list) {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        linkedHashSet.add("не");
        linkedHashSet.add("нет");
        linkedHashSet.add("отмена");
        Iterator<VoiceCommandCatalog.Command> it = list.iterator();
        while (it.hasNext()) {
            Iterator<String> it2 = it.next().phrases.iterator();
            while (it2.hasNext()) {
                String strReplaceAll = it2.next().toLowerCase(Locale.ROOT).replace((char) 1105, (char) 1077).replaceAll("[^\\p{L}\\p{N} ]", " ").trim().replaceAll("\\s+", " ");
                if (strReplaceAll.matches("[а-я ]+") && strReplaceAll.length() <= 160) {
                    linkedHashSet.add(strReplaceAll);
                    linkedHashSet.add("не " + strReplaceAll);
                }
            }
        }
        return String.join("/", linkedHashSet);
    }
}
