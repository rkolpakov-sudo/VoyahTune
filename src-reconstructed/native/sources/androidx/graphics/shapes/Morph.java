package androidx.graphics.shapes;

import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Morph.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0014\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u001e2\u00020\u0001:\u0001\u001eB\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0002\u0010\u0005J\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\t0\u00072\u0006\u0010\u0010\u001a\u00020\u0011J\u001c\u0010\u0012\u001a\u00020\u00132\b\b\u0002\u0010\u0014\u001a\u00020\u00132\b\b\u0002\u0010\u0015\u001a\u00020\u0016H\u0007J\u0010\u0010\u0017\u001a\u00020\u00132\b\b\u0002\u0010\u0014\u001a\u00020\u0013J2\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u001a\u001a\u00020\u001b2\u0012\u0010\u001c\u001a\u000e\u0012\u0004\u0012\u00020\u001b\u0012\u0004\u0012\u00020\u00190\u001dH\u0087\bø\u0001\u0000R \u0010\u0006\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\t0\b0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R,\u0010\n\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\t0\b0\u00078@X\u0081\u0004¢\u0006\f\u0012\u0004\b\u000b\u0010\f\u001a\u0004\b\r\u0010\u000eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\u001f"}, d2 = {"Landroidx/graphics/shapes/Morph;", "", "start", "Landroidx/graphics/shapes/RoundedPolygon;", "end", "(Landroidx/graphics/shapes/RoundedPolygon;Landroidx/graphics/shapes/RoundedPolygon;)V", "_morphMatch", "", "Lkotlin/Pair;", "Landroidx/graphics/shapes/Cubic;", "morphMatch", "getMorphMatch$annotations", "()V", "getMorphMatch", "()Ljava/util/List;", "asCubics", "progress", "", "calculateBounds", "", "bounds", "approximate", "", "calculateMaxBounds", "forEachCubic", "", "mutableCubic", "Landroidx/graphics/shapes/MutableCubic;", "callback", "Lkotlin/Function1;", "Companion", "graphics-shapes_release"}, k = 1, mv = {1, 8, 0}, xi = 48)
public final class Morph {

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private final List<Pair<Cubic, Cubic>> _morphMatch;
    private final RoundedPolygon end;
    private final RoundedPolygon start;

    public static /* synthetic */ void getMorphMatch$annotations() {
    }

    public final float[] calculateBounds() {
        return calculateBounds$default(this, null, false, 3, null);
    }

    public final float[] calculateBounds(float[] bounds) {
        Intrinsics.checkNotNullParameter(bounds, "bounds");
        return calculateBounds$default(this, bounds, false, 2, null);
    }

    public Morph(RoundedPolygon start, RoundedPolygon end) {
        Intrinsics.checkNotNullParameter(start, "start");
        Intrinsics.checkNotNullParameter(end, "end");
        this.start = start;
        this.end = end;
        this._morphMatch = INSTANCE.match$graphics_shapes_release(start, end);
    }

    public final List<Pair<Cubic, Cubic>> getMorphMatch() {
        return this._morphMatch;
    }

    public static /* synthetic */ float[] calculateBounds$default(Morph morph, float[] fArr, boolean z, int i, Object obj) {
        if ((i & 1) != 0) {
            fArr = new float[4];
        }
        if ((i & 2) != 0) {
            z = true;
        }
        return morph.calculateBounds(fArr, z);
    }

    public final float[] calculateBounds(float[] bounds, boolean approximate) {
        Intrinsics.checkNotNullParameter(bounds, "bounds");
        this.start.calculateBounds(bounds, approximate);
        float f = bounds[0];
        float f2 = bounds[1];
        float f3 = bounds[2];
        float f4 = bounds[3];
        this.end.calculateBounds(bounds, approximate);
        bounds[0] = Math.min(f, bounds[0]);
        bounds[1] = Math.min(f2, bounds[1]);
        bounds[2] = Math.max(f3, bounds[2]);
        bounds[3] = Math.max(f4, bounds[3]);
        return bounds;
    }

    public static /* synthetic */ float[] calculateMaxBounds$default(Morph morph, float[] fArr, int i, Object obj) {
        if ((i & 1) != 0) {
            fArr = new float[4];
        }
        return morph.calculateMaxBounds(fArr);
    }

