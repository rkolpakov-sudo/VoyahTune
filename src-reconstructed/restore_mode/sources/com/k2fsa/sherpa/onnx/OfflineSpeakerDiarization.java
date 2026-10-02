package com.k2fsa.sherpa.onnx;

import android.content.res.AssetManager;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.sun.jna.Callback;
import kotlin.Metadata;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import ru.big.town.restoremode.BuildConfig;

/* JADX INFO: compiled from: OfflineSpeakerDiarization.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0014\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\u0018\u0000 &2\u00020\u0001:\u0001&B\u0019\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0011\u0010\u000b\u001a\u00020\f2\u0006\u0010\t\u001a\u00020\nH\u0082 J\b\u0010\r\u001a\u00020\fH\u0004J\u0011\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\t\u001a\u00020\nH\u0082 J\u0019\u0010\u0010\u001a\u00020\n2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0082 J\u0011\u0010\u0011\u001a\u00020\n2\u0006\u0010\u0004\u001a\u00020\u0005H\u0082 J\u0019\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\u0006\u0010\u0015\u001a\u00020\u0016¢\u0006\u0002\u0010\u0017J$\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0015\u001a\u00020\u0016H\u0082 ¢\u0006\u0002\u0010\u0018Jp\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\u0006\u0010\u0015\u001a\u00020\u00162K\u0010\u001a\u001aG\u0012\u0013\u0012\u00110\u000f¢\u0006\f\b\u001c\u0012\b\b\u001d\u0012\u0004\b\b(\u001e\u0012\u0013\u0012\u00110\u000f¢\u0006\f\b\u001c\u0012\b\b\u001d\u0012\u0004\b\b(\u001f\u0012\u0013\u0012\u00110\n¢\u0006\f\b\u001c\u0012\b\b\u001d\u0012\u0004\b\b( \u0012\u0004\u0012\u00020\u000f0\u001b2\b\b\u0002\u0010 \u001a\u00020\n¢\u0006\u0002\u0010!Jy\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0015\u001a\u00020\u00162K\u0010\u001a\u001aG\u0012\u0013\u0012\u00110\u000f¢\u0006\f\b\u001c\u0012\b\b\u001d\u0012\u0004\b\b(\u001e\u0012\u0013\u0012\u00110\u000f¢\u0006\f\b\u001c\u0012\b\b\u001d\u0012\u0004\b\b(\u001f\u0012\u0013\u0012\u00110\n¢\u0006\f\b\u001c\u0012\b\b\u001d\u0012\u0004\b\b( \u0012\u0004\u0012\u00020\u000f0\u001b2\u0006\u0010 \u001a\u00020\nH\u0082 ¢\u0006\u0002\u0010\"J\u0006\u0010#\u001a\u00020\fJ\u0006\u0010$\u001a\u00020\u000fJ\u000e\u0010%\u001a\u00020\f2\u0006\u0010\u0004\u001a\u00020\u0005J\u0019\u0010%\u001a\u00020\f2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0004\u001a\u00020\u0005H\u0082 R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006'"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineSpeakerDiarization;", "", "assetManager", "Landroid/content/res/AssetManager;", "config", "Lcom/k2fsa/sherpa/onnx/OfflineSpeakerDiarizationConfig;", "(Landroid/content/res/AssetManager;Lcom/k2fsa/sherpa/onnx/OfflineSpeakerDiarizationConfig;)V", "getConfig", "()Lcom/k2fsa/sherpa/onnx/OfflineSpeakerDiarizationConfig;", "ptr", "", "delete", "", "finalize", "getSampleRate", "", "newFromAsset", "newFromFile", "process", "", "Lcom/k2fsa/sherpa/onnx/OfflineSpeakerDiarizationSegment;", "samples", "", "([F)[Lcom/k2fsa/sherpa/onnx/OfflineSpeakerDiarizationSegment;", "(J[F)[Lcom/k2fsa/sherpa/onnx/OfflineSpeakerDiarizationSegment;", "processWithCallback", Callback.METHOD_NAME, "Lkotlin/Function3;", "Lkotlin/ParameterName;", "name", "numProcessedChunks", "numTotalChunks", "arg", "([FLkotlin/jvm/functions/Function3;J)[Lcom/k2fsa/sherpa/onnx/OfflineSpeakerDiarizationSegment;", "(J[FLkotlin/jvm/functions/Function3;J)[Lcom/k2fsa/sherpa/onnx/OfflineSpeakerDiarizationSegment;", BuildConfig.BUILD_TYPE, "sampleRate", "setConfig", "Companion", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final class OfflineSpeakerDiarization {
    private final OfflineSpeakerDiarizationConfig config;
    private long ptr;

    private final native void delete(long ptr);

    private final native int getSampleRate(long ptr);

    private final native long newFromAsset(AssetManager assetManager, OfflineSpeakerDiarizationConfig config);

    private final native long newFromFile(OfflineSpeakerDiarizationConfig config);

    private final native OfflineSpeakerDiarizationSegment[] process(long ptr, float[] samples);

    private final native OfflineSpeakerDiarizationSegment[] processWithCallback(long ptr, float[] samples, Function3<? super Integer, ? super Integer, ? super Long, Integer> callback, long arg);

    private final native void setConfig(long ptr, OfflineSpeakerDiarizationConfig config);

    public OfflineSpeakerDiarization(AssetManager assetManager, OfflineSpeakerDiarizationConfig config) {
        long jNewFromFile;
        Intrinsics.checkNotNullParameter(config, "config");
        this.config = config;
        if (assetManager != null) {
            jNewFromFile = newFromAsset(assetManager, config);
        } else {
            jNewFromFile = newFromFile(config);
        }
        this.ptr = jNewFromFile;
        if (jNewFromFile == 0) {
            throw new IllegalArgumentException("Invalid OfflineSpeakerDiarizationConfig: failed to create native OfflineSpeakerDiarization".toString());
        }
    }

    public /* synthetic */ OfflineSpeakerDiarization(AssetManager assetManager, OfflineSpeakerDiarizationConfig offlineSpeakerDiarizationConfig, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : assetManager, offlineSpeakerDiarizationConfig);
    }

    public final OfflineSpeakerDiarizationConfig getConfig() {
        return this.config;
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

    public final void setConfig(OfflineSpeakerDiarizationConfig config) {
        Intrinsics.checkNotNullParameter(config, "config");
        setConfig(this.ptr, config);
    }

    public final int sampleRate() {
        return getSampleRate(this.ptr);
    }

    public final OfflineSpeakerDiarizationSegment[] process(float[] samples) {
        Intrinsics.checkNotNullParameter(samples, "samples");
        return process(this.ptr, samples);
    }

    public static /* synthetic */ OfflineSpeakerDiarizationSegment[] processWithCallback$default(OfflineSpeakerDiarization offlineSpeakerDiarization, float[] fArr, Function3 function3, long j, int i, Object obj) {
        if ((i & 4) != 0) {
            j = 0;
        }
        return offlineSpeakerDiarization.processWithCallback(fArr, function3, j);
    }

    public final OfflineSpeakerDiarizationSegment[] processWithCallback(float[] samples, Function3<? super Integer, ? super Integer, ? super Long, Integer> callback, long arg) {
        Intrinsics.checkNotNullParameter(samples, "samples");
        Intrinsics.checkNotNullParameter(callback, "callback");
        return processWithCallback(this.ptr, samples, callback, arg);
    }

    static {
        System.loadLibrary("sherpa-onnx-jni");
    }
}
