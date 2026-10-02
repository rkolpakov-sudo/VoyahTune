package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import ru.big.town.restoremode.BuildConfig;

/* JADX INFO: compiled from: OnlineStream.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0014\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u001a2\u00020\u0001:\u0001\u001aB\u000f\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0016\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rJ!\u0010\b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0082 J\u0011\u0010\u000e\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u0003H\u0082 J\b\u0010\u000f\u001a\u00020\tH\u0004J\u0019\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0012\u001a\u00020\u0011H\u0082 J\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0011J\u0006\u0010\u0013\u001a\u00020\tJ\u0011\u0010\u0013\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u0003H\u0082 J\u0006\u0010\u0014\u001a\u00020\tJ!\u0010\u0015\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0016\u001a\u00020\u0011H\u0082 J\u0016\u0010\u0015\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0016\u001a\u00020\u0011J\u001a\u0010\u0017\u001a\u00020\t2\u0012\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u0000\u0012\u0004\u0012\u00020\t0\u0019R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\u0004¨\u0006\u001b"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OnlineStream;", "", "ptr", "", "(J)V", "getPtr", "()J", "setPtr", "acceptWaveform", "", "samples", "", "sampleRate", "", "delete", "finalize", "getOption", "", "key", "inputFinished", BuildConfig.BUILD_TYPE, "setOption", "value", "use", "block", "Lkotlin/Function1;", "Companion", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final class OnlineStream {
    private long ptr;

    public OnlineStream() {
        this(0L, 1, null);
    }

    private final native void acceptWaveform(long ptr, float[] samples, int sampleRate);

    private final native void delete(long ptr);

    private final native String getOption(long ptr, String key);

    private final native void inputFinished(long ptr);

    private final native void setOption(long ptr, String key, String value);

    public OnlineStream(long j) {
        this.ptr = j;
        if (j == 0) {
            throw new IllegalArgumentException("Failed to create native OnlineStream".toString());
        }
    }

    public /* synthetic */ OnlineStream(long j, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? 0L : j);
    }

    public final long getPtr() {
        return this.ptr;
    }

    public final void setPtr(long j) {
        this.ptr = j;
    }

    public final void acceptWaveform(float[] samples, int sampleRate) {
        Intrinsics.checkNotNullParameter(samples, "samples");
        acceptWaveform(this.ptr, samples, sampleRate);
    }

    public final void inputFinished() {
        inputFinished(this.ptr);
    }

    public final void setOption(String key, String value) {
        Intrinsics.checkNotNullParameter(key, "key");
        Intrinsics.checkNotNullParameter(value, "value");
        setOption(this.ptr, key, value);
    }

    public final String getOption(String key) {
        Intrinsics.checkNotNullParameter(key, "key");
        return getOption(this.ptr, key);
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

    public final void use(Function1<? super OnlineStream, Unit> block) {
        Intrinsics.checkNotNullParameter(block, "block");
        try {
            block.invoke(this);
        } finally {
            release();
        }
    }

    static {
        System.loadLibrary("sherpa-onnx-jni");
    }
}
