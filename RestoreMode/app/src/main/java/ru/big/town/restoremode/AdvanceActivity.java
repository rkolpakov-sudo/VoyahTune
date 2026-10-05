package ru.big.town.restoremode;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.function.Predicate;
import ru.big.town.common.DriveSelectionPolicy;

/* JADX INFO: loaded from: classes2.dex */
public class AdvanceActivity extends AppCompatActivity {
    private static final String ACTION_BATTERY_HEAT_AUTO_CHANGED = "ru.big.town.anative.BATTERY_HEAT_AUTO_CHANGED";
    private static final String ACTION_MODE_REMEMBER_CHANGED = "ru.big.town.anative.MODE_REMEMBER_CHANGED";
    private static final String EXTRA_BATTERY_HEAT_AUTO_ENABLED = "autoEnabled";
    private static final String EXTRA_MODE_KEY = "modeKey";
    private static final String EXTRA_REMEMBER_LAST = "rememberLast";
    static final String EXTRA_SECTION = "settingsSection";
    private static final int LIGHT_DIAGNOSTICS_UNKNOWN = Integer.MIN_VALUE;
    private static final int LIGHT_DIAGNOSTICS_UPDATE = 2;
    private static final int LIGHT_DIAGNOSTICS_WATCH = 1;
    static final int MSG_APPLY_DRIVE_MODES = 1;
    static final int MSG_APPLY_FORCED_EV = 35;
    static final int MSG_APPLY_SUSPENSION_MAINTENANCE = 37;
    static final int MSG_AUTO_LIGHT_DISABLE = 11;
    static final int MSG_AUTO_LIGHT_ENABLE = 10;
    static final int MSG_CLOSE_ALL = 27;
    static final int MSG_FLOATING_BACK = 24;
    static final int MSG_FLOATING_BACK_SIDE = 25;
    static final int MSG_GRANT_INSTALL = 26;
    static final int MSG_REBOOT = 22;
    static final int MSG_RESULT = 4;
    static final int MSG_SET_THEME = 28;
    private static final String NATIVE_PACKAGE = "ru.big.town.anative";
    private static final String PREF_SHOW_CUSTOM_COMMANDS = "showCustomCommands";
    static final int SECTION_VOICE = 7;
    private static final long SYSTEM_METRICS_INTERVAL_MS = 5_000L;
    private boolean activityResumed;
    private View apolloGreenSoundContainer;
    private RadioGroup apolloGreenSoundGroup;
    private LinearLayout appShortcutsContainer;
    private LinearLayout appWidgetsContainer;
    private ProgressBar applyProgressAdvance;
    private RadioGroup autoLightGroup;
    private Button buttonApplyAdvance;
    private ImageButton buttonBack;
    private EditText canCommandsEditor;
    private CheckBox checkBox34;
    private int currentSection;
    private Button dockApp1Btn;
    private Button dockApp2Btn;
    private Button dockSplit1Btn;
    private Button dockSplit2Btn;
    private LinearLayout fullscreenAppsContainer;
    private boolean lightDiagnosticsActive;
    private boolean lightDiagnosticsBound;
    private int lightDiagnosticsSession;
    private SensorManager lightSensorManager;
    private TextView navApolloTech;
    private TextView navCustomCommands;
    private TextView navDriveModes;
    private TextView navMainScreen;
    private TextView navOther;
    private TextView navSplitScreen;
    private TextView navSteeringButtons;
    private TextView navVoiceControl;
    private View pageApolloTech;
    private View pageCustomCommands;
    private View pageDriveModes;
    private View pageMainScreen;
    private View pageOther;
    private View pageSplitScreen;
    private View pageSteeringButtons;
    private View pageVoiceControl;
    private NumberPicker pickerCustomCommandCount;
    private SharedPreferences prefs;
    private TextView sectionTitle;
    private TextView commandStatusText;
    private LinearLayout splitPresetsContainer;
    private LinearLayout steerDvrLongList;
    private LinearLayout steerDvrShortList;
    private LinearLayout steerPhoneLongList;
    private LinearLayout steerPhoneShortList;
    private LinearLayout steerStarLongList;
    private LinearLayout steerStarShortList;
    private LinearLayout steerVoiceLongList;
    private LinearLayout steerVoiceShortList;
    private Switch switchApolloTlc;
    private Switch switchApolloTrafficLights;
    private Switch switchApolloTrafficSigns;
    private boolean syncingModeUi;
    private boolean syncingSettingUi;
    private volatile boolean systemMetricsActive;
    private volatile long systemMetricsGeneration;
    private TextView textApolloStatus;
    private TextView textCpuStatus;
    private TextView textHookStatus;
    private TextView textRamStatus;
    private TextView textSensorLevel;
    private VoiceSettingsPage voiceSettings;
    private static final String[] SECTION_TITLES = {"Главный экран", "Настройки автомобиля", "Приложения и разделение экрана", "Apollo Tech", "Собственные команды", "Кнопки на руле", "Другое", "Голосовое управление"};
    private static final String[] LIGHT_DIAGNOSTICS_LABELS = {"SWReason", "RSM · внешняя освещённость", "RSM · освещённость впереди", "RSM · ИК-освещённость", "CarSignal · уровень света", "CarSignal · PAS уровень света", "Android · освещённость (лк)"};
    static final String[][] EXAMPLE_COMMANDS = {new String[]{"64 08 80 00 00 00 00 00 00 03", "обогрев руля вкл"}, new String[]{"64 08 40 00 00 00 00 00 00 03", "обогрев руля выкл"}, new String[]{"65 08 00 00 c1 c0 20 00 00 00", "обогрев заднего стекла вкл"}, new String[]{"65 08 00 00 c1 c0 10 00 00 00", "обогрев заднего стекла выкл"}, new String[]{"7a 08 00 00 00 00 01 00 00 00", "автодальний вкл"}, new String[]{"7a 08 00 00 00 00 02 00 00 00", "автодальний выкл"}, new String[]{"68 08 02 00 00 f0 2c 54 08 00", "форсе EV вкл"}, new String[]{"68 08 02 00 00 f0 2c 24 08 00", "форсе EV выкл"}};
    private static final int[] DPI_VALUES = {0, 120, 140, 160, 180, 200, 213, 240, 260, 280, 300, 320, 360};
    private static final String[] DPI_LABELS = {"Авто", "120", "140", "160", "180", "200", "213", "240", "260", "280", "300", "320", "360"};
    static final String[][] STEER_ACTIONS = {new String[]{"none", "Не менять"}, new String[]{"open_voyahtune", "Открыть VoyahTune"}, new String[]{"system_back", "Системное действие: Назад"}, new String[]{"energy:EV", "Энергорежим: Electric"}, new String[]{"energy:REV", "Энергорежим: Fuel"}, new String[]{"energy:SREV", "Энергорежим: Save"}, new String[]{"energy:EV,REV", "Энергорежим: Electric → Fuel"}, new String[]{"energy:EV,REV,SREV", "Энергорежим: Electric → Fuel → Save"}, new String[]{"energy:EV,SREV", "Энергорежим: Electric → Save"}, new String[]{"energy:REV,SREV", "Энергорежим: Fuel → Save"}, new String[]{"drive:ECO", "Режим езды: Eco"}, new String[]{"drive:COMFORT", "Режим езды: Comf"}, new String[]{"drive:SPORT", "Режим езды: Sport"}, new String[]{"drive:OUTING", "Режим езды: Outing"}, new String[]{"drive:SNOW", "Режим езды: Snow"}, new String[]{"drive:INDIVIDUAL", "Режим езды: Indiv"}, new String[]{"drive:ECO,COMFORT", "Режим езды: Eco → Comf"}, new String[]{"drive:ECO,SPORT", "Режим езды: Eco → Sport"}, new String[]{"drive:ECO,OUTING", "Режим езды: Eco → Outing"}, new String[]{"drive:ECO,SNOW", "Режим езды: Eco → Snow"}, new String[]{"drive:ECO,INDIVIDUAL", "Режим езды: Eco → Indiv"}, new String[]{"drive:COMFORT,SPORT", "Режим езды: Comf → Sport"}, new String[]{"drive:COMFORT,OUTING", "Режим езды: Comf → Outing"}, new String[]{"drive:COMFORT,SNOW", "Режим езды: Comf → Snow"}, new String[]{"drive:COMFORT,INDIVIDUAL", "Режим езды: Comf → Indiv"}, new String[]{"drive:SPORT,OUTING", "Режим езды: Sport → Outing"}, new String[]{"drive:SPORT,SNOW", "Режим езды: Sport → Snow"}, new String[]{"drive:SPORT,INDIVIDUAL", "Режим езды: Sport → Indiv"}, new String[]{"drive:OUTING,SNOW", "Режим езды: Outing → Snow"}, new String[]{"drive:OUTING,INDIVIDUAL", "Режим езды: Outing → Indiv"}, new String[]{"drive:SNOW,INDIVIDUAL", "Режим езды: Snow → Indiv"}, new String[]{"recycle:LOW", "Рекуперация: Низкая"}, new String[]{"recycle:MEDIUM", "Рекуперация: Стандартная"}, new String[]{"recycle:HIGH", "Рекуперация: Высокая"}, new String[]{"recycle:LOW,MEDIUM", "Рекуперация: Низкая → Стандартная"}, new String[]{"recycle:LOW,HIGH", "Рекуперация: Низкая → Высокая"}, new String[]{"recycle:MEDIUM,HIGH", "Рекуперация: Стандартная → Высокая"}, new String[]{"toggle_suspension_maintenance", "Сервисный режим подвески: вкл/выкл"}, new String[]{"toggle_forced_ev", "Force EV: вкл/выкл"}, new String[]{"toggle_pedestrian_sound", "Звук пешеходов: вкл/выкл"}, new String[]{"toggle_headlights", "Фары: выкл/ближний"}, new String[]{"toggle_headlights_auto", "Фары: ближний/авто"}};
    private final List<ImageButton> deleteButtons = new ArrayList();
    private final Object cpuSampleLock = new Object();
    private long cpuBaselineGeneration = -1;
    private long previousCpuTotal = -1;
    private long previousCpuIdle = -1;
    private final ExecutorService systemMetricsExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda78
        @Override // java.util.concurrent.ThreadFactory
        public final Thread newThread(Runnable runnable) {
            return AdvanceActivity.lambda$new$0(runnable);
        }
    });
    private final TextView[] lightDiagnosticsRows = new TextView[7];
    private final SensorEventListener androidLightListener = new SensorEventListener() { // from class: ru.big.town.restoremode.AdvanceActivity.1
        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (!AdvanceActivity.this.lightDiagnosticsActive || sensorEvent.values.length <= 0 || AdvanceActivity.this.lightDiagnosticsRows[6] == null) {
                return;
            }
            AdvanceActivity.this.lightDiagnosticsRows[6].setText(AdvanceActivity.LIGHT_DIAGNOSTICS_LABELS[6] + ": " + String.format(Locale.getDefault(), "%.1f", Float.valueOf(sensorEvent.values[0])));
        }
    };
    private final Messenger lightDiagnosticsClient = new Messenger(new Handler(Looper.getMainLooper()) { // from class: ru.big.town.restoremode.AdvanceActivity.2
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 2) {
                int[] intArray = message.getData().getIntArray("values");
                if (intArray != null && intArray.length == 6 && message.arg1 == AdvanceActivity.this.lightDiagnosticsSession && AdvanceActivity.this.lightDiagnosticsActive) {
                    AdvanceActivity.this.showLightDiagnostics(intArray);
                    return;
                }
                return;
            }
            super.handleMessage(message);
        }
    });
    private final ServiceConnection lightDiagnosticsConnection = new ServiceConnection() { // from class: ru.big.town.restoremode.AdvanceActivity.3
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (AdvanceActivity.this.lightDiagnosticsActive && AdvanceActivity.this.lightDiagnosticsBound) {
                Message messageObtain = Message.obtain((Handler) null, 1);
                messageObtain.arg1 = AdvanceActivity.this.lightDiagnosticsSession;
                messageObtain.replyTo = AdvanceActivity.this.lightDiagnosticsClient;
                try {
                    new Messenger(iBinder).send(messageObtain);
                } catch (RemoteException e) {
                    Log.w("LightDiagnostics", "Watch failed", e);
                }
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            int[] iArr = new int[6];
            Arrays.fill(iArr, Integer.MIN_VALUE);
            AdvanceActivity.this.showLightDiagnostics(iArr);
        }
    };
    private boolean applying = false;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final Runnable applyTimeout = new Runnable() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda79
        @Override // java.lang.Runnable
        public final void run() {
            AdvanceActivity.this.m1777lambda$new$1$rubigtownrestoremodeAdvanceActivity();
        }
    };
    private final Runnable systemMetricsTick = new Runnable() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda80
        @Override // java.lang.Runnable
        public final void run() {
            AdvanceActivity.this.sampleSystemMetrics();
        }
    };
    private final Messenger applyClient = new Messenger(new Handler(Looper.getMainLooper()) { // from class: ru.big.town.restoremode.AdvanceActivity.4
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 4) {
                AdvanceActivity.this.setApplying(false);
            } else {
                super.handleMessage(message);
            }
        }
    });
    private final BroadcastReceiver commandResultReceiver = new BroadcastReceiver() { // from class: ru.big.town.restoremode.AdvanceActivity.5x
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            AdvanceActivity.this.renderCommandResult(intent);
        }
    };
    private final BroadcastReceiver luxReceiver = new BroadcastReceiver() { // from class: ru.big.town.restoremode.AdvanceActivity.5
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int intExtra = intent.getIntExtra("sensorLevel", -1);
            if (AdvanceActivity.this.textSensorLevel != null) {
                AdvanceActivity.this.textSensorLevel.setText(intExtra >= 0 ? "Датчик: " + intExtra : "Датчик: —");
            }
        }
    };
    private final BroadcastReceiver modeSyncReceiver = new BroadcastReceiver() { // from class: ru.big.town.restoremode.AdvanceActivity.6
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String str;
            int i;
            String stringExtra = intent.getStringExtra("mode");
            if (stringExtra == null || stringExtra.isEmpty()) {
                return;
            }
            String stringExtra2 = intent.getStringExtra(AdvanceActivity.EXTRA_MODE_KEY);
            if (stringExtra2 == null) {
                stringExtra2 = intent.getBooleanExtra("isEnergy", false) ? "energy" : "driveMode";
            }
            if ("energy".equals(stringExtra2)) {
                str = "energyRememberLast";
            } else {
                str = "recycle".equals(stringExtra2) ? "recycleRememberLast" : "driveRememberLast";
            }
            if (AdvanceActivity.this.prefs.getBoolean(str, true)) {
                if ("energy".equals(stringExtra2)) {
                    i = R.id.energy_modes_group;
                } else {
                    i = "recycle".equals(stringExtra2) ? R.id.recycle_modes_group : R.id.drive_modes_group;
                }
                RadioGroup radioGroup = (RadioGroup) AdvanceActivity.this.findViewById(i);
                AdvanceActivity.this.syncingModeUi = true;
                if (radioGroup != null) {
                    try {
                        AdvanceActivity.this.checkRadioByTag(radioGroup, stringExtra);
                    } finally {
                        AdvanceActivity.this.syncingModeUi = false;
                    }
                }
            }
        }
    };
    private final BroadcastReceiver settingSyncReceiver = new BroadcastReceiver() { // from class: ru.big.town.restoremode.AdvanceActivity.7
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            RadioGroup radioGroup;
            String stringExtra = intent.getStringExtra("key");
            if (stringExtra == null || !intent.hasExtra("value")) {
                return;
            }
            boolean booleanExtra = intent.getBooleanExtra("value", false);
            AdvanceActivity.this.prefs.edit().putBoolean(stringExtra, booleanExtra).apply();
            AdvanceActivity.this.syncingSettingUi = true;
            try {
                if ("forcedEv".equals(stringExtra)) {
                    RadioGroup radioGroup2 = (RadioGroup) AdvanceActivity.this.findViewById(R.id.forcedEvGroup);
                    if (radioGroup2 != null) {
                        radioGroup2.check(booleanExtra ? R.id.forcedEvOn : R.id.forcedEvOff);
                    }
                } else if ("autoLight".equals(stringExtra)) {
                    if (AdvanceActivity.this.autoLightGroup != null) {
                        AdvanceActivity.this.autoLightGroup.check(booleanExtra ? R.id.autoLightOn : R.id.autoLightOff);
                    }
                } else if ("suspensionMaintenance".equals(stringExtra)) {
                    Switch r4 = (Switch) AdvanceActivity.this.findViewById(R.id.switchSuspensionMaintenance);
                    if (r4 != null) {
                        r4.setChecked(booleanExtra);
                    }
                } else if ("disablePedestrianSound".equals(stringExtra) && (radioGroup = (RadioGroup) AdvanceActivity.this.findViewById(R.id.pedestrianSoundGroup)) != null) {
                    radioGroup.check(booleanExtra ? R.id.pedestrianSoundOn : R.id.pedestrianSoundOff);
                }
            } finally {
                AdvanceActivity.this.syncingSettingUi = false;
            }
        }
    };

    interface AppPicked {
        void onPicked(String str, String str2);
    }

    static /* synthetic */ Thread lambda$new$0(Runnable runnable) {
        Thread thread = new Thread(runnable, "VoyahTune-system-metrics");
        thread.setPriority(1);
        return thread;
    }

    /* JADX INFO: renamed from: lambda$new$1$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1777lambda$new$1$rubigtownrestoremodeAdvanceActivity() {
        setApplying(false);
    }

    public void onButtonClickFinish(View view) {
        finishWithCustomCommands();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishWithCustomCommands() {
        if (saveCustomCommands()) {
            Intent intent = new Intent();
            intent.putExtra("customCommand", this.canCommandsEditor.getText().toString());
            intent.putExtra("customCommandCount", this.pickerCustomCommandCount.getValue());
            setResult(-1, intent);
            finish();
        }
    }

    private boolean saveCustomCommands() {
        ImageButton imageButton = this.buttonBack;
        if (imageButton != null && !imageButton.isEnabled()) {
            Log.w("$$$ Advance commands $$$", "Команды не сохранены: неверный формат");
            return false;
        }
        this.prefs.edit().putString("customCommand", this.canCommandsEditor.getText().toString()).putInt("customCommandCount", this.pickerCustomCommandCount.getValue()).apply();
        return true;
    }

    public void onButtonClickClean(View view) {
        this.canCommandsEditor.setText("");
    }

    private void buildExampleButtons() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.examplesContainer);
        if (linearLayout == null) {
            return;
        }
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(this);
        for (String[] strArr : EXAMPLE_COMMANDS) {
            final String str = strArr[0];
            String str2 = strArr[1];
            View viewInflate = layoutInflaterFrom.inflate(R.layout.item_command, (ViewGroup) linearLayout, false);
            Button button = (Button) viewInflate.findViewById(R.id.cmdButton);
            ImageButton imageButton = (ImageButton) viewInflate.findViewById(R.id.cmdDelete);
            button.setText(str2);
            button.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda81
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AdvanceActivity.this.m1762xff08bd03(str, view);
                }
            });
            imageButton.setTag(str.replaceAll("[^0-9a-fA-F]", "").toLowerCase());
            imageButton.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda82
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AdvanceActivity.this.m1763x8ba8e804(str, view);
                }
            });
            this.deleteButtons.add(imageButton);
            linearLayout.addView(viewInflate);
        }
        updateDeleteButtons();
    }

    /* JADX INFO: renamed from: lambda$buildExampleButtons$2$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1762xff08bd03(String str, View view) {
        insertCommand(str);
    }

    /* JADX INFO: renamed from: lambda$buildExampleButtons$3$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1763x8ba8e804(String str, View view) {
        removeCommand(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDeleteButtons() {
        if (this.canCommandsEditor == null) {
            return;
        }
        HashSet hashSet = new HashSet();
        for (String str : this.canCommandsEditor.getText().toString().split("\n")) {
            String lowerCase = str.replaceAll("[^0-9a-fA-F]", "").toLowerCase();
            if (!lowerCase.isEmpty()) {
                hashSet.add(lowerCase);
            }
        }
        for (ImageButton imageButton : this.deleteButtons) {
            String str2 = (String) imageButton.getTag();
            boolean z = str2 != null && hashSet.contains(str2);
            imageButton.setEnabled(z);
            imageButton.setAlpha(z ? 1.0f : 0.3f);
        }
    }

    private void insertCommand(String str) {
        String string = this.canCommandsEditor.getText().toString();
        if (string.length() > 0 && !string.endsWith("\n")) {
            string = string + "\n";
        }
        this.canCommandsEditor.setText(string + str + "\n");
        EditText editText = this.canCommandsEditor;
        editText.setSelection(editText.getText().length());
    }

    private void removeCommand(String str) {
        String lowerCase = str.replaceAll("[^0-9a-fA-F]", "").toLowerCase();
        String[] strArrSplit = this.canCommandsEditor.getText().toString().split("\n");
        StringBuilder sb = new StringBuilder();
        boolean z = false;
        for (String str2 : strArrSplit) {
            String lowerCase2 = str2.replaceAll("[^0-9a-fA-F]", "").toLowerCase();
            if (!lowerCase2.isEmpty()) {
                if (z || !lowerCase2.equals(lowerCase)) {
                    sb.append(lowerCase2).append("\n");
                } else {
                    z = true;
                }
            }
        }
        this.canCommandsEditor.setText(sb.toString());
    }

    /* JADX WARN: Code duplicated, block: B:14:0x0208  */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        int i;
        super.onCreate(bundle);
        EdgeToEdge.enable(this);
        setRequestedOrientation(0);
        setContentView(R.layout.activity_advance);
        applyWindowInsets();
        this.prefs = getSharedPreferences("DrivePreferences", 0);
        this.buttonApplyAdvance = (Button) findViewById(R.id.buttonApplyAdvance);
        this.applyProgressAdvance = (ProgressBar) findViewById(R.id.applyProgressAdvance);
        this.sectionTitle = (TextView) findViewById(R.id.sectionTitle);
        this.commandStatusText = (TextView) findViewById(R.id.commandStatusText);
        this.canCommandsEditor = (EditText) findViewById(R.id.rawCanCodes);
        this.buttonBack = (ImageButton) findViewById(R.id.buttonBack);
        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.pickerCustomCommandCount);
        this.pickerCustomCommandCount = numberPicker;
        numberPicker.setMaxValue(10);
        this.pickerCustomCommandCount.setMinValue(1);
        this.pickerCustomCommandCount.setTextColor(-1);
        this.pickerCustomCommandCount.setTextSize(40.0f);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) { // from class: ru.big.town.restoremode.AdvanceActivity.8
            @Override // androidx.activity.OnBackPressedCallback
            public void handleOnBackPressed() {
                AdvanceActivity.this.finishWithCustomCommands();
            }
        });
        Intent intent = getIntent();
        if (intent != null) {
            String stringExtra = intent.hasExtra("customCommand") ? intent.getStringExtra("customCommand") : this.prefs.getString("customCommand", "");
            int intExtra = intent.getIntExtra("customCommandCount", this.prefs.getInt("customCommandCount", 1));
            this.canCommandsEditor.setText(stringExtra);
            this.pickerCustomCommandCount.setValue(intExtra);
            Log.i("$$$ Advance Create $$$$", String.format("%s %d", stringExtra, Integer.valueOf(intExtra)));
        }
        ((TextView) findViewById(R.id.TextWarn)).setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) AdvanceActivity.this.getSystemService("input_method");
                View currentFocus = AdvanceActivity.this.getCurrentFocus();
                if (inputMethodManager == null || currentFocus == null) {
                    return;
                }
                inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                currentFocus.clearFocus();
            }
        });
        this.canCommandsEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity.10
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View view, boolean z) {
                if (z) {
                    Log.i("$$$ setOnFocusChangeListener $$$$", "FOCUS ON");
                } else {
                    Log.i("$$$ setOnFocusChangeListener $$$$", "FOCUS OFF");
                }
            }
        });
        this.canCommandsEditor.addTextChangedListener(new TextWatcher() { // from class: ru.big.town.restoremode.AdvanceActivity.11
            private boolean isFormatting = false;

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                Log.i("$$$ beforeTextChanged $$$", charSequence.toString() + String.format("int start, int count, int after: %d, %d %d ", Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4)));
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                Log.i("$$$ onTextChanged $$$", charSequence.toString() + String.format("int start, int before, int count: %d, %d %d ", Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4)));
                if (this.isFormatting) {
                    return;
                }
                this.isFormatting = true;
                String[] strArrSplit = charSequence.toString().toLowerCase().replaceAll("[^0-9a-f,\n]", "").split("\n");
                StringBuilder sb = new StringBuilder();
                for (String str : strArrSplit) {
                    Log.i("LENGTH i", String.format("%s %d", str, Integer.valueOf(str.length())));
                    for (int i5 = 0; i5 < str.length(); i5++) {
                        if (i5 % 2 == 0) {
                            sb.append(" ");
                        }
                        sb.append(str.charAt(i5));
                        if (i5 >= 19) {
                            sb.append("\n");
                        }
                    }
                }
                Log.i("$$$ LENGTH formatted.length $$$ ", String.format("%d", Integer.valueOf(sb.length())));
                if (sb.length() % 31 == 0) {
                    AdvanceActivity.this.canCommandsEditor.setBackgroundColor(-1);
                    AdvanceActivity.this.buttonBack.setEnabled(true);
                    AdvanceActivity.this.buttonBack.setAlpha(1.0f);
                } else {
                    AdvanceActivity.this.canCommandsEditor.setBackgroundColor(-20561);
                    AdvanceActivity.this.buttonBack.setEnabled(false);
                    AdvanceActivity.this.buttonBack.setAlpha(0.4f);
                }
                AdvanceActivity.this.canCommandsEditor.removeTextChangedListener(this);
                AdvanceActivity.this.canCommandsEditor.setText(sb.toString());
                AdvanceActivity.this.canCommandsEditor.setSelection(sb.length());
                AdvanceActivity.this.canCommandsEditor.addTextChangedListener(this);
                this.isFormatting = false;
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                Log.i("$$$ afterTextChanged $$$", editable.toString());
                AdvanceActivity.this.updateDeleteButtons();
            }
        });
        buildExampleButtons();
        this.navMainScreen = (TextView) findViewById(R.id.navMainScreen);
        this.navCustomCommands = (TextView) findViewById(R.id.navCustomCommands);
        this.navDriveModes = (TextView) findViewById(R.id.navDriveModes);
        this.navSplitScreen = (TextView) findViewById(R.id.navSplitScreen);
        this.navApolloTech = (TextView) findViewById(R.id.navApolloTech);
        this.navSteeringButtons = (TextView) findViewById(R.id.navSteeringButtons);
        this.navOther = (TextView) findViewById(R.id.navOther);
        this.navVoiceControl = (TextView) findViewById(R.id.navVoiceControl);
        this.pageMainScreen = findViewById(R.id.pageMainScreen);
        this.pageCustomCommands = findViewById(R.id.pageCustomCommands);
        this.pageDriveModes = findViewById(R.id.pageDriveModes);
        this.pageSplitScreen = findViewById(R.id.pageSplitScreen);
        this.pageApolloTech = findViewById(R.id.pageApolloTech);
        this.pageSteeringButtons = findViewById(R.id.pageSteeringButtons);
        this.pageOther = findViewById(R.id.pageOther);
        this.pageVoiceControl = findViewById(R.id.pageVoiceControl);
        this.voiceSettings = new VoiceSettingsPage(this, (LinearLayout) findViewById(R.id.voiceSettingsContent), this.prefs, new Runnable() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                AdvanceActivity.this.refreshSteerActions();
            }
        });
        this.textRamStatus = (TextView) findViewById(R.id.textRamStatus);
        this.textCpuStatus = (TextView) findViewById(R.id.textCpuStatus);
        this.textHookStatus = (TextView) findViewById(R.id.textHookStatus);
        this.navMainScreen.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda21
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AdvanceActivity.this.m1800lambda$onCreate$4$rubigtownrestoremodeAdvanceActivity(view);
            }
        });
        this.navDriveModes.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda27
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AdvanceActivity.this.m1801lambda$onCreate$5$rubigtownrestoremodeAdvanceActivity(view);
            }
        });
        this.navSplitScreen.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda28
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AdvanceActivity.this.m1802lambda$onCreate$6$rubigtownrestoremodeAdvanceActivity(view);
            }
        });
        this.navCustomCommands.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda29
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AdvanceActivity.this.m1803lambda$onCreate$7$rubigtownrestoremodeAdvanceActivity(view);
            }
        });
        this.navApolloTech.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda30
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AdvanceActivity.this.m1804lambda$onCreate$8$rubigtownrestoremodeAdvanceActivity(view);
            }
        });
        this.navSteeringButtons.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda31
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AdvanceActivity.this.m1805lambda$onCreate$9$rubigtownrestoremodeAdvanceActivity(view);
            }
        });
        this.navOther.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda32
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AdvanceActivity.this.m1783lambda$onCreate$10$rubigtownrestoremodeAdvanceActivity(view);
            }
        });
        this.navVoiceControl.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda34
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AdvanceActivity.this.m1784lambda$onCreate$11$rubigtownrestoremodeAdvanceActivity(view);
            }
        });
        initApolloTech();
        i = 0;
        if (intent != null) {
            i = intent.getIntExtra(EXTRA_SECTION, 0) != 7 ? 0 : 7;
        }
        setSection(i);
        this.navCustomCommands.setVisibility(this.prefs.getBoolean(PREF_SHOW_CUSTOM_COMMANDS, false) ? 0 : 8);
        bindShowSwitch(R.id.switchShowTripTimer, "showTripTimer", true);
        bindShowSwitch(R.id.switchShowPowerHold, "showPowerHold", true);
        bindShowSwitch(R.id.switchShowWashMode, "showWashMode", true);
        bindShowSwitch(R.id.switchShowAutoLight, "showAutoLight", true);
        bindShowSwitch(R.id.switchShowPedestrian, "showPedestrian", true);
        bindShowSwitch(R.id.switchShowBatteryHeat, "showBatteryHeat", true);
        bindShowSwitch(R.id.switchShowVoiceCommand, "showVoiceCommand", false);
        bindShowSwitch(R.id.switchShowForcedEv, "showForcedEv", false);
        bindShowSwitch(R.id.switchShowSuspensionMaintenance, "showSuspensionMaintenance", false);
        bindShowSwitch(R.id.switchShowLaunchAppsWidget, "showLaunchAppsWidget", false, R.id.launchAppsSizeRow);
        bindTileSizeSpinners(R.id.launchAppsSettingWidth, R.id.launchAppsSettingHeight, "launchAppsWidget", 2, 3);
        bindShowSwitch(R.id.switchShowSuspensionWidget, "showSuspensionWidget", false, R.id.suspensionSizeRow);
        bindTileSizeSpinners(R.id.suspensionSettingWidth, R.id.suspensionSettingHeight, "suspensionWidget", 4, 3);
        initDialWidgets();
        Switch r1 = (Switch) findViewById(R.id.switchSaveTripHistory);
        r1.setChecked(this.prefs.getBoolean("saveTripHistory", true));
        r1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda35
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                AdvanceActivity.this.m1785lambda$onCreate$12$rubigtownrestoremodeAdvanceActivity(compoundButton, z);
            }
        });
        initAppShortcuts();
        initAppWidgets();
        initDockOverride();
        initFullscreenApps();
        initSplitScreen();
        initAppDpiList();
        initModeRadios();
        initModeEnableToggles();
        initModeRememberLastToggles();
        initFragranceSettings();
        initCheckBox34();
        initPedestrianSoundGroup();
        initForcedEvGroup();
        initSuspensionMaintenance();
        Switch r2 = (Switch) findViewById(R.id.switchBatteryHeatAuto);
        if (r2 != null) {
            r2.setChecked(this.prefs.getBoolean("batteryHeatAuto", false));
            r2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda10
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    AdvanceActivity.this.m1786lambda$onCreate$13$rubigtownrestoremodeAdvanceActivity(compoundButton, z);
                }
            });
        }
        initAutoLight();
        Switch r3 = (Switch) findViewById(R.id.switchWiperCold);
        r3.setChecked(this.prefs.getBoolean("wiperColdMode", false));
        r3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda12
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                AdvanceActivity.this.m1787lambda$onCreate$14$rubigtownrestoremodeAdvanceActivity(compoundButton, z);
            }
        });
        Switch r4 = (Switch) findViewById(R.id.switchPauseMediaOnDoor);
        if (r4 != null) {
            r4.setChecked(this.prefs.getBoolean("pauseMediaOnDoor", false));
            r4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda13
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    AdvanceActivity.this.m1788lambda$onCreate$15$rubigtownrestoremodeAdvanceActivity(compoundButton, z);
                }
            });
        }
        ((TextView) findViewById(R.id.textAppVersion)).setText(BuildConfig.VERSION_NAME);
        findViewById(R.id.buttonOpenUpdates).setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda14
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AdvanceActivity.this.m1789lambda$onCreate$16$rubigtownrestoremodeAdvanceActivity(view);
            }
        });
        Switch r5 = (Switch) findViewById(R.id.switchDebugMode);
        final View viewFindViewById = findViewById(R.id.debugInformationBlock);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.debugLightSensorRows);
        for (int i2 = 0; i2 < this.lightDiagnosticsRows.length; i2++) {
            TextView textView = new TextView(this);
            textView.setTextColor(-1);
            textView.setTextSize(0, 20.0f);
            textView.setPadding(0, 4, 0, 4);
            linearLayout.addView(textView);
            this.lightDiagnosticsRows[i2] = textView;
        }
        resetLightDiagnostics();
        r5.setChecked(this.prefs.getBoolean("debugMode", false));
        viewFindViewById.setVisibility(r5.isChecked() ? 0 : 8);
        r5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda15
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                AdvanceActivity.this.m1790lambda$onCreate$17$rubigtownrestoremodeAdvanceActivity(viewFindViewById, compoundButton, z);
            }
        });
        Switch r6 = (Switch) findViewById(R.id.switchFullscreenGrid);
        final NumberPicker numberPicker2 = (NumberPicker) findViewById(R.id.pickerFullscreenGridColumns);
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(12);
        numberPicker2.setTextColor(-1);
        numberPicker2.setTextSize(40.0f);
        numberPicker2.setValue(this.prefs.getInt("fullscreenGridColumns", 8));
        numberPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda16
            @Override // android.widget.NumberPicker.OnValueChangeListener
            public final void onValueChange(NumberPicker numberPicker3, int i3, int i4) {
                AdvanceActivity.this.m1791lambda$onCreate$18$rubigtownrestoremodeAdvanceActivity(numberPicker3, i3, i4);
            }
        });
        r6.setChecked(this.prefs.getBoolean("fullscreenGrid", false));
        numberPicker2.setEnabled(r6.isChecked());
        r6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda17
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                AdvanceActivity.this.m1792lambda$onCreate$19$rubigtownrestoremodeAdvanceActivity(numberPicker2, compoundButton, z);
            }
        });
        final Switch switchKeyboardEnglish = (Switch) findViewById(R.id.switchKeyboardEnglish);
        final Switch switchKeyboardRussian = (Switch) findViewById(R.id.switchKeyboardRussian);
        if (switchKeyboardEnglish != null && switchKeyboardRussian != null) {
            String strNormalizeKeyboardMode = SplitConfigSync.normalizeKeyboardMode(this.prefs.getString("keyboardMode", "off"));
            switchKeyboardEnglish.setChecked("en".equals(strNormalizeKeyboardMode));
            switchKeyboardRussian.setChecked("ru".equals(strNormalizeKeyboardMode));
            final boolean[] zArr = {false};
            switchKeyboardEnglish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda18
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    AdvanceActivity.this.m1793lambda$onCreate$20$rubigtownrestoremodeAdvanceActivity(zArr, switchKeyboardRussian, compoundButton, z);
                }
            });
            switchKeyboardRussian.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda19
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    AdvanceActivity.this.m1794lambda$onCreate$21$rubigtownrestoremodeAdvanceActivity(zArr, switchKeyboardEnglish, compoundButton, z);
                }
            });
        }
        Switch r9 = (Switch) findViewById(R.id.switchShowCustomCommands);
        r9.setChecked(this.prefs.getBoolean(PREF_SHOW_CUSTOM_COMMANDS, false));
        r9.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda20
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                AdvanceActivity.this.m1795lambda$onCreate$22$rubigtownrestoremodeAdvanceActivity(compoundButton, z);
            }
        });
        Switch r10 = (Switch) findViewById(R.id.switchAutoLaunch);
        r10.setChecked(this.prefs.getBoolean("autoLaunchOnWake", false));
        r10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda23
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                AdvanceActivity.this.m1796lambda$onCreate$23$rubigtownrestoremodeAdvanceActivity(compoundButton, z);
            }
        });
        Switch r11 = (Switch) findViewById(R.id.switchFloatingBack);
        r11.setChecked(this.prefs.getBoolean("floatingBackButton", false));
        r11.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda24
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                AdvanceActivity.this.m1797lambda$onCreate$24$rubigtownrestoremodeAdvanceActivity(compoundButton, z);
            }
        });
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.themeOverrideGroup);
        if (radioGroup != null) {
            checkRadioByTag(radioGroup, String.valueOf(this.prefs.getInt("themeOverride", 0)));
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda25
                @Override // android.widget.RadioGroup.OnCheckedChangeListener
                public final void onCheckedChanged(RadioGroup radioGroup2, int i3) {
                    AdvanceActivity.this.m1798lambda$onCreate$25$rubigtownrestoremodeAdvanceActivity(radioGroup2, i3);
                }
            });
        }
        showEngineeringPassword();
        RadioGroup radioGroup2 = (RadioGroup) findViewById(R.id.floatingBackSideGroup);
        if (radioGroup2 != null) {
            checkRadioByTag(radioGroup2, String.valueOf(this.prefs.getInt("floatingBackSide", 0)));
            radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda26
                @Override // android.widget.RadioGroup.OnCheckedChangeListener
                public final void onCheckedChanged(RadioGroup radioGroup3, int i3) {
                    AdvanceActivity.this.m1799lambda$onCreate$26$rubigtownrestoremodeAdvanceActivity(radioGroup3, i3);
                }
            });
        }
        initSteeringButtons();
    }

    /* JADX INFO: renamed from: lambda$onCreate$4$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1800lambda$onCreate$4$rubigtownrestoremodeAdvanceActivity(View view) {
        setSection(0);
    }

    /* JADX INFO: renamed from: lambda$onCreate$5$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1801lambda$onCreate$5$rubigtownrestoremodeAdvanceActivity(View view) {
        setSection(1);
    }

    /* JADX INFO: renamed from: lambda$onCreate$6$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1802lambda$onCreate$6$rubigtownrestoremodeAdvanceActivity(View view) {
        setSection(2);
    }

    /* JADX INFO: renamed from: lambda$onCreate$7$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1803lambda$onCreate$7$rubigtownrestoremodeAdvanceActivity(View view) {
        setSection(4);
    }

    /* JADX INFO: renamed from: lambda$onCreate$8$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1804lambda$onCreate$8$rubigtownrestoremodeAdvanceActivity(View view) {
        setSection(3);
    }

    /* JADX INFO: renamed from: lambda$onCreate$9$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1805lambda$onCreate$9$rubigtownrestoremodeAdvanceActivity(View view) {
        setSection(5);
    }

    /* JADX INFO: renamed from: lambda$onCreate$10$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1783lambda$onCreate$10$rubigtownrestoremodeAdvanceActivity(View view) {
        setSection(6);
    }

    /* JADX INFO: renamed from: lambda$onCreate$11$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1784lambda$onCreate$11$rubigtownrestoremodeAdvanceActivity(View view) {
        setSection(7);
    }

    /* JADX INFO: renamed from: lambda$onCreate$12$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1785lambda$onCreate$12$rubigtownrestoremodeAdvanceActivity(CompoundButton compoundButton, boolean z) {
        this.prefs.edit().putBoolean("saveTripHistory", z).apply();
        Intent intent = new Intent("ru.big.town.anative.TRIP_HISTORY").setPackage(NATIVE_PACKAGE);
        intent.putExtra("enabled", z);
        sendBroadcast(intent);
    }

    /* JADX INFO: renamed from: lambda$onCreate$13$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1786lambda$onCreate$13$rubigtownrestoremodeAdvanceActivity(CompoundButton compoundButton, boolean z) {
        this.prefs.edit().putBoolean("batteryHeatAuto", z).apply();
        sendBroadcast(new Intent(ACTION_BATTERY_HEAT_AUTO_CHANGED).setPackage(NATIVE_PACKAGE).putExtra(EXTRA_BATTERY_HEAT_AUTO_ENABLED, z));
    }

    /* JADX INFO: renamed from: lambda$onCreate$14$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1787lambda$onCreate$14$rubigtownrestoremodeAdvanceActivity(CompoundButton compoundButton, boolean z) {
        this.prefs.edit().putBoolean("wiperColdMode", z).apply();
    }

    /* JADX INFO: renamed from: lambda$onCreate$15$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1788lambda$onCreate$15$rubigtownrestoremodeAdvanceActivity(CompoundButton compoundButton, boolean z) {
        this.prefs.edit().putBoolean("pauseMediaOnDoor", z).apply();
    }

    /* JADX INFO: renamed from: lambda$onCreate$16$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1789lambda$onCreate$16$rubigtownrestoremodeAdvanceActivity(View view) {
        try {
            startActivity(new Intent("android.intent.action.MAIN").setComponent(new ComponentName("ru.big.town.updater", "ru.big.town.updater.MainActivity")).addFlags(268468224).putExtra("ru.big.town.updater.OPEN_INITIAL_SCREEN", true));
        } catch (ActivityNotFoundException | SecurityException unused) {
            Toast.makeText(this, "Обновления недоступны. Установите релиз с поддержкой OTA через USB с компьютера.", 1).show();
        }
    }

    /* JADX INFO: renamed from: lambda$onCreate$17$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1790lambda$onCreate$17$rubigtownrestoremodeAdvanceActivity(View view, CompoundButton compoundButton, boolean z) {
        this.prefs.edit().putBoolean("debugMode", z).apply();
        view.setVisibility(z ? 0 : 8);
        updateLightDiagnosticsBinding();
    }

    /* JADX INFO: renamed from: lambda$onCreate$18$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1791lambda$onCreate$18$rubigtownrestoremodeAdvanceActivity(NumberPicker numberPicker, int i, int i2) {
        this.prefs.edit().putInt("fullscreenGridColumns", i2).apply();
    }

    /* JADX INFO: renamed from: lambda$onCreate$19$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1792lambda$onCreate$19$rubigtownrestoremodeAdvanceActivity(NumberPicker numberPicker, CompoundButton compoundButton, boolean z) {
        this.prefs.edit().putBoolean("fullscreenGrid", z).apply();
        numberPicker.setEnabled(z);
    }

    /* JADX INFO: renamed from: lambda$onCreate$20$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1793lambda$onCreate$20$rubigtownrestoremodeAdvanceActivity(boolean[] zArr, Switch switchKeyboardRussian, CompoundButton compoundButton, boolean checked) {
        String str;
        if (zArr[0]) {
            return;
        }
        zArr[0] = true;
        if (checked) switchKeyboardRussian.setChecked(false);
        if (checked) {
            str = "en";
        } else {
            str = switchKeyboardRussian.isChecked() ? "ru" : "off";
        }
        this.prefs.edit().putString("keyboardMode", str).apply();
        SplitConfigSync.pushKeyboard(this, this.prefs);
        zArr[0] = false;
    }

    /* JADX INFO: renamed from: lambda$onCreate$21$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1794lambda$onCreate$21$rubigtownrestoremodeAdvanceActivity(boolean[] zArr, Switch switchKeyboardEnglish, CompoundButton compoundButton, boolean checked) {
        String str;
        if (zArr[0]) {
            return;
        }
        zArr[0] = true;
        if (checked) switchKeyboardEnglish.setChecked(false);
        if (checked) {
            str = "ru";
        } else {
            str = switchKeyboardEnglish.isChecked() ? "en" : "off";
        }
        this.prefs.edit().putString("keyboardMode", str).apply();
        SplitConfigSync.pushKeyboard(this, this.prefs);
        zArr[0] = false;
    }

    /* JADX INFO: renamed from: lambda$onCreate$22$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1795lambda$onCreate$22$rubigtownrestoremodeAdvanceActivity(CompoundButton compoundButton, boolean z) {
        this.prefs.edit().putBoolean(PREF_SHOW_CUSTOM_COMMANDS, z).apply();
        this.navCustomCommands.setVisibility(z ? 0 : 8);
    }

    /* JADX INFO: renamed from: lambda$onCreate$23$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1796lambda$onCreate$23$rubigtownrestoremodeAdvanceActivity(CompoundButton compoundButton, boolean z) {
        this.prefs.edit().putBoolean("autoLaunchOnWake", z).apply();
    }

    /* JADX INFO: renamed from: lambda$onCreate$24$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1797lambda$onCreate$24$rubigtownrestoremodeAdvanceActivity(CompoundButton compoundButton, boolean z) {
        this.prefs.edit().putBoolean("floatingBackButton", z).apply();
        sendFloatingBack(z);
    }

    /* JADX INFO: renamed from: lambda$onCreate$25$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1798lambda$onCreate$25$rubigtownrestoremodeAdvanceActivity(RadioGroup radioGroup, int i) {
        View viewFindViewById = findViewById(i);
        if (viewFindViewById == null || viewFindViewById.getTag() == null) {
            return;
        }
        int i2 = Integer.parseInt(viewFindViewById.getTag().toString());
        this.prefs.edit().putInt("themeOverride", i2).apply();
        sendTheme(i2);
    }

    /* JADX INFO: renamed from: lambda$onCreate$26$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1799lambda$onCreate$26$rubigtownrestoremodeAdvanceActivity(RadioGroup radioGroup, int i) {
        View viewFindViewById = findViewById(i);
        if (viewFindViewById == null || viewFindViewById.getTag() == null) {
            return;
        }
        int i2 = Integer.parseInt(viewFindViewById.getTag().toString());
        this.prefs.edit().putInt("floatingBackSide", i2).apply();
        sendFloatingBackSide(i2);
    }

    private void initDialWidgets() {
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.dialWidgetsContainer);
        ((Button) findViewById(R.id.buttonAddDialWidget)).setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AdvanceActivity.this.m1766xa5bf8624(linearLayout, view);
            }
        });
        renderDialWidgets(linearLayout);
    }

    /* JADX INFO: renamed from: lambda$initDialWidgets$27$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1766xa5bf8624(LinearLayout linearLayout, View view) {
        List<DialWidgetStore.Entry> listLoad = DialWidgetStore.load(this.prefs);
        if (listLoad.size() >= 100) {
            Toast.makeText(this, "Достигнут лимит 100 карточек", 0).show();
            return;
        }
        listLoad.add(new DialWidgetStore.Entry());
        DialWidgetStore.save(this.prefs, listLoad);
        TileOrderStore.sync(this.prefs, getPackageManager());
        renderDialWidgets(linearLayout);
    }

    private void renderDialWidgets(final LinearLayout linearLayout) {
        linearLayout.removeAllViews();
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(this);
        for (final DialWidgetStore.Entry entry : DialWidgetStore.load(this.prefs)) {
            View viewInflate = layoutInflaterFrom.inflate(R.layout.item_dial_widget_setting, (ViewGroup) linearLayout, false);
            final EditText editText = (EditText) viewInflate.findViewById(R.id.dialSettingName);
            final EditText editText2 = (EditText) viewInflate.findViewById(R.id.dialSettingNumber);
            editText.setText(entry.name);
            editText2.setText(entry.number);
            viewInflate.findViewById(R.id.dialSettingSave).setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda52
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AdvanceActivity.this.m1819xc43161eb(editText, editText2, entry, view);
                }
            });
            viewInflate.findViewById(R.id.dialSettingDelete).setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda53
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AdvanceActivity.this.m1820x66953f02(entry, linearLayout, view);
                }
            });
            linearLayout.addView(viewInflate);
        }
    }

    /* JADX INFO: renamed from: lambda$renderDialWidgets$28$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1819xc43161eb(EditText editText, EditText editText2, DialWidgetStore.Entry entry, View view) {
        String strTrim = editText.getText().toString().trim();
        String strReplaceAll = editText2.getText().toString().replaceAll("[^0-9]", "");
        if (strTrim.isEmpty()) {
            editText.setError("Введите имя");
            return;
        }
        if (strReplaceAll.length() < 4 || strReplaceAll.length() > 10) {
            editText2.setError("Введите от 4 до 10 цифр");
            return;
        }
        List<DialWidgetStore.Entry> listLoad = DialWidgetStore.load(this.prefs);
        for (DialWidgetStore.Entry entry2 : listLoad) {
            if (entry2.id.equals(entry.id)) {
                entry2.name = strTrim;
                entry2.number = strReplaceAll;
                break;
            }
        }
        DialWidgetStore.save(this.prefs, listLoad);
        TileOrderStore.sync(this.prefs, getPackageManager());
        Toast.makeText(this, "Карточка сохранена", 0).show();
    }

    /* JADX INFO: renamed from: lambda$renderDialWidgets$30$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1820x66953f02(final DialWidgetStore.Entry entry, LinearLayout linearLayout, View view) {
        List<DialWidgetStore.Entry> listLoad = DialWidgetStore.load(this.prefs);
        listLoad.removeIf(new Predicate() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda22
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((DialWidgetStore.Entry) obj).id.equals(entry.id);
            }
        });
        DialWidgetStore.save(this.prefs, listLoad);
        TileOrderStore.sync(this.prefs, getPackageManager());
        renderDialWidgets(linearLayout);
    }

    private void applyWindowInsets() {
        final int iRound = Math.round(getResources().getDisplayMetrics().density * 145.0f);
        View viewFindViewById = findViewById(R.id.main);
        if (viewFindViewById == null) {
            return;
        }
        ViewCompat.setOnApplyWindowInsetsListener(viewFindViewById, new OnApplyWindowInsetsListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda2
            @Override // androidx.core.view.OnApplyWindowInsetsListener
            public final WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                return AdvanceActivity.this.m1757x2b883538(iRound, view, windowInsetsCompat);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$applyWindowInsets$31$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ WindowInsetsCompat m1757x2b883538(int i, View view, WindowInsetsCompat windowInsetsCompat) {
        int identifier;
        Insets insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars());
        int dimensionPixelSize = insets.top;
        if (dimensionPixelSize == 0 && (identifier = getResources().getIdentifier("status_bar_height", "dimen", "android")) > 0) {
            dimensionPixelSize = getResources().getDimensionPixelSize(identifier);
        }
        view.setPadding(i + insets.left, dimensionPixelSize, insets.right, insets.bottom);
        return windowInsetsCompat;
    }

    public void onButtonClickApply(View view) {
        if (!this.applying && saveCustomCommands()) {
            if (!GlobalVars.isBound || GlobalVars.serviceMessenger == null) {
                Log.w("$$$ Advance apply $$$", "SetModesService не забинден");
                return;
            }
            try {
                Message messageObtain = Message.obtain((Handler) null, 1);
                messageObtain.replyTo = this.applyClient;
                GlobalVars.serviceMessenger.send(messageObtain);
                setApplying(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setApplying(boolean z) {
        this.applying = z;
        Button button = this.buttonApplyAdvance;
        if (button != null) {
            button.setEnabled(!z);
        }
        ProgressBar progressBar = this.applyProgressAdvance;
        if (progressBar != null) {
            progressBar.setVisibility(z ? 0 : 8);
        }
        this.uiHandler.removeCallbacks(this.applyTimeout);
        if (z) {
            this.uiHandler.postDelayed(this.applyTimeout, 12000L);
        }
    }

    private void bindShowSwitch(int i, String str, boolean z) {
        bindShowSwitch(i, str, z, 0);
    }

    private void bindShowSwitch(int i, final String str, boolean z, int i2) {
        Switch r2 = (Switch) findViewById(i);
        if (r2 == null) {
            return;
        }
        final View viewFindViewById = i2 == 0 ? null : findViewById(i2);
        boolean z2 = this.prefs.getBoolean(str, z);
        r2.setChecked(z2);
        if (viewFindViewById != null) {
            viewFindViewById.setVisibility(z2 ? 0 : 8);
        }
        r2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda72
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z3) {
                AdvanceActivity.this.m1761lambda$bindShowSwitch$32$rubigtownrestoremodeAdvanceActivity(str, viewFindViewById, compoundButton, z3);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$bindShowSwitch$32$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1761lambda$bindShowSwitch$32$rubigtownrestoremodeAdvanceActivity(String str, View view, CompoundButton compoundButton, boolean z) {
        this.prefs.edit().putBoolean(str, z).apply();
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
        }
    }

    private void bindTileSizeSpinners(int i, int i2, final String str, final int i3, final int i4) {
        Spinner spinner = (Spinner) findViewById(i);
        Spinner spinner2 = (Spinner) findViewById(i2);
        if (spinner == null || spinner2 == null) {
            return;
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.spinner_item, new String[]{"1 ячейка", "2 ячейки", "3 ячейки", "4 ячейки", "5 ячеек", "6 ячеек", "7 ячеек", "8 ячеек", "9 ячеек", "10 ячеек", "11 ячеек", "12 ячеек"});
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter((SpinnerAdapter) arrayAdapter);
        spinner.setSelection(TileSizeStore.width(this.prefs, str, i3) - 1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: ru.big.town.restoremode.AdvanceActivity.12
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i5, long j) {
                int i6 = i5 + 1;
                if (TileSizeStore.width(AdvanceActivity.this.prefs, str, i3) != i6) {
                    TileSizeStore.setWidth(AdvanceActivity.this.prefs, str, i6);
                }
            }
        });
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, R.layout.spinner_item, new String[]{"1 ячейка", "2 ячейки", "3 ячейки", "4 ячейки", "5 ячеек"});
        arrayAdapter2.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner2.setAdapter((SpinnerAdapter) arrayAdapter2);
        spinner2.setSelection(TileSizeStore.height(this.prefs, str, i4) - 1);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: ru.big.town.restoremode.AdvanceActivity.13
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i5, long j) {
                int i6 = i5 + 1;
                if (TileSizeStore.height(AdvanceActivity.this.prefs, str, i4) != i6) {
                    TileSizeStore.setHeight(AdvanceActivity.this.prefs, str, i6);
                }
            }
        });
    }

    private void sendFloatingBack(boolean z) {
        if (!GlobalVars.isBound || GlobalVars.serviceMessenger == null) {
            Log.w("$$$ Advance floatBack $$$", "SetModesService не забинден");
            return;
        }
        try {
            GlobalVars.serviceMessenger.send(Message.obtain(null, 24, z ? 1 : 0, 0));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sendTheme(int i) {
        if (!GlobalVars.isBound || GlobalVars.serviceMessenger == null) {
            Log.w("$$$ Advance theme $$$", "SetModesService не забинден");
            return;
        }
        try {
            GlobalVars.serviceMessenger.send(Message.obtain(null, 28, i, 0));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    static String engineeringPassword(Calendar calendar) {
        String str = String.format(Locale.US, "%04d", Integer.valueOf(calendar.get(1)));
        String str2 = String.format(Locale.US, "%02d%02d", Integer.valueOf(calendar.get(2) + 1), Integer.valueOf(calendar.get(5)));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append((str.charAt(i) - '0') + (str2.charAt(i) - '0'));
        }
        return sb.toString();
    }

    private void showEngineeringPassword() {
        TextView textView = (TextView) findViewById(R.id.textEngPassword);
        TextView textView2 = (TextView) findViewById(R.id.textEngPasswordDate);
        if (textView == null) {
            return;
        }
        try {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
            textView.setText(engineeringPassword(calendar));
            if (textView2 != null) {
                textView2.setText(String.format(Locale.US, "дата расчёта: %02d.%02d.%04d по Пекину", Integer.valueOf(calendar.get(5)), Integer.valueOf(calendar.get(2) + 1), Integer.valueOf(calendar.get(1))));
            }
        } catch (Exception unused) {
            textView.setText("—");
            if (textView2 != null) {
                textView2.setText("не удалось определить дату машины");
            }
        }
    }

    private void sendFloatingBackSide(int i) {
        if (!GlobalVars.isBound || GlobalVars.serviceMessenger == null) {
            Log.w("$$$ Advance floatBack $$$", "SetModesService не забинден");
            return;
        }
        try {
            GlobalVars.serviceMessenger.send(Message.obtain(null, 25, i, 0));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void onButtonCloseAll(View view) {
        new MaterialAlertDialogBuilder(this, R.style.DarkDialog).setTitle((CharSequence) "Закрыть приложения").setMessage((CharSequence) "Все открытые сторонние приложения будут полностью закрыты и при следующем запуске откроются с нуля. Системные приложения не затрагиваются. Продолжить?").setPositiveButton((CharSequence) "Закрыть", new DialogInterface.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda8
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                AdvanceActivity.this.m1781xef0ea85a(dialogInterface, i);
            }
        }).setNegativeButton((CharSequence) "Отмена", (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: renamed from: lambda$onButtonCloseAll$33$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1781xef0ea85a(DialogInterface dialogInterface, int i) {
        boolean z;
        if (!GlobalVars.isBound || GlobalVars.serviceMessenger == null) {
            z = false;
        } else {
            try {
                GlobalVars.serviceMessenger.send(Message.obtain((Handler) null, 27));
                z = true;
            } catch (RemoteException e) {
                e.printStackTrace();
                z = false;
            }
        }
        Snackbar.make(findViewById(R.id.main), z ? "Приложения закрыты" : "Сервис не готов", 0).show();
        Log.i("$$$ Advance closeAll $$$", "MSG_CLOSE_ALL sent=" + z);
    }

    private void initDockOverride() {
        this.dockApp1Btn = (Button) findViewById(R.id.buttonDockApp1);
        this.dockApp2Btn = (Button) findViewById(R.id.buttonDockApp2);
        this.dockSplit1Btn = (Button) findViewById(R.id.buttonDockSplit1);
        this.dockSplit2Btn = (Button) findViewById(R.id.buttonDockSplit2);
        refreshDockButtons();
        Button button = this.dockApp1Btn;
        if (button != null) {
            button.setOnLongClickListener(new View.OnLongClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda37
                @Override // android.view.View.OnLongClickListener
                public final boolean onLongClick(View view) {
                    return AdvanceActivity.this.m1767x937fd586(view);
                }
            });
        }
        Button button2 = this.dockApp2Btn;
        if (button2 != null) {
            button2.setOnLongClickListener(new View.OnLongClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda38
                @Override // android.view.View.OnLongClickListener
                public final boolean onLongClick(View view) {
                    return AdvanceActivity.this.m1768x20200087(view);
                }
            });
        }
        pushDockConfig();
    }

    /* JADX INFO: renamed from: lambda$initDockOverride$34$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ boolean m1767x937fd586(View view) {
        clearDockApp(1);
        return true;
    }

    /* JADX INFO: renamed from: lambda$initDockOverride$35$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ boolean m1768x20200087(View view) {
        clearDockApp(2);
        return true;
    }

    public void onPickDockApp1(View view) {
        pickDockApp(1);
    }

    public void onPickDockApp2(View view) {
        pickDockApp(2);
    }

    public void onPickDockSplit1(View view) {
        pickDockLongPress(1);
    }

    public void onPickDockSplit2(View view) {
        pickDockLongPress(2);
    }

    private void pickDockLongPress(final int i) {
        new MaterialAlertDialogBuilder(this, R.style.DarkDialog).setTitle((CharSequence) ("Долгое нажатие · приложение " + i)).setItems((CharSequence[]) new String[]{"Открыть сплит", "Открыть в медиакарточке приборной панели", "Не назначено"}, new DialogInterface.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda6
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                AdvanceActivity.this.m1807x2e9fccde(i, dialogInterface, i2);
            }
        }).setNegativeButton((CharSequence) "Отмена", (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: renamed from: lambda$pickDockLongPress$36$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1807x2e9fccde(int i, DialogInterface dialogInterface, int i2) {
        if (i2 == 0) {
            pickDockSplit(i);
            return;
        }
        this.prefs.edit().putString("dockOverride" + i + "LongAction", i2 == 1 ? "cluster" : "none").apply();
        refreshDockButtons();
        pushDockConfig();
    }

    private void pickDockApp(final int i) {
        showAppPicker("Приложение " + i + " в доке", new AppPicked() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda1
            @Override // ru.big.town.restoremode.AdvanceActivity.AppPicked
            public final void onPicked(String str, String str2) {
                AdvanceActivity.this.m1806lambda$pickDockApp$37$rubigtownrestoremodeAdvanceActivity(i, str, str2);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$pickDockApp$37$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1806lambda$pickDockApp$37$rubigtownrestoremodeAdvanceActivity(int i, String str, String str2) {
        this.prefs.edit().putString("dockOverride" + i, str).putString("dockOverride" + i + "Label", str2).apply();
        refreshDockButtons();
        pushDockConfig();
    }

    private void pickDockSplit(final int i) {
        List<SplitStore.Preset> listLoad = SplitStore.load(this.prefs);
        final ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        arrayList2.add("Нет (только открыть приложение)");
        for (int i2 = 0; i2 < listLoad.size(); i2++) {
            SplitStore.Preset preset = listLoad.get(i2);
            if (preset.ready()) {
                arrayList.add(Integer.valueOf(i2));
                arrayList2.add((preset.ll.isEmpty() ? preset.l : preset.ll) + "  /  " + (preset.rl.isEmpty() ? preset.r : preset.rl));
            }
        }
        new MaterialAlertDialogBuilder(this, R.style.DarkDialog).setTitle((CharSequence) ("Сплит по долгому нажатию (слот " + i + ")")).setItems((CharSequence[]) arrayList2.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda7
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i3) {
                AdvanceActivity.this.m1808lambda$pickDockSplit$38$rubigtownrestoremodeAdvanceActivity(i, arrayList, arrayList2, dialogInterface, i3);
            }
        }).setNegativeButton((CharSequence) "Отмена", (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: renamed from: lambda$pickDockSplit$38$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1808lambda$pickDockSplit$38$rubigtownrestoremodeAdvanceActivity(int i, List list, List list2, DialogInterface dialogInterface, int i2) {
        if (i2 == 0) {
            this.prefs.edit().putString("dockOverride" + i + "LongAction", "none").remove("dockOverride" + i + "Split").remove("dockOverride" + i + "SplitLabel").apply();
        } else {
            this.prefs.edit().putString("dockOverride" + i + "LongAction", TileOrderStore.Tile.TYPE_SPLIT).putInt("dockOverride" + i + "Split", ((Integer) list.get(i2 - 1)).intValue()).putString("dockOverride" + i + "SplitLabel", ((CharSequence) list2.get(i2)).toString()).apply();
        }
        refreshDockButtons();
        pushDockConfig();
    }

    private void clearDockApp(int i) {
        this.prefs.edit().remove("dockOverride" + i).remove("dockOverride" + i + "Label").remove("dockOverride" + i + "LongAction").remove("dockOverride" + i + "Split").remove("dockOverride" + i + "SplitLabel").apply();
        refreshDockButtons();
        pushDockConfig();
        Snackbar.make(findViewById(R.id.main), "Слот " + i + " сброшен", -1).show();
    }

    private void refreshDockButtons() {
        setDockButtonText(this.dockApp1Btn, 1);
        setDockButtonText(this.dockApp2Btn, 2);
        setDockSplitButton(this.dockSplit1Btn, 1);
        setDockSplitButton(this.dockSplit2Btn, 2);
    }

    private void setDockButtonText(Button button, int i) {
        if (button == null) {
            return;
        }
        String string = this.prefs.getString("dockOverride" + i + "Label", "");
        StringBuilder sbAppend = new StringBuilder("Приложение ").append(i).append(": ");
        if (string.isEmpty()) {
            string = "не выбрано";
        }
        button.setText(sbAppend.append(string).toString());
    }

    private void setDockSplitButton(Button button, int i) {
        String str;
        if (button == null) {
            return;
        }
        button.setVisibility(!this.prefs.getString(new StringBuilder("dockOverride").append(i).toString(), "").isEmpty() ? 0 : 8);
        String strResolve = DockLongPressAction.resolve(this.prefs, i);
        if ("cluster".equals(strResolve)) {
            str = "медиакарточка приборной панели";
        } else if (!TileOrderStore.Tile.TYPE_SPLIT.equals(strResolve)) {
            str = "не назначено";
        } else {
            str = "сплит · " + this.prefs.getString("dockOverride" + i + "SplitLabel", "не выбран");
        }
        button.setText("Долгое нажатие: " + str);
    }

    private void showAppPicker(String str, final AppPicked appPicked) {
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> listQueryIntentActivities = packageManager.queryIntentActivities(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER"), 0);
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (ResolveInfo resolveInfo : listQueryIntentActivities) {
            String str2 = resolveInfo.activityInfo.packageName;
            if (!linkedHashMap.containsKey(str2)) {
                linkedHashMap.put(str2, resolveInfo.loadLabel(packageManager).toString());
            }
        }
        final ArrayList arrayList = new ArrayList(linkedHashMap.keySet());
        Collections.sort(arrayList, new Comparator() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda46
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return AdvanceActivity.this.m1832lambda$showAppPicker$39$rubigtownrestoremodeAdvanceActivity(linkedHashMap, (String) obj, (String) obj2);
            }
        });
        CharSequence[] charSequenceArr = new CharSequence[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            charSequenceArr[i] = ((String) linkedHashMap.get(arrayList.get(i))) + "  ·  " + ((String) arrayList.get(i));
        }
        new MaterialAlertDialogBuilder(this, R.style.DarkDialog).setTitle((CharSequence) str).setItems(charSequenceArr, new DialogInterface.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda47
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                AdvanceActivity.AppPicked appPicked2 = appPicked;
                List list = arrayList;
                appPicked2.onPicked((String) list.get(i2), (String) linkedHashMap.get(list.get(i2)));
            }
        }).setNegativeButton((CharSequence) "Отмена", (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: renamed from: lambda$showAppPicker$39$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ int m1832lambda$showAppPicker$39$rubigtownrestoremodeAdvanceActivity(LinkedHashMap linkedHashMap, String str, String str2) {
        boolean zIsSystemApp = isSystemApp(str);
        boolean zIsSystemApp2 = isSystemApp(str2);
        if (zIsSystemApp && !zIsSystemApp2) {
            return 1;
        }
        if (zIsSystemApp || !zIsSystemApp2) {
            return ((String) linkedHashMap.get(str)).compareToIgnoreCase((String) linkedHashMap.get(str2));
        }
        return -1;
    }

    private boolean isSystemApp(String str) {
        return str.startsWith("com.qinggan") || str.startsWith("com.bz") || str.startsWith("com.android") || str.startsWith("com.tencent") || str.startsWith("com.huawei") || str.startsWith("com.mega") || str.startsWith("com.thunder") || str.startsWith("com.pateo") || str.startsWith("com.baidu") || str.startsWith("com.richauto");
    }

    public void onButtonGrantInstall(View view) {
        showAppPicker("Выдать права на установку", new AppPicked() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda62
            @Override // ru.big.town.restoremode.AdvanceActivity.AppPicked
            public final void onPicked(String str, String str2) {
                AdvanceActivity.this.m1782x1e96a061(str, str2);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$onButtonGrantInstall$41$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1782x1e96a061(String str, String str2) {
        sendGrantInstall(str);
        Snackbar.make(findViewById(R.id.main), "Право на установку выдано: " + str2, 0).show();
    }

    private void initAppShortcuts() {
        this.appShortcutsContainer = (LinearLayout) findViewById(R.id.appShortcutsContainer);
        renderAppShortcuts();
    }

    public void onAddAppShortcut(View view) {
        showAppPicker("Добавить приложение", new AppPicked() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda60
            @Override // ru.big.town.restoremode.AdvanceActivity.AppPicked
            public final void onPicked(String str, String str2) {
                AdvanceActivity.this.m1778x9324514d(str, str2);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$onAddAppShortcut$42$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1778x9324514d(String str, String str2) {
        List<String> listLoad = AppShortcutStore.load(this.prefs);
        if (listLoad.contains(str)) {
            return;
        }
        listLoad.add(str);
        AppShortcutStore.save(this.prefs, listLoad);
        TileOrderStore.sync(this.prefs, getPackageManager());
        renderAppShortcuts();
    }

    private void renderAppShortcuts() {
        String string;
        LinearLayout linearLayout = this.appShortcutsContainer;
        if (linearLayout == null) {
            return;
        }
        linearLayout.removeAllViews();
        List<String> listLoad = AppShortcutStore.load(this.prefs);
        PackageManager packageManager = getPackageManager();
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(this);
        for (int i = 0; i < listLoad.size(); i++) {
            final String str = listLoad.get(i);
            View viewInflate = layoutInflaterFrom.inflate(R.layout.item_app_shortcut, (ViewGroup) this.appShortcutsContainer, false);
            ImageView imageView = (ImageView) viewInflate.findViewById(R.id.shortcutIco);
            TextView textView = (TextView) viewInflate.findViewById(R.id.shortcutLabel);
            ImageButton imageButton = (ImageButton) viewInflate.findViewById(R.id.shortcutDelete);
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
                string = packageManager.getApplicationLabel(applicationInfo).toString();
                try {
                    imageView.setImageDrawable(packageManager.getApplicationIcon(applicationInfo));
                } catch (Exception unused) {
                }
            } catch (Exception unused2) {
                string = str;
            }
            textView.setText(string);
            imageButton.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda11
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AdvanceActivity.this.m1813xc16514d1(str, view);
                }
            });
            this.appShortcutsContainer.addView(viewInflate);
        }
    }

    /* JADX INFO: renamed from: lambda$renderAppShortcuts$43$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1813xc16514d1(String str, View view) {
        List<String> listLoad = AppShortcutStore.load(this.prefs);
        listLoad.remove(str);
        AppShortcutStore.save(this.prefs, listLoad);
        TileOrderStore.sync(this.prefs, getPackageManager());
        renderAppShortcuts();
    }

    private void initAppWidgets() {
        this.appWidgetsContainer = (LinearLayout) findViewById(R.id.appWidgetsContainer);
        renderAppWidgets();
    }

    public void onAddAppWidget(View view) {
        if (AppWidgetStore.load(this.prefs).size() >= 20) {
            Snackbar.make(findViewById(R.id.main), "Можно создать не более 20 виджетов", 0).show();
        } else {
            showAppPicker("Приложение для виджета", new AppPicked() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda39
                @Override // ru.big.town.restoremode.AdvanceActivity.AppPicked
                public final void onPicked(String str, String str2) {
                    AdvanceActivity.this.m1779lambda$onAddAppWidget$44$rubigtownrestoremodeAdvanceActivity(str, str2);
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$onAddAppWidget$44$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1779lambda$onAddAppWidget$44$rubigtownrestoremodeAdvanceActivity(String str, String str2) {
        AppWidgetStore.add(this.prefs, str);
        TileOrderStore.sync(this.prefs, getPackageManager());
        renderAppWidgets();
    }

    private void renderAppWidgets() {
        LinearLayout linearLayout = this.appWidgetsContainer;
        if (linearLayout == null) {
            return;
        }
        linearLayout.removeAllViews();
        PackageManager packageManager = getPackageManager();
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(this);
        for (final AppWidgetStore.Entry entry : AppWidgetStore.load(this.prefs)) {
            View viewInflate = layoutInflaterFrom.inflate(R.layout.item_app_widget_setting, (ViewGroup) this.appWidgetsContainer, false);
            ImageView imageView = (ImageView) viewInflate.findViewById(R.id.appWidgetSettingIcon);
            TextView textView = (TextView) viewInflate.findViewById(R.id.appWidgetSettingLabel);
            ImageButton imageButton = (ImageButton) viewInflate.findViewById(R.id.appWidgetSettingDelete);
            Spinner spinner = (Spinner) viewInflate.findViewById(R.id.appWidgetSettingWidth);
            Spinner spinner2 = (Spinner) viewInflate.findViewById(R.id.appWidgetSettingHeight);
            Switch r12 = (Switch) viewInflate.findViewById(R.id.appWidgetSettingAutoStart);
            LinearLayout linearLayout2 = (LinearLayout) viewInflate.findViewById(R.id.appWidgetProfiles);
            Button button = (Button) viewInflate.findViewById(R.id.appWidgetAddProfile);
            String string = entry.packageName;
            LayoutInflater layoutInflater = layoutInflaterFrom;
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(entry.packageName, 0);
                string = packageManager.getApplicationLabel(applicationInfo).toString();
                imageView.setImageDrawable(packageManager.getApplicationIcon(applicationInfo));
            } catch (Exception unused) {
            }
            textView.setText("Виджет " + AppWidgetStore.designation(this.prefs, entry.id) + ": " + string);
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.spinner_item, new String[]{"1 ячейка", "2 ячейки", "3 ячейки", "4 ячейки", "5 ячеек", "6 ячеек", "7 ячеек", "8 ячеек", "9 ячеек", "10 ячеек", "11 ячеек", "12 ячеек"});
            arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinner.setAdapter((SpinnerAdapter) arrayAdapter);
            spinner.setSelection(AppWidgetStore.clampWidth(entry.width) - 1);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: ru.big.town.restoremode.AdvanceActivity.14
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                    int i2 = i + 1;
                    if (entry.width != i2) {
                        entry.width = i2;
                        AppWidgetStore.update(AdvanceActivity.this.prefs, entry);
                        TileOrderStore.sync(AdvanceActivity.this.prefs, AdvanceActivity.this.getPackageManager());
                    }
                }
            });
            ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, R.layout.spinner_item, new String[]{"1 ячейка", "2 ячейки", "3 ячейки", "4 ячейки", "5 ячеек"});
            arrayAdapter2.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinner2.setAdapter((SpinnerAdapter) arrayAdapter2);
            spinner2.setSelection(AppWidgetStore.clampHeight(entry.height) - 1);
            spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: ru.big.town.restoremode.AdvanceActivity.15
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                    int i2 = i + 1;
                    if (entry.height != i2) {
                        entry.height = i2;
                        AppWidgetStore.update(AdvanceActivity.this.prefs, entry);
                        TileOrderStore.sync(AdvanceActivity.this.prefs, AdvanceActivity.this.getPackageManager());
                    }
                }
            });
            r12.setChecked(entry.autoStart);
            final View viewFindViewById = viewInflate.findViewById(R.id.appWidgetDelayContainer);
            SeekBar seekBar = (SeekBar) viewInflate.findViewById(R.id.appWidgetSettingDelay);
            final TextView textView2 = (TextView) viewInflate.findViewById(R.id.appWidgetSettingDelayText);
            viewFindViewById.setVisibility(entry.autoStart ? 0 : 8);
            seekBar.setProgress(entry.autoStartDelay - 1);
            textView2.setText(entry.autoStartDelay + " сек");
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity.16
                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStartTrackingTouch(SeekBar seekBar2) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onStopTrackingTouch(SeekBar seekBar2) {
                }

                @Override // android.widget.SeekBar.OnSeekBarChangeListener
                public void onProgressChanged(SeekBar seekBar2, int i, boolean z) {
                    int i2 = i + 1;
                    textView2.setText(i2 + " сек");
                    if (z) {
                        entry.autoStartDelay = i2;
                        AppWidgetStore.update(AdvanceActivity.this.prefs, entry);
                    }
                }
            });
            r12.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda49
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    AdvanceActivity.this.m1815xdf04d751(entry, viewFindViewById, compoundButton, z);
                }
            });
            entry.ensureProfiles();
            renderAppWidgetProfiles(linearLayout2, entry);
            button.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda50
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AdvanceActivity.this.m1817xf8452d53(entry, view);
                }
            });
            imageButton.setContentDescription("Убрать виджет приложения");
            imageButton.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda51
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AdvanceActivity.this.m1818x84e55854(entry, view);
                }
            });
            this.appWidgetsContainer.addView(viewInflate);
            layoutInflaterFrom = layoutInflater;
        }
    }

    /* JADX INFO: renamed from: lambda$renderAppWidgets$45$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1815xdf04d751(AppWidgetStore.Entry entry, View view, CompoundButton compoundButton, boolean z) {
        entry.autoStart = z;
        view.setVisibility(z ? 0 : 8);
        AppWidgetStore.update(this.prefs, entry);
        TileOrderStore.sync(this.prefs, getPackageManager());
    }

    /* JADX INFO: renamed from: lambda$renderAppWidgets$47$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1817xf8452d53(final AppWidgetStore.Entry entry, View view) {
        showAppPicker("Добавить приложение в виджет", new AppPicked() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda33
            @Override // ru.big.town.restoremode.AdvanceActivity.AppPicked
            public final void onPicked(String str, String str2) {
                AdvanceActivity.this.m1816x6ba50252(entry, str, str2);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$renderAppWidgets$46$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1816x6ba50252(AppWidgetStore.Entry entry, String str, String str2) {
        AppWidgetStore.addProfile(entry, str, 0);
        AppWidgetStore.update(this.prefs, entry);
        TileOrderStore.sync(this.prefs, getPackageManager());
        renderAppWidgets();
    }

    /* JADX INFO: renamed from: lambda$renderAppWidgets$48$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1818x84e55854(AppWidgetStore.Entry entry, View view) {
        AppWidgetStore.remove(this.prefs, entry.id);
        TileOrderStore.sync(this.prefs, getPackageManager());
        renderAppWidgets();
    }

    private void renderAppWidgetProfiles(LinearLayout linearLayout, final AppWidgetStore.Entry entry) {
        linearLayout.removeAllViews();
        PackageManager packageManager = getPackageManager();
        for (int iIdx = 0; iIdx < entry.profiles.size(); iIdx++) {
            final int i = iIdx;
            final AppWidgetStore.Profile profile = entry.profiles.get(i);
            View viewInflate = LayoutInflater.from(this).inflate(R.layout.item_app_widget_profile, (ViewGroup) linearLayout, false);
            ImageView imageView = (ImageView) viewInflate.findViewById(R.id.appWidgetProfileIcon);
            TextView textView = (TextView) viewInflate.findViewById(R.id.appWidgetProfileLabel);
            Spinner spinner = (Spinner) viewInflate.findViewById(R.id.appWidgetProfileDpi);
            ImageButton imageButton = (ImageButton) viewInflate.findViewById(R.id.appWidgetProfileDelete);
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(profile.packageName, 0);
                imageView.setImageDrawable(packageManager.getApplicationIcon(applicationInfo));
                textView.setText(packageManager.getApplicationLabel(applicationInfo));
            } catch (Exception unused) {
                textView.setText(profile.packageName);
            }
            String[] strArr = new String[AppWidgetStore.DPI_VALUES.length];
            int i2 = 0;
            for (int i3 = 0; i3 < AppWidgetStore.DPI_VALUES.length; i3++) {
                int i4 = AppWidgetStore.DPI_VALUES[i3];
                strArr[i3] = i4 == 0 ? "Авто" : String.valueOf(i4);
                if (i4 == AppWidgetStore.normalizeDpi(profile.dpi)) {
                    i2 = i3;
                }
            }
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.spinner_item, strArr);
            arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinner.setAdapter((SpinnerAdapter) arrayAdapter);
            spinner.setSelection(i2);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: ru.big.town.restoremode.AdvanceActivity.17
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> adapterView, View view, int i5, long j) {
                    int i6 = AppWidgetStore.DPI_VALUES[i5];
                    if (profile.dpi != i6) {
                        profile.dpi = i6;
                        AppWidgetStore.update(AdvanceActivity.this.prefs, entry);
                        TileOrderStore.sync(AdvanceActivity.this.prefs, AdvanceActivity.this.getPackageManager());
                    }
                }
            });
            imageButton.setVisibility(entry.profiles.size() > 1 ? 0 : 8);
            imageButton.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda77
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AdvanceActivity.this.m1814x5ef4653a(entry, i, view);
                }
            });
            linearLayout.addView(viewInflate);
        }
    }

    /* JADX INFO: renamed from: lambda$renderAppWidgetProfiles$49$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1814x5ef4653a(AppWidgetStore.Entry entry, int i, View view) {
        AppWidgetStore.removeProfile(entry, i);
        AppWidgetStore.update(this.prefs, entry);
        TileOrderStore.sync(this.prefs, getPackageManager());
        renderAppWidgets();
    }

    private void initFullscreenApps() {
        this.fullscreenAppsContainer = (LinearLayout) findViewById(R.id.fullscreenAppsContainer);
        renderFullscreenApps();
    }

    public void onAddFullscreenApp(View view) {
        showAppPicker("Добавить полноэкранное приложение", new AppPicked() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda67
            @Override // ru.big.town.restoremode.AdvanceActivity.AppPicked
            public final void onPicked(String str, String str2) {
                AdvanceActivity.this.m1780x34bf4eeb(str, str2);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$onAddFullscreenApp$50$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1780x34bf4eeb(String str, String str2) {
        if (str.startsWith("ru.big.town")) {
            Snackbar.make(findViewById(R.id.main), "Экраны VoyahTune используют собственную системную раскладку", 0).show();
            return;
        }
        List<String> listLoad = FullscreenAppStore.load(this.prefs);
        if (listLoad.contains(str)) {
            return;
        }
        listLoad.add(str);
        saveFullscreenApps(listLoad);
    }

    private void saveFullscreenApps(List<String> list) {
        FullscreenAppStore.save(this.prefs, list);
        SplitConfigSync.pushFullscreenApps(this, this.prefs);
        renderFullscreenApps();
    }

    private void renderFullscreenApps() {
        String string;
        LinearLayout linearLayout = this.fullscreenAppsContainer;
        if (linearLayout == null) {
            return;
        }
        linearLayout.removeAllViews();
        List<String> listLoad = FullscreenAppStore.load(this.prefs);
        PackageManager packageManager = getPackageManager();
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(this);
        for (final String str : listLoad) {
            View viewInflate = layoutInflaterFrom.inflate(R.layout.item_app_shortcut, (ViewGroup) this.fullscreenAppsContainer, false);
            ImageView imageView = (ImageView) viewInflate.findViewById(R.id.shortcutIco);
            TextView textView = (TextView) viewInflate.findViewById(R.id.shortcutLabel);
            ImageButton imageButton = (ImageButton) viewInflate.findViewById(R.id.shortcutDelete);
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
                string = packageManager.getApplicationLabel(applicationInfo).toString();
                try {
                    imageView.setImageDrawable(packageManager.getApplicationIcon(applicationInfo));
                } catch (Exception unused) {
                }
            } catch (Exception unused2) {
                string = str;
            }
            textView.setText(string);
            imageButton.setContentDescription("Убрать из полноэкранных приложений");
            imageButton.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda84
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AdvanceActivity.this.m1821xb264590d(str, view);
                }
            });
            this.fullscreenAppsContainer.addView(viewInflate);
        }
    }

    /* JADX INFO: renamed from: lambda$renderFullscreenApps$51$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1821xb264590d(String str, View view) {
        List<String> listLoad = FullscreenAppStore.load(this.prefs);
        listLoad.remove(str);
        saveFullscreenApps(listLoad);
    }

    private void initSplitScreen() {
        this.splitPresetsContainer = (LinearLayout) findViewById(R.id.splitPresetsContainer);
        renderSplitPresets();
    }

    public void onAddSplitPreset(View view) {
        List<SplitStore.Preset> listLoad = SplitStore.load(this.prefs);
        listLoad.add(new SplitStore.Preset());
        saveSplitPresets(listLoad);
        renderSplitPresets();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveSplitPresets(List<SplitStore.Preset> list) {
        SplitStore.save(this.prefs, list);
        TileOrderStore.sync(this.prefs, getPackageManager());
        SplitConfigSync.pushAll(this, this.prefs);
    }

    private void renderSplitPresets() {
        LinearLayout linearLayout = this.splitPresetsContainer;
        if (linearLayout == null) {
            return;
        }
        linearLayout.removeAllViews();
        List<SplitStore.Preset> listLoad = SplitStore.load(this.prefs);
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(this);
        for (int iIdx = 0; iIdx < listLoad.size(); iIdx++) {
            final int i = iIdx;
            SplitStore.Preset preset = listLoad.get(i);
            View viewInflate = layoutInflaterFrom.inflate(R.layout.item_split_preset, (ViewGroup) this.splitPresetsContainer, false);
            Button button = (Button) viewInflate.findViewById(R.id.splitLeftBtn);
            Button button2 = (Button) viewInflate.findViewById(R.id.splitRightBtn);
            Button button3 = (Button) viewInflate.findViewById(R.id.splitDeleteBtn);
            Spinner spinner = (Spinner) viewInflate.findViewById(R.id.splitRatioSpinner);
            button.setText("Слева: " + (preset.ll.isEmpty() ? "не выбрано" : preset.ll));
            button2.setText("Справа: " + (preset.rl.isEmpty() ? "не выбрано" : preset.rl));
            button.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda54
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AdvanceActivity.this.m1823xb0af1c02(i, view);
                }
            });
            button2.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda56
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AdvanceActivity.this.m1825xc9ef7204(i, view);
                }
            });
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.spinner_ratio_item, SplitStore.RATIO_LABELS);
            arrayAdapter.setDropDownViewResource(R.layout.spinner_ratio_dropdown);
            spinner.setAdapter((SpinnerAdapter) arrayAdapter);
            spinner.setSelection(preset.ratio, false);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: ru.big.town.restoremode.AdvanceActivity.18
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> adapterView, View view, int i2, long j) {
                    List<SplitStore.Preset> listLoad2 = SplitStore.load(AdvanceActivity.this.prefs);
                    if (i >= listLoad2.size() || listLoad2.get(i).ratio == i2) {
                        return;
                    }
                    listLoad2.get(i).ratio = i2;
                    listLoad2.get(i).split = 0.0f;
                    AdvanceActivity.this.saveSplitPresets(listLoad2);
                }
            });
            Switch r6 = (Switch) viewInflate.findViewById(R.id.splitResizableSwitch);
            if (r6 != null) {
                r6.setChecked(preset.resizable);
                r6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda57
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        AdvanceActivity.this.m1826x568f9d05(i, compoundButton, z);
                    }
                });
            }
            button3.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda58
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AdvanceActivity.this.m1827xe32fc806(i, view);
                }
            });
            this.splitPresetsContainer.addView(viewInflate);
        }
    }

    /* JADX INFO: renamed from: lambda$renderSplitPresets$53$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1823xb0af1c02(final int i, View view) {
        showAppPicker("Приложение слева", new AppPicked() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda83
            @Override // ru.big.town.restoremode.AdvanceActivity.AppPicked
            public final void onPicked(String str, String str2) {
                AdvanceActivity.this.m1822x240ef101(i, str, str2);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$renderSplitPresets$52$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1822x240ef101(int i, String str, String str2) {
        List<SplitStore.Preset> listLoad = SplitStore.load(this.prefs);
        if (i < listLoad.size()) {
            listLoad.get(i).l = str;
            listLoad.get(i).ll = str2;
            saveSplitPresets(listLoad);
            renderSplitPresets();
        }
    }

    /* JADX INFO: renamed from: lambda$renderSplitPresets$55$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1825xc9ef7204(final int i, View view) {
        showAppPicker("Приложение справа", new AppPicked() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda70
            @Override // ru.big.town.restoremode.AdvanceActivity.AppPicked
            public final void onPicked(String str, String str2) {
                AdvanceActivity.this.m1824x3d4f4703(i, str, str2);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$renderSplitPresets$54$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1824x3d4f4703(int i, String str, String str2) {
        List<SplitStore.Preset> listLoad = SplitStore.load(this.prefs);
        if (i < listLoad.size()) {
            listLoad.get(i).r = str;
            listLoad.get(i).rl = str2;
            saveSplitPresets(listLoad);
            renderSplitPresets();
        }
    }

    /* JADX INFO: renamed from: lambda$renderSplitPresets$56$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1826x568f9d05(int i, CompoundButton compoundButton, boolean z) {
        List<SplitStore.Preset> listLoad = SplitStore.load(this.prefs);
        if (i < listLoad.size()) {
            listLoad.get(i).resizable = z;
            if (!z) {
                listLoad.get(i).split = 0.0f;
            }
            saveSplitPresets(listLoad);
        }
    }

    /* JADX INFO: renamed from: lambda$renderSplitPresets$57$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1827xe32fc806(int i, View view) {
        List<SplitStore.Preset> listLoad = SplitStore.load(this.prefs);
        if (i < listLoad.size()) {
            listLoad.remove(i);
            saveSplitPresets(listLoad);
            renderSplitPresets();
        }
    }

    private int dpiIndex(int i) {
        int i2 = 0;
        while (true) {
            int[] iArr = DPI_VALUES;
            if (i2 >= iArr.length) {
                return 0;
            }
            if (iArr[i2] == i) {
                return i2;
            }
            i2++;
        }
    }

    private void initAppDpiList() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.appDpiContainer);
        if (linearLayout == null) {
            return;
        }
        linearLayout.removeAllViews();
        PackageManager packageManager = getPackageManager();
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(this);
        Intent intentAddCategory = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER");
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(intentAddCategory, 0)) {
            String str = resolveInfo.activityInfo.packageName;
            if (!linkedHashMap.containsKey(str)) {
                try {
                    if ((packageManager.getApplicationInfo(str, 0).flags & 1) == 0) {
                        linkedHashMap.put(str, resolveInfo.loadLabel(packageManager).toString());
                    }
                } catch (Exception unused) {
                }
            }
        }
        ArrayList<String> arrayList = new ArrayList(linkedHashMap.keySet());
        Collections.sort(arrayList, new Comparator() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda3
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                LinkedHashMap linkedHashMap2 = linkedHashMap;
                return ((String) linkedHashMap2.get((String) obj)).compareToIgnoreCase((String) linkedHashMap2.get((String) obj2));
            }
        });
        for (final String fpkg : arrayList) {
            View viewInflate = layoutInflaterFrom.inflate(R.layout.item_app_dpi, (ViewGroup) linearLayout, false);
            ImageView imageView = (ImageView) viewInflate.findViewById(R.id.appDpiIco);
            TextView textView = (TextView) viewInflate.findViewById(R.id.appDpiLabel);
            Spinner spinner = (Spinner) viewInflate.findViewById(R.id.appDpiSpinner);
            try {
                imageView.setImageDrawable(packageManager.getApplicationIcon(fpkg));
            } catch (Exception unused2) {
            }
            textView.setText((CharSequence) linkedHashMap.get(fpkg));
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.spinner_ratio_item, DPI_LABELS);
            arrayAdapter.setDropDownViewResource(R.layout.spinner_ratio_dropdown);
            spinner.setAdapter((SpinnerAdapter) arrayAdapter);
            spinner.setSelection(dpiIndex(AppDpiStore.get(this.prefs, fpkg)), false);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: ru.big.town.restoremode.AdvanceActivity.19
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                    int dpi = AdvanceActivity.DPI_VALUES[i];
                    if (dpi != AppDpiStore.get(AdvanceActivity.this.prefs, fpkg)) {
                        AppDpiStore.set(AdvanceActivity.this.prefs, fpkg, dpi);
                        SplitConfigSync.pushAppDpi(AdvanceActivity.this, prefs, fpkg, dpi);
                    }
                }
            });
            linearLayout.addView(viewInflate);
        }
    }

    private void sendGrantInstall(String str) {
        if (!GlobalVars.isBound || GlobalVars.serviceMessenger == null) {
            Log.w("$$$ Advance grantInstall $$$", "SetModesService не забинден");
            return;
        }
        try {
            int i = getPackageManager().getApplicationInfo(str, 0).uid;
            try {
                Message messageObtain = Message.obtain(null, 26, i, 0);
                Bundle bundle = new Bundle();
                bundle.putString("pkg", str);
                messageObtain.setData(bundle);
                GlobalVars.serviceMessenger.send(messageObtain);
                Log.i("$$$ Advance grantInstall $$$", "MSG_GRANT_INSTALL pkg=" + str + " uid=" + i);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } catch (Exception unused) {
            Log.w("$$$ Advance grantInstall $$$", "не найден uid для " + str);
        }
    }

    public void onButtonRebootSystem(View view) {
        new MaterialAlertDialogBuilder(this, R.style.DarkDialog).setTitle((CharSequence) "Перезагрузка системы").setMessage((CharSequence) "Система (голова) будет перезагружена. Несохранённые действия могут прерваться. Продолжить?").setPositiveButton((CharSequence) "Перезагрузить", new DialogInterface.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                AdvanceActivity.lambda$onButtonRebootSystem$59(dialogInterface, i);
            }
        }).setNegativeButton((CharSequence) "Отмена", (DialogInterface.OnClickListener) null).show();
    }

    static /* synthetic */ void lambda$onButtonRebootSystem$59(DialogInterface dialogInterface, int i) {
        if (GlobalVars.isBound && GlobalVars.serviceMessenger != null) {
            try {
                GlobalVars.serviceMessenger.send(Message.obtain((Handler) null, 22));
                Log.i("$$$ Advance reboot $$$", "MSG_REBOOT sent");
                return;
            } catch (RemoteException e) {
                e.printStackTrace();
                return;
            }
        }
        Log.w("$$$ Advance reboot $$$", "SetModesService не забинден");
    }

    public void onButtonLogging(View view) {
        startActivity(new Intent(this, (Class<?>) LoggingActivity.class));
    }

    private void setSection(int i) {
        Switch r0;
        VoiceSettingsPage voiceSettingsPage;
        this.currentSection = i;
        TextView textView = this.sectionTitle;
        if (textView != null && i >= 0) {
            String[] strArr = SECTION_TITLES;
            if (i < strArr.length) {
                textView.setText(strArr[i]);
            }
        }
        View view = this.pageMainScreen;
        int i2 = 8;
        if (view != null) {
            view.setVisibility(i == 0 ? 0 : 8);
        }
        View view2 = this.pageDriveModes;
        if (view2 != null) {
            view2.setVisibility(i == 1 ? 0 : 8);
        }
        View view3 = this.pageSplitScreen;
        if (view3 != null) {
            view3.setVisibility(i == 2 ? 0 : 8);
        }
        View view4 = this.pageApolloTech;
        if (view4 != null) {
            view4.setVisibility(i == 3 ? 0 : 8);
        }
        View view5 = this.pageCustomCommands;
        if (view5 != null) {
            view5.setVisibility(i == 4 ? 0 : 8);
        }
        View view6 = this.pageSteeringButtons;
        if (view6 != null) {
            view6.setVisibility(i == 5 ? 0 : 8);
        }
        View view7 = this.pageOther;
        if (view7 != null) {
            view7.setVisibility(i == 6 ? 0 : 8);
        }
        View view8 = this.pageVoiceControl;
        if (view8 != null) {
            view8.setVisibility(i == 7 ? 0 : 8);
        }
        TextView textView2 = this.navMainScreen;
        if (textView2 != null) {
            textView2.setSelected(i == 0);
        }
        TextView textView3 = this.navDriveModes;
        if (textView3 != null) {
            textView3.setSelected(i == 1);
        }
        TextView textView4 = this.navSplitScreen;
        if (textView4 != null) {
            textView4.setSelected(i == 2);
        }
        TextView textView5 = this.navApolloTech;
        if (textView5 != null) {
            textView5.setSelected(i == 3);
        }
        TextView textView6 = this.navCustomCommands;
        if (textView6 != null) {
            textView6.setSelected(i == 4);
        }
        TextView textView7 = this.navSteeringButtons;
        if (textView7 != null) {
            textView7.setSelected(i == 5);
        }
        TextView textView8 = this.navOther;
        if (textView8 != null) {
            textView8.setSelected(i == 6);
        }
        TextView textView9 = this.navVoiceControl;
        if (textView9 != null) {
            textView9.setSelected(i == 7);
        }
        if (i == 7 && (voiceSettingsPage = this.voiceSettings) != null) {
            voiceSettingsPage.refresh();
        }
        if (i == 0 && (r0 = (Switch) findViewById(R.id.switchShowVoiceCommand)) != null) {
            r0.setChecked(this.prefs.getBoolean("showVoiceCommand", false));
        }
        Button button = this.buttonApplyAdvance;
        if (button != null) {
            button.setVisibility(i == 7 ? 8 : 0);
        }
        ProgressBar progressBar = this.applyProgressAdvance;
        if (progressBar != null) {
            if (this.applying && i != 7) {
                i2 = 0;
            }
            progressBar.setVisibility(i2);
        }
        updateSystemMetricsPolling();
        updateLightDiagnosticsBinding();
    }

    private void updateLightDiagnosticsBinding() {
        Sensor defaultSensor;
        boolean z = this.activityResumed && this.currentSection == 6 && this.prefs.getBoolean("debugMode", false) && !isFinishing();
        if (z == this.lightDiagnosticsActive) {
            return;
        }
        this.lightDiagnosticsActive = z;
        if (z) {
            this.lightDiagnosticsSession++;
            resetLightDiagnostics();
            SensorManager sensorManager = (SensorManager) getSystemService("sensor");
            this.lightSensorManager = sensorManager;
            if (sensorManager != null && (defaultSensor = sensorManager.getDefaultSensor(5)) != null) {
                this.lightSensorManager.registerListener(this.androidLightListener, defaultSensor, 3);
            }
            try {
                this.lightDiagnosticsBound = bindService(new Intent().setClassName(NATIVE_PACKAGE, "ru.big.town.anative.LightDiagnosticsService"), this.lightDiagnosticsConnection, 1);
                return;
            } catch (IllegalArgumentException | SecurityException e) {
                Log.w("LightDiagnostics", "Native diagnostics unavailable", e);
                return;
            }
        }
        boolean z2 = this.lightDiagnosticsBound;
        this.lightDiagnosticsBound = false;
        SensorManager sensorManager2 = this.lightSensorManager;
        if (sensorManager2 != null) {
            sensorManager2.unregisterListener(this.androidLightListener);
        }
        this.lightSensorManager = null;
        if (z2) {
            try {
                unbindService(this.lightDiagnosticsConnection);
            } catch (IllegalArgumentException unused) {
            }
        }
        resetLightDiagnostics();
    }

    private void resetLightDiagnostics() {
        int[] iArr = new int[this.lightDiagnosticsRows.length];
        Arrays.fill(iArr, Integer.MIN_VALUE);
        showLightDiagnostics(iArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showLightDiagnostics(int[] iArr) {
        String string;
        for (int i = 0; i < iArr.length; i++) {
            if (this.lightDiagnosticsRows[i] != null) {
                int i2 = iArr[i];
                if (i2 == Integer.MIN_VALUE) {
                    string = "—";
                } else if (i == 0) {
                    string = iArr[i] + " — " + swReasonDescription(iArr[i]);
                } else {
                    string = Integer.toString(i2);
                }
                this.lightDiagnosticsRows[i].setText(LIGHT_DIAGNOSTICS_LABELS[i] + ": " + string);
            }
        }
    }

    private static String swReasonDescription(int i) {
        if (i == 0) {
            return "день";
        }
        if (i == 1) {
            return "другое";
        }
        if (i == 2) {
            return "темно";
        }
        if (i == 3) {
            return "тоннель";
        }
        if (i == 4) {
            return "начало темноты";
        }
        return "неизвестно";
    }

    private void updateSystemMetricsPolling() {
        boolean z = activityResumed && currentSection == 6 && !isFinishing();
        if (z == this.systemMetricsActive) {
            return;
        }
        this.systemMetricsActive = z;
        this.systemMetricsGeneration++;
        this.uiHandler.removeCallbacks(this.systemMetricsTick);
        synchronized (this.cpuSampleLock) {
            this.cpuBaselineGeneration = -1L;
            this.previousCpuTotal = -1L;
            this.previousCpuIdle = -1L;
        }
        if (z) {
            TextView textView = this.textRamStatus;
            if (textView != null) {
                textView.setText("Используется: …\nДоступно: …");
            }
            TextView textView2 = this.textCpuStatus;
            if (textView2 != null) {
                textView2.setText("Измерение…");
            }
            TextView textView3 = this.textHookStatus;
            if (textView3 != null) {
                textView3.setText("Чтение состояния…");
            }
            this.uiHandler.post(this.systemMetricsTick);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sampleSystemMetrics() {
        if (this.systemMetricsActive && this.currentSection == 6) {
            final long j = this.systemMetricsGeneration;
            try {
                this.systemMetricsExecutor.execute(new Runnable() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda61
                    @Override // java.lang.Runnable
                    public final void run() {
                        AdvanceActivity.this.m1830x839fc31f(j);
                    }
                });
            } catch (RejectedExecutionException unused) {
            }
        }
    }

    /* JADX INFO: renamed from: lambda$sampleSystemMetrics$61$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1830x839fc31f(final long j) {
        final SystemMetricsSnapshot systemMetrics = readSystemMetrics(j);
        this.uiHandler.post(new Runnable() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda66
            @Override // java.lang.Runnable
            public final void run() {
                AdvanceActivity.this.m1829xf6ff981e(j, systemMetrics);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$sampleSystemMetrics$60$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1829xf6ff981e(long j, SystemMetricsSnapshot systemMetricsSnapshot) {
        String str;
        if (this.systemMetricsActive && this.currentSection == 6 && j == this.systemMetricsGeneration) {
            TextView textView = this.textRamStatus;
            if (textView != null) {
                textView.setText("Используется: " + Formatter.formatFileSize(this, systemMetricsSnapshot.usedMemoryBytes) + " из " + Formatter.formatFileSize(this, systemMetricsSnapshot.totalMemoryBytes) + "\nДоступно: " + Formatter.formatFileSize(this, systemMetricsSnapshot.availableMemoryBytes));
            }
            TextView textView2 = this.textCpuStatus;
            if (textView2 != null) {
                if (Double.isNaN(systemMetricsSnapshot.cpuPercent)) {
                    str = "Измерение…";
                } else if (systemMetricsSnapshot.cpuPercent >= 0.0d) {
                    str = String.format(Locale.getDefault(), "%.0f%%", Double.valueOf(systemMetricsSnapshot.cpuPercent));
                } else {
                    str = "Недоступно";
                }
                textView2.setText(str);
            }
            TextView textView3 = this.textHookStatus;
            if (textView3 != null) {
                textView3.setText(systemMetricsSnapshot.hookStatusText);
            }
            this.uiHandler.postDelayed(systemMetricsTick, SYSTEM_METRICS_INTERVAL_MS);
        }
    }

    private SystemMetricsSnapshot readSystemMetrics(long j) {
        long jMax;
        long j2;
        try {
            ActivityManager activityManager = (ActivityManager) getSystemService("activity");
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            if (activityManager != null) {
                activityManager.getMemoryInfo(memoryInfo);
                jMax = Math.max(0L, memoryInfo.totalMem);
                j2 = Math.max(0L, Math.min(jMax, memoryInfo.availMem));
            } else {
                jMax = 0;
                j2 = 0;
            }
        } catch (RuntimeException e) {
            Log.w("SystemMetrics", "RAM read failed: " + e.getMessage());
            jMax = 0;
            j2 = 0;
        }
        long j3 = jMax;
        String hookPayload = getSharedPreferences("HookStatus", 0).getString(HookStatusContract.PAYLOAD_KEY, null);
        return new SystemMetricsSnapshot(j3, Math.max(0L, j3 - j2), j2, readCpuPercent(j), HookStatusContract.renderForUi(hookPayload));
    }

    private double readCpuPercent(long j) {
        CpuTimes cpuTimes = readCpuTimes();
        if (cpuTimes == null) {
            return -1.0d;
        }
        synchronized (this.cpuSampleLock) {
            if (this.systemMetricsActive && j == this.systemMetricsGeneration) {
                if (this.cpuBaselineGeneration == j && this.previousCpuTotal >= 0) {
                    long j2 = cpuTimes.total - this.previousCpuTotal;
                    long j3 = cpuTimes.idle - this.previousCpuIdle;
                    this.previousCpuTotal = cpuTimes.total;
                    this.previousCpuIdle = cpuTimes.idle;
                    if (j2 <= 0) {
                        return -1.0d;
                    }
                    return Math.max(0.0d, Math.min(100.0d, ((j2 - Math.max(0L, j3)) * 100.0d) / j2));
                }
                this.cpuBaselineGeneration = j;
                this.previousCpuTotal = cpuTimes.total;
                this.previousCpuIdle = cpuTimes.idle;
                return Double.NaN;
            }
            return -1.0d;
        }
    }

    private static CpuTimes readCpuTimes() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/stat"));
            try {
                String line = bufferedReader.readLine();
                if (line != null && line.startsWith("cpu ")) {
                    String[] strArrSplit = line.trim().split("\\s+");
                    if (strArrSplit.length >= 5) {
                        long j = 0;
                        for (int i = 1; i < strArrSplit.length && i <= 8; i++) {
                            j += Long.parseLong(strArrSplit[i]);
                        }
                        long j2 = Long.parseLong(strArrSplit[4]);
                        if (strArrSplit.length > 5) {
                            j2 += Long.parseLong(strArrSplit[5]);
                        }
                        CpuTimes cpuTimes = new CpuTimes(j, j2);
                        bufferedReader.close();
                        return cpuTimes;
                    }
                    return null;
                }
                bufferedReader.close();
                return null;
            } catch (Throwable th) {
                try {
                    bufferedReader.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (Exception e) {
            Log.w("SystemMetrics", "CPU read failed: " + e.getMessage());
            return null;
        }
    }

    private static final class CpuTimes {
        final long idle;
        final long total;

        CpuTimes(long j, long j2) {
            this.total = j;
            this.idle = j2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class SystemMetricsSnapshot {
        final long availableMemoryBytes;
        final double cpuPercent;
        final String hookStatusText;
        final long totalMemoryBytes;
        final long usedMemoryBytes;

        SystemMetricsSnapshot(long j, long j2, long j3, double d, String str) {
            this.totalMemoryBytes = j;
            this.usedMemoryBytes = j2;
            this.availableMemoryBytes = j3;
            this.cpuPercent = d;
            this.hookStatusText = str;
        }
    }

    private void initApolloTech() {
        this.switchApolloTlc = (Switch) findViewById(R.id.switchApolloTlc);
        this.switchApolloTrafficLights = (Switch) findViewById(R.id.switchApolloTrafficLights);
        this.switchApolloTrafficSigns = (Switch) findViewById(R.id.switchApolloTrafficSigns);
        this.apolloGreenSoundGroup = (RadioGroup) findViewById(R.id.apolloGreenSoundGroup);
        this.apolloGreenSoundContainer = findViewById(R.id.apolloGreenSoundContainer);
        this.textApolloStatus = (TextView) findViewById(R.id.textApolloStatus);
        bindApolloSwitch(this.switchApolloTlc, "apolloTlcEnabled");
        bindApolloSwitch(this.switchApolloTrafficSigns, "apolloTrafficSignsEnabled");
        bindApolloSwitch(this.switchApolloTrafficLights, "apolloTrafficLightsEnabled");
        boolean z = this.prefs.getBoolean("apolloGreenSoundEnabled", false);
        RadioGroup radioGroup = this.apolloGreenSoundGroup;
        if (radioGroup != null) {
            radioGroup.check(z ? R.id.apolloGreenSoundOn : R.id.apolloGreenSoundOff);
            this.apolloGreenSoundGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda73
                @Override // android.widget.RadioGroup.OnCheckedChangeListener
                public final void onCheckedChanged(RadioGroup radioGroup2, int i) {
                    AdvanceActivity.this.m1764lambda$initApolloTech$62$rubigtownrestoremodeAdvanceActivity(radioGroup2, i);
                }
            });
        }
        updateApolloUi();
    }

    /* JADX INFO: renamed from: lambda$initApolloTech$62$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1764lambda$initApolloTech$62$rubigtownrestoremodeAdvanceActivity(RadioGroup radioGroup, int i) {
        if (i == R.id.apolloGreenSoundOn || i == R.id.apolloGreenSoundOff) {
            this.prefs.edit().putBoolean("apolloGreenSoundEnabled", i == R.id.apolloGreenSoundOn).apply();
        }
    }

    private void bindApolloSwitch(Switch r3, final String str) {
        if (r3 == null) {
            return;
        }
        r3.setChecked(this.prefs.getBoolean(str, false));
        r3.setEnabled(true);
        r3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda44
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                AdvanceActivity.this.m1758xb40f07d1(str, compoundButton, z);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$bindApolloSwitch$63$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1758xb40f07d1(String str, CompoundButton compoundButton, boolean z) {
        this.prefs.edit().putBoolean(str, z).apply();
        if ("apolloTrafficLightsEnabled".equals(str)) {
            updateApolloUi();
        }
    }

    private void updateApolloUi() {
        Switch r0 = this.switchApolloTrafficLights;
        boolean z = r0 != null && r0.isChecked();
        RadioGroup radioGroup = this.apolloGreenSoundGroup;
        if (radioGroup != null) {
            radioGroup.setEnabled(z);
        }
        View view = this.apolloGreenSoundContainer;
        if (view != null && this.apolloGreenSoundGroup != null) {
            view.setAlpha(z ? 1.0f : 0.45f);
            for (int i = 0; i < this.apolloGreenSoundGroup.getChildCount(); i++) {
                this.apolloGreenSoundGroup.getChildAt(i).setEnabled(z);
            }
        }
        TextView textView = this.textApolloStatus;
        if (textView != null) {
            textView.setText("VoyahTune хранит выбранные значения без чтения текущего состояния автомобиля. Они применяются кнопкой «Применить» и автоматически через 10 секунд после пробуждения.");
        }
    }

    private void initSteeringButtons() {
        this.steerStarShortList = (LinearLayout) findViewById(R.id.steerStarShortList);
        this.steerStarLongList = (LinearLayout) findViewById(R.id.steerStarLongList);
        this.steerDvrShortList = (LinearLayout) findViewById(R.id.steerDvrShortList);
        this.steerDvrLongList = (LinearLayout) findViewById(R.id.steerDvrLongList);
        this.steerVoiceShortList = (LinearLayout) findViewById(R.id.steerVoiceShortList);
        this.steerVoiceLongList = (LinearLayout) findViewById(R.id.steerVoiceLongList);
        this.steerPhoneShortList = (LinearLayout) findViewById(R.id.steerPhoneShortList);
        this.steerPhoneLongList = (LinearLayout) findViewById(R.id.steerPhoneLongList);
        refreshSteerActions();
        pushSteerConfig();
    }

    public void onPickSteerStarShort(View view) {
        pickSteerAction("steerStarShort");
    }

    public void onPickSteerStarLong(View view) {
        pickSteerAction("steerStarLong");
    }

    public void onPickSteerVoiceShort(View view) {
        pickSteerAction("steerVoiceShort");
    }

    public void onPickSteerVoiceLong(View view) {
        pickSteerAction("steerVoiceLong");
    }

    public void onPickSteerDvrShort(View view) {
        pickSteerAction("steerDvrShort");
    }

    public void onPickSteerDvrLong(View view) {
        pickSteerAction("steerDvrLong");
    }

    public void onPickSteerPhoneShort(View view) {
        pickSteerAction("steerPhoneShort");
    }

    public void onPickSteerPhoneLong(View view) {
        pickSteerAction("steerPhoneLong");
    }

    private void pickSteerAction(final String str) {
        if (voiceOwnsSlot(str)) {
            return;
        }
        int length = STEER_ACTIONS.length;
        final int i = length - 1;
        CharSequence[] charSequenceArr = new CharSequence[length + 3];
        int i2 = 0;
        while (i2 < i) {
            int i3 = i2 + 1;
            charSequenceArr[i2] = STEER_ACTIONS[i3][1];
            i2 = i3;
        }
        charSequenceArr[i] = "Открыть сплит…";
        charSequenceArr[length] = "Открыть приложение…";
        charSequenceArr[length + 1] = "Набрать номер…";
        charSequenceArr[length + 2] = "Своя CAN-команда…";
        new MaterialAlertDialogBuilder(this, R.style.DarkDialog).setTitle((CharSequence) "Добавить действие").setItems(charSequenceArr, new DialogInterface.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda68
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i4) {
                AdvanceActivity.this.m1809x976f22a6(i, str, dialogInterface, i4);
            }
        }).setNegativeButton((CharSequence) "Отмена", (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: renamed from: lambda$pickSteerAction$64$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1809x976f22a6(int i, String str, DialogInterface dialogInterface, int i2) {
        if (i2 < i) {
            appendSteerAction(str, STEER_ACTIONS[i2 + 1][0]);
            return;
        }
        if (i2 == i) {
            pickSteerSplit(str);
            return;
        }
        if (i2 == i + 1) {
            pickSteerApp(str);
        } else if (i2 == i + 2) {
            pickSteerDial(str);
        } else {
            showCustomSteerCommandDialog(str);
        }
    }

    private void pickSteerDial(final String str) {
        final List<DialWidgetStore.Entry> listLoad = DialWidgetStore.load(this.prefs);
        if (listLoad.isEmpty()) {
            Toast.makeText(this, "Сначала создайте карточку набора номера", 0).show();
            return;
        }
        CharSequence[] charSequenceArr = new CharSequence[listLoad.size()];
        for (int i = 0; i < listLoad.size(); i++) {
            DialWidgetStore.Entry entry = listLoad.get(i);
            charSequenceArr[i] = (entry.name.isEmpty() ? "Без имени" : entry.name) + " — " + entry.number;
        }
        new MaterialAlertDialogBuilder(this, R.style.DarkDialog).setTitle((CharSequence) "Выбрать номер для кнопки руля").setItems(charSequenceArr, new DialogInterface.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda36
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                AdvanceActivity.this.m1811lambda$pickSteerDial$65$rubigtownrestoremodeAdvanceActivity(listLoad, str, dialogInterface, i2);
            }
        }).setNegativeButton((CharSequence) "Отмена", (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: renamed from: lambda$pickSteerDial$65$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1811lambda$pickSteerDial$65$rubigtownrestoremodeAdvanceActivity(List list, String str, DialogInterface dialogInterface, int i) {
        String strReplaceAll = ((DialWidgetStore.Entry) list.get(i)).number.replaceAll("[^0-9]", "");
        if (strReplaceAll.length() < 4 || strReplaceAll.length() > 10) {
            return;
        }
        if (strReplaceAll.length() == 10) {
            strReplaceAll = "8" + strReplaceAll;
        }
        appendSteerAction(str, "call:" + strReplaceAll);
    }

    private void pickSteerSplit(final String str) {
        List<SplitStore.Preset> listLoad = SplitStore.load(this.prefs);
        final ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < listLoad.size(); i++) {
            SplitStore.Preset preset = listLoad.get(i);
            if (preset.ready()) {
                arrayList.add(Integer.valueOf(i));
                arrayList2.add((preset.ll.isEmpty() ? preset.l : preset.ll) + "  /  " + (preset.rl.isEmpty() ? preset.r : preset.rl));
            }
        }
        if (arrayList.isEmpty()) {
            Snackbar.make(findViewById(R.id.main), "Нет готовых сплитов — сначала настройте сплит в «Приложения и разделение экрана»", 0).show();
        } else {
            new MaterialAlertDialogBuilder(this, R.style.DarkDialog).setTitle((CharSequence) "Открыть сплит").setItems((CharSequence[]) arrayList2.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda5
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    AdvanceActivity.this.m1812lambda$pickSteerSplit$66$rubigtownrestoremodeAdvanceActivity(str, arrayList, dialogInterface, i2);
                }
            }).setNegativeButton((CharSequence) "Отмена", (DialogInterface.OnClickListener) null).show();
        }
    }

    /* JADX INFO: renamed from: lambda$pickSteerSplit$66$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1812lambda$pickSteerSplit$66$rubigtownrestoremodeAdvanceActivity(String str, List list, DialogInterface dialogInterface, int i) {
        appendSteerAction(str, "split:" + list.get(i));
    }

    private void pickSteerApp(final String str) {
        showAppPicker("Открыть приложение", new AppPicked() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda75
            @Override // ru.big.town.restoremode.AdvanceActivity.AppPicked
            public final void onPicked(String str2, String str3) {
                AdvanceActivity.this.m1810lambda$pickSteerApp$67$rubigtownrestoremodeAdvanceActivity(str, str2, str3);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$pickSteerApp$67$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1810lambda$pickSteerApp$67$rubigtownrestoremodeAdvanceActivity(String str, String str2, String str3) {
        appendSteerAction(str, "app:" + str2);
    }

    private void showCustomSteerCommandDialog(final String str) {
        View viewInflate = LayoutInflater.from(this).inflate(R.layout.dialog_steering_can_command, (ViewGroup) null, false);
        final EditText editText = (EditText) viewInflate.findViewById(R.id.steerCanCommandInput);
        final TextView textView = (TextView) viewInflate.findViewById(R.id.steerCanCommandError);
        final AlertDialog alertDialogCreate = new MaterialAlertDialogBuilder(this, R.style.DarkDialog).setTitle((CharSequence) "Своя CAN-команда").setView(viewInflate).setPositiveButton((CharSequence) "Добавить", (DialogInterface.OnClickListener) null).setNegativeButton((CharSequence) "Отмена", (DialogInterface.OnClickListener) null).create();
        alertDialogCreate.setOnShowListener(new DialogInterface.OnShowListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda74
            @Override // android.content.DialogInterface.OnShowListener
            public final void onShow(DialogInterface dialogInterface) {
                AdvanceActivity.this.m1834x49bf6d87(alertDialogCreate, editText, textView, str, dialogInterface);
            }
        });
        alertDialogCreate.show();
    }

    /* JADX INFO: renamed from: lambda$showCustomSteerCommandDialog$69$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1834x49bf6d87(final AlertDialog alertDialog, final EditText editText, final TextView textView, final String str, DialogInterface dialogInterface) {
        final Button button = alertDialog.getButton(-1);
        editText.addTextChangedListener(new TextWatcher() { // from class: ru.big.town.restoremode.AdvanceActivity.20
            private boolean formatting;

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (this.formatting) {
                    return;
                }
                String str2 = SteeringCanCommandPolicy.format(charSequence.toString());
                if (!str2.contentEquals(charSequence)) {
                    this.formatting = true;
                    editText.setText(str2);
                    editText.setSelection(str2.length());
                    this.formatting = false;
                }
                AdvanceActivity.this.updateCustomCanValidation(editText, textView, button);
            }
        });
        updateCustomCanValidation(editText, textView, button);
        button.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda45
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AdvanceActivity.this.m1833xbd1f4286(editText, str, alertDialog, view);
            }
        });
        editText.requestFocus();
    }

    /* JADX INFO: renamed from: lambda$showCustomSteerCommandDialog$68$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1833xbd1f4286(EditText editText, String str, AlertDialog alertDialog, View view) {
        if (SteeringCanCommandPolicy.isValid(editText.getText().toString())) {
            appendSteerAction(str, SteeringCanCommandPolicy.actionId(editText.getText().toString()));
            alertDialog.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCustomCanValidation(EditText editText, TextView textView, Button button) {
        String str;
        String strCompact = SteeringCanCommandPolicy.compact(editText.getText().toString());
        boolean z = strCompact.length() == 20;
        editText.setBackgroundColor(z ? -1 : -20561);
        if (z) {
            str = "Команда готова";
        } else {
            str = "Нужно 20 hex-символов (10 байт). Сейчас: " + strCompact.length();
        }
        textView.setText(str);
        textView.setTextColor(z ? -7616093 : -30080);
        button.setEnabled(z);
        button.setAlpha(z ? 1.0f : 0.4f);
    }

    private void appendSteerAction(String str, String str2) {
        if (voiceOwnsSlot(str)) {
            return;
        }
        List<String> listLoad = SteeringActionStore.load(this.prefs, str);
        listLoad.add(str2);
        SteeringActionStore.save(this.prefs, str, listLoad);
        refreshSteerActions();
        pushSteerConfig();
    }

    private boolean voiceOwnsSlot(String str) {
        return VoiceSteeringPolicy.ownsSlot(this.prefs.getBoolean("voiceAssistantEnabled", false), this.prefs.getString("voiceAssistantSteeringPress", "long"), str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshSteerActions() {
        int[] iArr = {R.id.steerVoiceShortBtn, R.id.steerVoiceLongBtn};
        String[] strArr = {"steerVoiceShort", "steerVoiceLong"};
        for (int i = 0; i < 2; i++) {
            View viewFindViewById = findViewById(iArr[i]);
            if (viewFindViewById != null) {
                boolean zVoiceOwnsSlot = voiceOwnsSlot(strArr[i]);
                viewFindViewById.setEnabled(!zVoiceOwnsSlot);
                viewFindViewById.setAlpha(zVoiceOwnsSlot ? 0.4f : 1.0f);
            }
        }
        renderSteerActionList("steerStarShort", this.steerStarShortList);
        renderSteerActionList("steerStarLong", this.steerStarLongList);
        renderSteerActionList("steerDvrShort", this.steerDvrShortList);
        renderSteerActionList("steerDvrLong", this.steerDvrLongList);
        renderSteerActionList("steerVoiceShort", this.steerVoiceShortList);
        renderSteerActionList("steerVoiceLong", this.steerVoiceLongList);
        renderSteerActionList("steerPhoneShort", this.steerPhoneShortList);
        renderSteerActionList("steerPhoneLong", this.steerPhoneLongList);
    }

    private void renderSteerActionList(final String str, LinearLayout linearLayout) {
        if (linearLayout == null) {
            return;
        }
        linearLayout.removeAllViews();
        if (voiceOwnsSlot(str)) {
            TextView textView = new TextView(this);
            textView.setText(("steerVoiceShort".equals(str) ? "Короткое" : "Долгое").concat(" нажатие занято голосовым помощником. Прежние действия сохранены. Изменить нажатие или отключить помощника: Голосовое управление."));
            textView.setTextColor(-6249040);
            textView.setTextSize(18.0f);
            linearLayout.addView(textView);
            return;
        }
        List<String> listLoad = SteeringActionStore.load(this.prefs, str);
        if (listLoad.isEmpty()) {
            TextView textView2 = new TextView(this);
            textView2.setText("Действия не назначены");
            textView2.setTextColor(-7829368);
            textView2.setTextSize(18.0f);
            textView2.setPadding(4, Math.round(getResources().getDisplayMetrics().density * 8.0f), 4, 0);
            linearLayout.addView(textView2);
            return;
        }
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(this);
        int iIdx = 0;
        while (iIdx < listLoad.size()) {
            final int i = iIdx;
            View viewInflate = layoutInflaterFrom.inflate(R.layout.item_steering_action, (ViewGroup) linearLayout, false);
            TextView textView3 = (TextView) viewInflate.findViewById(R.id.steerActionLabel);
            ImageButton imageButton = (ImageButton) viewInflate.findViewById(R.id.steerActionDelete);
            int i2 = i + 1;
            textView3.setText(i2 + ". " + steerActionLabel(listLoad.get(i)));
            imageButton.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda76
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AdvanceActivity.this.m1828x6b2af2b8(str, i, view);
                }
            });
            linearLayout.addView(viewInflate);
            iIdx = i2;
        }
    }

    /* JADX INFO: renamed from: lambda$renderSteerActionList$70$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1828x6b2af2b8(String str, int i, View view) {
        List<String> listLoad = SteeringActionStore.load(this.prefs, str);
        if (i < 0 || i >= listLoad.size()) {
            return;
        }
        listLoad.remove(i);
        SteeringActionStore.save(this.prefs, str, listLoad);
        refreshSteerActions();
        pushSteerConfig();
    }

    private String steerActionLabel(String str) {
        if (str == null || str.isEmpty()) {
            return "Не менять";
        }
        for (String[] strArr : STEER_ACTIONS) {
            if (strArr[0].equals(str)) {
                return strArr[1];
            }
        }
        if (str.startsWith("split:")) {
            try {
                int i = Integer.parseInt(str.substring("split:".length()));
                List<SplitStore.Preset> listLoad = SplitStore.load(this.prefs);
                if (i >= 0 && i < listLoad.size()) {
                    SplitStore.Preset preset = listLoad.get(i);
                    return "Сплит: " + (preset.ll.isEmpty() ? preset.l : preset.ll) + " / " + (preset.rl.isEmpty() ? preset.r : preset.rl);
                }
                return "Сплит (не найден)";
            } catch (Exception unused) {
                return "Сплит (не найден)";
            }
        }
        if (str.startsWith("app:")) {
            String strSubstring = str.substring("app:".length());
            try {
                PackageManager packageManager = getPackageManager();
                return "Приложение: " + packageManager.getApplicationLabel(packageManager.getApplicationInfo(strSubstring, 0)).toString();
            } catch (Exception unused2) {
                return "Приложение: " + strSubstring;
            }
        }
        if (str.startsWith("call:")) {
            return "Набрать номер: " + str.substring("call:".length());
        }
        if (str.startsWith("can:")) {
            String strSubstring2 = str.substring("can:".length());
            if (strSubstring2.length() == 20) {
                return "Своя команда: " + SteeringCanCommandPolicy.format(strSubstring2);
            }
            return "Своя команда (неверный формат)";
        }
        return "Неизвестное действие: " + str;
    }

    private void pushSteerConfig() {
        SplitConfigSync.pushSteering(this, this.prefs);
    }

    private void pushDockConfig() {
        SplitConfigSync.pushDock(this, this.prefs);
    }

    private void initPedestrianSoundGroup() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.pedestrianSoundGroup);
        if (radioGroup == null) {
            return;
        }
        radioGroup.check(this.prefs.getBoolean("disablePedestrianSound", false) ? R.id.pedestrianSoundOn : R.id.pedestrianSoundOff);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda59
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public final void onCheckedChanged(RadioGroup radioGroup2, int i) {
                AdvanceActivity.this.m1775x2de45553(radioGroup2, i);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$initPedestrianSoundGroup$71$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1775x2de45553(RadioGroup radioGroup, int i) {
        if (this.syncingSettingUi) {
            return;
        }
        boolean z = i == R.id.pedestrianSoundOn;
        this.prefs.edit().putBoolean("disablePedestrianSound", z).apply();
        sendPedestrianSound(z);
        Log.i("$$$ Advance pedestrian $$$", z ? "DISABLED (muted)" : "ENABLED");
    }

    private void sendPedestrianSound(boolean z) {
        if (!GlobalVars.isBound || GlobalVars.serviceMessenger == null) {
            Log.w("$$$ Advance pedestrian $$$", "SetModesService не забинден");
            return;
        }
        try {
            GlobalVars.serviceMessenger.send(Message.obtain(null, 21, z ? 1 : 0, 0));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void initSuspensionMaintenance() {
        Switch r0 = (Switch) findViewById(R.id.switchSuspensionMaintenance);
        r0.setChecked(this.prefs.getBoolean("suspensionMaintenance", false));
        r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda65
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                AdvanceActivity.this.m1776x45a7d243(compoundButton, z);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$initSuspensionMaintenance$72$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1776x45a7d243(CompoundButton compoundButton, boolean z) {
        if (this.syncingSettingUi) {
            return;
        }
        this.prefs.edit().putBoolean("suspensionMaintenance", z).apply();
        if (!GlobalVars.isBound || GlobalVars.serviceMessenger == null) {
            return;
        }
        try {
            GlobalVars.serviceMessenger.send(Message.obtain(null, 37, z ? 1 : 0, 0));
        } catch (RemoteException e) {
            Log.w("VoyahSuspension", "Service unavailable", e);
        }
    }

    private void initForcedEvGroup() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.forcedEvGroup);
        if (radioGroup == null) {
            return;
        }
        radioGroup.check(this.prefs.getBoolean("forcedEv", false) ? R.id.forcedEvOn : R.id.forcedEvOff);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda64
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public final void onCheckedChanged(RadioGroup radioGroup2, int i) {
                AdvanceActivity.this.m1769x8ad648a5(radioGroup2, i);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$initForcedEvGroup$73$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1769x8ad648a5(RadioGroup radioGroup, int i) {
        if (this.syncingSettingUi) {
            return;
        }
        boolean z = i == R.id.forcedEvOn;
        this.prefs.edit().putBoolean("forcedEv", z).apply();
        sendForcedEv(z);
        Log.i("$$$ Advance forcedEV $$$", z ? "ON" : "OFF");
    }

    private void sendForcedEv(boolean z) {
        if (!GlobalVars.isBound || GlobalVars.serviceMessenger == null) {
            Log.w("$$$ Advance forcedEV $$$", "SetModesService не забинден");
            return;
        }
        try {
            GlobalVars.serviceMessenger.send(Message.obtain(null, 35, z ? 1 : 0, 0));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void initModeRadios() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.drive_modes_group);
        RadioGroup radioGroup2 = (RadioGroup) findViewById(R.id.energy_modes_group);
        RadioGroup radioGroup3 = (RadioGroup) findViewById(R.id.recycle_modes_group);
        checkRadioByTag(radioGroup, this.prefs.getString("driveMode", "INDIVIDUAL"));
        checkRadioByTag(radioGroup2, this.prefs.getString("energy", "SREV"));
        checkRadioByTag(radioGroup3, this.prefs.getString("recycle", "LOW"));
        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda40
                @Override // android.widget.RadioGroup.OnCheckedChangeListener
                public final void onCheckedChanged(RadioGroup radioGroup4, int i) {
                    AdvanceActivity.this.m1771lambda$initModeRadios$74$rubigtownrestoremodeAdvanceActivity(radioGroup4, i);
                }
            });
        }
        if (radioGroup != null) {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                View childAt = radioGroup.getChildAt(i);
                if (childAt instanceof RadioButton) {
                    childAt.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda41
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            AdvanceActivity.this.m1772lambda$initModeRadios$75$rubigtownrestoremodeAdvanceActivity(view);
                        }
                    });
                }
            }
        }
        if (radioGroup2 != null) {
            radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda42
                @Override // android.widget.RadioGroup.OnCheckedChangeListener
                public final void onCheckedChanged(RadioGroup radioGroup4, int i2) {
                    AdvanceActivity.this.m1773lambda$initModeRadios$76$rubigtownrestoremodeAdvanceActivity(radioGroup4, i2);
                }
            });
        }
        if (radioGroup3 != null) {
            radioGroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda43
                @Override // android.widget.RadioGroup.OnCheckedChangeListener
                public final void onCheckedChanged(RadioGroup radioGroup4, int i2) {
                    AdvanceActivity.this.m1774lambda$initModeRadios$77$rubigtownrestoremodeAdvanceActivity(radioGroup4, i2);
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$initModeRadios$74$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1771lambda$initModeRadios$74$rubigtownrestoremodeAdvanceActivity(RadioGroup radioGroup, int i) {
        saveRadio("driveMode", i);
    }

    /* JADX INFO: renamed from: lambda$initModeRadios$75$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1772lambda$initModeRadios$75$rubigtownrestoremodeAdvanceActivity(View view) {
        saveRadio("driveMode", view.getId());
    }

    /* JADX INFO: renamed from: lambda$initModeRadios$76$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1773lambda$initModeRadios$76$rubigtownrestoremodeAdvanceActivity(RadioGroup radioGroup, int i) {
        saveRadio("energy", i);
    }

    /* JADX INFO: renamed from: lambda$initModeRadios$77$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1774lambda$initModeRadios$77$rubigtownrestoremodeAdvanceActivity(RadioGroup radioGroup, int i) {
        saveRadio("recycle", i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkRadioByTag(RadioGroup radioGroup, String str) {
        if (radioGroup == null || str == null) {
            return;
        }
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View childAt = radioGroup.getChildAt(i);
            if ((childAt instanceof RadioButton) && str.equals(childAt.getTag())) {
                ((RadioButton) childAt).setChecked(true);
                return;
            }
        }
    }

    private void saveRadio(String str, int i) {
        View viewFindViewById;
        if (this.syncingModeUi || (viewFindViewById = findViewById(i)) == null || viewFindViewById.getTag() == null) {
            return;
        }
        if ("driveMode".equals(str)) {
            DriveSelectionPreferences.select(this.prefs, viewFindViewById.getTag().toString(), DriveSelectionPolicy.SETTINGS);
        } else if ("energy".equals(str)) {
            DriveSelectionPreferences.selectEnergy(this.prefs, viewFindViewById.getTag().toString(), true);
        } else {
            this.prefs.edit().putString(str, viewFindViewById.getTag().toString()).apply();
        }
        Log.i("$$$ Advance mode $$$", str + "=" + viewFindViewById.getTag());
    }

    private void initModeEnableToggles() {
        setupEnableSwitch(R.id.switchDriveMode, R.id.drive_modes_group, "driveEnabled");
        setupEnableSwitch(R.id.switchEnergy, R.id.energy_modes_group, "energyEnabled");
        setupEnableSwitch(R.id.switchRecycle, R.id.recycle_modes_group, "recycleEnabled");
    }

    private void initModeRememberLastToggles() {
        bindRememberLastSwitch(R.id.switchDriveRememberLast, "driveRememberLast", "driveMode");
        bindRememberLastSwitch(R.id.switchEnergyRememberLast, "energyRememberLast", "energy");
        bindRememberLastSwitch(R.id.switchRecycleRememberLast, "recycleRememberLast", "recycle");
    }

    private void bindRememberLastSwitch(int i, final String str, final String str2) {
        Switch r3 = (Switch) findViewById(i);
        if (r3 == null) {
            return;
        }
        r3.setChecked(this.prefs.getBoolean(str, true));
        r3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda63
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                AdvanceActivity.this.m1760xa3514c01(str, str2, compoundButton, z);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$bindRememberLastSwitch$78$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1760xa3514c01(String str, String str2, CompoundButton compoundButton, boolean z) {
        this.prefs.edit().putBoolean(str, z).apply();
        sendBroadcast(new Intent(ACTION_MODE_REMEMBER_CHANGED).setPackage(NATIVE_PACKAGE).putExtra(EXTRA_MODE_KEY, str2).putExtra(EXTRA_REMEMBER_LAST, z));
    }

    private void initFragranceSettings() {
        Switch r0 = (Switch) findViewById(R.id.switchFragrance);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.fragranceTasteGroup);
        RadioGroup radioGroup2 = (RadioGroup) findViewById(R.id.fragranceDurationGroup);
        RadioGroup radioGroup3 = (RadioGroup) findViewById(R.id.fragranceIntensityGroup);
        int iNormalizeTaste = FragranceSettings.normalizeTaste(this.prefs.getInt("fragranceTaste", 1));
        int iNormalizeDuration = FragranceSettings.normalizeDuration(this.prefs.getInt("fragranceDuration", 0));
        int iNormalizeIntensity = FragranceSettings.normalizeIntensity(this.prefs.getInt("fragranceIntensity", 2));
        checkRadioByTag(radioGroup, String.valueOf(iNormalizeTaste));
        checkRadioByTag(radioGroup2, String.valueOf(iNormalizeDuration));
        checkRadioByTag(radioGroup3, String.valueOf(iNormalizeIntensity));
        bindIntRadio(radioGroup, "fragranceTaste");
        bindIntRadio(radioGroup2, "fragranceDuration");
        bindIntRadio(radioGroup3, "fragranceIntensity");
        boolean z = this.prefs.getBoolean("fragranceEnabled", false);
        if (r0 != null) {
            r0.setChecked(z);
            r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda55
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                    AdvanceActivity.this.m1770x379e9fee(compoundButton, z2);
                }
            });
        }
        applyFragranceEnabled(z);
    }

    /* JADX INFO: renamed from: lambda$initFragranceSettings$79$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1770x379e9fee(CompoundButton compoundButton, boolean z) {
        this.prefs.edit().putBoolean("fragranceEnabled", z).apply();
        applyFragranceEnabled(z);
    }

    private void bindIntRadio(RadioGroup radioGroup, final String str) {
        if (radioGroup == null) {
            return;
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda69
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public final void onCheckedChanged(RadioGroup radioGroup2, int i) {
                AdvanceActivity.this.m1759lambda$bindIntRadio$80$rubigtownrestoremodeAdvanceActivity(str, radioGroup2, i);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$bindIntRadio$80$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1759lambda$bindIntRadio$80$rubigtownrestoremodeAdvanceActivity(String str, RadioGroup radioGroup, int i) {
        View viewFindViewById = findViewById(i);
        if (viewFindViewById == null || viewFindViewById.getTag() == null) {
            return;
        }
        try {
            this.prefs.edit().putInt(str, Integer.parseInt(viewFindViewById.getTag().toString())).apply();
        } catch (NumberFormatException e) {
            Log.e("$$$ Advance fragrance $$$", "Invalid " + str + " tag", e);
        }
    }

    private void applyFragranceEnabled(boolean z) {
        applyModeToggle(R.id.fragranceTasteGroup, z);
        applyModeToggle(R.id.fragranceDurationGroup, z);
        applyModeToggle(R.id.fragranceIntensityGroup, z);
    }

    private void setupEnableSwitch(int i, final int i2, final String str) {
        Switch r3 = (Switch) findViewById(i);
        if (r3 == null) {
            return;
        }
        boolean z = this.prefs.getBoolean(str, false);
        r3.setChecked(z);
        applyModeToggle(i2, z);
        r3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda71
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                AdvanceActivity.this.m1831x703cceb3(str, i2, compoundButton, z2);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$setupEnableSwitch$81$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1831x703cceb3(String str, int i, CompoundButton compoundButton, boolean z) {
        this.prefs.edit().putBoolean(str, z).apply();
        applyModeToggle(i, z);
    }

    private void applyModeToggle(int i, boolean z) {
        RadioGroup radioGroup = (RadioGroup) findViewById(i);
        if (radioGroup == null) {
            return;
        }
        radioGroup.setAlpha(z ? 1.0f : 0.4f);
        for (int i2 = 0; i2 < radioGroup.getChildCount(); i2++) {
            radioGroup.getChildAt(i2).setEnabled(z);
            radioGroup.getChildAt(i2).setClickable(z);
        }
    }

    private void initCheckBox34() {
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox34);
        this.checkBox34 = checkBox;
        if (checkBox == null) {
            return;
        }
        checkBox.setChecked(this.prefs.getBoolean("checkBox34", false));
        applyCheckBox34();
    }

    public void onCheckBox34Click(View view) {
        applyCheckBox34();
    }

    private void applyCheckBox34() {
        if (this.checkBox34 == null) {
            return;
        }
        RadioButton radioButton = (RadioButton) findViewById(R.id.SMART);
        if (this.checkBox34.isChecked()) {
            if (radioButton != null) {
                radioButton.setVisibility(8);
            }
            this.checkBox34.setText("4 кнопки");
            this.prefs.edit().putBoolean("checkBox34", true).apply();
            return;
        }
        if (radioButton != null) {
            radioButton.setVisibility(0);
        }
        this.checkBox34.setText("3 кнопки");
        this.prefs.edit().putBoolean("checkBox34", false).apply();
    }

    private void initAutoLight() {
        this.autoLightGroup = (RadioGroup) findViewById(R.id.autoLightGroup);
        this.textSensorLevel = (TextView) findViewById(R.id.textSensorLevel);
        if (this.autoLightGroup == null) {
            return;
        }
        this.autoLightGroup.check(this.prefs.getBoolean("autoLight", false) ? R.id.autoLightOn : R.id.autoLightOff);
        this.autoLightGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.AdvanceActivity$$ExternalSyntheticLambda48
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public final void onCheckedChanged(RadioGroup radioGroup, int i) {
                AdvanceActivity.this.m1765lambda$initAutoLight$82$rubigtownrestoremodeAdvanceActivity(radioGroup, i);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$initAutoLight$82$ru-big-town-restoremode-AdvanceActivity, reason: not valid java name */
    /* synthetic */ void m1765lambda$initAutoLight$82$rubigtownrestoremodeAdvanceActivity(RadioGroup radioGroup, int i) {
        TextView textView;
        if (this.syncingSettingUi) {
            return;
        }
        boolean z = i == R.id.autoLightOn;
        this.prefs.edit().putBoolean("autoLight", z).apply();
        sendAutoLightMessage(z);
        if (!z && (textView = this.textSensorLevel) != null) {
            textView.setText("Датчик: —");
        }
        Log.i("$$$ Advance autolight $$$", z ? "ON" : "OFF");
    }

    private void sendAutoLightMessage(boolean z) {
        int i = z ? 10 : 11;
        if (!GlobalVars.isBound || GlobalVars.serviceMessenger == null) {
            Log.w("$$$ Advance autolight $$$", "SetModesService не забинден — состояние применится позже");
            return;
        }
        try {
            GlobalVars.serviceMessenger.send(Message.obtain((Handler) null, i));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        VoiceSettingsPage voiceSettingsPage = this.voiceSettings;
        if (voiceSettingsPage != null) {
            voiceSettingsPage.onPermissionResult(i, iArr);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void renderCommandResult(Intent intent) {
        TextView textView = this.commandStatusText;
        if (textView == null || intent == null) {
            return;
        }
        String stringExtra = intent.getStringExtra("feature");
        String stringExtra2 = intent.getStringExtra("state");
        if (stringExtra == null || stringExtra2 == null) {
            return;
        }
        int iCommandFeatureName = commandFeatureName(stringExtra);
        String string = iCommandFeatureName != 0 ? getString(iCommandFeatureName) : stringExtra;
        int i = R.string.cmd_status_sending;
        int i2 = R.drawable.pill_pending;
        if ("CONFIRMED".equals(stringExtra2)) {
            i = R.string.cmd_status_confirmed;
            i2 = R.drawable.pill_active;
        } else if ("FAILED".equals(stringExtra2)) {
            i = R.string.cmd_status_failed;
            i2 = R.drawable.pill_error;
        } else if ("TIMEOUT".equals(stringExtra2)) {
            i = R.string.cmd_status_timeout;
            i2 = R.drawable.pill_error;
        } else if (!"SENT".equals(stringExtra2) && !"PENDING_ACK".equals(stringExtra2)) {
            return;
        }
        textView.setText(getString(i, string));
        textView.setBackgroundResource(i2);
        textView.setVisibility(0);
    }

    private static int commandFeatureName(String str) {
        if ("driveMode".equals(str)) {
            return R.string.cmd_feature_driveMode;
        }
        if ("energy".equals(str)) {
            return R.string.cmd_feature_energy;
        }
        if ("recycle".equals(str)) {
            return R.string.cmd_feature_recycle;
        }
        if ("suspension".equals(str)) {
            return R.string.cmd_feature_suspension;
        }
        if ("light".equals(str)) {
            return R.string.cmd_feature_light;
        }
        if ("avas".equals(str)) {
            return R.string.cmd_feature_avas;
        }
        return 0;
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        VoiceSettingsPage voiceSettingsPage;
        super.onResume();
        this.activityResumed = true;
        refreshSteerActions();
        if (this.currentSection == 7 && (voiceSettingsPage = this.voiceSettings) != null) {
            voiceSettingsPage.refresh();
        }
        RadioGroup radioGroup = this.autoLightGroup;
        if (radioGroup != null) {
            this.syncingSettingUi = true;
            radioGroup.check(this.prefs.getBoolean("autoLight", false) ? R.id.autoLightOn : R.id.autoLightOff);
            this.syncingSettingUi = false;
        }
        updateSystemMetricsPolling();
        updateLightDiagnosticsBinding();
        registerReceiver(this.luxReceiver, new IntentFilter("ru.big.town.anative.LUX_UPDATE"), 2);
        registerReceiver(this.modeSyncReceiver, new IntentFilter("ru.big.town.anative.MODE_SYNCED"), 2);
        registerReceiver(this.settingSyncReceiver, new IntentFilter("ru.big.town.anative.SETTING_SYNCED"), "ru.big.town.anative.permission.BIND_SET_MODES_SERVICE", null, 2);
        registerReceiver(this.commandResultReceiver, new IntentFilter("ru.big.town.anative.ACTION_COMMAND_RESULT"), "ru.big.town.anative.permission.BIND_SET_MODES_SERVICE", null, 2);
        Intent intent = new Intent("ru.big.town.anative.REQUEST_LUX_UPDATE");
        intent.setPackage(NATIVE_PACKAGE);
        sendBroadcast(intent);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        this.activityResumed = false;
        updateSystemMetricsPolling();
        updateLightDiagnosticsBinding();
        super.onPause();
        try {
            unregisterReceiver(this.luxReceiver);
        } catch (Exception unused) {
        }
        try {
            unregisterReceiver(this.modeSyncReceiver);
        } catch (Exception unused2) {
        }
        try {
            unregisterReceiver(this.settingSyncReceiver);
        } catch (Exception unused3) {
        }
        try {
            unregisterReceiver(this.commandResultReceiver);
        } catch (Exception unused4) {
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        this.activityResumed = false;
        this.systemMetricsActive = false;
        this.systemMetricsGeneration++;
        this.uiHandler.removeCallbacks(this.systemMetricsTick);
        this.systemMetricsExecutor.shutdownNow();
        super.onDestroy();
    }
}
