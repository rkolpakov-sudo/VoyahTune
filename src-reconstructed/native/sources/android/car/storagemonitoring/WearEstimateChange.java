package android.car.storagemonitoring;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import java.time.Instant;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class WearEstimateChange implements Parcelable {
    public static final Parcelable.Creator<WearEstimateChange> CREATOR = new Parcelable.Creator<WearEstimateChange>() { // from class: android.car.storagemonitoring.WearEstimateChange.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WearEstimateChange createFromParcel(Parcel parcel) {
            return new WearEstimateChange(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WearEstimateChange[] newArray(int i) {
            return new WearEstimateChange[i];
        }
    };
    public final Instant dateAtChange;
    public final boolean isAcceptableDegradation;
    public final WearEstimate newEstimate;
    public final WearEstimate oldEstimate;
    public final long uptimeAtChange;

    public WearEstimateChange(WearEstimate wearEstimate, WearEstimate wearEstimate2, long j, Instant instant, boolean z) {
        if (j < 0) {
            throw new IllegalArgumentException("uptimeAtChange must be >= 0");
        }
        Objects.requireNonNull(wearEstimate);
        this.oldEstimate = wearEstimate;
        Objects.requireNonNull(wearEstimate2);
        this.newEstimate = wearEstimate2;
        this.uptimeAtChange = j;
        Objects.requireNonNull(instant);
        this.dateAtChange = instant;
        this.isAcceptableDegradation = z;
    }

    public WearEstimateChange(Parcel parcel) {
        this.oldEstimate = (WearEstimate) parcel.readParcelable(WearEstimate.class.getClassLoader());
        this.newEstimate = (WearEstimate) parcel.readParcelable(WearEstimate.class.getClassLoader());
        this.uptimeAtChange = parcel.readLong();
        this.dateAtChange = Instant.ofEpochMilli(parcel.readLong());
        this.isAcceptableDegradation = parcel.readInt() == 1;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj instanceof WearEstimateChange) {
            WearEstimateChange wearEstimateChange = (WearEstimateChange) obj;
            if (wearEstimateChange.isAcceptableDegradation == this.isAcceptableDegradation && wearEstimateChange.uptimeAtChange == this.uptimeAtChange && wearEstimateChange.dateAtChange.equals(this.dateAtChange) && wearEstimateChange.oldEstimate.equals(this.oldEstimate) && wearEstimateChange.newEstimate.equals(this.newEstimate)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.oldEstimate, this.newEstimate, Long.valueOf(this.uptimeAtChange), this.dateAtChange, Boolean.valueOf(this.isAcceptableDegradation));
    }

    public String toString() {
        return String.format("wear change{old level=%s, new level=%s, uptime=%d, date=%s, acceptable=%s}", this.oldEstimate, this.newEstimate, Long.valueOf(this.uptimeAtChange), this.dateAtChange, this.isAcceptableDegradation ? "yes" : "no");
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.oldEstimate, i);
        parcel.writeParcelable(this.newEstimate, i);
        parcel.writeLong(this.uptimeAtChange);
        parcel.writeLong(this.dateAtChange.toEpochMilli());
        parcel.writeInt(this.isAcceptableDegradation ? 1 : 0);
    }
}
