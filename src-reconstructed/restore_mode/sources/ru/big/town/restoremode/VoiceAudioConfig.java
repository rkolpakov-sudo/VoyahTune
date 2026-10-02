package ru.big.town.restoremode;

import android.content.SharedPreferences;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceAudioConfig {
    static final String DEEP_FILTER_DB_KEY = "voiceDeepFilterAttenuationDb";
    static final int DEFAULT_DEEP_FILTER_DB = 6;
    static final int MAX_DEEP_FILTER_DB = 30;
    final int deepFilterDb;

    VoiceAudioConfig(int i) {
        this.deepFilterDb = Math.max(0, Math.min(30, i));
    }

    static VoiceAudioConfig read(SharedPreferences sharedPreferences) {
        return new VoiceAudioConfig(sharedPreferences.getInt(DEEP_FILTER_DB_KEY, 6));
    }

    String label() {
        return "Zipformer2 · русский 0.54 INT8 + DeepFilterNet3 · " + this.deepFilterDb + " дБ";
    }
}
