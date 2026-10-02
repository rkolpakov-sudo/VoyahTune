package com.android.car.internal;

/* JADX INFO: loaded from: classes.dex */
public class FeatureUtil {
    public static void assertFeature(boolean z) {
        if (!z) {
            throw new IllegalStateException("Feature not enabled");
        }
    }
}
