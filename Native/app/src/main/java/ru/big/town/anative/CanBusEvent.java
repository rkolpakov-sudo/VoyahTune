package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class CanBusEvent {
    final long connectionEpoch;
    final long elapsedRealtime;
    final int first;
    final Kind kind;
    final Origin origin;
    final int second;
    final long sequence;
    final int third;

    enum Kind {
        CONNECTION,
        CONNECTION_LOST,
        DOOR,
        GEAR,
        LIGHT_STATUS,
        VEHICLE_STATE,
        AMBIENT_TEMPERATURE
    }

    enum Origin {
        LIVE,
        SEED,
        REPLAY
    }

    private CanBusEvent(Kind kind, Origin origin, long j, long j2, long j3, int i, int i2, int i3) {
        this.kind = kind;
        this.origin = origin;
        this.connectionEpoch = j;
        this.sequence = j2;
        this.elapsedRealtime = j3;
        this.first = i;
        this.second = i2;
        this.third = i3;
    }

    static CanBusEvent connection(long j, long j2, long j3) {
        return new CanBusEvent(Kind.CONNECTION, Origin.LIVE, j, j2, j3, 0, 0, 0);
    }

    static CanBusEvent connectionLost(long j, long j2, long j3, long j4) {
        return new CanBusEvent(Kind.CONNECTION_LOST, Origin.LIVE, j, j2, j3, (int) Math.min(2147483647L, j4), 0, 0);
    }

    static CanBusEvent door(Origin origin, long j, long j2, long j3, int i) {
        return new CanBusEvent(Kind.DOOR, origin, j, j2, j3, i, 0, 0);
    }

    static CanBusEvent gear(Origin origin, long j, long j2, long j3, int i) {
        return new CanBusEvent(Kind.GEAR, origin, j, j2, j3, i, 0, 0);
    }

    static CanBusEvent light(Origin origin, long j, long j2, long j3, int i, int i2, int i3) {
        return new CanBusEvent(Kind.LIGHT_STATUS, origin, j, j2, j3, i, i2, i3);
    }

    static CanBusEvent vehicleState(Origin origin, long j, long j2, long j3, int i, int i2) {
        return new CanBusEvent(Kind.VEHICLE_STATE, origin, j, j2, j3, i, i2, 0);
    }

    static CanBusEvent ambientTemperature(Origin origin, long j, long j2, long j3, int i) {
        return new CanBusEvent(Kind.AMBIENT_TEMPERATURE, origin, j, j2, j3, i, 0, 0);
    }

    int signalKey() {
        if (this.kind == Kind.VEHICLE_STATE) {
            return this.first ^ (this.kind.ordinal() << 24);
        }
        return this.kind.ordinal() << 24;
    }

    boolean isOrderedTransition() {
        return this.kind == Kind.DOOR || this.kind == Kind.GEAR;
    }

    boolean samePayload(CanBusEvent canBusEvent) {
        return canBusEvent != null && this.kind == canBusEvent.kind && this.origin == canBusEvent.origin && this.connectionEpoch == canBusEvent.connectionEpoch && this.first == canBusEvent.first && this.second == canBusEvent.second && this.third == canBusEvent.third;
    }
}
