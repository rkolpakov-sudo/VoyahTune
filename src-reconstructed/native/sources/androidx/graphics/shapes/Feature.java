package androidx.graphics.shapes;

import androidx.collection.FloatFloatPair;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Features.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\b \u0018\u00002\u00020\u0001:\u0002\f\rB\u0013\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0002\u0010\u0005J\u0015\u0010\b\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\nH ¢\u0006\u0002\b\u000bR\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u000e"}, d2 = {"Landroidx/graphics/shapes/Feature;", "", "cubics", "", "Landroidx/graphics/shapes/Cubic;", "(Ljava/util/List;)V", "getCubics", "()Ljava/util/List;", "transformed", "f", "Landroidx/graphics/shapes/PointTransformer;", "transformed$graphics_shapes_release", "Corner", "Edge", "graphics-shapes_release"}, k = 1, mv = {1, 8, 0}, xi = 48)
public abstract class Feature {
    private final List<Cubic> cubics;

    public abstract Feature transformed$graphics_shapes_release(PointTransformer f);

    /* JADX WARN: Multi-variable type inference failed */
    public Feature(List<? extends Cubic> cubics) {
        Intrinsics.checkNotNullParameter(cubics, "cubics");
        this.cubics = cubics;
    }

    public final List<Cubic> getCubics() {
        return this.cubics;
    }

    /* JADX INFO: compiled from: Features.kt */
    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0000\u0018\u00002\u00020\u0001B\u0013\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0002\u0010\u0005J\b\u0010\u0006\u001a\u00020\u0007H\u0016J\u0015\u0010\b\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\nH\u0010¢\u0006\u0002\b\u000b¨\u0006\f"}, d2 = {"Landroidx/graphics/shapes/Feature$Edge;", "Landroidx/graphics/shapes/Feature;", "cubics", "", "Landroidx/graphics/shapes/Cubic;", "(Ljava/util/List;)V", "toString", "", "transformed", "f", "Landroidx/graphics/shapes/PointTransformer;", "transformed$graphics_shapes_release", "graphics-shapes_release"}, k = 1, mv = {1, 8, 0}, xi = 48)
    public static final class Edge extends Feature {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public Edge(List<? extends Cubic> cubics) {
            super(cubics);
            Intrinsics.checkNotNullParameter(cubics, "cubics");
        }

        @Override // androidx.graphics.shapes.Feature
        public Edge transformed$graphics_shapes_release(PointTransformer f) {
            Intrinsics.checkNotNullParameter(f, "f");
            List listCreateListBuilder = CollectionsKt.createListBuilder();
            int size = getCubics().size();
            for (int i = 0; i < size; i++) {
                listCreateListBuilder.add(getCubics().get(i).transformed(f));
            }
            return new Edge(CollectionsKt.build(listCreateListBuilder));
        }

        public String toString() {
            return "Edge";
        }
    }

    /* JADX INFO: compiled from: Features.kt */
    @Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0000\u0018\u00002\u00020\u0001B5\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\n\u0010\u0005\u001a\u00060\u0006j\u0002`\u0007\u0012\n\u0010\b\u001a\u00060\u0006j\u0002`\u0007\u0012\b\b\u0002\u0010\t\u001a\u00020\n¢\u0006\u0002\u0010\u000bJ\b\u0010\u0012\u001a\u00020\u0013H\u0016J\u0015\u0010\u0014\u001a\u00020\u00012\u0006\u0010\u0015\u001a\u00020\u0016H\u0010¢\u0006\u0002\b\u0017R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u001d\u0010\b\u001a\u00060\u0006j\u0002`\u0007ø\u0001\u0000ø\u0001\u0001¢\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u000e\u0010\u000fR\u001d\u0010\u0005\u001a\u00060\u0006j\u0002`\u0007ø\u0001\u0000ø\u0001\u0001¢\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u0011\u0010\u000f\u0082\u0002\u000b\n\u0005\b¡\u001e0\u0001\n\u0002\b!¨\u0006\u0018"}, d2 = {"Landroidx/graphics/shapes/Feature$Corner;", "Landroidx/graphics/shapes/Feature;", "cubics", "", "Landroidx/graphics/shapes/Cubic;", "vertex", "Landroidx/collection/FloatFloatPair;", "Landroidx/graphics/shapes/Point;", "roundedCenter", "convex", "", "(Ljava/util/List;JJZLkotlin/jvm/internal/DefaultConstructorMarker;)V", "getConvex", "()Z", "getRoundedCenter-1ufDz9w", "()J", "J", "getVertex-1ufDz9w", "toString", "", "transformed", "f", "Landroidx/graphics/shapes/PointTransformer;", "transformed$graphics_shapes_release", "graphics-shapes_release"}, k = 1, mv = {1, 8, 0}, xi = 48)
    public static final class Corner extends Feature {
        private final boolean convex;
        private final long roundedCenter;
        private final long vertex;

        public /* synthetic */ Corner(List list, long j, long j2, boolean z, DefaultConstructorMarker defaultConstructorMarker) {
            this(list, j, j2, z);
        }

        public /* synthetic */ Corner(List list, long j, long j2, boolean z, int i, DefaultConstructorMarker defaultConstructorMarker) {
            this(list, j, j2, (i & 8) != 0 ? true : z, null);
        }

        /* JADX INFO: renamed from: getVertex-1ufDz9w, reason: not valid java name and from getter */
        public final long getVertex() {
            return this.vertex;
        }

        /* JADX INFO: renamed from: getRoundedCenter-1ufDz9w, reason: not valid java name and from getter */
        public final long getRoundedCenter() {
            return this.roundedCenter;
        }

        public final boolean getConvex() {
            return this.convex;
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        private Corner(List<? extends Cubic> cubics, long j, long j2, boolean z) {
            super(cubics);
            Intrinsics.checkNotNullParameter(cubics, "cubics");
            this.vertex = j;
            this.roundedCenter = j2;
            this.convex = z;
        }

        @Override // androidx.graphics.shapes.Feature
        public Feature transformed$graphics_shapes_release(PointTransformer f) {
            Intrinsics.checkNotNullParameter(f, "f");
            List listCreateListBuilder = CollectionsKt.createListBuilder();
            int size = getCubics().size();
            for (int i = 0; i < size; i++) {
                listCreateListBuilder.add(getCubics().get(i).transformed(f));
            }
            return new Corner(CollectionsKt.build(listCreateListBuilder), PointKt.m105transformedso9K2fw(this.vertex, f), PointKt.m105transformedso9K2fw(this.roundedCenter, f), this.convex, null);
        }

        public String toString() {
            return "Corner: vertex=" + ((Object) FloatFloatPair.m17toStringimpl(this.vertex)) + ", center=" + ((Object) FloatFloatPair.m17toStringimpl(this.roundedCenter)) + ", convex=" + this.convex;
        }
    }
}
