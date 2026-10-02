package ru.big.town.anative;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes2.dex */
final class SplitHostTaskLane {
    private static final String TAG = "$$$ SplitHostTasks $$$";
    private static final int TASK_QUERY_LIMIT = 100;
    private static final long WATCH_SETTING_CACHE_MS = 30000;
    private static volatile SplitHostTaskLane instance;
    private final ActivityManager activityManager;
    private Object activityTaskManager;
    private final Context appContext;
    private Field displayField;
    private boolean displayFieldResolved;
    private long latestSingleHostSequence;
    private long latestSupervisionHost;
    private final LatestValueDelivery<PaneLaunchResult> leftPaneResults;
    private long nextSequence;
    private Method removeTaskMethod;
    private final LatestValueDelivery<PaneLaunchResult> rightPaneResults;
    private final LatestValueDelivery<SingleHostResult> singleHostResults;
    private final LatestValueDelivery<SupervisionResult> supervisionResults;
    private long watchSettingExpiresAt;
    private final Handler worker;
    private final Handler main = new Handler(Looper.getMainLooper());
    private final SplitHostHostLease<SplitHostActivity> hostLease = new SplitHostHostLease<>();
    private final Object lock = new Object();
    private final SplitHostPendingSlots<SingleHostRequest> singleHostWork = new SplitHostPendingSlots<>(1);
    private final SplitHostPendingSlots<PaneLaunchRequest> paneLaunchWork = new SplitHostPendingSlots<>(2);
    private final long[] latestPaneSequence = new long[2];
    private final long[] latestPaneHost = new long[2];
    private final SplitHostPendingSlots<SupervisionRequest> supervisionWork = new SplitHostPendingSlots<>(1);
    private boolean cachedWatchEnabled = true;
    private final Runnable singleHostDrain = new Runnable() { // from class: ru.big.town.anative.SplitHostTaskLane.1
        @Override // java.lang.Runnable
        public void run() {
            SingleHostRequest singleHostRequest;
            synchronized (SplitHostTaskLane.this.lock) {
                singleHostRequest = (SingleHostRequest) SplitHostTaskLane.this.singleHostWork.take(0);
            }
            if (singleHostRequest != null) {
                try {
                    if (SplitHostTaskLane.this.isLatestSingleHost(singleHostRequest)) {
                        boolean z = SplitHostTaskLane.this.resolveLaunchIntent(singleHostRequest.packageName) != null;
                        if (z && SplitHostTaskLane.this.isLatestSingleHost(singleHostRequest)) {
                            DockLaunchGuard.arm(SplitHostTaskLane.this.appContext, singleHostRequest.displayId, BuildConfig.APPLICATION_ID);
                        }
                        SplitHostTaskLane.this.singleHostResults.offer(1L, singleHostRequest.sequence, new SingleHostResult(singleHostRequest, z));
                    }
                } catch (Throwable th) {
                    try {
                        Log.w(SplitHostTaskLane.TAG, "single-host preflight failed: " + SplitHostTaskLane.rootCause(th));
                    } finally {
                        SplitHostTaskLane.this.finishSingleHostDrain();
                    }
                }
            }
        }
    };
    private final Runnable paneLaunchDrain = new Runnable() { // from class: ru.big.town.anative.SplitHostTaskLane.2
        @Override // java.lang.Runnable
        public void run() {
            int i;
            PaneLaunchRequest[] paneLaunchRequestArr = new PaneLaunchRequest[2];
            synchronized (SplitHostTaskLane.this.lock) {
                paneLaunchRequestArr[0] = (PaneLaunchRequest) SplitHostTaskLane.this.paneLaunchWork.take(0);
                paneLaunchRequestArr[1] = (PaneLaunchRequest) SplitHostTaskLane.this.paneLaunchWork.take(1);
            }
            try {
                SplitHostTaskLane.this.processPaneLaunchBatch(paneLaunchRequestArr);
            } catch (Throwable th) {
                try {
                    Log.w(SplitHostTaskLane.TAG, "pane launch batch failed: " + SplitHostTaskLane.rootCause(th));
                    for (i = 0; i < 2; i++) {
                        PaneLaunchRequest paneLaunchRequest = paneLaunchRequestArr[i];
                        if (paneLaunchRequest != null) {
                            SplitHostTaskLane.this.postPaneLaunchResult(paneLaunchRequest, null);
                        }
                    }
                } catch (Throwable th2) {
                    SplitHostTaskLane.this.finishPaneLaunchDrain();
                    throw th2;
                }
            }
            SplitHostTaskLane.this.finishPaneLaunchDrain();
        }
    };
    private final Runnable supervisionDrain = new Runnable() { // from class: ru.big.town.anative.SplitHostTaskLane.3
        @Override // java.lang.Runnable
        public void run() {
            SupervisionRequest supervisionRequest;
            synchronized (SplitHostTaskLane.this.lock) {
                supervisionRequest = (SupervisionRequest) SplitHostTaskLane.this.supervisionWork.take(0);
            }
            if (supervisionRequest != null) {
                try {
                    if (SplitHostTaskLane.this.isCurrentSupervisionHost(supervisionRequest)) {
                        boolean watchEnabled = SplitHostTaskLane.this.readWatchEnabled();
                        if (SplitHostTaskLane.this.isCurrentSupervisionHost(supervisionRequest)) {
                            SplitHostTaskLane.this.postSupervisionResult(supervisionRequest, watchEnabled, watchEnabled ? SplitHostTaskLane.this.queryTasks().snapshot : SplitHostTaskSnapshot.unknown());
                        }
                    }
                } catch (Throwable th) {
                    try {
                        Log.w(SplitHostTaskLane.TAG, "supervision query failed: " + SplitHostTaskLane.rootCause(th));
                        if (supervisionRequest != null) {
                            SplitHostTaskLane.this.postSupervisionResult(supervisionRequest, true, SplitHostTaskSnapshot.unknown());
                        }
                    } finally {
                        SplitHostTaskLane.this.finishSupervisionDrain();
                    }
                }
            }
        }
    };

