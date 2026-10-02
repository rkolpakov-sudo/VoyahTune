package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OnlineRecognizer.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0019\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003¢\u0006\u0002\u0010\u0005J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0012\u001a\u00020\u0013HÖ\u0001J\t\u0010\u0014\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u0007\"\u0004\b\u000b\u0010\t¨\u0006\u0015"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OnlineParaformerModelConfig;", "", "encoder", "", "decoder", "(Ljava/lang/String;Ljava/lang/String;)V", "getDecoder", "()Ljava/lang/String;", "setDecoder", "(Ljava/lang/String;)V", "getEncoder", "setEncoder", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class OnlineParaformerModelConfig {
    private String decoder;
    private String encoder;

    /* JADX WARN: Multi-variable type inference failed */
    public OnlineParaformerModelConfig() {
        this(null, null, 3, null);
    }

    public static /* synthetic */ OnlineParaformerModelConfig copy$default(OnlineParaformerModelConfig onlineParaformerModelConfig, String str, String str2, int i, Object obj) {
        if ((i & 1) != 0) {
            str = onlineParaformerModelConfig.encoder;
        }
        if ((i & 2) != 0) {
            str2 = onlineParaformerModelConfig.decoder;
        }
        return onlineParaformerModelConfig.copy(str, str2);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String component1() {
        return this.encoder;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String component2() {
        return this.decoder;
    }

    public final OnlineParaformerModelConfig copy(String encoder, String decoder) {
        Intrinsics.checkNotNullParameter(encoder, "encoder");
        Intrinsics.checkNotNullParameter(decoder, "decoder");
        return new OnlineParaformerModelConfig(encoder, decoder);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OnlineParaformerModelConfig)) {
            return false;
        }
        OnlineParaformerModelConfig onlineParaformerModelConfig = (OnlineParaformerModelConfig) other;
        return Intrinsics.areEqual(this.encoder, onlineParaformerModelConfig.encoder) && Intrinsics.areEqual(this.decoder, onlineParaformerModelConfig.decoder);
    }

    public int hashCode() {
        return (this.encoder.hashCode() * 31) + this.decoder.hashCode();
    }

    public String toString() {
        return "OnlineParaformerModelConfig(encoder=" + this.encoder + ", decoder=" + this.decoder + ')';
    }

    public OnlineParaformerModelConfig(String encoder, String decoder) {
        Intrinsics.checkNotNullParameter(encoder, "encoder");
        Intrinsics.checkNotNullParameter(decoder, "decoder");
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public /* synthetic */ OnlineParaformerModelConfig(String str, String str2, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? "" : str, (i & 2) != 0 ? "" : str2);
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
}
