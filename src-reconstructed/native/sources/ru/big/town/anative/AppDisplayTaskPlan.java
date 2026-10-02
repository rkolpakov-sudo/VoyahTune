package ru.big.town.anative;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/* JADX INFO: loaded from: classes2.dex */
final class AppDisplayTaskPlan {
    private AppDisplayTaskPlan() {
    }

    static Set<Integer> retiring(List<SplitHostTaskSnapshot.TaskRecord> list, String str, int i) {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (SplitHostTaskSnapshot.TaskRecord taskRecord : list) {
            if (str.equals(taskRecord.packageName)) {
                if (taskRecord.displayId == null) {
                    throw new IllegalStateException("Unknown display for task " + taskRecord.taskId);
                }
                if (taskRecord.displayId.intValue() != i) {
                    linkedHashSet.add(Integer.valueOf(taskRecord.taskId));
                }
            }
        }
        return linkedHashSet;
    }
}
