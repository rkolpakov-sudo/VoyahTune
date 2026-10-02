package android.car.input;

import android.annotation.NonNull;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Annotation;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public final class RotaryEvent implements Parcelable {
    public static final Parcelable.Creator<RotaryEvent> CREATOR = new Parcelable.Creator<RotaryEvent>() { // from class: android.car.input.RotaryEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RotaryEvent createFromParcel(Parcel parcel) {
            return new RotaryEvent(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RotaryEvent[] newArray(int i) {
            return new RotaryEvent[i];
        }
    };
    private final boolean mClockwise;
    private final int mInputType;
    private final long[] mUptimeMillisForClicks;

    public RotaryEvent(int i, boolean z, long[] jArr) {
        this.mInputType = i;
        AnnotationValidations.validate(CarInputManager.InputTypeEnum.class, (Annotation) null, i);
        this.mClockwise = z;
        this.mUptimeMillisForClicks = jArr;
        AnnotationValidations.validate(NonNull.class, (NonNull) null, jArr);
    }

    RotaryEvent(Parcel parcel) {
        boolean z = (parcel.readByte() & 2) != 0;
        int i = parcel.readInt();
        long[] jArrCreateLongArray = parcel.createLongArray();
        this.mInputType = i;
        AnnotationValidations.validate(CarInputManager.InputTypeEnum.class, (Annotation) null, i);
        this.mClockwise = z;
        this.mUptimeMillisForClicks = jArrCreateLongArray;
        AnnotationValidations.validate(NonNull.class, (NonNull) null, jArrCreateLongArray);
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
            RotaryEvent rotaryEvent = (RotaryEvent) obj;
            if (this.mInputType == rotaryEvent.mInputType && this.mClockwise == rotaryEvent.mClockwise && Arrays.equals(this.mUptimeMillisForClicks, rotaryEvent.mUptimeMillisForClicks)) {
                return true;
            }
        }
        return false;
    }

    public int getInputType() {
        return this.mInputType;
    }

    public int getNumberOfClicks() {
        return this.mUptimeMillisForClicks.length;
    }

    public long getUptimeMillisForClick(int i) {
        return this.mUptimeMillisForClicks[i];
    }

    public long[] getUptimeMillisForClicks() {
        return this.mUptimeMillisForClicks;
    }

    public int hashCode() {
        return ((((this.mInputType + 31) * 31) + Boolean.hashCode(this.mClockwise)) * 31) + Arrays.hashCode(this.mUptimeMillisForClicks);
    }

    public boolean isClockwise() {
        return this.mClockwise;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("RotaryEvent{mInputType:");
        sb.append(this.mInputType);
        sb.append(",mClockwise:");
        sb.append(this.mClockwise);
        sb.append(",mUptimeMillisForClicks:");
        sb.append(Arrays.toString(this.mUptimeMillisForClicks));
        sb.append("}");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte(this.mClockwise ? (byte) 2 : (byte) 0);
        parcel.writeInt(this.mInputType);
        parcel.writeLongArray(this.mUptimeMillisForClicks);
    }
}
