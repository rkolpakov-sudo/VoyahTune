package ru.big.town.anative;

import android.content.Context;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

/* JADX INFO: loaded from: classes2.dex */
final class DockLaunchGuard {
    private static final long HOLD_MS = 5000;
    static final String KEY_PREFIX = "voyahtune_dockLaunchGuard";
    private static final String TAG = "voyahdock";

    private DockLaunchGuard() {
    }

    static void arm(Context context, int i, String str) {
        if (context == null || str == null || str.isEmpty()) {
            return;
        }
        if (i == 0 || i == 1) {
            try {
                Settings.Global.putString(context.getContentResolver(), KEY_PREFIX + i, (SystemClock.elapsedRealtime() + HOLD_MS) + "|" + str);
                Log.i(TAG, "launch guard armed screen=" + i + " pkg=" + str + " holdMs=5000");
            } catch (Exception e) {
                Log.w(TAG, "launch guard arm failed: " + e.getMessage());
            }
        }
    }
}
