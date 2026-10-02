package ru.big.town.restoremode;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceRecording {
    static final String DIRECTORY = "voice-preview";
    private final ByteArrayOutputStream pcm = new ByteArrayOutputStream(320000);

    VoiceRecording() {
    }

    void append(float[] fArr) {
        if (this.pcm.size() + (fArr.length * 2) > 320000) {
            throw new IllegalStateException("Recording exceeds 10 seconds");
        }
        for (float f : fArr) {
            int iRound = Math.round(Math.max(-32768.0f, Math.min(32767.0f, f * 32768.0f)));
            this.pcm.write(iRound & 255);
            this.pcm.write((iRound >> 8) & 255);
        }
    }

    boolean empty() {
        return this.pcm.size() == 0;
    }

    File save(File file) throws IOException {
        if (!file.isDirectory() && !file.mkdirs()) {
            throw new IOException("Cannot create recording cache");
        }
        File fileCreateTempFile = File.createTempFile("session-", ".wav", file);
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileCreateTempFile));
            try {
                bufferedOutputStream.write(new byte[]{82, 73, 70, 70});
                le(bufferedOutputStream, this.pcm.size() + 36, 4);
                bufferedOutputStream.write(new byte[]{87, 65, 86, 69, 102, 109, 116, 32});
                le(bufferedOutputStream, 16, 4);
                le(bufferedOutputStream, 1, 2);
                le(bufferedOutputStream, 1, 2);
                le(bufferedOutputStream, 16000, 4);
                le(bufferedOutputStream, 32000, 4);
                le(bufferedOutputStream, 2, 2);
                le(bufferedOutputStream, 16, 2);
                bufferedOutputStream.write(new byte[]{100, 97, 116, 97});
                le(bufferedOutputStream, this.pcm.size(), 4);
                this.pcm.writeTo(bufferedOutputStream);
                bufferedOutputStream.close();
                return fileCreateTempFile;
            } catch (Throwable th) {
                try {
                    bufferedOutputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (IOException e) {
            fileCreateTempFile.delete();
            throw e;
        }
    }

    private static void le(OutputStream outputStream, int i, int i2) throws IOException {
        for (int i3 = 0; i3 < i2; i3++) {
            outputStream.write((i >>> (i3 * 8)) & 255);
        }
    }

    static void clear(File file) {
        File[] fileArrListFiles = file.listFiles(new FilenameFilter() { // from class: ru.big.town.restoremode.VoiceRecording$$ExternalSyntheticLambda0
            @Override // java.io.FilenameFilter
            public final boolean accept(File file2, String str) {
                return VoiceRecording.lambda$clear$0(file2, str);
            }
        });
        if (fileArrListFiles != null) {
            for (File file2 : fileArrListFiles) {
                file2.delete();
            }
        }
    }

    static /* synthetic */ boolean lambda$clear$0(File file, String str) {
        return str.startsWith("session-") && str.endsWith(".wav");
    }
}
