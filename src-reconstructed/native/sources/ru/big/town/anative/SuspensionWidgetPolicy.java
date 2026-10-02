package ru.big.town.anative;

/* JADX INFO: loaded from: classes2.dex */
final class SuspensionWidgetPolicy {
    static boolean mediumMode(int i) {
        return i == 1 || i == 2 || i == 5 || i == 6;
    }

    static boolean validHeight(int i) {
        return i >= 1 && i <= 9;
    }

    SuspensionWidgetPolicy() {
    }

    static String driveMode(int i, int i2) {
        if (i == 1) {
            return "SPORT";
        }
        if (i == 3) {
            return "OUTING";
        }
        if (i != 2) {
            return null;
        }
        if (i2 == 2) {
            return "COMFORT";
        }
        if (i2 == 5) {
            return "INDIVIDUAL";
        }
        if (i2 == 6) {
            return "SNOW";
        }
        return "ECO";
    }

    static String lowestBlockedReason(int i, int i2) {
        if (i == 4) {
            return "Удобная посадка недоступна в Outing";
        }
        if (i < 1 || i > 6) {
            return "Нет данных режима движения";
        }
        if (i2 != 0) {
            return "Удобная посадка сейчас недоступна";
        }
        return null;
    }

    static String blocked(int i, int i2, int i3, int i4, Integer num, int i5) {
        String strLowestBlockedReason;
        if (i < 0 || i > 3) {
            return "Неизвестный уровень";
        }
        if (!validHeight(i2) || i3 < 1 || i3 > 2) {
            return "Нет данных подвески";
        }
        if (i3 == 2) {
            return "Включён сервисный режим подвески";
        }
        if (i == 0 && (strLowestBlockedReason = lowestBlockedReason(i5, i4)) != null) {
            return strLowestBlockedReason;
        }
        if (i == 3 && (num == null || num.intValue() < 0)) {
            return "Нет данных скорости";
        }
        if (i != 3 || num.intValue() < 40) {
            return null;
        }
        return "Outing доступен при скорости ниже 40 км/ч";
    }

    static boolean reached(int i, int i2) {
        return i2 == new int[]{9, 7, 5, 1}[i];
    }
}
