package ru.big.town.anative;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.InputEvent;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes2.dex */
public class SplitHostActivity extends Activity {
    private static final String ACTION_SCREEN_LIFT_CHANGED = "action.qg.layout.changed";
    private static final boolean DIVIDER_RESIZE_GESTURE_ENABLED = false;
    public static final String EXTRA_LEFT = "leftPkg";
    public static final String EXTRA_LEFT_DPI = "leftDpi";
    public static final String EXTRA_PRESET_ID = "presetId";
    public static final String EXTRA_PRESET_IDX = "presetIdx";
    public static final String EXTRA_RATIO = "ratio";
    public static final String EXTRA_RESIZABLE = "resizable";
    public static final String EXTRA_RIGHT = "rightPkg";
    public static final String EXTRA_RIGHT_DPI = "rightDpi";
    public static final String EXTRA_SPLIT = "split";
    private static final long MASK_HOLD_MS = 450;
    private static final float MIN_PANE_DP = 260.0f;
    private static final int SCREEN_DOWN_HEIGHT_PX = 560;
    private static final int SCREEN_LIFT_DOWN = 1;
    private static final String SCREEN_LIFT_PROPERTY = "persist.qg.canbus.bcm_screenAutoLiftFdb";
    private static final String SCREEN_LIFT_SETTING = "voyahtune_screen_lift_type";
    private static final int SCREEN_LIFT_UP = 2;
    private static final int SCREEN_UP_HEIGHT_PX = 720;
    private static final String TAG = "$$$ SplitHostActivity $$$";
    private static final int VD_FLAGS_FALLBACK = 265;
    private static final int VD_FLAGS_TRUSTED = 1289;
    private static final long WATCH_GRACE_MS = 8000;
    private static final int WATCH_MAX_RESTARTS = 3;
    private static final long WATCH_PERIOD_MS = 2500;
    private static volatile WeakReference<SplitHostActivity> activeHost = new WeakReference<>(null);
    private DisplayManager displayManager;
    private boolean hostDestroyed;
    private View maskDivider;
    private View maskGrip;
    private ImageView maskLeft;
    private FrameLayout maskOverlay;
    private ImageView maskRight;
    private boolean screenLiftReceiverRegistered;
    private SplitHostTaskLane taskLane;
    private boolean watchActive;
    private SplitHostGenerationGate workGate;
    private int defaultDpi = 213;
    private boolean touchWarned = false;
    private int screenLiftType = 2;
    private final BroadcastReceiver screenLiftReceiver = new BroadcastReceiver() { // from class: ru.big.town.anative.SplitHostActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent == null || !SplitHostActivity.ACTION_SCREEN_LIFT_CHANGED.equals(intent.getAction())) {
                return;
            }
            int intExtra = intent.getIntExtra("type", 2);
            int screenLiftProperty = SplitHostActivity.this.readScreenLiftProperty(intExtra);
            if (screenLiftProperty != intExtra) {
                Log.w(SplitHostActivity.TAG, "screen lift broadcast ignored: type=" + intExtra + " property=" + screenLiftProperty);
            } else {
                SplitHostActivity.this.applyScreenLiftSize(intExtra);
            }
        }
    };
    private final Handler watchHandler = new Handler(Looper.getMainLooper());
    private final Runnable watchTick = new Runnable() { // from class: ru.big.town.anative.SplitHostActivity.2
        @Override // java.lang.Runnable
        public void run() {
            if (!SplitHostActivity.this.watchActive || SplitHostActivity.this.hostDestroyed) {
                return;
            }
            try {
                SplitHostActivity.this.requestSupervisionSnapshot();
            } catch (Exception e) {
                Log.w(SplitHostActivity.TAG, "supervise: " + e.getMessage());
            }
            if (!SplitHostActivity.this.watchActive || SplitHostActivity.this.hostDestroyed) {
                return;
            }
            SplitHostActivity.this.watchHandler.postDelayed(this, SplitHostActivity.WATCH_PERIOD_MS);
        }
    };
    private final Pane left = new Pane("L");
    private final Pane right = new Pane("R");
    private boolean resizable = false;
    private int presetIdx = -1;
    private String presetId = "";
    private ResizeState resizeState = ResizeState.IDLE;
    private boolean dragging = false;
    private boolean doubleTapConsumed = false;
    private float lastDragFraction = 0.5f;
    private int resizeGeneration = 0;
    private long resizeUntil = 0;

    private enum ResizeState {
        IDLE,
        DRAGGING,
        SETTLING
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class Pane {
        View container;
        int dpi;
        int h;
        boolean launchInFlight;
        boolean launched;
        long launchedAt;
        String pkg;
        long resizeVersion;
        int restarts;
        final String side;
        VirtualDisplay vd;
        SurfaceView view;
        int w;

        Pane(String str) {
            this.side = str;
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.taskLane = SplitHostTaskLane.get(getApplicationContext());
        this.workGate = new SplitHostGenerationGate(this.taskLane.registerHost(this));
        activeHost = new WeakReference<>(this);
        getWindow().addFlags(128);
        getWindow().setDecorFitsSystemWindows(false);
        setContentView(R.layout.activity_split_host);
        setRequestedOrientation(0);
        applyScreenLiftSize(readScreenLiftType());
        registerScreenLiftReceiver();
        applyWindowInsets();
        this.displayManager = (DisplayManager) getSystemService("display");
        this.defaultDpi = getResources().getDisplayMetrics().densityDpi;
        Intent intent = getIntent();
        this.left.pkg = intent.getStringExtra(EXTRA_LEFT);
        this.right.pkg = intent.getStringExtra(EXTRA_RIGHT);
        this.left.dpi = intent.getIntExtra(EXTRA_LEFT_DPI, 0);
        this.right.dpi = intent.getIntExtra(EXTRA_RIGHT_DPI, 0);
        int intExtra = intent.getIntExtra(EXTRA_RATIO, 1);
        this.resizable = false;
        this.presetIdx = intent.getIntExtra(EXTRA_PRESET_IDX, -1);
        this.presetId = intent.getStringExtra(EXTRA_PRESET_ID);
        float floatExtra = intent.getFloatExtra(EXTRA_SPLIT, 0.0f);
        this.left.container = findViewById(R.id.splitPaneLeft);
        this.right.container = findViewById(R.id.splitPaneRight);
        this.left.view = (SurfaceView) findViewById(R.id.splitSurfaceLeft);
        this.right.view = (SurfaceView) findViewById(R.id.splitSurfaceRight);
        boolean z = this.right.pkg == null || this.right.pkg.isEmpty();
        if (z) {
            findViewById(R.id.splitDivider).setVisibility(8);
            this.right.container.setVisibility(8);
            setWeight(this.left.container, 1.0f);
        } else if (this.resizable && floatExtra > 0.05f && floatExtra < 0.95f) {
            applyFraction(floatExtra);
        } else {
            applyRatioWeights(intExtra);
        }
        setupSurface(this.left);
        applyRoundedCorners(this.left.container);
        if (!z) {
            setupSurface(this.right);
            setupDivider();
            applyRoundedCorners(this.right.container);
        }
        Log.i(TAG, "onCreate single=" + z + " left=" + this.left.pkg + " right=" + this.right.pkg + " ratio=" + intExtra + " lDpi=" + this.left.dpi + " rDpi=" + this.right.dpi + " defaultDpi=" + this.defaultDpi + " resizable=" + this.resizable + " split=" + floatExtra + " presetId=" + this.presetId + " liftType=" + this.screenLiftType + " hostHeight=" + currentHostHeight());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyScreenLiftSize(int i) {
        this.screenLiftType = i != 1 ? 2 : 1;
        int iCurrentHostHeight = currentHostHeight();
        View viewFindViewById = findViewById(R.id.splitHostRoot);
        if (viewFindViewById == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = viewFindViewById.getLayoutParams();
        if (layoutParams.height != iCurrentHostHeight) {
            layoutParams.height = iCurrentHostHeight;
            viewFindViewById.setLayoutParams(layoutParams);
        }
        viewFindViewById.requestLayout();
        Log.i(TAG, "screen lift type=" + this.screenLiftType + " hostHeight=" + iCurrentHostHeight + " (active VDs resize from SurfaceView.surfaceChanged)");
    }

    private int currentHostHeight() {
        return this.screenLiftType == 1 ? SCREEN_DOWN_HEIGHT_PX : SCREEN_UP_HEIGHT_PX;
    }

    private int readScreenLiftType() {
        int screenLiftProperty = readScreenLiftProperty(0);
        if (screenLiftProperty == 1 || screenLiftProperty == 2) {
            return screenLiftProperty;
        }
        try {
            return Settings.Global.getInt(getContentResolver(), SCREEN_LIFT_SETTING, 2);
        } catch (RuntimeException unused) {
            return 2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int readScreenLiftProperty(int i) {
        try {
            int iIntValue = ((Integer) Class.forName("android.os.SystemProperties").getDeclaredMethod("getInt", String.class, Integer.TYPE).invoke(null, SCREEN_LIFT_PROPERTY, Integer.valueOf(i))).intValue();
            return (iIntValue == 1 || iIntValue == 2) ? iIntValue : i;
        } catch (ReflectiveOperationException | RuntimeException e) {
            Log.w(TAG, "screen lift property unavailable: " + e.getMessage());
            return i;
        }
    }

    private void registerScreenLiftReceiver() {
        try {
            ContextCompat.registerReceiver(this, this.screenLiftReceiver, new IntentFilter(ACTION_SCREEN_LIFT_CHANGED), 2);
            this.screenLiftReceiverRegistered = true;
        } catch (RuntimeException e) {
            Log.w(TAG, "screen lift receiver unavailable: " + e.getMessage());
        }
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        retireAsyncHostWork(false);
        setIntent(intent);
        recreate();
    }

    private void applyWindowInsets() {
        float f = getResources().getDisplayMetrics().density;
        final int iRound = Math.round(6.0f * f);
        final int iRound2 = Math.round(f * 145.0f);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splitHostRoot), new OnApplyWindowInsetsListener() { // from class: ru.big.town.anative.SplitHostActivity$$ExternalSyntheticLambda6
            @Override // androidx.core.view.OnApplyWindowInsetsListener
            public final WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                return this.f$0.m2102lambda$applyWindowInsets$0$rubigtownanativeSplitHostActivity(iRound2, iRound, view, windowInsetsCompat);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$applyWindowInsets$0$ru-big-town-anative-SplitHostActivity, reason: not valid java name */
    /* synthetic */ WindowInsetsCompat m2102lambda$applyWindowInsets$0$rubigtownanativeSplitHostActivity(int i, int i2, View view, WindowInsetsCompat windowInsetsCompat) {
        int identifier;
        Insets insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars());
        int dimensionPixelSize = insets.top;
        if (dimensionPixelSize == 0 && (identifier = getResources().getIdentifier("status_bar_height", "dimen", "android")) > 0) {
            dimensionPixelSize = getResources().getDimensionPixelSize(identifier);
        }
        view.setPadding(i + insets.left + i2, dimensionPixelSize + i2, insets.right + i2, insets.bottom + i2);
        return windowInsetsCompat;
    }

    private void applyRatioWeights(int i) {
        float f;
        float f2 = 2.0f;
        if (i == 0) {
            f = 3.0f;
        } else if (i == 2) {
            f = 4.0f;
        } else if (i != 3) {
            f = i != 4 ? 1.0f : 2.0f;
        } else {
            f = 5.0f;
        }
        if (i == 0) {
            f2 = 4.0f;
        } else if (i == 2) {
            f2 = 3.0f;
        } else if (i != 3) {
            f2 = i != 4 ? 1.0f : 5.0f;
        }
        setWeight(this.left.container, f);
        setWeight(this.right.container, f2);
    }

    private void setWeight(View view, float f) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        layoutParams.weight = f;
        view.setLayoutParams(layoutParams);
    }

    private void applyFraction(float f) {
        setWeight(this.left.container, f);
        setWeight(this.right.container, 1.0f - f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float currentFraction() {
        int width = this.left.container.getWidth();
        int width2 = this.right.container.getWidth() + width;
        if (width2 > 0) {
            return width / width2;
        }
        return 0.5f;
    }

    private void setupSurface(final Pane pane) {
        pane.view.getHolder().addCallback(new SurfaceHolder.Callback() { // from class: ru.big.town.anative.SplitHostActivity.3
            @Override // android.view.SurfaceHolder.Callback
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
                pane.w = i2;
                pane.h = i3;
                if (pane.vd == null) {
                    SplitHostActivity.this.createVirtualDisplay(pane, surfaceHolder.getSurface());
                    SplitHostActivity.this.launchApp(pane);
                    return;
                }
                try {
                    pane.vd.resize(i2, i3, SplitHostActivity.this.effectiveDpi(pane));
                    pane.resizeVersion++;
                } catch (Exception e) {
                    Log.w(SplitHostActivity.TAG, "resize " + pane.side + " failed: " + e.getMessage());
                }
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                SplitHostActivity.this.releasePane(pane);
            }
        });
        pane.view.setOnTouchListener(new View.OnTouchListener() { // from class: ru.big.town.anative.SplitHostActivity$$ExternalSyntheticLambda5
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return this.f$0.m2107lambda$setupSurface$1$rubigtownanativeSplitHostActivity(pane, view, motionEvent);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$setupSurface$1$ru-big-town-anative-SplitHostActivity, reason: not valid java name */
    /* synthetic */ boolean m2107lambda$setupSurface$1$rubigtownanativeSplitHostActivity(Pane pane, View view, MotionEvent motionEvent) {
        if (pane.vd == null) {
            return true;
        }
        injectTouch(pane, motionEvent);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createVirtualDisplay(Pane pane, Surface surface) {
        Surface surface2;
        int iEffectiveDpi = effectiveDpi(pane);
        String str = "voyah-split-" + pane.side;
        try {
            surface2 = surface;
            try {
                pane.vd = this.displayManager.createVirtualDisplay(str, pane.w, pane.h, iEffectiveDpi, surface2, VD_FLAGS_TRUSTED);
                Log.i(TAG, "VD " + pane.side + " (trusted) id=" + (pane.vd != null ? pane.vd.getDisplay().getDisplayId() : -1) + " " + pane.w + "x" + pane.h + " dpi=" + iEffectiveDpi);
            } catch (Exception e) {
                e = e;
                Log.w(TAG, "VD " + pane.side + " trusted failed (" + e.getMessage() + ") → fallback");
                try {
                    pane.vd = this.displayManager.createVirtualDisplay(str, pane.w, pane.h, iEffectiveDpi, surface2, VD_FLAGS_FALLBACK);
                    Log.i(TAG, "VD " + pane.side + " (fallback) id=" + (pane.vd != null ? pane.vd.getDisplay().getDisplayId() : -1));
                } catch (Exception e2) {
                    Log.e(TAG, "VD " + pane.side + " fallback failed: " + e2.getMessage());
                }
            }
        } catch (Exception e3) {
            e = e3;
            surface2 = surface;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchApp(Pane pane) {
        Integer numPaneDisplayId;
        if (pane.vd == null || pane.launched || pane.launchInFlight || pane.pkg == null || pane.pkg.isEmpty() || this.taskLane == null || this.workGate == null || (numPaneDisplayId = paneDisplayId(pane)) == null) {
            return;
        }
        int iPaneIndex = paneIndex(pane);
        long jNextPaneGeneration = this.workGate.nextPaneGeneration(iPaneIndex);
        if (jNextPaneGeneration == -1) {
            return;
        }
        pane.launched = true;
        pane.launchInFlight = true;
        pane.launchedAt = System.currentTimeMillis();
        this.taskLane.requestPaneLaunch(this, new SplitHostTaskLane.PaneTicket(this.workGate.hostGeneration(), iPaneIndex, jNextPaneGeneration, pane.pkg, numPaneDisplayId.intValue()));
    }

    void onPaneLaunchPrepared(final SplitHostTaskLane.PaneLaunchRequest paneLaunchRequest, Intent intent) {
        if (isPaneTicketCurrent(paneLaunchRequest.pane)) {
            Pane panePaneForIndex = paneForIndex(paneLaunchRequest.pane.paneIndex);
            if (intent == null) {
                panePaneForIndex.launchInFlight = false;
                panePaneForIndex.launched = false;
                Log.w(TAG, "нет launch intent для " + paneLaunchRequest.pane.packageName);
            } else {
                final Intent intent2 = new Intent(intent);
                if (this.watchHandler.postDelayed(new Runnable() { // from class: ru.big.town.anative.SplitHostActivity$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m2105xe9a888d6(paneLaunchRequest, intent2);
                    }
                }, MASK_HOLD_MS)) {
                    return;
                }
                panePaneForIndex.launchInFlight = false;
                panePaneForIndex.launched = false;
                Log.w(TAG, "main Handler rejected delayed launch for " + paneLaunchRequest.pane.packageName);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: startPreparedPane, reason: merged with bridge method [inline-methods] */
    public void m2105xe9a888d6(SplitHostTaskLane.PaneLaunchRequest paneLaunchRequest, Intent intent) {
        if (isPaneTicketCurrent(paneLaunchRequest.pane)) {
            Pane panePaneForIndex = paneForIndex(paneLaunchRequest.pane.paneIndex);
            try {
                intent.addFlags(402653184);
                ActivityOptions activityOptionsMakeBasic = ActivityOptions.makeBasic();
                activityOptionsMakeBasic.setLaunchDisplayId(paneLaunchRequest.pane.displayId);
                startActivity(intent, activityOptionsMakeBasic.toBundle());
                panePaneForIndex.launchInFlight = false;
                panePaneForIndex.launchedAt = System.currentTimeMillis();
                Log.i(TAG, "launched " + paneLaunchRequest.pane.packageName + " on display " + paneLaunchRequest.pane.displayId + " (after removeTask)");
            } catch (Exception e) {
                panePaneForIndex.launchInFlight = false;
                panePaneForIndex.launched = false;
                Log.e(TAG, "launchApp " + paneLaunchRequest.pane.packageName + " failed: " + e.getMessage());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestSupervisionSnapshot() {
        if (this.taskLane == null || this.workGate == null || this.dragging || System.currentTimeMillis() < this.resizeUntil) {
            return;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        ArrayList arrayList = new ArrayList(2);
        addSupervisionTicket(arrayList, this.left, jCurrentTimeMillis);
        addSupervisionTicket(arrayList, this.right, jCurrentTimeMillis);
        if (arrayList.isEmpty()) {
            return;
        }
        this.taskLane.requestSupervision(this, this.workGate.hostGeneration(), this.workGate.currentSupervisionGeneration(), arrayList);
    }

    private void addSupervisionTicket(List<SplitHostTaskLane.PaneTicket> list, Pane pane, long j) {
        Integer numPaneDisplayId;
        if (pane.vd == null || !pane.launched || pane.launchInFlight || j - pane.launchedAt < WATCH_GRACE_MS || (numPaneDisplayId = paneDisplayId(pane)) == null || pane.pkg == null || pane.pkg.isEmpty()) {
            return;
        }
        int iPaneIndex = paneIndex(pane);
        list.add(new SplitHostTaskLane.PaneTicket(this.workGate.hostGeneration(), iPaneIndex, this.workGate.currentPaneGeneration(iPaneIndex), pane.pkg, numPaneDisplayId.intValue()));
    }

    void onSupervisionSnapshot(SplitHostTaskLane.SupervisionRequest supervisionRequest, boolean z, SplitHostTaskSnapshot splitHostTaskSnapshot) {
        SplitHostGenerationGate splitHostGenerationGate = this.workGate;
        if (splitHostGenerationGate == null || !splitHostGenerationGate.acceptsSupervision(supervisionRequest.hostGeneration, supervisionRequest.supervisionGeneration) || !z || this.dragging || System.currentTimeMillis() < this.resizeUntil) {
            return;
        }
        for (SplitHostTaskLane.PaneTicket paneTicket : supervisionRequest.panes) {
            if (isPaneTicketCurrent(paneTicket)) {
                Pane panePaneForIndex = paneForIndex(paneTicket.paneIndex);
                if (!panePaneForIndex.launchInFlight && System.currentTimeMillis() - panePaneForIndex.launchedAt >= WATCH_GRACE_MS) {
                    supervisePane(panePaneForIndex, splitHostTaskSnapshot.isAlive(paneTicket.packageName, paneTicket.displayId));
                }
            }
        }
    }

    private void supervisePane(Pane pane, boolean z) {
        if (z) {
            pane.restarts = 0;
            return;
        }
        if (pane.restarts >= 3) {
            if (pane.restarts == 3) {
                pane.restarts++;
                Log.w(TAG, "надзиратель: " + pane.pkg + " (" + pane.side + ") не удержался после 3 попыток — перезапуск прекращён");
                return;
            }
            return;
        }
        pane.restarts++;
        Log.i(TAG, "надзиратель: " + pane.pkg + " (" + pane.side + ") пропал со своего дисплея → перезапуск " + pane.restarts + "/3");
        pane.launched = false;
        launchApp(pane);
    }

    private boolean isPaneTicketCurrent(SplitHostTaskLane.PaneTicket paneTicket) {
        Integer numPaneDisplayId;
        SplitHostGenerationGate splitHostGenerationGate = this.workGate;
        if (splitHostGenerationGate != null && !this.hostDestroyed && splitHostGenerationGate.acceptsPane(paneTicket.hostGeneration, paneTicket.paneIndex, paneTicket.paneGeneration)) {
            Pane panePaneForIndex = paneForIndex(paneTicket.paneIndex);
            if (panePaneForIndex.vd != null && paneTicket.packageName.equals(panePaneForIndex.pkg) && (numPaneDisplayId = paneDisplayId(panePaneForIndex)) != null && numPaneDisplayId.intValue() == paneTicket.displayId) {
                return true;
            }
        }
        return false;
    }

    private Integer paneDisplayId(Pane pane) {
        try {
            if (pane.vd == null || pane.vd.getDisplay() == null) {
                return null;
            }
            return Integer.valueOf(pane.vd.getDisplay().getDisplayId());
        } catch (Exception unused) {
            return null;
        }
    }

    private int paneIndex(Pane pane) {
        return pane == this.left ? 0 : 1;
    }

    private Pane paneForIndex(int i) {
        return i == 0 ? this.left : this.right;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int effectiveDpi(Pane pane) {
        return pane.dpi > 0 ? pane.dpi : this.defaultDpi;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releasePane(Pane pane) {
        SplitHostGenerationGate splitHostGenerationGate = this.workGate;
        if (splitHostGenerationGate != null) {
            splitHostGenerationGate.invalidatePane(paneIndex(pane));
        }
        pane.launchInFlight = false;
        pane.launched = false;
        pane.restarts = 0;
        if (pane.vd != null) {
            try {
                pane.vd.release();
            } catch (Exception unused) {
            }
            pane.vd = null;
        }
    }

    private void injectTouch(Pane pane, MotionEvent motionEvent) {
        MotionEvent motionEventObtain = null;
        try {
            int displayId = pane.vd.getDisplay().getDisplayId();
            motionEventObtain = MotionEvent.obtain(motionEvent);
            MotionEvent.class.getMethod("setDisplayId", Integer.TYPE).invoke(motionEventObtain, Integer.valueOf(displayId));
            Object systemService = getSystemService("input");
            systemService.getClass().getMethod("injectInputEvent", InputEvent.class, Integer.TYPE).invoke(systemService, motionEventObtain, 0);
        } catch (Exception e) {
            if (!this.touchWarned) {
                this.touchWarned = true;
                Log.w(TAG, "injectTouch недоступен (нет INJECT_EVENTS у Native): " + e.getMessage() + " — ввод в VD требует root+Frida-в-system_server или роутинга WM для trusted-дисплея");
            }
        } finally {
            if (motionEventObtain != null) {
                motionEventObtain.recycle();
            }
        }
    }

    private void setupDivider() {
        View viewFindViewById = findViewById(R.id.splitDivider);
        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() { // from class: ru.big.town.anative.SplitHostActivity.4
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onDown(MotionEvent motionEvent) {
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
            public boolean onDoubleTap(MotionEvent motionEvent) {
                SplitHostActivity.this.cancelResizeGesture();
                SplitHostActivity.this.swapApps();
                SplitHostActivity.this.doubleTapConsumed = true;
                return true;
            }
        });
        final View viewFindViewById2 = findViewById(R.id.splitHandleGrip);
        Log.i(TAG, "setupDivider: resizable=" + this.resizable + " presetIdx=" + this.presetIdx + " presetId=" + this.presetId);
        if (!this.resizable) {
            viewFindViewById.setOnTouchListener(new View.OnTouchListener() { // from class: ru.big.town.anative.SplitHostActivity$$ExternalSyntheticLambda2
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    return this.f$0.m2106lambda$setupDivider$3$rubigtownanativeSplitHostActivity(viewFindViewById2, gestureDetector, view, motionEvent);
                }
            });
        } else {
            viewFindViewById.setOnTouchListener(new View.OnTouchListener() { // from class: ru.big.town.anative.SplitHostActivity.5
                float startFraction;
                float startX;

                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    gestureDetector.onTouchEvent(motionEvent);
                    int actionMasked = motionEvent.getActionMasked();
                    if (actionMasked == 0) {
                        if (SplitHostActivity.this.doubleTapConsumed) {
                            SplitHostActivity.this.doubleTapConsumed = false;
                            return true;
                        }
                        this.startX = motionEvent.getRawX();
                        float fCurrentFraction = SplitHostActivity.this.currentFraction();
                        this.startFraction = fCurrentFraction;
                        SplitHostActivity.this.lastDragFraction = fCurrentFraction;
                        SplitHostActivity.this.gripPressed(viewFindViewById2, true);
                        Log.i(SplitHostActivity.TAG, "драг начат: fraction=" + this.startFraction);
                        try {
                            SplitHostActivity.this.beginResize();
                        } catch (Throwable th) {
                            Log.w(SplitHostActivity.TAG, "beginResize: " + th);
                        }
                        SplitHostActivity.this.moveDivider(this.startFraction);
                        return true;
                    }
                    if (actionMasked == 1) {
                        SplitHostActivity.this.gripPressed(viewFindViewById2, false);
                        if (SplitHostActivity.this.resizeState == ResizeState.DRAGGING) {
                            SplitHostActivity splitHostActivity = SplitHostActivity.this;
                            splitHostActivity.lastDragFraction = splitHostActivity.fractionForDx(this.startFraction, motionEvent.getRawX() - this.startX);
                            SplitHostActivity splitHostActivity2 = SplitHostActivity.this;
                            splitHostActivity2.endResize(splitHostActivity2.lastDragFraction);
                        }
                        return true;
                    }
                    if (actionMasked != 2) {
                        if (actionMasked != 3) {
                            return false;
                        }
                        SplitHostActivity.this.gripPressed(viewFindViewById2, false);
                        SplitHostActivity.this.cancelResizeGesture();
                        return true;
                    }
                    if (SplitHostActivity.this.resizeState != ResizeState.DRAGGING) {
                        return true;
                    }
                    float fFractionForDx = SplitHostActivity.this.fractionForDx(this.startFraction, motionEvent.getRawX() - this.startX);
                    SplitHostActivity.this.lastDragFraction = fFractionForDx;
                    SplitHostActivity.this.moveDivider(fFractionForDx);
                    try {
                        SplitHostActivity.this.previewFraction(fFractionForDx);
                    } catch (Throwable th2) {
                        Log.w(SplitHostActivity.TAG, "preview: " + th2);
                    }
                    return true;
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$setupDivider$3$ru-big-town-anative-SplitHostActivity, reason: not valid java name */
    /* synthetic */ boolean m2106lambda$setupDivider$3$rubigtownanativeSplitHostActivity(View view, GestureDetector gestureDetector, View view2, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            gripPressed(view, true);
            Log.i(TAG, "делитель нажат, но пропорция зафиксирована (resizable=false)");
        } else if (motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 3) {
            gripPressed(view, false);
        }
        gestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveDivider(float f) {
        int width;
        View viewFindViewById = findViewById(R.id.splitPanes);
        View viewFindViewById2 = findViewById(R.id.splitDivider);
        if (viewFindViewById == null || viewFindViewById2 == null || (width = viewFindViewById.getWidth() - viewFindViewById2.getWidth()) <= 0) {
            return;
        }
        viewFindViewById2.setTranslationX(Math.round(width * f) - this.left.container.getWidth());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void gripPressed(View view, boolean z) {
        if (view == null) {
            return;
        }
        float f = z ? 1.6f : 1.0f;
        view.setScaleX(f);
        view.setScaleY(f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float fractionForDx(float f, float f2) {
        int width = findViewById(R.id.splitPanes).getWidth() - findViewById(R.id.splitDivider).getWidth();
        if (width <= 0) {
            return f;
        }
        float f3 = width;
        float fEffectiveDpi = ((effectiveDpi(this.left) * MIN_PANE_DP) / 160.0f) / f3;
        float fEffectiveDpi2 = ((effectiveDpi(this.right) * MIN_PANE_DP) / 160.0f) / f3;
        float f4 = fEffectiveDpi + fEffectiveDpi2;
        if (f4 > 0.9f) {
            float f5 = 0.9f / f4;
            fEffectiveDpi *= f5;
            fEffectiveDpi2 *= f5;
        }
        return Math.max(fEffectiveDpi, Math.min(1.0f - fEffectiveDpi2, f + (f2 / f3)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void beginResize() {
        this.resizeGeneration++;
        this.resizeState = ResizeState.DRAGGING;
        this.dragging = true;
        this.resizeUntil = System.currentTimeMillis() + 60000;
        ensureMaskOverlay();
        gripPressed(this.maskGrip, true);
        FrameLayout frameLayout = this.maskOverlay;
        if (frameLayout != null) {
            frameLayout.animate().cancel();
        }
        ImageView imageView = this.maskLeft;
        if (imageView != null) {
            imageView.setImageDrawable(null);
        }
        ImageView imageView2 = this.maskRight;
        if (imageView2 != null) {
            imageView2.setImageDrawable(null);
        }
        captureBlurred(this.left, this.maskLeft, this.resizeGeneration);
        captureBlurred(this.right, this.maskRight, this.resizeGeneration);
        this.maskOverlay.setVisibility(0);
        this.maskOverlay.setAlpha(1.0f);
        previewFraction(currentFraction());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void previewFraction(float f) {
        if (this.maskOverlay == null) {
            return;
        }
        View viewFindViewById = findViewById(R.id.splitPanes);
        View viewFindViewById2 = findViewById(R.id.splitDivider);
        int width = viewFindViewById.getWidth() - viewFindViewById2.getWidth();
        if (width <= 0) {
            return;
        }
        int iRound = Math.round(width * f);
        setLp(this.maskLeft, iRound, 0);
        setLp(this.maskRight, width - iRound, viewFindViewById2.getWidth() + iRound);
        setLp(this.maskDivider, viewFindViewById2.getWidth(), iRound);
    }

    private void setLp(View view, int i, int i2) {
        if (view == null) {
            return;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.width = i;
        layoutParams.leftMargin = i2;
        view.setLayoutParams(layoutParams);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void endResize(float f) {
        if (this.resizeState != ResizeState.DRAGGING) {
            return;
        }
        this.dragging = false;
        this.resizeState = ResizeState.SETTLING;
        gripPressed(this.maskGrip, false);
        int i = this.resizeGeneration;
        long j = this.left.resizeVersion;
        long j2 = this.right.resizeVersion;
        View viewFindViewById = findViewById(R.id.splitDivider);
        if (viewFindViewById != null) {
            viewFindViewById.setTranslationX(0.0f);
        }
        applyFraction(f);
        long jCurrentTimeMillis = System.currentTimeMillis();
        this.left.launchedAt = jCurrentTimeMillis;
        this.right.launchedAt = jCurrentTimeMillis;
        this.resizeUntil = jCurrentTimeMillis + WATCH_GRACE_MS;
        saveFraction(f);
        waitForSurfaceResize(i, j, j2, 0);
    }

    private void waitForSurfaceResize(final int i, final long j, final long j2, final int i2) {
        View viewFindViewById = findViewById(R.id.splitHostRoot);
        if (viewFindViewById == null) {
            return;
        }
        if ((this.left.resizeVersion <= j || this.right.resizeVersion <= j2) && i2 < 20) {
            viewFindViewById.postDelayed(new Runnable() { // from class: ru.big.town.anative.SplitHostActivity$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m2108x9113438e(i, j, j2, i2);
                }
            }, 50L);
        } else {
            viewFindViewById.postDelayed(new Runnable() { // from class: ru.big.town.anative.SplitHostActivity$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m2109x5a143acf(i);
                }
            }, MASK_HOLD_MS);
        }
    }

    /* JADX INFO: renamed from: lambda$waitForSurfaceResize$4$ru-big-town-anative-SplitHostActivity, reason: not valid java name */
    /* synthetic */ void m2108x9113438e(int i, long j, long j2, int i2) {
        waitForSurfaceResize(i, j, j2, i2 + 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: finishResizeVisual, reason: merged with bridge method [inline-methods] */
    public void m2109x5a143acf(final int i) {
        if (i == this.resizeGeneration && this.resizeState == ResizeState.SETTLING) {
            Runnable runnable = new Runnable() { // from class: ru.big.town.anative.SplitHostActivity$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m2104x3b0cd38a(i);
                }
            };
            FrameLayout frameLayout = this.maskOverlay;
            if (frameLayout == null) {
                runnable.run();
            } else {
                frameLayout.animate().cancel();
                this.maskOverlay.animate().alpha(0.0f).setDuration(180L).withEndAction(runnable).start();
            }
        }
    }

    /* JADX INFO: renamed from: lambda$finishResizeVisual$6$ru-big-town-anative-SplitHostActivity, reason: not valid java name */
    /* synthetic */ void m2104x3b0cd38a(int i) {
        if (i != this.resizeGeneration) {
            return;
        }
        FrameLayout frameLayout = this.maskOverlay;
        if (frameLayout != null) {
            frameLayout.setVisibility(8);
        }
        this.resizeState = ResizeState.IDLE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelResizeGesture() {
        this.resizeGeneration++;
        this.dragging = false;
        this.resizeState = ResizeState.IDLE;
        gripPressed(this.maskGrip, false);
        View viewFindViewById = findViewById(R.id.splitDivider);
        if (viewFindViewById != null) {
            viewFindViewById.setTranslationX(0.0f);
        }
        FrameLayout frameLayout = this.maskOverlay;
        if (frameLayout != null) {
            frameLayout.animate().cancel();
            this.maskOverlay.setVisibility(8);
        }
        this.resizeUntil = 0L;
    }

    private void saveFraction(float f) {
        String str;
        if (this.presetIdx >= 0 || !((str = this.presetId) == null || str.isEmpty())) {
            try {
                Intent intent = new Intent("ru.big.town.restoremode.SPLIT_RATIO_SAVE");
                intent.setClassName("ru.big.town.restoremode", "ru.big.town.restoremode.SplitRatioSaveReceiver");
                intent.putExtra(EXTRA_PRESET_IDX, this.presetIdx);
                String str2 = this.presetId;
                if (str2 == null) {
                    str2 = "";
                }
                intent.putExtra(EXTRA_PRESET_ID, str2);
                intent.putExtra(EXTRA_SPLIT, f);
                intent.addFlags(32);
                sendBroadcast(intent);
            } catch (Exception e) {
                Log.w(TAG, "saveFraction: " + e.getMessage());
            }
        }
    }

    private void ensureMaskOverlay() {
        if (this.maskOverlay != null) {
            return;
        }
        FrameLayout frameLayout = new FrameLayout(this);
        this.maskOverlay = frameLayout;
        frameLayout.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        this.maskOverlay.setClickable(false);
        this.maskLeft = newMaskImage();
        this.maskRight = newMaskImage();
        FrameLayout frameLayout2 = new FrameLayout(this);
        frameLayout2.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        frameLayout2.setLayoutParams(new FrameLayout.LayoutParams(0, -1));
        this.maskDivider = frameLayout2;
        this.maskGrip = new View(this);
        float f = getResources().getDisplayMetrics().density;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(Math.round(7.0f * f), Math.round(f * 80.0f));
        layoutParams.gravity = 17;
        this.maskGrip.setLayoutParams(layoutParams);
        this.maskGrip.setBackgroundResource(R.drawable.split_handle_grip);
        frameLayout2.addView(this.maskGrip);
        this.maskOverlay.addView(this.maskLeft);
        this.maskOverlay.addView(this.maskRight);
        this.maskOverlay.addView(this.maskDivider);
        ((ViewGroup) findViewById(R.id.splitHostRoot)).addView(this.maskOverlay, new FrameLayout.LayoutParams(-1, -1));
        this.maskOverlay.setVisibility(8);
    }

    private ImageView newMaskImage() {
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(0, -1));
        applyRoundedCorners(imageView);
        return imageView;
    }

    private void captureBlurred(Pane pane, final ImageView imageView, final int i) {
        final Pane pane2;
        if (pane.view == null || pane.view.getWidth() <= 0) {
            return;
        }
        try {
            final Bitmap bitmapCreateBitmap = Bitmap.createBitmap(Math.max(16, pane.view.getWidth() / 8), Math.max(16, pane.view.getHeight() / 8), Bitmap.Config.ARGB_8888);
            pane2 = pane;
            try {
                PixelCopy.request(pane.view, bitmapCreateBitmap, new PixelCopy.OnPixelCopyFinishedListener() { // from class: ru.big.town.anative.SplitHostActivity$$ExternalSyntheticLambda4
                    @Override // android.view.PixelCopy.OnPixelCopyFinishedListener
                    public final void onPixelCopyFinished(int i2) {
                        this.f$0.m2103lambda$captureBlurred$7$rubigtownanativeSplitHostActivity(bitmapCreateBitmap, pane2, i, imageView, i2);
                    }
                }, new Handler(Looper.getMainLooper()));
            } catch (Exception e) {
                e = e;
                Log.w(TAG, "captureBlurred " + pane2.side + ": " + e.getMessage());
            }
        } catch (Exception e2) {
            e = e2;
            pane2 = pane;
        }
    }

    /* JADX INFO: renamed from: lambda$captureBlurred$7$ru-big-town-anative-SplitHostActivity, reason: not valid java name */
    /* synthetic */ void m2103lambda$captureBlurred$7$rubigtownanativeSplitHostActivity(Bitmap bitmap, Pane pane, int i, ImageView imageView, int i2) {
        if (i2 != 0) {
            bitmap.recycle();
            Log.w(TAG, "PixelCopy " + pane.side + " = " + i2);
        } else if (i != this.resizeGeneration || this.resizeState != ResizeState.DRAGGING) {
            bitmap.recycle();
        } else {
            boxBlur(bitmap, 3);
            imageView.setImageBitmap(bitmap);
        }
    }

    private static void boxBlur(Bitmap bitmap, int i) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width < 3 || height < 3) {
            return;
        }
        int i2 = width * height;
        int[] iArr = new int[i2];
        bitmap.getPixels(iArr, 0, width, 0, 0, width, height);
        int[] iArr2 = new int[i2];
        for (int i3 = 0; i3 < i; i3++) {
            blurPass(iArr, iArr2, width, height, 2);
            blurPass(iArr2, iArr, height, width, 2);
        }
        bitmap.setPixels(iArr, 0, width, 0, 0, width, height);
    }

    private static void blurPass(int[] iArr, int[] iArr2, int i, int i2, int i3) {
        for (int i4 = 0; i4 < i2; i4++) {
            for (int i5 = 0; i5 < i; i5++) {
                int i6 = 0;
                int i7 = 0;
                int i8 = 0;
                int i9 = 0;
                int i10 = 0;
                for (int i11 = -i3; i11 <= i3; i11++) {
                    int i12 = i5 + i11;
                    if (i12 >= 0 && i12 < i) {
                        int i13 = iArr[(i4 * i) + i12];
                        i6 += i13 >>> 24;
                        i8 += (i13 >> 16) & 255;
                        i9 += (i13 >> 8) & 255;
                        i10 += i13 & 255;
                        i7++;
                    }
                }
                iArr2[(i5 * i2) + i4] = ((i6 / i7) << 24) | ((i8 / i7) << 16) | ((i9 / i7) << 8) | (i10 / i7);
            }
        }
    }

    private void applyRoundedCorners(View view) {
        if (view == null) {
            return;
        }
        final float f = getResources().getDisplayMetrics().density * 18.0f;
        view.setOutlineProvider(new ViewOutlineProvider() { // from class: ru.big.town.anative.SplitHostActivity.6
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view2, Outline outline) {
                outline.setRoundRect(0, 0, view2.getWidth(), view2.getHeight(), f);
            }
        });
        view.setClipToOutline(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void swapApps() {
        String str = this.left.pkg;
        this.left.pkg = this.right.pkg;
        this.right.pkg = str;
        int i = this.left.dpi;
        this.left.dpi = this.right.dpi;
        this.right.dpi = i;
        Log.i(TAG, "swapApps → left=" + this.left.pkg + " right=" + this.right.pkg);
        recreatePane(this.left);
        recreatePane(this.right);
    }

    private void recreatePane(Pane pane) {
        releasePane(pane);
        Surface surface = pane.view.getHolder().getSurface();
        if (surface == null || !surface.isValid() || pane.w <= 0 || pane.h <= 0) {
            return;
        }
        createVirtualDisplay(pane, surface);
        launchApp(pane);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onSupersededByHost(long j) {
        SplitHostGenerationGate splitHostGenerationGate;
        if (this.hostDestroyed || (splitHostGenerationGate = this.workGate) == null || splitHostGenerationGate.hostGeneration() >= j) {
            return;
        }
        retireAsyncHostWork(true);
        Log.i(TAG, "host generation " + this.workGate.hostGeneration() + " superseded by " + j + " — finishing old host");
        finish();
    }

    private void retireAsyncHostWork(boolean z) {
        this.hostDestroyed = true;
        this.watchActive = false;
        this.watchHandler.removeCallbacksAndMessages(null);
        SplitHostGenerationGate splitHostGenerationGate = this.workGate;
        if (splitHostGenerationGate != null) {
            splitHostGenerationGate.close();
            SplitHostTaskLane splitHostTaskLane = this.taskLane;
            if (splitHostTaskLane != null) {
                if (z) {
                    splitHostTaskLane.cancelHost(this.workGate.hostGeneration());
                } else {
                    splitHostTaskLane.cancelHostWork(this.workGate.hostGeneration());
                }
            }
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        if (this.hostDestroyed) {
            return;
        }
        this.watchActive = true;
        SplitHostGenerationGate splitHostGenerationGate = this.workGate;
        if (splitHostGenerationGate != null) {
            splitHostGenerationGate.resumeSupervision();
        }
        this.watchHandler.removeCallbacks(this.watchTick);
        this.watchHandler.postDelayed(this.watchTick, WATCH_PERIOD_MS);
    }

    @Override // android.app.Activity
    protected void onPause() {
        this.watchActive = false;
        this.watchHandler.removeCallbacks(this.watchTick);
        SplitHostGenerationGate splitHostGenerationGate = this.workGate;
        if (splitHostGenerationGate != null) {
            splitHostGenerationGate.pauseSupervision();
            SplitHostTaskLane splitHostTaskLane = this.taskLane;
            if (splitHostTaskLane != null) {
                splitHostTaskLane.cancelSupervision(this.workGate.hostGeneration());
            }
        }
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        if (activeHost.get() == this) {
            activeHost = new WeakReference<>(null);
        }
        retireAsyncHostWork(true);
        cancelResizeGesture();
        releasePane(this.left);
        releasePane(this.right);
        if (this.screenLiftReceiverRegistered) {
            try {
                unregisterReceiver(this.screenLiftReceiver);
            } catch (IllegalArgumentException unused) {
            }
            this.screenLiftReceiverRegistered = false;
        }
        super.onDestroy();
    }

    static boolean closeActiveHost() {
        final SplitHostActivity splitHostActivity = activeHost.get();
        if (splitHostActivity == null || splitHostActivity.isFinishing() || splitHostActivity.isDestroyed()) {
            return false;
        }
        activeHost = new WeakReference<>(null);
        Objects.requireNonNull(splitHostActivity);
        splitHostActivity.runOnUiThread(new Runnable() { // from class: ru.big.town.anative.SplitHostActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.finishAndRemoveTask();
            }
        });
        Log.i(TAG, "active VD host closed before physical window launch");
        return true;
    }

    public static void launchSingle(Context context, String str, int i, int i2) {
        if (context == null || str == null || str.isEmpty()) {
            Log.w(TAG, "launchSingle: пустой пакет — пропуск");
            return;
        }
        if (i2 != 0 && i2 != 1) {
            i2 = 0;
        }
        SplitHostTaskLane.get(context).requestSingleHost(str, Math.max(0, i), i2);
    }

    static void startSingleHost(Context context, String str, int i, int i2) {
        try {
            Intent intent = new Intent(context, (Class<?>) SplitHostActivity.class);
            intent.addFlags(335544320);
            intent.putExtra(EXTRA_LEFT, str);
            intent.putExtra(EXTRA_RIGHT, "");
            intent.putExtra(EXTRA_RATIO, 1);
            intent.putExtra(EXTRA_LEFT_DPI, Math.max(0, i));
            intent.putExtra(EXTRA_RIGHT_DPI, 0);
            ActivityOptions activityOptionsMakeBasic = ActivityOptions.makeBasic();
            activityOptionsMakeBasic.setLaunchDisplayId(i2);
            context.startActivity(intent, activityOptionsMakeBasic.toBundle());
            Log.i(TAG, "launchSingle host started pkg=" + str + " dpi=" + i + " display=" + i2);
        } catch (Exception e) {
            Log.e(TAG, "launchSingle failed: " + e.getMessage());
        }
    }

    public static void launchSplit(Context context, String str, String str2, int i, int i2, int i3) {
        launchSplit(context, str, str2, i, i2, i3, false, 0.0f, -1, "");
    }

    public static void launchSplit(Context context, String str, String str2, int i, int i2, int i3, boolean z, float f, int i4) {
        launchSplit(context, str, str2, i, i2, i3, z, f, i4, "");
    }

    public static void launchSplit(Context context, String str, String str2, int i, int i2, int i3, boolean z, float f, int i4, String str3) {
        if (str == null || str.isEmpty() || str2 == null || str2.isEmpty()) {
            Log.w(TAG, "launchSplit: пустой пакет — пропуск");
            return;
        }
        try {
            Settings.Global.putInt(context.getContentResolver(), "enable_freeform_support", 1);
            Settings.Global.putInt(context.getContentResolver(), "force_resizable_activities", 1);
        } catch (Exception e) {
            Log.w(TAG, "launchSplit freeform settings: " + e.getMessage());
        }
        try {
            Intent intent = new Intent(context, (Class<?>) SplitHostActivity.class);
            intent.addFlags(335544320);
            intent.putExtra(EXTRA_LEFT, str);
            intent.putExtra(EXTRA_RIGHT, str2);
            intent.putExtra(EXTRA_RATIO, i);
            intent.putExtra(EXTRA_LEFT_DPI, i2);
            intent.putExtra(EXTRA_RIGHT_DPI, i3);
            intent.putExtra(EXTRA_RESIZABLE, z);
            intent.putExtra(EXTRA_SPLIT, f);
            intent.putExtra(EXTRA_PRESET_IDX, i4);
            intent.putExtra(EXTRA_PRESET_ID, str3);
            DockLaunchGuard.arm(context, 0, BuildConfig.APPLICATION_ID);
            context.startActivity(intent);
            Log.i(TAG, "launchSplit host started " + str + "/" + str2);
        } catch (Exception e2) {
            Log.e(TAG, "launchSplit failed: " + e2.getMessage());
        }
    }
}
