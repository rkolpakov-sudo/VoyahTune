package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: AudioTagging.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u001f\b\u0086\b\u0018\u00002\u00020\u0001B7\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u0005¢\u0006\u0002\u0010\u000bJ\t\u0010\u001e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001f\u001a\u00020\u0005HÆ\u0003J\t\u0010 \u001a\u00020\u0007HÆ\u0003J\t\u0010!\u001a\u00020\tHÆ\u0003J\t\u0010\"\u001a\u00020\u0005HÆ\u0003J;\u0010#\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u0005HÆ\u0001J\u0013\u0010$\u001a\u00020\t2\b\u0010%\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010&\u001a\u00020\u0007HÖ\u0001J\t\u0010'\u001a\u00020\u0005HÖ\u0001R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u001a\u0010\b\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001a\u0010\n\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\r\"\u0004\b\u0019\u0010\u000fR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u001b\"\u0004\b\u001c\u0010\u001d¨\u0006("}, d2 = {"Lcom/k2fsa/sherpa/onnx/AudioTaggingModelConfig;", "", "zipformer", "Lcom/k2fsa/sherpa/onnx/OfflineZipformerAudioTaggingModelConfig;", "ced", "", "numThreads", "", "debug", "", "provider", "(Lcom/k2fsa/sherpa/onnx/OfflineZipformerAudioTaggingModelConfig;Ljava/lang/String;IZLjava/lang/String;)V", "getCed", "()Ljava/lang/String;", "setCed", "(Ljava/lang/String;)V", "getDebug", "()Z", "setDebug", "(Z)V", "getNumThreads", "()I", "setNumThreads", "(I)V", "getProvider", "setProvider", "getZipformer", "()Lcom/k2fsa/sherpa/onnx/OfflineZipformerAudioTaggingModelConfig;", "setZipformer", "(Lcom/k2fsa/sherpa/onnx/OfflineZipformerAudioTaggingModelConfig;)V", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class AudioTaggingModelConfig {
    private String ced;
    private boolean debug;
    private int numThreads;
    private String provider;
    private OfflineZipformerAudioTaggingModelConfig zipformer;

    public AudioTaggingModelConfig() {
        this(null, null, 0, false, null, 31, null);
    }

    public static /* synthetic */ AudioTaggingModelConfig copy$default(AudioTaggingModelConfig audioTaggingModelConfig, OfflineZipformerAudioTaggingModelConfig offlineZipformerAudioTaggingModelConfig, String str, int i, boolean z, String str2, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            offlineZipformerAudioTaggingModelConfig = audioTaggingModelConfig.zipformer;
        }
        if ((i2 & 2) != 0) {
            str = audioTaggingModelConfig.ced;
        }
        if ((i2 & 4) != 0) {
            i = audioTaggingModelConfig.numThreads;
        }
        if ((i2 & 8) != 0) {
            z = audioTaggingModelConfig.debug;
        }
        if ((i2 & 16) != 0) {
            str2 = audioTaggingModelConfig.provider;
        }
        String str3 = str2;
        int i3 = i;
        return audioTaggingModelConfig.copy(offlineZipformerAudioTaggingModelConfig, str, i3, z, str3);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final OfflineZipformerAudioTaggingModelConfig component1() {
        return this.zipformer;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String component2() {
        return this.ced;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final int component3() {
        return this.numThreads;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final boolean component4() {
        return this.debug;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final String component5() {
        return this.provider;
    }

    public final AudioTaggingModelConfig copy(OfflineZipformerAudioTaggingModelConfig zipformer, String ced, int numThreads, boolean debug, String provider) {
        Intrinsics.checkNotNullParameter(zipformer, "zipformer");
        Intrinsics.checkNotNullParameter(ced, "ced");
        Intrinsics.checkNotNullParameter(provider, "provider");
        return new AudioTaggingModelConfig(zipformer, ced, numThreads, debug, provider);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AudioTaggingModelConfig)) {
            return false;
        }
        AudioTaggingModelConfig audioTaggingModelConfig = (AudioTaggingModelConfig) other;
        return Intrinsics.areEqual(this.zipformer, audioTaggingModelConfig.zipformer) && Intrinsics.areEqual(this.ced, audioTaggingModelConfig.ced) && this.numThreads == audioTaggingModelConfig.numThreads && this.debug == audioTaggingModelConfig.debug && Intrinsics.areEqual(this.provider, audioTaggingModelConfig.provider);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v7, types: [int] */
    /* JADX WARN: Type inference failed for: r1v5, types: [int] */
    /* JADX WARN: Type inference failed for: r1v6 */
    /* JADX WARN: Type inference failed for: r1v7 */
    public int hashCode() {
        int iHashCode = ((((this.zipformer.hashCode() * 31) + this.ced.hashCode()) * 31) + Integer.hashCode(this.numThreads)) * 31;
        boolean z = this.debug;
        int r1 = z ? 1 : 0;
        if (z) {
            r1 = 1;
        }
        return ((iHashCode + r1) * 31) + this.provider.hashCode();
    }

    public String toString() {
        return "AudioTaggingModelConfig(zipformer=" + this.zipformer + ", ced=" + this.ced + ", numThreads=" + this.numThreads + ", debug=" + this.debug + ", provider=" + this.provider + ')';
    }

    public AudioTaggingModelConfig(OfflineZipformerAudioTaggingModelConfig zipformer, String ced, int i, boolean z, String provider) {
        Intrinsics.checkNotNullParameter(zipformer, "zipformer");
        Intrinsics.checkNotNullParameter(ced, "ced");
        Intrinsics.checkNotNullParameter(provider, "provider");
        this.zipformer = zipformer;
        this.ced = ced;
        this.numThreads = i;
        this.debug = z;
        this.provider = provider;
    }

    public /* synthetic */ AudioTaggingModelConfig(OfflineZipformerAudioTaggingModelConfig offlineZipformerAudioTaggingModelConfig, String str, int i, boolean z, String str2, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? new OfflineZipformerAudioTaggingModelConfig(null, 1, null) : offlineZipformerAudioTaggingModelConfig, (i2 & 2) != 0 ? "" : str, (i2 & 4) != 0 ? 1 : i, (i2 & 8) != 0 ? false : z, (i2 & 16) != 0 ? "cpu" : str2);
    }

    public final OfflineZipformerAudioTaggingModelConfig getZipformer() {
        return this.zipformer;
    }

    public final void setZipformer(OfflineZipformerAudioTaggingModelConfig offlineZipformerAudioTaggingModelConfig) {
        Intrinsics.checkNotNullParameter(offlineZipformerAudioTaggingModelConfig, "<set-?>");
        this.zipformer = offlineZipformerAudioTaggingModelConfig;
    }

    public final String getCed() {
        return this.ced;
    }

    public final void setCed(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.ced = str;
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
