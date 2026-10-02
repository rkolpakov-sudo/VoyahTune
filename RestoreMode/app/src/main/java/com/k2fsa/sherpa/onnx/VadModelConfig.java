package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Vad.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b#\b\u0086\b\u0018\u00002\u00020\u0001BA\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f¢\u0006\u0002\u0010\rJ\t\u0010$\u001a\u00020\u0003HÆ\u0003J\t\u0010%\u001a\u00020\u0005HÆ\u0003J\t\u0010&\u001a\u00020\u0007HÆ\u0003J\t\u0010'\u001a\u00020\u0007HÆ\u0003J\t\u0010(\u001a\u00020\nHÆ\u0003J\t\u0010)\u001a\u00020\fHÆ\u0003JE\u0010*\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\fHÆ\u0001J\u0013\u0010+\u001a\u00020\f2\b\u0010,\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010-\u001a\u00020\u0007HÖ\u0001J\t\u0010.\u001a\u00020\nHÖ\u0001R\u001a\u0010\u000b\u001a\u00020\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001a\u0010\b\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u001a\u0010\t\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u0013\"\u0004\b\u001b\u0010\u0015R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001fR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b \u0010!\"\u0004\b\"\u0010#¨\u0006/"}, d2 = {"Lcom/k2fsa/sherpa/onnx/VadModelConfig;", "", "sileroVadModelConfig", "Lcom/k2fsa/sherpa/onnx/SileroVadModelConfig;", "tenVadModelConfig", "Lcom/k2fsa/sherpa/onnx/TenVadModelConfig;", "sampleRate", "", "numThreads", "provider", "", "debug", "", "(Lcom/k2fsa/sherpa/onnx/SileroVadModelConfig;Lcom/k2fsa/sherpa/onnx/TenVadModelConfig;IILjava/lang/String;Z)V", "getDebug", "()Z", "setDebug", "(Z)V", "getNumThreads", "()I", "setNumThreads", "(I)V", "getProvider", "()Ljava/lang/String;", "setProvider", "(Ljava/lang/String;)V", "getSampleRate", "setSampleRate", "getSileroVadModelConfig", "()Lcom/k2fsa/sherpa/onnx/SileroVadModelConfig;", "setSileroVadModelConfig", "(Lcom/k2fsa/sherpa/onnx/SileroVadModelConfig;)V", "getTenVadModelConfig", "()Lcom/k2fsa/sherpa/onnx/TenVadModelConfig;", "setTenVadModelConfig", "(Lcom/k2fsa/sherpa/onnx/TenVadModelConfig;)V", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class VadModelConfig {
    private boolean debug;
    private int numThreads;
    private String provider;
    private int sampleRate;
    private SileroVadModelConfig sileroVadModelConfig;
    private TenVadModelConfig tenVadModelConfig;

    public VadModelConfig() {
        this(null, null, 0, 0, null, false, 63, null);
    }

    public static /* synthetic */ VadModelConfig copy$default(VadModelConfig vadModelConfig, SileroVadModelConfig sileroVadModelConfig, TenVadModelConfig tenVadModelConfig, int i, int i2, String str, boolean z, int i3, Object obj) {
        if ((i3 & 1) != 0) {
            sileroVadModelConfig = vadModelConfig.sileroVadModelConfig;
        }
        if ((i3 & 2) != 0) {
            tenVadModelConfig = vadModelConfig.tenVadModelConfig;
        }
        if ((i3 & 4) != 0) {
            i = vadModelConfig.sampleRate;
        }
        if ((i3 & 8) != 0) {
            i2 = vadModelConfig.numThreads;
        }
        if ((i3 & 16) != 0) {
            str = vadModelConfig.provider;
        }
        if ((i3 & 32) != 0) {
            z = vadModelConfig.debug;
        }
        String str2 = str;
        boolean z2 = z;
        return vadModelConfig.copy(sileroVadModelConfig, tenVadModelConfig, i, i2, str2, z2);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final SileroVadModelConfig component1() {
        return this.sileroVadModelConfig;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final TenVadModelConfig component2() {
        return this.tenVadModelConfig;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final int component3() {
        return this.sampleRate;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final int component4() {
        return this.numThreads;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final String component5() {
        return this.provider;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final boolean component6() {
        return this.debug;
    }

    public final VadModelConfig copy(SileroVadModelConfig sileroVadModelConfig, TenVadModelConfig tenVadModelConfig, int sampleRate, int numThreads, String provider, boolean debug) {
        Intrinsics.checkNotNullParameter(sileroVadModelConfig, "sileroVadModelConfig");
        Intrinsics.checkNotNullParameter(tenVadModelConfig, "tenVadModelConfig");
        Intrinsics.checkNotNullParameter(provider, "provider");
        return new VadModelConfig(sileroVadModelConfig, tenVadModelConfig, sampleRate, numThreads, provider, debug);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof VadModelConfig)) {
            return false;
        }
        VadModelConfig vadModelConfig = (VadModelConfig) other;
        return Intrinsics.areEqual(this.sileroVadModelConfig, vadModelConfig.sileroVadModelConfig) && Intrinsics.areEqual(this.tenVadModelConfig, vadModelConfig.tenVadModelConfig) && this.sampleRate == vadModelConfig.sampleRate && this.numThreads == vadModelConfig.numThreads && Intrinsics.areEqual(this.provider, vadModelConfig.provider) && this.debug == vadModelConfig.debug;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v11, types: [int] */
    /* JADX WARN: Type inference failed for: r2v2, types: [int] */
    /* JADX WARN: Type inference failed for: r2v3 */
    /* JADX WARN: Type inference failed for: r2v4 */
    public int hashCode() {
        int iHashCode = ((((((((this.sileroVadModelConfig.hashCode() * 31) + this.tenVadModelConfig.hashCode()) * 31) + Integer.hashCode(this.sampleRate)) * 31) + Integer.hashCode(this.numThreads)) * 31) + this.provider.hashCode()) * 31;
        boolean z = this.debug;
        int r2 = z ? 1 : 0;
        if (z) {
            r2 = 1;
        }
        return iHashCode + r2;
    }

    public String toString() {
        return "VadModelConfig(sileroVadModelConfig=" + this.sileroVadModelConfig + ", tenVadModelConfig=" + this.tenVadModelConfig + ", sampleRate=" + this.sampleRate + ", numThreads=" + this.numThreads + ", provider=" + this.provider + ", debug=" + this.debug + ')';
    }

    public VadModelConfig(SileroVadModelConfig sileroVadModelConfig, TenVadModelConfig tenVadModelConfig, int i, int i2, String provider, boolean z) {
        Intrinsics.checkNotNullParameter(sileroVadModelConfig, "sileroVadModelConfig");
        Intrinsics.checkNotNullParameter(tenVadModelConfig, "tenVadModelConfig");
        Intrinsics.checkNotNullParameter(provider, "provider");
        this.sileroVadModelConfig = sileroVadModelConfig;
        this.tenVadModelConfig = tenVadModelConfig;
        this.sampleRate = i;
        this.numThreads = i2;
        this.provider = provider;
        this.debug = z;
    }

    public /* synthetic */ VadModelConfig(SileroVadModelConfig sileroVadModelConfig, TenVadModelConfig tenVadModelConfig, int i, int i2, String str, boolean z, int i3, DefaultConstructorMarker defaultConstructorMarker) {
        this((i3 & 1) != 0 ? new SileroVadModelConfig(null, 0.0f, 0.0f, 0.0f, 0, 0.0f, 63, null) : sileroVadModelConfig, (i3 & 2) != 0 ? new TenVadModelConfig(null, 0.0f, 0.0f, 0.0f, 0, 0.0f, 63, null) : tenVadModelConfig, (i3 & 4) != 0 ? 16000 : i, (i3 & 8) != 0 ? 1 : i2, (i3 & 16) != 0 ? "cpu" : str, (i3 & 32) != 0 ? false : z);
    }

    public final SileroVadModelConfig getSileroVadModelConfig() {
        return this.sileroVadModelConfig;
    }

    public final void setSileroVadModelConfig(SileroVadModelConfig sileroVadModelConfig) {
        Intrinsics.checkNotNullParameter(sileroVadModelConfig, "<set-?>");
        this.sileroVadModelConfig = sileroVadModelConfig;
    }

    public final TenVadModelConfig getTenVadModelConfig() {
        return this.tenVadModelConfig;
    }

    public final void setTenVadModelConfig(TenVadModelConfig tenVadModelConfig) {
        Intrinsics.checkNotNullParameter(tenVadModelConfig, "<set-?>");
        this.tenVadModelConfig = tenVadModelConfig;
    }

    public final int getSampleRate() {
        return this.sampleRate;
    }

    public final void setSampleRate(int i) {
        this.sampleRate = i;
    }

    public final int getNumThreads() {
        return this.numThreads;
    }

    public final void setNumThreads(int i) {
        this.numThreads = i;
    }

    public final String getProvider() {
        return this.provider;
    }

    public final void setProvider(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.provider = str;
    }

    public final boolean getDebug() {
        return this.debug;
    }

    public final void setDebug(boolean z) {
        this.debug = z;
    }
}
