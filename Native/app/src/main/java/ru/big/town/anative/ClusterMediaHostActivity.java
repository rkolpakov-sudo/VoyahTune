package ru.big.town.anative;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import java.lang.ref.WeakReference;
import java.util.function.BooleanSupplier;

/* JADX INFO: loaded from: classes2.dex */
public final class ClusterMediaHostActivity extends Activity implements TextureView.SurfaceTextureListener {
    private static final int DPI = 160;
    private static final String EXTRA_GENERATION = "clusterGeneration";
    private static final String EXTRA_PACKAGE = "clusterPackage";
    private static final int HEIGHT = 464;
    private static final int LEFT = 66;
    private static final String MEDIA_DISPLAY_NAME = "Cluster-Media-Display";
    private static final String TAG = "VoyahClusterMedia";
    private static final int TOP = 200;
    private static final int WIDTH = 574;
    private boolean closing;
    private long generation;
    private String packageName;
    private Surface surface;
    private VirtualDisplay virtualDisplay;
    private static final ClusterMediaSession SESSION = new ClusterMediaSession();
    private static WeakReference<ClusterMediaHostActivity> active = new WeakReference<>(null);

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    static void launch(Context context, String str) {
        if (str == null || str.isEmpty() || "none".equals(str)) {
            return;
        }
        Context applicationContext = context.getApplicationContext();
        int iFindMediaDisplayId = findMediaDisplayId(applicationContext);
        if (iFindMediaDisplayId < 0 || applicationContext.getPackageManager().getLaunchIntentForPackage(str) == null) {
            showError(applicationContext, "Медиакарточка или приложение недоступны");
            return;
        }
        ClusterMediaHostActivity clusterMediaHostActivity = active.get();
        if (clusterMediaHostActivity != null && clusterMediaHostActivity.isCurrent() && str.equals(clusterMediaHostActivity.packageName)) {
            return;
        }
        long jBegin = SESSION.begin(str);
        if (clusterMediaHostActivity != null) {
            clusterMediaHostActivity.close();
        }
        AppDisplayLauncher.cancel(str);
        SplitHostActivity.closeActiveHost();
        try {
            Intent intentPutExtra = new Intent(applicationContext, (Class<?>) ClusterMediaHostActivity.class).addFlags(402653184).putExtra(EXTRA_PACKAGE, str).putExtra(EXTRA_GENERATION, jBegin);
            ActivityOptions activityOptionsMakeBasic = ActivityOptions.makeBasic();
            activityOptionsMakeBasic.setLaunchDisplayId(iFindMediaDisplayId);
            applicationContext.startActivity(intentPutExtra, activityOptionsMakeBasic.toBundle());
        } catch (Exception e) {
            SESSION.end(jBegin);
            Log.e(TAG, "Cannot open media host", e);
            showError(applicationContext, "Не удалось открыть медиакарточку");
        }
    }

    static void closeForPackage(String str) {
        if (SESSION.cancelPackage(str)) {
            AppDisplayLauncher.cancel(str);
            ClusterMediaHostActivity clusterMediaHostActivity = active.get();
            if (clusterMediaHostActivity == null || !str.equals(clusterMediaHostActivity.packageName)) {
                return;
            }
            clusterMediaHostActivity.close();
        }
    }

