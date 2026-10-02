package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.scheduling.WorkQueueKt;

/* JADX INFO: compiled from: Tts.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b9\b\u0086\b\u0018\u00002\u00020\u0001Bi\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0013\u0012\b\b\u0002\u0010\u0014\u001a\u00020\u0015¢\u0006\u0002\u0010\u0016J\t\u0010?\u001a\u00020\u0003HÆ\u0003J\t\u0010@\u001a\u00020\u0015HÆ\u0003J\t\u0010A\u001a\u00020\u0005HÆ\u0003J\t\u0010B\u001a\u00020\u0007HÆ\u0003J\t\u0010C\u001a\u00020\tHÆ\u0003J\t\u0010D\u001a\u00020\u000bHÆ\u0003J\t\u0010E\u001a\u00020\rHÆ\u0003J\t\u0010F\u001a\u00020\u000fHÆ\u0003J\t\u0010G\u001a\u00020\u0011HÆ\u0003J\t\u0010H\u001a\u00020\u0013HÆ\u0003Jm\u0010I\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u00132\b\b\u0002\u0010\u0014\u001a\u00020\u0015HÆ\u0001J\u0013\u0010J\u001a\u00020\u00132\b\u0010K\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010L\u001a\u00020\u0011HÖ\u0001J\t\u0010M\u001a\u00020\u0015HÖ\u0001R\u001a\u0010\u0012\u001a\u00020\u0013X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001aR\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010 \"\u0004\b!\u0010\"R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b#\u0010$\"\u0004\b%\u0010&R\u001a\u0010\u0010\u001a\u00020\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b'\u0010(\"\u0004\b)\u0010*R\u001a\u0010\f\u001a\u00020\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b+\u0010,\"\u0004\b-\u0010.R\u001a\u0010\u0014\u001a\u00020\u0015X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b/\u00100\"\u0004\b1\u00102R\u001a\u0010\u000e\u001a\u00020\u000fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b3\u00104\"\u0004\b5\u00106R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b7\u00108\"\u0004\b9\u0010:R\u001a\u0010\b\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b;\u0010<\"\u0004\b=\u0010>¨\u0006N"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineTtsModelConfig;", "", "vits", "Lcom/k2fsa/sherpa/onnx/OfflineTtsVitsModelConfig;", "matcha", "Lcom/k2fsa/sherpa/onnx/OfflineTtsMatchaModelConfig;", "kokoro", "Lcom/k2fsa/sherpa/onnx/OfflineTtsKokoroModelConfig;", "zipvoice", "Lcom/k2fsa/sherpa/onnx/OfflineTtsZipVoiceModelConfig;", "kitten", "Lcom/k2fsa/sherpa/onnx/OfflineTtsKittenModelConfig;", "pocket", "Lcom/k2fsa/sherpa/onnx/OfflineTtsPocketModelConfig;", "supertonic", "Lcom/k2fsa/sherpa/onnx/OfflineTtsSupertonicModelConfig;", "numThreads", "", "debug", "", "provider", "", "(Lcom/k2fsa/sherpa/onnx/OfflineTtsVitsModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineTtsMatchaModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineTtsKokoroModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineTtsZipVoiceModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineTtsKittenModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineTtsPocketModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineTtsSupertonicModelConfig;IZLjava/lang/String;)V", "getDebug", "()Z", "setDebug", "(Z)V", "getKitten", "()Lcom/k2fsa/sherpa/onnx/OfflineTtsKittenModelConfig;", "setKitten", "(Lcom/k2fsa/sherpa/onnx/OfflineTtsKittenModelConfig;)V", "getKokoro", "()Lcom/k2fsa/sherpa/onnx/OfflineTtsKokoroModelConfig;", "setKokoro", "(Lcom/k2fsa/sherpa/onnx/OfflineTtsKokoroModelConfig;)V", "getMatcha", "()Lcom/k2fsa/sherpa/onnx/OfflineTtsMatchaModelConfig;", "setMatcha", "(Lcom/k2fsa/sherpa/onnx/OfflineTtsMatchaModelConfig;)V", "getNumThreads", "()I", "setNumThreads", "(I)V", "getPocket", "()Lcom/k2fsa/sherpa/onnx/OfflineTtsPocketModelConfig;", "setPocket", "(Lcom/k2fsa/sherpa/onnx/OfflineTtsPocketModelConfig;)V", "getProvider", "()Ljava/lang/String;", "setProvider", "(Ljava/lang/String;)V", "getSupertonic", "()Lcom/k2fsa/sherpa/onnx/OfflineTtsSupertonicModelConfig;", "setSupertonic", "(Lcom/k2fsa/sherpa/onnx/OfflineTtsSupertonicModelConfig;)V", "getVits", "()Lcom/k2fsa/sherpa/onnx/OfflineTtsVitsModelConfig;", "setVits", "(Lcom/k2fsa/sherpa/onnx/OfflineTtsVitsModelConfig;)V", "getZipvoice", "()Lcom/k2fsa/sherpa/onnx/OfflineTtsZipVoiceModelConfig;", "setZipvoice", "(Lcom/k2fsa/sherpa/onnx/OfflineTtsZipVoiceModelConfig;)V", "component1", "component10", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class OfflineTtsModelConfig {
    private boolean debug;
    private OfflineTtsKittenModelConfig kitten;
    private OfflineTtsKokoroModelConfig kokoro;
    private OfflineTtsMatchaModelConfig matcha;
    private int numThreads;
    private OfflineTtsPocketModelConfig pocket;
    private String provider;
    private OfflineTtsSupertonicModelConfig supertonic;
    private OfflineTtsVitsModelConfig vits;
    private OfflineTtsZipVoiceModelConfig zipvoice;

    public OfflineTtsModelConfig() {
        this(null, null, null, null, null, null, null, 0, false, null, 1023, null);
    }

    public static /* synthetic */ OfflineTtsModelConfig copy$default(OfflineTtsModelConfig offlineTtsModelConfig, OfflineTtsVitsModelConfig offlineTtsVitsModelConfig, OfflineTtsMatchaModelConfig offlineTtsMatchaModelConfig, OfflineTtsKokoroModelConfig offlineTtsKokoroModelConfig, OfflineTtsZipVoiceModelConfig offlineTtsZipVoiceModelConfig, OfflineTtsKittenModelConfig offlineTtsKittenModelConfig, OfflineTtsPocketModelConfig offlineTtsPocketModelConfig, OfflineTtsSupertonicModelConfig offlineTtsSupertonicModelConfig, int i, boolean z, String str, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            offlineTtsVitsModelConfig = offlineTtsModelConfig.vits;
        }
        if ((i2 & 2) != 0) {
            offlineTtsMatchaModelConfig = offlineTtsModelConfig.matcha;
        }
        if ((i2 & 4) != 0) {
            offlineTtsKokoroModelConfig = offlineTtsModelConfig.kokoro;
        }
        if ((i2 & 8) != 0) {
            offlineTtsZipVoiceModelConfig = offlineTtsModelConfig.zipvoice;
        }
        if ((i2 & 16) != 0) {
            offlineTtsKittenModelConfig = offlineTtsModelConfig.kitten;
        }
        if ((i2 & 32) != 0) {
            offlineTtsPocketModelConfig = offlineTtsModelConfig.pocket;
        }
        if ((i2 & 64) != 0) {
            offlineTtsSupertonicModelConfig = offlineTtsModelConfig.supertonic;
        }
        if ((i2 & 128) != 0) {
            i = offlineTtsModelConfig.numThreads;
        }
        if ((i2 & 256) != 0) {
            z = offlineTtsModelConfig.debug;
        }
        if ((i2 & 512) != 0) {
            str = offlineTtsModelConfig.provider;
        }
        boolean z2 = z;
        String str2 = str;
        OfflineTtsSupertonicModelConfig offlineTtsSupertonicModelConfig2 = offlineTtsSupertonicModelConfig;
        int i3 = i;
        OfflineTtsKittenModelConfig offlineTtsKittenModelConfig2 = offlineTtsKittenModelConfig;
        OfflineTtsPocketModelConfig offlineTtsPocketModelConfig2 = offlineTtsPocketModelConfig;
        return offlineTtsModelConfig.copy(offlineTtsVitsModelConfig, offlineTtsMatchaModelConfig, offlineTtsKokoroModelConfig, offlineTtsZipVoiceModelConfig, offlineTtsKittenModelConfig2, offlineTtsPocketModelConfig2, offlineTtsSupertonicModelConfig2, i3, z2, str2);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final OfflineTtsVitsModelConfig component1() {
        return this.vits;
    }

    /* JADX INFO: renamed from: component10, reason: from getter */
    public final String component10() {
        return this.provider;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final OfflineTtsMatchaModelConfig component2() {
        return this.matcha;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final OfflineTtsKokoroModelConfig component3() {
        return this.kokoro;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final OfflineTtsZipVoiceModelConfig component4() {
        return this.zipvoice;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final OfflineTtsKittenModelConfig component5() {
        return this.kitten;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final OfflineTtsPocketModelConfig component6() {
        return this.pocket;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final OfflineTtsSupertonicModelConfig component7() {
        return this.supertonic;
    }

    /* JADX INFO: renamed from: component8, reason: from getter */
    public final int component8() {
        return this.numThreads;
    }

    /* JADX INFO: renamed from: component9, reason: from getter */
    public final boolean component9() {
        return this.debug;
    }

    public final OfflineTtsModelConfig copy(OfflineTtsVitsModelConfig vits, OfflineTtsMatchaModelConfig matcha, OfflineTtsKokoroModelConfig kokoro, OfflineTtsZipVoiceModelConfig zipvoice, OfflineTtsKittenModelConfig kitten, OfflineTtsPocketModelConfig pocket, OfflineTtsSupertonicModelConfig supertonic, int numThreads, boolean debug, String provider) {
        Intrinsics.checkNotNullParameter(vits, "vits");
        Intrinsics.checkNotNullParameter(matcha, "matcha");
        Intrinsics.checkNotNullParameter(kokoro, "kokoro");
        Intrinsics.checkNotNullParameter(zipvoice, "zipvoice");
        Intrinsics.checkNotNullParameter(kitten, "kitten");
        Intrinsics.checkNotNullParameter(pocket, "pocket");
        Intrinsics.checkNotNullParameter(supertonic, "supertonic");
        Intrinsics.checkNotNullParameter(provider, "provider");
        return new OfflineTtsModelConfig(vits, matcha, kokoro, zipvoice, kitten, pocket, supertonic, numThreads, debug, provider);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineTtsModelConfig)) {
            return false;
        }
        OfflineTtsModelConfig offlineTtsModelConfig = (OfflineTtsModelConfig) other;
        return Intrinsics.areEqual(this.vits, offlineTtsModelConfig.vits) && Intrinsics.areEqual(this.matcha, offlineTtsModelConfig.matcha) && Intrinsics.areEqual(this.kokoro, offlineTtsModelConfig.kokoro) && Intrinsics.areEqual(this.zipvoice, offlineTtsModelConfig.zipvoice) && Intrinsics.areEqual(this.kitten, offlineTtsModelConfig.kitten) && Intrinsics.areEqual(this.pocket, offlineTtsModelConfig.pocket) && Intrinsics.areEqual(this.supertonic, offlineTtsModelConfig.supertonic) && this.numThreads == offlineTtsModelConfig.numThreads && this.debug == offlineTtsModelConfig.debug && Intrinsics.areEqual(this.provider, offlineTtsModelConfig.provider);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v17, types: [int] */
    /* JADX WARN: Type inference failed for: r1v15, types: [int] */
    /* JADX WARN: Type inference failed for: r1v16 */
    /* JADX WARN: Type inference failed for: r1v17 */
    public int hashCode() {
        int iHashCode = ((((((((((((((this.vits.hashCode() * 31) + this.matcha.hashCode()) * 31) + this.kokoro.hashCode()) * 31) + this.zipvoice.hashCode()) * 31) + this.kitten.hashCode()) * 31) + this.pocket.hashCode()) * 31) + this.supertonic.hashCode()) * 31) + Integer.hashCode(this.numThreads)) * 31;
        boolean z = this.debug;
        int r1 = z ? 1 : 0;
        if (z) {
            r1 = 1;
        }
        return ((iHashCode + r1) * 31) + this.provider.hashCode();
    }

    public String toString() {
        return "OfflineTtsModelConfig(vits=" + this.vits + ", matcha=" + this.matcha + ", kokoro=" + this.kokoro + ", zipvoice=" + this.zipvoice + ", kitten=" + this.kitten + ", pocket=" + this.pocket + ", supertonic=" + this.supertonic + ", numThreads=" + this.numThreads + ", debug=" + this.debug + ", provider=" + this.provider + ')';
    }

    public OfflineTtsModelConfig(OfflineTtsVitsModelConfig vits, OfflineTtsMatchaModelConfig matcha, OfflineTtsKokoroModelConfig kokoro, OfflineTtsZipVoiceModelConfig zipvoice, OfflineTtsKittenModelConfig kitten, OfflineTtsPocketModelConfig pocket, OfflineTtsSupertonicModelConfig supertonic, int i, boolean z, String provider) {
        Intrinsics.checkNotNullParameter(vits, "vits");
        Intrinsics.checkNotNullParameter(matcha, "matcha");
        Intrinsics.checkNotNullParameter(kokoro, "kokoro");
        Intrinsics.checkNotNullParameter(zipvoice, "zipvoice");
        Intrinsics.checkNotNullParameter(kitten, "kitten");
        Intrinsics.checkNotNullParameter(pocket, "pocket");
        Intrinsics.checkNotNullParameter(supertonic, "supertonic");
        Intrinsics.checkNotNullParameter(provider, "provider");
        this.vits = vits;
        this.matcha = matcha;
        this.kokoro = kokoro;
        this.zipvoice = zipvoice;
        this.kitten = kitten;
        this.pocket = pocket;
        this.supertonic = supertonic;
        this.numThreads = i;
        this.debug = z;
        this.provider = provider;
    }

    public /* synthetic */ OfflineTtsModelConfig(OfflineTtsVitsModelConfig offlineTtsVitsModelConfig, OfflineTtsMatchaModelConfig offlineTtsMatchaModelConfig, OfflineTtsKokoroModelConfig offlineTtsKokoroModelConfig, OfflineTtsZipVoiceModelConfig offlineTtsZipVoiceModelConfig, OfflineTtsKittenModelConfig offlineTtsKittenModelConfig, OfflineTtsPocketModelConfig offlineTtsPocketModelConfig, OfflineTtsSupertonicModelConfig offlineTtsSupertonicModelConfig, int i, boolean z, String str, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? new OfflineTtsVitsModelConfig(null, null, null, null, null, 0.0f, 0.0f, 0.0f, 255, null) : offlineTtsVitsModelConfig, (i2 & 2) != 0 ? new OfflineTtsMatchaModelConfig(null, null, null, null, null, null, 0.0f, 0.0f, 255, null) : offlineTtsMatchaModelConfig, (i2 & 4) != 0 ? new OfflineTtsKokoroModelConfig(null, null, null, null, null, null, null, 0.0f, 255, null) : offlineTtsKokoroModelConfig, (i2 & 8) != 0 ? new OfflineTtsZipVoiceModelConfig(null, null, null, null, null, null, 0.0f, 0.0f, 0.0f, 0.0f, 1023, null) : offlineTtsZipVoiceModelConfig, (i2 & 16) != 0 ? new OfflineTtsKittenModelConfig(null, null, null, null, 0.0f, 31, null) : offlineTtsKittenModelConfig, (i2 & 32) != 0 ? new OfflineTtsPocketModelConfig(null, null, null, null, null, null, null, 0, 255, null) : offlineTtsPocketModelConfig, (i2 & 64) != 0 ? new OfflineTtsSupertonicModelConfig(null, null, null, null, null, null, null, WorkQueueKt.MASK, null) : offlineTtsSupertonicModelConfig, (i2 & 128) != 0 ? 1 : i, (i2 & 256) != 0 ? false : z, (i2 & 512) != 0 ? "cpu" : str);
    }

    public final OfflineTtsVitsModelConfig getVits() {
        return this.vits;
    }

    public final void setVits(OfflineTtsVitsModelConfig offlineTtsVitsModelConfig) {
        Intrinsics.checkNotNullParameter(offlineTtsVitsModelConfig, "<set-?>");
        this.vits = offlineTtsVitsModelConfig;
    }

    public final OfflineTtsMatchaModelConfig getMatcha() {
        return this.matcha;
    }

    public final void setMatcha(OfflineTtsMatchaModelConfig offlineTtsMatchaModelConfig) {
        Intrinsics.checkNotNullParameter(offlineTtsMatchaModelConfig, "<set-?>");
        this.matcha = offlineTtsMatchaModelConfig;
    }

    public final OfflineTtsKokoroModelConfig getKokoro() {
        return this.kokoro;
    }

    public final void setKokoro(OfflineTtsKokoroModelConfig offlineTtsKokoroModelConfig) {
        Intrinsics.checkNotNullParameter(offlineTtsKokoroModelConfig, "<set-?>");
        this.kokoro = offlineTtsKokoroModelConfig;
    }

    public final OfflineTtsZipVoiceModelConfig getZipvoice() {
        return this.zipvoice;
    }

    public final void setZipvoice(OfflineTtsZipVoiceModelConfig offlineTtsZipVoiceModelConfig) {
        Intrinsics.checkNotNullParameter(offlineTtsZipVoiceModelConfig, "<set-?>");
        this.zipvoice = offlineTtsZipVoiceModelConfig;
    }

    public final OfflineTtsKittenModelConfig getKitten() {
        return this.kitten;
    }

    public final void setKitten(OfflineTtsKittenModelConfig offlineTtsKittenModelConfig) {
        Intrinsics.checkNotNullParameter(offlineTtsKittenModelConfig, "<set-?>");
        this.kitten = offlineTtsKittenModelConfig;
    }

    public final OfflineTtsPocketModelConfig getPocket() {
        return this.pocket;
    }

    public final void setPocket(OfflineTtsPocketModelConfig offlineTtsPocketModelConfig) {
        Intrinsics.checkNotNullParameter(offlineTtsPocketModelConfig, "<set-?>");
        this.pocket = offlineTtsPocketModelConfig;
    }

    public final OfflineTtsSupertonicModelConfig getSupertonic() {
        return this.supertonic;
    }

    public final void setSupertonic(OfflineTtsSupertonicModelConfig offlineTtsSupertonicModelConfig) {
        Intrinsics.checkNotNullParameter(offlineTtsSupertonicModelConfig, "<set-?>");
        this.supertonic = offlineTtsSupertonicModelConfig;
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