    public final float[] calculateMaxBounds(float[] bounds) {
        Intrinsics.checkNotNullParameter(bounds, "bounds");
        this.start.calculateMaxBounds(bounds);
        float f = bounds[0];
        float f2 = bounds[1];
        float f3 = bounds[2];
        float f4 = bounds[3];
        this.end.calculateMaxBounds(bounds);
        bounds[0] = Math.min(f, bounds[0]);
        bounds[1] = Math.min(f2, bounds[1]);
        bounds[2] = Math.max(f3, bounds[2]);
        bounds[3] = Math.max(f4, bounds[3]);
        return bounds;
    }

    public final List<Cubic> asCubics(float progress) {
        List listCreateListBuilder = CollectionsKt.createListBuilder();
        int size = this._morphMatch.size();
        Cubic cubic = null;
        Cubic cubic2 = null;
        int i = 0;
        while (i < size) {
            float[] fArr = new float[8];
            for (int i2 = 0; i2 < 8; i2++) {
                fArr[i2] = Utils.interpolate(this._morphMatch.get(i).getFirst().getPoints()[i2], this._morphMatch.get(i).getSecond().getPoints()[i2], progress);
            }
            Cubic cubic3 = new Cubic(fArr);
            if (cubic2 == null) {
                cubic2 = cubic3;
            }
            if (cubic != null) {
                listCreateListBuilder.add(cubic);
            }
            i++;
            cubic = cubic3;
        }
        if (cubic != null && cubic2 != null) {
            listCreateListBuilder.add(CubicKt.Cubic(cubic.getAnchor0X(), cubic.getAnchor0Y(), cubic.getControl0X(), cubic.getControl0Y(), cubic.getControl1X(), cubic.getControl1Y(), cubic2.getAnchor0X(), cubic2.getAnchor0Y()));
        }
        return CollectionsKt.build(listCreateListBuilder);
    }

    public static /* synthetic */ void forEachCubic$default(Morph morph, float f, MutableCubic mutableCubic, Function1 callback, int i, Object obj) {
        if ((i & 2) != 0) {
            mutableCubic = new MutableCubic();
        }
        Intrinsics.checkNotNullParameter(mutableCubic, "mutableCubic");
        Intrinsics.checkNotNullParameter(callback, "callback");
        int size = morph.getMorphMatch().size();
        for (int i2 = 0; i2 < size; i2++) {
            mutableCubic.interpolate(morph.getMorphMatch().get(i2).getFirst(), morph.getMorphMatch().get(i2).getSecond(), f);
            callback.invoke(mutableCubic);
        }
    }

    public final void forEachCubic(float progress, MutableCubic mutableCubic, Function1<? super MutableCubic, Unit> callback) {
        Intrinsics.checkNotNullParameter(mutableCubic, "mutableCubic");
        Intrinsics.checkNotNullParameter(callback, "callback");
        int size = getMorphMatch().size();
        for (int i = 0; i < size; i++) {
            mutableCubic.interpolate(getMorphMatch().get(i).getFirst(), getMorphMatch().get(i).getSecond(), progress);
            callback.invoke(mutableCubic);
        }
    }

