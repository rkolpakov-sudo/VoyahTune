package ru.big.town.anative;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/* JADX INFO: loaded from: classes2.dex */
public class SetModesReceiverStatic extends BroadcastReceiver {
    static final String TAG = "$$$ SetModesReceiverStatic $$$";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent == null ? null : intent.getAction();
        if (!"android.intent.action.BOOT_COMPLETED".equals(action) && !"com.qinggan.intent.QINGGAN_BOOT_COMPLETE".equals(action)) {
            Log.w(TAG, "ignored unexpected action: " + action);
        } else {
            context.startForegroundService(new Intent(context, (Class<?>) SetModesService.class));
            Log.i(TAG, "onReceive boot action: " + action);
        }
    }
}
