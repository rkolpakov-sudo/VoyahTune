package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.scheduling.WorkQueueKt;

/* JADX INFO: compiled from: Tts.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b \n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BK\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\u0003¢\u0006\u0002\u0010\nJ\t\u0010\u001b\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001c\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001d\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001f\u001a\u00020\u0003HÆ\u0003J\t\u0010 \u001a\u00020\u0003HÆ\u0003J\t\u0010!\u001a\u00020\u0003HÆ\u0003JO\u0010\"\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u0003HÆ\u0001J\u0013\u0010#\u001a\u00020$2\b\u0010%\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010&\u001a\u00020'HÖ\u0001J\t\u0010(\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\f\"\u0004\b\u0010\u0010\u000eR\u001a\u0010\u0007\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\f\"\u0004\b\u0012\u0010\u000eR\u001a\u0010\b\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\f\"\u0004\b\u0014\u0010\u000eR\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\f\"\u0004\b\u0016\u0010\u000eR\u001a\u0010\u0006\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\f\"\u0004\b\u0018\u0010\u000eR\u001a\u0010\t\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\f\"\u0004\b\u001a\u0010\u000e¨\u0006)"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineTtsSupertonicModelConfig;", "", "durationPredictor", "", "textEncoder", "vectorEstimator", "vocoder", "ttsJson", "unicodeIndexer", "voiceStyle", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getDurationPredictor", "()Ljava/lang/String;", "setDurationPredictor", "(Ljava/lang/String;)V", "getTextEncoder", "setTextEncoder", "getTtsJson", "setTtsJson", "getUnicodeIndexer", "setUnicodeIndexer", "getVectorEstimator", "setVectorEstimator", "getVocoder", "setVocoder", "getVoiceStyle", "setVoiceStyle", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "", "other", "hashCode", "", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class OfflineTtsSupertonicModelConfig {
    private String durationPredictor;
    private String textEncoder;
    private String ttsJson;
    private String unicodeIndexer;
    private String vectorEstimator;
    private String vocoder;
    private String voiceStyle;

    public OfflineTtsSupertonicModelConfig() {
        this(null, null, null, null, null, null, null, WorkQueueKt.MASK, null);
    }

    public static /* synthetic */ OfflineTtsSupertonicModelConfig copy$default(OfflineTtsSupertonicModelConfig offlineTtsSupertonicModelConfig, String str, String str2, String str3, String str4, String str5, String str6, String str7, int i, Object obj) {
        if ((i & 1) != 0) {
            str = offlineTtsSupertonicModelConfig.durationPredictor;
        }
        if ((i & 2) != 0) {
            str2 = offlineTtsSupertonicModelConfig.textEncoder;
        }
        if ((i & 4) != 0) {
            str3 = offlineTtsSupertonicModelConfig.vectorEstimator;
        }
        if ((i & 8) != 0) {
            str4 = offlineTtsSupertonicModelConfig.vocoder;
        }
        if ((i & 16) != 0) {
            str5 = offlineTtsSupertonicModelConfig.ttsJson;
        }
        if ((i & 32) != 0) {
            str6 = offlineTtsSupertonicModelConfig.unicodeIndexer;
        }
        if ((i & 64) != 0) {
            str7 = offlineTtsSupertonicModelConfig.voiceStyle;
        }
        String str8 = str6;
        String str9 = str7;
        String str10 = str5;
        String str11 = str3;
        return offlineTtsSupertonicModelConfig.copy(str, str2, str11, str4, str10, str8, str9);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String getDurationPredictor() {
        return this.durationPredictor;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String getTextEncoder() {
        return this.textEncoder;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final String getVectorEstimator() {
        return this.vectorEstimator;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final String getVocoder() {
        return this.vocoder;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final String getTtsJson() {
        return this.ttsJson;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final String getUnicodeIndexer() {
        return this.unicodeIndexer;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final String getVoiceStyle() {
        return this.voiceStyle;
    }

    public final OfflineTtsSupertonicModelConfig copy(String durationPredictor, String textEncoder, String vectorEstimator, String vocoder, String ttsJson, String unicodeIndexer, String voiceStyle) {
        Intrinsics.checkNotNullParameter(durationPredictor, "durationPredictor");
        Intrinsics.checkNotNullParameter(textEncoder, "textEncoder");
        Intrinsics.checkNotNullParameter(vectorEstimator, "vectorEstimator");
        Intrinsics.checkNotNullParameter(vocoder, "vocoder");
        Intrinsics.checkNotNullParameter(ttsJson, "ttsJson");
        Intrinsics.checkNotNullParameter(unicodeIndexer, "unicodeIndexer");
        Intrinsics.checkNotNullParameter(voiceStyle, "voiceStyle");
        return new OfflineTtsSupertonicModelConfig(durationPredictor, textEncoder, vectorEstimator, vocoder, ttsJson, unicodeIndexer, voiceStyle);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineTtsSupertonicModelConfig)) {
            return false;
        }
        OfflineTtsSupertonicModelConfig offlineTtsSupertonicModelConfig = (OfflineTtsSupertonicModelConfig) other;
        return Intrinsics.areEqual(this.durationPredictor, offlineTtsSupertonicModelConfig.durationPredictor) && Intrinsics.areEqual(this.textEncoder, offlineTtsSupertonicModelConfig.textEncoder) && Intrinsics.areEqual(this.vectorEstimator, offlineTtsSupertonicModelConfig.vectorEstimator) && Intrinsics.areEqual(this.vocoder, offlineTtsSupertonicModelConfig.vocoder) && Intrinsics.areEqual(this.ttsJson, offlineTtsSupertonicModelConfig.ttsJson) && Intrinsics.areEqual(this.unicodeIndexer, offlineTtsSupertonicModelConfig.unicodeIndexer) && Intrinsics.areEqual(this.voiceStyle, offlineTtsSupertonicModelConfig.voiceStyle);
    }

    public int hashCode() {
        return (((((((((((this.durationPredictor.hashCode() * 31) + this.textEncoder.hashCode()) * 31) + this.vectorEstimator.hashCode()) * 31) + this.vocoder.hashCode()) * 31) + this.ttsJson.hashCode()) * 31) + this.unicodeIndexer.hashCode()) * 31) + this.voiceStyle.hashCode();
    }

    public String toString() {
        return "OfflineTtsSupertonicModelConfig(durationPredictor=" + this.durationPredictor + ", textEncoder=" + this.textEncoder + ", vectorEstimator=" + this.vectorEstimator + ", vocoder=" + this.vocoder + ", ttsJson=" + this.ttsJson + ", unicodeIndexer=" + this.unicodeIndexer + ", voiceStyle=" + this.voiceStyle + ')';
    }

    public OfflineTtsSupertonicModelConfig(String durationPredictor, String textEncoder, String vectorEstimator, String vocoder, String ttsJson, String unicodeIndexer, String voiceStyle) {
        Intrinsics.checkNotNullParameter(durationPredictor, "durationPredictor");
        Intrinsics.checkNotNullParameter(textEncoder, "textEncoder");
        Intrinsics.checkNotNullParameter(vectorEstimator, "vectorEstimator");
        Intrinsics.checkNotNullParameter(vocoder, "vocoder");
        Intrinsics.checkNotNullParameter(ttsJson, "ttsJson");
        Intrinsics.checkNotNullParameter(unicodeIndexer, "unicodeIndexer");
        Intrinsics.checkNotNullParameter(voiceStyle, "voiceStyle");
        this.durationPredictor = durationPredictor;
        this.textEncoder = textEncoder;
        this.vectorEstimator = vectorEstimator;
        this.vocoder = vocoder;
        this.ttsJson = ttsJson;
        this.unicodeIndexer = unicodeIndexer;
        this.voiceStyle = voiceStyle;
    }

    public /* synthetic */ OfflineTtsSupertonicModelConfig(String str, String str2, String str3, String str4, String str5, String str6, String str7, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? "" : str, (i & 2) != 0 ? "" : str2, (i & 4) != 0 ? "" : str3, (i & 8) != 0 ? "" : str4, (i & 16) != 0 ? "" : str5, (i & 32) != 0 ? "" : str6, (i & 64) != 0 ? "" : str7);
    }

    public final String getDurationPredictor() {
        return this.durationPredictor;
    }

    public final void setDurationPredictor(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.durationPredictor = str;
    }

    public final String getTextEncoder() {
        return this.textEncoder;
    }

    public final void setTextEncoder(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.textEncoder = str;
    }

    public final String getVectorEstimator() {
        return this.vectorEstimator;
    }

    public final void setVectorEstimator(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.vectorEstimator = str;
    }

    public final String getVocoder() {
        return this.vocoder;
    }

    public final void setVocoder(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.vocoder = str;
    }

    public final String getTtsJson() {
        return this.ttsJson;
    }

    public final void setTtsJson(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.ttsJson = str;
    }

    public final String getUnicodeIndexer() {
        return this.unicodeIndexer;
    }

    public final void setUnicodeIndexer(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.unicodeIndexer = str;
    }

    public final String getVoiceStyle() {
        return this.voiceStyle;
    }

    public final void setVoiceStyle(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.voiceStyle = str;
    }
}
