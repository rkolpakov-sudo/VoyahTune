package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OfflineRecognizer.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b7\b\u0086\b\u0018\u00002\u00020\u0001B\u0087\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u0012\b\b\u0002\u0010\r\u001a\u00020\f\u0012\b\b\u0002\u0010\u000e\u001a\u00020\n\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0003¢\u0006\u0002\u0010\u0013J\t\u00106\u001a\u00020\u0003HÆ\u0003J\t\u00107\u001a\u00020\nHÆ\u0003J\t\u00108\u001a\u00020\u0003HÆ\u0003J\t\u00109\u001a\u00020\u0011HÆ\u0003J\t\u0010:\u001a\u00020\u0003HÆ\u0003J\t\u0010;\u001a\u00020\u0003HÆ\u0003J\t\u0010<\u001a\u00020\u0003HÆ\u0003J\t\u0010=\u001a\u00020\u0003HÆ\u0003J\t\u0010>\u001a\u00020\u0003HÆ\u0003J\t\u0010?\u001a\u00020\u0003HÆ\u0003J\t\u0010@\u001a\u00020\nHÆ\u0003J\t\u0010A\u001a\u00020\fHÆ\u0003J\t\u0010B\u001a\u00020\fHÆ\u0003J\u008b\u0001\u0010C\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\f2\b\b\u0002\u0010\u000e\u001a\u00020\n2\b\b\u0002\u0010\u000f\u001a\u00020\u00032\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u0003HÆ\u0001J\u0013\u0010D\u001a\u00020\u00112\b\u0010E\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010F\u001a\u00020\nHÖ\u0001J\t\u0010G\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0015\"\u0004\b\u0019\u0010\u0017R\u001a\u0010\u0012\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u0015\"\u0004\b\u001b\u0010\u0017R\u001a\u0010\u0010\u001a\u00020\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001fR\u001a\u0010\u000f\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b \u0010\u0015\"\u0004\b!\u0010\u0017R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010\u0015\"\u0004\b#\u0010\u0017R\u001a\u0010\t\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b$\u0010%\"\u0004\b&\u0010'R\u001a\u0010\u000e\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b(\u0010%\"\u0004\b)\u0010'R\u001a\u0010\u0007\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b*\u0010\u0015\"\u0004\b+\u0010\u0017R\u001a\u0010\u000b\u001a\u00020\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b,\u0010-\"\u0004\b.\u0010/R\u001a\u0010\u0006\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b0\u0010\u0015\"\u0004\b1\u0010\u0017R\u001a\u0010\r\u001a\u00020\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b2\u0010-\"\u0004\b3\u0010/R\u001a\u0010\b\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b4\u0010\u0015\"\u0004\b5\u0010\u0017¨\u0006H"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineFunAsrNanoModelConfig;", "", "encoderAdaptor", "", "llm", "embedding", "tokenizer", "systemPrompt", "userPrompt", "maxNewTokens", "", "temperature", "", "topP", "seed", "language", "itn", "", "hotwords", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IFFILjava/lang/String;ZLjava/lang/String;)V", "getEmbedding", "()Ljava/lang/String;", "setEmbedding", "(Ljava/lang/String;)V", "getEncoderAdaptor", "setEncoderAdaptor", "getHotwords", "setHotwords", "getItn", "()Z", "setItn", "(Z)V", "getLanguage", "setLanguage", "getLlm", "setLlm", "getMaxNewTokens", "()I", "setMaxNewTokens", "(I)V", "getSeed", "setSeed", "getSystemPrompt", "setSystemPrompt", "getTemperature", "()F", "setTemperature", "(F)V", "getTokenizer", "setTokenizer", "getTopP", "setTopP", "getUserPrompt", "setUserPrompt", "component1", "component10", "component11", "component12", "component13", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class OfflineFunAsrNanoModelConfig {
    private String embedding;
    private String encoderAdaptor;
    private String hotwords;
    private boolean itn;
    private String language;
    private String llm;
    private int maxNewTokens;
    private int seed;
    private String systemPrompt;
    private float temperature;
    private String tokenizer;
    private float topP;
    private String userPrompt;

    public OfflineFunAsrNanoModelConfig() {
        this(null, null, null, null, null, null, 0, 0.0f, 0.0f, 0, null, false, null, 8191, null);
    }

    public static /* synthetic */ OfflineFunAsrNanoModelConfig copy$default(OfflineFunAsrNanoModelConfig offlineFunAsrNanoModelConfig, String str, String str2, String str3, String str4, String str5, String str6, int i, float f, float f2, int i2, String str7, boolean z, String str8, int i3, Object obj) {
        if ((i3 & 1) != 0) {
            str = offlineFunAsrNanoModelConfig.encoderAdaptor;
        }
        return offlineFunAsrNanoModelConfig.copy(str, (i3 & 2) != 0 ? offlineFunAsrNanoModelConfig.llm : str2, (i3 & 4) != 0 ? offlineFunAsrNanoModelConfig.embedding : str3, (i3 & 8) != 0 ? offlineFunAsrNanoModelConfig.tokenizer : str4, (i3 & 16) != 0 ? offlineFunAsrNanoModelConfig.systemPrompt : str5, (i3 & 32) != 0 ? offlineFunAsrNanoModelConfig.userPrompt : str6, (i3 & 64) != 0 ? offlineFunAsrNanoModelConfig.maxNewTokens : i, (i3 & 128) != 0 ? offlineFunAsrNanoModelConfig.temperature : f, (i3 & 256) != 0 ? offlineFunAsrNanoModelConfig.topP : f2, (i3 & 512) != 0 ? offlineFunAsrNanoModelConfig.seed : i2, (i3 & 1024) != 0 ? offlineFunAsrNanoModelConfig.language : str7, (i3 & 2048) != 0 ? offlineFunAsrNanoModelConfig.itn : z, (i3 & 4096) != 0 ? offlineFunAsrNanoModelConfig.hotwords : str8);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String getEncoderAdaptor() {
        return this.encoderAdaptor;
    }

    /* JADX INFO: renamed from: component10, reason: from getter */
    public final int getSeed() {
        return this.seed;
    }

    /* JADX INFO: renamed from: component11, reason: from getter */
    public final String getLanguage() {
        return this.language;
    }

    /* JADX INFO: renamed from: component12, reason: from getter */
    public final boolean getItn() {
        return this.itn;
    }

    /* JADX INFO: renamed from: component13, reason: from getter */
    public final String getHotwords() {
        return this.hotwords;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String getLlm() {
        return this.llm;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final String getEmbedding() {
        return this.embedding;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final String getTokenizer() {
        return this.tokenizer;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final String getSystemPrompt() {
        return this.systemPrompt;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final String getUserPrompt() {
        return this.userPrompt;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final int getMaxNewTokens() {
        return this.maxNewTokens;
    }

    /* JADX INFO: renamed from: component8, reason: from getter */
    public final float getTemperature() {
        return this.temperature;
    }

    /* JADX INFO: renamed from: component9, reason: from getter */
    public final float getTopP() {
        return this.topP;
    }

    public final OfflineFunAsrNanoModelConfig copy(String encoderAdaptor, String llm, String embedding, String tokenizer, String systemPrompt, String userPrompt, int maxNewTokens, float temperature, float topP, int seed, String language, boolean itn, String hotwords) {
        Intrinsics.checkNotNullParameter(encoderAdaptor, "encoderAdaptor");
        Intrinsics.checkNotNullParameter(llm, "llm");
        Intrinsics.checkNotNullParameter(embedding, "embedding");
        Intrinsics.checkNotNullParameter(tokenizer, "tokenizer");
        Intrinsics.checkNotNullParameter(systemPrompt, "systemPrompt");
        Intrinsics.checkNotNullParameter(userPrompt, "userPrompt");
        Intrinsics.checkNotNullParameter(language, "language");
        Intrinsics.checkNotNullParameter(hotwords, "hotwords");
        return new OfflineFunAsrNanoModelConfig(encoderAdaptor, llm, embedding, tokenizer, systemPrompt, userPrompt, maxNewTokens, temperature, topP, seed, language, itn, hotwords);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineFunAsrNanoModelConfig)) {
            return false;
        }
        OfflineFunAsrNanoModelConfig offlineFunAsrNanoModelConfig = (OfflineFunAsrNanoModelConfig) other;
        return Intrinsics.areEqual(this.encoderAdaptor, offlineFunAsrNanoModelConfig.encoderAdaptor) && Intrinsics.areEqual(this.llm, offlineFunAsrNanoModelConfig.llm) && Intrinsics.areEqual(this.embedding, offlineFunAsrNanoModelConfig.embedding) && Intrinsics.areEqual(this.tokenizer, offlineFunAsrNanoModelConfig.tokenizer) && Intrinsics.areEqual(this.systemPrompt, offlineFunAsrNanoModelConfig.systemPrompt) && Intrinsics.areEqual(this.userPrompt, offlineFunAsrNanoModelConfig.userPrompt) && this.maxNewTokens == offlineFunAsrNanoModelConfig.maxNewTokens && Float.compare(this.temperature, offlineFunAsrNanoModelConfig.temperature) == 0 && Float.compare(this.topP, offlineFunAsrNanoModelConfig.topP) == 0 && this.seed == offlineFunAsrNanoModelConfig.seed && Intrinsics.areEqual(this.language, offlineFunAsrNanoModelConfig.language) && this.itn == offlineFunAsrNanoModelConfig.itn && Intrinsics.areEqual(this.hotwords, offlineFunAsrNanoModelConfig.hotwords);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v23, types: [int] */
    /* JADX WARN: Type inference failed for: r1v21, types: [int] */
    /* JADX WARN: Type inference failed for: r1v22 */
    /* JADX WARN: Type inference failed for: r1v23 */
    public int hashCode() {
        int iHashCode = ((((((((((((((((((((this.encoderAdaptor.hashCode() * 31) + this.llm.hashCode()) * 31) + this.embedding.hashCode()) * 31) + this.tokenizer.hashCode()) * 31) + this.systemPrompt.hashCode()) * 31) + this.userPrompt.hashCode()) * 31) + Integer.hashCode(this.maxNewTokens)) * 31) + Float.hashCode(this.temperature)) * 31) + Float.hashCode(this.topP)) * 31) + Integer.hashCode(this.seed)) * 31) + this.language.hashCode()) * 31;
        boolean z = this.itn;
        ?? r1 = z;
        if (z) {
            r1 = 1;
        }
        return ((iHashCode + r1) * 31) + this.hotwords.hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("OfflineFunAsrNanoModelConfig(encoderAdaptor=");
        sb.append(this.encoderAdaptor).append(", llm=").append(this.llm).append(", embedding=").append(this.embedding).append(", tokenizer=").append(this.tokenizer).append(", systemPrompt=").append(this.systemPrompt).append(", userPrompt=").append(this.userPrompt).append(", maxNewTokens=").append(this.maxNewTokens).append(", temperature=").append(this.temperature).append(", topP=").append(this.topP).append(", seed=").append(this.seed).append(", language=").append(this.language).append(", itn=");
        sb.append(this.itn).append(", hotwords=").append(this.hotwords).append(')');
        return sb.toString();
    }

    public OfflineFunAsrNanoModelConfig(String encoderAdaptor, String llm, String embedding, String tokenizer, String systemPrompt, String userPrompt, int i, float f, float f2, int i2, String language, boolean z, String hotwords) {
        Intrinsics.checkNotNullParameter(encoderAdaptor, "encoderAdaptor");
        Intrinsics.checkNotNullParameter(llm, "llm");
        Intrinsics.checkNotNullParameter(embedding, "embedding");
        Intrinsics.checkNotNullParameter(tokenizer, "tokenizer");
        Intrinsics.checkNotNullParameter(systemPrompt, "systemPrompt");
        Intrinsics.checkNotNullParameter(userPrompt, "userPrompt");
        Intrinsics.checkNotNullParameter(language, "language");
        Intrinsics.checkNotNullParameter(hotwords, "hotwords");
        this.encoderAdaptor = encoderAdaptor;
        this.llm = llm;
        this.embedding = embedding;
        this.tokenizer = tokenizer;
        this.systemPrompt = systemPrompt;
        this.userPrompt = userPrompt;
        this.maxNewTokens = i;
        this.temperature = f;
        this.topP = f2;
        this.seed = i2;
        this.language = language;
        this.itn = z;
        this.hotwords = hotwords;
    }

    public /* synthetic */ OfflineFunAsrNanoModelConfig(String str, String str2, String str3, String str4, String str5, String str6, int i, float f, float f2, int i2, String str7, boolean z, String str8, int i3, DefaultConstructorMarker defaultConstructorMarker) {
        this((i3 & 1) != 0 ? "" : str, (i3 & 2) != 0 ? "" : str2, (i3 & 4) != 0 ? "" : str3, (i3 & 8) != 0 ? "" : str4, (i3 & 16) != 0 ? "You are a helpful assistant." : str5, (i3 & 32) != 0 ? "语音转写：" : str6, (i3 & 64) != 0 ? 512 : i, (i3 & 128) != 0 ? 1.0E-6f : f, (i3 & 256) != 0 ? 0.8f : f2, (i3 & 512) != 0 ? 42 : i2, (i3 & 1024) != 0 ? "" : str7, (i3 & 2048) != 0 ? true : z, (i3 & 4096) != 0 ? "" : str8);
    }

    public final String getEncoderAdaptor() {
        return this.encoderAdaptor;
    }

    public final void setEncoderAdaptor(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.encoderAdaptor = str;
    }

    public final String getLlm() {
        return this.llm;
    }

    public final void setLlm(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.llm = str;
    }

    public final String getEmbedding() {
        return this.embedding;
    }

    public final void setEmbedding(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.embedding = str;
    }

    public final String getTokenizer() {
        return this.tokenizer;
    }

    public final void setTokenizer(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.tokenizer = str;
    }

    public final String getSystemPrompt() {
        return this.systemPrompt;
    }

    public final void setSystemPrompt(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.systemPrompt = str;
    }

    public final String getUserPrompt() {
        return this.userPrompt;
    }

    public final void setUserPrompt(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.userPrompt = str;
    }

    public final int getMaxNewTokens() {
        return this.maxNewTokens;
    }

    public final void setMaxNewTokens(int i) {
        this.maxNewTokens = i;
    }

    public final float getTemperature() {
        return this.temperature;
    }

    public final void setTemperature(float f) {
        this.temperature = f;
    }

    public final float getTopP() {
        return this.topP;
    }

    public final void setTopP(float f) {
        this.topP = f;
    }

    public final int getSeed() {
        return this.seed;
    }

    public final void setSeed(int i) {
        this.seed = i;
    }

    public final String getLanguage() {
        return this.language;
    }

    public final void setLanguage(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.language = str;
    }

    public final boolean getItn() {
        return this.itn;
    }

    public final void setItn(boolean z) {
        this.itn = z;
    }

    public final String getHotwords() {
        return this.hotwords;
    }

    public final void setHotwords(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.hotwords = str;
    }
}
