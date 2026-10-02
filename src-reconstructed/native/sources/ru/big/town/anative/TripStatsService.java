package ru.big.town.anative;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class TripStatsService extends Service {
    public static final String ACTION_POWER_ON = "ru.big.town.anative.TRIP_POWER_ON";
    public static final String ACTION_REQUEST_TRIP_UPDATE = "ru.big.town.anative.REQUEST_TRIP_UPDATE";
    public static final String ACTION_TRIP_DELETE = "ru.big.town.anative.TRIP_DELETE";
    public static final String ACTION_TRIP_HISTORY = "ru.big.town.anative.TRIP_HISTORY";
    public static final String ACTION_TRIP_RESET = "ru.big.town.anative.TRIP_RESET";
    public static final String ACTION_TRIP_UPDATE = "ru.big.town.anative.TRIP_UPDATE";
    private static final long CAN_STATE_PUBLISH_COALESCE_MS = 250;
    private static final String CHANNEL_ID = "trip_stats_channel";
    private static final int DOOR_OPEN = 1;
    public static final String EXTRA_ACCUM_MS = "accumMs";
    public static final String EXTRA_DELETE_START = "deleteStart";
    public static final String EXTRA_DRIVE_START = "driveStartElapsed";
    public static final String EXTRA_IN_DRIVE = "inDrive";
    public static final String EXTRA_TRIPS_JSON = "tripsJson";
    public static final String EXTRA_TRIP_ACTIVE = "tripActive";
    private static final int GEAR_DRIVE = 3;
    private static final int MAX_TRIPS = 10;
    private static final long MIN_TRIP_MS = 300000;
    private static final String PREFS = "TripStats";
    private static final String TAG = "$$$ TripStatsService $$$";
    private boolean canStatePublishPending;
    private DriverDoorStateController.Subscription driverDoorSubscription;
    private GearStateController.Subscription gearStateSubscription;
    private Handler timerHandler;
    private volatile boolean destroyed = false;
    private boolean tripActive = false;
    private boolean inDrive = false;
    private long accumMs = 0;
    private long driveStartElapsed = 0;
    private long tripStartWall = 0;
    private int lastGear = -1;
    private int lastFLDoor = -1;
    private final Runnable canStatePublishRunnable = new Runnable() { // from class: ru.big.town.anative.TripStatsService$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.m2146lambda$new$0$rubigtownanativeTripStatsService();
        }
    };
    private final BroadcastReceiver requestReceiver = new AnonymousClass1();

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    /* JADX INFO: renamed from: lambda$new$0$ru-big-town-anative-TripStatsService, reason: not valid java name */
    /* synthetic */ void m2146lambda$new$0$rubigtownanativeTripStatsService() {
        this.canStatePublishPending = false;
        persistAndBroadcast();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onGear(int i) {
        if (i < 0 || i == this.lastGear) {
            return;
        }
        this.lastGear = i;
        boolean z = i == 3;
        if (z && !this.inDrive) {
            if (!this.tripActive) {
                this.tripActive = true;
                this.tripStartWall = System.currentTimeMillis();
                this.accumMs = 0L;
                Log.i(TAG, "поездка началась (первый Drive в цикле)");
            }
            this.inDrive = true;
            this.driveStartElapsed = SystemClock.elapsedRealtime();
            Log.i(TAG, "gear=Drive → таймер идёт");
        } else if (!z && this.inDrive) {
            this.accumMs += SystemClock.elapsedRealtime() - this.driveStartElapsed;
            this.inDrive = false;
            Log.i(TAG, "gear=" + i + " → пауза, накоплено=" + fmt(this.accumMs));
        }
        scheduleCanStatePublish();
    }

    private void onDoor(int i) {
        if (i < 0 || i == this.lastFLDoor) {
            return;
        }
        this.lastFLDoor = i;
        Log.i(TAG, "door: fLDoor=" + i);
        if (i == 1) {
            Log.i(TAG, "водительская дверь открыта → финализация поездки");
            finalizeTrip("door open");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPowerOn() {
        finalizeTrip("power on");
    }

    private void finalizeTrip(String str) {
        if (this.inDrive) {
            this.accumMs += SystemClock.elapsedRealtime() - this.driveStartElapsed;
            this.inDrive = false;
        }
        if (this.tripActive) {
            long j = this.accumMs;
            if (j >= MIN_TRIP_MS) {
                addTripToLog(this.tripStartWall, j);
                Log.i(TAG, "поездка финализирована (" + str + "): " + fmt(this.accumMs) + " → в лог");
            } else {
                Log.i(TAG, "поездка отброшена (" + str + ", короче 5 мин: " + fmt(this.accumMs) + ")");
            }
        }
        this.tripActive = false;
        this.accumMs = 0L;
        this.tripStartWall = 0L;
        this.driveStartElapsed = 0L;
        this.lastGear = -1;
        persistAndBroadcast();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetTrip() {
        boolean z = this.inDrive;
        this.accumMs = 0L;
        this.driveStartElapsed = SystemClock.elapsedRealtime();
        this.tripStartWall = z ? System.currentTimeMillis() : 0L;
        this.tripActive = z;
        this.inDrive = z;
        Log.i(TAG, "таймер сброшен вручную (inDrive=" + z + ")");
        persistAndBroadcast();
    }

    private void addTripToLog(long j, long j2) {
        if (!prefs().getBoolean("saveHistory", true)) {
            Log.i(TAG, "история поездок выключена — поездка не сохраняется в журнал");
            return;
        }
        try {
            JSONArray jSONArray = new JSONArray(prefs().getString(EXTRA_TRIPS_JSON, "[]"));
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("start", j);
            jSONObject.put("durationMs", j2);
            JSONArray jSONArray2 = new JSONArray();
            jSONArray2.put(jSONObject);
            for (int i = 0; i < jSONArray.length() && jSONArray2.length() < 10; i++) {
                jSONArray2.put(jSONArray.get(i));
            }
            prefs().edit().putString(EXTRA_TRIPS_JSON, jSONArray2.toString()).apply();
        } catch (Exception e) {
            Log.w(TAG, "addTripToLog: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setHistoryEnabled(boolean z) {
        prefs().edit().putBoolean("saveHistory", z).apply();
        if (z) {
            Log.i(TAG, "история поездок ВКЛ");
        } else {
            prefs().edit().putString(EXTRA_TRIPS_JSON, "[]").apply();
            Log.i(TAG, "история поездок ВЫКЛ → журнал очищен");
        }
        broadcastUpdate();
    }

    private String tripsJson() {
        return prefs().getString(EXTRA_TRIPS_JSON, "[]");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteTrip(long j) {
        try {
            JSONArray jSONArray = new JSONArray(tripsJson());
            JSONArray jSONArray2 = new JSONArray();
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject = jSONArray.getJSONObject(i);
                if (jSONObject.optLong("start", -1L) != j) {
                    jSONArray2.put(jSONObject);
                }
            }
            prefs().edit().putString(EXTRA_TRIPS_JSON, jSONArray2.toString()).apply();
            Log.i(TAG, "поездка удалена start=" + j + " → осталось " + jSONArray2.length());
        } catch (Exception e) {
            Log.w(TAG, "deleteTrip: " + e.getMessage());
        }
        broadcastUpdate();
    }

    private void scheduleCanStatePublish() {
        this.canStatePublishPending = true;
        this.timerHandler.removeCallbacks(this.canStatePublishRunnable);
        this.timerHandler.postDelayed(this.canStatePublishRunnable, CAN_STATE_PUBLISH_COALESCE_MS);
    }

    private void persistAndBroadcast() {
        this.canStatePublishPending = false;
        this.timerHandler.removeCallbacks(this.canStatePublishRunnable);
        persistState();
        broadcastUpdate();
    }

    private void persistState() {
        prefs().edit().putBoolean("curActive", this.tripActive).putBoolean("curInDrive", this.inDrive).putLong("curAccumMs", this.accumMs).putLong("curDriveStart", this.driveStartElapsed).putLong("curStartWall", this.tripStartWall).putInt("lastGear", this.lastGear).apply();
    }

    private void restoreState() {
        SharedPreferences sharedPreferencesPrefs = prefs();
        this.tripActive = sharedPreferencesPrefs.getBoolean("curActive", false);
        this.inDrive = sharedPreferencesPrefs.getBoolean("curInDrive", false);
        this.accumMs = sharedPreferencesPrefs.getLong("curAccumMs", 0L);
        this.driveStartElapsed = sharedPreferencesPrefs.getLong("curDriveStart", 0L);
        this.tripStartWall = sharedPreferencesPrefs.getLong("curStartWall", 0L);
        this.lastGear = sharedPreferencesPrefs.getInt("lastGear", -1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broadcastUpdate() {
        Intent intent = new Intent(ACTION_TRIP_UPDATE);
        intent.putExtra(EXTRA_TRIP_ACTIVE, this.tripActive);
        intent.putExtra(EXTRA_IN_DRIVE, this.inDrive);
        intent.putExtra(EXTRA_ACCUM_MS, this.accumMs);
        intent.putExtra(EXTRA_DRIVE_START, this.driveStartElapsed);
        intent.putExtra(EXTRA_TRIPS_JSON, tripsJson());
        sendBroadcast(intent);
    }

    /* JADX INFO: renamed from: ru.big.town.anative.TripStatsService$1, reason: invalid class name */
    class AnonymousClass1 extends BroadcastReceiver {
        AnonymousClass1() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TripStatsService.ACTION_TRIP_RESET.equals(action)) {
                Handler handler = TripStatsService.this.timerHandler;
                final TripStatsService tripStatsService = TripStatsService.this;
                handler.post(new Runnable() { // from class: ru.big.town.anative.TripStatsService$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        tripStatsService.resetTrip();
                    }
                });
            } else if (TripStatsService.ACTION_TRIP_DELETE.equals(action)) {
                final long longExtra = intent.getLongExtra(TripStatsService.EXTRA_DELETE_START, -1L);
                TripStatsService.this.timerHandler.post(new Runnable() { // from class: ru.big.town.anative.TripStatsService$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m2148lambda$onReceive$1$rubigtownanativeTripStatsService$1(longExtra);
                    }
                });
            } else if (TripStatsService.ACTION_TRIP_HISTORY.equals(action)) {
                final boolean booleanExtra = intent.getBooleanExtra("enabled", true);
                TripStatsService.this.timerHandler.post(new Runnable() { // from class: ru.big.town.anative.TripStatsService$1$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m2149lambda$onReceive$2$rubigtownanativeTripStatsService$1(booleanExtra);
                    }
                });
            } else {
                TripStatsService.this.broadcastUpdate();
            }
        }

        /* JADX INFO: renamed from: lambda$onReceive$1$ru-big-town-anative-TripStatsService$1, reason: not valid java name */
        /* synthetic */ void m2148lambda$onReceive$1$rubigtownanativeTripStatsService$1(long j) {
            TripStatsService.this.deleteTrip(j);
        }

        /* JADX INFO: renamed from: lambda$onReceive$2$ru-big-town-anative-TripStatsService$1, reason: not valid java name */
        /* synthetic */ void m2149lambda$onReceive$2$rubigtownanativeTripStatsService$1(boolean z) {
            TripStatsService.this.setHistoryEnabled(z);
        }
    }

    private SharedPreferences prefs() {
        return getSharedPreferences(PREFS, 0);
    }

    private static String fmt(long j) {
        long j2 = j / 1000;
        return (j2 / 60) + "м " + (j2 % 60) + "с";
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
        this.timerHandler = new Handler(Looper.getMainLooper());
        restoreState();
        createNotificationChannel();
        startForeground(4, new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("Статистика поездок").setContentText("Учёт времени в пути").setSmallIcon(R.drawable.ic_launcher_foreground).build());
        IntentFilter intentFilter = new IntentFilter(ACTION_REQUEST_TRIP_UPDATE);
        intentFilter.addAction(ACTION_TRIP_RESET);
        intentFilter.addAction(ACTION_TRIP_DELETE);
        intentFilter.addAction(ACTION_TRIP_HISTORY);
        ContextCompat.registerReceiver(this, this.requestReceiver, intentFilter, 2);
        VehicleStateControllers vehicleStateControllers = VehicleStateControllers.get(this);
        this.gearStateSubscription = vehicleStateControllers.gear().subscribe(this.timerHandler, new GearStateController.Listener() { // from class: ru.big.town.anative.TripStatsService$$ExternalSyntheticLambda2
            @Override // ru.big.town.anative.GearStateController.Listener
            public final void onGearChanged(int i) {
                this.f$0.onGear(i);
            }
        });
        this.driverDoorSubscription = vehicleStateControllers.driverDoor().subscribe(this.timerHandler, new DriverDoorStateController.Listener() { // from class: ru.big.town.anative.TripStatsService$$ExternalSyntheticLambda3
            @Override // ru.big.town.anative.DriverDoorStateController.Listener
            public final void onDriverDoorChanged(DriverDoorStateController.State state) {
                this.f$0.m2147lambda$onCreate$1$rubigtownanativeTripStatsService(state);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$onCreate$1$ru-big-town-anative-TripStatsService, reason: not valid java name */
    /* synthetic */ void m2147lambda$onCreate$1$rubigtownanativeTripStatsService(DriverDoorStateController.State state) {
        if (state.isLive()) {
            onDoor(state.frontLeft);
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        String action = intent != null ? intent.getAction() : null;
        Log.i(TAG, "onStartCommand() action=" + action);
        if (!ACTION_POWER_ON.equals(action)) {
            return 1;
        }
        this.timerHandler.post(new Runnable() { // from class: ru.big.town.anative.TripStatsService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.onPowerOn();
            }
        });
        return 1;
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        if (this.canStatePublishPending) {
            this.timerHandler.removeCallbacks(this.canStatePublishRunnable);
            this.canStatePublishPending = false;
            persistState();
        }
        this.destroyed = true;
        GearStateController.Subscription subscription = this.gearStateSubscription;
        DriverDoorStateController.Subscription subscription2 = this.driverDoorSubscription;
        this.gearStateSubscription = null;
        this.driverDoorSubscription = null;
        if (subscription != null) {
            subscription.close();
        }
        if (subscription2 != null) {
            subscription2.close();
        }
        try {
            unregisterReceiver(this.requestReceiver);
        } catch (Exception unused) {
        }
        this.timerHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Статистика поездок", 1);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
