package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OnlinePunctuation.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u001d\b\u0086\b\u0018\u00002\u00020\u0001B7\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\u0003¢\u0006\u0002\u0010\nJ\t\u0010\u001b\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001c\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001d\u001a\u00020\u0006HÆ\u0003J\t\u0010\u001e\u001a\u00020\bHÆ\u0003J\t\u0010\u001f\u001a\u00020\u0003HÆ\u0003J;\u0010 \u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\u0003HÆ\u0001J\u0013\u0010!\u001a\u00020\b2\b\u0010\"\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010#\u001a\u00020\u0006HÖ\u0001J\t\u0010$\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\f\"\u0004\b\u0010\u0010\u000eR\u001a\u0010\u0007\u001a\u00020\bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u001a\u0010\u0005\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\u001a\u0010\t\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\f\"\u0004\b\u001a\u0010\u000e¨\u0006%"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OnlinePunctuationModelConfig;", "", "cnnBilstm", "", "bpeVocab", "numThreads", "", "debug", "", "provider", "(Ljava/lang/String;Ljava/lang/String;IZLjava/lang/String;)V", "getBpeVocab", "()Ljava/lang/String;", "setBpeVocab", "(Ljava/lang/String;)V", "getCnnBilstm", "setCnnBilstm", "getDebug", "()Z", "setDebug", "(Z)V", "getNumThreads", "()I", "setNumThreads", "(I)V", "getProvider", "setProvider", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class OnlinePunctuationModelConfig {
    private String bpeVocab;
    private String cnnBilstm;
    private boolean debug;
    private int numThreads;
    private String provider;

    public OnlinePunctuationModelConfig() {
        this(null, null, 0, false, null, 31, null);
    }

    public static /* synthetic */ OnlinePunctuationModelConfig copy$default(OnlinePunctuationModelConfig onlinePunctuationModelConfig, String str, String str2, int i, boolean z, String str3, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            str = onlinePunctuationModelConfig.cnnBilstm;
        }
        if ((i2 & 2) != 0) {
            str2 = onlinePunctuationModelConfig.bpeVocab;
        }
        if ((i2 & 4) != 0) {
            i = onlinePunctuationModelConfig.numThreads;
        }
        if ((i2 & 8) != 0) {
            z = onlinePunctuationModelConfig.debug;
        }
        if ((i2 & 16) != 0) {
            str3 = onlinePunctuationModelConfig.provider;
        }
        String str4 = str3;
        int i3 = i;
        return onlinePunctuationModelConfig.copy(str, str2, i3, z, str4);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String getCnnBilstm() {
        return this.cnnBilstm;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String getBpeVocab() {
        return this.bpeVocab;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final int getNumThreads() {
        return this.numThreads;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final boolean getDebug() {
        return this.debug;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final String getProvider() {
        return this.provider;
    }

    public final OnlinePunctuationModelConfig copy(String cnnBilstm, String bpeVocab, int numThreads, boolean debug, String provider) {
        Intrinsics.checkNotNullParameter(cnnBilstm, "cnnBilstm");
        Intrinsics.checkNotNullParameter(bpeVocab, "bpeVocab");
        Intrinsics.checkNotNullParameter(provider, "provider");
        return new OnlinePunctuationModelConfig(cnnBilstm, bpeVocab, numThreads, debug, provider);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OnlinePunctuationModelConfig)) {
            return false;
        }
        OnlinePunctuationModelConfig onlinePunctuationModelConfig = (OnlinePunctuationModelConfig) other;
        return Intrinsics.areEqual(this.cnnBilstm, onlinePunctuationModelConfig.cnnBilstm) && Intrinsics.areEqual(this.bpeVocab, onlinePunctuationModelConfig.bpeVocab) && this.numThreads == onlinePunctuationModelConfig.numThreads && this.debug == onlinePunctuationModelConfig.debug && Intrinsics.areEqual(this.provider, onlinePunctuationModelConfig.provider);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v7, types: [int] */
    /* JADX WARN: Type inference failed for: r1v5, types: [int] */
    /* JADX WARN: Type inference failed for: r1v6 */
    /* JADX WARN: Type inference failed for: r1v7 */
    public int hashCode() {
        int iHashCode = ((((this.cnnBilstm.hashCode() * 31) + this.bpeVocab.hashCode()) * 31) + Integer.hashCode(this.numThreads)) * 31;
        boolean z = this.debug;
        ?? r1 = z;
        if (z) {
            r1 = 1;
        }
        return ((iHashCode + r1) * 31) + this.provider.hashCode();
    }

    public String toString() {
        return "OnlinePunctuationModelConfig(cnnBilstm=" + this.cnnBilstm + ", bpeVocab=" + this.bpeVocab + ", numThreads=" + this.numThreads + ", debug=" + this.debug + ", provider=" + this.provider + ')';
    }

    public OnlinePunctuationModelConfig(String cnnBilstm, String bpeVocab, int i, boolean z, String provider) {
        Intrinsics.checkNotNullParameter(cnnBilstm, "cnnBilstm");
        Intrinsics.checkNotNullParameter(bpeVocab, "bpeVocab");
        Intrinsics.checkNotNullParameter(provider, "provider");
        this.cnnBilstm = cnnBilstm;
        this.bpeVocab = bpeVocab;
        this.numThreads = i;
        this.debug = z;
        this.provider = provider;
    }

    public /* synthetic */ OnlinePunctuationModelConfig(String str, String str2, int i, boolean z, String str3, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? "" : str, (i2 & 2) != 0 ? "" : str2, (i2 & 4) != 0 ? 1 : i, (i2 & 8) != 0 ? false : z, (i2 & 16) != 0 ? "cpu" : str3);
    }

    public final String getCnnBilstm() {
        return this.cnnBilstm;
    }

    public final void setCnnBilstm(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.cnnBilstm = str;
    }

    public final String getBpeVocab() {
        return this.bpeVocab;
    }

    public final void setBpeVocab(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.bpeVocab = str;
    }

    public final int getNumThreads() {
        return this.numThreads;
    }

    public final void setNumThreads(int i) {
        this.numThreads = i;
    }

    public final boolean getDebug() {
        return this.debug;
    }

    public final void setDebug(boolean z) {
        this.debug = z;
    }

    public final String getProvider() {
        return this.provider;
    }

    public final void setProvider(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.provider = str;
    }
}
