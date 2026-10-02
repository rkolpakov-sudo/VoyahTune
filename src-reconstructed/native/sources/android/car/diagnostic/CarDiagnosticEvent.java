package android.car.diagnostic;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonWriter;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class CarDiagnosticEvent implements Parcelable {
    public static final Parcelable.Creator<CarDiagnosticEvent> CREATOR = new Parcelable.Creator<CarDiagnosticEvent>() { // from class: android.car.diagnostic.CarDiagnosticEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CarDiagnosticEvent createFromParcel(Parcel parcel) {
            return new CarDiagnosticEvent(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CarDiagnosticEvent[] newArray(int i) {
            return new CarDiagnosticEvent[i];
        }
    };
    public final String dtc;
    public final int frameType;
    private final SparseArray<Float> mFloatValues;
    private final SparseIntArray mIntValues;
    public final long timestamp;

    public static class Builder {
        private int mType;
        private long mTimestamp = 0;
        private SparseArray<Float> mFloatValues = new SparseArray<>();
        private SparseIntArray mIntValues = new SparseIntArray();
        private String mDtc = null;

        private Builder(int i) {
            this.mType = 0;
            this.mType = i;
        }

        public static Builder newFreezeFrameBuilder() {
            return new Builder(1);
        }

        public static Builder newLiveFrameBuilder() {
            return new Builder(0);
        }

        @Deprecated
        public Builder atTimestamp(long j) {
            this.mTimestamp = j;
            return this;
        }

        public CarDiagnosticEvent build() {
            return new CarDiagnosticEvent(this.mType, this.mTimestamp, this.mFloatValues, this.mIntValues, this.mDtc);
        }

        public Builder setDtc(String str) {
            this.mDtc = str;
            return this;
        }

        public Builder setFloatValue(int i, float f) {
            this.mFloatValues.put(i, Float.valueOf(f));
            return this;
        }

        public Builder setIntValue(int i, int i2) {
            this.mIntValues.put(i, i2);
            return this;
        }

        public Builder setTimeStamp(long j) {
            this.mTimestamp = j;
            return this;
        }

        @Deprecated
        public Builder withDtc(String str) {
            this.mDtc = str;
            return this;
        }

        @Deprecated
        public Builder withFloatValue(int i, float f) {
            this.mFloatValues.put(i, Float.valueOf(f));
            return this;
        }

        @Deprecated
        public Builder withIntValue(int i, int i2) {
            this.mIntValues.put(i, i2);
            return this;
        }
    }

    public static class CommonIgnitionMonitors {
        public static final int COMPONENTS_AVAILABLE = 1;
        public static final int COMPONENTS_INCOMPLETE = 2;
        public static final int FUEL_SYSTEM_AVAILABLE = 4;
        public static final int FUEL_SYSTEM_INCOMPLETE = 8;
        public static final int MISFIRE_AVAILABLE = 16;
        public static final int MISFIRE_INCOMPLETE = 32;
        public final IgnitionMonitor components;
        public final IgnitionMonitor fuelSystem;
        public final IgnitionMonitor misfire;
        static final IgnitionMonitor.Decoder COMPONENTS_DECODER = new IgnitionMonitor.Decoder(1, 2);
        static final IgnitionMonitor.Decoder FUEL_SYSTEM_DECODER = new IgnitionMonitor.Decoder(4, 8);
        static final IgnitionMonitor.Decoder MISFIRE_DECODER = new IgnitionMonitor.Decoder(16, 32);

        CommonIgnitionMonitors(int i) {
            this.components = COMPONENTS_DECODER.fromValue(i);
            this.fuelSystem = FUEL_SYSTEM_DECODER.fromValue(i);
            this.misfire = MISFIRE_DECODER.fromValue(i);
        }

        public CompressionIgnitionMonitors asCompressionIgnitionMonitors() {
            if (this instanceof CompressionIgnitionMonitors) {
                return (CompressionIgnitionMonitors) this;
            }
            return null;
        }

        public SparkIgnitionMonitors asSparkIgnitionMonitors() {
            if (this instanceof SparkIgnitionMonitors) {
                return (SparkIgnitionMonitors) this;
            }
            return null;
        }
    }

    public static final class CompressionIgnitionMonitors extends CommonIgnitionMonitors {
        public static final int BOOST_PRESSURE_AVAILABLE = 4096;
        public static final int BOOST_PRESSURE_INCOMPLETE = 8192;
        public static final int EGR_OR_VVT_AVAILABLE = 64;
        public static final int EGR_OR_VVT_INCOMPLETE = 128;
        public static final int EXHAUST_GAS_SENSOR_AVAILABLE = 1024;
        public static final int EXHAUST_GAS_SENSOR_INCOMPLETE = 2048;
        public static final int NMHC_CATALYST_AVAILABLE = 65536;
        public static final int NMHC_CATALYST_INCOMPLETE = 131072;
        public static final int NOx_SCR_AVAILABLE = 16384;
        public static final int NOx_SCR_INCOMPLETE = 32768;
        public static final int PM_FILTER_AVAILABLE = 256;
        public static final int PM_FILTER_INCOMPLETE = 512;
        public final IgnitionMonitor EGROrVVT;
        public final IgnitionMonitor NMHCCatalyst;
        public final IgnitionMonitor NOxSCR;
        public final IgnitionMonitor PMFilter;
        public final IgnitionMonitor boostPressure;
        public final IgnitionMonitor exhaustGasSensor;
        static final IgnitionMonitor.Decoder EGR_OR_VVT_DECODER = new IgnitionMonitor.Decoder(64, 128);
        static final IgnitionMonitor.Decoder PM_FILTER_DECODER = new IgnitionMonitor.Decoder(256, 512);
        static final IgnitionMonitor.Decoder EXHAUST_GAS_SENSOR_DECODER = new IgnitionMonitor.Decoder(1024, 2048);
        static final IgnitionMonitor.Decoder BOOST_PRESSURE_DECODER = new IgnitionMonitor.Decoder(4096, 8192);
        static final IgnitionMonitor.Decoder NOx_SCR_DECODER = new IgnitionMonitor.Decoder(16384, 32768);
        static final IgnitionMonitor.Decoder NMHC_CATALYST_DECODER = new IgnitionMonitor.Decoder(65536, 131072);

        CompressionIgnitionMonitors(int i) {
            super(i);
            this.EGROrVVT = EGR_OR_VVT_DECODER.fromValue(i);
            this.PMFilter = PM_FILTER_DECODER.fromValue(i);
            this.exhaustGasSensor = EXHAUST_GAS_SENSOR_DECODER.fromValue(i);
            this.boostPressure = BOOST_PRESSURE_DECODER.fromValue(i);
            this.NOxSCR = NOx_SCR_DECODER.fromValue(i);
            this.NMHCCatalyst = NMHC_CATALYST_DECODER.fromValue(i);
        }
    }

    public static final class FuelSystemStatus {
        public static final int CLOSED_LOOP = 2;
        public static final int CLOSED_LOOP_BUT_FEEDBACK_FAULT = 16;
        public static final int OPEN_ENGINE_LOAD_OR_DECELERATION = 4;
        public static final int OPEN_INSUFFICIENT_ENGINE_TEMPERATURE = 1;
        public static final int OPEN_SYSTEM_FAILURE = 8;

        @Retention(RetentionPolicy.SOURCE)
        public @interface Status {
        }

        private FuelSystemStatus() {
        }
    }

    public static final class FuelType {
        public static final int BIFUEL_RUNNING_CNG = 13;
        public static final int BIFUEL_RUNNING_DIESEL = 23;
        public static final int BIFUEL_RUNNING_ELECTRIC = 15;
        public static final int BIFUEL_RUNNING_ELECTRIC_AND_COMBUSTION = 16;
        public static final int BIFUEL_RUNNING_ETHANOL = 11;
        public static final int BIFUEL_RUNNING_GASOLINE = 9;
        public static final int BIFUEL_RUNNING_LPG = 12;
        public static final int BIFUEL_RUNNING_METHANOL = 10;
        public static final int BIFUEL_RUNNING_PROPANE = 14;
        public static final int CNG = 6;
        public static final int DIESEL = 4;
        public static final int ELECTRIC = 8;
        public static final int ETHANOL = 3;
        public static final int GASOLINE = 1;
        public static final int HYBRID_DIESEL = 19;
        public static final int HYBRID_ELECTRIC = 20;
        public static final int HYBRID_ETHANOL = 18;
        public static final int HYBRID_GASOLINE = 17;
        public static final int HYBRID_REGENERATIVE = 22;
        public static final int HYBRID_RUNNING_ELECTRIC_AND_COMBUSTION = 21;
        public static final int LPG = 5;
        public static final int METHANOL = 2;
        public static final int NOT_AVAILABLE = 0;
        public static final int PROPANE = 7;

        @Retention(RetentionPolicy.SOURCE)
        public @interface Type {
        }

        private FuelType() {
        }
    }

    public static final class IgnitionMonitor {
        public final boolean available;
        public final boolean incomplete;

        public static final class Decoder {
            private final int mAvailableBitmask;
            private final int mIncompleteBitmask;

            Decoder(int i, int i2) {
                this.mAvailableBitmask = i;
                this.mIncompleteBitmask = i2;
            }

            public IgnitionMonitor fromValue(int i) {
                return new IgnitionMonitor((this.mAvailableBitmask & i) != 0, (this.mIncompleteBitmask & i) != 0);
            }
        }

        IgnitionMonitor(boolean z, boolean z2) {
            this.available = z;
            this.incomplete = z2;
        }
    }

    public static final class SecondaryAirStatus {
        public static final int DOWNSTREAM_OF_CATALYCIC_CONVERTER = 2;
        public static final int FROM_OUTSIDE_OR_OFF = 4;
        public static final int PUMP_ON_FOR_DIAGNOSTICS = 8;
        public static final int UPSTREAM = 1;

        @Retention(RetentionPolicy.SOURCE)
        public @interface Status {
        }

        private SecondaryAirStatus() {
        }
    }

    public static final class SparkIgnitionMonitors extends CommonIgnitionMonitors {
        public static final int AC_REFRIGERANT_AVAILABLE = 4096;
        public static final int AC_REFRIGERANT_INCOMPLETE = 8192;
        public static final int CATALYST_AVAILABLE = 1048576;
        public static final int CATALYST_INCOMPLETE = 2097152;
        public static final int EGR_AVAILABLE = 64;
        public static final int EGR_INCOMPLETE = 128;
        public static final int EVAPORATIVE_SYSTEM_AVAILABLE = 65536;
        public static final int EVAPORATIVE_SYSTEM_INCOMPLETE = 131072;
        public static final int HEATED_CATALYST_AVAILABLE = 262144;
        public static final int HEATED_CATALYST_INCOMPLETE = 524288;
        public static final int OXYGEN_SENSOR_AVAILABLE = 1024;
        public static final int OXYGEN_SENSOR_HEATER_AVAILABLE = 256;
        public static final int OXYGEN_SENSOR_HEATER_INCOMPLETE = 512;
        public static final int OXYGEN_SENSOR_INCOMPLETE = 2048;
        public static final int SECONDARY_AIR_SYSTEM_AVAILABLE = 16384;
        public static final int SECONDARY_AIR_SYSTEM_INCOMPLETE = 32768;
        public final IgnitionMonitor ACRefrigerant;
        public final IgnitionMonitor EGR;
        public final IgnitionMonitor catalyst;
        public final IgnitionMonitor evaporativeSystem;
        public final IgnitionMonitor heatedCatalyst;
        public final IgnitionMonitor oxygenSensor;
        public final IgnitionMonitor oxygenSensorHeater;
        public final IgnitionMonitor secondaryAirSystem;
        static final IgnitionMonitor.Decoder EGR_DECODER = new IgnitionMonitor.Decoder(64, 128);
        static final IgnitionMonitor.Decoder OXYGEN_SENSOR_HEATER_DECODER = new IgnitionMonitor.Decoder(256, 512);
        static final IgnitionMonitor.Decoder OXYGEN_SENSOR_DECODER = new IgnitionMonitor.Decoder(1024, 2048);
        static final IgnitionMonitor.Decoder AC_REFRIGERANT_DECODER = new IgnitionMonitor.Decoder(4096, 8192);
        static final IgnitionMonitor.Decoder SECONDARY_AIR_SYSTEM_DECODER = new IgnitionMonitor.Decoder(16384, 32768);
        static final IgnitionMonitor.Decoder EVAPORATIVE_SYSTEM_DECODER = new IgnitionMonitor.Decoder(65536, 131072);
        static final IgnitionMonitor.Decoder HEATED_CATALYST_DECODER = new IgnitionMonitor.Decoder(262144, 524288);
        static final IgnitionMonitor.Decoder CATALYST_DECODER = new IgnitionMonitor.Decoder(1048576, 2097152);

        SparkIgnitionMonitors(int i) {
            super(i);
            this.EGR = EGR_DECODER.fromValue(i);
            this.oxygenSensorHeater = OXYGEN_SENSOR_HEATER_DECODER.fromValue(i);
            this.oxygenSensor = OXYGEN_SENSOR_DECODER.fromValue(i);
            this.ACRefrigerant = AC_REFRIGERANT_DECODER.fromValue(i);
            this.secondaryAirSystem = SECONDARY_AIR_SYSTEM_DECODER.fromValue(i);
            this.evaporativeSystem = EVAPORATIVE_SYSTEM_DECODER.fromValue(i);
            this.heatedCatalyst = HEATED_CATALYST_DECODER.fromValue(i);
            this.catalyst = CATALYST_DECODER.fromValue(i);
        }
    }

    private CarDiagnosticEvent(int i, long j, SparseArray<Float> sparseArray, SparseIntArray sparseIntArray, String str) {
        this.frameType = i;
        this.timestamp = j;
        this.mFloatValues = sparseArray;
        this.mIntValues = sparseIntArray;
        this.dtc = str;
    }

    public CarDiagnosticEvent(Parcel parcel) {
        this.frameType = parcel.readInt();
        this.timestamp = parcel.readLong();
        int i = parcel.readInt();
        this.mFloatValues = new SparseArray<>(i);
        for (int i2 = 0; i2 < i; i2++) {
            this.mFloatValues.put(parcel.readInt(), Float.valueOf(parcel.readFloat()));
        }
        int i3 = parcel.readInt();
        this.mIntValues = new SparseIntArray(i3);
        for (int i4 = 0; i4 < i3; i4++) {
            this.mIntValues.put(parcel.readInt(), parcel.readInt());
        }
        this.dtc = (String) parcel.readValue(String.class.getClassLoader());
    }

    public CarDiagnosticEvent checkFreezeFrame() {
        if (isFreezeFrame()) {
            return this;
        }
        throw new IllegalStateException("frame is not a freeze frame");
    }

    public CarDiagnosticEvent checkLiveFrame() {
        if (isLiveFrame()) {
            return this;
        }
        throw new IllegalStateException("frame is not a live frame");
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof CarDiagnosticEvent)) {
            return false;
        }
        CarDiagnosticEvent carDiagnosticEvent = (CarDiagnosticEvent) obj;
        if (carDiagnosticEvent.frameType != this.frameType || carDiagnosticEvent.timestamp != this.timestamp || carDiagnosticEvent.mIntValues.size() != this.mIntValues.size() || carDiagnosticEvent.mFloatValues.size() != this.mFloatValues.size() || !Objects.equals(this.dtc, carDiagnosticEvent.dtc)) {
            return false;
        }
        for (int i = 0; i < this.mIntValues.size(); i++) {
            if (this.mIntValues.keyAt(i) != carDiagnosticEvent.mIntValues.keyAt(i) || this.mIntValues.valueAt(i) != carDiagnosticEvent.mIntValues.valueAt(i)) {
                return false;
            }
        }
        for (int i2 = 0; i2 < this.mFloatValues.size(); i2++) {
            if (this.mFloatValues.keyAt(i2) != carDiagnosticEvent.mFloatValues.keyAt(i2) || this.mFloatValues.valueAt(i2).floatValue() != carDiagnosticEvent.mFloatValues.valueAt(i2).floatValue()) {
                return false;
            }
        }
        return true;
    }

    public Integer getFuelSystemStatus() {
        return getSystemIntegerSensor(0);
    }

    public Integer getFuelType() {
        return getSystemIntegerSensor(21);
    }

    public CommonIgnitionMonitors getIgnitionMonitors() {
        Integer systemIntegerSensor = getSystemIntegerSensor(2);
        Integer systemIntegerSensor2 = getSystemIntegerSensor(3);
        if (systemIntegerSensor == null || systemIntegerSensor2 == null) {
            return null;
        }
        int iIntValue = systemIntegerSensor.intValue();
        if (iIntValue == 0) {
            return new SparkIgnitionMonitors(systemIntegerSensor2.intValue());
        }
        if (iIntValue != 1) {
            return null;
        }
        return new CompressionIgnitionMonitors(systemIntegerSensor2.intValue());
    }

    public Integer getSecondaryAirStatus() {
        return getSystemIntegerSensor(5);
    }

    public float getSystemFloatSensor(int i, float f) {
        return this.mFloatValues.get(i, Float.valueOf(f)).floatValue();
    }

    public Float getSystemFloatSensor(int i) {
        int iIndexOfKey = this.mFloatValues.indexOfKey(i);
        if (iIndexOfKey < 0) {
            return null;
        }
        return this.mFloatValues.valueAt(iIndexOfKey);
    }

    public int getSystemIntegerSensor(int i, int i2) {
        return this.mIntValues.get(i, i2);
    }

    public Integer getSystemIntegerSensor(int i) {
        int iIndexOfKey = this.mIntValues.indexOfKey(i);
        if (iIndexOfKey < 0) {
            return null;
        }
        return Integer.valueOf(this.mIntValues.valueAt(iIndexOfKey));
    }

    public float getVendorFloatSensor(int i, float f) {
        return this.mFloatValues.get(i, Float.valueOf(f)).floatValue();
    }

    public Float getVendorFloatSensor(int i) {
        int iIndexOfKey = this.mFloatValues.indexOfKey(i);
        if (iIndexOfKey < 0) {
            return null;
        }
        return this.mFloatValues.valueAt(iIndexOfKey);
    }

    public int getVendorIntegerSensor(int i, int i2) {
        return this.mIntValues.get(i, i2);
    }

    public Integer getVendorIntegerSensor(int i) {
        int iIndexOfKey = this.mIntValues.indexOfKey(i);
        if (iIndexOfKey < 0) {
            return null;
        }
        return Integer.valueOf(this.mIntValues.valueAt(iIndexOfKey));
    }

    public int hashCode() {
        int size = this.mIntValues.size();
        Integer[] numArr = new Integer[size];
        int size2 = this.mFloatValues.size();
        Integer[] numArr2 = new Integer[size2];
        Integer[] numArr3 = new Integer[size];
        Float[] fArr = new Float[size2];
        for (int i = 0; i < size; i++) {
            numArr[i] = Integer.valueOf(this.mIntValues.keyAt(i));
            numArr3[i] = Integer.valueOf(this.mIntValues.valueAt(i));
        }
        for (int i2 = 0; i2 < size2; i2++) {
            numArr2[i2] = Integer.valueOf(this.mFloatValues.keyAt(i2));
            fArr[i2] = this.mFloatValues.valueAt(i2);
        }
        return Objects.hash(Integer.valueOf(this.frameType), Long.valueOf(this.timestamp), this.dtc, Integer.valueOf(Objects.hash(numArr)), Integer.valueOf(Objects.hash(numArr3)), Integer.valueOf(Objects.hash(numArr2)), Integer.valueOf(Objects.hash(fArr)));
    }

    public boolean isEarlierThan(CarDiagnosticEvent carDiagnosticEvent) {
        Objects.requireNonNull(carDiagnosticEvent);
        return this.timestamp < carDiagnosticEvent.timestamp;
    }

    public boolean isEmptyFrame() {
        boolean z = (this.mIntValues.size() == 0) & (this.mFloatValues.size() == 0);
        return isFreezeFrame() ? this.dtc.isEmpty() & z : z;
    }

    public boolean isFreezeFrame() {
        return 1 == this.frameType;
    }

    public boolean isLiveFrame() {
        return this.frameType == 0;
    }

    public String toString() {
        return String.format("%s diagnostic frame {\n\ttimestamp: %d, DTC: %s\n\tmIntValues: %s\n\tmFloatValues: %s\n}", isLiveFrame() ? "live" : "freeze", Long.valueOf(this.timestamp), this.dtc, this.mIntValues.toString(), this.mFloatValues.toString());
    }

    public CarDiagnosticEvent withVendorSensorsRemoved() {
        SparseIntArray sparseIntArrayClone = this.mIntValues.clone();
        SparseArray<Float> sparseArrayClone = this.mFloatValues.clone();
        for (int i = 0; i < this.mIntValues.size(); i++) {
            int iKeyAt = this.mIntValues.keyAt(i);
            if (iKeyAt >= 31) {
                sparseIntArrayClone.delete(iKeyAt);
            }
        }
        for (int i2 = 0; i2 < this.mFloatValues.size(); i2++) {
            int iKeyAt2 = this.mFloatValues.keyAt(i2);
            if (iKeyAt2 >= 70) {
                sparseArrayClone.delete(iKeyAt2);
            }
        }
        return new CarDiagnosticEvent(this.frameType, this.timestamp, sparseArrayClone, sparseIntArrayClone, this.dtc);
    }

    public void writeToJson(JsonWriter jsonWriter) throws IOException {
        String str;
        jsonWriter.beginObject();
        jsonWriter.name("type");
        int i = this.frameType;
        if (i == 0) {
            str = "live";
        } else {
            if (i != 1) {
                throw new IllegalStateException("unknown frameType " + this.frameType);
            }
            str = "freeze";
        }
        jsonWriter.value(str);
        jsonWriter.name("timestamp").value(this.timestamp);
        jsonWriter.name("intValues").beginArray();
        for (int i2 = 0; i2 < this.mIntValues.size(); i2++) {
            jsonWriter.beginObject();
            jsonWriter.name("id").value(this.mIntValues.keyAt(i2));
            jsonWriter.name("value").value(this.mIntValues.valueAt(i2));
            jsonWriter.endObject();
        }
        jsonWriter.endArray();
        jsonWriter.name("floatValues").beginArray();
        for (int i3 = 0; i3 < this.mFloatValues.size(); i3++) {
            jsonWriter.beginObject();
            jsonWriter.name("id").value(this.mFloatValues.keyAt(i3));
            jsonWriter.name("value").value(this.mFloatValues.valueAt(i3));
            jsonWriter.endObject();
        }
        jsonWriter.endArray();
        if (this.dtc != null) {
            jsonWriter.name("stringValue").value(this.dtc);
        }
        jsonWriter.endObject();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.frameType);
        parcel.writeLong(this.timestamp);
        parcel.writeInt(this.mFloatValues.size());
        for (int i2 = 0; i2 < this.mFloatValues.size(); i2++) {
            int iKeyAt = this.mFloatValues.keyAt(i2);
            parcel.writeInt(iKeyAt);
            parcel.writeFloat(this.mFloatValues.get(iKeyAt).floatValue());
        }
        parcel.writeInt(this.mIntValues.size());
        for (int i3 = 0; i3 < this.mIntValues.size(); i3++) {
            int iKeyAt2 = this.mIntValues.keyAt(i3);
            parcel.writeInt(iKeyAt2);
            parcel.writeInt(this.mIntValues.get(iKeyAt2));
        }
        parcel.writeValue(this.dtc);
    }
}
