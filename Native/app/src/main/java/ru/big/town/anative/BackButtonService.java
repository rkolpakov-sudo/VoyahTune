package ru.big.town.anative;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.lang.reflect.Field;
import java.util.LinkedHashSet;

/* JADX INFO: loaded from: classes2.dex */
public class BackButtonService extends AccessibilityService {
    private static final int BUTTON_GAP_DP = 10;
    private static final int BUTTON_TOUCH_OUTSET_DP = 10;
    private static final int BUTTON_VISUAL_SIZE_DP = 45;
    private static final String COMPONENT = "ru.big.town.anative/ru.big.town.anative.BackButtonService";
    private static final String PREF_FLOATING = "floatingBack";
    private static final String PREF_FULLSCREEN = "fullscreenApps";
    private static final String PREF_STEERING = "steeringBack";
    static final int SIDE_LEFT = 0;
    static final int SIDE_RIGHT = 2;
    static final int SIDE_TOP = 1;
    static final String TAG = "$$$ BackButtonService $$$";
    private static BackButtonService instance;
    private int btnGap;
    private int btnSize;
    private LinearLayout buttonView;
    private WindowManager.LayoutParams lp;
    private int touchOutset;
    private WindowManager wm;
    private final Handler overlayHandler = new Handler(Looper.getMainLooper());
    private String pendingEventPackage = "";
    private String lastForcedPackage = "";
    private final Runnable overlayReevaluate = new Runnable() { // from class: ru.big.town.anative.BackButtonService$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            BackButtonService.this.m1813lambda$new$0$rubigtownanativeBackButtonService();
        }
    };

    @Override // android.accessibilityservice.AccessibilityService
    public void onInterrupt() {
    }

    /* JADX INFO: renamed from: lambda$new$0$ru-big-town-anative-BackButtonService, reason: not valid java name */
    /* synthetic */ void m1813lambda$new$0$rubigtownanativeBackButtonService() {
        reevaluateOverlayNow(this.pendingEventPackage);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SharedPreferences prefs() {
        return getSharedPreferences("NativePrefs", 0);
    }

    @Override // android.accessibilityservice.AccessibilityService
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        if (prefs().getBoolean(PREF_FLOATING, false)) {
            Log.i(TAG, "onServiceConnected — показываем пользовательский оверлей");
            showButton();
        } else {
            Log.i(TAG, "onServiceConnected — ждём полноэкранное приложение");
        }
        scheduleOverlayEvaluation(null);
    }

    static void setFloatingButtonEnabled(Context context, boolean z) {
        prefs(context).edit().putBoolean(PREF_FLOATING, z).apply();
        syncAccessibility(context);
        BackButtonService backButtonService = instance;
        if (backButtonService != null) {
            backButtonService.scheduleOverlayEvaluation(null);
        }
    }

    static void setSteeringBackEnabled(Context context, boolean z) {
        prefs(context).edit().putBoolean(PREF_STEERING, z).apply();
        syncAccessibility(context);
    }

    static void setFullscreenPackages(Context context, String str) {
        prefs(context).edit().putString(PREF_FULLSCREEN, FullscreenPackagePolicy.normalizeCsv(str)).apply();
        syncAccessibility(context);
        BackButtonService backButtonService = instance;
        if (backButtonService != null) {
            backButtonService.scheduleOverlayEvaluation(null);
        }
    }

    static void performBack(final Context context) {
        BackButtonService backButtonService = instance;
        if (backButtonService != null) {
            backButtonService.performBackNow("steering wheel");
            return;
        }
        disableForReconnect(context);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() { // from class: ru.big.town.anative.BackButtonService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                BackButtonService.syncAccessibility(context);
            }
        }, 150L);
        handler.postDelayed(new Runnable() { // from class: ru.big.town.anative.BackButtonService$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                BackButtonService.lambda$performBack$2();
            }
        }, 700L);
    }

    static /* synthetic */ void lambda$performBack$2() {
        BackButtonService backButtonService = instance;
        if (backButtonService != null) {
            backButtonService.performBackNow("steering wheel delayed");
        } else {
            Log.w(TAG, "GLOBAL_ACTION_BACK пропущен: accessibility-сервис не подключён");
        }
    }

    private void performBackNow(String str) {
        performGlobalActionNow(1, "GLOBAL_ACTION_BACK", str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performGlobalActionNow(int i, String str, String str2) {
        Log.i(TAG, str + " (" + str2 + ") -> " + performGlobalAction(i));
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences("NativePrefs", 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void syncAccessibility(Context context) {
        SharedPreferences sharedPreferencesPrefs = prefs(context);
        writeAccessibility(context, FullscreenPackagePolicy.requiresAccessibilityService(sharedPreferencesPrefs.getBoolean(PREF_FLOATING, false), sharedPreferencesPrefs.getBoolean(PREF_STEERING, false), sharedPreferencesPrefs.getString(PREF_FULLSCREEN, "")));
    }

    static void disableForReconnect(Context context) {
        writeAccessibility(context, false);
    }

    private static void writeAccessibility(Context context, boolean z) {
        SharedPreferences sharedPreferencesPrefs = prefs(context);
        try {
            ContentResolver contentResolver = context.getContentResolver();
            String string = Settings.Secure.getString(contentResolver, "enabled_accessibility_services");
            LinkedHashSet<String> linkedHashSet = new LinkedHashSet();
            if (string != null) {
                for (String str : string.split(":")) {
                    if (!str.isEmpty()) {
                        linkedHashSet.add(str);
                    }
                }
            }
            if (z) {
                linkedHashSet.add(COMPONENT);
            } else {
                linkedHashSet.remove(COMPONENT);
            }
            StringBuilder sb = new StringBuilder();
            for (String str2 : linkedHashSet) {
                if (sb.length() > 0) {
                    sb.append(":");
                }
                sb.append(str2);
            }
            Settings.Secure.putString(contentResolver, "enabled_accessibility_services", sb.toString());
            Settings.Secure.putInt(contentResolver, "accessibility_enabled", !linkedHashSet.isEmpty() ? 1 : 0);
            Log.i(TAG, "back a11y " + (z ? "ON" : "OFF") + "; floating=" + sharedPreferencesPrefs.getBoolean(PREF_FLOATING, false) + "; steering=" + sharedPreferencesPrefs.getBoolean(PREF_STEERING, false) + "; fullscreen=" + sharedPreferencesPrefs.getString(PREF_FULLSCREEN, ""));
        } catch (Exception e) {
            Log.e(TAG, "syncAccessibility failed: " + e.getMessage());
        }
    }

    static void updatePosition() {
        BackButtonService backButtonService = instance;
        if (backButtonService != null) {
            backButtonService.applyLayout();
        }
    }

    static boolean reshow() {
        BackButtonService backButtonService = instance;
        if (backButtonService == null) {
            return false;
        }
        backButtonService.reshowOverlay();
        return true;
    }

    private void reshowOverlay() {
        WindowManager windowManager;
        LinearLayout linearLayout = this.buttonView;
        if (linearLayout != null && (windowManager = this.wm) != null) {
            try {
                windowManager.removeView(linearLayout);
            } catch (Exception unused) {
            }
        }
        this.buttonView = null;
        showButton();
        Log.i(TAG, "reshowOverlay — оверлей пересоздан");
    }

    private int dp(int i) {
        return (int) TypedValue.applyDimension(1, i, getResources().getDisplayMetrics());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int clamp(int i, int i2, int i3) {
        return Math.max(i2, Math.min(i3, i));
    }

    private void scheduleOverlayEvaluation(String str) {
        if (str != null) {
            this.pendingEventPackage = str;
        }
        this.overlayHandler.removeCallbacks(this.overlayReevaluate);
        this.overlayHandler.postDelayed(this.overlayReevaluate, 120L);
    }

    private void reevaluateOverlayNow(String str) {
        String string = prefs().getString(PREF_FULLSCREEN, "");
        String strDefaultDisplayTopPackage = FullscreenPackagePolicy.contains(string, str) ? str : defaultDisplayTopPackage();
        if (!strDefaultDisplayTopPackage.isEmpty() || str == null) {
            str = strDefaultDisplayTopPackage;
        }
        boolean z = prefs().getBoolean(PREF_FLOATING, false);
        boolean zContains = FullscreenPackagePolicy.contains(string, str);
        if (FullscreenPackagePolicy.shouldShowOverlay(z, string, str)) {
            if (this.buttonView == null) {
                showButton();
            }
        } else {
            hideButton();
        }
        String str2 = zContains ? str : "";
        if (str2.equals(this.lastForcedPackage)) {
            return;
        }
        Log.i(TAG, zContains ? "forced overlay ON for " + str : "forced overlay OFF");
        this.lastForcedPackage = str2;
    }

    private String defaultDisplayTopPackage() {
        try {
            ActivityManager activityManager = (ActivityManager) getSystemService("activity");
            if (activityManager == null) {
                return "";
            }
            String packageName = "";
            for (ActivityManager.RunningTaskInfo runningTaskInfo : activityManager.getRunningTasks(20)) {
                if (runningTaskInfo.topActivity != null) {
                    if (packageName.isEmpty()) {
                        packageName = runningTaskInfo.topActivity.getPackageName();
                    }
                    if (runningTaskDisplayId(runningTaskInfo) == 0) {
                        return runningTaskInfo.topActivity.getPackageName();
                    }
                }
            }
            return packageName;
        } catch (Exception e) {
            Log.w(TAG, "top task unavailable: " + e.getMessage());
            return "";
        }
    }

    private static int runningTaskDisplayId(ActivityManager.RunningTaskInfo runningTaskInfo) {
        for (Class<?> superclass = runningTaskInfo.getClass(); superclass != null; superclass = superclass.getSuperclass()) {
            try {
                Field declaredField = superclass.getDeclaredField("displayId");
                declaredField.setAccessible(true);
                return declaredField.getInt(runningTaskInfo);
            } catch (NoSuchFieldException unused) {
            } catch (Exception unused2) {
                return -1;
            }
        }
        return -1;
    }

    private void showButton() {
        if (this.buttonView != null) {
            applyLayout();
            return;
        }
        WindowManager windowManager = (WindowManager) getSystemService("window");
        this.wm = windowManager;
        if (windowManager == null) {
            Log.e(TAG, "WindowManager == null");
            return;
        }
        this.btnSize = dp(45);
        this.btnGap = dp(10);
        this.touchOutset = dp(10);
        int i = prefs().getInt("floatingBackSide", 0);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setShowDividers(0);
        linearLayout.setBackground(null);
        linearLayout.addView(createButton(R.drawable.ic_back_arrow, R.string.floating_back_button_desc));
        linearLayout.addView(createButton(R.drawable.ic_home, R.string.floating_home_button_desc));
        linearLayout.setOnTouchListener(new DragTouchListener());
        int i2 = this.btnSize;
        int i3 = (i2 * 2) + this.btnGap;
        int i4 = this.touchOutset;
        int i5 = i3 + (i4 * 2);
        int i6 = i2 + (i4 * 2);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(i == 1 ? i5 : i6, i == 1 ? i6 : i5, 2032, 40, -3);
        this.lp = layoutParams;
        layoutParams.gravity = 8388659;
        try {
            this.wm.addView(linearLayout, this.lp);
            this.buttonView = linearLayout;
            applyLayout();
            Log.i(TAG, "кнопки Назад/Home добавлены");
        } catch (Exception e) {
            Log.e(TAG, "addView failed: " + e.getMessage());
        }
    }

    private ImageView createButton(int i, int i2) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(i);
        imageView.setBackground(null);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setContentDescription(getString(i2));
        int iDp = dp(10);
        imageView.setPadding(iDp, iDp, iDp, iDp);
        int i3 = this.btnSize;
        imageView.setLayoutParams(new LinearLayout.LayoutParams(i3, i3));
        return imageView;
    }

    private void applyButtonGeometry(int i) {
        int i2 = i != 1 ? 0 : 1;
        this.buttonView.setOrientation(i2 ^ 1);
        LinearLayout linearLayout = this.buttonView;
        int i3 = this.touchOutset;
        linearLayout.setPadding(i3, i3, i3, i3);
        for (int i4 = 0; i4 < this.buttonView.getChildCount(); i4++) {
            int i5 = this.btnSize;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(i5, i5);
            if (i4 == 0) {
                if (i2 != 0) {
                    layoutParams.rightMargin = this.btnGap;
                } else {
                    layoutParams.bottomMargin = this.btnGap;
                }
            }
            this.buttonView.getChildAt(i4).setLayoutParams(layoutParams);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyLayout() {
        if (this.buttonView == null || this.wm == null || this.lp == null) {
            return;
        }
        int i = prefs().getInt("floatingBackSide", 0);
        int i2 = prefs().getInt("floatingBackOffset", -1);
        Point point = new Point();
        this.wm.getDefaultDisplay().getSize(point);
        int i3 = point.x;
        int i4 = point.y;
        int i5 = this.btnSize;
        int i6 = (i5 * 2) + this.btnGap;
        int i7 = this.touchOutset;
        int i8 = i6 + (i7 * 2);
        int i9 = i5 + (i7 * 2);
        this.lp.gravity = 8388659;
        applyButtonGeometry(i);
        if (i == 1) {
            this.lp.width = i8;
            this.lp.height = i9;
            int iMax = Math.max(0, i3 - this.lp.width);
            this.lp.x = i2 < 0 ? iMax / 2 : clamp(i2, 0, iMax);
            this.lp.y = 0;
        } else {
            this.lp.width = i9;
            this.lp.height = i8;
            int iMax2 = Math.max(0, i4 - this.lp.height);
            this.lp.y = i2 < 0 ? iMax2 / 2 : clamp(i2, 0, iMax2);
            WindowManager.LayoutParams layoutParams = this.lp;
            layoutParams.x = i == 2 ? Math.max(0, i3 - layoutParams.width) : 0;
        }
        try {
            this.wm.updateViewLayout(this.buttonView, this.lp);
            Log.i(TAG, "applyLayout side=" + i + " x=" + this.lp.x + " y=" + this.lp.y);
        } catch (Exception unused) {
        }
    }

    private class DragTouchListener implements View.OnTouchListener {
        private String actionName;
        private boolean dragging;
        private int globalAction;
        private float rawX0;
        private float rawY0;
        private final int slop;
        private int startX;
        private int startY;

        private DragTouchListener() {
            this.slop = ViewConfiguration.get(BackButtonService.this).getScaledTouchSlop();
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                boolean homeHalf = prefs().getInt("floatingBackSide", 0) == 1
                    ? motionEvent.getX() >= view.getWidth() / 2.0f
                    : motionEvent.getY() >= view.getHeight() / 2.0f;
                this.globalAction = homeHalf ? 2 : 1;
                this.actionName = homeHalf ? "GLOBAL_ACTION_HOME" : "GLOBAL_ACTION_BACK";
                this.startX = BackButtonService.this.lp.x;
                this.startY = BackButtonService.this.lp.y;
                this.rawX0 = motionEvent.getRawX();
                this.rawY0 = motionEvent.getRawY();
                this.dragging = false;
                return true;
            }
            if (actionMasked == 2) {
                int dx = (int) (motionEvent.getRawX() - this.rawX0);
                int dy = (int) (motionEvent.getRawY() - this.rawY0);
                if (!this.dragging && Math.hypot(dx, dy) > this.slop) {
                    this.dragging = true;
                }
                if (this.dragging) {
                    Point point = new Point();
                    BackButtonService.this.wm.getDefaultDisplay().getSize(point);
                    if (prefs().getInt("floatingBackSide", 0) == 1) {
                        BackButtonService.this.lp.x = clamp(this.startX + dx, 0, Math.max(0, point.x - BackButtonService.this.lp.width));
                    } else {
                        BackButtonService.this.lp.y = clamp(this.startY + dy, 0, Math.max(0, point.y - BackButtonService.this.lp.height));
                    }
                    try {
                        BackButtonService.this.wm.updateViewLayout(BackButtonService.this.buttonView, BackButtonService.this.lp);
                    } catch (Exception unused) {
                    }
                }
                return true;
            }
            if (actionMasked == 1) {
                if (this.dragging) {
                    int offset = prefs().getInt("floatingBackSide", 0) == 1
                        ? BackButtonService.this.lp.x
                        : BackButtonService.this.lp.y;
                    prefs().edit().putInt("floatingBackOffset", offset).commit();
                    Log.i(TAG, "позиция сохранена offset=" + offset);
                } else {
                    BackButtonService.this.performGlobalActionNow(this.globalAction, this.actionName, "floating button");
                }
                return true;
            }
            if (actionMasked == 3) {
                BackButtonService.this.applyLayout();
                return true;
            }
            return false;
        }
    }

    private void hideButton() {
        WindowManager windowManager;
        LinearLayout linearLayout = this.buttonView;
        if (linearLayout == null || (windowManager = this.wm) == null) {
            return;
        }
        try {
            windowManager.removeView(linearLayout);
        } catch (Exception unused) {
        }
        this.buttonView = null;
        Log.i(TAG, "блок кнопок убран");
    }

    @Override // android.accessibilityservice.AccessibilityService
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        CharSequence packageName = accessibilityEvent == null ? null : accessibilityEvent.getPackageName();
        scheduleOverlayEvaluation(packageName != null ? packageName.toString() : null);
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        this.overlayHandler.removeCallbacks(this.overlayReevaluate);
        hideButton();
        if (instance == this) {
            instance = null;
        }
        return super.onUnbind(intent);
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.overlayHandler.removeCallbacks(this.overlayReevaluate);
        hideButton();
        if (instance == this) {
            instance = null;
        }
        super.onDestroy();
    }
}
