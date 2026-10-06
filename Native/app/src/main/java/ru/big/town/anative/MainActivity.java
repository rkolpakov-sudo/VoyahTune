package ru.big.town.anative;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import java.lang.reflect.Array;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlinx.coroutines.DebugKt;
import ru.big.town.anative.databinding.ActivityMainBinding;
import ru.big.town.common.DriveSelectionPolicy;

/* JADX INFO: loaded from: classes2.dex */
public class MainActivity extends AppCompatActivity {
    private static final String[] BATTERY_HEAT_FRAMES;
    private static final String MODES_LOG = "$$$ MainActivity loadModes";
    private static final Uri MODES_PROVIDER_URI;
    private static boolean apolloGreenSoundEnabled = false;
    private static boolean apolloTlcEnabled = false;
    private static boolean apolloTrafficLightsEnabled = false;
    private static boolean apolloTrafficSignsEnabled = false;
    private static String customCommand = "";
    public static int customCommandCount = 1;
    public static String customCommandStarButton1 = "";
    public static String customCommandStarButton2 = "";
    private static boolean disablePedestrianSound = false;
    private static boolean driveEnabled = false;
    public static String driveMode = "INDIVIDUAL";
    private static boolean driveRememberLast = true;
    private static String energy = "SREV";
    private static boolean energyEnabled = false;
    private static boolean energyRememberLast = true;
    private static boolean forcedEv = false;
    private static int fragranceDuration = 0;
    private static boolean fragranceEnabled = false;
    private static int fragranceIntensity = 2;
    private static int fragranceTaste = 1;
    private static String recycle = "LOW";
    private static boolean recycleEnabled = false;
    private static boolean recycleRememberLast = true;
    private static boolean suspensionMaintenance = false;
    private ActivityMainBinding binding;

    public static native int cis_can_control_bytes(int i, byte[] bArr);

    private static int hexToBin(char c) {
        if ('0' <= c && c <= '9') {
            return c - '0';
        }
        if ('A' <= c && c <= 'F') {
            return c - '7';
        }
        if ('a' > c || c > 'f') {
            return -1;
        }
        return c - 'W';
    }

    public static void printBytesArrayToLog(String str, byte[][] bArr) {
        for (byte[] bArr2 : bArr) {
            Log.i(str, printHexBinary(bArr2));
        }
    }

