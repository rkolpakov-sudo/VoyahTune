package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: OnlineRecognizer.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B#\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003¢\u0006\u0002\u0010\u0006J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J'\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0016\u001a\u00020\u0017HÖ\u0001J\t\u0010\u0018\u001a\u00020\u0019HÖ\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\b\"\u0004\b\f\u0010\nR\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\b\"\u0004\b\u000e\u0010\n¨\u0006\u001a"}, d2 = {"Lcom/k2fsa/sherpa/onnx/EndpointConfig;", "", "rule1", "Lcom/k2fsa/sherpa/onnx/EndpointRule;", "rule2", "rule3", "(Lcom/k2fsa/sherpa/onnx/EndpointRule;Lcom/k2fsa/sherpa/onnx/EndpointRule;Lcom/k2fsa/sherpa/onnx/EndpointRule;)V", "getRule1", "()Lcom/k2fsa/sherpa/onnx/EndpointRule;", "setRule1", "(Lcom/k2fsa/sherpa/onnx/EndpointRule;)V", "getRule2", "setRule2", "getRule3", "setRule3", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "", "sherpa_onnx_release"}, k = 1, mv = {1, 7, 1}, xi = 48)
public final /* data */ class EndpointConfig {
    private EndpointRule rule1;
    private EndpointRule rule2;
    private EndpointRule rule3;

    public EndpointConfig() {
        this(null, null, null, 7, null);
    }

    public static /* synthetic */ EndpointConfig copy$default(EndpointConfig endpointConfig, EndpointRule endpointRule, EndpointRule endpointRule2, EndpointRule endpointRule3, int i, Object obj) {
        if ((i & 1) != 0) {
            endpointRule = endpointConfig.rule1;
        }
        if ((i & 2) != 0) {
            endpointRule2 = endpointConfig.rule2;
        }
        if ((i & 4) != 0) {
            endpointRule3 = endpointConfig.rule3;
        }
        return endpointConfig.copy(endpointRule, endpointRule2, endpointRule3);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final EndpointRule component1() {
        return this.rule1;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final EndpointRule component2() {
        return this.rule2;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final EndpointRule component3() {
        return this.rule3;
    }

    public final EndpointConfig copy(EndpointRule rule1, EndpointRule rule2, EndpointRule rule3) {
        Intrinsics.checkNotNullParameter(rule1, "rule1");
        Intrinsics.checkNotNullParameter(rule2, "rule2");
        Intrinsics.checkNotNullParameter(rule3, "rule3");
        return new EndpointConfig(rule1, rule2, rule3);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EndpointConfig)) {
            return false;
        }
        EndpointConfig endpointConfig = (EndpointConfig) other;
        return Intrinsics.areEqual(this.rule1, endpointConfig.rule1) && Intrinsics.areEqual(this.rule2, endpointConfig.rule2) && Intrinsics.areEqual(this.rule3, endpointConfig.rule3);
    }

    public int hashCode() {
        return (((this.rule1.hashCode() * 31) + this.rule2.hashCode()) * 31) + this.rule3.hashCode();
    }

    public String toString() {
        return "EndpointConfig(rule1=" + this.rule1 + ", rule2=" + this.rule2 + ", rule3=" + this.rule3 + ')';
    }

    public EndpointConfig(EndpointRule rule1, EndpointRule rule2, EndpointRule rule3) {
        Intrinsics.checkNotNullParameter(rule1, "rule1");
        Intrinsics.checkNotNullParameter(rule2, "rule2");
        Intrinsics.checkNotNullParameter(rule3, "rule3");
        this.rule1 = rule1;
        this.rule2 = rule2;
        this.rule3 = rule3;
    }

    public /* synthetic */ EndpointConfig(EndpointRule endpointRule, EndpointRule endpointRule2, EndpointRule endpointRule3, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? new EndpointRule(false, 2.4f, 0.0f) : endpointRule, (i & 2) != 0 ? new EndpointRule(true, 1.4f, 0.0f) : endpointRule2, (i & 4) != 0 ? new EndpointRule(false, 0.0f, 20.0f) : endpointRule3);
    }

    public final EndpointRule getRule1() {
        return this.rule1;
    }

    public final void setRule1(EndpointRule endpointRule) {
        Intrinsics.checkNotNullParameter(endpointRule, "<set-?>");
        this.rule1 = endpointRule;
    }

    public final EndpointRule getRule2() {
        return this.rule2;
    }

    public final void setRule2(EndpointRule endpointRule) {
        Intrinsics.checkNotNullParameter(endpointRule, "<set-?>");
        this.rule2 = endpointRule;
    }

    public final EndpointRule getRule3() {
        return this.rule3;
    }

    public final void setRule3(EndpointRule endpointRule) {
        Intrinsics.checkNotNullParameter(endpointRule, "<set-?>");
        this.rule3 = endpointRule;
    }
}
