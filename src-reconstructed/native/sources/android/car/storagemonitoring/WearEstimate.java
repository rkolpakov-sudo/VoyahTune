package android.car.storagemonitoring;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.JsonWriter;
import androidx.core.os.EnvironmentCompat;
import java.io.IOException;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class WearEstimate implements Parcelable {
    public static final int UNKNOWN = -1;
    public final int typeA;
    public final int typeB;
    public static final WearEstimate UNKNOWN_ESTIMATE = new WearEstimate(-1, -1);
    public static final Parcelable.Creator<WearEstimate> CREATOR = new Parcelable.Creator<WearEstimate>() { // from class: android.car.storagemonitoring.WearEstimate.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WearEstimate createFromParcel(Parcel parcel) {
            return new WearEstimate(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WearEstimate[] newArray(int i) {
            return new WearEstimate[i];
        }
    };

    public WearEstimate(int i, int i2) {
        this.typeA = validateWearValue(i);
        this.typeB = validateWearValue(i2);
    }

    public WearEstimate(Parcel parcel) {
        this.typeA = validateWearValue(parcel.readInt());
        this.typeB = validateWearValue(parcel.readInt());
    }

    public WearEstimate(JsonReader jsonReader) throws IOException {
        byte b;
        jsonReader.beginObject();
        int iValidateWearValue = -1;
        int iValidateWearValue2 = -1;
        while (jsonReader.hasNext()) {
            switch (jsonReader.nextName()) {
                case "wearEstimateTypeA":
                    b = 0;
                    break;
                case "wearEstimateTypeB":
                    b = 1;
                    break;
                default:
                    b = -1;
                    break;
            }
            if (b == 0) {
                iValidateWearValue = validateWearValue(jsonReader.nextInt());
            } else if (b == 1) {
                iValidateWearValue2 = validateWearValue(jsonReader.nextInt());
            }
        }
        jsonReader.endObject();
        this.typeA = iValidateWearValue;
        this.typeB = iValidateWearValue2;
    }

    public WearEstimate(JSONObject jSONObject) throws JSONException {
        this.typeA = jSONObject.getInt("wearEstimateTypeA");
        this.typeB = jSONObject.getInt("wearEstimateTypeB");
    }

    private static int validateWearValue(int i) {
        if (i == -1 || (i >= 0 && i <= 100)) {
            return i;
        }
        throw new IllegalArgumentException(i + " is not a valid wear estimate");
    }

    private static String wearValueToString(int i) {
        if (i == -1) {
            return EnvironmentCompat.MEDIA_UNKNOWN;
        }
        return i + "%";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj instanceof WearEstimate) {
            WearEstimate wearEstimate = (WearEstimate) obj;
            if (wearEstimate.typeA == this.typeA && wearEstimate.typeB == this.typeB) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.typeA), Integer.valueOf(this.typeB));
    }

    public String toString() {
        return "type A: " + wearValueToString(this.typeA) + ", type B: " + wearValueToString(this.typeB);
    }

    public void writeToJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("wearEstimateTypeA").value(this.typeA);
        jsonWriter.name("wearEstimateTypeB").value(this.typeB);
        jsonWriter.endObject();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.typeA);
        parcel.writeInt(this.typeB);
    }
}
