package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OfflinePunctuation.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u001a\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003¢\u0006\u0002\u0010\tJ\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0005HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0007HÆ\u0003J\t\u0010\u001b\u001a\u00020\u0003HÆ\u0003J1\u0010\u001c\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u001d\u001a\u00020\u00072\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001f\u001a\u00020\u0005HÖ\u0001J\t\u0010 \u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u001a\u0010\b\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u000b\"\u0004\b\u0017\u0010\r¨\u0006!"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflinePunctuationModelConfig;", "", "ctTransformer", "", "numThreads", "", "debug", "", "provider", "(Ljava/lang/String;IZLjava/lang/String;)V", "getCtTransformer", "()Ljava/lang/String;", "setCtTransformer", "(Ljava/lang/String;)V", "getDebug", "()Z", "setDebug", "(Z)V", "getNumThreads", "()I", "setNumThreads", "(I)V", "getProvider", "setProvider", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class OfflinePunctuationModelConfig {
    private String ctTransformer;
    private boolean debug;
    private int numThreads;
    private String provider;

    public OfflinePunctuationModelConfig() {
        this(null, 0, false, null, 15, null);
    }

    public static /* synthetic */ OfflinePunctuationModelConfig copy$default(OfflinePunctuationModelConfig offlinePunctuationModelConfig, String str, int i, boolean z, String str2, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            str = offlinePunctuationModelConfig.ctTransformer;
        }
        if ((i2 & 2) != 0) {
            i = offlinePunctuationModelConfig.numThreads;
        }
        if ((i2 & 4) != 0) {
            z = offlinePunctuationModelConfig.debug;
        }
        if ((i2 & 8) != 0) {
            str2 = offlinePunctuationModelConfig.provider;
        }
        return offlinePunctuationModelConfig.copy(str, i, z, str2);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String getCtTransformer() {
        return this.ctTransformer;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final int getNumThreads() {
        return this.numThreads;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final boolean getDebug() {
        return this.debug;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final String getProvider() {
        return this.provider;
    }

    public final OfflinePunctuationModelConfig copy(String ctTransformer, int numThreads, boolean debug, String provider) {
        Intrinsics.checkNotNullParameter(ctTransformer, "ctTransformer");
        Intrinsics.checkNotNullParameter(provider, "provider");
        return new OfflinePunctuationModelConfig(ctTransformer, numThreads, debug, provider);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflinePunctuationModelConfig)) {
            return false;
        }
        OfflinePunctuationModelConfig offlinePunctuationModelConfig = (OfflinePunctuationModelConfig) other;
        return Intrinsics.areEqual(this.ctTransformer, offlinePunctuationModelConfig.ctTransformer) && this.numThreads == offlinePunctuationModelConfig.numThreads && this.debug == offlinePunctuationModelConfig.debug && Intrinsics.areEqual(this.provider, offlinePunctuationModelConfig.provider);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v5, types: [int] */
    /* JADX WARN: Type inference failed for: r1v3, types: [int] */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r1v5 */
    public int hashCode() {
        int iHashCode = ((this.ctTransformer.hashCode() * 31) + Integer.hashCode(this.numThreads)) * 31;
        boolean z = this.debug;
        ?? r1 = z;
        if (z) {
            r1 = 1;
        }
        return ((iHashCode + r1) * 31) + this.provider.hashCode();
    }

    public String toString() {
        return "OfflinePunctuationModelConfig(ctTransformer=" + this.ctTransformer + ", numThreads=" + this.numThreads + ", debug=" + this.debug + ", provider=" + this.provider + ')';
    }

    public OfflinePunctuationModelConfig(String ctTransformer, int i, boolean z, String provider) {
        Intrinsics.checkNotNullParameter(ctTransformer, "ctTransformer");
        Intrinsics.checkNotNullParameter(provider, "provider");
        this.ctTransformer = ctTransformer;
        this.numThreads = i;
        this.debug = z;
        this.provider = provider;
    }

    public /* synthetic */ OfflinePunctuationModelConfig(String str, int i, boolean z, String str2, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? "" : str, (i2 & 2) != 0 ? 1 : i, (i2 & 4) != 0 ? false : z, (i2 & 8) != 0 ? "cpu" : str2);
    }

    public final String getCtTransformer() {
        return this.ctTransformer;
    }

    public final void setCtTransformer(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.ctTransformer = str;
    }

    public final int getNumThreads() {
        return this.numThreads;
    }

    public final void setNumThreads(int i) {
        this.numThreads = i;
    }

    public final boolean getDebug() {
        return this.debug;
    }

    public final void setDebug(boolean z) {
        this.debug = z;
    }

    public final String getProvider() {
        return this.provider;
    }

    public final void setProvider(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.provider = str;
    }
}
