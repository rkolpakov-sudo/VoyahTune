package ru.big.town.restoremode;

import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
final class TileGridPacking {
    private TileGridPacking() {
    }

    static int[] place(List<boolean[]> list, int i, int i2, int i3) {
        int i4;
        int i5;
        if (i2 < 1 || i3 < 1 || i3 > i) {
            throw new IllegalArgumentException("Invalid tile span");
        }
        int i6 = 0;
        loop0: while (true) {
            i4 = i6 + i2;
            if (list.size() < i4) {
                list.add(new boolean[i]);
            } else {
                i5 = 0;
                while (i5 <= i - i3) {
                    boolean z = true;
                    for (int i7 = i6; i7 < i4 && z; i7++) {
                        for (int i8 = i5; i8 < i5 + i3; i8++) {
                            if (list.get(i7)[i8]) {
                                z = false;
                                break;
                            }
                        }
                    }
                    if (z) {
                        break loop0;
                    }
                    i5++;
                }
                i6++;
            }
        }
        for (int i9 = i6; i9 < i4; i9++) {
            for (int i10 = i5; i10 < i5 + i3; i10++) {
                list.get(i9)[i10] = true;
            }
        }
        return new int[]{i6, i5};
    }
}
