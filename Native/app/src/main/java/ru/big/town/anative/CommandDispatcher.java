package ru.big.town.anative;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * IMP-01 (SPEC L43): read-back dispatcher.
 *
 * submit(feature, action) sends the command and opens a READ_BACK_WINDOW_MS
 * read-back window. An ack (onAck) confirms the active command with its
 * ackSource; a contradictory read-back (onMismatch) fails it. When the window
 * elapses without ack the command is retried up to MAX_RETRIES times with
 * BACKOFF_FIRST_MS / BACKOFF_SECOND_MS delays jittered by +/-10%; after the
 * last window the result becomes TIMEOUT. Every transition is published to
 * the listener (UI badge + logs); terminal results stay available via
 * lastResult(feature).
 */
final class CommandDispatcher {
    static final long READ_BACK_WINDOW_MS = 1500;
    static final long BACKOFF_FIRST_MS = 300;
    static final long BACKOFF_SECOND_MS = 900;
    static final int MAX_RETRIES = 2;
    static final int JITTER_PERCENT = 10;

    interface SendAction {
        boolean send();
    }

    interface Listener {
        void onResult(CommandResult result);
    }

    interface Scheduler {
        void schedule(Runnable runnable, long delayMs);

        void cancel(Runnable runnable);
    }

    interface Clock {
        long now();
    }

    private final Scheduler scheduler;
    private final Clock clock;
    private final Random jitter;
    private final Listener listener;
    private final Map<String, CommandResult> active = new HashMap();
    private final Map<String, SendAction> actions = new HashMap();
    private final Map<String, Runnable> tasks = new HashMap();
    private final Map<String, Integer> retries = new HashMap();
    private final Map<String, CommandResult> latest = new HashMap();

    CommandDispatcher(Scheduler scheduler, Clock clock, Random jitter, Listener listener) {
        this.scheduler = scheduler;
        this.clock = clock;
        this.jitter = jitter;
        this.listener = listener;
    }

    synchronized boolean submit(String feature, SendAction action) {
        CommandResult commandResult = this.active.get(feature);
        if (commandResult != null) {
            cancelTask(feature);
            commandResult.fail(null);
            publish(commandResult);
        }
        CommandResult commandResult2 = new CommandResult(feature, CommandResult.State.SENT, this.clock.now());
        this.active.put(feature, commandResult2);
        this.actions.put(feature, action);
        this.retries.put(feature, 0);
        commandResult2.markSent(this.clock.now());
        boolean zSend;
        try {
            zSend = action.send();
        } catch (RuntimeException e) {
            zSend = false;
        }
        if (!zSend) {
            if (this.active.get(feature) == commandResult2) {
                remove(feature);
                commandResult2.fail(null);
                publish(commandResult2);
            }
            return false;
        }
        if (this.active.get(feature) == commandResult2) {
            scheduleWindow(feature);
            publish(commandResult2);
        }
        return true;
    }

    synchronized void onAck(String feature, String str) {
        CommandResult commandResult = this.active.get(feature);
        if (commandResult == null || commandResult.isTerminal()) {
            return;
        }
        cancelTask(feature);
        remove(feature);
        commandResult.confirm(str);
        publish(commandResult);
    }

    synchronized void onMismatch(String feature, String str) {
        CommandResult commandResult = this.active.get(feature);
        if (commandResult == null || commandResult.isTerminal()) {
            return;
        }
        cancelTask(feature);
        remove(feature);
        commandResult.fail(str);
        publish(commandResult);
    }

    synchronized CommandResult lastResult(String feature) {
        return this.latest.get(feature);
    }

    synchronized boolean hasActive(String feature) {
        return this.active.containsKey(feature);
    }

    private void scheduleWindow(String feature) {
        final String str = feature;
        Runnable runnable = new Runnable() {
            @Override // java.lang.Runnable
            public void run() {
                CommandDispatcher.this.onWindowElapsed(str);
            }
        };
        this.tasks.put(feature, runnable);
        this.scheduler.schedule(runnable, READ_BACK_WINDOW_MS);
    }

    private void onWindowElapsed(String feature) {
        CommandResult commandResult = this.active.get(feature);
        if (commandResult == null) {
            return;
        }
        Integer num = this.retries.get(feature);
        int i = num == null ? 0 : num.intValue();
        if (i < MAX_RETRIES) {
            this.retries.put(feature, i + 1);
            final String str = feature;
            Runnable runnable = new Runnable() {
                @Override // java.lang.Runnable
                public void run() {
                    CommandDispatcher.this.runRetry(str);
                }
            };
            this.tasks.put(feature, runnable);
            this.scheduler.schedule(runnable, backoffForRetry(i));
            return;
        }
        cancelTask(feature);
        remove(feature);
        commandResult.timeout();
        publish(commandResult);
    }

    private void runRetry(String feature) {
        CommandResult commandResult = this.active.get(feature);
        SendAction sendAction = this.actions.get(feature);
        if (commandResult == null || sendAction == null) {
            return;
        }
        commandResult.markSent(this.clock.now());
        boolean zSend;
        try {
            zSend = sendAction.send();
        } catch (RuntimeException e) {
            zSend = false;
        }
        if (!zSend) {
            if (this.active.get(feature) == commandResult) {
                cancelTask(feature);
                remove(feature);
                commandResult.fail(null);
                publish(commandResult);
            }
            return;
        }
        if (this.active.get(feature) == commandResult) {
            scheduleWindow(feature);
            publish(commandResult);
        }
    }

    private long backoffForRetry(int i) {
        long j = i == 0 ? BACKOFF_FIRST_MS : BACKOFF_SECOND_MS;
        long j2 = (j * JITTER_PERCENT) / 100;
        long j3 = j - j2;
        return j3 + this.jitter.nextInt((int) ((j2 * 2) + 1));
    }

    private void remove(String feature) {
        this.active.remove(feature);
        this.actions.remove(feature);
        this.retries.remove(feature);
    }

    private void cancelTask(String feature) {
        Runnable runnable = this.tasks.remove(feature);
        if (runnable != null) {
            this.scheduler.cancel(runnable);
        }
    }

    private void publish(CommandResult commandResult) {
        this.latest.put(commandResult.feature(), commandResult);
        this.listener.onResult(commandResult);
    }
}
