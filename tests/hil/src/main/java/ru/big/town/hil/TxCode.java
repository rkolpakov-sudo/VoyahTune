package ru.big.town.hil;

public final class TxCode {
    public static final int DOOR_STATUS = 2;
    public static final int GEAR_STATUS = 6;
    public static final int FUEL_LEVEL = 9;
    public static final int SNAPSHOT_QUERY = 20;
    public static final int VEHICLE_SPEED = 26;
    public static final int ADD_CALLBACK = 28;
    public static final int REMOVE_CALLBACK = 29;
    public static final int GET_STATE = 57;
    public static final int SET_STATE = 58;
    public static final int SET_BUNDLE = 77;

    public static final int CB_DOOR = 1;
    public static final int CB_AIR = 4;
    public static final int CB_LIGHT = 10;
    public static final int CB_GEAR = 12;
    public static final int CB_VEHICLE_STATE = 36;

    private TxCode() {
    }

    public static String label(int code) {
        switch (code) {
            case DOOR_STATUS:
                return "TX2 getDoorStatus";
            case GEAR_STATUS:
                return "TX6 getGearStatus";
            case FUEL_LEVEL:
                return "TX9 getFuelLevel";
            case SNAPSHOT_QUERY:
                return "TX20 queryVehicleState";
            case VEHICLE_SPEED:
                return "TX26 getVehicleSpeed";
            case ADD_CALLBACK:
                return "TX28 addCallback";
            case REMOVE_CALLBACK:
                return "TX29 removeCallback";
            case GET_STATE:
                return "TX57 getVehicleState";
            case SET_STATE:
                return "TX58 setVehicleState";
            case SET_BUNDLE:
                return "TX77 setVehicleAndAirBundleState";
            case CB_DOOR:
                return "CB1 doorStatus";
            case CB_AIR:
                return "CB4 ambientTemperature";
            case CB_LIGHT:
                return "CB10 lightStatus";
            case CB_GEAR:
                return "CB12 gearStatus";
            case CB_VEHICLE_STATE:
                return "CB36 vehicleState";
            default:
                return "code" + code;
        }
    }
}
