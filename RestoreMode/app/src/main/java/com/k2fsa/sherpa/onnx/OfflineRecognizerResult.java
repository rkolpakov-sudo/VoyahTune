package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import java.util.Arrays;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OfflineRecognizer.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\u0014\n\u0002\b\u001a\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BC\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u0003\u0012\u0006\u0010\u000b\u001a\u00020\u0007¢\u0006\u0002\u0010\fJ\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003J\u0014\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005HÆ\u0003¢\u0006\u0002\u0010\u0016J\t\u0010\u001a\u001a\u00020\u0007HÆ\u0003J\t\u0010\u001b\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001c\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001d\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001e\u001a\u00020\u0007HÆ\u0003JZ\u0010\u001f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\u0007HÆ\u0001¢\u0006\u0002\u0010 J\u0013\u0010!\u001a\u00020\"2\b\u0010#\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010$\u001a\u00020%HÖ\u0001J\t\u0010&\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u000b\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\t\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\n\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0011\u0010\b\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u000eR\u0019\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005¢\u0006\n\n\u0002\u0010\u0017\u001a\u0004\b\u0015\u0010\u0016¨\u0006'"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineRecognizerResult;", "", "text", "", "tokens", "", "timestamps", "", "lang", "emotion", NotificationCompat.CATEGORY_EVENT, "durations", "(Ljava/lang/String;[Ljava/lang/String;[FLjava/lang/String;Ljava/lang/String;Ljava/lang/String;[F)V", "getDurations", "()[F", "getEmotion", "()Ljava/lang/String;", "getEvent", "getLang", "getText", "getTimestamps", "getTokens", "()[Ljava/lang/String;", "[Ljava/lang/String;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "(Ljava/lang/String;[Ljava/lang/String;[FLjava/lang/String;Ljava/lang/String;Ljava/lang/String;[F)Lcom/k2fsa/sherpa/onnx/OfflineRecognizerResult;", "equals", "", "other", "hashCode", "", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class OfflineRecognizerResult {
    private final float[] durations;
    private final String emotion;
    private final String event;
    private final String lang;
    private final String text;
    private final float[] timestamps;
    private final String[] tokens;

    public static /* synthetic */ OfflineRecognizerResult copy$default(OfflineRecognizerResult offlineRecognizerResult, String str, String[] strArr, float[] fArr, String str2, String str3, String str4, float[] fArr2, int i, Object obj) {
        if ((i & 1) != 0) {
            str = offlineRecognizerResult.text;
        }
        if ((i & 2) != 0) {
            strArr = offlineRecognizerResult.tokens;
        }
        if ((i & 4) != 0) {
            fArr = offlineRecognizerResult.timestamps;
        }
        if ((i & 8) != 0) {
            str2 = offlineRecognizerResult.lang;
        }
        if ((i & 16) != 0) {
            str3 = offlineRecognizerResult.emotion;
        }
        if ((i & 32) != 0) {
            str4 = offlineRecognizerResult.event;
        }
        if ((i & 64) != 0) {
            fArr2 = offlineRecognizerResult.durations;
        }
        String str5 = str4;
        float[] fArr3 = fArr2;
        String str6 = str3;
        float[] fArr4 = fArr;
        return offlineRecognizerResult.copy(str, strArr, fArr4, str2, str6, str5, fArr3);
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
    public final String component4() {
        return this.lang;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final String component5() {
        return this.emotion;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final String component6() {
        return this.event;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final float[] component7() {
        return this.durations;
    }

    public final OfflineRecognizerResult copy(String text, String[] tokens, float[] timestamps, String lang, String emotion, String event, float[] durations) {
        Intrinsics.checkNotNullParameter(text, "text");
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        Intrinsics.checkNotNullParameter(timestamps, "timestamps");
        Intrinsics.checkNotNullParameter(lang, "lang");
        Intrinsics.checkNotNullParameter(emotion, "emotion");
        Intrinsics.checkNotNullParameter(event, "event");
        Intrinsics.checkNotNullParameter(durations, "durations");
        return new OfflineRecognizerResult(text, tokens, timestamps, lang, emotion, event, durations);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineRecognizerResult)) {
            return false;
        }
        OfflineRecognizerResult offlineRecognizerResult = (OfflineRecognizerResult) other;
        return Intrinsics.areEqual(this.text, offlineRecognizerResult.text) && Intrinsics.areEqual(this.tokens, offlineRecognizerResult.tokens) && Intrinsics.areEqual(this.timestamps, offlineRecognizerResult.timestamps) && Intrinsics.areEqual(this.lang, offlineRecognizerResult.lang) && Intrinsics.areEqual(this.emotion, offlineRecognizerResult.emotion) && Intrinsics.areEqual(this.event, offlineRecognizerResult.event) && Intrinsics.areEqual(this.durations, offlineRecognizerResult.durations);
    }

    public int hashCode() {
        return (((((((((((this.text.hashCode() * 31) + Arrays.hashCode(this.tokens)) * 31) + Arrays.hashCode(this.timestamps)) * 31) + this.lang.hashCode()) * 31) + this.emotion.hashCode()) * 31) + this.event.hashCode()) * 31) + Arrays.hashCode(this.durations);
    }

    public String toString() {
        return "OfflineRecognizerResult(text=" + this.text + ", tokens=" + Arrays.toString(this.tokens) + ", timestamps=" + Arrays.toString(this.timestamps) + ", lang=" + this.lang + ", emotion=" + this.emotion + ", event=" + this.event + ", durations=" + Arrays.toString(this.durations) + ')';
    }

    public OfflineRecognizerResult(String text, String[] tokens, float[] timestamps, String lang, String emotion, String event, float[] durations) {
        Intrinsics.checkNotNullParameter(text, "text");
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        Intrinsics.checkNotNullParameter(timestamps, "timestamps");
        Intrinsics.checkNotNullParameter(lang, "lang");
        Intrinsics.checkNotNullParameter(emotion, "emotion");
        Intrinsics.checkNotNullParameter(event, "event");
        Intrinsics.checkNotNullParameter(durations, "durations");
        this.text = text;
        this.tokens = tokens;
        this.timestamps = timestamps;
        this.lang = lang;
        this.emotion = emotion;
        this.event = event;
        this.durations = durations;
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

    public final String getLang() {
        return this.lang;
    }

    public final String getEmotion() {
        return this.emotion;
    }

    public final String getEvent() {
        return this.event;
    }

    public final float[] getDurations() {
        return this.durations;
    }
}
