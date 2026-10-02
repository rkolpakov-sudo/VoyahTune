package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OfflineSpeakerDiarization.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u001b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B7\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\t¢\u0006\u0002\u0010\u000bJ\t\u0010\u001e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001f\u001a\u00020\u0005HÆ\u0003J\t\u0010 \u001a\u00020\u0007HÆ\u0003J\t\u0010!\u001a\u00020\tHÆ\u0003J\t\u0010\"\u001a\u00020\tHÆ\u0003J;\u0010#\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\tHÆ\u0001J\u0013\u0010$\u001a\u00020%2\b\u0010&\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010'\u001a\u00020(HÖ\u0001J\t\u0010)\u001a\u00020*HÖ\u0001R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001a\u0010\n\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001a\u0010\b\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0015\"\u0004\b\u0019\u0010\u0017R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u001b\"\u0004\b\u001c\u0010\u001d¨\u0006+"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineSpeakerDiarizationConfig;", "", "segmentation", "Lcom/k2fsa/sherpa/onnx/OfflineSpeakerSegmentationModelConfig;", "embedding", "Lcom/k2fsa/sherpa/onnx/SpeakerEmbeddingExtractorConfig;", "clustering", "Lcom/k2fsa/sherpa/onnx/FastClusteringConfig;", "minDurationOn", "", "minDurationOff", "(Lcom/k2fsa/sherpa/onnx/OfflineSpeakerSegmentationModelConfig;Lcom/k2fsa/sherpa/onnx/SpeakerEmbeddingExtractorConfig;Lcom/k2fsa/sherpa/onnx/FastClusteringConfig;FF)V", "getClustering", "()Lcom/k2fsa/sherpa/onnx/FastClusteringConfig;", "setClustering", "(Lcom/k2fsa/sherpa/onnx/FastClusteringConfig;)V", "getEmbedding", "()Lcom/k2fsa/sherpa/onnx/SpeakerEmbeddingExtractorConfig;", "setEmbedding", "(Lcom/k2fsa/sherpa/onnx/SpeakerEmbeddingExtractorConfig;)V", "getMinDurationOff", "()F", "setMinDurationOff", "(F)V", "getMinDurationOn", "setMinDurationOn", "getSegmentation", "()Lcom/k2fsa/sherpa/onnx/OfflineSpeakerSegmentationModelConfig;", "setSegmentation", "(Lcom/k2fsa/sherpa/onnx/OfflineSpeakerSegmentationModelConfig;)V", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class OfflineSpeakerDiarizationConfig {
    private FastClusteringConfig clustering;
    private SpeakerEmbeddingExtractorConfig embedding;
    private float minDurationOff;
    private float minDurationOn;
    private OfflineSpeakerSegmentationModelConfig segmentation;

    public OfflineSpeakerDiarizationConfig() {
        this(null, null, null, 0.0f, 0.0f, 31, null);
    }

    public static /* synthetic */ OfflineSpeakerDiarizationConfig copy$default(OfflineSpeakerDiarizationConfig offlineSpeakerDiarizationConfig, OfflineSpeakerSegmentationModelConfig offlineSpeakerSegmentationModelConfig, SpeakerEmbeddingExtractorConfig speakerEmbeddingExtractorConfig, FastClusteringConfig fastClusteringConfig, float f, float f2, int i, Object obj) {
        if ((i & 1) != 0) {
            offlineSpeakerSegmentationModelConfig = offlineSpeakerDiarizationConfig.segmentation;
        }
        if ((i & 2) != 0) {
            speakerEmbeddingExtractorConfig = offlineSpeakerDiarizationConfig.embedding;
        }
        if ((i & 4) != 0) {
            fastClusteringConfig = offlineSpeakerDiarizationConfig.clustering;
        }
        if ((i & 8) != 0) {
            f = offlineSpeakerDiarizationConfig.minDurationOn;
        }
        if ((i & 16) != 0) {
            f2 = offlineSpeakerDiarizationConfig.minDurationOff;
        }
        float f3 = f2;
        FastClusteringConfig fastClusteringConfig2 = fastClusteringConfig;
        return offlineSpeakerDiarizationConfig.copy(offlineSpeakerSegmentationModelConfig, speakerEmbeddingExtractorConfig, fastClusteringConfig2, f, f3);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final OfflineSpeakerSegmentationModelConfig getSegmentation() {
        return this.segmentation;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final SpeakerEmbeddingExtractorConfig getEmbedding() {
        return this.embedding;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final FastClusteringConfig getClustering() {
        return this.clustering;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final float getMinDurationOn() {
        return this.minDurationOn;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final float getMinDurationOff() {
        return this.minDurationOff;
    }

    public final OfflineSpeakerDiarizationConfig copy(OfflineSpeakerSegmentationModelConfig segmentation, SpeakerEmbeddingExtractorConfig embedding, FastClusteringConfig clustering, float minDurationOn, float minDurationOff) {
        Intrinsics.checkNotNullParameter(segmentation, "segmentation");
        Intrinsics.checkNotNullParameter(embedding, "embedding");
        Intrinsics.checkNotNullParameter(clustering, "clustering");
        return new OfflineSpeakerDiarizationConfig(segmentation, embedding, clustering, minDurationOn, minDurationOff);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineSpeakerDiarizationConfig)) {
            return false;
        }
        OfflineSpeakerDiarizationConfig offlineSpeakerDiarizationConfig = (OfflineSpeakerDiarizationConfig) other;
        return Intrinsics.areEqual(this.segmentation, offlineSpeakerDiarizationConfig.segmentation) && Intrinsics.areEqual(this.embedding, offlineSpeakerDiarizationConfig.embedding) && Intrinsics.areEqual(this.clustering, offlineSpeakerDiarizationConfig.clustering) && Float.compare(this.minDurationOn, offlineSpeakerDiarizationConfig.minDurationOn) == 0 && Float.compare(this.minDurationOff, offlineSpeakerDiarizationConfig.minDurationOff) == 0;
    }

    public int hashCode() {
        return (((((((this.segmentation.hashCode() * 31) + this.embedding.hashCode()) * 31) + this.clustering.hashCode()) * 31) + Float.hashCode(this.minDurationOn)) * 31) + Float.hashCode(this.minDurationOff);
    }

    public String toString() {
        return "OfflineSpeakerDiarizationConfig(segmentation=" + this.segmentation + ", embedding=" + this.embedding + ", clustering=" + this.clustering + ", minDurationOn=" + this.minDurationOn + ", minDurationOff=" + this.minDurationOff + ')';
    }

    public OfflineSpeakerDiarizationConfig(OfflineSpeakerSegmentationModelConfig segmentation, SpeakerEmbeddingExtractorConfig embedding, FastClusteringConfig clustering, float f, float f2) {
        Intrinsics.checkNotNullParameter(segmentation, "segmentation");
        Intrinsics.checkNotNullParameter(embedding, "embedding");
        Intrinsics.checkNotNullParameter(clustering, "clustering");
        this.segmentation = segmentation;
        this.embedding = embedding;
        this.clustering = clustering;
        this.minDurationOn = f;
        this.minDurationOff = f2;
    }

    public /* synthetic */ OfflineSpeakerDiarizationConfig(OfflineSpeakerSegmentationModelConfig offlineSpeakerSegmentationModelConfig, SpeakerEmbeddingExtractorConfig speakerEmbeddingExtractorConfig, FastClusteringConfig fastClusteringConfig, float f, float f2, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? new OfflineSpeakerSegmentationModelConfig(null, 0, false, null, 15, null) : offlineSpeakerSegmentationModelConfig, (i & 2) != 0 ? new SpeakerEmbeddingExtractorConfig(null, 0, false, null, 15, null) : speakerEmbeddingExtractorConfig, (i & 4) != 0 ? new FastClusteringConfig(0, 0.0f, false, 7, null) : fastClusteringConfig, (i & 8) != 0 ? 0.2f : f, (i & 16) != 0 ? 0.5f : f2);
    }

    public final OfflineSpeakerSegmentationModelConfig getSegmentation() {
        return this.segmentation;
    }

    public final void setSegmentation(OfflineSpeakerSegmentationModelConfig offlineSpeakerSegmentationModelConfig) {
        Intrinsics.checkNotNullParameter(offlineSpeakerSegmentationModelConfig, "<set-?>");
        this.segmentation = offlineSpeakerSegmentationModelConfig;
    }

    public final SpeakerEmbeddingExtractorConfig getEmbedding() {
        return this.embedding;
    }

    public final void setEmbedding(SpeakerEmbeddingExtractorConfig speakerEmbeddingExtractorConfig) {
        Intrinsics.checkNotNullParameter(speakerEmbeddingExtractorConfig, "<set-?>");
        this.embedding = speakerEmbeddingExtractorConfig;
    }

    public final FastClusteringConfig getClustering() {
        return this.clustering;
    }

    public final void setClustering(FastClusteringConfig fastClusteringConfig) {
        Intrinsics.checkNotNullParameter(fastClusteringConfig, "<set-?>");
        this.clustering = fastClusteringConfig;
    }

    public final float getMinDurationOn() {
        return this.minDurationOn;
    }

    public final void setMinDurationOn(float f) {
        this.minDurationOn = f;
    }

    public final float getMinDurationOff() {
        return this.minDurationOff;
    }

    public final void setMinDurationOff(float f) {
        this.minDurationOff = f;
    }
}
