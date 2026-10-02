package ru.big.town.restoremode;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.os.ResultReceiver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/* JADX INFO: loaded from: classes2.dex */
public class VoiceActivity extends AppCompatActivity {
    private static final int COLOR_NOTE = -4473925;
    private static final int COLOR_REJECTED = -38037;
    private static final int COLOR_REJECTED_NOTE = -20304;
    private static final long COMMAND_TIMEOUT_MS = 8000;
    private static final long SEQUENCE_BUDGET_MS = 20000;
    private static final long SEQUENCE_ROW_MS = 1200;
    static final String TEST_ONLY = "voiceTestOnly";
    private AudioManager audio;
    private boolean bound;
    private Runnable commandTimeout;
    private List<VoiceCommandCatalog.Command> commands;
    private AlertDialog confirmation;
    private TextView details;
    private boolean ended;
    private AudioFocusRequest focus;
    private boolean interrupted;
    private Messenger nativeService;
    private VoiceOrbView orb;
    private MediaPlayer playback;
    private File recording;
    private LinearLayout resultList;
    private List<VoiceCommandSequence.Segment> sequence;
    private int sequenceAccepted;
    private long sequenceDuration;
    private int sequenceNext;
    private String session;
    private VoiceSounds sounds;
    private TextView status;
    private boolean submitted;
    private boolean testOnly;
    private TextView transcript;
    private final Handler ui = new Handler(Looper.getMainLooper());
    private final VoiceRecognizer recognizer = new VoiceRecognizer();
    private final List<TextView> rowNotes = new ArrayList();
    private final VoiceCloseControl closeControl = new VoiceCloseControl();
    private final ServiceConnection connection = new ServiceConnection() { // from class: ru.big.town.restoremode.VoiceActivity.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            VoiceActivity.this.nativeService = new Messenger(iBinder);
            if (VoiceActivity.this.ended || VoiceActivity.this.session == null) {
                return;
            }
            VoiceActivity.this.beginRecognition();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            VoiceActivity.this.nativeService = null;
            VoiceActivity.this.fail("Нет связи с сервисом автомобиля");
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            VoiceActivity.this.nativeService = null;
            if (VoiceActivity.this.bound) {
                VoiceActivity.this.unbindService(this);
                VoiceActivity.this.bound = false;
            }
            VoiceActivity.this.fail("Соединение с сервисом прервано");
        }

