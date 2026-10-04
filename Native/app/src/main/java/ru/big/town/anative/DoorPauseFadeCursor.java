package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class DoorPauseFadeCursor {
    private final long fadeTotalMs;
    private int lastAttemptedVolume;
    private final long restoreDeadlineMs;
    private final int startVolume;
    private final int steps;

    enum Kind {
        WRITE,
        WAIT,
        RESTORE
    }

    static final class Action {
        final long delayMs;
        final Kind kind;
        final int volume;

        private Action(Kind kind, int i, long j) {
            this.kind = kind;
            this.volume = i;
            this.delayMs = j;
        }
    }

    DoorPauseFadeCursor(int i, int i2, long j, long j2) {
        if (i <= 0 || i2 <= 0) {
            throw new IllegalArgumentException("positive volume and steps required");
        }
        this.startVolume = i;
        this.steps = i2;
        long jMax = Math.max(0L, j);
        this.fadeTotalMs = jMax;
        this.restoreDeadlineMs = DoorPauseTimeline.restoreDelayMs(jMax, j2);
        this.lastAttemptedVolume = i;
    }

    Action actionAt(long j) {
        long jMax = Math.max(0L, j);
        if (jMax >= this.restoreDeadlineMs) {
            return restore();
        }
        int iStepAt = stepAt(jMax);
        int iVolumeAtStep = volumeAtStep(iStepAt);
        if (iVolumeAtStep != this.lastAttemptedVolume) {
            return write(iVolumeAtStep);
        }
        while (true) {
            iStepAt++;
            if (iStepAt <= this.steps) {
                if (volumeAtStep(iStepAt) != this.lastAttemptedVolume) {
                    long jFadeStepDelayMs = DoorPauseTimeline.fadeStepDelayMs(iStepAt, this.steps, this.fadeTotalMs);
                    if (jFadeStepDelayMs > jMax) {
                        return waitFor(jFadeStepDelayMs - jMax);
                    }
                }
            } else {
                return waitFor(Math.max(0L, this.restoreDeadlineMs - jMax));
            }
        }
    }

    void markAttempted(int i) {
        this.lastAttemptedVolume = i;
    }

    long capDelayToRestore(long j, long j2) {
        return Math.min(Math.max(0L, j2), Math.max(0L, this.restoreDeadlineMs - Math.max(0L, j)));
    }

    private int stepAt(long j) {
        int i;
        int i2 = 0;
        int i3 = 1;
        while (true) {
            int i4 = i3;
            i = i2;
            i2 = i4;
            int i5 = this.steps;
            if (i2 > i5 || DoorPauseTimeline.fadeStepDelayMs(i2, i5, this.fadeTotalMs) > j) {
                break;
            }
            i3 = i2 + 1;
        }
        return i;
    }

    private int volumeAtStep(int i) {
        if (i <= 0) {
            return this.startVolume;
        }
        return DoorPauseTimeline.fadeStepVolume(this.startVolume, i, this.steps);
    }

    private static Action write(int i) {
        return new Action(Kind.WRITE, i, 0L);
    }

    private static Action waitFor(long j) {
        return new Action(Kind.WAIT, 0, j);
    }

    private static Action restore() {
        return new Action(Kind.RESTORE, 0, 0L);
    }
}
