package androidx.graphics.shapes;

import androidx.collection.FloatFloatPair;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import ru.big.town.common.SuspensionWidgetProtocol;

/* JADX INFO: compiled from: Shapes.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u00002\n\u0000\n\u0002\u0010\u0014\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0003\u001aH\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u0005H\u0002\u001a0\u0010\f\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\r\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u0005H\u0002\u001a4\u0010\u000e\u001a\u00020\u000f*\u00020\u00102\b\b\u0003\u0010\u0011\u001a\u00020\u00032\b\b\u0002\u0010\r\u001a\u00020\u00052\b\b\u0002\u0010\n\u001a\u00020\u00052\b\b\u0002\u0010\u000b\u001a\u00020\u0005H\u0007\u001a>\u0010\u0012\u001a\u00020\u000f*\u00020\u00102\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0013\u001a\u00020\u00052\b\b\u0002\u0010\n\u001a\u00020\u00052\b\b\u0002\u0010\u000b\u001a\u00020\u0005H\u0007\u001a\u0084\u0001\u0010\u0014\u001a\u00020\u000f*\u00020\u00102\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0003\u0010\u0015\u001a\u00020\u00052\b\b\u0002\u0010\u0016\u001a\u00020\u00172\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u00172\u0010\b\u0002\u0010\u0019\u001a\n\u0012\u0004\u0012\u00020\u0017\u0018\u00010\u001a2\b\b\u0003\u0010\b\u001a\u00020\u00052\b\b\u0003\u0010\t\u001a\u00020\u00052\b\b\u0002\u0010\n\u001a\u00020\u00052\b\b\u0002\u0010\u000b\u001a\u00020\u0005H\u0007\u001aN\u0010\u001b\u001a\u00020\u000f*\u00020\u00102\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0016\u001a\u00020\u00172\u0010\b\u0002\u0010\u0019\u001a\n\u0012\u0004\u0012\u00020\u0017\u0018\u00010\u001a2\b\b\u0002\u0010\n\u001a\u00020\u00052\b\b\u0002\u0010\u000b\u001a\u00020\u0005\u001ad\u0010\u001c\u001a\u00020\u000f*\u00020\u00102\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\r\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\u0016\u001a\u00020\u00172\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u00172\u0010\b\u0002\u0010\u0019\u001a\n\u0012\u0004\u0012\u00020\u0017\u0018\u00010\u001a2\b\b\u0002\u0010\n\u001a\u00020\u00052\b\b\u0002\u0010\u000b\u001a\u00020\u0005H\u0007¨\u0006\u001d"}, d2 = {"pillStarVerticesFromNumVerts", "", "numVerticesPerRadius", "", "width", "", SuspensionWidgetProtocol.HEIGHT, "innerRadius", "vertexSpacing", "startLocation", "centerX", "centerY", "starVerticesFromNumVerts", "radius", "circle", "Landroidx/graphics/shapes/RoundedPolygon;", "Landroidx/graphics/shapes/RoundedPolygon$Companion;", "numVertices", "pill", "smoothing", "pillStar", "innerRadiusRatio", "rounding", "Landroidx/graphics/shapes/CornerRounding;", "innerRounding", "perVertexRounding", "", "rectangle", "star", "graphics-shapes_release"}, k = 2, mv = {1, 8, 0}, xi = 48)
public final class ShapesKt {
    public static final RoundedPolygon circle(RoundedPolygon.Companion companion) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        return circle$default(companion, 0, 0.0f, 0.0f, 0.0f, 15, null);
    }

    public static final RoundedPolygon circle(RoundedPolygon.Companion companion, int i) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        return circle$default(companion, i, 0.0f, 0.0f, 0.0f, 14, null);
    }

    public static final RoundedPolygon circle(RoundedPolygon.Companion companion, int i, float f) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        return circle$default(companion, i, f, 0.0f, 0.0f, 12, null);
    }

    public static final RoundedPolygon circle(RoundedPolygon.Companion companion, int i, float f, float f2) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        return circle$default(companion, i, f, f2, 0.0f, 8, null);
    }

    public static final RoundedPolygon pill(RoundedPolygon.Companion companion) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        return pill$default(companion, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 31, null);
    }

    public static final RoundedPolygon pill(RoundedPolygon.Companion companion, float f) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        return pill$default(companion, f, 0.0f, 0.0f, 0.0f, 0.0f, 30, null);
    }

    public static final RoundedPolygon pill(RoundedPolygon.Companion companion, float f, float f2) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        return pill$default(companion, f, f2, 0.0f, 0.0f, 0.0f, 28, null);
    }

    public static final RoundedPolygon pill(RoundedPolygon.Companion companion, float f, float f2, float f3) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        return pill$default(companion, f, f2, f3, 0.0f, 0.0f, 24, null);
    }

    public static final RoundedPolygon pill(RoundedPolygon.Companion companion, float f, float f2, float f3, float f4) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        return pill$default(companion, f, f2, f3, f4, 0.0f, 16, null);
    }

    public static final RoundedPolygon pillStar(RoundedPolygon.Companion companion) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        return pillStar$default(companion, 0.0f, 0.0f, 0, 0.0f, null, null, null, 0.0f, 0.0f, 0.0f, 0.0f, 2047, null);
    }

    public static final RoundedPolygon pillStar(RoundedPolygon.Companion companion, float f) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        return pillStar$default(companion, f, 0.0f, 0, 0.0f, null, null, null, 0.0f, 0.0f, 0.0f, 0.0f, 2046, null);
    }

    public static final RoundedPolygon pillStar(RoundedPolygon.Companion companion, float f, float f2) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        return pillStar$default(companion, f, f2, 0, 0.0f, null, null, null, 0.0f, 0.0f, 0.0f, 0.0f, 2044, null);
    }

    public static final RoundedPolygon pillStar(RoundedPolygon.Companion companion, float f, float f2, int i) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        return pillStar$default(companion, f, f2, i, 0.0f, null, null, null, 0.0f, 0.0f, 0.0f, 0.0f, 2040, null);
    }

    public static final RoundedPolygon pillStar(RoundedPolygon.Companion companion, float f, float f2, int i, float f3) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        return pillStar$default(companion, f, f2, i, f3, null, null, null, 0.0f, 0.0f, 0.0f, 0.0f, 2032, null);
    }

    public static final RoundedPolygon pillStar(RoundedPolygon.Companion companion, float f, float f2, int i, float f3, CornerRounding rounding) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        Intrinsics.checkNotNullParameter(rounding, "rounding");
        return pillStar$default(companion, f, f2, i, f3, rounding, null, null, 0.0f, 0.0f, 0.0f, 0.0f, 2016, null);
    }

    public static final RoundedPolygon pillStar(RoundedPolygon.Companion companion, float f, float f2, int i, float f3, CornerRounding rounding, CornerRounding cornerRounding) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        Intrinsics.checkNotNullParameter(rounding, "rounding");
        return pillStar$default(companion, f, f2, i, f3, rounding, cornerRounding, null, 0.0f, 0.0f, 0.0f, 0.0f, 1984, null);
    }

    public static final RoundedPolygon pillStar(RoundedPolygon.Companion companion, float f, float f2, int i, float f3, CornerRounding rounding, CornerRounding cornerRounding, List<CornerRounding> list) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        Intrinsics.checkNotNullParameter(rounding, "rounding");
        return pillStar$default(companion, f, f2, i, f3, rounding, cornerRounding, list, 0.0f, 0.0f, 0.0f, 0.0f, 1920, null);
    }

    public static final RoundedPolygon pillStar(RoundedPolygon.Companion companion, float f, float f2, int i, float f3, CornerRounding rounding, CornerRounding cornerRounding, List<CornerRounding> list, float f4) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        Intrinsics.checkNotNullParameter(rounding, "rounding");
        return pillStar$default(companion, f, f2, i, f3, rounding, cornerRounding, list, f4, 0.0f, 0.0f, 0.0f, 1792, null);
    }

    public static final RoundedPolygon pillStar(RoundedPolygon.Companion companion, float f, float f2, int i, float f3, CornerRounding rounding, CornerRounding cornerRounding, List<CornerRounding> list, float f4, float f5) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        Intrinsics.checkNotNullParameter(rounding, "rounding");
        return pillStar$default(companion, f, f2, i, f3, rounding, cornerRounding, list, f4, f5, 0.0f, 0.0f, 1536, null);
    }

    public static final RoundedPolygon pillStar(RoundedPolygon.Companion companion, float f, float f2, int i, float f3, CornerRounding rounding, CornerRounding cornerRounding, List<CornerRounding> list, float f4, float f5, float f6) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        Intrinsics.checkNotNullParameter(rounding, "rounding");
        return pillStar$default(companion, f, f2, i, f3, rounding, cornerRounding, list, f4, f5, f6, 0.0f, 1024, null);
    }

    public static final RoundedPolygon star(RoundedPolygon.Companion companion, int i) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        return star$default(companion, i, 0.0f, 0.0f, null, null, null, 0.0f, 0.0f, 254, null);
    }

    public static final RoundedPolygon star(RoundedPolygon.Companion companion, int i, float f) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        return star$default(companion, i, f, 0.0f, null, null, null, 0.0f, 0.0f, 252, null);
    }

    public static final RoundedPolygon star(RoundedPolygon.Companion companion, int i, float f, float f2) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        return star$default(companion, i, f, f2, null, null, null, 0.0f, 0.0f, 248, null);
    }

    public static final RoundedPolygon star(RoundedPolygon.Companion companion, int i, float f, float f2, CornerRounding rounding) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        Intrinsics.checkNotNullParameter(rounding, "rounding");
        return star$default(companion, i, f, f2, rounding, null, null, 0.0f, 0.0f, 240, null);
    }

    public static final RoundedPolygon star(RoundedPolygon.Companion companion, int i, float f, float f2, CornerRounding rounding, CornerRounding cornerRounding) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        Intrinsics.checkNotNullParameter(rounding, "rounding");
        return star$default(companion, i, f, f2, rounding, cornerRounding, null, 0.0f, 0.0f, 224, null);
    }

    public static final RoundedPolygon star(RoundedPolygon.Companion companion, int i, float f, float f2, CornerRounding rounding, CornerRounding cornerRounding, List<CornerRounding> list) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        Intrinsics.checkNotNullParameter(rounding, "rounding");
        return star$default(companion, i, f, f2, rounding, cornerRounding, list, 0.0f, 0.0f, 192, null);
    }

    public static final RoundedPolygon star(RoundedPolygon.Companion companion, int i, float f, float f2, CornerRounding rounding, CornerRounding cornerRounding, List<CornerRounding> list, float f3) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        Intrinsics.checkNotNullParameter(rounding, "rounding");
        return star$default(companion, i, f, f2, rounding, cornerRounding, list, f3, 0.0f, 128, null);
    }

    public static /* synthetic */ RoundedPolygon circle$default(RoundedPolygon.Companion companion, int i, float f, float f2, float f3, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            i = 8;
        }
        if ((i2 & 2) != 0) {
            f = 1.0f;
        }
        if ((i2 & 4) != 0) {
            f2 = 0.0f;
        }
        if ((i2 & 8) != 0) {
            f3 = 0.0f;
        }
        return circle(companion, i, f, f2, f3);
    }

    public static final RoundedPolygon circle(RoundedPolygon.Companion companion, int i, float f, float f2, float f3) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        if (i < 3) {
            throw new IllegalArgumentException("Circle must have at least three vertices");
        }
        return RoundedPolygonKt.RoundedPolygon$default(i, f / ((float) Math.cos(Utils.getFloatPi() / i)), f2, f3, new CornerRounding(f, 0.0f, 2, null), null, 32, null);
    }

    public static /* synthetic */ RoundedPolygon rectangle$default(RoundedPolygon.Companion companion, float f, float f2, CornerRounding cornerRounding, List list, float f3, float f4, int i, Object obj) {
        if ((i & 1) != 0) {
            f = 2.0f;
        }
        if ((i & 2) != 0) {
            f2 = 2.0f;
        }
        if ((i & 4) != 0) {
            cornerRounding = CornerRounding.Unrounded;
        }
        if ((i & 8) != 0) {
            list = null;
        }
        if ((i & 16) != 0) {
            f3 = 0.0f;
        }
        if ((i & 32) != 0) {
            f4 = 0.0f;
        }
        return rectangle(companion, f, f2, cornerRounding, list, f3, f4);
    }

    public static final RoundedPolygon rectangle(RoundedPolygon.Companion companion, float f, float f2, CornerRounding rounding, List<CornerRounding> list, float f3, float f4) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        Intrinsics.checkNotNullParameter(rounding, "rounding");
        float f5 = 2;
        float f6 = f / f5;
        float f7 = f3 - f6;
        float f8 = f2 / f5;
        float f9 = f4 - f8;
        float f10 = f6 + f3;
        float f11 = f8 + f4;
        return RoundedPolygonKt.RoundedPolygon(new float[]{f10, f11, f7, f11, f7, f9, f10, f9}, rounding, list, f3, f4);
    }

    public static /* synthetic */ RoundedPolygon star$default(RoundedPolygon.Companion companion, int i, float f, float f2, CornerRounding cornerRounding, CornerRounding cornerRounding2, List list, float f3, float f4, int i2, Object obj) {
        return star(companion, i, (i2 & 2) != 0 ? 1.0f : f, (i2 & 4) != 0 ? 0.5f : f2, (i2 & 8) != 0 ? CornerRounding.Unrounded : cornerRounding, (i2 & 16) != 0 ? null : cornerRounding2, (i2 & 32) == 0 ? list : null, (i2 & 64) != 0 ? 0.0f : f3, (i2 & 128) != 0 ? 0.0f : f4);
    }

    public static final RoundedPolygon star(RoundedPolygon.Companion companion, int i, float f, float f2, CornerRounding rounding, CornerRounding cornerRounding, List<CornerRounding> list, float f3, float f4) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        Intrinsics.checkNotNullParameter(rounding, "rounding");
        if (f <= 0.0f || f2 <= 0.0f) {
            throw new IllegalArgumentException("Star radii must both be greater than 0");
        }
        if (f2 >= f) {
            throw new IllegalArgumentException("innerRadius must be less than radius");
        }
        if (list == null && cornerRounding != null) {
            IntRange intRangeUntil = RangesKt.until(0, i);
            ArrayList arrayList = new ArrayList();
            Iterator<Integer> it = intRangeUntil.iterator();
            while (it.hasNext()) {
                ((IntIterator) it).nextInt();
                CollectionsKt.addAll(arrayList, CollectionsKt.listOf((Object[]) new CornerRounding[]{rounding, cornerRounding}));
            }
            list = arrayList;
        }
        return RoundedPolygonKt.RoundedPolygon(starVerticesFromNumVerts(i, f, f2, f3, f4), rounding, list, f3, f4);
    }

    public static /* synthetic */ RoundedPolygon pill$default(RoundedPolygon.Companion companion, float f, float f2, float f3, float f4, float f5, int i, Object obj) {
        if ((i & 1) != 0) {
            f = 2.0f;
        }
        if ((i & 2) != 0) {
            f2 = 1.0f;
        }
        if ((i & 4) != 0) {
            f3 = 0.0f;
        }
        if ((i & 8) != 0) {
            f4 = 0.0f;
        }
        return pill(companion, f, f2, f3, f4, (i & 16) != 0 ? 0.0f : f5);
    }

    public static final RoundedPolygon pill(RoundedPolygon.Companion companion, float f, float f2, float f3, float f4, float f5) {
        Intrinsics.checkNotNullParameter(companion, "<this>");
        if (f <= 0.0f || f2 <= 0.0f) {
            throw new IllegalArgumentException("Pill shapes must have positive width and height");
        }
        float f6 = 2;
        float f7 = f / f6;
        float f8 = f2 / f6;
        float f9 = f7 + f4;
        float f10 = f8 + f5;
        float f11 = (-f7) + f4;
        float f12 = (-f8) + f5;
        return RoundedPolygonKt.RoundedPolygon$default(new float[]{f9, f10, f11, f10, f11, f12, f9, f12}, new CornerRounding(Math.min(f7, f8), f3), null, f4, f5, 4, null);
    }

    public static /* synthetic */ RoundedPolygon pillStar$default(RoundedPolygon.Companion companion, float f, float f2, int i, float f3, CornerRounding cornerRounding, CornerRounding cornerRounding2, List list, float f4, float f5, float f6, float f7, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            f = 2.0f;
        }
        if ((i2 & 2) != 0) {
            f2 = 1.0f;
        }
        if ((i2 & 4) != 0) {
            i = 8;
        }
        if ((i2 & 8) != 0) {
            f3 = 0.5f;
        }
        if ((i2 & 16) != 0) {
            cornerRounding = CornerRounding.Unrounded;
        }
        if ((i2 & 32) != 0) {
            cornerRounding2 = null;
        }
        if ((i2 & 64) != 0) {
            list = null;
        }
        if ((i2 & 128) != 0) {
            f4 = 0.5f;
        }
        if ((i2 & 256) != 0) {
            f5 = 0.0f;
        }
        if ((i2 & 512) != 0) {
            f6 = 0.0f;
        }
        return pillStar(companion, f, f2, i, f3, cornerRounding, cornerRounding2, list, f4, f5, f6, (i2 & 1024) != 0 ? 0.0f : f7);
    }

    public static final RoundedPolygon pillStar(RoundedPolygon.Companion companion, float f, float f2, int i, float f3, CornerRounding rounding, CornerRounding cornerRounding, List<CornerRounding> list, float f4, float f5, float f6, float f7) {
        ArrayList arrayList;
        Intrinsics.checkNotNullParameter(companion, "<this>");
        Intrinsics.checkNotNullParameter(rounding, "rounding");
        if (f <= 0.0f || f2 <= 0.0f) {
            throw new IllegalArgumentException("Pill shapes must have positive width and height");
        }
        if (f3 <= 0.0f || f3 > 1.0f) {
            throw new IllegalArgumentException("innerRadius must be between 0 and 1");
        }
        if (list != null || cornerRounding == null) {
            arrayList = list;
        } else {
            IntRange intRangeUntil = RangesKt.until(0, i);
            ArrayList arrayList2 = new ArrayList();
            Iterator<Integer> it = intRangeUntil.iterator();
            while (it.hasNext()) {
                ((IntIterator) it).nextInt();
                CollectionsKt.addAll(arrayList2, CollectionsKt.listOf((Object[]) new CornerRounding[]{rounding, cornerRounding}));
            }
            arrayList = arrayList2;
        }
        return RoundedPolygonKt.RoundedPolygon(pillStarVerticesFromNumVerts(i, f, f2, f3, f4, f5, f6, f7), rounding, arrayList, f6, f7);
    }

    private static final float[] pillStarVerticesFromNumVerts(int i, float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        float f8;
        float f9;
        long jM10constructorimpl;
        float fMin = Math.min(f, f2);
        float fCoerceAtLeast = RangesKt.coerceAtLeast(f2 - f, 0.0f);
        float fCoerceAtLeast2 = RangesKt.coerceAtLeast(f - f2, 0.0f);
        float f10 = 2;
        float f11 = fCoerceAtLeast / f10;
        float f12 = fCoerceAtLeast2 / f10;
        float twoPi = Utils.getTwoPi() * fMin * Utils.interpolate(f3, 1.0f, f4);
        float f13 = (f10 * fCoerceAtLeast2) + (f10 * fCoerceAtLeast) + twoPi;
        float f14 = twoPi / 4;
        float f15 = f11 + f14;
        float f16 = f15 + fCoerceAtLeast2;
        float f17 = f16 + f14;
        float f18 = f17 + fCoerceAtLeast;
        float f19 = f18 + f14;
        float f20 = f19 + fCoerceAtLeast2;
        float f21 = f20 + f14;
        float[] fArr = {0.0f, f11, f15, f16, f17, f18, f19, f20, f21, f21 + f11, f13};
        int i2 = i * 2;
        float f22 = f13 / i2;
        float f23 = f5 * f13;
        float[] fArr2 = new float[i * 4];
        long jM10constructorimpl2 = FloatFloatPair.m10constructorimpl(f12, f11);
        float f24 = -f12;
        long jM10constructorimpl3 = FloatFloatPair.m10constructorimpl(f24, f11);
        float f25 = -f11;
        long jM10constructorimpl4 = FloatFloatPair.m10constructorimpl(f24, f25);
        long jM10constructorimpl5 = FloatFloatPair.m10constructorimpl(f12, f25);
        float f26 = f11;
        int i3 = 0;
        float f27 = 0.0f;
        boolean z = false;
        int i4 = 0;
        int i5 = 0;
        while (i3 < i2) {
            float f28 = f23 % f13;
            if (f28 < f27) {
                i4 = 0;
            }
            while (true) {
                int i6 = (i4 + 1) % 11;
                float f29 = fArr[i6];
                if (f28 >= f29) {
                    f26 = fArr[(i6 + 1) % 11];
                    i4 = i6;
                    f27 = f29;
                } else {
                    float f30 = (f28 - f27) / (f26 - f27);
                    float f31 = z ? fMin * f3 : fMin;
                    switch (i4) {
                        case 0:
                            f8 = f11;
                            f9 = f12;
                            jM10constructorimpl = FloatFloatPair.m10constructorimpl(f31, f30 * f8);
                            break;
                        case 1:
                            f8 = f11;
                            f9 = f12;
                            jM10constructorimpl = PointKt.m102plusybeJwSQ(Utils.m118radialToCartesianL6JJ3z0$default(f31, (f30 * Utils.getFloatPi()) / f10, 0L, 4, null), jM10constructorimpl2);
                            break;
                        case 2:
                            f8 = f11;
                            f9 = f12;
                            jM10constructorimpl = FloatFloatPair.m10constructorimpl(f9 - (f30 * fCoerceAtLeast2), f31);
                            break;
                        case 3:
                            f8 = f11;
                            f9 = f12;
                            jM10constructorimpl = PointKt.m102plusybeJwSQ(Utils.m118radialToCartesianL6JJ3z0$default(f31, (Utils.getFloatPi() / f10) + ((f30 * Utils.getFloatPi()) / f10), 0L, 4, null), jM10constructorimpl3);
                            break;
                        case 4:
                            f8 = f11;
                            f9 = f12;
                            jM10constructorimpl = FloatFloatPair.m10constructorimpl(-f31, f8 - (f30 * fCoerceAtLeast));
                            break;
                        case 5:
                            f8 = f11;
                            f9 = f12;
                            jM10constructorimpl = PointKt.m102plusybeJwSQ(Utils.m118radialToCartesianL6JJ3z0$default(f31, Utils.getFloatPi() + ((f30 * Utils.getFloatPi()) / f10), 0L, 4, null), jM10constructorimpl4);
                            break;
                        case 6:
                            f8 = f11;
                            f9 = f12;
                            jM10constructorimpl = FloatFloatPair.m10constructorimpl(f24 + (f30 * fCoerceAtLeast2), -f31);
                            break;
                        case 7:
                            f8 = f11;
                            f9 = f12;
                            jM10constructorimpl = PointKt.m102plusybeJwSQ(Utils.m118radialToCartesianL6JJ3z0$default(f31, (Utils.getFloatPi() * 1.5f) + ((f30 * Utils.getFloatPi()) / f10), 0L, 4, null), jM10constructorimpl5);
                            break;
                        default:
                            f8 = f11;
                            f9 = f12;
                            jM10constructorimpl = FloatFloatPair.m10constructorimpl(f31, f25 + (f30 * f8));
                            break;
                    }
                    int i7 = i5 + 1;
                    fArr2[i5] = PointKt.m98getXDnnuFBc(jM10constructorimpl) + f6;
                    i5 += 2;
                    fArr2[i7] = PointKt.m99getYDnnuFBc(jM10constructorimpl) + f7;
                    f23 += f22;
                    z = !z;
                    i3++;
                    f11 = f8;
                    f12 = f9;
                }
            }
        }
        return fArr2;
    }

    private static final float[] starVerticesFromNumVerts(int i, float f, float f2, float f3, float f4) {
        float[] fArr = new float[i * 4];
        int i2 = 0;
        for (int i3 = 0; i3 < i; i3++) {
            float f5 = i;
            long jM118radialToCartesianL6JJ3z0$default = Utils.m118radialToCartesianL6JJ3z0$default(f, (Utils.getFloatPi() / f5) * 2 * i3, 0L, 4, null);
            fArr[i2] = PointKt.m98getXDnnuFBc(jM118radialToCartesianL6JJ3z0$default) + f3;
            fArr[i2 + 1] = PointKt.m99getYDnnuFBc(jM118radialToCartesianL6JJ3z0$default) + f4;
            long jM118radialToCartesianL6JJ3z0$default2 = Utils.m118radialToCartesianL6JJ3z0$default(f2, (Utils.getFloatPi() / f5) * ((i3 * 2) + 1), 0L, 4, null);
            int i4 = i2 + 3;
            fArr[i2 + 2] = PointKt.m98getXDnnuFBc(jM118radialToCartesianL6JJ3z0$default2) + f3;
            i2 += 4;
            fArr[i4] = PointKt.m99getYDnnuFBc(jM118radialToCartesianL6JJ3z0$default2) + f4;
        }
        return fArr;
    }
}
