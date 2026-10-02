package ru.big.town.restoremode;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class TileOrderStore {
    private static final String KEY = "tileOrder";

    public static class Tile {
        public static final String TYPE_APP = "app";
        public static final String TYPE_APP_WIDGET = "appWidget";
        public static final String TYPE_DIAL = "dial";
        public static final String TYPE_SPLIT = "split";
        public static final String TYPE_WIDGET = "widget";
        public String id;
        public String type;

        public Tile(String str, String str2) {
            this.type = str;
            this.id = str2;
        }
    }

    static List<Tile> load(SharedPreferences sharedPreferences) {
        ArrayList arrayList = new ArrayList();
        String string = sharedPreferences.getString(KEY, "");
        if (!string.isEmpty()) {
            try {
                JSONArray jSONArray = new JSONArray(string);
                for (int i = 0; i < jSONArray.length(); i++) {
                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                    String strOptString = jSONObject.optString("type", "");
                    String strOptString2 = jSONObject.optString("id", "");
                    if (!strOptString.isEmpty() && !strOptString2.isEmpty()) {
                        arrayList.add(new Tile(strOptString, strOptString2));
                    }
                }
            } catch (Exception unused) {
            }
        } else {
            for (SplitStore.Preset preset : SplitStore.load(sharedPreferences)) {
                if (preset.ready()) {
                    arrayList.add(new Tile(Tile.TYPE_SPLIT, preset.id));
                }
            }
            Iterator<String> it = AppShortcutStore.load(sharedPreferences).iterator();
            while (it.hasNext()) {
                arrayList.add(new Tile(Tile.TYPE_APP, it.next()));
            }
            arrayList.add(new Tile("widget", "tripCard"));
            arrayList.add(new Tile("widget", "cardPowerHold"));
            arrayList.add(new Tile("widget", "cardWashMode"));
            arrayList.add(new Tile("widget", "cardAutoLight"));
            arrayList.add(new Tile("widget", "cardPedestrian"));
            arrayList.add(new Tile("widget", "cardForcedEv"));
            arrayList.add(new Tile("widget", "cardSuspensionMaintenance"));
            arrayList.add(new Tile("widget", "cardBatteryHeat"));
            arrayList.add(new Tile("widget", "suspensionWidget"));
            arrayList.add(new Tile("widget", "cardVoiceCommand"));
            arrayList.add(new Tile("widget", "launchAppsWidget"));
            if (!arrayList.isEmpty()) {
                save(sharedPreferences, arrayList);
            }
        }
        return arrayList;
    }

    static void save(SharedPreferences sharedPreferences, List<Tile> list) {
        JSONArray jSONArray = new JSONArray();
        try {
            for (Tile tile : list) {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("type", tile.type);
                jSONObject.put("id", tile.id);
                jSONArray.put(jSONObject);
            }
        } catch (Exception unused) {
        }
        sharedPreferences.edit().putString(KEY, jSONArray.toString()).apply();
    }

    static boolean exists(SharedPreferences sharedPreferences, Tile tile, PackageManager packageManager) {
        if (Tile.TYPE_SPLIT.equals(tile.type)) {
            for (SplitStore.Preset preset : SplitStore.load(sharedPreferences)) {
                if (preset.id.equals(tile.id) && preset.ready()) {
                    return true;
                }
            }
            return false;
        }
        if (Tile.TYPE_APP.equals(tile.type)) {
            if (!AppShortcutStore.load(sharedPreferences).contains(tile.id)) {
                return false;
            }
            try {
                packageManager.getApplicationInfo(tile.id, 0);
                return true;
            } catch (Exception unused) {
                return false;
            }
        }
        if ("widget".equals(tile.type)) {
            return isKnownWidget(tile.id);
        }
        if (Tile.TYPE_APP_WIDGET.equals(tile.type)) {
            AppWidgetStore.Entry entryFind = AppWidgetStore.find(sharedPreferences, tile.id);
            if (entryFind == null) {
                return false;
            }
            try {
                packageManager.getApplicationInfo(entryFind.packageName, 0);
                return true;
            } catch (Exception unused2) {
                return false;
            }
        }
        if (Tile.TYPE_DIAL.equals(tile.type)) {
            Iterator<DialWidgetStore.Entry> it = DialWidgetStore.load(sharedPreferences).iterator();
            while (it.hasNext()) {
                if (it.next().id.equals(tile.id)) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean isKnownWidget(String str) {
        return str.equals("suspensionWidget") || str.equals("tripCard") || str.equals("cardPowerHold") || str.equals("cardWashMode") || str.equals("cardAutoLight") || str.equals("cardPedestrian") || str.equals("cardForcedEv") || str.equals("cardSuspensionMaintenance") || str.equals("cardBatteryHeat") || str.equals("cardVoiceCommand") || str.equals("cardSettings") || str.equals("cardAndroidSettings") || str.equals("launchAppsWidget");
    }

    static void sync(SharedPreferences sharedPreferences, PackageManager packageManager) {
        List<Tile> listLoad = load(sharedPreferences);
        List<DialWidgetStore.Entry> listLoad2 = DialWidgetStore.load(sharedPreferences);
        if (listLoad2.isEmpty()) {
            String string = sharedPreferences.getString("dialWidgetNumber", "");
            if (!string.isEmpty()) {
                DialWidgetStore.Entry entry = new DialWidgetStore.Entry();
                entry.name = sharedPreferences.getString("dialWidgetContactName", "Набрать номер");
                entry.number = string;
                listLoad2.add(entry);
                DialWidgetStore.save(sharedPreferences, listLoad2);
            }
        }
        for (int size = listLoad.size() - 1; size >= 0; size--) {
            if (!exists(sharedPreferences, listLoad.get(size), packageManager)) {
                listLoad.remove(size);
            }
        }
        for (SplitStore.Preset preset : SplitStore.load(sharedPreferences)) {
            if (preset.ready()) {
                Iterator<Tile> it = listLoad.iterator();
                while (true) {
                    if (it.hasNext()) {
                        Tile next = it.next();
                        if (Tile.TYPE_SPLIT.equals(next.type) && next.id.equals(preset.id)) {
                            break;
                        }
                    } else {
                        listLoad.add(new Tile(Tile.TYPE_SPLIT, preset.id));
                        break;
                    }
                }
            }
        }
        for (String str : AppShortcutStore.load(sharedPreferences)) {
            Iterator<Tile> it2 = listLoad.iterator();
            while (true) {
                if (it2.hasNext()) {
                    Tile next2 = it2.next();
                    if (Tile.TYPE_APP.equals(next2.type) && next2.id.equals(str)) {
                        break;
                    }
                } else {
                    listLoad.add(new Tile(Tile.TYPE_APP, str));
                    break;
                }
            }
        }
        String[] strArr = {"tripCard", "cardPowerHold", "cardWashMode", "cardAutoLight", "cardPedestrian", "cardForcedEv", "cardSuspensionMaintenance", "cardBatteryHeat", "suspensionWidget", "cardSettings", "cardAndroidSettings", "cardVoiceCommand", "launchAppsWidget"};
        for (int i = 0; i < 13; i++) {
            String str2 = strArr[i];
            Iterator<Tile> it3 = listLoad.iterator();
            while (true) {
                if (it3.hasNext()) {
                    Tile next3 = it3.next();
                    if ("widget".equals(next3.type) && next3.id.equals(str2)) {
                        break;
                    }
                } else {
                    listLoad.add(new Tile("widget", str2));
                    break;
                }
            }
        }
        for (DialWidgetStore.Entry entry2 : listLoad2) {
            Iterator<Tile> it4 = listLoad.iterator();
            while (true) {
                if (it4.hasNext()) {
                    Tile next4 = it4.next();
                    if (Tile.TYPE_DIAL.equals(next4.type) && next4.id.equals(entry2.id)) {
                        break;
                    }
                } else {
                    listLoad.add(new Tile(Tile.TYPE_DIAL, entry2.id));
                    break;
                }
            }
        }
        for (AppWidgetStore.Entry entry3 : AppWidgetStore.load(sharedPreferences)) {
            Iterator<Tile> it5 = listLoad.iterator();
            while (true) {
                if (it5.hasNext()) {
                    Tile next5 = it5.next();
                    if (Tile.TYPE_APP_WIDGET.equals(next5.type) && next5.id.equals(entry3.id)) {
                        break;
                    }
                } else {
                    listLoad.add(new Tile(Tile.TYPE_APP_WIDGET, entry3.id));
                    break;
                }
            }
        }
        save(sharedPreferences, listLoad);
    }
}
