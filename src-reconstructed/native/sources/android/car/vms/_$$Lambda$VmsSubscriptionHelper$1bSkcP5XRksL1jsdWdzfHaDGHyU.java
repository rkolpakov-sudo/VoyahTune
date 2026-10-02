package android.car.vms;

import java.util.Map;
import java.util.function.Function;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class _$$Lambda$VmsSubscriptionHelper$1bSkcP5XRksL1jsdWdzfHaDGHyU implements Function {
    public static final -$.Lambda.VmsSubscriptionHelper.1bSkcP5XRksL1jsdWdzfHaDGHyU INSTANCE = new _$$Lambda$VmsSubscriptionHelper$1bSkcP5XRksL1jsdWdzfHaDGHyU();

    private /* synthetic */ _$$Lambda$VmsSubscriptionHelper$1bSkcP5XRksL1jsdWdzfHaDGHyU() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return VmsSubscriptionHelper.toAssociatedLayer((Map.Entry) obj);
    }
}
