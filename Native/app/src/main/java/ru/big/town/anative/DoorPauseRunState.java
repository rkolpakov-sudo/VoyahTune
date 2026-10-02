package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class DoorPauseRunState {
    static final int NO_RESTORE_VOLUME = Integer.MIN_VALUE;
    static final int REJECTED_GENERATION = -1;
    private boolean busy;
    private int generation;
    private int restoreVolume = Integer.MIN_VALUE;

    DoorPauseRunState() {
    }

    synchronized boolean isBusy() {
        return this.busy;
    }

    synchronized int begin(int i) {
        if (this.busy) {
            return -1;
        }
        this.busy = true;
        this.restoreVolume = i;
        int i2 = this.generation + 1;
        this.generation = i2;
        return i2;
    }

    synchronized boolean isCurrent(int i) {
        return this.busy && i == this.generation;
    }

    synchronized int finishAndTakeRestoreVolume(int i) {
        if (!isCurrent(i)) {
            return Integer.MIN_VALUE;
        }
        int i2 = this.restoreVolume;
        this.busy = false;
        this.restoreVolume = Integer.MIN_VALUE;
        return i2;
    }

    synchronized int cancelAndTakeRestoreVolume() {
        int i;
        i = this.busy ? this.restoreVolume : Integer.MIN_VALUE;
        this.generation++;
        this.busy = false;
        this.restoreVolume = Integer.MIN_VALUE;
        return i;
    }
}
