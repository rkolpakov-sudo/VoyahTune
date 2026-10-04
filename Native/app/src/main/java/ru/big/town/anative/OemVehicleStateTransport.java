package ru.big.town.anative;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import dalvik.system.PathClassLoader;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/* JADX INFO: loaded from: classes2.dex */
final class OemVehicleStateTransport {
    private static final long BIND_WAIT_MS = 1500;
    private static final String CANBUS_ACTION = "com.qinggan.canbus.CanBusService";
    private static final String CANBUS_DESCRIPTOR = "com.qinggan.canbus.ICanBusService";
    private static final String CANBUS_PACKAGE = "com.qinggan.canbus.service";
    private static final OemVehicleStateTransport INSTANCE = new OemVehicleStateTransport();
    private static final long STALE_BIND_MS = 5000;
    private static final String TAG = "$$$ OemVehicleState $$$";
    private static final int TX_FUEL_LEVEL = 9;
    private static final int TX_GEAR_STATUS = 6;
    private static final int TX_GET_VEHICLE_STATE = 57;
    private static final int TX_SET_VEHICLE_AND_AIR_BUNDLE_STATE = 77;
    private static final int TX_SET_VEHICLE_STATE = 58;
    private static final String WRITE_CANBUS_PERMISSION = "com.qinggan.permission.WRITE_CANBUS";
    private DemandConnection activeConnection;
    private Context appContext;
    private long bindingGeneration;
    private boolean bindingInProgress;
    private long bindingStartedElapsed;
    private IBinder canBusBinder;
    private boolean connectionRegistered;
    private Class<? extends Enum> vehicleStateClass;
    private Method vehicleStateGetValue;
    private final Object connectionLock = new Object();
    private final Object schemaLock = new Object();
    private final Object transactionLock = new Object();
    private final Map<StateKey, Integer> resolvedOrdinals = new HashMap();

    interface Session {
        default FuelLevel readFuelLevel() {
            return null;
        }

        GearStatus readGearStatus();

        default Integer readVehicleSpeed() {
            return null;
        }

        Integer readVehicleState(StateKey stateKey);

        Result sendBundle(Map<StateKey, Integer> map, String str);

        Result sendVehicleState(StateValue stateValue, String str);
    }

    interface SessionOperation<T> {
        T run(Session session);
    }

    enum Result {
        ACCEPTED_UNCONFIRMED,
        TRANSIENT_FAILURE;

        boolean accepted() {
            return this == ACCEPTED_UNCONFIRMED;
        }
    }

    static final class StateKey {
        final String name;
        final int stableId;

