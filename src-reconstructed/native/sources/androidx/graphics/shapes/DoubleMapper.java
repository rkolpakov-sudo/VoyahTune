package androidx.graphics.shapes;

import androidx.collection.MutableFloatList;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: FloatMapping.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0000\u0018\u0000 \r2\u00020\u0001:\u0001\rB1\u0012*\u0010\u0002\u001a\u0016\u0012\u0012\b\u0001\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u00040\u0003\"\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u0004¢\u0006\u0002\u0010\u0006J\u000e\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u0005J\u000e\u0010\f\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u0005R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Landroidx/graphics/shapes/DoubleMapper;", "", "mappings", "", "Lkotlin/Pair;", "", "([Lkotlin/Pair;)V", "sourceValues", "Landroidx/collection/MutableFloatList;", "targetValues", "map", "x", "mapBack", "Companion", "graphics-shapes_release"}, k = 1, mv = {1, 8, 0}, xi = 48)
public final class DoubleMapper {
    public static final DoubleMapper Identity;
    private final MutableFloatList sourceValues;
    private final MutableFloatList targetValues;

    public DoubleMapper(Pair<Float, Float>... mappings) {
        Intrinsics.checkNotNullParameter(mappings, "mappings");
        this.sourceValues = new MutableFloatList(mappings.length);
        this.targetValues = new MutableFloatList(mappings.length);
        int length = mappings.length;
        for (int i = 0; i < length; i++) {
            this.sourceValues.add(mappings[i].getFirst().floatValue());
            this.targetValues.add(mappings[i].getSecond().floatValue());
        }
        FloatMappingKt.validateProgress(this.sourceValues);
        FloatMappingKt.validateProgress(this.targetValues);
    }

    public final float map(float x) {
        return FloatMappingKt.linearMap(this.sourceValues, this.targetValues, x);
    }

    public final float mapBack(float x) {
        return FloatMappingKt.linearMap(this.targetValues, this.sourceValues, x);
    }

    static {
        Float fValueOf = Float.valueOf(0.0f);
        Float fValueOf2 = Float.valueOf(0.5f);
        Identity = new DoubleMapper(TuplesKt.to(fValueOf, fValueOf), TuplesKt.to(fValueOf2, fValueOf2));
    }
}
