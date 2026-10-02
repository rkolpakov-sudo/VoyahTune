package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class FuelRefillEstimate {
    FuelRefillEstimate() {
    }

    static Integer liters(int i, float f) {
        if (i <= 0 || i > 200 || !Float.isFinite(f) || f < 0.0f || f > 100.0f) {
            return null;
        }
        return Integer.valueOf(Math.round((i * (100.0f - f)) / 100.0f));
    }
}
