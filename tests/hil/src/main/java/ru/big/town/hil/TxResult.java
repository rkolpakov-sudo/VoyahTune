package ru.big.town.hil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class TxResult {
    public final boolean send;
    public final List<Object> replyArgs;

    private TxResult(boolean send, List<Object> replyArgs) {
        this.send = send;
        this.replyArgs = Collections.unmodifiableList(replyArgs);
    }

    public static TxResult reject() {
        return new TxResult(false, new ArrayList<>());
    }

    public static TxResult reply(List<Object> args) {
        return new TxResult(true, new ArrayList<>(args));
    }

    public static TxResult reply(Object... args) {
        return new TxResult(true, new ArrayList<>(Arrays.asList(args)));
    }
}
