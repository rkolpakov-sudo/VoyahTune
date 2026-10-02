package android.car.user;

import android.os.Parcel;
import android.os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public final class UserSwitchResult implements Parcelable {
    public static final Parcelable.Creator<UserSwitchResult> CREATOR = new Parcelable.Creator<UserSwitchResult>() { // from class: android.car.user.UserSwitchResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UserSwitchResult createFromParcel(Parcel parcel) {
            return new UserSwitchResult(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UserSwitchResult[] newArray(int i) {
            return new UserSwitchResult[i];
        }
    };
    public static final int STATUS_ANDROID_FAILURE = 2;
    public static final int STATUS_HAL_FAILURE = 3;
    public static final int STATUS_HAL_INTERNAL_FAILURE = 4;
    public static final int STATUS_INVALID_REQUEST = 5;
    public static final int STATUS_NOT_SWITCHABLE = 104;
    public static final int STATUS_OK_USER_ALREADY_IN_FOREGROUND = 101;
    public static final int STATUS_SUCCESSFUL = 1;
    public static final int STATUS_TARGET_USER_ABANDONED_DUE_TO_A_NEW_REQUEST = 103;
    public static final int STATUS_TARGET_USER_ALREADY_BEING_SWITCHED_TO = 102;
    private final String mErrorMessage;
    private final int mStatus;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
    }

    public UserSwitchResult(int i, String str) {
        this.mStatus = i;
        if (i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 101 || i == 102 || i == 103 || i == 104) {
            this.mErrorMessage = str;
            return;
        }
        throw new IllegalArgumentException("status was " + i + " but must be one of: STATUS_SUCCESSFUL(1), STATUS_ANDROID_FAILURE(2), STATUS_HAL_FAILURE(3), STATUS_HAL_INTERNAL_FAILURE(4), STATUS_INVALID_REQUEST(5), STATUS_OK_USER_ALREADY_IN_FOREGROUND(101), STATUS_TARGET_USER_ALREADY_BEING_SWITCHED_TO(102), STATUS_TARGET_USER_ABANDONED_DUE_TO_A_NEW_REQUEST(103), STATUS_NOT_SWITCHABLE(104)");
    }

    UserSwitchResult(Parcel parcel) {
        byte b = parcel.readByte();
        int i = parcel.readInt();
        String string = (b & 2) == 0 ? null : parcel.readString();
        this.mStatus = i;
        if (i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 101 || i == 102 || i == 103 || i == 104) {
            this.mErrorMessage = string;
            return;
        }
        throw new IllegalArgumentException("status was " + i + " but must be one of: STATUS_SUCCESSFUL(1), STATUS_ANDROID_FAILURE(2), STATUS_HAL_FAILURE(3), STATUS_HAL_INTERNAL_FAILURE(4), STATUS_INVALID_REQUEST(5), STATUS_OK_USER_ALREADY_IN_FOREGROUND(101), STATUS_TARGET_USER_ALREADY_BEING_SWITCHED_TO(102), STATUS_TARGET_USER_ABANDONED_DUE_TO_A_NEW_REQUEST(103), STATUS_NOT_SWITCHABLE(104)");
    }

    @Deprecated
    private void __metadata() {
    }

    public static String statusToString(int i) {
        if (i == 1) {
            return "STATUS_SUCCESSFUL";
        }
        if (i == 2) {
            return "STATUS_ANDROID_FAILURE";
        }
        if (i == 3) {
            return "STATUS_HAL_FAILURE";
        }
        if (i == 4) {
            return "STATUS_HAL_INTERNAL_FAILURE";
        }
        if (i == 5) {
            return "STATUS_INVALID_REQUEST";
        }
        switch (i) {
            case 101:
                return "STATUS_OK_USER_ALREADY_IN_FOREGROUND";
            case 102:
                return "STATUS_TARGET_USER_ALREADY_BEING_SWITCHED_TO";
            case 103:
                return "STATUS_TARGET_USER_ABANDONED_DUE_TO_A_NEW_REQUEST";
            case 104:
                return "STATUS_NOT_SWITCHABLE";
            default:
                return Integer.toHexString(i);
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getErrorMessage() {
        return this.mErrorMessage;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public boolean isSuccess() {
        int i = this.mStatus;
        return i == 1 || i == 101;
    }

    public String toString() {
        return "UserSwitchResult { status = " + statusToString(this.mStatus) + ", errorMessage = " + this.mErrorMessage + " }";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte(this.mErrorMessage != null ? (byte) 2 : (byte) 0);
        parcel.writeInt(this.mStatus);
        String str = this.mErrorMessage;
        if (str != null) {
            parcel.writeString(str);
        }
    }
}
