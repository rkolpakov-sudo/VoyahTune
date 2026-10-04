package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Tts.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u0007\n\u0002\b \n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BU\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\n¢\u0006\u0002\u0010\fJ\t\u0010!\u001a\u00020\u0003HÆ\u0003J\t\u0010\"\u001a\u00020\u0003HÆ\u0003J\t\u0010#\u001a\u00020\u0003HÆ\u0003J\t\u0010$\u001a\u00020\u0003HÆ\u0003J\t\u0010%\u001a\u00020\u0003HÆ\u0003J\t\u0010&\u001a\u00020\u0003HÆ\u0003J\t\u0010'\u001a\u00020\nHÆ\u0003J\t\u0010(\u001a\u00020\nHÆ\u0003JY\u0010)\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\nHÆ\u0001J\u0013\u0010*\u001a\u00020+2\b\u0010,\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010-\u001a\u00020.HÖ\u0001J\t\u0010/\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0007\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u000e\"\u0004\b\u0012\u0010\u0010R\u001a\u0010\b\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u000e\"\u0004\b\u0014\u0010\u0010R\u001a\u0010\u000b\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u000e\"\u0004\b\u001a\u0010\u0010R\u001a\u0010\t\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u0016\"\u0004\b\u001c\u0010\u0018R\u001a\u0010\u0006\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u000e\"\u0004\b\u001e\u0010\u0010R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010\u000e\"\u0004\b \u0010\u0010¨\u00060"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineTtsMatchaModelConfig;", "", "acousticModel", "", "vocoder", "lexicon", "tokens", "dataDir", "dictDir", "noiseScale", "", "lengthScale", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;FF)V", "getAcousticModel", "()Ljava/lang/String;", "setAcousticModel", "(Ljava/lang/String;)V", "getDataDir", "setDataDir", "getDictDir", "setDictDir", "getLengthScale", "()F", "setLengthScale", "(F)V", "getLexicon", "setLexicon", "getNoiseScale", "setNoiseScale", "getTokens", "setTokens", "getVocoder", "setVocoder", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "", "other", "hashCode", "", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class OfflineTtsMatchaModelConfig {
    private String acousticModel;
    private String dataDir;
    private String dictDir;
    private float lengthScale;
    private String lexicon;
    private float noiseScale;
    private String tokens;
    private String vocoder;

    public OfflineTtsMatchaModelConfig() {
        this(null, null, null, null, null, null, 0.0f, 0.0f, 255, null);
    }

    public static /* synthetic */ OfflineTtsMatchaModelConfig copy$default(OfflineTtsMatchaModelConfig offlineTtsMatchaModelConfig, String str, String str2, String str3, String str4, String str5, String str6, float f, float f2, int i, Object obj) {
        if ((i & 1) != 0) {
            str = offlineTtsMatchaModelConfig.acousticModel;
        }
        if ((i & 2) != 0) {
            str2 = offlineTtsMatchaModelConfig.vocoder;
        }
        if ((i & 4) != 0) {
            str3 = offlineTtsMatchaModelConfig.lexicon;
        }
        if ((i & 8) != 0) {
            str4 = offlineTtsMatchaModelConfig.tokens;
        }
        if ((i & 16) != 0) {
            str5 = offlineTtsMatchaModelConfig.dataDir;
        }
        if ((i & 32) != 0) {
            str6 = offlineTtsMatchaModelConfig.dictDir;
        }
        if ((i & 64) != 0) {
            f = offlineTtsMatchaModelConfig.noiseScale;
        }
        if ((i & 128) != 0) {
            f2 = offlineTtsMatchaModelConfig.lengthScale;
        }
        float f3 = f;
        float f4 = f2;
        String str7 = str5;
        String str8 = str6;
        return offlineTtsMatchaModelConfig.copy(str, str2, str3, str4, str7, str8, f3, f4);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String component1() {
        return this.acousticModel;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String component2() {
        return this.vocoder;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final String component3() {
        return this.lexicon;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final String component4() {
        return this.tokens;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final String component5() {
        return this.dataDir;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final String component6() {
        return this.dictDir;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final float component7() {
        return this.noiseScale;
    }

    /* JADX INFO: renamed from: component8, reason: from getter */
    public final float component8() {
        return this.lengthScale;
    }

    public final OfflineTtsMatchaModelConfig copy(String acousticModel, String vocoder, String lexicon, String tokens, String dataDir, String dictDir, float noiseScale, float lengthScale) {
        Intrinsics.checkNotNullParameter(acousticModel, "acousticModel");
        Intrinsics.checkNotNullParameter(vocoder, "vocoder");
        Intrinsics.checkNotNullParameter(lexicon, "lexicon");
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        Intrinsics.checkNotNullParameter(dataDir, "dataDir");
        Intrinsics.checkNotNullParameter(dictDir, "dictDir");
        return new OfflineTtsMatchaModelConfig(acousticModel, vocoder, lexicon, tokens, dataDir, dictDir, noiseScale, lengthScale);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineTtsMatchaModelConfig)) {
            return false;
        }
        OfflineTtsMatchaModelConfig offlineTtsMatchaModelConfig = (OfflineTtsMatchaModelConfig) other;
        return Intrinsics.areEqual(this.acousticModel, offlineTtsMatchaModelConfig.acousticModel) && Intrinsics.areEqual(this.vocoder, offlineTtsMatchaModelConfig.vocoder) && Intrinsics.areEqual(this.lexicon, offlineTtsMatchaModelConfig.lexicon) && Intrinsics.areEqual(this.tokens, offlineTtsMatchaModelConfig.tokens) && Intrinsics.areEqual(this.dataDir, offlineTtsMatchaModelConfig.dataDir) && Intrinsics.areEqual(this.dictDir, offlineTtsMatchaModelConfig.dictDir) && Float.compare(this.noiseScale, offlineTtsMatchaModelConfig.noiseScale) == 0 && Float.compare(this.lengthScale, offlineTtsMatchaModelConfig.lengthScale) == 0;
    }

    public int hashCode() {
        return (((((((((((((this.acousticModel.hashCode() * 31) + this.vocoder.hashCode()) * 31) + this.lexicon.hashCode()) * 31) + this.tokens.hashCode()) * 31) + this.dataDir.hashCode()) * 31) + this.dictDir.hashCode()) * 31) + Float.hashCode(this.noiseScale)) * 31) + Float.hashCode(this.lengthScale);
    }

    public String toString() {
        return "OfflineTtsMatchaModelConfig(acousticModel=" + this.acousticModel + ", vocoder=" + this.vocoder + ", lexicon=" + this.lexicon + ", tokens=" + this.tokens + ", dataDir=" + this.dataDir + ", dictDir=" + this.dictDir + ", noiseScale=" + this.noiseScale + ", lengthScale=" + this.lengthScale + ')';
    }

    public OfflineTtsMatchaModelConfig(String acousticModel, String vocoder, String lexicon, String tokens, String dataDir, String dictDir, float f, float f2) {
        Intrinsics.checkNotNullParameter(acousticModel, "acousticModel");
        Intrinsics.checkNotNullParameter(vocoder, "vocoder");
        Intrinsics.checkNotNullParameter(lexicon, "lexicon");
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        Intrinsics.checkNotNullParameter(dataDir, "dataDir");
        Intrinsics.checkNotNullParameter(dictDir, "dictDir");
        this.acousticModel = acousticModel;
        this.vocoder = vocoder;
        this.lexicon = lexicon;
        this.tokens = tokens;
        this.dataDir = dataDir;
        this.dictDir = dictDir;
        this.noiseScale = f;
        this.lengthScale = f2;
    }

    public /* synthetic */ OfflineTtsMatchaModelConfig(String str, String str2, String str3, String str4, String str5, String str6, float f, float f2, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? "" : str, (i & 2) != 0 ? "" : str2, (i & 4) != 0 ? "" : str3, (i & 8) != 0 ? "" : str4, (i & 16) != 0 ? "" : str5, (i & 32) != 0 ? "" : str6, (i & 64) != 0 ? 1.0f : f, (i & 128) != 0 ? 1.0f : f2);
    }

    public final String getAcousticModel() {
        return this.acousticModel;
    }

    public final void setAcousticModel(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.acousticModel = str;
    }

    public final String getVocoder() {
        return this.vocoder;
    }

    public final void setVocoder(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.vocoder = str;
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

    public final float getLengthScale() {
        return this.lengthScale;
    }

    public final void setLengthScale(float f) {
        this.lengthScale = f;
    }
}
