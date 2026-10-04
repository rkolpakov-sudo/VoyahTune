package ru.big.town.restoremode;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceSounds {
    private final int activation;
    private final int error;
    private final Set<Integer> loaded = new HashSet();
    private int pending;
    private final SoundPool pool;
    private boolean released;
    private int stream;
    private final int success;

    VoiceSounds(Context context) {
        SoundPool soundPoolBuild = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(new AudioAttributes.Builder().setUsage(13).setContentType(4).build()).build();
        this.pool = soundPoolBuild;
        soundPoolBuild.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() { // from class: ru.big.town.restoremode.VoiceSounds$$ExternalSyntheticLambda0
            @Override // android.media.SoundPool.OnLoadCompleteListener
            public final void onLoadComplete(SoundPool soundPool, int i, int i2) {
                VoiceSounds.this.m1991lambda$new$0$rubigtownrestoremodeVoiceSounds(soundPool, i, i2);
            }
        });
        this.activation = soundPoolBuild.load(context, R.raw.voice_activation, 1);
        this.success = soundPoolBuild.load(context, R.raw.voice_success, 1);
        this.error = soundPoolBuild.load(context, R.raw.voice_error, 1);
    }

    /* JADX INFO: renamed from: lambda$new$0$ru-big-town-restoremode-VoiceSounds, reason: not valid java name */
    /* synthetic */ void m1991lambda$new$0$rubigtownrestoremodeVoiceSounds(SoundPool soundPool, int i, int i2) {
        if (this.released || i2 != 0) {
            return;
        }
        this.loaded.add(Integer.valueOf(i));
        if (this.pending == i) {
            play(i);
        }
    }

    void activation() {
        play(this.activation);
    }

    void success() {
        play(this.success);
    }

    void error() {
        play(this.error);
    }

    private void play(int i) {
        if (this.released) {
            return;
        }
        stop();
        if (this.loaded.contains(Integer.valueOf(i))) {
            this.stream = this.pool.play(i, 0.45f, 0.45f, 1, 0, 1.0f);
        } else {
            this.pending = i;
        }
    }

    void stop() {
        if (this.released) {
            return;
        }
        this.pending = 0;
        int i = this.stream;
        if (i != 0) {
            this.pool.stop(i);
            this.stream = 0;
        }
    }

    void release() {
        if (this.released) {
            return;
        }
        stop();
        this.released = true;
        this.pool.release();
        this.loaded.clear();
    }
}
