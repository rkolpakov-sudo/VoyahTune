package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class SplitHostGenerationGate {
    static final int LEFT = 0;
    static final long REJECTED = -1;
    static final int RIGHT = 1;
    private boolean closed;
    private final long hostGeneration;
    private boolean resumed;
    private final long[] paneGenerations = {1, 1};
    private long supervisionGeneration = 1;

    SplitHostGenerationGate(long j) {
        this.hostGeneration = j;
    }

    long hostGeneration() {
        return this.hostGeneration;
    }

    long resumeSupervision() {
        if (this.closed) {
            return -1L;
        }
        this.resumed = true;
        long j = this.supervisionGeneration + 1;
        this.supervisionGeneration = j;
        return j;
    }

    void pauseSupervision() {
        this.resumed = false;
        this.supervisionGeneration++;
    }

    long currentSupervisionGeneration() {
        return this.supervisionGeneration;
    }

    boolean acceptsSupervision(long j, long j2) {
        return !this.closed && this.resumed && j == this.hostGeneration && j2 == this.supervisionGeneration;
    }

    long nextPaneGeneration(int i) {
        checkPane(i);
        if (this.closed) {
            return -1L;
        }
        long[] jArr = this.paneGenerations;
        long j = jArr[i] + 1;
        jArr[i] = j;
        return j;
    }

    long currentPaneGeneration(int i) {
        checkPane(i);
        return this.paneGenerations[i];
    }

    void invalidatePane(int i) {
        checkPane(i);
        long[] jArr = this.paneGenerations;
        jArr[i] = jArr[i] + 1;
    }

    boolean acceptsPane(long j, int i, long j2) {
        checkPane(i);
        return !this.closed && j == this.hostGeneration && j2 == this.paneGenerations[i];
    }

    void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        this.resumed = false;
        this.supervisionGeneration++;
        long[] jArr = this.paneGenerations;
        jArr[0] = jArr[0] + 1;
        jArr[1] = jArr[1] + 1;
    }

    private static void checkPane(int i) {
        if (i != 0 && i != 1) {
            throw new IllegalArgumentException("unknown pane " + i);
        }
    }
}
