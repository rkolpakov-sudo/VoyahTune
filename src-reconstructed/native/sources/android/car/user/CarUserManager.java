package android.car.user;

import android.annotation.SystemApi;
import android.car.Car;
import android.car.CarManagerBase;
import android.car.ICarUserService;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.sysprop.CarProperties;
import android.util.ArrayMap;
import android.util.EventLog;
import android.util.Log;
import com.android.internal.car.EventLogTags;
import com.android.internal.infra.AndroidFuture;
import com.android.internal.os.IResultReceiver;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class CarUserManager extends CarManagerBase {
    public static final String BUNDLE_PARAM_ACTION = "action";
    public static final String BUNDLE_PARAM_PREVIOUS_USER_ID = "previous_user";
    private static final boolean DBG = false;
    private static final int HAL_TIMEOUT_MS = ((Integer) CarProperties.user_hal_timeout().orElse(5000)).intValue();
    private static final String TAG = "CarUserManager";

    @SystemApi
    public static final int USER_LIFECYCLE_EVENT_TYPE_STARTING = 1;

    @SystemApi
    public static final int USER_LIFECYCLE_EVENT_TYPE_STOPPED = 6;

    @SystemApi
    public static final int USER_LIFECYCLE_EVENT_TYPE_STOPPING = 5;

    @SystemApi
    public static final int USER_LIFECYCLE_EVENT_TYPE_SWITCHING = 2;

    @SystemApi
    public static final int USER_LIFECYCLE_EVENT_TYPE_UNLOCKED = 4;

    @SystemApi
    public static final int USER_LIFECYCLE_EVENT_TYPE_UNLOCKING = 3;
    private ArrayMap<UserLifecycleListener, Executor> mListeners;
    private final Object mLock;
    private LifecycleResultReceiver mReceiver;
    private final ICarUserService mService;
    private final UserManager mUserManager;

    /* JADX INFO: Access modifiers changed from: private */
    class LifecycleResultReceiver extends IResultReceiver.Stub {
        final CarUserManager this$0;

        private LifecycleResultReceiver(CarUserManager carUserManager) {
            this.this$0 = carUserManager;
        }

        public void send(int i, Bundle bundle) {
            ArrayMap arrayMap;
            String str;
            String str2;
            if (bundle == null) {
                str = CarUserManager.TAG;
                str2 = "Received result (" + i + ") without data";
            } else {
                int i2 = bundle.getInt(CarUserManager.BUNDLE_PARAM_PREVIOUS_USER_ID, -10000);
                int i3 = bundle.getInt(CarUserManager.BUNDLE_PARAM_ACTION);
                final UserLifecycleEvent userLifecycleEvent = new UserLifecycleEvent(i3, i2, i);
                synchronized (this.this$0.mLock) {
                    arrayMap = this.this$0.mListeners;
                }
                if (arrayMap != null) {
                    int size = arrayMap.size();
                    EventLog.writeEvent(EventLogTags.CAR_USER_MGR_NOTIFY_LIFECYCLE_LISTENER, Integer.valueOf(size), Integer.valueOf(i3), Integer.valueOf(i2), Integer.valueOf(i));
                    for (int i4 = 0; i4 < size; i4++) {
                        final UserLifecycleListener userLifecycleListener = (UserLifecycleListener) arrayMap.keyAt(i4);
                        ((Executor) arrayMap.valueAt(i4)).execute(new Runnable(userLifecycleListener, userLifecycleEvent) { // from class: android.car.user._$$Lambda$CarUserManager$LifecycleResultReceiver$Kgwbj3nfsIZYdLn8tfDQuH3GuGw
                            public final CarUserManager.UserLifecycleListener f$0;
                            public final CarUserManager.UserLifecycleEvent f$1;

                            {
                                this.f$0 = userLifecycleListener;
                                this.f$1 = userLifecycleEvent;
                            }

                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.onEvent(this.f$1);
                            }
                        });
                    }
                    return;
                }
                str = CarUserManager.TAG;
                str2 = "No listeners for event " + userLifecycleEvent;
            }
            Log.w(str, str2);
        }
    }

    @SystemApi
    public static final class UserLifecycleEvent {
        private final int mEventType;
        private final int mPreviousUserId;
        private final int mUserId;

        public UserLifecycleEvent(int i, int i2) {
            this(i, -10000, i2);
        }

        public UserLifecycleEvent(int i, int i2, int i3) {
            this.mEventType = i;
            this.mPreviousUserId = i2;
            this.mUserId = i3;
        }

        public int getEventType() {
            return this.mEventType;
        }

        public UserHandle getPreviousUserHandle() {
            int i = this.mPreviousUserId;
            if (i == -10000) {
                return null;
            }
            return UserHandle.of(i);
        }

        public int getPreviousUserId() {
            return this.mPreviousUserId;
        }

        public UserHandle getUserHandle() {
            return UserHandle.of(this.mUserId);
        }

        public int getUserId() {
            return this.mUserId;
        }

        public String toString() {
            String str;
            StringBuilder sbAppend = new StringBuilder("Event[type=").append(CarUserManager.lifecycleEventTypeToString(this.mEventType));
            if (this.mPreviousUserId != -10000) {
                sbAppend.append(",from=");
                sbAppend.append(this.mPreviousUserId);
                str = ",to=";
            } else {
                str = ",user=";
            }
            sbAppend.append(str);
            sbAppend.append(this.mUserId);
            sbAppend.append(']');
            return sbAppend.toString();
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface UserLifecycleEventType {
    }

    @SystemApi
    public interface UserLifecycleListener {
        void onEvent(UserLifecycleEvent userLifecycleEvent);
    }

    public interface UserSwitchUiCallback {
        void showUserSwitchDialog(int i);
    }

    private final class UserSwitchUiCallbackReceiver extends IResultReceiver.Stub {
        private final UserSwitchUiCallback mUserSwitchUiCallback;
        final CarUserManager this$0;

        UserSwitchUiCallbackReceiver(CarUserManager carUserManager, UserSwitchUiCallback userSwitchUiCallback) {
            this.this$0 = carUserManager;
            this.mUserSwitchUiCallback = userSwitchUiCallback;
        }

        public void send(int i, Bundle bundle) throws RemoteException {
            this.mUserSwitchUiCallback.showUserSwitchDialog(i);
        }
    }

    public CarUserManager(Car car, ICarUserService iCarUserService, UserManager userManager) {
        super(car);
        this.mLock = new Object();
        this.mService = iCarUserService;
        this.mUserManager = userManager;
    }

    public CarUserManager(Car car, IBinder iBinder) {
        this(car, ICarUserService.Stub.asInterface(iBinder), UserManager.get(car.getContext()));
    }

    private boolean isHeadlessSystemUser(int i) {
        return i == 0 && UserManager.isHeadlessSystemUserMode();
    }

    public static String lifecycleEventTypeToString(int i) {
        switch (i) {
            case 1:
                return "STARTING";
            case 2:
                return "SWITCHING";
            case 3:
                return "UNLOCKING";
            case 4:
                return "UNLOCKED";
            case 5:
                return "STOPPING";
            case 6:
                return "STOPPED";
            default:
                return "UNKNOWN-" + i;
        }
    }

    private AndroidFuture<UserSwitchResult> newSwitchResuiltForFailure(int i) {
        AndroidFuture<UserSwitchResult> androidFuture = new AndroidFuture<>();
        androidFuture.complete(new UserSwitchResult(i, null));
        return androidFuture;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onGuestCreated(UserInfo userInfo) {
        Settings.Secure.putStringForUser(getContext().getContentResolver(), "skip_first_use_hints", "1", userInfo.id);
    }

    private static String safeName(String str) {
        if (str == null) {
            return str;
        }
        return str.length() + "_chars";
    }

    /* JADX WARN: Code duplicated, block: B:21:0x0047 A[Catch: all -> 0x0055, TryCatch #0 {, blocks: (B:4:0x0011, B:6:0x0016, B:11:0x0020, B:13:0x0029, B:15:0x0034, B:18:0x0040, B:19:0x0043, B:21:0x0047, B:22:0x004e, B:23:0x0053), top: B:28:0x0011, inners: #1 }] */
    @SystemApi
    public void addListener(Executor executor, UserLifecycleListener userLifecycleListener) {
        Objects.requireNonNull(executor, "executor cannot be null");
        Objects.requireNonNull(userLifecycleListener, "listener cannot be null");
        int iMyUid = Process.myUid();
        synchronized (this.mLock) {
            ArrayMap<UserLifecycleListener, Executor> arrayMap = this.mListeners;
            Preconditions.checkState(arrayMap == null || !arrayMap.containsKey(userLifecycleListener), "already called for this listener");
            if (this.mReceiver == null) {
                this.mReceiver = new LifecycleResultReceiver();
                try {
                    EventLog.writeEvent(EventLogTags.CAR_USER_MGR_ADD_LISTENER, iMyUid);
                    this.mService.setLifecycleListenerForUid(this.mReceiver);
                } catch (RemoteException e) {
                    handleRemoteExceptionFromCarService(e);
                }
                if (this.mListeners == null) {
                    this.mListeners = new ArrayMap<>(1);
                }
                this.mListeners.put(userLifecycleListener, executor);
            } else {
                if (this.mListeners == null) {
                    this.mListeners = new ArrayMap<>(1);
                }
                this.mListeners.put(userLifecycleListener, executor);
            }
            throw th;
        }
    }

    public AndroidFuture<UserCreationResult> createGuest(String str) {
        return createUser(str, "android.os.usertype.full.GUEST", 0);
    }

    public AndroidFuture<UserCreationResult> createUser(String str, int i) {
        return createUser(str, "android.os.usertype.full.SECONDARY", i);
    }

    public AndroidFuture<UserCreationResult> createUser(String str, String str2, int i) {
        int iMyUid = Process.myUid();
        try {
            AndroidFuture<UserCreationResult> androidFuture = new AndroidFuture<UserCreationResult>(this, iMyUid, str2, i) { // from class: android.car.user.CarUserManager.2
                final CarUserManager this$0;
                final int val$flags;
                final int val$uid;
                final String val$userType;

                {
                    this.this$0 = this;
                    this.val$uid = iMyUid;
                    this.val$userType = str2;
                    this.val$flags = i;
                }

                /* JADX INFO: Access modifiers changed from: protected */
                public void onCompleted(UserCreationResult userCreationResult, Throwable th) {
                    if (userCreationResult != null) {
                        EventLog.writeEvent(EventLogTags.CAR_USER_MGR_CREATE_USER_RESP, Integer.valueOf(this.val$uid), Integer.valueOf(userCreationResult.getStatus()), userCreationResult.getErrorMessage());
                        UserInfo user = userCreationResult.getUser();
                        if (userCreationResult.isSuccess() && user != null && user.isGuest()) {
                            this.this$0.onGuestCreated(user);
                        }
                    } else {
                        Log.w(CarUserManager.TAG, "createUser(" + this.val$userType + "," + UserInfo.flagsToString(this.val$flags) + ") failed: " + th);
                    }
                    super.onCompleted(userCreationResult, th);
                }
            };
            EventLog.writeEvent(EventLogTags.CAR_USER_MGR_CREATE_USER_REQ, Integer.valueOf(iMyUid), safeName(str), str2, Integer.valueOf(i));
            this.mService.createUser(str, str2, i, HAL_TIMEOUT_MS, androidFuture);
            return androidFuture;
        } catch (RemoteException e) {
            AndroidFuture androidFuture2 = new AndroidFuture();
            androidFuture2.complete(new UserCreationResult(4, null, null));
            return (AndroidFuture) handleRemoteExceptionFromCarService(e, androidFuture2);
        }
    }

    public UserIdentificationAssociationResponse getUserIdentificationAssociation(int... iArr) {
        Preconditions.checkArgument(!ArrayUtils.isEmpty(iArr), "must have at least one type");
        EventLog.writeEvent(EventLogTags.CAR_USER_MGR_GET_USER_AUTH_REQ, iArr.length);
        try {
            UserIdentificationAssociationResponse userIdentificationAssociation = this.mService.getUserIdentificationAssociation(iArr);
            if (userIdentificationAssociation != null) {
                int[] values = userIdentificationAssociation.getValues();
                EventLog.writeEvent(EventLogTags.CAR_USER_MGR_GET_USER_AUTH_RESP, values != null ? values.length : 0);
            }
            return userIdentificationAssociation;
        } catch (RemoteException e) {
            return (UserIdentificationAssociationResponse) handleRemoteExceptionFromCarService(e, null);
        }
    }

    public boolean isUserHalUserAssociationSupported() {
        try {
            return this.mService.isUserHalUserAssociationSupported();
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
        }
    }

    public boolean isValidUser(int i) {
        List users = this.mUserManager.getUsers(true);
        for (int i2 = 0; i2 < users.size(); i2++) {
            if (((UserInfo) users.get(i2)).id == i && (i != 0 || !UserManager.isHeadlessSystemUserMode())) {
                return true;
            }
        }
        return false;
    }

    ExperimentalCarUserManager newExperimentalCarUserManager() {
        return new ExperimentalCarUserManager(this.mCar, this.mService);
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
    }

    @SystemApi
    public void removeListener(UserLifecycleListener userLifecycleListener) {
        Objects.requireNonNull(userLifecycleListener, "listener cannot be null");
        int iMyUid = Process.myUid();
        synchronized (this.mLock) {
            ArrayMap<UserLifecycleListener, Executor> arrayMap = this.mListeners;
            Preconditions.checkState(arrayMap != null && arrayMap.containsKey(userLifecycleListener), "not called for this listener yet");
            this.mListeners.remove(userLifecycleListener);
            if (this.mListeners.isEmpty()) {
                this.mListeners = null;
                if (this.mReceiver != null) {
                    EventLog.writeEvent(EventLogTags.CAR_USER_MGR_REMOVE_LISTENER, iMyUid);
                    try {
                        this.mService.resetLifecycleListenerForUid();
                        this.mReceiver = null;
                    } catch (RemoteException e) {
                        handleRemoteExceptionFromCarService(e);
                    }
                    return;
                }
                Log.wtf(TAG, "removeListener(): receiver already null");
            }
        }
    }

    public UserRemovalResult removeUser(int i) {
        int iMyUid = Process.myUid();
        EventLog.writeEvent(EventLogTags.CAR_USER_MGR_REMOVE_USER_REQ, Integer.valueOf(iMyUid), Integer.valueOf(i));
        try {
            try {
                UserRemovalResult userRemovalResultRemoveUser = this.mService.removeUser(i);
                EventLog.writeEvent(EventLogTags.CAR_USER_MGR_REMOVE_USER_RESP, Integer.valueOf(iMyUid), Integer.valueOf(userRemovalResultRemoveUser.getStatus()));
                return userRemovalResultRemoveUser;
            } catch (RemoteException e) {
                UserRemovalResult userRemovalResult = (UserRemovalResult) handleRemoteExceptionFromCarService(e, new UserRemovalResult(4));
                EventLog.writeEvent(EventLogTags.CAR_USER_MGR_REMOVE_USER_RESP, Integer.valueOf(iMyUid), 4);
                return userRemovalResult;
            }
        } catch (Throwable th) {
            EventLog.writeEvent(EventLogTags.CAR_USER_MGR_REMOVE_USER_RESP, Integer.valueOf(iMyUid), 4);
            throw th;
        }
    }

    public AndroidFuture<UserIdentificationAssociationResponse> setUserIdentificationAssociation(int[] iArr, int[] iArr2) {
        Preconditions.checkArgument(!ArrayUtils.isEmpty(iArr), "must have at least one type");
        Preconditions.checkArgument(!ArrayUtils.isEmpty(iArr2), "must have at least one value");
        if (iArr.length != iArr2.length) {
            throw new IllegalArgumentException("types (" + Arrays.toString(iArr) + ") and values (" + Arrays.toString(iArr2) + ") should have the same length");
        }
        Integer[] numArr = new Integer[iArr.length * 2];
        for (int i = 0; i < iArr.length; i++) {
            int i2 = i * 2;
            numArr[i2] = Integer.valueOf(iArr[i]);
            numArr[i2 + 1] = Integer.valueOf(iArr2[i]);
        }
        EventLog.writeEvent(EventLogTags.CAR_USER_MGR_SET_USER_AUTH_REQ, numArr);
        try {
            AndroidFuture<UserIdentificationAssociationResponse> androidFuture = new AndroidFuture<UserIdentificationAssociationResponse>(this, iArr, iArr2) { // from class: android.car.user.CarUserManager.3
                final CarUserManager this$0;
                final int[] val$types;
                final int[] val$values;

                {
                    this.this$0 = this;
                    this.val$types = iArr;
                    this.val$values = iArr2;
                }

                /* JADX INFO: Access modifiers changed from: protected */
                public void onCompleted(UserIdentificationAssociationResponse userIdentificationAssociationResponse, Throwable th) {
                    if (userIdentificationAssociationResponse != null) {
                        int[] values = userIdentificationAssociationResponse.getValues();
                        if (values != null) {
                            Object[] objArr = new Object[values.length];
                            for (int i3 = 0; i3 < values.length; i3++) {
                                objArr[i3] = Integer.valueOf(values[i3]);
                            }
                            EventLog.writeEvent(EventLogTags.CAR_USER_MGR_SET_USER_AUTH_RESP, objArr);
                        }
                    } else {
                        Log.w(CarUserManager.TAG, "setUserIdentificationAssociation(" + Arrays.toString(this.val$types) + ", " + Arrays.toString(this.val$values) + ") failed: " + th);
                    }
                    super.onCompleted(userIdentificationAssociationResponse, th);
                }
            };
            this.mService.setUserIdentificationAssociation(HAL_TIMEOUT_MS, iArr, iArr2, androidFuture);
            return androidFuture;
        } catch (RemoteException e) {
            AndroidFuture androidFuture2 = new AndroidFuture();
            androidFuture2.complete(UserIdentificationAssociationResponse.forFailure());
            return (AndroidFuture) handleRemoteExceptionFromCarService(e, androidFuture2);
        }
    }

    public void setUserSwitchUiCallback(UserSwitchUiCallback userSwitchUiCallback) {
        Preconditions.checkArgument(userSwitchUiCallback != null, "Null callback");
        try {
            this.mService.setUserSwitchUiCallback(new UserSwitchUiCallbackReceiver(this, userSwitchUiCallback));
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    public AndroidFuture<UserSwitchResult> switchUser(int i) {
        int iMyUid = Process.myUid();
        if (this.mUserManager.getUserSwitchability() != 0) {
            return newSwitchResuiltForFailure(104);
        }
        try {
            AndroidFuture<UserSwitchResult> androidFuture = new AndroidFuture<UserSwitchResult>(this, iMyUid, i) { // from class: android.car.user.CarUserManager.1
                final CarUserManager this$0;
                final int val$targetUserId;
                final int val$uid;

                {
                    this.this$0 = this;
                    this.val$uid = iMyUid;
                    this.val$targetUserId = i;
                }

                /* JADX INFO: Access modifiers changed from: protected */
                public void onCompleted(UserSwitchResult userSwitchResult, Throwable th) {
                    if (userSwitchResult != null) {
                        EventLog.writeEvent(EventLogTags.CAR_USER_MGR_SWITCH_USER_RESP, Integer.valueOf(this.val$uid), Integer.valueOf(userSwitchResult.getStatus()), userSwitchResult.getErrorMessage());
                    } else {
                        Log.w(CarUserManager.TAG, "switchUser(" + this.val$targetUserId + ") failed: " + th);
                    }
                    super.onCompleted(userSwitchResult, th);
                }
            };
            EventLog.writeEvent(EventLogTags.CAR_USER_MGR_SWITCH_USER_REQ, Integer.valueOf(iMyUid), Integer.valueOf(i));
            this.mService.switchUser(i, HAL_TIMEOUT_MS, androidFuture);
            return androidFuture;
        } catch (RemoteException e) {
            return (AndroidFuture) handleRemoteExceptionFromCarService(e, newSwitchResuiltForFailure(4));
        }
    }
}
