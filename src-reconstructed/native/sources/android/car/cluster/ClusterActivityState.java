package android.car.cluster;

import android.graphics.Rect;
import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public class ClusterActivityState {
    private static final String KEY_EXTRAS = "android.car:activityState.extras";
    private static final String KEY_UNOBSCURED_BOUNDS = "android.car:activityState.unobscured";
    private static final String KEY_VISIBLE = "android.car:activityState.visible";
    private Bundle mExtras;
    private Rect mUnobscuredBounds;
    private boolean mVisible = true;

    private ClusterActivityState() {
    }

    public static ClusterActivityState create(boolean z, Rect rect) {
        return new ClusterActivityState().setVisible(z).setUnobscuredBounds(rect);
    }

    public static ClusterActivityState fromBundle(Bundle bundle) {
        return new ClusterActivityState().setVisible(bundle.getBoolean(KEY_VISIBLE, true)).setUnobscuredBounds((Rect) bundle.getParcelable(KEY_UNOBSCURED_BOUNDS)).setExtras(bundle.getBundle(KEY_EXTRAS));
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public Rect getUnobscuredBounds() {
        return this.mUnobscuredBounds;
    }

    public boolean isVisible() {
        return this.mVisible;
    }

    public ClusterActivityState setExtras(Bundle bundle) {
        this.mExtras = bundle;
        return this;
    }

    public ClusterActivityState setUnobscuredBounds(Rect rect) {
        this.mUnobscuredBounds = rect;
        return this;
    }

    public ClusterActivityState setVisible(boolean z) {
        this.mVisible = z;
        return this;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_VISIBLE, this.mVisible);
        bundle.putParcelable(KEY_UNOBSCURED_BOUNDS, this.mUnobscuredBounds);
        bundle.putBundle(KEY_EXTRAS, this.mExtras);
        return bundle;
    }

    public String toString() {
        return getClass().getSimpleName() + " {visible: " + this.mVisible + ", unobscuredBounds: " + this.mUnobscuredBounds + ", extras: " + this.mExtras + " }";
    }
}
