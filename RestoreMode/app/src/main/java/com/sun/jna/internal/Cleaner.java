package com.sun.jna.internal;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: classes.dex */
public class Cleaner {
    private static final Cleaner INSTANCE = new Cleaner();
    private Thread cleanerThread;
    private CleanerRef firstCleanable;
    private final ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();

    public interface Cleanable {
        void clean();
    }

    public static Cleaner getCleaner() {
        return INSTANCE;
    }

    private Cleaner() {
    }

    public synchronized Cleanable register(Object obj, Runnable runnable) {
        return add(new CleanerRef(this, obj, this.referenceQueue, runnable));
    }

    private synchronized CleanerRef add(CleanerRef cleanerRef) {
        synchronized (this.referenceQueue) {
            CleanerRef cleanerRef2 = this.firstCleanable;
            if (cleanerRef2 == null) {
                this.firstCleanable = cleanerRef;
            } else {
                cleanerRef.setNext(cleanerRef2);
                this.firstCleanable.setPrevious(cleanerRef);
                this.firstCleanable = cleanerRef;
            }
            if (this.cleanerThread == null) {
                Logger.getLogger(Cleaner.class.getName()).log(Level.FINE, "Starting CleanerThread");
                CleanerThread cleanerThread = new CleanerThread();
                this.cleanerThread = cleanerThread;
                cleanerThread.start();
            }
        }
        return cleanerRef;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized boolean remove(CleanerRef cleanerRef) {
        boolean z;
        boolean z2;
        synchronized (this.referenceQueue) {
            z = true;
            if (cleanerRef == this.firstCleanable) {
                this.firstCleanable = cleanerRef.getNext();
                z2 = true;
            } else {
                z2 = false;
            }
            if (cleanerRef.getPrevious() != null) {
                cleanerRef.getPrevious().setNext(cleanerRef.getNext());
            }
            if (cleanerRef.getNext() != null) {
                cleanerRef.getNext().setPrevious(cleanerRef.getPrevious());
            }
            if (cleanerRef.getPrevious() == null && cleanerRef.getNext() == null) {
                z = z2;
            }
            cleanerRef.setNext(null);
            cleanerRef.setPrevious(null);
        }
        return z;
    }

    private static class CleanerRef extends PhantomReference<Object> implements Cleanable {
        private final Cleaner cleaner;
        private final Runnable cleanupTask;
        private CleanerRef next;
        private CleanerRef previous;

        public CleanerRef(Cleaner cleaner, Object obj, ReferenceQueue<? super Object> referenceQueue, Runnable runnable) {
            super(obj, referenceQueue);
            this.cleaner = cleaner;
            this.cleanupTask = runnable;
        }

        @Override // com.sun.jna.internal.Cleaner.Cleanable
        public void clean() {
            if (this.cleaner.remove(this)) {
                this.cleanupTask.run();
            }
        }

        CleanerRef getPrevious() {
            return this.previous;
        }

        void setPrevious(CleanerRef cleanerRef) {
            this.previous = cleanerRef;
        }

        CleanerRef getNext() {
            return this.next;
        }

        void setNext(CleanerRef cleanerRef) {
            this.next = cleanerRef;
        }
    }

    private class CleanerThread extends Thread {
        private static final long CLEANER_LINGER_TIME = 30000;

        public CleanerThread() {
            super("JNA Cleaner");
            setDaemon(true);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (true) {
                try {
                    Reference referenceRemove = Cleaner.this.referenceQueue.remove(CLEANER_LINGER_TIME);
                    if (referenceRemove instanceof CleanerRef) {
                        ((CleanerRef) referenceRemove).clean();
                    } else {
                        if (referenceRemove == null) {
                            synchronized (Cleaner.this.referenceQueue) {
                                try {
                                    Logger logger = Logger.getLogger(Cleaner.class.getName());
                                    if (Cleaner.this.firstCleanable == null) {
                                        Cleaner.this.cleanerThread = null;
                                        logger.log(Level.FINE, "Shutting down CleanerThread");
                                        return;
                                    } else if (logger.isLoggable(Level.FINER)) {
                                        StringBuilder sb = new StringBuilder();
                                        for (CleanerRef cleanerRef = Cleaner.this.firstCleanable; cleanerRef != null; cleanerRef = cleanerRef.next) {
                                            if (sb.length() != 0) {
                                                sb.append(", ");
                                            }
                                            sb.append(cleanerRef.cleanupTask.toString());
                                        }
                                        logger.log(Level.FINER, "Registered Cleaners: {0}", sb.toString());
                                    }
                                } catch (Throwable th) {
                                    throw th;
                                }
                                return;
                            }
                        }
                        continue;
                    }
                } catch (InterruptedException unused) {
                    return;
                } catch (Exception e) {
                    Logger.getLogger(Cleaner.class.getName()).log(Level.SEVERE, (String) null, (Throwable) e);
                }
            }
        }
    }
}