    public static String printHexBinary(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bArr) {
            sb.append(String.format("%02X ", Byte.valueOf(b)));
        }
        return sb.toString();
    }

    public static byte[] parseHexBinary(String str) {
        String strReplace = str.replace(" ", "");
        int length = strReplace.length();
        if (length % 2 != 0) {
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + strReplace);
        }
        byte[] bArr = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            int iHexToBin = hexToBin(strReplace.charAt(i));
            int iHexToBin2 = hexToBin(strReplace.charAt(i + 1));
            if (iHexToBin == -1 || iHexToBin2 == -1) {
                throw new IllegalArgumentException("contains illegal character for hexBinary: " + strReplace);
            }
            bArr[i / 2] = (byte) ((iHexToBin * 16) + iHexToBin2);
        }
        return bArr;
    }

    public static byte[][] arraysStr2arraysBytes(String[] strArr) {
        if (strArr == null) {
            Log.w("$$$ MAIN arraysStr2arraysBytes $$$", "cmds=null (неизвестный режим?) → пустой набор");
            return new byte[0][];
        }
        byte[][] bArr = (byte[][]) Array.newInstance((Class<?>) Byte.TYPE, strArr.length, 10);
        int i = 0;
        for (String str : strArr) {
            bArr[i] = parseHexBinary(str);
            i++;
        }
        return bArr;
    }

    static {
        System.loadLibrary("anative");
        BATTERY_HEAT_FRAMES = new String[]{"65 08 00 00 c1 c0 00 00 00 00"};
        MODES_PROVIDER_URI = Uri.parse("content://ru.big.town.restoremode.restoremodecontentprovider/");
    }

    public static byte[][] getCustomCommand() {
        String str = customCommand;
        if (str == null || str.isEmpty()) {
            return new byte[][]{new byte[0]};
        }
        return arraysStr2arraysBytes(customCommand.split("\n"));
    }

    public static byte[][] getCustomCommandStarButton1() {
        String str = customCommandStarButton1;
        if (str == null || str.isEmpty()) {
            return new byte[][]{new byte[0]};
        }
        return arraysStr2arraysBytes(customCommandStarButton1.split("\n"));
    }

    public static byte[][] getCustomCommandStarButton2() {
        String str = customCommandStarButton2;
        if (str == null || str.isEmpty()) {
            return new byte[][]{new byte[0]};
        }
        return arraysStr2arraysBytes(customCommandStarButton2.split("\n"));
    }

    public static boolean sendEnergyModeCommand(Context context, String str) {
        return sendOemBundleState(context, "IVI_SOC_MODESET", 957, VehicleRestorePolicy.requireEnergy(str), "energy mode: " + str);
    }

    public static boolean sendDriveModeCommand(Context context, String str) {
        return DriveModeCanTransport.send(context, str);
    }

    public static boolean sendDriveModeCommand(String str) {
        return DriveModeCanTransport.send(GlobalVars.SAVE_CONTEXT, str);
    }

    public static boolean sendRecuperationModeCommand(Context context, String str) {
        if (context == null) {
            return false;
        }
        if (!VehicleRestorePolicy.allowsRecuperationRestore(currentVehicleMode(context, "driveMode"))) {
            Log.i("$$$ MainActivity recuperation $$$", "Snow owns minimum recuperation; storing selection without CAN send");
            return true;
        }
        return sendOemBundleState(context, "HUM_ENERGY_PTREGEN_LEVL", 619, VehicleRestorePolicy.requireRecycle(str), "recuperation level: " + str);
    }

    private static boolean sendOemBundleState(Context context, String str, int i, int i2, String str2) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put(str, Integer.valueOf(i2));
        LinkedHashMap linkedHashMap2 = new LinkedHashMap();
        linkedHashMap2.put(str, Integer.valueOf(i));
        return OemVehicleStateTransport.sendBundle(context, linkedHashMap, linkedHashMap2, str2).accepted();
    }

    public static boolean sendForcedEvCommand(boolean z) {
        int iRequireEnergy;
        String string = "EV";
        Context context = GlobalVars.SAVE_CONTEXT;
        if (context == null) {
            return false;
        }
        if (z) {
            iRequireEnergy = 5;
        } else {
            String strCurrentSavedMode = currentSavedMode(context, "energy");
            if ("FORCE_EV".equals(strCurrentSavedMode)) {
                try {
                    Bundle bundleCall = context.getContentResolver().call(MODES_PROVIDER_URI, "driveHookV2", "snapshot", (Bundle) null);
                    if (bundleCall != null) {
                        string = bundleCall.getString("configuredEnergy", "EV");
                    }
                } catch (RuntimeException unused) {
                }
            } else {
                string = strCurrentSavedMode;
            }
            try {
                iRequireEnergy = VehicleRestorePolicy.requireEnergy(string);
            } catch (IllegalArgumentException e) {
                Log.w("$$$ MainActivity forced EV $$$", "Invalid saved energy target; falling back to EV", e);
                iRequireEnergy = 2;
            }
        }
        return sendOemBundleState(context, "IVI_SOC_MODESET", 957, iRequireEnergy, "forced EV ".concat(z ? DebugKt.DEBUG_PROPERTY_VALUE_ON : "off / restore saved energy"));
    }

    public static boolean setHeadlights(Context context, boolean z) {
        Log.i("$$$ MainActivity setHeadlights $$$", "OEM CAN: ".concat(z ? "LOW_BEAM" : "OUT_LAMP_OFF"));
        return HeadlightCanTransport.send(context, z);
    }

    public static boolean setHeadlightsAutoLow(Context context, boolean z) {
        Log.i("$$$ MainActivity setHeadlights $$$", "OEM CAN: ".concat(z ? "LOW_BEAM" : "AUTO_LAMP_SWITCH"));
        return HeadlightCanTransport.sendAutoPair(context, z);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        startForegroundService(new Intent(this, (Class<?>) SetModesService.class));
        String lastManualApp = WidgetSupport.getLastManualApp(this);
        if (lastManualApp != null && !lastManualApp.isEmpty()) {
            try {
                getPackageManager().getApplicationInfo(lastManualApp, 0);
                SetModesReceiverDynamic.openFreeformApp(this, lastManualApp, 0);
                Log.i("$$$ MainActivity onCreate $$$", "Автозапуск последнего приложения: " + lastManualApp);
            } catch (Exception e) {
                Log.w("$$$ MainActivity onCreate $$$", "Не удалось автозапустить " + lastManualApp, e);
            }
        }
        setContentView(R.layout.activity_main);
        setRequestedOrientation(0);
    }

    public static boolean setCanValues(int i, byte[][] bArr) {
        return setCanValues(i, bArr, null);
    }

    public static boolean setCanValues(int i, byte[][] bArr, String str) {
        return CanSender.send(i, bArr, str);
    }

    public static int loadModes(Context context) {
        return loadModes(context, true);
    }

    /* JADX WARN: Code duplicated, block: B:121:0x02d9 A[PHI: r8
  0x02d9: PHI (r8v1 android.database.Cursor) = (r8v0 android.database.Cursor), (r8v2 android.database.Cursor) binds: [B:120:0x02d7, B:114:0x02bd] A[DONT_GENERATE, DONT_INLINE]] */
    public static int loadModes(Context context, boolean z) {
        Cursor cursorQuery = null;
        try {
            cursorQuery = context.getContentResolver().query(Uri.parse("content://ru.big.town.restoremode.restoremodecontentprovider/"), null, null, null, null);
            if (cursorQuery == null || cursorQuery.getCount() == 0 || cursorQuery.getColumnCount() < 5) {
                Log.w(MODES_LOG, "Content provider not ready or missing columns" + (cursorQuery != null ? " cols=" + cursorQuery.getColumnCount() : " cursor=null"));
            } else {
                cursorQuery.moveToFirst();
                driveMode = cursorQuery.getString(0);
                boolean z2 = true;
                energy = cursorQuery.getString(1);
                recycle = cursorQuery.getString(2);
                customCommand = cursorQuery.getString(3);
                customCommandCount = cursorQuery.getInt(4);
                driveEnabled = cursorQuery.getColumnCount() > 6 && cursorQuery.getInt(6) == 1;
                recycleEnabled = cursorQuery.getColumnCount() > 7 && cursorQuery.getInt(7) == 1;
                energyEnabled = cursorQuery.getColumnCount() > 8 && cursorQuery.getInt(8) == 1;
                disablePedestrianSound = cursorQuery.getColumnCount() > 11 && cursorQuery.getInt(11) == 1;
                forcedEv = cursorQuery.getColumnCount() > 19 && cursorQuery.getInt(19) == 1;
                suspensionMaintenance = cursorQuery.getColumnCount() > 32 && cursorQuery.getInt(32) == 1;
                fragranceEnabled = cursorQuery.getColumnCount() > 20 && cursorQuery.getInt(20) == 1;
                FragranceRestorePolicy.Settings settingsNormalize = FragranceRestorePolicy.normalize(cursorQuery.getColumnCount() > 21 ? cursorQuery.getInt(21) : 1, cursorQuery.getColumnCount() > 22 ? cursorQuery.getInt(22) : 0, cursorQuery.getColumnCount() > 23 ? cursorQuery.getInt(23) : 2);
                fragranceTaste = settingsNormalize.taste;
                fragranceDuration = settingsNormalize.duration;
                fragranceIntensity = settingsNormalize.intensity;
                apolloTlcEnabled = cursorQuery.getColumnCount() > 24 && cursorQuery.getInt(24) == 1;
                apolloTrafficLightsEnabled = cursorQuery.getColumnCount() > 25 && cursorQuery.getInt(25) == 1;
                apolloGreenSoundEnabled = cursorQuery.getColumnCount() > 26 && cursorQuery.getInt(26) == 1;
                apolloTrafficSignsEnabled = cursorQuery.getColumnCount() > 27 && cursorQuery.getInt(27) == 1;
                driveRememberLast = cursorBooleanDefaultTrue(cursorQuery, 29);
                energyRememberLast = cursorBooleanDefaultTrue(cursorQuery, 30);
                recycleRememberLast = cursorBooleanDefaultTrue(cursorQuery, 31);
                boolean z3 = cursorQuery.getColumnCount() > 13 && cursorQuery.getInt(13) == 1;
                if (cursorQuery.getColumnCount() > 14) {
                    customCommandStarButton1 = cursorQuery.getString(14);
                }
                if (cursorQuery.getColumnCount() > 15) {
                    customCommandStarButton2 = cursorQuery.getString(15);
                }
                if (cursorQuery.getColumnCount() <= 18 || cursorQuery.getInt(18) != 1) {
                    z2 = false;
                }
                applyModeSideEffects(context, z3, z2);
                saveModesCache(context, z3, z2);
                ApplyEngine.noteLoadedModes(driveMode, energy, recycle, driveEnabled, energyEnabled, recycleEnabled, driveRememberLast, energyRememberLast, recycleRememberLast);
                Log.i(MODES_LOG, "FRESH: driveEnabled=" + driveEnabled + " recycleEnabled=" + recycleEnabled + " energyEnabled=" + energyEnabled + " rememberLast=" + driveRememberLast + "/" + energyRememberLast + "/" + recycleRememberLast + " disablePedestrianSound=" + disablePedestrianSound + " fragranceEnabled=" + fragranceEnabled + " fragrance=" + fragranceTaste + "/" + fragranceDuration + "/" + fragranceIntensity + " apollo=" + apolloTlcEnabled + "/" + apolloTrafficLightsEnabled + "/" + apolloGreenSoundEnabled + "/" + apolloTrafficSignsEnabled + " wiperColdMode=" + z3 + " pauseMediaOnDoor=" + z2);
                return 2;
            }
        } catch (Exception e) {
            Log.e(MODES_LOG, "Exception reading ContentProvider: " + e.getMessage());
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
        if (!z) {
            return 0;
        }
        return loadModesFromCache(context) ? 1 : 0;
    }

    public static void initValueModes(Context context) {
        loadModes(context);
    }

    private static SharedPreferences nativePrefs(Context context) {
        return context.getSharedPreferences("NativePrefs", 0);
    }

    private static boolean cursorBooleanDefaultTrue(Cursor cursor, int column) {
        return cursor.getColumnCount() <= column || cursor.isNull(column) || cursor.getInt(column) != 0;
    }

    private static void saveModesCache(Context context, boolean z, boolean z2) {
        nativePrefs(context).edit().putString("cacheDriveMode", driveMode).putString("cacheEnergy", energy).putString("cacheRecycle", recycle).putString("cacheCustomCommand", customCommand).putInt("cacheCustomCommandCount", customCommandCount).putBoolean("cacheDriveEnabled", driveEnabled).putBoolean("cacheRecycleEnabled", recycleEnabled).putBoolean("cacheEnergyEnabled", energyEnabled).putBoolean("cacheDriveRememberLast", driveRememberLast).putBoolean("cacheEnergyRememberLast", energyRememberLast).putBoolean("cacheRecycleRememberLast", recycleRememberLast).putBoolean("cacheDisablePedestrianSound", disablePedestrianSound).putBoolean("cacheForcedEv", forcedEv).putBoolean("cacheSuspensionMaintenance", suspensionMaintenance).putBoolean("cacheFragranceEnabled", fragranceEnabled).putInt("cacheFragranceTaste", fragranceTaste).putInt("cacheFragranceDuration", fragranceDuration).putInt("cacheFragranceIntensity", fragranceIntensity).putBoolean("cacheApolloTlcEnabled", apolloTlcEnabled).putBoolean("cacheApolloTrafficLightsEnabled", apolloTrafficLightsEnabled).putBoolean("cacheApolloGreenSoundEnabled", apolloGreenSoundEnabled).putBoolean("cacheApolloTrafficSignsEnabled", apolloTrafficSignsEnabled).remove("cacheApolloStockUiEnabled").putBoolean("cacheWiperColdMode", z).putBoolean("cachePauseMediaOnDoor", z2).putBoolean("cacheValid", true).apply();
    }

    private static boolean loadModesFromCache(Context context) {
        SharedPreferences p = nativePrefs(context);
        if (!p.getBoolean("cacheValid", false)) {
            Log.w(MODES_LOG, "No cached modes available");
            return false;
        }
        driveMode = p.getString("cacheDriveMode", driveMode);
        energy = p.getString("cacheEnergy", energy);
        recycle = p.getString("cacheRecycle", recycle);
        customCommand = p.getString("cacheCustomCommand", customCommand);
        customCommandCount = p.getInt("cacheCustomCommandCount", customCommandCount);
        driveEnabled = p.getBoolean("cacheDriveEnabled", false);
        recycleEnabled = p.getBoolean("cacheRecycleEnabled", false);
        energyEnabled = p.getBoolean("cacheEnergyEnabled", false);
        driveRememberLast = p.getBoolean("cacheDriveRememberLast", true);
        energyRememberLast = p.getBoolean("cacheEnergyRememberLast", true);
        recycleRememberLast = p.getBoolean("cacheRecycleRememberLast", true);
        disablePedestrianSound = p.getBoolean("cacheDisablePedestrianSound", false);
        forcedEv = p.getBoolean("cacheForcedEv", false);
        suspensionMaintenance = p.getBoolean("cacheSuspensionMaintenance", false);
        fragranceEnabled = p.getBoolean("cacheFragranceEnabled", false);
        FragranceRestorePolicy.Settings settingsNormalize = FragranceRestorePolicy.normalize(p.getInt("cacheFragranceTaste", 1), p.getInt("cacheFragranceDuration", 0), p.getInt("cacheFragranceIntensity", 2));
        fragranceTaste = settingsNormalize.taste;
        fragranceDuration = settingsNormalize.duration;
        fragranceIntensity = settingsNormalize.intensity;
        apolloTlcEnabled = p.getBoolean("cacheApolloTlcEnabled", false);
        apolloTrafficLightsEnabled = p.getBoolean("cacheApolloTrafficLightsEnabled", false);
        apolloGreenSoundEnabled = p.getBoolean("cacheApolloGreenSoundEnabled", false);
        apolloTrafficSignsEnabled = p.getBoolean("cacheApolloTrafficSignsEnabled", false);
        boolean z = p.getBoolean("cacheWiperColdMode", false);
        boolean z2 = p.getBoolean("cachePauseMediaOnDoor", false);
        applyModeSideEffects(context, z, z2);
        ApplyEngine.noteLoadedModes(driveMode, energy, recycle, driveEnabled, energyEnabled, recycleEnabled, driveRememberLast, energyRememberLast, recycleRememberLast);
        Log.i(MODES_LOG, "CACHE: driveEnabled=" + driveEnabled + " recycleEnabled=" + recycleEnabled + " energyEnabled=" + energyEnabled + " rememberLast=" + driveRememberLast + "/" + energyRememberLast + "/" + recycleRememberLast + " disablePedestrianSound=" + disablePedestrianSound + " fragranceEnabled=" + fragranceEnabled + " fragrance=" + fragranceTaste + "/" + fragranceDuration + "/" + fragranceIntensity + " apollo=" + apolloTlcEnabled + "/" + apolloTrafficLightsEnabled + "/" + apolloGreenSoundEnabled + "/" + apolloTrafficSignsEnabled + " wiperColdMode=" + z + " pauseMediaOnDoor=" + z2);
        return true;
    }

    private static void applyModeSideEffects(Context context, boolean z, boolean z2) {
        applyDoorReactor(context, z, z2);
    }

    public static boolean sendBatteryHeatCommand() {
        String[] strArr = BATTERY_HEAT_FRAMES;
        if (strArr.length == 0) {
            Log.w("$$$ MainActivity batteryHeat $$$", "sendBatteryHeatCommand: CAN-команда прогрева ещё не задана (заглушка BATTERY_HEAT_FRAMES)");
            return false;
        }
        return setCanValues(1, arraysStr2arraysBytes(strArr), "battery preheat");
    }

    public static boolean sendSuspensionMaintenanceCommand(Context context, boolean z) {
        return OemVehicleStateTransport.sendVehicleState(context, "ASC_MAINTAIN_SWITCH", 711, VehicleRestorePolicy.suspensionMaintenanceState(z), "suspension maintenance ".concat(z ? DebugKt.DEBUG_PROPERTY_VALUE_ON : DebugKt.DEBUG_PROPERTY_VALUE_OFF)).accepted();
    }

    public static boolean sendPedestrianSoundCommand(boolean z) {
        return OemVehicleStateTransport.sendVehicleState(GlobalVars.SAVE_CONTEXT, "HUM_VSP_FUNCTION_SW", 665, VehicleRestorePolicy.pedestrianSoundState(z), "pedestrian sound ".concat(z ? DebugKt.DEBUG_PROPERTY_VALUE_OFF : DebugKt.DEBUG_PROPERTY_VALUE_ON)).accepted();
    }

    public static void applyDoorReactor(Context context, boolean z, boolean z2) {
        if (context == null) {
            return;
        }
        Log.i("$$$ DoorReactor $$$", "applyDoorReactor: wiper=" + z + " pauseMedia=" + z2);
        context.getSharedPreferences("NativePrefs", 0).edit().putBoolean("wiperCold", z).putBoolean("pauseMediaOnDoor", z2).apply();
        Intent intent = new Intent(context, (Class<?>) WiperColdService.class);
        if (z || z2) {
            context.startForegroundService(intent);
        } else {
            context.stopService(intent);
        }
    }

    static CanRestorePlan createCanRestorePlan() {
        return createCanRestorePlan(true);
    }

    /* JADX WARN: Code duplicated, block: B:37:0x0168  */
    static CanRestorePlan createCanRestorePlan(boolean includeModes) {
        final OemVehicleStateTransport.StateValue stateValue;
        String str;
        final String str2;
        Log.i("$$$ MainActivity runCmds $$$", "driveMode: " + driveMode + " energy: " + energy + " recycle: " + recycle + " | driveEnabled=" + driveEnabled + " energyEnabled=" + energyEnabled + " recycleEnabled=" + recycleEnabled + " disablePedestrianSound=" + disablePedestrianSound + " fragranceEnabled=" + fragranceEnabled + " apollo=" + apolloTlcEnabled + "/" + apolloTrafficLightsEnabled + "/" + apolloGreenSoundEnabled + "/" + apolloTrafficSignsEnabled);
        CanRestorePlan.Builder plan = new CanRestorePlan.Builder();
        final Context context = GlobalVars.SAVE_CONTEXT;
        plan.addOnce("auto light saved service switch", new CanRestorePlan.Operation() { // from class: ru.big.town.anative.MainActivity$$ExternalSyntheticLambda0
            @Override // ru.big.town.anative.CanRestorePlan.Operation
            public final CanRestorePlan.OperationResult send() {
                return MainActivity.lambda$createCanRestorePlan$0(context);
            }
        });
        final LinkedHashMap primaryValues = new LinkedHashMap();
        final LinkedHashMap trailingValues = new LinkedHashMap();
        final LinkedHashMap linkedHashMap3 = new LinkedHashMap();
        plan.addOnce("Apollo stock subscription/exam UI", new CanRestorePlan.Operation() { // from class: ru.big.town.anative.MainActivity$$ExternalSyntheticLambda1
            @Override // ru.big.town.anative.CanRestorePlan.Operation
            public final CanRestorePlan.OperationResult send() {
                return MainActivity.lambda$createCanRestorePlan$1(context);
            }
        });
        if (includeModes && driveEnabled) {
            if (!DriveModeCanTransport.appendStates(context, driveMode, primaryValues, linkedHashMap3)) {
                throw new IllegalArgumentException("Unsupported drive mode: " + driveMode);
            }
        }
        VehicleRestorePolicy.appendPrimaryTo(primaryValues, includeModes && energyEnabled, energy, includeModes && forcedEv);
        VehicleRestorePolicy.appendRecuperationTo(trailingValues, recycleEnabled, recycle, driveMode);
        linkedHashMap3.putAll(VehicleRestorePolicy.stableIds());
        ApolloRestorePolicy.appendTo(primaryValues, trailingValues, apolloTlcEnabled, apolloTrafficLightsEnabled, apolloGreenSoundEnabled, apolloTrafficSignsEnabled);
        linkedHashMap3.putAll(ApolloRestorePolicy.stableIds());
        String str3 = null;
        if (fragranceEnabled) {
            FragranceRestorePolicy.Settings settingsNormalize = FragranceRestorePolicy.normalize(fragranceTaste, fragranceDuration, fragranceIntensity);
            primaryValues.putAll(FragranceRestorePolicy.fragranceBundle(settingsNormalize));
            linkedHashMap3.putAll(FragranceRestorePolicy.stableIds());
            stateValue = new OemVehicleStateTransport.StateValue(new OemVehicleStateTransport.StateKey("FCM_DURATION_CONTROL", 1067), settingsNormalize.duration);
        } else {
            stateValue = null;
        }
        if (!primaryValues.isEmpty() || !trailingValues.isEmpty()) {
            final String str4 = (includeModes && driveEnabled) ? driveMode : null;
            if (includeModes) {
                if (forcedEv) {
                    str = "FORCE_EV";
                } else if (energyEnabled) {
                    str = energy;
                } else {
                    str = null;
                }
                str2 = str;
            } else {
                str2 = null;
            }
            if (recycleEnabled && VehicleRestorePolicy.allowsRecuperationRestore(driveMode)) {
                str3 = recycle;
            }
            final String str5 = str3;
            plan.addOnce("OEM vehicle restore snapshot", new CanRestorePlan.Operation() { // from class: ru.big.town.anative.MainActivity$$ExternalSyntheticLambda2
                @Override // ru.big.town.anative.CanRestorePlan.Operation
                public final CanRestorePlan.OperationResult send() {
                    return MainActivity.lambda$createCanRestorePlan$2(context, stateValue, primaryValues, trailingValues, linkedHashMap3, str4, str2, str5);
                }
            });
        }
        final boolean z2 = disablePedestrianSound;
        String str6 = DebugKt.DEBUG_PROPERTY_VALUE_OFF;
        plan.addOnce("pedestrian sound mode ".concat(z2 ? DebugKt.DEBUG_PROPERTY_VALUE_OFF : DebugKt.DEBUG_PROPERTY_VALUE_ON), new CanRestorePlan.Operation() { // from class: ru.big.town.anative.MainActivity$$ExternalSyntheticLambda3
            @Override // ru.big.town.anative.CanRestorePlan.Operation
            public final CanRestorePlan.OperationResult send() {
                return MainActivity.lambda$createCanRestorePlan$3(context, z2);
            }
        });
        final boolean z3 = suspensionMaintenance;
        if (z3) {
            str6 = DebugKt.DEBUG_PROPERTY_VALUE_ON;
        }
        plan.addOnce("suspension maintenance ".concat(str6), new CanRestorePlan.Operation() { // from class: ru.big.town.anative.MainActivity$$ExternalSyntheticLambda4
            @Override // ru.big.town.anative.CanRestorePlan.Operation
            public final CanRestorePlan.OperationResult send() {
                return MainActivity.lambda$createCanRestorePlan$4(context, z3);
            }
        });
        return plan.build();
    }

    static /* synthetic */ CanRestorePlan.OperationResult lambda$createCanRestorePlan$0(Context context) {
        try {
            AutoLightSettings.restore(context);
            return CanRestorePlan.OperationResult.ACCEPTED_UNCONFIRMED;
        } catch (RuntimeException e) {
            Log.w(MODES_LOG, "Auto light service restore failed", e);
            return CanRestorePlan.OperationResult.TRANSIENT_FAILURE;
        }
    }

    static /* synthetic */ CanRestorePlan.OperationResult lambda$createCanRestorePlan$1(Context context) {
        ApolloSettingsRuntimeState.TargetApplyResult targetApplyResultApplyTarget = ApolloSettingsRuntimeState.applyTarget(context, false);
        if (targetApplyResultApplyTarget == ApolloSettingsRuntimeState.TargetApplyResult.CONFIRMED) {
            return CanRestorePlan.OperationResult.CONFIRMED;
        }
        if (targetApplyResultApplyTarget == ApolloSettingsRuntimeState.TargetApplyResult.ACCEPTED_UNCONFIRMED) {
            return CanRestorePlan.OperationResult.ACCEPTED_UNCONFIRMED;
        }
        return CanRestorePlan.OperationResult.TRANSIENT_FAILURE;
    }

    static /* synthetic */ CanRestorePlan.OperationResult lambda$createCanRestorePlan$2(Context context, OemVehicleStateTransport.StateValue stateValue, Map map, Map map2, Map map3, String str, String str2, String str3) {
        if (!OemVehicleStateTransport.sendRestoreSequence(context, stateValue, map, map2, map3, "drive/energy/fragrance/Apollo entitlements then switches/recuperation").accepted()) {
            return CanRestorePlan.OperationResult.TRANSIENT_FAILURE;
        }
        ApplyEngine.noteVehicleMode("driveMode", str);
        ApplyEngine.noteVehicleMode("energy", str2);
        ApplyEngine.noteVehicleMode("recycle", str3);
        return CanRestorePlan.OperationResult.ACCEPTED_UNCONFIRMED;
    }

    static /* synthetic */ CanRestorePlan.OperationResult lambda$createCanRestorePlan$3(Context context, boolean z) {
        if (AvasController.get().applySnapshot("pedestrian sound restore")) {
            return CanRestorePlan.OperationResult.ACCEPTED_UNCONFIRMED;
        }
        return CanRestorePlan.OperationResult.TRANSIENT_FAILURE;
    }

    static /* synthetic */ CanRestorePlan.OperationResult lambda$createCanRestorePlan$4(Context context, boolean z) {
        if (sendSuspensionMaintenanceCommand(context, z)) {
            return CanRestorePlan.OperationResult.ACCEPTED_UNCONFIRMED;
        }
        return CanRestorePlan.OperationResult.TRANSIENT_FAILURE;
    }

    public static boolean runCmds() {
        try {
            return createCanRestorePlan().sendPending(new CanRestorePlan.Sender() { // from class: ru.big.town.anative.MainActivity$$ExternalSyntheticLambda5
                @Override // ru.big.town.anative.CanRestorePlan.Sender
                public final boolean send(byte[][] bArr, String str) {
                    return MainActivity.setCanValues(1, bArr, str);
                }
            }).isComplete();
        } catch (IllegalArgumentException e) {
            Log.e("$$$ MainActivity runCmds $$$", "Permanent CAN plan error: " + e.getMessage());
            return false;
        }
    }

    public static void setDriveMode(String str) {
        sendDriveModeCommand(str);
    }

    public static String currentSavedMode(Context context, boolean z) {
        return currentSavedMode(context, z ? "energy" : "driveMode");
    }

    public static String currentSavedMode(Context context, String str) {
        int iModeColumn = modeColumn(str);
        Cursor cursorQuery = null;
        try {
            if (iModeColumn < 0) {
                return null;
            }
            try {
                cursorQuery = context.getContentResolver().query(MODES_PROVIDER_URI, null, null, null, null);
                if (cursorQuery != null && cursorQuery.getCount() != 0 && cursorQuery.getColumnCount() > iModeColumn) {
                    cursorQuery.moveToFirst();
                    String string = cursorQuery.getString(iModeColumn);
                    if (string != null && !string.isEmpty()) {
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                        return string;
                    }
                }
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
            } catch (Exception e) {
                Log.w(MODES_LOG, "currentSavedMode: " + e.getMessage());
                if (cursorQuery != null) {
                }
            }
            if ("energy".equals(str)) {
                return energy;
            }
            return "recycle".equals(str) ? recycle : driveMode;
        } catch (Throwable th) {
            if (cursorQuery != null) {
                cursorQuery.close();
                throw th;
            }
            throw th;
        }
    }

    static String currentVehicleMode(Context context, String str) {
        return ApplyEngine.currentVehicleMode(str, currentSavedMode(context, str));
    }

    private static boolean remembersMode(Context context, String str) {
        int i;
        String str2;
        if ("driveMode".equals(str)) {
            i = 29;
        } else {
            i = "energy".equals(str) ? 30 : 31;
        }
        try {
            Cursor cursorQuery = context.getContentResolver().query(MODES_PROVIDER_URI, null, null, null, null);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToFirst()) {
                        boolean zCursorBooleanDefaultTrue = cursorBooleanDefaultTrue(cursorQuery, i);
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                        return zCursorBooleanDefaultTrue;
                    }
                } catch (Throwable th) {
                    if (cursorQuery == null) {
                        throw th;
                    }
                    try {
                        cursorQuery.close();
                        throw th;
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                        throw th;
                    }
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        } catch (Exception e) {
            Log.w(MODES_LOG, "remember-last lookup: " + e.getMessage());
        }
        if ("driveMode".equals(str)) {
            str2 = "cacheDriveRememberLast";
        } else {
            str2 = "energy".equals(str) ? "cacheEnergyRememberLast" : "cacheRecycleRememberLast";
        }
        return nativePrefs(context).getBoolean(str2, true);
    }

    static boolean isLoadedMode(boolean z, String str) {
        return isLoadedMode(z ? "energy" : "driveMode", str);
    }

    static boolean isLoadedMode(String str, String str2) {
        if (str2 == null) {
            return false;
        }
        if ("energy".equals(str)) {
            return str2.equals(energy);
        }
        if ("recycle".equals(str)) {
            return str2.equals(recycle);
        }
        return "driveMode".equals(str) && str2.equals(driveMode);
    }

    static void updateRememberLastMode(Context context, String str, boolean z) {
        String str2;
        if ("driveMode".equals(str)) {
            driveRememberLast = z;
            str2 = "cacheDriveRememberLast";
        } else if ("energy".equals(str)) {
            energyRememberLast = z;
            str2 = "cacheEnergyRememberLast";
        } else {
            if (!"recycle".equals(str)) {
                return;
            }
            recycleRememberLast = z;
            str2 = "cacheRecycleRememberLast";
        }
        ApplyEngine.noteRememberLastMode(str, z);
        if (context != null) {
            nativePrefs(context).edit().putBoolean(str2, z).apply();
        }
        Log.i(MODES_LOG, "rememberLast " + str + "=" + z);
    }

    public static void persistSavedMode(Context context, boolean z, String str) {
        persistSavedMode(context, z ? "energy" : "driveMode", str);
    }

    public static void persistSavedMode(Context context, String str, String str2) {
        persistSavedMode(context, str, str2, false);
    }

    static void persistExplicitMode(Context context, String str, String str2) {
        persistSavedMode(context, str, str2, true);
    }

    private static void persistSavedMode(Context context, String modeKey, String next, boolean explicit) {
        boolean z2;
        if (context == null || next == null || next.isEmpty()) {
            return;
        }
        if ("driveMode".equals(modeKey)) {
            DriveSelectionStore.record(context, next, DriveSelectionPolicy.EXPLICIT);
            return;
        }
        if ("energy".equals(modeKey)) {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("energySelection", next);
                if (context.getContentResolver().update(MODES_PROVIDER_URI, contentValues, null, null) > 0) {
                    energy = next;
                    ApplyEngine.noteSavedMode("energy", next);
                    ApplyEngine.driveSelectionSaved();
                }
                return;
            } catch (RuntimeException e) {
                Log.w(MODES_LOG, "Energy selection unavailable", e);
                return;
            }
        }
        if (modeColumn(modeKey) < 0) {
            return;
        }
        if (!ApplyEngine.canRememberModeSelection(explicit)) return;
        if (!remembersMode(context, modeKey)) return;
        try {
            ContentValues contentValues2 = new ContentValues();
            contentValues2.put(modeKey, next);
            z2 = context.getContentResolver().update(MODES_PROVIDER_URI, contentValues2, null, null) > 0;
            if (!z2) {
                return;
            }
        } catch (Exception e2) {
            Log.w(MODES_LOG, "persistSavedMode provider: " + e2.getMessage());
            z2 = false;
        }
        if ("energy".equals(modeKey)) {
            energy = next;
        } else if ("recycle".equals(modeKey)) {
            recycle = next;
        } else {
            driveMode = next;
        }
        ApplyEngine.noteSavedMode(modeKey, next);
        try {
            Intent intent = new Intent("ru.big.town.anative.MODE_SYNCED");
            intent.setPackage("ru.big.town.restoremode");
            intent.putExtra("isEnergy", "energy".equals(modeKey));
            intent.putExtra("modeKey", modeKey);
            intent.putExtra("mode", next);
            context.sendBroadcast(intent);
        } catch (Exception unused) {
        }
        if (!z2) {
            Log.w(MODES_LOG, "persistSavedMode " + modeKey + "=" + next + " — провайдер НЕ записан, режим не переживёт пробуждение");
        } else {
            try {
                context.getSharedPreferences("NativePrefs", 0).edit().putString(modeCacheKey(modeKey), next).apply();
            } catch (Exception unused2) {
            }
            Log.i(MODES_LOG, "persistSavedMode " + modeKey + "=" + next + " (provider ok)");
        }
    }

    private static int modeColumn(String str) {
        if ("driveMode".equals(str)) {
            return 0;
        }
        if ("energy".equals(str)) {
            return 1;
        }
        return "recycle".equals(str) ? 2 : -1;
    }

    private static String modeCacheKey(String str) {
        if ("energy".equals(str)) {
            return "cacheEnergy";
        }
        return "recycle".equals(str) ? "cacheRecycle" : "cacheDriveMode";
    }

    /* JADX WARN: Code duplicated, block: B:40:0x008a  */
    /* JADX WARN: Code duplicated, block: B:41:0x008d  */
    /* JADX WARN: Code duplicated, block: B:43:0x0093  */
    /* JADX WARN: Code duplicated, block: B:44:0x0096  */
    public static boolean currentSavedToggle(Context context, String str) {
        int i;
        if ("disablePedestrianSound".equals(str)) {
            i = 11;
        } else if ("forcedEv".equals(str)) {
            i = 19;
        } else {
            i = "suspensionMaintenance".equals(str) ? 32 : -1;
        }
        if (i < 0) {
            return false;
        }
        Cursor cursorQuery = null;
        try {
            cursorQuery = context.getContentResolver().query(MODES_PROVIDER_URI, null, null, null, null);
            if (cursorQuery != null && cursorQuery.getCount() != 0 && cursorQuery.getColumnCount() > i) {
                cursorQuery.moveToFirst();
                return cursorQuery.getInt(i) == 1;
            }
        } catch (Exception e) {
            Log.w(MODES_LOG, "currentSavedToggle " + str + ": " + e.getMessage());
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
        if ("forcedEv".equals(str)) {
            return forcedEv;
        }
        return "suspensionMaintenance".equals(str) ? suspensionMaintenance : disablePedestrianSound;
    }

    public static void persistSavedToggle(Context context, String str, boolean z) {
        boolean z2 = false;
        if (context != null) {
            String str2 = "autoLight";
            if ("forcedEv".equals(str) || "disablePedestrianSound".equals(str) || "suspensionMaintenance".equals(str) || "autoLight".equals(str)) {
                try {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(str, Boolean.valueOf(z));
                    z2 = context.getContentResolver().update(MODES_PROVIDER_URI, contentValues, null, null) > 0;
                } catch (Exception e) {
                    Log.w(MODES_LOG, "persistSavedToggle provider " + str + ": " + e.getMessage());
                }
                if ("forcedEv".equals(str)) {
                    forcedEv = z;
                } else if ("suspensionMaintenance".equals(str)) {
                    suspensionMaintenance = z;
                } else if ("disablePedestrianSound".equals(str)) {
                    disablePedestrianSound = z;
                }
                try {
                    Intent intent = new Intent("ru.big.town.anative.SETTING_SYNCED");
                    intent.setPackage("ru.big.town.restoremode");
                    intent.putExtra("key", str);
                    intent.putExtra("value", z);
                    context.sendBroadcast(intent);
                } catch (Exception unused) {
                }
                if (!z2) {
                    Log.w(MODES_LOG, "persistSavedToggle " + str + "=" + z + " — провайдер НЕ записан");
                    return;
                }
                try {
                    SharedPreferences.Editor editorEdit = context.getSharedPreferences("NativePrefs", 0).edit();
                    if ("forcedEv".equals(str)) {
                        str2 = "cacheForcedEv";
                    } else if ("suspensionMaintenance".equals(str)) {
                        str2 = "cacheSuspensionMaintenance";
                    } else if (!"autoLight".equals(str)) {
                        str2 = "cacheDisablePedestrianSound";
                    }
                    editorEdit.putBoolean(str2, z).apply();
                } catch (Exception unused2) {
                }
                Log.i(MODES_LOG, "persistSavedToggle " + str + "=" + z + " (provider ok)");
            }
        }
    }

    public void onButtonClick(View view) {
        Log.i("$$$ MainActivity click $$$", "");
        ApplyEngine.applyNow(null);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        Log.i("$$$ MainActivity click $$$", "onPause()");
        super.onPause();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        Log.i("$$$ MainActivity click $$$", "onStop()");
        super.onStop();
    }
}
