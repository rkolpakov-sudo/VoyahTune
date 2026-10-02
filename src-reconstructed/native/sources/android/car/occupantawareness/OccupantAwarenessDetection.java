package android.car.occupantawareness;

import android.os.Parcel;
import android.os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public final class OccupantAwarenessDetection implements Parcelable {
    public static final int CONFIDENCE_LEVEL_HIGH = 2;
    public static final int CONFIDENCE_LEVEL_LOW = 1;
    public static final int CONFIDENCE_LEVEL_MAX = 3;
    public static final int CONFIDENCE_LEVEL_NONE = 0;
    public static final Parcelable.Creator<OccupantAwarenessDetection> CREATOR = new Parcelable.Creator<OccupantAwarenessDetection>() { // from class: android.car.occupantawareness.OccupantAwarenessDetection.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public OccupantAwarenessDetection createFromParcel(Parcel parcel) {
            return new OccupantAwarenessDetection(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public OccupantAwarenessDetection[] newArray(int i) {
            return new OccupantAwarenessDetection[i];
        }
    };
    public static final int VEHICLE_OCCUPANT_ALL_FRONT_OCCUPANTS = 6;
    public static final int VEHICLE_OCCUPANT_ALL_OCCUPANTS = 510;
    public static final int VEHICLE_OCCUPANT_ALL_ROW_2_OCCUPANTS = 56;
    public static final int VEHICLE_OCCUPANT_ALL_ROW_3_OCCUPANTS = 448;
    public static final int VEHICLE_OCCUPANT_DRIVER = 4;
    public static final int VEHICLE_OCCUPANT_FRONT_PASSENGER = 2;
    public static final int VEHICLE_OCCUPANT_NONE = 0;
    public static final int VEHICLE_OCCUPANT_ROW_2_PASSENGER_CENTER = 16;
    public static final int VEHICLE_OCCUPANT_ROW_2_PASSENGER_LEFT = 8;
    public static final int VEHICLE_OCCUPANT_ROW_2_PASSENGER_RIGHT = 32;
    public static final int VEHICLE_OCCUPANT_ROW_3_PASSENGER_CENTER = 128;
    public static final int VEHICLE_OCCUPANT_ROW_3_PASSENGER_LEFT = 64;
    public static final int VEHICLE_OCCUPANT_ROW_3_PASSENGER_RIGHT = 256;
    public final DriverMonitoringDetection driverMonitoringDetection;
    public final GazeDetection gazeDetection;
    public final boolean isPresent;
    public final int role;
    public final long timestampMillis;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ConfidenceLevel {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface VehicleOccupantRole {
    }

    public OccupantAwarenessDetection(int i, long j, boolean z, GazeDetection gazeDetection, DriverMonitoringDetection driverMonitoringDetection) {
        this.role = i;
        this.timestampMillis = j;
        this.isPresent = z;
        this.gazeDetection = gazeDetection;
        this.driverMonitoringDetection = driverMonitoringDetection;
    }

    private OccupantAwarenessDetection(Parcel parcel) {
        this.role = parcel.readInt();
        this.timestampMillis = parcel.readLong();
        this.isPresent = parcel.readBoolean();
        this.gazeDetection = (GazeDetection) parcel.readParcelable(GazeDetection.class.getClassLoader());
        this.driverMonitoringDetection = (DriverMonitoringDetection) parcel.readParcelable(DriverMonitoringDetection.class.getClassLoader());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("OccupantAwarenessDetection{role=");
        sb.append(this.role);
        sb.append(", timestampMillis=");
        sb.append(this.timestampMillis);
        sb.append(", isPresent=");
        sb.append(this.isPresent);
        sb.append(", gazeDetection=");
        GazeDetection gazeDetection = this.gazeDetection;
        sb.append(gazeDetection == null ? "(null)" : gazeDetection.toString());
        sb.append(", driverMonitoringDetection=");
        DriverMonitoringDetection driverMonitoringDetection = this.driverMonitoringDetection;
        sb.append(driverMonitoringDetection != null ? driverMonitoringDetection.toString() : "(null)");
        sb.append("}");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.role);
        parcel.writeLong(this.timestampMillis);
        parcel.writeBoolean(this.isPresent);
        parcel.writeParcelable(this.gazeDetection, i);
        parcel.writeParcelable(this.driverMonitoringDetection, i);
    }
}
