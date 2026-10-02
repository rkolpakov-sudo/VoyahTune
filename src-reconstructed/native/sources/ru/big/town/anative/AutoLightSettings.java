package ru.big.town.anative;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

/* JADX INFO: loaded from: classes2.dex */
final class AutoLightSettings {

    interface Runtime {
        void apply(boolean z);

        void cache(boolean z);

        boolean cached();

        Boolean saved();
    }

    AutoLightSettings() {
    }

    static synchronized void restore(Runtime runtime) {
        Boolean boolSaved;
        try {
            boolSaved = runtime.saved();
        } catch (RuntimeException unused) {
            boolSaved = null;
        }
        boolean zBooleanValue = boolSaved != null ? boolSaved.booleanValue() : runtime.cached();
        runtime.cache(zBooleanValue);
        runtime.apply(zBooleanValue);
    }

    static void restore(Context context) {
        restore(runtime(context));
    }

    static synchronized void set(Context context, boolean z) {
        MainActivity.persistSavedToggle(context, "autoLight", z);
        Runtime runtime = runtime(context);
        runtime.cache(z);
        runtime.apply(z);
    }

    private static Runtime runtime(final Context context) {
        return new Runtime() { // from class: ru.big.town.anative.AutoLightSettings.1
            @Override // ru.big.town.anative.AutoLightSettings.Runtime
            public Boolean saved() {
                Cursor cursorQuery = context.getContentResolver().query(Uri.parse("content://ru.big.town.restoremode.restoremodecontentprovider/"), null, null, null, null);
                Boolean bool = null;
                if (cursorQuery != null) {
                    try {
                        if (cursorQuery.moveToFirst()) {
                            int columnIndex = cursorQuery.getColumnIndex("autoLight");
                            if (columnIndex >= 0 && !cursorQuery.isNull(columnIndex)) {
                                int i = cursorQuery.getInt(columnIndex);
                                if (i == 0) {
                                    bool = Boolean.FALSE;
                                } else if (i == 1) {
                                    bool = Boolean.TRUE;
                                }
                                if (cursorQuery != null) {
                                    cursorQuery.close();
                                }
                                return bool;
                            }
                            if (cursorQuery != null) {
                                cursorQuery.close();
                            }
                            return null;
                        }
                    } catch (Throwable th) {
                        if (cursorQuery == null) {
                            throw th;
                        }
                        try {
                            cursorQuery.close();
                            throw th;
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                            throw th;
                        }
                    }
                }
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                return null;
            }

            @Override // ru.big.town.anative.AutoLightSettings.Runtime
            public boolean cached() {
                return context.getSharedPreferences("NativePrefs", 0).getBoolean("autoLight", false);
            }

            @Override // ru.big.town.anative.AutoLightSettings.Runtime
            public void cache(boolean z) {
                context.getSharedPreferences("NativePrefs", 0).edit().putBoolean("autoLight", z).apply();
            }

            @Override // ru.big.town.anative.AutoLightSettings.Runtime
            public void apply(boolean z) {
                Intent intent = new Intent(context, (Class<?>) LightSensorService.class);
                if (z) {
                    if (context.startForegroundService(intent) == null) {
                        throw new IllegalStateException("Auto light service start was not accepted");
                    }
                } else {
                    context.stopService(intent);
                }
            }
        };
    }
}
