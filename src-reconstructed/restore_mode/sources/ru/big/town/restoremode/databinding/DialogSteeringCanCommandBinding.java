package ru.big.town.restoremode.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import ru.big.town.restoremode.R;

/* JADX INFO: loaded from: classes2.dex */
public final class DialogSteeringCanCommandBinding implements ViewBinding {
    private final LinearLayout rootView;
    public final TextView steerCanCommandError;
    public final EditText steerCanCommandInput;

    private DialogSteeringCanCommandBinding(LinearLayout linearLayout, TextView textView, EditText editText) {
        this.rootView = linearLayout;
        this.steerCanCommandError = textView;
        this.steerCanCommandInput = editText;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static DialogSteeringCanCommandBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static DialogSteeringCanCommandBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.dialog_steering_can_command, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static DialogSteeringCanCommandBinding bind(View view) {
        int i = R.id.steerCanCommandError;
        TextView textView = (TextView) ViewBindings.findChildViewById(view, i);
        if (textView != null) {
            i = R.id.steerCanCommandInput;
            EditText editText = (EditText) ViewBindings.findChildViewById(view, i);
            if (editText != null) {
                return new DialogSteeringCanCommandBinding((LinearLayout) view, textView, editText);
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
