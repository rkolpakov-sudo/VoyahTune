package android.car.hardware.property;

/* JADX INFO: loaded from: classes.dex */
public class PropertyNotAvailableException extends IllegalStateException {
    PropertyNotAvailableException(int i, int i2) {
        super("Property 0x" + Integer.toHexString(i) + " with area: " + Integer.toHexString(i2) + " is temporarily not available because the current state of cars.");
    }
}
