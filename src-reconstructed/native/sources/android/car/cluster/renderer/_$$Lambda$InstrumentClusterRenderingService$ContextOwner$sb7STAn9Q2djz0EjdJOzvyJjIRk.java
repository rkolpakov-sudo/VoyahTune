package android.car.cluster.renderer;

import android.content.pm.ProviderInfo;
import java.util.function.Function;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class _$$Lambda$InstrumentClusterRenderingService$ContextOwner$sb7STAn9Q2djz0EjdJOzvyJjIRk implements Function {
    public static final -$.Lambda.InstrumentClusterRenderingService.ContextOwner.sb7STAn9Q2djz0EjdJOzvyJjIRk INSTANCE = new _$$Lambda$InstrumentClusterRenderingService$ContextOwner$sb7STAn9Q2djz0EjdJOzvyJjIRk();

    private /* synthetic */ _$$Lambda$InstrumentClusterRenderingService$ContextOwner$sb7STAn9Q2djz0EjdJOzvyJjIRk() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return ((ProviderInfo) obj).authority;
    }
}
