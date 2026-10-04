package ru.big.town.anative;

import java.util.LinkedHashSet;

/* JADX INFO: loaded from: classes2.dex */
final class FullscreenPackagePolicy {
    private FullscreenPackagePolicy() {
    }

    static String normalizeCsv(String str) {
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet();
        if (str != null) {
            for (String str2 : str.split(",")) {
                String strTrim = str2.trim();
                if (isValidPackageName(strTrim)) {
                    linkedHashSet.add(strTrim);
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String str3 : linkedHashSet) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(str3);
        }
        return sb.toString();
    }

    static boolean contains(String str, String str2) {
        if (isValidPackageName(str2) && str != null && !str.isEmpty()) {
            for (String str3 : str.split(",")) {
                if (str2.equals(str3)) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean requiresAccessibilityService(boolean z, boolean z2, String str) {
        if (z || z2) {
            return true;
        }
        return (str == null || str.isEmpty()) ? false : true;
    }

    static boolean shouldShowOverlay(boolean z, String str, String str2) {
        return z || contains(str, str2);
    }

    private static boolean isValidPackageName(String str) {
        return str != null && str.indexOf(46) > 0 && str.matches("[A-Za-z0-9_.]+");
    }
}
