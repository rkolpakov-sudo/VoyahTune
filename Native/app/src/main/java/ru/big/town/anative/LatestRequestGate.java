package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class LatestRequestGate<T> {
    private boolean closed;
    private T latest;
    private T running;

    LatestRequestGate() {
    }

    synchronized T offer(T t) {
        if (!this.closed && t != null) {
            this.latest = t;
            if (this.running != null) {
                return null;
            }
            this.running = t;
            return t;
        }
        return null;
    }

    synchronized Completion<T> finish(T t) {
        if (!this.closed && t != null && this.running == t) {
            boolean z = this.latest == t;
            this.running = null;
            if (z) {
                this.latest = null;
            }
            T t2 = this.latest;
            if (t2 != null) {
                this.running = t2;
            } else {
                t2 = null;
            }
            return new Completion<>(z, t2);
        }
        return Completion.empty();
    }

    synchronized void reject(T t) {
        if (!this.closed && this.running == t) {
            this.running = null;
        }
    }

    synchronized T retry() {
        T t;
        if (!this.closed && this.running == null && (t = this.latest) != null) {
            this.running = t;
            return t;
        }
        return null;
    }

    synchronized void close() {
        this.closed = true;
        this.latest = null;
        this.running = null;
    }

    static final class Completion<T> {
        final T next;
        final boolean publish;

        private Completion(boolean z, T t) {
            this.publish = z;
            this.next = t;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static <T> Completion<T> empty() {
            return new Completion<>(false, null);
        }
    }
}
