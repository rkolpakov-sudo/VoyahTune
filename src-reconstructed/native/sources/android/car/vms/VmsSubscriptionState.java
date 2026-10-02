package android.car.vms;

import android.annotation.NonNull;
import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArraySet;
import com.android.internal.util.AnnotationValidations;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class VmsSubscriptionState implements Parcelable {
    public static final Parcelable.Creator<VmsSubscriptionState> CREATOR = new Parcelable.Creator<VmsSubscriptionState>() { // from class: android.car.vms.VmsSubscriptionState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VmsSubscriptionState createFromParcel(Parcel parcel) {
            return new VmsSubscriptionState(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VmsSubscriptionState[] newArray(int i) {
            return new VmsSubscriptionState[i];
        }
    };
    private Set<VmsAssociatedLayer> mAssociatedLayers;
    private Set<VmsLayer> mLayers;
    private final int mSequenceNumber;

    public VmsSubscriptionState(int i, Set<VmsLayer> set, Set<VmsAssociatedLayer> set2) {
        this.mSequenceNumber = i;
        this.mLayers = set;
        AnnotationValidations.validate(NonNull.class, (NonNull) null, set);
        this.mAssociatedLayers = set2;
        AnnotationValidations.validate(NonNull.class, (NonNull) null, set2);
        onConstructed();
    }

    VmsSubscriptionState(Parcel parcel) {
        int i = parcel.readInt();
        Set<VmsLayer> setUnparcelLayers = unparcelLayers(parcel);
        Set<VmsAssociatedLayer> setUnparcelAssociatedLayers = unparcelAssociatedLayers(parcel);
        this.mSequenceNumber = i;
        this.mLayers = setUnparcelLayers;
        AnnotationValidations.validate(NonNull.class, (NonNull) null, setUnparcelLayers);
        this.mAssociatedLayers = setUnparcelAssociatedLayers;
        AnnotationValidations.validate(NonNull.class, (NonNull) null, setUnparcelAssociatedLayers);
        onConstructed();
    }

    @Deprecated
    private void __metadata() {
    }

    private void onConstructed() {
        this.mLayers = Collections.unmodifiableSet(this.mLayers);
        this.mAssociatedLayers = Collections.unmodifiableSet(this.mAssociatedLayers);
    }

    private void parcelAssociatedLayers(Parcel parcel, int i) {
        parcel.writeArraySet(new ArraySet(this.mAssociatedLayers));
    }

    private void parcelLayers(Parcel parcel, int i) {
        parcel.writeArraySet(new ArraySet(this.mLayers));
    }

    private Set<VmsAssociatedLayer> unparcelAssociatedLayers(Parcel parcel) {
        return parcel.readArraySet(VmsAssociatedLayer.class.getClassLoader());
    }

    private Set<VmsLayer> unparcelLayers(Parcel parcel) {
        return parcel.readArraySet(VmsLayer.class.getClassLoader());
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
            VmsSubscriptionState vmsSubscriptionState = (VmsSubscriptionState) obj;
            if (this.mSequenceNumber == vmsSubscriptionState.mSequenceNumber && Objects.equals(this.mLayers, vmsSubscriptionState.mLayers) && Objects.equals(this.mAssociatedLayers, vmsSubscriptionState.mAssociatedLayers)) {
                return true;
            }
        }
        return false;
    }

    public Set<VmsAssociatedLayer> getAssociatedLayers() {
        return this.mAssociatedLayers;
    }

    public Set<VmsLayer> getLayers() {
        return this.mLayers;
    }

    public int getSequenceNumber() {
        return this.mSequenceNumber;
    }

    public int hashCode() {
        return ((((this.mSequenceNumber + 31) * 31) + Objects.hashCode(this.mLayers)) * 31) + Objects.hashCode(this.mAssociatedLayers);
    }

    public String toString() {
        return "VmsSubscriptionState { sequenceNumber = " + this.mSequenceNumber + ", layers = " + this.mLayers + ", associatedLayers = " + this.mAssociatedLayers + " }";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mSequenceNumber);
        parcelLayers(parcel, i);
        parcelAssociatedLayers(parcel, i);
    }
}
