package com.android.internal.car;

import android.util.EventLog;

/* JADX INFO: loaded from: classes.dex */
public class EventLogTags {
    public static final int CAR_HELPER_BOOT_PHASE = 150001;
    public static final int CAR_HELPER_HAL_CREATE_USER = 150013;
    public static final int CAR_HELPER_HAL_DEFAULT_BEHAVIOR = 150011;
    public static final int CAR_HELPER_HAL_REQUEST = 150009;
    public static final int CAR_HELPER_HAL_RESPONSE = 150010;
    public static final int CAR_HELPER_HAL_START_USER = 150012;
    public static final int CAR_HELPER_PRE_CREATION_REQUESTED = 150014;
    public static final int CAR_HELPER_PRE_CREATION_STATUS = 150015;
    public static final int CAR_HELPER_START = 150000;
    public static final int CAR_HELPER_SVC_CONNECTED = 150008;
    public static final int CAR_HELPER_USER_STARTING = 150002;
    public static final int CAR_HELPER_USER_STOPPED = 150007;
    public static final int CAR_HELPER_USER_STOPPING = 150006;
    public static final int CAR_HELPER_USER_SWITCHING = 150003;
    public static final int CAR_HELPER_USER_UNLOCKED = 150005;
    public static final int CAR_HELPER_USER_UNLOCKING = 150004;
    public static final int CAR_SERVICE_CONNECTED = 150056;
    public static final int CAR_SERVICE_CREATE = 150055;
    public static final int CAR_SERVICE_DESTROY = 150057;
    public static final int CAR_SERVICE_INIT = 150050;
    public static final int CAR_SERVICE_ON_USER_LIFECYCLE = 150053;
    public static final int CAR_SERVICE_SET_CAR_SERVICE_HELPER = 150052;
    public static final int CAR_SERVICE_SET_INITIAL_USER = 150054;
    public static final int CAR_SERVICE_VHAL_DIED = 150058;
    public static final int CAR_SERVICE_VHAL_RECONNECTED = 150051;
    public static final int CAR_USER_HAL_CREATE_USER_REQ = 150151;
    public static final int CAR_USER_HAL_CREATE_USER_RESP = 150152;
    public static final int CAR_USER_HAL_GET_USER_AUTH_REQ = 150145;
    public static final int CAR_USER_HAL_GET_USER_AUTH_RESP = 150146;
    public static final int CAR_USER_HAL_INITIAL_USER_INFO_REQ = 150140;
    public static final int CAR_USER_HAL_INITIAL_USER_INFO_RESP = 150141;
    public static final int CAR_USER_HAL_LEGACY_SWITCH_USER_REQ = 150147;
    public static final int CAR_USER_HAL_OEM_SWITCH_USER_REQ = 150150;
    public static final int CAR_USER_HAL_POST_SWITCH_USER_REQ = 150144;
    public static final int CAR_USER_HAL_REMOVE_USER_REQ = 150153;
    public static final int CAR_USER_HAL_SET_USER_AUTH_REQ = 150148;
    public static final int CAR_USER_HAL_SET_USER_AUTH_RESP = 150149;
    public static final int CAR_USER_HAL_SWITCH_USER_REQ = 150142;
    public static final int CAR_USER_HAL_SWITCH_USER_RESP = 150143;
    public static final int CAR_USER_MGR_ADD_LISTENER = 150171;
    public static final int CAR_USER_MGR_CREATE_USER_REQ = 150180;
    public static final int CAR_USER_MGR_CREATE_USER_RESP = 150181;
    public static final int CAR_USER_MGR_DISCONNECTED = 150173;
    public static final int CAR_USER_MGR_GET_USER_AUTH_REQ = 150176;
    public static final int CAR_USER_MGR_GET_USER_AUTH_RESP = 150177;
    public static final int CAR_USER_MGR_NOTIFY_LIFECYCLE_LISTENER = 150184;
    public static final int CAR_USER_MGR_REMOVE_LISTENER = 150172;
    public static final int CAR_USER_MGR_REMOVE_USER_REQ = 150182;
    public static final int CAR_USER_MGR_REMOVE_USER_RESP = 150183;
    public static final int CAR_USER_MGR_SET_USER_AUTH_REQ = 150178;
    public static final int CAR_USER_MGR_SET_USER_AUTH_RESP = 150179;
    public static final int CAR_USER_MGR_SWITCH_USER_REQ = 150174;
    public static final int CAR_USER_MGR_SWITCH_USER_RESP = 150175;
    public static final int CAR_USER_SVC_CREATE_USER_REQ = 150115;
    public static final int CAR_USER_SVC_CREATE_USER_RESP = 150116;
    public static final int CAR_USER_SVC_CREATE_USER_USER_CREATED = 150117;
    public static final int CAR_USER_SVC_CREATE_USER_USER_REMOVED = 150118;
    public static final int CAR_USER_SVC_GET_USER_AUTH_REQ = 150109;
    public static final int CAR_USER_SVC_GET_USER_AUTH_RESP = 150110;
    public static final int CAR_USER_SVC_INITIAL_USER_INFO_REQ = 150100;
    public static final int CAR_USER_SVC_INITIAL_USER_INFO_RESP = 150101;
    public static final int CAR_USER_SVC_NOTIFY_APP_LIFECYCLE_LISTENER = 150121;
    public static final int CAR_USER_SVC_NOTIFY_INTERNAL_LIFECYCLE_LISTENER = 150122;
    public static final int CAR_USER_SVC_POST_SWITCH_USER_REQ = 150108;
    public static final int CAR_USER_SVC_REMOVE_USER_REQ = 150119;
    public static final int CAR_USER_SVC_REMOVE_USER_RESP = 150120;
    public static final int CAR_USER_SVC_RESET_LIFECYCLE_LISTENER = 150105;
    public static final int CAR_USER_SVC_SET_INITIAL_USER = 150103;
    public static final int CAR_USER_SVC_SET_LIFECYCLE_LISTENER = 150104;
    public static final int CAR_USER_SVC_SET_USER_AUTH_REQ = 150113;
    public static final int CAR_USER_SVC_SET_USER_AUTH_RESP = 150114;
    public static final int CAR_USER_SVC_SWITCH_USER_FROM_HAL_REQ = 150112;
    public static final int CAR_USER_SVC_SWITCH_USER_REQ = 150106;
    public static final int CAR_USER_SVC_SWITCH_USER_RESP = 150107;
    public static final int CAR_USER_SVC_SWITCH_USER_UI_REQ = 150111;

