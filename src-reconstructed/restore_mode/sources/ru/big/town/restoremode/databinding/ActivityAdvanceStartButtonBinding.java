package ru.big.town.restoremode.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import ru.big.town.restoremode.R;

/* JADX INFO: loaded from: classes2.dex */
public final class ActivityAdvanceStartButtonBinding implements ViewBinding {
    public final Button buttonApplyStarButton1;
    public final Button buttonApplyStarButton2;
    public final Button buttonBackStarButton;
    public final Button buttonCleanStarButton1;
    public final Button buttonCleanStarButton2;
    public final Button buttonSaveStarButton;
    public final ConstraintLayout mainStarButton;
    public final EditText rawCanCodesStarButton1;
    public final EditText rawCanCodesStarButton2;
    private final ConstraintLayout rootView;

    private ActivityAdvanceStartButtonBinding(ConstraintLayout constraintLayout, Button button, Button button2, Button button3, Button button4, Button button5, Button button6, ConstraintLayout constraintLayout2, EditText editText, EditText editText2) {
        this.rootView = constraintLayout;
        this.buttonApplyStarButton1 = button;
        this.buttonApplyStarButton2 = button2;
        this.buttonBackStarButton = button3;
        this.buttonCleanStarButton1 = button4;
        this.buttonCleanStarButton2 = button5;
        this.buttonSaveStarButton = button6;
        this.mainStarButton = constraintLayout2;
        this.rawCanCodesStarButton1 = editText;
        this.rawCanCodesStarButton2 = editText2;
    }

    @Override // androidx.viewbinding.ViewBinding
    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static ActivityAdvanceStartButtonBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static ActivityAdvanceStartButtonBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.activity_advance_start_button, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static ActivityAdvanceStartButtonBinding bind(View view) {
        int i = R.id.buttonApplyStarButton1;
        Button button = (Button) ViewBindings.findChildViewById(view, i);
        if (button != null) {
            i = R.id.buttonApplyStarButton2;
            Button button2 = (Button) ViewBindings.findChildViewById(view, i);
            if (button2 != null) {
                i = R.id.buttonBackStarButton;
                Button button3 = (Button) ViewBindings.findChildViewById(view, i);
                if (button3 != null) {
                    i = R.id.buttonCleanStarButton1;
                    Button button4 = (Button) ViewBindings.findChildViewById(view, i);
                    if (button4 != null) {
                        i = R.id.buttonCleanStarButton2;
                        Button button5 = (Button) ViewBindings.findChildViewById(view, i);
                        if (button5 != null) {
                            i = R.id.buttonSaveStarButton;
                            Button button6 = (Button) ViewBindings.findChildViewById(view, i);
                            if (button6 != null) {
                                ConstraintLayout constraintLayout = (ConstraintLayout) view;
                                i = R.id.rawCanCodesStarButton1;
                                EditText editText = (EditText) ViewBindings.findChildViewById(view, i);
                                if (editText != null) {
                                    i = R.id.rawCanCodesStarButton2;
                                    EditText editText2 = (EditText) ViewBindings.findChildViewById(view, i);
                                    if (editText2 != null) {
                                        return new ActivityAdvanceStartButtonBinding(constraintLayout, button, button2, button3, button4, button5, button6, constraintLayout, editText, editText2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
