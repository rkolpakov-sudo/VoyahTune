package ru.big.town.anative;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;
import java.util.Random;

/**
 * IMP-01 (SPEC L43): production holder of the command state machine.
 *
 * Wires CommandDispatcher to the main-thread scheduler (Handler) and a
 * monotonic uptime clock, logs every transition, and publishes each result as
 * a broadcast to RestoreMode for the UI badge (same pattern as
 * SetModesService.publishPowerHoldStatus).
 */
final class CommandStatusHub {
    static final String ACTION_COMMAND_RESULT = "ru.big.town.anative.ACTION_COMMAND_RESULT";
    static final String EXTRA_FEATURE = "feature";
    static final String EXTRA_STATE = "state";
    static final String EXTRA_ATTEMPTS = "attempts";
    static final String EXTRA_ACK_SOURCE = "ackSource";
    static final String EXTRA_SENT_AT = "sentAt";
    private static final String BIND_PERMISSION = "ru.big.town.anative.permission.BIND_SET_MODES_SERVICE";
    private static final String RESTOREMODE_PKG = "ru.big.town.restoremode";
    private static final String TAG = "$$$ CommandStatus $$$";
    private static volatile CommandStatusHub instance;
    private final CommandDispatcher dispatcher;

    private CommandStatusHub() {
        HandlerThread handlerThread = new HandlerThread("CommandStatus");
        handlerThread.start();
        final Handler handler = new Handler(handlerThread.getLooper());
        this.dispatcher = new CommandDispatcher(new CommandDispatcher.Scheduler() { // from class: ru.big.town.anative.CommandStatusHub.1
            @Override // ru.big.town.anative.CommandDispatcher.Scheduler
            public void schedule(Runnable runnable, long j) {
                handler.postDelayed(runnable, j);
            }

            @Override // ru.big.town.anative.CommandDispatcher.Scheduler
            public void cancel(Runnable runnable) {
                handler.removeCallbacks(runnable);
            }
        }, new CommandDispatcher.Clock() { // from class: ru.big.town.anative.CommandStatusHub.2
            @Override // ru.big.town.anative.CommandDispatcher.Clock
            public long now() {
                return SystemClock.uptimeMillis();
            }
        }, new Random(), new CommandDispatcher.Listener() { // from class: ru.big.town.anative.CommandStatusHub.3
            @Override // ru.big.town.anative.CommandDispatcher.Listener
            public void onResult(CommandResult commandResult) {
                CommandStatusHub.this.publish(commandResult);
            }
        });
    }

    static CommandStatusHub get() {
        if (instance == null) {
            synchronized (CommandStatusHub.class) {
                if (instance == null) {
                    instance = new CommandStatusHub();
                }
            }
        }
        return instance;
    }

    boolean submit(String str, CommandDispatcher.SendAction sendAction) {
        return this.dispatcher.submit(str, sendAction);
    }

    void ack(String str, String str2) {
        this.dispatcher.onAck(str, str2);
    }

    void mismatch(String str, String str2) {
        this.dispatcher.onMismatch(str, str2);
    }

    CommandResult lastResult(String str) {
        return this.dispatcher.lastResult(str);
    }

    private void publish(CommandResult commandResult) {
        String strFeature = commandResult.feature();
        CommandResult.State state = commandResult.state();
        Log.i(TAG, "feature=" + strFeature + " state=" + state + " attempts=" + commandResult.attempts() + " ackSource=" + commandResult.ackSource());
        if (!ReadBackTable.isTracked(strFeature)) {
            return;
        }
        Context context = GlobalVars.SAVE_CONTEXT;
        if (context == null) {
            return;
        }
        Intent intent = new Intent(ACTION_COMMAND_RESULT);
        intent.setPackage(RESTOREMODE_PKG);
        intent.putExtra(EXTRA_FEATURE, strFeature);
        intent.putExtra(EXTRA_STATE, state.name());
        intent.putExtra(EXTRA_ATTEMPTS, commandResult.attempts());
        intent.putExtra(EXTRA_ACK_SOURCE, commandResult.ackSource());
        intent.putExtra(EXTRA_SENT_AT, commandResult.sentAt());
        try {
            context.sendBroadcast(intent, BIND_PERMISSION);
        } catch (RuntimeException e) {
            Log.w(TAG, "publish failed: " + e.getMessage());
        }
    }
}
