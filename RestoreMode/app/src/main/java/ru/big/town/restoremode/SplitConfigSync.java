package ru.big.town.restoremode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
final class SplitConfigSync {
    private static final String CONFIG_RECEIVER = "ru.big.town.anative.SetModesConfigReceiver";
    private static final String NATIVE_PKG = "ru.big.town.anative";

    private SplitConfigSync() {
    }

    static void pushAll(Context context, SharedPreferences prefs) {
        pushFullscreenApps(context, prefs);
        pushAppDpi(context, prefs, null, 0);
        pushDock(context, prefs);
        pushSteering(context, prefs);
        pushKeyboard(context, prefs);
    }

    static void pushFullscreenApps(Context context, SharedPreferences prefs) {
        Intent intentConfigIntent = configIntent("ru.big.town.anative.FULLSCREEN_APPS_CONFIG");
        intentConfigIntent.putExtra("packagesCsv", FullscreenAppStore.snapshotCsv(prefs));
        context.sendBroadcast(intentConfigIntent);
    }

    static void pushAppDpi(Context context, SharedPreferences prefs, String str, int i) {
        Intent intentConfigIntent = configIntent("ru.big.town.anative.APP_DPI_CONFIG");
        intentConfigIntent.putExtra("appDpiJson", AppDpiStore.snapshotJson(prefs));
        if (str != null && !str.isEmpty()) {
            intentConfigIntent.putExtra("changedPkg", str);
            intentConfigIntent.putExtra("changedDpi", Math.max(0, i));
        }
        context.sendBroadcast(intentConfigIntent);
    }

    static void pushDock(Context context, SharedPreferences prefs) {
        String string = prefs.getString("dockOverride1", "");
        String string2 = prefs.getString("dockOverride2", "");
        Intent intentConfigIntent = configIntent("ru.big.town.anative.DOCK_CONFIG");
        intentConfigIntent.putExtra("dock1", string.isEmpty() ? "none" : string);
        intentConfigIntent.putExtra("dock2", string2.isEmpty() ? "none" : string2);
        intentConfigIntent.putExtra("dock1Dpi", string.isEmpty() ? 0 : AppDpiStore.get(prefs, string));
        intentConfigIntent.putExtra("dock2Dpi", string2.isEmpty() ? 0 : AppDpiStore.get(prefs, string2));
        addDockSplitExtras(intentConfigIntent, 1, string, prefs);
        addDockSplitExtras(intentConfigIntent, 2, string2, prefs);
        context.sendBroadcast(intentConfigIntent);
    }

    static void pushSteering(Context context, SharedPreferences prefs) {
        Intent intentConfigIntent = configIntent("ru.big.town.anative.STEER_CONFIG");
        intentConfigIntent.putExtra("steerStarShort", resolveSteerActions(prefs.getString("steerStarShort", "none"), prefs));
        intentConfigIntent.putExtra("steerStarLong", resolveSteerActions(prefs.getString("steerStarLong", "none"), prefs));
        intentConfigIntent.putExtra("steerDvrShort", resolveSteerActions(prefs.getString("steerDvrShort", "none"), prefs));
        intentConfigIntent.putExtra("steerDvrLong", resolveSteerActions(prefs.getString("steerDvrLong", "none"), prefs));
        String[] strArr = {"steerVoiceShort", "steerVoiceLong"};
        for (int i = 0; i < 2; i++) {
            String str = strArr[i];
            intentConfigIntent.putExtra(str, VoiceSteeringPolicy.publishedAction(prefs.getBoolean("voiceAssistantEnabled", false), prefs.getString("voiceAssistantSteeringPress", "long"), str, resolveSteerActions(prefs.getString(str, "none"), prefs)));
        }
        intentConfigIntent.putExtra("steerPhoneShort", resolveSteerActions(prefs.getString("steerPhoneShort", "none"), prefs));
        intentConfigIntent.putExtra("steerPhoneLong", resolveSteerActions(prefs.getString("steerPhoneLong", "none"), prefs));
        context.sendBroadcast(intentConfigIntent);
    }

    static void pushKeyboard(Context context, SharedPreferences prefs) {
        String strNormalizeKeyboardMode = normalizeKeyboardMode(prefs.getString("keyboardMode", "off"));
        Intent intentConfigIntent = configIntent("ru.big.town.anative.KEYBOARD_CONFIG");
        intentConfigIntent.putExtra("keyboardMode", strNormalizeKeyboardMode);
        context.sendBroadcast(intentConfigIntent);
    }

    static String normalizeKeyboardMode(String str) {
        return ("en".equals(str) || "ru".equals(str)) ? str : "off";
    }

    private static Intent configIntent(String str) {
        Intent intent = new Intent(str);
        intent.setClassName(NATIVE_PKG, CONFIG_RECEIVER);
        return intent;
    }

    private static void addDockSplitExtras(Intent intent, int i, String str, SharedPreferences prefs) {
        String strResolve = DockLongPressAction.resolve(prefs, i);
        intent.putExtra("dock" + i + "LongAction", strResolve);
        if (!TileOrderStore.Tile.TYPE_SPLIT.equals(strResolve)) {
            intent.putExtra("dock" + i + "HasSplit", false);
            return;
        }
        int i2 = str.isEmpty() ? -1 : prefs.getInt("dockOverride" + i + "Split", -1);
        List<SplitStore.Preset> listLoad = SplitStore.load(prefs);
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
        intent.putExtra("dock" + i + "SplitLDpi", AppDpiStore.get(prefs, preset.l));
        intent.putExtra("dock" + i + "SplitRDpi", AppDpiStore.get(prefs, preset.r));
        intent.putExtra("dock" + i + "SplitResizable", preset.resizable);
        intent.putExtra("dock" + i + "SplitFraction", SplitStore.leftFraction(preset));
        intent.putExtra("dock" + i + "SplitPresetIdx", i2);
        intent.putExtra("dock" + i + "SplitPresetId", preset.id);
    }

    static String resolveSteerActions(String str, SharedPreferences prefs) {
        ArrayList arrayList = new ArrayList();
        Iterator<String> it = SteeringActionStore.decode(str).iterator();
        while (it.hasNext()) {
            String strResolveSteerAction = resolveSteerAction(it.next(), prefs);
            if (strResolveSteerAction != null && !strResolveSteerAction.isEmpty() && !"none".equals(strResolveSteerAction)) {
                arrayList.add(strResolveSteerAction);
            }
        }
        return SteeringActionStore.encode(arrayList);
    }

    static String resolveSteerAction(String str, SharedPreferences prefs) {
        if (str == null || !str.startsWith("split:")) {
            return str;
        }
        try {
            int i = Integer.parseInt(str.substring("split:".length()));
            List<SplitStore.Preset> listLoad = SplitStore.load(prefs);
            if (i >= 0 && i < listLoad.size() && listLoad.get(i).ready()) {
                SplitStore.Preset preset = listLoad.get(i);
                return "split:" + preset.l + "," + preset.r + "," + preset.ratio + "," + AppDpiStore.get(prefs, preset.l) + "," + AppDpiStore.get(prefs, preset.r) + "," + (preset.resizable ? "1" : "0") + "," + SplitStore.leftFraction(preset) + "," + preset.id;
            }
        } catch (Exception unused) {
        }
        return "none";
    }
}
