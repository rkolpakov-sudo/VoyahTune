package ru.big.town.restoremode;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Пресеты «Разделения экрана» в DrivePreferences ("splitPresets", JSON-массив). */
public class SplitStore {

    static final String KEY = "splitPresets";
    // 0=3:4, 1=1:1, 2=4:3, 3=5:2, 4=2:5
    static final String[] RATIO_LABELS = {"3:4", "1:1", "4:3", "5:2", "2:5"};

    public static class Preset {
        /** Стабильный id для межпроцессного сохранения. Индекс списка меняется при удалении пресетов. */
        public String id = UUID.randomUUID().toString();
        public String l = "", ll = "", r = "", rl = "";
        public int ratio = 1;
        /** Разрешено тянуть делитель и менять пропорцию прямо в сплите. По умолчанию выключено. */
        public boolean resizable = false;
        /** Доля левого окна 0..1, выставленная рукой. 0 = не задана, берём фиксированную ratio. */
        public float split = 0f;
        public boolean ready() { return !l.isEmpty() && !r.isEmpty(); }
    }

    /** Доля левого окна для пресета: сохранённая рукой, иначе из фиксированной пропорции. */
    public static float leftFraction(Preset ps) {
        if (ps.split > 0.05f && ps.split < 0.95f) return ps.split;
        switch (ps.ratio) {
            case 0:  return 3f / 7f;   // 3:4
            case 2:  return 4f / 7f;   // 4:3
            case 3:  return 5f / 7f;   // 5:2
            case 4:  return 2f / 7f;   // 2:5
            default: return 0.5f;      // 1:1
        }
    }

    static List<Preset> load(SharedPreferences p) {
        List<Preset> out = new ArrayList<>();
        boolean migratedIds = false;
        boolean anyCorrupt = false;
        try {
            JSONArray a = new JSONArray(p.getString(KEY, "[]"));
            for (int i = 0; i < a.length(); i++) {
                try {
                    JSONObject o = a.getJSONObject(i);
                    Preset ps = new Preset();
                    String storedId = o.optString("id", "");
                    if (!storedId.isEmpty()) ps.id = storedId;
                    else migratedIds = true;
                    ps.l  = o.optString("l", "");
                    ps.ll = o.optString("ll", "");
                    ps.r  = o.optString("r", "");
                    ps.rl = o.optString("rl", "");
                    ps.ratio = o.optInt("ratio", 1);
                    ps.resizable = o.optBoolean("resizable", false);
                    ps.split = (float) o.optDouble("split", 0d);
                    out.add(ps);
                } catch (Exception badElement) {
                    // Одна битая запись не должна обнулять остальные пресеты.
                    anyCorrupt = true;
                }
            }
        } catch (Exception ignored) {
            // Файл/строка не JSON — пустой список, без автосейва.
            return out;
        }
        // Миграция id или отбрасывание битых элементов: сохраняем очищенный список,
        // но только если исходный JSON был валиден (иначе не затираем исходник).
        if (migratedIds || anyCorrupt) save(p, out);
        return out;
    }

    static void save(SharedPreferences p, List<Preset> list) {
        JSONArray a = new JSONArray();
        for (Preset ps : list) {
            try {
                JSONObject o = new JSONObject();
                o.put("id", ps.id);
                o.put("l", ps.l);
                o.put("ll", ps.ll);
                o.put("r", ps.r);
                o.put("rl", ps.rl);
                o.put("ratio", ps.ratio);
                o.put("resizable", ps.resizable);
                o.put("split", ps.split);
                a.put(o);
            } catch (Exception ignored) {
                // Пропускаем элемент, который не сериализуется, — остальные сохраняем.
            }
        }
        p.edit().putString(KEY, a.toString()).apply();
    }
}