    /* JADX INFO: compiled from: Morph.kt */
    @Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0080\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J/\u0010\u0003\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\u00050\u00042\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\bH\u0001¢\u0006\u0002\b\n¨\u0006\u000b"}, d2 = {"Landroidx/graphics/shapes/Morph$Companion;", "", "()V", "match", "", "Lkotlin/Pair;", "Landroidx/graphics/shapes/Cubic;", "p1", "Landroidx/graphics/shapes/RoundedPolygon;", "p2", "match$graphics_shapes_release", "graphics-shapes_release"}, k = 1, mv = {1, 8, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        public final List<Pair<Cubic, Cubic>> match$graphics_shapes_release(RoundedPolygon p1, RoundedPolygon p2) {
            Pair<MeasuredPolygon.MeasuredCubic, MeasuredPolygon.MeasuredCubic> pairCutAtProgress;
            Pair<MeasuredPolygon.MeasuredCubic, MeasuredPolygon.MeasuredCubic> pairCutAtProgress2;
            Intrinsics.checkNotNullParameter(p1, "p1");
            Intrinsics.checkNotNullParameter(p2, "p2");
            MeasuredPolygon measuredPolygonMeasurePolygon$graphics_shapes_release = MeasuredPolygon.INSTANCE.measurePolygon$graphics_shapes_release(new AngleMeasurer(p1.getCenterX(), p1.getCenterY()), p1);
            MeasuredPolygon measuredPolygonMeasurePolygon$graphics_shapes_release2 = MeasuredPolygon.INSTANCE.measurePolygon$graphics_shapes_release(new AngleMeasurer(p2.getCenterX(), p2.getCenterY()), p2);
            DoubleMapper doubleMapperFeatureMapper = FeatureMappingKt.featureMapper(measuredPolygonMeasurePolygon$graphics_shapes_release.getFeatures(), measuredPolygonMeasurePolygon$graphics_shapes_release2.getFeatures());
            float map = doubleMapperFeatureMapper.map(0.0f);
            String unused = MorphKt.LOG_TAG;
            MeasuredPolygon measuredPolygonCutAndShift = measuredPolygonMeasurePolygon$graphics_shapes_release2.cutAndShift(map);
            ArrayList arrayList = new ArrayList();
            MeasuredPolygon measuredPolygon = measuredPolygonMeasurePolygon$graphics_shapes_release;
            MeasuredPolygon.MeasuredCubic measuredCubicComponent2 = (MeasuredPolygon.MeasuredCubic) CollectionsKt.getOrNull(measuredPolygon, 0);
            MeasuredPolygon measuredPolygon2 = measuredPolygonCutAndShift;
            MeasuredPolygon.MeasuredCubic measuredCubicComponent3 = (MeasuredPolygon.MeasuredCubic) CollectionsKt.getOrNull(measuredPolygon2, 0);
            int i = 1;
            int i2 = 1;
            while (measuredCubicComponent2 != null && measuredCubicComponent3 != null) {
                float endOutlineProgress = i == measuredPolygonMeasurePolygon$graphics_shapes_release.size() ? 1.0f : measuredCubicComponent2.getEndOutlineProgress();
                float fMapBack = i2 == measuredPolygonCutAndShift.size() ? 1.0f : doubleMapperFeatureMapper.mapBack(Utils.positiveModulo(measuredCubicComponent3.getEndOutlineProgress() + map, 1.0f));
                float fMin = Math.min(endOutlineProgress, fMapBack);
                String unused2 = MorphKt.LOG_TAG;
                float f = 1.0E-6f + fMin;
                if (endOutlineProgress > f) {
                    String unused3 = MorphKt.LOG_TAG;
                    pairCutAtProgress = measuredCubicComponent2.cutAtProgress(fMin);
                } else {
                    pairCutAtProgress = TuplesKt.to(measuredCubicComponent2, CollectionsKt.getOrNull(measuredPolygon, i));
                    i++;
                }
                MeasuredPolygon.MeasuredCubic measuredCubicComponent1 = pairCutAtProgress.component1();
                measuredCubicComponent2 = pairCutAtProgress.component2();
                if (fMapBack > f) {
                    String unused4 = MorphKt.LOG_TAG;
                    pairCutAtProgress2 = measuredCubicComponent3.cutAtProgress(Utils.positiveModulo(doubleMapperFeatureMapper.map(fMin) - map, 1.0f));
                } else {
                    pairCutAtProgress2 = TuplesKt.to(measuredCubicComponent3, CollectionsKt.getOrNull(measuredPolygon2, i2));
                    i2++;
                }
                MeasuredPolygon.MeasuredCubic measuredCubicComponent4 = pairCutAtProgress2.component1();
                measuredCubicComponent3 = pairCutAtProgress2.component2();
                String unused5 = MorphKt.LOG_TAG;
                arrayList.add(TuplesKt.to(measuredCubicComponent1.getCubic(), measuredCubicComponent4.getCubic()));
            }
            if (measuredCubicComponent2 == null && measuredCubicComponent3 == null) {
                return arrayList;
            }
            throw new IllegalArgumentException("Expected both Polygon's Cubic to be fully matched".toString());
        }
    }

    public final void forEachCubic(float f, Function1<? super MutableCubic, Unit> callback) {
        Intrinsics.checkNotNullParameter(callback, "callback");
        MutableCubic mutableCubic = new MutableCubic();
        int size = getMorphMatch().size();
        for (int i = 0; i < size; i++) {
            mutableCubic.interpolate(getMorphMatch().get(i).getFirst(), getMorphMatch().get(i).getSecond(), f);
            callback.invoke(mutableCubic);
        }
    }
}
