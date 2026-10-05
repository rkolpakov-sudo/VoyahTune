package ru.big.town.anative;

import java.util.ArrayList;
import java.util.List;

/**
 * IMP-06 (SPEC L48): coalesces a burst of manual applies into a single
 * restore cycle.
 *
 * While the previously posted task has not started yet, a newer apply
 * supersedes it: the old task is cancelled from the handler queue and its
 * payloads are carried into the new task, so every payload still runs exactly
 * once (3.22 ran them from the cancelled cycles as well). Only the newest
 * gate/generation/epoch drive the cycle; superseded cycles would have returned
 * CANCELLED before touching any side effect anyway.
 */
final class PendingApplySeries {

    interface Runner {
        void run(Runnable completion, long gate, long generation, long epoch, boolean manual);
    }

    static final class Offer {
        final Task toPost;
        final Task superseded;

        Offer(Task toPost, Task superseded) {
            this.toPost = toPost;
            this.superseded = superseded;
        }
    }

    static final class Task implements Runnable {
        private final PendingApplySeries series;
        private long gate;
        private long generation;
        private long epoch;
        private boolean manual;
        private final List<Runnable> payloads = new ArrayList();
        private boolean superseded;
        private boolean finished;

        Task(PendingApplySeries pendingApplySeries) {
            this.series = pendingApplySeries;
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (this.series) {
                if (this.finished || this.superseded) {
                    return;
                }
                this.finished = true;
            }
            this.series.runner.run(new Runnable() { // from class: ru.big.town.anative.PendingApplySeries$Task$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PendingApplySeries.Task.this.runPayloads();
                }
            }, this.gate, this.generation, this.epoch, this.manual);
        }

        private void runPayloads() {
            for (Runnable runnable : this.payloads) {
                runnable.run();
            }
        }
    }

    private final Runner runner;
    private Task pending;

    PendingApplySeries(Runner pendingApplySeriesRunner) {
        this.runner = pendingApplySeriesRunner;
    }

    synchronized Offer offer(long gate, long generation, long epoch, boolean manual, Runnable runnable) {
        Task task = new Task(this);
        Task task2 = this.pending;
        if (task2 != null && !task2.finished && !task2.superseded) {
            task2.superseded = true;
            task.payloads.addAll(task2.payloads);
        } else {
            task2 = null;
        }
        if (runnable != null) {
            task.payloads.add(runnable);
        }
        task.gate = gate;
        task.generation = generation;
        task.epoch = epoch;
        task.manual = manual;
        this.pending = task;
        return new Offer(task, task2);
    }
}
