package ru.big.town.restoremode.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import ru.big.town.restoremode.R;

/* JADX INFO: loaded from: classes2.dex */
public final class ItemAppWidgetProfileBinding implements ViewBinding {
    public final ImageButton appWidgetProfileDelete;
    public final Spinner appWidgetProfileDpi;
    public final ImageView appWidgetProfileIcon;
    public final TextView appWidgetProfileLabel;
    private final LinearLayout rootView;

    private ItemAppWidgetProfileBinding(LinearLayout linearLayout, ImageButton imageButton, Spinner spinner, ImageView imageView, TextView textView) {
        this.rootView = linearLayout;
        this.appWidgetProfileDelete = imageButton;
        this.appWidgetProfileDpi = spinner;
        this.appWidgetProfileIcon = imageView;
        this.appWidgetProfileLabel = textView;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ItemAppWidgetProfileBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static ItemAppWidgetProfileBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.item_app_widget_profile, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static ItemAppWidgetProfileBinding bind(View view) {
        int i = R.id.appWidgetProfileDelete;
        ImageButton imageButton = (ImageButton) ViewBindings.findChildViewById(view, i);
        if (imageButton != null) {
            i = R.id.appWidgetProfileDpi;
            Spinner spinner = (Spinner) ViewBindings.findChildViewById(view, i);
            if (spinner != null) {
                i = R.id.appWidgetProfileIcon;
                ImageView imageView = (ImageView) ViewBindings.findChildViewById(view, i);
                if (imageView != null) {
                    i = R.id.appWidgetProfileLabel;
                    TextView textView = (TextView) ViewBindings.findChildViewById(view, i);
                    if (textView != null) {
                        return new ItemAppWidgetProfileBinding((LinearLayout) view, imageButton, spinner, imageView, textView);
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
