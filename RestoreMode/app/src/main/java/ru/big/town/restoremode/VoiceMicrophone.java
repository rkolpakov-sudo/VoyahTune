package ru.big.town.restoremode;

import android.media.AudioRecord;
import java.io.IOException;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceMicrophone implements VoiceSessionControl.Capture {
    private final AudioRecord recorder;

    VoiceMicrophone() throws IOException {
        int minBufferSize = AudioRecord.getMinBufferSize(48000, 16, 2);
        if (minBufferSize <= 0) {
            throw new IOException("Unsupported microphone format");
        }
        AudioRecord audioRecord = new AudioRecord(6, 48000, 16, 2, Math.max(48000, minBufferSize));
        this.recorder = audioRecord;
        try {
            if (audioRecord.getState() != 1) {
                throw new IOException("Microphone unavailable");
            }
            audioRecord.startRecording();
            if (audioRecord.getRecordingState() != 3) {
                throw new IOException("Recording failed");
            }
        } catch (IOException | RuntimeException e) {
            this.recorder.release();
            throw e;
        }
    }

    @Override // ru.big.town.restoremode.VoiceSessionControl.Capture
    public int read(short[] sArr, int i, int i2) {
        return this.recorder.read(sArr, i, i2, 1);
    }

    @Override // ru.big.town.restoremode.VoiceSessionControl.Capture
    public void close() {
        try {
            if (this.recorder.getRecordingState() == 3) {
                this.recorder.stop();
            }
        } catch (RuntimeException unused) {
        } finally {
            this.recorder.release();
        }
    }
}
