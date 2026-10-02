package android.car;

import android.annotation.SystemApi;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import android.view.Display;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/* JADX INFO: loaded from: classes.dex */
public class CarOccupantZoneManager extends CarManagerBase {
    public static final int DISPLAY_TYPE_AUXILIARY = 5;
    public static final int DISPLAY_TYPE_HUD = 3;
    public static final int DISPLAY_TYPE_INPUT = 4;
    public static final int DISPLAY_TYPE_INSTRUMENT_CLUSTER = 2;
    public static final int DISPLAY_TYPE_MAIN = 1;
    public static final int DISPLAY_TYPE_UNKNOWN = 0;
    public static final int OCCUPANT_TYPE_DRIVER = 0;
    public static final int OCCUPANT_TYPE_FRONT_PASSENGER = 1;
    public static final int OCCUPANT_TYPE_INVALID = -1;
    public static final int OCCUPANT_TYPE_REAR_PASSENGER = 2;
    private static final String TAG = "CarOccupantZoneManager";
    public static final int ZONE_CONFIG_CHANGE_FLAG_AUDIO = 4;
    public static final int ZONE_CONFIG_CHANGE_FLAG_DISPLAY = 1;
    public static final int ZONE_CONFIG_CHANGE_FLAG_USER = 2;
    private final ICarOccupantZoneCallbackImpl mBinderCallback;
    private final DisplayManager mDisplayManager;
    private final EventHandler mEventHandler;
    private final CopyOnWriteArrayList<OccupantZoneConfigChangeListener> mListeners;
    private final ICarOccupantZone mService;

