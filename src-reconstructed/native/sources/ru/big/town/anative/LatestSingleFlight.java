package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class LatestSingleFlight {
    private boolean pending;
    private boolean running;

    LatestSingleFlight() {
    }

    void request() {
        this.pending = true;
    }

    boolean tryStart() {
        if (this.running || !this.pending) {
            return false;
        }
        this.pending = false;
        this.running = true;
        return true;
    }

    void complete() {
        if (!this.running) {
            throw new IllegalStateException("no operation is running");
        }
        this.running = false;
    }
}
