package android.car.user;

import android.os.Parcel;
import android.os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public final class UserRemovalResult implements Parcelable {
    public static final Parcelable.Creator<UserRemovalResult> CREATOR = new Parcelable.Creator<UserRemovalResult>() { // from class: android.car.user.UserRemovalResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UserRemovalResult createFromParcel(Parcel parcel) {
            return new UserRemovalResult(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UserRemovalResult[] newArray(int i) {
            return new UserRemovalResult[i];
        }
    };
    public static final int STATUS_ANDROID_FAILURE = 2;
    public static final int STATUS_HAL_INTERNAL_FAILURE = 4;
    public static final int STATUS_SUCCESSFUL = 1;
    public static final int STATUS_SUCCESSFUL_LAST_ADMIN_REMOVED = 103;
    public static final int STATUS_TARGET_USER_IS_CURRENT_USER = 101;
    public static final int STATUS_USER_DOES_NOT_EXIST = 102;
    private final int mStatus;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
    }

    public UserRemovalResult(int i) {
        this.mStatus = i;
        if (i == 1 || i == 2 || i == 4 || i == 101 || i == 102 || i == 103) {
            return;
        }
        throw new IllegalArgumentException("status was " + i + " but must be one of: STATUS_SUCCESSFUL(1), STATUS_ANDROID_FAILURE(2), STATUS_HAL_INTERNAL_FAILURE(4), STATUS_TARGET_USER_IS_CURRENT_USER(101), STATUS_USER_DOES_NOT_EXIST(102), STATUS_SUCCESSFUL_LAST_ADMIN_REMOVED(103)");
    }

    UserRemovalResult(Parcel parcel) {
        int i = parcel.readInt();
        this.mStatus = i;
        if (i == 1 || i == 2 || i == 4 || i == 101 || i == 102 || i == 103) {
            return;
        }
        throw new IllegalArgumentException("status was " + i + " but must be one of: STATUS_SUCCESSFUL(1), STATUS_ANDROID_FAILURE(2), STATUS_HAL_INTERNAL_FAILURE(4), STATUS_TARGET_USER_IS_CURRENT_USER(101), STATUS_USER_DOES_NOT_EXIST(102), STATUS_SUCCESSFUL_LAST_ADMIN_REMOVED(103)");
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
        if (i == 4) {
            return "STATUS_HAL_INTERNAL_FAILURE";
        }
        switch (i) {
            case 101:
                return "STATUS_TARGET_USER_IS_CURRENT_USER";
            case 102:
                return "STATUS_USER_DOES_NOT_EXIST";
            case 103:
                return "STATUS_SUCCESSFUL_LAST_ADMIN_REMOVED";
            default:
                return Integer.toHexString(i);
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public boolean isSuccess() {
        int i = this.mStatus;
        return i == 1 || i == 103;
    }

    public String toString() {
        return "UserRemovalResult { status = " + statusToString(this.mStatus) + " }";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mStatus);
    }
}
