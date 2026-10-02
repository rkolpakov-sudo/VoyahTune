package ru.big.town.anative;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.util.Log;
import dalvik.system.PathClassLoader;
import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.TimeoutException;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceOemTransport implements OemCommandSender.Transport {
    private static final VoiceOemTransport INSTANCE = new VoiceOemTransport();
    private boolean connected;
    private final Object connection = new Object();
    private Context context;
    private long deadline;
    private Object initListener;
    private Class<?> listenerClass;
    private Object manager;
    private Class<?> managerClass;
    private String selectedField;
    private Object selectedState;
    private Method setter;
    private Class<?> stateClass;

    VoiceOemTransport() {
    }

    static VoiceOemTransport get(Context context) {
        VoiceOemTransport voiceOemTransport = INSTANCE;
        synchronized (voiceOemTransport) {
            if (voiceOemTransport.context == null) {
                voiceOemTransport.context = context.getApplicationContext();
            }
        }
        return voiceOemTransport;
    }

    @Override // ru.big.town.anative.OemCommandSender.Transport
    public void prepare(String str, long j) throws Exception {
        this.deadline = j;
        this.selectedState = null;
        this.selectedField = null;
        if (this.context.checkSelfPermission("com.qinggan.permission.WRITE_CANBUS") != 0) {
            throw new SecurityException("WRITE_CANBUS not granted");
        }
        if (SystemClock.elapsedRealtime() >= this.deadline) {
            throw new TimeoutException();
        }
        if (this.managerClass == null) {
            loadApi();
        }
        try {
            Object obj = this.stateClass.getField(str).get(null);
            this.selectedState = obj;
            if (!this.stateClass.isInstance(obj)) {
                throw new UnsupportedOperationException(str);
            }
            this.selectedField = str;
            if (this.manager == null) {
                if (this.initListener == null) {
                    this.initListener = Proxy.newProxyInstance(this.listenerClass.getClassLoader(), new Class[]{this.listenerClass}, new InvocationHandler() { // from class: ru.big.town.anative.VoiceOemTransport$$ExternalSyntheticLambda0
                        @Override // java.lang.reflect.InvocationHandler
                        public final Object invoke(Object obj2, Method method, Object[] objArr) {
                            return VoiceOemTransport.this.m2162lambda$prepare$0$rubigtownanativeVoiceOemTransport(obj2, method, objArr);
                        }
                    });
                }
                Object objInvoke = invoke(this.managerClass.getMethod("getInstance", Context.class, this.listenerClass), null, this.context, this.initListener);
                this.manager = objInvoke;
                if (objInvoke == null) {
                    throw new UnsupportedOperationException("No CanBusManager instance");
                }
            }
            synchronized (this.connection) {
                while (!this.connected) {
                    long jElapsedRealtime = this.deadline - SystemClock.elapsedRealtime();
                    if (jElapsedRealtime <= 0) {
                        throw new TimeoutException();
                    }
                    this.connection.wait(jElapsedRealtime);
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new UnsupportedOperationException(str, e);
        }
    }

    /* JADX WARN: Code duplicated, block: B:14:0x0024  */
    /* JADX INFO: renamed from: lambda$prepare$0$ru-big-town-anative-VoiceOemTransport, reason: not valid java name */
    /* synthetic */ Object m2162lambda$prepare$0$rubigtownanativeVoiceOemTransport(Object obj, Method method, Object[] objArr) {
        boolean z = true;
        if (method.getName().equals("onConnectStatusChange")) {
            synchronized (this.connection) {
                if (objArr != null) {
                    try {
                        if (objArr.length != 1 || !Boolean.TRUE.equals(objArr[0])) {
                            z = false;
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                } else {
                    z = false;
                }
                this.connected = z;
                this.connection.notifyAll();
            }
            return null;
        }
        if (method.getName().equals("hashCode")) {
            return Integer.valueOf(System.identityHashCode(obj));
        }
        if (method.getName().equals("equals")) {
            return Boolean.valueOf(objArr != null && objArr.length == 1 && obj == objArr[0]);
        }
        if (method.getName().equals("toString")) {
            return "VoyahTune voice CAN connection";
        }
        return null;
    }

    @Override // ru.big.town.anative.OemCommandSender.Transport
    public int send(String str, int i) throws Exception {
        if (!str.equals(this.selectedField) || this.selectedState == null) {
            throw new IllegalStateException("Not prepared");
        }
        if (SystemClock.elapsedRealtime() >= this.deadline) {
            throw new TimeoutException();
        }
        synchronized (this.connection) {
            if (!this.connected) {
                throw new TimeoutException();
            }
        }
        try {
            try {
                Object objInvoke = invoke(this.setter, this.manager, this.selectedState, Integer.valueOf(i));
                int iIntValue = objInvoke instanceof Integer ? ((Integer) objInvoke).intValue() : -1;
                this.selectedState = null;
                this.selectedField = null;
                return iIntValue;
            } catch (Exception | LinkageError e) {
                Log.w("VoyahOemVoice", "OEM send failed for " + str, e);
                throw e;
            }
        } catch (Throwable th) {
            this.selectedState = null;
            this.selectedField = null;
            throw th;
        }
    }

    private void loadApi() {
        ClassLoader classLoader = this.context.getClassLoader();
        ArrayList arrayList = new ArrayList();
        arrayList.add(classLoader);
        File file = new File("/system/framework/QGVehicle.jar");
        if (file.isFile()) {
            arrayList.add(new PathClassLoader(file.getPath(), classLoader));
        }
        try {
            ApplicationInfo applicationInfo = this.context.getPackageManager().getApplicationInfo("com.qinggan.canbus.service", 1024);
            ArrayList arrayList2 = new ArrayList();
            if (applicationInfo.sharedLibraryFiles != null) {
                Collections.addAll(arrayList2, applicationInfo.sharedLibraryFiles);
            }
            arrayList2.add(applicationInfo.sourceDir);
            if (applicationInfo.splitSourceDirs != null) {
                Collections.addAll(arrayList2, applicationInfo.splitSourceDirs);
            }
            arrayList.add(new PathClassLoader(String.join(File.pathSeparator, arrayList2), classLoader));
        } catch (PackageManager.NameNotFoundException unused) {
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            try {
                Class<?> cls = Class.forName("com.qinggan.canbus.CanBusManager", false, (ClassLoader) it.next());
                ClassLoader classLoader2 = cls.getClassLoader();
                Class<?> cls2 = Class.forName("com.qinggan.canbus.VehicleState", false, classLoader2);
                Class<?> cls3 = Class.forName("com.qinggan.common.OnInitListener", false, classLoader2);
                Method method = cls.getMethod("setVehicleState", cls2, Integer.TYPE);
                cls.getMethod("getInstance", Context.class, cls3);
                cls3.getMethod("onConnectStatusChange", Boolean.TYPE);
                if (cls3.isInterface() && method.getReturnType() == Integer.TYPE) {
                    this.managerClass = cls;
                    this.stateClass = cls2;
                    this.listenerClass = cls3;
                    this.setter = method;
                    return;
                }
            } catch (LinkageError | ReflectiveOperationException e) {
                Log.d("VoyahOemVoice", "OEM API unavailable in class loader", e);
            }
        }
        throw new UnsupportedOperationException("CanBusManager API unavailable");
    }

    private static Object invoke(Method method, Object obj, Object... objArr) throws Exception {
        try {
            return method.invoke(obj, objArr);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception) {
                throw ((Exception) cause);
            }
            if (cause instanceof Error) {
                throw ((Error) cause);
            }
            throw e;
        }
    }
}
