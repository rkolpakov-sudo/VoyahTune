package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Arrays;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Tts.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0014\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b%\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001Bg\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\b\b\u0002\u0010\t\u001a\u00020\u0006\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\u0006\u0012\u0016\b\u0002\u0010\r\u001a\u0010\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u000b\u0018\u00010\u000e¢\u0006\u0002\u0010\u000fJ\t\u0010*\u001a\u00020\u0003HÆ\u0003J\t\u0010+\u001a\u00020\u0003HÆ\u0003J\t\u0010,\u001a\u00020\u0006HÆ\u0003J\u000b\u0010-\u001a\u0004\u0018\u00010\bHÆ\u0003J\t\u0010.\u001a\u00020\u0006HÆ\u0003J\u000b\u0010/\u001a\u0004\u0018\u00010\u000bHÆ\u0003J\t\u00100\u001a\u00020\u0006HÆ\u0003J\u0017\u00101\u001a\u0010\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u000b\u0018\u00010\u000eHÆ\u0003Jk\u00102\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\t\u001a\u00020\u00062\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b2\b\b\u0002\u0010\f\u001a\u00020\u00062\u0016\b\u0002\u0010\r\u001a\u0010\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u000b\u0018\u00010\u000eHÆ\u0001J\u0013\u00103\u001a\u0002042\b\u00105\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u00106\u001a\u00020\u0006HÖ\u0001J\t\u00107\u001a\u00020\u000bHÖ\u0001R(\u0010\r\u001a\u0010\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u000b\u0018\u00010\u000eX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001a\u0010\f\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001c\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001bR\u001a\u0010\t\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u0015\"\u0004\b\u001d\u0010\u0017R\u001c\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u001f\"\u0004\b \u0010!R\u001a\u0010\u0005\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010\u0015\"\u0004\b#\u0010\u0017R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b$\u0010%\"\u0004\b&\u0010'R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b(\u0010%\"\u0004\b)\u0010'¨\u00068"}, d2 = {"Lcom/k2fsa/sherpa/onnx/GenerationConfig;", "", "silenceScale", "", "speed", "sid", "", "referenceAudio", "", "referenceSampleRate", "referenceText", "", "numSteps", "extra", "", "(FFI[FILjava/lang/String;ILjava/util/Map;)V", "getExtra", "()Ljava/util/Map;", "setExtra", "(Ljava/util/Map;)V", "getNumSteps", "()I", "setNumSteps", "(I)V", "getReferenceAudio", "()[F", "setReferenceAudio", "([F)V", "getReferenceSampleRate", "setReferenceSampleRate", "getReferenceText", "()Ljava/lang/String;", "setReferenceText", "(Ljava/lang/String;)V", "getSid", "setSid", "getSilenceScale", "()F", "setSilenceScale", "(F)V", "getSpeed", "setSpeed", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class GenerationConfig {
    private Map<String, String> extra;
    private int numSteps;
    private float[] referenceAudio;
    private int referenceSampleRate;
    private String referenceText;
    private int sid;
    private float silenceScale;
    private float speed;

    public GenerationConfig() {
        this(0.0f, 0.0f, 0, null, 0, null, 0, null, 255, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static /* synthetic */ GenerationConfig copy$default(GenerationConfig generationConfig, float f, float f2, int i, float[] fArr, int i2, String str, int i3, Map map, int i4, Object obj) {
        if ((i4 & 1) != 0) {
            f = generationConfig.silenceScale;
        }
        if ((i4 & 2) != 0) {
            f2 = generationConfig.speed;
        }
        if ((i4 & 4) != 0) {
            i = generationConfig.sid;
        }
        if ((i4 & 8) != 0) {
            fArr = generationConfig.referenceAudio;
        }
        if ((i4 & 16) != 0) {
            i2 = generationConfig.referenceSampleRate;
        }
        if ((i4 & 32) != 0) {
            str = generationConfig.referenceText;
        }
        if ((i4 & 64) != 0) {
            i3 = generationConfig.numSteps;
        }
        if ((i4 & 128) != 0) {
            map = generationConfig.extra;
        }
        int i5 = i3;
        Map map2 = map;
        int i6 = i2;
        String str2 = str;
        return generationConfig.copy(f, f2, i, fArr, i6, str2, i5, map2);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final float component1() {
        return this.silenceScale;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final float component2() {
        return this.speed;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final int component3() {
        return this.sid;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final float[] component4() {
        return this.referenceAudio;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final int component5() {
        return this.referenceSampleRate;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final String component6() {
        return this.referenceText;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final int component7() {
        return this.numSteps;
    }

    public final Map<String, String> component8() {
        return this.extra;
    }

    public final GenerationConfig copy(float silenceScale, float speed, int sid, float[] referenceAudio, int referenceSampleRate, String referenceText, int numSteps, Map<String, String> extra) {
        return new GenerationConfig(silenceScale, speed, sid, referenceAudio, referenceSampleRate, referenceText, numSteps, extra);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof GenerationConfig)) {
            return false;
        }
        GenerationConfig generationConfig = (GenerationConfig) other;
        return Float.compare(this.silenceScale, generationConfig.silenceScale) == 0 && Float.compare(this.speed, generationConfig.speed) == 0 && this.sid == generationConfig.sid && Intrinsics.areEqual(this.referenceAudio, generationConfig.referenceAudio) && this.referenceSampleRate == generationConfig.referenceSampleRate && Intrinsics.areEqual(this.referenceText, generationConfig.referenceText) && this.numSteps == generationConfig.numSteps && Intrinsics.areEqual(this.extra, generationConfig.extra);
    }

    public int hashCode() {
        int iHashCode = ((((Float.hashCode(this.silenceScale) * 31) + Float.hashCode(this.speed)) * 31) + Integer.hashCode(this.sid)) * 31;
        float[] fArr = this.referenceAudio;
        int iHashCode2 = (((iHashCode + (fArr == null ? 0 : Arrays.hashCode(fArr))) * 31) + Integer.hashCode(this.referenceSampleRate)) * 31;
        String str = this.referenceText;
        int iHashCode3 = (((iHashCode2 + (str == null ? 0 : str.hashCode())) * 31) + Integer.hashCode(this.numSteps)) * 31;
        Map<String, String> map = this.extra;
        return iHashCode3 + (map != null ? map.hashCode() : 0);
    }

    public String toString() {
        return "GenerationConfig(silenceScale=" + this.silenceScale + ", speed=" + this.speed + ", sid=" + this.sid + ", referenceAudio=" + Arrays.toString(this.referenceAudio) + ", referenceSampleRate=" + this.referenceSampleRate + ", referenceText=" + this.referenceText + ", numSteps=" + this.numSteps + ", extra=" + this.extra + ')';
    }

    public GenerationConfig(float f, float f2, int i, float[] fArr, int i2, String str, int i3, Map<String, String> map) {
        this.silenceScale = f;
        this.speed = f2;
        this.sid = i;
        this.referenceAudio = fArr;
        this.referenceSampleRate = i2;
        this.referenceText = str;
        this.numSteps = i3;
        this.extra = map;
    }

    public /* synthetic */ GenerationConfig(float f, float f2, int i, float[] fArr, int i2, String str, int i3, Map map, int i4, DefaultConstructorMarker defaultConstructorMarker) {
        this((i4 & 1) != 0 ? 0.2f : f, (i4 & 2) != 0 ? 1.0f : f2, (i4 & 4) != 0 ? 0 : i, (i4 & 8) != 0 ? null : fArr, (i4 & 16) != 0 ? 0 : i2, (i4 & 32) != 0 ? null : str, (i4 & 64) != 0 ? 5 : i3, (i4 & 128) != 0 ? null : map);
    }

    public final float getSilenceScale() {
        return this.silenceScale;
    }

    public final void setSilenceScale(float f) {
        this.silenceScale = f;
    }

    public final float getSpeed() {
        return this.speed;
    }

    public final void setSpeed(float f) {
        this.speed = f;
    }

    public final int getSid() {
        return this.sid;
    }

    public final void setSid(int i) {
        this.sid = i;
    }

    public final float[] getReferenceAudio() {
        return this.referenceAudio;
    }

    public final void setReferenceAudio(float[] fArr) {
        this.referenceAudio = fArr;
    }

    public final int getReferenceSampleRate() {
        return this.referenceSampleRate;
    }

    public final void setReferenceSampleRate(int i) {
        this.referenceSampleRate = i;
    }

    public final String getReferenceText() {
        return this.referenceText;
    }

    public final void setReferenceText(String str) {
        this.referenceText = str;
    }

    public final int getNumSteps() {
        return this.numSteps;
    }

    public final void setNumSteps(int i) {
        this.numSteps = i;
    }

    public final Map<String, String> getExtra() {
        return this.extra;
    }

    public final void setExtra(Map<String, String> map) {
        this.extra = map;
    }
}
