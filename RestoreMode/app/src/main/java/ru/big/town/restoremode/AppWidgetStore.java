package ru.big.town.restoremode;

import android.content.SharedPreferences;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.big.town.common.SuspensionWidgetProtocol;

/* JADX INFO: loaded from: classes2.dex */
class AppWidgetStore {
    static final int DEFAULT_DPI = 0;
    static final int DEFAULT_HEIGHT = 4;
    static final int DEFAULT_WIDTH = 2;
    static final int[] DPI_VALUES = {0, 120, 140, 160, 180, 200, 213, 240, 260, 280, 300, 320, 360};
    private static final String KEY = "appWidgets";
    static final int MAX_WIDGETS = 20;

    AppWidgetStore() {
    }

    static class Profile {
        int dpi;
        String packageName;

        Profile(String str, int i) {
            this.packageName = str;
            this.dpi = AppWidgetStore.normalizeDpi(i);
        }
    }

    static class Entry {
        boolean autoStart;
        int autoStartDelay;
        int dpi;
        int height;
        final String id;
        String packageName;
        final List<Profile> profiles;
        int selectedProfile;
        int width;

        Entry(String str, String str2) {
            this.width = 2;
            this.height = 4;
            this.dpi = 0;
            this.autoStartDelay = 3;
            this.profiles = new ArrayList();
            this.id = str;
            this.packageName = str2;
        }

        Entry(String str, String str2, int i, int i2, int i3, boolean z, int i4) {
            this(str, str2);
            this.width = AppWidgetStore.clampWidth(i);
            this.height = AppWidgetStore.clampHeight(i2);
            this.dpi = AppWidgetStore.normalizeDpi(i3);
            this.autoStart = z;
            this.autoStartDelay = Math.max(1, Math.min(60, i4));
            this.profiles.add(new Profile(str2, this.dpi));
        }

        Profile selected() {
            ensureProfiles();
            int iMax = Math.max(0, Math.min(this.selectedProfile, this.profiles.size() - 1));
            this.selectedProfile = iMax;
            return this.profiles.get(iMax);
        }

        void ensureProfiles() {
            if (this.profiles.isEmpty()) {
                this.profiles.add(new Profile(this.packageName, this.dpi));
            }
            int iMax = Math.max(0, Math.min(this.selectedProfile, this.profiles.size() - 1));
            this.selectedProfile = iMax;
            Profile profile = this.profiles.get(iMax);
            this.packageName = profile.packageName;
            this.dpi = AppWidgetStore.normalizeDpi(profile.dpi);
        }
    }

    /* JADX WARN: Code duplicated, block: B:20:0x0097  */
    /* JADX WARN: Code duplicated, block: B:8:0x0028  */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r13v2 */
    /* JADX WARN: Type inference failed for: r13v5 */
    /* JADX WARN: Type inference failed for: r13v6 */
    /* JADX WARN: Type inference failed for: r5v2 */
    /* JADX WARN: Type inference failed for: r5v3, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r5v9 */
    /* JADX WARN: Type inference failed for: r7v2, types: [org.json.JSONObject] */
    static List<Entry> load(SharedPreferences sharedPreferences) {
        int r13 = 0;
        ArrayList arrayList = new ArrayList();
        try {
            JSONArray jSONArray = new JSONArray(sharedPreferences.getString(KEY, "[]"));
            int r5 = 0;
            int i = 0;
            while (i < jSONArray.length()) {
                JSONObject OptJSONObject = jSONArray.optJSONObject(i);
                if (OptJSONObject == null) {
                    r13 = r5;
                } else {
                    String strOptString = OptJSONObject.optString("id", "");
                    String strOptString2 = OptJSONObject.optString("package", "");
                    if (strOptString.isEmpty() || strOptString2.isEmpty()) {
                        r13 = r5;
                    } else {
                        arrayList.add(new Entry(strOptString, strOptString2, OptJSONObject.optInt("width", 2), OptJSONObject.optInt(SuspensionWidgetProtocol.HEIGHT, 4), OptJSONObject.optInt("dpi", r5), OptJSONObject.optBoolean("autoStart", r5 != 0), OptJSONObject.optInt("autoStartDelay", 3)));
                        Entry entry = (Entry) arrayList.get(arrayList.size() - 1);
                        entry.selectedProfile = OptJSONObject.optInt("selectedProfile", 0);
                        JSONArray jSONArrayOptJSONArray = OptJSONObject.optJSONArray("apps");
                        if (jSONArrayOptJSONArray != null) {
                            entry.profiles.clear();
                            for (int i2 = 0; i2 < jSONArrayOptJSONArray.length(); i2++) {
                                JSONObject jSONObjectOptJSONObject = jSONArrayOptJSONArray.optJSONObject(i2);
                                if (jSONObjectOptJSONObject != null) {
                                    String strOptString3 = jSONObjectOptJSONObject.optString("package", "");
                                    if (!strOptString3.isEmpty()) {
                                        entry.profiles.add(new Profile(strOptString3, jSONObjectOptJSONObject.optInt("dpi", 0)));
                                    }
                                }
                            }
                        }
                        r13 = 0;
                        entry.ensureProfiles();
                    }
                }
                i++;
                r5 = r13;
            }
        } catch (Exception unused) {
        }
        return arrayList;
    }

