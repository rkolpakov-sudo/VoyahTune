package com.k2fsa.sherpa.onnx;

import android.content.res.AssetManager;
import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import ru.big.town.restoremode.BuildConfig;

/* JADX INFO: compiled from: Speaker.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0014\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\u0018\u0000 \u00192\u00020\u0001:\u0001\u0019B\u0019\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fJ\u0019\u0010\t\u001a\u00020\n2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\bH\u0082 J\u0006\u0010\u000e\u001a\u00020\fJ\u0011\u0010\u000e\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\bH\u0082 J\u0011\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0007\u001a\u00020\bH\u0082 J\u0006\u0010\u0011\u001a\u00020\u0012J\u0011\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0007\u001a\u00020\bH\u0082 J\b\u0010\u0013\u001a\u00020\u0010H\u0004J\u000e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u000b\u001a\u00020\fJ\u0019\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\bH\u0082 J\u0019\u0010\u0016\u001a\u00020\b2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0082 J\u0011\u0010\u0017\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u0005H\u0082 J\u0006\u0010\u0018\u001a\u00020\u0010R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001a"}, d2 = {"Lcom/k2fsa/sherpa/onnx/SpeakerEmbeddingExtractor;", "", "assetManager", "Landroid/content/res/AssetManager;", "config", "Lcom/k2fsa/sherpa/onnx/SpeakerEmbeddingExtractorConfig;", "(Landroid/content/res/AssetManager;Lcom/k2fsa/sherpa/onnx/SpeakerEmbeddingExtractorConfig;)V", "ptr", "", "compute", "", "stream", "Lcom/k2fsa/sherpa/onnx/OnlineStream;", "streamPtr", "createStream", "delete", "", "dim", "", "finalize", "isReady", "", "newFromAsset", "newFromFile", BuildConfig.BUILD_TYPE, "Companion", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final class SpeakerEmbeddingExtractor {
    private long ptr;

    private final native float[] compute(long ptr, long streamPtr);

    private final native long createStream(long ptr);

    private final native void delete(long ptr);

    private final native int dim(long ptr);

    private final native boolean isReady(long ptr, long streamPtr);

    private final native long newFromAsset(AssetManager assetManager, SpeakerEmbeddingExtractorConfig config);

    private final native long newFromFile(SpeakerEmbeddingExtractorConfig config);

    public SpeakerEmbeddingExtractor(AssetManager assetManager, SpeakerEmbeddingExtractorConfig config) {
        long jNewFromFile;
        Intrinsics.checkNotNullParameter(config, "config");
        if (assetManager != null) {
            jNewFromFile = newFromAsset(assetManager, config);
        } else {
            jNewFromFile = newFromFile(config);
        }
        this.ptr = jNewFromFile;
        if (jNewFromFile == 0) {
            throw new IllegalArgumentException("Invalid SpeakerEmbeddingExtractorConfig: failed to create native SpeakerEmbeddingExtractor".toString());
        }
    }

    public /* synthetic */ SpeakerEmbeddingExtractor(AssetManager assetManager, SpeakerEmbeddingExtractorConfig speakerEmbeddingExtractorConfig, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : assetManager, speakerEmbeddingExtractorConfig);
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

    public final OnlineStream createStream() {
        return new OnlineStream(createStream(this.ptr));
    }

    public final boolean isReady(OnlineStream stream) {
        Intrinsics.checkNotNullParameter(stream, "stream");
        return isReady(this.ptr, stream.getPtr());
    }

    public final float[] compute(OnlineStream stream) {
        Intrinsics.checkNotNullParameter(stream, "stream");
        return compute(this.ptr, stream.getPtr());
    }

    public final int dim() {
        return dim(this.ptr);
    }

    static {
        System.loadLibrary("sherpa-onnx-jni");
    }
}
