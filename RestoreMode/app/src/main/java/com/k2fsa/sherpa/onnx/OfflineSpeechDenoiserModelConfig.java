package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OfflineSpeechDenoiser.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b \b\u0086\b\u0018\u00002\u00020\u0001B7\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b¢\u0006\u0002\u0010\fJ\t\u0010!\u001a\u00020\u0003HÆ\u0003J\t\u0010\"\u001a\u00020\u0005HÆ\u0003J\t\u0010#\u001a\u00020\u0007HÆ\u0003J\t\u0010$\u001a\u00020\tHÆ\u0003J\t\u0010%\u001a\u00020\u000bHÆ\u0003J;\u0010&\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000bHÆ\u0001J\u0013\u0010'\u001a\u00020\t2\b\u0010(\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010)\u001a\u00020\u0007HÖ\u0001J\t\u0010*\u001a\u00020\u000bHÖ\u0001R\u001a\u0010\b\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u001e\"\u0004\b\u001f\u0010 ¨\u0006+"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineSpeechDenoiserModelConfig;", "", "gtcrn", "Lcom/k2fsa/sherpa/onnx/OfflineSpeechDenoiserGtcrnModelConfig;", "dpdfnet", "Lcom/k2fsa/sherpa/onnx/OfflineSpeechDenoiserDpdfNetModelConfig;", "numThreads", "", "debug", "", "provider", "", "(Lcom/k2fsa/sherpa/onnx/OfflineSpeechDenoiserGtcrnModelConfig;Lcom/k2fsa/sherpa/onnx/OfflineSpeechDenoiserDpdfNetModelConfig;IZLjava/lang/String;)V", "getDebug", "()Z", "setDebug", "(Z)V", "getDpdfnet", "()Lcom/k2fsa/sherpa/onnx/OfflineSpeechDenoiserDpdfNetModelConfig;", "setDpdfnet", "(Lcom/k2fsa/sherpa/onnx/OfflineSpeechDenoiserDpdfNetModelConfig;)V", "getGtcrn", "()Lcom/k2fsa/sherpa/onnx/OfflineSpeechDenoiserGtcrnModelConfig;", "setGtcrn", "(Lcom/k2fsa/sherpa/onnx/OfflineSpeechDenoiserGtcrnModelConfig;)V", "getNumThreads", "()I", "setNumThreads", "(I)V", "getProvider", "()Ljava/lang/String;", "setProvider", "(Ljava/lang/String;)V", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class OfflineSpeechDenoiserModelConfig {
    private boolean debug;
    private OfflineSpeechDenoiserDpdfNetModelConfig dpdfnet;
    private OfflineSpeechDenoiserGtcrnModelConfig gtcrn;
    private int numThreads;
    private String provider;

    public OfflineSpeechDenoiserModelConfig() {
        this(null, null, 0, false, null, 31, null);
    }

    public static /* synthetic */ OfflineSpeechDenoiserModelConfig copy$default(OfflineSpeechDenoiserModelConfig offlineSpeechDenoiserModelConfig, OfflineSpeechDenoiserGtcrnModelConfig offlineSpeechDenoiserGtcrnModelConfig, OfflineSpeechDenoiserDpdfNetModelConfig offlineSpeechDenoiserDpdfNetModelConfig, int i, boolean z, String str, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            offlineSpeechDenoiserGtcrnModelConfig = offlineSpeechDenoiserModelConfig.gtcrn;
        }
        if ((i2 & 2) != 0) {
            offlineSpeechDenoiserDpdfNetModelConfig = offlineSpeechDenoiserModelConfig.dpdfnet;
        }
        if ((i2 & 4) != 0) {
            i = offlineSpeechDenoiserModelConfig.numThreads;
        }
        if ((i2 & 8) != 0) {
            z = offlineSpeechDenoiserModelConfig.debug;
        }
        if ((i2 & 16) != 0) {
            str = offlineSpeechDenoiserModelConfig.provider;
        }
        String str2 = str;
        int i3 = i;
        return offlineSpeechDenoiserModelConfig.copy(offlineSpeechDenoiserGtcrnModelConfig, offlineSpeechDenoiserDpdfNetModelConfig, i3, z, str2);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final OfflineSpeechDenoiserGtcrnModelConfig component1() {
        return this.gtcrn;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final OfflineSpeechDenoiserDpdfNetModelConfig component2() {
        return this.dpdfnet;
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

    public final OfflineSpeechDenoiserModelConfig copy(OfflineSpeechDenoiserGtcrnModelConfig gtcrn, OfflineSpeechDenoiserDpdfNetModelConfig dpdfnet, int numThreads, boolean debug, String provider) {
        Intrinsics.checkNotNullParameter(gtcrn, "gtcrn");
        Intrinsics.checkNotNullParameter(dpdfnet, "dpdfnet");
        Intrinsics.checkNotNullParameter(provider, "provider");
        return new OfflineSpeechDenoiserModelConfig(gtcrn, dpdfnet, numThreads, debug, provider);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineSpeechDenoiserModelConfig)) {
            return false;
        }
        OfflineSpeechDenoiserModelConfig offlineSpeechDenoiserModelConfig = (OfflineSpeechDenoiserModelConfig) other;
        return Intrinsics.areEqual(this.gtcrn, offlineSpeechDenoiserModelConfig.gtcrn) && Intrinsics.areEqual(this.dpdfnet, offlineSpeechDenoiserModelConfig.dpdfnet) && this.numThreads == offlineSpeechDenoiserModelConfig.numThreads && this.debug == offlineSpeechDenoiserModelConfig.debug && Intrinsics.areEqual(this.provider, offlineSpeechDenoiserModelConfig.provider);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v7, types: [int] */
    /* JADX WARN: Type inference failed for: r1v5, types: [int] */
    /* JADX WARN: Type inference failed for: r1v6 */
    /* JADX WARN: Type inference failed for: r1v7 */
    public int hashCode() {
        int iHashCode = ((((this.gtcrn.hashCode() * 31) + this.dpdfnet.hashCode()) * 31) + Integer.hashCode(this.numThreads)) * 31;
        boolean z = this.debug;
        int r1 = z ? 1 : 0;
        if (z) {
            r1 = 1;
        }
        return ((iHashCode + r1) * 31) + this.provider.hashCode();
    }

    public String toString() {
        return "OfflineSpeechDenoiserModelConfig(gtcrn=" + this.gtcrn + ", dpdfnet=" + this.dpdfnet + ", numThreads=" + this.numThreads + ", debug=" + this.debug + ", provider=" + this.provider + ')';
    }

    public OfflineSpeechDenoiserModelConfig(OfflineSpeechDenoiserGtcrnModelConfig gtcrn, OfflineSpeechDenoiserDpdfNetModelConfig dpdfnet, int i, boolean z, String provider) {
        Intrinsics.checkNotNullParameter(gtcrn, "gtcrn");
        Intrinsics.checkNotNullParameter(dpdfnet, "dpdfnet");
        Intrinsics.checkNotNullParameter(provider, "provider");
        this.gtcrn = gtcrn;
        this.dpdfnet = dpdfnet;
        this.numThreads = i;
        this.debug = z;
        this.provider = provider;
    }

    public /* synthetic */ OfflineSpeechDenoiserModelConfig(OfflineSpeechDenoiserGtcrnModelConfig offlineSpeechDenoiserGtcrnModelConfig, OfflineSpeechDenoiserDpdfNetModelConfig offlineSpeechDenoiserDpdfNetModelConfig, int i, boolean z, String str, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? new OfflineSpeechDenoiserGtcrnModelConfig(null, 1, null) : offlineSpeechDenoiserGtcrnModelConfig, (i2 & 2) != 0 ? new OfflineSpeechDenoiserDpdfNetModelConfig(null, 0.0f, 3, null) : offlineSpeechDenoiserDpdfNetModelConfig, (i2 & 4) != 0 ? 1 : i, (i2 & 8) != 0 ? false : z, (i2 & 16) != 0 ? "cpu" : str);
    }

    public final OfflineSpeechDenoiserGtcrnModelConfig getGtcrn() {
        return this.gtcrn;
    }

    public final void setGtcrn(OfflineSpeechDenoiserGtcrnModelConfig offlineSpeechDenoiserGtcrnModelConfig) {
        Intrinsics.checkNotNullParameter(offlineSpeechDenoiserGtcrnModelConfig, "<set-?>");
        this.gtcrn = offlineSpeechDenoiserGtcrnModelConfig;
    }

    public final OfflineSpeechDenoiserDpdfNetModelConfig getDpdfnet() {
        return this.dpdfnet;
    }

    public final void setDpdfnet(OfflineSpeechDenoiserDpdfNetModelConfig offlineSpeechDenoiserDpdfNetModelConfig) {
        Intrinsics.checkNotNullParameter(offlineSpeechDenoiserDpdfNetModelConfig, "<set-?>");
        this.dpdfnet = offlineSpeechDenoiserDpdfNetModelConfig;
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
