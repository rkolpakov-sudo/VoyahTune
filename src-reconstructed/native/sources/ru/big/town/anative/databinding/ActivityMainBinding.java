package ru.big.town.anative.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import ru.big.town.anative.R;

/* JADX INFO: loaded from: classes2.dex */
public final class ActivityMainBinding implements ViewBinding {
    private final ConstraintLayout rootView;
    public final Button setAllModes;

    private ActivityMainBinding(ConstraintLayout constraintLayout, Button button) {
        this.rootView = constraintLayout;
        this.setAllModes = button;
    }

    @Override // androidx.viewbinding.ViewBinding
    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static ActivityMainBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static ActivityMainBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.activity_main, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static ActivityMainBinding bind(View view) {
        int i = R.id.setAllModes;
        Button button = (Button) ViewBindings.findChildViewById(view, i);
        if (button != null) {
            return new ActivityMainBinding((ConstraintLayout) view, button);
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
