package ru.big.town.restoremode;

import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class SplitStore {
    static final String KEY = "splitPresets";
    static final String[] RATIO_LABELS = {"3:4", "1:1", "4:3", "5:2", "2:5"};

    public static class Preset {
        public String id = UUID.randomUUID().toString();
        public String l = "";
        public String ll = "";
        public String r = "";
        public String rl = "";
        public int ratio = 1;
        public boolean resizable = false;
        public float split = 0.0f;

        public boolean ready() {
            return (this.l.isEmpty() || this.r.isEmpty()) ? false : true;
        }
    }

    public static float leftFraction(Preset preset) {
        if (preset.split > 0.05f && preset.split < 0.95f) {
            return preset.split;
        }
        int i = preset.ratio;
        if (i == 0) {
            return 0.42857143f;
        }
        if (i == 2) {
            return 0.5714286f;
        }
        if (i != 3) {
            return i != 4 ? 0.5f : 0.2857143f;
        }
        return 0.71428573f;
    }

    static List<Preset> load(SharedPreferences sharedPreferences) {
        boolean z;
        ArrayList arrayList = new ArrayList();
        boolean z2 = false;
        try {
            JSONArray jSONArray = new JSONArray(sharedPreferences.getString(KEY, "[]"));
            z = false;
            for (int i = 0; i < jSONArray.length(); i++) {
                try {
                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                    Preset preset = new Preset();
                    String strOptString = jSONObject.optString("id", "");
                    if (strOptString.isEmpty()) {
                        z = true;
                    } else {
                        preset.id = strOptString;
                    }
                    preset.l = jSONObject.optString("l", "");
                    preset.ll = jSONObject.optString("ll", "");
                    preset.r = jSONObject.optString("r", "");
                    preset.rl = jSONObject.optString("rl", "");
                    preset.ratio = jSONObject.optInt("ratio", 1);
                    preset.resizable = jSONObject.optBoolean("resizable", false);
                    preset.split = (float) jSONObject.optDouble(TileOrderStore.Tile.TYPE_SPLIT, 0.0d);
                    arrayList.add(preset);
                } catch (Exception unused) {
                    z2 = z;
                    z = z2;
                }
            }
        } catch (Exception unused2) {
        }
        if (z) {
            save(sharedPreferences, arrayList);
        }
        return arrayList;
    }

    static void save(SharedPreferences sharedPreferences, List<Preset> list) {
        JSONArray jSONArray = new JSONArray();
        try {
            for (Preset preset : list) {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("id", preset.id);
                jSONObject.put("l", preset.l);
                jSONObject.put("ll", preset.ll);
                jSONObject.put("r", preset.r);
                jSONObject.put("rl", preset.rl);
                jSONObject.put("ratio", preset.ratio);
                jSONObject.put("resizable", preset.resizable);
                jSONObject.put(TileOrderStore.Tile.TYPE_SPLIT, preset.split);
                jSONArray.put(jSONObject);
            }
        } catch (Exception unused) {
        }
        sharedPreferences.edit().putString(KEY, jSONArray.toString()).apply();
    }
}
