package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: AudioTagging.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B#\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0007HÆ\u0003J'\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÆ\u0001J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001c\u001a\u00020\u0007HÖ\u0001J\t\u0010\u001d\u001a\u00020\u0005HÖ\u0001R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014¨\u0006\u001e"}, d2 = {"Lcom/k2fsa/sherpa/onnx/AudioTaggingConfig;", "", "model", "Lcom/k2fsa/sherpa/onnx/AudioTaggingModelConfig;", "labels", "", "topK", "", "(Lcom/k2fsa/sherpa/onnx/AudioTaggingModelConfig;Ljava/lang/String;I)V", "getLabels", "()Ljava/lang/String;", "setLabels", "(Ljava/lang/String;)V", "getModel", "()Lcom/k2fsa/sherpa/onnx/AudioTaggingModelConfig;", "setModel", "(Lcom/k2fsa/sherpa/onnx/AudioTaggingModelConfig;)V", "getTopK", "()I", "setTopK", "(I)V", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class AudioTaggingConfig {
    private String labels;
    private AudioTaggingModelConfig model;
    private int topK;

    public AudioTaggingConfig() {
        this(null, null, 0, 7, null);
    }

    public static /* synthetic */ AudioTaggingConfig copy$default(AudioTaggingConfig audioTaggingConfig, AudioTaggingModelConfig audioTaggingModelConfig, String str, int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            audioTaggingModelConfig = audioTaggingConfig.model;
        }
        if ((i2 & 2) != 0) {
            str = audioTaggingConfig.labels;
        }
        if ((i2 & 4) != 0) {
            i = audioTaggingConfig.topK;
        }
        return audioTaggingConfig.copy(audioTaggingModelConfig, str, i);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final AudioTaggingModelConfig getModel() {
        return this.model;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String getLabels() {
        return this.labels;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final int getTopK() {
        return this.topK;
    }

    public final AudioTaggingConfig copy(AudioTaggingModelConfig model, String labels, int topK) {
        Intrinsics.checkNotNullParameter(model, "model");
        Intrinsics.checkNotNullParameter(labels, "labels");
        return new AudioTaggingConfig(model, labels, topK);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AudioTaggingConfig)) {
            return false;
        }
        AudioTaggingConfig audioTaggingConfig = (AudioTaggingConfig) other;
        return Intrinsics.areEqual(this.model, audioTaggingConfig.model) && Intrinsics.areEqual(this.labels, audioTaggingConfig.labels) && this.topK == audioTaggingConfig.topK;
    }

    public int hashCode() {
        return (((this.model.hashCode() * 31) + this.labels.hashCode()) * 31) + Integer.hashCode(this.topK);
    }

    public String toString() {
        return "AudioTaggingConfig(model=" + this.model + ", labels=" + this.labels + ", topK=" + this.topK + ')';
    }

    public AudioTaggingConfig(AudioTaggingModelConfig model, String labels, int i) {
        Intrinsics.checkNotNullParameter(model, "model");
        Intrinsics.checkNotNullParameter(labels, "labels");
        this.model = model;
        this.labels = labels;
        this.topK = i;
    }

    public /* synthetic */ AudioTaggingConfig(AudioTaggingModelConfig audioTaggingModelConfig, String str, int i, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? new AudioTaggingModelConfig(null, null, 0, false, null, 31, null) : audioTaggingModelConfig, (i2 & 2) != 0 ? "" : str, (i2 & 4) != 0 ? 5 : i);
    }

    public final AudioTaggingModelConfig getModel() {
        return this.model;
    }

    public final void setModel(AudioTaggingModelConfig audioTaggingModelConfig) {
        Intrinsics.checkNotNullParameter(audioTaggingModelConfig, "<set-?>");
        this.model = audioTaggingModelConfig;
    }

    public final String getLabels() {
        return this.labels;
    }

    public final void setLabels(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.labels = str;
    }

    public final int getTopK() {
        return this.topK;
    }

    public final void setTopK(int i) {
        this.topK = i;
    }
}