    private EventLogTags() {
    }

    public static void writeCarHelperBootPhase(int i) {
        EventLog.writeEvent(CAR_HELPER_BOOT_PHASE, i);
    }

    public static void writeCarHelperHalCreateUser(int i, String str, String str2) {
        EventLog.writeEvent(CAR_HELPER_HAL_CREATE_USER, Integer.valueOf(i), str, str2);
    }

    public static void writeCarHelperHalDefaultBehavior(int i, String str) {
        EventLog.writeEvent(CAR_HELPER_HAL_DEFAULT_BEHAVIOR, Integer.valueOf(i), str);
    }

    public static void writeCarHelperHalRequest(int i) {
        EventLog.writeEvent(CAR_HELPER_HAL_REQUEST, i);
    }

    public static void writeCarHelperHalResponse(int i) {
        EventLog.writeEvent(CAR_HELPER_HAL_RESPONSE, i);
    }

    public static void writeCarHelperHalStartUser(int i, String str) {
        EventLog.writeEvent(CAR_HELPER_HAL_START_USER, Integer.valueOf(i), str);
    }

    public static void writeCarHelperPreCreationRequested(int i, int i2) {
        EventLog.writeEvent(CAR_HELPER_PRE_CREATION_REQUESTED, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static void writeCarHelperPreCreationStatus(int i, int i2, int i3, int i4, int i5, int i6, int i7) {
        EventLog.writeEvent(CAR_HELPER_PRE_CREATION_STATUS, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6), Integer.valueOf(i7));
    }

    public static void writeCarHelperStart(int i) {
        EventLog.writeEvent(CAR_HELPER_START, i);
    }

    public static void writeCarHelperSvcConnected(int i) {
        EventLog.writeEvent(CAR_HELPER_SVC_CONNECTED, i);
    }

    public static void writeCarHelperUserStarting(int i) {
        EventLog.writeEvent(CAR_HELPER_USER_STARTING, i);
    }

    public static void writeCarHelperUserStopped(int i) {
        EventLog.writeEvent(CAR_HELPER_USER_STOPPED, i);
    }

    public static void writeCarHelperUserStopping(int i) {
        EventLog.writeEvent(CAR_HELPER_USER_STOPPING, i);
    }

    public static void writeCarHelperUserSwitching(int i, int i2) {
        EventLog.writeEvent(CAR_HELPER_USER_SWITCHING, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static void writeCarHelperUserUnlocked(int i) {
        EventLog.writeEvent(CAR_HELPER_USER_UNLOCKED, i);
    }

    public static void writeCarHelperUserUnlocking(int i) {
        EventLog.writeEvent(CAR_HELPER_USER_UNLOCKING, i);
    }

    public static void writeCarServiceConnected(String str) {
        EventLog.writeEvent(CAR_SERVICE_CONNECTED, str);
    }

    public static void writeCarServiceCreate(int i) {
        EventLog.writeEvent(CAR_SERVICE_CREATE, i);
    }

    public static void writeCarServiceDestroy(int i) {
        EventLog.writeEvent(CAR_SERVICE_DESTROY, i);
    }

    public static void writeCarServiceInit(int i) {
        EventLog.writeEvent(CAR_SERVICE_INIT, i);
    }

    public static void writeCarServiceOnUserLifecycle(int i, int i2, int i3) {
        EventLog.writeEvent(CAR_SERVICE_ON_USER_LIFECYCLE, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3));
    }

    public static void writeCarServiceSetCarServiceHelper(int i) {
        EventLog.writeEvent(CAR_SERVICE_SET_CAR_SERVICE_HELPER, i);
    }

    public static void writeCarServiceSetInitialUser(int i) {
        EventLog.writeEvent(CAR_SERVICE_SET_INITIAL_USER, i);
    }

    public static void writeCarServiceVhalDied(long j) {
        EventLog.writeEvent(CAR_SERVICE_VHAL_DIED, j);
    }

    public static void writeCarServiceVhalReconnected(int i) {
        EventLog.writeEvent(CAR_SERVICE_VHAL_RECONNECTED, i);
    }

    public static void writeCarUserHalCreateUserReq(int i, String str, int i2, int i3) {
        EventLog.writeEvent(CAR_USER_HAL_CREATE_USER_REQ, Integer.valueOf(i), str, Integer.valueOf(i2), Integer.valueOf(i3));
    }

    public static void writeCarUserHalCreateUserResp(int i, int i2, int i3, String str) {
        EventLog.writeEvent(CAR_USER_HAL_CREATE_USER_RESP, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), str);
    }

    public static void writeCarUserHalGetUserAuthReq(Object[] objArr) {
        EventLog.writeEvent(CAR_USER_HAL_GET_USER_AUTH_REQ, objArr);
    }

    public static void writeCarUserHalGetUserAuthResp(Object[] objArr, String str) {
        EventLog.writeEvent(CAR_USER_HAL_GET_USER_AUTH_RESP, objArr, str);
    }

    public static void writeCarUserHalInitialUserInfoReq(int i, int i2, int i3) {
        EventLog.writeEvent(CAR_USER_HAL_INITIAL_USER_INFO_REQ, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3));
    }

