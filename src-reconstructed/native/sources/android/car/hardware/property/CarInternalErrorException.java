package android.car.hardware.property;

/* JADX INFO: loaded from: classes.dex */
public class CarInternalErrorException extends RuntimeException {
    CarInternalErrorException(int i, int i2) {
        super("Property 0x" + Integer.toHexString(i) + " with area: " + Integer.toHexString(i2) + " raised an internal error in cars.");
    }
}
