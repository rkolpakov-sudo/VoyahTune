package ru.big.town.anative;

import android.car.Car;
import android.content.Context;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import kotlinx.coroutines.scheduling.WorkQueueKt;

/* JADX INFO: loaded from: classes2.dex */
final class MediaControlRouter {
    private static final int MAX_SELECTION_ATTEMPTS = 3;
    static final String ROUTE_DIRECT = "direct";
    static final String ROUTE_KEYMANAGER = "keymanager";
    static final String ROUTE_NATIVE = "native";
    static final String ROUTE_NOOP = "noop";
    private static final String TAG = "$$$ MediaControlRouter $$$";
    private static boolean activeSnapshotInitialized;
    private static long activeSnapshotRevision;
    private static long observerGeneration;
    private static boolean stickyPinned;
    private static MediaSession.Token stickyToken;
    private static long targetRevision;
    private static final Object TARGET_LOCK = new Object();
    private static final List<MediaSession.Token> lastActiveTokens = new ArrayList();

    private MediaControlRouter() {
    }

    static final class Result {
        final int keyCode;
        final String packageName;
        final int playbackClass;
        final String route;

        Result(String str, int i, String str2, int i2) {
            this.route = str;
            this.keyCode = i;
            this.packageName = str2 == null ? "" : str2;
            this.playbackClass = i2;
        }

        Bundle toBundle() {
            Bundle bundle = new Bundle();
            bundle.putString("route", this.route);
            bundle.putInt("keyCode", this.keyCode);
            bundle.putString(Car.PACKAGE_SERVICE, this.packageName);
            bundle.putInt("playbackClass", this.playbackClass);
            return bundle;
        }
    }

    private static final class SelectionAttempt {
        final MediaController selected;
        final boolean stable;

        SelectionAttempt(MediaController mediaController, boolean z) {
            this.selected = mediaController;
            this.stable = z;
        }
    }

    static Result dispatch(Context context, MediaControlPolicy.Command command) {
        if (context == null || command == null) {
            return nativeFallback(command, "", -1);
        }
        try {
            MediaSessionManager mediaSessionManager = (MediaSessionManager) context.getSystemService("media_session");
            if (mediaSessionManager == null) {
                return nativeFallback(command, "", -1);
            }
            List<MediaController> activeSessions = mediaSessionManager.getActiveSessions(null);
            if (activeSessions == null) {
                activeSessions = Collections.emptyList();
            }
            MediaController mediaControllerSelectController = selectController(activeSessions);
            logDecisionInput(command, activeSessions, mediaControllerSelectController);
            if (mediaControllerSelectController == null) {
                return command == MediaControlPolicy.Command.PAUSE_ONLY ? new Result(ROUTE_KEYMANAGER, WorkQueueKt.MASK, "", -1) : nativeFallback(command, "", -1);
            }
            MediaControlPolicy.Candidate candidate = candidate(mediaControllerSelectController, false);
            Result resultExecutePlan = executePlan(mediaControllerSelectController, candidate, command, MediaControlPolicy.plan(candidate, command));
            Log.i(TAG, "dispatch " + command + " -> route=" + resultExecutePlan.route + " key=" + resultExecutePlan.keyCode + " pkg=" + resultExecutePlan.packageName + " stateClass=" + resultExecutePlan.playbackClass);
            return resultExecutePlan;
        } catch (SecurityException e) {
            Log.w(TAG, "dispatch " + command + ": no MEDIA_CONTENT_CONTROL: " + e.getMessage());
            return safeFailure(command);
        } catch (Throwable th) {
            Log.w(TAG, "dispatch " + command + ": " + th.getMessage());
            return safeFailure(command);
        }
    }

    static void notePlaying(MediaController mediaController) {
        if (mediaController == null) {
            return;
        }
        notePlaying(mediaController.getSessionToken());
    }

    static void notePlaying(MediaSession.Token token) {
        notePlaying(token, 0L);
    }

    static void notePlaying(MediaSession.Token token, long j) {
        if (token == null) {
            return;
        }
        synchronized (TARGET_LOCK) {
            if (canMutateObserverStateLocked(j)) {
                stickyToken = token;
                stickyPinned = false;
                targetRevision++;
            }
        }
    }

    static void activateObserverGeneration(long j) {
        if (j <= 0) {
            return;
        }
        synchronized (TARGET_LOCK) {
            observerGeneration = j;
        }
    }

