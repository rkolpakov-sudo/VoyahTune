package ru.big.town.anative;

import java.util.function.BooleanSupplier;

/* JADX INFO: loaded from: classes2.dex */
final class SaveChargeSequence {

    interface Clock {
        long now();

        void sleep(long j) throws InterruptedException;
    }

    enum Outcome {
        CONFIRMED,
        MODE_UNCONFIRMED,
        TARGET_UNCONFIRMED,
        UNAVAILABLE,
        SEND_FAILED,
        CANCELLED
    }

    interface Vehicle {
        void modeConfirmed();

        State read();

        boolean selectSrev();

        boolean setLevel(int i);
    }

    SaveChargeSequence() {
    }

    static final class State {
        final int level;
        final int mode;

        State(int i, int i2) {
            this.mode = i;
            this.level = i2;
        }
    }

    static final class Result {
        final boolean modeConfirmed;
        final State observed;
        final Outcome outcome;

        Result(Outcome outcome, boolean z, State state) {
            this.outcome = outcome;
            this.modeConfirmed = z;
            this.observed = state;
        }
    }

    static Result run(int i, Vehicle vehicle, Clock clock, BooleanSupplier booleanSupplier) {
        int iRequireSaveChargeLevel = VehicleRestorePolicy.requireSaveChargeLevel(i);
        State state = null;
            if (!booleanSupplier.getAsBoolean()) {
                return result(Outcome.CANCELLED, false, null);
            }
            State state2 = vehicle.read();
            try {
                if (!booleanSupplier.getAsBoolean()) {
                    return result(Outcome.CANCELLED, false, state2);
                }
                if (state2 == null) {
                    return result(Outcome.UNAVAILABLE, false, null);
                }
                if (state2.mode != 4) {
                    if (!vehicle.selectSrev()) {
                        return result(Outcome.SEND_FAILED, false, state2);
                    }
                    long jNow = clock.now() + 2000;
                    do {
                        clock.sleep(100L);
                        if (!booleanSupplier.getAsBoolean()) {
                            return result(Outcome.CANCELLED, false, state2);
                        }
                        state2 = vehicle.read();
                        if (!booleanSupplier.getAsBoolean()) {
                            return result(Outcome.CANCELLED, false, state2);
                        }
                        if (state2 == null) {
                            return result(Outcome.UNAVAILABLE, false, null);
                        }
                        if (state2.mode == 4) {
                            break;
                        }
                    } while (clock.now() < jNow);
                    if (state2.mode != 4) {
                        return result(Outcome.MODE_UNCONFIRMED, false, state2);
                    }
                }
                vehicle.modeConfirmed();
                if (!booleanSupplier.getAsBoolean()) {
                    return result(Outcome.CANCELLED, true, state2);
                }
                if (state2.level == iRequireSaveChargeLevel) {
                    return result(Outcome.CONFIRMED, true, state2);
                }
                if (!vehicle.setLevel(iRequireSaveChargeLevel)) {
                    return result(Outcome.SEND_FAILED, true, state2);
                }
                long jNow2 = clock.now() + 3000;
                do {
                    clock.sleep(100L);
                    if (!booleanSupplier.getAsBoolean()) {
                        return result(Outcome.CANCELLED, true, state2);
                    }
                    state2 = vehicle.read();
                    if (!booleanSupplier.getAsBoolean()) {
                        return result(Outcome.CANCELLED, true, state2);
                    }
                    if (state2 == null) {
                        return result(Outcome.UNAVAILABLE, true, null);
                    }
                    if (state2.mode != 4) {
                        return result(Outcome.MODE_UNCONFIRMED, true, state2);
                    }
                    if (state2.level == iRequireSaveChargeLevel) {
                        return result(Outcome.CONFIRMED, true, state2);
                    }
                } while (clock.now() < jNow2);
                return result(Outcome.TARGET_UNCONFIRMED, true, state2);
            } catch (InterruptedException unused) {
                state = state2;
                Thread.currentThread().interrupt();
                return result(Outcome.CANCELLED, false, state);
            }
    }

    private static Result result(Outcome outcome, boolean z, State state) {
        return new Result(outcome, z, state);
    }
}
