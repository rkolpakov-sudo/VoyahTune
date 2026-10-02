package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class HeadlightCanPolicy {
    static final int ACTIVATE = 1;

    enum Command {
        LOW_BEAM("LOW_BEAM", 215),
        OUT_LAMP_OFF("OUT_LAMP_OFF", 1096),
        AUTO_LAMP_SWITCH("AUTO_LAMP_SWITCH", 1097);

        final int stableId;
        final String vehicleStateName;

        Command(String str, int i) {
            this.vehicleStateName = str;
            this.stableId = i;
        }
    }

    private HeadlightCanPolicy() {
    }

    static Command commandFor(boolean z) {
        return z ? Command.LOW_BEAM : Command.OUT_LAMP_OFF;
    }

    static Command commandForAutoPair(boolean z) {
        return z ? Command.LOW_BEAM : Command.AUTO_LAMP_SWITCH;
    }
}
