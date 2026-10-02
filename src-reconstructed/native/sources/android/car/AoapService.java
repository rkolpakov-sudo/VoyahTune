package android.car;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public abstract class AoapService extends Service {
    public static final String KEY_DEVICE = "usb-device";
    public static final String KEY_RESULT = "result";
    public static final int MSG_CAN_SWITCH_TO_AOAP = 3;
    public static final int MSG_CAN_SWITCH_TO_AOAP_RESPONSE = 4;
    public static final int MSG_NEW_DEVICE_ATTACHED = 1;
    public static final int MSG_NEW_DEVICE_ATTACHED_RESPONSE = 2;
    public static final int RESULT_DEVICE_NOT_SUPPORTED = 1;
    public static final int RESULT_DO_NOT_SWITCH_TO_AOAP = 2;
    public static final int RESULT_OK = 0;
    private static final String TAG = "AoapService";
    private boolean mBound;
    private Messenger mMessenger;

    private static class IncomingHandler extends Handler {
        private final WeakReference<AoapService> mServiceRef;

        IncomingHandler(AoapService aoapService) {
            super(Looper.getMainLooper());
            this.mServiceRef = new WeakReference<>(aoapService);
        }

        private Message createResponseMessage(int i, int i2) {
            Message messageObtain = Message.obtain((Handler) null, i);
            Bundle bundle = new Bundle();
            bundle.putInt(AoapService.KEY_RESULT, i2);
            messageObtain.setData(bundle);
            return messageObtain;
        }

        private void sendResponse(Messenger messenger, int i, int i2) {
            try {
                messenger.send(createResponseMessage(i, i2));
            } catch (RemoteException e) {
                Log.e(AoapService.TAG, "Failed to send message", e);
            }
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            AoapService aoapService = this.mServiceRef.get();
            if (aoapService == null) {
                return;
            }
            Bundle data = message.getData();
            if (data == null) {
                Log.e(AoapService.TAG, "Ignoring message " + message.what + " without data");
                return;
            }
            Log.i(AoapService.TAG, "Message received: " + message.what);
            int i = message.what;
            if (i == 1) {
                UsbDevice usbDevice = (UsbDevice) data.getParcelable(AoapService.KEY_DEVICE);
                Objects.requireNonNull(usbDevice);
                int iIsDeviceSupported = aoapService.isDeviceSupported(usbDevice);
                if (iIsDeviceSupported == 0 || iIsDeviceSupported == 1) {
                    sendResponse(message.replyTo, 2, iIsDeviceSupported);
                    return;
                } else {
                    throw new IllegalArgumentException("Result can not be " + iIsDeviceSupported);
                }
            }
            if (i != 3) {
                Log.e(AoapService.TAG, "Unknown message received: " + message.what);
                return;
            }
            UsbDevice usbDevice2 = (UsbDevice) data.getParcelable(AoapService.KEY_DEVICE);
            Objects.requireNonNull(usbDevice2);
            int iCanSwitchToAoap = aoapService.canSwitchToAoap(usbDevice2);
            if (iCanSwitchToAoap == 0 || iCanSwitchToAoap == 1 || iCanSwitchToAoap == 2) {
                sendResponse(message.replyTo, 4, iCanSwitchToAoap);
            } else {
                throw new IllegalArgumentException("Result can not be " + iCanSwitchToAoap);
            }
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Result {
    }

    public int canSwitchToAoap(UsbDevice usbDevice) {
        return 0;
    }

    @Override // android.app.Service
    protected void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.write("Bound: " + this.mBound);
    }

    public abstract int isDeviceSupported(UsbDevice usbDevice);

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (this.mBound) {
            Log.w(TAG, "Received onBind event when the service was already bound");
        }
        this.mBound = true;
        return this.mMessenger.getBinder();
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mMessenger = new Messenger(new IncomingHandler(this));
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        this.mBound = false;
        return super.onUnbind(intent);
    }
}
