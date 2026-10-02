package ru.big.town.anative.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import ru.big.town.anative.R;

/* JADX INFO: loaded from: classes2.dex */
public final class WidgetLaunchAppsBinding implements ViewBinding {
    public final LinearLayout launchAppsList;
    private final HorizontalScrollView rootView;

    private WidgetLaunchAppsBinding(HorizontalScrollView horizontalScrollView, LinearLayout linearLayout) {
        this.rootView = horizontalScrollView;
        this.launchAppsList = linearLayout;
    }

    @Override // androidx.viewbinding.ViewBinding
    public HorizontalScrollView getRoot() {
        return this.rootView;
    }

    public static WidgetLaunchAppsBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static WidgetLaunchAppsBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.widget_launch_apps, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static WidgetLaunchAppsBinding bind(View view) {
        int i = R.id.launch_apps_list;
        LinearLayout linearLayout = (LinearLayout) ViewBindings.findChildViewById(view, i);
        if (linearLayout != null) {
            return new WidgetLaunchAppsBinding((HorizontalScrollView) view, linearLayout);
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
