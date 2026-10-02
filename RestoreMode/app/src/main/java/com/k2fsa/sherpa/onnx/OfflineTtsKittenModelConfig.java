package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Tts.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u0016\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B7\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b¢\u0006\u0002\u0010\tJ\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001b\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001c\u001a\u00020\bHÆ\u0003J;\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\bHÆ\u0001J\u0013\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010!\u001a\u00020\"HÖ\u0001J\t\u0010#\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0006\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u001a\u0010\u0007\u001a\u00020\bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u000b\"\u0004\b\u0013\u0010\rR\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u000b\"\u0004\b\u0015\u0010\rR\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u000b\"\u0004\b\u0017\u0010\r¨\u0006$"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineTtsKittenModelConfig;", "", "model", "", "voices", "tokens", "dataDir", "lengthScale", "", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;F)V", "getDataDir", "()Ljava/lang/String;", "setDataDir", "(Ljava/lang/String;)V", "getLengthScale", "()F", "setLengthScale", "(F)V", "getModel", "setModel", "getTokens", "setTokens", "getVoices", "setVoices", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class OfflineTtsKittenModelConfig {
    private String dataDir;
    private float lengthScale;
    private String model;
    private String tokens;
    private String voices;

    public OfflineTtsKittenModelConfig() {
        this(null, null, null, null, 0.0f, 31, null);
    }

    public static /* synthetic */ OfflineTtsKittenModelConfig copy$default(OfflineTtsKittenModelConfig offlineTtsKittenModelConfig, String str, String str2, String str3, String str4, float f, int i, Object obj) {
        if ((i & 1) != 0) {
            str = offlineTtsKittenModelConfig.model;
        }
        if ((i & 2) != 0) {
            str2 = offlineTtsKittenModelConfig.voices;
        }
        if ((i & 4) != 0) {
            str3 = offlineTtsKittenModelConfig.tokens;
        }
        if ((i & 8) != 0) {
            str4 = offlineTtsKittenModelConfig.dataDir;
        }
        if ((i & 16) != 0) {
            f = offlineTtsKittenModelConfig.lengthScale;
        }
        float f2 = f;
        String str5 = str3;
        return offlineTtsKittenModelConfig.copy(str, str2, str5, str4, f2);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String component1() {
        return this.model;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String component2() {
        return this.voices;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final String component3() {
        return this.tokens;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final String component4() {
        return this.dataDir;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final float component5() {
        return this.lengthScale;
    }

    public final OfflineTtsKittenModelConfig copy(String model, String voices, String tokens, String dataDir, float lengthScale) {
        Intrinsics.checkNotNullParameter(model, "model");
        Intrinsics.checkNotNullParameter(voices, "voices");
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        Intrinsics.checkNotNullParameter(dataDir, "dataDir");
        return new OfflineTtsKittenModelConfig(model, voices, tokens, dataDir, lengthScale);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineTtsKittenModelConfig)) {
            return false;
        }
        OfflineTtsKittenModelConfig offlineTtsKittenModelConfig = (OfflineTtsKittenModelConfig) other;
        return Intrinsics.areEqual(this.model, offlineTtsKittenModelConfig.model) && Intrinsics.areEqual(this.voices, offlineTtsKittenModelConfig.voices) && Intrinsics.areEqual(this.tokens, offlineTtsKittenModelConfig.tokens) && Intrinsics.areEqual(this.dataDir, offlineTtsKittenModelConfig.dataDir) && Float.compare(this.lengthScale, offlineTtsKittenModelConfig.lengthScale) == 0;
    }

    public int hashCode() {
        return (((((((this.model.hashCode() * 31) + this.voices.hashCode()) * 31) + this.tokens.hashCode()) * 31) + this.dataDir.hashCode()) * 31) + Float.hashCode(this.lengthScale);
    }

    public String toString() {
        return "OfflineTtsKittenModelConfig(model=" + this.model + ", voices=" + this.voices + ", tokens=" + this.tokens + ", dataDir=" + this.dataDir + ", lengthScale=" + this.lengthScale + ')';
    }

    public OfflineTtsKittenModelConfig(String model, String voices, String tokens, String dataDir, float f) {
        Intrinsics.checkNotNullParameter(model, "model");
        Intrinsics.checkNotNullParameter(voices, "voices");
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        Intrinsics.checkNotNullParameter(dataDir, "dataDir");
        this.model = model;
        this.voices = voices;
        this.tokens = tokens;
        this.dataDir = dataDir;
        this.lengthScale = f;
    }

    public /* synthetic */ OfflineTtsKittenModelConfig(String str, String str2, String str3, String str4, float f, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? "" : str, (i & 2) != 0 ? "" : str2, (i & 4) != 0 ? "" : str3, (i & 8) != 0 ? "" : str4, (i & 16) != 0 ? 1.0f : f);
    }

    public final String getModel() {
        return this.model;
    }

    public final void setModel(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.model = str;
    }

    public final String getVoices() {
        return this.voices;
    }

    public final void setVoices(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.voices = str;
    }

    public final String getTokens() {
        return this.tokens;
    }

    public final void setTokens(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.tokens = str;
    }

    public final String getDataDir() {
        return this.dataDir;
    }

    public final void setDataDir(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.dataDir = str;
    }

    public final float getLengthScale() {
        return this.lengthScale;
    }

    public final void setLengthScale(float f) {
        this.lengthScale = f;
    }
}
