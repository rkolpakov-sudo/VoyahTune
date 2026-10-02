package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.scheduling.WorkQueueKt;

/* JADX INFO: compiled from: OfflineRecognizer.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b#\b\u0086\b\u0018\u00002\u00020\u0001BK\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\n¢\u0006\u0002\u0010\fJ\t\u0010!\u001a\u00020\u0003HÆ\u0003J\t\u0010\"\u001a\u00020\u0003HÆ\u0003J\t\u0010#\u001a\u00020\u0003HÆ\u0003J\t\u0010$\u001a\u00020\u0003HÆ\u0003J\t\u0010%\u001a\u00020\bHÆ\u0003J\t\u0010&\u001a\u00020\nHÆ\u0003J\t\u0010'\u001a\u00020\nHÆ\u0003JO\u0010(\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\nHÆ\u0001J\u0013\u0010)\u001a\u00020\n2\b\u0010*\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010+\u001a\u00020\bHÖ\u0001J\t\u0010,\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001a\u0010\u000b\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u001a\u0010\t\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0012\"\u0004\b\u0016\u0010\u0014R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u000e\"\u0004\b\u0018\u0010\u0010R\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u000e\"\u0004\b\u001a\u0010\u0010R\u001a\u0010\u0007\u001a\u00020\bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u001a\u0010\u0006\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010\u000e\"\u0004\b \u0010\u0010¨\u0006-"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineWhisperModelConfig;", "", "encoder", "", "decoder", "language", "task", "tailPaddings", "", "enableTokenTimestamps", "", "enableSegmentTimestamps", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IZZ)V", "getDecoder", "()Ljava/lang/String;", "setDecoder", "(Ljava/lang/String;)V", "getEnableSegmentTimestamps", "()Z", "setEnableSegmentTimestamps", "(Z)V", "getEnableTokenTimestamps", "setEnableTokenTimestamps", "getEncoder", "setEncoder", "getLanguage", "setLanguage", "getTailPaddings", "()I", "setTailPaddings", "(I)V", "getTask", "setTask", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class OfflineWhisperModelConfig {
    private String decoder;
    private boolean enableSegmentTimestamps;
    private boolean enableTokenTimestamps;
    private String encoder;
    private String language;
    private int tailPaddings;
    private String task;

    public OfflineWhisperModelConfig() {
        this(null, null, null, null, 0, false, false, WorkQueueKt.MASK, null);
    }

    public static /* synthetic */ OfflineWhisperModelConfig copy$default(OfflineWhisperModelConfig offlineWhisperModelConfig, String str, String str2, String str3, String str4, int i, boolean z, boolean z2, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            str = offlineWhisperModelConfig.encoder;
        }
        if ((i2 & 2) != 0) {
            str2 = offlineWhisperModelConfig.decoder;
        }
        if ((i2 & 4) != 0) {
            str3 = offlineWhisperModelConfig.language;
        }
        if ((i2 & 8) != 0) {
            str4 = offlineWhisperModelConfig.task;
        }
        if ((i2 & 16) != 0) {
            i = offlineWhisperModelConfig.tailPaddings;
        }
        if ((i2 & 32) != 0) {
            z = offlineWhisperModelConfig.enableTokenTimestamps;
        }
        if ((i2 & 64) != 0) {
            z2 = offlineWhisperModelConfig.enableSegmentTimestamps;
        }
        boolean z3 = z;
        boolean z4 = z2;
        int i3 = i;
        String str5 = str3;
        return offlineWhisperModelConfig.copy(str, str2, str5, str4, i3, z3, z4);
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
    public final String component3() {
        return this.language;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final String component4() {
        return this.task;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final int component5() {
        return this.tailPaddings;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final boolean component6() {
        return this.enableTokenTimestamps;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final boolean component7() {
        return this.enableSegmentTimestamps;
    }

    public final OfflineWhisperModelConfig copy(String encoder, String decoder, String language, String task, int tailPaddings, boolean enableTokenTimestamps, boolean enableSegmentTimestamps) {
        Intrinsics.checkNotNullParameter(encoder, "encoder");
        Intrinsics.checkNotNullParameter(decoder, "decoder");
        Intrinsics.checkNotNullParameter(language, "language");
        Intrinsics.checkNotNullParameter(task, "task");
        return new OfflineWhisperModelConfig(encoder, decoder, language, task, tailPaddings, enableTokenTimestamps, enableSegmentTimestamps);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineWhisperModelConfig)) {
            return false;
        }
        OfflineWhisperModelConfig offlineWhisperModelConfig = (OfflineWhisperModelConfig) other;
        return Intrinsics.areEqual(this.encoder, offlineWhisperModelConfig.encoder) && Intrinsics.areEqual(this.decoder, offlineWhisperModelConfig.decoder) && Intrinsics.areEqual(this.language, offlineWhisperModelConfig.language) && Intrinsics.areEqual(this.task, offlineWhisperModelConfig.task) && this.tailPaddings == offlineWhisperModelConfig.tailPaddings && this.enableTokenTimestamps == offlineWhisperModelConfig.enableTokenTimestamps && this.enableSegmentTimestamps == offlineWhisperModelConfig.enableSegmentTimestamps;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v11, types: [int] */
    /* JADX WARN: Type inference failed for: r0v13, types: [int] */
    /* JADX WARN: Type inference failed for: r1v10 */
    /* JADX WARN: Type inference failed for: r1v11 */
    /* JADX WARN: Type inference failed for: r1v9, types: [int] */
    /* JADX WARN: Type inference failed for: r2v0 */
    /* JADX WARN: Type inference failed for: r2v1, types: [int] */
    /* JADX WARN: Type inference failed for: r2v2 */
    public int hashCode() {
        int iHashCode = ((((((((this.encoder.hashCode() * 31) + this.decoder.hashCode()) * 31) + this.language.hashCode()) * 31) + this.task.hashCode()) * 31) + Integer.hashCode(this.tailPaddings)) * 31;
        boolean z = this.enableTokenTimestamps;
        int r1 = z ? 1 : 0;
        if (z) {
            r1 = 1;
        }
        int i = (iHashCode + r1) * 31;
        boolean z2 = this.enableSegmentTimestamps;
        return i + (z2 ? 1 : 0);
    }

    public String toString() {
        return "OfflineWhisperModelConfig(encoder=" + this.encoder + ", decoder=" + this.decoder + ", language=" + this.language + ", task=" + this.task + ", tailPaddings=" + this.tailPaddings + ", enableTokenTimestamps=" + this.enableTokenTimestamps + ", enableSegmentTimestamps=" + this.enableSegmentTimestamps + ')';
    }

    public OfflineWhisperModelConfig(String encoder, String decoder, String language, String task, int i, boolean z, boolean z2) {
        Intrinsics.checkNotNullParameter(encoder, "encoder");
        Intrinsics.checkNotNullParameter(decoder, "decoder");
        Intrinsics.checkNotNullParameter(language, "language");
        Intrinsics.checkNotNullParameter(task, "task");
        this.encoder = encoder;
        this.decoder = decoder;
        this.language = language;
        this.task = task;
        this.tailPaddings = i;
        this.enableTokenTimestamps = z;
        this.enableSegmentTimestamps = z2;
    }

    public /* synthetic */ OfflineWhisperModelConfig(String str, String str2, String str3, String str4, int i, boolean z, boolean z2, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? "" : str, (i2 & 2) != 0 ? "" : str2, (i2 & 4) != 0 ? "en" : str3, (i2 & 8) != 0 ? "transcribe" : str4, (i2 & 16) != 0 ? 1000 : i, (i2 & 32) != 0 ? false : z, (i2 & 64) != 0 ? false : z2);
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

    public final String getLanguage() {
        return this.language;
    }

    public final void setLanguage(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.language = str;
    }

    public final String getTask() {
        return this.task;
    }

    public final void setTask(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.task = str;
    }

    public final int getTailPaddings() {
        return this.tailPaddings;
    }

    public final void setTailPaddings(int i) {
        this.tailPaddings = i;
    }

    public final boolean getEnableTokenTimestamps() {
        return this.enableTokenTimestamps;
    }

    public final void setEnableTokenTimestamps(boolean z) {
        this.enableTokenTimestamps = z;
    }

    public final boolean getEnableSegmentTimestamps() {
        return this.enableSegmentTimestamps;
    }

    public final void setEnableSegmentTimestamps(boolean z) {
        this.enableSegmentTimestamps = z;
    }
}
