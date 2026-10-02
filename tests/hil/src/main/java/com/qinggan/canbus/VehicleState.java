package com.qinggan.canbus;

public enum VehicleState {
    LOW_BEAM(215),
    OUT_LAMP_OFF(1096),
    AUTO_LAMP_SWITCH(1097),
    HUM_VSP_FUNCTION_SW(665),
    HUM_ENERGY_PTREGEN_LEVL(619),
    BMS_SOC_DISPLAY(615),
    SCENE_MODE_EXTENDER_SET(1127),
    POWER_HOLD_MODE_SWITCH(1161),
    POWER_HOLD_MODE_TIME(1162),
    POWER_HOLD_MODE_WARNING(1163),
    CAR_CLEANING_MODE_SWITCH(1133),
    IVI_SOC_MODESET(957),
    SREV_SOC_SET(1196),
    IVI_FUEL_PORT_CAP(778),
    IVI_CHRG_PORT_CAP(779),
    DRIVING_MODE_SET(545),
    EPS_MODE_SET(722),
    PROP_MODE_SET(782),
    ASC_MAINTAIN_SWITCH(711),
    ASC_MODE_SELECT(750),
    ASC_ADJUST_DIREACT(751),
    ASC_ADJUST_SUSTEMP(1060),
    MANUAL_EASY_ENTER_SET(790),
    FCM_DURATION_CONTROL(1067),
    DRIVER_PREHEAT_SET(1080),
    BATTERY_TEP_CONTROL_SWITCH(1294),
    BCM_RSM_lightSWReason(1072),
    BCM_RSM_AmbBrightness(1071),
    BCM_RSM_FwBrightness(1070),
    BCM_RSM_IRBrightness(1073);

    private final int value;

    VehicleState(int value) {
        this.value = value;
    }

    public Integer getValue() {
        return Integer.valueOf(this.value);
    }

    public static int idOf(String name) {
        return valueOf(name).value;
    }
}
