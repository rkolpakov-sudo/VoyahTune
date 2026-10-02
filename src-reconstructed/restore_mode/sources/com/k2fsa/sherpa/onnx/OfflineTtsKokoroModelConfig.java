package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Tts.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u0007\n\u0002\b\u001f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BU\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\u0003\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b¢\u0006\u0002\u0010\fJ\t\u0010!\u001a\u00020\u0003HÆ\u0003J\t\u0010\"\u001a\u00020\u0003HÆ\u0003J\t\u0010#\u001a\u00020\u0003HÆ\u0003J\t\u0010$\u001a\u00020\u0003HÆ\u0003J\t\u0010%\u001a\u00020\u0003HÆ\u0003J\t\u0010&\u001a\u00020\u0003HÆ\u0003J\t\u0010'\u001a\u00020\u0003HÆ\u0003J\t\u0010(\u001a\u00020\u000bHÆ\u0003JY\u0010)\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u000bHÆ\u0001J\u0013\u0010*\u001a\u00020+2\b\u0010,\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010-\u001a\u00020.HÖ\u0001J\t\u0010/\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0006\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001a\u0010\t\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u000e\"\u0004\b\u0012\u0010\u0010R\u001a\u0010\b\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u000e\"\u0004\b\u0014\u0010\u0010R\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\u001a\u0010\u0007\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u000e\"\u0004\b\u001a\u0010\u0010R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u000e\"\u0004\b\u001c\u0010\u0010R\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u000e\"\u0004\b\u001e\u0010\u0010R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010\u000e\"\u0004\b \u0010\u0010¨\u00060"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineTtsKokoroModelConfig;", "", "model", "", "voices", "tokens", "dataDir", "lexicon", "lang", "dictDir", "lengthScale", "", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;F)V", "getDataDir", "()Ljava/lang/String;", "setDataDir", "(Ljava/lang/String;)V", "getDictDir", "setDictDir", "getLang", "setLang", "getLengthScale", "()F", "setLengthScale", "(F)V", "getLexicon", "setLexicon", "getModel", "setModel", "getTokens", "setTokens", "getVoices", "setVoices", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "", "other", "hashCode", "", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class OfflineTtsKokoroModelConfig {
    private String dataDir;
    private String dictDir;
    private String lang;
    private float lengthScale;
    private String lexicon;
    private String model;
    private String tokens;
    private String voices;

    public OfflineTtsKokoroModelConfig() {
        this(null, null, null, null, null, null, null, 0.0f, 255, null);
    }

    public static /* synthetic */ OfflineTtsKokoroModelConfig copy$default(OfflineTtsKokoroModelConfig offlineTtsKokoroModelConfig, String str, String str2, String str3, String str4, String str5, String str6, String str7, float f, int i, Object obj) {
        if ((i & 1) != 0) {
            str = offlineTtsKokoroModelConfig.model;
        }
        if ((i & 2) != 0) {
            str2 = offlineTtsKokoroModelConfig.voices;
        }
        if ((i & 4) != 0) {
            str3 = offlineTtsKokoroModelConfig.tokens;
        }
        if ((i & 8) != 0) {
            str4 = offlineTtsKokoroModelConfig.dataDir;
        }
        if ((i & 16) != 0) {
            str5 = offlineTtsKokoroModelConfig.lexicon;
        }
        if ((i & 32) != 0) {
            str6 = offlineTtsKokoroModelConfig.lang;
        }
        if ((i & 64) != 0) {
            str7 = offlineTtsKokoroModelConfig.dictDir;
        }
        if ((i & 128) != 0) {
            f = offlineTtsKokoroModelConfig.lengthScale;
        }
        String str8 = str7;
        float f2 = f;
        String str9 = str5;
        String str10 = str6;
        return offlineTtsKokoroModelConfig.copy(str, str2, str3, str4, str9, str10, str8, f2);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String getModel() {
        return this.model;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String getVoices() {
        return this.voices;
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
    public final String getLexicon() {
        return this.lexicon;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final String getLang() {
        return this.lang;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final String getDictDir() {
        return this.dictDir;
    }

    /* JADX INFO: renamed from: component8, reason: from getter */
    public final float getLengthScale() {
        return this.lengthScale;
    }

    public final OfflineTtsKokoroModelConfig copy(String model, String voices, String tokens, String dataDir, String lexicon, String lang, String dictDir, float lengthScale) {
        Intrinsics.checkNotNullParameter(model, "model");
        Intrinsics.checkNotNullParameter(voices, "voices");
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        Intrinsics.checkNotNullParameter(dataDir, "dataDir");
        Intrinsics.checkNotNullParameter(lexicon, "lexicon");
        Intrinsics.checkNotNullParameter(lang, "lang");
        Intrinsics.checkNotNullParameter(dictDir, "dictDir");
        return new OfflineTtsKokoroModelConfig(model, voices, tokens, dataDir, lexicon, lang, dictDir, lengthScale);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineTtsKokoroModelConfig)) {
            return false;
        }
        OfflineTtsKokoroModelConfig offlineTtsKokoroModelConfig = (OfflineTtsKokoroModelConfig) other;
        return Intrinsics.areEqual(this.model, offlineTtsKokoroModelConfig.model) && Intrinsics.areEqual(this.voices, offlineTtsKokoroModelConfig.voices) && Intrinsics.areEqual(this.tokens, offlineTtsKokoroModelConfig.tokens) && Intrinsics.areEqual(this.dataDir, offlineTtsKokoroModelConfig.dataDir) && Intrinsics.areEqual(this.lexicon, offlineTtsKokoroModelConfig.lexicon) && Intrinsics.areEqual(this.lang, offlineTtsKokoroModelConfig.lang) && Intrinsics.areEqual(this.dictDir, offlineTtsKokoroModelConfig.dictDir) && Float.compare(this.lengthScale, offlineTtsKokoroModelConfig.lengthScale) == 0;
    }

    public int hashCode() {
        return (((((((((((((this.model.hashCode() * 31) + this.voices.hashCode()) * 31) + this.tokens.hashCode()) * 31) + this.dataDir.hashCode()) * 31) + this.lexicon.hashCode()) * 31) + this.lang.hashCode()) * 31) + this.dictDir.hashCode()) * 31) + Float.hashCode(this.lengthScale);
    }

    public String toString() {
        return "OfflineTtsKokoroModelConfig(model=" + this.model + ", voices=" + this.voices + ", tokens=" + this.tokens + ", dataDir=" + this.dataDir + ", lexicon=" + this.lexicon + ", lang=" + this.lang + ", dictDir=" + this.dictDir + ", lengthScale=" + this.lengthScale + ')';
    }

    public OfflineTtsKokoroModelConfig(String model, String voices, String tokens, String dataDir, String lexicon, String lang, String dictDir, float f) {
        Intrinsics.checkNotNullParameter(model, "model");
        Intrinsics.checkNotNullParameter(voices, "voices");
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        Intrinsics.checkNotNullParameter(dataDir, "dataDir");
        Intrinsics.checkNotNullParameter(lexicon, "lexicon");
        Intrinsics.checkNotNullParameter(lang, "lang");
        Intrinsics.checkNotNullParameter(dictDir, "dictDir");
        this.model = model;
        this.voices = voices;
        this.tokens = tokens;
        this.dataDir = dataDir;
        this.lexicon = lexicon;
        this.lang = lang;
        this.dictDir = dictDir;
        this.lengthScale = f;
    }

    public /* synthetic */ OfflineTtsKokoroModelConfig(String str, String str2, String str3, String str4, String str5, String str6, String str7, float f, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? "" : str, (i & 2) != 0 ? "" : str2, (i & 4) != 0 ? "" : str3, (i & 8) != 0 ? "" : str4, (i & 16) != 0 ? "" : str5, (i & 32) != 0 ? "" : str6, (i & 64) != 0 ? "" : str7, (i & 128) != 0 ? 1.0f : f);
    }

    public final String getModel() {
        return this.model;
    }

    public final void setModel(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.model = str;
    }

    public final String getVoices() {
        return this.voices;
    }

    public final void setVoices(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.voices = str;
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

    public final String getLexicon() {
        return this.lexicon;
    }

    public final void setLexicon(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.lexicon = str;
    }

    public final String getLang() {
        return this.lang;
    }

    public final void setLang(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.lang = str;
    }

    public final String getDictDir() {
        return this.dictDir;
    }

    public final void setDictDir(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.dictDir = str;
    }

    public final float getLengthScale() {
        return this.lengthScale;
    }

    public final void setLengthScale(float f) {
        this.lengthScale = f;
    }
}
