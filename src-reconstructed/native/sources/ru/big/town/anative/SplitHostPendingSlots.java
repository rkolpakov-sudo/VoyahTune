package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class SplitHostPendingSlots<T> {
    private boolean drainActive;
    private final Object[] pending;

    SplitHostPendingSlots(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("slotCount must be positive");
        }
        this.pending = new Object[i];
    }

    boolean offer(int i, T t) {
        checkSlot(i);
        if (t == null) {
            throw new IllegalArgumentException("null work");
        }
        this.pending[i] = t;
        if (this.drainActive) {
            return false;
        }
        this.drainActive = true;
        return true;
    }

    T take(int i) {
        checkSlot(i);
        Object[] objArr = this.pending;
        T t = (T) objArr[i];
        objArr[i] = null;
        return t;
    }

    T peek(int i) {
        checkSlot(i);
        return (T) this.pending[i];
    }

    void clear(int i) {
        checkSlot(i);
        this.pending[i] = null;
    }

    boolean finishDrain() {
        for (Object obj : this.pending) {
            if (obj != null) {
                return true;
            }
        }
        this.drainActive = false;
        return false;
    }

    void rejectDrainPost() {
        this.drainActive = false;
    }

    int pendingCount() {
        int i = 0;
        for (Object obj : this.pending) {
            if (obj != null) {
                i++;
            }
        }
        return i;
    }

    private void checkSlot(int i) {
        if (i < 0 || i >= this.pending.length) {
            throw new IllegalArgumentException("unknown slot " + i);
        }
    }
}
