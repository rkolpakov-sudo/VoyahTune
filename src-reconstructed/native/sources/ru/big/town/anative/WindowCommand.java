package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class WindowCommand {
    final String field;
    final int value;

    private WindowCommand(String str, int i) {
        this.field = str;
        this.value = i;
    }

    static boolean handles(String str) {
        if (str != null) {
            return str.startsWith("windows:") || str.startsWith("sunroof:") || str.startsWith("sunshade:");
        }
        return false;
    }

    static WindowCommand parse(String str) {
        byte b;
        if (str == null) {
            return null;
        }
        str.hashCode();
        switch (str) {
            case "sunshade:close":
                b = 0;
                break;
            case "windows:vent":
                b = 1;
                break;
            case "sunroof:open":
                b = 2;
                break;
            case "sunroof:vent":
                b = 3;
                break;
            case "sunroof:close":
                b = 4;
                break;
            case "sunshade:open":
                b = 5;
                break;
            default:
                b = -1;
                break;
        }
        String str2 = "ALL_WINDOW_CONTROL";
        switch (b) {
            case 0:
                return new WindowCommand("SunroofControl", 6);
            case 1:
                return new WindowCommand("ALL_WINDOW_CONTROL", 6);
            case 2:
                return new WindowCommand("SunroofControl", 1);
            case 3:
                return new WindowCommand("SunroofControl", 4);
            case 4:
                return new WindowCommand("SunroofControl", 2);
            case 5:
                return new WindowCommand("SunroofControl", 5);
            default:
                String[] strArrSplit = str.split(":", -1);
                if ((strArrSplit.length != 2 && strArrSplit.length != 3) || !strArrSplit[0].equals("windows")) {
                    return null;
                }
                String str3 = strArrSplit[strArrSplit.length - 1];
                if (!str3.equals("open") && !str3.equals("close")) {
                    return null;
                }
                if (strArrSplit.length != 2) {
                    String str4 = strArrSplit[1];
                    str4.hashCode();
                    switch (str4) {
                        case "driver":
                            str2 = "DRIVER_WINDOW_CONTROL";
                            break;
                        case "passenger":
                            str2 = "PAS_WIDOW_CONTROL";
                            break;
                        case "rear_left":
                            str2 = "LEFT_BACK_WINDOW_CONTROL";
                            break;
                        case "rear_right":
                            str2 = "RIGHT_BACK_WINDOW_CONTROL";
                            break;
                        case "left":
                            str2 = "ALL_LEFT_WINDOW_CONTROL";
                            break;
                        case "rear":
                            str2 = "ALL_REAR_WINDOW_CONTROL";
                            break;
                        case "front":
                            str2 = "ALL_FRONT_WINDOW_CONTROL";
                            break;
                        case "right":
                            str2 = "ALL_RIGHT_WINDOW_CONTROL";
                            break;
                        default:
                            return null;
                    }
                }
                return new WindowCommand(str2, str3.equals("open") ? 3 : 1);
        }
    }
}
