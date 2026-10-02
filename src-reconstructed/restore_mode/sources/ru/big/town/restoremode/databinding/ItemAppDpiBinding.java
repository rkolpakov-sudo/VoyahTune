package ru.big.town.restoremode.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import ru.big.town.restoremode.R;

/* JADX INFO: loaded from: classes2.dex */
public final class ItemAppDpiBinding implements ViewBinding {
    public final ImageView appDpiIco;
    public final TextView appDpiLabel;
    public final Spinner appDpiSpinner;
    private final LinearLayout rootView;

    private ItemAppDpiBinding(LinearLayout linearLayout, ImageView imageView, TextView textView, Spinner spinner) {
        this.rootView = linearLayout;
        this.appDpiIco = imageView;
        this.appDpiLabel = textView;
        this.appDpiSpinner = spinner;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ItemAppDpiBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static ItemAppDpiBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.item_app_dpi, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static ItemAppDpiBinding bind(View view) {
        int i = R.id.appDpiIco;
        ImageView imageView = (ImageView) ViewBindings.findChildViewById(view, i);
        if (imageView != null) {
            i = R.id.appDpiLabel;
            TextView textView = (TextView) ViewBindings.findChildViewById(view, i);
            if (textView != null) {
                i = R.id.appDpiSpinner;
                Spinner spinner = (Spinner) ViewBindings.findChildViewById(view, i);
                if (spinner != null) {
                    return new ItemAppDpiBinding((LinearLayout) view, imageView, textView, spinner);
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
