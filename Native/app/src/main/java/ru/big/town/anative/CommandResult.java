package ru.big.town.anative;

/**
 * IMP-01 (SPEC L43): unified command state machine record.
 *
 * States: SENT (handed to transport) -> PENDING_ACK (awaiting read-back
 * window) -> CONFIRMED (ack observed) | FAILED (send rejected or read-back
 * contradicted the expected value) | TIMEOUT (window elapsed twice without
 * ack; attempts exhausted).
 */
final class CommandResult {
    enum State {
        SENT,
        PENDING_ACK,
        CONFIRMED,
        FAILED,
        TIMEOUT
    }

    private final String feature;
    private State state;
    private long sentAt;
    private String ackSource;
    private int attempts;

    CommandResult(String feature, State state, long sentAt) {
        this.feature = feature;
        this.state = state;
        this.sentAt = sentAt;
    }

    String feature() {
        return this.feature;
    }

    State state() {
        return this.state;
    }

    long sentAt() {
        return this.sentAt;
    }

    String ackSource() {
        return this.ackSource;
    }

    int attempts() {
        return this.attempts;
    }

    boolean isTerminal() {
        State state = this.state;
        return state == State.CONFIRMED || state == State.FAILED || state == State.TIMEOUT;
    }

    void markSent(long j) {
        this.sentAt = j;
        this.attempts++;
        this.state = State.PENDING_ACK;
    }
    void confirm(String str) {
        this.ackSource = str;
        this.state = State.CONFIRMED;
    }

    void fail(String str) {
        if (str != null) {
            this.ackSource = str;
        }
        this.state = State.FAILED;
    }

    void timeout() {
        this.state = State.TIMEOUT;
    }
}
