package ru.big.town.restoremode;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

public class RestoreModeContentProvider extends ContentProvider {
    private SharedPreferences sharedPreferences;
    public RestoreModeContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return "vnd.android.cursor.dir/users";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public boolean onCreate() {
        sharedPreferences = getContext().getSharedPreferences("DrivePreferences", Context.MODE_PRIVATE);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.i("$$$", "QUERY1");
        // Только локальные переменные: поля-состояние между binder-потоками перемешивали колонки.
        String driveMode = sharedPreferences.getString("driveMode", "INDIVIDUAL");
        String energy = sharedPreferences.getString("energy", "SREV");
        String recycle = sharedPreferences.getString("recycle", "LOW");
        String customCommand = sharedPreferences.getString("customCommand", "");
        int customCommandCount = sharedPreferences.getInt("customCommandCount", 1);
        boolean autoLight = sharedPreferences.getBoolean("autoLight", false);
        boolean driveEnabled          = sharedPreferences.getBoolean("driveEnabled",          false);
        boolean recycleEnabled        = sharedPreferences.getBoolean("recycleEnabled",        false);
        boolean energyEnabled         = sharedPreferences.getBoolean("energyEnabled",         false);
        int lightSensorThreshold    = sharedPreferences.getInt("lightSensorThreshold",    3);
        int lightSensorThresholdOff = sharedPreferences.getInt("lightSensorThresholdOff", 5);
        boolean disablePedestrianSound  = sharedPreferences.getBoolean("disablePedestrianSound", false);
        boolean forcedEv                = sharedPreferences.getBoolean("forcedEv", false);
        boolean debugMode               = sharedPreferences.getBoolean("debugMode",              false);
        boolean wiperColdMode           = sharedPreferences.getBoolean("wiperColdMode",          false);
        String customCommandStarButton1 = sharedPreferences.getString("customCommandStarButton1", "");
        String customCommandStarButton2 = sharedPreferences.getString("customCommandStarButton2", "");
        boolean autoLaunchOnWake        = sharedPreferences.getBoolean("autoLaunchOnWake",         false);
        boolean batteryHeatAuto         = sharedPreferences.getBoolean("batteryHeatAuto",          false);
        boolean pauseMediaOnDoor        = sharedPreferences.getBoolean("pauseMediaOnDoor",         false);

        MatrixCursor cursor = new MatrixCursor(new String[]{
                "driveMode",               // 0
                "energy",                  // 1
                "recycle",                 // 2
                "customCommand",           // 3
                "customCommandCount",      // 4
                "autoLight",               // 5
                "driveEnabled",            // 6
                "recycleEnabled",          // 7
                "energyEnabled",           // 8
                "lightSensorThreshold",    // 9
                "lightSensorThresholdOff", // 10
                "disablePedestrianSound",  // 11
                "debugMode",               // 12
                "wiperColdMode",           // 13
                "customCommandStarButton1",// 14
                "customCommandStarButton2",// 15
                "autoLaunchOnWake",        // 16
                "batteryHeatAuto",         // 17
                "pauseMediaOnDoor",        // 18
                "forcedEv",                // 19 — форсированный электрорежим
        });

        cursor.addRow(new Object[]{
                driveMode, energy, recycle, customCommand, customCommandCount,
                autoLight ? 1 : 0,
                driveEnabled   ? 1 : 0,
                recycleEnabled ? 1 : 0,
                energyEnabled  ? 1 : 0,
                lightSensorThreshold,
                lightSensorThresholdOff,
                disablePedestrianSound ? 1 : 0,
                debugMode ? 1 : 0,
                wiperColdMode ? 1 : 0,
                customCommandStarButton1,
                customCommandStarButton2,
                autoLaunchOnWake ? 1 : 0,
                batteryHeatAuto ? 1 : 0,
                pauseMediaOnDoor ? 1 : 0,
                forcedEv ? 1 : 0,
        });
       return cursor;

    }

    /**
     * Разрешаем записывать режимы и бинарные настройки, доступные с кнопок руля, в prefs DrivePreferences — тот же
     * источник истины, что читают query() и UI VoyahTune, и что восстанавливает Native на пробуждении.
     * Нужно, чтобы смена режима кнопкой руля (и внешняя смена) синхронизировала «последний активированный»
     * режим сюда → он переживёт пробуждение и отразится в настройках. Пишет Native (см.
     * MainActivity.persistSavedMode). Прочие ключи игнорируем (провайдер остаётся почти read-only).
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        if (values == null || sharedPreferences == null) return 0;
        SharedPreferences.Editor e = sharedPreferences.edit();
        int n = 0;
        for (String key : new String[]{"driveMode", "energy", "recycle"}) {
            if (values.containsKey(key)) {
                String v = values.getAsString(key);
                if (v != null && !v.isEmpty()) { e.putString(key, v); n++; Log.i("$$$", "provider UPDATE " + key + "=" + v); }
            }
        }
        for (String key : new String[]{"forcedEv", "disablePedestrianSound"}) {
            if (values.containsKey(key)) {
                Boolean v = values.getAsBoolean(key);
                if (v != null) { e.putBoolean(key, v); n++; Log.i("$$$", "provider UPDATE " + key + "=" + v); }
            }
        }
        if (n > 0) e.apply();
        return n;
    }
}
