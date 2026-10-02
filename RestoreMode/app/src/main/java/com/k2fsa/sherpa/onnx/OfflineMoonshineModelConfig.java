package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OfflineRecognizer.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B7\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003¢\u0006\u0002\u0010\bJ\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0003HÆ\u0003J;\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001e\u001a\u00020\u001fHÖ\u0001J\t\u0010 \u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0006\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\n\"\u0004\b\u000e\u0010\fR\u001a\u0010\u0007\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\n\"\u0004\b\u0010\u0010\fR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\n\"\u0004\b\u0012\u0010\fR\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\n\"\u0004\b\u0014\u0010\f¨\u0006!"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineMoonshineModelConfig;", "", "preprocessor", "", "encoder", "uncachedDecoder", "cachedDecoder", "mergedDecoder", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getCachedDecoder", "()Ljava/lang/String;", "setCachedDecoder", "(Ljava/lang/String;)V", "getEncoder", "setEncoder", "getMergedDecoder", "setMergedDecoder", "getPreprocessor", "setPreprocessor", "getUncachedDecoder", "setUncachedDecoder", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class OfflineMoonshineModelConfig {
    private String cachedDecoder;
    private String encoder;
    private String mergedDecoder;
    private String preprocessor;
    private String uncachedDecoder;

    public OfflineMoonshineModelConfig() {
        this(null, null, null, null, null, 31, null);
    }

    public static /* synthetic */ OfflineMoonshineModelConfig copy$default(OfflineMoonshineModelConfig offlineMoonshineModelConfig, String str, String str2, String str3, String str4, String str5, int i, Object obj) {
        if ((i & 1) != 0) {
            str = offlineMoonshineModelConfig.preprocessor;
        }
        if ((i & 2) != 0) {
            str2 = offlineMoonshineModelConfig.encoder;
        }
        if ((i & 4) != 0) {
            str3 = offlineMoonshineModelConfig.uncachedDecoder;
        }
        if ((i & 8) != 0) {
            str4 = offlineMoonshineModelConfig.cachedDecoder;
        }
        if ((i & 16) != 0) {
            str5 = offlineMoonshineModelConfig.mergedDecoder;
        }
        String str6 = str5;
        String str7 = str3;
        return offlineMoonshineModelConfig.copy(str, str2, str7, str4, str6);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String component1() {
        return this.preprocessor;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String component2() {
        return this.encoder;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final String component3() {
        return this.uncachedDecoder;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final String component4() {
        return this.cachedDecoder;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final String component5() {
        return this.mergedDecoder;
    }

    public final OfflineMoonshineModelConfig copy(String preprocessor, String encoder, String uncachedDecoder, String cachedDecoder, String mergedDecoder) {
        Intrinsics.checkNotNullParameter(preprocessor, "preprocessor");
        Intrinsics.checkNotNullParameter(encoder, "encoder");
        Intrinsics.checkNotNullParameter(uncachedDecoder, "uncachedDecoder");
        Intrinsics.checkNotNullParameter(cachedDecoder, "cachedDecoder");
        Intrinsics.checkNotNullParameter(mergedDecoder, "mergedDecoder");
        return new OfflineMoonshineModelConfig(preprocessor, encoder, uncachedDecoder, cachedDecoder, mergedDecoder);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineMoonshineModelConfig)) {
            return false;
        }
        OfflineMoonshineModelConfig offlineMoonshineModelConfig = (OfflineMoonshineModelConfig) other;
        return Intrinsics.areEqual(this.preprocessor, offlineMoonshineModelConfig.preprocessor) && Intrinsics.areEqual(this.encoder, offlineMoonshineModelConfig.encoder) && Intrinsics.areEqual(this.uncachedDecoder, offlineMoonshineModelConfig.uncachedDecoder) && Intrinsics.areEqual(this.cachedDecoder, offlineMoonshineModelConfig.cachedDecoder) && Intrinsics.areEqual(this.mergedDecoder, offlineMoonshineModelConfig.mergedDecoder);
    }

    public int hashCode() {
        return (((((((this.preprocessor.hashCode() * 31) + this.encoder.hashCode()) * 31) + this.uncachedDecoder.hashCode()) * 31) + this.cachedDecoder.hashCode()) * 31) + this.mergedDecoder.hashCode();
    }

    public String toString() {
        return "OfflineMoonshineModelConfig(preprocessor=" + this.preprocessor + ", encoder=" + this.encoder + ", uncachedDecoder=" + this.uncachedDecoder + ", cachedDecoder=" + this.cachedDecoder + ", mergedDecoder=" + this.mergedDecoder + ')';
    }

    public OfflineMoonshineModelConfig(String preprocessor, String encoder, String uncachedDecoder, String cachedDecoder, String mergedDecoder) {
        Intrinsics.checkNotNullParameter(preprocessor, "preprocessor");
        Intrinsics.checkNotNullParameter(encoder, "encoder");
        Intrinsics.checkNotNullParameter(uncachedDecoder, "uncachedDecoder");
        Intrinsics.checkNotNullParameter(cachedDecoder, "cachedDecoder");
        Intrinsics.checkNotNullParameter(mergedDecoder, "mergedDecoder");
        this.preprocessor = preprocessor;
        this.encoder = encoder;
        this.uncachedDecoder = uncachedDecoder;
        this.cachedDecoder = cachedDecoder;
        this.mergedDecoder = mergedDecoder;
    }

    public /* synthetic */ OfflineMoonshineModelConfig(String str, String str2, String str3, String str4, String str5, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? "" : str, (i & 2) != 0 ? "" : str2, (i & 4) != 0 ? "" : str3, (i & 8) != 0 ? "" : str4, (i & 16) != 0 ? "" : str5);
    }

    public final String getPreprocessor() {
        return this.preprocessor;
    }

    public final void setPreprocessor(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.preprocessor = str;
    }

    public final String getEncoder() {
        return this.encoder;
    }

    public final void setEncoder(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.encoder = str;
    }

    public final String getUncachedDecoder() {
        return this.uncachedDecoder;
    }

    public final void setUncachedDecoder(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.uncachedDecoder = str;
    }

    public final String getCachedDecoder() {
        return this.cachedDecoder;
    }

    public final void setCachedDecoder(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.cachedDecoder = str;
    }

    public final String getMergedDecoder() {
        return this.mergedDecoder;
    }

    public final void setMergedDecoder(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.mergedDecoder = str;
    }
}
