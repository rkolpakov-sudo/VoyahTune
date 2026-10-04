package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class LightThresholds {
    static final int DEFAULT_OFF = 5;
    static final int DEFAULT_ON = 3;
    final int off;
    final int on;

    LightThresholds(int i, int i2) {
        if (i <= i2) {
            this.on = i;
            this.off = i2;
        } else {
            this.on = i2;
            this.off = i;
        }
    }

    static LightThresholds defaults() {
        return new LightThresholds(3, 5);
    }

    Boolean desiredFor(int i) {
        if (i < 0) {
            return null;
        }
        if (i <= this.on) {
            return Boolean.TRUE;
        }
        if (i > this.off) {
            return Boolean.FALSE;
        }
        return null;
    }
}
