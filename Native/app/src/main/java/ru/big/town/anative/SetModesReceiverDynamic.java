package ru.big.town.anative;

import android.app.ActivityOptions;
import android.car.VehicleAreaDoor;
import android.car.user.CarUserManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.BooleanSupplier;
import kotlinx.coroutines.DebugKt;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class SetModesReceiverDynamic extends BroadcastReceiver {
    private static final Handler STEER_SEQUENCE_HANDLER = new Handler(Looper.getMainLooper());
    static final String TAG = "$$$ SetModesReceiverDynamic $$$";
    public static volatile boolean isButton = false;
    public static volatile int repeat = 7;
    private final Runnable sleepCallback;
    private final Runnable wakeCallback;
    private final SleepController sleepController;

    static /* synthetic */ boolean lambda$openFreeformApp$3() {
        return true;
    }

    private static int sanitizeDpi(int i) {
        if (i < 100 || i > 640) {
            return 0;
        }
        return i;
    }

    public SetModesReceiverDynamic() {
        this(null, null, new SleepController());
    }

    SetModesReceiverDynamic(Runnable runnable, Runnable runnable2, SleepController sleepController) {
        this.sleepCallback = runnable;
        this.wakeCallback = runnable2;
        this.sleepController = sleepController;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        int intExtra;
        Runnable runnable;
        String action = intent.getAction();
        boolean z = intent.getComponent() != null;
        Log.i(TAG, "onReceive DYN enter by intent" + action);
        if ("android.intent.action.KEYCODE_SWC_USER_DEFINE".equals(action) && intent.getComponent() == null) {
            Log.i(TAG, "android.intent.action.KEYCODE_SWC_USER_DEFINE");
            Log.i(TAG, "GlobalVars.buttonDriveMode: " + GlobalVars.buttonDriveMode);
            int i = GlobalVars.buttonDriveMode;
            if (i == 1) {
                SetModesService.worker(1, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, 2, 1);
                GlobalVars.buttonDriveMode = 2;
            } else if (i == 2) {
                SetModesService.worker(1, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, 2, 2);
                GlobalVars.buttonDriveMode = 1;
            }
        }
        if ("ru.big.town.anative.OPEN_FREEFORM".equals(action)) {
            String pkg = intent.getStringExtra("pkg");
            int displayId = intent.getIntExtra("display", 0);
            if (displayId != 0 && displayId != 1) {
                Log.w(TAG, "OPEN_FREEFORM отклонён: неверный physical display " + displayId);
            } else if (isConfiguredDockPackage(context, pkg)) {
                openFreeformApp(context, pkg, displayId);
            } else {
                Log.w(TAG, "OPEN_FREEFORM отклонён: пакет не назначен доку: " + pkg);
            }
        }
        if ("ru.big.town.anative.OPEN_ON_DISPLAY".equals(action)) {
            String stringExtra2 = intent.getStringExtra("pkg");
            int intExtra3 = intent.getIntExtra("display", 0);
            if (intExtra3 != 0 && intExtra3 != 1) {
                Log.w(TAG, "OPEN_ON_DISPLAY отклонён: неверный physical display " + intExtra3);
            } else if (stringExtra2 == null || stringExtra2.isEmpty()) {
                Log.w(TAG, "OPEN_ON_DISPLAY отклонён: пустой пакет");
            } else {
                openFreeformApp(context, stringExtra2, intExtra3);
            }
        }
        if ("ru.big.town.anative.OPEN_FULLSCREEN".equals(action)) {
            String stringExtra3 = intent.getStringExtra("pkg");
            int intExtra4 = intent.getIntExtra("display", 0);
            if (intExtra4 != 0 && intExtra4 != 1) {
                Log.w(TAG, "OPEN_FULLSCREEN отклонён: неверный physical display " + intExtra4);
            } else if (isConfiguredFullscreenPackage(context, stringExtra3)) {
                openFreeformApp(context, stringExtra3, intExtra4);
            } else {
                Log.w(TAG, "OPEN_FULLSCREEN отклонён: пакет не в fullscreen-списке: " + stringExtra3);
            }
        }
        if (("ru.big.town.anative.OPEN_DOCK_SPLIT".equals(action) || "ru.big.town.anative.OPEN_DOCK_LONG_PRESS".equals(action)) && ((intExtra = intent.getIntExtra("slot", 0)) == 1 || intExtra == 2)) {
            ContentResolver contentResolver = context.getContentResolver();
            String string = Settings.Global.getString(contentResolver, "voyahtune_dock" + intExtra + "LongAction");
            String string2 = Settings.Global.getString(contentResolver, "voyahtune_dock" + intExtra + "HasSplit");
            if ("cluster".equals(string)) {
                ClusterMediaHostActivity.launch(context, Settings.Global.getString(contentResolver, "voyahtune_dock" + intExtra));
            } else if ((string == null || SplitHostActivity.EXTRA_SPLIT.equals(string)) && "1".equals(string2)) {
                String string3 = Settings.Global.getString(contentResolver, "voyahtune_dock" + intExtra + "SplitL");
                String string4 = Settings.Global.getString(contentResolver, "voyahtune_dock" + intExtra + "SplitR");
                int intSafe = parseIntSafe(Settings.Global.getString(contentResolver, "voyahtune_dock" + intExtra + "SplitRatio"), 1);
                int intSafe2 = parseIntSafe(Settings.Global.getString(contentResolver, "voyahtune_dock" + intExtra + "SplitLDpi"), 0);
                int intSafe3 = parseIntSafe(Settings.Global.getString(contentResolver, "voyahtune_dock" + intExtra + "SplitRDpi"), 0);
                boolean zEquals = "1".equals(Settings.Global.getString(contentResolver, "voyahtune_dock" + intExtra + "SplitResizable"));
                float floatSafe = parseFloatSafe(Settings.Global.getString(contentResolver, "voyahtune_dock" + intExtra + "SplitFraction"), 0.0f);
                int intSafe4 = parseIntSafe(Settings.Global.getString(contentResolver, "voyahtune_dock" + intExtra + "SplitPresetIdx"), -1);
                String string5 = Settings.Global.getString(contentResolver, "voyahtune_dock" + intExtra + "SplitPresetId");
                ClusterMediaHostActivity.closeForPackage(string3);
                ClusterMediaHostActivity.closeForPackage(string4);
                SplitHostActivity.launchSplit(context.getApplicationContext(), string3, string4, intSafe, intSafe2, intSafe3, zEquals, floatSafe, intSafe4, string5);
                Log.i(TAG, "OPEN_DOCK_SPLIT slot=" + intExtra + " " + string3 + "/" + string4 + " ratio=" + intSafe);
            } else {
                Log.i(TAG, "OPEN_DOCK_SPLIT slot=" + intExtra + " — сплит не назначен");
            }
        }
        if ("ru.big.town.anative.STEER_ACTION".equals(action)) {
            String stringExtra4 = intent.getStringExtra(CarUserManager.BUNDLE_PARAM_ACTION);
            if (isConfiguredSteerAction(context, stringExtra4)) {
                handleSteerActions(context, stringExtra4);
            } else {
                Log.w(TAG, "STEER_ACTION отклонён: действие не настроено: " + stringExtra4);
            }
        }
        if ("android.intent.action.SCREEN_OFF".equals(action) && !z) {
            if (this.sleepController.onSleepTrigger(SleepController.Event.SCREEN_OFF)) {
                ApplyEngine.resetRestoreGate("SCREEN_OFF");
                Runnable runnable2 = this.sleepCallback;
                if (runnable2 != null) {
                    runnable2.run();
                }
                Log.i(TAG, "onReceive SCREEN_OFF — mode sync gate reset");
            } else {
                Log.i(TAG, "onReceive duplicate SCREEN_OFF ignored, session=" + this.sleepController.sessionId());
            }
        }
        if (!z && ("android.intent.action.SCREEN_ON".equals(action) || "com.android.server.jobscheduler.GARAGE_MODE_OFF".equals(action))) {
            SleepController.Event event = "android.intent.action.SCREEN_ON".equals(action) ? SleepController.Event.SCREEN_ON : SleepController.Event.GARAGE_WAKE;
            if (this.sleepController.onWakeTrigger(event)) {
                Log.i(TAG, "onReceive ACTION_SCREEN_ON or GARAGE_MODE_OFF");
                ApplyEngine.activateWake(action);
                if ("android.intent.action.SCREEN_ON".equals(action) && (runnable = this.wakeCallback) != null) {
                    runnable.run();
                }
            } else {
                Log.i(TAG, "onReceive duplicate SCREEN_ON ignored, session=" + this.sleepController.sessionId());
            }
        }
        if (z && ("android.intent.action.SCREEN_ON".equals(action) || "android.intent.action.SCREEN_OFF".equals(action) || "com.android.server.jobscheduler.GARAGE_MODE_OFF".equals(action))) {
            Log.w(TAG, "ignored explicit power broadcast: " + action);
        }
        if (isOrderedBroadcast()) {
            setResultCode(-1);
        }
    }

    static void mirrorSteer(Context context, Intent intent, String str) {
        String stringExtra = intent.getStringExtra(str);
        if (stringExtra == null) {
            stringExtra = "none";
        }
        try {
            Settings.Global.putString(context.getContentResolver(), "voyahtune_" + str, stringExtra);
        } catch (Exception e) {
            Log.w(TAG, "mirrorSteer " + str + ": " + e.getMessage());
        }
    }

    static void mirrorDock(Context context, Intent intent, int i) {
        String stringExtra = intent.getStringExtra("dock" + i);
        String str = "none";
        String str2 = (stringExtra == null || stringExtra.isEmpty()) ? "none" : stringExtra;
        int intExtra = intent.getIntExtra("dock" + i + "Dpi", 0);
        try {
            ContentResolver contentResolver = context.getContentResolver();
            String string = Settings.Global.getString(contentResolver, "voyahtune_dock" + i);
            if (string != null && !string.equals(str2)) {
                ClusterMediaHostActivity.closeForPackage(string);
            }
            Settings.Global.putString(contentResolver, "voyahtune_dock" + i, str2);
            Settings.Global.putString(contentResolver, "voyahtune_dock" + i + "Dpi", String.valueOf(intExtra));
            if (!"none".equals(str2)) {
                int iSanitizeDpi = sanitizeDpi(intExtra);
                Settings.Global.putString(contentResolver, "voyahtune_dpi_" + str2, String.valueOf(iSanitizeDpi));
                updateAppDpiIndex(contentResolver, str2, iSanitizeDpi);
            }
            boolean booleanExtra = intent.getBooleanExtra("dock" + i + "HasSplit", false);
            String stringExtra2 = intent.getStringExtra("dock" + i + "LongAction");
            if (stringExtra2 == null) {
                stringExtra2 = booleanExtra ? SplitHostActivity.EXTRA_SPLIT : "none";
            }
            if (!"none".equals(str2) && (SplitHostActivity.EXTRA_SPLIT.equals(stringExtra2) || "cluster".equals(stringExtra2))) {
                str = stringExtra2;
            }
            boolean z = booleanExtra && SplitHostActivity.EXTRA_SPLIT.equals(str);
            Settings.Global.putString(contentResolver, "voyahtune_dock" + i + "LongAction", str);
            String str3 = "1";
            Settings.Global.putString(contentResolver, "voyahtune_dock" + i + "HasSplit", z ? "1" : "0");
            if (z) {
                Settings.Global.putString(contentResolver, "voyahtune_dock" + i + "SplitL", nz(intent.getStringExtra("dock" + i + "SplitL")));
                Settings.Global.putString(contentResolver, "voyahtune_dock" + i + "SplitR", nz(intent.getStringExtra("dock" + i + "SplitR")));
                Settings.Global.putString(contentResolver, "voyahtune_dock" + i + "SplitRatio", String.valueOf(intent.getIntExtra("dock" + i + "SplitRatio", 1)));
                Settings.Global.putString(contentResolver, "voyahtune_dock" + i + "SplitLDpi", String.valueOf(intent.getIntExtra("dock" + i + "SplitLDpi", 0)));
                Settings.Global.putString(contentResolver, "voyahtune_dock" + i + "SplitRDpi", String.valueOf(intent.getIntExtra("dock" + i + "SplitRDpi", 0)));
                String str4 = "voyahtune_dock" + i + "SplitResizable";
                if (!intent.getBooleanExtra("dock" + i + "SplitResizable", false)) {
                    str3 = "0";
                }
                Settings.Global.putString(contentResolver, str4, str3);
                Settings.Global.putString(contentResolver, "voyahtune_dock" + i + "SplitFraction", String.valueOf(intent.getFloatExtra("dock" + i + "SplitFraction", 0.0f)));
                Settings.Global.putString(contentResolver, "voyahtune_dock" + i + "SplitPresetIdx", String.valueOf(intent.getIntExtra("dock" + i + "SplitPresetIdx", -1)));
                Settings.Global.putString(contentResolver, "voyahtune_dock" + i + "SplitPresetId", nz(intent.getStringExtra("dock" + i + "SplitPresetId")));
            }
        } catch (Exception e) {
            Log.w(TAG, "mirrorDock " + i + ": " + e.getMessage());
        }
    }

    static void clearLegacyPassengerDock(Context context) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Settings.Global.putString(contentResolver, "voyahtune_dockPassenger1", "none");
            Settings.Global.putString(contentResolver, "voyahtune_dockPassenger2", "none");
            Settings.Global.putString(contentResolver, "voyahtune_dockPassenger1Dpi", "0");
            Settings.Global.putString(contentResolver, "voyahtune_dockPassenger2Dpi", "0");
        } catch (Exception e) {
            Log.w(TAG, "clearLegacyPassengerDock: " + e.getMessage());
        }
    }

    static void mirrorAppDpi(Context context, Intent intent) {
        int iSanitizeDpi;
        String stringExtra = intent.getStringExtra("appDpiJson");
        if (stringExtra == null) {
            return;
        }
        try {
            ContentResolver contentResolver = context.getContentResolver();
            JSONObject jSONObject = new JSONObject(stringExtra);
            LinkedHashSet linkedHashSet = new LinkedHashSet();
            Iterator<String> itKeys = jSONObject.keys();
            while (itKeys.hasNext()) {
                String next = itKeys.next();
                if (validPackageName(next) && (iSanitizeDpi = sanitizeDpi(jSONObject.optInt(next, 0))) > 0) {
                    Settings.Global.putString(contentResolver, "voyahtune_dpi_" + next, String.valueOf(iSanitizeDpi));
                    linkedHashSet.add(next);
                }
            }
            String string = Settings.Global.getString(contentResolver, "voyahtune_dpi_packages");
            if (string != null && !string.isEmpty()) {
                for (String str : string.split(",")) {
                    if (validPackageName(str) && !linkedHashSet.contains(str)) {
                        Settings.Global.putString(contentResolver, "voyahtune_dpi_" + str, "0");
                    }
                }
            }
            String stringExtra2 = intent.getStringExtra("changedPkg");
            if (validPackageName(stringExtra2)) {
                int iSanitizeDpi2 = sanitizeDpi(intent.getIntExtra("changedDpi", 0));
                Settings.Global.putString(contentResolver, "voyahtune_dpi_" + stringExtra2, String.valueOf(iSanitizeDpi2));
                if (iSanitizeDpi2 > 0) {
                    linkedHashSet.add(stringExtra2);
                } else {
                    linkedHashSet.remove(stringExtra2);
                }
            }
            Settings.Global.putString(contentResolver, "voyahtune_dpi_packages", TextUtils.join(",", linkedHashSet));
        } catch (Exception e) {
            Log.w(TAG, "mirrorAppDpi: " + e.getMessage());
        }
    }

    static boolean ensureAppDpi(Context ctx, String str, int i) {
        if (!validPackageName(str)) {
            return false;
        }
        int iSanitizeDpi = sanitizeDpi(i);
        try {
            ContentResolver contentResolver = ctx.getContentResolver();
            String str2 = "voyahtune_dpi_" + str;
            String strValueOf = String.valueOf(iSanitizeDpi);
            boolean zEquals = strValueOf.equals(Settings.Global.getString(contentResolver, str2));
            if (!zEquals) {
                Settings.Global.putString(contentResolver, str2, strValueOf);
            }
            boolean zUpdateAppDpiIndex = updateAppDpiIndex(contentResolver, str, iSanitizeDpi);
            if (zEquals && !zUpdateAppDpiIndex) {
                return false;
            }
            sendWinReload(ctx);
            return true;
        } catch (Exception e) {
            Log.w(TAG, "ensureAppDpi " + str + ": " + e.getMessage());
            return false;
        }
    }

    private static boolean updateAppDpiIndex(ContentResolver contentResolver, String str, int i) {
        if (!validPackageName(str)) {
            return false;
        }
        String string = Settings.Global.getString(contentResolver, "voyahtune_dpi_packages");
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        if (string != null) {
            for (String str2 : string.split(",")) {
                if (validPackageName(str2)) {
                    linkedHashSet.add(str2);
                }
            }
        }
        boolean zAdd = i > 0 ? linkedHashSet.add(str) : linkedHashSet.remove(str);
        if (zAdd) {
            Settings.Global.putString(contentResolver, "voyahtune_dpi_packages", TextUtils.join(",", linkedHashSet));
        }
        return zAdd;
    }

    private static boolean validPackageName(String str) {
        return str != null && !str.isEmpty() && str.matches("[A-Za-z0-9_.]+") && str.indexOf(46) > 0;
    }

    private static String nz(String str) {
        return str == null ? "" : str;
    }

    private static int parseIntSafe(String str, int i) {
        if (str != null) {
            try {
                if (!str.isEmpty()) {
                    return Integer.parseInt(str.trim());
                }
            } catch (Exception unused) {
            }
        }
        return i;
    }

    private static float parseFloatSafe(String str, float f) {
        if (str != null) {
            try {
                if (!str.isEmpty()) {
                    return Float.parseFloat(str.trim());
                }
            } catch (Exception unused) {
            }
        }
        return f;
    }

    private static boolean isConfiguredDockPackage(Context context, String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        ContentResolver contentResolver = context.getContentResolver();
        return str.equals(Settings.Global.getString(contentResolver, "voyahtune_dock1")) || str.equals(Settings.Global.getString(contentResolver, "voyahtune_dock2"));
    }

    private static boolean isConfiguredSteerAction(Context context, String str) {
        if (str != null && !str.isEmpty() && !"none".equals(str)) {
            ContentResolver contentResolver = context.getContentResolver();
            String[] strArr = {"Star", "Dvr", "Voice", "Phone"};
            for (int i = 0; i < 4; i++) {
                String str2 = strArr[i];
                if (str.equals(Settings.Global.getString(contentResolver, "voyahtune_steer" + str2 + "Short")) || str.equals(Settings.Global.getString(contentResolver, "voyahtune_steer" + str2 + "Long"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isConfiguredFullscreenPackage(Context context, String str) {
        if (str != null && !str.isEmpty()) {
            try {
                return FullscreenPackagePolicy.contains(Settings.Global.getString(context.getContentResolver(), "voyahtune_fullscreen_apps"), str);
            } catch (Exception e) {
                Log.w(TAG, "fullscreen allowlist unavailable: " + e.getMessage());
            }
        }
        return false;
    }

    static void mirrorFreeform(Context context, Intent intent) {
        try {
            if (intent.hasExtra(DebugKt.DEBUG_PROPERTY_VALUE_ON)) {
                Settings.Global.putString(context.getContentResolver(), "voyahtune_freeform", intent.getBooleanExtra(DebugKt.DEBUG_PROPERTY_VALUE_ON, false) ? "1" : "0");
            }
            int[] iArr = {intent.getIntExtra("left", -1), intent.getIntExtra("top", -1), intent.getIntExtra("right", -1), intent.getIntExtra("bottom", -1)};
            String[] strArr = {"voyahtune_win_left", "voyahtune_win_top", "voyahtune_win_right", "voyahtune_win_bottom"};
            for (int i = 0; i < 4; i++) {
                if (iArr[i] >= 0) {
                    Settings.Global.putString(context.getContentResolver(), strArr[i], String.valueOf(iArr[i]));
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "mirrorFreeform: " + e.getMessage());
        }
    }

    static void mirrorFullscreenApps(Context context, Intent intent) {
        String strNormalizeCsv = FullscreenPackagePolicy.normalizeCsv(intent.getStringExtra("packagesCsv"));
        try {
            Settings.Global.putString(context.getContentResolver(), "voyahtune_fullscreen_apps", strNormalizeCsv);
            BackButtonService.setFullscreenPackages(context, strNormalizeCsv);
        } catch (Exception e) {
            Log.w(TAG, "mirrorFullscreenApps: " + e.getMessage());
        }
    }

    static void sendWinReload(Context context) {
        try {
            Intent intent = new Intent("ru.big.town.anative.WIN_RELOAD");
            intent.addFlags(32);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            Log.w(TAG, "sendWinReload: " + e.getMessage());
        }
    }

    private static void handleSteerActions(Context context, String str) {
        List<String> listDecode = SteeringActionSequence.decode(str);
        if (listDecode.isEmpty()) {
            return;
        }
        Log.i(TAG, "STEER_ACTION sequence start, count=" + listDecode.size());
        runSteerAction(context.getApplicationContext(), listDecode, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void runSteerAction(final Context context, final List<String> list, final int i) {
        if (i >= list.size()) {
            Log.i(TAG, "STEER_ACTION sequence complete, count=" + list.size());
            return;
        }
        String str = list.get(i);
        Log.i(TAG, "STEER_ACTION sequence " + (i + 1) + "/" + list.size() + ": " + str);
        handleSteerAction(context, str, new Runnable() { // from class: ru.big.town.anative.SetModesReceiverDynamic$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                SetModesReceiverDynamic.STEER_SEQUENCE_HANDLER.post(new Runnable() { // from class: ru.big.town.anative.SetModesReceiverDynamic$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        SetModesReceiverDynamic.runSteerAction(context, list, i + 1);
                    }
                });
            }
        });
    }

    static void handleSteerAction(Context context, String str, Runnable runnable) {
        if (str == null || str.isEmpty()) {
            completeSteerAction(runnable);
            return;
        }
        if (str.startsWith("energy:")) {
            cycleMode(context, str.substring("energy:".length()), "energy", runnable);
            return;
        }
        if (str.startsWith("drive:")) {
            cycleMode(context, str.substring("drive:".length()), "driveMode", runnable);
            return;
        }
        if (str.startsWith("recycle:")) {
            cycleMode(context, str.substring("recycle:".length()), "recycle", runnable);
            return;
        }
        if ("toggle_suspension_maintenance".equals(str)) {
            toggleSetting(context, "suspensionMaintenance", runnable);
            return;
        }
        if ("toggle_forced_ev".equals(str)) {
            toggleSetting(context, "forcedEv", runnable);
            return;
        }
        if ("toggle_pedestrian_sound".equals(str)) {
            toggleSetting(context, "disablePedestrianSound", runnable);
            return;
        }
        if ("toggle_headlights".equals(str)) {
            toggleHeadlights(context, runnable);
            return;
        }
        if ("toggle_headlights_auto".equals(str)) {
            toggleHeadlightsAuto(context, runnable);
            return;
        }
        if (str.startsWith("can:")) {
            sendCustomCan(str, runnable);
            return;
        }
        try {
            if ("voice_assistant".equals(str)) {
                Intent className = new Intent().setClassName("ru.big.town.restoremode", "ru.big.town.restoremode.VoiceActivity");
                className.addFlags(872415232);
                ActivityOptions activityOptionsMakeBasic = ActivityOptions.makeBasic();
                activityOptionsMakeBasic.setLaunchDisplayId(0);
                Bundle bundle = activityOptionsMakeBasic.toBundle();
                bundle.putInt("android.activity.windowingMode", 1);
                context.startActivity(className, bundle);
            } else if ("system_back".equals(str)) {
                BackButtonService.performBack(context);
            } else if (str.startsWith("app:")) {
                openFreeformApp(context, str.substring("app:".length()));
                Log.i(TAG, "STEER_ACTION → приложение " + str.substring("app:".length()));
            } else if (str.startsWith("split:")) {
                launchSteerSplit(context, str);
            } else if (str.startsWith("call:")) {
                String strSubstring = str.substring("call:".length());
                Intent intent = new Intent("com.qinggan.broadcast.action.callfromcard");
                intent.putExtra("dial_number", strSubstring);
                context.sendBroadcast(intent);
                Log.i(TAG, "STEER_ACTION → вызов номера " + strSubstring);
            } else if ("open_voyahtune".equals(str)) {
                openVoyahTune(context);
            } else {
                Log.i(TAG, "STEER_ACTION неизвестно: " + str);
            }
        } finally {
            completeSteerAction(runnable);
        }
    }

    private static void launchSteerSplit(Context context, String str) {
        String[] strArrSplit = str.substring("split:".length()).split(",");
        if (strArrSplit.length >= 3) {
            try {
                int i = Integer.parseInt(strArrSplit[2].trim());
                SplitHostActivity.launchSplit(context.getApplicationContext(), strArrSplit[0].trim(), strArrSplit[1].trim(), i, strArrSplit.length > 3 ? Integer.parseInt(strArrSplit[3].trim()) : 0, strArrSplit.length > 4 ? Integer.parseInt(strArrSplit[4].trim()) : 0, strArrSplit.length > 5 && "1".equals(strArrSplit[5].trim()), strArrSplit.length > 6 ? parseFloatSafe(strArrSplit[6], 0.0f) : 0.0f, -1, strArrSplit.length > 7 ? strArrSplit[7].trim() : "");
                Log.i(TAG, "STEER_ACTION → сплит " + strArrSplit[0] + "/" + strArrSplit[1] + " ratio=" + i);
            } catch (Exception e) {
                Log.w(TAG, "STEER_ACTION split parse: " + e.getMessage());
            }
        }
    }

    private static void openVoyahTune(Context context) {
        try {
            Intent intent = new Intent();
            intent.setClassName("ru.big.town.restoremode", "ru.big.town.restoremode.MainActivity");
            intent.addFlags(VehicleAreaDoor.DOOR_HOOD);
            DockLaunchGuard.arm(context, 0, "ru.big.town.restoremode");
            ActivityOptions activityOptionsMakeBasic = ActivityOptions.makeBasic();
            activityOptionsMakeBasic.setLaunchDisplayId(0);
            context.startActivity(intent, activityOptionsMakeBasic.toBundle());
            Log.i(TAG, "STEER_ACTION → открыть VoyahTune");
        } catch (Exception e) {
            Log.w(TAG, "open VoyahTune failed: " + e.getMessage());
        }
    }

    private static void sendCustomCan(String str, Runnable runnable) {
        final byte[] customCan = SteeringActionSequence.parseCustomCan(str);
        if (customCan == null) {
            Log.w(TAG, "STEER_ACTION custom CAN отклонён: " + str);
            completeSteerAction(runnable);
        } else {
            ApplyEngine.postUserCommand("steer custom CAN", new Runnable() { // from class: ru.big.town.anative.SetModesReceiverDynamic$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    Log.i(SetModesReceiverDynamic.TAG, "STEER_ACTION custom CAN: ".concat(MainActivity.setCanValues(1, new byte[][]{customCan}, "steering custom CAN") ? "sent" : "failed"));
                }
            }, runnable);
        }
    }

    private static void completeSteerAction(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }

    static void openFreeformApp(Context context, String str) {
        openFreeformApp(context, str, 0);
    }

    static void openFreeformApp(Context context, String str, int i) {
        if (str == null || str.isEmpty()) {
            return;
        }
        if (i == 0 || i == 1) {
            final Context applicationContext = context.getApplicationContext();
            ClusterMediaHostActivity.closeForPackage(str);
            SplitHostActivity.closeActiveHost();
            AppDisplayLauncher.launch(applicationContext, str, i, isConfiguredFullscreenPackage(applicationContext, str), new BooleanSupplier() { // from class: ru.big.town.anative.SetModesReceiverDynamic$$ExternalSyntheticLambda6
                @Override // java.util.function.BooleanSupplier
                public final boolean getAsBoolean() {
                    return SetModesReceiverDynamic.lambda$openFreeformApp$3();
                }
            }, new Runnable() { // from class: ru.big.town.anative.SetModesReceiverDynamic$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    Toast.makeText(applicationContext, "Не удалось открыть приложение", 1).show();
                }
            });
        }
    }

    private static void cycleMode(Context context, final String str, final String str2, Runnable runnable) {
        final Context applicationContext = context.getApplicationContext();
        ApplyEngine.postUserCommand("steer " + str2, new Runnable() { // from class: ru.big.town.anative.SetModesReceiverDynamic$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SetModesReceiverDynamic.lambda$cycleMode$5(applicationContext, str2, str);
            }
        }, runnable);
    }

    static /* synthetic */ void lambda$cycleMode$5(Context context, String str, String str2) {
        boolean zSendRecuperationModeCommand;
        String strCurrentVehicleMode = MainActivity.currentVehicleMode(context, str);
        String strNextMode = SteeringActionPolicy.nextMode(str2, strCurrentVehicleMode);
        if (strNextMode == null) {
            return;
        }
        if ("driveMode".equals(str)) {
            zSendRecuperationModeCommand = MainActivity.sendDriveModeCommand(context, strNextMode);
        } else if ("energy".equals(str)) {
            zSendRecuperationModeCommand = MainActivity.sendEnergyModeCommand(context, strNextMode);
        } else {
            zSendRecuperationModeCommand = MainActivity.sendRecuperationModeCommand(context, strNextMode);
        }
        if (!zSendRecuperationModeCommand) {
            Log.w(TAG, "STEER_ACTION " + str + ": CAN failed, selection not persisted");
            return;
        }
        ApplyEngine.noteVehicleMode(str, strNextMode);
        MainActivity.persistExplicitMode(context, str, strNextMode);
        Log.i(TAG, "STEER_ACTION " + str + ": набор=" + str2 + " тек=" + strCurrentVehicleMode + " → " + strNextMode);
    }

    private static void toggleSetting(Context context, final String str, Runnable runnable) {
        final Context applicationContext = context.getApplicationContext();
        ApplyEngine.postUserCommand("steer " + str, new Runnable() { // from class: ru.big.town.anative.SetModesReceiverDynamic$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SetModesReceiverDynamic.lambda$toggleSetting$6(applicationContext, str);
            }
        }, runnable);
    }

    static /* synthetic */ void lambda$toggleSetting$6(Context context, String str) {
        boolean zSendPedestrianSoundCommand;
        boolean zCurrentSavedToggle = MainActivity.currentSavedToggle(context, str);
        boolean z = !zCurrentSavedToggle;
        if ("forcedEv".equals(str)) {
            zSendPedestrianSoundCommand = MainActivity.sendForcedEvCommand(z);
        } else if ("suspensionMaintenance".equals(str)) {
            zSendPedestrianSoundCommand = MainActivity.sendSuspensionMaintenanceCommand(context, z);
        } else if (!"disablePedestrianSound".equals(str)) {
            return;
        } else {
            zSendPedestrianSoundCommand = MainActivity.sendPedestrianSoundCommand(z);
        }
        if (!zSendPedestrianSoundCommand) {
            Log.w(TAG, "STEER_ACTION " + str + ": CAN failed, toggle not persisted");
        } else {
            MainActivity.persistSavedToggle(context, str, z);
            Log.i(TAG, "STEER_ACTION " + str + ": " + zCurrentSavedToggle + " → " + z);
        }
    }

    private static void toggleHeadlights(Context context, final Runnable runnable) {
        final Context applicationContext = context.getApplicationContext();
        final ManualAutoGate.Ticket ticketReserveManualHeadlightCommand = LightSensorService.reserveManualHeadlightCommand();
        ApplyEngine.postUserCommand("steer headlights", new Runnable() { // from class: ru.big.town.anative.SetModesReceiverDynamic$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                SetModesReceiverDynamic.lambda$toggleHeadlights$7(applicationContext);
            }
        }, new Runnable() { // from class: ru.big.town.anative.SetModesReceiverDynamic$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                SetModesReceiverDynamic.lambda$toggleHeadlights$8(ticketReserveManualHeadlightCommand, runnable);
            }
        });
    }

    static /* synthetic */ void lambda$toggleHeadlights$7(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("NativePrefs", 0);
        boolean z = sharedPreferences.getBoolean("steerHeadlightsOn", false);
        boolean z2 = !z;
        boolean manualAutoOverride = LightSensorService.setManualAutoOverride(false);
        if (MainActivity.setHeadlights(context, z2)) {
            sharedPreferences.edit().putBoolean("steerHeadlightsOn", z2).apply();
            Log.i(TAG, "STEER_ACTION headlights: " + z + " → " + z2);
        } else {
            LightSensorService.setManualAutoOverride(manualAutoOverride);
            Log.w(TAG, "STEER_ACTION headlights: CAN failed, state not persisted");
        }
    }

    static /* synthetic */ void lambda$toggleHeadlights$8(ManualAutoGate.Ticket ticket, Runnable runnable) {
        ticket.close();
        completeSteerAction(runnable);
    }

    private static void toggleHeadlightsAuto(Context context, final Runnable runnable) {
        final Context applicationContext = context.getApplicationContext();
        final ManualAutoGate.Ticket ticketReserveManualHeadlightCommand = LightSensorService.reserveManualHeadlightCommand();
        ApplyEngine.postUserCommand("steer headlights auto/low", new Runnable() { // from class: ru.big.town.anative.SetModesReceiverDynamic$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                SetModesReceiverDynamic.lambda$toggleHeadlightsAuto$9(applicationContext);
            }
        }, new Runnable() { // from class: ru.big.town.anative.SetModesReceiverDynamic$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                SetModesReceiverDynamic.lambda$toggleHeadlightsAuto$10(ticketReserveManualHeadlightCommand, runnable);
            }
        });
    }

    static /* synthetic */ void lambda$toggleHeadlightsAuto$9(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("NativePrefs", 0);
        boolean z = sharedPreferences.getBoolean("steerHeadlightsAutoLowBeam", false);
        boolean z2 = !z;
        boolean manualAutoOverride = LightSensorService.setManualAutoOverride(z);
        if (MainActivity.setHeadlightsAutoLow(context, z2)) {
            sharedPreferences.edit().putBoolean("steerHeadlightsAutoLowBeam", z2).apply();
            Log.i(TAG, "STEER_ACTION headlights auto/low: " + (z ? "LOW_BEAM" : "AUTO") + " → " + (z ? "AUTO" : "LOW_BEAM"));
        } else {
            LightSensorService.setManualAutoOverride(manualAutoOverride);
            Log.w(TAG, "STEER_ACTION headlights auto/low: CAN failed, state not persisted");
        }
    }

    static /* synthetic */ void lambda$toggleHeadlightsAuto$10(ManualAutoGate.Ticket ticket, Runnable runnable) {
        ticket.close();
        completeSteerAction(runnable);
    }
}
