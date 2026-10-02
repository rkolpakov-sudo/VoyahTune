package ru.big.town.anative;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
final class SteeringActionSequence {
    static final String PREFIX = "steer-actions-v1:";

    private SteeringActionSequence() {
    }

    static List<String> decode(String str) {
        if (str == null || str.isEmpty() || "none".equals(str)) {
            return new ArrayList();
        }
        if (!str.startsWith(PREFIX)) {
            return new ArrayList(Collections.singletonList(str));
        }
        ArrayList arrayList = new ArrayList();
        int length = PREFIX.length();
        while (length < str.length()) {
            int iIndexOf = str.indexOf(58, length);
            if (iIndexOf <= length) {
                return new ArrayList();
            }
            try {
                int i = Integer.parseInt(str.substring(length, iIndexOf));
                int i2 = iIndexOf + 1;
                int i3 = i2 + i;
                if (i <= 0 || i3 > str.length()) {
                    return new ArrayList();
                }
                String strSubstring = str.substring(i2, i3);
                if (!"none".equals(strSubstring)) {
                    arrayList.add(strSubstring);
                }
                length = i3;
            } catch (NumberFormatException unused) {
                return new ArrayList();
            }
        }
        return arrayList;
    }

    static boolean contains(String str, String str2) {
        return str2 != null && decode(str).contains(str2);
    }

    static byte[] parseCustomCan(String str) {
        if (str == null || !str.startsWith("can:")) {
            return null;
        }
        String strSubstring = str.substring("can:".length());
        if (strSubstring.length() != 20) {
            return null;
        }
        byte[] bArr = new byte[10];
        for (int i = 0; i < strSubstring.length(); i += 2) {
            int iDigit = Character.digit(strSubstring.charAt(i), 16);
            int iDigit2 = Character.digit(strSubstring.charAt(i + 1), 16);
            if (iDigit < 0 || iDigit2 < 0) {
                return null;
            }
            bArr[i / 2] = (byte) ((iDigit << 4) | iDigit2);
        }
        return bArr;
    }
}
