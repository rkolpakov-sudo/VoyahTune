package ru.big.town.anative;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
final class WidgetSupport {
    static final String ACTION_CLOSE_ALL = "ru.big.town.anative.WIDGET_CLOSE_ALL";
    static final String ACTION_CLOSE_APP = "ru.big.town.anative.WIDGET_CLOSE_APP";
    static final String ACTION_FULLSCREEN_LAUNCH = "ru.big.town.anative.WIDGET_FULLSCREEN_LAUNCH";
    static final String ACTION_OPEN_APP = "ru.big.town.anative.WIDGET_OPEN_APP";
    static final String ACTION_SIMPLE_LAUNCH = "ru.big.town.anative.WIDGET_SIMPLE_LAUNCH";
    static final String EXTRA_PACKAGE = "package";
    static final String PREF_LAST_MANUAL_APP = "lastManualApp";
    private static final String TAG = "NativeWidgets";

    private WidgetSupport() {
    }

    static String homePackage(Context context) {
        ResolveInfo resolveInfoResolveActivity = context.getPackageManager().resolveActivity(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME"), 0);
        if (resolveInfoResolveActivity == null || resolveInfoResolveActivity.activityInfo == null) {
            return null;
        }
        return resolveInfoResolveActivity.activityInfo.packageName;
    }

    static boolean isExternal(Context context, String str) {
        if (str == null || str.equals(context.getPackageName()) || str.equals("ru.big.town.restoremode") || str.equals(homePackage(context))) {
            return false;
        }
        if (str.equals("com.android.contacts") || str.equals("com.android.car.dialer")) {
            return true;
        }
        if (!str.startsWith("com.qinggan") && !str.startsWith("com.android.car")) {
            try {
                if ((context.getPackageManager().getApplicationInfo(str, 0).flags & 1) == 0) {
                    return true;
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        return false;
    }

    static List<RunningApp> runningApps(Context context) {
        CharSequence applicationLabel;
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        if (activityManager == null) {
            return new ArrayList();
        }
        for (ActivityManager.RunningTaskInfo runningTaskInfo : activityManager.getRunningTasks(100)) {
            ComponentName componentName = runningTaskInfo.topActivity != null ? runningTaskInfo.topActivity : runningTaskInfo.baseActivity;
            if (componentName != null && isExternal(context, componentName.getPackageName())) {
                String packageName = componentName.getPackageName();
                if (!linkedHashMap.containsKey(packageName)) {
                    try {
                        applicationLabel = context.getPackageManager().getApplicationLabel(context.getPackageManager().getApplicationInfo(packageName, 0));
                    } catch (PackageManager.NameNotFoundException unused) {
                        applicationLabel = packageName;
                    }
                    linkedHashMap.put(packageName, new RunningApp(packageName, applicationLabel.toString()));
                }
            }
        }
        return new ArrayList(linkedHashMap.values());
    }

    static List<LaunchableApp> launchableApps(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intentAddCategory = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER");
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(intentAddCategory, 0)) {
            String str = resolveInfo.activityInfo.packageName;
            if (isExternal(context, str) && !linkedHashMap.containsKey(str)) {
                linkedHashMap.put(str, new LaunchableApp(str, resolveInfo.loadLabel(packageManager).toString(), resolveInfo.loadIcon(packageManager)));
            }
        }
        return new ArrayList(linkedHashMap.values());
    }

    static PendingIntent appPendingIntent(Context context, String str, String str2, int i) {
        return PendingIntent.getBroadcast(context, i, new Intent(context, (Class<?>) WidgetActionReceiver.class).setAction(str).putExtra("package", str2), 201326592);
    }

    static void openApp(Context context, String str) {
        if (isExternal(context, str)) {
            SetModesReceiverDynamic.openFreeformApp(context.getApplicationContext(), str, 0);
        }
    }

    static void simpleLaunch(Context context, String str) {
        if (isExternal(context, str)) {
            SetModesReceiverDynamic.openFreeformApp(context.getApplicationContext(), str, 0);
        }
    }

    static void fullscreenLaunch(Context context, String str) {
        if (isExternal(context, str)) {
            SplitHostActivity.launchSingle(context.getApplicationContext(), str, 0, 0);
        }
    }

    static void stopApp(Context context, String str) {
        if (isExternal(context, str)) {
            try {
                ActivityManager.class.getMethod("forceStopPackage", String.class).invoke((ActivityManager) context.getSystemService("activity"), str);
            } catch (Exception e) {
                Log.w(TAG, "Не удалось закрыть " + str, e);
            }
        }
    }

    static void stopAllApps(Context context) {
        for (ApplicationInfo applicationInfo : context.getPackageManager().getInstalledApplications(0)) {
            if ((applicationInfo.flags & 1) == 0) {
                stopApp(context, applicationInfo.packageName);
            }
        }
    }

    static void saveLastManualApp(Context context, String str) {
        if (str == null || str.isEmpty()) {
            return;
        }
        try {
            context.getSharedPreferences("NativePrefs", 0).edit().putString(PREF_LAST_MANUAL_APP, str).apply();
            Log.i(TAG, "saveLastManualApp: " + str);
        } catch (Exception e) {
            Log.w(TAG, "saveLastManualApp: " + e.getMessage());
        }
    }

    static String getLastManualApp(Context context) {
        try {
            return context.getSharedPreferences("NativePrefs", 0).getString(PREF_LAST_MANUAL_APP, null);
        } catch (Exception e) {
            Log.w(TAG, "getLastManualApp: " + e.getMessage());
            return null;
        }
    }

    static void clearLastManualApp(Context context) {
        try {
            context.getSharedPreferences("NativePrefs", 0).edit().remove(PREF_LAST_MANUAL_APP).apply();
            Log.i(TAG, "clearLastManualApp");
        } catch (Exception e) {
            Log.w(TAG, "clearLastManualApp: " + e.getMessage());
        }
    }

    static Bitmap iconBitmap(Drawable drawable, int i) {
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        drawable.setBounds(0, 0, i, i);
        drawable.draw(canvas);
        return bitmapCreateBitmap;
    }

    static final class RunningApp {
        final String label;
        final String packageName;

        RunningApp(String str, String str2) {
            this.packageName = str;
            this.label = str2;
        }
    }

    static final class LaunchableApp {
        final Drawable icon;
        final String label;
        final String packageName;

        LaunchableApp(String str, String str2, Drawable drawable) {
            this.packageName = str;
            this.label = str2;
            this.icon = drawable;
        }
    }
}
