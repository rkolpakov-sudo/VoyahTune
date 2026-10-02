package ru.big.town.restoremode.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ScrollView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import ru.big.town.restoremode.R;

/* JADX INFO: loaded from: classes2.dex */
public final class TileLaunchAppsBinding implements ViewBinding {
    public final GridLayout launchAppsGrid;
    public final ScrollView launchAppsScroll;
    public final FrameLayout launchAppsWidget;
    private final FrameLayout rootView;

    private TileLaunchAppsBinding(FrameLayout frameLayout, GridLayout gridLayout, ScrollView scrollView, FrameLayout frameLayout2) {
        this.rootView = frameLayout;
        this.launchAppsGrid = gridLayout;
        this.launchAppsScroll = scrollView;
        this.launchAppsWidget = frameLayout2;
    }

    @Override // androidx.viewbinding.ViewBinding
    public FrameLayout getRoot() {
        return this.rootView;
    }

    public static TileLaunchAppsBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static TileLaunchAppsBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.tile_launch_apps, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static TileLaunchAppsBinding bind(View view) {
        int i = R.id.launchAppsGrid;
        GridLayout gridLayout = (GridLayout) ViewBindings.findChildViewById(view, i);
        if (gridLayout != null) {
            i = R.id.launchAppsScroll;
            ScrollView scrollView = (ScrollView) ViewBindings.findChildViewById(view, i);
            if (scrollView != null) {
                FrameLayout frameLayout = (FrameLayout) view;
                return new TileLaunchAppsBinding(frameLayout, gridLayout, scrollView, frameLayout);
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