    public static void writeCarUserHalInitialUserInfoResp(int i, int i2, int i3, int i4, int i5, String str, String str2) {
        EventLog.writeEvent(CAR_USER_HAL_INITIAL_USER_INFO_RESP, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5), str, str2);
    }

    public static void writeCarUserHalLegacySwitchUserReq(int i, int i2, int i3) {
        EventLog.writeEvent(CAR_USER_HAL_LEGACY_SWITCH_USER_REQ, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3));
    }

    public static void writeCarUserHalOemSwitchUserReq(int i, int i2) {
        EventLog.writeEvent(CAR_USER_HAL_OEM_SWITCH_USER_REQ, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static void writeCarUserHalPostSwitchUserReq(int i, int i2, int i3) {
        EventLog.writeEvent(CAR_USER_HAL_POST_SWITCH_USER_REQ, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3));
    }

    public static void writeCarUserHalRemoveUserReq(int i, int i2) {
        EventLog.writeEvent(CAR_USER_HAL_REMOVE_USER_REQ, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static void writeCarUserHalSetUserAuthReq(Object[] objArr) {
        EventLog.writeEvent(CAR_USER_HAL_SET_USER_AUTH_REQ, objArr);
    }

    public static void writeCarUserHalSetUserAuthResp(Object[] objArr, String str) {
        EventLog.writeEvent(CAR_USER_HAL_SET_USER_AUTH_RESP, objArr, str);
    }

    public static void writeCarUserHalSwitchUserReq(int i, int i2, int i3, int i4) {
        EventLog.writeEvent(CAR_USER_HAL_SWITCH_USER_REQ, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4));
    }

    public static void writeCarUserHalSwitchUserResp(int i, int i2, int i3, String str) {
        EventLog.writeEvent(CAR_USER_HAL_SWITCH_USER_RESP, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), str);
    }

    public static void writeCarUserMgrAddListener(int i) {
        EventLog.writeEvent(CAR_USER_MGR_ADD_LISTENER, i);
    }

    public static void writeCarUserMgrCreateUserReq(int i, String str, String str2, int i2) {
        EventLog.writeEvent(CAR_USER_MGR_CREATE_USER_REQ, Integer.valueOf(i), str, str2, Integer.valueOf(i2));
    }

    public static void writeCarUserMgrCreateUserResp(int i, int i2, String str) {
        EventLog.writeEvent(CAR_USER_MGR_CREATE_USER_RESP, Integer.valueOf(i), Integer.valueOf(i2), str);
    }

    public static void writeCarUserMgrDisconnected(int i) {
        EventLog.writeEvent(CAR_USER_MGR_DISCONNECTED, i);
    }

    public static void writeCarUserMgrGetUserAuthReq(Object[] objArr) {
        EventLog.writeEvent(CAR_USER_MGR_GET_USER_AUTH_REQ, objArr);
    }

    public static void writeCarUserMgrGetUserAuthResp(Object[] objArr) {
        EventLog.writeEvent(CAR_USER_MGR_GET_USER_AUTH_RESP, objArr);
    }

    public static void writeCarUserMgrNotifyLifecycleListener(int i, int i2, int i3, int i4) {
        EventLog.writeEvent(CAR_USER_MGR_NOTIFY_LIFECYCLE_LISTENER, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4));
    }

    public static void writeCarUserMgrRemoveListener(int i) {
        EventLog.writeEvent(CAR_USER_MGR_REMOVE_LISTENER, i);
    }

    public static void writeCarUserMgrRemoveUserReq(int i, int i2) {
        EventLog.writeEvent(CAR_USER_MGR_REMOVE_USER_REQ, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static void writeCarUserMgrRemoveUserResp(int i, int i2) {
        EventLog.writeEvent(CAR_USER_MGR_REMOVE_USER_RESP, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static void writeCarUserMgrSetUserAuthReq(Object[] objArr) {
        EventLog.writeEvent(CAR_USER_MGR_SET_USER_AUTH_REQ, objArr);
    }

    public static void writeCarUserMgrSetUserAuthResp(Object[] objArr) {
        EventLog.writeEvent(CAR_USER_MGR_SET_USER_AUTH_RESP, objArr);
    }

    public static void writeCarUserMgrSwitchUserReq(int i, int i2) {
        EventLog.writeEvent(CAR_USER_MGR_SWITCH_USER_REQ, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static void writeCarUserMgrSwitchUserResp(int i, int i2, String str) {
        EventLog.writeEvent(CAR_USER_MGR_SWITCH_USER_RESP, Integer.valueOf(i), Integer.valueOf(i2), str);
    }

    public static void writeCarUserSvcCreateUserReq(String str, String str2, int i, int i2) {
        EventLog.writeEvent(CAR_USER_SVC_CREATE_USER_REQ, str, str2, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static void writeCarUserSvcCreateUserResp(int i, int i2, String str) {
        EventLog.writeEvent(CAR_USER_SVC_CREATE_USER_RESP, Integer.valueOf(i), Integer.valueOf(i2), str);
    }

    public static void writeCarUserSvcCreateUserUserCreated(int i, String str, String str2, int i2) {
        EventLog.writeEvent(CAR_USER_SVC_CREATE_USER_USER_CREATED, Integer.valueOf(i), str, str2, Integer.valueOf(i2));
    }

    public static void writeCarUserSvcCreateUserUserRemoved(int i, String str) {
        EventLog.writeEvent(CAR_USER_SVC_CREATE_USER_USER_REMOVED, Integer.valueOf(i), str);
    }

    public static void writeCarUserSvcGetUserAuthReq(int i, int i2, int i3) {
        EventLog.writeEvent(CAR_USER_SVC_GET_USER_AUTH_REQ, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3));
    }

    public static void writeCarUserSvcGetUserAuthResp(int i) {
        EventLog.writeEvent(CAR_USER_SVC_GET_USER_AUTH_RESP, i);
    }

    public static void writeCarUserSvcInitialUserInfoReq(int i, int i2) {
        EventLog.writeEvent(CAR_USER_SVC_INITIAL_USER_INFO_REQ, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static void writeCarUserSvcInitialUserInfoResp(int i, int i2, int i3, int i4, String str, String str2) {
        EventLog.writeEvent(CAR_USER_SVC_INITIAL_USER_INFO_RESP, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), str, str2);
    }

    public static void writeCarUserSvcNotifyAppLifecycleListener(int i, int i2, int i3, int i4) {
        EventLog.writeEvent(CAR_USER_SVC_NOTIFY_APP_LIFECYCLE_LISTENER, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4));
    }

    public static void writeCarUserSvcNotifyInternalLifecycleListener(String str, int i, int i2, int i3) {
        EventLog.writeEvent(CAR_USER_SVC_NOTIFY_INTERNAL_LIFECYCLE_LISTENER, str, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3));
    }

    public static void writeCarUserSvcPostSwitchUserReq(int i, int i2) {
        EventLog.writeEvent(CAR_USER_SVC_POST_SWITCH_USER_REQ, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static void writeCarUserSvcRemoveUserReq(int i) {
        EventLog.writeEvent(CAR_USER_SVC_REMOVE_USER_REQ, i);
    }

    public static void writeCarUserSvcRemoveUserResp(int i, int i2) {
        EventLog.writeEvent(CAR_USER_SVC_REMOVE_USER_RESP, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static void writeCarUserSvcResetLifecycleListener(int i) {
        EventLog.writeEvent(CAR_USER_SVC_RESET_LIFECYCLE_LISTENER, i);
    }

    public static void writeCarUserSvcSetInitialUser(int i) {
        EventLog.writeEvent(CAR_USER_SVC_SET_INITIAL_USER, i);
    }

    public static void writeCarUserSvcSetLifecycleListener(int i) {
        EventLog.writeEvent(CAR_USER_SVC_SET_LIFECYCLE_LISTENER, i);
    }

    public static void writeCarUserSvcSetUserAuthReq(int i, int i2, int i3) {
        EventLog.writeEvent(CAR_USER_SVC_SET_USER_AUTH_REQ, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3));
    }

    public static void writeCarUserSvcSetUserAuthResp(int i, String str) {
        EventLog.writeEvent(CAR_USER_SVC_SET_USER_AUTH_RESP, Integer.valueOf(i), str);
    }

    public static void writeCarUserSvcSwitchUserFromHalReq(int i, int i2) {
        EventLog.writeEvent(CAR_USER_SVC_SWITCH_USER_FROM_HAL_REQ, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static void writeCarUserSvcSwitchUserReq(int i, int i2) {
        EventLog.writeEvent(CAR_USER_SVC_SWITCH_USER_REQ, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static void writeCarUserSvcSwitchUserResp(int i, int i2, String str) {
        EventLog.writeEvent(CAR_USER_SVC_SWITCH_USER_RESP, Integer.valueOf(i), Integer.valueOf(i2), str);
    }

    public static void writeCarUserSvcSwitchUserUiReq(int i) {
        EventLog.writeEvent(CAR_USER_SVC_SWITCH_USER_UI_REQ, i);
    }
}
