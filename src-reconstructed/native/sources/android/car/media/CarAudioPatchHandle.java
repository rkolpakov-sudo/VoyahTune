package android.car.media;

import android.annotation.SystemApi;
import android.media.AudioDevicePort;
import android.media.AudioPatch;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.util.Preconditions;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class CarAudioPatchHandle implements Parcelable {
    public static final Parcelable.Creator<CarAudioPatchHandle> CREATOR = new Parcelable.Creator<CarAudioPatchHandle>() { // from class: android.car.media.CarAudioPatchHandle.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CarAudioPatchHandle createFromParcel(Parcel parcel) {
            return new CarAudioPatchHandle(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CarAudioPatchHandle[] newArray(int i) {
            return new CarAudioPatchHandle[i];
        }
    };
    private final int mHandleId;
    private final String mSinkAddress;
    private final String mSourceAddress;

    public CarAudioPatchHandle(AudioPatch audioPatch) {
        Preconditions.checkArgument(audioPatch.sources().length == 1 && (audioPatch.sources()[0].port() instanceof AudioDevicePort), "Accepts exactly one device port as source");
        Preconditions.checkArgument(audioPatch.sinks().length == 1 && (audioPatch.sinks()[0].port() instanceof AudioDevicePort), "Accepts exactly one device port as sink");
        this.mHandleId = audioPatch.id();
        this.mSourceAddress = audioPatch.sources()[0].port().address();
        this.mSinkAddress = audioPatch.sinks()[0].port().address();
    }

    private CarAudioPatchHandle(Parcel parcel) {
        this.mHandleId = parcel.readInt();
        this.mSourceAddress = parcel.readString();
        this.mSinkAddress = parcel.readString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean represents(AudioPatch audioPatch) {
        return audioPatch.id() == this.mHandleId;
    }

    public String toString() {
        return "Patch (mHandleId=" + this.mHandleId + "): " + this.mSourceAddress + " => " + this.mSinkAddress;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mHandleId);
        parcel.writeString(this.mSourceAddress);
        parcel.writeString(this.mSinkAddress);
    }
}
