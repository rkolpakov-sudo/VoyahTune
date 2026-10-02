package android.car.media;

import android.annotation.SystemApi;
import android.car.Car;
import android.car.CarLibLog;
import android.car.CarManagerBase;
import android.media.AudioDeviceAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class CarAudioManager extends CarManagerBase {

    @SystemApi
    public static final String AUDIOFOCUS_EXTRA_RECEIVE_DUCKING_EVENTS = "android.car.media.AUDIOFOCUS_EXTRA_RECEIVE_DUCKING_EVENTS";
    public static final String AUDIOFOCUS_EXTRA_REQUEST_ZONE_ID = "android.car.media.AUDIOFOCUS_EXTRA_REQUEST_ZONE_ID";

    @SystemApi
    public static final int INVALID_AUDIO_ZONE = -1;
    public static final int INVALID_VOLUME_GROUP_ID = -1;

    @SystemApi
    public static final int PRIMARY_AUDIO_ZONE = 0;
    private final AudioManager mAudioManager;
    private final ICarVolumeCallback mCarVolumeCallbackImpl;
    private final CopyOnWriteArrayList<CarVolumeCallback> mCarVolumeCallbacks;
    private final ICarAudio mService;

    public static abstract class CarVolumeCallback {
        public void onGroupVolumeChanged(int i, int i2, int i3) {
        }

        public void onMasterMuteChanged(int i, int i2) {
        }
    }

    public CarAudioManager(Car car, IBinder iBinder) {
        super(car);
        this.mCarVolumeCallbackImpl = new ICarVolumeCallback.Stub(this) { // from class: android.car.media.CarAudioManager.1
            final CarAudioManager this$0;

            {
                this.this$0 = this;
            }

            @Override // android.car.media.ICarVolumeCallback
            public void onGroupVolumeChanged(int i, int i2, int i3) {
                Iterator it = this.this$0.mCarVolumeCallbacks.iterator();
                while (it.hasNext()) {
                    ((CarVolumeCallback) it.next()).onGroupVolumeChanged(i, i2, i3);
                }
            }

            @Override // android.car.media.ICarVolumeCallback
            public void onMasterMuteChanged(int i, int i2) {
                Iterator it = this.this$0.mCarVolumeCallbacks.iterator();
                while (it.hasNext()) {
                    ((CarVolumeCallback) it.next()).onMasterMuteChanged(i, i2);
                }
            }
        };
        this.mService = ICarAudio.Stub.asInterface(iBinder);
        this.mAudioManager = (AudioManager) getContext().getSystemService(AudioManager.class);
        this.mCarVolumeCallbacks = new CopyOnWriteArrayList<>();
    }

    private List<AudioDeviceInfo> convertInputDevicesToDeviceInfos(List<AudioDeviceAttributes> list, int i) {
        int size = list.size();
        HashSet hashSet = new HashSet(size);
        for (int i2 = 0; i2 < size; i2++) {
            hashSet.add(list.get(i2).getAddress());
        }
        ArrayList arrayList = new ArrayList(list.size());
        for (AudioDeviceInfo audioDeviceInfo : this.mAudioManager.getDevices(i)) {
            if (audioDeviceInfo.isSource() && hashSet.contains(audioDeviceInfo.getAddress())) {
                arrayList.add(audioDeviceInfo);
            }
        }
        return arrayList;
    }

    private void registerVolumeCallback() {
        try {
            this.mService.registerVolumeCallback(this.mCarVolumeCallbackImpl.asBinder());
        } catch (RemoteException e) {
            Log.e(CarLibLog.TAG_CAR, "registerVolumeCallback failed", e);
        }
    }

    private void unregisterVolumeCallback() {
        try {
            this.mService.unregisterVolumeCallback(this.mCarVolumeCallbackImpl.asBinder());
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    public boolean clearZoneIdForUid(int i) {
        try {
            return this.mService.clearZoneIdForUid(i);
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
        }
    }

    @SystemApi
    public CarAudioPatchHandle createAudioPatch(String str, int i, int i2) {
        try {
            return this.mService.createAudioPatch(str, i, i2);
        } catch (RemoteException e) {
            return (CarAudioPatchHandle) handleRemoteExceptionFromCarService(e, null);
        }
    }

    @SystemApi
    public List<Integer> getAudioZoneIds() {
        try {
            int[] audioZoneIds = this.mService.getAudioZoneIds();
            ArrayList arrayList = new ArrayList(audioZoneIds.length);
            for (int i : audioZoneIds) {
                arrayList.add(Integer.valueOf(i));
            }
            return arrayList;
        } catch (RemoteException e) {
            return (List) handleRemoteExceptionFromCarService(e, Collections.emptyList());
        }
    }

    @SystemApi
    public String[] getExternalSources() {
        try {
            return this.mService.getExternalSources();
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
            return new String[0];
        }
    }

    @SystemApi
    public int getGroupMaxVolume(int i) {
        return getGroupMaxVolume(0, i);
    }

    @SystemApi
    public int getGroupMaxVolume(int i, int i2) {
        try {
            return this.mService.getGroupMaxVolume(i, i2);
        } catch (RemoteException e) {
            return ((Integer) handleRemoteExceptionFromCarService(e, 0)).intValue();
        }
    }

    @SystemApi
    public int getGroupMinVolume(int i) {
        return getGroupMinVolume(0, i);
    }

    @SystemApi
    public int getGroupMinVolume(int i, int i2) {
        try {
            return this.mService.getGroupMinVolume(i, i2);
        } catch (RemoteException e) {
            return ((Integer) handleRemoteExceptionFromCarService(e, 0)).intValue();
        }
    }

    @SystemApi
    public int getGroupVolume(int i) {
        return getGroupVolume(0, i);
    }

    @SystemApi
    public int getGroupVolume(int i, int i2) {
        try {
            return this.mService.getGroupVolume(i, i2);
        } catch (RemoteException e) {
            return ((Integer) handleRemoteExceptionFromCarService(e, 0)).intValue();
        }
    }

    @SystemApi
    public List<AudioDeviceInfo> getInputDevicesForZoneId(int i) {
        try {
            return convertInputDevicesToDeviceInfos(this.mService.getInputDevicesForZoneId(i), 1);
        } catch (RemoteException e) {
            return (List) handleRemoteExceptionFromCarService(e, new ArrayList());
        }
    }

    @SystemApi
    public AudioDeviceInfo getOutputDeviceForUsage(int i, int i2) {
        try {
            String outputDeviceAddressForUsage = this.mService.getOutputDeviceAddressForUsage(i, i2);
            if (outputDeviceAddressForUsage == null) {
                return null;
            }
            for (AudioDeviceInfo audioDeviceInfo : this.mAudioManager.getDevices(2)) {
                if (audioDeviceInfo.getAddress().equals(outputDeviceAddressForUsage)) {
                    return audioDeviceInfo;
                }
            }
            return null;
        } catch (RemoteException e) {
            return (AudioDeviceInfo) handleRemoteExceptionFromCarService(e, null);
        }
    }

    @SystemApi
    public int[] getUsagesForVolumeGroupId(int i) {
        return getUsagesForVolumeGroupId(0, i);
    }

    @SystemApi
    public int[] getUsagesForVolumeGroupId(int i, int i2) {
        try {
            return this.mService.getUsagesForVolumeGroupId(i, i2);
        } catch (RemoteException e) {
            return (int[]) handleRemoteExceptionFromCarService(e, new int[0]);
        }
    }

    @SystemApi
    public int getVolumeGroupCount() {
        return getVolumeGroupCount(0);
    }

    @SystemApi
    public int getVolumeGroupCount(int i) {
        try {
            return this.mService.getVolumeGroupCount(i);
        } catch (RemoteException e) {
            return ((Integer) handleRemoteExceptionFromCarService(e, 0)).intValue();
        }
    }

    @SystemApi
    public int getVolumeGroupIdForUsage(int i) {
        return getVolumeGroupIdForUsage(0, i);
    }

    @SystemApi
    public int getVolumeGroupIdForUsage(int i, int i2) {
        try {
            return this.mService.getVolumeGroupIdForUsage(i, i2);
        } catch (RemoteException e) {
            return ((Integer) handleRemoteExceptionFromCarService(e, 0)).intValue();
        }
    }

    public int getZoneIdForUid(int i) {
        try {
            return this.mService.getZoneIdForUid(i);
        } catch (RemoteException e) {
            return ((Integer) handleRemoteExceptionFromCarService(e, 0)).intValue();
        }
    }

    public boolean isDynamicRoutingEnabled() {
        try {
            return this.mService.isDynamicRoutingEnabled();
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
        }
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
        if (this.mService != null) {
            unregisterVolumeCallback();
        }
    }

    public void registerCarVolumeCallback(CarVolumeCallback carVolumeCallback) {
        Objects.requireNonNull(carVolumeCallback);
        if (this.mCarVolumeCallbacks.isEmpty()) {
            registerVolumeCallback();
        }
        this.mCarVolumeCallbacks.add(carVolumeCallback);
    }

    @SystemApi
    public void releaseAudioPatch(CarAudioPatchHandle carAudioPatchHandle) {
        try {
            this.mService.releaseAudioPatch(carAudioPatchHandle);
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    @SystemApi
    public void setBalanceTowardRight(float f) {
        try {
            this.mService.setBalanceTowardRight(f);
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    @SystemApi
    public void setFadeTowardFront(float f) {
        try {
            this.mService.setFadeTowardFront(f);
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    @SystemApi
    public void setGroupVolume(int i, int i2, int i3) {
        setGroupVolume(0, i, i2, i3);
    }

    @SystemApi
    public void setGroupVolume(int i, int i2, int i3, int i4) {
        try {
            this.mService.setGroupVolume(i, i2, i3, i4);
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    public boolean setZoneIdForUid(int i, int i2) {
        try {
            return this.mService.setZoneIdForUid(i, i2);
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, false)).booleanValue();
        }
    }

    public void unregisterCarVolumeCallback(CarVolumeCallback carVolumeCallback) {
        Objects.requireNonNull(carVolumeCallback);
        if (this.mCarVolumeCallbacks.remove(carVolumeCallback) && this.mCarVolumeCallbacks.isEmpty()) {
            unregisterVolumeCallback();
        }
    }
}
