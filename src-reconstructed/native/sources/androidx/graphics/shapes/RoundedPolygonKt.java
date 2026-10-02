package androidx.graphics.shapes;

import androidx.collection.FloatFloatPair;
import androidx.collection.MutableFloatList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;

/* JADX INFO: compiled from: RoundedPolygon.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u00008\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0014\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u000e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0001\u001a@\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u00062\u0010\b\u0002\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\nH\u0007\u001aL\u0010\u0000\u001a\u00020\u00012\b\b\u0001\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\n2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\u0005\u001a\u00020\u00062\u0010\b\u0002\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\bH\u0007\u001a\u0019\u0010\u000f\u001a\u00060\u0010j\u0002`\u00112\u0006\u0010\u0003\u001a\u00020\u0004H\u0002¢\u0006\u0002\u0010\u0012\u001a(\u0010\u0013\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\n2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\nH\u0002¨\u0006\u0014"}, d2 = {"RoundedPolygon", "Landroidx/graphics/shapes/RoundedPolygon;", "source", "vertices", "", "rounding", "Landroidx/graphics/shapes/CornerRounding;", "perVertexRounding", "", "centerX", "", "centerY", "numVertices", "", "radius", "calculateCenter", "Landroidx/collection/FloatFloatPair;", "Landroidx/graphics/shapes/Point;", "([F)J", "verticesFromNumVerts", "graphics-shapes_release"}, k = 2, mv = {1, 8, 0}, xi = 48)
public final class RoundedPolygonKt {
    public static final RoundedPolygon RoundedPolygon(int i) {
        return RoundedPolygon$default(i, 0.0f, 0.0f, 0.0f, null, null, 62, null);
    }

    public static final RoundedPolygon RoundedPolygon(int i, float f) {
        return RoundedPolygon$default(i, f, 0.0f, 0.0f, null, null, 60, null);
    }

    public static final RoundedPolygon RoundedPolygon(int i, float f, float f2) {
        return RoundedPolygon$default(i, f, f2, 0.0f, null, null, 56, null);
    }

    public static final RoundedPolygon RoundedPolygon(int i, float f, float f2, float f3) {
        return RoundedPolygon$default(i, f, f2, f3, null, null, 48, null);
    }

    public static final RoundedPolygon RoundedPolygon(int i, float f, float f2, float f3, CornerRounding rounding) {
        Intrinsics.checkNotNullParameter(rounding, "rounding");
        return RoundedPolygon$default(i, f, f2, f3, rounding, null, 32, null);
    }

    public static final RoundedPolygon RoundedPolygon(float[] vertices) {
        Intrinsics.checkNotNullParameter(vertices, "vertices");
        return RoundedPolygon$default(vertices, null, null, 0.0f, 0.0f, 30, null);
    }

    public static final RoundedPolygon RoundedPolygon(float[] vertices, CornerRounding rounding) {
        Intrinsics.checkNotNullParameter(vertices, "vertices");
        Intrinsics.checkNotNullParameter(rounding, "rounding");
        return RoundedPolygon$default(vertices, rounding, null, 0.0f, 0.0f, 28, null);
    }

    public static final RoundedPolygon RoundedPolygon(float[] vertices, CornerRounding rounding, List<CornerRounding> list) {
        Intrinsics.checkNotNullParameter(vertices, "vertices");
        Intrinsics.checkNotNullParameter(rounding, "rounding");
        return RoundedPolygon$default(vertices, rounding, list, 0.0f, 0.0f, 24, null);
    }

    public static final RoundedPolygon RoundedPolygon(float[] vertices, CornerRounding rounding, List<CornerRounding> list, float f) {
        Intrinsics.checkNotNullParameter(vertices, "vertices");
        Intrinsics.checkNotNullParameter(rounding, "rounding");
        return RoundedPolygon$default(vertices, rounding, list, f, 0.0f, 16, null);
    }

    public static /* synthetic */ RoundedPolygon RoundedPolygon$default(int i, float f, float f2, float f3, CornerRounding cornerRounding, List list, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            f = 1.0f;
        }
        if ((i2 & 4) != 0) {
            f2 = 0.0f;
        }
        if ((i2 & 8) != 0) {
            f3 = 0.0f;
        }
        if ((i2 & 16) != 0) {
            cornerRounding = CornerRounding.Unrounded;
        }
        if ((i2 & 32) != 0) {
            list = null;
        }
        List list2 = list;
        return RoundedPolygon(i, f, f2, f3, cornerRounding, list2);
    }

    public static final RoundedPolygon RoundedPolygon(int i, float f, float f2, float f3, CornerRounding rounding, List<CornerRounding> list) {
        Intrinsics.checkNotNullParameter(rounding, "rounding");
        return RoundedPolygon(verticesFromNumVerts(i, f, f2, f3), rounding, list, f2, f3);
    }

    public static final RoundedPolygon RoundedPolygon(RoundedPolygon source) {
        Intrinsics.checkNotNullParameter(source, "source");
        return new RoundedPolygon(source.getFeatures$graphics_shapes_release(), source.getCenterX(), source.getCenterY());
    }

    public static /* synthetic */ RoundedPolygon RoundedPolygon$default(float[] fArr, CornerRounding cornerRounding, List list, float f, float f2, int i, Object obj) {
        if ((i & 2) != 0) {
            cornerRounding = CornerRounding.Unrounded;
        }
        if ((i & 4) != 0) {
            list = null;
        }
        if ((i & 8) != 0) {
            f = Float.MIN_VALUE;
        }
        if ((i & 16) != 0) {
            f2 = Float.MIN_VALUE;
        }
        return RoundedPolygon(fArr, cornerRounding, (List<CornerRounding>) list, f, f2);
    }

    public static final RoundedPolygon RoundedPolygon(float[] vertices, CornerRounding rounding, List<CornerRounding> list, float f, float f2) {
        long jCalculateCenter;
        Pair pair;
        CornerRounding cornerRounding;
        Intrinsics.checkNotNullParameter(vertices, "vertices");
        Intrinsics.checkNotNullParameter(rounding, "rounding");
        if (vertices.length < 6) {
            throw new IllegalArgumentException("Polygons must have at least 3 vertices");
        }
        int i = 2;
        int i2 = 1;
        if (vertices.length % 2 == 1) {
            throw new IllegalArgumentException("The vertices array should have even size");
        }
        if (list != null && list.size() * 2 != vertices.length) {
            throw new IllegalArgumentException("perVertexRounding list should be either null or the same size as the number of vertices (vertices.size / 2)");
        }
        ArrayList arrayList = new ArrayList();
        int length = vertices.length / 2;
        ArrayList arrayList2 = new ArrayList();
        int i3 = 0;
        int i4 = 0;
        while (i4 < length) {
            CornerRounding cornerRounding2 = (list == null || (cornerRounding = list.get(i4)) == null) ? rounding : cornerRounding;
            int i5 = (((i4 + length) - 1) % length) * 2;
            int i6 = i4 + 1;
            int i7 = (i6 % length) * 2;
            int i8 = i4 * 2;
            arrayList2.add(new RoundedCorner(FloatFloatPair.m10constructorimpl(vertices[i5], vertices[i5 + 1]), FloatFloatPair.m10constructorimpl(vertices[i8], vertices[i8 + 1]), FloatFloatPair.m10constructorimpl(vertices[i7], vertices[i7 + 1]), cornerRounding2, null));
            i4 = i6;
        }
        IntRange intRangeUntil = RangesKt.until(0, length);
        ArrayList arrayList3 = new ArrayList(CollectionsKt.collectionSizeOrDefault(intRangeUntil, 10));
        Iterator<Integer> it = intRangeUntil.iterator();
        while (it.hasNext()) {
            int iNextInt = ((IntIterator) it).nextInt();
            int i9 = (iNextInt + 1) % length;
            float expectedRoundCut = ((RoundedCorner) arrayList2.get(iNextInt)).getExpectedRoundCut() + ((RoundedCorner) arrayList2.get(i9)).getExpectedRoundCut();
            float expectedCut = ((RoundedCorner) arrayList2.get(iNextInt)).getExpectedCut() + ((RoundedCorner) arrayList2.get(i9)).getExpectedCut();
            int i10 = iNextInt * 2;
            int i11 = i9 * 2;
            float fDistance = Utils.distance(vertices[i10] - vertices[i11], vertices[i10 + 1] - vertices[i11 + 1]);
            if (expectedRoundCut > fDistance) {
                pair = TuplesKt.to(Float.valueOf(fDistance / expectedRoundCut), Float.valueOf(0.0f));
            } else if (expectedCut > fDistance) {
                pair = TuplesKt.to(Float.valueOf(1.0f), Float.valueOf((fDistance - expectedRoundCut) / (expectedCut - expectedRoundCut)));
            } else {
                pair = TuplesKt.to(Float.valueOf(1.0f), Float.valueOf(1.0f));
            }
            arrayList3.add(pair);
        }
        ArrayList arrayList4 = arrayList3;
        for (int i12 = 0; i12 < length; i12++) {
            MutableFloatList mutableFloatList = new MutableFloatList(2);
            for (int i13 = 0; i13 < 2; i13++) {
                Pair pair2 = (Pair) arrayList4.get((((i12 + length) - 1) + i13) % length);
                mutableFloatList.add((((RoundedCorner) arrayList2.get(i12)).getExpectedRoundCut() * ((Number) pair2.component1()).floatValue()) + ((((RoundedCorner) arrayList2.get(i12)).getExpectedCut() - ((RoundedCorner) arrayList2.get(i12)).getExpectedRoundCut()) * ((Number) pair2.component2()).floatValue()));
            }
            arrayList.add(((RoundedCorner) arrayList2.get(i12)).getCubics(mutableFloatList.get(0), mutableFloatList.get(1)));
        }
        ArrayList arrayList5 = new ArrayList();
        while (i3 < length) {
            int i14 = i3 + 1;
            int i15 = i14 % length;
            int i16 = i3 * 2;
            long jM10constructorimpl = FloatFloatPair.m10constructorimpl(vertices[i16], vertices[i16 + i2]);
            int i17 = (((i3 + length) - i2) % length) * i;
            long jM10constructorimpl2 = FloatFloatPair.m10constructorimpl(vertices[i17], vertices[i17 + i2]);
            int i18 = i15 * 2;
            arrayList5.add(new Feature.Corner((List) arrayList.get(i3), jM10constructorimpl, ((RoundedCorner) arrayList2.get(i3)).getCenter(), PointKt.m89clockwiseybeJwSQ(PointKt.m101minusybeJwSQ(jM10constructorimpl, jM10constructorimpl2), PointKt.m101minusybeJwSQ(FloatFloatPair.m10constructorimpl(vertices[i18], vertices[i18 + i2]), jM10constructorimpl)), null));
            arrayList5.add(new Feature.Edge(CollectionsKt.listOf(Cubic.INSTANCE.straightLine(((Cubic) CollectionsKt.last((List) arrayList.get(i3))).getAnchor1X(), ((Cubic) CollectionsKt.last((List) arrayList.get(i3))).getAnchor1Y(), ((Cubic) CollectionsKt.first((List) arrayList.get(i15))).getAnchor0X(), ((Cubic) CollectionsKt.first((List) arrayList.get(i15))).getAnchor0Y()))));
            i3 = i14;
            i = 2;
            i2 = 1;
        }
        if (f == Float.MIN_VALUE || f2 == Float.MIN_VALUE) {
            jCalculateCenter = calculateCenter(vertices);
        } else {
            jCalculateCenter = FloatFloatPair.m10constructorimpl(f, f2);
        }
        return new RoundedPolygon(arrayList5, Float.intBitsToFloat((int) (jCalculateCenter >> 32)), Float.intBitsToFloat((int) (jCalculateCenter & 4294967295L)));
    }

    private static final long calculateCenter(float[] fArr) {
        float f = 0.0f;
        int i = 0;
        float f2 = 0.0f;
        while (i < fArr.length) {
            int i2 = i + 1;
            f += fArr[i];
            i += 2;
            f2 += fArr[i2];
        }
        float f3 = 2;
        return FloatFloatPair.m10constructorimpl((f / fArr.length) / f3, (f2 / fArr.length) / f3);
    }

    private static final float[] verticesFromNumVerts(int i, float f, float f2, float f3) {
        float[] fArr = new float[i * 2];
        int i2 = 0;
        int i3 = 0;
        while (i2 < i) {
            float f4 = f;
            long jM102plusybeJwSQ = PointKt.m102plusybeJwSQ(Utils.m118radialToCartesianL6JJ3z0$default(f4, (Utils.getFloatPi() / i) * 2 * i2, 0L, 4, null), FloatFloatPair.m10constructorimpl(f2, f3));
            int i4 = i3 + 1;
            fArr[i3] = PointKt.m98getXDnnuFBc(jM102plusybeJwSQ);
            i3 += 2;
            fArr[i4] = PointKt.m99getYDnnuFBc(jM102plusybeJwSQ);
            i2++;
            f = f4;
        }
        return fArr;
    }
}
