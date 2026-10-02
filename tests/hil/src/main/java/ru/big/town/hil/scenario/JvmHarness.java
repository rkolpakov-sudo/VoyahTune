package ru.big.town.hil.scenario;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ru.big.town.hil.CanEmulatorCore;
import ru.big.town.hil.TxCode;

public final class JvmHarness implements HilHarness {
    private final Map<String, Object> state = new ConcurrentHashMap<>();
    private Integer pendingId;
    private Integer pendingValue;

    @Override
    public void sleep(long millis) {
        if (millis <= 0) {
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted during sleep", e);
        }
    }

    @Override
    public void action(String name, CanEmulatorCore core) {
        switch (name) {
            case "acc_on":
                this.state.put("acc", "ON");
                return;
            case "acc_off":
                this.state.put("acc", "OFF");
                return;
            case "drive":
                core.setGear(1, 4);
                return;
            case "park":
                core.setGear(0, 0);
                return;
            default:
                throw new IllegalStateException("harness has no action: " + name);
        }
    }

    @Override
    public Object appState(String name) {
        return this.state.get(name);
    }

    @Override
    public void onSend(int stateId, int value) {
        this.pendingId = stateId;
        this.pendingValue = value;
        this.state.put("ui_state", "ACCEPTED_UNCONFIRMED");
    }

    @Override
    public void onPush(int cbCode, List<Object> args) {
        if (cbCode != TxCode.CB_VEHICLE_STATE || args.size() < 4) {
            return;
        }
        Integer id = asInt(args.get(2));
        Integer value = asInt(args.get(3));
        if (id != null && this.pendingId != null
                && id.equals(this.pendingId) && value != null && value.equals(this.pendingValue)) {
            this.state.put("ui_state", "CONFIRMED");
        }
    }

    private static Integer asInt(Object v) {
        if (v instanceof Integer) {
            return (Integer) v;
        }
        if (v instanceof Long) {
            return ((Long) v).intValue();
        }
        return null;
    }
}
