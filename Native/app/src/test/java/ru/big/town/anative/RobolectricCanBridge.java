package ru.big.town.anative;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;

import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowApplication;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ru.big.town.hil.CanEmulatorCore;
import ru.big.town.hil.scenario.HilHarness;
import ru.big.town.hil.scenario.JvmHarness;

/**
 * HIL harness that runs scenario SEND steps through the real native stack
 * ({@link OemVehicleStateTransport} -> bindService -> {@link FakeCanBusBinder}
 * -> {@link CanEmulatorCore}) and delivers emulator echoes back through the
 * real {@link CanBusEventHub} callback pipeline.
 */
public final class RobolectricCanBridge implements HilHarness {
    private static final String CANBUS_SERVICE_ACTION = "com.qinggan.canbus.CanBusService";
    private static final String CANBUS_SERVICE_PACKAGE = "com.qinggan.canbus.service";
    private static final String WRITE_PERMISSION = "com.qinggan.permission.WRITE_CANBUS";
    private static final String CALLBACK_DESCRIPTOR = "com.qinggan.canbus.ICanBusServiceCallback";
    private static final int[] TRACKED_IDS = {665, 545, 957, 619};

    private final Context context;
    private final FakeCanBusBinder fake;
    private final JvmHarness vm = new JvmHarness();
    private final CanBusEventHub hub;
    private final List<Handler> hubHandlers = new ArrayList<>();
    private final AtomicInteger routedId = new AtomicInteger(-1);
    private final AtomicInteger routedValue = new AtomicInteger(-1);

    private RobolectricCanBridge(Context context, CanEmulatorCore core) {
        this.context = context;
        this.fake = new FakeCanBusBinder(core);
        setupEnvironment();
        this.hub = CanBusEventHub.get(context);
        collectHubHandlers();
        this.hub.subscribe(16, TRACKED_IDS, new Handler(Looper.getMainLooper()), this::onAppEvent);
        pump();
    }

    public static RobolectricCanBridge create(Context context, CanEmulatorCore core) {
        return new RobolectricCanBridge(context.getApplicationContext(), core);
    }

    private void setupEnvironment() {
        Application app = (Application) this.context;
        Shadows.shadowOf(app).grantPermissions(WRITE_PERMISSION);

        PackageInfo info = new PackageInfo();
        info.packageName = CANBUS_SERVICE_PACKAGE;
        info.applicationInfo = new ApplicationInfo();
        info.applicationInfo.packageName = CANBUS_SERVICE_PACKAGE;
        info.applicationInfo.sourceDir = "/hil/fake-canbus.apk";
        Shadows.shadowOf(this.context.getPackageManager()).addPackage(info);

        Intent intent = new Intent(CANBUS_SERVICE_ACTION).setPackage(CANBUS_SERVICE_PACKAGE);
        ComponentName component = new ComponentName(CANBUS_SERVICE_PACKAGE,
                CANBUS_SERVICE_PACKAGE + ".CanBusService");
        ShadowApplication shadowApp = Shadows.shadowOf(app);
        shadowApp.setComponentNameAndServiceForBindServiceForIntent(intent, component, this.fake);
        shadowApp.setBindServiceCallsOnServiceConnectedDirectly(true);
    }

    private void collectHubHandlers() {
        for (Field field : CanBusEventHub.class.getDeclaredFields()) {
            if (Handler.class.isAssignableFrom(field.getType())) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(this.hub);
                    if (value instanceof Handler) {
                        this.hubHandlers.add((Handler) value);
                    }
                } catch (ReflectiveOperationException | RuntimeException e) {
                    throw new IllegalStateException("cannot read hub handler " + field.getName(), e);
                }
            }
        }
    }

    /** Drains the main looper and the hub's background loopers (bind/TX28/queries are async). */
    public void pump() {
        for (int i = 0; i < 4; i++) {
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            for (Handler handler : this.hubHandlers) {
                try {
                    Shadows.shadowOf(handler.getLooper()).idle();
                } catch (RuntimeException ignored) {
                    // handler may already be quit; nothing to drain
                }
            }
        }
    }

    private void onAppEvent(CanBusEvent event) {
        if (event.kind == CanBusEvent.Kind.VEHICLE_STATE) {
            this.routedId.set(event.first);
            this.routedValue.set(event.second);
        }
    }

    @Override
    public void sleep(long millis) {
        pump();
        this.vm.sleep(millis);
        pump();
    }

    @Override
    public void action(String name, CanEmulatorCore core) {
        this.vm.action(name, core);
        pump();
    }

    @Override
    public Object appState(String name) {
        if ("routed_id".equals(name)) {
            int value = this.routedId.get();
            return value < 0 ? null : value;
        }
        if ("routed_value".equals(name)) {
            int value = this.routedValue.get();
            return value < 0 ? null : value;
        }
        return this.vm.appState(name);
    }

    @Override
    public void onSend(int stateId, int value) {
        this.vm.onSend(stateId, value);
    }

    @Override
    public boolean sendState(CanEmulatorCore core, String stateName, int stateId, int ordinal, int value) {
        this.vm.onSend(stateId, value);
        OemVehicleStateTransport.Result result =
                OemVehicleStateTransport.sendVehicleState(this.context, stateName, stateId, value, "hil-bridge");
        System.out.println("BRIDGE sendState " + stateName + "=" + value + " -> " + result);
        if (result != null && !result.accepted()) {
            dumpTransportState();
        }
        return result != null && result.accepted();
    }

    private void dumpTransportState() {
        try {
            for (String name : new String[] {"vehicleStateClass", "canBusBinder", "activeConnection",
                    "bindingInProgress", "resolvedOrdinals"}) {
                java.lang.reflect.Field field = OemVehicleStateTransport.class.getDeclaredField(name);
                field.setAccessible(true);
                System.out.println("TRANS " + name + "=" + field.get(null));
            }
        } catch (Throwable t) {
            System.out.println("TRANS dump failed: " + t);
        }
        System.out.println("TRANS perm=" + this.context.checkSelfPermission(WRITE_PERMISSION)
                + " mainThread=" + (Looper.myLooper() == Looper.getMainLooper()));
    }

    @Override
    public void onPush(int cbCode, List<Object> args) {
        this.vm.onPush(cbCode, args);
        IBinder callback = this.fake.callbackBinder();
        if (callback == null) {
            return;
        }
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(CALLBACK_DESCRIPTOR);
            for (Object arg : args) {
                FakeCanBusBinder.writeArg(data, arg);
            }
            callback.transact(cbCode, data, reply, IBinder.FLAG_ONEWAY);
        } catch (RemoteException e) {
            throw new IllegalStateException("echo delivery to app callback failed", e);
        } finally {
            reply.recycle();
            data.recycle();
        }
    }
}
