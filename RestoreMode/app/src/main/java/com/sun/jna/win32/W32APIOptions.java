package com.sun.jna.win32;

import com.sun.jna.Library;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public interface W32APIOptions extends StdCallLibrary {
    Map<String, Object> UNICODE_OPTIONS = Collections.unmodifiableMap(new HashMap<String, Object>() { // from class: com.sun.jna.win32.W32APIOptions.1
        private static final long serialVersionUID = 1;

        {
            put(Library.OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
            put(Library.OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
        }
    });
    Map<String, Object> ASCII_OPTIONS = Collections.unmodifiableMap(new HashMap<String, Object>() { // from class: com.sun.jna.win32.W32APIOptions.2
        private static final long serialVersionUID = 1;

        {
            put(Library.OPTION_TYPE_MAPPER, W32APITypeMapper.ASCII);
            put(Library.OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.ASCII);
        }
    });
    Map<String, Object> DEFAULT_OPTIONS = Boolean.getBoolean("w32.ascii") ? ASCII_OPTIONS : UNICODE_OPTIONS;
}
