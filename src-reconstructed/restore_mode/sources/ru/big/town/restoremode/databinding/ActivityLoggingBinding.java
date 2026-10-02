package ru.big.town.restoremode.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import ru.big.town.restoremode.R;

/* JADX INFO: loaded from: classes2.dex */
public final class ActivityLoggingBinding implements ViewBinding {
    public final ImageButton buttonBackLogging;
    public final Button buttonShareLog;
    public final LinearLayout main;
    private final LinearLayout rootView;
    public final ScrollView scrollLog;
    public final Switch switchLogging;
    public final TextView textLog;
    public final TextView textLogPath;

    private ActivityLoggingBinding(LinearLayout linearLayout, ImageButton imageButton, Button button, LinearLayout linearLayout2, ScrollView scrollView, Switch r6, TextView textView, TextView textView2) {
        this.rootView = linearLayout;
        this.buttonBackLogging = imageButton;
        this.buttonShareLog = button;
        this.main = linearLayout2;
        this.scrollLog = scrollView;
        this.switchLogging = r6;
        this.textLog = textView;
        this.textLogPath = textView2;
    }

    @Override // androidx.viewbinding.ViewBinding
    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ActivityLoggingBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static ActivityLoggingBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.activity_logging, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static ActivityLoggingBinding bind(View view) {
        int i = R.id.buttonBackLogging;
        ImageButton imageButton = (ImageButton) ViewBindings.findChildViewById(view, i);
        if (imageButton != null) {
            i = R.id.buttonShareLog;
            Button button = (Button) ViewBindings.findChildViewById(view, i);
            if (button != null) {
                LinearLayout linearLayout = (LinearLayout) view;
                i = R.id.scrollLog;
                ScrollView scrollView = (ScrollView) ViewBindings.findChildViewById(view, i);
                if (scrollView != null) {
                    i = R.id.switchLogging;
                    Switch r8 = (Switch) ViewBindings.findChildViewById(view, i);
                    if (r8 != null) {
                        i = R.id.textLog;
                        TextView textView = (TextView) ViewBindings.findChildViewById(view, i);
                        if (textView != null) {
                            i = R.id.textLogPath;
                            TextView textView2 = (TextView) ViewBindings.findChildViewById(view, i);
                            if (textView2 != null) {
                                return new ActivityLoggingBinding(linearLayout, imageButton, button, linearLayout, scrollView, r8, textView, textView2);
                            }
                        }
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(view.getResources().getResourceName(i)));
    }
}
