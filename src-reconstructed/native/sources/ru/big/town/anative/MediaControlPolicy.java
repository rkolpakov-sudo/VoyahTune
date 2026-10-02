package ru.big.town.anative;

import android.car.Car;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
final class MediaControlPolicy {
    static final int KEY_NEXT = 87;
    static final int KEY_PAUSE = 127;
    static final int KEY_PLAY_PAUSE = 85;
    static final int KEY_PREVIOUS = 88;
    static final int STATE_ACTIVE = 1;
    static final int STATE_INACTIVE = 0;
    static final int STATE_UNKNOWN = -1;

    enum Command {
        PLAY_PAUSE,
        PAUSE_ONLY,
        NEXT,
        PREVIOUS
    }

    enum Operation {
        NONE,
        TARGET_KEY,
        TRANSPORT_PAUSE,
        KEYMANAGER_KEY,
        NATIVE_QG
    }

    static int pauseKeyWithAudioEvidence(int i, boolean z) {
        return (i == 127 && z) ? KEY_PLAY_PAUSE : i;
    }

    static final class Candidate {
        final boolean bridge;
        final boolean nativeQinggan;
        final String packageName;
        final int playbackClass;
        final boolean supportsPause;
        final String tokenId;

        Candidate(String str, String str2, int i, boolean z, boolean z2, boolean z3) {
            this.tokenId = str == null ? "" : str;
            this.packageName = str2 == null ? "" : str2;
            this.playbackClass = i;
            this.supportsPause = z;
            this.bridge = z2;
            this.nativeQinggan = z3;
        }
    }

    static final class Plan {
        final int keyCode;
        final Operation operation;

        Plan(Operation operation, int i) {
            this.operation = operation;
            this.keyCode = i;
        }
    }

    private MediaControlPolicy() {
    }

    static int chooseTarget(List<Candidate> list, String str, boolean z) {
        if (list == null || list.isEmpty()) {
            return -1;
        }
        int iIndexOfToken = indexOfToken(list, str);
        if (!z || iIndexOfToken < 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).playbackClass == 1) {
                    return i;
                }
            }
            if (iIndexOfToken < 0) {
                return 0;
            }
        }
        return iIndexOfToken;
    }

    static Plan plan(Candidate candidate, Command command) {
        if (candidate == null || command == null) {
            return new Plan(Operation.NATIVE_QG, 0);
        }
        if (command == Command.PAUSE_ONLY) {
            if (candidate.playbackClass == 0) {
                return new Plan(Operation.NONE, 0);
            }
            if (candidate.playbackClass == -1) {
                return new Plan(candidate.bridge ? Operation.KEYMANAGER_KEY : Operation.TARGET_KEY, 127);
            }
        }
        if (candidate.nativeQinggan) {
            return new Plan(Operation.NATIVE_QG, keyFor(command));
        }
        if (candidate.bridge) {
            return new Plan(Operation.KEYMANAGER_KEY, keyFor(command));
        }
        if (command == Command.PAUSE_ONLY && candidate.supportsPause) {
            return new Plan(Operation.TRANSPORT_PAUSE, 0);
        }
        return new Plan(Operation.TARGET_KEY, keyFor(command));
    }

    static int keyFor(Command command) {
        if (command == null) {
            return 0;
        }
        int iOrdinal = command.ordinal();
        if (iOrdinal == 0 || iOrdinal == 1) {
            return KEY_PLAY_PAUSE;
        }
        if (iOrdinal == 2) {
            return KEY_NEXT;
        }
        if (iOrdinal != 3) {
            return 0;
        }
        return KEY_PREVIOUS;
    }

    static boolean isBridgePackage(String str) {
        String lowerCase = str == null ? "" : str.toLowerCase(Locale.US);
        return lowerCase.contains("carplay") || lowerCase.contains("autokit") || lowerCase.contains("zlink") || lowerCase.contains("speedplay") || lowerCase.contains("phonemirror") || lowerCase.contains(Car.PROJECTION_SERVICE);
    }

    static boolean isNativeQingganPackage(String str) {
        if (str == null) {
            return false;
        }
        return str.equals("android") || str.startsWith("com.android.") || str.startsWith("com.qinggan.");
    }

    private static int indexOfToken(List<Candidate> list, String str) {
        if (str != null && !str.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                if (str.equals(list.get(i).tokenId)) {
                    return i;
                }
            }
        }
        return -1;
    }
}
