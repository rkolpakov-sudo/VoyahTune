package com.k2fsa.sherpa.onnx;

import android.content.res.AssetManager;
import android.util.Log;
import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Speaker.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0012\u0010\u0013\u001a\u00020\u00142\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u0016R\u001c\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001c\u0010\t\u001a\u0004\u0018\u00010\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u0011\u0010\u000f\u001a\u00020\u00048F¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u0006R\u0011\u0010\u0011\u001a\u00020\n8F¢\u0006\u0006\u001a\u0004\b\u0012\u0010\f¨\u0006\u0017"}, d2 = {"Lcom/k2fsa/sherpa/onnx/SpeakerRecognition;", "", "()V", "_extractor", "Lcom/k2fsa/sherpa/onnx/SpeakerEmbeddingExtractor;", "get_extractor", "()Lcom/k2fsa/sherpa/onnx/SpeakerEmbeddingExtractor;", "set_extractor", "(Lcom/k2fsa/sherpa/onnx/SpeakerEmbeddingExtractor;)V", "_manager", "Lcom/k2fsa/sherpa/onnx/SpeakerEmbeddingManager;", "get_manager", "()Lcom/k2fsa/sherpa/onnx/SpeakerEmbeddingManager;", "set_manager", "(Lcom/k2fsa/sherpa/onnx/SpeakerEmbeddingManager;)V", "extractor", "getExtractor", "manager", "getManager", "initExtractor", "", "assetManager", "Landroid/content/res/AssetManager;", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final class SpeakerRecognition {
    public static final SpeakerRecognition INSTANCE = new SpeakerRecognition();
    private static SpeakerEmbeddingExtractor _extractor;
    private static SpeakerEmbeddingManager _manager;

    private SpeakerRecognition() {
    }

    public final SpeakerEmbeddingExtractor get_extractor() {
        return _extractor;
    }

    public final void set_extractor(SpeakerEmbeddingExtractor speakerEmbeddingExtractor) {
        _extractor = speakerEmbeddingExtractor;
    }

    public final SpeakerEmbeddingManager get_manager() {
        return _manager;
    }

    public final void set_manager(SpeakerEmbeddingManager speakerEmbeddingManager) {
        _manager = speakerEmbeddingManager;
    }

    public final SpeakerEmbeddingExtractor getExtractor() {
        SpeakerEmbeddingExtractor speakerEmbeddingExtractor = _extractor;
        Intrinsics.checkNotNull(speakerEmbeddingExtractor);
        return speakerEmbeddingExtractor;
    }

    public final SpeakerEmbeddingManager getManager() {
        SpeakerEmbeddingManager speakerEmbeddingManager = _manager;
        Intrinsics.checkNotNull(speakerEmbeddingManager);
        return speakerEmbeddingManager;
    }

    public static /* synthetic */ void initExtractor$default(SpeakerRecognition speakerRecognition, AssetManager assetManager, int i, Object obj) {
        if ((i & 1) != 0) {
            assetManager = null;
        }
        speakerRecognition.initExtractor(assetManager);
    }

    public final void initExtractor(AssetManager assetManager) {
        synchronized (this) {
            if (_extractor != null) {
                return;
            }
            Log.i("sherpa-onnx", "Initializing speaker embedding extractor");
            _extractor = new SpeakerEmbeddingExtractor(assetManager, new SpeakerEmbeddingExtractorConfig(SpeakerKt.modelName, 2, false, "cpu"));
            SpeakerEmbeddingExtractor speakerEmbeddingExtractor = _extractor;
            Intrinsics.checkNotNull(speakerEmbeddingExtractor);
            _manager = new SpeakerEmbeddingManager(speakerEmbeddingExtractor.dim());
            Unit unit = Unit.INSTANCE;
        }
    }
}
