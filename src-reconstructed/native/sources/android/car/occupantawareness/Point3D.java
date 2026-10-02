package android.car.occupantawareness;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public final class Point3D implements Parcelable {
    public static final Parcelable.Creator<Point3D> CREATOR = new Parcelable.Creator<Point3D>() { // from class: android.car.occupantawareness.Point3D.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Point3D createFromParcel(Parcel parcel) {
            return new Point3D(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Point3D[] newArray(int i) {
            return new Point3D[i];
        }
    };
    public final double x;
    public final double y;
    public final double z;

    public Point3D(double d, double d2, double d3) {
        this.x = d;
        this.y = d2;
        this.z = d3;
    }

    private Point3D(Parcel parcel) {
        this.x = parcel.readDouble();
        this.y = parcel.readDouble();
        this.z = parcel.readDouble();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return String.format("%f, %f, %f", Double.valueOf(this.x), Double.valueOf(this.y), Double.valueOf(this.z));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(this.x);
        parcel.writeDouble(this.y);
        parcel.writeDouble(this.z);
    }
}
