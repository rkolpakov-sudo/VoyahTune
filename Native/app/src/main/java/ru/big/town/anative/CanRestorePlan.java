package ru.big.town.anative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
final class CanRestorePlan {
    private static final int NO_DEPENDENCY = -1;
    private final List<Command> commands;
    private final OperationResult[] results;

    interface Operation {
        OperationResult send();
    }

    enum OperationResult {
        CONFIRMED,
        ACCEPTED_UNCONFIRMED,
        TRANSIENT_FAILURE
    }

    interface Sender {
        boolean send(byte[][] bArr, String str);
    }

    enum AttemptResult {
        SUCCESS,
        ACCEPTED_UNCONFIRMED,
        TRANSIENT_FAILURE;

        boolean isComplete() {
            return this != TRANSIENT_FAILURE;
        }
    }

    private CanRestorePlan(List<Command> list) {
        this.commands = list;
        this.results = new OperationResult[list.size()];
    }

    AttemptResult sendPending(Sender sender) {
        OperationResult operationResultSend;
        for (int i = 0; i < this.commands.size(); i++) {
            if (this.results[i] == null) {
                Command command = this.commands.get(i);
                if (command.dependency < 0 || this.results[command.dependency] != null) {
                    if (command.operation != null) {
                        operationResultSend = command.operation.send();
                    } else {
                        operationResultSend = sender.send(command.frames, command.label) ? OperationResult.CONFIRMED : OperationResult.TRANSIENT_FAILURE;
                    }
                    if (operationResultSend != OperationResult.TRANSIENT_FAILURE) {
                        this.results[i] = operationResultSend;
                    }
                }
            }
        }
        if (!isComplete()) {
            return AttemptResult.TRANSIENT_FAILURE;
        }
        for (OperationResult operationResult : this.results) {
            if (operationResult == OperationResult.ACCEPTED_UNCONFIRMED) {
                return AttemptResult.ACCEPTED_UNCONFIRMED;
            }
        }
        return AttemptResult.SUCCESS;
    }

    void resetForNextPass() {
        for (int i = 0; i < this.commands.size(); i++) {
            if (this.commands.get(i).repeatOnNextPass) {
                this.results[i] = null;
            }
        }
    }

    int pendingCount() {
        int i = 0;
        for (OperationResult operationResult : this.results) {
            if (operationResult == null) {
                i++;
            }
        }
        return i;
    }

    boolean hasRepeatableCommands() {
        Iterator<Command> it = this.commands.iterator();
        while (it.hasNext()) {
            if (it.next().repeatOnNextPass) {
                return true;
            }
        }
        return false;
    }

    private boolean isComplete() {
        return pendingCount() == 0;
    }

    static final class Builder {
        private final List<Command> commands = new ArrayList();

        Builder() {
        }

        int add(String str, byte[][] bArr) {
            return addAfter(str, bArr, -1);
        }

        int addAfter(String str, byte[][] bArr, int i) {
            validate(str, bArr);
            if (i < -1 || i >= this.commands.size()) {
                throw new IllegalArgumentException("Invalid dependency for " + str);
            }
            this.commands.add(new Command(str, copy(bArr), null, i, true));
            return this.commands.size() - 1;
        }

        int addOnce(String str, Operation operation) {
            return addOperation(str, operation, false);
        }

        int addOperation(String str, Operation operation, boolean z) {
            if (str == null || str.isEmpty()) {
                throw new IllegalArgumentException("CAN command label is empty");
            }
            if (operation == null) {
                throw new IllegalArgumentException("CAN operation is null for " + str);
            }
            this.commands.add(new Command(str, null, operation, -1, z));
            return this.commands.size() - 1;
        }

        CanRestorePlan build() {
            if (this.commands.isEmpty()) {
                throw new IllegalArgumentException("Restore plan has no CAN commands");
            }
            return new CanRestorePlan(new ArrayList(this.commands));
        }

        private static void validate(String str, byte[][] bArr) {
            if (str == null || str.isEmpty()) {
                throw new IllegalArgumentException("CAN command label is empty");
            }
            if (bArr == null || bArr.length == 0) {
                throw new IllegalArgumentException("No CAN frames for required " + str);
            }
            for (byte[] bArr2 : bArr) {
                if (bArr2 == null || bArr2.length != 10) {
                    throw new IllegalArgumentException("Invalid CAN frame for required " + str);
                }
            }
        }

        private static byte[][] copy(byte[][] bArr) {
            byte[][] bArr2 = new byte[bArr.length][];
            for (int i = 0; i < bArr.length; i++) {
                byte[] bArr3 = bArr[i];
                bArr2[i] = Arrays.copyOf(bArr3, bArr3.length);
            }
            return bArr2;
        }
    }

    private static final class Command {
        final int dependency;
        final byte[][] frames;
        final String label;
        final Operation operation;
        final boolean repeatOnNextPass;

        Command(String str, byte[][] bArr, Operation operation, int i, boolean z) {
            this.label = str;
            this.frames = bArr;
            this.operation = operation;
            this.dependency = i;
            this.repeatOnNextPass = z;
        }
    }
}
