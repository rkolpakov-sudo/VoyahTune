package android.car.cluster.renderer;

import android.annotation.SystemApi;
import android.car.navigation.CarNavigationInstrumentCluster;
import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public abstract class NavigationRenderer {
    public abstract CarNavigationInstrumentCluster getNavigationProperties();

    public void onEvent(int i, Bundle bundle) {
    }

    public void onNavigationStateChanged(Bundle bundle) {
    }
}
