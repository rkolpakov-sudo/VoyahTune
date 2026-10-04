package com.k2fsa.sherpa.onnx;

import android.content.res.AssetManager;
import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import ru.big.town.restoremode.BuildConfig;

/* JADX INFO: compiled from: OfflinePunctuation.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0006\u0018\u0000 \u00122\u00020\u0001:\u0001\u0012B\u0019\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0019\u0010\t\u001a\u00020\n2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\nH\u0082 J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\nJ\u0011\u0010\f\u001a\u00020\r2\u0006\u0010\u0007\u001a\u00020\bH\u0082 J\b\u0010\u000e\u001a\u00020\rH\u0004J\u0019\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0082 J\u0011\u0010\u0010\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u0005H\u0082 J\u0006\u0010\u0011\u001a\u00020\rR\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0013"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflinePunctuation;", "", "assetManager", "Landroid/content/res/AssetManager;", "config", "Lcom/k2fsa/sherpa/onnx/OfflinePunctuationConfig;", "(Landroid/content/res/AssetManager;Lcom/k2fsa/sherpa/onnx/OfflinePunctuationConfig;)V", "ptr", "", "addPunctuation", "", "text", "delete", "", "finalize", "newFromAsset", "newFromFile", BuildConfig.BUILD_TYPE, "Companion", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final class OfflinePunctuation {
    private long ptr;

    private final native String addPunctuation(long ptr, String text);

    private final native void delete(long ptr);

    private final native long newFromAsset(AssetManager assetManager, OfflinePunctuationConfig config);

    private final native long newFromFile(OfflinePunctuationConfig config);

    public OfflinePunctuation(AssetManager assetManager, OfflinePunctuationConfig config) {
        long jNewFromFile;
        Intrinsics.checkNotNullParameter(config, "config");
        if (assetManager != null) {
            jNewFromFile = newFromAsset(assetManager, config);
        } else {
            jNewFromFile = newFromFile(config);
        }
        this.ptr = jNewFromFile;
        if (jNewFromFile == 0) {
            throw new IllegalArgumentException("Invalid OfflinePunctuationConfig: failed to create native OfflinePunctuation".toString());
        }
    }

    public /* synthetic */ OfflinePunctuation(AssetManager assetManager, OfflinePunctuationConfig offlinePunctuationConfig, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : assetManager, offlinePunctuationConfig);
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

    public final String addPunctuation(String text) {
        Intrinsics.checkNotNullParameter(text, "text");
        return addPunctuation(this.ptr, text);
    }

    static {
        System.loadLibrary("sherpa-onnx-jni");
    }
}
