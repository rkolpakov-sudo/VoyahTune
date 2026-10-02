package android.car.projection;

import android.annotation.SystemApi;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.IntArray;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class ProjectionStatus implements Parcelable {
    public static final Parcelable.Creator<ProjectionStatus> CREATOR = new Parcelable.Creator<ProjectionStatus>() { // from class: android.car.projection.ProjectionStatus.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProjectionStatus createFromParcel(Parcel parcel) {
            return new ProjectionStatus(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProjectionStatus[] newArray(int i) {
            return new ProjectionStatus[i];
        }
    };
    public static final int PROJECTION_STATE_ACTIVE_BACKGROUND = 3;
    public static final int PROJECTION_STATE_ACTIVE_FOREGROUND = 2;
    public static final int PROJECTION_STATE_INACTIVE = 0;
    private static final int PROJECTION_STATE_MAX = 3;
    public static final int PROJECTION_STATE_READY_TO_PROJECT = 1;
    private static final int PROJECTION_TRANSPORT_MAX = 2;
    public static final int PROJECTION_TRANSPORT_NONE = 0;
    public static final int PROJECTION_TRANSPORT_USB = 1;
    public static final int PROJECTION_TRANSPORT_WIFI = 2;
    private final List<MobileDevice> mConnectedMobileDevices;
    private final Bundle mExtras;
    private final String mPackageName;
    private final int mState;
    private final int mTransport;

    public static final class Builder {
        private Bundle mExtras;
        private List<MobileDevice> mMobileDevices;
        private final String mPackageName;
        private final int mState;
        private int mTransport;

        private Builder(String str, int i) {
            this.mTransport = 0;
            this.mMobileDevices = new ArrayList();
            if (str == null) {
                throw new IllegalArgumentException("Package name can't be null");
            }
            if (i < 0 || i > 3) {
                throw new IllegalArgumentException("Invalid projection state: " + i);
            }
            this.mPackageName = str;
            this.mState = i;
        }

        public Builder addMobileDevice(MobileDevice mobileDevice) {
            this.mMobileDevices.add(mobileDevice);
            return this;
        }

        public ProjectionStatus build() {
            return new ProjectionStatus(this);
        }

        public Builder setExtras(Bundle bundle) {
            this.mExtras = bundle;
            return this;
        }

        public Builder setProjectionTransport(int i) {
            ProjectionStatus.checkProjectionTransport(i);
            this.mTransport = i;
            return this;
        }
    }

    public static final class MobileDevice implements Parcelable {
        public static final Parcelable.Creator<MobileDevice> CREATOR = new Parcelable.Creator<MobileDevice>() { // from class: android.car.projection.ProjectionStatus.MobileDevice.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public MobileDevice createFromParcel(Parcel parcel) {
                return new MobileDevice(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public MobileDevice[] newArray(int i) {
                return new MobileDevice[i];
            }
        };
        private final int[] mAvailableTransports;
        private final Bundle mExtras;
        private final int mId;
        private final String mName;
        private final boolean mProjecting;

        public static final class Builder {
            private IntArray mAvailableTransports;
            private Bundle mExtras;
            private int mId;
            private String mName;
            private boolean mProjecting;

            private Builder(int i, String str) {
                this.mAvailableTransports = new IntArray();
                this.mId = i;
                if (str == null) {
                    throw new IllegalArgumentException("Name of the device can't be null");
                }
                this.mName = str;
            }

            public Builder addTransport(int i) {
                ProjectionStatus.checkProjectionTransport(i);
                this.mAvailableTransports.add(i);
                return this;
            }

            public MobileDevice build() {
                return new MobileDevice(this);
            }

            public Builder setExtras(Bundle bundle) {
                this.mExtras = bundle;
                return this;
            }

            public Builder setProjecting(boolean z) {
                this.mProjecting = z;
                return this;
            }
        }

        private MobileDevice(Builder builder) {
            this.mId = builder.mId;
            this.mName = builder.mName;
            this.mAvailableTransports = builder.mAvailableTransports.toArray();
            this.mProjecting = builder.mProjecting;
            this.mExtras = builder.mExtras == null ? null : new Bundle(builder.mExtras);
        }

        private MobileDevice(Parcel parcel) {
            this.mId = parcel.readInt();
            this.mName = parcel.readString();
            this.mAvailableTransports = parcel.createIntArray();
            this.mProjecting = parcel.readBoolean();
            this.mExtras = parcel.readBundle(getClass().getClassLoader());
        }

        public static Builder builder(int i, String str) {
            return new Builder(i, str);
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public List<Integer> getAvailableTransports() {
            ArrayList arrayList = new ArrayList(this.mAvailableTransports.length);
            for (int i : this.mAvailableTransports) {
                arrayList.add(Integer.valueOf(i));
            }
            return arrayList;
        }

        public Bundle getExtras() {
            return this.mExtras == null ? new Bundle() : new Bundle(this.mExtras);
        }

        public int getId() {
            return this.mId;
        }

        public String getName() {
            return this.mName;
        }

        public boolean isProjecting() {
            return this.mProjecting;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("MobileDevice{mId=");
            sb.append(this.mId);
            sb.append(", mName='");
            sb.append(this.mName);
            sb.append("', mAvailableTransports=");
            sb.append(Arrays.toString(this.mAvailableTransports));
            sb.append(", mProjecting=");
            sb.append(this.mProjecting);
            sb.append(this.mExtras != null ? ", (has extras)" : "");
            sb.append('}');
            return sb.toString();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mId);
            parcel.writeString(this.mName);
            parcel.writeIntArray(this.mAvailableTransports);
            parcel.writeBoolean(this.mProjecting);
            parcel.writeBundle(this.mExtras);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ProjectionState {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ProjectionTransport {
    }

    private ProjectionStatus(Builder builder) {
        this.mPackageName = builder.mPackageName;
        this.mState = builder.mState;
        this.mTransport = builder.mTransport;
        this.mConnectedMobileDevices = new ArrayList(builder.mMobileDevices);
        this.mExtras = builder.mExtras == null ? null : new Bundle(builder.mExtras);
    }

    private ProjectionStatus(Parcel parcel) {
        this.mPackageName = parcel.readString();
        this.mState = parcel.readInt();
        this.mTransport = parcel.readInt();
        this.mExtras = parcel.readBundle(getClass().getClassLoader());
        this.mConnectedMobileDevices = parcel.createTypedArrayList(MobileDevice.CREATOR);
    }

    public static Builder builder(String str, int i) {
        return new Builder(str, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkProjectionTransport(int i) {
        if (i < 0 || i > 2) {
            throw new IllegalArgumentException("Invalid projection transport: " + i);
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public List<MobileDevice> getConnectedMobileDevices() {
        return new ArrayList(this.mConnectedMobileDevices);
    }

    public Bundle getExtras() {
        return this.mExtras == null ? new Bundle() : new Bundle(this.mExtras);
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public int getState() {
        return this.mState;
    }

    public int getTransport() {
        return this.mTransport;
    }

    public boolean isActive() {
        int i = this.mState;
        return i == 3 || i == 2;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ProjectionStatus{mPackageName='");
        sb.append(this.mPackageName);
        sb.append("', mState=");
        sb.append(this.mState);
        sb.append(", mTransport=");
        sb.append(this.mTransport);
        sb.append(", mConnectedMobileDevices=");
        sb.append(this.mConnectedMobileDevices);
        sb.append(this.mExtras != null ? " (has extras)" : "");
        sb.append('}');
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mPackageName);
        parcel.writeInt(this.mState);
        parcel.writeInt(this.mTransport);
        parcel.writeBundle(this.mExtras);
        parcel.writeTypedList(this.mConnectedMobileDevices);
    }
}
