package ru.big.town.restoremode;

import android.content.SharedPreferences;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;

/* JADX INFO: loaded from: classes2.dex */
final class FullscreenAppStore {
    static final String KEY = "fullscreenApps";

    private FullscreenAppStore() {
    }

    static List<String> load(SharedPreferences sharedPreferences) {
        return decode(sharedPreferences.getString(KEY, "[]"));
    }

    static void save(SharedPreferences sharedPreferences, List<String> list) {
        sharedPreferences.edit().putString(KEY, encode(list)).apply();
    }

    static String snapshotCsv(SharedPreferences sharedPreferences) {
        return TextUtils.join(",", load(sharedPreferences));
    }

    static List<String> decode(String str) {
        ArrayList arrayList = new ArrayList();
        try {
            if (str == null) {
                str = "[]";
            }
            JSONArray jSONArray = new JSONArray(str);
            for (int i = 0; i < jSONArray.length(); i++) {
                String strTrim = jSONArray.optString(i, "").trim();
                if (!strTrim.isEmpty() && !arrayList.contains(strTrim)) {
                    arrayList.add(strTrim);
                }
            }
        } catch (Exception unused) {
        }
        return arrayList;
    }

    static String encode(List<String> list) {
        JSONArray jSONArray = new JSONArray();
        if (list != null) {
            for (String str : list) {
                if (str != null && !str.trim().isEmpty()) {
                    jSONArray.put(str.trim());
                }
            }
        }
        return jSONArray.toString();
    }
}
