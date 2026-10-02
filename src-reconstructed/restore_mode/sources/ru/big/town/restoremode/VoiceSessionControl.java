package ru.big.town.restoremode;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceSessionControl {
    private Capture capture;
    private long generation;

    interface Capture {
        void close();

        int read(short[] sArr, int i, int i2);
    }

    interface Factory {
        Capture open() throws Exception;
    }

    VoiceSessionControl() {
    }

    synchronized long begin() {
        long j;
        closeCapture();
        j = this.generation + 1;
        this.generation = j;
        return j;
    }

    synchronized boolean active(long j) {
        return j == this.generation;
    }

    synchronized boolean cancel(long j) {
        long j2 = this.generation;
        if (j != j2) {
            return false;
        }
        this.generation = j2 + 1;
        closeCapture();
        return true;
    }

    synchronized void cancelAll() {
        this.generation++;
        closeCapture();
    }

    synchronized boolean open(long j, Factory factory) throws Exception {
        if (!active(j)) {
            return false;
        }
        if (this.capture != null) {
            throw new IllegalStateException("Microphone already open");
        }
        this.capture = factory.open();
        return true;
    }

    synchronized int read(long j, short[] sArr, int i, int i2) {
        Capture capture;
        return (!active(j) || (capture = this.capture) == null) ? 0 : capture.read(sArr, i, i2);
    }

    synchronized void finishCapture(long j) {
        if (active(j)) {
            closeCapture();
        }
    }

    private void closeCapture() {
        Capture capture = this.capture;
        this.capture = null;
        if (capture != null) {
            capture.close();
        }
    }
}
