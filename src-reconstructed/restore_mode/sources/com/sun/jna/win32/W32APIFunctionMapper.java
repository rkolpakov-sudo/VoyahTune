package com.sun.jna.win32;

import com.sun.jna.FunctionMapper;
import com.sun.jna.NativeLibrary;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class W32APIFunctionMapper implements FunctionMapper {
    private final String suffix;
    public static final FunctionMapper UNICODE = new W32APIFunctionMapper(true);
    public static final FunctionMapper ASCII = new W32APIFunctionMapper(false);

    protected W32APIFunctionMapper(boolean z) {
        this.suffix = z ? "W" : "A";
    }

    @Override // com.sun.jna.FunctionMapper
    public String getFunctionName(NativeLibrary nativeLibrary, Method method) {
        String name = method.getName();
        if (!name.endsWith("W") && !name.endsWith("A")) {
            try {
                return nativeLibrary.getFunction(name + this.suffix, 63).getName();
            } catch (UnsatisfiedLinkError unused) {
            }
        }
        return name;
    }
}