    static SplitHostTaskLane get(Context context) {
        SplitHostTaskLane splitHostTaskLane;
        SplitHostTaskLane splitHostTaskLane2 = instance;
        if (splitHostTaskLane2 != null) {
            return splitHostTaskLane2;
        }
        synchronized (SplitHostTaskLane.class) {
            splitHostTaskLane = instance;
            if (splitHostTaskLane == null) {
                Context applicationContext = context.getApplicationContext();
                if (applicationContext != null) {
                    context = applicationContext;
                }
                SplitHostTaskLane splitHostTaskLane3 = new SplitHostTaskLane(context);
                instance = splitHostTaskLane3;
                splitHostTaskLane = splitHostTaskLane3;
            }
        }
        return splitHostTaskLane;
    }

    static final class PaneTicket {
        final int displayId;
        final long hostGeneration;
        final String packageName;
        final long paneGeneration;
        final int paneIndex;

        PaneTicket(long j, int i, long j2, String str, int i2) {
            this.hostGeneration = j;
            this.paneIndex = i;
            this.paneGeneration = j2;
            this.packageName = str;
            this.displayId = i2;
        }
    }

    static final class PaneLaunchRequest {
        final WeakReference<SplitHostActivity> owner;
        final PaneTicket pane;
        final long sequence;

        PaneLaunchRequest(long j, SplitHostActivity splitHostActivity, PaneTicket paneTicket) {
            this.sequence = j;
            this.owner = new WeakReference<>(splitHostActivity);
            this.pane = paneTicket;
        }
    }

    static final class SupervisionRequest {
        final long hostGeneration;
        final WeakReference<SplitHostActivity> owner;
        final List<PaneTicket> panes;
        final long sequence;
        final long supervisionGeneration;

        SupervisionRequest(long j, SplitHostActivity splitHostActivity, long j2, long j3, List<PaneTicket> list) {
            this.sequence = j;
            this.owner = new WeakReference<>(splitHostActivity);
            this.hostGeneration = j2;
            this.supervisionGeneration = j3;
            this.panes = Collections.unmodifiableList(new ArrayList(list));
        }
    }

    private static final class SingleHostRequest {
        final int displayId;
        final int dpi;
        final String packageName;
        final long sequence;

        SingleHostRequest(long j, String str, int i, int i2) {
            this.sequence = j;
            this.packageName = str;
            this.dpi = i;
            this.displayId = i2;
        }
    }

    private static final class TaskQuery {
        final SplitHostTaskSnapshot snapshot;

