package ru.big.town.restoremode;

import androidx.core.os.EnvironmentCompat;
import java.util.LinkedHashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
final class HookStatusContract {
    static final String AUTHORITY = "ru.big.town.restoremode.restoremodecontentprovider";
    private static final String[] HOOK_IDS = {"vd-bypass", "steering-wheel", "launcher-dock", "multi-display", "apollo-tech", "keyboard-en", "keyboard-ru"};
    private static final String[] HOOK_LABELS = {"Окна / VirtualDisplay", "Кнопки руля", "Док лаунчера", "Перенос между экранами", "Apollo ADAS", "Клавиатура EN", "Клавиатура RU"};
    static final int MAX_PAYLOAD_LENGTH = 2_048;
    static final String METHOD_PUBLISH = "publishHookStatusV1";
    static final String PAYLOAD_KEY = "payload_v1";
    static final String PREFERENCES_NAME = "HookStatus";
    static final int SCHEMA_VERSION = 1;

    private HookStatusContract() {
    }

    static boolean isValidPayload(String str) {
        return parse(str) != null;
    }

    static String renderForUi(String str) {
        Snapshot snapshot = parse(str);
        if (snapshot == null) {
            return "Состояние ещё не опубликовано. Проверьте boot-сервис voyahtune_load.";
        }
        StringBuilder sb = new StringBuilder(320);
        sb.append("Loader: ").append("running".equals(snapshot.loaderState) ? "работает" : "остановлен");
        if (snapshot.loaderPid > 0) {
            sb.append(" (PID ").append(snapshot.loaderPid).append(')');
        }
        int i = 0;
        while (true) {
            String[] strArr = HOOK_IDS;
            if (i < strArr.length) {
                Entry entry = snapshot.hooks.get(strArr[i]);
                sb.append('\n').append(HOOK_LABELS[i]).append(": ");
                if (entry == null) {
                    sb.append("нет данных");
                } else {
                    sb.append(localizedState(entry.state));
                    if (entry.pid > 0) {
                        sb.append(" (PID ").append(entry.pid).append(')');
                    }
                }
                i++;
            } else {
                return sb.toString();
            }
        }
    }

    private static String localizedState(String str) {
        str.hashCode();
        switch (str) {
            case "active":
                return "активен";
            case "failed":
                return "ошибка (до перезапуска процесса)";
            case "injecting":
                return "устанавливается";
            case "disabled":
                return "выключен";
            case "waiting":
                return "ожидает процесс";
            default:
                return "неизвестно";
        }
    }

    private static Snapshot parse(String str) {
        int pid;
        int pid2;
        if (str == null || str.isEmpty() || str.length() > 2048 || str.indexOf(10) >= 0 || str.indexOf(13) >= 0) {
            return null;
        }
        String[] parts = str.split(";", -1);
        if (parts.length != 3 + HOOK_IDS.length || !"v=1".equals(parts[0])) {
            return null;
        }
        String strExactValue = exactValue(parts[1], "loader");
        if ((!"running".equals(strExactValue) && !"stopped".equals(strExactValue)) || (pid = parsePid(exactValue(parts[2], "pid"))) < 0) {
            return null;
        }
        if (("running".equals(strExactValue) && pid == 0) || ("stopped".equals(strExactValue) && pid != 0)) {
            return null;
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        int i = 0;
        while (true) {
            String[] strArr = HOOK_IDS;
            if (i < strArr.length) {
                String strExactValue2 = exactValue(parts[i + 3], strArr[i]);
                int iIndexOf = strExactValue2 == null ? -1 : strExactValue2.indexOf(58);
                if (iIndexOf <= 0 || iIndexOf == strExactValue2.length() - 1) {
                    return null;
                }
                String strSubstring = strExactValue2.substring(0, iIndexOf);
                if ((!"active".equals(strSubstring) && !"waiting".equals(strSubstring) && !"injecting".equals(strSubstring) && !"failed".equals(strSubstring) && !"disabled".equals(strSubstring) && !EnvironmentCompat.MEDIA_UNKNOWN.equals(strSubstring)) || (pid2 = parsePid(strExactValue2.substring(iIndexOf + 1))) < 0) {
                    return null;
                }
                linkedHashMap.put(strArr[i], new Entry(strSubstring, pid2));
                i++;
            } else {
                return new Snapshot(strExactValue, pid, linkedHashMap);
            }
        }
    }

    private static String exactValue(String str, String str2) {
        if (str == null) {
            return null;
        }
        String str3 = str2 + "=";
        if (str.startsWith(str3)) {
            return str.substring(str3.length());
        }
        return null;
    }

    private static int parsePid(String str) {
        if (str != null && str.matches("[0-9]{1,10}")) {
            try {
                long j = Long.parseLong(str);
                if (j <= 2147483647L) {
                    return (int) j;
                }
            } catch (NumberFormatException unused) {
            }
        }
        return -1;
    }

    private static final class Entry {
        final int pid;
        final String state;

        Entry(String str, int i) {
            this.state = str;
            this.pid = i;
        }
    }

    private static final class Snapshot {
        final Map<String, Entry> hooks;
        final int loaderPid;
        final String loaderState;

        Snapshot(String str, int i, Map<String, Entry> map) {
            this.loaderState = str;
            this.loaderPid = i;
            this.hooks = map;
        }
    }
}
