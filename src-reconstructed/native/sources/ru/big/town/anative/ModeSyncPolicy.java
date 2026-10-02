package ru.big.town.anative;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
final class ModeSyncPolicy {
    private String expectedDrive;
    private boolean feedbackOpen;
    private long generation;
    private boolean wakeActive;
    private final Map<String, String> currentModes = new HashMap();
    private boolean driveRememberLast = true;
    private boolean energyRememberLast = true;
    private boolean recycleRememberLast = true;

    enum Decision {
        ACCEPT,
        IGNORE
    }

    ModeSyncPolicy() {
    }

    synchronized void activateWake() {
        this.wakeActive = true;
    }

    synchronized boolean canRememberSelection() {
        return this.wakeActive && this.feedbackOpen;
    }

    synchronized boolean canRememberSelection(boolean z) {
        boolean z2;
        if (!z) {
            z2 = canRememberSelection();
        }
        return z2;
    }

    synchronized boolean reconcileCompletedAcc(int i, String str) {
        if (this.generation == 0 && this.wakeActive && i == 2 && "submitted".equals(str)) {
            this.feedbackOpen = true;
            return true;
        }
        return false;
    }

    synchronized boolean canAcceptDriveSelection() {
        return this.wakeActive && this.feedbackOpen;
    }

    synchronized long beginRestore() {
        long j;
        this.wakeActive = true;
        this.feedbackOpen = false;
        j = this.generation + 1;
        this.generation = j;
        return j;
    }

    synchronized long freeze() {
        long j;
        this.wakeActive = false;
        this.feedbackOpen = false;
        this.currentModes.clear();
        j = this.generation + 1;
        this.generation = j;
        return j;
    }

    synchronized long cancelRestore() {
        long j;
        this.feedbackOpen = false;
        j = this.generation + 1;
        this.generation = j;
        return j;
    }

    synchronized boolean completeUserCommand(long j) {
        return completeRestore(j);
    }

    synchronized boolean completeRestore(long j) {
        if (j == this.generation && this.wakeActive) {
            this.feedbackOpen = true;
            return true;
        }
        return false;
    }

    synchronized boolean failRestore(long j) {
        if (j != this.generation) {
            return false;
        }
        this.feedbackOpen = false;
        return true;
    }

    synchronized long currentGeneration() {
        return this.generation;
    }

    synchronized boolean canPersist(long j, String str) {
        return j == this.generation && canRememberSelection() && acceptsExternalFeedback(str);
    }

    synchronized void updateExpected(String str, String str2, String str3, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) {
        if (valid(str)) {
            this.expectedDrive = str;
        }
        this.driveRememberLast = z4;
        this.energyRememberLast = z5;
        this.recycleRememberLast = z6;
    }

    synchronized void updateExpectedMode(String str, String str2) {
        if ("driveMode".equals(str) && valid(str2)) {
            this.expectedDrive = str2;
        }
    }

    synchronized void updateRememberLast(String str, boolean z) {
        if ("driveMode".equals(str)) {
            this.driveRememberLast = z;
        } else if ("energy".equals(str)) {
            this.energyRememberLast = z;
        } else if ("recycle".equals(str)) {
            this.recycleRememberLast = z;
        }
    }

    synchronized void observe(String str, String str2) {
        if (knownModeKey(str) && valid(str2)) {
            this.currentModes.put(str, str2);
        }
    }

    synchronized String currentMode(String str, String str2) {
        return this.currentModes.getOrDefault(str, str2);
    }

    synchronized Decision evaluate(String str, String str2) {
        if (knownModeKey(str) && valid(str2)) {
            observe(str, str2);
            return (canRememberSelection() && acceptsExternalFeedback(str)) ? Decision.ACCEPT : Decision.IGNORE;
        }
        return Decision.IGNORE;
    }

    private boolean acceptsExternalFeedback(String str) {
        if ("energy".equals(str)) {
            return this.energyRememberLast;
        }
        if ("driveMode".equals(str)) {
            return this.driveRememberLast;
        }
        return "recycle".equals(str) && this.recycleRememberLast && !"SNOW".equals(currentMode("driveMode", this.expectedDrive));
    }

    private static boolean knownModeKey(String str) {
        return "driveMode".equals(str) || "energy".equals(str) || "recycle".equals(str);
    }

    private static boolean valid(String str) {
        return (str == null || str.isEmpty()) ? false : true;
    }
}
