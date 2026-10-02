package ru.big.town.restoremode;

import android.content.Context;
import com.k2fsa.sherpa.onnx.OfflineModelConfig;
import com.k2fsa.sherpa.onnx.OfflineRecognizer;
import com.k2fsa.sherpa.onnx.OfflineRecognizerConfig;
import com.k2fsa.sherpa.onnx.OfflineStream;
import com.k2fsa.sherpa.onnx.Vad;
import com.k2fsa.sherpa.onnx.VadModelConfig;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceModels {
    private static Vad detector;
    private static boolean warmed;
    private static OfflineRecognizer zipformer;

    interface Session extends AutoCloseable {
        boolean accept(float[] fArr) throws Exception;

        @Override // java.lang.AutoCloseable
        void close();

        String finish() throws Exception;

        String partial() throws Exception;
    }

    VoiceModels() {
    }

    private static void load(Context context) throws Exception {
        if (zipformer == null) {
            File fileUnpack = unpack(context, "zipformer-ru-0.54-int8");
            OfflineRecognizerConfig offlineRecognizerConfig = new OfflineRecognizerConfig();
            offlineRecognizerConfig.getFeatConfig().setSampleRate(16000);
            offlineRecognizerConfig.getFeatConfig().setDither(3.0E-5f);
            offlineRecognizerConfig.setDecodingMethod("modified_beam_search");
            offlineRecognizerConfig.setMaxActivePaths(10);
            offlineRecognizerConfig.setHotwordsScore(1.0f);
            OfflineModelConfig modelConfig = offlineRecognizerConfig.getModelConfig();
            modelConfig.setNumThreads(2);
            modelConfig.setProvider("cpu");
            modelConfig.setTokens(new File(fileUnpack, "tokens.txt").getAbsolutePath());
            modelConfig.setModelingUnit("bpe");
            modelConfig.setBpeVocab(new File(fileUnpack, "bpe.vocab").getAbsolutePath());
            modelConfig.getTransducer().setEncoder(new File(fileUnpack, "encoder.int8.onnx").getAbsolutePath());
            modelConfig.getTransducer().setDecoder(new File(fileUnpack, "decoder.int8.onnx").getAbsolutePath());
            modelConfig.getTransducer().setJoiner(new File(fileUnpack, "joiner.int8.onnx").getAbsolutePath());
            zipformer = new OfflineRecognizer(null, offlineRecognizerConfig);
        }
        if (detector == null) {
            VadModelConfig vadModelConfig = new VadModelConfig();
            vadModelConfig.setNumThreads(1);
            vadModelConfig.setSampleRate(16000);
            vadModelConfig.getSileroVadModelConfig().setModel("silero_vad.onnx");
            vadModelConfig.getSileroVadModelConfig().setMinSilenceDuration(0.7f);
            vadModelConfig.getSileroVadModelConfig().setMinSpeechDuration(0.15f);
            vadModelConfig.getSileroVadModelConfig().setMaxSpeechDuration(10.0f);
            detector = new Vad(context.getAssets(), vadModelConfig);
        }
    }

    static void warmUp(Context context) throws Exception {
        load(context);
        if (warmed) {
            return;
        }
        OfflineStream offlineStreamCreateStream = zipformer.createStream();
        try {
            offlineStreamCreateStream.acceptWaveform(new float[16000], 16000);
            zipformer.decode(offlineStreamCreateStream);
            zipformer.getResult(offlineStreamCreateStream);
            detector.acceptWaveform(new float[16000]);
            offlineStreamCreateStream.release();
            resetDetector();
            warmed = true;
        } catch (Throwable th) {
            offlineStreamCreateStream.release();
            resetDetector();
            throw th;
        }
    }

    static Session open(Context context) throws Exception {
        load(context);
        resetDetector();
        return new ZipformerSession(zipformer, detector, VoiceHotwords.fromCommands(VoiceCommands.load(context)));
    }

    private static void resetDetector() {
        detector.clear();
        detector.reset();
    }

    static void release() {
        Vad vad = detector;
        if (vad != null) {
            vad.release();
            detector = null;
        }
        OfflineRecognizer offlineRecognizer = zipformer;
        if (offlineRecognizer != null) {
            offlineRecognizer.release();
            zipformer = null;
        }
        warmed = false;
    }

    private static final class ZipformerSession implements Session {
        private int count;
        private final String hotwords;
        private final OfflineRecognizer recognizer;
        private final float[] recording = new float[160000];
        private boolean speech;
        private final Vad vad;

        ZipformerSession(OfflineRecognizer offlineRecognizer, Vad vad, String str) {
            this.recognizer = offlineRecognizer;
            this.vad = vad;
            this.hotwords = str;
        }

        @Override // ru.big.town.restoremode.VoiceModels.Session
        public boolean accept(float[] fArr) {
            int iMin = Math.min(fArr.length, this.recording.length - this.count);
            System.arraycopy(fArr, 0, this.recording, this.count, iMin);
            this.count += iMin;
            this.vad.acceptWaveform(fArr);
            this.speech |= this.vad.isSpeechDetected() || !this.vad.empty();
            return !this.vad.empty() || this.count == this.recording.length;
        }

        @Override // ru.big.town.restoremode.VoiceModels.Session
        public String partial() {
            return "";
        }

        @Override // ru.big.town.restoremode.VoiceModels.Session
        public String finish() {
            this.vad.flush();
            if ((!this.speech && this.vad.empty()) || this.count == 0) {
                return "";
            }
            OfflineStream offlineStreamCreateStream = this.recognizer.createStream(this.hotwords);
            try {
                offlineStreamCreateStream.acceptWaveform(Arrays.copyOf(this.recording, this.count), 16000);
                this.recognizer.decode(offlineStreamCreateStream);
                return this.recognizer.getResult(offlineStreamCreateStream).getText().trim();
            } finally {
                offlineStreamCreateStream.release();
            }
        }

        @Override // ru.big.town.restoremode.VoiceModels.Session, java.lang.AutoCloseable
        public void close() {
            this.vad.clear();
            this.vad.reset();
        }
    }

    private static File unpack(Context context, String str) throws IOException {
        File file = new File(context.getNoBackupFilesDir(), str);
        File file2 = new File(file, ".ready");
        if (!file2.isFile()) {
            copyAssets(context, str, file);
            if (!file2.createNewFile()) {
                throw new IOException("Cannot mark model ready");
            }
        }
        return file;
    }

    /* JADX WARN: Code duplicated, block: B:46:0x007b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    private static void copyAssets(Context context, String str, File file) throws IOException {
        String[] list = context.getAssets().list(str);
        if (list != null && list.length > 0) {
            if (!file.isDirectory() && !file.mkdirs()) {
                throw new IOException("Cannot create model directory");
            }
            for (String str2 : list) {
                copyAssets(context, str + "/" + str2, new File(file, str2));
            }
            return;
        }
        InputStream inputStreamOpen = context.getAssets().open(str);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            try {
                byte[] bArr = new byte[65536];
                while (true) {
                    int i = inputStreamOpen.read(bArr);
                    if (i < 0) {
                        break;
                    } else {
                        fileOutputStream.write(bArr, 0, i);
                    }
                    if (inputStreamOpen != null) {
                        try {
                            inputStreamOpen.close();
                        } catch (Throwable th) {
                            th.addSuppressed(th);
                        }
                    }
                    throw th;
                }
                fileOutputStream.close();
                if (inputStreamOpen != null) {
                    inputStreamOpen.close();
                }
            } catch (Throwable th2) {
                try {
                    fileOutputStream.close();
                } catch (Throwable th3) {
                    th2.addSuppressed(th3);
                }
                throw th2;
            }
        } catch (Throwable th4) {
            if (inputStreamOpen != null) {
                inputStreamOpen.close();
            }
            throw th4;
        }
    }
}
