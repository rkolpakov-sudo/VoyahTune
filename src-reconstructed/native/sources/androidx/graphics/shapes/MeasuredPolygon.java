package androidx.graphics.shapes;

import androidx.collection.FloatList;
import androidx.collection.MutableFloatList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.AbstractList;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;

/* JADX INFO: compiled from: PolygonMeasure.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u0005\b\u0000\u0018\u0000 \u00182\f\u0012\b\u0012\u00060\u0002R\u00020\u00000\u0001:\u0002\u0018\u0019B3\b\u0012\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0006\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0002\u0010\fJ\u000e\u0010\u0013\u001a\u00020\u00002\u0006\u0010\u0014\u001a\u00020\u0015J\u0015\u0010\u0016\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0017\u001a\u00020\u0010H\u0096\u0002R\u0018\u0010\b\u001a\f\u0012\b\u0012\u00060\u0002R\u00020\u00000\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\u00020\u00108VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0011\u0010\u0012¨\u0006\u001a"}, d2 = {"Landroidx/graphics/shapes/MeasuredPolygon;", "Lkotlin/collections/AbstractList;", "Landroidx/graphics/shapes/MeasuredPolygon$MeasuredCubic;", "measurer", "Landroidx/graphics/shapes/Measurer;", "features", "", "Landroidx/graphics/shapes/ProgressableFeature;", "cubics", "Landroidx/graphics/shapes/Cubic;", "outlineProgress", "Landroidx/collection/FloatList;", "(Landroidx/graphics/shapes/Measurer;Ljava/util/List;Ljava/util/List;Landroidx/collection/FloatList;)V", "getFeatures", "()Ljava/util/List;", "size", "", "getSize", "()I", "cutAndShift", "cuttingPoint", "", "get", "index", "Companion", "MeasuredCubic", "graphics-shapes_release"}, k = 1, mv = {1, 8, 0}, xi = 48)
public final class MeasuredPolygon extends AbstractList<MeasuredCubic> {

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private final List<MeasuredCubic> cubics;
    private final List<ProgressableFeature> features;
    private final Measurer measurer;

    public /* synthetic */ MeasuredPolygon(Measurer measurer, List list, List list2, FloatList floatList, DefaultConstructorMarker defaultConstructorMarker) {
        this(measurer, list, list2, floatList);
    }

    public /* bridge */ boolean contains(MeasuredCubic measuredCubic) {
        return super.contains(measuredCubic);
    }

    @Override // kotlin.collections.AbstractCollection, java.util.Collection, java.util.List
    public final /* bridge */ boolean contains(Object obj) {
        if (obj instanceof MeasuredCubic) {
            return contains((MeasuredCubic) obj);
        }
        return false;
    }

    public /* bridge */ int indexOf(MeasuredCubic measuredCubic) {
        return super.indexOf(measuredCubic);
    }

    @Override // kotlin.collections.AbstractList, java.util.List
    public final /* bridge */ int indexOf(Object obj) {
        if (obj instanceof MeasuredCubic) {
            return indexOf((MeasuredCubic) obj);
        }
        return -1;
    }

    public /* bridge */ int lastIndexOf(MeasuredCubic measuredCubic) {
        return super.lastIndexOf(measuredCubic);
    }

    @Override // kotlin.collections.AbstractList, java.util.List
    public final /* bridge */ int lastIndexOf(Object obj) {
        if (obj instanceof MeasuredCubic) {
            return lastIndexOf((MeasuredCubic) obj);
        }
        return -1;
    }

    public final List<ProgressableFeature> getFeatures() {
        return this.features;
    }

    private MeasuredPolygon(Measurer measurer, List<ProgressableFeature> list, List<? extends Cubic> list2, FloatList floatList) {
        if (floatList.getSize() != list2.size() + 1) {
            throw new IllegalArgumentException("Outline progress size is expected to be the cubics size + 1".toString());
        }
        if (floatList.first() != 0.0f) {
            throw new IllegalArgumentException("First outline progress value is expected to be zero".toString());
        }
        if (floatList.last() != 1.0f) {
            throw new IllegalArgumentException("Last outline progress value is expected to be one".toString());
        }
        this.measurer = measurer;
        this.features = list;
        ArrayList arrayList = new ArrayList();
        int size = list2.size();
        int i = 0;
        float f = 0.0f;
        while (i < size) {
            int i2 = i + 1;
            if (floatList.get(i2) - floatList.get(i) > 1.0E-4f) {
                arrayList.add(new MeasuredCubic(this, list2.get(i), f, floatList.get(i2)));
                f = floatList.get(i2);
            }
            i = i2;
        }
        MeasuredCubic.updateProgressRange$graphics_shapes_release$default((MeasuredCubic) arrayList.get(CollectionsKt.getLastIndex(arrayList)), 0.0f, 1.0f, 1, null);
        this.cubics = arrayList;
    }

    /* JADX INFO: compiled from: PolygonMeasure.kt */
    @Metadata(d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0080\u0004\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0005¢\u0006\u0002\u0010\u0007J\"\u0010\u0010\u001a\u0016\u0012\b\u0012\u00060\u0000R\u00020\u0012\u0012\b\u0012\u00060\u0000R\u00020\u00120\u00112\u0006\u0010\u0013\u001a\u00020\u0005J\b\u0010\u0014\u001a\u00020\u0015H\u0016J!\u0010\u0016\u001a\u00020\u00172\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005H\u0000¢\u0006\u0002\b\u0018R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u001e\u0010\u0006\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u0005@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\r\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\fR\u001e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u0005@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\f¨\u0006\u0019"}, d2 = {"Landroidx/graphics/shapes/MeasuredPolygon$MeasuredCubic;", "", "cubic", "Landroidx/graphics/shapes/Cubic;", "startOutlineProgress", "", "endOutlineProgress", "(Landroidx/graphics/shapes/MeasuredPolygon;Landroidx/graphics/shapes/Cubic;FF)V", "getCubic", "()Landroidx/graphics/shapes/Cubic;", "<set-?>", "getEndOutlineProgress", "()F", "measuredSize", "getMeasuredSize", "getStartOutlineProgress", "cutAtProgress", "Lkotlin/Pair;", "Landroidx/graphics/shapes/MeasuredPolygon;", "cutOutlineProgress", "toString", "", "updateProgressRange", "", "updateProgressRange$graphics_shapes_release", "graphics-shapes_release"}, k = 1, mv = {1, 8, 0}, xi = 48)
    public final class MeasuredCubic {
        private final Cubic cubic;
        private float endOutlineProgress;
        private final float measuredSize;
        private float startOutlineProgress;
        final /* synthetic */ MeasuredPolygon this$0;

        public MeasuredCubic(MeasuredPolygon measuredPolygon, Cubic cubic, float f, float f2) {
            Intrinsics.checkNotNullParameter(cubic, "cubic");
            this.this$0 = measuredPolygon;
            this.cubic = cubic;
            if (f2 >= f) {
                this.measuredSize = measuredPolygon.measurer.measureCubic(cubic);
                this.startOutlineProgress = f;
                this.endOutlineProgress = f2;
                return;
            }
            throw new IllegalArgumentException("endOutlineProgress is expected to be equal or greater than startOutlineProgress".toString());
        }

        public final Cubic getCubic() {
            return this.cubic;
        }

        public final float getMeasuredSize() {
            return this.measuredSize;
        }

        public final float getStartOutlineProgress() {
            return this.startOutlineProgress;
        }

        public final float getEndOutlineProgress() {
            return this.endOutlineProgress;
        }

        public static /* synthetic */ void updateProgressRange$graphics_shapes_release$default(MeasuredCubic measuredCubic, float f, float f2, int i, Object obj) {
            if ((i & 1) != 0) {
                f = measuredCubic.startOutlineProgress;
            }
            if ((i & 2) != 0) {
                f2 = measuredCubic.endOutlineProgress;
            }
            measuredCubic.updateProgressRange$graphics_shapes_release(f, f2);
        }

        public final void updateProgressRange$graphics_shapes_release(float startOutlineProgress, float endOutlineProgress) {
            if (endOutlineProgress < startOutlineProgress) {
                throw new IllegalArgumentException("endOutlineProgress is expected to be equal or greater than startOutlineProgress".toString());
            }
            this.startOutlineProgress = startOutlineProgress;
            this.endOutlineProgress = endOutlineProgress;
        }

        public final Pair<MeasuredCubic, MeasuredCubic> cutAtProgress(float cutOutlineProgress) {
            float fCoerceIn = RangesKt.coerceIn(cutOutlineProgress, this.startOutlineProgress, this.endOutlineProgress);
            float f = this.endOutlineProgress;
            float f2 = this.startOutlineProgress;
            float fFindCubicCutPoint = this.this$0.measurer.findCubicCutPoint(this.cubic, ((fCoerceIn - f2) / (f - f2)) * this.measuredSize);
            if (0.0f <= fFindCubicCutPoint && fFindCubicCutPoint <= 1.0f) {
                String unused = PolygonMeasureKt.LOG_TAG;
                Pair<Cubic, Cubic> pairSplit = this.cubic.split(fFindCubicCutPoint);
                return TuplesKt.to(new MeasuredCubic(this.this$0, pairSplit.component1(), this.startOutlineProgress, fCoerceIn), new MeasuredCubic(this.this$0, pairSplit.component2(), fCoerceIn, this.endOutlineProgress));
            }
            throw new IllegalArgumentException("Cubic cut point is expected to be between 0 and 1".toString());
        }

        public String toString() {
            return "MeasuredCubic(outlineProgress=[" + this.startOutlineProgress + " .. " + this.endOutlineProgress + "], size=" + this.measuredSize + ", cubic=" + this.cubic + ')';
        }
    }

    public final MeasuredPolygon cutAndShift(float cuttingPoint) {
        float fPositiveModulo;
        if (0.0f > cuttingPoint || cuttingPoint > 1.0f) {
            throw new IllegalArgumentException("Cutting point is expected to be between 0 and 1".toString());
        }
        if (cuttingPoint < 1.0E-4f) {
            return this;
        }
        Iterator<MeasuredCubic> it = this.cubics.iterator();
        int i = 0;
        while (true) {
            if (!it.hasNext()) {
                i = -1;
                break;
            }
            MeasuredCubic next = it.next();
            float startOutlineProgress = next.getStartOutlineProgress();
            if (cuttingPoint <= next.getEndOutlineProgress() && startOutlineProgress <= cuttingPoint) {
                break;
            }
            i++;
        }
        Pair<MeasuredCubic, MeasuredCubic> pairCutAtProgress = this.cubics.get(i).cutAtProgress(cuttingPoint);
        MeasuredCubic measuredCubicComponent1 = pairCutAtProgress.component1();
        MeasuredCubic measuredCubicComponent2 = pairCutAtProgress.component2();
        String unused = PolygonMeasureKt.LOG_TAG;
        List listMutableListOf = CollectionsKt.mutableListOf(measuredCubicComponent2.getCubic());
        int size = this.cubics.size();
        for (int i2 = 1; i2 < size; i2++) {
            List<MeasuredCubic> list = this.cubics;
            listMutableListOf.add(list.get((i2 + i) % list.size()).getCubic());
        }
        listMutableListOf.add(measuredCubicComponent1.getCubic());
        MutableFloatList mutableFloatList = new MutableFloatList(this.cubics.size() + 2);
        int size2 = this.cubics.size() + 2;
        int i3 = 0;
        while (i3 < size2) {
            if (i3 == 0) {
                fPositiveModulo = 0.0f;
            } else {
                fPositiveModulo = i3 == this.cubics.size() + 1 ? 1.0f : Utils.positiveModulo(this.cubics.get(((i + i3) - 1) % this.cubics.size()).getEndOutlineProgress() - cuttingPoint, 1.0f);
            }
            mutableFloatList.add(fPositiveModulo);
            i3++;
        }
        List listCreateListBuilder = CollectionsKt.createListBuilder();
        int size3 = this.features.size();
        for (int i4 = 0; i4 < size3; i4++) {
            listCreateListBuilder.add(new ProgressableFeature(Utils.positiveModulo(this.features.get(i4).getProgress() - cuttingPoint, 1.0f), this.features.get(i4).getFeature()));
        }
        return new MeasuredPolygon(this.measurer, CollectionsKt.build(listCreateListBuilder), listMutableListOf, mutableFloatList);
    }

    @Override // kotlin.collections.AbstractList, kotlin.collections.AbstractCollection
    public int getSize() {
        return this.cubics.size();
    }

    @Override // kotlin.collections.AbstractList, java.util.List
    public MeasuredCubic get(int index) {
        return this.cubics.get(index);
    }

    /* JADX INFO: compiled from: PolygonMeasure.kt */
    @Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001d\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0000¢\u0006\u0002\b\t¨\u0006\n"}, d2 = {"Landroidx/graphics/shapes/MeasuredPolygon$Companion;", "", "()V", "measurePolygon", "Landroidx/graphics/shapes/MeasuredPolygon;", "measurer", "Landroidx/graphics/shapes/Measurer;", "polygon", "Landroidx/graphics/shapes/RoundedPolygon;", "measurePolygon$graphics_shapes_release", "graphics-shapes_release"}, k = 1, mv = {1, 8, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final MeasuredPolygon measurePolygon$graphics_shapes_release(Measurer measurer, RoundedPolygon polygon) {
            ArrayList arrayListListOf;
            Intrinsics.checkNotNullParameter(measurer, "measurer");
            Intrinsics.checkNotNullParameter(polygon, "polygon");
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            int size = polygon.getFeatures$graphics_shapes_release().size();
            for (int i = 0; i < size; i++) {
                Feature feature = polygon.getFeatures$graphics_shapes_release().get(i);
                int size2 = feature.getCubics().size();
                for (int i2 = 0; i2 < size2; i2++) {
                    if ((feature instanceof Feature.Corner) && i2 == feature.getCubics().size() / 2) {
                        arrayList2.add(TuplesKt.to(feature, Integer.valueOf(arrayList.size())));
                    }
                    arrayList.add(feature.getCubics().get(i2));
                }
            }
            ArrayList<Cubic> arrayList3 = arrayList;
            Float fValueOf = Float.valueOf(0.0f);
            int iCollectionSizeOrDefault = CollectionsKt.collectionSizeOrDefault(arrayList3, 9);
            if (iCollectionSizeOrDefault == 0) {
                arrayListListOf = CollectionsKt.listOf(fValueOf);
            } else {
                ArrayList arrayList4 = new ArrayList(iCollectionSizeOrDefault + 1);
                arrayList4.add(fValueOf);
                for (Cubic cubic : arrayList3) {
                    float fFloatValue = fValueOf.floatValue();
                    float fMeasureCubic = measurer.measureCubic(cubic);
                    if (fMeasureCubic < 0.0f) {
                        throw new IllegalArgumentException("Measured cubic is expected to be greater or equal to zero".toString());
                    }
                    Unit unit = Unit.INSTANCE;
                    fValueOf = Float.valueOf(fFloatValue + fMeasureCubic);
                    arrayList4.add(fValueOf);
                }
                arrayListListOf = arrayList4;
            }
            float fFloatValue2 = ((Number) CollectionsKt.last(arrayListListOf)).floatValue();
            MutableFloatList mutableFloatList = new MutableFloatList(arrayListListOf.size());
            int size3 = arrayListListOf.size();
            for (int i3 = 0; i3 < size3; i3++) {
                mutableFloatList.add(((Number) arrayListListOf.get(i3)).floatValue() / fFloatValue2);
            }
            String unused = PolygonMeasureKt.LOG_TAG;
            List listCreateListBuilder = CollectionsKt.createListBuilder();
            int size4 = arrayList2.size();
            for (int i4 = 0; i4 < size4; i4++) {
                int iIntValue = ((Number) ((Pair) arrayList2.get(i4)).getSecond()).intValue();
                listCreateListBuilder.add(new ProgressableFeature((mutableFloatList.get(iIntValue) + mutableFloatList.get(iIntValue + 1)) / 2, (Feature) ((Pair) arrayList2.get(i4)).getFirst()));
            }
            return new MeasuredPolygon(measurer, CollectionsKt.build(listCreateListBuilder), arrayList, mutableFloatList, null);
        }
    }
}
