package ru.big.town.restoremode;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;

public class RestoreModeContentProvider extends ContentProvider {
    private SharedPreferences sharedPreferences;
    public RestoreModeContentProvider() {
    }

    /** Неподдерживаемая операция: 0 = ничего не удалено (контракт — см. {@link #insert}). */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return "vnd.android.cursor.dir/users";
    }

    /**
     * Провайдер — однострочный снимок DrivePreferences: строк для вставки нет. Поддержаны только
     * {@link #query} (чтение), {@link #update} (whitelist-запись режимов) и {@link #call}
     * (root-публикация hook-status от load.bin); insert/delete не используются ни одним
     * клиентом (проверено: Native, RestoreMode, Frida-скрипты, Packaging-тесты).
     * null = «строка не вставлена» по контракту ContentProvider — без исключений,
     * вызывающий не падает при обращении к неподдерживаемой операции.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public boolean onCreate() {
        sharedPreferences = getContext().getSharedPreferences("DrivePreferences", Context.MODE_PRIVATE);
        return true;
    }

    /**
     * Root-only, state-change delivery from {@code /data/local/bin/load.bin}. The CLI never runs on
     * a permanent cadence: load.bin calls it after its bounded status record changes and allows at
     * most three delivery attempts for that revision. SharedPreferences uses Android's AtomicFile
     * implementation, so the diagnostics screen never sees a torn value.
     */
    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        if (!HookStatusContract.METHOD_PUBLISH.equals(method)) {
            return super.call(method, arg, extras);
        }
        if (Binder.getCallingUid() != 0) {
            throw new SecurityException("Hook status may only be published by the root loader");
        }
        Bundle result = new Bundle();
        if (!HookStatusContract.isValidPayload(arg) || getContext() == null) {
            result.putBoolean("stored", false);
            return result;
        }
        boolean stored = getContext()
                .getSharedPreferences(HookStatusContract.PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(HookStatusContract.PAYLOAD_KEY, arg)
                .commit();
        result.putBoolean("stored", stored);
        return result;
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
        // Opt-out setting: upgrades without the key keep the historical remember-last behaviour.
        boolean driveRememberLast     = sharedPreferences.getBoolean("driveRememberLast",     true);
        boolean energyRememberLast    = sharedPreferences.getBoolean("energyRememberLast",    true);
        boolean recycleRememberLast   = sharedPreferences.getBoolean("recycleRememberLast",   true);
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
        boolean fragranceEnabled        = sharedPreferences.getBoolean(
                FragranceSettings.ENABLED, FragranceSettings.DEFAULT_ENABLED);
        int fragranceTaste          = FragranceSettings.normalizeTaste(sharedPreferences.getInt(
                FragranceSettings.TASTE, FragranceSettings.DEFAULT_TASTE));
        int fragranceDuration       = FragranceSettings.normalizeDuration(sharedPreferences.getInt(
                FragranceSettings.DURATION, FragranceSettings.DEFAULT_DURATION));
        int fragranceIntensity      = FragranceSettings.normalizeIntensity(sharedPreferences.getInt(
                FragranceSettings.INTENSITY, FragranceSettings.DEFAULT_INTENSITY));
        boolean apolloTlcEnabled        = sharedPreferences.getBoolean(
                ApolloSettings.TLC, ApolloSettings.DEFAULT_ENABLED);
        boolean apolloTrafficLightsEnabled = sharedPreferences.getBoolean(
                ApolloSettings.TRAFFIC_LIGHTS, ApolloSettings.DEFAULT_ENABLED);
        boolean apolloGreenSoundEnabled = sharedPreferences.getBoolean(
                ApolloSettings.GREEN_SOUND, ApolloSettings.DEFAULT_ENABLED);
        boolean apolloTrafficSignsEnabled = sharedPreferences.getBoolean(
                ApolloSettings.TRAFFIC_SIGNS, ApolloSettings.DEFAULT_ENABLED);
        boolean apolloStockUiEnabled = sharedPreferences.getBoolean(
                ApolloSettings.STOCK_UI, ApolloSettings.DEFAULT_ENABLED);

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
                FragranceSettings.ENABLED,  // 20 — opt-in восстановление ароматизатора
                FragranceSettings.TASTE,    // 21 — 1..3
                FragranceSettings.DURATION, // 22 — 0=без таймера, 1=30 мин, 2=60 мин
                FragranceSettings.INTENSITY,// 23 — 1=низкая, 2=средняя, 3=высокая
                ApolloSettings.TLC,          // 24 — желаемое состояние TLC
                ApolloSettings.TRAFFIC_LIGHTS, // 25 — распознавание светофоров
                ApolloSettings.GREEN_SOUND, // 26 — звук зелёного сигнала
                ApolloSettings.TRAFFIC_SIGNS,// 27 — распознавание дорожных знаков
                ApolloSettings.STOCK_UI,      // 28 — эмуляция подписки/экзамена для штатного UI
                "driveRememberLast",        // 29 — null/нет колонки трактуется Native как true
                "energyRememberLast",       // 30 — null/нет колонки трактуется Native как true
                "recycleRememberLast",      // 31 — null/нет колонки трактуется Native как true
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
                fragranceEnabled ? 1 : 0,
                fragranceTaste,
                fragranceDuration,
                fragranceIntensity,
                apolloTlcEnabled ? 1 : 0,
                apolloTrafficLightsEnabled ? 1 : 0,
                apolloGreenSoundEnabled ? 1 : 0,
                apolloTrafficSignsEnabled ? 1 : 0,
                apolloStockUiEnabled ? 1 : 0,
                driveRememberLast ? 1 : 0,
                energyRememberLast ? 1 : 0,
                recycleRememberLast ? 1 : 0,
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
            String rememberKey = "driveMode".equals(key) ? "driveRememberLast"
                    : "energy".equals(key) ? "energyRememberLast" : "recycleRememberLast";
            if (values.containsKey(key) && sharedPreferences.getBoolean(rememberKey, true)) {
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
