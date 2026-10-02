package ru.big.town.restoremode;

import android.content.SharedPreferences;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class AppDpiStore {
    static final String KEY = "appDpi";

    static int get(SharedPreferences sharedPreferences, String str) {
        if (str != null && !str.isEmpty()) {
            try {
                return new JSONObject(sharedPreferences.getString(KEY, "{}")).optInt(str, 0);
            } catch (Exception unused) {
            }
        }
        return 0;
    }

    static void set(SharedPreferences sharedPreferences, String str, int i) {
        if (str == null || str.isEmpty()) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(sharedPreferences.getString(KEY, "{}"));
            if (i > 0) {
                jSONObject.put(str, i);
            } else {
                jSONObject.remove(str);
            }
            sharedPreferences.edit().putString(KEY, jSONObject.toString()).apply();
        } catch (Exception unused) {
        }
    }

    static String snapshotJson(SharedPreferences sharedPreferences) {
        String string = sharedPreferences.getString(KEY, "{}");
        return (string == null || string.isEmpty()) ? "{}" : string;
    }
}
