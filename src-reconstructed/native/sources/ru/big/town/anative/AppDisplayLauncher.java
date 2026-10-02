package ru.big.town.anative;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.car.VehicleAreaDoor;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;

/* JADX INFO: loaded from: classes2.dex */
final class AppDisplayLauncher {
    private static final String TAG = "VoyahAppDisplay";
    private static final Handler WORKER;
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    private static final Map<String, Long> GENERATIONS = new HashMap();

    static {
        HandlerThread handlerThread = new HandlerThread("voyah-display-launch");
        handlerThread.start();
        WORKER = new Handler(handlerThread.getLooper());
    }

    private AppDisplayLauncher() {
    }

    static synchronized void cancel(String str) {
        Map<String, Long> map = GENERATIONS;
        map.put(str, Long.valueOf(map.getOrDefault(str, 0L).longValue() + 1));
    }

    private static synchronized long next(String str) {
        cancel(str);
        return GENERATIONS.get(str).longValue();
    }

    private static synchronized boolean current(String str, long j) {
        return GENERATIONS.getOrDefault(str, 0L).longValue() == j;
    }

    static void launch(Context context, final String str, final int i, final boolean z, final BooleanSupplier booleanSupplier, final Runnable runnable) {
        final Context applicationContext = context.getApplicationContext();
        final long next = next(str);
        WORKER.post(new Runnable() { // from class: ru.big.town.anative.AppDisplayLauncher$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AppDisplayLauncher.prepare(applicationContext, str, i, z, next, booleanSupplier, runnable);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void prepare(Context context, String str, int i, boolean z, long j, BooleanSupplier booleanSupplier, Runnable runnable) {
        String str2 = str;
        long j2 = j;
        if (!current(str2, j2)) {
            return;
        }
        try {
            Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(str2);
            if (launchIntentForPackage == null) {
                throw new IllegalStateException("No launcher for " + str2);
            }
            ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
            List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(100);
            if (runningTasks == null) {
                throw new IllegalStateException("Task list unavailable");
            }
            ArrayList<SplitHostTaskSnapshot.TaskRecord> arrayList = new ArrayList();
            for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTasks) {
                try {
                    ComponentName componentName = runningTaskInfo.baseActivity != null ? runningTaskInfo.baseActivity : runningTaskInfo.topActivity;
                    if (componentName != null && str2.equals(componentName.getPackageName())) {
                        arrayList.add(new SplitHostTaskSnapshot.TaskRecord(runningTaskInfo.taskId, str2, Integer.valueOf(runningTaskInfo.getClass().getField("displayId").getInt(runningTaskInfo))));
                    }
                } catch (Exception e) {
                    e = e;
                    failed(str2, j2, booleanSupplier, runnable, e);
                }
            }
            Set<Integer> setRetiring = AppDisplayTaskPlan.retiring(arrayList, str2, i);
            for (SplitHostTaskSnapshot.TaskRecord taskRecord : arrayList) {
                if (setRetiring.contains(Integer.valueOf(taskRecord.taskId)) && taskRecord.displayId != null && taskRecord.displayId.intValue() > 1) {
                    SetModesService.notifyEmbeddedTaskLeft(context, str);
                    break;
                }
            }
            Iterator<Integer> it = setRetiring.iterator();
            Object obj = null;
            Object objInvoke = null;
            Method method = null;
            while (it.hasNext()) {
                int iIntValue = it.next().intValue();
                if (!current(str2, j2)) {
                    return;
                }
                if (objInvoke == null) {
                    objInvoke = Class.forName("android.app.ActivityTaskManager").getMethod("getService", new Class[0]).invoke(obj, new Object[0]);
                    method = objInvoke.getClass().getMethod("removeTask", Integer.TYPE);
                }
                if (Boolean.FALSE.equals(method.invoke(objInvoke, Integer.valueOf(iIntValue)))) {
                    throw new IllegalStateException("Cannot retire task " + iIntValue);
                }
                obj = null;
            }
            launchIntentForPackage.addFlags(VehicleAreaDoor.DOOR_HOOD);
            try {
                awaitRemoval(context, activityManager, str2, i, z, j2, launchIntentForPackage, setRetiring, 0, booleanSupplier, runnable);
            } catch (Exception e2) {
                e = e2;
                str2 = str2;
                j2 = j;
                failed(str2, j2, booleanSupplier, runnable, e);
            }
        } catch (Exception e3) {
            e = e3;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void awaitRemoval(final Context context, final ActivityManager activityManager, final String str, final int i, final boolean z, final long j, final Intent intent, final Set<Integer> set, final int i2, final BooleanSupplier booleanSupplier, final Runnable runnable) {
        String str2 = str;
        long j2 = j;
        if (current(str2, j2)) {
            try {
                if (!set.isEmpty()) {
                    List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(100);
                    if (runningTasks == null) {
                        throw new IllegalStateException("Task list unavailable");
                    }
                    Iterator<ActivityManager.RunningTaskInfo> it = runningTasks.iterator();
                    while (it.hasNext()) {
                        if (set.contains(Integer.valueOf(it.next().taskId))) {
                            if (i2 >= 20) {
                                throw new IllegalStateException("Task teardown timed out");
                            }
                            final long j3 = j2;
                            final String str3 = str2;
                            WORKER.postDelayed(new Runnable() { // from class: ru.big.town.anative.AppDisplayLauncher$$ExternalSyntheticLambda1
                                @Override // java.lang.Runnable
                                public final void run() {
                                    AppDisplayLauncher.awaitRemoval(context, activityManager, str3, i, z, j3, intent, set, i2 + 1, booleanSupplier, runnable);
                                }
                            }, 100L);
                            return;
                        }
                        str2 = str;
                        j2 = j;
                    }
                }
                MAIN.post(new Runnable() { // from class: ru.big.town.anative.AppDisplayLauncher$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        AppDisplayLauncher.lambda$awaitRemoval$2(str, j, booleanSupplier, i, z, context, intent, runnable);
                    }
                });
            } catch (Exception e) {
                failed(str, j, booleanSupplier, runnable, e);
            }
        }
    }

    static /* synthetic */ void lambda$awaitRemoval$2(String str, long j, BooleanSupplier booleanSupplier, int i, boolean z, Context context, Intent intent, Runnable runnable) {
        if (current(str, j) && booleanSupplier.getAsBoolean()) {
            try {
                ActivityOptions activityOptionsMakeBasic = ActivityOptions.makeBasic();
                activityOptionsMakeBasic.setLaunchDisplayId(i);
                Bundle bundle = activityOptionsMakeBasic.toBundle();
                if (z) {
                    bundle.putInt("android.activity.windowingMode", 1);
                }
                DockLaunchGuard.arm(context, i, str);
                context.startActivity(intent, bundle);
                Log.i(TAG, "launched pkg=" + str + " display=" + i);
            } catch (Exception e) {
                failed(str, j, booleanSupplier, runnable, e);
            }
        }
    }

    private static void failed(final String str, final long j, final BooleanSupplier booleanSupplier, final Runnable runnable, Exception exc) {
        Log.w(TAG, "launch failed: " + str, exc);
        MAIN.post(new Runnable() { // from class: ru.big.town.anative.AppDisplayLauncher$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                AppDisplayLauncher.lambda$failed$3(str, j, booleanSupplier, runnable);
            }
        });
    }

    static /* synthetic */ void lambda$failed$3(String str, long j, BooleanSupplier booleanSupplier, Runnable runnable) {
        if (current(str, j) && booleanSupplier.getAsBoolean() && runnable != null) {
            runnable.run();
        }
    }
}
