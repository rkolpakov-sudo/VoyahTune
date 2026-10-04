package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Vad.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u001c\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001BA\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0005\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u0005¢\u0006\u0002\u0010\u000bJ\t\u0010\u001e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001f\u001a\u00020\u0005HÆ\u0003J\t\u0010 \u001a\u00020\u0005HÆ\u0003J\t\u0010!\u001a\u00020\u0005HÆ\u0003J\t\u0010\"\u001a\u00020\tHÆ\u0003J\t\u0010#\u001a\u00020\u0005HÆ\u0003JE\u0010$\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u0005HÆ\u0001J\u0013\u0010%\u001a\u00020&2\b\u0010'\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010(\u001a\u00020\tHÖ\u0001J\t\u0010)\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\n\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u001a\u0010\u0006\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\r\"\u0004\b\u0011\u0010\u000fR\u001a\u0010\u0007\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\r\"\u0004\b\u0013\u0010\u000fR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\r\"\u0004\b\u0019\u0010\u000fR\u001a\u0010\b\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u001b\"\u0004\b\u001c\u0010\u001d¨\u0006*"}, d2 = {"Lcom/k2fsa/sherpa/onnx/TenVadModelConfig;", "", "model", "", "threshold", "", "minSilenceDuration", "minSpeechDuration", "windowSize", "", "maxSpeechDuration", "(Ljava/lang/String;FFFIF)V", "getMaxSpeechDuration", "()F", "setMaxSpeechDuration", "(F)V", "getMinSilenceDuration", "setMinSilenceDuration", "getMinSpeechDuration", "setMinSpeechDuration", "getModel", "()Ljava/lang/String;", "setModel", "(Ljava/lang/String;)V", "getThreshold", "setThreshold", "getWindowSize", "()I", "setWindowSize", "(I)V", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class TenVadModelConfig {
    private float maxSpeechDuration;
    private float minSilenceDuration;
    private float minSpeechDuration;
    private String model;
    private float threshold;
    private int windowSize;

    public TenVadModelConfig() {
        this(null, 0.0f, 0.0f, 0.0f, 0, 0.0f, 63, null);
    }

    public static /* synthetic */ TenVadModelConfig copy$default(TenVadModelConfig tenVadModelConfig, String str, float f, float f2, float f3, int i, float f4, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            str = tenVadModelConfig.model;
        }
        if ((i2 & 2) != 0) {
            f = tenVadModelConfig.threshold;
        }
        if ((i2 & 4) != 0) {
            f2 = tenVadModelConfig.minSilenceDuration;
        }
        if ((i2 & 8) != 0) {
            f3 = tenVadModelConfig.minSpeechDuration;
        }
        if ((i2 & 16) != 0) {
            i = tenVadModelConfig.windowSize;
        }
        if ((i2 & 32) != 0) {
            f4 = tenVadModelConfig.maxSpeechDuration;
        }
        int i3 = i;
        float f5 = f4;
        return tenVadModelConfig.copy(str, f, f2, f3, i3, f5);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String component1() {
        return this.model;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final float component2() {
        return this.threshold;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final float component3() {
        return this.minSilenceDuration;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final float component4() {
        return this.minSpeechDuration;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final int component5() {
        return this.windowSize;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final float component6() {
        return this.maxSpeechDuration;
    }

    public final TenVadModelConfig copy(String model, float threshold, float minSilenceDuration, float minSpeechDuration, int windowSize, float maxSpeechDuration) {
        Intrinsics.checkNotNullParameter(model, "model");
        return new TenVadModelConfig(model, threshold, minSilenceDuration, minSpeechDuration, windowSize, maxSpeechDuration);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TenVadModelConfig)) {
            return false;
        }
        TenVadModelConfig tenVadModelConfig = (TenVadModelConfig) other;
        return Intrinsics.areEqual(this.model, tenVadModelConfig.model) && Float.compare(this.threshold, tenVadModelConfig.threshold) == 0 && Float.compare(this.minSilenceDuration, tenVadModelConfig.minSilenceDuration) == 0 && Float.compare(this.minSpeechDuration, tenVadModelConfig.minSpeechDuration) == 0 && this.windowSize == tenVadModelConfig.windowSize && Float.compare(this.maxSpeechDuration, tenVadModelConfig.maxSpeechDuration) == 0;
    }

    public int hashCode() {
        return (((((((((this.model.hashCode() * 31) + Float.hashCode(this.threshold)) * 31) + Float.hashCode(this.minSilenceDuration)) * 31) + Float.hashCode(this.minSpeechDuration)) * 31) + Integer.hashCode(this.windowSize)) * 31) + Float.hashCode(this.maxSpeechDuration);
    }

    public String toString() {
        return "TenVadModelConfig(model=" + this.model + ", threshold=" + this.threshold + ", minSilenceDuration=" + this.minSilenceDuration + ", minSpeechDuration=" + this.minSpeechDuration + ", windowSize=" + this.windowSize + ", maxSpeechDuration=" + this.maxSpeechDuration + ')';
    }

    public TenVadModelConfig(String model, float f, float f2, float f3, int i, float f4) {
        Intrinsics.checkNotNullParameter(model, "model");
        this.model = model;
        this.threshold = f;
        this.minSilenceDuration = f2;
        this.minSpeechDuration = f3;
        this.windowSize = i;
        this.maxSpeechDuration = f4;
    }

    public /* synthetic */ TenVadModelConfig(String str, float f, float f2, float f3, int i, float f4, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? "" : str, (i2 & 2) != 0 ? 0.5f : f, (i2 & 4) != 0 ? 0.25f : f2, (i2 & 8) != 0 ? 0.25f : f3, (i2 & 16) != 0 ? 256 : i, (i2 & 32) != 0 ? 5.0f : f4);
    }

    public final String getModel() {
        return this.model;
    }

    public final void setModel(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.model = str;
    }

    public final float getThreshold() {
        return this.threshold;
    }

    public final void setThreshold(float f) {
        this.threshold = f;
    }

    public final float getMinSilenceDuration() {
        return this.minSilenceDuration;
    }

    public final void setMinSilenceDuration(float f) {
        this.minSilenceDuration = f;
    }

    public final float getMinSpeechDuration() {
        return this.minSpeechDuration;
    }

    public final void setMinSpeechDuration(float f) {
        this.minSpeechDuration = f;
    }

    public final int getWindowSize() {
        return this.windowSize;
    }

    public final void setWindowSize(int i) {
        this.windowSize = i;
    }

    public final float getMaxSpeechDuration() {
        return this.maxSpeechDuration;
    }

    public final void setMaxSpeechDuration(float f) {
        this.maxSpeechDuration = f;
    }
}
