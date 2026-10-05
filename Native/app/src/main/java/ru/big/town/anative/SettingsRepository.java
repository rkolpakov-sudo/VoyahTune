package ru.big.town.anative;

import android.content.Context;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
final class SettingsRepository {
    static final String TAG = "SettingsRepository";

    enum Policy {
        VOT,          // snapshot of fork only (default)
        OEM_MIRROR,   // read TX57 cache, never write
        GUARDED_SYNC  // write to OEM only after account resolution + read-back
    }

    private static final Map<String, Policy> featurePolicies = new HashMap();
    static {
        featurePolicies.put("energy", Policy.GUARDED_SYNC);    // SREV
        featurePolicies.put("drive", Policy.OEM_MIRROR);       // drive modes
        featurePolicies.put("light", Policy.VOT);              // lighting
    }

    static Policy policyFor(String feature) {
        Policy p = featurePolicies.get(feature);
        return p != null ? p : Policy.VOT;
    }

    static boolean guardedWrite(Context context, String feature, String accountUid,
                                 Runnable writeOem, Runnable verifyRead) {
        Policy p = policyFor(feature);
        if (p != Policy.GUARDED_SYNC) {
            Log.w(TAG, "guardedWrite called for " + feature + " with policy " + p);
            writeOem.run();
            return true;
        }
        if (accountUid == null || accountUid.isEmpty() || "guest".equals(accountUid)) {
            Log.w(TAG, "Account not resolved for " + feature + "; using VOT (snapshot only)");
            return false;
        }
        // GUARDED_SYNC: write → read-back → confirm
        writeOem.run();
        try {
            verifyRead.run();
            Log.i(TAG, "GUARDED_SYNC confirmed for " + feature + " (account " + accountUid + ")");
            return true;
        } catch (RuntimeException e) {
            Log.e(TAG, "GUARDED_SYNC read-back failed for " + feature + ": " + e.getMessage());
            return false;
        }
    }
}