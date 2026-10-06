package ru.big.town.anative;

import android.content.Context;
import java.util.Collections;

/* JADX INFO: loaded from: classes2.dex */
final class WashModeController {
    private static final OemVehicleStateTransport.StateKey CLEANING_MODE_KEY = new OemVehicleStateTransport.StateKey("CAR_CLEANING_MODE_SWITCH", 1133);
    private final VehicleGateway gateway;
    private final WashModeRequestLease lease;

    interface Session {
        Gear readGear();

        boolean sendCleaning(int i, String str);
    }

    interface SessionAction {
        WashModePolicy.Outcome run(Session session);
    }

    interface VehicleGateway {
        WashModePolicy.Outcome runActivation(SessionAction sessionAction);

        boolean sendCleaning(int i, String str);
    }

    static final class Gear {
        final int ordinal;
        final int value;

        Gear(int i, int i2) {
            this.ordinal = i;
            this.value = i2;
        }
    }

    /* JADX INFO: renamed from: ru.big.town.anative.WashModeController$1, reason: invalid class name */
    static class AnonymousClass1 implements VehicleGateway {
        final /* synthetic */ Context val$app;

        AnonymousClass1(Context context) {
            this.val$app = context;
        }

        @Override // ru.big.town.anative.WashModeController.VehicleGateway
        public WashModePolicy.Outcome runActivation(final SessionAction sessionAction) {
            WashModePolicy.Outcome outcome = (WashModePolicy.Outcome) OemVehicleStateTransport.withSession(this.val$app, Collections.singleton(WashModeController.CLEANING_MODE_KEY), new OemVehicleStateTransport.SessionOperation() { // from class: ru.big.town.anative.WashModeController$1$$ExternalSyntheticLambda0
                @Override // ru.big.town.anative.OemVehicleStateTransport.SessionOperation
                public final Object run(OemVehicleStateTransport.Session session) {
                    return AnonymousClass1.this.m2165lambda$runActivation$0$rubigtownanativeWashModeController$1(sessionAction, session);
                }
            });
            return outcome == null ? WashModePolicy.Outcome.TRANSPORT_FAILURE : outcome;
        }

        /* JADX INFO: renamed from: lambda$runActivation$0$ru-big-town-anative-WashModeController$1, reason: not valid java name */
        /* synthetic */ WashModePolicy.Outcome m2165lambda$runActivation$0$rubigtownanativeWashModeController$1(SessionAction sessionAction, final OemVehicleStateTransport.Session oemSession) {
            return sessionAction.run(new Session() { // from class: ru.big.town.anative.WashModeController.1.1
                @Override // ru.big.town.anative.WashModeController.Session
                public Gear readGear() {
                    OemVehicleStateTransport.GearStatus gearStatus = oemSession.readGearStatus();
                    if (gearStatus == null) {
                        return null;
                    }
                    return new Gear(gearStatus.ordinal, gearStatus.value);
                }

                @Override // ru.big.town.anative.WashModeController.Session
                public boolean sendCleaning(int i, String str) {
                    return oemSession.sendVehicleState(new OemVehicleStateTransport.StateValue(WashModeController.CLEANING_MODE_KEY, i), str).accepted();
                }
            });
        }

        @Override // ru.big.town.anative.WashModeController.VehicleGateway
        public boolean sendCleaning(int i, String str) {
            return OemVehicleStateTransport.sendVehicleState(this.val$app, WashModeController.CLEANING_MODE_KEY, i, str).accepted();
        }
    }

    static WashModeController create(Context context) {
        Context applicationContext = context.getApplicationContext();
        return new WashModeController(new AnonymousClass1(applicationContext), WashModeRequestLease.from(applicationContext));
    }

    WashModeController(VehicleGateway vehicleGateway, WashModeRequestLease washModeRequestLease) {
        if (vehicleGateway == null) {
            throw new IllegalArgumentException("Wash gateway is null");
        }
        if (washModeRequestLease == null) {
            throw new IllegalArgumentException("Wash lease is null");
        }
        this.gateway = vehicleGateway;
        this.lease = washModeRequestLease;
    }

    WashModePolicy.Outcome activate() {
        return this.gateway.runActivation(new SessionAction() { // from class: ru.big.town.anative.WashModeController$$ExternalSyntheticLambda0
            @Override // ru.big.town.anative.WashModeController.SessionAction
            public final WashModePolicy.Outcome run(WashModeController.Session session) {
                return WashModeController.this.m2164lambda$activate$0$rubigtownanativeWashModeController(session);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$activate$0$ru-big-town-anative-WashModeController, reason: not valid java name */
    /* synthetic */ WashModePolicy.Outcome m2164lambda$activate$0$rubigtownanativeWashModeController(Session session) {
        Gear gear = session.readGear();
        if (gear == null) {
            return WashModePolicy.Outcome.TRANSPORT_FAILURE;
        }
        if (!WashModePolicy.isParking(gear.ordinal, gear.value)) {
            return WashModePolicy.Outcome.NOT_IN_PARK;
        }
        long generation = lease.arm();
        if (generation == 0) {
            return WashModePolicy.Outcome.TRANSPORT_FAILURE;
        }
        if (!session.sendCleaning(WashModePolicy.CLEANING_ON, "wash mode activate")) {
            lease.disarm(generation);
            return WashModePolicy.Outcome.TRANSPORT_FAILURE;
        }
        return WashModePolicy.Outcome.ACCEPTED;
    }

    boolean cleanupRequestBit(String reason) {
        long jActiveGeneration = this.lease.activeGeneration();
        if (jActiveGeneration == 0) {
            return true;
        }
        if (this.gateway.sendCleaning(WashModePolicy.CLEANING_OFF, "wash mode cleanup: " + reason)) {
            return this.lease.disarm(jActiveGeneration);
        }
        return false;
    }

    boolean hasArmedRequest() {
        return this.lease.activeGeneration() != 0;
    }
}
