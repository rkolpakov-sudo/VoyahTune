package ru.big.town.restoremode;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import java.io.InputStream;

/* JADX INFO: loaded from: classes2.dex */
public final class NowPlayingClient {
    public static final String ACTION_NOW_PLAYING = "ru.big.town.anative.NOW_PLAYING";
    public static final String ACTION_REQUEST_NOW_PLAYING = "ru.big.town.anative.REQUEST_NOW_PLAYING";
    private static final String AUTHORITY = "ru.big.town.anative.nowplaying";
    public static final Uri CONTENT_URI = Uri.parse("content://ru.big.town.anative.nowplaying");
    public static final Uri ART_URI = Uri.parse("content://ru.big.town.anative.nowplaying/art");

    private NowPlayingClient() {
    }

    public static final class NowPlaying {
        public String title = "";
        public String artist = "";
        public String album = "";
        public String packageName = "";
        public String appLabel = "";
        public int state = 0;
        public long position = 0;
        public long duration = 0;
        public boolean hasArt = false;
        public long updatedAt = 0;

        public boolean isPlaying() {
            return this.state == 3;
        }

        public boolean isEmpty() {
            String str = this.title;
            return str == null || str.isEmpty();
        }
    }

    public static NowPlaying query(Context context) {
        NowPlaying nowPlaying = new NowPlaying();
        Cursor cursorQuery = null;
        try {
            cursorQuery = context.getContentResolver().query(CONTENT_URI, null, null, null, null);
            if (cursorQuery != null && cursorQuery.moveToFirst()) {
                nowPlaying.title = str(cursorQuery, "title");
                nowPlaying.artist = str(cursorQuery, "artist");
                nowPlaying.album = str(cursorQuery, "album");
                nowPlaying.packageName = str(cursorQuery, "package");
                nowPlaying.appLabel = str(cursorQuery, "appLabel");
                nowPlaying.state = intOf(cursorQuery, "state", 0);
                nowPlaying.position = longOf(cursorQuery, "position");
                nowPlaying.duration = longOf(cursorQuery, TypedValues.TransitionType.S_DURATION);
                nowPlaying.hasArt = intOf(cursorQuery, "hasArt", 0) == 1;
                nowPlaying.updatedAt = longOf(cursorQuery, "updatedAt");
            }
            if (cursorQuery != null) {
                return nowPlaying;
            }
        } catch (Exception unused) {
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
        return nowPlaying;
    }

    public static NowPlaying fromBroadcast(Intent intent) {
        NowPlaying nowPlaying = new NowPlaying();
        if (intent == null) {
            return nowPlaying;
        }
        nowPlaying.title = nz(intent.getStringExtra("title"));
        nowPlaying.artist = nz(intent.getStringExtra("artist"));
        nowPlaying.album = nz(intent.getStringExtra("album"));
        nowPlaying.packageName = nz(intent.getStringExtra("package"));
        nowPlaying.appLabel = nz(intent.getStringExtra("appLabel"));
        nowPlaying.state = intent.getIntExtra("state", 0);
        nowPlaying.position = intent.getLongExtra("position", 0L);
        nowPlaying.duration = intent.getLongExtra(TypedValues.TransitionType.S_DURATION, 0L);
        nowPlaying.hasArt = intent.getBooleanExtra("hasArt", false);
        nowPlaying.updatedAt = intent.getLongExtra("updatedAt", 0L);
        return nowPlaying;
    }

    public static Bitmap loadArt(Context context) throws Throwable {
        Throwable th;
        InputStream inputStreamOpenInputStream;
        Bitmap bitmapDecodeStream = null;
        try {
            inputStreamOpenInputStream = context.getContentResolver().openInputStream(ART_URI);
            if (inputStreamOpenInputStream != null) {
                try {
                    bitmapDecodeStream = BitmapFactory.decodeStream(inputStreamOpenInputStream);
                } catch (Exception unused) {
                    if (inputStreamOpenInputStream != null) {
                        try {
                            inputStreamOpenInputStream.close();
                        } catch (Exception unused2) {
                        }
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    if (inputStreamOpenInputStream != null) {
                        try {
                            inputStreamOpenInputStream.close();
                        } catch (Exception unused3) {
                        }
                    }
                    throw th;
                }
            }
            if (inputStreamOpenInputStream != null) {
                try {
                    inputStreamOpenInputStream.close();
                } catch (Exception unused4) {
                }
            }
            return bitmapDecodeStream;
        } catch (Exception unused5) {
            inputStreamOpenInputStream = null;
        } catch (Throwable th3) {
            th = th3;
            inputStreamOpenInputStream = null;
        }
    }

    public static void requestRefresh(Context context) {
        try {
            Intent intent = new Intent(ACTION_REQUEST_NOW_PLAYING);
            intent.setPackage("ru.big.town.anative");
            context.sendBroadcast(intent);
        } catch (Exception unused) {
        }
    }

    private static String str(Cursor cursor, String str) {
        int columnIndex = cursor.getColumnIndex(str);
        return columnIndex >= 0 ? nz(cursor.getString(columnIndex)) : "";
    }

    private static int intOf(Cursor cursor, String str, int i) {
        int columnIndex = cursor.getColumnIndex(str);
        return columnIndex >= 0 ? cursor.getInt(columnIndex) : i;
    }

    private static long longOf(Cursor cursor, String str) {
        int columnIndex = cursor.getColumnIndex(str);
        if (columnIndex >= 0) {
            return cursor.getLong(columnIndex);
        }
        return 0L;
    }

    private static String nz(String str) {
        return str == null ? "" : str;
    }
}
