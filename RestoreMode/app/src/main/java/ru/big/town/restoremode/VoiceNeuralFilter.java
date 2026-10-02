package ru.big.town.restoremode;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.io.IOException;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceNeuralFilter implements AutoCloseable {
    private int attenuationDb;
    private DeepFilter deepFilter;
    private Pointer state;
    private boolean warmed;

    interface DeepFilter extends Library {
        Pointer voice_df_create(float f);

        void voice_df_free(Pointer pointer);

        int voice_df_process(Pointer pointer, float[] fArr, float[] fArr2);

        int voice_df_reset(Pointer pointer, float f);
    }

    VoiceNeuralFilter(int i) throws IOException {
        reset(i);
    }

    void reset(int i) throws IOException {
        if (i < 0 || i > 30) {
            throw new IllegalArgumentException("Invalid DeepFilter attenuation");
        }
        this.attenuationDb = i;
        if (i == 0 && this.state == null) {
            return;
        }
        Pointer pointer = this.state;
        if (pointer == null) {
            DeepFilter deepFilter = (DeepFilter) Native.load("voyah_df", DeepFilter.class);
            this.deepFilter = deepFilter;
            Pointer pointerVoice_df_create = deepFilter.voice_df_create(i);
            this.state = pointerVoice_df_create;
            if (pointerVoice_df_create == null) {
                throw new IOException("Cannot initialize DeepFilterNet3");
            }
            return;
        }
        if (this.deepFilter.voice_df_reset(pointer, i) != 0) {
            throw new IOException("Cannot reset DeepFilterNet3");
        }
    }

    void warmUp() throws IOException {
        if (this.attenuationDb == 0 || this.warmed) {
            return;
        }
        float[] fArr = new float[480];
        float[] fArr2 = new float[480];
        for (int i = 0; i < 480; i++) {
            fArr[i] = ((float) Math.sin(((double) i) * 0.17d)) * 0.01f;
        }
        for (int i2 = 0; i2 < 20; i2++) {
            process(fArr, fArr2);
        }
        reset(this.attenuationDb);
        this.warmed = true;
    }

    void process(float[] fArr, float[] fArr2) throws IOException {
        if (fArr.length != 480 || fArr2.length != 480) {
            throw new IllegalArgumentException("10 ms frames required");
        }
        if (this.attenuationDb > 0) {
            if (this.deepFilter.voice_df_process(this.state, fArr, fArr2) != 0) {
                throw new IOException("DeepFilter processing failed");
            }
        } else {
            System.arraycopy(fArr, 0, fArr2, 0, 480);
        }
        for (int i = 0; i < 480; i++) {
            if (!Float.isFinite(fArr2[i])) {
                throw new IOException("Noise filter produced invalid audio");
            }
            fArr2[i] = Math.max(-1.0f, Math.min(1.0f, fArr2[i]));
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        Pointer pointer = this.state;
        if (pointer == null) {
            return;
        }
        DeepFilter deepFilter = this.deepFilter;
        if (deepFilter != null) {
            deepFilter.voice_df_free(pointer);
        }
        this.state = null;
        this.warmed = false;
    }
}
