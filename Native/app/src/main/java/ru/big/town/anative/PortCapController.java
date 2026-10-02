package ru.big.town.anative;

import android.content.Context;
import java.util.Collections;

/* JADX INFO: loaded from: classes2.dex */
final class PortCapController {

    enum Outcome {
        ACCEPTED,
        NOT_IN_PARK,
        STATE_UNAVAILABLE,
        TRANSPORT_FAILURE
    }

    PortCapController() {
    }

    static final class OpenResult {
        final Outcome outcome;
        final Integer refillLiters;

        OpenResult(Outcome outcome, Integer num) {
            this.outcome = outcome;
            this.refillLiters = num;
        }
    }

    private static OpenResult result(Outcome outcome) {
        return new OpenResult(outcome, null);
    }

    private static OemVehicleStateTransport.StateKey keyFor(String str) {
        if ("port_cap:fuel".equals(str)) {
            return new OemVehicleStateTransport.StateKey("IVI_FUEL_PORT_CAP", 778);
        }
        if ("port_cap:charge".equals(str)) {
            return new OemVehicleStateTransport.StateKey("IVI_CHRG_PORT_CAP", 779);
        }
        return null;
    }

    static OpenResult open(Context context, final String str) {
        OemVehicleStateTransport.StateKey stateKeyKeyFor = keyFor(str);
        if (stateKeyKeyFor == null) {
            return result(Outcome.TRANSPORT_FAILURE);
        }
        OpenResult openResult = (OpenResult) OemVehicleStateTransport.withSession(context, Collections.singleton(stateKeyKeyFor), new OemVehicleStateTransport.SessionOperation() { // from class: ru.big.town.anative.PortCapController$$ExternalSyntheticLambda0
            @Override // ru.big.town.anative.OemVehicleStateTransport.SessionOperation
            public final Object run(OemVehicleStateTransport.Session session) {
                return PortCapController.open(session, str);
            }
        });
        return openResult == null ? result(Outcome.TRANSPORT_FAILURE) : openResult;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static OpenResult open(OemVehicleStateTransport.Session session, String str) {
        OemVehicleStateTransport.StateKey stateKeyKeyFor = keyFor(str);
        if (stateKeyKeyFor == null) {
            return result(Outcome.TRANSPORT_FAILURE);
        }
        OemVehicleStateTransport.GearStatus gearStatus = session.readGearStatus();
        if (gearStatus == null) {
            return result(Outcome.STATE_UNAVAILABLE);
        }
        if (gearStatus.ordinal != 0 || gearStatus.value != 0) {
            return result(Outcome.NOT_IN_PARK);
        }
        if (!session.sendVehicleState(new OemVehicleStateTransport.StateValue(stateKeyKeyFor, 1), "voice open " + stateKeyKeyFor.name).accepted()) {
            return result(Outcome.TRANSPORT_FAILURE);
        }
        if ("port_cap:fuel".equals(str)) {
            try {
                OemVehicleStateTransport.FuelLevel fuelLevel = session.readFuelLevel();
                if (fuelLevel != null) {
                    return new OpenResult(Outcome.ACCEPTED, FuelRefillEstimate.liters(fuelLevel.capacityLiters, fuelLevel.remainingPercent));
                }
            } catch (RuntimeException unused) {
            }
        }
        return result(Outcome.ACCEPTED);
    }
}
