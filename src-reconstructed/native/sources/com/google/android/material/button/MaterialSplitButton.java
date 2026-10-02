package com.google.android.material.button;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.android.material.R;
import com.google.android.material.theme.overlay.MaterialThemeOverlay;

/* JADX INFO: loaded from: classes.dex */
public class MaterialSplitButton extends MaterialButtonGroup {
    private static final int DEF_STYLE_RES = R.style.Widget_Material3_MaterialSplitButton;
    private static final int REQUIRED_BUTTON_COUNT = 2;

    public MaterialSplitButton(Context context) {
        this(context, null);
    }

    public MaterialSplitButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.materialSplitButtonStyle);
    }

    public MaterialSplitButton(Context context, AttributeSet attributeSet, int i) {
        super(MaterialThemeOverlay.wrap(context, attributeSet, i, DEF_STYLE_RES), attributeSet, i);
    }

    @Override // com.google.android.material.button.MaterialButtonGroup, android.view.ViewGroup
    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        int i2;
        if (!(view instanceof MaterialButton)) {
            throw new IllegalArgumentException("MaterialSplitButton can only hold MaterialButtons.");
        }
        if (getChildCount() > 2) {
            throw new IllegalArgumentException("MaterialSplitButton can only hold two MaterialButtons.");
        }
        MaterialButton materialButton = (MaterialButton) view;
        super.addView(view, i, layoutParams);
        if (indexOfChild(view) == 1) {
            materialButton.setCheckable(true);
            materialButton.setA11yClassName(Button.class.getName());
            Resources resources = getResources();
            if (materialButton.isChecked()) {
                i2 = R.string.mtrl_button_expanded_content_description;
            } else {
                i2 = R.string.mtrl_button_collapsed_content_description;
            }
            materialButton.setStateDescription(resources.getString(i2));
            materialButton.addOnCheckedChangeListener(new MaterialButton.OnCheckedChangeListener() { // from class: com.google.android.material.button.MaterialSplitButton$$ExternalSyntheticLambda0
                @Override // com.google.android.material.button.MaterialButton.OnCheckedChangeListener
                public final void onCheckedChanged(MaterialButton materialButton2, boolean z) {
                    this.f$0.m136xf376ee79(materialButton2, z);
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$addView$0$com-google-android-material-button-MaterialSplitButton, reason: not valid java name */
    /* synthetic */ void m136xf376ee79(MaterialButton materialButton, boolean z) {
        int i;
        Resources resources = getResources();
        if (z) {
            i = R.string.mtrl_button_expanded_content_description;
        } else {
            i = R.string.mtrl_button_collapsed_content_description;
        }
        materialButton.setStateDescription(resources.getString(i));
    }
}
