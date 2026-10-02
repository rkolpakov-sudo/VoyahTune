package ru.big.town.restoremode.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import ru.big.town.restoremode.R;

/* JADX INFO: loaded from: classes2.dex */
public final class SpinnerRatioItemBinding implements ViewBinding {
    private final TextView rootView;

    private SpinnerRatioItemBinding(TextView textView) {
        this.rootView = textView;
    }

    @Override // androidx.viewbinding.ViewBinding
    public TextView getRoot() {
        return this.rootView;
    }

    public static SpinnerRatioItemBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static SpinnerRatioItemBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.spinner_ratio_item, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static SpinnerRatioItemBinding bind(View view) {
        if (view == null) {
            throw new NullPointerException("rootView");
        }
        return new SpinnerRatioItemBinding((TextView) view);
    }
}
