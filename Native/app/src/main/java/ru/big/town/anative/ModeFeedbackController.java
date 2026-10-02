package ru.big.town.anative;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import androidx.core.content.ContextCompat;

/* JADX INFO: loaded from: classes2.dex */
final class ModeFeedbackController implements AutoCloseable {
    static final String ACTION_REMEMBER_LAST_CHANGED = "ru.big.town.anative.MODE_REMEMBER_CHANGED";
    private static final String BIND_PERMISSION = "ru.big.town.anative.permission.BIND_SET_MODES_SERVICE";
    private static final String TAG = "$$$ ModeFeedback $$$";
    private final Context appContext;
    private volatile boolean closed;
    private final Handler feedbackHandler;
    private boolean receiverRegistered;
    private final BroadcastReceiver rememberLastReceiver = new BroadcastReceiver() { // from class: ru.big.town.anative.ModeFeedbackController.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (ModeFeedbackController.this.closed) {
                return;
            }
            String stringExtra = intent.getStringExtra("modeKey");
            boolean z = true;
            if (intent.hasExtra("rememberLast") && !intent.getBooleanExtra("rememberLast", true)) {
                z = false;
            }
            MainActivity.updateRememberLastMode(ModeFeedbackController.this.appContext, stringExtra, z);
        }
    };

    static ModeFeedbackController create(Context context, Handler handler) {
        ModeFeedbackController modeFeedbackController = new ModeFeedbackController(context, handler);
        modeFeedbackController.start();
        return modeFeedbackController;
    }

    private ModeFeedbackController(Context context, Handler handler) {
        this.appContext = context.getApplicationContext();
        this.feedbackHandler = handler;
    }

    private void start() {
        try {
            ContextCompat.registerReceiver(this.appContext, this.rememberLastReceiver, new IntentFilter(ACTION_REMEMBER_LAST_CHANGED), BIND_PERMISSION, this.feedbackHandler, 2);
            this.receiverRegistered = true;
        } catch (RuntimeException e) {
            close();
            throw e;
        }
    }

    void onConnected() {
        if (this.closed) {
            return;
        }
        ApplyEngine.activateWake("CanBus connected");
    }

    void onVehicleState(int i, int i2) {
        if (this.closed) {
            return;
        }
        ModeFeedbackDecoder.Feedback feedbackDecode = ModeFeedbackDecoder.decode(i, i2);
        if (feedbackDecode == null) {
            if (NativeLog.get().isRunning()) {
                Log.i(TAG, "unknown mode state ignored id=" + i + " state=" + i2);
            }
        } else {
            try {
                ApplyEngine.persistModeFeedbackIfAllowed(this.appContext, feedbackDecode.modeKey, feedbackDecode.mode);
            } catch (RuntimeException e) {
                Log.w(TAG, "persist feedback: " + e.getMessage());
            }
            if (NativeLog.get().isRunning()) {
                Log.i(TAG, "VSTATE mode id=" + i + " state=" + i2);
            }
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        if (this.receiverRegistered) {
            try {
                this.appContext.unregisterReceiver(this.rememberLastReceiver);
            } catch (IllegalArgumentException unused) {
            }
            this.receiverRegistered = false;
        }
    }
}
