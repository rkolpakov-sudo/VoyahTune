package ru.big.town.restoremode.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import ru.big.town.restoremode.R;

/* JADX INFO: loaded from: classes2.dex */
public final class ItemDialWidgetSettingBinding implements ViewBinding {
    public final ImageButton dialSettingDelete;
    public final EditText dialSettingName;
    public final EditText dialSettingNumber;
    public final Button dialSettingSave;
    private final LinearLayout rootView;

    private ItemDialWidgetSettingBinding(LinearLayout linearLayout, ImageButton imageButton, EditText editText, EditText editText2, Button button) {
        this.rootView = linearLayout;
        this.dialSettingDelete = imageButton;
        this.dialSettingName = editText;
        this.dialSettingNumber = editText2;
        this.dialSettingSave = button;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ItemDialWidgetSettingBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static ItemDialWidgetSettingBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.item_dial_widget_setting, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static ItemDialWidgetSettingBinding bind(View view) {
        int i = R.id.dialSettingDelete;
        ImageButton imageButton = (ImageButton) ViewBindings.findChildViewById(view, i);
        if (imageButton != null) {
            i = R.id.dialSettingName;
            EditText editText = (EditText) ViewBindings.findChildViewById(view, i);
            if (editText != null) {
                i = R.id.dialSettingNumber;
                EditText editText2 = (EditText) ViewBindings.findChildViewById(view, i);
                if (editText2 != null) {
                    i = R.id.dialSettingSave;
                    Button button = (Button) ViewBindings.findChildViewById(view, i);
                    if (button != null) {
                        return new ItemDialWidgetSettingBinding((LinearLayout) view, imageButton, editText, editText2, button);
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
