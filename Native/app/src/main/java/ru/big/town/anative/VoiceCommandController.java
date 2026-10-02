package ru.big.town.anative;

import android.car.user.CarUserManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import ru.big.town.common.SuspensionWidgetProtocol;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceCommandController {
    private final VoiceSessionGate gate = new VoiceSessionGate();
    private final Handler main = new Handler(Looper.getMainLooper());
    private final SetModesService service;

    VoiceCommandController(SetModesService setModesService) {
        this.service = setModesService;
    }

    void close() {
        this.gate.clear();
        this.main.removeCallbacksAndMessages(null);
    }

    void handle(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        final String string = bundle.getString("session", "");
        if (string.isEmpty()) {
            return;
        }
        String string2 = bundle.getString("op", "");
        if ("begin".equals(string2)) {
            this.gate.begin(string);
            return;
        }
        if ("cancel".equals(string2)) {
            this.gate.cancel(string);
            return;
        }
        if ("execute".equals(string2)) {
            int i = bundle.getInt("index", 0);
            int i2 = bundle.getInt("count", 1);
            if (i2 < 1 || i < 0 || i >= i2 || !this.gate.submit(string, i)) {
                return;
            }
            final String string3 = bundle.getString(CarUserManager.BUNDLE_PARAM_ACTION, "");
            final ResultReceiver resultReceiver = (ResultReceiver) bundle.getParcelable("reply");
            if (isNavigation(string3) && i != i2 - 1) {
                respond(resultReceiver, false, "Навигация только последней командой");
                return;
            }
            final long jElapsedRealtime = SystemClock.elapsedRealtime() + 7000;
            if (SeatCommand.handles(string3) || WindowCommand.handles(string3)) {
                final String[] strArr = {"Не удалось отправить команду автомобилю"};
                ApplyEngine.postIndependentUserCommand("voice " + string3, new Runnable() { // from class: ru.big.town.anative.VoiceCommandController$$ExternalSyntheticLambda7
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoiceCommandController.this.m2151lambda$handle$0$rubigtownanativeVoiceCommandController(strArr, string3, jElapsedRealtime);
                    }
                }, new Runnable() { // from class: ru.big.town.anative.VoiceCommandController$$ExternalSyntheticLambda8
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoiceCommandController.this.m2152lambda$handle$1$rubigtownanativeVoiceCommandController(string, resultReceiver, strArr);
                    }
                });
                return;
            }
            if (isNavigation(string3)) {
                final int iMax = Math.max(0, Math.min(PathInterpolatorCompat.MAX_NUM_POINTS, bundle.getInt("resultDisplayMs", 0)));
                if (string3.startsWith("app:") && this.service.getPackageManager().getLaunchIntentForPackage(string3.substring(4)) == null) {
                    respond(resultReceiver, false, "Не удалось открыть приложение");
                    return;
                }
                if (iMax > 0) {
                    respond(resultReceiver, true, null);
                }
                this.main.postDelayed(new Runnable() { // from class: ru.big.town.anative.VoiceCommandController$$ExternalSyntheticLambda9
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoiceCommandController.this.m2156lambda$handle$4$rubigtownanativeVoiceCommandController(string, jElapsedRealtime, string3, resultReceiver, iMax);
                    }
                }, ((long) iMax) + 350);
                return;
            }
            if (this.service.isVoiceServiceAction(string3)) {
                if (this.gate.active(string)) {
                    this.service.executeVoiceServiceAction(string3, resultReceiver);
                }
            } else {
                if (string3.equals("power_hold")) {
                    this.service.activateVoicePowerHold(new BooleanSupplier() { // from class: ru.big.town.anative.VoiceCommandController$$ExternalSyntheticLambda10
                        @Override // java.util.function.BooleanSupplier
                        public final boolean getAsBoolean() {
                            return VoiceCommandController.this.m2157lambda$handle$5$rubigtownanativeVoiceCommandController(string, jElapsedRealtime);
                        }
                    }, new Consumer() { // from class: ru.big.town.anative.VoiceCommandController$$ExternalSyntheticLambda11
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            VoiceCommandController.this.m2158lambda$handle$6$rubigtownanativeVoiceCommandController(string, resultReceiver, (PowerHoldPolicy.Outcome) obj);
                        }
                    });
                    return;
                }
                final AtomicBoolean atomicBoolean = new AtomicBoolean();
                final Bundle bundle2 = new Bundle();
                ManualAutoGate.Ticket ticketReserveManualHeadlightCommand = null;
                final String[] strArr2 = {"Автомобиль не принял команду"};
                if (string3.startsWith("headlights:") || string3.startsWith("toggle_headlights")) {
                    ticketReserveManualHeadlightCommand = LightSensorService.reserveManualHeadlightCommand();
                }
                Runnable runnable = new Runnable() { // from class: ru.big.town.anative.VoiceCommandController$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoiceCommandController.this.m2153lambda$handle$10$rubigtownanativeVoiceCommandController(string, jElapsedRealtime, atomicBoolean, string3, strArr2, bundle2);
                    }
                };
                final ManualAutoGate.Ticket ticket = ticketReserveManualHeadlightCommand;
                ApplyEngine.postUserCommand("voice " + string3, runnable, new Runnable() { // from class: ru.big.town.anative.VoiceCommandController$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoiceCommandController.this.m2154lambda$handle$11$rubigtownanativeVoiceCommandController(ticket, string, resultReceiver, atomicBoolean, strArr2, bundle2);
                    }
                });
            }
        }
    }

    /* JADX INFO: renamed from: lambda$handle$0$ru-big-town-anative-VoiceCommandController, reason: not valid java name */
    /* synthetic */ void m2151lambda$handle$0$rubigtownanativeVoiceCommandController(String[] strArr, String str, long j) {
        strArr[0] = OemCommandSender.send(str, j, VoiceOemTransport.get(this.service));
    }

    /* JADX INFO: renamed from: lambda$handle$1$ru-big-town-anative-VoiceCommandController, reason: not valid java name */
    /* synthetic */ void m2152lambda$handle$1$rubigtownanativeVoiceCommandController(String str, ResultReceiver resultReceiver, String[] strArr) {
        if (this.gate.active(str)) {
            String str2 = strArr[0];
            respond(resultReceiver, str2 == null, str2);
        }
    }

    /* JADX INFO: renamed from: lambda$handle$4$ru-big-town-anative-VoiceCommandController, reason: not valid java name */
    /* synthetic */ void m2156lambda$handle$4$rubigtownanativeVoiceCommandController(final String str, long j, String str2, final ResultReceiver resultReceiver, int i) {
        if (!this.gate.active(str) || SystemClock.elapsedRealtime() > j) {
            return;
        }
        try {
            if ("system_back".equals(str2)) {
                BackButtonService.performBack(this.service);
            } else if (str2.startsWith("app:")) {
                String strSubstring = str2.substring(4);
                ClusterMediaHostActivity.closeForPackage(strSubstring);
                SplitHostActivity.closeActiveHost();
                AppDisplayLauncher.launch(this.service, strSubstring, 0, FullscreenPackagePolicy.contains(Settings.Global.getString(this.service.getContentResolver(), "voyahtune_fullscreen_apps"), strSubstring), new BooleanSupplier() { // from class: ru.big.town.anative.VoiceCommandController$$ExternalSyntheticLambda0
                    @Override // java.util.function.BooleanSupplier
                    public final boolean getAsBoolean() {
                        return VoiceCommandController.this.m2155lambda$handle$2$rubigtownanativeVoiceCommandController(str);
                    }
                }, new Runnable() { // from class: ru.big.town.anative.VoiceCommandController$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoiceCommandController.respond(resultReceiver, false, "Не удалось открыть приложение");
                    }
                });
            } else {
                SetModesReceiverDynamic.handleSteerAction(this.service, str2, null);
            }
            if (i == 0) {
                respond(resultReceiver, true, null);
            }
        } catch (RuntimeException unused) {
            respond(resultReceiver, false, "Не удалось открыть приложение");
        }
    }

    /* JADX INFO: renamed from: lambda$handle$2$ru-big-town-anative-VoiceCommandController, reason: not valid java name */
    /* synthetic */ boolean m2155lambda$handle$2$rubigtownanativeVoiceCommandController(String str) {
        return this.gate.active(str);
    }

    /* JADX INFO: renamed from: lambda$handle$5$ru-big-town-anative-VoiceCommandController, reason: not valid java name */
    /* synthetic */ boolean m2157lambda$handle$5$rubigtownanativeVoiceCommandController(String str, long j) {
        return this.gate.active(str) && SystemClock.elapsedRealtime() <= j;
    }

    /* JADX INFO: renamed from: lambda$handle$6$ru-big-town-anative-VoiceCommandController, reason: not valid java name */
    /* synthetic */ void m2158lambda$handle$6$rubigtownanativeVoiceCommandController(String str, ResultReceiver resultReceiver, PowerHoldPolicy.Outcome outcome) {
        String str2;
        if (this.gate.active(str)) {
            if (outcome == PowerHoldPolicy.Outcome.NOT_IN_PARK) {
                str2 = "Для Power Hold переведите селектор в P";
            } else {
                str2 = outcome == PowerHoldPolicy.Outcome.LOW_BATTERY ? "Для Power Hold нужен заряд не ниже 15%" : "Автомобиль не принял команду Power Hold";
            }
            respond(resultReceiver, outcome == PowerHoldPolicy.Outcome.ACCEPTED, str2);
        }
    }

    /* JADX INFO: renamed from: lambda$handle$10$ru-big-town-anative-VoiceCommandController, reason: not valid java name */
    /* synthetic */ void m2153lambda$handle$10$rubigtownanativeVoiceCommandController(final String str, final long j, final AtomicBoolean atomicBoolean, final String str2, final String[] strArr, final Bundle bundle) {
        if (!this.gate.active(str) || SystemClock.elapsedRealtime() > j) {
            return;
        }
        try {
            CanSender.runGuardedAction(new BooleanSupplier() { // from class: ru.big.town.anative.VoiceCommandController$$ExternalSyntheticLambda5
                @Override // java.util.function.BooleanSupplier
                public final boolean getAsBoolean() {
                    return VoiceCommandController.this.m2159lambda$handle$7$rubigtownanativeVoiceCommandController(str, j);
                }
            }, new Runnable() { // from class: ru.big.town.anative.VoiceCommandController$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    VoiceCommandController.this.m2161lambda$handle$9$rubigtownanativeVoiceCommandController(atomicBoolean, str2, strArr, bundle, str, j);
                }
            });
        } catch (RuntimeException e) {
            Log.e("VoyahVoice", "Command failed: " + str2, e);
        }
    }

    /* JADX INFO: renamed from: lambda$handle$7$ru-big-town-anative-VoiceCommandController, reason: not valid java name */
    /* synthetic */ boolean m2159lambda$handle$7$rubigtownanativeVoiceCommandController(String str, long j) {
        return this.gate.active(str) && SystemClock.elapsedRealtime() <= j;
    }

    /* JADX INFO: renamed from: lambda$handle$9$ru-big-town-anative-VoiceCommandController, reason: not valid java name */
    /* synthetic */ void m2161lambda$handle$9$rubigtownanativeVoiceCommandController(AtomicBoolean atomicBoolean, String str, String[] strArr, Bundle bundle, final String str2, final long j) {
        atomicBoolean.set(execute(str, strArr, bundle, new BooleanSupplier() { // from class: ru.big.town.anative.VoiceCommandController$$ExternalSyntheticLambda4
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                return VoiceCommandController.this.m2160lambda$handle$8$rubigtownanativeVoiceCommandController(str2, j);
            }
        }));
    }

    /* JADX INFO: renamed from: lambda$handle$8$ru-big-town-anative-VoiceCommandController, reason: not valid java name */
    /* synthetic */ boolean m2160lambda$handle$8$rubigtownanativeVoiceCommandController(String str, long j) {
        return this.gate.active(str) && SystemClock.elapsedRealtime() <= j;
    }

    /* JADX INFO: renamed from: lambda$handle$11$ru-big-town-anative-VoiceCommandController, reason: not valid java name */
    /* synthetic */ void m2154lambda$handle$11$rubigtownanativeVoiceCommandController(ManualAutoGate.Ticket ticket, String str, ResultReceiver resultReceiver, AtomicBoolean atomicBoolean, String[] strArr, Bundle bundle) {
        if (ticket != null) {
            ticket.close();
        }
        if (this.gate.active(str)) {
            respond(resultReceiver, atomicBoolean.get(), strArr[0], bundle);
        }
    }

    private static boolean isNavigation(String str) {
        return "system_back".equals(str) || "open_voyahtune".equals(str) || str.startsWith("app:") || str.startsWith("split:") || str.startsWith("call:");
    }

    /* JADX WARN: Code duplicated, block: B:92:0x0149  */
    /* JADX WARN: Code duplicated, block: B:93:0x014b  */
    private boolean execute(String str, String[] strArr, Bundle bundle, BooleanSupplier booleanSupplier) {
        boolean zSendEnergyModeCommand;
        boolean zEquals;
        boolean zEndsWith = true;
        if (str.startsWith("port_cap:")) {
            PortCapController.OpenResult openResultOpen = PortCapController.open(this.service, str);
            PortCapController.Outcome outcome = openResultOpen.outcome;
            if (outcome == PortCapController.Outcome.ACCEPTED && openResultOpen.refillLiters != null) {
                bundle.putInt("fuelRefillLiters", openResultOpen.refillLiters.intValue());
            }
            if (outcome == PortCapController.Outcome.NOT_IN_PARK) {
                strArr[0] = "Для открытия лючка переведите селектор в P";
            } else if (outcome == PortCapController.Outcome.STATE_UNAVAILABLE) {
                strArr[0] = "Не удалось проверить положение селектора";
            } else if (outcome == PortCapController.Outcome.TRANSPORT_FAILURE) {
                strArr[0] = "Не удалось отправить команду открытия лючка";
            }
            return outcome == PortCapController.Outcome.ACCEPTED;
        }
        if (str.startsWith("fuel_charge:")) {
            try {
                int i = Integer.parseInt(str.substring("fuel_charge:".length()));
                VehicleRestorePolicy.requireSaveChargeLevel(i);
                SaveChargeSequence.Result resultApply = SaveChargeController.apply(this.service, i, booleanSupplier);
                zEndsWith = resultApply.outcome == SaveChargeSequence.Outcome.CONFIRMED;
                if (!zEndsWith) {
                    strArr[0] = SaveChargeController.error(resultApply, i);
                }
                bundle.putBoolean("chargeTargetConfirmed", zEndsWith);
                return zEndsWith;
            } catch (IllegalArgumentException unused) {
                strArr[0] = "Некорректный уровень поддержания заряда";
                return false;
            }
        }
        if (str.startsWith("drive:") || str.startsWith("energy:") || str.startsWith("recycle:")) {
            int iIndexOf = str.indexOf(58);
            String strSubstring = str.substring(0, iIndexOf);
            String str2 = SuspensionWidgetProtocol.DRIVE.equals(strSubstring) ? "driveMode" : strSubstring;
            String strNextMode = SteeringActionPolicy.nextMode(str.substring(iIndexOf + 1), MainActivity.currentVehicleMode(this.service, str2));
            if (strNextMode == null) {
                return false;
            }
            if (SuspensionWidgetProtocol.DRIVE.equals(strSubstring)) {
                zSendEnergyModeCommand = MainActivity.sendDriveModeCommand(this.service, strNextMode);
            } else {
                zSendEnergyModeCommand = "energy".equals(strSubstring) ? MainActivity.sendEnergyModeCommand(this.service, strNextMode) : MainActivity.sendRecuperationModeCommand(this.service, strNextMode);
            }
            if (zSendEnergyModeCommand) {
                ApplyEngine.noteVehicleMode(str2, strNextMode);
                MainActivity.persistSavedMode(this.service, str2, strNextMode);
            }
            return zSendEnergyModeCommand;
        }
        if ("suspension_maintenance:on".equals(str) || "suspension_maintenance:off".equals(str)) {
            boolean zEndsWith2 = str.endsWith(":on");
            boolean zSendSuspensionMaintenanceCommand = MainActivity.sendSuspensionMaintenanceCommand(this.service, zEndsWith2);
            if (zSendSuspensionMaintenanceCommand) {
                MainActivity.persistSavedToggle(this.service, "suspensionMaintenance", zEndsWith2);
            }
            return zSendSuspensionMaintenanceCommand;
        }
        if (str.startsWith("forced_ev:") || "toggle_forced_ev".equals(str)) {
            if (!"toggle_forced_ev".equals(str)) {
                zEndsWith = str.endsWith(":on");
            } else if (MainActivity.currentSavedToggle(this.service, "forcedEv")) {
                zEndsWith = false;
            }
            boolean zSendForcedEvCommand = MainActivity.sendForcedEvCommand(zEndsWith);
            if (zSendForcedEvCommand) {
                MainActivity.persistSavedToggle(this.service, "forcedEv", zEndsWith);
            }
            return zSendForcedEvCommand;
        }
        if (str.startsWith("pedestrian:") || "toggle_pedestrian_sound".equals(str)) {
            if (!"toggle_pedestrian_sound".equals(str)) {
                zEndsWith = str.endsWith(":off");
            } else if (MainActivity.currentSavedToggle(this.service, "disablePedestrianSound")) {
                zEndsWith = false;
            }
            boolean zSendPedestrianSoundCommand = MainActivity.sendPedestrianSoundCommand(zEndsWith);
            if (zSendPedestrianSoundCommand) {
                MainActivity.persistSavedToggle(this.service, "disablePedestrianSound", zEndsWith);
            }
            return zSendPedestrianSoundCommand;
        }
        if (str.startsWith("headlights:") || str.startsWith("toggle_headlights")) {
            SharedPreferences sharedPreferences = this.service.getSharedPreferences("NativePrefs", 0);
            boolean z = str.equals("headlights:auto") || str.equals("toggle_headlights_auto");
            if (str.equals("toggle_headlights")) {
                if (sharedPreferences.getBoolean("steerHeadlightsOn", false)) {
                    zEquals = false;
                } else {
                    zEquals = true;
                }
            } else if (!str.equals("toggle_headlights_auto")) {
                zEquals = str.equals("headlights:on");
            } else if (sharedPreferences.getBoolean("steerHeadlightsAutoLowBeam", false)) {
                zEquals = false;
            } else {
                zEquals = true;
            }
            boolean manualAutoOverride = LightSensorService.setManualAutoOverride(z && !zEquals);
            SetModesService setModesService = this.service;
            boolean headlightsAutoLow = z ? MainActivity.setHeadlightsAutoLow(setModesService, zEquals) : MainActivity.setHeadlights(setModesService, zEquals);
            if (!headlightsAutoLow) {
                LightSensorService.setManualAutoOverride(manualAutoOverride);
                return headlightsAutoLow;
            }
            sharedPreferences.edit().putBoolean("steerHeadlightsOn", zEquals).putBoolean("steerHeadlightsAutoLowBeam", zEquals).apply();
            return headlightsAutoLow;
        }
        if (str.equals("wash")) {
            WashModePolicy.Outcome outcomeActivateVoiceWash = this.service.activateVoiceWash();
            if (outcomeActivateVoiceWash == WashModePolicy.Outcome.NOT_IN_PARK) {
                strArr[0] = "Для режима мойки переведите селектор в P";
            }
            return outcomeActivateVoiceWash == WashModePolicy.Outcome.ACCEPTED;
        }
        if (str.startsWith("can:")) {
            byte[] customCan = SteeringActionSequence.parseCustomCan(str);
            return customCan != null && MainActivity.setCanValues(1, new byte[][]{customCan}, "voice saved CAN");
        }
        strArr[0] = "Неизвестное действие";
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void respond(ResultReceiver resultReceiver, boolean z, String str) {
        respond(resultReceiver, z, str, new Bundle());
    }

    private static void respond(ResultReceiver resultReceiver, boolean z, String str, Bundle bundle) {
        if (resultReceiver == null) {
            return;
        }
        bundle.putString("error", str);
        resultReceiver.send(z ? 1 : 0, bundle);
    }
}
