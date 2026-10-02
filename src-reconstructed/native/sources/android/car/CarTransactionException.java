package android.car;

/* JADX INFO: loaded from: classes.dex */
public class CarTransactionException extends UnsupportedOperationException {
    CarTransactionException(Throwable th, String str, Object... objArr) {
        super(String.format(str, objArr), th);
    }
}
