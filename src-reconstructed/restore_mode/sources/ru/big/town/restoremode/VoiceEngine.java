package ru.big.town.restoremode;

import android.content.Context;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceEngine implements AutoCloseable {
    private final VoiceNeuralFilter filter;

    VoiceEngine(Context context, int i) throws Exception {
        VoiceNeuralFilter voiceNeuralFilter = null;
        try {
            VoiceModels.warmUp(context);
            VoiceNeuralFilter voiceNeuralFilter2 = new VoiceNeuralFilter(i);
            try {
                voiceNeuralFilter2.warmUp();
                this.filter = voiceNeuralFilter2;
            } catch (Exception | LinkageError e) {
                e = e;
                voiceNeuralFilter = voiceNeuralFilter2;
                if (voiceNeuralFilter != null) {
                    voiceNeuralFilter.close();
                }
                VoiceModels.release();
                throw e;
            }
        } catch (Exception | LinkageError e2) {
            e = e2;
        }
    }

    VoiceNeuralFilter prepareFilter(int i) throws Exception {
        this.filter.reset(i);
        this.filter.warmUp();
        return this.filter;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        try {
            this.filter.close();
        } finally {
            VoiceModels.release();
        }
    }
}
