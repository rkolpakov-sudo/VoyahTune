package ru.big.town.restoremode;

import android.content.SharedPreferences;
import android.os.Messenger;

/* JADX INFO: loaded from: classes2.dex */
public class GlobalVars {
    static Messenger clientMessenger = null;
    private static int connectedClients = 0;
    static SharedPreferences.Editor editor = null;
    static boolean isBound = false;
    static Messenger serviceMessenger;
    static SharedPreferences sharedPreferences;

    static synchronized void clientConnected(Messenger messenger) {
        connectedClients++;
        serviceMessenger = messenger;
        isBound = true;
    }

    static synchronized void clientDisconnected() {
        int i = connectedClients;
        if (i > 0) {
            connectedClients = i - 1;
        }
        if (connectedClients == 0) {
            serviceMessenger = null;
            isBound = false;
        }
    }
}
