package ru.big.town.restoremode.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import ru.big.town.restoremode.R;

/* JADX INFO: loaded from: classes2.dex */
public final class ActivityTripHistoryBinding implements ViewBinding {
    public final ImageButton buttonBackHistory;
    private final LinearLayout rootView;
    public final LinearLayout tripHistoryRoot;
    public final LinearLayout tripLogContainer;

    private ActivityTripHistoryBinding(LinearLayout linearLayout, ImageButton imageButton, LinearLayout linearLayout2, LinearLayout linearLayout3) {
        this.rootView = linearLayout;
        this.buttonBackHistory = imageButton;
        this.tripHistoryRoot = linearLayout2;
        this.tripLogContainer = linearLayout3;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ActivityTripHistoryBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static ActivityTripHistoryBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.activity_trip_history, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static ActivityTripHistoryBinding bind(View view) {
        int i = R.id.buttonBackHistory;
        ImageButton imageButton = (ImageButton) ViewBindings.findChildViewById(view, i);
        if (imageButton != null) {
            LinearLayout linearLayout = (LinearLayout) view;
            int i2 = R.id.tripLogContainer;
            LinearLayout linearLayout2 = (LinearLayout) ViewBindings.findChildViewById(view, i2);
            if (linearLayout2 != null) {
                return new ActivityTripHistoryBinding(linearLayout, imageButton, linearLayout, linearLayout2);
            }
            i = i2;
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
