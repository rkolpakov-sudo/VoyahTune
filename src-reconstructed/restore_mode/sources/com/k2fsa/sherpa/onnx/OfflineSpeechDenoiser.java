package com.k2fsa.sherpa.onnx;

import android.content.res.AssetManager;
import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import ru.big.town.restoremode.BuildConfig;

/* JADX INFO: compiled from: OfflineSpeechDenoiser.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0014\n\u0002\b\u0002\u0018\u0000 \u00172\u00020\u0001:\u0001\u0017B\u0019\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0011\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0007\u001a\u00020\bH\u0082 J\b\u0010\u000f\u001a\u00020\u000eH\u0004J\u0011\u0010\u000b\u001a\u00020\n2\u0006\u0010\u0007\u001a\u00020\bH\u0082 J\u0019\u0010\u0010\u001a\u00020\b2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0082 J\u0011\u0010\u0011\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u0005H\u0082 J\u0006\u0010\u0012\u001a\u00020\u000eJ\u0016\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\t\u001a\u00020\nJ!\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\t\u001a\u00020\nH\u0082 R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\t\u001a\u00020\n8F¢\u0006\u0006\u001a\u0004\b\u000b\u0010\f¨\u0006\u0018"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineSpeechDenoiser;", "", "assetManager", "Landroid/content/res/AssetManager;", "config", "Lcom/k2fsa/sherpa/onnx/OfflineSpeechDenoiserConfig;", "(Landroid/content/res/AssetManager;Lcom/k2fsa/sherpa/onnx/OfflineSpeechDenoiserConfig;)V", "ptr", "", "sampleRate", "", "getSampleRate", "()I", "delete", "", "finalize", "newFromAsset", "newFromFile", BuildConfig.BUILD_TYPE, "run", "Lcom/k2fsa/sherpa/onnx/DenoisedAudio;", "samples", "", "Companion", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final class OfflineSpeechDenoiser {
    private long ptr;

    private final native void delete(long ptr);

    private final native int getSampleRate(long ptr);

    private final native long newFromAsset(AssetManager assetManager, OfflineSpeechDenoiserConfig config);

    private final native long newFromFile(OfflineSpeechDenoiserConfig config);

    private final native DenoisedAudio run(long ptr, float[] samples, int sampleRate);

    public OfflineSpeechDenoiser(AssetManager assetManager, OfflineSpeechDenoiserConfig config) {
        long jNewFromFile;
        Intrinsics.checkNotNullParameter(config, "config");
        if (assetManager != null) {
            jNewFromFile = newFromAsset(assetManager, config);
        } else {
            jNewFromFile = newFromFile(config);
        }
        this.ptr = jNewFromFile;
        if (jNewFromFile == 0) {
            throw new IllegalArgumentException("Invalid OfflineSpeechDenoiserConfig: failed to create native OfflineSpeechDenoiser".toString());
        }
    }

    public /* synthetic */ OfflineSpeechDenoiser(AssetManager assetManager, OfflineSpeechDenoiserConfig offlineSpeechDenoiserConfig, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : assetManager, offlineSpeechDenoiserConfig);
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

    public final DenoisedAudio run(float[] samples, int sampleRate) {
        Intrinsics.checkNotNullParameter(samples, "samples");
        return run(this.ptr, samples, sampleRate);
    }

    public final int getSampleRate() {
        return getSampleRate(this.ptr);
    }

    static {
        System.loadLibrary("sherpa-onnx-jni");
    }
}
