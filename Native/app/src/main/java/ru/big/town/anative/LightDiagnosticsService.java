package ru.big.town.anative;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes2.dex */
public final class LightDiagnosticsService extends Service {
    private static final String CALLBACK_DESCRIPTOR = "com.qinggan.carsignal.ICarSignalServiceCallBack";
    private static final String CAR_ACTION = "com.qinggan.carsignal.CarSignalService";
    private static final String CAR_DESCRIPTOR = "com.qinggan.carsignal.ICarSignalService";
    private static final String CAR_PACKAGE = "com.qinggan.carsignal.service";
    private static final String TAG = "LightDiagnostics";
    private static final int UNKNOWN = Integer.MIN_VALUE;
    public static final int UPDATE = 2;
    public static final String VALUES = "values";
    public static final int WATCH = 1;
    private int canSnapshotRevision;
    private volatile IBinder carBinder;
    private volatile IBinder carCallback;
    private ServiceConnection carConnection;
    private Messenger client;
    private int clientSession;
    private volatile int generation;
    private CanBusEventHub.Subscription subscription;
    private static final int[] RSM_IDS = {1072, 1071, 1070, 1073};
    private static final String[] RSM_NAMES = {"BCM_RSM_lightSWReason", "BCM_RSM_AmbBrightness", "BCM_RSM_FwBrightness", "BCM_RSM_IRBrightness"};
    private static final ThreadPoolExecutor IO = new ThreadPoolExecutor(2, 2, 30, TimeUnit.SECONDS, new ArrayBlockingQueue(4), new ThreadFactory() { // from class: ru.big.town.anative.LightDiagnosticsService$$ExternalSyntheticLambda4
        @Override // java.util.concurrent.ThreadFactory
        public final Thread newThread(Runnable runnable) {
            return LightDiagnosticsService.lambda$static$0(runnable);
        }
    }, new ThreadPoolExecutor.AbortPolicy());
    private final Handler main = new Handler(Looper.getMainLooper());
    private final int[] values = new int[6];
    private final Messenger endpoint = new Messenger(new Handler(Looper.getMainLooper()) { // from class: ru.big.town.anative.LightDiagnosticsService.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 1) {
                LightDiagnosticsService.this.client = message.replyTo;
                LightDiagnosticsService.this.clientSession = message.arg1;
                LightDiagnosticsService.this.publish();
                return;
            }
            super.handleMessage(message);
        }
    });

    static /* synthetic */ Thread lambda$static$0(Runnable runnable) {
        Thread thread = new Thread(runnable, "LightDiagnostics-io");
        thread.setDaemon(true);
        return thread;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        Arrays.fill(this.values, Integer.MIN_VALUE);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        startWatching();
        return this.endpoint.getBinder();
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        stopWatching();
        return false;
    }

    @Override // android.app.Service
    public void onDestroy() {
        stopWatching();
        super.onDestroy();
    }

    private void startWatching() {
        if (this.subscription != null) {
            return;
        }
        final int i = this.generation + 1;
        this.generation = i;
        Arrays.fill(this.values, Integer.MIN_VALUE);
        this.subscription = CanBusEventHub.get(this).subscribe(17, RSM_IDS, this.main, new CanBusEventHub.Listener() { // from class: ru.big.town.anative.LightDiagnosticsService$$ExternalSyntheticLambda0
            @Override // ru.big.town.anative.CanBusEventHub.Listener
            public final void onCanBusEvent(CanBusEvent canBusEvent) {
                LightDiagnosticsService.this.m1903xa87af27(i, canBusEvent);
            }
        });
        this.carConnection = new AnonymousClass2(i);
        try {
            if (bindService(new Intent(CAR_ACTION).setPackage(CAR_PACKAGE), this.carConnection, 1)) {
                return;
            }
            Log.w(TAG, "CarSignalService unavailable");
            this.carConnection = null;
        } catch (IllegalArgumentException | SecurityException e) {
            Log.w(TAG, "CarSignalService bind failed", e);
            this.carConnection = null;
        }
    }

    /* JADX INFO: renamed from: ru.big.town.anative.LightDiagnosticsService$2, reason: invalid class name */
    class AnonymousClass2 implements ServiceConnection {
        final /* synthetic */ int val$session;

        AnonymousClass2(int i) {
            this.val$session = i;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, final IBinder iBinder) {
            if (LightDiagnosticsService.this.generation != this.val$session) {
                return;
            }
            LightDiagnosticsService.this.carBinder = iBinder;
            final AnonymousClass1 anonymousClass1 = new AnonymousClass1();
            LightDiagnosticsService.this.carCallback = anonymousClass1;
            final int i = this.val$session;
            LightDiagnosticsService.submitIo(new Runnable() { // from class: ru.big.town.anative.LightDiagnosticsService$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AnonymousClass2.this.m1905x87ff08ad(iBinder, anonymousClass1, i);
                }
            });
        }

        /* JADX INFO: renamed from: ru.big.town.anative.LightDiagnosticsService$2$1, reason: invalid class name */
        class AnonymousClass1 extends Binder {
            AnonymousClass1() {
            }

            /* JADX INFO: renamed from: lambda$onTransact$0$ru-big-town-anative-LightDiagnosticsService$2$1, reason: not valid java name */
            /* synthetic */ void m1906x2a38a38f(int i, int i2, int i3) {
                if (LightDiagnosticsService.this.generation == i && LightDiagnosticsService.this.carCallback == this) {
                    LightDiagnosticsService.this.values[i2] = i3;
                    LightDiagnosticsService.this.publish();
                }
            }

            @Override // android.os.Binder
            protected boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
                if (i != 13 && i != 25) {
                    if (i < 1 || i > 16777215) {
                        return super.onTransact(i, parcel, parcel2, i2);
                    }
                    return true;
                }
                parcel.enforceInterface(LightDiagnosticsService.CALLBACK_DESCRIPTOR);
                final int i3 = parcel.readInt();
                final int i4 = i == 13 ? 4 : 5;
                Handler handler = LightDiagnosticsService.this.main;
                final int i5 = AnonymousClass2.this.val$session;
                handler.post(new Runnable() { // from class: ru.big.town.anative.LightDiagnosticsService$2$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        AnonymousClass1.this.m1906x2a38a38f(i5, i4, i3);
                    }
                });
                return true;
            }
        }

        /* JADX INFO: renamed from: lambda$onServiceConnected$1$ru-big-town-anative-LightDiagnosticsService$2, reason: not valid java name */
        /* synthetic */ void m1905x87ff08ad(final IBinder iBinder, IBinder iBinder2, final int i) {
            boolean zTransactCallback = LightDiagnosticsService.transactCallback(iBinder, 46, iBinder2);
            if (LightDiagnosticsService.this.generation == i && LightDiagnosticsService.this.carBinder == iBinder && LightDiagnosticsService.this.carCallback == iBinder2) {
                final int carInt = LightDiagnosticsService.readCarInt(iBinder, 36);
                final int carInt2 = LightDiagnosticsService.readCarInt(iBinder, 73);
                LightDiagnosticsService.this.main.post(new Runnable() { // from class: ru.big.town.anative.LightDiagnosticsService$2$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        AnonymousClass2.this.m1904x1284e26c(i, iBinder, carInt, carInt2);
                    }
                });
            } else if (zTransactCallback) {
                LightDiagnosticsService.transactCallback(iBinder, 47, iBinder2);
            }
        }

        /* JADX INFO: renamed from: lambda$onServiceConnected$0$ru-big-town-anative-LightDiagnosticsService$2, reason: not valid java name */
        /* synthetic */ void m1904x1284e26c(int i, IBinder iBinder, int i2, int i3) {
            if (LightDiagnosticsService.this.generation == i && LightDiagnosticsService.this.carBinder == iBinder) {
                if (LightDiagnosticsService.this.values[4] == Integer.MIN_VALUE) {
                    LightDiagnosticsService.this.values[4] = i2;
                }
                if (LightDiagnosticsService.this.values[5] == Integer.MIN_VALUE) {
                    LightDiagnosticsService.this.values[5] = i3;
                }
                LightDiagnosticsService.this.publish();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            if (LightDiagnosticsService.this.generation == this.val$session) {
                LightDiagnosticsService.this.clearCarSignal();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: onCanEvent, reason: merged with bridge method [inline-methods] */
    public void m1903xa87af27(final int i, CanBusEvent canBusEvent) {
        if (this.generation != i) {
            return;
        }
        if (canBusEvent.kind == CanBusEvent.Kind.CONNECTION_LOST) {
            this.canSnapshotRevision++;
            Arrays.fill(this.values, 0, 4, Integer.MIN_VALUE);
            publish();
        } else if (canBusEvent.kind == CanBusEvent.Kind.CONNECTION) {
            final int i2 = this.canSnapshotRevision + 1;
            this.canSnapshotRevision = i2;
            submitIo(new Runnable() { // from class: ru.big.town.anative.LightDiagnosticsService$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    LightDiagnosticsService.this.m1902lambda$onCanEvent$3$rubigtownanativeLightDiagnosticsService(i, i2);
                }
            });
        } else if (canBusEvent.kind == CanBusEvent.Kind.VEHICLE_STATE) {
            for (int i3 = 0; i3 < 4; i3++) {
                if (canBusEvent.first == RSM_IDS[i3]) {
                    this.values[i3] = canBusEvent.second;
                    publish();
                    return;
                }
            }
        }
    }

    /* JADX INFO: renamed from: lambda$onCanEvent$3$ru-big-town-anative-LightDiagnosticsService, reason: not valid java name */
    /* synthetic */ void m1902lambda$onCanEvent$3$rubigtownanativeLightDiagnosticsService(final int i, final int i2) {
        final OemVehicleStateTransport.StateKey[] stateKeyArr = new OemVehicleStateTransport.StateKey[4];
        for (int i3 = 0; i3 < 4; i3++) {
            stateKeyArr[i3] = new OemVehicleStateTransport.StateKey(RSM_NAMES[i3], RSM_IDS[i3]);
        }
        final Map<OemVehicleStateTransport.StateKey, Integer> vehicleStates = OemVehicleStateTransport.readVehicleStates(this, Arrays.asList(stateKeyArr));
        this.main.post(new Runnable() { // from class: ru.big.town.anative.LightDiagnosticsService$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                LightDiagnosticsService.this.m1901lambda$onCanEvent$2$rubigtownanativeLightDiagnosticsService(i, i2, vehicleStates, stateKeyArr);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$onCanEvent$2$ru-big-town-anative-LightDiagnosticsService, reason: not valid java name */
    /* synthetic */ void m1901lambda$onCanEvent$2$rubigtownanativeLightDiagnosticsService(int i, int i2, Map map, OemVehicleStateTransport.StateKey[] stateKeyArr) {
        if (this.generation == i && this.canSnapshotRevision == i2 && map != null) {
            for (int i3 = 0; i3 < 4; i3++) {
                if (this.values[i3] == Integer.MIN_VALUE && map.get(stateKeyArr[i3]) != null) {
                    this.values[i3] = ((Integer) map.get(stateKeyArr[i3])).intValue();
                }
            }
            publish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearCarSignal() {
        this.carBinder = null;
        this.carCallback = null;
        int[] iArr = this.values;
        iArr[4] = Integer.MIN_VALUE;
        iArr[5] = Integer.MIN_VALUE;
        publish();
    }

    private void stopWatching() {
        if (this.subscription == null && this.carConnection == null) {
            return;
        }
        this.generation++;
        this.client = null;
        CanBusEventHub.Subscription subscription = this.subscription;
        if (subscription != null) {
            subscription.close();
            this.subscription = null;
        }
        final IBinder iBinder = this.carBinder;
        final IBinder iBinder2 = this.carCallback;
        this.carBinder = null;
        this.carCallback = null;
        ServiceConnection serviceConnection = this.carConnection;
        if (serviceConnection != null) {
            try {
                unbindService(serviceConnection);
            } catch (IllegalArgumentException unused) {
            }
            this.carConnection = null;
        }
        if (iBinder != null && iBinder2 != null) {
            submitIo(new Runnable() { // from class: ru.big.town.anative.LightDiagnosticsService$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    LightDiagnosticsService.transactCallback(iBinder, 47, iBinder2);
                }
            });
        }
        Arrays.fill(this.values, Integer.MIN_VALUE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void publish() {
        if (this.client == null) {
            return;
        }
        Message messageObtain = Message.obtain((Handler) null, 2);
        messageObtain.arg1 = this.clientSession;
        Bundle bundle = new Bundle();
        bundle.putIntArray(VALUES, (int[]) this.values.clone());
        messageObtain.setData(bundle);
        try {
            this.client.send(messageObtain);
        } catch (RemoteException unused) {
            this.client = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void submitIo(Runnable runnable) {
        try {
            IO.execute(runnable);
        } catch (RejectedExecutionException e) {
            Log.w(TAG, "Diagnostic OEM queue full", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean transactCallback(IBinder iBinder, int i, IBinder iBinder2) {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken(CAR_DESCRIPTOR);
                parcelObtain.writeStrongBinder(iBinder2);
                if (iBinder.transact(i, parcelObtain, parcelObtain2, 0)) {
                    parcelObtain2.readException();
                    return true;
                }
            } catch (RemoteException | RuntimeException e) {
                Log.w(TAG, "CarSignal callback transaction " + i + " failed", e);
            }
            return false;
        } finally {
            parcelObtain.recycle();
            parcelObtain2.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int readCarInt(IBinder iBinder, int i) {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken(CAR_DESCRIPTOR);
                if (iBinder.transact(i, parcelObtain, parcelObtain2, 0)) {
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                }
            } catch (RemoteException | RuntimeException e) {
                Log.w(TAG, "CarSignal read " + i + " failed", e);
            }
            return Integer.MIN_VALUE;
        } finally {
            parcelObtain.recycle();
            parcelObtain2.recycle();
        }
    }
}
