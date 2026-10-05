package ru.big.town.anative;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Deterministic scheduler for dispatcher tests: tasks fire when test code
 * advances virtual time; every scheduled delay is recorded so tests can
 * assert backoff jitter bounds.
 */
final class FakeScheduler implements CommandDispatcher.Scheduler {
    private long now;
    private final List<Entry> queue = new ArrayList();
    final List<Long> scheduledDelays = new ArrayList();

    private static final class Entry {
        final Runnable task;
        final long at;

        Entry(Runnable runnable, long j) {
            this.task = runnable;
            this.at = j;
        }
    }

    FakeScheduler() {
    }

    long now() {
        return this.now;
    }

    @Override // ru.big.town.anative.CommandDispatcher.Scheduler
    public void schedule(Runnable runnable, long j) {
        this.scheduledDelays.add(j);
        this.queue.add(new Entry(runnable, this.now + j));
    }

    @Override // ru.big.town.anative.CommandDispatcher.Scheduler
    public void cancel(Runnable runnable) {
        Iterator<Entry> it = this.queue.iterator();
        while (it.hasNext()) {
            if (it.next().task == runnable) {
                it.remove();
            }
        }
    }

    void advance(long j) {
        long j2 = this.now + j;
        while (true) {
            Entry entry = null;
            for (Entry entry2 : this.queue) {
                if (entry2.at <= j2 && (entry == null || entry2.at < entry.at)) {
                    entry = entry2;
                }
            }
            if (entry == null) {
                break;
            }
            this.now = entry.at;
            this.queue.remove(entry);
            entry.task.run();
        }
        this.now = j2;
    }

    boolean idle() {
        return this.queue.isEmpty();
    }
}
