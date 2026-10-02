package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class CarPowerCallbackGate {
    static final long REJECTED_GENERATION = -1;
    private long activeGeneration;
    private boolean closed;
    private long nextGeneration;

    CarPowerCallbackGate() {
    }

    synchronized long beginRegistration() {
        if (this.closed) {
            return -1L;
        }
        long j = this.nextGeneration + 1;
        this.nextGeneration = j;
        this.activeGeneration = j;
        return j;
    }

    synchronized boolean isCurrent(long j) {
        return !this.closed && j > 0 && j == this.activeGeneration;
    }

    synchronized void invalidate(long j) {
        if (j == this.activeGeneration) {
            this.activeGeneration = 0L;
        }
    }

    synchronized void invalidateCurrent() {
        this.activeGeneration = 0L;
    }

    synchronized void close() {
        this.closed = true;
        this.activeGeneration = 0L;
    }
}
