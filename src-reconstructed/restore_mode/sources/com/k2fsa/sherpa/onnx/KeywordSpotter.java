package com.k2fsa.sherpa.onnx;

import android.content.res.AssetManager;
import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import ru.big.town.restoremode.BuildConfig;

/* JADX INFO: compiled from: KeywordSpotter.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\u0018\u0000 \u001d2\u00020\u0001:\u0001\u001dB\u0019\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0019\u0010\u000b\u001a\u00020\n2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\rH\u0082 J\u0010\u0010\u000b\u001a\u00020\u000e2\b\b\u0002\u0010\f\u001a\u00020\rJ\u000e\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u000eJ\u0019\u0010\u000f\u001a\u00020\u00102\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\nH\u0082 J\u0011\u0010\u0013\u001a\u00020\u00102\u0006\u0010\t\u001a\u00020\nH\u0082 J\b\u0010\u0014\u001a\u00020\u0010H\u0004J\u000e\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0011\u001a\u00020\u000eJ\u0019\u0010\u0015\u001a\u00020\u00162\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\nH\u0082 J\u000e\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0011\u001a\u00020\u000eJ\u0019\u0010\u0017\u001a\u00020\u00182\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\nH\u0082 J\u0019\u0010\u0019\u001a\u00020\n2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0082 J\u0011\u0010\u001a\u001a\u00020\n2\u0006\u0010\u0004\u001a\u00020\u0005H\u0082 J\u0006\u0010\u001b\u001a\u00020\u0010J\u000e\u0010\u001c\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u000eJ\u0019\u0010\u001c\u001a\u00020\u00102\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\nH\u0082 R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001e"}, d2 = {"Lcom/k2fsa/sherpa/onnx/KeywordSpotter;", "", "assetManager", "Landroid/content/res/AssetManager;", "config", "Lcom/k2fsa/sherpa/onnx/KeywordSpotterConfig;", "(Landroid/content/res/AssetManager;Lcom/k2fsa/sherpa/onnx/KeywordSpotterConfig;)V", "getConfig", "()Lcom/k2fsa/sherpa/onnx/KeywordSpotterConfig;", "ptr", "", "createStream", "keywords", "", "Lcom/k2fsa/sherpa/onnx/OnlineStream;", "decode", "", "stream", "streamPtr", "delete", "finalize", "getResult", "Lcom/k2fsa/sherpa/onnx/KeywordSpotterResult;", "isReady", "", "newFromAsset", "newFromFile", BuildConfig.BUILD_TYPE, "reset", "Companion", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final class KeywordSpotter {
    private final KeywordSpotterConfig config;
    private long ptr;

    private final native long createStream(long ptr, String keywords);

    private final native void decode(long ptr, long streamPtr);

    private final native void delete(long ptr);

    private final native KeywordSpotterResult getResult(long ptr, long streamPtr);

    private final native boolean isReady(long ptr, long streamPtr);

    private final native long newFromAsset(AssetManager assetManager, KeywordSpotterConfig config);

    private final native long newFromFile(KeywordSpotterConfig config);

    private final native void reset(long ptr, long streamPtr);

    public KeywordSpotter(AssetManager assetManager, KeywordSpotterConfig config) {
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
            throw new IllegalArgumentException("Invalid KeywordSpotterConfig: failed to create native KeywordSpotter".toString());
        }
    }

    public /* synthetic */ KeywordSpotter(AssetManager assetManager, KeywordSpotterConfig keywordSpotterConfig, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : assetManager, keywordSpotterConfig);
    }

    public final KeywordSpotterConfig getConfig() {
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

    public static /* synthetic */ OnlineStream createStream$default(KeywordSpotter keywordSpotter, String str, int i, Object obj) {
        if ((i & 1) != 0) {
            str = "";
        }
        return keywordSpotter.createStream(str);
    }

    public final OnlineStream createStream(String keywords) {
        Intrinsics.checkNotNullParameter(keywords, "keywords");
        return new OnlineStream(createStream(this.ptr, keywords));
    }

    public final void decode(OnlineStream stream) {
        Intrinsics.checkNotNullParameter(stream, "stream");
        decode(this.ptr, stream.getPtr());
    }

    public final void reset(OnlineStream stream) {
        Intrinsics.checkNotNullParameter(stream, "stream");
        reset(this.ptr, stream.getPtr());
    }

    public final boolean isReady(OnlineStream stream) {
        Intrinsics.checkNotNullParameter(stream, "stream");
        return isReady(this.ptr, stream.getPtr());
    }

    public final KeywordSpotterResult getResult(OnlineStream stream) {
        Intrinsics.checkNotNullParameter(stream, "stream");
        return getResult(this.ptr, stream.getPtr());
    }

    static {
        System.loadLibrary("sherpa-onnx-jni");
    }
}
