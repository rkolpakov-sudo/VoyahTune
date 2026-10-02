package ru.big.town.restoremode;

import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
final class SteeringCanCommandPolicy {
    static final int HEX_LENGTH = 20;

    private SteeringCanCommandPolicy() {
    }

    static String compact(String str) {
        return str == null ? "" : str.toLowerCase(Locale.ROOT).replaceAll("[^0-9a-f]", "");
    }

    static String format(String str) {
        String strCompact = compact(str);
        StringBuilder sb = new StringBuilder(strCompact.length() + (strCompact.length() / 2));
        for (int i = 0; i < strCompact.length(); i++) {
            if (i > 0 && i % 2 == 0) {
                sb.append(' ');
            }
            sb.append(strCompact.charAt(i));
        }
        return sb.toString();
    }

    static boolean isValid(String str) {
        return compact(str).length() == 20;
    }

    static String actionId(String str) {
        String strCompact = compact(str);
        if (strCompact.length() != 20) {
            throw new IllegalArgumentException("CAN command must contain exactly 10 bytes");
        }
        return "can:" + strCompact;
    }
}
