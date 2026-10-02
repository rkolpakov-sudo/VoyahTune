package android.car.trust;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
@Deprecated
public final class TrustedDeviceInfo implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() { // from class: android.car.trust.TrustedDeviceInfo.1
        @Override // android.os.Parcelable.Creator
        public TrustedDeviceInfo createFromParcel(Parcel parcel) {
            return new TrustedDeviceInfo(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public TrustedDeviceInfo[] newArray(int i) {
            return new TrustedDeviceInfo[i];
        }
    };
    public static final String DEFAULT_NAME = "Default";
    private static final String DEVICE_INFO_DELIMITER = ",";
    private final String mAddress;
    private final long mHandle;
    private final String mName;

    public TrustedDeviceInfo(long j, String str, String str2) {
        this.mHandle = j;
        this.mAddress = str;
        this.mName = str2;
    }

    public TrustedDeviceInfo(Parcel parcel) {
        this.mHandle = parcel.readLong();
        this.mName = parcel.readString();
        this.mAddress = parcel.readString();
    }

    public static TrustedDeviceInfo deserialize(String str) {
        String[] strArrSplit = str.split(DEVICE_INFO_DELIMITER);
        return new TrustedDeviceInfo(Long.valueOf(strArrSplit[0]).longValue(), strArrSplit[1], strArrSplit[2]);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        return (obj instanceof TrustedDeviceInfo) && this.mHandle == ((TrustedDeviceInfo) obj).getHandle();
    }

    public String getAddress() {
        return this.mAddress;
    }

    public long getHandle() {
        return this.mHandle;
    }

    public String getName() {
        return this.mName;
    }

    public int hashCode() {
        return Objects.hash(Long.valueOf(this.mHandle));
    }

    public String serialize() {
        return String.join(DEVICE_INFO_DELIMITER, String.valueOf(this.mHandle), this.mAddress, this.mName);
    }

    public String toString() {
        return String.format("TrustedDevice{ handle=%d. address=%s, name=%s }", Long.valueOf(this.mHandle), this.mAddress, this.mName);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.mHandle);
        parcel.writeString(this.mName);
        parcel.writeString(this.mAddress);
    }
}
