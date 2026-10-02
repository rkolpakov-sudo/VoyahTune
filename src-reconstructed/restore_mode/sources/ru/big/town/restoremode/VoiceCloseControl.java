package ru.big.town.restoremode;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceCloseControl {
    private Dialog glass;
    private Button replay;

    VoiceCloseControl() {
    }

    void attach(final Activity activity, LinearLayout linearLayout, final Runnable runnable, final Runnable runnable2) {
        float f = activity.getResources().getDisplayMetrics().density;
        int iMin = Math.min(Math.round(640.0f * f), activity.getResources().getDisplayMetrics().widthPixels - Math.round(32.0f * f));
        int iRound = Math.round(72.0f * f);
        float f2 = f * 24.0f;
        Button button = new Button(activity);
        button.setText("Отмена");
        button.setAllCaps(false);
        button.setTextSize(24.0f);
        button.setTextColor(-1);
        button.setBackgroundTintList(null);
        button.setStateListAnimator(null);
        button.setPadding(0, 0, 0, 0);
        button.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.VoiceCloseControl$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                runnable.run();
            }
        });
        Button button2 = new Button(activity);
        this.replay = button2;
        button2.setText("Прослушать запись");
        this.replay.setAllCaps(false);
        this.replay.setTextSize(22.0f);
        this.replay.setTextColor(-1);
        this.replay.setBackgroundTintList(null);
        this.replay.setStateListAnimator(null);
        this.replay.setPadding(8, 0, 8, 0);
        this.replay.setEnabled(false);
        this.replay.setAlpha(0.4f);
        this.replay.setVisibility(8);
        this.replay.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.VoiceCloseControl$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                runnable2.run();
            }
        });
        LinearLayout linearLayout2 = new LinearLayout(activity);
        linearLayout2.setGravity(16);
        linearLayout2.addView(button, new LinearLayout.LayoutParams(0, -1, 1.0f));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -1, 1.4f);
        layoutParams.leftMargin = Math.round(12.0f * f);
        linearLayout2.addView(this.replay, layoutParams);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(iMin, iRound);
        layoutParams2.gravity = 1;
        float f3 = 28.0f * f;
        layoutParams2.topMargin = Math.round(f3);
        layoutParams2.bottomMargin = Math.round(8.0f * f);
        GradientDrawable gradientDrawableShape = shape(-1289608150, f2);
        gradientDrawableShape.setStroke(Math.max(1, Math.round(f)), 1090519039);
        RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(687865855), gradientDrawableShape, shape(-1, f2));
        if (Build.VERSION.SDK_INT < 31) {
            button.setBackground(rippleDrawable);
            this.replay.setBackground(rippleDrawable.getConstantState().newDrawable().mutate());
            linearLayout.addView(linearLayout2, layoutParams2);
            return;
        }
        final View view = new View(activity);
        view.setImportantForAccessibility(2);
        linearLayout.addView(view, layoutParams2);
        Dialog dialog = new Dialog(activity);
        this.glass = dialog;
        dialog.requestWindowFeature(1);
        this.glass.setCancelable(false);
        this.glass.setContentView(linearLayout2);
        final Window window = this.glass.getWindow();
        window.setBackgroundDrawable(rippleDrawable);
        window.clearFlags(2);
        window.addFlags(40);
        window.setElevation(0.0f);
        window.setGravity(51);
        window.setBackgroundBlurRadius(Math.round(f3));
        window.getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        button.setBackgroundColor(0);
        button.setForeground(new RippleDrawable(ColorStateList.valueOf(687865855), null, shape(-1, f2)));
        this.replay.setBackgroundColor(0);
        this.replay.setForeground(new RippleDrawable(ColorStateList.valueOf(687865855), null, shape(-1, f2)));
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: ru.big.town.restoremode.VoiceCloseControl$$ExternalSyntheticLambda2
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public final void onGlobalLayout() {
                this.f$0.m1980lambda$attach$2$rubigtownrestoremodeVoiceCloseControl(activity, view, window);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$attach$2$ru-big-town-restoremode-VoiceCloseControl, reason: not valid java name */
    /* synthetic */ void m1980lambda$attach$2$rubigtownrestoremodeVoiceCloseControl(Activity activity, View view, Window window) {
        if (activity.isFinishing() || activity.isDestroyed() || !view.isAttachedToWindow()) {
            return;
        }
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.x = iArr[0];
        attributes.y = iArr[1];
        attributes.width = view.getWidth();
        attributes.height = view.getHeight();
        window.setAttributes(attributes);
        if (this.glass.isShowing()) {
            return;
        }
        this.glass.show();
    }

    void testMode(boolean z) {
        Button button = this.replay;
        if (button != null) {
            button.setVisibility(z ? 0 : 8);
        }
    }

    void recordingAvailable(boolean z) {
        Button button = this.replay;
        if (button == null) {
            return;
        }
        button.setEnabled(z);
        this.replay.setAlpha(z ? 1.0f : 0.4f);
    }

    void playing(boolean z) {
        Button button = this.replay;
        if (button != null) {
            button.setText(z ? "Остановить запись" : "Прослушать запись");
        }
    }

    private static GradientDrawable shape(int i, float f) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(i);
        gradientDrawable.setCornerRadius(f);
        return gradientDrawable;
    }

    void dispose() {
        Dialog dialog = this.glass;
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