        TaskQuery(SplitHostTaskSnapshot splitHostTaskSnapshot) {
            this.snapshot = splitHostTaskSnapshot;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class SingleHostResult {
        final boolean launchable;
        final SingleHostRequest request;

        SingleHostResult(SingleHostRequest singleHostRequest, boolean z) {
            this.request = singleHostRequest;
            this.launchable = z;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class PaneLaunchResult {
        final Intent launchIntent;
        final PaneLaunchRequest request;

        PaneLaunchResult(PaneLaunchRequest paneLaunchRequest, Intent intent) {
            this.request = paneLaunchRequest;
            this.launchIntent = intent;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class SupervisionResult {
        final boolean enabled;
        final SupervisionRequest request;
        final SplitHostTaskSnapshot snapshot;

        SupervisionResult(SupervisionRequest supervisionRequest, boolean z, SplitHostTaskSnapshot splitHostTaskSnapshot) {
            this.request = supervisionRequest;
            this.enabled = z;
            this.snapshot = splitHostTaskSnapshot;
        }
    }

    private SplitHostTaskLane(Context context) {
        this.appContext = context;
        this.activityManager = (ActivityManager) context.getSystemService("activity");
        HandlerThread handlerThread = new HandlerThread("SplitHostTasks", 10);
        handlerThread.start();
        this.worker = new Handler(handlerThread.getLooper());
        Executor executor = new Executor() { // from class: ru.big.town.anative.SplitHostTaskLane$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                this.f$0.m2130lambda$new$0$rubigtownanativeSplitHostTaskLane(runnable);
            }
        };
        this.singleHostResults = new LatestValueDelivery<>(executor, new Consumer() { // from class: ru.big.town.anative.SplitHostTaskLane$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.deliverSingleHostResult((SplitHostTaskLane.SingleHostResult) obj);
            }
        });
        this.leftPaneResults = new LatestValueDelivery<>(executor, new Consumer() { // from class: ru.big.town.anative.SplitHostTaskLane$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.deliverPaneLaunchResult((SplitHostTaskLane.PaneLaunchResult) obj);
            }
        });
        this.rightPaneResults = new LatestValueDelivery<>(executor, new Consumer() { // from class: ru.big.town.anative.SplitHostTaskLane$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.deliverPaneLaunchResult((SplitHostTaskLane.PaneLaunchResult) obj);
            }
        });
        this.supervisionResults = new LatestValueDelivery<>(executor, new Consumer() { // from class: ru.big.town.anative.SplitHostTaskLane$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.deliverSupervisionResult((SplitHostTaskLane.SupervisionResult) obj);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$new$0$ru-big-town-anative-SplitHostTaskLane, reason: not valid java name */
    /* synthetic */ void m2130lambda$new$0$rubigtownanativeSplitHostTaskLane(Runnable runnable) {
        if (!this.main.post(runnable)) {
            throw new RejectedExecutionException("main Handler rejected SplitHost result");
        }
    }

    long registerHost(SplitHostActivity splitHostActivity) {
        final SplitHostHostLease.Registration<SplitHostActivity> registrationAcquire = this.hostLease.acquire(splitHostActivity);
        final SplitHostActivity splitHostActivity2 = registrationAcquire.previousOwner;
        if (splitHostActivity2 != null && splitHostActivity2 != splitHostActivity) {
            Runnable runnable = new Runnable() { // from class: ru.big.town.anative.SplitHostTaskLane$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    splitHostActivity2.onSupersededByHost(registrationAcquire.generation);
                }
            };
            if (Looper.myLooper() == Looper.getMainLooper()) {
                runnable.run();
            } else if (!this.main.post(runnable)) {
                Log.e(TAG, "main rejected old-host retirement");
            }
        }
        return registrationAcquire.generation;
    }

    void requestSingleHost(String str, int i, int i2) {
        synchronized (this.lock) {
            long j = this.nextSequence + 1;
            this.nextSequence = j;
            this.latestSingleHostSequence = j;
            if (this.singleHostWork.offer(0, new SingleHostRequest(j, str, i, i2)) && !this.worker.post(this.singleHostDrain)) {
                this.singleHostWork.rejectDrainPost();
                Log.e(TAG, "worker rejected single-host drain");
            }
        }
    }

    void requestPaneLaunch(SplitHostActivity splitHostActivity, PaneTicket paneTicket) {
        synchronized (this.lock) {
            long j = this.nextSequence + 1;
            this.nextSequence = j;
            boolean zOffer = this.paneLaunchWork.offer(paneTicket.paneIndex, new PaneLaunchRequest(j, splitHostActivity, paneTicket));
            this.latestPaneSequence[paneTicket.paneIndex] = j;
            this.latestPaneHost[paneTicket.paneIndex] = paneTicket.hostGeneration;
            if (zOffer && !this.worker.post(this.paneLaunchDrain)) {
                this.paneLaunchWork.rejectDrainPost();
                Log.e(TAG, "worker rejected pane-launch drain");
            }
        }
    }

    void requestSupervision(SplitHostActivity splitHostActivity, long j, long j2, List<PaneTicket> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        synchronized (this.lock) {
            long j3 = this.nextSequence + 1;
            this.nextSequence = j3;
            boolean zOffer = this.supervisionWork.offer(0, new SupervisionRequest(j3, splitHostActivity, j, j2, list));
            this.latestSupervisionHost = j;
            if (zOffer && !this.worker.post(this.supervisionDrain)) {
                this.supervisionWork.rejectDrainPost();
                Log.e(TAG, "worker rejected supervision drain");
            }
        }
    }

    void cancelHost(long j) {
        cancelHostWork(j);
        this.hostLease.release(j);
    }

    void cancelHostWork(long j) {
        synchronized (this.lock) {
            for (int i = 0; i < 2; i++) {
                PaneLaunchRequest paneLaunchRequestPeek = this.paneLaunchWork.peek(i);
                if (paneLaunchRequestPeek != null && paneLaunchRequestPeek.pane.hostGeneration == j) {
                    this.paneLaunchWork.clear(i);
                }
                long[] jArr = this.latestPaneHost;
                if (jArr[i] == j) {
                    jArr[i] = 0;
                    long[] jArr2 = this.latestPaneSequence;
                    long j2 = this.nextSequence + 1;
                    this.nextSequence = j2;
                    jArr2[i] = j2;
                }
            }
            cancelSupervisionLocked(j);
        }
    }

    void cancelSupervision(long j) {
        synchronized (this.lock) {
            cancelSupervisionLocked(j);
        }
    }

    private void cancelSupervisionLocked(long j) {
        SupervisionRequest supervisionRequestPeek = this.supervisionWork.peek(0);
        if (supervisionRequestPeek != null && supervisionRequestPeek.hostGeneration == j) {
            this.supervisionWork.clear(0);
        }
        if (this.latestSupervisionHost == j) {
            this.latestSupervisionHost = 0L;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processPaneLaunchBatch(PaneLaunchRequest[] paneLaunchRequestArr) {
        assertWorkerThread();
        Intent[] intentArr = new Intent[paneLaunchRequestArr.length];
        for (int i = 0; i < paneLaunchRequestArr.length; i++) {
            PaneLaunchRequest paneLaunchRequest = paneLaunchRequestArr[i];
            if (paneLaunchRequest != null && isLatestPane(paneLaunchRequest)) {
                intentArr[i] = resolveLaunchIntent(paneLaunchRequest.pane.packageName);
            }
        }
        boolean z = false;
        for (int i2 = 0; i2 < paneLaunchRequestArr.length; i2++) {
            PaneLaunchRequest paneLaunchRequest2 = paneLaunchRequestArr[i2];
            z |= (paneLaunchRequest2 == null || intentArr[i2] == null || !isLatestPane(paneLaunchRequest2)) ? false : true;
        }
        if (z) {
            TaskQuery taskQueryQueryTasks = queryTasks();
            HashSet hashSet = new HashSet();
            for (int i3 = 0; i3 < paneLaunchRequestArr.length; i3++) {
                PaneLaunchRequest paneLaunchRequest3 = paneLaunchRequestArr[i3];
                if (paneLaunchRequest3 != null && intentArr[i3] != null && isLatestPane(paneLaunchRequest3)) {
                    hashSet.add(paneLaunchRequest3.pane.packageName);
                }
            }
            removeMatchingTasks(taskQueryQueryTasks.snapshot, hashSet, paneLaunchRequestArr);
        }
        for (int i4 = 0; i4 < paneLaunchRequestArr.length; i4++) {
            PaneLaunchRequest paneLaunchRequest4 = paneLaunchRequestArr[i4];
            if (paneLaunchRequest4 != null) {
                postPaneLaunchResult(paneLaunchRequest4, intentArr[i4]);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Intent resolveLaunchIntent(String str) {
        assertWorkerThread();
        try {
            return this.appContext.getPackageManager().getLaunchIntentForPackage(str);
        } catch (Throwable th) {
            Log.w(TAG, "launch intent " + str + ": " + rootCause(th));
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean readWatchEnabled() {
        assertWorkerThread();
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        if (jElapsedRealtime < this.watchSettingExpiresAt) {
            return this.cachedWatchEnabled;
        }
        try {
            this.cachedWatchEnabled = !"0".equals(Settings.Global.getString(this.appContext.getContentResolver(), "voyahtune_splitwatch"));
        } catch (Throwable th) {
            Log.w(TAG, "splitwatch setting: " + rootCause(th));
            this.cachedWatchEnabled = true;
        }
        this.watchSettingExpiresAt = jElapsedRealtime + WATCH_SETTING_CACHE_MS;
        return this.cachedWatchEnabled;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public TaskQuery queryTasks() {
        assertWorkerThread();
        ActivityManager activityManager = this.activityManager;
        if (activityManager == null) {
            return new TaskQuery(SplitHostTaskSnapshot.unknown());
        }
        try {
            List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(100);
            if (runningTasks == null) {
                return new TaskQuery(SplitHostTaskSnapshot.unknown());
            }
            ArrayList arrayList = new ArrayList(runningTasks.size());
            for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTasks) {
                ComponentName componentName = runningTaskInfo.topActivity != null ? runningTaskInfo.topActivity : runningTaskInfo.baseActivity;
                arrayList.add(new SplitHostTaskSnapshot.TaskRecord(runningTaskInfo.taskId, componentName != null ? componentName.getPackageName() : null, readDisplayId(runningTaskInfo)));
            }
            return new TaskQuery(SplitHostTaskSnapshot.known(arrayList));
        } catch (Throwable th) {
            Log.w(TAG, "getRunningTasks: " + rootCause(th));
            return new TaskQuery(SplitHostTaskSnapshot.unknown());
        }
    }

    private Integer readDisplayId(ActivityManager.RunningTaskInfo runningTaskInfo) {
        if (!this.displayFieldResolved) {
            this.displayFieldResolved = true;
            try {
                this.displayField = runningTaskInfo.getClass().getField("displayId");
            } catch (Throwable th) {
                Log.w(TAG, "RunningTaskInfo.displayId unavailable: " + rootCause(th));
            }
        }
        Field field = this.displayField;
        if (field == null) {
            return null;
        }
        try {
            return Integer.valueOf(field.getInt(runningTaskInfo));
        } catch (Throwable unused) {
            return null;
        }
    }

    private void removeMatchingTasks(SplitHostTaskSnapshot splitHostTaskSnapshot, Set<String> set, PaneLaunchRequest[] paneLaunchRequestArr) {
        assertWorkerThread();
        if (set.isEmpty() || !ensureRemoveTask()) {
            return;
        }
        boolean z = false;
        int i = 0;
        for (SplitHostTaskSnapshot.TaskRecord taskRecord : splitHostTaskSnapshot.tasks()) {
            if (set.contains(taskRecord.packageName) && hasCurrentRequestForPackage(paneLaunchRequestArr, taskRecord.packageName)) {
                try {
                    this.removeTaskMethod.invoke(this.activityTaskManager, Integer.valueOf(taskRecord.taskId));
                    i++;
                } catch (Throwable th) {
                    Log.w(TAG, "removeTask " + taskRecord.taskId + " (" + taskRecord.packageName + "): " + rootCause(th));
                    z = true;
                }
            }
        }
        if (z) {
            this.activityTaskManager = null;
            this.removeTaskMethod = null;
        }
        Log.i(TAG, "removeTask packages=" + set + " removed=" + i + " (process alive)");
    }

    private boolean hasCurrentRequestForPackage(PaneLaunchRequest[] paneLaunchRequestArr, String str) {
        for (PaneLaunchRequest paneLaunchRequest : paneLaunchRequestArr) {
            if (paneLaunchRequest != null && str.equals(paneLaunchRequest.pane.packageName) && isLatestPane(paneLaunchRequest)) {
                return true;
            }
        }
        return false;
    }

    private boolean ensureRemoveTask() {
        if (this.activityTaskManager != null && this.removeTaskMethod != null) {
            return true;
        }
        try {
            Object objInvoke = Class.forName("android.app.ActivityTaskManager").getMethod("getService", new Class[0]).invoke(null, new Object[0]);
            this.activityTaskManager = objInvoke;
            this.removeTaskMethod = objInvoke.getClass().getMethod("removeTask", Integer.TYPE);
            return true;
        } catch (Throwable th) {
            Log.w(TAG, "IActivityTaskManager.removeTask unavailable: " + rootCause(th));
            this.activityTaskManager = null;
            this.removeTaskMethod = null;
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postPaneLaunchResult(PaneLaunchRequest paneLaunchRequest, Intent intent) {
        (paneLaunchRequest.pane.paneIndex == 0 ? this.leftPaneResults : this.rightPaneResults).offer(paneLaunchRequest.pane.hostGeneration, paneLaunchRequest.sequence, new PaneLaunchResult(paneLaunchRequest, intent));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postSupervisionResult(SupervisionRequest supervisionRequest, boolean z, SplitHostTaskSnapshot splitHostTaskSnapshot) {
        this.supervisionResults.offer(supervisionRequest.hostGeneration, supervisionRequest.sequence, new SupervisionResult(supervisionRequest, z, splitHostTaskSnapshot));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deliverSingleHostResult(SingleHostResult singleHostResult) {
        SingleHostRequest singleHostRequest = singleHostResult.request;
        if (isLatestSingleHost(singleHostRequest)) {
            if (!singleHostResult.launchable) {
                Log.w(TAG, "launchSingle: нет launch intent для " + singleHostRequest.packageName);
            } else {
                SplitHostActivity.startSingleHost(this.appContext, singleHostRequest.packageName, singleHostRequest.dpi, singleHostRequest.displayId);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deliverPaneLaunchResult(PaneLaunchResult paneLaunchResult) {
        SplitHostActivity splitHostActivity;
        PaneLaunchRequest paneLaunchRequest = paneLaunchResult.request;
        if (isLatestPane(paneLaunchRequest) && (splitHostActivity = paneLaunchRequest.owner.get()) != null) {
            splitHostActivity.onPaneLaunchPrepared(paneLaunchRequest, paneLaunchResult.launchIntent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deliverSupervisionResult(SupervisionResult supervisionResult) {
        SplitHostActivity splitHostActivity;
        SupervisionRequest supervisionRequest = supervisionResult.request;
        if (isCurrentSupervisionHost(supervisionRequest) && (splitHostActivity = supervisionRequest.owner.get()) != null) {
            splitHostActivity.onSupervisionSnapshot(supervisionRequest, supervisionResult.enabled, supervisionResult.snapshot);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isLatestSingleHost(SingleHostRequest singleHostRequest) {
        boolean z;
        synchronized (this.lock) {
            z = singleHostRequest.sequence == this.latestSingleHostSequence;
        }
        return z;
    }

    private boolean isLatestPane(PaneLaunchRequest paneLaunchRequest) {
        boolean z;
        int i = paneLaunchRequest.pane.paneIndex;
        synchronized (this.lock) {
            z = paneLaunchRequest.sequence == this.latestPaneSequence[i] && paneLaunchRequest.pane.hostGeneration == this.latestPaneHost[i];
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCurrentSupervisionHost(SupervisionRequest supervisionRequest) {
        boolean z;
        synchronized (this.lock) {
            z = supervisionRequest.hostGeneration == this.latestSupervisionHost;
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishSingleHostDrain() {
        synchronized (this.lock) {
            if (this.singleHostWork.finishDrain() && !this.worker.post(this.singleHostDrain)) {
                this.singleHostWork.rejectDrainPost();
                Log.e(TAG, "worker rejected single-host follow-up");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishPaneLaunchDrain() {
        synchronized (this.lock) {
            if (this.paneLaunchWork.finishDrain() && !this.worker.post(this.paneLaunchDrain)) {
                this.paneLaunchWork.rejectDrainPost();
                Log.e(TAG, "worker rejected pane-launch follow-up");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishSupervisionDrain() {
        synchronized (this.lock) {
            if (this.supervisionWork.finishDrain() && !this.worker.post(this.supervisionDrain)) {
                this.supervisionWork.rejectDrainPost();
                Log.e(TAG, "worker rejected supervision follow-up");
            }
        }
    }

    private static void assertWorkerThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException("SplitHost Binder work reached main thread");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Throwable rootCause(Throwable th) {
        return (!(th instanceof InvocationTargetException) || th.getCause() == null) ? th : th.getCause();
    }
}
