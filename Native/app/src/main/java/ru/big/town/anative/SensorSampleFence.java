package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class SensorSampleFence {
    final long liveRevisionFence;
    final long settingsGeneration;

    SensorSampleFence(long j, long j2) {
        this.settingsGeneration = j;
        this.liveRevisionFence = j2;
    }

    boolean accepts(long j, long j2) {
        return j2 == this.settingsGeneration || j > this.liveRevisionFence;
    }
}
