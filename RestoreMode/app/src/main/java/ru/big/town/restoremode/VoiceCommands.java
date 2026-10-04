package ru.big.town.restoremode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceCommands {
    static final String ENABLED = "voiceAssistantEnabled";
    static final int MESSAGE = 36;
    static final String START = "voice_assistant";

    VoiceCommands() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    static List<VoiceCommandCatalog.Command> load(final Context context) {
        char c;
        List<VoiceCommandCatalog.Command> listBuiltIns = VoiceCommandCatalog.builtIns();
        int i = 0;
        SharedPreferences sharedPreferences = context.getSharedPreferences("DrivePreferences", 0);
        String[][] strArr = AdvanceActivity.EXAMPLE_COMMANDS;
        int length = strArr.length;
        int i2 = 0;
        while (true) {
            c = 1;
            if (i2 >= length) {
                break;
            }
            String[] strArr2 = strArr[i2];
            if (!strArr2[1].startsWith("форсе") && !strArr2[1].startsWith("обогрев руля ")) {
                boolean zEndsWith = strArr2[1].endsWith(" вкл");
                String strReplace = strArr2[1].replace(" вкл", "").replace(" выкл", "");
                String str = "can:" + strArr2[0].replace(" ", "");
                String str2 = strReplace + (zEndsWith ? ": включить" : ": выключить");
                String[] strArr3 = new String[2];
                strArr3[0] = (zEndsWith ? "включи " : "выключи ") + strReplace;
                strArr3[1] = (zEndsWith ? "включить " : "отключи ") + strReplace;
                VoiceCommandCatalog.addVehicle(listBuiltIns, str, str2, strArr3);
            }
            i2++;
        }
        HashSet hashSet = new HashSet();
        List<ResolveInfo> listQueryIntentActivities = context.getPackageManager().queryIntentActivities(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER"), 0);
        listQueryIntentActivities.removeIf(new Predicate() { // from class: ru.big.town.restoremode.VoiceCommands$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return VoiceCommands.lambda$load$0((ResolveInfo) obj);
            }
        });
        listQueryIntentActivities.sort(new Comparator() { // from class: ru.big.town.restoremode.VoiceCommands$$ExternalSyntheticLambda1
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                Context context2 = context;
                return ((ResolveInfo) obj).loadLabel(context2.getPackageManager()).toString().compareToIgnoreCase(((ResolveInfo) obj2).loadLabel(context2.getPackageManager()).toString());
            }
        });
        for (ResolveInfo resolveInfo : listQueryIntentActivities) {
            String str3 = resolveInfo.activityInfo.packageName;
            if (hashSet.add(str3) && !str3.equals(context.getPackageName())) {
                String string = resolveInfo.loadLabel(context.getPackageManager()).toString();
                VoiceCommandCatalog.add(listBuiltIns, "app:" + str3, "Открыть приложение: " + string, "открой " + string, "запусти " + string, "открой приложение " + string);
            }
        }
        List<SplitStore.Preset> listLoad = SplitStore.load(sharedPreferences);
        for (int i3 = 0; i3 < listLoad.size(); i3++) {
            SplitStore.Preset preset = listLoad.get(i3);
            if (preset.ready()) {
                int i4 = i3 + 1;
                VoiceCommandCatalog.add(listBuiltIns, SplitConfigSync.resolveSteerAction("split:" + i3, sharedPreferences), "Сплит " + i4 + ": " + preset.ll + " / " + preset.rl, "открой сплит " + number(i4), "запусти сплит " + number(i4));
            }
        }
        for (DialWidgetStore.Entry entry : DialWidgetStore.load(sharedPreferences)) {
            String strReplaceAll = entry.number.replaceAll("[^0-9]", "");
            if (strReplaceAll.length() >= 4 && strReplaceAll.length() <= 10) {
                if (strReplaceAll.length() == 10) {
                    strReplaceAll = "8" + strReplaceAll;
                }
                if (!entry.name.trim().isEmpty()) {
                    VoiceCommandCatalog.add(listBuiltIns, "call:" + strReplaceAll, "Набрать номер: " + entry.name, "позвони " + entry.name, "набери " + entry.name);
                }
            }
        }
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet();
        String[] strArr4 = {"Star", "Dvr", "Voice", "Phone"};
        for (int i5 = 0; i5 < 4; i5++) {
            String str4 = strArr4[i5];
            String[] strArr5 = new String[2];
            strArr5[i] = "Short";
            strArr5[c] = "Long";
            int i6 = i;
            while (i6 < 2) {
                int i7 = i;
                char c2 = c;
                for (String str5 : SteeringActionStore.load(sharedPreferences, "steer" + str4 + strArr5[i6])) {
                    if (str5.startsWith("can:")) {
                        linkedHashSet.add(str5);
                    }
                }
                i6++;
                i = i7;
                c = c2;
            }
        }
        int i8 = i;
        boolean z = c == 1;
        String[] strArrSplit = sharedPreferences.getString("customCommand", "").split("\\n");
        int length2 = strArrSplit.length;
        for (int i9 = i8; i9 < length2; i9++) {
            String strCompact = SteeringCanCommandPolicy.compact(strArrSplit[i9]);
            if (strCompact.length() == 20) {
                linkedHashSet.add("can:" + strCompact);
            }
        }
        int i10 = i8;
        for (String str6 : linkedHashSet) {
            i10++;
            String str7 = "Своя CAN-команда " + i10 + ": " + str6.substring(4);
            String[] strArr6 = new String[2];
            strArr6[i8] = "выполни команду " + number(i10);
            strArr6[z ? 1 : 0] = "своя команда " + number(i10);
            listBuiltIns.add(new VoiceCommandCatalog.Command(str6, str7, z, strArr6));
        }
        return listBuiltIns;
    }

    static /* synthetic */ boolean lambda$load$0(ResolveInfo resolveInfo) {
        return resolveInfo.activityInfo == null || resolveInfo.activityInfo.applicationInfo == null || (resolveInfo.activityInfo.applicationInfo.flags & 129) != 0;
    }

    private static String number(int i) {
        return i < 11 ? new String[]{"ноль", "один", "два", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять", "десять"}[i] : Integer.toString(i);
    }
}
