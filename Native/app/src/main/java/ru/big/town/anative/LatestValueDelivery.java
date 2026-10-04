package ru.big.town.anative;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes2.dex */
final class LatestValueDelivery<T> {
    private final Runnable drainRunnable;
    private final Executor executor;
    private long latestRevision;
    private long latestToken;
    private T latestValue;
    private final Listener<T> listener;
    private long offeredRevision;
    private boolean scheduled;

    interface Listener<T> {
        void accept(long j, long j2, T t);
    }

    LatestValueDelivery(Executor executor, final Consumer<T> consumer) {
        this(executor, new Listener<T>() { // from class: ru.big.town.anative.LatestValueDelivery$$ExternalSyntheticLambda0
            @Override // ru.big.town.anative.LatestValueDelivery.Listener
            public final void accept(long j, long j2, T t) {
                consumer.accept(t);
            }
        });
    }

    LatestValueDelivery(Executor executor, Listener<T> listener) {
        this.drainRunnable = new Runnable() { // from class: ru.big.town.anative.LatestValueDelivery$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                LatestValueDelivery.this.drain();
            }
        };
        if (executor == null || listener == null) {
            throw new IllegalArgumentException("executor/listener required");
        }
        this.executor = executor;
        this.listener = listener;
    }

    void offer(long j, long j2, T t) {
        boolean z;
        synchronized (this) {
            long j3 = 0;
            if (j > 0) {
                long j4 = this.latestToken;
                if (j4 > 0 && (j < j4 || (j == j4 && j2 < this.latestRevision))) {
                    return;
                }
            }
            this.latestToken = j;
            this.latestRevision = j2;
            this.latestValue = t;
            long j5 = this.offeredRevision + 1;
            this.offeredRevision = j5;
            if (this.scheduled) {
                z = false;
            } else {
                z = true;
                this.scheduled = true;
                j3 = j5;
            }
            if (z) {
                schedule(j3);
            }
        }
    }

    private void schedule(long j) {
        try {
            this.executor.execute(this.drainRunnable);
        } catch (RuntimeException unused) {
            synchronized (this) {
                long j2 = this.offeredRevision;
                if (j2 == j) {
                    this.scheduled = false;
                    j2 = 0;
                }
                if (j2 != 0) {
                    schedule(j2);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void drain() {
        long j;
        long j2;
        T t;
        long j3;
        synchronized (this) {
            j = this.latestToken;
            j2 = this.latestRevision;
            t = this.latestValue;
            j3 = this.offeredRevision;
        }
        boolean z = true;
        long j4 = 0;
        try {
            this.listener.accept(j, j2, t);
            synchronized (this) {
                long j5 = this.offeredRevision;
                if (j5 == j3) {
                    z = false;
                }
                if (z) {
                    j4 = j5;
                } else {
                    this.scheduled = false;
                }
            }
            if (z) {
                schedule(j4);
            }
        } catch (RuntimeException unused) {
            synchronized (this) {
                long j6 = this.offeredRevision;
                if (j6 == j3) {
                    z = false;
                }
                if (z) {
                    j4 = j6;
                } else {
                    this.scheduled = false;
                }
                if (z) {
                    schedule(j4);
                }
            }
        } catch (Throwable th) {
            synchronized (this) {
                long j7 = this.offeredRevision;
                if (j7 == j3) {
                    z = false;
                }
                if (z) {
                    j4 = j7;
                } else {
                    this.scheduled = false;
                }
                if (z) {
                    schedule(j4);
                }
                throw th;
            }
        }
    }
}
