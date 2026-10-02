package ru.big.town.anative;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import ru.big.town.common.DriveSelectionPolicy;

/* JADX INFO: loaded from: classes2.dex */
final class DriveSelectionStore {
    private static final Uri URI = Uri.parse("content://ru.big.town.restoremode.restoremodecontentprovider/");

    DriveSelectionStore() {
    }

    static void applyConfigured(Context context) {
        try {
            context.getContentResolver().call(URI, "driveHookV2", "manual", (Bundle) null);
        } catch (RuntimeException e) {
            Log.w("DriveSelection", "Configured selection unavailable", e);
        }
    }

    static DriveSelectionPolicy read(Context context) {
        try {
            Cursor cursorQuery = context.getContentResolver().query(URI, null, null, null, null);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToFirst() && cursorQuery.getColumnIndex(DriveSelectionPolicy.CONFIGURED) >= 0) {
                        DriveSelectionPolicy driveSelectionPolicy = new DriveSelectionPolicy(cursorQuery.getString(cursorQuery.getColumnIndex(DriveSelectionPolicy.CONFIGURED)), cursorQuery.getString(cursorQuery.getColumnIndex(DriveSelectionPolicy.OVERRIDE)), cursorQuery.getString(cursorQuery.getColumnIndex(DriveSelectionPolicy.MEDIUM)), cursorQuery.getColumnIndex(DriveSelectionPolicy.CURRENT) < 0 ? "" : cursorQuery.getString(cursorQuery.getColumnIndex(DriveSelectionPolicy.CURRENT)));
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                        return driveSelectionPolicy;
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
            if (cursorQuery == null) {
                return null;
            }
            cursorQuery.close();
            return null;
        } catch (RuntimeException e) {
            Log.w("DriveSelection", "Read failed", e);
            return null;
        }
    }

    static boolean record(Context context, String str, String str2) {
        DriveSelectionPolicy driveSelectionPolicy;
        if (context != null && DriveSelectionPolicy.valid(str)) {
            try {
                if (read(context) == null) {
                    return false;
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put(DriveSelectionPolicy.SOURCE, str2);
                contentValues.put(DriveSelectionPolicy.MODE, str);
                if ((context.getContentResolver().update(URI, contentValues, null, null) == 0 && !DriveSelectionPolicy.FEEDBACK.equals(str2)) || (driveSelectionPolicy = read(context)) == null) {
                    return false;
                }
                MainActivity.driveMode = driveSelectionPolicy.effective();
                ApplyEngine.noteSavedMode("driveMode", driveSelectionPolicy.effective());
                context.getSharedPreferences("NativePrefs", 0).edit().putString("cacheDriveMode", driveSelectionPolicy.effective()).apply();
                if (DriveSelectionPolicy.WIDGET.equals(str2) || DriveSelectionPolicy.EXPLICIT.equals(str2)) {
                    ApplyEngine.driveSelectionSaved();
                }
                Intent intent = new Intent("ru.big.town.anative.MODE_SYNCED");
                intent.setPackage("ru.big.town.restoremode");
                intent.putExtra("modeKey", "driveMode");
                intent.putExtra("mode", driveSelectionPolicy.configured);
                context.sendBroadcast(intent);
                Log.i("DriveSelection", str2 + " mode=" + str + " restore=" + driveSelectionPolicy.effective() + " widget=" + driveSelectionPolicy.override + " medium=" + driveSelectionPolicy.medium);
                return true;
            } catch (RuntimeException e) {
                Log.w("DriveSelection", "Save failed", e);
            }
        }
        return false;
    }
}
