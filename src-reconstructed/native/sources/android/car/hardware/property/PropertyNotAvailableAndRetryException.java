package android.car.hardware.property;

/* JADX INFO: loaded from: classes.dex */
public class PropertyNotAvailableAndRetryException extends IllegalStateException {
    PropertyNotAvailableAndRetryException(int i, int i2) {
        super("Property 0x" + Integer.toHexString(i) + " with area: " + Integer.toHexString(i2) + " is temporarily not available. Try the operation later.");
    }
}
