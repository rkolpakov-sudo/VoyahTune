package ru.big.town.restoremode.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import ru.big.town.restoremode.R;

/* JADX INFO: loaded from: classes2.dex */
public final class TileEmbeddedAppBinding implements ViewBinding {
    public final TextView appWidgetBadge;
    public final ImageButton appWidgetClose;
    public final LinearLayout appWidgetControls;
    public final ImageButton appWidgetDragHandle;
    public final ImageButton appWidgetDragHandleLauncher;
    public final ImageButton appWidgetExpand;
    public final LinearLayout appWidgetLauncherControls;
    public final HorizontalScrollView appWidgetLauncherScroll;
    public final LinearLayout appWidgetList;
    public final FrameLayout appWidgetRoot;
    public final ImageButton appWidgetSwap;
    public final ImageButton appWidgetSwapLauncher;
    private final FrameLayout rootView;

    private TileEmbeddedAppBinding(FrameLayout frameLayout, TextView textView, ImageButton imageButton, LinearLayout linearLayout, ImageButton imageButton2, ImageButton imageButton3, ImageButton imageButton4, LinearLayout linearLayout2, HorizontalScrollView horizontalScrollView, LinearLayout linearLayout3, FrameLayout frameLayout2, ImageButton imageButton5, ImageButton imageButton6) {
        this.rootView = frameLayout;
        this.appWidgetBadge = textView;
        this.appWidgetClose = imageButton;
        this.appWidgetControls = linearLayout;
        this.appWidgetDragHandle = imageButton2;
        this.appWidgetDragHandleLauncher = imageButton3;
        this.appWidgetExpand = imageButton4;
        this.appWidgetLauncherControls = linearLayout2;
        this.appWidgetLauncherScroll = horizontalScrollView;
        this.appWidgetList = linearLayout3;
        this.appWidgetRoot = frameLayout2;
        this.appWidgetSwap = imageButton5;
        this.appWidgetSwapLauncher = imageButton6;
    }

    @Override // androidx.viewbinding.ViewBinding
    public FrameLayout getRoot() {
        return this.rootView;
    }

    public static TileEmbeddedAppBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, null, false);
    }

    public static TileEmbeddedAppBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        View viewInflate = layoutInflater.inflate(R.layout.tile_embedded_app, viewGroup, false);
        if (z) {
            viewGroup.addView(viewInflate);
        }
        return bind(viewInflate);
    }

    public static TileEmbeddedAppBinding bind(View view) {
        int i = R.id.appWidgetBadge;
        TextView textView = (TextView) ViewBindings.findChildViewById(view, i);
        if (textView != null) {
            i = R.id.appWidgetClose;
            ImageButton imageButton = (ImageButton) ViewBindings.findChildViewById(view, i);
            if (imageButton != null) {
                i = R.id.appWidgetControls;
                LinearLayout linearLayout = (LinearLayout) ViewBindings.findChildViewById(view, i);
                if (linearLayout != null) {
                    i = R.id.appWidgetDragHandle;
                    ImageButton imageButton2 = (ImageButton) ViewBindings.findChildViewById(view, i);
                    if (imageButton2 != null) {
                        i = R.id.appWidgetDragHandleLauncher;
                        ImageButton imageButton3 = (ImageButton) ViewBindings.findChildViewById(view, i);
                        if (imageButton3 != null) {
                            i = R.id.appWidgetExpand;
                            ImageButton imageButton4 = (ImageButton) ViewBindings.findChildViewById(view, i);
                            if (imageButton4 != null) {
                                i = R.id.appWidgetLauncherControls;
                                LinearLayout linearLayout2 = (LinearLayout) ViewBindings.findChildViewById(view, i);
                                if (linearLayout2 != null) {
                                    i = R.id.appWidgetLauncherScroll;
                                    HorizontalScrollView horizontalScrollView = (HorizontalScrollView) ViewBindings.findChildViewById(view, i);
                                    if (horizontalScrollView != null) {
                                        i = R.id.appWidgetList;
                                        LinearLayout linearLayout3 = (LinearLayout) ViewBindings.findChildViewById(view, i);
                                        if (linearLayout3 != null) {
                                            FrameLayout frameLayout = (FrameLayout) view;
                                            i = R.id.appWidgetSwap;
                                            ImageButton imageButton5 = (ImageButton) ViewBindings.findChildViewById(view, i);
                                            if (imageButton5 != null) {
                                                i = R.id.appWidgetSwapLauncher;
                                                ImageButton imageButton6 = (ImageButton) ViewBindings.findChildViewById(view, i);
                                                if (imageButton6 != null) {
                                                    return new TileEmbeddedAppBinding(frameLayout, textView, imageButton, linearLayout, imageButton2, imageButton3, imageButton4, linearLayout2, horizontalScrollView, linearLayout3, frameLayout, imageButton5, imageButton6);
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
