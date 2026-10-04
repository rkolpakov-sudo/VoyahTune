package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;

/* JADX INFO: compiled from: FeatureConfig.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\u0010\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\u001a\u0016\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0003¨\u0006\u0005"}, d2 = {"getFeatureConfig", "Lcom/k2fsa/sherpa/onnx/FeatureConfig;", "sampleRate", "", "featureDim", "sherpa_onnx_release"}, k = 2, mv = {1, 7, 1}, xi = 48)
public final class FeatureConfigKt {
    public static final FeatureConfig getFeatureConfig(int i, int i2) {
        return new FeatureConfig(i, i2, 0.0f, 4, null);
    }
}
