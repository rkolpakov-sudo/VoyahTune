package ru.big.town.anative;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import dalvik.system.PathClassLoader;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/* JADX INFO: loaded from: classes2.dex */
final class HeadlightCanTransport {
    private static final String CANBUS_ACTION = "com.qinggan.canbus.CanBusService";
    private static final String CANBUS_DESCRIPTOR = "com.qinggan.canbus.ICanBusService";
    private static final String CANBUS_PACKAGE = "com.qinggan.canbus.service";
    private static final HeadlightCanTransport INSTANCE = new HeadlightCanTransport();
    private static final long REBIND_DELAY_MS = 5000;
    private static final String TAG = "$$$ HeadlightCanTransport $$$";
    private static final int TX_SET_VEHICLE_STATE = 58;
    private static final String WRITE_CANBUS_PERMISSION = "com.qinggan.permission.WRITE_CANBUS";
    private Context appContext;
    private IBinder canBusBinder;
    private boolean connectionRegistered;
    private boolean initialized;
    private boolean schemaReady;
    private final Object lock = new Object();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService schemaExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() { // from class: ru.big.town.anative.HeadlightCanTransport$$ExternalSyntheticLambda0
        @Override // java.util.concurrent.ThreadFactory
        public final Thread newThread(Runnable runnable) {
            return HeadlightCanTransport.lambda$new$0(runnable);
        }
    });
    private final EnumMap<HeadlightCanPolicy.Command, Integer> ordinals = new EnumMap<>(HeadlightCanPolicy.Command.class);
    private final Runnable rebindRunnable = this::restartBinding;
    private final ServiceConnection connection = new ServiceConnection() { // from class: ru.big.town.anative.HeadlightCanTransport.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                String interfaceDescriptor = iBinder.getInterfaceDescriptor();
                if (!HeadlightCanTransport.CANBUS_DESCRIPTOR.equals(interfaceDescriptor)) {
                    Log.e(HeadlightCanTransport.TAG, "Unexpected Binder descriptor: " + interfaceDescriptor);
                    HeadlightCanTransport.this.rejectBinding();
                    return;
                }
                synchronized (HeadlightCanTransport.this.lock) {
                    HeadlightCanTransport.this.canBusBinder = iBinder;
                }
                HeadlightCanTransport.this.mainHandler.removeCallbacks(HeadlightCanTransport.this.rebindRunnable);
                Log.i(HeadlightCanTransport.TAG, "CanBusService connected; headlight TX58 ready");
            } catch (RemoteException | RuntimeException e) {
                Log.e(HeadlightCanTransport.TAG, "Cannot verify CanBus Binder descriptor", e);
                HeadlightCanTransport.this.restartBinding();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            synchronized (HeadlightCanTransport.this.lock) {
                HeadlightCanTransport.this.canBusBinder = null;
            }
            Log.w(HeadlightCanTransport.TAG, "CanBusService disconnected; waiting for reconnect");
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            Log.w(HeadlightCanTransport.TAG, "CanBusService binding died");
            HeadlightCanTransport.this.restartBinding();
        }

        @Override // android.content.ServiceConnection
        public void onNullBinding(ComponentName componentName) {
            Log.e(HeadlightCanTransport.TAG, "CanBusService returned a null binding");
            HeadlightCanTransport.this.restartBinding();
        }
    };

    static /* synthetic */ Thread lambda$new$0(Runnable runnable) {
        Thread thread = new Thread(runnable, "HeadlightCanSchema");
        thread.setDaemon(true);
        return thread;
    }

    private HeadlightCanTransport() {
    }

    static void initialize(Context context) {
        INSTANCE.initializeInternal(context);
    }

    static boolean send(Context context, boolean z) {
        HeadlightCanTransport headlightCanTransport = INSTANCE;
        headlightCanTransport.initializeInternal(context);
        return headlightCanTransport.sendInternal(HeadlightCanPolicy.commandFor(z));
    }

    static boolean sendAutoPair(Context context, boolean z) {
        HeadlightCanTransport headlightCanTransport = INSTANCE;
        headlightCanTransport.initializeInternal(context);
        return headlightCanTransport.sendInternal(HeadlightCanPolicy.commandForAutoPair(z));
    }

    private void initializeInternal(Context context) {
        if (context == null) {
            return;
        }
        synchronized (this.lock) {
            if (this.initialized) {
                return;
            }
            this.initialized = true;
            Context applicationContext = context.getApplicationContext();
            this.appContext = applicationContext;
            if (applicationContext.checkSelfPermission(WRITE_CANBUS_PERMISSION) != 0) {
                Log.e(TAG, "WRITE_CANBUS permission missing; headlight commands disabled");
            } else {
                this.schemaExecutor.execute(new Runnable() { // from class: ru.big.town.anative.HeadlightCanTransport$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        HeadlightCanTransport.this.m1885xabc08eaa();
                    }
                });
            }
        }
    }

    /* JADX INFO: renamed from: lambda$initializeInternal$2$ru-big-town-anative-HeadlightCanTransport, reason: not valid java name */
    /* synthetic */ void m1885xabc08eaa() {
        final SchemaResult schemaResultResolveSchema = resolveSchema();
        this.mainHandler.post(new Runnable() { // from class: ru.big.town.anative.HeadlightCanTransport$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                HeadlightCanTransport.this.m1884xc67f1fe9(schemaResultResolveSchema);
            }
        });
    }

    private SchemaResult resolveSchema() {
        try {
            Class<?> cls = Class.forName("com.qinggan.canbus.VehicleState", true, new PathClassLoader(this.appContext.getPackageManager().getApplicationInfo(CANBUS_PACKAGE, 0).sourceDir, this.appContext.getClassLoader()));
            if (!cls.isEnum()) {
                return SchemaResult.failed("VehicleState is not an enum");
            }
            Method method = cls.getMethod("getValue", new Class[0]);
            EnumMap enumMap = new EnumMap(HeadlightCanPolicy.Command.class);
            for (HeadlightCanPolicy.Command command : HeadlightCanPolicy.Command.values()) {
                Enum enumValueOf = Enum.valueOf((Class) cls, command.vehicleStateName);
                Object objInvoke = method.invoke(enumValueOf, new Object[0]);
                if (!(objInvoke instanceof Integer) || ((Integer) objInvoke).intValue() != command.stableId) {
                    return SchemaResult.failed("VehicleState id mismatch for " + command.vehicleStateName);
                }
                enumMap.put(command, Integer.valueOf(enumValueOf.ordinal()));
            }
            return SchemaResult.success(enumMap);
        } catch (PackageManager.NameNotFoundException unused) {
            return SchemaResult.failed("CanBusService APK not found");
        } catch (ReflectiveOperationException | RuntimeException e) {
            Log.e(TAG, "Cannot resolve VehicleState schema", e);
            return SchemaResult.failed("VehicleState schema unavailable");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: applySchema, reason: merged with bridge method [inline-methods] */
    public void m1884xc67f1fe9(SchemaResult schemaResult) {
        synchronized (this.lock) {
            this.ordinals.clear();
            if (schemaResult.success) {
                this.ordinals.putAll(schemaResult.ordinals);
            }
            this.schemaReady = schemaResult.success;
        }
        if (!schemaResult.success) {
            Log.e(TAG, "Headlight Binder disabled: " + schemaResult.error);
        } else {
            Log.i(TAG, "VehicleState schema verified for LOW_BEAM, OUT_LAMP_OFF and AUTO_LAMP_SWITCH");
            bindCanBus();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bindCanBus() {
        Context context;
        synchronized (this.lock) {
            if (this.schemaReady && !this.connectionRegistered && (context = this.appContext) != null) {
                try {
                    Intent intent = new Intent(CANBUS_ACTION);
                    intent.setPackage(CANBUS_PACKAGE);
                    boolean zBindService = context.bindService(intent, this.connection, 1);
                    synchronized (this.lock) {
                        this.connectionRegistered = zBindService;
                    }
                    Log.i(TAG, "bindService returned " + zBindService);
                    if (zBindService) {
                        return;
                    }
                    scheduleRebind();
                } catch (RuntimeException e) {
                    Log.e(TAG, "CanBus bind failed", e);
                    synchronized (this.lock) {
                        this.connectionRegistered = false;
                        scheduleRebind();
                    }
                }
            }
        }
    }

    private boolean sendInternal(HeadlightCanPolicy.Command command) {
        IBinder iBinder;
        Integer num;
        synchronized (this.lock) {
            iBinder = this.canBusBinder;
            num = this.ordinals.get(command);
        }
        if (iBinder == null || num == null || !iBinder.isBinderAlive()) {
            Log.w(TAG, "TX58 unavailable for " + command.vehicleStateName);
            this.mainHandler.post(this::restartBinding);
            return false;
        }
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken(CANBUS_DESCRIPTOR);
                parcelObtain.writeInt(1);
                parcelObtain.writeInt(num.intValue());
                parcelObtain.writeInt(command.stableId);
                parcelObtain.writeInt(1);
                if (!iBinder.transact(58, parcelObtain, parcelObtain2, 0)) {
                    Log.e(TAG, "TX58 rejected for " + command.vehicleStateName);
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                    return false;
                }
                parcelObtain2.readException();
                Log.i(TAG, "TX58 " + command.vehicleStateName + " state=1");
                parcelObtain2.recycle();
                parcelObtain.recycle();
                return true;
            } catch (RemoteException | RuntimeException e) {
                Log.e(TAG, "TX58 failed for " + command.vehicleStateName, e);
                synchronized (this.lock) {
                    if (this.canBusBinder == iBinder) {
                        this.canBusBinder = null;
                    }
                    this.mainHandler.post(new Runnable() { // from class: ru.big.town.anative.HeadlightCanTransport$$ExternalSyntheticLambda4
                        @Override // java.lang.Runnable
                        public final void run() {
                            HeadlightCanTransport.this.restartBinding();
                        }
                    });
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                    return false;
                }
            }
        } catch (Throwable th) {
            parcelObtain2.recycle();
            parcelObtain.recycle();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void rejectBinding() {
        synchronized (this.lock) {
            this.canBusBinder = null;
        }
        unbindCurrent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restartBinding() {
        synchronized (this.lock) {
            this.canBusBinder = null;
        }
        unbindCurrent();
        scheduleRebind();
    }

    private void unbindCurrent() {
        Context context;
        boolean z;
        synchronized (this.lock) {
            context = this.appContext;
            z = this.connectionRegistered;
            this.connectionRegistered = false;
        }
        if (!z || context == null) {
            return;
        }
        try {
            context.unbindService(this.connection);
        } catch (RuntimeException e) {
            Log.w(TAG, "CanBus unbind failed", e);
        }
    }

    private void scheduleRebind() {
        this.mainHandler.removeCallbacks(this.rebindRunnable);
        this.mainHandler.postDelayed(this.rebindRunnable, REBIND_DELAY_MS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class SchemaResult {
        final String error;
        final EnumMap<HeadlightCanPolicy.Command, Integer> ordinals;
        final boolean success;

        private SchemaResult(boolean z, EnumMap<HeadlightCanPolicy.Command, Integer> enumMap, String str) {
            this.success = z;
            this.ordinals = enumMap;
            this.error = str;
        }

        static SchemaResult success(EnumMap<HeadlightCanPolicy.Command, Integer> enumMap) {
            return new SchemaResult(true, enumMap, null);
        }

        static SchemaResult failed(String str) {
            return new SchemaResult(false, new EnumMap(HeadlightCanPolicy.Command.class), str);
        }
    }
}
