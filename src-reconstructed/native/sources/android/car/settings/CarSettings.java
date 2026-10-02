package android.car.settings;

import android.annotation.SystemApi;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public class CarSettings {
    public static final int DEFAULT_GARAGE_MODE_MAINTENANCE_WINDOW = 600000;
    public static final int[] DEFAULT_GARAGE_MODE_WAKE_UP_TIME = {0, 0};

    public static final class Global {
        public static final String DEFAULT_USER_RESTRICTIONS_SET = "android.car.DEFAULT_USER_RESTRICTIONS_SET";
        public static final String DISABLE_INSTRUMENTATION_SERVICE = "android.car.DISABLE_INSTRUMENTATION_SERVICE";
        public static final String ENABLE_USER_SWITCH_DEVELOPER_MESSAGE = "android.car.ENABLE_USER_SWITCH_DEVELOPER_MESSAGE";
        public static final String LAST_ACTIVE_PERSISTENT_USER_ID = "android.car.LAST_ACTIVE_PERSISTENT_USER_ID";
        public static final String LAST_ACTIVE_USER_ID = "android.car.LAST_ACTIVE_USER_ID";
        public static final String SYSTEM_BAR_VISIBILITY_OVERRIDE = "android.car.SYSTEM_BAR_VISIBILITY_OVERRIDE";

        private Global() {
            throw new UnsupportedOperationException("this class only provide constants");
        }
    }

    @SystemApi
    public static final class Secure {

        @SystemApi
        public static final String KEY_AUDIO_FOCUS_NAVIGATION_REJECTED_DURING_CALL = "android.car.KEY_AUDIO_FOCUS_NAVIGATION_REJECTED_DURING_CALL";
        public static final String KEY_BLUETOOTH_A2DP_SINK_DEVICES = "android.car.KEY_BLUETOOTH_A2DP_SINK_DEVICES";
        public static final String KEY_BLUETOOTH_HFP_CLIENT_DEVICES = "android.car.KEY_BLUETOOTH_HFP_CLIENT_DEVICES";
        public static final String KEY_BLUETOOTH_MAP_CLIENT_DEVICES = "android.car.KEY_BLUETOOTH_MAP_CLIENT_DEVICES";
        public static final String KEY_BLUETOOTH_PAN_DEVICES = "android.car.KEY_BLUETOOTH_PAN_DEVICES";
        public static final String KEY_BLUETOOTH_PBAP_CLIENT_DEVICES = "android.car.KEY_BLUETOOTH_PBAP_CLIENT_DEVICES";
        public static final String KEY_BLUETOOTH_PROFILES_INHIBITED = "android.car.BLUETOOTH_PROFILES_INHIBITED";
        public static final String KEY_ENABLE_INITIAL_NOTICE_SCREEN_TO_USER = "android.car.ENABLE_INITIAL_NOTICE_SCREEN_TO_USER";
        public static final String KEY_SETUP_WIZARD_IN_PROGRESS = "android.car.SETUP_WIZARD_IN_PROGRESS";

        private Secure() {
            throw new UnsupportedOperationException("this class only provide constants");
        }
    }

    private CarSettings() {
        throw new UnsupportedOperationException("this class only provide constants");
    }
}
