package ru.big.town.restoremode;

import android.content.SharedPreferences;
import android.os.Bundle;
import ru.big.town.common.DriveSelectionPolicy;
import ru.big.town.common.SuspensionWidgetProtocol;

/* JADX INFO: loaded from: classes2.dex */
final class DriveSelectionPreferences {
    private static final String ACC = "driveAcc";
    private static final String BOOT = "driveBoot";
    private static final String CYCLE = "driveCycle";
    private static final String REV = "driveRevision";
    private static final String SETTINGS_START = "settingsStartup";
    private static final String START = "driveStartup";

    DriveSelectionPreferences() {
    }

    static synchronized DriveSelectionPolicy read(SharedPreferences sharedPreferences) {
        return new DriveSelectionPolicy(sharedPreferences.getString("driveMode", "INDIVIDUAL"), sharedPreferences.getString(DriveSelectionPolicy.OVERRIDE, ""), sharedPreferences.getString(DriveSelectionPolicy.MEDIUM, null), sharedPreferences.getString(DriveSelectionPolicy.CURRENT, ""));
    }

    static synchronized boolean select(SharedPreferences sharedPreferences, String str, String str2) {
        DriveSelectionPolicy driveSelectionPolicy = read(sharedPreferences);
        DriveSelectionPolicy driveSelectionPolicySelect = driveSelectionPolicy.select(str, str2, sharedPreferences.getBoolean("driveRememberLast", true));
        if (driveSelectionPolicySelect == driveSelectionPolicy) {
            return false;
        }
        return sharedPreferences.edit().putString("driveMode", driveSelectionPolicySelect.configured).putString(DriveSelectionPolicy.OVERRIDE, driveSelectionPolicySelect.override).putString(DriveSelectionPolicy.MEDIUM, driveSelectionPolicySelect.medium).putString(DriveSelectionPolicy.CURRENT, driveSelectionPolicySelect.current).putString(START, "selected").putLong(REV, sharedPreferences.getLong(REV, 0L) + 1).commit();
    }

    static synchronized boolean selectEnergy(SharedPreferences sharedPreferences, String str, boolean z) {
        if (!validEnergy(str)) {
            return false;
        }
        SharedPreferences.Editor editorPutLong = sharedPreferences.edit().putString("currentTripEnergy", str).putBoolean("forcedEv", "FORCE_EV".equals(str)).putString(START, "selected").putLong(REV, sharedPreferences.getLong(REV, 0L) + 1);
        if (!"FORCE_EV".equals(str) && (z || sharedPreferences.getBoolean("energyRememberLast", true))) {
            editorPutLong.putString("energy", str);
        }
        return editorPutLong.commit();
    }

    static boolean validEnergy(String str) {
        return "SMART".equals(str) || "EV".equals(str) || "REV".equals(str) || "SREV".equals(str) || "FORCE_EV".equals(str);
    }

    static synchronized String energy(SharedPreferences sharedPreferences) {
        String string;
        string = sharedPreferences.getString("currentTripEnergy", "");
        if (!validEnergy(string)) {
            string = sharedPreferences.getString("energy", "SREV");
        }
        return string;
    }

