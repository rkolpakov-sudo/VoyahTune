package ru.big.town.restoremode;

import android.content.SharedPreferences;

/* JADX INFO: loaded from: classes2.dex */
final class DockLongPressAction {
    private DockLongPressAction() {
    }

    static String resolve(SharedPreferences sharedPreferences, int i) {
        if (sharedPreferences.getString("dockOverride" + i, "").isEmpty()) {
            return "none";
        }
        return normalize(sharedPreferences.getString("dockOverride" + i + "LongAction", null), sharedPreferences.getInt(new StringBuilder("dockOverride").append(i).append("Split").toString(), -1) >= 0);
    }

    static String normalize(String str, boolean z) {
        if (str == null) {
            return z ? TileOrderStore.Tile.TYPE_SPLIT : "none";
        }
        return ("cluster".equals(str) || TileOrderStore.Tile.TYPE_SPLIT.equals(str)) ? str : "none";
    }
}
