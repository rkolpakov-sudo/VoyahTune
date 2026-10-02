package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import ru.big.town.restoremode.BuildConfig;

/* JADX INFO: compiled from: Speaker.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0014\n\u0002\u0010\u0011\n\u0002\b\t\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010\u0007\n\u0002\b\u0003\u0018\u0000 \"2\u00020\u0001:\u0001\"B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J!\u0010\t\u001a\u00020\n2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0082 J!\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u000f¢\u0006\u0002\u0010\u0010J\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ,\u0010\u0011\u001a\u00020\n2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u000fH\u0082 ¢\u0006\u0002\u0010\u0012J\u0011\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\f0\u000f¢\u0006\u0002\u0010\u0014J\u001c\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\f0\u000f2\u0006\u0010\u0007\u001a\u00020\bH\u0082 ¢\u0006\u0002\u0010\u0015J\u0019\u0010\u0016\u001a\u00020\n2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\fH\u0082 J\u000e\u0010\u0016\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fJ\u0011\u0010\u0017\u001a\u00020\b2\u0006\u0010\u0002\u001a\u00020\u0003H\u0082 J\u0011\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0007\u001a\u00020\bH\u0082 J\b\u0010\u001a\u001a\u00020\u0019H\u0004J\u0006\u0010\u001b\u001a\u00020\u0003J\u0011\u0010\u001b\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\bH\u0082 J\u0006\u0010\u001c\u001a\u00020\u0019J\u0019\u0010\u001d\u001a\u00020\n2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\fH\u0082 J\u000e\u0010\u001d\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fJ\u0016\u0010\u001e\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u001f\u001a\u00020 J!\u0010\u001e\u001a\u00020\f2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u001f\u001a\u00020 H\u0082 J)\u0010!\u001a\u00020\n2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u001f\u001a\u00020 H\u0082 J\u001e\u0010!\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u001f\u001a\u00020 R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006#"}, d2 = {"Lcom/k2fsa/sherpa/onnx/SpeakerEmbeddingManager;", "", "dim", "", "(I)V", "getDim", "()I", "ptr", "", "add", "", "name", "", "embedding", "", "", "(Ljava/lang/String;[[F)Z", "addList", "(JLjava/lang/String;[[F)Z", "allSpeakerNames", "()[Ljava/lang/String;", "(J)[Ljava/lang/String;", "contains", "create", "delete", "", "finalize", "numSpeakers", BuildConfig.BUILD_TYPE, "remove", "search", "threshold", "", "verify", "Companion", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final class SpeakerEmbeddingManager {
    private final int dim;
    private long ptr;

    private final native boolean add(long ptr, String name, float[] embedding);

    private final native boolean addList(long ptr, String name, float[][] embedding);

    private final native String[] allSpeakerNames(long ptr);

    private final native boolean contains(long ptr, String name);

    private final native long create(int dim);

    private final native void delete(long ptr);

    private final native int numSpeakers(long ptr);

    private final native boolean remove(long ptr, String name);

    private final native String search(long ptr, float[] embedding, float threshold);

    private final native boolean verify(long ptr, String name, float[] embedding, float threshold);

    public SpeakerEmbeddingManager(int i) {
        this.dim = i;
        long jCreate = create(i);
        this.ptr = jCreate;
        if (jCreate == 0) {
            throw new IllegalArgumentException("Failed to create native SpeakerEmbeddingManager".toString());
        }
    }

    public final int getDim() {
        return this.dim;
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

    public final boolean add(String name, float[] embedding) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(embedding, "embedding");
        return add(this.ptr, name, embedding);
    }

    public final boolean add(String name, float[][] embedding) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(embedding, "embedding");
        return addList(this.ptr, name, embedding);
    }

    public final boolean remove(String name) {
        Intrinsics.checkNotNullParameter(name, "name");
        return remove(this.ptr, name);
    }

    public final String search(float[] embedding, float threshold) {
        Intrinsics.checkNotNullParameter(embedding, "embedding");
        return search(this.ptr, embedding, threshold);
    }

    public final boolean verify(String name, float[] embedding, float threshold) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(embedding, "embedding");
        return verify(this.ptr, name, embedding, threshold);
    }

    public final boolean contains(String name) {
        Intrinsics.checkNotNullParameter(name, "name");
        return contains(this.ptr, name);
    }

    public final int numSpeakers() {
        return numSpeakers(this.ptr);
    }

    public final String[] allSpeakerNames() {
        return allSpeakerNames(this.ptr);
    }

    static {
        System.loadLibrary("sherpa-onnx-jni");
    }
}
