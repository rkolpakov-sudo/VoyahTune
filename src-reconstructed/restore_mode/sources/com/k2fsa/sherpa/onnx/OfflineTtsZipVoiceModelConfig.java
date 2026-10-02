package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Tts.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u0007\n\u0002\b(\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001Bi\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\n\u0012\b\b\u0002\u0010\f\u001a\u00020\n\u0012\b\b\u0002\u0010\r\u001a\u00020\n¢\u0006\u0002\u0010\u000eJ\t\u0010'\u001a\u00020\u0003HÆ\u0003J\t\u0010(\u001a\u00020\nHÆ\u0003J\t\u0010)\u001a\u00020\u0003HÆ\u0003J\t\u0010*\u001a\u00020\u0003HÆ\u0003J\t\u0010+\u001a\u00020\u0003HÆ\u0003J\t\u0010,\u001a\u00020\u0003HÆ\u0003J\t\u0010-\u001a\u00020\u0003HÆ\u0003J\t\u0010.\u001a\u00020\nHÆ\u0003J\t\u0010/\u001a\u00020\nHÆ\u0003J\t\u00100\u001a\u00020\nHÆ\u0003Jm\u00101\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\f\u001a\u00020\n2\b\b\u0002\u0010\r\u001a\u00020\nHÆ\u0001J\u0013\u00102\u001a\u0002032\b\u00104\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u00105\u001a\u000206HÖ\u0001J\t\u00107\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0007\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0010\"\u0004\b\u0014\u0010\u0012R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0010\"\u0004\b\u0016\u0010\u0012R\u001a\u0010\t\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001aR\u001a\u0010\r\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u0018\"\u0004\b\u001c\u0010\u001aR\u001a\u0010\b\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u0010\"\u0004\b\u001e\u0010\u0012R\u001a\u0010\u000b\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010\u0018\"\u0004\b \u0010\u001aR\u001a\u0010\f\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\u0018\"\u0004\b\"\u0010\u001aR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b#\u0010\u0010\"\u0004\b$\u0010\u0012R\u001a\u0010\u0006\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b%\u0010\u0010\"\u0004\b&\u0010\u0012¨\u00068"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineTtsZipVoiceModelConfig;", "", "tokens", "", "encoder", "decoder", "vocoder", "dataDir", "lexicon", "featScale", "", "tShift", "targetRms", "guidanceScale", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;FFFF)V", "getDataDir", "()Ljava/lang/String;", "setDataDir", "(Ljava/lang/String;)V", "getDecoder", "setDecoder", "getEncoder", "setEncoder", "getFeatScale", "()F", "setFeatScale", "(F)V", "getGuidanceScale", "setGuidanceScale", "getLexicon", "setLexicon", "getTShift", "setTShift", "getTargetRms", "setTargetRms", "getTokens", "setTokens", "getVocoder", "setVocoder", "component1", "component10", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class OfflineTtsZipVoiceModelConfig {
    private String dataDir;
    private String decoder;
    private String encoder;
    private float featScale;
    private float guidanceScale;
    private String lexicon;
    private float tShift;
    private float targetRms;
    private String tokens;
    private String vocoder;

    public OfflineTtsZipVoiceModelConfig() {
        this(null, null, null, null, null, null, 0.0f, 0.0f, 0.0f, 0.0f, 1023, null);
    }

    public static /* synthetic */ OfflineTtsZipVoiceModelConfig copy$default(OfflineTtsZipVoiceModelConfig offlineTtsZipVoiceModelConfig, String str, String str2, String str3, String str4, String str5, String str6, float f, float f2, float f3, float f4, int i, Object obj) {
        if ((i & 1) != 0) {
            str = offlineTtsZipVoiceModelConfig.tokens;
        }
        if ((i & 2) != 0) {
            str2 = offlineTtsZipVoiceModelConfig.encoder;
        }
        if ((i & 4) != 0) {
            str3 = offlineTtsZipVoiceModelConfig.decoder;
        }
        if ((i & 8) != 0) {
            str4 = offlineTtsZipVoiceModelConfig.vocoder;
        }
        if ((i & 16) != 0) {
            str5 = offlineTtsZipVoiceModelConfig.dataDir;
        }
        if ((i & 32) != 0) {
            str6 = offlineTtsZipVoiceModelConfig.lexicon;
        }
        if ((i & 64) != 0) {
            f = offlineTtsZipVoiceModelConfig.featScale;
        }
        if ((i & 128) != 0) {
            f2 = offlineTtsZipVoiceModelConfig.tShift;
        }
        if ((i & 256) != 0) {
            f3 = offlineTtsZipVoiceModelConfig.targetRms;
        }
        if ((i & 512) != 0) {
            f4 = offlineTtsZipVoiceModelConfig.guidanceScale;
        }
        float f5 = f3;
        float f6 = f4;
        float f7 = f;
        float f8 = f2;
        String str7 = str5;
        String str8 = str6;
        return offlineTtsZipVoiceModelConfig.copy(str, str2, str3, str4, str7, str8, f7, f8, f5, f6);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String getTokens() {
        return this.tokens;
    }

    /* JADX INFO: renamed from: component10, reason: from getter */
    public final float getGuidanceScale() {
        return this.guidanceScale;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String getEncoder() {
        return this.encoder;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final String getDecoder() {
        return this.decoder;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final String getVocoder() {
        return this.vocoder;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final String getDataDir() {
        return this.dataDir;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final String getLexicon() {
        return this.lexicon;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final float getFeatScale() {
        return this.featScale;
    }

    /* JADX INFO: renamed from: component8, reason: from getter */
    public final float getTShift() {
        return this.tShift;
    }

    /* JADX INFO: renamed from: component9, reason: from getter */
    public final float getTargetRms() {
        return this.targetRms;
    }

    public final OfflineTtsZipVoiceModelConfig copy(String tokens, String encoder, String decoder, String vocoder, String dataDir, String lexicon, float featScale, float tShift, float targetRms, float guidanceScale) {
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        Intrinsics.checkNotNullParameter(encoder, "encoder");
        Intrinsics.checkNotNullParameter(decoder, "decoder");
        Intrinsics.checkNotNullParameter(vocoder, "vocoder");
        Intrinsics.checkNotNullParameter(dataDir, "dataDir");
        Intrinsics.checkNotNullParameter(lexicon, "lexicon");
        return new OfflineTtsZipVoiceModelConfig(tokens, encoder, decoder, vocoder, dataDir, lexicon, featScale, tShift, targetRms, guidanceScale);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineTtsZipVoiceModelConfig)) {
            return false;
        }
        OfflineTtsZipVoiceModelConfig offlineTtsZipVoiceModelConfig = (OfflineTtsZipVoiceModelConfig) other;
        return Intrinsics.areEqual(this.tokens, offlineTtsZipVoiceModelConfig.tokens) && Intrinsics.areEqual(this.encoder, offlineTtsZipVoiceModelConfig.encoder) && Intrinsics.areEqual(this.decoder, offlineTtsZipVoiceModelConfig.decoder) && Intrinsics.areEqual(this.vocoder, offlineTtsZipVoiceModelConfig.vocoder) && Intrinsics.areEqual(this.dataDir, offlineTtsZipVoiceModelConfig.dataDir) && Intrinsics.areEqual(this.lexicon, offlineTtsZipVoiceModelConfig.lexicon) && Float.compare(this.featScale, offlineTtsZipVoiceModelConfig.featScale) == 0 && Float.compare(this.tShift, offlineTtsZipVoiceModelConfig.tShift) == 0 && Float.compare(this.targetRms, offlineTtsZipVoiceModelConfig.targetRms) == 0 && Float.compare(this.guidanceScale, offlineTtsZipVoiceModelConfig.guidanceScale) == 0;
    }

    public int hashCode() {
        return (((((((((((((((((this.tokens.hashCode() * 31) + this.encoder.hashCode()) * 31) + this.decoder.hashCode()) * 31) + this.vocoder.hashCode()) * 31) + this.dataDir.hashCode()) * 31) + this.lexicon.hashCode()) * 31) + Float.hashCode(this.featScale)) * 31) + Float.hashCode(this.tShift)) * 31) + Float.hashCode(this.targetRms)) * 31) + Float.hashCode(this.guidanceScale);
    }

    public String toString() {
        return "OfflineTtsZipVoiceModelConfig(tokens=" + this.tokens + ", encoder=" + this.encoder + ", decoder=" + this.decoder + ", vocoder=" + this.vocoder + ", dataDir=" + this.dataDir + ", lexicon=" + this.lexicon + ", featScale=" + this.featScale + ", tShift=" + this.tShift + ", targetRms=" + this.targetRms + ", guidanceScale=" + this.guidanceScale + ')';
    }

    public OfflineTtsZipVoiceModelConfig(String tokens, String encoder, String decoder, String vocoder, String dataDir, String lexicon, float f, float f2, float f3, float f4) {
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        Intrinsics.checkNotNullParameter(encoder, "encoder");
        Intrinsics.checkNotNullParameter(decoder, "decoder");
        Intrinsics.checkNotNullParameter(vocoder, "vocoder");
        Intrinsics.checkNotNullParameter(dataDir, "dataDir");
        Intrinsics.checkNotNullParameter(lexicon, "lexicon");
        this.tokens = tokens;
        this.encoder = encoder;
        this.decoder = decoder;
        this.vocoder = vocoder;
        this.dataDir = dataDir;
        this.lexicon = lexicon;
        this.featScale = f;
        this.tShift = f2;
        this.targetRms = f3;
        this.guidanceScale = f4;
    }

    public /* synthetic */ OfflineTtsZipVoiceModelConfig(String str, String str2, String str3, String str4, String str5, String str6, float f, float f2, float f3, float f4, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? "" : str, (i & 2) != 0 ? "" : str2, (i & 4) != 0 ? "" : str3, (i & 8) != 0 ? "" : str4, (i & 16) != 0 ? "" : str5, (i & 32) != 0 ? "" : str6, (i & 64) != 0 ? 0.1f : f, (i & 128) != 0 ? 0.5f : f2, (i & 256) != 0 ? 0.1f : f3, (i & 512) != 0 ? 1.0f : f4);
    }

    public final String getTokens() {
        return this.tokens;
    }

    public final void setTokens(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.tokens = str;
    }

    public final String getEncoder() {
        return this.encoder;
    }

    public final void setEncoder(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.encoder = str;
    }

    public final String getDecoder() {
        return this.decoder;
    }

    public final void setDecoder(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.decoder = str;
    }

    public final String getVocoder() {
        return this.vocoder;
    }

    public final void setVocoder(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.vocoder = str;
    }

    public final String getDataDir() {
        return this.dataDir;
    }

    public final void setDataDir(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.dataDir = str;
    }

    public final String getLexicon() {
        return this.lexicon;
    }

    public final void setLexicon(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.lexicon = str;
    }

    public final float getFeatScale() {
        return this.featScale;
    }

    public final void setFeatScale(float f) {
        this.featScale = f;
    }

    public final float getTShift() {
        return this.tShift;
    }

    public final void setTShift(float f) {
        this.tShift = f;
    }

    public final float getTargetRms() {
        return this.targetRms;
    }

    public final void setTargetRms(float f) {
        this.targetRms = f;
    }

    public final float getGuidanceScale() {
        return this.guidanceScale;
    }

    public final void setGuidanceScale(float f) {
        this.guidanceScale = f;
    }
}
