package ru.big.town.anative;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/* JADX INFO: loaded from: classes2.dex */
final class OemIndividualDriveProfileReader {
    private static final String ACCELERATOR_PREFIX = "drive_mode_runState";
    private static final String ACCOUNT_INFO_PATH = "/private/configs/token/accountInfo";
    private static final String OEM_GLOBAL_SETTINGS_URI = "content://qinggan.settings/global";
    private static final String STEERING_PREFIX = "drive_mode_steeringWheelAssist";
    private static final String TAG = "$$$ IndividualProfile $$$";
    private static final String[] VALUE_PROJECTION = {"value"};

    private OemIndividualDriveProfileReader() {
    }

    static DriveModeCanPolicy.IndividualProfile read(Context context) {
        String str = "guest";
        if (context == null) {
            return null;
        }
        String currentAccountId = readCurrentAccountId();
        if (currentAccountId == null) {
            Log.e(TAG, "Cannot identify current OEM account; Individual mode not sent");
            return null;
        }
        try {
            SettingRead oemGlobal = readOemGlobal(context.getContentResolver(), STEERING_PREFIX + currentAccountId);
            SettingRead oemGlobal2 = readOemGlobal(context.getContentResolver(), ACCELERATOR_PREFIX + currentAccountId);
            if (oemGlobal.success && oemGlobal2.success) {
                DriveModeCanPolicy.IndividualProfile profile = parseProfile(oemGlobal.value, oemGlobal2.value);
                if (profile == null) {
                    Log.e(TAG, "Invalid OEM Individual profile; mode not sent");
                    return profile;
                }
                StringBuilder sbAppend = new StringBuilder("OEM Individual profile loaded: steering=").append(profile.steering).append(" accelerator=").append(profile.accelerator).append(" account=");
                if (!"guest".equals(currentAccountId)) {
                    str = "user";
                }
                Log.i(TAG, sbAppend.append(str).toString());
                return profile;
            }
            Log.e(TAG, "Cannot access OEM settings provider; Individual mode not sent");
            return null;
        } catch (RuntimeException e) {
            Log.e(TAG, "Cannot read OEM Individual profile", e);
            return null;
        }
    }

    static DriveModeCanPolicy.IndividualProfile parseProfile(String str, String str2) {
        Integer intOrDefault = parseIntOrDefault(str, 2);
        Integer intOrDefault2 = parseIntOrDefault(str2, 1);
        if (intOrDefault == null || intOrDefault2 == null) {
            return null;
        }
        return DriveModeCanPolicy.IndividualProfile.validated(intOrDefault.intValue(), intOrDefault2.intValue());
    }

    static String parseAccountId(String str) {
        if (str == null) {
            return null;
        }
        String strTrim = str.trim();
        if (strTrim.isEmpty()) {
            return null;
        }
        if ("guest".equals(strTrim)) {
            return "guest";
        }
        String[] strArrSplit = strTrim.split("%", -1);
        if (strArrSplit.length < 5) {
            return null;
        }
        String strTrim2 = strArrSplit[strArrSplit.length - 2].trim();
        if (strTrim2.isEmpty()) {
            return null;
        }
        return strTrim2;
    }

    private static String readCurrentAccountId() {
        File file = new File(ACCOUNT_INFO_PATH);
        if (!file.isFile()) {
            Log.e(TAG, "OEM accountInfo file unavailable");
            return null;
        }
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while (true) {
                try {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        bufferedReader.close();
                        return parseAccountId(sb.toString());
                    }
                    sb.append(line);
                } catch (Throwable th) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            }
        } catch (IOException | SecurityException e) {
            Log.e(TAG, "Cannot read OEM accountInfo", e);
            return null;
        }
    }

    private static SettingRead readOemGlobal(ContentResolver contentResolver, String str) {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(Uri.parse(OEM_GLOBAL_SETTINGS_URI), VALUE_PROJECTION, "name=?", new String[]{str}, null);
            if (cursor == null) {
                return SettingRead.failed();
            }
            return SettingRead.success(cursor.moveToNext() ? cursor.getString(0) : null);
        } catch (RuntimeException e) {
            Log.e(TAG, "Cannot read OEM setting " + str, e);
            return SettingRead.failed();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static Integer parseIntOrDefault(String str, int i) {
        if (str == null) {
            return Integer.valueOf(i);
        }
        try {
            return Integer.valueOf(str.trim());
        } catch (NumberFormatException unused) {
            return null;
        }
    }

    private static final class SettingRead {
        final boolean success;
        final String value;

        private SettingRead(boolean z, String str) {
            this.success = z;
            this.value = str;
        }

        static SettingRead success(String str) {
            return new SettingRead(true, str);
        }

        static SettingRead failed() {
            return new SettingRead(false, null);
        }
    }
}
