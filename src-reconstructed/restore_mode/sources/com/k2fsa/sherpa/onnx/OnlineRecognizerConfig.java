package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OnlineRecognizer.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\bH\b\u0086\b\u0018\u00002\u00020\u0001B\u0091\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0013\u0012\b\b\u0002\u0010\u0014\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0015\u001a\u00020\u0016\u0012\b\b\u0002\u0010\u0017\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0018\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0019\u001a\u00020\u0016¢\u0006\u0002\u0010\u001aJ\t\u0010K\u001a\u00020\u0003HÆ\u0003J\t\u0010L\u001a\u00020\u0011HÆ\u0003J\t\u0010M\u001a\u00020\u0016HÆ\u0003J\t\u0010N\u001a\u00020\u0011HÆ\u0003J\t\u0010O\u001a\u00020\u0011HÆ\u0003J\t\u0010P\u001a\u00020\u0016HÆ\u0003J\t\u0010Q\u001a\u00020\u0005HÆ\u0003J\t\u0010R\u001a\u00020\u0007HÆ\u0003J\t\u0010S\u001a\u00020\tHÆ\u0003J\t\u0010T\u001a\u00020\u000bHÆ\u0003J\t\u0010U\u001a\u00020\rHÆ\u0003J\t\u0010V\u001a\u00020\u000fHÆ\u0003J\t\u0010W\u001a\u00020\u0011HÆ\u0003J\t\u0010X\u001a\u00020\u0013HÆ\u0003J\u0095\u0001\u0010Y\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u00132\b\b\u0002\u0010\u0014\u001a\u00020\u00112\b\b\u0002\u0010\u0015\u001a\u00020\u00162\b\b\u0002\u0010\u0017\u001a\u00020\u00112\b\b\u0002\u0010\u0018\u001a\u00020\u00112\b\b\u0002\u0010\u0019\u001a\u00020\u0016HÆ\u0001J\u0013\u0010Z\u001a\u00020\u000f2\b\u0010[\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\\\u001a\u00020\u0013HÖ\u0001J\t\u0010]\u001a\u00020\u0011HÖ\u0001R\u001a\u0010\u0019\u001a\u00020\u0016X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u001a\u0010\b\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010 \"\u0004\b!\u0010\"R\u001a\u0010\u0010\u001a\u00020\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b#\u0010$\"\u0004\b%\u0010&R\u001a\u0010\u000e\u001a\u00020\u000fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b'\u0010(\"\u0004\b)\u0010*R\u001a\u0010\f\u001a\u00020\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b+\u0010,\"\u0004\b-\u0010.R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b/\u00100\"\u0004\b1\u00102R\u001a\u0010\u0014\u001a\u00020\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b3\u0010$\"\u0004\b4\u0010&R\u001a\u0010\u0015\u001a\u00020\u0016X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b5\u0010\u001c\"\u0004\b6\u0010\u001eR\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b7\u00108\"\u0004\b9\u0010:R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b;\u0010<\"\u0004\b=\u0010>R\u001a\u0010\u0012\u001a\u00020\u0013X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b?\u0010@\"\u0004\bA\u0010BR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bC\u0010D\"\u0004\bE\u0010FR\u001a\u0010\u0018\u001a\u00020\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bG\u0010$\"\u0004\bH\u0010&R\u001a\u0010\u0017\u001a\u00020\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bI\u0010$\"\u0004\bJ\u0010&¨\u0006^"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OnlineRecognizerConfig;", "", "featConfig", "Lcom/k2fsa/sherpa/onnx/FeatureConfig;", "modelConfig", "Lcom/k2fsa/sherpa/onnx/OnlineModelConfig;", "lmConfig", "Lcom/k2fsa/sherpa/onnx/OnlineLMConfig;", "ctcFstDecoderConfig", "Lcom/k2fsa/sherpa/onnx/OnlineCtcFstDecoderConfig;", "hr", "Lcom/k2fsa/sherpa/onnx/HomophoneReplacerConfig;", "endpointConfig", "Lcom/k2fsa/sherpa/onnx/EndpointConfig;", "enableEndpoint", "", "decodingMethod", "", "maxActivePaths", "", "hotwordsFile", "hotwordsScore", "", "ruleFsts", "ruleFars", "blankPenalty", "(Lcom/k2fsa/sherpa/onnx/FeatureConfig;Lcom/k2fsa/sherpa/onnx/OnlineModelConfig;Lcom/k2fsa/sherpa/onnx/OnlineLMConfig;Lcom/k2fsa/sherpa/onnx/OnlineCtcFstDecoderConfig;Lcom/k2fsa/sherpa/onnx/HomophoneReplacerConfig;Lcom/k2fsa/sherpa/onnx/EndpointConfig;ZLjava/lang/String;ILjava/lang/String;FLjava/lang/String;Ljava/lang/String;F)V", "getBlankPenalty", "()F", "setBlankPenalty", "(F)V", "getCtcFstDecoderConfig", "()Lcom/k2fsa/sherpa/onnx/OnlineCtcFstDecoderConfig;", "setCtcFstDecoderConfig", "(Lcom/k2fsa/sherpa/onnx/OnlineCtcFstDecoderConfig;)V", "getDecodingMethod", "()Ljava/lang/String;", "setDecodingMethod", "(Ljava/lang/String;)V", "getEnableEndpoint", "()Z", "setEnableEndpoint", "(Z)V", "getEndpointConfig", "()Lcom/k2fsa/sherpa/onnx/EndpointConfig;", "setEndpointConfig", "(Lcom/k2fsa/sherpa/onnx/EndpointConfig;)V", "getFeatConfig", "()Lcom/k2fsa/sherpa/onnx/FeatureConfig;", "setFeatConfig", "(Lcom/k2fsa/sherpa/onnx/FeatureConfig;)V", "getHotwordsFile", "setHotwordsFile", "getHotwordsScore", "setHotwordsScore", "getHr", "()Lcom/k2fsa/sherpa/onnx/HomophoneReplacerConfig;", "setHr", "(Lcom/k2fsa/sherpa/onnx/HomophoneReplacerConfig;)V", "getLmConfig", "()Lcom/k2fsa/sherpa/onnx/OnlineLMConfig;", "setLmConfig", "(Lcom/k2fsa/sherpa/onnx/OnlineLMConfig;)V", "getMaxActivePaths", "()I", "setMaxActivePaths", "(I)V", "getModelConfig", "()Lcom/k2fsa/sherpa/onnx/OnlineModelConfig;", "setModelConfig", "(Lcom/k2fsa/sherpa/onnx/OnlineModelConfig;)V", "getRuleFars", "setRuleFars", "getRuleFsts", "setRuleFsts", "component1", "component10", "component11", "component12", "component13", "component14", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class OnlineRecognizerConfig {
    private float blankPenalty;
    private OnlineCtcFstDecoderConfig ctcFstDecoderConfig;
    private String decodingMethod;
    private boolean enableEndpoint;
    private EndpointConfig endpointConfig;
    private FeatureConfig featConfig;
    private String hotwordsFile;
    private float hotwordsScore;
    private HomophoneReplacerConfig hr;
    private OnlineLMConfig lmConfig;
    private int maxActivePaths;
    private OnlineModelConfig modelConfig;
    private String ruleFars;
    private String ruleFsts;

    public OnlineRecognizerConfig() {
        this(null, null, null, null, null, null, false, null, 0, null, 0.0f, null, null, 0.0f, 16383, null);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final FeatureConfig getFeatConfig() {
        return this.featConfig;
    }

    /* JADX INFO: renamed from: component10, reason: from getter */
    public final String getHotwordsFile() {
        return this.hotwordsFile;
    }

    /* JADX INFO: renamed from: component11, reason: from getter */
    public final float getHotwordsScore() {
        return this.hotwordsScore;
    }

    /* JADX INFO: renamed from: component12, reason: from getter */
    public final String getRuleFsts() {
        return this.ruleFsts;
    }

    /* JADX INFO: renamed from: component13, reason: from getter */
    public final String getRuleFars() {
        return this.ruleFars;
    }

    /* JADX INFO: renamed from: component14, reason: from getter */
    public final float getBlankPenalty() {
        return this.blankPenalty;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final OnlineModelConfig getModelConfig() {
        return this.modelConfig;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final OnlineLMConfig getLmConfig() {
        return this.lmConfig;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final OnlineCtcFstDecoderConfig getCtcFstDecoderConfig() {
        return this.ctcFstDecoderConfig;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final HomophoneReplacerConfig getHr() {
        return this.hr;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final EndpointConfig getEndpointConfig() {
        return this.endpointConfig;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final boolean getEnableEndpoint() {
        return this.enableEndpoint;
    }

    /* JADX INFO: renamed from: component8, reason: from getter */
    public final String getDecodingMethod() {
        return this.decodingMethod;
    }

    /* JADX INFO: renamed from: component9, reason: from getter */
    public final int getMaxActivePaths() {
        return this.maxActivePaths;
    }

    public final OnlineRecognizerConfig copy(FeatureConfig featConfig, OnlineModelConfig modelConfig, OnlineLMConfig lmConfig, OnlineCtcFstDecoderConfig ctcFstDecoderConfig, HomophoneReplacerConfig hr, EndpointConfig endpointConfig, boolean enableEndpoint, String decodingMethod, int maxActivePaths, String hotwordsFile, float hotwordsScore, String ruleFsts, String ruleFars, float blankPenalty) {
        Intrinsics.checkNotNullParameter(featConfig, "featConfig");
        Intrinsics.checkNotNullParameter(modelConfig, "modelConfig");
        Intrinsics.checkNotNullParameter(lmConfig, "lmConfig");
        Intrinsics.checkNotNullParameter(ctcFstDecoderConfig, "ctcFstDecoderConfig");
        Intrinsics.checkNotNullParameter(hr, "hr");
        Intrinsics.checkNotNullParameter(endpointConfig, "endpointConfig");
        Intrinsics.checkNotNullParameter(decodingMethod, "decodingMethod");
        Intrinsics.checkNotNullParameter(hotwordsFile, "hotwordsFile");
        Intrinsics.checkNotNullParameter(ruleFsts, "ruleFsts");
        Intrinsics.checkNotNullParameter(ruleFars, "ruleFars");
        return new OnlineRecognizerConfig(featConfig, modelConfig, lmConfig, ctcFstDecoderConfig, hr, endpointConfig, enableEndpoint, decodingMethod, maxActivePaths, hotwordsFile, hotwordsScore, ruleFsts, ruleFars, blankPenalty);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OnlineRecognizerConfig)) {
            return false;
        }
        OnlineRecognizerConfig onlineRecognizerConfig = (OnlineRecognizerConfig) other;
        return Intrinsics.areEqual(this.featConfig, onlineRecognizerConfig.featConfig) && Intrinsics.areEqual(this.modelConfig, onlineRecognizerConfig.modelConfig) && Intrinsics.areEqual(this.lmConfig, onlineRecognizerConfig.lmConfig) && Intrinsics.areEqual(this.ctcFstDecoderConfig, onlineRecognizerConfig.ctcFstDecoderConfig) && Intrinsics.areEqual(this.hr, onlineRecognizerConfig.hr) && Intrinsics.areEqual(this.endpointConfig, onlineRecognizerConfig.endpointConfig) && this.enableEndpoint == onlineRecognizerConfig.enableEndpoint && Intrinsics.areEqual(this.decodingMethod, onlineRecognizerConfig.decodingMethod) && this.maxActivePaths == onlineRecognizerConfig.maxActivePaths && Intrinsics.areEqual(this.hotwordsFile, onlineRecognizerConfig.hotwordsFile) && Float.compare(this.hotwordsScore, onlineRecognizerConfig.hotwordsScore) == 0 && Intrinsics.areEqual(this.ruleFsts, onlineRecognizerConfig.ruleFsts) && Intrinsics.areEqual(this.ruleFars, onlineRecognizerConfig.ruleFars) && Float.compare(this.blankPenalty, onlineRecognizerConfig.blankPenalty) == 0;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v13, types: [int] */
    /* JADX WARN: Type inference failed for: r1v11, types: [int] */
    /* JADX WARN: Type inference failed for: r1v24 */
    /* JADX WARN: Type inference failed for: r1v25 */
    public int hashCode() {
        int iHashCode = ((((((((((this.featConfig.hashCode() * 31) + this.modelConfig.hashCode()) * 31) + this.lmConfig.hashCode()) * 31) + this.ctcFstDecoderConfig.hashCode()) * 31) + this.hr.hashCode()) * 31) + this.endpointConfig.hashCode()) * 31;
        boolean z = this.enableEndpoint;
        ?? r1 = z;
        if (z) {
            r1 = 1;
        }
        return ((((((((((((((iHashCode + r1) * 31) + this.decodingMethod.hashCode()) * 31) + Integer.hashCode(this.maxActivePaths)) * 31) + this.hotwordsFile.hashCode()) * 31) + Float.hashCode(this.hotwordsScore)) * 31) + this.ruleFsts.hashCode()) * 31) + this.ruleFars.hashCode()) * 31) + Float.hashCode(this.blankPenalty);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("OnlineRecognizerConfig(featConfig=");
        sb.append(this.featConfig).append(", modelConfig=").append(this.modelConfig).append(", lmConfig=").append(this.lmConfig).append(", ctcFstDecoderConfig=").append(this.ctcFstDecoderConfig).append(", hr=").append(this.hr).append(", endpointConfig=").append(this.endpointConfig).append(", enableEndpoint=").append(this.enableEndpoint).append(", decodingMethod=").append(this.decodingMethod).append(", maxActivePaths=").append(this.maxActivePaths).append(", hotwordsFile=").append(this.hotwordsFile).append(", hotwordsScore=").append(this.hotwordsScore).append(", ruleFsts=");
        sb.append(this.ruleFsts).append(", ruleFars=").append(this.ruleFars).append(", blankPenalty=").append(this.blankPenalty).append(')');
        return sb.toString();
    }

    public OnlineRecognizerConfig(FeatureConfig featConfig, OnlineModelConfig modelConfig, OnlineLMConfig lmConfig, OnlineCtcFstDecoderConfig ctcFstDecoderConfig, HomophoneReplacerConfig hr, EndpointConfig endpointConfig, boolean z, String decodingMethod, int i, String hotwordsFile, float f, String ruleFsts, String ruleFars, float f2) {
        Intrinsics.checkNotNullParameter(featConfig, "featConfig");
        Intrinsics.checkNotNullParameter(modelConfig, "modelConfig");
        Intrinsics.checkNotNullParameter(lmConfig, "lmConfig");
        Intrinsics.checkNotNullParameter(ctcFstDecoderConfig, "ctcFstDecoderConfig");
        Intrinsics.checkNotNullParameter(hr, "hr");
        Intrinsics.checkNotNullParameter(endpointConfig, "endpointConfig");
        Intrinsics.checkNotNullParameter(decodingMethod, "decodingMethod");
        Intrinsics.checkNotNullParameter(hotwordsFile, "hotwordsFile");
        Intrinsics.checkNotNullParameter(ruleFsts, "ruleFsts");
        Intrinsics.checkNotNullParameter(ruleFars, "ruleFars");
        this.featConfig = featConfig;
        this.modelConfig = modelConfig;
        this.lmConfig = lmConfig;
        this.ctcFstDecoderConfig = ctcFstDecoderConfig;
        this.hr = hr;
        this.endpointConfig = endpointConfig;
        this.enableEndpoint = z;
        this.decodingMethod = decodingMethod;
        this.maxActivePaths = i;
        this.hotwordsFile = hotwordsFile;
        this.hotwordsScore = f;
        this.ruleFsts = ruleFsts;
        this.ruleFars = ruleFars;
        this.blankPenalty = f2;
    }

    public /* synthetic */ OnlineRecognizerConfig(FeatureConfig featureConfig, OnlineModelConfig onlineModelConfig, OnlineLMConfig onlineLMConfig, OnlineCtcFstDecoderConfig onlineCtcFstDecoderConfig, HomophoneReplacerConfig homophoneReplacerConfig, EndpointConfig endpointConfig, boolean z, String str, int i, String str2, float f, String str3, String str4, float f2, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? new FeatureConfig(0, 0, 0.0f, 7, null) : featureConfig, (i2 & 2) != 0 ? new OnlineModelConfig(null, null, null, null, null, null, 0, false, null, null, null, null, 4095, null) : onlineModelConfig, (i2 & 4) != 0 ? new OnlineLMConfig(null, 0.0f, 3, null) : onlineLMConfig, (i2 & 8) != 0 ? new OnlineCtcFstDecoderConfig(null, 0, 3, null) : onlineCtcFstDecoderConfig, (i2 & 16) != 0 ? new HomophoneReplacerConfig(null, null, null, 7, null) : homophoneReplacerConfig, (i2 & 32) != 0 ? new EndpointConfig(null, null, null, 7, null) : endpointConfig, (i2 & 64) != 0 ? true : z, (i2 & 128) != 0 ? "greedy_search" : str, (i2 & 256) != 0 ? 4 : i, (i2 & 512) != 0 ? "" : str2, (i2 & 1024) != 0 ? 1.5f : f, (i2 & 2048) != 0 ? "" : str3, (i2 & 4096) == 0 ? str4 : "", (i2 & 8192) != 0 ? 0.0f : f2);
    }

    public final FeatureConfig getFeatConfig() {
        return this.featConfig;
    }

    public final void setFeatConfig(FeatureConfig featureConfig) {
        Intrinsics.checkNotNullParameter(featureConfig, "<set-?>");
        this.featConfig = featureConfig;
    }

    public final OnlineModelConfig getModelConfig() {
        return this.modelConfig;
    }

    public final void setModelConfig(OnlineModelConfig onlineModelConfig) {
        Intrinsics.checkNotNullParameter(onlineModelConfig, "<set-?>");
        this.modelConfig = onlineModelConfig;
    }

    public final OnlineLMConfig getLmConfig() {
        return this.lmConfig;
    }

    public final void setLmConfig(OnlineLMConfig onlineLMConfig) {
        Intrinsics.checkNotNullParameter(onlineLMConfig, "<set-?>");
        this.lmConfig = onlineLMConfig;
    }

    public final OnlineCtcFstDecoderConfig getCtcFstDecoderConfig() {
        return this.ctcFstDecoderConfig;
    }

    public final void setCtcFstDecoderConfig(OnlineCtcFstDecoderConfig onlineCtcFstDecoderConfig) {
        Intrinsics.checkNotNullParameter(onlineCtcFstDecoderConfig, "<set-?>");
        this.ctcFstDecoderConfig = onlineCtcFstDecoderConfig;
    }

    public final HomophoneReplacerConfig getHr() {
        return this.hr;
    }

    public final void setHr(HomophoneReplacerConfig homophoneReplacerConfig) {
        Intrinsics.checkNotNullParameter(homophoneReplacerConfig, "<set-?>");
        this.hr = homophoneReplacerConfig;
    }

    public final EndpointConfig getEndpointConfig() {
        return this.endpointConfig;
    }

    public final void setEndpointConfig(EndpointConfig endpointConfig) {
        Intrinsics.checkNotNullParameter(endpointConfig, "<set-?>");
        this.endpointConfig = endpointConfig;
    }

    public final boolean getEnableEndpoint() {
        return this.enableEndpoint;
    }

    public final void setEnableEndpoint(boolean z) {
        this.enableEndpoint = z;
    }

    public final String getDecodingMethod() {
        return this.decodingMethod;
    }

    public final void setDecodingMethod(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.decodingMethod = str;
    }

    public final int getMaxActivePaths() {
        return this.maxActivePaths;
    }

    public final void setMaxActivePaths(int i) {
        this.maxActivePaths = i;
    }

    public final String getHotwordsFile() {
        return this.hotwordsFile;
    }

    public final void setHotwordsFile(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.hotwordsFile = str;
    }

    public final float getHotwordsScore() {
        return this.hotwordsScore;
    }

    public final void setHotwordsScore(float f) {
        this.hotwordsScore = f;
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

    public final float getBlankPenalty() {
        return this.blankPenalty;
    }

    public final void setBlankPenalty(float f) {
        this.blankPenalty = f;
    }
}
