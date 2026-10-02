package android.car.occupantawareness;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public final class DriverMonitoringDetection implements Parcelable {
    public static final Parcelable.Creator<DriverMonitoringDetection> CREATOR = new Parcelable.Creator<DriverMonitoringDetection>() { // from class: android.car.occupantawareness.DriverMonitoringDetection.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DriverMonitoringDetection createFromParcel(Parcel parcel) {
            return new DriverMonitoringDetection(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DriverMonitoringDetection[] newArray(int i) {
            return new DriverMonitoringDetection[i];
        }
    };
    public final int confidenceLevel;
    public final long gazeDurationMillis;
    public final boolean isLookingOnRoad;

    public DriverMonitoringDetection() {
        this.confidenceLevel = 0;
        this.isLookingOnRoad = false;
        this.gazeDurationMillis = 0L;
    }

    public DriverMonitoringDetection(int i, boolean z, long j) {
        this.confidenceLevel = i;
        this.isLookingOnRoad = z;
        this.gazeDurationMillis = j;
    }

    private DriverMonitoringDetection(Parcel parcel) {
        this.confidenceLevel = parcel.readInt();
        this.isLookingOnRoad = parcel.readBoolean();
        this.gazeDurationMillis = parcel.readLong();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "DriverMonitoringDetection{confidenceLevel=" + this.confidenceLevel + ", isLookingOnRoad=" + this.isLookingOnRoad + ", gazeDurationMillis=" + this.gazeDurationMillis;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.confidenceLevel);
        parcel.writeBoolean(this.isLookingOnRoad);
        parcel.writeLong(this.gazeDurationMillis);
    }
}
