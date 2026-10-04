package ru.big.town.restoremode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import kotlinx.coroutines.DebugKt;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceCommandCatalog {
    private static final Set<String> FILLER = new LinkedHashSet(Arrays.asList("включи", "включить", "включите", "установи", "поставь", "режим", "режима", "на", "пожалуйста"));
    private static final Set<String> REJECT = new LinkedHashSet(Arrays.asList("не", "ни", "нет", "нельзя", "отмена", "отмени", "или", "потом", "затем", "если"));

    VoiceCommandCatalog() {
    }

    static final class Command {
        final String action;
        final boolean confirm;
        final List<VoiceFuzzyMatcher.Phrase> fuzzyPhrases;
        final List<String> phrases;
        private final List<Set<String>> repairedPhrases;
        final String title;

        Command(String str, String str2, boolean z, String... strArr) {
            this(str, str2, z, false, strArr);
        }

        Command(String str, String str2, boolean z, boolean z2, String... strArr) {
            VoiceFuzzyMatcher.Phrase phrasePrepare;
            this.repairedPhrases = new ArrayList();
            this.fuzzyPhrases = new ArrayList();
            this.action = str;
            this.title = str2;
            this.confirm = z;
            this.phrases = Collections.unmodifiableList(Arrays.asList(strArr));
            if (!z2 || z) {
                return;
            }
            for (String str3 : strArr) {
                String strNormalize = VoiceCommandRepair.normalize(str3, false);
                if (strNormalize != null) {
                    this.repairedPhrases.add(VoiceCommandCatalog.words(strNormalize));
                }
                if (!str.startsWith("fuel_charge:") && (phrasePrepare = VoiceFuzzyMatcher.prepare(str3)) != null) {
                    this.fuzzyPhrases.add(phrasePrepare);
                }
            }
        }
    }

    static String normalize(String str) {
        if (str == null) {
            return "";
        }
        return str.toLowerCase(Locale.ROOT).replace((char) 1105, (char) 1077).replaceAll("[^\\p{L}\\p{N} ]", " ").trim().replaceAll("\\s+", " ");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Set<String> words(String str) {
        String str2;
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        String[] strArrSplit = normalize(str).split(" ");
        int length = strArrSplit.length;
        String str3 = null;
        for (int i = 0; i < length; i++) {
            String str4 = strArrSplit[i];
            if (REJECT.contains(str4)) {
                return null;
            }
            if (str4.equals("переключи") || str4.equals("переключите")) {
                str4 = "переключить";
            }
            if (str4.equals("переключить")) {
                str2 = "switch";
            } else if (Arrays.asList("включи", "включить", "включите", "установи", "поставь").contains(str4)) {
                str2 = DebugKt.DEBUG_PROPERTY_VALUE_ON;
            } else if (Arrays.asList("выключи", "выключить", "выключите", "отключи", "отключить", "отключите").contains(str4)) {
                str2 = DebugKt.DEBUG_PROPERTY_VALUE_OFF;
            } else if (Arrays.asList("заблокировать", "заблокируй", "заблокируйте").contains(str4)) {
                str2 = "lock";
            } else {
                str2 = Arrays.asList("разблокировать", "разблокируй", "разблокируйте").contains(str4) ? "unlock" : null;
            }
            if (str2 != null) {
                if (str3 != null && !str3.equals(str2)) {
                    return null;
                }
                str3 = str2;
            }
            if (!str4.isEmpty() && !FILLER.contains(str4)) {
                linkedHashSet.add(str4);
            }
        }
        return linkedHashSet;
    }

    static Command match(List<Command> list, String str) {
        Set<String> setWords = words(str);
        if (setWords != null && !setWords.isEmpty()) {
            Command commandMatch = VoiceFuelCommand.match(str);
            Command commandMatch2 = VoiceSeatCommands.match(list, str);
            if (commandMatch2 != null) {
                commandMatch = commandMatch2;
            }
            Command commandMatch3 = VoiceWindowCommands.match(list, str);
            if (commandMatch3 != null) {
                if (commandMatch != null && !commandMatch.action.equals(commandMatch3.action)) {
                    return null;
                }
                commandMatch = commandMatch3;
            }
            for (Command command : list) {
                if (!command.action.startsWith("fuel_charge:") && !VoiceSeatCommands.isAction(command.action) && !VoiceWindowCommands.isAction(command.action)) {
                    Iterator<String> it = command.phrases.iterator();
                    while (it.hasNext()) {
                        if (setWords.equals(words(it.next()))) {
                            if (commandMatch != null && !commandMatch.action.equals(command.action)) {
                                return null;
                            }
                            commandMatch = command;
                        }
                    }
                }
            }
            if (commandMatch != null) {
                return commandMatch;
            }
            if (VoiceWindowCommands.mentionsWindow(str)) {
                return null;
            }
            String strNormalize = VoiceCommandRepair.normalize(str, true);
            if (strNormalize == null) {
                return VoiceFuzzyMatcher.match(list, str);
            }
            Set<String> setWords2 = words(strNormalize);
            if (setWords2 != null && !setWords2.isEmpty()) {
                Command commandMatch4 = VoiceFuelCommand.match(strNormalize);
                for (Command command2 : list) {
                    if (command2.repairedPhrases.contains(setWords2)) {
                        if (commandMatch4 != null && !commandMatch4.action.equals(command2.action)) {
                            return null;
                        }
                        commandMatch4 = command2;
                    }
                }
                return commandMatch4 != null ? commandMatch4 : VoiceFuzzyMatcher.match(list, str);
            }
        }
        return null;
    }

    static List<Command> builtIns() {
        ArrayList arrayList = new ArrayList();
        VoiceSeatCommands.addTo(arrayList);
        VoiceWindowCommands.addTo(arrayList);
        binary(arrayList, "wheel_heat", "Подогрев руля", "подогрев руля", "обогрев руля");
        mode(arrayList, "drive:SPORT", "Режим движения: Спорт", "спорт", "спортивный");
        mode(arrayList, "drive:ECO", "Режим движения: Эко", "эко", "экономичный");
        mode(arrayList, "drive:COMFORT", "Режим движения: Комфорт", "комфорт", "комфортный");
        mode(arrayList, "drive:OUTING", "Режим движения: Outing", "загород", "загородный", "внедорожье", "внедорожный");
        addVehicle(arrayList, "drive:OUTING", "Режим Outing — поднять подвеску", "поднять подвеску", "подними подвеску", "поднимите подвеску");
        mode(arrayList, "drive:SNOW", "Режим движения: Снег", "снег", "снежный");
        mode(arrayList, "drive:INDIVIDUAL", "Режим движения: Индивидуальный", "индивидуальный", "свой", "собственный");
        mode(arrayList, "energy:EV", "Энергорежим: Электро", "электро", "электрический");
        mode(arrayList, "energy:REV", "Энергорежим: Гибрид", "гибрид", "гибридный");
        mode(arrayList, "energy:SREV", "Энергорежим: Топливо / сохранение заряда", "топливо", "топливный", "сохранение заряда");
        for (int i = 25; i <= 80; i += 5) {
            arrayList.add(VoiceFuelCommand.command(i));
        }
        mode(arrayList, "recycle:LOW", "Рекуперация: Низкая", "низкая рекуперация", "слабая рекуперация");
        mode(arrayList, "recycle:MEDIUM", "Рекуперация: Стандартная", "стандартная рекуперация", "средняя рекуперация");
        mode(arrayList, "recycle:HIGH", "Рекуперация: Высокая", "высокая рекуперация", "сильная рекуперация");
        binary(arrayList, "forced_ev", "Принудительный электрорежим", "форсированный электро", "форс и ви", "форсированный электрорежим", "принудительный электрорежим", "форсед и ви");
        addSuspensionMaintenance(arrayList);
        binary(arrayList, "pedestrian", "Звук предупреждения пешеходов", "звук пешеходов", "предупреждение пешеходов");
        binary(arrayList, "headlights", "Ближний свет", "фары", "ближний свет");
        addVehicle(arrayList, "headlights:auto", "Штатный свет: Авто", "автоматический свет", "фары авто", "включи авто свет");
        binary(arrayList, "auto_light", "Автосвет VoyahTune", "автосвет воя тюн", "автосвет приложения");
        addVehicle(arrayList, "toggle_headlights", "Переключить фары: выкл / ближний", "переключи фары", "переключить фары");
        addVehicle(arrayList, "toggle_headlights_auto", "Переключить фары: ближний / авто", "переключи фары авто", "переключить фары авто", "переключи авто свет", "переключить авто свет");
        portCap(arrayList, "port_cap:fuel", "Открыть лючок бензобака (только в P)", "бензобак", "бак", "люк бензобака", "лючок бензобака", "люк бака", "лючок бака", "топливный люк", "топливный лючок");
        portCap(arrayList, "port_cap:charge", "Открыть лючок зарядки (только в P)", "зарядку", "зарядка", "люк зарядки", "лючок зарядки", "зарядный люк", "зарядный лючок", "люк для зарядки", "лючок для зарядки", "люк зарядного порта", "лючок зарядного порта");
        addVehicle(arrayList, "power_hold", "Power Hold — оставить автомобиль включённым", "пауэр холд", "оставь машину включенной", "режим ожидания");
        addVehicle(arrayList, "wash", "Режим мойки", "мойка", "включи мойку", "режим мойки");
        addVehicle(arrayList, "battery_heat", "Запросить прогрев батареи", "прогрей батарею", "прогрев батареи", "включи подогрев батареи");
        add(arrayList, "apply", "Применить сохранённые настройки", "примени настройки", "применить настройки", "восстанови настройки автомобиля");
        add(arrayList, "open_voyahtune", "Открыть VoyahTune", "открой воя тюн", "открой приложение воя тюн", "открой настройки автомобиля");
        add(arrayList, "system_back", "Назад", "назад", "вернись назад", "вернуться назад");
        arrayList.add(new Command("close_all", "Закрыть сторонние приложения", true, "закрой все приложения", "закрыть все приложения"));
        arrayList.add(new Command("reboot", "Перезагрузить головное устройство", true, "перезагрузи систему", "перезагрузи головное устройство", "перезагрузка системы"));
        return arrayList;
    }

    private static void portCap(List<Command> list, String str, String str2, String... strArr) {
        ArrayList arrayList = new ArrayList();
        for (String str3 : strArr) {
            Collections.addAll(arrayList, str3, "открой " + str3, "открыть " + str3, "откройте " + str3);
        }
        addVehicle(list, str, str2, (String[]) arrayList.toArray(new String[0]));
    }

    private static void mode(List<Command> list, String str, String str2, String... strArr) {
        ArrayList arrayList = new ArrayList();
        for (String str3 : strArr) {
            Collections.addAll(arrayList, str3, "включи " + str3, str3 + " режим", "включи " + str3 + " режим", "включи режим " + str3, "переключи на " + str3, "поставь " + str3);
            if (str.startsWith("drive:") || str.startsWith("energy:")) {
                Collections.addAll(arrayList, "режим " + str3, "включить режим " + str3, "включить " + str3 + " режим", "переключи на режим " + str3, "переключи на " + str3 + " режим");
            }
        }
        addVehicle(list, str, str2, (String[]) arrayList.toArray(new String[0]));
    }

    private static void addSuspensionMaintenance(List<Command> list) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        String[] strArr = {"сервисный режим подвески", "режим обслуживания подвески"};
        for (int i = 0; i < 2; i++) {
            String str = strArr[i];
            Collections.addAll(arrayList, "включи " + str, "включить " + str, "включите " + str);
            Collections.addAll(arrayList2, "выключи " + str, "выключить " + str, "отключи " + str, "отключить " + str);
        }
        Collections.addAll(arrayList, "заблокировать подвеску", "заблокируй подвеску", "заблокируйте подвеску", "заблокировать подвеска");
        Collections.addAll(arrayList2, "разблокировать подвеску", "разблокируй подвеску", "разблокируйте подвеску", "разблокировать подвеска");
        addVehicle(list, "suspension_maintenance:on", "Сервисный режим подвески: включить", (String[]) arrayList.toArray(new String[0]));
        addVehicle(list, "suspension_maintenance:off", "Сервисный режим подвески: выключить", (String[]) arrayList2.toArray(new String[0]));
    }

    private static void binary(List<Command> list, String str, String str2, String... strArr) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (String str3 : strArr) {
            Collections.addAll(arrayList, "включи " + str3, "включить " + str3);
            Collections.addAll(arrayList2, "выключи " + str3, "отключи " + str3, "выключить " + str3);
        }
        addVehicle(list, str + ":on", str2 + ": включить", (String[]) arrayList.toArray(new String[0]));
        addVehicle(list, str + ":off", str2 + ": выключить", (String[]) arrayList2.toArray(new String[0]));
    }

    static void add(List<Command> list, String str, String str2, String... strArr) {
        list.add(new Command(str, str2, false, strArr));
    }

    static void addVehicle(List<Command> list, String str, String str2, String... strArr) {
        list.add(new Command(str, str2, false, true, strArr));
    }
}
