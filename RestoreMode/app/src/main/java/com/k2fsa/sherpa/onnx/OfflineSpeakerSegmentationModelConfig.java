package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OfflineSpeakerDiarization.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u001b\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t¢\u0006\u0002\u0010\nJ\t\u0010\u001b\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001c\u001a\u00020\u0005HÆ\u0003J\t\u0010\u001d\u001a\u00020\u0007HÆ\u0003J\t\u0010\u001e\u001a\u00020\tHÆ\u0003J1\u0010\u001f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tHÆ\u0001J\u0013\u0010 \u001a\u00020\u00072\b\u0010!\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\"\u001a\u00020\u0005HÖ\u0001J\t\u0010#\u001a\u00020\tHÖ\u0001R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001a\u0010\b\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001a¨\u0006$"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineSpeakerSegmentationModelConfig;", "", "pyannote", "Lcom/k2fsa/sherpa/onnx/OfflineSpeakerSegmentationPyannoteModelConfig;", "numThreads", "", "debug", "", "provider", "", "(Lcom/k2fsa/sherpa/onnx/OfflineSpeakerSegmentationPyannoteModelConfig;IZLjava/lang/String;)V", "getDebug", "()Z", "setDebug", "(Z)V", "getNumThreads", "()I", "setNumThreads", "(I)V", "getProvider", "()Ljava/lang/String;", "setProvider", "(Ljava/lang/String;)V", "getPyannote", "()Lcom/k2fsa/sherpa/onnx/OfflineSpeakerSegmentationPyannoteModelConfig;", "setPyannote", "(Lcom/k2fsa/sherpa/onnx/OfflineSpeakerSegmentationPyannoteModelConfig;)V", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class OfflineSpeakerSegmentationModelConfig {
    private boolean debug;
    private int numThreads;
    private String provider;
    private OfflineSpeakerSegmentationPyannoteModelConfig pyannote;

    public OfflineSpeakerSegmentationModelConfig() {
        this(null, 0, false, null, 15, null);
    }

    public static /* synthetic */ OfflineSpeakerSegmentationModelConfig copy$default(OfflineSpeakerSegmentationModelConfig offlineSpeakerSegmentationModelConfig, OfflineSpeakerSegmentationPyannoteModelConfig offlineSpeakerSegmentationPyannoteModelConfig, int i, boolean z, String str, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            offlineSpeakerSegmentationPyannoteModelConfig = offlineSpeakerSegmentationModelConfig.pyannote;
        }
        if ((i2 & 2) != 0) {
            i = offlineSpeakerSegmentationModelConfig.numThreads;
        }
        if ((i2 & 4) != 0) {
            z = offlineSpeakerSegmentationModelConfig.debug;
        }
        if ((i2 & 8) != 0) {
            str = offlineSpeakerSegmentationModelConfig.provider;
        }
        return offlineSpeakerSegmentationModelConfig.copy(offlineSpeakerSegmentationPyannoteModelConfig, i, z, str);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final OfflineSpeakerSegmentationPyannoteModelConfig component1() {
        return this.pyannote;
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

    public final OfflineSpeakerSegmentationModelConfig copy(OfflineSpeakerSegmentationPyannoteModelConfig pyannote, int numThreads, boolean debug, String provider) {
        Intrinsics.checkNotNullParameter(pyannote, "pyannote");
        Intrinsics.checkNotNullParameter(provider, "provider");
        return new OfflineSpeakerSegmentationModelConfig(pyannote, numThreads, debug, provider);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineSpeakerSegmentationModelConfig)) {
            return false;
        }
        OfflineSpeakerSegmentationModelConfig offlineSpeakerSegmentationModelConfig = (OfflineSpeakerSegmentationModelConfig) other;
        return Intrinsics.areEqual(this.pyannote, offlineSpeakerSegmentationModelConfig.pyannote) && this.numThreads == offlineSpeakerSegmentationModelConfig.numThreads && this.debug == offlineSpeakerSegmentationModelConfig.debug && Intrinsics.areEqual(this.provider, offlineSpeakerSegmentationModelConfig.provider);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v5, types: [int] */
    /* JADX WARN: Type inference failed for: r1v3, types: [int] */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r1v5 */
    public int hashCode() {
        int iHashCode = ((this.pyannote.hashCode() * 31) + Integer.hashCode(this.numThreads)) * 31;
        boolean z = this.debug;
        int r1 = z ? 1 : 0;
        if (z) {
            r1 = 1;
        }
        return ((iHashCode + r1) * 31) + this.provider.hashCode();
    }

    public String toString() {
        return "OfflineSpeakerSegmentationModelConfig(pyannote=" + this.pyannote + ", numThreads=" + this.numThreads + ", debug=" + this.debug + ", provider=" + this.provider + ')';
    }

    public OfflineSpeakerSegmentationModelConfig(OfflineSpeakerSegmentationPyannoteModelConfig pyannote, int i, boolean z, String provider) {
        Intrinsics.checkNotNullParameter(pyannote, "pyannote");
        Intrinsics.checkNotNullParameter(provider, "provider");
        this.pyannote = pyannote;
        this.numThreads = i;
        this.debug = z;
        this.provider = provider;
    }

    public /* synthetic */ OfflineSpeakerSegmentationModelConfig(OfflineSpeakerSegmentationPyannoteModelConfig offlineSpeakerSegmentationPyannoteModelConfig, int i, boolean z, String str, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? new OfflineSpeakerSegmentationPyannoteModelConfig(null, 0.0f, 3, null) : offlineSpeakerSegmentationPyannoteModelConfig, (i2 & 2) != 0 ? 1 : i, (i2 & 4) != 0 ? false : z, (i2 & 8) != 0 ? "cpu" : str);
    }

    public final OfflineSpeakerSegmentationPyannoteModelConfig getPyannote() {
        return this.pyannote;
    }

    public final void setPyannote(OfflineSpeakerSegmentationPyannoteModelConfig offlineSpeakerSegmentationPyannoteModelConfig) {
        Intrinsics.checkNotNullParameter(offlineSpeakerSegmentationPyannoteModelConfig, "<set-?>");
        this.pyannote = offlineSpeakerSegmentationPyannoteModelConfig;
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
