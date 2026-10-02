package ru.big.town.anative;

import android.content.Context;
import android.util.Log;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
final class DriveModeCanTransport {
    private static final String TAG = "$$$ DriveModeCanTransport $$$";

    enum DispatchResult {
        ACCEPTED_UNCONFIRMED,
        TRANSIENT_FAILURE;

        boolean accepted() {
            return this == ACCEPTED_UNCONFIRMED;
        }
    }

    private DriveModeCanTransport() {
    }

    static boolean send(Context context, String str) {
        return dispatch(context, str).accepted();
    }

    static boolean send(String str) {
        return send(GlobalVars.SAVE_CONTEXT, str);
    }

    static DispatchResult dispatch(Context context, String str) {
        Map<OemVehicleStateTransport.StateKey, Integer> mapStatesFor = statesFor(context, str);
        if (mapStatesFor == null) {
            return DispatchResult.TRANSIENT_FAILURE;
        }
        if (OemVehicleStateTransport.sendBundle(context, mapStatesFor, "drive mode: " + str).accepted()) {
            Log.i(TAG, "OEM drive-mode bundle accepted-unconfirmed: " + str);
            return DispatchResult.ACCEPTED_UNCONFIRMED;
        }
        return DispatchResult.TRANSIENT_FAILURE;
    }

    static Map<OemVehicleStateTransport.StateKey, Integer> statesFor(Context context, String str) {
        if (!DriveModeCanPolicy.isSupported(str)) {
            Log.e(TAG, "Unsupported drive mode: " + str);
            return null;
        }
        DriveModeCanPolicy.Plan planPlanFor = DriveModeCanPolicy.planFor(str, "INDIVIDUAL".equals(str) ? OemIndividualDriveProfileReader.read(context) : null);
        if (planPlanFor == null) {
            Log.e(TAG, "Cannot build safe OEM plan for drive mode " + str);
            return null;
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (Map.Entry<DriveModeCanPolicy.VehicleStateKey, Integer> entry : planPlanFor.values().entrySet()) {
            DriveModeCanPolicy.VehicleStateKey key = entry.getKey();
            linkedHashMap.put(new OemVehicleStateTransport.StateKey(key.name(), key.stableId), entry.getValue());
        }
        return Collections.unmodifiableMap(linkedHashMap);
    }

    static boolean appendStates(Context context, String str, Map<String, Integer> map, Map<String, Integer> map2) {
        if (map == null || map2 == null) {
            throw new IllegalArgumentException("Drive-mode target maps are null");
        }
        Map<OemVehicleStateTransport.StateKey, Integer> mapStatesFor = statesFor(context, str);
        if (mapStatesFor == null) {
            return false;
        }
        for (Map.Entry<OemVehicleStateTransport.StateKey, Integer> entry : mapStatesFor.entrySet()) {
            map.put(entry.getKey().name, entry.getValue());
            map2.put(entry.getKey().name, Integer.valueOf(entry.getKey().stableId));
        }
        return true;
    }
}
