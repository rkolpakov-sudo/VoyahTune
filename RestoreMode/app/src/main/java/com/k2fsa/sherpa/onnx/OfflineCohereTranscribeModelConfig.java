package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OfflineRecognizer.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0019\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B7\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007¢\u0006\u0002\u0010\tJ\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001b\u001a\u00020\u0007HÆ\u0003J\t\u0010\u001c\u001a\u00020\u0007HÆ\u0003J;\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0007HÆ\u0001J\u0013\u0010\u001e\u001a\u00020\u00072\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010 \u001a\u00020!HÖ\u0001J\t\u0010\"\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000b\"\u0004\b\u000f\u0010\rR\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u000b\"\u0004\b\u0011\u0010\rR\u001a\u0010\b\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0013\"\u0004\b\u0017\u0010\u0015¨\u0006#"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineCohereTranscribeModelConfig;", "", "encoder", "", "decoder", "language", "usePunct", "", "useItn", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZ)V", "getDecoder", "()Ljava/lang/String;", "setDecoder", "(Ljava/lang/String;)V", "getEncoder", "setEncoder", "getLanguage", "setLanguage", "getUseItn", "()Z", "setUseItn", "(Z)V", "getUsePunct", "setUsePunct", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class OfflineCohereTranscribeModelConfig {
    private String decoder;
    private String encoder;
    private String language;
    private boolean useItn;
    private boolean usePunct;

    public OfflineCohereTranscribeModelConfig() {
        this(null, null, null, false, false, 31, null);
    }

    public static /* synthetic */ OfflineCohereTranscribeModelConfig copy$default(OfflineCohereTranscribeModelConfig offlineCohereTranscribeModelConfig, String str, String str2, String str3, boolean z, boolean z2, int i, Object obj) {
        if ((i & 1) != 0) {
            str = offlineCohereTranscribeModelConfig.encoder;
        }
        if ((i & 2) != 0) {
            str2 = offlineCohereTranscribeModelConfig.decoder;
        }
        if ((i & 4) != 0) {
            str3 = offlineCohereTranscribeModelConfig.language;
        }
        if ((i & 8) != 0) {
            z = offlineCohereTranscribeModelConfig.usePunct;
        }
        if ((i & 16) != 0) {
            z2 = offlineCohereTranscribeModelConfig.useItn;
        }
        boolean z3 = z2;
        String str4 = str3;
        return offlineCohereTranscribeModelConfig.copy(str, str2, str4, z, z3);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String component1() {
        return this.encoder;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String component2() {
        return this.decoder;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final String component3() {
        return this.language;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final boolean component4() {
        return this.usePunct;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final boolean component5() {
        return this.useItn;
    }

    public final OfflineCohereTranscribeModelConfig copy(String encoder, String decoder, String language, boolean usePunct, boolean useItn) {
        Intrinsics.checkNotNullParameter(encoder, "encoder");
        Intrinsics.checkNotNullParameter(decoder, "decoder");
        Intrinsics.checkNotNullParameter(language, "language");
        return new OfflineCohereTranscribeModelConfig(encoder, decoder, language, usePunct, useItn);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineCohereTranscribeModelConfig)) {
            return false;
        }
        OfflineCohereTranscribeModelConfig offlineCohereTranscribeModelConfig = (OfflineCohereTranscribeModelConfig) other;
        return Intrinsics.areEqual(this.encoder, offlineCohereTranscribeModelConfig.encoder) && Intrinsics.areEqual(this.decoder, offlineCohereTranscribeModelConfig.decoder) && Intrinsics.areEqual(this.language, offlineCohereTranscribeModelConfig.language) && this.usePunct == offlineCohereTranscribeModelConfig.usePunct && this.useItn == offlineCohereTranscribeModelConfig.useItn;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v7, types: [int] */
    /* JADX WARN: Type inference failed for: r0v9, types: [int] */
    /* JADX WARN: Type inference failed for: r1v5, types: [int] */
    /* JADX WARN: Type inference failed for: r1v6 */
    /* JADX WARN: Type inference failed for: r1v7 */
    /* JADX WARN: Type inference failed for: r2v0 */
    /* JADX WARN: Type inference failed for: r2v1, types: [int] */
    /* JADX WARN: Type inference failed for: r2v2 */
    public int hashCode() {
        int iHashCode = ((((this.encoder.hashCode() * 31) + this.decoder.hashCode()) * 31) + this.language.hashCode()) * 31;
        boolean z = this.usePunct;
        int r1 = z ? 1 : 0;
        if (z) {
            r1 = 1;
        }
        int i = (iHashCode + r1) * 31;
        boolean z2 = this.useItn;
        return i + (z2 ? 1 : 0);
    }

    public String toString() {
        return "OfflineCohereTranscribeModelConfig(encoder=" + this.encoder + ", decoder=" + this.decoder + ", language=" + this.language + ", usePunct=" + this.usePunct + ", useItn=" + this.useItn + ')';
    }

    public OfflineCohereTranscribeModelConfig(String encoder, String decoder, String language, boolean z, boolean z2) {
        Intrinsics.checkNotNullParameter(encoder, "encoder");
        Intrinsics.checkNotNullParameter(decoder, "decoder");
        Intrinsics.checkNotNullParameter(language, "language");
        this.encoder = encoder;
        this.decoder = decoder;
        this.language = language;
        this.usePunct = z;
        this.useItn = z2;
    }

    public /* synthetic */ OfflineCohereTranscribeModelConfig(String str, String str2, String str3, boolean z, boolean z2, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? "" : str, (i & 2) != 0 ? "" : str2, (i & 4) != 0 ? "" : str3, (i & 8) != 0 ? true : z, (i & 16) != 0 ? true : z2);
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

    public final String getLanguage() {
        return this.language;
    }

    public final void setLanguage(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.language = str;
    }

    public final boolean getUsePunct() {
        return this.usePunct;
    }

    public final void setUsePunct(boolean z) {
        this.usePunct = z;
    }

    public final boolean getUseItn() {
        return this.useItn;
    }

    public final void setUseItn(boolean z) {
        this.useItn = z;
    }
}
