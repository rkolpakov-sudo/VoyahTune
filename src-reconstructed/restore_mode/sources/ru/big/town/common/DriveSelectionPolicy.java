package ru.big.town.common;

/* JADX INFO: loaded from: classes2.dex */
public final class DriveSelectionPolicy {
    public static final String CONFIGURED = "configuredDriveMode";
    public static final String CURRENT = "currentTripDrive";
    public static final String EXPLICIT = "explicit";
    public static final String FEEDBACK = "feedback";
    public static final String MEDIUM = "lastMediumDrive";
    public static final String MODE = "driveSelectionMode";
    public static final String OVERRIDE = "suspensionDriveOverride";
    public static final String SETTINGS = "settings";
    public static final String SOURCE = "driveSelectionSource";
    public static final String WIDGET = "widget";
    public final String configured;
    public final String current;
    public final String medium;
    public final String override;

    public DriveSelectionPolicy(String str, String str2, String str3) {
        this(str, str2, str3, "");
    }

    public DriveSelectionPolicy(String str, String str2, String str3, String str4) {
        str = valid(str) ? str : "INDIVIDUAL";
        this.configured = str;
        this.override = valid(str2) ? str2 : "";
        if (!isMedium(str3)) {
            str3 = isMedium(str) ? str : "ECO";
        }
        this.medium = str3;
        this.current = valid(str4) ? str4 : "";
    }

    public String effective() {
        if (this.current.isEmpty()) {
            return this.override.isEmpty() ? this.configured : this.override;
        }
        return this.current;
    }

    public DriveSelectionPolicy nextTrip() {
        return new DriveSelectionPolicy(this.configured, this.override, this.medium);
    }

    public DriveSelectionPolicy select(String str, String str2, boolean z) {
        if (valid(str) && ("widget".equals(str2) || EXPLICIT.equals(str2) || SETTINGS.equals(str2))) {
            return new DriveSelectionPolicy((z || SETTINGS.equals(str2)) ? str : this.configured, "widget".equals(str2) ? str : "", isMedium(str) ? str : this.medium, str);
        }
        return this;
    }

    public static boolean valid(String str) {
        return isMedium(str) || "SPORT".equals(str) || "OUTING".equals(str);
    }

    public static boolean isMedium(String str) {
        return "ECO".equals(str) || "COMFORT".equals(str) || "SNOW".equals(str) || "INDIVIDUAL".equals(str);
    }

    public static int value(String str) {
        if ("ECO".equals(str)) {
            return 1;
        }
        if ("COMFORT".equals(str)) {
            return 2;
        }
        if ("SPORT".equals(str)) {
            return 3;
        }
        if ("OUTING".equals(str)) {
            return 4;
        }
        if ("INDIVIDUAL".equals(str)) {
            return 5;
        }
        return "SNOW".equals(str) ? 6 : -1;
    }
}
