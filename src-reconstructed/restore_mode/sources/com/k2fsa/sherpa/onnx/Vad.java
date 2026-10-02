package com.k2fsa.sherpa.onnx;

import android.content.res.AssetManager;
import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import ru.big.town.restoremode.BuildConfig;

/* JADX INFO: compiled from: Vad.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0014\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u0000 !2\u00020\u0001:\u0001!B\u0019\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010J\u0019\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u0010H\u0082 J\u0006\u0010\u0011\u001a\u00020\u000eJ\u0011\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\fH\u0082 J\u000e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u000f\u001a\u00020\u0010J\u0019\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u0010H\u0082 J\u0011\u0010\u0014\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\fH\u0082 J\u0006\u0010\u0015\u001a\u00020\u0016J\u0011\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u000b\u001a\u00020\fH\u0082 J\b\u0010\u0017\u001a\u00020\u000eH\u0004J\u0006\u0010\u0018\u001a\u00020\u000eJ\u0011\u0010\u0018\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\fH\u0082 J\u0006\u0010\u0019\u001a\u00020\u001aJ\u0011\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u000b\u001a\u00020\fH\u0082 J\u0006\u0010\u001b\u001a\u00020\u0016J\u0011\u0010\u001b\u001a\u00020\u00162\u0006\u0010\u000b\u001a\u00020\fH\u0082 J\u0019\u0010\u001c\u001a\u00020\f2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0082 J\u0011\u0010\u001d\u001a\u00020\f2\u0006\u0010\u0004\u001a\u00020\u0005H\u0082 J\u0006\u0010\u001e\u001a\u00020\u000eJ\u0011\u0010\u001e\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\fH\u0082 J\u0006\u0010\u001f\u001a\u00020\u000eJ\u0006\u0010 \u001a\u00020\u000eJ\u0011\u0010 \u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\fH\u0082 R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\""}, d2 = {"Lcom/k2fsa/sherpa/onnx/Vad;", "", "assetManager", "Landroid/content/res/AssetManager;", "config", "Lcom/k2fsa/sherpa/onnx/VadModelConfig;", "(Landroid/content/res/AssetManager;Lcom/k2fsa/sherpa/onnx/VadModelConfig;)V", "getConfig", "()Lcom/k2fsa/sherpa/onnx/VadModelConfig;", "setConfig", "(Lcom/k2fsa/sherpa/onnx/VadModelConfig;)V", "ptr", "", "acceptWaveform", "", "samples", "", "clear", "compute", "", "delete", "empty", "", "finalize", "flush", "front", "Lcom/k2fsa/sherpa/onnx/SpeechSegment;", "isSpeechDetected", "newFromAsset", "newFromFile", "pop", BuildConfig.BUILD_TYPE, "reset", "Companion", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final class Vad {
    private VadModelConfig config;
    private long ptr;

    private final native void acceptWaveform(long ptr, float[] samples);

    private final native void clear(long ptr);

    private final native float compute(long ptr, float[] samples);

    private final native void delete(long ptr);

    private final native boolean empty(long ptr);

    private final native void flush(long ptr);

    private final native SpeechSegment front(long ptr);

    private final native boolean isSpeechDetected(long ptr);

    private final native long newFromAsset(AssetManager assetManager, VadModelConfig config);

    private final native long newFromFile(VadModelConfig config);

    private final native void pop(long ptr);

    private final native void reset(long ptr);

    public Vad(AssetManager assetManager, VadModelConfig config) {
        Intrinsics.checkNotNullParameter(config, "config");
        this.config = config;
        if (assetManager != null) {
            this.ptr = newFromAsset(assetManager, config);
        } else {
            this.ptr = newFromFile(config);
        }
        if (this.ptr == 0) {
            throw new IllegalArgumentException("Invalid VadConfig: failed to create native Vad".toString());
        }
    }

    public /* synthetic */ Vad(AssetManager assetManager, VadModelConfig vadModelConfig, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : assetManager, vadModelConfig);
    }

    public final VadModelConfig getConfig() {
        return this.config;
    }

    public final void setConfig(VadModelConfig vadModelConfig) {
        Intrinsics.checkNotNullParameter(vadModelConfig, "<set-?>");
        this.config = vadModelConfig;
    }

    protected final void finalize() {
        long j = this.ptr;
        if (j != 0) {
            delete(j);
            this.ptr = 0L;
        }
    }

    public final void release() {
        finalize();
    }

    public final float compute(float[] samples) {
        Intrinsics.checkNotNullParameter(samples, "samples");
        return compute(this.ptr, samples);
    }

    public final void acceptWaveform(float[] samples) {
        Intrinsics.checkNotNullParameter(samples, "samples");
        acceptWaveform(this.ptr, samples);
    }

    public final boolean empty() {
        return empty(this.ptr);
    }

    public final void pop() {
        pop(this.ptr);
    }

    public final SpeechSegment front() {
        return front(this.ptr);
    }

    public final void clear() {
        clear(this.ptr);
    }

    public final boolean isSpeechDetected() {
        return isSpeechDetected(this.ptr);
    }

    public final void reset() {
        reset(this.ptr);
    }

    public final void flush() {
        flush(this.ptr);
    }

    static {
        System.loadLibrary("sherpa-onnx-jni");
    }
}