    @Target({ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DisplayTypeEnum {
    }

    private final class EventHandler extends Handler {
        private static final int MSG_ZONE_CHANGE = 1;
        final CarOccupantZoneManager this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        private EventHandler(CarOccupantZoneManager carOccupantZoneManager, Looper looper) {
            super(looper);
            this.this$0 = carOccupantZoneManager;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void dispatchOnOccupantZoneConfigChanged(int i) {
            sendMessage(obtainMessage(1, i, 0));
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 1) {
                this.this$0.handleOnOccupantZoneConfigChanged(message.arg1);
                return;
            }
            Log.e(CarOccupantZoneManager.TAG, "Unknown msg not handdled:" + message.what);
        }
    }

    private static class ICarOccupantZoneCallbackImpl extends ICarOccupantZoneCallback.Stub {
        private final WeakReference<CarOccupantZoneManager> mManager;

        private ICarOccupantZoneCallbackImpl(CarOccupantZoneManager carOccupantZoneManager) {
            this.mManager = new WeakReference<>(carOccupantZoneManager);
        }

        @Override // android.car.ICarOccupantZoneCallback
        public void onOccupantZoneConfigChanged(int i) {
            CarOccupantZoneManager carOccupantZoneManager = this.mManager.get();
            if (carOccupantZoneManager != null) {
                carOccupantZoneManager.mEventHandler.dispatchOnOccupantZoneConfigChanged(i);
            }
        }
    }

    @Target({ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OccupantTypeEnum {
    }

    public interface OccupantZoneConfigChangeListener {
        void onOccupantZoneConfigChanged(int i);
    }

    public static final class OccupantZoneInfo implements Parcelable {
        public static final Parcelable.Creator<OccupantZoneInfo> CREATOR = new Parcelable.Creator<OccupantZoneInfo>() { // from class: android.car.CarOccupantZoneManager.OccupantZoneInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public OccupantZoneInfo createFromParcel(Parcel parcel) {
                return new OccupantZoneInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public OccupantZoneInfo[] newArray(int i) {
                return new OccupantZoneInfo[i];
            }
        };
        public static final int INVALID_ZONE_ID = -1;
        public final int occupantType;
        public final int seat;
        public int zoneId;

        public OccupantZoneInfo(int i, int i2, int i3) {
            this.zoneId = i;
            this.occupantType = i2;
            this.seat = i3;
        }

        public OccupantZoneInfo(Parcel parcel) {
            this.zoneId = parcel.readInt();
            this.occupantType = parcel.readInt();
            this.seat = parcel.readInt();
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof OccupantZoneInfo)) {
                return false;
            }
            OccupantZoneInfo occupantZoneInfo = (OccupantZoneInfo) obj;
            return this.zoneId == occupantZoneInfo.zoneId && this.occupantType == occupantZoneInfo.occupantType && this.seat == occupantZoneInfo.seat;
        }

        public int hashCode() {
            return ((((391 + this.zoneId) * 17) + this.occupantType) * 17) + this.seat;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(64);
            sb.append("OccupantZoneInfo{zoneId=");
            sb.append(this.zoneId);
            sb.append(" type=");
            sb.append(this.occupantType);
            sb.append(" seat=");
            sb.append(Integer.toHexString(this.seat));
            sb.append("}");
            return sb.toString();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.zoneId);
            parcel.writeInt(this.occupantType);
            parcel.writeInt(this.seat);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @interface ZoneConfigChangeFlags {
    }

    public CarOccupantZoneManager(Car car, IBinder iBinder) {
        super(car);
        this.mListeners = new CopyOnWriteArrayList<>();
        this.mService = ICarOccupantZone.Stub.asInterface(iBinder);
        this.mBinderCallback = new ICarOccupantZoneCallbackImpl();
        this.mDisplayManager = (DisplayManager) getContext().getSystemService(DisplayManager.class);
        this.mEventHandler = new EventHandler(getEventHandler().getLooper());
    }

    private void assertNonNullDisplay(Display display) {
        if (display == null) {
            throw new IllegalArgumentException("null Display");
        }
    }

    private void assertNonNullOccupant(OccupantZoneInfo occupantZoneInfo) {
        if (occupantZoneInfo == null) {
            throw new IllegalArgumentException("null OccupantZoneInfo");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnOccupantZoneConfigChanged(int i) {
        Iterator<OccupantZoneConfigChangeListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onOccupantZoneConfigChanged(i);
        }
    }

    public boolean assignProfileUserToOccupantZone(OccupantZoneInfo occupantZoneInfo, int i) {
        assertNonNullOccupant(occupantZoneInfo);
        try {
            return this.mService.assignProfileUserToOccupantZone(occupantZoneInfo.zoneId, i);
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
        }
    }

    public List<Display> getAllDisplaysForOccupant(OccupantZoneInfo occupantZoneInfo) {
        Display display;
        assertNonNullOccupant(occupantZoneInfo);
        try {
            int[] allDisplaysForOccupantZone = this.mService.getAllDisplaysForOccupantZone(occupantZoneInfo.zoneId);
            ArrayList arrayList = new ArrayList(allDisplaysForOccupantZone.length);
            for (int i : allDisplaysForOccupantZone) {
                if (i != -1 && (display = this.mDisplayManager.getDisplay(i)) != null) {
                    arrayList.add(display);
                }
            }
            return arrayList;
        } catch (RemoteException e) {
            return (List) handleRemoteExceptionFromCarService(e, Collections.emptyList());
        }
    }

    public List<OccupantZoneInfo> getAllOccupantZones() {
        try {
            return this.mService.getAllOccupantZones();
        } catch (RemoteException e) {
            return (List) handleRemoteExceptionFromCarService(e, Collections.emptyList());
        }
    }

    @SystemApi
    public int getAudioZoneIdForOccupant(OccupantZoneInfo occupantZoneInfo) {
        assertNonNullOccupant(occupantZoneInfo);
        try {
            return this.mService.getAudioZoneIdForOccupant(occupantZoneInfo.zoneId);
        } catch (RemoteException e) {
            return ((Integer) handleRemoteExceptionFromCarService(e, null)).intValue();
        }
    }

    public Display getDisplayForOccupant(OccupantZoneInfo occupantZoneInfo, int i) {
        assertNonNullOccupant(occupantZoneInfo);
        try {
            int displayForOccupant = this.mService.getDisplayForOccupant(occupantZoneInfo.zoneId, i);
            if (displayForOccupant == -1) {
                return null;
            }
            return this.mDisplayManager.getDisplay(displayForOccupant);
        } catch (RemoteException e) {
            return (Display) handleRemoteExceptionFromCarService(e, null);
        }
    }

    public int getDisplayType(Display display) {
        assertNonNullDisplay(display);
        try {
            return this.mService.getDisplayType(display.getDisplayId());
        } catch (RemoteException e) {
            return ((Integer) handleRemoteExceptionFromCarService(e, 0)).intValue();
        }
    }

    @SystemApi
    public OccupantZoneInfo getOccupantForAudioZoneId(int i) {
        try {
            return this.mService.getOccupantForAudioZoneId(i);
        } catch (RemoteException e) {
            return (OccupantZoneInfo) handleRemoteExceptionFromCarService(e, null);
        }
    }

    public int getUserForOccupant(OccupantZoneInfo occupantZoneInfo) {
        assertNonNullOccupant(occupantZoneInfo);
        try {
            return this.mService.getUserForOccupant(occupantZoneInfo.zoneId);
        } catch (RemoteException e) {
            return ((Integer) handleRemoteExceptionFromCarService(e, -10000)).intValue();
        }
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
    }

    public void registerOccupantZoneConfigChangeListener(OccupantZoneConfigChangeListener occupantZoneConfigChangeListener) {
        if (this.mListeners.addIfAbsent(occupantZoneConfigChangeListener) && this.mListeners.size() == 1) {
            try {
                this.mService.registerCallback(this.mBinderCallback);
            } catch (RemoteException e) {
                handleRemoteExceptionFromCarService(e);
            }
        }
    }

    public void unregisterOccupantZoneConfigChangeListener(OccupantZoneConfigChangeListener occupantZoneConfigChangeListener) {
        if (this.mListeners.remove(occupantZoneConfigChangeListener) && this.mListeners.size() == 0) {
            try {
                this.mService.unregisterCallback(this.mBinderCallback);
            } catch (RemoteException unused) {
            }
        }
    }
}
