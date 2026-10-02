package ru.big.town.anative;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.car.Car;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.media.MediaMetadataCompat;
import android.system.Os;
import android.util.Log;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.core.app.NotificationCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes2.dex */
public class NowPlayingService extends Service {
    public static final String ACTION_NOW_PLAYING = "ru.big.town.anative.NOW_PLAYING";
    public static final String ACTION_REQUEST_NOW_PLAYING = "ru.big.town.anative.REQUEST_NOW_PLAYING";
    private static final String ART_FILE_NAME = "nowplaying_art.png";
    private static final LatestValueDelivery<BroadcastWrite> BROADCASTS;
    private static final ThreadPoolExecutor BROADCAST_EXECUTOR;
    private static final String CHANNEL_ID = "now_playing_channel";
    static final String MEDIA_ROUTE_KEY = "voyahtune_mediaRoute";
    private static final String ROUTE_DISPATCH = "dispatch";
    private static final ThreadPoolExecutor ROUTE_EXECUTOR;
    private static final String ROUTE_NATIVE = "native";
    private static final LatestValueDelivery<RouteWrite> ROUTE_WRITES;
    private static final String TAG = "$$$ NowPlayingService $$$";
    private static String lastWrittenRoute;
    private static long lastWrittenRouteGeneration;
    static volatile String sAlbum;
    static volatile String sAppLabel;
    static volatile String sArtist;
    static volatile long sDuration;
    static volatile boolean sHasArt;
    static volatile String sPackage;
    static volatile long sPosition;
    static volatile int sState;
    static volatile String sTitle;
    static volatile long sUpdatedAt;
    private volatile long activeWatcherEpoch;
    private volatile Handler callbackHandler;
    private HandlerThread callbackThread;
    private MediaController.Callback controllerCallback;
    private volatile MediaController current;
    private volatile Handler handler;
    private long instanceGeneration;
    private Bitmap lastWrittenArt;
    private volatile MediaRefreshDelivery mediaRefreshes;
    private MediaSessionManager msm;
    private boolean receiverRegistered;
    private volatile boolean stopping;
    private long watcherSequence;
    private HandlerThread workerThread;
    private static final AtomicLong INSTANCE_SEQUENCE = new AtomicLong();
    private static final AtomicLong ACTIVE_INSTANCE = new AtomicLong();
    private static final AtomicLong ROUTE_REVISION = new AtomicLong();
    private static final AtomicLong BROADCAST_REVISION = new AtomicLong();
    private static final Object INSTANCE_CALLBACK_LOCK = new Object();
    private static final Object SNAPSHOT_COMMIT_LOCK = new Object();
    private static final Object ART_COMMIT_LOCK = new Object();
    private final MediaSessionManager.OnActiveSessionsChangedListener sessionsListener = new MediaSessionManager.OnActiveSessionsChangedListener() { // from class: ru.big.town.anative.NowPlayingService$$ExternalSyntheticLambda6
        @Override // android.media.session.MediaSessionManager.OnActiveSessionsChangedListener
        public final void onActiveSessionsChanged(List list) {
            NowPlayingService.this.m1996lambda$new$1$rubigtownanativeNowPlayingService(list);
        }
    };
    private final BroadcastReceiver requestReceiver = new BroadcastReceiver() { // from class: ru.big.town.anative.NowPlayingService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            NowPlayingService.this.offerMediaRefresh(MediaRefreshDelivery.Work.PUBLISH, "request");
        }
    };
    private final List<MediaController> watched = new ArrayList();
    private final List<MediaController.Callback> watchedCbs = new ArrayList();

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        return 1;
    }

    static {
        ThreadPoolExecutor threadPoolExecutorNewDeliveryExecutor = newDeliveryExecutor("MediaRoute");
        ROUTE_EXECUTOR = threadPoolExecutorNewDeliveryExecutor;
        ThreadPoolExecutor threadPoolExecutorNewDeliveryExecutor2 = newDeliveryExecutor("NowPlayingBroadcast");
        BROADCAST_EXECUTOR = threadPoolExecutorNewDeliveryExecutor2;
        ROUTE_WRITES = new LatestValueDelivery<>(threadPoolExecutorNewDeliveryExecutor, new Consumer() { // from class: ru.big.town.anative.NowPlayingService$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                NowPlayingService.writeRoute((NowPlayingService.RouteWrite) obj);
            }
        });
        BROADCASTS = new LatestValueDelivery<>(threadPoolExecutorNewDeliveryExecutor2, new Consumer() { // from class: ru.big.town.anative.NowPlayingService$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                NowPlayingService.sendSnapshotBroadcast((NowPlayingService.BroadcastWrite) obj);
            }
        });
        lastWrittenRoute = "";
        sTitle = "";
        sArtist = "";
        sAlbum = "";
        sPackage = "";
        sAppLabel = "";
        sState = 0;
        sPosition = 0L;
        sDuration = 0L;
        sHasArt = false;
        sUpdatedAt = 0L;
    }

    private static ThreadPoolExecutor newDeliveryExecutor(final String str) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue(1), new ThreadFactory() { // from class: ru.big.town.anative.NowPlayingService$$ExternalSyntheticLambda5
            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable runnable) {
                return NowPlayingService.lambda$newDeliveryExecutor$0(str, runnable);
            }
        }, new ThreadPoolExecutor.AbortPolicy());
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }

    static /* synthetic */ Thread lambda$newDeliveryExecutor$0(String str, Runnable runnable) {
        Thread thread = new Thread(runnable, str);
        thread.setDaemon(true);
        return thread;
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class RouteWrite {
        final Context app;
        final boolean clearGeneration;
        final long generation;
        final String pkg;
        final String route;

        RouteWrite(Context context, long j, String str, String str2, boolean z) {
            this.app = context;
            this.generation = j;
            this.route = str;
            this.pkg = str2;
            this.clearGeneration = z;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class BroadcastWrite {
        final Context app;
        final long generation;
        final Intent intent;

        BroadcastWrite(Context context, long j, Intent intent) {
            this.app = context;
            this.generation = j;
            this.intent = intent;
        }
    }

    static File artFile(Context context) {
        return new File(context.getFilesDir(), ART_FILE_NAME);
    }

    /* JADX INFO: renamed from: lambda$new$1$ru-big-town-anative-NowPlayingService, reason: not valid java name */
    /* synthetic */ void m1996lambda$new$1$rubigtownanativeNowPlayingService(List list) {
        offerMediaRefresh(MediaRefreshDelivery.Work.REBUILD, "sessions");
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        synchronized (INSTANCE_CALLBACK_LOCK) {
            long jIncrementAndGet = INSTANCE_SEQUENCE.incrementAndGet();
            this.instanceGeneration = jIncrementAndGet;
            ACTIVE_INSTANCE.set(jIncrementAndGet);
            MediaControlRouter.activateObserverGeneration(this.instanceGeneration);
        }
        resetSnapshotForNewInstance();
        enqueueRoute(ROUTE_NATIVE, "startup", false);
        enqueueSnapshotBroadcast(buildSnapshotIntent());
        try {
            createNotificationChannel();
            startForeground(6, new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("Медиа-информация").setContentText("Отслеживание текущего трека").setSmallIcon(R.drawable.ic_launcher_foreground).build());
            HandlerThread handlerThread = new HandlerThread("NowPlaying", 10);
            this.workerThread = handlerThread;
            handlerThread.start();
            this.handler = new Handler(this.workerThread.getLooper());
            HandlerThread handlerThread2 = new HandlerThread("NowPlayingIngress", 10);
            this.callbackThread = handlerThread2;
            handlerThread2.start();
            this.callbackHandler = new Handler(this.callbackThread.getLooper());
            this.mediaRefreshes = new MediaRefreshDelivery(new Executor() { // from class: ru.big.town.anative.NowPlayingService$$ExternalSyntheticLambda2
                @Override // java.util.concurrent.Executor
                public final void execute(Runnable runnable) {
                    NowPlayingService.this.m1997lambda$onCreate$2$rubigtownanativeNowPlayingService(runnable);
                }
            }, new MediaRefreshDelivery.Listener() { // from class: ru.big.town.anative.NowPlayingService$$ExternalSyntheticLambda3
                @Override // ru.big.town.anative.MediaRefreshDelivery.Listener
                public final void accept(MediaRefreshDelivery.Work work, String str) {
                    NowPlayingService.this.runMediaRefresh(work, str);
                }
            });
            final Handler handler = this.callbackHandler;
            dispatchWorker("initialize", new Runnable() { // from class: ru.big.town.anative.NowPlayingService$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    NowPlayingService.this.m1998lambda$onCreate$3$rubigtownanativeNowPlayingService(handler);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onCreate startForeground: " + e.getMessage());
            stopSelf();
        }
    }

    /* JADX INFO: renamed from: lambda$onCreate$2$ru-big-town-anative-NowPlayingService, reason: not valid java name */
    /* synthetic */ void m1997lambda$onCreate$2$rubigtownanativeNowPlayingService(Runnable runnable) {
        Handler handler = this.handler;
        if (this.stopping || handler == null || !handler.post(runnable)) {
            throw new RejectedExecutionException("NowPlaying worker unavailable");
        }
    }

    /* JADX INFO: renamed from: lambda$onCreate$3$ru-big-town-anative-NowPlayingService, reason: not valid java name */
    /* synthetic */ void m1998lambda$onCreate$3$rubigtownanativeNowPlayingService(Handler handler) {
        NowPlayingService nowPlayingService;
        Handler handler2;
        try {
            nowPlayingService = this;
            handler2 = handler;
            try {
                nowPlayingService.registerReceiver(this.requestReceiver, new IntentFilter(ACTION_REQUEST_NOW_PLAYING), null, handler2, 2);
                nowPlayingService.receiverRegistered = true;
            } catch (Exception e) {
                e = e;
                Log.w(TAG, "onCreate registerReceiver: " + e.getMessage());
            }
        } catch (Exception e2) {
            Exception e = e2;
            nowPlayingService = this;
            handler2 = handler;
        }
        MediaSessionManager mediaSessionManager = (MediaSessionManager) nowPlayingService.getSystemService("media_session");
        nowPlayingService.msm = mediaSessionManager;
        if (mediaSessionManager == null) {
            return;
        }
        try {
            mediaSessionManager.addOnActiveSessionsChangedListener(nowPlayingService.sessionsListener, null, handler2);
            nowPlayingService.onSessionsChanged(nowPlayingService.msm.getActiveSessions(null));
            Log.i(TAG, "onCreate: подписка на активные медиа-сессии установлена");
        } catch (SecurityException e3) {
            Log.e(TAG, "onCreate: нет MEDIA_CONTENT_CONTROL (whitelist на enforce-ROM?) — ридер инертен: " + e3.getMessage());
        }
    }

    private boolean dispatchWorker(final String str, final Runnable runnable) {
        Handler handler = this.handler;
        if (this.stopping || handler == null) {
            return false;
        }
        return handler.post(new Runnable() { // from class: ru.big.town.anative.NowPlayingService$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                NowPlayingService.this.m1995lambda$dispatchWorker$4$rubigtownanativeNowPlayingService(runnable, str);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$dispatchWorker$4$ru-big-town-anative-NowPlayingService, reason: not valid java name */
    /* synthetic */ void m1995lambda$dispatchWorker$4$rubigtownanativeNowPlayingService(Runnable runnable, String str) {
        if (this.stopping) {
            return;
        }
        try {
            runnable.run();
        } catch (Throwable th) {
            Log.e(TAG, str + ": " + th.getMessage(), th);
        }
    }

    private boolean isActiveInstance() {
        return !this.stopping && ACTIVE_INSTANCE.get() == this.instanceGeneration;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isActiveWatcher(long j, long j2) {
        return !this.stopping && ACTIVE_INSTANCE.get() == j && this.activeWatcherEpoch == j2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void offerMediaRefresh(MediaRefreshDelivery.Work work, String str) {
        MediaRefreshDelivery mediaRefreshDelivery;
        if (isActiveInstance() && (mediaRefreshDelivery = this.mediaRefreshes) != null) {
            mediaRefreshDelivery.offer(work, str);
        }
    }

    /* JADX INFO: renamed from: ru.big.town.anative.NowPlayingService$3, reason: invalid class name */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$ru$big$town$anative$MediaRefreshDelivery$Work;

        static {
            int[] iArr = new int[MediaRefreshDelivery.Work.values().length];
            $SwitchMap$ru$big$town$anative$MediaRefreshDelivery$Work = iArr;
            try {
                iArr[MediaRefreshDelivery.Work.REBUILD.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$ru$big$town$anative$MediaRefreshDelivery$Work[MediaRefreshDelivery.Work.REPICK.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$ru$big$town$anative$MediaRefreshDelivery$Work[MediaRefreshDelivery.Work.PUBLISH.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runMediaRefresh(MediaRefreshDelivery.Work work, String str) {
        if (isActiveInstance()) {
            int i = AnonymousClass3.$SwitchMap$ru$big$town$anative$MediaRefreshDelivery$Work[work.ordinal()];
            if (i == 1) {
                onSessionsChanged(safeSessions());
            } else if (i == 2) {
                repick(str);
            } else {
                if (i != 3) {
                    return;
                }
                publish(str);
            }
        }
    }

    private void onSessionsChanged(List<MediaController> list) {
        Handler handler;
        if (isActiveInstance() && (handler = this.callbackHandler) != null) {
            long j = this.instanceGeneration;
            synchronized (INSTANCE_CALLBACK_LOCK) {
                if (isActiveInstance()) {
                    long j2 = 1 + this.watcherSequence;
                    this.watcherSequence = j2;
                    this.activeWatcherEpoch = j2;
                    detachAll();
                    if (list != null) {
                        for (MediaController mediaController : list) {
                            if (!this.isActiveInstance()) {
                                return;
                            }
                            NowPlayingService nowPlayingService = this;
                            MediaController.Callback callback = new MediaController.Callback() { // from class: ru.big.town.anative.NowPlayingService.2
                                private final PlaybackActivityTracker activity;
                                final /* synthetic */ long val$generation;
                                final /* synthetic */ MediaController val$watchedController;
                                final /* synthetic */ MediaSession.Token val$watchedToken;
                                final /* synthetic */ long val$watcherEpoch;

                                {
                                    this.val$watchedController = mediaController;
                                    this.val$generation = j;
                                    this.val$watcherEpoch = j2;
                                    this.val$watchedToken = mediaController.getSessionToken();
                                    this.activity = new PlaybackActivityTracker(MediaControlRouter.isActiveState(NowPlayingService.safePlaybackState(mediaController)));
                                }

                                @Override // android.media.session.MediaController.Callback
                                public void onMetadataChanged(MediaMetadata mediaMetadata) {
                                    if (NowPlayingService.this.isActiveWatcher(this.val$generation, this.val$watcherEpoch)) {
                                        NowPlayingService nowPlayingService2 = NowPlayingService.this;
                                        if (nowPlayingService2.sameController(this.val$watchedController, nowPlayingService2.current)) {
                                            NowPlayingService.this.offerMediaRefresh(MediaRefreshDelivery.Work.PUBLISH, "metadata");
                                        }
                                    }
                                }

                                @Override // android.media.session.MediaController.Callback
                                public void onPlaybackStateChanged(PlaybackState playbackState) {
                                    if (NowPlayingService.this.isActiveWatcher(this.val$generation, this.val$watcherEpoch)) {
                                        PlaybackActivityTracker.Change changeUpdate = this.activity.update(MediaControlRouter.isActiveState(playbackState));
                                        if (changeUpdate == PlaybackActivityTracker.Change.ENTERED_ACTIVE) {
                                            NowPlayingService.this.notePlayingIfActive(this.val$watchedToken, this.val$generation, this.val$watcherEpoch);
                                        }
                                        if (NowPlayingService.this.isActiveWatcher(this.val$generation, this.val$watcherEpoch)) {
                                            if (changeUpdate != PlaybackActivityTracker.Change.SAME) {
                                                NowPlayingService.this.offerMediaRefresh(MediaRefreshDelivery.Work.REPICK, "playback-edge");
                                                return;
                                            }
                                            NowPlayingService nowPlayingService2 = NowPlayingService.this;
                                            if (nowPlayingService2.sameController(this.val$watchedController, nowPlayingService2.current)) {
                                                NowPlayingService.this.offerMediaRefresh(MediaRefreshDelivery.Work.PUBLISH, "playback");
                                            }
                                        }
                                    }
                                }

                                @Override // android.media.session.MediaController.Callback
                                public void onSessionDestroyed() {
                                    if (NowPlayingService.this.isActiveWatcher(this.val$generation, this.val$watcherEpoch)) {
                                        NowPlayingService.this.offerMediaRefresh(MediaRefreshDelivery.Work.REBUILD, "session-destroyed");
                                    }
                                }
                            };
                            try {
                                mediaController.registerCallback(callback, handler);
                                nowPlayingService.watched.add(mediaController);
                                nowPlayingService.watchedCbs.add(callback);
                            } catch (Exception unused) {
                            }
                        }
                    }
                    NowPlayingService nowPlayingService2 = this;
                    if (nowPlayingService2.isActiveInstance()) {
                        MediaController mediaControllerSelectController = MediaControlRouter.selectController(list, nowPlayingService2.instanceGeneration);
                        if (nowPlayingService2.isActiveInstance()) {
                            nowPlayingService2.current = mediaControllerSelectController;
                            Log.i(TAG, "сессий: " + (list == null ? 0 : list.size()) + ", топ: " + (nowPlayingService2.current != null ? nowPlayingService2.current.getPackageName() : "нет"));
                            nowPlayingService2.publishMediaRoute();
                            nowPlayingService2.publish("sessions-changed");
                        }
                    }
                }
            }
        }
    }

    private void repick(String str) {
        if (isActiveInstance()) {
            List<MediaController> listSafeSessions = safeSessions();
            if (isActiveInstance()) {
                MediaController mediaControllerSelectController = MediaControlRouter.selectController(listSafeSessions, this.instanceGeneration);
                if (isActiveInstance()) {
                    if (!sameController(mediaControllerSelectController, this.current)) {
                        this.current = mediaControllerSelectController;
                        Log.i(TAG, "топ-сессия сменилась (" + str + "): " + (this.current != null ? this.current.getPackageName() : "нет"));
                    }
                    publishMediaRoute();
                    publish(str);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notePlayingIfActive(MediaSession.Token token, long j, long j2) {
        synchronized (INSTANCE_CALLBACK_LOCK) {
            if (isActiveWatcher(j, j2)) {
                MediaControlRouter.notePlaying(token, j);
            }
        }
    }

    private void detachAll() {
        for (int i = 0; i < this.watched.size(); i++) {
            try {
                this.watched.get(i).unregisterCallback(this.watchedCbs.get(i));
            } catch (Exception unused) {
            }
        }
        this.watched.clear();
        this.watchedCbs.clear();
        this.controllerCallback = null;
    }

    private void publishMediaRoute() {
        String strNz = this.current != null ? nz(this.current.getPackageName()) : "";
        String str = (strNz.isEmpty() || isOemMediaPackage(strNz)) ? ROUTE_NATIVE : ROUTE_DISPATCH;
        if (strNz.isEmpty()) {
            strNz = "none";
        }
        enqueueRoute(str, strNz, false);
    }

    private void enqueueRoute(String str, String str2, boolean z) {
        if (this.instanceGeneration == 0) {
            return;
        }
        ROUTE_WRITES.offer((this.instanceGeneration << 1) | (z ? 1L : 0L), ROUTE_REVISION.incrementAndGet(), new RouteWrite(getApplicationContext(), this.instanceGeneration, str, str2, z));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void writeRoute(RouteWrite routeWrite) {
        if (routeWrite != null) {
            AtomicLong atomicLong = ACTIVE_INSTANCE;
            if (atomicLong.get() != routeWrite.generation) {
                return;
            }
            try {
                try {
                    if (lastWrittenRouteGeneration != routeWrite.generation || !routeWrite.route.equals(lastWrittenRoute)) {
                        if (Settings.Global.putString(routeWrite.app.getContentResolver(), MEDIA_ROUTE_KEY, routeWrite.route)) {
                            lastWrittenRouteGeneration = routeWrite.generation;
                            lastWrittenRoute = routeWrite.route;
                            Log.i(TAG, "mediaRoute → " + routeWrite.route + " (pkg=" + routeWrite.pkg + ")");
                        } else {
                            Log.w(TAG, "publishMediaRoute: Settings.Global rejected write");
                        }
                    }
                    if (routeWrite.clearGeneration) {
                        atomicLong.compareAndSet(routeWrite.generation, 0L);
                    }
                } catch (Exception e) {
                    Log.w(TAG, "publishMediaRoute: " + e.getMessage());
                    if (routeWrite.clearGeneration) {
                        ACTIVE_INSTANCE.compareAndSet(routeWrite.generation, 0L);
                    }
                }
            } catch (Throwable th) {
                if (routeWrite.clearGeneration) {
                    ACTIVE_INSTANCE.compareAndSet(routeWrite.generation, 0L);
                }
                throw th;
            }
        }
    }

    private static boolean isOemMediaPackage(String str) {
        return str.startsWith("com.android.") || str.startsWith("com.qinggan.") || str.equals("android");
    }

    private List<MediaController> safeSessions() {
        try {
            return this.msm.getActiveSessions(null);
        } catch (Exception unused) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static PlaybackState safePlaybackState(MediaController mediaController) {
        if (mediaController == null) {
            return null;
        }
        try {
            return mediaController.getPlaybackState();
        } catch (Exception unused) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean sameController(MediaController mediaController, MediaController mediaController2) {
        if (mediaController == null || mediaController2 == null) {
            return mediaController == mediaController2;
        }
        try {
            return mediaController.getSessionToken().equals(mediaController2.getSessionToken());
        } catch (Exception unused) {
            return false;
        }
    }

    private void detachCurrent() {
        detachAll();
    }

    private void publish(String str) {
        long j;
        boolean zWriteArt;
        if (isActiveInstance()) {
            try {
                String str2 = "";
                String str3 = "";
                String strNz = "";
                String strNz2 = "";
                String strAppLabel = "";
                long position = 0;
                int state = 0;
                if (this.current != null) {
                    strNz2 = nz(this.current.getPackageName());
                    strAppLabel = appLabel(strNz2);
                    MediaMetadata metadata = this.current.getMetadata();
                    if (metadata != null) {
                        String strFirstNonEmpty = firstNonEmpty(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE), metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE));
                        String strFirstNonEmpty2 = firstNonEmpty(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST), metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST), metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE));
                        strNz = nz(metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM));
                        j = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
                        zWriteArt = writeArt(metadata);
                        str3 = strFirstNonEmpty2;
                        str2 = strFirstNonEmpty;
                    } else {
                        j = 0;
                        zWriteArt = false;
                    }
                    PlaybackState playbackState = this.current.getPlaybackState();
                    if (playbackState != null) {
                        state = playbackState.getState();
                        position = playbackState.getPosition();
                    }
                } else {
                    j = 0;
                    zWriteArt = false;
                }
                synchronized (SNAPSHOT_COMMIT_LOCK) {
                    if (isActiveInstance()) {
                        sTitle = str2;
                        sArtist = str3;
                        sAlbum = strNz;
                        sPackage = strNz2;
                        sAppLabel = strAppLabel;
                        sState = state;
                        sPosition = position;
                        sDuration = j;
                        sHasArt = zWriteArt;
                        sUpdatedAt = System.currentTimeMillis();
                        enqueueSnapshotBroadcast(buildSnapshotIntent());
                        Log.i(TAG, "publish(" + str + "): [" + strNz2 + "] " + str2 + " — " + str3 + " state=" + state + " art=" + zWriteArt);
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "publish: " + e.getMessage());
            }
        }
    }

    private void resetSnapshotForNewInstance() {
        synchronized (SNAPSHOT_COMMIT_LOCK) {
            sTitle = "";
            sArtist = "";
            sAlbum = "";
            sPackage = "";
            sAppLabel = "";
            sState = 0;
            sPosition = 0L;
            sDuration = 0L;
            sHasArt = false;
            sUpdatedAt = System.currentTimeMillis();
        }
    }

    private static Intent buildSnapshotIntent() {
        Intent intent;
        synchronized (SNAPSHOT_COMMIT_LOCK) {
            intent = new Intent(ACTION_NOW_PLAYING);
            intent.setPackage(null);
            intent.putExtra("title", sTitle);
            intent.putExtra("artist", sArtist);
            intent.putExtra("album", sAlbum);
            intent.putExtra(Car.PACKAGE_SERVICE, sPackage);
            intent.putExtra("appLabel", sAppLabel);
            intent.putExtra("state", sState);
            intent.putExtra("position", sPosition);
            intent.putExtra(TypedValues.TransitionType.S_DURATION, sDuration);
            intent.putExtra("hasArt", sHasArt);
            intent.putExtra("updatedAt", sUpdatedAt);
        }
        return intent;
    }

    private void enqueueSnapshotBroadcast(Intent intent) {
        if (this.instanceGeneration == 0 || intent == null) {
            return;
        }
        BROADCASTS.offer(this.instanceGeneration, BROADCAST_REVISION.incrementAndGet(), new BroadcastWrite(getApplicationContext(), this.instanceGeneration, intent));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void sendSnapshotBroadcast(BroadcastWrite broadcastWrite) {
        if (broadcastWrite == null || ACTIVE_INSTANCE.get() != broadcastWrite.generation) {
            return;
        }
        broadcastWrite.app.sendBroadcast(broadcastWrite.intent);
    }

    private boolean writeArt(MediaMetadata mediaMetadata) {
        if (!isActiveInstance()) {
            return false;
        }
        Bitmap bitmap = mediaMetadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART);
        if (bitmap == null) {
            bitmap = mediaMetadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ART);
        }
        if (bitmap == null) {
            bitmap = mediaMetadata.getBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON);
        }
        File fileArtFile = artFile(this);
        if (bitmap == null) {
            this.lastWrittenArt = null;
            synchronized (ART_COMMIT_LOCK) {
                if (isActiveInstance() && fileArtFile.exists()) {
                    fileArtFile.delete();
                }
            }
            return false;
        }
        if (bitmap == this.lastWrittenArt && fileArtFile.exists() && fileArtFile.length() > 0) {
            return isActiveInstance();
        }
        File file = new File(getFilesDir(), "nowplaying_art.png." + this.instanceGeneration + ".tmp");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            try {
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
                    file.delete();
                } else {
                    synchronized (ART_COMMIT_LOCK) {
                        if (!isActiveInstance()) {
                            file.delete();
                        } else {
                            Os.rename(file.getAbsolutePath(), fileArtFile.getAbsolutePath());
                            this.lastWrittenArt = bitmap;
                            fileOutputStream.close();
                            return true;
                        }
                    }
                }
                fileOutputStream.close();
                return false;
            } catch (Throwable th) {
                try {
                    fileOutputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (Exception e) {
            file.delete();
            Log.w(TAG, "writeArt: " + e.getMessage());
            return false;
        }
    }

    private String appLabel(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        try {
            PackageManager packageManager = getPackageManager();
            return packageManager.getApplicationLabel(packageManager.getApplicationInfo(str, 0)).toString();
        } catch (Exception unused) {
            return str;
        }
    }

    private static String nz(String str) {
        return str == null ? "" : str;
    }

    private static String firstNonEmpty(String... strArr) {
        for (String str : strArr) {
            if (str != null && !str.isEmpty()) {
                return str;
            }
        }
        return "";
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        synchronized (INSTANCE_CALLBACK_LOCK) {
            this.stopping = true;
            this.activeWatcherEpoch++;
            MediaControlRouter.deactivateObserverGeneration(this.instanceGeneration);
        }
        MediaRefreshDelivery mediaRefreshDelivery = this.mediaRefreshes;
        this.mediaRefreshes = null;
        if (mediaRefreshDelivery != null) {
            mediaRefreshDelivery.close();
        }
        HandlerThread handlerThread = this.callbackThread;
        this.callbackHandler = null;
        this.callbackThread = null;
        if (handlerThread != null) {
            handlerThread.quit();
        }
        final Handler handler = this.handler;
        final HandlerThread handlerThread2 = this.workerThread;
        enqueueRoute(ROUTE_NATIVE, "destroy", true);
        if (handler != null && handlerThread2 != null && !handler.postAtFrontOfQueue(new Runnable() { // from class: ru.big.town.anative.NowPlayingService$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                NowPlayingService.this.m1999lambda$onDestroy$5$rubigtownanativeNowPlayingService(handler, handlerThread2);
            }
        })) {
            handlerThread2.quitSafely();
        }
        super.onDestroy();
    }

    /* JADX INFO: renamed from: lambda$onDestroy$5$ru-big-town-anative-NowPlayingService, reason: not valid java name */
    /* synthetic */ void m1999lambda$onDestroy$5$rubigtownanativeNowPlayingService(Handler handler, HandlerThread handlerThread) {
        try {
            if (this.receiverRegistered) {
                try {
                    unregisterReceiver(this.requestReceiver);
                } catch (Exception unused) {
                }
                this.receiverRegistered = false;
            }
            MediaSessionManager mediaSessionManager = this.msm;
            if (mediaSessionManager != null) {
                try {
                    mediaSessionManager.removeOnActiveSessionsChangedListener(this.sessionsListener);
                } catch (Exception unused2) {
                }
            }
            detachCurrent();
        } finally {
            handler.removeCallbacksAndMessages(null);
            this.handler = null;
            handlerThread.quitSafely();
        }
    }

    private void createNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
        if (notificationManager == null) {
            return;
        }
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Медиа-информация", 1);
        notificationChannel.setShowBadge(false);
        notificationManager.createNotificationChannel(notificationChannel);
    }
}
