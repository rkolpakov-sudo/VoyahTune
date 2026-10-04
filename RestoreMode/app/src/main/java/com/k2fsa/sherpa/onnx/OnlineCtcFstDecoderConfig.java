package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OnlineRecognizer.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B\u0019\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÆ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0015\u001a\u00020\u0005HÖ\u0001J\t\u0010\u0016\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000e¨\u0006\u0017"}, d2 = {"Lcom/k2fsa/sherpa/onnx/OnlineCtcFstDecoderConfig;", "", "graph", "", "maxActive", "", "(Ljava/lang/String;I)V", "getGraph", "()Ljava/lang/String;", "setGraph", "(Ljava/lang/String;)V", "getMaxActive", "()I", "setMaxActive", "(I)V", "component1", "component2", "copy", "equals", "", "other", "hashCode", "toString", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class OnlineCtcFstDecoderConfig {
    private String graph;
    private int maxActive;

    /* JADX WARN: Multi-variable type inference failed */
    public OnlineCtcFstDecoderConfig() {
        this(null, 0, 3, null);
    }

    public static /* synthetic */ OnlineCtcFstDecoderConfig copy$default(OnlineCtcFstDecoderConfig onlineCtcFstDecoderConfig, String str, int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            str = onlineCtcFstDecoderConfig.graph;
        }
        if ((i2 & 2) != 0) {
            i = onlineCtcFstDecoderConfig.maxActive;
        }
        return onlineCtcFstDecoderConfig.copy(str, i);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String component1() {
        return this.graph;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final int component2() {
        return this.maxActive;
    }

    public final OnlineCtcFstDecoderConfig copy(String graph, int maxActive) {
        Intrinsics.checkNotNullParameter(graph, "graph");
        return new OnlineCtcFstDecoderConfig(graph, maxActive);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OnlineCtcFstDecoderConfig)) {
            return false;
        }
        OnlineCtcFstDecoderConfig onlineCtcFstDecoderConfig = (OnlineCtcFstDecoderConfig) other;
        return Intrinsics.areEqual(this.graph, onlineCtcFstDecoderConfig.graph) && this.maxActive == onlineCtcFstDecoderConfig.maxActive;
    }

    public int hashCode() {
        return (this.graph.hashCode() * 31) + Integer.hashCode(this.maxActive);
    }

    public String toString() {
        return "OnlineCtcFstDecoderConfig(graph=" + this.graph + ", maxActive=" + this.maxActive + ')';
    }

    public OnlineCtcFstDecoderConfig(String graph, int i) {
        Intrinsics.checkNotNullParameter(graph, "graph");
        this.graph = graph;
        this.maxActive = i;
    }

    public /* synthetic */ OnlineCtcFstDecoderConfig(String str, int i, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? "" : str, (i2 & 2) != 0 ? PathInterpolatorCompat.MAX_NUM_POINTS : i);
    }

    public final String getGraph() {
        return this.graph;
    }

    public final void setGraph(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.graph = str;
    }

    public final int getMaxActive() {
        return this.maxActive;
    }

    public final void setMaxActive(int i) {
        this.maxActive = i;
    }
}
