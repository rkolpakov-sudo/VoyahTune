package ru.big.town.anative.databinding;

import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import ru.big.town.anative.R;

/* JADX INFO: loaded from: classes2.dex */
public final class ActivitySplitHostBinding implements ViewBinding {
    private final FrameLayout rootView;
    public final FrameLayout splitDivider;
    public final View splitHandleGrip;
    public final FrameLayout splitHostRoot;
    public final FrameLayout splitPaneLeft;
    public final FrameLayout splitPaneRight;
    public final LinearLayout splitPanes;
    public final SurfaceView splitSurfaceLeft;
    public final SurfaceView splitSurfaceRight;

    private ActivitySplitHostBinding(FrameLayout frameLayout, FrameLayout frameLayout2, View view, FrameLayout frameLayout3, FrameLayout frameLayout4, FrameLayout frameLayout5, LinearLayout linearLayout, SurfaceView surfaceView, SurfaceView surfaceView2) {
        this.rootView = frameLayout;
        this.splitDivider = frameLayout2;
        this.splitHandleGrip = view;
        this.splitHostRoot = frameLayout3;
        this.splitPaneLeft = frameLayout4;
        this.splitPaneRight = frameLayout5;
        this.splitPanes = linearLayout;
        this.splitSurfaceLeft = surfaceView;
        this.splitSurfaceRight = surfaceView2;
    }

    @Override // androidx.viewbinding.ViewBinding
    public FrameLayout getRoot() {
        return this.rootView;
    }

    public static ActivitySplitHostBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static ActivitySplitHostBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.activity_split_host, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static ActivitySplitHostBinding bind(View view) {
        View viewFindChildViewById;
        int i = R.id.splitDivider;
        FrameLayout frameLayout = (FrameLayout) ViewBindings.findChildViewById(view, i);
        if (frameLayout != null && (viewFindChildViewById = ViewBindings.findChildViewById(view, (i = R.id.splitHandleGrip))) != null) {
            FrameLayout frameLayout2 = (FrameLayout) view;
            i = R.id.splitPaneLeft;
            FrameLayout frameLayout3 = (FrameLayout) ViewBindings.findChildViewById(view, i);
            if (frameLayout3 != null) {
                i = R.id.splitPaneRight;
                FrameLayout frameLayout4 = (FrameLayout) ViewBindings.findChildViewById(view, i);
                if (frameLayout4 != null) {
                    i = R.id.splitPanes;
                    LinearLayout linearLayout = (LinearLayout) ViewBindings.findChildViewById(view, i);
                    if (linearLayout != null) {
                        i = R.id.splitSurfaceLeft;
                        SurfaceView surfaceView = (SurfaceView) ViewBindings.findChildViewById(view, i);
                        if (surfaceView != null) {
                            i = R.id.splitSurfaceRight;
                            SurfaceView surfaceView2 = (SurfaceView) ViewBindings.findChildViewById(view, i);
                            if (surfaceView2 != null) {
                                return new ActivitySplitHostBinding(frameLayout2, frameLayout, viewFindChildViewById, frameLayout2, frameLayout3, frameLayout4, linearLayout, surfaceView, surfaceView2);
                            }
                        }
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
