package ru.big.town.anative;

import android.app.ActivityManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Process;
import androidx.core.os.EnvironmentCompat;
import java.util.Collections;
import java.util.Iterator;

/* JADX INFO: loaded from: classes2.dex */
public final class OtaStatusProvider extends ContentProvider {
    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    @Override // android.content.ContentProvider
    public Bundle call(String str, String str2, Bundle bundle) {
        boolean z;
        Bundle bundle2;
        if (Binder.getCallingUid() != 0) {
            throw new SecurityException("Root diagnostics only");
        }
        if (!"preflight".equals(str) && !"health".equals(str)) {
            throw new IllegalArgumentException("Unknown method");
        }
        Bundle bundle3 = new Bundle();
        long jClearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Iterator<ActivityManager.RunningServiceInfo> it = ((ActivityManager) getContext().getSystemService(ActivityManager.class)).getRunningServices(100).iterator();
            boolean z2 = false;
            while (true) {
                z = true;
                if (!it.hasNext()) {
                    break;
                }
                ActivityManager.RunningServiceInfo next = it.next();
                if (next.service.getClassName().equals(SetModesService.class.getName()) && next.pid == Process.myPid()) {
                    z2 = true;
                }
                Binder.restoreCallingIdentity(jClearCallingIdentity);
            }
            Bundle bundleCall = getContext().getContentResolver().call(Uri.parse("content://ru.big.town.restoremode.restoremodecontentprovider"), "otaHealth", (String) null, (Bundle) null);
            if (!z2 || bundleCall == null || !bundleCall.getBoolean("ready")) {
                z = false;
            }
            bundle3.putBoolean("otaReady", z);
            bundle3.putString("version", BuildConfig.VERSION_NAME);
            if ("preflight".equals(str) && (bundle2 = (Bundle) OemVehicleStateTransport.withSession(getContext(), Collections.emptyList(), new OemVehicleStateTransport.SessionOperation() { // from class: ru.big.town.anative.OtaStatusProvider$$ExternalSyntheticLambda0
                @Override // ru.big.town.anative.OemVehicleStateTransport.SessionOperation
                public final Object run(OemVehicleStateTransport.Session session) {
                    return OtaStatusProvider.lambda$call$0(session);
                }
            })) != null) {
                bundle3.putAll(bundle2);
            }
            return bundle3;
        } catch (Exception e) {
            bundle3.putBoolean("otaReady", false);
            bundle3.putString("error", e.toString());
            return bundle3;
        } finally {
            Binder.restoreCallingIdentity(jClearCallingIdentity);
        }
    }

    static /* synthetic */ Bundle lambda$call$0(OemVehicleStateTransport.Session session) {
        Bundle bundle = new Bundle();
        OemVehicleStateTransport.GearStatus gearStatus = session.readGearStatus();
        Integer vehicleSpeed = session.readVehicleSpeed();
        bundle.putBoolean("parked", gearStatus != null && gearStatus.value == 0);
        bundle.putBoolean("stationary", vehicleSpeed != null && vehicleSpeed.intValue() == 0);
        String strValueOf = EnvironmentCompat.MEDIA_UNKNOWN;
        bundle.putString("gear", gearStatus == null ? EnvironmentCompat.MEDIA_UNKNOWN : String.valueOf(gearStatus.value));
        if (vehicleSpeed != null) {
            strValueOf = String.valueOf(vehicleSpeed);
        }
        bundle.putString("speed", strValueOf);
        return bundle;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException();
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        throw new UnsupportedOperationException();
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        throw new UnsupportedOperationException();
    }
}