    static void deactivateObserverGeneration(long j) {
        if (j <= 0) {
            return;
        }
        synchronized (TARGET_LOCK) {
            if (observerGeneration == j) {
                observerGeneration = 0L;
            }
        }
    }

    static MediaController selectController(List<MediaController> list) {
        return selectController(list, 0L);
    }

    static MediaController selectController(List<MediaController> list, long j) {
        if (list == null) {
            list = Collections.emptyList();
        }
        if (!isObserverGenerationActive(j)) {
            return null;
        }
        for (int i = 0; i < 3; i++) {
            SelectionAttempt selectionAttemptSelectControllerOnce = selectControllerOnce(list, j);
            if (selectionAttemptSelectControllerOnce.stable) {
                return selectionAttemptSelectControllerOnce.selected;
            }
            if (!isObserverGenerationActive(j)) {
                return null;
            }
        }
        return null;
    }

    private static SelectionAttempt selectControllerOnce(List<MediaController> list, long j) {
        observeActiveTransitions(list, j);
        Object obj = TARGET_LOCK;
        synchronized (obj) {
            MediaController mediaController = null;
            boolean z = true;
            if (!canMutateObserverStateLocked(j)) {
                return new SelectionAttempt(null, true);
            }
            MediaSession.Token sessionToken = stickyToken;
            boolean z2 = stickyPinned;
            long j2 = targetRevision;
            if (list.isEmpty()) {
                synchronized (obj) {
                    if (!canMutateObserverStateLocked(j) || targetRevision != j2) {
                        z = false;
                    }
                    if (z) {
                        stickyToken = null;
                        stickyPinned = false;
                        targetRevision++;
                    }
                }
                return new SelectionAttempt(null, z);
            }
            ArrayList arrayList = new ArrayList(list.size());
            boolean z3 = false;
            boolean z4 = false;
            for (int i = 0; i < list.size(); i++) {
                MediaController mediaController2 = list.get(i);
                boolean zSameToken = sameToken(mediaController2.getSessionToken(), sessionToken);
                z3 |= zSameToken;
                MediaControlPolicy.Candidate candidate = candidate(mediaController2, zSameToken);
                z4 |= zSameToken && candidate.playbackClass == 1;
                arrayList.add(candidate);
            }
            if (!z3 && sessionToken != null) {
                sessionToken = null;
                z2 = false;
            }
            boolean z5 = z2;
            if (z2 && z4) {
                z2 = false;
                z5 = false;
            }
            int iChooseTarget = MediaControlPolicy.chooseTarget(arrayList, "sticky", z2 || z4);
            if (iChooseTarget >= 0 && iChooseTarget < list.size()) {
                mediaController = list.get(iChooseTarget);
            }
            if (mediaController != null && ((MediaControlPolicy.Candidate) arrayList.get(iChooseTarget)).playbackClass == 1) {
                sessionToken = mediaController.getSessionToken();
                z5 = false;
            }
            synchronized (TARGET_LOCK) {
                if (!canMutateObserverStateLocked(j) || targetRevision != j2) {
                    z = false;
                }
                if (z) {
                    stickyToken = sessionToken;
                    stickyPinned = z5;
                    targetRevision++;
                }
            }
            return new SelectionAttempt(mediaController, z);
        }
    }

