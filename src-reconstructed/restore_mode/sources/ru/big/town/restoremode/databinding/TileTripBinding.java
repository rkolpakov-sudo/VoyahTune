package ru.big.town.restoremode.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import ru.big.town.restoremode.R;

/* JADX INFO: loaded from: classes2.dex */
public final class TileTripBinding implements ViewBinding {
    public final Button buttonTripHistory;
    public final Button buttonTripReset;
    private final LinearLayout rootView;
    public final LinearLayout tripCard;
    public final TextView tripDate;
    public final TextView tripStatus;
    public final TextView tripTimer;

    private TileTripBinding(LinearLayout linearLayout, Button button, Button button2, LinearLayout linearLayout2, TextView textView, TextView textView2, TextView textView3) {
        this.rootView = linearLayout;
        this.buttonTripHistory = button;
        this.buttonTripReset = button2;
        this.tripCard = linearLayout2;
        this.tripDate = textView;
        this.tripStatus = textView2;
        this.tripTimer = textView3;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static TileTripBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static TileTripBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.tile_trip, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static TileTripBinding bind(View view) {
        int i = R.id.buttonTripHistory;
        Button button = (Button) ViewBindings.findChildViewById(view, i);
        if (button != null) {
            i = R.id.buttonTripReset;
            Button button2 = (Button) ViewBindings.findChildViewById(view, i);
            if (button2 != null) {
                LinearLayout linearLayout = (LinearLayout) view;
                i = R.id.tripDate;
                TextView textView = (TextView) ViewBindings.findChildViewById(view, i);
                if (textView != null) {
                    i = R.id.tripStatus;
                    TextView textView2 = (TextView) ViewBindings.findChildViewById(view, i);
                    if (textView2 != null) {
                        i = R.id.tripTimer;
                        TextView textView3 = (TextView) ViewBindings.findChildViewById(view, i);
                        if (textView3 != null) {
                            return new TileTripBinding(linearLayout, button, button2, linearLayout, textView, textView2, textView3);
                        }
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
