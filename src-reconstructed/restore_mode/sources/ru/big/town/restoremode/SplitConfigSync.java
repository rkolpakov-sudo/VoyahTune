package ru.big.town.restoremode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlinx.coroutines.DebugKt;

/* JADX INFO: loaded from: classes2.dex */
final class SplitConfigSync {
    private static final String CONFIG_RECEIVER = "ru.big.town.anative.SetModesConfigReceiver";
    private static final String NATIVE_PKG = "ru.big.town.anative";

    private SplitConfigSync() {
    }

    static void pushAll(Context context, SharedPreferences sharedPreferences) {
        pushFullscreenApps(context, sharedPreferences);
        pushAppDpi(context, sharedPreferences, null, 0);
        pushDock(context, sharedPreferences);
        pushSteering(context, sharedPreferences);
        pushKeyboard(context, sharedPreferences);
    }

    static void pushFullscreenApps(Context context, SharedPreferences sharedPreferences) {
        Intent intentConfigIntent = configIntent("ru.big.town.anative.FULLSCREEN_APPS_CONFIG");
        intentConfigIntent.putExtra("packagesCsv", FullscreenAppStore.snapshotCsv(sharedPreferences));
        context.sendBroadcast(intentConfigIntent);
    }

    static void pushAppDpi(Context context, SharedPreferences sharedPreferences, String str, int i) {
        Intent intentConfigIntent = configIntent("ru.big.town.anative.APP_DPI_CONFIG");
        intentConfigIntent.putExtra("appDpiJson", AppDpiStore.snapshotJson(sharedPreferences));
        if (str != null && !str.isEmpty()) {
            intentConfigIntent.putExtra("changedPkg", str);
            intentConfigIntent.putExtra("changedDpi", Math.max(0, i));
        }
        context.sendBroadcast(intentConfigIntent);
    }

    static void pushDock(Context context, SharedPreferences sharedPreferences) {
        String string = sharedPreferences.getString("dockOverride1", "");
        String string2 = sharedPreferences.getString("dockOverride2", "");
        Intent intentConfigIntent = configIntent("ru.big.town.anative.DOCK_CONFIG");
        intentConfigIntent.putExtra("dock1", string.isEmpty() ? "none" : string);
        intentConfigIntent.putExtra("dock2", string2.isEmpty() ? "none" : string2);
        intentConfigIntent.putExtra("dock1Dpi", string.isEmpty() ? 0 : AppDpiStore.get(sharedPreferences, string));
        intentConfigIntent.putExtra("dock2Dpi", string2.isEmpty() ? 0 : AppDpiStore.get(sharedPreferences, string2));
        addDockSplitExtras(intentConfigIntent, 1, string, sharedPreferences);
        addDockSplitExtras(intentConfigIntent, 2, string2, sharedPreferences);
        context.sendBroadcast(intentConfigIntent);
    }

    static void pushSteering(Context context, SharedPreferences sharedPreferences) {
        Intent intentConfigIntent = configIntent("ru.big.town.anative.STEER_CONFIG");
        intentConfigIntent.putExtra("steerStarShort", resolveSteerActions(sharedPreferences.getString("steerStarShort", "none"), sharedPreferences));
        intentConfigIntent.putExtra("steerStarLong", resolveSteerActions(sharedPreferences.getString("steerStarLong", "none"), sharedPreferences));
        intentConfigIntent.putExtra("steerDvrShort", resolveSteerActions(sharedPreferences.getString("steerDvrShort", "none"), sharedPreferences));
        intentConfigIntent.putExtra("steerDvrLong", resolveSteerActions(sharedPreferences.getString("steerDvrLong", "none"), sharedPreferences));
        String[] strArr = {"steerVoiceShort", "steerVoiceLong"};
        for (int i = 0; i < 2; i++) {
            String str = strArr[i];
            intentConfigIntent.putExtra(str, VoiceSteeringPolicy.publishedAction(sharedPreferences.getBoolean("voiceAssistantEnabled", false), sharedPreferences.getString("voiceAssistantSteeringPress", "long"), str, resolveSteerActions(sharedPreferences.getString(str, "none"), sharedPreferences)));
        }
        intentConfigIntent.putExtra("steerPhoneShort", resolveSteerActions(sharedPreferences.getString("steerPhoneShort", "none"), sharedPreferences));
        intentConfigIntent.putExtra("steerPhoneLong", resolveSteerActions(sharedPreferences.getString("steerPhoneLong", "none"), sharedPreferences));
        context.sendBroadcast(intentConfigIntent);
    }

