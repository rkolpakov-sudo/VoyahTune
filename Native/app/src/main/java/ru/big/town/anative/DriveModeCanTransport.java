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

    private static DispatchResult dispatchSplitIndividual(Context context, Map<OemVehicleStateTransport.StateKey, Integer> states, String label) {
        // Трасса A: две TX77 в порядке OEM — режим, затем руль/педаль
        Log.i(TAG, "Individual split: sending mode frame first");
        final boolean[] firstAccepted = {false};
        // First TX77: mode (DRIVING_MODE_SET)
        if (!CommandStatusHub.get().submit(ReadBackTable.FEATURE_DRIVE_MODE, new CommandDispatcher.SendAction() {
            @Override
            public boolean send() {
                // Filter to mode-only keys
                java.util.Map<OemVehicleStateTransport.StateKey, Integer> modeOnly = new java.util.LinkedHashMap();
                for (java.util.Map.Entry<OemVehicleStateTransport.StateKey, Integer> e : states.entrySet()) {
                    if (e.getKey().name.contains("DRIVING_MODE") || e.getKey().name.contains("EPS_MODE")) {
                        modeOnly.put(e.getKey(), e.getValue());
                    }
                }
                firstAccepted[0] = OemVehicleStateTransport.sendBundle(context, modeOnly, "individual mode: " + label).accepted();
                return firstAccepted[0];
            }
        })) {
            Log.w(TAG, "Individual mode frame failed; fallback to single bundle");
            return dispatchBundle(context, states, label + " (fallback)");
        }
        // Second TX77: steering/pedal (PROP_MODE_SET)
        final boolean[] secondAccepted = {false};
        if (!CommandStatusHub.get().submit(ReadBackTable.FEATURE_DRIVE_MODE, new CommandDispatcher.SendAction() {
            @Override
            public boolean send() {
                java.util.Map<OemVehicleStateTransport.StateKey, Integer> pedalOnly = new java.util.LinkedHashMap();
                for (java.util.Map.Entry<OemVehicleStateTransport.StateKey, Integer> e : states.entrySet()) {
                    if (e.getKey().name.contains("PROP_MODE")) {
                        pedalOnly.put(e.getKey(), e.getValue());
                    }
                }
                secondAccepted[0] = OemVehicleStateTransport.sendBundle(context, pedalOnly, "individual pedal: " + label).accepted();
                return secondAccepted[0];
            }
        })) {
            Log.w(TAG, "Individual pedal frame failed; second TX77 lost (ASC 785/959)");
            // Трасса B fallback: first уже принят, логируем ошибку
            return DispatchResult.ACCEPTED_UNCONFIRMED;
        }
        Log.i(TAG, "Individual split OK: mode + pedal both accepted");
        return DispatchResult.ACCEPTED_UNCONFIRMED;
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
