package ru.big.town.restoremode;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceSteeringPolicy {
    static final String ACTION = "voice_assistant";
    static final String LONG = "long";
    static final String PRESS_KEY = "voiceAssistantSteeringPress";
    static final String SHORT = "short";

    VoiceSteeringPolicy() {
    }

    static String normalize(String str) {
        return SHORT.equals(str) ? SHORT : LONG;
    }

    static boolean ownsSlot(boolean z, String str, String str2) {
        return z && (SHORT.equals(normalize(str)) ? "steerVoiceShort" : "steerVoiceLong").equals(str2);
    }

    static String publishedAction(boolean z, String str, String str2, String str3) {
        return ownsSlot(z, str, str2) ? ACTION : str3;
    }
}