    static void pushKeyboard(Context context, SharedPreferences sharedPreferences) {
        String strNormalizeKeyboardMode = normalizeKeyboardMode(sharedPreferences.getString("keyboardMode", DebugKt.DEBUG_PROPERTY_VALUE_OFF));
        Intent intentConfigIntent = configIntent("ru.big.town.anative.KEYBOARD_CONFIG");
        intentConfigIntent.putExtra("keyboardMode", strNormalizeKeyboardMode);
        context.sendBroadcast(intentConfigIntent);
    }

    static String normalizeKeyboardMode(String str) {
        return ("en".equals(str) || "ru".equals(str)) ? str : DebugKt.DEBUG_PROPERTY_VALUE_OFF;
    }

    private static Intent configIntent(String str) {
        Intent intent = new Intent(str);
        intent.setClassName(NATIVE_PKG, CONFIG_RECEIVER);
        return intent;
    }

    private static void addDockSplitExtras(Intent intent, int i, String str, SharedPreferences sharedPreferences) {
        String strResolve = DockLongPressAction.resolve(sharedPreferences, i);
        intent.putExtra("dock" + i + "LongAction", strResolve);
        if (!TileOrderStore.Tile.TYPE_SPLIT.equals(strResolve)) {
            intent.putExtra("dock" + i + "HasSplit", false);
            return;
        }
        int i2 = str.isEmpty() ? -1 : sharedPreferences.getInt("dockOverride" + i + "Split", -1);
        List<SplitStore.Preset> listLoad = SplitStore.load(sharedPreferences);
        if (i2 < 0 || i2 >= listLoad.size() || !listLoad.get(i2).ready()) {
            intent.putExtra("dock" + i + "LongAction", "none");
            intent.putExtra("dock" + i + "HasSplit", false);
            return;
        }
        SplitStore.Preset preset = listLoad.get(i2);
        intent.putExtra("dock" + i + "HasSplit", true);
        intent.putExtra("dock" + i + "SplitL", preset.l);
        intent.putExtra("dock" + i + "SplitR", preset.r);
        intent.putExtra("dock" + i + "SplitRatio", preset.ratio);
        intent.putExtra("dock" + i + "SplitLDpi", AppDpiStore.get(sharedPreferences, preset.l));
        intent.putExtra("dock" + i + "SplitRDpi", AppDpiStore.get(sharedPreferences, preset.r));
        intent.putExtra("dock" + i + "SplitResizable", preset.resizable);
        intent.putExtra("dock" + i + "SplitFraction", SplitStore.leftFraction(preset));
        intent.putExtra("dock" + i + "SplitPresetIdx", i2);
        intent.putExtra("dock" + i + "SplitPresetId", preset.id);
    }

    static String resolveSteerActions(String str, SharedPreferences sharedPreferences) {
        ArrayList arrayList = new ArrayList();
        Iterator<String> it = SteeringActionStore.decode(str).iterator();
        while (it.hasNext()) {
            String strResolveSteerAction = resolveSteerAction(it.next(), sharedPreferences);
            if (strResolveSteerAction != null && !strResolveSteerAction.isEmpty() && !"none".equals(strResolveSteerAction)) {
                arrayList.add(strResolveSteerAction);
            }
        }
        return SteeringActionStore.encode(arrayList);
    }

    static String resolveSteerAction(String str, SharedPreferences sharedPreferences) {
        if (str == null || !str.startsWith("split:")) {
            return str;
        }
        try {
            int i = Integer.parseInt(str.substring("split:".length()));
            List<SplitStore.Preset> listLoad = SplitStore.load(sharedPreferences);
            if (i >= 0 && i < listLoad.size() && listLoad.get(i).ready()) {
                SplitStore.Preset preset = listLoad.get(i);
                return "split:" + preset.l + "," + preset.r + "," + preset.ratio + "," + AppDpiStore.get(sharedPreferences, preset.l) + "," + AppDpiStore.get(sharedPreferences, preset.r) + "," + (preset.resizable ? "1" : "0") + "," + SplitStore.leftFraction(preset) + "," + preset.id;
            }
        } catch (Exception unused) {
        }
        return "none";
    }
}
