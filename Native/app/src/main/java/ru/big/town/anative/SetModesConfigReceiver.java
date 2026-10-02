package ru.big.town.anative;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import kotlinx.coroutines.DebugKt;

/* JADX INFO: loaded from: classes2.dex */
public class SetModesConfigReceiver extends BroadcastReceiver {
    private static final String TAG = "$$$ SetModesConfig $$$";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("ru.big.town.anative.STEER_CONFIG".equals(action)) {
            String[] strArr = {"Star", "Dvr", "Voice", "Phone"};
            boolean zContains = false;
            for (int i = 0; i < 4; i++) {
                String str = strArr[i];
                String str2 = "steer" + str + "Short";
                String str3 = "steer" + str + "Long";
                SetModesReceiverDynamic.mirrorSteer(context, intent, str2);
                SetModesReceiverDynamic.mirrorSteer(context, intent, str3);
                zContains = zContains | SteeringActionSequence.contains(intent.getStringExtra(str2), "system_back") | SteeringActionSequence.contains(intent.getStringExtra(str3), "system_back");
            }
            BackButtonService.setSteeringBackEnabled(context, SteeringActionSequence.contains(intent.getStringExtra("steerVoiceLong"), "voice_assistant") | zContains | SteeringActionSequence.contains(intent.getStringExtra("steerVoiceShort"), "voice_assistant"));
            Log.i(TAG, "STEER_CONFIG зеркалирован");
            return;
        }
        if ("ru.big.town.anative.DOCK_CONFIG".equals(action)) {
            SetModesReceiverDynamic.mirrorDock(context, intent, 1);
            SetModesReceiverDynamic.mirrorDock(context, intent, 2);
            SetModesReceiverDynamic.clearLegacyPassengerDock(context);
            Intent intent2 = new Intent("ru.big.town.anative.DOCK_RELOAD");
            intent2.addFlags(32);
            context.sendBroadcast(intent2);
            SetModesReceiverDynamic.sendWinReload(context);
            Log.i(TAG, "DOCK_CONFIG зеркалирован + reload");
            return;
        }
        if ("ru.big.town.anative.FREEFORM_CONFIG".equals(action)) {
            SetModesReceiverDynamic.mirrorFreeform(context, intent);
            SetModesReceiverDynamic.sendWinReload(context);
            Log.i(TAG, "FREEFORM_CONFIG зеркалирован + reload");
            return;
        }
        if ("ru.big.town.anative.FULLSCREEN_APPS_CONFIG".equals(action)) {
            SetModesReceiverDynamic.mirrorFullscreenApps(context, intent);
            Intent intent3 = new Intent("ru.big.town.anative.DOCK_RELOAD");
            intent3.addFlags(32);
            context.sendBroadcast(intent3);
            SetModesReceiverDynamic.sendWinReload(context);
            Log.i(TAG, "FULLSCREEN_APPS_CONFIG зеркалирован + dock/window reload");
            return;
        }
        if ("ru.big.town.anative.APP_DPI_CONFIG".equals(action)) {
            SetModesReceiverDynamic.mirrorAppDpi(context, intent);
            SetModesReceiverDynamic.sendWinReload(context);
            Log.i(TAG, "APP_DPI_CONFIG зеркалирован + reload");
        } else if ("ru.big.town.anative.KEYBOARD_CONFIG".equals(action)) {
            applyKeyboardMode(context, intent.getStringExtra("keyboardMode"));
        }
    }

    private static void applyKeyboardMode(Context context, String str) {
        String strNormalizeKeyboardMode = normalizeKeyboardMode(str);
        String strNormalizeKeyboardMode2 = normalizeKeyboardMode(Settings.Global.getString(context.getContentResolver(), "voyahtune_keyboard_mode"));
        if (!Settings.Global.putString(context.getContentResolver(), "voyahtune_keyboard_mode", strNormalizeKeyboardMode)) {
            Log.e(TAG, "KEYBOARD_CONFIG: Settings.Global write failed");
            return;
        }
        if (strNormalizeKeyboardMode.equals(strNormalizeKeyboardMode2)) {
            Log.i(TAG, "KEYBOARD_CONFIG unchanged: " + strNormalizeKeyboardMode);
            return;
        }
        try {
            ActivityManager.class.getMethod("forceStopPackage", String.class).invoke((ActivityManager) context.getSystemService("activity"), "com.qinggan.app.qgime");
            Log.i(TAG, "KEYBOARD_CONFIG=" + strNormalizeKeyboardMode + "; Qinggan IME restarted");
        } catch (Exception e) {
            Log.e(TAG, "KEYBOARD_CONFIG saved, but Qinggan IME restart failed", e);
        }
    }

    private static String normalizeKeyboardMode(String str) {
        return ("en".equals(str) || "ru".equals(str)) ? str : DebugKt.DEBUG_PROPERTY_VALUE_OFF;
    }
}
