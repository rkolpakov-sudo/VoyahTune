package android.car.vms;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class VmsLayer implements Parcelable {
    public static final Parcelable.Creator<VmsLayer> CREATOR = new Parcelable.Creator<VmsLayer>() { // from class: android.car.vms.VmsLayer.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VmsLayer createFromParcel(Parcel parcel) {
            return new VmsLayer(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VmsLayer[] newArray(int i) {
            return new VmsLayer[i];
        }
    };
    private int mChannel;
    private int mType;
    private int mVersion;

    public VmsLayer(int i, int i2, int i3) {
        this.mType = i;
        this.mChannel = i2;
        this.mVersion = i3;
    }

    VmsLayer(Parcel parcel) {
        int i = parcel.readInt();
        int i2 = parcel.readInt();
        int i3 = parcel.readInt();
        this.mType = i;
        this.mChannel = i2;
        this.mVersion = i3;
    }

    @Deprecated
    private void __metadata() {
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            VmsLayer vmsLayer = (VmsLayer) obj;
            if (this.mType == vmsLayer.mType && this.mChannel == vmsLayer.mChannel && this.mVersion == vmsLayer.mVersion) {
                return true;
            }
        }
        return false;
    }

    public int getChannel() {
        return this.mChannel;
    }

    @Deprecated
    public int getSubtype() {
        return this.mChannel;
    }

    public int getType() {
        return this.mType;
    }

    public int getVersion() {
        return this.mVersion;
    }

    public int hashCode() {
        return ((((this.mType + 31) * 31) + this.mChannel) * 31) + this.mVersion;
    }

    public String toString() {
        return "VmsLayer { type = " + this.mType + ", channel = " + this.mChannel + ", version = " + this.mVersion + " }";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mType);
        parcel.writeInt(this.mChannel);
        parcel.writeInt(this.mVersion);
    }
}
