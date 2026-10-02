package ru.big.town.restoremode;

import android.content.SharedPreferences;

/* JADX INFO: loaded from: classes2.dex */
class TileSizeStore {
    private static final String HEIGHT_KEY_PREFIX = "tileHeight_";
    static final int LAUNCH_APPS_DEFAULT_HEIGHT = 3;
    static final int LAUNCH_APPS_DEFAULT_WIDTH = 2;
    static final String LAUNCH_APPS_WIDGET_ID = "launchAppsWidget";
    static final int SUSPENSION_DEFAULT_HEIGHT = 3;
    static final int SUSPENSION_DEFAULT_WIDTH = 4;
    static final String SUSPENSION_WIDGET_ID = "suspensionWidget";
    private static final String WIDTH_KEY_PREFIX = "tileWidth_";

    private TileSizeStore() {
    }

    static int[] dimensions(SharedPreferences sharedPreferences, String str, int i, int i2) {
        return new int[]{width(sharedPreferences, str, i), height(sharedPreferences, str, i2)};
    }

    static int width(SharedPreferences sharedPreferences, String str, int i) {
        return (sharedPreferences == null || str == null) ? i : AppWidgetStore.clampWidth(sharedPreferences.getInt(WIDTH_KEY_PREFIX + str, i));
    }

    static int height(SharedPreferences sharedPreferences, String str, int i) {
        return (sharedPreferences == null || str == null) ? i : AppWidgetStore.clampHeight(sharedPreferences.getInt(HEIGHT_KEY_PREFIX + str, i));
    }

    static void setWidth(SharedPreferences sharedPreferences, String str, int i) {
        if (sharedPreferences == null || str == null) {
            return;
        }
        sharedPreferences.edit().putInt(WIDTH_KEY_PREFIX + str, AppWidgetStore.clampWidth(i)).apply();
    }

    static void setHeight(SharedPreferences sharedPreferences, String str, int i) {
        if (sharedPreferences == null || str == null) {
            return;
        }
        sharedPreferences.edit().putInt(HEIGHT_KEY_PREFIX + str, AppWidgetStore.clampHeight(i)).apply();
    }
}
