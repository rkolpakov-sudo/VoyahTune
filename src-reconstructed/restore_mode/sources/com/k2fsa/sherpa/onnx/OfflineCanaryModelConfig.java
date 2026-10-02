package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OfflineRecognizer.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0018\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B7\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b¢\u0006\u0002\u0010\tJ\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001b\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001c\u001a\u00020\bHÆ\u0003J;\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\bHÆ\u0001J\u0013\u0010\u001e\u001a\u00020\b2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010 \u001a\u00020!HÖ\u0001J\t\u0010\"\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000b\"\u0004\b\u000f\u0010\rR\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u000b\"\u0004\b\u0011\u0010\rR\u001a\u0010\u0006\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u000b\"\u0004\b\u0013\u0010\rR\u001a\u0010\u0007\u001a\u00020\bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017¨\u0006#"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineCanaryModelConfig;", "", "encoder", "", "decoder", "srcLang", "tgtLang", "usePnc", "", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V", "getDecoder", "()Ljava/lang/String;", "setDecoder", "(Ljava/lang/String;)V", "getEncoder", "setEncoder", "getSrcLang", "setSrcLang", "getTgtLang", "setTgtLang", "getUsePnc", "()Z", "setUsePnc", "(Z)V", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class OfflineCanaryModelConfig {
    private String decoder;
    private String encoder;
    private String srcLang;
    private String tgtLang;
    private boolean usePnc;

    public OfflineCanaryModelConfig() {
        this(null, null, null, null, false, 31, null);
    }

    public static /* synthetic */ OfflineCanaryModelConfig copy$default(OfflineCanaryModelConfig offlineCanaryModelConfig, String str, String str2, String str3, String str4, boolean z, int i, Object obj) {
        if ((i & 1) != 0) {
            str = offlineCanaryModelConfig.encoder;
        }
        if ((i & 2) != 0) {
            str2 = offlineCanaryModelConfig.decoder;
        }
        if ((i & 4) != 0) {
            str3 = offlineCanaryModelConfig.srcLang;
        }
        if ((i & 8) != 0) {
            str4 = offlineCanaryModelConfig.tgtLang;
        }
        if ((i & 16) != 0) {
            z = offlineCanaryModelConfig.usePnc;
        }
        boolean z2 = z;
        String str5 = str3;
        return offlineCanaryModelConfig.copy(str, str2, str5, str4, z2);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String getEncoder() {
        return this.encoder;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String getDecoder() {
        return this.decoder;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final String getSrcLang() {
        return this.srcLang;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final String getTgtLang() {
        return this.tgtLang;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final boolean getUsePnc() {
        return this.usePnc;
    }

    public final OfflineCanaryModelConfig copy(String encoder, String decoder, String srcLang, String tgtLang, boolean usePnc) {
        Intrinsics.checkNotNullParameter(encoder, "encoder");
        Intrinsics.checkNotNullParameter(decoder, "decoder");
        Intrinsics.checkNotNullParameter(srcLang, "srcLang");
        Intrinsics.checkNotNullParameter(tgtLang, "tgtLang");
        return new OfflineCanaryModelConfig(encoder, decoder, srcLang, tgtLang, usePnc);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineCanaryModelConfig)) {
            return false;
        }
        OfflineCanaryModelConfig offlineCanaryModelConfig = (OfflineCanaryModelConfig) other;
        return Intrinsics.areEqual(this.encoder, offlineCanaryModelConfig.encoder) && Intrinsics.areEqual(this.decoder, offlineCanaryModelConfig.decoder) && Intrinsics.areEqual(this.srcLang, offlineCanaryModelConfig.srcLang) && Intrinsics.areEqual(this.tgtLang, offlineCanaryModelConfig.tgtLang) && this.usePnc == offlineCanaryModelConfig.usePnc;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v9, types: [int] */
    /* JADX WARN: Type inference failed for: r2v2, types: [int] */
    /* JADX WARN: Type inference failed for: r2v3 */
    /* JADX WARN: Type inference failed for: r2v4 */
    public int hashCode() {
        int iHashCode = ((((((this.encoder.hashCode() * 31) + this.decoder.hashCode()) * 31) + this.srcLang.hashCode()) * 31) + this.tgtLang.hashCode()) * 31;
        boolean z = this.usePnc;
        ?? r2 = z;
        if (z) {
            r2 = 1;
        }
        return iHashCode + r2;
    }

    public String toString() {
        return "OfflineCanaryModelConfig(encoder=" + this.encoder + ", decoder=" + this.decoder + ", srcLang=" + this.srcLang + ", tgtLang=" + this.tgtLang + ", usePnc=" + this.usePnc + ')';
    }

    public OfflineCanaryModelConfig(String encoder, String decoder, String srcLang, String tgtLang, boolean z) {
        Intrinsics.checkNotNullParameter(encoder, "encoder");
        Intrinsics.checkNotNullParameter(decoder, "decoder");
        Intrinsics.checkNotNullParameter(srcLang, "srcLang");
        Intrinsics.checkNotNullParameter(tgtLang, "tgtLang");
        this.encoder = encoder;
        this.decoder = decoder;
        this.srcLang = srcLang;
        this.tgtLang = tgtLang;
        this.usePnc = z;
    }

    public /* synthetic */ OfflineCanaryModelConfig(String str, String str2, String str3, String str4, boolean z, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? "" : str, (i & 2) != 0 ? "" : str2, (i & 4) != 0 ? "en" : str3, (i & 8) != 0 ? "en" : str4, (i & 16) != 0 ? true : z);
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

    public final String getSrcLang() {
        return this.srcLang;
    }

    public final void setSrcLang(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.srcLang = str;
    }

    public final String getTgtLang() {
        return this.tgtLang;
    }

    public final void setTgtLang(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.tgtLang = str;
    }

    public final boolean getUsePnc() {
        return this.usePnc;
    }

    public final void setUsePnc(boolean z) {
        this.usePnc = z;
    }
}