        StateKey(String str, int i) {
            if (str == null || str.isEmpty()) {
                throw new IllegalArgumentException("VehicleState name is empty");
            }
            this.name = str;
            this.stableId = i;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof StateKey)) {
                return false;
            }
            StateKey stateKey = (StateKey) obj;
            return this.stableId == stateKey.stableId && this.name.equals(stateKey.name);
        }

        public int hashCode() {
            return (this.name.hashCode() * 31) + this.stableId;
        }

        public String toString() {
            return this.name + "(" + this.stableId + ")";
        }
    }

    static final class StateValue {
        final StateKey key;
        final int value;

        StateValue(StateKey stateKey, int i) {
            this.key = (StateKey) Objects.requireNonNull(stateKey, "key");
            this.value = i;
        }
    }

    static final class GearStatus {
        final int ordinal;
        final int value;

        GearStatus(int i, int i2) {
            this.ordinal = i;
            this.value = i2;
        }
    }

    static final class FuelLevel {
        final int capacityLiters;
        final float remainingPercent;

        FuelLevel(int i, float f) {
            this.capacityLiters = i;
            this.remainingPercent = f;
        }
    }

    private final class DemandConnection implements ServiceConnection {
        final long generation;

        DemandConnection(long j) {
            this.generation = j;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                String interfaceDescriptor = iBinder.getInterfaceDescriptor();
                if (!OemVehicleStateTransport.CANBUS_DESCRIPTOR.equals(interfaceDescriptor)) {
                    Log.e(OemVehicleStateTransport.TAG, "Unexpected Binder descriptor: " + interfaceDescriptor);
                    OemVehicleStateTransport.this.dropBinding(this, iBinder);
                    return;
                }
                synchronized (OemVehicleStateTransport.this.connectionLock) {
                    if (OemVehicleStateTransport.this.activeConnection != this) {
                        return;
                    }
                    OemVehicleStateTransport.this.canBusBinder = iBinder;
                    OemVehicleStateTransport.this.bindingInProgress = false;
                    OemVehicleStateTransport.this.connectionLock.notifyAll();
                    Log.i(OemVehicleStateTransport.TAG, "CanBusService connected for VehicleState demand gen=" + this.generation);
                }
            } catch (RemoteException | RuntimeException e) {
                Log.e(OemVehicleStateTransport.TAG, "Cannot verify CanBus Binder descriptor", e);
                OemVehicleStateTransport.this.dropBinding(this, iBinder);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            synchronized (OemVehicleStateTransport.this.connectionLock) {
                if (OemVehicleStateTransport.this.activeConnection != this) {
                    return;
                }
                OemVehicleStateTransport.this.canBusBinder = null;
                OemVehicleStateTransport.this.bindingInProgress = true;
                OemVehicleStateTransport.this.bindingStartedElapsed = SystemClock.elapsedRealtime();
                OemVehicleStateTransport.this.connectionLock.notifyAll();
                Log.w(OemVehicleStateTransport.TAG, "CanBusService disconnected; no independent rebind scheduled");
            }
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            Log.w(OemVehicleStateTransport.TAG, "CanBusService binding died");
            OemVehicleStateTransport.this.dropBinding(this, null);
        }

        @Override // android.content.ServiceConnection
        public void onNullBinding(ComponentName componentName) {
            Log.e(OemVehicleStateTransport.TAG, "CanBusService returned a null binding");
            OemVehicleStateTransport.this.dropBinding(this, null);
        }
    }

    private OemVehicleStateTransport() {
    }

    static Result sendVehicleState(Context context, String str, int i, int i2, String str2) {
        return sendVehicleState(context, new StateKey(str, i), i2, str2);
    }

    static Result sendVehicleState(Context context, StateKey stateKey, int i, String str) {
        return INSTANCE.sendSingleInternal(context, new StateValue(stateKey, i), str);
    }

    static <T> T withSession(Context context, Collection<StateKey> collection, SessionOperation<T> sessionOperation) {
        return (T) INSTANCE.withSessionInternal(context, (Collection) Objects.requireNonNull(collection, "VehicleState keys"), (SessionOperation) Objects.requireNonNull(sessionOperation, "session operation"));
    }

    static Result sendBundle(Context context, Map<StateKey, Integer> map, String str) {
        return INSTANCE.sendBundleInternal(context, immutableCopy(map), str);
    }

    static Map<StateKey, Integer> readVehicleStates(Context context, Collection<StateKey> collection) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException("VehicleState snapshot is empty");
        }
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        Iterator<StateKey> it = collection.iterator();
        while (it.hasNext()) {
            linkedHashMap.put((StateKey) Objects.requireNonNull(it.next(), "VehicleState key"), 0);
        }
        return (Map) withSession(context, linkedHashMap.keySet(), new SessionOperation() { // from class: ru.big.town.anative.OemVehicleStateTransport$$ExternalSyntheticLambda0
            @Override // ru.big.town.anative.OemVehicleStateTransport.SessionOperation
            public final Object run(OemVehicleStateTransport.Session session) {
                return OemVehicleStateTransport.lambda$readVehicleStates$0(linkedHashMap, session);
            }
        });
    }

    static /* synthetic */ Map lambda$readVehicleStates$0(LinkedHashMap<StateKey, Integer> linkedHashMap, Session session) {
        LinkedHashMap linkedHashMap2 = new LinkedHashMap();
        for (StateKey stateKey : linkedHashMap.keySet()) {
            Integer vehicleState = session.readVehicleState(stateKey);
            if (vehicleState == null) {
                return null;
            }
            linkedHashMap2.put(stateKey, vehicleState);
        }
        return Collections.unmodifiableMap(linkedHashMap2);
    }

    static Result sendBundle(Context context, Map<String, Integer> map, Map<String, Integer> map2, String str) {
        return sendBundle(context, keyedValues(map, map2), str);
    }

    static Result sendVehicleStateThenBundle(Context context, StateValue stateValue, Map<StateKey, Integer> map, String str) {
        return INSTANCE.sendSequenceInternal(context, (StateValue) Objects.requireNonNull(stateValue, "first"), immutableCopy(map), str);
    }

    static Result sendVehicleStateThenBundle(Context context, String str, int i, int i2, Map<String, Integer> map, Map<String, Integer> map2, String str2) {
        return sendVehicleStateThenBundle(context, new StateValue(new StateKey(str, i), i2), keyedValues(map, map2), str2);
    }

    static Result sendRestoreSequence(Context context, StateValue stateValue, Map<String, Integer> map, Map<String, Integer> map2, Map<String, Integer> map3, String str) {
        return INSTANCE.sendRestoreSequenceInternal(context, stateValue, keyedValuesOptional(map, map3), keyedValuesOptional(map2, map3), str);
    }

    private static LinkedHashMap<StateKey, Integer> keyedValues(Map<String, Integer> map, Map<String, Integer> map2) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException("VehicleState bundle is empty");
        }
        if (map2 == null) {
            throw new IllegalArgumentException("VehicleState stable-id map is null");
        }
        LinkedHashMap<StateKey, Integer> linkedHashMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            Integer num = map2.get(entry.getKey());
            if (num == null) {
                throw new IllegalArgumentException("No stable id for VehicleState " + entry.getKey());
            }
            linkedHashMap.put(new StateKey(entry.getKey(), num.intValue()), entry.getValue());
        }
        return linkedHashMap;
    }

    private static LinkedHashMap<StateKey, Integer> keyedValuesOptional(Map<String, Integer> map, Map<String, Integer> map2) {
        if (map == null || map.isEmpty()) {
            return new LinkedHashMap<>();
        }
        return keyedValues(map, map2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static LinkedHashMap<StateKey, Integer> immutableCopy(Map<StateKey, Integer> map) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException("VehicleState bundle is empty");
        }
        LinkedHashMap<StateKey, Integer> linkedHashMap = new LinkedHashMap<>();
        for (Map.Entry<StateKey, Integer> entry : map.entrySet()) {
            linkedHashMap.put((StateKey) Objects.requireNonNull(entry.getKey(), "VehicleState key"), (Integer) Objects.requireNonNull(entry.getValue(), "VehicleState value"));
        }
        return linkedHashMap;
    }

    private Result sendSingleInternal(Context context, StateValue stateValue, String str) {
        Result resultTransactSingle;
        Context contextApplicationContext = applicationContext(context);
        if (contextApplicationContext == null || !resolveStates(contextApplicationContext, Collections.singleton(stateValue.key))) {
            return Result.TRANSIENT_FAILURE;
        }
        IBinder iBinderAcquireBinder = acquireBinder(contextApplicationContext);
        if (iBinderAcquireBinder == null) {
            return Result.TRANSIENT_FAILURE;
        }
        synchronized (this.transactionLock) {
            resultTransactSingle = transactSingle(iBinderAcquireBinder, stateValue, str);
        }
        return resultTransactSingle;
    }

    private <T> T withSessionInternal(Context context, Collection<StateKey> collection, SessionOperation<T> sessionOperation) {
        IBinder iBinderAcquireBinder;
        Context contextApplicationContext = applicationContext(context);
        if (contextApplicationContext == null || !resolveStates(contextApplicationContext, collection) || (iBinderAcquireBinder = acquireBinder(contextApplicationContext)) == null) {
            return null;
        }
        synchronized (this.transactionLock) {
            if (isCurrentBinder(iBinderAcquireBinder)) {
                return sessionOperation.run(new BoundSession(iBinderAcquireBinder));
            }
            return null;
        }
    }

    private final class BoundSession implements Session {
        private final IBinder binder;

        BoundSession(IBinder iBinder) {
            this.binder = iBinder;
        }

        @Override // ru.big.town.anative.OemVehicleStateTransport.Session
        public GearStatus readGearStatus() {
            return OemVehicleStateTransport.this.transactGearStatus(this.binder);
        }

        @Override // ru.big.town.anative.OemVehicleStateTransport.Session
        public FuelLevel readFuelLevel() {
            return OemVehicleStateTransport.this.transactFuelLevel(this.binder);
        }

        @Override // ru.big.town.anative.OemVehicleStateTransport.Session
        public Integer readVehicleSpeed() {
            if (!OemVehicleStateTransport.this.isCurrentBinder(this.binder)) {
                return null;
            }
            Parcel parcelObtain = Parcel.obtain();
            Parcel parcelObtain2 = Parcel.obtain();
            try {
                try {
                    parcelObtain.writeInterfaceToken(OemVehicleStateTransport.CANBUS_DESCRIPTOR);
                    if (this.binder.transact(26, parcelObtain, parcelObtain2, 0)) {
                        parcelObtain2.readException();
                        return Integer.valueOf(parcelObtain2.readInt());
                    }
                } catch (RemoteException | RuntimeException e) {
                    Log.w(OemVehicleStateTransport.TAG, "getVehicleSpeed failed", e);
                }
                return null;
            } finally {
                parcelObtain2.recycle();
                parcelObtain.recycle();
            }
        }

        @Override // ru.big.town.anative.OemVehicleStateTransport.Session
        public Integer readVehicleState(StateKey stateKey) {
            Objects.requireNonNull(stateKey, "VehicleState key");
            return OemVehicleStateTransport.this.transactVehicleState(this.binder, stateKey);
        }

        @Override // ru.big.town.anative.OemVehicleStateTransport.Session
        public Result sendVehicleState(StateValue stateValue, String str) {
            Objects.requireNonNull(stateValue, "VehicleState value");
            return OemVehicleStateTransport.this.transactSingle(this.binder, stateValue, str);
        }

        @Override // ru.big.town.anative.OemVehicleStateTransport.Session
        public Result sendBundle(Map<StateKey, Integer> map, String str) {
            return OemVehicleStateTransport.this.transactBundle(this.binder, OemVehicleStateTransport.immutableCopy(map), str);
        }
    }

    private Result sendBundleInternal(Context context, LinkedHashMap<StateKey, Integer> linkedHashMap, String str) {
        Result resultTransactBundle;
        Context contextApplicationContext = applicationContext(context);
        if (contextApplicationContext == null || !resolveStates(contextApplicationContext, linkedHashMap.keySet())) {
            return Result.TRANSIENT_FAILURE;
        }
        IBinder iBinderAcquireBinder = acquireBinder(contextApplicationContext);
        if (iBinderAcquireBinder == null) {
            return Result.TRANSIENT_FAILURE;
        }
        synchronized (this.transactionLock) {
            resultTransactBundle = transactBundle(iBinderAcquireBinder, linkedHashMap, str);
        }
        return resultTransactBundle;
    }

    private Result sendSequenceInternal(Context context, StateValue stateValue, LinkedHashMap<StateKey, Integer> linkedHashMap, String str) {
        return sendRestoreSequenceInternal(context, stateValue, linkedHashMap, new LinkedHashMap<>(), str);
    }

    private Result sendRestoreSequenceInternal(Context context, StateValue stateValue, LinkedHashMap<StateKey, Integer> linkedHashMap, LinkedHashMap<StateKey, Integer> linkedHashMap2, String str) {
        Result resultTransactRestoreSequence;
        Context contextApplicationContext = applicationContext(context);
        if (contextApplicationContext == null) {
            return Result.TRANSIENT_FAILURE;
        }
        LinkedHashMap linkedHashMap3 = new LinkedHashMap(linkedHashMap);
        linkedHashMap3.putAll(linkedHashMap2);
        if (stateValue != null) {
            linkedHashMap3.put(stateValue.key, Integer.valueOf(stateValue.value));
        }
        if (linkedHashMap3.isEmpty()) {
            throw new IllegalArgumentException("VehicleState restore sequence is empty");
        }
        if (!resolveStates(contextApplicationContext, linkedHashMap3.keySet())) {
            return Result.TRANSIENT_FAILURE;
        }
        IBinder iBinderAcquireBinder = acquireBinder(contextApplicationContext);
        if (iBinderAcquireBinder == null) {
            return Result.TRANSIENT_FAILURE;
        }
        synchronized (this.transactionLock) {
            resultTransactRestoreSequence = transactRestoreSequence(iBinderAcquireBinder, stateValue, linkedHashMap, linkedHashMap2, str);
        }
        return resultTransactRestoreSequence;
    }

    private Result transactRestoreSequence(IBinder iBinder, StateValue stateValue, Map<StateKey, Integer> map, Map<StateKey, Integer> map2, String str) {
        if (stateValue != null) {
            Result resultTransactSingle = transactSingle(iBinder, stateValue, str + " first");
            if (!resultTransactSingle.accepted()) {
                return resultTransactSingle;
            }
        }
        if (!map.isEmpty()) {
            Result resultTransactBundle = transactBundle(iBinder, map, str + " primary");
            if (!resultTransactBundle.accepted()) {
                return resultTransactBundle;
            }
        }
        if (!map2.isEmpty()) {
            return transactBundle(iBinder, map2, str + " trailing");
        }
        return Result.ACCEPTED_UNCONFIRMED;
    }

    private Context applicationContext(Context context) {
        Context context2;
        if (context == null) {
            return null;
        }
        Context applicationContext = context.getApplicationContext();
        synchronized (this.connectionLock) {
            if (this.appContext == null) {
                this.appContext = applicationContext;
            }
            context2 = this.appContext;
        }
        return context2;
    }

    private boolean resolveStates(Context context, Collection<StateKey> collection) {
        synchronized (this.schemaLock) {
            try {
                try {
                    try {
                        if (this.vehicleStateClass == null) {
                            Class cls = Class.forName("com.qinggan.canbus.VehicleState", true, new PathClassLoader(context.getPackageManager().getApplicationInfo(CANBUS_PACKAGE, 0).sourceDir, context.getClassLoader()));
                            if (!cls.isEnum()) {
                                Log.e(TAG, "VehicleState is not an enum");
                                return false;
                            }
                            this.vehicleStateClass = cls;
                            this.vehicleStateGetValue = cls.getMethod("getValue", new Class[0]);
                        }
                        for (StateKey stateKey : collection) {
                            if (!this.resolvedOrdinals.containsKey(stateKey)) {
                                Enum enumValueOf = Enum.valueOf((Class) this.vehicleStateClass, stateKey.name);
                                Object objInvoke = this.vehicleStateGetValue.invoke(enumValueOf, new Object[0]);
                                if (!(objInvoke instanceof Integer) || ((Integer) objInvoke).intValue() != stateKey.stableId) {
                                    Log.e(TAG, "VehicleState id mismatch for " + stateKey + ", installed=" + objInvoke);
                                    return false;
                                }
                                this.resolvedOrdinals.put(stateKey, Integer.valueOf(enumValueOf.ordinal()));
                            }
                        }
                        return true;
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.e(TAG, "CanBusService APK not found", e);
                        return false;
                    }
                } catch (ReflectiveOperationException | RuntimeException e2) {
                    Log.e(TAG, "Cannot resolve installed VehicleState schema", e2);
                    return false;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private IBinder acquireBinder(Context context) {
        IBinder iBinderLiveBinderLocked;
        DemandConnection demandConnection = null;
        if (context.checkSelfPermission(WRITE_CANBUS_PERMISSION) != 0) {
            Log.e(TAG, "WRITE_CANBUS permission missing");
            return null;
        }
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        synchronized (this.connectionLock) {
            IBinder iBinderLiveBinderLocked2 = liveBinderLocked();
            if (iBinderLiveBinderLocked2 != null) {
                return iBinderLiveBinderLocked2;
            }
            DemandConnection demandConnection2 = this.activeConnection;
            if (demandConnection2 != null && !this.bindingInProgress) {
                this.bindingInProgress = true;
                this.bindingStartedElapsed = jElapsedRealtime;
            }
            boolean z = false;
            if (demandConnection2 == null || !this.bindingInProgress || jElapsedRealtime - this.bindingStartedElapsed < STALE_BIND_MS) {
                demandConnection2 = null;
            } else {
                boolean z2 = this.connectionRegistered;
                this.activeConnection = null;
                this.connectionRegistered = false;
                this.bindingInProgress = false;
                z = z2;
            }
            if (this.activeConnection == null) {
                long j = this.bindingGeneration + 1;
                this.bindingGeneration = j;
                demandConnection = new DemandConnection(j);
                this.activeConnection = demandConnection;
                this.bindingInProgress = true;
                this.bindingStartedElapsed = jElapsedRealtime;
            }
            if (z && demandConnection2 != null) {
                try {
                    context.unbindService(demandConnection2);
                } catch (RuntimeException e) {
                    Log.w(TAG, "Stale CanBus unbind failed", e);
                }
            }
            if (demandConnection != null) {
                bindOnce(context, demandConnection);
            }
            if (Looper.myLooper() == Looper.getMainLooper()) {
                synchronized (this.connectionLock) {
                    iBinderLiveBinderLocked = liveBinderLocked();
                }
                return iBinderLiveBinderLocked;
            }
            long jElapsedRealtime2 = SystemClock.elapsedRealtime() + BIND_WAIT_MS;
            synchronized (this.connectionLock) {
                while (liveBinderLocked() == null && this.bindingInProgress) {
                    long jElapsedRealtime3 = jElapsedRealtime2 - SystemClock.elapsedRealtime();
                    if (jElapsedRealtime3 <= 0) {
                        break;
                    }
                    try {
                        this.connectionLock.wait(jElapsedRealtime3);
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                        return liveBinderLocked();
                    }
                }
            }
            return liveBinderLocked();
        }
    }

    /* JADX WARN: Code duplicated, block: B:43:0x0083 A[Catch: all -> 0x0094, TryCatch #1 {, blocks: (B:41:0x007f, B:43:0x0083, B:45:0x0087, B:46:0x008d, B:47:0x0092), top: B:52:0x007f }] */
    /* JADX WARN: Code duplicated, block: B:45:0x0087 A[Catch: all -> 0x0094, TryCatch #1 {, blocks: (B:41:0x007f, B:43:0x0083, B:45:0x0087, B:46:0x008d, B:47:0x0092), top: B:52:0x007f }] */
    /* JADX WARN: Code duplicated, block: B:52:0x007f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    private void bindOnce(Context context, DemandConnection demandConnection) {
        boolean zBindService = false;
        try {
            Intent intent = new Intent(CANBUS_ACTION);
            intent.setPackage(CANBUS_PACKAGE);
            zBindService = context.bindService(intent, 1, context.getMainExecutor(), demandConnection);
            Log.i(TAG, "demand bindService gen=" + demandConnection.generation + " returned " + zBindService);
        } catch (RuntimeException e) {
            Log.e(TAG, "Demand CanBus bind failed", e);
        } finally {
            synchronized (this.connectionLock) {
                if (this.activeConnection == demandConnection) {
                    this.connectionRegistered = zBindService;
                    if (!zBindService) {
                        this.canBusBinder = null;
                        this.activeConnection = null;
                        this.bindingInProgress = false;
                    }
                }
                this.connectionLock.notifyAll();
            }
        }
    }

    private IBinder liveBinderLocked() {
        IBinder iBinder = this.canBusBinder;
        if (iBinder != null && iBinder.isBinderAlive()) {
            return this.canBusBinder;
        }
        this.canBusBinder = null;
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v11 */
    /* JADX WARN: Type inference failed for: r7v12 */
    /* JADX WARN: Type inference failed for: r7v13 */
    /* JADX WARN: Type inference failed for: r7v9 */
    public Result transactSingle(IBinder iBinder, StateValue stateValue, String str) {
        Result result;
        Integer num;
        synchronized (this.schemaLock) {
            num = this.resolvedOrdinals.get(stateValue.key);
        }
        if (num == null || !isCurrentBinder(iBinder)) {
            return Result.TRANSIENT_FAILURE;
        }
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken(CANBUS_DESCRIPTOR);
                parcelObtain.writeInt(1);
                parcelObtain.writeInt(num.intValue());
                parcelObtain.writeInt(stateValue.key.stableId);
                parcelObtain.writeInt(stateValue.value);
                if (!CanSender.beginFrameAttemptForCurrentGuard()) {
                    result = Result.TRANSIENT_FAILURE;
                } else if (!iBinder.transact(58, parcelObtain, parcelObtain2, 0)) {
                    Log.e(TAG, "TX58 rejected [" + safeLabel(str) + "] " + stateValue.key);
                    result = Result.TRANSIENT_FAILURE;
                } else {
                    parcelObtain2.readException();
                    Log.i(TAG, "TX58 accepted-unconfirmed [" + safeLabel(str) + "] " + stateValue.key + "=" + stateValue.value);
                    result = Result.ACCEPTED_UNCONFIRMED;
                }
            } catch (RemoteException | RuntimeException e) {
                Log.e(TAG, "TX58 failed [" + safeLabel(str) + "] " + stateValue.key, e);
                this.dropBinding(null, iBinder);
                result = Result.TRANSIENT_FAILURE;
            }
            return result;
        } finally {
            parcelObtain2.recycle();
            parcelObtain.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public GearStatus transactGearStatus(IBinder iBinder) {
        if (!isCurrentBinder(iBinder)) {
            return null;
        }
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken(CANBUS_DESCRIPTOR);
                if (iBinder.transact(6, parcelObtain, parcelObtain2, 0)) {
                    parcelObtain2.readException();
                    if (parcelObtain2.readInt() != 0) {
                        int i = parcelObtain2.readInt();
                        int i2 = parcelObtain2.readInt();
                        Log.i(TAG, "TX6 getGearStatus ordinal=" + i + " value=" + i2);
                        return new GearStatus(i, i2);
                    }
                    Log.e(TAG, "TX6 getGearStatus returned null");
                } else {
                    Log.e(TAG, "TX6 getGearStatus rejected");
                }
            } catch (RemoteException | RuntimeException e) {
                Log.e(TAG, "TX6 getGearStatus failed", e);
                dropBinding(null, iBinder);
            }
            return null;
        } finally {
            parcelObtain2.recycle();
            parcelObtain.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public FuelLevel transactFuelLevel(IBinder iBinder) {
        if (!isCurrentBinder(iBinder)) {
            return null;
        }
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken(CANBUS_DESCRIPTOR);
                if (iBinder.transact(9, parcelObtain, parcelObtain2, 0)) {
                    parcelObtain2.readException();
                    if (parcelObtain2.readInt() != 0 && parcelObtain2.dataAvail() >= 28) {
                        int i = parcelObtain2.readInt();
                        parcelObtain2.readInt();
                        return new FuelLevel(i, parcelObtain2.readFloat());
                    }
                }
            } catch (RemoteException | RuntimeException e) {
                Log.w(TAG, "TX9 getFuelLevel unavailable", e);
            }
            return null;
        } finally {
            parcelObtain2.recycle();
            parcelObtain.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Integer transactVehicleState(IBinder iBinder, StateKey stateKey) {
        Integer num;
        synchronized (this.schemaLock) {
            num = this.resolvedOrdinals.get(stateKey);
        }
        if (num == null || !isCurrentBinder(iBinder)) {
            return null;
        }
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken(CANBUS_DESCRIPTOR);
                parcelObtain.writeInt(1);
                parcelObtain.writeInt(num.intValue());
                parcelObtain.writeInt(stateKey.stableId);
                if (!iBinder.transact(57, parcelObtain, parcelObtain2, 0)) {
                    Log.e(TAG, "TX57 getVehicleState rejected " + stateKey);
                    return null;
                }
                parcelObtain2.readException();
                int i = parcelObtain2.readInt();
                Log.i(TAG, "TX57 getVehicleState " + stateKey + "=" + i);
                return Integer.valueOf(i);
            } catch (RemoteException | RuntimeException e) {
                Log.e(TAG, "TX57 getVehicleState failed " + stateKey, e);
                dropBinding(null, iBinder);
            }
            return null;
        } finally {
            parcelObtain2.recycle();
            parcelObtain.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v11 */
    /* JADX WARN: Type inference failed for: r6v12 */
    /* JADX WARN: Type inference failed for: r6v13 */
    /* JADX WARN: Type inference failed for: r6v14 */
    /* JADX WARN: Type inference failed for: r6v8 */
    public Result transactBundle(IBinder iBinder, Map<StateKey, Integer> map, String str) {
        Result result;
        if (!isCurrentBinder(iBinder)) {
            return Result.TRANSIENT_FAILURE;
        }
        Bundle bundle = new Bundle();
        for (Map.Entry<StateKey, Integer> entry : map.entrySet()) {
            bundle.putInt(entry.getKey().name, entry.getValue().intValue());
        }
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            try {
                parcelObtain.writeInterfaceToken(CANBUS_DESCRIPTOR);
                parcelObtain.writeInt(0);
                parcelObtain.writeInt(1);
                bundle.writeToParcel(parcelObtain, 0);
                if (!CanSender.beginFrameAttemptForCurrentGuard()) {
                    result = Result.TRANSIENT_FAILURE;
                } else if (!iBinder.transact(TX_SET_VEHICLE_AND_AIR_BUNDLE_STATE, parcelObtain, parcelObtain2, 0)) {
                    Log.e(TAG, "TX77 rejected [" + safeLabel(str) + "]");
                    result = Result.TRANSIENT_FAILURE;
                } else {
                    parcelObtain2.readException();
                    int i = parcelObtain2.readInt();
                    if (i != 0) {
                        Log.e(TAG, "TX77 returned " + i + " [" + safeLabel(str) + "]");
                        result = Result.TRANSIENT_FAILURE;
                    } else {
                        Log.i(TAG, "TX77 accepted-unconfirmed [" + safeLabel(str) + "] states=" + bundle);
                        result = Result.ACCEPTED_UNCONFIRMED;
                    }
                }
            } catch (RemoteException | RuntimeException e) {
                Log.e(TAG, "TX77 failed [" + safeLabel(str) + "]", e);
                this.dropBinding(null, iBinder);
                result = Result.TRANSIENT_FAILURE;
            }
            return result;
        } finally {
            parcelObtain2.recycle();
            parcelObtain.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:10:0x000f  */
    public boolean isCurrentBinder(IBinder iBinder) {
        boolean z;
        synchronized (this.connectionLock) {
            if (iBinder != null) {
                try {
                    if (iBinder == liveBinderLocked()) {
                        z = true;
                    } else {
                        z = false;
                    }
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                z = false;
            }
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dropBinding(DemandConnection demandConnection, IBinder iBinder) {
        IBinder iBinder2;
        synchronized (this.connectionLock) {
            if (demandConnection != null) {
                try {
                    if (this.activeConnection != demandConnection) {
                        return;
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            if (iBinder == null || (iBinder2 = this.canBusBinder) == null || iBinder2 == iBinder) {
                this.canBusBinder = null;
                this.bindingInProgress = false;
                Context context = this.appContext;
                boolean z = this.connectionRegistered || demandConnection != null;
                DemandConnection demandConnection2 = this.activeConnection;
                this.connectionRegistered = false;
                this.activeConnection = null;
                this.connectionLock.notifyAll();
                if (!z || context == null || demandConnection2 == null) {
                    return;
                }
                try {
                    context.unbindService(demandConnection2);
                } catch (RuntimeException e) {
                    Log.w(TAG, "CanBus unbind failed", e);
                }
            }
        }
    }

    private static String safeLabel(String str) {
        return (str == null || str.isEmpty()) ? "?" : str;
    }
}
