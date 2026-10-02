package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OfflineRecognizer.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0017\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b¢\u0006\u0002\u0010\tJ\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0006HÆ\u0003J\t\u0010\u001b\u001a\u00020\bHÆ\u0003J1\u0010\u001c\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\bHÆ\u0001J\u0013\u0010\u001d\u001a\u00020\u00062\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001f\u001a\u00020 HÖ\u0001J\t\u0010!\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000b\"\u0004\b\u000f\u0010\rR\u001a\u0010\u0007\u001a\u00020\bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001a\u0010\u0005\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017¨\u0006\""}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineSenseVoiceModelConfig;", "", "model", "", "language", "useInverseTextNormalization", "", "qnnConfig", "Lcom/k2fsa/sherpa/onnx/QnnConfig;", "(Ljava/lang/String;Ljava/lang/String;ZLcom/k2fsa/sherpa/onnx/QnnConfig;)V", "getLanguage", "()Ljava/lang/String;", "setLanguage", "(Ljava/lang/String;)V", "getModel", "setModel", "getQnnConfig", "()Lcom/k2fsa/sherpa/onnx/QnnConfig;", "setQnnConfig", "(Lcom/k2fsa/sherpa/onnx/QnnConfig;)V", "getUseInverseTextNormalization", "()Z", "setUseInverseTextNormalization", "(Z)V", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class OfflineSenseVoiceModelConfig {
    private String language;
    private String model;
    private QnnConfig qnnConfig;
    private boolean useInverseTextNormalization;

    public OfflineSenseVoiceModelConfig() {
        this(null, null, false, null, 15, null);
    }

    public static /* synthetic */ OfflineSenseVoiceModelConfig copy$default(OfflineSenseVoiceModelConfig offlineSenseVoiceModelConfig, String str, String str2, boolean z, QnnConfig qnnConfig, int i, Object obj) {
        if ((i & 1) != 0) {
            str = offlineSenseVoiceModelConfig.model;
        }
        if ((i & 2) != 0) {
            str2 = offlineSenseVoiceModelConfig.language;
        }
        if ((i & 4) != 0) {
            z = offlineSenseVoiceModelConfig.useInverseTextNormalization;
        }
        if ((i & 8) != 0) {
            qnnConfig = offlineSenseVoiceModelConfig.qnnConfig;
        }
        return offlineSenseVoiceModelConfig.copy(str, str2, z, qnnConfig);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String getModel() {
        return this.model;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String getLanguage() {
        return this.language;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final boolean getUseInverseTextNormalization() {
        return this.useInverseTextNormalization;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final QnnConfig getQnnConfig() {
        return this.qnnConfig;
    }

    public final OfflineSenseVoiceModelConfig copy(String model, String language, boolean useInverseTextNormalization, QnnConfig qnnConfig) {
        Intrinsics.checkNotNullParameter(model, "model");
        Intrinsics.checkNotNullParameter(language, "language");
        Intrinsics.checkNotNullParameter(qnnConfig, "qnnConfig");
        return new OfflineSenseVoiceModelConfig(model, language, useInverseTextNormalization, qnnConfig);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineSenseVoiceModelConfig)) {
            return false;
        }
        OfflineSenseVoiceModelConfig offlineSenseVoiceModelConfig = (OfflineSenseVoiceModelConfig) other;
        return Intrinsics.areEqual(this.model, offlineSenseVoiceModelConfig.model) && Intrinsics.areEqual(this.language, offlineSenseVoiceModelConfig.language) && this.useInverseTextNormalization == offlineSenseVoiceModelConfig.useInverseTextNormalization && Intrinsics.areEqual(this.qnnConfig, offlineSenseVoiceModelConfig.qnnConfig);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v5, types: [int] */
    /* JADX WARN: Type inference failed for: r1v3, types: [int] */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r1v5 */
    public int hashCode() {
        int iHashCode = ((this.model.hashCode() * 31) + this.language.hashCode()) * 31;
        boolean z = this.useInverseTextNormalization;
        ?? r1 = z;
        if (z) {
            r1 = 1;
        }
        return ((iHashCode + r1) * 31) + this.qnnConfig.hashCode();
    }

    public String toString() {
        return "OfflineSenseVoiceModelConfig(model=" + this.model + ", language=" + this.language + ", useInverseTextNormalization=" + this.useInverseTextNormalization + ", qnnConfig=" + this.qnnConfig + ')';
    }

    public OfflineSenseVoiceModelConfig(String model, String language, boolean z, QnnConfig qnnConfig) {
        Intrinsics.checkNotNullParameter(model, "model");
        Intrinsics.checkNotNullParameter(language, "language");
        Intrinsics.checkNotNullParameter(qnnConfig, "qnnConfig");
        this.model = model;
        this.language = language;
        this.useInverseTextNormalization = z;
        this.qnnConfig = qnnConfig;
    }

    public /* synthetic */ OfflineSenseVoiceModelConfig(String str, String str2, boolean z, QnnConfig qnnConfig, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? "" : str, (i & 2) != 0 ? "" : str2, (i & 4) != 0 ? true : z, (i & 8) != 0 ? new QnnConfig(null, null, null, 7, null) : qnnConfig);
    }

    public final String getModel() {
        return this.model;
    }

    public final void setModel(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.model = str;
    }

    public final String getLanguage() {
        return this.language;
    }

    public final void setLanguage(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.language = str;
    }

    public final boolean getUseInverseTextNormalization() {
        return this.useInverseTextNormalization;
    }

    public final void setUseInverseTextNormalization(boolean z) {
        this.useInverseTextNormalization = z;
    }

    public final QnnConfig getQnnConfig() {
        return this.qnnConfig;
    }

    public final void setQnnConfig(QnnConfig qnnConfig) {
        Intrinsics.checkNotNullParameter(qnnConfig, "<set-?>");
        this.qnnConfig = qnnConfig;
    }
}
