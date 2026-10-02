import pathlib

p = pathlib.Path("Native/app/src/main/java/ru/big/town/anative/SeatCommand.java")
ls = p.read_text(encoding="utf-8", errors="replace").splitlines()
assert ls[21] == "    static SeatCommand parse(String str) {", repr(ls[21])
assert ls[76] == "    }", repr(ls[76])
assert ls[77] == "}", repr(ls[77])

new_parse = """    static SeatCommand parse(String str) {
        String str2;
        String str3;
        String str4;
        int i;
        if (str == null) {
            return null;
        }
        if (str.equals("wheel_heat:on")) {
            return new SeatCommand("STEER_WHEEL_HEAT_SWITCH", 2);
        }
        if (str.equals("wheel_heat:off")) {
            return new SeatCommand("STEER_WHEEL_HEAT_SWITCH", 1);
        }
        String[] strArrSplit = str.split(":", -1);
        if (strArrSplit.length != 4 || !strArrSplit[0].equals("seat")) {
            return null;
        }
        if (strArrSplit[1].equals("driver")) {
            str2 = "LEFT";
        } else {
            str2 = strArrSplit[1].equals("passenger") ? "RIGHT" : null;
        }
        if (strArrSplit[2].equals("massage")) {
            str3 = "MASS";
        } else if (strArrSplit[2].equals("heat")) {
            str3 = "HEATING";
        } else {
            str3 = strArrSplit[2].equals("vent") ? "VENTILATION" : null;
        }
        if (str2 == null || str3 == null) {
            return null;
        }
        String str5 = strArrSplit[3];
        if (str5.equals(DebugKt.DEBUG_PROPERTY_VALUE_ON) || str5.equals(DebugKt.DEBUG_PROPERTY_VALUE_OFF)) {
            str4 = "SWITCH";
            i = str5.equals(DebugKt.DEBUG_PROPERTY_VALUE_ON) ? 2 : 1;
        } else if (str5.matches("[1-3]")) {
            str4 = str3.equals("MASS") ? "INTEN" : "COMMAND";
            i = Integer.parseInt(str5);
        } else if (str3.equals("MASS") && (str5.equals("waves") || str5.equals("rollers"))) {
            str4 = "COMMAND";
            i = str5.equals("waves") ? 1 : 2;
        } else {
            return null;
        }
        return new SeatCommand("FRONT_SEAT_" + str3 + "_" + str4 + "_" + str2, i);
    }""".split("\n")

ls[21:77] = new_parse
p.write_text("\n".join(ls) + "\n", encoding="utf-8")
print("parse reconstructed; lines:", len(ls))
