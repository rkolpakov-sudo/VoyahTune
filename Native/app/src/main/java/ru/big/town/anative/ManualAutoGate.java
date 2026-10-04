package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class ManualAutoGate {
    static final long INVALID_AUTOMATIC_TOKEN = -1;
    private boolean manualAutoSelected;
    private int pendingManualCommands;
    private long revision;

    ManualAutoGate() {
    }

    synchronized Ticket reserveManualCommand() {
        this.pendingManualCommands++;
        this.revision++;
        return new Ticket(this);
    }

    synchronized boolean setSelected(boolean z) {
        boolean z2;
        z2 = this.manualAutoSelected;
        this.manualAutoSelected = z;
        this.revision++;
        return z2;
    }

    synchronized boolean blocksAntiAuto() {
        return this.manualAutoSelected || this.pendingManualCommands > 0;
    }

    synchronized long beginAutomaticDecision() {
        if (this.pendingManualCommands > 0) {
            return -1L;
        }
        this.manualAutoSelected = false;
        long j = this.revision + 1;
        this.revision = j;
        return j;
    }

    /* JADX WARN: Code duplicated, block: B:13:0x0016  */
    synchronized boolean isAutomaticActionCurrent(long j) {
        boolean z;
        if (j == -1) {
            z = false;
        } else if (this.pendingManualCommands == 0 && this.revision == j) {
            z = true;
        } else {
            z = false;
        }
        return z;
    }

    synchronized int pendingManualCommandsForTest() {
        return this.pendingManualCommands;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void release(Ticket ticket) {
        if (ticket.closed) {
            return;
        }
        ticket.closed = true;
        int i = this.pendingManualCommands;
        if (i > 0) {
            this.pendingManualCommands = i - 1;
        }
        this.revision++;
    }

    static final class Ticket implements AutoCloseable {
        private boolean closed;
        private final ManualAutoGate owner;

        private Ticket(ManualAutoGate manualAutoGate) {
            this.owner = manualAutoGate;
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            this.owner.release(this);
        }
    }
}
