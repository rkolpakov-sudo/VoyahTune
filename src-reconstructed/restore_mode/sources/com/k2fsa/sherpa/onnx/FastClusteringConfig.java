package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;

/* JADX INFO: compiled from: OfflineSpeakerDiarization.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0015\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B#\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0007HÆ\u0003J'\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÆ\u0001J\u0013\u0010\u0019\u001a\u00020\u00072\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001b\u001a\u00020\u0003HÖ\u0001J\t\u0010\u001c\u001a\u00020\u001dHÖ\u0001R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014¨\u0006\u001e"}, d2 = {"Lcom/k2fsa/sherpa/onnx/FastClusteringConfig;", "", "numClusters", "", "threshold", "", "computeConfidence", "", "(IFZ)V", "getComputeConfidence", "()Z", "setComputeConfidence", "(Z)V", "getNumClusters", "()I", "setNumClusters", "(I)V", "getThreshold", "()F", "setThreshold", "(F)V", "component1", "component2", "component3", "copy", "equals", "other", "hashCode", "toString", "", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class FastClusteringConfig {
    private boolean computeConfidence;
    private int numClusters;
    private float threshold;

    public FastClusteringConfig() {
        this(0, 0.0f, false, 7, null);
    }

    public static /* synthetic */ FastClusteringConfig copy$default(FastClusteringConfig fastClusteringConfig, int i, float f, boolean z, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            i = fastClusteringConfig.numClusters;
        }
        if ((i2 & 2) != 0) {
            f = fastClusteringConfig.threshold;
        }
        if ((i2 & 4) != 0) {
            z = fastClusteringConfig.computeConfidence;
        }
        return fastClusteringConfig.copy(i, f, z);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final int getNumClusters() {
        return this.numClusters;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final float getThreshold() {
        return this.threshold;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final boolean getComputeConfidence() {
        return this.computeConfidence;
    }

    public final FastClusteringConfig copy(int numClusters, float threshold, boolean computeConfidence) {
        return new FastClusteringConfig(numClusters, threshold, computeConfidence);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FastClusteringConfig)) {
            return false;
        }
        FastClusteringConfig fastClusteringConfig = (FastClusteringConfig) other;
        return this.numClusters == fastClusteringConfig.numClusters && Float.compare(this.threshold, fastClusteringConfig.threshold) == 0 && this.computeConfidence == fastClusteringConfig.computeConfidence;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v5, types: [int] */
    /* JADX WARN: Type inference failed for: r2v2, types: [int] */
    /* JADX WARN: Type inference failed for: r2v3 */
    /* JADX WARN: Type inference failed for: r2v4 */
    public int hashCode() {
        int iHashCode = ((Integer.hashCode(this.numClusters) * 31) + Float.hashCode(this.threshold)) * 31;
        boolean z = this.computeConfidence;
        ?? r2 = z;
        if (z) {
            r2 = 1;
        }
        return iHashCode + r2;
    }

    public String toString() {
        return "FastClusteringConfig(numClusters=" + this.numClusters + ", threshold=" + this.threshold + ", computeConfidence=" + this.computeConfidence + ')';
    }

    public FastClusteringConfig(int i, float f, boolean z) {
        this.numClusters = i;
        this.threshold = f;
        this.computeConfidence = z;
    }

    public /* synthetic */ FastClusteringConfig(int i, float f, boolean z, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? -1 : i, (i2 & 2) != 0 ? 0.5f : f, (i2 & 4) != 0 ? false : z);
    }

    public final int getNumClusters() {
        return this.numClusters;
    }

    public final void setNumClusters(int i) {
        this.numClusters = i;
    }

    public final float getThreshold() {
        return this.threshold;
    }

    public final void setThreshold(float f) {
        this.threshold = f;
    }

    public final boolean getComputeConfidence() {
        return this.computeConfidence;
    }

    public final void setComputeConfidence(boolean z) {
        this.computeConfidence = z;
    }
}
