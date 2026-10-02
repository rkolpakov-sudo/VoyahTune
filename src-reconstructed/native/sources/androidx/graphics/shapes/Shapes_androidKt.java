package androidx.graphics.shapes;

import android.graphics.Matrix;
import android.graphics.Path;
import androidx.collection.FloatFloatPair;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Shapes.android.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u00000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u001e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0002\u001a\u001c\u0010\u0007\u001a\u00020\u0003*\u00020\b2\u0006\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u001a\u0016\u0010\u0007\u001a\u00020\u0003*\u00020\u000b2\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0007\u001a\u0012\u0010\f\u001a\u00020\u000b*\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000e¨\u0006\u000f"}, d2 = {"pathFromCubics", "", "path", "Landroid/graphics/Path;", "cubics", "", "Landroidx/graphics/shapes/Cubic;", "toPath", "Landroidx/graphics/shapes/Morph;", "progress", "", "Landroidx/graphics/shapes/RoundedPolygon;", "transformed", "matrix", "Landroid/graphics/Matrix;", "graphics-shapes_release"}, k = 2, mv = {1, 8, 0}, xi = 48)
public final class Shapes_androidKt {
    public static final Path toPath(RoundedPolygon roundedPolygon) {
        Intrinsics.checkNotNullParameter(roundedPolygon, "<this>");
        return toPath$default(roundedPolygon, null, 1, null);
    }

    public static final RoundedPolygon transformed(RoundedPolygon roundedPolygon, final Matrix matrix) {
        Intrinsics.checkNotNullParameter(roundedPolygon, "<this>");
        Intrinsics.checkNotNullParameter(matrix, "matrix");
        final float[] fArr = new float[2];
        return roundedPolygon.transformed(new PointTransformer() { // from class: androidx.graphics.shapes.Shapes_androidKt.transformed.1
            @Override // androidx.graphics.shapes.PointTransformer
            /* JADX INFO: renamed from: transform-XgqJiTY */
            public final long mo107transformXgqJiTY(float f, float f2) {
                float[] fArr2 = fArr;
                fArr2[0] = f;
                fArr2[1] = f2;
                matrix.mapPoints(fArr2);
                float[] fArr3 = fArr;
                return FloatFloatPair.m10constructorimpl(fArr3[0], fArr3[1]);
            }
        });
    }

    public static /* synthetic */ Path toPath$default(RoundedPolygon roundedPolygon, Path path, int i, Object obj) {
        if ((i & 1) != 0) {
            path = new Path();
        }
        return toPath(roundedPolygon, path);
    }

    public static final Path toPath(RoundedPolygon roundedPolygon, Path path) {
        Intrinsics.checkNotNullParameter(roundedPolygon, "<this>");
        Intrinsics.checkNotNullParameter(path, "path");
        pathFromCubics(path, roundedPolygon.getCubics());
        return path;
    }

    public static /* synthetic */ Path toPath$default(Morph morph, float f, Path path, int i, Object obj) {
        if ((i & 2) != 0) {
            path = new Path();
        }
        return toPath(morph, f, path);
    }

    public static final Path toPath(Morph morph, float f, Path path) {
        Intrinsics.checkNotNullParameter(morph, "<this>");
        Intrinsics.checkNotNullParameter(path, "path");
        pathFromCubics(path, morph.asCubics(f));
        return path;
    }

    private static final void pathFromCubics(Path path, List<? extends Cubic> list) {
        path.rewind();
        int size = list.size();
        boolean z = true;
        for (int i = 0; i < size; i++) {
            Cubic cubic = list.get(i);
            if (z) {
                path.moveTo(cubic.getAnchor0X(), cubic.getAnchor0Y());
                z = false;
            }
            path.cubicTo(cubic.getControl0X(), cubic.getControl0Y(), cubic.getControl1X(), cubic.getControl1Y(), cubic.getAnchor1X(), cubic.getAnchor1Y());
        }
        path.close();
    }
}
