package ru.big.town.restoremode;

import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
final class SteeringActionStore {
    static final String PREFIX = "steer-actions-v1:";

    private SteeringActionStore() {
    }

    static List<String> load(SharedPreferences sharedPreferences, String str) {
        return decode(sharedPreferences.getString(str, "none"));
    }

    static void save(SharedPreferences sharedPreferences, String str, List<String> list) {
        sharedPreferences.edit().putString(str, encode(list)).apply();
    }

    static String encode(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "none";
        }
        StringBuilder sb = new StringBuilder(PREFIX);
        int i = 0;
        for (String str : list) {
            if (str != null && !str.isEmpty() && !"none".equals(str)) {
                sb.append(str.length()).append(':').append(str);
                i++;
            }
        }
        if (i == 0) {
            return "none";
        }
        return sb.toString();
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
}
