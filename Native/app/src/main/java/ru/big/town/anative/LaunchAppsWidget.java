package ru.big.town.anative;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

/* JADX INFO: loaded from: classes2.dex */
public class LaunchAppsWidget extends AppWidgetProvider {
    @Override // android.appwidget.AppWidgetProvider
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] iArr) {
        for (int i : iArr) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_launch_apps);
            int i2 = 0;
            for (WidgetSupport.LaunchableApp launchableApp : WidgetSupport.launchableApps(context)) {
                RemoteViews remoteViews2 = new RemoteViews(context.getPackageName(), R.layout.widget_launch_app_item);
                remoteViews2.setImageViewBitmap(R.id.launch_app_icon, WidgetSupport.iconBitmap(launchableApp.icon, 64));
                remoteViews2.setTextViewText(R.id.launch_app_label, launchableApp.label);
                int i3 = (i * 1000) + i2;
                PendingIntent pendingIntentAppPendingIntent = WidgetSupport.appPendingIntent(context, "ru.big.town.anative.WIDGET_OPEN_APP", launchableApp.packageName, i3);
                remoteViews2.setOnClickPendingIntent(R.id.widget_btn_open, pendingIntentAppPendingIntent);
                remoteViews2.setOnClickPendingIntent(R.id.widget_btn_fullscreen, WidgetSupport.appPendingIntent(context, "ru.big.town.anative.WIDGET_FULLSCREEN_LAUNCH", launchableApp.packageName, i3 + 1));
                remoteViews2.setOnClickPendingIntent(R.id.widget_btn_close, WidgetSupport.appPendingIntent(context, "ru.big.town.anative.WIDGET_CLOSE_APP", launchableApp.packageName, i3 + 2));
                remoteViews2.setOnClickPendingIntent(R.id.launch_app_item, pendingIntentAppPendingIntent);
                remoteViews.addView(R.id.launch_apps_list, remoteViews2);
                i2++;
            }
            appWidgetManager.updateAppWidget(i, remoteViews);
        }
    }
}
