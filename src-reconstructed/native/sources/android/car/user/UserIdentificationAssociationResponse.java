package android.car.user;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import java.util.Arrays;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class UserIdentificationAssociationResponse implements Parcelable {
    public static final Parcelable.Creator<UserIdentificationAssociationResponse> CREATOR = new Parcelable.Creator<UserIdentificationAssociationResponse>() { // from class: android.car.user.UserIdentificationAssociationResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UserIdentificationAssociationResponse createFromParcel(Parcel parcel) {
            return new UserIdentificationAssociationResponse(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UserIdentificationAssociationResponse[] newArray(int i) {
            return new UserIdentificationAssociationResponse[i];
        }
    };
    private final String mErrorMessage;
    private final boolean mSuccess;
    private final int[] mValues;

    UserIdentificationAssociationResponse(Parcel parcel) {
        byte b = parcel.readByte();
        boolean z = (b & 1) != 0;
        String string = (b & 2) == 0 ? null : parcel.readString();
        int[] iArrCreateIntArray = (b & 4) != 0 ? parcel.createIntArray() : null;
        this.mSuccess = z;
        this.mErrorMessage = string;
        this.mValues = iArrCreateIntArray;
    }

    private UserIdentificationAssociationResponse(boolean z, String str, int[] iArr) {
        this.mSuccess = z;
        this.mErrorMessage = str;
        this.mValues = iArr;
    }

    @Deprecated
    private void __metadata() {
    }

    public static UserIdentificationAssociationResponse forFailure() {
        return forFailure(null);
    }

    public static UserIdentificationAssociationResponse forFailure(String str) {
        return new UserIdentificationAssociationResponse(false, str, null);
    }

    public static UserIdentificationAssociationResponse forSuccess(int[] iArr) {
        Preconditions.checkArgument(!ArrayUtils.isEmpty(iArr), "must have at least one value");
        Objects.requireNonNull(iArr);
        return new UserIdentificationAssociationResponse(true, null, iArr);
    }

    public static UserIdentificationAssociationResponse forSuccess(int[] iArr, String str) {
        Preconditions.checkArgument(!ArrayUtils.isEmpty(iArr), "must have at least one value");
        Objects.requireNonNull(iArr);
        return new UserIdentificationAssociationResponse(true, str, iArr);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getErrorMessage() {
        return this.mErrorMessage;
    }

    public int[] getValues() {
        return this.mValues;
    }

    public boolean isSuccess() {
        return this.mSuccess;
    }

    public String toString() {
        return "UserIdentificationAssociationResponse { success = " + this.mSuccess + ", errorMessage = " + this.mErrorMessage + ", values = " + Arrays.toString(this.mValues) + " }";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        byte b = this.mSuccess ? (byte) 1 : (byte) 0;
        if (this.mErrorMessage != null) {
            b = (byte) (b | 2);
        }
        if (this.mValues != null) {
            b = (byte) (b | 4);
        }
        parcel.writeByte(b);
        String str = this.mErrorMessage;
        if (str != null) {
            parcel.writeString(str);
        }
        int[] iArr = this.mValues;
        if (iArr != null) {
            parcel.writeIntArray(iArr);
        }
    }
}
