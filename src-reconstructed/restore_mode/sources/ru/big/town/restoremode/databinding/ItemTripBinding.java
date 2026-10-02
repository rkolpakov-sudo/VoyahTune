package ru.big.town.restoremode.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import ru.big.town.restoremode.R;

/* JADX INFO: loaded from: classes2.dex */
public final class ItemTripBinding implements ViewBinding {
    private final LinearLayout rootView;
    public final TextView tripDate;
    public final ImageButton tripDelete;
    public final TextView tripDuration;

    private ItemTripBinding(LinearLayout linearLayout, TextView textView, ImageButton imageButton, TextView textView2) {
        this.rootView = linearLayout;
        this.tripDate = textView;
        this.tripDelete = imageButton;
        this.tripDuration = textView2;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ItemTripBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static ItemTripBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.item_trip, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static ItemTripBinding bind(View view) {
        int i = R.id.tripDate;
        TextView textView = (TextView) ViewBindings.findChildViewById(view, i);
        if (textView != null) {
            i = R.id.tripDelete;
            ImageButton imageButton = (ImageButton) ViewBindings.findChildViewById(view, i);
            if (imageButton != null) {
                i = R.id.tripDuration;
                TextView textView2 = (TextView) ViewBindings.findChildViewById(view, i);
                if (textView2 != null) {
                    return new ItemTripBinding((LinearLayout) view, textView, imageButton, textView2);
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
