package ru.big.town.anative;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import java.util.ArrayDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/* JADX INFO: loaded from: classes2.dex */
final class CanBusEventHub {
    private static final int AIR_TEMPERATURE_INVALID = -9999;
    private static final int AIR_TEMPERATURE_OUT_INDEX = 35;
    private static final long BIND_RETRY_MS = 5000;
    private static final String CALLBACK_DESCRIPTOR = "com.qinggan.canbus.ICanBusServiceCallback";
    private static final long CALLBACK_RETRY_MS = 30000;
    private static final String CANBUS_ACTION = "com.qinggan.canbus.CanBusService";
    private static final String CANBUS_DESCRIPTOR = "com.qinggan.canbus.ICanBusService";
    private static final String CANBUS_PACKAGE = "com.qinggan.canbus.service";
    private static final int CB_AIR_CONDITION = 4;
    private static final int CB_DOOR_STATUS = 1;
    private static final int CB_GEAR_STATUS = 12;
    private static final int CB_LIGHT_STATUS = 10;
    private static final int CB_VEHICLE_STATE = 36;
    private static final int LIGHT_AUTO_LAMP_INDEX = 16;
    private static final int LIGHT_DIPPED_BEAM_INDEX = 7;
    private static final int LIGHT_FIELD_COUNT = 17;
    private static final int LIGHT_HEAD_LIGHT_INDEX = 13;
    private static final int PRE_READY_CAPACITY = 64;
    private static final String TAG = "CanBusEventHub";
    private static final int TX_ADD_CALLBACK = 28;
    private static final int TX_GET_DOOR_STATUS = 2;
    private static final int TX_QUERY_VEHICLE_STATE = 20;
    private static final int TX_REMOVE_CALLBACK = 29;
    private static volatile CanBusEventHub instance;
    private long activeBindingGeneration;
    private volatile long activeEpoch;
    private boolean bindingRequested;
    private boolean callbackAdded;
    private CallbackBinder callbackBinder;
    private final Context context;
    private IBinder.DeathRecipient deathRecipient;
    private final Handler doorQueryHandler;
    private final HandlerThread doorQueryThread;
    private long doorRevision;
    private final Executor ioExecutor;
    private final Handler ioHandler;
    private final HandlerThread ioThread;
    private long nextBindingGeneration;
    private long nextEpoch;
    private long nextSequence;
    private CanBusEvent pendingConnectionEvent;
    private long readyEpoch;
    private IBinder remote;
    private ServiceConnection serviceConnection;
    private final Handler vehicleQueryHandler;
    private final HandlerThread vehicleQueryThread;
    private final CanBusEventRouter router = new CanBusEventRouter();
    private final AtomicInteger subscriberCount = new AtomicInteger();
    private final AtomicLong malformedCallbacks = new AtomicLong();
    private final AtomicLong preReadyDrops = new AtomicLong();
    private final AtomicBoolean doorSeedRequestPosted = new AtomicBoolean();
    private final AtomicBoolean vehicleSnapshotRequestPosted = new AtomicBoolean();
    private final Object eventLock = new Object();
    private final ArrayDeque<CanBusEvent> preReadyEvents = new ArrayDeque<>();
    private long lastBindAttemptElapsed = -5000;
    private final LatestSingleFlight doorQueryGate = new LatestSingleFlight();
    private final LatestSingleFlight vehicleQueryGate = new LatestSingleFlight();
    private final Runnable bindRetryRunnable = new Runnable() { // from class: ru.big.town.anative.CanBusEventHub$$ExternalSyntheticLambda8
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.ensureBound();
        }
    };
    private final Runnable callbackRetryRunnable = new Runnable() { // from class: ru.big.town.anative.CanBusEventHub$$ExternalSyntheticLambda9
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.ensureCallbackRegistered();
        }
    };

    interface Listener {
        void onCanBusEvent(CanBusEvent canBusEvent);
    }

    static CanBusEventHub get(Context context) {
        CanBusEventHub canBusEventHub;
        CanBusEventHub canBusEventHub2 = instance;
        if (canBusEventHub2 != null) {
            return canBusEventHub2;
        }
        synchronized (CanBusEventHub.class) {
            canBusEventHub = instance;
            if (canBusEventHub == null) {
                Context applicationContext = context.getApplicationContext();
                if (applicationContext != null) {
                    context = applicationContext;
                }
                CanBusEventHub canBusEventHub3 = new CanBusEventHub(context);
                instance = canBusEventHub3;
                canBusEventHub = canBusEventHub3;
            }
        }
        return canBusEventHub;
    }

    private CanBusEventHub(Context context) {
        this.context = context;
        HandlerThread handlerThread = new HandlerThread("CanBusEventHubIo");
        this.ioThread = handlerThread;
        handlerThread.start();
        this.ioHandler = new Handler(handlerThread.getLooper());
        HandlerThread handlerThread2 = new HandlerThread("CanBusDoorQuery");
        this.doorQueryThread = handlerThread2;
        handlerThread2.start();
        this.doorQueryHandler = new Handler(handlerThread2.getLooper());
        HandlerThread handlerThread3 = new HandlerThread("CanBusVehicleQuery");
        this.vehicleQueryThread = handlerThread3;
        handlerThread3.start();
        this.vehicleQueryHandler = new Handler(handlerThread3.getLooper());
        this.ioExecutor = new Executor() { // from class: ru.big.town.anative.CanBusEventHub$$ExternalSyntheticLambda10
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                this.f$0.m1856lambda$new$0$rubigtownanativeCanBusEventHub(runnable);
            }
        };
    }

    /* JADX INFO: renamed from: lambda$new$0$ru-big-town-anative-CanBusEventHub, reason: not valid java name */
    /* synthetic */ void m1856lambda$new$0$rubigtownanativeCanBusEventHub(Runnable runnable) {
        if (!this.ioHandler.post(runnable)) {
            throw new RejectedExecutionException("CanBusEventHubIo stopped");
        }
    }

    Subscription subscribe(int i, int[] iArr, Handler handler, Listener listener) {
        if (handler == null || listener == null) {
            throw new IllegalArgumentException("deliveryHandler/listener required");
        }
        final Subscription subscription = new Subscription(this, i, iArr == null ? null : (int[]) iArr.clone(), handler, listener);
        this.subscriberCount.incrementAndGet();
        this.ioHandler.post(new Runnable() { // from class: ru.big.town.anative.CanBusEventHub$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1860lambda$subscribe$1$rubigtownanativeCanBusEventHub(subscription);
            }
        });
        return subscription;
    }

    void requestDriverDoorSeed() {
        if (this.router.hasInterest(2) && this.doorSeedRequestPosted.compareAndSet(false, true) && !this.ioHandler.post(new Runnable() { // from class: ru.big.town.anative.CanBusEventHub$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.acceptDriverDoorQueryRequest();
            }
        })) {
            this.doorSeedRequestPosted.set(false);
        }
    }

    void requestVehicleStateSnapshot() {
        if (this.router.hasInterest(16) && this.vehicleSnapshotRequestPosted.compareAndSet(false, true) && !this.ioHandler.post(new Runnable() { // from class: ru.big.town.anative.CanBusEventHub$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.acceptVehicleStateQueryRequest();
            }
        })) {
            this.vehicleSnapshotRequestPosted.set(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: addSubscription, reason: merged with bridge method [inline-methods] */
    public void m1860lambda$subscribe$1$rubigtownanativeCanBusEventHub(final Subscription subscription) {
        if (subscription.isClosed()) {
            releaseIfUnused();
            return;
        }
        Executor executor = new Executor() { // from class: ru.big.town.anative.CanBusEventHub$$ExternalSyntheticLambda11
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                CanBusEventHub.lambda$addSubscription$2(subscription, runnable);
            }
        };
        synchronized (this.eventLock) {
            CanBusEventRouter.Subscription subscriptionSubscribe = this.router.subscribe(subscription.interestMask, subscription.vehicleStateIds, executor, new CanBusEventRouter.Listener() { // from class: ru.big.town.anative.CanBusEventHub$$ExternalSyntheticLambda12
                @Override // ru.big.town.anative.CanBusEventRouter.Listener
                public final void onCanBusEvent(CanBusEvent canBusEvent) {
                    this.f$0.m1853lambda$addSubscription$3$rubigtownanativeCanBusEventHub(subscription, canBusEvent);
                }
            });
            if (subscription.attach(subscriptionSubscribe)) {
                if (this.activeEpoch != 0 && this.readyEpoch == this.activeEpoch) {
                    long j = this.activeEpoch;
                    long j2 = this.nextSequence + 1;
                    this.nextSequence = j2;
                    subscriptionSubscribe.offer(CanBusEvent.connection(j, j2, SystemClock.elapsedRealtime()));
                }
                ensureBound();
            }
        }
    }

    static /* synthetic */ void lambda$addSubscription$2(Subscription subscription, Runnable runnable) {
        if (!subscription.deliveryHandler.post(runnable)) {
            throw new RejectedExecutionException("consumer Handler stopped");
        }
    }

    /* JADX INFO: renamed from: lambda$addSubscription$3$ru-big-town-anative-CanBusEventHub, reason: not valid java name */
    /* synthetic */ void m1853lambda$addSubscription$3$rubigtownanativeCanBusEventHub(Subscription subscription, CanBusEvent canBusEvent) {
        if (canBusEvent.kind == CanBusEvent.Kind.CONNECTION_LOST || canBusEvent.connectionEpoch == this.activeEpoch) {
            subscription.listener.onCanBusEvent(canBusEvent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSubscriptionClosed() {
        int iDecrementAndGet = this.subscriberCount.decrementAndGet();
        if (iDecrementAndGet < 0) {
            this.subscriberCount.set(0);
            throw new IllegalStateException("negative CanBus subscriber count");
        }
        if (iDecrementAndGet == 0) {
            this.ioHandler.post(new Runnable() { // from class: ru.big.town.anative.CanBusEventHub$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.releaseIfUnused();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseIfUnused() {
        if (this.subscriberCount.get() == 0) {
            releaseBinding("last subscriber closed");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ensureBound() {
        if (this.subscriberCount.get() == 0 || this.bindingRequested) {
            return;
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        long j = BIND_RETRY_MS - (jElapsedRealtime - this.lastBindAttemptElapsed);
        if (j > 0) {
            this.ioHandler.removeCallbacks(this.bindRetryRunnable);
            this.ioHandler.postDelayed(this.bindRetryRunnable, j);
            return;
        }
        this.lastBindAttemptElapsed = jElapsedRealtime;
        long j2 = this.nextBindingGeneration + 1;
        this.nextBindingGeneration = j2;
        GenerationServiceConnection generationServiceConnection = new GenerationServiceConnection(j2);
        this.serviceConnection = generationServiceConnection;
        this.activeBindingGeneration = j2;
        try {
            boolean zBindService = this.context.bindService(new Intent(CANBUS_ACTION).setPackage(CANBUS_PACKAGE), 1, this.ioExecutor, generationServiceConnection);
            this.bindingRequested = zBindService;
            if (!zBindService) {
                this.serviceConnection = null;
                this.activeBindingGeneration = 0L;
                scheduleBindRetry();
            }
            Log.i(TAG, "bindService returned " + zBindService);
        } catch (RuntimeException e) {
            this.bindingRequested = false;
            this.serviceConnection = null;
            this.activeBindingGeneration = 0L;
            Log.w(TAG, "bindService failed: " + e.getMessage());
            scheduleBindRetry();
        }
    }

    private void scheduleBindRetry() {
        if (this.subscriberCount.get() == 0) {
            return;
        }
        this.ioHandler.removeCallbacks(this.bindRetryRunnable);
        this.ioHandler.postDelayed(this.bindRetryRunnable, BIND_RETRY_MS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleServiceConnected(final IBinder iBinder) {
        IBinder iBinder2;
        if (this.subscriberCount.get() == 0) {
            releaseBinding("connected without subscribers");
            return;
        }
        if (this.remote == iBinder) {
            this.bindingRequested = true;
            if (this.callbackAdded) {
                return;
            }
            ensureCallbackRegistered();
            return;
        }
        this.ioHandler.removeCallbacks(this.bindRetryRunnable);
        this.ioHandler.removeCallbacks(this.callbackRetryRunnable);
        if (this.callbackAdded && (iBinder2 = this.remote) != null && this.callbackBinder != null && iBinder2.isBinderAlive()) {
            removeCallback(this.remote, this.callbackBinder);
        }
        clearRemoteSession("new service connection");
        this.bindingRequested = true;
        this.remote = iBinder;
        this.callbackAdded = false;
        final long j = this.nextEpoch + 1;
        this.nextEpoch = j;
        this.callbackBinder = new CallbackBinder(j);
        IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() { // from class: ru.big.town.anative.CanBusEventHub$$ExternalSyntheticLambda4
            @Override // android.os.IBinder.DeathRecipient
            public final void binderDied() {
                this.f$0.m1855x3e9ad33e(j, iBinder);
            }
        };
        this.deathRecipient = deathRecipient;
        synchronized (this.eventLock) {
            this.activeEpoch = j;
            this.readyEpoch = 0L;
            this.doorRevision++;
            this.preReadyEvents.clear();
            long j2 = this.nextSequence + 1;
            this.nextSequence = j2;
            this.pendingConnectionEvent = CanBusEvent.connection(j, j2, SystemClock.elapsedRealtime());
        }
        try {
            iBinder.linkToDeath(deathRecipient, 0);
            Log.i(TAG, "CanBus service connected, epoch=" + j);
            ensureCallbackRegistered();
        } catch (RemoteException unused) {
            restartBinding("binder already dead");
        }
    }

    /* JADX INFO: renamed from: lambda$handleServiceConnected$5$ru-big-town-anative-CanBusEventHub, reason: not valid java name */
    /* synthetic */ void m1855x3e9ad33e(final long j, final IBinder iBinder) {
        this.ioHandler.post(new Runnable() { // from class: ru.big.town.anative.CanBusEventHub$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1854xca5b9adf(j, iBinder);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: handleBinderDeath, reason: merged with bridge method [inline-methods] */
    public void m1854xca5b9adf(long j, IBinder iBinder) {
        if (j == this.activeEpoch && iBinder == this.remote) {
            restartBinding("binder died");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ensureCallbackRegistered() {
        if (this.subscriberCount.get() == 0 || this.remote == null || this.callbackBinder == null || this.callbackAdded) {
            return;
        }
        this.ioHandler.removeCallbacks(this.callbackRetryRunnable);
        long j = this.activeEpoch;
        if (!addCallback(this.remote, this.callbackBinder)) {
            if (!this.remote.isBinderAlive()) {
                restartBinding("callback registration lost binder");
                return;
            } else {
                this.ioHandler.postDelayed(this.callbackRetryRunnable, CALLBACK_RETRY_MS);
                return;
            }
        }
        this.callbackAdded = true;
        synchronized (this.eventLock) {
            if (this.activeEpoch != j) {
                return;
            }
            this.readyEpoch = j;
            CanBusEvent canBusEvent = this.pendingConnectionEvent;
            if (canBusEvent != null) {
                this.router.dispatch(canBusEvent);
                this.pendingConnectionEvent = null;
            }
            while (!this.preReadyEvents.isEmpty()) {
                this.router.dispatch(this.preReadyEvents.removeFirst());
            }
            Log.i(TAG, "CanBus callback registered, epoch=" + j + " malformed=" + this.malformedCallbacks.get() + " preReadyDrops=" + this.preReadyDrops.get());
        }
    }

    private boolean addCallback(IBinder iBinder, IBinder iBinder2) {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        boolean z = false;
        try {
            try {
                parcelObtain.writeInterfaceToken(CANBUS_DESCRIPTOR);
                parcelObtain.writeStrongBinder(iBinder2);
                if (iBinder.transact(28, parcelObtain, parcelObtain2, 0)) {
                    parcelObtain2.readException();
                    if (parcelObtain2.readInt() != 0) {
                        z = true;
                    }
                }
            } catch (RemoteException | RuntimeException e) {
                Log.w(TAG, "addCallback failed: " + e.getMessage());
            }
            return z;
        } finally {
            parcelObtain.recycle();
            parcelObtain2.recycle();
        }
    }

    private void removeCallback(IBinder iBinder, IBinder iBinder2) {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken(CANBUS_DESCRIPTOR);
                parcelObtain.writeStrongBinder(iBinder2);
                if (iBinder.transact(29, parcelObtain, parcelObtain2, 0)) {
                    parcelObtain2.readException();
                    if (parcelObtain2.dataAvail() >= 4 && parcelObtain2.readInt() == 0) {
                        Log.w(TAG, "removeCallback returned false");
                    }
                }
            } catch (RemoteException | RuntimeException e) {
                Log.w(TAG, "removeCallback failed: " + e.getMessage());
            }
        } finally {
            parcelObtain.recycle();
            parcelObtain2.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restartBinding(String str) {
        Log.w(TAG, "Restarting CanBus binding: " + str);
        releaseBinding(str);
        scheduleBindRetry();
    }

    private void releaseBinding(String str) {
        this.ioHandler.removeCallbacks(this.bindRetryRunnable);
        this.ioHandler.removeCallbacks(this.callbackRetryRunnable);
        IBinder iBinder = this.remote;
        CallbackBinder callbackBinder = this.callbackBinder;
        if (this.callbackAdded && iBinder != null && callbackBinder != null) {
            removeCallback(iBinder, callbackBinder);
        }
        clearRemoteSession(str);
        ServiceConnection serviceConnection = this.serviceConnection;
        boolean z = this.bindingRequested;
        this.serviceConnection = null;
        this.activeBindingGeneration = 0L;
        this.bindingRequested = false;
        if (!z || serviceConnection == null) {
            return;
        }
        try {
            this.context.unbindService(serviceConnection);
        } catch (RuntimeException e) {
            Log.w(TAG, str + ": unbind failed: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearRemoteSession(String str) {
        this.ioHandler.removeCallbacks(this.callbackRetryRunnable);
        IBinder iBinder = this.remote;
        IBinder.DeathRecipient deathRecipient = this.deathRecipient;
        if (iBinder != null && deathRecipient != null) {
            try {
                iBinder.unlinkToDeath(deathRecipient, 0);
            } catch (RuntimeException unused) {
            }
        }
        this.remote = null;
        this.callbackBinder = null;
        this.deathRecipient = null;
        this.callbackAdded = false;
        synchronized (this.eventLock) {
            long j = this.activeEpoch;
            this.activeEpoch = 0L;
            this.readyEpoch = 0L;
            if (j != 0) {
                this.doorRevision++;
                this.router.invalidateThrough(j);
                long j2 = this.nextEpoch + 1;
                this.nextEpoch = j2;
                CanBusEventRouter canBusEventRouter = this.router;
                long j3 = 1 + this.nextSequence;
                this.nextSequence = j3;
                canBusEventRouter.dispatch(CanBusEvent.connectionLost(j2, j3, SystemClock.elapsedRealtime(), j));
            }
            this.pendingConnectionEvent = null;
            this.preReadyEvents.clear();
        }
        if ("new service connection".equals(str)) {
            return;
        }
        Log.i(TAG, "CanBus session cleared: " + str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void acceptDriverDoorQueryRequest() {
        this.doorSeedRequestPosted.set(false);
        this.doorQueryGate.request();
        startDriverDoorQueryIfNeeded();
    }

    private void startDriverDoorQueryIfNeeded() {
        if (this.doorQueryGate.tryStart()) {
            final IBinder iBinder = this.remote;
            synchronized (this.eventLock) {
                final long j = this.activeEpoch;
                if (j != 0 && this.readyEpoch == j && iBinder != null) {
                    final long j2 = this.doorRevision;
                    if (this.doorQueryHandler.post(new Runnable() { // from class: ru.big.town.anative.CanBusEventHub$$ExternalSyntheticLambda13
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.m1858x968f3a96(iBinder, j, j2);
                        }
                    })) {
                        return;
                    }
                    this.doorQueryGate.complete();
                    startDriverDoorQueryIfNeeded();
                    return;
                }
                this.doorQueryGate.complete();
            }
        }
    }

    /* JADX INFO: renamed from: lambda$startDriverDoorQueryIfNeeded$7$ru-big-town-anative-CanBusEventHub, reason: not valid java name */
    /* synthetic */ void m1858x968f3a96(final IBinder iBinder, final long j, final long j2) {
        final Integer driverDoor = readDriverDoor(iBinder);
        if (this.ioHandler.post(new Runnable() { // from class: ru.big.town.anative.CanBusEventHub$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1857x22500237(iBinder, j, j2, driverDoor);
            }
        })) {
            return;
        }
        Log.w(TAG, "Door query completion rejected: hub IO stopped");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: finishDriverDoorQuery, reason: merged with bridge method [inline-methods] */
    public void m1857x22500237(IBinder iBinder, long j, long j2, Integer num) {
        if (num != null) {
            try {
                synchronized (this.eventLock) {
                    if (this.activeEpoch == j && this.readyEpoch == j && iBinder == this.remote) {
                        long j3 = this.doorRevision;
                        if (j2 == j3) {
                            this.doorRevision = j3 + 1;
                            CanBusEventRouter canBusEventRouter = this.router;
                            CanBusEvent.Origin origin = CanBusEvent.Origin.SEED;
                            long j4 = this.nextSequence + 1;
                            this.nextSequence = j4;
                            canBusEventRouter.dispatch(CanBusEvent.door(origin, j, j4, SystemClock.elapsedRealtime(), num.intValue()));
                        }
                    }
                }
            } catch (Throwable th) {
                this.doorQueryGate.complete();
                startDriverDoorQueryIfNeeded();
                throw th;
            }
        }
        this.doorQueryGate.complete();
        startDriverDoorQueryIfNeeded();
    }

    private Integer readDriverDoor(IBinder iBinder) {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken(CANBUS_DESCRIPTOR);
                if (iBinder.transact(2, parcelObtain, parcelObtain2, 0)) {
                    parcelObtain2.readException();
                    if (parcelObtain2.readInt() != 0) {
                        parcelObtain2.readInt();
                        return Integer.valueOf(parcelObtain2.readInt());
                    }
                }
            } catch (RemoteException | RuntimeException e) {
                Log.w(TAG, "getDoorStatus failed: " + e.getMessage());
            }
            return null;
        } finally {
            parcelObtain.recycle();
            parcelObtain2.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void acceptVehicleStateQueryRequest() {
        this.vehicleSnapshotRequestPosted.set(false);
        this.vehicleQueryGate.request();
        startVehicleStateQueryIfNeeded();
    }

    private void startVehicleStateQueryIfNeeded() {
        if (this.vehicleQueryGate.tryStart()) {
            final IBinder iBinder = this.remote;
            final long j = this.activeEpoch;
            if (iBinder == null || j == 0) {
                this.vehicleQueryGate.complete();
                return;
            }
            synchronized (this.eventLock) {
                if (this.readyEpoch != j) {
                    this.vehicleQueryGate.complete();
                } else {
                    if (this.vehicleQueryHandler.post(new Runnable() { // from class: ru.big.town.anative.CanBusEventHub$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.m1859x339b20c4(iBinder, j);
                        }
                    })) {
                        return;
                    }
                    this.vehicleQueryGate.complete();
                    startVehicleStateQueryIfNeeded();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: queryVehicleState, reason: merged with bridge method [inline-methods] */
    public void m1859x339b20c4(IBinder iBinder, long j) {
        boolean zPost;
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInterfaceToken(CANBUS_DESCRIPTOR);
            if (!iBinder.transact(20, parcelObtain, parcelObtain2, 0)) {
                if (zPost) {
                    return;
                } else {
                    return;
                }
            }
            parcelObtain2.readException();
            long jElapsedRealtime2 = SystemClock.elapsedRealtime() - jElapsedRealtime;
            if (jElapsedRealtime2 > 1000) {
                Log.w(TAG, "TX20 took " + jElapsedRealtime2 + " ms");
            }
        } catch (RemoteException | RuntimeException e) {
            Log.w(TAG, "queryVehicleState failed: " + e.getMessage());
        } finally {
            parcelObtain.recycle();
            parcelObtain2.recycle();
            if (!this.ioHandler.post(new Runnable() { // from class: ru.big.town.anative.CanBusEventHub$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.finishVehicleStateQuery();
                }
            })) {
                Log.w(TAG, "Vehicle query completion rejected: hub IO stopped");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishVehicleStateQuery() {
        this.vehicleQueryGate.complete();
        startVehicleStateQueryIfNeeded();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void routeDoor(long j, int i) {
        synchronized (this.eventLock) {
            if (j != this.activeEpoch) {
                return;
            }
            this.doorRevision++;
            CanBusEvent.Origin origin = CanBusEvent.Origin.LIVE;
            long j2 = this.nextSequence + 1;
            this.nextSequence = j2;
            routeLocked(CanBusEvent.door(origin, j, j2, SystemClock.elapsedRealtime(), i));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void routeGear(long j, int i) {
        synchronized (this.eventLock) {
            if (j != this.activeEpoch) {
                return;
            }
            CanBusEvent.Origin origin = CanBusEvent.Origin.LIVE;
            long j2 = 1 + this.nextSequence;
            this.nextSequence = j2;
            routeLocked(CanBusEvent.gear(origin, j, j2, SystemClock.elapsedRealtime(), i));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void routeLight(long j, int i, int i2, int i3) {
        synchronized (this.eventLock) {
            if (j != this.activeEpoch) {
                return;
            }
            CanBusEvent.Origin origin = CanBusEvent.Origin.LIVE;
            long j2 = 1 + this.nextSequence;
            this.nextSequence = j2;
            routeLocked(CanBusEvent.light(origin, j, j2, SystemClock.elapsedRealtime(), i, i2, i3));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void routeVehicleState(long j, int i, int i2) {
        if (this.router.hasVehicleStateInterest(i)) {
            synchronized (this.eventLock) {
                if (j == this.activeEpoch && this.router.hasVehicleStateInterest(i)) {
                    CanBusEvent.Origin origin = CanBusEvent.Origin.LIVE;
                    long j2 = 1 + this.nextSequence;
                    this.nextSequence = j2;
                    routeLocked(CanBusEvent.vehicleState(origin, j, j2, SystemClock.elapsedRealtime(), i, i2));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void routeAmbientTemperature(long j, int i) {
        synchronized (this.eventLock) {
            if (j != this.activeEpoch) {
                return;
            }
            CanBusEvent.Origin origin = CanBusEvent.Origin.LIVE;
            long j2 = 1 + this.nextSequence;
            this.nextSequence = j2;
            routeLocked(CanBusEvent.ambientTemperature(origin, j, j2, SystemClock.elapsedRealtime(), i));
        }
    }

    private void routeLocked(CanBusEvent canBusEvent) {
        if (this.readyEpoch == canBusEvent.connectionEpoch) {
            this.router.dispatch(canBusEvent);
            return;
        }
        if (this.preReadyEvents.size() == 64) {
            this.preReadyEvents.removeFirst();
            this.preReadyDrops.incrementAndGet();
        }
        this.preReadyEvents.addLast(canBusEvent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void malformed(int i, RuntimeException runtimeException) {
        long jIncrementAndGet = this.malformedCallbacks.incrementAndGet();
        if (((jIncrementAndGet - 1) & jIncrementAndGet) == 0) {
            Log.w(TAG, "Dropped malformed callback code=" + i + " count=" + jIncrementAndGet + ": " + runtimeException.getMessage());
        }
    }

    private final class CallbackBinder extends Binder {
        private final long epoch;

        CallbackBinder(long j) {
            this.epoch = j;
            attachInterface(null, CanBusEventHub.CALLBACK_DESCRIPTOR);
        }

        @Override // android.os.Binder
        protected boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            int i3;
            int i4;
            try {
                if (i == 1) {
                    if (!CanBusEventHub.this.router.hasInterest(2)) {
                        return true;
                    }
                    parcel.enforceInterface(CanBusEventHub.CALLBACK_DESCRIPTOR);
                    if (parcel.readInt() == 0) {
                        return true;
                    }
                    parcel.readInt();
                    CanBusEventHub.this.routeDoor(this.epoch, parcel.readInt());
                    return true;
                }
                int i5 = 0;
                if (i == 4) {
                    if (!CanBusEventHub.this.router.hasInterest(32)) {
                        return true;
                    }
                    parcel.enforceInterface(CanBusEventHub.CALLBACK_DESCRIPTOR);
                    int i6 = parcel.readInt();
                    int i7 = CanBusEventHub.AIR_TEMPERATURE_INVALID;
                    if (i6 != 0) {
                        while (i5 <= 35) {
                            if (i5 >= 11 && i5 <= 13) {
                                parcel.readFloat();
                            } else {
                                int i8 = parcel.readInt();
                                if (i5 == 35) {
                                    i7 = i8;
                                }
                            }
                            i5++;
                        }
                    }
                    CanBusEventHub.this.routeAmbientTemperature(this.epoch, i7);
                    return true;
                }
                int i9 = -1;
                if (i == 10) {
                    if (!CanBusEventHub.this.router.hasInterest(8)) {
                        return true;
                    }
                    parcel.enforceInterface(CanBusEventHub.CALLBACK_DESCRIPTOR);
                    if (parcel.readInt() != 0) {
                        int i10 = -1;
                        int i11 = -1;
                        while (i5 < 17) {
                            int i12 = parcel.readInt();
                            if (i5 == 7) {
                                i10 = i12;
                            } else if (i5 == 13) {
                                i11 = i12;
                            } else if (i5 == 16) {
                                i9 = i12;
                            }
                            i5++;
                        }
                        i4 = i10;
                        i3 = i9;
                        i9 = i11;
                    } else {
                        i3 = -1;
                        i4 = -1;
                    }
                    CanBusEventHub.this.routeLight(this.epoch, i3, i4, i9);
                    return true;
                }
                if (i == 12) {
                    if (!CanBusEventHub.this.router.hasInterest(4)) {
                        return true;
                    }
                    parcel.enforceInterface(CanBusEventHub.CALLBACK_DESCRIPTOR);
                    if (parcel.readInt() == 0) {
                        return true;
                    }
                    parcel.readInt();
                    CanBusEventHub.this.routeGear(this.epoch, parcel.readInt());
                    return true;
                }
                if (i != 36) {
                    if (i < 1 || i > 16777215) {
                        return super.onTransact(i, parcel, parcel2, i2);
                    }
                    return true;
                }
                if (!CanBusEventHub.this.router.hasInterest(16)) {
                    return true;
                }
                parcel.enforceInterface(CanBusEventHub.CALLBACK_DESCRIPTOR);
                i5 = parcel.readInt() != 0 ? 1 : 0;
                if (i5 != 0) {
                    parcel.readInt();
                    i9 = parcel.readInt();
                }
                int i13 = parcel.readInt();
                if (i5 != 0) {
                    CanBusEventHub.this.routeVehicleState(this.epoch, i9, i13);
                }
                return true;
            } catch (RuntimeException e) {
                CanBusEventHub.this.malformed(i, e);
                return true;
            }
        }
    }

    private final class GenerationServiceConnection implements ServiceConnection {
        private final long generation;

        GenerationServiceConnection(long j) {
            this.generation = j;
        }

        private boolean isCurrent() {
            return CanBusEventHub.this.serviceConnection == this && CanBusEventHub.this.activeBindingGeneration == this.generation;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (isCurrent()) {
                CanBusEventHub.this.handleServiceConnected(iBinder);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            if (isCurrent()) {
                CanBusEventHub.this.clearRemoteSession("service disconnected");
                Log.w(CanBusEventHub.TAG, "CanBus service disconnected; waiting for framework reconnect");
            }
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            if (isCurrent()) {
                CanBusEventHub.this.restartBinding("binding died");
            }
        }

        @Override // android.content.ServiceConnection
        public void onNullBinding(ComponentName componentName) {
            if (isCurrent()) {
                CanBusEventHub.this.restartBinding("null binding");
            }
        }
    }

    static final class Subscription implements AutoCloseable {
        private boolean closed;
        private final Handler deliveryHandler;
        private final int interestMask;
        private final Listener listener;
        private final CanBusEventHub owner;
        private CanBusEventRouter.Subscription routed;
        private final int[] vehicleStateIds;

        Subscription(CanBusEventHub canBusEventHub, int i, int[] iArr, Handler handler, Listener listener) {
            this.owner = canBusEventHub;
            this.interestMask = i;
            this.vehicleStateIds = iArr;
            this.deliveryHandler = handler;
            this.listener = listener;
        }

        synchronized boolean isClosed() {
            return this.closed;
        }

        synchronized boolean attach(CanBusEventRouter.Subscription subscription) {
            if (this.closed) {
                subscription.close();
                return false;
            }
            this.routed = subscription;
            return true;
        }

        void forgetLightStatus() {
            synchronized (this) {
                if (this.closed) {
                    return;
                }
                CanBusEventRouter.Subscription subscription = this.routed;
                if (subscription != null) {
                    subscription.forgetSignal(CanBusEvent.Kind.LIGHT_STATUS);
                }
            }
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            synchronized (this) {
                if (this.closed) {
                    return;
                }
                this.closed = true;
                CanBusEventRouter.Subscription subscription = this.routed;
                this.routed = null;
                if (subscription != null) {
                    subscription.close();
                }
                this.owner.onSubscriptionClosed();
            }
        }
    }
}
