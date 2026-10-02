package ru.big.town.restoremode;

import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceResultPresentation {
    VoiceResultPresentation() {
    }

    static String successText(String str, String str2, int i) {
        if (!"port_cap:fuel".equals(str)) {
            return str2;
        }
        if (i >= 0 && i <= 200) {
            return "Можно заправить ~" + i + "л бензина";
        }
        return "Не удалось получить данные о топливе";
    }

    static int successDurationMs(String str) {
        if ("port_cap:fuel".equals(str)) {
            return 10000;
        }
        return PathInterpolatorCompat.MAX_NUM_POINTS;
    }
}
