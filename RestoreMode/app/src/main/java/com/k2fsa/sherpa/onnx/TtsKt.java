package com.k2fsa.sherpa.onnx;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import kotlinx.coroutines.scheduling.WorkQueueKt;

/* JADX INFO: compiled from: Tts.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\u001e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\n\u001aÁ\u0001\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u00032\u0006\u0010\u000b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\u00032\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u00102\b\b\u0002\u0010\u0012\u001a\u00020\u00032\b\b\u0002\u0010\u0013\u001a\u00020\u00032\b\b\u0002\u0010\u0014\u001a\u00020\u00032\b\b\u0002\u0010\u0015\u001a\u00020\u00032\b\b\u0002\u0010\u0016\u001a\u00020\u00032\b\b\u0002\u0010\u0017\u001a\u00020\u00032\b\b\u0002\u0010\u0018\u001a\u00020\u0003¢\u0006\u0002\u0010\u0019¨\u0006\u001a"}, d2 = {"getOfflineTtsConfig", "Lcom/k2fsa/sherpa/onnx/OfflineTtsConfig;", "modelDir", "", "modelName", "acousticModelName", "vocoder", "voices", "lexicon", "dataDir", "dictDir", "ruleFsts", "ruleFars", "numThreads", "", "isKitten", "", "isSupertonic", "durationPredictor", "textEncoder", "vectorEstimator", "supertonicVocoder", "ttsJson", "unicodeIndexer", "voiceStyle", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;ZZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/k2fsa/sherpa/onnx/OfflineTtsConfig;", "sherpa_onnx_release"}, k = 2, mv = {1, 7, 1}, xi = 48)
public final class TtsKt {
    /* JADX WARN: Code duplicated, block: B:38:0x0153  */
    /* JADX WARN: Code duplicated, block: B:41:0x0172  */
    /* JADX WARN: Code duplicated, block: B:42:0x01c8  */
    /* JADX WARN: Code duplicated, block: B:55:0x0276  */
    /* JADX WARN: Code duplicated, block: B:57:0x0293  */
    /* JADX WARN: Code duplicated, block: B:58:0x02df  */
    /* JADX WARN: Code duplicated, block: B:60:0x02f0  */
    /* JADX WARN: Code duplicated, block: B:61:0x03a7  */
    public static final OfflineTtsConfig getOfflineTtsConfig(String modelDir, String modelName, String acousticModelName, String vocoder, String voices, String lexicon, String dataDir, String dictDir, String ruleFsts, String ruleFars, Integer num, boolean z, boolean z2, String durationPredictor, String textEncoder, String vectorEstimator, String supertonicVocoder, String ttsJson, String unicodeIndexer, String voiceStyle) {
        int i;
        int iIntValue;
        String str;
        String str2;
        OfflineTtsVitsModelConfig offlineTtsVitsModelConfig;
        String str3;
        char c;
        OfflineTtsMatchaModelConfig offlineTtsMatchaModelConfig;
        OfflineTtsKokoroModelConfig offlineTtsKokoroModelConfig;
        OfflineTtsKittenModelConfig offlineTtsKittenModelConfig;
        OfflineTtsSupertonicModelConfig offlineTtsSupertonicModelConfig;
        Intrinsics.checkNotNullParameter(modelDir, "modelDir");
        Intrinsics.checkNotNullParameter(modelName, "modelName");
        Intrinsics.checkNotNullParameter(acousticModelName, "acousticModelName");
        Intrinsics.checkNotNullParameter(vocoder, "vocoder");
        Intrinsics.checkNotNullParameter(voices, "voices");
        Intrinsics.checkNotNullParameter(lexicon, "lexicon");
        Intrinsics.checkNotNullParameter(dataDir, "dataDir");
        Intrinsics.checkNotNullParameter(dictDir, "dictDir");
        Intrinsics.checkNotNullParameter(ruleFsts, "ruleFsts");
        Intrinsics.checkNotNullParameter(ruleFars, "ruleFars");
        Intrinsics.checkNotNullParameter(durationPredictor, "durationPredictor");
        Intrinsics.checkNotNullParameter(textEncoder, "textEncoder");
        Intrinsics.checkNotNullParameter(vectorEstimator, "vectorEstimator");
        Intrinsics.checkNotNullParameter(supertonicVocoder, "supertonicVocoder");
        Intrinsics.checkNotNullParameter(ttsJson, "ttsJson");
        Intrinsics.checkNotNullParameter(unicodeIndexer, "unicodeIndexer");
        Intrinsics.checkNotNullParameter(voiceStyle, "voiceStyle");
        if (num != null) {
            iIntValue = num.intValue();
        } else {
            if (voices.length() > 0) {
                i = 4;
            } else {
                i = 2;
            }
            if (z2 && modelName.length() == 0 && acousticModelName.length() == 0) {
                throw new IllegalArgumentException("Please specify a TTS model");
            }
            str = modelName;
            if (str.length() <= 0 && acousticModelName.length() > 0) {
                throw new IllegalArgumentException("Please specify either a VITS or a Matcha model, but not both");
            }
            str2 = acousticModelName;
            if (str2.length() <= 0 && vocoder.length() == 0) {
                throw new IllegalArgumentException("Please provide vocoder for Matcha TTS");
            }
            if (str.length() <= 0 && voices.length() == 0 && !z2) {
                offlineTtsVitsModelConfig = new OfflineTtsVitsModelConfig(modelDir + '/' + modelName, modelDir + '/' + lexicon, modelDir + "/tokens.txt", dataDir, null, 0.0f, 0.0f, 0.0f, 240, null);
            } else {
                offlineTtsVitsModelConfig = new OfflineTtsVitsModelConfig(null, null, null, null, null, 0.0f, 0.0f, 0.0f, 255, null);
            }
            if (str2.length() > 0) {
                str3 = "/tokens.txt";
                c = '/';
                offlineTtsMatchaModelConfig = new OfflineTtsMatchaModelConfig(modelDir + '/' + acousticModelName, vocoder, modelDir + '/' + lexicon, modelDir + "/tokens.txt", dataDir, null, 0.0f, 0.0f, 224, null);
            } else {
                str3 = "/tokens.txt";
                c = '/';
                offlineTtsMatchaModelConfig = new OfflineTtsMatchaModelConfig(null, null, null, null, null, null, 0.0f, 0.0f, 255, null);
            }
            if (voices.length() <= 0 && !z && !z2) {
                offlineTtsKokoroModelConfig = new OfflineTtsKokoroModelConfig(modelDir + c + modelName, modelDir + c + voices, modelDir + str3, dataDir, (Intrinsics.areEqual(lexicon, "") || StringsKt.contains((CharSequence) lexicon, (CharSequence) ",", false)) ? lexicon : modelDir + c + lexicon, null, null, 0.0f, 224, null);
            } else {
                offlineTtsKokoroModelConfig = new OfflineTtsKokoroModelConfig(null, null, null, null, null, null, null, 0.0f, 255, null);
            }
            if (z) {
                offlineTtsKittenModelConfig = new OfflineTtsKittenModelConfig(modelDir + c + modelName, modelDir + c + voices, modelDir + str3, dataDir, 0.0f, 16, null);
            } else {
                offlineTtsKittenModelConfig = new OfflineTtsKittenModelConfig(null, null, null, null, 0.0f, 31, null);
            }
            if (z2) {
                offlineTtsSupertonicModelConfig = new OfflineTtsSupertonicModelConfig(modelDir + c + durationPredictor, modelDir + c + textEncoder, modelDir + c + vectorEstimator, modelDir + c + supertonicVocoder, modelDir + c + ttsJson, modelDir + c + unicodeIndexer, modelDir + c + voiceStyle);
            } else {
                offlineTtsSupertonicModelConfig = new OfflineTtsSupertonicModelConfig(null, null, null, null, null, null, null, WorkQueueKt.MASK, null);
            }
            return new OfflineTtsConfig(new OfflineTtsModelConfig(offlineTtsVitsModelConfig, offlineTtsMatchaModelConfig, offlineTtsKokoroModelConfig, null, offlineTtsKittenModelConfig, null, offlineTtsSupertonicModelConfig, i, true, "cpu", 40, null), ruleFsts, ruleFars, 0, 0.0f, 24, null);
        }
        i = iIntValue;
            if (z2 && modelName.length() == 0 && acousticModelName.length() == 0) {
                throw new IllegalArgumentException("Please specify a TTS model");
            }
            str = modelName;
            if (str.length() <= 0 && acousticModelName.length() > 0) {
                throw new IllegalArgumentException("Please specify either a VITS or a Matcha model, but not both");
            }
            str2 = acousticModelName;
            if (str2.length() <= 0 && vocoder.length() == 0) {
                throw new IllegalArgumentException("Please provide vocoder for Matcha TTS");
            }
            if (str.length() <= 0 && voices.length() == 0 && !z2) {
                offlineTtsVitsModelConfig = new OfflineTtsVitsModelConfig(modelDir + '/' + modelName, modelDir + '/' + lexicon, modelDir + "/tokens.txt", dataDir, null, 0.0f, 0.0f, 0.0f, 240, null);
            } else {
                offlineTtsVitsModelConfig = new OfflineTtsVitsModelConfig(null, null, null, null, null, 0.0f, 0.0f, 0.0f, 255, null);
            }
            if (str2.length() > 0) {
                str3 = "/tokens.txt";
                c = '/';
                offlineTtsMatchaModelConfig = new OfflineTtsMatchaModelConfig(modelDir + '/' + acousticModelName, vocoder, modelDir + '/' + lexicon, modelDir + "/tokens.txt", dataDir, null, 0.0f, 0.0f, 224, null);
            } else {
                str3 = "/tokens.txt";
                c = '/';
                offlineTtsMatchaModelConfig = new OfflineTtsMatchaModelConfig(null, null, null, null, null, null, 0.0f, 0.0f, 255, null);
            }
            if (voices.length() <= 0 && !z && !z2) {
                offlineTtsKokoroModelConfig = new OfflineTtsKokoroModelConfig(modelDir + c + modelName, modelDir + c + voices, modelDir + str3, dataDir, (Intrinsics.areEqual(lexicon, "") || StringsKt.contains((CharSequence) lexicon, (CharSequence) ",", false)) ? lexicon : modelDir + c + lexicon, null, null, 0.0f, 224, null);
            } else {
                offlineTtsKokoroModelConfig = new OfflineTtsKokoroModelConfig(null, null, null, null, null, null, null, 0.0f, 255, null);
            }
            if (z) {
                offlineTtsKittenModelConfig = new OfflineTtsKittenModelConfig(modelDir + c + modelName, modelDir + c + voices, modelDir + str3, dataDir, 0.0f, 16, null);
            } else {
                offlineTtsKittenModelConfig = new OfflineTtsKittenModelConfig(null, null, null, null, 0.0f, 31, null);
            }
            if (z2) {
                offlineTtsSupertonicModelConfig = new OfflineTtsSupertonicModelConfig(modelDir + c + durationPredictor, modelDir + c + textEncoder, modelDir + c + vectorEstimator, modelDir + c + supertonicVocoder, modelDir + c + ttsJson, modelDir + c + unicodeIndexer, modelDir + c + voiceStyle);
            } else {
                offlineTtsSupertonicModelConfig = new OfflineTtsSupertonicModelConfig(null, null, null, null, null, null, null, WorkQueueKt.MASK, null);
            }
            return new OfflineTtsConfig(new OfflineTtsModelConfig(offlineTtsVitsModelConfig, offlineTtsMatchaModelConfig, offlineTtsKokoroModelConfig, null, offlineTtsKittenModelConfig, null, offlineTtsSupertonicModelConfig, i, true, "cpu", 40, null), ruleFsts, ruleFars, 0, 0.0f, 24, null);
    }
}
