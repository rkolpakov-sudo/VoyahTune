package ru.big.town.hil;

import java.util.List;

public interface CallbackSink {
    CallbackSink NOOP = (cbCode, args) -> {
    };

    void deliver(int cbCode, List<Object> args);
}
