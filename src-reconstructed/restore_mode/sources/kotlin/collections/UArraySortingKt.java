package kotlin.collections;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.UByte;
import kotlin.UByteArray;
import kotlin.UIntArray;
import kotlin.ULongArray;
import kotlin.UShort;
import kotlin.UShortArray;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: UArraySorting.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u00000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\f\u001a'\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003¢\u0006\u0004\b\u0006\u0010\u0007\u001a'\u0010\b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003¢\u0006\u0004\b\n\u0010\u000b\u001a'\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\f2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003¢\u0006\u0004\b\r\u0010\u000e\u001a'\u0010\b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\f2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003¢\u0006\u0004\b\u000f\u0010\u0010\u001a'\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00112\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003¢\u0006\u0004\b\u0012\u0010\u0013\u001a'\u0010\b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u00112\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003¢\u0006\u0004\b\u0014\u0010\u0015\u001a'\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00162\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003¢\u0006\u0004\b\u0017\u0010\u0018\u001a'\u0010\b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u00162\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003¢\u0006\u0004\b\u0019\u0010\u001a\u001a'\u0010\u001b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001¢\u0006\u0004\b\u001e\u0010\u000b\u001a'\u0010\u001b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\f2\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001¢\u0006\u0004\b\u001f\u0010\u0010\u001a'\u0010\u001b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u00112\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001¢\u0006\u0004\b \u0010\u0015\u001a'\u0010\u001b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u00162\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001¢\u0006\u0004\b!\u0010\u001a¨\u0006\""}, d2 = {"partition", "", "array", "Lkotlin/UByteArray;", "left", "right", "partition-4UcCI2c", "([BII)I", "quickSort", "", "quickSort-4UcCI2c", "([BII)V", "Lkotlin/UShortArray;", "partition-Aa5vz7o", "([SII)I", "quickSort-Aa5vz7o", "([SII)V", "Lkotlin/UIntArray;", "partition-oBK06Vg", "([III)I", "quickSort-oBK06Vg", "([III)V", "Lkotlin/ULongArray;", "partition--nroSd4", "([JII)I", "quickSort--nroSd4", "([JII)V", "sortArray", "fromIndex", "toIndex", "sortArray-4UcCI2c", "sortArray-Aa5vz7o", "sortArray-oBK06Vg", "sortArray--nroSd4", "kotlin-stdlib"}, k = 2, mv = {2, 2, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
public final class UArraySortingKt {
    /* JADX INFO: renamed from: partition-4UcCI2c, reason: not valid java name */
    private static final int m627partition4UcCI2c(byte[] bArr, int i, int i2) {
        int i3;
        byte bM243getw2LRezQ = UByteArray.m243getw2LRezQ(bArr, (i + i2) / 2);
        while (i <= i2) {
            while (true) {
                int iM243getw2LRezQ = UByteArray.m243getw2LRezQ(bArr, i) & UByte.MAX_VALUE;
                i3 = bM243getw2LRezQ & UByte.MAX_VALUE;
                if (Intrinsics.compare(iM243getw2LRezQ, i3) >= 0) {
                    break;
                }
                i++;
            }
            while (Intrinsics.compare(UByteArray.m243getw2LRezQ(bArr, i2) & UByte.MAX_VALUE, i3) > 0) {
                i2--;
            }
            if (i <= i2) {
                byte bM243getw2LRezQ2 = UByteArray.m243getw2LRezQ(bArr, i);
                UByteArray.m248setVurrAj0(bArr, i, UByteArray.m243getw2LRezQ(bArr, i2));
                UByteArray.m248setVurrAj0(bArr, i2, bM243getw2LRezQ2);
                i++;
                i2--;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: quickSort-4UcCI2c, reason: not valid java name */
    private static final void m631quickSort4UcCI2c(byte[] bArr, int i, int i2) {
        int iM627partition4UcCI2c = m627partition4UcCI2c(bArr, i, i2);
        int i3 = iM627partition4UcCI2c - 1;
        if (i < i3) {
            m631quickSort4UcCI2c(bArr, i, i3);
        }
        if (iM627partition4UcCI2c < i2) {
            m631quickSort4UcCI2c(bArr, iM627partition4UcCI2c, i2);
        }
    }

    /* JADX INFO: renamed from: partition-Aa5vz7o, reason: not valid java name */
    private static final int m628partitionAa5vz7o(short[] sArr, int i, int i2) {
        int i3;
        short sM506getMh2AYeg = UShortArray.m506getMh2AYeg(sArr, (i + i2) / 2);
        while (i <= i2) {
            while (true) {
                int iM506getMh2AYeg = UShortArray.m506getMh2AYeg(sArr, i) & UShort.MAX_VALUE;
                i3 = sM506getMh2AYeg & UShort.MAX_VALUE;
                if (Intrinsics.compare(iM506getMh2AYeg, i3) >= 0) {
                    break;
                }
                i++;
            }
            while (Intrinsics.compare(UShortArray.m506getMh2AYeg(sArr, i2) & UShort.MAX_VALUE, i3) > 0) {
                i2--;
            }
            if (i <= i2) {
                short sM506getMh2AYeg2 = UShortArray.m506getMh2AYeg(sArr, i);
                UShortArray.m511set01HTLdE(sArr, i, UShortArray.m506getMh2AYeg(sArr, i2));
                UShortArray.m511set01HTLdE(sArr, i2, sM506getMh2AYeg2);
                i++;
                i2--;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: quickSort-Aa5vz7o, reason: not valid java name */
    private static final void m632quickSortAa5vz7o(short[] sArr, int i, int i2) {
        int iM628partitionAa5vz7o = m628partitionAa5vz7o(sArr, i, i2);
        int i3 = iM628partitionAa5vz7o - 1;
        if (i < i3) {
            m632quickSortAa5vz7o(sArr, i, i3);
        }
        if (iM628partitionAa5vz7o < i2) {
            m632quickSortAa5vz7o(sArr, iM628partitionAa5vz7o, i2);
        }
    }

    /* JADX INFO: renamed from: partition-oBK06Vg, reason: not valid java name */
    private static final int m629partitionoBK06Vg(int[] iArr, int i, int i2) {
        int iM322getpVg5ArA = UIntArray.m322getpVg5ArA(iArr, (i + i2) / 2);
        while (i <= i2) {
            while (Integer.compareUnsigned(UIntArray.m322getpVg5ArA(iArr, i), iM322getpVg5ArA) < 0) {
                i++;
            }
            while (Integer.compareUnsigned(UIntArray.m322getpVg5ArA(iArr, i2), iM322getpVg5ArA) > 0) {
                i2--;
            }
            if (i <= i2) {
                int iM322getpVg5ArA2 = UIntArray.m322getpVg5ArA(iArr, i);
                UIntArray.m327setVXSXFK8(iArr, i, UIntArray.m322getpVg5ArA(iArr, i2));
                UIntArray.m327setVXSXFK8(iArr, i2, iM322getpVg5ArA2);
                i++;
                i2--;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: quickSort-oBK06Vg, reason: not valid java name */
    private static final void m633quickSortoBK06Vg(int[] iArr, int i, int i2) {
        int iM629partitionoBK06Vg = m629partitionoBK06Vg(iArr, i, i2);
        int i3 = iM629partitionoBK06Vg - 1;
        if (i < i3) {
            m633quickSortoBK06Vg(iArr, i, i3);
        }
        if (iM629partitionoBK06Vg < i2) {
            m633quickSortoBK06Vg(iArr, iM629partitionoBK06Vg, i2);
        }
    }

    /* JADX INFO: renamed from: partition--nroSd4, reason: not valid java name */
    private static final int m626partitionnroSd4(long[] jArr, int i, int i2) {
        long jM401getsVKNKU = ULongArray.m401getsVKNKU(jArr, (i + i2) / 2);
        while (i <= i2) {
            while (Long.compareUnsigned(ULongArray.m401getsVKNKU(jArr, i), jM401getsVKNKU) < 0) {
                i++;
            }
            while (Long.compareUnsigned(ULongArray.m401getsVKNKU(jArr, i2), jM401getsVKNKU) > 0) {
                i2--;
            }
            if (i <= i2) {
                long jM401getsVKNKU2 = ULongArray.m401getsVKNKU(jArr, i);
                ULongArray.m406setk8EXiF4(jArr, i, ULongArray.m401getsVKNKU(jArr, i2));
                ULongArray.m406setk8EXiF4(jArr, i2, jM401getsVKNKU2);
                i++;
                i2--;
            }
        }
        return i;
    }

    /* JADX INFO: renamed from: quickSort--nroSd4, reason: not valid java name */
    private static final void m630quickSortnroSd4(long[] jArr, int i, int i2) {
        int iM626partitionnroSd4 = m626partitionnroSd4(jArr, i, i2);
        int i3 = iM626partitionnroSd4 - 1;
        if (i < i3) {
            m630quickSortnroSd4(jArr, i, i3);
        }
        if (iM626partitionnroSd4 < i2) {
            m630quickSortnroSd4(jArr, iM626partitionnroSd4, i2);
        }
    }

    /* JADX INFO: renamed from: sortArray-4UcCI2c, reason: not valid java name */
    public static final void m635sortArray4UcCI2c(byte[] array, int i, int i2) {
        Intrinsics.checkNotNullParameter(array, "array");
        m631quickSort4UcCI2c(array, i, i2 - 1);
    }

    /* JADX INFO: renamed from: sortArray-Aa5vz7o, reason: not valid java name */
    public static final void m636sortArrayAa5vz7o(short[] array, int i, int i2) {
        Intrinsics.checkNotNullParameter(array, "array");
        m632quickSortAa5vz7o(array, i, i2 - 1);
    }

    /* JADX INFO: renamed from: sortArray-oBK06Vg, reason: not valid java name */
    public static final void m637sortArrayoBK06Vg(int[] array, int i, int i2) {
        Intrinsics.checkNotNullParameter(array, "array");
        m633quickSortoBK06Vg(array, i, i2 - 1);
    }

    /* JADX INFO: renamed from: sortArray--nroSd4, reason: not valid java name */
    public static final void m634sortArraynroSd4(long[] array, int i, int i2) {
        Intrinsics.checkNotNullParameter(array, "array");
        m630quickSortnroSd4(array, i, i2 - 1);
    }
}
