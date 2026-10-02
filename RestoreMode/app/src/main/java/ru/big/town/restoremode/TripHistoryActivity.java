package ru.big.town.restoremode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class TripHistoryActivity extends AppCompatActivity {
    private static final String ACTION_TRIP_DELETE = "ru.big.town.anative.TRIP_DELETE";
    private static final String TAG = "$$$ TripHistory $$$";
    private LinearLayout tripLogContainer;
    private final SimpleDateFormat dateFmt = new SimpleDateFormat("dd.MM  HH:mm", Locale.getDefault());
    private final BroadcastReceiver tripReceiver = new BroadcastReceiver() { // from class: ru.big.town.restoremode.TripHistoryActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            TripHistoryActivity.this.renderTripLog(intent.getStringExtra("tripsJson"));
        }
    };

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        EdgeToEdge.enable(this);
        setRequestedOrientation(0);
        setContentView(R.layout.activity_trip_history);
        applyWindowInsets();
        this.tripLogContainer = (LinearLayout) findViewById(R.id.tripLogContainer);
        renderTripLog(getIntent().getStringExtra("tripsJson"));
    }

    private void applyWindowInsets() {
        View viewFindViewById = findViewById(R.id.tripHistoryRoot);
        if (viewFindViewById == null) {
            return;
        }
        final int iRound = Math.round(getResources().getDisplayMetrics().density * 145.0f);
        final int paddingLeft = viewFindViewById.getPaddingLeft();
        final int paddingTop = viewFindViewById.getPaddingTop();
        final int paddingRight = viewFindViewById.getPaddingRight();
        final int paddingBottom = viewFindViewById.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(viewFindViewById, new OnApplyWindowInsetsListener() { // from class: ru.big.town.restoremode.TripHistoryActivity$$ExternalSyntheticLambda0
            @Override // androidx.core.view.OnApplyWindowInsetsListener
            public final WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                return TripHistoryActivity.this.m1932x58a9cd7d(paddingLeft, iRound, paddingTop, paddingRight, paddingBottom, view, windowInsetsCompat);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$applyWindowInsets$0$ru-big-town-restoremode-TripHistoryActivity, reason: not valid java name */
    /* synthetic */ WindowInsetsCompat m1932x58a9cd7d(int i, int i2, int i3, int i4, int i5, View view, WindowInsetsCompat windowInsetsCompat) {
        int identifier;
        Insets insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars());
        int dimensionPixelSize = insets.top;
        if (dimensionPixelSize == 0 && (identifier = getResources().getIdentifier("status_bar_height", "dimen", "android")) > 0) {
            dimensionPixelSize = getResources().getDimensionPixelSize(identifier);
        }
        view.setPadding(i + i2 + insets.left, i3 + dimensionPixelSize, i4 + insets.right, i5 + insets.bottom);
        return windowInsetsCompat;
    }

    public void onButtonBackHistory(View view) {
        finish();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        registerReceiver(this.tripReceiver, new IntentFilter("ru.big.town.anative.TRIP_UPDATE"), 2);
        Intent intent = new Intent("ru.big.town.anative.REQUEST_TRIP_UPDATE");
        intent.setPackage("ru.big.town.anative");
        sendBroadcast(intent);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(this.tripReceiver);
        } catch (Exception unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void renderTripLog(String str) {
        LinearLayout linearLayout = this.tripLogContainer;
        if (linearLayout == null) {
            return;
        }
        linearLayout.removeAllViews();
        try {
            if (str == null) {
                str = "[]";
            }
            JSONArray jSONArray = new JSONArray(str);
            if (jSONArray.length() == 0) {
                TextView textView = new TextView(this);
                textView.setText("Поездок пока нет");
                textView.setTextColor(-7829368);
                textView.setTextSize(0, 24.0f);
                textView.setPadding(8, 8, 8, 8);
                this.tripLogContainer.addView(textView);
                return;
            }
            LayoutInflater layoutInflaterFrom = LayoutInflater.from(this);
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject = jSONArray.getJSONObject(i);
                final long j = jSONObject.getLong("start");
                View viewInflate = layoutInflaterFrom.inflate(R.layout.item_trip, (ViewGroup) this.tripLogContainer, false);
                ((TextView) viewInflate.findViewById(R.id.tripDate)).setText(this.dateFmt.format(new Date(j)));
                ((TextView) viewInflate.findViewById(R.id.tripDuration)).setText(fmtDurationShort(jSONObject.getLong("durationMs")));
                ImageButton imageButton = (ImageButton) viewInflate.findViewById(R.id.tripDelete);
                if (imageButton != null) {
                    imageButton.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.restoremode.TripHistoryActivity$$ExternalSyntheticLambda1
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            TripHistoryActivity.this.m1934x39e8fd93(j, view);
                        }
                    });
                }
                this.tripLogContainer.addView(viewInflate);
            }
        } catch (Exception e) {
            Log.w(TAG, "renderTripLog: " + e.getMessage());
        }
    }

    /* JADX INFO: renamed from: lambda$renderTripLog$1$ru-big-town-restoremode-TripHistoryActivity, reason: not valid java name */
    /* synthetic */ void m1934x39e8fd93(long j, View view) {
        confirmDelete(j);
    }

    private void confirmDelete(final long j) {
        new MaterialAlertDialogBuilder(this, R.style.DarkDialog).setTitle((CharSequence) "Удалить поездку").setMessage((CharSequence) "Удалить эту поездку из истории? Действие необратимо.").setPositiveButton((CharSequence) "Удалить", new DialogInterface.OnClickListener() { // from class: ru.big.town.restoremode.TripHistoryActivity$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                TripHistoryActivity.this.m1933xe65112b6(j, dialogInterface, i);
            }
        }).setNegativeButton((CharSequence) "Отмена", (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: renamed from: lambda$confirmDelete$2$ru-big-town-restoremode-TripHistoryActivity, reason: not valid java name */
    /* synthetic */ void m1933xe65112b6(long j, DialogInterface dialogInterface, int i) {
        Intent intent = new Intent(ACTION_TRIP_DELETE);
        intent.setPackage("ru.big.town.anative");
        intent.putExtra("deleteStart", j);
        sendBroadcast(intent);
        Log.i(TAG, "TRIP_DELETE отправлен start=" + j);
    }

    private static String fmtDurationShort(long j) {
        long j2 = j / 1000;
        long j3 = j2 / 3600;
        long j4 = (j2 % 3600) / 60;
        return j3 > 0 ? String.format(Locale.US, "%d ч %02d мин", Long.valueOf(j3), Long.valueOf(j4)) : j4 + " мин";
    }
}
