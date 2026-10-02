package android.car.projection;

import android.annotation.SystemApi;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.os.Bundle;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public class ProjectionOptions {
    private static final String KEY_ACTIVITY_OPTIONS = "android.car.projection.activityOptions";
    private static final String KEY_CONSENT_ACTIVITY = "android.car.projection.consentActivity";
    private static final String KEY_PREFIX = "android.car.projection.";
    private static final String KEY_UI_MODE = "android.car.projection.systemUiFlags";
    public static final int UI_MODE_BLENDED = 1;
    private static final int UI_MODE_DEFAULT = 0;
    public static final int UI_MODE_FULL_SCREEN = 0;
    private final ActivityOptions mActivityOptions;
    private final ComponentName mConsentActivity;
    private final int mUiMode;

    public static class Builder {
        private ActivityOptions mActivityOptions;
        private ComponentName mConsentActivity;
        private int mUiMode = 0;

        public ProjectionOptions build() {
            return new ProjectionOptions(this);
        }

        public Builder setConsentActivity(ComponentName componentName) {
            this.mConsentActivity = componentName;
            return this;
        }

        public Builder setProjectionActivityOptions(ActivityOptions activityOptions) {
            this.mActivityOptions = activityOptions;
            return this;
        }

        public Builder setUiMode(int i) {
            this.mUiMode = i;
            return this;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ProjectionUiMode {
    }

    private ProjectionOptions(Builder builder) {
        this.mActivityOptions = builder.mActivityOptions;
        this.mUiMode = builder.mUiMode;
        this.mConsentActivity = builder.mConsentActivity;
    }

    public ProjectionOptions(Bundle bundle) {
        Bundle bundle2 = bundle.getBundle(KEY_ACTIVITY_OPTIONS);
        this.mActivityOptions = bundle2 != null ? new ActivityOptions(bundle2) : null;
        this.mUiMode = bundle.getInt(KEY_UI_MODE, 0);
        this.mConsentActivity = (ComponentName) bundle.getParcelable(KEY_CONSENT_ACTIVITY);
    }

    public static Builder builder() {
        return new Builder();
    }

    public ActivityOptions getActivityOptions() {
        return this.mActivityOptions;
    }

    public ComponentName getConsentActivity() {
        return this.mConsentActivity;
    }

    public int getUiMode() {
        return this.mUiMode;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        ActivityOptions activityOptions = this.mActivityOptions;
        if (activityOptions != null) {
            bundle.putBundle(KEY_ACTIVITY_OPTIONS, activityOptions.toBundle());
        }
        bundle.putParcelable(KEY_CONSENT_ACTIVITY, this.mConsentActivity);
        int i = this.mUiMode;
        if (i != 0) {
            bundle.putInt(KEY_UI_MODE, i);
        }
        return bundle;
    }

    public String toString() {
        return toBundle().toString();
    }
}
