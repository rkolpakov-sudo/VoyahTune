package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class PlaybackActivityTracker {
    private boolean active;

    enum Change {
        SAME,
        ENTERED_ACTIVE,
        LEFT_ACTIVE
    }

    PlaybackActivityTracker(boolean z) {
        this.active = z;
    }

    Change update(boolean z) {
        if (z == this.active) {
            return Change.SAME;
        }
        this.active = z;
        return z ? Change.ENTERED_ACTIVE : Change.LEFT_ACTIVE;
    }
}
