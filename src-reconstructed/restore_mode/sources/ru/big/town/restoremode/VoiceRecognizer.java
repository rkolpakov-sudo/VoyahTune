package ru.big.town.restoremode;

import android.content.Context;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes2.dex */
final class VoiceRecognizer {
    private long generation;
    private static final ExecutorService WORKER = Executors.newSingleThreadExecutor();
    private static final VoiceSessionControl SESSIONS = new VoiceSessionControl();
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    private static final VoiceEngineCache<VoiceEngine> ENGINES = new VoiceEngineCache<>();
    private static final AtomicLong RESIDENCY = new AtomicLong();

    VoiceRecognizer() {
    }

    static void keepWarm(Context context, final Consumer<Boolean> consumer) {
        final Context applicationContext = context.getApplicationContext();
        final long jIncrementAndGet = RESIDENCY.incrementAndGet();
        ENGINES.retain(true);
        WORKER.execute(new Runnable() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                VoiceRecognizer.lambda$keepWarm$1(jIncrementAndGet, applicationContext, consumer);
            }
        });
    }

    static /* synthetic */ void lambda$keepWarm$1(final long j, Context context, final Consumer consumer) {
        if (RESIDENCY.get() == j && ENGINES.retained()) {
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            final boolean z = false;
            try {
                prepareEngine(context, VoiceAudioConfig.read(context.getSharedPreferences("DrivePreferences", 0)));
                z = true;
                Log.i("VoyahVoice", "Warmup ready in " + (SystemClock.elapsedRealtime() - jElapsedRealtime) + " ms; microphone closed");
            } catch (Exception | LinkageError e) {
                ENGINES.release();
                Log.e("VoyahVoice", "Warmup failed", e);
            } finally {
                ENGINES.releaseIfUnretained();
            }
            MAIN.post(new Runnable() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda17
                @Override // java.lang.Runnable
                public final void run() {
                    VoiceRecognizer.lambda$keepWarm$0(j, consumer, z);
                }
            });
        }
    }

    static /* synthetic */ void lambda$keepWarm$0(long j, Consumer consumer, boolean z) {
        if (RESIDENCY.get() == j && ENGINES.retained()) {
            consumer.accept(Boolean.valueOf(z));
        }
    }

    static void stopKeepingWarm() {
        RESIDENCY.incrementAndGet();
        final VoiceEngineCache<VoiceEngine> voiceEngineCache = ENGINES;
        voiceEngineCache.retain(false);
        SESSIONS.cancelAll();
        ExecutorService executorService = WORKER;
        Objects.requireNonNull(voiceEngineCache);
        executorService.execute(new Runnable() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                voiceEngineCache.releaseIfUnretained();
            }
        });
    }

    static /* synthetic */ VoiceEngine lambda$prepareEngine$2(Context context, VoiceAudioConfig voiceAudioConfig) throws Exception {
        return new VoiceEngine(context, voiceAudioConfig.deepFilterDb);
    }

    private static VoiceEngine prepareEngine(final Context context, final VoiceAudioConfig voiceAudioConfig) throws Exception {
        VoiceEngine voiceEngine = (VoiceEngine) ENGINES.acquire(new VoiceEngineCache.Factory() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda0
            @Override // ru.big.town.restoremode.VoiceEngineCache.Factory
            public final Object create() {
                return VoiceRecognizer.lambda$prepareEngine$2(context, voiceAudioConfig);
            }
        });
        voiceEngine.prepareFilter(voiceAudioConfig.deepFilterDb);
        return voiceEngine;
    }

    interface Listener {
        void audio(float f, String str);

        default void details(String str) {
        }

        void error(String str);

        void listening();

        default void processing() {
        }

        void result(String str);

        default void ready(Runnable runnable) {
            runnable.run();
        }

        default void recording(File file) {
            file.delete();
        }
    }

    void cancel() {
        if (SESSIONS.cancel(this.generation)) {
            final long j = this.generation + 1;
            WORKER.execute(new Runnable() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    VoiceRecognizer.lambda$cancel$3(j);
                }
            });
        }
    }

    static /* synthetic */ void lambda$cancel$3(long j) {
        if (SESSIONS.active(j)) {
            ENGINES.releaseIfUnretained();
        }
    }

    void start(Context context, final Listener listener) {
        final long jBegin = SESSIONS.begin();
        this.generation = jBegin;
        final Context applicationContext = context.getApplicationContext();
        final VoiceAudioConfig voiceAudioConfig = VoiceAudioConfig.read(applicationContext.getSharedPreferences("DrivePreferences", 0));
        WORKER.execute(new Runnable() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                VoiceRecognizer.lambda$start$8(jBegin, applicationContext, voiceAudioConfig, listener);
            }
        });
    }

    /* JADX WARN: Code duplicated, block: B:29:0x0058  */
    /* JADX WARN: Code duplicated, block: B:39:? A[SYNTHETIC] */
    static /* synthetic */ void lambda$start$8(long j, final Context context, final VoiceAudioConfig voiceAudioConfig, Listener listener) throws Throwable {
        final long j2;
        final Listener listener2;
        Throwable th;
        VoiceSessionControl voiceSessionControl = SESSIONS;
        if (!voiceSessionControl.active(j)) {
            return;
        }
        try {
            prepareEngine(context, voiceAudioConfig);
            j2 = j;
            listener2 = listener;
            try {
                try {
                    post(j2, new Runnable() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda13
                        @Override // java.lang.Runnable
                        public final void run() {
                            VoiceRecognizer.Listener listener3 = listener2;
                            listener3.ready(new Runnable() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda11
                                @Override // java.lang.Runnable
                                public final void run() {
                                    VoiceRecognizer.WORKER.execute(new Runnable() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda9
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            VoiceRecognizer.recognize(context, j, listener, voiceAudioConfig);
                                        }
                                    });
                                }
                            });
                        }
                    });
                    if (voiceSessionControl.active(j2)) {
                        return;
                    }
                    ENGINES.releaseIfUnretained();
                } catch (Exception | LinkageError e) {
                    e = e;
                    Throwable th2 = e;
                    VoiceEngineCache<VoiceEngine> voiceEngineCache = ENGINES;
                    voiceEngineCache.release();
                    Log.e("VoyahVoice", "Voice preparation failed", th2);
                    post(j2, new Runnable() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda14
                        @Override // java.lang.Runnable
                        public final void run() {
                            listener2.error("Не удалось подготовить помощника. Повторите попытку.");
                        }
                    });
                    if (SESSIONS.active(j2)) {
                        return;
                    }
                    voiceEngineCache.releaseIfUnretained();
                }
            } catch (Throwable th3) {
                th = th3;
                th = th;
                if (!SESSIONS.active(j2)) {
                    ENGINES.releaseIfUnretained();
                    throw th;
                }
                throw th;
            }
        } catch (Exception | LinkageError e2) {
            e = e2;
            j2 = j;
            listener2 = listener;
        } catch (Throwable th4) {
            th = th4;
            j2 = j;
            th = th;
            if (!SESSIONS.active(j2)) {
                ENGINES.releaseIfUnretained();
                throw th;
            }
            throw th;
        }
    }

    static /* synthetic */ void lambda$post$9(long j, Runnable runnable) {
        if (SESSIONS.active(j)) {
            runnable.run();
        }
    }

    private static void post(final long j, final Runnable runnable) {
        MAIN.post(new Runnable() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                VoiceRecognizer.lambda$post$9(j, runnable);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:134:0x02fe A[Catch: all -> 0x02f4, TRY_ENTER, TryCatch #15 {all -> 0x02f4, blocks: (B:3:0x001c, B:7:0x002d, B:11:0x0059, B:17:0x0072, B:70:0x01f3, B:134:0x02fe, B:135:0x0301, B:139:0x0345, B:87:0x0295, B:125:0x02ef, B:124:0x02ec), top: B:154:0x001c }] */
    /* JADX WARN: Code duplicated, block: B:137:0x0340  */
    /* JADX WARN: Code duplicated, block: B:138:0x0343  */
    /* JADX WARN: Code duplicated, block: B:155:0x02e7 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:188:? A[Catch: Exception | LinkageError -> 0x02f0, all -> 0x02f4, SYNTHETIC, TRY_LEAVE, TryCatch #1 {Exception | LinkageError -> 0x02f0, blocks: (B:125:0x02ef, B:124:0x02ec), top: B:146:0x02ec }] */
    /* JADX WARN: Unreachable blocks removed: 2, instructions: 4 */
    public static void recognize(final Context context, long j, final Listener listener, final VoiceAudioConfig voiceAudioConfig) {
        String str;
        File file;
        boolean z;
        String str2;
        String str3;
        Throwable th;
        File file2;
        int i;
        String str4;
        short[] sArr;
        String str5 = "Audio processing cannot keep up";
        VoiceRecording voiceRecording = new VoiceRecording();
        File file3 = new File(context.getCacheDir(), "voice-preview");
        String str6 = "подготовка модели";
        try {
            try {
                VoiceSessionControl voiceSessionControl = SESSIONS;
                if (!voiceSessionControl.active(j)) {
                    voiceSessionControl.finishCapture(j);
                    ENGINES.releaseIfUnretained();
                    return;
                }
                VoiceRecording.clear(file3);
                post(j, new Runnable() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda18
                    @Override // java.lang.Runnable
                    public final void run() {
                        listener.details(voiceAudioConfig.label());
                    }
                });
                VoiceEngineCache<VoiceEngine> voiceEngineCache = ENGINES;
                VoiceNeuralFilter voiceNeuralFilterPrepareFilter = ((VoiceEngine) voiceEngineCache.acquire(new VoiceEngineCache.Factory() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda19
                    @Override // ru.big.town.restoremode.VoiceEngineCache.Factory
                    public final Object create() {
                        return VoiceRecognizer.lambda$recognize$11(context, voiceAudioConfig);
                    }
                })).prepareFilter(voiceAudioConfig.deepFilterDb);
                VoiceModels.Session sessionOpen = VoiceModels.open(context);
                try {
                    if (!voiceSessionControl.active(j)) {
                        if (sessionOpen != null) {
                            sessionOpen.close();
                        }
                        voiceSessionControl.finishCapture(j);
                        voiceEngineCache.releaseIfUnretained();
                        return;
                    }
                    str6 = "микрофон 48 кГц";
                    if (!voiceSessionControl.open(j, new VoiceSessionControl.Factory() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda1
                        @Override // ru.big.town.restoremode.VoiceSessionControl.Factory
                        public final VoiceSessionControl.Capture open() {
                            return new VoiceMicrophone();
                        }
                    })) {
                        if (sessionOpen != null) {
                            sessionOpen.close();
                        }
                        voiceSessionControl.finishCapture(j);
                        voiceEngineCache.releaseIfUnretained();
                        return;
                    }
                    String strLabel = voiceAudioConfig.label();
                    Objects.requireNonNull(listener);
                    post(j, new Runnable() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            listener.listening();
                        }
                    });
                    str3 = "обработка звука";
                    try {
                        short[] sArr2 = new short[480];
                        float[] fArr = new float[480];
                        float[] fArr2 = new float[480];
                        float[] fArr3 = new float[160];
                        VoiceDecimator voiceDecimator = new VoiceDecimator();
                        long jElapsedRealtime = SystemClock.elapsedRealtime();
                        long jNanoTime = 0;
                        int i2 = 480;
                        float[] fArr4 = fArr;
                        long j2 = 0;
                        int i3 = 0;
                        int i4 = 0;
                        while (true) {
                            try {
                                VoiceSessionControl voiceSessionControl2 = SESSIONS;
                                if (voiceSessionControl2.active(j) && i4 < 1000) {
                                    try {
                                        if (SystemClock.elapsedRealtime() - jElapsedRealtime < 10000) {
                                            int i5 = i2;
                                            file2 = file3;
                                            int i6 = i4;
                                            fArr4 = fArr4;
                                            strLabel = strLabel;
                                            fArr2 = fArr2;
                                            try {
                                                int i7 = voiceSessionControl2.read(j, sArr2, i3, 480 - i3);
                                                if (i7 < 0) {
                                                    throw new IOException("Audio read failed: " + i7);
                                                }
                                                if (i7 == 0) {
                                                    Thread.sleep(5L);
                                                } else {
                                                    i3 += i7;
                                                    if (i3 >= i5) {
                                                        for (int i8 = 0; i8 < i5; i8++) {
                                                            fArr4[i8] = sArr2[i8] / 32768.0f;
                                                        }
                                                        long jNanoTime2 = System.nanoTime();
                                                        voiceNeuralFilterPrepareFilter.process(fArr4, fArr2);
                                                        voiceDecimator.process(fArr2, fArr3);
                                                        jNanoTime += System.nanoTime() - jNanoTime2;
                                                        int i9 = i6 + 1;
                                                        voiceRecording.append(fArr3);
                                                        boolean zAccept = sessionOpen.accept(fArr3);
                                                        long jElapsedRealtime2 = SystemClock.elapsedRealtime();
                                                        VoiceDecimator voiceDecimator2 = voiceDecimator;
                                                        if ((jElapsedRealtime2 - jElapsedRealtime) - (((long) i9) * 10) > 1500) {
                                                            throw new IOException("Audio processing cannot keep up");
                                                        }
                                                        if (zAccept) {
                                                            i = i9;
                                                            break;
                                                        }
                                                        if (jElapsedRealtime2 >= j2) {
                                                            double d = 0.0d;
                                                            int i10 = 0;
                                                            while (i10 < 160) {
                                                                float f = fArr3[i10];
                                                                d += (double) (f * f);
                                                                i10++;
                                                                sArr2 = sArr2;
                                                            }
                                                            sArr = sArr2;
                                                            final float fMax = Math.max(0.0f, Math.min(1.0f, (float) (((Math.log10(Math.max((float) Math.sqrt(d / ((double) 160)), 1.0E-5d)) * 20.0d) + 55.0d) / 40.0d)));
                                                            final String strPartial = sessionOpen.partial();
                                                            post(j, new Runnable() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda3
                                                                @Override // java.lang.Runnable
                                                                public final void run() {
                                                                    listener.audio(fMax, strPartial);
                                                                }
                                                            });
                                                            j2 = jElapsedRealtime2 + 60;
                                                        } else {
                                                            sArr = sArr2;
                                                        }
                                                        voiceDecimator = voiceDecimator2;
                                                        file3 = file2;
                                                        sArr2 = sArr;
                                                        i3 = 0;
                                                        i2 = 480;
                                                        i4 = i9;
                                                    }
                                                }
                                                i2 = i5;
                                                file3 = file2;
                                                i4 = i6;
                                            } catch (Throwable th2) {
                                                th = th2;
                                                th = th;
                                                str5 = "Audio processing cannot keep up";
                                                str = "VoyahVoice";
                                                file = file2;
                                                z = false;
                                            }
                                        }
                                    } catch (Throwable th3) {
                                        th = th3;
                                        file2 = file3;
                                    }
                                }
                                file2 = file3;
                                strLabel = strLabel;
                                i = i4;
                                break;
                            } catch (Throwable th4) {
                                th = th4;
                                file = file3;
                            }
                        }
                        long j3 = jNanoTime;
                        try {
                            VoiceSessionControl voiceSessionControl3 = SESSIONS;
                            voiceSessionControl3.finishCapture(j);
                            if (!voiceSessionControl3.active(j)) {
                                if (sessionOpen != null) {
                                    try {
                                        sessionOpen.close();
                                    } catch (Exception | LinkageError e) {
                                        e = e;
                                        str6 = "обработка звука";
                                        str = "VoyahVoice";
                                        file = file2;
                                        z = false;
                                        if (!z) {
                                            publishRecording(file, j, listener, voiceRecording);
                                        }
                                        Log.e(str, "Recognition failed: " + voiceAudioConfig.label() + " / " + str6, e);
                                        StringBuilder sbAppend = new StringBuilder().append("Ошибка: ").append(str6);
                                        if (str5.equals(e.getMessage())) {
                                            str2 = ". Обработка звука не успевает за записью. Попробуйте установить шумоподавление на 0 дБ.";
                                        } else {
                                            str2 = ". Проверьте микрофон и повторите попытку.";
                                        }
                                        final String string = sbAppend.append(str2).toString();
                                        post(j, new Runnable() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda6
                                            @Override // java.lang.Runnable
                                            public final void run() {
                                                listener.error(string);
                                            }
                                        });
                                        SESSIONS.finishCapture(j);
                                        ENGINES.releaseIfUnretained();
                                        return;
                                    }
                                }
                                voiceSessionControl3.finishCapture(j);
                                ENGINES.releaseIfUnretained();
                                return;
                            }
                            file = file2;
                            try {
                                publishRecording(file, j, listener, voiceRecording);
                                try {
                                    Objects.requireNonNull(listener);
                                    post(j, new Runnable() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda4
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            listener.processing();
                                        }
                                    });
                                    long jElapsedRealtime3 = SystemClock.elapsedRealtime();
                                    final String strFinish = sessionOpen.finish();
                                    str4 = "распознавание фразы";
                                    try {
                                        str5 = "Audio processing cannot keep up";
                                        try {
                                            final String str7 = strLabel + String.format(Locale.ROOT, "\nАудио %.1f с · фильтр %.0f мс/с · финал %d мс · память приложения %d МБ", Float.valueOf(i / 100.0f), Double.valueOf(j3 / Math.max(1.0d, ((double) i) * 10000.0d)), Long.valueOf(SystemClock.elapsedRealtime() - jElapsedRealtime3), Long.valueOf(Debug.getPss() / 1024));
                                            str = "VoyahVoice";
                                            try {
                                                Log.i(str, str7.replace('\n', ' '));
                                                post(j, new Runnable() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda5
                                                    @Override // java.lang.Runnable
                                                    public final void run() {
                                                        VoiceRecognizer.lambda$recognize$13(listener, str7, strFinish);
                                                    }
                                                });
                                                if (sessionOpen != null) {
                                                    try {
                                                        sessionOpen.close();
                                                    } catch (Exception | LinkageError e2) {
                                                        e = e2;
                                                        str6 = str4;
                                                        z = true;
                                                        if (!z) {
                                                            publishRecording(file, j, listener, voiceRecording);
                                                        }
                                                        Log.e(str, "Recognition failed: " + voiceAudioConfig.label() + " / " + str6, e);
                                                        StringBuilder sbAppend2 = new StringBuilder().append("Ошибка: ").append(str6);
                                                        if (str5.equals(e.getMessage())) {
                                                            str2 = ". Обработка звука не успевает за записью. Попробуйте установить шумоподавление на 0 дБ.";
                                                        } else {
                                                            str2 = ". Проверьте микрофон и повторите попытку.";
                                                        }
                                                        final String string2 = sbAppend2.append(str2).toString();
                                                        post(j, new Runnable() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda6
                                                            @Override // java.lang.Runnable
                                                            public final void run() {
                                                                listener.error(string2);
                                                            }
                                                        });
                                                        SESSIONS.finishCapture(j);
                                                        ENGINES.releaseIfUnretained();
                                                        return;
                                                    }
                                                }
                                                voiceSessionControl3.finishCapture(j);
                                                ENGINES.releaseIfUnretained();
                                                return;
                                            } catch (Throwable th5) {
                                                th = th5;
                                                th = th;
                                                str3 = str4;
                                                z = true;
                                                if (sessionOpen != null) {
                                                    throw th;
                                                }
                                                try {
                                                    sessionOpen.close();
                                                    throw th;
                                                } catch (Throwable th6) {
                                                    try {
                                                        th.addSuppressed(th6);
                                                        throw th;
                                                    } catch (Exception | LinkageError e3) {
                                                        e = e3;
                                                        str6 = str3;
                                                        if (!z) {
                                                            publishRecording(file, j, listener, voiceRecording);
                                                        }
                                                        Log.e(str, "Recognition failed: " + voiceAudioConfig.label() + " / " + str6, e);
                                                        StringBuilder sbAppend3 = new StringBuilder().append("Ошибка: ").append(str6);
                                                        if (str5.equals(e.getMessage())) {
                                                            str2 = ". Обработка звука не успевает за записью. Попробуйте установить шумоподавление на 0 дБ.";
                                                        } else {
                                                            str2 = ". Проверьте микрофон и повторите попытку.";
                                                        }
                                                        final String string3 = sbAppend3.append(str2).toString();
                                                        post(j, new Runnable() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda6
                                                            @Override // java.lang.Runnable
                                                            public final void run() {
                                                                listener.error(string3);
                                                            }
                                                        });
                                                        SESSIONS.finishCapture(j);
                                                        ENGINES.releaseIfUnretained();
                                                        return;
                                                    }
                                                }
                                            }
                                        } catch (Throwable th7) {
                                            th = th7;
                                            str = "VoyahVoice";
                                            th = th;
                                            str3 = str4;
                                            z = true;
                                            if (sessionOpen != null) {
                                                throw th;
                                            }
                                            sessionOpen.close();
                                            throw th;
                                        }
                                    } catch (Throwable th8) {
                                        th = th8;
                                        str5 = "Audio processing cannot keep up";
                                        str = "VoyahVoice";
                                        th = th;
                                        str3 = str4;
                                        z = true;
                                        if (sessionOpen != null) {
                                            throw th;
                                        }
                                        sessionOpen.close();
                                        throw th;
                                    }
                                } catch (Throwable th9) {
                                    th = th9;
                                    str4 = "распознавание фразы";
                                }
                            } catch (Throwable th10) {
                                th = th10;
                                str = "VoyahVoice";
                                th = th;
                                z = false;
                            }
                        } catch (Throwable th11) {
                            th = th11;
                            str5 = "Audio processing cannot keep up";
                            str = "VoyahVoice";
                            file = file2;
                            th = th;
                            z = false;
                        }
                    } catch (Throwable th12) {
                        th = th12;
                        str5 = "Audio processing cannot keep up";
                        str = "VoyahVoice";
                        file = file3;
                    }
                } catch (Throwable th13) {
                    str5 = "Audio processing cannot keep up";
                    str = "VoyahVoice";
                    file = file3;
                    str3 = str6;
                    z = false;
                    th = th13;
                }
                if (sessionOpen != null) {
                    throw th;
                }
                sessionOpen.close();
                throw th;
            } catch (Exception | LinkageError e4) {
                e = e4;
                str = "VoyahVoice";
                file = file3;
            }
        } catch (Throwable th14) {
            SESSIONS.finishCapture(j);
            ENGINES.releaseIfUnretained();
            throw th14;
        }
    }

    static /* synthetic */ VoiceEngine lambda$recognize$11(Context context, VoiceAudioConfig voiceAudioConfig) throws Exception {
        return new VoiceEngine(context, voiceAudioConfig.deepFilterDb);
    }

    static /* synthetic */ void lambda$recognize$13(Listener listener, String str, String str2) {
        listener.details(str);
        listener.result(str2);
    }

    private static void publishRecording(File file, final long j, final Listener listener, VoiceRecording voiceRecording) {
        if (voiceRecording.empty() || !SESSIONS.active(j)) {
            return;
        }
        try {
            final File fileSave = voiceRecording.save(file);
            MAIN.post(new Runnable() { // from class: ru.big.town.restoremode.VoiceRecognizer$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    VoiceRecognizer.lambda$publishRecording$15(j, listener, fileSave);
                }
            });
        } catch (IOException e) {
            Log.w("VoyahVoice", "Cannot save temporary recording", e);
        }
    }

    static /* synthetic */ void lambda$publishRecording$15(long j, Listener listener, File file) {
        if (SESSIONS.active(j)) {
            listener.recording(file);
        } else {
            file.delete();
        }
    }
}
