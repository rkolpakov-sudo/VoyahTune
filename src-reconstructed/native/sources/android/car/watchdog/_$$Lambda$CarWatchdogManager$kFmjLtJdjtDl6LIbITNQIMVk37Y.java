package android.car.watchdog;

import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class _$$Lambda$CarWatchdogManager$kFmjLtJdjtDl6LIbITNQIMVk37Y implements Consumer {
    public static final -$.Lambda.CarWatchdogManager.kFmjLtJdjtDl6LIbITNQIMVk37Y INSTANCE = new _$$Lambda$CarWatchdogManager$kFmjLtJdjtDl6LIbITNQIMVk37Y();

    private /* synthetic */ _$$Lambda$CarWatchdogManager$kFmjLtJdjtDl6LIbITNQIMVk37Y() {
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        ((CarWatchdogManager) obj).checkMainThread();
    }
}
