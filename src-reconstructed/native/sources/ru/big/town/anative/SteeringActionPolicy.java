package ru.big.town.anative;

import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
final class SteeringActionPolicy {
    private SteeringActionPolicy() {
    }

    static String nextMode(String str, String str2) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        String[] strArrSplit = str.split(",");
        ArrayList arrayList = new ArrayList();
        for (String str3 : strArrSplit) {
            String strTrim = str3.trim();
            if (!strTrim.isEmpty()) {
                arrayList.add(strTrim);
            }
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        int iIndexOf = arrayList.indexOf(str2);
        return (String) arrayList.get(iIndexOf >= 0 ? (iIndexOf + 1) % arrayList.size() : 0);
    }
}
