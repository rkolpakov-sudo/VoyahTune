package ru.big.town.anative;

import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes2.dex */
final class MediaRefreshDelivery {
    private boolean closed;
    private final Executor executor;
    private final Listener listener;
    private Work pendingWork;
    private long revision;
    private boolean scheduled;
    private final Runnable drainRunnable = new Runnable() { // from class: ru.big.town.anative.MediaRefreshDelivery$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.drain();
        }
    };
    private String pendingReason = "";

    interface Listener {
        void accept(Work work, String str);
    }

    enum Work {
        PUBLISH,
        REPICK,
        REBUILD
    }

    MediaRefreshDelivery(Executor executor, Listener listener) {
        if (executor == null || listener == null) {
            throw new IllegalArgumentException("executor/listener required");
        }
        this.executor = executor;
        this.listener = listener;
    }

    void offer(Work work, String str) {
        boolean z;
        if (work == null) {
            return;
        }
        synchronized (this) {
            if (this.closed) {
                return;
            }
            if (this.pendingWork == null || work.ordinal() > this.pendingWork.ordinal()) {
                this.pendingWork = work;
                if (str == null) {
                    str = "";
                }
                this.pendingReason = str;
            } else if (work == this.pendingWork) {
                if (str == null) {
                    str = "";
                }
                this.pendingReason = str;
            }
            long j = this.revision + 1;
            this.revision = j;
            if (this.scheduled) {
                z = false;
                j = 0;
            } else {
                z = true;
                this.scheduled = true;
            }
            if (z) {
                schedule(j);
            }
        }
    }

    void close() {
        synchronized (this) {
            this.closed = true;
            this.pendingWork = null;
            this.pendingReason = "";
            this.scheduled = false;
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
                long j2 = this.revision;
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
    /* JADX WARN: Code duplicated, block: B:41:0x005d  */
    /* JADX WARN: Code duplicated, block: B:60:? A[RETURN, SYNTHETIC] */
    public void drain() {
        Work work;
        long j;
        long j2;
        synchronized (this) {
            if (!this.closed && (work = this.pendingWork) != null) {
                String str = this.pendingReason;
                this.pendingWork = null;
                this.pendingReason = "";
                try {
                    this.listener.accept(work, str);
                    synchronized (this) {
                        if (this.closed) {
                            this.scheduled = false;
                            this.pendingWork = null;
                            this.pendingReason = "";
                        } else if (this.pendingWork == null) {
                            this.scheduled = false;
                        } else {
                            j2 = this.revision;
                        }
                        j2 = 0;
                    }
                    if (j2 != 0) {
                        schedule(j2);
                        return;
                    }
                    return;
                } catch (Throwable unused) {
                    synchronized (this) {
                        if (!this.closed) {
                            if (this.pendingWork == null) {
                                this.scheduled = false;
                            } else {
                                j = this.revision;
                            }
                            if (j != 0) {
                                schedule(j);
                                return;
                            }
                            return;
                        }
                        this.scheduled = false;
                        this.pendingWork = null;
                        this.pendingReason = "";
                        j = 0;
                        if (j != 0) {
                            schedule(j);
                            return;
                        }
                        return;
                    }
                }
            }
            this.scheduled = false;
        }
    }
}
