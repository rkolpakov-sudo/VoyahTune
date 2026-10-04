package ru.big.town.restoremode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlinx.coroutines.DebugKt;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceWindowCommands {
    private static final Map<String, String> WORDS = new HashMap();

    VoiceWindowCommands() {
    }

    static {
        forms("open", "открой открыть откройте опусти опустить опустите");
        forms("close", "закрой закрыть закройте подними поднять поднимите");
        forms("ventVerb", "приоткрой приоткрыть приоткройте проветри проветрить проветрите");
        forms("vent", "проветривание проветривания");
        forms("little", "немного чуть слегка");
        forms(DebugKt.DEBUG_PROPERTY_VALUE_ON, "включи включить включите");
        forms("window", "окно стекло");
        forms("windows", "окна окон стекла стекол");
        forms("roof", "люк люка");
        forms("shade", "шторка шторку шторки штора штору шторы");
        forms("roofPlace", "крыша крыши крыше панорама панорамы панораме");
        forms("shadeKind", "солнцезащитную солнцезащитная солнцезащитной");
        forms("driver", "водителя водительское водительского");
        forms("passenger", "пассажира пассажирское пассажирского");
        forms("front", "переднее переднего передние передних спереди");
        forms("rear", "заднее заднего задние задних сзади");
        forms("left", "левое левого левые левых слева");
        forms("right", "правое правого правые правых справа");
        forms("all", "все всех");
        forms("both", "оба обоих");
        forms("cabin", "салон салона");
        forms("filler", "пожалуйста на у для режим режима только");
    }

    private static void forms(String str, String str2) {
        for (String str3 : str2.split(" ")) {
            WORDS.put(str3, str);
        }
    }

    static boolean isAction(String str) {
        if (str != null) {
            return str.startsWith("windows:") || str.startsWith("sunroof:") || str.startsWith("sunshade:");
        }
        return false;
    }

    static boolean mentionsWindow(String str) {
        boolean z = false;
        boolean z2 = false;
        for (String str2 : VoiceCommandCatalog.normalize(str).split(" ")) {
            String str3 = WORDS.get(str2);
            if ("roof".equals(str3) || "shade".equals(str3) || "vent".equals(str3) || "ventVerb".equals(str3)) {
                return true;
            }
            if ("window".equals(str3) || "windows".equals(str3)) {
                z = true;
            }
            if ("open".equals(str3) || "close".equals(str3) || "little".equals(str3)) {
                z2 = true;
            }
        }
        return z && z2;
    }

    static String action(String str) {
        String strTarget;
        if (str == null || str.length() > 256 || str.contains("%")) {
            return null;
        }
        HashSet hashSet = new HashSet();
        for (String str2 : VoiceCommandCatalog.normalize(str).split(" ")) {
            String str3 = WORDS.get(str2);
            if (str3 == null) {
                return null;
            }
            if (!str3.equals("filler") && !hashSet.add(str3)) {
                return null;
            }
        }
        boolean zContains = hashSet.contains("window");
        boolean zContains2 = hashSet.contains("windows");
        boolean zContains3 = hashSet.contains("roof");
        boolean zContains4 = hashSet.contains("shade");
        String str4 = "vent";
        boolean z = hashSet.contains("vent") || hashSet.contains("ventVerb") || hashSet.contains("little");
        boolean zContains5 = hashSet.contains("open");
        boolean zContains6 = hashSet.contains("close");
        boolean zContains7 = hashSet.contains(DebugKt.DEBUG_PROPERTY_VALUE_ON);
        if ((zContains5 && zContains6) || ((zContains6 && z) || ((zContains7 && !z) || (zContains7 && zContains6)))) {
            return null;
        }
        if (hashSet.contains("little") && !zContains5 && !hashSet.contains("ventVerb") && !hashSet.contains("vent")) {
            return null;
        }
        if (!z && !zContains5 && !zContains6) {
            return null;
        }
        if (zContains && zContains2) {
            return null;
        }
        boolean z2 = hashSet.contains("driver") || hashSet.contains("passenger") || hashSet.contains("front") || hashSet.contains("rear") || hashSet.contains("left") || hashSet.contains("right");
        if (zContains3 || zContains4) {
            if (zContains || zContains2 || z2 || hashSet.contains("all") || hashSet.contains("both") || hashSet.contains("cabin") || (hashSet.contains("shadeKind") && !zContains4)) {
                return null;
            }
            if (zContains4 && z) {
                return null;
            }
            StringBuilder sbAppend = new StringBuilder().append(zContains4 ? "sunshade:" : "sunroof:");
            if (!z) {
                str4 = zContains6 ? "close" : "open";
            }
            return sbAppend.append(str4).toString();
        }
        if (hashSet.contains("roofPlace") || hashSet.contains("shadeKind")) {
            return null;
        }
        if (hashSet.contains("cabin") && (!z || zContains || zContains2 || z2)) {
            return null;
        }
        if ((!zContains && !zContains2 && (!z || z2 || hashSet.contains("all") || hashSet.contains("both"))) || (strTarget = target(hashSet, zContains)) == null) {
            return null;
        }
        if (!z) {
            return "windows:" + strTarget + (strTarget.isEmpty() ? "" : ":") + (zContains6 ? "close" : "open");
        }
        if (!strTarget.isEmpty() || zContains) {
            return null;
        }
        return "windows:vent";
    }

    /* JADX WARN: Code duplicated, block: B:43:0x006f  */
    private static String target(Set<String> set, boolean z) {
        String str = "driver";
        boolean zContains = set.contains("driver");
        boolean zContains2 = set.contains("passenger");
        String str2 = "front";
        boolean zContains3 = set.contains("front");
        boolean zContains4 = set.contains("rear");
        boolean zContains5 = set.contains("left");
        boolean zContains6 = set.contains("right");
        if ((zContains && zContains2) || ((zContains3 && zContains4) || (zContains5 && zContains6))) {
            return null;
        }
        boolean z2 = true;
        if (zContains || zContains2) {
            if (zContains4 || ((zContains && zContains6) || (zContains2 && zContains5))) {
                return null;
            }
            if (!zContains) {
                str = "passenger";
            }
            str2 = str;
        } else if ((zContains3 || zContains4) && (zContains5 || zContains6)) {
            if (!zContains3) {
                str = zContains5 ? "rear_left" : "rear_right";
            } else if (!zContains5) {
                str = "passenger";
            }
            str2 = str;
        } else {
            if (!zContains3) {
                if (zContains4) {
                    str2 = "rear";
                } else if (zContains5) {
                    str2 = "left";
                } else {
                    str2 = zContains6 ? "right" : "";
                }
            }
            z2 = false;
        }
        if (z != z2) {
            return null;
        }
        if (z2 && (set.contains("all") || set.contains("both"))) {
            return null;
        }
        if (set.contains("both") && str2.isEmpty()) {
            return null;
        }
        return str2;
    }

    static VoiceCommandCatalog.Command match(List<VoiceCommandCatalog.Command> list, String str) {
        String strAction = action(str);
        if (strAction == null) {
            return null;
        }
        for (VoiceCommandCatalog.Command command : list) {
            if (strAction.equals(command.action)) {
                return command;
            }
        }
        return null;
    }

    static void addTo(List<VoiceCommandCatalog.Command> list) {
        String[][] strArr = {new String[]{"", "Все окна", "окна", "все окна", "все стекла"}, new String[]{"driver", "Окно водителя", "водительское окно", "водительское стекло", "окно водителя", "переднее левое окно"}, new String[]{"passenger", "Окно переднего пассажира", "окно пассажира", "пассажирское стекло", "окно переднего пассажира", "переднее правое окно"}, new String[]{"rear_left", "Заднее левое окно", "заднее левое окно", "заднее левое стекло", "окно сзади слева"}, new String[]{"rear_right", "Заднее правое окно", "заднее правое окно", "заднее правое стекло", "окно сзади справа"}, new String[]{"front", "Передние окна", "передние окна", "оба передних стекла", "окна спереди"}, new String[]{"rear", "Задние окна", "задние окна", "оба задних стекла", "окна сзади"}, new String[]{"left", "Левые окна", "левые окна", "оба левых стекла", "окна слева"}, new String[]{"right", "Правые окна", "правые окна", "оба правых стекла", "окна справа"}};
        for (int i = 0; i < 9; i++) {
            String[] strArr2 = strArr[i];
            addMotion(list, "windows:" + strArr2[0] + (strArr2[0].isEmpty() ? "" : ":"), strArr2, true);
        }
        addMotion(list, "sunroof:", new String[]{"", "Люк", "люк", "люк на крыше"}, false);
        addMotion(list, "sunshade:", new String[]{"", "Шторка люка", "шторку", "шторку люка", "шторку панорамы", "солнцезащитную шторку", "штору"}, false);
        list.add(new VoiceCommandCatalog.Command("windows:vent", "Все окна: проветривание", false, "проветривание", "режим проветривания", "включи проветривание", "проветри салон", "приоткрой окна", "немного открой все окна", "проветривание окон"));
        list.add(new VoiceCommandCatalog.Command("sunroof:vent", "Люк: проветривание", false, "проветривание люка", "приоткрой люк", "открой люк на проветривание", "включи проветривание люка", "немного открой люк"));
    }

    private static void addMotion(List<VoiceCommandCatalog.Command> list, String str, String[] strArr, boolean z) {
        boolean[] zArr = {true, false};
        for (int i = 0; i < 2; i++) {
            boolean z2 = zArr[i];
            ArrayList arrayList = new ArrayList();
            for (int i2 = 2; i2 < strArr.length; i2++) {
                arrayList.add((z2 ? "открой " : "закрой ") + strArr[i2]);
                arrayList.add((z2 ? "открыть " : "закрыть ") + strArr[i2]);
                if (z) {
                    arrayList.add((z2 ? "опусти " : "подними ") + strArr[i2]);
                }
            }
            list.add(new VoiceCommandCatalog.Command(str + (z2 ? "open" : "close"), strArr[1] + (z2 ? ": открыть" : ": закрыть"), false, (String[]) arrayList.toArray(new String[0])));
        }
    }
}
