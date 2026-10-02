package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: SpokenLanguageIdentification.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u001b\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t¢\u0006\u0002\u0010\nJ\t\u0010\u001b\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001c\u001a\u00020\u0005HÆ\u0003J\t\u0010\u001d\u001a\u00020\u0007HÆ\u0003J\t\u0010\u001e\u001a\u00020\tHÆ\u0003J1\u0010\u001f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tHÆ\u0001J\u0013\u0010 \u001a\u00020\u00072\b\u0010!\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\"\u001a\u00020\u0005HÖ\u0001J\t\u0010#\u001a\u00020\tHÖ\u0001R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001a\u0010\b\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001a¨\u0006$"}, d2 = {"Lcom/k2fsa/sherpa/onnx/SpokenLanguageIdentificationConfig;", "", "whisper", "Lcom/k2fsa/sherpa/onnx/SpokenLanguageIdentificationWhisperConfig;", "numThreads", "", "debug", "", "provider", "", "(Lcom/k2fsa/sherpa/onnx/SpokenLanguageIdentificationWhisperConfig;IZLjava/lang/String;)V", "getDebug", "()Z", "setDebug", "(Z)V", "getNumThreads", "()I", "setNumThreads", "(I)V", "getProvider", "()Ljava/lang/String;", "setProvider", "(Ljava/lang/String;)V", "getWhisper", "()Lcom/k2fsa/sherpa/onnx/SpokenLanguageIdentificationWhisperConfig;", "setWhisper", "(Lcom/k2fsa/sherpa/onnx/SpokenLanguageIdentificationWhisperConfig;)V", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class SpokenLanguageIdentificationConfig {
    private boolean debug;
    private int numThreads;
    private String provider;
    private SpokenLanguageIdentificationWhisperConfig whisper;

    public SpokenLanguageIdentificationConfig() {
        this(null, 0, false, null, 15, null);
    }

    public static /* synthetic */ SpokenLanguageIdentificationConfig copy$default(SpokenLanguageIdentificationConfig spokenLanguageIdentificationConfig, SpokenLanguageIdentificationWhisperConfig spokenLanguageIdentificationWhisperConfig, int i, boolean z, String str, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            spokenLanguageIdentificationWhisperConfig = spokenLanguageIdentificationConfig.whisper;
        }
        if ((i2 & 2) != 0) {
            i = spokenLanguageIdentificationConfig.numThreads;
        }
        if ((i2 & 4) != 0) {
            z = spokenLanguageIdentificationConfig.debug;
        }
        if ((i2 & 8) != 0) {
            str = spokenLanguageIdentificationConfig.provider;
        }
        return spokenLanguageIdentificationConfig.copy(spokenLanguageIdentificationWhisperConfig, i, z, str);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final SpokenLanguageIdentificationWhisperConfig getWhisper() {
        return this.whisper;
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

    public final SpokenLanguageIdentificationConfig copy(SpokenLanguageIdentificationWhisperConfig whisper, int numThreads, boolean debug, String provider) {
        Intrinsics.checkNotNullParameter(whisper, "whisper");
        Intrinsics.checkNotNullParameter(provider, "provider");
        return new SpokenLanguageIdentificationConfig(whisper, numThreads, debug, provider);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SpokenLanguageIdentificationConfig)) {
            return false;
        }
        SpokenLanguageIdentificationConfig spokenLanguageIdentificationConfig = (SpokenLanguageIdentificationConfig) other;
        return Intrinsics.areEqual(this.whisper, spokenLanguageIdentificationConfig.whisper) && this.numThreads == spokenLanguageIdentificationConfig.numThreads && this.debug == spokenLanguageIdentificationConfig.debug && Intrinsics.areEqual(this.provider, spokenLanguageIdentificationConfig.provider);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v5, types: [int] */
    /* JADX WARN: Type inference failed for: r1v3, types: [int] */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r1v5 */
    public int hashCode() {
        int iHashCode = ((this.whisper.hashCode() * 31) + Integer.hashCode(this.numThreads)) * 31;
        boolean z = this.debug;
        ?? r1 = z;
        if (z) {
            r1 = 1;
        }
        return ((iHashCode + r1) * 31) + this.provider.hashCode();
    }

    public String toString() {
        return "SpokenLanguageIdentificationConfig(whisper=" + this.whisper + ", numThreads=" + this.numThreads + ", debug=" + this.debug + ", provider=" + this.provider + ')';
    }

    public SpokenLanguageIdentificationConfig(SpokenLanguageIdentificationWhisperConfig whisper, int i, boolean z, String provider) {
        Intrinsics.checkNotNullParameter(whisper, "whisper");
        Intrinsics.checkNotNullParameter(provider, "provider");
        this.whisper = whisper;
        this.numThreads = i;
        this.debug = z;
        this.provider = provider;
    }

    public /* synthetic */ SpokenLanguageIdentificationConfig(SpokenLanguageIdentificationWhisperConfig spokenLanguageIdentificationWhisperConfig, int i, boolean z, String str, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? new SpokenLanguageIdentificationWhisperConfig(null, null, 0, 7, null) : spokenLanguageIdentificationWhisperConfig, (i2 & 2) != 0 ? 1 : i, (i2 & 4) != 0 ? false : z, (i2 & 8) != 0 ? "cpu" : str);
    }

    public final SpokenLanguageIdentificationWhisperConfig getWhisper() {
        return this.whisper;
    }

    public final void setWhisper(SpokenLanguageIdentificationWhisperConfig spokenLanguageIdentificationWhisperConfig) {
        Intrinsics.checkNotNullParameter(spokenLanguageIdentificationWhisperConfig, "<set-?>");
        this.whisper = spokenLanguageIdentificationWhisperConfig;
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
