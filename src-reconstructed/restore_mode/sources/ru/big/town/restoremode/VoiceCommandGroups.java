package ru.big.town.restoremode;

import java.util.EnumSet;
import java.util.Set;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceCommandGroups {
    VoiceCommandGroups() {
    }

    enum Group {
        SEATS("Сиденья"),
        WINDOWS("Окна"),
        CAR("Настройки автомобиля"),
        HEATING("Тёплые опции"),
        OTHER("Другое");

        final String title;

        Group(String str) {
            this.title = str;
        }
    }

    static Set<Group> forAction(String str) {
        EnumSet enumSetNoneOf = EnumSet.noneOf(Group.class);
        if (str.startsWith("seat:")) {
            enumSetNoneOf.add(Group.SEATS);
            if (str.contains(":heat:")) {
                enumSetNoneOf.add(Group.HEATING);
            }
            return enumSetNoneOf;
        }
        if (VoiceWindowCommands.isAction(str)) {
            enumSetNoneOf.add(Group.WINDOWS);
            return enumSetNoneOf;
        }
        if (str.startsWith("wheel_heat:") || str.equals("battery_heat") || str.equals("can:65080000c1c020000000") || str.equals("can:65080000c1c010000000")) {
            enumSetNoneOf.add(Group.HEATING);
            return enumSetNoneOf;
        }
        if (str.startsWith("drive:") || str.startsWith("energy:") || str.startsWith("fuel_charge:") || str.startsWith("recycle:") || str.startsWith("suspension_maintenance:") || str.startsWith("forced_ev:") || str.startsWith("pedestrian:") || str.startsWith("headlights:") || str.startsWith("auto_light:") || str.startsWith("port_cap:") || str.equals("toggle_headlights") || str.equals("toggle_headlights_auto") || str.equals("power_hold") || str.equals("wash") || str.equals("apply") || str.equals("can:7a080000000001000000") || str.equals("can:7a080000000002000000")) {
            enumSetNoneOf.add(Group.CAR);
            return enumSetNoneOf;
        }
        enumSetNoneOf.add(Group.OTHER);
        return enumSetNoneOf;
    }
}
