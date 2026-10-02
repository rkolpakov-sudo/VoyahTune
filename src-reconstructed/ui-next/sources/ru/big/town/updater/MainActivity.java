package ru.big.town.updater;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Insets;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public final class MainActivity extends Activity {
    private static final String OPEN_INITIAL_SCREEN = "ru.big.town.updater.OPEN_INITIAL_SCREEN";
    private boolean actionBusy;
    private boolean connected;
    private boolean dnsSupported;
    private String hiddenResultKey;
    private String hiddenSettingsError;
    private boolean hideCompletedResult;
    private boolean hideConnectionError;
    private AlertDialog noticeDialog;
    private String noticeShowing;
    private boolean pendingErrorFinish;
    private boolean polling;
    private UpdatePresentation presentation;
    private Button primary;
    private ProgressBar progress;
    private boolean resetCompletedOnOpen;
    private boolean resumed;
    private Button secondary;
    private AlertDialog settingsDialog;
    private TextView settingsMessage;
    private Button settingsRepeat;
    private Button settingsSave;
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private final RootClient client = new RootClient();
    private final Handler poll = new Handler(Looper.getMainLooper());
    private String connectionError = "";
    private String commandError = "";
    private JSONObject state = new JSONObject();
    private JSONObject settings = new JSONObject();
    private final Runnable tick = new Runnable() { // from class: ru.big.town.updater.MainActivity.1
        @Override // java.lang.Runnable
        public void run() {
            if (MainActivity.this.resumed) {
                if (!MainActivity.this.polling && !MainActivity.this.actionBusy) {
                    MainActivity.this.refresh();
                }
                MainActivity.this.poll.postDelayed(this, 2000L);
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    interface Result {
        void apply(JSONObject jSONObject) throws Exception;
    }

    static /* synthetic */ void lambda$perform$12(JSONObject jSONObject) throws Exception {
    }

    static /* synthetic */ void lambda$refresh$5(JSONObject jSONObject) throws Exception {
    }

    static /* synthetic */ void lambda$sendFinish$13(JSONObject jSONObject) throws Exception {
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.resetCompletedOnOpen = getIntent().getBooleanExtra(OPEN_INITIAL_SCREEN, false);
        setContentView(R.layout.activity_updater);
        applyWindowInsets(findViewById(R.id.root));
        this.progress = (ProgressBar) findViewById(R.id.progress);
        this.primary = (Button) findViewById(R.id.primary);
        this.secondary = (Button) findViewById(R.id.secondary);
        this.primary.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda23
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m9lambda$onCreate$0$rubigtownupdaterMainActivity(view);
            }
        });
        this.secondary.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda24
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m10lambda$onCreate$1$rubigtownupdaterMainActivity(view);
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda25
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m11lambda$onCreate$2$rubigtownupdaterMainActivity(view);
            }
        });
        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda26
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m12lambda$onCreate$3$rubigtownupdaterMainActivity(view);
            }
        });
        render();
    }

    /* JADX INFO: renamed from: lambda$onCreate$0$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m9lambda$onCreate$0$rubigtownupdaterMainActivity(View view) {
        primaryAction();
    }

    /* JADX INFO: renamed from: lambda$onCreate$1$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m10lambda$onCreate$1$rubigtownupdaterMainActivity(View view) {
        perform("check", false);
    }

    /* JADX INFO: renamed from: lambda$onCreate$2$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m11lambda$onCreate$2$rubigtownupdaterMainActivity(View view) {
        finish();
    }

    /* JADX INFO: renamed from: lambda$onCreate$3$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m12lambda$onCreate$3$rubigtownupdaterMainActivity(View view) {
        openSettings();
    }

    private void applyWindowInsets(View view) {
        final int iDp = dp(145);
        int identifier = getResources().getIdentifier("status_bar_height", "dimen", "android");
        final int dimensionPixelSize = identifier > 0 ? getResources().getDimensionPixelSize(identifier) : 0;
        view.setPadding(iDp, dimensionPixelSize, 0, 0);
        view.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnApplyWindowInsetsListener
            public final WindowInsets onApplyWindowInsets(View view2, WindowInsets windowInsets) {
                return MainActivity.lambda$applyWindowInsets$4(iDp, dimensionPixelSize, view2, windowInsets);
            }
        });
        view.requestApplyInsets();
    }

    static /* synthetic */ WindowInsets lambda$applyWindowInsets$4(int i, int i2, View view, WindowInsets windowInsets) {
        Insets insets = windowInsets.getInsets(WindowInsets.Type.systemBars());
        int i3 = i + insets.left;
        if (insets.top > 0) {
            i2 = insets.top;
        }
        if (view.getPaddingLeft() == i3 && view.getPaddingTop() == i2 && view.getPaddingRight() == insets.right && view.getPaddingBottom() == insets.bottom) {
            return windowInsets;
        }
        view.setPadding(i3, i2, insets.right, insets.bottom);
        return windowInsets;
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.resetCompletedOnOpen = intent.getBooleanExtra(OPEN_INITIAL_SCREEN, false);
        this.hideCompletedResult = false;
        this.hiddenResultKey = null;
        this.hideConnectionError = false;
        if (this.polling || this.actionBusy) {
            return;
        }
        refresh();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        this.resumed = true;
        this.poll.removeCallbacks(this.tick);
        this.poll.post(this.tick);
    }

    @Override // android.app.Activity
    protected void onPause() {
        this.resumed = false;
        this.poll.removeCallbacks(this.tick);
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        this.poll.removeCallbacks(this.tick);
        AlertDialog alertDialog = this.settingsDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        AlertDialog alertDialog2 = this.noticeDialog;
        if (alertDialog2 != null) {
            alertDialog2.dismiss();
        }
        this.worker.shutdown();
        super.onDestroy();
    }

    private boolean alive() {
        return (isDestroyed() || isFinishing()) ? false : true;
    }

    private JSONObject request(String str) throws Exception {
        return new JSONObject().put("command", str);
    }

    private void accept(JSONObject jSONObject) throws Exception {
        JSONObject jSONObject2 = jSONObject.getJSONObject("state");
        this.state = jSONObject2;
        if (!"committed".equals(jSONObject2.optString("phase"))) {
            this.hideCompletedResult = false;
        }
        String str = this.hiddenResultKey;
        if (str != null && (!str.equals(resultKey()) || UpdatePresentation.isBusy(this.state.optString("phase")))) {
            this.hiddenResultKey = null;
        }
        JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("settings");
        if (jSONObjectOptJSONObject != null) {
            this.settings = jSONObjectOptJSONObject;
        }
        JSONArray jSONArrayOptJSONArray = jSONObject.optJSONArray("capabilities");
        this.dnsSupported = false;
        if (jSONArrayOptJSONArray != null) {
            for (int i = 0; i < jSONArrayOptJSONArray.length(); i++) {
                if ("dns-settings".equals(jSONArrayOptJSONArray.optString(i))) {
                    this.dnsSupported = true;
                }
            }
        }
        this.connected = true;
        this.connectionError = "";
        this.hideConnectionError = false;
        String strOptString = jSONObject.optString("settingsError", "");
        if (!jSONObject.isNull("settingsError") && !strOptString.equals(this.hiddenSettingsError)) {
            this.commandError = strOptString;
        }
        render();
        if (this.pendingErrorFinish && !this.actionBusy) {
            this.pendingErrorFinish = false;
            sendFinish();
        } else if (this.resumed && this.settingsDialog == null && !this.actionBusy) {
            showNotice();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refresh() {
        if (this.polling || this.worker.isShutdown()) {
            return;
        }
        if (this.resetCompletedOnOpen && !this.actionBusy) {
            this.resetCompletedOnOpen = false;
            this.connected = false;
            this.connectionError = "";
            this.state = new JSONObject();
            try {
                command(request("finish"), new Result() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda8
                    @Override // ru.big.town.updater.MainActivity.Result
                    public final void apply(JSONObject jSONObject) throws Exception {
                        MainActivity.lambda$refresh$5(jSONObject);
                    }
                });
                return;
            } catch (Exception e) {
                this.commandError = reason(e);
                render();
                return;
            }
        }
        this.polling = true;
        this.worker.execute(new Runnable() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m21lambda$refresh$8$rubigtownupdaterMainActivity();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$refresh$8$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m21lambda$refresh$8$rubigtownupdaterMainActivity() {
        try {
            final JSONObject jSONObjectCall = this.client.call(request("status"));
            runOnUiThread(new Runnable() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m19lambda$refresh$6$rubigtownupdaterMainActivity(jSONObjectCall);
                }
            });
        } catch (Exception e) {
            runOnUiThread(new Runnable() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m20lambda$refresh$7$rubigtownupdaterMainActivity(e);
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$refresh$6$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m19lambda$refresh$6$rubigtownupdaterMainActivity(JSONObject jSONObject) {
        this.polling = false;
        if (alive()) {
            try {
                accept(jSONObject);
            } catch (Exception e) {
                disconnected(e);
            }
        }
    }

    /* JADX INFO: renamed from: lambda$refresh$7$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m20lambda$refresh$7$rubigtownupdaterMainActivity(Exception exc) {
        this.polling = false;
        if (alive()) {
            disconnected(exc);
        }
    }

    private void disconnected(Exception exc) {
        this.connected = false;
        this.connectionError = reason(exc);
        render();
    }

    private void command(final JSONObject jSONObject, final Result result) {
        if (this.actionBusy || this.worker.isShutdown()) {
            return;
        }
        this.actionBusy = true;
        this.commandError = "";
        render();
        this.worker.execute(new Runnable() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m6lambda$command$11$rubigtownupdaterMainActivity(jSONObject, result);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$command$11$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m6lambda$command$11$rubigtownupdaterMainActivity(final JSONObject jSONObject, final Result result) {
        final Exception exc;
        JSONObject jSONObject2;
        boolean z;
        try {
            try {
                try {
                    jSONObject2 = this.client.call(jSONObject);
                    z = false;
                } catch (Exception e) {
                    e = e;
                    exc = e;
                    runOnUiThread(new Runnable() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda7
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.m5lambda$command$10$rubigtownupdaterMainActivity(exc);
                        }
                    });
                    return;
                }
            } catch (UnsupportedOperationException e2) {
                if (!"finish".equals(jSONObject.optString("command"))) {
                    throw e2;
                }
                jSONObject2 = new JSONObject();
                z = true;
            } catch (Exception e3) {
                exc = e3;
                runOnUiThread(new Runnable() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda7
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m5lambda$command$10$rubigtownupdaterMainActivity(exc);
                    }
                });
                return;
            }
            final JSONObject jSONObject3 = jSONObject2;
            final boolean z2 = z;
            final JSONObject jSONObjectCall = this.client.call(request("status"));
            runOnUiThread(new Runnable() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m7lambda$command$9$rubigtownupdaterMainActivity(z2, jSONObjectCall, jSONObject, result, jSONObject3);
                }
            });
        } catch (Exception e4) {
            e = e4;
        }
    }

    /* JADX INFO: renamed from: lambda$command$9$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m7lambda$command$9$rubigtownupdaterMainActivity(boolean z, JSONObject jSONObject, JSONObject jSONObject2, Result result, JSONObject jSONObject3) {
        this.actionBusy = false;
        if (alive()) {
            this.hideCompletedResult = this.hideCompletedResult || z;
            try {
                accept(jSONObject);
                if ("finish".equals(jSONObject2.optString("command")) && jSONObject2.optBoolean("reset_errors")) {
                    resetResultScreen();
                }
                result.apply(jSONObject3);
            } catch (Exception e) {
                this.commandError = reason(e);
            }
            render();
            if (this.polling) {
                return;
            }
            refresh();
        }
    }

    /* JADX INFO: renamed from: lambda$command$10$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m5lambda$command$10$rubigtownupdaterMainActivity(Exception exc) {
        TextView textView;
        this.actionBusy = false;
        if (alive()) {
            String strReason = reason(exc);
            this.commandError = strReason;
            if (this.settingsDialog != null && (textView = this.settingsMessage) != null) {
                textView.setText(strReason);
            }
            render();
            if (this.polling) {
                return;
            }
            refresh();
        }
    }

    private void perform(String str, boolean z) {
        beginAction();
        try {
            JSONObject jSONObjectRequest = request(str);
            if ("check".equals(str)) {
                jSONObjectRequest.put("same_version", z);
            }
            command(jSONObjectRequest, new Result() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda13
                @Override // ru.big.town.updater.MainActivity.Result
                public final void apply(JSONObject jSONObject) throws Exception {
                    MainActivity.lambda$perform$12(jSONObject);
                }
            });
        } catch (Exception e) {
            this.commandError = reason(e);
            render();
        }
    }

    private String resultKey() {
        return this.state.optString("phase") + "/" + this.state.optString("error") + "/" + this.state.optString("installedVersion") + "/" + this.state.optLong("sourceGeneration") + "/" + this.state.optLong("lastAutoWall") + "/" + this.state.optJSONObject("selected");
    }

    private void resetResultScreen() {
        this.hiddenResultKey = UpdatePresentation.isBusy(this.state.optString("phase")) ? null : resultKey();
        if (!this.commandError.isEmpty()) {
            this.hiddenSettingsError = this.commandError;
        }
        this.commandError = "";
        this.connectionError = "";
        this.hideConnectionError = !this.connected;
        this.hideCompletedResult = false;
        this.noticeShowing = this.state.optString("notice");
        AlertDialog alertDialog = this.settingsDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        AlertDialog alertDialog2 = this.noticeDialog;
        if (alertDialog2 != null) {
            alertDialog2.dismiss();
        }
        render();
    }

    private void sendFinish() {
        try {
            command(request("finish").put("reset_errors", true), new Result() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda1
                @Override // ru.big.town.updater.MainActivity.Result
                public final void apply(JSONObject jSONObject) throws Exception {
                    MainActivity.lambda$sendFinish$13(jSONObject);
                }
            });
        } catch (Exception e) {
            this.commandError = reason(e);
            render();
        }
    }

    private void finishResult() {
        this.resetCompletedOnOpen = false;
        boolean z = this.connected;
        resetResultScreen();
        if (!z) {
            this.pendingErrorFinish = true;
        } else {
            sendFinish();
        }
    }

    private void beginAction() {
        this.hiddenResultKey = null;
        this.hiddenSettingsError = null;
        this.hideConnectionError = false;
        this.hideCompletedResult = false;
    }

    private void primaryAction() {
        UpdatePresentation updatePresentation = this.presentation;
        if (updatePresentation != null && "finish".equals(updatePresentation.command)) {
            finishResult();
        }
        beginAction();
        if (!this.connected) {
            refresh();
            return;
        }
        UpdatePresentation updatePresentation2 = this.presentation;
        if (updatePresentation2 == null) {
            return;
        }
        String str = updatePresentation2.command;
        str.hashCode();
        switch (str) {
            case "apply":
                confirmInstall();
                break;
            case "check":
                perform("check", false);
                break;
            case "download":
                perform("download", false);
                break;
        }
    }

    private void confirmInstall() {
        String str;
        if (!this.dnsSupported || this.settings.isNull("dnsEnabled")) {
            str = "";
        } else {
            str = "\n\nDNS: " + (this.settings.optBoolean("dnsEnabled") ? "Яндекс" : "стандартный") + ".";
        }
        new AlertDialog.Builder(this).setTitle("Установить обновление?").setMessage("Автомобиль должен стоять в P с включённым питанием. Головное устройство перезагрузится. Сохраняйте питание до завершения проверки запуска.\n\nНастройки VoyahTune сохранятся. При ошибке установите релиз через USB с компьютера." + str).setNegativeButton("Отмена", (DialogInterface.OnClickListener) null).setPositiveButton("Установить и перезагрузить", new DialogInterface.OnClickListener() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda12
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                this.f$0.m8lambda$confirmInstall$14$rubigtownupdaterMainActivity(dialogInterface, i);
            }
        }).show();
    }

    /* JADX INFO: renamed from: lambda$confirmInstall$14$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m8lambda$confirmInstall$14$rubigtownupdaterMainActivity(DialogInterface dialogInterface, int i) {
        perform("apply", false);
    }

    private void render() {
        String str;
        String str2;
        String str3;
        long j;
        boolean z;
        String str4;
        String str5;
        String str6;
        String str7;
        int i;
        String str8;
        String str9;
        String strOptString = this.state.optString("phase", "idle");
        String strOptString2 = this.state.optString("step");
        boolean z2 = !this.connected && this.hideConnectionError;
        boolean z3 = z2 || !((str9 = this.hiddenResultKey) == null || !str9.equals(resultKey()) || UpdatePresentation.isBusy(strOptString));
        String str10 = z2 ? "idle" : strOptString;
        String strMenuPhase = UpdatePresentation.menuPhase(str10, this.hideCompletedResult || z3);
        boolean zEquals = strMenuPhase.equals(str10);
        if (!zEquals || z3) {
            strOptString2 = "Готово к проверке обновлений";
            str = strMenuPhase;
        } else {
            str = str10;
        }
        String str11 = strOptString2;
        JSONObject jSONObjectOptJSONObject = (!zEquals || z3) ? null : this.state.optJSONObject("selected");
        JSONObject jSONObjectOptJSONObject2 = jSONObjectOptJSONObject != null ? jSONObjectOptJSONObject.optJSONObject("payload") : null;
        UpdatePresentation updatePresentationFrom = UpdatePresentation.from(str, jSONObjectOptJSONObject != null, str11, this.state.optLong("bytes"), this.state.optLong("total"), this.state.optLong("completedSteps"), this.state.optLong("totalSteps"));
        this.presentation = updatePresentationFrom;
        String strOptString3 = this.state.optString("installedVersion", "—");
        boolean z4 = "repair-required".equals(str) || "failed".equals(str);
        String strOptString4 = (z3 || this.state.isNull("error")) ? this.commandError : this.state.optString("error");
        String str12 = "";
        if (!this.connected) {
            strOptString4 = this.hideConnectionError ? "" : this.connectionError;
        }
        updatePresentationFrom.offerFinish(z4 || !strOptString4.isEmpty());
        int i2 = R.id.service;
        if (this.connected) {
            str2 = "●  Служба доступна";
        } else {
            str2 = this.connectionError.isEmpty() ? "Подключение…" : "●  Нет связи со службой";
        }
        text(i2, str2);
        text(R.id.installed, "Установлено: " + strOptString3);
        text(R.id.eyebrow, !this.connected ? "СЛУЖБА ОБНОВЛЕНИЙ" : updatePresentationFrom.eyebrow);
        int i3 = R.id.title;
        if (this.connected || z2) {
            str3 = updatePresentationFrom.title;
        } else {
            str3 = this.connectionError.isEmpty() ? "Подключаемся к службе" : "Служба обновления недоступна";
        }
        text(i3, str3);
        text(R.id.subtitle, (this.connected || z2) ? updatePresentationFrom.subtitle : "Если установка уже шла, её результат пока неизвестен.");
        int i4 = R.id.release;
        StringBuilder sb = new StringBuilder("VoyahTune ");
        if (jSONObjectOptJSONObject != null) {
            strOptString3 = jSONObjectOptJSONObject.optString("version");
        }
        text(i4, sb.append(strOptString3).toString());
        int i5 = R.id.meta;
        if (jSONObjectOptJSONObject == null) {
            str4 = "Установленная версия";
            z = true;
            j = 1048576;
        } else {
            j = 1048576;
            z = true;
            str4 = "Релиз " + jSONObjectOptJSONObject.optString("version") + (jSONObjectOptJSONObject2 == null ? "" : " · " + (jSONObjectOptJSONObject2.optLong("size") / 1048576) + " МБ") + (this.state.optBoolean("sameVersion") ? " · Повторная установка" : "");
        }
        text(i5, str4);
        text(R.id.badge, !this.connected ? "Нет связи" : updatePresentationFrom.badge);
        text(R.id.detail, strOptString4);
        visible(R.id.detail, !strOptString4.isEmpty());
        ((TextView) findViewById(R.id.detail)).setTextColor(getColor(R.color.error));
        visible(R.id.meter, (this.connected && updatePresentationFrom.meter) ? z : false);
        if (this.progress.isIndeterminate() != updatePresentationFrom.indeterminate) {
            this.progress.setIndeterminate(updatePresentationFrom.indeterminate);
        }
        if (!updatePresentationFrom.indeterminate && this.progress.getProgress() != updatePresentationFrom.percent) {
            this.progress.setProgress(updatePresentationFrom.percent, z);
        }
        text(R.id.progress_label, updatePresentationFrom.progressLabel);
        text(R.id.progress_note, updatePresentationFrom.progressNote);
        text(R.id.percent, updatePresentationFrom.percent + "%");
        visible(R.id.percent, !updatePresentationFrom.indeterminate);
        String[] strArr = {"Новый релиз", "Скачивание", "Установка", "Готово"};
        int[] iArr = {R.id.nav0, R.id.nav1, R.id.nav2, R.id.nav3};
        int i6 = 0;
        int i7 = 4;
        while (i6 < i7) {
            boolean z5 = this.connected && (i6 < updatePresentationFrom.nav || updatePresentationFrom.success);
            int i8 = iArr[i6];
            int[] iArr2 = iArr;
            StringBuilder sb2 = new StringBuilder();
            if (z5) {
                str8 = "✓";
                i = 4;
            } else {
                i = 4;
                str8 = new String[]{"①", "②", "③", "④"}[i6];
            }
            text(i8, sb2.append(str8).append("  ").append(strArr[i6]).toString());
            TextView textView = (TextView) findViewById(iArr2[i6]);
            int color = getColor((this.connected && (z5 || i6 == updatePresentationFrom.nav)) ? R.color.teal : R.color.muted);
            if (textView.getCurrentTextColor() != color) {
                textView.setTextColor(color);
            }
            i6++;
            i7 = i;
            strOptString4 = strOptString4;
            iArr = iArr2;
            strArr = strArr;
        }
        String str13 = strOptString4;
        boolean z6 = updatePresentationFrom.busy && updatePresentationFrom.nav == 2;
        int i9 = R.id.aside_title;
        if (z4 || !str13.isEmpty()) {
            str5 = "Установите через USB";
        } else if (z6) {
            str5 = "Сохраняйте питание";
        } else {
            str5 = updatePresentationFrom.success ? "Всё на месте" : "Настройки останутся с вами";
        }
        text(i9, str5);
        int i10 = R.id.aside_text;
        if (z4 || !str13.isEmpty()) {
            str6 = "Установите релиз через USB с компьютера. Причина ошибки показана на экране.";
        } else if (z6) {
            str6 = "Оставьте автомобиль в P. Не выключайте головное устройство до завершения установки и проверки запуска.";
        } else {
            str6 = updatePresentationFrom.success ? "Настройки VoyahTune сохранены. Можно вернуться к привычным функциям приложения." : "Обновление сохраняет настройки VoyahTune.\nПеред установкой переведите автомобиль в P и сохраняйте питание до завершения.";
        }
        text(i10, str6);
        int i11 = R.id.footer_note;
        if (z4) {
            str7 = "Для восстановления потребуется USB и компьютер.";
        } else if (z6) {
            str7 = "Обновление выполняется автономно. Не отключайте питание.";
        } else {
            str7 = updatePresentationFrom.success ? "Установка и проверка запуска завершены." : "Проверка новых версий — автоматически, не чаще одного раза в 24 часа.";
        }
        text(i11, str7);
        int i12 = R.id.primary;
        StringBuilder sbAppend = new StringBuilder().append(updatePresentationFrom.primary);
        if ("download".equals(updatePresentationFrom.command) && jSONObjectOptJSONObject2 != null) {
            str12 = " · " + (jSONObjectOptJSONObject2.optLong("size") / j) + " МБ";
        }
        text(i12, sbAppend.append(str12).toString());
        enabled(this.primary, (this.actionBusy || (!"finish".equals(updatePresentationFrom.command) && this.connected && updatePresentationFrom.busy)) ? false : true);
        visible(R.id.secondary, this.connected && updatePresentationFrom.secondary);
        enabled(this.secondary, (this.actionBusy || updatePresentationFrom.busy) ? false : true);
        if (this.settingsDialog != null) {
            Button button = this.settingsSave;
            if (button != null) {
                enabled(button, (this.actionBusy || updatePresentationFrom.busy) ? false : true);
            }
            Button button2 = this.settingsRepeat;
            if (button2 != null) {
                enabled(button2, (this.actionBusy || updatePresentationFrom.busy) ? false : true);
            }
        }
        enabled(findViewById(R.id.settings), (!this.connected || updatePresentationFrom.busy || this.actionBusy || "repair-required".equals(str)) ? false : true);
    }

    private void openSettings() {
        if (this.settingsDialog != null) {
            return;
        }
        beginAction();
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(dp(24), dp(12), dp(24), 0);
        label(linearLayout, "Адрес каталога релизов", 18);
        final EditText editText = new EditText(this);
        editText.setSingleLine(true);
        editText.setText(this.settings.optString("catalogUrl"));
        editText.setInputType(17);
        linearLayout.addView(editText);
        label(linearLayout, "Смена адреса не запускает скачивание или установку.", 15);
        final Switch r10 = new Switch(this);
        r10.setText("Яндекс DNS");
        r10.setTextSize(20.0f);
        r10.setPadding(0, dp(18), 0, dp(18));
        r10.setMinHeight(dp(60));
        linearLayout.addView(r10);
        r10.setEnabled(false);
        final TextView textViewLabel = label(linearLayout, this.dnsSupported ? "Определяем текущий DNS…" : "Настройка DNS доступна после обновления root-службы через USB.", 16);
        final boolean[] zArr = {false};
        r10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda0
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                MainActivity.lambda$openSettings$15(zArr, textViewLabel, compoundButton, z);
            }
        });
        Button button = new Button(this);
        button.setAllCaps(false);
        button.setText("Проверить релиз для повторной установки");
        linearLayout.addView(button);
        label(linearLayout, "Позволяет скачать и установить ту же версию VoyahTune.", 15);
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(linearLayout);
        final AlertDialog alertDialogCreate = new AlertDialog.Builder(this).setTitle("Настройки обновлений").setView(scrollView).setNegativeButton("Отмена", (DialogInterface.OnClickListener) null).setNeutralButton("Завершить", new DialogInterface.OnClickListener() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda11
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                this.f$0.m13lambda$openSettings$16$rubigtownupdaterMainActivity(dialogInterface, i);
            }
        }).setPositiveButton("Сохранить", (DialogInterface.OnClickListener) null).create();
        this.settingsDialog = alertDialogCreate;
        this.settingsRepeat = button;
        this.settingsMessage = textViewLabel;
        alertDialogCreate.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda19
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                this.f$0.m14lambda$openSettings$17$rubigtownupdaterMainActivity(alertDialogCreate, dialogInterface);
            }
        });
        alertDialogCreate.show();
        this.settingsSave = alertDialogCreate.getButton(-1);
        button.setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda20
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m15lambda$openSettings$18$rubigtownupdaterMainActivity(alertDialogCreate, view);
            }
        });
        final MainActivity mainActivity = this;
        alertDialogCreate.getButton(-1).setOnClickListener(new View.OnClickListener() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda21
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m17lambda$openSettings$20$rubigtownupdaterMainActivity(editText, zArr, r10, alertDialogCreate, textViewLabel, view);
            }
        });
        if (!mainActivity.dnsSupported) {
            return;
        }
        try {
            JSONObject jSONObjectRequest = mainActivity.request("get_settings");
            try {
                try {
                    Result result = new Result() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda22
                        @Override // ru.big.town.updater.MainActivity.Result
                        public final void apply(JSONObject jSONObject) throws Exception {
                            this.f$0.m18lambda$openSettings$21$rubigtownupdaterMainActivity(alertDialogCreate, zArr, r10, textViewLabel, jSONObject);
                        }
                    };
                    mainActivity = mainActivity;
                    textViewLabel = textViewLabel;
                    mainActivity.command(jSONObjectRequest, result);
                } catch (Exception e) {
                    e = e;
                    mainActivity = mainActivity;
                    textViewLabel = textViewLabel;
                    textViewLabel.setText(mainActivity.reason(e));
                }
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Exception e3) {
            e = e3;
        }
    }

    static /* synthetic */ void lambda$openSettings$15(boolean[] zArr, TextView textView, CompoundButton compoundButton, boolean z) {
        if (zArr[0]) {
            textView.setText(z ? "При установке релиза будет включён Яндекс DNS." : "При установке релиза будет использован стандартный DNS.");
        }
    }

    /* JADX INFO: renamed from: lambda$openSettings$16$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m13lambda$openSettings$16$rubigtownupdaterMainActivity(DialogInterface dialogInterface, int i) {
        finishResult();
    }

    /* JADX INFO: renamed from: lambda$openSettings$17$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m14lambda$openSettings$17$rubigtownupdaterMainActivity(AlertDialog alertDialog, DialogInterface dialogInterface) {
        if (this.settingsDialog == alertDialog) {
            this.settingsDialog = null;
            this.settingsSave = null;
            this.settingsRepeat = null;
            this.settingsMessage = null;
        }
    }

    /* JADX INFO: renamed from: lambda$openSettings$18$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m15lambda$openSettings$18$rubigtownupdaterMainActivity(AlertDialog alertDialog, View view) {
        alertDialog.dismiss();
        perform("check", true);
    }

    /* JADX INFO: renamed from: lambda$openSettings$20$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m17lambda$openSettings$20$rubigtownupdaterMainActivity(EditText editText, boolean[] zArr, Switch r4, final AlertDialog alertDialog, TextView textView, View view) {
        if (this.actionBusy) {
            return;
        }
        try {
            JSONObject jSONObjectPut = request(this.dnsSupported ? "set_settings" : "set_catalog_url").put("url", editText.getText().toString().trim());
            if (this.dnsSupported) {
                jSONObjectPut.put("dns_enabled", zArr[0] ? Boolean.valueOf(r4.isChecked()) : JSONObject.NULL);
            }
            alertDialog.getButton(-1).setEnabled(false);
            command(jSONObjectPut, new Result() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda10
                @Override // ru.big.town.updater.MainActivity.Result
                public final void apply(JSONObject jSONObject) throws Exception {
                    this.f$0.m16lambda$openSettings$19$rubigtownupdaterMainActivity(alertDialog, jSONObject);
                }
            });
        } catch (Exception e) {
            textView.setText(reason(e));
        }
    }

    /* JADX INFO: renamed from: lambda$openSettings$19$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m16lambda$openSettings$19$rubigtownupdaterMainActivity(AlertDialog alertDialog, JSONObject jSONObject) throws Exception {
        this.settings = jSONObject.getJSONObject("settings");
        alertDialog.dismiss();
        Toast.makeText(this, "Настройки сохранены", 0).show();
    }

    /* JADX INFO: renamed from: lambda$openSettings$21$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m18lambda$openSettings$21$rubigtownupdaterMainActivity(AlertDialog alertDialog, boolean[] zArr, Switch r8, TextView textView, JSONObject jSONObject) throws Exception {
        StringBuilder sb;
        String str;
        if (alertDialog.isShowing()) {
            JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("settings");
            if (jSONObjectOptJSONObject != null) {
                this.settings = jSONObjectOptJSONObject;
            }
            String strOptString = jSONObject.optString("dnsStatus");
            boolean z = true;
            boolean z2 = "on".equals(strOptString) || "off".equals(strOptString);
            zArr[0] = z2;
            if (!z2 || (this.settings.isNull("dnsEnabled") ? !"on".equals(strOptString) : !this.settings.optBoolean("dnsEnabled"))) {
                z = false;
            }
            r8.setChecked(z);
            r8.setEnabled(zArr[0]);
            if (zArr[0]) {
                sb = new StringBuilder("Сейчас на ГУ: ").append("on".equals(strOptString) ? "Яндекс DNS" : "стандартный DNS");
                str = ". Выбор применяется при установке релиза.";
            } else {
                sb = new StringBuilder("DNS не определён или изменён извне. Оставляем без изменений.");
                str = !jSONObject.isNull("dnsError") ? "\n" + jSONObject.optString("dnsError") : "";
            }
            textView.setText(sb.append(str).toString());
        }
    }

    /* JADX WARN: Code duplicated, block: B:39:0x0092  */
    /* JADX WARN: Code duplicated, block: B:42:0x00a2  */
    /* JADX WARN: Code duplicated, block: B:43:0x00a5  */
    private void showNotice() {
        String str;
        StringBuilder sbAppend;
        String str2;
        String string;
        final boolean z;
        String str3;
        if (this.state.isNull("notice")) {
            this.noticeShowing = null;
            return;
        }
        String strOptString = this.state.optString("notice");
        if (!(this.hideCompletedResult && "success".equals(strOptString)) && this.hiddenResultKey == null && !strOptString.equals(this.noticeShowing) && this.noticeDialog == null) {
            this.noticeShowing = strOptString;
            if ("error".equals(strOptString)) {
                str = "Ошибка обновления VoyahTune";
            } else {
                str = "success".equals(strOptString) ? "VoyahTune обновлён" : "Доступна новая версия VoyahTune";
            }
            if ("error".equals(strOptString)) {
                sbAppend = new StringBuilder().append(this.state.optString("error"));
                str2 = "\nУстановите релиз через USB с компьютера.";
            } else {
                if ("success".equals(strOptString)) {
                    string = "Проверка запуска служб завершена успешно.";
                } else {
                    sbAppend = new StringBuilder("Версия ").append(strOptString);
                    str2 = ". Скачать её можно в меню обновления.";
                }
                if (!"error".equals(strOptString) || "success".equals(strOptString)) {
                    z = true;
                } else {
                    z = false;
                }
                AlertDialog.Builder message = new AlertDialog.Builder(this).setTitle(str).setMessage(string);
                if (z) {
                    str3 = "Завершить";
                } else {
                    str3 = "Открыть меню";
                }
                AlertDialog alertDialogCreate = message.setPositiveButton(str3, new DialogInterface.OnClickListener() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda14
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        this.f$0.m22lambda$showNotice$22$rubigtownupdaterMainActivity(z, dialogInterface, i);
                    }
                }).setNegativeButton("Скрыть", new DialogInterface.OnClickListener() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda15
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        this.f$0.m23lambda$showNotice$23$rubigtownupdaterMainActivity(dialogInterface, i);
                    }
                }).create();
                this.noticeDialog = alertDialogCreate;
                alertDialogCreate.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda16
                    @Override // android.content.DialogInterface.OnDismissListener
                    public final void onDismiss(DialogInterface dialogInterface) {
                        this.f$0.m25lambda$showNotice$25$rubigtownupdaterMainActivity(dialogInterface);
                    }
                });
                alertDialogCreate.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda17
                    @Override // android.content.DialogInterface.OnCancelListener
                    public final void onCancel(DialogInterface dialogInterface) {
                        this.f$0.m26lambda$showNotice$26$rubigtownupdaterMainActivity(dialogInterface);
                    }
                });
                alertDialogCreate.show();
            }
            string = sbAppend.append(str2).toString();
            if ("error".equals(strOptString)) {
                z = true;
            } else {
                z = true;
            }
            AlertDialog.Builder message2 = new AlertDialog.Builder(this).setTitle(str).setMessage(string);
            if (z) {
                str3 = "Завершить";
            } else {
                str3 = "Открыть меню";
            }
            AlertDialog alertDialogCreate2 = message2.setPositiveButton(str3, new DialogInterface.OnClickListener() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda14
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    this.f$0.m22lambda$showNotice$22$rubigtownupdaterMainActivity(z, dialogInterface, i);
                }
            }).setNegativeButton("Скрыть", new DialogInterface.OnClickListener() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda15
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    this.f$0.m23lambda$showNotice$23$rubigtownupdaterMainActivity(dialogInterface, i);
                }
            }).create();
            this.noticeDialog = alertDialogCreate2;
            alertDialogCreate2.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda16
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    this.f$0.m25lambda$showNotice$25$rubigtownupdaterMainActivity(dialogInterface);
                }
            });
            alertDialogCreate2.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda17
                @Override // android.content.DialogInterface.OnCancelListener
                public final void onCancel(DialogInterface dialogInterface) {
                    this.f$0.m26lambda$showNotice$26$rubigtownupdaterMainActivity(dialogInterface);
                }
            });
            alertDialogCreate2.show();
        }
    }

    /* JADX INFO: renamed from: lambda$showNotice$22$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m22lambda$showNotice$22$rubigtownupdaterMainActivity(boolean z, DialogInterface dialogInterface, int i) {
        if (z) {
            finishResult();
        }
    }

    /* JADX INFO: renamed from: lambda$showNotice$23$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m23lambda$showNotice$23$rubigtownupdaterMainActivity(DialogInterface dialogInterface, int i) {
        finish();
    }

    /* JADX INFO: renamed from: lambda$showNotice$24$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m24lambda$showNotice$24$rubigtownupdaterMainActivity() {
        try {
            this.client.call(request("dismiss"));
        } catch (Exception unused) {
        }
    }

    /* JADX INFO: renamed from: lambda$showNotice$25$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m25lambda$showNotice$25$rubigtownupdaterMainActivity(DialogInterface dialogInterface) {
        this.noticeDialog = null;
        if (this.worker.isShutdown()) {
            return;
        }
        this.worker.execute(new Runnable() { // from class: ru.big.town.updater.MainActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m24lambda$showNotice$24$rubigtownupdaterMainActivity();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$showNotice$26$ru-big-town-updater-MainActivity, reason: not valid java name */
    /* synthetic */ void m26lambda$showNotice$26$rubigtownupdaterMainActivity(DialogInterface dialogInterface) {
        finish();
    }

    private TextView label(LinearLayout linearLayout, String str, int i) {
        TextView textView = new TextView(this);
        textView.setText(str);
        textView.setTextColor(getColor(R.color.muted));
        textView.setTextSize(i);
        textView.setPadding(0, dp(8), 0, dp(8));
        linearLayout.addView(textView);
        return textView;
    }

    private void text(int i, String str) {
        TextView textView = (TextView) findViewById(i);
        if (TextUtils.equals(textView.getText(), str)) {
            return;
        }
        textView.setText(str);
    }

    private void visible(int i, boolean z) {
        View viewFindViewById = findViewById(i);
        int i2 = z ? 0 : 8;
        if (viewFindViewById.getVisibility() != i2) {
            viewFindViewById.setVisibility(i2);
        }
    }

    private void enabled(View view, boolean z) {
        if (view.isEnabled() != z) {
            view.setEnabled(z);
        }
    }

    private String reason(Exception exc) {
        return exc.getMessage() == null ? exc.getClass().getSimpleName() : exc.getMessage();
    }

    private int dp(int i) {
        return Math.round(i * getResources().getDisplayMetrics().density);
    }
}
