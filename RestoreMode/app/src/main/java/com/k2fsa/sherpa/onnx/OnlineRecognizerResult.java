package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Arrays;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OnlineRecognizer.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\u0014\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007¢\u0006\u0002\u0010\tJ\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005HÆ\u0003¢\u0006\u0002\u0010\u000fJ\t\u0010\u0014\u001a\u00020\u0007HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0007HÆ\u0003J<\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0007HÆ\u0001¢\u0006\u0002\u0010\u0017J\u0013\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001b\u001a\u00020\u001cHÖ\u0001J\t\u0010\u001d\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0019\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005¢\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\b\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\r¨\u0006\u001e"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OnlineRecognizerResult;", "", "text", "", "tokens", "", "timestamps", "", "ysProbs", "(Ljava/lang/String;[Ljava/lang/String;[F[F)V", "getText", "()Ljava/lang/String;", "getTimestamps", "()[F", "getTokens", "()[Ljava/lang/String;", "[Ljava/lang/String;", "getYsProbs", "component1", "component2", "component3", "component4", "copy", "(Ljava/lang/String;[Ljava/lang/String;[F[F)Lcom/k2fsa/sherpa/onnx/OnlineRecognizerResult;", "equals", "", "other", "hashCode", "", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class OnlineRecognizerResult {
    private final String text;
    private final float[] timestamps;
    private final String[] tokens;
    private final float[] ysProbs;

    public static /* synthetic */ OnlineRecognizerResult copy$default(OnlineRecognizerResult onlineRecognizerResult, String str, String[] strArr, float[] fArr, float[] fArr2, int i, Object obj) {
        if ((i & 1) != 0) {
            str = onlineRecognizerResult.text;
        }
        if ((i & 2) != 0) {
            strArr = onlineRecognizerResult.tokens;
        }
        if ((i & 4) != 0) {
            fArr = onlineRecognizerResult.timestamps;
        }
        if ((i & 8) != 0) {
            fArr2 = onlineRecognizerResult.ysProbs;
        }
        return onlineRecognizerResult.copy(str, strArr, fArr, fArr2);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String component1() {
        return this.text;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String[] component2() {
        return this.tokens;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final float[] component3() {
        return this.timestamps;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final float[] component4() {
        return this.ysProbs;
    }

    public final OnlineRecognizerResult copy(String text, String[] tokens, float[] timestamps, float[] ysProbs) {
        Intrinsics.checkNotNullParameter(text, "text");
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        Intrinsics.checkNotNullParameter(timestamps, "timestamps");
        Intrinsics.checkNotNullParameter(ysProbs, "ysProbs");
        return new OnlineRecognizerResult(text, tokens, timestamps, ysProbs);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OnlineRecognizerResult)) {
            return false;
        }
        OnlineRecognizerResult onlineRecognizerResult = (OnlineRecognizerResult) other;
        return Intrinsics.areEqual(this.text, onlineRecognizerResult.text) && Intrinsics.areEqual(this.tokens, onlineRecognizerResult.tokens) && Intrinsics.areEqual(this.timestamps, onlineRecognizerResult.timestamps) && Intrinsics.areEqual(this.ysProbs, onlineRecognizerResult.ysProbs);
    }

    public int hashCode() {
        return (((((this.text.hashCode() * 31) + Arrays.hashCode(this.tokens)) * 31) + Arrays.hashCode(this.timestamps)) * 31) + Arrays.hashCode(this.ysProbs);
    }

    public String toString() {
        return "OnlineRecognizerResult(text=" + this.text + ", tokens=" + Arrays.toString(this.tokens) + ", timestamps=" + Arrays.toString(this.timestamps) + ", ysProbs=" + Arrays.toString(this.ysProbs) + ')';
    }

    public OnlineRecognizerResult(String text, String[] tokens, float[] timestamps, float[] ysProbs) {
        Intrinsics.checkNotNullParameter(text, "text");
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        Intrinsics.checkNotNullParameter(timestamps, "timestamps");
        Intrinsics.checkNotNullParameter(ysProbs, "ysProbs");
        this.text = text;
        this.tokens = tokens;
        this.timestamps = timestamps;
        this.ysProbs = ysProbs;
    }

    public final String getText() {
        return this.text;
    }

    public final String[] getTokens() {
        return this.tokens;
    }

    public final float[] getTimestamps() {
        return this.timestamps;
    }

    public final float[] getYsProbs() {
        return this.ysProbs;
    }
}
