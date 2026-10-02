package ru.big.town.restoremode.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import ru.big.town.restoremode.R;

/* JADX INFO: loaded from: classes2.dex */
public final class ItemAppWidgetSettingBinding implements ViewBinding {
    public final Button appWidgetAddProfile;
    public final LinearLayout appWidgetDelayContainer;
    public final LinearLayout appWidgetProfiles;
    public final Switch appWidgetSettingAutoStart;
    public final SeekBar appWidgetSettingDelay;
    public final TextView appWidgetSettingDelayText;
    public final ImageButton appWidgetSettingDelete;
    public final Spinner appWidgetSettingHeight;
    public final ImageView appWidgetSettingIcon;
    public final TextView appWidgetSettingLabel;
    public final Spinner appWidgetSettingWidth;
    private final LinearLayout rootView;

    private ItemAppWidgetSettingBinding(LinearLayout linearLayout, Button button, LinearLayout linearLayout2, LinearLayout linearLayout3, Switch r5, SeekBar seekBar, TextView textView, ImageButton imageButton, Spinner spinner, ImageView imageView, TextView textView2, Spinner spinner2) {
        this.rootView = linearLayout;
        this.appWidgetAddProfile = button;
        this.appWidgetDelayContainer = linearLayout2;
        this.appWidgetProfiles = linearLayout3;
        this.appWidgetSettingAutoStart = r5;
        this.appWidgetSettingDelay = seekBar;
        this.appWidgetSettingDelayText = textView;
        this.appWidgetSettingDelete = imageButton;
        this.appWidgetSettingHeight = spinner;
        this.appWidgetSettingIcon = imageView;
        this.appWidgetSettingLabel = textView2;
        this.appWidgetSettingWidth = spinner2;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ItemAppWidgetSettingBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static ItemAppWidgetSettingBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.item_app_widget_setting, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static ItemAppWidgetSettingBinding bind(View view) {
        int i = R.id.appWidgetAddProfile;
        Button button = (Button) ViewBindings.findChildViewById(view, i);
        if (button != null) {
            i = R.id.appWidgetDelayContainer;
            LinearLayout linearLayout = (LinearLayout) ViewBindings.findChildViewById(view, i);
            if (linearLayout != null) {
                i = R.id.appWidgetProfiles;
                LinearLayout linearLayout2 = (LinearLayout) ViewBindings.findChildViewById(view, i);
                if (linearLayout2 != null) {
                    i = R.id.appWidgetSettingAutoStart;
                    Switch r7 = (Switch) ViewBindings.findChildViewById(view, i);
                    if (r7 != null) {
                        i = R.id.appWidgetSettingDelay;
                        SeekBar seekBar = (SeekBar) ViewBindings.findChildViewById(view, i);
                        if (seekBar != null) {
                            i = R.id.appWidgetSettingDelayText;
                            TextView textView = (TextView) ViewBindings.findChildViewById(view, i);
                            if (textView != null) {
                                i = R.id.appWidgetSettingDelete;
                                ImageButton imageButton = (ImageButton) ViewBindings.findChildViewById(view, i);
                                if (imageButton != null) {
                                    i = R.id.appWidgetSettingHeight;
                                    Spinner spinner = (Spinner) ViewBindings.findChildViewById(view, i);
                                    if (spinner != null) {
                                        i = R.id.appWidgetSettingIcon;
                                        ImageView imageView = (ImageView) ViewBindings.findChildViewById(view, i);
                                        if (imageView != null) {
                                            i = R.id.appWidgetSettingLabel;
                                            TextView textView2 = (TextView) ViewBindings.findChildViewById(view, i);
                                            if (textView2 != null) {
                                                i = R.id.appWidgetSettingWidth;
                                                Spinner spinner2 = (Spinner) ViewBindings.findChildViewById(view, i);
                                                if (spinner2 != null) {
                                                    return new ItemAppWidgetSettingBinding((LinearLayout) view, button, linearLayout, linearLayout2, r7, seekBar, textView, imageButton, spinner, imageView, textView2, spinner2);
                                                }
                                            }
                                        }
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
