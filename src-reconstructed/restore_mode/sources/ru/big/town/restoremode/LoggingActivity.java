package ru.big.town.restoremode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import kotlinx.coroutines.DebugKt;

/* JADX INFO: loaded from: classes2.dex */
public class LoggingActivity extends AppCompatActivity {
    private static final String ACTION_LOGGING_SET = "ru.big.town.anative.LOGGING_SET";
    private static final String ACTION_LOGGING_SHARE = "ru.big.town.anative.LOGGING_SHARE";
    private static final String ACTION_LOG_UPDATE = "ru.big.town.anative.LOG_UPDATE";
    private static final String ACTION_REQUEST_LOG = "ru.big.town.anative.REQUEST_LOG";
    private static final String NATIVE_PKG = "ru.big.town.anative";
    private static final long POLL_MS = 1000;
    private static final String TAG = "$$$ LoggingActivity $$$";
    private SharedPreferences prefs;
    private ScrollView scrollLog;
    private Switch switchLogging;
    private TextView textLog;
    private TextView textLogPath;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private String lastContent = "";
    private boolean firstLoad = true;
    private final BroadcastReceiver logReceiver = new AnonymousClass1();
    private final Runnable poll = new Runnable() { // from class: ru.big.town.restoremode.LoggingActivity.2
        @Override // java.lang.Runnable
        public void run() {
            LoggingActivity.this.requestSnapshot();
            LoggingActivity.this.uiHandler.postDelayed(this, LoggingActivity.POLL_MS);
        }
    };

    /* JADX INFO: renamed from: ru.big.town.restoremode.LoggingActivity$1, reason: invalid class name */
    class AnonymousClass1 extends BroadcastReceiver {
        AnonymousClass1() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String stringExtra = intent.getStringExtra("log");
            String stringExtra2 = intent.getStringExtra("path");
            if (stringExtra2 != null) {
                LoggingActivity.this.textLogPath.setText("Файл: " + stringExtra2);
            }
            if (stringExtra == null || stringExtra.equals(LoggingActivity.this.lastContent)) {
                return;
            }
            final int iMax = Math.max(0, ((LoggingActivity.this.scrollLog.getChildCount() > 0 ? LoggingActivity.this.scrollLog.getChildAt(0).getHeight() : 0) - LoggingActivity.this.scrollLog.getScrollY()) - LoggingActivity.this.scrollLog.getHeight());
            final boolean z = LoggingActivity.this.firstLoad || iMax <= LoggingActivity.this.dp(12);
            LoggingActivity.this.lastContent = stringExtra;
            LoggingActivity.this.firstLoad = false;
            LoggingActivity.this.textLog.setText(stringExtra);
            LoggingActivity.this.scrollLog.post(new Runnable() { // from class: ru.big.town.restoremode.LoggingActivity$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1852lambda$onReceive$0$rubigtownrestoremodeLoggingActivity$1(z, iMax);
                }
            });
        }

        /* JADX INFO: renamed from: lambda$onReceive$0$ru-big-town-restoremode-LoggingActivity$1, reason: not valid java name */
        /* synthetic */ void m1852lambda$onReceive$0$rubigtownrestoremodeLoggingActivity$1(boolean z, int i) {
            if (z) {
                LoggingActivity.this.scrollLog.fullScroll(130);
            } else {
                LoggingActivity.this.scrollLog.scrollTo(0, Math.max(0, ((LoggingActivity.this.scrollLog.getChildCount() > 0 ? LoggingActivity.this.scrollLog.getChildAt(0).getHeight() : 0) - LoggingActivity.this.scrollLog.getHeight()) - i));
            }
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        EdgeToEdge.enable(this);
        setRequestedOrientation(0);
        setContentView(R.layout.activity_logging);
        this.prefs = getSharedPreferences("DrivePreferences", 0);
        this.switchLogging = (Switch) findViewById(R.id.switchLogging);
        this.textLog = (TextView) findViewById(R.id.textLog);
        this.textLogPath = (TextView) findViewById(R.id.textLogPath);
        this.scrollLog = (ScrollView) findViewById(R.id.scrollLog);
        this.switchLogging.setChecked(this.prefs.getBoolean("loggingEnabled", false));
        this.switchLogging.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.restoremode.LoggingActivity$$ExternalSyntheticLambda0
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.f$0.m1851lambda$onCreate$0$rubigtownrestoremodeLoggingActivity(compoundButton, z);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$onCreate$0$ru-big-town-restoremode-LoggingActivity, reason: not valid java name */
    /* synthetic */ void m1851lambda$onCreate$0$rubigtownrestoremodeLoggingActivity(CompoundButton compoundButton, boolean z) {
        this.prefs.edit().putBoolean("loggingEnabled", z).apply();
        Intent intent = new Intent(ACTION_LOGGING_SET).setPackage(NATIVE_PKG);
        intent.putExtra(DebugKt.DEBUG_PROPERTY_VALUE_ON, z);
        sendBroadcast(intent);
    }

    public void onButtonBackLogging(View view) {
        finish();
    }

    public void onButtonShareLog(View view) {
        sendBroadcast(new Intent(ACTION_LOGGING_SHARE).setPackage(NATIVE_PKG));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int dp(int i) {
        return Math.round(i * getResources().getDisplayMetrics().density);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestSnapshot() {
        Intent intent = new Intent(ACTION_REQUEST_LOG);
        intent.setPackage(NATIVE_PKG);
        sendBroadcast(intent);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        registerReceiver(this.logReceiver, new IntentFilter(ACTION_LOG_UPDATE), 2);
        this.uiHandler.removeCallbacks(this.poll);
        this.uiHandler.post(this.poll);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(this.logReceiver);
        } catch (Exception unused) {
        }
        this.uiHandler.removeCallbacks(this.poll);
    }
}
