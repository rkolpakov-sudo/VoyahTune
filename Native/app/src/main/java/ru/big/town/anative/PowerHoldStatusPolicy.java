package ru.big.town.anative;

import androidx.core.app.NotificationCompat;
import java.util.Objects;

/* JADX INFO: loaded from: classes2.dex */
final class PowerHoldStatusPolicy {

    enum Status {
        UNKNOWN(0),
        INACTIVE(1),
        ACTIVATING(2),
        ACTIVE(3),
        FAILED(4);

        final int ipcCode;

        Status(int i) {
            this.ipcCode = i;
        }

        static Status fromIpcCode(int i) {
            for (Status status : values()) {
                if (status.ipcCode == i) {
                    return status;
                }
            }
            return UNKNOWN;
        }
    }

    enum ExitReason {
        NONE(0),
        LOW_BATTERY(1),
        TIME_UP(2),
        COMMON(3);

        final int ipcCode;

        ExitReason(int i) {
            this.ipcCode = i;
        }

        static ExitReason fromIpcCode(int i) {
            for (ExitReason exitReason : values()) {
                if (exitReason.ipcCode == i) {
                    return exitReason;
                }
            }
            return NONE;
        }
    }

    static final class Snapshot {
        final long connectionEpoch;
        final ExitReason exitReason;
        final long requestGeneration;
        final Status status;

        Snapshot(Status status, ExitReason exitReason, long j, long j2) {
            this.status = (Status) Objects.requireNonNull(status, NotificationCompat.CATEGORY_STATUS);
            this.exitReason = (ExitReason) Objects.requireNonNull(exitReason, "exitReason");
            this.connectionEpoch = j;
            this.requestGeneration = j2;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Snapshot)) {
                return false;
            }
            Snapshot snapshot = (Snapshot) obj;
            return this.connectionEpoch == snapshot.connectionEpoch && this.requestGeneration == snapshot.requestGeneration && this.status == snapshot.status && this.exitReason == snapshot.exitReason;
        }

        public int hashCode() {
            return Objects.hash(this.status, this.exitReason, Long.valueOf(this.connectionEpoch), Long.valueOf(this.requestGeneration));
        }
    }

    static final class Machine {
        private boolean activationAccepted;
        private long connectionEpoch;
        private boolean exitedFromActive;
        private long requestGeneration;
        private Status status = Status.UNKNOWN;
        private Status statusBeforeActivation = Status.UNKNOWN;
        private ExitReason exitReason = ExitReason.NONE;
        private ExitReason pendingExitReason = ExitReason.NONE;

        Machine() {
        }

        Snapshot snapshot() {
            return new Snapshot(this.status, this.exitReason, this.connectionEpoch, this.requestGeneration);
        }

        Snapshot onConnection(long j) {
            if (j <= 0 || j == this.connectionEpoch) {
                return snapshot();
            }
            this.connectionEpoch = j;
            this.activationAccepted = false;
            this.pendingExitReason = ExitReason.NONE;
            this.exitedFromActive = false;
            this.status = Status.UNKNOWN;
            this.exitReason = ExitReason.NONE;
            return snapshot();
        }

        Snapshot onConnectionLost(long j) {
            if (j != this.connectionEpoch) {
                return snapshot();
            }
            this.activationAccepted = false;
            this.pendingExitReason = ExitReason.NONE;
            this.exitedFromActive = false;
            this.status = Status.UNKNOWN;
            this.exitReason = ExitReason.NONE;
            return snapshot();
        }

        long beginActivation() {
            this.requestGeneration++;
            this.statusBeforeActivation = this.status;
            this.activationAccepted = false;
            this.pendingExitReason = ExitReason.NONE;
            this.exitedFromActive = false;
            this.status = Status.ACTIVATING;
            this.exitReason = ExitReason.NONE;
            return this.requestGeneration;
        }

        Snapshot finishActivation(long j, PowerHoldPolicy.Outcome outcome) {
            if (j != this.requestGeneration || this.status != Status.ACTIVATING) {
                return snapshot();
            }
            if (outcome == PowerHoldPolicy.Outcome.ACCEPTED) {
                this.activationAccepted = true;
                return snapshot();
            }
            this.activationAccepted = false;
            if (outcome == PowerHoldPolicy.Outcome.NOT_IN_PARK || outcome == PowerHoldPolicy.Outcome.LOW_BATTERY) {
                this.status = this.statusBeforeActivation;
            } else {
                this.status = Status.FAILED;
            }
            return snapshot();
        }

        Snapshot onActivationTimeout(long j) {
            if (j == this.requestGeneration && this.status == Status.ACTIVATING && this.activationAccepted) {
                this.activationAccepted = false;
                this.status = Status.FAILED;
            }
            return snapshot();
        }

        Snapshot onSwitch(long j, int i) {
            ExitReason exitReason;
            if (j != this.connectionEpoch) {
                return snapshot();
            }
            if (i == 1) {
                this.activationAccepted = false;
                this.pendingExitReason = ExitReason.NONE;
                this.exitedFromActive = false;
                this.status = Status.ACTIVE;
                this.exitReason = ExitReason.NONE;
                return snapshot();
            }
            if (i != 0) {
                return snapshot();
            }
            if (this.status == Status.ACTIVATING && this.activationAccepted) {
                return snapshot();
            }
            boolean z = this.status == Status.ACTIVE;
            this.activationAccepted = false;
            this.status = Status.INACTIVE;
            this.exitedFromActive = z;
            if (z) {
                exitReason = this.pendingExitReason == ExitReason.NONE ? ExitReason.COMMON : this.pendingExitReason;
            } else {
                exitReason = ExitReason.NONE;
            }
            this.exitReason = exitReason;
            this.pendingExitReason = ExitReason.NONE;
            return snapshot();
        }

        Snapshot onWarning(long j, int i) {
            ExitReason exitReasonWarningReason;
            if (j == this.connectionEpoch && (exitReasonWarningReason = PowerHoldStatusPolicy.warningReason(i)) != ExitReason.NONE) {
                if (this.status == Status.INACTIVE && this.exitedFromActive) {
                    this.exitReason = exitReasonWarningReason;
                } else {
                    this.pendingExitReason = exitReasonWarningReason;
                }
                return snapshot();
            }
            return snapshot();
        }
    }

    private PowerHoldStatusPolicy() {
    }

    static ExitReason warningReason(int i) {
        if (i == 1) {
            return ExitReason.LOW_BATTERY;
        }
        if (i == 2) {
            return ExitReason.TIME_UP;
        }
        return ExitReason.NONE;
    }
}
