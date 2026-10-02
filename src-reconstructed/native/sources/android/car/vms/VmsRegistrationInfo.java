package android.car.vms;

import android.annotation.NonNull;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class VmsRegistrationInfo implements Parcelable {
    public static final Parcelable.Creator<VmsRegistrationInfo> CREATOR = new Parcelable.Creator<VmsRegistrationInfo>() { // from class: android.car.vms.VmsRegistrationInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VmsRegistrationInfo createFromParcel(Parcel parcel) {
            return new VmsRegistrationInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VmsRegistrationInfo[] newArray(int i) {
            return new VmsRegistrationInfo[i];
        }
    };
    private VmsAvailableLayers mAvailableLayers;
    private VmsSubscriptionState mSubscriptionState;

    public VmsRegistrationInfo(VmsAvailableLayers vmsAvailableLayers, VmsSubscriptionState vmsSubscriptionState) {
        this.mAvailableLayers = vmsAvailableLayers;
        AnnotationValidations.validate(NonNull.class, (NonNull) null, vmsAvailableLayers);
        this.mSubscriptionState = vmsSubscriptionState;
        AnnotationValidations.validate(NonNull.class, (NonNull) null, vmsSubscriptionState);
    }

    protected VmsRegistrationInfo(Parcel parcel) {
        VmsAvailableLayers vmsAvailableLayers = (VmsAvailableLayers) parcel.readTypedObject(VmsAvailableLayers.CREATOR);
        VmsSubscriptionState vmsSubscriptionState = (VmsSubscriptionState) parcel.readTypedObject(VmsSubscriptionState.CREATOR);
        this.mAvailableLayers = vmsAvailableLayers;
        AnnotationValidations.validate(NonNull.class, (NonNull) null, vmsAvailableLayers);
        this.mSubscriptionState = vmsSubscriptionState;
        AnnotationValidations.validate(NonNull.class, (NonNull) null, vmsSubscriptionState);
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
            VmsRegistrationInfo vmsRegistrationInfo = (VmsRegistrationInfo) obj;
            if (Objects.equals(this.mAvailableLayers, vmsRegistrationInfo.mAvailableLayers) && Objects.equals(this.mSubscriptionState, vmsRegistrationInfo.mSubscriptionState)) {
                return true;
            }
        }
        return false;
    }

    public VmsAvailableLayers getAvailableLayers() {
        return this.mAvailableLayers;
    }

    public VmsSubscriptionState getSubscriptionState() {
        return this.mSubscriptionState;
    }

    public int hashCode() {
        return ((Objects.hashCode(this.mAvailableLayers) + 31) * 31) + Objects.hashCode(this.mSubscriptionState);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedObject(this.mAvailableLayers, i);
        parcel.writeTypedObject(this.mSubscriptionState, i);
    }
}
