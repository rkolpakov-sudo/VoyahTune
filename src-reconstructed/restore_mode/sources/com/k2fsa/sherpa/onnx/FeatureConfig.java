package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;

/* JADX INFO: compiled from: FeatureConfig.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B#\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0006HÆ\u0003J'\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÆ\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0019\u001a\u00020\u0003HÖ\u0001J\t\u0010\u001a\u001a\u00020\u001bHÖ\u0001R\u001a\u0010\u0005\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\r\"\u0004\b\u0011\u0010\u000f¨\u0006\u001c"}, d2 = {"Lcom/k2fsa/sherpa/onnx/FeatureConfig;", "", "sampleRate", "", "featureDim", "dither", "", "(IIF)V", "getDither", "()F", "setDither", "(F)V", "getFeatureDim", "()I", "setFeatureDim", "(I)V", "getSampleRate", "setSampleRate", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class FeatureConfig {
    private float dither;
    private int featureDim;
    private int sampleRate;

    public FeatureConfig() {
        this(0, 0, 0.0f, 7, null);
    }

    public static /* synthetic */ FeatureConfig copy$default(FeatureConfig featureConfig, int i, int i2, float f, int i3, Object obj) {
        if ((i3 & 1) != 0) {
            i = featureConfig.sampleRate;
        }
        if ((i3 & 2) != 0) {
            i2 = featureConfig.featureDim;
        }
        if ((i3 & 4) != 0) {
            f = featureConfig.dither;
        }
        return featureConfig.copy(i, i2, f);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final int getSampleRate() {
        return this.sampleRate;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final int getFeatureDim() {
        return this.featureDim;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final float getDither() {
        return this.dither;
    }

    public final FeatureConfig copy(int sampleRate, int featureDim, float dither) {
        return new FeatureConfig(sampleRate, featureDim, dither);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FeatureConfig)) {
            return false;
        }
        FeatureConfig featureConfig = (FeatureConfig) other;
        return this.sampleRate == featureConfig.sampleRate && this.featureDim == featureConfig.featureDim && Float.compare(this.dither, featureConfig.dither) == 0;
    }

    public int hashCode() {
        return (((Integer.hashCode(this.sampleRate) * 31) + Integer.hashCode(this.featureDim)) * 31) + Float.hashCode(this.dither);
    }

    public String toString() {
        return "FeatureConfig(sampleRate=" + this.sampleRate + ", featureDim=" + this.featureDim + ", dither=" + this.dither + ')';
    }

    public FeatureConfig(int i, int i2, float f) {
        this.sampleRate = i;
        this.featureDim = i2;
        this.dither = f;
    }

    public /* synthetic */ FeatureConfig(int i, int i2, float f, int i3, DefaultConstructorMarker defaultConstructorMarker) {
        this((i3 & 1) != 0 ? 16000 : i, (i3 & 2) != 0 ? 80 : i2, (i3 & 4) != 0 ? 0.0f : f);
    }

    public final int getSampleRate() {
        return this.sampleRate;
    }

    public final void setSampleRate(int i) {
        this.sampleRate = i;
    }

    public final int getFeatureDim() {
        return this.featureDim;
    }

    public final void setFeatureDim(int i) {
        this.featureDim = i;
    }

    public final float getDither() {
        return this.dither;
    }

    public final void setDither(float f) {
        this.dither = f;
    }
}
