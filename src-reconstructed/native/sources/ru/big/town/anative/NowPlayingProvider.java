package ru.big.town.anative;

import android.car.Car;
import android.car.VehicleAreaDoor;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.util.Log;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import java.io.File;
import java.io.FileNotFoundException;

/* JADX INFO: loaded from: classes2.dex */
public class NowPlayingProvider extends ContentProvider {
    public static final String AUTHORITY = "ru.big.town.anative.nowplaying";
    private static final String KEYMANAGER_PACKAGE = "com.qinggan.keymanager.service";
    public static final String METHOD_MEDIA_COMMAND = "media_command";
    private static final String TAG = "$$$ NowPlayingProvider $$$";
    public static final Uri CONTENT_URI = Uri.parse("content://ru.big.town.anative.nowplaying");
    public static final Uri ART_URI = Uri.parse("content://ru.big.town.anative.nowplaying/art");
    public static final String[] COLUMNS = {"title", "artist", "album", Car.PACKAGE_SERVICE, "appLabel", "state", "position", TypedValues.TransitionType.S_DURATION, "hasArt", "updatedAt"};

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        MatrixCursor matrixCursor = new MatrixCursor(COLUMNS);
        matrixCursor.addRow(new Object[]{NowPlayingService.sTitle, NowPlayingService.sArtist, NowPlayingService.sAlbum, NowPlayingService.sPackage, NowPlayingService.sAppLabel, Integer.valueOf(NowPlayingService.sState), Long.valueOf(NowPlayingService.sPosition), Long.valueOf(NowPlayingService.sDuration), Integer.valueOf(NowPlayingService.sHasArt ? 1 : 0), Long.valueOf(NowPlayingService.sUpdatedAt)});
        return matrixCursor;
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String str) throws FileNotFoundException {
        if (getContext() == null) {
            throw new FileNotFoundException("no context");
        }
        File fileArtFile = NowPlayingService.artFile(getContext());
        if (!fileArtFile.exists()) {
            throw new FileNotFoundException("no album art");
        }
        return ParcelFileDescriptor.open(fileArtFile, VehicleAreaDoor.DOOR_HOOD);
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return (uri == null || !"art".equals(uri.getLastPathSegment())) ? "vnd.android.cursor.item/nowplaying" : "image/png";
    }

    @Override // android.content.ContentProvider
    public Bundle call(String str, String str2, Bundle bundle) {
        if (!METHOD_MEDIA_COMMAND.equals(str)) {
            return super.call(str, str2, bundle);
        }
        enforceMediaCommandCaller();
        MediaControlPolicy.Command command = parseCommand(str2);
        if (command == null) {
            Bundle bundle2 = new Bundle();
            bundle2.putString("route", "native");
            bundle2.putInt("keyCode", 0);
            bundle2.putString(Car.PACKAGE_SERVICE, "");
            bundle2.putInt("playbackClass", -1);
            return bundle2;
        }
        long jClearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return MediaControlRouter.dispatch(getContext(), command).toBundle();
        } finally {
            Binder.restoreCallingIdentity(jClearCallingIdentity);
        }
    }

    private void enforceMediaCommandCaller() {
        String callingPackage;
        if (Binder.getCallingUid() == Process.myUid()) {
            return;
        }
        try {
            callingPackage = getCallingPackage();
        } catch (SecurityException e) {
            Log.w(TAG, "media_command: invalid calling package: " + e.getMessage());
            callingPackage = null;
        }
        if (!KEYMANAGER_PACKAGE.equals(callingPackage)) {
            throw new SecurityException("media_command is not allowed for " + callingPackage);
        }
    }

    private static MediaControlPolicy.Command parseCommand(String str) {
        if ("play_pause".equals(str)) {
            return MediaControlPolicy.Command.PLAY_PAUSE;
        }
        if ("pause_only".equals(str)) {
            return MediaControlPolicy.Command.PAUSE_ONLY;
        }
        if ("next".equals(str)) {
            return MediaControlPolicy.Command.NEXT;
        }
        if ("previous".equals(str)) {
            return MediaControlPolicy.Command.PREVIOUS;
        }
        return null;
    }
}
