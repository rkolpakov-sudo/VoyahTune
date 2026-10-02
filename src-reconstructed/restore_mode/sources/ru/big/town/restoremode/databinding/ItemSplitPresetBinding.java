package ru.big.town.restoremode.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import ru.big.town.restoremode.R;

/* JADX INFO: loaded from: classes2.dex */
public final class ItemSplitPresetBinding implements ViewBinding {
    private final LinearLayout rootView;
    public final Button splitDeleteBtn;
    public final Button splitLeftBtn;
    public final Spinner splitRatioSpinner;
    public final Switch splitResizableSwitch;
    public final Button splitRightBtn;

    private ItemSplitPresetBinding(LinearLayout linearLayout, Button button, Button button2, Spinner spinner, Switch r5, Button button3) {
        this.rootView = linearLayout;
        this.splitDeleteBtn = button;
        this.splitLeftBtn = button2;
        this.splitRatioSpinner = spinner;
        this.splitResizableSwitch = r5;
        this.splitRightBtn = button3;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ItemSplitPresetBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static ItemSplitPresetBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.item_split_preset, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static ItemSplitPresetBinding bind(View view) {
        int i = R.id.splitDeleteBtn;
        Button button = (Button) ViewBindings.findChildViewById(view, i);
        if (button != null) {
            i = R.id.splitLeftBtn;
            Button button2 = (Button) ViewBindings.findChildViewById(view, i);
            if (button2 != null) {
                i = R.id.splitRatioSpinner;
                Spinner spinner = (Spinner) ViewBindings.findChildViewById(view, i);
                if (spinner != null) {
                    i = R.id.splitResizableSwitch;
                    Switch r7 = (Switch) ViewBindings.findChildViewById(view, i);
                    if (r7 != null) {
                        i = R.id.splitRightBtn;
                        Button button3 = (Button) ViewBindings.findChildViewById(view, i);
                        if (button3 != null) {
                            return new ItemSplitPresetBinding((LinearLayout) view, button, button2, spinner, r7, button3);
                        }
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
