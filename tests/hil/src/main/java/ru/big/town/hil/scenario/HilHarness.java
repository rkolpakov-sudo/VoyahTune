package ru.big.town.hil.scenario;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ru.big.town.hil.CanEmulatorCore;
import ru.big.town.hil.TxCode;
import ru.big.town.hil.TxResult;

public interface HilHarness {
    void sleep(long millis);

    void action(String name, CanEmulatorCore core);

    Object appState(String name);

    default void onPush(int cbCode, List<Object> args) {
    }

    default void onSend(int stateId, int value) {
    }

    default boolean sendState(CanEmulatorCore core, String stateName, int stateId, int ordinal, int value) {
        onSend(stateId, value);
        TxResult result = core.transact(TxCode.SET_STATE, Arrays.asList(1, ordinal, stateId, value));
        return result.send;
    }

    static HilHarness noop() {
        return new HilHarness() {
            @Override
            public void sleep(long millis) {
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("interrupted", e);
                }
            }

            @Override
            public void action(String name, CanEmulatorCore core) {
                throw new IllegalStateException("harness has no action: " + name);
            }

            @Override
            public Object appState(String name) {
                return null;
            }

            @Override
            public void onPush(int cbCode, List<Object> args) {
            }
        };
    }

    static HilHarness noop(Map<String, Runnable> actions) {
        HilHarness base = noop();
        return new HilHarness() {
            @Override
            public void sleep(long millis) {
                base.sleep(millis);
            }

            @Override
            public void action(String name, CanEmulatorCore core) {
                Runnable r = actions.get(name);
                if (r == null) {
                    base.action(name, core);
                } else {
                    r.run();
                }
            }

            @Override
            public Object appState(String name) {
                return base.appState(name);
            }
        };
    }
}
