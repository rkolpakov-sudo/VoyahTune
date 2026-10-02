package ru.big.town.anative;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ru.big.town.hil.CanEmulatorCore;
import ru.big.town.hil.TxCode;
import ru.big.town.hil.TxResult;

/**
 * In-process stand-in for the CanBusService binder: every app transaction is
 * decoded in the AIDL layout the real service expects and forwarded to the HIL
 * {@link CanEmulatorCore}; pushes from the core are delivered to the callback
 * binder the app registered via TX28.
 */
public final class FakeCanBusBinder extends Binder {
    static final String DESCRIPTOR = "com.qinggan.canbus.ICanBusService";

    private final CanEmulatorCore core;
    private volatile IBinder callback;

    public FakeCanBusBinder(CanEmulatorCore core) {
        this.core = core;
        attachInterface(null, DESCRIPTOR);
    }

    public IBinder callbackBinder() {
        return this.callback;
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        try {
            data.enforceInterface(DESCRIPTOR);
        } catch (RuntimeException e) {
            return false;
        }
        List<Object> args = readArgs(code, data);
        System.out.println("FAKE onTransact code=" + code + " args=" + args);
        if (code == TxCode.ADD_CALLBACK || code == TxCode.REMOVE_CALLBACK) {
            this.callback = (IBinder) args.get(0);
        }
        TxResult result = this.core.transact(code, args);
        System.out.println("FAKE core.transact send=" + result.send + " reply=" + result.replyArgs);
        if (!result.send) {
            return false;
        }
        if (reply != null) {
            reply.writeNoException();
            for (Object arg : result.replyArgs) {
                writeArg(reply, arg);
            }
        }
        return true;
    }

    private static List<Object> readArgs(int code, Parcel data) {
        List<Object> args = new ArrayList<>();
        if (code == TxCode.ADD_CALLBACK || code == TxCode.REMOVE_CALLBACK) {
            args.add(data.readStrongBinder());
            return args;
        }
        if (code == TxCode.SET_BUNDLE) {
            args.add(data.readInt());
            args.add(data.readInt());
            Bundle bundle = data.readBundle(FakeCanBusBinder.class.getClassLoader());
            Map<String, Integer> values = new LinkedHashMap<>();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    values.put(key, bundle.getInt(key));
                }
            }
            args.add(values);
            return args;
        }
        while (data.dataAvail() >= 4) {
            args.add(data.readInt());
        }
        return args;
    }

    static void writeArg(Parcel target, Object arg) {
        if (arg instanceof Integer) {
            target.writeInt((Integer) arg);
        } else if (arg instanceof Long) {
            target.writeInt(((Long) arg).intValue());
        } else if (arg instanceof String) {
            target.writeString((String) arg);
        } else if (arg instanceof IBinder) {
            target.writeStrongBinder((IBinder) arg);
        } else if (arg == null) {
            target.writeInt(0);
        } else {
            throw new IllegalStateException("unsupported reply arg: " + arg.getClass());
        }
    }
}
