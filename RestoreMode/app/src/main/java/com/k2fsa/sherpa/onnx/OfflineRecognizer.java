package com.k2fsa.sherpa.onnx;

import android.content.res.AssetManager;
import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import ru.big.town.restoremode.BuildConfig;

/* JADX INFO: compiled from: OfflineRecognizer.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 \u001c2\u00020\u0001:\u0001\u001cB\u0019\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0006\u0010\u000b\u001a\u00020\fJ\u0011\u0010\u000b\u001a\u00020\n2\u0006\u0010\t\u001a\u00020\nH\u0082 J\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\u0019\u0010\u000f\u001a\u00020\n2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\u000eH\u0082 J\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\fJ\u0019\u0010\u0010\u001a\u00020\u00112\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0013\u001a\u00020\nH\u0082 J\u0011\u0010\u0014\u001a\u00020\u00112\u0006\u0010\t\u001a\u00020\nH\u0082 J\b\u0010\u0015\u001a\u00020\u0011H\u0004J\u000e\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0012\u001a\u00020\fJ\u0011\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0013\u001a\u00020\nH\u0082 J\u0019\u0010\u0018\u001a\u00020\n2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0082 J\u0011\u0010\u0019\u001a\u00020\n2\u0006\u0010\u0004\u001a\u00020\u0005H\u0082 J\u0006\u0010\u001a\u001a\u00020\u0011J\u000e\u0010\u001b\u001a\u00020\u00112\u0006\u0010\u0004\u001a\u00020\u0005J\u0019\u0010\u001b\u001a\u00020\u00112\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0004\u001a\u00020\u0005H\u0082 R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001d"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineRecognizer;", "", "assetManager", "Landroid/content/res/AssetManager;", "config", "Lcom/k2fsa/sherpa/onnx/OfflineRecognizerConfig;", "(Landroid/content/res/AssetManager;Lcom/k2fsa/sherpa/onnx/OfflineRecognizerConfig;)V", "getConfig", "()Lcom/k2fsa/sherpa/onnx/OfflineRecognizerConfig;", "ptr", "", "createStream", "Lcom/k2fsa/sherpa/onnx/OfflineStream;", "hotwords", "", "createStreamWithHotwords", "decode", "", "stream", "streamPtr", "delete", "finalize", "getResult", "Lcom/k2fsa/sherpa/onnx/OfflineRecognizerResult;", "newFromAsset", "newFromFile", BuildConfig.BUILD_TYPE, "setConfig", "Companion", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final class OfflineRecognizer {

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private final OfflineRecognizerConfig config;
    private long ptr;

    private final native long createStream(long ptr);

    private final native long createStreamWithHotwords(long ptr, String hotwords);

    private final native void decode(long ptr, long streamPtr);

    private final native void delete(long ptr);

    private final native OfflineRecognizerResult getResult(long streamPtr);

    private final native long newFromAsset(AssetManager assetManager, OfflineRecognizerConfig config);

    private final native long newFromFile(OfflineRecognizerConfig config);

    @JvmStatic
    public static final native void prependAdspLibraryPath(String str);

    private final native void setConfig(long ptr, OfflineRecognizerConfig config);

    public OfflineRecognizer(AssetManager assetManager, OfflineRecognizerConfig config) {
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
            throw new IllegalArgumentException("Invalid OfflineRecognizerConfig: failed to create native OfflineRecognizer".toString());
        }
    }

    public /* synthetic */ OfflineRecognizer(AssetManager assetManager, OfflineRecognizerConfig offlineRecognizerConfig, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : assetManager, offlineRecognizerConfig);
    }

    public final OfflineRecognizerConfig getConfig() {
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

    public final OfflineStream createStream() {
        return new OfflineStream(createStream(this.ptr));
    }

    public final OfflineStream createStream(String hotwords) {
        Intrinsics.checkNotNullParameter(hotwords, "hotwords");
        return new OfflineStream(createStreamWithHotwords(this.ptr, hotwords));
    }

    public final OfflineRecognizerResult getResult(OfflineStream stream) {
        Intrinsics.checkNotNullParameter(stream, "stream");
        return getResult(stream.getPtr());
    }

    public final void decode(OfflineStream stream) {
        Intrinsics.checkNotNullParameter(stream, "stream");
        decode(this.ptr, stream.getPtr());
    }

    public final void setConfig(OfflineRecognizerConfig config) {
        Intrinsics.checkNotNullParameter(config, "config");
        setConfig(this.ptr, config);
    }

    /* JADX INFO: compiled from: OfflineRecognizer.kt */
    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0011\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0087 ¨\u0006\u0007"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineRecognizer$Companion;", "", "()V", "prependAdspLibraryPath", "", "newPath", "", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        @JvmStatic
        public final void prependAdspLibraryPath(String newPath) {
            OfflineRecognizer.prependAdspLibraryPath(newPath);
        }

        private Companion() {
        }
    }

    static {
        System.loadLibrary("sherpa-onnx-jni");
    }
}
