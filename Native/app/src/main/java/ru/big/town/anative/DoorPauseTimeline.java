package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class DoorPauseTimeline {
    private DoorPauseTimeline() {
    }

    static long fadeStepDelayMs(int i, int i2, long j) {
        if (i2 <= 0 || i <= 0) {
            return 0L;
        }
        return Math.round((j * ((double) Math.min(i, i2))) / ((double) i2));
    }

    static int fadeStepVolume(int i, int i2, int i3) {
        if (i <= 0 || i3 <= 0) {
            return 0;
        }
        return Math.max(0, Math.round(i * (float) (i3 - Math.max(0, Math.min(i2, i3))) / i3));
    }

    static long restoreDelayMs(long j, long j2) {
        return Math.max(j, j2);
    }
}
