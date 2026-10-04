package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceSessionGate {
    private String current;
    private int next;

    VoiceSessionGate() {
    }

    synchronized void begin(String str) {
        this.current = str;
        this.next = 0;
    }

    synchronized void cancel(String str) {
        if (str.equals(this.current)) {
            this.current = null;
        }
    }

    synchronized boolean submit(String str) {
        return submit(str, 0);
    }

    synchronized boolean submit(String str, int i) {
        int i2;
        if (str.equals(this.current) && i == (i2 = this.next)) {
            this.next = i2 + 1;
            return true;
        }
        return false;
    }

    synchronized boolean active(String str) {
        return str.equals(this.current);
    }

    synchronized void clear() {
        this.current = null;
        this.next = 0;
    }
}
