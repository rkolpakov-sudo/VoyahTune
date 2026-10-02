package android.car.hardware.property;

import android.car.hardware.CarPropertyValue;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class CarPropertyEvent implements Parcelable {
    public static final Parcelable.Creator<CarPropertyEvent> CREATOR = new Parcelable.Creator<CarPropertyEvent>() { // from class: android.car.hardware.property.CarPropertyEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CarPropertyEvent createFromParcel(Parcel parcel) {
            return new CarPropertyEvent(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CarPropertyEvent[] newArray(int i) {
            return new CarPropertyEvent[i];
        }
    };
    private static final int ERROR_EVENT_VALUE = -1;
    public static final int PROPERTY_EVENT_ERROR = 1;
    public static final int PROPERTY_EVENT_PROPERTY_CHANGE = 0;
    private final CarPropertyValue<?> mCarPropertyValue;
    private final int mErrorCode;
    private final int mEventType;

    public CarPropertyEvent(int i, CarPropertyValue<?> carPropertyValue) {
        this.mEventType = i;
        this.mErrorCode = 5;
        this.mCarPropertyValue = carPropertyValue;
    }

    public CarPropertyEvent(int i, CarPropertyValue<?> carPropertyValue, int i2) {
        this.mEventType = i;
        this.mErrorCode = i2;
        this.mCarPropertyValue = carPropertyValue;
    }

    private CarPropertyEvent(Parcel parcel) {
        this.mEventType = parcel.readInt();
        this.mErrorCode = parcel.readInt();
        this.mCarPropertyValue = (CarPropertyValue) parcel.readParcelable(CarPropertyValue.class.getClassLoader());
    }

    public static CarPropertyEvent createErrorEventWithErrorCode(int i, int i2, int i3) {
        return new CarPropertyEvent(1, new CarPropertyValue(i, i2, 2, 0L, -1), i3);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public CarPropertyValue<?> getCarPropertyValue() {
        return this.mCarPropertyValue;
    }

    public int getErrorCode() {
        return this.mErrorCode;
    }

    public int getEventType() {
        return this.mEventType;
    }

    public String toString() {
        return "CarPropertyEvent{mEventType=" + this.mEventType + ", mErrorCode=" + this.mErrorCode + ", mCarPropertyValue=" + this.mCarPropertyValue + '}';
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mEventType);
        parcel.writeInt(this.mErrorCode);
        parcel.writeParcelable(this.mCarPropertyValue, i);
    }
}
