package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class ClusterMediaSession {
    private long generation;
    private String packageName;

    ClusterMediaSession() {
    }

    synchronized long begin(String str) {
        long j;
        this.packageName = str;
        j = this.generation + 1;
        this.generation = j;
        return j;
    }

    synchronized boolean owns(long j, String str) {
        return j == this.generation && str != null && str.equals(this.packageName);
    }

    synchronized boolean cancelPackage(String str) {
        if (str != null) {
            if (str.equals(this.packageName)) {
                this.packageName = null;
                this.generation++;
                return true;
            }
        }
        return false;
    }

    synchronized void end(long j) {
        long j2 = this.generation;
        if (j2 == j) {
            this.packageName = null;
            this.generation = j2 + 1;
        }
    }
}