    private static int findMediaDisplayId(Context context) {
        int i;
        DisplayManager displayManager = (DisplayManager) context.getSystemService(DisplayManager.class);
        if (displayManager != null) {
            for (Display display : displayManager.getDisplays()) {
                if (MEDIA_DISPLAY_NAME.equals(display.getName())) {
                    return display.getDisplayId();
                }
            }
        }
        try {
            for (ActivityManager.RunningTaskInfo runningTaskInfo : ((ActivityManager) context.getSystemService(ActivityManager.class)).getRunningTasks(100)) {
                if (runningTaskInfo.baseActivity != null && "com.qinggan.instrumentcard".equals(runningTaskInfo.baseActivity.getPackageName()) && "com.qinggan.instrumentcard.ScreenActivity".equals(runningTaskInfo.baseActivity.getClassName()) && (i = runningTaskInfo.getClass().getField("displayId").getInt(runningTaskInfo)) > 1) {
                    return i;
                }
            }
            return -1;
        } catch (Exception e) {
            Log.w(TAG, "Cannot locate OEM media display", e);
            return -1;
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.packageName = getIntent().getStringExtra(EXTRA_PACKAGE);
        this.generation = getIntent().getLongExtra(EXTRA_GENERATION, -1L);
        Display display = getDisplay();
        Point point = new Point();
        if (display != null) {
            display.getRealSize(point);
        }
        if (!SESSION.owns(this.generation, this.packageName) || display == null || !MEDIA_DISPLAY_NAME.equals(display.getName()) || point.x != 1920 || point.y != 720) {
            finishAndRemoveTask();
            return;
        }
        active = new WeakReference<>(this);
        getWindow().addFlags(1024);
        getWindow().getDecorView().setSystemUiVisibility(4102);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        TextureView textureView = new TextureView(this);
        textureView.setSurfaceTextureListener(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(WIDTH, HEIGHT, 51);
        layoutParams.leftMargin = 66;
        layoutParams.topMargin = 200;
        frameLayout.addView(textureView, layoutParams);
        setContentView(frameLayout);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCurrent() {
        return (this.closing || isFinishing() || isDestroyed() || !SESSION.owns(this.generation, this.packageName)) ? false : true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v1 */
    /* JADX WARN: Type inference failed for: r1v3, types: [java.lang.String] */
    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        Exception e;
        if (isCurrent()) {
            try {
                surfaceTexture.setDefaultBufferSize(WIDTH, HEIGHT);
                this.surface = new Surface(surfaceTexture);
                VirtualDisplay virtualDisplayCreateVirtualDisplay = ((DisplayManager) getSystemService(DisplayManager.class)).createVirtualDisplay("voyah-cluster-media", WIDTH, HEIGHT, DPI, this.surface, 1289, new VirtualDisplay.Callback() { // from class: ru.big.town.anative.ClusterMediaHostActivity.1
                    @Override // android.hardware.display.VirtualDisplay.Callback
                    public void onStopped() {
                        if (ClusterMediaHostActivity.this.closing) {
                            return;
                        }
                        ClusterMediaHostActivity.this.close();
                    }
                }, new Handler(Looper.getMainLooper()));
                this.virtualDisplay = virtualDisplayCreateVirtualDisplay;
                try {
                    if (virtualDisplayCreateVirtualDisplay == null) {
                        throw new IllegalStateException("VirtualDisplay unavailable");
                    }
                    virtualDisplayCreateVirtualDisplay.setSurface(this.surface);
                    int displayId = this.virtualDisplay.getDisplay().getDisplayId();
                    AppDisplayLauncher.launch(this, this.packageName, displayId, true, new BooleanSupplier() { // from class: ru.big.town.anative.ClusterMediaHostActivity$$ExternalSyntheticLambda0
                        @Override // java.util.function.BooleanSupplier
                        public final boolean getAsBoolean() {
                            return ClusterMediaHostActivity.this.isCurrent();
                        }
                    }, new Runnable() { // from class: ru.big.town.anative.ClusterMediaHostActivity$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            ClusterMediaHostActivity.this.launchFailed();
                        }
                    });
                    Log.i(TAG, "host pkg=" + this.packageName + " display=" + displayId + " bounds=66,200-640,664");
                    return;
                } catch (Exception e2) {
                    e = e2;
                }
            } catch (Exception e3) {
                e = e3;
            }
            Log.e(TAG, "Virtual display failed", e);
            this.launchFailed();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchFailed() {
        Context applicationContext = getApplicationContext();
        close();
        showError(applicationContext, "Не удалось открыть приложение в медиакарточке");
    }

    private static void showError(Context context, String str) {
        try {
            Context applicationContext = context.getApplicationContext();
            DisplayManager displayManager = (DisplayManager) applicationContext.getSystemService(DisplayManager.class);
            Display display = displayManager == null ? null : displayManager.getDisplay(0);
            if (display != null && display.isValid()) {
                Toast.makeText(applicationContext.createDisplayContext(display), str, 1).show();
                return;
            }
            Log.w(TAG, str);
        } catch (Exception e) {
            Log.w(TAG, "Cannot show failure notification: " + str, e);
        }
    }

    private void releaseDisplay() {
        VirtualDisplay virtualDisplay = this.virtualDisplay;
        if (virtualDisplay != null) {
            this.virtualDisplay = null;
            try {
                virtualDisplay.release();
            } catch (Exception e) {
                Log.w(TAG, "Display was already removed", e);
            }
        }
        Surface surface = this.surface;
        if (surface != null) {
            try {
                surface.release();
            } finally {
                this.surface = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void close() {
        if (this.closing) {
            return;
        }
        this.closing = true;
        SESSION.end(this.generation);
        AppDisplayLauncher.cancel(this.packageName);
        releaseDisplay();
        finishAndRemoveTask();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        this.closing = true;
        SESSION.end(this.generation);
        if (active.get() == this) {
            active = new WeakReference<>(null);
        }
        releaseDisplay();
        super.onDestroy();
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        close();
        return true;
    }
}
