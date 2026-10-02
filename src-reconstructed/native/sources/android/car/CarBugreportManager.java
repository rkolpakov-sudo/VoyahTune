package android.car;

import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.Objects;
import libcore.io.IoUtils;

/* JADX INFO: loaded from: classes.dex */
public final class CarBugreportManager extends CarManagerBase {
    private final ICarBugreportService mService;

    public static abstract class CarBugreportManagerCallback {
        public static final int CAR_BUGREPORT_DUMPSTATE_CONNECTION_FAILED = 3;
        public static final int CAR_BUGREPORT_DUMPSTATE_FAILED = 1;
        public static final int CAR_BUGREPORT_IN_PROGRESS = 2;
        public static final int CAR_BUGREPORT_SERVICE_NOT_AVAILABLE = 4;

        @Retention(RetentionPolicy.SOURCE)
        public @interface CarBugreportErrorCode {
        }

        public void onError(int i) {
        }

        public void onFinished() {
        }

        public void onProgress(float f) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class CarBugreportManagerCallbackWrapper extends ICarBugreportCallback.Stub {
        private final WeakReference<CarBugreportManagerCallback> mWeakCallback;
        private final WeakReference<Handler> mWeakHandler;

        CarBugreportManagerCallbackWrapper(CarBugreportManagerCallback carBugreportManagerCallback, Handler handler) {
            this.mWeakCallback = new WeakReference<>(carBugreportManagerCallback);
            this.mWeakHandler = new WeakReference<>(handler);
        }

        @Override // android.car.ICarBugreportCallback
        public void onError(final int i) {
            final CarBugreportManagerCallback carBugreportManagerCallback = this.mWeakCallback.get();
            Handler handler = this.mWeakHandler.get();
            if (handler == null || carBugreportManagerCallback == null) {
                return;
            }
            handler.post(new Runnable(carBugreportManagerCallback, i) { // from class: android.car._$$Lambda$CarBugreportManager$CarBugreportManagerCallbackWrapper$6ynqvb6TW8a_N8urXMTzbjfrmsk
                public final CarBugreportManager.CarBugreportManagerCallback f$0;
                public final int f$1;

                {
                    this.f$0 = carBugreportManagerCallback;
                    this.f$1 = i;
                }

                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.onError(this.f$1);
                }
            });
        }

        @Override // android.car.ICarBugreportCallback
        public void onFinished() {
            final CarBugreportManagerCallback carBugreportManagerCallback = this.mWeakCallback.get();
            Handler handler = this.mWeakHandler.get();
            if (handler == null || carBugreportManagerCallback == null) {
                return;
            }
            Objects.requireNonNull(carBugreportManagerCallback);
            handler.post(new Runnable(carBugreportManagerCallback) { // from class: android.car._$$Lambda$xrCRTzZqIh_Vz_aS3bmIqYgze_s
                public final CarBugreportManager.CarBugreportManagerCallback f$0;

                {
                    this.f$0 = carBugreportManagerCallback;
                }

                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.onFinished();
                }
            });
        }

        @Override // android.car.ICarBugreportCallback
        public void onProgress(final float f) {
            final CarBugreportManagerCallback carBugreportManagerCallback = this.mWeakCallback.get();
            Handler handler = this.mWeakHandler.get();
            if (handler == null || carBugreportManagerCallback == null) {
                return;
            }
            handler.post(new Runnable(carBugreportManagerCallback, f) { // from class: android.car._$$Lambda$CarBugreportManager$CarBugreportManagerCallbackWrapper$SKlkbH2yc5GbUZezB8cF5ckvnkk
                public final CarBugreportManager.CarBugreportManagerCallback f$0;
                public final float f$1;

                {
                    this.f$0 = carBugreportManagerCallback;
                    this.f$1 = f;
                }

                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.onProgress(this.f$1);
                }
            });
        }
    }

    public CarBugreportManager(Car car, IBinder iBinder) {
        super(car);
        this.mService = ICarBugreportService.Stub.asInterface(iBinder);
    }

    public void cancelBugreport() {
        try {
            this.mService.cancelBugreport();
        } catch (RemoteException e) {
            handleRemoteExceptionFromCarService(e);
        }
    }

    @Override // android.car.CarManagerBase
    public void onCarDisconnected() {
    }

    public void requestBugreport(ParcelFileDescriptor parcelFileDescriptor, ParcelFileDescriptor parcelFileDescriptor2, CarBugreportManagerCallback carBugreportManagerCallback) {
        Objects.requireNonNull(parcelFileDescriptor);
        Objects.requireNonNull(parcelFileDescriptor2);
        Objects.requireNonNull(carBugreportManagerCallback);
        try {
            try {
                this.mService.requestBugreport(parcelFileDescriptor, parcelFileDescriptor2, new CarBugreportManagerCallbackWrapper(carBugreportManagerCallback, getEventHandler()));
            } catch (RemoteException e) {
                handleRemoteExceptionFromCarService(e);
            }
        } finally {
            IoUtils.closeQuietly(parcelFileDescriptor);
            IoUtils.closeQuietly(parcelFileDescriptor2);
        }
    }
}
