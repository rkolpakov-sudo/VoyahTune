package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.scheduling.WorkQueueKt;

/* JADX INFO: compiled from: KeywordSpotter.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b$\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001BK\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\u000b\u0012\b\b\u0002\u0010\r\u001a\u00020\u0007¢\u0006\u0002\u0010\u000eJ\t\u0010'\u001a\u00020\u0003HÆ\u0003J\t\u0010(\u001a\u00020\u0005HÆ\u0003J\t\u0010)\u001a\u00020\u0007HÆ\u0003J\t\u0010*\u001a\u00020\tHÆ\u0003J\t\u0010+\u001a\u00020\u000bHÆ\u0003J\t\u0010,\u001a\u00020\u000bHÆ\u0003J\t\u0010-\u001a\u00020\u0007HÆ\u0003JO\u0010.\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\r\u001a\u00020\u0007HÆ\u0001J\u0013\u0010/\u001a\u0002002\b\u00101\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u00102\u001a\u00020\u0007HÖ\u0001J\t\u00103\u001a\u00020\tHÖ\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001a\u0010\b\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001aR\u001a\u0010\f\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u0018\"\u0004\b\u001c\u0010\u001aR\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u001e\"\u0004\b\u001f\u0010 R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\"\"\u0004\b#\u0010$R\u001a\u0010\r\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b%\u0010\u001e\"\u0004\b&\u0010 ¨\u00064"}, d2 = {"Lcom/k2fsa/sherpa/onnx/KeywordSpotterConfig;", "", "featConfig", "Lcom/k2fsa/sherpa/onnx/FeatureConfig;", "modelConfig", "Lcom/k2fsa/sherpa/onnx/OnlineModelConfig;", "maxActivePaths", "", "keywordsFile", "", "keywordsScore", "", "keywordsThreshold", "numTrailingBlanks", "(Lcom/k2fsa/sherpa/onnx/FeatureConfig;Lcom/k2fsa/sherpa/onnx/OnlineModelConfig;ILjava/lang/String;FFI)V", "getFeatConfig", "()Lcom/k2fsa/sherpa/onnx/FeatureConfig;", "setFeatConfig", "(Lcom/k2fsa/sherpa/onnx/FeatureConfig;)V", "getKeywordsFile", "()Ljava/lang/String;", "setKeywordsFile", "(Ljava/lang/String;)V", "getKeywordsScore", "()F", "setKeywordsScore", "(F)V", "getKeywordsThreshold", "setKeywordsThreshold", "getMaxActivePaths", "()I", "setMaxActivePaths", "(I)V", "getModelConfig", "()Lcom/k2fsa/sherpa/onnx/OnlineModelConfig;", "setModelConfig", "(Lcom/k2fsa/sherpa/onnx/OnlineModelConfig;)V", "getNumTrailingBlanks", "setNumTrailingBlanks", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class KeywordSpotterConfig {
    private FeatureConfig featConfig;
    private String keywordsFile;
    private float keywordsScore;
    private float keywordsThreshold;
    private int maxActivePaths;
    private OnlineModelConfig modelConfig;
    private int numTrailingBlanks;

    public KeywordSpotterConfig() {
        this(null, null, 0, null, 0.0f, 0.0f, 0, WorkQueueKt.MASK, null);
    }

    public static /* synthetic */ KeywordSpotterConfig copy$default(KeywordSpotterConfig keywordSpotterConfig, FeatureConfig featureConfig, OnlineModelConfig onlineModelConfig, int i, String str, float f, float f2, int i2, int i3, Object obj) {
        if ((i3 & 1) != 0) {
            featureConfig = keywordSpotterConfig.featConfig;
        }
        if ((i3 & 2) != 0) {
            onlineModelConfig = keywordSpotterConfig.modelConfig;
        }
        if ((i3 & 4) != 0) {
            i = keywordSpotterConfig.maxActivePaths;
        }
        if ((i3 & 8) != 0) {
            str = keywordSpotterConfig.keywordsFile;
        }
        if ((i3 & 16) != 0) {
            f = keywordSpotterConfig.keywordsScore;
        }
        if ((i3 & 32) != 0) {
            f2 = keywordSpotterConfig.keywordsThreshold;
        }
        if ((i3 & 64) != 0) {
            i2 = keywordSpotterConfig.numTrailingBlanks;
        }
        float f3 = f2;
        int i4 = i2;
        float f4 = f;
        int i5 = i;
        return keywordSpotterConfig.copy(featureConfig, onlineModelConfig, i5, str, f4, f3, i4);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final FeatureConfig component1() {
        return this.featConfig;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final OnlineModelConfig component2() {
        return this.modelConfig;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final int component3() {
        return this.maxActivePaths;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final String component4() {
        return this.keywordsFile;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final float component5() {
        return this.keywordsScore;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final float component6() {
        return this.keywordsThreshold;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final int component7() {
        return this.numTrailingBlanks;
    }

    public final KeywordSpotterConfig copy(FeatureConfig featConfig, OnlineModelConfig modelConfig, int maxActivePaths, String keywordsFile, float keywordsScore, float keywordsThreshold, int numTrailingBlanks) {
        Intrinsics.checkNotNullParameter(featConfig, "featConfig");
        Intrinsics.checkNotNullParameter(modelConfig, "modelConfig");
        Intrinsics.checkNotNullParameter(keywordsFile, "keywordsFile");
        return new KeywordSpotterConfig(featConfig, modelConfig, maxActivePaths, keywordsFile, keywordsScore, keywordsThreshold, numTrailingBlanks);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof KeywordSpotterConfig)) {
            return false;
        }
        KeywordSpotterConfig keywordSpotterConfig = (KeywordSpotterConfig) other;
        return Intrinsics.areEqual(this.featConfig, keywordSpotterConfig.featConfig) && Intrinsics.areEqual(this.modelConfig, keywordSpotterConfig.modelConfig) && this.maxActivePaths == keywordSpotterConfig.maxActivePaths && Intrinsics.areEqual(this.keywordsFile, keywordSpotterConfig.keywordsFile) && Float.compare(this.keywordsScore, keywordSpotterConfig.keywordsScore) == 0 && Float.compare(this.keywordsThreshold, keywordSpotterConfig.keywordsThreshold) == 0 && this.numTrailingBlanks == keywordSpotterConfig.numTrailingBlanks;
    }

    public int hashCode() {
        return (((((((((((this.featConfig.hashCode() * 31) + this.modelConfig.hashCode()) * 31) + Integer.hashCode(this.maxActivePaths)) * 31) + this.keywordsFile.hashCode()) * 31) + Float.hashCode(this.keywordsScore)) * 31) + Float.hashCode(this.keywordsThreshold)) * 31) + Integer.hashCode(this.numTrailingBlanks);
    }

    public String toString() {
        return "KeywordSpotterConfig(featConfig=" + this.featConfig + ", modelConfig=" + this.modelConfig + ", maxActivePaths=" + this.maxActivePaths + ", keywordsFile=" + this.keywordsFile + ", keywordsScore=" + this.keywordsScore + ", keywordsThreshold=" + this.keywordsThreshold + ", numTrailingBlanks=" + this.numTrailingBlanks + ')';
    }

    public KeywordSpotterConfig(FeatureConfig featConfig, OnlineModelConfig modelConfig, int i, String keywordsFile, float f, float f2, int i2) {
        Intrinsics.checkNotNullParameter(featConfig, "featConfig");
        Intrinsics.checkNotNullParameter(modelConfig, "modelConfig");
        Intrinsics.checkNotNullParameter(keywordsFile, "keywordsFile");
        this.featConfig = featConfig;
        this.modelConfig = modelConfig;
        this.maxActivePaths = i;
        this.keywordsFile = keywordsFile;
        this.keywordsScore = f;
        this.keywordsThreshold = f2;
        this.numTrailingBlanks = i2;
    }

    public /* synthetic */ KeywordSpotterConfig(FeatureConfig featureConfig, OnlineModelConfig onlineModelConfig, int i, String str, float f, float f2, int i2, int i3, DefaultConstructorMarker defaultConstructorMarker) {
        this((i3 & 1) != 0 ? new FeatureConfig(0, 0, 0.0f, 7, null) : featureConfig, (i3 & 2) != 0 ? new OnlineModelConfig(null, null, null, null, null, null, 0, false, null, null, null, null, 4095, null) : onlineModelConfig, (i3 & 4) != 0 ? 4 : i, (i3 & 8) != 0 ? "keywords.txt" : str, (i3 & 16) != 0 ? 1.5f : f, (i3 & 32) != 0 ? 0.25f : f2, (i3 & 64) != 0 ? 2 : i2);
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

    public final int getMaxActivePaths() {
        return this.maxActivePaths;
    }

    public final void setMaxActivePaths(int i) {
        this.maxActivePaths = i;
    }

    public final String getKeywordsFile() {
        return this.keywordsFile;
    }

    public final void setKeywordsFile(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.keywordsFile = str;
    }

    public final float getKeywordsScore() {
        return this.keywordsScore;
    }

    public final void setKeywordsScore(float f) {
        this.keywordsScore = f;
    }

    public final float getKeywordsThreshold() {
        return this.keywordsThreshold;
    }

    public final void setKeywordsThreshold(float f) {
        this.keywordsThreshold = f;
    }

    public final int getNumTrailingBlanks() {
        return this.numTrailingBlanks;
    }

    public final void setNumTrailingBlanks(int i) {
        this.numTrailingBlanks = i;
    }
}
