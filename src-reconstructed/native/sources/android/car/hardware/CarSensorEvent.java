package android.car.hardware;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public class CarSensorEvent implements Parcelable {
    public static final Parcelable.Creator<CarSensorEvent> CREATOR = new Parcelable.Creator<CarSensorEvent>() { // from class: android.car.hardware.CarSensorEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CarSensorEvent createFromParcel(Parcel parcel) {
            return new CarSensorEvent(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CarSensorEvent[] newArray(int i) {
            return new CarSensorEvent[i];
        }
    };
    public static final int GEAR_DRIVE = 8;
    public static final int GEAR_EIGHTH = 2048;
    public static final int GEAR_FIFTH = 256;
    public static final int GEAR_FIRST = 16;
    public static final int GEAR_FOURTH = 128;
    public static final int GEAR_NEUTRAL = 1;
    public static final int GEAR_NINTH = 4096;
    public static final int GEAR_PARK = 4;
    public static final int GEAR_REVERSE = 2;
    public static final int GEAR_SECOND = 32;
    public static final int GEAR_SEVENTH = 1024;
    public static final int GEAR_SIXTH = 512;
    public static final int GEAR_TENTH = 8192;
    public static final int GEAR_THIRD = 64;
    public static final int IGNITION_STATE_ACC = 3;
    public static final int IGNITION_STATE_LOCK = 1;
    public static final int IGNITION_STATE_OFF = 2;
    public static final int IGNITION_STATE_ON = 4;
    public static final int IGNITION_STATE_START = 5;
    public static final int IGNITION_STATE_UNDEFINED = 0;
    public static final int INDEX_ENVIRONMENT_TEMPERATURE = 0;
    public static final int INDEX_WHEEL_DISTANCE_FRONT_LEFT = 1;
    public static final int INDEX_WHEEL_DISTANCE_FRONT_RIGHT = 2;
    public static final int INDEX_WHEEL_DISTANCE_REAR_LEFT = 4;
    public static final int INDEX_WHEEL_DISTANCE_REAR_RIGHT = 3;
    public static final int INDEX_WHEEL_DISTANCE_RESET_COUNT = 0;
    private static final long MILLI_IN_NANOS = 1000000;
    public final float[] floatValues;
    public final int[] intValues;
    public final long[] longValues;
    public int sensorType;
    public long timestamp;

    public static class CarAbsActiveData {
        public boolean absIsActive;
        public long timestamp;

        private CarAbsActiveData() {
        }
    }

    public static class CarEngineOilLevelData {
        public int engineOilLevel;
        public long timestamp;

        private CarEngineOilLevelData() {
        }
    }

    public static class CarEvBatteryChargeRateData {
        public float evChargeRate;
        public long timestamp;

        private CarEvBatteryChargeRateData() {
        }
    }

    public static class CarEvBatteryLevelData {
        public float evBatteryLevel;
        public long timestamp;

        private CarEvBatteryLevelData() {
        }
    }

    public static class CarEvChargePortConnectedData {
        public boolean evChargePortIsConnected;
        public long timestamp;

        private CarEvChargePortConnectedData() {
        }
    }

    public static class CarEvChargePortOpenData {
        public boolean evChargePortIsOpen;
        public long timestamp;

        private CarEvChargePortOpenData() {
        }
    }

    public static class CarFuelDoorOpenData {
        public boolean fuelDoorIsOpen;
        public long timestamp;

        private CarFuelDoorOpenData() {
        }
    }

    public static class CarSpeedData {
        public float carSpeed;
        public long timestamp;

        private CarSpeedData() {
        }
    }

    public static class CarTractionControlActiveData {
        public long timestamp;
        public boolean tractionControlIsActive;

        private CarTractionControlActiveData() {
        }
    }

    public static class CarWheelTickDistanceData {
        public long frontLeftWheelDistanceMm;
        public long frontRightWheelDistanceMm;
        public long rearLeftWheelDistanceMm;
        public long rearRightWheelDistanceMm;
        public long sensorResetCount;
        public long timestamp;

        private CarWheelTickDistanceData() {
        }
    }

    public static class EnvironmentData {
        public float temperature;
        public long timestamp;

        private EnvironmentData() {
        }
    }

    public static class FuelLevelData {
        public float level;
        public long timestamp;

        private FuelLevelData() {
        }
    }

    public static class GearData {
        public int gear;
        public long timestamp;

        private GearData() {
        }
    }

    public static class IgnitionStateData {
        public int ignitionState;
        public long timestamp;

        private IgnitionStateData() {
        }
    }

    public static class NightData {
        public boolean isNightMode;
        public long timestamp;

        private NightData() {
        }
    }

    public static class OdometerData {
        public float kms;
        public long timestamp;

        private OdometerData() {
        }
    }

    public static class ParkingBrakeData {
        public boolean isEngaged;
        public long timestamp;

        private ParkingBrakeData() {
        }
    }

    public static class RpmData {
        public float rpm;
        public long timestamp;

        private RpmData() {
        }
    }

    public CarSensorEvent(int i, long j, int i2, int i3, int i4) {
        this.sensorType = i;
        this.timestamp = j;
        this.floatValues = new float[i2];
        this.intValues = new int[i3];
        this.longValues = new long[i4];
    }

    CarSensorEvent(int i, long j, float[] fArr, int[] iArr, long[] jArr) {
        this.sensorType = i;
        this.timestamp = j;
        this.floatValues = fArr;
        this.intValues = iArr;
        this.longValues = jArr;
    }

    public CarSensorEvent(Parcel parcel) {
        this.sensorType = parcel.readInt();
        this.timestamp = parcel.readLong();
        float[] fArr = new float[parcel.readInt()];
        this.floatValues = fArr;
        parcel.readFloatArray(fArr);
        int[] iArr = new int[parcel.readInt()];
        this.intValues = iArr;
        parcel.readIntArray(iArr);
        long[] jArr = new long[parcel.readInt()];
        this.longValues = jArr;
        parcel.readLongArray(jArr);
    }

    private void checkType(int i) {
        if (this.sensorType != i) {
            throw new UnsupportedOperationException(String.format("Invalid sensor type: expected %d, got %d", Integer.valueOf(i), Integer.valueOf(this.sensorType)));
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public CarAbsActiveData getCarAbsActiveData(CarAbsActiveData carAbsActiveData) {
        checkType(287310858);
        if (carAbsActiveData == null) {
            carAbsActiveData = new CarAbsActiveData();
        }
        carAbsActiveData.timestamp = this.timestamp;
        carAbsActiveData.absIsActive = this.intValues[0] == 1;
        return carAbsActiveData;
    }

    public CarEngineOilLevelData getCarEngineOilLevelData(CarEngineOilLevelData carEngineOilLevelData) {
        checkType(289407747);
        if (carEngineOilLevelData == null) {
            carEngineOilLevelData = new CarEngineOilLevelData();
        }
        carEngineOilLevelData.timestamp = this.timestamp;
        carEngineOilLevelData.engineOilLevel = this.intValues[0];
        return carEngineOilLevelData;
    }

    public CarEvBatteryChargeRateData getCarEvBatteryChargeRateData(CarEvBatteryChargeRateData carEvBatteryChargeRateData) {
        checkType(291504908);
        if (carEvBatteryChargeRateData == null) {
            carEvBatteryChargeRateData = new CarEvBatteryChargeRateData();
        }
        carEvBatteryChargeRateData.timestamp = this.timestamp;
        carEvBatteryChargeRateData.evChargeRate = this.floatValues[0];
        return carEvBatteryChargeRateData;
    }

    public CarEvBatteryLevelData getCarEvBatteryLevelData(CarEvBatteryLevelData carEvBatteryLevelData) {
        checkType(291504905);
        if (carEvBatteryLevelData == null) {
            carEvBatteryLevelData = new CarEvBatteryLevelData();
        }
        carEvBatteryLevelData.timestamp = this.timestamp;
        float[] fArr = this.floatValues;
        if (fArr == null) {
            carEvBatteryLevelData.evBatteryLevel = -1.0f;
            return carEvBatteryLevelData;
        }
        float f = fArr[0];
        if (f < 0.0f) {
            carEvBatteryLevelData.evBatteryLevel = -1.0f;
            return carEvBatteryLevelData;
        }
        carEvBatteryLevelData.evBatteryLevel = f;
        return carEvBatteryLevelData;
    }

    public CarEvChargePortConnectedData getCarEvChargePortConnectedData(CarEvChargePortConnectedData carEvChargePortConnectedData) {
        checkType(287310603);
        if (carEvChargePortConnectedData == null) {
            carEvChargePortConnectedData = new CarEvChargePortConnectedData();
        }
        carEvChargePortConnectedData.timestamp = this.timestamp;
        carEvChargePortConnectedData.evChargePortIsConnected = this.intValues[0] == 1;
        return carEvChargePortConnectedData;
    }

    public CarEvChargePortOpenData getCarEvChargePortOpenData(CarEvChargePortOpenData carEvChargePortOpenData) {
        checkType(287310602);
        if (carEvChargePortOpenData == null) {
            carEvChargePortOpenData = new CarEvChargePortOpenData();
        }
        carEvChargePortOpenData.timestamp = this.timestamp;
        carEvChargePortOpenData.evChargePortIsOpen = this.intValues[0] == 1;
        return carEvChargePortOpenData;
    }

    public CarFuelDoorOpenData getCarFuelDoorOpenData(CarFuelDoorOpenData carFuelDoorOpenData) {
        checkType(287310600);
        if (carFuelDoorOpenData == null) {
            carFuelDoorOpenData = new CarFuelDoorOpenData();
        }
        carFuelDoorOpenData.timestamp = this.timestamp;
        carFuelDoorOpenData.fuelDoorIsOpen = this.intValues[0] == 1;
        return carFuelDoorOpenData;
    }

    public CarSpeedData getCarSpeedData(CarSpeedData carSpeedData) {
        checkType(291504647);
        if (carSpeedData == null) {
            carSpeedData = new CarSpeedData();
        }
        carSpeedData.timestamp = this.timestamp;
        carSpeedData.carSpeed = this.floatValues[0];
        return carSpeedData;
    }

    public CarTractionControlActiveData getCarTractionControlActiveData(CarTractionControlActiveData carTractionControlActiveData) {
        checkType(287310859);
        if (carTractionControlActiveData == null) {
            carTractionControlActiveData = new CarTractionControlActiveData();
        }
        carTractionControlActiveData.timestamp = this.timestamp;
        carTractionControlActiveData.tractionControlIsActive = this.intValues[0] == 1;
        return carTractionControlActiveData;
    }

    public CarWheelTickDistanceData getCarWheelTickDistanceData(CarWheelTickDistanceData carWheelTickDistanceData) {
        checkType(290521862);
        if (carWheelTickDistanceData == null) {
            carWheelTickDistanceData = new CarWheelTickDistanceData();
        }
        carWheelTickDistanceData.timestamp = this.timestamp;
        carWheelTickDistanceData.sensorResetCount = this.longValues[0];
        carWheelTickDistanceData.frontLeftWheelDistanceMm = this.longValues[1];
        carWheelTickDistanceData.frontRightWheelDistanceMm = this.longValues[2];
        carWheelTickDistanceData.rearRightWheelDistanceMm = this.longValues[3];
        carWheelTickDistanceData.rearLeftWheelDistanceMm = this.longValues[4];
        return carWheelTickDistanceData;
    }

    public EnvironmentData getEnvironmentData(EnvironmentData environmentData) {
        checkType(291505923);
        if (environmentData == null) {
            environmentData = new EnvironmentData();
        }
        environmentData.timestamp = this.timestamp;
        environmentData.temperature = this.floatValues[0];
        return environmentData;
    }

    public FuelLevelData getFuelLevelData(FuelLevelData fuelLevelData) {
        checkType(291504903);
        if (fuelLevelData == null) {
            fuelLevelData = new FuelLevelData();
        }
        fuelLevelData.timestamp = this.timestamp;
        float[] fArr = this.floatValues;
        if (fArr == null) {
            fuelLevelData.level = -1.0f;
            return fuelLevelData;
        }
        float f = fArr[0];
        if (f < 0.0f) {
            fuelLevelData.level = -1.0f;
            return fuelLevelData;
        }
        fuelLevelData.level = f;
        return fuelLevelData;
    }

    public GearData getGearData(GearData gearData) {
        checkType(289408000);
        if (gearData == null) {
            gearData = new GearData();
        }
        gearData.timestamp = this.timestamp;
        gearData.gear = this.intValues[0];
        return gearData;
    }

    public IgnitionStateData getIgnitionStateData(IgnitionStateData ignitionStateData) {
        checkType(289408009);
        if (ignitionStateData == null) {
            ignitionStateData = new IgnitionStateData();
        }
        ignitionStateData.timestamp = this.timestamp;
        ignitionStateData.ignitionState = this.intValues[0];
        return ignitionStateData;
    }

    public NightData getNightData(NightData nightData) {
        checkType(287310855);
        if (nightData == null) {
            nightData = new NightData();
        }
        nightData.timestamp = this.timestamp;
        nightData.isNightMode = this.intValues[0] == 1;
        return nightData;
    }

    public OdometerData getOdometerData(OdometerData odometerData) {
        checkType(291504644);
        if (odometerData == null) {
            odometerData = new OdometerData();
        }
        odometerData.timestamp = this.timestamp;
        odometerData.kms = this.floatValues[0];
        return odometerData;
    }

    public ParkingBrakeData getParkingBrakeData(ParkingBrakeData parkingBrakeData) {
        checkType(287310850);
        if (parkingBrakeData == null) {
            parkingBrakeData = new ParkingBrakeData();
        }
        parkingBrakeData.timestamp = this.timestamp;
        parkingBrakeData.isEngaged = this.intValues[0] == 1;
        return parkingBrakeData;
    }

    public RpmData getRpmData(RpmData rpmData) {
        checkType(291504901);
        if (rpmData == null) {
            rpmData = new RpmData();
        }
        rpmData.timestamp = this.timestamp;
        rpmData.rpm = this.floatValues[0];
        return rpmData;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName() + "[");
        StringBuilder sb2 = new StringBuilder("type:");
        sb2.append(Integer.toHexString(this.sensorType));
        sb.append(sb2.toString());
        float[] fArr = this.floatValues;
        if (fArr != null && fArr.length > 0) {
            sb.append(" float values:");
            for (float f : this.floatValues) {
                sb.append(" " + f);
            }
        }
        int[] iArr = this.intValues;
        if (iArr != null && iArr.length > 0) {
            sb.append(" int values:");
            for (int i : this.intValues) {
                sb.append(" " + i);
            }
        }
        long[] jArr = this.longValues;
        if (jArr != null && jArr.length > 0) {
            sb.append(" long values:");
            for (long j : this.longValues) {
                sb.append(" " + j);
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.sensorType);
        parcel.writeLong(this.timestamp);
        parcel.writeInt(this.floatValues.length);
        parcel.writeFloatArray(this.floatValues);
        parcel.writeInt(this.intValues.length);
        parcel.writeIntArray(this.intValues);
        parcel.writeInt(this.longValues.length);
        parcel.writeLongArray(this.longValues);
    }
}
