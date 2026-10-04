package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OfflineSpeakerDiarization.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0019\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÆ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0015\u001a\u00020\u0016HÖ\u0001J\t\u0010\u0017\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000e¨\u0006\u0018"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineSpeakerSegmentationPyannoteModelConfig;", "", "model", "", "windowShiftRatio", "", "(Ljava/lang/String;F)V", "getModel", "()Ljava/lang/String;", "setModel", "(Ljava/lang/String;)V", "getWindowShiftRatio", "()F", "setWindowShiftRatio", "(F)V", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class OfflineSpeakerSegmentationPyannoteModelConfig {
    private String model;
    private float windowShiftRatio;

    /* JADX WARN: Multi-variable type inference failed */
    public OfflineSpeakerSegmentationPyannoteModelConfig() {
        this(null, 0.0f, 3, null);
    }

    public static /* synthetic */ OfflineSpeakerSegmentationPyannoteModelConfig copy$default(OfflineSpeakerSegmentationPyannoteModelConfig offlineSpeakerSegmentationPyannoteModelConfig, String str, float f, int i, Object obj) {
        if ((i & 1) != 0) {
            str = offlineSpeakerSegmentationPyannoteModelConfig.model;
        }
        if ((i & 2) != 0) {
            f = offlineSpeakerSegmentationPyannoteModelConfig.windowShiftRatio;
        }
        return offlineSpeakerSegmentationPyannoteModelConfig.copy(str, f);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String component1() {
        return this.model;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final float component2() {
        return this.windowShiftRatio;
    }

    public final OfflineSpeakerSegmentationPyannoteModelConfig copy(String model, float windowShiftRatio) {
        Intrinsics.checkNotNullParameter(model, "model");
        return new OfflineSpeakerSegmentationPyannoteModelConfig(model, windowShiftRatio);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineSpeakerSegmentationPyannoteModelConfig)) {
            return false;
        }
        OfflineSpeakerSegmentationPyannoteModelConfig offlineSpeakerSegmentationPyannoteModelConfig = (OfflineSpeakerSegmentationPyannoteModelConfig) other;
        return Intrinsics.areEqual(this.model, offlineSpeakerSegmentationPyannoteModelConfig.model) && Float.compare(this.windowShiftRatio, offlineSpeakerSegmentationPyannoteModelConfig.windowShiftRatio) == 0;
    }

    public int hashCode() {
        return (this.model.hashCode() * 31) + Float.hashCode(this.windowShiftRatio);
    }

    public String toString() {
        return "OfflineSpeakerSegmentationPyannoteModelConfig(model=" + this.model + ", windowShiftRatio=" + this.windowShiftRatio + ')';
    }

    public OfflineSpeakerSegmentationPyannoteModelConfig(String model, float f) {
        Intrinsics.checkNotNullParameter(model, "model");
        this.model = model;
        this.windowShiftRatio = f;
    }

    public /* synthetic */ OfflineSpeakerSegmentationPyannoteModelConfig(String str, float f, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? "" : str, (i & 2) != 0 ? 0.1f : f);
    }

    public final String getModel() {
        return this.model;
    }

    public final void setModel(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.model = str;
    }

    public final float getWindowShiftRatio() {
        return this.windowShiftRatio;
    }

    public final void setWindowShiftRatio(float f) {
        this.windowShiftRatio = f;
    }
}
