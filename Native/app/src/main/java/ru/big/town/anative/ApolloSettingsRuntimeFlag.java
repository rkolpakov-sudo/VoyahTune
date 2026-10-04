package ru.big.town.anative;

import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes2.dex */
final class ApolloSettingsRuntimeFlag {
    private static final Pattern BOOT_ID = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    static final String FILE_NAME = "apollo_settings_runtime.v1";
    static final int MAX_PAYLOAD_CHARS = 192;

    private ApolloSettingsRuntimeFlag() {
    }

    static String encodeEnabled(String str) {
        String strNormalizeBootId = normalizeBootId(str);
        if (strNormalizeBootId == null) {
            throw new IllegalArgumentException("invalid boot id");
        }
        return "v=1\nboot=" + strNormalizeBootId + "\nenabled=1\n";
    }

    static boolean isEnabledForBoot(String str, String str2) {
        String strNormalizeBootId = normalizeBootId(str2);
        if (str != null && strNormalizeBootId != null && str.length() != 0 && str.length() <= MAX_PAYLOAD_CHARS) {
            HashSet hashSet = new HashSet();
            String[] strArrSplit = str.split("\\n", -1);
            int length = strArrSplit.length;
            String str3 = null;
            int i = 0;
            String str4 = null;
            String str5 = null;
            while (true) {
                if (i < length) {
                    String str6 = strArrSplit[i];
                    if (!str6.isEmpty()) {
                        int iIndexOf = str6.indexOf(61);
                        if (iIndexOf <= 0 || iIndexOf != str6.lastIndexOf(61)) {
                            return false;
                        }
                        String strSubstring = str6.substring(0, iIndexOf);
                        String strSubstring2 = str6.substring(iIndexOf + 1);
                        if (!hashSet.add(strSubstring)) {
                            return false;
                        }
                        strSubstring.hashCode();
                        switch (strSubstring) {
                            case "enabled":
                                str5 = strSubstring2;
                                break;
                            case "v":
                                str3 = strSubstring2;
                                break;
                            case "boot":
                                str4 = strSubstring2;
                                break;
                            default:
                                return false;
                        }
                    }
                    i++;
                } else if (hashSet.size() == 3 && "1".equals(str3) && strNormalizeBootId.equals(str4) && "1".equals(str5)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    static String normalizeBootId(String str) {
        if (str == null) {
            return null;
        }
        String lowerCase = str.trim().toLowerCase(Locale.ROOT);
        if (BOOT_ID.matcher(lowerCase).matches()) {
            return lowerCase;
        }
        return null;
    }
}
