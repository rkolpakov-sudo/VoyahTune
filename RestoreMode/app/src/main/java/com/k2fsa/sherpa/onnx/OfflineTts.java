package com.k2fsa.sherpa.onnx;

import android.content.res.AssetManager;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.sun.jna.Callback;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import ru.big.town.restoremode.BuildConfig;

/* JADX INFO: compiled from: Tts.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u0014\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000b\u0018\u0000 .2\u00020\u0001:\u0001.B\u0019\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0012\u0010\r\u001a\u00020\u000e2\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003J\u0011\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\fH\u0082 J\b\u0010\u0010\u001a\u00020\u000eH\u0004J\u0006\u0010\u0011\u001a\u00020\u000eJ\"\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u00172\b\b\u0002\u0010\u0018\u001a\u00020\u0019J-\u0010\u001a\u001a\u00020\u00132\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u00172\b\b\u0002\u0010\u0018\u001a\u00020\u0019H\u0082 JE\u0010\u001b\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u00172\b\b\u0002\u0010\u0018\u001a\u00020\u00192!\u0010\u001c\u001a\u001d\u0012\u0013\u0012\u00110\u001e¢\u0006\f\b\u001f\u0012\b\b \u0012\u0004\b\b(!\u0012\u0004\u0012\u00020\u00170\u001dJP\u0010\"\u001a\u00020\u00132\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u00172\b\b\u0002\u0010\u0018\u001a\u00020\u00192!\u0010\u001c\u001a\u001d\u0012\u0013\u0012\u00110\u001e¢\u0006\f\b\u001f\u0012\b\b \u0012\u0004\b\b(!\u0012\u0004\u0012\u00020\u00170\u001dH\u0082 J\u0016\u0010#\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0004\u001a\u00020$J9\u0010%\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0004\u001a\u00020$2!\u0010\u001c\u001a\u001d\u0012\u0013\u0012\u00110\u001e¢\u0006\f\b\u001f\u0012\b\b \u0012\u0004\b\b(!\u0012\u0004\u0012\u00020\u00170\u001dJF\u0010&\u001a\u00020\u00132\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0004\u001a\u00020$2#\u0010\u001c\u001a\u001f\u0012\u0013\u0012\u00110\u001e¢\u0006\f\b\u001f\u0012\b\b \u0012\u0004\b\b(!\u0012\u0004\u0012\u00020\u0017\u0018\u00010\u001dH\u0082 J\u0011\u0010'\u001a\u00020\u00172\u0006\u0010\u000b\u001a\u00020\fH\u0082 J\u0011\u0010(\u001a\u00020\u00172\u0006\u0010\u000b\u001a\u00020\fH\u0082 J\u0019\u0010)\u001a\u00020\f2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0082 J\u0011\u0010*\u001a\u00020\f2\u0006\u0010\u0004\u001a\u00020\u0005H\u0082 J\u0006\u0010+\u001a\u00020\u0017J\u0006\u0010,\u001a\u00020\u000eJ\u0006\u0010-\u001a\u00020\u0017R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006/"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OfflineTts;", "", "assetManager", "Landroid/content/res/AssetManager;", "config", "Lcom/k2fsa/sherpa/onnx/OfflineTtsConfig;", "(Landroid/content/res/AssetManager;Lcom/k2fsa/sherpa/onnx/OfflineTtsConfig;)V", "getConfig", "()Lcom/k2fsa/sherpa/onnx/OfflineTtsConfig;", "setConfig", "(Lcom/k2fsa/sherpa/onnx/OfflineTtsConfig;)V", "ptr", "", "allocate", "", "delete", "finalize", "free", "generate", "Lcom/k2fsa/sherpa/onnx/GeneratedAudio;", "text", "", "sid", "", "speed", "", "generateImpl", "generateWithCallback", Callback.METHOD_NAME, "Lkotlin/Function1;", "", "Lkotlin/ParameterName;", "name", "samples", "generateWithCallbackImpl", "generateWithConfig", "Lcom/k2fsa/sherpa/onnx/GenerationConfig;", "generateWithConfigAndCallback", "generateWithConfigImpl", "getNumSpeakers", "getSampleRate", "newFromAsset", "newFromFile", "numSpeakers", BuildConfig.BUILD_TYPE, "sampleRate", "Companion", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final class OfflineTts {
    private OfflineTtsConfig config;
    private long ptr;

    private final native void delete(long ptr);

    private final native GeneratedAudio generateImpl(long ptr, String text, int sid, float speed);

    private final native GeneratedAudio generateWithCallbackImpl(long ptr, String text, int sid, float speed, Function1<? super float[], Integer> callback);

    private final native GeneratedAudio generateWithConfigImpl(long ptr, String text, GenerationConfig config, Function1<? super float[], Integer> callback);

    private final native int getNumSpeakers(long ptr);

    private final native int getSampleRate(long ptr);

    private final native long newFromAsset(AssetManager assetManager, OfflineTtsConfig config);

    private final native long newFromFile(OfflineTtsConfig config);

    public OfflineTts(AssetManager assetManager, OfflineTtsConfig config) {
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
            throw new IllegalArgumentException("Invalid OfflineTtsConfig: failed to create native OfflineTts".toString());
        }
    }

    public /* synthetic */ OfflineTts(AssetManager assetManager, OfflineTtsConfig offlineTtsConfig, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : assetManager, offlineTtsConfig);
    }

    public final OfflineTtsConfig getConfig() {
        return this.config;
    }

    public final void setConfig(OfflineTtsConfig offlineTtsConfig) {
        Intrinsics.checkNotNullParameter(offlineTtsConfig, "<set-?>");
        this.config = offlineTtsConfig;
    }

    public final int sampleRate() {
        return getSampleRate(this.ptr);
    }

    public final int numSpeakers() {
        return getNumSpeakers(this.ptr);
    }

    public static /* synthetic */ GeneratedAudio generate$default(OfflineTts offlineTts, String str, int i, float f, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = 0;
        }
        if ((i2 & 4) != 0) {
            f = 1.0f;
        }
        return offlineTts.generate(str, i, f);
    }

    public final GeneratedAudio generate(String text, int sid, float speed) {
        Intrinsics.checkNotNullParameter(text, "text");
        return generateImpl(this.ptr, text, sid, speed);
    }

    public static /* synthetic */ GeneratedAudio generateWithCallback$default(OfflineTts offlineTts, String str, int i, float f, Function1 function1, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = 0;
        }
        if ((i2 & 4) != 0) {
            f = 1.0f;
        }
        return offlineTts.generateWithCallback(str, i, f, function1);
    }

    public final GeneratedAudio generateWithCallback(String text, int sid, float speed, Function1<? super float[], Integer> callback) {
        Intrinsics.checkNotNullParameter(text, "text");
        Intrinsics.checkNotNullParameter(callback, "callback");
        return generateWithCallbackImpl(this.ptr, text, sid, speed, callback);
    }

    public final GeneratedAudio generateWithConfig(String text, GenerationConfig config) {
        Intrinsics.checkNotNullParameter(text, "text");
        Intrinsics.checkNotNullParameter(config, "config");
        return generateWithConfigImpl(this.ptr, text, config, null);
    }

    public final GeneratedAudio generateWithConfigAndCallback(String text, GenerationConfig config, Function1<? super float[], Integer> callback) {
        Intrinsics.checkNotNullParameter(text, "text");
        Intrinsics.checkNotNullParameter(config, "config");
        Intrinsics.checkNotNullParameter(callback, "callback");
        return generateWithConfigImpl(this.ptr, text, config, callback);
    }

    public static /* synthetic */ void allocate$default(OfflineTts offlineTts, AssetManager assetManager, int i, Object obj) {
        if ((i & 1) != 0) {
            assetManager = null;
        }
        offlineTts.allocate(assetManager);
    }

    public final void allocate(AssetManager assetManager) {
        long jNewFromFile;
        if (this.ptr == 0) {
            if (assetManager != null) {
                jNewFromFile = newFromAsset(assetManager, this.config);
            } else {
                jNewFromFile = newFromFile(this.config);
            }
            this.ptr = jNewFromFile;
            if (jNewFromFile == 0) {
                throw new IllegalArgumentException("Invalid OfflineTtsConfig: failed to create native OfflineTts".toString());
            }
        }
    }

    public final void free() {
        long j = this.ptr;
        if (j != 0) {
            delete(j);
            this.ptr = 0L;
        }
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

    static /* synthetic */ GeneratedAudio generateImpl$default(OfflineTts offlineTts, long j, String str, int i, float f, int i2, Object obj) {
        if ((i2 & 4) != 0) {
            i = 0;
        }
        int i3 = i;
        if ((i2 & 8) != 0) {
            f = 1.0f;
        }
        return offlineTts.generateImpl(j, str, i3, f);
    }

    static /* synthetic */ GeneratedAudio generateWithCallbackImpl$default(OfflineTts offlineTts, long j, String str, int i, float f, Function1 function1, int i2, Object obj) {
        if ((i2 & 4) != 0) {
            i = 0;
        }
        int i3 = i;
        if ((i2 & 8) != 0) {
            f = 1.0f;
        }
        return offlineTts.generateWithCallbackImpl(j, str, i3, f, function1);
    }

    static {
        System.loadLibrary("sherpa-onnx-jni");
    }
}
