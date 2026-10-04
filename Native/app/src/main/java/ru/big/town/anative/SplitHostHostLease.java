package ru.big.town.anative;

import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes2.dex */
final class SplitHostHostLease<T> {
    private long generation;
    private WeakReference<T> owner = new WeakReference<>(null);

    SplitHostHostLease() {
    }

    static final class Registration<T> {
        final long generation;
        final T previousOwner;

        Registration(long j, T t) {
            this.generation = j;
            this.previousOwner = t;
        }
    }

    synchronized Registration<T> acquire(T t) {
        T t2;
        long j;
        try {
            if (t == null) {
                throw new IllegalArgumentException("owner required");
            }
            t2 = this.owner.get();
            this.owner = new WeakReference<>(t);
            j = this.generation + 1;
            this.generation = j;
        } catch (Throwable th) {
            throw th;
        }
        return new Registration<>(j, t2);
    }

    synchronized void release(long j) {
        if (j != this.generation) {
            return;
        }
        this.owner.clear();
        this.owner = new WeakReference<>(null);
    }
}
