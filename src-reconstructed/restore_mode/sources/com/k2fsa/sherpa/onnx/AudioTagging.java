package com.k2fsa.sherpa.onnx;

import android.content.res.AssetManager;
import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import ru.big.town.restoremode.BuildConfig;

/* JADX INFO: compiled from: AudioTagging.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0006\u0018\u0000 \u001a2\u00020\u0001:\u0001\u001aB\u0019\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J#\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000f¢\u0006\u0002\u0010\u0010J,\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0011\u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\u000fH\u0082 ¢\u0006\u0002\u0010\u0012J\u0006\u0010\u0013\u001a\u00020\rJ\u0011\u0010\u0013\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\bH\u0082 J\u0011\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0007\u001a\u00020\bH\u0082 J\b\u0010\u0016\u001a\u00020\u0015H\u0004J\u0019\u0010\u0017\u001a\u00020\b2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0082 J\u0011\u0010\u0018\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u0005H\u0082 J\u0006\u0010\u0019\u001a\u00020\u0015R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001b"}, d2 = {"Lcom/k2fsa/sherpa/onnx/AudioTagging;", "", "assetManager", "Landroid/content/res/AssetManager;", "config", "Lcom/k2fsa/sherpa/onnx/AudioTaggingConfig;", "(Landroid/content/res/AssetManager;Lcom/k2fsa/sherpa/onnx/AudioTaggingConfig;)V", "ptr", "", "compute", "", "Lcom/k2fsa/sherpa/onnx/AudioEvent;", "stream", "Lcom/k2fsa/sherpa/onnx/OfflineStream;", "topK", "", "(Lcom/k2fsa/sherpa/onnx/OfflineStream;I)[Lcom/k2fsa/sherpa/onnx/AudioEvent;", "streamPtr", "(JJI)[Lcom/k2fsa/sherpa/onnx/AudioEvent;", "createStream", "delete", "", "finalize", "newFromAsset", "newFromFile", BuildConfig.BUILD_TYPE, "Companion", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final class AudioTagging {
    private long ptr;

    private final native AudioEvent[] compute(long ptr, long streamPtr, int topK);

    private final native long createStream(long ptr);

    private final native void delete(long ptr);

    private final native long newFromAsset(AssetManager assetManager, AudioTaggingConfig config);

    private final native long newFromFile(AudioTaggingConfig config);

    public AudioTagging(AssetManager assetManager, AudioTaggingConfig config) {
        long jNewFromFile;
        Intrinsics.checkNotNullParameter(config, "config");
        if (assetManager != null) {
            jNewFromFile = newFromAsset(assetManager, config);
        } else {
            jNewFromFile = newFromFile(config);
        }
        this.ptr = jNewFromFile;
        if (jNewFromFile == 0) {
            throw new IllegalArgumentException("Invalid AudioTaggingConfig: failed to create native AudioTagging".toString());
        }
    }

    public /* synthetic */ AudioTagging(AssetManager assetManager, AudioTaggingConfig audioTaggingConfig, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : assetManager, audioTaggingConfig);
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

    public final OfflineStream createStream() {
        return new OfflineStream(createStream(this.ptr));
    }

    public static /* synthetic */ AudioEvent[] compute$default(AudioTagging audioTagging, OfflineStream offlineStream, int i, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = -1;
        }
        return audioTagging.compute(offlineStream, i);
    }

    public final AudioEvent[] compute(OfflineStream stream, int topK) {
        Intrinsics.checkNotNullParameter(stream, "stream");
        return compute(this.ptr, stream.getPtr(), topK);
    }

    static {
        System.loadLibrary("sherpa-onnx-jni");
    }
}
