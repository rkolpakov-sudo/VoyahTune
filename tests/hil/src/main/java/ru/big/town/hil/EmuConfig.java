package ru.big.town.hil;

public final class EmuConfig {
    public volatile WriteMode writeMode = WriteMode.ACK;
    public volatile long lateMillis = 1500L;
    public volatile int conflictValue = -1;
    public volatile int missingValue = 0;
    public volatile int doorValue = 0;
    public volatile int gearOrdinal = 0;
    public volatile int gearValue = 0;
    public volatile int speed = 0;
    public volatile int fuelPercent = 100;
    public volatile float fuelLiters = 60.0f;

    public EmuConfig copy() {
        EmuConfig c = new EmuConfig();
        c.writeMode = this.writeMode;
        c.lateMillis = this.lateMillis;
        c.conflictValue = this.conflictValue;
        c.missingValue = this.missingValue;
        c.doorValue = this.doorValue;
        c.gearOrdinal = this.gearOrdinal;
        c.gearValue = this.gearValue;
        c.speed = this.speed;
        c.fuelPercent = this.fuelPercent;
        c.fuelLiters = this.fuelLiters;
        return c;
    }
}
