package android.car.hardware;

import android.annotation.SystemApi;
import android.car.VehiclePropertyType;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class CarPropertyConfig<T> implements Parcelable {
    public static final Parcelable.Creator<CarPropertyConfig> CREATOR = new Parcelable.Creator<CarPropertyConfig>() { // from class: android.car.hardware.CarPropertyConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CarPropertyConfig createFromParcel(Parcel parcel) {
            return new CarPropertyConfig(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CarPropertyConfig[] newArray(int i) {
            return new CarPropertyConfig[i];
        }
    };
    public static final int VEHICLE_PROPERTY_ACCESS_NONE = 0;
    public static final int VEHICLE_PROPERTY_ACCESS_READ = 1;
    public static final int VEHICLE_PROPERTY_ACCESS_READ_WRITE = 3;
    public static final int VEHICLE_PROPERTY_ACCESS_WRITE = 2;
    public static final int VEHICLE_PROPERTY_CHANGE_MODE_CONTINUOUS = 2;
    public static final int VEHICLE_PROPERTY_CHANGE_MODE_ONCHANGE = 1;
    public static final int VEHICLE_PROPERTY_CHANGE_MODE_STATIC = 0;
    private final int mAccess;
    private final int mAreaType;
    private final int mChangeMode;
    private final ArrayList<Integer> mConfigArray;
    private final String mConfigString;
    private final float mMaxSampleRate;
    private final float mMinSampleRate;
    private final int mPropertyId;
    private final SparseArray<AreaConfig<T>> mSupportedAreas;
    private final Class<T> mType;

    public static class AreaConfig<T> implements Parcelable {
        public static final Parcelable.Creator<AreaConfig<Object>> CREATOR = getCreator(Object.class);
        private final T mMaxValue;
        private final T mMinValue;

        private AreaConfig(Parcel parcel) {
            this.mMinValue = (T) parcel.readValue(getClass().getClassLoader());
            this.mMaxValue = (T) parcel.readValue(getClass().getClassLoader());
        }

        private AreaConfig(T t, T t2) {
            this.mMinValue = t;
            this.mMaxValue = t2;
        }

        private static <E> Parcelable.Creator<AreaConfig<E>> getCreator(Class<E> cls) {
            return new Parcelable.Creator<AreaConfig<E>>(cls) { // from class: android.car.hardware.CarPropertyConfig.AreaConfig.1
                final Class val$clazz;

                {
                    this.val$clazz = cls;
                }

                @Override // android.os.Parcelable.Creator
                public AreaConfig<E> createFromParcel(Parcel parcel) {
                    return new AreaConfig<>(parcel);
                }

                @Override // android.os.Parcelable.Creator
                public AreaConfig<E>[] newArray(int i) {
                    return (AreaConfig[]) Array.newInstance((Class<?>) this.val$clazz, i);
                }
            };
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public T getMaxValue() {
            return this.mMaxValue;
        }

        public T getMinValue() {
            return this.mMinValue;
        }

        public String toString() {
            return "CarAreaConfig{mMinValue=" + this.mMinValue + ", mMaxValue=" + this.mMaxValue + '}';
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeValue(this.mMinValue);
            parcel.writeValue(this.mMaxValue);
        }
    }

    @SystemApi
    public static class Builder<T> {
        private int mAccess;
        private final int mAreaType;
        private int mChangeMode;
        private final ArrayList<Integer> mConfigArray;
        private String mConfigString;
        private float mMaxSampleRate;
        private float mMinSampleRate;
        private final int mPropertyId;
        private final SparseArray<AreaConfig<T>> mSupportedAreas;
        private final Class<T> mType;

        private Builder(int i, int i2, int i3, Class<T> cls) {
            this.mAreaType = i2;
            this.mConfigArray = new ArrayList<>();
            this.mPropertyId = i3;
            if (i != 0) {
                this.mSupportedAreas = new SparseArray<>(i);
            } else {
                this.mSupportedAreas = new SparseArray<>();
            }
            this.mType = cls;
        }

        /* JADX WARN: Multi-variable type inference failed */
        private boolean isRangeAvailable(T t, T t2) {
            if (t == 0 || t2 == 0) {
                return false;
            }
            int i = this.mPropertyId & VehiclePropertyType.MASK;
            if (i == 4194304) {
                return (((Integer) t).intValue() == 0 && ((Integer) t2).intValue() == 0) ? false : true;
            }
            if (i == 5242880) {
                return (((Long) t).longValue() == 0 && ((Long) t2).longValue() == 0) ? false : true;
            }
            if (i != 6291456) {
                return false;
            }
            return (((Float) t).floatValue() == 0.0f && ((Float) t2).floatValue() == 0.0f) ? false : true;
        }

        public Builder<T> addArea(int i) {
            return addAreaConfig(i, null, null);
        }

        public Builder<T> addAreaConfig(int i, T t, T t2) {
            if (isRangeAvailable(t, t2)) {
                this.mSupportedAreas.put(i, new AreaConfig<>(t, t2));
                return this;
            }
            this.mSupportedAreas.put(i, null);
            return this;
        }

        public Builder<T> addAreas(int[] iArr) {
            for (int i : iArr) {
                this.mSupportedAreas.put(i, null);
            }
            return this;
        }

        public CarPropertyConfig<T> build() {
            return new CarPropertyConfig<>(this.mAccess, this.mAreaType, this.mChangeMode, this.mConfigArray, this.mConfigString, this.mMaxSampleRate, this.mMinSampleRate, this.mPropertyId, this.mSupportedAreas, this.mType);
        }

        public Builder<T> setAccess(int i) {
            this.mAccess = i;
            return this;
        }

        public Builder<T> setChangeMode(int i) {
            this.mChangeMode = i;
            return this;
        }

        public Builder<T> setConfigArray(ArrayList<Integer> arrayList) {
            this.mConfigArray.clear();
            this.mConfigArray.addAll(arrayList);
            return this;
        }

        public Builder<T> setConfigString(String str) {
            this.mConfigString = str;
            return this;
        }

        public Builder<T> setMaxSampleRate(float f) {
            this.mMaxSampleRate = f;
            return this;
        }

        public Builder<T> setMinSampleRate(float f) {
            this.mMinSampleRate = f;
            return this;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface VehiclePropertyAccessType {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface VehiclePropertyChangeModeType {
    }

    private CarPropertyConfig(int i, int i2, int i3, ArrayList<Integer> arrayList, String str, float f, float f2, int i4, SparseArray<AreaConfig<T>> sparseArray, Class<T> cls) {
        this.mAccess = i;
        this.mAreaType = i2;
        this.mChangeMode = i3;
        this.mConfigArray = arrayList;
        this.mConfigString = str;
        this.mMaxSampleRate = f;
        this.mMinSampleRate = f2;
        this.mPropertyId = i4;
        this.mSupportedAreas = sparseArray;
        this.mType = cls;
    }

    private CarPropertyConfig(Parcel parcel) {
        this.mAccess = parcel.readInt();
        this.mAreaType = parcel.readInt();
        this.mChangeMode = parcel.readInt();
        int i = parcel.readInt();
        this.mConfigArray = new ArrayList<>(i);
        for (int i2 = 0; i2 < i; i2++) {
            this.mConfigArray.add(Integer.valueOf(parcel.readInt()));
        }
        this.mConfigString = parcel.readString();
        this.mMaxSampleRate = parcel.readFloat();
        this.mMinSampleRate = parcel.readFloat();
        this.mPropertyId = parcel.readInt();
        int i3 = parcel.readInt();
        this.mSupportedAreas = new SparseArray<>(i3);
        for (int i4 = 0; i4 < i3; i4++) {
            this.mSupportedAreas.put(parcel.readInt(), (AreaConfig) parcel.readParcelable(getClass().getClassLoader()));
        }
        String string = parcel.readString();
        try {
            this.mType = (Class<T>) Class.forName(string);
        } catch (ClassNotFoundException unused) {
            throw new IllegalArgumentException("Class not found: " + string);
        }
    }

    public static <T> Builder<T> newBuilder(Class<T> cls, int i, int i2) {
        return new Builder<>(0, i2, i, cls);
    }

    @SystemApi
    public static <T> Builder<T> newBuilder(Class<T> cls, int i, int i2, int i3) {
        return new Builder<>(i3, i2, i, cls);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getAccess() {
        return this.mAccess;
    }

    public int getAreaCount() {
        return this.mSupportedAreas.size();
    }

    public int[] getAreaIds() {
        int size = this.mSupportedAreas.size();
        int[] iArr = new int[size];
        for (int i = 0; i < size; i++) {
            iArr[i] = this.mSupportedAreas.keyAt(i);
        }
        return iArr;
    }

    public int getAreaType() {
        return this.mAreaType;
    }

    public int getChangeMode() {
        return this.mChangeMode;
    }

    public List<Integer> getConfigArray() {
        return Collections.unmodifiableList(this.mConfigArray);
    }

    public String getConfigString() {
        return this.mConfigString;
    }

    public int getFirstAndOnlyAreaId() {
        if (this.mSupportedAreas.size() == 1) {
            return this.mSupportedAreas.keyAt(0);
        }
        throw new IllegalStateException("Expected one and only area in this property. Prop: 0x" + Integer.toHexString(this.mPropertyId));
    }

    public float getMaxSampleRate() {
        return this.mMaxSampleRate;
    }

    public T getMaxValue() {
        AreaConfig<T> areaConfig = this.mSupportedAreas.get(0);
        if (areaConfig == null) {
            return null;
        }
        return areaConfig.getMaxValue();
    }

    public T getMaxValue(int i) {
        AreaConfig<T> areaConfig = this.mSupportedAreas.get(i);
        if (areaConfig == null) {
            return null;
        }
        return areaConfig.getMaxValue();
    }

    public float getMinSampleRate() {
        return this.mMinSampleRate;
    }

    public T getMinValue() {
        AreaConfig<T> areaConfig = this.mSupportedAreas.get(0);
        if (areaConfig == null) {
            return null;
        }
        return areaConfig.getMinValue();
    }

    public T getMinValue(int i) {
        AreaConfig<T> areaConfig = this.mSupportedAreas.get(i);
        if (areaConfig == null) {
            return null;
        }
        return areaConfig.getMinValue();
    }

    public int getPropertyId() {
        return this.mPropertyId;
    }

    public Class<T> getPropertyType() {
        return this.mType;
    }

    public boolean hasArea(int i) {
        return this.mSupportedAreas.indexOfKey(i) >= 0;
    }

    public boolean isGlobalProperty() {
        return this.mAreaType == 0;
    }

    public String toString() {
        return "CarPropertyConfig{mPropertyId=" + this.mPropertyId + ", mAccess=" + this.mAccess + ", mAreaType=" + this.mAreaType + ", mChangeMode=" + this.mChangeMode + ", mConfigArray=" + this.mConfigArray + ", mConfigString=" + this.mConfigString + ", mMaxSampleRate=" + this.mMaxSampleRate + ", mMinSampleRate=" + this.mMinSampleRate + ", mSupportedAreas=" + this.mSupportedAreas + ", mType=" + this.mType + '}';
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mAccess);
        parcel.writeInt(this.mAreaType);
        parcel.writeInt(this.mChangeMode);
        parcel.writeInt(this.mConfigArray.size());
        for (int i2 = 0; i2 < this.mConfigArray.size(); i2++) {
            parcel.writeInt(this.mConfigArray.get(i2).intValue());
        }
        parcel.writeString(this.mConfigString);
        parcel.writeFloat(this.mMaxSampleRate);
        parcel.writeFloat(this.mMinSampleRate);
        parcel.writeInt(this.mPropertyId);
        parcel.writeInt(this.mSupportedAreas.size());
        for (int i3 = 0; i3 < this.mSupportedAreas.size(); i3++) {
            parcel.writeInt(this.mSupportedAreas.keyAt(i3));
            parcel.writeParcelable(this.mSupportedAreas.valueAt(i3), i);
        }
        parcel.writeString(this.mType.getName());
    }
}
