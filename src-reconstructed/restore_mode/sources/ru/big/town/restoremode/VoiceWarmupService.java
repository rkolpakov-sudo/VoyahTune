package ru.big.town.restoremode;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.core.app.NotificationCompat;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes2.dex */
public final class VoiceWarmupService extends Service {
    private static final String CHANNEL = "voice_ready";
    private static final int NOTIFICATION = 4102;
    private boolean destroyed;
    private SharedPreferences prefs;
    private boolean requested;
    private boolean retaining;
    private int requestedDb = -1;
    private final SharedPreferences.OnSharedPreferenceChangeListener changes = new SharedPreferences.OnSharedPreferenceChangeListener() { // from class: ru.big.town.restoremode.VoiceWarmupService$$ExternalSyntheticLambda1
        @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
        public final void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
            this.f$0.m1992lambda$new$0$rubigtownrestoremodeVoiceWarmupService(sharedPreferences, str);
        }
    };

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    /* JADX INFO: renamed from: lambda$new$0$ru-big-town-restoremode-VoiceWarmupService, reason: not valid java name */
    /* synthetic */ void m1992lambda$new$0$rubigtownrestoremodeVoiceWarmupService(SharedPreferences sharedPreferences, String str) {
        if ("voiceAssistantEnabled".equals(str) || "voiceDeepFilterAttenuationDb".equals(str)) {
            update();
        }
    }

    static void sync(Context context) {
        Context applicationContext = context.getApplicationContext();
        Intent intent = new Intent(applicationContext, (Class<?>) VoiceWarmupService.class);
        if (!applicationContext.getSharedPreferences("DrivePreferences", 0).getBoolean("voiceAssistantEnabled", false)) {
            applicationContext.stopService(intent);
            return;
        }
        try {
            applicationContext.startForegroundService(intent);
        } catch (RuntimeException e) {
            Log.w("VoyahVoice", "Cannot start resident voice service", e);
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.prefs = getSharedPreferences("DrivePreferences", 0);
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL, "Готовность голосового помощника", 2);
        notificationChannel.setSound(null, null);
        notificationChannel.enableVibration(false);
        ((NotificationManager) getSystemService(NotificationManager.class)).createNotificationChannel(notificationChannel);
        Notification notification = notification("Подготовка помощника…");
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(NOTIFICATION, notification, BasicMeasure.EXACTLY);
        } else {
            startForeground(NOTIFICATION, notification);
        }
        this.prefs.registerOnSharedPreferenceChangeListener(this.changes);
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        update();
        return this.prefs.getBoolean("voiceAssistantEnabled", false) ? 1 : 2;
    }

    private void update() {
        if (this.destroyed) {
            return;
        }
        if (!this.prefs.getBoolean("voiceAssistantEnabled", false)) {
            this.requested = false;
            releaseModels();
            stopForeground(1);
            stopSelf();
            return;
        }
        int i = VoiceAudioConfig.read(this.prefs).deepFilterDb;
        if (this.requested && this.requestedDb == i) {
            return;
        }
        this.requested = true;
        this.requestedDb = i;
        this.retaining = true;
        ((NotificationManager) getSystemService(NotificationManager.class)).notify(NOTIFICATION, notification("Подготовка помощника…"));
        VoiceRecognizer.keepWarm(this, new Consumer() { // from class: ru.big.town.restoremode.VoiceWarmupService$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.m1993lambda$update$1$rubigtownrestoremodeVoiceWarmupService((Boolean) obj);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$update$1$ru-big-town-restoremode-VoiceWarmupService, reason: not valid java name */
    /* synthetic */ void m1993lambda$update$1$rubigtownrestoremodeVoiceWarmupService(Boolean bool) {
        if (this.destroyed) {
            return;
        }
        if (!bool.booleanValue()) {
            this.requested = false;
        }
        ((NotificationManager) getSystemService(NotificationManager.class)).notify(NOTIFICATION, notification(bool.booleanValue() ? "Модели загружены · быстрый запуск" : "Подготовка не удалась. Откройте помощника для повтора"));
    }

    private Notification notification(String str) {
        return new Notification.Builder(this, CHANNEL).setSmallIcon(R.drawable.ic_voice_command).setContentTitle("Голосовой помощник VoyahTune").setContentText(str).setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, (Class<?>) MainActivity.class), 201326592)).setOngoing(true).setOnlyAlertOnce(true).setShowWhen(false).setCategory(NotificationCompat.CATEGORY_SERVICE).build();
    }

    private void releaseModels() {
        if (this.retaining) {
            this.retaining = false;
            VoiceRecognizer.stopKeepingWarm();
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.destroyed = true;
        this.prefs.unregisterOnSharedPreferenceChangeListener(this.changes);
        releaseModels();
        super.onDestroy();
    }
}
