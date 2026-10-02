package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OfflineRecognizer.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0007HÆ\u0003J1\u0010\u0019\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÆ\u0001J\u0013\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001d\u001a\u00020\u001eHÖ\u0001J\t\u0010\u001f\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\n\"\u0004\b\u000e\u0010\fR\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\n\"\u0004\b\u0010\u0010\fR\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014¨\u0006 "}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineTransducerModelConfig;", "", "encoder", "", "decoder", "joiner", "qnnConfig", "Lcom/k2fsa/sherpa/onnx/QnnConfig;", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/k2fsa/sherpa/onnx/QnnConfig;)V", "getDecoder", "()Ljava/lang/String;", "setDecoder", "(Ljava/lang/String;)V", "getEncoder", "setEncoder", "getJoiner", "setJoiner", "getQnnConfig", "()Lcom/k2fsa/sherpa/onnx/QnnConfig;", "setQnnConfig", "(Lcom/k2fsa/sherpa/onnx/QnnConfig;)V", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class OfflineTransducerModelConfig {
    private String decoder;
    private String encoder;
    private String joiner;
    private QnnConfig qnnConfig;

    public OfflineTransducerModelConfig() {
        this(null, null, null, null, 15, null);
    }

    public static /* synthetic */ OfflineTransducerModelConfig copy$default(OfflineTransducerModelConfig offlineTransducerModelConfig, String str, String str2, String str3, QnnConfig qnnConfig, int i, Object obj) {
        if ((i & 1) != 0) {
            str = offlineTransducerModelConfig.encoder;
        }
        if ((i & 2) != 0) {
            str2 = offlineTransducerModelConfig.decoder;
        }
        if ((i & 4) != 0) {
            str3 = offlineTransducerModelConfig.joiner;
        }
        if ((i & 8) != 0) {
            qnnConfig = offlineTransducerModelConfig.qnnConfig;
        }
        return offlineTransducerModelConfig.copy(str, str2, str3, qnnConfig);
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
    public final String getJoiner() {
        return this.joiner;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final QnnConfig getQnnConfig() {
        return this.qnnConfig;
    }

    public final OfflineTransducerModelConfig copy(String encoder, String decoder, String joiner, QnnConfig qnnConfig) {
        Intrinsics.checkNotNullParameter(encoder, "encoder");
        Intrinsics.checkNotNullParameter(decoder, "decoder");
        Intrinsics.checkNotNullParameter(joiner, "joiner");
        Intrinsics.checkNotNullParameter(qnnConfig, "qnnConfig");
        return new OfflineTransducerModelConfig(encoder, decoder, joiner, qnnConfig);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineTransducerModelConfig)) {
            return false;
        }
        OfflineTransducerModelConfig offlineTransducerModelConfig = (OfflineTransducerModelConfig) other;
        return Intrinsics.areEqual(this.encoder, offlineTransducerModelConfig.encoder) && Intrinsics.areEqual(this.decoder, offlineTransducerModelConfig.decoder) && Intrinsics.areEqual(this.joiner, offlineTransducerModelConfig.joiner) && Intrinsics.areEqual(this.qnnConfig, offlineTransducerModelConfig.qnnConfig);
    }

    public int hashCode() {
        return (((((this.encoder.hashCode() * 31) + this.decoder.hashCode()) * 31) + this.joiner.hashCode()) * 31) + this.qnnConfig.hashCode();
    }

    public String toString() {
        return "OfflineTransducerModelConfig(encoder=" + this.encoder + ", decoder=" + this.decoder + ", joiner=" + this.joiner + ", qnnConfig=" + this.qnnConfig + ')';
    }

    public OfflineTransducerModelConfig(String encoder, String decoder, String joiner, QnnConfig qnnConfig) {
        Intrinsics.checkNotNullParameter(encoder, "encoder");
        Intrinsics.checkNotNullParameter(decoder, "decoder");
        Intrinsics.checkNotNullParameter(joiner, "joiner");
        Intrinsics.checkNotNullParameter(qnnConfig, "qnnConfig");
        this.encoder = encoder;
        this.decoder = decoder;
        this.joiner = joiner;
        this.qnnConfig = qnnConfig;
    }

    public /* synthetic */ OfflineTransducerModelConfig(String str, String str2, String str3, QnnConfig qnnConfig, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? "" : str, (i & 2) != 0 ? "" : str2, (i & 4) != 0 ? "" : str3, (i & 8) != 0 ? new QnnConfig(null, null, null, 7, null) : qnnConfig);
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

    public final String getJoiner() {
        return this.joiner;
    }

    public final void setJoiner(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.joiner = str;
    }

    public final QnnConfig getQnnConfig() {
        return this.qnnConfig;
    }

    public final void setQnnConfig(QnnConfig qnnConfig) {
        Intrinsics.checkNotNullParameter(qnnConfig, "<set-?>");
        this.qnnConfig = qnnConfig;
    }
}
