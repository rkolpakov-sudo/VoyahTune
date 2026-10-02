package ru.big.town.restoremode.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import ru.big.town.restoremode.R;

/* JADX INFO: loaded from: classes2.dex */
public final class TileBatteryHeatBinding implements ViewBinding {
    public final TextView batteryHeatFail;
    public final ImageView batteryHeatIcon;
    public final TextView batteryHeatState;
    public final TextView batteryHeatStatus;
    public final TextView batteryHeatTemp;
    public final Button buttonBatteryHeat;
    public final LinearLayout cardBatteryHeat;
    private final LinearLayout rootView;

    private TileBatteryHeatBinding(LinearLayout linearLayout, TextView textView, ImageView imageView, TextView textView2, TextView textView3, TextView textView4, Button button, LinearLayout linearLayout2) {
        this.rootView = linearLayout;
        this.batteryHeatFail = textView;
        this.batteryHeatIcon = imageView;
        this.batteryHeatState = textView2;
        this.batteryHeatStatus = textView3;
        this.batteryHeatTemp = textView4;
        this.buttonBatteryHeat = button;
        this.cardBatteryHeat = linearLayout2;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static TileBatteryHeatBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static TileBatteryHeatBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.tile_battery_heat, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static TileBatteryHeatBinding bind(View view) {
        int i = R.id.batteryHeatFail;
        TextView textView = (TextView) ViewBindings.findChildViewById(view, i);
        if (textView != null) {
            i = R.id.batteryHeatIcon;
            ImageView imageView = (ImageView) ViewBindings.findChildViewById(view, i);
            if (imageView != null) {
                i = R.id.batteryHeatState;
                TextView textView2 = (TextView) ViewBindings.findChildViewById(view, i);
                if (textView2 != null) {
                    i = R.id.batteryHeatStatus;
                    TextView textView3 = (TextView) ViewBindings.findChildViewById(view, i);
                    if (textView3 != null) {
                        i = R.id.batteryHeatTemp;
                        TextView textView4 = (TextView) ViewBindings.findChildViewById(view, i);
                        if (textView4 != null) {
                            i = R.id.buttonBatteryHeat;
                            Button button = (Button) ViewBindings.findChildViewById(view, i);
                            if (button != null) {
                                LinearLayout linearLayout = (LinearLayout) view;
                                return new TileBatteryHeatBinding(linearLayout, textView, imageView, textView2, textView3, textView4, button, linearLayout);
                            }
                        }
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
