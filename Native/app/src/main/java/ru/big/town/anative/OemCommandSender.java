package ru.big.town.anative;

import java.util.concurrent.TimeoutException;

/* JADX INFO: loaded from: classes2.dex */
final class OemCommandSender {

    interface Transport {
        void prepare(String str, long j) throws Exception;

        int send(String str, int i) throws Exception;
    }

    OemCommandSender() {
    }

    static String send(String str, long j, Transport transport) {
        SeatCommand seatCommand = SeatCommand.parse(str);
        WindowCommand windowCommand = WindowCommand.parse(str);
        if (seatCommand == null && windowCommand == null) {
            return "Неизвестная команда автомобиля";
        }
        String str2 = seatCommand != null ? seatCommand.field : windowCommand.field;
        int i = seatCommand != null ? seatCommand.value : windowCommand.value;
        try {
            transport.prepare(str2, j);
            if (transport.send(str2, i) == 0) {
                return null;
            }
            return "Не удалось отправить команду автомобилю";
        } catch (InterruptedException unused) {
            Thread.currentThread().interrupt();
            return "Не удалось отправить команду автомобилю";
        } catch (SecurityException unused2) {
            return "Нет разрешения на управление автомобилем";
        } catch (UnsupportedOperationException unused3) {
            return "Эта функция недоступна в OEM API автомобиля";
        } catch (TimeoutException unused4) {
            return "Нет подключения к сервису автомобиля";
        } catch (Exception | LinkageError unused5) {
            return "Не удалось отправить команду автомобилю";
        }
    }
}
