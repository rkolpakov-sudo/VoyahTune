package ru.big.town.anative;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

/* JADX INFO: loaded from: classes2.dex */
final class VehicleStateControllers {
    private static final String TAG = "VehicleStateControllers";
    private static volatile VehicleStateControllers instance;
    private final Context appContext;
    private final CanBusEventHub canBusEventHub;
    private final CanBusEventHub.Subscription canBusSubscription;
    private final DriverDoorStateController driverDoorStateController;
    private final GearStateController gearStateController;
    private final ModeFeedbackController modeFeedbackController;
    private final Handler stateHandler;
    private final HandlerThread stateThread;

    static VehicleStateControllers get(Context context) {
        VehicleStateControllers vehicleStateControllers;
        VehicleStateControllers vehicleStateControllers2 = instance;
        if (vehicleStateControllers2 != null) {
            return vehicleStateControllers2;
        }
        synchronized (VehicleStateControllers.class) {
            vehicleStateControllers = instance;
            if (vehicleStateControllers == null) {
                vehicleStateControllers = new VehicleStateControllers(context.getApplicationContext());
                instance = vehicleStateControllers;
            }
        }
        return vehicleStateControllers;
    }

    private VehicleStateControllers(Context context) {
        ModeFeedbackController modeFeedbackControllerCreate;
        this.appContext = context;
        HandlerThread handlerThread = new HandlerThread(TAG);
        this.stateThread = handlerThread;
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        this.stateHandler = handler;
        this.gearStateController = new GearStateController(handler);
        this.driverDoorStateController = new DriverDoorStateController(handler);
        try {
            modeFeedbackControllerCreate = ModeFeedbackController.create(context, handler);
        } catch (RuntimeException e) {
            Log.w(TAG, "start mode feedback: " + e.getMessage());
            modeFeedbackControllerCreate = null;
        }
        this.modeFeedbackController = modeFeedbackControllerCreate;
        CanBusEventHub canBusEventHub = CanBusEventHub.get(this.appContext);
        this.canBusEventHub = canBusEventHub;
        try {
            this.canBusSubscription = canBusEventHub.subscribe(23, new int[]{545, 957, 619}, this.stateHandler, new CanBusEventHub.Listener() { // from class: ru.big.town.anative.VehicleStateControllers$$ExternalSyntheticLambda0
                @Override // ru.big.town.anative.CanBusEventHub.Listener
                public final void onCanBusEvent(CanBusEvent canBusEvent) {
                    VehicleStateControllers.this.onCanBusEvent(canBusEvent);
                }
            });
        } catch (RuntimeException e2) {
            ModeFeedbackController modeFeedbackController = this.modeFeedbackController;
            if (modeFeedbackController != null) {
                modeFeedbackController.close();
            }
            this.stateThread.quitSafely();
            throw e2;
        }
    }

    GearStateController gear() {
        return this.gearStateController;
    }

    DriverDoorStateController driverDoor() {
        return this.driverDoorStateController;
    }

    /* JADX INFO: renamed from: ru.big.town.anative.VehicleStateControllers$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$ru$big$town$anative$CanBusEvent$Kind;

        static {
            int[] iArr = new int[CanBusEvent.Kind.values().length];
            $SwitchMap$ru$big$town$anative$CanBusEvent$Kind = iArr;
            try {
                iArr[CanBusEvent.Kind.CONNECTION.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$ru$big$town$anative$CanBusEvent$Kind[CanBusEvent.Kind.CONNECTION_LOST.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$ru$big$town$anative$CanBusEvent$Kind[CanBusEvent.Kind.DOOR.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$ru$big$town$anative$CanBusEvent$Kind[CanBusEvent.Kind.GEAR.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$ru$big$town$anative$CanBusEvent$Kind[CanBusEvent.Kind.VEHICLE_STATE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCanBusEvent(CanBusEvent canBusEvent) {
        DriverDoorStateController.Source source;
        ModeFeedbackController modeFeedbackController;
        int i = AnonymousClass1.$SwitchMap$ru$big$town$anative$CanBusEvent$Kind[canBusEvent.kind.ordinal()];
        if (i == 1) {
            this.gearStateController.reset();
            this.driverDoorStateController.reset();
            this.canBusEventHub.requestDriverDoorSeed();
            ModeFeedbackController modeFeedbackController2 = this.modeFeedbackController;
            if (modeFeedbackController2 != null) {
                modeFeedbackController2.onConnected();
                return;
            }
            return;
        }
        if (i == 2) {
            this.gearStateController.reset();
            this.driverDoorStateController.reset();
            return;
        }
        if (i == 3) {
            DriverDoorStateController driverDoorStateController = this.driverDoorStateController;
            int i2 = canBusEvent.first;
            if (canBusEvent.origin == CanBusEvent.Origin.LIVE) {
                source = DriverDoorStateController.Source.LIVE;
            } else {
                source = DriverDoorStateController.Source.SNAPSHOT;
            }
            driverDoorStateController.accept(i2, source);
            return;
        }
        if (i == 4) {
            this.gearStateController.accept(canBusEvent.first);
        } else if (i == 5 && (modeFeedbackController = this.modeFeedbackController) != null) {
            modeFeedbackController.onVehicleState(canBusEvent.first, canBusEvent.second);
        }
    }
}
