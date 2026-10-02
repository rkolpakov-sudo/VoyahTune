package ru.big.town.updater;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
final class RootClient {
    private static final int MAX_RESPONSE = 8388608;

    RootClient() {
    }

    JSONObject call(JSONObject jSONObject) throws Exception {
        int i;
        LocalSocket localSocket = new LocalSocket();
        try {
            localSocket.connect(new LocalSocketAddress("voyahtune_updater", LocalSocketAddress.Namespace.RESERVED));
            String strOptString = jSONObject.optString("command");
            localSocket.setSoTimeout(("get_settings".equals(strOptString) || "set_settings".equals(strOptString)) ? 45000 : 5000);
            if (localSocket.getPeerCredentials().getUid() != 0) {
                throw new IOException("Ответ получен не от root-службы");
            }
            byte[] bytes = (jSONObject.toString() + "\n").getBytes(StandardCharsets.UTF_8);
            if (bytes.length > 8192) {
                throw new IOException("Запрос слишком велик");
            }
            localSocket.getOutputStream().write(bytes);
            localSocket.getOutputStream().flush();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(localSocket.getInputStream());
            while (byteArrayOutputStream.size() < MAX_RESPONSE && (i = bufferedInputStream.read()) != -1) {
                if (i != 10) {
                    byteArrayOutputStream.write(i);
                } else {
                    JSONObject jSONObject2 = new JSONObject(byteArrayOutputStream.toString(StandardCharsets.UTF_8.name()));
                    if (jSONObject2.optInt("schema", -1) != 1) {
                        throw new IOException("Несовместимая версия службы");
                    }
                    if (!jSONObject2.optBoolean("ok")) {
                        String strOptString2 = jSONObject2.optString("error", "Ошибка службы");
                        if ("Неизвестная команда или формат запроса".equals(strOptString2)) {
                            throw new UnsupportedOperationException(strOptString2);
                        }
                        throw new IOException(strOptString2);
                    }
                    localSocket.close();
                    return jSONObject2;
                }
            }
            throw new IOException("Служба вернула неполный или слишком большой ответ");
        } catch (Throwable th) {
            try {
                localSocket.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }
}
