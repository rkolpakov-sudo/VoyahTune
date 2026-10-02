package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Tts.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b!\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BU\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\t\u0012\b\b\u0002\u0010\u000b\u001a\u00020\t¢\u0006\u0002\u0010\fJ\t\u0010!\u001a\u00020\u0003HÆ\u0003J\t\u0010\"\u001a\u00020\u0003HÆ\u0003J\t\u0010#\u001a\u00020\u0003HÆ\u0003J\t\u0010$\u001a\u00020\u0003HÆ\u0003J\t\u0010%\u001a\u00020\u0003HÆ\u0003J\t\u0010&\u001a\u00020\tHÆ\u0003J\t\u0010'\u001a\u00020\tHÆ\u0003J\t\u0010(\u001a\u00020\tHÆ\u0003JY\u0010)\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\t2\b\b\u0002\u0010\u000b\u001a\u00020\tHÆ\u0001J\u0013\u0010*\u001a\u00020+2\b\u0010,\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010-\u001a\u00020.HÖ\u0001J\t\u0010/\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0006\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0007\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u000e\"\u0004\b\u0012\u0010\u0010R\u001a\u0010\u000b\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u000e\"\u0004\b\u0018\u0010\u0010R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u000e\"\u0004\b\u001a\u0010\u0010R\u001a\u0010\b\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u0014\"\u0004\b\u001c\u0010\u0016R\u001a\u0010\n\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u0014\"\u0004\b\u001e\u0010\u0016R\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010\u000e\"\u0004\b \u0010\u0010¨\u00060"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineTtsVitsModelConfig;", "", "model", "", "lexicon", "tokens", "dataDir", "dictDir", "noiseScale", "", "noiseScaleW", "lengthScale", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;FFF)V", "getDataDir", "()Ljava/lang/String;", "setDataDir", "(Ljava/lang/String;)V", "getDictDir", "setDictDir", "getLengthScale", "()F", "setLengthScale", "(F)V", "getLexicon", "setLexicon", "getModel", "setModel", "getNoiseScale", "setNoiseScale", "getNoiseScaleW", "setNoiseScaleW", "getTokens", "setTokens", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "", "other", "hashCode", "", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class OfflineTtsVitsModelConfig {
    private String dataDir;
    private String dictDir;
    private float lengthScale;
    private String lexicon;
    private String model;
    private float noiseScale;
    private float noiseScaleW;
    private String tokens;

    public OfflineTtsVitsModelConfig() {
        this(null, null, null, null, null, 0.0f, 0.0f, 0.0f, 255, null);
    }

    public static /* synthetic */ OfflineTtsVitsModelConfig copy$default(OfflineTtsVitsModelConfig offlineTtsVitsModelConfig, String str, String str2, String str3, String str4, String str5, float f, float f2, float f3, int i, Object obj) {
        if ((i & 1) != 0) {
            str = offlineTtsVitsModelConfig.model;
        }
        if ((i & 2) != 0) {
            str2 = offlineTtsVitsModelConfig.lexicon;
        }
        if ((i & 4) != 0) {
            str3 = offlineTtsVitsModelConfig.tokens;
        }
        if ((i & 8) != 0) {
            str4 = offlineTtsVitsModelConfig.dataDir;
        }
        if ((i & 16) != 0) {
            str5 = offlineTtsVitsModelConfig.dictDir;
        }
        if ((i & 32) != 0) {
            f = offlineTtsVitsModelConfig.noiseScale;
        }
        if ((i & 64) != 0) {
            f2 = offlineTtsVitsModelConfig.noiseScaleW;
        }
        if ((i & 128) != 0) {
            f3 = offlineTtsVitsModelConfig.lengthScale;
        }
        float f4 = f2;
        float f5 = f3;
        String str6 = str5;
        float f6 = f;
        return offlineTtsVitsModelConfig.copy(str, str2, str3, str4, str6, f6, f4, f5);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String getModel() {
        return this.model;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String getLexicon() {
        return this.lexicon;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final String getTokens() {
        return this.tokens;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final String getDataDir() {
        return this.dataDir;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final String getDictDir() {
        return this.dictDir;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final float getNoiseScale() {
        return this.noiseScale;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final float getNoiseScaleW() {
        return this.noiseScaleW;
    }

    /* JADX INFO: renamed from: component8, reason: from getter */
    public final float getLengthScale() {
        return this.lengthScale;
    }

    public final OfflineTtsVitsModelConfig copy(String model, String lexicon, String tokens, String dataDir, String dictDir, float noiseScale, float noiseScaleW, float lengthScale) {
        Intrinsics.checkNotNullParameter(model, "model");
        Intrinsics.checkNotNullParameter(lexicon, "lexicon");
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        Intrinsics.checkNotNullParameter(dataDir, "dataDir");
        Intrinsics.checkNotNullParameter(dictDir, "dictDir");
        return new OfflineTtsVitsModelConfig(model, lexicon, tokens, dataDir, dictDir, noiseScale, noiseScaleW, lengthScale);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineTtsVitsModelConfig)) {
            return false;
        }
        OfflineTtsVitsModelConfig offlineTtsVitsModelConfig = (OfflineTtsVitsModelConfig) other;
        return Intrinsics.areEqual(this.model, offlineTtsVitsModelConfig.model) && Intrinsics.areEqual(this.lexicon, offlineTtsVitsModelConfig.lexicon) && Intrinsics.areEqual(this.tokens, offlineTtsVitsModelConfig.tokens) && Intrinsics.areEqual(this.dataDir, offlineTtsVitsModelConfig.dataDir) && Intrinsics.areEqual(this.dictDir, offlineTtsVitsModelConfig.dictDir) && Float.compare(this.noiseScale, offlineTtsVitsModelConfig.noiseScale) == 0 && Float.compare(this.noiseScaleW, offlineTtsVitsModelConfig.noiseScaleW) == 0 && Float.compare(this.lengthScale, offlineTtsVitsModelConfig.lengthScale) == 0;
    }

    public int hashCode() {
        return (((((((((((((this.model.hashCode() * 31) + this.lexicon.hashCode()) * 31) + this.tokens.hashCode()) * 31) + this.dataDir.hashCode()) * 31) + this.dictDir.hashCode()) * 31) + Float.hashCode(this.noiseScale)) * 31) + Float.hashCode(this.noiseScaleW)) * 31) + Float.hashCode(this.lengthScale);
    }

    public String toString() {
        return "OfflineTtsVitsModelConfig(model=" + this.model + ", lexicon=" + this.lexicon + ", tokens=" + this.tokens + ", dataDir=" + this.dataDir + ", dictDir=" + this.dictDir + ", noiseScale=" + this.noiseScale + ", noiseScaleW=" + this.noiseScaleW + ", lengthScale=" + this.lengthScale + ')';
    }

    public OfflineTtsVitsModelConfig(String model, String lexicon, String tokens, String dataDir, String dictDir, float f, float f2, float f3) {
        Intrinsics.checkNotNullParameter(model, "model");
        Intrinsics.checkNotNullParameter(lexicon, "lexicon");
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        Intrinsics.checkNotNullParameter(dataDir, "dataDir");
        Intrinsics.checkNotNullParameter(dictDir, "dictDir");
        this.model = model;
        this.lexicon = lexicon;
        this.tokens = tokens;
        this.dataDir = dataDir;
        this.dictDir = dictDir;
        this.noiseScale = f;
        this.noiseScaleW = f2;
        this.lengthScale = f3;
    }

    public /* synthetic */ OfflineTtsVitsModelConfig(String str, String str2, String str3, String str4, String str5, float f, float f2, float f3, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? "" : str, (i & 2) != 0 ? "" : str2, (i & 4) != 0 ? "" : str3, (i & 8) != 0 ? "" : str4, (i & 16) != 0 ? "" : str5, (i & 32) != 0 ? 0.667f : f, (i & 64) != 0 ? 0.8f : f2, (i & 128) != 0 ? 1.0f : f3);
    }

    public final String getModel() {
        return this.model;
    }

    public final void setModel(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.model = str;
    }

    public final String getLexicon() {
        return this.lexicon;
    }

    public final void setLexicon(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.lexicon = str;
    }

    public final String getTokens() {
        return this.tokens;
    }

    public final void setTokens(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.tokens = str;
    }

    public final String getDataDir() {
        return this.dataDir;
    }

    public final void setDataDir(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.dataDir = str;
    }

    public final String getDictDir() {
        return this.dictDir;
    }

    public final void setDictDir(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.dictDir = str;
    }

    public final float getNoiseScale() {
        return this.noiseScale;
    }

    public final void setNoiseScale(float f) {
        this.noiseScale = f;
    }

    public final float getNoiseScaleW() {
        return this.noiseScaleW;
    }

    public final void setNoiseScaleW(float f) {
        this.noiseScaleW = f;
    }

    public final float getLengthScale() {
        return this.lengthScale;
    }

    public final void setLengthScale(float f) {
        this.lengthScale = f;
    }
}
