package ru.big.town.anative;

import android.content.Context;
import android.util.Log;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
final class PowerHoldController {
    private static final String TAG = "PowerHoldController";
    private final VehicleGateway gateway;
    private static final OemVehicleStateTransport.StateKey BMS_SOC_KEY = new OemVehicleStateTransport.StateKey("BMS_SOC_DISPLAY", 615);
    private static final OemVehicleStateTransport.StateKey EXTENDER_KEY = new OemVehicleStateTransport.StateKey("SCENE_MODE_EXTENDER_SET", 1127);
    private static final OemVehicleStateTransport.StateKey SWITCH_KEY = new OemVehicleStateTransport.StateKey("POWER_HOLD_MODE_SWITCH", 1161);
    private static final OemVehicleStateTransport.StateKey TIME_KEY = new OemVehicleStateTransport.StateKey("POWER_HOLD_MODE_TIME", 1162);

    interface Session {
        Gear readGear();

        Integer readSoc();

        boolean sendActivation(Map<String, Integer> map, String str);
    }

    interface SessionAction {
        PowerHoldPolicy.Outcome run(Session session);
    }

    interface VehicleGateway {
        PowerHoldPolicy.Outcome runActivation(SessionAction sessionAction);
    }

    static final class Gear {
        final int ordinal;
        final int value;

        Gear(int i, int i2) {
            this.ordinal = i;
            this.value = i2;
        }
    }

    static PowerHoldController create(Context context) {
        final Context applicationContext = context.getApplicationContext();
        return new PowerHoldController(new VehicleGateway() { // from class: ru.big.town.anative.PowerHoldController$$ExternalSyntheticLambda2
            @Override // ru.big.town.anative.PowerHoldController.VehicleGateway
            public final PowerHoldPolicy.Outcome runActivation(PowerHoldController.SessionAction sessionAction) {
                return PowerHoldController.lambda$create$1(applicationContext, sessionAction);
            }
        });
    }

    static /* synthetic */ PowerHoldPolicy.Outcome lambda$create$1(Context context, final SessionAction sessionAction) {
        PowerHoldPolicy.Outcome outcome = (PowerHoldPolicy.Outcome) OemVehicleStateTransport.withSession(context, Arrays.asList(BMS_SOC_KEY, EXTENDER_KEY, SWITCH_KEY, TIME_KEY), new OemVehicleStateTransport.SessionOperation() { // from class: ru.big.town.anative.PowerHoldController$$ExternalSyntheticLambda0
            @Override // ru.big.town.anative.OemVehicleStateTransport.SessionOperation
            public final Object run(OemVehicleStateTransport.Session session) {
                return sessionAction.run(new PowerHoldController.Session() { // from class: ru.big.town.anative.PowerHoldController.1
                    @Override // ru.big.town.anative.PowerHoldController.Session
                    public Gear readGear() {
                        OemVehicleStateTransport.GearStatus gearStatus = session.readGearStatus();
                        if (gearStatus == null) {
                            return null;
                        }
                        return new Gear(gearStatus.ordinal, gearStatus.value);
                    }

                    @Override // ru.big.town.anative.PowerHoldController.Session
                    public Integer readSoc() {
                        return session.readVehicleState(PowerHoldController.BMS_SOC_KEY);
                    }

                    @Override // ru.big.town.anative.PowerHoldController.Session
                    public boolean sendActivation(Map<String, Integer> map, String str) {
                        LinkedHashMap linkedHashMap = new LinkedHashMap();
                        linkedHashMap.put(PowerHoldController.TIME_KEY, map.get("POWER_HOLD_MODE_TIME"));
                        linkedHashMap.put(PowerHoldController.EXTENDER_KEY, map.get("SCENE_MODE_EXTENDER_SET"));
                        linkedHashMap.put(PowerHoldController.SWITCH_KEY, map.get("POWER_HOLD_MODE_SWITCH"));
                        return session.sendBundle(linkedHashMap, str).accepted();
                    }
                });
            }
        });
        return outcome == null ? PowerHoldPolicy.Outcome.TRANSPORT_FAILURE : outcome;
    }

    PowerHoldController(VehicleGateway vehicleGateway) {
        if (vehicleGateway == null) {
            throw new IllegalArgumentException("Power Hold gateway is null");
        }
        this.gateway = vehicleGateway;
    }

    PowerHoldPolicy.Outcome activate() {
        try {
            PowerHoldPolicy.Outcome outcomeRunActivation = this.gateway.runActivation(new SessionAction() { // from class: ru.big.town.anative.PowerHoldController$$ExternalSyntheticLambda1
                @Override // ru.big.town.anative.PowerHoldController.SessionAction
                public final PowerHoldPolicy.Outcome run(PowerHoldController.Session session) {
                    return PowerHoldController.lambda$activate$2(session);
                }
            });
            return outcomeRunActivation == null ? PowerHoldPolicy.Outcome.TRANSPORT_FAILURE : outcomeRunActivation;
        } catch (RuntimeException e) {
            try {
                Log.e(TAG, "Power Hold activation failed", e);
            } catch (RuntimeException unused) {
            }
            return PowerHoldPolicy.Outcome.TRANSPORT_FAILURE;
        }
    }

    static /* synthetic */ PowerHoldPolicy.Outcome lambda$activate$2(Session session) {
        Gear gear = session.readGear();
        if (gear == null) {
            return PowerHoldPolicy.Outcome.STATE_UNAVAILABLE;
        }
        if (!PowerHoldPolicy.isParking(gear.ordinal, gear.value)) {
            return PowerHoldPolicy.Outcome.NOT_IN_PARK;
        }
        Integer soc = session.readSoc();
        if (soc == null) {
            return PowerHoldPolicy.Outcome.STATE_UNAVAILABLE;
        }
        PowerHoldPolicy.Outcome outcomeValidate = PowerHoldPolicy.validate(gear.ordinal, gear.value, soc.intValue());
        if (outcomeValidate != PowerHoldPolicy.Outcome.ACCEPTED) {
            return outcomeValidate;
        }
        if (session.sendActivation(PowerHoldPolicy.activationValues(), "power hold activate")) {
            return PowerHoldPolicy.Outcome.ACCEPTED;
        }
        return PowerHoldPolicy.Outcome.TRANSPORT_FAILURE;
    }
}
