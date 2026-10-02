package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OfflineRecognizer.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\r\u001a\u00020\u000eHÖ\u0001J\t\u0010\u000f\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\u0004¨\u0006\u0010"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineMedAsrCtcModelConfig;", "", "model", "", "(Ljava/lang/String;)V", "getModel", "()Ljava/lang/String;", "setModel", "component1", "copy", "equals", "", "other", "hashCode", "", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class OfflineMedAsrCtcModelConfig {
    private String model;

    /* JADX WARN: Multi-variable type inference failed */
    public OfflineMedAsrCtcModelConfig() {
        this(null, 1, null);
    }

    public static /* synthetic */ OfflineMedAsrCtcModelConfig copy$default(OfflineMedAsrCtcModelConfig offlineMedAsrCtcModelConfig, String str, int i, Object obj) {
        if ((i & 1) != 0) {
            str = offlineMedAsrCtcModelConfig.model;
        }
        return offlineMedAsrCtcModelConfig.copy(str);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String component1() {
        return this.model;
    }

    public final OfflineMedAsrCtcModelConfig copy(String model) {
        Intrinsics.checkNotNullParameter(model, "model");
        return new OfflineMedAsrCtcModelConfig(model);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return (other instanceof OfflineMedAsrCtcModelConfig) && Intrinsics.areEqual(this.model, ((OfflineMedAsrCtcModelConfig) other).model);
    }

    public int hashCode() {
        return this.model.hashCode();
    }

    public String toString() {
        return "OfflineMedAsrCtcModelConfig(model=" + this.model + ')';
    }

    public OfflineMedAsrCtcModelConfig(String model) {
        Intrinsics.checkNotNullParameter(model, "model");
        this.model = model;
    }

    public /* synthetic */ OfflineMedAsrCtcModelConfig(String str, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? "" : str);
    }

    public final String getModel() {
        return this.model;
    }

    public final void setModel(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.model = str;
    }
}
