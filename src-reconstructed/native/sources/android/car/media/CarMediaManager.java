package android.car.media;

import android.annotation.SystemApi;
import android.car.Car;
import android.car.CarManagerBase;
import android.content.ComponentName;
import android.os.IBinder;
import android.os.RemoteException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public final class CarMediaManager extends CarManagerBase {
    public static final int MEDIA_SOURCE_MODE_BROWSE = 1;
    public static final int MEDIA_SOURCE_MODE_PLAYBACK = 0;
    private Map<MediaSourceChangedListener, ICarMediaSourceListener> mCallbackMap;
    private final Object mLock;
    private final ICarMedia mService;

    public interface MediaSourceChangedListener {
        void onMediaSourceChanged(ComponentName componentName);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface MediaSourceMode {
    }

    public CarMediaManager(Car car, IBinder iBinder) {
        super(car);
        this.mLock = new Object();
        this.mCallbackMap = new HashMap();
        this.mService = ICarMedia.Stub.asInterface(iBinder);
    }

    public void addMediaSourceListener(MediaSourceChangedListener mediaSourceChangedListener, int i) {
        try {
            ICarMediaSourceListener.Stub stub = new ICarMediaSourceListener.Stub(this, mediaSourceChangedListener) { // from class: android.car.media.CarMediaManager.1
                final CarMediaManager this$0;
                final MediaSourceChangedListener val$callback;

                {
                    this.this$0 = this;
                    this.val$callback = mediaSourceChangedListener;
                }

                @Override // android.car.media.ICarMediaSourceListener
                public void onMediaSourceChanged(ComponentName componentName) {
                    this.val$callback.onMediaSourceChanged(componentName);
                }
            };
            synchronized (this.mLock) {
                this.mCallbackMap.put(mediaSourceChangedListener, stub);
            }
            this.mService.registerMediaSourceListener(stub, i);
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    public List<ComponentName> getLastMediaSources(int i) {
        try {
            return this.mService.getLastMediaSources(i);
        } catch (RemoteException e) {
            return (List) handleRemoteExceptionFromCarService(e, null);
        }
    }

    public ComponentName getMediaSource(int i) {
        try {
            return this.mService.getMediaSource(i);
        } catch (RemoteException e) {
            return (ComponentName) handleRemoteExceptionFromCarService(e, null);
        }
    }

    public boolean isIndependentPlaybackConfig() {
        try {
            return this.mService.isIndependentPlaybackConfig();
        } catch (RemoteException e) {
            return ((Boolean) handleRemoteExceptionFromCarService(e, null)).booleanValue();
        }
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
        synchronized (this.mLock) {
            this.mCallbackMap.clear();
        }
    }

    public void removeMediaSourceListener(MediaSourceChangedListener mediaSourceChangedListener, int i) {
        try {
            synchronized (this.mLock) {
                this.mService.unregisterMediaSourceListener(this.mCallbackMap.remove(mediaSourceChangedListener), i);
            }
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    public void setIndependentPlaybackConfig(boolean z) {
        try {
            this.mService.setIndependentPlaybackConfig(z);
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    public void setMediaSource(ComponentName componentName, int i) {
        try {
            this.mService.setMediaSource(componentName, i);
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }
}
