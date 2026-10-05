package ru.big.town.restoremode;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import ru.big.town.common.DriveSelectionPolicy;
import ru.big.town.common.SuspensionWidgetProtocol;

/* JADX INFO: loaded from: classes2.dex */
public class RestoreModeContentProvider extends ContentProvider {
    private SharedPreferences sharedPreferences;
    private String energy = "SREV";
    private String recycle = "LOW";
    private String customCommand = "";
    private int customCommandCount = 1;
    private boolean autoLight = false;
    private boolean driveEnabled = false;
    private boolean recycleEnabled = false;
    private boolean energyEnabled = false;
    private boolean driveRememberLast = true;
    private boolean energyRememberLast = true;
    private boolean recycleRememberLast = true;
    private int lightSensorThreshold = 3;
    private int lightSensorThresholdOff = 5;
    private boolean disablePedestrianSound = false;
    private boolean forcedEv = false;
    private boolean suspensionMaintenance = false;
    private boolean wiperColdMode = false;
    private String customCommandStarButton1 = "";
    private String customCommandStarButton2 = "";
    private boolean autoLaunchOnWake = false;
    private boolean batteryHeatAuto = false;
    private boolean pauseMediaOnDoor = false;
    private boolean fragranceEnabled = false;
    private int fragranceTaste = 1;
    private int fragranceDuration = 0;
    private int fragranceIntensity = 2;
    private boolean apolloTlcEnabled = false;
    private boolean apolloTrafficLightsEnabled = false;
    private boolean apolloGreenSoundEnabled = false;
    private boolean apolloTrafficSignsEnabled = false;

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return "vnd.android.cursor.dir/users";
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("DrivePreferences", 0);
        this.sharedPreferences = sharedPreferences;
        if (!sharedPreferences.contains("apolloStockUiEnabled") || this.sharedPreferences.edit().remove("apolloStockUiEnabled").commit()) {
            return true;
        }
        Log.e("ApolloSettings", "Unable to clear retired stock UI target");
        return true;
    }

    private void notifySavedMode(String str, String str2) {
        try {
            getContext().sendBroadcast(new Intent("ru.big.town.anative.MODE_SYNCED").setPackage(getContext().getPackageName()).putExtra("modeKey", str).putExtra("mode", str2));
        } catch (RuntimeException e) {
            Log.w("DriveSelection", "Selection saved, UI notification unavailable", e);
        }
    }

    @Override // android.content.ContentProvider
    public Bundle call(String str, String arg, Bundle bundle) {
        boolean z = false;
        if ("driveHookV2".equals(str)) {
            int callingUid = Binder.getCallingUid();
            if (callingUid != 0 && callingUid != 1000 && callingUid != Process.myUid()) {
                getContext().enforceCallingOrSelfPermission("ru.big.town.anative.permission.BIND_SET_MODES_SERVICE", "Drive hook state");
            }
            Bundle bundleHook = DriveSelectionPreferences.hook(this.sharedPreferences, "dispatchSettings".equals(arg) ? "snapshot" : arg, bundle, Settings.Global.getInt(getContext().getContentResolver(), "boot_count", -1));
            if ("dispatchSettings".equals(arg) && bundleHook.getInt("acc", -1) == 2 && SuspensionWidgetProtocol.PENDING.equals(bundleHook.getString("settingsStartup"))) {
                try {
                    z = getContext().startForegroundService(new Intent().setClassName("ru.big.town.anative", "ru.big.town.anative.SetModesService").setAction("ru.big.town.anative.ACC_RESTORE")) != null;
                } catch (RuntimeException e) {
                    Log.w("DriveSelection", "ACC settings dispatch unavailable", e);
                }
            }
            if ("dispatchSettings".equals(arg)) {
                bundleHook.putBoolean("settingsDispatched", z);
            }
            if ("user".equals(arg) && bundle != null) {
                if (bundle.containsKey("mode")) {
                    notifySavedMode("driveMode", DriveSelectionPreferences.read(this.sharedPreferences).configured);
                }
                if (bundle.containsKey("energy")) {
                    notifySavedMode("energy", this.sharedPreferences.getString("energy", "SREV"));
                }
            }
            return bundleHook;
        }
        if ("otaHealth".equals(str)) {
            int callingUid2 = Binder.getCallingUid();
            try {
                int i = getContext().getPackageManager().getApplicationInfo("ru.big.town.anative", 0).uid;
                if (callingUid2 != 0 && (callingUid2 != i || getContext().getPackageManager().checkSignatures("ru.big.town.anative", getContext().getPackageName()) != 0)) {
                    throw new SecurityException("Root or trusted Native only");
                }
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("ready", this.sharedPreferences != null);
                bundle2.putString("version", BuildConfig.VERSION_NAME);
                return bundle2;
            } catch (PackageManager.NameNotFoundException e2) {
                throw new SecurityException(e2);
            }
        }
        if (!"publishHookStatusV1".equals(str)) {
            return super.call(str, arg, bundle);
        }
        if (Binder.getCallingUid() != 0) {
            throw new SecurityException("Hook status may only be published by the root loader");
        }
        Bundle bundle3 = new Bundle();
        if (!HookStatusContract.isValidPayload(arg) || getContext() == null) {
            bundle3.putBoolean("stored", false);
            return bundle3;
        }
        boolean stored = getContext().getSharedPreferences("HookStatus", 0).edit().putString(HookStatusContract.PAYLOAD_KEY, arg).commit();
        bundle3.putBoolean("stored", stored);
        if (stored) {
            // IMP-08: boot-reconciliation — факт (payload) сверяется с манифестом сразу при
            // публикации, без открытого UI. Ошибка реконсиляции не должна ронять публикацию.
            try {
                HookGenerationRegistry.reconcile(getContext(), arg);
            } catch (RuntimeException e) {
                Log.w("HookGeneration", "reconcile failed", e);
            }
        }
        return bundle3;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        Log.i("$$$", "QUERY1");
        DriveSelectionPolicy driveSelectionPolicy = DriveSelectionPreferences.read(this.sharedPreferences);
        this.energy = DriveSelectionPreferences.energy(this.sharedPreferences);
        this.recycle = this.sharedPreferences.getString("recycle", "LOW");
        this.customCommand = this.sharedPreferences.getString("customCommand", "");
        this.customCommandCount = this.sharedPreferences.getInt("customCommandCount", 1);
        this.autoLight = this.sharedPreferences.getBoolean("autoLight", false);
        this.driveEnabled = this.sharedPreferences.getBoolean("driveEnabled", false);
        this.recycleEnabled = this.sharedPreferences.getBoolean("recycleEnabled", false);
        this.energyEnabled = this.sharedPreferences.getBoolean("energyEnabled", false);
        this.driveRememberLast = this.sharedPreferences.getBoolean("driveRememberLast", true);
        this.energyRememberLast = this.sharedPreferences.getBoolean("energyRememberLast", true);
        this.recycleRememberLast = this.sharedPreferences.getBoolean("recycleRememberLast", true);
        this.lightSensorThreshold = this.sharedPreferences.getInt("lightSensorThreshold", 3);
        this.lightSensorThresholdOff = this.sharedPreferences.getInt("lightSensorThresholdOff", 5);
        this.disablePedestrianSound = this.sharedPreferences.getBoolean("disablePedestrianSound", false);
        this.forcedEv = this.sharedPreferences.getBoolean("forcedEv", false);
        this.suspensionMaintenance = this.sharedPreferences.getBoolean("suspensionMaintenance", false);
        this.wiperColdMode = this.sharedPreferences.getBoolean("wiperColdMode", false);
        this.customCommandStarButton1 = this.sharedPreferences.getString("customCommandStarButton1", "");
        this.customCommandStarButton2 = this.sharedPreferences.getString("customCommandStarButton2", "");
        this.autoLaunchOnWake = this.sharedPreferences.getBoolean("autoLaunchOnWake", false);
        this.batteryHeatAuto = this.sharedPreferences.getBoolean("batteryHeatAuto", false);
        this.pauseMediaOnDoor = this.sharedPreferences.getBoolean("pauseMediaOnDoor", false);
        this.fragranceEnabled = this.sharedPreferences.getBoolean("fragranceEnabled", false);
        this.fragranceTaste = FragranceSettings.normalizeTaste(this.sharedPreferences.getInt("fragranceTaste", 1));
        this.fragranceDuration = FragranceSettings.normalizeDuration(this.sharedPreferences.getInt("fragranceDuration", 0));
        this.fragranceIntensity = FragranceSettings.normalizeIntensity(this.sharedPreferences.getInt("fragranceIntensity", 2));
        this.apolloTlcEnabled = this.sharedPreferences.getBoolean("apolloTlcEnabled", false);
        this.apolloTrafficLightsEnabled = this.sharedPreferences.getBoolean("apolloTrafficLightsEnabled", false);
        this.apolloGreenSoundEnabled = this.sharedPreferences.getBoolean("apolloGreenSoundEnabled", false);
        this.apolloTrafficSignsEnabled = this.sharedPreferences.getBoolean("apolloTrafficSignsEnabled", false);
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"driveMode", "energy", "recycle", "customCommand", "customCommandCount", "autoLight", "driveEnabled", "recycleEnabled", "energyEnabled", "lightSensorThreshold", "lightSensorThresholdOff", "disablePedestrianSound", "debugMode", "wiperColdMode", "customCommandStarButton1", "customCommandStarButton2", "autoLaunchOnWake", "batteryHeatAuto", "pauseMediaOnDoor", "forcedEv", "fragranceEnabled", "fragranceTaste", "fragranceDuration", "fragranceIntensity", "apolloTlcEnabled", "apolloTrafficLightsEnabled", "apolloGreenSoundEnabled", "apolloTrafficSignsEnabled", "apolloStockUiEnabled", "driveRememberLast", "energyRememberLast", "recycleRememberLast", "suspensionMaintenance", DriveSelectionPolicy.OVERRIDE, DriveSelectionPolicy.MEDIUM, DriveSelectionPolicy.CONFIGURED, DriveSelectionPolicy.CURRENT});
        matrixCursor.addRow(new Object[]{driveSelectionPolicy.effective(), this.energy, this.recycle, this.customCommand, Integer.valueOf(this.customCommandCount), Integer.valueOf(this.autoLight ? 1 : 0), Integer.valueOf(this.driveEnabled ? 1 : 0), Integer.valueOf(this.recycleEnabled ? 1 : 0), Integer.valueOf(this.energyEnabled ? 1 : 0), Integer.valueOf(this.lightSensorThreshold), Integer.valueOf(this.lightSensorThresholdOff), Integer.valueOf(this.disablePedestrianSound ? 1 : 0), 0, Integer.valueOf(this.wiperColdMode ? 1 : 0), this.customCommandStarButton1, this.customCommandStarButton2, Integer.valueOf(this.autoLaunchOnWake ? 1 : 0), Integer.valueOf(this.batteryHeatAuto ? 1 : 0), Integer.valueOf(this.pauseMediaOnDoor ? 1 : 0), Integer.valueOf(this.forcedEv ? 1 : 0), Integer.valueOf(this.fragranceEnabled ? 1 : 0), Integer.valueOf(this.fragranceTaste), Integer.valueOf(this.fragranceDuration), Integer.valueOf(this.fragranceIntensity), Integer.valueOf(this.apolloTlcEnabled ? 1 : 0), Integer.valueOf(this.apolloTrafficLightsEnabled ? 1 : 0), Integer.valueOf(this.apolloGreenSoundEnabled ? 1 : 0), Integer.valueOf(this.apolloTrafficSignsEnabled ? 1 : 0), 0, Integer.valueOf(this.driveRememberLast ? 1 : 0), Integer.valueOf(this.energyRememberLast ? 1 : 0), Integer.valueOf(this.recycleRememberLast ? 1 : 0), Integer.valueOf(this.suspensionMaintenance ? 1 : 0), driveSelectionPolicy.override, driveSelectionPolicy.medium, driveSelectionPolicy.configured, driveSelectionPolicy.current});
        return matrixCursor;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        Boolean asBoolean;
        String str2;
        String asString;
        int i = 0;
        if (contentValues == null || this.sharedPreferences == null) {
            return 0;
        }
        if (contentValues.containsKey(DriveSelectionPolicy.SOURCE)) {
            if (Binder.getCallingUid() != 0) {
                getContext().enforceCallingOrSelfPermission("ru.big.town.anative.permission.BIND_SET_MODES_SERVICE", "Drive selection update");
            }
            return DriveSelectionPreferences.select(this.sharedPreferences, contentValues.getAsString(DriveSelectionPolicy.MODE), contentValues.getAsString(DriveSelectionPolicy.SOURCE)) ? 1 : 0;
        }
        if (contentValues.containsKey("energySelection")) {
            if (Binder.getCallingUid() != 0) {
                getContext().enforceCallingOrSelfPermission("ru.big.town.anative.permission.BIND_SET_MODES_SERVICE", "Energy selection update");
            }
            return DriveSelectionPreferences.selectEnergy(this.sharedPreferences, contentValues.getAsString("energySelection"), false) ? 1 : 0;
        }
        SharedPreferences.Editor editorEdit = this.sharedPreferences.edit();
        int i2 = 3;
        String[] strArr2 = {"driveMode", "energy", "recycle"};
        int i3 = 0;
        int i4 = 0;
        while (i3 < i2) {
            String str3 = strArr2[i3];
            if ("driveMode".equals(str3)) {
                str2 = "driveRememberLast";
            } else {
                str2 = "energy".equals(str3) ? "energyRememberLast" : "recycleRememberLast";
            }
            int i5 = i;
            String str4 = str2;
            int i6 = i2;
            if (contentValues.containsKey(str3) && this.sharedPreferences.getBoolean(str4, true) && (asString = contentValues.getAsString(str3)) != null && !asString.isEmpty()) {
                editorEdit.putString(str3, asString);
                i4++;
                Log.i("$$$", "provider UPDATE " + str3 + "=" + asString);
            }
            i3++;
            i = i5;
            i2 = i6;
        }
        int i7 = i;
        String[] strArr3 = new String[4];
        strArr3[i7] = "forcedEv";
        strArr3[1] = "disablePedestrianSound";
        strArr3[2] = "suspensionMaintenance";
        strArr3[i2] = "autoLight";
        for (int i8 = i7; i8 < 4; i8++) {
            String str5 = strArr3[i8];
            if (contentValues.containsKey(str5) && (asBoolean = contentValues.getAsBoolean(str5)) != null) {
                editorEdit.putBoolean(str5, asBoolean.booleanValue());
                i4++;
                Log.i("$$$", "provider UPDATE " + str5 + "=" + asBoolean);
            }
        }
        if (i4 > 0) {
            editorEdit.apply();
        }
        return i4;
    }
}
