package android.car.occupantawareness;

import android.os.Parcel;
import android.os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public final class SystemStatusEvent implements Parcelable {
    public static final Parcelable.Creator<SystemStatusEvent> CREATOR = new Parcelable.Creator<SystemStatusEvent>() { // from class: android.car.occupantawareness.SystemStatusEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SystemStatusEvent createFromParcel(Parcel parcel) {
            return new SystemStatusEvent(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SystemStatusEvent[] newArray(int i) {
            return new SystemStatusEvent[i];
        }
    };
    public static final int DETECTION_TYPE_DRIVER_MONITORING = 4;
    public static final int DETECTION_TYPE_GAZE = 2;
    public static final int DETECTION_TYPE_NONE = 0;
    public static final int DETECTION_TYPE_PRESENCE = 1;
    public static final int SYSTEM_STATUS_NOT_READY = 2;
    public static final int SYSTEM_STATUS_NOT_SUPPORTED = 1;
    public static final int SYSTEM_STATUS_READY = 0;
    public static final int SYSTEM_STATUS_SYSTEM_FAILURE = 3;
    public final int detectionType;
    public final int systemStatus;

    @Retention(RetentionPolicy.SOURCE)
    public @interface DetectionTypeFlags {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface SystemStatus {
    }

    public SystemStatusEvent() {
        this.systemStatus = 2;
        this.detectionType = 0;
    }

    public SystemStatusEvent(int i, int i2) {
        this.systemStatus = i;
        this.detectionType = i2;
    }

    private SystemStatusEvent(Parcel parcel) {
        this.systemStatus = parcel.readInt();
        this.detectionType = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "SystemStatusEvent{systemStatus=" + this.systemStatus + ", detectionType=" + this.detectionType + "}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.systemStatus);
        parcel.writeInt(this.detectionType);
    }
}
