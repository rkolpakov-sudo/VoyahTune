package ru.big.town.anative;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import ru.big.town.common.DriveSelectionPolicy;
import ru.big.town.common.SuspensionWidgetProtocol;

/* JADX INFO: loaded from: classes2.dex */
final class SuspensionWidgetController {
    private static final OemVehicleStateTransport.StateKey DIRECTION;
    private static final OemVehicleStateTransport.StateKey DRIVE;
    private static final OemVehicleStateTransport.StateKey ENTER;
    private static final OemVehicleStateTransport.StateKey HEIGHT;
    private static final OemVehicleStateTransport.StateKey INHIBIT;
    private static final List<OemVehicleStateTransport.StateKey> KEYS;
    private static final OemVehicleStateTransport.StateKey MAINTENANCE;
    private Messenger client;
    private volatile boolean closed;
    private boolean commandInFlight;
    private final Context context;
    private int direction;
    private int drive;
    private int height;
    private int inhibit;
    private int maintenance;
    private String message;
    private int pending;
    private CanBusEventHub.Subscription subscription;
    private final HandlerThread thread;
    private final Runnable timeout;
    private final Handler worker;

    static {
        OemVehicleStateTransport.StateKey stateKeyKey = key("ASC_MODE_SELECT", 750);
        HEIGHT = stateKeyKey;
        OemVehicleStateTransport.StateKey stateKeyKey2 = key("ASC_ADJUST_DIREACT", 751);
        DIRECTION = stateKeyKey2;
        OemVehicleStateTransport.StateKey stateKeyKey3 = key("ASC_MAINTAIN_SWITCH", 711);
        MAINTENANCE = stateKeyKey3;
        OemVehicleStateTransport.StateKey stateKeyKey4 = key("ASC_ADJUST_SUSTEMP", 1060);
        INHIBIT = stateKeyKey4;
        OemVehicleStateTransport.StateKey stateKeyKey5 = key("DRIVING_MODE_SET", 545);
        DRIVE = stateKeyKey5;
        OemVehicleStateTransport.StateKey stateKeyKey6 = key("MANUAL_EASY_ENTER_SET", 790);
        ENTER = stateKeyKey6;
        KEYS = Arrays.asList(stateKeyKey, stateKeyKey2, stateKeyKey3, stateKeyKey4, stateKeyKey5, stateKeyKey6);
    }

    /* JADX INFO: renamed from: lambda$new$0$ru-big-town-anative-SuspensionWidgetController, reason: not valid java name */
    /* synthetic */ void m2135lambda$new$0$rubigtownanativeSuspensionWidgetController() {
        if (this.pending >= 0) {
            this.pending = -1;
            this.message = "Изменение высоты не подтверждено";
            CommandStatusHub.get().mismatch(ReadBackTable.FEATURE_SUSPENSION, ReadBackTable.SOURCE_ASC);
            publish();
        }
    }

