package android.car;

import android.annotation.SystemApi;
import android.app.Activity;
import android.app.Service;
import android.car.cluster.CarInstrumentClusterManager;
import android.car.content.pm.CarPackageManager;
import android.car.diagnostic.CarDiagnosticManager;
import android.car.drivingstate.CarDrivingStateManager;
import android.car.drivingstate.CarUxRestrictionsManager;
import android.car.hardware.CarSensorManager;
import android.car.hardware.CarVendorExtensionManager;
import android.car.hardware.cabin.CarCabinManager;
import android.car.hardware.hvac.CarHvacManager;
import android.car.hardware.power.CarPowerManager;
import android.car.hardware.property.CarPropertyManager;
import android.car.hardware.property.ICarProperty;
import android.car.input.CarInputManager;
import android.car.media.CarAudioManager;
import android.car.media.CarMediaManager;
import android.car.navigation.CarNavigationStatusManager;
import android.car.occupantawareness.OccupantAwarenessManager;
import android.car.settings.CarConfigurationManager;
import android.car.storagemonitoring.CarStorageMonitoringManager;
import android.car.test.CarTestManagerBinderWrapper;
import android.car.trust.CarTrustAgentEnrollmentManager;
import android.car.user.CarUserManager;
import android.car.vms.VmsClientManager;
import android.car.vms.VmsSubscriberManager;
import android.car.watchdog.CarWatchdogManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.TransactionTooLargeException;
import android.os.UserHandle;
import android.util.Log;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class Car {
    public static final String APP_FOCUS_SERVICE = "app_focus";
    public static final String AUDIO_SERVICE = "audio";
    public static final String BLUETOOTH_SERVICE = "car_bluetooth";

    @SystemApi
    @Deprecated
    public static final String CABIN_SERVICE = "cabin";
    public static final String CAR_BUGREPORT_SERVICE = "car_bugreport";
    public static final String CAR_CATEGORY_NAVIGATION = "android.car.cluster.NAVIGATION";
    public static final String CAR_CONFIGURATION_SERVICE = "configuration";

    @SystemApi
    public static final String CAR_DRIVING_STATE_SERVICE = "drivingstate";
    public static final String CAR_EXTRA_BROWSE_SERVICE_FOR_SESSION = "android.media.session.BROWSE_SERVICE";

    @SystemApi
    public static final String CAR_EXTRA_CLUSTER_ACTIVITY_STATE = "android.car.cluster.ClusterActivityState";
    public static final String CAR_EXTRA_MEDIA_COMPONENT = "android.car.intent.extra.MEDIA_COMPONENT";

    @Deprecated
    public static final String CAR_EXTRA_MEDIA_PACKAGE = "android.car.intent.extra.MEDIA_PACKAGE";
    public static final String CAR_INPUT_SERVICE = "android.car.input";

    @Deprecated
    public static final String CAR_INSTRUMENT_CLUSTER_SERVICE = "cluster_service";
    public static final String CAR_INTENT_ACTION_MEDIA_TEMPLATE = "android.car.intent.action.MEDIA_TEMPLATE";

    @SystemApi
    public static final String CAR_MEDIA_SERVICE = "car_media";
    public static final String CAR_NAVIGATION_SERVICE = "car_navigation_service";
    public static final String CAR_OCCUPANT_ZONE_SERVICE = "car_occupant_zone_service";
    private static final long CAR_SERVICE_BINDER_POLLING_INTERVAL_MS = 50;
    private static final long CAR_SERVICE_BINDER_POLLING_MAX_RETRY = 100;
    public static final String CAR_SERVICE_BINDER_SERVICE_NAME = "car_service";
    private static final long CAR_SERVICE_BIND_MAX_RETRY = 20;
    private static final long CAR_SERVICE_BIND_RETRY_INTERVAL_MS = 500;
    private static final String CAR_SERVICE_CLASS = "com.android.car.CarService";
    public static final String CAR_SERVICE_INTERFACE_NAME = "android.car.ICar";
    private static final String CAR_SERVICE_PACKAGE = "com.android.car";

    @SystemApi
    public static final String CAR_TRUST_AGENT_ENROLLMENT_SERVICE = "trust_enroll";

    @SystemApi
    public static final String CAR_USER_SERVICE = "car_user_service";
    public static final String CAR_UX_RESTRICTION_SERVICE = "uxrestriction";
    public static final long CAR_WAIT_TIMEOUT_DO_NOT_WAIT = 0;
    public static final long CAR_WAIT_TIMEOUT_WAIT_FOREVER = -1;

    @SystemApi
    public static final String CAR_WATCHDOG_SERVICE = "car_watchdog";
    public static final int CONNECTION_TYPE_EMBEDDED = 5;
    private static final boolean DBG = false;

    @SystemApi
    public static final String DIAGNOSTIC_SERVICE = "diagnostic";

    @SystemApi
    public static final int FEATURE_REQUEST_ALREADY_IN_THE_STATE = 1;

    @SystemApi
    public static final int FEATURE_REQUEST_MANDATORY = 2;

    @SystemApi
    public static final int FEATURE_REQUEST_NOT_EXISTING = 3;

    @SystemApi
    public static final int FEATURE_REQUEST_SUCCESS = 0;

    @SystemApi
    @Deprecated
    public static final String HVAC_SERVICE = "hvac";
    public static final String INFO_SERVICE = "info";
    public static final String META_DATA_DISTRACTION_OPTIMIZED = "distractionOptimized";
    public static final String META_DATA_REQUIRES_CAR_FEATURE = "requires-car-feature";

    @SystemApi
    public static final String OCCUPANT_AWARENESS_SERVICE = "occupant_awareness";
    public static final String PACKAGE_SERVICE = "package";

    @SystemApi
    public static final String PERMISSION_ADJUST_RANGE_REMAINING = "android.car.permission.ADJUST_RANGE_REMAINING";
    public static final String PERMISSION_BIND_VMS_CLIENT = "android.car.permission.BIND_VMS_CLIENT";
    public static final String PERMISSION_CAR_CONTROL_AUDIO_SETTINGS = "android.car.permission.CAR_CONTROL_AUDIO_SETTINGS";
    public static final String PERMISSION_CAR_CONTROL_AUDIO_VOLUME = "android.car.permission.CAR_CONTROL_AUDIO_VOLUME";

    @SystemApi
    public static final String PERMISSION_CAR_DIAGNOSTIC_CLEAR = "android.car.permission.CLEAR_CAR_DIAGNOSTICS";

    @SystemApi
    public static final String PERMISSION_CAR_DIAGNOSTIC_READ_ALL = "android.car.permission.CAR_DIAGNOSTICS";
    public static final String PERMISSION_CAR_DISPLAY_IN_CLUSTER = "android.car.permission.CAR_DISPLAY_IN_CLUSTER";

    @SystemApi
    public static final String PERMISSION_CAR_DRIVING_STATE = "android.car.permission.CAR_DRIVING_STATE";

    @SystemApi
    public static final String PERMISSION_CAR_DYNAMICS_STATE = "android.car.permission.CAR_DYNAMICS_STATE";

    @SystemApi
    public static final String PERMISSION_CAR_ENGINE_DETAILED = "android.car.permission.CAR_ENGINE_DETAILED";

    @SystemApi
    public static final String PERMISSION_CAR_ENROLL_TRUST = "android.car.permission.CAR_ENROLL_TRUST";
    public static final String PERMISSION_CAR_INFO = "android.car.permission.CAR_INFO";

    @SystemApi
    public static final String PERMISSION_CAR_INSTRUMENT_CLUSTER_CONTROL = "android.car.permission.CAR_INSTRUMENT_CLUSTER_CONTROL";
    public static final String PERMISSION_CAR_NAVIGATION_MANAGER = "android.car.permission.CAR_NAVIGATION_MANAGER";

    @SystemApi
    public static final String PERMISSION_CAR_POWER = "android.car.permission.CAR_POWER";

    @SystemApi
    public static final String PERMISSION_CAR_PROJECTION = "android.car.permission.CAR_PROJECTION";

    @SystemApi
    public static final String PERMISSION_CAR_PROJECTION_STATUS = "android.car.permission.ACCESS_CAR_PROJECTION_STATUS";

    @SystemApi
    public static final String PERMISSION_CAR_TEST_SERVICE = "android.car.permission.CAR_TEST_SERVICE";
    public static final String PERMISSION_CAR_UX_RESTRICTIONS_CONFIGURATION = "android.car.permission.CAR_UX_RESTRICTIONS_CONFIGURATION";

    @SystemApi
    public static final String PERMISSION_CONTROL_APP_BLOCKING = "android.car.permission.CONTROL_APP_BLOCKING";

    @SystemApi
    public static final String PERMISSION_CONTROL_CAR_CLIMATE = "android.car.permission.CONTROL_CAR_CLIMATE";

    @SystemApi
    public static final String PERMISSION_CONTROL_CAR_DOORS = "android.car.permission.CONTROL_CAR_DOORS";

    @SystemApi
    public static final String PERMISSION_CONTROL_CAR_FEATURES = "android.car.permission.CONTROL_CAR_FEATURES";

    @SystemApi
    public static final String PERMISSION_CONTROL_CAR_MIRRORS = "android.car.permission.CONTROL_CAR_MIRRORS";

    @SystemApi
    public static final String PERMISSION_CONTROL_CAR_OCCUPANT_AWARENESS_SYSTEM = "android.car.permission.CONTROL_CAR_OCCUPANT_AWARENESS_SYSTEM";

    @SystemApi
    public static final String PERMISSION_CONTROL_CAR_SEATS = "android.car.permission.CONTROL_CAR_SEATS";

    @SystemApi
    public static final String PERMISSION_CONTROL_CAR_WINDOWS = "android.car.permission.CONTROL_CAR_WINDOWS";
    public static final String PERMISSION_CONTROL_DISPLAY_UNITS = "android.car.permission.CONTROL_CAR_DISPLAY_UNITS";

    @SystemApi
    public static final String PERMISSION_CONTROL_ENERGY_PORTS = "android.car.permission.CONTROL_CAR_ENERGY_PORTS";

    @SystemApi
    public static final String PERMISSION_CONTROL_EXTERIOR_LIGHTS = "android.car.permission.CONTROL_CAR_EXTERIOR_LIGHTS";
    public static final String PERMISSION_CONTROL_INTERIOR_LIGHTS = "android.car.permission.CONTROL_CAR_INTERIOR_LIGHTS";
    public static final String PERMISSION_ENERGY = "android.car.permission.CAR_ENERGY";
    public static final String PERMISSION_ENERGY_PORTS = "android.car.permission.CAR_ENERGY_PORTS";
    public static final String PERMISSION_EXTERIOR_ENVIRONMENT = "android.car.permission.CAR_EXTERIOR_ENVIRONMENT";

    @SystemApi
    public static final String PERMISSION_EXTERIOR_LIGHTS = "android.car.permission.CAR_EXTERIOR_LIGHTS";
    public static final String PERMISSION_IDENTIFICATION = "android.car.permission.CAR_IDENTIFICATION";

    @SystemApi
    public static final String PERMISSION_MILEAGE = "android.car.permission.CAR_MILEAGE";

    @SystemApi
    @Deprecated
    public static final String PERMISSION_MOCK_VEHICLE_HAL = "android.car.permission.CAR_MOCK_VEHICLE_HAL";
    public static final String PERMISSION_POWERTRAIN = "android.car.permission.CAR_POWERTRAIN";

    @SystemApi
    public static final String PERMISSION_READ_CAR_OCCUPANT_AWARENESS_STATE = "android.car.permission.READ_CAR_OCCUPANT_AWARENESS_STATE";

    @SystemApi
    public static final String PERMISSION_READ_CAR_VENDOR_PERMISSION_INFO = "android.car.permission.READ_CAR_VENDOR_PERMISSION_INFO";
    public static final String PERMISSION_READ_DISPLAY_UNITS = "android.car.permission.READ_CAR_DISPLAY_UNITS";
    public static final String PERMISSION_READ_INTERIOR_LIGHTS = "android.car.permission.READ_CAR_INTERIOR_LIGHTS";
    public static final String PERMISSION_READ_STEERING_STATE = "android.car.permission.READ_CAR_STEERING";

    @SystemApi
    public static final String PERMISSION_RECEIVE_CAR_AUDIO_DUCKING_EVENTS = "android.car.permission.RECEIVE_CAR_AUDIO_DUCKING_EVENTS";
    public static final String PERMISSION_SPEED = "android.car.permission.CAR_SPEED";

    @SystemApi
    public static final String PERMISSION_STORAGE_MONITORING = "android.car.permission.STORAGE_MONITORING";

    @SystemApi
    public static final String PERMISSION_TIRES = "android.car.permission.CAR_TIRES";

    @SystemApi
    public static final String PERMISSION_USE_CAR_WATCHDOG = "android.car.permission.USE_CAR_WATCHDOG";

    @SystemApi
    public static final String PERMISSION_VENDOR_EXTENSION = "android.car.permission.CAR_VENDOR_EXTENSION";

    @SystemApi
    public static final String PERMISSION_VMS_PUBLISHER = "android.car.permission.VMS_PUBLISHER";

    @SystemApi
    public static final String PERMISSION_VMS_SUBSCRIBER = "android.car.permission.VMS_SUBSCRIBER";

    @SystemApi
    public static final String POWER_SERVICE = "power";

    @SystemApi
    public static final String PROJECTION_SERVICE = "projection";
    public static final String PROPERTY_SERVICE = "property";

    @Deprecated
    public static final String SENSOR_SERVICE = "sensor";
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_DISCONNECTED = 0;

    @SystemApi
    public static final String STORAGE_MONITORING_SERVICE = "storage_monitoring";

    @SystemApi
    public static final String TEST_SERVICE = "car-service-test";

    @SystemApi
    public static final String VEHICLE_MAP_SERVICE = "vehicle_map_service";

    @SystemApi
    @Deprecated
    public static final String VENDOR_EXTENSION_SERVICE = "vendor_extension";

    @SystemApi
    @Deprecated
    public static final String VMS_SUBSCRIBER_SERVICE = "vehicle_map_subscriber_service";
    private int mConnectionRetryCount;
    private final Runnable mConnectionRetryFailedRunnable;
    private final Runnable mConnectionRetryRunnable;
    private int mConnectionState;
    private final Exception mConstructionStack;
    private final Context mContext;
    private final Handler mEventHandler;
    private final CarFeatures mFeatures;
    private final Object mLock;
    private final Handler mMainThreadEventHandler;
    private ICar mService;
    private boolean mServiceBound;
    private final ServiceConnection mServiceConnectionListener;
    private final ServiceConnection mServiceConnectionListenerClient;
    private final HashMap<String, CarManagerBase> mServiceMap;
    private final CarServiceLifecycleListener mStatusChangeCallback;

    public interface CarServiceLifecycleListener {
        void onLifecycleChanged(Car car, boolean z);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ConnectionType {
    }

    @Target({ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FeaturerRequestEnum {
    }

    @Target({ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface StateTypeEnum {
    }

    private Car(Context context, ICar iCar, ServiceConnection serviceConnection, CarServiceLifecycleListener carServiceLifecycleListener, Handler handler) {
        this.mLock = new Object();
        this.mConnectionRetryRunnable = new Runnable(this) { // from class: android.car.Car.1
            final Car this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.this$0.startCarService();
            }
        };
        this.mConnectionRetryFailedRunnable = new Runnable(this) { // from class: android.car.Car.2
            final Car this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.this$0.mServiceConnectionListener.onServiceDisconnected(new ComponentName(Car.CAR_SERVICE_PACKAGE, Car.CAR_SERVICE_CLASS));
            }
        };
        this.mServiceConnectionListener = new ServiceConnection(this) { // from class: android.car.Car.3
            final Car this$0;

            {
                this.this$0 = this;
            }

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                synchronized (this.this$0.mLock) {
                    ICar iCarAsInterface = ICar.Stub.asInterface(iBinder);
                    if (iCarAsInterface == null) {
                        Log.wtf(CarLibLog.TAG_CAR, "null binder service", new RuntimeException());
                    } else if (this.this$0.mService == null || !this.this$0.mService.asBinder().equals(iCarAsInterface.asBinder())) {
                        this.this$0.mConnectionState = 2;
                        this.this$0.mService = iCarAsInterface;
                        if (this.this$0.mStatusChangeCallback != null) {
                            this.this$0.mStatusChangeCallback.onLifecycleChanged(this.this$0, true);
                        } else if (this.this$0.mServiceConnectionListenerClient != null) {
                            this.this$0.mServiceConnectionListenerClient.onServiceConnected(componentName, iBinder);
                        }
                    }
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                this.this$0.mFeatures.resetCache();
                synchronized (this.this$0.mLock) {
                    if (this.this$0.mConnectionState == 0) {
                        return;
                    }
                    this.this$0.handleCarDisconnectLocked();
                    if (this.this$0.mStatusChangeCallback != null) {
                        this.this$0.mStatusChangeCallback.onLifecycleChanged(this.this$0, false);
                        return;
                    }
                    ServiceConnection serviceConnection2 = this.this$0.mServiceConnectionListenerClient;
                    Car car = this.this$0;
                    if (serviceConnection2 != null) {
                        car.mServiceConnectionListenerClient.onServiceDisconnected(componentName);
                    } else {
                        car.finishClient();
                    }
                }
            }
        };
        this.mServiceMap = new HashMap<>();
        this.mFeatures = new CarFeatures();
        this.mContext = context;
        Handler handlerDetermineEventHandler = determineEventHandler(handler);
        this.mEventHandler = handlerDetermineEventHandler;
        this.mMainThreadEventHandler = determineMainThreadEventHandler(handlerDetermineEventHandler);
        this.mService = iCar;
        this.mConnectionState = iCar != null ? 2 : 0;
        this.mServiceConnectionListenerClient = serviceConnection;
        this.mStatusChangeCallback = carServiceLifecycleListener;
        this.mConstructionStack = (serviceConnection == null && carServiceLifecycleListener == null) ? new RuntimeException() : null;
    }

    public Car(Context context, ICar iCar, Handler handler) {
        this(context, iCar, null, null, handler);
    }

    private static void assertNonNullContext(Context context) {
        Objects.requireNonNull(context);
        if ((context instanceof ContextWrapper) && ((ContextWrapper) context).getBaseContext() == null) {
            throw new NullPointerException("ContextWrapper with null base passed as Context, forgot to set base Context?");
        }
    }

    private CarManagerBase constructCarManager(String str, IBinder iBinder) {
        try {
            return (CarManagerBase) this.mContext.getClassLoader().loadClass(str).getConstructor(Car.class, IBinder.class).newInstance(this, iBinder);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            Log.e(CarLibLog.TAG_CAR, "Cannot construct CarManager, class:" + str, e);
            return null;
        }
    }

    public static Car createCar(Context context) {
        return createCar(context, (Handler) null);
    }

    @Deprecated
    public static Car createCar(Context context, ServiceConnection serviceConnection) {
        return createCar(context, serviceConnection, null);
    }

    @Deprecated
    public static Car createCar(Context context, ServiceConnection serviceConnection, Handler handler) {
        assertNonNullContext(context);
        if (context.getPackageManager().hasSystemFeature("android.hardware.type.automotive")) {
            try {
                return new Car(context, null, serviceConnection, null, handler);
            } catch (IllegalArgumentException unused) {
                return null;
            }
        }
        Log.e(CarLibLog.TAG_CAR, "FEATURE_AUTOMOTIVE not declared while android.car is used");
        return null;
    }

    public static Car createCar(Context context, Handler handler) {
        Context context2;
        Handler handler2;
        String str;
        String str2;
        RuntimeException runtimeException;
        assertNonNullContext(context);
        boolean z = false;
        Car car = null;
        int i = 0;
        while (true) {
            IBinder service = ServiceManager.getService(CAR_SERVICE_BINDER_SERVICE_NAME);
            if (car == null) {
                context2 = context;
                handler2 = handler;
                car = new Car(context2, ICar.Stub.asInterface(service), null, null, handler2);
            } else {
                context2 = context;
                handler2 = handler;
            }
            if (service != null) {
                if (!z) {
                    car.startCarService();
                    return car;
                }
                synchronized (car) {
                    if (car.mService == null) {
                        car.mService = ICar.Stub.asInterface(service);
                        Log.w(CarLibLog.TAG_CAR, "waited for car_service (ms):" + (((long) i) * CAR_SERVICE_BINDER_POLLING_INTERVAL_MS), new RuntimeException());
                    }
                    car.mConnectionState = 2;
                }
                return car;
            }
            if (!z) {
                car.startCarService();
                z = true;
            }
            i++;
            if (i > CAR_SERVICE_BINDER_POLLING_MAX_RETRY) {
                str = CarLibLog.TAG_CAR;
                str2 = "cannot get car_service, waited for car service (ms):5000";
                runtimeException = new RuntimeException();
            } else {
                try {
                    Thread.sleep(CAR_SERVICE_BINDER_POLLING_INTERVAL_MS);
                    context = context2;
                    handler = handler2;
                } catch (InterruptedException unused) {
                    str = CarLibLog.TAG_CAR;
                    str2 = "interrupted while waiting for car_service";
                    runtimeException = new RuntimeException();
                }
            }
            Log.e(str, str2, runtimeException);
            return null;
        }
    }

    /* JADX WARN: Code duplicated, block: B:41:0x00b9  */
    /* JADX WARN: Code duplicated, block: B:45:0x00c2  */
    /* JADX WARN: Instruction removed from duplicated block: B:45:0x00c2, please report this as an issue */
    public static Car createCar(Context context, Handler handler, long j, CarServiceLifecycleListener carServiceLifecycleListener) {
        long j2;
        int i;
        boolean z;
        assertNonNullContext(context);
        Objects.requireNonNull(carServiceLifecycleListener);
        long j3 = 0;
        if (j > 0) {
            j2 = j / CAR_SERVICE_BINDER_POLLING_INTERVAL_MS;
            if (j2 == 0) {
                j2 = 1;
            }
        } else {
            j2 = 0;
        }
        Car car = null;
        boolean z2 = false;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            z = true;
            i = 0;
        } else {
            i = 0;
            z = false;
        }
        while (true) {
            IBinder service = ServiceManager.getService(CAR_SERVICE_BINDER_SERVICE_NAME);
            if (car == null) {
                car = new Car(context, ICar.Stub.asInterface(service), null, carServiceLifecycleListener, handler);
            }
            if (service != null) {
                if (!z2) {
                    car.dispatchCarReadyToMainThread(z);
                    car.startCarService();
                    return car;
                }
                synchronized (car.mLock) {
                    Log.w(CarLibLog.TAG_CAR, "waited for car_service (ms):" + (((long) i) * CAR_SERVICE_BINDER_POLLING_INTERVAL_MS), new RuntimeException());
                    if (car.mService != null) {
                        return car;
                    }
                    car.mService = ICar.Stub.asInterface(service);
                    car.mConnectionState = 2;
                    car.dispatchCarReadyToMainThread(z);
                    return car;
                }
            }
            if (!z2) {
                car.startCarService();
                z2 = true;
            }
            i++;
            if (j < 0) {
                long j4 = i;
                if (j4 >= CAR_SERVICE_BINDER_POLLING_MAX_RETRY && j4 % CAR_SERVICE_BINDER_POLLING_MAX_RETRY == j3) {
                    Log.w(CarLibLog.TAG_CAR, "car_service not ready, waited for car service (ms):" + (j4 * CAR_SERVICE_BINDER_POLLING_INTERVAL_MS), new RuntimeException());
                } else if (j >= 0 && i > j2) {
                    if (j > 0) {
                        Log.w(CarLibLog.TAG_CAR, "car_service not ready, waited for car service (ms):" + j, new RuntimeException());
                    }
                    return car;
                }
            } else if (j >= 0) {
                if (j > 0) {
                    Log.w(CarLibLog.TAG_CAR, "car_service not ready, waited for car service (ms):" + j, new RuntimeException());
                }
                return car;
            }
            try {
                Thread.sleep(CAR_SERVICE_BINDER_POLLING_INTERVAL_MS);
                j3 = 0;
            } catch (InterruptedException unused) {
                Thread.currentThread().interrupt();
                Log.w(CarLibLog.TAG_CAR, "interrupted", new RuntimeException());
                return car;
            }
        }
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    private CarManagerBase createCarManagerLocked(String str, IBinder iBinder) {
        switch (str.hashCode()) {
            case -1969960369:
                if (str.equals(PROJECTION_SERVICE)) {
                    return new CarProjectionManager(this, iBinder);
                }
                break;
            case -1855028221:
                if (str.equals(BLUETOOTH_SERVICE)) {
                    return new CarBluetoothManager(this, iBinder);
                }
                break;
            case -1853877803:
                if (str.equals(CAR_NAVIGATION_SERVICE)) {
                    return new CarNavigationStatusManager(this, iBinder);
                }
                break;
            case -1566007885:
                if (str.equals(OCCUPANT_AWARENESS_SERVICE)) {
                    return new OccupantAwarenessManager(this, iBinder);
                }
                break;
            case -1547904089:
                if (str.equals(DIAGNOSTIC_SERVICE)) {
                    return new CarDiagnosticManager(this, iBinder);
                }
                break;
            case -993141291:
                if (str.equals(PROPERTY_SERVICE)) {
                    return new CarPropertyManager(this, ICarProperty.Stub.asInterface(iBinder));
                }
                break;
            case -905948230:
                if (str.equals(SENSOR_SERVICE)) {
                    return new CarSensorManager(this, iBinder);
                }
                break;
            case -874200568:
                if (str.equals(VENDOR_EXTENSION_SERVICE)) {
                    return new CarVendorExtensionManager(this, iBinder);
                }
                break;
            case -807062458:
                if (str.equals(PACKAGE_SERVICE)) {
                    return new CarPackageManager(this, iBinder);
                }
                break;
            case -603093501:
                if (str.equals(TEST_SERVICE)) {
                    return new CarTestManagerBinderWrapper(this, iBinder);
                }
                break;
            case -444756694:
                if (str.equals(CAR_DRIVING_STATE_SERVICE)) {
                    return new CarDrivingStateManager(this, iBinder);
                }
                break;
            case -408637895:
                if (str.equals(CAR_OCCUPANT_ZONE_SERVICE)) {
                    return new CarOccupantZoneManager(this, iBinder);
                }
                break;
            case -375708743:
                if (str.equals(CAR_MEDIA_SERVICE)) {
                    return new CarMediaManager(this, iBinder);
                }
                break;
            case -259003252:
                if (str.equals(STORAGE_MONITORING_SERVICE)) {
                    return new CarStorageMonitoringManager(this, iBinder);
                }
                break;
            case 3214768:
                if (str.equals(HVAC_SERVICE)) {
                    return new CarHvacManager(this, iBinder);
                }
                break;
            case 3237038:
                if (str.equals(INFO_SERVICE)) {
                    return new CarInfoManager(this, iBinder);
                }
                break;
            case 93166550:
                if (str.equals(AUDIO_SERVICE)) {
                    return new CarAudioManager(this, iBinder);
                }
                break;
            case 94415849:
                if (str.equals(CABIN_SERVICE)) {
                    return new CarCabinManager(this, iBinder);
                }
                break;
            case 106858757:
                if (str.equals(POWER_SERVICE)) {
                    return new CarPowerManager(this, iBinder);
                }
                break;
            case 486923284:
                if (str.equals(VMS_SUBSCRIBER_SERVICE)) {
                    return VmsSubscriberManager.wrap(this, (VmsClientManager) getCarManager(VEHICLE_MAP_SERVICE));
                }
                break;
            case 807575756:
                if (str.equals(CAR_USER_SERVICE)) {
                    return new CarUserManager(this, iBinder);
                }
                break;
            case 1075548489:
                if (str.equals(CAR_UX_RESTRICTION_SERVICE)) {
                    return new CarUxRestrictionsManager(this, iBinder);
                }
                break;
            case 1391630872:
                if (str.equals(CAR_WATCHDOG_SERVICE)) {
                    return new CarWatchdogManager(this, iBinder);
                }
                break;
            case 1644291440:
                if (str.equals(CAR_INSTRUMENT_CLUSTER_SERVICE)) {
                    return new CarInstrumentClusterManager(this, iBinder);
                }
                break;
            case 1652695153:
                if (str.equals(CAR_INPUT_SERVICE)) {
                    return new CarInputManager(this, iBinder);
                }
                break;
            case 1763569149:
                if (str.equals(CAR_BUGREPORT_SERVICE)) {
                    return new CarBugreportManager(this, iBinder);
                }
                break;
            case 1830376762:
                if (str.equals(APP_FOCUS_SERVICE)) {
                    return new CarAppFocusManager(this, iBinder);
                }
                break;
            case 1891269741:
                if (str.equals(CAR_TRUST_AGENT_ENROLLMENT_SERVICE)) {
                    return new CarTrustAgentEnrollmentManager(this, iBinder);
                }
                break;
            case 1932752118:
                if (str.equals(CAR_CONFIGURATION_SERVICE)) {
                    return new CarConfigurationManager(this, iBinder);
                }
                break;
            case 2012119615:
                if (str.equals(VEHICLE_MAP_SERVICE)) {
                    return new VmsClientManager(this, iBinder);
                }
                break;
        }
        try {
            String carManagerClassForFeature = this.mService.getCarManagerClassForFeature(str);
            if (carManagerClassForFeature != null) {
                return constructCarManager(carManagerClassForFeature, iBinder);
            }
            Log.e(CarLibLog.TAG_CAR, "Cannot construct CarManager for service:" + str + " : no class defined");
            return null;
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
            return null;
        }
    }

    private static Handler determineEventHandler(Handler handler) {
        return handler == null ? new Handler(Looper.getMainLooper()) : handler;
    }

    private static Handler determineMainThreadEventHandler(Handler handler) {
        Looper mainLooper = Looper.getMainLooper();
        return handler.getLooper() == mainLooper ? handler : new Handler(mainLooper);
    }

    private void dispatchCarReadyToMainThread(boolean z) {
        if (z) {
            this.mStatusChangeCallback.onLifecycleChanged(this, true);
        } else {
            this.mMainThreadEventHandler.post(new Runnable(this) { // from class: android.car._$$Lambda$Car$Y2U0t8cCv_IWFLzW1mxSSFa_QVc
                public final Car f$0;

                {
                    this.f$0 = this;
                }

                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$dispatchCarReadyToMainThread$0$Car();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishClient() {
        Context context = this.mContext;
        if (context == null) {
            throw new IllegalStateException("Car service has crashed, null Context");
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isFinishing()) {
                return;
            }
            Log.w(CarLibLog.TAG_CAR, "Car service crashed, client not handling it, finish Activity, created from " + this.mConstructionStack);
            activity.finish();
            return;
        }
        if (!(context instanceof Service)) {
            killClient(null);
            return;
        }
        Service service = (Service) context;
        killClient(service.getPackageName() + "," + service.getClass().getSimpleName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleCarDisconnectLocked() {
        if (this.mConnectionState == 0) {
            return;
        }
        this.mEventHandler.removeCallbacks(this.mConnectionRetryRunnable);
        this.mMainThreadEventHandler.removeCallbacks(this.mConnectionRetryFailedRunnable);
        this.mConnectionRetryCount = 0;
        tearDownCarManagersLocked();
        this.mService = null;
        this.mConnectionState = 0;
    }

    public static <T> T handleRemoteExceptionFromCarService(Service service, RemoteException remoteException, T t) {
        handleRemoteExceptionFromCarService(service, remoteException);
        return t;
    }

    public static void handleRemoteExceptionFromCarService(Service service, RemoteException remoteException) {
        if (remoteException instanceof TransactionTooLargeException) {
            Log.w(CarLibLog.TAG_CAR, "Car service threw TransactionTooLargeException, client:" + service.getPackageName() + "," + service.getClass().getSimpleName(), remoteException);
            throw new CarTransactionException(remoteException, "Car service threw TransactionTooLargeException, client: %s, %s", service.getPackageName(), service.getClass().getSimpleName());
        }
        Log.w(CarLibLog.TAG_CAR, "Car service has crashed, client:" + service.getPackageName() + "," + service.getClass().getSimpleName(), remoteException);
        service.stopSelf();
    }

    private void killClient(String str) {
        Log.w(CarLibLog.TAG_CAR, "**Car service has crashed. Client(" + str + ") is not handling it. Client should use Car.createCar(..., CarServiceLifecycleListener, ...) to handle it properly. Check pritned callstack to check where other version of Car.createCar() was called. Killing the client process**", this.mConstructionStack);
        Process.killProcess(Process.myPid());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startCarService() {
        Intent intent = new Intent();
        intent.setPackage(CAR_SERVICE_PACKAGE);
        intent.setAction(CAR_SERVICE_INTERFACE_NAME);
        boolean zBindServiceAsUser = this.mContext.bindServiceAsUser(intent, this.mServiceConnectionListener, 1, UserHandle.CURRENT_OR_SELF);
        synchronized (this.mLock) {
            try {
                if (zBindServiceAsUser) {
                    this.mEventHandler.removeCallbacks(this.mConnectionRetryRunnable);
                    this.mMainThreadEventHandler.removeCallbacks(this.mConnectionRetryFailedRunnable);
                    this.mConnectionRetryCount = 0;
                    this.mServiceBound = true;
                } else {
                    int i = this.mConnectionRetryCount + 1;
                    this.mConnectionRetryCount = i;
                    if (i > CAR_SERVICE_BIND_MAX_RETRY) {
                        Log.w(CarLibLog.TAG_CAR, "cannot bind to car service after max retry");
                        this.mMainThreadEventHandler.post(this.mConnectionRetryFailedRunnable);
                    } else {
                        this.mEventHandler.postDelayed(this.mConnectionRetryRunnable, CAR_SERVICE_BIND_RETRY_INTERVAL_MS);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void tearDownCarManagersLocked() {
        Iterator<CarManagerBase> it = this.mServiceMap.values().iterator();
        while (it.hasNext()) {
            it.next().onCarDisconnected();
        }
        this.mServiceMap.clear();
    }

    @Deprecated
    public void connect() throws IllegalStateException {
        synchronized (this.mLock) {
            if (this.mConnectionState != 0) {
                throw new IllegalStateException("already connected or connecting");
            }
            this.mConnectionState = 1;
            startCarService();
        }
    }

    @SystemApi
    public int disableFeature(String str) {
        synchronized (this.mLock) {
            ICar iCar = this.mService;
            if (iCar == null) {
                return 3;
            }
            try {
                return iCar.disableFeature(str);
            } catch (RemoteException e) {
                return ((Integer) handleRemoteExceptionFromCarService(e, 3)).intValue();
            }
        }
    }

    public void disconnect() {
        synchronized (this.mLock) {
            handleCarDisconnectLocked();
            if (this.mServiceBound) {
                this.mContext.unbindService(this.mServiceConnectionListener);
                this.mServiceBound = false;
            }
        }
    }

    @SystemApi
    public int enableFeature(String str) {
        synchronized (this.mLock) {
            ICar iCar = this.mService;
            if (iCar == null) {
                return 3;
            }
            try {
                return iCar.enableFeature(str);
            } catch (RemoteException e) {
                return ((Integer) handleRemoteExceptionFromCarService(e, 3)).intValue();
            }
        }
    }

    @SystemApi
    public List<String> getAllEnabledFeatures() {
        synchronized (this.mLock) {
            ICar iCar = this.mService;
            if (iCar == null) {
                return Collections.EMPTY_LIST;
            }
            try {
                return iCar.getAllEnabledFeatures();
            } catch (RemoteException e) {
                return (List) handleRemoteExceptionFromCarService(e, Collections.EMPTY_LIST);
            }
        }
    }

    @SystemApi
    public List<String> getAllPendingDisabledFeatures() {
        synchronized (this.mLock) {
            ICar iCar = this.mService;
            if (iCar == null) {
                return Collections.EMPTY_LIST;
            }
            try {
                return iCar.getAllPendingDisabledFeatures();
            } catch (RemoteException e) {
                return (List) handleRemoteExceptionFromCarService(e, Collections.EMPTY_LIST);
            }
        }
    }

    @SystemApi
    public List<String> getAllPendingEnabledFeatures() {
        synchronized (this.mLock) {
            ICar iCar = this.mService;
            if (iCar == null) {
                return Collections.EMPTY_LIST;
            }
            try {
                return iCar.getAllPendingEnabledFeatures();
            } catch (RemoteException e) {
                return (List) handleRemoteExceptionFromCarService(e, Collections.EMPTY_LIST);
            }
        }
    }

    public int getCarConnectionType() {
        return 5;
    }

    public Object getCarManager(String str) {
        synchronized (this.mLock) {
            if (this.mService == null) {
                Log.w(CarLibLog.TAG_CAR, "getCarManager not working while car service not ready");
                return null;
            }
            CarManagerBase carManagerBaseCreateCarManagerLocked = this.mServiceMap.get(str);
            if (carManagerBaseCreateCarManagerLocked == null) {
                try {
                    IBinder carService = this.mService.getCarService(str);
                    if (carService == null) {
                        Log.w(CarLibLog.TAG_CAR, "getCarManager could not get binder for service:" + str);
                        return null;
                    }
                    carManagerBaseCreateCarManagerLocked = createCarManagerLocked(str, carService);
                    if (carManagerBaseCreateCarManagerLocked == null) {
                        Log.w(CarLibLog.TAG_CAR, "getCarManager could not create manager for service:" + str);
                        return null;
                    }
                    this.mServiceMap.put(str, carManagerBaseCreateCarManagerLocked);
                } catch (RemoteException e) {
                    handleRemoteExceptionFromCarService(e);
                }
            }
            return carManagerBaseCreateCarManagerLocked;
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    public Handler getEventHandler() {
        return this.mEventHandler;
    }

    public ServiceConnection getServiceConnectionListener() {
        return this.mServiceConnectionListener;
    }

    public <T> T handleRemoteExceptionFromCarService(RemoteException remoteException, T t) {
        handleRemoteExceptionFromCarService(remoteException);
        return t;
    }

    void handleRemoteExceptionFromCarService(RemoteException remoteException) {
        if (remoteException instanceof TransactionTooLargeException) {
            Log.w(CarLibLog.TAG_CAR, "Car service threw TransactionTooLargeException", remoteException);
            throw new CarTransactionException(remoteException, "Car service threw TransactionTooLargException", new Object[0]);
        }
        Log.w(CarLibLog.TAG_CAR, "Car service has crashed", remoteException);
    }

    public boolean isConnected() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mService != null;
        }
        return z;
    }

    public boolean isConnecting() {
        boolean z;
        synchronized (this.mLock) {
            z = true;
            if (this.mConnectionState != 1) {
                z = false;
            }
        }
        return z;
    }

    public boolean isFeatureEnabled(String str) {
        synchronized (this.mLock) {
            ICar iCar = this.mService;
            if (iCar == null) {
                return false;
            }
            return this.mFeatures.isFeatureEnabled(iCar, str);
        }
    }

    public /* synthetic */ void lambda$dispatchCarReadyToMainThread$0$Car() {
        this.mStatusChangeCallback.onLifecycleChanged(this, true);
    }
}
