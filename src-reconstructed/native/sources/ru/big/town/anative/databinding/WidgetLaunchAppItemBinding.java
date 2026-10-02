package ru.big.town.anative.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import ru.big.town.anative.R;

/* JADX INFO: loaded from: classes2.dex */
public final class WidgetLaunchAppItemBinding implements ViewBinding {
    public final ImageView launchAppIcon;
    public final LinearLayout launchAppItem;
    public final TextView launchAppLabel;
    private final LinearLayout rootView;
    public final LinearLayout widgetBtnClose;
    public final LinearLayout widgetBtnFullscreen;
    public final LinearLayout widgetBtnOpen;

    private WidgetLaunchAppItemBinding(LinearLayout linearLayout, ImageView imageView, LinearLayout linearLayout2, TextView textView, LinearLayout linearLayout3, LinearLayout linearLayout4, LinearLayout linearLayout5) {
        this.rootView = linearLayout;
        this.launchAppIcon = imageView;
        this.launchAppItem = linearLayout2;
        this.launchAppLabel = textView;
        this.widgetBtnClose = linearLayout3;
        this.widgetBtnFullscreen = linearLayout4;
        this.widgetBtnOpen = linearLayout5;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static WidgetLaunchAppItemBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static WidgetLaunchAppItemBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.widget_launch_app_item, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static WidgetLaunchAppItemBinding bind(View view) {
        int i = R.id.launch_app_icon;
        ImageView imageView = (ImageView) ViewBindings.findChildViewById(view, i);
        if (imageView != null) {
            LinearLayout linearLayout = (LinearLayout) view;
            i = R.id.launch_app_label;
            TextView textView = (TextView) ViewBindings.findChildViewById(view, i);
            if (textView != null) {
                i = R.id.widget_btn_close;
                LinearLayout linearLayout2 = (LinearLayout) ViewBindings.findChildViewById(view, i);
                if (linearLayout2 != null) {
                    i = R.id.widget_btn_fullscreen;
                    LinearLayout linearLayout3 = (LinearLayout) ViewBindings.findChildViewById(view, i);
                    if (linearLayout3 != null) {
                        i = R.id.widget_btn_open;
                        LinearLayout linearLayout4 = (LinearLayout) ViewBindings.findChildViewById(view, i);
                        if (linearLayout4 != null) {
                            return new WidgetLaunchAppItemBinding(linearLayout, imageView, linearLayout, textView, linearLayout2, linearLayout3, linearLayout4);
                        }
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
