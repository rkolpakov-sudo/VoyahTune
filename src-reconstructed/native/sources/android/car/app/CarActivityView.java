package android.car.app;

import android.app.ActivityView;
import android.car.Car;
import android.car.drivingstate.CarUxRestrictionsManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public final class CarActivityView extends ActivityView {
    private static final String TAG = "CarActivityView";
    private Car mCar;
    private volatile ActivityView.StateCallback mUserActivityViewCallback;
    private CarUxRestrictionsManager mUxRestrictionsManager;
    private int mVirtualDisplayId;

    private class CarActivityViewCallback extends ActivityView.StateCallback {
        final CarActivityView this$0;

        private CarActivityViewCallback(CarActivityView carActivityView) {
            this.this$0 = carActivityView;
        }

        public void onActivityViewDestroyed(ActivityView activityView) {
            int i = this.this$0.mVirtualDisplayId;
            this.this$0.mVirtualDisplayId = -1;
            CarActivityView.reportPhysicalDisplayId(this.this$0.mUxRestrictionsManager, i, -1);
            ActivityView.StateCallback stateCallback = this.this$0.mUserActivityViewCallback;
            if (stateCallback != null) {
                stateCallback.onActivityViewDestroyed(activityView);
            }
        }

        public void onActivityViewReady(ActivityView activityView) {
            CarActivityView carActivityView = this.this$0;
            carActivityView.mVirtualDisplayId = carActivityView.getVirtualDisplayId();
            CarActivityView.reportPhysicalDisplayId(this.this$0.mUxRestrictionsManager, this.this$0.mVirtualDisplayId, this.this$0.mContext.getDisplayId());
            ActivityView.StateCallback stateCallback = this.this$0.mUserActivityViewCallback;
            if (stateCallback != null) {
                stateCallback.onActivityViewReady(activityView);
            }
        }

        public void onTaskCreated(int i, ComponentName componentName) {
            ActivityView.StateCallback stateCallback = this.this$0.mUserActivityViewCallback;
            if (stateCallback != null) {
                stateCallback.onTaskCreated(i, componentName);
            }
        }

        public void onTaskMovedToFront(int i) {
            ActivityView.StateCallback stateCallback = this.this$0.mUserActivityViewCallback;
            if (stateCallback != null) {
                stateCallback.onTaskMovedToFront(i);
            }
        }

        public void onTaskRemovalStarted(int i) {
            ActivityView.StateCallback stateCallback = this.this$0.mUserActivityViewCallback;
            if (stateCallback != null) {
                stateCallback.onTaskRemovalStarted(i);
            }
        }
    }

    public CarActivityView(Context context) {
        this(context, null);
    }

    public CarActivityView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CarActivityView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, false);
    }

    public CarActivityView(Context context, AttributeSet attributeSet, int i, boolean z) {
        super(context, attributeSet, i, z, true);
        this.mVirtualDisplayId = -1;
        super.setCallback(new CarActivityViewCallback());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void reportPhysicalDisplayId(CarUxRestrictionsManager carUxRestrictionsManager, int i, int i2) {
        String str;
        String str2 = TAG;
        Log.d(str2, "reportPhysicalDisplayId: virtualDisplayId=" + i + ", physicalDisplayId=" + i2);
        if (i == -1) {
            str = "No virtual display to report";
        } else {
            if (carUxRestrictionsManager != null) {
                carUxRestrictionsManager.reportVirtualDisplayToPhysicalDisplay(i, i2);
                return;
            }
            str = "CarUxRestrictionsManager is not ready yet";
        }
        Log.w(str2, str);
    }

    public /* synthetic */ void lambda$onAttachedToWindow$0$CarActivityView(Car car, boolean z) {
        if (z) {
            CarUxRestrictionsManager carUxRestrictionsManager = (CarUxRestrictionsManager) car.getCarManager(Car.CAR_UX_RESTRICTION_SERVICE);
            this.mUxRestrictionsManager = carUxRestrictionsManager;
            int i = this.mVirtualDisplayId;
            if (i != -1) {
                reportPhysicalDisplayId(carUxRestrictionsManager, i, this.mContext.getDisplayId());
            }
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mCar = Car.createCar(this.mContext, null, 0L, new Car.CarServiceLifecycleListener(this) { // from class: android.car.app._$$Lambda$CarActivityView$MxqQl3q8OCsOuebHhBjc2WecV6g
            public final CarActivityView f$0;

            {
                this.f$0 = this;
            }

            @Override // android.car.Car.CarServiceLifecycleListener
            public final void onLifecycleChanged(Car car, boolean z) {
                this.f$0.lambda$onAttachedToWindow$0$CarActivityView(car, z);
            }
        });
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Car car = this.mCar;
        if (car != null) {
            car.disconnect();
        }
    }

    public void setCallback(ActivityView.StateCallback stateCallback) {
        this.mUserActivityViewCallback = stateCallback;
        if (getVirtualDisplayId() == -1 || stateCallback == null) {
            return;
        }
        stateCallback.onActivityViewReady(this);
    }
}
