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
        final Map<OemVehicleStateTransport.StateKey, Integer> mapStatesFor = statesFor(context, str);
        if (mapStatesFor == null) {
            return DispatchResult.TRANSIENT_FAILURE;
        }
        if ("INDIVIDUAL".equals(str)) {
            // IMP-03 (R4): две TX77 — кадр режима, затем кадр руль/педаль
            return dispatchSplitIndividual(context, mapStatesFor, str);
        }
        return dispatchBundle(context, mapStatesFor, str);
    }

    private static DispatchResult dispatchBundle(Context context, Map<OemVehicleStateTransport.StateKey, Integer> states, String label) {
        if (CommandStatusHub.get().submit(ReadBackTable.FEATURE_DRIVE_MODE, new CommandDispatcher.SendAction() { // from class: ru.big.town.anative.DriveModeCanTransport.1
            @Override // ru.big.town.anative.CommandDispatcher.SendAction
            public boolean send() {
                if (OemVehicleStateTransport.sendBundle(context, states, "drive mode: " + label).accepted()) {
                    Log.i(TAG, "OEM drive-mode bundle accepted-unconfirmed: " + label);
                    return true;
                }
                return false;
            }
        })) {
            return DispatchResult.ACCEPTED_UNCONFIRMED;
        }
        return DispatchResult.TRANSIENT_FAILURE;
    }

    private static DispatchResult dispatchSplitIndividual(Context context, final Map<OemVehicleStateTransport.StateKey, Integer> states, String label) {
        // Трасса A (SPEC L45): одна заявка в CommandStatusHub, внутри две TX77
        // в порядке OEM: кадр режима (DRIVING_MODE_SET), затем кадр руль/педаль
        // (EPS_MODE_SET + PROP_MODE_SET). Fallback (Трасса B): если кадр режима
        // не ушёл — одна TX77 целиком + лог.
        final Map<OemVehicleStateTransport.StateKey, Integer> modeOnly = new LinkedHashMap();
        final Map<OemVehicleStateTransport.StateKey, Integer> steerPedal = new LinkedHashMap();
        for (Map.Entry<OemVehicleStateTransport.StateKey, Integer> e : states.entrySet()) {
            String name = e.getKey().name;
            if (name.contains("DRIVING_MODE")) {
                modeOnly.put(e.getKey(), e.getValue());
            } else if (name.contains("EPS_MODE") || name.contains("PROP_MODE")) {
                steerPedal.put(e.getKey(), e.getValue());
            }
        }
        if (modeOnly.isEmpty() || steerPedal.isEmpty()) {
            Log.w(TAG, "Individual split incomplete (mode=" + modeOnly.size() + " steerPedal=" + steerPedal.size() + "); single TX77");
            return dispatchBundle(context, states, label);
        }
        final Context appContext = context;
        if (CommandStatusHub.get().submit(ReadBackTable.FEATURE_DRIVE_MODE, new CommandDispatcher.SendAction() {
            @Override // ru.big.town.anative.CommandDispatcher.SendAction
            public boolean send() {
                if (!OemVehicleStateTransport.sendBundle(appContext, modeOnly, "individual mode frame").accepted()) {
                    Log.w(TAG, "Individual mode frame rejected; fallback single TX77 (trace B)");
                    return OemVehicleStateTransport.sendBundle(appContext, states, "drive mode: individual (trace B)").accepted();
                }
                if (!OemVehicleStateTransport.sendBundle(appContext, steerPedal, "individual steer/pedal frame").accepted()) {
                    // ASC 785/959: второй кадр потерян — режим уже применён
                    Log.w(TAG, "Individual steer/pedal frame lost (ASC 785/959); mode frame kept");
                } else {
                    Log.i(TAG, "Individual split OK: mode + steer/pedal frames accepted");
                }
                return true;
            }
        })) {
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
