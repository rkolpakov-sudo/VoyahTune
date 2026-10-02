package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Tts.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u001f\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001BU\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\u0003\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b¢\u0006\u0002\u0010\fJ\t\u0010!\u001a\u00020\u0003HÆ\u0003J\t\u0010\"\u001a\u00020\u0003HÆ\u0003J\t\u0010#\u001a\u00020\u0003HÆ\u0003J\t\u0010$\u001a\u00020\u0003HÆ\u0003J\t\u0010%\u001a\u00020\u0003HÆ\u0003J\t\u0010&\u001a\u00020\u0003HÆ\u0003J\t\u0010'\u001a\u00020\u0003HÆ\u0003J\t\u0010(\u001a\u00020\u000bHÆ\u0003JY\u0010)\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u000bHÆ\u0001J\u0013\u0010*\u001a\u00020+2\b\u0010,\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010-\u001a\u00020\u000bHÖ\u0001J\t\u0010.\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0006\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u000e\"\u0004\b\u0012\u0010\u0010R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u000e\"\u0004\b\u0014\u0010\u0010R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u000e\"\u0004\b\u0016\u0010\u0010R\u001a\u0010\u0007\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u000e\"\u0004\b\u0018\u0010\u0010R\u001a\u0010\t\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u000e\"\u0004\b\u001a\u0010\u0010R\u001a\u0010\b\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u000e\"\u0004\b\u001c\u0010\u0010R\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u001e\"\u0004\b\u001f\u0010 ¨\u0006/"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineTtsPocketModelConfig;", "", "lmFlow", "", "lmMain", "encoder", "decoder", "textConditioner", "vocabJson", "tokenScoresJson", "voiceEmbeddingCacheCapacity", "", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V", "getDecoder", "()Ljava/lang/String;", "setDecoder", "(Ljava/lang/String;)V", "getEncoder", "setEncoder", "getLmFlow", "setLmFlow", "getLmMain", "setLmMain", "getTextConditioner", "setTextConditioner", "getTokenScoresJson", "setTokenScoresJson", "getVocabJson", "setVocabJson", "getVoiceEmbeddingCacheCapacity", "()I", "setVoiceEmbeddingCacheCapacity", "(I)V", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class OfflineTtsPocketModelConfig {
    private String decoder;
    private String encoder;
    private String lmFlow;
    private String lmMain;
    private String textConditioner;
    private String tokenScoresJson;
    private String vocabJson;
    private int voiceEmbeddingCacheCapacity;

    public OfflineTtsPocketModelConfig() {
        this(null, null, null, null, null, null, null, 0, 255, null);
    }

    public static /* synthetic */ OfflineTtsPocketModelConfig copy$default(OfflineTtsPocketModelConfig offlineTtsPocketModelConfig, String str, String str2, String str3, String str4, String str5, String str6, String str7, int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            str = offlineTtsPocketModelConfig.lmFlow;
        }
        if ((i2 & 2) != 0) {
            str2 = offlineTtsPocketModelConfig.lmMain;
        }
        if ((i2 & 4) != 0) {
            str3 = offlineTtsPocketModelConfig.encoder;
        }
        if ((i2 & 8) != 0) {
            str4 = offlineTtsPocketModelConfig.decoder;
        }
        if ((i2 & 16) != 0) {
            str5 = offlineTtsPocketModelConfig.textConditioner;
        }
        if ((i2 & 32) != 0) {
            str6 = offlineTtsPocketModelConfig.vocabJson;
        }
        if ((i2 & 64) != 0) {
            str7 = offlineTtsPocketModelConfig.tokenScoresJson;
        }
        if ((i2 & 128) != 0) {
            i = offlineTtsPocketModelConfig.voiceEmbeddingCacheCapacity;
        }
        String str8 = str7;
        int i3 = i;
        String str9 = str5;
        String str10 = str6;
        return offlineTtsPocketModelConfig.copy(str, str2, str3, str4, str9, str10, str8, i3);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String component1() {
        return this.lmFlow;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String component2() {
        return this.lmMain;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final String component3() {
        return this.encoder;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final String component4() {
        return this.decoder;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final String component5() {
        return this.textConditioner;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final String component6() {
        return this.vocabJson;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final String component7() {
        return this.tokenScoresJson;
    }

    /* JADX INFO: renamed from: component8, reason: from getter */
    public final int component8() {
        return this.voiceEmbeddingCacheCapacity;
    }

    public final OfflineTtsPocketModelConfig copy(String lmFlow, String lmMain, String encoder, String decoder, String textConditioner, String vocabJson, String tokenScoresJson, int voiceEmbeddingCacheCapacity) {
        Intrinsics.checkNotNullParameter(lmFlow, "lmFlow");
        Intrinsics.checkNotNullParameter(lmMain, "lmMain");
        Intrinsics.checkNotNullParameter(encoder, "encoder");
        Intrinsics.checkNotNullParameter(decoder, "decoder");
        Intrinsics.checkNotNullParameter(textConditioner, "textConditioner");
        Intrinsics.checkNotNullParameter(vocabJson, "vocabJson");
        Intrinsics.checkNotNullParameter(tokenScoresJson, "tokenScoresJson");
        return new OfflineTtsPocketModelConfig(lmFlow, lmMain, encoder, decoder, textConditioner, vocabJson, tokenScoresJson, voiceEmbeddingCacheCapacity);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineTtsPocketModelConfig)) {
            return false;
        }
        OfflineTtsPocketModelConfig offlineTtsPocketModelConfig = (OfflineTtsPocketModelConfig) other;
        return Intrinsics.areEqual(this.lmFlow, offlineTtsPocketModelConfig.lmFlow) && Intrinsics.areEqual(this.lmMain, offlineTtsPocketModelConfig.lmMain) && Intrinsics.areEqual(this.encoder, offlineTtsPocketModelConfig.encoder) && Intrinsics.areEqual(this.decoder, offlineTtsPocketModelConfig.decoder) && Intrinsics.areEqual(this.textConditioner, offlineTtsPocketModelConfig.textConditioner) && Intrinsics.areEqual(this.vocabJson, offlineTtsPocketModelConfig.vocabJson) && Intrinsics.areEqual(this.tokenScoresJson, offlineTtsPocketModelConfig.tokenScoresJson) && this.voiceEmbeddingCacheCapacity == offlineTtsPocketModelConfig.voiceEmbeddingCacheCapacity;
    }

    public int hashCode() {
        return (((((((((((((this.lmFlow.hashCode() * 31) + this.lmMain.hashCode()) * 31) + this.encoder.hashCode()) * 31) + this.decoder.hashCode()) * 31) + this.textConditioner.hashCode()) * 31) + this.vocabJson.hashCode()) * 31) + this.tokenScoresJson.hashCode()) * 31) + Integer.hashCode(this.voiceEmbeddingCacheCapacity);
    }

    public String toString() {
        return "OfflineTtsPocketModelConfig(lmFlow=" + this.lmFlow + ", lmMain=" + this.lmMain + ", encoder=" + this.encoder + ", decoder=" + this.decoder + ", textConditioner=" + this.textConditioner + ", vocabJson=" + this.vocabJson + ", tokenScoresJson=" + this.tokenScoresJson + ", voiceEmbeddingCacheCapacity=" + this.voiceEmbeddingCacheCapacity + ')';
    }

    public OfflineTtsPocketModelConfig(String lmFlow, String lmMain, String encoder, String decoder, String textConditioner, String vocabJson, String tokenScoresJson, int i) {
        Intrinsics.checkNotNullParameter(lmFlow, "lmFlow");
        Intrinsics.checkNotNullParameter(lmMain, "lmMain");
        Intrinsics.checkNotNullParameter(encoder, "encoder");
        Intrinsics.checkNotNullParameter(decoder, "decoder");
        Intrinsics.checkNotNullParameter(textConditioner, "textConditioner");
        Intrinsics.checkNotNullParameter(vocabJson, "vocabJson");
        Intrinsics.checkNotNullParameter(tokenScoresJson, "tokenScoresJson");
        this.lmFlow = lmFlow;
        this.lmMain = lmMain;
        this.encoder = encoder;
        this.decoder = decoder;
        this.textConditioner = textConditioner;
        this.vocabJson = vocabJson;
        this.tokenScoresJson = tokenScoresJson;
        this.voiceEmbeddingCacheCapacity = i;
    }

    public /* synthetic */ OfflineTtsPocketModelConfig(String str, String str2, String str3, String str4, String str5, String str6, String str7, int i, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? "" : str, (i2 & 2) != 0 ? "" : str2, (i2 & 4) != 0 ? "" : str3, (i2 & 8) != 0 ? "" : str4, (i2 & 16) != 0 ? "" : str5, (i2 & 32) != 0 ? "" : str6, (i2 & 64) != 0 ? "" : str7, (i2 & 128) != 0 ? 50 : i);
    }

    public final String getLmFlow() {
        return this.lmFlow;
    }

    public final void setLmFlow(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.lmFlow = str;
    }

    public final String getLmMain() {
        return this.lmMain;
    }

    public final void setLmMain(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.lmMain = str;
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

    public final String getTextConditioner() {
        return this.textConditioner;
    }

    public final void setTextConditioner(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.textConditioner = str;
    }

    public final String getVocabJson() {
        return this.vocabJson;
    }

    public final void setVocabJson(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.vocabJson = str;
    }

    public final String getTokenScoresJson() {
        return this.tokenScoresJson;
    }

    public final void setTokenScoresJson(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.tokenScoresJson = str;
    }

    public final int getVoiceEmbeddingCacheCapacity() {
        return this.voiceEmbeddingCacheCapacity;
    }

    public final void setVoiceEmbeddingCacheCapacity(int i) {
        this.voiceEmbeddingCacheCapacity = i;
    }
}
