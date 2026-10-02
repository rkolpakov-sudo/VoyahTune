package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OfflinePunctuation.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\r\u001a\u00020\u000eHÖ\u0001J\t\u0010\u000f\u001a\u00020\u0010HÖ\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\u0004¨\u0006\u0011"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflinePunctuationConfig;", "", "model", "Lcom/k2fsa/sherpa/onnx/OfflinePunctuationModelConfig;", "(Lcom/k2fsa/sherpa/onnx/OfflinePunctuationModelConfig;)V", "getModel", "()Lcom/k2fsa/sherpa/onnx/OfflinePunctuationModelConfig;", "setModel", "component1", "copy", "equals", "", "other", "hashCode", "", "toString", "", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class OfflinePunctuationConfig {
    private OfflinePunctuationModelConfig model;

    public static /* synthetic */ OfflinePunctuationConfig copy$default(OfflinePunctuationConfig offlinePunctuationConfig, OfflinePunctuationModelConfig offlinePunctuationModelConfig, int i, Object obj) {
        if ((i & 1) != 0) {
            offlinePunctuationModelConfig = offlinePunctuationConfig.model;
        }
        return offlinePunctuationConfig.copy(offlinePunctuationModelConfig);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final OfflinePunctuationModelConfig getModel() {
        return this.model;
    }

    public final OfflinePunctuationConfig copy(OfflinePunctuationModelConfig model) {
        Intrinsics.checkNotNullParameter(model, "model");
        return new OfflinePunctuationConfig(model);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return (other instanceof OfflinePunctuationConfig) && Intrinsics.areEqual(this.model, ((OfflinePunctuationConfig) other).model);
    }

    public int hashCode() {
        return this.model.hashCode();
    }

    public String toString() {
        return "OfflinePunctuationConfig(model=" + this.model + ')';
    }

    public OfflinePunctuationConfig(OfflinePunctuationModelConfig model) {
        Intrinsics.checkNotNullParameter(model, "model");
        this.model = model;
    }

    public final OfflinePunctuationModelConfig getModel() {
        return this.model;
    }

    public final void setModel(OfflinePunctuationModelConfig offlinePunctuationModelConfig) {
        Intrinsics.checkNotNullParameter(offlinePunctuationModelConfig, "<set-?>");
        this.model = offlinePunctuationModelConfig;
    }
}
