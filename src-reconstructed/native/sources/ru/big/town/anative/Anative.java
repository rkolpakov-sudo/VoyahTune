package ru.big.town.anative;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Process;
import java.io.File;

/* JADX INFO: loaded from: classes2.dex */
public class Anative extends Application {
    @Override // android.content.ContextWrapper
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        if (new File("/data/local/bin/voyahtune-update.block").exists()) {
            Process.killProcess(Process.myPid());
            throw new IllegalStateException("VoyahTune update requires USB repair");
        }
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        GlobalVars.SAVE_CONTEXT = getBaseContext();
    }

    @Override // android.app.Application, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    @Override // android.app.Application, android.content.ComponentCallbacks
    public void onLowMemory() {
        super.onLowMemory();
    }
}
