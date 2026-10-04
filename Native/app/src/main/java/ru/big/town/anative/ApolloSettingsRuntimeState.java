package ru.big.town.anative;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardCopyOption;

/* JADX INFO: loaded from: classes2.dex */
final class ApolloSettingsRuntimeState {
    private static final File BOOT_ID_FILE = new File("/proc/sys/kernel/random/boot_id");
    private static final String TAG = "ApolloSettingsFlag";

    enum TargetApplyResult {
        CONFIRMED,
        ACCEPTED_UNCONFIRMED,
        TRANSIENT_FAILURE
    }

    private ApolloSettingsRuntimeState() {
    }

    static TargetApplyResult applyTarget(Context context, boolean z) {
        if (context == null) {
            return TargetApplyResult.TRANSIENT_FAILURE;
        }
        if (isEnabled(context) == z) {
            return TargetApplyResult.CONFIRMED;
        }
        if (!setEnabled(context, z)) {
            return TargetApplyResult.TRANSIENT_FAILURE;
        }
        if (forceStopVehicleSettings(context)) {
            return TargetApplyResult.CONFIRMED;
        }
        Log.w(TAG, "VehicleSettings restart failed; target will apply on next process start");
        return TargetApplyResult.ACCEPTED_UNCONFIRMED;
    }

    static boolean isEnabled(Context context) {
        if (context == null) {
            return false;
        }
        File fileFlagFile = flagFile(context);
        try {
            if (fileFlagFile.isFile() && fileFlagFile.length() <= 192) {
                boolean zIsEnabledForBoot = ApolloSettingsRuntimeFlag.isEnabledForBoot(new String(Files.readAllBytes(fileFlagFile.toPath()), StandardCharsets.US_ASCII), readBootId());
                if (!zIsEnabledForBoot) {
                    deleteQuietly(fileFlagFile);
                }
                return zIsEnabledForBoot;
            }
            return false;
        } catch (Exception e) {
            Log.w(TAG, "Unable to read runtime flag", e);
            deleteQuietly(fileFlagFile);
            return false;
        }
    }

    static boolean setEnabled(Context context, boolean z) {
        if (context == null) {
            return false;
        }
        File fileFlagFile = flagFile(context);
        if (!z) {
            return !fileFlagFile.exists() || fileFlagFile.delete();
        }
        File parentFile = fileFlagFile.getParentFile();
        File file = new File(parentFile, "apollo_settings_runtime.v1.new");
        if (parentFile != null) {
            try {
                if (parentFile.isDirectory() || parentFile.mkdirs()) {
                    Files.write(file.toPath(), ApolloSettingsRuntimeFlag.encodeEnabled(readBootId()).getBytes(StandardCharsets.US_ASCII), new OpenOption[0]);
                    FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                    try {
                        fileOutputStream.getFD().sync();
                        fileOutputStream.close();
                        Files.move(file.toPath(), fileFlagFile.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                        return isEnabled(context);
                    } catch (Throwable th) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Unable to publish runtime flag", e);
                deleteQuietly(file);
            }
        }
        return false;
    }

    static File flagFile(Context context) {
        return new File(context.createDeviceProtectedStorageContext().getFilesDir(), "apollo_settings_runtime.v1");
    }

    private static boolean forceStopVehicleSettings(Context context) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
            if (activityManager == null) {
                return false;
            }
            ActivityManager.class.getMethod("forceStopPackage", String.class).invoke(activityManager, "com.qinggan.app.vehiclesetting");
            return true;
        } catch (Exception e) {
            boolean z = e instanceof InvocationTargetException;
            Throwable cause = e;
            if (z && e.getCause() != null) {
                cause = e;
                cause = e.getCause();
            }
            cause = e;
            Log.e(TAG, "force-stop VehicleSettings failed", cause);
            return false;
        }
    }

    private static String readBootId() throws IOException {
        return new String(Files.readAllBytes(BOOT_ID_FILE.toPath()), StandardCharsets.US_ASCII).trim();
    }

    private static void deleteQuietly(File file) {
        if (file == null || !file.exists() || file.delete()) {
            return;
        }
        Log.w(TAG, "Unable to remove stale runtime flag");
    }
}
