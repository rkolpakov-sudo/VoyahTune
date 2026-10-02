package ru.big.town.restoremode;

import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public final class DialWidgetStore {
    private static final String KEY = "dialWidgets";
    public static final int MAX_COUNT = 100;

    public static class Entry {
        public String id = UUID.randomUUID().toString();
        public String name = "";
        public String number = "";
    }

    private DialWidgetStore() {
    }

    static List<Entry> load(SharedPreferences sharedPreferences) {
        ArrayList arrayList = new ArrayList();
        try {
            JSONArray jSONArray = new JSONArray(sharedPreferences.getString(KEY, "[]"));
            for (int i = 0; i < jSONArray.length() && arrayList.size() < 100; i++) {
                JSONObject jSONObject = jSONArray.getJSONObject(i);
                Entry entry = new Entry();
                String strOptString = jSONObject.optString("id", "");
                if (strOptString.isEmpty()) {
                    strOptString = UUID.randomUUID().toString();
                }
                entry.id = strOptString;
                entry.name = jSONObject.optString("name", "");
                entry.number = jSONObject.optString("number", "");
                arrayList.add(entry);
            }
        } catch (Exception unused) {
        }
        return arrayList;
    }

    static void save(SharedPreferences sharedPreferences, List<Entry> list) {
        JSONArray jSONArray = new JSONArray();
        for (Entry entry : list) {
            if (jSONArray.length() >= 100) {
                break;
            }
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("id", entry.id);
                jSONObject.put("name", entry.name);
                jSONObject.put("number", entry.number);
                jSONArray.put(jSONObject);
            } catch (Exception unused) {
            }
        }
        sharedPreferences.edit().putString(KEY, jSONArray.toString()).apply();
    }
}
