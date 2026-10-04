package ru.big.town.anative;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes2.dex */
final class SplitHostTaskSnapshot {
    private final boolean known;
    private final Map<String, PackageState> packages = new HashMap();
    private final List<TaskRecord> tasks;

    static final class TaskRecord {
        final Integer displayId;
        final String packageName;
        final int taskId;

        TaskRecord(int i, String str, Integer num) {
            this.taskId = i;
            this.packageName = str;
            this.displayId = num;
        }
    }

    private static final class PackageState {
        final Set<Integer> displayIds;
        boolean hasUnknownDisplay;

        private PackageState() {
            this.displayIds = new HashSet();
        }
    }

    private SplitHostTaskSnapshot(boolean z, List<TaskRecord> list) {
        this.known = z;
        this.tasks = Collections.unmodifiableList(new ArrayList(list));
        if (z) {
            for (TaskRecord taskRecord : list) {
                if (taskRecord != null && taskRecord.packageName != null && !taskRecord.packageName.isEmpty()) {
                    PackageState packageState = this.packages.get(taskRecord.packageName);
                    if (packageState == null) {
                        packageState = new PackageState();
                        this.packages.put(taskRecord.packageName, packageState);
                    }
                    if (taskRecord.displayId == null) {
                        packageState.hasUnknownDisplay = true;
                    } else {
                        packageState.displayIds.add(taskRecord.displayId);
                    }
                }
            }
        }
    }

    static SplitHostTaskSnapshot unknown() {
        return new SplitHostTaskSnapshot(false, Collections.emptyList());
    }

    static SplitHostTaskSnapshot known(List<TaskRecord> list) {
        if (list == null) {
            list = Collections.emptyList();
        }
        return new SplitHostTaskSnapshot(true, list);
    }

    boolean isAlive(String str, int i) {
        if (this.known && str != null && !str.isEmpty()) {
            PackageState packageState = this.packages.get(str);
            if (packageState == null) {
                return false;
            }
            if (!packageState.hasUnknownDisplay && !packageState.displayIds.contains(Integer.valueOf(i))) {
                return false;
            }
        }
        return true;
    }

    List<TaskRecord> tasks() {
        return this.tasks;
    }
}
