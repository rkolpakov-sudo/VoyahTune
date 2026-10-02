package android.car.hardware.property;

/* JADX INFO: loaded from: classes.dex */
public class PropertyAccessDeniedSecurityException extends SecurityException {
    PropertyAccessDeniedSecurityException(int i, int i2) {
        super("Cars denied the access of property 0x" + Integer.toHexString(i) + " in area: " + Integer.toHexString(i2));
    }
}