        @Override // android.content.ServiceConnection
        public void onNullBinding(ComponentName componentName) {
            VoiceActivity.this.fail("Сервис автомобиля недоступен");
        }
    };

    protected boolean isAnimationPreview() {
        return false;
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.sounds = new VoiceSounds(this);
        getWindow().clearFlags(2);
        getWindow().addFlags(1152);
        getWindow().getDecorView().setSystemUiVisibility(5894);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setBackgroundColor(-872415232);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setGravity(17);
        this.orb = new VoiceOrbView(this);
        int i = (int) (getResources().getDisplayMetrics().density * 360.0f);
        linearLayout.addView(this.orb, new LinearLayout.LayoutParams(i, i));
        this.status = label(26);
        this.transcript = label(40);
        linearLayout.addView(this.status);
        linearLayout.addView(this.transcript);
        LinearLayout linearLayout2 = new LinearLayout(this);
        this.resultList = linearLayout2;
        linearLayout2.setOrientation(1);
        this.resultList.setGravity(17);
        linearLayout.addView(this.resultList);
        TextView textViewLabel = label(16);
        this.details = textViewLabel;
        textViewLabel.setTag("voiceDiagnostics");
        this.details.setTextColor(COLOR_NOTE);
        linearLayout.addView(this.details);
        this.closeControl.attach(this, linearLayout, new Runnable() { // from class: ru.big.town.restoremode.VoiceActivity$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                VoiceActivity.this.m1965lambda$onCreate$0$rubigtownrestoremodeVoiceActivity();
            }
        }, new Runnable() { // from class: ru.big.town.restoremode.VoiceActivity$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                VoiceActivity.this.playRecording();
            }
        });
        frameLayout.addView(linearLayout, new FrameLayout.LayoutParams(-1, -2, 17));
        setContentView(frameLayout);
        this.audio = (AudioManager) getSystemService("audio");
        newSession();
        ensureBinding();
    }

    /* JADX INFO: renamed from: lambda$onCreate$0$ru-big-town-restoremode-VoiceActivity, reason: not valid java name */
    /* synthetic */ void m1965lambda$onCreate$0$rubigtownrestoremodeVoiceActivity() {
        cancelSession();
        finish();
    }

    private void ensureBinding() {
        if (isAnimationPreview() || this.ended || this.bound) {
            return;
        }
        if (this.testOnly) {
            beginRecognition();
            return;
        }
        try {
            boolean zBindService = bindService(new Intent().setClassName("ru.big.town.anative", "ru.big.town.anative.SetModesService"), this.connection, 1);
            this.bound = zBindService;
            if (zBindService) {
                return;
            }
            fail("Сервис автомобиля недоступен");
        } catch (RuntimeException unused) {
            fail("Не удалось подключиться к сервису автомобиля");
        }
    }

    private TextView label(int i) {
        TextView textView = new TextView(this);
        textView.setTextColor(-1);
        textView.setTextSize(i);
        textView.setGravity(17);
        textView.setPadding(24, 8, 24, 8);
        return textView;
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        newSession();
        if (this.nativeService == null || this.ended) {
            ensureBinding();
        } else {
            beginRecognition();
        }
    }

    private void newSession() {
        newSession(true);
    }

    private void newSession(boolean z) {
        cancelSession();
        clearRecording();
        boolean booleanExtra = getIntent().getBooleanExtra(TEST_ONLY, false);
        this.testOnly = booleanExtra;
        this.closeControl.testMode(booleanExtra);
        this.session = UUID.randomUUID().toString();
        this.ended = false;
        this.submitted = false;
        this.interrupted = false;
        this.sequence = null;
        this.sequenceNext = 0;
        this.sequenceAccepted = 0;
        this.sequenceDuration = 0L;
        this.commandTimeout = null;
        clearRows();
        this.status.setText("Подготовка помощника…");
        this.transcript.setText("");
        this.orb.state(false, false);
        this.details.setText("");
        this.details.setVisibility(this.testOnly ? 0 : 8);
        if (isAnimationPreview()) {
            this.status.setText("Слушаю…");
            this.transcript.setText("Тест анимации · имитация голоса");
            this.orb.state(true, false);
            if (z) {
                this.sounds.activation();
                return;
            }
            return;
        }
        if (!this.testOnly && !getSharedPreferences("DrivePreferences", 0).getBoolean("voiceAssistantEnabled", false)) {
            fail("Голосовое управление выключено");
        } else if (checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
            fail("Разрешите микрофон в разделе «Голосовое управление»");
        } else {
            this.commands = VoiceCommands.load(this);
            this.ui.postDelayed(new Runnable() { // from class: ru.big.town.restoremode.VoiceActivity$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    VoiceActivity.this.m1964lambda$newSession$1$rubigtownrestoremodeVoiceActivity();
                }
            }, 90000L);
        }
    }

    /* JADX INFO: renamed from: lambda$newSession$1$ru-big-town-restoremode-VoiceActivity, reason: not valid java name */
    /* synthetic */ void m1964lambda$newSession$1$rubigtownrestoremodeVoiceActivity() {
        fail("Подготовка помощника занимает слишком много времени");
    }

    protected final void previewLevel(float f) {
        this.orb.level(f);
    }

    protected final void previewState(String str) {
        if (isAnimationPreview()) {
            if ("listening".equals(str)) {
                newSession();
                return;
            }
            cancelSession();
            this.session = UUID.randomUUID().toString();
            this.ended = false;
            this.submitted = false;
            this.interrupted = false;
            if ("recognized".equals(str)) {
                this.orb.recognized();
                this.status.setText("Команда распознана");
                this.transcript.setText("Режим движения: Спорт");
                return;
            }
            if ("success".equals(str)) {
                showSuccess("Тестовая команда · закрытие через 3 секунды");
                return;
            }
            if ("fuel".equals(str)) {
                showSuccess(VoiceResultPresentation.successText("port_cap:fuel", "", 26), VoiceResultPresentation.successDurationMs("port_cap:fuel"));
                return;
            }
            if ("error".equals(str)) {
                fail("Тестовая ошибка · закрытие через 6 секунд");
                return;
            }
            if ("sequence".equals(str)) {
                this.ended = true;
                showRows(VoiceCommandSequence.sample());
                this.orb.recognized();
                this.status.setText("Команды переданы");
                this.sounds.success();
                this.ui.postDelayed(new Runnable() { // from class: ru.big.town.restoremode.VoiceActivity$$ExternalSyntheticLambda7
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoiceActivity.this.m1970lambda$previewState$2$rubigtownrestoremodeVoiceActivity();
                    }
                }, 6000L);
            }
        }
    }

    /* JADX INFO: renamed from: lambda$previewState$2$ru-big-town-restoremode-VoiceActivity, reason: not valid java name */
    /* synthetic */ void m1970lambda$previewState$2$rubigtownrestoremodeVoiceActivity() {
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void beginRecognition() {
        String str = this.session;
        if (this.testOnly || send("begin", null, null)) {
            this.recognizer.start(this, new AnonymousClass2(str));
        } else {
            fail("Сервис автомобиля недоступен");
        }
    }

    /* JADX INFO: renamed from: ru.big.town.restoremode.VoiceActivity$2, reason: invalid class name */
    class AnonymousClass2 implements VoiceRecognizer.Listener {
        final /* synthetic */ String val$token;

        AnonymousClass2(String str) {
            this.val$token = str;
        }

        private boolean active() {
            return (!this.val$token.equals(VoiceActivity.this.session) || VoiceActivity.this.ended || VoiceActivity.this.isFinishing()) ? false : true;
        }

        @Override // ru.big.town.restoremode.VoiceRecognizer.Listener
        public void ready(Runnable runnable) {
            if (!active() || VoiceActivity.this.interrupted || !VoiceActivity.this.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                VoiceActivity.this.recognizer.cancel();
                return;
            }
            VoiceActivity voiceActivity = VoiceActivity.this;
            AudioFocusRequest.Builder audioAttributes = new AudioFocusRequest.Builder(4).setAudioAttributes(new AudioAttributes.Builder().setUsage(16).setContentType(1).build());
            final String str = this.val$token;
            voiceActivity.focus = audioAttributes.setOnAudioFocusChangeListener(new AudioManager.OnAudioFocusChangeListener() { // from class: ru.big.town.restoremode.VoiceActivity$2$$ExternalSyntheticLambda1
                @Override // android.media.AudioManager.OnAudioFocusChangeListener
                public final void onAudioFocusChange(int i) {
                    AnonymousClass2.this.m1979lambda$ready$0$rubigtownrestoremodeVoiceActivity$2(str, i);
                }
            }, VoiceActivity.this.ui).build();
            if (VoiceActivity.this.audio.requestAudioFocus(VoiceActivity.this.focus) != 1) {
                VoiceActivity.this.fail("Аудиоканал занят. Завершите звонок и повторите команду.");
            } else {
                runnable.run();
            }
        }

        /* JADX INFO: renamed from: lambda$ready$0$ru-big-town-restoremode-VoiceActivity$2, reason: not valid java name */
        /* synthetic */ void m1979lambda$ready$0$rubigtownrestoremodeVoiceActivity$2(String str, int i) {
            if (i >= 0 || !str.equals(VoiceActivity.this.session)) {
                return;
            }
            VoiceActivity.this.fail("Микрофон занят другим приложением");
        }

        @Override // ru.big.town.restoremode.VoiceRecognizer.Listener
        public void listening() {
            if (active()) {
                VoiceActivity.this.ui.removeCallbacksAndMessages(null);
                VoiceActivity.this.status.setText("Слушаю…");
                VoiceActivity.this.orb.state(true, false);
                VoiceActivity.this.sounds.activation();
                VoiceActivity.this.ui.postDelayed(new Runnable() { // from class: ru.big.town.restoremode.VoiceActivity$2$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        AnonymousClass2.this.m1977lambda$listening$1$rubigtownrestoremodeVoiceActivity$2();
                    }
                }, 12000L);
            }
        }

        /* JADX INFO: renamed from: lambda$listening$1$ru-big-town-restoremode-VoiceActivity$2, reason: not valid java name */
        /* synthetic */ void m1977lambda$listening$1$rubigtownrestoremodeVoiceActivity$2() {
            VoiceActivity.this.fail("Не удалось распознать команду");
        }

        @Override // ru.big.town.restoremode.VoiceRecognizer.Listener
        public void audio(float f, String str) {
            if (active()) {
                VoiceActivity.this.orb.level(f);
                VoiceActivity.this.transcript.setText(str);
            }
        }

        @Override // ru.big.town.restoremode.VoiceRecognizer.Listener
        public void processing() {
            if (active()) {
                VoiceActivity.this.releaseFocus();
                VoiceActivity.this.status.setText("Распознаю…");
                VoiceActivity.this.orb.state(false, false);
                VoiceActivity.this.ui.removeCallbacksAndMessages(null);
                VoiceActivity.this.ui.postDelayed(new Runnable() { // from class: ru.big.town.restoremode.VoiceActivity$2$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        AnonymousClass2.this.m1978lambda$processing$2$rubigtownrestoremodeVoiceActivity$2();
                    }
                }, 15000L);
            }
        }

        /* JADX INFO: renamed from: lambda$processing$2$ru-big-town-restoremode-VoiceActivity$2, reason: not valid java name */
        /* synthetic */ void m1978lambda$processing$2$rubigtownrestoremodeVoiceActivity$2() {
            VoiceActivity.this.fail("Распознавание занимает слишком много времени");
        }

        @Override // ru.big.town.restoremode.VoiceRecognizer.Listener
        public void details(String str) {
            if (active() && VoiceActivity.this.testOnly) {
                VoiceActivity.this.details.setText(str);
            }
        }

        @Override // ru.big.town.restoremode.VoiceRecognizer.Listener
        public void recording(File file) {
            if (!active()) {
                file.delete();
                return;
            }
            if (VoiceActivity.this.recording != null) {
                VoiceActivity.this.recording.delete();
            }
            VoiceActivity.this.recording = file;
        }

        @Override // ru.big.town.restoremode.VoiceRecognizer.Listener
        public void result(String str) {
            if (active()) {
                VoiceActivity.this.recognized(str);
            }
        }

        @Override // ru.big.town.restoremode.VoiceRecognizer.Listener
        public void error(String str) {
            if (active()) {
                VoiceActivity.this.fail(str);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recognized(String str) {
        this.recognizer.cancel();
        releaseFocus();
        this.ui.removeCallbacksAndMessages(null);
        this.orb.state(false, false);
        this.transcript.setText(str);
        List<VoiceCommandSequence.Segment> list = VoiceCommandSequence.parse(this.commands, str);
        if (list.size() > 1) {
            recognizedSequence(str, list);
            return;
        }
        final VoiceCommandCatalog.Command command = list.isEmpty() ? null : list.get(0).command;
        if (!this.testOnly) {
            if (command == null) {
                fail(str.isEmpty() ? "Не расслышал команду" : "Команда не распознана");
                return;
            }
            this.orb.recognized();
            if (command.confirm) {
                this.status.setText("Подтвердите действие");
                AlertDialog alertDialogCreate = new MaterialAlertDialogBuilder(this, R.style.DarkDialog).setTitle((CharSequence) command.title).setMessage((CharSequence) "Выполнить распознанную команду?").setPositiveButton((CharSequence) "Выполнить", new DialogInterface.OnClickListener() { // from class: ru.big.town.restoremode.VoiceActivity$$ExternalSyntheticLambda4
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        VoiceActivity.this.m1971lambda$recognized$3$rubigtownrestoremodeVoiceActivity(command, dialogInterface, i);
                    }
                }).setNegativeButton((CharSequence) "Отмена", new DialogInterface.OnClickListener() { // from class: ru.big.town.restoremode.VoiceActivity$$ExternalSyntheticLambda5
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        VoiceActivity.this.m1972lambda$recognized$4$rubigtownrestoremodeVoiceActivity(dialogInterface, i);
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: ru.big.town.restoremode.VoiceActivity$$ExternalSyntheticLambda6
                    @Override // android.content.DialogInterface.OnCancelListener
                    public final void onCancel(DialogInterface dialogInterface) {
                        VoiceActivity.this.m1973lambda$recognized$5$rubigtownrestoremodeVoiceActivity(dialogInterface);
                    }
                }).create();
                this.confirmation = alertDialogCreate;
                alertDialogCreate.show();
                return;
            }
            execute(command);
            return;
        }
        this.ended = true;
        this.closeControl.recordingAvailable(this.recording != null);
        VoiceOrbView voiceOrbView = this.orb;
        if (command != null) {
            voiceOrbView.recognized();
        } else {
            voiceOrbView.state(false, true);
        }
        this.status.setText(command == null ? "Тест: команда не распознана" : "Тест: " + command.title);
        TextView textView = this.transcript;
        if (str.isEmpty()) {
            str = "Речь не распознана";
        }
        textView.setText(str);
        this.details.append("\nПроверка без выполнения команды");
    }

    /* JADX INFO: renamed from: lambda$recognized$3$ru-big-town-restoremode-VoiceActivity, reason: not valid java name */
    /* synthetic */ void m1971lambda$recognized$3$rubigtownrestoremodeVoiceActivity(VoiceCommandCatalog.Command command, DialogInterface dialogInterface, int i) {
        execute(command);
    }

    /* JADX INFO: renamed from: lambda$recognized$4$ru-big-town-restoremode-VoiceActivity, reason: not valid java name */
    /* synthetic */ void m1972lambda$recognized$4$rubigtownrestoremodeVoiceActivity(DialogInterface dialogInterface, int i) {
        finish();
    }

    /* JADX INFO: renamed from: lambda$recognized$5$ru-big-town-restoremode-VoiceActivity, reason: not valid java name */
    /* synthetic */ void m1973lambda$recognized$5$rubigtownrestoremodeVoiceActivity(DialogInterface dialogInterface) {
        finish();
    }

    private void recognizedSequence(String str, List<VoiceCommandSequence.Segment> list) {
        this.sequence = list;
        this.sequenceNext = 0;
        this.sequenceAccepted = 0;
        this.sequenceDuration = 0L;
        showRows(list);
        Iterator<VoiceCommandSequence.Segment> it = list.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (!it.next().accepted()) {
                i++;
            }
        }
        if (this.testOnly) {
            this.ended = true;
            this.closeControl.recordingAvailable(this.recording != null);
            if (i < list.size()) {
                this.orb.recognized();
            } else {
                this.orb.state(false, true);
            }
            this.status.setText("Тест: команд " + list.size() + ", отклонено " + i);
            TextView textView = this.transcript;
            if (str.isEmpty()) {
                str = "Речь не распознана";
            }
            textView.setText(str);
            this.details.append("\nПроверка без выполнения команд");
            return;
        }
        if (i == list.size()) {
            fail("Команды не распознаны");
            return;
        }
        this.orb.recognized();
        this.status.setText("Передаю команды…");
        this.ui.postDelayed(new Runnable() { // from class: ru.big.town.restoremode.VoiceActivity$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                VoiceActivity.this.m1974x9421ae43();
            }
        }, SEQUENCE_BUDGET_MS);
        stepSequence();
    }

    /* JADX INFO: renamed from: lambda$recognizedSequence$6$ru-big-town-restoremode-VoiceActivity, reason: not valid java name */
    /* synthetic */ void m1974x9421ae43() {
        fail("Выполнение команд занимает слишком много времени");
    }

    private void stepSequence() {
        while (this.sequenceNext < this.sequence.size() && !this.sequence.get(this.sequenceNext).accepted()) {
            this.sequenceNext++;
        }
        if (this.sequenceNext >= this.sequence.size()) {
            finishSequence();
            return;
        }
        final int i = this.sequenceNext;
        final String str = this.session;
        this.status.setText("Команда " + (i + 1) + " из " + this.sequence.size() + "…");
        this.rowNotes.get(i).setTextColor(COLOR_NOTE);
        this.rowNotes.get(i).setText("Отправляю…");
        ResultReceiver resultReceiver = new ResultReceiver(this.ui) { // from class: ru.big.town.restoremode.VoiceActivity.3
            @Override // android.os.ResultReceiver
            protected void onReceiveResult(int i2, Bundle bundle) {
                String string;
                if (!str.equals(VoiceActivity.this.session) || VoiceActivity.this.ended || VoiceActivity.this.isFinishing()) {
                    return;
                }
                VoiceActivity.this.ui.removeCallbacks(VoiceActivity.this.commandTimeout);
                VoiceActivity voiceActivity = VoiceActivity.this;
                int i3 = i;
                if (i2 == 1) {
                    string = null;
                } else {
                    string = "Не удалось выполнить команду";
                    if (bundle != null) {
                        string = bundle.getString("error", "Не удалось выполнить команду");
                    }
                }
                voiceActivity.completeStep(i3, string, bundle);
            }
        };
        this.commandTimeout = null;
        boolean zSend = send("execute", this.sequence.get(i).command.action, resultReceiver, i, this.sequence.size());
        this.submitted = zSend;
        if (!zSend) {
            completeStep(i, "Не удалось отправить команду", null);
            return;
        }
        Runnable runnable = new Runnable() { // from class: ru.big.town.restoremode.VoiceActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                VoiceActivity.this.m1976lambda$stepSequence$7$rubigtownrestoremodeVoiceActivity(str, i);
            }
        };
        this.commandTimeout = runnable;
        this.ui.postDelayed(runnable, COMMAND_TIMEOUT_MS);
    }

    /* JADX INFO: renamed from: lambda$stepSequence$7$ru-big-town-restoremode-VoiceActivity, reason: not valid java name */
    /* synthetic */ void m1976lambda$stepSequence$7$rubigtownrestoremodeVoiceActivity(String str, int i) {
        if (!str.equals(this.session) || this.ended || isFinishing() || this.sequenceNext != i) {
            return;
        }
        completeStep(i, "Сервис не ответил на команду", null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void completeStep(int i, String str, Bundle bundle) {
        if (str == null) {
            this.sequenceAccepted++;
            this.rowNotes.get(i).setText(noteFor(this.sequence.get(i).command, bundle));
            this.sequenceDuration = Math.max(this.sequenceDuration, VoiceResultPresentation.successDurationMs(this.sequence.get(i).command.action));
        } else {
            this.rowNotes.get(i).setTextColor(COLOR_REJECTED_NOTE);
            this.rowNotes.get(i).setText(str);
        }
        this.sequenceNext = i + 1;
        stepSequence();
    }

    private String noteFor(VoiceCommandCatalog.Command command, Bundle bundle) {
        if (command.action.startsWith("auto_light:")) {
            getSharedPreferences("DrivePreferences", 0).edit().putBoolean("autoLight", command.action.endsWith(":on")).apply();
        }
        if ("port_cap:fuel".equals(command.action)) {
            return VoiceResultPresentation.successText(command.action, command.title, bundle != null ? bundle.getInt("fuelRefillLiters", -1) : -1);
        }
        if (command.action.startsWith("fuel_charge:") && bundle != null && bundle.getBoolean("chargeTargetConfirmed", false)) {
            return "Уровень поддержания заряда подтверждён";
        }
        if (VoiceSeatCommands.isAction(command.action) || command.action.startsWith("wheel_heat:") || VoiceWindowCommands.isAction(command.action)) {
            return "Команда отправлена";
        }
        return "Выполнено";
    }

    private void finishSequence() {
        if (this.ended || isFinishing()) {
            return;
        }
        if (this.sequenceAccepted == 0) {
            fail("Команды не выполнены");
            return;
        }
        this.ended = true;
        this.recognizer.cancel();
        releaseFocus();
        this.ui.removeCallbacksAndMessages(null);
        this.orb.recognized();
        this.status.setText("Команды переданы");
        this.sounds.success();
        this.closeControl.recordingAvailable(this.recording != null);
        this.ui.postDelayed(new Runnable() { // from class: ru.big.town.restoremode.VoiceActivity$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                VoiceActivity.this.m1963lambda$finishSequence$8$rubigtownrestoremodeVoiceActivity();
            }
        }, Math.max(3000L, Math.max(this.sequenceDuration, ((long) this.sequenceAccepted) * SEQUENCE_ROW_MS)));
    }

    /* JADX INFO: renamed from: lambda$finishSequence$8$ru-big-town-restoremode-VoiceActivity, reason: not valid java name */
    /* synthetic */ void m1963lambda$finishSequence$8$rubigtownrestoremodeVoiceActivity() {
        finish();
    }

    private void showRows(List<VoiceCommandSequence.Segment> list) {
        clearRows();
        int i = 0;
        while (i < list.size()) {
            VoiceCommandSequence.Segment segment = list.get(i);
            TextView textViewLabel = label(22);
            i++;
            textViewLabel.setText(i + ". " + (segment.accepted() ? segment.command.title : segment.text));
            TextView textViewLabel2 = label(16);
            textViewLabel2.setTextColor(COLOR_NOTE);
            if (segment.accepted()) {
                textViewLabel2.setText("");
            } else {
                textViewLabel.setTextColor(COLOR_REJECTED);
                textViewLabel2.setTextColor(COLOR_REJECTED_NOTE);
                textViewLabel2.setText(segment.reason);
            }
            this.resultList.addView(textViewLabel);
            this.resultList.addView(textViewLabel2);
            this.rowNotes.add(textViewLabel2);
        }
    }

    private void clearRows() {
        this.resultList.removeAllViews();
        this.rowNotes.clear();
    }

    private void execute(final VoiceCommandCatalog.Command command) {
        final String str = this.session;
        this.status.setText("Передаю команду…");
        this.transcript.setText(command.title);
        boolean zSend = send("execute", command.action, new ResultReceiver(this.ui) { // from class: ru.big.town.restoremode.VoiceActivity.4
            @Override // android.os.ResultReceiver
            protected void onReceiveResult(int i, Bundle bundle) {
                if (!str.equals(VoiceActivity.this.session) || VoiceActivity.this.ended || VoiceActivity.this.isFinishing()) {
                    return;
                }
                if (i != 1) {
                    VoiceActivity.this.fail(bundle != null ? bundle.getString("error", "Не удалось выполнить команду") : "Не удалось выполнить команду");
                    return;
                }
                if (command.action.startsWith("auto_light:")) {
                    VoiceActivity.this.getSharedPreferences("DrivePreferences", 0).edit().putBoolean("autoLight", command.action.endsWith(":on")).apply();
                }
                VoiceActivity.this.showSuccess(VoiceResultPresentation.successText(command.action, command.title, bundle != null ? bundle.getInt("fuelRefillLiters", -1) : -1), VoiceResultPresentation.successDurationMs(command.action));
                if (VoiceSeatCommands.isAction(command.action) || command.action.startsWith("wheel_heat:") || VoiceWindowCommands.isAction(command.action)) {
                    VoiceActivity.this.status.setText("Команда отправлена");
                }
                if (command.action.startsWith("fuel_charge:") && bundle != null && bundle.getBoolean("chargeTargetConfirmed", false)) {
                    VoiceActivity.this.status.setText("Уровень поддержания заряда подтверждён");
                }
            }
        });
        this.submitted = zSend;
        if (!zSend) {
            fail("Не удалось отправить команду");
        } else {
            this.ui.postDelayed(new Runnable() { // from class: ru.big.town.restoremode.VoiceActivity$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    VoiceActivity.this.m1961lambda$execute$9$rubigtownrestoremodeVoiceActivity();
                }
            }, COMMAND_TIMEOUT_MS);
        }
    }

    /* JADX INFO: renamed from: lambda$execute$9$ru-big-town-restoremode-VoiceActivity, reason: not valid java name */
    /* synthetic */ void m1961lambda$execute$9$rubigtownrestoremodeVoiceActivity() {
        fail("Сервис не ответил на команду");
    }

    private boolean send(String str, String str2, ResultReceiver resultReceiver) {
        return send(str, str2, resultReceiver, 0, 1);
    }

    private boolean send(String str, String str2, ResultReceiver resultReceiver, int i, int i2) {
        String str3;
        Messenger messenger = this.nativeService;
        if (messenger != null && (str3 = this.session) != null) {
            try {
                messenger.send(VoiceCommandMessage.create(str3, str, str2, resultReceiver, i, i2));
                return true;
            } catch (Exception unused) {
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fail(String str) {
        if (this.ended || isFinishing()) {
            return;
        }
        this.ended = true;
        this.recognizer.cancel();
        releaseFocus();
        send("cancel", null, null);
        this.ui.removeCallbacksAndMessages(null);
        this.orb.state(false, true);
        this.status.setText(str);
        this.sounds.error();
        this.closeControl.recordingAvailable(this.recording != null);
        if (this.testOnly) {
            return;
        }
        this.ui.postDelayed(new Runnable() { // from class: ru.big.town.restoremode.VoiceActivity$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                VoiceActivity.this.m1962lambda$fail$10$rubigtownrestoremodeVoiceActivity();
            }
        }, 6000L);
    }

    /* JADX INFO: renamed from: lambda$fail$10$ru-big-town-restoremode-VoiceActivity, reason: not valid java name */
    /* synthetic */ void m1962lambda$fail$10$rubigtownrestoremodeVoiceActivity() {
        finish();
    }

    private void showSuccess(String str) {
        showSuccess(str, PathInterpolatorCompat.MAX_NUM_POINTS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSuccess(String str, int i) {
        this.ended = true;
        this.recognizer.cancel();
        releaseFocus();
        this.ui.removeCallbacksAndMessages(null);
        this.orb.recognized();
        this.status.setText("Команда передана");
        this.transcript.setText(str);
        this.sounds.success();
        this.closeControl.recordingAvailable(this.recording != null);
        this.ui.postDelayed(new Runnable() { // from class: ru.big.town.restoremode.VoiceActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                VoiceActivity.this.m1975lambda$showSuccess$11$rubigtownrestoremodeVoiceActivity();
            }
        }, i);
    }

    /* JADX INFO: renamed from: lambda$showSuccess$11$ru-big-town-restoremode-VoiceActivity, reason: not valid java name */
    /* synthetic */ void m1975lambda$showSuccess$11$rubigtownrestoremodeVoiceActivity() {
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseFocus() {
        AudioManager audioManager;
        AudioFocusRequest audioFocusRequest = this.focus;
        if (audioFocusRequest == null || (audioManager = this.audio) == null) {
            return;
        }
        audioManager.abandonAudioFocusRequest(audioFocusRequest);
        this.focus = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playRecording() {
        File file;
        if (this.testOnly) {
            if (this.playback != null) {
                stopPlayback();
                return;
            }
            if (this.ended && (file = this.recording) != null && file.isFile()) {
                this.ui.removeCallbacksAndMessages(null);
                this.sounds.stop();
                AudioFocusRequest audioFocusRequestBuild = new AudioFocusRequest.Builder(2).setAudioAttributes(new AudioAttributes.Builder().setUsage(1).setContentType(1).build()).setOnAudioFocusChangeListener(new AudioManager.OnAudioFocusChangeListener() { // from class: ru.big.town.restoremode.VoiceActivity$$ExternalSyntheticLambda0
                    @Override // android.media.AudioManager.OnAudioFocusChangeListener
                    public final void onAudioFocusChange(int i) {
                        VoiceActivity.this.m1966lambda$playRecording$12$rubigtownrestoremodeVoiceActivity(i);
                    }
                }, this.ui).build();
                this.focus = audioFocusRequestBuild;
                if (this.audio.requestAudioFocus(audioFocusRequestBuild) != 1) {
                    releaseFocus();
                    this.status.setText("Не удалось получить звук для прослушивания");
                    return;
                }
                MediaPlayer mediaPlayer = new MediaPlayer();
                this.playback = mediaPlayer;
                this.closeControl.playing(true);
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(1).setContentType(1).build());
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: ru.big.town.restoremode.VoiceActivity$$ExternalSyntheticLambda8
                    @Override // android.media.MediaPlayer.OnPreparedListener
                    public final void onPrepared(MediaPlayer mediaPlayer2) {
                        VoiceActivity.this.m1967lambda$playRecording$13$rubigtownrestoremodeVoiceActivity(mediaPlayer2);
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: ru.big.town.restoremode.VoiceActivity$$ExternalSyntheticLambda9
                    @Override // android.media.MediaPlayer.OnCompletionListener
                    public final void onCompletion(MediaPlayer mediaPlayer2) {
                        VoiceActivity.this.m1968lambda$playRecording$14$rubigtownrestoremodeVoiceActivity(mediaPlayer2);
                    }
                });
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: ru.big.town.restoremode.VoiceActivity$$ExternalSyntheticLambda10
                    @Override // android.media.MediaPlayer.OnErrorListener
                    public final boolean onError(MediaPlayer mediaPlayer2, int i, int i2) {
                        return VoiceActivity.this.m1969lambda$playRecording$15$rubigtownrestoremodeVoiceActivity(mediaPlayer2, i, i2);
                    }
                });
                try {
                    mediaPlayer.setDataSource(this.recording.getAbsolutePath());
                    mediaPlayer.prepareAsync();
                } catch (Exception unused) {
                    stopPlayback();
                    this.status.setText("Не удалось открыть запись");
                }
            }
        }
    }

    /* JADX INFO: renamed from: lambda$playRecording$12$ru-big-town-restoremode-VoiceActivity, reason: not valid java name */
    /* synthetic */ void m1966lambda$playRecording$12$rubigtownrestoremodeVoiceActivity(int i) {
        if (i < 0) {
            stopPlayback();
        }
    }

    /* JADX INFO: renamed from: lambda$playRecording$13$ru-big-town-restoremode-VoiceActivity, reason: not valid java name */
    /* synthetic */ void m1967lambda$playRecording$13$rubigtownrestoremodeVoiceActivity(MediaPlayer mediaPlayer) {
        if (this.playback != mediaPlayer || isFinishing()) {
            return;
        }
        mediaPlayer.start();
    }

    /* JADX INFO: renamed from: lambda$playRecording$14$ru-big-town-restoremode-VoiceActivity, reason: not valid java name */
    /* synthetic */ void m1968lambda$playRecording$14$rubigtownrestoremodeVoiceActivity(MediaPlayer mediaPlayer) {
        if (this.playback == mediaPlayer) {
            stopPlayback();
        }
    }

    /* JADX INFO: renamed from: lambda$playRecording$15$ru-big-town-restoremode-VoiceActivity, reason: not valid java name */
    /* synthetic */ boolean m1969lambda$playRecording$15$rubigtownrestoremodeVoiceActivity(MediaPlayer mediaPlayer, int i, int i2) {
        if (this.playback != mediaPlayer) {
            return true;
        }
        stopPlayback();
        this.status.setText("Не удалось прослушать запись");
        return true;
    }

    private void stopPlayback() {
        MediaPlayer mediaPlayer = this.playback;
        if (mediaPlayer != null) {
            mediaPlayer.setOnPreparedListener(null);
            this.playback.setOnCompletionListener(null);
            this.playback.setOnErrorListener(null);
            this.playback.release();
            this.playback = null;
            releaseFocus();
        }
        this.closeControl.playing(false);
    }

    private void clearRecording() {
        stopPlayback();
        this.closeControl.recordingAvailable(false);
        File file = this.recording;
        if (file != null) {
            file.delete();
            this.recording = null;
        }
    }

    private void cancelSession() {
        this.sounds.stop();
        this.ended = true;
        this.recognizer.cancel();
        releaseFocus();
        this.ui.removeCallbacksAndMessages(null);
        AlertDialog alertDialog = this.confirmation;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.confirmation = null;
        }
        send("cancel", null, null);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        if (this.interrupted) {
            finish();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        clearRecording();
        this.sounds.stop();
        this.interrupted = true;
        this.recognizer.cancel();
        releaseFocus();
        if (!this.submitted) {
            cancelSession();
        }
        super.onPause();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        super.onStop();
        this.recognizer.cancel();
        releaseFocus();
        if (!this.submitted) {
            cancelSession();
        }
        finish();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        clearRecording();
        this.sounds.release();
        this.closeControl.dispose();
        this.recognizer.cancel();
        releaseFocus();
        this.ui.removeCallbacksAndMessages(null);
        if (!this.submitted) {
            send("cancel", null, null);
        }
        if (this.bound) {
            unbindService(this.connection);
        }
        super.onDestroy();
    }
}
