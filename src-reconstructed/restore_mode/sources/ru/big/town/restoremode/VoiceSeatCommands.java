package ru.big.town.restoremode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlinx.coroutines.DebugKt;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceSeatCommands {
    private static final Map<String, String> WORDS = new LinkedHashMap();

    VoiceSeatCommands() {
    }

    static {
        forms("massage", "массаж массажа массажем массажный массажного");
        forms("heat", "подогрев подогрева обогрев обогрева подогрей подогреть");
        forms("vent", "вентиляция вентиляцию вентиляции обдув обдува");
        forms("seat", "сиденье сиденья сидение сидения сиденью сидению кресло кресла креслу");
        forms("driver", "водитель водителя водителю водительское водительского водительском водительский");
        forms("passenger", "пассажир пассажира пассажиру пассажирское пассажирского пассажирском пассажирский");
        forms("front", "передний переднего переднее переднем");
        forms("waves", "волны волна волнами волн волновой волнового");
        forms("rollers", "ролики роликами роликов роликовый роликового");
        forms("level", "уровень уровня уровне интенсивность интенсивности");
        forms("1", "1 один единица первый первом первую");
        forms("2", "2 два двойка второй втором вторую");
        forms("3", "3 три тройка третий третьем третью");
        forms(DebugKt.DEBUG_PROPERTY_VALUE_ON, "включи включить включите установи установите поставь поставьте");
        forms(DebugKt.DEBUG_PROPERTY_VALUE_OFF, "выключи выключить выключите отключи отключить отключите");
        forms("filler", "на у для пожалуйста режим режима");
    }

    private static void forms(String str, String str2) {
        for (String str3 : str2.split(" ")) {
            WORDS.put(str3, str);
        }
    }

    static boolean isAction(String str) {
        return str.startsWith("seat:");
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:100:0x0144  */
    /* JADX WARN: Code duplicated, block: B:101:0x0146  */
    /* JADX WARN: Code duplicated, block: B:102:0x0148  */
    /* JADX WARN: Code duplicated, block: B:106:0x0151 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:108:0x0154  */
    /* JADX WARN: Code duplicated, block: B:109:0x0156 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:111:0x0159  */
    /* JADX WARN: Code duplicated, block: B:157:0x0129 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:158:0x012c A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:159:0x0131 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:160:0x0137 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:161:0x013d A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:162:0x0143 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:164:0x0153 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:165:0x0158 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:167:0x015a A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:86:0x012a A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:88:0x012d  */
    /* JADX WARN: Code duplicated, block: B:89:0x012f A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:91:0x0132  */
    /* JADX WARN: Code duplicated, block: B:92:0x0135 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:94:0x0138  */
    /* JADX WARN: Code duplicated, block: B:95:0x013b A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:97:0x013e  */
    /* JADX WARN: Code duplicated, block: B:98:0x0141 A[DONT_INVERT] */
    static String action(String str) {
        if (str == null || str.length() > 256 || str.matches(".*[0-9][.,/+-][0-9].*") || str.matches(".*[+−-][0-9].*")) {
            return null;
        }
        String[] strArrSplit = VoiceCommandCatalog.normalize(str).split(" ");
        int length = strArrSplit.length;
        int i = 0;
        boolean z = false;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        String str6 = null;
        boolean z2 = false;
        boolean z3 = false;
        while (i < length) {
            int i2 = length;
            String str7 = WORDS.get(strArrSplit[i]);
            if (str7 == null) {
                return null;
            }
            str7.hashCode();
            byte b = -1;
            switch (str7) {
                case "driver":
                    b = 0;
                case "filler":
                    b = 1;
                case "passenger":
                    b = 2;
                case "1":
                    b = 3;
                case "2":
                    b = 4;
                case "3":
                    b = 5;
                case "on":
                    b = 6;
                case "off":
                    b = 7;
                case "heat":
                    b = 8;
                case "seat":
                    b = 9;
                case "vent":
                    b = 10;
                case "front":
                    b = 11;
                case "level":
                    b = 12;
                case "waves":
                    b = 13;
                case "massage":
                    b = 14;
                case "rollers":
                    b = 15;
                default:
                    switch (b) {
                        case 0:
                        case 2:
                            if (str2 != null) {
                                return null;
                            }
                            str2 = str7;
                            break;
                        case 1:
                            break;
                        case 3:
                        case 4:
                        case 5:
                            if (str5 != null) {
                                return null;
                            }
                            str5 = str7;
                            break;
                        case 6:
                        case 7:
                            if (str6 == null && !str6.equals(str7)) {
                                return null;
                            }
                            str6 = str7;
                            break;
                            break;
                        case 8:
                        case 10:
                        case 14:
                            if (str3 != null) {
                                return null;
                            }
                            str3 = str7;
                            break;
                        case 9:
                            if (z2) {
                                return null;
                            }
                            z2 = true;
                            break;
                        case 11:
                            if (z) {
                                return null;
                            }
                            z = true;
                            break;
                        case 12:
                            if (z3) {
                                return null;
                            }
                            z3 = true;
                            break;
                        case 13:
                        case 15:
                            if (str4 != null) {
                                return null;
                            }
                            str4 = str7;
                            break;
                        default:
                            return null;
                    }
            }
        }
        if (z && str2 == null) {
            return null;
        }
        if (str4 != null) {
            if (str3 != null && !str3.equals("massage")) {
                return null;
            }
            str3 = "massage";
        }
        if (str3 == null || (str3.equals("heat") && !z2 && str2 == null)) {
            return null;
        }
        if (z3 && str5 == null) {
            return null;
        }
        if (str4 != null && str5 != null) {
            return null;
        }
        if (DebugKt.DEBUG_PROPERTY_VALUE_OFF.equals(str6) && (str4 != null || str5 != null)) {
            return null;
        }
        if (str4 == null) {
            if (str5 != null) {
                str4 = str5;
            } else {
                str4 = DebugKt.DEBUG_PROPERTY_VALUE_OFF.equals(str6) ? DebugKt.DEBUG_PROPERTY_VALUE_OFF : DebugKt.DEBUG_PROPERTY_VALUE_ON;
            }
        }
        StringBuilder sb = new StringBuilder("seat:");
        if (str2 == null) {
            str2 = "driver";
        }
        return sb.append(str2).append(":").append(str3).append(":").append(str4).toString();
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
        String str;
        String[] strArr;
        String str2;
        int i = 2;
        int i2 = 0;
        int i3 = 1;
        String[] strArr2 = {"driver", "passenger"};
        int i4 = 0;
        while (i4 < i) {
            String str3 = strArr2[i4];
            String str4 = str3.equals("driver") ? "водителя" : "пассажира";
            char c = 3;
            String[] strArr3 = new String[3];
            String str5 = "massage";
            strArr3[i2 == true ? 1 : 0] = "massage";
            String str6 = "heat";
            strArr3[i3] = "heat";
            strArr3[i] = "vent";
            int i5 = i2 == true ? 1 : 0;
            int i6 = i2;
            while (i5 < c) {
                String str7 = strArr3[i5];
                if (str7.equals(str5)) {
                    str = "Массаж";
                } else {
                    str = str7.equals(str6) ? "Подогрев сиденья" : "Вентиляция сиденья";
                }
                if (str7.equals(str5)) {
                    strArr = new String[i3];
                    strArr[i6 == true ? 1 : 0] = "массаж";
                } else if (str7.equals(str6)) {
                    strArr = new String[i];
                    strArr[i6 == true ? 1 : 0] = "подогрев сиденья";
                    strArr[i3] = "обогрев кресла";
                } else {
                    strArr = new String[i];
                    strArr[i6 == true ? 1 : 0] = "вентиляция сиденья";
                    strArr[i3] = "обдув кресла";
                }
                int i7 = i3;
                char c2 = c;
                String[] strArr4 = new String[5];
                int i8 = i6 == true ? 1 : 0;
                strArr4[i8] = DebugKt.DEBUG_PROPERTY_VALUE_ON;
                int i9 = i;
                String str8 = DebugKt.DEBUG_PROPERTY_VALUE_OFF;
                strArr4[i7] = DebugKt.DEBUG_PROPERTY_VALUE_OFF;
                strArr4[i9] = "1";
                strArr4[c2] = "2";
                strArr4[4] = "3";
                ArrayList arrayList = new ArrayList(Arrays.asList(strArr4));
                String[] strArr5 = strArr2;
                String str9 = "rollers";
                int i10 = i4;
                if (str7.equals(str5)) {
                    String[] strArr6 = new String[i9];
                    strArr6[i8] = "waves";
                    strArr6[i7] = "rollers";
                    arrayList.addAll(Arrays.asList(strArr6));
                }
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    String str10 = (String) it.next();
                    ArrayList arrayList2 = new ArrayList();
                    Iterator it2 = it;
                    int length = strArr.length;
                    String str11 = str5;
                    String str12 = str6;
                    int i11 = i8;
                    while (i11 < length) {
                        int i12 = length;
                        String str13 = strArr[i11];
                        int i13 = i11;
                        variants(arrayList2, str13 + " " + str4, str10);
                        if (str3.equals("driver")) {
                            variants(arrayList2, str13, str10);
                        }
                        i11 = (i13 == true ? 1 : 0) + 1;
                        length = i12;
                    }
                    if (str10.equals(DebugKt.DEBUG_PROPERTY_VALUE_ON)) {
                        str2 = "включить";
                    } else if (str10.equals(str8)) {
                        str2 = "выключить";
                    } else if (str10.equals("waves")) {
                        str2 = "волны";
                    } else {
                        str2 = str10.equals(str9) ? "ролики" : "уровень " + str10;
                    }
                    list.add(new VoiceCommandCatalog.Command("seat:" + str3 + ":" + str7 + ":" + str10, str + " " + str4 + ": " + str2, i8, (String[]) arrayList2.toArray(new String[i8])));
                    it = it2;
                    str5 = str11;
                    str6 = str12;
                    str8 = str8;
                    str9 = str9;
                }
                i5++;
                i6 = i8;
                i3 = i7;
                c = c2;
                strArr3 = strArr3;
                strArr2 = strArr5;
                i4 = i10;
                i = 2;
            }
            boolean z = i6 == true ? 1 : 0;
            i4++;
            i3 = i3;
            i = 2;
            i2 = i6;
        }
    }

    private static void variants(List<String> list, String str, String str2) {
        if (str2.equals(DebugKt.DEBUG_PROPERTY_VALUE_ON)) {
            list.addAll(Arrays.asList(str, "включи " + str, "включить " + str));
            return;
        }
        if (str2.equals(DebugKt.DEBUG_PROPERTY_VALUE_OFF)) {
            list.addAll(Arrays.asList("выключи " + str, "выключить " + str, "отключи " + str));
            return;
        }
        if (str2.equals("waves") || str2.equals("rollers")) {
            list.add(str + " " + (str2.equals("waves") ? "волны" : "ролики"));
            list.add("включи " + str + " " + (str2.equals("waves") ? "волнами" : "роликами"));
            return;
        }
        int i = Integer.parseInt(str2);
        list.add(str + " " + str2);
        list.add(str + " " + new String[]{"", "один", "два", "три"}[i]);
        list.add(str + " на " + str2);
        list.add(str + " " + new String[]{"", "первый", "второй", "третий"}[i] + " уровень");
    }
}
