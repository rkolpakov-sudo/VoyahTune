package android.car.settings;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public final class SpeedBumpConfiguration implements Parcelable {
    public static final Parcelable.Creator<SpeedBumpConfiguration> CREATOR = new Parcelable.Creator<SpeedBumpConfiguration>() { // from class: android.car.settings.SpeedBumpConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SpeedBumpConfiguration createFromParcel(Parcel parcel) {
            return new SpeedBumpConfiguration(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SpeedBumpConfiguration[] newArray(int i) {
            return new SpeedBumpConfiguration[i];
        }
    };
    private final double mAcquiredPermitsPerSecond;
    private final double mMaxPermitPool;
    private final long mPermitFillDelay;

    public SpeedBumpConfiguration(double d, double d2, long j) {
        this.mAcquiredPermitsPerSecond = d;
        this.mMaxPermitPool = d2;
        this.mPermitFillDelay = j;
    }

    private SpeedBumpConfiguration(Parcel parcel) {
        this.mAcquiredPermitsPerSecond = parcel.readDouble();
        this.mMaxPermitPool = parcel.readDouble();
        this.mPermitFillDelay = parcel.readLong();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SpeedBumpConfiguration)) {
            return false;
        }
        SpeedBumpConfiguration speedBumpConfiguration = (SpeedBumpConfiguration) obj;
        return this.mAcquiredPermitsPerSecond == speedBumpConfiguration.getAcquiredPermitsPerSecond() && this.mMaxPermitPool == speedBumpConfiguration.getMaxPermitPool() && this.mPermitFillDelay == speedBumpConfiguration.getPermitFillDelay();
    }

    public double getAcquiredPermitsPerSecond() {
        return this.mAcquiredPermitsPerSecond;
    }

    public double getMaxPermitPool() {
        return this.mMaxPermitPool;
    }

    public long getPermitFillDelay() {
        return this.mPermitFillDelay;
    }

    public int hashCode() {
        return Objects.hash(Double.valueOf(this.mAcquiredPermitsPerSecond), Double.valueOf(this.mMaxPermitPool), Long.valueOf(this.mPermitFillDelay));
    }

    public String toString() {
        return String.format("[acquired_permits_per_second: %f, max_permit_pool: %f, permit_fill_delay: %d]", Double.valueOf(this.mAcquiredPermitsPerSecond), Double.valueOf(this.mMaxPermitPool), Long.valueOf(this.mPermitFillDelay));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(this.mAcquiredPermitsPerSecond);
        parcel.writeDouble(this.mMaxPermitPool);
        parcel.writeLong(this.mPermitFillDelay);
    }
}
