package ru.big.town.anative;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.car.VehicleAreaDoor;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import androidx.core.content.ContextCompat;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
final class ScreenLiftTaskRestorer implements AutoCloseable {
    private static final String ACTION_CHANGED = "action.qg.layout.changed";
    private static final String ACTION_START = "action.qg.layout.start_change";
    private static final String LAUNCHER_PKG = "com.qinggan.app.launcher";
    private static final long RESTORE_DELAY_MS = 2000;
    private static final String SCREEN_LIFT_PROPERTY = "persist.qg.canbus.bcm_screenAutoLiftFdb";
    private static final String SCREEN_LIFT_SETTING = "voyahtune_screen_lift_type";
    private static final String[] STOCK_PREFIXES = {"com.android", "com.qinggan", "com.pateo", "com.baidu", "com.huawei", "com.iflytek", "com.iland", "com.mega", "com.qti", "com.qualcomm", "com.tencent", "com.nng.igo.primong", "com.bz.CA08"};
    private static final String TAG = "ScreenLiftRestore";
    private final ActivityManager activityManager;
    private final Context context;
    private boolean displayFieldResolved;
    private Field displayIdField;
    private long generation;
    private boolean registered;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Map<Integer, SavedTask> savedByDisplay = new HashMap();
    private final BroadcastReceiver receiver = new BroadcastReceiver() { // from class: ru.big.town.anative.ScreenLiftTaskRestorer.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent == null ? null : intent.getAction();
            if (ScreenLiftTaskRestorer.ACTION_START.equals(action)) {
                ScreenLiftTaskRestorer.this.capture();
            } else if (ScreenLiftTaskRestorer.ACTION_CHANGED.equals(action)) {
                ScreenLiftTaskRestorer.this.scheduleRestore(intent.getIntExtra("type", 0));
            }
        }
    };

    private static final class SavedTask {
        final ComponentName component;
        final int displayId;
        final String packageName;
        final int taskId;

        SavedTask(int i, int i2, String str, ComponentName componentName) {
            this.taskId = i;
            this.displayId = i2;
            this.packageName = str;
            this.component = componentName;
        }
    }

    ScreenLiftTaskRestorer(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.context = applicationContext;
        this.activityManager = (ActivityManager) applicationContext.getSystemService("activity");
    }

    void register() {
        if (this.registered) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_START);
        intentFilter.addAction(ACTION_CHANGED);
        try {
            ContextCompat.registerReceiver(this.context, this.receiver, intentFilter, 2);
            this.registered = true;
            Log.i(TAG, "registered");
        } catch (RuntimeException e) {
            Log.w(TAG, "register failed: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void capture() {
        ComponentName componentName;
        this.generation++;
        this.handler.removeCallbacksAndMessages(null);
        this.savedByDisplay.clear();
        HashSet hashSet = new HashSet();
        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTasks()) {
            int iDisplayId = displayId(runningTaskInfo);
            if (iDisplayId == 0 || iDisplayId == 1) {
                if (hashSet.add(Integer.valueOf(iDisplayId)) && (componentName = runningTaskInfo.topActivity) != null && isRestorable(componentName.getPackageName())) {
                    this.savedByDisplay.put(Integer.valueOf(iDisplayId), new SavedTask(runningTaskInfo.taskId, iDisplayId, componentName.getPackageName(), componentName));
                    Log.i(TAG, "captured task=" + runningTaskInfo.taskId + " display=" + iDisplayId + " component=" + componentName.flattenToShortString());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleRestore(final int i) {
        if (i == 1 || i == 2) {
            int liftProperty = readLiftProperty(i);
            if (liftProperty != i) {
                Log.w(TAG, "changed broadcast ignored; type=" + i + " property=" + liftProperty);
                return;
            }
            try {
                Settings.Global.putInt(this.context.getContentResolver(), SCREEN_LIFT_SETTING, i);
            } catch (RuntimeException e) {
                Log.w(TAG, "persist lift type failed: " + e.getMessage());
            }
            final long j = this.generation;
            if (this.savedByDisplay.isEmpty()) {
                return;
            }
            this.handler.postDelayed(new Runnable() { // from class: ru.big.town.anative.ScreenLiftTaskRestorer$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ScreenLiftTaskRestorer.this.m2030xc8ac7057(j, i);
                }
            }, RESTORE_DELAY_MS);
        }
    }

    private int readLiftProperty(int i) {
        try {
            int iIntValue = ((Integer) Class.forName("android.os.SystemProperties").getDeclaredMethod("getInt", String.class, Integer.TYPE).invoke(null, SCREEN_LIFT_PROPERTY, Integer.valueOf(i))).intValue();
            return (iIntValue == 1 || iIntValue == 2) ? iIntValue : i;
        } catch (ReflectiveOperationException | RuntimeException unused) {
            return i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: restore, reason: merged with bridge method [inline-methods] */
    public void m2030xc8ac7057(long j, int i) {
        if (j != this.generation) {
            return;
        }
        Map<Integer, SavedTask> map = new HashMap<>(this.savedByDisplay);
        this.savedByDisplay.clear();
        for (SavedTask savedTask : map.values()) {
            ComponentName componentName = topComponent(savedTask.displayId);
            if (componentName == null || !savedTask.packageName.equals(componentName.getPackageName())) {
                if (componentName != null && !LAUNCHER_PKG.equals(componentName.getPackageName())) {
                    Log.i(TAG, "restore skipped; another app is foreground display=" + savedTask.displayId + " component=" + componentName.flattenToShortString());
                } else {
                    try {
                        this.activityManager.moveTaskToFront(savedTask.taskId, 0);
                        Log.i(TAG, "restored existing task=" + savedTask.taskId + " display=" + savedTask.displayId + " liftType=" + i);
                    } catch (RuntimeException e) {
                        launchFallback(savedTask, e);
                    }
                }
            }
        }
    }

    private void launchFallback(SavedTask savedTask, RuntimeException runtimeException) {
        try {
            Intent launchIntentForPackage = this.context.getPackageManager().getLaunchIntentForPackage(savedTask.packageName);
            if (launchIntentForPackage == null) {
                throw runtimeException;
            }
            launchIntentForPackage.addFlags(VehicleAreaDoor.DOOR_HOOD);
            ActivityOptions activityOptionsMakeBasic = ActivityOptions.makeBasic();
            activityOptionsMakeBasic.setLaunchDisplayId(savedTask.displayId);
            this.context.startActivity(launchIntentForPackage, activityOptionsMakeBasic.toBundle());
            Log.i(TAG, "restored by launch fallback display=" + savedTask.displayId + " component=" + savedTask.component.flattenToShortString());
        } catch (RuntimeException e) {
            Log.w(TAG, "restore failed task=" + savedTask.taskId + " display=" + savedTask.displayId + ": " + e.getMessage());
        }
    }

    private ComponentName topComponent(int i) {
        ComponentName componentNameOemTopComponent = oemTopComponent(i);
        if (componentNameOemTopComponent != null) {
            return componentNameOemTopComponent;
        }
        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTasks()) {
            if (displayId(runningTaskInfo) == i) {
                return runningTaskInfo.topActivity;
            }
        }
        return null;
    }

    private ComponentName oemTopComponent(int i) {
        try {
            Object objInvoke = Class.forName("com.qinggan.os.ServiceManager").getMethod("getDpyTopAppInfo", Context.class, Integer.TYPE, Integer.TYPE).invoke(null, this.context, Integer.valueOf(i), 4);
            if (!(objInvoke instanceof String)) {
                return null;
            }
            String strTrim = ((String) objInvoke).trim();
            if (strTrim.isEmpty()) {
                return null;
            }
            return ComponentName.unflattenFromString(strTrim);
        } catch (ReflectiveOperationException | RuntimeException e) {
            Log.w(TAG, "OEM top component unavailable display=" + i + ": " + e.getMessage());
            return null;
        }
    }

    private List<ActivityManager.RunningTaskInfo> runningTasks() {
        ActivityManager activityManager = this.activityManager;
        if (activityManager == null) {
            return Collections.emptyList();
        }
        try {
            return activityManager.getRunningTasks(64);
        } catch (RuntimeException e) {
            Log.w(TAG, "getRunningTasks failed: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private int displayId(ActivityManager.RunningTaskInfo runningTaskInfo) {
        if (!this.displayFieldResolved) {
            this.displayFieldResolved = true;
            try {
                this.displayIdField = runningTaskInfo.getClass().getField("displayId");
            } catch (ReflectiveOperationException e) {
                Log.w(TAG, "RunningTaskInfo.displayId unavailable: " + e.getMessage());
            }
        }
        Field field = this.displayIdField;
        if (field == null) {
            return -1;
        }
        try {
            return field.getInt(runningTaskInfo);
        } catch (IllegalAccessException unused) {
            return -1;
        }
    }

    private static boolean isRestorable(String str) {
        if (str == null || str.isEmpty() || LAUNCHER_PKG.equals(str)) {
            return false;
        }
        if (!"com.android.settings".equals(str) && !"com.android.documentsui".equals(str)) {
            for (String str2 : STOCK_PREFIXES) {
                if (str.startsWith(str2)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        this.generation++;
        this.handler.removeCallbacksAndMessages(null);
        this.savedByDisplay.clear();
        if (this.registered) {
            try {
                this.context.unregisterReceiver(this.receiver);
            } catch (IllegalArgumentException unused) {
            }
            this.registered = false;
        }
    }
}
