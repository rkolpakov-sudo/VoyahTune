package ru.big.town.anative;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.BooleanSupplier;

/* JADX INFO: loaded from: classes2.dex */
final class SaveChargeController {
    private static final OemVehicleStateTransport.StateKey MODE = new OemVehicleStateTransport.StateKey("IVI_SOC_MODESET", 957);
    private static final OemVehicleStateTransport.StateKey LEVEL = new OemVehicleStateTransport.StateKey("SREV_SOC_SET", 1196);

    SaveChargeController() {
    }

    static SaveChargeSequence.Result apply(final Context context, final int i, final BooleanSupplier booleanSupplier) {
        SaveChargeSequence.Result resultRun = SaveChargeSequence.run(i, new SaveChargeSequence.Vehicle() { // from class: ru.big.town.anative.SaveChargeController.1
            @Override // ru.big.town.anative.SaveChargeSequence.Vehicle
            public SaveChargeSequence.State read() {
                Map<OemVehicleStateTransport.StateKey, Integer> vehicleStates = OemVehicleStateTransport.readVehicleStates(context, Arrays.asList(SaveChargeController.MODE, SaveChargeController.LEVEL));
                if (vehicleStates == null) {
                    return null;
                }
                return new SaveChargeSequence.State(vehicleStates.get(SaveChargeController.MODE).intValue(), vehicleStates.get(SaveChargeController.LEVEL).intValue());
            }

            @Override // ru.big.town.anative.SaveChargeSequence.Vehicle
            public boolean selectSrev() {
                return booleanSupplier.getAsBoolean() && OemVehicleStateTransport.sendBundle(context, Collections.singletonMap(SaveChargeController.MODE, 4), "voice select SREV before target").accepted();
            }

            @Override // ru.big.town.anative.SaveChargeSequence.Vehicle
            public boolean setLevel(int i2) {
                return booleanSupplier.getAsBoolean() && OemVehicleStateTransport.sendVehicleState(context, SaveChargeController.LEVEL, i2, new StringBuilder("voice SREV target: ").append(i).append("%").toString()).accepted();
            }

            @Override // ru.big.town.anative.SaveChargeSequence.Vehicle
            public void modeConfirmed() {
                MainActivity.persistSavedToggle(context, "forcedEv", false);
                ApplyEngine.noteVehicleMode("energy", "SREV");
                MainActivity.persistSavedMode(context, "energy", "SREV");
            }
        }, new SaveChargeSequence.Clock() { // from class: ru.big.town.anative.SaveChargeController.2
            @Override // ru.big.town.anative.SaveChargeSequence.Clock
            public long now() {
                return SystemClock.elapsedRealtime();
            }

            @Override // ru.big.town.anative.SaveChargeSequence.Clock
            public void sleep(long j) throws InterruptedException {
                Thread.sleep(j);
            }
        }, booleanSupplier);
        Log.i("VoyahVoice", "SREV target " + i + "%: " + resultRun.outcome + ", modeConfirmed=" + resultRun.modeConfirmed + ", feedback=" + (resultRun.observed == null ? "unavailable" : resultRun.observed.mode + "/" + resultRun.observed.level));
        return resultRun;
    }

    static String error(SaveChargeSequence.Result result, int i) {
        if (result.outcome == SaveChargeSequence.Outcome.CANCELLED) {
            return "Команда отменена или истекло время ожидания";
        }
        if (!result.modeConfirmed) {
            return "Не удалось подтвердить режим «Топливо». Уровень заряда не изменён";
        }
        if (result.outcome == SaveChargeSequence.Outcome.MODE_UNCONFIRMED) {
            return "Режим «Топливо» изменился во время установки заряда";
        }
        return "Не подтверждён уровень поддержания заряда " + i + "%." + ((result.observed == null || result.observed.level < 0 || result.observed.level > 11) ? " Обратное значение недоступно." : " Сейчас: " + ((result.observed.level * 5) + 25) + "%.");
    }
}
