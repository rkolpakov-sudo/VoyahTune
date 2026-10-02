package ru.big.town.restoremode;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceDecimator {
    private static final double[] COEFFICIENTS = coefficients();
    private static final int TAPS = 63;
    private int cursor;
    private final float[] history = new float[63];
    private int phase;

    VoiceDecimator() {
    }

    void process(float[] fArr, float[] fArr2) {
        int i;
        if (fArr.length != 480 || fArr2.length != 160) {
            throw new IllegalArgumentException("10 ms frames required");
        }
        int length = fArr.length;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (i3 < length) {
            float f = fArr[i3];
            float[] fArr3 = this.history;
            int i5 = this.cursor;
            fArr3[i5] = f;
            int i6 = this.phase + 1;
            this.phase = i6;
            if (i6 == 3) {
                this.phase = i2;
                double[] dArr = COEFFICIENTS;
                int length2 = dArr.length;
                double d = 0.0d;
                int i7 = i2;
                while (i7 < length2) {
                    int i8 = i3;
                    d += ((double) this.history[i5]) * dArr[i7];
                    i5--;
                    if (i5 < 0) {
                        i5 = 62;
                    }
                    i7++;
                    i3 = i8;
                }
                i = i3;
                fArr2[i4] = (float) d;
                i4++;
            } else {
                i = i3;
            }
            int i9 = this.cursor + 1;
            this.cursor = i9;
            if (i9 == 63) {
                i2 = 0;
                this.cursor = 0;
            } else {
                i2 = 0;
            }
            i3 = i + 1;
        }
    }

    private static double[] coefficients() {
        double dSin;
        double[] dArr = new double[63];
        double d = 0.0d;
        for (int i = 0; i < 63; i++) {
            int i2 = i - 31;
            if (i2 == 0) {
                dSin = 0.2916666666666667d;
            } else {
                double d2 = i2;
                dSin = Math.sin(0.9162978572970231d * d2) / (d2 * 3.141592653589793d);
            }
            double dCos = dSin * (0.54d - (Math.cos((((double) i) * 6.283185307179586d) / 62.0d) * 0.46d));
            dArr[i] = dCos;
            d += dCos;
        }
        for (int i3 = 0; i3 < 63; i3++) {
            dArr[i3] = dArr[i3] / d;
        }
        return dArr;
    }
}
