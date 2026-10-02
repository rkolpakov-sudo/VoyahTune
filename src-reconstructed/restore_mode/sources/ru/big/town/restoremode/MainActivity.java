package ru.big.town.restoremode;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Outline;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import ru.big.town.common.SuspensionWidgetProtocol;

/* JADX INFO: loaded from: classes2.dex */
public class MainActivity extends AppCompatActivity {
    static final String ACTION_BATTERY_HEAT_ACTIVATE = "ru.big.town.anative.BATTERY_HEAT_ACTIVATE";
    static final String ACTION_BATTERY_HEAT_UPDATE = "ru.big.town.anative.BATTERY_HEAT_UPDATE";
    static final String ACTION_EMBEDDED_TASK_LEFT = "ru.big.town.anative.EMBEDDED_TASK_LEFT";
    static final String ACTION_OPEN_ON_DISPLAY = "ru.big.town.anative.OPEN_ON_DISPLAY";
    static final String ACTION_POWER_HOLD_STATUS_UPDATE = "ru.big.town.anative.POWER_HOLD_STATUS_UPDATE";
    static final String ACTION_REQUEST_BATTERY_HEAT = "ru.big.town.anative.REQUEST_BATTERY_HEAT";
    static final String ACTION_REQUEST_POWER_HOLD_STATUS = "ru.big.town.anative.REQUEST_POWER_HOLD_STATUS";
    static final String ACTION_REQUEST_TRIP_UPDATE = "ru.big.town.anative.REQUEST_TRIP_UPDATE";
    static final String ACTION_TRIP_RESET = "ru.big.town.anative.TRIP_RESET";
    static final String ACTION_TRIP_UPDATE = "ru.big.town.anative.TRIP_UPDATE";
    private static final int BH_COLOR_COLD = -12746800;
    private static final int BH_COLOR_FAULT = -3126710;
    private static final int BH_COLOR_HEATING = -13258646;
    private static final int BH_COLOR_NORMAL = -9735552;
    private static final int BH_COLOR_WARN = -3102417;
    private static final int BH_PHASE_ACTIVE = 3;
    private static final int BH_PHASE_AWAITING_CONFIRMATION = 2;
    private static final int BH_PHASE_BLOCKED = 4;
    private static final int BH_PHASE_ENABLED = 5;
    private static final int BH_PHASE_IDLE = 0;
    private static final int BH_PHASE_SENDING = 1;
    private static final int BH_PLATFORM_H97C = 2;
    private static final int BH_PLATFORM_H97X = 1;
    private static final int BH_TEMP_INVALID = -9999;
    private static final int BH_UNKNOWN = Integer.MIN_VALUE;
    private static final long BIND_RETRY_MS = 5000;
    private static final String BIND_SET_MODES_PERMISSION = "ru.big.town.anative.permission.BIND_SET_MODES_SERVICE";
    static final String EXTRA_EMBEDDED_TASK_PKG = "pkg";
    private static final int FULLSCREEN_GRID_COLUMNS_DEFAULT = 8;
    private static final int FULLSCREEN_GRID_COLUMNS_MAX = 12;
    private static final int LAUNCH_APPS_MAX_COLUMNS = 4;
    static final int MSG_APPLY_FORCED_EV = 35;
    static final int MSG_APPLY_PEDESTRIAN = 21;
    static final int MSG_APPLY_SUSPENSION_MAINTENANCE = 37;
    static final int MSG_AUTO_LIGHT_DISABLE = 11;
    static final int MSG_AUTO_LIGHT_ENABLE = 10;
    static final int MSG_EMBEDDED_TRANSFER = 38;
    static final int MSG_LEAVE_CAR = 20;
    static final int MSG_RESULT = 4;
    static final int MSG_SPLIT_LAUNCH_VD = 34;
    static final int MSG_WASH_MODE = 23;
    private static final int POWER_HOLD_ACTIVATING = 2;
    private static final int POWER_HOLD_ACTIVE = 3;
    private static final int POWER_HOLD_EXIT_LOW_BATTERY = 1;
    private static final int POWER_HOLD_EXIT_TIME_UP = 2;
    private static final int POWER_HOLD_FAILED = 4;
    private static final int POWER_HOLD_INACTIVE = 1;
    private static final int POWER_HOLD_REQUEST_ACCEPTED = 1;
    private static final int POWER_HOLD_REQUEST_LOW_BATTERY = 3;
    private static final int POWER_HOLD_REQUEST_NOT_IN_PARK = 2;
    private static final int POWER_HOLD_REQUEST_STATE_UNAVAILABLE = 4;
    private static final int POWER_HOLD_REQUEST_TRANSPORT_FAILURE = 5;
    private static final int POWER_HOLD_UNKNOWN = 0;
    static final int REQUEST_CODE = 1;
    static final String TAG = "$$$ MainActivityRestoreMode $$$";
    private static final DateTimeFormatter TRIP_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy, EEEE", Locale.forLanguageTag("ru"));
    private TextView autoLightBadge;
    private boolean autoLightOn;
    private TextView batteryHeatFail;
    private ImageView batteryHeatIcon;
    private TextView batteryHeatState;
    private TextView batteryHeatStatus;
    private TextView batteryHeatTemp;
    private Button buttonBatteryHeat;
    private View cardAutoLight;
    private View cardBatteryHeat;
    private View cardForcedEv;
    private View cardPedestrian;
    private View cardPowerHold;
    private View cardSuspensionMaintenance;
    private View cardWashMode;
    private int contentInsetTop;
    private TextView forcedEvBadge;
    private boolean forcedEvOn;
    private boolean fullscreenGrid;
    private int gridPaddingBottom;
    private int gridPaddingTop;
    private View launchAppsWidget;
    private TextView pedestrianBadge;
    private boolean pedestrianOn;
    private TextView powerHoldBadge;
    private SharedPreferences sharedPreferences;
    private GridLayout splitTilesGrid;
    private TextView suspensionMaintenanceBadge;
    private boolean suspensionMaintenanceOn;
    private boolean suspensionScreenResumed;
    private SuspensionWidgetView suspensionWidgetView;
    private TileDragController tileDragController;
    private View tripCard;
    private TextView tripDate;
    private TextView tripStatus;
    private TextView tripTimer;
    private String driveMode = "INDIVIDUAL";
    private String energy = "SREV";
    private String recycle = "LOW";
    private String customCommand = "";
    private int customCommandCount = 1;
    private Intent resultIntent = null;
    private Intent resultIntentStarButton = null;
    private SharedPreferences.Editor editor = null;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private boolean tripActive = false;
    private boolean tripInDrive = false;
    private long tripAccumMs = 0;
    private long tripDriveStartElapsed = 0;
    private String lastTripsJson = "[]";
    private final Map<String, TextureView> embeddedWidgetSurfaces = new HashMap();
    private final Map<String, Surface> embeddedWidgetOutputs = new HashMap();
    private final Map<String, String> embeddedWidgetPackages = new HashMap();
    private final Map<String, Integer> embeddedWidgetDpi = new HashMap();
    private final Set<String> embeddedSuppressRelease = new HashSet();
    private final Map<String, View> appWidgetTileViews = new HashMap();
    private Bundle suspensionState = new Bundle();
    private final Messenger suspensionClient = new Messenger(new Handler(Looper.getMainLooper(), new Handler.Callback() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda4
        @Override // android.os.Handler.Callback
        public final boolean handleMessage(Message message) {
            return this.f$0.m1890lambda$new$0$rubigtownrestoremodeMainActivity(message);
        }
    }));
    private final Runnable suspensionUnavailable = new Runnable() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda5
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.m1891lambda$new$1$rubigtownrestoremodeMainActivity();
        }
    };
    private final BroadcastReceiver tripReceiver = new BroadcastReceiver() { // from class: ru.big.town.restoremode.MainActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            MainActivity.this.tripActive = intent.getBooleanExtra("tripActive", false);
            MainActivity.this.tripInDrive = intent.getBooleanExtra("inDrive", false);
            MainActivity.this.tripAccumMs = intent.getLongExtra("accumMs", 0L);
            MainActivity.this.tripDriveStartElapsed = intent.getLongExtra("driveStartElapsed", 0L);
            String stringExtra = intent.getStringExtra("tripsJson");
            if (stringExtra != null) {
                MainActivity.this.lastTripsJson = stringExtra;
            }
            MainActivity.this.updateTripTimer();
        }
    };
    private final BroadcastReceiver batteryHeatReceiver = new BroadcastReceiver() { // from class: ru.big.town.restoremode.MainActivity.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            MainActivity.this.renderBatteryHeat(intent);
        }
    };
    private final BroadcastReceiver powerHoldStatusReceiver = new BroadcastReceiver() { // from class: ru.big.town.restoremode.MainActivity.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            MainActivity.this.renderPowerHoldStatus(intent);
        }
    };
    private final BroadcastReceiver embeddedLeftReceiver = new BroadcastReceiver() { // from class: ru.big.town.restoremode.MainActivity.4
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            MainActivity.this.releaseEmbeddedWidgetsOf(intent.getStringExtra(MainActivity.EXTRA_EMBEDDED_TASK_PKG));
        }
    };
    private final BroadcastReceiver settingSyncReceiver = new BroadcastReceiver() { // from class: ru.big.town.restoremode.MainActivity.5
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String stringExtra = intent.getStringExtra("key");
            if (stringExtra == null || !intent.hasExtra("value")) {
                return;
            }
            boolean booleanExtra = intent.getBooleanExtra("value", false);
            MainActivity.this.editor.putBoolean(stringExtra, booleanExtra).apply();
            if ("forcedEv".equals(stringExtra)) {
                MainActivity.this.forcedEvOn = booleanExtra;
            } else if ("autoLight".equals(stringExtra)) {
                MainActivity.this.autoLightOn = booleanExtra;
            } else if ("suspensionMaintenance".equals(stringExtra)) {
                MainActivity.this.suspensionMaintenanceOn = booleanExtra;
            } else {
                if (!"disablePedestrianSound".equals(stringExtra)) {
                    return;
                }
                MainActivity.this.pedestrianOn = !booleanExtra;
            }
            MainActivity.this.updateToggleVisuals();
        }
    };
    private final Runnable tripTick = new Runnable() { // from class: ru.big.town.restoremode.MainActivity.6
        @Override // java.lang.Runnable
        public void run() {
            MainActivity.this.updateTripTimer();
            MainActivity.this.uiHandler.postDelayed(this, 1000L);
        }
    };
    private boolean bindingRequested = false;
    private boolean connectionReported = false;
    private boolean destroyed = false;
    private final Runnable messengerRebindRunnable = new Runnable() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda6
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.bindToMessengerService();
        }
    };
    private ServiceConnection connection = new ServiceConnection() { // from class: ru.big.town.restoremode.MainActivity.7
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (MainActivity.this.destroyed) {
                return;
            }
            MainActivity.this.uiHandler.removeCallbacks(MainActivity.this.messengerRebindRunnable);
            MainActivity.this.bindingRequested = true;
            Log.i(MainActivity.TAG, "onServiceConnected()");
            if (MainActivity.this.connectionReported) {
                return;
            }
            MainActivity.this.connectionReported = true;
            GlobalVars.clientConnected(new Messenger(iBinder));
            MainActivity.this.watchSuspension();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            MainActivity.this.clearReportedConnection();
            MainActivity.this.suspensionState = new Bundle();
            if (MainActivity.this.suspensionWidgetView != null) {
                MainActivity.this.suspensionWidgetView.update(MainActivity.this.suspensionState);
            }
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            MainActivity.this.restartMessengerBinding("binding died");
        }

        @Override // android.content.ServiceConnection
        public void onNullBinding(ComponentName componentName) {
            MainActivity.this.restartMessengerBinding("null binding");
        }
    };

    /* JADX INFO: renamed from: lambda$new$0$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ boolean m1890lambda$new$0$rubigtownrestoremodeMainActivity(Message message) {
        if (message.what != 93) {
            return false;
        }
        Bundle bundle = new Bundle(message.getData());
        this.suspensionState = bundle;
        SuspensionWidgetView suspensionWidgetView = this.suspensionWidgetView;
        if (suspensionWidgetView == null) {
            return true;
        }
        suspensionWidgetView.update(bundle);
        return true;
    }

    /* JADX INFO: renamed from: lambda$new$1$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ void m1891lambda$new$1$rubigtownrestoremodeMainActivity() {
        if (!this.suspensionScreenResumed || this.suspensionState.containsKey(SuspensionWidgetProtocol.HEIGHT)) {
            return;
        }
        this.suspensionState.putString(SuspensionWidgetProtocol.MESSAGE, "Нужна связь с обновлённым Native");
        SuspensionWidgetView suspensionWidgetView = this.suspensionWidgetView;
        if (suspensionWidgetView != null) {
            suspensionWidgetView.update(this.suspensionState);
        }
    }

    private void sendSuspensionMessage(int i, int i2) {
        if (!GlobalVars.isBound || GlobalVars.serviceMessenger == null) {
            return;
        }
        try {
            Message messageObtain = Message.obtain(null, i, i2, 0);
            messageObtain.replyTo = this.suspensionClient;
            GlobalVars.serviceMessenger.send(messageObtain);
        } catch (RemoteException e) {
            Log.w(TAG, "Suspension service unavailable", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void watchSuspension() {
        this.uiHandler.removeCallbacks(this.suspensionUnavailable);
        boolean z = this.suspensionScreenResumed && this.sharedPreferences.getBoolean("showSuspensionWidget", false);
        sendSuspensionMessage(z ? 90 : 91, 0);
        if (z) {
            this.uiHandler.postDelayed(this.suspensionUnavailable, BIND_RETRY_MS);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseEmbeddedWidgetsOf(String str) {
        if (str == null) {
            return;
        }
        for (String str2 : new ArrayList(this.embeddedWidgetSurfaces.keySet())) {
            View view = this.appWidgetTileViews.get(str2);
            if (view != null && str.equals(this.embeddedWidgetPackages.get(str2))) {
                releaseEmbeddedWidget(str2, view);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void renderPowerHoldStatus(Intent intent) {
        if (this.powerHoldBadge == null || intent == null) {
            return;
        }
        int intExtra = intent.getIntExtra(NotificationCompat.CATEGORY_STATUS, 0);
        int intExtra2 = intent.getIntExtra("exitReason", 0);
        int intExtra3 = intent.getIntExtra("requestOutcome", 0);
        if (intExtra == 1) {
            if (intExtra2 == 1) {
                this.powerHoldBadge.setText(R.string.power_hold_status_exit_low_battery);
            } else if (intExtra2 == 2) {
                this.powerHoldBadge.setText(R.string.power_hold_status_exit_time_up);
            } else {
                this.powerHoldBadge.setText(R.string.power_hold_status_inactive);
            }
            this.powerHoldBadge.setBackgroundResource(R.drawable.pill_inactive);
        } else if (intExtra == 2) {
            this.powerHoldBadge.setText(R.string.power_hold_status_activating);
            this.powerHoldBadge.setBackgroundResource(R.drawable.pill_pending);
        } else if (intExtra == 3) {
            this.powerHoldBadge.setText(R.string.power_hold_status_active);
            this.powerHoldBadge.setBackgroundResource(R.drawable.pill_active);
        } else if (intExtra == 4) {
            this.powerHoldBadge.setText(R.string.power_hold_status_failed);
            this.powerHoldBadge.setBackgroundResource(R.drawable.pill_error);
        } else {
            this.powerHoldBadge.setText(R.string.power_hold_status_unknown);
            this.powerHoldBadge.setBackgroundResource(R.drawable.pill_inactive);
        }
        showPowerHoldRequestOutcome(intExtra3);
    }

    private void showPowerHoldRequestOutcome(int i) {
        if (i == 1) {
            showSnack(getString(R.string.power_hold_request_accepted));
            return;
        }
        if (i == 2) {
            showSnack(getString(R.string.power_hold_request_not_in_park));
            return;
        }
        if (i == 3) {
            showSnack(getString(R.string.power_hold_request_low_battery));
        } else if (i == 4) {
            showSnack(getString(R.string.power_hold_request_state_unavailable));
        } else {
            if (i != 5) {
                return;
            }
            showSnack(getString(R.string.power_hold_request_transport_failure));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTripTimer() {
        String str;
        if (this.tripDate != null) {
            String str2 = LocalDate.now().format(TRIP_DATE_FORMAT);
            if (!str2.contentEquals(this.tripDate.getText())) {
                this.tripDate.setText(str2);
            }
        }
        long jElapsedRealtime = this.tripAccumMs;
        if (this.tripActive && this.tripInDrive) {
            jElapsedRealtime += SystemClock.elapsedRealtime() - this.tripDriveStartElapsed;
        }
        TextView textView = this.tripTimer;
        if (textView != null) {
            textView.setText(fmtDuration(jElapsedRealtime));
        }
        TextView textView2 = this.tripStatus;
        if (textView2 != null) {
            if (this.tripActive) {
                str = this.tripInDrive ? "в пути" : "на паузе (не Drive)";
            } else {
                str = "нет активной поездки";
            }
            textView2.setText(str);
        }
    }

    private static String fmtDuration(long j) {
        long j2 = j / 1000;
        return String.format(Locale.US, "%d:%02d:%02d", Long.valueOf(j2 / 3600), Long.valueOf((j2 % 3600) / 60), Long.valueOf(j2 % 60));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void renderBatteryHeat(Intent intent) {
        String strBhControlState;
        if (this.batteryHeatTemp == null) {
            return;
        }
        int intExtra = intent.getIntExtra("ambientTemp", BH_TEMP_INVALID);
        int intExtra2 = intent.getIntExtra("controlStatus", Integer.MIN_VALUE);
        int intExtra3 = intent.getIntExtra("switchState", Integer.MIN_VALUE);
        int intExtra4 = intent.getIntExtra("preheatSet", Integer.MIN_VALUE);
        int intExtra5 = intent.getIntExtra("bmsState", Integer.MIN_VALUE);
        int intExtra6 = intent.getIntExtra("autoCtrl", Integer.MIN_VALUE);
        int intExtra7 = intent.getIntExtra("failReason", Integer.MIN_VALUE);
        int intExtra8 = intent.getIntExtra("vehiclePlatform", 0);
        int intExtra9 = intent.getIntExtra("activationPhase", 0);
        int intExtra10 = intent.getIntExtra("tempThreshold", 10);
        boolean z = (intExtra == BH_TEMP_INVALID || intExtra == Integer.MIN_VALUE) ? false : true;
        this.batteryHeatTemp.setText(z ? "за бортом: " + intExtra + " °C" : "за бортом: —");
        if (intExtra5 == 9) {
            strBhControlState = "идёт";
        } else {
            strBhControlState = bhControlState(intExtra8, intExtra4, intExtra3);
        }
        this.batteryHeatStatus.setText("Нагрев: " + bhHeating(intExtra2) + "   ·   Pre-heat: " + strBhControlState + "   ·   Автоподогрев: " + bhOnOff(intExtra6));
        String strBhFail = bhFail(intExtra7);
        if (strBhFail != null) {
            this.batteryHeatFail.setText("Не удалось запустить прогрев: " + strBhFail);
            this.batteryHeatFail.setVisibility(0);
        } else {
            this.batteryHeatFail.setVisibility(8);
        }
        Button button = this.buttonBatteryHeat;
        if (button != null) {
            boolean z2 = intExtra9 == 0;
            button.setEnabled(z2);
            this.buttonBatteryHeat.setAlpha(z2 ? 1.0f : 0.55f);
            if (intExtra9 == 1) {
                this.buttonBatteryHeat.setText("Отправка…");
            } else if (intExtra9 == 2) {
                this.buttonBatteryHeat.setText("Ожидаем ответ…");
            } else if (intExtra9 == 3) {
                this.buttonBatteryHeat.setText("Прогрев активен");
            } else if (intExtra9 == 5) {
                this.buttonBatteryHeat.setText("Контроль включён");
            } else if (intExtra9 == 4) {
                this.buttonBatteryHeat.setText("Сейчас недоступно");
            } else {
                this.buttonBatteryHeat.setText("Запустить прогрев");
            }
        }
        applyBatteryHeatIndicator(intExtra2, intExtra5, intExtra7, intExtra, z, intExtra10, intExtra9);
    }

    private void applyBatteryHeatIndicator(int i, int i2, int i3, int i4, boolean z, int i5, int i6) {
        String str;
        int i7;
        boolean z2 = (i == Integer.MIN_VALUE && i2 == Integer.MIN_VALUE && i3 == Integer.MIN_VALUE && !z) ? false : true;
        if (i2 == 8) {
            i7 = BH_COLOR_FAULT;
            str = "Неисправность";
        } else {
            if (i == 1 || i2 == 9 || i6 == 3) {
                str = "Прогрев";
            } else if (i3 < 1 || i3 > 4) {
                i7 = BH_COLOR_COLD;
                if (i6 == 1 || i6 == 2) {
                    str = "Ожидание";
                } else if (i6 == 5) {
                    str = "Контроль включён";
                } else if (z && i4 < i5) {
                    str = "Холодно";
                } else {
                    str = z2 ? "Норма" : "Нет данных";
                    i7 = BH_COLOR_NORMAL;
                }
            } else {
                i7 = BH_COLOR_WARN;
                str = "Внимание";
            }
            i7 = BH_COLOR_HEATING;
        }
        ImageView imageView = this.batteryHeatIcon;
        if (imageView != null) {
            imageView.setColorFilter(i7);
        }
        TextView textView = this.batteryHeatState;
        if (textView != null) {
            textView.setText(str);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(i7);
            gradientDrawable.setCornerRadius(getResources().getDisplayMetrics().density * 14.0f);
            this.batteryHeatState.setBackground(gradientDrawable);
        }
    }

    private static String bhHeating(int i) {
        if (i == 0) {
            return "нет";
        }
        if (i == 1) {
            return "идёт";
        }
        if (i == 2) {
            return "инициализация";
        }
        return "—";
    }

    private static String bhOnOff(int i) {
        if (i == 1) {
            return "вкл";
        }
        if (i == 2) {
            return "выкл";
        }
        return "—";
    }

    private static String bhControlState(int i, int i2, int i3) {
        if (i == 1) {
            if (i2 == 1) {
                return "вкл";
            }
            if (i2 == 0) {
                return "выкл";
            }
            return "—";
        }
        if (i == 2) {
            return bhOnOff(i3);
        }
        if (i2 == 1) {
            return "вкл";
        }
        if (i2 == 0) {
            return "выкл";
        }
        return bhOnOff(i3);
    }

    private static String bhFail(int i) {
        if (i == 1) {
            return "идёт зарядка";
        }
        if (i == 2) {
            return "высоковольтная сеть выключена";
        }
        if (i == 3) {
            return "низкий заряд батареи";
        }
        if (i != 4) {
            return null;
        }
        return "температура вне допустимого диапазона";
    }

    public void onButtonBatteryHeat(View view) {
        Intent intent = new Intent(ACTION_BATTERY_HEAT_ACTIVATE);
        intent.setPackage("ru.big.town.anative");
        sendBroadcast(intent);
        showSnack("Запрос отправлен, ожидаем подтверждение автомобиля…");
        Log.i(TAG, "BATTERY_HEAT_ACTIVATE отправлен");
    }

    public void onButtonTripHistory(View view) {
        Intent intent = new Intent(this, (Class<?>) TripHistoryActivity.class);
        intent.putExtra("tripsJson", this.lastTripsJson);
        startActivity(intent);
    }

    public void onButtonTripReset(View view) {
        new MaterialAlertDialogBuilder(this, R.style.DarkDialog).setTitle((CharSequence) "Сбросить таймер").setMessage((CharSequence) "Обнулить время текущей поездки? Действие не пишется в историю.").setPositiveButton((CharSequence) "Сбросить", new DialogInterface.OnClickListener() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda20
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                this.f$0.m1893lambda$onButtonTripReset$2$rubigtownrestoremodeMainActivity(dialogInterface, i);
            }
        }).setNegativeButton((CharSequence) "Отмена", (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: renamed from: lambda$onButtonTripReset$2$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ void m1893lambda$onButtonTripReset$2$rubigtownrestoremodeMainActivity(DialogInterface dialogInterface, int i) {
        Intent intent = new Intent(ACTION_TRIP_RESET);
        intent.setPackage("ru.big.town.anative");
        sendBroadcast(intent);
        Log.i(TAG, "TRIP_RESET отправлен");
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1 && i2 == -1) {
            Log.i("onActivityResult", String.format("requestCode - %d resultCode - %d data %s", Integer.valueOf(i), Integer.valueOf(i2), intent.toString()));
            this.customCommand = intent.getStringExtra("customCommand");
            int intExtra = intent.getIntExtra("customCommandCount", 1);
            this.customCommandCount = intExtra;
            Log.i("onActivityResult", String.format("customCommand=%s count=%d", this.customCommand, Integer.valueOf(intExtra)));
            this.editor.putString("customCommand", this.customCommand);
            this.editor.putInt("customCommandCount", this.customCommandCount);
            this.editor.apply();
        }
    }

    class IncomingHandler extends Handler {
        IncomingHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 4) {
                Log.i(MainActivity.TAG, "handleMessage() MSG_RESULT");
            } else {
                Log.i(MainActivity.TAG, "handleMessage() default");
                super.handleMessage(message);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bindToMessengerService() {
        if (this.destroyed || this.bindingRequested) {
            return;
        }
        this.uiHandler.removeCallbacks(this.messengerRebindRunnable);
        Log.i(TAG, "bindToMessengerService() begin");
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("ru.big.town.anative", "ru.big.town.anative.SetModesService"));
        try {
            this.bindingRequested = bindService(intent, this.connection, 1);
            Log.i(TAG, "bindToMessengerService() end, requested=" + this.bindingRequested);
            if (this.bindingRequested) {
                return;
            }
            scheduleMessengerRebind();
        } catch (RuntimeException e) {
            this.bindingRequested = false;
            Log.w(TAG, "bindToMessengerService() failed: " + e.getMessage());
            scheduleMessengerRebind();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearReportedConnection() {
        if (this.connectionReported) {
            this.connectionReported = false;
            GlobalVars.clientDisconnected();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restartMessengerBinding(String str) {
        Log.w(TAG, "SetModesService " + str + " — replacing binding");
        releaseMessengerBinding(str);
        scheduleMessengerRebind();
    }

    private void scheduleMessengerRebind() {
        if (this.destroyed) {
            return;
        }
        this.uiHandler.removeCallbacks(this.messengerRebindRunnable);
        this.uiHandler.postDelayed(this.messengerRebindRunnable, BIND_RETRY_MS);
    }

    private void releaseMessengerBinding(String str) {
        this.uiHandler.removeCallbacks(this.messengerRebindRunnable);
        clearReportedConnection();
        if (this.bindingRequested) {
            try {
                unbindService(this.connection);
            } catch (RuntimeException e) {
                Log.w(TAG, str + ": unbindService failed: " + e.getMessage());
            }
        }
        this.bindingRequested = false;
    }

    public boolean sendMessageToService(int i) {
        return sendMessageToService(i, 0);
    }

    public boolean sendMessageToService(int i, int i2) {
        if (GlobalVars.isBound && GlobalVars.serviceMessenger != null) {
            try {
                Message messageObtain = Message.obtain(null, i, i2, 0);
                messageObtain.replyTo = GlobalVars.clientMessenger;
                GlobalVars.serviceMessenger.send(messageObtain);
                return true;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void initToggleCards() {
        refreshToggles();
    }

    public void onCardAutoLight(View view) {
        boolean z = !this.autoLightOn;
        this.autoLightOn = z;
        this.editor.putBoolean("autoLight", z).apply();
        sendMessageToService(this.autoLightOn ? 10 : 11);
        updateToggleVisuals();
        Log.i(TAG, "card autoLight=" + this.autoLightOn);
    }

    public void onCardPedestrian(View view) {
        boolean z = this.pedestrianOn;
        this.pedestrianOn = !z;
        this.editor.putBoolean("disablePedestrianSound", z).apply();
        sendMessageToService(21, z ? 1 : 0);
        updateToggleVisuals();
        Log.i(TAG, "card pedestrianSound on=" + this.pedestrianOn);
    }

    public void onCardSuspensionMaintenance(View view) {
        boolean z = !this.sharedPreferences.getBoolean("suspensionMaintenance", false);
        this.suspensionMaintenanceOn = z;
        this.editor.putBoolean("suspensionMaintenance", z).apply();
        sendMessageToService(37, this.suspensionMaintenanceOn ? 1 : 0);
        updateToggleVisuals();
    }

    public void onCardForcedEv(View view) {
        boolean z = !this.forcedEvOn;
        this.forcedEvOn = z;
        this.editor.putBoolean("forcedEv", z).apply();
        sendMessageToService(35, this.forcedEvOn ? 1 : 0);
        updateToggleVisuals();
        Log.i(TAG, "card forcedEv on=" + this.forcedEvOn);
    }

    private void refreshToggles() {
        this.autoLightOn = this.sharedPreferences.getBoolean("autoLight", false);
        this.pedestrianOn = !this.sharedPreferences.getBoolean("disablePedestrianSound", false);
        this.forcedEvOn = this.sharedPreferences.getBoolean("forcedEv", false);
        this.suspensionMaintenanceOn = this.sharedPreferences.getBoolean("suspensionMaintenance", false);
        updateToggleVisuals();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateToggleVisuals() {
        applyBadge(this.autoLightBadge, this.autoLightOn);
        applyBadge(this.pedestrianBadge, this.pedestrianOn);
        applyBadge(this.forcedEvBadge, this.forcedEvOn);
        applyBadge(this.suspensionMaintenanceBadge, this.suspensionMaintenanceOn);
    }

    private void applyBadge(TextView textView, boolean z) {
        if (textView == null) {
            return;
        }
        textView.setText(z ? "активно" : "не активно");
        textView.setBackgroundResource(z ? R.drawable.pill_active : R.drawable.pill_inactive);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        bindToMessengerService();
        EdgeToEdge.enable(this);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(0);
        final View viewFindViewById = findViewById(R.id.mainContent);
        final int iRound = Math.round(getResources().getDisplayMetrics().density * 145.0f);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), new OnApplyWindowInsetsListener() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda2
            @Override // androidx.core.view.OnApplyWindowInsetsListener
            public final WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                return this.f$0.m1895lambda$onCreate$3$rubigtownrestoremodeMainActivity(viewFindViewById, iRound, view, windowInsetsCompat);
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("DrivePreferences", 0);
        this.sharedPreferences = sharedPreferences;
        GlobalVars.sharedPreferences = sharedPreferences;
        GridLayout gridLayout = (GridLayout) findViewById(R.id.splitTilesGrid);
        this.splitTilesGrid = gridLayout;
        this.gridPaddingTop = gridLayout.getPaddingTop();
        this.gridPaddingBottom = this.splitTilesGrid.getPaddingBottom();
        SharedPreferences.Editor editorEdit = this.sharedPreferences.edit();
        this.editor = editorEdit;
        GlobalVars.editor = editorEdit;
        initToggleCards();
        initIntent();
        GlobalVars.clientMessenger = new Messenger(new IncomingHandler());
    }

    /* JADX INFO: renamed from: lambda$onCreate$3$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ WindowInsetsCompat m1895lambda$onCreate$3$rubigtownrestoremodeMainActivity(View view, int i, View view2, WindowInsetsCompat windowInsetsCompat) {
        int identifier;
        Insets insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars());
        int dimensionPixelSize = insets.top;
        if (dimensionPixelSize == 0 && (identifier = getResources().getIdentifier("status_bar_height", "dimen", "android")) > 0) {
            dimensionPixelSize = getResources().getDimensionPixelSize(identifier);
        }
        boolean zIsFullscreenGridEnabled = isFullscreenGridEnabled();
        view.setPadding(i + insets.left, zIsFullscreenGridEnabled ? 0 : dimensionPixelSize, insets.right, 0);
        if (dimensionPixelSize != this.contentInsetTop || zIsFullscreenGridEnabled != this.fullscreenGrid) {
            this.contentInsetTop = dimensionPixelSize;
            this.fullscreenGrid = zIsFullscreenGridEnabled;
            GridLayout gridLayout = this.splitTilesGrid;
            if (gridLayout != null) {
                gridLayout.post(new Runnable() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.renderSplitTiles();
                    }
                });
            }
        }
        return windowInsetsCompat;
    }

    public void initIntent() {
        if (this.resultIntent == null) {
            this.resultIntent = new Intent(this, (Class<?>) AdvanceActivity.class);
        }
        this.resultIntent.putExtra("customCommand", this.customCommand);
        this.resultIntent.putExtra("customCommandCount", this.customCommandCount);
    }

    public void initIntentStarButton() {
        if (this.resultIntentStarButton == null) {
            this.resultIntentStarButton = new Intent(this, (Class<?>) AdvanceActivityStarButton.class);
        }
    }

    private void getModes() {
        Cursor cursorQuery = getContentResolver().query(Uri.parse("content://ru.big.town.restoremode.restoremodecontentprovider/"), null, null, null, null);
        if (cursorQuery.getCount() != 0) {
            cursorQuery.moveToFirst();
            this.driveMode = cursorQuery.getString(0);
            this.energy = cursorQuery.getString(1);
            this.recycle = cursorQuery.getString(2);
            this.customCommand = cursorQuery.getString(3);
            this.customCommandCount = cursorQuery.getInt(4);
            Log.i("$$$ getModes() $$$", "Query Result:\ndriveMode: " + this.driveMode + "\nenergy: " + this.energy + "\nrecycle: " + this.recycle);
        }
        cursorQuery.close();
    }

    public void onButtonClickClose(View view) {
        finish();
    }

    public void onButtonLeaveCar(View view) {
        new MaterialAlertDialogBuilder(this, R.style.DarkDialog).setTitle(R.string.power_hold_title).setMessage(R.string.power_hold_confirmation).setPositiveButton(R.string.power_hold_activate, new DialogInterface.OnClickListener() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda14
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                this.f$0.m1892lambda$onButtonLeaveCar$4$rubigtownrestoremodeMainActivity(dialogInterface, i);
            }
        }).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: renamed from: lambda$onButtonLeaveCar$4$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ void m1892lambda$onButtonLeaveCar$4$rubigtownrestoremodeMainActivity(DialogInterface dialogInterface, int i) {
        boolean zSendMessageToService = sendMessageToService(20);
        if (!zSendMessageToService) {
            showSnack(getString(R.string.service_not_ready));
        }
        Log.i(TAG, "onButtonLeaveCar sent=" + zSendMessageToService);
    }

    public void onCardWashMode(View view) {
        new MaterialAlertDialogBuilder(this, R.style.DarkDialog).setTitle(R.string.wash_mode_title).setMessage(R.string.wash_mode_confirmation).setPositiveButton(R.string.wash_mode_activate, new DialogInterface.OnClickListener() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda19
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                this.f$0.m1894lambda$onCardWashMode$5$rubigtownrestoremodeMainActivity(dialogInterface, i);
            }
        }).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: renamed from: lambda$onCardWashMode$5$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ void m1894lambda$onCardWashMode$5$rubigtownrestoremodeMainActivity(DialogInterface dialogInterface, int i) {
        boolean zSendMessageToService = sendMessageToService(23);
        if (!zSendMessageToService) {
            showSnack(getString(R.string.service_not_ready));
        }
        Log.i(TAG, "onCardWashMode sent=" + zSendMessageToService);
    }

    private void showSnack(String str) {
        Snackbar.make(findViewById(R.id.main), str, 0).show();
    }

    private void populateLaunchAppsWidget() {
        ViewGroup viewGroup;
        View view = this.launchAppsWidget;
        if (view == null || (viewGroup = (ViewGroup) view.findViewById(R.id.launchAppsGrid)) == null) {
            return;
        }
        viewGroup.removeAllViews();
        PackageManager packageManager = getPackageManager();
        Intent intentAddCategory = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER");
        final HashMap map = new HashMap();
        for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(intentAddCategory, 0)) {
            String str = resolveInfo.activityInfo.packageName;
            if (!map.containsKey(str) && isLaunchAppsTileCandidate(packageManager, str)) {
                map.put(str, resolveInfo.loadLabel(packageManager).toString());
            }
        }
        ArrayList<String> arrayList = new ArrayList(map.keySet());
        arrayList.sort(new Comparator() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda16
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                Map map2 = map;
                return ((String) map2.get((String) obj)).compareToIgnoreCase((String) map2.get((String) obj2));
            }
        });
        int iMax = Math.max(1, Math.min(4, TileSizeStore.width(this.sharedPreferences, "launchAppsWidget", 2)));
        boolean z = viewGroup instanceof GridLayout;
        if (z) {
            ((GridLayout) viewGroup).setColumnCount(iMax);
        }
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(this);
        for (final String str2 : arrayList) {
            View viewInflate = layoutInflaterFrom.inflate(R.layout.item_launch_app_grid, viewGroup, false);
            ((TextView) viewInflate.findViewById(R.id.launchAppGridLabel)).setText((CharSequence) map.get(str2));
            try {
                ((ImageView) viewInflate.findViewById(R.id.launchAppGridIcon)).setImageDrawable(packageManager.getApplicationIcon(packageManager.getApplicationInfo(str2, 0)));
            } catch (Exception unused) {
            }
            viewInflate.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda17
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    this.f$0.m1899x22a551a9(str2, view2);
                }
            });
            viewInflate.setOnLongClickListener(new View.OnLongClickListener() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda18
                @Override // android.view.View.OnLongClickListener
                public final boolean onLongClick(View view2) {
                    return this.f$0.m1900x83f7ee48(str2, view2);
                }
            });
            if (z) {
                int childCount = ((GridLayout) viewGroup).getChildCount();
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                layoutParams.width = 0;
                layoutParams.height = -2;
                layoutParams.columnSpec = GridLayout.spec(childCount % iMax, 1, 1.0f);
                layoutParams.rowSpec = GridLayout.spec(childCount / iMax, 1);
                viewInflate.setLayoutParams(layoutParams);
            }
            viewGroup.addView(viewInflate);
        }
    }

    /* JADX INFO: renamed from: lambda$populateLaunchAppsWidget$7$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ void m1899x22a551a9(String str, View view) {
        launchAppNormally(str);
    }

    /* JADX INFO: renamed from: lambda$populateLaunchAppsWidget$8$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ boolean m1900x83f7ee48(String str, View view) {
        showLaunchAppMenu(view, str);
        return true;
    }

    private boolean isLaunchAppsTileCandidate(PackageManager packageManager, String str) {
        if (!str.equals(getPackageName()) && !str.equals("ru.big.town.anative") && !str.startsWith("com.qinggan") && !str.startsWith("com.android.car")) {
            try {
                if ((packageManager.getApplicationInfo(str, 0).flags & 1) == 0) {
                    return true;
                }
            } catch (Exception unused) {
            }
        }
        return false;
    }

    private void showLaunchAppMenu(View view, final String str) {
        final List<AppWidgetStore.Entry> listLoad = AppWidgetStore.load(this.sharedPreferences);
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenu().add(0, 1, 0, "Запустить на основном дисплее");
        if (listLoad.isEmpty()) {
            popupMenu.getMenu().add(0, 2, 1, "Нет виджетов приложения").setEnabled(false);
        } else {
            int i = 0;
            while (i < listLoad.size()) {
                int i2 = i + 1;
                popupMenu.getMenu().add(0, i + 100, i2, "Запустить в виджете: " + appWidgetTitle(listLoad.get(i)));
                i = i2;
            }
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda13
            @Override // android.widget.PopupMenu.OnMenuItemClickListener
            public final boolean onMenuItemClick(MenuItem menuItem) {
                return this.f$0.m1916lambda$showLaunchAppMenu$9$rubigtownrestoremodeMainActivity(str, listLoad, menuItem);
            }
        });
        popupMenu.show();
    }

    /* JADX INFO: renamed from: lambda$showLaunchAppMenu$9$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ boolean m1916lambda$showLaunchAppMenu$9$rubigtownrestoremodeMainActivity(String str, List list, MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            launchAppOnDisplay(str, 0);
            return true;
        }
        int i = itemId - 100;
        if (i < 0 || i >= list.size()) {
            return false;
        }
        launchInsideAppWidget(str, ((AppWidgetStore.Entry) list.get(i)).id);
        return true;
    }

    private String appWidgetTitle(AppWidgetStore.Entry entry) {
        String strDesignation = AppWidgetStore.designation(this.sharedPreferences, entry.id);
        String string = this.embeddedWidgetPackages.get(entry.id);
        if (string == null) {
            string = entry.selected().packageName;
        }
        try {
            PackageManager packageManager = getPackageManager();
            string = packageManager.getApplicationLabel(packageManager.getApplicationInfo(string, 0)).toString();
        } catch (Exception unused) {
        }
        return strDesignation.isEmpty() ? string : strDesignation + " · " + string;
    }

    private void launchAppOnDisplay(String str, int i) {
        try {
            sendBroadcast(new Intent(ACTION_OPEN_ON_DISPLAY).setPackage("ru.big.town.anative").putExtra(EXTRA_EMBEDDED_TASK_PKG, str).putExtra("display", i), BIND_SET_MODES_PERMISSION);
            Log.i(TAG, "launchAppOnDisplay " + str + " display=" + i);
        } catch (Exception e) {
            showSnack("Не удалось открыть приложение");
            Log.w(TAG, "launchAppOnDisplay " + str + ": " + e.getMessage());
        }
    }

    private void launchInsideAppWidget(String str, String str2) {
        AppWidgetStore.Entry entryFind = AppWidgetStore.find(this.sharedPreferences, str2);
        if (entryFind == null) {
            showSnack("Виджет приложения не найден");
            return;
        }
        View view = this.appWidgetTileViews.get(entryFind.id);
        if (view == null) {
            showSnack("Виджет приложения не на экране");
            return;
        }
        if (this.embeddedWidgetSurfaces.containsKey(entryFind.id)) {
            releaseEmbeddedWidget(entryFind.id, view);
        }
        showEmbeddedAppWidget(view, entryFind, str, AppDpiStore.get(this.sharedPreferences, str));
    }

    public void onCardAndroidSettings(View view) {
        try {
            Intent intent = new Intent("android.settings.SETTINGS");
            intent.addFlags(268435456);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Не удалось открыть настройки Android", 0).show();
            Log.w(TAG, "onCardAndroidSettings failed: " + e.getMessage());
        }
    }

    public void onDialNumber(View view) {
        sendDialNumber(this.sharedPreferences.getString("dialWidgetNumber", ""));
    }

    private void onDialNumber(DialWidgetStore.Entry entry) {
        sendDialNumber(entry.number);
    }

    private void sendDialNumber(String str) {
        String strReplaceAll = str != null ? str.replaceAll("[^0-9]", "") : "";
        if (strReplaceAll.length() < 4 || strReplaceAll.length() > 10) {
            showSnack("Сначала сохраните номер от 4 до 10 цифр в настройках");
            return;
        }
        if (strReplaceAll.length() == 10) {
            strReplaceAll = "8" + strReplaceAll;
        }
        try {
            Intent intent = new Intent("com.qinggan.broadcast.action.callfromcard");
            intent.putExtra("dial_number", strReplaceAll);
            getApplicationContext().sendBroadcast(intent);
        } catch (Exception e) {
            showSnack("Не удалось передать номер для вызова");
            Log.w(TAG, "onDialNumber failed: " + e.getMessage());
        }
    }

    public void onButtonClickAdvance(View view) {
        getModes();
        initIntent();
        Log.i("$$$ Main onButtonClickAdvance $$$", String.format("%s %d", this.customCommand, Integer.valueOf(this.customCommandCount)));
        startActivityForResult(this.resultIntent, 1);
    }

    public void onButtonClickAdvanceStarButton(View view) {
        getModes();
        initIntentStarButton();
        startActivity(this.resultIntentStarButton);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        this.suspensionScreenResumed = true;
        registerReceiver(this.tripReceiver, new IntentFilter(ACTION_TRIP_UPDATE), 2);
        Intent intent = new Intent(ACTION_REQUEST_TRIP_UPDATE);
        intent.setPackage("ru.big.town.anative");
        sendBroadcast(intent);
        this.uiHandler.removeCallbacks(this.tripTick);
        this.uiHandler.post(this.tripTick);
        registerReceiver(this.batteryHeatReceiver, new IntentFilter(ACTION_BATTERY_HEAT_UPDATE), 2);
        registerReceiver(this.settingSyncReceiver, new IntentFilter("ru.big.town.anative.SETTING_SYNCED"), BIND_SET_MODES_PERMISSION, null, 2);
        registerReceiver(this.powerHoldStatusReceiver, new IntentFilter(ACTION_POWER_HOLD_STATUS_UPDATE), BIND_SET_MODES_PERMISSION, null, 2);
        registerReceiver(this.embeddedLeftReceiver, new IntentFilter(ACTION_EMBEDDED_TASK_LEFT), BIND_SET_MODES_PERMISSION, null, 2);
        Intent intent2 = new Intent(ACTION_REQUEST_POWER_HOLD_STATUS);
        intent2.setPackage("ru.big.town.anative");
        sendBroadcast(intent2, BIND_SET_MODES_PERMISSION);
        Intent intent3 = new Intent(ACTION_REQUEST_BATTERY_HEAT);
        intent3.setPackage("ru.big.town.anative");
        sendBroadcast(intent3);
        refreshToggles();
        applyMainScreenVisibility();
        this.fullscreenGrid = isFullscreenGridEnabled();
        applyContentTopInset();
        TileOrderStore.sync(this.sharedPreferences, getPackageManager());
        renderSplitTiles();
    }

    public void onVoiceCommand(View view) {
        if (this.sharedPreferences.getBoolean("voiceAssistantEnabled", false)) {
            startActivity(new Intent(this, (Class<?>) VoiceActivity.class));
        } else {
            startActivityForResult(new Intent(this, (Class<?>) AdvanceActivity.class).putExtra("settingsSection", 7), 1);
        }
    }

    private boolean isWidgetVisible(String str) {
        str.hashCode();
        switch (str) {
            case "cardPowerHold":
            case "cardLeaveCar":
                return this.sharedPreferences.getBoolean("showPowerHold", true);
            case "cardAutoLight":
                return this.sharedPreferences.getBoolean("showAutoLight", true);
            case "launchAppsWidget":
                return this.sharedPreferences.getBoolean("showLaunchAppsWidget", false);
            case "suspensionWidget":
                return this.sharedPreferences.getBoolean("showSuspensionWidget", false);
            case "cardForcedEv":
                return this.sharedPreferences.getBoolean("showForcedEv", false);
            case "cardWashMode":
                return this.sharedPreferences.getBoolean("showWashMode", true);
            case "cardVoiceCommand":
                return this.sharedPreferences.getBoolean("showVoiceCommand", false);
            case "tripCard":
                return this.sharedPreferences.getBoolean("showTripTimer", true);
            case "cardPedestrian":
                return this.sharedPreferences.getBoolean("showPedestrian", true);
            case "cardBatteryHeat":
                return this.sharedPreferences.getBoolean("showBatteryHeat", true);
            case "cardSuspensionMaintenance":
                return this.sharedPreferences.getBoolean("showSuspensionMaintenance", false);
            default:
                return true;
        }
    }

    private void applyMainScreenVisibility() {
        setCardVisible(this.tripCard, "showTripTimer");
        setCardVisible(this.cardPowerHold, "showPowerHold");
        setCardVisible(this.cardWashMode, "showWashMode");
        setCardVisible(this.cardAutoLight, "showAutoLight");
        setCardVisible(this.cardPedestrian, "showPedestrian");
        View view = this.cardSuspensionMaintenance;
        if (view != null) {
            view.setVisibility(this.sharedPreferences.getBoolean("showSuspensionMaintenance", false) ? 0 : 8);
        }
        View view2 = this.cardForcedEv;
        if (view2 != null) {
            view2.setVisibility(this.sharedPreferences.getBoolean("showForcedEv", false) ? 0 : 8);
        }
        setCardVisible(this.cardBatteryHeat, "showBatteryHeat");
        if (this.launchAppsWidget != null) {
            boolean z = this.sharedPreferences.getBoolean("showLaunchAppsWidget", false);
            this.launchAppsWidget.setVisibility(z ? 0 : 8);
            if (z) {
                populateLaunchAppsWidget();
            }
        }
        View viewFindViewById = findViewById(R.id.buttonTripHistory);
        if (viewFindViewById != null) {
            viewFindViewById.setVisibility(this.sharedPreferences.getBoolean("saveTripHistory", true) ? 0 : 8);
        }
    }

    private void setCardVisible(View view, String str) {
        if (view == null) {
            return;
        }
        view.setVisibility(this.sharedPreferences.getBoolean(str, true) ? 0 : 8);
    }

    private int[] getWidgetDimensions(String str) {
        if ("suspensionWidget".equals(str)) {
            return TileSizeStore.dimensions(this.sharedPreferences, str, 4, 3);
        }
        if (!"tripCard".equals(str) && !"cardBatteryHeat".equals(str)) {
            if ("cardSettings".equals(str) || "cardAndroidSettings".equals(str) || "cardVoiceCommand".equals(str)) {
                return new int[]{1, 1};
            }
            if ("launchAppsWidget".equals(str)) {
                return TileSizeStore.dimensions(this.sharedPreferences, str, 2, 3);
            }
            return new int[]{2, 1};
        }
        return new int[]{3, 2};
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:37:0x013e  */
    /* JADX WARN: Code duplicated, block: B:39:0x014d  */
    /* JADX WARN: Code duplicated, block: B:41:0x0166  */
    /* JADX WARN: Code duplicated, block: B:42:0x0169  */
    /* JADX WARN: Code duplicated, block: B:45:0x017e  */
    /* JADX WARN: Code duplicated, block: B:46:0x0181  */
    /* JADX WARN: Code duplicated, block: B:52:0x021c A[PHI: r32
  0x021c: PHI (r32v26 byte) = (r32v0 byte), (r32v0 byte), (r32v0 byte), (r32v9 byte) binds: [B:51:0x0219, B:58:0x0230, B:54:0x0224, B:56:0x0227] A[DONT_GENERATE, DONT_INLINE]] */
    /*  JADX ERROR: JadxRuntimeException in pass: IfRegionVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't remove SSA var: r7v54 ru.big.town.restoremode.DialWidgetStore$Entry, still in use, count: 2, list:
          (r7v54 ru.big.town.restoremode.DialWidgetStore$Entry) from 0x012e: IGET (r7v54 ru.big.town.restoremode.DialWidgetStore$Entry) A[WRAPPED] (LINE:1284) ru.big.town.restoremode.DialWidgetStore.Entry.id java.lang.String
          (r7v54 ru.big.town.restoremode.DialWidgetStore$Entry) from 0x013c: PHI (r7 I:??) = (r7v46 ru.big.town.restoremode.DialWidgetStore$Entry), (r7v54 ru.big.town.restoremode.DialWidgetStore$Entry) binds: [B:35:0x013b, B:231:0x013c] A[DONT_GENERATE, DONT_INLINE]
        	at jadx.core.utils.InsnRemover.removeSsaVar(InsnRemover.java:164)
        	at jadx.core.utils.InsnRemover.unbindResult(InsnRemover.java:129)
        	at jadx.core.utils.InsnRemover.unbindInsn(InsnRemover.java:93)
        	at jadx.core.dex.visitors.regions.TernaryMod.makeTernaryInsn(TernaryMod.java:132)
        	at jadx.core.dex.visitors.regions.TernaryMod.processRegion(TernaryMod.java:67)
        	at jadx.core.dex.visitors.regions.TernaryMod.enterRegion(TernaryMod.java:50)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:96)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:27)
        	at jadx.core.dex.visitors.regions.TernaryMod.process(TernaryMod.java:36)
        	at jadx.core.dex.visitors.regions.IfRegionVisitor.process(IfRegionVisitor.java:44)
        	at jadx.core.dex.visitors.regions.IfRegionVisitor.visit(IfRegionVisitor.java:30)
        */
    public void renderSplitTiles() {
        /*
            Method dump skipped, instruction units count: 2018
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.big.town.restoremode.MainActivity.renderSplitTiles():void");
    }

    static /* synthetic */ GridLayout.LayoutParams lambda$renderSplitTiles$10(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10) {
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = (i * i9) + ((i9 - 1) * 2 * i2);
        layoutParams.columnSpec = GridLayout.spec(i7, i9);
        layoutParams.rowSpec = GridLayout.spec(i8, i10);
        applyTileVerticalMetrics(layoutParams, i2, i3, new int[]{i7, i8}, i10, i4, i5, i6);
        return layoutParams;
    }

    /* JADX INFO: renamed from: lambda$renderSplitTiles$11$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ void m1901lambda$renderSplitTiles$11$rubigtownrestoremodeMainActivity(int i, int i2) {
        insertTileAt(i, i2);
        renderSplitTiles();
    }

    /* JADX INFO: renamed from: lambda$renderSplitTiles$12$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ void m1902lambda$renderSplitTiles$12$rubigtownrestoremodeMainActivity(DialWidgetStore.Entry entry, View view) {
        onDialNumber(entry);
    }

    /* JADX INFO: renamed from: lambda$renderSplitTiles$13$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ boolean m1903lambda$renderSplitTiles$13$rubigtownrestoremodeMainActivity(DialWidgetStore.Entry entry, View view) {
        startTileDrag(view, TileOrderStore.Tile.TYPE_DIAL, entry.id);
        return true;
    }

    /* JADX INFO: renamed from: lambda$renderSplitTiles$14$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ void m1904lambda$renderSplitTiles$14$rubigtownrestoremodeMainActivity(int i) {
        if (GlobalVars.isBound) {
            sendSuspensionMessage(92, i);
        } else {
            showSnack("Сервис не готов");
        }
    }

    /* JADX INFO: renamed from: lambda$renderSplitTiles$15$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ boolean m1905lambda$renderSplitTiles$15$rubigtownrestoremodeMainActivity(TileOrderStore.Tile tile, View view) {
        startTileDrag(view, "widget", tile.id);
        return true;
    }

    /* JADX INFO: renamed from: lambda$renderSplitTiles$16$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ void m1906lambda$renderSplitTiles$16$rubigtownrestoremodeMainActivity(AppWidgetStore.Entry entry, View view) {
        if (this.embeddedWidgetSurfaces.containsKey(entry.id)) {
            return;
        }
        showEmbeddedAppWidget(view, entry);
    }

    /* JADX INFO: renamed from: lambda$renderSplitTiles$17$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ void m1907lambda$renderSplitTiles$17$rubigtownrestoremodeMainActivity(SplitStore.Preset preset, View view) {
        onSplitTileClick(preset);
    }

    /* JADX INFO: renamed from: lambda$renderSplitTiles$18$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ boolean m1908lambda$renderSplitTiles$18$rubigtownrestoremodeMainActivity(SplitStore.Preset preset, View view) {
        startTileDrag(view, TileOrderStore.Tile.TYPE_SPLIT, preset.id);
        return true;
    }

    /* JADX INFO: renamed from: lambda$renderSplitTiles$19$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ void m1909lambda$renderSplitTiles$19$rubigtownrestoremodeMainActivity(String str, View view) {
        onAppTileClick(str);
    }

    /* JADX INFO: renamed from: lambda$renderSplitTiles$20$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ boolean m1910lambda$renderSplitTiles$20$rubigtownrestoremodeMainActivity(String str, View view) {
        startTileDrag(view, TileOrderStore.Tile.TYPE_APP, str);
        return true;
    }

    private static void applyTileVerticalMetrics(GridLayout.LayoutParams layoutParams, int i, int i2, int[] iArr, int i3, int i4, int i5, int i6) {
        int i7;
        int i8 = iArr[1];
        layoutParams.height = tileHeight(i2, i, i8, i3, i4);
        if (i5 <= 0 || i8 != 0) {
            i7 = i;
        } else {
            i7 = 0;
            if (iArr[0] < i6) {
                layoutParams.height += i5 + i;
            } else {
                i7 = i + i5;
            }
        }
        layoutParams.setMargins(i, i7, i, i);
    }

    private boolean isFullscreenGridEnabled() {
        SharedPreferences sharedPreferences = this.sharedPreferences;
        return sharedPreferences != null && sharedPreferences.getBoolean("fullscreenGrid", false);
    }

    private int fullscreenGridColumns() {
        SharedPreferences sharedPreferences = this.sharedPreferences;
        if (sharedPreferences == null) {
            return 8;
        }
        return Math.max(0, Math.min(12, sharedPreferences.getInt("fullscreenGridColumns", 8)));
    }

    private void applyContentTopInset() {
        View viewFindViewById = findViewById(R.id.mainContent);
        if (viewFindViewById == null) {
            return;
        }
        viewFindViewById.setPadding(viewFindViewById.getPaddingLeft(), this.fullscreenGrid ? 0 : this.contentInsetTop, viewFindViewById.getPaddingRight(), 0);
    }

    private static int tileHeight(int i, int i2, int i3, int i4, int i5) {
        return (i * i4) + ((i4 - 1) * 2 * i2) + Math.max(0, Math.min(i4, i5 - i3));
    }

    private void insertTileAt(int i, int i2) {
        List<TileOrderStore.Tile> listLoad = TileOrderStore.load(this.sharedPreferences);
        if (i < 0 || i >= listLoad.size()) {
            return;
        }
        TileOrderStore.Tile tileRemove = listLoad.remove(i);
        if (i < i2) {
            i2--;
        }
        listLoad.add(Math.max(0, Math.min(i2, listLoad.size())), tileRemove);
        TileOrderStore.save(this.sharedPreferences, listLoad);
    }

    private void startTileDrag(View view, String str, String str2) {
        TileDragController tileDragController = this.tileDragController;
        if (tileDragController != null) {
            tileDragController.start(view);
        }
    }

    private void onAppTileClick(String str) {
        if (!GlobalVars.isBound || GlobalVars.serviceMessenger == null) {
            showSnack("Сервис не готов");
        } else {
            sendAppWindow(str);
        }
    }

    private void populateAppWidgetLauncher(final View view, final AppWidgetStore.Entry entry) {
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.appWidgetList);
        if (viewGroup == null) {
            return;
        }
        viewGroup.removeAllViews();
        PackageManager packageManager = getPackageManager();
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(this);
        entry.ensureProfiles();
        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda21
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view2) {
                return this.f$0.m1896xd41715a7(view, view2);
            }
        };
        view.setOnLongClickListener(onLongClickListener);
        viewGroup.setOnLongClickListener(onLongClickListener);
        for (final int i = 0; i < entry.profiles.size(); i++) {
            AppWidgetStore.Profile profile = entry.profiles.get(i);
            View viewInflate = layoutInflaterFrom.inflate(R.layout.item_app_widget_launcher_item, viewGroup, false);
            ImageView imageView = (ImageView) viewInflate.findViewById(R.id.launcherItemIcon);
            TextView textView = (TextView) viewInflate.findViewById(R.id.launcherItemLabel);
            String string = profile.packageName;
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(profile.packageName, 0);
                string = packageManager.getApplicationLabel(applicationInfo).toString();
                imageView.setImageDrawable(packageManager.getApplicationIcon(applicationInfo));
            } catch (Exception unused) {
            }
            textView.setText(string);
            viewInflate.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda23
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    this.f$0.m1897x3569b246(entry, i, view, view2);
                }
            });
            viewInflate.setOnLongClickListener(onLongClickListener);
            viewGroup.addView(viewInflate);
        }
        View viewFindViewById = view.findViewById(R.id.appWidgetLauncherScroll);
        if (viewFindViewById != null) {
            viewFindViewById.setVisibility(0);
            viewFindViewById.setOnLongClickListener(onLongClickListener);
        }
        View viewFindViewById2 = view.findViewById(R.id.appWidgetControls);
        if (viewFindViewById2 != null) {
            viewFindViewById2.setVisibility(8);
        }
        View viewFindViewById3 = view.findViewById(R.id.appWidgetDragHandleLauncher);
        if (viewFindViewById3 != null) {
            viewFindViewById3.setOnLongClickListener(onLongClickListener);
        }
        View viewFindViewById4 = view.findViewById(R.id.appWidgetLauncherControls);
        if (viewFindViewById4 != null) {
            viewFindViewById4.setVisibility(0);
            viewFindViewById4.bringToFront();
        }
        final View viewFindViewById5 = view.findViewById(R.id.appWidgetSwapLauncher);
        if (viewFindViewById5 != null) {
            viewFindViewById5.setOnLongClickListener(onLongClickListener);
            final String str = entry.id;
            viewFindViewById5.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda24
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    this.f$0.m1898x96bc4ee5(viewFindViewById5, str, view2);
                }
            });
        }
        refreshAppWidgetBadge(view, entry.id);
    }

    /* JADX INFO: renamed from: lambda$populateAppWidgetLauncher$21$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ boolean m1896xd41715a7(View view, View view2) {
        TileDragController tileDragController = this.tileDragController;
        return tileDragController != null && tileDragController.start(view);
    }

    /* JADX INFO: renamed from: lambda$populateAppWidgetLauncher$22$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ void m1897x3569b246(AppWidgetStore.Entry entry, int i, View view, View view2) {
        entry.selectedProfile = i;
        AppWidgetStore.update(this.sharedPreferences, entry);
        showEmbeddedAppWidget(view, entry);
    }

    /* JADX INFO: renamed from: lambda$populateAppWidgetLauncher$23$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ void m1898x96bc4ee5(View view, String str, View view2) {
        showSwapAppWidgetMenu(view, str);
    }

    private void refreshAppWidgetBadge(View view, String str) {
        TextView textView;
        if (view == null || (textView = (TextView) view.findViewById(R.id.appWidgetBadge)) == null) {
            return;
        }
        String strDesignation = AppWidgetStore.designation(this.sharedPreferences, str);
        if (strDesignation.isEmpty()) {
            textView.setVisibility(8);
        } else {
            textView.setText(strDesignation);
            textView.setVisibility(0);
        }
    }

    private void showEmbeddedAppWidget(View view, AppWidgetStore.Entry entry) {
        AppWidgetStore.Profile profileSelected = entry.selected();
        showEmbeddedAppWidget(view, entry, profileSelected.packageName, profileSelected.dpi);
    }

    private void showEmbeddedAppWidget(View view, AppWidgetStore.Entry entry, String str, int i) {
        showEmbeddedAppWidget(view, entry, str, i, null);
    }

    private void showEmbeddedAppWidget(final View view, AppWidgetStore.Entry entry, String str, int i, String str2) {
        if (!GlobalVars.isBound || GlobalVars.serviceMessenger == null) {
            showSnack("Сервис не готов");
            return;
        }
        final String str3 = entry.id;
        this.embeddedWidgetPackages.put(str3, str);
        this.embeddedWidgetDpi.put(str3, Integer.valueOf(i));
        this.embeddedSuppressRelease.remove(str3);
        View viewFindViewById = view.findViewById(R.id.appWidgetLauncherScroll);
        if (viewFindViewById != null) {
            viewFindViewById.setVisibility(8);
        }
        View viewFindViewById2 = view.findViewById(R.id.appWidgetLauncherControls);
        if (viewFindViewById2 != null) {
            viewFindViewById2.setVisibility(8);
        }
        final View viewFindViewById3 = view.findViewById(R.id.appWidgetControls);
        if (viewFindViewById3 != null) {
            viewFindViewById3.setVisibility(8);
            View viewFindViewById4 = view.findViewById(R.id.appWidgetClose);
            if (viewFindViewById4 != null) {
                viewFindViewById4.setVisibility(0);
                viewFindViewById4.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda7
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        this.f$0.m1911x4e886e27(str3, view, view2);
                    }
                });
            }
            View viewFindViewById5 = view.findViewById(R.id.appWidgetDragHandle);
            if (viewFindViewById5 != null) {
                viewFindViewById5.setVisibility(0);
                viewFindViewById5.setOnLongClickListener(new View.OnLongClickListener() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda8
                    @Override // android.view.View.OnLongClickListener
                    public final boolean onLongClick(View view2) {
                        return this.f$0.m1912xafdb0ac6(view, str3, view2);
                    }
                });
            }
            View viewFindViewById6 = view.findViewById(R.id.appWidgetExpand);
            if (viewFindViewById6 != null) {
                viewFindViewById6.setVisibility(0);
                viewFindViewById6.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda9
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        this.f$0.m1913x112da765(str3, view, view2);
                    }
                });
            }
            final View viewFindViewById7 = view.findViewById(R.id.appWidgetSwap);
            if (viewFindViewById7 != null) {
                viewFindViewById7.setVisibility(0);
                viewFindViewById7.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda10
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        this.f$0.m1914x72804404(viewFindViewById7, str3, view2);
                    }
                });
            }
        }
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.appWidgetRoot);
        if (viewGroup == null) {
            viewGroup = (ViewGroup) view;
        }
        final String[] strArr = {str2};
        TextureView textureView = new TextureView(this);
        textureView.setOpaque(true);
        textureView.setClipToOutline(true);
        textureView.setOutlineProvider(new ViewOutlineProvider() { // from class: ru.big.town.restoremode.MainActivity.8
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view2, Outline outline) {
                outline.setRoundRect(0, 0, view2.getWidth(), view2.getHeight(), MainActivity.this.getResources().getDisplayMetrics().density * 18.0f);
            }
        });
        viewGroup.addView(textureView, new ViewGroup.LayoutParams(-1, -1));
        this.embeddedWidgetSurfaces.put(str3, textureView);
        View viewFindViewById8 = view.findViewById(R.id.appWidgetBadge);
        if (viewFindViewById8 != null) {
            viewFindViewById8.bringToFront();
        }
        final GestureDetector gestureDetector = new GestureDetector(this, new AnonymousClass9(viewFindViewById3));
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() { // from class: ru.big.town.restoremode.MainActivity.10
            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i2, int i3) {
                surfaceTexture.setDefaultBufferSize(i2, i3);
                Surface surface = new Surface(surfaceTexture);
                MainActivity.this.embeddedWidgetOutputs.put(str3, surface);
                String[] strArr2 = strArr;
                String str4 = strArr2[0];
                if (str4 != null) {
                    strArr2[0] = null;
                    MainActivity mainActivity = MainActivity.this;
                    mainActivity.sendEmbeddedMove(str4, str3, (String) mainActivity.embeddedWidgetPackages.get(str3), surface, i2, i3);
                } else {
                    MainActivity mainActivity2 = MainActivity.this;
                    mainActivity2.sendEmbeddedSurface(str3, (String) mainActivity2.embeddedWidgetPackages.get(str3), MainActivity.this.runningDpi(str3), surface, i2, i3);
                }
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i2, int i3) {
                surfaceTexture.setDefaultBufferSize(i2, i3);
                Surface surface = (Surface) MainActivity.this.embeddedWidgetOutputs.get(str3);
                if (surface != null) {
                    MainActivity mainActivity = MainActivity.this;
                    mainActivity.sendEmbeddedSurface(str3, (String) mainActivity.embeddedWidgetPackages.get(str3), MainActivity.this.runningDpi(str3), surface, i2, i3);
                }
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                MainActivity.this.embeddedWidgetSurfaces.remove(str3);
                Surface surface = (Surface) MainActivity.this.embeddedWidgetOutputs.remove(str3);
                if (surface != null) {
                    surface.release();
                }
                MainActivity.this.embeddedWidgetPackages.remove(str3);
                MainActivity.this.embeddedWidgetDpi.remove(str3);
                if (MainActivity.this.embeddedSuppressRelease.remove(str3)) {
                    return true;
                }
                MainActivity.this.sendEmbeddedRelease(str3);
                return true;
            }
        });
        textureView.setOnTouchListener(new View.OnTouchListener() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda12
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view2, MotionEvent motionEvent) {
                return this.f$0.m1915xd3d2e0a3(gestureDetector, viewFindViewById3, str3, view2, motionEvent);
            }
        });
        if (viewFindViewById3 != null) {
            viewFindViewById3.bringToFront();
        }
    }

    /* JADX INFO: renamed from: lambda$showEmbeddedAppWidget$24$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ void m1911x4e886e27(String str, View view, View view2) {
        releaseEmbeddedWidget(str, view);
    }

    /* JADX INFO: renamed from: lambda$showEmbeddedAppWidget$25$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ boolean m1912xafdb0ac6(View view, String str, View view2) {
        startTileDrag(view, TileOrderStore.Tile.TYPE_APP_WIDGET, str);
        return true;
    }

    /* JADX INFO: renamed from: lambda$showEmbeddedAppWidget$26$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ void m1913x112da765(String str, View view, View view2) {
        String str2 = this.embeddedWidgetPackages.get(str);
        releaseEmbeddedWidget(str, view);
        if (str2 == null) {
            return;
        }
        sendAppWindow(str2);
    }

    /* JADX INFO: renamed from: lambda$showEmbeddedAppWidget$27$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ void m1914x72804404(View view, String str, View view2) {
        showSwapAppWidgetMenu(view, str);
    }

    /* JADX INFO: renamed from: ru.big.town.restoremode.MainActivity$9, reason: invalid class name */
    class AnonymousClass9 extends GestureDetector.SimpleOnGestureListener {
        private final Runnable hideControls;
        final /* synthetic */ View val$controls;

        AnonymousClass9(final View view) {
            this.val$controls = view;
            this.hideControls = new Runnable() { // from class: ru.big.town.restoremode.MainActivity$9$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    MainActivity.AnonymousClass9.lambda$$0(view);
                }
            };
        }

        static /* synthetic */ void lambda$$0(View view) {
            if (view != null) {
                view.setVisibility(8);
            }
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public void onLongPress(MotionEvent motionEvent) {
            View view = this.val$controls;
            if (view != null) {
                view.setVisibility(0);
                this.val$controls.bringToFront();
                MainActivity.this.uiHandler.removeCallbacks(this.hideControls);
                MainActivity.this.uiHandler.postDelayed(this.hideControls, 3000L);
            }
        }
    }

    /* JADX INFO: renamed from: lambda$showEmbeddedAppWidget$28$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ boolean m1915xd3d2e0a3(GestureDetector gestureDetector, View view, String str, View view2, MotionEvent motionEvent) {
        int i;
        gestureDetector.onTouchEvent(motionEvent);
        if (view != null && view.getVisibility() == 0) {
            int[] iArr = new int[2];
            view.getLocationOnScreen(iArr);
            int rawX = (int) motionEvent.getRawX();
            int rawY = (int) motionEvent.getRawY();
            int i2 = iArr[0];
            if (rawX >= i2 && rawX < i2 + view.getWidth() && rawY >= (i = iArr[1]) && rawY < i + view.getHeight()) {
                return false;
            }
        }
        view2.getParent().requestDisallowInterceptTouchEvent(!(motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 3));
        sendEmbeddedTouch(str, motionEvent);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int runningDpi(String str) {
        Integer num = this.embeddedWidgetDpi.get(str);
        if (num == null) {
            return 0;
        }
        return num.intValue();
    }

    private void releaseEmbeddedWidget(String str, View view) {
        removeEmbeddedSurface(str, view);
        sendEmbeddedRelease(str);
    }

    private void detachEmbeddedWidget(String str, View view) {
        this.embeddedSuppressRelease.add(str);
        removeEmbeddedSurface(str, view);
    }

    private void removeEmbeddedSurface(String str, View view) {
        this.embeddedWidgetSurfaces.remove(str);
        Surface surfaceRemove = this.embeddedWidgetOutputs.remove(str);
        if (surfaceRemove != null) {
            surfaceRemove.release();
        }
        this.embeddedWidgetPackages.remove(str);
        this.embeddedWidgetDpi.remove(str);
        if (view == null) {
            return;
        }
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.appWidgetRoot);
        if (viewGroup == null) {
            viewGroup = (ViewGroup) view;
        }
        for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
            if (viewGroup.getChildAt(childCount) instanceof TextureView) {
                viewGroup.removeViewAt(childCount);
            }
        }
        View viewFindViewById = view.findViewById(R.id.appWidgetControls);
        if (viewFindViewById != null) {
            viewFindViewById.setVisibility(8);
        }
        View viewFindViewById2 = view.findViewById(R.id.appWidgetExpand);
        if (viewFindViewById2 != null) {
            viewFindViewById2.setVisibility(8);
        }
        View viewFindViewById3 = view.findViewById(R.id.appWidgetClose);
        if (viewFindViewById3 != null) {
            viewFindViewById3.setVisibility(8);
        }
        AppWidgetStore.Entry entryFind = AppWidgetStore.find(this.sharedPreferences, str);
        if (entryFind != null) {
            populateAppWidgetLauncher(view, entryFind);
        }
    }

    private void showSwapAppWidgetMenu(View view, final String str) {
        final ArrayList arrayList = new ArrayList();
        for (AppWidgetStore.Entry entry : AppWidgetStore.load(this.sharedPreferences)) {
            if (!entry.id.equals(str)) {
                arrayList.add(entry);
            }
        }
        if (arrayList.isEmpty()) {
            showSnack("Нет другого виджета для обмена");
            return;
        }
        if (arrayList.size() == 1) {
            onSwapAppWidgetChosen(str, ((AppWidgetStore.Entry) arrayList.get(0)).id);
            return;
        }
        PopupMenu popupMenu = new PopupMenu(this, view);
        for (int i = 0; i < arrayList.size(); i++) {
            popupMenu.getMenu().add(0, i, i, "Обменять с " + appWidgetTitle((AppWidgetStore.Entry) arrayList.get(i)));
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda25
            @Override // android.widget.PopupMenu.OnMenuItemClickListener
            public final boolean onMenuItemClick(MenuItem menuItem) {
                return this.f$0.m1917x1f4b12d8(arrayList, str, menuItem);
            }
        });
        popupMenu.show();
    }

    /* JADX INFO: renamed from: lambda$showSwapAppWidgetMenu$29$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ boolean m1917x1f4b12d8(List list, String str, MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId < 0 || itemId >= list.size()) {
            return false;
        }
        onSwapAppWidgetChosen(str, ((AppWidgetStore.Entry) list.get(itemId)).id);
        return true;
    }

    private void onSwapAppWidgetChosen(String str, String str2) {
        if (str == null || str2 == null || str.equals(str2)) {
            return;
        }
        boolean zContainsKey = this.embeddedWidgetSurfaces.containsKey(str);
        boolean zContainsKey2 = this.embeddedWidgetSurfaces.containsKey(str2);
        if (zContainsKey && zContainsKey2) {
            swapAppWidgetInstances(str, str2);
            return;
        }
        if (zContainsKey) {
            confirmSendAppWidgetInstance(str, str2);
        } else if (zContainsKey2) {
            moveAppWidgetInstance(str2, str);
        } else {
            showSnack("В этом виджете нет запущенного приложения");
        }
    }

    private void confirmSendAppWidgetInstance(final String str, final String str2) {
        String strDesignation = AppWidgetStore.designation(this.sharedPreferences, str2);
        new AlertDialog.Builder(this).setMessage("Отправить запущенное приложение в " + (strDesignation.isEmpty() ? "другой виджет" : "виджет " + strDesignation) + "?").setPositiveButton("Отправить", new DialogInterface.OnClickListener() { // from class: ru.big.town.restoremode.MainActivity$$ExternalSyntheticLambda15
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                this.f$0.m1889xb29028da(str, str2, dialogInterface, i);
            }
        }).setNegativeButton("Отмена", (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: renamed from: lambda$confirmSendAppWidgetInstance$30$ru-big-town-restoremode-MainActivity, reason: not valid java name */
    /* synthetic */ void m1889xb29028da(String str, String str2, DialogInterface dialogInterface, int i) {
        moveAppWidgetInstance(str, str2);
    }

    private void swapAppWidgetInstances(String str, String str2) {
        String str3 = this.embeddedWidgetPackages.get(str);
        String str4 = this.embeddedWidgetPackages.get(str2);
        if (str3 == null || str4 == null || !this.embeddedWidgetSurfaces.containsKey(str) || !this.embeddedWidgetSurfaces.containsKey(str2)) {
            showSnack("Для обмена нужны запущенные приложения в обоих виджетах");
            return;
        }
        sendEmbeddedSwap(str, str2);
        this.embeddedWidgetPackages.put(str, str4);
        this.embeddedWidgetPackages.put(str2, str3);
        int iRunningDpi = runningDpi(str);
        this.embeddedWidgetDpi.put(str, Integer.valueOf(runningDpi(str2)));
        this.embeddedWidgetDpi.put(str2, Integer.valueOf(iRunningDpi));
        showSnack("Виджеты обменялись запущенными приложениями");
    }

    private void moveAppWidgetInstance(String str, String str2) {
        String str3 = this.embeddedWidgetPackages.get(str);
        View view = this.appWidgetTileViews.get(str);
        View view2 = this.appWidgetTileViews.get(str2);
        AppWidgetStore.Entry entryFind = AppWidgetStore.find(this.sharedPreferences, str2);
        if (str3 == null || view == null || view2 == null || entryFind == null) {
            showSnack("Не удалось перенести приложение");
            return;
        }
        showEmbeddedAppWidget(view2, entryFind, str3, runningDpi(str), str);
        if (this.embeddedWidgetSurfaces.containsKey(str2)) {
            detachEmbeddedWidget(str, view);
            String strDesignation = AppWidgetStore.designation(this.sharedPreferences, str2);
            showSnack(strDesignation.isEmpty() ? "Приложение перенесено" : "Приложение перенесено в виджет " + strDesignation);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendEmbeddedSurface(String str, String str2, int i, Surface surface, int i2, int i3) {
        if (!GlobalVars.isBound || GlobalVars.serviceMessenger == null || surface == null || str2 == null || str2.isEmpty()) {
            return;
        }
        try {
            Message messageObtain = Message.obtain(null, 34, 1, 0);
            Bundle bundle = new Bundle();
            bundle.putString("left", str2);
            bundle.putString("widgetId", str);
            bundle.putBoolean("embeddedSurface", true);
            bundle.putParcelable("surface", surface);
            bundle.putInt("width", i2);
            bundle.putInt(SuspensionWidgetProtocol.HEIGHT, i3);
            bundle.putInt("leftDpi", AppWidgetStore.normalizeDpi(i));
            messageObtain.setData(bundle);
            messageObtain.replyTo = GlobalVars.clientMessenger;
            GlobalVars.serviceMessenger.send(messageObtain);
            Log.i(TAG, "sendEmbeddedSurface widget=" + str + " pkg=" + str2 + " size=" + i2 + "x" + i3);
        } catch (RemoteException e) {
            Log.w(TAG, "sendEmbeddedSurface failed: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendEmbeddedMove(String str, String str2, String str3, Surface surface, int i, int i2) {
        if (!GlobalVars.isBound || GlobalVars.serviceMessenger == null || surface == null || str3 == null || str3.isEmpty()) {
            return;
        }
        try {
            Message messageObtain = Message.obtain(null, 38, 1, 0);
            Bundle bundle = new Bundle();
            bundle.putBoolean("embeddedMove", true);
            bundle.putString("fromWidgetId", str);
            bundle.putString("widgetId", str2);
            bundle.putString("package", str3);
            bundle.putParcelable("surface", surface);
            bundle.putInt("width", i);
            bundle.putInt(SuspensionWidgetProtocol.HEIGHT, i2);
            bundle.putInt("dpi", AppWidgetStore.normalizeDpi(runningDpi(str2)));
            messageObtain.setData(bundle);
            messageObtain.replyTo = GlobalVars.clientMessenger;
            GlobalVars.serviceMessenger.send(messageObtain);
            Log.i(TAG, "sendEmbeddedMove " + str + " -> " + str2 + " pkg=" + str3);
        } catch (RemoteException e) {
            Log.w(TAG, "sendEmbeddedMove failed: " + e.getMessage());
        }
    }

    private void sendEmbeddedSwap(String str, String str2) {
        if (!GlobalVars.isBound || GlobalVars.serviceMessenger == null) {
            return;
        }
        Surface surface = this.embeddedWidgetOutputs.get(str);
        Surface surface2 = this.embeddedWidgetOutputs.get(str2);
        if (surface == null || surface2 == null) {
            return;
        }
        try {
            Message messageObtain = Message.obtain(null, 38, 1, 0);
            Bundle bundle = new Bundle();
            bundle.putBoolean("embeddedSwap", true);
            bundle.putString("widgetId", str);
            bundle.putString("widgetId2", str2);
            bundle.putParcelable("surface", surface);
            bundle.putParcelable("surface2", surface2);
            bundle.putInt("width", embeddedPixelWidth(str));
            bundle.putInt(SuspensionWidgetProtocol.HEIGHT, embeddedPixelHeight(str));
            bundle.putInt("dpi", runningDpi(str));
            bundle.putInt("width2", embeddedPixelWidth(str2));
            bundle.putInt("height2", embeddedPixelHeight(str2));
            bundle.putInt("dpi2", runningDpi(str2));
            messageObtain.setData(bundle);
            messageObtain.replyTo = GlobalVars.clientMessenger;
            GlobalVars.serviceMessenger.send(messageObtain);
            Log.i(TAG, "sendEmbeddedSwap " + str + " <-> " + str2);
        } catch (RemoteException e) {
            Log.w(TAG, "sendEmbeddedSwap failed: " + e.getMessage());
        }
    }

    private int embeddedPixelWidth(String str) {
        TextureView textureView = this.embeddedWidgetSurfaces.get(str);
        if (textureView == null) {
            return 0;
        }
        return textureView.getWidth();
    }

    private int embeddedPixelHeight(String str) {
        TextureView textureView = this.embeddedWidgetSurfaces.get(str);
        if (textureView == null) {
            return 0;
        }
        return textureView.getHeight();
    }

    private void sendEmbeddedTouch(String str, MotionEvent motionEvent) {
        if (!GlobalVars.isBound || GlobalVars.serviceMessenger == null) {
            return;
        }
        try {
            Message messageObtain = Message.obtain(null, 34, 1, 0);
            Bundle bundle = new Bundle();
            bundle.putString("widgetId", str);
            bundle.putBoolean("embeddedTouch", true);
            bundle.putParcelable(NotificationCompat.CATEGORY_EVENT, MotionEvent.obtain(motionEvent));
            messageObtain.setData(bundle);
            messageObtain.replyTo = GlobalVars.clientMessenger;
            GlobalVars.serviceMessenger.send(messageObtain);
        } catch (RemoteException e) {
            Log.w(TAG, "sendEmbeddedTouch failed: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendEmbeddedRelease(String str) {
        if (!GlobalVars.isBound || GlobalVars.serviceMessenger == null) {
            return;
        }
        try {
            Message messageObtain = Message.obtain(null, 34, 1, 0);
            Bundle bundle = new Bundle();
            bundle.putString("widgetId", str);
            bundle.putBoolean("embeddedRelease", true);
            messageObtain.setData(bundle);
            messageObtain.replyTo = GlobalVars.clientMessenger;
            GlobalVars.serviceMessenger.send(messageObtain);
        } catch (RemoteException e) {
            Log.w(TAG, "sendEmbeddedRelease failed: " + e.getMessage());
        }
    }

    private void launchAppNormally(String str) {
        try {
            Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(str);
            if (launchIntentForPackage == null) {
                showSnack("Не удалось открыть приложение");
                return;
            }
            launchIntentForPackage.addFlags(268435456);
            startActivity(launchIntentForPackage);
            Log.i(TAG, "launchAppNormally " + str);
        } catch (Exception e) {
            showSnack("Не удалось открыть приложение");
            Log.w(TAG, "launchAppNormally " + str + ": " + e.getMessage());
        }
    }

    private void sendAppWindow(String str) {
        if (str == null || str.isEmpty()) {
            return;
        }
        int i = AppDpiStore.get(this.sharedPreferences, str);
        try {
            Message messageObtain = Message.obtain(null, 34, 1, 0);
            Bundle bundle = new Bundle();
            bundle.putString("left", str);
            bundle.putString("right", "");
            bundle.putInt("leftDpi", i);
            bundle.putInt("rightDpi", 0);
            messageObtain.setData(bundle);
            messageObtain.replyTo = GlobalVars.clientMessenger;
            GlobalVars.serviceMessenger.send(messageObtain);
            Log.i(TAG, "sendAppWindow " + str);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void onSplitTileClick(SplitStore.Preset preset) {
        if (!GlobalVars.isBound || GlobalVars.serviceMessenger == null) {
            showSnack("Сервис не готов");
        } else {
            sendSplitVd(preset);
        }
    }

    private int presetIndex(SplitStore.Preset preset) {
        List<SplitStore.Preset> listLoad = SplitStore.load(this.sharedPreferences);
        for (int i = 0; i < listLoad.size(); i++) {
            SplitStore.Preset preset2 = listLoad.get(i);
            if (preset2.l.equals(preset.l) && preset2.r.equals(preset.r) && preset2.ratio == preset.ratio) {
                return i;
            }
        }
        return -1;
    }

    private void sendSplitVd(SplitStore.Preset preset) {
        if (preset.l == null || preset.l.isEmpty() || preset.r == null || preset.r.isEmpty()) {
            return;
        }
        int i = AppDpiStore.get(this.sharedPreferences, preset.l);
        int i2 = AppDpiStore.get(this.sharedPreferences, preset.r);
        try {
            Message messageObtain = Message.obtain(null, 34, preset.ratio, 0);
            Bundle bundle = new Bundle();
            bundle.putString("left", preset.l);
            bundle.putString("right", preset.r);
            bundle.putInt("leftDpi", i);
            bundle.putInt("rightDpi", i2);
            bundle.putBoolean("resizable", preset.resizable);
            bundle.putFloat(TileOrderStore.Tile.TYPE_SPLIT, SplitStore.leftFraction(preset));
            bundle.putInt("presetIdx", presetIndex(preset));
            bundle.putString("presetId", preset.id);
            messageObtain.setData(bundle);
            messageObtain.replyTo = GlobalVars.clientMessenger;
            GlobalVars.serviceMessenger.send(messageObtain);
            Log.i(TAG, "sendSplitVd left=" + preset.l + " right=" + preset.r + " ratio=" + preset.ratio + " lDpi=" + i + " rDpi=" + i2);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        this.suspensionScreenResumed = false;
        watchSuspension();
        this.suspensionState = new Bundle();
        Iterator it = new ArrayList(this.embeddedWidgetSurfaces.keySet()).iterator();
        while (it.hasNext()) {
            sendEmbeddedRelease((String) it.next());
        }
        this.embeddedWidgetSurfaces.clear();
        Iterator<Surface> it2 = this.embeddedWidgetOutputs.values().iterator();
        while (it2.hasNext()) {
            it2.next().release();
        }
        this.embeddedWidgetOutputs.clear();
        this.embeddedWidgetPackages.clear();
        this.embeddedWidgetDpi.clear();
        this.embeddedSuppressRelease.clear();
        try {
            unregisterReceiver(this.tripReceiver);
        } catch (Exception unused) {
        }
        try {
            unregisterReceiver(this.batteryHeatReceiver);
        } catch (Exception unused2) {
        }
        try {
            unregisterReceiver(this.settingSyncReceiver);
        } catch (Exception unused3) {
        }
        try {
            unregisterReceiver(this.powerHoldStatusReceiver);
        } catch (Exception unused4) {
        }
        try {
            unregisterReceiver(this.embeddedLeftReceiver);
        } catch (Exception unused5) {
        }
        this.uiHandler.removeCallbacks(this.tripTick);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        this.destroyed = true;
        TileDragController tileDragController = this.tileDragController;
        if (tileDragController != null) {
            tileDragController.cancel();
        }
        this.uiHandler.removeCallbacks(this.suspensionUnavailable);
        this.uiHandler.removeCallbacks(this.tripTick);
        releaseMessengerBinding("onDestroy");
        super.onDestroy();
    }
}
