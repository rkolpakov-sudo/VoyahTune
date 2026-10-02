package ru.big.town.anative;

import android.car.Car;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/* JADX INFO: loaded from: classes2.dex */
public class WidgetActionReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String stringExtra = intent.getStringExtra(Car.PACKAGE_SERVICE);
        if ("ru.big.town.anative.WIDGET_OPEN_APP".equals(action)) {
            WidgetSupport.saveLastManualApp(context, stringExtra);
            WidgetSupport.openApp(context, stringExtra);
            return;
        }
        if ("ru.big.town.anative.WIDGET_SIMPLE_LAUNCH".equals(action)) {
            WidgetSupport.saveLastManualApp(context, stringExtra);
            WidgetSupport.simpleLaunch(context, stringExtra);
            return;
        }
        if ("ru.big.town.anative.WIDGET_FULLSCREEN_LAUNCH".equals(action)) {
            WidgetSupport.saveLastManualApp(context, stringExtra);
            WidgetSupport.fullscreenLaunch(context, stringExtra);
        } else if ("ru.big.town.anative.WIDGET_CLOSE_APP".equals(action)) {
            WidgetSupport.clearLastManualApp(context);
            WidgetSupport.stopApp(context, stringExtra);
        } else if ("ru.big.town.anative.WIDGET_CLOSE_ALL".equals(action)) {
            WidgetSupport.stopAllApps(context);
        }
    }
}