    static synchronized Bundle hook(SharedPreferences sharedPreferences, String str, Bundle bundle, int i) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        boolean z = true;
        if ("acc".equals(str)) {
            int i2 = bundle.getInt("acc", -1);
            if (i2 == 0 || i2 == 2) {
                int i3 = sharedPreferences.getInt(ACC, -1);
                boolean z2 = sharedPreferences.getInt(BOOT, -1) != i;
                boolean z3 = (z2 || i3 == 0) && i2 == 2;
                SharedPreferences.Editor editorPutInt = sharedPreferences.edit().putInt(BOOT, i).putInt(ACC, i2);
                if (z2 || z3) {
                    if (sharedPreferences.getInt(BOOT, -1) != -1 || i3 != -1) {
                        z = false;
                    }
                    editorPutInt.putLong(CYCLE, sharedPreferences.getLong(CYCLE, 0L) + 1).putLong(REV, sharedPreferences.getLong(REV, 0L) + 1);
                    editorPutInt.putString(SETTINGS_START, SuspensionWidgetProtocol.PENDING);
                    if (!z) {
                        editorPutInt.putString(DriveSelectionPolicy.CURRENT, "").putString("currentTripEnergy", "").putString(START, SuspensionWidgetProtocol.PENDING);
                    }
                }
                if (!editorPutInt.commit()) {
                    throw new IllegalStateException("ACC state not persisted");
                }
            }
        } else {
            if ("claimSettings".equals(str)) {
                if (!SuspensionWidgetProtocol.PENDING.equals(sharedPreferences.getString(SETTINGS_START, "idle")) || sharedPreferences.getInt(ACC, -1) != 2) {
                    z = false;
                }
                if (z && !sharedPreferences.edit().putString(SETTINGS_START, "claimed").commit()) {
                    throw new IllegalStateException("Settings claim not persisted");
                }
                Bundle bundleSnapshot = snapshot(sharedPreferences);
                bundleSnapshot.putBoolean("claimed", z);
                return bundleSnapshot;
            }
            if ("completeSettings".equals(str)) {
                if (bundle.getLong("cycle", -1L) == sharedPreferences.getLong(CYCLE, 0L) && "claimed".equals(sharedPreferences.getString(SETTINGS_START, "idle"))) {
                    if (!sharedPreferences.edit().putString(SETTINGS_START, bundle.getBoolean("accepted") ? "submitted" : "uncertain").commit()) {
                        throw new IllegalStateException("Settings result not persisted");
                    }
                }
            } else if ("manual".equals(str)) {
                if (sharedPreferences.getBoolean("driveEnabled", false)) {
                    select(sharedPreferences, sharedPreferences.getString("driveMode", "INDIVIDUAL"), DriveSelectionPolicy.SETTINGS);
                }
                if (sharedPreferences.getBoolean("energyEnabled", false) || sharedPreferences.getBoolean("forcedEv", false)) {
                    selectEnergy(sharedPreferences, sharedPreferences.getBoolean("forcedEv", false) ? "FORCE_EV" : sharedPreferences.getString("energy", "SREV"), true);
                }
            } else if ("user".equals(str)) {
                if (bundle.containsKey("mode") && !select(sharedPreferences, bundle.getString("mode"), DriveSelectionPolicy.EXPLICIT)) {
                    throw new IllegalArgumentException("Invalid user drive mode");
                }
                if (bundle.containsKey("energy") && !selectEnergy(sharedPreferences, bundle.getString("energy"), false)) {
                    throw new IllegalArgumentException("Invalid user energy mode");
                }
            } else {
                if ("claim".equals(str)) {
                    if (!SuspensionWidgetProtocol.PENDING.equals(sharedPreferences.getString(START, SuspensionWidgetProtocol.PENDING)) || sharedPreferences.getInt(ACC, -1) != 2 || bundle.getLong("revision", -1L) != sharedPreferences.getLong(REV, 0L) || (!sharedPreferences.getBoolean("driveEnabled", false) && !sharedPreferences.getBoolean("energyEnabled", false) && !sharedPreferences.getBoolean("forcedEv", false))) {
                        z = false;
                    }
                    if (z && !sharedPreferences.edit().putString(START, "claimed").commit()) {
                        throw new IllegalStateException("Startup claim not persisted");
                    }
                    Bundle bundleSnapshot2 = snapshot(sharedPreferences);
                    bundleSnapshot2.putBoolean("claimed", z);
                    return bundleSnapshot2;
                }
                if ("complete".equals(str)) {
                    if (bundle.getLong("revision", -1L) == sharedPreferences.getLong(REV, 0L) && "claimed".equals(sharedPreferences.getString(START, SuspensionWidgetProtocol.PENDING))) {
                        if (!sharedPreferences.edit().putString(START, bundle.getBoolean("accepted") ? "submitted" : "uncertain").commit()) {
                            throw new IllegalStateException("Startup result not persisted");
                        }
                    }
                } else if (!"snapshot".equals(str)) {
                    throw new IllegalArgumentException("Unknown drive hook operation");
                }
            }
        }
        return snapshot(sharedPreferences);
    }

    private static Bundle snapshot(SharedPreferences sharedPreferences) {
        Bundle bundle = new Bundle();
        bundle.putInt("protocol", 2);
        bundle.putString("mode", read(sharedPreferences).effective());
        bundle.putString("energy", energy(sharedPreferences));
        bundle.putString("configuredEnergy", sharedPreferences.getString("energy", "SREV"));
        bundle.putLong("revision", sharedPreferences.getLong(REV, 0L));
        bundle.putLong("cycle", sharedPreferences.getLong(CYCLE, 0L));
        bundle.putInt("acc", sharedPreferences.getInt(ACC, -1));
        bundle.putString("startup", sharedPreferences.getString(START, SuspensionWidgetProtocol.PENDING));
        bundle.putString(SETTINGS_START, sharedPreferences.getString(SETTINGS_START, "idle"));
        bundle.putBoolean("enabled", sharedPreferences.getBoolean("driveEnabled", false));
        return bundle;
    }
}
