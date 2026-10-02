package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;

/* JADX INFO: compiled from: OfflineSpeakerDiarization.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0003¢\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0006HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J1\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0017\u001a\u00020\u0006HÖ\u0001J\t\u0010\u0018\u001a\u00020\u0019HÖ\u0001R\u0011\u0010\u0007\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\n¨\u0006\u001a"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineSpeakerDiarizationSegment;", "", "start", "", "end", "speaker", "", "confidence", "(FFIF)V", "getConfidence", "()F", "getEnd", "getSpeaker", "()I", "getStart", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "toString", "", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final /* data */ class OfflineSpeakerDiarizationSegment {
    private final float confidence;
    private final float end;
    private final int speaker;
    private final float start;

    public static /* synthetic */ OfflineSpeakerDiarizationSegment copy$default(OfflineSpeakerDiarizationSegment offlineSpeakerDiarizationSegment, float f, float f2, int i, float f3, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            f = offlineSpeakerDiarizationSegment.start;
        }
        if ((i2 & 2) != 0) {
            f2 = offlineSpeakerDiarizationSegment.end;
        }
        if ((i2 & 4) != 0) {
            i = offlineSpeakerDiarizationSegment.speaker;
        }
        if ((i2 & 8) != 0) {
            f3 = offlineSpeakerDiarizationSegment.confidence;
        }
        return offlineSpeakerDiarizationSegment.copy(f, f2, i, f3);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final float getStart() {
        return this.start;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final float getEnd() {
        return this.end;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final int getSpeaker() {
        return this.speaker;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final float getConfidence() {
        return this.confidence;
    }

    public final OfflineSpeakerDiarizationSegment copy(float start, float end, int speaker, float confidence) {
        return new OfflineSpeakerDiarizationSegment(start, end, speaker, confidence);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OfflineSpeakerDiarizationSegment)) {
            return false;
        }
        OfflineSpeakerDiarizationSegment offlineSpeakerDiarizationSegment = (OfflineSpeakerDiarizationSegment) other;
        return Float.compare(this.start, offlineSpeakerDiarizationSegment.start) == 0 && Float.compare(this.end, offlineSpeakerDiarizationSegment.end) == 0 && this.speaker == offlineSpeakerDiarizationSegment.speaker && Float.compare(this.confidence, offlineSpeakerDiarizationSegment.confidence) == 0;
    }

    public int hashCode() {
        return (((((Float.hashCode(this.start) * 31) + Float.hashCode(this.end)) * 31) + Integer.hashCode(this.speaker)) * 31) + Float.hashCode(this.confidence);
    }

    public String toString() {
        return "OfflineSpeakerDiarizationSegment(start=" + this.start + ", end=" + this.end + ", speaker=" + this.speaker + ", confidence=" + this.confidence + ')';
    }

    public OfflineSpeakerDiarizationSegment(float f, float f2, int i, float f3) {
        this.start = f;
        this.end = f2;
        this.speaker = i;
        this.confidence = f3;
    }

    public final float getStart() {
        return this.start;
    }

    public final float getEnd() {
        return this.end;
    }

    public final int getSpeaker() {
        return this.speaker;
    }

    public final float getConfidence() {
        return this.confidence;
    }
}
