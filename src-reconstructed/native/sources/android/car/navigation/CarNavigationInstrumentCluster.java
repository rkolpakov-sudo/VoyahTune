package android.car.navigation;

import android.annotation.SystemApi;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class CarNavigationInstrumentCluster implements Parcelable {
    public static final int CLUSTER_TYPE_CUSTOM_IMAGES_SUPPORTED = 1;
    public static final int CLUSTER_TYPE_IMAGE_CODES_ONLY = 2;
    public static final Parcelable.Creator<CarNavigationInstrumentCluster> CREATOR = new Parcelable.Creator<CarNavigationInstrumentCluster>() { // from class: android.car.navigation.CarNavigationInstrumentCluster.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CarNavigationInstrumentCluster createFromParcel(Parcel parcel) {
            return new CarNavigationInstrumentCluster(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CarNavigationInstrumentCluster[] newArray(int i) {
            return new CarNavigationInstrumentCluster[i];
        }
    };
    private final Bundle mExtra;
    private final int mImageColorDepthBits;
    private final int mImageHeight;
    private final int mImageWidth;
    private int mMinIntervalMillis;
    private final int mType;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ClusterType {
    }

    private CarNavigationInstrumentCluster(int i, int i2, int i3, int i4, int i5) {
        this.mMinIntervalMillis = i;
        this.mType = i2;
        this.mImageWidth = i3;
        this.mImageHeight = i4;
        this.mImageColorDepthBits = i5;
        this.mExtra = new Bundle();
    }

    public CarNavigationInstrumentCluster(CarNavigationInstrumentCluster carNavigationInstrumentCluster) {
        this(carNavigationInstrumentCluster.mMinIntervalMillis, carNavigationInstrumentCluster.mType, carNavigationInstrumentCluster.mImageWidth, carNavigationInstrumentCluster.mImageHeight, carNavigationInstrumentCluster.mImageColorDepthBits);
    }

    private CarNavigationInstrumentCluster(Parcel parcel) {
        this.mMinIntervalMillis = parcel.readInt();
        this.mType = parcel.readInt();
        this.mImageWidth = parcel.readInt();
        this.mImageHeight = parcel.readInt();
        this.mImageColorDepthBits = parcel.readInt();
        this.mExtra = parcel.readBundle(getClass().getClassLoader());
    }

    public static CarNavigationInstrumentCluster createCluster(int i) {
        return new CarNavigationInstrumentCluster(i, 2, 0, 0, 0);
    }

    public static CarNavigationInstrumentCluster createCustomImageCluster(int i, int i2, int i3, int i4) {
        return new CarNavigationInstrumentCluster(i, 1, i2, i3, i4);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Bundle getExtra() {
        return this.mExtra;
    }

    public int getImageColorDepthBits() {
        return this.mImageColorDepthBits;
    }

    public int getImageHeight() {
        return this.mImageHeight;
    }

    public int getImageWidth() {
        return this.mImageWidth;
    }

    public int getMinIntervalMillis() {
        return this.mMinIntervalMillis;
    }

    public int getType() {
        return this.mType;
    }

    public boolean supportsCustomImages() {
        return this.mType == 1;
    }

    public String toString() {
        return "CarNavigationInstrumentCluster{ minIntervalMillis: " + this.mMinIntervalMillis + ", type: " + this.mType + ", imageWidth: " + this.mImageWidth + ", imageHeight: " + this.mImageHeight + ", imageColourDepthBits: " + this.mImageColorDepthBits + "extra: " + this.mExtra + " }";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mMinIntervalMillis);
        parcel.writeInt(this.mType);
        parcel.writeInt(this.mImageWidth);
        parcel.writeInt(this.mImageHeight);
        parcel.writeInt(this.mImageColorDepthBits);
        parcel.writeBundle(this.mExtra);
    }
}