    static void save(SharedPreferences sharedPreferences, List<Entry> list) {
        JSONArray jSONArray = new JSONArray();
        for (Entry entry : list) {
            JSONObject jSONObject = new JSONObject();
            try {
                entry.ensureProfiles();
                jSONObject.put("id", entry.id);
                jSONObject.put("package", entry.packageName);
                jSONObject.put("width", clampWidth(entry.width));
                jSONObject.put(SuspensionWidgetProtocol.HEIGHT, clampHeight(entry.height));
                jSONObject.put("dpi", normalizeDpi(entry.dpi));
                jSONObject.put("autoStart", entry.autoStart);
                jSONObject.put("autoStartDelay", Math.max(1, Math.min(60, entry.autoStartDelay)));
                jSONObject.put("selectedProfile", entry.selectedProfile);
                JSONArray jSONArray2 = new JSONArray();
                for (Profile profile : entry.profiles) {
                    JSONObject jSONObject2 = new JSONObject();
                    jSONObject2.put("package", profile.packageName);
                    jSONObject2.put("dpi", normalizeDpi(profile.dpi));
                    jSONArray2.put(jSONObject2);
                }
                jSONObject.put("apps", jSONArray2);
                jSONArray.put(jSONObject);
            } catch (Exception unused) {
            }
        }
        sharedPreferences.edit().putString(KEY, jSONArray.toString()).apply();
    }

    static Entry add(SharedPreferences sharedPreferences, String str) {
        List<Entry> listLoad = load(sharedPreferences);
        if (listLoad.size() >= 20) {
            return null;
        }
        Entry entry = new Entry(UUID.randomUUID().toString(), str);
        listLoad.add(entry);
        save(sharedPreferences, listLoad);
        return entry;
    }

    static String designation(int i) {
        if (i < 0) {
            return "";
        }
        if (i < 26) {
            return String.valueOf((char) (i + 65));
        }
        return String.valueOf(i + 1);
    }

    static String designation(List<Entry> list, String str) {
        if (list != null && str != null) {
            for (int i = 0; i < list.size(); i++) {
                if (str.equals(list.get(i).id)) {
                    return designation(i);
                }
            }
        }
        return "";
    }

    static String designation(SharedPreferences sharedPreferences, String str) {
        return designation(load(sharedPreferences), str);
    }

    static void remove(SharedPreferences sharedPreferences, final String str) {
        List<Entry> listLoad = load(sharedPreferences);
        listLoad.removeIf(new Predicate() { // from class: ru.big.town.restoremode.AppWidgetStore$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((AppWidgetStore.Entry) obj).id.equals(str);
            }
        });
        save(sharedPreferences, listLoad);
    }

    static Entry find(SharedPreferences sharedPreferences, String str) {
        for (Entry entry : load(sharedPreferences)) {
            if (entry.id.equals(str)) {
                return entry;
            }
        }
        return null;
    }

    static void update(SharedPreferences sharedPreferences, Entry entry) {
        List<Entry> listLoad = load(sharedPreferences);
        for (Entry entry2 : listLoad) {
            if (entry2.id.equals(entry.id)) {
                entry.ensureProfiles();
                entry2.packageName = entry.packageName;
                entry2.width = clampWidth(entry.width);
                entry2.height = clampHeight(entry.height);
                entry2.dpi = normalizeDpi(entry.dpi);
                entry2.autoStart = entry.autoStart;
                entry2.autoStartDelay = Math.max(1, Math.min(60, entry.autoStartDelay));
                entry2.selectedProfile = entry.selectedProfile;
                entry2.profiles.clear();
                for (Profile profile : entry.profiles) {
                    entry2.profiles.add(new Profile(profile.packageName, profile.dpi));
                }
                break;
            }
        }
        save(sharedPreferences, listLoad);
    }

    static int clampWidth(int i) {
        return Math.max(1, Math.min(12, i));
    }

    static int clampHeight(int i) {
        return Math.max(1, Math.min(5, i));
    }

    static int normalizeDpi(int i) {
        for (int i2 : DPI_VALUES) {
            if (i2 == i) {
                return i;
            }
        }
        return 0;
    }

    static void addProfile(Entry entry, String str, int i) {
        entry.ensureProfiles();
        Iterator<Profile> it = entry.profiles.iterator();
        while (it.hasNext()) {
            if (it.next().packageName.equals(str)) {
                return;
            }
        }
        entry.profiles.add(new Profile(str, i));
    }

    static void removeProfile(Entry entry, int i) {
        entry.ensureProfiles();
        if (entry.profiles.size() <= 1 || i < 0 || i >= entry.profiles.size()) {
            return;
        }
        entry.profiles.remove(i);
        if (entry.selectedProfile >= entry.profiles.size()) {
            entry.selectedProfile = entry.profiles.size() - 1;
        }
        entry.ensureProfiles();
    }
}
