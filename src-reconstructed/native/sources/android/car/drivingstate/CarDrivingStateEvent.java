package android.car.drivingstate;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class CarDrivingStateEvent implements Parcelable {
    public static final Parcelable.Creator<CarDrivingStateEvent> CREATOR = new Parcelable.Creator<CarDrivingStateEvent>() { // from class: android.car.drivingstate.CarDrivingStateEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CarDrivingStateEvent createFromParcel(Parcel parcel) {
            return new CarDrivingStateEvent(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CarDrivingStateEvent[] newArray(int i) {
            return new CarDrivingStateEvent[i];
        }
    };
    public static final int DRIVING_STATE_IDLING = 1;
    public static final int DRIVING_STATE_MOVING = 2;
    public static final int DRIVING_STATE_PARKED = 0;
    public static final int DRIVING_STATE_UNKNOWN = -1;
    public final int eventValue;
    public final long timeStamp;

    @Retention(RetentionPolicy.SOURCE)
    public @interface CarDrivingState {
    }

    public CarDrivingStateEvent(int i, long j) {
        this.eventValue = i;
        this.timeStamp = j;
    }

    private CarDrivingStateEvent(Parcel parcel) {
        this.eventValue = parcel.readInt();
        this.timeStamp = parcel.readLong();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return this.eventValue + " " + this.timeStamp;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.eventValue);
        parcel.writeLong(this.timeStamp);
    }
}
