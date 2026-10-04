package ru.big.town.restoremode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/* JADX INFO: loaded from: classes2.dex */
public final class SavedConfigSyncReceiver extends BroadcastReceiver {
    public static final String ACTION = "ru.big.town.restoremode.SYNC_SAVED_CONFIG";
    private static final String PREFS = "DrivePreferences";
    private static final String TAG = "SavedConfigSync";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !ACTION.equals(intent.getAction()) || intent.getComponent() == null) {
            return;
        }
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, 0);
            if (!prefs.edit().remove("dockPassengerOverride1").remove("dockPassengerOverride1Label").remove("dockPassengerOverride2").remove("dockPassengerOverride2Label").commit()) {
                Log.w(TAG, "obsolete passenger dock preferences could not be removed");
            }
            SplitConfigSync.pushAll(context, prefs);
            Log.i(TAG, "saved fullscreen/Dock/steering/app-DPI/keyboard configuration published");
        } catch (RuntimeException e) {
            Log.e(TAG, "saved configuration publication failed", e);
        }
    }
}
