package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Tts.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u001a\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B7\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\n¢\u0006\u0002\u0010\u000bJ\t\u0010\u001e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001f\u001a\u00020\u0005HÆ\u0003J\t\u0010 \u001a\u00020\u0005HÆ\u0003J\t\u0010!\u001a\u00020\bHÆ\u0003J\t\u0010\"\u001a\u00020\nHÆ\u0003J;\u0010#\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\nHÆ\u0001J\u0013\u0010$\u001a\u00020%2\b\u0010&\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010'\u001a\u00020\bHÖ\u0001J\t\u0010(\u001a\u00020\u0005HÖ\u0001R\u001a\u0010\u0007\u001a\u00020\bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001a\u0010\u0006\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0015\"\u0004\b\u0019\u0010\u0017R\u001a\u0010\t\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u001b\"\u0004\b\u001c\u0010\u001d¨\u0006)"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineTtsConfig;", "", "model", "Lcom/k2fsa/sherpa/onnx/OfflineTtsModelConfig;", "ruleFsts", "", "ruleFars", "maxNumSentences", "", "silenceScale", "", "(Lcom/k2fsa/sherpa/onnx/OfflineTtsModelConfig;Ljava/lang/String;Ljava/lang/String;IF)V", "getMaxNumSentences", "()I", "setMaxNumSentences", "(I)V", "getModel", "()Lcom/k2fsa/sherpa/onnx/OfflineTtsModelConfig;", "setModel", "(Lcom/k2fsa/sherpa/onnx/OfflineTtsModelConfig;)V", "getRuleFars", "()Ljava/lang/String;", "setRuleFars", "(Ljava/lang/String;)V", "getRuleFsts", "setRuleFsts", "getSilenceScale", "()F", "setSilenceScale", "(F)V", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class OfflineTtsConfig {
    private int maxNumSentences;
    private OfflineTtsModelConfig model;
    private String ruleFars;
    private String ruleFsts;
    private float silenceScale;

    public OfflineTtsConfig() {
        this(null, null, null, 0, 0.0f, 31, null);
    }

    public static /* synthetic */ OfflineTtsConfig copy$default(OfflineTtsConfig offlineTtsConfig, OfflineTtsModelConfig offlineTtsModelConfig, String str, String str2, int i, float f, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            offlineTtsModelConfig = offlineTtsConfig.model;
        }
        if ((i2 & 2) != 0) {
            str = offlineTtsConfig.ruleFsts;
        }
        if ((i2 & 4) != 0) {
            str2 = offlineTtsConfig.ruleFars;
        }
        if ((i2 & 8) != 0) {
            i = offlineTtsConfig.maxNumSentences;
        }
        if ((i2 & 16) != 0) {
            f = offlineTtsConfig.silenceScale;
        }
        float f2 = f;
        String str3 = str2;
        return offlineTtsConfig.copy(offlineTtsModelConfig, str, str3, i, f2);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final OfflineTtsModelConfig getModel() {
        return this.model;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String getRuleFsts() {
        return this.ruleFsts;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final String getRuleFars() {
        return this.ruleFars;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final int getMaxNumSentences() {
        return this.maxNumSentences;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final float getSilenceScale() {
        return this.silenceScale;
    }

    public final OfflineTtsConfig copy(OfflineTtsModelConfig model, String ruleFsts, String ruleFars, int maxNumSentences, float silenceScale) {
        Intrinsics.checkNotNullParameter(model, "model");
        Intrinsics.checkNotNullParameter(ruleFsts, "ruleFsts");
        Intrinsics.checkNotNullParameter(ruleFars, "ruleFars");
        return new OfflineTtsConfig(model, ruleFsts, ruleFars, maxNumSentences, silenceScale);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineTtsConfig)) {
            return false;
        }
        OfflineTtsConfig offlineTtsConfig = (OfflineTtsConfig) other;
        return Intrinsics.areEqual(this.model, offlineTtsConfig.model) && Intrinsics.areEqual(this.ruleFsts, offlineTtsConfig.ruleFsts) && Intrinsics.areEqual(this.ruleFars, offlineTtsConfig.ruleFars) && this.maxNumSentences == offlineTtsConfig.maxNumSentences && Float.compare(this.silenceScale, offlineTtsConfig.silenceScale) == 0;
    }

    public int hashCode() {
        return (((((((this.model.hashCode() * 31) + this.ruleFsts.hashCode()) * 31) + this.ruleFars.hashCode()) * 31) + Integer.hashCode(this.maxNumSentences)) * 31) + Float.hashCode(this.silenceScale);
    }

    public String toString() {
        return "OfflineTtsConfig(model=" + this.model + ", ruleFsts=" + this.ruleFsts + ", ruleFars=" + this.ruleFars + ", maxNumSentences=" + this.maxNumSentences + ", silenceScale=" + this.silenceScale + ')';
    }

    public OfflineTtsConfig(OfflineTtsModelConfig model, String ruleFsts, String ruleFars, int i, float f) {
        Intrinsics.checkNotNullParameter(model, "model");
        Intrinsics.checkNotNullParameter(ruleFsts, "ruleFsts");
        Intrinsics.checkNotNullParameter(ruleFars, "ruleFars");
        this.model = model;
        this.ruleFsts = ruleFsts;
        this.ruleFars = ruleFars;
        this.maxNumSentences = i;
        this.silenceScale = f;
    }

    public /* synthetic */ OfflineTtsConfig(OfflineTtsModelConfig offlineTtsModelConfig, String str, String str2, int i, float f, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? new OfflineTtsModelConfig(null, null, null, null, null, null, null, 0, false, null, 1023, null) : offlineTtsModelConfig, (i2 & 2) != 0 ? "" : str, (i2 & 4) == 0 ? str2 : "", (i2 & 8) != 0 ? 1 : i, (i2 & 16) != 0 ? 0.2f : f);
    }

    public final OfflineTtsModelConfig getModel() {
        return this.model;
    }

    public final void setModel(OfflineTtsModelConfig offlineTtsModelConfig) {
        Intrinsics.checkNotNullParameter(offlineTtsModelConfig, "<set-?>");
        this.model = offlineTtsModelConfig;
    }

    public final String getRuleFsts() {
        return this.ruleFsts;
    }

    public final void setRuleFsts(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.ruleFsts = str;
    }

    public final String getRuleFars() {
        return this.ruleFars;
    }

    public final void setRuleFars(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.ruleFars = str;
    }

    public final int getMaxNumSentences() {
        return this.maxNumSentences;
    }

    public final void setMaxNumSentences(int i) {
        this.maxNumSentences = i;
    }

    public final float getSilenceScale() {
        return this.silenceScale;
    }

    public final void setSilenceScale(float f) {
        this.silenceScale = f;
    }
}
