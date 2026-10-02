package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class DoorPauseWorkGate {
    static final int REJECTED_GENERATION = -1;
    private boolean busy;
    private boolean closed;
    private boolean fallbackClaimed;
    private boolean fallbackResolved;
    private int generation;

    DoorPauseWorkGate() {
    }

    synchronized int tryAcquire() {
        if (!this.closed && !this.busy && !this.fallbackClaimed) {
            this.busy = true;
            this.fallbackResolved = false;
            int i = this.generation + 1;
            this.generation = i;
            return i;
        }
        return -1;
    }

    synchronized void release(int i) {
        if (!this.closed && i == this.generation) {
            this.busy = false;
        }
    }

    synchronized boolean isLatest(int i) {
        return !this.closed && i == this.generation;
    }

    synchronized boolean tryClaimFallback(int i) {
        if (!this.closed && i == this.generation && !this.fallbackResolved) {
            this.fallbackResolved = true;
            this.fallbackClaimed = true;
            return true;
        }
        return false;
    }

    synchronized void finishFallback(int i) {
        if (i == this.generation) {
            this.fallbackClaimed = false;
        }
    }

    synchronized void acknowledgeProxy(int i) {
        if (!this.closed && i == this.generation) {
            this.fallbackResolved = true;
        }
    }

    synchronized void close() {
        this.closed = true;
        this.generation++;
        this.busy = false;
        this.fallbackClaimed = false;
        this.fallbackResolved = true;
    }

    synchronized boolean isBusy() {
        return this.busy;
    }
}