    private static void observeActiveTransitions(List<MediaController> list, long j) {
        MediaSession.Token token;
        synchronized (TARGET_LOCK) {
            if (canMutateObserverStateLocked(j)) {
                long j2 = targetRevision;
                long j3 = activeSnapshotRevision;
                boolean z = activeSnapshotInitialized;
                ArrayList arrayList = new ArrayList(lastActiveTokens);
                ArrayList arrayList2 = new ArrayList();
                for (MediaController mediaController : list) {
                    if (isActiveState(safePlaybackState(mediaController))) {
                        arrayList2.add(mediaController.getSessionToken());
                    }
                }
                if (!z) {
                    token = null;
                    break;
                }
                Iterator it = arrayList2.iterator();
                do {
                    if (!it.hasNext()) {
                        token = null;
                        break;
                    }
                    token = (MediaSession.Token) it.next();
                } while (containsToken(arrayList, token));
                synchronized (TARGET_LOCK) {
                    if (canMutateObserverStateLocked(j) && activeSnapshotRevision == j3) {
                        List<MediaSession.Token> list2 = lastActiveTokens;
                        list2.clear();
                        list2.addAll(arrayList2);
                        activeSnapshotInitialized = true;
                        activeSnapshotRevision++;
                        if (token != null) {
                            long j4 = targetRevision;
                            if (j4 == j2) {
                                stickyToken = token;
                                stickyPinned = false;
                                targetRevision = j4 + 1;
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean isObserverGenerationActive(long j) {
        boolean z = true;
        if (j == 0) {
            return true;
        }
        synchronized (TARGET_LOCK) {
            if (observerGeneration != j) {
                z = false;
            }
        }
        return z;
    }

    private static boolean canMutateObserverStateLocked(long j) {
        return j == 0 || observerGeneration == j;
    }

    static boolean isActiveState(PlaybackState playbackState) {
        return playbackClass(playbackState) == 1;
    }

    /* JADX INFO: renamed from: ru.big.town.anative.MediaControlRouter$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$ru$big$town$anative$MediaControlPolicy$Operation;

        static {
            int[] iArr = new int[MediaControlPolicy.Operation.values().length];
            $SwitchMap$ru$big$town$anative$MediaControlPolicy$Operation = iArr;
            try {
                iArr[MediaControlPolicy.Operation.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$ru$big$town$anative$MediaControlPolicy$Operation[MediaControlPolicy.Operation.NATIVE_QG.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$ru$big$town$anative$MediaControlPolicy$Operation[MediaControlPolicy.Operation.KEYMANAGER_KEY.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$ru$big$town$anative$MediaControlPolicy$Operation[MediaControlPolicy.Operation.TRANSPORT_PAUSE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$ru$big$town$anative$MediaControlPolicy$Operation[MediaControlPolicy.Operation.TARGET_KEY.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    private static Result executePlan(MediaController mediaController, MediaControlPolicy.Candidate candidate, MediaControlPolicy.Command command, MediaControlPolicy.Plan plan) {
        int i = AnonymousClass1.$SwitchMap$ru$big$town$anative$MediaControlPolicy$Operation[plan.operation.ordinal()];
        if (i == 1) {
            return new Result(ROUTE_NOOP, 0, candidate.packageName, candidate.playbackClass);
        }
        if (i == 2) {
            rememberCommandTarget(mediaController, command);
            return new Result(ROUTE_NATIVE, plan.keyCode, candidate.packageName, candidate.playbackClass);
        }
        if (i == 3) {
            rememberCommandTarget(mediaController, command);
            return new Result(ROUTE_KEYMANAGER, plan.keyCode, candidate.packageName, candidate.playbackClass);
        }
        if (i == 4) {
            mediaController.getTransportControls().pause();
            rememberCommandTarget(mediaController, command);
            return new Result(ROUTE_DIRECT, 0, candidate.packageName, candidate.playbackClass);
        }
        if (i == 5) {
            if (!dispatchTargetedKey(mediaController, plan.keyCode)) {
                rememberCommandTarget(mediaController, command);
                return new Result(ROUTE_KEYMANAGER, plan.keyCode, candidate.packageName, candidate.playbackClass);
            }
            rememberCommandTarget(mediaController, command);
            return new Result(ROUTE_DIRECT, plan.keyCode, candidate.packageName, candidate.playbackClass);
        }
        return new Result(ROUTE_NATIVE, plan.keyCode, candidate.packageName, candidate.playbackClass);
    }

    private static boolean dispatchTargetedKey(final MediaController mediaController, final int i) {
        if (mediaController != null && i != 0) {
            final long jUptimeMillis = SystemClock.uptimeMillis();
            MediaKeyPairDelivery.Outcome outcomeDispatch = MediaKeyPairDelivery.dispatch(new MediaKeyPairDelivery.EventSender() { // from class: ru.big.town.anative.MediaControlRouter$$ExternalSyntheticLambda0
                @Override // ru.big.town.anative.MediaKeyPairDelivery.EventSender
                public final boolean send(boolean z) {
                    MediaController mediaController2 = mediaController;
                    long j = jUptimeMillis;
                    return mediaController2.dispatchMediaButtonEvent(new KeyEvent(j, j, !z ? 1 : 0, i, 0));
                }
            });
            if (outcomeDispatch == MediaKeyPairDelivery.Outcome.DOWN_ONLY) {
                Log.w(TAG, "target key UP failed/not accepted after DOWN; no fallback, key=" + i);
            }
            if (outcomeDispatch != MediaKeyPairDelivery.Outcome.NOT_SENT) {
                return true;
            }
        }
        return false;
    }

    private static void rememberCommandTarget(MediaController mediaController, MediaControlPolicy.Command command) {
        if (mediaController == null) {
            return;
        }
        synchronized (TARGET_LOCK) {
            stickyToken = mediaController.getSessionToken();
            stickyPinned = command == MediaControlPolicy.Command.PAUSE_ONLY || command == MediaControlPolicy.Command.PLAY_PAUSE;
            targetRevision++;
        }
    }

    private static MediaControlPolicy.Candidate candidate(MediaController mediaController, boolean z) {
        String strSafePackage = mediaController == null ? "" : safePackage(mediaController);
        PlaybackState playbackStateSafePlaybackState = mediaController == null ? null : safePlaybackState(mediaController);
        long actions = playbackStateSafePlaybackState == null ? 0L : playbackStateSafePlaybackState.getActions();
        boolean zIsBridgePackage = MediaControlPolicy.isBridgePackage(strSafePackage);
        boolean z2 = false;
        if (!zIsBridgePackage && MediaControlPolicy.isNativeQingganPackage(strSafePackage)) {
            z2 = true;
        }
        return new MediaControlPolicy.Candidate(z ? "sticky" : "other", strSafePackage, playbackClass(playbackStateSafePlaybackState), (actions & 2) != 0, zIsBridgePackage, z2);
    }

    private static int playbackClass(PlaybackState playbackState) {
        int state;
        if (playbackState != null && (state = playbackState.getState()) != 0) {
            switch (state) {
                case 3:
                case 4:
                case 5:
                case 6:
                case 8:
                case 9:
                case 10:
                case 11:
                    return 1;
                case 7:
                    break;
                default:
                    return 0;
            }
        }
        return -1;
    }

    private static Result safeFailure(MediaControlPolicy.Command command) {
        if (command == MediaControlPolicy.Command.PAUSE_ONLY) {
            return new Result(ROUTE_KEYMANAGER, WorkQueueKt.MASK, "", -1);
        }
        return nativeFallback(command, "", -1);
    }

    private static Result nativeFallback(MediaControlPolicy.Command command, String str, int i) {
        return new Result(ROUTE_NATIVE, MediaControlPolicy.keyFor(command), str, i);
    }

    private static void logDecisionInput(MediaControlPolicy.Command command, List<MediaController> list, MediaController mediaController) {
        StringBuilder sbAppend = new StringBuilder("command=").append(command).append(" sessions=[");
        for (int i = 0; i < list.size(); i++) {
            MediaController mediaController2 = list.get(i);
            PlaybackState playbackStateSafePlaybackState = safePlaybackState(mediaController2);
            if (i > 0) {
                sbAppend.append(", ");
            }
            sbAppend.append(i).append(':').append(safePackage(mediaController2)).append(" state=").append(playbackStateSafePlaybackState == null ? "null" : Integer.valueOf(playbackStateSafePlaybackState.getState())).append(" actions=0x").append(Long.toHexString(playbackStateSafePlaybackState == null ? 0L : playbackStateSafePlaybackState.getActions()));
        }
        sbAppend.append("] selected=").append(mediaController == null ? "none" : safePackage(mediaController));
        Log.i(TAG, sbAppend.toString());
    }

    private static String safePackage(MediaController mediaController) {
        if (mediaController != null) {
            try {
                if (mediaController.getPackageName() != null) {
                    return mediaController.getPackageName();
                }
            } catch (Exception unused) {
            }
        }
        return "";
    }

    private static PlaybackState safePlaybackState(MediaController mediaController) {
        if (mediaController == null) {
            return null;
        }
        try {
            return mediaController.getPlaybackState();
        } catch (Exception unused) {
            return null;
        }
    }

    private static boolean sameToken(MediaSession.Token token, MediaSession.Token token2) {
        if (token == null || token2 == null) {
            return token == token2;
        }
        try {
            return token.equals(token2);
        } catch (Exception unused) {
            return false;
        }
    }

    private static boolean containsToken(List<MediaSession.Token> list, MediaSession.Token token) {
        Iterator<MediaSession.Token> it = list.iterator();
        while (it.hasNext()) {
            if (sameToken(it.next(), token)) {
                return true;
            }
        }
        return false;
    }
}
