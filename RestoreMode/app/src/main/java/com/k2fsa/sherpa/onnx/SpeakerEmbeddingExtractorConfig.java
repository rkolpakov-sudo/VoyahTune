package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: SpeakerEmbeddingExtractorConfig.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0019\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003¢\u0006\u0002\u0010\tJ\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0007HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0003HÆ\u0003J1\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u001c\u001a\u00020\u00072\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001e\u001a\u00020\u0005HÖ\u0001J\t\u0010\u001f\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001a\u0010\b\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u000f\"\u0004\b\u0015\u0010\u0016¨\u0006 "}, d2 = {"Lcom/k2fsa/sherpa/onnx/SpeakerEmbeddingExtractorConfig;", "", "model", "", "numThreads", "", "debug", "", "provider", "(Ljava/lang/String;IZLjava/lang/String;)V", "getDebug", "()Z", "setDebug", "(Z)V", "getModel", "()Ljava/lang/String;", "getNumThreads", "()I", "setNumThreads", "(I)V", "getProvider", "setProvider", "(Ljava/lang/String;)V", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class SpeakerEmbeddingExtractorConfig {
    private boolean debug;
    private final String model;
    private int numThreads;
    private String provider;

    public SpeakerEmbeddingExtractorConfig() {
        this(null, 0, false, null, 15, null);
    }

    public static /* synthetic */ SpeakerEmbeddingExtractorConfig copy$default(SpeakerEmbeddingExtractorConfig speakerEmbeddingExtractorConfig, String str, int i, boolean z, String str2, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            str = speakerEmbeddingExtractorConfig.model;
        }
        if ((i2 & 2) != 0) {
            i = speakerEmbeddingExtractorConfig.numThreads;
        }
        if ((i2 & 4) != 0) {
            z = speakerEmbeddingExtractorConfig.debug;
        }
        if ((i2 & 8) != 0) {
            str2 = speakerEmbeddingExtractorConfig.provider;
        }
        return speakerEmbeddingExtractorConfig.copy(str, i, z, str2);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String component1() {
        return this.model;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final int component2() {
        return this.numThreads;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final boolean component3() {
        return this.debug;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final String component4() {
        return this.provider;
    }

    public final SpeakerEmbeddingExtractorConfig copy(String model, int numThreads, boolean debug, String provider) {
        Intrinsics.checkNotNullParameter(model, "model");
        Intrinsics.checkNotNullParameter(provider, "provider");
        return new SpeakerEmbeddingExtractorConfig(model, numThreads, debug, provider);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SpeakerEmbeddingExtractorConfig)) {
            return false;
        }
        SpeakerEmbeddingExtractorConfig speakerEmbeddingExtractorConfig = (SpeakerEmbeddingExtractorConfig) other;
        return Intrinsics.areEqual(this.model, speakerEmbeddingExtractorConfig.model) && this.numThreads == speakerEmbeddingExtractorConfig.numThreads && this.debug == speakerEmbeddingExtractorConfig.debug && Intrinsics.areEqual(this.provider, speakerEmbeddingExtractorConfig.provider);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v5, types: [int] */
    /* JADX WARN: Type inference failed for: r1v3, types: [int] */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r1v5 */
    public int hashCode() {
        int iHashCode = ((this.model.hashCode() * 31) + Integer.hashCode(this.numThreads)) * 31;
        boolean z = this.debug;
        int r1 = z ? 1 : 0;
        if (z) {
            r1 = 1;
        }
        return ((iHashCode + r1) * 31) + this.provider.hashCode();
    }

    public String toString() {
        return "SpeakerEmbeddingExtractorConfig(model=" + this.model + ", numThreads=" + this.numThreads + ", debug=" + this.debug + ", provider=" + this.provider + ')';
    }

    public SpeakerEmbeddingExtractorConfig(String model, int i, boolean z, String provider) {
        Intrinsics.checkNotNullParameter(model, "model");
        Intrinsics.checkNotNullParameter(provider, "provider");
        this.model = model;
        this.numThreads = i;
        this.debug = z;
        this.provider = provider;
    }

    public /* synthetic */ SpeakerEmbeddingExtractorConfig(String str, int i, boolean z, String str2, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? "" : str, (i2 & 2) != 0 ? 1 : i, (i2 & 4) != 0 ? false : z, (i2 & 8) != 0 ? "cpu" : str2);
    }

    public final String getModel() {
        return this.model;
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
