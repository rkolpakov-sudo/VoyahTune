package ru.big.town.restoremode.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import ru.big.town.restoremode.R;

/* JADX INFO: loaded from: classes2.dex */
public final class TileAutoLightBinding implements ViewBinding {
    public final TextView autoLightBadge;
    public final LinearLayout cardAutoLight;
    private final LinearLayout rootView;

    private TileAutoLightBinding(LinearLayout linearLayout, TextView textView, LinearLayout linearLayout2) {
        this.rootView = linearLayout;
        this.autoLightBadge = textView;
        this.cardAutoLight = linearLayout2;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static TileAutoLightBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static TileAutoLightBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.tile_auto_light, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static TileAutoLightBinding bind(View view) {
        int i = R.id.autoLightBadge;
        TextView textView = (TextView) ViewBindings.findChildViewById(view, i);
        if (textView != null) {
            LinearLayout linearLayout = (LinearLayout) view;
            return new TileAutoLightBinding(linearLayout, textView, linearLayout);
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
