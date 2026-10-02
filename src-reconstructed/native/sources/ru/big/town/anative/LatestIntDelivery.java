package ru.big.town.anative;

import java.util.concurrent.Executor;
import java.util.function.IntConsumer;

/* JADX INFO: loaded from: classes2.dex */
final class LatestIntDelivery implements AutoCloseable {
    private boolean closed;
    private final Runnable drainRunnable;
    private final Executor executor;
    private long latestRevision;
    private long latestToken;
    private int latestValue;
    private final Listener listener;
    private long offeredRevision;
    private boolean scheduled;

    interface Listener {
        void accept(long j, long j2, int i);
    }

    LatestIntDelivery(Executor executor, final IntConsumer intConsumer) {
        this(executor, new Listener() { // from class: ru.big.town.anative.LatestIntDelivery$$ExternalSyntheticLambda1
            @Override // ru.big.town.anative.LatestIntDelivery.Listener
            public final void accept(long j, long j2, int i) {
                intConsumer.accept(i);
            }
        });
    }

    LatestIntDelivery(Executor executor, Listener listener) {
        this.drainRunnable = new Runnable() { // from class: ru.big.town.anative.LatestIntDelivery$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.drain();
            }
        };
        if (executor == null || listener == null) {
            throw new IllegalArgumentException("executor/listener required");
        }
        this.executor = executor;
        this.listener = listener;
    }

    void offer(int i) {
        offer(0L, 0L, i);
    }

    void offer(long j, int i) {
        offer(j, 0L, i);
    }

    void offer(long j, long j2, int i) {
        boolean z;
        synchronized (this) {
            if (this.closed) {
                return;
            }
            long j3 = 0;
            if (j > 0) {
                long j4 = this.latestToken;
                if (j4 > 0 && (j < j4 || (j == j4 && j2 < this.latestRevision))) {
                    return;
                }
            }
            this.latestToken = j;
            this.latestRevision = j2;
            this.latestValue = i;
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
                if (this.closed) {
                    return;
                }
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
        synchronized (this) {
            if (this.closed) {
                return;
            }
            long j = this.latestToken;
            long j2 = this.latestRevision;
            int i = this.latestValue;
            long j3 = this.offeredRevision;
            boolean z = true;
            long j4 = 0;
            try {
                this.listener.accept(j, j2, i);
                synchronized (this) {
                    if (this.closed) {
                        return;
                    }
                    long j5 = this.offeredRevision;
                    if (j5 == j3) {
                        z = false;
                    }
                    if (z) {
                        j4 = j5;
                    } else {
                        this.scheduled = false;
                    }
                    if (z) {
                        schedule(j4);
                    }
                }
            } catch (RuntimeException unused) {
                synchronized (this) {
                    if (this.closed) {
                        return;
                    }
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
                    if (this.closed) {
                        return;
                    }
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

    @Override // java.lang.AutoCloseable
    public synchronized void close() {
        this.closed = true;
        this.scheduled = false;
    }
}
