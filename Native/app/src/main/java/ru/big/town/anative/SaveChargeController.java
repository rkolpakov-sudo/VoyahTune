package ru.big.town.anative;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.BooleanSupplier;

/* JADX INFO: loaded from: classes2.dex */
final class SaveChargeController {
    private static final OemVehicleStateTransport.StateKey MODE = new OemVehicleStateTransport.StateKey("IVI_SOC_MODESET", 957);
    private static final OemVehicleStateTransport.StateKey LEVEL = new OemVehicleStateTransport.StateKey("SREV_SOC_SET", 1196);
    private static final String ACCOUNT_INFO_PATH = "/private/configs/token/accountInfo";

    SaveChargeController() {
    }

    private static String readCurrentAccountId() {
        try {
            File file = new File(ACCOUNT_INFO_PATH);
            if (!file.isFile()) return null;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            reader.close();
            if (line != null && !line.isEmpty() && !"guest".equals(line)) {
                return line.trim();
            }
        } catch (IOException e) {
            Log.w("SettingsRepository", "Cannot read account info", e);
        }
        return null;
    }

    static SaveChargeSequence.Result apply(final Context context, final int i, final BooleanSupplier booleanSupplier) {
        final String accountUid = readCurrentAccountId();
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
                if (!booleanSupplier.getAsBoolean()) return false;
                final boolean[] written = {false};
                SettingsRepository.guardedWrite(context, "energy", accountUid,
                    new Runnable() {
                        @Override public void run() {
                            written[0] = OemVehicleStateTransport.sendBundle(context, Collections.singletonMap(SaveChargeController.MODE, 4), "guard: select SREV").accepted();
                        }
                    },
                    new Runnable() {
                        @Override public void run() {
                            Map<OemVehicleStateTransport.StateKey, Integer> states = OemVehicleStateTransport.readVehicleStates(context, Arrays.asList(SaveChargeController.MODE, SaveChargeController.LEVEL));
                            if (states == null || !Integer.valueOf(4).equals(states.get(SaveChargeController.MODE))) {
                                throw new RuntimeException("SREV mode not confirmed after guarded write");
                            }
                        }
                    }
                );
                return written[0];
            }

            @Override // ru.big.town.anative.SaveChargeSequence.Vehicle
            public boolean setLevel(int i2) {
                if (!booleanSupplier.getAsBoolean()) return false;
                final boolean[] written = {false};
                SettingsRepository.guardedWrite(context, "energy", accountUid,
                    new Runnable() {
                        @Override public void run() {
                            written[0] = OemVehicleStateTransport.sendVehicleState(context, SaveChargeController.LEVEL, i2, "guard: set SREV level").accepted();
                        }
                    },
                    new Runnable() {
                        @Override public void run() {
                            Map<OemVehicleStateTransport.StateKey, Integer> states = OemVehicleStateTransport.readVehicleStates(context, Arrays.asList(SaveChargeController.MODE, SaveChargeController.LEVEL));
                            if (states == null || !Integer.valueOf(i2).equals(states.get(SaveChargeController.LEVEL))) {
                                throw new RuntimeException("SREV level not confirmed after guarded write");
                            }
                        }
                    }
                );
                return written[0];
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