    SuspensionWidgetController(Context context) {
        HandlerThread handlerThread = new HandlerThread("SuspensionWidget");
        this.thread = handlerThread;
        this.height = -1;
        this.direction = -1;
        this.maintenance = -1;
        this.drive = -1;
        this.inhibit = -1;
        this.pending = -1;
        this.message = "Нет данных подвески";
        this.timeout = new Runnable() { // from class: ru.big.town.anative.SuspensionWidgetController$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                SuspensionWidgetController.this.m2135lambda$new$0$rubigtownanativeSuspensionWidgetController();
            }
        };
        this.context = context.getApplicationContext();
        handlerThread.start();
        this.worker = new Handler(handlerThread.getLooper());
    }

    private static OemVehicleStateTransport.StateKey key(String str, int i) {
        return new OemVehicleStateTransport.StateKey(str, i);
    }

    void handle(Message message) {
        final int i = message.what;
        final int i2 = message.arg1;
        final Messenger messenger = message.replyTo;
        final boolean z = message.getData().getBoolean(SuspensionWidgetProtocol.HEIGHT_ONLY, false);
        this.worker.post(new Runnable() { // from class: ru.big.town.anative.SuspensionWidgetController$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                SuspensionWidgetController.this.m2134lambda$handle$1$rubigtownanativeSuspensionWidgetController(i, messenger, z, i2);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$handle$1$ru-big-town-anative-SuspensionWidgetController, reason: not valid java name */
    /* synthetic */ void m2134lambda$handle$1$rubigtownanativeSuspensionWidgetController(int i, Messenger messenger, boolean z, int i2) {
        if (this.closed) {
            return;
        }
        if (i == 91) {
            Messenger messenger2 = this.client;
            if (messenger2 == null || !messenger2.equals(messenger)) {
                return;
            }
            stopWatching();
            return;
        }
        if (messenger == null) {
            return;
        }
        this.client = messenger;
        if (this.subscription == null) {
            this.subscription = CanBusEventHub.get(this.context).subscribe(17, new int[]{750, 751, 711, 545, 1060}, this.worker, new CanBusEventHub.Listener() { // from class: ru.big.town.anative.SuspensionWidgetController$$ExternalSyntheticLambda2
                @Override // ru.big.town.anative.CanBusEventHub.Listener
                public final void onCanBusEvent(CanBusEvent canBusEvent) {
                    SuspensionWidgetController.this.onEvent(canBusEvent);
                }
            });
            refresh();
        }
        if (i != 92) {
            publish();
        } else if (z) {
            this.message = "Независимая высота пока не проверена";
            publish();
        } else {
            select(i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onEvent(CanBusEvent canBusEvent) {
        if (this.closed) {
            return;
        }
        if (canBusEvent.kind == CanBusEvent.Kind.CONNECTION_LOST) {
            this.height = -1;
            this.maintenance = -1;
            this.drive = -1;
            this.inhibit = -1;
            this.pending = -1;
            this.message = "Нет связи с автомобилем";
            publish();
            return;
        }
        if (canBusEvent.kind == CanBusEvent.Kind.CONNECTION) {
            refresh();
            return;
        }
        if (canBusEvent.kind == CanBusEvent.Kind.VEHICLE_STATE) {
            if (canBusEvent.first == 750) {
                this.height = canBusEvent.second;
            }
            if (canBusEvent.first == 751) {
                this.direction = canBusEvent.second;
            }
            if (canBusEvent.first == 711) {
                this.maintenance = canBusEvent.second;
            }
            if (canBusEvent.first == 1060) {
                this.inhibit = canBusEvent.second;
            }
            if (canBusEvent.first == 545) {
                this.drive = canBusEvent.second;
            }
            checkCompletion();
            publish();
        }
    }

    private void refresh() {
        Map<OemVehicleStateTransport.StateKey, Integer> vehicleStates = OemVehicleStateTransport.readVehicleStates(this.context, KEYS);
        this.height = value(vehicleStates, HEIGHT);
        this.direction = value(vehicleStates, DIRECTION);
        this.maintenance = value(vehicleStates, MAINTENANCE);
        this.drive = value(vehicleStates, DRIVE);
        this.inhibit = value(vehicleStates, INHIBIT);
        checkCompletion();
        publish();
    }

    private static int value(Map<OemVehicleStateTransport.StateKey, Integer> map, OemVehicleStateTransport.StateKey stateKey) {
        if (map == null || map.get(stateKey) == null) {
            return -1;
        }
        return map.get(stateKey).intValue();
    }

    private void checkCompletion() {
        int i = this.pending;
        if (i >= 0 && SuspensionWidgetPolicy.reached(i, this.height)) {
            this.pending = -1;
            this.worker.removeCallbacks(this.timeout);
            this.message = "Высота подтверждена";
            CommandStatusHub.get().ack(ReadBackTable.FEATURE_SUSPENSION, ReadBackTable.SOURCE_ASC);
        } else if (this.pending < 0 && "Нет данных подвески".equals(this.message) && SuspensionWidgetPolicy.validHeight(this.height)) {
            this.message = "";
        }
    }

    private void select(final int i) {
        if (this.pending >= 0 || this.commandInFlight) {
            publish();
            return;
        }
        this.commandInFlight = true;
        this.message = "Отправляем команду…";
        publish();
        ApplyEngine.postUserCommand("suspension widget", new Runnable() { // from class: ru.big.town.anative.SuspensionWidgetController$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                SuspensionWidgetController.this.m2137lambda$select$3$rubigtownanativeSuspensionWidgetController(i);
            }
        }, new Runnable() { // from class: ru.big.town.anative.SuspensionWidgetController$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                SuspensionWidgetController.this.m2139lambda$select$5$rubigtownanativeSuspensionWidgetController();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$select$3$ru-big-town-anative-SuspensionWidgetController, reason: not valid java name */
    /* synthetic */ void m2137lambda$select$3$rubigtownanativeSuspensionWidgetController(final int i) {
        final String[] strArr = new String[1];
        CommandStatusHub.get().submit(ReadBackTable.FEATURE_SUSPENSION, new CommandDispatcher.SendAction() { // from class: ru.big.town.anative.SuspensionWidgetController.1
            @Override // ru.big.town.anative.CommandDispatcher.SendAction
            public boolean send() {
                String strDispatch = SuspensionWidgetController.this.dispatch(i);
                strArr[0] = strDispatch;
                return strDispatch.isEmpty();
            }
        });
        String str = strArr[0];
        final String strDispatch2 = str == null ? "Команда не выполнена" : str;
        this.worker.post(new Runnable() { // from class: ru.big.town.anative.SuspensionWidgetController$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                SuspensionWidgetController.this.m2136lambda$select$2$rubigtownanativeSuspensionWidgetController(strDispatch2, i);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$select$2$ru-big-town-anative-SuspensionWidgetController, reason: not valid java name */
    /* synthetic */ void m2136lambda$select$2$rubigtownanativeSuspensionWidgetController(String str, int i) {
        this.commandInFlight = false;
        if (this.closed) {
            return;
        }
        if (str.isEmpty()) {
            this.pending = i;
            this.message = "Команда отправлена, ожидаем высоту";
            this.worker.removeCallbacks(this.timeout);
            this.worker.postDelayed(this.timeout, 45000L);
            checkCompletion();
        } else {
            this.message = str;
        }
        publish();
    }

    /* JADX INFO: renamed from: lambda$select$5$ru-big-town-anative-SuspensionWidgetController, reason: not valid java name */
    /* synthetic */ void m2139lambda$select$5$rubigtownanativeSuspensionWidgetController() {
        this.worker.post(new Runnable() { // from class: ru.big.town.anative.SuspensionWidgetController$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                SuspensionWidgetController.this.m2138lambda$select$4$rubigtownanativeSuspensionWidgetController();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$select$4$ru-big-town-anative-SuspensionWidgetController, reason: not valid java name */
    /* synthetic */ void m2138lambda$select$4$rubigtownanativeSuspensionWidgetController() {
        if (this.commandInFlight) {
            this.commandInFlight = false;
            this.message = "Команда не выполнена";
            publish();
        }
    }

    private String dispatch(final int i) {
        if (this.closed) {
            return "Сервис остановлен";
        }
        DriveSelectionPolicy driveSelectionPolicy = DriveSelectionStore.read(this.context);
        if (driveSelectionPolicy == null) {
            return "Обновите RestoreMode: нет общего состояния режимов";
        }
        final String strDriveMode = SuspensionWidgetPolicy.driveMode(i, DriveSelectionPolicy.value(driveSelectionPolicy.medium));
        final Map<OemVehicleStateTransport.StateKey, Integer> mapStatesFor = strDriveMode == null ? null : DriveModeCanTransport.statesFor(this.context, strDriveMode);
        if (i != 0 && mapStatesFor == null) {
            return "Профиль движения недоступен";
        }
        ArrayList arrayList = new ArrayList(KEYS);
        if (mapStatesFor != null) {
            arrayList.addAll(mapStatesFor.keySet());
        }
        String str = (String) OemVehicleStateTransport.withSession(this.context, arrayList, new OemVehicleStateTransport.SessionOperation() { // from class: ru.big.town.anative.SuspensionWidgetController$$ExternalSyntheticLambda0
            @Override // ru.big.town.anative.OemVehicleStateTransport.SessionOperation
            public final Object run(OemVehicleStateTransport.Session session) {
                return SuspensionWidgetController.this.m2133lambda$dispatch$6$rubigtownanativeSuspensionWidgetController(i, mapStatesFor, strDriveMode, session);
            }
        });
        return str == null ? "Нет связи с автомобилем" : str;
    }

    /* JADX INFO: renamed from: lambda$dispatch$6$ru-big-town-anative-SuspensionWidgetController, reason: not valid java name */
    /* synthetic */ String m2133lambda$dispatch$6$rubigtownanativeSuspensionWidgetController(int i, Map map, String str, OemVehicleStateTransport.Session session) {
        OemVehicleStateTransport.Result resultSendBundle;
        String str2;
        Integer vehicleState = session.readVehicleState(HEIGHT);
        Integer vehicleState2 = session.readVehicleState(MAINTENANCE);
        Integer numValueOf = Integer.valueOf(i == 0 ? session.readVehicleState(INHIBIT).intValue() : 0);
        Integer numValueOf2 = Integer.valueOf(i == 0 ? session.readVehicleState(DRIVE).intValue() : -1);
        String strBlocked = SuspensionWidgetPolicy.blocked(i, vehicleState == null ? -1 : vehicleState.intValue(), vehicleState2 == null ? -1 : vehicleState2.intValue(), numValueOf == null ? -1 : numValueOf.intValue(), Integer.valueOf(i == 3 ? session.readVehicleSpeed().intValue() : 0), numValueOf2 == null ? -1 : numValueOf2.intValue());
        if (strBlocked != null) {
            return strBlocked;
        }
        if (i == 0) {
            resultSendBundle = session.sendVehicleState(new OemVehicleStateTransport.StateValue(ENTER, 2), "suspension easy entry");
        } else {
            resultSendBundle = session.sendBundle(map, "suspension drive " + str);
        }
        if (!resultSendBundle.accepted()) {
            return "Команда не отправлена";
        }
        if (i == 0) {
            ModeFeedbackDecoder.Feedback feedbackDecode = ModeFeedbackDecoder.decode(545, numValueOf2 != null ? numValueOf2.intValue() : -1);
            str2 = feedbackDecode == null ? null : feedbackDecode.mode;
        } else {
            str2 = str;
        }
        ApplyEngine.noteVehicleMode("driveMode", str2);
        return DriveSelectionStore.record(this.context, str2, DriveSelectionPolicy.WIDGET) ? "" : "Команда отправлена, но режим не сохранён";
    }

    /* JADX WARN: Code duplicated, block: B:14:0x0050  */
    private void publish() {
        boolean z;
        if (this.client == null || this.closed) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(SuspensionWidgetProtocol.HEIGHT, this.height);
        bundle.putInt(SuspensionWidgetProtocol.DIRECTION, this.direction);
        bundle.putInt(SuspensionWidgetProtocol.MAINTENANCE, this.maintenance);
        bundle.putInt(SuspensionWidgetProtocol.DRIVE, this.drive);
        bundle.putString(SuspensionWidgetProtocol.LOWEST_BLOCKED_REASON, SuspensionWidgetPolicy.lowestBlockedReason(this.drive, this.inhibit));
        bundle.putInt(SuspensionWidgetProtocol.PENDING, this.pending);
        z = SuspensionWidgetPolicy.validHeight(this.height) && this.maintenance == 1 && !this.commandInFlight;
        bundle.putBoolean(SuspensionWidgetProtocol.AVAILABLE, z);
        bundle.putString(SuspensionWidgetProtocol.MESSAGE, this.message);
        Message messageObtain = Message.obtain((Handler) null, 93);
        messageObtain.setData(bundle);
        try {
            this.client.send(messageObtain);
        } catch (RemoteException unused) {
            stopWatching();
        }
    }

    private void stopWatching() {
        CanBusEventHub.Subscription subscription = this.subscription;
        if (subscription != null) {
            subscription.close();
        }
        this.subscription = null;
        this.client = null;
        this.height = -1;
        this.maintenance = -1;
        this.drive = -1;
        this.inhibit = -1;
        this.worker.removeCallbacks(this.timeout);
        this.pending = -1;
    }

    void close() {
        this.closed = true;
        this.worker.post(new Runnable() { // from class: ru.big.town.anative.SuspensionWidgetController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SuspensionWidgetController.this.m2132lambda$close$7$rubigtownanativeSuspensionWidgetController();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$close$7$ru-big-town-anative-SuspensionWidgetController, reason: not valid java name */
    /* synthetic */ void m2132lambda$close$7$rubigtownanativeSuspensionWidgetController() {
        stopWatching();
        this.thread.quitSafely();
    }
}
