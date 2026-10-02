package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OfflineRecognizer.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b0\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001Bi\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\t\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u000f\u001a\u00020\t\u0012\b\b\u0002\u0010\u0010\u001a\u00020\t\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u000e¢\u0006\u0002\u0010\u0012J\t\u00103\u001a\u00020\u0003HÆ\u0003J\t\u00104\u001a\u00020\u000eHÆ\u0003J\t\u00105\u001a\u00020\u0005HÆ\u0003J\t\u00106\u001a\u00020\u0007HÆ\u0003J\t\u00107\u001a\u00020\tHÆ\u0003J\t\u00108\u001a\u00020\u000bHÆ\u0003J\t\u00109\u001a\u00020\tHÆ\u0003J\t\u0010:\u001a\u00020\u000eHÆ\u0003J\t\u0010;\u001a\u00020\tHÆ\u0003J\t\u0010<\u001a\u00020\tHÆ\u0003Jm\u0010=\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\t2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\t2\b\b\u0002\u0010\u0010\u001a\u00020\t2\b\b\u0002\u0010\u0011\u001a\u00020\u000eHÆ\u0001J\u0013\u0010>\u001a\u00020?2\b\u0010@\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010A\u001a\u00020\u000bHÖ\u0001J\t\u0010B\u001a\u00020\tHÖ\u0001R\u001a\u0010\u0011\u001a\u00020\u000eX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u001a\u0010\b\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001aR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u001a\u0010\f\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010\u0018\"\u0004\b \u0010\u001aR\u001a\u0010\r\u001a\u00020\u000eX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\u0014\"\u0004\b\"\u0010\u0016R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b#\u0010$\"\u0004\b%\u0010&R\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b'\u0010(\"\u0004\b)\u0010*R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b+\u0010,\"\u0004\b-\u0010.R\u001a\u0010\u0010\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b/\u0010\u0018\"\u0004\b0\u0010\u001aR\u001a\u0010\u000f\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b1\u0010\u0018\"\u0004\b2\u0010\u001a¨\u0006C"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineRecognizerConfig;", "", "featConfig", "Lcom/k2fsa/sherpa/onnx/FeatureConfig;", "modelConfig", "Lcom/k2fsa/sherpa/onnx/OfflineModelConfig;", "hr", "Lcom/k2fsa/sherpa/onnx/HomophoneReplacerConfig;", "decodingMethod", "", "maxActivePaths", "", "hotwordsFile", "hotwordsScore", "", "ruleFsts", "ruleFars", "blankPenalty", "(Lcom/k2fsa/sherpa/onnx/FeatureConfig;Lcom/k2fsa/sherpa/onnx/OfflineModelConfig;Lcom/k2fsa/sherpa/onnx/HomophoneReplacerConfig;Ljava/lang/String;ILjava/lang/String;FLjava/lang/String;Ljava/lang/String;F)V", "getBlankPenalty", "()F", "setBlankPenalty", "(F)V", "getDecodingMethod", "()Ljava/lang/String;", "setDecodingMethod", "(Ljava/lang/String;)V", "getFeatConfig", "()Lcom/k2fsa/sherpa/onnx/FeatureConfig;", "setFeatConfig", "(Lcom/k2fsa/sherpa/onnx/FeatureConfig;)V", "getHotwordsFile", "setHotwordsFile", "getHotwordsScore", "setHotwordsScore", "getHr", "()Lcom/k2fsa/sherpa/onnx/HomophoneReplacerConfig;", "setHr", "(Lcom/k2fsa/sherpa/onnx/HomophoneReplacerConfig;)V", "getMaxActivePaths", "()I", "setMaxActivePaths", "(I)V", "getModelConfig", "()Lcom/k2fsa/sherpa/onnx/OfflineModelConfig;", "setModelConfig", "(Lcom/k2fsa/sherpa/onnx/OfflineModelConfig;)V", "getRuleFars", "setRuleFars", "getRuleFsts", "setRuleFsts", "component1", "component10", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class OfflineRecognizerConfig {
    private float blankPenalty;
    private String decodingMethod;
    private FeatureConfig featConfig;
    private String hotwordsFile;
    private float hotwordsScore;
    private HomophoneReplacerConfig hr;
    private int maxActivePaths;
    private OfflineModelConfig modelConfig;
    private String ruleFars;
    private String ruleFsts;

    public OfflineRecognizerConfig() {
        this(null, null, null, null, 0, null, 0.0f, null, null, 0.0f, 1023, null);
    }

    public static /* synthetic */ OfflineRecognizerConfig copy$default(OfflineRecognizerConfig offlineRecognizerConfig, FeatureConfig featureConfig, OfflineModelConfig offlineModelConfig, HomophoneReplacerConfig homophoneReplacerConfig, String str, int i, String str2, float f, String str3, String str4, float f2, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            featureConfig = offlineRecognizerConfig.featConfig;
        }
        if ((i2 & 2) != 0) {
            offlineModelConfig = offlineRecognizerConfig.modelConfig;
        }
        if ((i2 & 4) != 0) {
            homophoneReplacerConfig = offlineRecognizerConfig.hr;
        }
        if ((i2 & 8) != 0) {
            str = offlineRecognizerConfig.decodingMethod;
        }
        if ((i2 & 16) != 0) {
            i = offlineRecognizerConfig.maxActivePaths;
        }
        if ((i2 & 32) != 0) {
            str2 = offlineRecognizerConfig.hotwordsFile;
        }
        if ((i2 & 64) != 0) {
            f = offlineRecognizerConfig.hotwordsScore;
        }
        if ((i2 & 128) != 0) {
            str3 = offlineRecognizerConfig.ruleFsts;
        }
        if ((i2 & 256) != 0) {
            str4 = offlineRecognizerConfig.ruleFars;
        }
        if ((i2 & 512) != 0) {
            f2 = offlineRecognizerConfig.blankPenalty;
        }
        String str5 = str4;
        float f3 = f2;
        float f4 = f;
        String str6 = str3;
        int i3 = i;
        String str7 = str2;
        return offlineRecognizerConfig.copy(featureConfig, offlineModelConfig, homophoneReplacerConfig, str, i3, str7, f4, str6, str5, f3);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final FeatureConfig getFeatConfig() {
        return this.featConfig;
    }

    /* JADX INFO: renamed from: component10, reason: from getter */
    public final float getBlankPenalty() {
        return this.blankPenalty;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final OfflineModelConfig getModelConfig() {
        return this.modelConfig;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final HomophoneReplacerConfig getHr() {
        return this.hr;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final String getDecodingMethod() {
        return this.decodingMethod;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final int getMaxActivePaths() {
        return this.maxActivePaths;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final String getHotwordsFile() {
        return this.hotwordsFile;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final float getHotwordsScore() {
        return this.hotwordsScore;
    }

    /* JADX INFO: renamed from: component8, reason: from getter */
    public final String getRuleFsts() {
        return this.ruleFsts;
    }

    /* JADX INFO: renamed from: component9, reason: from getter */
    public final String getRuleFars() {
        return this.ruleFars;
    }

    public final OfflineRecognizerConfig copy(FeatureConfig featConfig, OfflineModelConfig modelConfig, HomophoneReplacerConfig hr, String decodingMethod, int maxActivePaths, String hotwordsFile, float hotwordsScore, String ruleFsts, String ruleFars, float blankPenalty) {
        Intrinsics.checkNotNullParameter(featConfig, "featConfig");
        Intrinsics.checkNotNullParameter(modelConfig, "modelConfig");
        Intrinsics.checkNotNullParameter(hr, "hr");
        Intrinsics.checkNotNullParameter(decodingMethod, "decodingMethod");
        Intrinsics.checkNotNullParameter(hotwordsFile, "hotwordsFile");
        Intrinsics.checkNotNullParameter(ruleFsts, "ruleFsts");
        Intrinsics.checkNotNullParameter(ruleFars, "ruleFars");
        return new OfflineRecognizerConfig(featConfig, modelConfig, hr, decodingMethod, maxActivePaths, hotwordsFile, hotwordsScore, ruleFsts, ruleFars, blankPenalty);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineRecognizerConfig)) {
            return false;
        }
        OfflineRecognizerConfig offlineRecognizerConfig = (OfflineRecognizerConfig) other;
        return Intrinsics.areEqual(this.featConfig, offlineRecognizerConfig.featConfig) && Intrinsics.areEqual(this.modelConfig, offlineRecognizerConfig.modelConfig) && Intrinsics.areEqual(this.hr, offlineRecognizerConfig.hr) && Intrinsics.areEqual(this.decodingMethod, offlineRecognizerConfig.decodingMethod) && this.maxActivePaths == offlineRecognizerConfig.maxActivePaths && Intrinsics.areEqual(this.hotwordsFile, offlineRecognizerConfig.hotwordsFile) && Float.compare(this.hotwordsScore, offlineRecognizerConfig.hotwordsScore) == 0 && Intrinsics.areEqual(this.ruleFsts, offlineRecognizerConfig.ruleFsts) && Intrinsics.areEqual(this.ruleFars, offlineRecognizerConfig.ruleFars) && Float.compare(this.blankPenalty, offlineRecognizerConfig.blankPenalty) == 0;
    }

    public int hashCode() {
        return (((((((((((((((((this.featConfig.hashCode() * 31) + this.modelConfig.hashCode()) * 31) + this.hr.hashCode()) * 31) + this.decodingMethod.hashCode()) * 31) + Integer.hashCode(this.maxActivePaths)) * 31) + this.hotwordsFile.hashCode()) * 31) + Float.hashCode(this.hotwordsScore)) * 31) + this.ruleFsts.hashCode()) * 31) + this.ruleFars.hashCode()) * 31) + Float.hashCode(this.blankPenalty);
    }

    public String toString() {
        return "OfflineRecognizerConfig(featConfig=" + this.featConfig + ", modelConfig=" + this.modelConfig + ", hr=" + this.hr + ", decodingMethod=" + this.decodingMethod + ", maxActivePaths=" + this.maxActivePaths + ", hotwordsFile=" + this.hotwordsFile + ", hotwordsScore=" + this.hotwordsScore + ", ruleFsts=" + this.ruleFsts + ", ruleFars=" + this.ruleFars + ", blankPenalty=" + this.blankPenalty + ')';
    }

    public OfflineRecognizerConfig(FeatureConfig featConfig, OfflineModelConfig modelConfig, HomophoneReplacerConfig hr, String decodingMethod, int i, String hotwordsFile, float f, String ruleFsts, String ruleFars, float f2) {
        Intrinsics.checkNotNullParameter(featConfig, "featConfig");
        Intrinsics.checkNotNullParameter(modelConfig, "modelConfig");
        Intrinsics.checkNotNullParameter(hr, "hr");
        Intrinsics.checkNotNullParameter(decodingMethod, "decodingMethod");
        Intrinsics.checkNotNullParameter(hotwordsFile, "hotwordsFile");
        Intrinsics.checkNotNullParameter(ruleFsts, "ruleFsts");
        Intrinsics.checkNotNullParameter(ruleFars, "ruleFars");
        this.featConfig = featConfig;
        this.modelConfig = modelConfig;
        this.hr = hr;
        this.decodingMethod = decodingMethod;
        this.maxActivePaths = i;
        this.hotwordsFile = hotwordsFile;
        this.hotwordsScore = f;
        this.ruleFsts = ruleFsts;
        this.ruleFars = ruleFars;
        this.blankPenalty = f2;
    }

    public /* synthetic */ OfflineRecognizerConfig(FeatureConfig featureConfig, OfflineModelConfig offlineModelConfig, HomophoneReplacerConfig homophoneReplacerConfig, String str, int i, String str2, float f, String str3, String str4, float f2, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? new FeatureConfig(0, 0, 0.0f, 7, null) : featureConfig, (i2 & 2) != 0 ? new OfflineModelConfig(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, false, null, null, null, null, null, 33554431, null) : offlineModelConfig, (i2 & 4) != 0 ? new HomophoneReplacerConfig(null, null, null, 7, null) : homophoneReplacerConfig, (i2 & 8) != 0 ? "greedy_search" : str, (i2 & 16) != 0 ? 4 : i, (i2 & 32) != 0 ? "" : str2, (i2 & 64) != 0 ? 1.5f : f, (i2 & 128) != 0 ? "" : str3, (i2 & 256) == 0 ? str4 : "", (i2 & 512) != 0 ? 0.0f : f2);
    }

    public final FeatureConfig getFeatConfig() {
        return this.featConfig;
    }

    public final void setFeatConfig(FeatureConfig featureConfig) {
        Intrinsics.checkNotNullParameter(featureConfig, "<set-?>");
        this.featConfig = featureConfig;
    }

    public final OfflineModelConfig getModelConfig() {
        return this.modelConfig;
    }

    public final void setModelConfig(OfflineModelConfig offlineModelConfig) {
        Intrinsics.checkNotNullParameter(offlineModelConfig, "<set-?>");
        this.modelConfig = offlineModelConfig;
    }

    public final HomophoneReplacerConfig getHr() {
        return this.hr;
    }

    public final void setHr(HomophoneReplacerConfig homophoneReplacerConfig) {
        Intrinsics.checkNotNullParameter(homophoneReplacerConfig, "<set-?>");
        this.hr = homophoneReplacerConfig;
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
