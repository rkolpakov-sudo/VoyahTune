package ru.big.town.restoremode;

import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;

/* JADX INFO: loaded from: classes2.dex */
class AppShortcutStore {
    private static final String KEY = "appShortcuts";

    AppShortcutStore() {
    }

    static List<String> load(SharedPreferences sharedPreferences) {
        ArrayList arrayList = new ArrayList();
        try {
            JSONArray jSONArray = new JSONArray(sharedPreferences.getString(KEY, "[]"));
            for (int i = 0; i < jSONArray.length(); i++) {
                String strOptString = jSONArray.optString(i, "");
                if (!strOptString.isEmpty() && !arrayList.contains(strOptString)) {
                    arrayList.add(strOptString);
                }
            }
        } catch (Exception unused) {
        }
        return arrayList;
    }

    static void save(SharedPreferences sharedPreferences, List<String> list) {
        JSONArray jSONArray = new JSONArray();
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            jSONArray.put(it.next());
        }
        sharedPreferences.edit().putString(KEY, jSONArray.toString()).apply();
    }
}
