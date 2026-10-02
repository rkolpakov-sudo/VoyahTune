package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;

/* JADX INFO: compiled from: Vad.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\u001a\u0010\u0010\u0000\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u0002\u001a\u00020\u0003¨\u0006\u0004"}, d2 = {"getVadModelConfig", "Lcom/k2fsa/sherpa/onnx/VadModelConfig;", "type", "", "sherpa_onnx_release"}, k = 2, mv = {1, 7, 1}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final class VadKt {
    public static final VadModelConfig getVadModelConfig(int i) {
        if (i == 0) {
            return new VadModelConfig(new SileroVadModelConfig("silero_vad.onnx", 0.5f, 0.25f, 0.25f, 512, 0.0f, 32, null), null, 16000, 1, "cpu", false, 34, null);
        }
        if (i != 1) {
            return null;
        }
        return new VadModelConfig(null, new TenVadModelConfig("ten-vad.onnx", 0.5f, 0.25f, 0.25f, 256, 0.0f, 32, null), 16000, 1, "cpu", false, 33, null);
    }
}
