package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: SpokenLanguageIdentification.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B#\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0006HÆ\u0003J'\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÆ\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0019\u001a\u00020\u0006HÖ\u0001J\t\u0010\u001a\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\t\"\u0004\b\r\u0010\u000bR\u001a\u0010\u0005\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011¨\u0006\u001b"}, d2 = {"Lcom/k2fsa/sherpa/onnx/SpokenLanguageIdentificationWhisperConfig;", "", "encoder", "", "decoder", "tailPaddings", "", "(Ljava/lang/String;Ljava/lang/String;I)V", "getDecoder", "()Ljava/lang/String;", "setDecoder", "(Ljava/lang/String;)V", "getEncoder", "setEncoder", "getTailPaddings", "()I", "setTailPaddings", "(I)V", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class SpokenLanguageIdentificationWhisperConfig {
    private String decoder;
    private String encoder;
    private int tailPaddings;

    public SpokenLanguageIdentificationWhisperConfig() {
        this(null, null, 0, 7, null);
    }

    public static /* synthetic */ SpokenLanguageIdentificationWhisperConfig copy$default(SpokenLanguageIdentificationWhisperConfig spokenLanguageIdentificationWhisperConfig, String str, String str2, int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            str = spokenLanguageIdentificationWhisperConfig.encoder;
        }
        if ((i2 & 2) != 0) {
            str2 = spokenLanguageIdentificationWhisperConfig.decoder;
        }
        if ((i2 & 4) != 0) {
            i = spokenLanguageIdentificationWhisperConfig.tailPaddings;
        }
        return spokenLanguageIdentificationWhisperConfig.copy(str, str2, i);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String component1() {
        return this.encoder;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String component2() {
        return this.decoder;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final int component3() {
        return this.tailPaddings;
    }

    public final SpokenLanguageIdentificationWhisperConfig copy(String encoder, String decoder, int tailPaddings) {
        Intrinsics.checkNotNullParameter(encoder, "encoder");
        Intrinsics.checkNotNullParameter(decoder, "decoder");
        return new SpokenLanguageIdentificationWhisperConfig(encoder, decoder, tailPaddings);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SpokenLanguageIdentificationWhisperConfig)) {
            return false;
        }
        SpokenLanguageIdentificationWhisperConfig spokenLanguageIdentificationWhisperConfig = (SpokenLanguageIdentificationWhisperConfig) other;
        return Intrinsics.areEqual(this.encoder, spokenLanguageIdentificationWhisperConfig.encoder) && Intrinsics.areEqual(this.decoder, spokenLanguageIdentificationWhisperConfig.decoder) && this.tailPaddings == spokenLanguageIdentificationWhisperConfig.tailPaddings;
    }

    public int hashCode() {
        return (((this.encoder.hashCode() * 31) + this.decoder.hashCode()) * 31) + Integer.hashCode(this.tailPaddings);
    }

    public String toString() {
        return "SpokenLanguageIdentificationWhisperConfig(encoder=" + this.encoder + ", decoder=" + this.decoder + ", tailPaddings=" + this.tailPaddings + ')';
    }

    public SpokenLanguageIdentificationWhisperConfig(String encoder, String decoder, int i) {
        Intrinsics.checkNotNullParameter(encoder, "encoder");
        Intrinsics.checkNotNullParameter(decoder, "decoder");
        this.encoder = encoder;
        this.decoder = decoder;
        this.tailPaddings = i;
    }

    public /* synthetic */ SpokenLanguageIdentificationWhisperConfig(String str, String str2, int i, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? "" : str, (i2 & 2) != 0 ? "" : str2, (i2 & 4) != 0 ? -1 : i);
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

    public final int getTailPaddings() {
        return this.tailPaddings;
    }

    public final void setTailPaddings(int i) {
        this.tailPaddings = i;
    }
}
