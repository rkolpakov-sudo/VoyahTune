package com.sun.jna;

/* JADX INFO: loaded from: classes.dex */
public class FromNativeContext {
    private Class<?> type;

    FromNativeContext(Class<?> cls) {
        this.type = cls;
    }

    public Class<?> getTargetType() {
        return this.type;
    }
}
