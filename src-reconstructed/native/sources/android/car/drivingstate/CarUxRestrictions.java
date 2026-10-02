package android.car.drivingstate;

import android.os.Parcel;
import android.os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public final class CarUxRestrictions implements Parcelable {
    public static final Parcelable.Creator<CarUxRestrictions> CREATOR = new Parcelable.Creator<CarUxRestrictions>() { // from class: android.car.drivingstate.CarUxRestrictions.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CarUxRestrictions createFromParcel(Parcel parcel) {
            return new CarUxRestrictions(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CarUxRestrictions[] newArray(int i) {
            return new CarUxRestrictions[i];
        }
    };
    private static final int DEFAULT_MAX_CONTENT_DEPTH = 3;
    private static final int DEFAULT_MAX_CUMULATIVE_ITEMS = 21;
    private static final int DEFAULT_MAX_LENGTH = 120;
    public static final int UX_RESTRICTIONS_BASELINE = 0;
    public static final int UX_RESTRICTIONS_FULLY_RESTRICTED = 511;
    public static final int UX_RESTRICTIONS_LIMIT_CONTENT = 32;
    public static final int UX_RESTRICTIONS_LIMIT_STRING_LENGTH = 4;
    public static final int UX_RESTRICTIONS_NO_DIALPAD = 1;
    public static final int UX_RESTRICTIONS_NO_FILTERING = 2;
    public static final int UX_RESTRICTIONS_NO_KEYBOARD = 8;
    public static final int UX_RESTRICTIONS_NO_SETUP = 64;
    public static final int UX_RESTRICTIONS_NO_TEXT_MESSAGE = 128;
    public static final int UX_RESTRICTIONS_NO_VIDEO = 16;
    public static final int UX_RESTRICTIONS_NO_VOICE_TRANSCRIPTION = 256;
    private final int mActiveRestrictions;
    private final int mMaxContentDepth;
    private final int mMaxCumulativeContentItems;
    private final int mMaxStringLength;
    private final boolean mRequiresDistractionOptimization;
    private final long mTimeStamp;

    public static class Builder {
        private final int mActiveRestrictions;
        private final boolean mRequiresDistractionOptimization;
        private final long mTimeStamp;
        private int mMaxStringLength = CarUxRestrictions.DEFAULT_MAX_LENGTH;
        private int mMaxCumulativeContentItems = 21;
        private int mMaxContentDepth = 3;

        public Builder(boolean z, int i, long j) {
            this.mRequiresDistractionOptimization = z;
            this.mActiveRestrictions = i;
            this.mTimeStamp = j;
        }

        public CarUxRestrictions build() {
            return new CarUxRestrictions(this);
        }

        public Builder setMaxContentDepth(int i) {
            this.mMaxContentDepth = i;
            return this;
        }

        public Builder setMaxCumulativeContentItems(int i) {
            this.mMaxCumulativeContentItems = i;
            return this;
        }

        public Builder setMaxStringLength(int i) {
            this.mMaxStringLength = i;
            return this;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface CarUxRestrictionsInfo {
    }

    private CarUxRestrictions(Builder builder) {
        this.mTimeStamp = builder.mTimeStamp;
        this.mActiveRestrictions = builder.mActiveRestrictions;
        this.mRequiresDistractionOptimization = builder.mRequiresDistractionOptimization;
        this.mMaxStringLength = builder.mMaxStringLength;
        this.mMaxCumulativeContentItems = builder.mMaxCumulativeContentItems;
        this.mMaxContentDepth = builder.mMaxContentDepth;
    }

    public CarUxRestrictions(CarUxRestrictions carUxRestrictions) {
        this.mTimeStamp = carUxRestrictions.getTimeStamp();
        this.mRequiresDistractionOptimization = carUxRestrictions.isRequiresDistractionOptimization();
        this.mActiveRestrictions = carUxRestrictions.getActiveRestrictions();
        this.mMaxStringLength = carUxRestrictions.mMaxStringLength;
        this.mMaxCumulativeContentItems = carUxRestrictions.mMaxCumulativeContentItems;
        this.mMaxContentDepth = carUxRestrictions.mMaxContentDepth;
    }

    private CarUxRestrictions(Parcel parcel) {
        this.mActiveRestrictions = parcel.readInt();
        this.mTimeStamp = parcel.readLong();
        this.mRequiresDistractionOptimization = parcel.readInt() != 0;
        this.mMaxStringLength = parcel.readInt();
        this.mMaxCumulativeContentItems = parcel.readInt();
        this.mMaxContentDepth = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getActiveRestrictions() {
        return this.mActiveRestrictions;
    }

    public int getMaxContentDepth() {
        return this.mMaxContentDepth;
    }

    public int getMaxCumulativeContentItems() {
        return this.mMaxCumulativeContentItems;
    }

    public int getMaxRestrictedStringLength() {
        return this.mMaxStringLength;
    }

    public long getTimeStamp() {
        return this.mTimeStamp;
    }

    public boolean isRequiresDistractionOptimization() {
        return this.mRequiresDistractionOptimization;
    }

    public boolean isSameRestrictions(CarUxRestrictions carUxRestrictions) {
        if (carUxRestrictions == null) {
            return false;
        }
        if (carUxRestrictions == this) {
            return true;
        }
        return carUxRestrictions.mRequiresDistractionOptimization == this.mRequiresDistractionOptimization && carUxRestrictions.mActiveRestrictions == this.mActiveRestrictions;
    }

    public String toString() {
        return "DO: " + this.mRequiresDistractionOptimization + " UxR: " + this.mActiveRestrictions + " time: " + this.mTimeStamp;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mActiveRestrictions);
        parcel.writeLong(this.mTimeStamp);
        parcel.writeInt(this.mRequiresDistractionOptimization ? 1 : 0);
        parcel.writeInt(this.mMaxStringLength);
        parcel.writeInt(this.mMaxCumulativeContentItems);
        parcel.writeInt(this.mMaxContentDepth);
    }
}
