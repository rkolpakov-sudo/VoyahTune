package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OnlineRecognizer.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b?\b\u0086\b\u0018\u00002\u00020\u0001B}\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0012\u001a\u00020\r\u0012\b\b\u0002\u0010\u0013\u001a\u00020\r\u0012\b\b\u0002\u0010\u0014\u001a\u00020\r\u0012\b\b\u0002\u0010\u0015\u001a\u00020\r¢\u0006\u0002\u0010\u0016J\t\u0010?\u001a\u00020\u0003HÆ\u0003J\t\u0010@\u001a\u00020\rHÆ\u0003J\t\u0010A\u001a\u00020\rHÆ\u0003J\t\u0010B\u001a\u00020\rHÆ\u0003J\t\u0010C\u001a\u00020\u0005HÆ\u0003J\t\u0010D\u001a\u00020\u0007HÆ\u0003J\t\u0010E\u001a\u00020\tHÆ\u0003J\t\u0010F\u001a\u00020\u000bHÆ\u0003J\t\u0010G\u001a\u00020\rHÆ\u0003J\t\u0010H\u001a\u00020\u000fHÆ\u0003J\t\u0010I\u001a\u00020\u0011HÆ\u0003J\t\u0010J\u001a\u00020\rHÆ\u0003J\u0081\u0001\u0010K\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\r2\b\b\u0002\u0010\u0013\u001a\u00020\r2\b\b\u0002\u0010\u0014\u001a\u00020\r2\b\b\u0002\u0010\u0015\u001a\u00020\rHÆ\u0001J\u0013\u0010L\u001a\u00020\u00112\b\u0010M\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010N\u001a\u00020\u000fHÖ\u0001J\t\u0010O\u001a\u00020\rHÖ\u0001R\u001a\u0010\u0015\u001a\u00020\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001aR\u001a\u0010\u0010\u001a\u00020\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u001a\u0010\u0013\u001a\u00020\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010\u0018\"\u0004\b \u0010\u001aR\u001a\u0010\u0014\u001a\u00020\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\u0018\"\u0004\b\"\u0010\u001aR\u001a\u0010\b\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b#\u0010$\"\u0004\b%\u0010&R\u001a\u0010\u000e\u001a\u00020\u000fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b'\u0010(\"\u0004\b)\u0010*R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b+\u0010,\"\u0004\b-\u0010.R\u001a\u0010\u0012\u001a\u00020\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b/\u0010\u0018\"\u0004\b0\u0010\u001aR\u001a\u0010\f\u001a\u00020\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b1\u0010\u0018\"\u0004\b2\u0010\u001aR\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b3\u00104\"\u0004\b5\u00106R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b7\u00108\"\u0004\b9\u0010:R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b;\u0010<\"\u0004\b=\u0010>¨\u0006P"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OnlineModelConfig;", "", "transducer", "Lcom/k2fsa/sherpa/onnx/OnlineTransducerModelConfig;", "paraformer", "Lcom/k2fsa/sherpa/onnx/OnlineParaformerModelConfig;", "zipformer2Ctc", "Lcom/k2fsa/sherpa/onnx/OnlineZipformer2CtcModelConfig;", "neMoCtc", "Lcom/k2fsa/sherpa/onnx/OnlineNeMoCtcModelConfig;", "toneCtc", "Lcom/k2fsa/sherpa/onnx/OnlineToneCtcModelConfig;", "tokens", "", "numThreads", "", "debug", "", "provider", "modelType", "modelingUnit", "bpeVocab", "(Lcom/k2fsa/sherpa/onnx/OnlineTransducerModelConfig;Lcom/k2fsa/sherpa/onnx/OnlineParaformerModelConfig;Lcom/k2fsa/sherpa/onnx/OnlineZipformer2CtcModelConfig;Lcom/k2fsa/sherpa/onnx/OnlineNeMoCtcModelConfig;Lcom/k2fsa/sherpa/onnx/OnlineToneCtcModelConfig;Ljava/lang/String;IZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getBpeVocab", "()Ljava/lang/String;", "setBpeVocab", "(Ljava/lang/String;)V", "getDebug", "()Z", "setDebug", "(Z)V", "getModelType", "setModelType", "getModelingUnit", "setModelingUnit", "getNeMoCtc", "()Lcom/k2fsa/sherpa/onnx/OnlineNeMoCtcModelConfig;", "setNeMoCtc", "(Lcom/k2fsa/sherpa/onnx/OnlineNeMoCtcModelConfig;)V", "getNumThreads", "()I", "setNumThreads", "(I)V", "getParaformer", "()Lcom/k2fsa/sherpa/onnx/OnlineParaformerModelConfig;", "setParaformer", "(Lcom/k2fsa/sherpa/onnx/OnlineParaformerModelConfig;)V", "getProvider", "setProvider", "getTokens", "setTokens", "getToneCtc", "()Lcom/k2fsa/sherpa/onnx/OnlineToneCtcModelConfig;", "setToneCtc", "(Lcom/k2fsa/sherpa/onnx/OnlineToneCtcModelConfig;)V", "getTransducer", "()Lcom/k2fsa/sherpa/onnx/OnlineTransducerModelConfig;", "setTransducer", "(Lcom/k2fsa/sherpa/onnx/OnlineTransducerModelConfig;)V", "getZipformer2Ctc", "()Lcom/k2fsa/sherpa/onnx/OnlineZipformer2CtcModelConfig;", "setZipformer2Ctc", "(Lcom/k2fsa/sherpa/onnx/OnlineZipformer2CtcModelConfig;)V", "component1", "component10", "component11", "component12", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class OnlineModelConfig {
    private String bpeVocab;
    private boolean debug;
    private String modelType;
    private String modelingUnit;
    private OnlineNeMoCtcModelConfig neMoCtc;
    private int numThreads;
    private OnlineParaformerModelConfig paraformer;
    private String provider;
    private String tokens;
    private OnlineToneCtcModelConfig toneCtc;
    private OnlineTransducerModelConfig transducer;
    private OnlineZipformer2CtcModelConfig zipformer2Ctc;

    public OnlineModelConfig() {
        this(null, null, null, null, null, null, 0, false, null, null, null, null, 4095, null);
    }

    public static /* synthetic */ OnlineModelConfig copy$default(OnlineModelConfig onlineModelConfig, OnlineTransducerModelConfig onlineTransducerModelConfig, OnlineParaformerModelConfig onlineParaformerModelConfig, OnlineZipformer2CtcModelConfig onlineZipformer2CtcModelConfig, OnlineNeMoCtcModelConfig onlineNeMoCtcModelConfig, OnlineToneCtcModelConfig onlineToneCtcModelConfig, String str, int i, boolean z, String str2, String str3, String str4, String str5, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            onlineTransducerModelConfig = onlineModelConfig.transducer;
        }
        if ((i2 & 2) != 0) {
            onlineParaformerModelConfig = onlineModelConfig.paraformer;
        }
        if ((i2 & 4) != 0) {
            onlineZipformer2CtcModelConfig = onlineModelConfig.zipformer2Ctc;
        }
        if ((i2 & 8) != 0) {
            onlineNeMoCtcModelConfig = onlineModelConfig.neMoCtc;
        }
        if ((i2 & 16) != 0) {
            onlineToneCtcModelConfig = onlineModelConfig.toneCtc;
        }
        if ((i2 & 32) != 0) {
            str = onlineModelConfig.tokens;
        }
        if ((i2 & 64) != 0) {
            i = onlineModelConfig.numThreads;
        }
        if ((i2 & 128) != 0) {
            z = onlineModelConfig.debug;
        }
        if ((i2 & 256) != 0) {
            str2 = onlineModelConfig.provider;
        }
        if ((i2 & 512) != 0) {
            str3 = onlineModelConfig.modelType;
        }
        if ((i2 & 1024) != 0) {
            str4 = onlineModelConfig.modelingUnit;
        }
        if ((i2 & 2048) != 0) {
            str5 = onlineModelConfig.bpeVocab;
        }
        String str6 = str4;
        String str7 = str5;
        String str8 = str2;
        String str9 = str3;
        int i3 = i;
        boolean z2 = z;
        OnlineToneCtcModelConfig onlineToneCtcModelConfig2 = onlineToneCtcModelConfig;
        String str10 = str;
        return onlineModelConfig.copy(onlineTransducerModelConfig, onlineParaformerModelConfig, onlineZipformer2CtcModelConfig, onlineNeMoCtcModelConfig, onlineToneCtcModelConfig2, str10, i3, z2, str8, str9, str6, str7);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final OnlineTransducerModelConfig getTransducer() {
        return this.transducer;
    }

    /* JADX INFO: renamed from: component10, reason: from getter */
    public final String getModelType() {
        return this.modelType;
    }

    /* JADX INFO: renamed from: component11, reason: from getter */
    public final String getModelingUnit() {
        return this.modelingUnit;
    }

    /* JADX INFO: renamed from: component12, reason: from getter */
    public final String getBpeVocab() {
        return this.bpeVocab;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final OnlineParaformerModelConfig getParaformer() {
        return this.paraformer;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final OnlineZipformer2CtcModelConfig getZipformer2Ctc() {
        return this.zipformer2Ctc;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final OnlineNeMoCtcModelConfig getNeMoCtc() {
        return this.neMoCtc;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final OnlineToneCtcModelConfig getToneCtc() {
        return this.toneCtc;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final String getTokens() {
        return this.tokens;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final int getNumThreads() {
        return this.numThreads;
    }

    /* JADX INFO: renamed from: component8, reason: from getter */
    public final boolean getDebug() {
        return this.debug;
    }

    /* JADX INFO: renamed from: component9, reason: from getter */
    public final String getProvider() {
        return this.provider;
    }

    public final OnlineModelConfig copy(OnlineTransducerModelConfig transducer, OnlineParaformerModelConfig paraformer, OnlineZipformer2CtcModelConfig zipformer2Ctc, OnlineNeMoCtcModelConfig neMoCtc, OnlineToneCtcModelConfig toneCtc, String tokens, int numThreads, boolean debug, String provider, String modelType, String modelingUnit, String bpeVocab) {
        Intrinsics.checkNotNullParameter(transducer, "transducer");
        Intrinsics.checkNotNullParameter(paraformer, "paraformer");
        Intrinsics.checkNotNullParameter(zipformer2Ctc, "zipformer2Ctc");
        Intrinsics.checkNotNullParameter(neMoCtc, "neMoCtc");
        Intrinsics.checkNotNullParameter(toneCtc, "toneCtc");
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        Intrinsics.checkNotNullParameter(provider, "provider");
        Intrinsics.checkNotNullParameter(modelType, "modelType");
        Intrinsics.checkNotNullParameter(modelingUnit, "modelingUnit");
        Intrinsics.checkNotNullParameter(bpeVocab, "bpeVocab");
        return new OnlineModelConfig(transducer, paraformer, zipformer2Ctc, neMoCtc, toneCtc, tokens, numThreads, debug, provider, modelType, modelingUnit, bpeVocab);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OnlineModelConfig)) {
            return false;
        }
        OnlineModelConfig onlineModelConfig = (OnlineModelConfig) other;
        return Intrinsics.areEqual(this.transducer, onlineModelConfig.transducer) && Intrinsics.areEqual(this.paraformer, onlineModelConfig.paraformer) && Intrinsics.areEqual(this.zipformer2Ctc, onlineModelConfig.zipformer2Ctc) && Intrinsics.areEqual(this.neMoCtc, onlineModelConfig.neMoCtc) && Intrinsics.areEqual(this.toneCtc, onlineModelConfig.toneCtc) && Intrinsics.areEqual(this.tokens, onlineModelConfig.tokens) && this.numThreads == onlineModelConfig.numThreads && this.debug == onlineModelConfig.debug && Intrinsics.areEqual(this.provider, onlineModelConfig.provider) && Intrinsics.areEqual(this.modelType, onlineModelConfig.modelType) && Intrinsics.areEqual(this.modelingUnit, onlineModelConfig.modelingUnit) && Intrinsics.areEqual(this.bpeVocab, onlineModelConfig.bpeVocab);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v15, types: [int] */
    /* JADX WARN: Type inference failed for: r1v13, types: [int] */
    /* JADX WARN: Type inference failed for: r1v20 */
    /* JADX WARN: Type inference failed for: r1v21 */
    public int hashCode() {
        int iHashCode = ((((((((((((this.transducer.hashCode() * 31) + this.paraformer.hashCode()) * 31) + this.zipformer2Ctc.hashCode()) * 31) + this.neMoCtc.hashCode()) * 31) + this.toneCtc.hashCode()) * 31) + this.tokens.hashCode()) * 31) + Integer.hashCode(this.numThreads)) * 31;
        boolean z = this.debug;
        ?? r1 = z;
        if (z) {
            r1 = 1;
        }
        return ((((((((iHashCode + r1) * 31) + this.provider.hashCode()) * 31) + this.modelType.hashCode()) * 31) + this.modelingUnit.hashCode()) * 31) + this.bpeVocab.hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("OnlineModelConfig(transducer=");
        sb.append(this.transducer).append(", paraformer=").append(this.paraformer).append(", zipformer2Ctc=").append(this.zipformer2Ctc).append(", neMoCtc=").append(this.neMoCtc).append(", toneCtc=").append(this.toneCtc).append(", tokens=").append(this.tokens).append(", numThreads=").append(this.numThreads).append(", debug=").append(this.debug).append(", provider=").append(this.provider).append(", modelType=").append(this.modelType).append(", modelingUnit=").append(this.modelingUnit).append(", bpeVocab=");
        sb.append(this.bpeVocab).append(')');
        return sb.toString();
    }

    public OnlineModelConfig(OnlineTransducerModelConfig transducer, OnlineParaformerModelConfig paraformer, OnlineZipformer2CtcModelConfig zipformer2Ctc, OnlineNeMoCtcModelConfig neMoCtc, OnlineToneCtcModelConfig toneCtc, String tokens, int i, boolean z, String provider, String modelType, String modelingUnit, String bpeVocab) {
        Intrinsics.checkNotNullParameter(transducer, "transducer");
        Intrinsics.checkNotNullParameter(paraformer, "paraformer");
        Intrinsics.checkNotNullParameter(zipformer2Ctc, "zipformer2Ctc");
        Intrinsics.checkNotNullParameter(neMoCtc, "neMoCtc");
        Intrinsics.checkNotNullParameter(toneCtc, "toneCtc");
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        Intrinsics.checkNotNullParameter(provider, "provider");
        Intrinsics.checkNotNullParameter(modelType, "modelType");
        Intrinsics.checkNotNullParameter(modelingUnit, "modelingUnit");
        Intrinsics.checkNotNullParameter(bpeVocab, "bpeVocab");
        this.transducer = transducer;
        this.paraformer = paraformer;
        this.zipformer2Ctc = zipformer2Ctc;
        this.neMoCtc = neMoCtc;
        this.toneCtc = toneCtc;
        this.tokens = tokens;
        this.numThreads = i;
        this.debug = z;
        this.provider = provider;
        this.modelType = modelType;
        this.modelingUnit = modelingUnit;
        this.bpeVocab = bpeVocab;
    }

    public /* synthetic */ OnlineModelConfig(OnlineTransducerModelConfig onlineTransducerModelConfig, OnlineParaformerModelConfig onlineParaformerModelConfig, OnlineZipformer2CtcModelConfig onlineZipformer2CtcModelConfig, OnlineNeMoCtcModelConfig onlineNeMoCtcModelConfig, OnlineToneCtcModelConfig onlineToneCtcModelConfig, String str, int i, boolean z, String str2, String str3, String str4, String str5, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? new OnlineTransducerModelConfig(null, null, null, null, 15, null) : onlineTransducerModelConfig, (i2 & 2) != 0 ? new OnlineParaformerModelConfig(null, null, 3, null) : onlineParaformerModelConfig, (i2 & 4) != 0 ? new OnlineZipformer2CtcModelConfig(null, 1, null) : onlineZipformer2CtcModelConfig, (i2 & 8) != 0 ? new OnlineNeMoCtcModelConfig(null, 1, null) : onlineNeMoCtcModelConfig, (i2 & 16) != 0 ? new OnlineToneCtcModelConfig(null, 1, null) : onlineToneCtcModelConfig, (i2 & 32) != 0 ? "" : str, (i2 & 64) == 0 ? i : 1, (i2 & 128) != 0 ? false : z, (i2 & 256) != 0 ? "cpu" : str2, (i2 & 512) != 0 ? "" : str3, (i2 & 1024) != 0 ? "" : str4, (i2 & 2048) != 0 ? "" : str5);
    }

    public final OnlineTransducerModelConfig getTransducer() {
        return this.transducer;
    }

    public final void setTransducer(OnlineTransducerModelConfig onlineTransducerModelConfig) {
        Intrinsics.checkNotNullParameter(onlineTransducerModelConfig, "<set-?>");
        this.transducer = onlineTransducerModelConfig;
    }

    public final OnlineParaformerModelConfig getParaformer() {
        return this.paraformer;
    }

    public final void setParaformer(OnlineParaformerModelConfig onlineParaformerModelConfig) {
        Intrinsics.checkNotNullParameter(onlineParaformerModelConfig, "<set-?>");
        this.paraformer = onlineParaformerModelConfig;
    }

    public final OnlineZipformer2CtcModelConfig getZipformer2Ctc() {
        return this.zipformer2Ctc;
    }

    public final void setZipformer2Ctc(OnlineZipformer2CtcModelConfig onlineZipformer2CtcModelConfig) {
        Intrinsics.checkNotNullParameter(onlineZipformer2CtcModelConfig, "<set-?>");
        this.zipformer2Ctc = onlineZipformer2CtcModelConfig;
    }

    public final OnlineNeMoCtcModelConfig getNeMoCtc() {
        return this.neMoCtc;
    }

    public final void setNeMoCtc(OnlineNeMoCtcModelConfig onlineNeMoCtcModelConfig) {
        Intrinsics.checkNotNullParameter(onlineNeMoCtcModelConfig, "<set-?>");
        this.neMoCtc = onlineNeMoCtcModelConfig;
    }

    public final OnlineToneCtcModelConfig getToneCtc() {
        return this.toneCtc;
    }

    public final void setToneCtc(OnlineToneCtcModelConfig onlineToneCtcModelConfig) {
        Intrinsics.checkNotNullParameter(onlineToneCtcModelConfig, "<set-?>");
        this.toneCtc = onlineToneCtcModelConfig;
    }

    public final String getTokens() {
        return this.tokens;
    }

    public final void setTokens(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.tokens = str;
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

    public final String getModelType() {
        return this.modelType;
    }

    public final void setModelType(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.modelType = str;
    }

    public final String getModelingUnit() {
        return this.modelingUnit;
    }

    public final void setModelingUnit(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.modelingUnit = str;
    }

    public final String getBpeVocab() {
        return this.bpeVocab;
    }

    public final void setBpeVocab(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.bpeVocab = str;
    }
}
