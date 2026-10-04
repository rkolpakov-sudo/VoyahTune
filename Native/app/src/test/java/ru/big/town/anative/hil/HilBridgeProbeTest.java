package ru.big.town.anative.hil;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowPackageManager;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class HilBridgeProbeTest {

    @Test
    public void probe() throws Exception {
        Context ctx = RuntimeEnvironment.getApplication();

        Parcel p = Parcel.obtain();
        p.writeInterfaceToken("com.qinggan.canbus.ICanBusService");
        p.writeInt(1);
        p.writeInt(711);
        p.setDataPosition(0);
        p.enforceInterface("com.qinggan.canbus.ICanBusService");
        System.out.println("PROBE parcel ints: " + p.readInt() + "," + p.readInt());

        Parcel reply = Parcel.obtain();
        reply.writeNoException();
        reply.writeInt(42);
        reply.setDataPosition(0);
        reply.readException();
        System.out.println("PROBE readException ok val=" + reply.readInt());

        final AtomicInteger hits = new AtomicInteger();
        Binder local = new Binder() {
            @Override
            protected boolean onTransact(int code, Parcel data, Parcel replyArg, int flags)
                    throws RemoteException {
                hits.incrementAndGet();
                System.out.println("PROBE localBinder onTransact code=" + code);
                return true;
            }
        };
        boolean sent = local.transact(7, Parcel.obtain(), Parcel.obtain(), 0);
        System.out.println("PROBE local transact returned " + sent + " hits=" + hits.get());

        try {
            ClassLoader cl = new dalvik.system.PathClassLoader("/nonexistent/fake-canbus.apk",
                    ctx.getClassLoader());
            Class<?> c = Class.forName("com.qinggan.canbus.VehicleState", true, cl);
            System.out.println("PROBE PathClassLoader ok: " + c.getName() + " enum=" + c.isEnum()
                    + " sameLoader=" + (c.getClassLoader() == ctx.getClassLoader()));
        } catch (Throwable t) {
            System.out.println("PROBE PathClassLoader FAILED: " + t);
        }

        try {
            ApplicationInfo ai = ctx.getPackageManager().getApplicationInfo("com.qinggan.canbus", 0);
            System.out.println("PROBE pm already found sourceDir=" + ai.sourceDir);
        } catch (PackageManager.NameNotFoundException e) {
            System.out.println("PROBE pm NameNotFound (expected before install)");
        }

        ShadowPackageManager spm = Shadows.shadowOf(ctx.getPackageManager());
        for (Method m : ShadowPackageManager.class.getMethods()) {
            String n = m.getName().toLowerCase();
            if ((n.contains("package") || n.contains("applicationinfo"))
                    && (n.startsWith("add") || n.startsWith("set") || n.startsWith("install") || n.contains("upsert"))) {
                System.out.println("PROBE spm method: " + m);
            }
        }

        System.out.println("PROBE permission before grant: "
                + ctx.checkSelfPermission("com.qinggan.permission.WRITE_CANBUS"));
        Shadows.shadowOf((Application) ctx.getApplicationContext())
                .grantPermissions("com.qinggan.permission.WRITE_CANBUS");
        System.out.println("PROBE permission after grant: "
                + ctx.checkSelfPermission("com.qinggan.permission.WRITE_CANBUS"));

        final AtomicInteger connected = new AtomicInteger();
        boolean bound = ctx.bindService(
                new Intent("com.qinggan.canbus.CanBusService").setPackage("com.qinggan.canbus"),
                Context.BIND_AUTO_CREATE,
                ctx.getMainExecutor(),
                new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        connected.incrementAndGet();
                        System.out.println("PROBE onServiceConnected " + service);
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                    }
                });
        System.out.println("PROBE bindService returned " + bound + " connected=" + connected.get());

        for (Class<?> shadowCls : new Class<?>[] {
                org.robolectric.shadows.ShadowContextWrapper.class,
                org.robolectric.shadows.ShadowApplication.class}) {
            for (Method m : shadowCls.getMethods()) {
                if (m.getName().toLowerCase().contains("bind")) {
                    System.out.println("PROBE " + shadowCls.getSimpleName() + " bind-method: " + m);
                }
            }
        }
        System.out.println("PROBE done");
    }

    @Test
    public void probe2() throws Exception {
        Context ctx = RuntimeEnvironment.getApplication();
        Application app = (Application) ctx.getApplicationContext();

        Binder fake = new Binder() {
            @Override
            protected boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                    throws RemoteException {
                System.out.println("PROBE2 fake onTransact code=" + code + " reply=" + reply
                        + " flags=" + flags);
                if (code == 7) {
                    IBinder cb = data.readStrongBinder();
                    System.out.println("PROBE2 readStrongBinder ok=" + (cb != null));
                }
                if (reply != null) {
                    reply.writeNoException();
                }
                return true;
            }
        };

        final int[] hits = {0};
        final IBinder[] got = {null};
        ComponentName cn = new ComponentName("pkg", "cls");
        Intent intent = new Intent("act");
        org.robolectric.shadows.ShadowApplication shApp = Shadows.shadowOf(app);
        shApp.setComponentNameAndServiceForBindServiceForIntent(intent, cn, fake);
        shApp.setBindServiceCallsOnServiceConnectedDirectly(true);
        boolean bound = ctx.bindService(intent, Context.BIND_AUTO_CREATE, ctx.getMainExecutor(),
                new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        hits[0]++;
                        got[0] = service;
                        System.out.println("PROBE2 onServiceConnected sync hit, service=" + service);
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                    }
                });
        System.out.println("PROBE2 bindService returned " + bound + " hits=" + hits[0]
                + " serviceIsFake=" + (got[0] == fake));

        Parcel d = Parcel.obtain();
        d.writeStrongBinder(new Binder());
        boolean t = fake.transact(7, d, Parcel.obtain(), 0);
        System.out.println("PROBE2 transact7=" + t);

        Parcel onewayData = Parcel.obtain();
        onewayData.writeInterfaceToken("desc");
        onewayData.writeInt(1);
        onewayData.writeInt(665);
        onewayData.writeInt(1);
        try {
            boolean ot = fake.transact(36, onewayData, null, IBinder.FLAG_ONEWAY);
            System.out.println("PROBE2 oneway-null-reply transact=" + ot);
        } catch (Throwable ex) {
            System.out.println("PROBE2 oneway-null-reply FAILED: " + ex);
        }
        System.out.println("PROBE2 done");
    }
}
