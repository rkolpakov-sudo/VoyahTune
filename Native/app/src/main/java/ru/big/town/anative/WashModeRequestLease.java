package ru.big.town.anative;

import android.content.Context;
import android.content.SharedPreferences;

/* JADX INFO: loaded from: classes2.dex */
final class WashModeRequestLease {
    private static final String KEY_ACTIVE_GENERATION = "active_generation";
    private static final String KEY_NEXT_GENERATION = "next_generation";
    private static final String PREFS = "wash_mode_runtime";
    private long activeGeneration;
    private long nextGeneration;
    private final Store store;

    interface Store {
        Snapshot read();

        boolean write(long j, long j2);
    }

    static final class Snapshot {
        final long activeGeneration;
        final long nextGeneration;

        Snapshot(long j, long j2) {
            this.nextGeneration = j;
            this.activeGeneration = j2;
        }
    }

    static WashModeRequestLease from(Context context) {
        final SharedPreferences sharedPreferences = context.getApplicationContext().createDeviceProtectedStorageContext().getSharedPreferences(PREFS, 0);
        return new WashModeRequestLease(new Store() { // from class: ru.big.town.anative.WashModeRequestLease.1
            @Override // ru.big.town.anative.WashModeRequestLease.Store
            public Snapshot read() {
                return new Snapshot(sharedPreferences.getLong(WashModeRequestLease.KEY_NEXT_GENERATION, 0L), sharedPreferences.getLong(WashModeRequestLease.KEY_ACTIVE_GENERATION, 0L));
            }

            @Override // ru.big.town.anative.WashModeRequestLease.Store
            public boolean write(long j, long j2) {
                return sharedPreferences.edit().putLong(WashModeRequestLease.KEY_NEXT_GENERATION, j).putLong(WashModeRequestLease.KEY_ACTIVE_GENERATION, j2).commit();
            }
        });
    }

    WashModeRequestLease(Store store) {
        if (store == null) {
            throw new IllegalArgumentException("Wash lease store is null");
        }
        this.store = store;
        Snapshot snapshot = store.read();
        snapshot = snapshot == null ? new Snapshot(0L, 0L) : snapshot;
        this.nextGeneration = Math.max(0L, snapshot.nextGeneration);
        long jMax = Math.max(0L, snapshot.activeGeneration);
        this.activeGeneration = jMax;
        if (this.nextGeneration < jMax) {
            this.nextGeneration = jMax;
        }
    }

    synchronized long arm() {
        long j = this.nextGeneration;
        long j2 = 1;
        if (j != Long.MAX_VALUE) {
            j2 = 1 + j;
        }
        if (!this.store.write(j2, j2)) {
            return 0L;
        }
        this.nextGeneration = j2;
        this.activeGeneration = j2;
        return j2;
    }

    synchronized long activeGeneration() {
        return this.activeGeneration;
    }

    synchronized boolean disarm(long j) {
        if (j > 0) {
            if (this.activeGeneration == j) {
                if (!this.store.write(this.nextGeneration, 0L)) {
                    return false;
                }
                this.activeGeneration = 0L;
                return true;
            }
        }
        return false;
    }
}
