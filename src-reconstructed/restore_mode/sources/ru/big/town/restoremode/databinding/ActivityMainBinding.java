package ru.big.town.restoremode.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import ru.big.town.restoremode.R;

/* JADX INFO: loaded from: classes2.dex */
public final class ActivityMainBinding implements ViewBinding {
    public final HorizontalScrollView contentScroll;
    public final LinearLayout main;
    public final LinearLayout mainContent;
    private final LinearLayout rootView;
    public final GridLayout splitTilesGrid;

    private ActivityMainBinding(LinearLayout linearLayout, HorizontalScrollView horizontalScrollView, LinearLayout linearLayout2, LinearLayout linearLayout3, GridLayout gridLayout) {
        this.rootView = linearLayout;
        this.contentScroll = horizontalScrollView;
        this.main = linearLayout2;
        this.mainContent = linearLayout3;
        this.splitTilesGrid = gridLayout;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ActivityMainBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static ActivityMainBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.activity_main, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static ActivityMainBinding bind(View view) {
        int i = R.id.contentScroll;
        HorizontalScrollView horizontalScrollView = (HorizontalScrollView) ViewBindings.findChildViewById(view, i);
        if (horizontalScrollView != null) {
            LinearLayout linearLayout = (LinearLayout) view;
            i = R.id.mainContent;
            LinearLayout linearLayout2 = (LinearLayout) ViewBindings.findChildViewById(view, i);
            if (linearLayout2 != null) {
                i = R.id.splitTilesGrid;
                GridLayout gridLayout = (GridLayout) ViewBindings.findChildViewById(view, i);
                if (gridLayout != null) {
                    return new ActivityMainBinding(linearLayout, horizontalScrollView, linearLayout, linearLayout2, gridLayout);
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
